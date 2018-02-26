package com.ca.arcserve.edge.app.base.webservice.monitor;

import java.util.List;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;

/**
 * Job monitor tracker which used for get current job detail information
 * 
 * @author zhati04
 *
 */
public interface JobMonitorReader {
	/**
	 * this method is used for connect asbu to getMonitor
	 * @param jobDetail
	 * @return
	 * @throws EdgeServiceFault
	 */
	@Deprecated
	List<JobMonitor>  getJobMonitor(JobDetail jobDetail) throws EdgeServiceFault;
	/**
	 * this method is used for connect D2DAgent/rps/linuxD2D getMonitor
	 * @param jobDetail
	 * @return
	 * @throws EdgeServiceFault
	 */
	List<FlashJobMonitor> getJobMonitorOnServer(JobDetail jobDetail)throws EdgeServiceFault;
	
	boolean cancelJob(JobDetail jobDetail) throws EdgeServiceFault;
}
