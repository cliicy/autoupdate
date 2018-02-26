package com.ca.arcserve.edge.app.base.schedulers;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.scheduler.ISchedulerID2DataMapper;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;

public class TestJob implements IScheduleCallBack {
    private static Logger _log = Logger.getLogger(TestJob.class);
	public static String data = "";
	private static TestJob instance = null;
	public static final int scheduler_id = 100;
	public static void init(){
		if(instance==null)
		instance  = new TestJob();
		try {
			SchedulerUtilsImpl.getInstance().registerIDs(instance, new ArrayList<Integer>());
		} catch (EdgeSchedulerException e) {

		}
	}
	public static IScheduleCallBack getInstance(){
		return instance;
	}
	@Override
	public int run(ScheduleData scheduleData, Object arg) {
		data = scheduleData.getScheduleName();
		Date d = new Date(System.currentTimeMillis());
		data +=d.toString();
		_log.debug("+++++++++++++"+data+"+++++++++++++++++++++++++");
		return 1;
	}
	@Override
	public ISchedulerID2DataMapper getID2DataMapper() {
		// TODO Auto-generated method stub
		return EdgeDBIDMapper.getInstance();
	}
}
