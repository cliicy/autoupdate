package com.ca.arcflash.ui.client.backup.advschedule;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.AdvanceScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.DailyScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.MergeDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.PeriodScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.backup.schedule.ThrottleModel;
import com.ca.arcflash.ui.client.common.CheckBoxIncrementalSelectionModel;
import com.ca.arcflash.ui.client.common.GxtFactory;
import com.ca.arcflash.ui.client.common.HasValidateValue;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.ca.arcflash.ui.client.model.DayTimeModel;


public abstract class ScheduleGridPanel extends LayoutContainer implements HasValidateValue<AdvanceScheduleModel> {
	protected Grid<ScheduleItemModel> scheduleGrid;
	protected ListStore<ScheduleItemModel> scheduleStore;
	private static FlashUIMessages uiMessages=UIContext.Messages;
	private static FlashUIConstants uiConstants=UIContext.Constants;
	protected NotificateSet notificationSet;
	protected abstract Button createAddScheduleMenu(); 
	protected Button deleteButton;
	private boolean enabled = true;
	
	public ScheduleGridPanel(){
		notificationSet = new NotificateSet();		
		initGui();
	}

	private void initGui() {		
		
		HorizontalPanel menuContainer = new HorizontalPanel();
		menuContainer.ensureDebugId("d245ffed-c9a9-40b1-86c6-a6518a411a27");
		menuContainer.setSpacing(4);		
		menuContainer.add(createAddScheduleMenu());
		
		deleteButton = new Button(uiConstants.deleteSchedule());
		deleteButton.addStyleName("ARCSERVE-STYLE-BUTTON");
		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				List<ScheduleItemModel> list = scheduleGrid.getSelectionModel().getSelectedItems();
				if (list == null || list.isEmpty()) 
					return;
				
				deleteItemModels();
				
			}
		});		
		menuContainer.add(deleteButton);
		
		this.add(menuContainer);			
		this.add(createSchedulePanel());
		this.add(notificationSet.getNotificateFieldSet(), new FlowData(10, 0, 0, 0));
		
	}
	
	private LayoutContainer createSchedulePanel(){
		LayoutContainer schedulePanel =  new LayoutContainer();
		schedulePanel.setLayout(new FitLayout());
		schedulePanel.setAutoHeight(true);
		schedulePanel.setAutoWidth(true);
		GridCellRenderer<ScheduleItemModel> typeRenderer = new GridCellRenderer<ScheduleItemModel>() {
			@Override
			public Object render(ScheduleItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleItemModel> store,
					Grid<ScheduleItemModel> grid)
			{
				return generateJobTypeWidget(model);
			}
			
		};
		
		GridCellRenderer<ScheduleItemModel> descriptionRenderer = new GridCellRenderer<ScheduleItemModel>() {
			@Override
			public Object render(final ScheduleItemModel model, String property,
					ColumnData config, final int rowIndex, int colIndex,
					ListStore<ScheduleItemModel> store,
					Grid<ScheduleItemModel> grid) 
			{
				final Anchor link = new Anchor();
				link.ensureDebugId("cc24227d-2ad7-4cc8-b604-4f4950d6f91b");
				link.getElement().getStyle().setCursor(Cursor.POINTER);
				link.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
				link.setText(model.getDescription());
				link.addClickHandler(new ClickHandler(){
					@Override
					public void onClick(ClickEvent event) {
						showEditWindow(model, rowIndex);
					}
				});
				return link;
			}
			
		};
		
		GridCellRenderer<ScheduleItemModel> sundayRenderer = new GridCellRenderer<ScheduleItemModel>() {
			@Override
			public Object render(ScheduleItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleItemModel> store,
					Grid<ScheduleItemModel> grid) 
			{
				return generateDayOfWeekWidget(model, 0);
			}
			
		};
		
		GridCellRenderer<ScheduleItemModel> mondayRenderer = new GridCellRenderer<ScheduleItemModel>() {
			@Override
			public Object render(ScheduleItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleItemModel> store,
					Grid<ScheduleItemModel> grid) 
			{
				return generateDayOfWeekWidget(model, 1);
			}
			
		};
		
		GridCellRenderer<ScheduleItemModel> tuesdayRenderer = new GridCellRenderer<ScheduleItemModel>() {
			@Override
			public Object render(ScheduleItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleItemModel> store,
					Grid<ScheduleItemModel> grid) 
			{
				return generateDayOfWeekWidget(model, 2);
			}
			
		};
		
		GridCellRenderer<ScheduleItemModel> wednesdayRenderer = new GridCellRenderer<ScheduleItemModel>() {
			@Override
			public Object render(ScheduleItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleItemModel> store,
					Grid<ScheduleItemModel> grid) 
			{
				return generateDayOfWeekWidget(model, 3);
			}
			
		};
		
		GridCellRenderer<ScheduleItemModel> thursdayRenderer = new GridCellRenderer<ScheduleItemModel>() {
			@Override
			public Object render(ScheduleItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleItemModel> store,
					Grid<ScheduleItemModel> grid) 
			{
				return  generateDayOfWeekWidget(model, 4);
			}
			
		};
		
		GridCellRenderer<ScheduleItemModel> fridayRenderer = new GridCellRenderer<ScheduleItemModel>() {
			@Override
			public Object render(ScheduleItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleItemModel> store,
					Grid<ScheduleItemModel> grid) 
			{
				return  generateDayOfWeekWidget(model, 5);
			}
			
		};
		
		GridCellRenderer<ScheduleItemModel> saturdayRenderer = new GridCellRenderer<ScheduleItemModel>() {
			@Override
			public Object render(ScheduleItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleItemModel> store,
					Grid<ScheduleItemModel> grid) 
			{
				return  generateDayOfWeekWidget(model, 6);
			}
			
		};
		
		GridCellRenderer<ScheduleItemModel> timeRenderer = new GridCellRenderer<ScheduleItemModel>() {
			@Override
			public Object render(ScheduleItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleItemModel> store,
					Grid<ScheduleItemModel> grid) 
			{
				if(model.getScheduleType() == ScheduleTypeModel.RepeatJob){
					if(ScheduleUtils.isRegularBackup(model)){
						if(model.isRepeatEnabled()){
							return ScheduleUtils.getTimeRange(model.startTimeModel, model.endTimeModel);
						}else{
							return ScheduleUtils.formatTime(model.startTimeModel);
						}
					}				
					
					return ScheduleUtils.getTimeRange(model.startTimeModel, model.endTimeModel);
				}
				else{
					return ScheduleUtils.formatTime(model.startTimeModel);
				}
			}
			
		};
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig configJobType = Utils.createColumnConfig("jobType", uiConstants.scheduleType(), 40, typeRenderer);
		configJobType.setAlignment(HorizontalAlignment.CENTER);
		configJobType.setResizable(true);
		configJobType.setSortable(true);
		configs.add(configJobType);

		ColumnConfig configDescription = Utils.createColumnConfig("description", uiConstants.scheduleDescription(), 194, descriptionRenderer);
		configDescription.setAlignment(HorizontalAlignment.LEFT);
		configDescription.setResizable(true);
		configs.add(configDescription);

		ColumnConfig configSunday = Utils.createColumnConfig("sunday", uiConstants.scheduleSundayAbbr(), 35, sundayRenderer);
		configSunday.setAlignment(HorizontalAlignment.CENTER);
		configSunday.setResizable(true);
		configs.add(configSunday);

		ColumnConfig configMonday = Utils.createColumnConfig("monday", uiConstants.scheduleMondayAbbr(), 35, mondayRenderer);
		configMonday.setAlignment(HorizontalAlignment.CENTER);
		configMonday.setResizable(true);
		configs.add(configMonday);

		ColumnConfig configTuesday = Utils.createColumnConfig("tuesday", uiConstants.scheduleTuesdayAbbr(), 35, tuesdayRenderer);
		configTuesday.setAlignment(HorizontalAlignment.CENTER);
		configTuesday.setResizable(true);
		configs.add(configTuesday);

		ColumnConfig configWednesday = Utils.createColumnConfig("wednesday", uiConstants.scheduleWednesdayAbbr(), 35, wednesdayRenderer);
		configWednesday.setAlignment(HorizontalAlignment.CENTER);
		configWednesday.setResizable(true);
		configs.add(configWednesday);

		ColumnConfig configThursday = Utils.createColumnConfig("thursday", uiConstants.scheduleThursdayAbbr(), 35, thursdayRenderer);
		configThursday.setAlignment(HorizontalAlignment.CENTER);
		configThursday.setResizable(true);
		configs.add(configThursday);

		ColumnConfig configFriday = Utils.createColumnConfig("friday", uiConstants.scheduleFridayAbbr(), 35, fridayRenderer);
		configFriday.setAlignment(HorizontalAlignment.CENTER);
		configFriday.setResizable(true);
		configs.add(configFriday);

		ColumnConfig configSaturday = Utils.createColumnConfig("saturday", uiConstants.scheduleSaturdayAbbr(), 35, saturdayRenderer);
		configSaturday.setAlignment(HorizontalAlignment.CENTER);
		configSaturday.setResizable(true);
		configs.add(configSaturday);

		ColumnConfig configTime = Utils.createColumnConfig("time", uiConstants.scheduleTime(), 140, timeRenderer);
		configTime.setAlignment(HorizontalAlignment.LEFT);
		configTime.setResizable(true);
		configTime.setSortable(true);
		configs.add(configTime);
		
		ColumnModel columnModel = new ColumnModel(configs);	
		scheduleStore = new ListStore<ScheduleItemModel>();
		
		StoreSorter<ScheduleItemModel> storeSorter = new StoreSorter<ScheduleItemModel>(){
			public int compare( Store<ScheduleItemModel> store,	ScheduleItemModel m1, ScheduleItemModel m2,
						String property ){
				if (property == null) {
					return 0;
				} else if (property.equalsIgnoreCase("jobType")) {
					Integer s1 = m1.getJobType();
					Integer s2 = m2.getJobType();
					return s1.compareTo(s2);
				} else if (property.equalsIgnoreCase("time")) {
					DayTimeModel s1 = m1.startTimeModel;
					DayTimeModel s2 = m2.startTimeModel;
					return ScheduleUtils.compareDayTimeModel(s1,s2);
				} else
					return 0;
			}
		};		
		scheduleGrid = GxtFactory.createMultiSelectGrid(scheduleStore, columnModel);
		columnModel.getColumn(0).setColumnStyleName("grid_col_cb");
		scheduleStore.setSortField("jobtype");
		scheduleStore.setSortDir( Style.SortDir.ASC );
		scheduleStore.setStoreSorter(storeSorter);
		scheduleGrid.ensureDebugId("83c7dd9a-2c93-467c-9664-8902d5a00e83");
		scheduleGrid.setBorders(true);	
		scheduleGrid.setAutoHeight(true);
		scheduleGrid.addStyleName("ARCSERVE_GRID");
//		scheduleGrid.setAutoWidth(true);
		scheduleGrid.setStripeRows(true);   
		scheduleGrid.setAutoExpandColumn("time");
		schedulePanel.add(scheduleGrid);
		
		return schedulePanel;
		
	}
	
	protected Object generateDayOfWeekWidget(ScheduleItemModel model, int nDay) {
		int nStatus = model.getDayofWeek(nDay);
		if(model.getScheduleType() != ScheduleTypeModel.OnceMonthlyBackup){

			if (nStatus == 0) {
				return "";
			} else if (nStatus == ScheduleUtils.SELECTED) {
				Image image = new Image();
				image.setResource(UIContext.IconBundle.generic_check_16());
				return image;
			} else {
				return nStatus;
			}
		}else{
			if(nStatus == ScheduleUtils.SELECTED)
				return model.getEveryMonthDate();
			else {
				return "";
			}
		}
		
	}
	
	protected Object generateJobTypeWidget(ScheduleItemModel model) {
		int nJobType = model.getJobType();
		Image image = new Image();
		if(nJobType == ScheduleUtils.MERGE){
			image.setResource(UIContext.IconBundle.merge_16());
		}else if(nJobType == ScheduleUtils.THROTTLE){
			image.setResource(UIContext.IconBundle.throttle_16());
		}else{
			image.setResource(UIContext.IconBundle.backup_16());
		}
		
		return image;		
	}
	
	protected boolean checkMaxBackupScheduleCount(ScheduleItemModel currentModel, ScheduleItemModel exceptModel){
		boolean bRet = true;
		if(currentModel.getScheduleType()!= ScheduleTypeModel.RepeatJob)					
			return true;
		
		int[] scheduleCount = getBackupScheduleCount(exceptModel);
		
		String strMsg="";
		for(int i=0; i<7; i++){
			if((currentModel.getDayofWeek(i)==ScheduleUtils.SELECTED) && (scheduleCount[i] >= ScheduleUtils.MAX_SCHEDULE_ITEM_COUNT))
				strMsg += ScheduleUtils.getDayDisplayName(i) + ", ";
		}
		if(!strMsg.isEmpty()){
			String dayOfWeek = (String) strMsg.subSequence(0, (strMsg.length()-2));
			String msg = getScheduleMaxItemMessage(ScheduleUtils.MAX_SCHEDULE_ITEM_COUNT, dayOfWeek);
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), msg, MessageBox.ERROR);
			bRet = false;
		}
		
		return bRet;
		
	}
	
	protected boolean checkMaxThrottleScheduleCount(ScheduleItemModel currentModel, ScheduleItemModel exceptModel){
		boolean bRet = true;		
		int[] scheduleCount = getThrottleScheduleCount(exceptModel);
		
		String strMsg="";
		for(int i=0; i<7; i++){
			if((currentModel.getDayofWeek(i)==ScheduleUtils.SELECTED) && (scheduleCount[i] >= ScheduleUtils.MAX_THROTTLE_ITEM_COUNT))
				strMsg += ScheduleUtils.getDayDisplayName(i) + ", ";
		}
		if(!strMsg.isEmpty()){
			String dayOfWeek = (String) strMsg.subSequence(0, (strMsg.length()-2));
			String msg = UIContext.Messages.scheduleThrottleMaxItemEx(ScheduleUtils.MAX_THROTTLE_ITEM_COUNT, dayOfWeek);
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), msg, MessageBox.ERROR);
			bRet = false;
		}
		
		return bRet;
		
	}
	
	protected boolean checkMaxMergeScheduleCount(ScheduleItemModel currentModel,ScheduleItemModel exceptModel){
		boolean bRet = true;		
		int[] scheduleCount = getMergeScheduleCount(exceptModel);
		
		String strMsg="";
		for(int i=0; i<7; i++){
			if((currentModel.getDayofWeek(i)==ScheduleUtils.SELECTED) && (scheduleCount[i] >= ScheduleUtils.MAX_MERGE_ITEM_COUNT))
				strMsg += ScheduleUtils.getDayDisplayName(i) + ", ";
		}
		if(!strMsg.isEmpty()){
			String dayOfWeek = (String) strMsg.subSequence(0, (strMsg.length()-2));
			String msg = UIContext.Messages.scheduleMergeMaxItemEx(ScheduleUtils.MAX_MERGE_ITEM_COUNT, dayOfWeek);
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), msg, MessageBox.ERROR);
			bRet = false;
		}
		
		return bRet;
		
	}
	
	protected String getScheduleMaxItemMessage(int nMaxCount, String dayOfWeek){
		return UIContext.Messages.scheduleMaxItemEx(ScheduleUtils.MAX_SCHEDULE_ITEM_COUNT, dayOfWeek);
	}
	
	protected String checkMinBackupScheduleCount(List<ScheduleItemModel> itemList,ScheduleItemModel exceptModel){
		int[] scheduleCount = getBackupScheduleCount(exceptModel);
		String strMsg="";
		for(ScheduleItemModel itemModel: itemList){
			for(int i=0; i<7; i++){
				if(ScheduleUtils.isRegularBackup(itemModel) &&
						(itemModel.getDayofWeek(i)== ScheduleUtils.SELECTED) && (scheduleCount[i] <=1) && 
						!strMsg.contains(ScheduleUtils.getDayDisplayName(i)))
					strMsg += ScheduleUtils.getDayDisplayName(i) + ", ";
			}
		}		
		
		if(!strMsg.isEmpty()){
			strMsg = (String) strMsg.subSequence(0, (strMsg.length()-2));			
		}
		
		return strMsg;
		
	}
	
	private int[] getBackupScheduleCount(ScheduleItemModel exceptModel){
		int[] scheduleCount = new int[7];
		for(ScheduleItemModel scheduleItemModel : scheduleStore.getModels()){
			if(ScheduleUtils.isRegularBackup(scheduleItemModel) && (scheduleItemModel != exceptModel))
				for(int i=0; i<7; i++){
					if(scheduleItemModel.getDayofWeek(i)==ScheduleUtils.SELECTED){
						int nCount = scheduleCount[i];
						scheduleCount[i] = nCount+1;
					}
				}
		}
		
		return scheduleCount;
	}
	
	private int[] getThrottleScheduleCount(ScheduleItemModel exceptModel){
		int[] scheduleCount = new int[7];
		for(ScheduleItemModel scheduleItemModel : scheduleStore.getModels()){
			if((scheduleItemModel.getScheduleType() == ScheduleTypeModel.RepeatJob) &&
					(scheduleItemModel.getJobType()==ScheduleUtils.THROTTLE) && (scheduleItemModel != exceptModel))
				for(int i=0; i<7; i++){
					if(scheduleItemModel.getDayofWeek(i)==ScheduleUtils.SELECTED){
						int nCount = scheduleCount[i];
						scheduleCount[i] = nCount+1;
					}
				}
		}
		
		return scheduleCount;
	}
	
	private int[] getMergeScheduleCount(ScheduleItemModel exceptModel){
		int[] scheduleCount = new int[7];
		for(ScheduleItemModel scheduleItemModel : scheduleStore.getModels()){
			if((scheduleItemModel.getScheduleType() == ScheduleTypeModel.RepeatJob) &&
					(scheduleItemModel.getJobType()==ScheduleUtils.MERGE ) && (scheduleItemModel != exceptModel))
				for(int i=0; i<7; i++){
					if(scheduleItemModel.getDayofWeek(i)==ScheduleUtils.SELECTED){
						int nCount = scheduleCount[i];
						scheduleCount[i] = nCount+1;
					}
				}
		}
		
		return scheduleCount;
	}
	protected void showScheduleDetailWindow(int rowIndex,
			ScheduleItemModel newModel) {
		boolean isNewAdd = rowIndex<0 ? true:false;
		
		ScheduleAddItemBackupWindow  window = new ScheduleAddItemBackupWindow(newModel, isNewAdd);
		window.setOKButtonListener(getScheduleConfirmHandler(rowIndex, newModel, window));
		if(!isNewAdd){
			window.updateData();
		}
		window.setModal(true);
		window.show();
	}
	
	protected void showThrottleWindow(int rowIndex, ScheduleItemModel newModel){
		boolean isNewAdd = rowIndex<0 ? true:false;
		if(isNewAdd)
			setDefaultThrottleUnit(newModel);
		final ScheduleAddItemThrottleWindow window = new ScheduleAddItemThrottleWindow( newModel, isNewAdd);
		window.setOKButtonListener(getThrottleConfirmHandler(rowIndex, newModel, window));
		if(!isNewAdd){
			window.updateData();
		}
		window.setModal(true);
		window.show();
	}
	
	protected void setDefaultThrottleUnit(ScheduleItemModel newModel){
		newModel.setThrottleUnit(ScheduleUtils.MB_MIN_Unit);
	}
	
	protected void showMergeWindow(int rowIndex, ScheduleItemModel newModel){
		boolean isNewAdd = rowIndex<0 ? true:false;
		final ScheduleAddItemMergeWindow window = new ScheduleAddItemMergeWindow( newModel, isNewAdd);
		window.setOKButtonListener(getMergeConfirmHandler(rowIndex, newModel, window));
		if(!isNewAdd){
			window.updateData();
		}
		window.setModal(true);
		window.show();
	}

	protected SelectionListener<ButtonEvent> getScheduleConfirmHandler(
			final int rowIndex, final ScheduleItemModel itemModel,
			final ScheduleAddItemBaseWindow window) {
		
		SelectionListener<ButtonEvent> confirmHandler = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!window.validate())
					return;
				
				ScheduleItemModel currentModel = window.getCurrentModel();				
				
				if(!checkAndSetScheduleItems(currentModel, rowIndex, itemModel)){
					return;
				}
				
				updateGrid();				

			}
			
			private void updateGrid(){
				window.save();
				if(rowIndex<0){
					scheduleStore.add(itemModel);					
				}
				else{
					scheduleStore.update(itemModel);
				}

				scheduleGrid.reconfigure(scheduleStore, scheduleGrid.getColumnModel());
							
				window.hide();
				
				checkSchedules();	
			}
			
		};
		return confirmHandler;
	}	
	
	protected boolean checkAndSetScheduleItems(ScheduleItemModel currentModel, int rowIndex, ScheduleItemModel exceptModel){				
		boolean bRet = true;		
		String retMessage = "";
		
		if(!checkMaxBackupScheduleCount(currentModel, exceptModel))
			return false;
		
		for(ScheduleItemModel itemModel: scheduleStore.getModels()){
			if(currentModel.getScheduleType() == ScheduleTypeModel.RepeatJob){
				if(currentModel.getScheduleType() == itemModel.getScheduleType() && currentModel.getJobType() == itemModel.getJobType()){
					if(rowIndex>=0){//Edit schedule
						if(exceptModel == itemModel)
							continue;
					}
					
					for(int i=0; i<7; i++){
						if((currentModel.getDayofWeek(i)==ScheduleUtils.SELECTED) && (itemModel.getDayofWeek(i) == ScheduleUtils.SELECTED)){
							if(!checkDayScheduleItems(currentModel, itemModel)){
								if(!retMessage.contains(ScheduleAddDetailItemDaySelectionPanel.getDayDisplayName(i)))
									retMessage += ScheduleAddDetailItemDaySelectionPanel.getDayDisplayName(i) + ", ";
								bRet = false;
							}
						}
					}			
				}			
				
			}
		}
		
		if(!retMessage.isEmpty()){
			String retErrorMessage = (String) retMessage.subSequence(0, (retMessage.length()-2));
			String messageString = getScheduleItemOverlapMessage(
				ScheduleUtils.getJobTypeStr(currentModel.getJobType()), 
				ScheduleUtils.formatTime(currentModel.startTimeModel), 
				ScheduleUtils.formatTime(currentModel.endTimeModel),
				retErrorMessage);
		
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), messageString, MessageBox.ERROR);
		}
		return bRet;	
	}

	protected String getScheduleItemOverlapMessage(String jobType, String startTime, String endTime, String day){
		return uiMessages.scheduleItemOverLapWithDay(jobType, startTime,endTime,day);
	}
	
	protected boolean checkDayScheduleItems(ScheduleItemModel currentModel, ScheduleItemModel storeModel){
		boolean bRet = false;
		DayTimeModel cstart = currentModel.startTimeModel;
		DayTimeModel cend = currentModel.endTimeModel;
		DayTimeModel sstart = storeModel.startTimeModel;
		DayTimeModel send = storeModel.endTimeModel;
		if(ScheduleUtils.compareDayTimeModel(cend,new DayTimeModel(0,0))==0)cend=new DayTimeModel(24,0);
		if(ScheduleUtils.compareDayTimeModel(send,new DayTimeModel(0,0))==0)send=new DayTimeModel(24,0);
		int result1 = ScheduleUtils.compareDayTimeModel(cend, sstart);
		int result2 = ScheduleUtils.compareDayTimeModel(cstart, send);
		if(result1<=0){
			bRet = true;
		}else if(result2>=0){
			bRet = true;
		}else{
			bRet = false;				
		}
					
		return bRet;
	}
	
	protected SelectionListener<ButtonEvent> getThrottleConfirmHandler(
			final int rowIndex, final ScheduleItemModel newModel, final BaseDetailItemWindow<ScheduleItemModel> window) {
		SelectionListener<ButtonEvent> confirmHandler = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!window.validate())
					return;
				
				ScheduleItemModel currentModel = window.getCurrentModel();
				
				if(!checkThrottleItems(currentModel.startTimeModel,currentModel.endTimeModel, currentModel, newModel))
					return;
				
				updateGrid();
			}
			
			private void updateGrid(){
				window.save();
				if(rowIndex<0){
					scheduleStore.add(newModel);
				}else{ 
					scheduleStore.update(newModel);
				}
				scheduleGrid.reconfigure(scheduleStore, scheduleGrid.getColumnModel());
				window.hide();
				
				checkSchedules();
			}
		};
		return confirmHandler;
	}
	
	protected boolean checkThrottleItems(DayTimeModel start, DayTimeModel end, ScheduleItemModel currentModel, ScheduleItemModel exceptModel){
		if(!checkThrottleItemStartEndTime(start, end))
			return false;
		
		if(!checkMaxThrottleScheduleCount(currentModel, exceptModel))
			return false;
		
		String retMessage = "";
		boolean bRet = true;
		for (ScheduleItemModel itemModel : scheduleStore.getModels()) {
			if (itemModel.getScheduleType() == ScheduleTypeModel.RepeatJob
					&& itemModel.getJobType() == ScheduleUtils.THROTTLE) {
				if (exceptModel == itemModel)
						continue;
				
				for(int i=0; i<7; i++){
					if(currentModel.getDayofWeek(i)==ScheduleUtils.SELECTED && itemModel.getDayofWeek(i) ==ScheduleUtils.SELECTED){
						int result1 = ScheduleUtils.compareDayTimeModel(end,
								itemModel.startTimeModel);
						int result2 = ScheduleUtils.compareDayTimeModel(start,
								itemModel.endTimeModel);
						if (result1 <= 0) {
							continue;
						} else if (result2 >= 0) {
							continue;
						} else {
							if(!retMessage.contains(ScheduleAddDetailItemDaySelectionPanel.getDayDisplayName(i)))
								retMessage += ScheduleAddDetailItemDaySelectionPanel.getDayDisplayName(i) + ", ";
								bRet = false;
							}
						}
					}
				}
				
			}
		
		if(!retMessage.isEmpty()){
			String retErrorMessage = (String) retMessage.subSequence(0, (retMessage.length()-2));
			String messageString = uiMessages.scheduleThrottleItemOverLapEx(
					ScheduleUtils.formatTime(start), 
					ScheduleUtils.formatTime(end), 
					retErrorMessage);

			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), messageString, MessageBox.ERROR);
		}
	
		return bRet;
	}
	
	private boolean checkThrottleItemStartEndTime(DayTimeModel start, DayTimeModel end){
		//For endTime,12:00AM or 00:00 is refer to the end of the day. 
		if(end.getHour() == 0 && end.getMinutes() ==0){
			end.setHour(24);
			end.setMinute(0);
		}//
		
		if(ScheduleUtils.compareDayTimeModel(start, end)>=0){
			//show the message		
			String message = UIContext.Constants.scheduleThrottleStartTimeBeforeEndTime();
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), message, MessageBox.ERROR);
			return false;
		}
		int endTimeMins = end.getHour()*60 + end.getMinutes();
		int startTimeMins = start.getHour()*60 + start.getMinutes();
		if(endTimeMins-startTimeMins<15){
			//show the message
			String message = UIContext.Constants.scheduleThrottleStartTimeBeforeEndTime15Mins();
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), message, MessageBox.ERROR);
			return false;
		}
		
		return true;
	}
	
	protected SelectionListener<ButtonEvent> getMergeConfirmHandler(
			final int rowIndex, final ScheduleItemModel newModel, final BaseDetailItemWindow<ScheduleItemModel> window) {
		SelectionListener<ButtonEvent> confirmHandler = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!window.validate())
					return;
				
				ScheduleItemModel currentModel = window.getCurrentModel();
				
				if(!checkMergeItems(currentModel.startTimeModel,currentModel.endTimeModel, currentModel,  newModel))
					return;
				
				updateGrid();				
				
			}
			
			private void updateGrid(){
				window.save();
				if(rowIndex<0){
					scheduleStore.add(newModel);
				}
				else 
					scheduleStore.update(newModel);
				scheduleGrid.reconfigure(scheduleStore, scheduleGrid.getColumnModel());
				window.hide();
				
				checkSchedules();
			}
		};
		return confirmHandler;
	}
	
	protected boolean checkMergeItems(DayTimeModel start, DayTimeModel end,ScheduleItemModel currentModel, ScheduleItemModel exceptModel){
		if(!checkMergeItemStartEndTime(start, end))
			return false;
		
		if(!checkMaxMergeScheduleCount(currentModel, exceptModel))
			return false;
		
		String retMessage = "";
		Boolean bRet = true;
		
		List<ScheduleItemModel> models = getMergeItems(exceptModel);
		for (ScheduleItemModel itemModel : models) {
			for(int i=0; i<7; i++){
				if(currentModel.getDayofWeek(i)==ScheduleUtils.SELECTED && itemModel.getDayofWeek(i) == ScheduleUtils.SELECTED){
					DayTimeModel ie=itemModel.endTimeModel;
					DayTimeModel ce=currentModel.endTimeModel;
					if(ScheduleUtils.compareDayTimeModel(ce,new DayTimeModel(0,0))==0)ce=new DayTimeModel(24,0);
					if(ScheduleUtils.compareDayTimeModel(ie,new DayTimeModel(0,0))==0)ie=new DayTimeModel(24,0);
					int result1 = ScheduleUtils.compareDayTimeModel(ce, itemModel.startTimeModel);
					int result2 = ScheduleUtils.compareDayTimeModel(start, ie);
					if(result1<=0){
						continue;
					}
					else if(result2>=0){
						continue;
					}
					else{						
						if(!retMessage.contains(ScheduleAddDetailItemDaySelectionPanel.getDayDisplayName(i)))
							retMessage += ScheduleAddDetailItemDaySelectionPanel.getDayDisplayName(i) + ", ";
							bRet = false;

						}
				}
			}
			
		}
		
		if(!retMessage.isEmpty()){
			String retErrorMessage = (String) retMessage.subSequence(0, (retMessage.length()-2));
			String messageString = uiMessages.scheduleMergeItemOverLapEx(
					ScheduleUtils.formatTime(start), 
					ScheduleUtils.formatTime(end), 
					retErrorMessage);
		
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), messageString, MessageBox.ERROR);
		}
			
		return bRet;
	}
	
	protected boolean checkMergeItemStartEndTime(DayTimeModel start, DayTimeModel end){
		int endTimeMins = end.getHour()*60 + end.getMinutes();
		int startTimeMins = start.getHour()*60 + start.getMinutes();
		if(ScheduleUtils.compareDayTimeModel(start, end)>=0&&endTimeMins!=0){
			//show the message		
			String message = UIContext.Constants.scheduleMergeStartTimeBeforeEndTime();
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), message, MessageBox.ERROR);
			return false;
		}
		if(endTimeMins==0)endTimeMins+=1440;
		if(endTimeMins-startTimeMins<15){
			//show the message
			String message = UIContext.Constants.scheduleMergeStartTimeBeforeEndTime15Mins();
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), message, MessageBox.ERROR);
			return false;
		}
		
		return true;
	}
	
	private List<ScheduleItemModel> getMergeItems(ScheduleItemModel exceptModel){
		ArrayList<ScheduleItemModel> models = new ArrayList<ScheduleItemModel>();
			
		for (ScheduleItemModel detail : scheduleStore.getModels()) {			
			if (detail.getScheduleType() == ScheduleTypeModel.RepeatJob
					&& detail.getJobType() == ScheduleUtils.MERGE) {
				if(detail == exceptModel){
					continue;
				}
				models.add(detail);
			}
		}
		return models;
	}
	
	@Override
	public void applyValue(AdvanceScheduleModel value) {
		scheduleStore.removeAll();
		List<ScheduleItemModel> models = new ArrayList<ScheduleItemModel> ();
		if(value.daylyScheduleDetailItemModel !=null && !value.daylyScheduleDetailItemModel.isEmpty()){
			for (int i = 0; i < 7; i++) {
				for (DailyScheduleDetailItemModel detailItemValue : value.daylyScheduleDetailItemModel) {
					if (detailItemValue.dayOfweek == (i + 1)) {
						if( detailItemValue.scheduleDetailItemModels != null) {
							for (ScheduleDetailItemModel item : detailItemValue.scheduleDetailItemModels) {
								boolean bFound = false;
								for(ScheduleItemModel scheduleItemModel : models){
									if(scheduleItemModel.getJobType()==item.getJobType() &&  
											ScheduleUtils.compareDayTimeModel(scheduleItemModel.startTimeModel, item.startTimeModel)==0 &&
											ScheduleUtils.compareDayTimeModel(scheduleItemModel.endTimeModel, item.endTimeModel)==0 &&
											ScheduleUtils.compare(scheduleItemModel.getInterval(),item.getInterval()) &&
											ScheduleUtils.compare(scheduleItemModel.getIntervalUnit(), item.getIntervalUnit())){
											scheduleItemModel.setDayofWeek(i, 1);
											bFound = true;
											break;
									}
								}
								if(!bFound){
									models.add(ModelConverter.convertToScheduleItemModel(item, i));							
								}	
							}
						}

						if (detailItemValue.throttleModels != null) {
							for (ThrottleModel item : detailItemValue.throttleModels) {
								boolean bFound = false;
								for (ScheduleItemModel scheduleItemModel : models) {
									if (scheduleItemModel.getJobType() == ScheduleUtils.THROTTLE
											&& ScheduleUtils.compareDayTimeModel(scheduleItemModel.startTimeModel, item.startTimeModel) == 0
											&& ScheduleUtils.compareDayTimeModel(scheduleItemModel.endTimeModel, item.endTimeModel) == 0
											&& ScheduleUtils.compare(scheduleItemModel.getThrottle(), item.getThrottleValue())) {
										scheduleItemModel.setDayofWeek(i, 1);
										bFound = true;
										break;
									}
								}
								if (!bFound) {
									models.add(convertToScheduleItemModel(item, i));
								}
							}
						}

						if (detailItemValue.mergeModels != null) {
							for (MergeDetailItemModel item : detailItemValue.mergeModels) {
								boolean bFound = false;
								for (ScheduleItemModel scheduleItemModel : models) {
									if (scheduleItemModel.getJobType() == ScheduleUtils.MERGE
											&& ScheduleUtils.compareDayTimeModel(scheduleItemModel.startTimeModel, item.startTimeModel) == 0
											&& ScheduleUtils.compareDayTimeModel(scheduleItemModel.endTimeModel, item.endTimeModel) == 0) {
										scheduleItemModel.setDayofWeek(i, 1);
										bFound = true;
										break;
									}
								}
								if (!bFound) {
									models.add(ModelConverter.convertToScheduleItemModel(item, i));
								}

							}
						}
					}
						
				}
			}
			scheduleStore.add(models);
		}
		deleteButton.setEnabled(scheduleStore.getCount() > 0);	
	}
	
	protected ScheduleItemModel convertToScheduleItemModel(ThrottleModel item, int dayOfWeek){
		ScheduleItemModel itemModel = ModelConverter.convertToScheduleItemModel(item,dayOfWeek);
		return itemModel;		
	}

