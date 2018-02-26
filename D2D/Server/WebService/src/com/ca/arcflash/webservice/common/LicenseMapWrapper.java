package com.ca.arcflash.webservice.common;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "Licenses")
public class LicenseMapWrapper {
	//This field used to validate the host which has the license.
	private String hostNameLicenseApplies;
	
	@XmlElement(name="licenseList")
	@XmlJavaTypeAdapter(LicenseMapAdater.class)
	private Map<String, LicenseObject> licenseCacheMap = new HashMap<String, LicenseObject>();
	
	public Map<String, LicenseObject> getLicenseMap() {
		return licenseCacheMap;
	}
	
	/**
	 * Return the host name which has the license.
	 * @return
	 */
	public String getHostNameLicenseApplies() {
		return hostNameLicenseApplies;
	}

	/**
	 * Set the host name which has the license.
	 * @param hostName
	 */
	public void setHostNameLicenseApplies(String hostName) {
		this.hostNameLicenseApplies = hostName;
	}

}
