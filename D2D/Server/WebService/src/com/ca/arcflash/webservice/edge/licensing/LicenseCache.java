/**
 * 
 */
package com.ca.arcflash.webservice.edge.licensing;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CipherUtils;
import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.FileUtils;
import com.ca.arcflash.common.StringUtil;

/**
 * @author lijwe02
 * 
 */
@Deprecated
public class LicenseCache {
	private static final Logger logger = Logger.getLogger(LicenseCache.class);
	private String cachedPoolPath;
	private CachedLicensePool licensePool = null;

	public LicenseCache(String cachedPoolPath) {
		this.cachedPoolPath = cachedPoolPath;
		init(cachedPoolPath);
		clearAllExpiredComponents();
	}

	private void init(String cachedPoolFilePath) {
		byte[] fileContent = FileUtils.readFileToByteArray(cachedPoolFilePath);
		if (fileContent != null) {
			byte[] cacheContent = CipherUtils.decryptByteArray(fileContent);
			if (cacheContent != null) {
				ByteArrayInputStream input = new ByteArrayInputStream(cacheContent);
				licensePool = JAXB.unmarshal(input, CachedLicensePool.class);
				if (logger.isDebugEnabled()) {
					logger.debug(licensePool.getPoolInfo());
				}
			} else {
				logger.error("Parse cache content failed.");
			}
		} else {
			logger.info("The cached pool content is null.");
		}
	}

	public synchronized void addCachedComponentInfo(String computerName, CachedComponentInfo cachedComponentInfo) {
		if (logger.isInfoEnabled()) {
			logger.info("Add cached component info:" + cachedComponentInfo);
		}
		getLicensePool().addCachedComponentInfo(computerName, cachedComponentInfo);
		store();
	}

	public synchronized void updateCachedLicense(String cachedLicenseKey, LicenseInfo licenseInfo) {
		if (StringUtil.isEmptyOrNull(cachedLicenseKey) || licenseInfo == null) {
			logger.error("The cached license key or license info is null.");
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Update license cache.");
		}
		getLicensePool().updateCachedLicense(cachedLicenseKey, licenseInfo);
		store();
	}

	public synchronized List<CachedComponentInfo> getCachedComponentInfoList(String cachedLicenseKey) {
		if (StringUtil.isEmptyOrNull(cachedLicenseKey)) {
			logger.error("The cached license key is empty.");
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Get cached component info list for:" + cachedLicenseKey);
		}
		ArrayList<CachedComponentInfo> cachedComponentInfoList = getLicensePool().getCachedComponentList(
				cachedLicenseKey);
		if (trimExpiredComponents(cachedLicenseKey, cachedComponentInfoList) > 0) {
			getLicensePool().clearEmptyCachedLicenseKey();
			store();
		}
		return cachedComponentInfoList;
	}

	public synchronized void clearCachedLicense(String cachedLicenseKey) {
		if (StringUtil.isEmptyOrNull(cachedLicenseKey)) {
			logger.error("The cached license key is empty.");
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Clear cached license for:" + cachedLicenseKey);
		}
		getLicensePool().clearCachedLicenses(cachedLicenseKey);
		store();
	}

	private synchronized void clearAllExpiredComponents() {
		logger.debug("clear all expired components.");
		List<String> keys = getLicensePool().getCachedLicenseKeySet();
		Iterator<String> keysIterator = keys.iterator();
		int clearedComponents = 0;
		while(keysIterator.hasNext()) {
			String key = keysIterator.next();
			ArrayList<CachedComponentInfo> cachedComponentInfoList = getLicensePool().getCachedComponentList(key);
			if (cachedComponentInfoList != null) {
				clearedComponents += trimExpiredComponents(key, cachedComponentInfoList);
			}
		}
		if (clearedComponents > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug(clearedComponents + " components were expired, so move them from cache.");
			}
			getLicensePool().clearEmptyCachedLicenseKey();
			store();
		}
	}

	private synchronized int trimExpiredComponents(String cachedLicenseKey,
			ArrayList<CachedComponentInfo> cachedComponentInfoList) {
		if (StringUtil.isEmptyOrNull(cachedLicenseKey) || cachedComponentInfoList == null) {
			logger.info("The cached license key or cached component info list is empty.");
			return 0;
		}
		List<CachedComponentInfo> expiredCachedComponentInfoList = new ArrayList<CachedComponentInfo>();
		for (CachedComponentInfo cachedComponentInfo : cachedComponentInfoList) {
			if (isCachedComponentInfoExpired(cachedComponentInfo)) {
				expiredCachedComponentInfoList.add(cachedComponentInfo);
			}
		}
		if (expiredCachedComponentInfoList.size() > 0) {
			cachedComponentInfoList.removeAll(expiredCachedComponentInfoList);
			getLicensePool().updateCachedComponentInfoList(cachedLicenseKey, cachedComponentInfoList);
		}
		return expiredCachedComponentInfoList.size();
	}

	private synchronized boolean isCachedComponentInfoExpired(CachedComponentInfo cachedComponentInfo) {
		if (cachedComponentInfo == null) {
			return true;
		}
		long diff = System.currentTimeMillis() - cachedComponentInfo.getCachedTime();
		if (diff >= 0 && diff < CommonUtil.getCachedLicenseValidInHour() * 60 * 60 * 1000) {
			if (logger.isInfoEnabled()) {
				logger.info("The cached component with id:" + cachedComponentInfo.getComponentId() + " is valid.");
			}
			return false;
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("the cached component with id:" + cachedComponentInfo.getComponentId()
						+ " is expired, the cached time is:" + new Date(cachedComponentInfo.getCachedTime())
						+ " the diff is:" + diff + " cached time in hours is:"
						+ CommonUtil.getCachedLicenseValidInHour());
			}
		}
		return true;
	}

	private synchronized CachedLicensePool getLicensePool() {
		if (licensePool == null) {
			licensePool = new CachedLicensePool();
		}
		return licensePool;
	}

	private synchronized void store() {
		logger.debug("save license cache.");
		String xml = getLicensePool().getMarshalXML();
		byte[] encryptedXml = CipherUtils.encryptByteArray(xml.getBytes());
		FileUtils.writeByteArrayToFile(cachedPoolPath, encryptedXml);
	}
}
