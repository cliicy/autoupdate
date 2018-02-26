package com.ca.arcserve.edge.app.base.scheduler;

import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;

/**
 * every callback should have a ID2Mapper. Edge will mapp ID from DB
 * @author gonro07
 *
 */
public interface ISchedulerID2DataMapper {
	public  List<ScheduleData> getSchedules(List<Integer> scheduleIDs) ;
	public  void putSchedules(Integer id,ScheduleData scheduleData) ;
	public ScheduleData getSchedule(Integer scheduleID) ;
}
