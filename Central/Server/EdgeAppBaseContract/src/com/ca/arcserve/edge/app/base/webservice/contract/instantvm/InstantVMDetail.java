package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;


public class InstantVMDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int SOURCE_TYPE_RPS 			= 1;
	public static final int SOURCE_TYPE_SHARED_FOLDER   = 2;
	
	public static final int HYPERVISOR_TYPE_UNKNOWN 	= 0;
	public static final int HYPERVISOR_TYPE_VMWARE 		= 1;
	public static final int HYPERVISOR_TYPE_HYPERV   	= 2;
	
	private int sourceType=SOURCE_TYPE_RPS;
	private int rpsServerId;
	private String rpsServer;
	private String dataStoreUuid;
	private String dataStore;
	private int sharedFolderId;
	private String sharedFolder;
	private int hypervisorType;
	private String hypervisor; //vCenter, ESX or Hyper-v
	private boolean vCenter = false;
	private VMWareInfoForIVM vmWareInfo;
	private VMInfoInCPM vmInfo;
//	private String vmFilePath; // VM Files Path on Recovery Server
//	private long cpuCount;
//	private long memorySize;
//	private List<AdapterForInstantVM> adapterSetting;
	private long vmFreeSpace;
	private long vmFullSpace;
	
	public int getSourceType() {
		return sourceType;
	}
	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
	public int getRpsServerId() {
		return rpsServerId;
	}
	public void setRpsServerId(int rpsServerId) {
		this.rpsServerId = rpsServerId;
	}
	public String getRpsServer() {
		return rpsServer;
	}
	public void setRpsServer(String rpsServer) {
		this.rpsServer = rpsServer;
	}
	public String getDataStoreUuid() {
		return dataStoreUuid;
	}
	public void setDataStoreUuid(String dataStoreUuid) {
		this.dataStoreUuid = dataStoreUuid;
	}
	public String getDataStore() {
		return dataStore;
	}
	public void setDataStore(String dataStore) {
		this.dataStore = dataStore;
	}
	public int getSharedFolderId() {
		return sharedFolderId;
	}
	public void setSharedFolderId(int sharedFolderId) {
		this.sharedFolderId = sharedFolderId;
	}
	public String getSharedFolder() {
		return sharedFolder;
	}
	public void setSharedFolder(String sharedFolder) {
		this.sharedFolder = sharedFolder;
	}

	public int getHypervisorType() {
		return hypervisorType;
	}
	public void setHypervisorType(int hypervisorType) {
		this.hypervisorType = hypervisorType;
	}
	public String getHypervisor() {
		return hypervisor;
	}
	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}
	public boolean isvCenter() {
		return vCenter;
	}
	public void setvCenter(boolean vCenter) {
		this.vCenter = vCenter;
	}
	public VMWareInfoForIVM getVmWareInfo() {
		return vmWareInfo;
	}
	public void setVmWareInfo(VMWareInfoForIVM vmWareInfo) {
		this.vmWareInfo = vmWareInfo;
	}
	public VMInfoInCPM getVmInfo() {
		return vmInfo;
	}
	public void setVmInfo(VMInfoInCPM vmInfo) {
		this.vmInfo = vmInfo;
	}
//	public String getVmFilePath() {
//		return vmFilePath;
//	}
//	public void setVmFilePath(String vmFilePath) {
//		this.vmFilePath = vmFilePath;
//	}
//	public long getCpuCount() {
//		return cpuCount;
//	}
//	public void setCpuCount(long cpuCount) {
//		this.cpuCount = cpuCount;
//	}
//	public long getMemorySize() {
//		return memorySize;
//	}
//	public void setMemorySize(long memorySize) {
//		this.memorySize = memorySize;
//	}
//	public List<AdapterForInstantVM> getAdapterSetting() {
//		return adapterSetting;
//	}
//	public void setAdapterSetting(List<AdapterForInstantVM> adapterSetting) {
//		this.adapterSetting = adapterSetting;
//	}
	public long getVmFreeSpace() {
		return vmFreeSpace;
	}
	public void setVmFreeSpace(long vmFreeSpace) {
		this.vmFreeSpace = vmFreeSpace;
	}
	public long getVmFullSpace() {
		return vmFullSpace;
	}
	public void setVmFullSpace(long vmFullSpace) {
		this.vmFullSpace = vmFullSpace;
	}
}
