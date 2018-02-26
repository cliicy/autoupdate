package com.ca.arcflash.ui.client.vsphere.backup;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ScheduleSubSettings;
import com.ca.arcflash.ui.client.backup.StartDateTimeSetting;
import com.ca.arcflash.ui.client.backup.ScheduleSubSettings.BkpType;
import com.ca.arcflash.ui.client.model.BackupScheduleModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class ScheduleSettings {
	
	private StartDateTimeSetting startTimeContainer;
	private ScheduleSubSettings incrementalSchedule;
	private ScheduleSubSettings fullSchedule;
	private ScheduleSubSettings resyncSchedule;
	private VSphereBackupSettingWindow parentWindow;
	
	private LayoutContainer container;
	
	public ScheduleSettings(VSphereBackupSettingWindow w)
	{
		parentWindow = w;
	}
	
	public LayoutContainer Render()
	{
		container = new LayoutContainer();
		
//		RowLayout layout = new RowLayout();		
//		container.setLayout(layout);
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("97%");
		tl.setHeight("95%");
		container.setLayout(tl);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsSchedule());
		label.addStyleName("restoreWizardTitle");
		container.add(label);

		startTimeContainer = new StartDateTimeSetting();
		container.add(startTimeContainer);
		//TODO Default data-3
		Date backupStartTime ;
		backupStartTime = new Date();
		long startTimeInMilliseconds = backupStartTime.getTime();
		//set backup start time plus 5 minutes
		startTimeInMilliseconds += 5 * 60 * 1000;
		backupStartTime.setTime(startTimeInMilliseconds);		
		startTimeContainer.setStartDateTime(backupStartTime);
		
		container.add(new Html("<HR>"));
		
		incrementalSchedule = new ScheduleSubSettings("IncrementalBackupRadioID",
				UIContext.Constants.scheduleLabelIncrementalBackup(), 
				UIContext.Messages.scheduleLabelIncrementalDescription(UIContext.productNamevSphere));
		incrementalSchedule.setDebugID("90E54010-CC6D-4d84-87B7-DCAC82697359", 
				"52346003-A1EF-46ba-ACF8-18C8C098813B", "F31E8992-7131-4249-B600-7FA8125BD82E", 
				"EBCF49FF-17A2-4997-8D9A-88D1FA98CDAD");
		
		incrementalSchedule.bkpType = BkpType.INC;
		
		fullSchedule = new ScheduleSubSettings("FullBackupRadioID",
				UIContext.Constants.scheduleLabelFullBackup(), 
				UIContext.Messages.scheduleLabelFullDescription(UIContext.productNamevSphere));
		fullSchedule.setDebugID("248858A8-E58F-4326-8883-68C52DA5C905", 
				"BFE3695D-CF8B-487c-ACB7-840574A85127", "D2B13E03-6D8B-4c18-934C-8EC9E48366AC", 
				"3686E519-691C-48f8-B6B9-6B93D54C9F49");
	
		fullSchedule.bkpType = BkpType.FULL;

		resyncSchedule = new ScheduleSubSettings("ResyncBackupRadioID",
				UIContext.Constants.scheduleLabelResyncBackup(), 
				UIContext.Messages.scheduleLabelResyncDescription(UIContext.productNamevSphere));				
		resyncSchedule.setDebugID("89D6831C-2988-452f-8BA7-F25535024166", 
				"2C5A60B6-9BE0-40e1-AEA7-2B756241E71A", "487AF869-4A26-4034-AC85-D0B8B4E466FB", 
				"DEA6AD1B-B1CC-452a-A49E-39B6C9702A52");
		
		container.add(incrementalSchedule.Render());
		
		container.add(new Html("<HR>"));
		container.add(fullSchedule.Render());
		
		container.add(new Html("<HR>"));
		container.add(resyncSchedule.Render());
		//TODO Default data-4
		fullSchedule.RefreshData(null);
		incrementalSchedule.RefreshData(null);
		resyncSchedule.RefreshData(null);
		
		return container;
	}
	
	
	public void RefreshData(VSphereBackupSettingModel model) {

		try{
			Date backupStartTime ;
			if(model.getBackupStartTime() > 0)
				backupStartTime = new Date(model.getBackupStartTime());
			else{
				backupStartTime = new Date();
				long startTimeInMilliseconds = backupStartTime.getTime();
				//set backup start time plus 5 minutes
				startTimeInMilliseconds += 5 * 60 * 1000;
				backupStartTime.setTime(startTimeInMilliseconds);
			}
			
			startTimeContainer.setStartDateTime(backupStartTime, model.getStartTimezoneOffset() != null ?
					model.getStartTimezoneOffset() : -1);
			
			fullSchedule.RefreshData(model.fullSchedule);
			incrementalSchedule.RefreshData(model.incrementalSchedule);
			resyncSchedule.RefreshData(model.resyncSchedule);
		}
		catch (Exception e)
		{
			
		}		
	}
	public void Save()
	{
		if (container == null || !container.isRendered())
			return;
		
		BackupScheduleModel fullModel = fullSchedule.Save();
		BackupScheduleModel incModel = incrementalSchedule.Save();
		BackupScheduleModel resyncModel = resyncSchedule.Save();
		
		Date selectedDate = startTimeContainer.getStartDateTime();
		
		parentWindow.model.setBackupStartTime(selectedDate.getTime());
		parentWindow.model.startTime = startTimeContainer.getUserSetTime();
		
		parentWindow.model.fullSchedule = fullModel;
		parentWindow.model.incrementalSchedule = incModel;
		parentWindow.model.resyncSchedule = resyncModel;		
	}
	public boolean Validate()
	{
		if (container == null || !container.isRendered())
			return true;
		
		return startTimeContainer.getStartDateTime() != null && 
		   (incrementalSchedule.Validate() && fullSchedule.Validate() && resyncSchedule.Validate());
	}
}
