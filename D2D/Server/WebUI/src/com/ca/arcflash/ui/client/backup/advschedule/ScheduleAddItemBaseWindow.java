package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.OnLineHelpTopics;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;

public abstract class ScheduleAddItemBaseWindow extends BaseDetailItemWindow<ScheduleItemModel>{
	protected ScheduleItemModel model;
	protected boolean isNewAdd;
	protected Button OKButton;
	protected SelectionListener<ButtonEvent> OKButtonListener;
	public abstract ScheduleItemModel save();
	public abstract boolean validate();
	public abstract void updateData();
	
	public ScheduleAddItemBaseWindow(ScheduleItemModel model, boolean isNewAdd) {
		this.model = model;
		this.isNewAdd = isNewAdd;
		this.setResizable(false);
		this.ensureDebugId("28866937-2d84-4a4b-aa41-547a5a7bb0ff");
		String windowHeaderString = isNewAdd? getScheduleAddWindowHeader(): getScheduleEditWindowHeader();
		this.setHeadingHtml(windowHeaderString);				
		
		OKButton = new Button(UIContext.Constants.remoteDeployPanelSave());
		OKButton.ensureDebugId("02f54fa2-520c-406b-b9e9-b76d5c346b83");
		
		Button cancelButton = new Button(UIContext.Constants.backupSettingsCancel());
		cancelButton.ensureDebugId("8d32647c-e7e5-4393-8abd-cb3c9bff1d1a");
		cancelButton.addStyleName("ca-tertiaryText");
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		
		Button helpButton = new Button(UIContext.Constants.help());
		helpButton.ensureDebugId("ee1532f0-fb2e-410d-87bd-ddf25c375f44");
		helpButton.addStyleName("ca-tertiaryText");
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				String URL = getScheduleHelpURL();
				OnLineHelpTopics.showHelpURL(URL);
			}
		});
		this.setButtonAlign(HorizontalAlignment.LEFT);
		this.addButton(helpButton);
		this.getButtonBar().add(new FillToolItem());
		this.addButton(OKButton);
		this.addButton(cancelButton);

	}
	
	protected String getScheduleAddWindowHeader(){
		return UIContext.Constants.scheduleAddWindowHeader();
	}
	
	protected String getScheduleEditWindowHeader(){
		return UIContext.Constants.scheduleEditWindowHeader();
	}
	
	protected String getScheduleHelpURL(){
		return UIContext.externalLinks.getBackupSettingAdvanceScheduleHelp();
	}

	public void setOKButtonListener(SelectionListener<ButtonEvent> OKButtonListener){
		this.OKButtonListener = OKButtonListener;
	}
		
}
