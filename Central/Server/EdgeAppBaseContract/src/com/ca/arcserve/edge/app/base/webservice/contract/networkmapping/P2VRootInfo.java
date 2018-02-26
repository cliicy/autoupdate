package com.ca.arcserve.edge.app.base.webservice.contract.networkmapping;

import java.io.Serializable;

public class P2VRootInfo implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private long totalMemorySize = 1024;
	private long memorySize = 1024;
	private long cpuNumber = 1;
	private boolean isLinux = false;
	public long getTotalMemorySize() {
		return totalMemorySize;
	}
	public void setTotalMemorySize(long totalMemorySize) {
		this.totalMemorySize = totalMemorySize;
	}
	public long getMemorySize() {
		return memorySize;
	}
	public void setMemorySize(long memorySize) {
		this.memorySize = memorySize;
	}
	public long getCpuNumber() {
		return cpuNumber;
	}
	public void setCpuNumber(long cpuNumber) {
		this.cpuNumber = cpuNumber;
	}
	public boolean isLinux() {
		return isLinux;
	}
	public void setLinux(boolean isLinux) {
		this.isLinux = isLinux;
	}
}
