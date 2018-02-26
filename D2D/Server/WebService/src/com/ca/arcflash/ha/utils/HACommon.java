package com.ca.arcflash.ha.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.bind.JAXB;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.ADRConfigureFactory;
import com.ca.arcflash.failover.model.ADRConfigureUtil;
import com.ca.arcflash.failover.model.BackupInfo;
import com.ca.arcflash.failover.model.BackupInfoFactory;
import com.ca.arcflash.failover.model.Disk;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.TransServerReplicaRoot;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.model.VirtualMachineInfo;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.model.manager.CustomSessionPasswordManager;
import com.ca.arcflash.ha.model.manager.VMInfomationModelManager;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.ha.webservice.MonitorWebClientManager;
import com.ca.arcflash.job.failover.FailoverJob;
import com.ca.arcflash.jobqueue.JobQueueFactory;
import com.ca.arcflash.jobscript.base.JobType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.service.jni.model.JActLogDetails;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.BaseVSpherePolicy;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.jni.FileItemModel;
import com.ca.arcflash.webservice.jni.HyperVRepParameterModel;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.WSJNIException;
import com.ca.arcflash.webservice.replication.BackupDestinationInfo;
import com.ca.arcflash.webservice.replication.BaseReplicationCommand;
import com.ca.arcflash.webservice.replication.ReplicationMessage;
import com.ca.arcflash.webservice.replication.SessionInfo;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;

public class HACommon {
	private static final Logger log = Logger.getLogger(HACommon.class);
	
	public static final int REPLICATE_DISKSIZECHANE = -2;
	public static final int REPLICATE_NOSESSIONS = -1;
	public static final int REPLICATE_SUCCESS = 0;
	public static final int REPLICATE_FAILURE = 1;
	public static final int REPLICATE_CANCEL = 2;
	public static final int REPLICATE_HOTADD_SUCCESS = 3;
	public static final int REPLICATE_HOTADD_FAILURE = 4;
	public static final int PASSWORD_FILE_DAMAGE = 5;
	public static final int REPLICATE_SKIPPED = 6; // in case of auto conversion is paused
	public static final int REPLICATE_LICENSE_FAILURE = 7; // in case of auto conversion is paused
	
	public static final int STORAGE_BLOCK_SIZE_1_MB = 1; // 1MB
	public static final int STORAGE_BLOCK_SIZE_2_MB = 2; // 1MB
	public static final int STORAGE_BLOCK_SIZE_4_MB = 4; // 1MB
	public static final int STORAGE_BLOCK_SIZE_8_MB = 8; // 1MB
	
	public static final long VIRTUAL_DISK_MAX_SIZE_256_GB = (long)256*1024*1024*1024; //in byte
	public static final long VIRTUAL_DISK_MAX_SIZE_512_GB = (long)512*1024*1024*1024; //in byte
	public static final long VIRTUAL_DISK_MAX_SIZE_1024_GB = (long)1024*1024*1024*1024; //in byte
	public static final long VIRTUAL_DISK_MAX_SIZE_2024_GB = (long)2024*1024*1024*1024; //in byte
	
	public static final int DYNAMIC_DISK_TYPE = 2; //dynamic disk type from adrconfigure
	public static final int MBR_DISK_TYPE = 1;     //mbr disk type from adrconfigure
	
	
	public static String getSnapshot(int handle, String sessionName,
			String vmGuid) throws HAException {
		try {
			String snapeGUID = "";
			Map<String, String> getVmSnapshot = null;
			try {
				getVmSnapshot = HyperVJNI.GetVmSnapshots(handle, vmGuid);
				if (getVmSnapshot == null) {
					throw new HAException("No snapshot found",
							MonitorWebServiceErrorCode.HyperV_Operation_Error);
				}
			} finally {
				try {
					HyperVJNI.CloseHypervHandle(handle);
				} catch (HyperVException hyperVe) {
					log.debug(hyperVe.getMessage());
				}
			}
			Set<Entry<String, String>> entrySet = getVmSnapshot.entrySet();
			for (Entry<String, String> e : entrySet) {
				String guid = e.getKey();
				String name = e.getValue();
				if (name.equals(sessionName)) {
					snapeGUID = guid;
					break;
				}
			}
			if (snapeGUID == null || snapeGUID.isEmpty()) {
				String error = "No snapshot found for VM:" + vmGuid
						+ " Session: " + sessionName;
				log.debug(error);
				throw new HAException(error,
						MonitorWebServiceErrorCode.HyperV_Operation_Error);
			}
			return snapeGUID;
		} catch (HyperVException he) {
			throw new HAException(he.getMessage() + "-" + he.getErrorCode(),
					MonitorWebServiceErrorCode.HyperV_Operation_Error);
		}

	}
	
	public static VMSnapshotsInfo[] getAndPurgeVMSnapshotsHyperV(String lastRepDest,String afGuid,
			VirtualMachineInfo vmInfo) {
		return getAndPurgeVMSnapshotsHyperV(lastRepDest, afGuid, vmInfo, true);
	}

