package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RecentBackupModel extends BaseModelData{

	private static final long serialVersionUID = -8377601091857317952L;
	public String getName() {
		return get("name");
	}
	public void setName(String name) {
		set("name",name);
	}
	public Integer getType() {
		return (Integer)get("type");
	}
	public void setType(Integer type) {
		set("type",type);
	}
	public Integer getStatus() {
		return (Integer)get("status");
	}
	public void setStatus(Integer status) {
		set("status",status);
	}
	public Date getTime() {
		return (Date)get("time");
	}
	public void setTime(Date time) {
		set("time",time);
	}
	
	public void setTimeZoneOffset(Long offset) {
		set("offset", offset);
	}
	
	public Long getTimeZoneOffset() {
		return get("offset");
	}
}
