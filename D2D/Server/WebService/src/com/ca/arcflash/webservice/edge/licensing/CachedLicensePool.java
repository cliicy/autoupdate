/**
 * 
 */
package com.ca.arcflash.webservice.edge.licensing;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ca.arcflash.common.StringUtil;

/**
 * @author lijwe02
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CachedLicensePool")
@Deprecated
public class CachedLicensePool {
	@XmlJavaTypeAdapter(CachedLicenseXmlAdapter.class)
	private Map<String, ArrayList<CachedComponentInfo>> cachedLicenses = new ConcurrentHashMap<String, ArrayList<CachedComponentInfo>>();

	public ArrayList<CachedComponentInfo> getCachedComponentList(String cachedLicenseKey) {
		if (StringUtil.isEmptyOrNull(cachedLicenseKey)) {
			return null;
		}
		return cachedLicenses.get(cachedLicenseKey);
	}

	public CachedComponentInfo getCachedComponent(String cachedLicenseKey, long componentId) {
		ArrayList<CachedComponentInfo> cachedComponentList = getCachedComponentList(cachedLicenseKey);
		if (cachedComponentList != null) {
			for (CachedComponentInfo cachedComponent : cachedComponentList) {
				if (cachedComponent.getComponentId() == componentId) {
					return cachedComponent;
				}
			}
		}
		return null;
	}

	public synchronized void addCachedComponentInfo(String cachedLicenseKey,
			CachedComponentInfo paramCachedComponentInfo) {
		ArrayList<CachedComponentInfo> cachedComponentInfoList = getCachedComponentList(cachedLicenseKey);
		if (cachedComponentInfoList == null) {
			cachedComponentInfoList = new ArrayList<CachedComponentInfo>();
			cachedLicenses.put(cachedLicenseKey, cachedComponentInfoList);
		}
		if (paramCachedComponentInfo == null) {
			return;
		}
		boolean isNewComponent = true;
		for (CachedComponentInfo cachedComponentInfo : cachedComponentInfoList) {
			if (cachedComponentInfo.getComponentId() == paramCachedComponentInfo.getComponentId()) {
				isNewComponent = false;
				// Update the component's information
				cachedComponentInfo.setCachedTime(paramCachedComponentInfo.getCachedTime());
				cachedComponentInfo.setCheckResult(paramCachedComponentInfo.getCheckResult());
				cachedComponentInfo.setComponentCode(paramCachedComponentInfo.getComponentCode());
				cachedComponentInfo.setReserved(paramCachedComponentInfo.isReserved());
				break;
			}
		}
		if (isNewComponent) {
			cachedComponentInfoList.add(paramCachedComponentInfo);
		}
	}

	public synchronized void updateCachedLicense(String cachedLicenseKey, LicenseInfo licenseInfo) {
		if (StringUtil.isEmptyOrNull(cachedLicenseKey) || licenseInfo == null) {
			System.err.println("The cached license key or licene info is null.");
			return;
		}
		ArrayList<CachedComponentInfo> cachedComponentInfoList = getCachedComponentList(cachedLicenseKey);
		if (cachedComponentInfoList == null) {
			cachedComponentInfoList = new ArrayList<CachedComponentInfo>();
		}
		for (ComponentInfo componentInfo : licenseInfo.getComponentList()) {
			boolean found = false;
			for (CachedComponentInfo cachedComponentInfo : cachedComponentInfoList) {
				if (cachedComponentInfo.getComponentId() == componentInfo.getComponentId()) {
					found = true;
					cachedComponentInfo.setCachedTime(System.currentTimeMillis());
					cachedComponentInfo.setReserved(componentInfo.isReserved());
					cachedComponentInfo.setCheckResult(componentInfo.getCheckResult());
				}
			}
			if (!found) {
				cachedComponentInfoList.add(new CachedComponentInfo(componentInfo));
			}
		}
		updateCachedComponentInfoList(cachedLicenseKey, cachedComponentInfoList);
	}

	public synchronized void updateCachedComponentInfoList(String cachedLicenseKey,
			ArrayList<CachedComponentInfo> cachedComponentInfoList) {
		if (!StringUtil.isEmptyOrNull(cachedLicenseKey)) {
			cachedLicenses.remove(cachedLicenseKey);
			if (cachedComponentInfoList.size() > 0) {
				cachedLicenses.put(cachedLicenseKey, cachedComponentInfoList);
			}
		}
	}

	public synchronized void clearCachedLicenses(String cachedLicenseKey) {
		if (!StringUtil.isEmptyOrNull(cachedLicenseKey)) {
			cachedLicenses.remove(cachedLicenseKey);
		}
	}

	/**
	 * Remove all empty cached license keys
	 * 
	 */
	public synchronized void clearEmptyCachedLicenseKey() {
		List<String> emptyCachedLicenseKeys = new ArrayList<String>();
		Set<String> keys = cachedLicenses.keySet();
		for (String key : keys) {
			ArrayList<CachedComponentInfo> cachedComponentInfoList = cachedLicenses.get(key);
			if (cachedComponentInfoList == null || cachedComponentInfoList.size() == 0) {
				emptyCachedLicenseKeys.add(key);
			}
		}
		for (String key : emptyCachedLicenseKeys) {
			cachedLicenses.remove(key);
		}
	}

	public synchronized List<String> getCachedLicenseKeySet() {
		Set<String> keys = cachedLicenses.keySet();
		List<String> keyList = new ArrayList<String>();
		keyList.addAll(keys);
		return keyList;
	}

	public String getMarshalXML() {
		StringWriter sw = new StringWriter();
		JAXB.marshal(this, sw);
		return sw.toString();
	}

	public String getPoolInfo() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("[\r\n");
		Set<String> keys = cachedLicenses.keySet();
		int index = 0;
		for (String key : keys) {
			ArrayList<CachedComponentInfo> cachedComponentInfoList = cachedLicenses.get(key);
			if (index++ > 0) {
				strBuf.append("\r\n");
			}
			strBuf.append(key).append("=>").append("[\r\n");
			if (cachedComponentInfoList != null) {
				for (CachedComponentInfo cachedComponentInfo : cachedComponentInfoList) {
					strBuf.append(cachedComponentInfo).append("\r\n");
				}
			}
			strBuf.append("]");
		}
		strBuf.append("]");
		return strBuf.toString();
	}
}
