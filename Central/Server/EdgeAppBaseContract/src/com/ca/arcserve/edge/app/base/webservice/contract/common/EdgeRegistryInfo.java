package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

public class EdgeRegistryInfo implements Serializable {
	private static final long serialVersionUID = -7700445420107738735L;
	
	private String udpHomePath;	// e.g. C:\Program Files\Arcserve\Unified Data Protection\
	private String homePath;
	private String adminUser;
	private String adminPassword;
	private String webServerVersion;
	private String webServerPath;
	private String webServerJREPath;
	private int webServerPort;
	private String webServerProtocol;
	private boolean webServerUpdateWhenStartup;
	private String appVersion;
	private String appPath;
	private boolean appUpdateWhenEdgeStartup;
	private String consoleUrl;
	private String agentUrl;
	
	public String getUdpHomePath()
	{
		return udpHomePath;
	}
	public void setUdpHomePath( String udpHomePath )
	{
		this.udpHomePath = udpHomePath;
	}
	public String getHomePath() {
		return homePath;
	}
	public void setHomePath(String homePath) {
		this.homePath = homePath;
	}
	public String getAdminUser() {
		return adminUser;
	}
	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}
	public String getAdminPassword() {
		return adminPassword;
	}
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
	public String getWebServerVersion() {
		return webServerVersion;
	}
	public void setWebServerVersion(String webServerVersion) {
		this.webServerVersion = webServerVersion;
	}
	public String getWebServerPath() {
		return webServerPath;
	}
	public void setWebServerPath(String webServerPath) {
		this.webServerPath = webServerPath;
	}
	public String getWebServerJREPath() {
		return webServerJREPath;
	}
	public void setWebServerJREPath(String webServerJREPath) {
		this.webServerJREPath = webServerJREPath;
	}
	public int getWebServerPort() {
		return webServerPort;
	}
	public void setWebServerPort(int webServerPort) {
		this.webServerPort = webServerPort;
	}
	public String getWebServerProtocol()
	{
		return webServerProtocol;
	}
	public void setWebServerProtocol( String webServerProtocol )
	{
		this.webServerProtocol = webServerProtocol;
	}
	public boolean isWebServerUpdateWhenStartup() {
		return webServerUpdateWhenStartup;
	}
	public void setWebServerUpdateWhenStartup(boolean webServerUpdateWhenStartup) {
		this.webServerUpdateWhenStartup = webServerUpdateWhenStartup;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getAppPath() {
		return appPath;
	}
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
	public boolean isAppUpdateWhenEdgeStartup() {
		return appUpdateWhenEdgeStartup;
	}
	public void setAppUpdateWhenEdgeStartup(boolean appUpdateWhenEdgeStartup) {
		this.appUpdateWhenEdgeStartup = appUpdateWhenEdgeStartup;
	}
	public String getConsoleUrl() {
		return consoleUrl;
	}
	public void setConsoleUrl(String consoleUrl) {
		this.consoleUrl = consoleUrl;
	}
	public String getAgentUrl() {
		return agentUrl;
	}
	public void setAgentUrl(String agentUrl) {
		this.agentUrl = agentUrl;
	}
}
