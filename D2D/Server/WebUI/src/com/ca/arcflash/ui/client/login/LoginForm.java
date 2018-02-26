package com.ca.arcflash.ui.client.login;


import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.homepage.HomeContentFactory;
import com.ca.arcflash.ui.client.model.ExternalLinksModel;
import com.ca.arcflash.ui.client.model.VersionInfoModel;
import com.ca.arcflash.ui.client.vsphere.backup.VSphereBackupSettingWindow;
import com.ca.arcflash.ui.client.vsphere.homepage.VSphereHomepagePanelForEdge;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;

public class LoginForm extends LayoutContainer {

	private static final int DEFAULT_PORT = 80;	
	private TextField<String> domainNameTextField;
	private TextField<String> nameTextField;
	private PasswordTextField passwordTextField;
	private Button loginButton;	
	private LoginKeyListener keyListener = new LoginKeyListener();
	private LabelField buildLabel;
	private LabelField updateVersionLabel;
	
	private String wsHostname = "localhost";// Location.getHostName();
	// D2D server get localhost webservice default,
	// If you want to debug UI for remote webservice,
	// you can change the wsHostName, or don't change it.
	private int wsPort = DEFAULT_PORT;
	private String wsProtocol = "https:"; //"https:" fixed by cliicy.luo Location.getProtocol(); // http: or https:
	
	private static LoginServiceAsync service;
	private static CommonServiceAsync commmonsevice;
	public LoginForm(){
		service = GWT.create(LoginService.class);
		commmonsevice=GWT.create(CommonService.class);
	}
	
	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);		
		
