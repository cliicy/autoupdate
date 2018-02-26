package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.webservice.jni.model.JHypervPFCDataConsistencyStatus;
import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.schedulers.EdgeJob;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMStatusCode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMStatusDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMVerifyStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMVerifyStatus.CheckStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMVerifyStatus.CheckType;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.DiscoveryUtil;

/**
 * This class is to execute pre-flight-check job for Hyper-V VMs.
 */
public class VerifyHypervVMsJob extends EdgeJob {
	private static final Logger logger = Logger.getLogger(VerifyHypervVMsJob.class);
	private static final String TAG_NODES = "Nodes";
	private static NodeServiceImpl nodeService = new NodeServiceImpl();
	Map<String, List<JHypervVMInfo>> hyperVMInfoCache = new HashMap<String, List<JHypervVMInfo>>();
	private int[] nodeIDs;

	public JobDetail createJobDetail(int[] nodeIDs) {
		JobDetail jobDetail = new JobDetailImpl(getClass().getSimpleName() + getId(), null, getClass());
		super.createJobDetail(jobDetail);
		jobDetail.getJobDataMap().put(TAG_NODES, nodeIDs);
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
		trigger.setName(((JobDetailImpl)jobDetail).getName() + "_Trigger");
		importNodesScheduler.scheduleJob(jobDetail, trigger);
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		loadContextData(context);

		if (!validateContextData()) {
			return;
		}

		synchronized (hyperVMInfoCache) {
			hyperVMInfoCache.clear();
		}
		try {
			logger.info("verify " + nodeIDs.length + " Hyper-V VMs job begin.");
			writeActivityLog(Severity.Information, nodeIDs.length == 1 ? nodeIDs[0] : 0,
					EdgeCMWebServiceMessages.getMessage("verifyVMJobStart", nodeIDs.length));

			verifyAll();

			logger.info("verify Hyper-V VMs job end");
			writeActivityLog(Severity.Information, nodeIDs.length == 1 ? nodeIDs[0] : 0,
					EdgeCMWebServiceMessages.getMessage("verifyVMJobEnd"));

			SchedulerUtilsImpl.getScheduler().deleteJob(new JobKey(((JobDetailImpl)context.getJobDetail()).getName(), null));

		} catch (Exception e) {
			logger.error("Verify Hyper-V VMs falied.", e);
			// BUG 764538 2016/1/8
			// modify
//			writeActivityLog(Severity.Error, nodeIDs.length == 1 ? nodeIDs[0] : 0,
//					EdgeCMWebServiceMessages.getMessage("verifyVMJobFailed", e.getMessage()));
			String activityLogMsg = "";
			if (e.getCause() instanceof NullPointerException) {
				activityLogMsg = EdgeCMWebServiceMessages.getMessage("verifyVMJobFailedBecauseOfSchedulerIsShutdown");
			} else {
				activityLogMsg = EdgeCMWebServiceMessages.getMessage("verifyVMJobFailed", e.getMessage());
			}
			writeActivityLog(Severity.Error, nodeIDs.length == 1 ? nodeIDs[0] : 0, activityLogMsg);
			// end
		} finally {
			synchronized (hyperVMInfoCache) {
				hyperVMInfoCache.clear();
			}
		}

	}

	@Override
	protected void loadContextData(JobExecutionContext context) {
		super.loadContextData(context);
		if (context.getJobDetail().getJobDataMap().get(TAG_NODES) instanceof int[]) {
			nodeIDs = (int[]) context.getJobDetail().getJobDataMap().get(TAG_NODES);
		}
	}

	@Override
	protected boolean validateContextData() {
		boolean result = super.validateContextData();

		if (!result) {
			logger.error("job id is null.");
			return false;
		}

		if (nodeIDs == null || nodeIDs.length == 0) {
			logger.debug("There is no nodes to verify.");
			return false;
		}

		logger.debug("Verify " + nodeIDs.length + " nodes.");

		return true;
	}

	private void verifyAll() throws Exception {
		List<Runnable> tasks = new ArrayList<Runnable>();

		for (int nodeID : nodeIDs) {
			try {
				nodeService.hyperVDao.as_edge_hyperv_verify_status_update(nodeID, CheckStatus.WAITING.value(), "");
				tasks.add(new VerifySingleHypervVMTask(nodeService, this, nodeID));
			} catch (Exception e) {
				logger.error("verify vm failed, nodeID:" + nodeID, e);
			}
		}

		EdgeExecutors.submitAndWaitTermination(tasks);
	}

