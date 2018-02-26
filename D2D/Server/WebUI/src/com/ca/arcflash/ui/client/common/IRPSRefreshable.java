package com.ca.arcflash.ui.client.common;

public interface IRPSRefreshable {	
	//catalog complete
	int CS_CATALOG_FINISHED = 0;
	//replication complete
	int CS_REPLICATION_FINISHED = 1;
	//merge complete
	int CS_MERGE_FINISHED = 2;
	//all job complete
	int CS_ALL_JOB_FINISHED = 3;
	//policy changed
	int CS_POLICY_CHANGED = 4;
	//datastore changed
	int CS_DATASTORE_CHANGED = 5;
	
	
	public void refresh(Object data);
	
	/**
	 * Refresh different part of depending on the change source
	 * @param data: the data used for refresh
	 * @param changeSource: the source that causes this change, it's defined as above
	 */
	public void refresh(Object data, int changeSource);
}
