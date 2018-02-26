package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class NodeSyncStatus implements Serializable {
	private static final long serialVersionUID = -6895679061535933953L;
	private int status;
	private int changeStatus;

	/**
	 * Get synchronization status.
	 * See {@link com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus}
	 * for available values.
	 * 
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set synchronization status. See
	 * {@link com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus}
	 * for available values.
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public int getChangeStatus() {
		return changeStatus;
	}

	public void setChangeStatus(int changeStatus) {
		this.changeStatus = changeStatus;
	}
}
