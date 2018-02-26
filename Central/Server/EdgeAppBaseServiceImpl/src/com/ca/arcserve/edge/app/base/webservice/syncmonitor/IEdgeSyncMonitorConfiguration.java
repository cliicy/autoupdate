package com.ca.arcserve.edge.app.base.webservice.syncmonitor;

public interface IEdgeSyncMonitorConfiguration {
	public Integer getVerifyPeriod();
	public Integer getMonitorInterval();
	public Integer getRefreshPeriod();
	public Integer getCleanupPeriod();
	public boolean getSyncAlertEmailSendFlag();
}
