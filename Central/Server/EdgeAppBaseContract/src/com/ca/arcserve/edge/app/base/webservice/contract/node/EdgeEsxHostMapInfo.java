package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeEsxHostMapInfo implements Serializable{

	private static final long serialVersionUID = 969547081773931528L;
	private int esxId;
	private int hostId;
	private int status;
	private String vmName;
	private String vmUuid;
	private String vmInstanceUuid;
	private String esxHost;
	private String vmXPath;
	private String userName;
	private @NotPrintAttribute String password;
	public int getEsxId() {
		return esxId;
	}
	public void setEsxId(int esxId) {
		this.esxId = esxId;
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
	public String getEsxHost() {
		return esxHost;
	}
	public void setEsxHost(String esxHost) {
		this.esxHost = esxHost;
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
	public void setVmXPath(String vmXPath) {
		this.vmXPath = vmXPath;
	}
	public String getVmXPath() {
		return vmXPath;
	}
	
	
	
	
	
	
}
