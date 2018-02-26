package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData.RepeatMethodData;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData.RepeatMethodType;


public class NodeDeleteSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2995743628159901268L;

	private ScheduleData schedule;	
	private int concurrentThreadCount;
	private int timeOut;
	private int retryTimes;
	private int retryInterval;
	private NodeDeleteSettingStatus status;	

	public boolean contentEqualsOther(Object obj) {
		if(obj == null) return false;
	
		if(obj instanceof NodeDeleteSetting) {
			NodeDeleteSetting temp = (NodeDeleteSetting)obj;
			
			if(this.status == temp.status) {
				if(this.status != NodeDeleteSettingStatus.disabled) {
					return this.schedule.contentEqualsOther(temp.schedule)
					&& this.concurrentThreadCount == temp.concurrentThreadCount
					&& this.timeOut == temp.timeOut
					&& this.retryTimes == temp.retryTimes
					&& this.retryInterval == temp.retryInterval;
				}
				else return true;
			} else return false;
		}
		else return false;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public NodeDeleteSetting() {
		concurrentThreadCount = 40;
		timeOut = 300;
		retryTimes = 5;
		retryInterval = 300;
		status = NodeDeleteSettingStatus.disabled;
		schedule = new ScheduleData();
//		RepeatMethodData repeatMethodData = new RepeatMethodData();
//		repeatMethodData.setRepeatMethodType(RepeatMethodType.everyNumberOfDays);
//		repeatMethodData.setEveryDays(1);
//		schedule.setScheduleTimeStr("0:0");
//		schedule.setRepeatMethodData(repeatMethodData);
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
	public NodeDeleteSettingStatus getStatus() {
		return status;
	}
	public void setStatus(NodeDeleteSettingStatus status) {
		this.status = status;
	}
	
	public static enum NodeDeleteSettingStatus {
		enabled(1), disabled(2);
		
		private int value;
		private NodeDeleteSettingStatus(int value) {
			this.value = value;
		}		
		public int getValue() {
			return value;
		}
		
		public static NodeDeleteSettingStatus parseInt(int value) {
			NodeDeleteSettingStatus type;
			switch (value) {
			case 1:
				type = NodeDeleteSettingStatus.enabled;
				break;
			case 2:
				type = NodeDeleteSettingStatus.disabled;
				break;		
			default:
				type = NodeDeleteSettingStatus.enabled;
				break;
			}

			return type;
		}	
	}		
}
