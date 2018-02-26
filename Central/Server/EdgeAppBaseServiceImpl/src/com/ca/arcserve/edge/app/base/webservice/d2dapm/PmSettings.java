package com.ca.arcserve.edge.app.base.webservice.d2dapm;

import java.io.File;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;

public class PmSettings {
	
	private static Logger logger = Logger.getLogger(PmSettings.class);
	private static PmSettings instance = new PmSettings();
	
	private PmSettings() {
	}
	
	public static PmSettings getInstance() {
		return instance;
	}
	
	public synchronized void save(AutoUpdateSettings autoUpdateSettings) {
		String proxyPassword = autoUpdateSettings.getproxySettings().getProxyPassword();
		
		try {
			String encryptedProxyPassword = ApmUtility.getNativeFacade().encrypt(proxyPassword);
			autoUpdateSettings.getproxySettings().setProxyPassword(encryptedProxyPassword);
			
	    	String updateSettingPath = ApmUtility.getUpdateSettingPath();
			JAXB.marshal(autoUpdateSettings, new File(updateSettingPath));
		} catch (Exception e) {
			logger.error("save auto update setting failed, error message = " + e.getMessage(), e);
		} finally {
			autoUpdateSettings.getproxySettings().setProxyPassword(proxyPassword);
		}
	}
	
	public synchronized AutoUpdateSettings load() {
		return load(false);
	}
	
	public synchronized AutoUpdateSettings load(boolean generateIfNotExist) {
		String updateSettingPath = ApmUtility.getUpdateSettingPath();
		File updateSettingFile = new File(updateSettingPath);
		if (!updateSettingFile.exists()) {
			AutoUpdateSettings settings = getDefaultSettings();
			
			if (generateIfNotExist) {
				save(settings);
			}
			
			return settings;
		}
		
		try {
			AutoUpdateSettings settings = JAXB.unmarshal(updateSettingFile, AutoUpdateSettings.class);
			String descryptedProxyPassword = ApmUtility.getNativeFacade().decrypt(settings.getproxySettings().getProxyPassword());
			settings.getproxySettings().setProxyPassword(descryptedProxyPassword);
			return settings;
		} catch (Exception e) {
			logger.error("load auto update settings failed.", e);
			return getDefaultSettings();
		}
	}

	private AutoUpdateSettings getDefaultSettings(){
		AutoUpdateSettings setting = new AutoUpdateSettings();
		
		setting.setServerType(0);	// CA server
		setting.setScheduleType(true);
		setting.setScheduledWeekDay(1);
		setting.setScheduledHour(3);
		
		return setting;
	}
	
}
