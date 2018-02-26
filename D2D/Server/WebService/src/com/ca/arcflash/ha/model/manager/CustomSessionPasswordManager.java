/**
 * 
 */
package com.ca.arcflash.ha.model.manager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;

/**
 * @author lijwe02
 * 
 */
public class CustomSessionPasswordManager {
	private static final Logger logger = Logger.getLogger(CustomSessionPasswordManager.class);
	private static SessionPasswordPool passwordPool;
	private static final String CUSTOM_PASSWORD_FILEPATH = CommonUtil.D2DInstallPath + "Configuration\\custompwds.xml";

	private CustomSessionPasswordManager() {
	}

	static {
		File customPasswordFile = new File(CUSTOM_PASSWORD_FILEPATH);
		if (customPasswordFile.exists()) {
			try {
				passwordPool = JAXB.unmarshal(customPasswordFile, SessionPasswordPool.class);
			} catch (Exception e) {
				logger.error("Failed to load custom passwords.", e);
				passwordPool = new SessionPasswordPool();
			}
		} else {
			passwordPool = new SessionPasswordPool();
		}
	}

	public static List<String> getCustomPasswords(String afGuid) {
		return passwordPool.getPasswordList(afGuid);
	}

	public static synchronized void addCustomPassword(String afGuid, String password) {
		if (StringUtil.isEmptyOrNull(afGuid) || StringUtil.isEmptyOrNull(password)) {
			logger.error("Error on add custom password, the afGuid or password is empty.");
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Add password for node:" + afGuid);
		}
		passwordPool.addPassword(afGuid, password);
		try {
			save();
		} catch (IOException e) {
			logger.error("Failed to save custom password to file.", e);
		}
	}

	public static synchronized void removeCustomPassword(String afGuid, String password) {
		if (StringUtil.isEmptyOrNull(afGuid) || StringUtil.isEmptyOrNull(password)) {
			logger.error("Error on remove custom password, the afGuid or password is empty.");
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Remove password for node:" + afGuid);
		}
		passwordPool.removePassword(afGuid, password);
		try {
			save();
		} catch (IOException e) {
			logger.error("Failed to save custom password to file.", e);
		}
	}

	public static synchronized void clearCustomPassword(String afGuid) {
		if (StringUtil.isEmptyOrNull(afGuid)) {
			logger.error("Error on clear custom passwords, the afGuid is empty.");
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("clear password for node:" + afGuid);
		}
		passwordPool.clearPassword(afGuid);
		try {
			save();
		} catch (IOException e) {
			logger.error("Failed to save custom password to file.", e);
		}
	}

	public static synchronized void save() throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Save custom password file:" + CUSTOM_PASSWORD_FILEPATH);
		}
		try {
			File customPasswordFile = new File(CUSTOM_PASSWORD_FILEPATH);
			JAXB.marshal(passwordPool, customPasswordFile);
		} catch (Exception e) {
			logger.error("Failed to save password pool.");
		}
	}
}
