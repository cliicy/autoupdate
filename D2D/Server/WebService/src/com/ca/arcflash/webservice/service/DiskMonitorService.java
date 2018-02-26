package com.ca.arcflash.webservice.service;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.webservice.scheduler.DiskMonitorJob;
import com.ca.arcflash.webservice.scheduler.VSphereDiskMonitorJob;
import com.ca.arcflash.webservice.util.ScheduleUtils;

public class DiskMonitorService{
	
	private static final Logger logger = Logger.getLogger(DiskMonitorService.class);
	private static DiskMonitorService instance = new DiskMonitorService();
	private Scheduler scheduler;
	
	private DiskMonitorService(){
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}
	
	public static DiskMonitorService getInstance(){
		return instance;
	}
	
	public void startDiskMonitorJob(){
		
		SimpleTriggerImpl cycleTrigger = ScheduleUtils.makeMinutelyTrigger(60);
		cycleTrigger.setName("cycleTrigger");
		JobDetail cycleJobDetail = new JobDetailImpl("Detect Disk Space","group2", DiskMonitorJob.class);
		try {
			
			scheduler.scheduleJob(cycleJobDetail, cycleTrigger);
			
		} catch (SchedulerException e) {
			logger.error("startDiskMonitorJob failed");
			
		}
	}
	
	public void startVSphereDiskMonitorJob(){
		
		SimpleTriggerImpl cycleTrigger = ScheduleUtils.makeMinutelyTrigger(60*24);
		cycleTrigger.setName("cycleTrigger-vSphere");
		JobDetail cycleJobDetail = new JobDetailImpl("Detect Disk Space-vSphere","group2-vSphere", VSphereDiskMonitorJob.class);
		try {
			
			scheduler.scheduleJob(cycleJobDetail, cycleTrigger);
			
		} catch (SchedulerException e) {
			logger.error("startVSphereDiskMonitorJob failed");
			
		}
	}
}
