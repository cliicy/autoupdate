package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.common.LinuxNodeUtil;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.EdgeJob;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.linuximaging.webservice.FlashServiceErrorCode;
import com.ca.arcserve.linuximaging.webservice.data.NodeConnectionInfo;

public class ImportLinuxNodesJob extends EdgeJob {

	private static final String TAG_NODES = "nodes";
	private List<NodeRegistrationInfo> nodes;
	private static final String TAG_SERVICE = "service";
	private LinuxNodeServiceImpl linuxNodeService = new LinuxNodeServiceImpl();
	private NodeServiceImpl nodeService;
	private static final Logger logger = Logger.getLogger(ImportLinuxNodesJob.class);
	private static ImportNodeType type = ImportNodeType.File;
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	
	public JobDetail createJobDetail(List<NodeRegistrationInfo> nodes, NodeServiceImpl nodeService) {
		
		JobDetail jobDetail = new JobDetailImpl(getClass().getSimpleName()+getId(), null, getClass());
		
		super.createJobDetail(jobDetail);//store job id into jobdetail
		jobDetail.getJobDataMap().put(TAG_NODES, nodes);
		jobDetail.getJobDataMap().put(TAG_SERVICE, nodeService);
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
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		loadContextData(context);
		
		if (!validateContextData()) {
			return;
		}
		nodeService.addActivityLogForImportNodes(Severity.Information, type, 
				EdgeCMWebServiceMessages.getMessage("importNodes_Start", nodes.size()));
		for (NodeRegistrationInfo node : nodes) {
			int ret = 0;
			if(node.isLinux()){
				ret = importLinuxNode(node, false, null);
			}else{
				ret = importLinuxD2DServer(node);
			}
			if(ret == 0 ){
				String message = EdgeCMWebServiceMessages.getMessage("importNodes_ImportSingleFinished", node.getNodeName());
				logger.debug(message);
				nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Information, type, message);
			}
		}
		
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
	}
	
	private int importLinuxNode(NodeRegistrationInfo node, boolean markAsLinuxD2DInstalled, Version version){
		int ret = 0;
		try{

			NodeConnectionInfo nodeInfo = linuxNodeService.validateNode(null,node,true,true);
			RegistrationNodeResult result = linuxNodeService.getRegistrationNodeResult(nodeInfo);
			Severity severity = LinuxNodeUtil.getMessageType(result.getErrorCodes());
			if((nodeInfo.getErrCode1() == 0) || (severity == Severity.Warning)){
				
				// This function may be invoked when adding Linux D2D Server.
				// Adding Linux D2D Server will try to add server as a normal
				// Linux node. But when doing that, the node maybe existed
				// already. So, here, we need to read the data of the node out
				// and merge with new information and save it back to DB.
				
				// [Update 2014-11-07, panbo01]:
				// Because issue 193076, we decide not to add the Linux D2D
				// Server as a normal Linux node when adding the server. As a
				// result, some process and comments here may be useless. I'll
				// keep them here temporarily, and will adjust them later when
				// we are sure that the new logic is OK.
				
				EdgeHost host = null;
				EdgeConnectInfo connInfo = null;
				
				if (node.getId() > 0)
				{
					// get EdgeHost from database
					
					List<EdgeHost> hostList = new ArrayList<EdgeHost>();
					linuxNodeService.hostMgrDao.as_edge_host_list( node.getId(), 1, hostList );
					if (hostList.size() > 0)
						host = hostList.get( 0 );
					
					// get EdgeConnectInfo from database
					
					List<EdgeConnectInfo> connInfoList = new ArrayList<EdgeConnectInfo>();
					linuxNodeService.connectionInfoDao.as_edge_connect_info_list( node.getId(), connInfoList );
					if (connInfoList.size() > 0)
						connInfo = connInfoList.get( 0 );
				}
				
				// handle the case that the node or part of the node records
				// got removed
				//
				// This may happens. There will be a period of time between
				// adding records of the node or checking for duplication and
				// the function get invoked. In this period, the node may
				// happen to be deleted. In this case, we continue to insert
				// records as if the this is the first time to add the node.
				
				if ((host == null) || (connInfo == null))
				{
					node.setId( 0 );
					
					host = new EdgeHost();
					host.setRhostname( node.getNodeName() );
					host.setNodeDescription( node.getNodeDescription() );
					host.setOstype( "" );
					host.setServerPrincipalName( "" );
					host.setRhostType( 0 );
					host.setProtectionTypeBitmap( ProtectionType.WIN_D2D.getValue() );
					
					connInfo = new EdgeConnectInfo();
					connInfo.setProtocol( 0 );
					connInfo.setPort( 0 );
					connInfo.setType( 0 );
				}
				
				// merge new data and save it back
				
				int[] output = new int[1];
				String ip = linuxNodeService.getIPAddress(node.getGatewayId(), node.getNodeName());
				
				// if the node already been added as Linux D2D Server, get its
				// version info if version is not specified
				
				int serverNodeId = linuxNodeService.getNodeId(node.getNodeName(), null, 2);
				if ((serverNodeId > 0) && (version == null))
				{
					List<EdgeHost> hostList = new ArrayList<EdgeHost>();
					linuxNodeService.hostMgrDao.as_edge_host_list( serverNodeId, 1, hostList );
					if (hostList.size() > 0)
					{
						EdgeHost serverHost = hostList.get( 0 );
						version = new Version();
						version.setMajorVersion( Integer.parseInt( serverHost.getD2DMajorversion() ) );
						version.setMinorVersion( Integer.parseInt( serverHost.getD2dMinorversion() ) );
						version.setBuildNumber( serverHost.getD2dBuildnumber() );
					}
					
					// NOTE: There are several cases that the version may be 0.0:
					// 1. The server node was added just now and have not been
					//    updated with details yet.
					// 2. When we getting the server node ID, the server node
					//    was there, but before we try to get host info, the
					//    node got deleted.
					// If you encounter similar issues, consider these cases.
				}
				
				int appStatus = (markAsLinuxD2DInstalled || (serverNodeId > 0)) ?
					ApplicationUtil.setLinuxD2DInstalled( host.getAppStatus() ) :
					host.getAppStatus();
				
				String hostName = host.getRhostname();
				if(!StringUtil.isEmptyOrNull(hostName))
					hostName = hostName.toLowerCase();
				
//				List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
//				String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
				List<String> fqdnNameList = new ArrayList<String>();
				if(node.getGatewayId()!=null && node.getGatewayId().isValid()){
					try {
						IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade(node.getGatewayId());
						fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
					} catch (Exception e) {
						logger.error("[ImportLinuxNodesJob] importLinuxNode() get fqdn name failed.",e);
					}
				}
				String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
				
				linuxNodeService.hostMgrDao.as_edge_host_update(
					node.getId(),										// rhostid
					new Date(),											// lastupdated
					hostName,								// rhostname
					host.getNodeDescription(),							// nodeDescription
					ip,													// ipaddress
					nodeInfo.getOsName(),								// osdesc
					host.getOstype(),									// ostype
					1,													// IsVisible
					appStatus,											// appStatus
					host.getServerPrincipalName(),						// ServerPrincipalName
					HostTypeUtil.setLinuxNode( host.getRhostType() ),	// rhostType
					host.getProtectionTypeBitmap(),						// protectionType
					fqdnNames,                                          // fqdn name
					output												// [out] id
					);
				
				node.setId( output[0] );
				
				linuxNodeService.connectionInfoDao.as_edge_connect_info_update(
					node.getId(),															// hostid
					node.getUsername(),														// username
					node.getPassword(),														// password
					nodeInfo.getNodeUUID(),	// uuid
					connInfo.getProtocol(),													// protocol
					connInfo.getPort(),														// port
					connInfo.getType(),														// type
					(version == null) ? connInfo.getMajorversion() : Integer.toString( version.getMajorVersion() ),			// majorversion
					(version == null) ? connInfo.getMinorversion() : Integer.toString( version.getMinorVersion() ),			// minorversion
					(version == null) ? connInfo.getUpdateversionnumber() : Integer.toString( version.getUpdateNumber() ),	// updateversionnumber
					(version == null) ? connInfo.getBuildnumber() : version.getBuildNumber(),								// buildnumber
					NodeManagedStatus.Managed.ordinal()										// managed
					);
				
			}else{
				
				String reason = LinuxNodeUtil.getLinuxMessage(result, node.getNodeName());
				String message = "";
				if(severity == Severity.Error){
					if(result.getErrorCodes()[0] !=null){
						int errorCode = Integer.valueOf(result.getErrorCodes()[0]);
						if(errorCode == 5 || errorCode == 6 || errorCode ==7 || errorCode == 8){
							linuxNodeService.hostMgrDao.as_edge_host_remove(node.getId());
							logger.info("ImportLinuxNodesJob.importLinuxNode(): delete node, nodeId:" + node.getId());
						}
					}
					message = EdgeCMWebServiceMessages.getMessage("importLinuxNodes_ImportSingleFailed", node.getNodeName(),reason);
				}else{
					message = EdgeCMWebServiceMessages.getMessage("importLinuxNodes_ImportSingleFinishedWithWarning", node.getNodeName(),reason);
				}
				
				nodeService.addActivityLogForImportNodes(node.getNodeName(), severity, type, message);
			}
		}catch(EdgeServiceFault e){
			String message = "";
			String reason = "";
			if(e.getFaultInfo().getCode() == EdgeServiceErrorCode.Node_Linux_No_Available_D2D_Server){
				reason = EdgeCMWebServiceMessages.getMessage("updateLinuxNodeNoAvailableServer", node.getNodeName() );
			}
			message = EdgeCMWebServiceMessages.getMessage("importLinuxNodes_ImportSingleFailed", node.getNodeName(),reason);
			nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Error, type, message);
			ret = -1;
		}
		return ret;
	}
	
	private int importLinuxD2DServer(NodeRegistrationInfo node){
		int ret = 0;
		Version version = new Version();
		try{
			linuxNodeService.registerLinuxD2DServer(node,true,true,version);
		}catch(EdgeServiceFault e){
			String message = "";
			String reason = "";
			if (e.getFaultInfo().getCode() == EdgeServiceErrorCode.Node_Linux_D2D_Server_Not_Reachable) {
				reason = EdgeCMWebServiceMessages.getMessage("linuxBackupServerNotReachable", node.getNodeName());
			}else if(e.getFaultInfo().getCode() == EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential){
				reason = EdgeCMWebServiceMessages.getMessage("connectFailedWrongUserAccount", node.getNodeName() );
			}else if(e.getFaultInfo().getCode() == FlashServiceErrorCode.D2D_Server_Management_Managed_By_Others){
				reason = EdgeCMWebServiceMessages.getMessage("linuxBackupServerIsManagedByOther", node.getNodeName());
			}
			message = EdgeCMWebServiceMessages.getMessage("importLinuxNodes_ImportSingleFailed", node.getNodeName(),reason);
			nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Error, type, message);
			ret = -1;
		}
		//tryToAddLinuxD2DServerAsOneNode(node, version);
		tryToAppendServerInfoToExistingNode( node, version );
		return ret;
	}
	
	private void tryToAddLinuxD2DServerAsOneNode(NodeRegistrationInfo node, Version version){
		int nodeid=linuxNodeService.getNodeId(node.getNodeName(), null, 1);
		node.setId(nodeid);
		try{
			importLinuxNode(node, true, version);
		}catch(Exception e){
			logger.warn("tryToAddLinuxD2DServerAsOneNode failed", e);
		}
	}
	
	private void tryToAppendServerInfoToExistingNode( NodeRegistrationInfo serverNode, Version version )
	{
		int nodeId = linuxNodeService.getNodeId( serverNode.getNodeName(), null, 1 );
		if (nodeId <= 0)
			return;
		
		EdgeHost host = null;
		EdgeConnectInfo connInfo = null;
		
		// get existing information
		
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		linuxNodeService.hostMgrDao.as_edge_host_list( nodeId, 1, hostList );
		if (hostList.size() > 0)
			host = hostList.get( 0 );
		
		List<EdgeConnectInfo> connInfoList = new ArrayList<EdgeConnectInfo>();
		linuxNodeService.connectionInfoDao.as_edge_connect_info_list( nodeId, connInfoList );
		if (connInfoList.size() > 0)
			connInfo = connInfoList.get( 0 );
		
		if ((host == null) || (connInfo == null))
			return;
		
		// merge new data and save it back
		
		int[] output = new int[1];
		int appStatus = ApplicationUtil.setLinuxD2DInstalled( host.getAppStatus() );
		
		String hostName = host.getRhostname();
		if(!StringUtil.isEmptyOrNull(hostName))
			hostName = hostName.toLowerCase();
		
//		List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
		List<String> fqdnNameList = new ArrayList<String>();
		if(serverNode.getGatewayId() != null && serverNode.getGatewayId().isValid()){
			try {
				IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( serverNode.getGatewayId() );
				fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
			} catch (Exception e) {
				logger.error("[ImportLinuxNodesJob] tryToAppendServerInfoToExistingNode() get fqdn name failed.",e);
			}
		}
		String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
		
		linuxNodeService.hostMgrDao.as_edge_host_update(
			nodeId,									// rhostid
			new Date(),								// lastupdated
			hostName,					// rhostname
			host.getNodeDescription(),				// nodeDescription
			host.getIpaddress(),					// ipaddress
			host.getOsdesc(),						// osdesc
			host.getOstype(),						// ostype
			host.getIsVisible(),					// IsVisible
			appStatus,								// appStatus
			host.getServerPrincipalName(),			// ServerPrincipalName
			host.getRhostType(),					// rhostType
			host.getProtectionTypeBitmap(),			// protectionType
			fqdnNames,                              // fqdn name
			output									// [out] id
			);
		
		linuxNodeService.connectionInfoDao.as_edge_connect_info_update(
			nodeId,									// hostid
			connInfo.getUsername(),					// username
			connInfo.getPassword(),					// password
			connInfo.getUuid(),						// uuid
			connInfo.getProtocol(),					// protocol
			connInfo.getPort(),						// port
			connInfo.getType(),						// type
			(version == null) ? connInfo.getMajorversion() : Integer.toString( version.getMajorVersion() ),			// majorversion
			(version == null) ? connInfo.getMinorversion() : Integer.toString( version.getMinorVersion() ),			// minorversion
			(version == null) ? connInfo.getUpdateversionnumber() : Integer.toString( version.getUpdateNumber() ),	// updateversionnumber
			(version == null) ? connInfo.getBuildnumber() : version.getBuildNumber(),								// buildnumber
			connInfo.getManaged()					// managed
			);
	}
	
	protected void loadContextData(JobExecutionContext context) {
		
		super.loadContextData(context);
		
		nodes = (List<NodeRegistrationInfo>)context.getJobDetail().getJobDataMap().get(TAG_NODES);
		
		if (context.getJobDetail().getJobDataMap().get(TAG_SERVICE) instanceof NodeServiceImpl) {
			nodeService = (NodeServiceImpl)context.getJobDetail().getJobDataMap().get(TAG_SERVICE);
		}
	}
	
	protected boolean validateContextData() {
		
		boolean result = super.validateContextData();
		
		if(!result){
			logger.error("job id is null.");
			return false;
		}
		
		if (nodes == null || nodes.size() == 0) {
			logger.debug("There is no nodes to import.");
			return false;
		}

		logger.debug("Import linux node" + nodes.size() + " nodes.");

		return true;
	}
}
