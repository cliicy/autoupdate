package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;

public class SiteInfo implements Serializable
{
	private static final long serialVersionUID = 8584265131540226374L;

	private SiteId id = SiteId.INVALID_SITE_ID;
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	private boolean isLocal = false;
	private Date createTime;
	private Date updateTime;
	private Date lastContactTime;
	
	private String name;
	private String description;
	private String address;
	private String email;
	private String consoleHostName;
	private int consoleProtocol;
	private int consoleProt;
	private int gatewayProtocol;
	private int gatewayPort;
	private String gatewayUsername;
	private @NotPrintAttribute String gatewayPassword;	
	private String registrationText;
	private SiteStatus siteStatus;
	private String gatewayHostName;
	private int heartbeatInterval; // unit:s range:60-7200
	private Version gatewayVersion;
	/* gateway upgrade information*/
	private int upgradeStatus;
	private String upgradeDetailMessage;
	private Date upgradeStartTime;
	private Date lastReportStatusTime;
	private Date upgradeTime;
	// gateway version is same with console
	private boolean isVersionSame = true;
	private boolean isUpgradingTimeout = false;
	
	public static enum SiteStatus {
		UN_KOWN(0), ON_LINE(1), OFF_LINE(2), NERVER_CONNECT(3),
		/*NotifyingGatewayToUpdate(4),FailedToNotifyUpgradation(5),GettingUpdateInfo(6),FailedToGetUpdateInfo(7),DownloadingUpdates(8),
		FailedToDownloadUpdates(9),InstallingUpdates(10),FailedToInstallUpdates(11),UpdatedSuccessfully(12),
		UpdatedSuccessfullyNeedReboot(13)*/
		;
		
		private int status;
		
		private SiteStatus(int status){
			this.status = status;
		}
		
		public int getSiteStatus() {
			return status;
		}
	}
		
	public SiteId getId()
	{
		return id;
	}

	public void setId( SiteId id )
	{
		if (id == null)
			id = SiteId.INVALID_SITE_ID;
		this.id = id;
	}

	public GatewayId getGatewayId()
	{
		return gatewayId;
	}

	public void setGatewayId( GatewayId gatewayId )
	{
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}
	
	public boolean isLocal()
	{
		return isLocal;
	}

	public void setLocal( boolean isLocal )
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

	public int getConsoleProt() {
		return consoleProt;
	}

