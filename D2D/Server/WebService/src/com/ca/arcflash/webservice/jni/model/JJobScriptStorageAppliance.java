// October sprint - Aravind
package com.ca.arcflash.webservice.jni.model;
import java.util.List;

import com.ca.arcflash.common.NotPrintAttribute;

public class JJobScriptStorageAppliance {
	private String pwszNodeName;
	//Dec sprint
	private String pwszSystemMode;
	private String pwszDataIP;
	private String pwszNodeAddr;
	private String pwszUserName;
	@NotPrintAttribute
	private String pwszPassword;
	private String pwszProtocol;
	private String pwszPort;
	private List<JJobScriptNFSShare> pAFNFSShareList;
	
	public String getPwszPassword() {
		return pwszPassword;
	}
	public void setPwszPassword(String pwszPassword) {
		this.pwszPassword = pwszPassword;
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
	public String getPwszProtocol() {
		return pwszProtocol;
	}
	public void setPwszProtocol(String pwszProtocol) {
		this.pwszProtocol = pwszProtocol;
	}
	public String getPwszPort() {
		return pwszPort;
	}
	public void setPwszPort(String pwszPort) {
		this.pwszPort = pwszPort;
	}
	public List<JJobScriptNFSShare> getpAFNFSShareList() {
		return pAFNFSShareList;
	}
	public void setpAFNFSShareList(List<JJobScriptNFSShare> pAFNFSShareList) {
		this.pAFNFSShareList = pAFNFSShareList;
	}
	//Dec sprint
	public String getPwszDataIP() {
		return pwszDataIP;
	}
	public void setPwszDataIP(String pwszDataIP) {
		this.pwszDataIP = pwszDataIP;
	}
	public String getPwszSystemMode() {
		return pwszSystemMode;
	}
	public void setPwszSystemMode(String pwszSystemMode) {
		this.pwszSystemMode = pwszSystemMode;
	}

}

