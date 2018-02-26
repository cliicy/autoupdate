package com.ca.arcserve.edge.app.base.webservice.monitor.handle;

import java.util.List;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitorReader;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;


public abstract class JobMonitorHandler {
	
	abstract public void doPreHandles(JobMonitorReader reader,JobDetail jobDetail);
	abstract public void doPostHandles(JobMonitorReader reader,JobDetail jobDetail,List<FlashJobMonitor> monitors);
		
}
