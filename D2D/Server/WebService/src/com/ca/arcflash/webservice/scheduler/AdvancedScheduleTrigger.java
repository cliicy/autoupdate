package com.ca.arcflash.webservice.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.AbstractTrigger;

import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.backup.BackupScheduleIntervalUnit;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.util.DSTUtils;

public class AdvancedScheduleTrigger extends AbstractTrigger {
	
	private static final long serialVersionUID = -4384273444983277140L;
	
	private static final int YEAR_TO_GIVEUP_SCHEDULING_AT = 2999;
	
	public static final int MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = 1;
	
	public static final int MISFIRE_INSTRUCTION_DO_NOTHING = 2;
	
	private ScheduleDetailItem scheduleItem;
	private DayTime startDayTime;
	private DayTime endDayTime;
	private int dayOfWeek = 0;
	
	private Date startTime = null;
    private Date endTime = null;
    private Date nextFireTime = null;
    private Date previousFireTime = null;
    private Trigger[] triggers;
    
    private int repeatInterval = 15 * 60 * 1000;//milliseconds
    private final long TIME_IN_MILLIS_FOR_HOUR = 60 * 60 * 1000;
    
    private Logger logger = Logger.getLogger(AdvancedScheduleTrigger.class);
	
    public AdvancedScheduleTrigger(String triggerName) {
		this(triggerName, null);
	}
    
    public AdvancedScheduleTrigger(String triggerName, String triggerGroup) {
		this(triggerName, triggerGroup, new Date());
	}
    
    public AdvancedScheduleTrigger(String triggerName, String triggerGroup, Date startTime) {
		this(triggerName, triggerGroup, startTime, null, 0);
	}
    
    public AdvancedScheduleTrigger(String triggerName, String triggerGroup, Date startTime, ScheduleDetailItem scheduleItem, int dayOfWeek, Trigger... triggers) {
		super(triggerName, triggerGroup);
		this.setStartTime(startTime);
		this.setScheduleItem(scheduleItem);
		this.setDayOfWeek(dayOfWeek);
		
		if(triggers == null) 
			this.triggers = new Trigger[0];
		else
			this.triggers = triggers;
	}
    
    public AdvancedScheduleTrigger(String triggerName, String triggerGroup, String jobName, String jobGroup, 
    		Date startTime, ScheduleDetailItem scheduleItem) {
    	super(triggerName, triggerGroup, jobName, jobGroup);
    	this.setStartTime(startTime);
		this.setScheduleItem(scheduleItem);
	}
    
    public AdvancedScheduleTrigger(String triggerName, String triggerGroup, 
    		ScheduleDetailItem scheduleItem, int dayOfWeek) {
		this(triggerName, triggerGroup, new Date(), scheduleItem, dayOfWeek);
	}
	
