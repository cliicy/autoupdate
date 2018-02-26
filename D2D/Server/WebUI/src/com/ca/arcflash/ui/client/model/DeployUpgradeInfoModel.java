package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * 	String hostname;
	String installPath;
	Long port;
	Long build;
	Long majVer;
	Long minVer;
 * @author gonro07
 *
 */
public class DeployUpgradeInfoModel  extends BaseModelData {
	public String getHostname() {
		return (String) get("hostname");
	}
	
	public void setHostname(String hostname) {
		set("hostname",hostname);
	}
	public String getInstallPath() {
		return (String)get("installPath");
	}
	public void setInstallPath(String installPath) {
		set("installPath",installPath);
	}
	public Long getPort() {
		return (Long)get("port");
	}
	public void setPort(Long port) {
		set("port",port);
	}
	public Long getBuild() {
		return (Long)get("build");
	}
	public void setBuild(Long build) {
		set("build",build);
	}
	public Long getMajVer() {
		return (Long)get("majVer");
	}
	public void setMajVer(Long majVer) {
		set("majVer",majVer);
	}
	public Long getMinVer() {
		return (Long)get("minVer");
	}
	public void setMinVer(Long minVer) {
		set("minVer",minVer);
	}
	public Boolean useHttps(){
		return (Boolean)get("useHttps");
	}
	public void setUseHttps(Boolean https) {
		set("useHttps", https);
	}
}
