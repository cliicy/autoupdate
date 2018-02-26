package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common;

public interface IASBUServerInfo {
	int get_branchID();
	String get_serverName();
	String get_URL();
	String get_syncPath();
	int get_serverType();
	int get_retryTimes();
	int get_retryInterval();
}
