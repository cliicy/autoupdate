package com.ca.arcflash.ui.client.backup;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ScheduleSubSettings.BkpType;
import com.ca.arcflash.ui.client.common.SettingsGroupType;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.model.BackupScheduleModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.ca.arcflash.ui.client.vsphere.setting.ScheduleVMRecoverySetCheckPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class ScheduleSettings {
	
	private StartDateTimeSetting startTimeContainer;
	private ScheduleSubSettings incrementalSchedule;
	private ScheduleSubSettings fullSchedule;
	private ScheduleSubSettings resyncSchedule;
	private BackupSettingsContent parentWindow;
//	private RetentionPanel retention;
	private CatalogPanel scheduleCatalogRecoverySetPanel;
	private LayoutContainer container;
	private SettingsGroupType settingsGroupType;
	private ScheduleVMRecoverySetCheckPanel checkPanel;
	
	public CatalogPanel  getCatalogPanel(){
		return scheduleCatalogRecoverySetPanel;
	}
	
	public ScheduleSettings(BackupSettingsContent w)
	{
		this(w, null);
	}
	
	public ScheduleSettings(BackupSettingsContent w, SettingsGroupType settingsGroupType)
	{
		parentWindow = w;
		this.settingsGroupType = settingsGroupType;
	}
	
	public LayoutContainer Render()
	{
		container = new LayoutContainer();
		
//		RowLayout layout = new RowLayout();		
//		container.setLayout(layout);
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("97%");
//		tl.setHeight("95%");
		container.setLayout(tl);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsSchedule());
		label.addStyleName("restoreWizardTitle");
		container.add(label);
		
		DisclosurePanel disSettingsPanel; 
		disSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.scheduleStartDateTime());
		
		LayoutContainer timeContainer = new LayoutContainer();
		
		
		startTimeContainer = new StartDateTimeSetting();
		startTimeContainer.getDateTimeSettingsHeader().hide();
		timeContainer.add(startTimeContainer);
		timeContainer.add(new Html("<HR>"));
		disSettingsPanel.add(timeContainer);
		container.add(disSettingsPanel);
		
				
		String incrementalScheduleDescription = "";
		if(parentWindow.isShowForVSphere()){
			incrementalScheduleDescription = UIContext.Messages.scheduleLabelIncrementalDescription(UIContext.productNamevSphere);
		}else{
			incrementalScheduleDescription = UIContext.Messages.scheduleLabelIncrementalDescription(UIContext.productNameD2D);
		}
		incrementalSchedule = new ScheduleSubSettings("IncrementalBackupRadioID",
				UIContext.Constants.scheduleLabelIncrementalBackup(), 
				incrementalScheduleDescription);
		incrementalSchedule.bkpType = BkpType.INC;
		
		String fullScheduleDescription = "";
		if(parentWindow.isShowForVSphere()){
			fullScheduleDescription = UIContext.Messages.scheduleLabelFullDescription(UIContext.productNamevSphere);
		}else{
			fullScheduleDescription = UIContext.Messages.scheduleLabelFullDescription(UIContext.productNameD2D);
		}
		fullSchedule = new ScheduleSubSettings("FullBackupRadioID",
				UIContext.Constants.scheduleLabelFullBackup(), 
				fullScheduleDescription);
	
		fullSchedule.bkpType = BkpType.FULL;

		String resyncDescription = "";
		if(parentWindow.isShowForVSphere()){
			resyncDescription = UIContext.Messages.scheduleLabelResyncDescription(UIContext.productNamevSphere);
		}else{
			resyncDescription = UIContext.Messages.scheduleLabelResyncDescription(UIContext.productNameD2D);
		}
		resyncSchedule = new ScheduleSubSettings("ResyncBackupRadioID",
				UIContext.Constants.scheduleLabelResyncBackup(), 
				resyncDescription);			
		
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.scheduleLabelIncrementalBackup());		
		LayoutContainer cont = new LayoutContainer();
		cont.add(incrementalSchedule.Render());
		cont.add(new Html("<HR>"));		
		panel.add(cont);
		
		
		container.add(panel);
		
		
		panel = Utils.getDisclosurePanel(UIContext.Constants.scheduleLabelFullBackup());		
		cont = new LayoutContainer();
		cont.add(fullSchedule.Render());
		cont.add(new Html("<HR>"));		
		panel.add(cont);
		
		container.add(panel);
		
		panel = Utils.getDisclosurePanel(UIContext.Constants.scheduleLabelResyncBackup());		
		cont = new LayoutContainer();
		cont.add(resyncSchedule.Render());	
		cont.add(new Html("<HR>"));	
		panel.add(cont);		
		container.add(panel);	
		
		panel = Utils.getDisclosurePanel(UIContext.Constants.destinationCatalogTitle());
		cont = new LayoutContainer();			
		scheduleCatalogRecoverySetPanel = new CatalogPanel();
		cont.add(scheduleCatalogRecoverySetPanel);
		panel.add(cont);
		container.add(panel);
		
		checkPanel = new ScheduleVMRecoverySetCheckPanel();
		if (settingsGroupType == SettingsGroupType.VMBackupSettings){
			scheduleCatalogRecoverySetPanel.showCheckboxGenerateGRTCatalog(true);
			container.add(checkPanel);
		}
		
		return container;
	}
	
