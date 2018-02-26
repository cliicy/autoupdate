package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.util.Date;
import java.util.Formatter;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class D2DBaseXmlParser {
	private static final Logger logger = Logger.getLogger(D2DBaseXmlParser.class);

	long 	edgeTaskId = 0;
	int 	branchid = 0;
	String 	hostName = "";
	
	public void setTaskId(long id) {
		edgeTaskId = id;
	}
	
	public void setBranchid(int branchid) {
		this.branchid = branchid;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public void writeActivityLog(Severity severity, int msgId) {
		 writeActivityLog(hostName, edgeTaskId, severity, msgId);
	}
	
	public static void writeActivityLog(String HostName, long taskId, Severity severity, int msgId) {
		IActivityLogService activityLogSvc = new ActivityLogServiceImpl();
		ActivityLog log = new ActivityLog();
		Date time = new Date();

		String message = D2DSyncMessage.GetMessage(msgId);
		
		log.setJobId(taskId);
		log.setMessage(message);
		log.setModule(Module.D2DSync);
		log.setNodeName(HostName);
		log.setSeverity(severity);
		log.setTime(time);

		try {
			activityLogSvc.addLog(log);
		} catch (EdgeServiceFault e) {
			// TODO Auto-generated catch block
			logger.debug(e);
			logger.debug("write activity log failed!");
		}
	}
	
	public static void writeActivityLog(String HostName, long taskId, Severity severity, int msgId, String s) {
		IActivityLogService activityLogSvc = new ActivityLogServiceImpl();
		ActivityLog log = new ActivityLog();
		Date time = new Date();

		String message = D2DSyncMessage.GetMessage(msgId);
		
		log.setJobId(taskId);
		
		Formatter fmt = new Formatter();
		fmt.format(message, s);
		log.setMessage(fmt.toString());
		
		log.setModule(Module.D2DSync);
		log.setNodeName(HostName);
		log.setSeverity(severity);
		log.setTime(time);

		try {
			activityLogSvc.addLog(log);
		} catch (EdgeServiceFault e) {
			// TODO Auto-generated catch block
			logger.debug(e);
			logger.debug("write activity log failed!");
		}
	}
    
    public static Date UTCToLocal(Date utcDate) {
    	java.util.Calendar cal = java.util.Calendar.getInstance();
    	cal.setTime(utcDate);
    	int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
    	int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
    	cal.add(java.util.Calendar.MILLISECOND, (zoneOffset + dstOffset));
    	return cal.getTime();
    }
}
