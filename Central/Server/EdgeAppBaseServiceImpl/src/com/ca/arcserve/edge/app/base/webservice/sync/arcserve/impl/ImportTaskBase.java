package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

public interface ImportTaskBase {
	void run();
	void SetConfiguration(ASBUJobInfo jobinfo, SyncFileQueueItem item);
}
