package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RecoverVMOptionModel extends BaseModelData {
	
	public void setOriginalLocation(Boolean originalLocation){
		set("originalLocation",originalLocation);
	}
	
	public Boolean getOriginalLocation(){
		return get("originalLocation");
	}
	
	public void setOverwriteExistingVM(Boolean overwriteExistingVM){
		set("overwriteExistingVM",overwriteExistingVM);
	}
	
	public Boolean isOverwriteExistingVM(){
		return get("overwriteExistingVM");
	}
	
	//<huvfe01>###
	public void setGenerateNewInstVMID(Boolean generateNewInstVMID){
		set("generateNewInstVMID",generateNewInstVMID);
	}
	
	public Boolean isGenerateNewInstVMID(){
		return get("generateNewInstVMID");
	}
	
	public void setPowerOnAfterRestore(Boolean powerOnAfterRestore){
		set("powerOnAfterRestore",powerOnAfterRestore);
	}
	
	public Boolean isPowerOnAfterRestore(){
		return get("powerOnAfterRestore");
	}
	
	/**
	 * set ESX/vCenter or vCloud connection information
	 */
	public void setVCModel(VirtualCenterModel vcModel){
		set("vc",vcModel);
	}
	
	/**
	 * get ESX/vCenter or vCloud connection information
	 */
	public VirtualCenterModel getVCModel(){
		return get("vc");
	}
	
	/**
	 * set VM name or vApp name
	 */
	public void setVMName(String vmName){
		set("vmName",vmName);
	}
	
	/**
	 * get VM name or vApp name
	 */
	public String getVMName(){
		return get("vmName");
	}
	
	public void setVMUsername(String vmUsername){
		set("vmUsername",vmUsername);
	}
	
	public String getVMUsername(){
		return get("vmUsername");
	}
	
	public void setVMPassword(String vmPassword){
		set("vmPassword",vmPassword);
	}
	
	public String getVMPassword(){
		return get("vmPassword");
	}
	
	/**
	 * Set ESX server name or virtual data center id.
	 */
	public void setESXServerName(String esxServerName){
		set("esxServerName",esxServerName);
	}
	
	/**
	 * get ESX server name or virtual data center id.
	 */
	public String getESXServerName(){
		return get("esxServerName");
	}
	
	public void setDiskDataStore(List<DiskDataStoreModel> diskDataStoreList){
		set("diskDataStore",diskDataStoreList);
	}
	
	public List<DiskDataStoreModel> getDiskDataStore(){
		return get("diskDataStore");
	}
	
	public void setNetworkConfigInfo(List<VMNetworkConfigInfoModel> networkConfigList){
		set("vmNetWorkConfigInfo", networkConfigList);
	}
	
	public List<VMNetworkConfigInfoModel> getVMNetworkConfigInfoList() {
		return get("vmNetWorkConfigInfo");
	}
	public void setSessionNumber(int sessionNumber){
		set("sessionNumber",sessionNumber);
	}
	
	public Integer getSesstionNumber(){
		return get("sessionNumber");
	}
	public void setVMDataStore(String dataStore){
		set("vmDataStore",dataStore);
	}
	
	public String getVmDataStore(){
		return get("vmDataStore");
	}
	
	public void setVMDataStoreId(String dataStoreId){
		set("vmDataStoreId",dataStoreId);
	}
	
	public String getVmDataStoreId(){
		return get("vmDataStoreId");
	}
	
	public void setVmDiskCount(int vmDiskCount){
		set("vmDiskCount",vmDiskCount);
	}
	
	public Integer getVmDiskCount(){
		return get("vmDiskCount");
	}
	
	/**
	 * set vCenter name or vCloud name.
	 */
	public void setVcName(String vcName){
		set("vcName",vcName);
	}
	
	/**
	 * get vCenter name or vCloud name.
	 */
	public String getVcName(){
		return get("vcName");
	}
	
	public void setVMUUID(String uuid){
		set("uuid",uuid);
	}
	
	public String getVMUUID(){
		return get("uuid");
	}
	
	/**
	 * set VM instance UUID or vApp Id
	 */
	public void setVMInstanceUUID(String instanceUUID){
		set("instanceUUID",instanceUUID);
	}
	
	/**
	 * get VM instance UUID or vApp Id
	 */
	public String getVMInstanceUUID(){
		return get("instanceUUID");
	}
	
	public void setVmIdInVApp(String vmIdInVApp) {
		set("vmIdInVApp", vmIdInVApp);
	}
	
	public String getVmIdInVApp() {
		return (String) get("vmIdInVApp");
	}
	
	public void setVmVMX(String vmVMX){
		set("vmVMX",vmVMX);
	}
	
	public String getVmVMX(){
		return get("vmVMX");
	}
	
	public String getEncryptPassword() {
		return get("encryptPassword");
	}
	
	public void setEncryptPassword(String encryptPassword) {
		set("encryptPassword", encryptPassword);
	}
	
	public void setResourcePoolName(String resourcePoolName){
		set("resourcePoolName",resourcePoolName);
	}
	
	public String getResourcePoolName(){
		return get("resourcePoolName");
	}
	
	public void setResourcePoolShowName(String resourcePoolShowName){
		set("resourcePoolShowName",resourcePoolShowName);
	}
	
	public String getResourcePoolShowName(){
		return get("resourcePoolShowName");
	}
	
	public void setRegisterAsClusterHyperVVM(Boolean registerAsClusterHyperVVM){
		set("registerAsClusterHyperVVM",registerAsClusterHyperVVM);
	}
	
	public Boolean isRegisterAsClusterHyperVVM(){
		return get("registerAsClusterHyperVVM");
	}
	
	/**
	 * set virtual data center name for vApp.
	 */
	public void setVirtualDataCenterName(String vDCName) {
		set("vDCName", vDCName);
	}
	
	/**
	 * get virtual data center name for vApp.
	 */
	public String getVirtualDataCenterName() {
		return (String) get("vDCName");
	}
	
	public void setCPUCount(Integer cpuCount) {
		set("cpuCount", cpuCount);
	}
	
	public Integer getCPUCount() {
		return (Integer) get("cpuCount");
	}
	
	public void setMemorySize(Long memorySize) {
		set("memorySize", memorySize); //MB
	}
	
	public Long getMemorySize() {
		return (Long) get("memorySize"); //MB
	}
	
	public void setStorageProfileName(String profileName) {
		set("profileName", profileName);
	}
	
	public String getStorageProfileName() {
		return (String) get("profileName");
	}
	
	public void setStorageProfileId(String profileId) {
		set("profileId", profileId);
	}
	
	public String getStorageProfileId() {
		return (String) get("profileId");
	}
}
