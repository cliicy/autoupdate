package com.ca.arcflash.ha.model.manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.model.VMVirtualDiskInfo;
import com.ca.arcflash.ha.model.VirtualMachineInfo;
import com.ca.arcflash.ha.model.internal.SnapshotsWrapper;
import com.ca.arcflash.ha.model.internal.VMVirtualDiskList;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;

public class VMwareSnapshotModelManager {

	public static final String DUMMY_SNAPSHOT_TYPE = "Dummy Type";
	public static final String DUMMY_SNAPSHOT_NAME = "Dummy Snapshot";

	private static Logger log = Logger.getLogger(VMwareSnapshotModelManager.class);
	private AtomicLong snapNo = new AtomicLong(0);

	private CAVirtualInfrastructureManager vmwareOBJ;
	private String vmname;
	private String vmuuid;
	private Object vmMor = null;
	private SnapshotsWrapper snaps = null;
	
	private boolean isReady = false;

	private VMwareSnapshotModelManager(
			CAVirtualInfrastructureManager vmwareOBJ, String vmname,
			String vmuuid) throws Exception{
		this.vmwareOBJ = vmwareOBJ;
		this.vmname = vmname;
		this.vmuuid = vmuuid;
		initialize();
		isReady = true;
	}
	
	
	private VMwareSnapshotModelManager(
			CAVirtualInfrastructureManager vmwareOBJ, String vmname,
			String vmuuid, Object vmMor) throws Exception{
		this.vmwareOBJ = vmwareOBJ;
		this.vmname = vmname;
		this.vmuuid = vmuuid;
		this.vmMor = vmMor;
		initialize();
		isReady = true;
	}
	

	public synchronized static VMwareSnapshotModelManager getManagerInstance(
			CAVirtualInfrastructureManager vmwareOBJ, String vmname,
			String vmuuid) throws Exception{
		
		VMwareSnapshotModelManager manager = new VMwareSnapshotModelManager(
				vmwareOBJ, vmname, vmuuid);

		return manager;
	
	}

	public synchronized static VMwareSnapshotModelManager getManagerInstance(
			CAVirtualInfrastructureManager vmwareOBJ, String vmname,
			String vmuuid, Object vmMor) throws Exception{
		
		VMwareSnapshotModelManager manager = new VMwareSnapshotModelManager(
				vmwareOBJ, vmname, vmuuid, vmMor);

		return manager;
	
	}
	
	
	public boolean isReady() {
		return isReady;
	}

	public synchronized SortedSet<VMSnapshotsInfo> getSnapshots(
			VirtualMachineInfo vmi) {
		SortedSet<VMSnapshotsInfo> re = new TreeSet<VMSnapshotsInfo>();
		SortedSet<VMSnapshotsInfo> sortedSet = snaps.getSnaps().get(vmi);
		if (sortedSet != null) {
			re.addAll(sortedSet);
		}
		return re;
	}

	public synchronized SortedSet<VMSnapshotsInfo> getSnapshots(String afguid) {
		VirtualMachineInfo vmi = new VirtualMachineInfo(afguid);
		return getSnapshots(vmi);
	}

	public synchronized boolean replaceSnapshots(VirtualMachineInfo vmi,
			SortedSet<VMSnapshotsInfo> sinfos) throws Exception{
		SortedSet<VMSnapshotsInfo> sortedSet = snaps.getSnaps().get(vmi);
		if (sortedSet == null) {
			return false;
		}
		sortedSet.clear();
		putSnapShot(vmi, sinfos);
		boolean result = saveVMSnapshotToStorage();
		return result;
	}
	
	public synchronized boolean replaceSnapshots(String afguid,	SortedSet<VMSnapshotsInfo> sinfos) throws Exception{
		VirtualMachineInfo vmi = getInternalVMInfo(afguid);
		boolean result = replaceSnapshots(vmi, sinfos);
		return result;
	}

	public synchronized boolean putSnapShot(VirtualMachineInfo vmi,
			SortedSet<VMSnapshotsInfo> sinfos) throws Exception{
		for (VMSnapshotsInfo sinfo : sinfos) {
			putSnapShot(vmi, sinfo);
		}

		boolean result = saveVMSnapshotToStorage();

		return result;

	}

	public synchronized VirtualMachineInfo getInternalVMInfo(String afguid) {

		Set<VirtualMachineInfo> keySet = snaps.getSnaps().keySet();
		for (VirtualMachineInfo vmInfo : keySet) {
			if (vmInfo.getAfguid().equals(afguid)) {
				return vmInfo;
			}
		}
		return null;
	}

	private synchronized String initialize() throws Exception{
		try {
			String snapshots = getVMSnapshotXmlFromStorage();
			StringBuffer buffer = null;
			if (snapshots == null) {
				buffer = new StringBuffer();
			} else {
				buffer = new StringBuffer(snapshots);
			}
			initizlize(buffer);
			return snapshots;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}

	}

	public synchronized void putVirtualDisk(VMVirtualDiskInfo disk) {
		snaps.getVirtualDiskList().getVirtualDiskList().add(disk);
	}

	public synchronized void removeVirtualDisk(VMVirtualDiskInfo disk) {
		snaps.getVirtualDiskList().getVirtualDiskList().remove(disk);
	}

