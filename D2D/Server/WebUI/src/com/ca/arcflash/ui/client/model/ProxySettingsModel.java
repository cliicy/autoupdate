package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ProxySettingsModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2945666441763256908L;
	// Proxy
	public Boolean getUseProxy()
	{
		return (Boolean)get("UseProxy", false);
	}
	
	public void setUseProxy(Boolean bUseProxy)
	{
		set("UseProxy",bUseProxy);
	}
	
	public String getProxyServerName()
	{
		return (String)get("ProxyServerName");
	}
	
	public void setProxyServerName(String strProxyServerName)
	{
		set("ProxyServerName",strProxyServerName);
	}
	
	public Integer getProxyPort()
	{
		return (Integer)get("ProxyPort",0);
	}
	
	public void setProxyPort(Integer in_iProxyPort)
	{
		set("ProxyPort",in_iProxyPort);
	}
	
	public Boolean getProxyRequiresAuth()
	{
		return (Boolean)get("ProxyRequiresAuth",false);
	}
	
	public void setProxyRequiresAuth(Boolean in_bProxyRequiresAuth)
	{
		set("ProxyRequiresAuth",in_bProxyRequiresAuth);
	}
	
	public String getProxyUserName()
	{
		return (String)get("ProxyUserName");
	}
	
	public void setProxyUserName(String in_ProxyUserName)
	{
		set("ProxyUserName",in_ProxyUserName);
	}
	
	public String getProxyPassword()
	{
		return (String)get("ProxyPassword");
	}
	
	public void setProxyPassword(String in_strProxyPassword)
	{
		set("ProxyPassword",in_strProxyPassword);
	}
}
