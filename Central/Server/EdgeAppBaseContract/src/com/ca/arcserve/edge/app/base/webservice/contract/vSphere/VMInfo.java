package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="VMInfo1") 
public class VMInfo implements Serializable {
	private static final long serialVersionUID = 3552002692116609227L;
	List<EdgeHostVSphere> vmNodesList;
	List<EdgeConnectInfoVSphere> vmConnectInfoList;
	List<EsxVSphere> vmESXInfoList;
	List<VmEsxMap> vmESXMapList;
	List<VSphereProxyInfo> vmProxyList;
	List<EdgePolicyVSphere> vmPolicyList;
	List<EdgeVMPolicyMap> vmPolicyMapList;
	
	public void setVmNodesList(List<EdgeHostVSphere> vmNodesList)
	{
		this.vmNodesList = vmNodesList;
	}
	public List<EdgeHostVSphere> getVmNodesList()
	{
		return this.vmNodesList;
	}
	
	public void setVmConnectInfoList(List<EdgeConnectInfoVSphere> vmConnectInfoList)
	{
		this.vmConnectInfoList = vmConnectInfoList;
	}
	public List<EdgeConnectInfoVSphere> getVmConnectInfoList()
	{
		return this.vmConnectInfoList;
	}
	
	public void setVmESXInfoList(List<EsxVSphere> vmESXInfoList)
	{
		this.vmESXInfoList = vmESXInfoList;
	}
	public List<EsxVSphere> getVmESXInfoList()
	{
		return this.vmESXInfoList;
	}
	
	public void setVmESXMapList(List<VmEsxMap> vmESXMapList)
	{
		this.vmESXMapList = vmESXMapList;
	}
	public List<VmEsxMap> getVmESXMapList()
	{
		return this.vmESXMapList;
	}
	
	public void setVmProxyList(List<VSphereProxyInfo> vmProxyList)
	{
		this.vmProxyList = vmProxyList;
	}
	public List<VSphereProxyInfo> getVmProxyList()
	{
		return this.vmProxyList;
	}
	
	public void setVmPolicyList(List<EdgePolicyVSphere> vmPolicyList)
	{
		this.vmPolicyList = vmPolicyList;
	}
	public List<EdgePolicyVSphere> getVmPolicyList()
	{
		return this.vmPolicyList;
	}
	
	public void setVmPolicyMapList(List<EdgeVMPolicyMap> vmPolicyMapList)
	{
		this.vmPolicyMapList = vmPolicyMapList;
	}
	public List<EdgeVMPolicyMap> getVmPolicyMapList()
	{
		return this.vmPolicyMapList;
	}
}
