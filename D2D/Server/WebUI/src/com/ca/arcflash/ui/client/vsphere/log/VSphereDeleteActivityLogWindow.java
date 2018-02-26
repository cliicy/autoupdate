package com.ca.arcflash.ui.client.vsphere.log;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyManager;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.FixedDatePicker;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.log.LogWindow;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class VSphereDeleteActivityLogWindow extends Window{
	
	final CommonServiceAsync service = GWT.create(CommonService.class);
	final ColdStandbyServiceAsync coldStandbyServiceAsyc = GWT.create(ColdStandbyService.class);
	public Window window;
	public boolean deleted;
	public Radio deleteAllRadio;
	public Radio deleteByDateRadio;
	//It is true when 1. login vSphere vcm, 2. login VCM monitor and select a vSphere VM on the tree.
	private boolean isOnVCMTab = false;
	
	public VSphereDeleteActivityLogWindow(boolean isOnVCMTab){
		this.isOnVCMTab = isOnVCMTab;
		initialize();
	}
	
	public VSphereDeleteActivityLogWindow(){
		initialize();
	}

	private void initialize() {
		this.window = this;
		this.setHeadingHtml(UIContext.Constants.activityLogWindowHeader());
		this.setClosable(false);
		this.setHeight(330);
		this.setWidth(380);
		this.setResizable(false);
		
		LayoutContainer container = new LayoutContainer();
		container.setAutoHeight(true);
		container.setAutoWidth(true);
		container.setStyleAttribute("padding", "8px");
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(1);
		container.setLayout(tableLayout);
		
		TableData tableData = new TableData();
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		RadioGroup radioGroup = new RadioGroup();
		
		deleteAllRadio = new Radio();
		deleteAllRadio.ensureDebugId("31FBDE2D-9DC0-4559-8E9A-5CEA035ACFAA");
		deleteAllRadio.setName("activityLog_Delete_Type");
		deleteAllRadio.setBoxLabel(UIContext.Constants.radioButtonDeleteAllLabel());
		container.add(deleteAllRadio, tableData);
		
		deleteByDateRadio = new Radio();
		deleteByDateRadio.ensureDebugId("7CA3F0FD-5E1C-421c-9458-2483D699AFD5");
		deleteByDateRadio.setLabelStyle("wrap: word-break;");
		deleteByDateRadio.setStyleAttribute("padding-top", "8px");
		deleteByDateRadio.setName("activityLog_Delete_Type");
		deleteByDateRadio.setBoxLabel(UIContext.Constants.radioButtonDeleteByDateLabel());
		
		tableData = new TableData();
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		container.add(deleteByDateRadio, tableData);
		
		final DatePicker field = new FixedDatePicker();
		field.ensureDebugId("8801A36F-4D03-40d2-96CE-33D397797483");
		field.setMaxDate(Utils.maxDate);
		field.setMinDate(Utils.minDate);
		field.setValue(new Date());
		field.setEnabled(false);

		tableData = new TableData();
		tableData.setStyle("padding-left:20px");
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		container.add(field, tableData);
		
		radioGroup.add(deleteByDateRadio);
		radioGroup.add(deleteAllRadio);
		
		deleteByDateRadio.addListener(Events.Change, new Listener<FieldEvent>()
				{
					@Override
					public void handleEvent(FieldEvent be) {
						Boolean checked = deleteByDateRadio.getValue();
						field.setEnabled(checked);
					}			
				});
		
		deleteAllRadio.addListener(Events.Change, new Listener<FieldEvent>()
				{
					@Override
					public void handleEvent(FieldEvent be) {
						Boolean checked = deleteAllRadio.getValue();
						field.setEnabled(!checked);
					}			
				});
		
		setDatePickerValue(field);
		
		Button okButton = new Button();
		okButton.ensureDebugId("04DC9ADD-64D2-4119-B881-782831E25CE7");
		okButton.setText(UIContext.Constants.ok());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				MessageBox mb = new MessageBox();
				mb.setIcon(MessageBox.WARNING);
				mb.setButtons(MessageBox.YESNO);
				mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNamevSphere));
				mb.setMessage(UIContext.Constants.activityLogDeleteConfirm());
				mb.addCallback(new Listener<MessageBoxEvent>()
				{
					public void handleEvent(MessageBoxEvent be)
					{
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							deleteLog(field);
						}
					}

				});
				Utils.setMessageBoxDebugId(mb);
				mb.show();
			}

			});
		
		Button cancelButton = new Button();
		cancelButton.ensureDebugId("9BBCC7D4-6F9B-4dec-90CB-D7A15E006BD8");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}

			});
		
		this.addButton(okButton);
		this.addButton(cancelButton);
		
		
		
		this.add(container);
	}
	
	public void deleteLog(final DatePicker field) {
		Date date = null;
		if (deleteByDateRadio.getValue())
		{
			date = field.getValue();
			date = Utils.serverTimeToLocalTime(date);
		}
		
		window.setEnabled(false);
		BackupVMModel backupVM = UIContext.backupVM;
		BaseAsyncCallback<Void> callback = new BaseAsyncCallback<Void>(){
				
				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
					window.setEnabled(true);
					window.hide();
				}

				@Override
				public void onSuccess(Void result) {
					window.setEnabled(true);
					deleted = true;
					window.hide();
				}
				
			};
		if(isOnVCMTab && backupVM == null && ColdStandbyManager.getVMInstanceUUID() != null) {
			backupVM = new BackupVMModel();
			backupVM.setVmInstanceUUID(ColdStandbyManager.getVMInstanceUUID());
			coldStandbyServiceAsyc.deleteVMActivityLog(date, backupVM, callback);
		}
		else
			service.deleteVMActivityLog(date, backupVM, callback);
	}

	public void setDatePickerValue(final DatePicker field) {
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.getServerTime(new BaseAsyncCallback<Date>() {
			@Override
			public void onSuccess(Date result) {
				Date serverDate = Utils.localTimeToServerTime(result);
				field.setValue(serverDate);
			}
		});
	}

	public boolean isDeleted() {
		return deleted;
	}

	@Override
	protected void afterShow() {
		super.afterShow();
		this.setFocusWidget(deleteAllRadio);
		deleteAllRadio.setValue(true);
	}

	
	
}
