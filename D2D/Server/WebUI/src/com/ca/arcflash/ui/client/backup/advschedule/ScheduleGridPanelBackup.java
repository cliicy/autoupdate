package com.ca.arcflash.ui.client.backup.advschedule;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.AdvanceScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.PeriodScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleDaySelectionPanel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.common.AdvScheduleUtil;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;


public class ScheduleGridPanelBackup extends ScheduleGridPanel {
	private FlashUIConstants uiConstants=UIContext.Constants;
//	private static FlashUIMessages uiMessages= UIContext.Messages;
	public static interface UpdateControls{
		void updateRetetntionCount(int backupType,int count);
		void hideRetentionCount(int backupType);
		int getRetentionCount(int backupType);
		void enableStartTime(boolean enabled);
		
	}
	protected UpdateControls updateControls;
	
	public ScheduleGridPanelBackup(UpdateControls updateRetentionCount){
		super();
		this.updateControls = updateRetentionCount;
	}
	
	public List<ScheduleItemModel> getScheduleModels(){
		return scheduleStore.getModels();
	}
	
	@Override
	protected Button createAddScheduleMenu(){
		AddScheduleButton addScheduleButton = new AddScheduleButton(this);
		return addScheduleButton;
	}
	
	@Override
	protected void showScheduleDetailWindow(int rowIndex, ScheduleItemModel newModel) {
		boolean isNewAdd = rowIndex<0 ? true:false;
		
		ScheduleAddItemBackupWindow  window = new ScheduleAddItemBackupWindow(newModel, isNewAdd);
		window.setOKButtonListener(getScheduleConfirmHandler(rowIndex, newModel, window));
		if(!isNewAdd){
			if(newModel.getScheduleType()==ScheduleTypeModel.OnceDailyBackup){
				int retentionCount = updateControls.getRetentionCount(ScheduleTypeModel.OnceDailyBackup);
				if((newModel.getEveryDaySchedule().getRetentionCount()!= retentionCount)
						&& (retentionCount > 0 )){
//					newModel.getEveryDaySchedule().setRetentionCount(retentionCount);
					newModel.getEveryDaySchedule().setRetentionCount(retentionCount>UIContext.maxRecoveryPointLimit?UIContext.maxRecoveryPointLimit:retentionCount);
				}
			}else if(newModel.getScheduleType()==ScheduleTypeModel.OnceWeeklyBackup){
				int retentionCount = updateControls.getRetentionCount(ScheduleTypeModel.OnceWeeklyBackup);
				if((newModel.getEveryWeekSchedule().getRetentionCount()!= retentionCount)
						&& (retentionCount > 0 )){
//					newModel.getEveryWeekSchedule().setRetentionCount(retentionCount);
					newModel.getEveryWeekSchedule().setRetentionCount(retentionCount>UIContext.maxRecoveryPointLimit?UIContext.maxRecoveryPointLimit:retentionCount);
				}
			}else if(newModel.getScheduleType()==ScheduleTypeModel.OnceMonthlyBackup){
				int retentionCount = updateControls.getRetentionCount(ScheduleTypeModel.OnceMonthlyBackup);
				if((newModel.getEveryMonthSchedule().getRetentionCount()!= retentionCount)
						&& (retentionCount > 0 )){
//					newModel.getEveryMonthSchedule().setRetentionCount(retentionCount);
					newModel.getEveryMonthSchedule().setRetentionCount(retentionCount>UIContext.maxRecoveryPointLimit?UIContext.maxRecoveryPointLimit:retentionCount);
				}
			}
			
			window.updateData();
		}
		window.setModal(true);
		window.show();		
	}
	
	@Override
	protected SelectionListener<ButtonEvent> getScheduleConfirmHandler(
			final int rowIndex, final ScheduleItemModel itemModel,
			final ScheduleAddItemBaseWindow window) {
		final int oldScheduleType = itemModel.getScheduleType();
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
					if(oldScheduleType != itemModel.getScheduleType()){
						updateControls.hideRetentionCount(oldScheduleType);
					}
				}
				
				updateControls(itemModel);				
				
				scheduleGrid.reconfigure(scheduleStore, scheduleGrid.getColumnModel());
											
				window.hide();
				
