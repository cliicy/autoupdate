package com.ca.arcflash.ui.client.backup.schedule;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.advschedule.ScheduleItemModel;
import com.ca.arcflash.ui.client.common.FormatUtil;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.ca.arcflash.webservice.data.DayTime;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.user.client.ui.Widget;

public class ScheduleUtils {
	public static final int Minute_Unit 	= 0;
	public static final int Hour_Unit 		= 1;
	public static final int Day_Unit	 	= 2;
	
	public static final int MB_MIN_Unit		= 0;
	public static final int Mbps_Unit		= 1;
	public static final int Kbps_Unit		= 2; 
	
	public static final int SELECTED 		= 1;
	
	
	public static final int FULL_BACKUP = BackupTypeModel.Full; //0;
	public static final int VERIFY_BACKUP = BackupTypeModel.Resync;//2;
	public static final int INC_BACKUP = BackupTypeModel.Incremental;//1;
	public static final int NA_BACKUP = BackupTypeModel.Unknown;//-1;
	
	public static final int THROTTLE = 3;
	public static final int MERGE = 4;
	
	public static final int MAX_SCHEDULE_ITEM_COUNT = 12;
	public static final int MAX_THROTTLE_ITEM_COUNT = 4;
	public static final int MAX_MERGE_ITEM_COUNT = 2;
	
	public static String getTimeRange(DayTimeModel startTime, DayTimeModel endTime){
		return UIContext.Messages.scheduleTimeRange(ScheduleUtils.formatTime(startTime), ScheduleUtils.formatTime(endTime));				
	}
	public static String getJobTypeStr(int backupType){
		String str;
		switch (backupType) {
		case FULL_BACKUP:
			str = UIContext.Constants.scheduleSimpleFullBackup();
			break;
		case VERIFY_BACKUP:
			str = UIContext.Constants.scheduleSimpleResyncBackup();
			break;
		case INC_BACKUP:
			str = UIContext.Constants.scheduleSimpleIncrementalBackup();
			break;
		default:
			str = UIContext.Constants.NA();
			break;
		}
		return str;
	}
	
	public static DayTime ConvertToDayTime(DayTimeModel dailyBackupTime) {
		DayTime time = new DayTime();
		time.setHour(dailyBackupTime.getHour());
		time.setMinute(dailyBackupTime.getMinutes());
		
		return time;
	}

	
	public static ListStore<FlashFieldSetModel> getBackupTypeModels(){
		FlashFieldSetModel model1 = new FlashFieldSetModel(UIContext.Constants.scheduleSimpleFullBackup(), FULL_BACKUP);
		FlashFieldSetModel model2 = new FlashFieldSetModel(UIContext.Constants.scheduleSimpleResyncBackup(), VERIFY_BACKUP);
		FlashFieldSetModel model3 = new FlashFieldSetModel(UIContext.Constants.scheduleSimpleIncrementalBackup(), INC_BACKUP);
		ListStore<FlashFieldSetModel> lists = new ListStore<FlashFieldSetModel>();
		lists.add(model1);
		lists.add(model2);
		lists.add(model3);
		return lists;
	
	}
	
	public static String getScheduleRepeatStr(int interval,int intervalUnit){
		String str;
		switch (intervalUnit) {
		case Minute_Unit:
			str = UIContext.Constants.scheduleLabelMinutes();
			break;
		case Hour_Unit:
			str = UIContext.Constants.scheduleLabelHours();
			break;
		default:
			str = UIContext.Constants.NA();
			break;
		}
		return interval+" "+str;
	}
	
	public static int compareDayTimeModel(DayTimeModel model1, DayTimeModel model2){
		if(model1 == null){
			return -1;
		}
		if(model2 == null){
			return 1;
		}
		
		if(model1.getHour()>model2.getHour()){
			return 1;
		}
		else if(model1.getHour()<model2.getHour()){
			return -1;
		}
		else{
			if(model1.getMinutes()>model2.getMinutes()){
				return 1;
			}
			else if(model1.getMinutes()<model2.getMinutes()){
				return -1;
			}
			else{
				return 0;
			}
		}
	}
	
	public static void showMesssageBox(String title, String msgContent, String msgType){
		MessageBox msg = new MessageBox();
		msg.setIcon(msgType);
		msg.setTitleHtml(title);
		msg.setMessage(msgContent);
		msg.setModal(true);
		Utils.setMessageBoxDebugId(msg);
		msg.show();
	}
	
