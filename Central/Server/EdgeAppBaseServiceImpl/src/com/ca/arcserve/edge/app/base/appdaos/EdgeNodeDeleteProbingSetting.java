package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;
public class EdgeNodeDeleteProbingSetting {
	private String ProbeFilter;
	private int ThreadCount;
	private int Timeout;
	private int RetryTimes;
	private int RetryInterval;
	private int Status;
	private int scheduleID;	
	private String scheduleName;
	private String scheduleDescription;
	/*
	 * Action type. This can be one of the following values:
	 * 	1: Email reports
		This field is reserved for later extension.
	 */
	private int ActionType;
	/**
	 * Schedule type. This can be one of the following values:
		1: Every several days
		2: Every selected weekday
		3: Day of every month
		4: every selected hours
		5: every selected minutes
		6: every selected seconds
	 */
	private int ScheduleType;
	private String ScheduleParam;
	private Date ActionTime;
	private Date RepeatFrom;
	private int RepeatType;
	private String RepeatParam;		

	public String getProbeFilter() {
		return ProbeFilter;
	}

	public void setProbeFilter(String probeFilter) {
		ProbeFilter = probeFilter;
	}

	public int getThreadCount() {
		return ThreadCount;
	}

	public void setThreadCount(int threadCount) {
		ThreadCount = threadCount;
	}

	public int getTimeout() {
		return Timeout;
	}

	public void setTimeout(int timeout) {
		Timeout = timeout;
	}

	public int getRetryTimes() {
		return RetryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		RetryTimes = retryTimes;
	}

	public int getRetryInterval() {
		return RetryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		RetryInterval = retryInterval;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}
	
	public int getScheduleID() {
		return scheduleID;
	}

	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}
	
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String name) {
		scheduleName = name;
	}
	public String getScheduleDescription() {
		return scheduleDescription;
	}
	public void setScheduleDescription(String description) {
		scheduleDescription = description;
	}
	/**
	 * Action type. This can be one of the following values:
		1: Email reports
		This field is reserved for later extension.

	 * @return
	 */
	public int getActionType() {
		return ActionType;
	}
	/**
	 * 	 * Action type. This can be one of the following values:
		1: Email reports
		This field is reserved for later extension.
	 * @param actionType
	 */
	public void setActionType(int actionType) {
		ActionType = actionType;
	}
	/**
	 * 	Schedule type. This can be one of the following values:
		1: Every several days
		2: Every selected weekday
		3: Day of every month
		4: every selected hours
		5: every selected minutes
		6: every selected seconds
	 * @return
	 */
	public int getScheduleType() {
		return ScheduleType;
	}
	/**
	 * 	 * 	Schedule type. This can be one of the following values:
		1: Every several days
		2: Every selected weekday
		3: Day of every month
		4: every selected hours
		5: every selected minutes
		6: every selected seconds
	 * @param scheduleType
	 */
	public void setScheduleType(int scheduleType) {
		ScheduleType = scheduleType;
	}
	public String getScheduleParam() {
		return ScheduleParam;
	}
	public void setScheduleParam(String scheduleParam) {
		ScheduleParam = scheduleParam;
	}
	/**
	 * When to run the action in a day. Only the time part makes scenes.
	 * @return
	 */
	public Date getActionTime() {
		return ActionTime;
	}
	/**
	 * When to run the action in a day. Only the time part makes scenes.
	 * @param actionTime
	 */
	public void setActionTime(Date actionTime) {
		ActionTime = actionTime;
	}
	/**
	 * From which date, the schedule will take effect.
	 * @return
	 */
	public Date getRepeatFrom() {
		return RepeatFrom;
	}
	/**
	 * From which date, the schedule will take effect.
	 * @param repeatFrom
	 */
	public void setRepeatFrom(Date repeatFrom) {
		RepeatFrom = repeatFrom;
	}
	/**
	 * Repeat type. This can be one of the following values:
		1: Forever
		2: Repeat to end date
		3: Repeat several times

	 * @return
	 */
	public int getRepeatType() {
		return RepeatType;
	}
	/**
	 * Repeat type. This can be one of the following values:
		1: Forever
		2: Repeat to end date
		3: Repeat several times

	 * @param repeatType
	 */
	public void setRepeatType(int repeatType) {
		RepeatType = repeatType;
	}
	public String getRepeatParam() {
		return RepeatParam;
	}
	public void setRepeatParam(String repeatParam) {
		RepeatParam = repeatParam;
	}	
}
