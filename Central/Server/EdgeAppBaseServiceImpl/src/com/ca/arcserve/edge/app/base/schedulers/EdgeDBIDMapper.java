package com.ca.arcserve.edge.app.base.schedulers;

import java.util.List;

import com.ca.arcserve.edge.app.base.scheduler.ISchedulerID2DataMapper;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;

public class EdgeDBIDMapper implements ISchedulerID2DataMapper {
	private static EdgeDBIDMapper instance = new EdgeDBIDMapper();
	@Override
	public ScheduleData getSchedule(Integer scheduleID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ScheduleData> getSchedules(List<Integer> scheduleIDs) {
		return SchedulerHelp.getSchedules(scheduleIDs);
	}

	@Override
	public void putSchedules(Integer id, ScheduleData scheduleData) {
		// TODO Auto-generated method stub

	}
	public static ISchedulerID2DataMapper getInstance(){
		return instance;
	}
}
