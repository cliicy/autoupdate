package com.ca.arcflash.ui.client.common;


import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class UserPasswordWindow extends Window {
	final CommonServiceAsync service = GWT.create(CommonService.class);
	private UserPasswordWindow thisWindow;
	private TextField<String> nameTextField;
	private PasswordTextField passwordTextField;
	private Button okButton;
	private Button cancelButton;
	private String path;
	private boolean isClickOK = false;
	private CredKeyListener keyListener = new CredKeyListener();
	
	public static final String ICON_LOADING = "images/gxt/icons/grid-loading.gif";
	private Image image = new Image(ICON_LOADING);// IconHelper.create(ICON_LOADING,
													// 16, 16).createImage();
	private int mode = 0;
	private int MIN_BUTTON_WIDTH = 90;


	public UserPasswordWindow(String path, String username, String password) {
		this.thisWindow = this;
		this.path = path;
		this.setResizable(false);
		this.setWidth(400);
//		this.setAutoWidth(true);
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

		LabelField label = new LabelField();
		label.setValue(UIContext.Messages.connectToNetworkPath(path));
		label.addStyleName("restoreWizardSubItem");
		label.setStyleAttribute("word-wrap", "break-word");
		label.setStyleAttribute("word-break", "break-all");
		//label.setWidth(320);
		lc.add(label, td);

		this.add(lc, tableData);

		tableData = new TableData();
		tableData.setWidth("30%");

		label = new LabelField();
		label.setValue(UIContext.Constants.loginLabelUsername());
		label.addStyleName("connectDialogSpacing");
		this.add(label, tableData);

		tableData = new TableData();
		tableData.setColspan(2);
		tableData.setWidth("70%");
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);

		nameTextField = new TextField<String>();
		nameTextField.setWidth("100%");
		nameTextField.ensureDebugId("328c02b0-6c3b-4ef7-a5ea-67c95656aaa1");
		if(username!=null && !username.isEmpty())
			nameTextField.setValue(username);
		else {
			nameTextField.setValue(UIContext.getGlobalDefaultUser());
		}
		nameTextField.addKeyListener(keyListener);
		this.add(nameTextField, tableData);

		nameTextField.setAllowBlank(false);
		/*nameTextField.setValidator(new Validator() {
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
		});*/

		label = new LabelField();
		label.setValue(UIContext.Constants.loginLabelPassword());
		label.addStyleName("connectDialogSpacing");
		tableData = new TableData();
		tableData.setWidth("30%");
		this.add(label, tableData);

		passwordTextField = new PasswordTextField();
		passwordTextField.ensureDebugId("aec00e43-ad32-42a6-96c5-9695282453cd");
		passwordTextField.setPassword(true);
		if(Util.isEmptyString(password)&&UIContext.hasGlobalDefaultPassword())
			passwordTextField.setValue(UIContext.getGlobalDefaultPassword());
		else
			passwordTextField.setValue(password);
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
		okButton.ensureDebugId("a8b8a97c-5aaf-4af5-9bdf-307b5f35e036");
		okButton.setText(UIContext.Constants.ok());
		okButton.setMinWidth(MIN_BUTTON_WIDTH);
		this.add(okButton, new TableData(Style.HorizontalAlignment.RIGHT,
				Style.VerticalAlignment.MIDDLE));

		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				isClickOK = true;
				nameTextField.disable();
				passwordTextField.disable();
				okButton.disable();
				String path = thisWindow.path;
				String domain = "";
				String user = nameTextField.getValue();
				String pwd = passwordTextField.getValue();
				image.setVisible(true);

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
               	if (getMode() == PathSelectionPanel.BACKUP_MODE || getMode() == PathSelectionPanel.RESTORE_ALT_MODE  || 
               			getMode() == PathSelectionPanel.ARCHIVE_MODE || getMode() == PathSelectionPanel.ARCHIVE_DEST_MODE || getMode() == PathSelectionPanel.DIAGNOSTIC_MODE) {
					service.validateDest(path, domain, user, pwd, getMode(),
							new BaseAsyncCallback<Long>() {
								@Override
								public void onFailure(Throwable caught) {
									isClickOK = false;
									super.onFailure(caught);
									image.setVisible(false);
									nameTextField.enable();
									passwordTextField.enable();
									passwordTextField.clear();
									UserPasswordWindow.this.setFocusWidget(passwordTextField);
									okButton.enable();
								}

								@Override
								public void onSuccess(Long result) {
									image.setVisible(false);
									thisWindow.hide();
									
								}
							});
				} else if (getMode() == PathSelectionPanel.RESTORE_MODE || getMode() == PathSelectionPanel.ARCHIVE_RESTORE_MODE 
						|| getMode() == PathSelectionPanel.MOUNT_VOLUME_MODEL) {
					service.validateSource(path, domain, user, pwd, getMode(), 
							new BaseAsyncCallback<Long>() {

								@Override
								public void onFailure(Throwable caught) {
									isClickOK = false;
									super.onFailure(caught);
									image.setVisible(false);
									nameTextField.enable();
									passwordTextField.enable();
									passwordTextField.clear();
									UserPasswordWindow.this.setFocusWidget(passwordTextField);
									okButton.enable();
								}

								@Override
								public void onSuccess(Long result) {
									image.setVisible(false);
									thisWindow.hide();
								}
							});
				} else if (getMode() == PathSelectionPanel.COPY_MODE) {
					service.validateCopyDest(path, domain, user, pwd,
							new BaseAsyncCallback<Long>() {

								@Override
								public void onFailure(Throwable caught) {
									isClickOK = false;
									super.onFailure(caught);
									image.setVisible(false);
									nameTextField.enable();
									passwordTextField.enable();
									passwordTextField.clear();
									UserPasswordWindow.this.setFocusWidget(passwordTextField);
									okButton.enable();
								}

								@Override
								public void onSuccess(Long result) {
									image.setVisible(false);
									thisWindow.hide();
								}
							});
				}

			}

		});

		cancelButton = new Button();
		cancelButton.ensureDebugId("ea145e35-6ed5-4202-bd89-a7c88a0f03e7");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.setMinWidth(MIN_BUTTON_WIDTH);
		this.add(cancelButton, new TableData(Style.HorizontalAlignment.RIGHT,
				Style.VerticalAlignment.MIDDLE));

		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				isClickOK = false;
				thisWindow.hide();
			}

		});
	}

	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		this.setFocusWidget(nameTextField);
		passwordTextField.focus();
	}

	public String getUsername() {
		return nameTextField.getValue();
	}

	public String getPassword() {
		return passwordTextField.getValue();
	}

	public boolean getCancelled() {
		return !isClickOK;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}
	
	private class CredKeyListener extends KeyListener {
		@Override
		public void componentKeyPress(ComponentEvent event) {
			if (event.getKeyCode() == KeyCodes.KEY_ENTER)
				okButton.fireEvent(Events.Select);
		}
	}

	@Override
	public void hide(Button buttonPressed) {
		super.hide(buttonPressed);
		if(isClickOK){
			Utils.cacheConnectionInfo(path, getUsername(), getPassword());
		}
	}	
}
