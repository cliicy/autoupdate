package com.ca.arcserve.edge.app.rps.webservice.node;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.rps.webservice.data.DisabledNodes;
import com.ca.arcflash.rps.webservice.data.ManualFilecopyItem;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.registration.RPSRegInfo;
import com.ca.arcflash.rps.webservice.replication.ManualMergeItem;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.restore.BackupD2D;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeRPSDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSBDao;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.common.ConsoleUrlUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.NodeConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.RpsSettingTaskDeployment;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryApplication;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RPSConverterNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RPSSourceNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult.NodeManagedStatusByConsole;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanEnableStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.RPSPolicyWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.d2dreg.EdgeD2DRegServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.msp.webservice.MspNodeServiceImpl;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsConnectionInfoDao;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsNodeDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsConnectionInfo;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsDataStoreUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsNodeUtil;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.AddRpsNodesResult;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.NodeRegistrationInfoForRPS;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsConnectionInfo;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;
import com.ca.arcserve.edge.app.rps.webservice.datastore.DataStoreManager;
import com.ca.arcserve.edge.app.rps.webservice.node.setting.RPSNodeSettingServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.rps.IEdgeRPSRegService;
import com.ca.arcserve.edge.app.rps.webservice.rps.IRPSNodeService;
import com.ca.arcserve.edge.app.rps.webservice.rpsReg.EdgeRPSRegServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.serviceexception.EdgeRpsServiceErrorCode;
import com.ca.arcserve.edge.app.rps.webservice.setting.datastore.DataStoreWebUtil;

public class RPSNodeServiceImpl implements IRPSNodeService {
		
	private static final Logger logger = Logger.getLogger(RPSNodeServiceImpl.class);
	
	private IRpsNodeDao rpsNodeDao = DaoFactory.getDao(IRpsNodeDao.class);
	private IEdgeRPSDao edgeRpsDao = DaoFactory.getDao(IEdgeRPSDao.class);
	private IEdgeConnectInfoDao conInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IRpsConnectionInfoDao rpsConnectionInfoDao = DaoFactory.getDao(IRpsConnectionInfoDao.class);
	IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeVSBDao vsbDao;
	private IEdgeRPSRegService regService = new EdgeRPSRegServiceImpl();
	private ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	private static final String htmlSpecialCharsRegx = "[<>\\\\/&]";
	Pattern htmlPattern = Pattern.compile(htmlSpecialCharsRegx, Pattern.UNICODE_CASE);
	EdgeWebServiceImpl serviceImpl;
	NativeFacade  nativeFacade = new NativeFacadeImpl();
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	public RPSNodeServiceImpl(EdgeWebServiceImpl serviceImpl){
		this.serviceImpl = serviceImpl;	
	}

	public RPSNodeServiceImpl(){
	}
	
	@Override
	public List<RpsNode> getRpsNodesByGroup(int gateway, int groupID) throws EdgeServiceFault {
		List<EdgeRpsNode> hosts = new LinkedList<EdgeRpsNode>();
		rpsNodeDao.as_edge_rps_node_list_bygroupid(gateway, groupID, null,hosts);
		
		MspNodeServiceImpl mspNodeService = new MspNodeServiceImpl();
		Set<Integer> mspReplicateRpsServerIds = mspNodeService.getMspReplicateRpsServerIds();
		
		List<RpsNode> nodes = new LinkedList<RpsNode>();
		for (EdgeRpsNode host : hosts){
			RpsNode rpsNode = convertDaoNode2ContractNode(host);
			rpsNode.setMspReplicateDestination(mspReplicateRpsServerIds.contains(rpsNode.getNode_id()));
			nodes.add(rpsNode);
		}
		
		return nodes;
	}
	
	public static RpsNode convertDaoNode2ContractNode(EdgeRpsNode daoNode){

		if(daoNode==null)
			return null;
		
		RpsNode host = new RpsNode();
		host.setNode_id(daoNode.getNode_id());
		host.setNode_name(daoNode.getNode_name());
		host.setNode_description(daoNode.getNode_description());
		host.setIp_address(daoNode.getIp_address());
		host.setLastUpdate(daoNode.getLastUpdate());
		host.setNode_type(daoNode.getNode_type());
		host.setPort(daoNode.getPort());
		host.setProtocol(daoNode.getProtocol());
		host.setUsername(daoNode.getUsername());
		host.setPassword(daoNode.getPassword());
		host.setUuid(daoNode.getUuid());
		host.setPolicy_count(daoNode.getPolicy_count());
		host.setDedup_store_count(daoNode.getData_store_count());
		host.setRpsInstalled(ApplicationUtil.isRPSInstalled(daoNode.getAppStatus()));
		host.setMajor_version(daoNode.getMajor_version());
		host.setMinor_version(daoNode.getMinor_version());
		host.setBuild_number(daoNode.getBuild_number());
		host.setUpdate_version(daoNode.getD2dUpdateversionnumber());
		if (daoNode.getManage() == NodeManagedStatus.Managed.ordinal())
			host.setManaged(NodeManagedStatus.Managed);
		else if (daoNode.getManage() == NodeManagedStatus.Unmanaged.ordinal())
			host.setManaged(NodeManagedStatus.Unmanaged);
		else
			host.setManaged(NodeManagedStatus.Unknown);
		host.setRemoteDeployStatus(daoNode.getRemoteDeployStatus());
		host.setDeployTaskStatus(daoNode.getDeployTaskStatus());
		host.setRemoteDeployTime(daoNode.getRemoteDeployTime());
		host.setSiteName(daoNode.getSiteName());
		return host;
	}
	
	public static RpsConnectionInfo convertDaoConInfo2ContractConInfo(EdgeRpsConnectionInfo daoInfo){
		if(daoInfo == null)
			return null;
		
		RpsConnectionInfo info = new RpsConnectionInfo();
		info.setNode_id(daoInfo.getNode_id());
		info.setMajor_version(daoInfo.getMajor_version());
		info.setManage(daoInfo.getManage());
		info.setMinor_version(daoInfo.getMinor_version());
		info.setPassword(daoInfo.getPassword());
		info.setPort(daoInfo.getPort());
		info.setProtocol(daoInfo.getProtocol());
		info.setUsername(daoInfo.getUsername());
		info.setUuid(daoInfo.getUuid());
		info.setBuild_number(daoInfo.getBuild_number());
		info.setUpdate_number(daoInfo.getUpdate_number());
		return info;
	}
	
	private ConnectionContext createConnectionContext(NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		GatewayEntity gateway = null;
		if(registrationNodeInfo.getId() > 0){
			gateway = gatewayService.getGatewayByHostId(registrationNodeInfo.getId());
		}else {
			gateway =  gatewayService.getGatewayById(registrationNodeInfo.getGatewayId());
		}

		String protocol = registrationNodeInfo.getD2dProtocol() == Protocol.Https ? "https" : "http";
		ConnectionContext context = new ConnectionContext(protocol, registrationNodeInfo.getNodeName(), registrationNodeInfo.getD2dPort());
		context.buildCredential(registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(), "");
		context.setGateway(gateway);
		
		return context;
	}
	
	private String getAuthUuid(RPSConnection connection, ConnectionContext context) {
		String encryptedAuthUuid = connection.getService().establishTrust(context.getUsername(), context.getPassword(), context.getDomain());
		String authUuid = nativeFacade.AFDecryptString(encryptedAuthUuid);
		return authUuid;
	}
	
