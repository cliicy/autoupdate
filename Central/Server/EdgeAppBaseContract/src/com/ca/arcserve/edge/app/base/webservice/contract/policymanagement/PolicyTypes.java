package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;

public class PolicyTypes
{
	public static final int UnKnowType			= 0;
	public static final int BackupAndArchiving	= 1;
	public static final int VCM					= 2;
	public static final int VMBackup			= 3;
	public static final int RemoteVCM			= 4;
	public static final int Rps 				= 5;
	public static final int Unified  			= 6;
	public static final int Unified_MSP			= 7;
	public static final int RemoteVCMForRHA		= 8;
	
	public static boolean isVCMPolicy(int policyType) {
		return (VCM == policyType || RemoteVCM == policyType || RemoteVCMForRHA == policyType);
	}
	
	public static boolean isRemoteVCMPolicy(int policyType) {
		return (RemoteVCM == policyType || RemoteVCMForRHA == policyType);
	}

	public static PlanTaskType getPlanTaskType(int policyType) {
		if (policyType == VCM) {
			return PlanTaskType.LocalConversion;
		}
		if (policyType == RemoteVCM || policyType == RemoteVCMForRHA) {
			return PlanTaskType.RemoteConversion;
		}
		// If other task types needs this, please remember to add converter code here
		return null;
	}
}
