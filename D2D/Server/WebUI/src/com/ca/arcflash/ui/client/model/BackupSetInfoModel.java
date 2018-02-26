package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupSetInfoModel extends BaseModelData {
	private static final long serialVersionUID = -9119042813583278743L;

	public RecoveryPointModel startRecoveryPoint;
	public RecoveryPointModel endRecoveryPoint;
	
	public Long getTotalSize() {
		return (Long)get("size");
	}
	
	public void setTotalSize(Long size) {
		set("size", size);
	}
	
	public Integer getCount() {
		return (Integer)get("count");
	}
	
	public void setCount(Integer count) {
		set("count", count);
	}
}
