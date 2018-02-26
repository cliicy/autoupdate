package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VMAssureModel extends BaseModelData {
	private static final long serialVersionUID = -8412110120172375108L;
	
	public VMAssureType getType() {
		return (VMAssureType)get("type");
	}
	public void setType(VMAssureType type) {
		set("type", type);
	}
	public String getStatus() {
		return get("status");
	}
	public void setStatus(String status) {
		set("status", status);
	}
	public Date getLastUpdate() {
		return get("lastUpdate");
	}
	public void setLastUpdate(Date lastUpdate) {
		set("lastUpdate", lastUpdate);
	}
	public Date getNextSchedule() {
		return get("nextSchedule");
	}
	public void setNextSchedule(Date nextSchedule) {
		set("nextSchedule", nextSchedule);
	}
}
