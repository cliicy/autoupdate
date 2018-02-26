package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.common.udpapplication.ConsoleApplication;
import com.ca.arcserve.edge.app.base.common.udpapplication.UDPApplication;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult.NodeManagedStatusByConsole;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeGatewayBean;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.DiscoveryService;
import com.ca.arcserve.edge.app.rps.webservice.node.RPSNodeServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.serviceexception.EdgeRpsServiceErrorCode;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;

public class ImportLocalAgentAndServer implements Runnable{
	private static int RETRYCOUNT = 3;
	private static Logger logger = Logger.getLogger( ImportLocalAgentAndServer.class );
	private NodeServiceImpl nodeService;
	private RPSNodeServiceImpl rpsService;
	private IEdgeHostMgrDao hostDao;
	private EdgeWebServiceImpl edgeService;
	private IEdgeConnectInfoDao connectInfoDao;
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private EdgeGatewayBean gatewayService = new EdgeGatewayBean();
	private GatewayEntity localGateway = null;
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	
	private long checkConsoleStateTimeOut = 10 * 60 * 1000; // 10 minutes
	
	public ImportLocalAgentAndServer() {
		edgeService = new EdgeWebServiceImpl();
		edgeService.setLocalCheckSession(true);
		nodeService = new NodeServiceImpl(edgeService);
		rpsService = new RPSNodeServiceImpl();
		hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	}
	
