package com.ca.arcflash.ui.client.vsphere.setting;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class VSphereBackupSettingsContentForLiteLT extends
		VSphereBackupSettingContent {
	private Button okButton;
	private Button cancelButton;
	
	public VSphereBackupSettingsContentForLiteLT()
	{
		outerThis = this;
	}
	
	@Override
	protected void setLayout(LayoutContainer contentPanel) {
		this.setLayout(new TableLayout(1));
		contentPanel.setLayout(new TableLayout(2));
	}
	
	protected void addPanels(LayoutContainer contentPanel) {
		TableData table = new TableData(Style.HorizontalAlignment.LEFT, Style.VerticalAlignment.TOP);
		table.setWidth("120");
		table.setHeight("100%");
		contentPanel.add( toggleButtonPanel, table);
		contentPanel.add( deckPanel, new TableData(Style.HorizontalAlignment.LEFT, Style.VerticalAlignment.TOP));
		this.add(contentPanel, new TableData(Style.HorizontalAlignment.LEFT, Style.VerticalAlignment.TOP) );
	}
	
	private void addButtons() {
		LayoutContainer buttonContainer = new LayoutContainer();
		buttonContainer.setStyleAttribute("background-color","#DFE8F6");
		//buttonContainer.setHeight(80);
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(4);
		tableLayout.setCellSpacing(4);
		tableLayout.setColumns(4);		
		
		//Repeat Section
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		td.setWidth("100%");
		buttonContainer.setLayout(tableLayout);				
	
		LabelField leftSpace = new LabelField();
		buttonContainer.add(leftSpace, td);
	
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		
		okButton = new Button();
		okButton.setMinWidth(80);
		okButton.setText(UIContext.Constants.backupSettingsOk());
		okButton.ensureDebugId("93888763-EB4F-455f-944A-37B546E9AF18");
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				
			}
		
		});		
		buttonContainer.add(okButton, td);
		
		cancelButton = new Button();	
		cancelButton.setMinWidth(80);
		cancelButton.ensureDebugId("A6A2BBE6-344D-4abf-B028-A47EA4F8DF12");
		cancelButton.setText(UIContext.Constants.backupSettingsCancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				//Cancel Clicked hide the dialog 				
				
					final Listener<MessageBoxEvent> messageBoxHandler = new Listener<MessageBoxEvent>() {
					public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId().equals(Dialog.YES))
							{
								contentHost.close();
							}
						}
					};
					
					MessageBox mb = new MessageBox();
					mb.setIcon(MessageBox.WARNING);
					mb.setButtons(MessageBox.YESNO);
					mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameD2D));
					mb.setMessage(UIContext.Constants.backupSettingExistAlert());
					mb.addCallback(messageBoxHandler);
					Utils.setMessageBoxDebugId(mb);
					mb.show();
			}
		
		});		
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);		
		buttonContainer.add(cancelButton, td);
		
		Button helpButton = new Button();
		helpButton.ensureDebugId("598D3BFA-C461-45f2-A607-A7A5F6BBD3FC");
		helpButton.setMinWidth(80);
		helpButton.setText(UIContext.Constants.help());
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			private String url = UIContext.externalLinks.getBackupSettingsHelp();

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(destinationButton.isDown())
					url = UIContext.externalLinks.getBackupSettingDestinationHelp();
				else if(scheduleButton.isDown())
					url = UIContext.externalLinks.getBackupSettingScheduleHelp();
				else if(settingsButton.isDown())
					url = UIContext.externalLinks.getBackupSettingSettingsHelp();
				else if(advancedButton.isDown())
					url = UIContext.externalLinks.getBackupSettingAdvancedHelp();

				HelpTopics.showHelpURL(url);
			}
		});
		
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		buttonContainer.add(helpButton, td);
	
		this.add(buttonContainer, new TableData(Style.HorizontalAlignment.LEFT, Style.VerticalAlignment.TOP) );
	}
	
	@Override
	public void enableEditing(boolean isEnabled) {
		super.enableEditing(isEnabled);
		if (this.okButton != null)
			this.okButton.setEnabled( isEnabled );
	}
	
	protected void doInitialization() {
		super.doInitialization();
		addButtons();
	}		
}
