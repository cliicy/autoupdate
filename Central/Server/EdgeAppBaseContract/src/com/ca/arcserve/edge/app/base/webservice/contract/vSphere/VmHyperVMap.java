package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import java.io.Serializable;

public class VmHyperVMap implements Serializable {
	private static final long serialVersionUID = 4008235751810330497L;
	private int hypervId;
	private int hostId;
	private int status;
	private String vmName;
	private String hypervHost;
	
	public String getHypervHost() {
		return hypervHost;
	}
	public void setHypervHost(String hypervHost) {
		this.hypervHost = hypervHost;
	}

	private String vmUuid;
	private String vmInstanceUuid;
	private String vmGuestOS;
	
	public String getVmGuestOS() {
		return vmGuestOS;
	}
	public void setVmGuestOS(String vmGuestOS) {
		this.vmGuestOS = vmGuestOS;
	}
	public void setEsxId(int hypervId)
	{
		this.hypervId = hypervId;
	}
	public int getEsxId()
	{
		return this.hypervId;
	}
	
	public void setHostId(int hostId)
	{
		this.hostId = hostId;
	}
	public int getHostId()
	{
		return this.hostId;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
	public int getStatus()
	{
		return this.status;
	}
	
	public void setVmName(String vmName)
	{
		this.vmName = vmName;
	}
	public String getVmName()
	{
		return this.vmName;
	}
	
	public void setVmUuid(String vmUuid)
	{
		this.vmUuid = vmUuid;
	}
	
	public String getVmUuid()
	{
		return this.vmUuid;
	}
	
	public void setVmInstanceUuid(String vmInstanceUuid)
	{
		this.vmInstanceUuid = vmInstanceUuid;
	}
	
	public String getVmInstanceUuid()
	{
		return this.vmInstanceUuid;
	}
}
