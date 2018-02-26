package com.ca.arcflash.ui.client.common;

public interface IRefreshable {	
	//back up complete
	int CS_BACKUP_FINISHED = 0;
	//restore complete
	int CS_RESTORE_FINISHED = 1;
	//archive complete
	int CS_ARCHIVE_FINISHED = 2;
	//export complete
	int CS_COPY_FINISHED = 3;
	//D2D settings changed
	int CS_CONFIG_CHANGED = 4;
	
	int CS_D2D_UPDATE = 5;
	
	int CS_FSCATALOG_FINISHED = 6;
	
	int CS_MERGEJOB_FINISHED = 7;
	
	int CS_MERGEJOB_STARTED = 8;
	
	public void refresh(Object data);
	
	/**
	 * Refresh different part of depending on the change source
	 * @param data: the data used for refresh
	 * @param changeSource: the source that causes this change, it's defined as above
	 */
	public void refresh(Object data, int changeSource);
}