	public RegistrationNodeResult registerRpsNode(boolean failedReadRemoteRegistry,
			NodeRegistrationInfo registrationNodeInfo, boolean overwrite) throws EdgeServiceFault {
		
		int hostId = registrationNodeInfo.getId();
				
		GatewayId gatewayId = registrationNodeInfo.getGatewayId();
		if(hostId > 0){
			try {
				GatewayEntity entity = gatewayService.getGatewayByHostId(hostId);
				if(entity != null && entity.getId() != null && entity.getId().isValid()){
					gatewayId = entity.getId();
				}
			} catch (Exception e) {
				logger.error("[RPSNodeServiceImpl] registerNodeResult() get gateway by rps host id : "+hostId+" failed.");
			}
		}
		
		List<String> fqdnNameList = new ArrayList<String>();
		if(gatewayId != null && gatewayId.isValid()){
			try {
				IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId);
				fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(registrationNodeInfo.getNodeName());
			} catch (Exception e) {
				logger.error("[RPSNodeServiceImpl] registerRpsNode() get fqdn name failed.",e);
			}
		}
		
		
		String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
		String ip = getIpByName( gatewayId, registrationNodeInfo.getNodeName() );
		if(hostId <= 0){
			hostId = getHostIdByName(gatewayId.getRecordId(), 
					registrationNodeInfo.getNodeName(), ip, fqdnNameList);
		}
		
		if(hostId <= 0){
			int[] hostIds = new int[1];
			hostMgrDao.as_edge_host_getHostIdByUuid(registrationNodeInfo.getNodeInfo().getD2DUUID(), ProtectionType.RPS.getValue(),
					hostIds);
			if(hostIds[0] != 0)
				hostId = hostIds[0];
		}
		
		if(hostId > 0)
			registrationNodeInfo.setId(hostId);
		
