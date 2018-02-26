package com.ca.arcflash.webservice.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.ThrottleItem;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceException;

public abstract class AbstractBackupThrottleJob implements Job {
	private static final Logger logger = Logger.getLogger(AbstractBackupThrottleJob.class);
	protected String vmInstanceUUID = null;
	protected int jobId;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		vmInstanceUUID = dataMap.getString("vmInstanceUUID");
	}
	

	public String getVMInstanceUUID() {
		return vmInstanceUUID;
	}
	
	public String setVMInstanceUUID(String uuid) {
		return vmInstanceUUID = uuid;
	}
	
	protected void updateThrottling4Jobs(long throttling) {
		try {
			CommonService.getInstance().updateThrottling4AllJobs(vmInstanceUUID, throttling);
		} catch (ServiceException e) {
			logger.error("Failed to update throttling for jobs", e);
		}
	}
	
	protected long getCurrentThrottling(BackupConfiguration backupConf) {
		if (backupConf == null || backupConf.getAdvanceSchedule() == null)
			return 0;
		
		List<DailyScheduleDetailItem> dailySchedules = backupConf.getAdvanceSchedule().getDailyScheduleDetailItems();
		if (!hasSchedule(dailySchedules)) {
			return 0;
		}
		else {
			Calendar currentTime = Calendar.getInstance();
			int dayofWeek = currentTime.get(Calendar.DAY_OF_WEEK);
			DayTime currentDayTime = new DayTime(currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE));

			for (DailyScheduleDetailItem dailySchedule : dailySchedules) {
				if (dailySchedule.getDayofWeek() != dayofWeek)
					continue;
				ArrayList<ThrottleItem> throttleItems = dailySchedule.getThrottleItems();
				if (throttleItems != null) {
					for (ThrottleItem throttleItem : throttleItems) {
						if (throttleItem != null
								&& throttleItem.getStartTime().getHour() == 0
								&& throttleItem.getStartTime().getMinute() == 0
								&& throttleItem.getEndTime().getHour() == 0
								&& throttleItem.getEndTime().getMinute() == 0) {
							//throttle 00:00-24:00
							return throttleItem.getThrottleValue();
						}
						if ((currentDayTime.after(throttleItem.getStartTime()) || currentDayTime.equals(throttleItem.getStartTime()))
								&& currentDayTime.before(throttleItem.getEndTime()))
							return throttleItem.getThrottleValue();
					}
				}
			}
			return 0;
		}
	}
	
	static protected boolean hasSchedule(List<DailyScheduleDetailItem> dailySchedules) {
		boolean hasSchedule = false;
		if(dailySchedules == null || dailySchedules.size() == 0) {
			logger.debug("No backup throttle settings.");	
		}
		else {
			for (DailyScheduleDetailItem dailySchedule : dailySchedules) {
				ArrayList<ThrottleItem> throttleItems = dailySchedule.getThrottleItems();
				if (throttleItems != null && throttleItems.size() != 0)
					hasSchedule = true;
			}
		}
		
		return hasSchedule;
	}
}
