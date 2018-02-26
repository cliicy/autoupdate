package com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ca.arcflash.jobscript.failover.NetworkAdapter;

public class MachineConfigure implements Serializable {

	private static final long serialVersionUID = 1L;
	private int cpuCount;
	private int memoryMBSize;
	private boolean hasSysVol = true;	
	private boolean hasBootVol = true;
	private double version;
	
	private List<NetworkAdapter> netAdapters = new ArrayList<NetworkAdapter>();
	public List<NetworkAdapter> getNetAdapters() {
		return netAdapters;
	}
	public void setNetAdapters(Collection<NetworkAdapter> netAdapters) {
		this.netAdapters.addAll(netAdapters);
	}

	public int getCpuCount() {
		return cpuCount;
	}
	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}
	public int getMemoryMBSize() {
		return memoryMBSize;
	}
	public void setMemoryMBSize(int memoryMBSize) {
		this.memoryMBSize = memoryMBSize;
	}
	public boolean hasSysVol() {
		return hasSysVol;
	}
	public void setHasSysVol(boolean hasSysVol) {
		this.hasSysVol = hasSysVol;
	}
	public boolean hasBootVol() {
		return hasBootVol;
	}
	public void setHasBootVol(boolean hasBootVol) {
		this.hasBootVol = hasBootVol;
	}
	public double getVersion() {
		return version;
	}
	public void setVersion(double version) {
		this.version = version;
	}
	
}
