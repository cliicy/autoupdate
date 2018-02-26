package com.ca.arcserve.edge.app.base.webservice.monitor.model;

import java.util.List;

public class JobMonitorResult {
	private List<JobMonitorResultModel> JobMonResult;

	public List<JobMonitorResultModel> getJobMonResult() {
		return JobMonResult;
	}

	public void setJobMonResult(List<JobMonitorResultModel> jobMonResult) {
		JobMonResult = jobMonResult;
	}
}
