package com.ca.arcflash.webservice.service;

import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.ca.arcflash.webservice.scheduler.AERPJob;

public class AERPService {
	Logger logger = Logger.getLogger(AERPService.class);
	private static AERPService INSTANCE = new AERPService();

	public static AERPService getInstance() {
		return INSTANCE;
	}

	public void submitAERPJob(int uploadFileFrequency, String uploadDateTimeStamp) throws Exception {
		try
		{
			logger.info("Submitting AERPJob at " + new Date() +" " + this.getClass().getName());
			logger.info("AERPJob will be triggerd at " + uploadDateTimeStamp);
			// Creating Job and link to our Job class
			/*JobDetail jobDetail = JobBuilder.newJob( AERPJob.class )
					.withIdentity( "AERP Job","AERP" )
					.build();
	
			Trigger trigger = newTrigger().withIdentity("AERPJobTrigger", "AERP")
	                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
	                .withIntervalInHours(uploadFileFrequency * 24).repeatForever()).build();*/
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			Date uploadDateTime = format.parse(uploadDateTimeStamp);
			
			JobDetail jobDetail = JobBuilder.newJob( AERPJob.class )
					.withIdentity( "AERP Job","AERP" )
					.build();
			
			Trigger trigger = newTrigger().withIdentity("AERPJobTrigger", "AERP")
	                .startAt(uploadDateTime).withSchedule(CalendarIntervalScheduleBuilder
	                .calendarIntervalSchedule().withIntervalInDays(uploadFileFrequency)).build();
			
			Scheduler sched = new StdSchedulerFactory().getScheduler();
			for (String groupName : sched.getJobGroupNames()) {
				for (JobKey jobKey : sched.getJobKeys(GroupMatcher
						.jobGroupEquals(groupName))) {
					if (jobKey.getName().equals(jobDetail.getKey().getName())) {
						//if same as what we wanted to trigger
						TriggerKey triggerKey = TriggerKey.triggerKey(
								"AERPJobTrigger", "AERP");
						sched.unscheduleJob(triggerKey);
						sched.deleteJob(new JobKey("AERP Job", "AERP"));
						logger.info("There's another instance of AERP Job running, so stopping the old AERP job and triggering new"
								+ " " + this.getClass().getName());
						// return;
					}

				}
			}			
	        sched.scheduleJob(jobDetail, trigger);
	        sched.start();
			logger.debug("submitAERPJob - end at " + new Date() + " "+ this.getClass().getName());
		}
		catch (Exception e)
		{
			logger.error("submitAERPJob()", e);
			throw e;
		}
	}
	
	public boolean isAERPJobTriggered()
	{
		logger.info("Check AERPJob is triggerd for registration");
		boolean isAERPJobTriggered = false;
		try
		{
			JobDetail jobDetail = JobBuilder.newJob( AERPJob.class )
					.withIdentity( "AERP Job","AERP" )
					.build();
			
			Scheduler sched = new StdSchedulerFactory().getScheduler();
			for (String groupName : sched.getJobGroupNames()) {
				for (JobKey jobKey : sched.getJobKeys(GroupMatcher
						.jobGroupEquals(groupName))) {
					if (jobKey.getName().equals(jobDetail.getKey().getName())) {
						isAERPJobTriggered =  true;
						logger.info("AERPJob is already triggered for registration " +isAERPJobTriggered);
					}

				}
			}
		}
		catch(Exception e)
		{
			logger.error("isAERPJobTriggered() Exception", e);
		}
		return isAERPJobTriggered;
	}
	
	public boolean terminateAERPJob()
	{
		logger.info("Terminate AERPJob triggerd for registration");
		boolean isAERPJobTerminated = false;
		try
		{
			JobDetail jobDetail = JobBuilder.newJob( AERPJob.class )
					.withIdentity( "AERP Job","AERP" )
					.build();
			
			Scheduler sched = new StdSchedulerFactory().getScheduler();
			for (String groupName : sched.getJobGroupNames()) {
				for (JobKey jobKey : sched.getJobKeys(GroupMatcher
						.jobGroupEquals(groupName))) {
					if (jobKey.getName().equals(jobDetail.getKey().getName())) {
						TriggerKey triggerKey = TriggerKey.triggerKey(
								"AERPJobTrigger", "AERP");
						sched.unscheduleJob(triggerKey);
						sched.deleteJob(new JobKey("AERP Job", "AERP"));
						isAERPJobTerminated = true;
						logger.info("Terminated AERPJob is  triggered for registration " +isAERPJobTerminated);
					}

				}
			}
		}
		catch(Exception e)
		{
			logger.error("isAERPJobTriggered() Exception", e);
		}
		return isAERPJobTerminated;
	}
}
