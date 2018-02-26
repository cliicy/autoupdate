package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class SourceMachineNetworkAdapterInfo implements Serializable, BeanModelTag {

	private static final long serialVersionUID = 8118592400686269268L;
	
	private String adapterName;
	private String adapterDescription;
	private String speed;
	private String macAddress;
	private String domain;
	private List<String> virtualNetworkList = new ArrayList<String>();
	private String defaultVirtualNetwork = "";
	private boolean isVirtualNameFromPolicy;
	private List<String> networkTypeList = new ArrayList<String>();
	private String defaultNetworkType = "";
	private boolean isNICTypeFromPolicy;
	private List<IPSettingForADR> ipSettings;
	private String policyVirtualName;
	private String policyNICType;
	private List<IPSettingForADR> savedIpSettings = new ArrayList<IPSettingForADR>();
	boolean keepWithBackup = true;

	public boolean isKeepWithBackup() {
		return keepWithBackup;
	}

	public void setKeepWithBackup(boolean keepWithBackup) {
		this.keepWithBackup = keepWithBackup;
	}

	public List<IPSettingForADR> getSavedIpSettings() {
		return savedIpSettings;
	}
	public void setSavedIpSettings(List<IPSettingForADR> savedIpSettings) {
		this.savedIpSettings = savedIpSettings;
	}
	public String getPolicyVirtualName() {
		return policyVirtualName;
	}
	public void setPolicyVirtualName(String policyVirtualName) {
		this.policyVirtualName = policyVirtualName;
	}
	public String getPolicyNICType() {
		return policyNICType;
	}
	public void setPolicyNICType(String policyNICType) {
		this.policyNICType = policyNICType;
	}
	public boolean isVirtualNameFromPolicy() {
		return isVirtualNameFromPolicy;
	}
	public void setVirtualNameFromPolicy(boolean isVirtualNameFromPolicy) {
		this.isVirtualNameFromPolicy = isVirtualNameFromPolicy;
	}
	public boolean isNICTypeFromPolicy() {
		return isNICTypeFromPolicy;
	}
	public void setNICTypeFromPolicy(boolean isNICTypeFromPolicy) {
		this.isNICTypeFromPolicy = isNICTypeFromPolicy;
	}
	public List<IPSettingForADR> getIpSettings() {
		return ipSettings;
	}
	public void setIpSettings(List<IPSettingForADR> ipSettings) {
		this.ipSettings = ipSettings;
	}
	public List<String> getNetworkTypeList() {
		return networkTypeList;
	}
	public void setNetworkTypeList(List<String> networkTypeList) {
		this.networkTypeList = networkTypeList;
	}
	
	public String getDefaultNetworkType() {
		return defaultNetworkType;
	}
	public void setDefaultNetworkType(String defaultNetworkType) {
		this.defaultNetworkType = defaultNetworkType;
	}
	public String getDefaultVirtualNetwork() {
		return defaultVirtualNetwork;
	}
	public void setDefaultVirtualNetwork(String defaultVirtualNetwork) {
		this.defaultVirtualNetwork = defaultVirtualNetwork;
	}
	public List<String> getVirtualNetworkList() {
		return virtualNetworkList;
	}
	public void setVirtualNetworkList(List<String> virtualNetworkList) {
		this.virtualNetworkList = virtualNetworkList;
	}
	
	public String getAdapterName() {
		return adapterName;
	}
	public void setAdapterName(String adapterName) {
		this.adapterName = adapterName;
	}
	public String getAdapterDescription() {
		return adapterDescription;
	}
	public void setAdapterDescription(String adapterDescription) {
		this.adapterDescription = adapterDescription;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}

}
