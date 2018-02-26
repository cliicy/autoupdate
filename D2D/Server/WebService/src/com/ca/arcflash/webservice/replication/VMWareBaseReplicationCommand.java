package com.ca.arcflash.webservice.replication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.ADRConfigureUtil;
import com.ca.arcflash.failover.model.BackupInfo;
import com.ca.arcflash.failover.model.BackupInfoFactory;
import com.ca.arcflash.failover.model.Disk;
import com.ca.arcflash.ha.model.DiskInfo;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.ReplicaRoot;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.model.VirtualMachineInfo;
import com.ca.arcflash.ha.model.manager.VMwareSnapshotModelManager;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.utils.HARetryStrategy;
import com.ca.arcflash.ha.utils.VMwareUploadManager;
import com.ca.arcflash.ha.utils.WriteLogImpl;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.Disk_Info;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.LogSingleton;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareStorage;
import com.ca.arcflash.ha.vmwaremanager.powerState;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVMDetails;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualDisk;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.jobscript.alert.AlertType;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.webservice.common.VCMLicenseCheck;
import com.ca.arcflash.webservice.edge.license.LICENSEDSTATUS;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSPhereFailoverService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.ha.webservice.jni.VMWareJNI;
import com.ca.arcflash.webservice.data.VMwareConnParams;

public abstract class VMWareBaseReplicationCommand extends
		BaseReplicationCommand {

	private static final long serialVersionUID = -1394604096811105854L;
	private static final Logger log = Logger
			.getLogger(VMWareBaseReplicationCommand.class);

	private CAVirtualInfrastructureManager vmwareOBJ = null;
//	private List<CAVirtualInfrastructureManager> oldVMwareOBJ = new ArrayList<CAVirtualInfrastructureManager>();
	private CAVMDetails vmDetails;
	private String afguid;
	private String otherVmOS = "otherGuest";  //fanda03 otherGuest equals Vi client Other(32-bit)
	private VMwareSnapshotModelManager vmwareSnapshot;
	
	// Smart Copy
	public VMWareBaseReplicationCommand() {
	}

	private String needChangeGuestOS(ReplicationJobScript jobScript, String strGuestOSID, CAVirtualInfrastructureManager vmwareOBJ) {
		if (strGuestOSID == null)
			return strGuestOSID;
		if (strGuestOSID.equals("windows8Server64Guest") && isESXExpectedVer(jobScript, vmwareOBJ, 5, 0, 0))
			return "windows7Server64Guest";
		//later than ESX5.5 Update3 and ESX6.0 support win10 guest os
		if (strGuestOSID.equals("windows9Guest") || strGuestOSID.equals("windows9_64Guest") || strGuestOSID.equals("windows9Server64Guest"))
		{
		    if ((!isESXExpectedVer(jobScript, vmwareOBJ, 5, 5, 3116895))
		       && (!isESXExpectedVer(jobScript, vmwareOBJ, 6, 0, 0)))
		    {
		    	if (strGuestOSID.equals("windows9Guest"))
		    	{
		    		return "windows8Guest";
		    	}
		    	else if (strGuestOSID.equals("windows9_64Guest"))
		    	{
		    		return "windows8_64Guest";
		    	}
		    	else
		    	{
		    		return "windows8Server64Guest";
		    	}
		    }
		}

		return strGuestOSID;
	}

	private boolean isESXExpectedVer(ReplicationJobScript jobScript, CAVirtualInfrastructureManager vmwareOBJ, long compareMajorVer, long compareMinorVer, long compareBuild) {
		try {
			String buildNum = "";
			String platformVersion = "";
			if (jobScript.getVirtualType() == VirtualizationType.VMwareESX) {
				// Get the destination esx version and build
				platformVersion = vmwareOBJ.GetESXServerVersion();
				buildNum = vmwareOBJ.GetESXServerBuild();
			}
			else {
				ArrayList<ESXNode> nodeList = vmwareOBJ.getESXNodeList();
				String esxName = jobScript.getESXName();
				if (esxName == null) {
					log.error("ESX name is empty, skip the check.");
					return false;
				}
				for (int i = 0; i < nodeList.size(); i++) {
					if (nodeList.get(i).getEsxName().equals(esxName)) {
						// Get the destination vCenter version and build
						platformVersion = vmwareOBJ.getESXHostVersion(nodeList.get(i));
						buildNum = vmwareOBJ.getESXHostBuild(nodeList.get(i));
						break;
					}
				}
			}
			
			if (platformVersion == null || buildNum == null)
				return false;
			String[] versions = platformVersion.split("\\.");
			int esxVer = Integer.valueOf(versions[0].trim()).intValue();
			if (compareMajorVer == 5 && platformVersion.equals("5.0.0") && buildNum.equals("623860"))
				return true;
			if (compareMajorVer == 5 && compareMinorVer == 5 && compareBuild == 3116895 && platformVersion.equals("5.5.0") && buildNum.compareTo("3116895") >= 0)
				return true;
			else if (compareMajorVer == 6 && versions.length > 0 && esxVer >= compareMajorVer)
				return true;			
		}
		catch (Exception e) {
			log.error(e);
			log.error("Fail to check the ESX version.");
			return false;
		}
		return false;
	}
	
	protected boolean isVMWareVMNameExist(CAVirtualInfrastructureManager vmwareOBJ, String dcName, String vmName, String vmUUID){
		try {
			List<ESXNode> esxNodeList = vmwareOBJ.getESXNodeList();
			for (ESXNode e : esxNodeList) {
				if (e.getDataCenter().equalsIgnoreCase(dcName)) {
					ArrayList<VM_Info> vmList = vmwareOBJ.getVMNames(e, false);
					for (VM_Info vmInfo : vmList) {
						if(vmInfo.getVMName().compareToIgnoreCase(vmName) == 0
								&& vmInfo.getVMvmInstanceUUID().compareToIgnoreCase(vmUUID) == 0){
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(String.format("Fail to check if VM %s (uuid: %s) exists. ", vmName, vmUUID), e); 
		}
		return false;
	}


	private void cleanupDiskSizeJobScript(ReplicationJobScript jobScript) {
		try {
			for (DiskDestination diskDest : jobScript.getReplicationDestination().get(0).getDiskDestinations()) {
				try {
					diskDest.getDisk().setSize(0);
				} catch (NullPointerException e) {
				}
			}
		} catch (NullPointerException e) {
		}
	}
	
	//Smart Copy
	@Override
	public int doReplication(ReplicationJobScript jobScript,
			ReplicationDestination dest, BackupDestinationInfo backupDestinationInfo) {

		//set it for detecting smart copy
		setMaxSnapshotCount(HACommon.getMaxSnapshotCountForVMware(jobScript.getAFGuid()));
		
		//log.info("AFGUID: " + jobScript.getAFGuid());
		if(StringUtil.isEmptyOrNull(jobScript.getAFGuid())){
			log.error("afGUid is null");
			return HACommon.REPLICATE_FAILURE;
		}
		
		if(dest.getDiskDestinations().isEmpty()){
			log.error("dest.getDiskDestinations() is empty.");
			return HACommon.REPLICATE_FAILURE;
		}

		RepJobMonitor jobMonitor = CommonService.getInstance()
				.getRepJobMonitorInternal(jobScript.getAFGuid());

		long jobID = jobMonitor.getId();
		
		WriteLogImpl writeLogImpl = WriteLogImpl.getInstance();
		writeLogImpl.setActiveLogObj(jobID, jobScript.getAFGuid());
		LogSingleton.getInstance().setLogWriter(writeLogImpl);
		
		try {

			setJobMonitorSnapshotCount(jobScript.getAFGuid());

			String msg = "";

			VMWareInfo vmInfo = getVMWareInfo(dest);
			
			if (vmInfo == null) {
				log.error("VMInfo is null!!! Can not proceed without hostname,username,password....");
				return HACommon.REPLICATE_FAILURE;
			}

			synchronized (jobMonitor) {
				if (isVirtualStandbyCancelled(jobMonitor))
					return HACommon.REPLICATE_CANCEL;

				jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_GET_CONNECTION);
				jobMonitor.setTargetMachine(vmInfo.hostname);
			}

			// Perhaps these two objects needs to serialize
			log.debug("Get connection!!!!");
			try {
				
				createNewConnection(vmInfo);
				
			} catch (Exception e) {
				
				log.error("Failed to get connection.",e);
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_GET_CONN,
													 vmInfo.hostname, vmInfo.username);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
												new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID,AlertType.VMHostNotReachable, vmInfo.hostname);
				return HACommon.REPLICATE_FAILURE;
			}
			
			if (!validatePlatformDestination(jobScript.getAFGuid(), jobID, jobScript, vmwareOBJ))
				return HACommon.REPLICATE_FAILURE;
			
			// Check if the user have sufficient permission
			afguid = jobScript.getAFGuid();
			if (jobScript.getVirtualType() == VirtualizationType.VMwareVirtualCenter && !HAService.getInstance().hasSufficientPermissionForVSB(vmwareOBJ)) {
				String userName = ((VMwareVirtualCenterStorage)dest).getVirtualCenterUserName();
				
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERT_NOT_SUFFICIENT_PERMISSION, userName);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
									new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				log.warn("The user " + userName + " may not have sufficient permissions to perform virtual standby job. A user with administrative privileges is recommended.");
			}
			
			// Check VStroage API license
			if (checkVStorageApiLicense(jobScript, jobID) != HACommon.REPLICATE_SUCCESS){
				log.error("Fail to check VStorage API license.");
				return HACommon.REPLICATE_FAILURE;
			}

			// if host name is IP address, get its ESXHost and use it in
			// following api
			if (dest instanceof VMwareESXStorage) {
				ESXNode node = vmwareOBJ.getESXNodeList().get(0);
				vmInfo.esxHost = node.getEsxName();
				vmInfo.dcName = node.getDataCenter();
			}
			////fanda03 fix 161256
			if (!checkDataStoreExist( vmInfo.esxHost,  vmInfo.storage , jobID,  jobScript))
				return HACommon.REPLICATE_FAILURE;
			
			String moref = null;
			
			vmDetails = loadUUIDFromRepository(jobScript, vmwareOBJ, true);

			boolean isExist = false;
			boolean isStandbyVMUUIDChange = false;
			if(vmDetails == null || !vmInfo.vmName.equals(vmDetails.getVmName())){
				//re-deploy policy with a new name
				isExist = false;
			}else{
				isExist = (dest instanceof VMwareVirtualCenterStorage) ? 
						isVMWareVMNameExist(vmwareOBJ, vmInfo.dcName, vmDetails.getVmName(),vmDetails.getUuid()) 
							: vmwareOBJ.isVMExistWithUuid(vmDetails.getUuid(), vmDetails.getVmName());

				if(isExist){
					try {
						String tmpVmname = vmwareOBJ.getVMNameFromInstUUID(vmDetails.getUuid());
						log.info("tmpvmname: " + tmpVmname);
						if(!vmDetails.getVmName().equals(tmpVmname)){
							vmDetails.setVmName(tmpVmname);
							vmInfo.vmName = tmpVmname;
							isStandbyVMUUIDChange = true;
						}
						vmwareOBJ.reconfigVMCPUCount(vmDetails.getVmName(), vmDetails.getUuid(), vmInfo.cpuCount);
						vmwareOBJ.reconfigVMMemory(vmDetails.getVmName(),vmDetails.getUuid(), vmInfo.memoryMB);
					} catch (Exception e) {
						log.error("getVMNameFromInstUUID: " + e.getMessage(),e);
					}
					
					VM_Info vm = vmwareOBJ.getVMInfo(vmDetails.getVmName(),vmDetails.getUuid());
					try {
						// Change resource pool
						if (!StringUtil.isEmptyOrNull(vmInfo.resourcePoolmoref) && !StringUtil.isEmptyOrNull(vm.getVMresPool()) && 
								!(vm.getVMresPool()).equals(vmInfo.resourcePoolmoref)) {
							if (!vmwareOBJ.moveVMIntoResourcePool(vmInfo.vmName, vmDetails.getUuid(), vmInfo.esxHost, vmInfo.resourcePoolmoref)) {
								log.warn("The target resource pool [Name = " + vmInfo.resourcePool + ", ID = " + vmInfo.resourcePoolmoref + "] does not exist!");
								msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERT_RESOURCE_POOL_NOT_EXIST, vmInfo.resourcePool, vmInfo.resourcePoolmoref, vmInfo.esxHost);
								HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "",
										"", "" }, jobScript.getAFGuid());
							}
						}
					} catch (Exception e) {
						log.warn(String.format("Update resource pool from %s to %s [ID = %s] error: %s. ", 
								vm.getVMresPool(), vmInfo.resourcePool, vmInfo.resourcePoolmoref, e.getMessage()), e);
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_VMWARE_MSG, e.getMessage());
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERT_FAIL_UPDATE_RESOURCE_POOL, vmInfo.resourcePool, vmInfo.resourcePoolmoref, vmInfo.esxHost, msg);
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "",
								"", "" }, jobScript.getAFGuid());
					}
					
					// If Standby VM does not have any snapshot, need to clean up files in its datastore
					Map<String, String> allSnapshots = vmwareOBJ.listVMSnapShots(vmDetails.getVmName(), vmDetails.getUuid());
					if(allSnapshots == null || allSnapshots.size() == 0){
						log.info(String.format("VM %s has no any snapshots, remove the file %s", vmDetails.getVmName(), CommonUtil.SNAPSHOT_XML_FILE));
						deleteVMSnapshotModelFromStroage();
					}
				}
			}
			
			boolean isVMNew = false;
			
			// always check if source node is booting from uefi
			StringBuilder strGuestOSID = new StringBuilder();
			{
				StringBuilder strGuestOSUefiFlag = new StringBuilder();
				int result = getGuestOSInfo(strGuestOSID,strGuestOSUefiFlag, jobScript, backupDestinationInfo);
				if(result == 1){
					log.info("No backup session.");
					addActivityLog(jobScript.getAFGuid(), Constants.AFRES_AFALOG_INFO,
							ReplicationMessage.REPLICATION_NO_BACKUP_SESSION);
					
					return HACommon.REPLICATE_NOSESSIONS;
				}
				else if (result == 2) {
					log.error("Failed to get the guest OS info");
					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_TO_GET_SOURCE_GUEST_OS, jobScript.getAgentNodeName());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					return HACommon.REPLICATE_FAILURE;
				}
				else{
					vmInfo.isUEFI = Boolean.parseBoolean(strGuestOSUefiFlag.toString());
				}
				
