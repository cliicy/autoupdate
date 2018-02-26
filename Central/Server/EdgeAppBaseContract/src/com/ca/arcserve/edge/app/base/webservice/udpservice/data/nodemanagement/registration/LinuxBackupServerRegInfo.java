package com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.registration;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.WebServiceConnectInfo;

public class LinuxBackupServerRegInfo extends NodeRegInfo implements Serializable
{
	private static final long serialVersionUID = -9009632855693923280L;

	private WebServiceConnectInfo webSvcConnectInfo;

	public WebServiceConnectInfo getWebSvcConnectInfo()
	{
		return webSvcConnectInfo;
	}

	public void setWebSvcConnectInfo( WebServiceConnectInfo webSvcConnectInfo )
	{
		this.webSvcConnectInfo = webSvcConnectInfo;
	}
}
