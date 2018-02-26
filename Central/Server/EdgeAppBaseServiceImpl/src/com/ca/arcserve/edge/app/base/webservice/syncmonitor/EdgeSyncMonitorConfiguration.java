package com.ca.arcserve.edge.app.base.webservice.syncmonitor;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;

public class EdgeSyncMonitorConfiguration implements IEdgeSyncMonitorConfiguration {
	public static final String regVerifyPeriodName = "SyncVerifyPeriod";
	public static final String regMonitorIntervalName = "SyncMonInterval";
	public static final String regRefreshPeriodName = "SyncMonRefreshPeriod";
	public static final String regCleanupPeriodName = "AlertCleanupPeriod";
	public static final String regEnableSyncAlertEmailSend = "EnableSyncAlertEmailSend";
	
	public static final int DEF_VERIFY_PERIOD = 3; // default the period is 3 days
	public static final int DEF_MONITOR_INTERVAL = 3600; // default alert monitor schedule interval is 1 hour
	public static final int DEF_REFRESH_PERIOD = 1; // one hour by default
	public static final int DEF_CLEANUP_PERIOD = 1; // default the cleanup alert record period is 1 years
	public static final boolean DEF_FLAG_SYNC_ALERT_EMAIL_SEND = true; // default the sync alert raised will send an email
	
	private static Logger _log = Logger.getLogger(EdgeSyncMonitorConfiguration.class);
	
	private Integer verifyPeriod = DEF_VERIFY_PERIOD; 
	private Integer monitorInterval = DEF_MONITOR_INTERVAL;  
	private Integer refreshPeriod = DEF_REFRESH_PERIOD;
	private Integer cleanupPeriod = DEF_CLEANUP_PERIOD;
	private boolean syncAlertEmailSendFlag = DEF_FLAG_SYNC_ALERT_EMAIL_SEND;
	
	
	public EdgeSyncMonitorConfiguration() {
		load();
	}
	
	
	private void load() {
		
		verifyPeriod    		= DEF_VERIFY_PERIOD;
		monitorInterval 		= DEF_MONITOR_INTERVAL;
		refreshPeriod          	= DEF_REFRESH_PERIOD;
		cleanupPeriod 			= DEF_CLEANUP_PERIOD;
		syncAlertEmailSendFlag 	= DEF_FLAG_SYNC_ALERT_EMAIL_SEND;
		
		try {
			// Load alert configuration from registry
			if (hasRegistryKey()) {
				 if (!readRegistryKey()) {
					 // Log it
					 _log.debug("[EdgeSyncMonitorConfiguration]read sync monitor verify period setting failed.");
				 } else {
					 return;
				 }
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
		} 
	}

	@Override
	public Integer getVerifyPeriod() {
		return verifyPeriod;
	}

	@Override
	public Integer getMonitorInterval() {
		return monitorInterval;
	}
	
	@Override
	public Integer getRefreshPeriod() {
		return refreshPeriod;
	}
	
	@Override
	public Integer getCleanupPeriod() {
		return cleanupPeriod;
	}
	
	@Override
	public boolean getSyncAlertEmailSendFlag() {
		return syncAlertEmailSendFlag;
	}

	private boolean hasRegistryKey() {
		
		String periodStr = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, regVerifyPeriodName);
		if (periodStr == null) {
			_log.debug("[EdgeSyncMonitorConfiguration] no regVerifyPeriodName");
		}
		
		String scheduleIntervalStr = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, regMonitorIntervalName);
		if (scheduleIntervalStr == null) {
			_log.debug("[EdgeSyncMonitorConfiguration] no regMonitorIntervalName");
		}
		
		String refreshPeriodStr = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, regRefreshPeriodName);
		if (refreshPeriodStr == null) {
			_log.debug("[EdgeSyncMonitorConfiguration] no regRefreshPeriodName");
		}
		
		String cleanupPeriodStr = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, regCleanupPeriodName);
		if (cleanupPeriodStr == null) {
			_log.debug("[EdgeSyncMonitorConfiguration] no regCleanupPeriodName");
		}
		
		String syncAlertEmailSendFlagStr = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, regEnableSyncAlertEmailSend);
		if (syncAlertEmailSendFlagStr == null) {
			_log.debug("[EdgeSyncMonitorConfiguration] no regEnableSyncAlertEmailSend");
		}
		
		return (periodStr != null || 
				scheduleIntervalStr != null ||
				refreshPeriodStr != null ||
				cleanupPeriodStr != null ||
				syncAlertEmailSendFlagStr != null);
	}
	
	
	private boolean readRegistryKey() {
		String periodStr = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, regVerifyPeriodName);
		if (periodStr != null) {
			if (periodStr.startsWith("0.")) {
				verifyPeriod = (int)(Float.valueOf(periodStr) * 24 * 3600);
			} else {
				verifyPeriod = Integer.valueOf(periodStr);
			}
		}
		
		String scheduleIntervalStr = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, regMonitorIntervalName);
		if (scheduleIntervalStr != null) {
			monitorInterval = Integer.valueOf(scheduleIntervalStr);
		}
		
		String refreshPeriodStr = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, regRefreshPeriodName);
		if (refreshPeriodStr != null) {
			refreshPeriod = Integer.valueOf(refreshPeriodStr);
		}
		
		String cleanupPeriodStr = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, regCleanupPeriodName);
		if (cleanupPeriodStr != null) {
			cleanupPeriod = Integer.valueOf(cleanupPeriodStr);
		}
		
		String syncAlertEmailSendFlagStr = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, regEnableSyncAlertEmailSend);
		if (syncAlertEmailSendFlagStr != null) {
			syncAlertEmailSendFlag = Boolean.valueOf(syncAlertEmailSendFlagStr);
		}
		
		return (periodStr != null || 
				scheduleIntervalStr != null ||
				refreshPeriodStr != null ||
				cleanupPeriodStr != null ||
				syncAlertEmailSendFlagStr != null);
	}


}
