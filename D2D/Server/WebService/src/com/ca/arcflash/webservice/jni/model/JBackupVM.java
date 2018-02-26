package com.ca.arcflash.webservice.jni.model;

import java.util.ArrayList;
import java.util.List;


public class JBackupVM {
	
	public static final int HYPERVISOR_TYPE_VMWARE				=	0;
	public static final int HYPERVISOR_TYPE_HYPERV				=	1;
	public static final int HYPERVISOR_TYPE_HYPERV_CLUSTER		=	2;
	public static final int HYPERVISOR_TYPE_VMWARE_VAPP			=	3;
	
	private String vmName;
	private String username;
	private String password;
	private String uuid;
	private String instanceUUID;
	private String esxServerName;
	private String esxUsername;
	private String esxPassword;
	private String protocol;
	private int port;
	private String destination;
	private String vmVMX;
	private String vmHostName;
	private String desUsername;
	private String desPassword;
	private String browseDestination;
	private int hypervisorType;
	
	private List<JDisk> disks = new ArrayList<JDisk>();
	private List<JJobScriptBackupVC> vAppVCInfos = new ArrayList<JJobScriptBackupVC>();
	
	private int cpuCount;
	private long memorySize; //MB;
	private String storagePolicyId;
	private String storagePolicyName;
	private String virtualDataCenterId;
	private String virtualDataCenterName;
	private String vmxDataStoreId;
	private String vmxDataStoreName;

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEsxServerName() {
		return esxServerName;
	}

	public void setEsxServerName(String esxServerName) {
		this.esxServerName = esxServerName;
	}

	public String getEsxUsername() {
		return esxUsername;
	}

	public void setEsxUsername(String esxUsername) {
		this.esxUsername = esxUsername;
	}

	public String getEsxPassword() {
		return esxPassword;
	}

	public void setEsxPassword(String esxPassword) {
		this.esxPassword = esxPassword;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getVmVMX() {
		return vmVMX;
	}

	public void setVmVMX(String vmVMX) {
		this.vmVMX = vmVMX;
	}

	public String getVmHostName() {
		return vmHostName;
	}

	public void setVmHostName(String vmHostName) {
		this.vmHostName = vmHostName;
	}

	public String getDesUsername() {
		return desUsername;
	}

	public void setDesUsername(String desUsername) {
		this.desUsername = desUsername;
	}

	public String getDesPassword() {
		return desPassword;
	}

	public void setDesPassword(String desPassword) {
		this.desPassword = desPassword;
	}

	public List<JDisk> getDisks() {
		return disks;
	}

	public void setDisks(List<JDisk> disks) {
		this.disks = disks;
	}

	public String getInstanceUUID() {
		return instanceUUID;
	}

	public void setInstanceUUID(String instanceUUID) {
		this.instanceUUID = instanceUUID;
	}

	public String getBrowseDestination() {
		return browseDestination;
	}

	public void setBrowseDestination(String browseDestination) {
		this.browseDestination = browseDestination;
	}

	public int getHypervisorType() {
		return hypervisorType;
	}

	public void setHypervisorType(int hypervisorType) {
		this.hypervisorType = hypervisorType;
	}

	public List<JJobScriptBackupVC> getVAppVCInfos() {
		return vAppVCInfos;
	}

	public void setVAppVCInfos(List<JJobScriptBackupVC> vAppVCInfos) {
		this.vAppVCInfos = vAppVCInfos;
	}
	
	public int getCpuCount() {
		return cpuCount;
	}

	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}

	public long getMemorySize() {
		return memorySize; // MB
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

	public String getVirtualDataCenterId() {
		return virtualDataCenterId;
	}

	public void setVirtualDataCenterId(String virtualDataCenterId) {
		this.virtualDataCenterId = virtualDataCenterId;
	}

	public String getVirtualDataCenterName() {
		return virtualDataCenterName;
	}

	public void setVirtualDataCenterName(String virtualDataCenterName) {
		this.virtualDataCenterName = virtualDataCenterName;
	}

	public String getVmxDataStoreId() {
		return vmxDataStoreId;
	}

	public void setVmxDataStoreId(String vmxDataStoreId) {
		this.vmxDataStoreId = vmxDataStoreId;
	}

	public String getVmxDataStoreName() {
		return vmxDataStoreName;
	}

	public void setVmxDataStoreName(String vmxDataStoreName) {
		this.vmxDataStoreName = vmxDataStoreName;
	}
}