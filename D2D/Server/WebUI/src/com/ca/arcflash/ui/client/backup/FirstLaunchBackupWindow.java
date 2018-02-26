package com.ca.arcflash.ui.client.backup;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;

public class FirstLaunchBackupWindow extends Window{
	private final CommonServiceAsync service = GWT.create(CommonService.class);
	private Window window;
	private Button okButton;
	private int firstLaunchBackupType = BackupTypeModel.Full;
	private HTML warningHtml;
	private Date startTime;
	private Date currentTime;
	
	public int getFirstLaunchBackupType() {
		return firstLaunchBackupType;
	}

	public void setFirstLaunchBackupType(int firstLaunchBackupType) {
		this.firstLaunchBackupType = firstLaunchBackupType;
	}

	public FirstLaunchBackupWindow(){
		this.window = this;
		this.setWidth(400);
		this.setResizable(false);
		this.setHeadingHtml(UIContext.Constants.fisrtLaunchWindowTitle());

		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setCellPadding(4);
		layout.setCellSpacing(4);
		this.setLayout(layout);
		TableData td = new TableData();
		td.setWidth("80%");
		td.setHeight("80%");
		warningHtml = new HTML();
		
		this.add(warningHtml,td);
		

		final Button cancelButton = new Button();
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					window.hide();
				}
			});
		
		okButton = new Button();
		okButton.ensureDebugId("5B84DDE5-0151-49e6-984D-18689D5A5A29");
		okButton.setText(UIContext.Constants.ok());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {

					
					service.backup(firstLaunchBackupType,  UIContext.Constants.firstLaunchedJobName(),  new BaseAsyncCallback<Void>(){

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							okButton.setEnabled(true);
							cancelButton.setEnabled(true);
						}

						@Override
						public void onSuccess(Void result) {
							MessageBox box = MessageBox.info(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.backupNowWindowSubmitSuccessful(), null);
							Utils.setMessageBoxDebugId(box);
							window.hide();
						}
						
					});
				}
			});
		
		this.addButton(okButton);
		this.addButton(cancelButton);

	}

	public void setStartTime(Date date) {
		this.startTime = date;
		
	}

	public void setCurrentTime(Date date) {
		this.currentTime = date;
		
	}
	public void setHtmlText(){

		String start = Utils.formatTimeToServerTime(startTime);
		//String current =Utils.formatTimeToServerTime(currentTime);
		
		String str = Format.substitute( UIContext.Constants.firstJobDescription(),new Object[]{start});
		this.warningHtml.setHTML(str);

	}
}
