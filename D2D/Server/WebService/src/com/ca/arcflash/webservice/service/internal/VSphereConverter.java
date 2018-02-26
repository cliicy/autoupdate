package com.ca.arcflash.webservice.service.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.vcloudmanager.objects.VCloudDatastore;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudNetwork;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudOrganization;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudStorageProfile;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVCenter;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVDC;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareStorage;
import com.ca.arcflash.webservice.data.ApplicationStatus;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.BackupVMOriginalInfo;
import com.ca.arcflash.webservice.data.vsphere.DataStore;
import com.ca.arcflash.webservice.data.vsphere.Disk;
import com.ca.arcflash.webservice.data.vsphere.ESXServer;
import com.ca.arcflash.webservice.data.vsphere.VCloudOrg;
import com.ca.arcflash.webservice.data.vsphere.VCloudVC;
import com.ca.arcflash.webservice.data.vsphere.VCloudVDCStorageProfile;
import com.ca.arcflash.webservice.data.vsphere.VCloudVirtualDC;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMItem;
import com.ca.arcflash.webservice.data.vsphere.VMNetworkConfig;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.data.vsphere.Volume;
import com.ca.arcflash.webservice.jni.model.JApplicationStatus;
import com.ca.arcflash.webservice.jni.model.JBackupVM;
import com.ca.arcflash.webservice.jni.model.JBackupVMOriginalInfo;
import com.ca.arcflash.webservice.jni.model.JDisk;
import com.ca.arcflash.webservice.jni.model.JJobContext;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.jni.model.JJobScriptBackupVC;
import com.ca.arcflash.webservice.jni.model.JVolume;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;

public class VSphereConverter {
	private static final Logger logger = Logger.getLogger(VSphereConverter.class);
	
	public class NameComparator<T> implements Comparator<T> {
		private boolean ignoreCase = true;
		
		public NameComparator() {
			
		}
		
		public NameComparator(boolean ignoreCase) {
			this.ignoreCase = ignoreCase;
		}
		
		@Override
		public int compare(T o1, T o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}

