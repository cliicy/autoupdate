package com.ca.arcflash.failover;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.ADRConfigureUtil;
import com.ca.arcflash.ha.event.VCMEvent;
import com.ca.arcflash.ha.event.VCMEventManager;
import com.ca.arcflash.ha.event.VCMVMType;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.TransServerReplicaRoot;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.model.VirtualMachineInfo;
import com.ca.arcflash.ha.model.manager.HeartBeatModelManager;
import com.ca.arcflash.ha.model.manager.VMInfomationModelManager;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.jobscript.alert.AlertType;
import com.ca.arcflash.jobscript.failover.DNSUpdaterParameters;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.Gateway;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.IPAddressInfo;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.Protocol;
import com.ca.arcflash.repository.RepositoryManager;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.IPSettingDetail;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;
import com.ca.ha.webservice.jni.HyperVMountDisk;
import com.ca.ha.webservice.jni.HyperVMountParam;

public class HyperVFailoverCommand extends PreFailoverCommand {
	private static final Logger log = Logger
			.getLogger(HyperVFailoverCommand.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -5508937673663162159L;
	private static final int ADD_NIC_TIME = 3 * 1000;

	@Override
	public int executeFailover(String jobID, FailoverJobScript jobScript,
			String sessionGuid, boolean isAutoFailover) throws Exception {
		int result = 0;
		try {
			
			Date startTime = new Date();
			if(isAutoFailover) {
				log.debug("Miss heart beat of source machine");
				sessionGuid = null;
			}
			
			long foJobID = BackupService.getInstance().getNativeFacade().getJobID();
			result = doRealFailover(jobScript, sessionGuid, isAutoFailover, foJobID);
			
			//send the alert email
			if(result == 0) {
				
				String hyperVHostName = "Hyper-V";
				Virtualization virtualization = jobScript.getFailoverMechanism().get(0);
				if((virtualization!=null)&&(virtualization instanceof HyperV)) {
					HyperV hyperVJobscript = (HyperV) virtualization;
					hyperVHostName =String.format("Hyper-V[%s]",hyperVJobscript.getHostName());
				}
				
				AlertType alertType= AlertType.Unknown;
				if(isAutoFailover) {
					alertType = AlertType.AutoFaiover;
				}
				else {
					alertType = AlertType.MaualFaiover;
				}
				String vmName = jobScript.getFailoverMechanism().get(0).getVirtualMachineDisplayName();
				String productHostName = jobScript.getProductionServerName();
				HAService.getInstance().sendAlertMailWithParameters(jobScript.getAFGuid(), foJobID,alertType, startTime, vmName, productHostName, hyperVHostName );
			
			}
			//end
		
		} catch (Exception e) {
			throw e;
		}

		return result;
	}
	
	private int doRealFailover(FailoverJobScript jobScript,	String sessionGuid, boolean isAutoFailover, long jobID) throws Exception {
		
		TransServerReplicaRoot hostRoot;
		ProductionServerRoot prodRoot;
		try {
			prodRoot = RepositoryManager.getProductionServerRoot(jobScript.getAFGuid());
			hostRoot = (TransServerReplicaRoot)prodRoot.getReplicaRoot();
		} catch (Exception e) {
			log.error("Failed in retrieving vmuuid from respository.xml for failover." + e.getMessage(),e);
			return 1;
		}
		
		HAService.getInstance().activityLogForStartFailover(jobScript, jobID, isAutoFailover);
		
		// we tell is it is fail over automatically because of the heart beat
		// lack
		String afguid = jobScript.getAFGuid();
		String hostname = jobScript.getProductionServerName();
		
		String msg = "";
		
		if (StringUtil.isEmptyOrNull(sessionGuid)) {
			long latestHeartBeat = HeartBeatModelManager
					.getLatestHeartBeat(afguid);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, 0 - (int) jobScript
					.getHeartBeatFailoverTimeoutInSecond());
			
			if (cal.getTimeInMillis() < latestHeartBeat) {
				
				msg = FailoverMessage.getResource(
									FailoverMessage.FAILOVER_Exit_HYPERV_With_Live_HeartBeat,
									jobScript.getProductionServerName());
					
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					
				log.error("executeFailover end because detection of the newer heartbeat");
				return 1;
			}
		}
		
		long handle = 0;
		try {
			
			try {
				
				handle = HyperVJNI.OpenHypervHandle("", "", "");
				
			} catch (HyperVException he) {

				log.error("HyperVJNI.OpenHypervHandle: " + he.getMessage()+he.getErrorCode());
				
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Handler);
					
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				throw new Exception(msg);
			}
			
			VMSnapshotsInfo snapshotForD2DSession = null;
			if (StringUtil.isEmptyOrNull(sessionGuid)) {

				SortedSet<VMSnapshotsInfo> lastSnapshotForD2DSession = HACommon.getLastSnapshotForD2DSession(
						hostRoot.getRootPath(),handle, afguid,true);
				
				if(!lastSnapshotForD2DSession.isEmpty()){
					for (VMSnapshotsInfo vmSnapshotsInfo : lastSnapshotForD2DSession) {
						if(!vmSnapshotsInfo.isDRSnapshot()){
							snapshotForD2DSession = vmSnapshotsInfo;
							break;
						}
					}
				}
				
			} else {
				snapshotForD2DSession = HACommon.getSnapshotForD2DSession(hostRoot.getRootPath(),handle, afguid, sessionGuid);
			}
			
			VMInfomationModelManager manager = HACommon.getSnapshotModeManager(hostRoot.getRootPath(), afguid);
			log.info("rootPath=" + hostRoot.getRootPath());
			
			VirtualMachineInfo vmInfo = manager.getInternalVMInfo(afguid);
			
			if (vmInfo == null || vmInfo.getType() != 0) {
				
				String error = "No VM found for production server:" + afguid
						+ " " + hostname;
				log.error(error);
				
				msg = FailoverMessage.getResource(
							FailoverMessage.FAILOVER_Error_HYPERV_Without_VM,
							jobScript.getProductionServerName());
					
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				return 1;
			}
			
			String vmGuid = vmInfo.getVmGUID();
			try {
				
				final int vmState = HyperVJNI.GetVmState(handle, vmInfo.getVmGUID());
				
				if (HyperVJNI.VM_STATE_TURNED_OFF != vmState) {
					HyperVJNI.ShutdownVM(handle, vmGuid);
					
					msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VM_SHUTDOWN, vmInfo.getVmName());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
			} catch (HyperVException he) {
				log.error("HyperVJNI.ShutdownVM: "  + he.getMessage() + he.getErrorCode());
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VM_SHUTDOWN_FAILED, vmInfo.getVmName());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				throw new Exception(msg);
			}
	
