package com.ca.arcflash.webservice.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.xml.XMLBeanMapper;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.data.D2DTime;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.RetentionSetting;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.GeneralSettings;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.PM.ProxySettings;
import com.ca.arcflash.webservice.data.PM.StagingServerSettings;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.backup.BackupVolumes;
import com.ca.arcflash.webservice.data.backup.SRMPkiAlertSetting;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;

public class BackupConfigXMLParser {
	private static XMLBeanMapper<BackupConfiguration> backupConfigMapper;
	private static XMLBeanMapper<GeneralSettings> generalSettingsMapper;
	private static XMLBeanMapper<BackupEmail> backupEmailMapper;
	private static XMLBeanMapper<BackupSchedule> backupScheduleMapper;
	private static XMLBeanMapper<BackupVolumes> backupVolumesMapper;
	private static XMLBeanMapper<AutoUpdateSettings> AutoUpdateSettingsMapper;
	private static XMLBeanMapper<StagingServerSettings> StagingServerSettingsMapper;
	private static XMLBeanMapper<ProxySettings> ProxySettingsMapper;
	private static XMLBeanMapper<SRMPkiAlertSetting> alertSettingMapper;
	private static XMLBeanMapper<D2DTime> startTimeMapper;
	private static XMLBeanMapper<RetentionPolicy> retentionPolicyMapper;
	private static XMLBeanMapper<BackupRPSDestSetting> backupRpsDestSettingMapper;
	private static XMLBeanMapper<RpsHost> rpsHostMapper;
	
	private static Logger logger = Logger.getLogger(BackupConfigXMLParser.class);
	
