package com.ca.arcflash.repository;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.failover.FailoverMessage;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.BackupInfo;
import com.ca.arcflash.failover.model.BackupInfoFactory;
import com.ca.arcflash.failover.model.Disk;
import com.ca.arcflash.failover.model.DiskExtent;
import com.ca.arcflash.failover.model.Volume;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.ReplicaRoot;
import com.ca.arcflash.ha.model.TransServerReplicaRoot;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.model.VirtualMachineInfo;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.model.manager.VMInfomationModelManager;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.utils.SessionInfoForSnapshot;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.Protocol;
import com.ca.arcflash.webservice.data.SourceNodeSysInfo;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.replication.ReplicationMessage;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;

/**
 * this is used for hyperV replication report and repository access
 * 
 * @author gonro07
 * 
 */

public class RepositoryManager {
	private static final Logger log = Logger.getLogger(RepositoryManager.class);
	private static Map<String, SnapshotFutureTask> taskContainer = new ConcurrentHashMap<String, SnapshotFutureTask>();
	
	static long MAX_TIMEOUT_VALUE = 3 * 60 * 60 * 1000;
	static class SnapshotTaskResult{}
	static class SnapshotCallable implements Callable<SnapshotTaskResult>
	{
		ProductionServerRoot _psr;
		public SnapshotCallable(ProductionServerRoot psr) {
			_psr = psr;
		}
		
		@Override
		public SnapshotTaskResult call() throws Exception {
			RepositoryManager.SnapeShotVM(_psr);
			return new SnapshotTaskResult();
		}	
	}
	
	static class SnapshotFutureTask extends FutureTask<SnapshotTaskResult>
	{		
		private long startTick = System.currentTimeMillis();
		public SnapshotFutureTask(Callable<SnapshotTaskResult> callable) {
			super(callable);
		}
		public long getStartTick() {
			return startTick;
		}
	}
	
	static void purgeInvalidTask()
	{
		log.info("purge the invalid task id");
		List<String> removeList = new ArrayList<String>();
		for (Entry<String, SnapshotFutureTask> entry :taskContainer.entrySet()) {
			if (!entry.getValue().isDone()) {
				continue;
			}
			if (System.currentTimeMillis() > entry.getValue().getStartTick() + MAX_TIMEOUT_VALUE){
				removeList.add(entry.getKey());
			}
		}
		
		for (String taskId : removeList) {
			taskContainer.remove(taskId);
			log.warn("remove invalid vm snapshot task " + taskId + " .");
		}
	}	
	
	// private static final String BASESNAPSHOT = "BaseSnapshot";
	/**
	 * get the names for sessions under rootPath
	 * 
	 * @param rootPath
	 * @return
	 */
	
	private static String xml = CommonUtil.D2DHAInstallPath
			+ "Configuration\\repository.xml";
	private static int RETRY_INTERVAL = 4000;;
	
	private static String CA_AVHD_GUID  = "_00000000-0000-0000-0000-000000000000";

	private static boolean useVHDXformat = true;
	// public static String[] getSessions(String rootPath){
	// File vstore = new File(rootPath+"VStore\\");
	// String [] result = vstore.list(new FilenameFilter(){
	//
	// @Override
	// public boolean accept(File dir, String name) {
	// if(name.startsWith("S")) return true;
	// return false;
	// }});
	// return result;
	// }
	public static ADRConfigure getADRConfigure(String rootPath,
			String sessionName) throws Exception {
		
		String sessionPath = getSessionPath(rootPath, sessionName);
		ADRConfigure adrConfigure;
		try {
			adrConfigure = HACommon.getAdrConfigure(sessionPath);
		} catch (FileNotFoundException e) {
			adrConfigure = null;
		}

		if (adrConfigure == null){
			String msg = ReplicationMessage.getMonitorServiceErrorString(MonitorWebServiceErrorCode.ADRConfigure_Parser);
			throw new Exception(msg);
		}
		
		return adrConfigure;
	}

	public static ProductionServerRoot getProductionServerRoot(String afguid)
			throws HAException {
		ProductionServerRoot root = RepositoryUtil.getInstance(xml).getProductionServerRoot(afguid);
		return root;

	}

	public static boolean useVhdxFormat() throws HAException {
			return useVHDXformat;
	}
	
	public static boolean IsTargetPlatformSupportVMGeneretion2(ADRConfigure adrConfigure, BackupInfo backupInfo, SourceNodeSysInfo SourceNodeSysInfo)
	{
		String osVersion = adrConfigure.getOSVersion();
		String osType = backupInfo.getServerInfo().getOsType();
		boolean isAMD64 = osType.equalsIgnoreCase("64-bit");
		String targetPlatformVersion = SourceNodeSysInfo.getVersion();		
		return IsTargetPlatformSupportVMGeneretion2(osVersion, isAMD64, targetPlatformVersion);
	}
	
	public static boolean IsTargetPlatformSupportVMGeneretion2(String osVersion, boolean isAMD64, String targetPlatformVersion)
	{
		// Get source machine's OS version
		//The following guest operating systems are supported as generation 2 virtual machines.
		//Windows Server 2012 
		//Windows Server 2012 R2 
		//64-bit versions of Windows 8
		//64-bit versions of Windows 8.1
		boolean sourceMachineSupportGeneration2 = isAMD64 && ((new BigDecimal(osVersion)).compareTo(new BigDecimal("6.2")) >= 0);
		if(sourceMachineSupportGeneration2 && (new BigDecimal(targetPlatformVersion)).compareTo(new BigDecimal("6.3")) >= 0) // Target hyperv must be 2012 r2 or above
		{
			return true;
		}	
		return false;
	}
	
	public static String taskSnapshotVMAsync(ProductionServerRoot psr) throws Exception
	{
		log.info("start the hyper-v Snapshot task...");
		purgeInvalidTask();
		
		SnapshotFutureTask task = new SnapshotFutureTask(new SnapshotCallable(psr));
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
		String taskId = psr.getProductionServerAFUID() + ":" + now.toString();
		
		taskContainer.put(taskId, task);
		Thread executor = new Thread(task);
		executor.start();
		log.info(String.format("The Hyper-V Snapshot task id %s has started.", taskId));
		return taskId;
	}
	
	public static boolean isSnapshotVmTaskDone(String taskId) throws Exception
	{
		if (!taskContainer.containsKey(taskId)) {
			log.error(String.format("The Hyper-V Snapshot task id %s does not exist.", taskId));
			throw new Exception(String.format("The Hyper-V Snapshot task id %s does not exist.", taskId));
		}
		
		SnapshotFutureTask task = taskContainer.get(taskId);
		if (task.isDone()){
     		taskContainer.remove(taskId);
			task.get();		
			return true;
		}
		return false;
	}
	
