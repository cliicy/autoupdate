package com.ca.arcflash.ui.client.common;

import java.util.ArrayList;

import com.ca.arcflash.ui.client.backup.advschedule.ScheduleItemModel;
import com.ca.arcflash.ui.client.backup.schedule.AdvanceScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.DailyScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.PeriodScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.ca.arcflash.ui.client.model.RetentionPolicyModel;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.user.client.ui.Widget;

public class AdvScheduleUtil {
	public final static int FieldWidth = 250;
	public final static String LabelWidth = "200";

	public static LayoutContainer createFormLayout(String name, Widget comp) {
		HorizontalPanel p = new HorizontalPanel();
		TableData td = new TableData();
		td.setWidth(LabelWidth);
		td.setVerticalAlign(VerticalAlignment.TOP);
		p.add(new LabelField(name), td);
		p.add(comp);
		return p;
	}
	
	public static LayoutContainer createFormLayout(String name, LabelField commentLabel, Widget comp){
		VerticalPanel v = new VerticalPanel();
//shaji02: for adding debug id.
v.ensureDebugId("13dfa55e-2a96-46d5-80b8-25c2cf881fd4");
		TableData vtd = new TableData();
		vtd.setWidth(LabelWidth);
		vtd.setVerticalAlign(VerticalAlignment.TOP);
		v.add(new LabelField(name), vtd);
		commentLabel.setEnabled(false);
		commentLabel.setVisible(false);
		v.add(commentLabel, vtd);
		
		HorizontalPanel p=new HorizontalPanel();
//shaji02: for adding debug id.
p.ensureDebugId("b9e04dbb-0082-4789-8ade-2d355b3a4505");
		p.add(v);
		p.add(comp);
		return p;
	}
	
	public static LayoutContainer createFormLayout(LabelField label, Widget comp){
		HorizontalPanel p=new HorizontalPanel();
		//shaji02: for adding debug id.
		p.ensureDebugId("133be4d9-0fb4-467c-a6d2-f022eb5a3c19");
		TableData td = new TableData();
		td.setWidth(LabelWidth);
		td.setVerticalAlign(VerticalAlignment.TOP);
		p.add(label, td);
		p.add(comp);
		return p;
	}
	
	public static LayoutContainer createFormLayout(String name, LabelField[] commentLabels, Widget[] comps){
		VerticalPanel leftPanel = new VerticalPanel();
		leftPanel.ensureDebugId("f918ee61-59b8-4ca3-842b-df5c7ff9b0f6");
		TableData leftTD = new TableData();
		leftTD.setWidth(LabelWidth);
		leftTD.setVerticalAlign(VerticalAlignment.TOP);
		leftPanel.add(new LabelField(name), leftTD);
		for (LabelField commentLabel : commentLabels) {
			commentLabel.setEnabled(false);
			commentLabel.setVisible(false);
			leftPanel.add(commentLabel, leftTD);
		}
		
		VerticalPanel rightPanel = new VerticalPanel();
		TableData rightTD = new TableData();
		rightTD.setVerticalAlign(VerticalAlignment.TOP);
		rightPanel.ensureDebugId("93cf0fcd-75c1-4ad1-b295-6acbd0b0acbd");
		int size = comps.length;
		for (int index = 0; index < size; index++) {
			rightPanel.add(comps[index]);
			if (index != size-1) {
				Label spaceLine = new Label();
				spaceLine.setHeight(10);
				rightPanel.add(spaceLine);
			}
		}

		HorizontalPanel p = new HorizontalPanel();
		p.setVerticalAlign(VerticalAlignment.TOP);
		p.ensureDebugId("c9b4f632-7fb1-48b1-b11b-2b86476e3a27");
		p.add(leftPanel);
		p.add(rightPanel);
		return p;
	}
	
	public static Component wrapFieldSet(Component c, String n){
		return wrapFieldSet(c, n ,false);
	}

	public static Component wrapFieldSet(Component c, String n, boolean isFirst) {
		// if(!isFirst){
		// LayoutContainer lc=new LayoutContainer();
		// lc.add(new Html("<HR>"));
		// lc.add(c, new FlowData(30, 0, 0, 0));
		// return lc;
		// }else
		return c;
	}

	public static LayoutData createLineLayoutData() {
		FlowData l = new FlowData(0, 0, 10, 0);
		return l;
	}

	public static Layout createLineLayout() {
		return new FlowLayout();
	}

	public static Layout createMainTabLayout() {
		return new FlowLayout();
	}

	public static LayoutData createMainTabLayoutData() {
		FlowData l = new FlowData(20, 10, 10, 10);
		return l;
	}

	public static LayoutContainer wrap(TabItem item) {
		LayoutContainer wrapper = new LayoutContainer();

		wrapper.setWidth(680);

		item.setAutoHeight(true);
		item.setLayout(createMainTabLayout());
		item.add(wrapper, createMainTabLayoutData());

		return wrapper;
	}
	
		public static LayoutContainer createTaskItemPanel() {
		LayoutContainer container = new LayoutContainer();

		container.setLayout(createLineLayout());
		return container;
	}