//				if (vmInfo.isUEFI)
//				{
//					log.error("Virtual Standby Job failed, the source machine boots from the EFI partition.");
//					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_FOR_UEFI, jobScript.getAgentNodeName());
//					
//					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
//													new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
//					
//					HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
//					
//					reportEventsToVIClientWithReason(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL, msg);
//					return HACommon.REPLICATE_FAILURE;
//				}
			}
			
			boolean needReCreateVm = false;
			do {

				if (vmDetails == null || !isExist || needReCreateVm) {

					synchronized (jobMonitor) {
						if (isVirtualStandbyCancelled(jobMonitor))
							return HACommon.REPLICATE_CANCEL;
						jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_CREATE_VM);
					}

					try {
						// If the ESX is 5.0 Update 1 and the source machine is
						// Windows Server 2012, we have to change the guest os
						// to Server 2008 R2, otherwise, the virtual machine
						// cannot start.
						vmInfo.guestID = needChangeGuestOS(jobScript, strGuestOSID.toString(), vmwareOBJ);
						// guestID can be null, but can't be empty string or
						// invalid string
						if (StringUtil.isEmptyOrNull(vmInfo.guestID)
								|| "null".equalsIgnoreCase(vmInfo.guestID)) {
							vmInfo.guestID = otherVmOS; // fanda03
						}

						// If VM not exist, use standard VM name
						if (!StringUtil.isEmptyOrNull(jobScript
								.getVmNamePrefix())) {
							String stdVMName = jobScript.getVmNamePrefix()
									+ jobScript.getAgentVMName();
							vmInfo.vmName = stdVMName;
						}
						vmDetails = createVM(vmInfo);
						isVMNew = true;
						needReCreateVm = false;
						deleteVMSnapshotModelFromStroage();
						vmInfo.vmName = vmDetails.getVmName();

						msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_CREATE_VM,
								vmDetails.getVmName());

						HACommon.addActivityLogByAFGuid(
								Constants.AFRES_AFALOG_INFO, jobID,
								Constants.AFRES_AFJWBS_GENERAL, new String[] {
										msg, "", "", "", "" },
								jobScript.getAFGuid());

					} catch (Exception e) {
						log.error("Failed to create VM", e);
						msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_RET_MSG,
								e.getMessage());
						HACommon.addActivityLogByAFGuid(
								Constants.AFRES_AFALOG_ERROR, jobID,
								Constants.AFRES_AFJWBS_GENERAL, new String[] {
										msg, "", "", "", "" },
								jobScript.getAFGuid());

						msg = ReplicationMessage.getMonitorServiceErrorString(
								"8889900000", vmInfo.vmName);
						HACommon.addActivityLogByAFGuid(
								Constants.AFRES_AFALOG_ERROR, jobID,
								Constants.AFRES_AFJWBS_GENERAL, new String[] {
										msg, "", "", "", "" },
								jobScript.getAFGuid());

						HAService.getInstance().sendAlertMail(
								jobScript.getAFGuid(), jobID,
								AlertType.ConversionFailed, msg);
						reportEventsToVIClientWithReason(
								ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL,
								msg);
						return HACommon.REPLICATE_FAILURE;
					}
				} else {
					msg = ReplicationMessage.getResource(
							ReplicationMessage.REPLICATION_FIND_EXISTING_VM,
							vmDetails.getVmName());
					HACommon.addActivityLogByAFGuid(
							Constants.AFRES_AFALOG_INFO, jobID,
							Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,
									"", "", "", "" }, jobScript.getAFGuid());
				}

				reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_STARTED);

				if (isVMNew || isStandbyVMUUIDChange) {

					log.info("standby vm change. vmname="
							+ vmDetails.getVmName() + ";vminstanceuuid="
							+ vmDetails.getUuid());
					updateReplicationAndFailoverScript(jobScript.getAFGuid(),
							vmDetails.getVmName(), vmDetails.getUuid(),
							vmDetails.getResPoolID(),
							vmDetails.getResPoolName());
					sendVMInfoToMonitor(jobScript, vmDetails.getUuid(),
							vmDetails.getVmName());

					cleanupDiskSizeJobScript(jobScript);
					// save vmname and instance uuid
					updateRepository(vmDetails, afguid, jobScript, null);

					// log.info("registerForHA to the monitor");
					//
					// String failoverJobScriptTxt =
					// CommonUtil.marshal(HAService.getInstance().getFailoverJobScript(afguid));
					// if (dest.isProxyEnabled()) {
					// WebServiceClientProxy client =
					// MonitorWebClientManager.getMonitorWebClientProxy(HAService.getInstance().getHeartBeatJobScript(afguid));
					// client.getServiceV2().registerForHA(failoverJobScriptTxt,
					// true);
					// } else {
					// try {
					// WebServiceClientProxy client =
					// MonitorWebClientManager.getMonitorWebClientProxy(HAService.getInstance().getHeartBeatJobScript(afguid));
					// client.getServiceV2().registerForHA(failoverJobScriptTxt,
					// true);
					// } catch (Exception e) {
					// log.error("Failed to registerForHA on the monitor", e);
					// }
					// }
				}

				moref = vmwareOBJ.getVMMorefID(vmDetails.getVmName(),
						vmDetails.getUuid());
				vmInfo.moref = moref;

				powerState vmstates = vmwareOBJ.getVMPowerstate(
						vmDetails.getVmName(), vmDetails.getUuid());
				if (vmstates != powerState.poweredOff) {
					msg = ReplicationMessage.getMonitorServiceErrorString(
							"8589934604", "");

					HACommon.addActivityLogByAFGuid(
							Constants.AFRES_AFALOG_ERROR, jobID,
							Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,
									"", "", "", "" }, jobScript.getAFGuid());
					HAService.getInstance().sendAlertMail(
							jobScript.getAFGuid(), jobID,
							AlertType.ConversionFailed, msg);
					reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_FOR_POWERON);
					return HACommon.REPLICATE_FAILURE;
				}

				// populate replicated session guid
				SessionInfo[] sessions = null;

				int replicateTime = 0;
				String lastSessionName = null;

				BaseReplicationJobContext jobContext = getJobContext(jobID);
				
				do {
					synchronized (jobMonitor) {
						if (isVirtualStandbyCancelled(jobMonitor))
							return HACommon.REPLICATE_CANCEL;
					}

					replicateTime++;
					if (replicateTime > 1) {
						lastSessionName = sessions[sessions.length - 1]
								.getSessionName();
						sessions = null;
					}

					try {
						sessions = getNextReplicatedSessions(jobScript, vmInfo,
								jobID, lastSessionName, jobContext,
								backupDestinationInfo);
						if (sessions == null || sessions.length == 0) {

							if (replicateTime == 1) {
								addActivityLog(
										jobScript.getAFGuid(),
										Constants.AFRES_AFALOG_INFO,
										ReplicationMessage.REPLICATION_NO_BACKUP_SESSION);

								if (getSmartCopyMethod() == VCM_SMART_COPY_UNKNOWN) {
									reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_FOR_UNKNOWN_SMARTCOPY);
								} else {
									reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_FOR_NOSESSION);
								}
								return HACommon.REPLICATE_NOSESSIONS;
							}
							return HACommon.REPLICATE_SUCCESS;
						}
					} catch (Exception e) {
						log.error("Unable to get next sessions.", e);
						long logLevel = Constants.AFRES_AFALOG_WARNING;
						if (e instanceof NotFullMachineBackupSessionException) {
							logLevel = Constants.AFRES_AFALOG_ERROR;
							msg = ReplicationMessage
									.getResource(ReplicationMessage.AFRES_AFREPC_PREX
											+ ReplicationMessage.AFRES_AFREPC_JOB_FAILED);
						} else {
							msg = ReplicationMessage
									.getResource(ReplicationMessage.REPLICATION_GET_NEXT_SESSION);
						}
						HACommon.addActivityLogByAFGuid(logLevel, jobID,
								Constants.AFRES_AFJWBS_GENERAL, new String[] {
										msg, "", "", "", "" },
								jobScript.getAFGuid());
						if (replicateTime > 1) {
							// The current session is successfully converted. So
							// the job should succeed though it failed to get
							// next sessions.
							return HACommon.REPLICATE_SUCCESS;
						} else {
							msg = ReplicationMessage.getResource(
									ReplicationMessage.REPLICATION_RET_MSG,
									e.getMessage());
							HACommon.addActivityLogByAFGuid(
									Constants.AFRES_AFALOG_ERROR, jobID,
									Constants.AFRES_AFJWBS_GENERAL,
									new String[] { msg, "", "", "", "" },
									jobScript.getAFGuid());
							HAService.getInstance().sendAlertMail(
									jobScript.getAFGuid(), jobID,
									AlertType.ConversionFailed, msg);
							return HACommon.REPLICATE_FAILURE;
						}
					}

					if (isVSBWithoutHASupport() && !jobScript.getBackupToRPS()) {
						if (checkIfSessionMerging(jobScript, sessions, jobID))
							return HACommon.REPLICATE_SKIPPED;
					}

					try {
						String mess = "";
						if (sessions.length == 1) {
							mess = ReplicationMessage.getResource(
									ReplicationMessage.REPLICATION_SESSION,
									sessions[0].getSessionName());
						} else {
							mess = ReplicationMessage.getResource(
									ReplicationMessage.REPLICATION_SESSIONS,
									sessions[0].getSessionName(),
									sessions[sessions.length - 1]
											.getSessionName());
						}
						HACommon.addActivityLogByAFGuid(
								Constants.AFRES_AFALOG_INFO, jobID,
								Constants.AFRES_AFJWBS_GENERAL, new String[] {
										mess, "", "", "", "" },
								jobScript.getAFGuid());

						int ret = replicateSessions(sessions, jobScript,
								vmInfo, jobID, isVMNew, backupDestinationInfo, vmDetails);
						
						if (ret == HACommon.REPLICATE_DISKSIZECHANE) {
							needReCreateVm = true;
							break;
						}

						if (ret != HACommon.REPLICATE_SUCCESS) {
							log.error("replicateSessions return error! return code: "
									+ ret);

							return ret;
						}

					} catch (Exception e) {
						log.error("Catch replicateSessions error!", e);

						msg = ReplicationMessage
								.getResource(
										ReplicationMessage.REPLICATION_CONVER_ESX_FAIL_UNEXPECTED,
										vmInfo.vmName, vmInfo.esxHost);

						HACommon.addActivityLogByAFGuid(
								Constants.AFRES_AFALOG_ERROR, jobID,
								Constants.AFRES_AFJWBS_GENERAL, new String[] {
										msg, "", "", "", "" },
								jobScript.getAFGuid());

						HAService.getInstance().sendAlertMail(
								jobScript.getAFGuid(), jobID,
								AlertType.ConversionFailed, msg);

						reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL);
						return HACommon.REPLICATE_FAILURE;
					}

				} while ((sessions != null)
						&& !isVirtualStandbyPaused(jobScript));
				
				if (needReCreateVm) {
					String vmName = vmDetails.getVmName();
					log.info(String.format("Delete standby VM %s because its disk size has been changed.", vmName));
					if (!vmwareOBJ.deleteVM(vmName, vmDetails.getUuid())) {
						log.error(String.format("Fail to delete vm %s.", vmName));
					} 
					vmDetails = null;
				}

			} while (needReCreateVm);

			msg = ReplicationMessage.getResource(
					ReplicationMessage.REPLICATION_COMPLETE, "");
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FINISHED);

		} catch (Exception e) {
			log.error("VMwareESXReplicationCommand failed." + e.getMessage(),e);

			reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL);
			
			return HACommon.REPLICATE_FAILURE;

		} finally {
	
			try {
				if (vmwareOBJ != null) {
					try {
						vmwareOBJ.close();
					}catch (Exception e) {}
				}
				
//				for(CAVirtualInfrastructureManager mng : oldVMwareOBJ) {
//					try {
//						mng.close();
//					}catch (Exception e) {}
//				}
//				oldVMwareOBJ.clear();
				writeLogImpl.RemoveActiveLogObj();
				
			} catch (Exception e2) {
			}
			
			calculateThroughput(jobMonitor,afguid);
			
		}
		
		return HACommon.REPLICATE_SUCCESS;
	
	}
	
	private int checkVStorageApiLicense(ReplicationJobScript jobScript, long jobID)
	{
		LICENSEDSTATUS vStorageApiLicense = null;
		String msg = null;
		try {
			vStorageApiLicense = VCMLicenseCheck.getInstance().getVMwareVStorageApiLicense(jobScript);
		} catch (Exception e) {
			msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_IN_CHECK_VSTORAGE_LICENSE);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "",
					"", "" }, jobScript.getAFGuid());
			return HACommon.REPLICATE_FAILURE;
		}
		
		if (vStorageApiLicense==null || vStorageApiLicense != LICENSEDSTATUS.VALID) {
			msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_LICENSE_NO_VSTORAGE_API);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "",
					"", "" }, jobScript.getAFGuid());
			
			HAService.getInstance().sendAlertMail(AlertType.LicenseFailed, jobScript.getAFGuid(), jobID);
			return HACommon.REPLICATE_FAILURE;
		}
		return HACommon.REPLICATE_SUCCESS;
	}
	
	private void reportEventsToVIClient(String resID) {
		String vmName = null;
		String vmuuid = null;
		if(vmDetails != null) {
			vmuuid = vmDetails.getUuid();
			if(!StringUtil.isEmptyOrNull(vmuuid))
				vmName = vmDetails.getVmName();
		}
		
		ReplicationEventsReportManager.getInstance().logEvent(vmwareOBJ,
			 	vmName, vmuuid, 
				ReplicationMessage.getResource(resID, vmName));
	}
	
	private void reportStartProvisionEventsToVIClient(String startProvision, String endProvision) {
		String vmName = null;
		String vmuuid = null;
		if(vmDetails != null) {
			vmuuid = vmDetails.getUuid();
			if(!StringUtil.isEmptyOrNull(vmuuid))
				vmName = vmDetails.getVmName();
		}
		
		if(startProvision.equalsIgnoreCase(endProvision)) {
			ReplicationEventsReportManager.getInstance().logEvent(vmwareOBJ,
					vmName, vmuuid, 
					ReplicationMessage.getResource(ReplicationMessage.VIRTUAL_CONVERSION_PROVISION_POINT_START,
							startProvision, vmDetails.getVmName()));
		}
		else {
			ReplicationEventsReportManager.getInstance().logEvent(vmwareOBJ,
					vmName, vmuuid, 
					ReplicationMessage.getResource(ReplicationMessage.VIRTUAL_CONVERSION_PROVISION_POINT_SMART_START,
							startProvision, endProvision, vmDetails.getVmName()));
		}
	}
	
	private void reportFinishProvisionEventsToVIClient(String startProvision, String endProvision) {
		String vmName = null;
		String vmuuid = null;
		if(vmDetails != null) {
			vmuuid = vmDetails.getUuid();
			if(!StringUtil.isEmptyOrNull(vmuuid))
				vmName = vmDetails.getVmName();
		}
		
		if(startProvision.equalsIgnoreCase(endProvision)) {
			ReplicationEventsReportManager.getInstance().logEvent(vmwareOBJ,
					vmName, vmuuid, 
					ReplicationMessage.getResource(ReplicationMessage.VIRTUAL_CONVERSION_PROVISION_POINT_FINISH,
							startProvision, vmDetails.getVmName()));
		}
		else {
			ReplicationEventsReportManager.getInstance().logEvent(vmwareOBJ,
					vmName, vmuuid, 
					ReplicationMessage.getResource(ReplicationMessage.VIRTUAL_CONVERSION_PROVISION_POINT_SMART_FINISH,
							startProvision, endProvision, vmDetails.getVmName()));
		}
	}
	
	private void reportEventsToVIClientWithReason(String resID, String reason) {
		String vmName = null;
		String vmuuid = null;
		if(vmDetails != null) {
			vmuuid = vmDetails.getUuid();
			if(!StringUtil.isEmptyOrNull(vmuuid))
				vmName = vmDetails.getVmName();
		}
		ReplicationEventsReportManager.getInstance().logEvent(vmwareOBJ,
				vmName, vmuuid, 
				ReplicationMessage.getResource(resID, vmName) + " " + reason);
	}

	abstract VMWareInfo getVMWareInfo(ReplicationDestination dest);
	
	abstract void updateReplicationAndFailoverScript(String afGuid,String vmname,String vminstanceuuid, String resPoolID, String resPoolName);
	
	
	private final boolean updateRepository(CAVMDetails vmDetails,
			String afguid, ReplicationJobScript jobScript, String latestSessGuid) throws Exception {
		return saveVMUUIDToRepository(vmDetails, afguid, jobScript, latestSessGuid)
				&& putRepositoryToDatastore(vmDetails.getVmName(), vmDetails.getUuid());
	}

	abstract boolean saveVMUUIDToRepository(CAVMDetails vmDetails,
			String afguid, ReplicationJobScript jobScript, String latestSessGuid) throws Exception;

	abstract void sendVMInfoToMonitor(ReplicationJobScript replicationJobScript,
										String vmuuid, String vmname) throws Exception;

	abstract CAVMDetails loadUUIDFromRepository(ReplicationJobScript jobScript, CAVirtualInfrastructureManager vmManager, boolean downloadFromDatastore) throws Exception;

	abstract VM_Info getVirtualMachine(CAVirtualInfrastructureManager vmwareOBJ,VMWareInfo vminfo)throws Exception;
	
	private long getSessionGuidString(SessionInfo[] sessions, long jobID,
			List<String> sessionNames) {

		boolean containCompressedData = false;
		long toTransferBackupSize = 0;
//		StringBuffer sessBuffer = new StringBuffer();
		for (SessionInfo session : sessions) {
			sessionNames.add(session.getSessionName());
			if (session.getSessionCompressType() > 0)
				containCompressedData = true;
			toTransferBackupSize += session.getSessionDataSize();
//			if (sessBuffer.length() == 0) {
//				sessBuffer.append(session.getSessionName());
//			} else {
//				sessBuffer.append(", ").append(session.getSessionName());
//			}
		}
		
		if(sessionNames != null && sessionNames.size() > 0){
			sessionNames.remove(0);
		}
		
//		if (containCompressedData)
//			toTransferBackupSize = 0; // if containing compressed data, mark the
										// value to be unknown(0)

//		String msg = ReplicationMessage.getResource(
//				ReplicationMessage.REPLICATION_SESSIONS, sessBuffer.toString());
//		
//		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
//				new String[] { msg,"", "", "", "" }, afguid);
		
		return toTransferBackupSize;
	}

	private SessionInfo[] getNextReplicatedSessions(
			ReplicationJobScript jobScript, VMWareInfo vmInfo, long shrmemid,
			String lastSessionName, BaseReplicationJobContext jobContext, BackupDestinationInfo backupDestinationInfo) throws Exception {

		SessionInfo[] sessions = null;
		sessions = getNextSessionsToReplicate(jobScript, true, lastSessionName,true, jobContext, backupDestinationInfo);

		if (sessions == null || sessions.length == 0) {
			log.info("Empty session array return from getNextSessionsToReplicate. No session to replicate. Replication job quit.");
			return new SessionInfo[0];
		}

		return sessions;
	}
	
	private String getCustomDiskController(String afGuid) {
		String rootRegistry = CommonRegistryKey.getVSBRegistryRoot() + "\\VSBDiskController\\" + afGuid;
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(rootRegistry);
			if (handle == 0) {
				handle = registry.createKey(rootRegistry);
			}
			String value = registry.getValue(handle, "DiskController");
			if (value != null)
				return value.toUpperCase();
		} catch (Exception e1) {
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e2) {
				}
			}
		}
		return null;
	}
	
	private void AdjustDiskController(SortedSet<Disk> disks, VMWareInfo vmInfo,
			ADRConfigure adrConfigure, ReplicationJobScript replicationJobScript) {
		try {
			SupportedDiskControllerType supportedDiskCtlType = new SupportedDiskControllerType();
			
			// Does the VMware ESX(i) server support SATA
			supportedDiskCtlType.SATA = vmwareOBJ.isESXHostSupportSATA(vmInfo.esxHost, vmInfo.dcName);
			
			String osVersion = adrConfigure.getOSVersion();
			BigDecimal osVer = new BigDecimal(osVersion);
			// Does the source OS is 2012 or later version. Then SAS will be supported by default.
			if (osVer.compareTo(new BigDecimal("6.2")) >= 0)
				supportedDiskCtlType.SAS = true;
			
			boolean osUpgradedFromWin2k = false;
			{
				String bootVolumeWindowsSystemRootFolder = ADRConfigureUtil.getBootVolumeWindowsSystemRootDirectory(adrConfigure);
				if (!StringUtil.isEmptyOrNull(bootVolumeWindowsSystemRootFolder)) {
					if (bootVolumeWindowsSystemRootFolder.toUpperCase().endsWith("\\WINNT")) {
						log.info("The system root directory is “WINNT”, so consider that OS is upgraded from Windows 2000.");
						osUpgradedFromWin2k = true;
					}
				}
			}

			{
				Disk sysDisk = null, tempIDEDisk = null;
				int numberOfIDENonSystemDisk = 0;
				boolean systemDiskAdjustedToIDE = false;
				for (Disk disk : disks) {
					// find system disk
					if(disk.isSystemVolume())
						sysDisk = disk;
					
					if(disk.getControllerType().equals("IDE") && !disk.isSystemVolume()) {
						// count the number of IDE disk and disk is non system disk
						tempIDEDisk = disk;
						numberOfIDENonSystemDisk++;
					}

					if (supportedDiskCtlType.SAS) {
						// Support SAS. Use SAS
						disk.setControllerType("SAS");
					} else if (disk.getControllerTypeAdjusted()) {
						// The disks whose controller type was adjusted during
						// parse the adrconfgure.xml

						if (disk.isSystemVolume()) {
							// System disk need to be adjust to IDE if it is not
							// IDE.
							systemDiskAdjustedToIDE = true;
							log.info("The disk controller type for system disk need to be adjusted to IDE.");
						}

					}
					else {
						// The disks whose controller type is SATA and was not adjusted during parse the adrconfgure.xml
						if(!supportedDiskCtlType.SATA && disk.getControllerType().equals("SATA")) {
							// Doesn't support SATA
							if(supportedDiskCtlType.SAS) {
								// Support SAS. Use SAS
								disk.setControllerType("SAS");
							}
							else {
								// Use default SCSI
								disk.setControllerType("SCSI");
							}
							disk.setControllerTypeAdjusted(true);
						}
					}
					
					if (osUpgradedFromWin2k) {
						if(!disk.getControllerType().equals("IDE") && disk.isSystemVolume() && !systemDiskAdjustedToIDE) {
							// System disk need to be adjust to IDE if it is not IDE.
							systemDiskAdjustedToIDE = true;
							log.info("The disk controller type for system disk need to be adjusted to IDE.");
						}
					}
				}
				
				// Handle system disk.
				// The rule here is if there are any IDE disks the system disk will be changed to IDE to ensure the system could boot up.
				// Reason is because the standby VM created will have default boot sequence with IDE 0:0 if there are IDE disks attached
				// and could not boot from SAS/SCSI until manually change the boot sequence in the BIOS. 
				if(numberOfIDENonSystemDisk > 0 || systemDiskAdjustedToIDE) {
					// There are IDE disks, so have to change the disk controller type to IDE for system disk.
					if(sysDisk != null && !sysDisk.getControllerType().equals("IDE")) {
						log.info("Adjust the system disk controller type from " + sysDisk.getControllerType() + " to IDE.");
						sysDisk.setControllerType("IDE");
						sysDisk.setControllerTypeAdjusted(true);
						if (numberOfIDENonSystemDisk >= 3) {
							// More than 3 IDE disk so need to be adjusted one of the non system disk to other type.
							if(supportedDiskCtlType.SAS) {
								tempIDEDisk.setControllerType("SAS");
							}
							else {
								tempIDEDisk.setControllerType("SCSI");
							}
							tempIDEDisk.setControllerTypeAdjusted(true);
						}
					}
				}
			}
			
			// adjust based on registry key set under key 
			// SOFTWARE\\CA\\ARCserve Unified Data Protection\\Engine\\OfflineCopy\\VSBDiskController\\<NodeID>
			{
				String customDiskController = getCustomDiskController(replicationJobScript.getAFGuid()); 
				if (customDiskController != null) {
					int resetNumberOfIDENonSystemDisk = 0;
					for (Disk disk : disks) {
						if(customDiskController.equals("IDE")) {
							// only support 2 IDE disks besides the system disk;
							if(disk.isSystemVolume()) {
								disk.setControllerType(customDiskController);
							}
							else {
								if(resetNumberOfIDENonSystemDisk < 2) {
									disk.setControllerType(customDiskController);
									resetNumberOfIDENonSystemDisk++;
								}
								else {
									// remaining disks
									if(supportedDiskCtlType.SAS) {
										disk.setControllerType("SAS");
									}
									else {
										disk.setControllerType("SCSI");
									}
								}
							}
						}
						else {
							disk.setControllerType(customDiskController);
						}
					}
				}
			}
		}
		catch (Exception e) {
			log.warn("Exception occurred during adjust the disk controller type. ", e);
		}
	}
	

	private int replicateSessions(SessionInfo[] sessions,
			ReplicationJobScript jobScript,
			VMWareInfo vmInfo, long jobID, boolean isVMNew, BackupDestinationInfo backupDestinationInfo, CAVMDetails vmDetails) throws Exception {
		
		
		ReplicationDestination dest = jobScript.getReplicationDestination().get(0);

		String msg = null;
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();

		log.debug("Inside replicateSessions");

		// SmartCopy is for pre-existing sessions before vcm setting is made
		
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(jobScript.getAFGuid()); 
		
		boolean isSmartCopy = getSmartCopyFlag();
		
		boolean forceSmartCopy = HAService.getInstance().isNextReplicationForcedToMergeAllSessions(jobScript.getAFGuid());
		
		if(!forceSmartCopy){
			forceSmartCopy = HAService.getInstance().isIncrmentalBackupComplete(jobScript);
		}
		log.info("Force SmartCopy flag: " + Boolean.toString(forceSmartCopy));
		
		SessionInfo startSession = null;
		SessionInfo endSession = null;
		//This varialbe will be assigned with sessions delimited with white space
		StringBuilder smartCopySessions = new StringBuilder();
		List<SessionInfo> copiedSessions = new ArrayList<SessionInfo>();
		
		if (isSmartCopy || forceSmartCopy) {
			
			int from = 0;
			if(isSmartCopy){
				
				String startSessionName = getSmartCopySynthetizeStart();
				String endSessionName = getSmartCopySynthetizeSession();
					
				for (from = 0; from < sessions.length; from++) {
					if(sessions[from].getSessionName().equals(startSessionName)){
						startSession = sessions[from];
					}
					if (sessions[from].getSessionName().equals(endSessionName)) {
						endSession = sessions[from];
						break;
					}
				}
					
				if(startSession == null){
					startSession = new SessionInfo();
					startSession.setSessionName(startSessionName);
					int len = endSession.getSessionFolder().length();
					String sessionFolder = endSession.getSessionFolder().substring(0,len - endSessionName.length());
					if(!sessionFolder.endsWith("\\")){
						sessionFolder += "\\";
					}
					sessionFolder += startSessionName;
					startSession.setSessionFolder(sessionFolder);
				}
				
				log.info("smart copy begin session: " + startSession.getSessionName());
				log.info("smart copy end session: " + endSession.getSessionName());
					
			}
			
			if(forceSmartCopy){
				if(!isSmartCopy){
					startSession = sessions[0];
				}
				//if isSmartCopy==false, forceSmartCopy must be true
				from = sessions.length - 1;
				endSession = sessions[from];
				log.info("merge begin session: " + startSession.getSessionName());
				log.info("merget end session: " + endSession.getSessionName());
			}
			
			if(!isSmartCopy){
				//isSmartCopy will be passed to VMwareConversionManager
				isSmartCopy = true;
			}
			
			
			for (int i = 0; i <= from; i++) {
				smartCopySessions.append(sessions[i].getSessionName() + " ");
				copiedSessions.add(sessions[i]);
			}
			
			log.info("smart copy sessions: " + smartCopySessions.toString());
			
			sessions = Arrays.copyOfRange(sessions, from, sessions.length);
		}
		// Smart Copy

		// This is for showing in queue in UI
		List<String> sessionNames = new LinkedList<String>();
		long toTransferBackupSize = getSessionGuidString(sessions, jobID,sessionNames);

		try {
			
			vmwareSnapshot = VMwareSnapshotModelManager.getManagerInstance(vmwareOBJ,vmDetails.getVmName(), vmDetails.getUuid());
		
		} catch (NoRouteToHostException e) {
			
			log.error(e.getMessage(),e);
			
			msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DOWNLOAD_FAIL,
					 vmInfo.hostname,vmInfo.hostname);

			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
				new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			return HACommon.REPLICATE_FAILURE;
			
		} catch (SocketException e) {
			
			log.error(e.getMessage(),e);
			msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DOWNLOAD_FAIL,
					 vmInfo.hostname,vmInfo.hostname);

			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
				new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			return HACommon.REPLICATE_FAILURE;
		}
		
		
		for (SessionInfo session : sessions) {
			
			if(copiedSessions.isEmpty()){
				//this session list will be passed to upload and save snapshot method
				copiedSessions.add(session);
			}
			
			if (isSmartCopy) {
				if(startSession.getSessionName().compareToIgnoreCase(endSession.getSessionName()) == 0){
					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SESSION_BEGIN, startSession.getSessionName());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				} else {
					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SESSION_BEGIN_SC, startSession.getSessionName(), endSession.getSessionName());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
			} else {
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SESSION_BEGIN, session.getSessionName());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}

			synchronized (jobMonitor) {
				jobMonitor.setRepJobID(session.getSessionGuid() + "_" + jobID);
				if(isSmartCopy){
					jobMonitor.setRepSessionName(smartCopySessions.toString().trim());
				}else{
					jobMonitor.setRepSessionName(session.getSessionName());
				}
				jobMonitor.setRepSessionBackupTime(session.getBackupTime());
			}

			try {
				connectToRemote(backupDestinationInfo, jobScript.getAFGuid(), jobID);
			} catch (Exception e) {
				log.warn("Failed to connect to backup destination" + backupDestinationInfo.getBackupDestination(), e);
			}

			ADRConfigure adrConfigure;
			try {
				adrConfigure = HACommon.getAdrConfigure(session);
			} catch (FileNotFoundException e1) {
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILTO_ACCESS_SESSION);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				adrConfigure=null;
			}
			if(adrConfigure == null){
				log.error("adrconfigure is null");
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILTO_PARSE_ADRCONFIGURE,
						 session.getSessionName());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				return HACommon.REPLICATE_FAILURE;
			}
			
			try {
				if (HAService.getInstance().isDiskSizeChanged(
						HAService.getInstance().ifVmOwnedByAgent(vmwareOBJ,
								vmDetails.getVmName(), vmDetails.getUuid(),
								jobScript.getAFGuid()), adrConfigure)) {
					log.warn("Detect disk size is changed!");
					return HACommon.REPLICATE_DISKSIZECHANE;
				}
			} catch (NullPointerException e) {

			}
		
			
			//issue19941427
			//logSystemBootVolumeOnDiffDisk(jobScript,adrConfigure,shrmemid);
			if(HACommon.isTargetPhysicalMachine(jobScript.getAFGuid())){
				//Update disk with system/boot volume info for issue19941427
				if(!ADRConfigureUtil.isBootAndSystemVolumeOnOneDisk(adrConfigure)
				   && !LogSysBootOnDiffDisk){
					
					// If adrConfigure is parsed from VMDiskInof.xml instead of Adrconfigure.xml, now it's hard to identify if sys and boot volumes are on diff disks
					if (!adrConfigure.isPartialAdrconfigure()) {
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SYSTEM_BOOT_VOLUME_ON_DIFF_DISK);
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						
						
						LogSysBootOnDiffDisk = true;
					}
				}
			}
			
			long lockHandle = 0;
			try {

				//lock the session
				if(!isSmartCopy) {
					startSession = session;
					endSession = session;
				}
				
				String startSessionName = startSession.getSessionName();
				String endSessionName = endSession.getSessionName();
				boolean isForRemoteVCM = ManualConversionUtility.isVSBWithoutHASupport(jobScript);
				List<Integer> resultCodeList = new ArrayList<Integer>();
				lockHandle = HAService.getInstance().HALockD2DSessions(getSessionRootPath(session.getSessionFolder()),
						convertSessionNameToSessionNumber(startSessionName), convertSessionNameToSessionNumber(endSessionName),
						isForRemoteVCM, resultCodeList);
				
				if (resultCodeList.size() >= 1)
				{
					int resultCode = resultCodeList.get( 0 );
					
					if (resultCode == VMWareJNI.HALockD2DSessionsResult.Merging)
					{
						String message = "Some sessions of node " + jobScript.getAFGuid() + " are being merged.";
						log.info( message );
					}
				}
				
				if(lockHandle == 0) {
					
					log.error("Failed to lock.");
					
					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_LOCK_SESSIONS, startSessionName, endSessionName);
					String errorMsg = String.format("Failed to lock the sessions: from session [%s] to session [%s]", startSessionName, endSessionName);
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					
					
					HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
					reportEventsToVIClientWithReason(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL, msg);
					log.error(errorMsg);
					return HACommon.REPLICATE_FAILURE;
				}
							
				File sessonFolder = new File(session.getSessionFolder());
				
				//Catalog job and conversion job will run in parallel
				//There is a conflict when catalog attempt to grab a write lock as conversion job is still
				//holding the read lock. Minimizing the lock scope will reduce the conflict.
				//Meanwhile, conversion job will transfer ctf2 and adrconfigure first to reduce the conflict.
				/*Begin*/
				synchronized (jobMonitor) {
					if (isVirtualStandbyCancelled(jobMonitor))
						return HACommon.REPLICATE_CANCEL;
					jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_UPLOAD_META_DATA);
				}
				
				msg = ReplicationMessage.getResource(
						ReplicationMessage.REPLICATION_UPLOAD_META_DATA, vmInfo.vmName);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						
				log.info("Upload meta data files to VM.");
				boolean isUploaded = putBackupMetaDataToStorage(vmInfo,sessonFolder, session.getSessionGuid(), jobID, jobScript.getAFGuid());
				
				if (!isUploaded) {
					
					synchronized (jobMonitor) {
						if (isVirtualStandbyCancelled(jobMonitor))
							return HACommon.REPLICATE_CANCEL;
					}
					
					log.error("Failed to upload adrconfigure to storage!!!");
					log.error("ESXHost: " + vmInfo.esxHost);
					log.error("Username: " + vmInfo.username);
					log.error("VMName: " + vmInfo.vmName);
					
					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_UPLOAD_META_DATA_FAILED, vmInfo.storage, vmInfo.hostname);
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					
					HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
					reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL);
					
					return HACommon.REPLICATE_FAILURE;
					
				}

				log.info("Finish uploading meta data files to VM.");
				
				SortedSet<VMSnapshotsInfo> snapshots = vmwareSnapshot.getSnapshots(afguid);

				nativeFacade.HAUnlockCTF2(lockHandle);
				/*End*/
								
				synchronized (jobMonitor) {
					sessionNames.remove(session.getSessionName());
					jobMonitor
							.setToRepSessions(sessionNames.toArray(new String[0]));
					if (toTransferBackupSize > 0)
						toTransferBackupSize -= session.getSessionDataSize();
					jobMonitor.setToRepSessionsSize(toTransferBackupSize);
					jobMonitor.setTotalSessionNumbers(sessions.length);
				}
				
				setJobMonitorSnapshotCount(jobScript.getAFGuid());

				int isComplete = 1; // all disks in a session have been
				// replicated successfully, isComplete=0
				warnIncompleteSession(nativeFacade, jobID, adrConfigure, session
							.getSessionName(),jobScript.getAFGuid());
				
				SortedSet<Disk> disks = adrConfigure.getDisks();
				
				removeDiskWihoutFile(disks, sessonFolder);

				// Adjust the disk controller type for disks. Especially for system disk to ensure the system could boot up.
				AdjustDiskController(disks, vmInfo, adrConfigure, jobScript);

				// update the disk signature in the replication job script
				//if (HAService.getInstance().isIntegratedEdge()) {
				HAService.getInstance().updateReplicationJobScript(jobScript, adrConfigure);
				//}
				
				
				//fix issue 19437678, check replication destination's space before do convert -begin
				HashMap<String, DiskInfo> storageFreeSizeGroup = getAllStorageFreeSize(vmInfo);
				if(null == storageFreeSizeGroup){
						log.error("storageFreeSizeGroup is NULL!");
				}
				if (compareSessionSizeWithStorageSize(jobScript, backupDestinationInfo, dest, session, jobID, 
						storageFreeSizeGroup) == false) {
					log.error("Session Size > storage Size, replication Exit! ");

					msg = ReplicationMessage
							.getResource(
									ReplicationMessage.REPLICATION_CHECK_DESTINATIONSIZE_ERROR,
									new String[] { session.getSessionName(),
											vmInfo.storage });
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					
					HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
					reportEventsToVIClientWithReason(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL, msg);
					return HACommon.REPLICATE_FAILURE;
				}
				// fix issue 19437678, check replication destination's space before
				// do convert -end
				
				{
					String lastConvertedSession = ConversionHistoryUtil.getDefaultConversionHistory()
							.getLastConvertedSession(jobScript.getAFGuid(),vmDetails.getUuid(), backupDestinationInfo.getBackupDestination());
					if (lastConvertedSession != null && endSession.getSessionName().compareToIgnoreCase(lastConvertedSession) > 0) {
						nativeFacade.DetectAndRemoveObsolteBitmap(backupDestinationInfo.getBackupDestination(),
								lastConvertedSession, endSession.getSessionName());
					}
				}

				// Construct conversion manager on the base of proxy flag setting.
				VMwareConversionManager manager = null;
				if (dest.isProxyEnabled()) {
					manager = VMwareProxyConversionManager.getInstance(vmwareOBJ,
							vmDetails, vmInfo, jobScript, session, jobID,
							isSmartCopy,startSession,endSession,adrConfigure,vmwareSnapshot);
				} else {
					manager = VMwareNonProxyConversionManager.getInstance(
							vmwareOBJ, vmDetails, vmInfo, jobScript, session,
							jobID,isSmartCopy,startSession,endSession,adrConfigure,vmwareSnapshot);
				}
				
				try {
					Map<String, String> existSnapshots = vmwareOBJ.listVMSnapShots(vmDetails.getVmName(), vmDetails.getUuid());
					manager.setFirstConversion(existSnapshots == null || existSnapshots.size() == 0);
				}catch (Exception e){
					log.warn("Fail to get snapshot. Suppose it isn't first conversion. ");
				}
				
				reportStartProvisionEventsToVIClient(startSession.getSessionName(), endSession.getSessionName());
				
				boolean isFirstReplic = false;
				if (snapshots == null || snapshots.isEmpty()) {
					
					if (isVMNew) {
						// If VM is new, all hartfile should be deleted to avoid
						// skipping replication.
						String[] undeleteHartFiles = HACommon.deleteHartFilesRecursiveAllSession(jobScript, session);
						if(undeleteHartFiles.length > 0){
							for (String hartFileName : undeleteHartFiles) {
								log.warn("undeleted hart file: " + hartFileName);
							}
						}
					}
					
					//check storage block size,
					/*
					 *  Block Size   Largest virtual disk on VMFS-3 
					 *	1MB          256GB 
					 *	2MB          512GB 
					 *	4MB 		 1TB 
					 *	8MB 		 2TB 
					 */
					manager.checkStorageBlockSize(jobScript, vmInfo, disks, jobID);
					
					Map<String, Disk_Info> baseDiskMappings = null;
						
					log.info("Add virtual disks to VM based on adrconfig");
					try {
						baseDiskMappings = addDisksAndGetDiskMapping(jobScript,vmInfo,disks,adrConfigure);
					} catch (Exception e) {
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_ADD_DISK, vmInfo.vmName);						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
									new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
							
						HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
							
						log.error("Failed in removing and adding disks.",e);
						reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL);
						return HACommon.REPLICATE_FAILURE;
							
					}
					
					if (baseDiskMappings == null) {
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_ADD_DISK, vmInfo.vmName);						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
									new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
							
						HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
							
						log.error("Failed in removing and adding disks.");
						reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL);
						return HACommon.REPLICATE_FAILURE;
					}
					
					isFirstReplic = true;
					// Issue 107248
					// Changed Block Tracking (CBT) must be disabled for SAN transport mode. So we enable CBT after conversion.
					// Refer to: http://kb.vmware.com/selfservice/microsites/search.do?language=en_US&cmd=displayKC&externalId=1035096
