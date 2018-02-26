package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import java.io.Serializable;

public class VmEsxMap implements Serializable {
	private static final long serialVersionUID = 3552002692116609667L;
	private int esxId;
	private int hostId;
	private int status;
	private String vmName;
	private String vmUuid;
	private String vmInstanceUuid;
	private String esxHost;
	private String vmXPath;
	private String vmGuestOS;
	
	public String getVmXPath() {
		return vmXPath;
	}
	public void setVmXPath(String vmXPath) {
		this.vmXPath = vmXPath;
	}
	public String getVmGuestOS() {
		return vmGuestOS;
	}
	public void setVmGuestOS(String vmGuestOS) {
		this.vmGuestOS = vmGuestOS;
	}
	public void setEsxId(int esxId)
	{
		this.esxId = esxId;
	}
	public int getEsxId()
	{
		return this.esxId;
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
	
	public void setEsxHost(String esxHost)
	{
		this.esxHost = esxHost;
	}
	public String getEsxHost()
	{
		return this.esxHost;
	}
}
