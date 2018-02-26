package com.ca.arcflash.webservice.edge.policymanagement.policyapplyers;

import java.io.File;
import java.util.List;

import com.ca.arcflash.service.common.CommonService;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.D2DConfiguration;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.LogUtility;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyUsageMarker;
import com.ca.arcflash.webservice.service.ArchiveToTapeService;
import com.ca.arcflash.webservice.service.DeleteArchiveService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;

public class BackupAndArchivingPolicyApplyerEx extends
		BackupAndArchivingPolicyApplyer {

	private D2DConfiguration d2dconfiguration;

	public BackupAndArchivingPolicyApplyerEx(D2DConfiguration configuration) {
		this.d2dconfiguration = configuration;

	}

	public void applyPolicy(List<PolicyDeploymentError> errorList,
			String policeUUID) {
		try {
			logUtility.writeLog(LogUtility.LogTypes.Debug,
					"applyPolicy() enter.");
			try {
				this.errorList = errorList;
				this.policyUsageMarker = PolicyUsageMarker.getInstance();
				this.policyUuid = policeUUID + ":" + POLICYVERSION;
				this.planId = policeUUID;
			} catch (Exception e) {
				logUtility.writeLog(LogUtility.LogTypes.Error, e,
						"Initialize common variables failed.");

				addGeneralError(null,
						ID2DPolicyManagementService.GenericErrors.InternalError);
				return;
			}
			logUtility.writeLog(LogUtility.LogTypes.Info,
					"applyPolicy(): before call doApplying().");
			doApplying();
			logUtility.writeLog(LogUtility.LogTypes.Info,
					"applyPolicy(): doApplying() returned.");
		} catch (Exception e) {
			logUtility.writeLog(LogUtility.LogTypes.Error, e,
					"applyPolicy() error.");
		} finally {
			logUtility.writeLog(LogUtility.LogTypes.Debug,
					"applyPolicy() exit.");
		}
	}

	@Override
	protected void doApplying() {
		super.backupConfig = null;
		super.configuration = null;
		if (validateSettings()) {
			applyBackupSettings();
			applyArchiveSettings();
			applyArchiveDeleteSettings();
			applyArchiveToTapeSettings();
			applyScheduledExportSettings();
			applyPreferencesSettings();
		}

		if (!hasError())
			savePolicyUuid();
	}

	@Override
	protected boolean validateSettings()
	{
		boolean result = super.validateSettings();
		
		if (!validateArchiveDeleteSettings())
			result = false;
		
		if (!validateArchiveToTapeSettings())
			result = false;
		
		return result;
	}

	protected boolean validateArchiveDeleteSettings(){
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateArchiveDeleteSettings(): enter" );
			
			ArchiveConfiguration configuration =this.d2dconfiguration.getArchiveDelConfiguration();
			
			if (configuration == null)
			{
				logUtility.writeLog( LogUtility.LogTypes.Debug,
					"validateArchiveDeleteSettings(): No archive delete settings." );
				return true;
			}
			
			//DeleteArchiveService.getInstance().validateConfiguration(configuration);
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateArchiveDeleteSettings(): Archive Delete settings validation passed." );
			
			return true;
		}
//		catch (ServiceException e)
//		{
//			logUtility.writeLog( LogUtility.LogTypes.Error, e,
//				"validateArchiveDeleteSettings(): error" );
//			
//			this.addError( null,
//				ID2DPolicyManagementService.SettingsTypes.Archiving,
//				e.getErrorCode(),
//				e.getMultipleArguments() );
//			
//			return false;
//		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"validateArchiveDeleteSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ArchiveFileSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
			
			return false;
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"validateArchiveDeleteSettings(): exit" );
		}
		
	}
	
	protected boolean validateArchiveToTapeSettings(){
		//FIXME
		return true;
	}
	
	
	
	@Override
	protected BackupConfiguration _getBackupSettings() {
		return this.d2dconfiguration.getBackupConfiguration();
	}

	@Override
	protected ArchiveConfiguration _getArchiveSettings() {
		return this.d2dconfiguration.getArchiveConfiguration();

	}

	@Override
	protected ScheduledExportConfiguration getScheduledExportSettings() {

		return this.d2dconfiguration.getScheduledExportConfiguration();
	}

	@Override
	protected PreferencesConfiguration getPreferenceSettings() {
		return this.d2dconfiguration.getPreferencesConfiguration();
	}

	protected void applyArchiveDeleteSettings() {
		
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyArchiveDeleteSettings(): enter" );
			
			ArchiveConfiguration configuration = this.d2dconfiguration.getArchiveDelConfiguration();

			if (configuration == null||this.d2dconfiguration.getBackupConfiguration().isD2dOrRPSDestType()==false)
			{
				logUtility.writeLog( LogUtility.LogTypes.Debug,
					"applyArchiveDeleteSettings(): No archive settings." );
				
				unapplyArchiveDeleteSettings();
				return;
			}
			
			DeleteArchiveService.getInstance().saveArchiveDelConfiguration(configuration);
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyArchiveDeleteSettings(): Archive settings applied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyArchiveDeleteSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ArchiveFileSettings,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyArchiveDeleteSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ArchiveFileSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyArchiveDeleteSettings(): exit" );
		}
	} 
	
	
	protected void unapplyArchiveDeleteSettings() {
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyArchiveDeleteSettings(): enter" );
			
			DeleteArchiveService.getInstance().removeArchiveDelConfiguration();
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyArchiveDeleteSettings(): Archive delete settings unapplied ok." );
		}