	protected static void writeActivityLog(Severity severity, int nodeID, String message) {
		ActivityLog log = new ActivityLog();
		log.setModule(Module.VerifyVMsJob);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(message);
		log.setNodeName(getNodeNamebyID(nodeID));

		try {
			ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
			logService.addLog(log);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected static String getNodeNamebyID(int nodeID) {
		String nodeName = "";
		try {
			if (nodeID > 0) {
				NodeDetail nodeDetail = nodeService.getNodeDetailInformation(nodeID);
				if (nodeDetail != null) {
					nodeName = nodeDetail.getHostname();
				}
				if (nodeName == null || nodeName.isEmpty()) {
					nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", nodeDetail.getVmName());
				}
			}
		} catch (EdgeServiceFault e) {
			logger.error("getNodeNamebyID() error", e);
		}
		return nodeName;
	}
	
	/**
	 * Get node displaying name.
	 * 
	 * @param nodeID
	 * @return host name if it has, return "VM:{VM name}" if it does not have
	 *         host name.
	 */
	protected static String getNodeDisplayNameByID(int nodeID) {
		String nodeName = "";
		try {
			if (nodeID > 0) {
				NodeDetail nodeDetail = nodeService.getNodeDetailInformation(nodeID);
				if (nodeDetail != null) {
					nodeName = nodeDetail.getHostname();
				}
				if (nodeName == null || nodeName.isEmpty()) {
					nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", nodeDetail.getVmName());
				}
			}
		} catch (EdgeServiceFault e) {
			logger.error("getNodeNamebyID() error", e);
		}
		return nodeName;
	}

}

class VerifySingleHypervVMTask implements Runnable {
	private int nodeID;
	private static final Logger logger = Logger.getLogger(VerifySingleHypervVMTask.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	private IRemoteNativeFacade nativeFacade = null;
//	private static com.ca.arcflash.webservice.jni.NativeFacade D2D_NATIVEFACADE = null;
	private NodeServiceImpl nodeService;
	private VerifyHypervVMsJob wrapper;
	
	public VerifySingleHypervVMTask(NodeServiceImpl nodeService, VerifyHypervVMsJob wrapper, int nodeId) {
		this.nodeID = nodeId;
		this.wrapper = wrapper;
		this.nodeService = nodeService;
	}
	
	private IRemoteNativeFacade getNativeFacade( GatewayId gatewayId )
	{
		if (this.nativeFacade == null)
		{
			this.nativeFacade =
				remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
		}
		return this.nativeFacade;
	}

	@Override
	public void run() {
		try {
			verifySingle(nodeID);
		} catch (Exception e) {
			logger.error("verify single Hyper-V vm failed. ID:" + nodeID, e);
			VerifyHypervVMsJob.writeActivityLog(Severity.Error, nodeID,
					EdgeCMWebServiceMessages.getMessage("verifyVMFailed", e.getMessage()));
			nodeService.hyperVDao.as_edge_hyperv_verify_status_update(nodeID, CheckStatus.INVALID.value(), "");
		}
	}

	private void verifySingle(int nodeId) throws Exception {
		EdgeHyperVHostMapInfo vmMapInfo = null;
		List<VMStatusDetail> infos = new LinkedList<VMStatusDetail>();

		// Update nodes verify status to checking...
		nodeService.hyperVDao.as_edge_hyperv_verify_status_update(nodeId, CheckStatus.CHECKING.value(), "");

		// Get Hyper-V VM information
		List<EdgeHyperVHostMapInfo> hostMapInfo = new ArrayList<EdgeHyperVHostMapInfo>(1);
		nodeService.hyperVDao.as_edge_hyperv_host_map_getById(nodeId, hostMapInfo);
		if (hostMapInfo.size() < 1) {
			logger.error("cannot get Hyper-V vm information , skip nodeID:" + nodeId);
			return;
		} else {
			vmMapInfo = hostMapInfo.get(0);
		}
		
		// get hyper-v server connection information
		DiscoveryHyperVOption hyperVOption = nodeService.getVMNodeHyperVSettings(nodeId);

		// Skip deleted VM
		if (vmMapInfo.getStatus() == VMStatus.DELETED.getValue()) {
			VMVerifyStatus result = new VMVerifyStatus();
			result.setStatus(CheckStatus.ERROR.value());
			List<VMStatusDetail> info = new LinkedList<VMStatusDetail>();
			VMStatusDetail detail = new VMStatusDetail();
			detail.setCheckType(CheckType.HYPERV_SERVER);
			detail.setStatus(VMStatusCode.ERROR);
			detail.setErrorCode(VMStatusCode.ERROR_VM_Deleted);
			String serverName = vmMapInfo.getHypervHost();
			if (serverName == null && hyperVOption != null) {
				serverName = hyperVOption.getServerName();
			}
			detail.setParameters(new String[] { serverName });
			info.add(detail);
			result.setDetails(info);
			nodeService.hyperVDao.as_edge_hyperv_verify_status_update(nodeId, CheckStatus.ERROR.value(),
					CommonUtil.marshal(result));
			VerifyHypervVMsJob.writeActivityLog(Severity.Information, nodeId,
					EdgeCMWebServiceMessages.getMessage("verifyVMSkipped"));
			return;
		}

		JHypervVMInfo discoveryVMInfo = null;
		try {
			discoveryVMInfo = getDiscoveryVMInfo(nodeId, vmMapInfo.getVmInstanceUuid(), hyperVOption);
		} catch (EdgeServiceFault ex) {
			logger.error("connect to Hyper-V server failed.", ex);
			VMVerifyStatus result = new VMVerifyStatus();
			result.setStatus(CheckStatus.ERROR.value());
			List<VMStatusDetail> tempInfos = new LinkedList<VMStatusDetail>();
			VMStatusDetail detail = new VMStatusDetail();
			detail.setCheckType(CheckType.HYPERV_SERVER);
			detail.setStatus(VMStatusCode.ERROR);
			detail.setErrorCode(VMStatusCode.ERROR_HYPERV_Connect_Failed);
			tempInfos.add(detail);
			result.setDetails(tempInfos);
			nodeService.hyperVDao.as_edge_hyperv_verify_status_update(nodeId, CheckStatus.ERROR.value(),
					CommonUtil.marshal(result));
			return;
		}
		
		if (discoveryVMInfo == null) {
			logger.error("Failed to get information about the Hyper-V VM[InstanceUUID=" + vmMapInfo.getVmInstanceUuid() + ", VMName="
					+ vmMapInfo.getVmName() + "] from server " + hyperVOption.getServerName());
			VMVerifyStatus result = new VMVerifyStatus();
			result.setStatus(CheckStatus.ERROR.value());
			List<VMStatusDetail> info = new LinkedList<VMStatusDetail>();
			VMStatusDetail detail = new VMStatusDetail();
			detail.setCheckType(CheckType.HYPERV_SERVER);
			detail.setStatus(VMStatusCode.ERROR);
			detail.setErrorCode(VMStatusCode.ERROR_VM_Deleted);
			detail.setParameters(new String[] { hyperVOption.getServerName() });
			info.add(detail);
			result.setDetails(info);
			nodeService.hyperVDao.as_edge_hyperv_verify_status_update(nodeId, CheckStatus.ERROR.value(),
					CommonUtil.marshal(result));
			VerifyHypervVMsJob.writeActivityLog(Severity.Information, nodeId,
					EdgeCMWebServiceMessages.getMessage("verifyVMSkipped"));
			return;
		}
		
		boolean isWindows = false;
		if (discoveryVMInfo.getVmGuestOS() != null && discoveryVMInfo.getVmGuestOS().contains("Windows")) {
			isWindows = true;
		}
		vmMapInfo = updateVMHostInfo(vmMapInfo, discoveryVMInfo, hyperVOption.getGatewayId());

		// Do PFC
		infos.add(checkHyperVAdminFolder(hyperVOption)); // $admin check
		infos.add(checkIntegrationServiceState(discoveryVMInfo)); // Integration Service check
		VMStatusDetail ret = checkVMPowerState(discoveryVMInfo); // Power state check
		infos.add(ret);
		
		if (ret.getStatus() == VMStatusCode.OK) { // power on
			JHypervPFCDataConsistencyStatus dcStatus = getDataConsistencyStatus(hyperVOption, vmMapInfo);
			infos.addAll(checkDiskAndDataConsistencyState(dcStatus)); // Data Consistency and Disk check
			
			//Only VM is power on , we do credential check
			if (isWindows) {
				ret = checkCredential(vmMapInfo, dcStatus);
//				infos.add(ret); //defect 175114, remove credential check
			}
		} else {
			VMStatusDetail diskStatus = new VMStatusDetail();
			diskStatus = new VMStatusDetail();
			diskStatus.setCheckType(CheckType.DISK_INFO);
			diskStatus.setStatus(VMStatusCode.WARNING);
			diskStatus.setErrorCode(VMStatusCode.WARNING_Credential_No_Check);
			infos.add(diskStatus);
			
			VMStatusDetail dcStatus = new VMStatusDetail();
			dcStatus.setCheckType(CheckType.HYPERV_DATACONSISTENCY);
			dcStatus.setStatus(VMStatusCode.WARNING);
			dcStatus.setErrorCode(VMStatusCode.WARNING_Credential_No_Check);
			infos.add(dcStatus);
			
			//defect 175114, remove credential check
//			if (isWindows) {
//				VMStatusDetail credentialStatus = new VMStatusDetail();
//				credentialStatus.setCheckType(CheckType.CREDENTIAL);
//				credentialStatus.setStatus(VMStatusCode.WARNING);
//				credentialStatus.setErrorCode(VMStatusCode.WARNING_Credential_No_Check);
//				infos.add(credentialStatus);
//			}
		}

		// Update nodes verify status to check result
		int totalStatus = 0;
		for (VMStatusDetail detail : infos) {
			totalStatus |= detail.getStatus();
		}
		if ((totalStatus & VMStatusCode.ERROR) == VMStatusCode.ERROR) {
			totalStatus = CheckStatus.ERROR.value();
		} else if ((totalStatus & VMStatusCode.WARNING) == VMStatusCode.WARNING) {
			totalStatus = CheckStatus.WARNING.value();
		} else {
			totalStatus = CheckStatus.OK.value();
		}
		VMVerifyStatus result = new VMVerifyStatus();
		result.setStatus(totalStatus);
		result.setDetails(infos);
		nodeService.hyperVDao.as_edge_hyperv_verify_status_update(nodeId, totalStatus, CommonUtil.marshal(result));

		String nodeName = VerifyHypervVMsJob.getNodeDisplayNameByID(nodeId);
		VerifyHypervVMsJob.writeActivityLog(Severity.Information, nodeId,
				EdgeCMWebServiceMessages.getMessage("verifyVMComplete", nodeName));

	}

	private List<JHypervVMInfo> getDiscoveryVMInfoList(DiscoveryHyperVOption hypervOption) throws EdgeServiceFault {
		String serverName = hypervOption.getServerName();
		List<JHypervVMInfo> newInfoList = null;
		
		synchronized (wrapper.hyperVMInfoCache) {
			newInfoList = wrapper.hyperVMInfoCache.get(serverName);
		}
		
		if (newInfoList == null || newInfoList.isEmpty()) {
			newInfoList = HyperVServerConnector.getInstance().getHypervVMInfoList(
				hypervOption.getGatewayId(), serverName, hypervOption.getUsername(), hypervOption.getPassword(), !hypervOption.isCluster());  // fix TFS Bug 762909:HBBU PFC error not find the vm in hyperV in CSV if import using IP and vm as standalone
			synchronized (wrapper.hyperVMInfoCache) {
				wrapper.hyperVMInfoCache.put(serverName, newInfoList);
			}
		}
		
		return newInfoList;
	}

	private JHypervVMInfo getDiscoveryVMInfo(int nodeId, String vmGuid, DiscoveryHyperVOption hypervOption)
			throws EdgeServiceFault {
		List<JHypervVMInfo> infoList = getDiscoveryVMInfoList(hypervOption);
		if (infoList == null || infoList.size() == 0) {
			return null;
		}

		for (JHypervVMInfo info : infoList) {
			if (vmGuid != null && vmGuid.equals(info.getVmUuid())) {
				return info;
			}
		}

		return null;
	}

	private EdgeHyperVHostMapInfo updateVMHostInfo(EdgeHyperVHostMapInfo mapInfo, JHypervVMInfo discoveryInfo,GatewayId gatewayId) {
		if (mapInfo == null || discoveryInfo == null) {
			return mapInfo;
		}
		String msg = " failed update virtual machine %s( %s )'s host infomation ";

		String instanceUUID = mapInfo.getVmInstanceUuid();
		String vmName = discoveryInfo.getVmName();
		String vmUUID = mapInfo.getVmUuid();
		int hostId = mapInfo.getHostId();

		try {
			String serverName = mapInfo.getHypervHost();
			if (serverName != null && !serverName.isEmpty()) {
				List<EdgeHyperVHostMapInfo> hostMapInfo = new LinkedList<EdgeHyperVHostMapInfo>();
				nodeService.hyperVDao.as_edge_hyperv_host_map_getById(hostId, hostMapInfo);
				if (hostMapInfo != null && !hostMapInfo.isEmpty()) {
					String oldServerName = hostMapInfo.get(0).getHypervHost();
					if (serverName.equals(oldServerName)) {
						serverName = null;
					} else {
						List<String> ipHostList = DiscoveryUtil.getIpAdressAndHostNames(serverName);
						if (ipHostList.contains(oldServerName)) {
							serverName = null;
						}
					}
				}
			}
			String nodeName = StringUtil.isEmptyOrNull(discoveryInfo.getVmHostName())?("vm("+vmName+")"):discoveryInfo.getVmHostName();
			logger.info("[VerifyHypervVMsJob] Update vm host map information to DB, the vm is: "+nodeName+", vminstance uuid is "+instanceUUID);
			nodeService.hyperVDao.as_edge_hyperv_host_map_update(hostId, vmName, vmUUID, instanceUUID,
					serverName, discoveryInfo.getVmGuestOS());
			
			mapInfo.setVmName(discoveryInfo.getVmName());
			mapInfo.setVmUuid(discoveryInfo.getVmUuid());
			mapInfo.setVmInstanceUuid(instanceUUID);
			mapInfo.setStatus(discoveryInfo.getVmPowerStatus());
			try{
	    		String newHostName = discoveryInfo.getVmHostName();
	    		if (newHostName!=null && !newHostName.isEmpty()){
	    			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		    		nodeService.hostMgrDao.as_edge_host_list(hostId, 1, hosts);
		    		if (hosts != null && !hosts.isEmpty()) {
		    			List<String> ipList = DiscoveryUtil.getIpAdressByHostName(newHostName);
						if (!ipList.contains(hosts.get(0).getRhostname())) {
							EdgeHost node = hosts.get(0);
							node.setRhostname(discoveryInfo.getVmHostName());
							
							String hostName = node.getRhostname();
							if(!StringUtil.isEmptyOrNull(hostName))
								hostName = hostName.toLowerCase();
							
//							List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
							List<String> fqdnNameList = new ArrayList<String>();
							if(gatewayId != null && gatewayId.isValid()){
								try {
									IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId);
									fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
								} catch (Exception e) {
									logger.error("[VcloudNodeImporter] updateVMHostInfo() get fqdn name failed.",e);
								}
							}
							String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
							
							logger.info("[VerifyHypervVMsJob] Update vm host information to DB, the vm is: "+nodeName+", vminstance uuid is "+instanceUUID);
							nodeService.hostMgrDao.as_edge_host_update(node.getRhostid(), node.getLastupdated(),
									hostName, node.getNodeDescription(), node.getIpaddress(), node.getOsdesc(),
									node.getOstype(), node.getIsVisible(), node.getAppStatus(),
									node.getServerPrincipalName(), node.getRhostType(), node.getProtectionTypeBitmap(),
									fqdnNames, new int[1]);
						}
		    		}
	    		}
	    			
	    	}catch(Exception e){
	    		logger.error(e);
	    	}
			
			return mapInfo;
		} catch (Exception e) {
			logger.error(String.format(msg, vmName, instanceUUID), e);
			return mapInfo;
		}

	}
	
	private VMStatusDetail checkIntegrationServiceState(JHypervVMInfo vmInfo) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.INTEGRATION_SERVICE);
		try {
			int ret = vmInfo.getVmInteServiceSatus();
			switch(ret){
			case -1:
			case 0:
			case 3:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_HyperVInteService_NotInstall);
				break;
			case 1:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_HyperVInteService_OutOfDate);
				break;
			case 2:
				status.setStatus(VMStatusCode.OK);
				status.setErrorCode(VMStatusCode.OK_HyperVInteService);
				break;
			case 7:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_HyperVInteService_Incompatible);
				break;
			case 12:
			case 13:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_HyperVInteService_LostCommunication);
				break;
			default:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_HyperVInteService_Unkown);
				status.setParameters(new String[]{String.valueOf(ret)});
				break;
			}
		} catch (Exception e) {
			logger.error(e);
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVInteService_Unkown);
			status.setParameters(new String[]{e.getMessage()});
		}
		
		return status;
	}

	private List<VMStatusDetail> checkDiskAndDataConsistencyState(JHypervPFCDataConsistencyStatus dcStatus) {
		List<VMStatusDetail> resultList = new ArrayList<VMStatusDetail>();
		if (dcStatus == null) {
			VMStatusDetail diskStatus = new VMStatusDetail();
			diskStatus.setCheckType(CheckType.DISK_INFO);
			diskStatus.setStatus(VMStatusCode.WARNING);
			diskStatus.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_Failed);
			resultList.add(diskStatus);
			
			VMStatusDetail appStatus = new VMStatusDetail();
			appStatus.setCheckType(CheckType.HYPERV_DATACONSISTENCY);
			appStatus.setStatus(VMStatusCode.WARNING);
			appStatus.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_Failed);
			resultList.add(appStatus);
			
			return resultList;
		}

		resultList.addAll(checkDiskState(dcStatus));
		resultList.addAll(checkDataConsistencyState(dcStatus));

		return resultList;
	}

	private List<VMStatusDetail> checkDiskState(JHypervPFCDataConsistencyStatus dcStatus) {
		List<VMStatusDetail> diskStateList = new ArrayList<VMStatusDetail>();
		
		VMStatusDetail status = checkVMPhysicalDiskState(dcStatus);
		if (status != null) {
			diskStateList.add(status);
			return diskStateList;
		}
		
		status = checkVMDiskOnRemoteShareState(dcStatus);
		if (status != null) {
			diskStateList.add(status);
			return diskStateList;
		}
		
		if (diskStateList.isEmpty()) { //succeeded to check
			status = new VMStatusDetail();
			status.setCheckType(CheckType.DISK_INFO);
			status.setStatus(VMStatusCode.OK);
			status.setErrorCode(VMStatusCode.OK_HyperVDiskInfo);
			diskStateList.add(status);
		}
		
		return diskStateList;
	}
	
	private List<VMStatusDetail> checkDataConsistencyState(JHypervPFCDataConsistencyStatus dcStatus) {
		List<VMStatusDetail> appStateList = new ArrayList<VMStatusDetail>();

		// application consistent snapshot is supported
		if (dcStatus.getIsDataConsistencyNotSupported() == 0)
		{
			VMStatusDetail status = new VMStatusDetail();
			status.setCheckType(CheckType.HYPERV_DATACONSISTENCY);
			status.setStatus(VMStatusCode.OK);
			status.setErrorCode(VMStatusCode.OK_HyperVDataConsistency);
			appStateList.add(status);
		}
		else
		// data consistent snapshot is NOT supported, check for reason
		{
			do
			{
				VMStatusDetail status = checkVMIntegrationServiceStateInBadState(dcStatus);
				if (status != null)
				{
					appStateList.add(status);
					break;
				}

				status = checkVMDiskTypeState(dcStatus);
				if (status != null)
				{
					appStateList.add(status);
					break;
				}

				status = checkVMFSTypeState(dcStatus);
				if (status != null)
				{
					appStateList.add(status);
					break;
				}

				status = checkVMScopedSnapshotState(dcStatus);
				if (status != null)
				{
					appStateList.add(status);
					break;
				}

				status = checkVMShadowStorageState(dcStatus);
				if (status != null)
				{
					appStateList.add(status);
					break;
				}

				status = checkVMStorageSpace(dcStatus);
				if (status != null)
				{
					appStateList.add(status);
					break;
				}

				// unknown reason
				status = new VMStatusDetail();
				status.setCheckType(CheckType.HYPERV_DATACONSISTENCY);
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_HyperVVMDataConsistentSnapshotNotSupported);
				appStateList.add(status);

			}
			while (false);
		}

		return appStateList;
	}
	
	private VMStatusDetail checkVMDiskTypeState(JHypervPFCDataConsistencyStatus dataConsistencyStatus) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.HYPERV_DATACONSISTENCY);

		int ret = dataConsistencyStatus.getHasNotSupportedDiskType();
		switch (ret) {
		case 0:
			return null;
		case 1:
		case 4:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVDiskType_NotSupported);
			break;
		case 2:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotGetVMbyGuid);
			break;
		case 3:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_VMnotRunning);
			break;
		case 5:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotAccessVM);
			break;
		default:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVDiskType_Unkown);
			status.setParameters(new String[] { String.valueOf(ret) });
			break;
		}

		return status;
	}
	
	private VMStatusDetail checkVMFSTypeState(JHypervPFCDataConsistencyStatus dataConsistencyStatus) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.HYPERV_DATACONSISTENCY);

		int ret = dataConsistencyStatus.getHasNotSupportedFileSystem();
		switch (ret) {
		case 0:
			return null;
		case 1:
		case 4:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVFSType_NotSupported);
			break;
		case 2:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotGetVMbyGuid);
			break;
		case 3:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_VMnotRunning);
			break;
		case 5:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotAccessVM);
			break;
		default:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVFSType_Unkown);
			status.setParameters(new String[] { String.valueOf(ret) });
			break;
		}

		return status;
	}
	
	private VMStatusDetail checkVMScopedSnapshotState(JHypervPFCDataConsistencyStatus dataConsistencyStatus) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.HYPERV_DATACONSISTENCY);

		int ret = dataConsistencyStatus.getIsScopeSnapshotEnabled();
		switch (ret) {
		case 0:
			return null;
		case 1:
		case 4:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVScopedSnapshot_Enabled);
			break;
		case 2:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotGetVMbyGuid);
			break;
		case 3:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_VMnotRunning);
			break;
		case 5:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotAccessVM);
			break;
		default:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVScopedSnapshot_Unkown);
			status.setParameters(new String[] { String.valueOf(ret) });
			break;
		}

		return status;
	}
	
	private VMStatusDetail checkVMIntegrationServiceStateInBadState(JHypervPFCDataConsistencyStatus dataConsistencyStatus) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.HYPERV_DATACONSISTENCY);

		int ret = dataConsistencyStatus.getIsIntegrationServiceInBadState();
		switch (ret) {
		case 0:
			return null;
		case 1:
		case 4:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVIntegrationService_InBad);
			break;
		case 2:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotGetVMbyGuid);
			break;
		case 3:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_VMnotRunning);
			break;
		case 5:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotAccessVM);
			break;
		default:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVInteServiceBadState_Unkown);
			status.setParameters(new String[] { String.valueOf(ret) });
			break;
		}

		return status;
	}
	
	private VMStatusDetail checkVMPhysicalDiskState(JHypervPFCDataConsistencyStatus dataConsistencyStatus) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.DISK_INFO);

		int ret = dataConsistencyStatus.getIsPhysicalHardDisk();
		switch (ret) {
		case 0:
			return null;
		case 1:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVPhysicalDisk);
			break;
		case 2:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotGetVMbyGuid);
			break;
		default:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVPhysicalDisk_Unkown);
			status.setParameters(new String[] { String.valueOf(ret) });
			break;
		}

		return status;
	}
	
	private VMStatusDetail checkVMDiskOnRemoteShareState(JHypervPFCDataConsistencyStatus dataConsistencyStatus) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.DISK_INFO);

		int ret = dataConsistencyStatus.getHasDiskOnRemoteShare();
		switch (ret) {
		case 0:
			return null;
		case 1:
			status.setStatus(VMStatusCode.ERROR);
			status.setErrorCode(VMStatusCode.WARNING_HyperVDiskOnRemoteShare);
			break;
		case 2:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotGetVMbyGuid);
			break;
		default:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVDiskOnRemoteShare_Unknow);
			status.setParameters(new String[] { String.valueOf(ret) });
			break;
		}

		return status;
	}

	private VMStatusDetail checkVMPowerState(JHypervVMInfo vmInfo) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.POWER_STATUS);
		try {
			int ret = vmInfo.getVmPowerStatus();
			switch (ret) {
			case 2:
				status.setStatus(VMStatusCode.OK);
				status.setErrorCode(VMStatusCode.OK_VMPowerState_ON);
				break;
			case 3:
			case 4:
			case 5:
			case 10:
			case 32770:
			case 32771:
			case 32773:
			case 32774:
			case 32776:
			case 32777:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_HyperVVM_PowerState_PowerOff);
				break;
			case 6:
			case 9:
			case 32768:
			case 32769:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_HyperVVM_PowerState_Suspended);
				break;
			default:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_VMPowerState_Unkown);
				status.setParameters(new String[] { String.valueOf(ret) });
				break;
			}
		} catch (Exception e) {
			logger.error("checkVMPowerState falied.", e);
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_VMPowerState_Unkown);
			status.setParameters(new String[] { e.getMessage() });
		}

		return status;

	}

	private VMStatusDetail checkCredential(EdgeHyperVHostMapInfo vmInfo, JHypervPFCDataConsistencyStatus dcStatus) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.CREDENTIAL);

		// Get connection information
		String userName = null;
		String password = null;
		String hostName = null;
		try {
			NodeDetail nodeDetail = nodeService.getNodeDetailInformation(vmInfo.getHostId());
			hostName = nodeDetail.getHostname();
			if (nodeDetail.getD2dConnectInfo() != null) {
				userName = nodeDetail.getD2dConnectInfo().getUsername();
				password = nodeDetail.getD2dConnectInfo().getPassword();
			}
		} catch (EdgeServiceFault error) {
			logger.error(error);
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVVM_Credential_Failed);
			status.setParameters(new String[] { error.getMessage() });
			return status;
		}

		// Validate user with administrator privilege
		if (userName == null || userName.isEmpty()) {
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVVM_Credential_Not_Provide);
		} else {
			if (dcStatus == null) {
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_HyperVVM_Credential_Failed);
				return status;
			}
			
			int ret = dcStatus.getIsVMCredentialNotOK();
			switch (ret) {
			case 0:
				status.setStatus(VMStatusCode.OK);
				status.setErrorCode(VMStatusCode.OK_Credential);
				break;
			case 2:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotGetVMbyGuid);
				break;
			case 3:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_VMnotRunning);
				break;
			default:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_HyperVVM_Credential_Failed);
				status.setParameters(new String[] { String.valueOf(ret) });
				break;
			}
		}
		return status;
	}
	
	
	private VMStatusDetail checkHyperVAdminFolder(DiscoveryHyperVOption hypervOption) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.HYPERV_CREDENTIAL);

		String hostName = null;
		String userName = null;
		String password = null;
		try {
			hostName = hypervOption.getServerName();
			userName = hypervOption.getUsername();
			password = hypervOption.getPassword();

			if (userName == null || userName.isEmpty()) {
				status.setStatus(VMStatusCode.ERROR);
				status.setErrorCode(VMStatusCode.ERROR_HYPERV_Credential);
			} else {
				IRemoteNativeFacade nativeFacade = this.getNativeFacade( hypervOption.getGatewayId() );
				nativeFacade.verifyHyperVAdminAccount(hostName, userName, password, hypervOption.isCluster());
				status.setStatus(VMStatusCode.OK);
				status.setErrorCode(VMStatusCode.OK_HyperVCredential);
			}
		} catch (EdgeServiceFault e) {
			logger.error(e);
			status.setStatus(VMStatusCode.ERROR);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_Credential);
			status.setParameters(new String[] { e.getMessage() });
		}
		return status;
	}
	
	private JHypervPFCDataConsistencyStatus getDataConsistencyStatus(DiscoveryHyperVOption hypervOption,
			EdgeHyperVHostMapInfo vmInfo) {
		long handle = 0;
		String hostName = null;
		String userName = null;
		String password = null;
		
		hostName = hypervOption.getServerName();
		userName = hypervOption.getUsername();
		password = hypervOption.getPassword();
		try {
			NodeDetail nodeDetail = nodeService.getNodeDetailInformation(vmInfo.getHostId());
			IRemoteNativeFacade nativeFacade = this.getNativeFacade( hypervOption.getGatewayId() );
			return nativeFacade.getHypervPFCDataConsistentStatus(hostName, userName, password,vmInfo.getVmInstanceUuid(),
					nodeDetail.getUsername(), nodeDetail.getPassword());
		} catch (Exception e) {
			logger.error("Failed to get the data consistency infomation of " + "the Hyper-V VM [" + "instanceUUID = "
					+ vmInfo.getVmInstanceUuid() + "]: ", e);
			return null;
		} 
	}
	
	private VMStatusDetail checkVMShadowStorageState(JHypervPFCDataConsistencyStatus dataConsistencyStatus) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.HYPERV_DATACONSISTENCY);

		int ret = dataConsistencyStatus.getHasShadowStorageOnDifferentVolume();
		switch (ret) {
		case 0:
			return null;
		case 1:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVVMHasShadowStorageOnDifferentVolume);
			break;
		case 2:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotGetVMbyGuid);
			break;
		case 4:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVIntegrationService_InBad);
			break;
		case 3:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_VMnotRunning);
			break;
		case 5:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotAccessVM);
			break;
		default:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVVMHasShadowStorageOnDifferentVolume_Unknown);
			status.setParameters(new String[] { String.valueOf(ret) });
			break;
		}

		return status;
	}
	
	private VMStatusDetail checkVMStorageSpace(JHypervPFCDataConsistencyStatus dataConsistencyStatus) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.HYPERV_DATACONSISTENCY);

		int ret = dataConsistencyStatus.getHasStorageSpace();
		switch (ret) {
		case 0:
			return null;
		case 1:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVVMHasStorageSpace);
			break;
		case 2:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotGetVMbyGuid);
			break;
		case 4:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVIntegrationService_InBad);
			break;
		case 3:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_VMnotRunning);
			break;
		case 5:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.ERROR_HYPERV_DC_CannotAccessVM);
			break;
		default:
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_HyperVVMHasStorageSpace_Unknown);
			status.setParameters(new String[] { String.valueOf(ret) });
			break;
		}

		return status;
	}
}

class HyperVServerConnector {
	private static HyperVServerConnector instance = new HyperVServerConnector();

	private HyperVServerConnector() {
	}

	public static HyperVServerConnector getInstance() {
		return instance;
	}

	public List<JHypervVMInfo> getHypervVMInfoList(GatewayId gatewayId, String host, String user, @NotPrintAttribute String password, boolean onlyUnderThisHyperv )
			throws EdgeServiceFault {
		IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
		IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
		return nativeFacade.GetVmList(host, user, password, onlyUnderThisHyperv/*false*/); // fix TFS Bug 762909:HBBU PFC error not find the vm in hyperV in CSV if import using IP and vm as standalone
	}

}
