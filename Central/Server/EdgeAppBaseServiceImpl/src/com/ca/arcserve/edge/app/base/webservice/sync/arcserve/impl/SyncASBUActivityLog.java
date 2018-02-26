package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.util.Date;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class SyncASBUActivityLog {
	private IActivityLogService _iSyncActivityLog = new ActivityLogServiceImpl();
	private ActivityLog _log = new ActivityLog();
	private long _jobid = 0;

	public SyncASBUActivityLog() {
		_log.setModule(Module.ArcserveSync);
	}

	public void set_jobid(long jobid) {
		_jobid = jobid;
	}
	
	public static SyncASBUActivityLog GetInstance() {
		return new SyncASBUActivityLog();
	}
	
	public static SyncASBUActivityLog GetInstance(ASBUJobInfo jobinfo){
		SyncASBUActivityLog instance = new SyncASBUActivityLog();
		if(jobinfo == null)
			instance.set_jobid(0);
		else
			instance.set_jobid(jobinfo.getJobid());
		return instance;
	}

	private void WriteLog(String nodeName, String message) {
		//synchronized (this) {
			_log.setNodeName(nodeName);
			_log.setTime(new Date(System.currentTimeMillis()));
			_log.setMessage(message);
			_log.setJobId(_jobid);
			try {
				_iSyncActivityLog.addLog(_log);
			} catch (EdgeServiceFault e) {
				// TODO Auto-generated catch block
				ConfigurationOperator.debugMessage(e.getMessage());
			}
		//}
	}

	public void WriteError(String nodeName, String message) {
		_log.setSeverity(Severity.Error);
		WriteLog(nodeName, message);
	}

	public void WriteInformation(String nodeName, String message) {
		_log.setSeverity(Severity.Information);
		WriteLog(nodeName, message);
	}

	public void WriteWarning(String nodeName, String message) {
		_log.setSeverity(Severity.Warning);
		WriteLog(nodeName, message);
	}

}
