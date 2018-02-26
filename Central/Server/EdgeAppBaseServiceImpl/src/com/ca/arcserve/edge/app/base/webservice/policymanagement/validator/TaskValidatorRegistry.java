package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator;

import java.util.HashMap;
import java.util.Map;

import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators.BackupTaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators.ConversionTaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators.CopyRecoveryPointsTaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators.LinuxBackupTaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators.MspClientReplicationTaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators.ReplicationTaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators.VSphereBackupTaskValidator;

public class TaskValidatorRegistry
{
	private static Map<TaskType, ITaskValidator> validatorMap = new HashMap<TaskType, ITaskValidator>();
	
	static
	{
		validatorMap.put( TaskType.BackUP,					new BackupTaskValidator() );
		validatorMap.put( TaskType.VSphereBackUP,			new VSphereBackupTaskValidator() );
		validatorMap.put( TaskType.LinuxBackUP,				new LinuxBackupTaskValidator() );
		validatorMap.put( TaskType.Replication,				new ReplicationTaskValidator() );
		validatorMap.put( TaskType.FileCopy,				new ReplicationTaskValidator() );
		validatorMap.put( TaskType.CopyRecoveryPoints,		new CopyRecoveryPointsTaskValidator() );
		validatorMap.put( TaskType.Conversion,				new ConversionTaskValidator() );
		validatorMap.put( TaskType.RemoteConversion,		new ConversionTaskValidator() );
		validatorMap.put( TaskType.RemoteConversionForRHA,	new ConversionTaskValidator() );
		validatorMap.put( TaskType.MspServerReplication,	new ReplicationTaskValidator() );
		validatorMap.put( TaskType.MspClientReplication,	new MspClientReplicationTaskValidator() );
	}
	
	public static ITaskValidator getValidator( TaskType taskType )
	{
		return validatorMap.get( taskType );
	}
}
