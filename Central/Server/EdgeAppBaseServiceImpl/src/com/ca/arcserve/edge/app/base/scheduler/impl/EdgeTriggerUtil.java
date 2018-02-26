package com.ca.arcserve.edge.app.base.scheduler.impl;

import java.text.ParseException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.triggers.AbstractTrigger;

import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.EdgeTrigger;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;


public class EdgeTriggerUtil {
	private static Logger log = Logger.getLogger(EdgeTriggerUtil.class);
	public static Trigger getEdgeTrigger(String triggerName,
			String triggerGroup, ScheduleData sinfo,Object arg,IScheduleCallBack callback)
			throws EdgeSchedulerException {
		AbstractTrigger result = null;

		long sTime = sinfo.getScheduleTime().getTime();
		Calendar sCal = Calendar.getInstance();
		sCal.setTimeInMillis(sTime);

		long d = sinfo.getStartFromDate().getTime();
		if(d<sTime){
			log.info("EdgeTriggerUtil BEGIN... reset dCal time from = "+d);
			d=sTime;
		}
		Calendar dCal = Calendar.getInstance();
		dCal.setTimeInMillis(d);
		
		Calendar dCalActionTime = Calendar.getInstance();
		log.info("EdgeTriggerUtil BEGIN... sTime = " + sTime + " dCal time =" + d);
		if (sCal.get(Calendar.DST_OFFSET) >0) //scheduled DST ON
		{
			long dTime = dCalActionTime.getTimeInMillis();
			log.info("EdgeTriggerUtil sCal Schedule DST ON. dTime =" + dTime);
			if(dCalActionTime.get(Calendar.DST_OFFSET) > 0)
			{
				log.info("EdgeTriggerUtil dCalActionTime DST ON, no change to time");
				//current time DST ON
				dCalActionTime.setTimeInMillis(sinfo.getScheduleTime().getTime());
			}
			else
			{
				log.info("EdgeTriggerUtil dCalActionTime DST OFF, add 1 hr to time");
				//current time DST OFF
				dCalActionTime.setTimeInMillis(sinfo.getScheduleTime().getTime() + 60*60*1000);
				log.info("EdgeTriggerUtil dCalActionTime = " + dCalActionTime.getTimeInMillis());
			}
		}
		else  //scheduled DST OFF
		{
			log.info("EdgeTriggerUtil sCal Schedule DST OFF");
			if(dCalActionTime.get(Calendar.DST_OFFSET) >0)
			{
				log.info("EdgeTriggerUtil dCalActionTime DST ON,  remove 1 hr to time");
				//current time DST ON
				dCalActionTime.setTimeInMillis(sinfo.getScheduleTime().getTime() - 60*60*1000);
			}
			else
			{
				log.info("EdgeTriggerUtil dCalActionTime DST OFF, no change to time");
				dCalActionTime.setTimeInMillis(sinfo.getScheduleTime().getTime());
			}
			
			
		}
		//dCalActionTime.setTimeInMillis(sinfo.getScheduleTime().getTime());

		int actionMin = dCalActionTime.get(Calendar.MINUTE);
		int actionSec = dCalActionTime.get(Calendar.SECOND);
		if (actionSec > 59)
			actionSec = 59;
		int actionHour = dCalActionTime.get(Calendar.HOUR_OF_DAY);
		int actionHourforCron = sCal.get(Calendar.HOUR_OF_DAY); //no need to calculate DST.

		dCal.set(Calendar.MINUTE, actionMin);
		dCal.set(Calendar.SECOND, actionSec);
		dCal.set(Calendar.HOUR_OF_DAY, actionHour);
		String cronExpression = "";
		switch (sinfo.getRepeatMethodData().getRepeatMethodType()) {
		case everyNumberOfHours: // every selected hours
		{

			long parseInt = sinfo.getRepeatMethodData().getEveryHours();
			if (parseInt <= 0)
				throw new EdgeSchedulerException("Invalid hour(s) value",
						EdgeSchedulerException.ERR_BAD_SCHEDULE_PARAMETER);
			EdgeFixedIntervalTrigger re = new EdgeFixedIntervalTrigger(
					triggerName, triggerGroup, dCal.getTime(), null,
					parseInt * 60 * 60 * 1000);
			result = re;
			break;
		}
		case everyNumberOfMins: // every selected mins
		{
			long parseInt = sinfo.getRepeatMethodData().getEveryMins();
			if (parseInt <= 0)
				throw new EdgeSchedulerException("Invalid minute(s) value",
						EdgeSchedulerException.ERR_BAD_SCHEDULE_PARAMETER);
			EdgeFixedIntervalTrigger re = new EdgeFixedIntervalTrigger(
					triggerName, triggerGroup, dCal.getTime(), null,
					parseInt * 60 * 1000);
			result = re;

			break;
		}
		case everyNumberOfSecs: // every selected seconds
		{
			long parseInt = sinfo.getRepeatMethodData().getEverySeconds();
			if (parseInt <= 0)
				throw new EdgeSchedulerException("Invalid second(s) value",
						EdgeSchedulerException.ERR_BAD_SCHEDULE_PARAMETER);
			EdgeFixedIntervalTrigger re = new EdgeFixedIntervalTrigger(
					triggerName, triggerGroup, dCal.getTime(), null,
					parseInt * 1000);
			result = re;
			break;
		}
		case everyNumberOfDays: // every selected day
		{

			long parseInt = sinfo.getRepeatMethodData().getEveryDays();
			if (parseInt <= 0)
				throw new EdgeSchedulerException("Invalid day(s) value",
						EdgeSchedulerException.ERR_BAD_SCHEDULE_PARAMETER);
			if(parseInt != 1){
				EdgeFixedIntervalTrigger re = new EdgeFixedIntervalTrigger(
						triggerName, triggerGroup, dCal.getTime(), null, parseInt
								* 24 * 60 * 60 * 1000);
				result = re;
			} else {
				cronExpression = "" + actionSec + " " + actionMin + " "
						+ actionHourforCron + " ? * * ";
				dCal.set(Calendar.HOUR_OF_DAY, actionHourforCron);
				try {
					EdgeWeekTrigger re = new EdgeWeekTrigger(triggerName,
							triggerGroup, cronExpression);
					result = re;
				} catch (ParseException e) {
					throw new EdgeSchedulerException(e.getMessage(), e.getCause());
				}
			}

			break;
		}
		case everySelectedDaysOfWeek: // every selected weekday;
		{
			/**
			 * Seconds 0-59 , - * / Minutes 0-59 , - * / Hours 0-23 , - * /
			 * Day-of-month 1-31 , - * ? / L W Month 1-12 or JAN-DEC , - * /
			 * Day-of-Week 1-7 or SUN-SAT , - * ? / L # Year (Optional) empty,
			 * 1970-2099 , - * /
			 */
			// SUN-SAT, 1-7
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			if (sinfo.getRepeatMethodData().isRepeatSunday()) {
				if (!first)
					sb.append(",");
				sb.append("1");
				first = false;
			}
			if (sinfo.getRepeatMethodData().isRepeatMonday()) {
				if (!first)
					sb.append(",");
				sb.append("2");
				first = false;
			}
			if (sinfo.getRepeatMethodData().isRepeatTuesday()) {
				if (!first)
					sb.append(",");
				sb.append("3");
				first = false;
			}
			if (sinfo.getRepeatMethodData().isRepeatWednesday()) {
				if (!first)
					sb.append(",");
				sb.append("4");
				first = false;
			}
			if (sinfo.getRepeatMethodData().isRepeatThursday()) {
				if (!first)
					sb.append(",");
				sb.append("5");
				first = false;
			}
			if (sinfo.getRepeatMethodData().isRepeatFriday()) {
				if (!first)
					sb.append(",");
				sb.append("6");
				first = false;
			}
			if (sinfo.getRepeatMethodData().isRepeatSaturday()) {
				if (!first)
					sb.append(",");
				sb.append("7");
				first = false;
			}
			
			cronExpression = "" + actionSec + " " + actionMin + " "
					+ actionHourforCron + " ? * " + sb.toString();
			dCal.set(Calendar.HOUR_OF_DAY, actionHourforCron);

			try {
				EdgeWeekTrigger re = new EdgeWeekTrigger(triggerName, triggerGroup, cronExpression);
				result = re;
			} catch (ParseException e) {
				throw new EdgeSchedulerException(e.getMessage(), e.getCause());
			}
			break;
		}

		case everySelectedDaysOfMonth: // day of month
		{

			int parseInt = sinfo.getRepeatMethodData().getDayNumber();
			boolean fromBegin = sinfo.getRepeatMethodData().isFromBegin();
			if (parseInt <= 0)
				throw new EdgeSchedulerException("Invalid day(s) value",
						EdgeSchedulerException.ERR_BAD_SCHEDULE_PARAMETER);

//			MonthSelectedDayTrigger re = new MonthSelectedDayTrigger(triggerName,
//					triggerGroup,fromBegin,parseInt);
//			result = re;
			cronExpression = "" + actionSec + " " + actionMin + " " //for example: 1st 8:00 every month 0 0 8 1 * ?
					+ actionHourforCron + " " + parseInt + " * ? ";
			dCal.set(Calendar.HOUR_OF_DAY, actionHourforCron);
			try {
				EdgeWeekTrigger re = new EdgeWeekTrigger(triggerName, triggerGroup, cronExpression);
				result = re;
			} catch (ParseException e) {
				throw new EdgeSchedulerException(e.getMessage(), e.getCause());
			}
			break;
		}

		default:
			throw new EdgeSchedulerException(
					EdgeSchedulerException.ERR_BAD_SCHEDULE_TYPE);
		}
		result.setDescription(sinfo.getScheduleDescription()==null?"":sinfo.getScheduleDescription());
		result.setStartTime(dCal.getTime());
		switch (sinfo.getRepeatUntilType()) {
		case forever:
			result.setEndTime(null);
			((EdgeTrigger) result).setRepeat_count(SimpleTrigger.REPEAT_INDEFINITELY);
			break;
		case endDate:
			((EdgeTrigger) result).setRepeat_count(SimpleTrigger.REPEAT_INDEFINITELY);
			result.setEndTime(sinfo.getEndDate());
			break;
		case numberOfTimes:
			result.setEndTime(null);
			if (result instanceof EdgeTrigger)
				((EdgeTrigger) result)
						.setRepeat_count(sinfo.getNumberOfTimes());
			break;
		}
		if (result instanceof EdgeTrigger) {
			((EdgeTrigger) result).setCallback(callback);
			((EdgeTrigger) result).setScheduleData(sinfo);
			((EdgeTrigger) result).setArgs(arg);
		}
		return result;
	}
}