				checkSchedules();	
			}
			
		};
		return confirmHandler;
	}	
	
	private void updateControls(ScheduleItemModel itemModel){
		if(ScheduleUtils.isRegularBackup(itemModel) || AdvScheduleUtil.isOnceBackup(itemModel)){
			updateControls.enableStartTime(getBackupScheduleCount()>0);
		
			if(itemModel.getScheduleType() == ScheduleTypeModel.OnceDailyBackup){
				updateControls.updateRetetntionCount(ScheduleTypeModel.OnceDailyBackup, itemModel.getEveryDaySchedule().getRetentionCount());
			}else if(itemModel.getScheduleType() == ScheduleTypeModel.OnceWeeklyBackup){
				updateControls.updateRetetntionCount(ScheduleTypeModel.OnceWeeklyBackup, itemModel.getEveryWeekSchedule().getRetentionCount());
			}else if(itemModel.getScheduleType() == ScheduleTypeModel.OnceMonthlyBackup){
				updateControls.updateRetetntionCount(ScheduleTypeModel.OnceMonthlyBackup, itemModel.getEveryMonthSchedule().getRetentionCount());
			}
		}
	}
	
	@Override
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
						if(currentModel.getDayofWeek(i)==ScheduleUtils.SELECTED && itemModel.getDayofWeek(i) ==ScheduleUtils.SELECTED){
							if(!checkDayScheduleItems(currentModel, itemModel)){
								if(!retMessage.contains(ScheduleDaySelectionPanel.getDayDisplayName(i)))
									retMessage += ScheduleDaySelectionPanel.getDayDisplayName(i) + ", ";
								bRet = false;
							}
						}
					}			
				}			
				
			}else{
				if(currentModel.getScheduleType() == itemModel.getScheduleType()){
					if(rowIndex>=0){//Edit schedule
						if(exceptModel == itemModel)
							continue;
					}
					
					if(itemModel.getScheduleType() == ScheduleTypeModel.OnceDailyBackup)
						ScheduleUtils.showMesssageBox(uiConstants.errorTitle(),uiConstants.alreadyHaveOnceDailyBackupSchedule(), MessageBox.ERROR);
					else if(itemModel.getScheduleType() == ScheduleTypeModel.OnceWeeklyBackup)
						ScheduleUtils.showMesssageBox(uiConstants.errorTitle(),uiConstants.alreadyHaveOnceWeeklyBackupSchedule(), MessageBox.ERROR);
					else if(itemModel.getScheduleType() == ScheduleTypeModel.OnceMonthlyBackup)
						ScheduleUtils.showMesssageBox(uiConstants.errorTitle(),uiConstants.alreadyHaveOnceMonthlyBackupSchedule(), MessageBox.ERROR);
				
					return false;
				}
			}
		}
		
		if(!retMessage.isEmpty()){
			String retErrorMessage = (String) retMessage.subSequence(0, (retMessage.length()-2));
			String messageString = UIContext.Messages.scheduleItemOverLapWithDay(
				ScheduleUtils.getJobTypeStr(currentModel.getJobType()), 
				ScheduleUtils.formatTime(currentModel.startTimeModel), 
				ScheduleUtils.formatTime(currentModel.endTimeModel),
				retErrorMessage);
		
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), messageString, MessageBox.ERROR);
		}
		return bRet;	
	}

	public boolean hasOnceBackupSchedule(int onceBackupType){
		boolean bRet = false;
		for (ScheduleItemModel detail : scheduleStore.getModels()) {
			if(detail.getScheduleType() == onceBackupType){
				bRet = true;
				break;
			}
		}
		
		return bRet;
	}
	
	@Override
	protected void removeItems(){
		List<ScheduleItemModel> itemList = scheduleGrid.getSelectionModel().getSelectedItems();
		for(ScheduleItemModel itemModel: itemList){
			scheduleStore.remove(itemModel);
			updateControls.hideRetentionCount(itemModel.getScheduleType());
		}
		checkSchedules();
	}
	
	@Override
	protected void checkSchedules(){
		deleteButton.setEnabled(scheduleStore.getCount() > 0);
		
		int regularCount = 0;
		int onceCount=0;
		boolean bFoundSameSchedule = false;
		List<ScheduleItemModel> backups=new ArrayList<ScheduleItemModel>();
		for(ScheduleItemModel comparedItem : scheduleStore.getModels()){
			
			if(ScheduleUtils.isRegularBackup(comparedItem)){
				backups.add(comparedItem);
				regularCount++;
			}
			if(AdvScheduleUtil.isOnceBackup(comparedItem)){
				backups.add(comparedItem);
				onceCount++;
			}
				
			
			if(!bFoundSameSchedule && comparedItem.getScheduleType() == ScheduleTypeModel.RepeatJob){				
				for (ScheduleItemModel itemModel : scheduleStore.getModels()) {
					if((comparedItem != itemModel) && comparedItem.getDescription().equals(itemModel.getDescription()) && 
								(ScheduleUtils.compareDayTimeModel(comparedItem.startTimeModel, itemModel.startTimeModel) == 0) &&
								  (ScheduleUtils.compareDayTimeModel(comparedItem.endTimeModel, itemModel.endTimeModel) == 0)){
						bFoundSameSchedule = true;
						notificationSet.showDisplayInfoNotificateSet(uiConstants.scheduleSameBackupSchedule());
						break;
					}					
				}	
			}		
		}
		
		if(!bFoundSameSchedule){
			notificationSet.removeMessageFromInfoNotificationSet(uiConstants.scheduleSameBackupSchedule());
		}
		
		if(regularCount == 0){
			notificationSet.showDisplayInfoNotificateSet(uiConstants.scheduleWithoutRegularBackup());
		}else{
			notificationSet.removeMessageFromInfoNotificationSet(uiConstants.scheduleWithoutRegularBackup());
			
		}	
		if(backups.size() >=2&&checkJobConflict(backups)){
			notificationSet.showDisplayWarningNotificateSet(uiConstants.scheduleConflictSchedule());
			}
		else {
			notificationSet.removeMessageFromWaringNotificationSet(uiConstants.scheduleConflictSchedule());
		}
		updateControls.enableStartTime((regularCount + onceCount)> 0);

	}
	
	
	private boolean checkJobConflict(List<ScheduleItemModel> backups) {
		boolean result = false;
		for (int i = 0; i < backups.size() - 1; i++) {

			ScheduleItemModel backup = backups.get(i);
			for (int j = i + 1; j < backups.size(); j++) {

				ScheduleItemModel item = backups.get(j);
				if (item == backup)
					continue;
				boolean bIsEnable = item.isRepeatEnabled() == null ? false
						: item.isRepeatEnabled();

				if (!bIsEnable) {
					if (ScheduleUtils.compareDayTimeModel(
							backup.startTimeModel, item.startTimeModel) == 0)
						return true;
				} else {
					if (ScheduleUtils.compareDayTimeModel(
							backup.startTimeModel, item.startTimeModel) >= 0
							&& ScheduleUtils.compareDayTimeModel(
									backup.startTimeModel, item.endTimeModel) <= 0)
						return true;
					else {
						boolean bcIsEnable = backup.isRepeatEnabled() == null ? false
								: backup.isRepeatEnabled();
						if (bcIsEnable) {
							if (ScheduleUtils.compareDayTimeModel(
									item.startTimeModel, backup.startTimeModel) >= 0
									&& ScheduleUtils.compareDayTimeModel(
											item.startTimeModel,
											backup.endTimeModel) <= 0)
								return true;
						}
					}
				}
			}

		}
		return result;

	}
	
	@Override
	public void applyValue(AdvanceScheduleModel value){
		super.applyValue(value);
		
		PeriodScheduleModel periodSchedule = value.periodScheduleModel;
		if(periodSchedule!=null){
			EveryDayScheduleModel daySchedule = periodSchedule.dayScheduleModel;
			updateControls.hideRetentionCount(ScheduleTypeModel.OnceDailyBackup);
			updateControls.hideRetentionCount(ScheduleTypeModel.OnceWeeklyBackup);
			updateControls.hideRetentionCount(ScheduleTypeModel.OnceMonthlyBackup);
			if(daySchedule != null && daySchedule.isEnabled()){
				scheduleStore.add(ModelConverter.convertToScheduleItemModel(daySchedule));
				updateControls.updateRetetntionCount(ScheduleTypeModel.OnceDailyBackup, daySchedule.getRetentionCount());
			}
			EveryWeekScheduleModel weekSchedule = periodSchedule.weekScheduleModel;
			if(weekSchedule != null && weekSchedule.isEnabled()){
				scheduleStore.add(ModelConverter.convertToScheduleItemModel(weekSchedule));
				updateControls.updateRetetntionCount(ScheduleTypeModel.OnceWeeklyBackup, weekSchedule.getRetentionCount());
			}
			EveryMonthScheduleModel monthSchedule = periodSchedule.monthScheduleModel;
			if(monthSchedule!=null && monthSchedule.isEnabled()){
				scheduleStore.add(ModelConverter.convertToScheduleItemModel(monthSchedule));
				updateControls.updateRetetntionCount(ScheduleTypeModel.OnceMonthlyBackup, monthSchedule.getRetentionCount());
			}
		}
		
		deleteButton.setEnabled(scheduleStore.getCount() > 0);
	}
}
