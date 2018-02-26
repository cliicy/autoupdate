/**
 * 
 */
package com.ca.arcflash.webservice.edge.licensing;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author lijwe02
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public class CachedLicenseHolder {
	private String machineName;
	private ArrayList<CachedComponentInfo> componentInfoList;

	public CachedLicenseHolder() {

	}

	public CachedLicenseHolder(String computerName, ArrayList<CachedComponentInfo> componentInfoList) {
		this.machineName = computerName;
		this.componentInfoList = componentInfoList;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String computerName) {
		this.machineName = computerName;
	}

	public ArrayList<CachedComponentInfo> getComponentInfoList() {
		return componentInfoList;
	}

	public void setComponentInfoList(ArrayList<CachedComponentInfo> componentInfoList) {
		this.componentInfoList = componentInfoList;
	}
}
