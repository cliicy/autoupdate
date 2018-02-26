package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import java.io.Serializable;
import java.util.Date;

public class EdgeVMPolicyMap implements Serializable {
	private static final long serialVersionUID = 3552002692116609997L;
	private int hostid;
	private int policytype;
	private int policyid;
	private int deploystatus;
	private int deployreason;
	private int deployflags;
	private int trycount;
	private Date lastsuccdeploy;
	private Date lastupdate;
	
	
	public void setHostid(int hostid)
	{
		this.hostid = hostid;
	}
	public int getHostid()
	{
		return this.hostid;
	}
	
	public void setPolicytype(int policytype)
	{
		this.policytype = policytype;
	}
	public int getPolicytype()
	{
		return this.policytype;
	}
	
	public void setPolicyid(int policyid)
	{
		this.policyid = policyid;
	}
	public int getPolicyid()
	{
		return this.policyid;
	}
	
	public void setDeploystatus(int deploystatus)
	{
		this.deploystatus = deploystatus;
	}
	public int getDeploystatus()
	{
		return this.deploystatus;
	}
	
	public void setDeployreason(int deployreason)
	{
		this.deployreason = deployreason;
	}
	public int getDeployreason()
	{
		return this.deployreason;
	}
	
	public void setDeployflags(int deployflags)
	{
		this.deployflags = deployflags;
	}
	public int getDeployflags()
	{
		return this.deployflags;
	}
	
	public void setTrycount(int trycount)
	{
		this.trycount = trycount;
	}
	public int getTrycount()
	{
		return this.trycount;
	}
	
	public void setLastsuccdeploy(Date lastsuccdeploy)
	{
		this.lastsuccdeploy = lastsuccdeploy;
	}
	public Date getLastsuccdeploy()
	{
		return this.lastsuccdeploy;
	}
	
	public void setLastupdate(Date lastupdate)
	{
		this.lastupdate = lastupdate;
	}
	public Date getLastupdate()
	{
		return this.lastupdate;
	}
}