	@Override
	public Date computeFirstFireTime(org.quartz.Calendar cal) {
		repeatInterval = scheduleItem.getInterval() * 60 * 1000;//milliseconds	
		
		if(scheduleItem.getIntervalUnit() == BackupScheduleIntervalUnit.Hour) {
			repeatInterval *= 60;
		}
		
		Calendar time = Calendar.getInstance();
		time.setTime(startTime);
		if(time.get(Calendar.DAY_OF_WEEK) == this.dayOfWeek){
			setAsStartTime(time);
			nextFireTime = time.getTime();
			nextFireTime = adjustFirstTimeForDST(time);
			this.updateAfterMisfire(cal);
//			if(nextFireTime.before(startTime))
//				nextFireTime = getFireTimeAfter(startTime);
		}else {
			nextFireTime = getNextFirstFireTime(time);
		}
		
		while (nextFireTime != null && cal != null
	            && !cal.isTimeIncluded(nextFireTime.getTime())) {
	        nextFireTime = getFireTimeAfter(nextFireTime);
	    }
		
		while (nextFireTime != null && nextFireTime.before(startTime)) {
	        nextFireTime = getFireTimeAfter(nextFireTime);
	    }

		 Date date = nextFireTime;
	     if (triggers != null && triggers.length > 0) {
	            List<Date> list = new ArrayList<>();
	            for (Trigger t : triggers) {
	                if (t != null) {
	                    list.add(t.getFireTimeAfter(t.getStartTime()));
	                }
	            }
	            if (!list.isEmpty()) {
	                Collections.sort(list);
	                for (Date hd : list) {
	                    if (Math.abs(hd.getTime()-date.getTime())<=5000) {
	                    	logger.debug("Conflicted detected at date:" + date +",conflict with this trigger:" + this.getFullName());	
	                    	date = getFireTimeAfter(date);
	                    	logger.debug("Conflicted detected, current trigger will be skipped and delay to:" + date +","+ this.getFullName());
	                    }
	                }
	            }
	            
	        }
	        logger.debug("The Fisrt time for this trigger is "+date);

	        nextFireTime=date;    
		
	    return nextFireTime;
	}

//	@Override
//	public int executionComplete(JobExecutionContext context,
//			JobExecutionException result) {
//		if (result != null && result.refireImmediately()) {
//			return INSTRUCTION_RE_EXECUTE_JOB;
//	    }
//
//	    if (result != null && result.unscheduleFiringTrigger()) {
//	        return INSTRUCTION_SET_TRIGGER_COMPLETE;
//	    }
//
//	    if (result != null && result.unscheduleAllTriggers()) {
//	        return INSTRUCTION_SET_ALL_JOB_TRIGGERS_COMPLETE;
//	    }
//
//	    if (!mayFireAgain()) {
//	        return INSTRUCTION_DELETE_TRIGGER;
//	    }
//
//	    return INSTRUCTION_NOOP;
//	}

	@Override
	public Date getEndTime() {
		return this.endTime;
	}

	@Override
	public Date getFinalFireTime() {
		throw new UnsupportedOperationException("D2D's schedule job will run until user " +
				"stop the schedule, so doesn't support this operation now");
	}
	
	@Override
	public Date getFireTimeAfter(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		if(cal.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
			//the dayofweek of time is different from the actual fire day, then the fire 
			//time should be the start time of the schedule item of the next dayOfWeek
			return getNextFirstFireTime(cal);
		}else{			
			DayTime dtime = new DayTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
			boolean isStartTimeInDSTBegin = DSTUtils.isInDSTBeginHour(startDayTime);
			Calendar starttime = Calendar.getInstance();
			starttime.setTime(time);
			setAsStartTime(starttime);	
			
			Date firstFireTime = starttime.getTime();
			if(isStartTimeInDSTBegin) {
				firstFireTime = this.adjustFirstTimeForDST(starttime);
			}			
			
			time.setSeconds(0);
			cal.set(Calendar.SECOND, 0);
			
			DayTime firstFire = new DayTime(firstFireTime.getHours(),firstFireTime.getMinutes());
			
			if(time.before(firstFireTime) && firstFire.before(endDayTime)) {
				return firstFireTime;
			}else if(dtime.before(endDayTime)){
				Calendar endTime = Calendar.getInstance();
				endTime.setTime(time);
				endTime.set(Calendar.HOUR_OF_DAY, endDayTime.getHour());
				
				if (endTime.get(Calendar.HOUR_OF_DAY) == endDayTime.getHour()) {
					endTime.set(Calendar.MINUTE, endDayTime.getMinute());
					endTime.set(Calendar.SECOND, 0);	
				} else {
					endTime.set(Calendar.HOUR_OF_DAY, endDayTime.getHour() - 1);
					endTime.set(Calendar.MINUTE, 59);
					endTime.set(Calendar.SECOND, 59);
				}
				endTime.set(Calendar.MILLISECOND, 0);
				
				endTime = DSTUtils.adjustForDSTEnd(endTime);
				boolean isInDSTSetBackTime = false;
				if (isInDSTSetBackTime(endTime)) {
					endTime.setTimeInMillis(endTime.getTimeInMillis() + TIME_IN_MILLIS_FOR_HOUR);
					isInDSTSetBackTime = true;
				}
				logger.debug("First fire time is " + firstFireTime);
				Date fireTime =  getFireTime(cal, firstFireTime.getTime());
		
				if (isInDSTSetBackTime) {
					if(fireTime.before(endTime.getTime()))
						return fireTime;
					else
						return getNextFirstFireTime(cal);
				}
					
				if(firstFireTime.getTimezoneOffset() != endTime.getTime().getTimezoneOffset()
						&& !isStartTimeInDSTBegin && fireTime.before(endTime.getTime())){
					starttime.setTime(firstFireTime);
					starttime.add(Calendar.DAY_OF_MONTH, 7);
					cal.add(Calendar.DAY_OF_MONTH, 7);
					Date nextWeekFireTime = getFireTime(cal, starttime.getTimeInMillis());
					if(nextWeekFireTime.getHours() == fireTime.getHours() 
							&& nextWeekFireTime.getMinutes() == fireTime.getMinutes()){
						//return fireTime;
					}
					/*else if(nextWeekFireTime.getHours() > fireTime.getHours()) {
						//DST ends
						fireTime = new Date(fireTime.getTime() + 3600 * 1000);
					}*/else{
						//DST begin this time
						fireTime.setHours(nextWeekFireTime.getHours());
						fireTime.setMinutes(nextWeekFireTime.getMinutes());
						while(nextWeekFireTime.getHours() != fireTime.getHours()
								|| nextWeekFireTime.getMinutes() != fireTime.getMinutes()){
							cal.setTime(nextWeekFireTime);
							nextWeekFireTime = getFireTime(cal, starttime.getTimeInMillis());
							fireTime.setHours(nextWeekFireTime.getHours());
							fireTime.setMinutes(nextWeekFireTime.getMinutes());
						}
					}
				}
				logger.debug("returned fire time is :" + fireTime);
				if(fireTime.before(endTime.getTime()))
					return fireTime;
				else
					return getNextFirstFireTime(cal);
			}else {
				//time should be the start time of the schedule item of the next dayOfWeek
				return getNextFirstFireTime(cal);
			}
		}
	}
	