	public static VMSnapshotsInfo[] getAndPurgeVMSnapshotsHyperV(String lastRepDest,String afGuid,
			VirtualMachineInfo vmInfo, boolean isCheckError) {
		
		VMInfomationModelManager manager = getSnapshotModeManager(lastRepDest, afGuid);
		
		if(manager == null)
			return new VMSnapshotsInfo[0];
		
		SortedSet<VMSnapshotsInfo> snapshots = manager.getSnapshots(afGuid);
		
		if (vmInfo == null
				|| vmInfo.getType() != VirtualMachineInfo.VIRTUAL_TYPE_HYPERV) {
			if (snapshots != null && !snapshots.isEmpty()) {
				manager.clearSnapshots(afGuid);
			}
			return new VMSnapshotsInfo[0];
		}

		long handle = 0;
		try {
			handle = HyperVJNI.OpenHypervHandle("", "", "");
		} catch (HyperVException he) {
			throw AxisFault.fromAxisFault(
					"Failed to open hyperv manger handle",
					MonitorWebServiceErrorCode.HyperV_Operation_Error);
		}
		try {
			SortedSet<VMSnapshotsInfo> lastSnapshotForD2DSession = new TreeSet<VMSnapshotsInfo>();
			try {
				lastSnapshotForD2DSession = getSnapshotForD2DSession(
						lastRepDest,handle, afGuid, true, isCheckError);
			} catch (HAException ha) {
				if (ha.getCode() != null
						&& ha.getCode()
								.equals(MonitorWebServiceErrorCode.Repository_Session_SnapShot_WITHOUT_VM)) {
					;
				} else
					throw AxisFault
							.fromAxisFault(ha.getMessage(), ha.getCode());
			}
			if (snapshots.size() != lastSnapshotForD2DSession.size())
				manager.putNewVM(vmInfo,lastSnapshotForD2DSession);
			
			//remove the DR snapshot
			snapshots.clear();
			for (VMSnapshotsInfo vmSnapshotsInfo : lastSnapshotForD2DSession) {
				if(vmSnapshotsInfo.getTimestamp() > 0) {
					vmSnapshotsInfo.setTimeZoneOffset(ServiceUtils
							.getServerTimeZoneOffsetByDate(new Date(
									vmSnapshotsInfo.getTimestamp())));
				}
				if(!vmSnapshotsInfo.isDRSnapshot()){
					snapshots.add(vmSnapshotsInfo);
				}
			}
			//snapshots = lastSnapshotForD2DSession;

			VMSnapshotsInfo[] array = snapshots.toArray(new VMSnapshotsInfo[0]);
			if (log.isDebugEnabled()) {
				String string = Arrays.toString(array);
				log.debug("getVMSnapshots end -> " + string);
			}
			return array;
		} finally {
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException hyperVe) {
				log.debug(hyperVe.getMessage());
			}
		}
	}

	/**
	 * return the VM's snapshots for replication consideration: user deletes the
	 * last snapshot.
	 * 
	 * @param handle
	 * @param afguid
	 * @return
	 * @throws HAException
	 */
	
	public static SortedSet<VMSnapshotsInfo> getSnapshotForD2DSession(
			String lastRepDest, long handle, String afguid,
			boolean allowDeleteLast) throws HAException {

			return getSnapshotForD2DSession(lastRepDest, handle, afguid, allowDeleteLast, true);
		
	}
	
	/**
	 * return the VM's snapshots for replication consideration: user deletes the
	 * last snapshot.
	 * 
	 * @param handle
	 * @param afguid
	 * @return
	 * @throws HAException
	 */
	
	public static SortedSet<VMSnapshotsInfo> getSnapshotForD2DSession(
			String lastRepDest, long handle, String afguid,
			boolean allowDeleteLast, boolean isCheckError) throws HAException {

		VMInfomationModelManager manager = getSnapshotModeManager(lastRepDest, afguid);

		return getSnapshotForD2DSession(manager, handle, afguid, allowDeleteLast, isCheckError);
		
	}

	public static SortedSet<VMSnapshotsInfo> getSnapshotForD2DSessionByVMGUID(
			String vmGUID, long handle, String afguid,
			boolean allowDeleteLast) throws HAException {

		VMInfomationModelManager manager = getSnapshotModeManagerByVMGUID(vmGUID);

		return getSnapshotForD2DSession(manager, handle, afguid, allowDeleteLast);
	}
	
	public static SortedSet<VMSnapshotsInfo> getSnapshotForD2DSession(
			VMInfomationModelManager manager, long handle, String afguid,
			boolean allowDeleteLast) throws HAException {
		return getSnapshotForD2DSession(manager, handle, afguid, allowDeleteLast, true);
	}
	public static SortedSet<VMSnapshotsInfo> getSnapshotForD2DSession(
			VMInfomationModelManager manager, long handle, String afguid,
			boolean allowDeleteLast, boolean isCheckError) throws HAException {
		
		if(manager == null){
			log.error("manager is null");
			throw new HAException("manager is null","1");
		}

		SortedSet<VMSnapshotsInfo> re_snapshots = new TreeSet<VMSnapshotsInfo>(
				new Comparator<VMSnapshotsInfo>() {

					@Override
					public int compare(VMSnapshotsInfo o1, VMSnapshotsInfo o2) {
						if (o1.getSnapNo() < o2.getSnapNo())
							return 1;
						else if (o1.getSnapNo() == o2.getSnapNo())
							return 0;
						else
							return -1;
					}
				});

		VirtualMachineInfo internalVMInfo = manager.getInternalVMInfo(afguid);
		if (internalVMInfo == null || internalVMInfo.getType() != 0) {
			return re_snapshots;
		}
		SortedSet<VMSnapshotsInfo> snapshots = manager.getSnapshots(afguid);

		for (VMSnapshotsInfo t : snapshots) {
			re_snapshots.add(t);
		}
		if (re_snapshots.isEmpty())
			return re_snapshots;

		//If only data snapshot is taken, remove this snapshot
		List<VMSnapshotsInfo> incompelteSnapshots = new ArrayList<VMSnapshotsInfo>();
		for (VMSnapshotsInfo vmSnapshotsInfo : re_snapshots) {
			if(StringUtil.isEmptyOrNull(vmSnapshotsInfo.getBootableSnapGuid())){
				log.info("VM snapshot for session " + vmSnapshotsInfo.getSessionName() + " has no corresponding bootable snapshot.");
				incompelteSnapshots.add(vmSnapshotsInfo);
			}
		}
		
		if(incompelteSnapshots.size() > 0){
			re_snapshots.removeAll(incompelteSnapshots);
			
			log.info("Clean all incompelte snapshots in xml.");
			manager.replaceSnapshots(internalVMInfo, re_snapshots);
		}
		//
		
		Map<String, String> getVmSnapshot = null;
		try {
			getVmSnapshot = HyperVJNI.GetVmSnapshots(handle, internalVMInfo
					.getVmGUID());
		} catch (HyperVException he) {
			if (he.getMessage() != null
					&& he.getMessage().startsWith(HAException.INVALID_VMGUID))
				throw new HAException(
						he.getMessage() + "-" + he.getErrorCode(),
						MonitorWebServiceErrorCode.Repository_Session_SnapShot_WITHOUT_VM);
			else
				throw new HAException(
						he.getMessage() + "-" + he.getErrorCode(),
						MonitorWebServiceErrorCode.HyperV_Operation_Error);
		}
		
		//delete incomplete snapshot
		for (VMSnapshotsInfo vmSnapshotsInfo : incompelteSnapshots) {
			if(getVmSnapshot == null){
				break;
			}
			if(getVmSnapshot.keySet().contains(vmSnapshotsInfo.getSnapGuid())){
				try {
					if(isCheckError) {
						log.warn("Delete VM snapshot " + vmSnapshotsInfo.getSnapGuid() +" for session " + vmSnapshotsInfo.getSessionName());
						HyperVJNI.DeleteVmSnapshot(handle, internalVMInfo.getVmGUID(), 
								vmSnapshotsInfo.getSnapGuid());
					}
				} catch (HyperVException e) {
				}
			}
		}
		
		VMSnapshotsInfo first2 = re_snapshots.first();
		if (getVmSnapshot == null && !allowDeleteLast) {
			throw new HAException(
					first2.getSessionName(),
					MonitorWebServiceErrorCode.Repository_Session_SnapShot_LAST_SNAPSHOT_LOST);
		}
		
		List<String> snapshotList = new LinkedList<String>();
		Set<String> snapshotGuids = getVmSnapshot.keySet(); 
		List<VMSnapshotsInfo> tmpVMList = new LinkedList<VMSnapshotsInfo>();
		for (VMSnapshotsInfo vm : re_snapshots) {
			if (snapshotGuids.contains(vm.getSnapGuid()) && snapshotGuids.contains(vm.getBootableSnapGuid())) {
				snapshotList.add(vm.getSnapGuid());
				snapshotList.add(vm.getBootableSnapGuid());
			}else{
				tmpVMList.add(vm);
			}
		}
		
		if(tmpVMList.size() > 0){
			log.info("total snapshots in xml is bigger than real snapshot count.Remove unexisted snapshot from xml.");
			re_snapshots.removeAll(tmpVMList);
			manager.replaceSnapshots(internalVMInfo, re_snapshots);
		}
		
		if(snapshotList.size() < snapshotGuids.size()){
			//If snapshot is not in vmsnapshotmodel.xml
			//delete these snapshot from vm.
			//This situation will happen if snapshot and bootable snapshot is taken but Tomcat poweroff or d2d service stops
			log.info("real snapshot count is bigger than intersection of vmsnapshotmodel and real snapshot count");
			if(snapshotList.size() > 0){
				snapshotGuids.removeAll(snapshotList);
				for (String tmp1 : snapshotGuids) {
					try {
						if(isCheckError) {
							log.warn("Delete VM snapshot " + tmp1);
							HyperVJNI.DeleteVmSnapshot(handle, internalVMInfo.getVmGUID(), tmp1);
						}
					} catch (HyperVException e) {
						log.error(e.getMessage(),e);
					}
				}
			}
		}
		
		return re_snapshots;
		
	}
	
	/**
	 * return the VM's snapshots for failover consideration: user deletes the
	 * last snapshot.
	 * 
	 * @param handle
	 * @param afguid
	 * @return
	 * @throws HAException
	 */
	public static SortedSet<VMSnapshotsInfo> getLastSnapshotForD2DSession(
			String lastRepDest,long handle, String afguid, boolean allowDeleteLast)
			throws HAException {
		VMInfomationModelManager manager = getSnapshotModeManager(lastRepDest, afguid);
		SortedSet<VMSnapshotsInfo> re_snapshots = new TreeSet<VMSnapshotsInfo>(
				new Comparator<VMSnapshotsInfo>() {

					@Override
					public int compare(VMSnapshotsInfo o1, VMSnapshotsInfo o2) {
						if (o1.getSnapNo() < o2.getSnapNo())
							return 1;
						else if (o1.getSnapNo() == o2.getSnapNo())
							return 0;
						else
							return -1;
					}
				});
		
		if (manager == null) {
			return re_snapshots;
		}
		
		VirtualMachineInfo vmInfo = manager.getInternalVMInfo(afguid);
		if (vmInfo == null || vmInfo.getType() != 0) {
			return re_snapshots;
		}
		VirtualMachineInfo internalVMInfo = manager.getInternalVMInfo(afguid);

		if (internalVMInfo == null || internalVMInfo.getType() != 0
				|| !internalVMInfo.getVmGUID().equals(vmInfo.getVmGUID())) {
			return re_snapshots;
		}
		SortedSet<VMSnapshotsInfo> snapshots = manager.getSnapshots(afguid);

		for (VMSnapshotsInfo t : snapshots) {
			re_snapshots.add(t);
		}
		if (re_snapshots.isEmpty())
			return re_snapshots;

		ArrayList<VMSnapshotsInfo> temp = new ArrayList<VMSnapshotsInfo>();

		Map<String, String> getVmSnapshot = null;
		try {
			getVmSnapshot = HyperVJNI
					.GetVmSnapshots(handle, vmInfo.getVmGUID());
		} catch (HyperVException he) {
			throw new HAException(he.getMessage() + "-" + he.getErrorCode(),
					MonitorWebServiceErrorCode.HyperV_Operation_Error);
		}
		VMSnapshotsInfo first2 = re_snapshots.first();
		if (getVmSnapshot == null && !allowDeleteLast) {
			throw new HAException(
					first2.getSessionName(),
					MonitorWebServiceErrorCode.Repository_Session_SnapShot_LAST_SNAPSHOT_LOST);
		}
		Iterator<VMSnapshotsInfo> iterator = re_snapshots.iterator();
		boolean first = true;
		while (iterator.hasNext()) {
			VMSnapshotsInfo vmSnapshotsInfo = (VMSnapshotsInfo) iterator.next();
			boolean found = false;
			if (getVmSnapshot != null) {
				Set<Entry<String, String>> entrySet = getVmSnapshot.entrySet();
				for (Entry<String, String> e : entrySet) {
					String guid = e.getKey();
					if (guid.equals(vmSnapshotsInfo.getSnapGuid())) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				if (first && !allowDeleteLast) {
					throw new HAException(
							"user deletes the last session between the replication job starts and take snapshot",
							MonitorWebServiceErrorCode.Repository_Session_SnapShot_LAST_SNAPSHOT_LOST);
				}
				temp.add(vmSnapshotsInfo);
			}
			first = false;
		}
		if (!temp.isEmpty()) {
			re_snapshots.removeAll(temp);
		}
		return re_snapshots;
	}

	public static VMSnapshotsInfo[] getVMSnapshotsHyperV(String lastRepDest,String afGuid,
			VirtualMachineInfo vmInfo) {
		
		if (vmInfo == null || vmInfo.getType() != VirtualMachineInfo.VIRTUAL_TYPE_HYPERV)
			return new VMSnapshotsInfo[0];

		long handle = 0;
		try {
			handle = HyperVJNI.OpenHypervHandle("", "", "");
		} catch (HyperVException he) {
			throw AxisFault.fromAxisFault(
					"Failed to open hyperv manger handle",
					MonitorWebServiceErrorCode.HyperV_Operation_Error);
		}

		try {
			SortedSet<VMSnapshotsInfo> lastSnapshotForD2DSession = null;
			try {
				lastSnapshotForD2DSession = getCompleteSnapshotForD2DSession(lastRepDest,handle, afGuid);
			} catch (HAException ha) {
				if (ha.getCode() != null
						&& ha.getCode()
								.equals(MonitorWebServiceErrorCode.Repository_Session_SnapShot_WITHOUT_VM)) {
					;
				} else
					throw AxisFault
							.fromAxisFault(ha.getMessage(), ha.getCode());
			}
			
			if (lastSnapshotForD2DSession == null)
				return new VMSnapshotsInfo[0];

			SortedSet<VMSnapshotsInfo> snapshots = new TreeSet<VMSnapshotsInfo>();
			for (VMSnapshotsInfo vmSnapshotsInfo : lastSnapshotForD2DSession) {
				if(vmSnapshotsInfo.getTimestamp() > 0) {
					vmSnapshotsInfo.setTimeZoneOffset(ServiceUtils
							.getServerTimeZoneOffsetByDate(new Date(
									vmSnapshotsInfo.getTimestamp())));
				}
				if(!vmSnapshotsInfo.isDRSnapshot()){
					snapshots.add(vmSnapshotsInfo);
				}
			}

			VMSnapshotsInfo[] array = snapshots.toArray(new VMSnapshotsInfo[0]);
			if (log.isDebugEnabled()) {
				String string = Arrays.toString(array);
				log.debug("getVMSnapshots end -> " + string);
			}
			return array;
		} finally {
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException hyperVe) {
				log.debug(hyperVe.getMessage());
			}
		}
	}
	private static SortedSet<VMSnapshotsInfo> getCompleteSnapshotForD2DSession(
			String lastRepDest,long handle, String afguid) throws HAException {
		
		VMInfomationModelManager manager = getSnapshotModeManager(lastRepDest, afguid);
		
		SortedSet<VMSnapshotsInfo> re_snapshots = new TreeSet<VMSnapshotsInfo>(
				new Comparator<VMSnapshotsInfo>() {

					@Override
					public int compare(VMSnapshotsInfo o1, VMSnapshotsInfo o2) {
						if (o1.getSnapNo() < o2.getSnapNo())
							return 1;
						else if (o1.getSnapNo() == o2.getSnapNo())
							return 0;
						else
							return -1;
					}
				});
		
		if (manager == null)
			return re_snapshots;
		
		VirtualMachineInfo vmInfo = manager.getInternalVMInfo(afguid);
		if (vmInfo == null || vmInfo.getType() != 0)
			return re_snapshots;

		VirtualMachineInfo internalVMInfo = manager.getInternalVMInfo(afguid);

		if (internalVMInfo == null || internalVMInfo.getType() != 0
				|| !internalVMInfo.getVmGUID().equals(vmInfo.getVmGUID()))
			return re_snapshots;

		SortedSet<VMSnapshotsInfo> snapshots = manager.getSnapshots(afguid);

		for (VMSnapshotsInfo t : snapshots) {
			re_snapshots.add(t);
		}
		if (re_snapshots.isEmpty())
			return re_snapshots;

		List<VMSnapshotsInfo> incompelteSnapshots = new ArrayList<VMSnapshotsInfo>();
		for (VMSnapshotsInfo vmSnapshotsInfo : re_snapshots) {
			if(StringUtil.isEmptyOrNull(vmSnapshotsInfo.getBootableSnapGuid())){
				log.info("VM snapshot for session " + vmSnapshotsInfo.getSessionName() + " has no corresponding bootable snapshot.");
				incompelteSnapshots.add(vmSnapshotsInfo);
			}
		}
		
		if(incompelteSnapshots.size() > 0){
			re_snapshots.removeAll(incompelteSnapshots);
		}
		
		Map<String, String> getVmSnapshot = null;
		try {
			getVmSnapshot = HyperVJNI.GetVmSnapshots(handle, internalVMInfo.getVmGUID());
		} catch (HyperVException he) {
			if (he.getMessage() != null
					&& he.getMessage().startsWith(HAException.INVALID_VMGUID))
				throw new HAException(
						he.getMessage() + "-" + he.getErrorCode(),
						MonitorWebServiceErrorCode.Repository_Session_SnapShot_WITHOUT_VM);
			else
				throw new HAException(
						he.getMessage() + "-" + he.getErrorCode(),
						MonitorWebServiceErrorCode.HyperV_Operation_Error);
		}

		Set<String> snapshotGuids = getVmSnapshot.keySet(); 
		List<VMSnapshotsInfo> tmpVMList = new LinkedList<VMSnapshotsInfo>();
		for (VMSnapshotsInfo vm : re_snapshots) {
			if (snapshotGuids.contains(vm.getSnapGuid()) && snapshotGuids.contains(vm.getBootableSnapGuid())) {
				;
			}else{
				tmpVMList.add(vm);
			}
		}
		
		if(tmpVMList.size() > 0){
			re_snapshots.removeAll(tmpVMList);
		}

		return re_snapshots;
	}

	/**
	 * 
	 * @param handle
	 * @param sessionGuid
	 * @param vmGuid
	 * @return
	 * @throws HAException
	 */
	public static String testSnapshot(String lastRepDest,long handle, String afguid,
			String sessionGuid, String vmGuid) throws Exception {
		
		VMInfomationModelManager manager = getSnapshotModeManager(lastRepDest, afguid);
		
		if(manager == null)
			return "";
		
		VirtualMachineInfo vmInfo = manager.getInternalVMInfo(afguid);
		if (vmInfo != null && vmInfo.getType() != 0) {
			return "";
		}
		SortedSet<VMSnapshotsInfo> snapshots = manager.getSnapshots(afguid);

		for (VMSnapshotsInfo t : snapshots) {
			if (t.getSessionGuid().equals(sessionGuid)) {
				try {
					Map<String, String> getVmSnapshot = HyperVJNI
							.GetVmSnapshots(handle, vmGuid);
					if (getVmSnapshot == null) {
						return "";
					}
					Set<Entry<String, String>> entrySet = getVmSnapshot
							.entrySet();
					for (Entry<String, String> e : entrySet) {
						String guid = e.getKey();
						if (guid.equals(t.getSnapGuid())) {
							return guid;
						}
					}
				} catch (HyperVException he) {
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_GET_SNAPSHOT, vmInfo.getVmName());
					throw new Exception(msg);
				}
			}
		}
		return "";
	}

	public static FailoverJobScript getFailoverJobScriptObject(String afGuid) {
		FailoverJob job = getFailoverJob(afGuid);
		if(job != null) {
			return job.getJobScript();
		}
		
		return null;
	}
	
	public static FailoverJob getFailoverJob(String afGuid) {
		synchronized (JobQueueFactory.getDefaultJobQueue()) {
			for (FailoverJob job : JobQueueFactory.getDefaultJobQueue()
					.findByJobType(JobType.Failover).toArray(new FailoverJob[] {})) {
				if (job.getJobScript() != null) {
					FailoverJobScript temp = job.getJobScript();
					if (temp.getAFGuid().equals(afGuid)) {
						return job;
					}
				}
			}
		}
		
		return null;
	}

	public static VMSnapshotsInfo getSnapshotForD2DSession(String lastRepDest,long handle,
			String afguid, String sessionGuid) throws HAException {
		
	VMInfomationModelManager manager = getSnapshotModeManager(lastRepDest, afguid);

		VirtualMachineInfo vmInfo = manager.getInternalVMInfo(afguid);
		if (vmInfo == null || vmInfo.getType() != 0) {
			log.error("VirtualMachineInfo is null. afguid: " + afguid);
			return null;
		}
		
		SortedSet<VMSnapshotsInfo> snapshots = manager.getSnapshots(afguid);

		Iterator<VMSnapshotsInfo> iterator = snapshots.iterator();
		while (iterator.hasNext()) {
			VMSnapshotsInfo vmSnapshotsInfo = (VMSnapshotsInfo) iterator.next();
			if (vmSnapshotsInfo.getSessionGuid().equals(sessionGuid)) {
				try {
					Map<String, String> getVmSnapshot = HyperVJNI
							.GetVmSnapshots(handle, vmInfo.getVmGUID());
					if (getVmSnapshot == null) {
						return null;
					}
					Set<Entry<String, String>> entrySet = getVmSnapshot
							.entrySet();
					for (Entry<String, String> e : entrySet) {
						String guid = e.getKey();
						if (guid.equals(vmSnapshotsInfo.getSnapGuid())) {
							return vmSnapshotsInfo;
						}
					}

				} catch (HyperVException he) {
					throw new HAException(he.getMessage() + "-"
							+ he.getErrorCode(),
							MonitorWebServiceErrorCode.HyperV_Operation_Error);
				}
			}
		}

		return null;
	}

	public static void dealWithGPT(ADRConfigure aDRConfigure)
			throws HAException {
		SortedSet<Disk> disks2 = aDRConfigure.getDisks();
		dealWithGPT(disks2);
	}

	public static void dealWithGPT(Collection<Disk> disks2) throws HAException {
		for (Disk di : disks2) {
			String diskSig = di.getSignature();
			if (StringUtil.isEmptyOrNull(diskSig) || "2".equals(di.getPartitionType())) {
				if (!StringUtil.isEmptyOrNull(di.getDiskGuid())) {
					try {
						String diskGuid = di.getDiskGuid();
						if (!diskGuid.startsWith("{"))
							diskGuid = "{" + diskGuid;
						if (!diskGuid.endsWith("}"))
							diskGuid = diskGuid + "}";
						diskSig = ""
								+ WSJNI.getDiskSignatureFromGPTGuid(diskGuid);
						if (log.isDebugEnabled())
							log.debug("caculating disk signature for disk guid:"
									+ diskSig + "->" + diskGuid);
						di.setSignature(diskSig);
					} catch (WSJNIException e) {
						throw new HAException(
								"Failed to get disk signature from GUID",
								MonitorWebServiceErrorCode.ADRConfigure_Parser);
					}

				} else {
					throw new HAException("null disksignature and disk GUID",
							MonitorWebServiceErrorCode.ADRConfigure_Parser);
				}
			}
		}
	}
	
	public static void removeDiskWihoutFile(SortedSet<Disk> disks, File session) {
		ArrayList<Disk> unexistedDisks = new ArrayList<Disk>();
		for (Disk d : disks) {
			String sig = d.getSignature();
			String fileName = getVHDFileName(session,sig);
			File d2d = new File(fileName);
			if (!d2d.exists()) {
				unexistedDisks.add(d);
			}
		}
		disks.removeAll(unexistedDisks);
	}
	
	public static String getVHDFileName(File session, String signature) {

		return session.getAbsolutePath() + "\\disk" + signature + ".D2D";
	}
	
	
	public static ADRConfigure getAdrConfigure(SessionInfo session)throws FileNotFoundException{
		
		ADRConfigure adrConfigure = getAdrConfigure(session.getSessionFolder());
		
		return adrConfigure;
		
	}
	
	public static ADRConfigure getAdrConfigure(String sessionPath) throws FileNotFoundException {
		
		if(!sessionPath.endsWith("\\")){
			sessionPath += "\\";
		}
		
		if(!new File(sessionPath).canWrite()){
			log.error("The session path "+ sessionPath + " is not writable.");
			throw new FileNotFoundException();
		}
		
		String adrConfigXML = sessionPath + CommonUtil.ADRCONFIG_XML_FILE;

		boolean isPartialAdrconfigure = false;
		File f = new File(adrConfigXML);
		if(!f.exists()){
			log.warn("adrconfigure File \"" + adrConfigXML + "\" does not exist.");
			adrConfigXML = sessionPath + CommonUtil.PARTIAL_ADRCONFIG_File  ;
			isPartialAdrconfigure = true;
		}
		
		ADRConfigure adrConfigure = null;
		try {
			
			adrConfigure = ADRConfigureFactory.parseADRConfigureXML(adrConfigXML);
			adrConfigure.setPartialAdrconfigure(isPartialAdrconfigure);
			//Update disk with system/boot volume info for issue19941427
			ADRConfigureUtil.updateDiskWithSystemBootVolumeInfo(adrConfigure);
			
			dealWithGPT(adrConfigure);
			
			// issue 131553. for power-on storage disk backup, we need to merge VMDiskInfo.xml disk info into AdrConfigure
			// because AdrConfigure.xml may not include all physical disk info when it has storage space disk.
			if(!isPartialAdrconfigure){
				adrConfigXML = sessionPath + CommonUtil.PARTIAL_ADRCONFIG_File;
				File vmDiskInfoXML = new File(adrConfigXML);
				if(vmDiskInfoXML.exists()){
					ADRConfigure vmDiskInfo = ADRConfigureFactory.parseADRConfigureXML(adrConfigXML);
					mergeDiskInfo(adrConfigure , vmDiskInfo);
				}
			}
			
		}catch(FileNotFoundException e){
			log.error("Error in getting adrconfigure. File \"" + adrConfigXML + "\" ." + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error in getting adrconfigure. File \"" + adrConfigXML + "\" ." + e.getMessage(), e);
			return null;
		}
		
		return adrConfigure;
		
	}
	
	public static void updateDiskDestWithAdrconfigInfo(
			ReplicationDestination replicationDestination, SessionInfo session) throws FileNotFoundException {
				
		try {
			ADRConfigure adrConfigure = getAdrConfigure(session);
			SortedSet<Disk> disks = adrConfigure.getDisks();
			File sessFile = new File(session.getSessionFolder());
			HACommon.removeDiskWihoutFile(disks, sessFile);		
			List<DiskDestination> diskDestList = replicationDestination.getDiskDestinations();
			Collections.sort(diskDestList, new Comparator<DiskDestination>() {
				@Override
				public int compare(DiskDestination dd1, DiskDestination dd2) {
					try {
						return dd1.getDisk().getDiskNumber() - dd2.getDisk().getDiskNumber();
					} catch (Exception e2) {
						log.error(e2.getClass()+e2.getMessage(),e2);
						return 0;
					}
				}
			});	
			
			List<DiskDestination> newAddedDisks = new ArrayList<DiskDestination>();
			for (Disk disk : disks) {
				boolean found = false;
				for (DiskDestination diskDestination : diskDestList) {
					if(disk.getSignature().equals(diskDestination.getDisk().getSignature())){
						found = true;
						break;
					}
				}
				if(!found){
					DiskDestination tmp = new DiskDestination();
					DiskModel diskModel = new DiskModel();
					diskModel.setSignature(disk.getSignature());
					diskModel.setDiskNumber(disk.getDiskNumber());
					tmp.setDisk(diskModel);
					VMStorage storage = new VMStorage();
					storage.setName(diskDestList.get(0).getStorage().getName());
					tmp.setStorage(storage);
					
					newAddedDisks.add(tmp);
				}
			}
			
			if(newAddedDisks.size()>0){
				replicationDestination.getDiskDestinations().addAll(newAddedDisks);
				newAddedDisks.clear();
			}
		} catch (FileNotFoundException e1) {
			throw e1;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

	}

	public static List<FileItemModel> getFileItemModels(ReplicationDestination replicationDestination, SessionInfo session){
		
		File sessFile = new File(session.getSessionFolder());
		File[] d2dFiles = sessFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".d2d") || name.endsWith(".D2D");
			}
		});

		List<DiskDestination> diskDestinations = replicationDestination.getDiskDestinations();
		
		Collections.sort(diskDestinations, new Comparator<DiskDestination>() {
			@Override
			public int compare(DiskDestination dd1, DiskDestination dd2) {
				try {
					return dd1.getDisk().getDiskNumber() - dd2.getDisk().getDiskNumber();
				} catch (Exception e2) {
					log.error(e2.getClass()+e2.getMessage(),e2);
					return 0;
				}
			}
		});

		List<FileItemModel> fileItems = new ArrayList<FileItemModel>();
		for (File file : d2dFiles) {
			FileItemModel item = new FileItemModel();
			item.setFilePath(file.getName());
			boolean hasDest = false;
			for (DiskDestination diskDestination : diskDestinations) {
				if (file.getName().indexOf(
						diskDestination.getDisk().getSignature()) != -1) {
					hasDest = true;
					item.setFileDestination(diskDestination.getStorage()
							.getName());
				}
			}
			if (!hasDest) {
				item.setFileDestination(diskDestinations.get(0).getStorage().getName());
			}
			fileItems.add(item);
		}

		return fileItems;
	}
	
	public static String handleGTPDiskSignature(Disk disk) throws HAException{
		if(disk.getPartitionType().equals("1")){
			return disk.getSignature();
		}else if(disk.getPartitionType().equals("2")){
			String diskGuid = disk.getDiskGuid();
			if(!diskGuid.startsWith("{")){
				diskGuid = "{" + diskGuid;
			}
			if(!diskGuid.endsWith("}")){
				diskGuid = diskGuid + "}";
			}
			return diskGuid;
		}else{
			log.error("This disk is neither MBR nor GPT disk. Partition Type: " + disk.getPartitionType());
			throw new HAException("This disk is neither MBR nor GPT disk", "1");
		}	
	}
	
	public static String fillSignature(String signature) {

		if (StringUtil.isEmptyOrNull(signature)) {
			return "";
		}

		if (signature.length() == 10) {
			return signature;
		}

		if (signature.length() < 10) {
			char[] prefix = new char[10 - signature.length()];
			Arrays.fill(prefix, '0');
			signature = new String(prefix) + signature;
		}

		return signature;
	}
	
	public static String getDiskStorage(List<DiskDestination> diskDestinations, Disk disk) throws Exception {

		if (diskDestinations == null || diskDestinations.size() == 0)
			throw new HAException("No disk found in disk destination",
					MonitorWebServiceErrorCode.HyperV_Operation_Error);
		String path = null;
		for (DiskDestination diskDestination : diskDestinations) {
			String diskSignature = diskDestination.getDisk().getSignature();
			if ((diskSignature!=null)&&diskSignature.equals(disk.getSignature())) {
				path = diskDestination.getStorage().getName();
				break;
			}
		}
		if(path == null){
			Collections.sort(diskDestinations, new Comparator<DiskDestination>() {
				@Override
				public int compare(DiskDestination d1, DiskDestination d2) {
					return d1.getDisk().getDiskNumber() - d2.getDisk().getDiskNumber();
				}
			});
			path = diskDestinations.get(0).getStorage().getName();
		}
		return path;

	}
	
	public static VMInfomationModelManager getSnapshotModeManager(
			String lastRepDest) {
//		log.info("lastRepDest: " + lastRepDest);

		if (StringUtil.isEmptyOrNull(lastRepDest)) {
			log.error("lastRepDest is null");
			return null;
		}

		String file = getQualifiedLastRepDest(lastRepDest);

		VMInfomationModelManager manager = VMInfomationModelManager
				.getModeManagerInstance(file);

		return manager;

	}

	private static String getQualifiedLastRepDest(String lastRepDest) {
		File tmp = new File(lastRepDest);
		String file = "";
		if (tmp.exists()) {
			NativeFacade facade = BackupService.getInstance().getNativeFacade();
			try {
				file = facade.getHAConfigurationFileURL(lastRepDest,
						CommonUtil.SNAPSHOT_XML_FILE);
				if (!file.endsWith("\\")) {
					file += "\\";
				}
				file += CommonUtil.SNAPSHOT_XML_FILE;
			} catch (ServiceException e) {
				log.error(e.getMessage()+e.getErrorCode(),e);
				if (!lastRepDest.endsWith("\\")) {
					lastRepDest += "\\";
				}
				file = lastRepDest + CommonUtil.SNAPSHOT_XML_FILE;
			}
		} else {
			if (!lastRepDest.endsWith("\\")) {
				lastRepDest += "\\";
			}
			file = lastRepDest + CommonUtil.SNAPSHOT_XML_FILE;
		}
		return file;
	}

	public static VMInfomationModelManager getSnapshotModeManager(
			String lastRepDest, String afGuid) {
		log.debug("lastRepDest: " + lastRepDest);

		if (StringUtil.isEmptyOrNull(lastRepDest)) {
			log.debug("lastRepDest is null");
			return null;
		}

		String file = getQualifiedLastRepDest(lastRepDest);
		
		String locFile = null;
		try {
			locFile = Paths.get(file).getParent().getParent().getParent().toString();
		} catch (NullPointerException e) {
		}
		
		boolean found = false;
		try {
			File xmlFile = new File(file);
			found = xmlFile.exists();
			if(!found) {
				String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
				ProductionServerRoot serverRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(afGuid);
				if(serverRoot.getReplicaRoot() instanceof TransServerReplicaRoot) {
					String firstFile = ((TransServerReplicaRoot)serverRoot.getReplicaRoot()).getFirstReplicDest();
					if(!StringUtil.isEmptyOrNull(firstFile) && new File(firstFile).exists()) {
						file = firstFile;
						if (!file.endsWith("\\")) {
							file += "\\";
						}
						file = file + CommonUtil.SNAPSHOT_XML_FILE;
						xmlFile =  new File(file);
						found = xmlFile.exists();
					}
				}
			}
		}
		catch(Exception e) {
			log.info(String.format("Fail to check %s location in repository.xml.", CommonUtil.SNAPSHOT_XML_FILE) + e.getMessage());
		}
		
		if (!found && locFile != null) {
			BufferedReader fr = null;
			try {
				fr =  new BufferedReader(new FileReader(locFile + "\\" + CommonUtil.SNAPSHOT_XML_LOCATION_FILE));
				String path = fr.readLine();
				if (path != null && path != "")
					file = path;
			}
			catch (FileNotFoundException e) {
			}
			catch (Exception e) {
				log.warn(String.format("Fail to read %s. ", CommonUtil.SNAPSHOT_XML_LOCATION_FILE), e);
			}
			finally {
				if (fr != null) {
					try {
						fr.close();
					} catch (IOException e) {
					}
				}
			}
		}

		VMInfomationModelManager manager = VMInfomationModelManager
				.getModeManagerInstance(file);

		return manager;
	}
	
	public static VMInfomationModelManager getSnapshotModeManagerByVMGUID(
			String vmGUID) {
		log.debug("vmuuid: " + vmGUID);

		if (StringUtil.isEmptyOrNull(vmGUID)) {
			log.debug("vmuuid is null");
			return null;
		}

		NativeFacade facade = BackupService.getInstance().getNativeFacade();
		String file = "";
		try {
			file = facade.getHAConfigurationFileURLByVMGUID(vmGUID,	CommonUtil.SNAPSHOT_XML_FILE);
			if (!file.endsWith("\\")) {
				file += "\\";
			}
			file += CommonUtil.SNAPSHOT_XML_FILE;
		} catch (ServiceException e) {
			log.error(e.getMessage());
			log.error(e.getErrorCode());
			return null;
		}
		
		File xmlFile = new File(file);
		if (!xmlFile.exists()) {
			String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
			String firstRepDes = RepositoryUtil.getInstance(xml).getFirtRepDestination(vmGUID);
			if (firstRepDes != null)
			{
				if (!firstRepDes.endsWith("\\"))
					firstRepDes += "\\";
				firstRepDes += CommonUtil.SNAPSHOT_XML_FILE;
				file = firstRepDes;
			}
		}

		VMInfomationModelManager manager = VMInfomationModelManager
				.getModeManagerInstance(file);
		return manager;

	}
	
	public static BackupConfiguration getBackupConfigurationViaAFGuid(String afGuid) throws ServiceException{
		
		if(StringUtil.isEmptyOrNull(afGuid)){
			log.error("afguid is null");
			return null;
		}
		
		//log.info("afGuid: " + afGuid);
		
		BackupConfiguration configuration = null;
		
		if(HAService.getInstance().retrieveCurrentNodeID().equals(afGuid)){
			
			configuration = BackupService.getInstance().getBackupConfiguration();
			
		}else {
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(afGuid);
			VMBackupConfiguration vmBackup = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if(vmBackup == null){
				log.error("can't find the vm backup info:"+afGuid);
				throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_VM_BACKUPINFO);
			}
			
			configuration = convertVMBackupConfiguration2BackupConfiguration(vmBackup);
			
		}
		
		return configuration;
		
	}
	
	public static void main(String[] args) {
		VMBackupConfiguration con  = new VMBackupConfiguration();
		con.setDestination("Destination");
		con.setRetentionCount(100);
		
		convertVMBackupConfiguration2BackupConfiguration(con);
		
	}
	
	private static BackupConfiguration convertVMBackupConfiguration2BackupConfiguration(VMBackupConfiguration vmBackup){
		
		BackupConfiguration conf = new BackupConfiguration();

		Field[] vmBackupFields = BaseVSpherePolicy.class.getDeclaredFields();
		Field[] backupFields = conf.getClass().getDeclaredFields();
		
		for (Field field1 : vmBackupFields) {
			Class<?> type1 = field1.getType();
			String name1 = field1.getName();
			for (Field field2 : backupFields) {
				Class<?> type2 = field2.getType();
				String name2 = field2.getName();
				if (name1.equals(name2) && type1.getName().equals(type2.getName())) {
					try {
						field1.setAccessible(true);
						field2.setAccessible(true);
						Object tmp = field1.get(vmBackup);
						field2.set(conf, tmp);
					} catch (IllegalArgumentException e) {
					} catch (IllegalAccessException e) {
					}
				}
			}
		}
		
		if(vmBackup != null && vmBackup.getBackupVM() != null) {
			BackupVM vm = vmBackup.getBackupVM();
			conf.setDestination(vm.getDestination());
			conf.setUserName(vm.getDesUsername());
			conf.setPassword(vm.getDesPassword());
		}
		
		return conf;
		 
	}
	
	public static void submitBackupJob(String afGuid) throws ServiceException{
		
		if(StringUtil.isEmptyOrNull(afGuid)){
			throw new ServiceException("AFGuid is null",new Object[0]);
		}
		
		String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_BACKUP_FOR_SMART_COPY);
		
		if(afGuid.equals(HAService.getInstance().retrieveCurrentNodeID())){
			BackupService.getInstance().backup(BackupType.Incremental, msg);
		}else{
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(afGuid);
			VSphereService.getInstance().backupVM(BackupType.Incremental, msg, vm);
		}
	}

	public static boolean isTargetPhysicalMachine(String afGuid){
	
		String physicalMachineAFGuid = HAService.getInstance().retrieveCurrentNodeID();
		
		return physicalMachineAFGuid.equals(afGuid);
		
	}
	
	// the name returned equals to that is used in D2D backup destination path
	public static String getProductionServerNameByAFRepJobScript(ReplicationJobScript jobScript){
		String productionServer = "";
		if (!jobScript.getBackupToRPS()){
			productionServer = HACommon.getProductionServerNameByAFGuid(jobScript.getAFGuid());
		}
		else{
			productionServer = jobScript.getAgentNodeName();
		}
		
		return productionServer;
	}
	public static String getProductionServerNameByAFGuid(String afGuid){
		String productionServer = null;
		if(HACommon.isTargetPhysicalMachine(afGuid)){
			try {
				String backupDest = getBackupConfigurationViaAFGuid(afGuid).getDestination();
				int index = backupDest.lastIndexOf("\\");
				productionServer = backupDest.substring(index+1);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				try {
					productionServer = InetAddress.getLocalHost().getHostName();
				} catch (UnknownHostException e1) {
					log.error("Failed to get host name." + e.getMessage(),e);
				}
			}
		}else{
			
			try {
				
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(afGuid);
				VMBackupConfiguration vmBackup = VSphereService.getInstance().getVMBackupConfiguration(vm);
				
				String backupDest = vmBackup.getBackupVM().getDestination();
				int index = backupDest.lastIndexOf("\\");
				productionServer = backupDest.substring(index+1);
				
			} catch (Exception e) {
				log.error("Failed to get VM destination." + e.getMessage(),e);
			}
			
		}
		
		return productionServer;
		
	}
	
	public static boolean isBackupToRPS(String afGuid){
		boolean ifBackupToRPS = false;
		if(HACommon.isTargetPhysicalMachine(afGuid)){
			ifBackupToRPS = BackupService.getInstance().isBackupToRPS();
		}else{
			
			try {
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(afGuid);
				VMBackupConfiguration vmBackup = VSphereService.getInstance().getVMBackupConfiguration(vm);
				
				ifBackupToRPS = !vmBackup.isD2dOrRPSDestType();
			} catch (Exception e) {
				log.error("Failed to get VM destination." + e.getMessage(),e);
			}
		}
		return ifBackupToRPS;
	}

	public static void printJobScript(HyperVRepParameterModel model){
		
		if(!CommonUtil.isPrintScriptEnabled()){
			return;
		}
	
		try {
			String folder = "c:\\HATemp";
			File f = new File(folder);
			if(!f.exists()){
				f.mkdirs();
			}
			String name = "jobscript-" + System.currentTimeMillis() + ".xml";
			OutputStream script = new FileOutputStream(folder + "\\" + name);
			JAXB.marshal(model, script);
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public static void addActivityLogByAFGuid(long level, long jobID,long resourceID, String[] msg,String afGuid){
		
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();

		log.info("Adding activity log [" + msg[0] +"], jobID = " + jobID);
		
		try {
			if(jobID == -1){
				addActivityLogByAFGuidWithDetails(nativeFacade, level, 0,resourceID, msg, afGuid);
			} else {
				
				boolean ifJobHistoryExist = nativeFacade.ifJobHistoryExist(1, jobID, Constants.AF_JOBTYPE_CONVERSION, afGuid);
				if (ifJobHistoryExist) {
					if(StringUtil.isEmptyOrNull(afGuid) || isTargetPhysicalMachine(afGuid)){
						nativeFacade.addLogActivityWithJobID(level,jobID, resourceID, msg);
					}else {
						nativeFacade.addVMLogActivityWithJobID(level, jobID,resourceID, msg, afGuid);
					}
				} else {
					log.info("Not find the correspoding job history.");
					addActivityLogByAFGuidWithDetails(nativeFacade, level, jobID,resourceID, msg, afGuid);
				}
			}
		} catch (Throwable e) {
			log.error("Failed to add activity log:"+e.getMessage(),e);
		}
	}
	private static void addActivityLogByAFGuidWithDetails(NativeFacade nativeFacade, long level, long jobID,long resourceID, String[] msg,String afGuid){
		JActLogDetails logDetails = new JActLogDetails();
		logDetails.setProductType(1); // APT_D2D
		logDetails.setJobID(new Long(jobID).intValue());
		logDetails.setJobType(Constants.AF_JOBTYPE_CONVERSION);
		logDetails.setJobMethod(0);
		logDetails.setLogLevel(new Long(level).intValue());

		logDetails.setIsVMInstance(true);

		String physicalUUID = HAService.getInstance().retrieveCurrentNodeID();
		
		if (StringUtil.isEmptyOrNull(afGuid))
			afGuid = physicalUUID;
		
		logDetails.setSvrNodeName("");
		logDetails.setSvrNodeID(physicalUUID);
		logDetails.setAgentNodeName("");
		logDetails.setAgentNodeID(afGuid);
		logDetails.setSourceRPSID("");
		logDetails.setTargetRPSID("");
		logDetails.setDSUUID("");
		logDetails.setTargetDSUUID("");
		
		nativeFacade.addLogActivityWithDetailsEx(logDetails, resourceID, msg);
	}
	
	public static RepositoryUtil getRepositoryManager()throws Exception{
		
		RepositoryUtil repository = null;
		try{
			String xml = CommonUtil.getRepositoryConfPath();
			repository = RepositoryUtil.getInstance(xml);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new Exception();
		}
		
		return repository;
		
	}
	
	public static String[] deleteHartFiles(ReplicationJobScript jobScript,SessionInfo session){
		
		ReplicationDestination dest = jobScript.getReplicationDestination().get(0);
		
		
		List<String> undeletedFiles = new LinkedList<String>();
		String[] EMPTY = new String[0];
		
		if(dest.isProxyEnabled()){
			//disk0853684962.D2D.hart			
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(jobScript.getAFGuid());
			String moniteeName = getProductionServerNameByAFRepJobScript(jobScript);
			try {
				String[] undeleteFiles = client.getServiceV2().deleteMonitorHartFiles(moniteeName,session.getSessionName());
				return undeleteFiles;
			} catch (Exception e) {
				log.warn(e.getMessage());
				log.warn("Failed to delete hart files.");
				return new String[0];
			}
		} else {
			/*
			 *  For normal conversion: disk0853684962_HartFile.txt
 			 *	For Hotadd signature update:  disk0853684962_DiskSignHartFile.txt
  			 *	For smart copy:  disk0853684962_ScpyHartFile.txt
			 */
			File sessonFolder = new File(session.getSessionFolder());
			
			if(!sessonFolder.exists()){
				return EMPTY;
			}
			
			File[] files = sessonFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.getName().endsWith("HartFile.txt");
				}
			});
			
			if(files == null || files.length == 0){
				return EMPTY;
			}
			
			for (File file : files) {
				if(!file.delete()){
					undeletedFiles.add(file.getName());
				}
			}
			
			return undeletedFiles.toArray(EMPTY);
			
		}
		
	}
	
	public static String[] deleteHartFilesRecursiveAllSession(ReplicationJobScript jobScript,
															  SessionInfo session){
		
		ReplicationDestination dest = jobScript.getReplicationDestination().get(0);
		
		List<String> undeletedFiles = new LinkedList<String>();
		String[] EMPTY = new String[0];
		
		if(dest.isProxyEnabled()){
			//disk0853684962.D2D.hart			
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(jobScript.getAFGuid());
			String moniteeName = getProductionServerNameByAFRepJobScript(jobScript);
			String[] undeleteFiles = client.getServiceV2().deleteMonitorHartFiles(moniteeName,"");
			return undeleteFiles;
		} else {
			/*
			 *  For normal conversion: disk0853684962_HartFile.txt
 			 *	For Hotadd signature update:  disk0853684962_DiskSignHartFile.txt
  			 *	For smart copy:  disk0853684962_ScpyHartFile.txt
			 */
			File sessonFolder = new File(session.getSessionFolder());
			
			if(!sessonFolder.exists()){
				return EMPTY;
			}
			
			File parentFolder = sessonFolder.getParentFile(); //VStore
			File[] sessions = parentFolder.listFiles();
			for (File singleSession : sessions) {
				File[] hartFiles = singleSession.listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						return file.getName().endsWith("HartFile.txt");
					}
				});
				
				for (File file : hartFiles) {
					if(!file.delete()){
						undeletedFiles.add(file.getName());
					}
				}
			}
			
			return undeletedFiles.toArray(EMPTY);
			
		}
		
	}
	
	
	public static boolean checkHartFileExistence(ReplicationJobScript jobScript,SessionInfo session) throws Exception{
		
		//As child disk can not support resumed conversion
		//delete hart files
		
		ReplicationDestination dest = jobScript.getReplicationDestination().get(0);
		if(dest.isProxyEnabled()){
			//disk0853684962.D2D.hart
			WebServiceClientProxy client = null;
			try {
				client = MonitorWebClientManager.getMonitorWebClientProxy(jobScript.getAFGuid());
			} catch (Exception e) {
				if (e.getCause() instanceof ConnectException || e.getCause() instanceof SSLHandshakeException ||
						e.getCause() instanceof SocketException || e.getCause() instanceof SSLException) {
					HeartBeatJobScript heartJobScript = HAService.getInstance().getHeartBeatJobScript(jobScript.getAFGuid());
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_CONNECT_HOST, heartJobScript.getHeartBeatMonitorHostName(), e.getMessage());
					throw new Exception(msg);
				}
				throw e;
			}
			String moniteeName = getProductionServerNameByAFRepJobScript(jobScript);
			boolean result = false;
			try {
				result = client.getServiceV2().checkMonitorHartFilesExistence(moniteeName,session.getSessionName());
			} catch (Exception e) {
				log.warn(e.getMessage());
				log.warn("Failed to check monitor hart file existence.");
			}
			return result;
			
		} else {
			/*
			 *  For normal conversion: disk0853684962_HartFile.txt
 			 *	For Hotadd signature update:  disk0853684962_DiskSignHartFile.txt
  			 *	For smart copy:  disk0853684962_ScpyHartFile.txt
			 */
			
			File sessonFolder = new File(session.getSessionFolder());
			
			if(!sessonFolder.exists()){
				return false;
			}
			
			File[] files = sessonFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.getName().endsWith("HartFile.txt");
				}
			});
			
			if(files == null || files.length == 0){
				return false;
			}
			
			return files.length > 0;
			
		}
		
	}
	
	public static String date2String(Date date){
		SimpleDateFormat df = new SimpleDateFormat(
				CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeDateFormat(), DataFormatUtil.getDateFormatLocale());
		String backupLocalTime = df.format(date);
		return backupLocalTime;
	}
	
	public static String date2String(long millseconds){
		String backupLocalTime = date2String(new Date(millseconds));
		return backupLocalTime;
	}
	
	public static String getUserFromUsername(String username){
		int idx = username.indexOf("\\");
		return username.substring(idx+1, username.length());	
	}
	
	public static String getDomainFromUsername(String username){
		int idx = username.indexOf("\\");
		if(idx != -1){
			return username.substring(0, idx);
		}
		
		return "";
		
	}
	
	public static String getHostNameForVddk(String hostname, int port){
		
		if(port != 443){
			hostname = hostname + ":" + port;
		}
		
		return hostname;
	}
	
	public static String getRealHostName(){
		
		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}
		
		return hostname;
		
	}
	
	public static ADRConfigure getADRConfigurationForRVS(String destination) {
		ADRConfigure configuration = null;
		if (!destination.endsWith("\\"))
			destination += "\\";
		File f = new File(destination + "VStore");
		File[] listSessions = f.listFiles();
		if (listSessions == null)
			return null;
		Arrays.sort(listSessions, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());

			}
		});
		for (int i=listSessions.length-1;i>=0;i--) {			
			try {
				configuration = HACommon.getAdrConfigureForRVS(listSessions[i].getPath());
				if (configuration != null)
					break; // Always try to get the newest existed one 
			} catch (Exception e) {
				continue;
			}
		}
		return configuration;
	}
	private static ADRConfigure getAdrConfigureForRVS(String sessionPath) {
		
		if(!sessionPath.endsWith("\\")){
			sessionPath += "\\";
		}
		
		String adrConfigXML = sessionPath + CommonUtil.ADRCONFIG_XML_FILE;
		File f = new File(adrConfigXML);
		if(!f.exists()){
			log.warn("The adrconfigure file under "+ sessionPath + " does not exist.");
			return null;
		}
		
		ADRConfigure adrConfigure = null;
		try {
			adrConfigure = ADRConfigureFactory.parseADRConfigureXML(adrConfigXML);
		} catch (Exception e) {
			log.warn("Error in getting adrconfigure."+e.getMessage(),e);
			return null;
		}
		
		return adrConfigure;
		
	}
	
	public static BackupInfo getBackupInfoForRVS(String destination) {
		BackupInfo info = null;
		if (!destination.endsWith("\\"))
			destination += "\\";
		File f = new File(destination + "VStore");
		File[] listSessions = f.listFiles();
		if (listSessions == null)
			return null;
		Arrays.sort(listSessions, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());

			}
		});
		for (int i=listSessions.length-1;i>=0;i--) {			
			try {
				info = HACommon.getBackupInfoFileForRVS(listSessions[i].getPath());
				if (info != null)
					break; // Always try to get the newest existed one 
			} catch (Exception e) {
				continue;
			}
		}
		return info;
	}
	private static BackupInfo getBackupInfoFileForRVS(String sessionPath) {
		
		if(!sessionPath.endsWith("\\")){
			sessionPath += "\\";
		}
		
		String BackupInfoXML = sessionPath + "BackupInfo.xml";;
		File f = new File(BackupInfoXML);
		if(!f.exists()){
			log.warn("The backupinfo file under "+ sessionPath + " does not exist.");
			return null;
		}

		BackupInfo info = null;
		try {
			info = BackupInfoFactory.getBackupInfo(BackupInfoXML);
		} catch (Exception e) {
			log.warn("Error in getting backupinfo."+e.getMessage(),e);
			return null;
		}
		
		return info;
	}
	
	
	public static final int getMaxSnapshotCountForVMware(String afGuid) {
		
		return getMaxSnapshotCount(afGuid, CommonUtil.MAX_VMWARE_SNAPSHOT);
		
	}
	
	public static final int getMaxSnapshotCountForHyperV(String afGuid) {
		
		return getMaxSnapshotCount(afGuid, CommonUtil.MAX_HYPERV_SNAPSHOT);
	}
	
	
	private static final int getMaxSnapshotCount(String afGuid, int maxSnapshotInTheory) {
		
		int maxSnapshots = 1000; //default value is 1000. Bigger than max in theory.Make it unrealistic value
		
		String rootRegistry = "";
		if(isTargetPhysicalMachine(afGuid)){
			rootRegistry = CommonRegistryKey.getD2DRegistryRoot();
		}else{
			rootRegistry = CommonRegistryKey.getD2DRegistryRoot() + "\\vSphereVMInstUUID\\"+afGuid;
		}
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			
			handle = registry.openKey(rootRegistry);
			String value = registry.getValue(handle, "MaximumSnapshotsforVS");
			if (!StringUtil.isEmptyOrNull(value)) {
				maxSnapshots = Integer.parseInt(value);
			}
			
			if(maxSnapshots > maxSnapshotInTheory){
				maxSnapshots = maxSnapshotInTheory;
			}
			
			return maxSnapshots;
			
		} catch (Exception e) {
			return maxSnapshotInTheory;
		}finally{
			if(handle != 0){
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
				}
			}
		}
	}
	
	public static ADRConfigure getADRConfiguration(String uuid, BackupDestinationInfo backupDestinationInfo) throws ServiceException, Exception {
		if (backupDestinationInfo == null || backupDestinationInfo.getBackupDestination() == null)
			throw new ServiceException("getADRConfiguration: Null or empty backup configuration destination!",new Object[0]);

		String remotePath = backupDestinationInfo.getBackupDestination();
		boolean isRemote = CommonUtil.isRemote(remotePath);
		ADRConfigure adr = null;
		try {

			if (isRemote) {
				BaseReplicationCommand.connectToRemote(backupDestinationInfo,uuid,-1);;
			}
			adr = getADRConfigurationForRVS(remotePath);
		} finally {
			if (isRemote) {
				try {
					BaseReplicationCommand.closeRemoteConnect(backupDestinationInfo);
				} catch (Exception e) {
					log.error("getADRConfiguration: closeRemoteConnect", e);
				}
			}
		}
		return adr;
	}

	public static BackupInfo getBackupConfiguration(String uuid, BackupDestinationInfo backupDestinationInfo) throws ServiceException, Exception {
		if (backupDestinationInfo == null || backupDestinationInfo.getBackupDestination() == null)
			throw new ServiceException("getBackupConfiguration: Null or empty backup configuration destination!",new Object[0]);

		String remotePath = backupDestinationInfo.getBackupDestination();
		boolean isRemote = CommonUtil.isRemote(remotePath);
		BackupInfo info = null;
		try {

			if (isRemote) {
				BaseReplicationCommand.connectToRemote(backupDestinationInfo,uuid,-1);;
			}
			info = getBackupInfoForRVS(remotePath);
		} finally {
			if (isRemote) {
				try {
					BaseReplicationCommand.closeRemoteConnect(backupDestinationInfo);
				} catch (Exception e) {
					log.error("getBackupConfiguration: closeRemoteConnect", e);
				}
			}
		}
		return info;
	}
	
	// merge vmDiskInfo disk info to adrConfigure
	private static void mergeDiskInfo(ADRConfigure adrConfigure, ADRConfigure vmDiskInfo) {
		SortedSet<Disk> adrDiskList = adrConfigure.getDisks();
		SortedSet<Disk> vmDiskList = vmDiskInfo.getDisks();
		if(adrDiskList == null || adrDiskList.size() == 0){
			adrConfigure.setDisks(vmDiskList);
		}
		if(vmDiskList == null || vmDiskList.size() == 0){
			return; 
		}
		// add vmDiskInfo disk info to adrConfigure 
		for(Disk vmDisk : vmDiskList){
			boolean isExist = false;
			//check if it exists in adrConfigure
			for(Disk adrDisk : adrDiskList){
				if(adrDisk.getSignature()!=null && adrDisk.getSignature().equals(vmDisk.getSignature())) {
					isExist = true;
					break;
				}
			}
			// if not exist , add to disk list
			if(!isExist){
				adrDiskList.add(vmDisk);
			}
		}
	}

	public static synchronized SessionPasswordCheckStatus checkAndUpdateSessionPassword(SessionInfo sessionInfo,
			ReplicationJobScript jobScript, String dest) {
		if (log.isInfoEnabled()) {
			log.info("Check session password for node:" + jobScript.getAFGuid() + " session:"
					+ sessionInfo.getSessionName());
		}
		if (!sessionInfo.isSessionEncrypted()) {
			if (log.isInfoEnabled()) {
				log.info(sessionInfo.getSessionName() + " is not encrypted.");
			}
			return SessionPasswordCheckStatus.PLAIN;
		}

		// The session is encrypted
		String[] sessionGuids = new String[] { getWrappedSessionGuid(sessionInfo.getSessionGuid()) };
		String[] sessionPwds = WSJNI.AFGetSessionPasswordBySessionGuid(sessionGuids);
		if (sessionPwds[0] != null) {
			log.info("Password already updated, skip to check other sessions.");
			return SessionPasswordCheckStatus.UPDATED;
		}

		List<String> passwordList = getCustomSessionPasswords(jobScript.getAFGuid());
		if (passwordList == null || passwordList.size() == 0) {
			log.error("The custom session password list is empty, but the session is encrypted.");
			return SessionPasswordCheckStatus.INVALID;
		}

		String validPassword = null;
		for (String password : passwordList) {
			boolean passwordValid = WSJNI.AFValidateSessPassword(password, dest,
					Long.parseLong(sessionInfo.getBackupID()));
			if (passwordValid) {
				validPassword = password;
				break;
			}
		}

		if (jobScript.getBackupToRPS()){
			if (validPassword != null) {
				log.info("Get the valid password.");
				return SessionPasswordCheckStatus.VALID;
			}
		}
		else{
			if (validPassword != null) {
				log.info("Find the valid password, update now.");
				// Update session password
				String[] passwords = new String[] { validPassword };
				int updateResult = WSJNI.updateSessionPasswordByGUID(sessionGuids, passwords);
				if (log.isInfoEnabled()) {
					log.info("update session password result:" + updateResult);
				}
				if (updateResult == 0) {
					return SessionPasswordCheckStatus.VALID;
				} else {
					log.error("Failed to update session password for session:" + sessionInfo.getSessionName());
				}
			} else {
				log.error("Failed to get valid session password for session:" + sessionInfo.getSessionName());
			}
		}
		return SessionPasswordCheckStatus.INVALID;
	}

	private static List<String> getCustomSessionPasswords(String afGuid) {
		return CustomSessionPasswordManager.getCustomPasswords(afGuid);
	}
	
	public static String processWebServiceException(WebServiceException e, Object... args) throws ServiceException {

		if(e.getCause() instanceof ConnectException 
				|| e.getCause() instanceof UnknownHostException
				|| e.getCause() instanceof SSLHandshakeException 
				|| e.getCause() instanceof SocketException
				|| e.getCause() instanceof SSLException 
				|| e.getMessage().startsWith("XML reader error") ){
			throw new ServiceException(FlashServiceErrorCode.Common_CantConnectNode, args); 
		} 
		
		if(e instanceof SOAPFaultException){
			SOAPFaultException fault = (SOAPFaultException)e;
			String m = fault.getFault().getFaultString();
			String mesg = m.replace("Client received SOAP Fault from server: ", "");
			return mesg.replace(" Please see the server log to find more detail regarding exact cause of the failure.", "");			
		}
		return "";
	}

	private static String getWrappedSessionGuid(String sessionGuid) {
		if (sessionGuid != null && !(sessionGuid.startsWith("{") && sessionGuid.endsWith("}"))) {
			return "{" + sessionGuid + "}";
		}
		return sessionGuid;
	}
}
