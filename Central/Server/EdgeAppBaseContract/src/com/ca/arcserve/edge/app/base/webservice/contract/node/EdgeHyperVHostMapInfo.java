package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeHyperVHostMapInfo implements Serializable{
	private static final long serialVersionUID = 1761409823535641500L;
	private int hypervId;
	private int clusterId;
	private int hostId;
	private int status;
	private String vmName;
	private String hypervHost;
	private int isVisible = 0;
	
	public String getHypervHost() {
		return hypervHost;
	}
	public void setHypervHost(String hypervHost) {
		this.hypervHost = hypervHost;
	}
	private String vmUuid;
	private String vmInstanceUuid;
	private String userName;
	private @NotPrintAttribute String password;
	public int getHyperVId() {
		return hypervId;
	}
	public void setHyperVId(int hypervId) {
		this.hypervId = hypervId;
	}
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@EncryptSave
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getVmUuid() {
		return vmUuid;
	}
	public void setVmUuid(String vmUuid) {
		this.vmUuid = vmUuid;
	}
	public String getVmInstanceUuid() {
		return vmInstanceUuid;
	}
	public void setVmInstanceUuid(String vmInstanceUuid) {
		this.vmInstanceUuid = vmInstanceUuid;
	}
	public int getClusterId() {
		return clusterId;
	}
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}
	public int getIsVisible() {
		return isVisible;
	}
	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}	
}