	private Date getFireTime(Calendar cal, long firstFireTime) {
		logger.debug("cal.getTime " + cal.getTimeInMillis());
		logger.debug("First Time " + firstFireTime);
		long mill = cal.getTimeInMillis() - firstFireTime;
		long executedTimes = mill / repeatInterval + 1;
		long  retMill = firstFireTime + executedTimes * repeatInterval;
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(retMill);
		if (isInDSTSetBackTime(c))
			retMill += TIME_IN_MILLIS_FOR_HOUR;
		logger.debug("Return new date: " + new Date(retMill));
		return new Date(retMill);
	}
	
	private boolean isInDSTSetBackTime(Calendar c)
	{
		Calendar nextC = Calendar.getInstance();
		nextC.setTimeInMillis(c.getTimeInMillis() + TIME_IN_MILLIS_FOR_HOUR);
		if (c.get(Calendar.HOUR_OF_DAY) == nextC.get(Calendar.HOUR_OF_DAY))
			return true;
		else 
			return false;
	}
	
	private Date adjustFirstTimeForDST(Calendar time) {
		
		Date ret = time.getTime();
		
		if(time.get(Calendar.HOUR_OF_DAY) != startDayTime.getHour()) {
			//DST begins			
			Calendar time2 = Calendar.getInstance();
			time2.setTimeInMillis(time.getTimeInMillis());			
			Date nextWeekFireStartTime = this.getNextFirstFireTime(time2);
			Calendar nextWeekFireTime = Calendar.getInstance();
			nextWeekFireTime.setTime(nextWeekFireStartTime);
			
			Date date2 = nextWeekFireTime.getTime();			
			while(time.get(Calendar.HOUR_OF_DAY) != nextWeekFireTime.get(Calendar.HOUR_OF_DAY) || time.get(Calendar.MINUTE) != nextWeekFireTime.get(Calendar.MINUTE)) {
				date2 = this.getFireTime(nextWeekFireTime, nextWeekFireStartTime.getTime());
				nextWeekFireTime.setTime(date2);
				time.set(Calendar.HOUR_OF_DAY, date2.getHours());
				time.set(Calendar.MINUTE, date2.getMinutes());
			}
			
			if(nextWeekFireStartTime.getDate() != nextWeekFireTime.get(Calendar.DAY_OF_MONTH))
				ret = nextWeekFireStartTime;
			else
				ret= time.getTime();
		}	
		
		
		DayTime retdt = new DayTime(ret.getHours(), ret.getMinutes());
		
		
		if(retdt.before(endDayTime)){		
			return ret;
		}
		
		Calendar next = Calendar.getInstance();
		next.setTime(ret);
		
		return getNextFirstFireTime(next);
	}
	
