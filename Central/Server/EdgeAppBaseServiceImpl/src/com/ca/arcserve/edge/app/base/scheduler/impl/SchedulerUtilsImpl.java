package com.ca.arcserve.edge.app.base.scheduler.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.scheduler.ISchedulerUtils;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;


public class SchedulerUtilsImpl implements ISchedulerUtils {
	private static Scheduler scheduler = null;
	private static Logger _log = Logger.getLogger(SchedulerUtilsImpl.class);
	static ISchedulerUtils instance = null;

	static ICallBackIDMapping maps;

	private SchedulerUtilsImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static ISchedulerUtils getInstance()   {

		return instance;
	}
	public static synchronized Scheduler getScheduler() throws EdgeSchedulerException {
		if(scheduler == null) 			
			throw new EdgeSchedulerException("we have no scheduler instance yet!",new NullPointerException());		
		
		return scheduler;
	}
	public static Properties props = null;
	public static  synchronized void initScheduler(Properties props)
			throws EdgeSchedulerException {
		try {
			_log.info("initScheduler begin");
			if (scheduler != null) {
				scheduler.shutdown();
			}
		} catch (SchedulerException e) {
		} finally{
			scheduler = null;
		}
		try {
			SchedulerUtilsImpl.props = props;
			StdSchedulerFactory fa = null;
			if (props == null)
				fa = new StdSchedulerFactory();
			else
				fa = new StdSchedulerFactory(props);

			scheduler = fa.getScheduler();
			instance = new SchedulerUtilsImpl();
			CallBackIDMapping.init();
			maps = CallBackIDMapping.getInstance();
			scheduler.start();

		} catch (SchedulerException e) {
			_log.error("initScheduler exception");
			throw new EdgeSchedulerException(e.getMessage(), e.getCause());
		}
		_log.info("initScheduler end");
	}

	public static  synchronized  void shutdownScheduler() throws EdgeSchedulerException {
		try {
			_log.warn("shutdownScheduler begin");
			if (scheduler != null)
				scheduler.shutdown();
			_log.warn("shutdownScheduler end");

		} catch (SchedulerException e) {
			_log.error("shutdownScheduler exception");
			throw new EdgeSchedulerException(e.getMessage(), e.getCause());
		}
		scheduler = null;
	}

	@Override
	public synchronized boolean  addIDs(IScheduleCallBack callback, List<Integer> ids,List<Object> args)
			throws EdgeSchedulerException {
		List<ScheduleData> schedules = maps.addIDs(callback, ids);
		if (!schedules.isEmpty()) {
			scheduleJobs(callback,schedules,args);
		}
		return !schedules.isEmpty();

	}

	private void scheduleJobs(IScheduleCallBack callback,List<ScheduleData> schedules,List<Object> args)
			throws EdgeSchedulerException {
		int i = 0;
		for (ScheduleData scheduleData : schedules) {
			Object arg = null;
			if(args!=null && args.size()>i)
				arg = args.get(i);
			scheduleData = scheduleJob( callback,scheduleData,arg);
		}
	}
	public static String getJobName(ScheduleData scheduleData){
		if(scheduleData.getScheduleName()==null || scheduleData.getScheduleName().trim().isEmpty())
			return JOBNAME_PRE + scheduleData.getScheduleID();
		else
			return  JOBNAME_PRE + scheduleData.getScheduleName().trim()+"_"+scheduleData.getScheduleID();
	}
	public static String getTriggerName(ScheduleData scheduleData){
		return getJobName(scheduleData)+TRIGGER_SUFFIX;
	}
	private ScheduleData scheduleJob(IScheduleCallBack callback,
			ScheduleData scheduleData,Object arg) throws EdgeSchedulerException {
		String jobName = getJobName(scheduleData);
		try {
			Trigger edgeTrigger = EdgeTriggerUtil.getEdgeTrigger(
					getTriggerName(scheduleData), TRIGGER_GROUP, scheduleData,arg,callback);
			JobDetail job = new JobDetailImpl(jobName, JOB_GROUP,
					SchedulerEdgeJob.class);

			scheduler.deleteJob(new JobKey(jobName, JOB_GROUP));
			scheduler.scheduleJob(job, edgeTrigger);

			_log.warn("job name:" + jobName + ", start time:" + edgeTrigger.getStartTime());

		} catch (SchedulerException e) {
			throw new EdgeSchedulerException(e.getMessage(), e.getCause());
		}
		return scheduleData;
	}
	@Override
	public boolean registerIDs(IScheduleCallBack callback, List<Integer> ids)
			throws EdgeSchedulerException {
		return registerIDs(callback,ids,null);
	}
	@Override
	public synchronized boolean registerIDs(IScheduleCallBack callback, List<Integer> ids, List<Object> args)
			throws EdgeSchedulerException {
		List<ScheduleData> schedules = maps.registerIDs(callback, ids);
		if (!schedules.isEmpty()) {
			scheduleJobs(callback,schedules,args);
		}
		return !schedules.isEmpty();

	}

