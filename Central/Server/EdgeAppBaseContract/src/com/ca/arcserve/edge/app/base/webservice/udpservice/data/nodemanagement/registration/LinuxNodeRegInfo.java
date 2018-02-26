package com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.registration;

import java.io.Serializable;

public class LinuxNodeRegInfo extends NodeRegInfo implements Serializable
{
	private static final long serialVersionUID = 7348900661321264606L;

	private boolean useSSHKeyAuth;

	public boolean isUseSSHKeyAuth()
	{
		return useSSHKeyAuth;
	}

	public void setUseSSHKeyAuth( boolean useSSHKeyAuth )
	{
		this.useSSHKeyAuth = useSSHKeyAuth;
	}
}