//					try {
//						log.info("Enable CBT.");
//						vmwareOBJ.CheckAndEnableCBT(vmDetails.getVmName(), vmDetails.getUuid());
//					}
//					catch(Exception e) {
//						log.error("Failed to enable CBT.");
//					}
					isComplete = manager.doVirtualConversion(disks,baseDiskMappings);

				} else {
					
					isComplete = manager.doVirtualConversion(disks, null);

				}

				if(manager.getRecreatedVMManager() != null) {
//					oldVMwareOBJ.addAll(manager.getOldVMManager());
					vmwareOBJ = manager.getRecreatedVMManager();
				}
				
				try {
					connectToRemote(backupDestinationInfo, jobScript.getAFGuid(), jobID);
				} catch (Exception e) {
					log.warn("Failed to connect to backup destination" + backupDestinationInfo.getBackupDestination(), e);
				}
				if (isComplete == 0) {
					
					// Save last successfully replicated session
					//dest.setBackupSession(session.getSessionGuid());
					jobScript.getReplicationDestination().clear();
					jobScript.getReplicationDestination().add(dest);
					Date backupTime = new Date();

					BackupInfo backupInfo = getBackupInfo(session);
					if(backupInfo != null){
						backupTime = BackupConverterUtil.string2Date(backupInfo
								.getDate()
								+ " " + backupInfo.getTime());
						jobScript.setReplicateTime(backupTime.getTime());
						
						HAService.getInstance().updateRepJobScript(jobScript);
					} 
					else {
						log.error("BackupInfo.xml is missing!");
					}

					log.debug("Take snapshot");
					synchronized (jobMonitor) {
						if (isVirtualStandbyCancelled(jobMonitor))
							return HACommon.REPLICATE_CANCEL;
						jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_TAKE_SNAPSHOT);
					}
					// Check if snapshots are more than 30, delete the oldest
					// snapshot
					String currentSnapshot = detectAndTakeSnapshot(session, vmInfo, jobScript);
					if (StringUtil.isEmptyOrNull(currentSnapshot)) {
						log.error("Failed to take snapshot!!!!");
						
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_TAKE_SNAPSHOT,
								vmInfo.vmName, session.getSessionName());
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						
						HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
						
						
						reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_SNAPSHOT);
						return HACommon.REPLICATE_FAILURE;
					}
					// Add snapshot to activity log
					msg = ReplicationMessage.getResource(
							ReplicationMessage.REPLICATION_TAKE_SHAPSHOT, session
									.getSessionName());
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());

					saveVMuuid(jobScript,sessonFolder, session, currentSnapshot);
					HAService.getInstance().HAUnlockD2DSessions(lockHandle);
					lockHandle = 0;
					
					synchronized (jobMonitor) {
						if (isVirtualStandbyCancelled(jobMonitor))
							return HACommon.REPLICATE_CANCEL;
						jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_CRATE_BOOTABLESNAPSHOT);
					}

					String bootableSnapshotName = session.getSessionName()
							+ "_bootable";
					String bootableSnapshotGuid = createBootableSnapshot(jobScript.getAFGuid(),
							bootableSnapshotName, session, currentSnapshot, jobID);
					if (StringUtil.isEmptyOrNull(bootableSnapshotGuid)) {
						log.error("Failed to create the bootable snapshot");
						
						boolean bResult = vmwareOBJ.revertToSnapshot(vmDetails.getVmName(), 
								vmDetails.getUuid(), currentSnapshot);

						if(!bResult){
							log.warn("Failed to revert snapshot. Delete hart file.");
							String[] undeletedFiles = HACommon.deleteHartFiles(jobScript,session);
							for (String fileName : undeletedFiles) {
								log.warn("undelete hart file: " + fileName);
							}
						}
						
						bResult = vmwareOBJ.removeSnapshot(vmDetails
								.getVmName(), vmDetails.getUuid(), currentSnapshot);
						if (bResult) {
							log.info("Successfully remove the session snapshot:snapshotURL:"
											+ currentSnapshot
											+ " sessionName:"
											+ session.getSessionName());
						} else {
							log.error("Failed to remvoe the session snapshot:snapshotURL:"
											+ currentSnapshot
											+ " sessionName:"
											+ session.getSessionName());
						}

						msg = ReplicationMessage
								.getResource(
										ReplicationMessage.REPLICATION_CREATE_BOOTABLESHAPSHOT_FAIL,
										bootableSnapshotName);
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						
						HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
						reportEventsToVIClientWithReason(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL, msg);
						return HACommon.REPLICATE_FAILURE;
					}

					msg = ReplicationMessage
							.getResource(
									ReplicationMessage.REPLICATION_CREATE_BOOTABLESHAPSHOT_SUCCESS,
									bootableSnapshotName);

					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					
					log.debug("Upload snapshot to VM.");
					boolean result = saveSnapshotUrl(sessonFolder, copiedSessions,	currentSnapshot, afguid, 
											 bootableSnapshotGuid, backupTime.getTime(),adrConfigure);
					
					if (!result) {
						log.error("Failed to save snapshot to storage");
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_SAVE_SNAPSHOT_INFO,
								vmInfo.vmName);
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						
						HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
						
						reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL);
						return HACommon.REPLICATE_FAILURE;
						
					}					
					
