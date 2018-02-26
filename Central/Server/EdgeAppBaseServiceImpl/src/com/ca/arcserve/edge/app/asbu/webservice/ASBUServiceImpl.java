package com.ca.arcserve.edge.app.asbu.webservice;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.arcserve.asbu.webservice.archive2tape.udp.ASBUInfo;
import com.arcserve.asbu.webservice.archive2tape.udp.ASBUStatus;
import com.ca.arcserve.edge.app.asbu.dao.IASBUDao;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.ASBUConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.asbuintegration.ASBUDestinationManager;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUAuthenticationType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUDeviceInformation;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaGroupInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaGroupType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaPool;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaPoolSet;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBURegularGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUServerClass;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUServerInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUServerStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUServerStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUSyncResult;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUSyncResultFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.DeleteASBUBackupServerResult;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.DeleteASBUBackupServerResult.DeleteASBUBackupServerReturnCode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.EdgeASBUServer;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.MediaPoolType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlanDestinationType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.asbu.webservice.IArchiveToTapeService;
import com.ca.asbu.webservice.data.AsbuServerStatus;
import com.ca.asbu.webservice.data.archive2tape.ASBUGroupInfo;
import com.ca.asbu.webservice.data.archive2tape.mediapool.ArchiveJobMediaPool;
import com.ca.asbu.webservice.data.archive2tape.mediapool.ArchiveJobMediaPoolSet;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

public class ASBUServiceImpl implements IASBUService{
	private static final Logger logger = Logger.getLogger(ASBUServiceImpl.class);
	private IASBUDao asbuDao = DaoFactory.getDao(IASBUDao.class);
	private IEdgePolicyDao planDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private IEdgeHostMgrDao hostManagerDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
    //String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
	
	EdgeWebServiceImpl serviceImpl;
	public ASBUServiceImpl(EdgeWebServiceImpl serviceImpl){
		this.serviceImpl = serviceImpl;
	}
	private IActivityLogService logService = new ActivityLogServiceImpl();
	public ASBUServiceImpl(){}
	
