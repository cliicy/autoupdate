package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;

public class BackupNowWindow extends Window{
	protected final CommonServiceAsync service = GWT.create(CommonService.class);
	protected Window window;
	protected Radio radioFull;
	protected Radio radioIncremental;
	protected Radio radioResync;
	protected Button okButton;
	private RadioGroup radioGroup;
	protected TextField<String> nameTextField;
	protected LabelField warningLabel;
	private BackupNowKeyListener keyListener =  new BackupNowKeyListener();
	protected String lastSelectedID;
	protected Button cancelButton;
	
	public BackupNowWindow(){
		this.window = this;
		this.setWidth(440);
		this.setResizable(false);
		this.setModal(true);
		this.setHeadingHtml(UIContext.Constants.backupNowWindowHeading());

		nameTextField = new TextField<String>();
		nameTextField.ensureDebugId("85252059-6e5a-42bb-b60e-d50ec6f0b3be");

		TableLayout layout = new TableLayout();
		layout.setWidth("95%");
		layout.setCellPadding(4);
		layout.setCellSpacing(4);
		this.setLayout(layout);
		
		warningLabel = new LabelField();
		warningLabel.setVisible(false);
		warningLabel.setValue(UIContext.Constants.backupNowWindowCompressionChanged());
		this.add(warningLabel);
		
		radioGroup = new RadioGroup();
		
		radioIncremental = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				updateNameField();
			}
			
		};
		radioIncremental.ensureDebugId("98899cbe-7d64-4d11-9f06-625a623ac0ae");
		radioIncremental.setId("BackupNow_Radio_Incremental");
		radioIncremental.setBoxLabel(UIContext.Constants.backupNowWindowIncremental());
		radioIncremental.addKeyListener(keyListener);
		this.add(radioIncremental);
		radioGroup.add(radioIncremental);
		
		radioResync = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				updateNameField();
			}
			
		};
		radioResync.ensureDebugId("2d2aaada-606c-43ed-ae2f-f700e678064d");
		radioResync.setId("BackupNow_Radio_Resync");
		radioResync.setBoxLabel(UIContext.Constants.backupNowWindowResync());
		radioResync.addKeyListener(keyListener);
		this.add(radioResync);
		radioGroup.add(radioResync);
		
		radioFull = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				updateNameField();
			}
			
		};
		radioFull.ensureDebugId("dc6e5c48-7879-40bb-92e3-59fa732e8692");
		radioFull.setId("BackupNow_Radio_Full");
		radioFull.setBoxLabel(UIContext.Constants.backupNowWindowFull());
		radioFull.addKeyListener(keyListener);

		this.add(radioFull);
		radioGroup.add(radioFull);
		//There is a bug in gxt for event change listener, so we override onClick to do that.
