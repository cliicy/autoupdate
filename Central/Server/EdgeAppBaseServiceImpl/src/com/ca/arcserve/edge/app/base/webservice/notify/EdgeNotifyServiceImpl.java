/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.notify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.job.UDPJob;
import com.ca.arcflash.webservice.edge.data.notify.NodeNotifyInfo;
import com.ca.arcflash.webservice.edge.data.notify.NotifyMessage;
import com.ca.arcflash.webservice.edge.data.notify.NotifyMessageConstants;
import com.ca.arcflash.webservice.toedge.ID2DChangeProtocolNotify;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeProductDeployDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeTaskMonitorDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.IEdgeNotifyService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.ProductType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsConnectionInfoDao;
import com.ca.arcserve.edge.app.rps.webservice.node.RPSNodeServiceImpl;

/**
 * @author lijwe02
 * 
 */
public class EdgeNotifyServiceImpl implements IEdgeNotifyService, ID2DChangeProtocolNotify {
	
	private static Logger logger = Logger.getLogger(EdgeNotifyServiceImpl.class);
	
	static IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static IEdgeProductDeployDao productDeployDao = DaoFactory.getDao(IEdgeProductDeployDao.class);
	private static IEdgeTaskMonitorDao taskDao = DaoFactory.getDao(IEdgeTaskMonitorDao.class);
	private static NodeServiceImpl nodeService = new NodeServiceImpl();
	private static EdgeWebServiceImpl edgeWebService = new EdgeWebServiceImpl();
	private static RPSNodeServiceImpl rpsNodeService = new RPSNodeServiceImpl(edgeWebService);
	private static IEdgePolicyDao edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
	private static IEdgeJobHistoryDao edgeJobHistoryDao = DaoFactory.getDao( IEdgeJobHistoryDao.class );
	
	@Override
	public void notify(NotifyMessage message) throws EdgeServiceFault {
		if (message == null) {
			logger.error("EdgeNotifyServiceImpl.notify - the notify message is null.");
			return;
		}
		
		String type = message.getType();
		
		if (NotifyMessageConstants.TYPE_D2D_VERSION.equals(type)) {
			notifyVersion(message);
		}else if(NotifyMessageConstants.TYPE_JOB_SCHEDULE.equals(type)){//schedule job message
			notifyJobSchedule(message);
		}
	}
	
