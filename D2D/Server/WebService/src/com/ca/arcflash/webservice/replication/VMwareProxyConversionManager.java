package com.ca.arcflash.webservice.replication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

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
import com.ca.arcflash.webservice.jni.FileItemModel;
import com.ca.arcflash.webservice.jni.VMwareRepParameterModel;
import com.ca.arcflash.webservice.replication.VMWareBaseReplicationCommand.VMWareInfo;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.CommonService;
	
public class VMwareProxyConversionManager extends VMwareConversionManager{
	
	public final static int SAN_MODE = 1;
	public final static int NBD_MODE = 0;
	
	public static final Map<String,Map<Long, Integer>> FailRepDisks = new HashMap<String,Map<Long, Integer>>();
	
		
	private static final Logger log = Logger.getLogger(VMwareConversionManager.class);
	
	private boolean isHotAddFail = false;
	
	public VMwareProxyConversionManager() {
	}
	
	public VMwareProxyConversionManager(CAVirtualInfrastructureManager vmwareManager, CAVMDetails vmDetails,
			VMWareInfo vmInfo, ReplicationJobScript jobScript, SessionInfo session, long jobID,
			boolean isSmartCopy,SessionInfo beginD2DFile, SessionInfo endD2DFile,ADRConfigure adrConfigure,
			VMwareSnapshotModelManager vmwareSnapshot) {
		
		super(vmwareManager, vmDetails, vmInfo,  jobScript,  session,  jobID, 
				isSmartCopy, beginD2DFile, endD2DFile,adrConfigure,vmwareSnapshot);
		
	}
	
	public static VMwareConversionManager getInstance(CAVirtualInfrastructureManager vmwareManager, CAVMDetails vmDetails,
			VMWareInfo vmInfo, ReplicationJobScript jobScript, SessionInfo session, long jobID,
			boolean isSmartCopy,SessionInfo beginD2DFile, SessionInfo endD2DFile,ADRConfigure adrConfigure,
			VMwareSnapshotModelManager vmwareSnapshot){
		
		return new VMwareProxyConversionManager( vmwareManager, vmDetails, vmInfo,  jobScript,  session,  
												jobID, isSmartCopy,beginD2DFile, endD2DFile,adrConfigure,
												vmwareSnapshot);
		
	}
	
	@Override
	public int doSANModeVirtualConversion(SortedSet<Disk> disks, 
			Map<String, Disk_Info> baseDiskMapping, String dummySnapshotUrl, boolean tryHotAddOnly){
		
		log.info("SAN mode begins.");
		
		if(isJobCancelled()) {
			return HACommon.REPLICATE_CANCEL;
		}
		
		try {
			
			synchronized (FailRepDisks) {
				if(FailRepDisks.containsKey(jobScript.getAFGuid())){
					FailRepDisks.get(jobScript.getAFGuid()).clear();
				}else {
					FailRepDisks.put(jobScript.getAFGuid(), new HashMap<Long, Integer>());
				}
			}
			
			log.info("Failed disk count before replication: " + FailRepDisks.size());
			
			int result = convert(disks,sanDiskMapping,SAN_MODE, tryHotAddOnly? "[hotadd]" + dummySnapshotUrl : dummySnapshotUrl);
			
			if(result == HACommon.REPLICATE_CANCEL){
				log.error("Replication canceled.");
				log.error("return code: " + result);
				return HACommon.REPLICATE_CANCEL;
			}
			
			log.info("Failed disks count in san mode: " + FailRepDisks.size());
			Map<Long, Integer> failedDisks = FailRepDisks.get(jobScript.getAFGuid());
			if(!failedDisks.isEmpty()){
				log.info("Failed disk is not empty.");
				List<Disk> tmp = new ArrayList<Disk>();
				for(Entry<Long, Integer> diskEntry : failedDisks.entrySet()){
					for (Disk disk : disks) {
						String sigStr = HACommon.fillSignature(String.valueOf(diskEntry.getKey()));
						if(sigStr.equalsIgnoreCase(disk.getSignature())){
							tmp.add(disk);
							if(diskEntry.getValue() == HACommon.REPLICATE_HOTADD_FAILURE){
								isHotAddFail = true;
							}
							break;
						}
					}
				}
				if(tmp.size() > 0){
					disks.clear();
					disks.addAll(tmp);
				}
			}else{
				log.info("All disks are replicated in SAN mode.");
				disks.clear();
			}
			
			return HACommon.REPLICATE_SUCCESS;
			
		} catch (Exception e) {
			log.error("Exception in proxy san." + e.getMessage(),e);
			return HACommon.REPLICATE_FAILURE;
		}finally{
			
			try {
				
				deleteDummySnapshot(dummySnapshotUrl);
				
			} catch (Exception e) {
				log.error("Failed to revert or delete dummy snapshot.Conversion exits.");
				log.error(e.getMessage(),e);
				return HACommon.REPLICATE_FAILURE;
			}
		}
	}

