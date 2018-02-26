package com.ca.arcflash.ui.client.log;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FixedDatePicker;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
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

public class DeleteActivityLogWindow extends Window{
	
	private Window window;
	private boolean deleted;
	private Radio deleteAllRadio;
	private Radio deleteByDateRadio;
	
	public DeleteActivityLogWindow(){
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
		deleteAllRadio.ensureDebugId("a67d3b3d-2965-4ffe-a0b7-1c8256a88511");
		deleteAllRadio.setName("activityLog_Delete_Type");
		deleteAllRadio.setBoxLabel(UIContext.Constants.radioButtonDeleteAllLabel());
		container.add(deleteAllRadio, tableData);
		
		deleteByDateRadio = new Radio();
		deleteByDateRadio.ensureDebugId("fc5916b5-d3e7-4336-b774-9602fa411190");
		deleteByDateRadio.setLabelStyle("wrap: word-break;");
		deleteByDateRadio.setStyleAttribute("padding-top", "8px");
		deleteByDateRadio.setName("activityLog_Delete_Type");
		deleteByDateRadio.setBoxLabel(UIContext.Constants.radioButtonDeleteByDateLabel());
		
		tableData = new TableData();
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		container.add(deleteByDateRadio, tableData);
		
		final DatePicker field = new FixedDatePicker();
		field.ensureDebugId("082cdfda-7597-4d48-82b1-75b0151c8eee");
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
		
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.getServerTime(new BaseAsyncCallback<Date>() {
			@Override
			public void onSuccess(Date result) {
				Date serverDate = Utils.localTimeToServerTime(result);
				field.setValue(serverDate);
			}
		});
		
		Button okButton = new Button();
		okButton.ensureDebugId("d40da367-224c-4747-a713-0849b5bf3300");
		okButton.setText(UIContext.Constants.ok());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				String title = LogWindow.LogOperationHelper.isForD2D == true ? UIContext.productNameD2D:UIContext.productNameVCM;
				MessageBox mb = new MessageBox();
				mb.setIcon(MessageBox.WARNING);
				mb.setButtons(MessageBox.YESNO);
				mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(title));
				mb.setMessage(UIContext.Constants.activityLogDeleteConfirm());
				Utils.setMessageBoxDebugId(mb);
				mb.addCallback(new Listener<MessageBoxEvent>()
				{
					public void handleEvent(MessageBoxEvent be)
					{
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							Date date = null;
							if (deleteByDateRadio.getValue())
							{
								date = field.getValue();
								date = Utils.serverTimeToLocalTime(date);
							}
							
							window.setEnabled(false);
							LogWindow.LogOperationHelper.deleteActivityLog(date, new BaseAsyncCallback<Void>(){

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
								
							});
						}
					}
				});
				mb.show();
			}

			});
		
		Button cancelButton = new Button();
		cancelButton.ensureDebugId("10fc942a-cd1b-45f3-973c-8ad3f797a8a9");
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