			try {
				Method m1 = o1.getClass().getMethod("getName");
				String result1 = (String) m1.invoke(o1);
				String result2 = (String) m1.invoke(o2);

				if (result1 == null && result2 == null) {
					return 0;
				}
				if (result1 == null) {
					return -1;
				}
				if (result2 == null) {
					return 1;
				}
				if (ignoreCase) {
					return result1.compareToIgnoreCase(result2);
				} else {
					return result1.compareTo(result2);
				}
			} catch (Throwable t) {
				logger.warn("Failed to sort in VSphereConverter$NameComparator", t);
				return 0;
			}

		}
	}
	
	public List<ESXServer> ESXNodeConverter(List<ESXNode> esxNodeList){
		List<ESXServer> serverList = new ArrayList<ESXServer>();
		for(ESXNode node : esxNodeList){
			ESXServer server = new ESXServer();
			server.setDataCenter(node.getDataCenter());
			server.setEsxName(node.getEsxName());
			serverList.add(server);
		}
		return serverList;
	}
	
	public ESXNode ESXServerToESXNode(ESXServer server){
		ESXNode node = new ESXNode();
		node.setDataCenter(server.getDataCenter());
		node.setEsxName(server.getEsxName());
		return node;
	}
	
	public List<VirtualMachine> ESXVmConverter(List<VM_Info> vmList){
		List<VirtualMachine> vMachineList = new ArrayList<VirtualMachine>();
		for(VM_Info vm : vmList){
			VirtualMachine vMachine = new VirtualMachine();
			vMachine.setState(vm.getVMPowerstate()== true ? 1:2);
			vMachine.setVmHostName(vm.getVMHostName());
			vMachine.setVmName(vm.getVMName());
			vMachine.setVmUUID(vm.getVMUUID());
			vMachine.setVmVMX(vm.getVMVMX());
			vMachine.setVmInstanceUUID(vm.getVMvmInstanceUUID());
			vMachineList.add(vMachine);
		}
		return vMachineList;
	}
	
	public VMItem VMItemConverter(ESXNode esxServer,VM_Info vm){
		VMItem vmItem = new VMItem();
		vmItem.setEsxServerName(esxServer.getEsxName());
		vmItem.setState(vm.getVMPowerstate()== true ? 1:2);
		vmItem.setVmHostName(vm.getVMHostName());
		vmItem.setVmName(vm.getVMName());
		vmItem.setVmUUID(vm.getVMUUID());
		vmItem.setVmVMX(vm.getVMVMX());
		return vmItem;
	}
	
	public VMItem VMItemConverter(BackupVM backupVM) {
		VMItem vmItem = new VMItem();
		vmItem.setVmHostName(backupVM.getVmHostName());
		vmItem.setVmName(backupVM.getVmName());
		vmItem.setVmUUID(backupVM.getUuid());
		vmItem.setVmVMX(backupVM.getVmVMX());
		vmItem.setEsxServerName(backupVM.getEsxServerName());
		vmItem.setVmInstanceUUID(backupVM.getInstanceUUID());
		vmItem.setVmType(backupVM.getVmType());
		return vmItem;
	}
	
	public VirtualMachine ConvertToVirtuaMachine(BackupVM backupVM){
		VirtualMachine vm = new VirtualMachine();
		vm.setVmUUID(backupVM.getUuid());
		vm.setVmHostName(backupVM.getVmHostName());
		vm.setVmName(backupVM.getVmName());
		vm.setVmInstanceUUID(backupVM.getInstanceUUID());
		return vm;
	}
	
	public VirtualCenter ConvertToVirtualCenter(BackupVM backupVM){
		VirtualCenter vc = new VirtualCenter();
		vc.setPassword(backupVM.getEsxPassword());
		vc.setUsername(backupVM.getEsxUsername());
		vc.setPort(backupVM.getPort());
		vc.setProtocol(backupVM.getProtocol());
		vc.setVcName(backupVM.getEsxServerName());
		return vc;
	}
	
	public VirtualCenter convertJJobScriptBackupVCToVirtualCenter(JJobScriptBackupVC jobScriptBackupVC){
		VirtualCenter vc = new VirtualCenter();
		vc.setPassword(jobScriptBackupVC.getPassword());
		vc.setUsername(jobScriptBackupVC.getUsername());
		vc.setPort(jobScriptBackupVC.getPort());
		vc.setProtocol(jobScriptBackupVC.getProtocol());
		vc.setVcName(jobScriptBackupVC.getVcName());
		return vc;
	}
	
	public BackupVM ConertToBackupVM(JBackupVM jbackupVM){
		BackupVM vmModel = new BackupVM();
		vmModel.setDesPassword(jbackupVM.getDesPassword());
		vmModel.setDestination(jbackupVM.getDestination());
		vmModel.setBrowseDestination(jbackupVM.getBrowseDestination());
		vmModel.setDesUsername(jbackupVM.getDesUsername());
		vmModel.setEsxPassword(jbackupVM.getEsxPassword());
		vmModel.setEsxServerName(jbackupVM.getEsxServerName());
		vmModel.setEsxUsername(jbackupVM.getEsxUsername());
		vmModel.setPassword(jbackupVM.getPassword());
		vmModel.setPort(jbackupVM.getPort());
		vmModel.setProtocol(jbackupVM.getProtocol());
		vmModel.setUsername(jbackupVM.getUsername());
		vmModel.setUuid(jbackupVM.getUuid());
		vmModel.setInstanceUUID(jbackupVM.getInstanceUUID());
		vmModel.setVmHostName(jbackupVM.getVmHostName());
		vmModel.setVmName(jbackupVM.getVmName());
		vmModel.setVmVMX(jbackupVM.getVmVMX());
		if (JBackupVM.HYPERVISOR_TYPE_VMWARE == jbackupVM.getHypervisorType())
			vmModel.setVmType(BackupVM.Type.VMware.ordinal());
		else if (JBackupVM.HYPERVISOR_TYPE_HYPERV == jbackupVM.getHypervisorType())
			vmModel.setVmType(BackupVM.Type.HyperV.ordinal());
		else if (JBackupVM.HYPERVISOR_TYPE_VMWARE_VAPP == jbackupVM.getHypervisorType())
			vmModel.setVmType(BackupVM.Type.VMware_VApp.ordinal());
		else if (JBackupVM.HYPERVISOR_TYPE_HYPERV_CLUSTER == jbackupVM.getHypervisorType())
			vmModel.setVmType(BackupVM.Type.HyperV_Cluster.ordinal());
		
		if(jbackupVM.getDisks()!=null&&jbackupVM.getDisks().size()>0){
			Disk[] disks = new Disk[jbackupVM.getDisks().size()];
			int i =0;
			for(JDisk disk : jbackupVM.getDisks()){
				disks[i++]= ConvertToDisk(disk);
			}
			vmModel.setDisks(disks);
		}
		
		if (jbackupVM.getVAppVCInfos() != null && jbackupVM.getVAppVCInfos().size() > 0) {
			VirtualCenter[] vAppVCInfos = new VirtualCenter[jbackupVM.getVAppVCInfos().size()];
			int i =0;
			for(JJobScriptBackupVC vc : jbackupVM.getVAppVCInfos()){
				vAppVCInfos[i++]= convertJJobScriptBackupVCToVirtualCenter(vc);
			}
			vmModel.setVAppVCInfos(vAppVCInfos);
		}
		
		vmModel.setCpuCount(jbackupVM.getCpuCount());
		vmModel.setMemorySize(jbackupVM.getMemorySize());
		vmModel.setStoragePolicyId(jbackupVM.getStoragePolicyId());
		vmModel.setStoragePolicyName(jbackupVM.getStoragePolicyName());
		vmModel.setVirtualDataCenterId(jbackupVM.getVirtualDataCenterId());
		vmModel.setVirtualDataCenterName(jbackupVM.getVirtualDataCenterName());
		vmModel.setVmxDataStoreId(jbackupVM.getVmxDataStoreId());
		vmModel.setVmxDataStoreName(jbackupVM.getVmxDataStoreName());
		
		return vmModel;
	}
	
	private BackupVMOriginalInfo ConvertToBackupVMOriginalInfo(JBackupVMOriginalInfo originalInfo){
		if(originalInfo == null){
			return null;
		}
		BackupVMOriginalInfo info = new BackupVMOriginalInfo();
		info.setOriginalEsxServer(originalInfo.getOriginalEsxServer());
		info.setOriginalVcName(originalInfo.getOriginalVcName());
		info.setOriginalResourcePool(originalInfo.getOriginalResourcePool());
		return info;
		
	}
	
	public Disk ConvertToDisk(JDisk disk){
		if(disk==null){
			return null;
		}
		Disk diskModel = new Disk();
		diskModel.setControllerType(disk.getControllerType());
		diskModel.setDiskNumber(disk.getDiskNumber());
		diskModel.setDiskType(disk.getDiskType());
		diskModel.setPartitionType(disk.getPartitionType());
		diskModel.setSignature(disk.getSignature());
		diskModel.setSize(disk.getSize());
		diskModel.setDiskUrl(disk.getDiskUrl());
		diskModel.setDiskDataStore(disk.getDiskDataStore());
		if(disk.getVolumes()!=null&&disk.getVolumes().size()>0){
			Volume[] volumes = new Volume[disk.getVolumes().size()];
			int i = 0;
			for(JVolume volume : disk.getVolumes()){
				volumes[i++] = ConvertToVolume(volume);
			}
			diskModel.setVolumes(volumes);
		}
		return diskModel;
	}
	
	private Volume ConvertToVolume(JVolume volume){
		if(volume==null){
			return null;
		}
		Volume volumeModel = new Volume();
		volumeModel.setDriverLetter(volume.getDriverLetter());
		volumeModel.setVolumeID(volume.getVolumeID());
		return volumeModel;
	}
	
	public DataStore ConvertToDataStore(VMwareStorage vmStorage){
		if(vmStorage == null){
			return null;
		}
		DataStore dataStore = new DataStore();
		dataStore.setName(vmStorage.getName());
		dataStore.setFreeSize(vmStorage.getFreeSize());
		dataStore.setTotalSize(vmStorage.getTotalSize());
		dataStore.setOtherSize(vmStorage.getOtherSize());
		dataStore.setAccessible(vmStorage.isAccessible());
		return dataStore;
	}

	public VSphereJobContext ConvertToVSphereJobContext(JJobContext jJobContext){
		VSphereJobContext context = new VSphereJobContext();
		context.setExecuterInstanceUUID(jJobContext.getExecuterInstanceUUID());
		context.setLauncherInstanceUUID(jJobContext.getLauncherInstanceUUID());
		context.setJobId(jJobContext.getDwJobId());
		context.setPriority((int)jJobContext.getDwPriority());
		context.setJobLauncher((int)jJobContext.getDwLauncher());
		context.setJobType((int)jJobContext.getDwJobType());
		context.setMasterJobId(jJobContext.getDwMasterJobId());
		
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(jJobContext.getLauncherInstanceUUID());
		VMBackupConfiguration conf = null;
		try {
			conf = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if(conf != null && conf.getBackupVM() != null) {
				context.setVmName(conf.getBackupVM().getVmName());
			}
		}catch(ServiceException se) {
			
		}
		
		return context;
	}
	
	public VSphereJobContext ConvertToVSphereJobContext(JJobScript jJobScript){
		VSphereJobContext context = new VSphereJobContext();
		context.setExecuterInstanceUUID(jJobScript.getLauncherInstanceUUID());
		context.setLauncherInstanceUUID(jJobScript.getLauncherInstanceUUID());
		context.setJobId(jJobScript.getUlJobID());
		context.setJobType((int)jJobScript.getUsJobType());
		context.setMasterJobId(jJobScript.getDwMasterJobId());
		
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(jJobScript.getLauncherInstanceUUID());
		VMBackupConfiguration conf = null;
		try {
			conf = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if(conf != null && conf.getBackupVM() != null) {
				context.setVmName(conf.getBackupVM().getVmName());
			}
		}catch(ServiceException se) {
			
		}
		
		return context;
	}
	
	public ApplicationStatus ConvertToApplicationStatus(JApplicationStatus appStatus){
		if(appStatus == null){
			return null;
		}
		ApplicationStatus status = new ApplicationStatus();
		status.setArcserveInstalled(appStatus.isArcserveInstalled());
		status.setD2dInstalled(appStatus.isD2dInstalled());
		status.setExchangeInstalled(appStatus.isExchangeInstalled());
		status.setSqlInstalled(appStatus.isSqlInstalled());
		status.setOSVersion(appStatus.getOSVersion());
		return status;
	}
	
	public VCloudOrg convertVCouldOrganization2VCloudOrg(VCloudOrganization org) {
		if (org == null) {
			return null;
		}
		
		VCloudOrg resultOrg = new VCloudOrg();
		resultOrg.setName(org.getName());
		resultOrg.setId(org.getId());
		resultOrg.setFullName(org.getFullName());
		
		return resultOrg;
	}
	
	public VCloudVirtualDC convertVCouldVDC2VCloudVirtualDC(VCloudVDC vDC) {
		if (vDC == null) {
			return null;
		}
		
		VCloudVirtualDC resultVDC = new VCloudVirtualDC();
		resultVDC.setName(vDC.getName());
		resultVDC.setId(vDC.getId());
		resultVDC.setAllocationModel(vDC.getAllocationModel());
		resultVDC.setUnitOfMemory(vDC.getUnitOfMemory());
		resultVDC.setMemoryLimit(vDC.getMemoryLimit());
		resultVDC.setAvailableNetworks(convertVCloudNetworkList2VMNetworkConfigList(null, vDC.getVdcNetworks()));
		resultVDC.setStorageProfiles(convert2VCloudVDCStorageProfileList(vDC.getStorageProfiles(), vDC.getStorageProfileIds()));
		resultVDC.setSupportedHardwareVersions(vDC.getSupportedHardwareVersions());
		resultVDC.setCpuCount(vDC.getTotalCpusLogical());
		
		return resultVDC;
	}
	
	private List<VCloudVDCStorageProfile> convert2VCloudVDCStorageProfileList(List<String> profileNameList, List<String> profileIdList) {
		List<VCloudVDCStorageProfile> resultList = new ArrayList<>();
		if (profileNameList == null || profileNameList.isEmpty() || profileIdList == null || profileIdList.isEmpty()
				|| profileNameList.size() != profileIdList.size()) {
			return resultList;
		}
		
		int size = profileNameList.size();
		for (int index = 0; index < size; index++) {
			VCloudVDCStorageProfile profile = new VCloudVDCStorageProfile();
			profile.setName(profileNameList.get(index));
			profile.setId(profileIdList.get(index));
			
			resultList.add(profile);
		}
		
		Collections.sort(resultList, new NameComparator<VCloudVDCStorageProfile>());
		return resultList;
	}
	
	private VMNetworkConfig convertVCloudNetwork2VMNetworkConfig(String vAppName, VCloudNetwork vCloudNetwork) {
		if (vCloudNetwork == null) {
			return null;
		}
		
		VMNetworkConfig vmNetwork = new VMNetworkConfig();
		vmNetwork.setLabel(vCloudNetwork.getName());
		vmNetwork.setNodeName(vAppName);
		vmNetwork.setId(vCloudNetwork.getId());
		vmNetwork.setAdapterType(vCloudNetwork.getNetworkAdapterType());
		vmNetwork.setParentName(vCloudNetwork.getParentNetwork());
		vmNetwork.setParentId(vCloudNetwork.getParentNetworkId());
		
		return vmNetwork;
	}
	
	public List<VMNetworkConfig> convertVCloudNetworkList2VMNetworkConfigList(String vAppName, List<VCloudNetwork> vCloudNetworkList) {
		List<VMNetworkConfig> resultList = new ArrayList<>();
		if (vCloudNetworkList == null || vCloudNetworkList.isEmpty()) {
			return resultList;
		}
		
		for (VCloudNetwork vCloudNetwork : vCloudNetworkList) {
			VMNetworkConfig vmNetwork = convertVCloudNetwork2VMNetworkConfig(vAppName, vCloudNetwork);
			if (vmNetwork != null) {
				resultList.add(vmNetwork);
			}
		}
		
		Collections.sort(resultList, new Comparator<VMNetworkConfig>() {
			@Override
			public int compare(VMNetworkConfig o1, VMNetworkConfig o2) {
				if (o1 == null && o2 == null) {
					return 0;
				} 
				if (o1 == null) {
					return -1;
				}
				if (o2 == null) {
					return 1;
				}
				
				String name1 = o1.getLabel();
				if (name1 == null) {
					return -1;
				}
				return name1.compareToIgnoreCase(o2.getLabel());
			}
		});
		return resultList;
	}
	
	public VCloudVC convertVCloudVCenter2VCloudVC(VCloudVCenter vCenter) {
		if (vCenter == null) {
			return null;
		}

		VCloudVC vc = new VCloudVC();
		vc.setId(vCenter.getId());
		vc.setName(vCenter.getName());
		vc.setUsername(vCenter.getUsername());

		String url = vCenter.getUrl();
		String protocol = "HTTPS";
		int port = 443;
		if (url != null && !url.trim().isEmpty()) {
			String[] parts = url.split(":");
			if (parts != null && parts.length == 3) {
				protocol = parts[0];
				try {
					port = Integer.parseInt(parts[2]);
				} catch (Exception e) {
					if ("HTTP".equalsIgnoreCase(protocol)) {
						port = 80;
					} else {
						protocol = "HTTPS";
						port = 443;
					}
				}
			}
		}
		vc.setHostname(vCenter.getName());
		vc.setProtocol(protocol);
		vc.setPort(port);

		return vc;
	}

	public VCloudVDCStorageProfile convertVCloudStorageProfile2VCloudVDCStorageProfile(VCloudStorageProfile profile) {
		if (profile == null) {
			return null;
		}
		
		VCloudVDCStorageProfile result = new VCloudVDCStorageProfile();
		result.setName(profile.getName());
		result.setId(profile.getId());
		result.setUnitsOfLimit(profile.getUnitsOfLimit());
		result.setLimit(profile.getLimit());
		result.setRequested(profile.getRequested());
		result.setUnit(profile.getUnitsOfCapacity());
		result.setTotalCapacity(profile.getCapacityTotal());
		result.setUsedCapacity(profile.getCapacityUsed());
		
		List<VCloudDatastore> vCloudDatastoreList = profile.getDatastores();
		List<DataStore> storages = new ArrayList<DataStore>();
		result.setStorages(storages);
		
		if (vCloudDatastoreList != null && !vCloudDatastoreList.isEmpty()) {
			for (VCloudDatastore vCloudDatastore : vCloudDatastoreList) {
				DataStore storage = convertVCloudDatastore2DataStore(vCloudDatastore);
				if (storage != null) {
					storages.add(storage);
				}
			}
		}
		
		Collections.sort(storages, new NameComparator<DataStore>());
		return result;
	}
	
	private DataStore convertVCloudDatastore2DataStore(VCloudDatastore vCloudDatastore) {
		if (vCloudDatastore == null) {
			return null;
		}
		
		DataStore result = new DataStore();
		result.setName(vCloudDatastore.getName());
		result.setId(vCloudDatastore.getId());
		result.setFileType(vCloudDatastore.getDatastoreFsType());
		result.setAccessible(true);
		result.setMoRef(vCloudDatastore.getMoRef());
		
		long totalSize = vCloudDatastore.getTotalCapacityMb();
		long usedSize = vCloudDatastore.getUsedCapacityMb();
		long freeSize = totalSize - usedSize;
		if (freeSize < 0){
			freeSize = 0;
		}
		
		long scale = 1024 * 1024;
		result.setTotalSize(totalSize * scale);
		result.setFreeSize(freeSize * scale);
		result.setOtherSize(usedSize * scale);
		
		return result;
	}
}