	@Override
	public void run() {
		try {
			//Should check console state fistly
			long startTime = System.currentTimeMillis();
			while(true){
				ConsoleApplication consoleApp = (ConsoleApplication) UDPApplication.getInstance();
				if(consoleApp.isReady()){
					break;
				}else{
					if(System.currentTimeMillis() - startTime > checkConsoleStateTimeOut){
						logger.info("[ImportLocalAgentAndServer] check console state time out, console appliacation is not ready. time out value is: "+checkConsoleStateTimeOut);
						return;
					}
					long sleep = 30 * 1000 ;
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
			
			// Begin import local agent and server
			//String localHostName = CommonUtil.getLocalHost().toLowerCase();
			String localHostName = EdgeCommonUtil.getLocalFqdnName();
			
			EdgeAccount edgeAccount = new EdgeAccount();
			WSJNI.getEdgeAccount( edgeAccount );
			String userName = edgeAccount.getUserName();
			String pwd = edgeAccount.getPassword();
			String domain = edgeAccount.getDomain();
			NodeRegistrationInfo node = getNodeRegistrationInfo(localHostName, userName,domain, pwd);
			//set Local gateway
			localGateway = gatewayService.getLocalGateway();
			if(localGateway == null){
				logger.error("[ImportLocalAgentAndServer] can't get local gateway. so can't import local agent and RPS server.");
				return;
			}
			node.setGatewayId(localGateway.getId());
			RemoteNodeInfo nodeInfo = getRemoteNodeInfo(node, userName, domain, pwd);
			if(nodeInfo == null){
				logger.error("[ImportLocalAgentAndServer] can't get the node info from registry. so can't import local agent and RPS server.");
				return;
			}
			node.setUsername(getUserNameWithDomin(userName,domain));
			node.setNodeName(localHostName);
			node.setPassword(pwd);
			node.setD2dProtocol(nodeInfo.getD2DProtocol());
			node.setD2dPort(nodeInfo.getD2DPortNumber());
			node.setNodeInfo(nodeInfo);
			
			if(nodeInfo.isARCserveBackInstalled()){
				node.setRegisterARCserveBackup(true);
				node.setAbAuthMode(ABFuncAuthMode.WINDOWS);
				node.setCarootUsername(getUserNameWithDomin(userName,domain));
				node.setCarootPassword(pwd);
				node.setArcserveProtocol(nodeInfo.getARCserveProtocol());
				node.setArcservePort(nodeInfo.getARCservePortNumber());
			}
			
			if(nodeInfo.isD2DInstalled()){
				String agentUUID = connectAgentWebService(node); //connect webservice , and wait until the web service can be accessed
				if(!StringUtil.isEmptyOrNull(agentUUID)){
					/*int[] agentHostId = new int[1];
					hostDao.as_edge_host_getHostIdByUuid(agentUUID, ProtectionType.WIN_D2D.getValue(), agentHostId);
					if(agentHostId[0] > 0){ //The node exist , then update the node
						node.setId(agentHostId[0]);
						autoUpdateAgent(node,localIp);
					}
					else if (!AgentHaveBeenAdded()) {
						autoAddAgent(node,agentUUID);
					}*/
					
					if (!AgentHaveBeenAdded()) {
						autoAddAgent(node,agentUUID);
					}else {
						int[] agentHostId = new int[1];
						hostDao.as_edge_host_getHostIdByUuid(agentUUID, ProtectionType.WIN_D2D.getValue(), agentHostId);
						if(agentHostId[0] > 0){ //The node exist , then update the node
							node.setId(agentHostId[0]);
							updateVersion(node);
						}
					}
				}
			}
			
			if(nodeInfo.isRPSInstalled()){
				String rpsUUID = connectRpsWebService(node); //connect webservice , and wait until the web service can be accessed
				if(!StringUtil.isEmptyOrNull(rpsUUID)){
					/*int[] rpsHostId = new int[1];
					hostDao.as_edge_host_getHostIdByUuid(rpsUUID, ProtectionType.RPS.getValue(), rpsHostId);
					if(rpsHostId[0] > 0){ //The node exist , then update the node
						node.setId(rpsHostId[0]);
						autoUpdateRpsServer(node,rpsUUID);
					}
					else if (!RpsServerHaveBeenAdded()) {
						autoAddRpsServer(node,rpsUUID);
					}*/
					
					if (!RpsServerHaveBeenAdded()) {
						autoAddRpsServer(node,rpsUUID);
					}else {
						int[] rpsHostId = new int[1];
						hostDao.as_edge_host_getHostIdByUuid(rpsUUID, ProtectionType.RPS.getValue(), rpsHostId);
						if(rpsHostId[0] > 0){ //The node exist , then update the node
							node.setId(rpsHostId[0]);
							updateVersion(node);
						}
					}
				}
			}
		}catch (Exception unkownException){
			logger.error("ImportLocalAgentAndServer failed.", unkownException);
			return;
		}
	}
	
	private NodeRegistrationInfo getNodeRegistrationInfo(String name, String userName,String domain, String password){
		NodeRegistrationInfo node = new NodeRegistrationInfo();
		node.setId(0);
		node.setNodeName(name);
		String nodeUserName = "";
		if(StringUtil.isEmptyOrNull(domain)||domain.equalsIgnoreCase(".")){
			nodeUserName = userName;
		}else {
			nodeUserName = domain+"\\"+userName;
		}
		node.setUsername(nodeUserName);
		node.setPassword(password);
		node.setRegisterD2D(true);
		return node;
	}
	
	private RemoteNodeInfo getRemoteNodeInfo(NodeRegistrationInfo node,String edgeUserName,String edgeDomain,String edgePassword) {
		try {
			return DiscoveryService.getInstance().scanRemoteNode(
				node.getGatewayId(), edgeUserName, edgeDomain, edgePassword,
				node.getNodeName(), node.getUsername(), node.getPassword());
		} catch (Exception e) {
			logger.info("[ImportLocalAgentAndServer]: Scan remote registry failed, error message = " + e.getMessage());
			
			try {
				// Try connect to web service with http
				return nodeService.tryConnectD2D(node.getGatewayId(),
						"HTTP", node.getNodeName(), 8014, node.getUsername(), node.getPassword());
			} catch (Exception e1) {
				try {
					// Try connect to web service with https
					return nodeService.tryConnectD2D(node.getGatewayId(),
							"HTTPS", node.getNodeName(), 8014, node.getUsername(), node.getPassword());
				} catch (Exception e3) {
				}
			}
		}
		return null;
	}
	
	private ConnectionContext createConnectionContext(NodeRegistrationInfo node) {
		String protocol = node.getD2dProtocol() == Protocol.Https ? "https" : "http";
		ConnectionContext context = new ConnectionContext(protocol, node.getNodeName(), node.getD2dPort());
		context.buildCredential(node.getUsername(), node.getPassword(), "");
		if(localGateway == null){
			logger.error("[ImportLocalAgentAndServer] can't get local gateway. so can't import local agent and RPS server.");
			return null;
		}
		context.setGateway(localGateway);
		return context;
	}
	
	private String connectAgentWebService(NodeRegistrationInfo node){
		RETRYCOUNT = 3;
		String uuid = null;
		
		ConnectionContext context = createConnectionContext(node);
		if(context == null){
			return null;
		}
		
		while (RETRYCOUNT > 0) {
			try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
				connection.connect();
				uuid = connection.getNodeUuid();
				if(!StringUtil.isEmptyOrNull(uuid)){
					logger.info("[ImportLocalAgentAndServer]: Connect agent webservice successfully.");
					RETRYCOUNT = 0;
					return uuid;
				}
			} catch (Exception e) {
				logger.warn("[ImportLocalAgentAndServer]: Connect agent webservice failed, the retry count is :"+String.valueOf(4-RETRYCOUNT));
			}
			try {
				Thread.sleep(50000);//50s
			}catch (InterruptedException e){
				throw new RuntimeException(e);
			} catch (Exception e) {
				logger.warn("[ImportLocalAgentAndServer]: Thread exception.");
			}
			RETRYCOUNT--;
		}
		return uuid;
	}
	
	private String connectRpsWebService(NodeRegistrationInfo node){
		RETRYCOUNT = 3;
		String uuid = null;
		
		ConnectionContext context = createConnectionContext(node);
		if(context == null){
			return null;
		}
		
		while (RETRYCOUNT > 0) {
			try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
				connection.connect();
				uuid = connection.getService().getRPSServerID();
				if(!StringUtil.isEmptyOrNull(uuid)){
					logger.info("[ImportLocalAgentAndwerver]: Connect rps server webservice successfully.");
					RETRYCOUNT = 0;
					return uuid;
				}
			} catch (Exception e) {
				logger.warn("[ImportLocalAgentAndServer]: Connect agent webservice failed, the retry count is :"+String.valueOf(4-RETRYCOUNT));
			}
			try {
				Thread.sleep(30000);//30s
			} catch (InterruptedException e){
				throw new RuntimeException(e);
			} catch (Exception e) {
				logger.warn("[ImportLocalAgentAndServer]: Thread exception.");
			}
			RETRYCOUNT--;
		}
		return uuid;
	}
	
