package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseCommonSettingTab;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.Image;

public class UpdateUserNamePassWindow extends Window {
	LoginServiceAsync loginService = GWT.create(LoginService.class);
	private UpdateUserNamePassWindow thisWindow;
	private TextField<String> nameTextField;
	private PasswordTextField passwordTextField;
	private Button okButton;
	private Button cancelButton;
	private String path;
	private int MIN_BUTTON_WIDTH = 90;
	private CredKeyListener keyListener = new CredKeyListener();
	public static final String ICON_LOADING = "images/gxt/icons/grid-loading.gif";
	private Image image = new Image(ICON_LOADING);
	private LabelField pathLabel = null;
	
	private BackupSettingsContent backupSettingsWindow;
	
	public UpdateUserNamePassWindow(String destination) {
		this.thisWindow = this;
		this.path = destination;
		this.setResizable(false);
		this.setWidth(380);
		// this.setHeading();
		this.setHeadingHtml(UIContext.Constants.loginConnectWindow());

		TableLayout layout = new TableLayout();
		layout.setWidth("95%");
		layout.setColumns(3);
		layout.setCellPadding(4);
		layout.setCellSpacing(0);
		this.setLayout(layout);

		TableData tableData = new TableData();
		tableData.setColspan(3);

		// Table section just for the loading icon and
		LayoutContainer lc = new LayoutContainer();
		TableLayout subTable = new TableLayout();
		subTable.setWidth("100%");
		subTable.setColumns(2);
		lc.setLayout(subTable);
		TableData td = new TableData();
		td.setWidth("0%");
		image.setVisible(false);
		lc.add(image, td);
		
		td = new TableData();
		td.setWidth("100%");

		pathLabel = new LabelField();
		pathLabel.setValue(UIContext.Messages.connectToNetworkPath(path));
		pathLabel.addStyleName("restoreWizardSubItem");
		lc.add(pathLabel, td);

		this.add(lc, tableData);

		tableData = new TableData();
		
		tableData.setWidth("30%");

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.loginLabelUsername());
		label.addStyleName("connectDialogSpacing");
		this.add(label, tableData);

		tableData = new TableData();
		tableData.setColspan(2);
		tableData.setWidth("70%");
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);

		nameTextField = new TextField<String>();
		nameTextField.ensureDebugId("94CD8DA7-10F0-438f-8F2A-5D51D053C30E");
		nameTextField.setWidth("100%");
		nameTextField.addKeyListener(keyListener);
		this.add(nameTextField, tableData);

		nameTextField.setAllowBlank(false);
		nameTextField.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value == null
						|| value.indexOf('\\') < 1
						|| (value.trim().indexOf('\\') == (value.trim()
								.length() - 1))) {
					return UIContext.Constants
							.backupSettingsDestInvalidUserName();
				}

				return null;
			}
		});

		label = new LabelField();
		label.setValue(UIContext.Constants.loginLabelPassword());
		label.addStyleName("connectDialogSpacing");
		tableData = new TableData();
		tableData.setWidth("30%");
		this.add(label, tableData);

		passwordTextField = new PasswordTextField();
		passwordTextField.ensureDebugId("E0E0B932-777F-42c5-A71F-9184E6DD4B2C");
		passwordTextField.setPassword(true);
		passwordTextField.addKeyListener(keyListener);
		passwordTextField.setWidth("100%");
		tableData = new TableData();
		tableData.setColspan(2);
		tableData.setWidth("70%");
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		this.add(passwordTextField, tableData);

		tableData = new TableData();
		tableData.setColspan(3);
		label = new LabelField();
		label.setValue(UIContext.Constants.loginUsernameExample());
		label.addStyleName("connectDialogSpacing");
		this.add(label, tableData);

		this.add(new Html(""));

		okButton = new Button();
		okButton.ensureDebugId("675CA163-8F4C-408b-A248-413AFC8AEE3A");
		okButton.setText(UIContext.Constants.ok());
		okButton.setMinWidth(MIN_BUTTON_WIDTH);
		this.add(okButton, new TableData(Style.HorizontalAlignment.RIGHT,
				Style.VerticalAlignment.MIDDLE));

		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				nameTextField.disable();
				passwordTextField.disable();
				image.setVisible(true);
				okButton.disable();
				String path = thisWindow.path;
				String domain = "";
				String user = nameTextField.getValue();
				String pwd = passwordTextField.getValue();

				if (path == null)
					path = "";

				if (user == null)
					user = "";

				if (pwd == null)
					pwd = "";

				path = path.trim();
				if (path.endsWith("\\") || path.endsWith("/")) {
					path = path.substring(0, path.length() - 1);
				}
				
				loginService.updateDestAccess(path, user, pwd, domain, new BaseAsyncCallback<String>(){

					@Override
					public void onFailure(Throwable caught) {
						image.setVisible(false);
						super.onFailure(caught);
						nameTextField.enable();
						nameTextField.clear();
						passwordTextField.enable();
						passwordTextField.clear();
						okButton.enable();
					}

					@Override
					public void onSuccess(String result) {
						if(backupSettingsWindow!=null){
							sychronizeBackupDestination(result);
						}
						image.setVisible(false);
						if(result == null || result.isEmpty()) {
							MessageBox box = MessageBox.info(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
											UIContext.Constants.settingsConnectionFine(),null);
							Utils.setMessageBoxDebugId(box);
							thisWindow.hide();
						}else {
							MessageBox box = MessageBox.info(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
											UIContext.Messages.settingsUpdateConnection(result),null);
							Utils.setMessageBoxDebugId(box);
							pathLabel.setValue(result);
							nameTextField.enable();
							nameTextField.clear();
							passwordTextField.enable();
							passwordTextField.clear();
							okButton.enable();
							thisWindow.path = result;
						}
					}
					
				});
			}

		});

		cancelButton = new Button();
		cancelButton.ensureDebugId("5A84ED29-D3A4-46c0-936A-F3DF311E2F07");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.setMinWidth(MIN_BUTTON_WIDTH);
		this.add(cancelButton, new TableData(Style.HorizontalAlignment.RIGHT,
				Style.VerticalAlignment.MIDDLE));

		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}

		});
	}
	
	private class CredKeyListener extends KeyListener {
		@Override
		public void componentKeyPress(ComponentEvent event) {
			if (event.getKeyCode() == KeyCodes.KEY_ENTER)
				okButton.fireEvent(Events.Select);
		}
	}
	private void sychronizeBackupDestination(String result) {
		if(!backupSettingsWindow.getDestination().getPathSelectionPanel().getDestination().equals(result)){
			String user = nameTextField.getValue();
			String pwd = passwordTextField.getValue();
			SettingPresenter.model.setDestUserName(user);
			SettingPresenter.model.setDestPassword(pwd);
			backupSettingsWindow.getDestination().getPathSelectionPanel().setUsername(user);
			backupSettingsWindow.getDestination().getPathSelectionPanel().setPassword(pwd);
			BaseCommonSettingTab tab=(BaseCommonSettingTab)backupSettingsWindow.contentHost;
			tab.getD2dSettings().getBackupSettingsModel().setDestUserName(user);
			tab.getD2dSettings().getBackupSettingsModel().setDestPassword(pwd);
		}
	}
	public void setBackupSettingsWindow(BackupSettingsContent backupSettingsWindow) {
		this.backupSettingsWindow=backupSettingsWindow;
	}
}
