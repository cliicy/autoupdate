package com.ca.arcflash.webservice.replication;

import java.io.File;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.failover.FailoverMessage;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.Disk;
import com.ca.arcflash.ha.model.ReplicaRoot;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.model.manager.VMwareSnapshotModelManager;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.utils.LRUHashMap;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.Disk_Info;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVMDetails;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualDisk;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.webservice.replication.VMWareBaseReplicationCommand.VMWareInfo;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.data.VMwareConnParams;

public abstract class VMwareConversionManager {

	
	public static final String SAN_MODE_NO_SUPPORT = "SAN NOT Supported";
	private static final Logger log = Logger.getLogger(VMwareConversionManager.class);

	public static final LRUHashMap<String, Long> cache = new LRUHashMap<String, Long>(10);
	static final Map<String, VMWareInfo> uuidToVMInfo = new Hashtable<String, VMWareInfo>();
	CAVirtualInfrastructureManager vmwareManager;
	CAVirtualInfrastructureManager createdVMManager;
	
//	private List<CAVirtualInfrastructureManager> oldVMwareOBJ = new ArrayList<CAVirtualInfrastructureManager>();
	CAVMDetails vmDetails;
	VMWareInfo vmInfo;
	ReplicationJobScript jobScript;
	SessionInfo session;
	long jobID;

	ADRConfigure adrConfigure;
	Map<String, Disk_Info> sanDiskMapping = new HashMap<String, Disk_Info>();
	Map<String, Disk_Info> nbdDiskMapping = new HashMap<String, Disk_Info>();
	
	Map<String, Disk_Info> tmpNbdDiskMapping = new HashMap<String, Disk_Info>();
	
	//paramters for smart copy
	boolean isSmartCopy = false;
	SessionInfo beginSession;
	SessionInfo endSession;
	
	VMwareSnapshotModelManager vmwareSnapshot;
	
	
	protected boolean firstConversion = false;
	
	public void setFirstConversion(boolean first) {
		firstConversion = first;
	}

	public VMwareConversionManager(){
	}
	
	public VMwareConversionManager(CAVirtualInfrastructureManager vmwareManager, CAVMDetails vmDetails,
			VMWareInfo vmInfo, ReplicationJobScript jobScript, SessionInfo session, long jobID,
			boolean isSmartCopy,SessionInfo beginSession, SessionInfo endSession,ADRConfigure adrConfigure,
			VMwareSnapshotModelManager vmwareSnapshot) {
		
		this.vmwareManager = vmwareManager;
		this.vmDetails = vmDetails;
		this.vmInfo = vmInfo;
		this.jobScript = jobScript;
		this.session = session;
		this.jobID = jobID;
		this.adrConfigure = adrConfigure;
		//smart copy parameter
		this.isSmartCopy = isSmartCopy;
		this.beginSession = beginSession;
		this.endSession = endSession;
		this.vmwareSnapshot = vmwareSnapshot;
	}
	
