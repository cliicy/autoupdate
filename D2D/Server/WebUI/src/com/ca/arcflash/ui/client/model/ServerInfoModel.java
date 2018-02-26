package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ServerInfoModel extends BaseModelData {

	private static final long serialVersionUID = 1L;
	private String tag_uuid = "uuid";
	private String tag_serverName = "serverName";
	private String tag_userName = "userName";
	private String tag_password = "password";
	private String tag_port = "port";
	private String tag_installPath = "installPath";
	private String tag_reboot = "reboot";
	private String tag_selected = "selected";
	private String tag_deployStatus = "deployStatus";
	private String tag_deployStatusCode = "deployStatusCode";
	private String tag_deployPercentage = "deployPercentage";
	private String tag_deployMessage = "deployMessage";
	private String tag_autoStartRemoteRegService = "autoStartRemoteRegService";
	private String tag_installDriver = "installDriver";
	private String tag_useHttps = "useHttps";

	public String getUuid() {
		return get(tag_uuid);
	}

	public void setUuid(String uuid) {
		set(tag_uuid, uuid);
	}

	public String getServerName() {
		return get(tag_serverName);
	}

	public void setServerName(String serverName) {
		set(tag_serverName, serverName);
	}

	public String getUserName() {
		return get(tag_userName);
	}

	public void setUserName(String userName) {
		set(tag_userName, userName);
	}

	public String getPassword() {
		return get(tag_password);
	}

	public void setPassword(String password) {
		set(tag_password, password);
	}

	public Integer getPort() {
		return get(tag_port);
	}

	public void setPort(Integer port) {
		set(tag_port, port);
	}

	public String getInstallPath() {
		return get(tag_installPath);
	}

	public void setInstallPath(String installPath) {
		set(tag_installPath, installPath);
	}

	public Boolean isAutoStartRemoteRegService() {
		return get(tag_autoStartRemoteRegService);
	}

	public void setAutoStartRemoteRegService(Boolean autoStartRemoteRegService) {
		set(tag_autoStartRemoteRegService, autoStartRemoteRegService);
	}
	
	public Boolean isReboot() {
		return get(tag_reboot);
	}

	public void setReboot(Boolean reboot) {
		set(tag_reboot, reboot);
	}

	public Boolean isSelected() {
		return get(tag_selected);
	}

	public void setSelected(Boolean selected) {
		set(tag_selected, selected);
	}

	public String getDeployStatus() {
		return get(tag_deployStatus);
	}

	public void setDeployStatus(String deployStatus) {
		set(tag_deployStatus, deployStatus);
	}

	public Integer getDeployPercentage() {
		return get(tag_deployPercentage);
	}

	public void setDeployPercentage(Integer deployPercentage) {
		set(tag_deployPercentage, deployPercentage);
	}
	
	public String getDeployMessage() {
		return get(tag_deployMessage);
	}
	
	public void setDeployMessage(String deployMessage) {
		set(tag_deployMessage, deployMessage);
	}

	public Integer getDeployStatusCode() {
		return get(tag_deployStatusCode);
	}

	public void setDeployStatusCode(Integer deployStatusCode) {
		set(tag_deployStatusCode, deployStatusCode);
	}
	
	public Boolean isInstallDriver(){
		return get(tag_installDriver);
	}
	
	public void setInstallDriver(Boolean isInstallDriver){
		set(tag_installDriver, isInstallDriver);
	}
	
	public Boolean isUseHttps() {
		return get(tag_useHttps);
	}
	
	public void setUseHttps(Boolean useHttps) {
		set(tag_useHttps, useHttps);
	}
}
