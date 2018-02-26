package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveJobInfoModel extends BaseModelData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -658460605269074349L;
	
	public void setarchiveJobStatus(int in_archiveJobStatus) {
		set("archiveJobStatus",in_archiveJobStatus);
	}
	public int getarchiveJobStatus() {
		return (Integer)get("archiveJobStatus");
	}
	
	public void setbackupSessionPath(String in_backupSessionPath) {
		set("backupSessionPath",in_backupSessionPath);
	}
	public String getbackupSessionPath() {
		return get("backupSessionPath");
	}
	
	public void setbackupSessionId(String in_backupSessionId) {
		set("backupSessionId",in_backupSessionId);
	}
	public String getbackupSessionId() {
		return get("backupSessionId");
	}
	
	public void setArchiveDataSize(long in_archiveDataSize) {
		set("archiveDataSize",in_archiveDataSize);
	}
	public long getArchiveDataSize() {
		return (Long)get("archiveDataSize");
	}
	
	public void setCopyDataSize(long in_CopyDataSize) {
		set("copyDataSize",in_CopyDataSize);
	}
	public long getCopyDataSize() {
		return (Long)get("copyDataSize");
	}
	
	public void setlastJobDateTime(String in_lastJobDateTime) {
		set("lastJobDateTime",in_lastJobDateTime);
	}
	public String getlastJobDateTime() {
		return get("lastJobDateTime");
	}
}
