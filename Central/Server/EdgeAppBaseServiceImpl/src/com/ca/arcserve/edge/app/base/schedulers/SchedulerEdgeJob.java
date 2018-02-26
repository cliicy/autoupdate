package com.ca.arcserve.edge.app.base.schedulers;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;

import com.ca.arcserve.edge.app.base.scheduler.EdgeTrigger;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;

public class SchedulerEdgeJob extends EdgeJob {
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Trigger trigger = context.getTrigger();
		if(trigger instanceof EdgeTrigger){
			EdgeTrigger et = (EdgeTrigger)trigger;
			IScheduleCallBack callback = et.getCallback();
			if(callback!=null)
				callback.run(et.getScheduleData(), (Object)null);
		}
	}


}
