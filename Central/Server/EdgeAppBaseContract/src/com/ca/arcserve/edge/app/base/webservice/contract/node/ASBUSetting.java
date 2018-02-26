package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;

public class ASBUSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4283257339174492906L;

	private int branchID;
	private String syncFilePath;
	private int retryTimes;
	private int retryInterval;
	private ASBUSettingStatus status;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private ScheduleData schedule;

	public ASBUSetting() {
		branchID = -1;
		syncFilePath = "<EdgeHome>\\ASBUSync";
		retryTimes = 5;
		retryInterval = 300;
		status = ASBUSettingStatus.useGlobalSchedule;
		schedule = new ScheduleData();
	}

	public void setBranchID(int branchID) {
		this.branchID = branchID;
	}

	public int getBranchID() {
		return branchID;
	}

	public ScheduleData getSchedule() {
		return schedule;
	}

	public void setSchedule(ScheduleData schedule) {
		this.schedule = schedule;
	}

	public String getSyncFilePath() {
		return syncFilePath;
	}

	public void setSyncFilePath(String syncFilePath) {
		this.syncFilePath = syncFilePath;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public int getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public ASBUSettingStatus getStatus() {
		return status;
	}

	public void setStatus(ASBUSettingStatus status) {
		this.status = status;
	}

	public static enum ASBUSettingStatus {
		enabled(1), disabled(2), useGlobalSchedule(3);

		private int value;

		private ASBUSettingStatus(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static ASBUSettingStatus parseInt(int value) {
			ASBUSettingStatus type;
			switch (value) {
			case 1:
				type = ASBUSettingStatus.enabled;
				break;
			case 2:
				type = ASBUSettingStatus.disabled;
				break;
			case 3:
				type = ASBUSettingStatus.useGlobalSchedule;
				break;
			default:
				type = ASBUSettingStatus.enabled;
				break;
			}

			return type;
		}
	}

	public boolean contentEqualsOther(Object obj) {
		if (obj == null)
			return false;

		if (obj instanceof ASBUSetting) {
			ASBUSetting right = (ASBUSetting) obj;

			return this.syncFilePath.equalsIgnoreCase(right.getSyncFilePath())
					&& this.retryTimes == right.getRetryTimes()
					&& this.retryInterval == right.getRetryInterval()
					&& this.status == right.getStatus()
					&& this.schedule.contentEqualsOther(right.schedule);
		}

		return false;
	}
}
