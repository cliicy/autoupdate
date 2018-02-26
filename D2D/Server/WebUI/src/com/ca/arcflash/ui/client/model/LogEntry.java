package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class LogEntry extends BaseModelData{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7799944967734892952L;
	
	public LogEntry(){
		
	}
	
	public LogEntry(int type, Date time, String d2dServer, String message, long jobID) {
		set("Type", type);
	    set("Time", time);	    
	    set("D2dServer", d2dServer);
	    set("Message", message);
	    set("Icon", "<img src=''>");
	    set("JobID", jobID);
	}
	
	public Integer getType() {
		return (Integer) get("Type");
	}
	public void setType(Integer type) {
		set("Type",type);
	}
	public Date getTime() {
		return (Date) get("Time");
	}
	public void setTime(Date time) {
		set("Time",time);
	}
	public String getMessage() {
		return (String) get("Message");
	}
	public void setMessage(String message) {
		set("Message",message);
	}
	
	public String getIcon()
	{
		return (String) get("Icon");
	}
	
	public void setIcon(String icon)
	{
		set("Icon",icon);
	}
	public Long getJobID() {
		return (Long) get("JobID");
	}
	public void setJobID(Long type) {
		set("JobID",type);
	}
	
	public Long getTimeZoneOffset() {
		return (Long)get("timezoneOffset");
	}
	
	public void setTimeZoneOffset(Long offset) {
		set("timezoneOffset", offset); 
	}
	
	public String getD2dServer() {
		return (String) get("D2dServer");
	}
	
	public void setD2dServer(String D2dServer) {
		set("D2dServer",D2dServer);
	}
}
