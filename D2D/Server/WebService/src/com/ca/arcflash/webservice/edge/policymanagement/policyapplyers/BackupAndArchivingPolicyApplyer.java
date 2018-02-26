package com.ca.arcflash.webservice.edge.policymanagement.policyapplyers;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.common.VolumnMapAdapter;
import com.ca.arcflash.webservice.data.D2DTime;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.LogUtility;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyXmlObject;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.CopyService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.BackupConfigurationXMLDAO;
import com.ca.arcflash.webservice.util.ArchiveConfigXMLParser;
import com.ca.arcflash.webservice.util.ArchiveToTapeUtils;
import com.ca.arcflash.webservice.util.ArchiveUtil;

public class BackupAndArchivingPolicyApplyer extends BasePolicyApplyer
{
	// Volume sub-status, refer to com.ca.arcflash.ui.client.model.VolumeSubStatus
//	private static final int EVSS_SYSTEM = 0x00010000;
//	private static final int EVSS_BOOT = 0x00020000;
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected int getResponsiblePolicyType()
	{
		return ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving;
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void doApplying()
	{
		//we need to clear them in case this class is used across multiple policies
		//backup configuration
		backupConfig = null;
		//archive configuration
		configuration = null;
		
//		// if old policy is same, no need save again
//		if(checkpolicysame()){
//			return;
//		}
		if (validateSettings())
		{
			applyBackupSettings();
			applyArchiveSettings();
			applyScheduledExportSettings();
			applyPreferencesSettings();
		}
		
		if (!hasError()) // no errors, then save policy uuid
			savePolicyUuid();
	}
	

	private boolean checkpolicysame() {
		EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
		if(edgeRegInfo==null)
			return false;
		String oldpolicyUuid=edgeRegInfo.getPolicyUuids().get("Default");
		if(oldpolicyUuid==null || oldpolicyUuid.isEmpty())
			return false;
		
		if(oldpolicyUuid.equals(policyUuid))
			return true;
		
		return false;
	}

	protected void savePolicyUuid() {
		Map<String, String> policyUuids = new HashMap<String, String>();
		policyUuids.put("Default", policyUuid);
		new D2DEdgeRegistration().SavePolicyUuid2Xml(ApplicationType.CentralManagement, policyUuids);
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void doUnApplying()
	{
		unapplyBackupSettings();
		unapplyArchiveSettings();
		unapplyScheduledExportSettings();
		unapplyPreferencesSettings();
		
		ArchiveToTapeUtils.removeArchiveToTape();
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void removePolicyRecord()
	{
		List<String> list = new ArrayList<String>();
		list.add("Default");
		new D2DEdgeRegistration().RemovePolicyUuidFromXml(ApplicationType.CentralManagement, list );
	}

	//////////////////////////////////////////////////////////////////////////
	
	protected boolean validateSettings()
	{
		boolean result = true;
		
		if (!validateBackupSettings())
			result = false;
		
//		if (!validateArchiveSettings())
//			result = false;
		
		if (!validateScheduledExportSettings())
			result = false;
		
		if (!validatePreferencesSettings())
			result = false;
		
		return result;
	}

	//////////////////////////////////////////////////////////////////////////
	BackupConfiguration backupConfig = null;
	protected BackupConfiguration getBackupSettings() throws Exception
	{
		if(backupConfig==null){
			backupConfig = _getBackupSettings();
			backupConfig.setPlanId(planId);
			
			if(backupConfig.getStartTime() != null && backupConfig.getStartTime().getYear() > 1900) {
				//To solve the issue, for start time, Edge is in DST startTime, while D2D is not, we need to set 
				//backup start time as user input one.
				//If start time is in D2D DST start time, validation fails
				D2DTime time = backupConfig.getStartTime();
				Calendar cal = Calendar.getInstance();
				cal.set(time.getYear(), time.getMonth(), time.getDay(), time.getHourOfday(), time.getMinute(), 0);
				cal.set(Calendar.MILLISECOND, 0);
				backupConfig.setBackupStartTime(cal.getTimeInMillis());
			}
			
			if(backupConfig.getRetentionPolicy() == null) {
				RetentionPolicy policy = new RetentionPolicy();
				backupConfig.setRetentionPolicy(policy);
			}
			
			VolumnMapAdapter.convertBackupVolumes(backupConfig);
		}
		return backupConfig;
	}
	
	protected BackupConfiguration _getBackupSettings() throws Exception{
		Document backupSettingsDocument =this.policyXmlObject.getSettingsSection(PolicyXmlObject.PolicyXmlSectionNames.BackupSettings );
		BackupConfigurationXMLDAO backupConfigXmlDao = new BackupConfigurationXMLDAO();
		return backupConfigXmlDao.XmlDocumentToBackupConfig( backupSettingsDocument );
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	
	protected boolean validateBackupSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateBackupSettings(): enter" );

			BackupConfiguration backupConfig = getBackupSettings();
			BackupService.getInstance().validateBackupConfiguration( backupConfig );
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateBackupSettings(): Backup settings validation passed." );
			
			return true;
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"validateBackupSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.BackupSettings,
				e.getErrorCode(),
				e.getMultipleArguments() );
			
			return false;
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"validateBackupSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.BackupSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
			
			return false;
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateBackupSettings(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////

	protected void applyBackupSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyBackupSettings(): enter" );

			BackupConfiguration backupConfig = getBackupSettings();
			BackupConfiguration old=null;
			try {
				old = BackupService.getInstance().getBackupConfiguration();
			} catch (ServiceException e) {
				logUtility.writeLog( LogUtility.LogTypes.Error,
						e.getMessage() );
			}
			
			if(backupConfig!=null && backupConfig.getPlanGUID()!=null && old!=null &&!backupConfig.getPlanGUID().equals(old.getPlanGUID())){
				ArchiveToTapeUtils.removeArchiveToTape();
			}
			
			if(old != null && ArchiveUtil.needDeletePendingJobs(getArchiveSettings(), ArchiveService.getInstance().getArchiveConfiguration())){
				ArchiveService.getInstance().deleteAllPendingFileCopyJobs(old.getDestination(), 
						ArchiveUtil.getDomainName(old.getUserName()), 
						ArchiveUtil.getUserName(old.getUserName()), 
						old.getPassword() == null ? "" : old.getPassword());
			}
			
			BackupService.getInstance().saveBackupConfiguration( backupConfig );
			List<String> dsVolumes = BackupService.getInstance().getBackupDatastoreVolumes(backupConfig);
			if(dsVolumes!=null&&dsVolumes.size()>0){
				StringBuilder builder = new StringBuilder();
				for(String dsVolume : dsVolumes){
					builder.append(",").append(dsVolume);
				}
				builder.deleteCharAt(0);
				this.addWarning(
						null,
						ID2DPolicyManagementService.SettingsTypes.BackupSettings,
						FlashServiceErrorCode.BackupConfig_WARN_SOURCE_CONTAIN_DATASTORE,
						new String[]{builder.toString()});
			}
			
			List<BackupService.DisableVolumeInfo> volumes = BackupService
					.getInstance().getVolumesCannotBackup(backupConfig);
			if(dsVolumes!=null&&!dsVolumes.isEmpty()){
				for(String dsVolume : dsVolumes){
					if(volumes.contains(dsVolume))
						volumes.remove(dsVolume);
				}
			}
			if(!volumes.isEmpty()){
				StringBuilder builder=new StringBuilder();
				StringBuilder systemVolumeBuilder = new StringBuilder();
				for (BackupService.DisableVolumeInfo vol : volumes) {
					if (VolumnMapAdapter
							.isSystemVolue(vol.getSubVolumeStatus())) {
						systemVolumeBuilder.append(",").append(vol).append("\\");
					} else {
						builder.append(",").append(vol).append("\\");
					}
				}
				
				if (builder.length() > 0) {
					builder.deleteCharAt(0);
					this.addWarning(
							null,
							ID2DPolicyManagementService.SettingsTypes.BackupSettings,
							FlashServiceErrorCode.BackupConfig_ERR_INVALID_BACKUP_VOLUMES_NOTExist,
							new String[] { builder.toString() });
				} 
				
				if(systemVolumeBuilder.length() > 0) {
					systemVolumeBuilder.deleteCharAt(0);
					this.addWarning(
							null,
							ID2DPolicyManagementService.SettingsTypes.BackupSettings,
							FlashServiceErrorCode.BackupConfig_ERR_INVALID_SYSTEM_VOLUMES_FAT32,
							new String[] { systemVolumeBuilder.toString() });
				}
			}
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyBackupSettings(): Backup settings applied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyBackupSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.BackupSettings,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyBackupSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.BackupSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyBackupSettings(): exit" );
		}
	}

		
//	public void convertBackupVolumes(BackupConfiguration backupConfig) throws ServiceException {
//		if (backupConfig == null || backupConfig.getBackupVolumes() == null) {
//			return;
//		}
//		
//		List<String> volumeNameList = backupConfig.getBackupVolumes().getVolumes();
//		if (volumeNameList == null) {
//			return;
//		}
//		
//		boolean backupSystemVolume = volumeNameList.contains(ID2DPolicyManagementService.BackupSources.SystemVolume);
//		boolean backupBootVolume = volumeNameList.contains(ID2DPolicyManagementService.BackupSources.BootVolume);
//		
//		if (!backupSystemVolume && !backupBootVolume) {
//			return;
//		}
//		
//		Volume[] volumeArray = BrowserService.getInstance().getVolumes(true, 
//				backupConfig.getDestination(),
//				backupConfig.getUserName(),
//				backupConfig.getPassword());
//		
//		if (backupSystemVolume) {
//			volumeNameList.remove(ID2DPolicyManagementService.BackupSources.SystemVolume);
//			String systemVolumeName = getSystemVolumeName(volumeArray);
//			if (!volumeNameList.contains(systemVolumeName)) {
//				volumeNameList.add(systemVolumeName);
//			}
//		}
//		
//		if (backupBootVolume) {
//			volumeNameList.remove(ID2DPolicyManagementService.BackupSources.BootVolume);
//			String bootVolumeName = getBootVolumeName(volumeArray);
//			if (!volumeNameList.contains(bootVolumeName)) {
//				volumeNameList.add(bootVolumeName);
//			}
//		}
//	}
//
//	private String getBootVolumeName(Volume[] volumeArray) {
//		if (volumeArray == null) {
//			return null;
//		}
//		
//		for (Volume volume : volumeArray) {
//			if ((volume.getSubStatus() & EVSS_BOOT) > 0) {
//				return removeEndSlash(volume.getName());
//			}
//		}
//		
//		return null;
//	}
//	
//	private String removeEndSlash(String name) {
//		if(name != null && (name.endsWith("\\") || name.endsWith("/")))
//			name = name.substring(0, name.length() - 1);
//		return name;
//	}
//
//	private String getSystemVolumeName(Volume[] volumeArray) {
//		if (volumeArray == null) {
//			return null;
//		}
//		
//		for (Volume volume : volumeArray) {
//			if ((volume.getSubStatus() & EVSS_SYSTEM) > 0) {
//				return removeEndSlash(volume.getName());
//			}
//		}
//		
//		return null;
//	}

	//////////////////////////////////////////////////////////////////////////

	protected void unapplyBackupSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyBackupSettings(): enter" );
			
			String configFilePath =
				ServiceContext.getInstance().getBackupConfigurationFilePath();
			File configFile = new File( configFilePath );
			if (configFile.exists())
			{	
				if(tryDeleteFile(configFile)) {
					BackupService.getInstance().cleanBackupConfiguration();
				}
			}
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyBackupSettings(): Backup settings unapplied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyBackupSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.BackupSettings,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyBackupSettings(): error" );
		
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.BackupSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyBackupSettings(): exit" );
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	ArchiveConfiguration configuration = null;
	protected ArchiveConfiguration getArchiveSettings() throws Exception
	{
		if(configuration==null)
		{
			configuration =_getArchiveSettings();
		}
		/**
		 * [Robin] We need to fill backup the backup dest and volumes into ArchiveConfiguration
		 *  since the XML section of it does not contain these things. 
		 *  In D2D, the UI will fill	in these things and go through to backup end. D2D has no such case:
		 *  1. UI -> backend -> XML
		 *  2. XML -> backend -> backend */
		BackupConfiguration backupSettings = this.getBackupSettings();
		if(backupSettings!=null&&configuration!=null){
			configuration.setbackupDestination(backupSettings.getDestination());
			configuration.setBackupVolumes(backupSettings.getBackupVolumes());
		}
		return configuration;
	}

	protected ArchiveConfiguration _getArchiveSettings() throws Exception{
		Document archiveSettingsDocument =
				this.policyXmlObject.getSettingsSection(
					PolicyXmlObject.PolicyXmlSectionNames.ArchivingSettings );
			
			if (archiveSettingsDocument == null)
				return null;
			
			ArchiveConfigXMLParser archiveConfigXMLParser = new ArchiveConfigXMLParser();
			
			return	archiveConfigXMLParser.loadXML( archiveSettingsDocument );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	protected boolean validateArchiveSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateArchiveSettings(): enter" );
			
			ArchiveConfiguration configuration = getArchiveSettings();
			
			if (configuration == null)
			{
				logUtility.writeLog( LogUtility.LogTypes.Debug,
					"validateArchiveSettings(): No archive settings." );
				return true;
			}
			
			ArchiveService.getInstance().validateArchiveConfiguration( configuration );
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateArchiveSettings(): Archive settings validation passed." );
			
			return true;
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"validateArchiveSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Archiving,
				e.getErrorCode(),
				e.getMultipleArguments() );
			
			return false;
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"validateArchiveSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Archiving,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
			
			return false;
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateArchiveSettings(): exit" );
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	protected void applyArchiveSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyArchiveSettings(): enter" );
			
			ArchiveConfiguration configuration = getArchiveSettings();

			if (configuration == null||this.getBackupSettings().isD2dOrRPSDestType()==false)
			{
				logUtility.writeLog( LogUtility.LogTypes.Debug,
					"applyArchiveSettings(): No archive settings." );
				
				unapplyArchiveSettings();
				return;
			}
			
			ArchiveService.getInstance().saveArchiveConfiguration( configuration );
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyArchiveSettings(): Archive settings applied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyArchiveSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Archiving,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyArchiveSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Archiving,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyArchiveSettings(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	protected void unapplyArchiveSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyArchiveSettings(): enter" );
			ArchiveService.getInstance().removeArchiveConfiguration();
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyArchiveSettings(): Archive settings unapplied ok." );
		}
//		catch (ServiceException e)
//		{
//			logUtility.writeLog( LogUtility.LogTypes.Error, e,
//				"unapplyArchiveSettings(): error" );
//			
//			this.addError( null,
//				ID2DPolicyManagementService.SettingsTypes.Archiving,
//				e.getErrorCode(),
//				e.getMultipleArguments() );
//		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyArchiveSettings(): error" );
		
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Archiving,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyArchiveSettings(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	protected boolean validateScheduledExportSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateScheduledExportSettings(): enter" );

			ScheduledExportConfiguration configuration = getScheduledExportSettings();
			
			if (configuration == null)
				return true;
			
			CopyService.getInstance().validateScheduledExportConfiguration( configuration );
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateScheduledExportSettings(): Backup settings validation passed." );
			
			return true;
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"validateScheduledExportSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ScheduledExportSettings,
				e.getErrorCode(),
				e.getMultipleArguments() );
			
			return false;
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"validateScheduledExportSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ScheduledExportSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
			
			return false;
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateScheduledExportSettings(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////

	protected void applyScheduledExportSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyScheduledExportSettings(): enter" );

			ScheduledExportConfiguration configuration = getScheduledExportSettings();
			
			if (configuration == null)
			{
				logUtility.writeLog( LogUtility.LogTypes.Debug,
					"applyScheduledExportSettings(): No scheduled export settings." );
				
				unapplyScheduledExportSettings();
				return;
			}
			
			CopyService.getInstance().saveScheduledExportConfiguration( configuration );
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyScheduledExportSettings(): Backup settings applied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyScheduledExportSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ScheduledExportSettings,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyScheduledExportSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ScheduledExportSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyScheduledExportSettings(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	protected void unapplyScheduledExportSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyScheduledExportSettings(): enter" );
			
			String configFilePath =
				ServiceContext.getInstance().getScheduledExportConfigurationPath();
			File configFile = new File( configFilePath );
			if (configFile.exists())
			{
				if(tryDeleteFile(configFile))
					CopyService.getInstance().clearCachedConfiguration();
			}
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyScheduledExportSettings(): Scheduled export settings unapplied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyScheduleExportSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ScheduledExportSettings,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyScheduledExportSettings(): error" );
		
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ScheduledExportSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyScheduledExportSettings(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	protected PreferencesConfiguration getPreferenceSettings() throws Exception
	{

		Document preferencesSettingsDocument = this.policyXmlObject
				.getSettingsSection(PolicyXmlObject.PolicyXmlSectionNames.PreferencesSettings);

		BackupConfigurationXMLDAO backupConfigXmlDao = new BackupConfigurationXMLDAO();
		PreferencesConfiguration preferencesConfig = backupConfigXmlDao
				.XmlDocumentToPreferencesSettings(preferencesSettingsDocument);

		return preferencesConfig;
	}

	//////////////////////////////////////////////////////////////////////////

	protected boolean validatePreferencesSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validatePreferencesSettings(): enter" );
			
			PreferencesConfiguration preferencesConfig = getPreferenceSettings();
			CommonService.getInstance().validatePreferences( preferencesConfig );
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validatePreferencesSettings(): Preferences settings validation passed." );
			
			return true;
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"validatePreferencesSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Preferences,
				e.getErrorCode(),
				e.getMultipleArguments() );
			
			return false;
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"validatePreferencesSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Preferences,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
			
			return false;
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validatePreferencesSettings(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////

	protected void applyPreferencesSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyPreferencesSettings(): enter" );
			
			PreferencesConfiguration preferencesConfig = getPreferenceSettings();
			CommonService.getInstance().savePreferences( preferencesConfig, true);
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyPreferencesSettings(): Preferences settings applied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyPreferencesSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Preferences,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyPreferencesSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Preferences,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyPreferencesSettings(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////

	protected void unapplyPreferencesSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyPreferencesSettings(): enter" );
			
			String configFilePath =
				ServiceContext.getInstance().getPreferencesConfigurationFilePath();
			File configFile = new File( configFilePath );
			if (configFile.exists())
			{
				if(tryDeleteFile(configFile))
					CommonService.getInstance().cleanupPreferenceConfiguration();
			}
			
			String updateCfgFilePath = ServiceContext.getInstance().getAutoUpdateSettingsFilePath();
			File updateCfgFile = new File( updateCfgFilePath );
			if( updateCfgFile.exists() )
				tryDeleteFile( updateCfgFile );
			
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyPreferencesSettings(): Preferences settings unapplied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyPreferenceSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Preferences,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyPreferencesSettings(): error" );
		
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.Preferences,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyPreferencesSettings(): exit" );
		}
	}
}