	private void notifyVersion(NotifyMessage message){
		try {
			String uuid = message.getParameter(NotifyMessageConstants.KEY_UUID);
			if (StringUtil.isEmptyOrNull(uuid)) {
				logger.error("EdgeNotifyServiceImpl.notify - uuid is null or empty.");
				return;
			}
			
			String productName = message.getParameter(NotifyMessageConstants.KEY_PRODUCT_NAME);
			if (StringUtil.isEmptyOrNull(productName)) {
				logger.error("EdgeNotifyServiceImpl.notify - product name is null or empty.");
				return;
			}
			
			String majorVersion = message.getParameter(NotifyMessageConstants.KEY_D2D_MAJOR_VERSION);
			String minorVersion = message.getParameter(NotifyMessageConstants.KEY_D2D_MINOR_VERSION);
			String buildNumber = message.getParameter(NotifyMessageConstants.KEY_D2D_BUILD_NUMBER);
			String updateVersionNumber = message.getParameter(NotifyMessageConstants.KEY_D2D_UPDATE_VERSION_NUMBER);
			
			if (StringUtil.isEmptyOrNull(majorVersion) || StringUtil.isEmptyOrNull(minorVersion)) {
				logger.error("EdgeNotifyServiceImpl.notify - major/minor version is null or empty.");
				return;
			}
			
			int agentId = 0;
			int rpsId = 0;
			if (NotifyMessageConstants.PRODUCT_NAME_D2D.endsWith(productName)) {
				agentId = getD2DHostId(uuid);
				if(agentId != 0){
					connectionInfoDao.as_edge_connect_info_update_version(agentId, majorVersion, minorVersion, updateVersionNumber, buildNumber);
					logger.info("[EdgeNotifyServiceImpl] notify() Update Agent version succeed. the agent id is: "+agentId
							+" the agent uuid is: "+uuid+" the version info is: "+majorVersion+"."+minorVersion+"."+buildNumber+"("+"Update"+updateVersionNumber+")");
				}else {
					logger.info("[EdgeNotifyServiceImpl] notify() Can not find the Agent according to the uuid: "+uuid);
				}
				
			} else if (NotifyMessageConstants.PRODUCT_NAME_RPS.endsWith(productName)) {
				rpsId = getRpsHostId(uuid);
				if(rpsId != 0){
					connectionInfoDao.as_edge_connect_info_update_version(rpsId, majorVersion, minorVersion, updateVersionNumber, buildNumber);
					logger.info("[EdgeNotifyServiceImpl] notify() update Rps version succeed. the agent id is: "+rpsId
							+" the version info is: "+majorVersion+"."+minorVersion+"."+buildNumber+"("+"Update"+updateVersionNumber+")");
				}else {
					logger.info("[EdgeNotifyServiceImpl] notify() Can not find the Rps according to the uuid: "+uuid);
				}
			}
			
			String hostname = message.getParameter(NotifyMessageConstants.KEY_AGENT_HOSTNAME);
			String ipAddress = message.getParameter(NotifyMessageConstants.KEY_AGENT_IP);
			String statusString = message.getParameter(NotifyMessageConstants.KEY_AGENT_REBOOT_STATUS);
			
			if (!StringUtil.isEmptyOrNull(uuid)) {
				logger.info("[EdgeNotifyServiceImpl] notify(): Rebooting notification, hostname = " + hostname + " ip = " + ipAddress + " haveBeenRebooted = " + statusString + " UUID = " + uuid);
				List<DeployTargetDetail> detailList = new ArrayList<DeployTargetDetail>();
				productDeployDao.as_edge_deploy_target_query_by_uuid(uuid, detailList);
				IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean( IEdgeGatewayLocalService.class );
				for (DeployTargetDetail detail : detailList) {
					GatewayEntity gateway = gatewayService.getGatewayByHostId( detail.getNodeID() );
					detail.setGatewayId( gateway.getId() );
					EdgeHost host = HostInfoCache.getInstance().getHostInfo(detail.getNodeID());
					if(host.getRhostname() != null && !host.getRhostname().equals(""))
						detail.setServerName(host.getRhostname());
					DeleteNeedRebootMessageAndUpdateNode(detail,statusString);
				}
			}
			//Update same name machine's connect information
			EdgeCommonUtil.updateSameNameMachineConnectInfo(hostname,new int[]{agentId,rpsId});
		} catch (Exception e) {
			logger.error("[EdgeNotifyServiceImpl] notifyVersion() failed." , e);
		}
	}
	
	private void notifyJobSchedule(NotifyMessage message){
		try {
			Map<String, String> paramMap = message.getParamMap();
			NodeNotifyInfo nodeNotifyInfo = new NodeNotifyInfo();
			for(String className : paramMap.keySet()){
				try {
					if(Class.forName(className).newInstance() instanceof NodeNotifyInfo){
						String xmlContext = paramMap.get(className);
						nodeNotifyInfo = CommonUtil.unmarshal(xmlContext, NodeNotifyInfo.class);
						break;
					}
				} catch (Exception e) {
					logger.error("Add Next Job Schedule," + e.getMessage());
					return;
				}
			}
			if(nodeNotifyInfo == null){
				logger.error("Add Next Job Schedule, NodeNotifyInfo  can't be null!");
				return;
			}
			String nodeUuid = nodeNotifyInfo.getNodeUuid();
			String vmInstanceUuid = nodeNotifyInfo.getVmInstanceUuid();
			if((nodeUuid==null || nodeUuid.equals("")) && (vmInstanceUuid==null || vmInstanceUuid.equals(""))){
				logger.error("Add Next Job Schedule, both nodeUuid and vmInstanceUuid can't be null!");
				return;
			}
			int serverId = nodeNotifyInfo.getServerId();
			UDPJob udpJob = nodeNotifyInfo.getUdpJob();
			if(udpJob == null){
				logger.error("Add Next Job Schedule, UDPJob can't be null!");
				return;
			}
			int nodeId = 0;
			int[] hostId = new int[1];
			if(nodeUuid != null && !nodeUuid.equals("")){
				int protectionType = ProtectionType.WIN_D2D.getValue();// 1
				hostMgrDao.as_edge_host_getHostIdByUuid(nodeUuid, protectionType, hostId);
				if(hostId[0]==0){//not exist d2d node
					logger.info("Add Next Job Schedule, query d2dnode is null by nodeuuid! , nodeuuid : " + nodeUuid);
					return;
				}
				nodeId = hostId[0];
			}else{
				hostMgrDao.as_edge_host_vm_by_instanceUUID(vmInstanceUuid, hostId);
				if(hostId[0]==0){//not exist VM node
					logger.info("Add Next Job Schedule, query vmnode is null by vmInstanceUuid! , vmInstanceUuid : " + vmInstanceUuid);
					return;
				}
				nodeId = hostId[0];
			}
			long jobType = udpJob.getJobType();	
			long scheduleUTCTime = udpJob.getScheduleUTCTime();
			long scheduleUTCTimeZone = udpJob.getScheduleUTCTimeZone();
			edgeJobHistoryDao.addJobSchedule(nodeId, serverId, jobType, scheduleUTCTime, scheduleUTCTimeZone);
			logger.info("Add Next Job Schedule END");
		} catch (Exception e) {
			logger.error("[EdgeNotifyServiceImpl] notifyJobSchedule() failed. ",e);
		}
	}
	
