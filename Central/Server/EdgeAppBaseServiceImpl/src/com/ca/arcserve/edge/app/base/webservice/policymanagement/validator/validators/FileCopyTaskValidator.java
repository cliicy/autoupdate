package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationSession;

public class FileCopyTaskValidator implements ITaskValidator
{
	private static Logger logger = Logger.getLogger( FileCopyTaskValidator.class );

	@Override
	public ValidationError validate( UnifiedPolicy policy, TaskType taskType, int taskIndex,
		ValidationSession validationSession )
	{
//		logger.info( "Begin to validate file copy task. Task index: " + taskIndex );
//		
//		assert policy != null : "policy is null";
//		
//		BackupConfiguration backupConfig = policy.getBackupConfiguration();
//		if (backupConfig == null)
//			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_BackupConfigIsNull, null,
//				"Backup configuration is null." );
//		
//		if (!backupConfig.isGenerateCatalog())
//			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_FSCatalogGenerationNotEnabled, null,
//				"File system catalog generation was not enabled." );
//		
//		List<FileCopySettingWrapper> archiveConfig = policy.getFileCopySettingsWrapper();
//		if (archiveConfig == null)
//			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_ArchiveConfigIsNull, null,
//				"Archive configuration is null." );
//		
//		if ((archiveConfig.getArchiveSources() == null) ||
//			(archiveConfig.getArchiveSources().length == 0))
//			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidArchiveSources, null,
//				"Invalid archive sources." );
//		
//		if (archiveConfig.isbArchiveToDrive() &&
//			((archiveConfig.getStrArchiveToDrivePath() == null) || archiveConfig.getStrArchiveToDrivePath().isEmpty()))
//			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidArchiveDest, null,
//				"Invalid archive destination." );
//		
//		if (archiveConfig.isbArchiveToCloud() && (archiveConfig.getCloudConfig() == null))
//			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidArchiveDest, null,
//				"Invalid archive destination." );
		
		return null;
	}

}
