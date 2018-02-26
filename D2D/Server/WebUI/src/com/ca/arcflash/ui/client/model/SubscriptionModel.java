package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SubscriptionModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1852025327294933996L;

	public SubscriptionModel() {

	}

	public SubscriptionModel(String userName, String region,
			String password) {
		setUserName(userName);
		setRegion(region);
		setPassword(password);
	}

	//proxy info
	public Boolean getcloudUseProxy() {
		return (Boolean) get("cloudUseProxy");
	}

	public void setcloudUseProxy(Boolean in_bcloudUseProxy) {
		set("cloudUseProxy", in_bcloudUseProxy);
	}

	public String getcloudProxyServerName() {
		return get("cloudProxyServerName");
	}

	public void setcloudProxyServerName(String in_cloudProxyServerName) {
		set("cloudProxyServerName", in_cloudProxyServerName);
	}

	public Long getcloudProxyPort() {
		return (Long) get("cloudProxyPort");
	}

	public void setcloudProxyPort(Long in_cloudProxyPort) {
		set("cloudProxyPort", in_cloudProxyPort);
	}

	public Boolean getcloudProxyRequireAuth() {
		return (Boolean) get("cloudProxyRequireAuth");
	}

	public void setcloudProxyRequireAuth(boolean in_cloudProxyRequireAuth) {
		set("cloudProxyRequireAuth", in_cloudProxyRequireAuth);
	}

	public String getcloudProxyUserName() {
		return get("cloudProxyUserName");
	}

	public void setcloudProxyUserName(String in_cloudProxyUserName) {
		set("cloudProxyUserName", in_cloudProxyUserName);
	}

	public String getcloudProxyPassword() {
		return get("cloudProxyPassword");
	}

	public void setcloudProxyPassword(String in_cloudProxyPassword) {
		set("cloudProxyPassword", in_cloudProxyPassword);
	}
	
	//subscription info
	public Long getcloudVendorType() {
		return (Long) get("cloudVendorType");
	}

	public void setcloudVendorType(Long in_cloudVendorType) {
		set("cloudVendorType", in_cloudVendorType);
	}

	public String getcloudVendorURL() {
		return get("cloudVendorURL");
	}

	public void setcloudVendorURL(String in_cloudVendorURL) {
		set("cloudVendorURL", in_cloudVendorURL);
	}

	public String getStorageKey() {
		return get("storageKey");
	}

	public void setStorageKey(String in_storageKey) {
		set("storageKey", in_storageKey);
	}

	public String getUserName() {
		return get("userName");
	}

	public void setUserName(String name) {
		set("userName", name);
	}

	public String getPassword() {
		return get("password");
	}

	public void setPassword(String password) {
		set("password", password);
	}
	
	public String getRegion() {
		return get("region");
	}

	public void setRegion(String region) {
		set("region", region);
	}

	public String getServerName() {
		return get("serverName");
	}

	public void setServerName(String serverName) {
		set("serverName", serverName);
	}
	
//	public String getDeviceIP() {
//		return get("deviceIP");
//	}
//
//	public void setDeviceIP(String deviceIP) {
//		set("deviceIP", deviceIP);
//	}
//
//	public String getDeviceType() {
//		return get("deviceType");
//	}
//
//	public void setDeviceType(String deviceType) {
//		set("deviceType", deviceType);
//	}
	
}