	public static void showConfirmMsgBox(Listener<MessageBoxEvent> callBackup){
		MessageBox msg = new MessageBox();
		msg.setIcon(MessageBox.QUESTION);
		msg.setTitleHtml(UIContext.productNameD2D);
		msg.setButtons(Dialog.YESNO);
		msg.addCallback(callBackup);
		msg.setMessage(UIContext.Constants.scheduleCopyItemWaring());
		msg.setModal(true);
		Utils.setMessageBoxDebugId(msg);
		msg.show();
	}
	
	public static boolean is24Hours()
	{
		String fmt = UIContext.Constants.timeDateFormat();
		if( fmt.indexOf('H') > -1 || fmt.indexOf('k') > -1 )
			return true;
		return false;
	}
	
	public static int minHour()
	{
		String fmt = UIContext.Constants.timeDateFormat();
		if( fmt.indexOf('H') > -1 || fmt.indexOf('K') > -1 )
			return 0;
		else
			return 1;
	}
	
	public static int maxHour()
	{
		String fmt = UIContext.Constants.timeDateFormat();
		if( is24Hours())
		{
			if( fmt.indexOf('H') > -1 )
				return 23;
			else
				return 24;
		}
		else
		{
			if( fmt.indexOf('h') > -1 )
				return 12;
			else
				return 11;
		}
	}
	
	public static boolean isHourPrefix()
	{
		String fmt = UIContext.Constants.timeDateFormat();
		if( fmt.indexOf("HH") > -1 || fmt.indexOf("hh") > -1 ||
				fmt.indexOf("KK") > -1 || fmt.indexOf("kk") > -1 )
			return true;
		return false;
	}
	
	public static boolean isMinutePrefix()
	{
		String fmt = UIContext.Constants.timeDateFormat();
		if( fmt.indexOf("mm") > -1 )
			return true;
		return false;
	}
	
	public static String prefixZero( int val, int digit )
	{
		String str = Integer.toString(val);
		int pre = digit - str.length();
		for( int i = 0; i < pre; i++ )
			str = '0' + str;
		return str;
	}
	
	public static String formatTime(Date date){
		return FormatUtil.getShortTimeFormat().format(date);
	}
	
	public static String formatTime(int hour, int minute) {
		Date date = new Date();
		date.setMonth(12);//defect 175502
		date.setDate(1); //defect 175502, set the date as 12-1 to avoid DST time's affect. Client side would always display the time user selected.
		date.setHours(hour);
		date.setMinutes(minute);
		
		return formatTime(date);		
	}
	
	public static String formatTime(DayTimeModel model) {
		return formatTime(model.getHour(), model.getMinutes());
	}
	
	public static String getWeekDayByIndex(int index) {
		int dayofWeek = (index + 1) % 7;
		if(dayofWeek == 0){
			dayofWeek = 7;
		}
		return Utils.getDayofWeek(dayofWeek);
	}
	
	
	public static String getDayDisplayName(int nDay) {
		switch(nDay){
		case 0:
			return UIContext.Constants.weekSunday();
		case 1:
			return UIContext.Constants.weekMonday();
		case 2:
			return UIContext.Constants.weekTuesday();
		case 3:
			return UIContext.Constants.weekWednesday();
		case 4:
			return UIContext.Constants.weekThursday();
		case 5:
			return UIContext.Constants.weekFriday();
		case 6:
			return UIContext.Constants.weekSaturday();
		}
		return "";
	}
	
	
	public static boolean isSameJobWithSameSchedule(ScheduleDetailItemModel model1, 
			ScheduleDetailItemModel model2){
		if(model1.getJobType() != model2.getJobType())
			return false;
		
		if(compareDayTimeModel(model1.startTimeModel, model2.startTimeModel) != 0)
			return false;
		
		if(compareDayTimeModel(model1.endTimeModel, model2.endTimeModel) != 0)
			return false;
		
		return true;
	}
	
	public static DayTimeModel cloneDayTimeModel(DayTimeModel sourceModel){
		DayTimeModel destModel = new DayTimeModel();
		destModel.setHour(sourceModel.getHour());
		destModel.setMinute(sourceModel.getMinutes());
		return destModel;
	}
	
	public static ScheduleDetailItemModel cloneScheduleItemModel(ScheduleDetailItemModel sourceModel){
		ScheduleDetailItemModel destModel = new ScheduleDetailItemModel();
		destModel.setJobType(sourceModel.getJobType());
		destModel.setInterval(sourceModel.getInterval());
		destModel.setIntervalUnit(sourceModel.getIntervalUnit());
		destModel.startTimeModel = cloneDayTimeModel(sourceModel.startTimeModel);
		destModel.endTimeModel = cloneDayTimeModel(sourceModel.endTimeModel);
		destModel.setRepeatEnabled(sourceModel.isRepeatEnabled());
		return destModel;
	}