//			radioGroup.addListener(Events.Change, new Listener<FieldEvent>(){
//				@Override
//				public void handleEvent(FieldEvent be) {
//					
//					if (radioGroup.getValue().getId().equals(radioIncremental.getId()))
//						nameTextField.setValue(UIContext.Constants.backupNowWindowIncrementalName());
//					else if (radioGroup.getValue().getId().equals(radioResync.getId()))
//						nameTextField.setValue(UIContext.Constants.backupNowWindowResyncName());
//					else if (radioGroup.getValue().getId().equals(radioFull.getId()))
//						nameTextField.setValue(UIContext.Constants.backupNowWindowFullName());
//				}			
//			});
		//radioGroup.setValue(radioFull);
		radioGroup.setValue(radioIncremental);
		lastSelectedID = radioIncremental.getId();
		
		LayoutContainer container = new LayoutContainer();
		TableLayout rowLayout = new TableLayout();
		rowLayout.setColumns(2);
		rowLayout.setWidth("100%");
		container.setLayout(rowLayout);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupNowWindowNameLabel());
		label.setWidth("100%");
		TableData td = new TableData();
		td.setWidth("25%");
		container.add(label,td);
		
		//nameTextField.setValue(UIContext.Constants.backupNowWindowFullName());
		nameTextField.setValue(UIContext.Constants.backupNowWindowIncrementalName());
		nameTextField.setAllowBlank(false);
		nameTextField.setMaxLength(128);
		nameTextField.addKeyListener(keyListener);
		nameTextField.setWidth("100%");
		// nameTextField.setWidth(290);
		td = new TableData();
		td.setWidth("75%");
		Utils.addToolTip(nameTextField, UIContext.Constants.backupNowWindowNameTooltip());
		/*nameTextField.addListener(Events.Change, new Listener<FieldEvent>(){

			@Override
			public void handleEvent(FieldEvent be) {
				if(!nameTextField.validate()){
					okButton.disable();
				}else{
					okButton.enable();
				}
			}
			
		});*/
		container.add(nameTextField, td);
		
		this.add(container);
		cancelButton = new Button();
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.ensureDebugId("a98a780c-5d19-4689-8b54-01e513247ba3");
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					window.hide();
				}
			});
		
		okButton = new Button();
		okButton.setText(UIContext.Constants.ok());
		okButton.ensureDebugId("5998697c-aa6a-475a-83c0-fecbc30ce5ea");
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					if (!nameTextField.validate()){
						return;
					}
					
					okButton.setEnabled(false);
					cancelButton.setEnabled(false);
					
					int backupType = BackupTypeModel.Full;
					
					if (radioIncremental.getValue())
						backupType = BackupTypeModel.Incremental;
					else if (radioResync.getValue())
						backupType = BackupTypeModel.Resync;
					
					doBackup(backupType, true);
				}
			});
		
		Button helpButton = HelpTopics.createHelpButton(getHelpButtonURL(), -1);
		helpButton.ensureDebugId("09b46422-4189-415c-ad28-9cfe3d51f6c9");
		
		helpButton.addStyleName("ca-tertiaryText");
		cancelButton.addStyleName("ca-tertiaryText");
		this.setButtonAlign(HorizontalAlignment.LEFT);
		this.addButton(helpButton);
		this.getButtonBar().add(new FillToolItem());
		this.addButton(okButton);
		this.addButton(cancelButton);
	}

	protected String getHelpButtonURL() {
		return UIContext.externalLinks.getBackUpNowHelp();
	}

	public void changeSettings(boolean isCompressionLevelChagned) {
		if (isCompressionLevelChagned == true) {
			radioIncremental.setEnabled(false);
			radioResync.setEnabled(false);
			radioFull.setValue(true);
			nameTextField.setValue(UIContext.Constants
					.backupNowWindowFullName());
			warningLabel.setVisible(true);
			lastSelectedID = radioFull.getId();
		} else {
			// radioFull.setValue(true);
			radioIncremental.setValue(true);
			lastSelectedID = radioIncremental.getId();
		}
	}

	@Override
	protected void onRender(Element target, int index) {
		  super.onRender(target, index);
		  this.setFocusWidget(radioIncremental);
		  radioIncremental.focus();
		  //this.setFocusWidget(radioFull);
		  //radioFull.focus();
	  }	
	
	public void setKeyListener(BackupNowKeyListener keyListener) {
		this.keyListener = keyListener;
	}

	public BackupNowKeyListener getKeyListener() {
		return keyListener;
	}

	class BackupNowKeyListener extends KeyListener{

		@Override
		public void componentKeyPress(ComponentEvent event) {
			if (event.getKeyCode() == KeyCodes.KEY_ENTER)
				okButton.fireEvent(Events.Select);
		}
		
	}
	
	private void updateNameField(){
		if(radioGroup.getValue().getId().equals(lastSelectedID)){
			return;
		}
		
		if (radioGroup.getValue().getId().equals(radioIncremental.getId()))
			nameTextField.setValue(UIContext.Constants.backupNowWindowIncrementalName());
		else if (radioGroup.getValue().getId().equals(radioResync.getId()))
			nameTextField.setValue(UIContext.Constants.backupNowWindowResyncName());
		else if (radioGroup.getValue().getId().equals(radioFull.getId()))
			nameTextField.setValue(UIContext.Constants.backupNowWindowFullName());
		
		lastSelectedID = radioGroup.getValue().getId();
	}
	
	protected void doBackup(final int backupType, final boolean convert) {
		service.backup(backupType, nameTextField.getValue(), convert, new BaseAsyncCallback<Void>(){

			@Override
			public void onFailure(Throwable caught) {
				if(caught instanceof BusinessLogicException 
						&& "68719476738".equals(((BusinessLogicException)caught).getErrorCode())) {
					MessageBox.confirm(UIContext.Messages.messageBoxTitleInformation(
							Utils.getProductName()), ((BusinessLogicException)caught).getDisplayMessage(), 
							new Listener<MessageBoxEvent>(){
						@Override
						public void handleEvent(MessageBoxEvent be) {
							if(be.getButtonClicked().getItemId().equals(Dialog.YES)){
								doBackup(BackupTypeModel.Full, convert);
							}else {
								doBackup(backupType, false);
							}
						}
					});
				}else if(caught instanceof BusinessLogicException
						&& ("4294967315".equals(((BusinessLogicException)caught).getErrorCode()))) {
					MessageBox.info(UIContext.Messages.messageBoxTitleInformation(
							Utils.getProductName()), 
							((BusinessLogicException)caught).getDisplayMessage(), null);
					window.hide();
				}else {
					//issue 71572, need to hide the window before display the exception
					window.hide();
					super.onFailure(caught);
//					okButton.setEnabled(true);
//					cancelButton.setEnabled(true);
				}
			}

			@Override
			public void onSuccess(Void result) {
				Info.display(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.backupNowWindowSubmitSuccessful());
				window.hide();
			}
			
		});
	}
}