	/**
	 * if the first one, create a HyperVVM, else modify the disk, and then
	 * attach. the snapshot of the first session, which the replication client
	 * reports after fail over registry, maybe not full session.
	 * 
	 * We do this work at server because we need to attach file locally. Also,
	 * we can isolate the HyperV WMI on monitor alone, instead of each monitee.
	 * 
	 * @param psr
	 * @throws Exception
	 */
	public static void SnapeShotVM(ProductionServerRoot psr) throws Exception {
		if (log.isDebugEnabled())
			log.debug("SnapeShotVM begin:" + psr.toString());

		ReplicaRoot replicaRoot = psr.getReplicaRoot();
		List<DiskDestination> diskDestinations = replicaRoot.getDiskDestinations();
		
		String afGuid = psr.getProductionServerAFUID();

		// if not the right replication protocol, return directly
		if (replicaRoot.whatRepliProtocol() != Protocol.HeartBeatMonitor) {
			log.debug("Only support " + Protocol.HeartBeatMonitor
					+ " replication. However, "
					+ replicaRoot.whatRepliProtocol() + "is reported.");
			return;
		}

		String sessionName = "";
		// VM should be created already
		String rootPath = RepositoryManager.getRootPath(psr,Protocol.HeartBeatMonitor);
		log.info("RootPath in Monitor: rootPath=" + rootPath);
		
		VMInfomationModelManager manager = HACommon.getSnapshotModeManager(rootPath, afGuid);
		VirtualMachineInfo vmInfo = manager.getInternalVMInfo(afGuid);
		String vmName = psr.getReplicaRoot().getVmname();
		Virtualization hypervisor = HAService.getInstance().getFailoverJobScript(afGuid).getFailoverMechanism().get(0);
		if (vmInfo == null || vmInfo.getType() != 0) {
			String msg = ReplicationMessage.getMonitorServiceErrorString(MonitorWebServiceErrorCode.Repository_Session_SnapShot_WITHOUT_VM);
			throw new Exception(msg);
		}

		long handle = 0;
		try {
			handle = HyperVJNI.OpenHypervHandle("", "", "");
		} catch (HyperVException hyperVe) {
			log.error(hyperVe.getMessage(), hyperVe);
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_CONNECT_HYPERV, ((HyperV)hypervisor).getHostName());
			throw new Exception(msg);
		}
		try {

			// If it is not powered off, throw an error
			try {
				if (HyperVJNI.VM_STATE_TURNED_OFF != HyperVJNI.GetVmState(handle, vmInfo.getVmGUID())) {
					String msg = ReplicationMessage.getMonitorServiceErrorString(MonitorWebServiceErrorCode.Repository_Session_SnapShot_VM_POWEROFF);
					throw new Exception(msg);
				}
			} catch (HyperVException e) {
				String msg = ReplicationMessage.getMonitorServiceErrorString(MonitorWebServiceErrorCode.Repository_Session_SnapShot_VM_POWEROFF);
				throw new Exception(msg);
			}

			// check use vhd or vhdx format.
			try {
				try {
					SourceNodeSysInfo sourceNodeSysInfo = new SourceNodeSysInfo();
					HAService.getInstance().getNativeFacade().getSourceNodeSysInfo(sourceNodeSysInfo);
					String targetPlatform = sourceNodeSysInfo.getVersion();
					if(targetPlatform.startsWith("6.0") || targetPlatform.startsWith("6.1"))
						useVHDXformat = false;
					
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					log.error("Fail to get source node information, afguid = " + afGuid);
				}
				
				if(useVHDXformat)
				{
					List<String> getAttachedDiskImage = new ArrayList<String>();
					HyperVJNI.GetAttachedDiskImage(handle, vmInfo.getVmGUID(), getAttachedDiskImage);
					ListIterator<String> listIterator = getAttachedDiskImage.listIterator();
						
					if (listIterator.hasNext()) {
						String string = (String) listIterator.next();
						if(string.endsWith(".vhd") || string.endsWith(".avhd"))
							useVHDXformat = false;
					}
				}
			} catch (HyperVException e) {
				log.error(e.getMessage(), e);
				log.error("Cannot get disk images file of VM:" + vmInfo.getVmGUID());
			}
			
			//
			sessionName = replicaRoot.getLatestReplicatedSession();
			
			String sessionPath = RepositoryManager.getSessionPath(rootPath,
					sessionName);

			BackupInfo backupInfo = BackupInfoFactory.getBackupInfo(sessionPath
					+ "BackupInfo.XML");
			String sessionGuids = replicaRoot.getSessionGuids();
			String[] tokens = sessionGuids.split("[|]");
			String lastSessionGuid = tokens[tokens.length-1];
			// if it is already snapshot on this VM, return immediately
			boolean existB = false;
			try {
				String snapshot = HACommon.testSnapshot(rootPath,handle, afGuid,
						lastSessionGuid, vmInfo.getVmGUID());
				existB = (snapshot != null && !snapshot.isEmpty());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw e;
			}
			if (existB) {
				log.debug("Session with Guid:" + lastSessionGuid
						+ " is already snapshoted on VM:" + vmInfo.getVmGUID());
				return;
			}

			log.debug("Get last snapshot for our session, and revert to it...");
			String lastSnapshot = null;
			SortedSet<VMSnapshotsInfo> lastSnapshotForD2DSession = HACommon
					.getSnapshotForD2DSession(rootPath,handle, afGuid, false);
			VMSnapshotsInfo snapshotForD2DSession = null;
			if (!lastSnapshotForD2DSession.isEmpty()) {
				for (VMSnapshotsInfo tempVMSnapshotsInfo : lastSnapshotForD2DSession) {
					if(!tempVMSnapshotsInfo.isDRSnapshot()){
						snapshotForD2DSession = tempVMSnapshotsInfo;
						break;
					}
				}
				//snapshotForD2DSession = lastSnapshotForD2DSession.first();
			}
			if (snapshotForD2DSession != null)
				lastSnapshot = snapshotForD2DSession.getSnapGuid();
			if (lastSnapshot != null) {
				try {
					HyperVJNI.RevertToVmSnapshot(handle, vmInfo.getVmGUID(),
							lastSnapshot);
				} catch (HyperVException e) {
					
					log.error(e.getMessage(),e);
					
					String msg = FailoverMessage.getResource(
							FailoverMessage.FAILOVER_PROCESS_VMI_FAIL_REVERT_SNAPSHOT,
							snapshotForD2DSession.getSessionName(),
							psr.getProductionServerHostname(),HACommon.getRealHostName(),
							psr.getReplicaRoot().getVmname());
					
					log.error("wmi return message: " + HyperVJNI.GetLastErrorMessage());
					
					msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_HYPERV_Returne_MESSAGE)
							+ HyperVJNI.GetLastErrorMessage();					
					throw new Exception(msg);
				}
			}

			log.debug("SnapeShotVM rename all d2d files into (a)vhds ..., the parent path will also be changed");
			
			ADRConfigure adrConfigure = RepositoryManager.getADRConfigure(
					rootPath, sessionName);
			
