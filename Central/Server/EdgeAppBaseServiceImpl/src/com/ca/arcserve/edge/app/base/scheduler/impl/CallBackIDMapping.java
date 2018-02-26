package com.ca.arcserve.edge.app.base.scheduler.impl;


import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;



public class CallBackIDMapping implements ICallBackIDMapping{
	private static ICallBackIDMapping instance = null;
	public static void init(){
		instance = new CallBackIDMapping();
	}
	public static ICallBackIDMapping getInstance(){
		return instance;
	}
	private Map<IScheduleCallBack,List<ScheduleData> > maps = new IdentityHashMap<IScheduleCallBack,List<ScheduleData>>();
	@Override
	public List<ScheduleData>  addIDs(IScheduleCallBack callback, List<Integer> ids) throws EdgeSchedulerException {
		List<ScheduleData> schedules = maps.get(callback);
		if(schedules == null) throw new EdgeSchedulerException("Job does not exist",EdgeSchedulerException.ERR_BAD_SCHEDULE_JOB_NONEXISTED);
		for(ScheduleData schedule:schedules)
			for(int id:ids){
				if(schedule.getScheduleID() == id) throw new EdgeSchedulerException("ID existed",EdgeSchedulerException.ERR_BAD_SCHEDULE_ID_EXISTED);
			}
		List<ScheduleData> newschedules = callback.getID2DataMapper().getSchedules(ids);
		schedules.addAll(newschedules);
		return newschedules;
	}



	@Override
	public List<ScheduleData>  getScheduleData(IScheduleCallBack callback)  throws EdgeSchedulerException {
		return maps.get(callback);
	}

	@Override
	public List<ScheduleData>  registerIDs(IScheduleCallBack callback, List<Integer> ids)  throws EdgeSchedulerException {
		if(null!=maps.get(callback))  throw new EdgeSchedulerException("Job already exists",EdgeSchedulerException.ERR_BAD_SCHEDULE_JOB_EXISTED);
		List<ScheduleData> newschedules = callback.getID2DataMapper().getSchedules(ids);
		maps.put(callback, newschedules);
		return newschedules;
	}

	@Override
	public List<ScheduleData> removeIDs(IScheduleCallBack callback, List<Integer> ids)  throws EdgeSchedulerException {
		if(null==maps.get(callback))  throw new EdgeSchedulerException("Job does not exist",EdgeSchedulerException.ERR_BAD_SCHEDULE_JOB_NONEXISTED);
		List<ScheduleData> list = maps.get(callback);
		List<ScheduleData> newschedules = new ArrayList<ScheduleData>();
		for(int id:ids){
			ScheduleData da = new ScheduleData();
			da.setScheduleID(id);
			newschedules.add(da);
		}
		list.removeAll(newschedules);
		return newschedules;

	}
	@Override
	public List<ScheduleData> clearIDs(IScheduleCallBack callback)
			throws EdgeSchedulerException {
		List<ScheduleData> results = new ArrayList<ScheduleData>();
		List<ScheduleData> list = maps.get(callback);
		if(null==list) return results;
		//maps.remove(callback);
		return list;
	}

	@Override
	public boolean updateData(IScheduleCallBack callback,ScheduleData scheduleData)
			throws EdgeSchedulerException {
		List<ScheduleData> schedules = maps.get(callback);
		if(null == schedules)  {
			 throw new EdgeSchedulerException("Job does not exist",EdgeSchedulerException.ERR_BAD_SCHEDULE_JOB_NONEXISTED);
		}

		if(schedules.contains(scheduleData)){
			schedules.remove(scheduleData);
			schedules.add(scheduleData);
			return true;
		}

		throw new EdgeSchedulerException("Schedule Data does not exist",EdgeSchedulerException.ERR_BAD_SCHEDULE_ID_INVALID);

	}
	@Override
	public boolean isScheduleIDExist(IScheduleCallBack callback, Integer id) throws EdgeSchedulerException {
		if(null == maps.get(callback))  {
			return false;
		}
		List<ScheduleData> list = maps.get(callback);
		if (!list.isEmpty()) {
			for (ScheduleData scheduleData : list) {
				if (scheduleData.getScheduleID() == id) {
					return true;
				}
			}
		}
		return false;

	}

}
