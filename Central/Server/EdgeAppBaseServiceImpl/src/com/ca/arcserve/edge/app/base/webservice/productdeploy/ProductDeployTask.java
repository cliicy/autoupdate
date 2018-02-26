package com.ca.arcserve.edge.app.base.webservice.productdeploy;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcflash.rps.webservice.replication.CAProxy;
import com.ca.arcflash.rps.webservice.replication.CAProxySelector;
import com.ca.arcflash.rps.webservice.replication.HttpProxy;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeProductDeployDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IProductDeployService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.ProductType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.RPSPolicyWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.DeployStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.ProductDeployUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.Task;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.gateway.IMessageServiceModule;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.DiscoveryService;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.taskmonitor.TaskMonitor;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsNodeDao;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;
import com.ca.arcserve.edge.app.rps.webservice.datastore.RPSDataStoreServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.node.RPSNodeServiceImpl;
import com.ca.arcserve.edge.webservice.jni.model.DeployD2DConstants;

public class ProductDeployTask extends Observable implements Runnable{
	private IProductDeployService deployService = ProductDeployServiceImpl.getInstance();
	private NodeServiceImpl nodeService = new NodeServiceImpl();
	private RPSNodeServiceImpl rpsNodeService = new RPSNodeServiceImpl();
	private IEdgePolicyDao edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
	private IEdgeHostMgrDao edgeHostMgrDao = DaoFactory.getDao( IEdgeHostMgrDao.class );
	private IRpsNodeDao rpsNodeDao = DaoFactory.getDao( IRpsNodeDao.class );
	private IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgeProductDeployDao targetDao = DaoFactory.getDao(IEdgeProductDeployDao.class);
	private DeployTargetDetail target;
	private int taskID;
	private String targetName;
	
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	//If true, and then not lunch deploy.exe, 
    //just only monitor the process active status and deploy status
	private boolean monitorOnly;
	
	private boolean selfIsRunning = false;
	
	//private final long REBOOT_TIMEOUT = 30 * 60 * 1000; // 30 minutes
	private final long CONNECT_SERVICE_TIMEOUT = 3 * 60 * 1000; // 3 minutes
	
	private static final Logger logger = Logger.getLogger( ProductDeployTask.class );
	
	public ProductDeployTask(DeployTargetDetail target, boolean monitorOnly) {
		this.monitorOnly = monitorOnly;
		this.target = target;
		this.targetName = target.getNodeID()+"_"+target.getServerName();
		
		this.selfIsRunning = selfIsRunning(target);
		if(selfIsRunning){
			//Write activity log
			writeSelfIsRunningActivityLog();
			//update plan status
			PlanStatusUpdater.updatePlanDeployingD2D(target);
		}else {
			//start a new task
			target.setStatus(DeployStatus.DEPLOY_PENDING_FOR_DEPLOY.value());
			target.setPercentage(0);
			target.setMsgCode(0);
			target.setTaskstatus( TaskStatus.Pending.getValue() );
			this.taskID = registNewTask();
			updateTarget(0);
			PlanStatusUpdater.updatePlanDeployingD2D(target);
			registerObservers();
		}
	}
	
	private int registNewTask(){
		TaskDetail<DeployTargetDetail> taskDetail = new TaskDetail<DeployTargetDetail>();
		String[] finalMessage = ProductDeployUtil.getFinalMessage(target.getProductType(), TaskStatus.Pending, DeployStatus.DEPLOY_PENDING_FOR_DEPLOY, 
				target.getProgressMessage(), target.getWarningMessage(), 0, getGateWayHostName());
		target.setStatus(DeployStatus.DEPLOY_PENDING_FOR_DEPLOY.value());
		target.setTaskstatus(TaskStatus.Pending.getValue());
		target.setFinalTitleMessage(finalMessage[0]);
		target.setFinalDetailMessage(finalMessage[1]);
		taskDetail.setCode(target.getStatus());
		taskDetail.setMessage(target.getProgressMessage());
		taskDetail.setRawData(target); 
		return TaskMonitor.registerNewTask(Module.RemoteDeploy, targetName, taskDetail);
	}

	@Override
	public void run() {
		try {
			if(selfIsRunning){
				logger.info("[ProductDeployTask]: deploy target "+targetName+" is running, so just print log and skip.");
				return;
			}
			
			logger.info("[ProductDeployTask]: Begin deploy the target "+targetName);
			TaskMonitor.setTaskStarted(taskID);
			
			if(!doPreProcess(target)){
				return;
			}
			
			if(!monitorOnly){
				deployService.startDeployProcess(target);
			}
			
			//Set agent deploy is running flag
			targetDao.as_edge_deploy_target_set_selected(1,target.getNodeID());
			target.setSelected(true);
			updateTarget(TaskStatus.InProcess, DeployStatus.valueOf(target.getStatus()),target.getProgressMessage(),target.getWarningMessage());
		
			while(true){
				
				DeployStatusInfo info = getDeployStatus();
				if(info.getDeployStatus() != DeployStatus.DEPLOY_NA)
					updateTarget(TaskStatus.InProcess, info.getDeployStatus(),info.getPrograssMessage(),info.getWarnningMessage());
				
				if (DeployStatus.canWaitDeployProcess( info.getDeployStatus() ))
				{
					int retCode = deployService.getDeployProcessExitValue(target);
					if(retCode != DeployD2DConstants.DeployProcessIsRunning){
						logger.info("deploy process complete for " +targetName+". the return code is: "+ retCode);
						break;
					}
				}
				else if (DeployStatus.isFailed( info.getDeployStatus().value() ))
				{
					break;
				}
				
				Thread.sleep(3*1000);// sleep 3 seconds to sync result
			}
			
			DeployStatusInfo info = getDeployStatus();
			if(info.getDeployStatus() != DeployStatus.DEPLOY_NA)
				updateTarget(TaskStatus.InProcess, info.getDeployStatus(),info.getPrograssMessage(),info.getWarnningMessage());
			onDeployFinished();
			logger.info("[ProductDeployTask]: End deploy the target "+targetName);
		
		}catch(Throwable e) {
		
			logger.error("[ProductDeployTask]: Have errors when product deploy thread running.",e);
			
			IMessageServiceModule msgSvcModule = EdgeFactory.getBean( IMessageServiceModule.class );
			String message = msgSvcModule.getLocalizedMessageOfException( e );
			if (message != null)
			{
				updateTarget(TaskStatus.Error, DeployStatus.DEPLOY_FAILED, message, "" );
			}
			else
			{
				updateTarget(TaskStatus.Error, DeployStatus.DEPLOY_FAILED);
			}
		}
	}
	