	static {
		try {
			backupConfigMapper = new XMLBeanMapper<BackupConfiguration>(BackupConfiguration.class);
			generalSettingsMapper = new XMLBeanMapper<GeneralSettings>(GeneralSettings.class);
			backupEmailMapper = new XMLBeanMapper<BackupEmail>(BackupEmail.class);
			backupScheduleMapper = new XMLBeanMapper<BackupSchedule>(BackupSchedule.class);
			backupVolumesMapper = new XMLBeanMapper<BackupVolumes>(BackupVolumes.class);
			AutoUpdateSettingsMapper = new XMLBeanMapper<AutoUpdateSettings>(AutoUpdateSettings.class);
			StagingServerSettingsMapper = new XMLBeanMapper<StagingServerSettings>(StagingServerSettings.class);
			ProxySettingsMapper = new XMLBeanMapper<ProxySettings>(ProxySettings.class);
			alertSettingMapper = new XMLBeanMapper<SRMPkiAlertSetting>(SRMPkiAlertSetting.class);
			startTimeMapper = new XMLBeanMapper<D2DTime>(D2DTime.class);
			retentionPolicyMapper = new XMLBeanMapper<RetentionPolicy>(RetentionPolicy.class);
			backupRpsDestSettingMapper = new XMLBeanMapper<BackupRPSDestSetting>(BackupRPSDestSetting.class);
			rpsHostMapper = new XMLBeanMapper<RpsHost>(RpsHost.class);
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}
	
	public Document saveXML(BackupConfiguration backupConfig) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		
		Element rootElement = backupConfigMapper.saveBean(backupConfig, doc);
		doc.appendChild(rootElement);
		
/*		if (backupConfig.getEmail() == null)
			backupConfig.setEmail(new BackupEmail());
		Element emailElement = backupEmailMapper.saveBean(backupConfig.getEmail(), doc);
		rootElement.appendChild(emailElement);*/
		
		if (backupConfig.getFullBackupSchedule() == null)
			backupConfig.setFullBackupSchedule(new BackupSchedule());
		Element fullSheduleElement = backupScheduleMapper.saveBean(backupConfig.getFullBackupSchedule(), doc,"FullBackupSchedule");
		rootElement.appendChild(fullSheduleElement);
		
		if (backupConfig.getIncrementalBackupSchedule() == null)
			backupConfig.setIncrementalBackupSchedule(new BackupSchedule());
		Element incrementalSheduleElement = backupScheduleMapper.saveBean(backupConfig.getIncrementalBackupSchedule(), doc,"IncrementalBackupSchedule");
		rootElement.appendChild(incrementalSheduleElement);
		
		if (backupConfig.getResyncBackupSchedule() == null)
			backupConfig.setResyncBackupSchedule(new BackupSchedule());
		Element resyncSheduleElement = backupScheduleMapper.saveBean(backupConfig.getResyncBackupSchedule(), doc,"ResyncBackupSchedule");
		rootElement.appendChild(resyncSheduleElement);
		
		if(backupConfig.getBackupVolumes() == null)
			backupConfig.setBackupVolumes(new BackupVolumes());
		Element volumesElement = backupVolumesMapper.saveBean(backupConfig.getBackupVolumes(), doc, "BackupVolumes");
		rootElement.appendChild(volumesElement);
		if(backupConfig.getStartTime() == null) 
			backupConfig.setStartTime(new D2DTime());
		Element startTimeElement = startTimeMapper.saveBean(backupConfig.getStartTime(), doc, "StartTime");
		rootElement.appendChild(startTimeElement);
		
		if(backupConfig.getRetentionPolicy() != null) {
			Element mergeScheduleElement = retentionPolicyMapper.saveBean(
					backupConfig.getRetentionPolicy(), doc, "RetentionPolicy");
			rootElement.appendChild(mergeScheduleElement);
		}
		
		/*if(backupConfig.getUpdateSettings() == null)
			backupConfig.setUpdateSettings(new AutoUpdateSettings());
		
		if(AutoUpdateSettingsMapper == null)
		{
			AutoUpdateSettingsMapper = new XMLBeanMapper<AutoUpdateSettings>(AutoUpdateSettings.class);
		}
		Element selfUpdateSettingsElement = AutoUpdateSettingsMapper.saveBean(backupConfig.getUpdateSettings(), doc, "SelfUpdateSettings");
		
		if(StagingServerSettingsMapper == null)
		{
			StagingServerSettingsMapper = new XMLBeanMapper<StagingServerSettings>(StagingServerSettings.class);
		}
		StagingServerSettings[] stagingServers = backupConfig.getUpdateSettings().getStagingServers();
		for(int iIndex = 0;iIndex < stagingServers.length;iIndex++)
		{
			Element stagingServer = StagingServerSettingsMapper.saveBean(stagingServers[iIndex], doc,"StagingServerSettings");
			selfUpdateSettingsElement.appendChild(stagingServer);
		}
		
		if(ProxySettingsMapper == null)
		{
			ProxySettingsMapper = new XMLBeanMapper<ProxySettings>(ProxySettings.class);
		}
		Element proxySettings = ProxySettingsMapper.saveBean(backupConfig.getUpdateSettings().getproxySettings(), doc,"ProxySettings");
		selfUpdateSettingsElement.appendChild(proxySettings);*/
		
		//rootElement.appendChild(selfUpdateSettingsElement);
		//SRM Alert Setting
		if (backupConfig.getSrmPkiAlertSetting() == null)
			backupConfig.setSrmPkiAlertSetting(new SRMPkiAlertSetting());
		Element alertElement = alertSettingMapper.saveBean(backupConfig.getSrmPkiAlertSetting(), doc, "SRMAlertSettings");
		rootElement.appendChild(alertElement);
		if(backupConfig.getBackupRpsDestSetting() == null)
			backupConfig.setBackupRpsDestSetting(new BackupRPSDestSetting());
		Element backupRpsDestSettingElement = 	backupRpsDestSettingMapper.saveBean(backupConfig.getBackupRpsDestSetting() , doc, "BackupRpsDestSetting");
		rootElement.appendChild(backupRpsDestSettingElement);
		
		if(backupConfig.getBackupRpsDestSetting().getRpsHost() == null)
			backupConfig.getBackupRpsDestSetting().setRpsHost(new RpsHost());
		
		RpsHost rpsHost = backupConfig.getBackupRpsDestSetting().getRpsHost();
		Element rpsHostElement = rpsHostMapper.saveBean(rpsHost , doc, "rpsHost");
		backupRpsDestSettingElement.appendChild(rpsHostElement);
		if(backupConfig.getAdvanceSchedule() != null){
			Element advScheduleElement = AdvanceScheduleXMLParser.getElement(backupConfig.getAdvanceSchedule(), doc);
			rootElement.appendChild(advScheduleElement);
		}
		return doc;
	}
	
