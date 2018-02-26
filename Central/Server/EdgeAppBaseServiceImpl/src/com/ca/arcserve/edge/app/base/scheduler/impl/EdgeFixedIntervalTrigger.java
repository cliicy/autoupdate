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


/**
 * this trigger will server every seconds, minutes, hours, and days
 * @author gonro07
 *
 */
public class EdgeFixedIntervalTrigger extends SimpleTriggerImpl implements EdgeTrigger {
	 /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(EdgeFixedIntervalTrigger.class);
	private IScheduleCallBack callback;
	private  ScheduleData scheduleData;
	public EdgeFixedIntervalTrigger(String name, String group, Date startTime,
	            Date endTime,  long repeatInterval) {
		 super(name,group,startTime,endTime,SimpleTrigger.REPEAT_INDEFINITELY,repeatInterval);
	 }
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
	@Override
    public void triggered(Calendar calendar) {
        this.setTimesTriggered(this.getTimesTriggered()+1);
        this.setPreviousFireTime(this.getNextFireTime());
        if(this.getTimesTriggered() >= this.getRepeatCount() && this.getRepeatCount()!=SimpleTrigger.REPEAT_INDEFINITELY){
        	this.setNextFireTime(null);
        	return;
        }
        Date d = getFireTimeAfter(this.getNextFireTime());
        this.setNextFireTime(d);
        return;

    }
	@Override
    public void updateAfterMisfire(org.quartz.Calendar cal) {
		this.setTimesTriggered(this.getTimesTriggered() + 1);
		if (this.getTimesTriggered() >= this.getRepeat_count()
				&& this.getRepeat_count() != SimpleTrigger.REPEAT_INDEFINITELY) {
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
			temp = this.getFireTimeAfter(temp);
		}
		return list;

	}
}
