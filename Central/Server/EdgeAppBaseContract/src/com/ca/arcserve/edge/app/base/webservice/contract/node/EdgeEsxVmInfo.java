package com.ca.arcserve.edge.app.base.webservice.contract.node;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeEsxVmInfo {
	private int esxId;
	private int hostId;
	private int serverType;
	private int status;
	private String hostName;
	private String userName;
	private @NotPrintAttribute String password;
	private int protocol;
	private int port;
	private String vmName;
	private String vmUuid;
	private String vmInstanceUuid;
	private String esxHost;
	private String vmXPath;
	private int isVisible=0;
	
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
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	@EncryptSave
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getServerType() {
		return serverType;
	}
	public void setServerType(int serverType) {
		this.serverType = serverType;
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
	public String getEsxHost() {
		return esxHost;
	}
	public void setEsxHost(String esxHost) {
		this.esxHost = esxHost;
	}
	public String getVmXPath() {
		return vmXPath;
	}
	public void setVmXPath(String vmXPath) {
		this.vmXPath = vmXPath;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getIsVisible() {
		return isVisible;
	}
	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}	
}
