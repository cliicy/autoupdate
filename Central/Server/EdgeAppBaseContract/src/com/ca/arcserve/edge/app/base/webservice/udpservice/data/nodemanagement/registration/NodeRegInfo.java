package com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.registration;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.Credential;

public class NodeRegInfo implements Serializable
{
	private static final long serialVersionUID = 6115355702839206508L;

	private String nodeName;
	private String description;
	private Credential credential;
	
	public String getNodeName()
	{
		return nodeName;
	}
	
	public void setNodeName( String nodeName )
	{
		this.nodeName = nodeName;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription( String description )
	{
		this.description = description;
	}
	
	public Credential getCredential()
	{
		return credential;
	}
	
	public void setCredential( Credential credential )
	{
		this.credential = credential;
	}
}
