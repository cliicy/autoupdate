package com.ca.arcserve.edge.app.base.webservice.contract.node;

public enum DeployStatus {
	
	// Status codes defined by Setup team
	
	DEPLOY_PENDING_FOR_DEPLOY(-1), 
	DEPLOY_NA(0), 
	DEPLOY_CONNECTING(1), 
	DEPLOY_INSTALLING(2), 
	DEPLOY_SUCCESS(3), 
	DEPLOY_FAILED(4), 
	DEPLOY_NOT_STARTED(5),//?
	DEPLOY_THIRD_PARTY(6), //?
	DEPLOY_COPYING_IMAGE(7), 
	DEPLOY_PENDING(8), //
	
	DEPLOY_REBOOTING(9),
	
	DEPLOY_SUCCESS_NEEDREBOOT(10),
	DEPLOY_SUCCESS_SUPPRESSREBOOT(11),
	DEPLOY_COMPLETE_REBOOTTIMEOUT(12),
	DEPLOY_DIRECT_CONNECT_WS(13),
	
	//Wait schedule to deploy
	DEPLOY_PENDING_SCHEDULE(14),
	
	DEPLOY_CONNECT_WS_TIMEOUT(15),
	
	//Fail phases
	DEPLOY_FAIL_ON_PENDING(19), 
	DEPLOY_FAIL_ON_CONNECTING(21),
	DEPLOY_FAIL_ON_INSTALLING(22),
	DEPLOY_FAIL_ON_COPYING_IMAGE(27),

	
	
	DEPLOY_DOWNLOADING_IMAGE(30),
	DEPLOY_DOWNLOADING_IMAGE_FAILED(31),
	DEPLOY_PREPARATION_DONE(32),
	DEPLOY_FINISHED(33);
	
	private final int value;

	DeployStatus(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static DeployStatus valueOf(int value) {
		for (DeployStatus status : DeployStatus.values()) {
			if (status.value() == value) {
				return status;
			}
		}
		return null;
	}
	
	public static boolean isInProgress(int value){
		if(value==DEPLOY_PENDING_FOR_DEPLOY.value
				|| value==DEPLOY_DOWNLOADING_IMAGE.value
				|| value==DEPLOY_CONNECTING.value
				|| value==DEPLOY_INSTALLING.value
				|| value==DEPLOY_THIRD_PARTY.value
				|| value==DEPLOY_COPYING_IMAGE.value
				|| value==DEPLOY_PENDING.value
				|| value==DEPLOY_REBOOTING.value
				|| value==DEPLOY_DIRECT_CONNECT_WS.value)
			return true;
		else
			return false;
	}
	
	public static boolean isFailed(int value){
		if(value==DEPLOY_FAILED.value
				|| value==DEPLOY_FAIL_ON_PENDING.value 
				|| value==DEPLOY_DOWNLOADING_IMAGE_FAILED.value
				|| value==DEPLOY_FAIL_ON_CONNECTING.value
				|| value==DEPLOY_FAIL_ON_INSTALLING.value
				|| value==DEPLOY_FAIL_ON_COPYING_IMAGE.value
				|| value==DEPLOY_DOWNLOADING_IMAGE_FAILED.value)
			return true;
		else
			return false;
	}
	
	public static boolean canWaitDeployProcess( DeployStatus value )
	{
		if ((value == DEPLOY_PENDING_FOR_DEPLOY) ||
			(value == DEPLOY_DOWNLOADING_IMAGE) ||
			(value == DEPLOY_DOWNLOADING_IMAGE_FAILED) )
//			||
//			(value == DEPLOY_NA))
			return false;
		
		return true;
	}
}
