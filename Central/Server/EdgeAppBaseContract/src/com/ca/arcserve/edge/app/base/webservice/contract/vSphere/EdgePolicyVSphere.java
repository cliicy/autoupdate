package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import java.io.Serializable;
import java.util.Date;

public class EdgePolicyVSphere implements Serializable {
	private static final long serialVersionUID = 3552002692116609887L;
	private int id;
	private String name;
	private String policyxml;
	private int contentflag;
	private int type;
	private String version;
	private Date creationtime;
	private Date modifiedtime;
	
	public void setId(int id)
	{
		this.id = id;
	}
	public int getId()
	{
		return this.id;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}
	
	public void setPolicyxml(String policyxml)
	{
		this.policyxml = policyxml;
	}
	public String getPolicyxml()
	{
		return this.policyxml;
	}
	
	public void setContentflag(int contentflag)
	{
		this.contentflag = contentflag;
	}
	public int getContentflag()
	{
		return this.contentflag;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	public int getType()
	{
		return this.type;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
	public String getVersion()
	{
		return this.version;
	}
	
	public void setCreationtime(Date creationtime)
	{
		this.creationtime = creationtime;
	}
	public Date getCreationtime()
	{
		return this.creationtime;
	}
	
	public void setModifiedtime(Date modifiedtime)
	{
		this.modifiedtime = modifiedtime;
	}
	public Date getModifiedtime()
	{
		return this.modifiedtime;
	}
}
