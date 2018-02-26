package com.ca.arcserve.edge.app.base.scheduler.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Calendar;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.CronTriggerImpl;

import com.ca.arcserve.edge.app.base.scheduler.EdgeTrigger;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;

/**
 *
 * <pre>Cron expressions are comprised of 6 required fields and one optional field separated by white space. The fields respectively are described as follows:

	Field Name   Allowed Values   Allowed Special Characters
	Seconds    0-59    , - * /
	Minutes    0-59    , - * /
	Hours    0-23    , - * /
	Day-of-month    1-31    , - * ? / L W
	Month    1-12 or JAN-DEC    , - * /
	Day-of-Week    1-7 or SUN-SAT    , - * ? / L #
	Year (Optional)    empty, 1970-2099    , - * /
</pre>
 * @author gonro07
 *
 */
public class EdgeWeekTrigger extends CronTriggerImpl implements EdgeTrigger {
	
	private static final Logger logger = Logger.getLogger(EdgeWeekTrigger.class);
	
	private int repeat_count = -1;
	private IScheduleCallBack callback;
	private  ScheduleData scheduleData;
	private int TimesTriggered = 0;
	public int getTimesTriggered() {
		return TimesTriggered;
	}
	public void setTimesTriggered(int timesTriggered) {
		TimesTriggered = timesTriggered;
	}
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public int getRepeat_count() {
		return repeat_count;
	}
	public void setRepeat_count(int repeatCount) {
		repeat_count = repeatCount;
	}

	public EdgeWeekTrigger() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EdgeWeekTrigger(String name, String group, String cronExpression)
			throws ParseException {
		super(name, group, cronExpression);

		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMisfireInstruction() {
		// TODO Auto-generated method stub
		return MISFIRE_INSTRUCTION_DO_NOTHING;
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
	public IScheduleCallBack getCallback() {
		return callback;
	}
	public void setCallback(IScheduleCallBack callback) {
		this.callback = callback;
	}
	public ScheduleData getScheduleData() {
		return scheduleData;
	}
	public void setScheduleData(ScheduleData scheduleData) {
		this.scheduleData = scheduleData;
	}
	@Override
	public void triggered(Calendar calendar) {
		this.setTimesTriggered(this.getTimesTriggered() + 1);
		this.setPreviousFireTime(this.getNextFireTime());
		if (this.getTimesTriggered() >= this.getRepeat_count()
				&& this.getRepeat_count() != SimpleTrigger.REPEAT_INDEFINITELY) {
			this.setNextFireTime(null);
			return;
		}
		Date d = getFireTimeAfter(this.getNextFireTime());
		this.setNextFireTime(d);
		return;

	}
	@Override
	public Date computeFirstFireTime(Calendar calendar) {
		// TODO Auto-generated method stub
		return super.computeFirstFireTime(calendar);
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
