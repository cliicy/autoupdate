package com.ca.arcflash.webservice.edge.policymanagement;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.policymanagement.policyapplyers.AssureRecoveryPolicyApplyer;
import com.ca.arcflash.webservice.edge.policymanagement.policyapplyers.BackupAndArchivingPolicyApplyer;
import com.ca.arcflash.webservice.edge.policymanagement.policyapplyers.VCMPolicyApplyer;
import com.ca.arcflash.webservice.edge.policymanagement.policyapplyers.VMBackupPolicyApplyer;

public class PolicyApplyerFactory
{
	private static final Logger logger = Logger.getLogger( PolicyApplyerFactory.class );
	
	public static IPolicyApplyer createPolicyApplyer( int policyType )
	{
		switch (policyType)
		{
		case ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving:
			return new BackupAndArchivingPolicyApplyer();
			
		case ID2DPolicyManagementService.PolicyTypes.VCM:
			return new VCMPolicyApplyer( false );
			
		case ID2DPolicyManagementService.PolicyTypes.RemoteVCM:
		case ID2DPolicyManagementService.PolicyTypes.RemoteVCMForRHA:
			return new VCMPolicyApplyer( true );
			
		case ID2DPolicyManagementService.PolicyTypes.VMBackup:
			return new VMBackupPolicyApplyer();
		case ID2DPolicyManagementService.PolicyTypes.AssureRecovery:
			return new AssureRecoveryPolicyApplyer();
		}
		
		String logMessage = String.format(
			"Unsupported policy type. (Policy type: %d)", policyType );
		logger.error( logMessage );
		
		return null;
	}
}
