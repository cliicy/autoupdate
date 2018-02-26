package com.ca.arcflash.ui.client.backup.advschedule;

import java.util.Date;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.MergeDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.backup.schedule.ThrottleModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.ca.arcflash.webservice.data.DayTime;
import com.extjs.gxt.ui.client.util.DateWrapper;

public class ModelConverter {
	private static FlashUIMessages uiMessages = UIContext.Messages;
	private static FlashUIConstants uiConstants = UIContext.Constants;
	
	private static DayTime convert(DayTimeModel dailyBackupTime) {
		DayTime time = new DayTime();
		
		time.setHour(dailyBackupTime.getHour());
		time.setMinute(dailyBackupTime.getMinutes());
		
		return time;
	}
	
	public static DayTimeModel convert(DayTime time) {
		DayTimeModel model = new DayTimeModel();
		
		model.setHour(time.getHour());
		model.setMinute(time.getMinute());
		
		return model;
	}	

	public static ScheduleItemModel convertToScheduleItemModel(ScheduleDetailItemModel item, int dayOfWeek) {
		ScheduleItemModel model = new ScheduleItemModel();
		model.setScheduleType(ScheduleTypeModel.RepeatJob);
		model.setDayofWeek(dayOfWeek, 1);
		model.setJobType(item.getJobType());
		model.startTimeModel = item.startTimeModel;
		model.endTimeModel = item.endTimeModel;
		model.setInterval(item.getInterval());
		model.setIntervalUnit(item.getIntervalUnit());
		model.setRepeatEnabled(item.isRepeatEnabled());
		model.setThrottle(0);
		
		if (item.isRepeatEnabled()) {
			model.setDescription(uiMessages.scheduleDescriptionBackup(
					ScheduleUtils.getJobTypeStr(item.getJobType()),
					ScheduleUtils.getScheduleRepeatStr(item.getInterval(),
							item.getIntervalUnit())));
		} else {
			model.setDescription(uiMessages.scheduleDescriptionBackupNonRepeat(
							ScheduleUtils.getJobTypeStr(model.getJobType()),
							ScheduleUtils.formatTime(model.startTimeModel)));
		}		
		
		return model;
	}
	
	public static ScheduleItemModel convertToScheduleItemModel(ThrottleModel item, int dayOfWeek){
		ScheduleItemModel model = new ScheduleItemModel();
		model.setScheduleType(ScheduleTypeModel.RepeatJob);
		model.setDayofWeek(dayOfWeek, 1);
		model.setJobType(ScheduleUtils.THROTTLE); 
		model.startTimeModel = item.startTimeModel;
		model.endTimeModel = item.endTimeModel;
		model.setInterval(0);
		model.setIntervalUnit(0);
		model.setRepeatEnabled(false);
		model.setThrottle(item.getThrottleValue());
		model.setThrottleUnit(item.getUnit());
		model.setDescription(uiMessages.scheduleDescriptionThrottle(item.getThrottleValue(), ScheduleUtils.getThrottleUnit(item.getUnit())));
		return model;
	}
	
	public static ScheduleItemModel convertToScheduleItemModel(MergeDetailItemModel item, int dayofWeek){
		ScheduleItemModel model = new ScheduleItemModel();
		model.setScheduleType(ScheduleTypeModel.RepeatJob);
		model.setDayofWeek(dayofWeek, 1);
		model.setJobType(ScheduleUtils.MERGE); 
		model.startTimeModel = item.startTimeModel;
		model.endTimeModel = item.endTimeModel;
		model.setInterval(0);
		model.setIntervalUnit(0);
		model.setRepeatEnabled(false);	
		model.setThrottle(0);
		model.setDescription(uiConstants.scheduleMergeDescription());
		return model;
	}
	
	public static ScheduleItemModel convertToScheduleItemModel(EveryDayScheduleModel daySchedule){
		ScheduleItemModel itemModel = new ScheduleItemModel();
		itemModel.setScheduleType(ScheduleTypeModel.OnceDailyBackup);
		itemModel.setJobType(daySchedule.getBkpType());
		itemModel.startTimeModel = daySchedule.getDayTime();
		itemModel.setDescription(uiMessages.scheduleDescriptionDailyBackup(ScheduleUtils.getJobTypeStr(itemModel.getJobType())));
		for (int i = 0; i < 7; i++) {
			Boolean isEnabled = daySchedule.getDayEnabled()[i];
			itemModel.setDayofWeek(i, isEnabled !=null && isEnabled?1:0);
		}			
		itemModel.setEveryDaySchedule(daySchedule);
		
		return itemModel;
	}
	
	public static ScheduleItemModel convertToScheduleItemModel(EveryWeekScheduleModel weekSchedule){
		ScheduleItemModel itemModel = new ScheduleItemModel();
		itemModel.setScheduleType(ScheduleTypeModel.OnceWeeklyBackup);
		itemModel.startTimeModel = weekSchedule.getDayTime();
		itemModel.setJobType(weekSchedule.getBkpType());
		itemModel.setDayofWeek(weekSchedule.getDayOfWeek()-1, 1);
		itemModel.setDescription(uiMessages.scheduleDescriptionWeeklyBackup(ScheduleUtils.getJobTypeStr(itemModel.getJobType())));
		itemModel.setEveryWeekSchedule(weekSchedule);
		
		return itemModel;
	}
	
