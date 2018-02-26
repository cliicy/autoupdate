package com.ca.arcserve.edge.app.base.scheduler;

import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;

/**
 * if {@link ScheduleData#getScheduleName()} is valid, the Job Name will be {@link #JOBNAME_PRE}_{@link ScheduleData#getScheduleName()}_{@link ScheduleData#getScheduleID()}
 * <br/>else  Job Name is {@link #JOBNAME_PRE}_{@link ScheduleData#getScheduleID()}
 * @author gonro07
 *
 */
public interface ISchedulerUtils {
	public static final String JOB_GROUP = "EDGE_JOB_GROUP";
	public static final String JOBNAME_PRE = "ID_";
	public static final String TRIGGER_SUFFIX = "_TRIGGER";
	public static final String TRIGGER_GROUP = "EDGE_TRIGGER_GROUP";
	/**
	 * used by UI to update scheduler. The Id of scheduleData maynot exist. In that case, the system will get Data from as_edge_schedule.
	 * <br/>
	 * It will reschedule the job
	 * @param callback
	 * @param scheduleData
	 * @return
	 * @throws EdgeSchedulerException
	 */
	public boolean updateScheduler(IScheduleCallBack callback, ScheduleData scheduleData,Object args)  throws EdgeSchedulerException ;
	/**
	 * used by UI to update scheduler. The Id of scheduleData maynot exist. In that case, the system will get Data from as_edge_schedule.
	 * <br/>
	 * It will reschedule the job
	 * @param callback
	 * @param scheduleData
	 * @return
	 * @throws EdgeSchedulerException
	 */
	public boolean updateScheduler(IScheduleCallBack callback, ScheduleData scheduleData)  throws EdgeSchedulerException ;

	/**
	 * used in init phase,  make sure the id exists in as_edge_schedule.
	 * <br/>
	 * It will schedule the jobs.
	 * @param callback
	 * @param ids
	 * @throws EdgeSchedulerException
	 */
	public boolean registerIDs(IScheduleCallBack callback,List<Integer> ids,List<Object> args)throws EdgeSchedulerException ;

	/**
	 * there is no args for job to run
	 * @param callback
	 * @param ids
	 * @return
	 * @throws EdgeSchedulerException
	 */
	public boolean registerIDs(IScheduleCallBack callback,List<Integer> ids)throws EdgeSchedulerException ;


	/**
	 * called when adding a machine to do synch. the callback must has been registered.
	 * Make sure the id exists in as_edge_schedule.
	 * <br/>
	 * It will schedule the jobs.
	 * @param callback
	 * @param ids
	 * @return true for success, else false
	 * @throws EdgeSchedulerException
	 */
	public boolean addIDs(IScheduleCallBack callback,List<Integer> ids,List<Object> args)throws EdgeSchedulerException ;
	/**
	 * called when remove a machine
	 * <br/>
	 * It will delete the jobs.
	 * @param callback
	 * @param ids
	 * @return
	 * @throws EdgeSchedulerException
	 */
	public boolean removeIDs(IScheduleCallBack callback,List<Integer> ids)throws EdgeSchedulerException ;
	/**
	 * It will delete all jobs under the callback
	 * @param callback
	 * @throws EdgeSchedulerException
	 */
	public void clearIDs(IScheduleCallBack callback)throws EdgeSchedulerException ;
	
	/**
	 * Remove all the jobs under the callback, and remove the ids from the map
	 * 
	 * @param callback
	 *            the callback for retrieve job ids
	 * @throws EdgeSchedulerException
	 *             error occurred when clear jobs
	 */
	public void clearJobs(IScheduleCallBack callback) throws EdgeSchedulerException;

	/**
	 * Run the callback at once with NULL scheduleData.
	 * @param callback
	 * @param arg
	 * @throws EdgeSchedulerException
	 */
	public void runNow(IScheduleCallBack callback, Object arg) throws EdgeSchedulerException ;
	
	/**
	 * Run the callback at once with NULL scheduleData.
	 * @param callback
	 * @param schedule data
	 * @param arg
	 * @throws EdgeSchedulerException
	 */
	public void runNow(IScheduleCallBack callback,ScheduleData data, Object arg) throws EdgeSchedulerException ;

}
