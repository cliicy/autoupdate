package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeConnectInfoVSphere implements Serializable {
	private static final long serialVersionUID = 3552002692116609447L;
	private int hostid;
	private String username;
	private @NotPrintAttribute String password;
	private @NotPrintAttribute String uuid;
	private int protocol;
	private int port;
	private int type;
	private String majorversion;
	private String minorversion;
	private String updateversionnumber;
	private String buildnumber;
	private int status;
	private int managed;
	
	public void setHostid(int hostid)
	{
		this.hostid = hostid;
	}
	public int getHostid()
	{
		return this.hostid;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	public String getUsername()
	{
		return this.username;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
	@EncryptSave
	public String getPassword()
	{
		return this.password;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}
	@EncryptSave
	public String getUuid()
	{
		return this.uuid;
	}
	
	public void setProtocol(int protocol)
	{
		this.protocol = protocol;
	}
	public int getProtocol()
	{
		return this.protocol;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	public int getPort()
	{
		return this.port;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	public int getType()
	{
		return this.type;
	}
	
	public void setMajorversion(String majorversion)
	{
		this.majorversion = majorversion;
	}
	public String getMajorversion()
	{
		return this.majorversion;
	}
	
	public void setMinorversion(String minorversion)
	{
		this.minorversion = minorversion;
	}
	public String getMinorversion()
	{
		return this.minorversion;
	}
	
	public String getUpdateversionnumber() {
		return updateversionnumber;
	}

	public void setUpdateversionnumber(String updateversionnumber) {
		this.updateversionnumber = updateversionnumber;
	}

	public void setBuildnumber(String buildnumber)
	{
		this.buildnumber = buildnumber;
	}
	public String getBuildnumber()
	{
		return this.buildnumber;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
	public int getStatus()
	{
		return this.status;
	}
	
	public void setManaged(int managed)
	{
		this.managed = managed;
	}
	public int getManaged()
	{
		return this.managed;
	}
}
