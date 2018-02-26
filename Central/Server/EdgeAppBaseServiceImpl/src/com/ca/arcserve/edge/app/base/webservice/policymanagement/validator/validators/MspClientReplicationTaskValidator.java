package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.validators;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.MspPlanSettings;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationSession;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationUtils;
import com.ca.arcserve.edge.app.msp.webservice.contract.MspReplicationDestination;

public class MspClientReplicationTaskValidator implements ITaskValidator
{
	private static Logger logger = Logger.getLogger( MspClientReplicationTaskValidator.class );

	@Override
	public ValidationError validate( UnifiedPolicy policy, TaskType taskType,
		int taskIndex, ValidationSession validationSession )
	{
		logger.info( "Begin to validate MSP client replication task. Task index: " + taskIndex );
		
		assert policy != null : "policy is null";
		
		ValidationError error;
		
		error = ReplicationTaskValidator.validateBackupToRps( policy, taskIndex );
		if (error != null)
			return error;
		
		MspReplicationDestination repDest = policy.getMspReplicationDestination();
		if (repDest == null)
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_NoMspRpsDestSettings, null,
				"No MSP RPS destination settings." );
		
		RpsHost rpsHost = repDest.getReplicationServer();
		RPSPolicy rpsPolicy = repDest.getReplicationRpsPolicy();
		MspPlanSettings planSettings = repDest.getMspPlanSettings();
		
		if (!ValidationUtils.isHostInfoValid( rpsHost.getRhostname(), rpsHost.getUsername() ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidMspHost, null,
				"Invalid MSP host." );
		
		if (ValidationUtils.isStringNullOrEmpty( planSettings.getMspPlanName() ) ||
			ValidationUtils.isStringNullOrEmpty( planSettings.getMspPlanUuid() ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_InvalidMspPlan, null,
				"Invalid MSP plan." );
		
		if (ValidationUtils.isStringNullOrEmpty( rpsPolicy.getName() ) ||
			ValidationUtils.isStringNullOrEmpty( rpsPolicy.getPlanUUID() ) ||
			!rpsPolicy.getName().equals( planSettings.getMspPlanName() ) ||
			!rpsPolicy.getPlanUUID().equals( planSettings.getMspPlanUuid() ))
			return new ValidationError( EdgeServiceErrorCode.PolicyValidation_PlanInfoDiffThanMspPlanSettings, null,
				"Plan info in replication RPS policy inconsistent with MSP plan settings." );
		
		error = ReplicationTaskValidator.validateRpsPolicy( rpsPolicy );
		if (error != null)
			return error;
		
		return null;
	}

}
