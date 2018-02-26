package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ProxySettingsModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PreferencesProxySettingsWindow extends Window{
	
	private final LoginServiceAsync loginService = GWT.create(LoginService.class);
	
	private PreferencesProxySettingsWindow thisWindow;
	LayoutContainer lcProxySettingsContainer;
	
	//private static FieldSet proxyFieldSet;
	private LayoutContainer lcProxysettings;
	
	private RadioGroup rgProxyOptions;
	private Radio rbBrowserSettings;
	private Radio rbConfigureProxySettings;
	private LabelField lblDefaultIEProxyNote;
	
	//private CheckBox cbEnableProxy;
	//private boolean UseProxy = false;
	private boolean UseProxyAuth = false;
	
	private LabelField lblProxyServer;
	private TextField<String> txtProxyServer;
	private LabelField lblProxyPort;
	private NumberField ProxyPort;
	
	private CheckBox cbProxyRequiresAuth;
	
	private LabelField lblProxyUserName;
	private TextField<String> txtProxyUsername;
	
	private LabelField lblProxyPassword;
	private PasswordTextField txtProxyPassword;
	
	private Listener<BaseEvent> ProxySettingsListerner;
	private Validator ProxySettingsValidator;
	
	private Button btOK;
	private Button btCancel;
	private Button btHelp;
	
	private ProxySettingsModel proxySettings = null;
	private String strButtonClicked = "";
	public final int MIN_WIDTH = 90;
	
	public PreferencesProxySettingsWindow()
	{
		this.setResizable(false);
		thisWindow = this;
		thisWindow.setWidth("600");
		thisWindow.setHeight("300");
		thisWindow.setHeadingHtml(UIContext.Constants.ProxySettingsWindowLabel());
		
		defineProxySettingsListener();
		
		lcProxysettings = new LayoutContainer();
		TableLayout tlProxySettings = new TableLayout(4);
		tlProxySettings.setCellPadding(0);
		tlProxySettings.setCellSpacing(4);
		tlProxySettings.setWidth("100%");
		lcProxysettings.setLayout(tlProxySettings);
		lcProxysettings.setStyleName("HTTPProxyLayout");
	    /*proxyFieldSet = new FieldSet();
	    proxyFieldSet.setCollapsible(false);
	    proxyFieldSet.setHeading(UIContext.Constants.UseProxyLabel());   
	    proxyFieldSet.setCheckboxToggle(true);
		{
			TableLayout tablePasswordLayout = new TableLayout();
			tablePasswordLayout.setColumns(4);
			tablePasswordLayout.setCellPadding(0);
			tablePasswordLayout.setCellSpacing(4);
			tablePasswordLayout.setWidth("100%");		
			proxyFieldSet.setLayout(tablePasswordLayout);
		}

		proxyFieldSet.addListener(Events.Expand,ProxySettingsListerner);
		proxyFieldSet.addListener(Events.Collapse,ProxySettingsListerner);
		
		*/
		
/*		cbEnableProxy = new CheckBox();
		cbEnableProxy.setBoxLabel(UIContext.Constants.UseProxyLabel());
		
		cbEnableProxy.addListener(Events.Change, ProxySettingsListerner);*/

		rgProxyOptions = new RadioGroup();
		rbBrowserSettings = new Radio(){

			@Override
			protected void onClick(ComponentEvent be) {
				// kasra04 Auto-generated method stub
				super.onClick(be);
			}
			
		};
		rbBrowserSettings.ensureDebugId("4c4edf8e-19c8-478c-a691-6ae6519ce806");
		rbBrowserSettings.setStyleName("x-form-field");
		rbBrowserSettings.setBoxLabel(UIContext.Constants.AutoUpdateBrowserProxySettings());
		rbBrowserSettings.setValue(true);
		//rbBrowserSettings.setToolTip(UIContext.Constants.ToolTipBrowserProxySettings());
	
		rbBrowserSettings.setWidth(550);
		rgProxyOptions.add(rbBrowserSettings);
		lblDefaultIEProxyNote = new LabelField(UIContext.Constants.ToolTipBrowserProxySettings());
		lblDefaultIEProxyNote.setStyleName("DefaultIEProxyNote");
		
		rbConfigureProxySettings = new Radio(){

			@Override
			protected void onClick(ComponentEvent be) {
				// kasra04 Auto-generated method stub
				super.onClick(be);
			}
			
		};
		rbConfigureProxySettings.ensureDebugId("3fc0161b-c306-410a-840a-1813d2a5c4c2");
		rbConfigureProxySettings.setStyleName("x-form-field");
		rbConfigureProxySettings.setBoxLabel(UIContext.Constants.AutoUpdateConfigureProxySettings());
		rbConfigureProxySettings.setValue(false);
		rbConfigureProxySettings.setWidth(550);
		rgProxyOptions.add(rbConfigureProxySettings);
		rgProxyOptions.addListener(Events.Change, ProxySettingsListerner);
		
		TableData tdBrowserProxy = new TableData();
		tdBrowserProxy.setColspan(4);
		tdBrowserProxy.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcProxysettings.add(rbBrowserSettings,tdBrowserProxy);
		
		TableData tdDefaultIEProxyNote = new TableData();
		tdDefaultIEProxyNote.setColspan(4);
		//tdDefaultIEProxyNote.setPadding(5);
		tdDefaultIEProxyNote.setHorizontalAlign(HorizontalAlignment.LEFT);
		tdDefaultIEProxyNote.setStyleName("DefaultIEProxyNote");
		lcProxysettings.add(lblDefaultIEProxyNote,tdDefaultIEProxyNote);
		
		TableData tdConfigureProxy = new TableData();
		tdConfigureProxy.setColspan(4);
		tdConfigureProxy.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcProxysettings.add(rbConfigureProxySettings,tdConfigureProxy);
		

		TableData tdProxyServerLable = new TableData();
		tdProxyServerLable.setHorizontalAlign(HorizontalAlignment.RIGHT);
		
		lblProxyServer = new LabelField(UIContext.Constants.ProxyServerLabel());
		lblProxyServer.setWidth(100);
		lcProxysettings.add(lblProxyServer,tdProxyServerLable);
		
		txtProxyServer = new TextField<String>();
		txtProxyServer.ensureDebugId("781b2fa7-0974-4d79-ac6c-1287e54903ca");
		txtProxyServer.setWidth(150);
		txtProxyServer.setMaxLength(128);
		txtProxyServer.setAllowBlank(false);
		txtProxyServer.setAutoValidate(false);
		txtProxyServer.setValidateOnBlur(true);
		txtProxyServer.setValidator(ProxySettingsValidator);
		Utils.addToolTip(txtProxyServer, UIContext.Messages.EnterProxyServerNameMessage());
		lcProxysettings.add(txtProxyServer);
		
		lblProxyPort = new LabelField(UIContext.Constants.ProxyServerPortLabel());
		lblProxyPort.setWidth(50);
		TableData tdProxyPort = new TableData();
		tdProxyPort.setHorizontalAlign(HorizontalAlignment.CENTER);
		lcProxysettings.add(lblProxyPort,tdProxyPort);
		
		ProxyPort = new NumberField();
		ProxyPort.ensureDebugId("32ae7ee9-0891-45fc-801e-c9f8d643e1dc");
		ProxyPort.setAllowBlank(false);
		ProxyPort.setWidth(60);
		//ProxyPort.setMaxLength(5);
		ProxyPort.setMinValue(1);
		ProxyPort.setMaxValue(65535);
		ProxyPort.setAllowNegative(false);
		Utils.addToolTip(ProxyPort, UIContext.Messages.EnterPortMessage());
		ProxyPort.setAutoValidate(false);
		ProxyPort.setValidateOnBlur(true);
		ProxyPort.setValidator(ProxySettingsValidator);

		lcProxysettings.add(ProxyPort);
		
		cbProxyRequiresAuth = new CheckBox();
		cbProxyRequiresAuth.ensureDebugId("755c17fe-f474-40f7-84f2-d60a31aff43e");
		cbProxyRequiresAuth.setBoxLabel(UIContext.Constants.IsProxyRequiresAuthenticationLabel());
		cbProxyRequiresAuth.addListener(Events.Change, ProxySettingsListerner);
		//cbProxyRequiresAuth.setStyleAttribute("padding-right", "15px");
		//cbProxyRequiresAuth.setWidth("10%");
		TableData tdProxyRequireAuth = new TableData();
		tdProxyRequireAuth.setColspan(4);
		tdProxyRequireAuth.setHorizontalAlign(HorizontalAlignment.LEFT);
		tdProxyRequireAuth.setStyleName("AutoUpdateConfigureProxy");
		//tdProxyRequireAuth.setPadding(15);
		//tdProxyRequireAuth.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcProxysettings.add(cbProxyRequiresAuth,tdProxyRequireAuth);
		
		lblProxyUserName = new LabelField(UIContext.Constants.ProxyUsernameLabel());
		lblProxyUserName.setWidth(100);
		TableData tdProxyUserNameLable = new TableData();
		tdProxyUserNameLable.setColspan(1);
		tdProxyUserNameLable.setHorizontalAlign(HorizontalAlignment.RIGHT);
		lcProxysettings.add(lblProxyUserName,tdProxyUserNameLable);
		
		txtProxyUsername = new TextField<String>();
		txtProxyUsername.ensureDebugId("ccf5affc-42ca-45cd-8b83-1d2f2e127513");
		txtProxyUsername.setWidth(350);
		txtProxyUsername.setMaxLength(128);
		txtProxyUsername.setEnabled(false);
		txtProxyUsername.setAllowBlank(false);
		TableData tdProxyUsername = new TableData();
		tdProxyUsername.setColspan(3);
		tdProxyUsername.setHorizontalAlign(HorizontalAlignment.LEFT);
		Utils.addToolTip(txtProxyUsername, UIContext.Messages.EnterProxyUserNameMessage());
		lcProxysettings.add(txtProxyUsername,tdProxyUsername);
		
		lblProxyPassword = new LabelField(UIContext.Constants.ProxyPasswordLabel());
		lblProxyPassword.setWidth(100);
		TableData tdProxyPasswordLable = new TableData();
		tdProxyPasswordLable.setColspan(1);
		tdProxyPasswordLable.setHorizontalAlign(HorizontalAlignment.RIGHT);
		lcProxysettings.add(lblProxyPassword,tdProxyPasswordLable);
		
		txtProxyPassword = new PasswordTextField();
		txtProxyPassword.ensureDebugId("927903cf-50c1-4207-b620-7a0af61d96a0");
		txtProxyPassword.setWidth(350);
		txtProxyPassword.setMaxLength(200);
		txtProxyPassword.setAllowBlank(false);
		txtProxyPassword.setPassword(true);
		Utils.addToolTip(txtProxyPassword, UIContext.Messages.EnterProxyPasswordMessage());
		txtProxyPassword.setEnabled(false);
		TableData tdProxyPassword = new TableData();
		tdProxyPassword.setColspan(3);
		tdProxyPassword.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcProxysettings.add(txtProxyPassword,tdProxyPassword);
	
		lcProxySettingsContainer = new LayoutContainer();
	    lcProxySettingsContainer.add(lcProxysettings);
		
	    defineProxySettingsButtons();		
	
		//UseProxy = false;
		//cbEnableProxy.setValue(false);
		rbBrowserSettings.setValue(true);
		rbConfigureProxySettings.setValue(false);
		
		thisWindow.setFocusWidget(btCancel);
		thisWindow.add(lcProxySettingsContainer);
	}
	
	private void defineProxySettingsListener()
	{
		ProxySettingsListerner = new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent ProxySettingsEvent) {
					
					if(ProxySettingsEvent.getSource() == rgProxyOptions)
					{
						if(rbBrowserSettings.getValue())
						{
							setProxyFieldsEnabled(false);
						}
						else
						{
							setProxyFieldsEnabled(true);
						}
					}
					else if(ProxySettingsEvent.getSource() == cbProxyRequiresAuth)
					{
						if(cbProxyRequiresAuth.getValue())
						{
							UseProxyAuth = true;
							proxyAuthEnable(true);
						}
						else
						{
							UseProxyAuth = false;
							proxyAuthEnable(false);
							btOK.setEnabled(true);
						}
					}
					else if(ProxySettingsEvent.getSource() == btOK)
					{
						ValidateAndSaveProxyServerDetails();
					}
					else if(ProxySettingsEvent.getSource() == btCancel)
					{
						strButtonClicked = "CANCEL";
						thisWindow.hide(btCancel);
					}
					else if(ProxySettingsEvent.getSource() == btHelp)
					{
						HelpTopics.showHelpURL(UIContext.externalLinks.getPreferencesAutoUpdateSettings());
					}
				}
		};
		
		ProxySettingsValidator = new Validator() {
			
			@Override
			public String validate(Field<?> field, String value) {
				if(field == txtProxyServer)
				{
					if(rbConfigureProxySettings.getValue())
					{
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.ERROR);
						msgError.setTitleHtml(getErrorMessageBoxTitle());
						msgError.setModal(true);
						if((txtProxyServer.getValue() == null) || (txtProxyServer.getValue().length() == 0))
						{
							msgError.setMessage(UIContext.Messages.EnterValidProxyServerNameMessage());
							Utils.setMessageBoxDebugId(msgError);
							msgError.show();
							return null;
						}
						
						ValidateProxyServer(false);
					}
				}
				else if(field == ProxyPort)
				{
					int iProxyServerPort = ProxyPort.getValue().intValue();
					
					MessageBox msgError = new MessageBox();
					msgError.setIcon(MessageBox.ERROR);
					msgError.setTitleHtml(getErrorMessageBoxTitle());
					msgError.setModal(true);
					if(iProxyServerPort < 1 || iProxyServerPort > 65535)
					{
						ProxyPort.setValue(null);
						msgError.setMessage(UIContext.Messages.EnterValidPortMessage(UIContext.Constants.ProxyServer()));
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
					}
				}
				return null;
			}
		};
		return;
	}
	
	private void defineProxySettingsButtons()
	{
		btOK = new Button();
		btOK.ensureDebugId("a47067a7-eea3-4d96-ba87-eb1e730d1923");
		btOK.setText(UIContext.Constants.ok());
		btOK.setMinWidth(MIN_WIDTH);
		btOK.addListener(Events.Select, ProxySettingsListerner);
		this.addButton(btOK);
		
		btCancel = new Button();
		btCancel.ensureDebugId("66a22678-f092-43a9-963f-9d7bf5ce7c60");
		btCancel.setText(UIContext.Constants.cancel());
		btCancel.setMinWidth(MIN_WIDTH);
		btCancel.addListener(Events.Select, ProxySettingsListerner);
		this.addButton(btCancel);	
		
		btHelp = new Button();
		btHelp.ensureDebugId("222e8c08-fce1-4d4b-aa7c-e0b69281f0ac");
		btHelp.setText(UIContext.Constants.help());
		btHelp.setMinWidth(MIN_WIDTH);
		btHelp.addListener(Events.Select, ProxySettingsListerner);
		this.addButton(btHelp);	
	}
	
	private void setProxyFieldsEnabled(boolean enabled)
	{
		//setUseProxy(enabled);
		
		if(enabled == false)
		{
			ProxyPort.clearInvalid();
			txtProxyServer.clearInvalid();
		}
		
		lblProxyServer.setEnabled(enabled);
		lblDefaultIEProxyNote.setEnabled(!enabled);
		lblProxyPort.setEnabled(enabled);
		txtProxyServer.setEnabled(enabled);
		ProxyPort.setEnabled(enabled);
		cbProxyRequiresAuth.setEnabled(enabled);
		//proxyAuthEnable(enabled);
		
		//if(enabled)
		//{
		boolean bEnabled = cbProxyRequiresAuth.getValue();  
		proxyAuthEnable(bEnabled & enabled);
		//}
	}
	
	private void proxyAuthEnable(boolean bEnabled) 
	{
		if(bEnabled == false)
		{
			txtProxyUsername.clearInvalid();
			txtProxyPassword.clearInvalid();
		}
		lblProxyUserName.setEnabled(bEnabled);
		txtProxyUsername.setEnabled(bEnabled);
		lblProxyPassword.setEnabled(bEnabled);
		txtProxyPassword.setEnabled(bEnabled);
	}
	
	/*public void setUseProxy(boolean useProxy) {
		UseProxy = useProxy;
	}*/
	
	public void RefreshData(ProxySettingsModel in_proxySettingsModel)
	{
		proxySettings = in_proxySettingsModel;
		
		if(proxySettings == null)
		{
			//UseProxy = false;
			//cbEnableProxy.setValue(false);
			rbBrowserSettings.setValue(true);
			rbConfigureProxySettings.setValue(false);
			//lcProxysettings.collapse();
			proxySettings = new ProxySettingsModel();
			//default data
			proxySettings.setProxyPort(0);
			proxySettings.setUseProxy(false);
			proxySettings.setProxyRequiresAuth(false);
		}
		
		boolean bUseProxy = proxySettings.getUseProxy() != null ? proxySettings.getUseProxy().booleanValue() : false; 
		
		if(bUseProxy)
		{
			//UseProxy = true;
			//cbEnableProxy.setValue(true);
			rbBrowserSettings.setValue(false);
			rbConfigureProxySettings.setValue(true);
			
			//proxyFieldSet.collapse();
			//proxyFieldSet.expand();

			setProxyFieldsEnabled(true);			
			
			txtProxyServer.setValue(proxySettings.getProxyServerName());
			ProxyPort.setValue(proxySettings.getProxyPort());
			
			Boolean bProxyRequiresAuth = proxySettings.getProxyRequiresAuth();
			
			cbProxyRequiresAuth.setValue(bProxyRequiresAuth);
			proxyAuthEnable(bProxyRequiresAuth);
			
			if(bProxyRequiresAuth)
			{
				txtProxyUsername.setValue(proxySettings.getProxyUserName());
				txtProxyPassword.setValue(proxySettings.getProxyPassword());
			}
		}
		else
		{
			//UseProxy = false;
			//cbEnableProxy.setValue(false);
			rbBrowserSettings.setValue(true);
			rbConfigureProxySettings.setValue(false);
			//proxyFieldSet.collapse();
			setProxyFieldsEnabled(false);
		}
		return;
	}

	public void Save()
	{
		if(proxySettings == null)
		{
			proxySettings = new ProxySettingsModel();
		}
		
		if(rbConfigureProxySettings.getValue())
		{
			proxySettings.setProxyServerName(txtProxyServer.getValue());
			proxySettings.setProxyPort(ProxyPort.getValue().intValue());
						
			if(UseProxyAuth)
			{
				proxySettings.setProxyUserName(txtProxyUsername.getValue());
				proxySettings.setProxyPassword(txtProxyPassword.getValue());
			}
			
			proxySettings.setProxyRequiresAuth(UseProxyAuth);
		}

		proxySettings.setUseProxy(rbConfigureProxySettings.getValue());
		return;
	}
	
	public ProxySettingsModel getProxySettingsModel()
	{
		return proxySettings;
	}
	
	public String getButtonClicked()
	{
		return strButtonClicked;
	}
	
	private boolean ValidateAndSaveProxyServerDetails()
	{
		boolean bValidated = true;
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(getErrorMessageBoxTitle());
		msgError.setModal(true);
		
		if(rbConfigureProxySettings.getValue())
		{
			if((txtProxyServer.getValue() == null) || (txtProxyServer.getValue().length() == 0) || (txtProxyServer.getValue().length() > 128))
			{
				txtProxyServer.setValue(null);
				msgError.setMessage(UIContext.Messages.EnterValidProxyServerNameMessage());
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(ProxyPort.getValue() == null)
			{
				msgError.setMessage(UIContext.Messages.EnterValidPortMessage(UIContext.Constants.ProxyServer()));
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(ProxyPort.getValue().intValue() < 1 || ProxyPort.getValue().intValue() > 65535)
			{
				bValidated = false;
				return bValidated;
			}
			
			if(UseProxyAuth)
			{
				if(txtProxyUsername.getValue() == null  || (txtProxyUsername.getValue().length() == 0) || txtProxyUsername.getValue().length() > 128)
				{
					msgError.setMessage(UIContext.Messages.EnterValidProxyUserNameMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
				
				if((txtProxyPassword.getValue() == null) || (txtProxyPassword.getValue().length() == 0) || (txtProxyPassword.getValue().length() > 200))
				{
					msgError.setMessage(UIContext.Messages.EnterValidProxyPasswordMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
			}
			
			ValidateProxyServer(true);
		}
		else
		{
			Save();
			strButtonClicked = "OK";
			thisWindow.hide(btOK);
		}
		return bValidated;
	}
	
	private void ValidateProxyServer(final boolean bSave)
	{
		loginService.ValidateServerName(txtProxyServer.getValue(), new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if(!result)
				{
					txtProxyServer.setValue(null);
					if(!bSave)
					{
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.ERROR);
						msgError.setTitleHtml(getErrorMessageBoxTitle());
						msgError.setModal(true);
						msgError.setMessage(UIContext.Messages.InValidCharactersServerNameFoundMessage(UIContext.Constants.ProxyServer()));
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
					}
					return;
				}
				else
				{
					if(bSave)
					{
						Save();
						strButtonClicked = "OK";
						thisWindow.hide(btOK);
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitleHtml(getErrorMessageBoxTitle());
				msgError.setModal(true);
				
				String strMessage = "";
				strMessage += UIContext.Messages.FailedToValidateProxyServerName();
				strMessage += caught.getMessage();
				
				msgError.setMessage(strMessage);
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				return;					
			}
		});
		return;
	}
	
	//EDGE app will override this method
	protected String getErrorMessageBoxTitle(){
		return UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
	}
}
