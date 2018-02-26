package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereProxy;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationSession;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationUtils;

public class VSphereBackupTaskValidator implements ITaskValidator
{
	private static Logger logger = Logger.getLogger( VSphereBackupTaskValidator.class );

	@Override
	public ValidationError validate( UnifiedPolicy policy, TaskType taskType, int taskIndex,
		ValidationSession validationSession )
	{
		logger.info( "Begin to validate vSphere backup task. Task index: " + taskIndex );
		
		assert policy != null : "policy is null";
		
		VSphereBackupConfiguration backupConfig = policy.getVSphereBackupConfiguration();
		if (backupConfig == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_BackupConfigIsNull, null,
				"Backup configuration is null." );
		
		if (backupConfig.getvSphereProxy() == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidVSphereProxyInfo, null,
				"Invalid vSphere proxy info." );
		VSphereProxy proxy = backupConfig.getvSphereProxy();
		if (ValidationUtils.isStringNullOrEmpty( proxy.getVSphereProxyName() ) ||
			ValidationUtils.isStringNullOrEmpty( proxy.getVSphereProxyUsername() ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidVSphereProxyInfo, null,
				"Invalid vSphere proxy info." );
		
		ValidationError error = BackupTaskValidator.validateBackupDestination(
			policy, backupConfig, validationSession );
		if (error != null)
			return error;
		
		return null;
	}

}