//		LayoutContainer container = new LayoutContainer();
//		TableLayout tbLayout = new TableLayout();
//		tbLayout.setWidth("98%");
//		tbLayout.setColumns(1);
//		tbLayout.setCellPadding(0);
//		tbLayout.setCellSpacing(0);	
//		container.setLayout(tbLayout);
	
		LayoutContainer loginFormPanel = new LayoutContainer();		
		TableLayout layout = new TableLayout();		
		layout.setColumns(2);
		layout.setCellPadding(3);
		layout.setCellSpacing(0);
		loginFormPanel.setLayout(layout);
		//loginFormPanel.setStyleName("login_form");
		this.add(loginFormPanel);
		
		TableData tableData = new TableData();
		tableData.setColspan(2);
		tableData.setMargin(0);
		tableData.setPadding(0);

		// Hide this text field. (Issue: 18496025)
		domainNameTextField = new TextField<String>();
		domainNameTextField.ensureDebugId("2d0dd8eb-6895-4e9d-bdc9-fdfc57c62524");

		nameTextField = new TextField<String>();
		nameTextField.ensureDebugId("92be14c0-b588-413e-a347-c472f3bb76c9");
		// nameTextField.setWidth("100%");
		nameTextField.setWidth(251);
		nameTextField.addKeyListener(keyListener);		
		nameTextField.setEmptyText(UIContext.Constants.loginLabelUsername());
		nameTextField.setStyleName("login-input");
		// Tool tip
		String location = Window.Location.getParameter(VSphereHomepagePanelForEdge.LOCATION_PARAM);
		boolean isVM = false;
		if (location != null && HomeContentFactory.LOCATION_VSPHERE.equalsIgnoreCase(location))
			isVM = true;
	
		ToolTipConfig tipConfig = new ToolTipConfig(isVM ? UIContext.Constants.loginUsernameTooltipForProxy() : UIContext.Constants.loginUsernameTooltip());
		ToolTip tip = new ToolTip(nameTextField, tipConfig);
		tip.ensureDebugId("1a1ae48e-3849-420e-bdcd-15a621e779e6");
		tip.setHeaderVisible(false);
		loginFormPanel.add(nameTextField, tableData);

		passwordTextField = new PasswordTextField();
		passwordTextField.ensureDebugId("350083ef-768b-4a00-bc1f-e5f4eb510cf1");
		passwordTextField.setPassword(true);
		passwordTextField.setWidth(251);
		passwordTextField.addKeyListener(keyListener);
		passwordTextField.setEmptyText(UIContext.Constants.loginLabelPassword());
		passwordTextField.setStyleName("login-input");
		// Tool tip
		tipConfig = new ToolTipConfig(UIContext.Constants.loginPasswordTooltip());
		tip = new ToolTip(passwordTextField, tipConfig);
		tip.ensureDebugId("4ff688da-738f-4fd0-b6ab-ba2d880e9673");
		tip.setHeaderVisible(false);
		loginFormPanel.add(passwordTextField, tableData);
	

		loginButton = new Button();
		loginButton.ensureDebugId("7ba45dca-14ca-4936-abfc-363ab6e4e88a");		
		loginButton.setText(UIContext.Constants.loginButtonTextLogin());
		// Tool tip
		tipConfig = new ToolTipConfig(UIContext.Messages.loginButtonTooltip(UIContext.productNameD2D));
		tip = new ToolTip(loginButton, tipConfig);
		tip.ensureDebugId("c7ddf687-733c-4510-9ac1-70038dc36b74");
		tip.setHeaderVisible(false);		
		
		LayoutContainer buildInfoContainer = new LayoutContainer(new TableLayout());
		
		buildLabel = new LabelField();
		buildLabel.setStyleName("buildtext");
		
		TableData ld = new TableData();
		ld.setHorizontalAlign(HorizontalAlignment.LEFT);
		buildInfoContainer.add(buildLabel, ld);
		
		updateVersionLabel = new LabelField();
		updateVersionLabel.setStyleName("buildtext");		
		
		buildInfoContainer.add(updateVersionLabel, ld);
		
		loginFormPanel.add(buildInfoContainer);
		
		
		loginFormPanel.add(loginButton, new TableData(Style.HorizontalAlignment.RIGHT, Style.VerticalAlignment.MIDDLE));

		loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				buttonClicked();
			}
		});
		
		passwordTextField.focus();
	}


	private void buttonClicked() {
		domainNameTextField.disable();
		nameTextField.disable();
		passwordTextField.disable();
		loginButton.disable();
		// String port = Location.getPort();
		// String host = Location.getHostName();

		// Extract domain name from nameTextField (UserName) ex)
		// tant-a01\kimwo01 --> tant-a01 , kimwo01
		String strDomain = getDomainNameFromUserTextField(nameTextField.getValue());
		if (strDomain == "") // If there is no domain value, use default vaule.
			strDomain = domainNameTextField.getValue();

		String strUser = getUserNameFromUserTextField(nameTextField.getValue());

		final String domainName = strDomain;
		final String userName = strUser;

		service.validateUser(wsProtocol, wsHostname, getPortNumber(), strDomain, // domainNameTextField.getValue(),
				strUser, // nameTextField.getValue(),
				passwordTextField.getValue(), new BaseAsyncCallback<Boolean>() {

					/**
					 * when user inputs wrong password, the password field will
					 * focus after message box shows.
					 */
					@Override
					public void showErrorMessage(BusinessLogicException exception) {
						MessageBox messageBox = new MessageBox();
						messageBox.addCallback(new Listener<MessageBoxEvent>() {

							@Override
							public void handleEvent(MessageBoxEvent be) {
								isShow = false;
								passwordTextField.focus();
							}

						});

						messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
						messageBox.setMessage(exception.getDisplayMessage());
						messageBox.setIcon(MessageBox.ERROR);
						messageBox.setModal(true);
						messageBox.setMinWidth(400);					
						Utils.setMessageBoxDebugId(messageBox);
						messageBox.show();
					}

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						domainNameTextField.enable();
						nameTextField.enable();
						passwordTextField.enable();
						passwordTextField.setValue("");
						loginButton.enable();
					}

					@Override
					public void onSuccess(Boolean result) {
						LoginForm.this.hide();
						String fullName = "";
						if (domainName != null && domainName.length() > 0)
							fullName = domainName + "\\";
						fullName += userName;
						UIContext.loginUser = fullName;
					}
				});

	}
	

	
	private int getPortNumber() {
		int port = DEFAULT_PORT;

		try {
			port = wsPort;
		} catch (Exception e) {
			// Ignore
		}

		return port;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		// this.toBack();
		/*
		 * First we set the user name to loading indicator, and make user name
		 * field and button disabled
		 */
		nameTextField.setValue(UIContext.Constants.loadingIndicatorText());
		nameTextField.disable();
		passwordTextField.disable();
		loginButton.disable();

		service.getWSPort(new BaseAsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				// do nothing
			}

			@Override
			public void onSuccess(Integer result) {
				wsPort = result;
				service.getDefaultUserAndBuild(wsProtocol, wsHostname, result, new BaseAsyncCallback<VersionInfoModel>() {

					@Override
					public void onFailure(Throwable caught) {
						domainNameTextField.setValue(Location.getHostName());
						// loading account name failed, we set the user name to
						// empty to make a difference with a good accunt name
						nameTextField.setValue("");
						// setDefaults();
						nameTextField.enable();
						passwordTextField.enable();
						loginButton.enable();
						// we'd better not chagne the focus, because this time
						// user maybe typing the password
						// nameTextField.focus();

						// thisWindow.toFront();
					}

					@Override
					public void onSuccess(VersionInfoModel result) {
						String userName = result != null ? result.getUserName() : null;
						if (userName != null && !userName.trim().isEmpty()) {
							nameTextField.setValue(userName.trim());
							buildLabel.setValue(UIContext.Messages.versionAndBuild(result.getMajorVersion(), result.getMinorVersion(), result.getBuildNumber()));						
							//don't show the first version 5.0 of UDP
//							if(result.getMajorVersion().equals("5")&&result.getMinorVersion().equals("0")){
//								buildLabel.hide();
//							}
							if(result.getUpdateNumber()!=null&&!result.getUpdateNumber().isEmpty()&&result.getUpdateBuildNumber()!=null&&!result.getUpdateBuildNumber().isEmpty())
							{
								buildLabel.show();
								updateVersionLabel.setValue(UIContext.Messages.updateBuildNumber(result.getUpdateNumber(),result.getUpdateBuildNumber()));	
							}
							else{
								updateVersionLabel.setValue("&nbsp;");
							}
						} else {
							domainNameTextField.setValue(Location.getHostName());
							// loading account name failed, we set the user name
							// to empty to make a difference with a good accunt
							// name
							nameTextField.setValue("");
						}

						nameTextField.enable();
						passwordTextField.enable();
						passwordTextField.focus();
						loginButton.enable();
						// we'd better not chagne the focus, because this time
						// user maybe typing the password
						// if(nameTextField.getValue()==null ||
						// nameTextField.getValue().trim().isEmpty())
						// {
						// nameTextField.focus();
						// }

						// thisWindow.toFront();
						commmonsevice.getExternalLinks(result.getLocale(), result.getCountry(), new BaseAsyncCallback<ExternalLinksModel>(){

							@Override
							public void onFailure(Throwable caught) {
								GWT.log("Error", caught);
							}

							@Override
							public void onSuccess(ExternalLinksModel result) {
								UIContext.externalLinks = result;
							}
							
						});
					}
				});
			}
		});

	}
	
	
	private String getDomainNameFromUserTextField(String strUserInput) {
		String strDomain = "";

		if (strUserInput == null || strUserInput.isEmpty())
			return strDomain;

		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
		} else {
			// Extract domain part
			strDomain = strUserInput.substring(0, pos);
		}
		return strDomain;
	}

	private String getUserNameFromUserTextField(String strUserInput) {
		String strUser = "";

		if (strUserInput == null || strUserInput.isEmpty())
			return strUser;

		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
			strUser = strUserInput;
		} else {
			// Extract user name part
			strUser = strUserInput.substring(pos + 1);
		}
		return strUser;
	}
	
	private class LoginKeyListener extends KeyListener {

		@Override
		public void componentKeyPress(ComponentEvent event) {
			if (event.getKeyCode() == KeyCodes.KEY_ENTER)
				loginButton.fireEvent(Events.Select);
		}
	}
}
