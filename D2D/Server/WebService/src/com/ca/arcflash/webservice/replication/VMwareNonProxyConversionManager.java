package com.ca.arcflash.webservice.replication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.Disk;
import com.ca.arcflash.ha.model.manager.VMwareSnapshotModelManager;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.vmwaremanager.Disk_Info;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVMDetails;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.replication.VMWareBaseReplicationCommand.VMWareInfo;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;

public class VMwareNonProxyConversionManager extends VMwareConversionManager {

	private static final Logger log = Logger
			.getLogger(VMwareNonProxyConversionManager.class);
	
	private boolean isHotAddFail = false;
	
	//VDDK ERROR
	public static final long NETWORK_ERROR    = 20000;
	public static final long VMDK_ERROR       = 21000;
	public static final long DISK_ERROR       = 22000;
	public static final long CREDENTIAL_ERROR = 23000; 
	public static final long INTERNAL_ERROR   = 25000;  
	public static final long SPACE_ERROR      = 26000;   
	public static final long VMDK_FILE_ERROR  = 27000;       
	public static final long VMDK_LICENSE_ERROR = 28000;                                                 
	
	//D2D ERROR
	public static final long UNABLE_READ_BACKUP = 30001;
	public static final long OTHER_READ_ERRORS = 30002;

	//define vddk error code to commmon error mapping
	private static final Properties errorMapping = new Properties();
	
	static{
		//load error code mapping from vddkerrocode.properties
		genereateErrorCodeMapping();
	}
	

	public VMwareNonProxyConversionManager() {
	}

	public VMwareNonProxyConversionManager(
			CAVirtualInfrastructureManager vmwareManager,
			CAVMDetails vmDetails, VMWareInfo vmInfo,
			ReplicationJobScript jobScript, SessionInfo session, long jobID,
			boolean isSmartCopy, SessionInfo beginD2DFile,
			SessionInfo endD2DFile,ADRConfigure adrConfigure,VMwareSnapshotModelManager vmwareSnapshot) {

		super(vmwareManager, vmDetails, vmInfo, jobScript, session, jobID,
				isSmartCopy, beginD2DFile, endD2DFile,adrConfigure,vmwareSnapshot);

	}

	public static VMwareNonProxyConversionManager getInstance(
			CAVirtualInfrastructureManager vmwareManager,
			CAVMDetails vmDetails, VMWareInfo vmInfo,
			ReplicationJobScript jobScript, SessionInfo session, long jobID,
			boolean isSmartCopy, SessionInfo beginD2DFile,
			SessionInfo endD2DFile,ADRConfigure adrConfigure,VMwareSnapshotModelManager vmwareSnapshot) {

		return new VMwareNonProxyConversionManager(vmwareManager, vmDetails,
				vmInfo, jobScript, session, jobID, isSmartCopy, beginD2DFile,
				endD2DFile,adrConfigure,vmwareSnapshot);

	}

