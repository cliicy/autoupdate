package com.ca.arcserve.edge.app.base.scheduler.impl;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;

import com.ca.arcserve.edge.app.base.scheduler.EdgeTrigger;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;



public class SchedulerEdgeJob implements Job  {
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try{
		Trigger trigger = context.getTrigger();
		if(trigger instanceof EdgeTrigger){
			EdgeTrigger et = (EdgeTrigger)trigger;
			IScheduleCallBack callback = et.getCallback();
			if(callback!=null)
				callback.run(et.getScheduleData(),et.getArgs());
		}
		}catch(Throwable t){
			System.out.println("SchedulerEdgeJob ends with exception: "+t.getMessage());
		}
	}


}