	public BackupConfiguration loadXML(Document doc) throws Exception{
		BackupConfiguration backupConfig = null;
		NodeList backupNodeList = doc.getElementsByTagName("BackupConfiguration");
		
		if (backupNodeList.getLength()>0)
			backupConfig = backupConfigMapper.loadBean(backupNodeList.item(0));
		
		if (backupConfig == null)
			return null;

		NodeList fullScheduleNodeList = doc.getElementsByTagName("FullBackupSchedule");
		if (fullScheduleNodeList.getLength()>0)
			backupConfig.setFullBackupSchedule(backupScheduleMapper.loadBean(fullScheduleNodeList.item(0)));
		
		NodeList incrementalScheduleNodeList = doc.getElementsByTagName("IncrementalBackupSchedule");
		if (incrementalScheduleNodeList.getLength()>0)
			backupConfig.setIncrementalBackupSchedule(backupScheduleMapper.loadBean(incrementalScheduleNodeList.item(0)));
		
		NodeList resyncScheduleNodeList = doc.getElementsByTagName("ResyncBackupSchedule");
		if (resyncScheduleNodeList.getLength()>0)
			backupConfig.setResyncBackupSchedule(backupScheduleMapper.loadBean(resyncScheduleNodeList.item(0)));
		
		NodeList backupVolumesList = doc.getElementsByTagName("BackupVolumes");
		if(backupNodeList.getLength() > 0)
			backupConfig.setBackupVolumes(backupVolumesMapper.loadBean(backupVolumesList.item(0)));
		
		NodeList startTime = doc.getElementsByTagName("StartTime");
		if(startTime.getLength() > 0) {
			backupConfig.setStartTime(startTimeMapper.loadBean(startTime.item(0)));
		}
		
		NodeList retention = doc.getElementsByTagName("RetentionPolicy");
		if(retention.getLength() > 0) {
			backupConfig.setRetentionPolicy(retentionPolicyMapper.loadBean(retention.item(0)));
		}
		
		/*NodeList SelfUpdateSettingsList = doc.getElementsByTagName("SelfUpdateSettings");
		if(SelfUpdateSettingsList.getLength() > 0)
		{
			AutoUpdateSettings updateSettings = null;
			updateSettings = AutoUpdateSettingsMapper.loadBean(SelfUpdateSettingsList.item(0));
		
			NodeList proxySettingsList = doc.getElementsByTagName("ProxySettings");
			ProxySettings proxyConfig = ProxySettingsMapper.loadBean(proxySettingsList.item(0));
			updateSettings.setproxySettings(proxyConfig);
			
			NodeList StagingServerSettingsList = doc.getElementsByTagName("StagingServerSettings");
			
			StagingServerSettings[] stagingServers = new StagingServerSettings[StagingServerSettingsList.getLength()];
			
			for(int iIndex = 0;iIndex < StagingServerSettingsList.getLength();iIndex++)
			{
				StagingServerSettings stagingServer = null;
				stagingServer = StagingServerSettingsMapper.loadBean(StagingServerSettingsList.item(iIndex));
				
				stagingServers[iIndex] = stagingServer;
			}
			updateSettings.setStagingServers(stagingServers);
			
			backupConfig.setUpdateSettings(updateSettings);
		}*/
		
		//SRM Alert Settings
		NodeList alertSetting = doc.getElementsByTagName("SRMAlertSettings");
		if ( alertSetting != null && alertSetting.getLength() > 0 ) {
			backupConfig.setSrmPkiAlertSetting(alertSettingMapper.loadBean(alertSetting.item(0)));
		}
		AdvanceSchedule advanceSchedule = AdvanceScheduleXMLParser.getAdvanceScheduleFromXML(doc);
		if(advanceSchedule!=null){
			backupConfig.setAdvanceSchedule(advanceSchedule);
		}
		//BackupRpsDestSetting
		NodeList backupRpsDestSettingNodeList = doc.getElementsByTagName("BackupRpsDestSetting");
		if (backupRpsDestSettingNodeList != null && backupRpsDestSettingNodeList.getLength() > 0 ) {
			BackupRPSDestSetting backupRpsDestSetting = backupRpsDestSettingMapper.loadBean(backupRpsDestSettingNodeList.item(0));
			backupConfig.setBackupRpsDestSetting(backupRpsDestSetting);
			NodeList rpsHostNodeList = doc.getElementsByTagName("rpsHost");
			if (rpsHostNodeList != null && rpsHostNodeList.getLength() > 0 ){
				backupRpsDestSetting.setRpsHost(rpsHostMapper.loadBean(rpsHostNodeList.item(0)));
			}
		}
		return backupConfig;
	}

