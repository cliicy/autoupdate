package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ConversionTask;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationSession;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationUtils;

public class ConversionTaskValidator implements ITaskValidator
{
	private static Logger logger = Logger.getLogger( ConversionTaskValidator.class );

	@Override
	public ValidationError validate( UnifiedPolicy policy, TaskType taskType, int taskIndex,
		ValidationSession validationSession )
	{
		logger.info( "Begin to validate conversion task. Task: " + taskType + ", Task index: " + taskIndex );
		
		assert policy != null : "policy is null";
		
		BackupConfiguration backupConfig = policy.getBackupConfiguration();
		if (backupConfig == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_BackupConfigIsNull, null,
				"Backup configuration is null." );
		
		ConversionTask conversionConfig = policy.getConversionConfiguration();
		if (conversionConfig == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_ConversionConfigIsNull, null,
				"Conversion configuration is null." );
		
		JobScriptCombo conversionJobScript = conversionConfig.getConversionJobScript();
		if (conversionJobScript == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_ConversionJobScriptIsNull, null,
				"Conversion job script is null." );
		
		// validate hypervisor
		
		ReplicationJobScript repJobScript = conversionJobScript.getRepJobScript();
		if (repJobScript == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_ReplicationJobScriptIsNull, null,
				"Replication job script is null." );
		
		if ((repJobScript.getReplicationDestination() == null) ||
			repJobScript.getReplicationDestination().isEmpty())
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidReplicationDestination, null,
				"Invalid replication destination." );
		ReplicationDestination repDest = repJobScript.getReplicationDestination().get( 0 );
		if (repDest instanceof VMwareESXStorage) // ESX server
		{
			VMwareESXStorage dest = (VMwareESXStorage) repDest;
			if (ValidationUtils.isStringNullOrEmpty( dest.getESXHostName() ) ||
				ValidationUtils.isStringNullOrEmpty( dest.getESXUserName() ))
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidReplicationDestination, null,
					"Invalid replication destination." );
		}
		else if (repDest instanceof VMwareVirtualCenterStorage) // vCenter
		{
			VMwareVirtualCenterStorage dest = (VMwareVirtualCenterStorage) repDest;
			if (ValidationUtils.isStringNullOrEmpty( dest.getVirtualCenterHostName() ) ||
				ValidationUtils.isStringNullOrEmpty( dest.getVirtualCenterUserName() ) ||
				ValidationUtils.isStringNullOrEmpty( dest.getEsxName() ))
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidReplicationDestination, null,
					"Invalid replication destination." );
		}
		else if (repDest instanceof ARCFlashStorage)
		{
			ARCFlashStorage dest = (ARCFlashStorage) repDest;
			if (ValidationUtils.isStringNullOrEmpty( dest.getHostName() ) ||
				ValidationUtils.isStringNullOrEmpty( dest.getUserName() ))
				return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidReplicationDestination, null,
					"Invalid replication destination." );
		}
		
		// validate monitor
		
		if (taskType == TaskType.Conversion)
		{
			VirtualizationType virtualType = conversionConfig.getConversionJobScript().getRepJobScript().getVirtualType();
			if ((virtualType == VirtualizationType.VMwareESX) || (virtualType == VirtualizationType.VMwareVirtualCenter))
			{
				HeartBeatJobScript hbJobScript = conversionJobScript.getHbJobScript();
				if (hbJobScript == null)
					return new ValidationError( EdgeServiceErrorCode.PolicyValidation_HeartbeatJobScriptIsNull, null,
						"Heartbeat job script is null." );
				
				if (ValidationUtils.isStringNullOrEmpty( hbJobScript.getHeartBeatMonitorHostName() ) ||
					ValidationUtils.isStringNullOrEmpty( hbJobScript.getHeartBeatMonitorUserName() ))
					return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidHeartbeatMonitorInfo, null,
						"Invalid heartbeat monitor info." );
			}
		}
		
		return null;
	}
}
