package com.ca.arcserve.edge.app.base.webservice.jobhistory;

import java.util.List;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;

public class JobDetailMonitorResult {
	private JobDetail jobDetail;
	private List<FlashJobMonitor> monitorList;
	public JobDetailMonitorResult(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}	
	public JobDetail getJobDetail() {
		return jobDetail;
	}
	public List<FlashJobMonitor> getMonitorList() {
		return monitorList;
	}
	public void setMonitorList(List<FlashJobMonitor> monitorList) {
		this.monitorList = monitorList;
	}
}