			SessionInfoForSnapshot[] sessionInfos = RepositoryManager
					.modifyD2DFile(handle, vmInfo.getVmGUID(),
							lastSnapshotForD2DSession, diskDestinations, adrConfigure,
							sessionName, vmName, ((HyperV)hypervisor).getHostName());
			if (sessionInfos == null || sessionInfos.length == 0) {
				String msg = ReplicationMessage.getMonitorServiceErrorString(MonitorWebServiceErrorCode.Repository_VHD_File_Lost);
				throw new Exception(msg);
			}
			if (log.isDebugEnabled()) {

				String temp = Arrays.toString(sessionInfos);
				log.debug("SnapeShotVM vhd files:" + temp);

			}
			List<String> scsiName = new ArrayList<String>();
			try {
				HyperVJNI
						.ListScsiControls(handle, vmInfo.getVmGUID(), scsiName);
				if (scsiName.isEmpty()) {
					HyperVJNI.AddScsiControl(handle, vmInfo.getVmGUID(),
							HyperVJNI.SCSI_CONTROLLER);
					scsiName.add(HyperVJNI.SCSI_CONTROLLER);
				}
			} catch (HyperVException hyperVe) {
				log.error(hyperVe.getMessage(), hyperVe);
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_TAKE_SNAPSHOT, vmInfo.getVmName(), sessionName);
				throw new Exception(msg);
			}
			
			//get the current disk image file list, which should be deleted after detached.
			List<String> currentDiskImageList = new ArrayList<String>();
			try {
				HyperVJNI.GetAttachedDiskImage(handle, vmInfo.getVmGUID(), currentDiskImageList);
				if (currentDiskImageList.size() > 0) {
					List<String> toBeIgnored = new ArrayList<String>();
					for (String imagePath:currentDiskImageList) {
						if (imagePath.contains(CA_AVHD_GUID) || 
							imagePath.toUpperCase().endsWith(".VHDX") || 
							imagePath.toUpperCase().endsWith(".VHD") ||
							imagePath.toUpperCase().endsWith(".D2D")) {
							toBeIgnored.add(imagePath);
							log.info("Ignore file " + imagePath + ".");
						}
					}
					currentDiskImageList.removeAll(toBeIgnored);
				}
			}
			catch (Exception e) {
				log.error(String.format("Failed to get the disk image files of VM %s in Hyper-V.", vmInfo.getVmGUID()).toString(), e);
			}
			
			//detach all the disks from virtual machine
			try {
				HyperVJNI.DetachAllDisks(handle, vmInfo.getVmGUID());
			}
			catch (HyperVException hyperVe) {
				log.error(hyperVe.getMessage(), hyperVe);
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_TAKE_SNAPSHOT, vmInfo.getVmName(), sessionName);
				throw new Exception(msg);
			}
			
			//we should maybe maintain the previous disks in case user just backup some of disks
			// first we attach system disk to IDE 0,0
			int attachedIDE = 0;
			int attachedSCSIPos = 0;
			int vmGeneration = vmInfo.getVmGeneration();
			if(vmGeneration == 1)
			{
				for (SessionInfoForSnapshot sessionInfo : sessionInfos) {
					if (sessionInfo.isSystemDisk()) {
						if (log.isDebugEnabled()) {
							log.debug("Attach system volume disk IDE(0,0):"
									+ sessionInfo.getDiskFile());
						}
						try{
							HyperVJNI.ModifyAttachedIdeHardDisk(handle, vmInfo
											.getVmGUID(), sessionInfo.getDiskFile(),
											true, 0, 0);
						}catch (HyperVException e) {
							log.error(e.getMessage(), e);
							String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_TAKE_SNAPSHOT, vmInfo.getVmName(), sessionName);
							throw new Exception(msg);
						}
						attachedIDE++;
					} 
				}
			}
			
			for (SessionInfoForSnapshot sessionInfo : sessionInfos) {
				if (sessionInfo.isSystemDisk() && (vmGeneration == 1))
					continue;
				else {
					try{
						// IDE1,1 is reserved for DVD
						if( attachedIDE < HyperVJNI.IDE_POSITION_MAX*HyperVJNI.IDE_CONTROLLER_MAX - 1
								&& (sessionInfo.getControllerType() == "IDE" || sessionInfo.isBootDisk()) && (vmGeneration == 1)){
							HyperVJNI.ModifyAttachedIdeHardDisk(handle, vmInfo.getVmGUID(), 
									sessionInfo.getDiskFile(), true, attachedIDE/HyperVJNI.IDE_POSITION_MAX, 
									attachedIDE%HyperVJNI.IDE_POSITION_MAX);
							attachedIDE++;
						}
						else{
							if( attachedSCSIPos >= HyperVJNI.SCSI_POSITION_MAX * scsiName.size())
							{
								String scsiControllerName =  HyperVJNI.SCSI_CONTROLLER + Integer.toString(attachedSCSIPos / HyperVJNI.SCSI_POSITION_MAX);
								HyperVJNI.AddScsiControl(handle, vmInfo.getVmGUID(), scsiControllerName);
								scsiName.add(scsiControllerName);
							}
							HyperVJNI.ModifyAttachedScsiHardDisk(handle, vmInfo.getVmGUID(), 
									sessionInfo.getDiskFile(), true,
									scsiName.get(attachedSCSIPos/HyperVJNI.SCSI_POSITION_MAX), 
									attachedSCSIPos%HyperVJNI.SCSI_POSITION_MAX);
							
							if((vmGeneration == 2) && (attachedSCSIPos == 0)) // SCSI 0, position 1 is reserved for DVD
								attachedSCSIPos++;
							
							attachedSCSIPos++;
						}
					}catch (HyperVException e) {
						log.error(e.getMessage(), e);
						String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_TAKE_SNAPSHOT, vmInfo.getVmName(), sessionName);
						throw new Exception(msg);
					}
				}
			}

			log.debug("Taking Snapshot for session: " + sessionName);
			String snapdesc = sessionName;
			if (backupInfo.getBackupname() != null
					&& !backupInfo.getBackupname().isEmpty()) {
				snapdesc = backupInfo.getBackupname();
			}

			MessageFormat form = new MessageFormat(
					"Backup name: {0} \n\tDate: {1}\n\tTime: {2}\n\tBackup Type: {3}\n{4}");
			Object[] args = { snapdesc, backupInfo.getDate(),
					backupInfo.getTime(), backupInfo.getBackupType(),
					backupInfo.getServerInfo().toFomattedString() };
			snapdesc = form.format(args);

			try {
				
				Date bakupTime = BackupConverterUtil.string2Date(backupInfo.getDate() + " " + backupInfo.getTime());
				
				VMSnapshotsInfo snapshotInfo = new VMSnapshotsInfo();
				snapshotInfo.setSessionName(sessionName);
				snapshotInfo.setSessionGuid(sessionGuids);
				snapshotInfo.setDesc(snapdesc);
				snapshotInfo.setTimestamp(bakupTime.getTime());
				snapshotInfo.setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(bakupTime));
				createSessionSnapshot(handle, psr, vmInfo, lastSnapshotForD2DSession, 
						snapshotInfo, false,rootPath,adrConfigure);
						
						
				//delete the detached disk image files, since they will be replaced by the actual converted disk file.
				try {
					if (currentDiskImageList.size() > 0) {
						log.info("Start to delete the detached image files, which have been replaced by the attached converted disk files.");
						for (String strDiskImageName : currentDiskImageList) {
							File vhdFile = new File(strDiskImageName);
							if (vhdFile.exists()) {
								if (vhdFile.delete()) {
									log.info(String
											.format("Disk image file %s has been deleted successfully.",
													strDiskImageName).toString());
								}
							}
						}
						log.info("Finish deleting the detached image file list.");
					}
				} catch (Exception e) {
					log.error(String.format("Failed to dete the detached image files.").toString(), e);
				}
				
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				log.error("Failed to take the session snapshot:"+e.getMessage(),e);
			}

			if (psr.getReplicaRoot() != null) {
				if (psr.getReplicaRoot().getVmuuid() == null) {
					psr.getReplicaRoot().setVmuuid(vmInfo.getVmGUID());
				}
			}
			
			RepositoryUtil.getInstance(xml).saveProductionServerRoot(psr);

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		} catch (Throwable te) {
			log.error(te.getMessage(),te);				
		} finally {
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException hyperVe) {}
		}
	}

	public static String createSessionSnapshot(long handle,ProductionServerRoot psr, VirtualMachineInfo vmInfo,SortedSet<VMSnapshotsInfo> lastSnapshotForD2DSession,
			VMSnapshotsInfo snapshotInfo,boolean isTakenDRSnapshot,String lastRepDest,ADRConfigure adrConfigure) throws Exception{
		
		log.info("rootPath=" + lastRepDest);
		VMInfomationModelManager manager = HACommon.getSnapshotModeManager(lastRepDest, psr.getProductionServerAFUID());
		String snap = createSessionSnapshot(handle, psr, vmInfo, lastSnapshotForD2DSession, snapshotInfo, isTakenDRSnapshot, manager,adrConfigure);
	
		return snap;
	}

	
	private static void delLeftImageFiles(String preSnapShotGUID, String sessionName, String nextSnapShotGUID, List<String>curRootSnapshotFileList,  List<String> preRootSnapshotFileList)
	{
		if (nextSnapShotGUID == null || nextSnapShotGUID.isEmpty() || preRootSnapshotFileList.size() == 0 
				|| curRootSnapshotFileList.size() == 0) {
			return;
		}

		// check the current snapshot is a full backup session's snapshot.
		for (String fileName:curRootSnapshotFileList) {
			if (!fileName.trim().toLowerCase().endsWith(".vhd") && !fileName.trim().toLowerCase().endsWith(".vhdx")) {
				log.info(String.format("The next snapshot %s should be a incremental backup session's snapshot.", nextSnapShotGUID).toString());
				return;
			}
		}
		
		List<String> delList = new ArrayList<String>();
		for (String fileName: preRootSnapshotFileList) {
			if (!fileName.trim().toLowerCase().endsWith(".vhd") && !fileName.trim().toLowerCase().endsWith(".vhdx")) {
				continue;
			}
			
			for (String curFileName:curRootSnapshotFileList) {
				if (curFileName.trim().equalsIgnoreCase(fileName.trim()) || curFileName.toLowerCase().contains("\\" + sessionName.toLowerCase() + "\\")) {
					log.info(String.format("The image file %s of session %s is still contained in the next snapshot.", fileName, sessionName, nextSnapShotGUID).toString());
					return;
				}
				else
				{
					log.info(String.format("The image file %s is not contained in the next snapshot.", fileName, nextSnapShotGUID).toString());
					delList.add(fileName);
				}
			}
		}
		
		try
		{
			if (delList.size() > 0) {
				log.info("Image file list of the next root snapshot.");
				for (String fileName : curRootSnapshotFileList) {
					log.info(String.format("\t%s", fileName));
				}
				
				log.info(String.format("Start to delete the image files beloned to session: %s snapshot %s.", preSnapShotGUID, sessionName).toString());
				for (String fileName:delList){
					if (new File(fileName).delete()) {
						log.info(String.format("Delete file %s successfully", fileName).toString());
					}
				} 
			}
		}
		catch(Exception e){
			log.info(String.format("Failed to delete the image files for the full backup session: %s snapshot %s.", preSnapShotGUID, sessionName).toString());
		}
	}
	
	
	public static String createSessionSnapshot(long handle,ProductionServerRoot psr, VirtualMachineInfo vmInfo,
				  SortedSet<VMSnapshotsInfo> lastSnapshotForD2DSession, VMSnapshotsInfo snapshotInfo,
				  boolean isTakenDRSnapshot, VMInfomationModelManager manager,ADRConfigure adrConfigure) throws Exception{
		int adjust = 1;
		String snap = "";
		boolean hasTakenSnapshot = false;
		try {
			
			Map<String, String> snapShots = HyperVJNI.GetVmSnapshots(handle, vmInfo.getVmGUID());
			//int smallNumber = psr.getRetentionCount() > CommonUtil.MAX_HYPERV_SNAPSHOT?psr.getRetentionCount():CommonUtil.MAX_HYPERV_SNAPSHOT;
			int smallNumber = Math.min(psr.getRetentionCount()*2, psr.getMaxSnapshotCount());
			
			if(psr.getMaxSnapshotCount() != CommonUtil.MAX_HYPERV_SNAPSHOT){
				//user configure how many recovery point are kept. Need to double that recovery points.  One recovery point consists of snapshot 
				//and bootable snapshot
				smallNumber = (smallNumber * 2) > CommonUtil.MAX_HYPERV_SNAPSHOT ? CommonUtil.MAX_HYPERV_SNAPSHOT:(smallNumber * 2);
			}
			
			if(smallNumber < CommonUtil.MAX_HYPERV_SNAPSHOT){
				//if user configure max snapshot count via registry key and it is smaller than max snapshot 
				//count in theory.  Then no need to minus 1 snapshot for DR snapshot.
				adjust = 0;
			}
			
			if(isTakenDRSnapshot){
				adjust = 0;
				smallNumber = 50; //If taking the DR snapshot, begin mrege the snapshot when it reachs snapshot count = 50
			}
			
			
			//We should reverse two snapshots for taking snapshot: one is for the session snapshot, and another is for the bootable session snapshot.
			if(snapShots.size() >= (smallNumber-adjust)){
				int totalDeleteCount=0;
				
				List<VMSnapshotsInfo> vmList = new ArrayList<VMSnapshotsInfo>(lastSnapshotForD2DSession);
				List<VMSnapshotsInfo> deletedSnapshots = new ArrayList<VMSnapshotsInfo>();
				
				//begin to delete the DR snapshot
				for (VMSnapshotsInfo vmSnapshotsInfo : vmList) {
					if(vmSnapshotsInfo.isDRSnapshot()){
						try {						
							HyperVJNI.DeleteVmSnapshot(handle, vmInfo.getVmGUID(), vmSnapshotsInfo.getSnapGuid());
							totalDeleteCount++;
							deletedSnapshots.add(vmSnapshotsInfo);
							log.info("Successfully delete the DR snapshot:"+vmSnapshotsInfo.getSessionName()+" snapshot GUID:"+vmSnapshotsInfo.getSnapGuid());
							
						} catch (Exception e) {
							log.error("Failed to delete the DR snapshot:"+vmSnapshotsInfo.getSessionName()+" snapshot GUID:"+vmSnapshotsInfo.getSnapGuid());
							log.error(e.getMessage(),e);
						}
					}
				}
				//end 
				
				int leftSnapshotCount = snapShots.size()-totalDeleteCount;
				if( leftSnapshotCount <(smallNumber-adjust)){
					log.info("Successfully merge the VM DR snapshots, with delete snapshot cout:"+totalDeleteCount);
				}
				else{
					if (smallNumber == 2) {
						try {
							snap = HyperVJNI.TakeVmSnapshot(handle, vmInfo.getVmGUID(),
									snapshotInfo.getSessionName(), snapshotInfo.getDesc());
							hasTakenSnapshot = true;
							log.info("Create the session snapshot: snapshotName:"+snapshotInfo.getSessionName() + "SnapshotGuid:"+snap);
						} catch (HyperVException hyperVe) {
							
							log.error(hyperVe.getMessage(),hyperVe);
							
							String msg = FailoverMessage.getResource(
									FailoverMessage.FAILOVER_PROCESS_VMI_FAIL_TAKE_SNAPSHOT,
									snapshotInfo.getSessionName(),
									psr.getProductionServerHostname(),HACommon.getRealHostName(),
									psr.getReplicaRoot().getVmname());
							
							log.error("wmi return message: " + HyperVJNI.GetLastErrorMessage());
							
							msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_HYPERV_Returne_MESSAGE)
									+ HyperVJNI.GetLastErrorMessage();						
							throw new Exception(msg);
						}
					}
					int diff = leftSnapshotCount - smallNumber + 1 + adjust;
					for(int i=vmList.size()-1; i>=0; i--){
						if(diff >= 0){
							VMSnapshotsInfo info = vmList.get(i);
							boolean delSuc = true;
							int delteHandle = -1;
							if(snapShots.containsKey(info.getSnapGuid())){
								String snapshotFolder = null;
								
								List<String> preRootSnapshotFileList = new ArrayList<String>();
								String nextSnapShotGUID = null;
								if(!info.isDRSnapshot()) {
									nextSnapShotGUID = i >= 1 ?  vmList.get(i - 1).getSnapGuid() : snap;
									try {
										delteHandle = HyperVJNI.BeginDeleteSnapshot(vmInfo.getVmGUID(), info.getSnapGuid(), info.getBootableSnapGuid(), nextSnapShotGUID);
										log.info("Start to get image file list snapshot:"+info.getSessionName()+" snapshot GUID:"+info.getBootableSnapGuid());
										HyperVJNI.GetSnapshotVhds(handle, vmInfo.getVmGUID(), info.getSnapGuid(), preRootSnapshotFileList);
										log.info("Successfully get image file list snapshot:"+info.getSessionName()+" snapshot GUID:"+info.getBootableSnapGuid());
									} catch (Exception e) {
										log.error("Failed to begin the snapshot delete:"+info.getSessionName()+" snapshot GUID:"+info.getSnapGuid());
										log.error(e.getMessage(),e);
									}
								}
								else {
									try {
										snapshotFolder = HyperVJNI.getVHDDirForSpecificSnapshot(handle,vmInfo.getVmGUID(),info.getBootableSnapGuid());
										log.info("snapshotFolder: " + snapshotFolder);
									} catch (Exception e) {
										log.error(e.getMessage(),e);
									}
								}
								
								//delete the bootable snapshot
								if(!info.isDRSnapshot()){
									try {
										
										HyperVJNI.DeleteVmSnapshot(handle, vmInfo.getVmGUID(), info.getBootableSnapGuid());
										totalDeleteCount++;
										log.info("Successfully delete the bootable snapshot:"+info.getSessionName()+" snapshot GUID:"+info.getBootableSnapGuid());
										
									} catch (Exception e) {
										delSuc = false;
										log.error("Failed to delete the bootable snapshot:"+info.getSessionName()+" snapshot GUID:"+info.getBootableSnapGuid());
										log.error(e.getMessage(),e);
									}
								}
																
								//delete the session snapshot
								try {
									HyperVJNI.DeleteVmSnapshot(handle, vmInfo.getVmGUID(), info.getSnapGuid());
									totalDeleteCount++;
									deletedSnapshots.add(info);
									log.info("Successfully delete the snapshot:"+info.getSessionName()+" snapshot GUID:"+info.getSnapGuid());
								} catch (Exception e) {
									delSuc = false;
									log.error("Failed to delete the snapshot:"+info.getSessionName()+" snapshot GUID:"+info.getSnapGuid());
									log.error(e.getMessage(),e);
								}
								
								if(!info.isDRSnapshot()) 
								{
									try {
										if(delteHandle != 0)
											HyperVJNI.EndDeleteSnapshot(delteHandle, delSuc);
									} catch (Exception e) {
										log.error("Failed to end the snapshot delete:"+info.getSessionName()+" snapshot GUID:"+info.getSnapGuid());
										log.error(e.getMessage(),e);
									}
									
									List<String> curRootSnapshotFileList = new ArrayList<String>();
									try {
										log.info("Start to get image file list of next snapshot GUID:"+nextSnapShotGUID);
										HyperVJNI.GetSnapshotVhds(handle, vmInfo.getVmGUID(), nextSnapShotGUID, curRootSnapshotFileList);
										log.info("Successfully get image files of next snapshot GUID:"+info.getBootableSnapGuid());
									} catch (Exception e) {
										log.error("Failed to begin the snapshot delete:"+info.getSessionName()+" snapshot GUID:"+info.getSnapGuid());
										log.error(e.getMessage(),e);
									}
									
									// check if the current snapshot and the next snapshot are the full backup session's snapshots.
									// if then, the left image files should be deleted.
									if (delSuc)
										delLeftImageFiles(info.getSessionName(), info.getSnapGuid(), nextSnapShotGUID, curRootSnapshotFileList, preRootSnapshotFileList);
								}
								else
								{
									NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
									if(!StringUtil.isEmptyOrNull(snapshotFolder) 
										 && nativeFacade.HAIsHostOSGreaterEqual(6, 1, (short)0, (short)0)){ //if > win2008R2 
										File folder = new File(snapshotFolder);
										File[] files = folder.listFiles(new FileFilter() {
											@Override
											public boolean accept(File pathname) {
												return pathname.getName().endsWith(".vhd")
													|| pathname.getName().endsWith("000000000000.avhd")
													|| pathname.getName().endsWith(".vhdx")
													|| pathname.getName().endsWith("000000000000.avhdx");
											}
										});
										if(files != null && files.length == 0){
											File[] allFiles = folder.listFiles();
											for (File file : allFiles) {
												file.delete();
											}
											if(folder.delete()){
												log.info("succeed to delete bootable snapshot folder." + info.getBootableSnapGuid());
											}else{
												log.info("fail to delete bootable snapshot folder." + info.getBootableSnapGuid());
											}
										}
									}
								}

								diff--;
								
								if((snapShots.size()-totalDeleteCount)<(smallNumber-adjust)){
									log.info("Successfully merge the VM snapshots, with delete snapshot cout:"+totalDeleteCount);
									break;
								}
							}
						}
					}
				}
				
				lastSnapshotForD2DSession.removeAll(deletedSnapshots);
				//sync snapshot in vm and in configuration file
				manager.putSnapShot(vmInfo, lastSnapshotForD2DSession);		
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
		
		if (!hasTakenSnapshot) {
			try {
				snap = HyperVJNI.TakeVmSnapshot(handle, vmInfo.getVmGUID(),
						snapshotInfo.getSessionName(), snapshotInfo.getDesc());
				log.info("Create the session snapshot: snapshotName:"+snapshotInfo.getSessionName() + "SnapshotGuid:"+snap);
			} catch (HyperVException hyperVe) {
				
				log.error(hyperVe.getMessage(),hyperVe);
				
				String msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_PROCESS_VMI_FAIL_TAKE_SNAPSHOT,
						snapshotInfo.getSessionName(),
						psr.getProductionServerHostname(),HACommon.getRealHostName(),
						psr.getReplicaRoot().getVmname());
				
				log.error("wmi return message: " + HyperVJNI.GetLastErrorMessage());
				
				msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_HYPERV_Returne_MESSAGE)
						+ HyperVJNI.GetLastErrorMessage();
			
				throw new Exception(msg);
			}
		}

		try {
			
			VMSnapshotsInfo sinfo = new VMSnapshotsInfo(snapshotInfo.getSessionName(),
					snapshotInfo.getSessionGuid(), snap, snapshotInfo.getTimestamp());
			sinfo.setSnapNo(manager.getSnapNo());
			sinfo.setLocalTime(psr.getReplicaRoot().getBackupLocalTime());
			if(adrConfigure != null){
				sinfo.setPowerOffBackup(adrConfigure.isPartialAdrconfigure());
			}
			manager.putSnapShot(vmInfo, sinfo);
		} catch (Throwable e) {
			log.error("Try to delete snapshot:"+snap);
			HyperVJNI.DeleteVmSnapshot(handle, vmInfo.getVmGUID(), snap);
		}
		
		return snap;
	}
	
	
	/**
	 * 
	 * @param serverRoot
	 * @return rootPath for example d:\temp\GONRO07-W2k3-2\
	 * @throws HAException
	 */
	public static String getRootPath(ProductionServerRoot serverRoot,
			Protocol protocol) {
		if (serverRoot.getReplicaRoot().whatRepliProtocol() == protocol) {
			switch (serverRoot.getReplicaRoot().whatRepliProtocol()) {
			case HeartBeatMonitor:
				TransServerReplicaRoot temp = (TransServerReplicaRoot) serverRoot
						.getReplicaRoot();
				String rootPath = temp.getRootPath();//Default folder is root path
				return rootPath;
			default:
				break;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param rootPath
	 * @param sessionName
	 * @return for example d:\temp\GONRO07-W2k3-2\VStore\S0000000001\
	 */
	private static String getSessionPath(String rootPath, String sessionName) {
		return rootPath + "VStore\\" + sessionName + "\\";
	}

	/**
	 * rename the disk name according to its type. Differencing will be changed
	 * to avhd, and also its parent path will be changed
	 * 
	 * @param rootPath
	 * @param aDRConfigure
	 * @param sessionName
	 * @param previousDisks
	 *            c:\tt\disk1111.avhd, disk222
	 * @return
	 * @throws HAException
	 */
	private static SessionInfoForSnapshot[] modifyD2DFile(long handle,
			String vmGuid,
			SortedSet<VMSnapshotsInfo> lastSnapshotForD2DSession,
			List<DiskDestination> diskDestinations, ADRConfigure aDRConfigure,
			String sessionNumber, String vmName, String hyperVServer) throws Exception {

		if (diskDestinations == null || diskDestinations.isEmpty())
			return new SessionInfoForSnapshot[0];

		ArrayList<SessionInfoForSnapshot> re = new ArrayList<SessionInfoForSnapshot>();

		String fakeGuid = "_00000000-0000-0000-0000-000000000000";
		
		List<String> bootVolumes = new ArrayList<String>();
		List<String> systemVolumes = new ArrayList<String>();
		
		boolean foundSys = false;
		try {
			int count = 0;
			boolean foundBoot = false;
			while(count < 2 && (!foundBoot || !foundSys)){
				count++;
				re.clear();
				log.info("try to found boot/system volume:" + count);
				
				//if it's the second time retry, let the current thread sleep 3 seconds.
				if(count == 2) {
					try {
						Thread.sleep(RETRY_INTERVAL);
					}
					catch(Exception e) {
					}
				}
				SortedSet<Disk> disks2 = aDRConfigure.getDisks();
				for (Disk di : disks2) {
					
					String diskSig = di.getSignature();
					String diskPath = getDiskPath(diskDestinations, di);
					String sessionpath = getSessionPath(diskPath,sessionNumber); //rootPath + "VStore\\" + sessionNumber + "\\";
					String diskName = sessionpath + "disk" + diskSig;
					String fileExt = ".vhd";
					if(useVhdxFormat())
						fileExt = ".vhdx";
					
					File file = new File(diskName + fileExt);
					if (!file.exists() || !file.isFile() || !file.canRead()) {
						fileExt = ".avhd";
						if(useVhdxFormat())
							fileExt = ".avhdx";
						
						file = new File(diskName + fileExt);
						if (!file.exists() || !file.isFile() || !file.canRead()){
							fileExt = ".d2d";
							file = new File(diskName + ".d2d");
							if (!file.exists() || !file.isFile() || !file.canRead()){
								fileExt = ".avhd";
								if(useVhdxFormat())
									fileExt = ".avhdx";
								
								file = new File(diskName + fakeGuid + fileExt);
								if (!file.exists() || !file.isFile() || !file.canRead()){
									continue;
								}
							}
						}
					}
					
					
					String bootVolumePath = "";
					
					String systemVolumePath = "";
					
					//When aDRConfigure.getBootvolume() != null, we get the boot/systemVolume from adrconfigure
					//Otherwise, by trying to mount vhd, avhd. These codes are put at the end of the method after the 
					//files are correctly renamed.
					if(aDRConfigure.getBootvolume() != null){
						//foundBoot and foundSys only are used for the not-adrconfigure situation, set them to true if it exists.
						foundBoot = true;
						foundSys = true;
						List<String> getAttachedDiskImage = new ArrayList<String>();
						bootVolumePath = getBootVolumeD2DPath(handle, aDRConfigure,
								diskDestinations, sessionNumber, getAttachedDiskImage, hyperVServer);
						
						systemVolumePath = getSystemVolumeD2DPath(aDRConfigure,
								diskDestinations, sessionNumber, hyperVServer);
						
					}
					
					
					
					
					int sourceSuffixLen = 4;
					if (file.getName().toLowerCase().endsWith(".d2d")) {
						sourceSuffixLen = 4;
					} else if (file.getName().toLowerCase().endsWith(".vhd")) {
						sourceSuffixLen = 4;
					} else if (file.getName().toLowerCase().endsWith(".avhd")) {
						sourceSuffixLen = 5;
					}
					else if (file.getName().toLowerCase().endsWith(".vhdx")) {
						sourceSuffixLen = 5;
					}
					else if (file.getName().toLowerCase().endsWith(".avhdx")) {
						sourceSuffixLen = 6;
					}
	
					{
	
						// filevhd c:/sessoin1/S00000000/disk0000000.vhd,d2d,avhd
						String filevhd = file.getAbsolutePath();
						// filePathWithoutSuffix c:/sessoin1/S00000000/disk0000000
						String filePathWithoutSuffix = filevhd.substring(0, filevhd
								.length()
								- sourceSuffixLen);
						// pureNameWithoutSuffix disk0000000
						String pureNameWithoutSuffix = file.getName().substring(0,
								file.getName().length() - sourceSuffixLen);
						
						boolean isDiff = HyperVJNI.isDifferencingDisk(filevhd);
						if (isDiff) {
							List<String> disks = new ArrayList<String>();
	
							boolean foundParent = false;
							outloop: for (VMSnapshotsInfo snapshotInfo : lastSnapshotForD2DSession) {
								String snapshot = snapshotInfo.getSnapGuid();
	
								try {
									HyperVJNI.GetSnapshotVhds(handle, vmGuid,
											snapshot, disks);
								} catch (HyperVException e) {
									
									String msg = ReplicationMessage.getResource(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_SNAPSHOT, vmName);
									throw new Exception(msg);
								}
	
								for (String preDisk : disks) {
									File temp = new File(preDisk);
									// String preFilevhd = preDisk;
									int suffixLen = 0;
									if (preDisk.toLowerCase().endsWith(".vhd"))
										suffixLen = 4;
									else if (preDisk.toLowerCase()
											.endsWith(".avhd"))
										suffixLen = 5;
									else if (preDisk.toLowerCase()
											.endsWith(".vhdx"))
										suffixLen = 5;
									else if (preDisk.toLowerCase()
											.endsWith(".avhdx"))
										suffixLen = 6;
									else {
										String msg = ReplicationMessage.getResource(ReplicationMessage.VIRTUAL_CONVERSION_FAILED_SNAPSHOT, vmName);
										throw new Exception(msg);
									}
	
									// String preFilePathWithoutSuffix = filevhd.substring(0,filevhd.length()-suffixLen);
									String prePureNameWithSuffix = temp.getName()
											.substring(
													0,
													temp.getName().length()
															- suffixLen);
									//yuver01
									//Parent disk has been appended with fakeGuid, so when compare, fakeGuid need to 
									//be appended.
									//fakeGuid = _00000000-0000-0000-0000-000000000000;
									String nameX = prePureNameWithSuffix.replace(fakeGuid, "");
									String nameY = pureNameWithoutSuffix.replace(fakeGuid, ""); //Take care of IndexOutOfRange exception in some cases.
									if ((pureNameWithoutSuffix.length() <= prePureNameWithSuffix.length()
											&& pureNameWithoutSuffix.equalsIgnoreCase(prePureNameWithSuffix.substring(0, pureNameWithoutSuffix.length())))
											|| (nameX.equalsIgnoreCase(nameY))){ // we
										// found the possible parent disk
										HyperVJNI.modifyParentAbsolutePath(filevhd,
												preDisk);
										foundParent = true;
										break outloop;
	
									}
	
								}
							}
							if (!foundParent) {
								String msg = ReplicationMessage.getMonitorServiceErrorString(MonitorWebServiceErrorCode.Repository_Session_SnapShot_NO_PARENTDISK, filevhd);
								throw new Exception(msg);
							}
						}
						// we then rename the d2d file into vhd file
						if (file.getName().toLowerCase().endsWith(".d2d")) {
							if (isDiff) {
								// FIXME if isDiff, we should change it into .avhd,
								// so that HyperV can merge the code when we delete
								// the oldest snapshots
								fileExt = ".avhd";
								if(useVhdxFormat())
									fileExt = ".avhdx";
								
								File dest = new File(filePathWithoutSuffix + fakeGuid + fileExt);
								file.renameTo(dest);
								file = dest;
							} else {
								fileExt = ".vhd";
								if(useVhdxFormat())
									fileExt = ".vhdx";
								File dest = new File(filePathWithoutSuffix + fileExt);
								file.renameTo(dest);
								file = dest;
							}
						}
						
						boolean isBootable = false;
						boolean isSys = false;
						///When aDRConfigure.getBootvolume() == null, we get the boot/systemVolumeby by 
						//trying to mount vhd, avhd. 
						if(aDRConfigure.getBootvolume() == null){
							
							bootVolumes.clear();
							systemVolumes.clear();
							
							NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
							
							nativeFacade.MountVHDGetWinSysBootVol(handle,file.getAbsolutePath(), bootVolumes, systemVolumes);
							
							if(!bootVolumes.isEmpty()){
								isBootable = true;
								foundBoot = true;
							}
							
							if(!systemVolumes.isEmpty()){
								isSys = true;
								foundSys = true;
							}
							
							HyperVJNI.UnmountVHD(handle, file.getAbsolutePath());
							
						}
						
						SessionInfoForSnapshot s = new SessionInfoForSnapshot();
						s.setDiskFile(file.getAbsolutePath());
						s.setDiskSignature(diskSig);
						if(aDRConfigure.getBootvolume() != null){
							s.setBootDisk(bootVolumePath
									.endsWith(pureNameWithoutSuffix));
							boolean sysVol = systemVolumePath
									.endsWith(pureNameWithoutSuffix);
							s.setSystemDisk(sysVol);
							if(sysVol)
								foundSys = true;
						}
						else {
							s.setBootDisk(isBootable);
							s.setSystemDisk(isSys);
						}
						s.setControllerType(di.getControllerType());
						re.add(s);
					}
	
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
		
		if(!foundSys) {
			log.error("No system disk found. vmGuid: " + vmGuid + ", sessionNumber:" + sessionNumber);
			String msg = ReplicationMessage.getMonitorServiceErrorString(MonitorWebServiceErrorCode.Repository_NO_SYSTEM_DISK_FOUND, vmName, hyperVServer);
			throw new Exception(msg);
		}
		
		return re.toArray(new SessionInfoForSnapshot[0]);
	}

	/**
	 * get the D2D file path which contains the boot volume, removing the suffix
	 * 
	 * @param aDRConfigure
	 * @param rootPath
	 * @param sessionName
	 * @return
	 * @throws HAException
	 * @throws HyperVException 
	 */
	public static String getBootVolumeD2DPath(long handle,ADRConfigure aDRConfigure,
			List<DiskDestination> diskDestinations, String sessionName, List<String> getAttachedDiskImage, String hyperVServer) throws Exception {
		
		Iterator<Disk> iterator = aDRConfigure.getDisks().iterator();
		
		if(aDRConfigure.getBootvolume() == null){
			
			String fakeGuid = "_00000000-0000-0000-0000-000000000000";
			NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
			List<String> bootVolumes = new ArrayList<String>();
			List<String> systemVolume = new ArrayList<String>();
			while (iterator.hasNext()) {
				
				Disk disk = (Disk) iterator.next();
				String diskRoot =  getDiskPath(diskDestinations, disk);
				String sessionPath = getSessionPath(diskRoot,sessionName);
				String absoluteDiskPath = sessionPath + "disk" + disk.getSignature();
				
				boolean foundDiskImagePath = false;
				if(useVhdxFormat())
				{
					String absoluteDiskPathLCase = absoluteDiskPath.toLowerCase();
					ListIterator<String> listIterator = getAttachedDiskImage.listIterator();				
					while (listIterator.hasNext()) {
						String string = (String) listIterator.next();
						String diskImagePath = string.toLowerCase();
						if (diskImagePath.startsWith(absoluteDiskPathLCase)) {
							File diskImageFile = new File(string);
							if (diskImageFile.exists() && diskImageFile.isFile()) {
								foundDiskImagePath = true;
								absoluteDiskPath = string;
							}
							break;
						}
					}
				}
				if (!foundDiskImagePath) {
					String fileExt = ".d2d";
					File d2dFile = new File(absoluteDiskPath+fileExt);
					if(!d2dFile.exists() || !d2dFile.isFile()){
						fileExt = ".vhd";
						if(useVhdxFormat())
							fileExt = ".vhdx";
						d2dFile = new File(absoluteDiskPath + fileExt);
						if(!d2dFile.exists() || !d2dFile.isFile() ){
							fileExt = ".avhd";
							if(useVhdxFormat())
								fileExt = ".avhdx";
							d2dFile = new File(absoluteDiskPath + fileExt);
							if(!d2dFile.exists() || !d2dFile.isFile()){
								d2dFile = new File(absoluteDiskPath + fakeGuid + fileExt);
								if(!d2dFile.exists() || !d2dFile.isFile()){
									continue;
								}
							}
						}
					}
					
					absoluteDiskPath = d2dFile.getAbsolutePath();
				}
				bootVolumes.clear();
				systemVolume.clear();
				nativeFacade.MountVHDGetWinSysBootVol(handle,absoluteDiskPath, bootVolumes,systemVolume);
				HyperVJNI.UnmountVHD(handle, absoluteDiskPath);
				if(!bootVolumes.isEmpty()){
					return sessionPath + "disk" + disk.getSignature();
				}
			}
			String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Without_BootVolume, sessionName, hyperVServer);
			throw new Exception(msg);
			
		} else {
			
			String bootVolumeID = getBootVolumeID(aDRConfigure);
			while (iterator.hasNext()) {
				Disk disk = (Disk) iterator.next();
				SortedSet<DiskExtent> diskExtents = disk.getDiskExtents();
				Iterator<DiskExtent> iterator2 = diskExtents.iterator();
				String diskRoot =  getDiskPath(diskDestinations, disk);
				String sessionPath = getSessionPath(diskRoot,sessionName);
				while (iterator2.hasNext()) {
					DiskExtent diskExtent = (DiskExtent) iterator2.next();
					if (bootVolumeID.indexOf(diskExtent.getVolumeID()) != -1) {
						return sessionPath + "disk" + disk.getSignature(); // +".vhd";
					}
				}
			}
			String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Without_BootVolume, sessionName, hyperVServer);
			throw new Exception(msg);
		}
	}
	
	/*
	 * bootVolumes and sysVolume are out parameters
	 */
	public static void getBootAndSysVolumeD2DPath(int handle,ADRConfigure aDRConfigure,
			List<DiskDestination> diskDestinations, String sessionName,List<String> bootVolumes,String sysVolume) throws HAException{
		
		
		Iterator<Disk> iterator = aDRConfigure.getDisks().iterator();
		
		String fakeGuid = "_00000000-0000-0000-0000-000000000000";
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		List<String> tmpbootVolumes = new ArrayList<String>();
		List<String> tmpSystemVolume = new ArrayList<String>();
		while (iterator.hasNext()) {
			
			Disk disk = (Disk) iterator.next();
			String diskRoot =  getDiskPath(diskDestinations, disk);
			String sessionPath = getSessionPath(diskRoot,sessionName);
			String absoluteDiskPath = sessionPath + "disk" + disk.getSignature();
			String fileExt = ".d2d";
			File d2dFile = new File(absoluteDiskPath+fileExt);
			if(!d2dFile.exists() || !d2dFile.isFile()){
				fileExt = ".vhd";
				if(useVhdxFormat())
					fileExt = ".vhdx";
				d2dFile = new File(absoluteDiskPath + fileExt);
				if(!d2dFile.exists() || !d2dFile.isFile() ){
					fileExt = ".avhd";
					if(useVhdxFormat())
						fileExt = ".avhdx";
					d2dFile = new File(absoluteDiskPath + fileExt);
					if(!d2dFile.exists() || !d2dFile.isFile()){
						d2dFile = new File(absoluteDiskPath + fakeGuid + fileExt);
						if(!d2dFile.exists() || !d2dFile.isFile()){
							continue;
						}
					}
				}
			}
			
			absoluteDiskPath = d2dFile.getAbsolutePath();
			bootVolumes.clear();
			tmpSystemVolume.clear();
			nativeFacade.MountVHDGetWinSysBootVol(handle,absoluteDiskPath, tmpbootVolumes, tmpSystemVolume);
			if(!bootVolumes.isEmpty()){
				bootVolumes.add(sessionPath + "disk" + disk.getSignature());
			}
			if(!tmpSystemVolume.isEmpty()){
				sysVolume = tmpSystemVolume.get(0);
			}
		}
		
	}
	
	public static String getSystemVolumeD2DPath(ADRConfigure aDRConfigure,
			List<DiskDestination> diskDestinations, String sessionName, String hyperVServer) throws Exception {
		
			
			String bootVolumeID = getSystemVolumeID(aDRConfigure);
			Iterator<Disk> iterator = aDRConfigure.getDisks().iterator();
			while (iterator.hasNext()) {
				Disk disk = (Disk) iterator.next();
				String diskRoot =  getDiskPath(diskDestinations, disk);
				String sessionPath = getSessionPath(diskRoot,sessionName);
				SortedSet<DiskExtent> diskExtents = disk.getDiskExtents();
				Iterator<DiskExtent> iterator2 = diskExtents.iterator();
				while (iterator2.hasNext()) {
					DiskExtent diskExtent = (DiskExtent) iterator2.next();
					if (bootVolumeID.indexOf(diskExtent.getVolumeID()) != -1) {
						return sessionPath + "disk" + disk.getSignature(); // +".vhd";
					}
				}
			}
			String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_HYPERV_Without_BootVolume, sessionName, hyperVServer);
			throw new Exception(msg);

	}

	/**
	 * get windows dir in GUID format from adrconfigure.xml and mounted VHD, for
	 * example \\?\GLOBALROOT\Device\HarddiskVolume22\windows\
	 * 
	 * @param aDRConfigure
	 * @param bootVolumeGuid
	 *            for example \\?\GLOBALROOT\Device\HarddiskVolume22
	 * @return
	 * @throws HAException
	 */
	public static String getBootVolumeWindowsPath(ADRConfigure aDRConfigure,
			String bootVolumeGuid, String sessionName, String nodeName) throws Exception {
		// windowsDir in adrconfigure c:\windows\system32
		String windowsDir = aDRConfigure.getBootvolume().getWindowsDir();
		if (bootVolumeGuid == null || bootVolumeGuid.isEmpty())
		{
			String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_FAILED_CREATE_BOOTABLE_SNAHSOT, sessionName, nodeName);
			throw new Exception(msg);
		}

		int len = bootVolumeGuid.length();

		if (bootVolumeGuid.charAt(len - 1) != '\\')
			bootVolumeGuid += "\\";

		String guidPath = bootVolumeGuid; // aDRConfigure.getBootvolume().getGuidPath();
		boolean error = false;
		if (windowsDir != null) {
			int indexOf = windowsDir.lastIndexOf("\\system32");
			if (indexOf != -1) {
				windowsDir = windowsDir.substring(0, indexOf);
				indexOf = windowsDir.indexOf(":\\");
				if (indexOf != -1 && windowsDir.length() > 2) {
					windowsDir = windowsDir.substring(indexOf + 2);
				} else
					error = true;

			} else
				error = true;
		} else
			error = true;
		if (error) {
			String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_FAILED_CREATE_BOOTABLE_SNAHSOT, sessionName, nodeName);
			throw new Exception(msg);
		}
		String result = guidPath + windowsDir;

		return result;
	}

	/**
	 * 
	 * @param aDRConfigure
	 * @return
	 */

	private static String getBootVolumeID(ADRConfigure aDRConfigure) {
		String bootGuidPath = aDRConfigure.getBootvolume().getGuidPath();
		List<Volume> volumes = aDRConfigure.getVolumes();
		Iterator<Volume> iterator3 = volumes.iterator();
		String bootVolumeID = "";
		while (iterator3.hasNext()) {
			Volume volume = (Volume) iterator3.next();
			if (volume.getGuidPath().equals(bootGuidPath))
				bootVolumeID = volume.getVolumeID();
		}
		return bootVolumeID;
	}

	private static String getSystemVolumeID(ADRConfigure aDRConfigure) {
		String bootGuidPath = aDRConfigure.getSystemVolume().getGuidPath();
		List<Volume> volumes = aDRConfigure.getVolumes();
		Iterator<Volume> iterator3 = volumes.iterator();
		String bootVolumeID = "";
		while (iterator3.hasNext()) {
			Volume volume = (Volume) iterator3.next();
			if (volume.getGuidPath().equals(bootGuidPath))
				bootVolumeID = volume.getVolumeID();
		}
		return bootVolumeID;
	}

	public static long getBootVolumeOffset(ADRConfigure aDRConfigure)
			throws HAException {
		String bootVolumeID = getBootVolumeID(aDRConfigure);
		Iterator<Disk> iterator = aDRConfigure.getDisks().iterator();
		while (iterator.hasNext()) {
			Disk disk = (Disk) iterator.next();
			SortedSet<DiskExtent> diskExtents = disk.getDiskExtents();
			Iterator<DiskExtent> iterator2 = diskExtents.iterator();

			while (iterator2.hasNext()) {

				DiskExtent diskExtent = (DiskExtent) iterator2.next();
				if (bootVolumeID.indexOf(diskExtent.getVolumeID()) != -1) {
					return diskExtent.getOffset();
				}
			}
		}
		throw new HAException(
				"no boot disk extent for boot volume",
				MonitorWebServiceErrorCode.Repository_BootVolume_Windows_Dir_Error);

	}
	
	public static long getSystemVolumeOffset(ADRConfigure aDRConfigure)
			throws HAException {
		String systemVolumeID = getSystemVolumeID(aDRConfigure);
		Iterator<Disk> iterator = aDRConfigure.getDisks().iterator();
		while (iterator.hasNext()) {
			Disk disk = (Disk) iterator.next();
			SortedSet<DiskExtent> diskExtents = disk.getDiskExtents();
			Iterator<DiskExtent> iterator2 = diskExtents.iterator();

			while (iterator2.hasNext()) {

				DiskExtent diskExtent = (DiskExtent) iterator2.next();
				if (systemVolumeID.indexOf(diskExtent.getVolumeID()) != -1) {
					return diskExtent.getOffset();
				}
			}
		}
		throw new HAException(
				"no system disk extent for system volume",
				MonitorWebServiceErrorCode.Repository_BootVolume_Windows_Dir_Error);
	}
	
	private static String getDiskPath(List<DiskDestination> diskDestinations, Disk disk) throws HAException{
		if(diskDestinations == null || diskDestinations.size() == 0)
			throw new HAException(
					"No disk found in disk destination",
					MonitorWebServiceErrorCode.HyperV_Operation_Error);
		
		for (DiskDestination diskDestination : diskDestinations) {
			if(diskDestination.getDisk().getSignature().equals(disk.getSignature())){
				String path = diskDestination.getStorage().getName();
				if(!path.endsWith("\\")){
					path += "\\";
				}
				return path;
			}
		}
		return null;
	}
	
}
