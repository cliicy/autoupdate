package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.GeneralSettings;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.PM.StagingServerSettings;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.WinRegistry;
import com.ca.arcflash.webservice.util.BackupConfigXMLParser;

public class BackupConfigurationXMLDAO extends XMLDAO {
	private BackupConfigXMLParser backupConfigXMLParser = new BackupConfigXMLParser();

	private static Logger logger = Logger.getLogger(BackupConfigurationXMLDAO.class);
	//////////////////////////////////////////////////////////////////////////
	// Adjusted for Edge - 2010.08.27
	// Merged - 2010.09.08
	//
	// Pang, Bo (panbo01)
	// 2010.09.08
	
	synchronized public BackupConfiguration get(String filePath) throws Exception
	{
		File file = new File(filePath);
		if (!file.exists())
			return null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		BackupConfiguration config = XmlDocumentToBackupConfig( doc );
		
/*		if (ServiceContext.getInstance().getPreferencesConfigurationFilePath() != null)
		{
			//kappr01 - reading email settings from preferences configuration xml
			db = dbf.newDocumentBuilder();
			file = new File(ServiceContext.getInstance().getPreferencesConfigurationFilePath());
			if (file.exists())
			{
				Document preferencesDoc = db.parse(file);
				config.setEmail(BackupConfigXMLParser.getEmailSettings(preferencesDoc));
			}
			//
		}*/
		
		return config;
	}

	synchronized public void save(String filePath,
			BackupConfiguration configuration) throws Exception
	{
		Document xmlDocument = BackupConfigToXmlDocument( configuration );
		doc2XmlFile( xmlDocument, filePath );
	}
	
	public BackupConfiguration XmlDocumentToBackupConfig( Document xmlDocument ) throws Exception
	{
		BackupConfiguration config = backupConfigXMLParser.loadXML( xmlDocument );
		
		if (config.getAdminPassword() != null)
		{
			config.setAdminPassword(
				CommonService.getInstance().getNativeFacade().decrypt(config.getAdminPassword()));
		}
		if (config.getPrePostPassword() != null)
		{
			config.setPrePostPassword(
				CommonService.getInstance().getNativeFacade().decrypt(config.getPrePostPassword()));
		}
		if (config.getPassword() != null)
		{
			config.setPassword(
				CommonService.getInstance().getNativeFacade().decrypt(config.getPassword()));
		}
		if (config.getEmail() != null && config.getEmail().getProxyPassword() != null)
		{
			config.getEmail().setProxyPassword(
				CommonService.getInstance().getNativeFacade().decrypt(config.getEmail().getProxyPassword()));
		}
		if (config.getEmail() != null && config.getEmail().getMailPassword() != null)
		{
			config.getEmail().setMailPassword(
				CommonService.getInstance().getNativeFacade().decrypt(config.getEmail().getMailPassword()));
		}
		if(!StringUtil.isJustEmptyOrNull(config.getEncryptionKey()))
		{
			config.setEncryptionKey(
					CommonService.getInstance().getNativeFacade().decrypt(config.getEncryptionKey()));
		}
		if(config.getBackupRpsDestSetting() != null 
				&& config.getBackupRpsDestSetting().getRpsHost() != null
				&& config.getBackupRpsDestSetting().getRpsHost().getPassword() != null){
			String pwd = config.getBackupRpsDestSetting().getRpsHost().getPassword();
			config.getBackupRpsDestSetting().getRpsHost().setPassword(
					CommonService.getInstance().getNativeFacade().decrypt(pwd));
		}
/*		if (config.getUpdateSettings() != null && config.getUpdateSettings().getproxySettings().getProxyPassword() != null)
		{
			config.getUpdateSettings().getproxySettings().setProxyPassword(
				CommonService.getInstance().getNativeFacade().decrypt(config.getUpdateSettings().getproxySettings().getProxyPassword()));
		}*/
		return config;
	}
	