			try {
				HyperVJNI.RevertToVmSnapshot(handle, vmGuid, snapshotForD2DSession.getBootableSnapGuid());
			} catch (HyperVException e) {
				msg = "Failed to revert to snapshot ";
				log.error( msg + e.getMessage(),e);
				
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_PROCESS_VMI_FAIL_REVERT_SNAPSHOT,
						snapshotForD2DSession.getSessionName(),
						prodRoot.getProductionServerHostname(),HACommon.getRealHostName(),
						vmInfo.getVmName());
				
				log.error("wmi return message: " + HyperVJNI.GetLastErrorMessage());
				
				msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_HYPERV_Returne_MESSAGE)
						+ HyperVJNI.GetLastErrorMessage();
			
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, -1, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				throw new Exception(msg);
			}
			if (jobScript.isPowerOnWithIPSettings() || StringUtil.isEmptyOrNull(sessionGuid)) {
				List<IPSetting> ipSettings = new ArrayList<IPSetting>();
				if (jobScript.getIpSettings()!=null&&jobScript.getIpSettings().size()>0) {
					ipSettings = jobScript.getIpSettings();
					HAService.getInstance().setFailoverJobScript(jobScript);
				} else {
					if (jobScript.getFailoverMechanism()!=null&&jobScript.getFailoverMechanism().size()>0) {
						Virtualization virtualInfo = jobScript.getFailoverMechanism().get(0);
						if (virtualInfo.getNetworkAdapters()!=null&&virtualInfo.getNetworkAdapters().size()>0) {							
							List<NetworkAdapter> networkAdapters = virtualInfo.getNetworkAdapters();
							for (NetworkAdapter networkAdapter : networkAdapters) {
								if (networkAdapter.getIpSettings()!=null&&networkAdapter.getIpSettings().size()>0) {								
									IPSetting ipSetting = networkAdapter.getIpSettings().get(0);
									ipSettings.add(ipSetting);
								}
							}
						}
					}
				}
				if (ipSettings!=null && ipSettings.size()>0) {
					try {
						DoRVCMInjectServiceForHyperV(prodRoot, jobScript, jobID, handle, snapshotForD2DSession, ipSettings);
					} catch (Exception e) {
						log.error("Do DoRVCMInjectServiceForHyperV failed, go on to power on HyperV VM without ip setting.");
					}
				}
			}
			
			try {
				HyperVJNI.PowerOnVM(handle, vmGuid);
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VM_POWER_ON,jobScript.getFailoverMechanism().get(0).getVirtualMachineDisplayName());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());	

			} catch (HyperVException hyperVe) {
				log.error("power on error code = " + hyperVe.getErrorCode());
				
				msg = FailoverMessage.getResource(
							FailoverMessage.FAILOVER_PROCESS_VM_POWER_ON_FAIL,
							jobScript.getFailoverMechanism().get(0).getVirtualMachineDisplayName());
				msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_HYPERV_Returne_MESSAGE)
							+ HyperVJNI.GetLastErrorMessage();
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());			
				
				throw new Exception(msg);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.FailoverFailure, e.getMessage());
			throw e;
		}finally{
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException hyperVe) {
				log.error(hyperVe.getMessage()+":"+hyperVe.getErrorCode());
			}
		}
		return 0;
		
	}
	
	public int configureBootableSession(String job, FailoverJobScript jobScript,
			 VMSnapshotsInfo snapshotForD2DSession, long jobID) throws Exception {

		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		if (jobID == -1){
			jobID = nativeFacade.getJobID();
		}
		
		if (snapshotForD2DSession == null) {	
			String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Without_Snapshot_for_session, jobScript.getProductionServerName());			
			log.error(msg);
			throw new Exception(msg);
		}
		
		//VMSnapshotsInfo snapshotForD2DSession = preBootableSnapshot;

		String msg = FailoverMessage.getResource(
				FailoverMessage.FAILOVER_PROCESS_BEGIN_CONFIGURE_BOOTABLE_SESSION, snapshotForD2DSession.getSessionName(),jobScript.getProductionServerName());
		
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
				new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		
		// if the registered failover is not HyperV, throw Exception
		if (jobScript == null || jobScript.getVirtualType() != VirtualizationType.HyperV) {
			msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_BEGIN_HYPERV_VIRTUAL_NEEDED, jobScript.getProductionServerName());
			throw new Exception(msg);
		}

		List<Virtualization> failoverMechanism = jobScript
				.getFailoverMechanism();
		
		if (failoverMechanism.isEmpty()) {
			msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_BEGIN_HYPERV_VIRTUAL_NEEDED, jobScript.getProductionServerName());
			throw new Exception(msg);
		}

		Virtualization virtualization = jobScript.getFailoverMechanism().get(0);
		
		HyperV hyperV = null;
		if (virtualization instanceof HyperV)
			hyperV = (HyperV) virtualization;
		else {
			msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_BEGIN_HYPERV_VIRTUAL_NEEDED, jobScript.getProductionServerName());
			throw new Exception(msg);
		}

		String hostname;
		hostname = jobScript.getProductionServerName();
		String afguid = jobScript.getAFGuid();

		ProductionServerRoot productionServerRoot = RepositoryManager
				.getProductionServerRoot(afguid);

		String rootPath = RepositoryManager.getRootPath(productionServerRoot,
				Protocol.HeartBeatMonitor);
		log.info("rootPath=" + rootPath);
		
		{
			msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_HYPERV_Check_VM,
												jobScript.getProductionServerName());
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
		}
		long handle = 0;
		try {
			handle = HyperVJNI.OpenHypervHandle("", "", "");
		} catch (HyperVException he) {
			msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Handler);
			throw new Exception(msg);
		}

		try {

			VirtualMachineInfo vmInfo = HACommon.getSnapshotModeManager(rootPath, afguid).getInternalVMInfo(afguid);
			if (vmInfo == null || vmInfo.getType() != 0) {
				String error = "No VM found for production server:" + afguid
						+ " " + hostname;
				log.error(error);
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Without_VM, jobScript.getProductionServerName());
				throw new Exception(msg);
			}

			String vmGuid = vmInfo.getVmGUID();

			try {
				final int vmState = HyperVJNI.GetVmState(handle, vmInfo.getVmGUID());
				if (HyperVJNI.VM_STATE_TURNED_OFF != vmState) {
					HyperVJNI.ShutdownVM(handle, vmGuid);
					{
						msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VM_SHUTDOWN, vmInfo.getVmName());
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					}
				}
			} catch (HyperVException he) {
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VM_SHUTDOWN_FAILED, vmInfo.getVmName());
				throw new Exception(msg);
			}
			
			String sessionName = null;
			String snapeGUID = null;
			//VMSnapshotsInfo snapshotForD2DSession = null;
			{
				msg = FailoverMessage
				.getResource(
						FailoverMessage.FAILOVER_Process_HYPERV_Position_Snapshot,
						jobScript.getProductionServerName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}
			
			sessionName = snapshotForD2DSession.getSessionName();
			snapeGUID = snapshotForD2DSession.getSnapGuid();

			try {
				log.debug("Revert to snapeshot: " + snapeGUID);
				{
					msg = FailoverMessage.getResource(
							FailoverMessage.FAILOVER_Process_HYPERV_Apply_SnapShot,
							jobScript.getProductionServerName());
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					
				}
				HyperVJNI.RevertToVmSnapshot(handle, vmGuid, snapeGUID);
			} catch (HyperVException he) {
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_PROCESS_VMI_FAIL_REVERT_SNAPSHOT,
						snapshotForD2DSession.getSessionName(),
						productionServerRoot.getProductionServerHostname(),HACommon.getRealHostName(),
						productionServerRoot.getReplicaRoot().getVmname());
				
				log.error("wmi return message: " + HyperVJNI.GetLastErrorMessage());
				
				msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_HYPERV_Returne_MESSAGE)
						+ HyperVJNI.GetLastErrorMessage();
				throw new Exception(msg);
			}
			
			{
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_Process_HYPERV_Reconfigure,
						jobScript.getProductionServerName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
			}
			try {
//				HyperVJNI.ChangeVmFriendlyName(handle, vmGuid, hyperV
//						.getVirtualMachineDisplayName());
				HyperVJNI.SetVMMemorySize(handle, vmGuid, hyperV
						.getMemorySizeInMB());

				HyperVJNI.SetVMLogicalProcessorNum(handle, vmGuid, hyperV
						.getVirtualMachineProcessorNumber());
			} catch (HyperVException he) {
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_VM_RECONFIGURE, jobScript.getProductionServerName());
				throw new Exception(msg);
			}
			
			ADRConfigure adrConfigure = RepositoryManager.getADRConfigure(rootPath, sessionName);
			driverInject(handle, jobScript, snapshotForD2DSession, jobID,
					adrConfigure, vmInfo, hyperV);
		
			//Modify volume header to support vmware workstation 7
			//yuver01
			try {
				int result = nativeFacade.AdjustVolumeBootCodeForHyperV(vmGuid, snapeGUID);
				if(result != 0){
					log.error("Failed to change volume header.");
					log.error("result: " + result);
				}
			} catch (Exception e) {
				log.error("Failed to change volume header.",e);
			}
			
			log.debug("Mount integration service ISO...");
			try {
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_Process_HYPERV_Attach_VMGUESTISO,
						jobScript.getProductionServerName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				String systemRoot = System.getenv("SystemRoot");
				String vmguestiso = "";
				boolean attachISO = true;
				if (systemRoot != null) {
					vmguestiso = systemRoot + "\\System32\\vmguest.iso";
					File f = new File(vmguestiso);
					if (!f.exists()) {
						attachISO = false;
						log.warn("The vm tool iso file does not exsit, do not attach dvd after add the DVD device");
					}

				} else {
					attachISO = false;
					log.warn("The vm tool iso file does not exsit, do not attach dvd after add the DVD device");
				}
				
				if (!attachISO) {
					msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_warn_HYPERV_VM_TOOL_NOT_EXIST, vmguestiso, vmInfo.getVmName());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
				
				if(vmInfo.getVmGeneration() == 2)
				{
					List<String> scsiName = new ArrayList<String>();
					HyperVJNI.ListScsiControls(handle, vmGuid, scsiName);
					if (scsiName.isEmpty()) {
						HyperVJNI.AddScsiControl(handle, vmGuid,HyperVJNI.SCSI_CONTROLLER);
						scsiName.add(HyperVJNI.SCSI_CONTROLLER);
					}
				
					HyperVJNI.AttachDVDDiskToVMSCSI(handle, vmGuid, vmguestiso, scsiName.get(0), 1, attachISO);
				}
				else
				{
					HyperVJNI.AttachDVDDiskToVMIde(handle, vmGuid, vmguestiso, 1, 1, attachISO);
				}
							
			} catch (HyperVException he) {
				msg = FailoverMessage
						.getResource(
								FailoverMessage.FAILOVER_Error_HYPERV_Attach_VMGUESTISO,"IDE[1,1]");
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
			}
			// FIXME we should let manage UI to configure the virtutal network
			// externalNetworkGuid =
			// HyperVJNI.AddExternalNetwork("HyperV-External",
			// "Intel(R) 82566DM-2 Gigabit Network Connection");

			log.debug("configure network adapter...");
			{
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_Process_HYPERV_Configure_Network,
						jobScript.getProductionServerName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
			}
			
			addNetworkAdapters(handle, jobID, vmGuid, jobScript, adrConfigure);
		} catch (Exception e) {
			throw e;
		} catch (Throwable te) {
			log.error(te.getMessage(),te);			
			return -1;

		} finally {
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException hyperVe){}
		}
		
		msg = FailoverMessage.getResource(
				FailoverMessage.FAILOVER_PROCESS_FINISH_CONFIGURE_BOOTABLE_SESSION, snapshotForD2DSession.getSessionName(),jobScript.getProductionServerName());
		
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
				new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		
		
		return 0;
	}
	
	public int driverInject(long handle, FailoverJobScript jobScript, VMSnapshotsInfo snapshotForD2DSession, long jobID, 
			ADRConfigure adrConfigure, VirtualMachineInfo vmInfo, HyperV hyperV) throws Exception {

		String bootVolumeD2DPath_snaped = "";
		String sysVolumeD2DPath_snaped = "";
		
		long mntHandle = 0;
		try {
			String afguid = jobScript.getAFGuid();
			ProductionServerRoot productionServerRoot = RepositoryManager.getProductionServerRoot(afguid);
			String rootPath = RepositoryManager.getRootPath(productionServerRoot, Protocol.HeartBeatMonitor);
			NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();

			String vmGuid = vmInfo.getVmGUID();
			String sessionName = snapshotForD2DSession.getSessionName();
			String sessionSnapGUID = snapshotForD2DSession.getSnapGuid();
			String windowsDir = "";
			String msg = "";

			{
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_Process_HYPERV_Process_BootVolumeDisk,
						jobScript.getProductionServerName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}
			
			List<String> getAttachedDiskImage = new ArrayList<String>();
			try {
				HyperVJNI.GetAttachedDiskImage(handle, vmGuid, getAttachedDiskImage);
			} catch (HyperVException e) {
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_FAILED_CREATE_BOOTABLE_SNAHSOT, 
						snapshotForD2DSession.getSessionName(), jobScript.getProductionServerName());
				throw new Exception(msg);
			}
			
			
			log.debug("executeFailover decides to restore session:"
					+ sessionName);
			List<DiskDestination> diskDestinations = productionServerRoot.getReplicaRoot().getDiskDestinations();
			
			String	bootVolumeD2DPath = RepositoryManager.getBootVolumeD2DPath(handle,
											adrConfigure, diskDestinations, sessionName, getAttachedDiskImage, hyperV.getHostName());
			String	sysVolumeD2DPath = ""; 
			try {
				if (vmInfo.getVmGeneration() == 2)
					sysVolumeD2DPath = RepositoryManager.getSystemVolumeD2DPath(adrConfigure, diskDestinations, sessionName, hyperV.getHostName());
			} catch (Exception e) {
				log.warn("Failed to get system volume D2D path", e);
			}
			if(bootVolumeD2DPath==null){
				bootVolumeD2DPath = "";			
			} 
				
			bootVolumeD2DPath = bootVolumeD2DPath.toLowerCase();
			sysVolumeD2DPath = sysVolumeD2DPath.toLowerCase();
			ListIterator<String> listIterator = getAttachedDiskImage
					.listIterator();
			
			while (listIterator.hasNext()) {
				String disk = (String) listIterator.next();
				if (disk != null && !bootVolumeD2DPath.isEmpty() && disk.toLowerCase().startsWith(bootVolumeD2DPath)) {
					bootVolumeD2DPath_snaped = disk;
					//break;
				}
				if (disk != null && !sysVolumeD2DPath.isEmpty() && disk.toLowerCase().startsWith(sysVolumeD2DPath)) {
					sysVolumeD2DPath_snaped = disk;
					//break;
				}
			}
			
			String bootVolumeD2DParentPath = "";
			String sysVolumeD2DParentPath = "";
			try {
				List<String> sessionSnapDisks = new ArrayList<String>();
				HyperVJNI.GetSnapshotVhds(handle, vmGuid, sessionSnapGUID, sessionSnapDisks);
				
				int pos = bootVolumeD2DPath.lastIndexOf(File.separator);
				String bootDiskName = bootVolumeD2DPath.substring(pos + 1).toLowerCase();
				pos = sysVolumeD2DPath.lastIndexOf(File.separator);
				String sysDiskName =  sysVolumeD2DPath.substring(pos + 1).toLowerCase();
				
				log.info("boot disk file name:" + bootDiskName + " system disk file name:" + sysDiskName);
				
				for (String sessionSnapDisk : sessionSnapDisks) {
					String sessionDiskName = sessionSnapDisk.substring(sessionSnapDisk.lastIndexOf(File.separator) + 1).toLowerCase();
					if (!bootVolumeD2DPath.isEmpty() && sessionDiskName.startsWith(bootDiskName)) {
						bootVolumeD2DParentPath = sessionSnapDisk;
						//break;
					}
					if (!sysVolumeD2DPath.isEmpty() && sessionDiskName.startsWith(sysDiskName)) {
						sysVolumeD2DParentPath = sessionSnapDisk;
						//break;
					}
				}
			} catch (HyperVException e) {
				log.warn("Failed to get snapshot disks for session snapshot guid", e);
			}
			
			if (bootVolumeD2DPath_snaped.isEmpty()) {
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Without_BootVolume,sessionName, jobScript.getProductionServerName());
				throw new Exception(msg);
			}

			log.info("Mount VHD " + bootVolumeD2DPath_snaped + ", " + " parent " + bootVolumeD2DParentPath);
			String guidBootVolume = null;
			String guidSystemVolume = null;		
				
			if(adrConfigure.getBootvolume() == null){
				
				List<String> bootVolumes = new ArrayList<String>();
				List<String> sysVolume = new ArrayList<String>();
				
				log.info("bootVolume avhd: " + bootVolumeD2DPath_snaped);
				if(nativeFacade.MountVHDGetWinSysBootVol(handle, bootVolumeD2DPath_snaped, bootVolumes,sysVolume)==0xE000010AL){// found same disk signature
					log.warn(" can't MountVHDGetWinSysBootVol() after retry "+bootVolumeD2DPath_snaped);
					msg = FailoverMessage.getResource(
							FailoverMessage.FAILOVER_Error_HYPERV_Mount_Same_Disk);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
				
				if(bootVolumes.isEmpty()){
					log.error("Failed to get boot volume drive letter.");
					msg = FailoverMessage.getResource(FailoverMessage.FIALOVER_Error_GET_BOOT_DRIVER_LETTER);
					throw new Exception(msg);
				}
				
				if(sysVolume.isEmpty()){
					log.error("Failed to get system volume drive letter.");
					msg = FailoverMessage.getResource(FailoverMessage.FIALOVER_Error_GET_SYS_DRIVER_LETTER);
					throw new Exception(msg);
				}
				
				guidBootVolume = bootVolumes.get(0);
				guidSystemVolume = sysVolume.get(0);
				log.info("bootVolumes: " + guidBootVolume);
				log.info("systemVolumes: " + guidSystemVolume);
				
				if (StringUtil.isEmptyOrNull(guidBootVolume)) {
					msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Mount, bootVolumeD2DPath_snaped);
					throw new Exception(msg);
				}
				log.info("GuidBootVolume: " + guidBootVolume);

				if(!guidBootVolume.endsWith("\\")){
					guidBootVolume += "\\";
				}
				windowsDir = guidBootVolume + "Windows";
			} else {
				mntHandle = HyperVJNI.OpenHypervMountHandle("", "", "");
				
				List<HyperVMountDisk> mountDisks = new ArrayList<HyperVMountDisk>();

				HyperVMountDisk bootVolumeD2DDisk = new HyperVMountDisk();
				bootVolumeD2DDisk.setDiskFilePath(bootVolumeD2DPath_snaped);
				bootVolumeD2DDisk.setDiskParentFilePath(bootVolumeD2DParentPath);

				long bootVolumeOffset = RepositoryManager.getBootVolumeOffset(adrConfigure);
				bootVolumeD2DDisk.setRootVolumePartitionOffset(bootVolumeOffset);

				mountDisks.add(bootVolumeD2DDisk);
				if(!sysVolumeD2DPath_snaped.isEmpty() && vmInfo.getVmGeneration() == 2)
				{
					log.info("Mount VHD " + sysVolumeD2DPath_snaped + ", " + " parent " + sysVolumeD2DParentPath);
					HyperVMountDisk sysVolumeD2DDisk = new HyperVMountDisk();
					sysVolumeD2DDisk.setDiskFilePath(sysVolumeD2DPath_snaped);
					sysVolumeD2DDisk.setDiskParentFilePath(sysVolumeD2DParentPath);
					
					long systemVolumeOffset = 1; // Hard code to 1 so hyperv know to get uefi system volume.
					sysVolumeD2DDisk.setSysVolumePartitionOffset(systemVolumeOffset);
					
					mountDisks.add(sysVolumeD2DDisk);
				}

				String bootVolumeWindowsSystemRootFolder = ADRConfigureUtil.getBootVolumeWindowsSystemRootDirectory(adrConfigure);
				if (StringUtil.isEmptyOrNull(bootVolumeWindowsSystemRootFolder))
					bootVolumeWindowsSystemRootFolder = "C:\\Windows";

				HyperVMountParam mntParam = new HyperVMountParam();
				mntParam.setRootVolumeSystemDirectory(bootVolumeWindowsSystemRootFolder);
				mntParam.setDiskList(mountDisks);
				
				try {
					HyperVJNI.MountVHDEx(mntHandle, mntParam);
				} catch (HyperVException e1) {
					int errorcode = e1.getErrorCode();
					if(errorcode == 5){
						msg = FailoverMessage.getResource(
								FailoverMessage.FAILOVER_Error_HYPERV_Mount_Dynamic_Disk);
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					}
					throw e1;
				}

				windowsDir = mntParam.getRootVolPath();
				guidSystemVolume = mntParam.getSysVolPath();
				if (StringUtil.isEmptyOrNull(windowsDir)) {
					msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Mount, bootVolumeD2DPath_snaped);
					throw new Exception(msg);
				}
				log.info("GuidBootVolume: " + windowsDir);
			}
				
			if((!StringUtil.isEmptyOrNull(guidSystemVolume)) && (vmInfo.getVmGeneration() == 2))
			{
				log.info(String.format("Update BCD file for UEFI partition.") + guidSystemVolume);
				String EFISystemVolume = guidSystemVolume;
				String sessionFolder = rootPath + "VStore\\" + sessionName;
						
				try {
					HyperVJNI.InjectBCDFile(EFISystemVolume, sessionFolder);
				} catch (Exception he) {
					msg = FailoverMessage.getResource(
							FailoverMessage.FAILOVER_Error_HYPERV_InjectDriver,
							windowsDir);
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}		
			}

			log.debug("Do IDE Driver Injection." + windowsDir);
			try {
				HyperVJNI.InjectIDEDriver(windowsDir);
			} catch (HyperVException he) {
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_Error_HYPERV_InjectDriver,
						windowsDir);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
			}
			
			log.debug("Do disable services of vmware platform." + windowsDir);
			try {
				HyperVJNI.EnableVirtualPlatformServices(windowsDir, 1, false); //  Disable VMware services. platform_hyperv_t = 0, platform_vsphere_t = 1,
			} catch (HyperVException he) {
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_Error_HYPERV_InjectDriver,
						windowsDir);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}
			
			// log.debug("Do SCSI Driver Injection." + windowsDir);
			// HyperVJNI.InjectSCSIDriver(windowsDir);
			String changedOSName = CommonUtil.getNewGuestOSName();
			if(!StringUtil.isEmptyOrNull(changedOSName)){
				HyperVJNI.ChangeProductionHostName(windowsDir, changedOSName);
			}
			
			log.debug("Set target D2D for failover type:"
					+ CommonUtil.FailoverVM_HYPERV);
			String jobStringStr = CommonUtil.marshal(jobScript);
			HyperVJNI.PrepareD2DForFailover(windowsDir,
					CommonUtil.FailoverVM_HYPERV,CommonUtil.D2DFailoverJobScript,jobStringStr);
			try {
				log.debug("Set target D2D's service to start automatically...");
				HyperVJNI.SetD2DSrvStart(windowsDir, CommonUtil.D2DSrvName);
			} catch (Exception e) {
				log.debug("Set target D2D's service to start automatically failed. ignore it.");
			}

			try {
				log.debug("Disable shutdown eventtracker...");
				HyperVJNI.DisableShutDownEventTracker(windowsDir);
			} catch (Exception e) {
				log.debug("Disable shutdown eventtracker failed. ignore it.");
			}
			
			try {
				log.info("Start to adjust CD Driver letter.");
				HyperVJNI.AdjustVirtualCDDriveLetter(windowsDir, 1, 1);
			} catch(Exception e) {
				log.warn("Adjust CD Driver letter fails." + e.getMessage());
			}
			
			try {
				String bootVolumeWindowsSystemRootFolder = ADRConfigureUtil.getBootVolumeWindowsSystemRootDirectory(adrConfigure);
				if (StringUtil.isEmptyOrNull(bootVolumeWindowsSystemRootFolder))
					bootVolumeWindowsSystemRootFolder = "C:\\Windows";
				String scriptPath = PrepareInstantVMHelperScript(jobScript, new ArrayList<IPSetting>(), adrConfigure);
				long value = BackupService.getInstance().getNativeFacade().DoRVCMInjectServiceForHyperV(windowsDir, bootVolumeWindowsSystemRootFolder,
						ADRConfigureUtil.GetVolumesOnNonSystemOrBootDisk(adrConfigure), adrConfigure.isX86(), scriptPath);
				log.info("DoRVCMInjectServiceForHyperV in bootable snapshot return value is " + value);
			} catch (Exception e) {
				log.error("DoRVCMInjectServiceForHyperV fails." + e.getMessage());
			}	
			
		} finally {
			try {
				if(adrConfigure.getBootvolume() == null){
					if (!bootVolumeD2DPath_snaped.isEmpty()) {
						HyperVJNI.UnmountVHD(handle, bootVolumeD2DPath_snaped);
						log.info(String.format("Unmount VHD %s successfully.", bootVolumeD2DPath_snaped));
					}
					if (!sysVolumeD2DPath_snaped.isEmpty() && vmInfo.getVmGeneration() == 2) {
						HyperVJNI.UnmountVHD(handle, sysVolumeD2DPath_snaped);
						log.info(String.format("Unmount VHD %s successfully.", sysVolumeD2DPath_snaped));
					}
				}
				if (mntHandle != 0) {
					log.info("Unmount VHD files.");
					HyperVJNI.UnmountVHDEx(mntHandle);
					HyperVJNI.CloseHypervMountHandle(mntHandle);
				}
			} catch (Exception e) {
				log.error("Unmount VHD: ", e);
			}
		}
		
		return 0;
	}
	private void addNetworkAdapters(long handle, long jobID, String vmGuid, FailoverJobScript jobScript, ADRConfigure adrConfigure) throws Exception {
		String msg = "";
		String networkGuid = "";
		Virtualization virtualization = jobScript.getFailoverMechanism().get(0);
		if (!virtualization.getNetworkAdapters().isEmpty()) {
			Map<String, String> getVirutalNetworkList = null;
			try {
				getVirutalNetworkList = HyperVJNI.GetVirutalNetworkList(handle);
				if (getVirutalNetworkList == null) {
					msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Get_VirtualNetWorks);
					throw new Exception(msg);
				}
			} catch (HyperVException he) {
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Get_VirtualNetWorks);
				throw new Exception(msg);
			}
			Set<Entry<String, String>> entrySet = getVirutalNetworkList.entrySet();
			
			try {
				log.info("Remove all VM networks");
				HyperVJNI.RemoveAllVMNetworks(handle, vmGuid);
				log.info("Remove all VM networks end");
			} catch (HyperVException e) {
				log.error("Remove all virtual network failed.");
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_REMOVE_ALL_ADAPTERS, jobScript.getProductionServerName());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}

			if (jobScript.isConfiguredSameNetwork() && adrConfigure.getNetadapters()!=null && adrConfigure.getNetadapters().size()>0) {
				NetworkAdapter networkAdapter = virtualization.getNetworkAdapters().get(0);
				networkGuid = getNetworkGuid(entrySet, networkAdapter);
				if (networkGuid == "") {
					log.error("fail to get specific network in Hyper-V: " + networkAdapter.getNetworkLabel());
				} else {					
					for (int i=0;i<adrConfigure.getNetadapters().size();i++) {

						if(networkAdapter.getAdapterType().equalsIgnoreCase(HAService.HYPERV_LEGACY_NETWORK_ADAPTER) && adrConfigure.isUEFI())
						{
							String msg2 = FailoverMessage.getResource(FailoverMessage.FAILOVER_WARN_HYPERV_CHANGE_ADAPTER_TYPE,jobScript.getProductionServerName());
							HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
									new String[] { msg2,"", "", "", "" }, jobScript.getAFGuid());

							networkAdapter.setAdapterType(HAService.HYPERV_NETWORK_ADAPTER);
						}
							
						addNetworkAdapter(networkAdapter, networkGuid, jobID, handle, vmGuid, jobScript);
					}
				}
			} else {
				for (NetworkAdapter networkAdapter : virtualization.getNetworkAdapters()) {					
					networkGuid = getNetworkGuid(entrySet, networkAdapter);
					if (networkGuid == "") {
						log.error("fail to get specific network in Hyper-V: " + networkAdapter.getNetworkLabel());
					} else {
						if(networkAdapter.getAdapterType().equalsIgnoreCase(HAService.HYPERV_LEGACY_NETWORK_ADAPTER) && adrConfigure.isUEFI())
						{
							String msg2 = FailoverMessage.getResource(FailoverMessage.FAILOVER_WARN_HYPERV_CHANGE_ADAPTER_TYPE,jobScript.getProductionServerName());
							HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
									new String[] { msg2,"", "", "", "" }, jobScript.getAFGuid());

							networkAdapter.setAdapterType(HAService.HYPERV_NETWORK_ADAPTER);
						}
						
						addNetworkAdapter(networkAdapter, networkGuid, jobID, handle, vmGuid, jobScript);
					}
				}
			}
		}
		
	}
	
	private void addNetworkAdapter(NetworkAdapter networkAdapter, String networkGuid, long jobID, long handle, String vmGuid, FailoverJobScript jobScript) throws Exception {
		String msg = "";
		try {
			if (networkAdapter.getAdapterType().equals(HAService.HYPERV_LEGACY_NETWORK_ADAPTER)) {
				log.info("AddLegacyNetworkAdapterToVm - create bootable snapshot - Virtual network is " + networkAdapter.getNetworkLabel());
				HyperVJNI.AddLegacyNetworkAdapterToVm(handle, vmGuid, networkAdapter.getAdapterName(), "", networkGuid);
			} else {
				log.info("AddNetworkAdapterToVm - create bootable snapshot - Virtual network is " + networkAdapter.getNetworkLabel());
				HyperVJNI.AddNetworkAdapterToVm(handle, vmGuid, networkAdapter.getAdapterName(), "", networkGuid);
			}
			Thread.sleep(ADD_NIC_TIME);
		} catch (HyperVException he) {
			msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Add_NetWorkAdapter,
					jobScript.getProductionServerName());
			log.debug(he.getMessage() + ":" + he.getErrorCode());
			throw new Exception(msg);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			log.error("Add NIC - InterruptedException when create bootable snapshot");
		}
	}
	
	private String getNetworkGuid(Set<Entry<String, String>> entrySet, NetworkAdapter networkAdapter) {
		Iterator<Entry<String, String>> itrNetwork = entrySet.iterator();
		String networkGuid = "";
		while (itrNetwork.hasNext()) {
			Map.Entry<java.lang.String, java.lang.String> entry = (Map.Entry<java.lang.String, java.lang.String>) itrNetwork.next();
			if (entry.getValue().equals(networkAdapter.getNetworkLabel())) {
				networkGuid = entry.getKey();
				break;
			}
		}
		return networkGuid;
	}

	private void saveVCMEvent(FailoverJobScript jobScript, Date startTime, String status){
		try {
			VCMEventManager evnetManager = VCMEventManager.getInstance();
			VCMEvent event = getVCMEvent(jobScript);
			event.setStartTime(startTime);
			event.setStatus(status);
			evnetManager.saveVCMEvent(jobScript.getAFGuid(),event);
		} catch (Exception e) {
			CommonUtil.getExceptionStackMessage(e.getStackTrace());
		}
	}
	
	private VCMEvent getVCMEvent(FailoverJobScript jobScript){
	
		VCMEvent event = new VCMEvent();
		event.setTaskGuid(UUID.randomUUID().toString());
		event.setTaskName("Failover Task");
		event.setEndTime(new Date());
		Virtualization virtual = jobScript.getFailoverMechanism().get(0);
		HyperV hyperV = (HyperV)virtual;
		event.setDestHostName(hyperV.getHostName());
		event.setDestVMType(VCMVMType.HyperV);
		event.setDestVMName(hyperV.getVirtualMachineDisplayName());

		ProductionServerRoot prodRoot = null;
		String afguid = jobScript.getAFGuid();
		String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
		try{
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(afguid);
			String vmuuid = prodRoot.getReplicaRoot().getVmuuid();
			String vmname = prodRoot.getReplicaRoot().getVmname();
			event.setDestVMUUID(vmuuid);
			event.setDestVMName(vmname);
		}catch (Exception e) {}
		
		return event;
		
	}
	
	private long DoRVCMInjectServiceForHyperV(ProductionServerRoot productionServerRoot, FailoverJobScript jobScript, long jobID, long handle, VMSnapshotsInfo snapshotForD2DSession, List<IPSetting> ipSettings) throws Exception {
		
		String hostname;
		String msg;
		hostname = jobScript.getProductionServerName();
		String afguid = jobScript.getAFGuid();
	
		String rootPath = RepositoryManager.getRootPath(productionServerRoot, Protocol.HeartBeatMonitor);
		log.info("rootPath=" + rootPath);
		msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_HYPERV_Check_VM, jobScript.getProductionServerName());
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());

		VirtualMachineInfo vmInfo = HACommon.getSnapshotModeManager(rootPath, afguid).getInternalVMInfo(afguid);
		if (vmInfo == null) {
			String error = "No VM found for production server:" + afguid + " " + hostname;
			log.error(error);
			msg = FailoverMessage.getResource( FailoverMessage.FAILOVER_Error_HYPERV_Without_VM, jobScript.getProductionServerName());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			throw new Exception(msg);
		}
		
		if (vmInfo.getType() != 0) {
			String error = "No HyperV VM found for production server:"+ afguid + " " + hostname;
			log.error(error);
			msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Without_VM, jobScript.getProductionServerName());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			throw new Exception(msg);
		}

		String vmGuid = vmInfo.getVmGUID();
		
		String sessionName = null;
		msg = FailoverMessage.getResource(
				FailoverMessage.FAILOVER_Process_HYPERV_Position_Snapshot,
				jobScript.getProductionServerName());
		
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		
		sessionName = snapshotForD2DSession.getSessionName();
		
	
		
		String bootVolumeD2DPath_snaped = "";
		long returnValue = -99L;
		try {
			msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_HYPERV_Process_BootVolumeDisk, jobScript.getProductionServerName());
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			List<String> getAttachedDiskImage = new ArrayList<String>();
			HyperVJNI.GetAttachedDiskImage(handle, vmGuid, getAttachedDiskImage);
			
			
			log.debug("executeFailover decides to restore session:" + sessionName);
			List<DiskDestination> diskDestinations = productionServerRoot.getReplicaRoot().getDiskDestinations();
			ADRConfigure adrConfigure = RepositoryManager.getADRConfigure(rootPath, sessionName);
			
			String bootVolumeD2DPath;
			try {
				HyperV hyperv = (HyperV)(jobScript.getFailoverMechanism().get(0));
				bootVolumeD2DPath = RepositoryManager.getBootVolumeD2DPath(handle,
						adrConfigure, diskDestinations, sessionName, getAttachedDiskImage, hyperv.getHostName());
			} catch (Exception e1) {
				bootVolumeD2DPath = "";
			}
			
			if(bootVolumeD2DPath==null){
				bootVolumeD2DPath = "";
			} 
			
			bootVolumeD2DPath = bootVolumeD2DPath.toLowerCase();
			ListIterator<String> listIterator = getAttachedDiskImage.listIterator();
			
			while (listIterator.hasNext()) {
				String string = (String) listIterator.next();
				String parentFile = HyperVJNI.getParentAbsolutePath(string);
				if(parentFile==null) parentFile = ""; 
				parentFile=parentFile.toLowerCase();
				if (parentFile.startsWith(bootVolumeD2DPath)) {
					bootVolumeD2DPath_snaped = string;
					break;
				}
			}
			if (bootVolumeD2DPath_snaped.isEmpty()) {
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Without_BootVolume, sessionName, jobScript.getProductionServerName());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				throw new Exception(msg);
			}
			
			log.debug("Mount VHD " + bootVolumeD2DPath_snaped + " ...");
			String guidBootVolume = null;
			try {
				if(adrConfigure.getBootvolume() == null){
					List<String> bootVolumes = new ArrayList<String>();
					List<String> sysVolume = new ArrayList<String>();
					
					log.info("bootVolume avhd: " + bootVolumeD2DPath_snaped);
					BackupService.getInstance().getNativeFacade().MountVHDGetWinSysBootVol(handle, bootVolumeD2DPath_snaped, bootVolumes,sysVolume);
					
					if(bootVolumes.isEmpty()){
						log.error("Failed to get boot volume drive letter.");
						msg = FailoverMessage.getResource(FailoverMessage.FIALOVER_Error_GET_BOOT_DRIVER_LETTER);
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						throw new Exception(msg);
					}
					guidBootVolume = bootVolumes.get(0);
					log.info("bootVolumes: " + guidBootVolume);
				}else {
					long bootVolumeOffset = RepositoryManager.getBootVolumeOffset(adrConfigure);
					guidBootVolume = HyperVJNI.MountVHD(handle, bootVolumeD2DPath_snaped, bootVolumeOffset);
				}
				
			} catch (HyperVException e) {
				int errorcode = e.getErrorCode();
				if(errorcode == 5){
					msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Mount_Dynamic_Disk);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
				
				throw e;
			}
			
			if (StringUtil.isEmptyOrNull(guidBootVolume)) {
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Mount, bootVolumeD2DPath_snaped);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				throw new Exception(msg);
			}
			
			String windowsDir = "";
			if(adrConfigure.getBootvolume() == null){
				if(!guidBootVolume.endsWith("\\"))
					guidBootVolume += "\\";
				windowsDir = guidBootVolume + "Windows";
			}else {
				windowsDir = RepositoryManager.getBootVolumeWindowsPath(adrConfigure, guidBootVolume, snapshotForD2DSession.getSessionName(), productionServerRoot.getProductionServerHostname());
			}
			
			Map<String, String> getVirutalNetworkList = new HashMap<String, String>();
			try {				
				getVirutalNetworkList = HyperVJNI.GetVirutalNetworkList(handle);
			} catch (HyperVException e) {
				log.error("Get virtual network list failed.");
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Get_VirtualNetWorks);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				throw new Exception(msg);
			}
			try {
				log.info("Remove all VM networks");
				HyperVJNI.RemoveAllVMNetworks(handle, vmGuid);
				log.info("Remove all VM networks end");
			} catch (HyperVException e) {
				log.error("Remove all virtual network failed.");
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_REMOVE_ALL_ADAPTERS, jobScript.getProductionServerName());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}
			
			List<IPSettingDetail> ipSettingDetails = new ArrayList<IPSettingDetail>();
			for (int i=0;i<ipSettings.size();i++) {
				IPSetting ipSetting = ipSettings.get(i);
				IPSettingDetail detail = new IPSettingDetail();
				detail.setDhcp(ipSetting.isDhcp());
				detail.setDns(ipSetting.getDnses());
				List<String> gatewayList = new ArrayList<String>();
				for (Gateway gateway : ipSetting.getGateways()) {
					gatewayList.add(gateway.getGatewayAddress());
				}
				detail.setGateways(gatewayList);
				List<String> ipList = new ArrayList<String>();
				List<String> subnetList = new ArrayList<String>();
				for (IPAddressInfo ipAddress :ipSetting.getIpAddresses()) {
					ipList.add(ipAddress.getIp());
					subnetList.add(ipAddress.getSubnet());
				}
				detail.setIps(ipList);
				detail.setSubnets(subnetList);
				detail.setNicType(ipSetting.getNicType());
				detail.setVirtualNetwork(ipSetting.getVirtualNetwork());
				for (int j = 0; j < ipSetting.getWins().size(); j++) {	
					if (j==0)
						detail.setWinsPrimary(ipSetting.getWins().get(j));
					if (j==1)
						detail.setWinsSecond(ipSetting.getWins().get(j));
				}
				ipSettingDetails.add(detail);
				String networkGuid = "";
				try {
					for (Map.Entry<String, String> virtualNetworkEntry : getVirutalNetworkList.entrySet()) {
						if (virtualNetworkEntry.getValue().equals(ipSetting.getVirtualNetwork())) {
							networkGuid = virtualNetworkEntry.getKey();
							break;
						}
					}
					if ("".equals(networkGuid)) {
						msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_VIRTUAL_NETWORK_NOTAVAILABLE, ipSetting.getVirtualNetwork(), jobScript.getProductionServerName());
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					}
					if (ipSetting.getNicType().equals(HAService.HYPERV_LEGACY_NETWORK_ADAPTER) && !adrConfigure.isUEFI()) {
						log.info("AddLegacyNetworkAdapterToVm - power on VM - Virtual network is " + ipSetting.getVirtualNetwork());
						HyperVJNI.AddLegacyNetworkAdapterToVm(handle, vmGuid, "Adapter" + (i+1), "", networkGuid);
					} else {
						if (ipSetting.getNicType().equals(HAService.HYPERV_LEGACY_NETWORK_ADAPTER))
						{
							String msg2 = FailoverMessage.getResource(FailoverMessage.FAILOVER_WARN_HYPERV_CHANGE_ADAPTER_TYPE,jobScript.getProductionServerName());
							HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
									new String[] { msg2,"", "", "", "" }, jobScript.getAFGuid());
						}
						
						log.info("AddNetworkAdapterToVm - power on VM - Virtual network is " + ipSetting.getVirtualNetwork());
						HyperVJNI.AddNetworkAdapterToVm(handle, vmGuid, "Adapter" + (i+1), "", networkGuid);
					}
					Thread.sleep(ADD_NIC_TIME);
				} catch (HyperVException he) {
					msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Add_NetWorkAdapter, jobScript.getProductionServerName());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				} catch (InterruptedException e) {
					log.error("Add NIC - InterruptedException when power on VM");
				}
			}			
			List<DNSUpdaterParameters> dnsParameters = new ArrayList<DNSUpdaterParameters>();
			if (jobScript.getDnsParameters() == null) {
				jobScript.setDnsParameters(dnsParameters);
			}

			log.info("DoRVCMInjectServiceForHyperV - Adapter size is " + ipSettingDetails.size() + " and DNS redirection size is " + jobScript.getDnsParameters().size());
			String bootVolumeWindowsSystemRootFolder = ADRConfigureUtil.getBootVolumeWindowsSystemRootDirectory(adrConfigure);
			if (StringUtil.isEmptyOrNull(bootVolumeWindowsSystemRootFolder))
				bootVolumeWindowsSystemRootFolder = "C:\\Windows";
			// Prepare instant vm helper script
			String scriptPath = PrepareInstantVMHelperScript(jobScript, ipSettings, adrConfigure);
			returnValue = BackupService.getInstance().getNativeFacade().DoRVCMInjectServiceForHyperV(windowsDir, bootVolumeWindowsSystemRootFolder, 
					ADRConfigureUtil.GetVolumesOnNonSystemOrBootDisk(adrConfigure), adrConfigure.isX86(), scriptPath);
			log.info("DoRVCMInjectServiceForHyperV in power on VM return value is " + returnValue);
		} finally {			
			if (!bootVolumeD2DPath_snaped.isEmpty()) {
				HyperVJNI.UnmountVHD(handle, bootVolumeD2DPath_snaped);
			}
		}
		return returnValue;
			
	}
}
