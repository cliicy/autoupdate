package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUpdateStatusCode;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeGatewayBean;

public class EdgeSiteInfo
{
	private int id;
	private String name;
	private String description;
	private int gatewayId;
	private int isLocal;
	private Date createTime;
	private Date updateTime;
	private String address;
	private String email;
	private String consoleHostname;
	private int consoleProtocol;
	private int consolePort;
	private int gatewayProtocol;
	private int gatewayPort;
	private String gatewayUsername;
	private @NotPrintAttribute String gatewayPassword;
	private String registrationText;
	private Date lastContactTime;
	private Date currentTime;
	private String hostUuid;
	private String gatewayHostName;
	private int heartbeatInterval; // unit:s range:60-7200
	private String gatewayVersion;
	/* gateway upgrade information*/
	private int upgradeStatus;
	private String upgradeDetailMessage;
	private Date upgradeStartTime;
	private Date lastReportStatusTime;
	private Date upgradeTime;
	private Date instantiationTime = new Date();
	
	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public int getGatewayId()
	{
		return gatewayId;
	}

	public void setGatewayId( int gatewayId )
	{
		this.gatewayId = gatewayId;
	}

	public int getIsLocal()
	{
		return isLocal;
	}

	public void setIsLocal( int isLocal )
	{
		this.isLocal = isLocal;
	}

	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime( Date createTime )
	{
		this.createTime = createTime;
	}

	public Date getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime( Date updateTime )
	{
		this.updateTime = updateTime;
	}

	public Date getLastContactTime() {
		return lastContactTime;
	}

	public void setLastContactTime(Date lastContactTime) {
		this.lastContactTime = lastContactTime;
	}

	public Date getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
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

	public String getConsoleHostname() {
		return consoleHostname;
	}

	public void setConsoleHostname(String consoleHostname) {
		this.consoleHostname = consoleHostname;
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

	public String getGatewayUsername() {
		return gatewayUsername;
	}

	public void setGatewayUsername(String gatewayUsername) {
		this.gatewayUsername = gatewayUsername;
	}

	@EncryptSave
	public String getGatewayPassword() {
		return gatewayPassword;
	}

	public void setGatewayPassword(String gatewayPassword) {
		this.gatewayPassword = gatewayPassword;
	}

	public String getRegistrationText() {
		return registrationText;
	}

	public void setRegistrationText(String registrationText) {
		this.registrationText = registrationText;
	}

	public String getHostUuid() {
		return hostUuid;
	}

	public void setHostUuid(String hostUuid) {
		this.hostUuid = hostUuid;
	}

	public String getGatewayHostName() {
		return gatewayHostName;
	}

	public void setGatewayHostName(String gatewayHostName) {
		this.gatewayHostName = gatewayHostName;
	}

	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}

	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public String getGatewayVersion() {
		return gatewayVersion;
	}

	public void setGatewayVersion(String gatewayVersion) {
		this.gatewayVersion = gatewayVersion;
	}

	public int getUpgradeStatus() {
		return upgradeStatus;
	}

	public void setUpgradeStatus(int upgradeStatus) {
		this.upgradeStatus = upgradeStatus;
	}

	public String getUpgradeDetailMessage() {
		return upgradeDetailMessage;
	}

	public void setUpgradeDetailMessage(String upgradeDetailMessage) {
		this.upgradeDetailMessage = upgradeDetailMessage;
	}

	public Date getUpgradeStartTime() {
		return upgradeStartTime;
	}

	public void setUpgradeStartTime(Date upgradeStartTime) {
		this.upgradeStartTime = upgradeStartTime;
	}

	public Date getLastReportStatusTime()
	{
		return lastReportStatusTime;
	}

	public void setLastReportStatusTime( Date lastReportStatusTime )
	{
		this.lastReportStatusTime = lastReportStatusTime;
	}

	public Date getUpgradeTime() {
		return upgradeTime;
	}

	public void setUpgradeTime(Date upgradeTime) {
		this.upgradeTime = upgradeTime;
	}	
	
	public boolean isUpgradingTimeout()
	{
		GatewayUpdateStatusCode statusCode = GatewayUpdateStatusCode.fromValue( this.upgradeStatus );
		if (!statusCode.isInProgressStatus())
			return false;
		
		if (this.lastReportStatusTime == null)
			return false;
		
		return ((this.instantiationTime.getTime() - this.lastReportStatusTime.getTime()) / 1000) >
			EdgeGatewayBean.getGatewayUpgradeTimeout();
	}
}