	public Document BackupConfigToXmlDocument( BackupConfiguration configuration ) throws Exception
	{
		String plainAdminPassword = null;
		String plainPrePostPassword = null;
		String plainPassword = null;
		String plainProxyPassword = null;
		String plainMailPassword = null;
		String plainEncryptionKey = null;
		String plainRPSPassword = null;
	//	String selfUpdateProxyPassword = null;
		
		if (configuration.getAdminPassword() != null)
		{
			plainAdminPassword = configuration.getAdminPassword();
			configuration.setAdminPassword(
				CommonService.getInstance().getNativeFacade().encrypt(configuration.getAdminPassword()));	
		}
		if (configuration.getPrePostPassword() != null)
		{
			plainPrePostPassword = configuration.getPrePostPassword();
			configuration.setPrePostPassword(
				CommonService.getInstance().getNativeFacade().encrypt(configuration.getPrePostPassword()));	
		}
		if (configuration.getPassword() != null)
		{
			plainPassword = configuration.getPassword();
			configuration.setPassword(
				CommonService.getInstance().getNativeFacade().encrypt(configuration.getPassword()));
		}
		if (configuration.getEmail() != null && configuration.getEmail().getProxyPassword() != null)
		{
			plainProxyPassword = configuration.getEmail().getProxyPassword();
			configuration.getEmail().setProxyPassword(
				CommonService.getInstance().getNativeFacade().encrypt(configuration.getEmail().getProxyPassword()));
		}
		if (configuration.getEmail() != null && configuration.getEmail().getMailPassword() != null)
		{
			plainMailPassword = configuration.getEmail().getMailPassword();
			configuration.getEmail().setMailPassword(
				CommonService.getInstance().getNativeFacade().encrypt(configuration.getEmail().getMailPassword()));
		}
		if (!StringUtil.isJustEmptyOrNull(configuration.getEncryptionKey()))
		{
			plainEncryptionKey = configuration.getEncryptionKey();
			configuration.setEncryptionKey(
					CommonService.getInstance().getNativeFacade().encrypt(plainEncryptionKey));
			
		}
		
		if(configuration.getBackupRpsDestSetting() != null 
				&& configuration.getBackupRpsDestSetting().getRpsHost() != null 
				&& configuration.getBackupRpsDestSetting().getRpsHost().getPassword() != null){
			plainRPSPassword = configuration.getBackupRpsDestSetting().getRpsHost().getPassword();
			configuration.getBackupRpsDestSetting().getRpsHost().setPassword(
					CommonService.getInstance().getNativeFacade().encrypt(plainRPSPassword));
		}
/*		if (configuration.getUpdateSettings() != null && configuration.getUpdateSettings().getproxySettings().getProxyPassword() != null)
		{
			selfUpdateProxyPassword = configuration.getUpdateSettings().getproxySettings().getProxyPassword();
			configuration.getUpdateSettings().getproxySettings().setProxyPassword(
				CommonService.getInstance().getNativeFacade().encrypt(configuration.getUpdateSettings().getproxySettings().getProxyPassword()));
		}*/
		
		Document xmlDocument = backupConfigXMLParser.saveXML(configuration);
		
		if (configuration.getAdminPassword() != null){
			configuration.setAdminPassword(plainAdminPassword);
		}
		
		if (configuration.getPrePostPassword() != null){
			configuration.setPrePostPassword(plainPrePostPassword);
		}
		
		if (configuration.getPassword() != null){
			configuration.setPassword(plainPassword);
		}
		
		if (configuration.getEmail() != null && configuration.getEmail().getProxyPassword() != null){
			configuration.getEmail().setProxyPassword(plainProxyPassword);
		}
		if (configuration.getEmail() != null && configuration.getEmail().getMailPassword() != null){
			configuration.getEmail().setMailPassword(plainMailPassword);
		}
		if (!StringUtil.isEmptyOrNull(configuration.getEncryptionKey()))
		{
			configuration.setEncryptionKey(plainEncryptionKey);
		}
		if(configuration.getBackupRpsDestSetting() != null 
				&& configuration.getBackupRpsDestSetting().getRpsHost() != null 
				&& configuration.getBackupRpsDestSetting().getRpsHost().getPassword() != null){
			configuration.getBackupRpsDestSetting().getRpsHost().setPassword(plainRPSPassword);
		}
/*		if (configuration.getUpdateSettings() != null && configuration.getUpdateSettings().getproxySettings().getProxyPassword() != null){
			configuration.getUpdateSettings().getproxySettings().setProxyPassword(selfUpdateProxyPassword);
		}*/
		
		return xmlDocument;
	}
	
