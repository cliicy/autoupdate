package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationSession;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationUtils;

public class MspServerReplicationTaskValidator implements ITaskValidator
{
	private static Logger logger = Logger.getLogger( MspServerReplicationTaskValidator.class );

	@Override
	public ValidationError validate( UnifiedPolicy policy, TaskType taskType, int taskIndex,
		ValidationSession validationSession )
	{
		logger.info( "Begin to validate MSP server replication task. Task index: " + taskIndex );
		
		assert policy != null : "policy is null";
		
		RPSReplicationSettings repSettings = policy.getMspServerReplicationSettings();
		if (repSettings == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_MSPServerReplicationSettingsIsNull, null,
				"MSP server replication settings is null." );
		
		if (!ValidationUtils.isHostInfoValid( repSettings.getHostName(), repSettings.getUserName() ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidRpsInfo, null,
				"Invalid RPS information." );
		
		if ((policy.getRpsPolices() == null) || policy.getRpsPolices().isEmpty())
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_NoRpsSettingsForMSPServerRep, null,
				"No RPS settings for MSP server replication." );
		
		RPSPolicy rpsPolicy = policy.getRpsPolices().get( 0 ).getRpsPolicy();
		ValidationError error = ReplicationTaskValidator.validateRpsPolicy( rpsPolicy );
		if (error != null)
			return error;
		
		validationSession.registerUsedRps( repSettings.getHostName() );
		
		return null;
	}

}