		RemoteNodeInfo nodeInfo = registrationNodeInfo.getNodeInfo();
		if (registrationNodeInfo==null||StringUtil.isEmptyOrNull(registrationNodeInfo.getUsername())
				||StringUtil.isEmptyOrNull(registrationNodeInfo.getPassword())) {
			throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeRpsServiceErrorCode.Node_InvalidUser , ""));
		}
		
		ConnectionContext context = createConnectionContext(registrationNodeInfo);
		
		if(failedReadRemoteRegistry)
		{
			RemoteNodeInfo rpsRemoteNodeInfo = null;
			if(nodeInfo.isRPSInstalled() /*&& registrationNodeInfo.isRegisterD2D()*/)
			{
				rpsRemoteNodeInfo = tryConnectRps(context);
				if(rpsRemoteNodeInfo == null)
					throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeRpsServiceErrorCode.Node_RPS_Reg_connection_refuse , ""));

				nodeInfo.setD2DMajorVersion(rpsRemoteNodeInfo.getD2DMajorVersion());
				nodeInfo.setD2DMinorVersion(rpsRemoteNodeInfo.getD2DMinorVersion());
				nodeInfo.setD2DUUID(rpsRemoteNodeInfo.getD2DUUID());
				nodeInfo.setD2DBuildNumber(rpsRemoteNodeInfo.getD2DBuildNumber());
				nodeInfo.setUpdateVersionNumber(rpsRemoteNodeInfo.getUpdateVersionNumber());
				nodeInfo.setHostEdgeServer(rpsRemoteNodeInfo.getHostEdgeServer());
			}
		}

		int appStatus = 0;
		appStatus = registrationNodeInfo.getNodeInfo().isRPSInstalled()?ApplicationUtil.setRPSInstalled(appStatus):appStatus;
		appStatus = registrationNodeInfo.getNodeInfo().isD2DInstalled()?ApplicationUtil.setD2DInstalled(appStatus):appStatus;
	    appStatus = registrationNodeInfo.getNodeInfo().isSQLServerInstalled()?ApplicationUtil.setSQLInstalled(appStatus):appStatus;
	    appStatus = registrationNodeInfo.getNodeInfo().isExchangeInstalled()?ApplicationUtil.setExchangeInstalled(appStatus):appStatus;	   
		
	    String nodeUuid = nodeInfo.getD2DUUID();
	    String authUuid = null;
	    
	    if (nodeInfo.isRPSInstalled()) {
	    	try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
	    		connection.connect();
	    		nodeUuid = connection.getService().getRPSServerID();
	    		authUuid = getAuthUuid(connection, context);
	    	}
	    }
	    
	    if(StringUtil.isEmptyOrNull(nodeUuid)){
	    	nodeUuid = UUID.randomUUID().toString();
	    	nodeInfo.setD2DUUID(nodeUuid);
	    }
	    
		int[] output = new int[1];
		rpsNodeDao.as_edge_rps_node_update(gatewayId.getRecordId(), 
				hostId, registrationNodeInfo.getNodeName(), 
				registrationNodeInfo.getNodeDescription(), ip,appStatus, fqdnNames, output);
		
		if (hostId <= 0) {
			hostId = output[0];
		}
		
		this.gatewayService.bindEntity( gatewayId, hostId, EntityType.Node );
		rpsConnectionInfoDao.as_edge_rps_connection_info_update(hostId, nodeInfo.getD2DProtocol().ordinal(), 
				nodeInfo.getD2DPortNumber(), registrationNodeInfo.getUsername(), 
				registrationNodeInfo.getPassword(), nodeInfo.getD2DMajorVersion(), 
				nodeInfo.getD2DMinorVersion(),nodeInfo.getD2DBuildNumber(), nodeInfo.getUpdateVersionNumber(),
				NodeManagedStatus.Unknown.ordinal(),nodeUuid);
		
		if (authUuid != null) {
			rpsConnectionInfoDao.as_edge_rps_connection_info_setAuthUuid(nodeUuid, authUuid);
		}
		
		registrationNodeInfo.setId(hostId);
		RegistrationNodeResult result = new RegistrationNodeResult();
		String[] errorCodes = new String[2];
		errorCodes[0] = tryMarkRPSServerAsManaged(registrationNodeInfo,overwrite, false, false);
		result.setHostID(hostId);
		result.setErrorCodes(errorCodes);
		return result;
	}
	
	@Override
	public RegistrationNodeResult registerRpsNode(boolean failedReadRemoteRegistry,
			NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		return registerRpsNode(failedReadRemoteRegistry, registrationNodeInfo, true);
	}
	
	protected String getIpByName(GatewayId gatewayId, String hostname){
		if(hostname==null)
			return null;
		String ip = "";
		try {
			IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean( IRemoteNativeFacadeFactory.class );
			IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
			ip = nativeFacade.getIpByHostName( hostname );
		} catch (UnknownHostException e) {
			logger.error("getIpByName", e); //$NON-NLS-1$
		}
		return ip;
	}
	
	private RemoteNodeInfo tryConnectRps(ConnectionContext context) throws EdgeServiceFault {
		EdgeD2DRegServiceImpl regService = new EdgeD2DRegServiceImpl();
		EdgeConnectInfo d2dConnectInfo = regService.tryConnectD2D(context);

		RemoteNodeInfo nodeInfoForD2D = new RemoteNodeInfo();
		nodeInfoForD2D.setD2DBuildNumber(d2dConnectInfo.getBuildnumber());
		nodeInfoForD2D.setD2DMajorVersion(d2dConnectInfo.getMajorversion());
		nodeInfoForD2D.setD2DMinorVersion(d2dConnectInfo.getMinorversion());
		nodeInfoForD2D.setD2DUUID(d2dConnectInfo.getUuid());
		nodeInfoForD2D.setHostEdgeServer(d2dConnectInfo.getRhostname());
		nodeInfoForD2D.setUpdateVersionNumber(d2dConnectInfo.getUpdateversionnumber());
		
		return nodeInfoForD2D;
	}
	
	public String tryMarkRPSServerAsManaged(NodeRegistrationInfo registrationNodeInfo, boolean overwrite, boolean isUpdateNode, boolean needToRedeploy) throws EdgeServiceFault{
		logger.debug("tryMarkRPSServerAsManaged(NodeRegistrationInfo) - start"); //$NON-NLS-1$
		
		/*if (isSaaSD2D(host)){
			String errorCode = proccessSaaSD2DNode(host, isUpdateNode);
			if (errorCode!=null)
				return errorCode;
		}*/
		
		if (!registrationNodeInfo.getNodeInfo().isRPSInstalled() || !registrationNodeInfo.isRegisterD2D())
			return null;

		ConnectionContext context = new ConnectionContext(registrationNodeInfo.getD2dProtocol(), registrationNodeInfo.getNodeName(), registrationNodeInfo.getD2dPort());
		context.buildCredential(registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(), "");
		GatewayEntity gateway = gatewayService.getGatewayById(registrationNodeInfo.getGatewayId());
		context.setGateway(gateway);
		
		try{
			regService.UpdateRegInfoToRpsServer(context,registrationNodeInfo.getId(), overwrite);
			
			if(registrationNodeInfo.getId()!=0){
				RPSNodeSettingServiceImpl.syncNodeSetting(registrationNodeInfo);
				rpsConnectionInfoDao.as_edge_rps_connection_info_manage_update(registrationNodeInfo.getId(), NodeManagedStatus.Managed.ordinal());				rpsConnectionInfoDao.as_edge_rps_connection_info_manage_update(registrationNodeInfo.getId(), NodeManagedStatus.Managed.ordinal());
				if (needToRedeploy) {				
					reDeployPlanByRPSId(registrationNodeInfo.getId());
				}
			}
			logger.debug("tryMarkRPSServerAsManaged(NodeRegistrationInfo, EdgeHost) - end"); //$NON-NLS-1$
			
			return null;
		}catch(EdgeServiceFault e){
		
			if(EdgeRpsServiceErrorCode.Node_RPS_Reg_Again.equalsIgnoreCase(e.getFaultInfo().getCode())){//RPS managed successfully
				logger.error("tryMarkRPSServerAsManaged(NodeRegistrationInfo, EdgeHost) Node_D2D_Reg_Again", e);
				if(registrationNodeInfo.getId()!=0){
					if (needToRedeploy) {				
						reDeployPlanByRPSId(registrationNodeInfo.getId());
					}
					rpsConnectionInfoDao.as_edge_rps_connection_info_manage_update(registrationNodeInfo.getId(), NodeManagedStatus.Managed.ordinal());
				}
				return null;
				
			}else { //RPS managed failed
				logger.error("tryMarkRPSServerAsManaged(NodeRegistrationInfo, EdgeHost)", e); //$NON-NLS-1$
				if(registrationNodeInfo.getId()!=0){
					rpsConnectionInfoDao.as_edge_rps_connection_info_manage_update(registrationNodeInfo.getId(), NodeManagedStatus.Unmanaged.ordinal());
				}
		
				if (EdgeRpsServiceErrorCode.Node_RPS_Reg_Duplicate.equalsIgnoreCase(e.getFaultInfo().getCode())){
					String message = e.getMessage();
					String hostEdgeNameOfD2D = message.substring(message.indexOf('^')+1);
					registrationNodeInfo.getNodeInfo().setHostEdgeServer(hostEdgeNameOfD2D);
					return e.getFaultInfo().getCode();
				}else {
					throw e;
				}
				
			}
		}catch(Exception e){
			logger.error("tryMarkRPSServerAsManaged(NodeRegistrationInfo, EdgeHost)", e); //$NON-NLS-1$
			if(registrationNodeInfo.getId()!=0){
				rpsConnectionInfoDao.as_edge_rps_connection_info_manage_update(registrationNodeInfo.getId(), NodeManagedStatus.Unmanaged.ordinal());
			}
			return EdgeRpsServiceErrorCode.Common_Service_General;
		}
	}

	@Override
	public String[] updateRpsNode(boolean failedReadRemoteRegistry,NodeRegistrationInfo registrationNodeInfo, boolean overwrite)
			throws EdgeServiceFault {
		String message = "";
		try{
			String[] errors = updateRpsNodeNoActivityLog(failedReadRemoteRegistry, registrationNodeInfo, overwrite);
			//successful
			if(errors[0]==null && errors[1]==null){
				message = EdgeCMWebServiceMessages.getMessage("updateNodeSuccessful", registrationNodeInfo.getNodeName());
				String updateLog = EdgeCMWebServiceMessages.getMessage("updateRps_Log",message);
				generateLog(Severity.Information, registrationNodeInfo, 
						updateLog, Module.UpdateNode);
			}
			
			//warnning
			if (errors[0] != null) {
				if (EdgeServiceErrorCode.Node_D2D_Reg_Duplicate.equals(errors[0]) ) {
					message = EdgeCMWebServiceMessages.getMessage("failedToManageRpsByAnotherServe", 
							registrationNodeInfo.getNodeName(),registrationNodeInfo.getNodeInfo().getHostEdgeServer());
				}else {
					message = MessageReader.getErrorMessage(errors[0]);
					if( message.equals( EdgeCMWebServiceMessages.getResource( "unknownError" ) ) ) {
						message = EdgeCMWebServiceMessages.getMessage("failedToManageRps", registrationNodeInfo.getNodeName());
						logger.error(" unable to manage Rps node "+registrationNodeInfo.getNodeName() +" , error code is " + errors[0] );
					}
				}
				String updateLog = EdgeCMWebServiceMessages.getMessage("updateRps_Log",message);
				generateLog(Severity.Warning, registrationNodeInfo, updateLog, Module.UpdateNode);
			}
			return errors;
		}catch(Exception exception){
			//Error
			NodeExceptionUtil.generateActivityLogByExceptionForRegInfo(Module.UpdateNode, registrationNodeInfo, "updateRps_Log", exception);
			throw exception;
		}
	}
	
	public String[] updateRpsNodeNoActivityLog(boolean failedReadRemoteRegistry,NodeRegistrationInfo registrationNodeInfo, boolean overwrite)
			throws EdgeServiceFault {
		if(registrationNodeInfo.getId() != 0){
			GatewayEntity gatewayEntity = gatewayService.getGatewayByHostId(registrationNodeInfo.getId());
			registrationNodeInfo.setGatewayId(gatewayEntity.getId());	
		}
		String ip = getIpByName(registrationNodeInfo.getGatewayId(), registrationNodeInfo.getNodeName());
		
		RemoteNodeInfo nodeInfo = registrationNodeInfo.getNodeInfo();
		ConnectionContext context = createConnectionContext(registrationNodeInfo);
		
		if(failedReadRemoteRegistry)
		{
			RemoteNodeInfo d2dRemoteNodeInfo = null;
			if(nodeInfo.isRPSInstalled() /*&& registrationNodeInfo.isRegisterD2D()*/)
			{
				d2dRemoteNodeInfo = tryConnectRps(context);
				if(d2dRemoteNodeInfo == null)
					throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeRpsServiceErrorCode.Node_RPS_Reg_connection_refuse , ""));

				nodeInfo.setD2DMajorVersion(d2dRemoteNodeInfo.getD2DMajorVersion());
				nodeInfo.setD2DMinorVersion(d2dRemoteNodeInfo.getD2DMinorVersion());
				nodeInfo.setD2DUUID(d2dRemoteNodeInfo.getD2DUUID());
				nodeInfo.setD2DBuildNumber(d2dRemoteNodeInfo.getD2DBuildNumber());
				nodeInfo.setUpdateVersionNumber(d2dRemoteNodeInfo.getUpdateVersionNumber());
				nodeInfo.setHostEdgeServer(d2dRemoteNodeInfo.getHostEdgeServer());
			}
		}
		
		int appStatus = 0;
		appStatus = registrationNodeInfo.getNodeInfo().isRPSInstalled()?ApplicationUtil.setRPSInstalled(appStatus):appStatus;
		appStatus = registrationNodeInfo.getNodeInfo().isD2DInstalled()?ApplicationUtil.setD2DInstalled(appStatus):appStatus;
	    appStatus = registrationNodeInfo.getNodeInfo().isSQLServerInstalled()?ApplicationUtil.setSQLInstalled(appStatus):appStatus;
	    appStatus = registrationNodeInfo.getNodeInfo().isExchangeInstalled()?ApplicationUtil.setExchangeInstalled(appStatus):appStatus;	   
	    
	    String nodeUuid = nodeInfo.getD2DUUID();
	    String authUuid = null;
	    
	    if (nodeInfo.isRPSInstalled()) {
	    	try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
	    		connection.connect();
	    		nodeUuid = connection.getService().getRPSServerID();
	    		authUuid = getAuthUuid(connection, context);
	    	}
	    }
		
	    boolean needToRedeploy = false;
	    //defect 763975
	    //int[] nodes = new int[1];
	    //rpsNodeDao.as_edge_rps_node_getIdByHostnameIp(registrationNodeInfo.getId(), registrationNodeInfo.getNodeName(), ip, nodes);
	    if (nodeInfo.isRPSInstalled()) {
	    	List<EdgeRpsNode> lst = new ArrayList<EdgeRpsNode>(); 
	    	rpsNodeDao.as_edge_rps_node_list(registrationNodeInfo.getId(), lst); 
	    	if (lst.size() != 0) {
	    		EdgeRpsNode rpsNode = lst.get(0);
	    		if(!registrationNodeInfo.getUsername().equals(rpsNode.getUsername())){
	    			logger.info("[RpsNodeServiceImpl] updateNode() rps user name changed from "+rpsNode.getUsername()+" to "+registrationNodeInfo.getUsername()
	    					+", so need re deploy plans which have relationship with this RPS: "+registrationNodeInfo.getNodeName());
	    			needToRedeploy = true;
	    		}
	    		if(!registrationNodeInfo.getPassword().equals(rpsNode.getPassword())){
	    			logger.info("[RpsNodeServiceImpl] updateNode() rps password changed, so need re deploy plans which have relationship with this RPS: "+registrationNodeInfo.getNodeName());
	    			needToRedeploy = true;
	    		}
	    		if(registrationNodeInfo.getD2dPort() != rpsNode.getPort()){
	    			logger.info("[RpsNodeServiceImpl] updateNode() rps port changed from "+rpsNode.getPort()+" to "+registrationNodeInfo.getD2dPort()
	    					+", so need re deploy plans which have relationship with this RPS: "+registrationNodeInfo.getNodeName());
	    			needToRedeploy = true;
	    		}
	    		if(registrationNodeInfo.getD2dProtocol().ordinal() != rpsNode.getProtocol()){
	    			logger.info("[RpsNodeServiceImpl] updateNode() rps protocol changed from "+rpsNode.getProtocol()+" to "+registrationNodeInfo.getD2dProtocol()
	    					+", so need re deploy plans which have relationship with this RPS: "+registrationNodeInfo.getNodeName());
	    			needToRedeploy = true;
	    		}
	    		if(!registrationNodeInfo.getNodeName().equals(rpsNode.getNode_name())){
	    			logger.info("[RpsNodeServiceImpl] updateNode() rps node name  changed from "+rpsNode.getProtocol()+" to "+registrationNodeInfo.getD2dProtocol()
	    					+", so need re deploy plans which have relationship with this RPS: "+registrationNodeInfo.getNodeName());
	    			needToRedeploy = true;
	    		}
	    		if(!nodeUuid.equals(rpsNode.getUuid())){
	    			logger.info("[RpsNodeServiceImpl] updateNode() rps uuid  changed from "+rpsNode.getUuid()+" to "+nodeUuid
	    					+", so need re deploy plans which have relationship with this RPS: "+registrationNodeInfo.getNodeName());
	    			needToRedeploy = true;
	    		}
	    	}
	    }
	    
	    List<String> fqdnNameList = new ArrayList<String>();
		if(registrationNodeInfo.getGatewayId() != null &&registrationNodeInfo.getGatewayId().isValid()){
			try {
				IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( registrationNodeInfo.getGatewayId());
				fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(registrationNodeInfo.getNodeName());
			} catch (Exception e) {
				logger.error("[RPSNodeServiceImpl] updateRpsNodeNoActivityLog() get fqdn name failed.",e);
			}
		}
		String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
		
		int[] output = new int[1];
		rpsNodeDao.as_edge_rps_node_update(registrationNodeInfo.getGatewayId().getRecordId(), registrationNodeInfo.getId(), registrationNodeInfo.getNodeName(), registrationNodeInfo.getNodeDescription(), ip,appStatus, fqdnNames, output);
		Protocol d2dProtocol = registrationNodeInfo.getD2dProtocol();
		if (d2dProtocol == null) {
			d2dProtocol = Protocol.UnKnown;
		}
		rpsConnectionInfoDao.as_edge_rps_connection_info_update(registrationNodeInfo.getId(), d2dProtocol.ordinal(), registrationNodeInfo.getD2dPort(), registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(), nodeInfo.getD2DMajorVersion(), nodeInfo.getD2DMinorVersion(),nodeInfo.getD2DBuildNumber(), nodeInfo.getUpdateVersionNumber(), 0, nodeUuid);
		
		if (authUuid != null) {
			rpsConnectionInfoDao.as_edge_rps_connection_info_setAuthUuid(nodeUuid, authUuid);
		}
		
		String[] errorCodes = new String[2];
		
		errorCodes[0] = tryMarkRPSServerAsManaged(registrationNodeInfo, overwrite, true, needToRedeploy);
		if (needToRedeploy) {
			tryUpdateNode(failedReadRemoteRegistry, registrationNodeInfo);
		}
		
		return errorCodes;
	}
	
	private void tryUpdateNode(boolean failedReadRemoteRegistry,NodeRegistrationInfo registrationNodeInfo) {
		NodeServiceImpl nodeService = new NodeServiceImpl();
		List<EdgeConnectInfo> infos = new ArrayList<EdgeConnectInfo>();
		conInfoDao.as_edge_connect_info_list(registrationNodeInfo.getId(), infos);
		int[] hostId = new int[1];
		conInfoDao.as_edge_GetConnInfoByUUID(infos.get(0).getUuid(), hostId, new String[1], new int[1], new int[1]);
		
		if (hostId[0] > 0) {
			NodeRegistrationInfo newRegistrationNodeInfo = registrationNodeInfo; 
			newRegistrationNodeInfo.setId(hostId[0]);
			try {
				nodeService.updateNode(failedReadRemoteRegistry, newRegistrationNodeInfo , false, false);//Should invoke the API which have not print activity log
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void reDeployPlanByRPSId(int rpsNodeId) throws EdgeServiceFault{
		logger.debug("Redeploy plan by RPS node id: " + rpsNodeId + " Start"); //$NON-NLS-1$
		List<Integer> planToBeDeployed = new ArrayList<Integer>();
		//Get all plan
		PolicyManagementServiceImpl policyService = PolicyManagementServiceImpl.getInstance();
		List<PolicyInfo> lstPlans = policyService.getPlanList();
		for (PolicyInfo policyInfo : lstPlans) {
			String planUUID = policyInfo.getPolicyUuid();
			UnifiedPolicy unifiedPolicy = policyService.loadUnifiedPolicyByUuid(planUUID);
			//Check backup task
			if (unifiedPolicy.getBackupConfiguration() != null && unifiedPolicy.getBackupConfiguration().getBackupRpsDestSetting() != null) {
				if (rpsNodeId == unifiedPolicy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId()) {
					logger.debug("Found plan to redeploy plan name " + policyInfo.getPolicyName()); //$NON-NLS-1$
					planToBeDeployed.add(policyInfo.getPolicyId());
					continue;
				}
			}
			//Check replication task
			if (unifiedPolicy.getRpsPolices() != null && unifiedPolicy.getRpsPolices().size() != 0) {
				//Get plan enable status
				List<PolicyInfo> policyList = new ArrayList<PolicyInfo>();
				IEdgePolicyDao edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
				edgePolicyDao.as_edge_plan_getPlanList(policyList);
				DisabledNodes dn = new DisabledNodes();
				for (PolicyInfo pi : policyList) {
					if (unifiedPolicy.getId() == pi.getPolicyId()) {
						if (pi.getEnabled() == PlanEnableStatus.Enable) {
							dn.setDisablePlan(false);
						} else if (pi.getEnabled() == PlanEnableStatus.Disable) {
							dn.setDisablePlan(true);
						}
					}
				}
				for (RPSPolicyWrapper rpsPolicy : unifiedPolicy.getRpsPolices()) {
					if (rpsPolicy.getRpsPolicy().getRpsSettings().getRpsReplicationSettings() != null && rpsPolicy.getRpsPolicy().getRpsSettings().getRpsReplicationSettings().isEnableReplication() && rpsNodeId == rpsPolicy.getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getHostId()) {
						logger.debug("Found plan to redeploy plan name " + policyInfo.getPolicyName()); //$NON-NLS-1$
						planToBeDeployed.add(policyInfo.getPolicyId());
						
						int index = unifiedPolicy.getRpsPolices().indexOf(rpsPolicy);
						if (index == 0) {
							if (unifiedPolicy.getBackupConfiguration() != null && unifiedPolicy.getBackupConfiguration().getBackupRpsDestSetting() != null) {
								RpsSettingTaskDeployment.deployRpsPolicy(unifiedPolicy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost(), rpsPolicy.getRpsPolicy(), dn);
							} else if (unifiedPolicy.getVSphereBackupConfiguration() != null && unifiedPolicy.getVSphereBackupConfiguration().getBackupRpsDestSetting() != null) {
								RpsSettingTaskDeployment.deployRpsPolicy(unifiedPolicy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost(), rpsPolicy.getRpsPolicy(), dn);
							}
						} else if (index > 0) {
							RpsSettingTaskDeployment.deployRpsPolicy(unifiedPolicy.getRpsPolices().get(index - 1).getRpsPolicy().getRpsSettings().getRpsReplicationSettings(), rpsPolicy.getRpsPolicy(), dn);
						}
						
						continue;
					}
				}
			}
			//Check HBBU replication task
			if (unifiedPolicy.getVSphereBackupConfiguration() != null && unifiedPolicy.getVSphereBackupConfiguration().getBackupRpsDestSetting() != null) {
				if (rpsNodeId == unifiedPolicy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId()) {
					logger.debug("Found plan to redeploy plan name " + policyInfo.getPolicyName()); //$NON-NLS-1$
					planToBeDeployed.add(policyInfo.getPolicyId());
					continue;
				}
			}
		}		
		policyService.redeployPolicies(PolicyTypes.Unified, planToBeDeployed);
		logger.debug("Redeploy plan by RPS node id: " + rpsNodeId + " End"); //$NON-NLS-1$
	}
	
	@Override
	public void deleteRpsNodeOnly(int id) throws EdgeServiceFault {
		rpsNodeDao.as_edge_rps_node_delete(id);		
	}

	@Override
	public void deleteRpsNode(int id, boolean keepCurrentSettings)
			throws EdgeServiceFault {
		RpsNode node = null;
		List<EdgeRpsNode> nodeList = new ArrayList<EdgeRpsNode>();
		rpsNodeDao.as_edge_rps_node_list(id, nodeList);
		if(nodeList == null || nodeList.size()==0){
			return;
		}
		checkIsRpsInUse(nodeList.get(0));
		
		node = convertDaoNode2ContractNode(nodeList.get(0));
		List<EdgeRpsConnectionInfo> connInfoLst = new ArrayList<EdgeRpsConnectionInfo>();
		rpsConnectionInfoDao.as_edge_rps_connection_info_list(node.getNode_id(), connInfoLst);
		if(connInfoLst != null && connInfoLst.size()!=0){
			
			RpsConnectionInfo connectionInfo = convertDaoConInfo2ContractConInfo(connInfoLst.get(0));
			unregisterRPS(id,node,connectionInfo);
		}
		//We won't delete policy and data store when deleteing node
//		try{
//			PolicyManagement.getPolicyManager().deletePolicyByNode(id);
//			DataStoreManager.getDataStoreManager().deleteDataStoreByNode(id);
//		}catch(SOAPFaultException e){
//			throw new EdgeServiceFault(EdgeRpsServiceErrorCode.POLICY_RPS_DELETE_FAILED,new EdgeServiceFaultBean(EdgeRpsServiceErrorCode.POLICY_RPS_DELETE_FAILED, "delete fail"), e);
//		}
		
		
		rpsNodeDao.as_edge_rps_node_delete(id);
	}
	
	private void checkIsRpsInUse(EdgeRpsNode node) throws EdgeServiceFault {
		List<PolicyInfo> planInfos =  PolicyManagementServiceImpl.getInstance().getPlanList();
		for( PolicyInfo pi : planInfos ) {
			UnifiedPolicy planDetail = PolicyManagementServiceImpl.getInstance().loadUnifiedPolicyById( pi.getPolicyId() );
			String lastName="";
			if(planDetail.getBackupConfiguration()!=null){
				if(!planDetail.getBackupConfiguration().isD2dOrRPSDestType()){
					lastName=planDetail.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
				}
			}else if(planDetail.getVSphereBackupConfiguration()!=null){
				if(!planDetail.getVSphereBackupConfiguration().isD2dOrRPSDestType()){
					lastName=planDetail.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
				}
			}else if(planDetail.getMspServerReplicationSettings()!=null){
				lastName=planDetail.getMspServerReplicationSettings().getHostName();
			}
			List<RPSPolicyWrapper> rpsPolicies =planDetail.getRpsPolices();
			for( RPSPolicyWrapper rpsPolicy :  rpsPolicies ) {				
				if((lastName).equals(node.getNode_name()))
					throw DataStoreWebUtil
						.generateException(
							EdgeServiceErrorCode.POLICY_RPS_Node_DELETE_FAILED_USED,
							"Failed_PolicyIsInUse",	new Object[] {node.getNode_name() });
				if(rpsPolicy.getRpsPolicy().getRpsSettings().getRpsReplicationSettings()!=null)
					lastName=rpsPolicy.getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getHostName();
			}
		}
	}
	
	private void unregisterRPS(final int id,RpsNode node,RpsConnectionInfo conInfo) {
		try {
			
			if (conInfo.getManage() == NodeManagedStatus.Managed.ordinal()){
				this.regService.RemoveRegInfoFromRpsServer(node,conInfo, false);
			}

		} catch (EdgeServiceFault e){
			if (EdgeRpsServiceErrorCode.Node_RPS_UnReg_Not_Owner.equals(e.getFaultInfo().getCode()))
				rpsConnectionInfoDao.as_edge_rps_connection_info_delete(id);
			//else
				//connectionInfoDao.as_edge_connect_update_managedStatus(id, NodeManagedStatus.Unmanaged.ordinal());
		} catch (Exception e) {
			logger.error("$Thread.run() - exception ignored", e); //$NON-NLS-1$
			//connectionInfoDao.as_edge_connect_update_managedStatus(id, NodeManagedStatus.Unmanaged.ordinal());
		}
	}

	@Override
	public RpsNode getRpsNodeDetailInformation(int hostID) throws EdgeServiceFault {
		List<EdgeRpsNode> nodeList = new ArrayList<EdgeRpsNode>();
		rpsNodeDao.as_edge_rps_node_list(hostID, nodeList);
		if(nodeList.size() == 0){
			return null;
		}
		return convertDaoNode2ContractNode(nodeList.get(0));
	}

	
	public int getNode_Id(int gatewayid, String node_name, String ip){
		int[] output = new int[1];
		rpsNodeDao.as_edge_rps_node_getIdByHostnameIp(gatewayid, node_name, ip, output);
		return output[0];
	}

	@Override
	public void markRpsNodeAsManaged(NodeRegistrationInfo nodeInfo,
			boolean overwrite) throws EdgeServiceFault {
		try{
			markRpsNodeAsManagedNoActivityLog(nodeInfo, overwrite);
			//generate successfull activitylog
			String message = EdgeCMWebServiceMessages.getMessage("manageRpsSuccessful", nodeInfo.getNodeName());
			String updateLog = EdgeCMWebServiceMessages.getMessage("manageRps_Log",message);
			generateLog(Severity.Information, nodeInfo, 
					updateLog, Module.ManageMultipleNodes);
		}catch(Exception e){
			//Error ActivityLog
			NodeExceptionUtil.generateActivityLogByExceptionForRegInfo(Module.ManageMultipleNodes,nodeInfo, "manageRps_Log", e);
			throw e;
		}
	}
	
	public void markRpsNodeAsManagedNoActivityLog(NodeRegistrationInfo nodeInfo,
			boolean overwrite) throws EdgeServiceFault {
		if(nodeInfo.getId() != 0){
			GatewayEntity gatewayEntity = gatewayService.getGatewayByHostId(nodeInfo.getId());
			nodeInfo.setGatewayId(gatewayEntity.getId());	
		}
		String ip = this.getIpByName(nodeInfo.getGatewayId(), nodeInfo.getNodeName());
		//int node_id = this.getNode_Id(nodeInfo.getNodeName(), ip);
		nodeInfo.setId(this.getNode_Id(nodeInfo.getId(), nodeInfo.getNodeName(), ip));
		String errCode = tryMarkRPSServerAsManaged(nodeInfo, overwrite,false, false);
		if (errCode != null) {
			throw EdgeServiceFault.getFault(errCode,
					"Error ocurred during mark RPS Server as managed ");
		}
	}
	
	public void markRpsNodeAsManagedById(int rpsNodeId, boolean overwrite) throws EdgeServiceFault{
		List<EdgeRpsNode> lst = new ArrayList<EdgeRpsNode>(); 
    	rpsNodeDao.as_edge_rps_node_list(rpsNodeId, lst); 
    	
    	EdgeRpsNode rpsNode = lst.get(0);
		NodeRegistrationInfo nodeRegistrationInfo = new NodeRegistrationInfo();
		
		RemoteNodeInfo nodeInfo = new RemoteNodeInfo();
		
		nodeInfo.setD2DInstalled(ApplicationUtil.isD2DInstalled(rpsNode.getAppStatus()));
		nodeInfo.setRPSInstalled(ApplicationUtil.isD2DInstalled(rpsNode.getAppStatus()));
		nodeInfo.setD2DUUID(rpsNode.getUuid());
		nodeRegistrationInfo.setNodeInfo(nodeInfo);
		
		nodeRegistrationInfo.setId(rpsNodeId);
		nodeRegistrationInfo.setRegisterD2D(ApplicationUtil.isD2DInstalled(rpsNode.getAppStatus()));
		nodeRegistrationInfo.setD2dProtocol(Protocol.parse(rpsNode.getProtocol()));
		nodeRegistrationInfo.setNodeName(rpsNode.getNode_name());
		nodeRegistrationInfo.setD2dPort(rpsNode.getPort());
		nodeRegistrationInfo.setUsername(rpsNode.getUsername());
		nodeRegistrationInfo.setPassword(rpsNode.getPassword());
		GatewayEntity gateway = gatewayService.getGatewayByHostId( rpsNodeId );
		nodeRegistrationInfo.setGatewayId(gateway.getId());
		
		markRpsNodeAsManaged(nodeRegistrationInfo, overwrite);
	}
	
	@Override
	public List<RPSSourceNode> importNodeFromRpsServer(
			RpsNode rpsNode) throws EdgeServiceFault {
		if (rpsNode.getNode_id() == 0) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "The RPS node id is invalid.");
		}
		
		ConnectionContext context = new NodeConnectionContextProvider(rpsNode.getNode_id()).create();
		
		String nodeUuid;
		String authUuid;
		List<BackupD2D> list;
		
		try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
    		connection.connect();
    		
    		nodeUuid = connection.getService().getRPSServerID();
    		authUuid = getAuthUuid(connection, context);
    		list = connection.getService().getRegistedClientList();
    	}
		
		List<RPSSourceNode> nodeList = new ArrayList<RPSSourceNode>();
		RPSConverterNode converter = new RPSConverterNode();
		int[] updateResults = new int[2];
		converter.setConverterName(rpsNode.getNode_name());
		converter.setConverterPassword(rpsNode.getPassword());
		converter.setConverterPort(rpsNode.getPort());
		converter.setConverterProtocol(rpsNode.getProtocol() == 1 ? "HTTP" : "HTTPS");
		converter.setConverterUsername(rpsNode.getUsername());
		//insert or update the converter
		int[] id = new int[1];
		int[] insert = new int[1];
		// TODO need to investigate the real implementation
		getVsbDao().as_edge_vsb_converter_cu(0, rpsNode.getNode_id(), rpsNode.getNode_name(), rpsNode.getPort(),
				rpsNode.getProtocol(), rpsNode.getUsername(), rpsNode.getPassword(), nodeUuid,
				authUuid, id, insert);
//		edgeHostDao.as_edge_host_OffsiteVCMConverters_insertOrUpdate(0,rpsNode.getNode_name(),rpsNode.getUsername()
//				,rpsNode.getPassword(),rpsNode.getPort(),Protocol.parse(rpsNode.getProtocol()),rpsuuid,updateResults);
		int converterId = id[0];
		
		//insert or update the rps server
		updateResults = new int[2];
		edgeRpsDao.as_edge_rps_update(rpsNode.getNode_name(), rpsNode.getPort(), rpsNode.getProtocol(), rpsNode.getUsername(), rpsNode.getPassword(),nodeUuid, updateResults);
		int rpsId = updateResults[0];

		for (BackupD2D backupD2D : list) {
			RPSSourceNode sourceNode = new RPSSourceNode();
			sourceNode.setDatastoreName(backupD2D.getDatastoreName());
			sourceNode.setDatastoreUUID(backupD2D.getDatastoreUUID());
			sourceNode.setDesPassword(backupD2D.getDesPassword());
			sourceNode.setDestination(backupD2D.getDestination());
			sourceNode.setDesUsername(backupD2D.getDesUsername());
			sourceNode.setHostname(backupD2D.getHostname());
			sourceNode.setHostUUID(backupD2D.getClientUUID()==null?"":WSJNI.AFDecryptStringEx(backupD2D.getClientUUID()));
			sourceNode.setPolicyUUID(backupD2D.getPolicyUUID());
			sourceNode.setLoginUUID(backupD2D.getLoginUUID()==null?"":WSJNI.AFDecryptStringEx(backupD2D.getLoginUUID()));
			sourceNode.setVM(backupD2D.isVm());
			sourceNode.setConverterNode(converter);
			sourceNode.setReplicationNode(backupD2D.isReplicatedClient());
			Protocol hostProtocol = Protocol.parse(backupD2D.getProtocol());
			sourceNode.setHostProtocal(hostProtocol);
			sourceNode.setHostPort((int)backupD2D.getPort());
			
			int hostId = 0;
			int hostType = HostType.EDGE_NODE_IMPORT_FROM_RPS.getValue();
			if(sourceNode.isReplicationNode()){
				hostType = HostType.EDGE_NODE_IMPORT_FROM_RPS_REPLICA.getValue();
			}
			if(sourceNode.isVM()){
				//check whether it was imported from HBBU
				hostId = edgeRpsDao.getHostIdByVMINSTUUID(sourceNode.getHostUUID());
				if(hostId>0){
					hostId = edgeRpsDao.getHostIdByVMINSTUUIDAndRPSUUID(sourceNode.getHostUUID(),nodeUuid);
				}
				else {
					// have not been imported from HBBU 
					// MSP client , just insert new node
					hostId = 0;
				}
				hostType = (hostType|(HostType.EDGE_NODE_VM_IMPORT_FROM_VSPHERE.getValue()));
			}
			else {
				hostId = edgeRpsDao.getHostIdForNodeImportedFromRPS(sourceNode.getHostUUID(),nodeUuid);
			}
			// Save node
			hostType = HostTypeUtil.setVCMMonitee(hostType);
			updateResults = new int[2];
			edgeRpsDao.as_edge_host_update_ImportFromRPS(hostId, new Date(), sourceNode.getHostname(), 1,
					DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_NONE.getValue(),
					hostType,sourceNode.getHostUUID(),sourceNode.isVM(),updateResults); //Add one node to as_edge_host
			hostId = updateResults[0];
			
			// The node is a virtual node, it doesn't need a gateway, so give it a binding record
			// to avoid special processing in other logics. The value of the gateway ID here is
			// meaningless.
			this.gatewayService.bindEntity( new GatewayId( 1 ), hostId, EntityType.Node );
			
			//Update rps and host and converter  map table
			//if it is vm , vm name is hostname , storage the vmname to map table
			edgeRpsDao.as_edge_node_dest_update(hostId, rpsId,converterId, sourceNode.getDatastoreName(), sourceNode.getDatastoreUUID(), sourceNode.getPolicyUUID(), sourceNode.getDestination(),sourceNode.getHostname());
			// Update connection info table
			conInfoDao.as_edge_connect_info_update(hostId, "", "", sourceNode.getHostUUID(), sourceNode.getHostProtocal().ordinal(), sourceNode.getHostPort(), 0, "", "", "", "", 0);
			
			if (!sourceNode.getHostUUID().isEmpty()) {
				conInfoDao.as_edge_connect_info_setAuthUuid(sourceNode.getHostUUID(), sourceNode.getLoginUUID());
			}
			
			nodeList.add(sourceNode);
		}
		return nodeList;
	}
	
	@Override
	public AddRpsNodesResult importRpsNodes(NodeRegistrationInfo[] nodes, ImportNodeType type) throws EdgeServiceFault {
		AddRpsNodesResult result = new AddRpsNodesResult();
		for (NodeRegistrationInfo node : nodes) {
			List<String> fqdnNameList = new ArrayList<String>();
			if(node.getGatewayId() != null && node.getGatewayId().isValid()){
				try {
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( node.getGatewayId());
					fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(node.getNodeName());
				} catch (Exception e) {
					logger.error("[RPSNodeServiceImpl] importRpsNodes() get fqdn name failed.",e);
				}
			}
			String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
			
			int hostId = getHostIdByName(node.getGatewayId().getRecordId(), node.getNodeName(), null,fqdnNameList);
			
			//check exist by uuid
			if(hostId == 0){
				int[] hostIds = new int[1];
				hostMgrDao.as_edge_host_getHostIdByUuid(node.getNodeInfo().getD2DUUID(), ProtectionType.RPS.getValue(),
						hostIds);
				if(hostIds[0] != 0)
					hostId = hostIds[0];
			}
			
			if(hostId > 0){
				//Not change db node name
				EdgeRpsNode dbNode = RpsNodeUtil.getNodeById(hostId);
				if(dbNode != null && !StringUtil.isEmptyOrNull(dbNode.getNode_name())){
					node.setNodeName(dbNode.getNode_name());
				}
				
				result.getHaveExistedRpsNodeIds().add(hostId);
			}
			
			int[] output = new int[1];
			rpsNodeDao.as_edge_rps_node_update(node.getGatewayId().getRecordId(), hostId, node.getNodeName(), node.getNodeDescription(), null, 0, fqdnNames, output);
			node.setId(output[0]);
			result.getRpsNodes().add(node);
			
			int protocol = node.getD2dProtocol().ordinal();
			int port = node.getD2dPort();
			RemoteNodeInfo nodeInfo = node.getNodeInfo();
			if (nodeInfo != null) {
				protocol = nodeInfo.getD2DProtocol().ordinal();
				port = nodeInfo.getD2DPortNumber();
			}
			rpsConnectionInfoDao.as_edge_rps_connection_info_update(node.getId(), protocol, port,
					node.getUsername(), node.getPassword(), "", "", "", "", 
					NodeManagedStatus.Unknown.ordinal(), "");

			if (node.getGatewayId().isValid() && hostId <= 0) {
				gatewayService.addNode(node.getGatewayId(), node.getId());
			}
			
			//If data store information is not empty, then save the settings to DB
			if(node instanceof NodeRegistrationInfoForRPS){
				List<DataStoreSettingInfo> dataStoreSettings = ((NodeRegistrationInfoForRPS) node).getDataStoreSettings();
				if(dataStoreSettings!=null && !dataStoreSettings.isEmpty()){
					for(DataStoreSettingInfo dataStoreSetting : dataStoreSettings){
						if(dataStoreSetting != null){
							dataStoreSetting.setNode_id(node.getId());
							dataStoreSetting.setDatastore_name(UUID.randomUUID().toString().toLowerCase());
							RpsDataStoreUtil.saveDatabase(dataStoreSetting);
						}
					}	
				}							
			}
		}

		ImportRpsNodesJob job = new ImportRpsNodesJob();
		String jobID = java.util.UUID.randomUUID().toString();
		job.setId(jobID);
		job.importRpsNode(nodes, this);
//		JobMonitor monitor = JobMonitorManager.getInstance().getJobMonitor(jobID, ImportNodesJobMonitor.class);
//		if (monitor == null) {
//			logger.error("Job monitor is null in NodeServiceImpl.");
//			return;
//		}
//
//		synchronized (monitor) {
//			try {
//				job.schedule(job.createJobDetail(nodes, type, this));
////				monitor.wait(5 * 60 * 1000);
//			} catch (SchedulerException e) {
//				logger.error("Failed to schedule job to import RPS nodes.", e);
//				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
//			} finally {
//				JobMonitorManager.getInstance().removeJobMonitor(jobID);
//			}
//		}
		return result;
	}
	
	public void addActivityLogForImportNodes(Severity severity, ImportNodeType type, String message){
		addActivityLogForImportNodes(0, null, severity, type, message);
	}
	
	public void addActivityLogForImportNodes(int gateway, String nodeName, Severity severity, ImportNodeType type, String message){
		LogAddEntity log = new LogAddEntity();
		if(nodeName!=null && !nodeName.isEmpty()){
			int targetHostId = this.getNode_Id(gateway, nodeName, "");
			log.setTargetHostId( targetHostId );
		}
		log.setMessage(message);
		log.setSeverity(severity);

//		if (type == ImportNodeType.AutoDiscovery_AD || type == ImportNodeType.AutoDiscovery_VMWare){
//			log.setMessage(EdgeCMWebServiceMessages.getMessage("importNodes_AutoDiscovery_Log", log.getMessage()));
//		}else if (type == ImportNodeType.File){
//			log.setMessage(EdgeCMWebServiceMessages.getMessage("importNodes_File_Log", log.getMessage()));
//		}
		logService.addUnifiedLog(log);

	}
		
	@Override
	public void deleteDataStoreById(int nodeId, String dedupId) throws EdgeServiceFault {
		DataStoreManager.getDataStoreManager().deleteDataStoreByGuid(nodeId, dedupId);		
	}

	/*Functions for remote deploy: end*/
	
	public IEdgeVSBDao getVsbDao() {
		if (vsbDao == null) {
			vsbDao = DaoFactory.getDao(IEdgeVSBDao.class);
		}
		return vsbDao;
	}
	
	@Override
	public List<NetworkPath> getMappedNetworkPath(int nodeID)
			throws EdgeServiceFault {
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(nodeID)){
			RPSWebServiceClientProxy proxy = conn.getClientProxy();
			
			List<EdgeRpsNode> nodeList = new ArrayList<EdgeRpsNode>();
			rpsNodeDao.as_edge_rps_node_list(nodeID, nodeList);
			if(nodeList == null || nodeList.size()==0){
				return null;
			}
			
			EdgeRpsNode host= nodeList.get(0);
			String userName=host.getUsername();
			if(userName.toLowerCase().startsWith("localhost\\"))
				userName=userName.substring("localhost\\".length());
			NetworkPath[] mappedNetworkPath = proxy.getServiceForCPM().getMappedNetworkPath(userName);
			List<NetworkPath> r=new ArrayList<NetworkPath>();
			for(NetworkPath p:mappedNetworkPath){
				r.add(p);
			}
			return r;
		}
	}
	
	private long generateLog(Severity severity, NodeRegistrationInfo node, String message, Module module) {
		if(StringUtil.isEmptyOrNull(message))
			return 0;
		String nodeName = (node == null ? "":node.getNodeName());
		int nodeId = (node == null ? 0 : node.getId());
		ActivityLog log = new ActivityLog();
		log.setNodeName(nodeName);
		log.setHostId(nodeId);
		log.setModule(module);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(message);
		
		try {
			return logService.addLog(log);
		} catch (Exception e) {
			logger.error(e);
		}
		return 0;
	}

	@Override
	public void startMergeNow(int rpsNodeId, List<ManualMergeItem> mergeItems)
			throws EdgeServiceFault {
		try(RPSConnection conn = EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			RPSWebServiceClientProxy proxy = conn.getClientProxy();
			proxy.getServiceForCPM().startMergeNow(mergeItems);
		} catch(javax.xml.ws.soap.SOAPFaultException e) {
			String errorMessage = e.getFault().getFaultString();			
			if (errorMessage != null && 
					errorMessage.contains("Cannot find dispatch method")) {					
				logger.info("[RPSNodeServiceImpl] startMergeNow catch ERROR(Cannot find dispatch method) to notify user upgrade");	
				throw EdgeServiceFault.getFault( EdgeServiceErrorCode.METHODNOTSUPPORT_RPS,
						new Object[] { rpsNodeId }, "METHODNOTSUPPORT_RPS" );
			}else{
				logger.error("[RPSNodeServiceImpl] startMergeNow failed.", e );
				throw e;
			}
		}
	}
	
	public int getHostIdByName(int gatewayid, String name, String ip, List<String> fqdnNameList){
		int[] output = new int[1];
		rpsNodeDao.as_edge_rps_node_getIdByHostnameIp(gatewayid, name, ip, output);
		int hostId = output[0];
		
		// check fqdn name
		if(hostId <= 0 && fqdnNameList != null){
			//check duplication with fqdn name
			for(String fqdnName : fqdnNameList){
				hostId = getHostIdByFqdnName(gatewayid, fqdnName);
				if (hostId > 0) {
					break;
				}
			}
		}
		return hostId;
	}
	
	public int getHostIdByFqdnName(int gatewayid, String fqdnName) {
		int[] output = new int[1];
		rpsNodeDao.as_edge_rps_node_getIdByFqdnName(gatewayid, fqdnName, output);
		return output[0];
	}
	
	@Override
	public NodeManageResult queryRpsManagedStatus(NodeRegistrationInfo registrationNodeInfo) 
			throws EdgeServiceFault{
		NodeManageResult result = new NodeManageResult();
		ConnectionContext context = new ConnectionContext(registrationNodeInfo.getD2dProtocol(), registrationNodeInfo.getNodeName(), registrationNodeInfo.getD2dPort());
		context.buildCredential(registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(), "");
		GatewayEntity gateway = gatewayService.getGatewayById(registrationNodeInfo.getGatewayId());
		context.setGateway(gateway);
		try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context)))
		{
			connection.connect();
			RPSRegInfo regSrv = connection.getService().getEdgeRpsRegInfo();
			
			if (regSrv == null || regSrv.getRpsAppUUID()==null){
				result.setManagedStatus(NodeManagedStatusByConsole.NotBeManaged);
				return result;
			}
			
			String anotherConsoleName = regSrv.getRpsAppHostName();
					
			if(regSrv.getRpsAppUUID().equalsIgnoreCase(CommonUtil.retrieveCurrentAppUUID())){
				result.setManagedStatus(NodeManagedStatusByConsole.ManagedByCurrentConsle);
				return result;
			}else {
				if(regSrv.getConsoleUrl()!=null){
					anotherConsoleName = ConsoleUrlUtil.getConsoleHostName(regSrv.getConsoleUrl());
					if(anotherConsoleName.equalsIgnoreCase(EdgeCommonUtil.getLocalFqdnName())){
						result.setManagedStatus(NodeManagedStatusByConsole.ManagedByCurrentConsle);
						return result;
					}
				}
			}
			result.setManagedStatus(NodeManagedStatusByConsole.ManagedByAnotherConsole);
			result.setMnanagedConsoleName(anotherConsoleName);
		}
		return result;
	}
	
	@Override
	public void startFilecopyNow(int rpsNodeId, List<ManualFilecopyItem> filecopyitems) throws EdgeServiceFault 
	{
		try(RPSConnection conn = EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			conn.connect();
			
			//Check whether the RPS server is currently being managed by the same console or not.
			RPSRegInfo regSrv = conn.getService().getEdgeRpsRegInfo();
			if(regSrv.getConsoleUrl()!=null && !regSrv.getRpsAppUUID().equalsIgnoreCase(CommonUtil.retrieveCurrentAppUUID())){
				String managedConsoleName = regSrv.getRpsAppHostName();
				//Note: reusing the existing message as the resource strings freezed
				String message = EdgeCMWebServiceMessages.getMessage("failedToManageRpsByAnotherServe", 
						regSrv.getRpsName(),managedConsoleName);
				
				logger.error("startFilecopyNow: It seems the RPS node: "  + regSrv.getRpsName() + " is not managed by the local console: " + EdgeCommonUtil.getLocalFqdnName() + ", but its managed by: " + managedConsoleName);
			
				ActivityLog log = new ActivityLog();
				log.setModule(Module.All);
				log.setSeverity(Severity.Error);
				log.setNodeName(regSrv.getRpsName());
				log.setMessage(message);
				log.setHostId(rpsNodeId);
				logService.addLog(log);
			}
			else{
				RPSWebServiceClientProxy proxy = conn.getClientProxy();
				proxy.getServiceForCPM().startFilecopyNow(filecopyitems);
			}
		} catch(javax.xml.ws.soap.SOAPFaultException e) {
			String errorMessage = e.getFault().getFaultString();			
			if (errorMessage != null && 
					errorMessage.contains("Cannot find dispatch method")) {					
				logger.info("[RPSNodeServiceImpl] startFilecopyNow catch ERROR(Cannot find dispatch method) to notify user upgrade");	
				throw EdgeServiceFault.getFault( EdgeServiceErrorCode.METHODNOTSUPPORT_RPS,
						new Object[] { rpsNodeId }, "METHODNOTSUPPORT_RPS" );
			}else{
				logger.error("[RPSNodeServiceImpl] startFilecopyNow failed.", e );
				throw e;
			}
		}
		
	}

	@Override
	public void startFileArchiveNow(int rpsNodeId,
			List<ManualFilecopyItem> filearchiveItems) throws EdgeServiceFault {
		try(RPSConnection conn = EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			conn.connect();
			
			//Check whether the RPS server is currently being managed by the same console or not.
			RPSRegInfo regSrv = conn.getService().getEdgeRpsRegInfo();
			if(regSrv.getConsoleUrl()!=null && !regSrv.getRpsAppUUID().equalsIgnoreCase(CommonUtil.retrieveCurrentAppUUID())){
				String managedConsoleName = regSrv.getRpsAppHostName();
				//Note: reusing the existing message as the resource strings freezed
				String message = EdgeCMWebServiceMessages.getMessage("failedToManageRpsByAnotherServe", 
						regSrv.getRpsName(),managedConsoleName);
				
				logger.error("startFilecopyNow: It seems the RPS node: "  + regSrv.getRpsName() + " is not managed by the local console: " + EdgeCommonUtil.getLocalFqdnName() + ", but its managed by: " + managedConsoleName);
			
				ActivityLog log = new ActivityLog();
				log.setModule(Module.All);
				log.setSeverity(Severity.Error);
				log.setNodeName(regSrv.getRpsName());
				log.setMessage(message);
				log.setHostId(rpsNodeId);
				logService.addLog(log);
			}
			else{
				RPSWebServiceClientProxy proxy = conn.getClientProxy();
				proxy.getServiceForCPM().startFileArchiveNow(filearchiveItems);
			}
		} catch(javax.xml.ws.soap.SOAPFaultException e) {
			String errorMessage = e.getFault().getFaultString();			
			if (errorMessage != null && 
					errorMessage.contains("Cannot find dispatch method")) {					
				logger.info("[RPSNodeServiceImpl] startFileArchiveNow catch ERROR(Cannot find dispatch method) to notify user upgrade");	
				throw EdgeServiceFault.getFault( EdgeServiceErrorCode.METHODNOTSUPPORT_RPS,
						new Object[] { rpsNodeId }, "METHODNOTSUPPORT_RPS" );
			}else{
				logger.error("[RPSNodeServiceImpl] startFileArchiveNow failed.", e );
				throw e;
			}
		}
		
	}
}
