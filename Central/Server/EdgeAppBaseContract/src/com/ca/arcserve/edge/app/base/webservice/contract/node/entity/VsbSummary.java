package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VCMMonitor;

public class VsbSummary implements Serializable{
	private static final long serialVersionUID = -2490355255256761581L;
	
	private VCMMonitor vcmMonitor;
	private String vcmSettings;
	private D2DStatusInfo vsbSatusInfo;
	private boolean isVMRunning;
	private String runningVMName;
	private boolean isCrossSiteVsb;
	
	public VCMMonitor getVcmMonitor() {
		return vcmMonitor;
	}
	public void setVcmMonitor(VCMMonitor vcmMonitor) {
		this.vcmMonitor = vcmMonitor;
	}
	public String getVcmSettings() {
		return vcmSettings;
	}
	public void setVcmSettings(String vcmSettings) {
		this.vcmSettings = vcmSettings;
	}
	public D2DStatusInfo getVsbSatusInfo() {
		return vsbSatusInfo;
	}
	public void setVsbSatusInfo(D2DStatusInfo vsbSatusInfo) {
		this.vsbSatusInfo = vsbSatusInfo;
	}
	public boolean isVMRunning() {
		return isVMRunning;
	}
	public void setVMRunning(boolean isVMRunning) {
		this.isVMRunning = isVMRunning;
	}
	public String getRunningVMName() {
		return runningVMName;
	}
	public void setRunningVMName(String runningVMName) {
		this.runningVMName = runningVMName;
	}	
	public boolean isCrossSiteVsb() {
		return isCrossSiteVsb;
	}
	public void setCrossSiteVsb(boolean isCrossSiteVsb) {
		this.isCrossSiteVsb = isCrossSiteVsb;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VsbSummary other = (VsbSummary) obj;
		if(!Utils.simpleObjectEquals(vcmMonitor, other.getVcmMonitor()))
			return false;
		if(!Utils.simpleObjectEquals(vcmSettings, other.getVcmMonitor()))
			return false;
		if(!Utils.simpleObjectEquals(vsbSatusInfo, other.getVsbSatusInfo()))
			return false;
		if(isVMRunning != other.isVMRunning())
			return false;
		if(!Utils.simpleObjectEquals(runningVMName, other.getRunningVMName()))
			return false;
		if(isCrossSiteVsb != other.isCrossSiteVsb())
			return false;
		return true;
	}
	
	public void update(VsbSummary other){
		if(!Utils.simpleObjectEquals(vcmMonitor, other.getVcmMonitor()))
			vcmMonitor = other.getVcmMonitor();
		if(!Utils.simpleObjectEquals(vcmSettings, other.getVcmSettings()))
			vcmSettings = other.getVcmSettings();
		if(!Utils.simpleObjectEquals(vsbSatusInfo, other.getVsbSatusInfo()))
			vsbSatusInfo = other.getVsbSatusInfo();
		if(isVMRunning != other.isVMRunning())
			isVMRunning = other.isVMRunning();
		if(!Utils.simpleObjectEquals(runningVMName, other.getRunningVMName()))
			runningVMName = other.getRunningVMName();
		if(isCrossSiteVsb != other.isCrossSiteVsb())
			isCrossSiteVsb = other.isCrossSiteVsb();
	}
}
