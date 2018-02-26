package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import java.io.Serializable;
import java.util.Date;

public class EdgeHostVSphere implements Serializable {
	private static final long serialVersionUID = 3552002692116609337L;
	private int rhostid;
	private String nodeDescription;
	private Date lastupdated;
	private String rhostname;
	private String ipaddress;
	private String osdesc;
	private int IsVisible;
	private int appStatus;
	private String ServerPrincipalName;
	private int timezone;
	private int rhostType;
	
	public int getRhostType() {
		return rhostType;
	}
	public void setRhostType(int rhostType) {
		this.rhostType = rhostType;
	}
	public void setRhostid(int rhostid)
	{
		this.rhostid = rhostid;
	}
	public int getRhostid()
	{
		return this.rhostid;
	}
	
	public void setNodeDescription(String nodeDescription)
	{
		this.nodeDescription = nodeDescription;
	}
	public String getNodeDescription()
	{
		return this.nodeDescription;
	}

	public void setLastupdated(Date lastupdated)
	{
		this.lastupdated = lastupdated;
	}
	public Date getLastupdated()
	{
		return this.lastupdated;
	}
	
	public void setRhostname(String rhostname)
	{
		this.rhostname = rhostname;
	}
	public String getRhostname()
	{
		return this.rhostname;
	}
	
	public void setIpaddress(String ipaddress)
	{
		this.ipaddress = ipaddress;
	}
	public String getIpaddress()
	{
		return this.ipaddress;
	}
	
	public void setOsdesc(String osdesc)
	{
		this.osdesc = osdesc;
	}
	public String getOsdesc()
	{
		return this.osdesc;
	}
	
	public void setIsVisible(int IsVisible)
	{
		this.IsVisible = IsVisible;
	}
	public int getIsVisible()
	{
		return this.IsVisible;
	}
	
	public void setAppStatus(int appStatus)
	{
		this.appStatus = appStatus;
	}
	public int getAppStatus()
	{
		return this.appStatus;
	}
	
	public void setServerPrincipalName(String ServerPrincipalName)
	{
		this.ServerPrincipalName = ServerPrincipalName;
	}
	public String getServerPrincipalName()
	{
		return this.ServerPrincipalName;
	}
	
	public void setTimezone(int timezone)
	{
		this.timezone = timezone;
	}
	public int getTimezone()
	{
		return this.timezone;
	}
}
