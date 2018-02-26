package com.ca.arcserve.edge.app.base.webservice.node;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.ha.vmwaremanager.Disk_Info;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.powerState;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.edge.data.pfc.PFCVMInfo;
import com.ca.arcflash.webservice.edge.pfc.D2DPFCServiceImpl;
import com.ca.arcflash.webservice.edge.pfc.ID2DPFCService;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.VMConnectionContextProvider;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.schedulers.EdgeJob;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
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
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerService;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerServiceFactory;

/**
 * This class is to execute pre-flight-check job for vSphere VMs. 
 */
public class VerifyVMsJob extends EdgeJob {
	
	private static final Logger logger = Logger.getLogger(VerifyVMsJob.class);
	private static final String TAG_NODES = "Nodes";
	private static NodeServiceImpl nodeService = new NodeServiceImpl();
	private int[] nodeIDs;
	
	public JobDetail createJobDetail(int[] nodeIDs) {
		JobDetail jobDetail = new JobDetailImpl(getClass().getSimpleName()+getId(), null, getClass());
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
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		  
		loadContextData(context);
		
		if (!validateContextData()) {
			return;
		}
		
		try {
			logger.info("verify "+nodeIDs.length+" VMs job begin.");
			writeActivityLog(Severity.Information, nodeIDs.length==1?nodeIDs[0]:0, 
						EdgeCMWebServiceMessages.getMessage("verifyVMJobStart",nodeIDs.length));

			verifyAll();
			
			logger.info("verify VMs job end");
			writeActivityLog(Severity.Information, nodeIDs.length==1?nodeIDs[0]:0, 
					EdgeCMWebServiceMessages.getMessage("verifyVMJobEnd"));
			
			SchedulerUtilsImpl.getScheduler().deleteJob(new JobKey(((JobDetailImpl)context.getJobDetail()).getName(), null));
			
		} catch (Exception e) {
			logger.error("Verify VMs falied.", e);
			// BUG 764538 2016/1/8
			// modify
//			writeActivityLog(Severity.Error, nodeIDs.length==1?nodeIDs[0]:0, 
//					EdgeCMWebServiceMessages.getMessage("verifyVMJobFailed",e.getMessage()));
			String activityLogMsg = "";
			if (e.getCause() instanceof NullPointerException) {
				activityLogMsg = EdgeCMWebServiceMessages.getMessage("verifyVMJobFailedBecauseOfSchedulerIsShutdown");
			} else {
				activityLogMsg = EdgeCMWebServiceMessages.getMessage("verifyVMJobFailed", e.getMessage());
			}
			writeActivityLog(Severity.Error, nodeIDs.length == 1 ? nodeIDs[0]: 0, activityLogMsg);
			// end
			
		}
	}

	@Override
	protected void loadContextData(JobExecutionContext context) {
		
		super.loadContextData(context);
		
		if (context.getJobDetail().getJobDataMap().get(TAG_NODES) instanceof int[]) {
			nodeIDs = (int[])context.getJobDetail().getJobDataMap().get(TAG_NODES);
		}
		
	}
	
