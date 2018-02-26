package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.AdvanceScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.DailyScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.MergeDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ThrottleModel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class ScheduleSummaryPanel extends LayoutContainer {

	private TreeStore<BaseTreeModel> treeStore;
	private TreeGrid<ModelData> treeGrid;	
	
	private LayoutContainer treeContainerPanel;
	
	private static final int Header_Row_HEIGHT = 60;	
	
	public TreeGrid<ModelData> getTreeGrid() {
		return treeGrid;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		FitLayout ft = new FitLayout();
		this.setLayout(ft);	
		this.add(Render());	
	}

	public LayoutContainer Render() {
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("padding", "10px");	
		container.setHeight("95%");
		RowLayout rl = new RowLayout();
		container.setLayout(rl);	
		LabelField label = new LabelField();
		label.addStyleName("restoreWizardTitle");
		label.setValue(UIContext.Constants.scheduleSummaryScheduleAndRetention());
		container.add(label, new RowData(1, -1, new Margins(10)));
		container.add(createTreeGrid(), new RowData(1, -1));	
		container.addListener(Events.Resize, new Listener<BoxComponentEvent>(){
			@Override
			public void handleEvent(BoxComponentEvent be) {
				int height = be.getHeight();
				int adjustHeight = 530;
				if(height- Header_Row_HEIGHT > 0){
					adjustHeight = height- Header_Row_HEIGHT;
				}
				treeContainerPanel.setHeight(adjustHeight);
			}});
		return container;
	}

	private Widget createTreeGrid() {
		ColumnConfig scheduleType = new ColumnConfig("scheduleType", UIContext.Constants.scheduleSummaryColType(),
				150);
		scheduleType.setMenuDisabled(true);
		scheduleType.setSortable(false);
		scheduleType.setRenderer(new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				String value = model.<String> get(property);
				if (value == null)
					value = "";
				return value;
			}

		});

		ColumnConfig schedule = new ColumnConfig("schedule", UIContext.Constants.scheduleSummaryColSchedule(), 310);
		schedule.setMenuDisabled(true);
		schedule.setSortable(false);
		schedule.setRenderer(new WidgetTreeGridCellRenderer<ModelData>() {

			@Override
			public Widget getWidget(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {

				LayoutContainer lc = new LayoutContainer();

				TableLayout layout = new TableLayout();
				layout.setColumns(3);
				lc.setLayout(layout);
				ToolTip tp = null;
				if (GXT.isIE) {
					LabelField lf = new LabelField();
					String text = model.<String> get(property);
					lf.setValue(text);				
					lc.add(lf);
					tp = Utils.addToolTip(lf, text);
				} else {
					LabelField lf = new LabelField();
					String text = model.<String> get(property).replace(" ",
							"&nbsp;");
					lf.setValue(text);
					lc.add(lf);
					tp = Utils.addToolTip(lf, text);
				}
				
				tp.setMaxWidth(400);
				
				return lc;
			}

		});

		ColumnConfig retention = new ColumnConfig("retention", UIContext.Constants.scheduleSummaryColRetention(), 140);
		retention.setMenuDisabled(true);
		retention.setSortable(false);
		retention.setAlignment(HorizontalAlignment.RIGHT);
		retention.setRenderer(new GridCellRenderer<BaseModelData>() {

			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				String value = model.<String> get(property);
				if (value == null)
					value = "";
				return value;
			}

		});

		ColumnModel treeColModel = new ColumnModel(Arrays.asList(scheduleType,
				schedule, retention));
		treeStore = new TreeStore<BaseTreeModel>();
		treeGrid = new TreeGrid<ModelData>(treeStore,
				treeColModel);
		// /((TreeGridView) tree.getView()).setRowHeight(23);

		treeGrid.setBorders(true);
		treeGrid.setAutoExpandColumn("schedule");
		treeGrid.setAutoExpandMax(5000);
		treeGrid.setTrackMouseOver(false);
		treeGrid.getTreeView().setBufferEnabled(false);
		treeGrid.setView(new TreeGridView() {
			@Override
			protected int getVisibleRowCount() {
				int nVisableRowCount = super.getVisibleRowCount();

				if (nVisableRowCount < 10) {
					nVisableRowCount = 10;
				}

				return nVisableRowCount;
			}		
		});
		
		treeGrid.getView().setAutoFill(true);
		//treeGrid.getView().setForceFit(true);
		//treeGrid.setWidth(660);
		//treeGrid.setHeight(450);
		treeContainerPanel = new LayoutContainer();
		treeContainerPanel.setLayout(new FitLayout());
		//cp.setHeight("100%");
		//cp.setWidth("100%");
		//cp.setWidth("100%");
		//cp.setHeight(450);		
		treeContainerPanel.setSize(660, 450);
		treeContainerPanel.add(treeGrid);
		
		treeContainerPanel.addListener(Events.Resize, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				
			}});
	///	RefreshData(null);
		return treeContainerPanel;
	}
	
	

	public void RefreshData(BackupSettingsModel model) {
 		treeStore.removeAll();

		AdvanceScheduleModel scheduleModel = null;
		if (model == null)
			model = new BackupSettingsModel();
		scheduleModel = model.advanceScheduleModel;
		if (scheduleModel == null) {
			scheduleModel = new AdvanceScheduleModel();
			model.advanceScheduleModel = scheduleModel;
		}
		if ((scheduleModel.daylyScheduleDetailItemModel == null)
				|| (scheduleModel.daylyScheduleDetailItemModel.size() == 0)) {
			scheduleModel.daylyScheduleDetailItemModel = new ArrayList<DailyScheduleDetailItemModel>(
					7);
			for (int i = 0; i < 7; i++) {
				scheduleModel.daylyScheduleDetailItemModel
						.add(new DailyScheduleDetailItemModel());
			}
		}

		String[] week = { UIContext.Constants.scheduleSunday(),
				UIContext.Constants.scheduleMonday(),
				UIContext.Constants.scheduleTuesday(),
				UIContext.Constants.scheduleWednesday(),
				UIContext.Constants.scheduleThursday(),
				UIContext.Constants.scheduleFriday(),
				UIContext.Constants.scheduleSaturday()
				};

		List<BaseTreeModel> roots = new ArrayList<BaseTreeModel>();
		BaseTreeModel root;
		for (int i = 0; i < 7; i++) {
			root = new BaseTreeModel();
			root.set("schedule", week[i]);
			if(i == 0){
				root.set("scheduleType", UIContext.Constants.scheduleSummaryRepeatBackup());
				if(model.retentionPolicy !=null && model.retentionPolicy.isUseBackupSet()!=null && model.retentionPolicy.isUseBackupSet()){
					//do nothing for backupset.
				}else{					
					Integer rc = model.getRetentionCount();
					if(rc != null){
						root.set("retention", UIContext.Messages.scheduleSummaryRetentionLastBackups(rc));
					}
				}
			}
			roots.add(root);
			DailyScheduleDetailItemModel itemModel = getWeekDaySchedule(i+1, scheduleModel.daylyScheduleDetailItemModel);
			if(itemModel != null) insertChildren(root, itemModel);				
		}
	
		
				
		String daySchedule = "";
		String dayRetention = "";
		
		String weekSchedule = "";
		String weekRetention = "";
		
		String monthSchedule = "";
		String monthRetention = "";
		
		if(scheduleModel != null && scheduleModel.periodScheduleModel != null){
			int dayCount = -1;
			int weekCount = -1;
			int monthCount = -1;
			
			String dayBkpType ="";		
			String weekBkpType ="";
			String monthBkpType="";
			
			DayTimeModel dayTime = null;
			DayTimeModel weekTime = null;
			DayTimeModel monthTime = null;
			
			int weeklyDayOfWeek = -1;
			int monthlyDayOfWeek = -1;
			String monthlyWeekNum = "";
			
			EveryDayScheduleModel dayScheduleModel = scheduleModel.periodScheduleModel.dayScheduleModel;	
			EveryWeekScheduleModel weekScheduleModel = scheduleModel.periodScheduleModel.weekScheduleModel;
			EveryMonthScheduleModel monthScheduleModel = scheduleModel.periodScheduleModel.monthScheduleModel;
			if (dayScheduleModel!=null && dayScheduleModel.isEnabled()){
				dayCount = dayScheduleModel.getRetentionCount();
				dayBkpType = getBackupType(dayScheduleModel.getBkpType());
				dayTime = dayScheduleModel.getDayTime();
				daySchedule = UIContext.Messages.scheduleSummaryDailyBackupSchedule(dayBkpType, ScheduleUtils.formatTime(dayTime));
				dayRetention = UIContext.Messages.scheduleSummaryDailyRentention(dayCount);
			}
			
			if (weekScheduleModel!=null && weekScheduleModel.isEnabled()){
				weekCount = weekScheduleModel.getRetentionCount();
				weekBkpType = getBackupType(weekScheduleModel.getBkpType());
				weekTime = weekScheduleModel.getDayTime();	
				weeklyDayOfWeek = weekScheduleModel.getDayOfWeek();
				weekSchedule = UIContext.Messages.scheduleSummaryWeeklyBackupSchedule(weekBkpType, ScheduleUtils.formatTime(weekTime), week[weeklyDayOfWeek-1]);
				weekRetention = UIContext.Messages.scheduleSummaryWeeklyRentention(weekCount);
			}
			
			if (monthScheduleModel!=null && monthScheduleModel.isEnabled()){
				monthCount = monthScheduleModel.getRetentionCount();
				monthBkpType = getBackupType(monthScheduleModel.getBkpType());
				monthTime = monthScheduleModel.getDayTime();
				monthlyDayOfWeek = monthScheduleModel.getWeekDayOfMonth();
				if(monthScheduleModel.isWeekOfMonthEnabled()){
					monthlyWeekNum = monthScheduleModel.getWeekNumOfMonth() == 0? UIContext.Constants.scheduleSummaryLast():UIContext.Constants.scheduleSummaryFirst();
					//"Full Backup at 9:00 PM on last Friday of the month"
					monthSchedule = UIContext.Messages.scheduleSummaryMonthlyBackupScheduleWeekOfMonth(monthBkpType,  ScheduleUtils.formatTime(monthTime), monthlyWeekNum, week[monthlyDayOfWeek-1]);
				}else{
					int dayOfMonth = monthScheduleModel.getDayOfMonth();
					if(dayOfMonth <= 31){
						monthSchedule = UIContext.Messages.scheduleSummaryMonthlyBackupScheduleDayOfMonth(monthBkpType,  ScheduleUtils.formatTime(monthTime), dayOfMonth); 
					}else{
						monthSchedule = UIContext.Messages.scheduleSummaryMonthlyBackupScheduleLastDay(monthBkpType,  ScheduleUtils.formatTime(monthTime)); 
					}
				}
				
				monthRetention = UIContext.Messages.scheduleSummaryMonthlyRentention(monthCount);
			}
		}
		
		root = new BaseTreeModel();
		root.set("scheduleType", UIContext.Constants.scheduleSummaryDailyBackup());		
		root.set("schedule", daySchedule);		
		root.set("retention", dayRetention);
		roots.add(root);

		root = new BaseTreeModel();
		root.set("scheduleType", UIContext.Constants.scheduleSummaryWeeklyBackup());
		root.set("schedule", weekSchedule);
		root.set("retention", weekRetention);
		roots.add(root);

		root = new BaseTreeModel();
		root.set("scheduleType", UIContext.Constants.scheduleSummaryMonthlyBackup());		
		root.set("schedule", monthSchedule);
		root.set("retention", monthRetention);
		
		roots.add(root);
		treeStore.add(roots, true);

	}
	
	public static String getScheduledWeekDay(int dayOfWeek)
	{
		String strSelectedWeekDay = "";
		switch(dayOfWeek)
		{
		case 0:
			strSelectedWeekDay = UIContext.Constants.Day();
			break;
		case 1:
			strSelectedWeekDay = UIContext.Constants.Sunday();
			break;
		case 2:
			strSelectedWeekDay = UIContext.Constants.Monday();
			break;
		case 3:
			strSelectedWeekDay = UIContext.Constants.Tuesday();
			break;
		case 4:
			strSelectedWeekDay = UIContext.Constants.Wednesday();
			break;
		case 5:
			strSelectedWeekDay = UIContext.Constants.Thursday();
			break;
		case 6:
			strSelectedWeekDay = UIContext.Constants.Friday();
			break;
		case 7:
			strSelectedWeekDay = UIContext.Constants.Saturday();
			break;
		default:
			strSelectedWeekDay = "";
			break;
		}
		return strSelectedWeekDay;
	}
	
	private String getBackupType(int bkpType) {
		if(bkpType == BackupTypeModel.Full) return UIContext.Constants.backupTypeFull();
		else if(bkpType == BackupTypeModel.Incremental) return UIContext.Constants.backupTypeIncremental();		
		else if(bkpType == BackupTypeModel.Resync) return UIContext.Constants.backupTypeResync();
		return null;
	}

	private DailyScheduleDetailItemModel getWeekDaySchedule(int dayOfWeek, List<DailyScheduleDetailItemModel> dailySchedule){
		if(dailySchedule == null) return null;
		for(DailyScheduleDetailItemModel item:dailySchedule){
			if(item.dayOfweek == dayOfWeek){
				return item;
			}
		}
		return null;
	}

	private void insertChildren(BaseTreeModel parent,
			DailyScheduleDetailItemModel daylyScheduleDetailItemModel) {

		List<ScheduleDetailItemModel> scheduleDetailItemModels = daylyScheduleDetailItemModel.scheduleDetailItemModels;
		List<ThrottleModel> throttleModels = daylyScheduleDetailItemModel.throttleModels;
		List<MergeDetailItemModel> mergeModels = daylyScheduleDetailItemModel.mergeModels;
		if (scheduleDetailItemModels == null) {
			scheduleDetailItemModels = new ArrayList<ScheduleDetailItemModel>();
		}
		if (throttleModels == null) {
			throttleModels = new ArrayList<ThrottleModel>();
		}
		if (mergeModels == null) {
			mergeModels = new ArrayList<MergeDetailItemModel>();
		}

		BaseTreeModel item1 = new BaseTreeModel();
		item1.set("schedule", UIContext.Messages.scheduleSummaryRepeatBackup(scheduleDetailItemModels.size()));
		parent.add(item1);

		for (ScheduleDetailItemModel m : scheduleDetailItemModels) {
			ModelData item = new BaseModelData();
			item.set("schedule", UIContext.Messages.scheduleSummaryRepeatBackupRepeat(ScheduleUtils.getScheduleRepeatStr(m.getInterval(), 
					m.getIntervalUnit()),ScheduleUtils.formatTime(m.startTimeModel), 
					ScheduleUtils.formatTime(m.endTimeModel), ScheduleUtils.getJobTypeStr(m.getJobType())));
			item1.add(item);
		}

		BaseTreeModel item2 = new BaseTreeModel();
		item2.set("schedule", UIContext.Messages.scheduleSummaryThrottling(throttleModels.size()));
		parent.add(item2);

		for (ThrottleModel m : throttleModels) {
			ModelData item = new BaseModelData();
			item.set("schedule", UIContext.Messages.scheduleSummaryThrottlingLimit(m.getThrottleValue()
					,ScheduleUtils.formatTime(m.startTimeModel), ScheduleUtils.formatTime(m.endTimeModel)));
			item2.add(item);
		}

		BaseTreeModel item3 = new BaseTreeModel();
		item3.set("schedule", UIContext.Messages.scheduleSummaryRepeatBackupMerge(mergeModels.size()));
		parent.add(item3);
		for (MergeDetailItemModel m : mergeModels) {
			ModelData item = new BaseModelData();
			item.set("schedule", UIContext.Messages.scheduleSummaryRepeatBackupMergeAllow(ScheduleUtils.formatTime(m.startTimeModel), ScheduleUtils.formatTime(m.endTimeModel)));
			item3.add(item);
		}
	}
}
