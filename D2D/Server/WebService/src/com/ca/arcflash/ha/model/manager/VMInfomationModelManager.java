package com.ca.arcflash.ha.model.manager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.model.VirtualMachineInfo;
import com.ca.arcflash.ha.model.internal.SnapshotsWrapper;
/**
 * We add this model because:
 * 	1. user may change the snapshot, or add new snapshot temporary
 * @author gonro07
 *
 */
public class VMInfomationModelManager {
	
	private static Logger log = Logger.getLogger(VMInfomationModelManager.class);
	
	private AtomicLong snapNo = new AtomicLong(0);

	//This update operation should be synchronized
	public static Lock UpdateLock = new ReentrantLock(); 
	
	private SnapshotsWrapper snaps = null;
	private String MODEL_XML = "";

	private static ThreadLocal<VMInfomationModelManager> managerThreadLocal = new ThreadLocal<VMInfomationModelManager>() {
		@Override
		protected VMInfomationModelManager initialValue() {
			return new VMInfomationModelManager();
		}
	};
	
	private VMInfomationModelManager() {
	}
	
	public static VMInfomationModelManager getModeManagerInstance(String file) {
		VMInfomationModelManager manager = managerThreadLocal.get();
		manager.initizlize(file);
		return manager;
	}

	public static VMInfomationModelManager getNonThreadLocalModeManagerInstance(
			String file) {
		VMInfomationModelManager manager = new VMInfomationModelManager();
		manager.initizlize(file);
		return manager;
	}
	
	public synchronized void initizlize(String file) {
		
		if (StringUtil.isEmptyOrNull(file)	|| !file.endsWith(CommonUtil.SNAPSHOT_XML_FILE)) {
			log.error("file: " + file);
			throw new RuntimeException(	"file name not or correct to initialize " + CommonUtil.SNAPSHOT_XML_FILE);
		}
		
		MODEL_XML = file;
		try{
	
			snaps = CommonUtil.unmarshal(new FileInputStream(new File(MODEL_XML)), SnapshotsWrapper.class);
	
		} catch (Exception e) {
			log.error("loading Snapshots failded.");
			snaps = new SnapshotsWrapper();
		}
		log.debug("Caculating max snap No....");
		HashMap<VirtualMachineInfo, SortedSet<VMSnapshotsInfo>> snaps2 = snaps.getSnaps();
		Set<VirtualMachineInfo> keySet = snaps2.keySet();
		long max = 1;
		for(VirtualMachineInfo info:keySet){
			SortedSet<VMSnapshotsInfo> sortedSet = snaps2.get(info);
			for(VMSnapshotsInfo vSnapInfo: sortedSet){
				long snapNo2 = vSnapInfo.getSnapNo();
				if(max < snapNo2) max = snapNo2;
			}
		}
		snapNo.set(max);
	}
	
	
	
	
	/**
	 * get a copy set of snapshot for the given production server
	 * @param afguid
	 * @return
	 */
	public SortedSet<VMSnapshotsInfo> getSnapshots(String afguid){
		VirtualMachineInfo vmi = new VirtualMachineInfo(afguid);
		return getSnapshots(vmi);
	}
	/**
	 * 
	 * @param afguid
	 * @param sessionGuid
	 * @return
	 */
	public VMSnapshotsInfo getSnapshot(String afguid, String sessionGuid) {
		SortedSet<VMSnapshotsInfo> snapshots = getSnapshots(afguid);
		for (VMSnapshotsInfo info : snapshots) {
			if (info.getSessionGuid().compareToIgnoreCase(sessionGuid) == 0)
				return info;
		}
		return null;
	}
	
//	public VirtualMachineInfo getVMInfo(String afguid){	
//		return  HeartBeatModelManager.getVMInfo(afguid);
//	}
	
	public VirtualMachineInfo getInternalVMInfo(String afguid){
		
		synchronized(snaps){
			Set<VirtualMachineInfo> keySet = snaps.getSnaps().keySet();
			for(VirtualMachineInfo vmInfo: keySet){
				if(vmInfo.getAfguid().equals(afguid)) return vmInfo;
			}
		}
		return null;
	}
	/**
	 * get a copy set of snapshot for the given VM.
	 * @param vmi
	 * @return
	 */
	public SortedSet<VMSnapshotsInfo> getSnapshots(VirtualMachineInfo vmi){
		synchronized(snaps){
			 SortedSet<VMSnapshotsInfo> re = new  TreeSet<VMSnapshotsInfo>();
			 SortedSet<VMSnapshotsInfo> sortedSet = snaps.getSnaps().get(vmi);
			 if(sortedSet!=null){
				 re.addAll(sortedSet);
			 }
			 return re;
		}
	}

