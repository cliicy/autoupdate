package com.ca.arcserve.edge.app.base.webservice.dataSync;

import com.ca.arcflash.listener.service.event.DataSyncEvent;

public interface IDataSyncEventHandler {
	int saveDataRecord(DataSyncEvent event);
}
