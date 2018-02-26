package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;
import java.util.List;

public class InstantVHDOperationResult implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public InstantVHDOperationResult() {
		errCode = 0;
	}
	
	private int errCode;
	public int getErrCode() {
		return errCode;
	}
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	private String errString;
	public String getErrString() {
		return errString;
	}
	public void setErrString(String errString) {
		this.errString = errString;
	}
	private String jobUUID;
	public String getJobUUID() {
		return jobUUID;
	}
	public void setJobUUID(String jobUUID) {
		this.jobUUID = jobUUID;
	}
	private List<String> vhdList;
	public List<String> getVhdList() {
		return vhdList;
	}
	public void setVhdList(List<String> vhdList) {
		this.vhdList = vhdList;
	}
	
	private String bootVHD;
	public String getBootVHD() {
		return bootVHD;
	}
	public void setBootVHD(String bootVHD) {
		this.bootVHD = bootVHD;
	}

	private String sysVHD;
	public String getSysVHD() {
		return sysVHD;
	}
	public void setSysVHD(String sysVHD) {
		this.sysVHD = sysVHD;
	}

	private boolean isUEFI;
	public boolean isUEFI() {
		return isUEFI;
	}
	public void setUEFI(boolean isUEFI) {
		this.isUEFI = isUEFI;
	}

	private long maxDiskSize;
	public long getMaxDiskSize() {
		return maxDiskSize;
	}
	public void setMaxDiskSize(long maxDiskSize) {
		this.maxDiskSize = maxDiskSize;
	}

}
