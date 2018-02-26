package com.ca.arcflash.webservice.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.ThrottleItem;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.internal.VSphereBackupConfigurationXMLDAO;

public class VSphereBackupThrottleJob extends AbstractBackupThrottleJob {
	private static final Logger logger = Logger.getLogger(VSphereBackupThrottleJob.class);
	private VSphereBackupConfigurationXMLDAO vspherebackupConfigurationXMLDAO = new VSphereBackupConfigurationXMLDAO();
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		super.execute(context);
		
		VirtualMachine virtualMachine = new VirtualMachine();
		virtualMachine.setVmInstanceUUID(vmInstanceUUID);
		VMBackupConfiguration configuration = null;
		try {
			configuration = vspherebackupConfigurationXMLDAO.get(ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath(), virtualMachine);
		} catch (Exception e) {
			logger.error("Failed to get vsphere backup configuration");
		}
		
        long throttling = getCurrentThrottling(configuration);
        
        BackupService.getInstance().setThrottling(throttling);
        
        setVMInstanceUUID(vmInstanceUUID);
        updateThrottling4Jobs(throttling);
	}

	
	static public long getCurrentThrottling(VMBackupConfiguration backupConf) {
		
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
}
