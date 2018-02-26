package com.ca.arcserve.edge.app.base.webservice.contract.scheduler;

import java.io.Serializable;

public class RPMScheduleData implements Serializable  {
	public static final String RecoveryPointSyncTargetMarker = "RecoveryPointSyncTargetForTaskframework";
	private static final long serialVersionUID = 1L;
	private int startHour = 8;
	private int startMinute = 0;
	private int repeatEachDay = 1;


	public int getStartHour() {
		return startHour;
	}
	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}
	public int getStartMinute() {
		return startMinute;
	}
	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public int getRepeatEachDay() {
		return repeatEachDay;
	}
	public void setRepeatEachDay(int repeatEachDay) {
		this.repeatEachDay = repeatEachDay;
	}

}
