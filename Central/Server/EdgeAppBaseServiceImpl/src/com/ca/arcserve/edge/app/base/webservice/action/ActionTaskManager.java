package com.ca.arcserve.edge.app.base.webservice.action;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskData;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.taskmonitor.TaskMonitor;

public class ActionTaskManager<T extends Serializable>{
	private static final Logger logger = Logger.getLogger(ActionTaskManager.class);
	private ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	private volatile TaskDetail<ActionTaskData<T>> taskDetail;
	private volatile ActionTaskData<T> data;
	private volatile int taskId;
	
	private ActionTaskParameter<T> parameter;
	private EdgeWebServiceImpl webService;
	
	public ActionTaskManager(ActionTaskParameter<T> parameter){
		this(parameter,null);
	}
	
	public ActionTaskManager(ActionTaskParameter<T> parameter,EdgeWebServiceImpl webService){
		this.parameter = parameter;
		this.webService = webService;
	}

	public int doAction() {
		int taskId = registerTask(parameter.getModule());
		TaskMonitor.setTaskStarted(taskId);
		runTask();
		return taskId;
	}
	
	private void runTask(){
		final List<T> entityIds = parameter.getEntityIds();
		data.setTotalEntities(entityIds);
		updateTask(TaskStatus.InProcess);
		Runnable actionTask = new Runnable(){
			
			@Override
			public void run() {
				CountDownLatch doneSignal = new CountDownLatch(entityIds.size());
				generateTaskBeginLog();
				try {	
					Iterator<T> iterator = entityIds.iterator();
					while (iterator.hasNext()) {
						Object entityId = (Object) iterator.next();
						Runnable taskRunner = ActionTaskRunnerFactory.getInstance().createTaskRunner(parameter , entityId, doneSignal, ActionTaskManager.this);
						EdgeExecutors.getCachedPool().submit(taskRunner);
					}
				} catch (Exception e) {
					logger.error("[ActionTaskManager] runTask() failed.", e);
				}
					
				try {
					doneSignal.await();
				} catch (InterruptedException e) {
					logger.error("[ActionTaskManager] doneSignal.await() faild", e);
				} finally {
					updateTask(TaskStatus.OK);
				}
					
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
						
				}
				
				generateTaskFinishLog();
			}		
		};
		EdgeExecutors.getCachedPool().submit(actionTask);
	}

	public int registerTask(Module module) {
		if(data == null)
			data = new ActionTaskData<T>();
		if(taskDetail == null){
			 taskDetail = new TaskDetail<ActionTaskData<T>>();
			 taskDetail.setRawData(data);
		}
		taskId = TaskMonitor.registerNewTask(module, "", taskDetail);
		return taskId;
	}

	public void updateTask(TaskStatus status) {
		TaskMonitor.updateTaskStatus(taskId, status, taskDetail);
	}

	public ActionTaskData<T> getData() {
		return data;
	}
	
	public EdgeWebServiceImpl getWebService(){
		return webService;
	}
	
	private long generateTaskBeginLog(){
		if(parameter == null)
			return 0;
		
		String message = "";
		String logWrapperdMessage="";
		
		if (Module.UpdateMutipleNode == parameter.getModule()) {
			message = EdgeCMWebServiceMessages.getMessage("updateMutipleNodeStart", parameter.getEntityIds().size());
			logWrapperdMessage=EdgeCMWebServiceMessages.getMessage("updateMultiNodes_Log",message);
			
		}else if (Module.ManageMultipleNodes == parameter.getModule()) {
			message = EdgeCMWebServiceMessages.getMessage("manageMultiNodesJobStarted", parameter.getEntityIds().size());
			logWrapperdMessage=EdgeCMWebServiceMessages.getMessage("manageMultiNodes_Log",message);
			
		}else if (Module.SubmitD2DJob == parameter.getModule()) {
			
			message = EdgeCMWebServiceMessages.getMessage("submitMultiBackupJobStart", parameter.getEntityIds().size());
			logWrapperdMessage=EdgeCMWebServiceMessages.getMessage("submitMultiBackupJob_Log",message);
		}else if (Module.SendRegistrationEmails == parameter.getModule()) {
			
			message = EdgeCMWebServiceMessages.getMessage("sendRegistrationEmailsJobStarted", parameter.getEntityIds().size());
			logWrapperdMessage=EdgeCMWebServiceMessages.getMessage("sendRegistrationEmails_Log",message);
		}
		return generateLog(Severity.Information, parameter.getModule(),null, logWrapperdMessage);
	}
	
	private long generateTaskFinishLog(){
		if(parameter == null)
			return 0;
		long totalSize = parameter.getEntityIds().size(); 
		long successSum = data.getSuccessfullEntities().size();
		String message = "";
		String logWrapperdMessage="";
		
		if(Module.UpdateMutipleNode == parameter.getModule()){
			if (totalSize == successSum) {
				message = EdgeCMWebServiceMessages.getMessage("updateMutipleNodeSumAllSuc", totalSize, successSum);
			} else if (successSum == 0) {
				message = EdgeCMWebServiceMessages.getMessage("updateMutipleNodeSumAllFailed", totalSize, totalSize-successSum);
			} else {	
				message = EdgeCMWebServiceMessages.getMessage("updateMutipleNodeSum", totalSize, successSum, totalSize-successSum);
			}
			logWrapperdMessage = EdgeCMWebServiceMessages.getMessage("updateMultiNodes_Log",message);
		}else if (Module.ManageMultipleNodes == parameter.getModule()) {
			if (totalSize == successSum) {
				message = EdgeCMWebServiceMessages.getMessage("manageMutipleNodeSumAllSuc", totalSize, successSum);
			} else if (successSum == 0) {
				message = EdgeCMWebServiceMessages.getMessage("manageMutipleNodeSumAllFailed", totalSize, totalSize-successSum);
			} else {	
				message = EdgeCMWebServiceMessages.getMessage("manageMutipleNodeSum", totalSize, successSum, totalSize-successSum);
			}
			logWrapperdMessage = EdgeCMWebServiceMessages.getMessage("manageMultiNodes_Log",message);
		}else if (Module.SubmitD2DJob == parameter.getModule()) {
			if (totalSize == successSum) {
				message = EdgeCMWebServiceMessages.getMessage("submitMutipleBackupJobSumAllSuc", totalSize, successSum);
			} else if (successSum == 0) {
				message = EdgeCMWebServiceMessages.getMessage("submitMutipleBackupJobSumAllFailed", totalSize, totalSize-successSum);
			} else {	
				message = EdgeCMWebServiceMessages.getMessage("submitMutipleBackupJobSum", totalSize, successSum, totalSize-successSum);
			}
			logWrapperdMessage = EdgeCMWebServiceMessages.getMessage("submitMultiBackupJob_Log",message);
		}else if (Module.SendRegistrationEmails == parameter.getModule()) {
			if (totalSize == successSum) {
				message = EdgeCMWebServiceMessages.getMessage("sendRegistrationEmailsSumAllSuc", totalSize, successSum);
			} else if (successSum == 0) {
				message = EdgeCMWebServiceMessages.getMessage("sendRegistrationEmailsSumAllFailed", totalSize, totalSize-successSum);
			} else {	
				message = EdgeCMWebServiceMessages.getMessage("sendRegistrationEmailsSum", totalSize, successSum, totalSize-successSum);
			}
			logWrapperdMessage = EdgeCMWebServiceMessages.getMessage("sendRegistrationEmails_Log",message);
		}
		return generateLog(Severity.Information, parameter.getModule(),null,logWrapperdMessage);
	}
	
	private long generateLog(Severity severity, Module module, Node node, String message) {
		if(StringUtil.isEmptyOrNull(message))
			return 0;
		ActivityLog log = new ActivityLog();
		log.setNodeName(node!=null?node.getHostname():"");
		log.setModule(module);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(message);
		try {
			return logService.addLog(log);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 0;
	}
}
