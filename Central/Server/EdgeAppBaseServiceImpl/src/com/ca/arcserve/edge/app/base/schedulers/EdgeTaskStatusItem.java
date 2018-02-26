package com.ca.arcserve.edge.app.base.schedulers;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTaskFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class EdgeTaskStatusItem implements IEdgeTaskItem {

	private EdgeTaskStatus status       = null;
	private String         description  = null;
	private String         taskName     = null;
	private long           jobid        = 0;


	private IActivityLogService _iSyncActivityLog = new ActivityLogServiceImpl();
	private ActivityLog         activityLog              = new ActivityLog();
	private static Logger logger = Logger.getLogger( EdgeTaskStatusItem.class );
	public EdgeTaskStatus getStatus() {
		return status;
	}

	public void setStatus(EdgeTaskStatus status) {
		this.status = status;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public long getJobID() {
		return jobid;
	}
	
	public void setJobID(long id) {
		jobid = id;
	}
	
	public EdgeTaskStatusItem() {
		activityLog.setModule(Module.Common);
	}

	@Override
	public void run() {
		try {
		
			activityLog.setSeverity(Severity.Information);
			switch(status) {
			case Task_Start:
				if (taskName == EdgeTaskFactory.EDGE_TASK_SRM) {
					logger.info("srm job start at time: " + new Date().toString() );
				}
				break;
	
			case Task_Finish:
				EdgeTask task = EdgeTaskFactory.getInstance().getTask(taskName);
				if (task != null) {
	
					while (!(task.getWaitingQueueSize()==0 && task.getExecuteQueueSize() == 1)) {
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							logger.error("interrpution error happens when wait for srm exec thread!", e );
						}
					}
	
					if (taskName == EdgeTaskFactory.EDGE_TASK_SRM) {
						//edao.spsrmedgecopyNode2HostFromTempTable(); 
						logger.info("srm job successful at time: " + new Date().toString() );
					}
				}
				break;
	
			case Task_Canncel:
				break;
			case Task_Error:
				activityLog.setSeverity(Severity.Error);
				break;
			default:
				break;
			}
	
			// Add the activity log in here
	
			activityLog.setTime(new Date(System.currentTimeMillis()));
			activityLog.setMessage(description);
			activityLog.setJobId(jobid);
			try {
				_iSyncActivityLog.addLog(activityLog);
			} catch (EdgeServiceFault e) {
				logger.error("error happens when record srm activity log", e );
			}
		}
		catch(Exception e){
			logger.error("error happens when do CPM SRM!", e );
		}
	}
}