//					try {
//						if(isFirstReplic) {
//							log.info("Enable CBT.");
//							vmwareOBJ.CheckAndEnableCBT(vmDetails.getVmName(), vmDetails.getUuid());
//						}
//					}
//					catch(Exception e) {
//						log.error("Failed to enable CBT.");
//					}
						
					//delete all the hartfiles,since this round conversion succeeds.
					String[] undeletedFiles = HACommon.deleteHartFiles(jobScript,session);
					for (String fileName : undeletedFiles) {
						log.warn("undelete hart file: " + fileName);
					}
					
					if(!HACommon.isTargetPhysicalMachine(jobScript.getAFGuid())){
						//issue 19941427
						File[] nvram = sessonFolder.listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.endsWith(".nvram");
							}
						});
						
						if(nvram != null && nvram.length > 0){
							log.info("set nvram: " + nvram[0].getAbsolutePath());
							try {
								vmwareOBJ.setVMNVRAMFile(vmDetails.getVmName(), vmDetails.getUuid(), nvram[0].getAbsolutePath());
							} catch (Exception e) {
								log.error(e.getMessage(),e);
							}
						}
					}
					
					// Delete bitmap also
					nativeFacade.DeleteSessionBitmap(backupDestinationInfo.getBackupDestination(), session.getSessionName());
					saveConversionHistoryToDatastore(backupDestinationInfo.getBackupDestination(), session);
					try
					{
						ConversionHistoryUtil.getDefaultConversionHistory().updateLastConversion(jobScript.getAFGuid(), vmDetails.getUuid(),
								backupDestinationInfo.getBackupDestination(), endSession.getSessionName());
					}catch (Exception e) {
						log.error("Update conversion history error. ", e);
					}
							
					
					//clear incremental complete flag
					HAService.getInstance().clearIncrementalBackupComplete(jobScript);
					
					reportFinishProvisionEventsToVIClient(startSession.getSessionName(), endSession.getSessionName());

					
					//set smart copy flag to false
					isSmartCopy = false;
					setSmartCopyFlag(false);
					smartCopySessions.delete(0, smartCopySessions.length());
					copiedSessions.clear();
					
					//spawn a thread to monitor VM
					if(!isVSBWithoutHASupport() && jobScript.isVSphereBackup()){
						log.info("Monitor the status of VM " + jobScript.getAFGuid());
						try {
							if (!jobScript.getBackupToRPS())
								VSPhereFailoverService.getInstance().monitorVM(jobScript.getAFGuid());
							else {
								log.info("BackupToRPS: Monitor VM on the HBBU backup proxy.");
								FailoverJobScript failoverScript = HAService.getInstance().getFailoverJobScript(jobScript.getAFGuid());
								HAService.getInstance().getD2DService(failoverScript).getServiceV2().monitorVMForHA(jobScript.getAFGuid());
							}
						} catch (Exception e) {
							log.error("monitor thread does not start up." + e.getMessage(),e);
						}
					}
					
				} else {
					
					log.error("return code = " + isComplete + " No Disk is replicated!!!!");
					
					if(isComplete != HACommon.REPLICATE_CANCEL) {
						
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_REPLICATION_SESSION,
								session.getSessionName(), vmInfo.vmName);
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
														new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						
						HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
						
						reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL);
					}
					
					return isComplete;
				}
				
			}
			catch (SocketException e) {
				log.error("Failed to replication the session:"+session.getSessionName(),e);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { e.getLocalizedMessage(),"", "", "", "" }, jobScript.getAFGuid());
				
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_REPLICATION_SESSION,
						session.getSessionName(), vmInfo.vmName);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
				
				reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL);
				return HACommon.REPLICATE_FAILURE;
			}
			catch (Throwable e) {
				log.error("Failed to replication the session:"+session.getSessionName(),e);
				
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_REPLICATION_SESSION,
						session.getSessionName(), vmInfo.vmName);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
				
				reportEventsToVIClient(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_GENERAL);
				return HACommon.REPLICATE_FAILURE;
			}
			finally {
				HAService.getInstance().HAUnlockD2DSessions(lockHandle);
			}
		}
		
		return HACommon.REPLICATE_SUCCESS;
		
	}
	
	
	private void deleteVMSnapshotModelFromStroage(){

		HARetryStrategy strategy = HARetryStrategy.newStrategy(10, 6);
		List<String> files = new ArrayList<String>(1);
		files.add(CommonUtil.SNAPSHOT_XML_FILE);
		VMwareUploadManager.deleteFromStorage(strategy, vmwareOBJ, vmDetails,files);
		
	}

	private Map<String, Disk_Info> addDisksAndGetDiskMapping(ReplicationJobScript jobScript,VMWareInfo vmInfo,
											    SortedSet<Disk> disks,ADRConfigure adrConfigure)
											    throws Exception{
		
		// If no full backup, iterate all sessions and find the
		// first full backup

		Map<String, Disk_Info> baseDiskMappings = addVirtualDiskToVM(disks, jobScript, vmInfo);
		return baseDiskMappings;
	}
	
	private String createBootableSnapshot(String afGuid,String bootableSnapshotName,
			SessionInfo session, String currentSnapshot, long jobID) {
		String bootableSnapshotGuid = null;
		int result = 0;
		log.info("Entry createBootableSnapshot");
		ReplicationJobScript replicationJobScript = HAService.getInstance()
				.getReplicationJobScript(afGuid);
		ReplicationDestination dest = replicationJobScript
				.getReplicationDestination().get(0);
		long backupTime = System.currentTimeMillis();
		VMSnapshotsInfo vmSnapshotInfo = new VMSnapshotsInfo(session
				.getSessionName(), session.getSessionGuid(), currentSnapshot,
				backupTime);
		vmSnapshotInfo.setLocalTime(HACommon.date2String(backupTime));
		if (dest.isProxyEnabled()) {
			// call the web service to inject the drivers, because the monitee
			// doesn't install VDDK.
			log.info("Configure the bootable session on the monitor");
			result = HAService.getInstance().configBootableSessionByMonitor(
					afGuid, vmSnapshotInfo, jobID);
		} else {
			log.info("Configure the bootable session on the converter");
			result = HAService.getInstance().configBootableSession(afGuid,
					vmSnapshotInfo, jobID);
		}

		if (result != 0) {
			log.error("Failed to configure the bootable session");
			if (result == -2) {
				// Failover job script does not find.
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CREATE_BOOTABLESHAPSHOT_FAIL_JOB_SCRIPT_NOT_FOUND,
						replicationJobScript.getAgentNodeName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			}
			return bootableSnapshotGuid;
		}

		try {
			log.info("Begin to take the bootable snapshot");
			bootableSnapshotGuid = vmwareOBJ.createSnapshot(vmDetails
					.getVmName(), vmDetails.getUuid(), bootableSnapshotName,
					session.getSessionGuid());
			log.info("Create the bootable snaphot:" + bootableSnapshotName
					+ " " + bootableSnapshotGuid);
		} catch (Exception e) {
			log.error("Failed to take bootable snaphot:" + e.getMessage(), e);
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, afGuid);
			return bootableSnapshotGuid;
		}

		return bootableSnapshotGuid;
	}

	
	@Override
	String[] getGuidsOfReplicatedSessions(ReplicationJobScript jobScript)
			throws Exception {

		String[] EMPTY = new String[0];
		
		CAVirtualInfrastructureManager vmManager = null;
		try {
			if(jobScript == null){
				return EMPTY;
			}
				
			VMWareInfo vInfo = getVMWareInfo(jobScript.getReplicationDestination().get(0));
			
			vmManager = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
						vInfo.hostname, vInfo.username,vInfo.password, vInfo.protocol, true,vInfo.port);
			

			vInfo.setExParams(vmManager);
				
			CAVMDetails vmdetail = loadUUIDFromRepository(jobScript, vmManager, false);
			
			if(vmdetail == null){
				return EMPTY;
			}
			
			String[] sessionGuids =  getReplicatedSessGuid(vmManager,vmdetail,jobScript.getAFGuid());
			
			return sessionGuids;
			
		} catch (Exception e) {
			log.error("Failed to get replicated session guids." + e.getMessage(),e);
			throw e;
		}
		finally {
			if(vmManager != null) {
				try {
					vmManager.close();
				}
				catch (Exception e) {
				}
			}
		}

	}

	private void saveVMuuid(ReplicationJobScript jobScript,
			File sessonFolder, SessionInfo session, String currentSnapshot) throws Exception{

		updateRepository(vmDetails, afguid, jobScript, session
				.getSessionGuid());
		sendVMInfoToMonitor(jobScript,vmDetails.getUuid(), vmDetails
				.getVmName());

	}

	private String detectAndTakeSnapshot(SessionInfo session,
			VMWareInfo vmInfo, ReplicationJobScript replicationJobScript) throws Exception{

		try {
			try {
				createNewConnection(vmInfo);
				
				vmwareOBJ.reconfigVMCPUCount(vmDetails.getVmName(), vmDetails.getUuid(), vmInfo.cpuCount);
				vmwareOBJ.reconfigVMMemory(vmDetails.getVmName(),vmDetails.getUuid(), vmInfo.memoryMB);

				//Fix the issue:20033392
				//try to use the new vmware connection
				vmwareSnapshot.setConnectionInfo(vmwareOBJ, vmDetails.getVmName(), vmDetails.getUuid());
				
			} catch (Exception e) {
				log.error("Failed to re-establish connection to take snapshot."
						+ e.getMessage(),e);
				return null;
			}

			SortedSet<VMSnapshotsInfo> snapshotsInConf =vmwareSnapshot.getSnapshots(afguid);
			List<VMSnapshotsInfo> unPairedSnapshots = new LinkedList<VMSnapshotsInfo>();			
			
			
			//begin
			//if data snapshot and bootable snapshot does not show in pair
			//delete one of them existing
			for (VMSnapshotsInfo vmSnapshotsInfo : snapshotsInConf) {
				
				if(StringUtil.isEmptyOrNull(vmSnapshotsInfo.getSnapGuid())
					|| StringUtil.isEmptyOrNull(vmSnapshotsInfo.getBootableSnapGuid())){
					
					if(!StringUtil.isEmptyOrNull(vmSnapshotsInfo.getSnapGuid())){
						try {
							vmwareOBJ.removeSnapshot(vmDetails.getVmName(), vmDetails.getUuid(), vmSnapshotsInfo.getSnapGuid());
						} catch (Exception e) {
							log.error(e.getMessage(),e);
							return null;
						}
					}
					
					if(!StringUtil.isEmptyOrNull(vmSnapshotsInfo.getBootableSnapGuid())){
						try {
							vmwareOBJ.removeSnapshot(vmDetails.getVmName(), vmDetails.getUuid(), vmSnapshotsInfo.getBootableSnapGuid());
						} catch (Exception e) {
							log.error(e.getMessage(),e);
							return null;
						}
					}
					
					unPairedSnapshots.add(vmSnapshotsInfo);
					
				}

			}
			if(!unPairedSnapshots.isEmpty()){
				snapshotsInConf.removeAll(unPairedSnapshots);
			}
			//End
			
			List<VMSnapshotsInfo> realSnapshots = new ArrayList<VMSnapshotsInfo>();
			Map<String, String> allSnapshots = vmwareOBJ.listVMSnapShots(
					vmDetails.getVmName(), vmDetails.getUuid());
			for (VMSnapshotsInfo vmSnapshotsInfo : snapshotsInConf) {
				for (String snapshotGuid : allSnapshots.keySet()) {
					if (snapshotGuid.compareTo(vmSnapshotsInfo.getSnapGuid()) == 0) {
						realSnapshots.add(vmSnapshotsInfo);
						break;
					}
				}
			}

			LinkedList<VMSnapshotsInfo> realSnapshotCopy = new LinkedList<VMSnapshotsInfo>(realSnapshots);
			Collections.sort(realSnapshotCopy, new Comparator<VMSnapshotsInfo>() {
				@Override
				public int compare(VMSnapshotsInfo s1, VMSnapshotsInfo s2) {
					return (int) (s1.getSnapNo() - s2.getSnapNo());
				}
			});

			if (allSnapshots != null && allSnapshots.size() > 0) {
				
				int minRetentionCount = replicationJobScript.getStandbySnapshots();
				minRetentionCount = HACommon.getMaxSnapshotCountForVMware(afguid)>minRetentionCount
									?minRetentionCount:HACommon.getMaxSnapshotCountForVMware(afguid);
				
				// Max vmware snapshot is 30,if there are 30 delete the oldest snapshot
				int realSnapshotDeep = realSnapshots.size(); // Becuase the every snapshot has bootable snapshot
				
				if (realSnapshotDeep >= minRetentionCount) {
					boolean bResult = false;
					// int diff = realSnapshotDeep - num + 1;
					log.info("Begin to delete the snapshot");

					// Begin to delete the DR snapshot
					ArrayList<VMSnapshotsInfo> deletedSnapshots = new ArrayList<VMSnapshotsInfo>();
					for (VMSnapshotsInfo vmSnapshotsInfo : realSnapshotCopy) {
						if (vmSnapshotsInfo.isDRSnapshot()) {
							try {
								bResult = vmwareOBJ.removeSnapshot(vmDetails
										.getVmName(), vmDetails.getUuid(),
										vmSnapshotsInfo.getSnapGuid());
								if (bResult) {
									deletedSnapshots.add(vmSnapshotsInfo);
									String msg = "Successfully delete the DR snapshot:"
											+ vmSnapshotsInfo.getSessionName()
											+ " snap GUID:"
											+ vmSnapshotsInfo.getSnapGuid();
									log.info(msg);
								} else {
									String msg = "Failed to delete the DR snapshot:"
											+ vmSnapshotsInfo.getSessionName()
											+ " snap GUID:"
											+ vmSnapshotsInfo.getSnapGuid();
									log.error(msg);
									return null;
								}
							} catch (Exception e) {
								String msg = "Failed to delete DR snapshot:"
										+ vmSnapshotsInfo.getSessionName()
										+ " snap GUID:"
										+ vmSnapshotsInfo.getSnapGuid();
								log.error(msg + e.getMessage(), e);
								return null;
							}
						}
					}
					if (deletedSnapshots.size() > 0) {
						realSnapshotCopy.removeAll(deletedSnapshots);
					}
					// end

					int leftSnapshotDeep = realSnapshotDeep
							- deletedSnapshots.size();
					if (leftSnapshotDeep < minRetentionCount) {
						log.info("Successfully merge the VM DR snapshots");
					} else {

						int diff = leftSnapshotDeep - minRetentionCount + 1;
						for (int i = 0; i < diff; i++) {
							// Firstly delete bootable snapshot, then delete the
							// Org snapshort
							VMSnapshotsInfo snapInfo = realSnapshotCopy.removeFirst();

							// delete the bootable snapshot
							if (!snapInfo.isDRSnapshot()) {
								try {
									log.info("Begin to delete the bootable snapshot:"
											+ snapInfo.getSessionName()
											+ " bootable GUID:"
											+ snapInfo.getBootableSnapGuid());

									bResult = vmwareOBJ.removeSnapshot(
											vmDetails.getVmName(), vmDetails
													.getUuid(), snapInfo
													.getBootableSnapGuid());
									if (bResult) {
										String msg = "Successfully delete the bootable snapshot:"
												+ snapInfo.getSessionName()
												+ " bootable GUID:"
												+ snapInfo
														.getBootableSnapGuid();
										log.info(msg);
									} else {
										String msg = "Failed to delete the bootable snapshot:"
												+ snapInfo.getSessionName()
												+ " bootable GUID:"
												+ snapInfo.getBootableSnapGuid();
										log.error(msg);
										return null;
									}
								} catch (Exception e) {
									String msg = "Failed to delete bootalbe snapshot:"
											+ snapInfo.getSessionName()
											+ " bootable GUID:"
											+ snapInfo.getBootableSnapGuid();
									log.error(msg + e.getMessage(), e);
									return null;
								}
							}

							// delete the session snapshot
							try {
								log.info("Begin to delete the snapshot:"
										+ snapInfo.getSessionName()
										+ " snapshot GUID:"
										+ snapInfo.getSnapGuid());

								bResult = vmwareOBJ.removeSnapshot(vmDetails
										.getVmName(), vmDetails.getUuid(),
										snapInfo.getSnapGuid());
								if (bResult) {
									log
											.info("Successfully delete the snapshot:"
													+ snapInfo.getSessionName()
													+ " snapshot GUID:"
													+ snapInfo.getSnapGuid());
								} else {
									String msg = "Failed to delete the snapshot:"
												+ snapInfo.getSessionName()
												+ " snapshot GUID:"
												+ snapInfo.getSnapGuid();
									log.error(msg);
									return null;
								}
								File sessonFolder = new File(session
										.getSessionFolder());
								deleteBackupMetaDataFromStorage(sessonFolder
										.listFiles(),
										snapInfo.getSessionName(), snapInfo
												.getSessionGuid());
							} catch (Exception e) {
								String msg = "Failed to delete snapshot:"
										+ snapInfo.getSessionName()
										+ " snapshot GUID:"
										+ snapInfo.getSnapGuid();
								log.error(msg + e.getMessage(), e);
								return null;
							}
						}
					}

					log.info("Successfully merge the VM snapshots");

					vmwareSnapshot.replaceSnapshots(vmwareSnapshot.getInternalVMInfo(afguid),
													new TreeSet<VMSnapshotsInfo>(realSnapshotCopy));

				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return null;
		}

		String currentSnapshot = null;

		try {
			BackupInfo backupInfo = BackupInfoFactory.getBackupInfo(session.getSessionFolder()
					+ "\\BackupInfo.XML");
			String snapdesc = session.getSessionName();
			MessageFormat form = new MessageFormat(
					"Backup name: {0} \n\tDate: {1}\n\tTime: {2}\n\tBackup Type: {3}\n{4}");
			Object[] args = { snapdesc, backupInfo.getDate(),
					backupInfo.getTime(), backupInfo.getBackupType(),
					backupInfo.getServerInfo().toFomattedString() };
			snapdesc = form.format(args);
			snapdesc = session.getSessionGuid() + "\n\t" + snapdesc;
			
			log.info("Begin to take the snapshot:" + session.getSessionName());
			currentSnapshot = vmwareOBJ.createSnapshot(vmDetails.getVmName(),
					vmDetails.getUuid(), session.getSessionName(), snapdesc);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			try {
				currentSnapshot = vmwareOBJ.createSnapshot(vmDetails
						.getVmName(), vmDetails.getUuid(), session
						.getSessionName(), session.getSessionGuid());
			} catch (Exception e2) {
				log.error(e2.getMessage(),e);
				return null;
			}
		}

		return currentSnapshot;

	}

	private void createNewConnection(VMWareInfo vmInfo) throws Exception {
//		oldVMwareOBJ.add(vmwareOBJ);
		try {
			vmwareOBJ.close();
		} catch (Exception e) {
			
		}

		vmwareOBJ = CAVMwareInfrastructureManagerFactory
			.getCAVMwareVirtualInfrastructureManager(
				vmInfo.hostname, vmInfo.username,
				vmInfo.password, vmInfo.protocol, true,
				vmInfo.port);

		vmInfo.setExParams(vmwareOBJ);
	}

	private String[] getReplicatedSessGuid(CAVirtualInfrastructureManager vmwaremanager,CAVMDetails vmdetail,String afGuid) throws Exception {

		String[] EMPTY = new String[0];
		
		try {
			
			Map<String, String> snapshots = null;

			snapshots = vmwaremanager.listVMSnapShots(vmdetail.getVmName(), vmdetail
					.getUuid());

			if (snapshots == null || snapshots.size() == 0) {	
				return EMPTY;
			}
			
			Set<String> snapshotUrls = snapshots.keySet();

			VMwareSnapshotModelManager vmwareSnapshot = VMwareSnapshotModelManager.getManagerInstance(
					vmwaremanager, vmdetail.getVmName(), vmdetail.getUuid());

			if (vmwareSnapshot.isReady()) {

				SortedSet<VMSnapshotsInfo> repositorySnapshots = vmwareSnapshot.getSnapshots(afGuid);

				if (repositorySnapshots == null
						|| repositorySnapshots.size() == 0) {
					return  EMPTY;
				}

				List<String> tmpSessGuid = new LinkedList<String>();
				List<String> snapshotList = new LinkedList<String>();
				List<VMSnapshotsInfo> tmpVMList = new LinkedList<VMSnapshotsInfo>();
				for (VMSnapshotsInfo vm : repositorySnapshots) {
					
					if(vm.isDRSnapshot()){
						snapshotList.add(vm.getSnapGuid());
						continue;
					}
					
					if(StringUtil.isEmptyOrNull(vm.getSnapGuid()) 
							|| StringUtil.isEmptyOrNull(vm.getBootableSnapGuid())){
						tmpVMList.add(vm);
					}else{
						if (snapshotUrls.contains(vm.getSnapGuid()) 
								&& snapshotUrls.contains(vm.getBootableSnapGuid())) {
							snapshotList.add(vm.getSnapGuid());
							snapshotList.add(vm.getBootableSnapGuid());
							String sessionGuids = vm.getSessionGuid();
							String[] tokens = sessionGuids.split("[|]");
							tmpSessGuid.addAll(Arrays.asList(tokens));
						}else{
							tmpVMList.add(vm);
						}
					}
				}
				
				if(tmpVMList.size() > 0){
					log.info("total snapshots in xml is bigger than real snapshot count.Remove unexisted snapshot from xml.");
					repositorySnapshots.removeAll(tmpVMList);
					vmwareSnapshot.replaceSnapshots(afGuid, repositorySnapshots);
				}
				
				if(snapshotList.size() < snapshotUrls.size()){
					//If snapshot is not in vmsnapshotmodel.xml
					//delete these snapshot from vm.
					//This situation will happen if snapshot and bootable snapshot is taken but Tomcat poweroff or d2d service stops
					log.info("real snapshot count is bigger than intersection of vmsnapshotmodel and real snapshot count");
					if(snapshotList.size() > 0){
						snapshotUrls.removeAll(snapshotList);
						if(snapshotUrls.size() > 0){
							for (String tmp1 : snapshotUrls) {
								vmwaremanager.removeSnapshot(vmdetail.getVmName(), vmdetail.getUuid(), tmp1);
							}
						}
					}
				}
				
				return tmpSessGuid.toArray(EMPTY);

			} else {
				return EMPTY;
			}

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	class VMWareInfo {
		String vmName;
		long memoryMB;
		int cpuCount;
		String guestID;
		String vmVersion;
		String storage;
		String esxHost;
		String dcName;
		String hostname;
		String username;
		String password;
		String protocol;
		int port;
		VMwareConnParams exParams;
		boolean isProxyEnabled;
		String moref;
		String resourcePool;
		String resourcePoolmoref;
		boolean isUEFI;
		
		public VMWareInfo()
		{
			exParams = new VMwareConnParams();
		}
		
		public void setExParams(CAVirtualInfrastructureManager vmwaremanager)
		{
			try {
				exParams.setThumbprint(vmwaremanager.getThumbprint());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void setExParams(VMwareConnParams exParams)
		{
			this.exParams = exParams;
		}
		
		public void setThumbprint(String thumbprint)
		{
			this.exParams.setThumbprint(thumbprint);
		}
	}
	
	class SupportedDiskControllerType {
		boolean IDE = true;
		boolean SCSI = true;
		boolean SAS = false;
		boolean SATA = false;
	}

	void setSendVMInfoToMonitorFlag(String afguid, boolean flag) {
		try {
			String xml = CommonUtil.D2DInstallPath
					+ "Configuration\\repository.xml";
			ProductionServerRoot prodRoot = RepositoryUtil.getInstance(xml)
					.getProductionServerRoot(afguid);
			if (prodRoot != null) {
				prodRoot.getReplicaRoot().setSendToMonitor(flag);
				RepositoryUtil.getInstance(xml).saveProductionServerRoot(
						prodRoot);
			}
		} catch (Exception e) {
			log
					.error("Failed to save send-to-monitor flag to repository.xml!!!");
		}
	}

	boolean getSendVMInfoToMonitorFlag(String afguid) {
		try {
			String xml = CommonUtil.D2DInstallPath
					+ "Configuration\\repository.xml";
			ProductionServerRoot prodRoot = RepositoryUtil.getInstance(xml)
					.getProductionServerRoot(afguid);
			if (prodRoot != null) {
				ReplicaRoot repRoot = prodRoot.getReplicaRoot();
				return repRoot.isSendToMonitor();
			}
		} catch (Exception e) {
			log.error("Failed to get send-to-monitor flag.");
		}
		return false;
	}
	
	private boolean saveSnapshotUrl(File sessonFolder, List<SessionInfo> sessions,
			String snapshotUrl, String afguid, String bootableSnapshotGuid,
			long backupTime,ADRConfigure adrConfigure) {

		if(sessions == null || sessions.isEmpty()){
			log.error("sessions is null or empty.");
			return false;
		}
		
		try {
			
			SessionInfo lastSession = sessions.get(sessions.size() - 1);
			String lastSessionName = lastSession.getSessionName();
			String lastSessionGuid = lastSession.getSessionGuid();
			StringBuilder sessionGuids = new StringBuilder();
			for (SessionInfo sessionInfo : sessions) {
				if(sessionGuids.length() == 0){
					sessionGuids.append(sessionInfo.getSessionGuid());
				}else {
					sessionGuids.append("|" + sessionInfo.getSessionGuid());
				}
			}

			SortedSet<VMSnapshotsInfo> snapshotSet = getSnapshotWithRealSnapshot();

			int snapshotCount = snapshotSet.size();
			VMSnapshotsInfo vsinfo = new VMSnapshotsInfo(lastSessionName, sessionGuids.toString(), snapshotUrl,
					backupTime);
			vsinfo.setBootableSnapGuid(bootableSnapshotGuid);
			vsinfo.setSnapNo(vmwareSnapshot.getSnapNo());
			vsinfo.setLocalTime(HACommon.date2String(backupTime));
			vsinfo.setPowerOffBackup(adrConfigure.isPartialAdrconfigure());
			snapshotSet.add(vsinfo);
			
			VirtualMachineInfo vmi = vmwareSnapshot.getInternalVMInfo(afguid);
			boolean result;
			if (vmi == null) {
				vmi = new VirtualMachineInfo(1, vmDetails.getUuid(), afguid);
				result = vmwareSnapshot.putSnapShot(vmi, snapshotSet);
			} else {
				result = vmwareSnapshot.replaceSnapshots(vmi, snapshotSet);
			}
			
			SortedSet<VMSnapshotsInfo> newSnapshotSet = getSnapshotWithRealSnapshot();
			if (newSnapshotSet.size() != snapshotCount + 1) {
				log.error("Upload VMSnapshotsModel.xml failed, the new snapshot count is not equal to original count plus one");
			}

			if (!result) {
				log.error("Failed to upload VMSnapshotsModel.xml to VMWare storage!!!");
				log.error("Delete Bootable Snapshot: " + bootableSnapshotGuid);
				try {
					vmwareOBJ.removeSnapshot(vmDetails.getVmName(), vmDetails
							.getUuid(), bootableSnapshotGuid);
				} catch (Exception e) {
					log.error("Failed to delete bootable snapshot." + e.getMessage(),e);
					
					RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afguid);
					long jobID = jobMonitor.getId();
					
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_DELETE_SNAPSHOT);
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, afguid);
					
					return result;
				}
				
				log.error("Delete Snapshot: " + snapshotUrl);
				try {
					vmwareOBJ.removeSnapshot(vmDetails.getVmName(), vmDetails
							.getUuid(), snapshotUrl);
				} catch (Exception e) {
					log.error("Failed to delete data snapshot." + e.getMessage(),e);
					RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afguid);
					long jobID = jobMonitor.getId();
					
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_DELETE_SNAPSHOT);
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, afguid);
					return result;
				}
				
				log.error("Delete backup meta data uploaded to storage!!!");
				deleteBackupMetaDataFromStorage(sessonFolder.listFiles(),
						lastSessionName, lastSessionGuid);
				return result;
			}

			return result;

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return false;
		}
	}

	private SortedSet<VMSnapshotsInfo> getSnapshotWithRealSnapshot() {

		SortedSet<VMSnapshotsInfo> repositorySnapshots = null;

		try {
			// check snapshot whether exists
			// if there are snapshots in configure file and no snapshot in
			// vm.This means snapshots
			// are deleted manually and all changes are merged into base disk
			Map<String, String> vmSnapshots = vmwareOBJ.listVMSnapShots(
					vmDetails.getVmName(), vmDetails.getUuid());
			Set<String> keys = vmSnapshots.keySet();
			log.info("Total snapshot for vm:" + vmDetails.getVmName() + " is:" + vmSnapshots.size());

			repositorySnapshots = vmwareSnapshot.getSnapshots(afguid);
			if (repositorySnapshots != null) {
				log.info("repositorySnapshots size:" + repositorySnapshots.size());
			}

			ArrayList<VMSnapshotsInfo> tmp = new ArrayList<VMSnapshotsInfo>();
			for (VMSnapshotsInfo vm : repositorySnapshots) {
				if (!keys.contains(vm.getSnapGuid())) {
					tmp.add(vm);
					log.info("snapshot:" + vm.getSnapGuid() + " for session:" + vm.getSessionName()
							+ " doesn't exists, will remove it from repositorySnapshots.");
				}
			}
			if (tmp.size() > 0) {
				// remove all unexisted snapshot
				try {
					repositorySnapshots.removeAll(tmp);
				} catch (Exception e) {
				}
				// remove shapshot url in VMSnapshot.xml that are deleted from
				// snapshots
			}

		} catch (Exception e1) {
			log.error("Failed to get snapshots.", e1);
		}

		if (repositorySnapshots == null)
			repositorySnapshots = new TreeSet<VMSnapshotsInfo>();
		
		log.info("return repositorySnapshots with size:" + repositorySnapshots.size());
		return repositorySnapshots;

	}
	
	private boolean putRepositoryToDatastore(String vmName, String vmUUID) {
		String localFile = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
		String remoteFile = "repository.xml";
		InputStream stream = null;
		try {
			stream = vmwareOBJ.getVMConfig(vmName, vmUUID, remoteFile);
		}catch (Exception e) {
			log.warn(String.format("Fail to check if file '%s' exists on vmware datastore. ", remoteFile), e);
			return false;
		}finally {
			if (stream != null)
				try {
					stream.close();
				}catch (Exception e){
					return false;
				}
		}
		try {
			if (stream != null && !vmwareOBJ.deleteVMConfig(vmName, vmUUID, remoteFile)) {
				log.warn(String.format("Fail to delete %s on vmware datastore. ", remoteFile));
				return false;
			}
		}catch (Exception e) {
			log.warn(String.format("Fail to delete the existing file '%s' on vmware datastore. ", remoteFile), e);
			return false;
		}
		try {
			vmwareOBJ.putVMConfig(vmName, vmUUID, remoteFile, localFile);
		}catch (Exception e) {
			log.error(String.format("Fail to update %s to vmware datastore. ", remoteFile), e);
			return false;
		}
		log.info(String.format("Upload %s to vmware datastore successfully. ", remoteFile));
		return true;
	}

	private boolean putBackupMetaDataToStorage(VMWareInfo vmInfo,File session, String sessGuid, long jobID, String afguid) {

		String localFilePath = "";
		File[] files = session.listFiles();
		Map<String, String> fileUrisMapping = new HashMap<String, String>();
		
		for (File file : files) {
			
			if(file.getName().equals(CommonUtil.ADRCONFIG_XML_FILE)
					   || file.getName().equals(CommonUtil.PARTIAL_ADRCONFIG_File)
//					   || file.getName().equals(CommonUtil.BLOCKCTF_FILE)
					   || file.getName().equals(CommonUtil.ADRINFOC_DRZ_FILE)){
				
				
				localFilePath = file.getAbsolutePath();
				String remoteFileName = session.getName() + "-" + sessGuid + "-"
						+ file.getName();
				fileUrisMapping.put(localFilePath, remoteFileName);
				
			}
			
		}

		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afguid); 
		boolean result = false;
		HARetryStrategy strategy = HARetryStrategy.newStrategy(10, 6);
		try {
			result = VMwareUploadManager.uploadToStorage(strategy, vmwareOBJ,
					vmDetails, fileUrisMapping, afguid);
		} catch (Throwable e) {
			log.warn("Exception during uploading metadata files.", e);
		}

		synchronized (jobMonitor) {
			if (isVirtualStandbyCancelled(jobMonitor))
				return false;
		}

		if (!result) {
			// In the worst condition, each file makes session to ESX time out.
			int retryTimes = fileUrisMapping.size(); 
			while (retryTimes-- > 0) {
				log.info("Create a new connection to ESX, and retry uploading metadata files.");
				try {
					createNewConnection(vmInfo);
				} catch (Throwable e) {
					log.error("Failed to create connection.", e);
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
													new String[] { msg,"", "", "", "" }, afguid);
					return false;
				}

				try {
					result = VMwareUploadManager.uploadToStorage(strategy,
							vmwareOBJ, vmDetails, fileUrisMapping, afguid);
				} catch (Throwable e) {
					log.error("Exception during retrying uploading metadata files.", e);
				}

				synchronized (jobMonitor) {
					if (isVirtualStandbyCancelled(jobMonitor))
						return false;
				}
				
				if (result)
					break;
			}
		}
		
		return result;
	}
	
	
	private void deleteBackupMetaDataFromStorage(File[] files, String sessionName, String sessionGuid) {
		try {
			List<String> fileNames = new ArrayList<String>();
			
			String localSessionGuid = null;
			{
				String[] tokens = sessionGuid.split("[|]");
				localSessionGuid = tokens[tokens.length-1];
			}
	
			for (File file : files) {
				if(file.getName().equals(CommonUtil.ADRCONFIG_XML_FILE)
				   || file.getName().equals(CommonUtil.PARTIAL_ADRCONFIG_File)
//				   || file.getName().equals(CommonUtil.BLOCKCTF_FILE)
				   || file.getName().equals(CommonUtil.ADRINFOC_DRZ_FILE)){
					
					String configFile = sessionName + "-" + localSessionGuid + "-"
					+ file.getName();
					fileNames.add(configFile);
					
				}
				
			}
	
			HARetryStrategy strategy = HARetryStrategy.newStrategy(10, 6);
			VMwareUploadManager.deleteFromStorage(strategy, vmwareOBJ, vmDetails,
						fileNames);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
	}

	private CAVMDetails createVM(VMWareInfo vmInfo) throws Exception {
		
		CAVMDetails vmDetails = null;
		try {
			log.info("Attempting to create VM " + vmInfo.vmName);
			
			vmDetails = vmwareOBJ.createVMwareVM(vmInfo.vmName,
					vmInfo.memoryMB, vmInfo.cpuCount, vmInfo.guestID,
					vmInfo.vmVersion, vmInfo.storage, vmInfo.esxHost,
					vmInfo.dcName, null, null, vmInfo.resourcePoolmoref, vmInfo.isUEFI);
			
		} catch (Exception e) {
			log.error("Failed to create VM." + e.getMessage(),e);
			throw e;
		}
		return vmDetails;
	}
	//fanda03 fix 161256
	private boolean checkDataStoreExist( String esxHost, String dataStoreName ,long jobID, ReplicationJobScript jobScript ) throws Exception {
		
		boolean isExist = true;
		try{
			ESXNode thisNode = null;
			ArrayList<ESXNode> nodeList;
		
			nodeList = vmwareOBJ.getESXNodeList();
		
			for (ESXNode esxNode : nodeList) {
				if(esxNode.getEsxName().equals(esxHost)){
					thisNode = esxNode;
					break;
				}
			}
			if( thisNode !=null &&dataStoreName!=null ){
				 VMwareStorage[] storages = vmwareOBJ.getVMwareStorages( thisNode,
						new String[]{dataStoreName} );
				 if( storages==null || storages.length == 0 ){
					 isExist = false;
				 }
			}
		}
		catch( Exception e ) {
			log.error("Failed to check datastore information" + e.getMessage(),e);
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		}
		if(!isExist) {
			String msg = ReplicationMessage.getResource(
					ReplicationMessage.REPLICATION_FAIL_DATASTORE_CHANGE, dataStoreName );
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			String logMsg = "the dataStore named "+dataStoreName + " does not exist in ESX/VCenter:"+ esxHost ;
			log.error(logMsg);
		}
		return isExist;
	}
	
private Map<String, Disk_Info> addVirtualDiskToVM(SortedSet<Disk> disks,
			ReplicationJobScript jobScript, VMWareInfo vmInfo)
			throws Exception {

		
		log.debug("Base disk replication!!!!!");
		Map<String, Disk_Info> existingMapping = removeVirtualDiskFromVM(jobScript,vmInfo);
		
		List<DiskDestination> diskDestinations = jobScript.getReplicationDestination().get(0).getDiskDestinations();
		
		Map<String, Disk_Info> diskMapping = new HashMap<String, Disk_Info>();		
		
		Set<String> keys = existingMapping.keySet();
		
		for (Disk d : disks) {
			
			String diskSig = HACommon.handleGTPDiskSignature(d);
			boolean findDisk = false;
			for (String signature : keys) {
				if (signature.equalsIgnoreCase(diskSig)) {
					findDisk = true;
					break;
				}
			}
			if(findDisk){
				//This disk has been replicated before and is not removed
				//Do not re-add.
				continue;
			}
			
			CAVirtualDisk virtualDisk = new CAVirtualDisk();
			virtualDisk.setThinProvisioning(true);
			long tmp = (long) CommonUtil.roundUp(d.getSize() / 1024.0 / 1024.0);
			virtualDisk.setCapacityinKB(tmp * 1024);
			String controllerType = d.getControllerType();
			virtualDisk.setDiskController(controllerType);
			virtualDisk.setDiskDataStore(HACommon.getDiskStorage(diskDestinations, d));
			try {

				log.info("Adding virtual disk " + d.getSignature() + " to VM.");
				
				Disk_Info info = vmwareOBJ.addVirtualDisk(
						vmDetails.getVmName(), vmDetails.getUuid(),
						vmInfo.esxHost, virtualDisk);
				
				if (info == null) {
					log.error("Failed to add virtual disk to VM."
							+ d.getSignature());
					return null;
				}
				
				diskMapping.put(HACommon.handleGTPDiskSignature(d), info);

			} catch (Exception e) {
				log.error("Failed to add virtual disk to VM."
						+ d.getSignature());
				log.error(CommonUtil
						.getExceptionStackMessage(e.getStackTrace()));
				log.error(e.getMessage());
				throw e;
			}
		}
		
		if(!existingMapping.isEmpty()){
			diskMapping.putAll(existingMapping);
		}
		
		return diskMapping;
	}

	private Map<String, Disk_Info> removeVirtualDiskFromVM(ReplicationJobScript jobScript,VMWareInfo vmInfo) throws Exception {

		try {
			
			RepJobMonitor jobMonitor = CommonService.getInstance()
									.getRepJobMonitorInternal(jobScript.getAFGuid());

			long jobID = jobMonitor.getId();
	
			Map<String, Disk_Info> diskMapping = new HashMap<String, Disk_Info>();
			int vddkPort = CommonUtil.getCustomizedVDDKPort();
			
			ArrayList<Disk_Info> diskInfos = vmwareOBJ.getCurrentDiskInfo(vmDetails.getVmName(), vmDetails
					.getUuid());
			
			if (diskInfos == null || diskInfos.size() == 0) {
				return diskMapping;
			}
			
			Iterator<Disk_Info> it = diskInfos.iterator();
			while(it.hasNext()){
				
				Disk_Info diskInfo = it.next();
		
				String childDiskSig = ReplicationProxy.getInstance().getVMDiskSignature(jobScript,HACommon.getHostNameForVddk(vmInfo.hostname,vmInfo.port),
						vmInfo.username, vmInfo.password, vmInfo.moref, vddkPort,vmInfo.exParams, "", diskInfo.getdiskURL(),
						jobID + "");

				if (StringUtil.isEmptyOrNull(childDiskSig) || childDiskSig.equals("0")) {
					vmwareOBJ.removeVirtualDisk(vmDetails.getVmName(), vmDetails.getUuid(), 
												diskInfo.getdiskURL());
				}else{
					diskMapping.put(HACommon.fillSignature(childDiskSig), diskInfo);
				}
				
				it.remove();
				
			}
			
			return diskMapping;
			
		} catch (Exception e) {
			
			log.error("Failed to remove virtual disk from vm.");
			throw e;
		}

	}

	private void removeDiskWihoutFile(SortedSet<Disk> disks, File session) {
		ArrayList<Disk> unexistedDisks = new ArrayList<Disk>();
		for (Disk d : disks) {
			String sig = d.getSignature();
			String fileName = getVHDFileName(session, sig);
			File d2d = new File(fileName);
			if (!d2d.exists()) {
				unexistedDisks.add(d);
			}
		}
		disks.removeAll(unexistedDisks);
	}

	private String getVHDFileName(File session, String signature) {

		return session.getAbsolutePath() + "\\disk" + signature + ".D2D";
	}

	/**
	 * get all VM dataStore' free size, used for later
	 * compareSessionSizeWithStorageSize
	 * 
	 * @param vmInfo
	 * @return
	 */
	private HashMap<String, DiskInfo> getAllStorageFreeSize(VMWareInfo vmInfo) {

		ESXNode node = null;
		String nodeName = vmInfo.esxHost;

		try {
			ArrayList<ESXNode> nodeList = vmwareOBJ.getESXNodeList();
			for (ESXNode esxNode : nodeList) {
				if (esxNode.getEsxName().equals(nodeName)) {
					node = esxNode;
					break;
				}
			}
		} catch (Exception e) {
			log.error("Failed to get esxnode:" + nodeName);
			log.error(e.getMessage());
			return null;
		}

		VMwareStorage[] results = new VMwareStorage[0];
		try {
			results = vmwareOBJ.getVMwareStorages(node, null);

		} catch (Exception e) {
			log.error("Failed to get vm storages for this ESXNode by storage."
					+ nodeName);
			log.error("Error msg:" + e.getMessage());
			return null;
		}
		
		HashMap<String, DiskInfo> storageFreeSizeGroup = new HashMap<String, DiskInfo>();
		
		for(VMwareStorage storage : results){
			DiskInfo diskInfo = new DiskInfo(storage.getName(), storage.getTotalSize(), storage.getFreeSize());
			storageFreeSizeGroup.put(storage.getName(), diskInfo);
		}

		return storageFreeSizeGroup;
	}

	
	//E:\d2d\cheji10-w2k3-1\VStore\S0000000001 
	private String getSessionRootPath(String sessionFolder) {
		int index = sessionFolder.lastIndexOf("VStore");
		if(index>0) {
			return sessionFolder.substring(0,index-1);
		}
		else {
			log.warn("Failed to get session root");
			return sessionFolder;
		}
	}
	
//	/*
//	 * Check block size on storage,different block size support
//	 * different virtual disk size
//	 *  Block Size   Largest virtual disk on VMFS-3 
//	 *	1MB          256GB 
//	 *	2MB          512GB 
//	 *	4MB 		 1TB 
//	 *	8MB 		 2TB 
//	*/ 
//	private void checkStorageBlockSize(ReplicationJobScript jobScript, VMWareInfo vmInfo, SortedSet<Disk> disks,long jobID)throws Exception{
//		
//		List<DiskDestination> diskDests = jobScript.getReplicationDestination().get(0).getDiskDestinations();
//		
//		ESXNode node = new ESXNode();
//		node.setEsxName(vmInfo.esxHost);
//		node.setDataCenter(vmInfo.dcName);
//		
//		Map<Integer, Long> mapping = new HashMap<Integer, Long>();
//		mapping.put(HACommon.STORAGE_BLOCK_SIZE_1_MB, HACommon.VIRTUAL_DISK_MAX_SIZE_256_GB);    //256GB
//		mapping.put(HACommon.STORAGE_BLOCK_SIZE_2_MB, HACommon.VIRTUAL_DISK_MAX_SIZE_512_GB);    //512GB
//		mapping.put(HACommon.STORAGE_BLOCK_SIZE_4_MB, HACommon.VIRTUAL_DISK_MAX_SIZE_1024_GB);   //1TB
//		mapping.put(HACommon.STORAGE_BLOCK_SIZE_8_MB, HACommon.VIRTUAL_DISK_MAX_SIZE_2024_GB);   //2TB
//		
//		for (Disk d : disks) {
//			String storageName = HACommon.getDiskStorage(diskDests, d);
//			
//			int VMFSVersion = vmwareOBJ.getDataStoreVMFSVersion(node, storageName);
//			String infoMsg = String.format("The esx[%s, %s] storageName[%s] VMFSVersion is [%d]", node.getEsxName(), node.getDataCenter(),storageName, VMFSVersion);
//			log.info(infoMsg);
//			//If storage format is VMFS-5, don't check the block size support.
//			if(VMFSVersion != 3){
//				continue;
//			}
//			
//			//If storage format is VMFS-3, check the block size. 
//			int blockSize = vmwareOBJ.getDataStoreBlockSize(node, storageName); //block size in MB
//			
//			if(blockSize==0){
//				log.info("return block size is zero and datastore is not VMFS. So no limitation on disk size.sroragename=" + storageName);
//				continue;
//			}
//			
//			if(mapping.get(blockSize) < d.getSize()){
//				
//				String msg = "";
//				long sizeInGB = mapping.get(blockSize)/1024/1024/1024; //GB
//				
//				int reasonableBlockSize = blockSize;
//				while (true) {
//					reasonableBlockSize = reasonableBlockSize * 2;
//					if(reasonableBlockSize > HACommon.STORAGE_BLOCK_SIZE_8_MB){
//						log.error("source disk is bigger than 2TB.No proper block size.");
//						msg = ReplicationMessage.getResource(
//								ReplicationMessage.REPLICATION_DEST_EXCEED_LIMIT);
//						break;
//					}
//					if(mapping.get(reasonableBlockSize) >= d.getSize()){
//						msg = ReplicationMessage.getResource(
//								ReplicationMessage.REPLICATION_DEST_BLOCK_SIZE,
//								storageName,node.getEsxName(),sizeInGB+"GB",reasonableBlockSize+"MB");
//						break;
//					}
//				}
//				
//				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
//												new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
//			
//				throw new Exception();
//				
//			}
//		}
//	}
	
	private int getGuestOSInfo(StringBuilder strGuestOSID,StringBuilder strGuestOSUefiFlag, ReplicationJobScript jobScript, BackupDestinationInfo backupDestinationInfo){
		int result = 0;
		try {
			String afguid = jobScript.getAFGuid();
			if(!isVSBWithoutHASupport() && HACommon.isTargetPhysicalMachine(afguid)){
				String guestOSID = BackupService.getInstance().getNativeFacade().GetGuestID();
				strGuestOSID.append(guestOSID);
				boolean isUEFI = BackupService.getInstance().getNativeFacade().IsFirmwareuEFI();
				strGuestOSUefiFlag.append(isUEFI);
			}
			else{
				//if the share folder has not been connected, to create the connection is required.
				result = getVsphereVMOSInfo(strGuestOSID,strGuestOSUefiFlag, jobScript, false, backupDestinationInfo);
			}
		} catch (Exception e) {
			log.error(e);
			result = 2;
		}
		
		return result;
	}
	
	private int getVsphereVMOSInfo(StringBuilder strGuestOSID,StringBuilder strGuestOSUefiFlag,ReplicationJobScript jobScript,
			boolean connect, BackupDestinationInfo backupDestinationInfo) throws ServiceException {
		int result = 1;

		if (backupDestinationInfo == null || backupDestinationInfo.getBackupDestination() == null)
			return result;

		String remotePath = backupDestinationInfo.getBackupDestination();
		if (StringUtil.isEmptyOrNull(remotePath)) {
			return result;
		}
		boolean isRemote = CommonUtil.isRemote(remotePath);

		String afGuid = jobScript.getAFGuid();
		try {

			if (!connect && isRemote) {
				try {
					connectToRemote(backupDestinationInfo,afGuid,-1);
				} catch (Exception e) {
					log.error("Failed to connect to share folder:"+e);
				}
			}

			// here we get the sessions
			List<String> dests = new ArrayList<String>();

			long ret = BrowserService.getInstance().getNativeFacade()
					.GetAllBackupDestinations(remotePath, dests);
			// the dests contains the dest in the time order, from old to new
			if (ret != 0)
				return result;

			for (String dest : dests) {
				if (!dest.endsWith("\\"))
					dest += "\\";
				File f = new File(dest + "VStore");
				File[] listSessions = f.listFiles();
				if (listSessions == null)
					continue;
				Arrays.sort(listSessions, new Comparator<File>() {

					@Override
					public int compare(File o1, File o2) {
						return o1.getName().compareTo(o2.getName());

					}
				});

				for (File session : listSessions) {
//					String vmConfig = session.getPath() + "\\VMSnapshotConfigInfo.vsci";
//					File tmp = new File(vmConfig);
//					if(!tmp.exists()){
//						log.warn(vmConfig + "does not exist.This session will be skipped.");
						// if MSP Manual Conversio, check BackupInfo.XML file
//						if (isMSPManualConversion() || jobScript.getBackupToRPS() || jobScript.getVSphereBackupType() == VSphereBackupType.HYPERV_HBBU_BACKUP) {
							log.info("Try to get VM OS Information from BackupInfo.");
							BackupInfo backupInfo = getSessionBackupInfo(session);
							if (backupInfo != null && backupInfo.getServerInfo() != null) {
								strGuestOSID.append(ManualConversionUtility.getGuestOSID(backupInfo));

								strGuestOSUefiFlag.append(ManualConversionUtility.getBootFirmwareUEFIFlag(backupInfo));
								result = 0;
								return result;
							}
//						}
//						continue;
//					}
//					try {
//						String guestOSID = vmwareOBJ.getGuestOSIdFromSerializedFile(vmConfig);
//						strGuestOSID.append(guestOSID);
//						strGuestOSUefiFlag.append(vmwareOBJ.getGuestOSIsUefiFromSerializedFile(vmConfig));
//						result = 0;
//						return result;
//					} catch (Exception e) {
//						log.error("Failed to get the guest OS ID " + vmConfig, e);
//					}
				}
			}

		} finally {
			if (!connect && isRemote) {
				try {
					closeRemoteConnect(backupDestinationInfo);
				} catch (Exception e) {
					log.debug(e.getMessage());
				}
			}
		}
		return result;
	}
	
	protected String getGuestOSID(){
		//Window 8 guest OS type
		//windows8Guest
		//windows8_64Guest
		//windows8Server64Guest
//		Set<String> win8GuestOS = new HashSet<String>(3);
//		win8GuestOS.add("windows8Guest");
//		win8GuestOS.add("windows8_64Guest");
//		win8GuestOS.add("windows8Server64Guest");
		
		String guestID = BackupService.getInstance().getNativeFacade().GetGuestID();
		if(!"windows8Guest".equals(guestID) &&
			!"windows8_64Guest".equals(guestID) &&
			!"windows8Server64Guest".equals(guestID))
		{
			guestID = null;
		}
		
		return guestID;
	}
	
	abstract protected ReplicaRoot loadProductionServerRootFromRepository(ReplicationJobScript jobScript) throws Exception;
	protected ReplicaRoot loadProductionServerRoot(ReplicationJobScript jobScript, CAVirtualInfrastructureManager vmManager, boolean downloadFromDatastore) throws Exception{
		ReplicaRoot repRoot = loadProductionServerRootFromRepository(jobScript);
		if (repRoot == null && !downloadFromDatastore)
			return null;
		
		if (repRoot == null && downloadFromDatastore) {
			ReplicationDestination  repDest = jobScript.getReplicationDestination().get(0);
			if (repDest.getVmUUID() == null)
				return null;
			
			{
				// If VM UUID in job script is not null, try download repository from datastore
				ProductionServerRoot prodRoot = HAService.getInstance().ifVmOwnedByAgent(vmManager, repDest.getVmName(), repDest.getVmUUID(), jobScript.getAFGuid());
				if (prodRoot == null) {
					log.warn("Production server root from repository is empty.");
					return null;
				}
				if (prodRoot.getReplicaRoot() == null) {
					log.warn("Replica root of production server root from repository is empty.");
					return null;
				}
				
				HAService.getInstance().updateLocalRepository(prodRoot);
			}
			
			repRoot = loadProductionServerRootFromRepository(jobScript);
		}
		
		return repRoot;
	}

	
	
	
}
