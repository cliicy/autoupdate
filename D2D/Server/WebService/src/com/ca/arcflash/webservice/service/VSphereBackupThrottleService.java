package com.ca.arcflash.webservice.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.ThrottleItem;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.scheduler.DSTWeeklyTrigger;
import com.ca.arcflash.webservice.scheduler.VSphereBackupThrottleJob;
import com.ca.arcflash.webservice.service.internal.VSphereBackupConfigurationXMLDAO;
import com.ca.arcflash.webservice.util.ScheduleUtils;


public class VSphereBackupThrottleService extends AbstractBackupThrottleService {

	private static final Logger logger = Logger.getLogger(VSphereBackupThrottleService.class);	
	private static VSphereBackupThrottleService INSTANCE = SingletonInstance.INSTANCE;
	private VSphereBackupConfigurationXMLDAO vspherebackupConfigurationXMLDAO = new VSphereBackupConfigurationXMLDAO();
	private VSphereBackupThrottleService() {
    	try {
    		scheduler = VSphereService.getInstance().getCatalogScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static VSphereBackupThrottleService getInstance(){
		return INSTANCE;
	}

	@Override
	public void unschedule(String vmInstanceUUID) {
		try {
			scheduler.pauseTrigger(new TriggerKey(getThrottleJobName(vmInstanceUUID), BACKUP_THROTTLE_JOB_GROUP_NAME));
			scheduler.unscheduleJob(new TriggerKey(getThrottleJobName(vmInstanceUUID), BACKUP_THROTTLE_JOB_GROUP_NAME));
			scheduler.deleteJob(new JobKey(getThrottleJobName(vmInstanceUUID), BACKUP_THROTTLE_JOB_GROUP_NAME));
		}catch(SchedulerException e){
			logger.debug("Failed to delete job");
		}
	}
	
	protected void scheduleBackupThrottleJob(BackupVM vm){
		logger.debug("Schedule backup throttle job");
		List<DailyScheduleDetailItem> dailyScheduleDetailItems = getThrottlingSchedule(vm.getInstanceUUID());
		if(!hasSchedule(dailyScheduleDetailItems)) {
			return;
		}
		unschedule(vm.getInstanceUUID());
    	schedule(vm.getInstanceUUID());
	}
	
	private void schedule(String vmInstanceUUID) {
		JobDetailImpl jd = new JobDetailImpl(getThrottleJobName(vmInstanceUUID), BACKUP_THROTTLE_JOB_GROUP_NAME, VSphereBackupThrottleJob.class);
		jd.getJobDataMap().put("vmInstanceUUID", vmInstanceUUID);
        jd.setDurability(true);
        try {
			scheduler.addJob(jd, false);
			SimpleTriggerImpl immediateTrigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			immediateTrigger.setName(jd.getName() + "immediateTrigger");
			immediateTrigger.setGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
			immediateTrigger.setJobName(jd.getName());
			immediateTrigger.setJobGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
			scheduler.scheduleJob(immediateTrigger);		
		} catch (SchedulerException e1) {
			logger.error("Failed to schedule backup throttle job for quartz error" + e1.getMessage());
		}
        
        List<DailyScheduleDetailItem> dailySchedules = this.getThrottlingSchedule(vmInstanceUUID);
		if (!hasSchedule(dailySchedules)) {
			return;
		}

		try {
			int triggerNumber = 1;
			for (DailyScheduleDetailItem dailySchedule : dailySchedules) {
				ArrayList<ThrottleItem> throttleItems = dailySchedule.getThrottleItems();
				if (throttleItems != null) {
					for (ThrottleItem throttleItem : throttleItems) {
						CronTriggerImpl startTrigger = new DSTWeeklyTrigger(throttleItem.getStartTime().getHour(), 
								throttleItem.getStartTime().getMinute(), throttleItem.getEndTime().getHour(),
								throttleItem.getEndTime().getMinute(), dailySchedule.getDayofWeek());
						startTrigger.setName(jd.getName() + "trigger" + triggerNumber);
						triggerNumber++;
						startTrigger.setGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
						startTrigger.setJobName(jd.getName());
						startTrigger.setJobGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
						scheduler.scheduleJob(startTrigger);
						
						if (!endIsStart(dailySchedules, dailySchedule.getDayofWeek(), throttleItem.getEndTime())) {
							DayTime nextTime = getNextDayTime(dailySchedules, dailySchedule.getDayofWeek(), throttleItem.getEndTime());
							CronTriggerImpl endTrigger = new DSTWeeklyTrigger(throttleItem.getEndTime().getHour(), 
									throttleItem.getEndTime().getMinute(), nextTime.getHour(),
									nextTime.getMinute(), dailySchedule.getDayofWeek());
							endTrigger.setName(jd.getName() + "trigger" + triggerNumber);
							triggerNumber++;
							endTrigger.setGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
							endTrigger.setJobName(jd.getName());
							endTrigger.setJobGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
							scheduler.scheduleJob(endTrigger);
						}
					}
				}
			}
		}catch(SchedulerException se) {
			logger.error("Failed to schedule backup throttle job for quartz error" + se.getMessage());
		}
	}
	
	public void startImmediateTrigger() {
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}

		for (File one : files) {
			BackupVM tempVM = new BackupVM();
			String filename = one.getName();
			String instanceUUID = new String();
			instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
			if(instanceUUID != null && instanceUUID.startsWith(VSphereService.VMCONFIG_PREFIX))
				continue;
			
			tempVM.setInstanceUUID(instanceUUID);
			
			this.scheduleBackupThrottleJob(tempVM);
		}
	}
	
	public void unScheduleAllVM() {
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}

		for (File one : files) {
			String filename = one.getName();
			String instanceUUID = new String();
			instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
			if(instanceUUID != null && instanceUUID.startsWith(VSphereService.VMCONFIG_PREFIX))
				continue;
			
			this.unschedule(instanceUUID);
		}
	}
	
	private static class SingletonInstance {
		static VSphereBackupThrottleService INSTANCE = new VSphereBackupThrottleService();
	}

	public List<DailyScheduleDetailItem> getThrottlingSchedule(String vmInstanceUUID) {
		List<DailyScheduleDetailItem> dailyScheduleDetailItems = null;
		VirtualMachine virtualMachine = new VirtualMachine();
		virtualMachine.setVmInstanceUUID(vmInstanceUUID);
		VMBackupConfiguration configuration = null;
		try {
			configuration = vspherebackupConfigurationXMLDAO.get(ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath(), virtualMachine);
		} catch (Exception e) {
			logger.error("Failed to get vsphere backup configuration");
		}
		
		if(configuration != null && configuration.getAdvanceSchedule() != null) {
			dailyScheduleDetailItems = configuration.getAdvanceSchedule().getDailyScheduleDetailItems();
		}
		
		return dailyScheduleDetailItems;
	}

	@Override
	protected String getThrottleJobName(String vmInstanceUUID) {
		return VSPHERE_BACKUP_THROTTLE_JOB_NAME +  "_" + vmInstanceUUID;
	}
}