//	private void applyStartTime(AdvanceScheduleModel value) {
//		Date backupStartTime;
//		if (value !=null &&value.getBackupStartTime() > 0)
//			backupStartTime = new Date(value.getBackupStartTime());
//		else {
//			backupStartTime = new Date();
//			long startTimeInMilliseconds = backupStartTime.getTime();
//			// set backup start time plus 5 minutes
//			startTimeInMilliseconds += 5 * 60 * 1000;
//			backupStartTime.setTime(startTimeInMilliseconds);
//		}
//		
////		if (UIContext.serverVersionInfo.getTimeZoneOffset() != null)
////			startTimeContainer.setStartDateTime(backupStartTime, UIContext.serverVersionInfo.getTimeZoneOffset());
////		else
////			startTimeContainer.setStartDateTime(backupStartTime);
//		
//	}

	@Override
	public boolean validate(){
		return true;
	}

	@Override
	public void buildValue(AdvanceScheduleModel value) {
		value.daylyScheduleDetailItemModel.clear();
		PeriodScheduleModel periodSchedule = value.periodScheduleModel;
		List<DailyScheduleDetailItemModel> dailyScheduleDetailItemList = new ArrayList<DailyScheduleDetailItemModel>();

		for (ScheduleItemModel itemModel : scheduleStore.getModels()) {

			if (itemModel.getScheduleType() == ScheduleTypeModel.RepeatJob) {

				for (int i = 0; i < 7; i++) {
					if (itemModel.getDayofWeek(i) == ScheduleUtils.SELECTED) {
						boolean bFound = false;
						for (DailyScheduleDetailItemModel dailyItem : dailyScheduleDetailItemList) {
							if (dailyItem.dayOfweek == (i + 1)) {
								buildDailyScheduleItemValue(itemModel,
										dailyItem);
								bFound = true;
								break;
							}
						}

						if (!bFound) {
							DailyScheduleDetailItemModel item = new DailyScheduleDetailItemModel();
							item.dayOfweek = i + 1;
							buildDailyScheduleItemValue(itemModel, item);
							dailyScheduleDetailItemList.add(item);
						}
					}
				}
			}else if( itemModel.getScheduleType() == ScheduleTypeModel.OnceDailyBackup){
				periodSchedule.dayScheduleModel = itemModel.getEveryDaySchedule();				
			}else if( itemModel.getScheduleType() == ScheduleTypeModel.OnceWeeklyBackup){
				periodSchedule.weekScheduleModel = itemModel.getEveryWeekSchedule();
			}else if( itemModel.getScheduleType() == ScheduleTypeModel.OnceMonthlyBackup){
				periodSchedule.monthScheduleModel = itemModel.getEveryMonthSchedule();
			}			
		}
		
		value.daylyScheduleDetailItemModel  = dailyScheduleDetailItemList;		
		value.periodScheduleModel = periodSchedule;
		
	//	Date selectedDate = startTimeContainer.getStartDateTime(UIContext.serverVersionInfo.getTimeZoneOffset());		
	//	value.setBackupStartTime(selectedDate.getTime());
	}

	protected void checkSchedules(){
		deleteButton.setEnabled(scheduleStore.getCount() > 0);
		
		//check whether there are same backup schedules
		for(ScheduleItemModel comparedItem : scheduleStore.getModels()){
			if(comparedItem.getScheduleType() == ScheduleTypeModel.RepeatJob){				
				for (ScheduleItemModel itemModel : scheduleStore.getModels()) {
					if((comparedItem != itemModel) && comparedItem.getDescription().equals(itemModel.getDescription()) && 
								(ScheduleUtils.compareDayTimeModel(comparedItem.startTimeModel, itemModel.startTimeModel) == 0) &&
								  (ScheduleUtils.compareDayTimeModel(comparedItem.endTimeModel, itemModel.endTimeModel) == 0)){
						notificationSet.showDisplayInfoNotificateSet(uiConstants.scheduleSameBackupSchedule());
						return;
					}					
				}	
			}		
		}
		
		notificationSet.removeMessageFromInfoNotificationSet(uiConstants.scheduleSameBackupSchedule());					

	}
	
	private void buildDailyScheduleItemValue(ScheduleItemModel itemModel, DailyScheduleDetailItemModel dailyItem ){
		if(itemModel.getJobType()== ScheduleUtils.FULL_BACKUP || 
				itemModel.getJobType()== ScheduleUtils.INC_BACKUP ||
				itemModel.getJobType()== ScheduleUtils.VERIFY_BACKUP){
			if (dailyItem.scheduleDetailItemModels == null) {
				dailyItem.scheduleDetailItemModels = new ArrayList<ScheduleDetailItemModel>();
			}
			dailyItem.scheduleDetailItemModels.add(ModelConverter.convertToBackupSchedule(itemModel));						
		}else if(itemModel.getJobType() == ScheduleUtils.THROTTLE){
			if (dailyItem.throttleModels == null) {
				dailyItem.throttleModels = new ArrayList<ThrottleModel>();
			}
			dailyItem.throttleModels.add(ModelConverter.convertToThrottle(itemModel));								
		}else if(itemModel.getJobType() == ScheduleUtils.MERGE){
			if (dailyItem.mergeModels == null) {
				dailyItem.mergeModels = new ArrayList<MergeDetailItemModel>();
			}
			dailyItem.mergeModels.add(ModelConverter.convertToMerge(itemModel));
		}
	}	
	
	protected void deleteItemModels(){	
				
		Listener<MessageBoxEvent> callback = new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					removeItems();
				} 
			}};
			
		popUpWarning(getDetScheduleConfirmMessage(), callback);
	}
	
	protected String getDetScheduleConfirmMessage(){
		return UIContext.Constants.scheduleDelConfirm();
	}
	
	protected void removeItems(){
		List<ScheduleItemModel> itemList = scheduleGrid.getSelectionModel().getSelectedItems();
		for(ScheduleItemModel itemModel: itemList){
				scheduleStore.remove(itemModel);
		}	
		checkSchedules();
	}	
	
	protected void popUpWarning(String message, Listener<MessageBoxEvent> callback) {		
		MessageBox box = new MessageBox();
		box.setIcon(MessageBox.WARNING);
		box.setButtons(Dialog.YESNO);
		box.setMessage(message);
		box.setTitleHtml(uiConstants.confirmTitle());
		box.addCallback(callback);
		box.setModal(true);
		Utils.setMessageBoxDebugId(box);
		box.show();	
	}
	
	protected void showEditWindow(ScheduleItemModel model, int rowIndex){
		if(!enabled)
			return;
		if(model.getJobType()== ScheduleUtils.MERGE){
			showMergeWindow(rowIndex, model);
		}else if(model.getJobType() == ScheduleUtils.THROTTLE){
			showThrottleWindow(rowIndex, model);
		}else{
			showScheduleDetailWindow(rowIndex, model);
		}
	}
	
	public int getBackupScheduleCount(){
		int count = 0;
		for(ScheduleItemModel comparedItem : scheduleStore.getModels()){
			if(ScheduleUtils.isRegularBackup(comparedItem) || comparedItem.getScheduleType() == ScheduleTypeModel.OnceDailyBackup ||
					 comparedItem.getScheduleType() == ScheduleTypeModel.OnceWeeklyBackup ||
					 comparedItem.getScheduleType() == ScheduleTypeModel.OnceMonthlyBackup )
				count++;
		}
		
		return count;
	}	
	
	public void setEditable(boolean isEnabled) {
			this.enabled = isEnabled;
			if(scheduleGrid.getSelectionModel() instanceof CheckBoxIncrementalSelectionModel) {
				CheckBoxIncrementalSelectionModel model = (CheckBoxIncrementalSelectionModel) scheduleGrid.getSelectionModel();
				model.setEnabled(isEnabled);
		}
	}
}