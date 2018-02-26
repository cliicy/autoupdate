package com.ca.arcflash.webservice.util;

import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;

public class EmailContentContext {
	public long jobStatus;
	public long jobType;
	public long jobMethod;
	public int jobScheduleType;
	public long jobID;
	public String executionTime;
	public String source;
	public String destination;
	public boolean isLink;
	public ActivityLogResult result;
	public String URL;
	public String backupSize;
	public String backupStartTime;
	public boolean enableHtml;
	public String rpsName;
}
