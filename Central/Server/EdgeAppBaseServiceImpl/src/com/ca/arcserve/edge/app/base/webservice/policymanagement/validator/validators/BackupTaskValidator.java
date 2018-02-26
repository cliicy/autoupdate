package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BaseBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.BaseVSpherePolicy;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationSession;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationUtils;

/**
 * Policy validator for backup for windows
 * @author panbo01
 *
 */
public class BackupTaskValidator implements ITaskValidator
{
	private static Logger logger = Logger.getLogger( BackupTaskValidator.class );

	@Override
	public ValidationError validate( UnifiedPolicy policy, TaskType taskType, int taskIndex,
		ValidationSession validationSession )
	{
		logger.info( "Begin to validate backup task. Task index: " + taskIndex );
		
		assert policy != null : "policy is null";
		assert validationSession != null : "validationSession is null";
		
		BackupConfiguration backupConfig = policy.getBackupConfiguration();
		if (backupConfig == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_BackupConfigIsNull, null,
				"Backup configuration is null." );
		
		ValidationError error = validateBackupDestination(
			policy, backupConfig, validationSession );
		if (error != null)
			return error;
		
		return null;
	}
	
	public static ValidationError validateBackupDestination(
		UnifiedPolicy policy, BaseBackupConfiguration backupConfig, ValidationSession validationSession )
	{
		assert policy != null : "policy is null";
		assert backupConfig != null : "backupConfig is null";
		assert validationSession != null : "validationSession is null";
		
		BackupConfigWrapper backupConfigWrapper = new BackupConfigWrapper( backupConfig );
		
		boolean isBackupToRps = !backupConfigWrapper.isD2dOrRPSDestType();
		if (!isBackupToRps)
		{
			if (ValidationUtils.isStringNullOrEmpty( backupConfigWrapper.getDestination() ))
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidBackupDest, null,
					"Invalid backup destination." );
		}
		else // backup to RPS
		{
			BackupRPSDestSetting rpsDestSettings = backupConfigWrapper.getBackupRpsDestSetting();
			if (rpsDestSettings == null)
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_NoRpsDestSettings, null,
					"No RPS destination settings." );
			
			if ((rpsDestSettings.getRpsHost() == null) ||
				!ValidationUtils.isHostInfoValid( rpsDestSettings.getRpsHost().getRhostname(), rpsDestSettings.getRpsHost().getUsername() ))
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidRpsInfo, null,
					"Invalid RPS info." );
			
			if (ValidationUtils.isStringNullOrEmpty( rpsDestSettings.getRPSDataStore() ))
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidDataStoreInfo, null,
					"Invalid datastore info." );
			
			validationSession.registerUsedRps( rpsDestSettings.getRpsHost().getRhostname() );
			
			if (ValidationUtils.isStringNullOrEmpty( backupConfigWrapper.getEncryptionKey() ))
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_NoSessionPassword, null,
					"No session password." );

			if ((policy.getRpsPolices() == null) || policy.getRpsPolices().isEmpty())
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_NoRpsSettingsForBackup, null,
					"No RPS settings for backup." );
			
			RPSPolicy rpsPolicy = policy.getRpsPolices().get( 0 ).getRpsPolicy();
			ValidationError error = ReplicationTaskValidator.validateRpsPolicy( rpsPolicy );
			if (error != null)
				return error;
			
			rpsPolicy.getRpsSettings().getRpsDataStoreSettings().setDataStoreName(
				rpsDestSettings.getRPSDataStore() );
		}
		
		return null;
	}

	
	/**
	 * This is a wrapper for backup configurations in order to provide some
	 * unified interfaces. This is a temporary solution before the
	 * BackupConfiguration and BaseVSpherePolicy got merged. Once these classes
	 * got consolidated into BaseBackupConfiguration, these APIs can be called
	 * directly on the backup configuration object. 
	 * 
	 * Pang, Bo (panbo01)
	 * 2014-09-11
	 */
	public static class BackupConfigWrapper
	{
		private BaseBackupConfiguration backupConfig;
		
		public BackupConfigWrapper( BaseBackupConfiguration backupConfig )
		{
			this.backupConfig = backupConfig;
		}
		
		public boolean isD2dOrRPSDestType()
		{
			if (backupConfig instanceof BackupConfiguration)
				return ((BackupConfiguration)backupConfig).isD2dOrRPSDestType();
			
			else if (backupConfig instanceof BaseVSpherePolicy)
				return ((BaseVSpherePolicy)backupConfig).isD2dOrRPSDestType();
			
			assert false : "Unhandled backup configuration.";
			return true;
		}
		
		public String getDestination()
		{
			if (backupConfig instanceof BackupConfiguration)
				return ((BackupConfiguration)backupConfig).getDestination();
			
			else if (backupConfig instanceof BaseVSpherePolicy)
				return ((BaseVSpherePolicy)backupConfig).getDestination();
			
			assert false : "Unhandled backup configuration.";
			return "";
		}
		
		public String getEncryptionKey()
		{
			if (backupConfig instanceof BackupConfiguration)
				return ((BackupConfiguration)backupConfig).getEncryptionKey();
			
			else if (backupConfig instanceof BaseVSpherePolicy)
				return ((BaseVSpherePolicy)backupConfig).getEncryptionKey();
			
			assert false : "Unhandled backup configuration.";
			return "";
		}
		
		public BackupRPSDestSetting getBackupRpsDestSetting()
		{
			if (backupConfig instanceof BackupConfiguration)
				return ((BackupConfiguration)backupConfig).getBackupRpsDestSetting();
			
			else if (backupConfig instanceof BaseVSpherePolicy)
				return ((BaseVSpherePolicy)backupConfig).getBackupRpsDestSetting();
			
			assert false : "Unhandled backup configuration.";
			return null;
		}
	}
}
