package com.ca.arcflash.ui.client.model;

public enum RemoteDeployStatus {
	DEPLOY_PENDING_FOR_DEPLOY(-1), DEPLOY_NA(0), DEPLOY_IN_PROGRESS(1), DEPLOY_SUCCESS(2), DEPLOY_FAILED(3), DEPLOY_NOT_STARTED(
			4), DEPLOY_THIRD_PARTY(5), DEPLOY_COPYING_IMAGE(6), DEPLOY_WAITING(
			7), DEPLOY_TEMINATE(8);

	private final int value;

	RemoteDeployStatus(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static RemoteDeployStatus valueOf(int value) {
		for (RemoteDeployStatus status : RemoteDeployStatus.values()) {
			if (status.value() == value) {
				return status;
			}
		}
		return null;
	}

}