	public int doVirtualConversion(SortedSet<Disk> disks, Map<String, Disk_Info> baseDiskMappings) {
		try {
			uuidToVMInfo.put(jobScript.getAFGuid(), vmInfo);
			int result = 0;
			
			try {
				deleteLastRoundDummySnapshot();
			} catch (Exception e1) {
				log.error("Failed to delete dummy snapshot." + e1.getMessage(),e1);
				return HACommon.REPLICATE_FAILURE;
			}
			
			if (baseDiskMappings == null) {
				//child disk conversion
				/*
				 * if it is not a base disk replication, disk count change will
				 * happen. If a new brandly new disk is replicated, a corresponding
				 * virtual disk needs to be created. If an disk is left out in
				 * replication, this disk's vmdk should be detached. If an old disk
				 * is replicated again, then its detached vmdk should be attached
				 * again.
				 */
				try {
					getNBDDiskMappingByDiskSignature(baseDiskMappings);
				} catch (Exception e) {
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					return HACommon.REPLICATE_FAILURE;
				}
				
				List<DiskDestination> diskDestinations = jobScript.getReplicationDestination().get(0).getDiskDestinations();
				try {
					dealWithVolumeChange(vmwareManager, vmDetails, tmpNbdDiskMapping, disks, vmInfo, diskDestinations);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
					return HACommon.REPLICATE_FAILURE;
				}
				
				log.info("Start SAN mode try.");
				
			}
			
			boolean isNBDEnfored = false; 
			try{
				ReplicationDestination dest = jobScript.getReplicationDestination().get(0);
				if (dest.isProxyEnabled()) {
					// call the web service to inject the drivers, because the monitee
					// doesn't install VDDK.
					
					isNBDEnfored = HAService.getInstance().getEnforeVDDKNBDFlagFromMonitor(jobScript.getAFGuid());
					
				} else {
					isNBDEnfored = HAService.getInstance().getEnforeVDDKNBDFlag();
				}
				
				if(isNBDEnfored){

					String msg = ReplicationMessage.getResource(ReplicationMessage.VDDK_ENFORCED_NBD);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				}
				
			}catch(Exception e){
				log.warn(e.getMessage() + "Failed to read registry key VDDKEnforceNBD");
				
			}			
			
			if (!isNBDEnfored) {
				String dummySnapshotGuid = null;
				try {
					dummySnapshotGuid = generateSANDiskMapping(baseDiskMappings);
				} catch (Exception e) {
					log.error(e);
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					return HACommon.REPLICATE_FAILURE;
				}
				if (StringUtil.isEmptyOrNull(dummySnapshotGuid)){
					log.error("Failed to get dummy snapshot GUID.");
					return HACommon.REPLICATE_FAILURE;
				}
				
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_TRY_ADVANCED_MODE);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				result = doSANModeVirtualConversion(disks, baseDiskMappings, dummySnapshotGuid, !firstConversion);
				
				if(result == HACommon.REPLICATE_CANCEL 
						|| result == HACommon.REPLICATE_FAILURE){
					return result;
				}
			}
	
			log.info("Start NBD mode try");
			result = doNBDModeVirtualConversion(disks, baseDiskMappings);
	
			return result;
		}
		finally {
			uuidToVMInfo.remove(jobScript.getAFGuid());
		}

	}
	
	
	protected abstract int doSANModeVirtualConversion(SortedSet<Disk> disks,Map<String, Disk_Info> baseDiskMapping, String dummySnapshotGuid, boolean tryHotAddOnly);
	
	protected abstract int doNBDModeVirtualConversion(SortedSet<Disk> disks,Map<String, Disk_Info> baseDiskMapping);
	
	String getVHDFileName(File session, String signature) {

		return session.getAbsolutePath() + "\\disk" + signature + ".D2D";
	}

	String getVHDFileName(SessionInfo session, String signature) {

		File sessonFolder = new File(session.getSessionFolder());
		
		return sessonFolder.getAbsolutePath() + "\\disk" + signature + ".D2D";
		
	}
	
	int getVMWareDiskType(int compressType) {
		if (compressType == 0) {
			// no d2d compress
			return 1;
		} else {
			// d2d compress
			return 0;
		}
	}

	private void dealWithVolumeChange(CAVirtualInfrastructureManager vmwareManager, CAVMDetails vmDetails,
			Map<String, Disk_Info> diskMappings, SortedSet<Disk> disks, VMWareInfo vmInfo,
			List<DiskDestination> diskDestinations) throws Exception {
		// find out overlapped disk of last replication and current replication
		List<String> overlappedDisk = new ArrayList<String>();
		for (Entry<String, Disk_Info> entry : diskMappings.entrySet()) {
			for (Disk disk : disks) {
				if (HACommon.handleGTPDiskSignature(disk).equalsIgnoreCase(entry.getKey())) {
					overlappedDisk.add(HACommon.handleGTPDiskSignature(disk));
				}
			}
		}

		if ((overlappedDisk.size() > 0 && overlappedDisk.size() < disks.size()) || diskMappings.size() != disks.size()) {

			log.info("Begin to handle disks change.");
			
			log.info("The disks of current conversion job are: ");
			for (Disk disk : disks) {
				log.info(HACommon.handleGTPDiskSignature(disk));
			}
			
			log.info("The disks of current virtual standby VM are: ");
			for (Entry<String, Disk_Info> entry : diskMappings.entrySet()) {
				log.info(entry.getKey());
			}

			// save disk mapping into a copy
			Map<String, Disk_Info> copyVmdkUrls = new HashMap<String, Disk_Info>(diskMappings);
			// remove overlapped disks from last replication disks
			for (String signature : overlappedDisk) {
				if(copyVmdkUrls.containsKey(signature.toLowerCase())){
					copyVmdkUrls.remove(signature.toLowerCase());
				}else if (copyVmdkUrls.containsKey(signature.toUpperCase())) {
					copyVmdkUrls.remove(signature.toUpperCase());
				}	
			}

			// Remove overlapped disks from current d2d disks
			List<Disk> copyDisks = new ArrayList<Disk>(disks);
			List<Disk> tmp = new ArrayList<Disk>();
			for (Disk disk : copyDisks) {
				for (String signature : overlappedDisk) {
					if (HACommon.handleGTPDiskSignature(disk).equalsIgnoreCase(signature)) {
						tmp.add(disk);
						break;
					}
				}
			}
			if (tmp.size() > 0) {
				copyDisks.removeAll(tmp);
			}

			// Add new virtual disks
			for (Disk disk : copyDisks) {
				log.info("Add new disk. Signature: " + disk.getSignature());
				CAVirtualDisk caVirtualDisk = new CAVirtualDisk();
				caVirtualDisk.setThinProvisioning(true);
				caVirtualDisk.setCapacityinKB(disk.getSize() / 1024);
				caVirtualDisk.setDiskController(disk.getControllerType());
				String storageName = HACommon.getDiskStorage(diskDestinations, disk);
				caVirtualDisk.setDiskDataStore(storageName);
				try {
					SortedSet<Disk> newDisks = new TreeSet<Disk>();
					newDisks.add(disk);
					checkStorageBlockSize(jobScript, vmInfo, newDisks, jobID);
					Disk_Info diskInfo = vmwareManager.addVirtualDisk(
							vmDetails.getVmName(), vmDetails.getUuid(),
							vmInfo.esxHost, caVirtualDisk);
					diskMappings.put(HACommon.handleGTPDiskSignature(disk),
							diskInfo);
				} catch (Exception e) {
					log.error("Failed to add virtual disk.", e);
					throw new Exception(e.getMessage());
				}

			}

			// Detach\remove obsolete virtual disks
			for (Entry<String, Disk_Info> entry : copyVmdkUrls.entrySet()) {
				try {
					log.info("DetachVirtualDisk.");
					log.info("vmname: " + vmDetails.getVmName());
					log.info("vmuuid: " + vmDetails.getUuid());
					log.info("vmdk url: " + entry.getValue());
					vmwareManager.detachVirtualDisk(vmDetails.getVmName(), vmDetails.getUuid(), entry.getValue().getdiskURL());
					diskMappings.remove(entry.getKey());
				} catch (Exception e) {
					log.error("Failed to dettach virtual disk.", e);
					throw new Exception(e.getMessage());
				}

			}


		}
	}
	
	String generateSANDiskMapping(Map<String, Disk_Info> baseDiskMapping) throws Exception{
		
		sanDiskMapping.clear();

		String dummySnapshotGuid = createDummySnapshot();
		
		ArrayList<Disk_Info> dummyDiskInfos = vmwareManager.getsnapDiskInfo(vmDetails.getVmName(), vmDetails
					.getUuid(), dummySnapshotGuid);
		
		if (baseDiskMapping != null && !baseDiskMapping.isEmpty()) {

			try {

				if (dummyDiskInfos != null) {
					for (String tmpSigKey : baseDiskMapping.keySet()) {
						int index = dummyDiskInfos.indexOf(baseDiskMapping.get(tmpSigKey));
						if (index != -1) {
							sanDiskMapping.put(tmpSigKey, dummyDiskInfos.get(index));
						}
					}
				}
			} catch (Exception e) {
				log.error("Failed to get dummy snapshot disk mapping." + e.getMessage(),e);
				
				try {
					deleteDummySnapshot(dummySnapshotGuid);
				} catch (Exception e1) {
					log.error(e1.getMessage(),e1);
				}
				
				throw new Exception(e.getMessage());
				
			}

		} else {

			try {

				if (dummyDiskInfos != null) {
					for (String tmpSigKey : tmpNbdDiskMapping.keySet()) {
						int index = dummyDiskInfos.indexOf(tmpNbdDiskMapping.get(tmpSigKey));
						if (index != -1) {
							sanDiskMapping.put(tmpSigKey, dummyDiskInfos.get(index));
						}
					}
				}
				
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				
				try {
					deleteDummySnapshot(dummySnapshotGuid);
				} catch (Exception e1) {
					log.error(e1.getMessage(),e1);
				}
				
				throw new Exception(e.getMessage());
			}
			
		}
		
		return dummySnapshotGuid;
	}
	
	void generateNBDDiskMapping(Map<String, Disk_Info> baseDiskMapping, boolean isNBDRep) throws Exception{
		
		nbdDiskMapping.clear();
		
		if (baseDiskMapping != null && !baseDiskMapping.isEmpty()) {
			nbdDiskMapping = baseDiskMapping;
		} else {

			try {

				ArrayList<Disk_Info> diskInfos = vmwareManager.getCurrentDiskInfo(vmDetails.getVmName(), vmDetails
						.getUuid());
				
				if(diskInfos.size() != tmpNbdDiskMapping.size()){
					log.info("vmdk count is unequal.");
					log.info("vmname=" + vmDetails.getVmName());
					log.info("vmuuid=" + vmDetails.getUuid());
					for (Disk_Info disk_Info : diskInfos) {
						log.info("vmdk url = " + disk_Info.getdiskURL());
					}
					for (Entry<String, Disk_Info> entry : tmpNbdDiskMapping.entrySet()) {
						log.info("key=" + entry.getKey() + ";value=" + entry.getValue().getdiskURL());
					}
				}
				
				for (String tmpSigKey : tmpNbdDiskMapping.keySet()) {
					int index = diskInfos.indexOf(tmpNbdDiskMapping.get(tmpSigKey));
					if (index != -1) {
						nbdDiskMapping.put(tmpSigKey, diskInfos.get(index));
					}
				}
				
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				throw new Exception();
			}
		}
	}
	
	private void getNBDDiskMappingByDiskSignature(Map<String, Disk_Info> baseDiskMapping) throws Exception{
		
		tmpNbdDiskMapping.clear();
		
		int vddkPort = CommonUtil.getCustomizedVDDKPort();
		
		if (baseDiskMapping != null && !baseDiskMapping.isEmpty()) {
			tmpNbdDiskMapping = baseDiskMapping;
		} else {

			try {

			
				//if it is for generating diskmapping before replication begin
				//revert to current snapshot is madatory.
				String afguid = jobScript.getAFGuid();

				SortedSet<VMSnapshotsInfo> snapshots = vmwareSnapshot.getSnapshots(afguid);

				SortedSet<VMSnapshotsInfo> orderedSnapshots = new TreeSet<VMSnapshotsInfo>(
						new Comparator<VMSnapshotsInfo>() {

							@Override
							public int compare(VMSnapshotsInfo v1, VMSnapshotsInfo v2) {
									return (int) (v1.getSnapNo() - v2.getSnapNo());
							}
						});
				orderedSnapshots.addAll(snapshots);
				
				VMSnapshotsInfo[] snapshotsArray = orderedSnapshots.toArray(new VMSnapshotsInfo[0]);
				VMSnapshotsInfo latestSnapshot = null;
				for(int i = snapshotsArray.length-1; i>=0; i--){
					latestSnapshot = snapshotsArray[i];
					if(!latestSnapshot.isDRSnapshot()){
						break;
					}
				}

				String currSnapshot = vmwareManager.getCurrentSnapshotforVCM(vmDetails.getVmName(), vmDetails.getUuid());
				
				log.info("current snapshot: " + currSnapshot);
				log.info("last snapshot in vmsnapshotmodel.xml: " + latestSnapshot.getSnapGuid());
				log.info("last bootable snapshot in vmsnapshotmodel.xml: " + latestSnapshot.getBootableSnapGuid());
				
				boolean hartFileExist = HACommon.checkHartFileExistence(jobScript,session);
				
				if(!hartFileExist){
					log.info("No hart files.");
				}
				
				if(!currSnapshot.equals(latestSnapshot.getSnapGuid())
						|| !hartFileExist){
					//if current snapshot is not in the latest snapshot,we will revert snapshot
					//otherwise we will not revert snapshot
					log.info("revert snapshot begin.snapshot url: " + latestSnapshot.getSnapGuid());
					vmwareManager.revertToSnapshot(vmDetails.getVmName(), vmDetails.getUuid(), latestSnapshot.getSnapGuid());
					log.info("revert snapshot end.");
					
					currSnapshot = vmwareManager.getCurrentSnapshotforVCM(vmDetails.getVmName(), vmDetails.getUuid());
					log.info("After revert, the current snapshot is:" + currSnapshot);
					if(currSnapshot == null || !currSnapshot.equalsIgnoreCase(latestSnapshot.getSnapGuid())) {
						String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VMWARE_REVERT_SNAPSHOT_FAIL, latestSnapshot.getSnapGuid(), vmDetails.getVmName());
						throw new Exception(msg);
					}
					
					HACommon.deleteHartFiles(jobScript,session);

				}else{
					//did not revert snapshot 
					log.info(hartFileExist==true?"hart file exist.":"hart file does not exits");
					log.info("current snapshot url: " + currSnapshot);
					log.info("last snapshot url: " + latestSnapshot.getSnapGuid());
				}
				
				ArrayList<Disk_Info> diskInfos = vmwareManager.getCurrentDiskInfo(vmDetails.getVmName(), vmDetails
						.getUuid());
				
				for (Disk_Info diskInfo : diskInfos) {
					
					String childDiskSig = ReplicationProxy.getInstance().getVMDiskSignature(jobScript,
							HACommon.getHostNameForVddk(vmInfo.hostname,vmInfo.port),
							vmInfo.username, vmInfo.password, vmInfo.moref, vddkPort,vmInfo.exParams, "", diskInfo.getdiskURL(),
							jobID + "");

					log.info("childDiskSig: " + childDiskSig);
					if (StringUtil.isEmptyOrNull(childDiskSig)) {
						log.error("Invalid disk signature.Disk signature is empty.");
						log.error("vmdkUrl: " + diskInfo.getdiskURL());
						String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_GET_DISK_SIG, diskInfo.getdiskURL());
						throw new Exception(msg);
					}

					tmpNbdDiskMapping.put(HACommon.fillSignature(childDiskSig), diskInfo);
				}
				
			}
			catch (Exception e) {
				log.error(e.getMessage(),e);
				throw e;
			}
			
		}
	}
	
	void deleteLastRoundDummySnapshot() throws Exception{
		
		RepositoryUtil repository = HACommon.getRepositoryManager();
		ReplicaRoot repRoot = repository.getProductionServerRoot(jobScript.getAFGuid()).getReplicaRoot();
		
		Map<String, String> vmSnapshots = vmwareManager.listVMSnapShots(
								vmDetails.getVmName(), vmDetails.getUuid());
			
		if(vmSnapshots != null && repRoot.getDymmySnapshotUrl() != null){
			Set<String> keys = vmSnapshots.keySet();
			if (keys != null && keys.contains(repRoot.getDymmySnapshotUrl())) {
				vmwareManager.removeSnapshot(vmDetails.getVmName(), vmDetails.getUuid(), repRoot.getDymmySnapshotUrl());
			}
		}
		
	}
	
	String createDummySnapshot() throws Exception{
		
		String dummySnapshotGuid = vmwareManager.createSnapshot(vmDetails.getVmName(), vmDetails.getUuid(),
				VMwareSnapshotModelManager.DUMMY_SNAPSHOT_NAME, "snapshot guid");
		
		RepositoryUtil repository = HACommon.getRepositoryManager();
		ReplicaRoot repRoot = repository.getProductionServerRoot(jobScript.getAFGuid()).getReplicaRoot();
		
		repRoot.setDymmySnapshotUrl(dummySnapshotGuid);
		
		repository.saveProductionServerRoot();
		
		
		return dummySnapshotGuid;
		
	}
	
	void deleteDummySnapshot(String dummySnapshotGuid) throws Exception{
		
		if(dummySnapshotGuid == null)
			return;
		
		createNewConnection();
		
		log.info("Revert to dummy snapshot." + dummySnapshotGuid);
		vmwareManager.revertToSnapshot(vmDetails.getVmName(), vmDetails.getUuid(), dummySnapshotGuid);

		log.info("Delete dummy snapshot begin." + dummySnapshotGuid);
		vmwareManager.removeSnapshot(vmDetails.getVmName(), vmDetails.getUuid(), dummySnapshotGuid);
		
		
		RepositoryUtil repository = HACommon.getRepositoryManager();
		ReplicaRoot repRoot = repository.getProductionServerRoot(jobScript.getAFGuid()).getReplicaRoot();
		repRoot.setDymmySnapshotUrl("");
		repository.saveProductionServerRoot();
		
		log.info("Delete dummy snapshot end." + dummySnapshotGuid);
		
	}

	private void createNewConnection() throws Exception {
//		oldVMwareOBJ.add(vmwareManager);
		try {
			vmwareManager.close();
		} catch (Exception e) {
			
		}
		vmwareManager = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
				vmInfo.hostname, vmInfo.username,vmInfo.password, vmInfo.protocol, 
				true,vmInfo.port);
		
		createdVMManager = vmwareManager;
		vmInfo.setExParams(vmwareManager);
	}
	