	public List<com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo> getServersFromAsbu(final ArcserveConnectInfo connectInfo, final String hostNameOrIp) throws EdgeServiceFault
	{
		if (connectInfo == null){
			logger.error("conncetInfo must not be null");
		}
		if (StringUtil.isEmptyOrNull(hostNameOrIp)){
			logger.error("host name must not be null");
		}
		if(connectInfo.getPort() < 1 || connectInfo.getPort() > 65535){
			logger.error("invalid port number");
		}
		if (StringUtil.isEmptyOrNull(connectInfo.getCauser())){
			logger.error("username must not be null");
		}
		if(logger.isDebugEnabled()){
			logger.debug("connect info include properties are: \n");
			logger.debug("primary host name = "+hostNameOrIp);
			logger.debug("primary protocol = "+connectInfo.getProtocol());
			logger.debug("primary port = "+connectInfo.getPort());
			logger.debug("ASBU username = "+connectInfo.getCauser());
			logger.debug("ASBU auth mode = "+connectInfo.getAuthmode());
			if(connectInfo.getGatewayEntity() != null){
				logger.debug(connectInfo.getGatewayEntity().getId());
			}
		}

		if (connectInfo.isUpdate())
		{
			createASBUActivityLog(hostNameOrIp, EdgeCMWebServiceMessages.getMessage("asbu_server_start_update", hostNameOrIp), Severity.Information);
		} else {
			createASBUActivityLog(hostNameOrIp, EdgeCMWebServiceMessages.getMessage("asbu_server_start_add", hostNameOrIp), Severity.Information);
		}
		
		IArchiveToTapeService archiveToTapeService = null;
		logger.debug("start get archive service");
		ConnectionContext context = new ConnectionContext();
		context.setHost(hostNameOrIp);
		context.setDomain(hostNameOrIp);
		context.setProtocol(connectInfo.getProtocol() == Protocol.Http ? "http" : "https");
		context.setPort(connectInfo.getPort());
		context.setAuthenticationType(connectInfo.getAuthmode() == ABFuncAuthMode.AR_CSERVE ? ASBUAuthenticationType.ARCSERVE_BACKUP.getValue() : ASBUAuthenticationType.WINDOWS.getValue());
		context.setUsername(connectInfo.getCauser());
		context.setPassword(connectInfo.getCapasswd());
		context.setGateway(connectInfo.getGatewayEntity());
		if(logger.isDebugEnabled()){
			logger.debug("ASBU connection context are: " + context);
		}
		try(ASBUConnection connection = connectionFactory.createASBUConnection(new DefaultConnectionContextProvider(context))){
			archiveToTapeService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connection);
			final String edgeHostName = getEdgeHostName();
			final String edgeProtocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
			final int edgePort = EdgeCommonUtil.getEdgeWebServicePort();
			final String edgeWSDL = com.ca.arcserve.edge.app.base.webservice.WebServiceFactory.getGateWayWSDL(edgeHostName, edgePort, edgeProtocol);
			final String edgeUUID = CommonUtil.retrieveCurrentAppUUID();
			logger.debug("start register");
			logger.info("edgeHostName = "+edgeHostName);
			logger.info("edgeProtocol = "+edgeProtocol);
			logger.info("edgePort = "+edgePort);
			logger.info("edgeWSDL = "+edgeWSDL);
			logger.info("edgeUUID = "+edgeUUID);
			if(!connectInfo.isForceRegister())
			{
				archiveToTapeService.register(edgeUUID, "1", edgeHostName, edgeWSDL, Locale.getDefault().toString(), false, hostNameOrIp);
		
			}
			logger.debug("get archive service successful");
			if(archiveToTapeService!=null){
				return archiveToTapeService.getServerList();
			}
		}
		return null;
	}
	
	public ASBUSyncResult validate(final ArcserveConnectInfo connectInfo, final String hostNameOrIp, List<com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo> serversFromAsbu) throws EdgeServiceFault
	{
		List<IntegerId> ids = new ArrayList<IntegerId>();
		asbuDao.findDomainIdByHostnameAndGatewayId(connectInfo.getGatewayEntity().getId().getRecordId(), hostNameOrIp, ids);
		if(ids != null && ids.size() > 0){
			List<ASBUServerInfo> oldServers = getASBUServerListWithoutGroup(connectInfo.getGatewayEntity().getId(), ids.get(0).getId());
			//deal with server class change
			ASBUServerInfo oldPrimaryServer = null;
			com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo newPrimaryServer = null;
			boolean isHasPlanReferencePerServer = false;
			for(ASBUServerInfo oldServer : oldServers){
				if(oldServer.getServerClass() == ASBUServerClass.Primary){
					oldPrimaryServer = oldServer;
				}
				int planCount = getASBUPlanCount(oldServer.getServerId(), "");
				if(planCount > 0){
					isHasPlanReferencePerServer = true; 
				}
			}
			for(com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo newServer : serversFromAsbu){
				if(newServer.getServerClass() == com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo.SERVER_CLASS_PRIMARY){
					newPrimaryServer = newServer;
					break;
				}
			}
			if(oldPrimaryServer != null && oldPrimaryServer.getHostName().contains(".")){
				if(!oldPrimaryServer.getHostName().matches(ipv4Pattern)){
					String hostname = oldPrimaryServer.getHostName().substring(0,oldPrimaryServer.getHostName().indexOf("."));
					oldPrimaryServer.setHostName(hostname);
				}
			}
			if(oldPrimaryServer != null && newPrimaryServer != null && !(oldPrimaryServer.getHostName().equalsIgnoreCase(newPrimaryServer.getServerName()) || oldPrimaryServer.getHostName().equalsIgnoreCase(newPrimaryServer.getServerIP()))){//Primary server role is changed
				//check whether has plan used servers under this domain
				if(isHasPlanReferencePerServer){//if yes pop up message tell user
					List<UnifiedPolicy> plans = new ArrayList<>();
					planDao.getPlanNamesByASBUDomainId(oldPrimaryServer.getDomainId(), plans);
					if(CollectionUtils.isNotEmpty(plans)){
						String[] planNames = new String[plans.size()];
						int i = 0;
						for(UnifiedPolicy plan : plans){
							planNames[i++] = plan.getName();
						}
						return ASBUSyncResultFactory.createPrimaryRoleChangedAndHasPlanUseServerResult(oldPrimaryServer.getHostName(), planNames);
					}
				}else{//if not, delete all server and pop up message tell user
					deleteASBUDomain(oldPrimaryServer.getDomainId());
					return ASBUSyncResultFactory.createPrimaryRoleChangedAndNoPlanUseServerResult(oldPrimaryServer.getDomainName());
				}
			}
		}
		return null;
	}
	
	public ASBUSyncResult operateDB(final ArcserveConnectInfo connectInfo, final String hostNameOrIp,	List<com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo> serversFromAsbu, List<ASBUServerInfo> deletedServers) throws EdgeServiceFault {
		int domainId = 0;
		List<ASBUServerInfo> serverInfoList = new ArrayList<>();
		for (com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo server : serversFromAsbu) {
			ASBUServerInfo serverInfo = new ASBUServerInfo();
			serverInfo.setConnectInfo(connectInfo);
			if (hostNameOrIp.equalsIgnoreCase(server.getServerName())
					|| hostNameOrIp.equalsIgnoreCase(server.getServerIP())) {
				serverInfo.setHostName(hostNameOrIp);
			} else {
				serverInfo.setHostName(server.getServerName());
			}
			if(hostNameOrIp != null && hostNameOrIp.contains(".")){
				if(!hostNameOrIp.matches(ipv4Pattern)){//fqdn name
					String hostname = hostNameOrIp.substring(0,hostNameOrIp.indexOf("."));
					if(hostname != null && hostname.equalsIgnoreCase(server.getServerName())){
						serverInfo.setHostName(hostNameOrIp);
					}
				}
			}
			serverInfo.setServerClass(ASBUServerClass.fromValue((int) server
					.getServerClass()));
			serverInfo.setStatus(ASBUServerStatus.UNAVALIABLE);
			serverInfoList.add(serverInfo);
			List<IntegerId> idList = new ArrayList<IntegerId>();
			asbuDao.findASBUDomain(hostNameOrIp, connectInfo.getGatewayEntity()
					.getId().getRecordId(), idList);
			if (idList.size() == 0) {// 0 = create operate
				// only deal with create server
				if (server.getServerClass() == ASBUServerClass.Member
						.getValue()
						&& server.getServerName()
								.equalsIgnoreCase(hostNameOrIp)) {
					EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
							EdgeServiceErrorCode.ASBU_MemberServerCannotUse,
							EdgeCMWebServiceMessages
									.getResource("asbu_member_server_cannot_used"),
							FaultType.ASBU);
					throw new EdgeServiceFault(
							EdgeCMWebServiceMessages
									.getResource("asbu_member_server_cannot_used"),
							bean);
				}
				logger.debug("db has not this asbu server, insert domain");
				asbuDao.insertASBUDomain(hostNameOrIp, idList);
			}
			domainId = idList.get(0).getId();
			logger.debug("domain id is " + domainId);
			int[] hostIds = new int[1];
			int hostId = 0;
			// todo need modify
			hostManagerDao.getIdByHostnameAndProtectionType(
					serverInfo.getHostName(),
					ProtectionType.ASBUServer.getValue(), hostIds);
			if (hostIds[0] != 0) {
				logger.debug("db has this host, id is " + hostIds[0]);
				hostId = hostIds[0];
				serverInfo.setServerId(hostId);
				logger.debug("update host info");
				updateOrInsertHostInfoAndConnectInfo(serverInfo);
				asbuDao.updateASBUServerExtInfo(hostId, serverInfo
						.getServerClass().getValue(), domainId, serverInfo
						.getStatus().ordinal());
			} else {
				logger.debug("db has not this host");
				hostId = updateOrInsertHostInfoAndConnectInfo(serverInfo);
				logger.debug("insert host info");
				asbuDao.insertASBUServerExtInfo(hostId, serverInfo
						.getServerClass().getValue(), domainId, serverInfo
						.getStatus().ordinal());
			}
			if (connectInfo.getGatewayEntity() != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("mapping gateway and host relationship, gateway id is "
							+ connectInfo.getGatewayEntity().getId()
							+ " host id is " + hostId);
				}
				gatewayService.addNode(connectInfo.getGatewayEntity().getId(),
						hostId);
			}
		}
		try {
			DaoFactory.beginTrans();
			List<ASBUServerInfo> dbServers = getASBUServerListWithoutGroup(
					connectInfo.getGatewayEntity().getId(), domainId);
			if (CollectionUtils.isNotEmpty(dbServers)) {
				logger.debug("default set all server status deleted");
				for (ASBUServerInfo serverInfo : dbServers) {
					serverInfo.setStatus(ASBUServerStatus.DELETED);
				}
				logger.debug("start mark normal server");
				for (ASBUServerInfo serverInfo : dbServers) {
					for (com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo server : serversFromAsbu) {
						if (serverInfo.getHostName().equalsIgnoreCase(
								server.getServerName())
								|| serverInfo.getHostName().equalsIgnoreCase(
										server.getServerIP())) {
							logger.debug("this server is normal server "
									+ serverInfo.getServerId());
							serverInfo.setStatus(ASBUServerStatus.AVALIABLE);
						}
						if(serverInfo.getHostName() != null && serverInfo.getHostName().contains(".")){
							
							if(!serverInfo.getHostName().matches(ipv4Pattern)){//fqdn name
								String hostname = serverInfo.getHostName().substring(0,serverInfo.getHostName().indexOf("."));
								if(hostname != null && hostname.equalsIgnoreCase(server.getServerName())){
									logger.debug("this server is normal server "
											+ serverInfo.getServerId());
									serverInfo.setStatus(ASBUServerStatus.AVALIABLE);
								}
							}
						}
					}
				}

				Map<String, String[]> serverPlanMap = new HashMap<>();
				for (ASBUServerInfo server : dbServers) {
					if (server.getStatus() == ASBUServerStatus.DELETED) {
						logger.debug("delete server " + server.getServerId());
						deletedServers.add(server);
						int planCount = getASBUPlanCount(server.getServerId(), "");
						if (planCount == 0) {// no plan use
							asbuDao.updateDeletedServerStatus(
									server.getDomainId(), server.getServerId(),
									ASBUServerStatus.DELETED.ordinal());
						} else {
							List<UnifiedPolicy> plans = new ArrayList<>();
							planDao.getPlanNamesByDestinationId(
									server.getServerId(), plans);
							if (CollectionUtils.isNotEmpty(plans)) {
								String[] planNames = new String[plans.size()];
								int i = 0;
								for (UnifiedPolicy plan : plans) {
									planNames[i++] = plan.getName();
								}
								serverPlanMap.put(server.getHostName(),
										(String[]) planNames);
							}
						}
					}
				}
				if (serverPlanMap.size() > 0) {
					DaoFactory.rollbackTrans();
					return ASBUSyncResultFactory
							.createMemberRoleChangedAndHasPlanUseServerResult(serverPlanMap);
				}
			}
			DaoFactory.commitTrans();
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			DaoFactory.rollbackTrans();
			return null;
		}
	}

	public void register(final ArcserveConnectInfo connectInfo, final String hostNameOrIp,	List<com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo> serversFromAsbu) throws EdgeServiceFault
	{
		//register and update server uuid
		List<IntegerId> idList = new ArrayList<IntegerId>();
		asbuDao.findASBUDomain(hostNameOrIp, connectInfo.getGatewayEntity()
				.getId().getRecordId(), idList);
		int domainId = idList.get(0).getId();
		List<ASBUServerInfo> dbServers = getASBUServerListWithoutGroup(connectInfo.getGatewayEntity().getId(), domainId);
		List<Future<ASBUServerInfo>> futureList = new ArrayList<Future<ASBUServerInfo>>();
		if(CollectionUtils.isNotEmpty(dbServers)){
			final String edgeHostName = getEdgeHostName();
			final String edgeProtocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
			final int edgePort = EdgeCommonUtil.getEdgeWebServicePort();
			final String edgeWSDL = com.ca.arcserve.edge.app.base.webservice.WebServiceFactory.getGateWayWSDL(edgeHostName, edgePort, edgeProtocol);
			final String edgeUUID = CommonUtil.retrieveCurrentAppUUID();
			logger.debug("start register");
			logger.info("edgeHostName = "+edgeHostName);
			logger.info("edgeProtocol = "+edgeProtocol);
			logger.info("edgePort = "+edgePort);
			logger.info("edgeWSDL = "+edgeWSDL);
			logger.info("edgeUUID = "+edgeUUID);
			ExecutorService pool = Executors.newCachedThreadPool();
			for(final ASBUServerInfo server : dbServers){
				Future<ASBUServerInfo> futrue = pool.submit(new Callable<ASBUServerInfo>(){
					@Override
					public ASBUServerInfo call() throws Exception {
						IArchiveToTapeService currentService = null;
						try{
							logger.debug("host name = "+server.getHostName());
							logger.debug("protocol = "+server.getConnectInfo().getProtocol());
							logger.debug("port = "+server.getConnectInfo().getPort());
							logger.debug("username = "+server.getConnectInfo().getCauser());
							//server.getConnectInfo().setCapasswd(WSJNI.AFDecryptString(server.getConnectInfo().getCapasswd()));
							logger.debug("start init archive service");
							long startTime = System.currentTimeMillis();
							server.setStatus(ASBUServerStatus.AVALIABLE);//1
							try(ASBUConnection connection = connectionFactory.createASBUConnection(server.getServerId())){
								currentService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connection);
								long endTime = System.currentTimeMillis();
								logger.debug("end init archive service, take "+(endTime - startTime)/1000 +"s seconds.");
								//update uuid
								logger.debug("start get uuid");
								long startTime1 = System.currentTimeMillis();
								String serverUUID = currentService.getServerUUID();
								logger.debug("server uuid is "+serverUUID);
								server.getConnectInfo().setUuid(serverUUID);
								long endTime1 = System.currentTimeMillis();
								logger.debug("end get uuid, take "+(endTime1 - startTime1)/1000 +"s seconds.");
								//register
								long startTime2 = System.currentTimeMillis();
								try{
									currentService.register(edgeUUID, "1", edgeHostName, edgeWSDL, Locale.getDefault().toString(), true, server.getHostName());
								}catch(ServerSOAPFaultException e){
									server.setStatus(ASBUServerStatus.UNAVALIABLE);//2
									createASBUActivityLog(server.getServerId(), EdgeCMWebServiceMessages.getMessage("asbu_server_register_error", server.getHostName()));
								}catch(WebServiceException e){
									server.setStatus(ASBUServerStatus.UNAVALIABLE);//3
									createASBUActivityLog(server.getServerId(), EdgeCMWebServiceMessages.getMessage("asbu_server_register_error", server.getHostName()));
								}
								long endTime2 = System.currentTimeMillis();
								logger.debug("end register, take "+(endTime2 - startTime2)/1000 +"s seconds.");
								
								updateOrInsertHostInfoAndConnectInfo(server);
								asbuDao.updateASBUServerExtInfo(server.getServerId(), server.getServerClass().getValue(), server.getDomainId(), server.getStatus().ordinal());
							}
						}catch(ServerSOAPFaultException e){
							server.setStatus(ASBUServerStatus.UNAVALIABLE);//4
							logger.error("[ASBUServiceImpl] register failed, hostname : " + server.getHostName());
							createASBUActivityLog(server.getServerId(), EdgeCMWebServiceMessages.getMessage("asbu_server_connect_error", server.getHostName()));
						}catch(WebServiceException e){
							server.setStatus(ASBUServerStatus.UNAVALIABLE);//5
							logger.error("[ASBUServiceImpl] register failed, hostname : " + server.getHostName());
							createASBUActivityLog(server.getServerId(), EdgeCMWebServiceMessages.getMessage("asbu_server_connect_error", server.getHostName()));
						}
						return server;
					}
				});
				futureList.add(futrue);
			}
			long allStartTime = System.currentTimeMillis();
			for(Future<ASBUServerInfo> future : futureList){
				try {
					future.get();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			long allEndTime = System.currentTimeMillis();
			logger.debug("end add a server, take "+(allEndTime - allStartTime)/1000 +"s seconds.");
			pool.shutdown();
		}

		if (connectInfo.isUpdate())
		{
			createASBUActivityLog(hostNameOrIp, EdgeCMWebServiceMessages.getMessage("asbu_server_update_successfully", hostNameOrIp), Severity.Information);
		} else {
			createASBUActivityLog(hostNameOrIp, EdgeCMWebServiceMessages.getMessage("asbu_server_add_successfully", hostNameOrIp), Severity.Information);
		}		
	}
	
	@Override
	public ASBUSyncResult createOrUpdateASBUServers(final ArcserveConnectInfo connectInfo, final String hostNameOrIp) throws EdgeServiceFault {
		List<com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo> serversFromAsbu = null;
		try {
			GatewayEntity gateway = gatewayService.getGatewayById(connectInfo.getGatewayEntity().getId());
			connectInfo.setGatewayEntity(gateway);
			serversFromAsbu = getServersFromAsbu(connectInfo, hostNameOrIp);
		}catch(SOAPFaultException e){
			String faultCode = e.getFault().getFaultCode();
			String b = faultCode;
			logger.error("[ASBUServiceImpl]createOrUpdateASBUServers failed. the error code is:"+b);
			return ASBUSyncResultFactory.createAsbuServerHasBeenControledByOtherConsoleResult();
		}
		
		ASBUSyncResult validateResult = validate(connectInfo, hostNameOrIp, serversFromAsbu);
		if (validateResult != null)
		{
			return validateResult;
		}
		List<ASBUServerInfo> deletedServers = new ArrayList<ASBUServerInfo>();
		ASBUSyncResult result = operateDB(connectInfo, hostNameOrIp, serversFromAsbu, deletedServers);
		if (result != null)
		{
			return result;
		}
		
		register(connectInfo, hostNameOrIp,	serversFromAsbu);

		if(deletedServers.size() > 0)
		{
			return ASBUSyncResultFactory.createSyncSuccessWithMemberServersDeleted(deletedServers);
			
		} else {
			return ASBUSyncResultFactory.createSyncSuccessWithoutExceptionResult();
		}
	}

	@Override
	public List<DeleteASBUBackupServerResult> deleteASBUDomain(int domainId) throws EdgeServiceFault {
		List<DeleteASBUBackupServerResult> resultList = new ArrayList<DeleteASBUBackupServerResult>();
		DeleteASBUBackupServerResult result = new DeleteASBUBackupServerResult();
		result.setReturnCode(DeleteASBUBackupServerReturnCode.Unknown);
		resultList.add(result);
		try {
			DaoFactory.beginTrans();
			List<EdgeASBUServer> servers = new ArrayList<>();
			asbuDao.findServersByDomainId(domainId,servers);

			for(EdgeASBUServer ASBUServer : servers){
				int planCount = getASBUPlanCount(ASBUServer.getServerId(), "");
				if(planCount > 0){
					result.setReturnCode(DeleteASBUBackupServerReturnCode.Proctected);
					logger.error("Error deleting ASBU domain with id " + domainId + ". planCount > 0, serverId:" + ASBUServer.getServerId());
					return resultList;
				}
			}
			try(ASBUConnection connection = connectionFactory.createASBUConnection(servers.get(0).getServerId())){
				String edgeHostname = getEdgeHostName();
				try{
					IArchiveToTapeService archiveToTapeService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connection);
					archiveToTapeService.unregister(CommonUtil.retrieveCurrentAppUUID(), edgeHostname, true);
				}catch (Exception e){
					logger.error("unregister fail", e);
					createASBUActivityLog(servers.get(0).getServerName(), "ASBU unregister failed:" + e.getMessage(), Severity.Warning);
				}
			}
			result.setReturnCode(DeleteASBUBackupServerReturnCode.Successful);
			asbuDao.deleteDomainById(domainId);
			DaoFactory.commitTrans();
			createASBUActivityLog(servers.get(0).getServerName(), EdgeCMWebServiceMessages.getMessage("asbu_server_delete_successfully", servers.get(0).getServerName()), Severity.Information);
		} catch (Exception e) {
			DaoFactory.rollbackTrans();
			result.setReturnCode(DeleteASBUBackupServerReturnCode.Failed);
			logger.error("Error deleting ASBU domain with id " + domainId + ".", e);
		}
		return resultList;
	}
	private String getEdgeHostName() throws EdgeServiceFault {
		String edgeHostName="";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			edgeHostName = addr.getHostName().toLowerCase();
		} catch (UnknownHostException e) {
			logger.debug("Cannot get local hostname");
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_Fatal_Error, "Cannot get local hostname");
		}
		return edgeHostName;
	}

	@Override
	public List<ASBUServerInfo> getASBUServerList(GatewayId gatewayId, int domainId) throws EdgeServiceFault {
		List<ASBUServerInfo> servers = getASBUServerListWithoutGroup(gatewayId, domainId);
		for(ASBUServerInfo server : servers){
			try{
				server.setGroups(getASBUMediaGroupList(server.getServerId()));
			}catch(Exception e){
				logger.error("get group fail, host name is "+ server.getHostName());
			}
		}
		return servers;
	}
	
	@Override
	public List<ASBUServerInfo> getASBUServerListWithoutGroup(GatewayId gatewayId, int domainId) throws EdgeServiceFault {
		List<EdgeASBUServer> servers = new ArrayList<EdgeASBUServer>();
		List<ASBUServerInfo> serverInfoList = new ArrayList<ASBUServerInfo>();
		if(domainId > 0){
			asbuDao.findServersByDomainId(domainId,servers);
		}else{
			if(gatewayId==null)
				logger.error("getASBUServerListWithoutGroup caught a ERROR: gatewayId isNULL");
			asbuDao.findAllServers(gatewayId.getRecordId(), servers);
		}
		for(EdgeASBUServer server : servers){
			ASBUServerInfo serverInfo = new ASBUServerInfo();
			serverInfo.setDomainId(server.getDomainId());
			serverInfo.setServerId(server.getServerId());
			serverInfo.setHostName(server.getServerName());
			serverInfo.setDomainName(server.getDomainName());
			serverInfo.setStatus(ASBUServerStatus.values()[server.getServerStatus()]);
			serverInfo.setServerClass(ASBUServerClass.fromValue(server.getServerClass()));
			serverInfo.setStatus(ASBUServerStatus.values()[server.getServerStatus()]);
			serverInfo.setSiteName(server.getSiteName());
			ArcserveConnectInfo connectInfo = new ArcserveConnectInfo();
			serverInfo.setConnectInfo(connectInfo);
			connectInfo.setPort(server.getPort());
			connectInfo.setProtocol(Protocol.parse(server.getProtocol()));
			connectInfo.setCauser(server.getUsername());
			connectInfo.setCapasswd(server.getPassword());
			connectInfo.setAuthmode(server.getAuthMode() == ASBUAuthenticationType.WINDOWS.getValue() ? ABFuncAuthMode.WINDOWS : ABFuncAuthMode.AR_CSERVE);
			GatewayEntity gatewayEntity = gatewayService.getGatewayById(gatewayId);
			connectInfo.setGatewayEntity(gatewayEntity);
			/*int[] planCount = new int[1];
			planDao.getPlanCountByDestinationIdAndType(server.getHostId(), PlanDestinationType.ASBU.ordinal(), null, planCount);
			serverInfo.setPlanCount(planCount[0]);*/
			serverInfoList.add(serverInfo);
		}
		return serverInfoList;
	}
	
	@Override
	public List<ASBUMediaGroupInfo> getASBUMediaGroupList(int serverId)throws EdgeServiceFault {
		List<ASBUMediaGroupInfo> groupInfoList = new ArrayList<>();
		EdgeHost host = ASBUDestinationManager.getInstance().getServer(serverId);
		if(host != null){
			ArcserveConnectInfo connectInfo = new ArcserveConnectInfo(); 
			ASBUDestinationManager.getInstance().bindConnectInfo(host, connectInfo);
			IArchiveToTapeService archiveToTapeService = null;
			try(ASBUConnection connection = connectionFactory.createASBUConnection(host.getRhostid())){
				archiveToTapeService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connection);
				List<ASBUGroupInfo> groups =  archiveToTapeService.getGroupList();
				if(CollectionUtils.isNotEmpty(groups)){
					boolean displayASBUGroupFSD = getASBURegularGroupFSDFlag();
					for(ASBUGroupInfo group : groups){
						ASBUMediaGroupInfo groupInfo = new ASBUMediaGroupInfo();
						ASBURegularGroup regularGroup = parseRegularGroup(group.getFlags(), displayASBUGroupFSD);
						if(regularGroup == ASBURegularGroup.TSI_EMPTY_GROUP){
							continue;
						}
						groupInfo.setRegularType(regularGroup);
						groupInfo.setType(ASBUMediaGroupType.fromValue(group.getGroupType()));
						groupInfo.setName(group.getName());
						groupInfo.setNumber(group.getNumber());
						groupInfoList.add(groupInfo);
					}
				}
			}
		}
		return groupInfoList; 
	}
	
	@Override
	public int getASBUPlanCount(int serverId, String mediaGroupName)
			throws EdgeServiceFault {
		int[] planCount = new int[1];
		planDao.getPlanCountByDestinationIdAndType(serverId, PlanDestinationType.ASBU.ordinal(), mediaGroupName, planCount);
		return planCount[0];
	}
	
	@Override
	public List<ASBUMediaInfo> getASBUMediaList(int serverId, int groupNum) throws EdgeServiceFault	{
		List<ASBUMediaInfo> mediaist = new ArrayList<>();
		EdgeHost host = ASBUDestinationManager.getInstance().getServer(serverId);
		if(host != null){
			ArcserveConnectInfo connectInfo = new ArcserveConnectInfo(); 
			ASBUDestinationManager.getInstance().bindConnectInfo(host, connectInfo);
			IArchiveToTapeService archiveToTapeService = null;
//			final String hostName = host.getRhostname();
//			archiveToTapeService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connectInfo,hostName);
			try(ASBUConnection connection = connectionFactory.createASBUConnection(host.getRhostid())){
				List<com.ca.asbu.webservice.data.archive2tape.ASBUMediaInfo> medias = null;
				try {
					archiveToTapeService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connection);
					medias =  archiveToTapeService.getMediaList(groupNum);
				} catch (WebServiceException | EdgeServiceFault ex){
					logger.error("[ASBUServiceImpl] getASBUMediaPoolSet failed, hostname : " + host.getRhostname());
					logger.error(ex);
					createASBUActivityLog(host.getRhostid(), EdgeCMWebServiceMessages.getMessage("asbu_server_connect_error", host.getRhostname()));
				}
				if(CollectionUtils.isNotEmpty(medias)){
					for(com.ca.asbu.webservice.data.archive2tape.ASBUMediaInfo media : medias){
						ASBUMediaInfo mediaInfo = new ASBUMediaInfo();
						/*if(media.getName() == null || "".equals(media.getName())){
							if((media.getSlotFlag() & ASBUMediaFlag.TSI_TF_BLANK.getValue()) == ASBUMediaFlag.TSI_TF_BLANK.getValue()){
								mediaInfo.setName("<Blank Media>");
							}else{
								mediaInfo.setName("<Empty>");
							}
						}else{
							mediaInfo.setName(media.getName());
						}
						if(media.getSerialNo() == null || "".equals(media.getSerialNo())){
							mediaInfo.setSerialNo("<N/A>");
						}else{
							mediaInfo.setSerialNo(media.getSerialNo());
						}*/
						
						mediaInfo.setName(media.getName());
						mediaInfo.setSerialNo(media.getSerialNo());
						mediaInfo.setSlotNo(String.valueOf(media.getSlotNo()));
						mediaInfo.setDeviceNo(String.valueOf(media.getDeviceNo()));
						mediaInfo.setBlockSize(media.getBlockSize());
						mediaInfo.setCreated(media.getCreated());
						mediaInfo.setDensityCodeString(media.getDensityCodeString());
						mediaInfo.setExpirationDate(media.getExpirationDate());
						mediaInfo.setFormatCode(media.getFormatCode());
						mediaInfo.setMagazineNo(media.getMagazineNo());
						mediaInfo.setMediumTypeString(media.getMediumTypeString());
						mediaInfo.setRandomId(media.getRandomID());
						mediaInfo.setSequence(media.getSequence());
						mediaInfo.setSlotFlag(media.getSlotFlag());
						mediaInfo.setSlotType(media.getSlotType());
						mediaInfo.setTapeType(media.getTapeType());
						mediaInfo.setWirteProtected(media.isWirteProtected());
						mediaInfo.setExpiration(media.isExpiration());
						mediaInfo.setmBWritten(media.getMBWritten());
						mediaInfo.setLastWriteTime(media.getLastWriteTime());
						mediaInfo.setMediaPoolName(media.getMediaPoolName());
						mediaInfo.setMediaStatusInPool(media.getMediaStatusInPool());
						mediaInfo.setExpirationDateInPool(media.getExpirationDateInPool());
						mediaist.add(mediaInfo);
					}
				}
			}
		}
		return mediaist;
	}

	@Override
	public List<ASBUMediaPoolSet> getASBUMediaPoolSet(int serverId, String groupName) throws EdgeServiceFault {
		EdgeHost host = ASBUDestinationManager.getInstance().getServer(serverId);
		List<ASBUMediaPoolSet> returnMediaPoolSetList = new ArrayList<ASBUMediaPoolSet>();
		if(host == null){
			logger.error("ASBUServiceImpl---getASBUMediaPoolSet, getServer is null, serverId =" + serverId);
			return returnMediaPoolSetList;
		}
		ArcserveConnectInfo connectInfo = new ArcserveConnectInfo(); 
		ASBUDestinationManager.getInstance().bindConnectInfo(host, connectInfo);
//		IArchiveToTapeService archiveToTapeService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connectInfo,serverName);
		IArchiveToTapeService archiveToTapeService;
		try(ASBUConnection connection = connectionFactory.createASBUConnection(host.getRhostid())){
			List<ArchiveJobMediaPoolSet> mediaPoolSetList = new ArrayList<ArchiveJobMediaPoolSet>();
			try {
				archiveToTapeService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connection);
				mediaPoolSetList = archiveToTapeService.getArchiveJobMediaPool(groupName);
			} catch (WebServiceException | EdgeServiceFault ex){
				logger.error("[ASBUServiceImpl] getASBUMediaPoolSet failed, hostname : " + host.getRhostname());
				logger.error(ex);
				createASBUActivityLog(host.getRhostid(), EdgeCMWebServiceMessages.getMessage("asbu_server_connect_error", host.getRhostname()));
			}
			for(ArchiveJobMediaPoolSet archiveJobMediaPoolSet : mediaPoolSetList){
				List<ASBUMediaPool> pools = new ArrayList<ASBUMediaPool>();
				ASBUMediaPoolSet set = new ASBUMediaPoolSet();
				set.setName(archiveJobMediaPoolSet.getMediaPoolSetName());
				if(archiveJobMediaPoolSet.getMediaPools()!=null && archiveJobMediaPoolSet.getMediaPools().size() > 0)
				for(ArchiveJobMediaPool archiveJobMediaPool : archiveJobMediaPoolSet.getMediaPools()){
					ASBUMediaPool mediaPool = new ASBUMediaPool();
					mediaPool.setName(archiveJobMediaPool.getMediaPoolName());
					mediaPool.setSelected(archiveJobMediaPool.isSelectForArchive());
					mediaPool.setValue(archiveJobMediaPool.getRententionValue());
					mediaPool.setMediaUsageMode(archiveJobMediaPool.getMediaUsageMode());
					mediaPool.setbMuxEnable(archiveJobMediaPool.isbMuxEnable());
					mediaPool.setnMuxStream(archiveJobMediaPool.getnMuxStream());
					if(archiveJobMediaPool.getRententionUnit()!=null){
						mediaPool.setType(MediaPoolType.fromValue(archiveJobMediaPool.getRententionUnit().getValue()));
					}
					pools.add(mediaPool);
				}
				set.setMediaPools(pools);
				returnMediaPoolSetList.add(set);
			}
		}
		return returnMediaPoolSetList;
	}
	
	private int updateOrInsertHostInfoAndConnectInfo(ASBUServerInfo serverInfo) {
		int[] newHostId = new int[1];
		int hostId = serverInfo.getServerId();
		
		String hostName = serverInfo.getHostName();
		if(!StringUtil.isEmptyOrNull(hostName))
			hostName = hostName.toLowerCase();
		//List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
		List<String> fqdnNameList = new ArrayList<String>();
		GatewayEntity gateway = serverInfo.getConnectInfo().getGatewayEntity();
		if(gateway != null && gateway.getId()!=null && gateway.getId().isValid()){
			IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gateway.getId());
			try {
				fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
			} catch (Exception e) {
				logger.error("[ASBUServiceImpl] updateOrInsertHostInfoAndConnectInfo() get fqdn name failed.",e);
			}
		}
		String fqdnName = CommonUtil.listToCommaString(fqdnNameList);
		
		hostManagerDao.as_edge_host_update(
			hostId,									    // rhostid
			new Date(),									// lastupdated
			hostName,		// rhostname
			"",											// nodeDescription
			"",											// ipaddress
			"",											// osdesc
			"",											// ostype
			1,											// IsVisible
			0,											// appStatus
			"",											// ServerPrincipalName
			HostType.EDGE_NODE_UNKNOWN.getValue(),		// rhostType
			ProtectionType.ASBUServer.getValue(),		// protectionType
			fqdnName,                                   // fqdnName
			newHostId);
		ArcserveConnectInfo connectInfo = serverInfo.getConnectInfo();
		int authMode = ASBUDestinationManager.getInstance().parseAuthenticationType(connectInfo.getAuthmode());
		hostId = (hostId != 0) ? hostId : newHostId[0];
		connectInfoDao.insertOrUpdateArcserveConnectInfo(
			hostId, 
			connectInfo.getCauser(), 
			connectInfo.getCapasswd(), 
			authMode, 
			connectInfo.getProtocol().ordinal(), 
			connectInfo.getPort(), 
			0, 
			connectInfo.getVersion(), 
			0,
			connectInfo.getUuid());
		return hostId;
	}
	
	private ASBURegularGroup parseRegularGroup(long flags, boolean displayASBUGroupFSD){
		if((flags & ASBURegularGroup.TSI_SHARED_GROUP.getValue()) == ASBURegularGroup.TSI_SHARED_GROUP.getValue()){
			return ASBURegularGroup.TSI_SHARED_GROUP;
		}
		if((flags & ASBURegularGroup.TSI_GROUP_ON_PRIMARY.getValue()) == ASBURegularGroup.TSI_GROUP_ON_PRIMARY.getValue()){
			return ASBURegularGroup.TSI_GROUP_ON_PRIMARY;
		}
		if((flags & ASBURegularGroup.TSI_NAS_ENABLED_GROUP.getValue()) == ASBURegularGroup.TSI_NAS_ENABLED_GROUP.getValue()){
			return ASBURegularGroup.TSI_NAS_ENABLED_GROUP;
		}
		if((flags & ASBURegularGroup.TSI_STAGING_GROUP.getValue()) == ASBURegularGroup.TSI_STAGING_GROUP.getValue()){
			return ASBURegularGroup.TSI_EMPTY_GROUP;
		}
		if((flags & ASBURegularGroup.TSI_DEDUPE_GROUP.getValue()) == ASBURegularGroup.TSI_DEDUPE_GROUP.getValue()){
			return ASBURegularGroup.TSI_EMPTY_GROUP;
		}
		if((flags & ASBURegularGroup.TSI_FSD_GROUP.getValue()) == ASBURegularGroup.TSI_FSD_GROUP.getValue()){
			if(displayASBUGroupFSD)
				return ASBURegularGroup.TSI_FSD_GROUP;
			else
				return ASBURegularGroup.TSI_EMPTY_GROUP;
		}
		if((flags & ASBURegularGroup.TSI_VTL_GROUP.getValue()) == ASBURegularGroup.TSI_VTL_GROUP.getValue()){
			return ASBURegularGroup.TSI_VTL_GROUP;
		}
		if((flags & ASBURegularGroup.TSI_EMPTY_GROUP.getValue()) == ASBURegularGroup.TSI_EMPTY_GROUP.getValue()){
			return ASBURegularGroup.TSI_EMPTY_GROUP;
		}
		return ASBURegularGroup.TSI_REGULAR_GROUP;
	}
	@Override
	public ASBUStatus checkASBUStatus(ASBUInfo info) throws EdgeServiceFault {
		return ASBUStatus.OK;
	}
	@Override
	public int checkPlanStatus(String d2dUuid, String policyUuid,
			boolean justcheck) throws EdgeServiceFault {
		return 0;
	}
	@Override
	public List<ASBUDeviceInformation> getASBUDeviceList(int serverId, int groupNum) throws EdgeServiceFault {
		List<ASBUDeviceInformation> deviceList = new ArrayList<>();
		EdgeHost host = ASBUDestinationManager.getInstance().getServer(serverId);
		if(host != null){
			ArcserveConnectInfo connectInfo = new ArcserveConnectInfo(); 
			ASBUDestinationManager.getInstance().bindConnectInfo(host, connectInfo);
			IArchiveToTapeService archiveToTapeService = null;
//			final String hostName = host.getRhostname();
//			archiveToTapeService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connectInfo,hostName);
			try(ASBUConnection connection = connectionFactory.createASBUConnection(host.getRhostid())){
				archiveToTapeService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connection);
				List<com.ca.asbu.webservice.data.archive2tape.ASBUDeviceInfo> devices =  archiveToTapeService.getDeviceList(groupNum);
//				List<com.ca.asbu.webservice.data.archive2tape.ASBUDeviceInfo> devices =  new ArrayList<>();
				if(CollectionUtils.isNotEmpty(devices)){
					for(com.ca.asbu.webservice.data.archive2tape.ASBUDeviceInfo deviceInfo : devices){
						ASBUDeviceInformation deviceInformation = new ASBUDeviceInformation();
						deviceInformation.setBlockSize(deviceInfo.getBlockSize());
						deviceInformation.setCapFlags(deviceInfo.getCapFlags());
						deviceInformation.setCartridgeType(deviceInfo.getCartridgeType());
						deviceInformation.setCompliance(deviceInfo.getCompliance());
						deviceInformation.setDeviceType(deviceInfo.getDeviceType());
						deviceInformation.setFirmware(deviceInfo.getFirmware());
						deviceInformation.setFsdPath(deviceInfo.getFsdPath());
						deviceInformation.setFormatCode(deviceInfo.getFormatCode());
						deviceInformation.setGroupName(deviceInfo.getGroupName());
						deviceInformation.setHostBoardNo(deviceInfo.getHostBoardNo());
						deviceInformation.setLogicalDeviceNo(deviceInfo.getLogicalDeviceNo());
						deviceInformation.setLun(deviceInfo.getLun());
						deviceInformation.setNoOfDrives(deviceInfo.getNoOfDrives());
						deviceInformation.setNoOfIeElement(deviceInfo.getNoOfIeElement());
						deviceInformation.setNoOfMagazines(deviceInfo.getNoOfMagazines());
						deviceInformation.setNoOfSlots(deviceInfo.getNoOfSlots());
						deviceInformation.setProductID(deviceInfo.getProductID());
						deviceInformation.setReadShots(deviceInfo.getReadShots());
						deviceInformation.setReservedDrives(deviceInfo.getReservedDrives());
						deviceInformation.setScsiBusNo(deviceInfo.getScsiBusNo());
						deviceInformation.setScsiID(deviceInfo.getScsiID());
						deviceInformation.setTapeFlags(deviceInfo.getTapeFlags());
						deviceInformation.setUnReservedDrives(deviceInfo.getUnReservedDrives());
						deviceInformation.setVendorID(deviceInfo.getVendorID());
						deviceInformation.setWriteShots(deviceInfo.getWriteShots());
						deviceList.add(deviceInformation);
					}
				}
			}
		}
		return deviceList;
	}
	
	
	@Override
	public ConnectionContext getASBUConnectInfo(int nodeId) throws EdgeServiceFault {
		List<ConnectionContext> contexts = new ArrayList<>();
		asbuDao.findConnectionInfoByHostId(nodeId, contexts);
		if(CollectionUtils.isNotEmpty(contexts)){
			ConnectionContext context =  contexts.get(0);
			if(StringUtil.isNotEmpty(context.getProtocol()) && "1".equals(context.getProtocol())){
				context.setProtocol("http");
			}else{
				context.setProtocol("https");
			}
			GatewayEntity gatewayEntity = gatewayService.getGatewayByHostId(nodeId);
			if(gatewayEntity != null){
				context.setGateway(gatewayEntity);
			}
			return context;
		}
		return null;
	}

	private void createASBUActivityLog(int hostId, String message) throws EdgeServiceFault{
		ActivityLog log = new ActivityLog();
		log.setModule(Module.ASBUConnectServer);
		log.setHostId(hostId);
		log.setMessage(message);
		log.setSeverity(Severity.Error);
		log.setTime(new Date());
		logService.addLog(log);
	}
	
	private void createASBUActivityLog(String nodeName, String message, Severity severity) throws EdgeServiceFault{
		ActivityLog log = new ActivityLog();
		log.setModule(Module.ASBUConnectServer);
		log.setNodeName(nodeName);
		log.setMessage(message);
		log.setSeverity(severity);
		log.setTime(new Date());
		logService.addLog(log);
	}
	
	private boolean getASBURegularGroupFSDFlag(){
		String ASBURegularGroupFSDFlag = CommonUtil.getApplicationExtentionKey(WindowsRegistry.VALUE_ASBU_REGULARGROUP_FSD);
		if(StringUtil.isEmptyOrNull(ASBURegularGroupFSDFlag)){
			CommonUtil.setApplicationExtentionKey(WindowsRegistry.VALUE_ASBU_REGULARGROUP_FSD, "0");
			return false;
		}
		if(ASBURegularGroupFSDFlag.equalsIgnoreCase("1"))
			return true;
		return false;
	}
	
	@Override
	public ASBUServerStatusInfo getAsbuServerStatus(int serverId)throws EdgeServiceFault {
		EdgeHost host = ASBUDestinationManager.getInstance().getServer(serverId);
		if(host != null){
			ArcserveConnectInfo connectInfo = new ArcserveConnectInfo(); 
			ASBUDestinationManager.getInstance().bindConnectInfo(host, connectInfo);
			IArchiveToTapeService archiveToTapeService = null;
			try(ASBUConnection connection = connectionFactory.createASBUConnection(host.getRhostid())){
				archiveToTapeService = ASBUDestinationManager.getInstance().initArchiveToTapeService(connection);
				AsbuServerStatus asbuStatus =  archiveToTapeService.getAsbuServerStatus();
				if(asbuStatus == null)
					return null;
				ASBUServerStatusInfo asbuStatusInfo = new ASBUServerStatusInfo();
				asbuStatusInfo.setValue(asbuStatus.getReturnVal());
				asbuStatusInfo.setName(asbuStatus.getStatusString());
				asbuStatusInfo.setMessage(asbuStatus.getMsgString());
				return asbuStatusInfo;
			}
		}
		return null; 
	}
}