	private void updateVersion(NodeRegistrationInfo nodeRegistrationInfo){
		RemoteNodeInfo nodeInfo = nodeRegistrationInfo.getNodeInfo();
		connectInfoDao.as_edge_connect_info_update_version(nodeRegistrationInfo.getId(),nodeInfo.getD2DMajorVersion(),nodeInfo.getD2DMinorVersion(),
				nodeInfo.getUpdateVersionNumber(),nodeInfo.getD2DBuildNumber());
		logger.info("[ImportLocalAgentAndServer]: updateVersion(), for node: "+
				nodeRegistrationInfo.getId()+"_"+nodeRegistrationInfo.getNodeName()+" Vserion is: "+nodeInfo.getD2DMajorVersion()
				+"."+nodeInfo.getD2DMinorVersion()+"."+nodeInfo.getD2DBuildNumber()+"."+nodeInfo.getUpdateVersionNumber());
	}
	
	private void autoAddAgent(NodeRegistrationInfo node,String agentUUID) {
		try {
			//Defect 755905, not to add when found that the node have been managed by other console
			RegistrationNodeResult agentResult = null;
			NodeManageResult manageResult = nodeService.queryNodeManagedStatus(node);
			if(NodeManagedStatusByConsole.ManagedByAnotherConsole == manageResult.getManagedStatus()){
				logger.info("[ImportLocalAgentAndServer]: Can't add local agent, because the node have been managed by other console: "+manageResult.getMnanagedConsoleName());
				return;
			}else {
				agentResult = nodeService.registerNode(false, node, true);
			}
			if(agentResult.getErrorCodes()[0]==null){
				logger.info("[ImportLocalAgentAndServer]: Automatically add the local agent successfully");
				setAutoAddedSuccessful(WindowsRegistry.VALUE_AUTO_ADDED_LOCAL_AGENT);
				return;
			}
			if(agentResult.getErrorCodes()[0]!=null && EdgeServiceErrorCode.Node_D2D_Reg_Duplicate.equalsIgnoreCase(agentResult.getErrorCodes()[0])){
//				int[] agentHostId = new int[1];
//				hostDao.as_edge_host_getHostIdByUuid(agentUUID, ProtectionType.WIN_D2D.getValue(), agentHostId);
//				if(agentHostId!=null && agentHostId[0]>0)//If the agent is managed by other edge , then roback the adding.
//					nodeService.rollbackAddedD2DNode(agentHostId[0]);
				setAutoAddedSuccessful(WindowsRegistry.VALUE_AUTO_ADDED_LOCAL_AGENT);
				logger.warn("[ImportLocalAgentAndServer]: Automatically add the local agent succeed.");
				return;
			}
			logger.error("[ImportLocalAgentAndServer]: Automatically add the local agent failed, the error code is: "+agentResult.getErrorCodes()[0]);
		}catch (EdgeServiceFault eFault){
			if(EdgeServiceErrorCode.Node_AlreadyExist == eFault.getFaultInfo().getCode()){
				logger.info("[ImportLocalAgentAndServer]: The local agent have been exist in console.");
			}else {
				logger.error("[ImportLocalAgentAndServer]: Automatically add the local agent failed becasue the exception:",eFault);
			}
		}catch (Exception e) {
			logger.error("[ImportLocalAgentAndServer]: Automatically add the local agent failed becasue the exception:",e);
		} finally{
			EdgeHost edgeHost = nodeService.populateEdgeHost(node);
			//check exists
//			List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(edgeHost.getRhostname());
			List<String> fqdnNameList = new ArrayList<String>();
			if(node.getGatewayId() != null && node.getGatewayId().isValid()){
				try {
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( node.getGatewayId());
					fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(edgeHost.getRhostname());
				} catch (Exception e) {
					logger.error("[ImportLocalAgentAndServer]: autoAddAgent() get fqdn name failed.",e);
				}
			}
			
			if (nodeService.getHostIdByName(node.getGatewayId().getRecordId(), edgeHost.getRhostname(), edgeHost.getIpaddress(),fqdnNameList,1)>0){
				setAutoAddedSuccessful(WindowsRegistry.VALUE_AUTO_ADDED_LOCAL_AGENT);
			}
			logger.info("[ImportLocalAgentAndServer]: Automatically add the local agent finished.");
		}
	}
	
