package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;

public class LinuxD2DInfoSummary implements Serializable{
	
	private static final long serialVersionUID = 7697165921206581604L;
	
	private int hostId;
	private int linuxD2DHostId;
	private String linuxD2DHostName;
	private String majorversion;
	private String minorversion;
	private String buildnumber;
	private String updateversionnumber;
	
	
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public int getLinuxD2DHostId() {
		return linuxD2DHostId;
	}
	public void setLinuxD2DHostId(int linuxD2DHostId) {
		this.linuxD2DHostId = linuxD2DHostId;
	}
	public String getLinuxD2DHostName() {
		return linuxD2DHostName;
	}
	public void setLinuxD2DHostName(String linuxD2DHostName) {
		this.linuxD2DHostName = linuxD2DHostName;
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
		LinuxD2DInfoSummary other = (LinuxD2DInfoSummary) obj;
		if(hostId != other.getHostId())
			return false;
		if(linuxD2DHostId != other.getLinuxD2DHostId())
			return false;
		if(!StringUtil.isEqual(linuxD2DHostName, other.getLinuxD2DHostName()))
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
	
	public void update(LinuxD2DInfoSummary other) {
		if (other == null)
			return;
		if(hostId != other.getHostId())
			hostId = other.getHostId();
		if(linuxD2DHostId != other.getLinuxD2DHostId())
			linuxD2DHostId = other.getLinuxD2DHostId();
		if(!StringUtil.isEqual(linuxD2DHostName, other.getLinuxD2DHostName()))
			linuxD2DHostName = other.getLinuxD2DHostName();
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