	public long getSnapNo() {
		return snapNo.incrementAndGet();
	}

	public synchronized VMVirtualDiskList getVirtualDiskList() {
		return snaps.getVirtualDiskList();
	}

	private synchronized String getSnapShotsXml() {
		StringWriter writer = new StringWriter();
		JAXB.marshal(snaps, writer);
		return writer.toString();
	}

	private synchronized boolean saveVMSnapshotToStorage() throws Exception{

		String snapshots = getSnapShotsXml();

		if (StringUtil.isEmptyOrNull(snapshots))
			return false;

		ByteArrayInputStream buffer = new ByteArrayInputStream(
				snapshots.getBytes("UTF-8"));
		boolean isUpload = false;
		int retryDownloadTimes = 0;
		while (true) {
			try {
				isUpload = vmwareOBJ.putVMConfig(vmname, vmuuid,
						CommonUtil.SNAPSHOT_XML_FILE, buffer);
				
				break;
				
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {}
				
				if (++retryDownloadTimes >= 3) {
					throw e;
				}
				
			}
		}

		return isUpload;
	}

	private synchronized void initizlize(StringBuffer content) {
		try {

			snaps = CommonUtil.unmarshal(content.toString(),
					SnapshotsWrapper.class);

		} catch (Exception e) {
			log.info("snaps content: " + content.toString());
			log.error("loading Snapshots failded."+e.getMessage(),e);
		}

		if (snaps == null) {
			snaps = new SnapshotsWrapper();
		}

		HashMap<VirtualMachineInfo, SortedSet<VMSnapshotsInfo>> snaps2 = snaps
				.getSnaps();
		Set<VirtualMachineInfo> keySet = snaps2.keySet();
		long max = 1;
		for (VirtualMachineInfo info : keySet) {
			SortedSet<VMSnapshotsInfo> sortedSet = snaps2.get(info);
			for (VMSnapshotsInfo vSnapInfo : sortedSet) {
				long snapNo2 = vSnapInfo.getSnapNo();
				if (max < snapNo2)
					max = snapNo2;
			}
		}
		snapNo.set(max);

	}

	private synchronized String getVMSnapshotXmlFromStorage() throws Exception{

		int retryDownloadTimes = 0;
		InputStream in = null;
		while (true) {
			try {
				
				if (null != vmMor) {
					in = vmwareOBJ.getVMConfigByMoref(vmname, vmMor,
							CommonUtil.SNAPSHOT_XML_FILE);
				} else {
					in = vmwareOBJ.getVMConfig(vmname, vmuuid,
							CommonUtil.SNAPSHOT_XML_FILE);
				}
								
				break;
				
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
				}
				
				if (++retryDownloadTimes >= 3) {
					throw e;
				}
			}
		}

		if (in == null) {
			return null;
		}

		StringBuffer snapshots = new StringBuffer();
		try {
			byte[] buffer = new byte[1024];
			int readCount = in.read(buffer);
			while (readCount != -1) {
				if (readCount == 1024) {
					snapshots.append(new String(buffer,"UTF-8"));
				} else if (readCount < 1024) {
					byte[] buffer1 = new byte[readCount];
					System.arraycopy(buffer, 0, buffer1, 0, readCount);
					snapshots.append(new String(buffer1,"UTF-8"));
				}
				readCount = in.read(buffer);
			}
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
		finally {
			if(in != null) {
				try {
					in.close();
				}catch(Exception e){}
			}
		}

		return snapshots.toString().trim();
	}

	private synchronized void putSnapShot(VirtualMachineInfo vmi,
			VMSnapshotsInfo sinfo) {
		SortedSet<VMSnapshotsInfo> sortedSet = snaps.getSnaps().get(vmi);
		if (sortedSet == null) {
			sortedSet = new TreeSet<VMSnapshotsInfo>();
			snaps.getSnaps().put(vmi, sortedSet);
		}
		sortedSet.remove(sinfo);
		sortedSet.add(sinfo);
	}

	public synchronized void setConnectionInfo(CAVirtualInfrastructureManager vmwareOBJ,
												String vmname,String vmuuid){
		
		this.vmwareOBJ = vmwareOBJ;
		this.vmname = vmname;
		this.vmuuid = vmuuid;
		
	}
	
	
	public static void main(String[] args) throws Exception {

		CAVirtualInfrastructureManager vmwareOBJ = CAVMwareInfrastructureManagerFactory
				.getCAVMwareVirtualInfrastructureManager("155.35.102.178",
						"root", "caworld", "https", true, 0);
		
		String vmname = "VM_yuver01-esxvm-3";
		String vmuuid = "52e2fe69-0657-4669-6b8e-946f62f9b40d";
		

		int retryDownloadTimes = 0;
		InputStream in = null;
		while (in == null) {
			try {
				in = vmwareOBJ.getVMConfig(vmname, vmuuid,
						CommonUtil.SNAPSHOT_XML_FILE);
				if (++retryDownloadTimes >= 3) {
					break;
				}
				if (in == null) {
					Thread.sleep(2000);
				}
			} catch (Exception e) {
				if (++retryDownloadTimes >= 3) {
					break;
				}
			}
		}
	}
}