	private void autoAddRpsServer(NodeRegistrationInfo node , String rpsUUID ) {
		try {
			NodeManageResult result = rpsService.queryRpsManagedStatus(node);
			if(result!=null && result.getManagedStatus() == NodeManagedStatusByConsole.ManagedByAnotherConsole){
				logger.info("[ImportLocalAgentAndServer]: Can't add local Rps server, because the node have been managed by other console: "+result.getMnanagedConsoleName());
				return;
			}
			RegistrationNodeResult serveResult = rpsService.registerRpsNode(false, node,false);
			if(serveResult.getErrorCodes()[0] == null){
				logger.info("[ImportLocalAgentAndServer]:  Automatically add local Rps server successful ");
				setAutoAddedSuccessful(WindowsRegistry.VALUE_AUTO_ADDED_LOCAL_RPS);
				return;
			}
			if(serveResult.getErrorCodes()[0]!=null && EdgeRpsServiceErrorCode.Node_RPS_Reg_Duplicate.equalsIgnoreCase(serveResult.getErrorCodes()[0])){
				int[] rpsHostId = new int[1];
				hostDao.as_edge_host_getHostIdByUuid(rpsUUID, ProtectionType.RPS.getValue(), rpsHostId);
				if(rpsHostId!=null && rpsHostId[0]>0){
					rpsService.deleteRpsNode(rpsHostId[0], false);//If the node have been managed by other edge , then roballack the adding
					logger.warn("[ImportLocalAgentAndServer]: Automatically add local rps server failed , because the node have been managed by other edge.");
					setAutoAddedSuccessful(WindowsRegistry.VALUE_AUTO_ADDED_LOCAL_RPS);
					return;
				}
			}
			logger.error("[ImportLocalAgentAndServer]: Automatically add local rps server failed, the error code is: "+serveResult.getErrorCodes()[0]);
		} catch (Exception e) {
			logger.error("[ImportLocalAgentAndServer]: Automatically add rps server failed because the exception: ",e);
		}
	}
	
	private boolean AgentHaveBeenAdded(){
		return haveBeenAdded(WindowsRegistry.VALUE_AUTO_ADDED_LOCAL_AGENT);
	}
	
	private boolean RpsServerHaveBeenAdded(){
		return haveBeenAdded(WindowsRegistry.VALUE_AUTO_ADDED_LOCAL_RPS);
	}
	
	private boolean haveBeenAdded(String registryKey){
		String autoAddedFlag = CommonUtil.getApplicationExtentionKey(registryKey);
		if(StringUtil.isEmptyOrNull(autoAddedFlag)|| autoAddedFlag.equals("0"))
			return false;
		return true;
	}
	
	private void setAutoAddedSuccessful(String registryKey) {
		CommonUtil.setApplicationExtentionKey(registryKey, "1");
	}
	
	private String getUserNameWithDomin(String userName, String domain){
		if(StringUtil.isEmptyOrNull(domain)||domain.equals(".")||StringUtil.isEmptyOrNull(userName))
			return userName;
		return domain+"\\"+userName;
	}
}
