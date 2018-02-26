package com.ca.arcflash.webservice.scheduler;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.impl.triggers.CronTriggerImpl;

import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.util.ServiceUtils;

/**
 * Make a trigger that will fire every day at the given time.
 * If the start time is in DST start time range, we will recompute the start time. 
 * @author zhawe03
 *
 */
public class DSTDailyTrigger extends CronTriggerImpl {
	private static final Logger logger = Logger.getLogger(DSTDailyTrigger.class);
	
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;	
	
	public DSTDailyTrigger(int hour, int minute, int endHour, int endMinute) {
		startHour = hour;
		startMinute = minute;
		this.endHour = endHour;
		this.endMinute = endMinute;
		
		try {
            setCronExpression("0 " + minute + " " + hour + " ? * *");
        } catch (Exception ignore) {
            logger.error("Error in DST dailyTrigger to set cron expression");
        }

        setStartTime(new Date());
	}

	@Override
	public Date getFireTimeAfter(Date afterTime) {
		Date nextFireTime = super.getFireTimeAfter(afterTime);
		Calendar nextTime = Calendar.getInstance();
		nextTime.setTime(nextFireTime);
		Calendar cafterTime = Calendar.getInstance();
		cafterTime.setTime(afterTime);
		
		if(nextTime.get(DAY_OF_MONTH) - cafterTime.get(DAY_OF_MONTH) > 1){
			//it must because the start time is in DST start time range, 
			//CronTrigger move the next time to next day automatically
			//we need to move back one day
			nextTime.add(DAY_OF_MONTH, -1);
		}else {
			if(CommonService.getInstance().isTimeInDSTBeginInterval(cafterTime.get(YEAR), 
					cafterTime.get(MONTH), cafterTime.get(DAY_OF_MONTH), startHour, startMinute)
					&& ServiceUtils.isTimeBefore(startHour, startMinute, endHour, endMinute)
					&& ServiceUtils.isTimeBefore(cafterTime.get(HOUR_OF_DAY), 
							cafterTime.get(MINUTE), endHour, endMinute)){
				//after time is DST start day, and start time is before end time, after time is before endtime, 
				//like time range in 2:20-3:30am, after time is 3:10am, use this date
				//if in the above time range, after tiem is 3:10pm, then don't use this date.
				nextTime.set(Calendar.DAY_OF_MONTH, cafterTime.get(DAY_OF_MONTH));
			}
		}
		
		logger.info("super: the fire time after " + afterTime + " is " + nextFireTime);
		if(CommonService.getInstance().isTimeInDSTBeginInterval(nextTime.get(YEAR), 
				nextTime.get(MONTH), nextTime.get(DAY_OF_MONTH), startHour, startMinute)){
			logger.debug("DST start " + nextFireTime);
			//DST start
			//check whether end time is also in DST start time range, 
			if(ServiceUtils.isTimeBefore(startHour, startMinute, endHour, endMinute)){
				if(CommonService.getInstance().isTimeInDSTBeginInterval(nextTime.get(YEAR), 
						nextTime.get(MONTH), nextTime.get(DAY_OF_MONTH), endHour, endMinute)
						|| (endHour - startHour == 1) && endMinute == 0){
					//if yes, then the next fire time is next day's time.
					nextTime.add(DAY_OF_MONTH, 1);
					nextTime.set(HOUR_OF_DAY, startHour);
					nextTime.set(MINUTE, startMinute);
					logger.info("End time is also DST start time, set start as next day.");
				}else{
					//if no, find a middle time as the next fire time.
					nextTime.set(MINUTE, 0);
					logger.info("End time is NOT in DST start time, set a middle time.");
				}
			}
		}
		logger.info("Fixed: the fire time is " + nextTime.getTime());
		return nextTime.getTime();
	}
}
