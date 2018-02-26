package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;

import java.io.Serializable;

/**
 * Filters for querying job histories.
 */
public class JobHistoryFilter implements Serializable {

	private static final long serialVersionUID = 1084934550214286984L;
	
	private int jobId = -1; // -1 for all jobs
	private JobStatus jobStatus = JobStatus.All;
	private String datastoreUUID; // null for all datastores
	private int serverId = 0; //using it when we want get the recent event for one agent at one rps and one datastore
	
	/**
	 * Get job ID filter setting. Set it to -1 to query all jobs.
	 * 
	 * @return
	 */
	public int getJobId() {
		return jobId;
	}
	
	/**
	 * Set job ID filter setting. Set it to -1 to query all jobs.
	 * 
	 * @param jobId
	 */
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	
	/**
	 * Get job status filter setting.
	 * 
	 * @return
	 */
	public JobStatus getJobStatus() {
		return jobStatus;
	}
	
	/**
	 * Set job status filter setting.
	 * 
	 * @param jobStatus
	 */
	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	/**
	 * Get data store filter setting. Set it to null to query all data stores.
	 * 
	 * @return
	 */
	public String getDatastoreUUID() {
		return datastoreUUID;
	}
	
	/**
	 * Set data store filter setting. Set it to null to query all data stores.
	 * 
	 * @param datastoreUUID
	 */
	public void setDatastoreUUID(String datastoreUUID) {
		this.datastoreUUID = datastoreUUID;
	}
	
	/**
	 * Get RPS ID filter setting. Set it to 0 to query all RPSs. Use it when
	 * you want to get the recent events for one agent at one RPS and one
	 * data store.
	 * 
	 * @return
	 */
	public int getServerId() {
		return serverId;
	}
	
	/**
	 * Set RPS ID filter setting. Set it to 0 to query all RPSs. Use it when
	 * you want to get the recent events for one agent at one RPS and one
	 * data store.
	 * 
	 * @param serverId
	 */
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
}
