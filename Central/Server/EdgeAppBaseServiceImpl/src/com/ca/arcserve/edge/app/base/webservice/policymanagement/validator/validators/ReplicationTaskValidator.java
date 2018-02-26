package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.policy.RPSDataStoreSettings;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcflash.webservice.data.backup.BaseBackupConfiguration;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationSession;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationUtils;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators.BackupTaskValidator.BackupConfigWrapper;

public class ReplicationTaskValidator implements ITaskValidator
{
	private static Logger logger = Logger.getLogger( ReplicationTaskValidator.class );

	@Override
	public ValidationError validate( UnifiedPolicy policy, TaskType taskType, int taskIndex,
		ValidationSession validationSession )
	{
		logger.info( "Begin to validate replication task. Task index: " + taskIndex +
			", Replication task index: " + validationSession.getValidatedReplicationCount() );
		
		assert policy != null : "policy is null";
		
		if (validationSession.getValidatedReplicationCount() == 0)
		{
			// Only need to check this when validate the first replication task.
			
			ValidationError error = validateBackupToRps( policy, taskIndex );
			if (error != null)
				return error;
		}
		
		// Check destination RPS
		
		RPSPolicy prevRpsPolicy =
			policy.getRpsPolices().get( validationSession.getValidatedReplicationCount() ).getRpsPolicy();
		
		RPSReplicationSettings prevRepSettings = prevRpsPolicy.getRpsSettings().getRpsReplicationSettings();
		if ((prevRepSettings == null) ||
			ValidationUtils.isHostInfoValid( prevRepSettings.getHostName(), prevRepSettings.getUserName() ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidReplicationSettings, null,
				"Invalid replication settings." );
		
		if (validationSession.isRpsUsed( prevRepSettings.getHostName() ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_RpsIsUsed, null,
				"RPS was used in this plan." );
		
		// Check destination data store
		
		if (policy.getRpsPolices().size() < validationSession.getValidatedReplicationCount() + 2)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_IncorrectRpsPolicyCount, null,
				"Incorrect RPS policy count." );
		
		RPSPolicy currRpsPolicy =
			policy.getRpsPolices().get( validationSession.getValidatedReplicationCount() + 1 ).getRpsPolicy();
		ValidationError error = validateRpsPolicy( currRpsPolicy );
		if (error != null)
			return error;
		
		validationSession.registerUsedRps( prevRepSettings.getHostName() );
		validationSession.increaseValidatedReplicationCount();

		return null;
	}
	
	public static ValidationError validateBackupToRps( UnifiedPolicy policy, int taskIndex )
	{
		BaseBackupConfiguration backupConfig = null;
		if (isReplicateFromNormalBackup( policy, taskIndex ))
		{
			backupConfig = policy.getBackupConfiguration();
			if (backupConfig == null)
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_BackupConfigIsNull, null,
					"Backup configuration is null." );
		}
		else if (isReplicateFromVSphereBackup( policy, taskIndex ))
		{
			backupConfig = policy.getVSphereBackupConfiguration();
			if (backupConfig == null)
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_BackupConfigIsNull, null,
					"Backup configuration is null." );
		}
		
		if ((backupConfig != null) && !isBackupToRps( backupConfig ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_BackupDestIsNotRps, null,
				"Backup destination is not RPS." );
		
		return null;
	}
	
	private static boolean isReplicateFromNormalBackup( UnifiedPolicy policy, int taskIndex )
	{
		assert taskIndex < policy.getTaskList().size() : "taskIndex out of range";
		
		for (int i = 0; i < taskIndex; i ++)
		{
			TaskType task = policy.getTaskList().get( i );
			if (task == TaskType.BackUP)
				return true;
		}
		
		return false;
	}
	
	private static boolean isReplicateFromVSphereBackup( UnifiedPolicy policy, int taskIndex )
	{
		assert taskIndex < policy.getTaskList().size() : "taskIndex out of range";
		
		for (int i = 0; i < taskIndex; i ++)
		{
			TaskType task = policy.getTaskList().get( i );
			if (task == TaskType.LinuxBackUP)
				return true;
		}
		
		return false;
	}
	
	private static boolean isBackupToRps( BaseBackupConfiguration backupConfig )
	{
		BackupConfigWrapper backupConfigWrapper = new BackupConfigWrapper( backupConfig );
		return !backupConfigWrapper.isD2dOrRPSDestType();
	}
	
	public static ValidationError validateRpsPolicy( RPSPolicy rpsPolicy )
	{
		if (rpsPolicy == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_RpsPolicyIsNull, null,
				"RPS policy is null." );
			
		if (rpsPolicy.getRpsSettings() == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_RpsSettingsIsNull, null,
				"RPS settings in RPS policy is null." );
		
		RPSDataStoreSettings dataStoreSettings = rpsPolicy.getRpsSettings().getRpsDataStoreSettings();
		if (dataStoreSettings == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_DataStoreSettingsIsNull, null,
				"Data store settings is null." );
		
		if (ValidationUtils.isStringNullOrEmpty( dataStoreSettings.getDataStoreName() ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidDataStoreInfo, null,
				"Invalid datastore info." );
		
		return null;
	}
}
