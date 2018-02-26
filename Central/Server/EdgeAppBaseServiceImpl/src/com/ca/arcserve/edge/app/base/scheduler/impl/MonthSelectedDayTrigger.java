package com.ca.arcserve.edge.app.base.scheduler.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Calendar;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcserve.edge.app.base.scheduler.EdgeTrigger;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;



public class MonthSelectedDayTrigger extends SimpleTriggerImpl implements
		EdgeTrigger {
	private static final Logger logger = Logger.getLogger(MonthSelectedDayTrigger.class);
	public MonthSelectedDayTrigger(String name, String group,
			boolean fromBegin, int dayNumber) {
		super(name, group);
		this.fromBegin = fromBegin;
		this.dayNumber = dayNumber;
	}

	@Override
	public Date computeFirstFireTime(Calendar calendar) {
		boolean isTime = this.isOnSchedulerPoint(getStartTime());
		if (isTime)
			this.setNextFireTime(getStartTime());
		else
			this.setNextFireTime(getFireTimeAfter(getStartTime()));

		return this.getNextFireTime();
	}

	@Override
	public void validate() throws SchedulerException {
		this.setRepeatInterval(1);
		super.validate();
		this.setRepeatInterval(0);
	}
	@Override
    public void updateAfterMisfire(org.quartz.Calendar cal) {
		this.setTimesTriggered(this.getTimesTriggered() + 1);
		if (this.getTimesTriggered() >= this.getRepeatCount()
				&& this.getRepeatCount() != SimpleTrigger.REPEAT_INDEFINITELY) {
			this.setNextFireTime(null);
			try {
				SchedulerUtilsImpl.getScheduler().deleteJob(new JobKey(this.getJobName(), this.getJobGroup()));
			} catch (SchedulerException e) {
				logger.error(e.getMessage(), e);
			}
			return;
		}

		Date d = getFireTimeAfter(this.getNextFireTime());
        if (d!=null && getEndTime() != null && getEndTime().before(d)) {
            setNextFireTime(null); // We are past the end time
			try {
				SchedulerUtilsImpl.getScheduler().deleteJob(new JobKey(this.getJobName(), this.getJobGroup()));
			} catch (SchedulerException e) {
				logger.error(e.getMessage(), e);
			}
            return;
        }
		this.setNextFireTime(d);
    }
	@Override
	public Date getFireTimeAfter(Date afterTime) {
		if (afterTime == null) {
			afterTime = new Date();
		}

		long startMillis = getStartTime().getTime();
		long afterMillis = afterTime.getTime();
		long endMillis = (getEndTime() == null) ? Long.MAX_VALUE : getEndTime()
				.getTime();

		if (endMillis <= afterMillis) {
			return null;
		}

		if (afterMillis < startMillis) {
			return new Date(startMillis);
		}

		java.util.Calendar afterCal = java.util.Calendar.getInstance();
		afterCal.setTimeInMillis(afterTime.getTime());

		int mon = afterCal.get(java.util.Calendar.MONTH); // jan 0

		java.util.Calendar instance = java.util.Calendar.getInstance();
		int i = 0;
		while (i < 12) {
			i++;
			instance.setTimeInMillis(this.getStartTime().getTime());
			instance.set(java.util.Calendar.MONTH, mon);
			instance.add(java.util.Calendar.MONTH, i - 1);
			int days = instance
					.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
			
			// [lijwe02] if the day is fewer then dayNumber, then use day of the month
			int triggerDay = dayNumber > days ? days : dayNumber;
			if (!this.fromBegin)
				triggerDay = days - (triggerDay - 1);
			if (triggerDay < 0)
				continue;
			instance.set(java.util.Calendar.DAY_OF_MONTH, triggerDay);

			if (instance.getTimeInMillis() <= afterTime.getTime())
				continue;
			if (endMillis <= instance.getTimeInMillis()) {
				return null;
			}
			return instance.getTime();

		}

		return null;
	}

	private boolean isOnSchedulerPoint(Date questionTime) {

		java.util.Calendar questionCal = java.util.Calendar.getInstance();
		questionCal.setTimeInMillis(this.getStartTime().getTime());

		int days = questionCal
				.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

		// [lijwe02] if day of the month less than day number, use day of the month
		int triggerDay = dayNumber > days ? days : dayNumber;
		if (!this.fromBegin)
			triggerDay = days - (triggerDay - 1);
		if (triggerDay < 0)
			return false;
		questionCal.set(java.util.Calendar.DAY_OF_MONTH, triggerDay);
		if (questionTime.getTime() == questionCal.getTimeInMillis())
			return true;
		return false;

	}

	@Override
	public void triggered(Calendar calendar) {
		this.setTimesTriggered(this.getTimesTriggered() + 1);
		this.setPreviousFireTime(this.getNextFireTime());
		if (this.getTimesTriggered() >= this.getRepeatCount()
				&& this.getRepeatCount() != SimpleTrigger.REPEAT_INDEFINITELY) {
			this.setNextFireTime(null);
			return;
		}
		Date d = getFireTimeAfter(this.getNextFireTime());
		this.setNextFireTime(d);
		return;

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private boolean fromBegin = true;
	private int dayNumber = 1;

	public boolean isFromBegin() {
		return fromBegin;
	}

	public void setFromBegin(boolean fromBegin) {
		this.fromBegin = fromBegin;
	}

	public int getDayNumber() {
		return dayNumber;
	}

	public void setDayNumber(int dayNumber) {
		this.dayNumber = dayNumber;
	}

	private IScheduleCallBack callback;
	private ScheduleData scheduleData;

	@Override
	public IScheduleCallBack getCallback() {
		// TODO Auto-generated method stub
		return callback;
	}

	@Override
	public int getRepeat_count() {
		return this.getRepeatCount();
	}

	@Override
	public ScheduleData getScheduleData() {
		// TODO Auto-generated method stub
		return scheduleData;
	}

	public void setCallback(IScheduleCallBack callback) {
		this.callback = callback;
	}

	public void setScheduleData(ScheduleData scheduleData) {
		this.scheduleData = scheduleData;
	}

	@Override
	public void setRepeat_count(int repeartCount) {
		this.setRepeatCount(repeartCount);

	}
	Object args = null;
	@Override
	public Object getArgs() {
		// TODO Auto-generated method stub
		return args;
	}
	@Override
	public void setArgs(Object args) {
		this.args = args;

	}
	@Override
	public List<Date> getScheduleDates(Date begin, Date end) {
		List<Date> list = new ArrayList<Date>();
		Date temp = this.getFireTimeAfter(begin);
		while(temp!=null  && temp.before(end)){
			list.add(temp);
			temp = this.getFireTimeAfter(begin);
		}
		return list;

	}
}
