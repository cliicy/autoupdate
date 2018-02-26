package com.ca.arcserve.edge.app.base.webservice.test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IJobHistroryService;
import com.ca.arcserve.edge.app.base.webservice.jobhistory.JobHistoryServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.ASBUJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail.SourceType;

/**
 * Job monitor test case
 * 
 * @author zhati04
 *
 */
public class JobMonitorServiceTestCase extends AbstractTestCase{
	private static final Logger logger = Logger.getLogger(JobMonitorServiceTestCase.class);
	private static final IJobHistroryService jobMonitorService = new JobHistoryServiceImpl();
	
	@Test
	public void testFindRemoteMonitor(){
		try {
			JobDetail jobDetail = new JobDetail();
			jobDetail.setSource(SourceType.ASBU);
			jobDetail.setServerId(73);
			jobDetail.setNodeUUIDs(Arrays.asList("088e9ff3-7979-4b13-a25b-f02fea55f0a4","3615dc6c-d9af-4959-8566-f3fd7ff4950c"));
			List<JobMonitor> jobMonitors = jobMonitorService.findRemoteJobMonitor(jobDetail);
			if(CollectionUtils.isNotEmpty(jobMonitors)){
				for(JobMonitor monitor : jobMonitors){
					logger.debug(monitor.getPlanUUID());
					logger.debug(monitor.getPlanGlobalUUID());
					logger.debug(monitor.getNodeUUID());
				}
			}
		} catch (EdgeServiceFault e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFindJobMonitors(){
		try {
			JobDetail jobDetail = new JobDetail();
			jobDetail.setJobType(JobType.JOBTYPE_ARCHIVE_TO_TAPE);
			jobDetail.setServerId(2);
			jobDetail.setSource(SourceType.ASBU);
			List<? extends JobMonitor> jobMonitors = jobMonitorService.findJobMonitors(jobDetail);
			if(jobMonitors != null && jobMonitors.size() > 0){
				logger.debug(jobMonitors.size());
			}
		} catch (EdgeServiceFault e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreateJobMonitor(){
		try {
			JobMonitor jobMonitor = new ASBUJobMonitor();
			jobMonitor.setJobId(52+"");
			jobMonitor.setJobMethod(0L);
			jobMonitor.setJobStatus(0L);
			jobMonitor.setJobType(71);
			jobMonitor.setPlanUUID("fe1a08b8-f58b-4c14-abff-04f07bd01f43");
			jobMonitor.setProductType(4);
			jobMonitor.setServerId(73);
			jobMonitor.setStartTime(new Date());
			jobMonitorService.createJobMonitor(jobMonitor);
		} catch (EdgeServiceFault e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCancelJob(){
		try {
			JobDetail jobDetail = new JobDetail();
			jobDetail.setSource(SourceType.ASBU);
			jobDetail.setScheduleType(176);
			jobDetail.setServerId(1);
			jobDetail.setNodeUUID("e26e388d-26d7-41ca-82d5-b41ce03be4a6");
			boolean result = jobMonitorService.cancelJobForEdge(jobDetail);
			logger.debug(result);
		} catch (EdgeServiceFault e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDeleteJobMonitor(){
		try {
			JobMonitor jobMonitor = new ASBUJobMonitor();
			jobMonitorService.deleteJobMonitor(jobMonitor);
		} catch (EdgeServiceFault e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDate(){
		long time = 1278902362*1000;
		Date date = new Date(time);
		System.out.print(date);
	}
}
