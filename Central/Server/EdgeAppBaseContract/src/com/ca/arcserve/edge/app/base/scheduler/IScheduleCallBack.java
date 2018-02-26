package com.ca.arcserve.edge.app.base.scheduler;

import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;


public interface IScheduleCallBack {
	/**
	 *
	 * @param scheduleData
	 * @return delay request in seconds
	 */
	public int run(ScheduleData scheduleData, Object args);
	public ISchedulerID2DataMapper getID2DataMapper();
}
