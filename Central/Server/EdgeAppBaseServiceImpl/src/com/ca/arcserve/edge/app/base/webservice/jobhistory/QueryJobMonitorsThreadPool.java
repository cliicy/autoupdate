package com.ca.arcserve.edge.app.base.webservice.jobhistory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail.SourceType;

public class QueryJobMonitorsThreadPool {

	private static List<JobDetail> jobDetailList;
	private static ThreadPoolExecutor jobsExcutor;
	private static Logger logger = Logger.getLogger( QueryJobMonitorsThreadPool.class );
	
	private QueryJobMonitorsThreadPool()
	{
		jobsExcutor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
					new NamingThreadFactory("QueryJobMonitorsTaskFactory"));
	}
	
	
	private static class LazyHolder {
		public static final QueryJobMonitorsThreadPool tracker = new QueryJobMonitorsThreadPool();
	}

	public static QueryJobMonitorsThreadPool getInstance(List<JobDetail> jobDetails) {
		jobDetailList = jobDetails;
		return LazyHolder.tracker;
	}
	
	public List<FlashJobMonitor> getJobMonitors(){
		//ExecutorService executor = (ExecutorService) Executors.newCachedThreadPool();
		if(jobDetailList==null||jobDetailList.size()<=0){
			logger.debug("QueryJobMonitorsThreadPool jobDetailList==null");
			return null;
		}
		ExecutorService executor = (ExecutorService) jobsExcutor;
		List<QueryJobMonitorsTaskRunner> taskList = new ArrayList<>();
		for (JobDetail deatil:jobDetailList) {
			QueryJobMonitorsTaskRunner task = new QueryJobMonitorsTaskRunner(deatil);
			taskList.add(task);
		}
		List<Future<JobDetailMonitorResult>> resultList = null;
		try {
			resultList = executor.invokeAll(taskList);
		} catch (InterruptedException e) {
			logger.error("QueryJobMonitorsThreadPool executor.invokeAll catch error "+e.getMessage());
			return null;
		}	

		List<FlashJobMonitor> monitors = new ArrayList<FlashJobMonitor>();
		for (int i = 0; i < resultList.size(); i++) {
			Future<JobDetailMonitorResult> future = resultList.get(i);
			try {
				JobDetailMonitorResult result = future.get();
				
				if(result.getMonitorList() == null || result.getMonitorList().size()<=0){
					logger.debug("QueryJobMonitorsThreadPool return size is 0");
				} else {
					logger.debug("QueryJobMonitorsThreadPool return size="+result.getMonitorList().size());
					
					if(result.getJobDetail().getSource()==SourceType.RPS){
						for (FlashJobMonitor monitor:result.getMonitorList()) {
							if (monitor.isRunningOnRPS()) {
								logger.debug("QueryJobMonitorsThreadPool Add runOnRPS JobMonitor:"+monitor.toString());
								monitors.add(monitor);	
							} else {
								logger.debug("QueryJobMonitorsThreadPool no Add !runOnRPS JobMonitor:"+monitor.toString());
							}
						}
					} else {
						monitors.addAll(result.getMonitorList());	
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				logger.error("QueryJobMonitorsThreadPool InterruptedException | ExecutionException catch error "+e.getMessage());
			}
		}
		return monitors;
	}
	
	public static void shutDownThreadPool() {
		if(jobsExcutor!=null)
			jobsExcutor.shutdownNow();
	}
}