	// End of Adjusted for Edge
	//////////////////////////////////////////////////////////////////////////

	synchronized public PreferencesConfiguration getPreferences(String in_preferencesConfigurationFilePath) throws Exception
	{
		File file = new File(in_preferencesConfigurationFilePath);
		if (!file.exists())
			return null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		PreferencesConfiguration preferencesConfig = XmlDocumentToPreferencesSettings( doc );
		
		GeneralSettings generalSettings = new GeneralSettings();
		GetGeneralSettings(generalSettings);
		preferencesConfig.setGeneralSettings(generalSettings);
		return preferencesConfig;
	}
	
	synchronized public AutoUpdateSettings getUpdateSettings(String in_updateSettingsFilePath ){
		try{
			File cfgFile = new File( in_updateSettingsFilePath );
			AutoUpdateSettings updateSettings = JAXB.unmarshal(cfgFile, AutoUpdateSettings.class);
			if( updateSettings.getproxySettings() != null){
				String strHTTPProxyPassword = updateSettings.getproxySettings().getProxyPassword();
				updateSettings.getproxySettings().setProxyPassword(CommonService.getInstance().getNativeFacade().decrypt(strHTTPProxyPassword));
			}
			if(updateSettings.getStagingServers()!=null){
				
				for(StagingServerSettings server:updateSettings.getStagingServers()){
					server.setStagingServerStatus(-1);//
				}
			}
			return updateSettings;
		}		
		catch( Exception ex ){
			AutoUpdateSettings updateSettings = new AutoUpdateSettings();
			updateSettings.setBackupsConfigured(false);
			updateSettings.setiCAServerStatus(0);
			updateSettings.setproxySettings(null);
			updateSettings.setScheduledHour(23);
			updateSettings.setScheduledWeekDay(0);
			updateSettings.setServerType(0);
			updateSettings.setScheduleType(true);
			return updateSettings;
		}
	}
	
	public PreferencesConfiguration XmlDocumentToPreferencesSettings( Document xmlDocument )
	{
		PreferencesConfiguration preferencesConfig = backupConfigXMLParser.loadPreferencesXML(xmlDocument);
		
		if (preferencesConfig != null)
		{
			String strMailPassword = preferencesConfig.getEmailAlerts() != null ? preferencesConfig.getEmailAlerts().getMailPassword() : "";
			if(strMailPassword != null && strMailPassword.length() != 0)
			{
				preferencesConfig.getEmailAlerts().setMailPassword(CommonService.getInstance().getNativeFacade().decrypt(strMailPassword));
			}
			
			String strProxyPassword = preferencesConfig.getEmailAlerts() != null ? preferencesConfig.getEmailAlerts().getProxyPassword() : "";
			if(strProxyPassword != null && strProxyPassword.length() != 0)
			{
				preferencesConfig.getEmailAlerts().setProxyPassword(CommonService.getInstance().getNativeFacade().decrypt(strProxyPassword));
			}
			
			if ( preferencesConfig.getupdateSettings() != null && preferencesConfig.getupdateSettings().getproxySettings() != null ) {
				String strHTTPProxyPassword = preferencesConfig.getupdateSettings().getproxySettings().getProxyPassword();
				if(strHTTPProxyPassword != null && strHTTPProxyPassword.length() != 0)
				{
					preferencesConfig.getupdateSettings().getproxySettings().setProxyPassword(CommonService.getInstance().getNativeFacade().decrypt(strHTTPProxyPassword));
				}
			}
		}
		
		return preferencesConfig;
	}
	
