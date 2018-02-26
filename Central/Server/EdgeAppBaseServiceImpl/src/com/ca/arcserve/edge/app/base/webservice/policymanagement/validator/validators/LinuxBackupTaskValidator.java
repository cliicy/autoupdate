package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.LinuxBackupLocationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.LinuxBackupSettings;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationSession;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationUtils;

public class LinuxBackupTaskValidator implements ITaskValidator
{
	private static Logger logger = Logger.getLogger( LinuxBackupTaskValidator.class );

	@Override
	public ValidationError validate( UnifiedPolicy policy, TaskType taskType, int taskIndex,
		ValidationSession validationSession )
	{
		logger.info( "Begin to validate linux backup task. Task index: " + taskIndex );
		
		assert policy != null : "policy is null";
		
		BackupConfiguration backupConfig = policy.getBackupConfiguration();
		if (backupConfig == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_BackupConfigIsNull, null,
				"Backup configuration is null." );
		
		LinuxBackupSettings linuxBackupConfig = policy.getLinuxBackupsetting();
		if (linuxBackupConfig == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_LinuxBackupConfigIsNull, null,
				"Linux backup configuration is null." );
		
		LinuxBackupLocationInfo locationInfo = linuxBackupConfig.getBackupLocationInfo();
		if ((locationInfo == null) ||
			ValidationUtils.isStringNullOrEmpty( locationInfo.getBackupDestLocation() ) ||
			ValidationUtils.isStringNullOrEmpty( locationInfo.getBackupDestUser() ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidBackupDest, null,
				"Invalid backup destination." );
		
		return null;
	}

}
