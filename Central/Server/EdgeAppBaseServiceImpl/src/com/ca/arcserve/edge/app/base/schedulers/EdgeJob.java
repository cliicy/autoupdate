package com.ca.arcserve.edge.app.base.schedulers;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import com.ca.arcflash.common.StringUtil;

public abstract  class EdgeJob implements Job {
	
	protected static final String TAG_JOB_ID = "JOB_ID";
	
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void createJobDetail(JobDetail jobDetail) {
		
		if(StringUtil.isEmptyOrNull(id)){
			id = java.util.UUID.randomUUID().toString();
		}
		
		jobDetail.getJobDataMap().put(TAG_JOB_ID, id);
		
	}
	
	protected void loadContextData(JobExecutionContext context) {
		if (context.getJobDetail().getJobDataMap().get(TAG_JOB_ID) instanceof String) {
			id = (String)context.getJobDetail().getJobDataMap().get(TAG_JOB_ID);
		}
	}
	
	protected boolean validateContextData() {
		if (StringUtil.isEmptyOrNull(id)) {
			return false;
		}
		return true;
	}
	
	
}
