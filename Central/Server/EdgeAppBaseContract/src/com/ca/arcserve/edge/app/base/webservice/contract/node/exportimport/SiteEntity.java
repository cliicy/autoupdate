package com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class SiteEntity implements Serializable{

	private static final long serialVersionUID = -3738307523441315094L;
	//as_edge_gateway
	private int gatewayId;
	private String gatewayName;
	private String gatewayUuid;
	private String gatewayHostUuid;
	private String gatewayHostName;
	private int isLocal;
	private Date lastContactTime;
	private int heartBeatInterval;
	private Date gatewayCreateTime;
	private Date updateTime;
	private String consoleHostName;
	private int consoleProtocol;
	private int consolePort;
	private int gatewayProtocol;
	private int gatewayPort;
	private String gatewayUserName;
	@NotPrintAttribute
	private String gatewayPassword;
	private String registrationText; //should be generated new registrationText, so not to export it
	
	//as_edge_site
	private String siteName;
	private String siteDescription;
	private Date siteCreateTime;
	private Date siteUpdateTime;
	private String address;
	private String email;
	
	public int getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public String getGatewayUuid() {
		return gatewayUuid;
	}
	public void setGatewayUuid(String gatewayUuid) {
		this.gatewayUuid = gatewayUuid;
	}
	public String getGatewayHostUuid() {
		return gatewayHostUuid;
	}
	public void setGatewayHostUuid(String gatewayHostUuid) {
		this.gatewayHostUuid = gatewayHostUuid;
	}
	public String getGatewayHostName() {
		return gatewayHostName;
	}
	public void setGatewayHostName(String gatewayHostName) {
		this.gatewayHostName = gatewayHostName;
	}
	public int getIsLocal() {
		return isLocal;
	}
	public void setIsLocal(int isLocal) {
		this.isLocal = isLocal;
	}
	public int getHeartBeatInterval() {
		return heartBeatInterval;
	}
	public void setHeartBeatInterval(int heartBeatInterval) {
		this.heartBeatInterval = heartBeatInterval;
	}
	public Date getGatewayCreateTime() {
		return gatewayCreateTime;
	}
	public void setGatewayCreateTime(Date gatewayCreateTime) {
		this.gatewayCreateTime = gatewayCreateTime;
	}
	public String getConsoleHostName() {
		return consoleHostName;
	}
	public void setConsoleHostName(String consoleHostName) {
		this.consoleHostName = consoleHostName;
	}
	public int getConsoleProtocol() {
		return consoleProtocol;
	}
	public void setConsoleProtocol(int consoleProtocol) {
		this.consoleProtocol = consoleProtocol;
	}
	public int getConsolePort() {
		return consolePort;
	}
	public void setConsolePort(int consolePort) {
		this.consolePort = consolePort;
	}
	public int getGatewayProtocol() {
		return gatewayProtocol;
	}
	public void setGatewayProtocol(int gatewayProtocol) {
		this.gatewayProtocol = gatewayProtocol;
	}
	public int getGatewayPort() {
		return gatewayPort;
	}
	public void setGatewayPort(int gatewayPort) {
		this.gatewayPort = gatewayPort;
	}
	public String getGatewayUserName() {
		return gatewayUserName;
	}
	public void setGatewayUserName(String gatewayUserName) {
		this.gatewayUserName = gatewayUserName;
	}
	
	@EncryptSave
	public String getGatewayPassword() {
		return gatewayPassword;
	}
	public void setGatewayPassword(String gatewayPassword) {
		this.gatewayPassword = gatewayPassword;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getSiteDescription() {
		return siteDescription;
	}
	public void setSiteDescription(String siteDescription) {
		this.siteDescription = siteDescription;
	}
	public Date getSiteCreateTime() {
		return siteCreateTime;
	}
	public void setSiteCreateTime(Date siteCreateTime) {
		this.siteCreateTime = siteCreateTime;
	}
	public Date getSiteUpdateTime() {
		return siteUpdateTime;
	}
	public void setSiteUpdateTime(Date siteUpdateTime) {
		this.siteUpdateTime = siteUpdateTime;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getLastContactTime() {
		return lastContactTime;
	}
	public void setLastContactTime(Date lastContactTime) {
		this.lastContactTime = lastContactTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getRegistrationText() {
		return registrationText;
	}
	public void setRegistrationText(String registrationText) {
		this.registrationText = registrationText;
	}
}