	private DeployStatusInfo getDeployStatus() throws Exception
	{
		int totalTries = 5;
		int tried = 0;
		
		for (;;) // if cannot communicate with gateway, retry
		{
			try
			{
				return deployService.getDeployStatus(target);
			}
			catch (Exception e)
			{
				IMessageServiceModule msgSvcModule = EdgeFactory.getBean( IMessageServiceModule.class );
				if (!msgSvcModule.isConnectionTimeoutException( e ))
					throw e;
				
				tried ++;
				
				if (tried >= totalTries)
					throw e;
			}
		}
	}
	
	private void onDeployFinished()throws EdgeServiceFault{
		targetDao.as_edge_deploy_target_set_selected(0, target.getNodeID());
		//deploy.exe have exit, deploy should be finished, if it is inprogress, we think that the deploy.exe crashed.
		updateAgentUUIDFromFile();
		if(DeployStatus.isInProgress(target.getStatus())){
			String product = "";
			if(Integer.parseInt(ProductType.ProductRPS) == target.getProductType()){
				product = EdgeCMWebServiceMessages.getResource("productShortNameRPS");
			}else {
				product = EdgeCMWebServiceMessages.getResource("productShortNameD2D");
			}
			target.setStatus(DeployStatus.DEPLOY_FAILED.value());
			target.setProgressMessage(EdgeCMWebServiceMessages.getMessage("deployFailedWithProcessCrash",product,target.getServerName()));
			deployFinishedWithError();
			return;
		}else if(DeployStatus.isFailed(target.getStatus())){
			deployFinishedWithError();
			return;
		}else { //success
			boolean requiredReboot = (target.getStatus()==DeployStatus.DEPLOY_SUCCESS_NEEDREBOOT.value())?true:false;
			//handle local host machine, we not reboot local host machine what ever user set and whether need reboot or not
			if(nodeService.isLocalHost(target.getServerName())){
				if(requiredReboot){
					deployFinishedNeedReboot();
				}else {
					updateTarget(TaskStatus.InProcess, DeployStatus.DEPLOY_DIRECT_CONNECT_WS);
					boolean connectSucceed = connctTarget(CONNECT_SERVICE_TIMEOUT);
					if(connectSucceed){
						deployFinishedWithOk();
					}else {
						deployFinishedWithTimeOut(CONNECT_SERVICE_TIMEOUT,DeployStatus.DEPLOY_CONNECT_WS_TIMEOUT);
					}
				}
				return;
			}
			//handle other machine
			if (!requiredReboot)
			{
				updateTarget(TaskStatus.InProcess, DeployStatus.DEPLOY_DIRECT_CONNECT_WS);
				boolean connectSucceed = connctTarget(CONNECT_SERVICE_TIMEOUT);
				if(connectSucceed){
					deployFinishedWithOk();
				}else {
					deployFinishedWithTimeOut(CONNECT_SERVICE_TIMEOUT,DeployStatus.DEPLOY_CONNECT_WS_TIMEOUT);
				}
			}
			else {
				//In tungsten, agent or rps deploy no needed reboot, but sometimes, because file lock or other problems,
				//setup need reboot, then need make use know that: the mechine need reboot.
				deployFinishedNeedReboot();
//				updateTarget(TaskStatus.InProcess, DeployStatus.DEPLOY_REBOOTING);
//				boolean connectSucceed = connctTarget(REBOOT_TIMEOUT);
//				if(connectSucceed){
//					deployFinishedWithOk();
//				}else {
//					deployFinishedWithTimeOut(REBOOT_TIMEOUT,DeployStatus.DEPLOY_COMPLETE_REBOOTTIMEOUT);
//				}
			}
		}
		//deploy finished, need notify all the observers
		try {
			this.setChanged();
			this.notifyObservers(target);
		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() +": Notify observers failed.",e);
		}
	}
	
	private void updateAgentUUIDFromFile() {
		try {
			String uuid = deployService.getTargetUUID(target);
			if (!StringUtil.isEmptyOrNull(uuid)) {					
				// udpate uuid into db
				connectionInfoDao.as_edge_connect_info_update_uuid(target.getNodeID(), uuid);
			}
		} catch (Exception e) {
			logger.error("[ProductDeployTask]: updateAgentUUIDFromFile() failed for target: " + targetName, e);
		}
	}

	private boolean connctTarget(long timeout){
		logger.info("[ProductDeployTask]: Start connect agent webservice, the time out is:" +timeout+"mill seconds");
		long startTime = System.currentTimeMillis();
		while(true){
			if(connectTarget()){
				return true;
			}else{
				if(System.currentTimeMillis() - startTime > timeout){
					logger.info("[ProductDeployTask]: Conect target: "+targetName+" web service timeout.");
					return false;
				}
				long sleep = 10 * 1000 ;
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					// ignore 
				}
			}
		}
	}
	
	private boolean connectTarget(){
		try {
			GatewayEntity gateway = gatewayService.getGatewayByHostId(target.getNodeID());
			RemoteNodeInfo nodeInfo = nodeService.queryRemoteNodeInfo(gateway.getId(), target.getNodeID(),target.getServerName(),
					 target.getUsername(), target.getPassword(),target.getProtocol()==Protocol.Http?"http":"https", target.getPort());
			if(nodeInfo != null && nodeInfo.isD2DInstalled()){
				target.setProtocol(nodeInfo.getD2DProtocol());
				target.setPort(nodeInfo.getD2DPortNumber());
			}
			String protocol = target.getProtocol()==Protocol.Http?"http":"https";
			//connect webservice
			ConnectionContext context = createConnectionContext(protocol,target.getServerName(), 
					target.getPort(),target.getUsername(), target.getPassword(),target.getNodeID());
			D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context));
			connection.connect();
			return true;
		}
		catch( Exception e ) {
			return false;
		}
	}
	
	private void updateNode(){
		NodeDetail nodeDetail = null;
		RemoteNodeInfo nodeInfo = null;
		try {
			nodeDetail = nodeService.getNodeDetailInformation(target.getNodeID());
			GatewayEntity gateway = gatewayService.getGatewayByHostId(target.getNodeID());
			nodeInfo = nodeService.queryRemoteNodeInfo(gateway.getId(), target.getNodeID(),target.getServerName(),
					 target.getUsername(), target.getPassword(),target.getProtocol()==Protocol.Https?"https":"http", target.getPort());
		} catch (Exception e) {
			logger.error("[ProductDeployTask]: get node detail information failed. the target is:"+targetName);
		}
		
		if(nodeInfo == null){
			logger.error("[ProductDeployTask]: Update node failed, node info is null. the target is:"+targetName);
			return;
		}	
		
		NodeRegistrationInfo registreationNode = new NodeRegistrationInfo();
		registreationNode.setNodeInfo(nodeInfo);
		registreationNode.setId(target.getNodeID());
		registreationNode.setNodeName(target.getServerName());
		registreationNode.setUsername(target.getUsername());
		registreationNode.setPassword(target.getPassword());
		registreationNode.setRegisterD2D(true);
		registreationNode.setD2dPort( target.getPort() );
		registreationNode.setD2dProtocol( target.getProtocol());
		if(nodeDetail != null){
			registreationNode.setNodeDescription(nodeDetail.getNodeDescription());						
		}	
		
		String[] errorCodes = null;
		try {
			if(target.getProductType() == Integer.valueOf(ProductType.ProductRPS)){
				errorCodes = rpsNodeService.updateRpsNodeNoActivityLog(false, registreationNode, true);
			}else {
				errorCodes = nodeService.updateNode(false,registreationNode,true,false,true,getLastPlanDeployReason(target.getNodeID()),true);
			}
		} catch (Exception e) {
			logger.error("[ProductDeployTask]Update node have exception, exception is: ",e);
			updateVersion();
		}
		
		//Defect 761936, if install agent then update rps connect info, if install rps then update agent connect info
		//updateSameNameNodesConnectInfos(target.getServerName(), target.getNodeID());
		//Notification will do this, so don't do it again here
		
		try {
			if(errorCodes!=null && EdgeServiceErrorCode.Node_D2D_Reg_Duplicate.equals(errorCodes[0])){
				// when managed by other server , force manage it.
				if(target.getProductType() == Integer.valueOf(ProductType.ProductRPS)){
					rpsNodeService.markRpsNodeAsManagedNoActivityLog(registreationNode, true);
				}else{
					nodeService.markNodeAsManagedNoActivityLog(registreationNode, true);
				}
			}else if (errorCodes!=null && (errorCodes[1]!=null || errorCodes[0]!=null) && !EdgeServiceErrorCode.Node_D2D_Reg_Again.equals(errorCodes[0])) {
				logger.error("[ProductDeployTask]Update node failed, error code0 is: "+errorCodes[0] +"error code1 is:"+errorCodes[1]);
			}
		} catch (Exception e) {
			logger.error("[ProductDeployTask]Mark node have exception, exception is: ",e);
		}	
	}
	
	private void updateVersion(){
		try {
			RemoteNodeInfo nodeInfo = DiscoveryService.getInstance().scanRemoteNode(target.getGatewayId(),
					DeployTargetDetail.localAdmin, DeployTargetDetail.localDomain, DeployTargetDetail.localAdminPassword,
					target.getServerName(), target.getUsername(), target.getPassword());
			target.setProtocol(nodeInfo.getD2DProtocol());
			target.setPort(nodeInfo.getD2DPortNumber() );
			//update version info to DB
			connectionInfoDao.as_edge_connect_info_update_version(target.getNodeID(),nodeInfo.getD2DMajorVersion(), nodeInfo.getD2DMinorVersion(), nodeInfo.getUpdateVersionNumber(), nodeInfo.getD2DBuildNumber());

			logger.info("[ProductDeployTask] Update version info succeed for target: "+targetName);
		} catch ( Exception e  ){
			logger.error("[ProductDeployTask]Update version info failed, that is scan remote node failed for target"+targetName,e);
		}
		
		//When agent connect info can't connect the web service, then need update the connect info
		EdgeCommonUtil.updateSameNameMachineConnectInfo(target.getServerName(), new int[]{target.getNodeID()});
	}
	
	private int getLastPlanDeployReason(int hostId){
		List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
		this.edgePolicyDao.getHostPolicyMap(hostId, PolicyTypes.Unified, mapList );
		if(mapList==null || mapList.size()<1){
			return PolicyDeployReasons.PolicyContentChanged;
		}
		EdgeHostPolicyMap map = mapList.get( 0 );
		return map.getDeployReason();
	}
	
	private void updateTaskInfo(TaskStatus status){
		TaskDetail<DeployTargetDetail> taskDetail = new TaskDetail<DeployTargetDetail>();
		taskDetail.setCode(target.getStatus());
		taskDetail.setMessage(target.getProgressMessage());
		taskDetail.setRawData(target); 
		TaskMonitor.updateTaskStatus(taskID, status, taskDetail);
		if(status==TaskStatus.Error || status==TaskStatus.Warning || status==TaskStatus.OK)
			ProductDeployServiceImpl.getInstance().writeActivityLogForDeployTarget(target);
	}
	
	private void updateTarget(TaskStatus taskStatus,DeployStatus deployStatus){
		updateTarget(taskStatus,deployStatus,0);
	}
	private void updateTarget(TaskStatus taskStatus,DeployStatus deployStatus,long timeout){
		updateTarget(taskStatus, deployStatus,target.getProgressMessage(),target.getWarningMessage(),timeout);
	}
	private void updateTarget(TaskStatus taskStatus,DeployStatus deployStatus,String progressMessage,String warnningMessage){
		updateTarget(taskStatus, deployStatus,progressMessage,warnningMessage,0);
	}
	private void updateTarget(TaskStatus taskStatus,DeployStatus deployStatus,String progressMessage,String warnningMessage , long timeout){
		if(target == null || deployStatus == null){
			logger.error("[ProductDeployTask] updateTarget() failed. target==null or deploystatus==null. deploystatus:"+deployStatus);
			return;
		}
		boolean needUpdate = false;
		if(target.getStatus() != deployStatus.value()){
			target.setStatus(deployStatus.value());
			needUpdate = true;
		}
		if(target.getTaskstatus() != taskStatus.getValue()){
			target.setTaskstatus(taskStatus.getValue());
			needUpdate = true;
		}
		if(!simpleEqual(target.getProgressMessage(), progressMessage)){
			target.setProgressMessage(progressMessage);
			needUpdate=true;
		}
		if(!simpleEqual(target.getWarningMessage(), warnningMessage)){
			target.setWarningMessage(warnningMessage);
			needUpdate = true;
		}
		if(needUpdate){
			updateTarget(timeout); //update target self
			updateTaskInfo(taskStatus); //update task monitor content
		}
	}
	
	private boolean simpleEqual(Object a, Object b){
		if(a==null && b!=null)
			return false;
		if(b==null && a!=null)
			return false;
		if(a==null && b==null)
			return true;
		if(a.equals(b))
			return true;
		return false;
	}
	
	private void updateTarget(long timeOutOfConnectWS){
		try {
			long minuteTime = timeOutOfConnectWS/60000;
			String[] finalMessgage = ProductDeployUtil.getFinalMessage(target.getProductType(), TaskStatus.parseTaskStatus(target.getTaskstatus()), 
					DeployStatus.valueOf(target.getStatus()), target.getProgressMessage(), target.getWarningMessage(), minuteTime,getGateWayHostName());
			target.setFinalTitleMessage(finalMessgage[0]);
			target.setFinalDetailMessage(finalMessgage[1]);
			targetDao.updateDeployTargetStatus(target.getNodeID(),target.getProtocol().ordinal(),target.getPort(), target.getStatus(), target.getTaskstatus(),
					target.getProgressMessage(), target.getWarningMessage());
		} catch (Exception e) {
			logger.error("[ProductDeployTask]: Update deploy target "+targetName+" to DB failed.",e);
		}
	}
	
	private void deployFinishedWithOk(){
		updateTarget(TaskStatus.OK, DeployStatus.DEPLOY_SUCCESS);
		logger.info("[ProductDeployTask]: Product deploy for "+targetName+" successed. status is: "+target.getStatus());
		PlanStatusUpdater.updatePlanDeployD2DSuccessed(target);
		updateNode();
	}
	
	private void deployFinishedNeedReboot(){
		updateTarget(TaskStatus.Warning,DeployStatus.DEPLOY_SUCCESS_NEEDREBOOT);
		logger.info("[ProductDeployTask]: Product deploy for "+targetName+" have warnnings. status is: "+target.getStatus());
		updateVersion();
		PlanStatusUpdater.updatePlanDeployD2DSuccessed(target);
	}
	
	private void deployFinishedWithError(){
		logger.info("[ProductDeployTask]: Product deploy for "+targetName+" failed. status is: "+target.getStatus());
		updateTarget(TaskStatus.Error,DeployStatus.valueOf(target.getStatus()));
		PlanStatusUpdater.updatePlanDeployD2DFailed(target);
	}
	
	private void deployFinishedWithTimeOut(long timeout,DeployStatus deployStatus){
		updateTarget(TaskStatus.Warning, deployStatus, timeout);
		logger.info("[ProductDeployTask]: Product deploy for "+targetName+" have warnnings. status is: "+target.getStatus());
		updateVersion();
		PlanStatusUpdater.updatePlanDeployD2DSuccessed(target);
	}
	
	private ConnectionContext createConnectionContext(String protocol, String nodeName, 
			int port, String userName, String password,int nodeId) throws EdgeServiceFault{
		ConnectionContext context = new ConnectionContext(protocol, nodeName, port);
		context.buildCredential(userName, password, "");
		GatewayEntity gateway = gatewayService.getGatewayByHostId(nodeId);
		context.setGateway(gateway);
		return context;
	}
	
	private boolean doPreProcess(DeployTargetDetail target){
		String targetName = target.getNodeID()+"_"+(StringUtil.isEmptyOrNull(target.getServerName())?"":target.getServerName());
		try {
			int nodeId = target.getNodeID();
			String product = "";
			if(Integer.parseInt(ProductType.ProductRPS) == target.getProductType()){
				product = EdgeCMWebServiceMessages.getResource("productShortNameRPS");
			}else {
				product = EdgeCMWebServiceMessages.getResource("productShortNameD2D");
			}
			
			Node node = nodeService.getNodeDetailInformation(nodeId);
			if(StringUtil.isEmptyOrNull(node.getHostname())){
				logger.info("[ProductDeployTask]: The target "+targetName+" have no host name, so deploy job failed.");
				target.setProgressMessage(EdgeCMWebServiceMessages.getMessage("deployFailedWithNoHostName",product,target.getServerName()));
				gennerateFailedTask(target);
				return false;
			}
			if(!isWindowsNode(node)){
				logger.info("[ProductDeployTask]: Not support the deployment of linux node "+targetName+", so deploy job failed.");
				target.setProgressMessage(EdgeCMWebServiceMessages.getMessage("deployFailedWithNoNWindows",product,target.getServerName()));
				gennerateFailedTask(target);
				return false;
			}
			
			if(StringUtil.isEmptyOrNull(node.getUsername())){
				logger.info("[ProductDeployTask]: The target "+targetName+" have no user name, so deploy job failed.");
				target.setProgressMessage(EdgeCMWebServiceMessages.getMessage("deployFailedWithNoCredential",product,target.getServerName()));//need add the new message
				gennerateFailedTask(target);
				return false;
			}
			
			if (sameNameMachineNotSelfIsRunning(target)) {
				logger.info("[ProductDeployTask]: Anoter same name remote deploy job for: "+targetName+" is running, failed to submit.");
				target.setProgressMessage(EdgeCMWebServiceMessages.getResource("deployAgentFailedWithAnotherRun"));
				gennerateFailedTask(target);
				return false;
			}
			
			//check dest rps version
			if(target.isCheckDestinationVersion())
				return doCheckDestRpsVersion(target, nodeId);
			
			return true;
		
		}catch (EdgeServiceFault e) {
			logger.error("[ProductDeployTask]: Submit deploy job failed for the target: "+targetName,e);
		} catch (DaoException e) {
			logger.error("[ProductDeployTask]: Submit deploy job failed for the target: "+targetName,e);
		}
		return false;
	}
	
	private boolean doCheckDestRpsVersion(DeployTargetDetail target, int nodeId){
		List<ValuePair<String, String>> lowerRPSList = null;
		List<ValuePair<String, String>> lowerRemoteConsoleList = null;
		
		if(Integer.parseInt(ProductType.ProductRPS) == target.getProductType()){
			//judge related replication task rps version
			lowerRPSList = checkRpsDesRpsVersion(nodeId);
			lowerRemoteConsoleList = checkMspDestinationRpsVersion(nodeId);
		}else {
			//judge backup task rps version
			lowerRPSList = checkAgentDesRpsVersion(nodeId);
			int relatedRpsId = getSameRPSHostIdByAgentId(nodeId);
			lowerRemoteConsoleList = checkMspDestinationRpsVersion(relatedRpsId);
		}
		
		String deployFailedMessage = null;
		if(haveValues(lowerRPSList) && haveValues(lowerRemoteConsoleList)){
			String rpss = builderRpsServerString(lowerRPSList);
			String remoteConsoles = builderRemoteConsoleServerString(lowerRemoteConsoleList);
			logger.info("[ProductDeployTask]: The target "+targetName+" 's destination RPSs: "+rpss+" and rpss in remote console: "+remoteConsoles+" is lower version. deploy failed.");
			deployFailedMessage = EdgeCMWebServiceMessages.getMessage("deployFailedWithMultiDesRpsVersionLower",target.getServerName())
					+" "+ EdgeCMWebServiceMessages.getMessage("deployFailedWithMultiBothCurrentAndRemote",rpss,remoteConsoles);
		}else if (haveValues(lowerRPSList) && !haveValues(lowerRemoteConsoleList)) {
			String rpss = builderRpsServerString(lowerRPSList);
			logger.info("[ProductDeployTask]: The target "+targetName+" 's destination RPSs: "+rpss+" is lower version. deploy failed.");
			deployFailedMessage = EdgeCMWebServiceMessages.getMessage("deployFailedWithMultiDesRpsVersionLower",target.getServerName())
					+" "+ EdgeCMWebServiceMessages.getMessage("deployFailedWithMultiCurrent",rpss);
		}else if (!haveValues(lowerRPSList) && haveValues(lowerRemoteConsoleList)) {
			String remoteConsoles = builderRemoteConsoleServerString(lowerRemoteConsoleList);
			logger.info("[ProductDeployTask]: The target "+targetName+" 's destination rpss in remote console: "+remoteConsoles+" is lower version. deploy failed.");
			deployFailedMessage = EdgeCMWebServiceMessages.getMessage("deployFailedWithMultiDesRpsVersionLower",target.getServerName())
					+" "+ EdgeCMWebServiceMessages.getMessage("deployFailedWithMultiRemote",remoteConsoles);
		}
		
		if(deployFailedMessage != null){
			target.setProgressMessage(deployFailedMessage);
			gennerateWarningTask(target);
			return false;
		}
		
		return true;
	}
	
	private boolean haveValues(List<ValuePair<String, String>> list){
		if(list == null || list.isEmpty())
			return false;
		return true;
	}
	
	private String builderRpsServerString(List<ValuePair<String, String>> lowerRPSList){
		StringBuilder rpsServerBuilder = new StringBuilder("");
		rpsServerBuilder.append(lowerRPSList.get(0).getKey()).
			append("(").append(lowerRPSList.get(0).getValue()).append(")");
		for (int i = 1 ; i < lowerRPSList.size() ; i++) {
			rpsServerBuilder.append(", ").append(lowerRPSList.get(i).getKey()).
			append("(").append(lowerRPSList.get(i).getValue()).append(")");
		}
		return rpsServerBuilder.toString();
	}
	
	private String builderRemoteConsoleServerString(List<ValuePair<String, String>> lowerRemoteConsoleList){
		StringBuilder remoteConsoleServerBuilder = new StringBuilder("");
		remoteConsoleServerBuilder.append(lowerRemoteConsoleList.get(0).getKey());
		if(!StringUtil.isEmptyOrNull(lowerRemoteConsoleList.get(0).getValue()))
			remoteConsoleServerBuilder.append("(").append(lowerRemoteConsoleList.get(0).getValue()).append(")");
		for (int i = 1 ; i < lowerRemoteConsoleList.size() ; i++) {
			remoteConsoleServerBuilder.append(", ").append(lowerRemoteConsoleList.get(i).getKey());
			if(!StringUtil.isEmptyOrNull(lowerRemoteConsoleList.get(0).getValue()))
				remoteConsoleServerBuilder.append("(").append(lowerRemoteConsoleList.get(i).getValue()).append(")");
		}
		return remoteConsoleServerBuilder.toString();
	}
	
	/**
	 * For the target which have not passed the pre check, service not submit a real task to running, but service will fake a failed task
	 * so that UI can display the failed message for this target.
	 * @param target
	 */
	private void gennerateFailedTask(DeployTargetDetail target){
		target.setProgressMessage(target.getProgressMessage());
		target.setStatus(DeployStatus.DEPLOY_FAILED.value());
		deployFinishedWithError();
//		target.setTaskstatus(TaskStatus.Error.getValue());
//		String[] finalMessage = ProductDeployUtil.getFinalMessage(target.getProductType(), TaskStatus.Error, DeployStatus.DEPLOY_FAILED, 
//				target.getProgressMessage(), target.getWarningMessage(), 0);
//		target.setFinalTitleMessage(finalMessage[0]);
//		target.setFinalDetailMessage(finalMessage[1]);
//		//update target to DB
//		targetDao.updateDeployTargetStatus(target.getNodeID(),target.getProtocol().ordinal(),target.getPort(), target.getStatus(), target.getTaskstatus(),
//				target.getProgressMessage(), target.getWarningMessage());
//		//New an task and update task status
//		TaskDetail<DeployTargetDetail> taskDetail = new TaskDetail<DeployTargetDetail>();
//		taskDetail.setCode(target.getStatus());
//		taskDetail.setMessage(target.getProgressMessage());
//		taskDetail.setRawData(target); 
//		String taskName = target.getNodeID()+"_"+target.getServerName();
//		int taskID = TaskMonitor.registerNewTask(Module.RemoteDeploy, taskName, taskDetail);
//		TaskMonitor.setTaskStarted(taskID);
//		TaskMonitor.updateTaskStatus(taskID, TaskStatus.Error, taskDetail);
//		//write activity log
//		ProductDeployServiceImpl.getInstance().writeActivityLogForDeployTarget(target);
//		//update plan status
//		PlanStatusUpdater.updatePlanDeployD2DFailed(target);
	}
	
	private void gennerateWarningTask(DeployTargetDetail target){
		target.setProgressMessage(target.getProgressMessage());
		target.setStatus(DeployStatus.DEPLOY_FAILED.value());
		logger.info("[ProductDeployTask]: Product deploy for "+targetName+" has warnnings. status is: "+target.getStatus());
		updateTarget(TaskStatus.WarnningCanContinue,DeployStatus.valueOf(target.getStatus()));
		PlanStatusUpdater.updatePlanDeployD2DFailed(target);
	}
	
	private boolean isWindowsNode(Node node){
		if(node.isLinuxNode()) //linux node
			return false;
		if((node.getProtectionTypeBitmap() & ProtectionType.LINUX_D2D_SERVER.getValue() ) == ProtectionType.LINUX_D2D_SERVER.getValue())
			return false;//linux server
		return true;
	}
	
	private boolean selfIsRunning(DeployTargetDetail detail){
		List<Task> deployTasks = TaskMonitor.getTasksByModule(Module.RemoteDeploy);
		List<DeployTargetDetail> details = new ArrayList<DeployTargetDetail>();
		targetDao.getDeployTargetByNodeId(detail.getNodeID(), details);
		if(!details.isEmpty()){
			DeployTargetDetail detail2 = details.get(0);
			for(Task task : deployTasks){
				DeployTargetDetail targetTask = (DeployTargetDetail) task.getDetails().getRawData();
				if(targetTask.getNodeID() == detail2.getNodeID()){
					if(targetTask.isSelected()
							&& (targetTask.getTaskstatus()==TaskStatus.Pending.ordinal() 
								|| targetTask.getTaskstatus()==TaskStatus.InProcess.ordinal())){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean sameNameMachineNotSelfIsRunning(DeployTargetDetail target){
		List<Task> deployTasks = TaskMonitor.getTasksByModule(Module.RemoteDeploy);
		List<DeployTargetDetail> details = new ArrayList<DeployTargetDetail>();
		targetDao.getDeployTargetsByServerName(target.getServerName(), details);
		if(!details.isEmpty()){
			for (DeployTargetDetail detail : details) {
				if((detail.getNodeID()!=target.getNodeID())
						&& detail.isSelected()){
					target.setSelected(true);
				}
			}
		}else {
			return false;
		}
		for(Task task : deployTasks){
			DeployTargetDetail targetTask = (DeployTargetDetail) task.getDetails().getRawData();
			if(target.isSelected()
					&& targetTask.getServerName().equals(target.getServerName())
					&& (targetTask.getTaskstatus()==TaskStatus.Pending.ordinal() 
						|| targetTask.getTaskstatus()==TaskStatus.InProcess.ordinal())){
				return true;
			}
		}
		return false;
	}
	
	private void writeSelfIsRunningActivityLog(){
		//Made the another running message to write the log, and skip the submit.
		logger.info("[ProductDeployTask]: Remote deploy job for: "+targetName+" is running, skip the submit.");
		String[] finalMessage = ProductDeployUtil.getFinalMessage(target.getProductType(), TaskStatus.Error, DeployStatus.DEPLOY_FAILED, 
				EdgeCMWebServiceMessages.getResource("deployAgentFailedWithAnotherRun"), "", 0,getGateWayHostName());
		String logContent = "";
		if(!StringUtil.isEmptyOrNull(finalMessage[0])){
			logContent = finalMessage[0]+". ";
		}
		logContent = logContent+finalMessage[1];
		ProductDeployServiceImpl.getInstance().writeAcitivityLog(Severity.Error,0,target.getNodeID(),logContent);
	}
	
	//Currently, we just have create datastore to observe deploy finish status
	//If have more observers, then add observers here
	//After service restarted, ProductDeployTask will be invoked, then register observers also be invoked,
	private void registerObservers(){
		RPSDataStoreServiceImpl observer = new RPSDataStoreServiceImpl();
		if(observer.haveDataStoreSettingsToCreate()){
			this.addObserver(observer);
		}
	}
	
	private String getGateWayHostName(){
		int nodeId = target.getNodeID();
		return EdgeCommonUtil.getGatewayHostNameByNodeId(nodeId);
	}

	private List<ValuePair<String, String>> checkAgentDesRpsVersion(int agentId){
		int sameNameRpsId = getSameRPSHostIdByAgentId(agentId);
		List<ValuePair<String, String>> agentBased = asSourceCheckAgentBasedBackupTaskDesRpsVersion(agentId, sameNameRpsId);
		List<ValuePair<String, String>> agentLess = asProxyCheckAgentLessBackupTaskDesRpsVersion(agentId, sameNameRpsId);
		List<ValuePair<String, String>> rpsReplication = asReplicationTaskSourceCheckDesRpsVersion(sameNameRpsId);
		return mergeResultList(agentBased, agentLess, rpsReplication);
	}
	
	private List<ValuePair<String, String>> checkRpsDesRpsVersion(int rpsId){
		int sameNameAgentId = getSameAgentHostIdByRpsId(rpsId);
		List<ValuePair<String, String>> agentBased = asSourceCheckAgentBasedBackupTaskDesRpsVersion(sameNameAgentId, rpsId);
		List<ValuePair<String, String>> agentLess = asProxyCheckAgentLessBackupTaskDesRpsVersion(sameNameAgentId, rpsId);
		List<ValuePair<String, String>> rpsReplication = asReplicationTaskSourceCheckDesRpsVersion(rpsId);
		return mergeResultList(agentBased, agentLess, rpsReplication);
	}
	
	private List<ValuePair<String, String>> mergeResultList(List<ValuePair<String, String>> list1,
			List<ValuePair<String, String>> list2,
			List<ValuePair<String, String>> list3){
		List<ValuePair<String, String>> result = new ArrayList<ValuePair<String, String>>();
		if(list1 != null && !list1.isEmpty()){
			for(ValuePair<String, String> rps : list1){
				if(!containsRpsInResult(rps.getKey(), result)){
					result.add(rps);
				}
			}
		}
		if(list2 != null && !list2.isEmpty()){
			for(ValuePair<String, String> rps : list2){
				if(!containsRpsInResult(rps.getKey(), result)){
					result.add(rps);
				}
			}
		}
		if(list3 != null && !list3.isEmpty()){
			for(ValuePair<String, String> rps : list3){
				if(!containsRpsInResult(rps.getKey(), result)){
					result.add(rps);
				}
			}
		}
		return result;
	}
	
	/**
	 * If rps node have used by plan, then check whether is the source of replication task
	 * If yes, check the destination rps version
	 * If destination rps is lower then return the plan name and destination rps host name.
	 * ValuePair<RpsName, RpsVersion> list
	 * @param rpsNodeId
	 * @return
	 */
	private List<ValuePair<String, String>> asReplicationTaskSourceCheckDesRpsVersion(int rpsNodeId){
		if(rpsNodeId <= 0)
			return null;
		List<ValuePair<String, String>> result = new ArrayList<ValuePair<String,String>>();
		PolicyManagementServiceImpl policyManagementServiceImpl = PolicyManagementServiceImpl.getInstance();
		List<EdgePolicy> planList = new ArrayList<EdgePolicy>();
		edgePolicyDao.as_edge_policy_list_bytype( 6, 0, planList );
		if(planList == null || planList.isEmpty()){
			return null;
		}
		for(EdgePolicy edgePolicy : planList){
			int policyId = edgePolicy.getId();
			UnifiedPolicy plan = null;
			try {
				plan = policyManagementServiceImpl.loadUnifiedPolicyById(policyId);
			} catch (Exception e) {
				logger.error("[ProductDeployTask] asReplicationTaskSourceCheckDesRpsVersion() failed to load plan for plan id: "+policyId);
				continue;
			}
			if(plan != null){
				
				boolean currentRpsIsBackupDes = false;
				
				if(plan.getBackupConfiguration()!=null){
					if(!plan.getBackupConfiguration().isD2dOrRPSDestType()){
						int hostid=plan.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
						if(rpsNodeId == hostid){
							currentRpsIsBackupDes = true;
						}
					}
				}
				if(!currentRpsIsBackupDes && plan.getVSphereBackupConfiguration()!=null){
					if(!plan.getVSphereBackupConfiguration().isD2dOrRPSDestType()){
						int hostid=plan.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
						if(rpsNodeId == hostid){
							currentRpsIsBackupDes = true;
						}
					}
				}
				
				if(!currentRpsIsBackupDes && plan.getMspServerReplicationSettings()!=null){//replicate from remote server
					int hostid = plan.getMspServerReplicationSettings().getHostId();
					if(rpsNodeId == hostid){
						currentRpsIsBackupDes = true;
					}
				}
				
				if(currentRpsIsBackupDes && plan.getRpsPolices().size() > 1){ //have repliaction task
					RPSPolicyWrapper rpsPolicyWrapper = plan.getRpsPolices().get(0);
					int desRpsHostId = rpsPolicyWrapper.getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getHostId();
					if(EdgeCommonUtil.compareWithConsoleVersion(desRpsHostId)<0){
						String hostname=rpsPolicyWrapper.getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getHostName();
						if(!containsRpsInResult(hostname, result)){
							ValuePair<String, String> desRps1 = new ValuePair<String, String>();
							desRps1.setKey(hostname);
							desRps1.setValue(EdgeCommonUtil.getDisplayVersionByHostId(desRpsHostId));
							result.add(desRps1);
							continue;
						}
					}
				}
				
				if(!currentRpsIsBackupDes){ //if it is not backup destination then chek whether is the source of rps
					List<RPSPolicyWrapper> rpsPolicyWrappers = plan.getRpsPolices();
					for(int i=0; i < rpsPolicyWrappers.size()-1; i++){
						RPSPolicyWrapper rpsPolicyWrapper = rpsPolicyWrappers.get(i);
						int hostId =  rpsPolicyWrapper.getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getHostId();
						if(hostId == rpsNodeId){
							RPSPolicyWrapper desWrapper = rpsPolicyWrappers.get(i+1);
							int desRpsHostId = desWrapper.getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getHostId();
							if(desRpsHostId == 0){
								continue;
							}
							if(EdgeCommonUtil.compareWithConsoleVersion(desRpsHostId)<0){
								String hostName = desWrapper.getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getHostName();
								if(!containsRpsInResult(hostName, result)){
									ValuePair<String, String> desRps2 = new ValuePair<String, String>();
									desRps2.setKey(hostName);
									desRps2.setValue(EdgeCommonUtil.getDisplayVersionByHostId(desRpsHostId));
									result.add(desRps2);
									continue;
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	private boolean containsRpsInResult(String hostName, List<ValuePair<String, String>> rpsSet){
		if(hostName == null || hostName.isEmpty() || rpsSet == null || rpsSet.isEmpty())
			return false;
		for (ValuePair<String, String> rps : rpsSet) {
			if(rps.getKey()!=null && rps.getKey().equalsIgnoreCase(hostName)){
				return true;
			}
		}
		return false;
	}
	
	private int getSameRPSHostIdByAgentId(int id){
		if(id <= 0)
			return 0;
		int rpsId = 0;
		try {
			Node node = nodeService.getNodeDetailInformation(id);
			
			int[] hostIds = new int[1];
			if(node == null || StringUtil.isEmptyOrNull(node.getD2DUUID())){
				//use name find
				rpsNodeDao.as_edge_rps_node_getIdByHostnameIp(node.getGatewayId().getRecordId(),
						node.getHostname(), node.getIpaddress(), hostIds);
				rpsId = hostIds[0];
			}else {
				//use uuid find
				edgeHostMgrDao.as_edge_host_getHostIdByUuid(node.getD2DUUID(), ProtectionType.RPS.getValue(), hostIds);
				if(hostIds[0] > 0){
					rpsId = hostIds[0];
				}else {
					//use name find
					rpsNodeDao.as_edge_rps_node_getIdByHostnameIp(node.getGatewayId().getRecordId(),
							node.getHostname(), node.getIpaddress(), hostIds);
					rpsId = hostIds[0];
				}
			}
		} catch (Exception e) {
			logger.error("[ProductDeployTask] getSameRPSHostIdByAgentId() failed.", e);
			return 0;
		}
		
		return rpsId;
	}
	
	private int getSameAgentHostIdByRpsId(int id){
		if(id == 0)
			return 0;
		int agentId = 0;
		try {
			RpsNode node = rpsNodeService.getRpsNodeDetailInformation(id);
			int[] hostIds = new int[1];
			if(node == null || StringUtil.isEmptyOrNull(node.getUuid())){
				//use host name
				GatewayEntity entity = gatewayService.getGatewayByHostId(id);
				edgeHostMgrDao.as_edge_host_getIdByHostnameIp(entity.getId().getRecordId(),
						node.getNode_name(), node.getIp_address(), 1, hostIds);
				agentId = hostIds[0];
			}else {
				//use uuid find
				edgeHostMgrDao.as_edge_host_getHostIdByUuid(node.getUuid(), ProtectionType.WIN_D2D.getValue(), hostIds);
				if(hostIds[0] > 0){
					agentId = hostIds[0];
				}else {
					//use host name
					GatewayEntity entity = gatewayService.getGatewayByHostId(id);
					edgeHostMgrDao.as_edge_host_getIdByHostnameIp(entity.getId().getRecordId(),
							node.getNode_name(), node.getIp_address(), 1, hostIds);
					agentId = hostIds[0];
				}
			}
		} catch (Exception e) {
			logger.error("[ProductDeployTask] getSameAgentHostIdByRpsId() failed.",e);
			return 0;
		}
		return agentId;
	}
	
	/**
	 * If node have plan, then check whether is the source of agent-based backup task, if have, then check
	 * the whether the destination is Rps, if yes check the rps version, if the rps version is lower
	 * then return ValuePair<RpsName, RpsVersion> list
	 * @param nodeId, sameNameRpsId
	 * @return
	 */
	private List<ValuePair<String, String>> asSourceCheckAgentBasedBackupTaskDesRpsVersion(int nodeId, int sameNameRpsId){
		if(nodeId <= 0)
			return null;
		List<ValuePair<String, String>> result = new ArrayList<ValuePair<String,String>>();
		PolicyManagementServiceImpl policyManagementServiceImpl = PolicyManagementServiceImpl.getInstance();
		List<PolicyInfo> lstPolicies = policyManagementServiceImpl.getHostPolicies(nodeId);
		if (lstPolicies == null || lstPolicies.size() == 0) {
			return null;
		}
		
		PolicyInfo policyInfo = lstPolicies.get(0);
		int policyId = policyInfo.getPolicyId();
		UnifiedPolicy plan = null;
		try {
			plan = policyManagementServiceImpl.loadUnifiedPolicyById(policyId);
		} catch (Exception e) {
			logger.error("[ProductDeployTask] asSourceCheckAgentBasedBackupTaskDesRpsVersion() failed to load plan for plan id: "+policyId);
			return null;
		}
		if(plan.getBackupConfiguration()!=null){
			if(!plan.getBackupConfiguration().isD2dOrRPSDestType()){
				int hostid=plan.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
				if(EdgeCommonUtil.compareWithConsoleVersion(hostid)<0){
					String hostname=plan.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
					if(sameNameRpsId <= 0 || (sameNameRpsId >0 && sameNameRpsId != hostid)){ //not backup to self rps
						ValuePair<String, String> desRps2 = new ValuePair<String, String>();
						desRps2.setKey(hostname);
						desRps2.setValue(EdgeCommonUtil.getDisplayVersionByHostId(hostid));
						result.add(desRps2);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * If node have plan, then check whether is the proxy of agent-less backup task, if have, then check
	 * the whether the destination is Rps, if yes check the rps version, if the rps version is lower
	 * then return ValuePair<RpsName, RpsVersion> list
	 * @param nodeId , sameNameRpsId
	 * @return
	 */
	private List<ValuePair<String, String>> asProxyCheckAgentLessBackupTaskDesRpsVersion(int proxyNodeId, int sameNameRpsId){
		if(proxyNodeId <= 0)
			return null;
		List<ValuePair<String, String>> result = new ArrayList<ValuePair<String,String>>();
		PolicyManagementServiceImpl policyManagementServiceImpl = PolicyManagementServiceImpl.getInstance();
		List<EdgePolicy> planList = new ArrayList<EdgePolicy>();
		edgePolicyDao.as_edge_policy_list_byProxyHostId(proxyNodeId, planList);
		if(planList == null || planList.isEmpty())
			return null;
		for (EdgePolicy edgePolicy : planList) {
			UnifiedPolicy plan = null;
			try {
				plan = policyManagementServiceImpl.loadUnifiedPolicyById(edgePolicy.getId());
			} catch (Exception e) {
				logger.error("[ProductDeployTask] asProxyCheckAgentLessBackupTaskDesRpsVersion()"
						+ " Failed to load UnifiedPolicy for plan id: "+edgePolicy.getId());
				return null;
			}
			if(plan.getVSphereBackupConfiguration()!=null){
				if(!plan.getVSphereBackupConfiguration().isD2dOrRPSDestType()){
					int hostid=plan.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
					if(EdgeCommonUtil.compareWithConsoleVersion(hostid)<0){
						String hostname=plan.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
						if((sameNameRpsId <= 0 || (sameNameRpsId > 0 && sameNameRpsId != hostid )) && !containsRpsInResult(hostname, result)){ //Not backup to self RPS server
							ValuePair<String, String> desRps2 = new ValuePair<String, String>();
							desRps2.setKey(hostname);
							desRps2.setValue(EdgeCommonUtil.getDisplayVersionByHostId(hostid));
							result.add(desRps2);
						}
					}
				}
			}
		}
		return result;
	}
	
	private List<ValuePair<String, String>> checkMspDestinationRpsVersion(int rpsNodeId){
		if(rpsNodeId <= 0)
			return null;
		List<ValuePair<String, String>> result = new ArrayList<ValuePair<String,String>>();
		PolicyManagementServiceImpl policyManagementServiceImpl = PolicyManagementServiceImpl.getInstance();
		List<EdgePolicy> planList = new ArrayList<EdgePolicy>();
		edgePolicyDao.as_edge_policy_list_bytype( 6, 0, planList );
		if(planList == null || planList.isEmpty()){
			return null;
		}
		String unreachableString = EdgeCMWebServiceMessages.getMessage("remoteConsoleUnreachable");
		for(EdgePolicy edgePolicy : planList){
			int policyId = edgePolicy.getId();
			UnifiedPolicy plan = null;
			try {
				plan = policyManagementServiceImpl.loadUnifiedPolicyById(policyId);
			} catch (Exception e) {
				logger.error("[ProductDeployTask] checkMspDestinationRpsVersion() failed to load plan for plan id: "+policyId);
				continue;
			}
			if(plan != null){
				if(plan.getMspReplicationDestination() != null && plan.getRpsPolices().size() > 0){
					
					if(plan.getRpsPolices().size() > 1){ //replication + msp remote replication
						int sourceRpsHostId = plan.getRpsPolices().get(plan.getRpsPolices().size()-2).getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getHostId();
						if(sourceRpsHostId != rpsNodeId)
							continue;
					}
					
					if(plan.getRpsPolices().size() == 1){//backup + msp remote repliaction
						if(plan.getBackupConfiguration() != null
								&& !plan.getBackupConfiguration().isD2dOrRPSDestType()){
							if(plan.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId()!=rpsNodeId){
								continue;
							}
						}else if(plan.getVSphereBackupConfiguration() != null
								&& !plan.getVSphereBackupConfiguration().isD2dOrRPSDestType()){
							if(plan.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId() != rpsNodeId){
								continue;
							}
						}
					}
					
					String consoleName = plan.getMspServer().getRhostname();
					if(containsRpsInResult(consoleName, result))
						continue;
					
					RpsHost rpsDes = plan.getMspReplicationDestination().getReplicationServer();
					RPSPolicyWrapper mspSourceRpsPolicy = plan.getRpsPolices().get(plan.getRpsPolices().size()-1);
					int gatewayId = mspSourceRpsPolicy.getSiteId();
					VersionInfo rpsVersion = null;
					HttpProxy clientHttpProxy = null;
					try{
						RPSReplicationSettings rpsReplicationSettings = plan.getRpsPolices().get(0).getRpsPolicy().getRpsSettings().getRpsReplicationSettings();
						if(rpsReplicationSettings.isEnableProxy()){
							clientHttpProxy = rpsReplicationSettings.isProxyRequireAuthentication()
									? new HttpProxy(rpsReplicationSettings.getProxyHostname(), rpsReplicationSettings.getProxyPort()
											, rpsReplicationSettings.getProxyUsername(), rpsReplicationSettings.getProxyPassword())
									: new HttpProxy(rpsReplicationSettings.getProxyHostname(), rpsReplicationSettings.getProxyPort());
						
								CAProxy proxy = new CAProxy();
								proxy.setTargetHost(rpsDes.getRhostname());
								proxy.setHttpProxy(clientHttpProxy);
								CAProxySelector.getInstance().registryProxy(proxy);
						}
						
						String protocol = rpsDes.isHttpProtocol() ? "http" : "https";
					
						ConnectionContext context = new ConnectionContext(protocol, rpsDes.getRhostname(), rpsDes.getPort());
						//context.buildCredential(rpsDes.getUsername(), rpsDes.getPassword(), "");
						GatewayEntity gateway = gatewayService.getGatewayById(new GatewayId(gatewayId));
						context.setGateway(gateway);

						D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context));
						connection.connect(false);
						rpsVersion = connection.getService().getVersionInfo();
					}catch(Exception e){
						logger.error("[ProductDeployTask] checkMspDestinationRpsVersion() check remote rps version failed. ",e );
						ValuePair<String, String> remoteConsole = new ValuePair<String, String>();
						remoteConsole.setKey(consoleName);
						remoteConsole.setValue(unreachableString);
						result.add(remoteConsole);
						continue;
					}finally{
						if(clientHttpProxy != null){
							CAProxySelector.getInstance().unRegistryProxy(rpsDes.getRhostname());
						}
					}
					if(rpsVersion != null){
						if(EdgeCommonUtil.compareWithConsoleVersion(rpsVersion)<0){
							ValuePair<String, String> remoteConsole = new ValuePair<String, String>();
							remoteConsole.setKey(consoleName);
							remoteConsole.setValue(null);
							result.add(remoteConsole);
							continue;
						}
					}
				}	
			}
		}
		return result;
	}
}
