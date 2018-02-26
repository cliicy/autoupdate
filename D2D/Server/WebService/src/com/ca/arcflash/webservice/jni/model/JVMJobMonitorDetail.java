package com.ca.arcflash.webservice.jni.model;

import com.ca.arcflash.webservice.service.internal.VSphereJobContext;

public class JVMJobMonitorDetail {
	private JJobMonitor jJobMonitor;
	private VSphereJobContext jobContext;
	
	public JVMJobMonitorDetail(JJobMonitor jJobMonitor, VSphereJobContext jobContext){
		this.jJobMonitor = jJobMonitor;
		this.jobContext = jobContext;
	}
	
	public JJobMonitor getjJobMonitor() {
		return jJobMonitor;
	}
	public void setjJobMonitor(JJobMonitor jJobMonitor) {
		this.jJobMonitor = jJobMonitor;
	}
	public VSphereJobContext getJobContext() {
		return jobContext;
	}
	public void setJobContext(VSphereJobContext jobContext) {
		this.jobContext = jobContext;
	}
	
}
