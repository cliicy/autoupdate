package com.ca.arcserve.edge.app.base.webservice.dataSync;

import com.ca.arcflash.listener.service.event.DataSyncEventType;

public class DataSyncEventHandlerFactory {
	public static IDataSyncEventHandler getDataSyncEventHandler(DataSyncEventType eventType)
	{
		switch (eventType)
		{
		case CustomBackupJobHistoryEvent:
			return CustomBackupJobhistoryDataSyncHandler.getInstance();
		default:
			return null;
		}
	}
}
