package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;

public class ProxyInfoSummary implements Serializable{
	
	private static final long serialVersionUID = 7697165921206581604L;
	
	private int vmHostId;
	private int proxyHostId;
	private String proxyHostName;
	private String majorversion;
	private String minorversion;
	private String buildnumber;
	private String updateversionnumber;
	
	public int getVmHostId() {
		return vmHostId;
	}
	public void setVmHostId(int vmHostId) {
		this.vmHostId = vmHostId;
	}
	public int getProxyHostId() {
		return proxyHostId;
	}
	public void setProxyHostId(int proxyHostId) {
		this.proxyHostId = proxyHostId;
	}
	public String getProxyHostName() {
		return proxyHostName;
	}
	public void setProxyHostName(String proxyHostName) {
		this.proxyHostName = proxyHostName;
	}
	public String getMajorversion() {
		return majorversion;
	}
	public void setMajorversion(String majorversion) {
		this.majorversion = majorversion;
	}
	public String getMinorversion() {
		return minorversion;
	}
	public void setMinorversion(String minorversion) {
		this.minorversion = minorversion;
	}
	public String getBuildnumber() {
		return buildnumber;
	}
	public void setBuildnumber(String buildnumber) {
		this.buildnumber = buildnumber;
	}
	public String getUpdateversionnumber() {
		return updateversionnumber;
	}
	public void setUpdateversionnumber(String updateversionnumber) {
		this.updateversionnumber = updateversionnumber;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProxyInfoSummary other = (ProxyInfoSummary) obj;
		if(vmHostId != other.getVmHostId())
			return false;
		if(proxyHostId != other.getProxyHostId())
			return false;
		if(!StringUtil.isEqual(proxyHostName, other.getProxyHostName()))
			return false;
		if(!StringUtil.isEqual(majorversion, other.getMajorversion()))
			return false;
		if(!StringUtil.isEqual(minorversion, other.getMinorversion()))
			return false;
		if(!StringUtil.isEqual(buildnumber, other.getBuildnumber()))
			return false;
		if(!StringUtil.isEqual(updateversionnumber, other.getUpdateversionnumber()))
			return false;
		return true;
	}
	
	public void update(ProxyInfoSummary other) {
		if (other == null)
			return;
		if(vmHostId != other.getVmHostId())
			vmHostId = other.getVmHostId();
		if(proxyHostId != other.getProxyHostId())
			proxyHostId = other.getProxyHostId();
		if(!StringUtil.isEqual(proxyHostName, other.getProxyHostName()))
			proxyHostName = other.getProxyHostName();
		if(!StringUtil.isEqual(majorversion, other.getMajorversion()))
			majorversion = other.getMajorversion();
		if(!StringUtil.isEqual(minorversion, other.getMinorversion()))
			minorversion = other.getMinorversion();
		if(!StringUtil.isEqual(buildnumber, other.getBuildnumber()))
			buildnumber = other.getBuildnumber();
		if(!StringUtil.isEqual(updateversionnumber, other.getUpdateversionnumber()))
			updateversionnumber = other.getUpdateversionnumber();
	}
}
