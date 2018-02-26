package com.ca.arcflash.webservice.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LicenseMapAdater extends XmlAdapter<LicenseList, Map<String, LicenseObject>> {

	@Override
	public LicenseList marshal(Map<String, LicenseObject> v) throws Exception {
		LicenseList licList = new LicenseList();
		
		List<LicenseObject> list = licList.getLicenseList();
		
		for(LicenseObject value : v.values()) {
			list.add(value);
		}
		
		return licList;
	}

	@Override
	public Map<String, LicenseObject> unmarshal(LicenseList v) throws Exception {
		Map<String, LicenseObject> map = new HashMap<String, LicenseObject>();
		
		List<LicenseObject> licenseList = v.getLicenseList();
		int count = v == null ? 0 : licenseList.size();
		for (int i = 0; i < count; i++) {
			LicenseObject licenseObject = licenseList.get(i);
			map.put(licenseObject.getLicenseSubject(), licenseObject);
		}
		
		return map;
	}
	
	

}
