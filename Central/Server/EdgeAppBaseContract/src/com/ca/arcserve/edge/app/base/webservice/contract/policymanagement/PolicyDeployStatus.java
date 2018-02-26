package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

public class PolicyDeployStatus
{
	public static final int ToBeDeployed			= 1;
	public static final int Deploying				= 2;
	public static final int DeployedSuccessfully	= 3;
	public static final int DeploymentFailed		= 4;
	public static final int DeployingD2D			= 5;
	public static final int DeployD2DSucceed		= 6;
	public static final int DeployD2DFailed			= 7;
	public static final int DeployD2DRebooting		= 8;
	
	public static final int ToBeDeployAsScheduled = 12;
	public static final int DeployFaileBecauseOtherEdge		= 41;
	
	public static final int CreateRPSPolicy_Pending = 20;
	public static final int CreateRPSPolicy_Creating = 21;
	public static final int CreateRPSPolicy_Succeed = 22;
	public static final int CreateRPSPolicy_Failed = 23;
	public static final int CreateASBUPolicy_Creating = 24;
	public static final int CreateASBUPolicy_Failed = 25;
	
	public static boolean isInPrograss(int status){
		if(status == Deploying
				|| status == DeployingD2D
				|| status == DeployD2DRebooting
				|| status == ToBeDeployAsScheduled
				|| status == CreateRPSPolicy_Pending
				|| status == CreateRPSPolicy_Creating
				|| status == CreateRPSPolicy_Succeed
			    || status == CreateASBUPolicy_Creating)
			return true;
		return false;
	}
	
	public static boolean isFailed(int status){
		if(status == DeploymentFailed
				|| status == DeployD2DFailed
				|| status == DeployFaileBecauseOtherEdge
				|| status == CreateRPSPolicy_Failed
			    || status == CreateASBUPolicy_Failed)
			return true;
		return false;
	}
}
