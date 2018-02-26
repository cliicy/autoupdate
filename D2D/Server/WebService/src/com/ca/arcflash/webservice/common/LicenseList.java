package com.ca.arcflash.webservice.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "licenseList")
public class LicenseList {
	
	@XmlElement(name = "license")
	private final List<LicenseObject> licenseList = new ArrayList<LicenseObject>();

	public List<LicenseObject> getLicenseList() {
		return licenseList;
	}
	
	
}