//	private DisclosurePanel getExchageGRTAndSharePointSetting() {
//		
//		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.destinationCatalogTitle());
//		
//		LayoutContainer settingsContainer = new LayoutContainer();
//		
//		LabelField label;		
//
//		
//		TableLayout tableGRTLayout = new TableLayout();
//		
//		tableGRTLayout.setCellPadding(4);
//		tableGRTLayout.setCellSpacing(4);
//		tableGRTLayout.setColumns(1);
//		tableGRTLayout.setWidth("95%");
//		
//		TableData tableGRTData = new TableData();
//		tableGRTData.setPadding(4);
//		
//		LayoutContainer GRTContainer = new LayoutContainer();
//		GRTContainer.setStyleAttribute("padding-left", "8px");
//		GRTContainer.setLayout(tableGRTLayout);
//		
//		if(!parentWindow.isShowForVSphere()) {
//			checkboxGenerateGRTCatalog = new CheckBox();
//			checkboxGenerateGRTCatalog.ensureDebugId("4834f054-3bd8-4ab0-9e33-ff18dbe0ff0d");
//			checkboxGenerateGRTCatalog.setVisible(true);
//			checkboxGenerateGRTCatalog.setBoxLabel(UIContext.Constants.settingsLableGRTEnableAfterBackup());
//			Utils.addToolTip(checkboxGenerateGRTCatalog, UIContext.Constants.backupSettingsGRTEnableTooltip());
//			checkboxGenerateGRTCatalog.setStyleAttribute("white-space", "normal");
//			
//			GRTContainer.add(checkboxGenerateGRTCatalog);
//			checkboxGenerateGRTCatalog.setEnabled(true);
//			// -----end of Exchange GRT Settings ----------
//		}
//		
//		
//		catalogCheckListener = new Listener<BaseEvent>() {
//
//			@Override
//			public void handleEvent(BaseEvent ArchiveEvent) {
//				if(ArchiveEvent.getSource() == cbCatalog)
//				{
//					if(!cbCatalog.getValue() && BackupContext.isFileCopyEnable()){
//						MessageBox mb = new MessageBox();
//						mb.setIcon(MessageBox.WARNING);
//						mb.setButtons(MessageBox.YESNO);
//						mb.setTitle(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
//						mb.setModal(true);
//						mb.setMinWidth(400);
//						mb.setMessage(UIContext.Constants.settingsIFDisableArchive());
//						mb.addCallback(new Listener<MessageBoxEvent>()
//						{
//							public void handleEvent(MessageBoxEvent be)
//							{
//								if (be.getButtonClicked().getItemId().equals(Dialog.NO)) {
//									BackupContext.getArchiveSourceSettings().cbArchiveAfterBackup.setValue(false);
//								}
//								else {
//									cbCatalog.setValue(true);
//								}
//									
//							}
//						});
//						Utils.setMessageBoxDebugId(mb);
//						mb.show();
//					}
//					if( parentWindow.getRefsVolList() != null){
//						if (cbCatalog.getValue())
//						{
//							addWaringIcon();	
//							notificationSet.add(new LabelField(UIContext.Messages.refsVolumesSelect(parentWindow.getRefsVolList())));
//							notificationSet.setVisible(true);
//							notificationSet.expand();	
//							notificationSet.layout(true);
//						}						
//						else
//						{
//							notificationSet.removeAll();
//							notificationSet.setVisible(false);				
//							
//						}
//					}
//				
//				}				
//				
//			}
//		};
//		
//		cbCatalog.ensureDebugId("4834f054-3bd8-4ab0-9e33-ff18dbe0ff0f");
//		cbCatalog.setVisible(true);
//		cbCatalog.setBoxLabel(UIContext.Constants.destinationCatalogLabel());
//		Utils.addToolTip(cbCatalog, UIContext.Constants.destinationCatalogTooltip());
//		cbCatalog.setStyleAttribute("white-space", "normal");
//		cbCatalog.addListener(Events.Change,catalogCheckListener);
//		GRTContainer.add(cbCatalog);
//		
//		
//		notificationSet = new FieldSet();		
//		notificationSet.setHeading(UIContext.Messages.backupSettingsNodifications(1));
//		notificationSet.setCollapsible(true);
//		TableLayout warningLayout = new TableLayout();
//		warningLayout.setWidth("100%");
//		warningLayout.setCellSpacing(1);
//		warningLayout.setColumns(2);
//		notificationSet.setLayout(warningLayout);	
//		if(parentWindow.getRefsVolList() != null)
//		{
//			addWaringIcon();	
//			notificationSet.add(new LabelField(UIContext.Messages.refsVolumesSelect(parentWindow.getRefsVolList())));
//			notificationSet.setVisible(true);
//			notificationSet.expand();		
//		}
//		else
//		{
//			notificationSet.removeAll();
//			notificationSet.setVisible(false);
//		}		
//		GRTContainer.add(notificationSet);	
//		
//		settingsContainer.add(GRTContainer);
//		settingsContainer.add(new Html("<HR>"));
//		//SharePoint Setting
//		//rowContainer.add(new Html("<HR>"));
//		
//		if(!parentWindow.isShowForVSphere()) {
//
//			LayoutContainer tableContainer = new LayoutContainer();
//			TableLayout tableLayout = new TableLayout();
//			tableLayout.setCellPadding(4);
//			tableLayout.setCellSpacing(4);
//			tableLayout.setColumns(2);
//			tableLayout.setWidth("95%");
//			tableContainer.setLayout(tableLayout);
//			
//			label = new LabelField();
//			label.setText(UIContext.Constants.destinationEnableSharePoint());
//			label.addStyleName("restoreWizardSubItem");
//			TableData data = new TableData();
//			data.setColspan(2);
//			tableContainer.add(label, data);
//			
//	        RadioGroup rgSharePoint = new RadioGroup();
//			
//			enableSharePoint = new Radio();
//			enableSharePoint.ensureDebugId("7759A237-2918-4d99-A68E-A8A18D65D10F");
//			enableSharePoint.setBoxLabel(UIContext.Constants.enable());
//			enableSharePoint.setValue(true);
//			rgSharePoint.add(enableSharePoint);
//			tableContainer.add(enableSharePoint);
//			
//			disableSharePoint = new Radio();
//			disableSharePoint.ensureDebugId("2A72EDE8-A72A-4183-8632-655D2BBA55F6");
//			disableSharePoint.setBoxLabel(UIContext.Constants.disable());
//			disableSharePoint.setValue(false);
//			rgSharePoint.add(disableSharePoint);
//			tableContainer.add(disableSharePoint);
//			
//			// hide SharePoint options
//			tableContainer.setVisible(false);
//			settingsContainer.add(tableContainer);
//		}
//		panel.add(settingsContainer);
//		return panel;
//	}
	
	
	
	public void RefreshData(BackupSettingsModel model) {

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
			
			if(parentWindow.isForEdge() && model.startTime != null) {
				startTimeContainer.setUserStartTime(model.startTime);
			}else {
				if(model.getStartTimezoneOffset() != null)
					startTimeContainer.setStartDateTime(backupStartTime, model.getStartTimezoneOffset());
				else 
					startTimeContainer.setStartDateTime(backupStartTime);
			}
			
			fullSchedule.RefreshData(model.fullSchedule);
			incrementalSchedule.RefreshData(model.incrementalSchedule);
			resyncSchedule.RefreshData(model.resyncSchedule);	
			
			scheduleCatalogRecoverySetPanel.applyValue(model);
			checkPanel.applyValue(model);
		}
		catch (Exception e)	{}		
	}
	public void Save(long timeZoneOffset)
	{
		if (container == null || !container.isRendered())
			return;
		
		BackupScheduleModel fullModel = fullSchedule.Save();
		BackupScheduleModel incModel = incrementalSchedule.Save();
		BackupScheduleModel resyncModel = resyncSchedule.Save();
		
		Date selectedDate = startTimeContainer.getStartDateTime(timeZoneOffset);
		
		SettingPresenter.model.setBackupStartTime(selectedDate.getTime());
		SettingPresenter.model.startTime = startTimeContainer.getUserSetTime();
		
		SettingPresenter.model.fullSchedule = fullModel;
		SettingPresenter.model.incrementalSchedule = incModel;
		SettingPresenter.model.resyncSchedule = resyncModel;
		
		scheduleCatalogRecoverySetPanel.buildValue(SettingPresenter.model);


	}
	public boolean Validate()
	{
		if (container == null || !container.isRendered())
			return true;
		
		return startTimeContainer.getStartDateTime() != null && 
		   (incrementalSchedule.Validate() && fullSchedule.Validate() 
				   && resyncSchedule.Validate());
	}
	
	public void setEditable(boolean isEditable){
		startTimeContainer.setEnabled(isEditable);
		incrementalSchedule.setEditable(isEditable);
		fullSchedule.setEditable(isEditable);
		resyncSchedule.setEditable(isEditable);
		this.scheduleCatalogRecoverySetPanel.setEditable(isEditable);
	}
	
	public D2DTimeModel getUserSetTime() {
		return startTimeContainer.getUserSetTime();
	}
}
