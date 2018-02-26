package com.ca.arcserve.edge.app.base.webservice.jobhistory;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.handle.JobMonitorHandlerManager;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;

public class QueryJobMonitorsTaskRunner implements Runnable,Callable<JobDetailMonitorResult>
{
	private JobDetail jobDetail;
	private static Logger logger = Logger.getLogger( QueryJobMonitorsTaskRunner.class );
	
	public QueryJobMonitorsTaskRunner(JobDetail jobDetail)
	{
		this.jobDetail = jobDetail;
	}
	
	@Override
	public JobDetailMonitorResult call() throws Exception {
		JobDetailMonitorResult reslult = new JobDetailMonitorResult(this.jobDetail);	
		try{			 
			List<FlashJobMonitor> list = JobMonitorHandlerManager.getJobMonitorOnServer(jobDetail,true);
			logger.debug("QueryJobMonitorsTaskRunner getJobMonitorOnServer size=" + ((list==null)?0:list.size()));
			reslult.setMonitorList(list);
		} catch(Throwable e) {
			logger.error("QueryJobMonitorsTaskRunner catch Error " + jobDetail.toString(), e);
		}
		return reslult;
	}

	@Override
	public void run() {
		
	}
}
