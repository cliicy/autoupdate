package com.ca.arcserve.edge.app.base.webservice.taskmonitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoTask;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeTaskMonitorDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.Task;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;

public class TaskMonitor {

	private static IEdgeTaskMonitorDao taskDao = DaoFactory.getDao(IEdgeTaskMonitorDao.class);
	private final static Logger logger = Logger.getLogger(TaskMonitor.class);
	
	/* Clean up old task after service restart */
	static{
		try {
			taskDao.as_edge_task_delete(-1);// -1 mean delete all
		}
		catch( Throwable e ){
			logger.error( "failed delete out of date task list", e);
		}
	}

	/*
	 * Register a new task in common task monitor . return task id if success
	 * Task status will be initialized to pending, should call setTaskStarted() to update task to start.
	 */ 
	public static synchronized int registerNewTask(Module module, String target, TaskDetail details){
		return registerNewTask( module, target, TaskStatus.Pending , details );
	}
	public static synchronized int registerNewTask(Module module, String target, TaskStatus initStatus, TaskDetail details ){
		int[] taskID = new int[1];
		String xmlString = null;
		try {
			xmlString = CommonUtil.marshal(details);
		} catch (JAXBException e) {
			logger.error(e);
		}
		taskDao.as_edge_task_register(
				module, 
				target, 
				initStatus,
				xmlString,
				taskID );
		return taskID[0];
	}
	/* set task status to started */
	public static void setTaskStarted(int taskID){
		taskDao.as_edge_task_set_started( taskID , TaskStatus.InProcess, new Date());
	}
	
	/*update task status */
	public static void updateTaskStatus(int taskID, TaskStatus status, TaskDetail details){
		String xmlString = null;
		try {
			xmlString = CommonUtil.marshal(details);
		} catch (JAXBException e) {
			logger.error(e);
		}
		Date endTime = null;
		if(status.getValue() > TaskStatus.InProcess.getValue()){
			endTime = new Date(); // when task complete, set task endTime
		}
		taskDao.as_edge_task_update_status(taskID, status, xmlString, endTime);
	}
	
	/* Get task list*/
	public static List<Task> getTaskList(){
		List<EdgeDaoTask> result = new ArrayList<EdgeDaoTask>();
		taskDao.as_edge_task_get_list(result);
		return convertDaoTask(result);
	}

	/* Delete task */
	public static void deleteTask(int taskID){
		taskDao.as_edge_task_delete(taskID);
	}
	
	private static List<Task> convertDaoTask(List<EdgeDaoTask> list) {
		List<Task> result = new ArrayList<Task>(list.size());
		for(EdgeDaoTask daotask: list){
			Task task = new Task();
			task.setId(daotask.getId());
			task.setModule(daotask.getModule());
			task.setTarget(daotask.getTarget());
			task.setStatus(daotask.getStatus());
			task.setStartTime(daotask.getStartTime());
			task.setEndTime(daotask.getEndTime());
			try {
				// Unmarshal detailXml and set detail to Task object
				TaskDetail detail = CommonUtil.unmarshal(daotask.getDetailXml(), TaskDetail.class);
				task.setDetails(detail);
			} catch (JAXBException e) {
				logger.error(e);
			}
			result.add(task);
		}
		return result;
	}
	public static List<Task> getTasksByModule(Module module) {
		List<EdgeDaoTask> result = new ArrayList<EdgeDaoTask>();
		taskDao.as_edge_task_get_list_by_module(module, result);
		return convertDaoTask(result);
	}
}


