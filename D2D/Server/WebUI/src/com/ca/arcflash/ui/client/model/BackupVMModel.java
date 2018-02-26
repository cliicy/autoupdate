package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupVMModel extends BaseModelData {
	private static final long serialVersionUID = -3387429102074625479L;
	
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
		return (String)get("vmName");
	}
	
	public void setUsername(String username){
		set("username",username);
	}
	
	public String getUsername(){
		return (String)get("username");
	}
	
	public void setPassword(String password){
		set("password",password);
	}
	
	public String getPassword(){
		return (String)get("password");
	}
	
	public void setDestination(String destination){
		set("destination",destination);
	}
	
	public String getDestination(){
		return (String)get("destination");
	}
	
	/**
	 * set ESX server name or vCloud name
	 */
	public void setEsxServerName(String esxServerName){
		set("esxServer",esxServerName);
	}
	
	/**
	 * get ESX server name or vCloud name//vCenter
	 */
	public String getEsxServerName(){
		return (String)get("esxServer");
	}
	
	//added for vm recovery 
	public String getSubVMEsxHost(){ //esx host 
		return (String)get("subVMEsxHost");
	}
	
	public void setSubVMEsxHost(String name){
		set("subVMEsxHost", name);
	}

	/**
	 * set password for ESX server or vCloud
	 */
	public void setEsxPassword(String esxPassword){
		set("esxPassword",esxPassword);
	}
	
	/**
	 * get password for ESX server or vCloud
	 */
	public String getEsxPassword(){
		return (String)get("esxPassword");
	}
	
	/**
	 * set user name for ESX server or vCloud
	 */
	public void setEsxUsername(String esxUsername){
		set("esxUsername",esxUsername);
	}
	
	/**
	 * get user name for ESX server or vCloud
	 */
	public String getEsxUsername(){
		return (String)get("esxUsername");
	}
	
	public void setUUID(String uuid){
		set("uuid",uuid);
	}
	
	public String getUUID(){
		return (String)get("uuid");
	}
	
	public void setVmVMX(String vmVMX){
		set("vmVMX",vmVMX);
	}
	
	public String getVmVMX(){
		return (String)get("vmVMX");
	}
	
	public void setVmHostName(String vmHostName){
		set("vmHostName",vmHostName);
	}
	
	public String getVmHostName(){
		return (String)get("vmHostName");
	}
	
	public void setDesUsername(String desUsername){
		set("desUsername",desUsername);
	}
	
	public String getDesUsername(){
		return (String)get("desUsername");
	}
	
	public void setDesPassword(String desPassword){
		set("desPassword",desPassword);
	}
	
	public String getDesPassword(){
		return (String)get("desPassword");
	}
	
	/**
	 * get protocol for ESX server or vCloud
	 */
	public String getProtocol(){
		return (String)get("protocol");
	}
	
	/**
	 * set protocol for ESX server or vCloud
	 */
	public void setProtocol(String protocol){
		set("protocol",protocol);
	}
	
	/**
	 * set port for ESX server or vCloud
	 */
	public Integer getPort(){
		return (Integer)get("port");
	}
	
	/**
	 * set port for ESX server or vCloud
	 */
	public void setPort(int port){
		set("port",port);
	}
	
	/**
	 * set VM instance UUID for vApp Id.
	 */
	public void setVmInstanceUUID(String vmInstanceUUID){
		set("vmInstanceUUID",vmInstanceUUID);
	}
	
	/**
	 * get VM instance UUID for vApp Id.
	 */
	public String getVmInstanceUUID(){
		return (String)get("vmInstanceUUID");
	}
	
	public void setBrowseDestination(String browserDes){
		set("browserDes",browserDes);
	}
	
	public String getBrowseDestination(){
		return get("browserDes");
	}
	
	public void setVMType(int vmType){
		set("vmType", vmType);
	}
	
	public int getVMType(){
		return get("vmType");
	}
	
	/**
	 * get vDC id for vCloud
	 */
	public String getVirtualDataCenterId() {
		return (String) get("vDCId");
	}

	/**
	 * set vDC id for vCloud
	 */
	public void setVirtualDataCenterId(String vDCId) {
		set("vDCId", vDCId);
	}
	
	/**
	 * get vDC name for vCloud
	 */
	public String getVirtualDataCenterName() {
		return (String) get("vDCName");
	}

	/**
	 * set vDC name for vCloud
	 */
	public void setVirtualDataCenterName(String vDCName) {
		set("vDCName", vDCName);
	}
	
	public Integer getCPUCount() {
		return (Integer) get("cpuCount");
	}
	
	public void setCPUCount(Integer cpuCount) {
		set("cpuCount", cpuCount);
	}
	
	public Integer getMaxCPUCount() {
		return (Integer) get("maxCpuCount");
	}
	
	public void setMaxCPUCount(Integer maxCpuCount) {
		set("maxCpuCount", maxCpuCount);
	}
	
	public Long getMemorySize() {
		return (Long) get("memorySize"); //MB
	}
	
	public void setMemorySize(Long memorySize) { //MB
		set("memorySize", memorySize);
	}
	
	public Long getMaxMemorySizeGB() {
		return (Long) get("maxMemorySize"); //GB
	}
	
	public void setMaxMemorySizeGB(Long maxMemorySize) { //GB
		set("maxMemorySize", maxMemorySize);
	}
	
	public String getStorageProfileName() {
		return (String) get("profileName");
	}
	
	public void setStorageProfileName(String profileName) {
		set("profileName", profileName);
	}
	
	public String getStorageProfileId() {
		return (String) get("profileId");
	}
	
	public void setStorageProfileId(String profileId) {
		set("profileId", profileId);
	}
	
	public String getVMXDataStoreId() {
		return (String) get("VMXDataStoreId");
	}
	
	public void setVMXDataStoreId(String datastoreId) {
		set("VMXDataStoreId", datastoreId);
	}
	
	public String getVMXDataStoreName() {
		return (String) get("VMXDataStoreName");
	}
	
	public void setVMXDataStoreName(String datastoreId) {
		set("VMXDataStoreName", datastoreId);
	}
	
	public String getVmIdInVApp() {
		return (String) get("vmIdInVApp");
	}
	
	public void setVmIdInVApp(String vmIdInVApp) {
		set("vmIdInVApp", vmIdInVApp);
	}
	
	public List<DiskModel> diskList;
	
	public List<VMNetworkConfigInfoModel> adapterList = new ArrayList<VMNetworkConfigInfoModel>();

	public List<BackupVMModel> memberVMList = new ArrayList<BackupVMModel>();
	
	public long flashReadCacheSize = -1;
	
	//vCenter connection for vCloud
	public List<VirtualCenterModel> vAppVCInfos = new ArrayList<VirtualCenterModel>();
	
	public enum Type {
		VMware, HyperV, HyperV_Cluster, VMware_VApp
	}
}
