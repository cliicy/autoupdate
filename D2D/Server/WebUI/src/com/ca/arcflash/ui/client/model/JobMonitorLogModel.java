package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class JobMonitorLogModel extends BaseModelData {

	public Long getId()
	{
		return (Long)get("ID");
	}
	public void setId(Long id)
	{
		set("ID", id);
	}
	
	public String getJobId()
	{
		return (String)get("JobId");
	}
	public void setJobId(String JobId)
	{
		set("JobId", JobId);
	}
	
	public String getType()
	{
		return (String)get("Type");
	}
	public void setType(String type)
	{
		set("Type", type);
	}
	
	public String getDateTime()
	{
		return (String)get("DateTime");
	}
	public void setDateTime(String DateTime)
	{
		set("DateTime", DateTime);
	}
	
	public String getMessage()
	{
		return (String)get("Message");
	}
	public void setMessage(String message)
	{
		set("Message", message);
	}
}
