package com.ca.arcflash.webservice.scheduler;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.impl.triggers.CronTriggerImpl;

public class DSTWeeklyTrigger extends CronTriggerImpl {
	private static final Logger logger = Logger.getLogger(DSTWeeklyTrigger.class);
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;
	private int day;
	
	public DSTWeeklyTrigger(int startHour, int startMinute, int endHour, int endMinute, int day) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.day = day;
		try {
            setCronExpression("0 " + startMinute + " " + startHour + " ? * " + day);
        } catch (Exception ignore) {
            logger.error("Error in DST weeklyTrigger to set cron expression");
        }
	}
	
	@Override
	public Date getFireTimeAfter(Date afterTime) {
		Date nextFireTime = super.getFireTimeAfter(afterTime);
		Calendar nextTime = Calendar.getInstance();
		nextTime.setTime(nextFireTime);
		Calendar currentTime = Calendar.getInstance();
		// if next expected time is not existing because of DST, next time will be after one more week
		// under this situation, we need to change next time one week back if end time is after DST start time.
		if (isAfter7Days(currentTime, nextTime)) {
			if (this.endHour - this.startHour > 1 || (this.endHour - this.startHour == 1 && this.endMinute != 0)) {
				nextTime.add(Calendar.DAY_OF_YEAR, -7);
			    nextTime.set(Calendar.MINUTE, 0);
			}
		}
		else {
			if (nextTime.get(Calendar.HOUR_OF_DAY) != this.startHour) {
				nextTime.set(Calendar.HOUR_OF_DAY, this.startHour);
				nextTime.set(Calendar.MINUTE, this.startMinute);
			}
		}
		return nextTime.getTime();
	}
	
	private boolean isAfter7Days(Calendar currentTime, Calendar nextTime) {
		int daysInterval = nextTime.get(Calendar.DAY_OF_YEAR) - currentTime.get(Calendar.DAY_OF_YEAR);
		int nextTimeHour = nextTime.get(Calendar.HOUR_OF_DAY);
		int currentTimeHour = currentTime.get(Calendar.HOUR_OF_DAY);
		if (daysInterval > 7 || (daysInterval == 7 && nextTimeHour > currentTimeHour))
			return true;
		return false;
	}
}
