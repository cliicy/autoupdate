package com.ca.arcflash.webservice.scheduler;

import static java.util.Calendar.DATE;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.impl.triggers.SimpleTriggerImpl;

/**
 * The class is to remove the DST's impact for D2D's job scheduler, we 
 * adjust the job schedule automatically to let it run follow the local clock time
 * instead of real past milliseconds when DST starts and ends.
 */
public class DSTSimpleTrigger extends SimpleTriggerImpl {
	private static final long serialVersionUID = -6093635984582064751L;

	private Logger logger = Logger.getLogger(DSTSimpleTrigger.class);
	/**
	 * When DST starts, we will move the schedule one hour back, and if before
	 * DST some jobs fall into the lost one hour, we will compute the next real 
	 * scheduled time and set the start time as it.
	 * When DST ends, we just move the schedule one hour's milliseconds ahead to 
	 * let it run according to the clock time.
	 */
	@Override
	public Date getFireTimeAfter(Date afterTime) {
		logger.debug("After time: " + afterTime);
		Date time = super.getFireTimeAfter(afterTime);
		if(time == null)
			return null;
		Date prevTime = this.getPreviousFireTime() != null? 
				this.getPreviousFireTime() : this.getStartTime();
		Date dt = time;
		logger.debug("No DST, return " + dt);
		dt = fixForDST(time, prevTime, afterTime);
		setStartTime(dt);
		return dt;
        
	}

	private Calendar time2UTC(Date time) {
		logger.debug("Before time is " + time);
		Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		utc.set(time.getYear() + 1900, time.getMonth(), time.getDate(), 
				time.getHours(), time.getMinutes(), time.getSeconds());
		logger.debug("UTC time is " + utc.getTime());
		return utc;
	}
	
	private Date fixForDST(Date computedTime, Date comparedTime, Date afterTime) {
		Date dt = computedTime;
		if(computedTime.getTimezoneOffset() == comparedTime.getTimezoneOffset())
		{	
			return dt;
		}
		else if(computedTime.getTimezoneOffset() < comparedTime.getTimezoneOffset()) {
				dt = new Date(computedTime.getYear(), computedTime.getMonth(), computedTime
						.getDate(), computedTime.getHours() - 1, computedTime.getMinutes(),
						computedTime.getSeconds());
				//like in summer time, it's changed from 2:30 to 3:30, then there should 
				//not be this schedule, we need advanced one  
				while(dt.getTime() == computedTime.getTime()) {
					computedTime = super.getFireTimeAfter(computedTime);
					dt = new Date(computedTime.getYear(), computedTime.getMonth(), computedTime
							.getDate(), computedTime.getHours() - 1, computedTime.getMinutes(),
							computedTime.getSeconds());
				}
				logger.debug("DST start, return " + dt);
		}else if(computedTime.getTimezoneOffset() > comparedTime.getTimezoneOffset()){
			if(afterTime == null)
				dt = dt = new Date(computedTime.getTime() + 3600 * 1000);
			else {
				//in fact, we can use the following code directly to replace the getFireTimeAfter.
				long startMillis = time2UTC(getStartTime()).getTimeInMillis();
		        long afterMillis = time2UTC(afterTime).getTimeInMillis();
		        
		        long numberOfTimesExecuted = ((afterMillis - startMillis) / this.getRepeatInterval()) + 1;        

		        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		        cal.setTimeInMillis(startMillis + (numberOfTimesExecuted * getRepeatInterval()));
		        logger.debug("Computed UTC time is " + cal.getTime());
		        Date time = new Date(cal.get(YEAR) - 1900, cal.get(MONTH), cal.get(DATE),
		        		cal.get(HOUR_OF_DAY), cal.get(MINUTE), cal.get(SECOND));   
		        logger.debug("computed local time is " + time);
		        dt = time;
				logger.debug("DST ends, return " + dt);
			}
		}
		
		return dt;
	}
	
	/**
	 * Get last fire time before endTime while start from the startTime
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Date getFireTimeBefore(Date startTime, Date endTime) {
		if(startTime.getTime() > endTime.getTime())
			return null;
		
		int numFires = computeNumTimesFiredBetween(startTime, endTime);

        Date dt = new Date(startTime.getTime() + (numFires * getRepeatInterval()));
		logger.debug("Start time " + startTime + ", ednTime is " + endTime
				+ ", before DST fix is " + dt);
        dt =  fixForDST(dt, startTime, null);
        logger.debug("Start time " + startTime + ", ednTime is " + endTime
				+ ", after DST fix is " + dt);
        return dt;
	}
}
