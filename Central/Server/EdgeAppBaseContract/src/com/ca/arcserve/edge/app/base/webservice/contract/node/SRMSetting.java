package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;


public class SRMSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2995743628159901268L;

	private ScheduleData schedule;	
	private int concurrentThreadCount;
	private int timeOut;
	private int retryTimes;
	private int retryInterval;
	private SRMSettingStatus status;	

	public boolean contentEqualsOther(Object obj) {
		if(obj == null) return false;
	
		if(obj instanceof SRMSetting) {
			SRMSetting temp = (SRMSetting)obj;
			
			return this.schedule.contentEqualsOther(temp.schedule)
				&& this.concurrentThreadCount == temp.concurrentThreadCount
				&& this.timeOut == temp.timeOut
				&& this.retryTimes == temp.retryTimes
				&& this.retryInterval == temp.retryInterval
				&& this.status == temp.status;
		}
		else return false;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public SRMSetting() {
		concurrentThreadCount = 40;
		timeOut = 300;
		retryTimes = 5;
		retryInterval = 300;
		status = SRMSettingStatus.enabled;
		schedule = new ScheduleData();
	}
	
	public int getConcurrentThreadCount() {
		return concurrentThreadCount;
	}
	public void setConcurrentThreadCount(int concurrentThreadCount) {
		this.concurrentThreadCount = concurrentThreadCount;
	}
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
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
	public ScheduleData getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleData schedule) {
		this.schedule = schedule;
	}	
	public SRMSettingStatus getStatus() {
		return status;
	}
	public void setStatus(SRMSettingStatus status) {
		this.status = status;
	}
	
	public static enum SRMSettingStatus {
		enabled(1), disabled(2);
		
		private int value;
		private SRMSettingStatus(int value) {
			this.value = value;
		}		
		public int getValue() {
			return value;
		}
		
		public static SRMSettingStatus parseInt(int value) {
			SRMSettingStatus type;
			switch (value) {
			case 1:
				type = SRMSettingStatus.enabled;
				break;
			case 2:
				type = SRMSettingStatus.disabled;
				break;		
			default:
				type = SRMSettingStatus.enabled;
				break;
			}

			return type;
		}	
	}		
}
