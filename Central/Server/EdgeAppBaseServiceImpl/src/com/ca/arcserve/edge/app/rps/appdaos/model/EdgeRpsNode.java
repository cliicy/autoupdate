package com.ca.arcserve.edge.app.rps.appdaos.model;

import java.util.Date;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeRpsNode {
	
	private int node_id;
	private String node_name;
	private String ip_address;
	private String node_description;
	private int node_type;
	private Date lastUpdate;
	private int protocol;
	private int port;
	private int appstatus;
	private String username;
	private @NotPrintAttribute String password;
	private String major_version;
	private String minor_version;
	private int manage;
	private @NotPrintAttribute String uuid;
	private int policy_count;
	private int data_store_count;
	private int deploy_status;
	private int deploy_reason;
	private String build_number;
	private String d2dUpdateversionnumber;
	//remote deploy
	private int remoteDeployStatus;
	private int deployTaskStatus;
	private Date remoteDeployTime;
	// siteName, node belongs to which site/gateway
	private String siteName;
		
	public int getAppstatus() {
		return appstatus;
	}
	public void setAppstatus(int appstatus) {
		this.appstatus = appstatus;
	}
	@EncryptSave
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getAppStatus() {
		return appstatus;
	}
	public void setAppStatus(int appstatus) {
		this.appstatus = appstatus;
	}
	public int getManage() {
		return manage;
	}
	public void setManage(int manage) {
		this.manage = manage;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@EncryptSave
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMajor_version() {
		return major_version;
	}
	public void setMajor_version(String major_version) {
		this.major_version = major_version;
	}
	public String getMinor_version() {
		return minor_version;
	}
	public void setMinor_version(String minor_version) {
		this.minor_version = minor_version;
	}
	public int getNode_id() {
		return node_id;
	}
	public void setNode_id(int node_id) {
		this.node_id = node_id;
	}
	public String getNode_name() {
		return node_name;
	}
	public void setNode_name(String node_name) {
		this.node_name = node_name;
	}
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public String getNode_description() {
		return node_description;
	}
	public void setNode_description(String node_description) {
		this.node_description = node_description;
	}
	public int getNode_type() {
		return node_type;
	}
	public void setNode_type(int node_type) {
		this.node_type = node_type;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public int getPolicy_count() {
		return policy_count;
	}
	public void setPolicy_count(int policy_count) {
		this.policy_count = policy_count;
	}
	public int getData_store_count() {
		return data_store_count;
	}
	public void setData_store_count(int data_store_count) {
		this.data_store_count = data_store_count;
	}
	public int getDeploy_status() {
		return deploy_status;
	}
	public void setDeploy_status(int deploy_status) {
		this.deploy_status = deploy_status;
	}
	public int getDeploy_reason() {
		return deploy_reason;
	}
	public void setDeploy_reason(int deploy_reason) {
		this.deploy_reason = deploy_reason;
	}
	public String getBuild_number() {
		return build_number;
	}
	public void setBuild_number(String build_number) {
		this.build_number = build_number;
	}
	public int getRemoteDeployStatus() {
		return remoteDeployStatus;
	}
	public void setRemoteDeployStatus(int remoteDeployStatus) {
		this.remoteDeployStatus = remoteDeployStatus;
	}
	public Date getRemoteDeployTime() {
		return remoteDeployTime;
	}
	public void setRemoteDeployTime(Date remoteDeployTime) {
		this.remoteDeployTime = remoteDeployTime;
	}
	public String getD2dUpdateversionnumber() {
		return d2dUpdateversionnumber;
	}
	public void setD2dUpdateversionnumber(String d2dUpdateversionnumber) {
		this.d2dUpdateversionnumber = d2dUpdateversionnumber;
	}
	public int getDeployTaskStatus() {
		return deployTaskStatus;
	}
	public void setDeployTaskStatus(int deployTaskStatus) {
		this.deployTaskStatus = deployTaskStatus;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}	
}