	public static ScheduleDetailItemModel updateScheduleItemModel(ScheduleDetailItemModel sourceModel, ScheduleDetailItemModel targetModel){
		targetModel.setJobType(sourceModel.getJobType());
		targetModel.setInterval(sourceModel.getInterval());
		targetModel.setIntervalUnit(sourceModel.getIntervalUnit());
		targetModel.startTimeModel = cloneDayTimeModel(sourceModel.startTimeModel);
		targetModel.endTimeModel = cloneDayTimeModel(sourceModel.endTimeModel);
		targetModel.setRepeatEnabled(sourceModel.isRepeatEnabled());
		return targetModel;
	}
	
	public static boolean isSameSchedule(ScheduleDetailItemModel model1,ScheduleDetailItemModel model2){
		if(model1.getJobType() != model2.getJobType())
			return false;
		
		if(compareDayTimeModel(model1.startTimeModel, model2.startTimeModel) != 0)
			return false;
		
		if(compareDayTimeModel(model1.endTimeModel, model2.endTimeModel) != 0)
			return false;
		
		if(model1.getInterval() != model2.getInterval())
			return false;
		
		if(model1.getIntervalUnit() != model2.getIntervalUnit())
			return false;
		
		if(model1.isRepeatEnabled() != model2.isRepeatEnabled())
			return false;
		
		return true;
	}
	
	public static ThrottleModel cloneThrottleItemModel(ThrottleModel sourceModel){
		ThrottleModel destModel = new ThrottleModel();
		destModel.setThrottleValue(sourceModel.getThrottleValue());
		destModel.startTimeModel = cloneDayTimeModel(sourceModel.startTimeModel);
		destModel.endTimeModel = cloneDayTimeModel(sourceModel.endTimeModel);
		return destModel;
	}
	
	public static MergeDetailItemModel cloneMergeItemModel(MergeDetailItemModel sourceModel){
		MergeDetailItemModel destModel = new MergeDetailItemModel();
		destModel.startTimeModel = cloneDayTimeModel(sourceModel.startTimeModel);
		destModel.endTimeModel = cloneDayTimeModel(sourceModel.endTimeModel);
		return destModel;
	}
	
	public static void addWidget(LayoutContainer container, LabelField label, Widget widget){
		TableData td = new TableData();
		td.setWidth("32%");
		container.add(label, td);
		
		td = new TableData();
		td.setWidth("68%");
		container.add(widget, td);
	}
	
	public class ScheduleTypeModel {
		public final static int RepeatJob		   = 0;
		public final static int OnceDailyBackup	   = 1;
		public final static int OnceWeeklyBackup   = 2;
		public final static int OnceMonthlyBackup  = 3;		
	}
	
	public final static String[] oneWeek = { UIContext.Constants.scheduleSunday(), UIContext.Constants.scheduleMonday(),
		UIContext.Constants.scheduleTuesday(), UIContext.Constants.scheduleWednesday(),
		UIContext.Constants.scheduleThursday(), UIContext.Constants.scheduleFriday(),
		UIContext.Constants.scheduleSaturday() };
	
	public static boolean isRegularBackup(ScheduleItemModel itemModel){
		if(itemModel.getScheduleType() == ScheduleTypeModel.RepeatJob &&
				(itemModel.getJobType() == FULL_BACKUP ||
					itemModel.getJobType() == INC_BACKUP ||
					itemModel.getJobType() == VERIFY_BACKUP ))
			return true;
		
		return false;
	}
	
	public static boolean compare(Long val1, Long val2) {
		if(val1 == null){ 
			if(val2 == null)  return true;
			else return false;
		}else{
			if(val2 == null) return false;	
			else{
				return val1.longValue() == val2.longValue();
			}
		}
	}
	
	public static boolean compare(Integer val1, Integer val2) {
		if(val1 == null){ 
			if(val2 == null)  return true;
			else return false;
		}else{
			if(val2 == null) return false;	
			else{
				return val1.intValue() == val2.longValue();
			}
		}		
	}
	
	public static String getThrottleUnit(int throttleUnit){
		String throttleUnitString = (throttleUnit == Mbps_Unit) ? UIContext.Constants.scheduleThrottleUnitMbps():UIContext.Constants.scheduleThrottleUnitMBMin() ;
		return throttleUnitString;
	}
	
}
