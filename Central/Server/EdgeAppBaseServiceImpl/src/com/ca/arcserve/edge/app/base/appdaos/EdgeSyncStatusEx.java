package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

public class EdgeSyncStatusEx extends EdgeSyncStatus {
	private Date last_update;

	/**
	 * @return the lastUpdate
	 */
	public Date getLast_update() {
		return last_update;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLast_update(Date lastUpdate) {
		this.last_update = lastUpdate;
	}
	
	
}