	public Document saveToPreferencesXML(
			PreferencesConfiguration in_Preferences) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		db = dbf.newDocumentBuilder();
		
		Document preferencesXMLdoc = null;
		Element RootElement = null;
		preferencesXMLdoc = db.newDocument();
		if(preferencesXMLdoc == null)
		{
			return null;
		}
		RootElement = preferencesXMLdoc.createElement("Preferences");
		
		preferencesXMLdoc.appendChild(RootElement);
		
		if(RootElement != null)
		{
			if (in_Preferences.getGeneralSettings() == null)
				in_Preferences.setGeneralSettings(new GeneralSettings());
			Element generalSettingsElement = generalSettingsMapper.saveBean(in_Preferences.getGeneralSettings(), preferencesXMLdoc,"GeneralSettings");
			RootElement.appendChild(generalSettingsElement);
			
			if (in_Preferences.getEmailAlerts() == null)
				in_Preferences.setEmailAlerts(new BackupEmail());
			Element emailElement = backupEmailMapper.saveBean(in_Preferences.getEmailAlerts(), preferencesXMLdoc,"BackupEmail");
			RootElement.appendChild(emailElement);
			
			// marshal auto update settings
			AutoUpdateSettings settings = in_Preferences.getupdateSettings();
			if (settings != null) {
				ProxySettings proxy = settings.getproxySettings();
				if (proxy.getProxyServerName() != null) {
					if (proxy.getProxyServerName().length() == 0) {
						proxy.setProxyServerName("empty");
					}
				} else {
					proxy.setProxyServerName("empty");
				}
				if (proxy.getProxyPassword() != null) {
					if (proxy.getProxyPassword().length() == 0)
						proxy.setProxyPassword("empty");
				}

				if (proxy.getProxyUserName() != null) {
					if (proxy.getProxyUserName().length() == 0)
						proxy.setProxyUserName("empty");
				} else {
					proxy.setProxyUserName("empty");
				}

				if (AutoUpdateSettingsMapper == null) {
					AutoUpdateSettingsMapper = new XMLBeanMapper<AutoUpdateSettings>(
							AutoUpdateSettings.class);
				}
				Element selfUpdateSettingsElement = AutoUpdateSettingsMapper
						.saveBean(in_Preferences.getupdateSettings(),
								preferencesXMLdoc, "autoUpdateSettings");

				if (StagingServerSettingsMapper == null) {
					StagingServerSettingsMapper = new XMLBeanMapper<StagingServerSettings>(
							StagingServerSettings.class);
				}
				StagingServerSettings[] stagingServers = null;
				stagingServers = in_Preferences.getupdateSettings()
						.getStagingServers();
				if (stagingServers != null) {
					for (int iIndex = 0; iIndex < stagingServers.length; iIndex++) {
						Element stagingServer = StagingServerSettingsMapper
								.saveBean(stagingServers[iIndex],
										preferencesXMLdoc,
										"StagingServerSettings");
						selfUpdateSettingsElement.appendChild(stagingServer);
					}
				}
				if (ProxySettingsMapper == null) {
					ProxySettingsMapper = new XMLBeanMapper<ProxySettings>(
							ProxySettings.class);
				}

				if (in_Preferences.getupdateSettings().getproxySettings() != null) {
					Element proxySettings = ProxySettingsMapper.saveBean(
							in_Preferences.getupdateSettings()
									.getproxySettings(), preferencesXMLdoc,
							"ProxySettings");
					selfUpdateSettingsElement.appendChild(proxySettings);
				}
				RootElement.appendChild(selfUpdateSettingsElement);
			}
		}
		return preferencesXMLdoc;
	}
		//rootElement.appendChild(selfUpdateSettingsElement);
			/*Element backupEmail = backupXMLdoc.createElement("BackupEmail");
			if(backupEmail != null)
			{
				BackupEmail emailSettings = in_Preferences.getEmailAlerts();
				backupEmail.setAttribute("EnableEmail",Boolean.toString(emailSettings.isEnableEmail()));					
				backupEmail.setAttribute("EnableEmailOnSuccess",Boolean.toString(emailSettings.isEnableEmailOnSuccess()));
				backupEmail.setAttribute("enableEmailOnMissedJob",Boolean.toString(emailSettings.isEnableEmailOnMissedJob()));
				backupEmail.setAttribute("enableSpaceNotification",Boolean.toString(emailSettings.isEnableSpaceNotification()));
				backupEmail.setAttribute("notifyOnNewUpdates",Boolean.toString(emailSettings.isNotifyOnNewUpdates()));
									
				backupEmail.setAttribute("EnableHTMLFormat", Boolean.toString(emailSettings.isEnableHTMLFormat()));
				backupEmail.setAttribute("EnableSsl",Boolean.toString(emailSettings.isEnableSsl()));
				backupEmail.setAttribute("EnableTls",Boolean.toString(emailSettings.isEnableTls()));
				
				backupEmail.setAttribute("MailAuth",Boolean.toString(emailSettings.isMailAuth()));
				backupEmail.setAttribute("EnableProxy",Boolean.toString(emailSettings.isEnableProxy()));					
				
				backupEmail.setAttribute("ProxyPort",Integer.toString(emailSettings.getProxyPort()));					
				backupEmail.setAttribute("proxyAuth",Boolean.toString(emailSettings.isProxyAuth()));
				backupEmail.setAttribute("smtpPort",Integer.toString(emailSettings.getSmtpPort()));
				backupEmail.setAttribute("mailUser",emailSettings.getMailUser());
				backupEmail.setAttribute("url",emailSettings.getUrl());
				backupEmail.setAttribute("mailServiceName",emailSettings.getMailServiceName());
				backupEmail.setAttribute("mailPassword",emailSettings.getMailPassword());
				backupEmail.setAttribute("smtp",emailSettings.getSmtp());
				backupEmail.setAttribute("proxyAddress",emailSettings.getProxyAddress());
				backupEmail.setAttribute("proxyUsername",emailSettings.getProxyUsername());
				backupEmail.setAttribute("proxyPassword",emailSettings.getProxyPassword());
				
				backupEmail.setAttribute("subject",emailSettings.getSubject());
				backupEmail.setAttribute("content",emailSettings.getContent());
				backupEmail.setAttribute("fromAddress",emailSettings.getFromAddress());
				
				String[] strRecepients = null;
				strRecepients = emailSettings.getRecipients();
				
				if(strRecepients != null)
				{
					for(int iIndex = 0; iIndex < strRecepients.length;iIndex++)
					{
						Element eleRecepient = backupXMLdoc.createElement("Recipients");
						eleRecepient.setAttribute("Value",strRecepients[iIndex]);
						backupEmail.appendChild(eleRecepient);
					}
				}
				RootElement.appendChild(backupEmail);
			}
			
			Element autoUpdate = backupXMLdoc.createElement("AutoUpdateSettings");
			if(autoUpdate != null)
			{
				AutoUpdateSettings updateSettings = in_Preferences.getupdateSettings();
				autoUpdate.setAttribute("serverType", Integer.toString(updateSettings.getServerType()));
				
				//writing staging servers information
				StagingServerSettings[] StagingServers = null;
				StagingServers = updateSettings.getStagingServers();
				
				if(StagingServers != null)
				{
					for(int iIndex = 0;iIndex<StagingServers.length;iIndex++)
					{
						Element stagingServer = backupXMLdoc.createElement("StagingServer");						
						stagingServer.setAttribute("Id", Integer.toString(iIndex));
						stagingServer.setAttribute("stagingServer",StagingServers[iIndex].getStagingServer());
						stagingServer.setAttribute("stagingServerPort",Integer.toString(StagingServers[iIndex].getStagingServerPort()));
						autoUpdate.appendChild(stagingServer);
					}
				}
				autoUpdate.setAttribute("scheduleType",Boolean.toString(updateSettings.getScheduleType()));
				autoUpdate.setAttribute("ScheduledWeekDay", Integer.toString(updateSettings.getScheduledWeekDay()));
				autoUpdate.setAttribute("ScheduledHour", Integer.toString(updateSettings.getScheduledHour()));
				
				ProxySettings proxyConfig = updateSettings.getproxySettings();
				if(proxyConfig != null)
				{
					Element proxySettings = backupXMLdoc.createElement("ProxySettings");
					proxySettings.setAttribute("useProxy", Boolean.toString(proxyConfig.isUseProxy()));
					proxySettings.setAttribute("proxyServerName", proxyConfig.getProxyServerName());
					proxySettings.setAttribute("proxyServerPort", Integer.toString(proxyConfig.getProxyServerPort()));
					
					proxySettings.setAttribute("proxyRequiresAuth",Boolean.toString(proxyConfig.isProxyRequiresAuth()));
					proxySettings.setAttribute("proxyUserName", proxyConfig.getProxyUserName());
					proxySettings.setAttribute("proxyPassword", proxyConfig.getProxyPassword());
				}
			}*/

	public PreferencesConfiguration loadPreferencesXML(Document doc) {
		PreferencesConfiguration preferencesConfig = null;
		preferencesConfig = new PreferencesConfiguration();
		
		try {
			preferencesConfig.setGeneralSettings(getGeneralSettings(doc));
			preferencesConfig.setEmailAlerts(getEmailSettings(doc));
			
			NodeList SelfUpdateSettingsList = doc.getElementsByTagName("autoUpdateSettings");
			if(SelfUpdateSettingsList.getLength() > 0)
			{
				AutoUpdateSettings updateSettings = null;
				updateSettings = AutoUpdateSettingsMapper.loadBean(SelfUpdateSettingsList.item(0));
			
				NodeList proxySettingsList = doc.getElementsByTagName("ProxySettings");
				ProxySettings proxyConfig = ProxySettingsMapper.loadBean(proxySettingsList.item(0));
				updateSettings.setproxySettings(proxyConfig);
				
				NodeList StagingServerSettingsList = doc.getElementsByTagName("StagingServerSettings");
				
				if(StagingServerSettingsList.getLength() > 0)
				{
					StagingServerSettings[] stagingServers = new StagingServerSettings[StagingServerSettingsList.getLength()];
					
					for(int iIndex = 0;iIndex < StagingServerSettingsList.getLength();iIndex++)
					{
						StagingServerSettings stagingServer = null;
						stagingServer = StagingServerSettingsMapper.loadBean(StagingServerSettingsList.item(iIndex));
						
						stagingServers[iIndex] = stagingServer;
					}
					updateSettings.setStagingServers(stagingServers);
				}
				preferencesConfig.setupdateSettings(updateSettings);
			}
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		
		return preferencesConfig;
	}
	
	public static GeneralSettings getGeneralSettings(Document in_doc)
	{
		GeneralSettings generalSettings = null;
		NodeList settingsNodeList = in_doc.getElementsByTagName("GeneralSettings");
		if (settingsNodeList.getLength()>0)
			try {
				generalSettings = generalSettingsMapper.loadBean(settingsNodeList.item(0));
			} catch (Exception e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
			}
		return generalSettings;
	}
	
	public static BackupEmail getEmailSettings(Document in_doc)
	{
		BackupEmail EmailSettings = null;
		NodeList emailNodeList = in_doc.getElementsByTagName("BackupEmail");
		if (emailNodeList.getLength()>0)
			try {
				EmailSettings = backupEmailMapper.loadBean(emailNodeList.item(0));
			} catch (Exception e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
			}
		return EmailSettings;
	}
}