//		catch (ServiceException e)
//		{
//			logUtility.writeLog( LogUtility.LogTypes.Error, e,
//				"unapplyArchiveDeleteSettings(): error" );
//			
//			this.addError( null,
//				ID2DPolicyManagementService.SettingsTypes.ArchiveFileSettings,
//				e.getErrorCode(),
//				e.getMultipleArguments() );
//		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyArchiveDeleteSettings(): error" );
		
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ArchiveFileSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyArchiveDeleteSettings(): exit" );
		}
	}
	
	protected void unapplyArchiveToTapeSettings() {
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyArchiveToTapeSettings(): enter" );
			
			String configFilePath =
				ServiceContext.getInstance().getArchiveToTapeFilePath();
			File configFile = new File( configFilePath );
			if (configFile.exists())
			{
				tryDeleteFile(configFile);
			}
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyArchiveToTapeSettings(): Archive delete settings unapplied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyArchiveToTapeSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ArchiveToTapeSettings,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyArchiveToTapeSettings(): error" );
		
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ArchiveToTapeSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyArchiveToTapeSettings(): exit" );
		}
	}

	protected void applyArchiveToTapeSettings() {
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyArchiveToTapeSettings(): enter" );
			
			ArchiveConfig configuration = this.d2dconfiguration.getArchiveToTapeConfig();			
			if (configuration == null)
			{
				logUtility.writeLog( LogUtility.LogTypes.Debug,
					"applyArchiveToTapeSettings(): No archive settings." );
				
				unapplyArchiveToTapeSettings();
				return;
			}
			
			if (configuration.getSource() != null
					&& this.d2dconfiguration.getBackupConfiguration()
							.getDestination() != null&&this.getBackupSettings().isD2dOrRPSDestType()) {
				boolean isASBUAgentInstalled = CommonNativeInstance.getICommonNative().isASBUAgentInstalled();
				if (!isASBUAgentInstalled) {
					this.addWarning(
							null,
							ID2DPolicyManagementService.SettingsTypes.ArchiveToTapeSettings,
							FlashServiceErrorCode.ASBU_AGENT_NOT_INSTALLED,
							null);
				}
			}
			
			ArchiveToTapeService.getInstance().saveArchiveToTapeConfig(configuration);
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyArchiveToTapeSettings(): Archive settings applied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyArchiveToTapeSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ArchiveToTapeSettings,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyArchiveToTapeSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ArchiveToTapeSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyArchiveToTapeSettings(): exit" );
		}
	}
}
