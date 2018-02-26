package com.ca.arcserve.edge.app.base.webservice.d2djobstatus;

import com.ca.arcflash.webservice.data.merge.MergeStatus;

public class D2DMergeJobStatusCache extends AbstractJobStatusCache<MergeStatus> {
	
	private static D2DMergeJobStatusCache instance = new D2DMergeJobStatusCache();
	
	public static D2DMergeJobStatusCache getInstance() { 
		return instance;
	}
	
	private D2DMergeJobStatusCache() {
	}

	@Override
	protected void onTimeout(TimeoutJobStatus<MergeStatus> timeoutJobStatus) {
		if (!timeoutJobStatus.getJobStatus().isRunning()) {
			return;
		}
		
		int nodeId = timeoutJobStatus.getNodeId();
		MergeStatus dummyStatus = new MergeStatus();
		cache.replace(nodeId, timeoutJobStatus, createTimeoutJobStatus(nodeId, dummyStatus));
	}
	
	@Override
	protected boolean checkTimeout(MergeStatus status) {
		return status.isRunning();
	}

}