	public void putSnapShot(VirtualMachineInfo vmi,VMSnapshotsInfo sinfo){
		synchronized(snaps){
			 SortedSet<VMSnapshotsInfo> sortedSet = snaps.getSnaps().get(vmi);
			 if(sortedSet == null){
				 sortedSet =  new  TreeSet<VMSnapshotsInfo>();
				 snaps.getSnaps().put(vmi, sortedSet);
			 }
			 sortedSet.remove(sinfo);
			 sortedSet.add(sinfo);
			 store();
		}
	}
	
	public void replaceSnapshots(VirtualMachineInfo vmi, SortedSet<VMSnapshotsInfo> sinfos){
		synchronized(snaps){
			 SortedSet<VMSnapshotsInfo> sortedSet = snaps.getSnaps().get(vmi); 
			 if(sortedSet == null){
				 return;
			 }
			 sortedSet.clear();
			 putSnapShot(vmi, sinfos);
		}
	}
	public void clearSnapshots(String afguid){
		VirtualMachineInfo vmi = new VirtualMachineInfo(afguid);
		synchronized(snaps){
			SortedSet<VMSnapshotsInfo> remove = snaps.getSnaps().remove(vmi);
			if(remove!=null)
				store();
		}
	}
	public void putSnapShot(VirtualMachineInfo vmi,SortedSet<VMSnapshotsInfo> sinfos){
		synchronized(snaps){
			for(VMSnapshotsInfo sinfo : sinfos){
				putSnapShot(vmi, sinfo);
			}
			store();
		}
	}
	public void putNewVM(VirtualMachineInfo vmi,SortedSet<VMSnapshotsInfo> sinfos){
		synchronized(snaps){
			snaps.getSnaps().remove(vmi);
			snaps.getSnaps().put(vmi, sinfos);
			store();
		}
	}
	
	public synchronized void mergeSnapshots(String snapshotXml){
		try {
			synchronized (snaps) {
				SnapshotsWrapper snapshotWrapper = CommonUtil.unmarshal(snapshotXml, SnapshotsWrapper.class);
				HashMap<VirtualMachineInfo, SortedSet<VMSnapshotsInfo>> snapshots = snapshotWrapper.getSnaps();
				Set<Entry<VirtualMachineInfo, SortedSet<VMSnapshotsInfo>>> entries = snapshots.entrySet();
				for(Entry<VirtualMachineInfo, SortedSet<VMSnapshotsInfo>> entry : entries){
					putSnapShot(entry.getKey(),entry.getValue());
				}
			}
		} catch (JAXBException e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public VirtualMachineInfo getVirtualMachineInfo(String vmGUID){
		VirtualMachineInfo virtualMachineInfo=null;
		synchronized (snaps) {
			Set<VirtualMachineInfo> virtualMachineInfoSet= snaps.getSnaps().keySet();
			for (VirtualMachineInfo vm : virtualMachineInfoSet) {
				if(vm.getVmGUID().compareToIgnoreCase(vmGUID)==0){
					virtualMachineInfo=new VirtualMachineInfo(vm.getType(),
							vm.getVmGUID(), vm.getAfguid(), vm.getBaseSnapshot());
					break;
				}
			}
		}
		return virtualMachineInfo;
	}
	
	public void updateVirtualMachine(VirtualMachineInfo vmInfo){
		VirtualMachineInfo oldVM = getInternalVMInfo(vmInfo.getAfguid());
		if(oldVM == null){
			putNewVM(vmInfo, new TreeSet<VMSnapshotsInfo>());
		}else{
			oldVM.setBaseSnapshot(vmInfo.getBaseSnapshot());
			oldVM.setVmGUID(vmInfo.getVmGUID());
			oldVM.setVmName(vmInfo.getVmName());
			oldVM.setType(vmInfo.getType());
		}
		store();
	}
	
	public long getSnapNo() {
		return snapNo.incrementAndGet();
	}
	
	private void store() {
		
		//This update operation should be synchronized
		VMInfomationModelManager.UpdateLock.lock();

		BufferedOutputStream bos = null;
		try {

			bos = new BufferedOutputStream(new FileOutputStream(MODEL_XML));
			JAXB.marshal(snaps, bos);

		} catch (FileNotFoundException e) {
			String tmpFile = MODEL_XML.substring(0, MODEL_XML.length()
					- CommonUtil.SNAPSHOT_XML_FILE.length());
			File temp = new File(tmpFile);
			temp.mkdirs();
			try {
				bos = new BufferedOutputStream(new FileOutputStream(MODEL_XML));
				JAXB.marshal(snaps, bos);
			} catch (FileNotFoundException e1) {
				log.error(e1.getMessage(), e1);
				throw new RuntimeException(e1.getMessage());
			}
		} finally {
			try {
				if (bos != null)
					bos.close();
			} catch (Exception e) {
			}
			
			VMInfomationModelManager.UpdateLock.unlock();
		}
		
	}

	public String getMODEL_XML() {
		return MODEL_XML;
	}
	
}