	synchronized public void GetGeneralSettings(GeneralSettings generalSettings) {
		
		//Get NewsFeed		 
		String strNewsFeedRegistryStringName = "ShowRSS"; 
		String valueNewsFeed = "";
		
		try {
			valueNewsFeed =	WinRegistry.readString(
			          WinRegistry.HKEY_LOCAL_MACHINE, 
			          CommonRegistryKey.getD2DRegistryRoot(), 
			          strNewsFeedRegistryStringName 
			          ); 
		} catch (IllegalArgumentException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (IllegalAccessException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (InvocationTargetException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		}
		generalSettings.setbNewsFeed(valueNewsFeed != null ? (Integer.parseInt(valueNewsFeed) == 1 ? true : false) : true);
		
		//Get Social networking
		 
		String strSocialNWRegistryStringName = "ShowSocialNW"; 
		String valueSocialNW = "";
		
		try {
			valueSocialNW =	WinRegistry.readString(
			          WinRegistry.HKEY_LOCAL_MACHINE, 
			          CommonRegistryKey.getD2DRegistryRoot(), 
			          strSocialNWRegistryStringName 
			          ); 
		} catch (IllegalArgumentException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (IllegalAccessException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (InvocationTargetException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		}
		generalSettings.setbSocialNetworking(valueSocialNW != null ? (Integer.parseInt(valueSocialNW) == 1 ? true : false) : true);

		//Get Help option		 
		String strUseVideosRegistryStringName = "UseVideos"; 
		String valueHelp = "";
		
		try {
			valueHelp =	WinRegistry.readString(
			          WinRegistry.HKEY_LOCAL_MACHINE, 
			          CommonRegistryKey.getD2DRegistryRoot(), 
			          strUseVideosRegistryStringName 
			          ); 
		} catch (IllegalArgumentException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (IllegalAccessException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (InvocationTargetException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		}
		generalSettings.setUseVideos(valueHelp != null ? (Integer.parseInt(valueHelp)) : 1);
		
		//Get TrayIcon option
		String strTrayIconRegistryKeyPath = CommonRegistryKey.getD2DRegistryRoot()+"\\TrayIcon"; 
		String strTrayIconRegistryStringName = "TrayIconNotification"; 
		String valueTrayIcon = "";
		
		try {
			valueTrayIcon =	WinRegistry.readString(
			          WinRegistry.HKEY_LOCAL_MACHINE, 
			          strTrayIconRegistryKeyPath, 
			          strTrayIconRegistryStringName 
			          ); 
		} catch (IllegalArgumentException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (IllegalAccessException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (InvocationTargetException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		}
		generalSettings.setTrayIconOption(valueTrayIcon != null ? (Integer.parseInt(valueTrayIcon)) : 0);
	}

	synchronized public void savePreferences(String preferencesConfigXMLPath,PreferencesConfiguration in_preferences) throws Exception
	{
		Document xmlDocument = PreferencesSettingsToXmlDocument( in_preferences );
		doc2XmlFile(xmlDocument, preferencesConfigXMLPath);
		
		WriteGeneralSettingsToRegistry(in_preferences);
		return;
	}
	
	synchronized public void saveAutoUpdateSettings( String autoUpdateSettingsFile, AutoUpdateSettings updateSettings  ) throws Exception{
		if( updateSettings==null)
			updateSettings = new AutoUpdateSettings();
		
		String strHTTPProxyPassword = "";
		if (updateSettings.getproxySettings() != null) {
			strHTTPProxyPassword = updateSettings.getproxySettings().getProxyPassword();
			updateSettings.getproxySettings().setProxyPassword( CommonService.getInstance().getNativeFacade().encrypt(strHTTPProxyPassword));
		}

		JAXB.marshal(updateSettings, new File(autoUpdateSettingsFile));
		
		if (updateSettings.getproxySettings() != null) {
			updateSettings.getproxySettings().setProxyPassword(strHTTPProxyPassword);
		}
	}
	
	public Document PreferencesSettingsToXmlDocument(
		PreferencesConfiguration in_preferences ) throws Exception
	{
		String strMailPassword = "";
		String strProxyPassword = "";
		String strHTTPProxyPassword = "";
		if (in_preferences != null)
		{
			if (in_preferences.getEmailAlerts() != null) {
				strMailPassword = in_preferences.getEmailAlerts() != null ? in_preferences.getEmailAlerts().getMailPassword() : "";
				in_preferences.getEmailAlerts().setMailPassword(
					CommonService.getInstance().getNativeFacade().encrypt(strMailPassword));
				
				strProxyPassword = in_preferences.getEmailAlerts() != null ? in_preferences.getEmailAlerts().getProxyPassword() : "";
				in_preferences.getEmailAlerts().setProxyPassword(
					CommonService.getInstance().getNativeFacade().encrypt(strProxyPassword));
			}
			
			if (in_preferences.getupdateSettings() != null && in_preferences.getupdateSettings().getproxySettings() != null) {
				strHTTPProxyPassword = in_preferences.getupdateSettings().getproxySettings().getProxyPassword();
				in_preferences.getupdateSettings().getproxySettings().setProxyPassword(
					CommonService.getInstance().getNativeFacade().encrypt(strHTTPProxyPassword));
			}
		}
		
		Document xmlDocument = backupConfigXMLParser.saveToPreferencesXML(in_preferences);
		
		if (in_preferences.getEmailAlerts() != null) {
			in_preferences.getEmailAlerts().setMailPassword(strMailPassword);
			in_preferences.getEmailAlerts().setProxyPassword(strProxyPassword);
		}
		
		if(in_preferences.getupdateSettings() != null && in_preferences.getupdateSettings().getproxySettings() != null){
			in_preferences.getupdateSettings().getproxySettings().setProxyPassword(strHTTPProxyPassword);
		}
		
		return xmlDocument;
	}

	private void WriteGeneralSettingsToRegistry(PreferencesConfiguration in_preferences) {
		
		if ( in_preferences == null || in_preferences.getGeneralSettings() == null ) {
			return;
		}
		
		// Create Help option		 
		String strUseVideosRegistryStringName = "UseVideos"; 
		
		try {
			WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, CommonRegistryKey.getD2DRegistryRoot());

			WinRegistry.writeStringValue(
			          WinRegistry.HKEY_LOCAL_MACHINE, 
			          CommonRegistryKey.getD2DRegistryRoot(), 
			          strUseVideosRegistryStringName, 
			          Integer.toString(in_preferences.getGeneralSettings().getUseVideos())); 
		} catch (IllegalArgumentException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (IllegalAccessException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (InvocationTargetException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		}

		//Create TryIConOption
		String strTrayIconRegistryKeyPath = CommonRegistryKey.getD2DRegistryRoot()+"\\TrayIcon"; 
		String strTrayIconRegistryStringName = "TrayIconNotification"; 
		
		try {
			WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, strTrayIconRegistryKeyPath);

			WinRegistry.writeStringValue(
			          WinRegistry.HKEY_LOCAL_MACHINE, 
			          strTrayIconRegistryKeyPath, 
			          strTrayIconRegistryStringName, 
			          Integer.toString(in_preferences.getGeneralSettings().getTrayIconOption())); 
		} catch (IllegalArgumentException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (IllegalAccessException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (InvocationTargetException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		}
		
		//Create News Feed		 
		String strNewsFeedRegistryStringName = "ShowRSS"; 
		
		try {
			WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, CommonRegistryKey.getD2DRegistryRoot());

			WinRegistry.writeStringValue(
			          WinRegistry.HKEY_LOCAL_MACHINE, 
			          CommonRegistryKey.getD2DRegistryRoot(), 
			          strNewsFeedRegistryStringName, 
			          in_preferences.getGeneralSettings().isbNewsFeed() == true ? "1" : "0"); 
		} catch (IllegalArgumentException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (IllegalAccessException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (InvocationTargetException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		}
		//Create Social networking		 
		String strSocialNWRegistryStringName = "ShowSocialNW"; 
		
		try {
			WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, CommonRegistryKey.getD2DRegistryRoot());

			WinRegistry.writeStringValue(
			          WinRegistry.HKEY_LOCAL_MACHINE, 
			          CommonRegistryKey.getD2DRegistryRoot(), 
			          strSocialNWRegistryStringName, 
			          in_preferences.getGeneralSettings().isbSocialNetworking() == true ? "1" : "0"); 
		} catch (IllegalArgumentException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (IllegalAccessException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		} catch (InvocationTargetException e1) {
			logger.error(e1.getMessage() == null? e1 : e1.getMessage());
		}
	}
}
