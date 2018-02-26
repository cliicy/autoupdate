package com.ca.arcflash.ui.client.backup.schedule;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class BackupScheduleDetail extends ScheduleDetail{
	private BackupSchedulePanel parentPanel;
	
	class ScheduleDetailItemModelEx {
		private int index;
		private ScheduleDetailItemModel scheduleDetailItemModel;
		public void setIndex(int index){
			this.index = index;
		}
		public int getIndex(){
			return this.index;
		}
		public void setScheduleModel(ScheduleDetailItemModel scheduleDetailItemModel){
			this.scheduleDetailItemModel = scheduleDetailItemModel;
		}
		public ScheduleDetailItemModel getScheduleModel(){
			return this.scheduleDetailItemModel;
		}
	}
	
	private List<ScheduleDetailItemModelEx> modifyItemModelExList;

	public BackupScheduleDetail(BackupSchedulePanel parentPanel,int exceptIndex,boolean showMergeGrid,int dayOfWeek) {
		super(exceptIndex,showMergeGrid, dayOfWeek);
		this.parentPanel = parentPanel;
	}
	
	@Override
	public ScheduleDetailItemModel getDefaultScheduleItemModel(){
		ScheduleDetailItemModel itemModel = new ScheduleDetailItemModel();
		itemModel.setJobType(ScheduleUtils.INC_BACKUP);
		DayTimeModel startDayTimeModel = new DayTimeModel();
		startDayTimeModel.setHour(8);
		startDayTimeModel.setMinute(0);
		itemModel.startTimeModel = startDayTimeModel;
		DayTimeModel endDayTimeModel = new DayTimeModel();
		endDayTimeModel.setHour(18);
		endDayTimeModel.setMinute(0);
		itemModel.endTimeModel = endDayTimeModel;
		itemModel.setInterval(3);
		itemModel.setIntervalUnit(1);
		itemModel.setRepeatEnabled(Boolean.TRUE);
		return itemModel;
	}
	@Override
	public ThrottleModel getDefaultThrottleItemModel(){
		ThrottleModel itemModel = new ThrottleModel();
		DayTimeModel startDayTimeModel = new DayTimeModel();
		startDayTimeModel.setHour(8);
		startDayTimeModel.setMinute(0);
		itemModel.startTimeModel = startDayTimeModel;
		DayTimeModel endDayTimeModel = new DayTimeModel();
		endDayTimeModel.setHour(18);
		endDayTimeModel.setMinute(0);
		itemModel.endTimeModel = endDayTimeModel;
		itemModel.setThrottleValue(0L);
		return itemModel;
	}
	@Override
	public MergeDetailItemModel getDefaultMergeItemModel(){
		MergeDetailItemModel itemModel = new MergeDetailItemModel();
		DayTimeModel startDayTimeModel = new DayTimeModel();
		startDayTimeModel.setHour(8);
		startDayTimeModel.setMinute(0);
		itemModel.startTimeModel = startDayTimeModel;
		DayTimeModel endDayTimeModel = new DayTimeModel();
		endDayTimeModel.setHour(18);
		endDayTimeModel.setMinute(0);
		itemModel.endTimeModel = endDayTimeModel;
		return itemModel;
	}
	
	@Override
	public SchedulePanel getParentPanel(){
		return parentPanel;
	}
	
	@Override
	public ListStore<FlashFieldSetModel> getJobTypeModels(){
		return ScheduleUtils.getBackupTypeModels();
	}

	@Override
	protected String getThrottleSpeedLabel(){
		return UIContext.Constants.destinationThrottleDescription(); 
	}
	
	@Override
	protected String getThrottleUnit(){
		return UIContext.Constants.destinationThrottleUnit();
	}
	
	@Override 
	protected String getScheduleLabelTitle(){
		return UIContext.Constants.scheduleBackupJob();
	}
	
	@Override
	protected String getScheduleAddText(){
		return UIContext.Constants.scheduleAddText();
	}
	@Override
	public String getScheduleAddWindowHeader(){
		return UIContext.Constants.scheduleAddWindowHeader();
	}
	@Override
	public String getScheduleEditWindowHeader(){
		return UIContext.Constants.scheduleEditWindowHeader();
	}
	@Override
	protected String getThrottleLabelTitle(){
		return UIContext.Constants.scheduleBackupThrottle();
	}
	@Override 
	protected String getThrottleAddText(){
		return UIContext.Constants.scheduleThrottleAddText();
	}
	@Override
	protected String getThrottleAddWindowHeader(){
		return UIContext.Constants.scheduleThrottleAddWindowHeader();
	}
	@Override
	protected String getThrottleEditWindowHeader(){
		return UIContext.Constants.scheduleThrottleEditWindowHeader();
	}
	@Override
	protected String getMergeLabelTitle(){
		return UIContext.Constants.scheduleMerge();
	}
	@Override 
	protected String getMergeAddText(){
		return UIContext.Constants.scheduleMergeAddText();
	}
	@Override
	protected String getMergeAddWindowHeader(){
		return UIContext.Constants.scheduleMergeAddWindowHeader();
	}
	@Override
	protected String getMergeEditWindowHeader(){
		return UIContext.Constants.scheduleMergeEditWindowHeader();
	}
	@Override
	protected String getCopyButtonText(){
		return UIContext.Constants.scheduleCopyText();
	}
	@Override
	protected String getCopyWindowDescription(){
		return UIContext.Constants.scheduleCopyDescription();
	}
	@Override
	protected String getCopyWindowScheduleOptionText(){
		return UIContext.Constants.scheduleCopyOption();
	}
	@Override
	protected String getCopyWindowThrottleOptionText(){
		return UIContext.Constants.scheduleCopyOptionThrottling();
	}
	@Override
	protected String getCopyWindowMergeOptionText(){
		return UIContext.Constants.scheduleCopyOptionMerge();
	}
	@Override
	protected SelectionListener<ButtonEvent> getScheduleConfirmHandler(
			final int rowIndex, final ScheduleDetailItemModel itemModel,
			final ScheduleDetailItemWindow window) {
		SelectionListener<ButtonEvent> confirmHandler = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!window.validate())
					return;
				
				ScheduleDetailItemModel currentModel = window.getCurrentModel();
				
				if(!checkAndSetScheduleItems(window.getDayIndexs(), currentModel)){
					window.hide();
					return;
				}
				
				updateGrid();
				
			}
			
			private void updateGrid(){
				window.save();
				if(rowIndex<0){
					scheduleStore.add(itemModel);
					if(parentPanel.GetScheduleDetails() != null){
						for(ScheduleDetail  s : parentPanel.GetScheduleDetails()){
							s.checkButtonIcon();
						}
					}					
				}
				else{
					scheduleStore.update(itemModel);
				}
				scheduleGrid.reconfigure(scheduleStore, scheduleGrid.getColumnModel());
				window.hide();
			}
			
		};
		return confirmHandler;
	}

	@Override
	protected ClickHandler getScheduleDelHandler(
			final ScheduleDetailItemModel newModel) {
		ClickHandler delHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!isEditable)
					return;
				
				if(!checkDailyBackupAfterDel())
					return;
				
				Listener<MessageBoxEvent> callback = new Listener<MessageBoxEvent>() {
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							updateGrid();
						} 
					}
				};
				popUpWarning(UIContext.Constants.scheduleDelConfirm(), callback);
			}
			
			private void updateGrid(){
				scheduleStore.remove(newModel);
				checkButtonIcon();
			}
			
			private boolean checkDailyBackupAfterDel(){
				if(scheduleStore.getModels().size()<=1){
					Listener<MessageBoxEvent> callback = new Listener<MessageBoxEvent>() {
						@Override
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
								updateGrid();
							} 
						}
					};
					
					String msg = UIContext.Constants.scheduledJobTypeBackup();
					if (scheduleStore.getModels().size() == 1) {
						ScheduleDetailItemModel itemModel = scheduleStore.getModels().get(0);
						if (itemModel.getJobType() == ScheduleUtils.THROTTLE) {
							msg = UIContext.Constants.scheduledJobTypeThrottle();
						} else if (itemModel.getJobType() == ScheduleUtils.MERGE) {
							msg = UIContext.Constants.scheduledJobTypeMerge();
						}
					}
					popUpWarning(getConfirmDelecteMessage(ScheduleUtils.getWeekDayByIndex(exceptIndex), msg), callback);
					return false;
				}
				return true;
			}
		};
		
		return delHandler;
	}
	
	protected String getConfirmDelecteMessage(String dayOfWeek, String scheduledJobTypeNames){
		return UIContext.Messages.scheduleNoDailyBackupAfterDelete(dayOfWeek, scheduledJobTypeNames);
	}

	@Override
	protected SelectionListener<ButtonEvent> getThrottleConfirmHandler(
			final int rowIndex, final ThrottleModel newModel, final ThrottleItemWindow window) {
		SelectionListener<ButtonEvent> confirmHandler = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!window.validate())
					return;
				
				ThrottleModel currentModel = window.getCurrentModel();
				
				if(!checkThrottleItems(currentModel.startTimeModel,currentModel.endTimeModel, newModel))
					return;
				
				updateGrid();
			}
			
			private void updateGrid(){
				window.save();
				if(rowIndex<0){
					throttleStore.add(newModel);
					checkButtonIcon();
				}
				else 
					throttleStore.update(newModel);
				throttleGrid.reconfigure(throttleStore, throttleGrid.getColumnModel());
				window.hide();
			}
		};
		return confirmHandler;
	}
	
	@Override
	protected SelectionListener<ButtonEvent> getMergeConfirmHandler(
			final int rowIndex, final MergeDetailItemModel newModel, final MergeItemWindow window) {
		SelectionListener<ButtonEvent> confirmHandler = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!window.validate())
					return;
				
				MergeDetailItemModel currentModel = window.getCurrentModel();
				
				if(!checkMergeItems(currentModel.startTimeModel,currentModel.endTimeModel, newModel))
					return;
				
				updateGrid();
			}
			
			private void updateGrid(){
				window.save();
				if(rowIndex<0){
					mergeStore.add(newModel);
					checkButtonIcon();
				}
				else 
					mergeStore.update(newModel);
				mergeGrid.reconfigure(mergeStore, mergeGrid.getColumnModel());
				window.hide();
			}
		};
		return confirmHandler;
	}

	@Override
	protected ClickHandler getThrottleDelHandler(final ThrottleModel newModel) {
		ClickHandler delHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!isEditable)
					return;
				
				Listener<MessageBoxEvent> callback = new Listener<MessageBoxEvent>() {
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							throttleStore.remove(newModel);
							checkButtonIcon();
						} 
					}
				};
				
				popUpWarning(UIContext.Constants.scheduleThrottleDelConfirm(), callback);
				
			}
		};
		return delHandler;
	}
	
	@Override
	protected ClickHandler getMergeDelHandler(final MergeDetailItemModel newModel) {
		ClickHandler delHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!isEditable)
					return;
				
				Listener<MessageBoxEvent> callback = new Listener<MessageBoxEvent>() {
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							mergeStore.remove(newModel);
							checkButtonIcon();
						} 
					}
				};
				
				popUpWarning(UIContext.Constants.scheduleMergeDelConfirm(), callback);
				
			}
		};
		return delHandler;
	}
	
	@Override
	public String getScheduleHelpURL(){
		return UIContext.externalLinks.getBackupSettingAdvanceScheduleHelp();
	}
	@Override
	protected String getThrottleHelpURL(){
		return UIContext.externalLinks.getBackupSettingAdvanceThrottlingHelp();
	}
	@Override
	protected String getMergeHelpURL(){
		return UIContext.externalLinks.getBackupSettingAdvanceMergeHelp();
	}
	
	@Override
	protected void showScheduleDetailWindow(int rowIndex,ScheduleDetailItemModel newModel){
		boolean isNewAdd = rowIndex<0 ? true:false;		
		List<Integer> modifyItemIndexList = getModifyItemList(newModel, isNewAdd);
		ScheduleDetailItemWindow window = new ScheduleDetailItemWindow( newModel, isNewAdd, this);	
		window.setModifyItemIndexList(modifyItemIndexList);
		window.setOKButtonListener(getScheduleConfirmHandler(rowIndex, newModel, window));
		window.setModal(true);
		window.show();
	}
	
	public List<Integer> getModifyItemList(ScheduleDetailItemModel curModel, boolean isNewAdd){
		List<Integer> indexList = new ArrayList<Integer>();
		modifyItemModelExList = new ArrayList<ScheduleDetailItemModelEx>();
		ScheduleDetail[] allScheduleDetails = getParentPanel().getAllScheduleDetails();
		if (!isNewAdd && allScheduleDetails != null) {
			for (int i=0; i< allScheduleDetails.length; i++) {
				ScheduleDetail scheduleDetail = allScheduleDetails[i];
				ListStore<ScheduleDetailItemModel> scheduleDetailStore = scheduleDetail.getScheduleDetailStore();
				for (ScheduleDetailItemModel detailModel : scheduleDetailStore.getModels()) {
					if(ScheduleUtils.isSameSchedule(detailModel, curModel)){
						indexList.add(i);
						ScheduleDetailItemModelEx itemModelEx = new ScheduleDetailItemModelEx();
						itemModelEx.setIndex(i);
						itemModelEx.setScheduleModel(detailModel);
						modifyItemModelExList.add(itemModelEx);
					}
				}
			}
		}
		return indexList;
	}
	
	public boolean checkAndSetScheduleItems(List<Integer> destIndexs, ScheduleDetailItemModel currentModel){
		ScheduleDetail[] allScheduleDetails = getParentPanel().getAllScheduleDetails();
		if(allScheduleDetails==null)
			return false;		
				
		boolean bRet = true;
		boolean bFoundCurId = false;
		String retErrorMessage = "";
		for (int destIndex : destIndexs) {
			if(destIndex == (getDayofWeek()-1))
				bFoundCurId = true;
			
			ScheduleDetail destScheduleDetail = allScheduleDetails[destIndex];
			if(destScheduleDetail==null)
				continue;
			
			//if modify schedule, get the modified item model and skip it when checking schedule items
			ScheduleDetailItemModel exceptModel = null;
			for(ScheduleDetailItemModelEx detailEx : modifyItemModelExList){
				if(destIndex == detailEx.getIndex()){
					exceptModel = detailEx.scheduleDetailItemModel;
					break;
				}
			}
			
			//check whether the schedule item meets the requirements
			ListStore<ScheduleDetailItemModel> destScheduleStore = destScheduleDetail.getScheduleDetailStore();			
			String retMessage = checkDayScheduleItems(currentModel, destScheduleStore, destIndex, exceptModel);
						
			if(!retMessage.isEmpty()){
				retErrorMessage += retMessage + ", ";
				if(destIndex == (getDayofWeek()-1)){
					bRet = false;					
				}
			}else{
				if(destIndex != (getDayofWeek()-1)){ //skip current selected day, because it will invoke updateGrid() to add/modify item model.
					if(modifyItemModelExList.isEmpty()){
						//Add item
						destScheduleStore.add(ScheduleUtils.cloneScheduleItemModel(currentModel));
					}else{						
						//Modify item
						destScheduleStore.update(ScheduleUtils.updateScheduleItemModel(currentModel, exceptModel));
					}
				}					
			}		
			
		}
		
		if(!retErrorMessage.isEmpty()){
			retErrorMessage = (String) retErrorMessage.subSequence(0, (retErrorMessage.length()-2));
			String messageString = UIContext.Messages.scheduleItemOverLapWithDay(
					ScheduleUtils.getJobTypeStr(currentModel.getJobType()), 
					ScheduleUtils.formatTime(currentModel.startTimeModel), 
					ScheduleUtils.formatTime(currentModel.endTimeModel),
					retErrorMessage);
			
			ScheduleUtils.showMesssageBox(UIContext.Constants.wrongInfo(), messageString, MessageBox.ERROR);
		}
		
		return bRet && bFoundCurId;
	}
	
	protected String checkDayScheduleItems(ScheduleDetailItemModel currentModel, ListStore<ScheduleDetailItemModel> destScheduleStore, int nDayIndex, ScheduleDetailItemModel exceptModel){
		DayTimeModel start = currentModel.startTimeModel;
		DayTimeModel end = 	currentModel.endTimeModel;
		int backupType = currentModel.getJobType();

		String message = "";
		List<ScheduleDetailItemModel> models;		
		models = getBackupScheduleItemsByJobType(backupType,destScheduleStore, exceptModel); 
				
		for (ScheduleDetailItemModel itemModel : models) {			
			int result1 = ScheduleUtils.compareDayTimeModel(end, itemModel.startTimeModel);
			int result2 = ScheduleUtils.compareDayTimeModel(start, itemModel.endTimeModel);
			if(result1<=0){
				continue;
			}else if(result2>=0){
				continue;
			}else{
				message = ScheduleDaySelectionPanel.getDayDisplayName(nDayIndex);				
			}
		}			
		return message;
	}
	
	protected List<ScheduleDetailItemModel> getBackupScheduleItemsByJobType(int jobType, ListStore<ScheduleDetailItemModel> destScheduleStore, ScheduleDetailItemModel exceptModel){
		ArrayList<ScheduleDetailItemModel> models = new ArrayList<ScheduleDetailItemModel>();
		
		for (ScheduleDetailItemModel detail : destScheduleStore.getModels()) {
			
			if(exceptModel!=null && ScheduleUtils.isSameSchedule(detail, exceptModel)){
				continue;
			}
			
			if(detail.getJobType()== jobType){
				models.add(detail);
			}
		}
		return models;
	}
	
}
