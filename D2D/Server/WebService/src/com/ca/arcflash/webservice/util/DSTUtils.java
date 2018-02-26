package com.ca.arcflash.webservice.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.ca.arcflash.webservice.data.DayTime;

public class DSTUtils {
	
	public static final long ONE_HOUR_MILLISECONDS = 60 * 60 * 1000;
	
	public static long getTimezoneOffset(Date date) {
		if(date == null)
			return 0;
		TimeZone zone = TimeZone.getDefault();
		return zone.getOffset(date.getTime());
	}
	
	public static boolean isInDSTBeginHour(DayTime time) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, time.getHour());
		cal.set(Calendar.MINUTE, time.getMinute());
		cal.set(Calendar.SECOND, 0);
		if(cal.get(Calendar.HOUR_OF_DAY) != time.getHour())
			return true;
		else 
			return false;
	}
	
	/**
	 * Check whether the time is in the DST begin one hour interval, 
	 * e.g: if DST starts at 2:00am, the API will check whether the time 
	 * is in 2:00-3:00am of the DST start date, if yes, return true, or return false.
	 * @param time
	 * @return
	 */
	public static boolean isInDSTBeginHour(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.HOUR_OF_DAY, 1);
		if(cal.getTimeInMillis() == time.getTime())
			return true;
		else
			return false;
	}
	
	/**
	 * Check whether the time is in the DST ends one hour interval,
	 * like DST ends at 2:00 am, then there will be two 1:00-2:00am interval,
	 * this API checks whether  the time is in 1:00-2:00am, if yes, return true, else return false.
	 * @param time
	 * @return
	 */
	public static boolean isInDSTEndHour(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time.getTime() + ONE_HOUR_MILLISECONDS);
		if(cal.get(Calendar.HOUR_OF_DAY) == time.getHours())
			return true;
		else
			return false;
	}
	
	public static Calendar adjustForDSTEnd(Calendar cal) {
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(cal.getTimeInMillis() - ONE_HOUR_MILLISECONDS);
		if(cal2.get(Calendar.HOUR_OF_DAY) == cal.get(Calendar.HOUR_OF_DAY))
			return cal2;
		else
			return cal;	
		}
}
