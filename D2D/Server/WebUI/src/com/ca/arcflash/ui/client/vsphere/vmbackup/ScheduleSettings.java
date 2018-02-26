package com.ca.arcflash.ui.client.vsphere.vmbackup;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ScheduleSubSettings;
import com.ca.arcflash.ui.client.backup.StartDateTimeSetting;
import com.ca.arcflash.ui.client.backup.ScheduleSubSettings.BkpType;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class ScheduleSettings {
	
	private StartDateTimeSetting startTimeContainer;
	private ScheduleSubSettings incrementalSchedule;
	private ScheduleSubSettings fullSchedule;
	private ScheduleSubSettings resyncSchedule;
	private VMBackupSettingWindow parentWindow;
	
	private LayoutContainer container;
	
	public ScheduleSettings(VMBackupSettingWindow w)
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
		incrementalSchedule.setDebugID("2DB72A94-89B4-4f71-86C4-069423399EAB", 
				"CDCA0159-E628-41b7-B16D-CC2F517C9171", "40E6BDDB-16BF-474b-A8A4-83A6BBE54B71", 
				"1E2ABFD9-1069-4439-A3A5-106ADC8FC65E");
		
		incrementalSchedule.bkpType = BkpType.INC;
		
		fullSchedule = new ScheduleSubSettings("FullBackupRadioID",
				UIContext.Constants.scheduleLabelFullBackup(), 
				UIContext.Messages.scheduleLabelFullDescription(UIContext.productNamevSphere));
		fullSchedule.setDebugID("38014232-E351-47b6-A3D7-AFA701C3C0EA", 
				"1908B86C-437C-4674-AA6D-E1FDDB560C8A", "2472B67E-73CA-4b59-966F-E5836D8E0590", 
				"0ED067B2-5D7D-4caa-8DA7-5D535E0EEADD");
		fullSchedule.bkpType = BkpType.FULL;

		resyncSchedule = new ScheduleSubSettings("ResyncBackupRadioID",
				UIContext.Constants.scheduleLabelResyncBackup(), 
				UIContext.Messages.scheduleLabelResyncDescription(UIContext.productNamevSphere));				
		resyncSchedule.setDebugID("07102B27-1F16-40a7-8405-82301A8A8CDD", 
				"728E4431-A632-46d5-8A21-A240B0FB6E6E", "DAF5D598-310E-4dbc-9FC4-D01AC8D6B88F", 
				"F6358DE8-F807-40b5-BA83-6C8DE86B6FCD");	
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
	
	
	public void RefreshData(VMBackupSettingModel model) {

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
}