	public static void addTaskItem(LayoutContainer container, Widget widget,
			String label) {
		container.add(createFormLayout(label, widget),createLineLayoutData());
	}
	

	
	public static void initPolicyBackupEventModel(BackupSettingsModel bm) {		
		bm.setBackupToRps(false);
		bm.backupVolumes = new BackupVolumeModel();
		bm.backupVolumes.setIsFullMachine(true);
		bm.retentionPolicy = new RetentionPolicyModel();
		bm.retentionPolicy.setUseBackupSet(false);
		bm.retentionPolicy.setBackupSetCount(2);
		bm.retentionPolicy.setUseTimeRange(false);
		bm.setRetentionCount(31);
		bm.setCompressionLevel(1);
		bm.setEnableEncryption(false);
		bm.setEncryptionAlgorithm(0);
		bm.setChangedBackupDest(false);
		bm.setChangedBackupDestType(0);
		bm.setBackupDataFormat(1);	// New Format
		
		bm.advanceScheduleModel.daylyScheduleDetailItemModel = new ArrayList<DailyScheduleDetailItemModel>();
		for (int i = 1; i <= 7; ++i) {
			DailyScheduleDetailItemModel item = new DailyScheduleDetailItemModel();
			item.dayOfweek = i;
			item.scheduleDetailItemModels = new ArrayList<ScheduleDetailItemModel>();
			item.scheduleDetailItemModels.add(createDefaultScheduleDetailItem());
			bm.advanceScheduleModel.daylyScheduleDetailItemModel.add(item);
		}
		
		initPeriodSchedule(bm.advanceScheduleModel);
	}
	
	private static ScheduleDetailItemModel createDefaultScheduleDetailItem() {
		ScheduleDetailItemModel item = new ScheduleDetailItemModel();
		
		item.setJobType(ScheduleUtils.INC_BACKUP);		
		item.startTimeModel = new DayTimeModel(0, 0);
		item.endTimeModel = new DayTimeModel(6, 0);
		item.setInterval(3);
		item.setIntervalUnit(1);
		item.setRepeatEnabled(true);
		
		return item;
	}
	
	protected static void initPeriodSchedule(AdvanceScheduleModel aSchedule) {
		PeriodScheduleModel pSchedule = aSchedule.periodScheduleModel;
		
		EveryDayScheduleModel dailySchedule = pSchedule.dayScheduleModel;
		dailySchedule.setBkpType(1);
		dailySchedule.setDayTime(new DayTimeModel(20,0));
		dailySchedule.setEnabled(false);
		dailySchedule.setGenerateCatalog(false);
		dailySchedule.setRetentionCount(7);
		
		EveryWeekScheduleModel weeklySchedule = pSchedule.weekScheduleModel;
		weeklySchedule.setBkpType(1);
		weeklySchedule.setDayOfWeek(6);
		weeklySchedule.setDayTime(new DayTimeModel(20,0));
		weeklySchedule.setEnabled(false);
		weeklySchedule.setGenerateCatalog(false);
		weeklySchedule.setRetentionCount(5);
		
		EveryMonthScheduleModel monthlySchedule = pSchedule.monthScheduleModel;
		monthlySchedule.setBkpType(1);
		monthlySchedule.setDayOfMonth(32);
		monthlySchedule.setDayOfMonthEnabled(true);
		monthlySchedule.setDayTime(new DayTimeModel(20,0));
		monthlySchedule.setEnabled(false);
		monthlySchedule.setGenerateCatalog(false);
		monthlySchedule.setRetentionCount(12);
		monthlySchedule.setWeekDayOfMonth(6);
		monthlySchedule.setWeekNumOfMonth(0);
		monthlySchedule.setWeekOfMonthEnabled(false);		
	}
	
	public static boolean isOnceBackup(ScheduleItemModel itemModel){
		if(itemModel.getScheduleType() == ScheduleTypeModel.OnceDailyBackup ||
				itemModel.getScheduleType() == ScheduleTypeModel.OnceWeeklyBackup ||
				itemModel.getScheduleType() == ScheduleTypeModel.OnceMonthlyBackup )
			return true;
		
		return false;
	}
	
	public static LayoutContainer createLayout(String name, LabelField[] commentLabels, Widget[] comps){		
		
		VerticalPanel rightPanel = new VerticalPanel();
		TableData rightTD = new TableData();
		rightTD.setVerticalAlign(VerticalAlignment.TOP);
		rightPanel.ensureDebugId("93cf0fcd-75c1-4ad1-b295-6acbd0b0acbd");
		int size = comps.length;
		for (int index = 0; index < size; index++) {
			rightPanel.add(comps[index]);
			if (index != size-1) {
				Label spaceLine = new Label();
				spaceLine.setHeight(10);
				rightPanel.add(spaceLine);
			}
		}

		HorizontalPanel p = new HorizontalPanel();
		p.setVerticalAlign(VerticalAlignment.TOP);
		p.ensureDebugId("c9b4f632-7fb1-48b1-b11b-2b86476e3a27");	
		p.add(rightPanel);
		return p;
	}
	
	public static LayoutContainer wrap(LayoutContainer item) {
		LayoutContainer wrapper = new LayoutContainer();

//		wrapper.setWidth(680);
		item.setAutoHeight(true);
		item.setLayout(createMainTabLayout());
		item.add(wrapper, createMainTabLayoutData());

		return wrapper;
	}

	
}
