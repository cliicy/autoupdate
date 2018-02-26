/**
 * 
 */
package com.ca.arcflash.webservice.edge.licensing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author lijwe02
 * 
 */
@Deprecated
public class CachedLicenseXmlAdapter extends
		XmlAdapter<CachedLicenseHolderWrapper, Map<String, ArrayList<CachedComponentInfo>>> {

	@Override
	public CachedLicenseHolderWrapper marshal(Map<String, ArrayList<CachedComponentInfo>> licenseMap) throws Exception {
		ArrayList<CachedLicenseHolder> holderList = new ArrayList<CachedLicenseHolder>();
		if (licenseMap != null) {
			Set<String> computerNameSet = licenseMap.keySet();
			for (String computerName : computerNameSet) {
				ArrayList<CachedComponentInfo> passwordList = licenseMap.get(computerName);
				holderList.add(new CachedLicenseHolder(computerName, passwordList));
			}
		}
		CachedLicenseHolderWrapper wrapper = new CachedLicenseHolderWrapper();
		wrapper.setHolderList(holderList);
		return wrapper;
	}

	@Override
	public Map<String, ArrayList<CachedComponentInfo>> unmarshal(CachedLicenseHolderWrapper wrapper) throws Exception {
		Map<String, ArrayList<CachedComponentInfo>> passwordMap = new HashMap<String, ArrayList<CachedComponentInfo>>();
		if (wrapper != null) {
			for (CachedLicenseHolder holder : wrapper.getHolderList()) {
				ArrayList<CachedComponentInfo> componentInfoList = holder.getComponentInfoList();
				passwordMap.put(holder.getMachineName(), componentInfoList);
			}
		}
		return passwordMap;
	}

}