	@Override
	public int doNBDModeVirtualConversion(SortedSet<Disk> disks, Map<String, Disk_Info> baseDiskMapping){
		
		log.info(disks.size() + " disks to be replicated.");
		
		if(isJobCancelled()) {
			return HACommon.REPLICATE_CANCEL;
		}
		
		if(disks.isEmpty()){
			log.info("No NBD disks to be replicated As all disks are successfully replicated in SAN.");
			return HACommon.REPLICATE_SUCCESS;
		}
		
		try {
			generateNBDDiskMapping(baseDiskMapping,true);
		} catch (Exception e) {
			log.error("Failed in genereating nbd diskmapping." +e.getMessage(), e);
			return HACommon.REPLICATE_FAILURE;
		}

		try {
			int result = convert(disks,nbdDiskMapping,NBD_MODE,"");
			
			if(result == HACommon.REPLICATE_CANCEL){
				
				log.error("NBD mode try canceled.");
				return HACommon.REPLICATE_CANCEL;
				
			}else if(result == HACommon.REPLICATE_FAILURE){
				
				log.error("NBD mode try failed.");
				
				if(isHotAddFail){
					//For fixing issue 19986387
					String hostName = getHostName(jobScript);
					String msg = ReplicationMessage.getResource(
							ReplicationMessage.REPLICATION_HOT_ADD_MODE_FAIL, hostName);
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					
				} else {
					String msg = ReplicationMessage.getResource(
							ReplicationMessage.REPLICATION_FAIL_COMMON_FAILURE, vmInfo.vmName);
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
				
				return HACommon.REPLICATE_FAILURE;
			}else if (result == HACommon.PASSWORD_FILE_DAMAGE) {

				log.error("conversion failed bacuse password file is damaged.");
				String msg = ReplicationMessage.getResource(
						ReplicationMessage.REPLICATION_SESSION_PASSWORD_FILE_DAMAGE);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				return HACommon.REPLICATE_FAILURE;
				
			}
			
			return HACommon.REPLICATE_SUCCESS;
			
		} catch (Exception e) {
			log.error("Conversion job failed, " + e.getMessage(), e);
			return HACommon.REPLICATE_FAILURE;
		}
	}
	
	private int convert(SortedSet<Disk> disks, Map<String, Disk_Info> diskMapping,int mode,String snapshotUrl){
		
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(jobScript.getAFGuid());
		List<FileItemModel> fileItems = new ArrayList<FileItemModel>();

		int isComplete = 1;		
		
		try {
			if(disks.size() != diskMapping.size()){
				log.info("disks count is unequals to vmdk count.");
				for (Disk d : disks) {
					log.info("signature=" + HACommon.handleGTPDiskSignature(d));
				}
				for (Entry<String, Disk_Info> entry : diskMapping.entrySet()){
					log.info("key="+entry.getKey() + ";value=" + entry.getValue().getdiskURL());
				}
			}
			
			for (Disk d : disks) {
				long blockSize = getDataStoreBlockSize(d);
				for (Entry<String, Disk_Info> entry : diskMapping.entrySet()) {
					if (entry.getKey().equalsIgnoreCase(HACommon.handleGTPDiskSignature(d))) {
						FileItemModel item = new FileItemModel();
						item.setFilePath("disk" + d.getSignature() + ".D2D");
						item.setFileVMDKUrl(entry.getValue().getdiskURL());
						item.setBlockSize(blockSize);
						fileItems.add(item);
						break;
					}
				}
			}
			
			if(fileItems.isEmpty()){
				log.error("No d2d file and vmdk mapping found.");
				for (Disk d : disks) {
					log.info("signature=" + HACommon.handleGTPDiskSignature(d));
				}
				for (Entry<String, Disk_Info> entry : diskMapping.entrySet()){
					log.info("key="+entry.getKey() + ";value=" + entry.getValue().getdiskURL());
				}
				return HACommon.REPLICATE_FAILURE;
			}

			synchronized (jobMonitor) {
				// reset for each disk replication.
				//jobMonitor.setRepTransedSize(0);
				jobMonitor.setRepTotalSize(0);
				jobMonitor.setRepStartTime(System.currentTimeMillis());
				jobMonitor.setRepStartNanoTime(System.nanoTime());
			}

			int vddkPort = CommonUtil.getCustomizedVDDKPort();
			
			VMwareRepParameterModel repModel = new VMwareRepParameterModel(HACommon.getHostNameForVddk(vmInfo.hostname,vmInfo.port), 
																			vmInfo.username,vmInfo.password, vmInfo.moref, vddkPort,vmInfo.exParams);
			repModel.setIsSAN(mode);
			repModel.setSnapshotUrl(snapshotUrl);
			repModel.setFiles(fileItems);
			
			
			isComplete = ReplicationProxy.getInstance().replicate(jobScript, session, jobID, isSmartCopy,
									beginSession.getSessionName(),endSession.getSessionName(), repModel);

			return isComplete;

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return HACommon.REPLICATE_FAILURE;
		}
		
	}
	
}