	@Override
	public synchronized boolean removeIDs(IScheduleCallBack callback, List<Integer> ids)
			throws EdgeSchedulerException {

		List<Integer> scheduleIDs = new ArrayList<Integer>();
		scheduleIDs.clear();

		for (Integer id : ids) {
			if (maps.isScheduleIDExist(callback, id)) {
				scheduleIDs.add(id);
			}
		}
		if (scheduleIDs.isEmpty()) {
			return true;
		}


		List<ScheduleData> removedIds = maps.removeIDs(callback, scheduleIDs);
		if (!removedIds.isEmpty()) {

			for (ScheduleData id : removedIds) {
				String jobName = getJobName(id);
				try {
					scheduler.deleteJob(new JobKey(jobName, JOB_GROUP));
				} catch (SchedulerException e) {
					throw new EdgeSchedulerException(e.getMessage(), e
							.getCause());
				}
			}
		}
		return !removedIds.isEmpty();
	}
	@Override
	public synchronized void clearIDs(IScheduleCallBack callback)
			throws EdgeSchedulerException {
		List<ScheduleData> clearIDs = maps.clearIDs(callback);
		{
			for (ScheduleData id : clearIDs) {
				String jobName = getJobName(id);
				try {
					scheduler.deleteJob(new JobKey(jobName, JOB_GROUP));
				} catch (SchedulerException e) {

				}
			}
		}
		return ;
	}



	@Override
	public synchronized boolean updateScheduler(IScheduleCallBack callback, ScheduleData scheduleData,Object arg)
			throws EdgeSchedulerException {

		if (!maps.isScheduleIDExist(callback, scheduleData.getScheduleID())) {
			List<Integer> ids = new ArrayList<Integer>();
			ids.add(scheduleData.getScheduleID());
			List<Object> args = new ArrayList<Object>();
			if(arg!=null)
				args.add(arg);
			this.addIDs(callback, ids,args);
			return true;
		}
		{
			boolean re = maps.updateData(callback,scheduleData);
			if (!re)
				return false;
			scheduleJob(callback,scheduleData,arg);
			return re;
		}
	}
	
	public List<Trigger> listTriggers(){
		List<Trigger> result = new ArrayList<Trigger>();
		try {
			List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
			for(String triGroup:triggerGroupNames){
				String[] triggerNames = getTriggerNames(scheduler, triGroup);
				for(String triggerName:triggerNames){
					Trigger tri = scheduler.getTrigger(new TriggerKey(triggerName, triGroup));
					result.add(tri);
				}
			}
		} catch (SchedulerException e) {

		}
		return result;

	}
	
	public static String[] getTriggerNames(Scheduler scheduler, String tirggerGroupname) throws SchedulerException{
		List<String> triggerNameList = new ArrayList<String>();
		Set<TriggerKey> sets = scheduler.getTriggerKeys(GroupMatcher.<TriggerKey>groupEquals(tirggerGroupname));

		if (sets != null)
			for (TriggerKey key : sets) {
				Trigger trigger = scheduler.getTrigger(key);
				triggerNameList.add(trigger.getKey().getName());
			}
		
		return triggerNameList.toArray(new String[0]);
	}

	@Override
	public void runNow(final IScheduleCallBack callback, final Object arg)
			throws EdgeSchedulerException {
		runNow(callback, null, arg );
	}
	@Override
	public void runNow(final IScheduleCallBack callback, final  ScheduleData data,  final Object arg)
			throws EdgeSchedulerException {
		EdgeExecutors.getCachedPool().submit(new Runnable() {
			
			@Override
			public void run() {
				callback.run(data, arg);
			}
			
		});
	}
	@Override
	public boolean updateScheduler(IScheduleCallBack callback,
			ScheduleData scheduleData) throws EdgeSchedulerException {
		return updateScheduler(callback,scheduleData,(Object)null);
	}

	@Override
	public void clearJobs(IScheduleCallBack callback) throws EdgeSchedulerException {
		if (_log.isDebugEnabled()){
			_log.debug("clearJobs for callback:"+callback.getClass().getName());
		}
		List<ScheduleData> scheduleDataList = maps.getScheduleData(callback);
		if (scheduleDataList != null && scheduleDataList.size() > 0) {
			List<Integer> ids = new ArrayList<Integer>();
			for (ScheduleData scheduleData : scheduleDataList) {
				ids.add(scheduleData.getScheduleID());
			}
			removeIDs(callback, ids);
		}
	}


}