	private void setAsStartTime(Calendar time) {

		Calendar time2 = Calendar.getInstance();
		time2.setTime(startTime);

		time.set(Calendar.HOUR_OF_DAY, startDayTime.getHour());
		time.set(Calendar.MINUTE, startDayTime.getMinute());
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);

	}
	
	private Date getNextFirstFireTime(Calendar time) {
		int specDayOfWeek = time.get(Calendar.DAY_OF_WEEK);
		int addDays = 0;
		
		if(specDayOfWeek < dayOfWeek) {
			addDays = dayOfWeek - specDayOfWeek;			
		}else {
			addDays = 7 - (specDayOfWeek - dayOfWeek);
		}
		
		time.add(Calendar.DAY_OF_MONTH, addDays);
		setAsStartTime(time);
		return this.adjustFirstTimeForDST(time);
	}

//	@Override
//	public Date getNextFireTime() {
//		return this.nextFireTime;
//	}
	
//	private Date getTriggeredNextTime(){			
//		try {
//			
//			Trigger tg = BackupService.getInstance().getBackupSchedule().getTrigger(new TriggerKey(this.getName(),this.getGroup()));
//			if(tg != null){
//				Date date = tg.getNextFireTime();
//				return date;
//			}
//		} catch (Exception e) {
//			logger.error("Failed to get scheduled trigger", e);
//		}
//		return null;
//	}
	
	@Override
	public Date getNextFireTime() {
		Date date = this.nextFireTime;
		
		 boolean isconflict = false;
	     if (triggers != null && triggers.length > 0) {
	           
	          List<Date> list = new ArrayList<Date>();
	            for (Trigger t : triggers) {
	                if (t != null) {
	                    list.add(t.getFireTimeAfter(new Date()));
	                }
	            }
	            if (!list.isEmpty()) {
	                Collections.sort(list);
	                for (Date hd : list) {
	                    if (Math.abs(hd.getTime()-date.getTime())<=5000) {
	                    	logger.debug("Conflicted detected at date:" + date +",conflict with this trigger:" + this.getFullName());	
	                    	date = getFireTimeAfter(date);
	                    	logger.debug("Conflicted detected, current trigger will be skipped and delay to:" + date +","+ this.getFullName());
	                        isconflict = true;
	                    }
	                }
	            }
	            if (isconflict) {
	            	
	                this.nextFireTime=date;
	            }
	    }
		return nextFireTime;
	}
	
	public Date getNextEventTime() {
		Date date = this.nextFireTime;
		
		boolean hasConflict = false;

		if (triggers != null && triggers.length > 0) {

			List<Trigger> list = new ArrayList<Trigger>();
			for (Trigger t : triggers) {
				if (t != null)
					list.add(t);
			}
			if (!list.isEmpty()) {
				Collections.sort(list);

				for (Trigger t : list) {
					if (t.getNextFireTime().getTime() == date.getTime()) {
						date = getFireTimeAfter(date);
						hasConflict = true;
					}
				}
			}
		}
		if (hasConflict) this.nextFireTime = date;
		
		return date;
	}
	
    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

	@Override
	public Date getPreviousFireTime() {
		return this.previousFireTime;
	}

	@Override
	public Date getStartTime() {
		return this.startTime;
	}

	@Override
	public boolean mayFireAgain() {
		return (getNextFireTime() != null);
	}

	@Override
	public void setEndTime(Date endTime) {
		Date sTime = getStartTime();
        if (sTime != null && endTime != null && sTime.after(endTime)) {
            throw new IllegalArgumentException(
                    "End time cannot be before start time");
        }

        this.endTime = endTime;
	}

	@Override
	public void setStartTime(Date startTime) {
		if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }

        Date eTime = getEndTime();
        if (eTime != null && startTime != null && eTime.before(startTime)) {
            throw new IllegalArgumentException(
                "End time cannot be before start time");
        }
        
        // round off millisecond...
        // Note timeZone is not needed here as parameter for
        // Calendar.getInstance(),
        // since time zone is implicit when using a Date in the setTime method.
        Calendar cl = Calendar.getInstance();
        cl.setTime(startTime);
        cl.set(Calendar.MILLISECOND, 0);

        this.startTime = cl.getTime();
	}

	@Override
	public void triggered(org.quartz.Calendar calendar) {
		previousFireTime = nextFireTime;
		nextFireTime = getFireTimeAfter(nextFireTime);

	    while (nextFireTime != null && calendar != null
	    	&& !calendar.isTimeIncluded(nextFireTime.getTime())) {
	        nextFireTime = getFireTimeAfter(nextFireTime);
	    }
	}

	@Override
	public void updateAfterMisfire(org.quartz.Calendar cal) {
		int instr = getMisfireInstruction();

        if (instr == MISFIRE_INSTRUCTION_SMART_POLICY) {
            instr = MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;
        }

        if (instr == MISFIRE_INSTRUCTION_DO_NOTHING) {
            Date newFireTime = getFireTimeAfter(new Date());
            while (newFireTime != null && cal != null
                    && !cal.isTimeIncluded(newFireTime.getTime())) {
                newFireTime = getFireTimeAfter(newFireTime);
            }
            setNextFireTime(newFireTime);
        } else if (instr == MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
            setNextFireTime(new Date());
        }
	}

	@Override
	public void updateWithNewCalendar(org.quartz.Calendar calendar, long misfireThreshold) {
		nextFireTime = getFireTimeAfter(previousFireTime);

        if (nextFireTime == null || calendar == null) {
            return;
        }
        
        Date now = new Date();
        while (nextFireTime != null && !calendar.isTimeIncluded(nextFireTime.getTime())) {

            nextFireTime = getFireTimeAfter(nextFireTime);

            if(nextFireTime == null)
            	break;
            
            //avoid infinite loop
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(nextFireTime);
            if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
                nextFireTime = null;
            }

            if(nextFireTime != null && nextFireTime.before(now)) {
                long diff = now.getTime() - nextFireTime.getTime();
                if(diff >= misfireThreshold) {
                    nextFireTime = getFireTimeAfter(nextFireTime);
                }
            }
        }

	}

	@Override
	protected boolean validateMisfireInstruction(int misfireInstruction) {
		if (misfireInstruction < MISFIRE_INSTRUCTION_SMART_POLICY) {
            return false;
        }

		return true;
	}

	public ScheduleDetailItem getScheduleItem() {
		return scheduleItem;
	}

	public void setScheduleItem(ScheduleDetailItem scheduleItem) {
		if(scheduleItem == null) {
			throw new IllegalArgumentException("Schedule detail item can not be null");
		}
		this.scheduleItem = scheduleItem;
		startDayTime = scheduleItem.getStartTime();
		endDayTime = scheduleItem.getEndTime();
		if(endDayTime.equals(new DayTime(0,0))){
			//support End of Day.
			endDayTime=new DayTime(23,59);
		}
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		if(dayOfWeek > Calendar.SATURDAY || dayOfWeek < Calendar.SUNDAY)
			throw new IllegalArgumentException("Schedule dayOfWeek must be valid");
		this.dayOfWeek = dayOfWeek;
	}

	@Override
	public void setPreviousFireTime(Date previousFireTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ScheduleBuilder getScheduleBuilder() {
		// TODO Auto-generated method stub
		return null;
	}
}