	@Override
	protected boolean validateContextData() {
		
		boolean result = super.validateContextData();
		
		if(!result){
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
			try{
				// Update status to waiting
				nodeService.esxDao.as_edge_esx_verify_status_update(
						nodeID, CheckStatus.WAITING.value(), "");
				logger.debug("[VerifyVMsJob]update esx verify status to wating, node ID:"+ nodeID);
				// Submit verify task
				tasks.add(new verifySingleVMTask(nodeService, this, nodeID));
				
			}catch(Exception e){
				logger.error("verify vm failed, nodeID:"+nodeID, e);
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

	protected static String getNodeNamebyID(int nodeID){
		String nodeName = "";
		try {
			if(nodeID > 0){
				NodeDetail nodeDetail = nodeService.getNodeDetailInformation(nodeID);
				if(nodeDetail != null){
					nodeName = nodeDetail.getHostname();	
				}
				if(nodeName == null || nodeName.isEmpty()){
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

class verifySingleVMTask implements Runnable{

	private static final String REQUIRED_D2D_VERSION = "16.0.4";
	private static final String REQUIRED_ESX_SERVER_VERSION = "4.1.0";
	private int nodeID;
	private static final Logger logger = Logger.getLogger(verifySingleVMTask.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	private NodeServiceImpl nodeService;
	private IVmwareManagerService vmwareService = null;
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);

	private static final long INVALID_CREDENTIAL_OR_NOT_ENOUGH_PRIVILEGE = -6;  // -6 for invalid credential or not enough privilege

	public verifySingleVMTask(NodeServiceImpl nodeService, VerifyVMsJob wrapper, int nodeId){
		this.nodeID = nodeId;
		this.nodeService = nodeService;
	}
	@Override
	public void run() {
		try {
			verifySingle(nodeID);
		} catch (Exception e) {
			logger.error("verify single vm failed. ID:"+nodeID,e);
			VerifyVMsJob.writeActivityLog(Severity.Error, nodeID, 
					EdgeCMWebServiceMessages.getMessage("verifyVMFailed",e.getMessage()));
			nodeService.esxDao.as_edge_esx_verify_status_update(nodeID,CheckStatus.INVALID.value(),"");
			logger.debug("[verifySingleVMTask] update esx verify status to invalid, node ID:"+ nodeID);
		}
		finally
		{
			if (vmwareService != null)
				vmwareService.close();
		}
	}
	
	private void verifySingle(int nodeId) throws Exception{

		EdgeEsxVmInfo vmInfo = null;
		List<VMStatusDetail> infos = new LinkedList<VMStatusDetail>();
		
		// Update nodes verify status to checking...
		nodeService.esxDao.as_edge_esx_verify_status_update(
				nodeId, CheckStatus.CHECKING.value(), "");
		logger.debug("update esx verify status to checking, node ID:"+ nodeId);
		// Connect to VMManager
		DiscoveryESXOption esxOption = nodeService.getVMNodeESXSettingsFromDB(nodeId);
		if(esxOption == null) {
			logger.error("[verifySingleVMTask] cannot get esx setting. skip nodeID:" + nodeId);
			return;
		}

		try{
			IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
			vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
			vmwareService.cleanCache();
			
			vmwareService.addConnection(esxOption);
		}catch(EdgeServiceFault ex){
			logger.error("connect to VMwareManager failed.",ex);
			VMVerifyStatus result = new VMVerifyStatus();
			result.setStatus(CheckStatus.ERROR.value());
			List<VMStatusDetail> infos1 = new LinkedList<VMStatusDetail>();
			VMStatusDetail detail = new VMStatusDetail();
			detail.setCheckType(CheckType.ESXSERVER);
			detail.setStatus(VMStatusCode.ERROR);
			detail.setErrorCode(VMStatusCode.ERROR_Server_Connect_Failed);
			infos1.add(detail);
			result.setDetails(infos1);
			nodeService.esxDao.as_edge_esx_verify_status_update(
					nodeId, CheckStatus.ERROR.value(), CommonUtil.marshal(result));
			logger.debug("[verifySingleVMTask] update esx verify status to error, node ID:"+ nodeId);
		}

		// Get VM information
		List<EdgeEsxVmInfo> vmList = new LinkedList<>();
		nodeService.esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(nodeId, vmList);
		
		if(vmList.size() < 1){
			logger.error("cannot get vm information , skip nodeID:" + nodeId);
			return;
		}else{
			vmInfo = vmList.get(0);
		}
		
		// Skip deleted VM
		if(vmInfo.getStatus() == VMStatus.DELETED.getValue()){
			VMVerifyStatus result = new VMVerifyStatus();
			result.setStatus(CheckStatus.ERROR.value());
			List<VMStatusDetail> info = new LinkedList<VMStatusDetail>();
			VMStatusDetail detail = new VMStatusDetail();
			detail.setCheckType(CheckType.ESXSERVER);
			detail.setStatus(VMStatusCode.ERROR);
			detail.setErrorCode(VMStatusCode.ERROR_VM_Deleted);
			String serverName = vmInfo.getEsxHost();
			if (serverName == null && esxOption != null) {
				serverName = esxOption.getEsxHost();
			}
			detail.setParameters(new String[]{serverName});
			info.add(detail);
			result.setDetails(info);
			nodeService.esxDao.as_edge_esx_verify_status_update(
					nodeId, CheckStatus.ERROR.value(), CommonUtil.marshal(result));
			logger.debug("[verifySingleVMTask] update esx verify status to error, node ID:"+ nodeId);
			VerifyVMsJob.writeActivityLog(Severity.Information, nodeId, 
					EdgeCMWebServiceMessages.getMessage("verifyVMSkipped"));
			return;
		}
		
		boolean isWindows = false;
		VM_Info vm = getVMInformation(nodeId, vmInfo, esxOption, 3);
		if (vm == null) {
			return;
		}
		
		if (vm.getvmGuestOS()!=null && vm.getvmGuestOS().contains("Microsoft"))
			isWindows = true;
		
		//fix issue 98176
		vmInfo = updateVMHostInfo(esxOption , vmInfo );
		//
		// Do pre-flight-check
		infos.add(checkAndEnableCBT(esxOption,vmInfo));//CBT check
		
		VMStatusDetail VMStatusRet = checkForHWSnaphostSupport(esxOption,vmInfo);
		if(VMStatusRet.getCheckType() == CheckType.HWSNAPSHOT_SUPPORT)
			infos.add(VMStatusRet);//HWSnapshot check
		infos.add(checkVMToolsState(esxOption,vmInfo));//VMTools check
		
		infos.addAll(checkDiskInfo(esxOption,vmInfo));//Disk check
		
		VMStatusDetail ret = checkVMPowerState(esxOption,vmInfo);//Power state check
		infos.add(ret);
		
		if (isWindows)
		{
			do 
			{
				if (ret.getStatus() != VMStatusCode.OK) // VM is not powered on
				{
					VMStatusDetail status = new VMStatusDetail();
					status.setCheckType(CheckType.APPLICATIONS);
					status.setStatus(VMStatusCode.WARNING);
					status.setErrorCode(VMStatusCode.WARNING_Credential_No_Check); // not verified because the virtual machine is not powered on.
					infos.add(status);	
					break;
				}
				
				// check credential
				ret = checkCredential(esxOption, vmInfo);
				
				if(ret.getStatus() != VMStatusCode.OK) // credential is not OK
				{
					VMStatusDetail status = new VMStatusDetail();
					status.setCheckType(CheckType.APPLICATIONS);
					status.setStatus(VMStatusCode.WARNING);
					status.setErrorCode(VMStatusCode.WARNING_Application_No_Check);
					infos.add(status);
					break;
				}
				
				// check application
				ret = checkApplications(D2DPFCServiceImpl.getInstance(),vmInfo,esxOption);
				
				// If VIX not installed in edge, we try to connect proxy d2d to check application 
				if(ret.getErrorCode() == VMStatusCode.WARNING_VIX_NOT_INSTALL||
					ret.getErrorCode() == VMStatusCode.WARNING_VIX_OUT_OF_DATE){
					
					ConnectionContext context = new VMConnectionContextProvider(vmInfo.getHostId()).create();
					try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context));) {
						connection.setRequestTimeout(3 * 60 * 1000);	// 3 minutes
						connection.connect();
						
						VersionInfo versionInfo = connection.getService().getVersionInfo();
						if (versionInfo != null) {
							String version = versionInfo.getMajorVersion()+"."+versionInfo.getMinorVersion()+"."+versionInfo.getUpdateNumber();
							if(REQUIRED_D2D_VERSION.compareTo(version) > 0){
								throw new Exception(EdgeCMWebServiceMessages.getMessage("verifyVMJobNotSupportD2DVersion"));
							}
						}
						
						ret = checkApplications(connection.getService(), vmInfo, esxOption);
					} catch (Exception e) {
						logger.error("connectD2D() failed.", e);
						VerifyVMsJob.writeActivityLog(Severity.Warning, nodeID,
								EdgeCMWebServiceMessages.getMessage("verifyVMJobConnectD2DFail", context.getHost(), e.getMessage()));
						throw e;
					}
				}
				infos.add(ret);
			}while(false);
		}
		
		/*
		if (isWindows){
			//Only VM is power on , we do credential check
			if(ret.getStatus() == VMStatusCode.OK){
				ret = checkCredential(vmInfo);
//				infos.add(ret);	// defect 153081 remove this check
			}else{
				VMStatusDetail status = new VMStatusDetail();
				status.setCheckType(CheckType.CREDENTIAL);
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_Credential_No_Check);
//				infos.add(status);	// defect 153081 remove this check
			}
	
			//Only Credential check is OK , we do Application check
			if(ret.getStatus() == VMStatusCode.OK){
				ret = checkApplications(D2DPFCServiceImpl.getInstance(),vmInfo,esxOption);
				// If VIX not installed in edge, we try to connect proxy d2d to check application 
				if(ret.getErrorCode() == VMStatusCode.WARNING_VIX_NOT_INSTALL||
					ret.getErrorCode() == VMStatusCode.WARNING_VIX_OUT_OF_DATE){
					
					ConnectionContext context = new VMConnectionContextProvider(vmInfo.getHostId()).create();
					try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context));) {
						connection.setRequestTimeout(3 * 60 * 1000);	// 3 minutes
						connection.connect();
						
						VersionInfo versionInfo = connection.getService().getVersionInfo();
						if (versionInfo != null) {
							String version = versionInfo.getMajorVersion()+"."+versionInfo.getMinorVersion()+"."+versionInfo.getUpdateNumber();
							if(REQUIRED_D2D_VERSION.compareTo(version) > 0){
								throw new Exception(EdgeCMWebServiceMessages.getMessage("verifyVMJobNotSupportD2DVersion"));
							}
						}
						
						ret = checkApplications(connection.getService(), vmInfo, esxOption);
					} catch (Exception e) {
						logger.error("connectD2D() failed.", e);
						VerifyVMsJob.writeActivityLog(Severity.Warning, nodeID,
								EdgeCMWebServiceMessages.getMessage("verifyVMJobConnectD2DFail", context.getHost(), e.getMessage()));
						throw e;
					}
				}
				infos.add(ret);
			}else{
				VMStatusDetail status = new VMStatusDetail();
				status.setCheckType(CheckType.APPLICATIONS);
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_Application_No_Check);
				infos.add(status);
			}
		}
		*/
		
		// Update nodes verify status to check result
		int totalStatus = 0;
		for (VMStatusDetail detail : infos) {
			totalStatus |= detail.getStatus();
		}
		if ((totalStatus & VMStatusCode.ERROR) == VMStatusCode.ERROR) 
		{
			totalStatus = CheckStatus.ERROR.value();
		} 
		else if ((totalStatus & VMStatusCode.WARNING) == VMStatusCode.WARNING) 
		{
			totalStatus = CheckStatus.WARNING.value();
		}
		else{
			totalStatus = CheckStatus.OK.value();
		}
		VMVerifyStatus result = new VMVerifyStatus();
		result.setStatus(totalStatus);
		result.setDetails(infos);
		nodeService.esxDao.as_edge_esx_verify_status_update(
				nodeId, totalStatus, CommonUtil.marshal(result));
		logger.debug("[verifySingleVMTask] update esx verify status to " + totalStatus +", node ID:"+ nodeId);
		String nodeName = VerifyVMsJob.getNodeDisplayNameByID(nodeId);
		VerifyVMsJob.writeActivityLog(Severity.Information, nodeId, 
				EdgeCMWebServiceMessages.getMessage("verifyVMComplete", nodeName));

	}
//	private CAVirtualInfrastructureManager connectToVMManager(int nodeId, DiscoveryESXOption esxOption)
//			throws EdgeServiceFault, JAXBException {
//		CAVirtualInfrastructureManager vmwareManager = null;
//		try{
//			synchronized (wrapper.esxServerCache) {
//				vmwareManager = wrapper.esxServerCache.get(esxOption.getEsxServerName());
//			}
//			if( vmwareManager == null){
//				vmwareManager = VMwareManagerAdapter.getInstance().createVMWareManager(esxOption);
//				synchronized (wrapper.esxServerCache) {
//					wrapper.esxServerCache.put(esxOption.getEsxServerName(), vmwareManager);
//				}
//			}
//		}catch(EdgeServiceFault ex){
//			logger.error("connect to VMwareManager failed.",ex);
//			VMVerifyStatus result = new VMVerifyStatus();
//			result.setStatus(CheckStatus.ERROR.value());
//			List<VMStatusDetail> infos = new LinkedList<VMStatusDetail>();
//			VMStatusDetail detail = new VMStatusDetail();
//			detail.setCheckType(CheckType.ESXSERVER);
//			detail.setStatus(VMStatusCode.ERROR);
//			detail.setErrorCode(VMStatusCode.ERROR_Server_Connect_Failed);
//			infos.add(detail);
//			result.setDetails(infos);
//			nodeService.esxDao.as_edge_esx_verify_status_update(
//					nodeId, CheckStatus.ERROR.value(), CommonUtil.marshal(result));
//		}
//		return vmwareManager;
//	}
	
	private VM_Info getVMInformation(int nodeId, EdgeEsxVmInfo vmInfo, DiscoveryESXOption esxOption, int times)
			throws JAXBException {
		VM_Info vm = null;
		int count = 0;
		while (count < times) {
			if (count != 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException interEx) {
				}
			}
			
			try {
				count++;
				assert vmwareService != null : "vmwareService is null.";
				vm = vmwareService.getVMInfoWithOriginal(esxOption, "", vmInfo.getVmInstanceUuid());
			} catch (Exception ex) {
				if (count < times) {
					continue;
				}
				
				if ("unable to get Vm  mor for VM.".equals(ex.getMessage())) {
					logger.error("Failed to get information about the VMware VM[InstanceUUID=" + vmInfo.getVmInstanceUuid() + ", VMName="
							+ vmInfo.getVmName() + "] from server " + esxOption.getEsxHost());
					VMVerifyStatus result = new VMVerifyStatus();
					result.setStatus(CheckStatus.ERROR.value());
					List<VMStatusDetail> info = new LinkedList<VMStatusDetail>();
					VMStatusDetail detail = new VMStatusDetail();
					detail.setCheckType(CheckType.ESXSERVER);
					detail.setStatus(VMStatusCode.ERROR);
					detail.setErrorCode(VMStatusCode.ERROR_VM_Deleted);
					String serverName = vmInfo.getEsxHost();
					if (serverName == null && esxOption != null) {
						serverName = esxOption.getEsxHost();
					}
					detail.setParameters(new String[]{serverName});
					info.add(detail);
					result.setDetails(info);
					nodeService.esxDao.as_edge_esx_verify_status_update(
							nodeId, CheckStatus.ERROR.value(), CommonUtil.marshal(result));
					logger.debug("[verifySingleVMTask] update esx verify status to error, node ID:"+ nodeId);
					VerifyVMsJob.writeActivityLog(Severity.Information, nodeId, 
							EdgeCMWebServiceMessages.getMessage("verifyVMSkipped"));
					return null;
				} else {
					logger.error("connect to vCenter/ESX server failed.", ex);
					VMVerifyStatus result = new VMVerifyStatus();
					result.setStatus(CheckStatus.ERROR.value());
					List<VMStatusDetail> tempInfos = new LinkedList<VMStatusDetail>();
					VMStatusDetail detail = new VMStatusDetail();
					detail.setCheckType(CheckType.ESXSERVER);
					detail.setStatus(VMStatusCode.ERROR);
					detail.setErrorCode(VMStatusCode.ERROR_Server_Connect_Failed);
					tempInfos.add(detail);
					result.setDetails(tempInfos);
					nodeService.esxDao.as_edge_esx_verify_status_update(nodeId, CheckStatus.ERROR.value(),
							CommonUtil.marshal(result));
					logger.debug("[verifySingleVMTask] update esx verify status to error, node ID:"+ nodeId);
					return null;
				}
			}
			
			if (vm == null) {
				if (count < times) {
					continue;
				}
				logger.error("Unable to get information about the VMware VM[InstanceUUID=" + vmInfo.getVmInstanceUuid() + ", VMName="
						+ vmInfo.getVmName() + "] from server " + esxOption.getEsxHost());
				VMVerifyStatus result = new VMVerifyStatus();
				result.setStatus(CheckStatus.ERROR.value());
				List<VMStatusDetail> tempInfos = new LinkedList<VMStatusDetail>();
				VMStatusDetail detail = new VMStatusDetail();
				detail.setCheckType(CheckType.ESXSERVER);
				detail.setStatus(VMStatusCode.ERROR);
				detail.setErrorCode(VMStatusCode.ERROR_VM_Deleted);
				String serverName = vmInfo.getEsxHost();
				if (serverName == null && esxOption != null) {
					serverName = esxOption.getEsxHost();
				}
				detail.setParameters(new String[]{serverName});
				tempInfos.add(detail);
				result.setDetails(tempInfos);
				nodeService.esxDao.as_edge_esx_verify_status_update(nodeId, CheckStatus.ERROR.value(),
						CommonUtil.marshal(result));
				logger.debug("[verifySingleVMTask] update esx verify status to error, node ID:"+ nodeId);
				return null;
			} else {
				break;
			}
		}
		return vm;
	}

	
    private EdgeEsxVmInfo  updateVMHostInfo(DiscoveryESXOption esxOption,  EdgeEsxVmInfo mapInfo ) {
    	String msg = " failed update virtual machine %s( %s )'s host infomation ";
    	
    	String vmName = mapInfo.getVmName();
    	String instanceUid = mapInfo.getVmInstanceUuid() ;
    	int hostId = mapInfo.getHostId();

    	try {
    		assert vmwareService != null : "vmwareService is null.";
    		VM_Info vm = vmwareService.getVMInfoWithOriginal(esxOption, vmName, instanceUid);
    		
			String exsHostName = vm.getvmEsxHost();
			if (exsHostName != null && !exsHostName.isEmpty()) {
				List<EdgeEsxVmInfo> vmList = new LinkedList<>();
				nodeService.esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(hostId, vmList);
				
				if (!vmList.isEmpty()) {
					String oldServerName = vmList.get(0).getEsxHost();
					if (exsHostName.equals(oldServerName)) {
						exsHostName = null;
					} else {
						List<String> ipHostList = DiscoveryUtil.getIpAdressAndHostNames(exsHostName);
						if (ipHostList.contains(oldServerName)) {
							exsHostName = null;
						}
					}
				}
			}
			String nodeName = StringUtil.isEmptyOrNull(vm.getVMHostName())?("vm("+vmName+")"):vm.getVMHostName();
			logger.info("[VerifyVMsJob] Update vm host map information to DB, the vm is: "+nodeName+", vm instanceuuid is "+vm.getVMvmInstanceUUID());
			nodeService.saveVMToDB(esxOption.getGatewayId().getRecordId(), esxOption.getId(), hostId, vm.getVMvmInstanceUUID(), vm.getVMHostName(), vmName, vm.getVMUUID(), exsHostName, vm.getVMVMX(), vm.getvmGuestOS(),
					"", "", 0, 0, 0, "", false);
			
			mapInfo.setVmName(vm.getVMName());
			mapInfo.setVmUuid(vm.getVMUUID());
			mapInfo.setVmInstanceUuid(vm.getVMvmInstanceUUID());
			mapInfo.setEsxHost(vm.getvmEsxHost());
			mapInfo.setVmXPath(vm.getVMVMX());
			
			try{
	    		String newHostName = vm.getVMHostName();
	    		if (newHostName !=null && !newHostName.isEmpty()){
	    			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		    		nodeService.hostMgrDao.as_edge_host_list(hostId, 1, hosts);
		    		if (hosts != null && !hosts.isEmpty()) {
		    			List<String> ipList = DiscoveryUtil.getIpAdressByHostName(newHostName);
						if (!ipList.contains(hosts.get(0).getRhostname())) {
							EdgeHost node = hosts.get(0);
							node.setRhostname(vm.getVMHostName());
							
							String hostName = node.getRhostname();
							if(!StringUtil.isEmptyOrNull(hostName))
								hostName = hostName.toLowerCase();
							
//							List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
							List<String> fqdnNameList = new ArrayList<String>();
							if(esxOption.getGatewayId() != null && esxOption.getGatewayId().isValid()){
								try {
									IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( esxOption.getGatewayId());
									fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
								} catch (Exception e) {
									logger.error("[VerifyVMsJob] updateVMHostInfo() get fqdn name failed.",e);
								}
							}
							String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
							
							logger.info("[VerifyVMsJob] Update vm host information to DB, the vm is: "+nodeName+", vm instanceuuid is "+vm.getVMvmInstanceUUID());
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
			logger.error( String.format( msg,  vmName, instanceUid ),e );
			return mapInfo;
		}
  
    }
	private VMStatusDetail checkVMPowerState(DiscoveryESXOption esxOption,EdgeEsxVmInfo vmInfo) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.POWER_STATUS);
		try {
			assert vmwareService != null : "vmwareService is null.";
			powerState ret = vmwareService.getVMPowerStatus(esxOption, vmInfo.getVmName(), vmInfo.getVmInstanceUuid());			switch(ret){
			case poweredOn:
				status.setStatus(VMStatusCode.OK);
				status.setErrorCode(VMStatusCode.OK_VMPowerState_ON);
				break;
			case poweredOff:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_VMPowerState_PowerOff);
				break;
			case suspended:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_VMPowerState_Suspended);
				break;
			default:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_VMPowerState_Unkown);
				status.setParameters(new String[]{String.valueOf(ret)});
				break;
			}
		} catch (Exception e) {
			logger.error("checkVMPowerState falied.",e);
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_VMPowerState_Unkown);
			status.setParameters(new String[]{e.getMessage()});
		}
		
		return status;
		
	}

	private VMStatusDetail checkApplications(ID2DPFCService D2DService, EdgeEsxVmInfo vmInfo, DiscoveryESXOption esxOption) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.APPLICATIONS);
		try {
			assert vmwareService != null : "vmwareService is null.";
			
			// Get VMX info
			//VM_Info vmVMXInfo = vmwareService.getVMInfo(esxOption, vmInfo.getVmName(), vmInfo.getVmUuid());
			VM_Info vmVMXInfo = vmwareService.getVMInfoWithOriginal(esxOption, vmInfo.getVmName(), vmInfo.getVmInstanceUuid());
			String vmVMX = vmVMXInfo.getVMVMX();

			// Check VIX version
			String esxVersion = vmwareService.getEsxVersion(esxOption);
			
			String[] versions = esxVersion.split("\\.");
	        int majorVersion = Integer.parseInt(versions[0]);
	        
	        if (majorVersion < 5){
	        	// Check VIX version
				long retCode = D2DService.checkVIXVersion();
				if (retCode == ID2DPFCService.PFC_CHECK_APP_VIX_NOT_INSTALL)
				{
					status.setStatus(VMStatusCode.WARNING);
					status.setErrorCode(VMStatusCode.WARNING_VIX_NOT_INSTALL);
					return status;
				}
				else if (retCode == ID2DPFCService.PFC_CHECK_APP_VIX_OUT_OF_DATE )
				{
					status.setStatus(VMStatusCode.WARNING);
					status.setErrorCode(VMStatusCode.WARNING_VIX_OUT_OF_DATE);
					return status;
				}
	        }
			
			// Check VMtools status
			VMStatusDetail vmtoolsStatus = checkVMToolsState(esxOption,vmInfo);
			if(vmtoolsStatus.getStatus() != VMStatusCode.OK){
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_Application_VMTools_Invalid);
				return status;				
			}
			
			// Check Applications info 
			String esxServerURL = getESXServerURL(esxOption);
			PFCVMInfo result = D2DService.getVMInformation( 
					esxServerURL,esxOption.getEsxUserName(),
					esxOption.getEsxPassword(),vmInfo.getVmName(), vmVMX, 
					vmInfo.getUserName(), vmInfo.getPassword());
			if(result.getErrorCode() < 0)
			{
				logger.error( "esxServerURL: " + esxServerURL + ", EsxUserName: " + esxOption.getEsxUserName() +
					", vmName: " + vmInfo.getVmName() + ", vmVMX: " + vmVMX + ", vmUsername: " + vmInfo.getUserName() + "." );
				logger.error("getVMInformation Failed , retCode="+result.getErrorCode());
				
				// invalid credential or not enough privilege
				if (result.getErrorCode() == INVALID_CREDENTIAL_OR_NOT_ENOUGH_PRIVILEGE) 
				{
					status.setStatus(VMStatusCode.WARNING);
					status.setErrorCode(VMStatusCode.WARNING_Application_No_Check);
				}
				else // other errors
				{					
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_Application_Failed);
				}			
				
				return status;
			}
			else
			{
				boolean isSQLInstalled = result.isSqlserverInstalled();
				boolean isExchangeInstalled = result.isExchangeInstalled();
				boolean hasDynamicDisk = result.isHasDynamicDisk();
				String vmOSVersion = result.getVmOSVersion();
				// Check for target OS version limitations (windows2008/win7/vista)
				// now we check Data Consistency, not Application Consistency, so not need SQL or Exchange installed
				if (isTargetOS(vmOSVersion)) { //&& (isSQLInstalled || isExchangeInstalled)) { 
					esxVersion = null;
					esxVersion = vmwareService.getEsxVersion(esxOption);
					if (esxVersion != null && !esxVersion.isEmpty()) {
						// if ESX server version is 4.0 or less, not support
						// application restore in these OS version.
						if (REQUIRED_ESX_SERVER_VERSION.compareTo(esxVersion) > 0) {
							status.setStatus(VMStatusCode.WARNING);
							status.setErrorCode(VMStatusCode.WARNING_ESXVersion_Not_Support);
							return status;
						}
						// if ESX server version is 4.1 or later, we need to
						// check for IDE disk/SCSI slots/dynamic disk limitations
						else {
							// Check for IDE disk
							if (vmwareService.isVMHasIDEDisks(esxOption, vmInfo.getVmName(),vmInfo.getVmInstanceUuid())) {
								status.setStatus(VMStatusCode.WARNING);
								status.setErrorCode(VMStatusCode.WARNING_VMDisk_IDE_WindowsOS);
								return status;
							}
							//check SATA disk
							if (vmwareService.isVMHasSATADisks(esxOption,vmInfo.getVmName(),vmInfo.getVmInstanceUuid())) {
								status.setStatus(VMStatusCode.WARNING);
								status.setErrorCode(VMStatusCode.WARNING_VMDisk_SATA_WindowsOS);
								return status;
							}
							// Check for SCIS slot
							if (!vmwareService.isVMHasSCSISlots(esxOption, vmInfo.getVmName(),vmInfo.getVmInstanceUuid())) {
								status.setStatus(VMStatusCode.WARNING);
								status.setErrorCode(VMStatusCode.WARNING_VMDisk_Slot_Invalid);
								return status;
							}
							// Check for dynamic disk
							if(hasDynamicDisk){
								status.setStatus(VMStatusCode.WARNING);
								status.setErrorCode(VMStatusCode.WARNING_Has_Dynamic_Disk);
								return status;
							}
						}
					}
				}
				
				// check for win8 storage spaces limitation
				if(result.isHasStorageSpaces()){
					status.setStatus(VMStatusCode.WARNING);
					status.setErrorCode(VMStatusCode.WARNING_Has_StorageSpaces_Disk);
					return status;
				}
				
				if(isSQLInstalled && isExchangeInstalled){
						status.setStatus(VMStatusCode.OK);
						status.setErrorCode(VMStatusCode.OK_Application_Both);						
				}else if(isSQLInstalled){
					status.setStatus(VMStatusCode.OK);
					status.setErrorCode(VMStatusCode.OK_Application_SQLServer);						
				}else if(isExchangeInstalled){
					status.setStatus(VMStatusCode.OK);
					status.setErrorCode(VMStatusCode.OK_Application_Exchange);						
				}else{
					status.setStatus(VMStatusCode.OK);
					status.setErrorCode(VMStatusCode.OK_Application_None);						
				}
			}
		} catch (Exception e) {
			logger.error("checkApplications failed",e);
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_Application_Failed);
			status.setParameters(new String[]{e.getMessage()});
		}
		
		return status;
		
	}

	/**
	 * Validate username and password. 
	 * @return 
	 */
	private VMStatusDetail checkCredential(DiscoveryESXOption esxOption, EdgeEsxVmInfo vmInfo) {
		
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.CREDENTIAL);
		
		// Get connection information
		NodeDetail nodeDetail = null;
		String userName = null;
		String password = null;
		try {
			nodeDetail = nodeService.getNodeDetailInformation(vmInfo.getHostId());
			if (nodeDetail.getD2dConnectInfo() != null) {
				userName = nodeDetail.getD2dConnectInfo().getUsername();
				password = nodeDetail.getD2dConnectInfo().getPassword();
			}
		} catch (EdgeServiceFault error) {
			logger.error(error);
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_Credential_Failed);
			status.setParameters(new String[]{error.getMessage()});
			return status;
		}

		// Validate user with administrator privilege
		if(userName == null || userName.isEmpty())
		{
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_Credential_Not_Provide);
		}else{
			try
			{
				String esxVersion = vmwareService.getEsxVersion(esxOption);
				String[] versions = esxVersion.split("\\.");
				int majorVersion = Integer.parseInt(versions[0]);

				logger.info("ESX Version = " + esxVersion);

				if (majorVersion >= 5)
				{
					// check credential through VMware webservice
					int ret = vmwareService.validateVMCredential(esxOption, vmInfo.getVmName(), vmInfo.getVmInstanceUuid(), userName, password);
					logger.info("validateVMCredential ret = " + ret + " vmName = " + vmInfo.getVmName() + " vmUuid = " + vmInfo.getVmInstanceUuid());
					if (ret == 0)
					{
						status.setStatus(VMStatusCode.OK);
						status.setErrorCode(VMStatusCode.OK_Credential);
					}
					else
					{
						status.setStatus(VMStatusCode.WARNING);
						status.setErrorCode(VMStatusCode.WARNING_Credential_Failed);
					}
				}
				else
				{
					// use admin$
//					IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
//					GatewayEntity gateway = gatewayService.getGatewayByHostId(nodeDetail.getId());
//					nodeService.validateAdminAccount(gateway.getId(), hostName, userName, password);
					
					// the checkApplications will handle the credential error
					status.setStatus(VMStatusCode.OK);
					status.setErrorCode(VMStatusCode.OK_Credential);
				}
				
			}
			catch (Exception e)
			{
				logger.error(e);
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_Credential_Failed);
				status.setParameters(new String[]{e.getMessage()});
			}
		}
		
		return status;
		
	}
	
	/**
	 * Check VM disk information. 
	 */
	private List<VMStatusDetail> checkDiskInfo(DiscoveryESXOption esxOption, EdgeEsxVmInfo vmInfo) {
		List<VMStatusDetail> result = new ArrayList<VMStatusDetail>();
		try {
			
			assert vmwareService != null : "vmwareService is null.";
			
			// Check for share SCSI device , return error if has share SCIS disk
			boolean hasShareDevice = vmwareService.isVMHasSharedSCSI(esxOption, vmInfo.getVmName(), vmInfo.getVmInstanceUuid());
			if(hasShareDevice){
				VMStatusDetail status = new VMStatusDetail();
				status.setCheckType(CheckType.DISK_INFO);
				status.setStatus(VMStatusCode.ERROR);
				status.setErrorCode(VMStatusCode.ERROR_SharedSCSI_Device);
				result.add(status);
				return result;
			}
			
			// Check for independent/physical disk , add them to result list as a warning 
			ArrayList<Disk_Info> disks = vmwareService.getDiskInfoForEdge(esxOption, vmInfo.getVmName(), vmInfo.getVmInstanceUuid());
			boolean isOK = true;
			for (Disk_Info disk : disks) {
				if ("physicalMode".equals(disk.getDiskCompMode())) {
					isOK = false;
					VMStatusDetail diskSkip = new VMStatusDetail();
					diskSkip.setCheckType(CheckType.DISK_INFO);
					diskSkip.setStatus(VMStatusCode.WARNING);
					diskSkip.setErrorCode(VMStatusCode.WARNING_VMDisk_Physical);
					//diskSkip.setParameters(new String[]{disk.getdiskURL()});
					result.add(diskSkip);
				} else if ("independent_nonpersistent".equals(disk.getDiskMode())
						|| "independent_persistent".equals(disk.getDiskMode())) {
					isOK = false;
					VMStatusDetail diskSkip = new VMStatusDetail();
					diskSkip.setCheckType(CheckType.DISK_INFO);
					diskSkip.setStatus(VMStatusCode.WARNING);
					diskSkip.setErrorCode(VMStatusCode.WARNING_VMDisk_Skip);
					//diskSkip.setParameters(new String[]{disk.getdiskURL()});
					result.add(diskSkip);
				} else if ("virtualMode".equals(disk.getDiskCompMode())) {
					isOK = false;
					VMStatusDetail diskSkip = new VMStatusDetail();
					diskSkip.setCheckType(CheckType.DISK_INFO);
					diskSkip.setStatus(VMStatusCode.WARNING);
					diskSkip.setErrorCode(VMStatusCode.WARNING_VMDisk_Virtual);
					//diskSkip.setParameters(new String[]{disk.getdiskURL()});
					result.add(diskSkip);
				} else if(disk.getDatastoreType() != null && disk.getDatastoreType().startsWith("NFS")){
					isOK = false;
					VMStatusDetail diskSkip = new VMStatusDetail();
					diskSkip.setCheckType(CheckType.DISK_INFO);
					diskSkip.setStatus(VMStatusCode.WARNING);
					diskSkip.setErrorCode(VMStatusCode.WARNING_VMDisk_DataStore_NFS);
					result.add(diskSkip);					
				}
				
				if (!isOK) {
					break;
				}
			}
			
			if(isOK){
				VMStatusDetail status = new VMStatusDetail();
				status.setCheckType(CheckType.DISK_INFO);
				status.setStatus(VMStatusCode.OK);
				status.setErrorCode(VMStatusCode.OK_DiskInfo);
				result.add(status);
			}
			
		} catch (Exception e) {
			logger.error(e);
			VMStatusDetail status = new VMStatusDetail();
			status.setCheckType(CheckType.DISK_INFO);
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_VMDisk_Failed);
			status.setParameters(new String[]{e.getMessage()});
			result.add(status);
		}
		return result;
	}

	/**
	 * Check VM tools state. 
	 * @return 
	 */
	private VMStatusDetail checkVMToolsState(DiscoveryESXOption esxOption, EdgeEsxVmInfo vmInfo) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.VMTOOLS);
		try {
			assert vmwareService != null : "vmwareService is null.";
			int ret = vmwareService.checkVMToolsVersion(esxOption, vmInfo.getVmName(), vmInfo.getVmInstanceUuid());
			switch(ret){
			case 0:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_VMToolsState_NotInstall);
				break;
			case 1:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_VMToolsState_OutOfDate);
				break;
			case 2:
				status.setStatus(VMStatusCode.OK);
				status.setErrorCode(VMStatusCode.OK_VMToolsState);
				break;
			default:
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_VMToolsState_Unkown);
				status.setParameters(new String[]{String.valueOf(ret)});
				break;
			}
		} catch (Exception e) {
			logger.error(e);
			status.setStatus(VMStatusCode.WARNING);
			status.setErrorCode(VMStatusCode.WARNING_VMToolsState_Unkown);
			status.setParameters(new String[]{e.getMessage()});
		}
		
		return status;
		
	}

	/**
	 * Check and Enable CBT. 
	 */
	private VMStatusDetail checkAndEnableCBT(DiscoveryESXOption esxOption, EdgeEsxVmInfo vmInfo) {
		VMStatusDetail status = new VMStatusDetail();
		status.setCheckType(CheckType.CBT);
		try {
			assert vmwareService != null : "vmwareService is null.";
			int retCode = vmwareService.checkAndEnableCBT(esxOption,vmInfo.getVmName(), vmInfo.getVmInstanceUuid());
			if(retCode == 1){
				status.setStatus(VMStatusCode.OK);
				status.setErrorCode(VMStatusCode.OK_CBT);
			}
			else if (retCode == 2){
				status.setStatus(VMStatusCode.WARNING);
				status.setErrorCode(VMStatusCode.WARNING_CBT_With_Snapshot);
			}
			else{
				status.setStatus(VMStatusCode.ERROR);
				status.setErrorCode(VMStatusCode.ERROR_CBT_Failed);
			}
		} catch (Exception e) {
			logger.error(e);
			status.setStatus(VMStatusCode.ERROR);
			status.setErrorCode(VMStatusCode.ERROR_CBT_Failed);
			status.setParameters(new String[]{e.getMessage()});
		}
		
		return status;

	}
	
	/**
	 * Check if VM has one the volume is from NetApp appliance. 
	 */
	private VMStatusDetail checkForHWSnaphostSupport(DiscoveryESXOption esxOption, EdgeEsxVmInfo vmInfo) {
		VMStatusDetail status = new VMStatusDetail();
		try {
			assert vmwareService != null : "vmwareService is null.";
			logger.info( "checkForHWSnaphostSupport for VM : " + vmInfo.getVmName() + " with with instance id: " + vmInfo.getVmInstanceUuid());
			int retCode = vmwareService.checkForHWSnaphostSupport(esxOption,vmInfo.getVmName(), vmInfo.getVmInstanceUuid());
			if(retCode == 1){
				status.setCheckType(CheckType.HWSNAPSHOT_SUPPORT);
				status.setStatus(VMStatusCode.OK);
				status.setErrorCode(VMStatusCode.OK_HWSnapshotSupport_Configure_Storage_Appliance);
			}
			else if(retCode == 2){
				status.setCheckType(CheckType.HWSNAPSHOT_SUPPORT);
				status.setStatus(VMStatusCode.OK);
				status.setErrorCode(VMStatusCode.OK_HWSnapshotSupport_Configure_Storage_Appliance_NFS);
			}
		} catch (Exception e) {
			logger.error(e);
			status.setStatus(VMStatusCode.ERROR);
			status.setErrorCode(VMStatusCode.ERROR_HWSnapshot_Check_Failed);
			status.setParameters(new String[]{e.getMessage()});
		}
		
		return status;

	}
	private boolean isTargetOS(String os) {
		if (os != null && !os.isEmpty()) {
			if (os.startsWith("Windows Server 2008")
				|| os.startsWith("Windows 7")
				|| os.startsWith("Windows Vista")
				|| os.startsWith("Windows Server Longhorn")
				|| os.startsWith("Windows 8")
				|| os.startsWith("Windows Server 2012")
				) 
			{
				return true;
			}
		}
		return false;
	}

	private String getESXServerURL(DiscoveryESXOption esxOption) {
		String serverIP = convertHostNameToIP(esxOption.getGatewayId(), esxOption.getEsxServerName());
		String protocal = "http";
		if(esxOption.getProtocol()== Protocol.Https){
			protocal = "https";
		}
		String port = "";
		if(esxOption.getPort()>0){
			port = ":"+esxOption.getPort();
		}
		//http(s)://<hostName>:<port>/sdk , this is required by VMWareVIX API VixHost_Connect
		String url = protocal+"://"+serverIP+port+"/sdk";
		return url;
	}
	
	private String convertHostNameToIP(GatewayId gatewayId, String hostName){
		String ipAddress = hostName;
		try {
			IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean( IRemoteNativeFacadeFactory.class );
			IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
			ipAddress = nativeFacade.getIpByHostName( hostName );
		} catch (UnknownHostException e) {
			logger.error( "convertHostNameToIP failed.",e);
		}
	    return ipAddress;
	}
} 