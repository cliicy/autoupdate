package com.ca.arcserve.edge.app.base.webservice.monitor.handle;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitorReader;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitorReaderFactory;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail.SourceType;


public class JobMonitorHandlerManager {
	
	private static List<JobMonitorHandler> handlers = new ArrayList<JobMonitorHandler>();
	private static final Logger logger = Logger.getLogger(JobMonitorHandlerManager.class);
	static{
		handlers.add(new BackupJobMonitorHandler());
	}
		
	public static List<FlashJobMonitor> getJobMonitorOnServer(JobDetail jobDetail,boolean needHandle) throws EdgeServiceFault{
		if(jobDetail==null || jobDetail.getSource()==null
				|| jobDetail.getSource()==SourceType.NO_TYPE){
			logger.error("JobMonitorManager getJobMonitorOnServer caught a ERROR: jobDetail.SourceType="+(jobDetail==null?"null":jobDetail.getSource()));
			return null;
		}
		JobMonitorReader reader = JobMonitorReaderFactory.getReader(jobDetail.getSource());
		// no need to Handle
		if(!needHandle){
			return reader.getJobMonitorOnServer(jobDetail);
		}	
		// need handle something
		for (JobMonitorHandler handle : handlers) {
			handle.doPreHandles(reader, jobDetail);
		}
		List<FlashJobMonitor> monitors = reader.getJobMonitorOnServer(jobDetail);		
		for (JobMonitorHandler handle : handlers) {
			handle.doPostHandles(reader, jobDetail, monitors);
		}
		return monitors;
	}

	public static boolean cancelJob(JobDetail jobDetail) throws EdgeServiceFault{
		if(jobDetail==null || jobDetail.getSource()==null
				|| jobDetail.getSource()==SourceType.NO_TYPE){
			logger.error("JobMonitorManager cancelJob caught a ERROR: jobDetail.SourceType="+(jobDetail==null?"null":jobDetail.getSource()));
			return false;
		}
		JobMonitorReader reader = JobMonitorReaderFactory.getReader(jobDetail.getSource());
		return reader.cancelJob(jobDetail);		
	}
		
}