	public static ScheduleItemModel convertToScheduleItemModel(EveryMonthScheduleModel monthSchedule){
		ScheduleItemModel itemModel = new ScheduleItemModel();
		itemModel.setScheduleType(ScheduleTypeModel.OnceMonthlyBackup);
		itemModel.startTimeModel = monthSchedule.getDayTime();
		itemModel.setJobType(monthSchedule.getBkpType());		
		itemModel.setDescription(uiMessages.scheduleDescriptionMonthlyBackup(ScheduleUtils.getJobTypeStr(itemModel.getJobType())));
		itemModel.setEveryMonthSchedule(monthSchedule);
		
		Date scheduleDate = calendarConvertToDate(monthSchedule);
		DateWrapper dateWrapper = new DateWrapper(scheduleDate);
		int nDayOfWeek = dateWrapper.getDayInWeek();
		
		itemModel.setDayofWeek(nDayOfWeek, ScheduleUtils.SELECTED);
		itemModel.setEveryMonthDate(String.valueOf(scheduleDate.getMonth()+1) + "/" + String.valueOf(scheduleDate.getDate()));			

		return itemModel;
	}
	
	public static ScheduleDetailItemModel convertToBackupSchedule(ScheduleItemModel model){
		ScheduleDetailItemModel item = new ScheduleDetailItemModel();		
		item.setJobType(model.getJobType());
		item.startTimeModel = model.startTimeModel;
		item.endTimeModel = model.endTimeModel;
		item.setInterval(model.getInterval());
		item.setIntervalUnit(model.getIntervalUnit());
		item.setRepeatEnabled(model.isRepeatEnabled());
		
		return item;
	}
	
	public static ThrottleModel convertToThrottle(ScheduleItemModel model){
		ThrottleModel item = new ThrottleModel();		
		item.startTimeModel = model.startTimeModel;
		item.endTimeModel = model.endTimeModel;
		item.setThrottleValue(model.getThrottle());
		item.setUnit(model.getThrottleUnit());
		return item;
	}
	
	public static MergeDetailItemModel convertToMerge(ScheduleItemModel model){
		MergeDetailItemModel item = new MergeDetailItemModel();		
		item.startTimeModel = model.startTimeModel;
		item.endTimeModel = model.endTimeModel;		
		
		return item;
	}
	
	public static Date calendarConvertToDate(EveryMonthScheduleModel monthly){
		Date curDate = new Date();
		Date destDate = new Date();

		DateWrapper dateWrapper =  new DateWrapper();
		boolean dayOfMonthEnabled = monthly.isDayOfMonthEnabled();
		boolean weekOfMonthEnabled = monthly.isWeekOfMonthEnabled();
		int dayOfMonth = monthly.getDayOfMonth();
		int weekNumOfMonth = monthly.getWeekNumOfMonth();
		int weekDayOfMonth = monthly.getWeekDayOfMonth();
		DayTimeModel startTime = monthly.getDayTime();
		
		if(dayOfMonthEnabled){
			if(dayOfMonth == 32){ // Last day
				DateWrapper lastDay = dateWrapper.getLastDateOfMonth();	
				destDate.setDate(lastDay.getDate());
			}else{				
				destDate.setDate(dayOfMonth);
			}
		}else if(weekOfMonthEnabled){
			if(weekNumOfMonth == 0){
				destDate.setDate(getLastWeekDayOfMonth(dateWrapper, weekDayOfMonth));
			}else{
				destDate.setDate(getFirstWeekDayOfMonth(dateWrapper, weekDayOfMonth));
			}
		}
		
		destDate.setHours(startTime.getHour());
		destDate.setMinutes(startTime.getMinutes());

		if(curDate.getTime() > destDate.getTime()){
			//Current date is larger than the scheduled date, recount the date
			if(dayOfMonthEnabled){
				int nMonth = curDate.getMonth()+1;
				if(nMonth > 11){
					nMonth = 0;
					int nYear = destDate.getYear()+ 1;
					destDate.setYear(nYear);
				}
				
				destDate.setMonth(nMonth);
				
				//recount the last day
				if(dayOfMonth == 32){ 
					DateWrapper cal =  new DateWrapper(destDate);								 
					destDate.setDate(cal.getLastDateOfMonth().getDate());	
				}
				
			}else if(weekOfMonthEnabled){
				int nMonth = curDate.getMonth()+1;
				Date date = destDate;
				if(nMonth > 11){
					nMonth = 0;
					int nYear = destDate.getYear()+ 1;
					date.setYear(nMonth);					
				}
				date.setMonth(nMonth);
				DateWrapper dateWrapper2 = new DateWrapper(date);				
				
				if(weekNumOfMonth == 0){
					destDate.setDate(getLastWeekDayOfMonth(dateWrapper2, weekDayOfMonth));
				}else{
					destDate.setDate(getFirstWeekDayOfMonth(dateWrapper2, weekDayOfMonth));
				}
			}	
			
		}
		
		return destDate;
	}
	
	private static int getFirstWeekDayOfMonth(DateWrapper dateWrapper, int weekDayOfMonth){		
		DateWrapper firstDayWrapper = dateWrapper.getFirstDayOfMonth();				
		int nDay = firstDayWrapper.getDate() + weekDayOfMonth - 1 - firstDayWrapper.getDayInWeek();
		if(nDay <= 0)
			nDay += 7;
				
		return nDay; 
		
	}
	
	private static int getLastWeekDayOfMonth(DateWrapper dateWrapper, int weekDayOfMonth){
		DateWrapper lastDayWrapper = dateWrapper.getLastDateOfMonth();
		int nLastdayOfWeek = lastDayWrapper.getDate() - (lastDayWrapper.getDayInWeek() - weekDayOfMonth + 1);
		if(nLastdayOfWeek > lastDayWrapper.getDate())
			nLastdayOfWeek -= 7;
		
		return nLastdayOfWeek;
	}
}
