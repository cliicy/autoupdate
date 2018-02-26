package com.ca.arcserve.edge.app.base.webservice.contract.scheduler;

import java.io.Serializable;
import java.util.Date;


public class ScheduleData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 73586802108779041L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	private int scheduleID;	
	private int actedTimes;
	private String scheduleName;
	private String scheduleDescription;

	private ScheduleStatus scheduleStatus;

	// Repeat Method
	private RepeatMethodData repeatMethodData;

	// Schedule Time
	private Date scheduleTime;
	// Schedule Time String
	private String scheduleTimeStr;
	public String getScheduleTimeStr() {
		return scheduleTimeStr;
	}

	public void setScheduleTimeStr(String scheduleTimeStr) {
		this.scheduleTimeStr = scheduleTimeStr;
	}

	// Reoccurrence
	private Date startFromDate;
	private RepeatUnitlType repeatUntilType;
	private Date endDate;
	private int numberOfTimes;

	public ScheduleData() {
		// Default values for Repeat Method section
		repeatMethodData = new RepeatMethodData();
		repeatMethodData.setEveryHours(1);
		repeatMethodData
				.setRepeatMethodType(RepeatMethodType.everyNumberOfDays);
		repeatMethodData.setEveryDays(1);
		repeatMethodData.setRepeatMonday(true);
		repeatMethodData.setRepeatTuesday(true);
		repeatMethodData.setRepeatWednesday(true);
		repeatMethodData.setRepeatThursday(true);
		repeatMethodData.setRepeatFriday(true);
		repeatMethodData.setRepeatSaturday(false);
		repeatMethodData.setRepeatSunday(false);
		repeatMethodData.setFromBegin(true);
		repeatMethodData.setDayNumber(1);

		// Default values for Schedule Time section
		scheduleTime = new Date();
		scheduleTimeStr = "23:59";
		// Default values for Reoccurrence section
		startFromDate = new Date();
		repeatUntilType = RepeatUnitlType.forever;
		endDate = new Date();
		numberOfTimes = 1;
	}
	
	public ScheduleData clone() {
		ScheduleData temp = new ScheduleData();
		
		temp.scheduleID = this.scheduleID;
		temp.scheduleDescription = this.scheduleDescription == null ? null : new String(this.scheduleDescription);
		temp.actedTimes = this.actedTimes;
		temp.scheduleStatus = this.scheduleStatus == null ? null : ScheduleStatus.parseInt(this.scheduleStatus.getValue());
		temp.repeatMethodData = this.repeatMethodData.clone();
		temp.scheduleTime = new Date(this.scheduleTime.getTime());
		temp.startFromDate = new Date(this.startFromDate.getTime());
		temp.endDate = new Date(this.endDate.getTime());
		temp.repeatUntilType = this.repeatUntilType == null ? null : RepeatUnitlType.parseInt(this.repeatUntilType.getValue());
		temp.numberOfTimes = this.numberOfTimes;
		temp.scheduleTimeStr = this.scheduleTimeStr == null ? null : new String(this.scheduleTimeStr);
		temp.scheduleName = this.scheduleName == null ? null : new String(this.scheduleName);
		
		return temp;
	}

	public boolean contentEqualsOther(Object obj) {
		if(obj == null) return false;
		
		if(obj instanceof ScheduleData) {
			ScheduleData temp = (ScheduleData)obj;
			
			return this.actedTimes == temp.actedTimes
				&& this.scheduleStatus == temp.scheduleStatus
				&& this.repeatMethodData.equals(temp.repeatMethodData)
				&& this.scheduleTimeStr.equals(temp.scheduleTimeStr)
				&& this.repeatUntilType == temp.repeatUntilType
				&& this.numberOfTimes == temp.numberOfTimes;
			
		} else return false;
	}
	
	public int getActedTimes() {
		return actedTimes;
	}
	
	public void setActedTimes(int actedTimes) {
		this.actedTimes = actedTimes;
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

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getScheduleDescription() {
		return scheduleDescription;
	}

	public void setScheduleDescription(String scheduleDescription) {
		this.scheduleDescription = scheduleDescription;
	}	
	
	public ScheduleStatus getScheduleStatus() {
		return scheduleStatus;
	}

	public void setScheduleStatus(ScheduleStatus scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}
	
	public RepeatMethodData getRepeatMethodData() {
		return repeatMethodData;
	}

	public void setRepeatMethodData(RepeatMethodData repeatMethodData) {
		this.repeatMethodData = repeatMethodData;
	}

	public Date getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public Date getStartFromDate() {
		return startFromDate;
	}

	public void setStartFromDate(Date startFromDate) {
		this.startFromDate = startFromDate;
	}

	public RepeatUnitlType getRepeatUntilType() {
		return repeatUntilType;
	}

	public void setRepeatUntilType(RepeatUnitlType repeatUntilType) {
		this.repeatUntilType = repeatUntilType;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getNumberOfTimes() {
		return numberOfTimes;
	}

	public void setNumberOfTimes(int numberOfTimes) {
		this.numberOfTimes = numberOfTimes;
	}
	
	public enum RepeatMethodType {
		everyNumberOfDays(1), everySelectedDaysOfWeek(2), everySelectedDaysOfMonth(3),everyNumberOfHours(4), everyNumberOfMins(5),  everyNumberOfSecs(6);
		
		private int value;
		private RepeatMethodType(int value) {
			this.value = value;
		}	
		public int getValue() {
			return value;
		}
		
		public static RepeatMethodType parseInt(int value) {
			RepeatMethodType type;
			switch (value) {
			case 1:
				type = RepeatMethodType.everyNumberOfDays;
				break;
			case 2:
				type = RepeatMethodType.everySelectedDaysOfWeek;
				break;
			case 3:
				type = RepeatMethodType.everySelectedDaysOfMonth;
				break;
			case 4:
				type = RepeatMethodType.everyNumberOfHours;
				break;
			case 5:
				type = RepeatMethodType.everyNumberOfMins;
				break;
			case 6:
				type = RepeatMethodType.everyNumberOfSecs;
				break;
			default:
				type = RepeatMethodType.everyNumberOfDays;
				break;
			}

			return type;
		}
	}	
	
	
	public enum RepeatUnitlType {
		forever(1), endDate(2), numberOfTimes(3);
		
		private int value;
		private RepeatUnitlType(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
		
		public static RepeatUnitlType parseInt(int value) {
			RepeatUnitlType type;
			switch (value) {
			case 1:
				type = RepeatUnitlType.forever;
				break;
			case 2:
				type = RepeatUnitlType.endDate;
				break;
			case 3:
				type = RepeatUnitlType.numberOfTimes;
				break;
			default:
				type = RepeatUnitlType.forever;
				break;
			}

			return type;
		}
	}	
	
	public static enum ScheduleStatus {
		enabled(2), disabled(3), useGlobalSchedule(4);
		
		private int value;
		private ScheduleStatus(int value) {
			this.value = value;
		}		
		public int getValue() {
			return value;
		}
		
		public static ScheduleStatus parseInt(int value) {
			ScheduleStatus type;
			switch (value) {
			case 2:
				type = ScheduleStatus.enabled;
				break;
			case 3:
				type = ScheduleStatus.disabled;
				break;
			case 4:
				type = ScheduleStatus.useGlobalSchedule;
				break;
			default:
				type = ScheduleStatus.enabled;
				break;
			}

			return type;
		}	
	}		

	@Override
	public boolean equals(Object obj) {
		if(obj== null) return false;
		if(obj instanceof ScheduleData){
			ScheduleData temp = (ScheduleData)obj;
			return this.getScheduleID() == temp.getScheduleID();
		}else return false;
		
	}

	@Override
	public int hashCode() {
		return this.getScheduleID();
	}

	public static class RepeatMethodData implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8351225831491699914L;

		private RepeatMethodType repeatMethodType;

		private int everyDays;

		private boolean repeatMonday;
		private boolean repeatTuesday;
		private boolean repeatWednesday;
		private boolean repeatThursday;
		private boolean repeatFriday;
		private boolean repeatSaturday;
		private boolean repeatSunday;

		private boolean fromBegin;
		private int dayNumber;

		private int everyHours;
		private int everyMins;
		private int everySeconds;
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;

			if(obj instanceof RepeatMethodData) {
				RepeatMethodData temp = (RepeatMethodData)obj;
				
				return this.repeatMethodType == temp.repeatMethodType
					&& this.everyDays == temp.everyDays
					&& this.repeatMonday == temp.repeatMonday
					&& this.repeatTuesday == temp.repeatTuesday
					&& this.repeatWednesday == temp.repeatWednesday
					&& this.repeatThursday == temp.repeatThursday
					&& this.repeatFriday == temp.repeatFriday
					&& this.repeatSaturday == temp.repeatSaturday
					&& this.repeatSunday == temp.repeatSunday
					&& this.fromBegin == temp.fromBegin
					&& this.dayNumber == temp.dayNumber
					&& this.everyHours == temp.everyHours
					&& this.everyMins == temp.everyMins
					&& this.everySeconds == temp.everySeconds;
			}
			
			return false;
		}
		
		public RepeatMethodData clone() {
			RepeatMethodData temp = new RepeatMethodData();
			
			temp.repeatMethodType = this.repeatMethodType;
			temp.everyDays = this.everyDays;
			temp.repeatMonday = this.repeatMonday;
			temp.repeatTuesday = this.repeatTuesday;
			temp.repeatWednesday = this.repeatWednesday;
			temp.repeatThursday = this.repeatThursday;
			temp.repeatFriday = this.repeatFriday;
			temp.repeatSaturday = this.repeatSaturday;
			temp.repeatSunday = this.repeatSunday;
			temp.fromBegin = this.fromBegin;
			temp.dayNumber = this.dayNumber;
			temp.everyHours = this.everyHours;
			temp.everyMins = this.everyMins;
			temp.everySeconds = this.everySeconds;
			
			return temp;
		}
		
		public int getEveryHours() {
			return everyHours;
		}

		public void setEveryHours(int everyHours) {
			this.everyHours = everyHours;
		}

		public int getEveryMins() {
			return everyMins;
		}

		public void setEveryMins(int everyMins) {
			this.everyMins = everyMins;
		}

		public int getEverySeconds() {
			return everySeconds;
		}

		public void setEverySeconds(int everySeconds) {
			this.everySeconds = everySeconds;
		}

		public RepeatMethodData() {

		}

		public RepeatMethodType getRepeatMethodType() {
			return repeatMethodType;
		}

		public void setRepeatMethodType(RepeatMethodType repeatMethodType) {
			this.repeatMethodType = repeatMethodType;
		}

		public int getEveryDays() {
			return everyDays;
		}

		public void setEveryDays(int everyDays) {
			this.everyDays = everyDays;
		}

		public boolean isRepeatMonday() {
			return repeatMonday;
		}

		public void setRepeatMonday(boolean repeatMonday) {
			this.repeatMonday = repeatMonday;
		}

		public boolean isRepeatTuesday() {
			return repeatTuesday;
		}

		public void setRepeatTuesday(boolean repeatTuesday) {
			this.repeatTuesday = repeatTuesday;
		}

		public boolean isRepeatWednesday() {
			return repeatWednesday;
		}

		public void setRepeatWednesday(boolean repeatWednesday) {
			this.repeatWednesday = repeatWednesday;
		}

		public boolean isRepeatThursday() {
			return repeatThursday;
		}

		public void setRepeatThursday(boolean repeatThursday) {
			this.repeatThursday = repeatThursday;
		}

		public boolean isRepeatFriday() {
			return repeatFriday;
		}

		public void setRepeatFriday(boolean repeatFriday) {
			this.repeatFriday = repeatFriday;
		}

		public boolean isRepeatSaturday() {
			return repeatSaturday;
		}

		public void setRepeatSaturday(boolean repeatSaturday) {
			this.repeatSaturday = repeatSaturday;
		}

		public boolean isRepeatSunday() {
			return repeatSunday;
		}

		public void setRepeatSunday(boolean repeatSunday) {
			this.repeatSunday = repeatSunday;
		}

		public boolean isFromBegin() {
			return fromBegin;
		}

		public void setFromBegin(boolean fromBegin) {
			this.fromBegin = fromBegin;
		}

		public int getDayNumber() {
			return dayNumber;
		}

		public void setDayNumber(int dayNumber) {
			this.dayNumber = dayNumber;
		}
	}
}
