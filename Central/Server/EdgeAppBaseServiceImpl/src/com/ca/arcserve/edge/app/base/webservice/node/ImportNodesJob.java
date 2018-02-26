package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.EdgeJob;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.monitor.ImportNodesJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitorManager;

public class ImportNodesJob extends EdgeJob {

	public static double REQUIRED_D2D_VERSION;
	public static double REQUIRED_D2D_UPDATE_VERSION;
	public static final double REQUIRED_ARCSERVE_VERSION = 16.0;
	private static final Logger logger = Logger.getLogger(ImportNodesJob.class);
	
	static{
		try{
			REQUIRED_D2D_VERSION = CommonUtil.getRequireD2DVersion();
			REQUIRED_D2D_UPDATE_VERSION = CommonUtil.getRequiredD2DUpdateVersionNumber();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			REQUIRED_D2D_VERSION = 0;
		}
	}
	
	private static final String TAG_NODES = "nodes";
	private static final String TAG_TYPE = "type";
	private static final String TAG_SERVICE = "service";
	private static final String TAG_EDGE_USERNAME = "edgeUsername";
	private static final String TAG_EDGE_PASSWORD = "edgePassword";
	private static final String TAG_EDGE_DOMAIN = "edgeDomain";


	protected NodeRegistrationInfo[] nodes;
	protected ImportNodeType type;
	protected NodeServiceImpl nodeService;
	protected String edgeUsername;
	protected String edgePassword;
	protected String edgeDomain;
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	protected IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);

	public ImportNodesJob() {
	}

	public JobDetail createJobDetail(NodeRegistrationInfo[] nodes, ImportNodeType type, NodeServiceImpl nodeService) {
		
		JobDetail jobDetail = new JobDetailImpl(getClass().getSimpleName()+getId(), null, getClass());
		
		super.createJobDetail(jobDetail);//store job id into jobdetail

		jobDetail.getJobDataMap().put(TAG_NODES, nodes);
		jobDetail.getJobDataMap().put(TAG_TYPE, type);
		jobDetail.getJobDataMap().put(TAG_SERVICE, nodeService);

		String edgeUser =(String) nodeService.serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_USERNAME);
		String edgePassword = (String)nodeService.serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_PASSWORD);
		String edgeDomain =(String) nodeService.serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_DOMAIN);

		jobDetail.getJobDataMap().put(TAG_EDGE_USERNAME, edgeUser);
		jobDetail.getJobDataMap().put(TAG_EDGE_PASSWORD, edgePassword);
		jobDetail.getJobDataMap().put(TAG_EDGE_DOMAIN, edgeDomain);

		return jobDetail;
	}
	
	public static SimpleTriggerImpl makeImmediateTrigger(int repeatCount, long repeatInterval) {
		SimpleTriggerImpl trig = new SimpleTriggerImpl();
		trig.setStartTime(new Date());
		trig.setRepeatCount(repeatCount);
		trig.setRepeatInterval(repeatInterval);
		return trig;
	}

	public void schedule(JobDetail jobDetail) throws SchedulerException {
		Scheduler importNodesScheduler = SchedulerUtilsImpl.getScheduler();
		SimpleTriggerImpl trigger = makeImmediateTrigger(0, 0);
		trigger.setName(((JobDetailImpl)jobDetail).getName() + "Trigger");
		importNodesScheduler.scheduleJob(jobDetail, trigger);
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		loadContextData(context);
		
		if (!validateContextData()) {
			return;
		}
		
		JobMonitor monitor = JobMonitorManager.getInstance().getJobMonitor(getId(), ImportNodesJobMonitor.class);
		if(monitor == null){
			logger.error("job monitor is null. job id=" + getId());
			return;
		}
		
		synchronized (monitor) {
			nodeService.addActivityLogForImportNodes(Severity.Information, type, 
					EdgeCMWebServiceMessages.getMessage("importNodes_Start", nodes.length));
			importAll();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
			}
			nodeService.addActivityLogForImportNodes(Severity.Information, type, 
					EdgeCMWebServiceMessages.getMessage("importNodes_End"));
			
			try {
				SchedulerUtilsImpl.getScheduler().deleteJob(new JobKey(((JobDetailImpl)context.getJobDetail()).getName(), null));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			monitor.notifyAll();
		}
	}

	protected void loadContextData(JobExecutionContext context) {
		
		super.loadContextData(context);
		
		if (context.getJobDetail().getJobDataMap().get(TAG_NODES) instanceof NodeRegistrationInfo[]) {
			nodes = (NodeRegistrationInfo[])context.getJobDetail().getJobDataMap().get(TAG_NODES);
		}

		if (context.getJobDetail().getJobDataMap().get(TAG_TYPE) instanceof ImportNodeType) {
			type = (ImportNodeType)context.getJobDetail().getJobDataMap().get(TAG_TYPE);
		}

		if (context.getJobDetail().getJobDataMap().get(TAG_SERVICE) instanceof NodeServiceImpl) {
			nodeService = (NodeServiceImpl)context.getJobDetail().getJobDataMap().get(TAG_SERVICE);
		}

		if (context.getJobDetail().getJobDataMap().get(TAG_EDGE_USERNAME) instanceof String) {
			edgeUsername = (String)context.getJobDetail().getJobDataMap().get(TAG_EDGE_USERNAME);
		}

		if (context.getJobDetail().getJobDataMap().get(TAG_EDGE_PASSWORD) instanceof String) {
			edgePassword = (String)context.getJobDetail().getJobDataMap().get(TAG_EDGE_PASSWORD);
		}

		if (context.getJobDetail().getJobDataMap().get(TAG_EDGE_DOMAIN) instanceof String) {
			edgeDomain = (String)context.getJobDetail().getJobDataMap().get(TAG_EDGE_DOMAIN);
		}
	}

	protected boolean validateContextData() {
		
		boolean result = super.validateContextData();
		
		if(!result){
			logger.error("job id is null.");
			return false;
		}
		
		if (type == null) {
			logger.error("Import nodes type is null.");
			return false;
		}

		logger.debug("Import nodes type: " + type.name());

		if (nodeService == null) {
			logger.error("Node service is null.");
			return false;
		}

		if (nodes == null || nodes.length == 0) {
			logger.debug("There is no nodes to import.");
			return false;
		}

		logger.debug("Import " + nodes.length + " nodes.");

		return true;
	}

	protected void importAll() {
		for (NodeRegistrationInfo node : nodes) {
			importSingle(node);
		}
	}
	
	private ConnectionContext createConnectionContext(NodeRegistrationInfo node) throws EdgeServiceFault {
		String protocol = node.getD2dProtocol() == Protocol.Https ? "https" : "http";
		int port = node.getD2dPort() == 0 ? 8014 : node.getD2dPort();
		ConnectionContext context = new ConnectionContext(protocol, node.getNodeName(), port);
		context.buildCredential(node.getUsername(), node.getPassword(), "");
		
		IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
		GatewayEntity gateway = gatewayService.getGatewayById(node.getGatewayId());
		context.setGateway(gateway);
		
		return context;
	}
	
	protected RemoteNodeInfo getRemoteNodeInfo(NodeRegistrationInfo node) {
		IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
		IRemoteNativeFacade remoteNativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade(node.getGatewayId());
		
		try {
			return remoteNativeFacade.scanRemoteNode(edgeUsername, edgeDomain, edgePassword,
					node.getNodeName(), node.getUsername(), node.getPassword());
		} catch (Exception e) {
			logger.info("scan remote registry failed, error message = " + e.getMessage());
		}
		
		ConnectionContext context;
		
		try {
			context = createConnectionContext(node);
		} catch (EdgeServiceFault e) {
			logger.error("create connection context failed.", e);
			return null;
		}
		
		try {
			// Try connect to web service with default protocol
			return nodeService.tryConnectD2D(context);
		} catch (Exception e) {
		}
		
		context.setProtocol(node.getD2dProtocol() == Protocol.Https ? "http" : "https");
		
		try {
			// Try connect to web service with different protocol					
			return nodeService.tryConnectD2D(context);
		} catch (Exception e) {
		}
		
		return null;
	}
	
	protected boolean updateRemoteNodeInfo(NodeRegistrationInfo node) {
		boolean queryRemoteRegRet = true;
		RemoteNodeInfo nodeInfo = getRemoteNodeInfo(node);
		if (nodeInfo == null) {
			queryRemoteRegRet = false;
			//nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Warning, type, EdgeCMWebServiceMessages.getMessage("ImportNode_FailedQueryRemoteRegistry"));
			nodeInfo = new RemoteNodeInfo();
			if (node.getD2dPort() != 0) {
				nodeInfo.setD2DPortNumber(node.getD2dPort());				
			}
			if (node.getD2dProtocol() != null) {
				nodeInfo.setD2DProtocol(node.getD2dProtocol()); 
			}
			nodeInfo.setD2DInstalled(true);
			node.setRegisterD2D(true);
		}
		node.setD2dPort(nodeInfo.getD2DPortNumber());
		node.setD2dProtocol(nodeInfo.getD2DProtocol());
		node.setNodeInfo(nodeInfo);
		return queryRemoteRegRet;
	}

	protected int importSingle(NodeRegistrationInfo node) {
		//If the node information have not transfered, then get it again
		if(node.getNodeInfo() == null){
			String protocol = (node.getD2dProtocol() == Protocol.Https ? "http" : "https");
			try {
				RemoteNodeInfo nodeInfo = nodeService.queryRemoteNodeInfo(node.getGatewayId(), node.getId(), 
						node.getNodeName(), node.getUsername(), node.getPassword(), protocol, node.getD2dPort());
				node.setNodeInfo(nodeInfo);
			} catch (Exception e) {
				nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Warning, type, EdgeCMWebServiceMessages.getMessage("ImportNode_FailedQueryRemoteRegistry"));
				logger.error("[ImportNodesJob] importSingle() getRemoteNodeInfo failed.",e);
				RemoteNodeInfo nodeInfo = new RemoteNodeInfo();
				node.setNodeInfo(nodeInfo);
			}
		}
		
		if (node.getNodeInfo().isD2DInstalled()||node.getNodeInfo().isD2DODInstalled()){
			double d2dVersion = 0;
			try{
	    		d2dVersion = Double.parseDouble(node.getNodeInfo().getD2DMajorVersion()+"."+node.getNodeInfo().getD2DMinorVersion());
	    		if (d2dVersion==REQUIRED_D2D_VERSION)
	    			node.setRegisterD2D(true);
	    	}catch (Exception e){
	    		
	    	}
		}
		
		String nodeUuid = node.getNodeInfo().getD2DUUID();
		String authUuid = null;
		if (node.getNodeInfo().isD2DInstalled()) {
			String protocol = (node.getNodeInfo().getD2DProtocol() == Protocol.Http ? "http" : "https");
			ConnectionContext context = new ConnectionContext(protocol, node.getNodeName(), node.getNodeInfo().getD2DPortNumber());
			context.buildCredential(node.getUsername(), node.getPassword(), "");
			
			if (node.getGatewayId().isValid()) {
				try {
					GatewayEntity gateway = gatewayService.getGatewayById(node.getGatewayId());
					context.setGateway(gateway);
				} catch (EdgeServiceFault e) {
					logger.error("Cannot find the gateway by id " + node.getGatewayId(), e);
				}
			}

			try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
				connection.connect();
				nodeUuid = connection.getNodeUuid();
				authUuid = connection.getAuthUuid();
			} catch (EdgeServiceFault e) {
				nodeUuid = UUID.randomUUID().toString();
				node.getNodeInfo().setD2DUUID(nodeUuid);
				logger.error("[ImportNodesJob] Failed to connect to D2D: " +node.getNodeName()+" then generate uuid for it, the uuid is: "+ nodeUuid, e);
			}
		}else{// if agent is not installed, console create d2duuid.
			nodeUuid = UUID.randomUUID().toString();
			node.getNodeInfo().setD2DUUID(nodeUuid);
			logger.info("[ImportNodesJob] Node: "+node.getNodeName()+" has not install agent, so generate uuid for it, the uuid is: "+nodeUuid);
		}
		
		//Use the hostname in DB, If have duplicate nodes, not use new name replace the old name in DB
		EdgeHost hostInDB = HostInfoCache.getInstance().getHostInfo(node.getId());
		if(hostInDB != null && !StringUtil.isEmptyOrNull(hostInDB.getRhostname())){
			node.setNodeName(hostInDB.getRhostname().toLowerCase());
		}

		try {
			EdgeHost edgeHost = nodeService.populateEdgeHost(node);
			
			//update the node information to DB
			int[] output = new int[1];
			
			List<String> fqdnNameList = new ArrayList<String>();
			if(node.getGatewayId() != null && node.getGatewayId().isValid()){
				try {
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( node.getGatewayId());
					fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(node.getNodeName());
				} catch (Exception e) {
					logger.error("[ImportNodesJob] importSingle() get fqdn name failed.",e);
				}
			}
			String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
			
			nodeService.hostMgrDao.as_edge_host_update(node.getId(), edgeHost.getLastupdated(), node.getNodeName(),edgeHost.getNodeDescription(),
					edgeHost.getIpaddress(), edgeHost.getOsdesc(),edgeHost.getOstype(), 1, edgeHost.getAppStatus(), 
					"",edgeHost.getRhostType(), node.getProtectionType().getValue(), fqdnNames, output);
			edgeHost.setRhostid(node.getId());
			nodeService.connectionInfoDao.as_edge_connect_info_update(output[0], node.getUsername(), node.getPassword(),
					nodeUuid, node.getNodeInfo().getD2DProtocol().ordinal(), node.getNodeInfo().getD2DPortNumber(), 0,
					node.getNodeInfo().getD2DMajorVersion(), node.getNodeInfo().getD2DMinorVersion(), node.getNodeInfo().getUpdateVersionNumber(), node.getNodeInfo().getD2DBuildNumber(), NodeManagedStatus.Unmanaged.ordinal());
			
			if (authUuid != null) {
				nodeService.connectionInfoDao.as_edge_connect_info_setAuthUuid(nodeUuid, authUuid);
			}
			
			nodeService.connectionInfoDao.as_edge_arcserve_connect_info_update(output[0],node.getCarootUsername(), node.getCarootPassword(),
					node.getAbAuthMode().ordinal(), node.getArcserveProtocol() != null ? node.getArcserveProtocol().ordinal() : 0,
					node.getArcservePort(), node.getNodeInfo().getARCserveType().ordinal(), node.getNodeInfo().getARCserveVersion(), NodeManagedStatus.Unmanaged.ordinal());
			
			if (node.isVMWareVM()) {
				nodeService.hostMgrDao.as_edge_host_updateMachineType(output[0], LicenseMachineType.VSHPERE_VM.getValue());
			} else if (node.isHyperVVM()) {
				nodeService.hostMgrDao.as_edge_host_updateMachineType(output[0], LicenseMachineType.HYPER_V_VM.getValue());
			} else {
				nodeService.tryDetectMachineType(output[0], node.getNodeName(), node.getUsername(), node.getPassword());
			}

			if (EdgeWebServiceContext.getApplicationType() != EdgeApplicationType.vShpereManager && node.getUsername() != null && !"".equals(node.getUsername())){
				tryMarkARCserveProducts(node, edgeHost);	
			}
			
			String nodeName = node.getNodeName();
			if(StringUtil.isEmptyOrNull(nodeName) && node.getVmRegistrationInfo() != null && node.getVmRegistrationInfo().getVmInfo() != null) {
			 	nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", node.getVmRegistrationInfo().getVmInfo().getVmName());
			}
			
			String message = EdgeCMWebServiceMessages.getMessage("importNodes_ImportSingleFinished", nodeName);
			logger.debug(message);
			nodeService.addActivityLogForImportNodes(nodeName, Severity.Information, type, message);

			return output[0];
		} catch (Exception e) {
			String message = EdgeCMWebServiceMessages.getMessage("importNodes_ImportSingleFailed", node.getNodeName());
			logger.error(message, e);
			nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Error, type, message);
			return 0;
		}
	}
	
	protected void tryMarkARCserveProducts(NodeRegistrationInfo node,	EdgeHost edgeHost) {
		try{
			String errorCode = nodeService.tryMarkD2DAsManaged(node, edgeHost, true);
			if (errorCode !=null && !errorCode.isEmpty()){
				String message = null;
				if("12884901905".equals(errorCode)){// already managed by another server .
					message = EdgeCMWebServiceMessages.getMessage("importNodes_AlreadyManagedByOther", 
							node.getNodeInfo().getHostEdgeServer()==null?EdgeCMWebServiceMessages.getMessage("EDGEMAIL_Unknown"):node.getNodeInfo().getHostEdgeServer());
				}else{
					message = MessageReader.getErrorMessage(errorCode);
				}
				if("12884901933".equals(errorCode)||"12884901934".equals(errorCode)){
					message = Utils.getMessage(message,node.getNodeName(),errorCode);
				}
				nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Error, type, message);
			} else {
				nodeService.updateNode(false, node, false, false, false);
			}
		}catch(Exception e){
			logger.error("error when register D2D", e);
		}

		try{
			String errorCode = nodeService.tryMarkARCserveAsManaged(node, edgeHost, true);
			if (errorCode !=null && !errorCode.isEmpty()){
				String message = null;
				if("12884901905".equals(errorCode)){// already managed by another server .
					message = EdgeCMWebServiceMessages.getMessage("importNodes_AlreadyManagedByOther", 
							node.getNodeInfo().getHostEdgeServer()==null?"Unkown":node.getNodeInfo().getHostEdgeServer());
				}else{
					message = MessageReader.getErrorMessage(errorCode);
				}
				if("12884901935".equals(errorCode)||"12884901936".equals(errorCode)){
					message = Utils.getMessage(message,node.getNodeName(),errorCode);
				}
				nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Error, type, message);
			}
		}catch(Exception e){
			logger.error("error when register ARCserve Backup", e);
		}
	}
}