	private void DeleteNeedRebootMessageAndUpdateNode(DeployTargetDetail detail, String rebootStatus)throws EdgeServiceFault{
		logger.info("Rebooting notification successfully." + " target id = " + detail.getTargetId() + " haveBeenRebooted = " + rebootStatus);
		if(rebootStatus == null)
			return;
		logger.info("Rebooting notification: target deploy status is " + detail.getStatus());
		if(rebootStatus.equalsIgnoreCase("true") && 
				(detail.getStatus()==DeployStatus.DEPLOY_SUCCESS_NEEDREBOOT.value()
				||detail.getStatus()==DeployStatus.DEPLOY_COMPLETE_REBOOTTIMEOUT.value())){
			// delete as_edge_deploy_target according to node id
			hostMgrDao.as_edge_deploy_target_delete_by_id(detail.getNodeID());
		}
		if(rebootStatus.equalsIgnoreCase("true") && (detail.getStatus()==DeployStatus.DEPLOY_SUCCESS_NEEDREBOOT.value()
				||detail.getStatus()==DeployStatus.DEPLOY_COMPLETE_REBOOTTIMEOUT.value()
				||detail.getStatus()==DeployStatus.DEPLOY_FAILED.value())){//Deploy failed = deploy failed on reboot
			// delete as_edge_task_monitor item according to node id and name
			taskDao.as_edge_task_monitor_delete_by_moduleAndTarget(Module.RemoteDeploy, detail.getNodeID() + "_" + detail.getServerName());
		}		
		
		// update node
		RemoteNodeInfo nodeInfo = null;
		NodeRegistrationInfo registreationNode = new NodeRegistrationInfo();
		try {
			nodeInfo = nodeService.queryRemoteNodeInfo(detail.getGatewayId(), detail.getNodeID(),
					detail.getServerName(), detail.getUsername(), detail.getPassword(),
					detail.getProtocol()==Protocol.Https ? "HTTPS": "HTTP", detail.getPort());
			registreationNode.setNodeInfo(nodeInfo);
		} catch (Exception e) {
			logger.error("Update node in notification failed, connect to D2D failed. Protocol = " + detail.getProtocol().name()
					+ " Server Name = " + detail.getServerName() + " Port = " + detail.getPort() + " User name = " + detail.getUsername());
		}
		
		if(nodeInfo != null){
			//update node
			logger.info("Update Node in notification " + detail.getServerName() + " successful get node Info:" +
						" protocol: " + nodeInfo.getD2DProtocol() +
							" port: " + nodeInfo.getD2DPortNumber() +
							" version:" + nodeInfo.getD2DMajorVersion() + nodeInfo.getD2DMinorVersion() +nodeInfo.getD2DBuildNumber() +  
							" d2dInstall:" + nodeInfo.isD2DInstalled() +
							" rpsInstall:" + nodeInfo.isRPSInstalled() + 
							" d2d uuid: " + nodeInfo.getD2DUUID() );
			registreationNode.setId(detail.getNodeID());
			registreationNode.setNodeName(detail.getServerName());
			registreationNode.setUsername(detail.getUsername());
			registreationNode.setPassword(detail.getPassword());
			registreationNode.setRegisterD2D(nodeInfo.isD2DInstalled());
			registreationNode.setD2dPort( nodeInfo.getD2DPortNumber());
			registreationNode.setD2dProtocol( nodeInfo.getD2DProtocol());
					
			//get the node description from database
			NodeDetail nodeDetail = nodeService.getNodeDetailInformation(detail.getNodeID());
			if(nodeDetail != null){
				registreationNode.setNodeDescription(nodeDetail.getNodeDescription());						
			}
			//invoke update node method
			String[] errorCodes = null;
			if (detail.getProductType() == Integer.valueOf(ProductType.ProductRPS)){
				errorCodes = rpsNodeService.updateRpsNodeNoActivityLog(false, registreationNode,true);
			} else {
				errorCodes = nodeService.updateNode(false,registreationNode,true,false,true,getLastPlanDeployReason(detail.getNodeID()),false);
			}
			
			if(null == errorCodes[0]){
				logger.debug("Update node in notification successfully, node name is " + detail.getServerName());
			} else if(EdgeServiceErrorCode.Node_D2D_Reg_Duplicate.equals(errorCodes[0])){
				// when managed by other server , force manage it.
				if(detail.getProductType() == Integer.valueOf(ProductType.ProductRPS)){
					rpsNodeService.markRpsNodeAsManagedNoActivityLog(registreationNode, true);
				}else{
					nodeService.markNodeAsManagedNoActivityLog(registreationNode, true);
				}
			} else if (EdgeServiceErrorCode.Node_D2D_Reg_Again.equals(errorCodes[0])) {
				logger.debug("Update node in notification, node register again. ");
			} else{
				logger.error("Update node in notification failed, error code is: "+errorCodes[0]);
			}
		}
	}
	