	@Override
	public int doSANModeVirtualConversion(SortedSet<Disk> disks,
			Map<String, Disk_Info> baseDiskMapping, String dummySnapshotGuid, boolean tryHotAddOnly) {

		isHotAddFail = false;
		
		NativeFacade nativeFacade = BackupService.getInstance()
				.getNativeFacade();

		int vddkPort = CommonUtil.getCustomizedVDDKPort();
		int vmdkDiskType = getVMWareDiskType(session.getSessionCompressType());

		SortedSet<Disk> sanDisk = new TreeSet<Disk>();

		Map<String, String> hotAddDiskMapping = new HashMap<String, String>();

		long isComplete = 1;

		try {

			if (isSmartCopy) {

				isComplete = doSmartCopyConversion(disks, 
						dummySnapshotGuid, vddkPort, sanDisk, hotAddDiskMapping, tryHotAddOnly);

			} else {

				isComplete = doNonSmartCopyConversion(disks, 
						dummySnapshotGuid, vddkPort, sanDisk, hotAddDiskMapping, tryHotAddOnly);

			}

			if (isComplete == HACommon.REPLICATE_CANCEL) {
				return HACommon.REPLICATE_CANCEL;
			}

			if (!sanDisk.isEmpty()) {
				log.info(sanDisk.size() + " san disks are replicated.");
				disks.removeAll(sanDisk);
			}

		} catch (Exception e) {
			log.error("Error in san conversion."+e.getMessage(),e);
		} finally {
			try {

				deleteDummySnapshot(dummySnapshotGuid);

			} catch (Exception e) {
				log.error("Failed to revert or delete dummy snapshot.Conversion exits.");
				log.error(e.getMessage(),e);
				return HACommon.REPLICATE_FAILURE;
			}
		}

		try {

			log.info("hotAddDiskMapping size " + hotAddDiskMapping.size());

			String afGuid = jobScript.getAFGuid();
			
			BackupDestinationInfo backupDestinationInfo = null;
			try {
				backupDestinationInfo = BaseReplicationCommand.getBackupDestinationInfo(jobScript, false, jobID);
			} catch (Exception e) {
				log.error("Failed to get backup destination, " + e.getMessage());
			}
			
			for (Map.Entry<String, String> entry : hotAddDiskMapping.entrySet()) {

				log.info("UpdateDiskSigViaNBD start........");
				log.info("d2d file: " + entry.getKey());
				log.info("vmdk file: " + entry.getValue());
				isComplete = nativeFacade.UpdateDiskSigViaNBD(afGuid,entry.getKey(),
						vmInfo.moref, vmInfo.hostname, vmInfo.username,
						vmInfo.password, entry.getValue(), vddkPort, vmInfo.exParams,
						vmdkDiskType, "", jobID + "", jobScript.getBackupDestType(),
						backupDestinationInfo.getNetConnUserName(), backupDestinationInfo.getNetConnPwd());
				log.info("return code: " + isComplete);
				log.info("UpdateDiskSigViaNBD end.......");
			}

			log.info("hotAddDiskMapping update disk signature end.");

		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

		return HACommon.REPLICATE_SUCCESS;

	}

	public int doNBDModeVirtualConversion(SortedSet<Disk> disks,
										  Map<String, Disk_Info> baseDiskMapping) {
		isHotAddFail = false;
		log.info("disk counts" + disks.size());

		if (disks.isEmpty()) {
			log.info("no NBD disks.");
			return HACommon.REPLICATE_SUCCESS;
		}

		try {
			generateNBDDiskMapping(baseDiskMapping, true);
		} catch (Exception e) {
			return HACommon.REPLICATE_FAILURE;
		}

		int vddkPort = CommonUtil.getCustomizedVDDKPort();

		int isComplete = 1;

		try {

			if (isSmartCopy) {

				isComplete = doSmartCopyConversion(disks, "", vddkPort, null, null, false);

			} else {

				isComplete = doNonSmartCopyConversion(disks, "", vddkPort, null, null, false);

			}
			
			if (isHotAddFail) {
				String hostName = getHostName(jobScript);
				String msg = ReplicationMessage.getResource(
						ReplicationMessage.REPLICATION_HOT_ADD_MODE_FAIL, hostName);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}

			return isComplete;

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return HACommon.REPLICATE_FAILURE;
		}
	}

	private int doSmartCopyConversion(SortedSet<Disk> disks, String dummySnapshotGuid,	int vddkPort, 
			SortedSet<Disk> sanDisk, Map<String, String> hotAddDiskMapping, boolean tryHotAddOnly) throws Exception {

		NativeFacade nativeFacade = BackupService.getInstance()
				.getNativeFacade();

		Set<Entry<String, Disk_Info>> entries = null;
		
		if(StringUtil.isEmptyOrNull(dummySnapshotGuid)){
			entries = nbdDiskMapping.entrySet();
		}else{
			entries = sanDiskMapping.entrySet();
		}

		RepJobMonitor jobMonitor = CommonService.getInstance()
				.getRepJobMonitorInternal(jobScript.getAFGuid());

		int vmdkDiskType = getVMWareDiskType(session.getSessionCompressType());

		String afGuid = jobScript.getAFGuid();
		
		long isComplete = 1;
		int ret = HACommon.REPLICATE_SUCCESS;
		
		List<Long> errorCode = new ArrayList<Long>(1);
		
		for (Disk d : disks) {
			if(isJobCancelled()) {
				return HACommon.REPLICATE_CANCEL;
			}
			
			long blockSize = getDataStoreBlockSize(d);
			
			for (Entry<String, Disk_Info> entry : entries) {
				if (entry.getKey().equalsIgnoreCase(
						HACommon.handleGTPDiskSignature(d))) {

					synchronized (jobMonitor) {
						// reset for each disk replication.
						//jobMonitor.setFirst(true);
						//jobMonitor.setRepTransedSize(0);
						jobMonitor.setRepTotalSize(0);
						jobMonitor.setRepStartTime(System.currentTimeMillis());
						jobMonitor.setRepStartNanoTime(System.nanoTime());
					}

					String startD2DFile = getVHDFileName(beginSession, d
							.getSignature());
					String endD2DFile = getVHDFileName(session, d
							.getSignature());

					log.info(d.getDiskNumber());
					log.info(d.getSignature());
					log.info(startD2DFile + "," + vmInfo.moref + ","
							+ HACommon.getHostNameForVddk(vmInfo.hostname,vmInfo.port) + "," + vmInfo.username + ","
							+ entry.getValue().getdiskURL() + "," + vddkPort
							+ "," + vmdkDiskType + "," + dummySnapshotGuid);

					errorCode.clear();
					
					String d2dDiskFile = "disk" + d.getSignature() + ".D2D";
					{
						String msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_CONVERT_DISK_IMAGE_START, d2dDiskFile, entry.getValue().getdiskURL());
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					}
					BackupDestinationInfo backupDestinationInfo = null;
					try {
						backupDestinationInfo = BaseReplicationCommand.getBackupDestinationInfo(jobScript, false, jobID);
					} catch (Exception e) {
						log.error("Failed to get backup destination, " + e.getMessage());
					}
					
					isComplete = nativeFacade.D2D2VmdkSmartCopy(afGuid,startD2DFile,
							endD2DFile, vmInfo.moref, HACommon.getHostNameForVddk(vmInfo.hostname,vmInfo.port),
							vmInfo.username, vmInfo.password, entry.getValue()
									.getdiskURL(), vddkPort,vmInfo.exParams, vmdkDiskType,
									tryHotAddOnly ? "[hotadd]" + dummySnapshotGuid :  dummySnapshotGuid, jobID + "",blockSize, jobScript.getBackupDestType(), 
											backupDestinationInfo.getNetConnUserName(), backupDestinationInfo.getNetConnPwd(), errorCode);

					log.info("conversion module return value: " + isComplete);

					if (isComplete == HACommon.REPLICATE_SUCCESS || isComplete == HACommon.REPLICATE_HOTADD_SUCCESS) {
						String msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_CONVERT_DISK_IMAGE_COMPLETED, d2dDiskFile);
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					} else if (isComplete == HACommon.REPLICATE_CANCEL) {
						log.info("conversion job was canceled.");
					} else {
						String msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_CONVERT_DISK_IMAGE_FAILED, d2dDiskFile);
						
						long logLevel = Constants.AFRES_AFALOG_WARNING;
						if(StringUtil.isEmptyOrNull(dummySnapshotGuid))
							logLevel = Constants.AFRES_AFALOG_ERROR;
						HACommon.addActivityLogByAFGuid(logLevel, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					}
					
					if (isComplete == HACommon.REPLICATE_SUCCESS) {
						log.info(d.getDiskNumber() + " replicated.");
						if(sanDisk != null){
							sanDisk.add(d);
						}
						break;

					} else if (isComplete == HACommon.REPLICATE_HOTADD_SUCCESS) {
						log.info("REPLICATION_HOTADD_SUCCESS");
						log.info("return code: " + isComplete);
						log.info("begin d2d file: " + startD2DFile);
						log.info("begin d2d file: " + startD2DFile);
						log.info("vmdk url: " + entry.getValue().getdiskURL());

						if(sanDisk != null){
							sanDisk.add(d);
						}
						
						if(hotAddDiskMapping != null){
							hotAddDiskMapping.put(endD2DFile, entry.getValue().getdiskURL());
						}
						
						break;
					} else if (isComplete == HACommon.REPLICATE_HOTADD_FAILURE) {
						log.info("REPLICATION_HOTADD_FAILURE");
						log.info("return code: " + isComplete);
						log.info("end d2d file: " + endD2DFile);
						log.info("vmdk url: " + entry.getValue().getdiskURL());
						if(hotAddDiskMapping != null){
							hotAddDiskMapping.put(endD2DFile, entry.getValue()
									.getdiskURL());
						}
						
						isHotAddFail = true;
						ret = HACommon.REPLICATE_HOTADD_FAILURE;
						
						break;
						
					} else if (isComplete == HACommon.REPLICATE_CANCEL) {

						return HACommon.REPLICATE_CANCEL;

					} else if (isComplete == HACommon.PASSWORD_FILE_DAMAGE) {
						log.error("conversion failed bacuse password file is damaged.");
						String msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_SESSION_PASSWORD_FILE_DAMAGE);
						
						//SAN mode and NDB mode may meet his same error code.
						//only print the msg in NBD model to avoid duplicated log.
						//hotAddDiskMapping is null in NBC mode, 
						if(hotAddDiskMapping == null)
							HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							  	new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						else
							log.error("In SAN mode, leave the following log to be printed in NBD mode:" + msg);
						
						return HACommon.REPLICATE_FAILURE;
					} else {
						// SAN mode is not suppoted.
						log.info(d.getDiskNumber() + " is not in san mode.");
						
						//if dummySnapshotGuid is null or empty string, conversion is running on NBD mode
						if(StringUtil.isEmptyOrNull(dummySnapshotGuid)){
							log.error("Replication in NBD mode failed.");
							log.error("return code: " + isComplete);
							
							printConversionFailureMessage(errorCode);
							
							return HACommon.REPLICATE_FAILURE;
						}
						
						break;
					}
				}
			}
		}

		return ret;

	}

	/*
	 * The last two parameters are out-parameter.
	 */
	private int doNonSmartCopyConversion(SortedSet<Disk> disks,	String dummySnapshotGuid,
			int vddkPort, SortedSet<Disk> sanDisk,Map<String, String> hotAddDiskMapping, boolean tryHotAddOnly) throws Exception {

		NativeFacade nativeFacade = BackupService.getInstance()
				.getNativeFacade();

		Set<Entry<String, Disk_Info>> entries = null;
		
		if(StringUtil.isEmptyOrNull(dummySnapshotGuid)){
			entries = nbdDiskMapping.entrySet();
		}else{
			entries = sanDiskMapping.entrySet();
		}
		

		RepJobMonitor jobMonitor = CommonService.getInstance()
				.getRepJobMonitorInternal(jobScript.getAFGuid());

		File sessonFolder = new File(session.getSessionFolder());

		int vmdkDiskType = getVMWareDiskType(session.getSessionCompressType());

		long isComplete = 1;
		int ret = HACommon.REPLICATE_SUCCESS;
		
		List<Long> errorCode = new ArrayList<Long>(1);
		
		for (Disk d : disks) {
			if(isJobCancelled()) {
				return HACommon.REPLICATE_CANCEL;
			}
			
			long blockSize = getDataStoreBlockSize(d);
			
			for (Entry<String, Disk_Info> entry : entries) {
				if (entry.getKey().equalsIgnoreCase(
						HACommon.handleGTPDiskSignature(d))) {

					synchronized (jobMonitor) {
						// reset for each disk replication.
						//jobMonitor.setFirst(true);
						//jobMonitor.setRepTransedSize(0);
						jobMonitor.setRepTotalSize(0);
						jobMonitor.setRepStartTime(System.currentTimeMillis());
						jobMonitor.setRepStartNanoTime(System.nanoTime());
					}

					String d2dFile = getVHDFileName(sessonFolder, d
							.getSignature());
					log.info("D2D File: " + d2dFile);
					log.info(d.getDiskNumber());
					log.info(d.getSignature());
					log.info(d2dFile + "," + vmInfo.moref + ","
							+ HACommon.getHostNameForVddk(vmInfo.hostname,vmInfo.port) + "," + vmInfo.username + ","
							+ entry.getValue().getdiskURL() + "," + vddkPort
							+ "," + vmdkDiskType + "," + dummySnapshotGuid);

					errorCode.clear();
					
					String d2dDiskFile = "disk" + d.getSignature() + ".D2D";
					{
						String msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_CONVERT_DISK_IMAGE_START, d2dDiskFile, entry.getValue().getdiskURL());
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					}
					
					BackupDestinationInfo backupDestinationInfo = null;
					try {
						backupDestinationInfo = BaseReplicationCommand.getBackupDestinationInfo(jobScript, false, jobID);
					} catch (Exception e) {
						log.error("Failed to get backup destination, " + e.getMessage());
					}
					
					isComplete = nativeFacade.ConvertVHD2VMDK(jobScript.getAFGuid(),d2dFile,
							vmInfo.moref, HACommon.getHostNameForVddk(vmInfo.hostname,vmInfo.port), 
							vmInfo.username,vmInfo.password, entry.getValue().getdiskURL(),
							vddkPort,vmInfo.exParams, vmdkDiskType, tryHotAddOnly ? "[hotadd]" + dummySnapshotGuid : dummySnapshotGuid, jobID + "",blockSize, jobScript.getBackupDestType(), 
									backupDestinationInfo.getNetConnUserName(), backupDestinationInfo.getNetConnPwd(), errorCode);

					log.info("conversion module return value: " + isComplete);

					if (isComplete == HACommon.REPLICATE_SUCCESS || isComplete == HACommon.REPLICATE_HOTADD_SUCCESS) {
						String msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_CONVERT_DISK_IMAGE_COMPLETED, d2dDiskFile);
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					} else if (isComplete == HACommon.REPLICATE_CANCEL) {
						// Do nothing
					} else {
						String msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_CONVERT_DISK_IMAGE_FAILED, d2dDiskFile);
						
						long logLevel = Constants.AFRES_AFALOG_WARNING;
						if(StringUtil.isEmptyOrNull(dummySnapshotGuid))
							logLevel = Constants.AFRES_AFALOG_ERROR;
						HACommon.addActivityLogByAFGuid(logLevel, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					}

					if (isComplete == HACommon.REPLICATE_SUCCESS) {
						log.info(d.getDiskNumber() + " replicated.");
						if(sanDisk != null){
							//if conversion is carried out on SAN mode, sanDisk will not be null
							sanDisk.add(d);
						}
						break;

					} else if (isComplete == HACommon.REPLICATE_HOTADD_SUCCESS) {
						
						log.info("REPLICATION_HOTADD_SUCCESS");
						log.info("return code: " + isComplete);
						log.info("d2d file: " + d2dFile);
						log.info("vmdk url: " + entry.getValue().getdiskURL());
						if(sanDisk != null){
							//if conversion is carried out on SAN mode, sanDisk will not be null
							sanDisk.add(d);
						}
						if(hotAddDiskMapping != null){
							//if conversion is carried out on SAN mode, hotAddDiskMapping will not be null
							hotAddDiskMapping.put(d2dFile, entry.getValue()
									.getdiskURL());
						}
						
						
						break;
						
					} else if (isComplete == HACommon.REPLICATE_HOTADD_FAILURE) {
						log.info("REPLICATION_HOTADD_FAILURE");
						log.info("return code: " + isComplete);
						log.info("d2d file: " + d2dFile);
						log.info("vmdk url: " + entry.getValue().getdiskURL());
						if(hotAddDiskMapping != null){
							hotAddDiskMapping.put(d2dFile, entry.getValue()
									.getdiskURL());
						}
						
						isHotAddFail = true;
						ret = HACommon.REPLICATE_HOTADD_FAILURE;
						break;
					} else if (isComplete == HACommon.REPLICATE_CANCEL) {

						return HACommon.REPLICATE_CANCEL;

					} else if (isComplete == HACommon.PASSWORD_FILE_DAMAGE) {
						log.error("conversion failed bacuse password file is damaged.");
						String msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_SESSION_PASSWORD_FILE_DAMAGE);
						
						//SAN mode and NDB mode may meet his same error code.
						//only print the msg in NBD model to avoid duplicated log.
						//hotAddDiskMapping is null in NBC mode, 
						if(hotAddDiskMapping == null)
							HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						else
							log.error("In SAN mode, leave the following log to be printed in NBD mode:" + msg);
						
						return HACommon.REPLICATE_FAILURE;
					}else {
						
						log.info(d.getDiskNumber() + " is not in san mode.");
						
						//if dummySnapshotGuid is null or empty string, conversion is running on NBD mode
						if(StringUtil.isEmptyOrNull(dummySnapshotGuid)){
							log.error("Replication in NBD mode failed.");
							log.error("return code: " + isComplete);
							
							printConversionFailureMessage(errorCode);
							
							return HACommon.REPLICATE_FAILURE;
						}
						
						// SAN mode is not suppoted.
						//if dummySnapshotGuid is not null and is not empty string,
						//conversion is running on SAN mode. 
						break;
					}
				}
			}
		}

		return ret;

	}
	
	private void printConversionFailureMessage(List<Long> errorCodes){
		
		try {
			
			if(errorCodes.isEmpty()){
				log.error("Error code is not set.");
				return ;
			}
			
			Long errorCode = null;
			if(errorCodes.size() > 0) {
				errorCode = errorCodes.get(0);
			}
			log.error("vddkErrorCode = " + errorCode);
			
			Long d2dErrorCode = null;
			if(errorCodes.size() > 1)
				 d2dErrorCode = errorCodes.get(1);
			log.error("d2dErrorCode = " + d2dErrorCode);
			
			printVDDKErrorMessage(errorCode, true);
			
			printVDDKErrorMessage(d2dErrorCode, false);
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
	}

	private void printVDDKErrorMessage(Long errorCode, boolean isVDDKError) {
		if(errorCode != null) {
			String categoryErrorCode = (String)errorMapping.get(errorCode + "");
			if(categoryErrorCode == null)
				return;
			
			errorCode = Long.parseLong(categoryErrorCode.trim());
			
			String msg = "";
			if(isVDDKError) {
				msg = getVDDKErrorMsg(errorCode, vmInfo.hostname, vmInfo.storage, null);
			}
			else {
				if(errorCode == UNABLE_READ_BACKUP) {
					msg = ReplicationMessage.getResource(
							ReplicationMessage.AFRES_VCM_NON_PROXY_PREFIX + errorCode);
				}else {
					msg = ReplicationMessage.getResource(
							ReplicationMessage.AFRES_VCM_NON_PROXY_PREFIX + OTHER_READ_ERRORS);
				}
			}
			
			if(!StringUtil.isEmptyOrNull(msg)){
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
			}
		}
	}

	public static String getVDDKErrorMsg(Long errorCode, String vmHostName, String storage, String afGuid) {
		String msg = "";
		if(afGuid != null && uuidToVMInfo.get(afGuid) != null) {
			vmHostName = uuidToVMInfo.get(afGuid).hostname;
			storage = uuidToVMInfo.get(afGuid).storage;
		}
		
		if(errorCode == NETWORK_ERROR){
			msg = ReplicationMessage.getResource(
					ReplicationMessage.AFRES_VCM_NON_PROXY_PREFIX + errorCode, vmHostName);
		}else if(errorCode == VMDK_ERROR){
			msg = ReplicationMessage.getResource(
					ReplicationMessage.AFRES_VCM_NON_PROXY_PREFIX + errorCode);
		}else if(errorCode == DISK_ERROR){
			msg = ReplicationMessage.getResource(
					ReplicationMessage.AFRES_VCM_NON_PROXY_PREFIX + errorCode);
		}else if(errorCode == CREDENTIAL_ERROR){
			msg = ReplicationMessage.getResource(
					ReplicationMessage.AFRES_VCM_NON_PROXY_PREFIX + errorCode, vmHostName);
		}else if(errorCode == INTERNAL_ERROR){
			msg = ReplicationMessage.getResource(
					ReplicationMessage.AFRES_VCM_NON_PROXY_PREFIX + errorCode);
		}else if(errorCode == SPACE_ERROR){
			msg = ReplicationMessage.getResource(
					ReplicationMessage.AFRES_VCM_NON_PROXY_PREFIX + errorCode, storage,storage);
		}else if(errorCode == VMDK_FILE_ERROR){
			msg = ReplicationMessage.getResource(
					ReplicationMessage.AFRES_VCM_NON_PROXY_PREFIX + errorCode);
		}else if(errorCode == VMDK_LICENSE_ERROR){
			msg = ReplicationMessage.getResource(
					ReplicationMessage.AFRES_VCM_NON_PROXY_PREFIX + errorCode, vmHostName);
		}
		return msg;
	}
	
	private static void genereateErrorCodeMapping(){
		
		try {
			InputStream in  = VMwareNonProxyConversionManager.class.getResourceAsStream("vddkerrorcode.properties");
			errorMapping.load(in);
		} catch (IOException e) {
			log.error("load vddk error code incorrectly." + e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
	}
	
	public static void main(String[] args) {
		
		genereateErrorCodeMapping();
		
	}
	
	

}
