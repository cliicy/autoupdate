package com.ca.arcserve.edge.app.base.scheduler.impl;

import java.util.List;

import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;


interface ICallBackIDMapping {

	 /**
	  *
	  * @param callback
	  * @return the ScheduleData list for the callback. may be null
	  * @throws EdgeSchedulerException
	  */
	 List<ScheduleData>  getScheduleData(IScheduleCallBack callback) throws EdgeSchedulerException;
	/**
	 * It will get ScheduleData from as_edge_schedule table
	 * @param callback
	 * @param ids
	 * @return successfully added ScheduleData list, cannot be null.
	 * @throws EdgeSchedulerException
	 */
	 List<ScheduleData>  addIDs(IScheduleCallBack callback, List<Integer> ids) throws EdgeSchedulerException;
	 /**
	  * It will get ScheduleData from as_edge_schedule table
	  * @param callback
	  * @param ids
	  * @return the list of ScheduleData converted from List of Ids
	  * @throws EdgeSchedulerException if the callback has been registered
	  */
	List<ScheduleData> registerIDs(IScheduleCallBack callback, List<Integer> ids) throws EdgeSchedulerException;
	/**
	 *
	 * @param callback
	 * @param ids
	 * @return the list of ScheduleData removed
	 * @throws EdgeSchedulerException if callback does not exist
	 */
	List<ScheduleData>  removeIDs(IScheduleCallBack callback, List<Integer> ids) throws EdgeSchedulerException;
	/**
	 *
	 * @param callback
	 * @return the ScheduleData list of callback. the list cannot be null
	 * @throws EdgeSchedulerException
	 */
	List<ScheduleData>  clearIDs(IScheduleCallBack callback)  throws EdgeSchedulerException;
	/**
	 *
	 * @param scheduleData
	 * @return true if callback contains the scheduleData
	 * @throws EdgeSchedulerException if callback or scheduleData doesnot exist
	 */
	boolean updateData(IScheduleCallBack callback,ScheduleData scheduleData) throws EdgeSchedulerException;
	/**
	 *
	 * @param callback
	 * @param id
	 * @return false if callback or id does not exist in the map, else true
	 * @throws EdgeSchedulerException
	 */
	boolean isScheduleIDExist(IScheduleCallBack callback, Integer id) throws EdgeSchedulerException;


}
