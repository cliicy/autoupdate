/**
 * @(#)PolicyLogProxy.java 7/26/2011
 * Copyright 2011 CA Technologies, Inc. All rights reserved. 
 */
package com.ca.arcserve.edge.app.rps.webservice.policy;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

/**
 * Class <code> PolicyLogProxy </code> provides save policy operation message to activity log
 * @author lijbi02
 * @version 1.0
 * @since JDK1.6
 *
 */
public class PolicyLogProxy {

	private static PolicyLogProxy instance = new PolicyLogProxy();
	private static Logger debugLog = Logger.getLogger(PolicyLogProxy.class);

	public static PolicyLogProxy getInstance() {
		return instance ;
	}
	
	private PolicyLogProxy() {
		 
	}
	
	public void addLog(Severity severity, Module module, String host, String message) {
		IActivityLogService logService = new ActivityLogServiceImpl();
		ActivityLog log = new ActivityLog();
		log.setJobId(0);
		log.setSeverity(severity);
		log.setModule(module);
		log.setNodeName(host);
		log.setMessage(message);
		log.setTime(new Date(System.currentTimeMillis()));
		try {
			logService.addLog(log);
		} catch (EdgeServiceFault e) {
			debugLog.debug(e.getMessage());
		}
	}

	public void addErrorLog(Module module, String host,	String message) {
		addLog(Severity.Error, module, host, message);
	}


	public void addSuccessLog(Module module, String host, String message) {
		addLog(Severity.Information, module, host, message);
	}

}
