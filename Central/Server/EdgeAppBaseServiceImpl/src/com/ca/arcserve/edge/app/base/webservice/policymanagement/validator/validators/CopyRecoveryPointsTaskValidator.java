package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationSession;

public class CopyRecoveryPointsTaskValidator implements ITaskValidator
{
	private static Logger logger = Logger.getLogger( CopyRecoveryPointsTaskValidator.class );

	@Override
	public ValidationError validate( UnifiedPolicy policy, TaskType taskType, int taskIndex,
		ValidationSession validationSession )
	{
		logger.info( "Begin to validate copy recovery point task. Task index: " + taskIndex );
		
		assert policy != null : "policy is null";
		
		BackupConfiguration backupConfig = policy.getBackupConfiguration();
		if (backupConfig == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_BackupConfigIsNull, null,
				"Backup configuration is null." );
		
		ScheduledExportConfiguration exportConfig = policy.getExportConfiguration();
		if (exportConfig == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_ExportConfigIsNull, null,
				"Export configuration is null." );
		
		String exportDest = exportConfig.getDestination();
		if ((exportDest == null) || exportDest.trim().isEmpty())
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidExportDest, null,
				"Invalid export destination." );
		
		if ((backupConfig.getDestination() != null) &&
			exportDest.trim().equals( backupConfig.getDestination() ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_ExportDestIsSameWithBackupDest, null,
				"Export destination is same with backup destination." );
		
		return null;
	}

}
