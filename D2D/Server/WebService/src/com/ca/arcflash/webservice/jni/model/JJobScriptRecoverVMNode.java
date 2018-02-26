package com.ca.arcflash.webservice.jni.model;

import java.util.List;

import com.ca.arcflash.common.NotPrintAttribute;

public class JJobScriptRecoverVMNode {
	private long jobId;
	private String pwszNodeName;
	private String pwszNodeAddr;
	private String pwszUserName;
	@NotPrintAttribute
	private String pwszUserPW;
	private String pwszSessPath;
	private int ulSessNum;
	private int nVolumeApp;
	
	private JJobScriptBackupVC vc;
	private String vmName;
	private String vmUsername;
	private String vmPassword;
	private String esxServerName;
	private int originalLocation;
	private int poweronAfterRestore;
	private int overwriteExistingVM;
	private int generateNewVMID;//<huvfe01>###
	private String vcName;
	private int vmDiskCount;
	private String vmDataStore;
	private String vmDataStoreId;
	private String encryptionPassword;
	private String resourcePoolName;
	private List<JJobScriptDiskDataStore> diskDataStore;
	private List<JJobScriptVMNetworkConfigInfo> vmNetworkConfig;
	private int nFilterItems;
	private int fOptions;
	
	private int cpuCount;
	private long memorySize; //MB;
	private String storagePolicyId;
	private String storagePolicyName;
	private String vmIdInVApp;
	private List<JJobScriptRecoverVMNode> childVMNodeList;
	private String networkMappingInfo;

	public long getJobId() {
		return jobId;
	}
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
	public String getPwszNodeName() {
		return pwszNodeName;
	}
	public void setPwszNodeName(String pwszNodeName) {
		this.pwszNodeName = pwszNodeName;
	}
	public String getPwszNodeAddr() {
		return pwszNodeAddr;
	}
	public void setPwszNodeAddr(String pwszNodeAddr) {
		this.pwszNodeAddr = pwszNodeAddr;
	}
	public String getPwszUserName() {
		return pwszUserName;
	}
	public void setPwszUserName(String pwszUserName) {
		this.pwszUserName = pwszUserName;
	}
	public String getPwszUserPW() {
		return pwszUserPW;
	}
	public void setPwszUserPW(String pwszUserPW) {
		this.pwszUserPW = pwszUserPW;
	}
	public String getPwszSessPath() {
		return pwszSessPath;
	}
	public void setPwszSessPath(String pwszSessPath) {
		this.pwszSessPath = pwszSessPath;
	}
	public int getUlSessNum() {
		return ulSessNum;
	}
	public void setUlSessNum(int ulSessNum) {
		this.ulSessNum = ulSessNum;
	}
	public int getNVolumeApp() {
		return nVolumeApp;
	}
	public void setNVolumeApp(int volumeApp) {
		nVolumeApp = volumeApp;
	}
	
	public int getNFilterItems() {
		return nFilterItems;
	}
	public void setNFilterItems(int filterItems) {
		nFilterItems = filterItems;
	}
	public int getFOptions() {
		return fOptions;
	}
	public void setFOptions(int options) {
		fOptions = options;
	}
	public JJobScriptBackupVC getVc() {
		return vc;
	}
	public void setVc(JJobScriptBackupVC vc) {
		this.vc = vc;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public List<JJobScriptDiskDataStore> getDiskDataStore() {
		return diskDataStore;
	}
	public void setVMNetworkConfig(List<JJobScriptVMNetworkConfigInfo> vmNetworkConfig) {
		this.vmNetworkConfig = vmNetworkConfig;
	}
	public List<JJobScriptVMNetworkConfigInfo> getVMNetworkConfig() {
		return vmNetworkConfig;
	}
	public void setDiskDataStore(List<JJobScriptDiskDataStore> diskDataStore) {
		this.diskDataStore = diskDataStore;
	}
	public String getEsxServerName() {
		return esxServerName;
	}
	public void setEsxServerName(String esxServerName) {
		this.esxServerName = esxServerName;
	}
	
	public int getOriginalLocation() {
		return originalLocation;
	}
	public void setOriginalLocation(int originalLocation) {
		this.originalLocation = originalLocation;
	}
	public int getPoweronAfterRestore() {
		return poweronAfterRestore;
	}
	public void setPoweronAfterRestore(int poweronAfterRestore) {
		this.poweronAfterRestore = poweronAfterRestore;
	}
	public int getOverwriteExistingVM() {
		return overwriteExistingVM;
	}
	public void setOverwriteExistingVM(int overwriteExistingVM) {
		this.overwriteExistingVM = overwriteExistingVM;
	}
	public String getVmDataStore() {
		return vmDataStore;
	}
	public void setVmDataStore(String vmDataStore) {
		this.vmDataStore = vmDataStore;
	}
	public String getVmDataStoreId() {
		return vmDataStoreId;
	}
	public void setVmDataStoreId(String vmDataStoreId) {
		this.vmDataStoreId = vmDataStoreId;
	}
	public String getVcName() {
		return vcName;
	}
	public void setVcName(String vcName) {
		this.vcName = vcName;
	}
	public int getVmDiskCount() {
		return vmDiskCount;
	}
	public void setVmDiskCount(int vmDiskCount) {
		this.vmDiskCount = vmDiskCount;
	}
	public String getEncryptionPassword() {
		return encryptionPassword;
	}
	public void setEncryptionPassword(String encryptionPassword) {
		this.encryptionPassword = encryptionPassword;
	}
	public String getResourcePoolName() {
		return resourcePoolName;
	}
	public void setResourcePoolName(String resourcePoolName) {
		this.resourcePoolName = resourcePoolName;
	}
	public String getVmUsername() {
		return vmUsername;
	}
	public void setVmUsername(String vmUsername) {
		this.vmUsername = vmUsername;
	}
	public String getVmPassword() {
		return vmPassword;
	}
	public void setVmPassword(String vmPassword) {
		this.vmPassword = vmPassword;
	}
	public int getCpuCount() {
		return cpuCount;
	}
	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}
	public long getMemorySize() {
		return memorySize; //MB
	}
	public void setMemorySize(long memorySize) {
		this.memorySize = memorySize; //MB
	}
	public String getStoragePolicyId() {
		return storagePolicyId;
	}
	public void setStoragePolicyId(String storagePolicyId) {
		this.storagePolicyId = storagePolicyId;
	}
	public String getStoragePolicyName() {
		return storagePolicyName;
	}
	public void setStoragePolicyName(String storagePolicyName) {
		this.storagePolicyName = storagePolicyName;
	}
	public String getVmIdInVApp() {
		return vmIdInVApp;
	}
	public void setVmIdInVApp(String vmIdInVApp) {
		this.vmIdInVApp = vmIdInVApp;
	}
	public List<JJobScriptRecoverVMNode> getChildVMNodeList() {
		return childVMNodeList;
	}
	public void setChildVMNodeList(List<JJobScriptRecoverVMNode> childVMNodeList) {
		this.childVMNodeList = childVMNodeList;
	}
	public String getNetworkMappingInfo() {
		return networkMappingInfo;
	}
	public void setNetworkMappingInfo(String networkMappingInfo) {
		this.networkMappingInfo = networkMappingInfo;
	}
	public int getGenerateNewVMID() {
		return generateNewVMID;
	}
	public void setGenerateNewVMID(int generateNewVMID) {
		this.generateNewVMID = generateNewVMID;
	}
}