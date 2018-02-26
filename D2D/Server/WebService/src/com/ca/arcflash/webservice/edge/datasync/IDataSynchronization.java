package com.ca.arcflash.webservice.edge.datasync;

public interface IDataSynchronization {
	boolean doSync(boolean isFullSync);
	boolean isFullSyncFinished();
	void cleanFullSyncFinished();
	boolean markFullSyncFinished();
}
