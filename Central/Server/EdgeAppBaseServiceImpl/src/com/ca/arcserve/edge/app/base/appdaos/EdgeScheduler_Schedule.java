package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

public class EdgeScheduler_Schedule {
	private int ID;
	private String Name;
	private String Description;
	
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
	private Date LastActionTime;
	private int ActedTimes;
	private int UserID;
	private Date CreatedAt;
	private Date LastModifiedAt;
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
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
	public Date getLastActionTime() {
		return LastActionTime;
	}
	public void setLastActionTime(Date lastActionTime) {
		LastActionTime = lastActionTime;
	}
	public int getActedTimes() {
		return ActedTimes;
	}
	public void setActedTimes(int actedTimes) {
		ActedTimes = actedTimes;
	}
	public int getUserID() {
		return UserID;
	}
	public void setUserID(int userID) {
		UserID = userID;
	}
	public Date getCreatedAt() {
		return CreatedAt;
	}
	public void setCreatedAt(Date createdAt) {
		CreatedAt = createdAt;
	}
	public Date getLastModifiedAt() {
		return LastModifiedAt;
	}
	public void setLastModifiedAt(Date lastModifiedAt) {
		LastModifiedAt = lastModifiedAt;
	}
}