	public void setConsoleProt(int consoleProt) {
		this.consoleProt = consoleProt;
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
		
	public SiteStatus getSiteStatus() {
		return siteStatus;
	}

	public void setSiteStatus(SiteStatus siteStatus) {
		this.siteStatus = siteStatus;
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

	public Version getGatewayVersion() {
		return gatewayVersion;
	}

	public void setGatewayVersion(Version gatewayVersion) {
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
	
	public boolean isVersionSame() {
		return isVersionSame;
	}

	public void setVersionSame(boolean isVersionSame) {
		this.isVersionSame = isVersionSame;
	}

	public boolean isUpgradingTimeout()
	{
		return isUpgradingTimeout;
	}

	public void setUpgradingTimeout( boolean isUpgradingTimeout )
	{
		this.isUpgradingTimeout = isUpgradingTimeout;
	}

	public static void updateSite(SiteInfo oldSite,SiteInfo newSite){	
		oldSite.setId(newSite.getId());
		oldSite.setAddress(newSite.getAddress());
		oldSite.setConsoleHostName(newSite.getConsoleHostName());
		oldSite.setConsoleProt(newSite.getConsoleProt());
		oldSite.setConsoleProtocol(newSite.getConsoleProtocol());
		oldSite.setCreateTime(newSite.getCreateTime());
		oldSite.setDescription(newSite.getDescription());
		oldSite.setEmail(newSite.getEmail());
		oldSite.setGatewayHostName(newSite.getGatewayHostName());
		oldSite.setGatewayPassword(newSite.getGatewayPassword());
		oldSite.setGatewayPort(newSite.getGatewayPort());
		oldSite.setGatewayProtocol(newSite.getGatewayProtocol());
		oldSite.setGatewayId(newSite.getGatewayId());
		oldSite.setGatewayUsername(newSite.getGatewayUsername());
		oldSite.setGatewayVersion(newSite.getGatewayVersion());
		oldSite.setHeartbeatInterval(newSite.getHeartbeatInterval());
		oldSite.setLastContactTime(newSite.getLastContactTime());
		oldSite.setLocal(newSite.isLocal());
		oldSite.setName(newSite.getName());
		oldSite.setRegistrationText(newSite.getRegistrationText());
		oldSite.setSiteStatus(newSite.getSiteStatus());
		oldSite.setUpdateTime(newSite.getUpdateTime());
		oldSite.setUpgradeDetailMessage(newSite.getUpgradeDetailMessage());
		oldSite.setUpgradeStartTime(newSite.getUpgradeStartTime());
		oldSite.setUpgradeStatus(newSite.getUpgradeStatus());
		oldSite.setLastReportStatusTime( newSite.getLastReportStatusTime() );
		oldSite.setUpgradeTime(newSite.getUpgradeTime());
		oldSite.setVersionSame(newSite.isVersionSame());
		oldSite.setUpgradingTimeout( newSite.isUpgradingTimeout() );
	}
	
	public SiteInfo clone(){
		SiteInfo ret = new SiteInfo();
		ret.setId(getId());
		ret.setAddress(getAddress());
		ret.setConsoleHostName(getConsoleHostName());
		ret.setConsoleProt(getConsoleProt());
		ret.setConsoleProtocol(getConsoleProtocol());
		ret.setCreateTime(getCreateTime());
		ret.setDescription(getDescription());
		ret.setEmail(getEmail());
		ret.setGatewayHostName(getGatewayHostName());
		ret.setGatewayPassword(getGatewayPassword());
		ret.setGatewayPort(getGatewayPort());
		ret.setGatewayProtocol(getGatewayProtocol());
		ret.setGatewayId(getGatewayId());
		ret.setGatewayUsername(getGatewayUsername());
		ret.setGatewayVersion(getGatewayVersion());
		ret.setHeartbeatInterval(getHeartbeatInterval());
		ret.setLastContactTime(getLastContactTime());
		ret.setLocal(isLocal());
		ret.setName(getName());
		ret.setRegistrationText(getRegistrationText());
		ret.setSiteStatus(getSiteStatus());
		ret.setUpdateTime(getUpdateTime());
		ret.setUpgradeDetailMessage(getUpgradeDetailMessage());
		ret.setUpgradeStartTime(getUpgradeStartTime());
		ret.setLastReportStatusTime( getLastReportStatusTime() );
		ret.setUpgradeStatus(getUpgradeStatus());
		ret.setUpgradeTime(getUpgradeTime());
		ret.setVersionSame(isVersionSame());
		ret.setUpgradingTimeout( isUpgradingTimeout() );
		return ret;	
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "SiteInfo { " );
		sb.append( "id = " + id );
		sb.append( ", gatewayId = " + gatewayId );
		sb.append( ", name = '" + this.getName() + "'" );
		sb.append( ", siteStatus = '" + this.getSiteStatus() + "'" );
		sb.append( ", heartbearInterval = '" + this.getHeartbeatInterval() + "'" );
		sb.append( ", description = '" + this.getDescription() + "'" );
		sb.append( ", address = '" + this.getAddress() + "'" );
		sb.append( ", email = '" + this.getEmail() + "'" );
		sb.append( ", consoleHostName = '" + this.getConsoleHostName() + "'" );
		sb.append( ", consoleProtocol = '" + this.getConsoleProtocol() + "'" );
		sb.append( ", consolePort = '" + this.getConsoleProt() + "'" );
		sb.append( ", gatewayProtocol = '" + this.getGatewayProtocol() + "'" );
		sb.append( ", gatewayPort = '" + this.gatewayPort + "'" );
		sb.append( ", gatewayUsername = '" + this.getGatewayUsername() + "'" );	
		sb.append( ", registrationText = '" + this.getRegistrationText() + "'" );
		sb.append( ", isVersionSame = '" + this.isVersionSame() + "'" );
		sb.append( ", isUpgradingTimeout = '" + this.isUpgradingTimeout() + "'" );
		sb.append( " }" );
		return sb.toString();
	}
}
