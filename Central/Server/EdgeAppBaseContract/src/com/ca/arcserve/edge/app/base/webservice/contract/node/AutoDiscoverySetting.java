package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.node.ASBUSetting.ASBUSettingStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;

public class AutoDiscoverySetting  implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 635778345518363355L;
	private ASBUSettingStatus status = ASBUSettingStatus.enabled;
	private ASBUSettingStatus aDStatus = ASBUSettingStatus.enabled;
	private List<DiscoveryOption> options;
	private ScheduleData schedule;
	public ASBUSettingStatus getStatus() {
		return status;
	}
	public void setStatus(ASBUSettingStatus status) {
		this.status = status;
	}

	public ASBUSettingStatus getaDStatus() {
		return aDStatus;
	}
	public void setaDStatus(ASBUSettingStatus aDStatus) {
		this.aDStatus = aDStatus;
	}
	public List<DiscoveryOption> getOptions() {
		return options;
	}
	public void setOptions(List<DiscoveryOption> options) {
		this.options = options;
	}
	public ScheduleData getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleData schedule) {
		this.schedule = schedule;
	}
	public DiscoveryOption getADOption(){
		return options.get(0);
	}
	public AutoDiscoverySetting() {
		schedule = new ScheduleData();
		String str = "23:59";
		Date date = new Date();
		date.setHours(Integer.valueOf(str.split(":")[0]));
		date.setMinutes(Integer.valueOf(str.split(":")[1]));
		schedule.setScheduleTime(date);
		
		options = new ArrayList<DiscoveryOption>(2);
		DiscoveryOption adO = new DiscoveryOption();
		adO.setUserName("domain\\username");
		adO.setPassword("password");
		options.add(adO);
	}

	public boolean contentEqualsOther(Object obj) {
		if(obj == null) return false;
		
		if(obj instanceof AutoDiscoverySetting) {
			AutoDiscoverySetting temp = (AutoDiscoverySetting)obj;
			
//			if(this.status == temp.status) {
//				if(this.status != ASBUSettingStatus.disabled) {
//					return this.aDStatus == temp.aDStatus
//						&& this.optionsEquals(temp.options)
//						&& this.scheduleDataEquals(temp.schedule);
//				} 
//				else
//					return true;
//			}
//			else {
//				return false;
//			}
			
			return this.schedule.contentEqualsOther(temp.schedule)
			&& this.aDStatus == temp.aDStatus
			&& this.optionsEquals(temp.options)
			&& this.status == temp.status;
		}
		else return false;
	}
	
	private boolean scheduleDataEquals(ScheduleData right) {
		if(this.schedule == null && right == null) return true;
		
		if(this.schedule != null && right != null) {
			return this.schedule.contentEqualsOther(right);
		}
		else {
			return false;
		}
	}
	
	private boolean optionsEquals(List<DiscoveryOption> right) {
		if(this.options == null && right == null)
			return true;
		
		if(this.options != null && right != null) {
			if(this.options.size() != right.size())
				return false;
			
			DiscoveryOption op1 = this.options.get(0);
			DiscoveryOption op2 = right.get(0);
			
			if(op1 == null && op2 == null)
				return true;
			else if(op1 != null && op2 != null) {
				return this.stringsAreEqualIgnoreCase(op1.getUserName(), op2.getUserName())
				&& this.stringsAreEqualIgnoreCase(op1.getPassword(), op2.getPassword());
			}
			else
				return false;
		} else return false;
	}
	
	public boolean stringsAreEqualIgnoreCase(String source, String dest) {
		if(source==null){
			if(dest ==null) return true;
			else return false;
		}else
			return source.equalsIgnoreCase(dest);
	}
	
	public static enum SettingType{
		AD, ESX, HYPERV;
		public static SettingType parseInt(int value) {
			switch (value) {
			case 0:
				return AD;
			case 1:
				return ESX;
			case 2:
				return HYPERV;
			default:
				return AD;
			}
		}
	}
}
