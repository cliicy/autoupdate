package com.ca.arcserve.edge.app.base.webservice.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.LinuxNodeUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.action.UpdateMultiNodesParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.linuximaging.webservice.FlashServiceErrorCode;

public class UpdateNodeTaskRunner extends AbstractTaskRunner<Integer>{
	private static final Logger logger = Logger.getLogger(UpdateNodeTaskRunner.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private EdgeWebServiceImpl webService;
	protected NodeDetail node;
	protected GatewayEntity gateway;
	private UpdateMultiNodesParameter<Integer> updateParameter;
	
	public UpdateNodeTaskRunner(Integer entityKey,
			ActionTaskParameter<Integer> parameter, CountDownLatch doneSignal,
			ActionTaskManager<Integer> manager) {
		super(entityKey, parameter, doneSignal, manager);
		this.webService = manager.getWebService();
		this.updateParameter = (UpdateMultiNodesParameter<Integer>)parameter;
	}

	@Override
	protected void excute() {
		try {
			node = nodeService.getNodeDetailInformation((Integer)entityKey);
			gateway = gatewayService.getGatewayByHostId( node.getId() );
			
			if(node.isLinuxNode()){
				updateLinuxNode();
			}else if((node.getProtectionTypeBitmap() & ProtectionType.LINUX_D2D_SERVER.getValue()) == ProtectionType.LINUX_D2D_SERVER.getValue()){
				updateLinuxD2DServer();
			}else if(node.isVMwareMachine() || node.isHyperVMachine()){
				updateVmNode();
			}else {
				updateWindowsNode();
			}
		} catch (Exception e) {
			logger.error("[UpdateNodeTaskRunner] excute() failed.", e);
			String nodeName = node.getHostname();
			if(StringUtil.isEmptyOrNull(nodeName))
				nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", node.getVmName());
			long logId = NodeExceptionUtil.generateActivityLogByException(Module.UpdateMutipleNode,node,"updateNode_Log", e);
			addFailedEntities(entityKey,logId);
		}
	}
	
	private void updateVmNode() throws Exception{
		if (!node.isVmWindowsOS()){
			generateLog(Severity.Warning, node, 
					EdgeCMWebServiceMessages.getMessage("updateNodeSkipNonWindowsVM",  node.getVmName()));
			addSucceedEntities(entityKey);
			return;
		}
		//query vm host name
		String userName = updateParameter.getGlobalUsername();
		String password = updateParameter.getGlobalPassword();
		if (updateParameter.isUsingOrignalCredential()) {
			userName = node.getUsername() == null ? "" : node.getUsername();
			password = node.getPassword() == null ? "" : node.getPassword();
		}
		String vmHostName = node.getHostname();
		if(StringUtil.isEmptyOrNull(vmHostName)){
			vmHostName = nodeService.queryVMHostName(node.getId());
			node.setHostname(vmHostName);
		}
		if(StringUtil.isEmptyOrNull(vmHostName)){
			List<Integer> nodeIds = new ArrayList<Integer>();
			nodeIds.add(node.getId());
			nodeService.changeNodesCredentials(nodeIds, userName, password);
			//generate log, just update the vm node credential
			long logId = generateLog(Severity.Error, node, 
					EdgeCMWebServiceMessages.getResource("failedFindVMHostName", node.getVmName(), getGatewayHostName()));
			addFailedEntities(entityKey, logId);
		}else {
			//update windows node need default protocol and port to connect d2d web service
			if(node.getD2dProtocol()==Protocol.UnKnown.ordinal()
					|| node.getD2dPort().equalsIgnoreCase("0")){
				node.setD2dProtocol(Protocol.Http.ordinal());
				node.setD2dPort("8014");
			}
			updateWindowsNode();
		}
	}
	
	private void updateWindowsNode(){
		RemoteNodeInfo nodeInfo = new RemoteNodeInfo();
		NodeRegistrationInfo registreationNode = new NodeRegistrationInfo();
		String userName = updateParameter.getGlobalUsername();
		String password = updateParameter.getGlobalPassword();
		if (updateParameter.isUsingOrignalCredential()) {
			userName = node.getUsername() == null ? "" : node.getUsername();
			password = node.getPassword() == null ? "" : node.getPassword();
		}
		
		String nodeName = node.getHostname();
		if(StringUtil.isEmptyOrNull(nodeName))
			nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", node.getVmName());
		
		try {
			String protocol = (Protocol.Https.ordinal()==node.getD2dProtocol())?"https":"http";
			nodeInfo = nodeService.queryRemoteNodeInfo(gateway.getId(), node.getId(), node.getHostname(), 
					userName, password, protocol, Integer.parseInt(node.getD2dPort()));
			
			registreationNode.setNodeName(node.getHostname());
			registreationNode.setNodeDescription(node.getNodeDescription());
			registreationNode.setUsername(userName);
			registreationNode.setPassword(password);
			registreationNode.setNodeInfo(nodeInfo);
			registreationNode.setId(node.getId());
			registreationNode.setGatewayId(gateway.getId());
			
			if (nodeInfo.isD2DInstalled()) {
				registreationNode.setRegisterD2D((nodeInfo.isD2DInstalled()));
				registreationNode.setD2dPort(nodeInfo.getD2DPortNumber());
				registreationNode.setD2dProtocol(nodeInfo.getD2DProtocol());
			}
			if (nodeInfo.isConsoleInstalled()) {
				registreationNode.setConsoleInstalled(nodeInfo.isConsoleInstalled());
				registreationNode.setConsolePort(nodeInfo.getD2DPortNumber());
				registreationNode.setConsoleProtocol(nodeInfo.getD2DProtocol());
			}
			
			if (nodeInfo.isARCserveBackInstalled()) {
				registreationNode.setRegisterARCserveBackup(nodeInfo.isARCserveBackInstalled());
				if(node.getArcserveConnectInfo().getAuthmode() == ABFuncAuthMode.WINDOWS) {
					registreationNode.setAbAuthMode(ABFuncAuthMode.WINDOWS);
				} else {
					registreationNode.setAbAuthMode(ABFuncAuthMode.AR_CSERVE);
				}
				registreationNode.setCarootUsername(node.getArcserveConnectInfo().getCauser());
				registreationNode.setCarootPassword(node.getArcserveConnectInfo().getCapasswd());
				registreationNode.setArcservePort(node.getArcserveConnectInfo().getPort());
				registreationNode.setArcserveProtocol(node.getArcserveConnectInfo().getProtocol());
			} else {
				registreationNode.setCarootUsername(userName);
				registreationNode.setCarootPassword(password);
			}
			
			String[] error = nodeService.updateNode(false, registreationNode,false,false);
			
			if (error[0] != null) {
				if (EdgeServiceErrorCode.Node_D2D_Reg_Duplicate == error[0]) {	
					generateLog(Severity.Warning, node, EdgeCMWebServiceMessages.getMessage("failedToManageD2DByAnotherServe",  node.getHostname(), 
							registreationNode.getNodeInfo().getHostEdgeServer()));
					addWarnningEntities(entityKey, registreationNode.getNodeInfo().getHostEdgeServer());
					return;
				} else {
					String errorMsg = MessageReader.getErrorMessage(error[0]);
					if( errorMsg.equals( EdgeCMWebServiceMessages.getResource( "unknownError" ) ) ) {
						errorMsg = EdgeCMWebServiceMessages.getMessage("failedToManageD2D", node.getHostname() );
						logger.error(" unable to manage d2d node "+node.getHostname() +" , error code is " + error[0] );
					}
					long logId = generateLog(Severity.Warning, node,errorMsg);
					addFailedEntities(entityKey, logId);
					return;
				}
			}
			
			if (error[1] != null) {
				String errorMsg = "";
				if(EdgeServiceErrorCode.ABFunc_HaveManagedByAnotherServer == error[1]) {
					generateLog(Severity.Warning, node,EdgeCMWebServiceMessages.getMessage("failedToManageARCServerBackupByAnotherServe", node.getHostname(), registreationNode.getNodeInfo().getHostEdgeServer()));
					addWarnningEntities(entityKey, registreationNode.getNodeInfo().getHostEdgeServer());
					return;
				}else if(EdgeServiceErrorCode.Node_ASBU_ADD_CANNOT_CONNECT == error[1] || EdgeServiceErrorCode.Node_ASBU_ADD_MISMATCH_VERSION == error[1]){//have args
					errorMsg = MessageReader.getErrorMessage(error[1],node.getHostname());
				}else{
					errorMsg = MessageReader.getErrorMessage(error[1]);
				}
				if( errorMsg.equals( EdgeCMWebServiceMessages.getResource( "unknownError" ) ) ) {
					errorMsg = EdgeCMWebServiceMessages.getMessage("failedToManageARCServerBackup", node.getHostname() ) ;
					logger.error(" Unable to manage CA ARCserve Backup node " + node.getHostname() +" , error code is " + error[1] );
				}
				long logId = generateLog(Severity.Warning, node,errorMsg);
				addFailedEntities(entityKey, logId);
				return;
			}
			
			generateLog(Severity.Information, node, EdgeCMWebServiceMessages.getMessage("updateNodeSuccessful", node.getHostname()));
			addSucceedEntities(entityKey);
			
		} catch (EdgeServiceFault e) {
			logger.error("updateMultipleNodeByIds failed", e);
			long logId = 0;
			if (EdgeServiceErrorCode.Node_ASBU_ADD_CANNOT_CONNECT.equals(e.getFaultInfo().getCode())) {					
				logId = generateLog(Severity.Error, node,EdgeCMWebServiceMessages.getMessage("asbuCannotConnectWebservice", node.getHostname()));
			} else if (EdgeServiceErrorCode.ABFunc_UserNamePasswordError.equals(e.getFaultInfo().getCode())) {
				logId = generateLog(Severity.Error, node,EdgeCMWebServiceMessages.getMessage("asbuUsernamePasswordError", node.getHostname()));
			} else if (EdgeServiceErrorCode.ABFunc_NoPermission.equals(e.getFaultInfo().getCode())) {
				logId = generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getMessage("asbuNoPermission", node.getHostname()));
			} else if (EdgeServiceErrorCode.ABFunc_ConnectArcserveFailed.equals(e.getFaultInfo().getCode())) {
				logId = generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getMessage("asbuConnectFailed", node.getHostname()));
			} else if (EdgeServiceErrorCode.ABFunc_WCFConnectTimeout.equals(e.getFaultInfo().getCode())) {
				logId = generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getMessage("asbuConnectTimeout", node.getHostname()));
			} else if (EdgeServiceErrorCode.ABFunc_HaveManagedByAnotherServer.equals(e.getFaultInfo().getCode())) {
				logId = generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getMessage("asbuManagedByAnotherServe", node.getHostname()));
			} else{
				logId = NodeExceptionUtil.generateActivityLogByException(Module.UpdateNode,node, "updateNode_Log", e);
			}
			addFailedEntities(entityKey, logId);
		}catch (Exception e) {
			long logId = NodeExceptionUtil.generateActivityLogByException(Module.UpdateNode,node,"updateNode_Log", e);
			addFailedEntities(entityKey, logId);
		}
	}
	
	private void updateLinuxNode(){
		NodeRegistrationInfo registreationNode = new NodeRegistrationInfo();
		registreationNode.setId(node.getId());
		registreationNode.setGatewayId(gateway.getId());
		registreationNode.setNodeName(node.getHostname());
		registreationNode.setNodeDescription(node.getNodeDescription());
		UpdateMultiNodesParameter<Integer> updateParameter = (UpdateMultiNodesParameter<Integer>)parameter;
		if(updateParameter.isUsingOrignalCredential()){
			registreationNode.setUsername(node.getUsername());
			registreationNode.setPassword(node.getPassword());
		}else{
			registreationNode.setUsername(updateParameter.getGlobalUsername());
			registreationNode.setPassword(updateParameter.getGlobalPassword());
		}
		
		try {
			RegistrationNodeResult result = webService.registerLinuxNode(registreationNode, updateParameter.isForceManaged());
			if(result.getErrorCodes()[0]!=null){
				String message = LinuxNodeUtil.getLinuxMessage(result,node.getHostname());
				Severity severity = LinuxNodeUtil.getMessageType(result.getErrorCodes());
				if(severity == Severity.Error){
					long jobId = generateLog(severity, node, EdgeCMWebServiceMessages.getMessage("updateLinuxNodeFailed", node.getHostname(),message));
					addFailedEntities(entityKey, jobId);
				}else{
					generateLog(severity, node, EdgeCMWebServiceMessages.getMessage("updateNodeSuccessful", node.getHostname(),message));
					addSucceedEntities(entityKey);
				}
			}else{
				generateLog(Severity.Information, node, EdgeCMWebServiceMessages.getMessage("updateNodeSuccessful", node.getHostname()));
				addSucceedEntities(entityKey);
			}
		} catch (EdgeServiceFault e) {
			String message = "";
			String errorCode = e.getFaultInfo().getCode();
			IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
			List<EdgeConnectInfo> d2dServer = new ArrayList<EdgeConnectInfo>();
			connectionInfoDao.as_edge_linux_d2d_server_by_hostid(node.getId(),d2dServer);
			if(EdgeServiceErrorCode.Node_Linux_D2D_Server_Managed_By_Others.equals(errorCode)){
				message = EdgeCMWebServiceMessages.getMessage("updateLinuxNodeServerIsManagedByOther", node.getHostname(),d2dServer.get(0).getRhostname() );
				generateLog(Severity.Warning,node, message);
				addWarnningEntities(entityKey, d2dServer.get(0).getRhostname());
			}else if(EdgeServiceErrorCode.Node_Linux_No_Available_D2D_Server.equals(errorCode)){
				message = EdgeCMWebServiceMessages.getMessage("updateLinuxNodeNoAvailableServer", node.getHostname() );
				long logId = generateLog(Severity.Error,node,message);
				addFailedEntities(entityKey, logId);
			}else if (EdgeServiceErrorCode.Node_Linux_D2D_Server_Not_Reachable.equals(errorCode)) {
				message = EdgeCMWebServiceMessages.getMessage("updateLinuxNodeServerNotReachable", node.getHostname() ,d2dServer.get(0).getRhostname());
				long logId = generateLog(Severity.Error,node, message);
				addFailedEntities(entityKey, logId);
			}else {
				String nodeName = node.getHostname();
				if(StringUtil.isEmptyOrNull(nodeName))
					nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", node.getVmName());
				long logId = NodeExceptionUtil.generateActivityLogByException(Module.UpdateMutipleNode,node,"updateNode_Log", e);
				addFailedEntities(entityKey, logId);
			}
		}catch (Exception e) {
			long logId = NodeExceptionUtil.generateActivityLogByException(Module.UpdateNode,node,"updateNode_Log", e);
			addFailedEntities(entityKey, logId);
		}
	}
	
	private void updateLinuxD2DServer(){
		NodeRegistrationInfo registreationNode = new NodeRegistrationInfo();
		registreationNode.setId(node.getId());
		registreationNode.setGatewayId(gateway.getId());
		registreationNode.setNodeName(node.getHostname());
		registreationNode.setNodeDescription(node.getNodeDescription());
		UpdateMultiNodesParameter<Integer> updateParameter = (UpdateMultiNodesParameter<Integer>)parameter;
		if(updateParameter.isUsingOrignalCredential()){
			registreationNode.setUsername(node.getUsername());
			registreationNode.setPassword(node.getPassword());
		}else{
			registreationNode.setUsername(updateParameter.getGlobalUsername());
			registreationNode.setPassword(updateParameter.getGlobalPassword());
		}
		registreationNode.setD2dProtocol(Protocol.parse(node.getD2dProtocol()));
		registreationNode.setD2dPort(Integer.parseInt(node.getD2dPort()));
		
		try {
			
			webService.registerLinuxD2DServer(registreationNode, updateParameter.isForceManaged(),updateParameter.isForceManaged());
			generateLog(Severity.Information, node,EdgeCMWebServiceMessages.getMessage("updateLinuxBackupServerSuccessful", node.getHostname()));
			addSucceedEntities(entityKey);
		} catch (EdgeServiceFault e) {
			String message = "";
			String reason = "";
			String errorCode = e.getFaultInfo().getCode();
			if (EdgeServiceErrorCode.Node_Linux_D2D_Server_Not_Reachable.equals(errorCode)) {
				reason = EdgeCMWebServiceMessages.getMessage("linuxBackupServerNotReachable", node.getHostname());
				message = EdgeCMWebServiceMessages.getMessage("failedToUpdateLinuxBackupServer", node.getHostname(),reason );
				long logId = generateLog(Severity.Error,node,message);
				addFailedEntities(entityKey, logId);
			}else if(EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential.equals(errorCode)){
				reason = EdgeCMWebServiceMessages.getMessage("connectFailedWrongUserAccount", node.getHostname() );
				message = EdgeCMWebServiceMessages.getMessage("failedToUpdateLinuxBackupServer", node.getHostname(),reason );
				long logId = generateLog(Severity.Error,node,message);
				addFailedEntities(entityKey, logId);
			}else if(FlashServiceErrorCode.D2D_Server_Management_Managed_By_Others.equals(errorCode)){
				reason = EdgeCMWebServiceMessages.getMessage("linuxBackupServerIsManagedByOther", node.getHostname());
				message = EdgeCMWebServiceMessages.getMessage("failedToUpdateLinuxBackupServer", node.getHostname(),reason );
				generateLog(Severity.Warning,node,message);
				addWarnningEntities(entityKey, "");
			}
		}catch (Exception e) {
			long logId = NodeExceptionUtil.generateActivityLogByException(Module.UpdateNode,node,"updateNode_Log", e);
			addFailedEntities(entityKey, logId);
		}
	}
	
	private long generateLog(Severity severity, Node node, String message) {
		String logMsg = EdgeCMWebServiceMessages.getMessage("updateNode_Log", message);
		ActivityLog log = new ActivityLog();
		log.setNodeName(node!=null?node.getHostname():"");
		log.setHostId(node.getId());
		log.setModule(Module.UpdateMutipleNode);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(logMsg);
		
		try {
			return logService.addLog(log);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 0;
	}
	
	public String getGatewayHostName(){
		return EdgeCommonUtil.getGatewayHostNameByNodeId(entityKey);
	}
}
