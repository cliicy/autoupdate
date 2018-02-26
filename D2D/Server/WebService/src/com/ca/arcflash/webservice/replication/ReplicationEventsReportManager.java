package com.ca.arcflash.webservice.replication;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;

public class ReplicationEventsReportManager {
	private static final Logger logger = Logger.getLogger(ReplicationEventsReportManager.class);
	private boolean isReportEvent = true;
	
	private static final String REPORT_VCM_EVENT = "VCMEventToESX";
	private static final ReplicationEventsReportManager manager = new ReplicationEventsReportManager();
	
	public static ReplicationEventsReportManager getInstance() {
		return manager;
	}
	
	private ReplicationEventsReportManager() {
		String report = CommonUtil.getStringKeyValueFromD2DRoot(REPORT_VCM_EVENT);
		if(report != null && "false".equalsIgnoreCase(report)) 
			isReportEvent = false;
	}
	
	public void logEvent(CAVirtualInfrastructureManager vSpherevmwareOBJ, String vmName, String vmuuid, String eventMsg) {
		if(!isReportEvent)
			return;
		
		try {
			vSpherevmwareOBJ.logUserEvent(vmName, vmuuid, eventMsg);
		} catch (Exception e) {
			logger.error("fails to report message '" + eventMsg + "' to vc/esx server: " + e.getMessage());
		}
	}
	
}