//	public List<CAVirtualInfrastructureManager> getOldVMManager(){
//		return oldVMwareOBJ;
//	}
	
	public CAVirtualInfrastructureManager getRecreatedVMManager() {
		return createdVMManager;
	}
	
	boolean isJobCancelled(){
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(jobScript.getAFGuid()); 
		synchronized(jobMonitor) {
			if(jobMonitor.isCurrentJobCancelled()) {
				log.info("Replication job is cancelled. uuid: " + jobScript.getAFGuid());
				return true;
			}
		}
		
		return false;
	}

//	private void checkStorageBlockSize(String storageName,VMWareInfo vmInfo, Disk disk)throws Exception{
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
//		int blockSize = vmwareManager.getDataStoreBlockSize(node, storageName); //block size in MB
//		
//		if(blockSize==0){
//			log.info("return block size is zero and datastore is not VMFS. So no limitation on disk size.sroragename=" + storageName);
//			return;
//		}
//		
//		if(mapping.get(blockSize) < disk.getSize()){
//				
//			long sizeInGB = mapping.get(blockSize)/1024/1024/1024; //GB
//				
//			int reasonableBlockSize = blockSize;
//			while (true) {
//				reasonableBlockSize = reasonableBlockSize * 2;
//				if(mapping.get(reasonableBlockSize) > disk.getSize()){
//					break;
//				}
//			}
//				
//			String msg = ReplicationMessage.getResource(
//						ReplicationMessage.REPLICATION_DEST_BLOCK_SIZE,
//						storageName,node.getEsxName(),sizeInGB+"GB",reasonableBlockSize+"MB");
//				
//			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
//												new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
//			
//			throw new Exception();
//				
//		}
//		
//	}
	
	/*
	 * Check block size on storage,different block size support
	 * different virtual disk size
	 *  Block Size   Largest virtual disk on VMFS-3 
	 *	1MB          256GB 
	 *	2MB          512GB 
	 *	4MB 		 1TB 
	 *	8MB 		 2TB 
	*/ 
	public void checkStorageBlockSize(ReplicationJobScript jobScript, VMWareInfo vmInfo, SortedSet<Disk> disks,long jobID)throws Exception{
		
		List<DiskDestination> diskDests = jobScript.getReplicationDestination().get(0).getDiskDestinations();
		
		ESXNode node = new ESXNode();
		node.setEsxName(vmInfo.esxHost);
		node.setDataCenter(vmInfo.dcName);
		
		Map<Integer, Long> mapping = new HashMap<Integer, Long>();
		mapping.put(HACommon.STORAGE_BLOCK_SIZE_1_MB, HACommon.VIRTUAL_DISK_MAX_SIZE_256_GB);    //256GB
		mapping.put(HACommon.STORAGE_BLOCK_SIZE_2_MB, HACommon.VIRTUAL_DISK_MAX_SIZE_512_GB);    //512GB
		mapping.put(HACommon.STORAGE_BLOCK_SIZE_4_MB, HACommon.VIRTUAL_DISK_MAX_SIZE_1024_GB);   //1TB
		mapping.put(HACommon.STORAGE_BLOCK_SIZE_8_MB, HACommon.VIRTUAL_DISK_MAX_SIZE_2024_GB);   //2TB
		
		for (Disk d : disks) {
			String storageName = HACommon.getDiskStorage(diskDests, d);
			
			int VMFSVersion = vmwareManager.getDataStoreVMFSVersion(node, storageName);
			String infoMsg = String.format("The esx[%s, %s] storageName[%s] VMFSVersion is [%d]", node.getEsxName(), node.getDataCenter(),storageName, VMFSVersion);
			log.info(infoMsg);
			//If storage format is VMFS-5, don't check the block size support.
			if(VMFSVersion != 3){
				continue;
			}
			
			//If storage format is VMFS-3, check the block size. 
			int blockSize = vmwareManager.getDataStoreBlockSize(node, storageName); //block size in MB
			
			if(blockSize==0){
				log.info("return block size is zero and datastore is not VMFS. So no limitation on disk size.sroragename=" + storageName);
				continue;
			}
			
			if(mapping.get(blockSize) < d.getSize()){
				
				String msg = "";
				long sizeInGB = mapping.get(blockSize)/1024/1024/1024; //GB
				
				int reasonableBlockSize = blockSize;
				while (true) {
					reasonableBlockSize = reasonableBlockSize * 2;
					if(reasonableBlockSize > HACommon.STORAGE_BLOCK_SIZE_8_MB){
						log.error("source disk is bigger than 2TB.No proper block size.");
						msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_DEST_EXCEED_LIMIT);
						break;
					}
					if(mapping.get(reasonableBlockSize) >= d.getSize()){
						msg = ReplicationMessage.getResource(
								ReplicationMessage.REPLICATION_DEST_BLOCK_SIZE,
								storageName,node.getEsxName(),sizeInGB+"GB",reasonableBlockSize+"MB");
						break;
					}
				}
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
												new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
				throw new Exception();
				
			}
		}
	}

	long getDataStoreBlockSize(Disk disk) throws Exception{
		
		try {
			log.info("getDataStoreBlockSize begin...");
			List<DiskDestination> diskDests = jobScript.getReplicationDestination().get(0).getDiskDestinations();
			String storageName = HACommon.getDiskStorage(diskDests, disk);
			String key = vmInfo.esxHost + "-" + storageName;
			log.info("storeage name: " + key);
			if(cache.get(key) == null){
				ESXNode node = new ESXNode();
				node.setDataCenter(vmInfo.dcName);
				node.setEsxName(vmInfo.esxHost);
				log.info("dcname:" + vmInfo.dcName);
				log.info("esxHost:" + vmInfo.esxHost);
				int blockSize = vmwareManager.getDataStoreBlockSize(node, vmInfo.storage);
				log.info("block size from mob: " + blockSize);
				long blockSizeLong = blockSize * 1024 * 1024; // convert MB to byte
				log.info("block size into byte: " + blockSizeLong);
				cache.put(key, blockSizeLong);
				
				log.info("getDataStoreBlockSize end...");
				
				return blockSizeLong;
			}else{
				
				log.info("block size in cache: " + cache.get(key));
				log.info("getDataStoreBlockSize end...");	
				return cache.get(key);
			}
			
		} catch (Exception e) {
			log.error("Failed to get datastore block size. " + e.getMessage(),e);
			throw new Exception(e);
		}
		
	}
	
	String getHostName(ReplicationJobScript jobScript){

		ReplicationDestination dest = jobScript.getReplicationDestination().get(0);
		String hostname;
		if(dest.isProxyEnabled()){
			HeartBeatJobScript heartBeatJobScript = HAService.getInstance().getHeartBeatJobScript(jobScript.getAFGuid());
			hostname = heartBeatJobScript.getHeartBeatMonitorHostName();
		}else{

			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				FailoverJobScript failoverJobScript = HAService.getInstance().getFailoverJobScript(jobScript.getAFGuid());
				hostname = failoverJobScript.getProductionServerName();
			}
		}
		
		return hostname;
		
	}
	
	
	public static void main(String[] args) {
				
	}
	

}
