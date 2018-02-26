package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class DiscoveryVirtualMachineInfo implements Serializable, Comparable<DiscoveryVirtualMachineInfo> {
	
	private static final long serialVersionUID = 1284730057264944612L;
	
	private String vmName;
	private String vmUuid;
	private String vmHostName;
	private String vmInstanceUuid;
	private String vmEsxHost;//for hyper-v vm , it is hyper-v host name
	private int vmEsxSocketCount = 1;
	private boolean vmEsxEssential = false;
	private String vmXPath;
	private String vmGuestOS;
	private String vmIP;
	private boolean bRunning;
	private int vmServerType;
	private boolean windowsOS;
	private int connectionState;
	private String userName;
	private String password;
	private Boolean passwordVerified;
	private String validationResult;
	private int vmType; //0: unknown; 1: stand alone VM; 2: cluster resource VM; 3: stand alone VM, but in CSV
	private String clusterVirtualName;//for hyperv cluster
	private boolean isManagedByVCloud;
	
	public boolean isWindowsOS() {
		return windowsOS;
	}
	public void setWindowsOS(boolean windowsOS) {
		this.windowsOS = windowsOS;
	}
	/**
	 * Get the server type of the hypervisor. See getVmServerType() for value
	 * definitions.
	 * 
	 * @return
	 */
	public int getVmServerType() {
		return vmServerType;
	}
	/**
	 * Set server type of hypervisor.
	 * <p>
	 * <dl>
	 * <dt>0
	 * <dd>
	 * <dt>1
	 * <dd>ESX server
	 * <dt>2
	 * <dd>vCenter
	 * </dl>
	 * 
	 * @param vmServerType
	 */
	public void setVmServerType(int vmServerType) {
		this.vmServerType = vmServerType;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public String getVmUuid() {
		return vmUuid;
	}
	public void setVmUuid(String vmUuid) {
		this.vmUuid = vmUuid;
	}
	public String getVmHostName() {
		return vmHostName;
	}
	public void setVmHostName(String vmHostName) {
		this.vmHostName = vmHostName;
	}
	public String getVmInstanceUuid() {
		return vmInstanceUuid;
	}
	public void setVmInstanceUuid(String vmInstanceUuid) {
		this.vmInstanceUuid = vmInstanceUuid;
	}
	public String getVmEsxHost() {
		return vmEsxHost;
	}
	public void setVmEsxHost(String vmEsxHost) {
		this.vmEsxHost = vmEsxHost;
	}
	public void setVmXPath(String vmXPath) {
		this.vmXPath = vmXPath;
	}
	public String getVmXPath() {
		return vmXPath;
	}
	public String getVmGuestOS() {
		return vmGuestOS;
	}
	public void setVmGuestOS(String vmGuestOS) {
		this.vmGuestOS = vmGuestOS;
	}
	public String getVmIP() {
		return vmIP;
	}
	public void setVmIP(String vmIP) {
		this.vmIP = vmIP;
	}
	public boolean isbRunning() {
		return bRunning;
	}
	public void setbRunning(boolean bRunning) {
		this.bRunning = bRunning;
	}
	/**
	 * Get the connection sate of VM.
	 * 
	 * @return 0: connected
	 * 		   1: disconnected
	 * 		   2: orphaned
	 * 		   3: inaccessible
	 *         4: invalid
	 */
	public int getVmConnectionState() {
		return connectionState;
	}
	public void setVmConnectionState(int connectionState) {
		this.connectionState = connectionState;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean isPasswordVerified() {
		return passwordVerified;
	}
	public void setPasswordVerified(Boolean passwordVerified) {
		this.passwordVerified = passwordVerified;
	}
	public String getValidationResult() {
		return validationResult;
	}
	public void setValidationResult(String validationResult) {
		this.validationResult = validationResult;
	}
	public int getVmType() {
		return vmType;
	}
	public void setVmType(int vmType) {
		this.vmType = vmType;
	}
	public boolean isManagedByVCloud() {
		return isManagedByVCloud;
	}
	public void setManagedByVCloud(boolean isManagedByVCloud) {
		this.isManagedByVCloud = isManagedByVCloud;
	}
	public int getVmEsxSocketCount() {
		return vmEsxSocketCount;
	}
	public void setVmEsxSocketCount(int vmEsxSocketCount) {
		this.vmEsxSocketCount = vmEsxSocketCount;
	}
	public boolean isVmEsxEssential() {
		return vmEsxEssential;
	}
	public void setVmEsxEssential(boolean vmEsxEssential) {
		this.vmEsxEssential = vmEsxEssential;
	}
	public String getClusterVirtualName() {
		return clusterVirtualName;
	}
	public void setClusterVirtualName(String clusterVirtualName) {
		this.clusterVirtualName = clusterVirtualName;
	}
	@Override
	public int compareTo(DiscoveryVirtualMachineInfo o) {
		return this.vmName.compareTo(o.getVmName());
	}
}

