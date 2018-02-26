package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;

public class ASBUCredential extends Credential implements Serializable
{
	private static final long serialVersionUID = 7912372019986557638L;

	private ABFuncAuthMode asbuAuthType;

	public ABFuncAuthMode getAsbuAuthType()
	{
		return asbuAuthType;
	}

	public void setAsbuAuthType( ABFuncAuthMode asbuAuthType )
	{
		this.asbuAuthType = asbuAuthType;
	}
}
