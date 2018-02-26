package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class JobTypeForGroupByPlan implements Serializable,BeanModelTag{

	private static final long serialVersionUID = -8259099963651534011L;
	private long jobType;
	private JobStatusForPlan jobStatusForPlan;
	
	public long getJobType() {
		return jobType;
	}
	public void setJobType(long jobType) {
		this.jobType = jobType;
	}
	public JobStatusForPlan getJobStatusForPlan() {
		return jobStatusForPlan;
	}
	public void setJobStatusForPlan(JobStatusForPlan jobStatusForPlan) {
		this.jobStatusForPlan = jobStatusForPlan;
	}
	
}