	private int getLastPlanDeployReason(int hostId){
		List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
		edgePolicyDao.getHostPolicyMap(hostId, PolicyTypes.Unified, mapList );
		if(mapList==null || mapList.size()<1){
			return PolicyDeployReasons.PolicyContentChanged;
		}
		EdgeHostPolicyMap map = mapList.get( 0 );
		return map.getDeployReason();
	}
	
	public static int getD2DHostId(String uuid) {
		int[] rhostid = new int[1];
		connectionInfoDao.as_edge_GetConnInfoByUUID(uuid, rhostid, new String[1], new int[1], new int[1]);
		return rhostid[0];
	}
	
	public static int getRpsHostId(String uuid) {
		int[] rhostid = new int[1];
		IRpsConnectionInfoDao rpsConnectionInfoDao = DaoFactory.getDao(IRpsConnectionInfoDao.class);
		rpsConnectionInfoDao.as_edge_rps_GetConnInfoByUUID(uuid, rhostid, new String[1], new int[1], new int[1]);
		return rhostid[0];
	}

	@Override
	public int flashChangeToProtocol(String flashUUID, int toProtocol) throws EdgeServiceFault {
		IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		connectionInfoDao.as_edge_connect_info_update_protocol(flashUUID, toProtocol);
		//vcmDao.as_edge_vsphere_proxy_update_protocol(flashUUID, toProtocol);
		
		int[] hostId = new int[1];
		connectionInfoDao.as_edge_GetConnInfoByUUID(flashUUID, hostId, new String[1], new int[1], new int[1]);
		if (hostId[0] > 0) {
			List<EdgeHostPolicyMap> maps = new ArrayList<EdgeHostPolicyMap>();
			DaoFactory.getDao(IEdgePolicyDao.class).getHostPolicyMap(hostId[0], PolicyTypes.Unified, maps);
			if (!maps.isEmpty()) {
				PolicyManagementServiceImpl.getInstance().redeployPolicyToNodes(Arrays.asList(hostId[0]), PolicyTypes.Unified, maps.get(0).getPolicyId());
			}
		}
		
		return 0;
	}
}
