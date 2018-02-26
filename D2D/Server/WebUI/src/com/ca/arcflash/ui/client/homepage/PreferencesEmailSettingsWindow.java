package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.EmailAlertsModel;
import com.ca.arcflash.ui.client.model.EmailServerType;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.ca.arcflash.ui.client.model.MailServiceInfo;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.FlexTable;

public class PreferencesEmailSettingsWindow extends Window {
	LoginServiceAsync service = GWT.create(LoginService.class);
	
	LabelField addressLabel;
	LabelField portLabel;
	LabelField usernameLabel;
	LabelField passwordLabel;
	
	private TextField<String> addressTF;
	private NumberField portTF;
	private TextField<String> usernameTF;
	private PasswordTextField passwordTF;
	
	private TextField<String> mailserverTF;
	private TextField<String> subjectTF;
	private TextField<String> fromTF;
	private TextField<String> recipientsTF;
	
	private Button testBtn;
	
	private CheckBox useHTMLFormat; 
	
	private PreferencesEmailSettingsWindow thisWindow;
	
	private Button okButton;
	private Button cancelButton;
	private Button helpButton;
	
	public final int MIN_WIDTH = 90;
	public final int MIN_FIELD_WIDTH = 300;
	
	public static final int ERR_MAILSERVER_INVALID = -1;
	public static final int ERR_FROM_INVALID = -2;
	public static final int ERR_RECIPIENTS_INVALID = -3;
	public static final int ERR_MAILUSERNAME_INVALID = -4;
	public static final int ERR_PROXYUSERNAME_INVALID = -5;
	public static final int ERR_PROXYSERVER_INVALID = -6;
	public static final int ERR_MAILPASSWORD_INVALID = -7;
	public static final int ERR_PROXYPASSWORD_INVALID = -8;
	public static final int ERR_PROXYPORT_INVALID=-9;
    public static final int ERR_RECIPIENTS_BLANK = -10;
    public static final int ERR_MAILSERVERPORT_INVALID = -11;
    public static final String EMAIL_PATTEN = "[A-Z0-9._%$&+-]+@[A-Z0-9-]+[A-Z0-9.-]*\\.[A-Z-]{2,}";
	
//	private BackupSettingsModel tempModel;
	private boolean HTMLFormat = true;
	private boolean UseProxy = false;
	
	/** Alert Email enhancement PR */
	private boolean useSsl_b = false;
	private boolean starttls_b =false;
	private boolean mailAuth_b =false;
	private boolean proxyAuth_b = false;
	
	//messageBox title
	private AppType appType = AppType.D2D;
	public void setAppType(AppType appType){
		this.appType = appType;
	}
	
	@Override
	public void setEnabled(boolean isEnable){
		mailFieldSet.setEnabled(isEnable);
		proxyFieldSet.setEnabled(isEnable);
		okButton.setEnabled(isEnable);
		testBtn.setEnabled(isEnable);
		
		if(useSsl != null)
			useSsl.setEnabled(isEnable);
		
		if(starttls != null)
			starttls.setEnabled(isEnable);
		
		if(useHTMLFormat != null)
			useHTMLFormat.setEnabled(isEnable);
		
		//Fix the issue:20114663
		Element parentElement = proxyFieldSet.getElement();
		NodeList<Element> childElements = parentElement.getElementsByTagName("INPUT");
		if(childElements!=null){
			for (int i=0;i<childElements.getLength();i++) {
				Element element = childElements.getItem(i);
				if(element instanceof InputElement){
					InputElement inputElement = (InputElement)element;
					inputElement.setDisabled(true);
				}
			}
		}
	}
	
	public boolean isProxyAuth_b() {
		return proxyAuth_b;
	}

	public void setProxyAuth_b(boolean proxyAuthB) {
		proxyAuth_b = proxyAuthB;
	}
	
	public boolean isUseSsl_b() {
		return useSsl_b;
	}

	public boolean isStarttls_b() {
		return starttls_b;
	}

	public void setStarttls_b(boolean starttlsB) {
		starttls_b = starttlsB;
	}

	public void setUseSsl_b(boolean useSslB) {
		useSsl_b = useSslB;
	}
	
	public boolean isHTMLFormat() {
		return HTMLFormat;
	}

	public void setHTMLFormat(boolean HTMLFormat) {
		this.HTMLFormat = HTMLFormat;
	}

	public PreferencesEmailSettingsWindow(AppType appType)
	{
		this.appType = appType;
		this.setResizable(false);
		thisWindow = this;
		thisWindow.setHeadingHtml(UIContext.Constants.settingsMailTitle());
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(1);
		layout.setCellPadding(0);
		layout.setCellSpacing(0);
		this.setLayout(layout);
		
		this.setMinWidth(500);
		this.setMinHeight(475);
		this.setHeight(475);
//		this.setAutoWidth(true);
		this.setWidth(600);
		this.setScrollMode(Scroll.AUTOY);

		
		LayoutContainer mailCon = new LayoutContainer();
		{
			TableLayout tablePasswordLayout = new TableLayout();
			tablePasswordLayout.setCellPadding(0);
			tablePasswordLayout.setCellSpacing(0);
			tablePasswordLayout.setColumns(1);
			tablePasswordLayout.setCellPadding(10);
			tablePasswordLayout.setCellSpacing(0);
			tablePasswordLayout.setWidth("100%");		
			mailCon.setLayout(tablePasswordLayout);	
			mailCon.setHeight(400);
		}
		addEmailSection(mailCon);
		mailCon.add(new Html("<HR>"));
		addProxySection(mailCon);
		
		this.add(mailCon);
		
		addTestButton();
		
		ButtonBar tl = this.getButtonBar();
		tl.setAlignment(HorizontalAlignment.LEFT);

		tl.add(testBtn);
		tl.add(new FillToolItem());
		
		okButton = new Button();
		okButton.ensureDebugId("989bcd00-f831-4995-a52c-4d9c67e3c118");
		okButton.setText(UIContext.Constants.ok());
		okButton.setMinWidth(MIN_WIDTH);		
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				BaseAsyncCallback<Boolean> callback = new BaseAsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						if(caught instanceof BusinessLogicException) {
							BusinessLogicException be = (BusinessLogicException)caught;
							showError(be.getDisplayMessage());
						}else
							super.onFailure(caught);
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result)
							thisWindow.hide(okButton);
						else {
							showError(UIContext.Constants.settingsFromFieldError());
						}
					}
				};
				validateWithFrom(callback);
			}	
			});		

		tl.add(okButton);
		
		
		cancelButton = new Button();
		cancelButton.ensureDebugId("9a5ed65c-25bf-4c0b-a399-42ab0a01ebd6");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.setMinWidth(MIN_WIDTH);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
				
			}});
		tl.add(cancelButton);
		
		helpButton = new Button();
		helpButton.ensureDebugId("9283192b-6de2-49af-8f18-d37cf7154b56");
		helpButton.setText(UIContext.Constants.help());
		helpButton.setMinWidth(MIN_WIDTH);
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				HelpTopics.showHelpURL(getHelpLink());
			}
		});

		tl.add(helpButton);
		
		this.setMinWidth(500);
		
		// liuwe05 2011-06-01 fix Issue: 20312591    Title: INCORRECT BEHAVIOR EXHIBITED I
		// focus on the OK button, otherwise after press ESC in Firefox/Chrome, it will pop up confirmation to close the setting window.
		// and this window will still be there after the setting window is closed
		this.setFocusWidget(okButton);
	}
	
	public PreferencesEmailSettingsWindow()
	{
		this(AppType.D2D);
	}
	
	private void addTestButton() {
		testBtn = new Button(UIContext.Constants.settingsTestMailButton());
		testBtn.setMinWidth(MIN_WIDTH);
		testBtn.setAutoWidth(true);
		testBtn.ensureDebugId("0804FE2C-C021-47b4-969A-ABB7DF9DBB8B");
		Utils.addToolTip(testBtn, UIContext.Constants.settingsTestMailDesp());
		testBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.mask(UIContext.Constants.settingsSendMailMask());
				testBtn.setEnabled(false);
				BaseAsyncCallback<Boolean> callback = new BaseAsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						testBtn.setEnabled(true);
						thisWindow.unmask();
						if(caught instanceof BusinessLogicException) {
							BusinessLogicException be = (BusinessLogicException)caught;
							showError(be.getDisplayMessage());
						}else
							super.onFailure(caught);
					}

					@Override
					public void onSuccess(Boolean result) {						
						if(result) {
							EmailAlertsModel model = new EmailAlertsModel();
							saveSettings(model);
							model.setSubject(UIContext.Messages
									.settingsTestMailSubject(getProductName()));
							model.setContent(UIContext.Messages
									.settingsTestMailContent(getProductName()));
							service.testMailSettings(model, new BaseAsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									testBtn.setEnabled(true);
									thisWindow.unmask();
									if(caught instanceof BusinessLogicException) {
										BusinessLogicException ble = (BusinessLogicException)caught;
										showError(ble.getDisplayMessage());
									}else
										super.onFailure(caught);
								}

								@Override
								public void onSuccess(Boolean result) {
									testBtn.setEnabled(true);
									thisWindow.unmask();
									MessageBox.info(UIContext.Messages.messageBoxTitleInformation(getProductName()), 
											UIContext.Constants.settingsSendTestMailSuccess(), null);
								}
							});
						}else {
							thisWindow.unmask();
							testBtn.setEnabled(true);
							showError(UIContext.Constants.settingsFromFieldError());
						}
					}
				};
				
				if(!validateWithFrom(callback)){
					thisWindow.unmask();
					testBtn.setEnabled(true);
				}
			}	
		});
	}
	
	private void showError(String message) {
		if(!thisWindow.isVisible())
			return;
		MessageBox msg = new MessageBox();
		msg.setIcon(MessageBox.ERROR);
		msg.setTitleHtml(getMessageBoxTitle());
		msg.setMessage(message);
		msg.setModal(true);
		msg.getDialog().setStyleAttribute("word-wrap", "break-word");
		
		if(message.contains("http://") || message.contains("https://")) {
			msg.getDialog().setStyleAttribute("word-break", "break-all");
		}		
		
		msg.show();
	}
	
	private boolean validateWithFrom(BaseAsyncCallback<Boolean> callback) {
		if ((!mailserverTF.validate()) ||
			(!subjectTF.validate()) ||
			(!fromTF.validate()) ||
			(!recipientsTF.validate()) ||
			(!addressTF.validate()) ||
			(!portTF.validate()) ||
			(!usernameTF.validate()) ||
			(!passwordTF.validate())) 
		{
			return false;
		}
		
		int ret = validate();
		String message = "";
		
		if(ret == 0) {
			service.validateEmailFromAddress(fromTF.getValue(), callback);
		}else {
			switch(ret)
			{
				case ERR_MAILSERVER_INVALID:
					message = UIContext.Constants.settingsMailServerError();
					break;
				case ERR_FROM_INVALID:
					message = UIContext.Constants.settingsFromFieldError();
					break;
				case ERR_RECIPIENTS_BLANK:
					message = UIContext.Constants.settingsRecipientsError();
					break;
				case ERR_MAILUSERNAME_INVALID:
					message = UIContext.Constants.mailUserNameEmptyError();
					break;
				case ERR_MAILPASSWORD_INVALID:
					message = UIContext.Constants.mailPasswordEmptyError();
					break;
				case ERR_PROXYUSERNAME_INVALID:
					message = UIContext.Constants.proxyUserNameEmptyError();
					break;
				case ERR_PROXYPASSWORD_INVALID:
					message = UIContext.Constants.proxyPasswordEmptyError();
					break;
				case ERR_PROXYSERVER_INVALID:
					message = UIContext.Constants.proxyServerEmptyError();
					break;
			    case ERR_PROXYPORT_INVALID:
				    message = UIContext.Constants.proxyServerPortError();
				    break;
				case ERR_RECIPIENTS_INVALID:
					message = UIContext.Constants.preferencesRecipientsEmailFormatError();
					break;
                case ERR_MAILSERVERPORT_INVALID:
			    	message= UIContext.Constants.mailServerPortError();
			    	break;
			}
		}				
		
		if (message.length() > 0)
		{
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(getMessageBoxTitle());
			msg.setMessage(message);
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
			return false;
		}
		
		return true;
	}
	
	private String getHelpLink(){
		String link = "";
		if(appType == AppType.VCM){
			link = UIContext.externalLinks.getVirtualStandbyBackupSettingEmailHelp();
		}
		else if(appType == AppType.VSPHERE){
			link = UIContext.externalLinks.getVMBackupSettingEmailHelp();
		}
		else{
			link = UIContext.externalLinks.getBackupSettingEmailHelp();
		}
		return link;
	}
	
	private String getMessageBoxTitle(){
		String productName = getProductName();
		return UIContext.Messages.messageBoxTitleError(productName);
	}
	
	private String getProductName() {
		String productName = "";
		if(appType == AppType.VCM){
			productName = UIContext.productNameVCM;
		}
		else if(appType == appType.VSPHERE){
			productName = UIContext.productNamevSphere;
		}
		else{
			productName = UIContext.productNameD2D;
		}
		return productName;
	}
	
	public int validate() {
		int ret = 0;
		
		if (mailserverTF == null ||
				mailserverTF.getValue() == null ||
				mailserverTF.getValue().trim().length() == 0)
		{
			ret = ERR_MAILSERVER_INVALID;
		}
		else if(mailserverPortTF.getValue() == null || mailserverPortTF.getValue().intValue()<1||mailserverPortTF.getValue().intValue()>65535)
		{
			ret = ERR_MAILSERVERPORT_INVALID;
		}
		else if(mailAuth_b && (mailUserTF.getValue() == null || mailUserTF.getValue().trim().isEmpty())){

				ret = ERR_MAILUSERNAME_INVALID;
			
		}
		else if(mailAuth_b && (pwdTF.getValue() == null || pwdTF.getValue().trim().isEmpty()))
		{
			ret = ERR_MAILPASSWORD_INVALID;
		}
		else if (fromTF == null ||
				fromTF.getValue() == null ||
				fromTF.getValue().trim().length() == 0)
		{
			ret = ERR_FROM_INVALID;
		} 
		else if (recipientsTF == null ||
				recipientsTF.getValue() == null ||
				recipientsTF.getValue().trim().length() == 0)
		{
			ret = ERR_RECIPIENTS_BLANK;
		} 
		else if((ret = this.validateRecipientsEmailFormat(recipientsTF.getValue())) != 0)
		{
			ret = ERR_RECIPIENTS_INVALID;
		}
		else if(UseProxy && (addressTF.getValue() == null || addressTF.getValue().trim().isEmpty()))
		{

			ret = ERR_PROXYSERVER_INVALID;
		}
		else if(UseProxy && (portTF.getValue() == null || portTF.getValue().intValue()<1||portTF.getValue().intValue()>65535))
		{
			ret = ERR_PROXYPORT_INVALID;
		}
		else if( UseProxy  && proxyAuth_b && (usernameTF.getValue() == null || usernameTF.getValue().trim().isEmpty()))
		{
			ret = ERR_PROXYUSERNAME_INVALID;
		}
		else if( UseProxy  && proxyAuth_b && (passwordTF.getValue() == null || passwordTF.getValue().trim().isEmpty()))
		{
			ret = ERR_PROXYPASSWORD_INVALID;
		}
		return ret;
	}
	MailServiceInfo google_info;
	MailServiceInfo yahoo_info;
	MailServiceInfo live_info ;
	MailServiceInfo other_info;
	
	public ListStore<MailServiceInfo> CreateMailServiceInfo()
	{
		ListStore<MailServiceInfo> mailListStore = new ListStore<MailServiceInfo>();
		
		google_info = new MailServiceInfo();
		google_info.setName(UIContext.Constants.googleMail());
		google_info.setMailServer(UIContext.Constants.googleMailServer());
		google_info.setSmtpPort(465);
		google_info.setUseSSL(true);
		google_info.setUseTLS(false);
		google_info.setMailServerValue(EmailServerType.Google.getType());
		mailListStore.add(google_info);
		
		yahoo_info = new MailServiceInfo();
		yahoo_info.setName(UIContext.Constants.yahooMail());
		yahoo_info.setMailServer(UIContext.Constants.yahooMailServer());
		yahoo_info.setSmtpPort(25);
		yahoo_info.setUseSSL(false);
		yahoo_info.setUseTLS(false);
		yahoo_info.setMailServerValue(EmailServerType.Yahoo.getType());
		mailListStore.add(yahoo_info);
		
		live_info = new MailServiceInfo();
		live_info.setName(UIContext.Constants.liveMail());
		live_info.setMailServer(UIContext.Constants.liveMailServer());
		live_info.setSmtpPort(25);
		live_info.setUseSSL(false);
		live_info.setUseTLS(true);
		live_info.setMailServerValue(EmailServerType.Live.getType());
		mailListStore.add(live_info);

		other_info = new MailServiceInfo();
		other_info.setName(UIContext.Constants.otherMail());
		other_info.setMailServer("");
		other_info.setSmtpPort(25);
		other_info.setUseSSL(false);
		other_info.setUseTLS(false);
		other_info.setMailServerValue(EmailServerType.Other.getType());
		mailListStore.add(other_info);
		return mailListStore;
	}
	
	ComboBox<MailServiceInfo> serviceCombo;
	PasswordTextField pwdTF;

	private NumberField mailserverPortTF;

	private CheckBox useSsl;

	private TextField<String> mailUserTF;

	private CheckBox starttls;
	
	private CheckBox mailAuth;

	private CheckBox proxyAuth;

	private LabelField mailUserLabel;

	private LabelField mailPwdLabel;

	private FieldSet proxyFieldSet;
	private FieldSet mailFieldSet;
	public void addEmailSection(LayoutContainer mailCon)
	{
		
		TableData td = new TableData();
		td.setColspan(2);
		
	    mailFieldSet = new FieldSet();   
	    mailFieldSet.setHeadingHtml(UIContext.Constants.settingsEmailSettings());
	    mailFieldSet.setStylePrimaryName("restoreWizardSubItem");
	    mailFieldSet.setCheckboxToggle(false);   
	    mailFieldSet.setCollapsible(false);
		{
			TableLayout tablePasswordLayout = new TableLayout();
			tablePasswordLayout.setCellPadding(0);
			tablePasswordLayout.setCellSpacing(0);
			tablePasswordLayout.setColumns(2);
			tablePasswordLayout.setCellPadding(0);
			tablePasswordLayout.setCellSpacing(4);
			tablePasswordLayout.setWidth("100%");		
			mailFieldSet.setLayout(tablePasswordLayout);	
		}
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.mailService());		
		mailFieldSet.add(label);

		serviceCombo = new BaseComboBox<MailServiceInfo>();
		serviceCombo.ensureDebugId("90ae2e5f-5710-47b8-96b1-47e73335e732");
		serviceCombo.setDisplayField(MailServiceInfo.NAMEFIELD);
		serviceCombo.setEditable(false);		
		serviceCombo.setStore(this.CreateMailServiceInfo());
		serviceCombo
				.addSelectionChangedListener(new SelectionChangedListener<MailServiceInfo>() {
					@Override
					public void selectionChanged(
							SelectionChangedEvent<MailServiceInfo> se) {
						
							if (serviceCombo.getValue() !=other_info) {
								mailserverTF.setEnabled(false);
								mailserverTF.setValue(serviceCombo.getValue().getMailServer());
								mailserverPortTF.setValue(serviceCombo.getValue().getSmtpPort());
								useSsl.setValue(serviceCombo.getValue().isUseSSL());
								starttls.setValue(serviceCombo.getValue().isUseTLS());
								
							} else {
								mailserverTF.setEnabled(true);
							}
						
					}
				});
		
		mailFieldSet.add(serviceCombo);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsMailServer());		
		mailFieldSet.add(label);
		
		{//to composite the server host port 
			LayoutContainer tablePasswordContainer = new LayoutContainer();
			TableLayout tablePasswordLayout = new TableLayout();
			tablePasswordLayout.setCellPadding(0);
			tablePasswordLayout.setCellSpacing(0);
			tablePasswordLayout.setColumns(5);
			tablePasswordLayout.setWidth("100%");		
			tablePasswordContainer.setLayout(tablePasswordLayout);	

			mailserverTF = new TextField<String>();
			mailserverTF.ensureDebugId("024a4feb-26e1-465c-b8b0-8081a6649e89");
			mailserverTF.setMaxLength(128);		// Fix (18709243)
			// Tooltip
			Utils.addToolTip(mailserverTF, UIContext.Messages.settingsMailServerTooltip(UIContext.productNameD2D));
			mailserverTF.setValidator(new Validator() { // Fix (18663653)
				@Override
				public String validate(Field<?> field, String value) {
					// User Name validation format. (kimwo01): 10-26-2009
					//if (value.indexOf("\/"[]:|<>+=;,?*@") >= 0) // total 14 character set.
					return ValidationInvalidChar (value);
				}
			});
			mailserverTF.setWidth(MIN_FIELD_WIDTH-103);
			TableData td3 = new TableData();
			td3.setColspan(3);
			tablePasswordContainer.add(mailserverTF,td3);
			
			label = new LabelField();
			label.setValue(UIContext.Constants.settingsMailServerPort());		
			tablePasswordContainer.add(label);
	
			mailserverPortTF = new NumberField();
			mailserverPortTF.ensureDebugId("0fb0a96a-12fb-4a80-87f0-d45baf862fac");
			mailserverPortTF.setValue(25);
			mailserverPortTF.setMaxLength(5);
			mailserverPortTF.setWidth(60);
			mailserverPortTF.setAllowDecimals(false);
			mailserverPortTF.setMaxValue(65535);
			mailserverPortTF.setMinValue(0);
			TableData td4 = new TableData();
			td4.setWidth(""+60);
			tablePasswordContainer.add(mailserverPortTF,td4);
			
			mailFieldSet.add(tablePasswordContainer);
		}
		
		mailAuth = new CheckBox();
		mailAuth.ensureDebugId("0202bf06-ab6a-443e-97a9-845705869385");
		mailAuth.setBoxLabel(UIContext.Constants.requiresAuth());
		mailAuth.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				mailAuth_b = mailAuth.getValue();
				mailAuthEnable();
			}
		});
		mailFieldSet.add(mailAuth,td);
		
		
		mailUserLabel = new LabelField();
		mailUserLabel.setValue(UIContext.Constants.mailUser());		
		mailFieldSet.add(mailUserLabel);

		TableData data = new TableData();
		data.setStyleName("mailServerTextFieldWidth");
		mailUserTF = new TextField<String>();
		mailUserTF.ensureDebugId("300ea716-f00d-419e-9de7-698209723068");
		mailUserTF.setMaxLength(128);
		
		// Tooltip
		Utils.addToolTip(mailUserTF, UIContext.Constants.settingsMailUserTooltip());
		//mailUserTF.setWidth(MIN_FIELD_WIDTH);
		mailUserTF.setWidth(300);
		mailFieldSet.add(mailUserTF, data);
		
		
		mailPwdLabel = new LabelField();
		mailPwdLabel.setValue(UIContext.Constants.mailPassword());		
		mailFieldSet.add(mailPwdLabel);
		pwdTF = new PasswordTextField();
		pwdTF.ensureDebugId("f3a7a7aa-b33b-4f74-8e9b-98ef61675d44");
		Utils.addToolTip(pwdTF, UIContext.Constants.settingsMailPasswdTooltip());
		pwdTF.setMaxLength(128);		
		pwdTF.setPassword(true);

		pwdTF.setWidth(300);
		mailFieldSet.add(pwdTF, data);
		
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsSubject());		
		mailFieldSet.add(label);
		
		subjectTF = new TextField<String>();
		subjectTF.ensureDebugId("cff63e21-80de-4022-9edb-8f906d30a60b");
		subjectTF.setMaxLength(128);		// Fix (18709243)
		// Tooltip
		String subjectTooltip = "";
		if(appType == AppType.D2D){
			subjectTooltip = UIContext.Messages.settingsSubjectTooltip(UIContext.productNameD2D);
		}else if(appType == AppType.VSPHERE){
			subjectTooltip = UIContext.Messages.settingsSubjectTooltip(UIContext.productNamevSphere);
		}else if(appType == AppType.VCM){
			subjectTooltip = UIContext.Messages.settingsSubjectTooltip(UIContext.productNameVCM);
		}
		Utils.addToolTip(subjectTF, subjectTooltip);
		/*// There is no restriction in subject item. (Please check outlook express.)
		subjectTF.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				// User Name validation format. (kimwo01): 10-26-2009
				//if (value.indexOf("\/"[]:|<>+=;,?*@") >= 0) // total 16 character set.
				return ValidationInvalidChar (value);
			}
		});
		*/
		subjectTF.setWidth(300);
		mailFieldSet.add(subjectTF, data);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsFrom());		
		mailFieldSet.add(label);
		
		fromTF = new TextField<String>();
		fromTF.ensureDebugId("8a6b4881-29b1-4863-ba43-8fe49aad1dfd");
		fromTF.setMaxLength(128);		// Fix (18709243)
		// Tooltip
		Utils.addToolTip(fromTF, UIContext.Messages.settingsFromTooltip(UIContext.productNameD2D));
		// It could be email and server. so we cannot define ONLY email.
		/*fromTF.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				String pattenStr = ".+@.+";
				// E-mail validation format. (kimwo01): 10-26-2009
				return value.matches(pattenStr) ? null 
						: UIContext.Constants.settingsInvalidFromEmailErrorMsg();
			}
		});*/
		
		fromTF.setWidth(300);
		mailFieldSet.add(fromTF, data);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsRecipients());		
		mailFieldSet.add(label);	
		
		recipientsTF = new TextField<String>();
		recipientsTF.ensureDebugId("0afdc20c-cc60-4507-8792-41606860373f");
		recipientsTF.setMaxLength(128);		// Fix (18709243)
		// Tooltip
		Utils.addToolTip(recipientsTF, UIContext.Constants.settingsRecipientsTooltip());
//		recipientsTF.setValidator(new Validator() { // Fix (18663653)
//			@Override
//			public String validate(Field<?> field, String value) {
//				// E-mail validation format. (kimwo01): 10-26-2009
//				return validateRecipientsEmailFormat (value);
//			}
//		});
		recipientsTF.setWidth(300);
		mailFieldSet.add(recipientsTF, data);

		FlexTable optionsTable = new FlexTable();
		optionsTable.setWidth("98%");
		useSsl = new CheckBox();
		useSsl.ensureDebugId("5feb8d20-d762-42e2-a74c-b9e4c6d9b3ac");
		useSsl.setBoxLabel(UIContext.Constants.useSsl());
		Utils.addToolTip(useSsl, UIContext.Constants.useSslToolTip());
		useSsl.addListener(Events.Change, new Listener<FieldEvent>()
		{
			

			@Override
			public void handleEvent(FieldEvent be) {
				useSsl_b = useSsl.getValue();
			}
		});
		optionsTable.setWidget(0, 0, useSsl);
//		mailFieldSet.add(useSsl);

		{
//			LayoutContainer tablePasswordContainer = new LayoutContainer();
//			TableLayout tablePasswordLayout = new TableLayout();
//			tablePasswordLayout.setCellPadding(0);
//			tablePasswordLayout.setCellSpacing(0);
//			tablePasswordLayout.setColumns(2);
//			tablePasswordLayout.setWidth("100%");		
//			tablePasswordContainer.setLayout(tablePasswordLayout);	

			starttls = new CheckBox();
			starttls.ensureDebugId("fb11c9d9-3204-448d-9cbe-7be6aecd454d");
			starttls.setBoxLabel(UIContext.Constants.starttls());
			Utils.addToolTip(starttls, UIContext.Constants.starttllsToolTip());
			starttls.addListener(Events.Change, new Listener<FieldEvent>()
			{
				
	
			
	
				@Override
				public void handleEvent(FieldEvent be) {
					starttls_b = starttls.getValue();
				}
			});
			optionsTable.setWidget(0, 1, starttls);
//			tablePasswordContainer.add(starttls);
			
			useHTMLFormat = new CheckBox();
			useHTMLFormat.ensureDebugId("d5e2ec3a-0ed9-433d-82e3-a1aa72a8d5ea");
			useHTMLFormat.setBoxLabel(UIContext.Constants.advancedUseHTMLFormat());
			Utils.addToolTip(useHTMLFormat, UIContext.Constants.useHTMLFormatToolTip());
			useHTMLFormat.addListener(Events.Change, new Listener<FieldEvent>()
			{
				@Override
				public void handleEvent(FieldEvent be) {
					HTMLFormat = useHTMLFormat.getValue();
				}
			});
			useHTMLFormat.setValue(HTMLFormat);
			optionsTable.setWidget(0, 2, useHTMLFormat);
//			tablePasswordContainer.add(useHTMLFormat);
			data = new TableData();
			data.setWidth("98%");
			data.setColspan(2);
			mailFieldSet.add(optionsTable, data);
		}
		
		mailCon.add(mailFieldSet);
	}

	private int validateRecipientsEmailFormat(String value) {
		if (value == null) {
			return ERR_RECIPIENTS_BLANK;
		}
		String[] emails = value.split(";");
		int count = 0;
		for (int i = 0; i < emails.length; i++) {
			String email = emails[i].trim();// allowed space begin or end of
											// email
			if (email.equals("")) {// allowed like this : ;mail@ca.com or
									// mail1@ca.com;;;mail2@ca.com or ;;
				continue;
			}
			String result = validateSingleEmailFormat(email);
			if (result != null) {
				return ERR_RECIPIENTS_INVALID;
			} else {
				count++;
			}
		}
		if (count == 0) {// not allowed like this: ; or ;; or ; ; ;; .There must
							// be at least one email
			return ERR_RECIPIENTS_BLANK;
		} else {
			return 0;
		}
	}

	private String validateSingleEmailFormat(String value) {
		if (value == null) {
			return UIContext.Constants.settingsInvalidEmailFormatErrorMsg();
		} else if (!value.toUpperCase().matches(
				EMAIL_PATTEN)) {
			return UIContext.Constants.settingsInvalidEmailFormatErrorMsg();
		} else {
			return null;
		}
	}		
	public int ValidationEmailFormat (String value) 
	{
		String emailStr = value;
		String subStr   = ""; 
		int ret = 0;
		
		int pos = emailStr.indexOf(";");
		if (pos == -1) // If not exist.
		{
			// E-mail validation format. (kimwo01): 10-26-2009. 
			//E-mail address is composed of local part and domain part. According to 
			//http://en.wikipedia.org/wiki/Domain_name_registry, domain name is not limited to 4 characters.
			if (!value.toUpperCase().matches(EMAIL_PATTEN)) 
				return ERR_RECIPIENTS_INVALID;
		}
		else if (pos == 0) // ex)xxx.ca.com; --> invalid.
			return ERR_RECIPIENTS_INVALID;
		else	// ex) xxx.ca.com;yyy.ca.com;zzz.ca.com  (multi emails)
		{
			if (pos > 0)
			{
				subStr = emailStr.substring(0, pos);
				if ((ret = ValidationEmailFormat (subStr)) != 0)
					return ret;
				
				subStr = emailStr.substring(pos+1);
				if ((ret = ValidationEmailFormat (subStr)) != 0)
					return ret;
			}
			else
			{
				return ERR_RECIPIENTS_INVALID;
			}
		}

		return 0;
	}
	
	public String ValidationInvalidChar (String value)
	{
		// User Name validation format. (kimwo01): 10-26-2009
		//if (value.indexOf("\"[]:|<>+=;,?*") >= 0) // total 13 character set. - To support domain format ( tant-a01\xxxxxx)
		if (//(value.indexOf(47) >= 0) ||	 // 47: "slash" /
			//(value.indexOf(92) >= 0) ||  // 92: "back-slash" \
			(value.indexOf(64) >= 0) ||	 // 64: @				- To support email format xxxx@ca.com
			(value.indexOf(63) >= 0) ||  // 63: ?
			(value.indexOf(43) >= 0) ||	 // 43: +
			(value.indexOf(44) >= 0) ||	 // 44: "comma" , 
			(value.indexOf(61) >= 0) ||	 // 61: =
			(value.indexOf(58) >= 0) ||	 // 58: "colon" :
			(value.indexOf(59) >= 0) ||  // 59: "semicolon" ;
			(value.indexOf(42) >= 0) ||  // 42: "star" *
			(value.indexOf(91) >= 0) ||  // 91: [
			(value.indexOf(93) >= 0) ||  // 93: ]
			(value.indexOf(60) >= 0) ||  // 60: <
			(value.indexOf(62) >= 0) ||  // 62: >
			(value.indexOf(34) >= 0) ||  // 34: "quote"  "
			(value.indexOf(124)>= 0))   // 124: "vertical bar" |
		{
			// "Names may not consist entirely of periods and/or spaces, or contain these characters:\\/\"[]:|<>+=;,?*@";
			return UIContext.Constants.settingsProxyUsernameFormatErrorMsg();
		}
		
		return null;
		
	}
	
	public void addProxySection(LayoutContainer mailCon)
	{
		
	    proxyFieldSet = new FieldSet(); 
	    proxyFieldSet.ensureDebugId("a757ae5a-a1f7-40e8-8f5b-c13d906877fb");
	    proxyFieldSet.setHeadingHtml(UIContext.Constants.settingsEnableProxy());   
	    proxyFieldSet.setCheckboxToggle(true);   
		{
			TableLayout tablePasswordLayout = new TableLayout();
			tablePasswordLayout.setCellPadding(0);
			tablePasswordLayout.setCellSpacing(0);
			tablePasswordLayout.setColumns(2);
			tablePasswordLayout.setCellPadding(0);
			tablePasswordLayout.setCellSpacing(4);
			tablePasswordLayout.setWidth("100%");		
			proxyFieldSet.setLayout(tablePasswordLayout);	
		}
		
		proxyFieldSet.addListener(Events.Expand, new Listener<FieldSetEvent>()
				{
					@Override
					public void handleEvent(FieldSetEvent be) {
						setUseProxy(true);
						setProxyFieldsEnabled(true);	
					}
				});

		proxyFieldSet.addListener(Events.Collapse, new Listener<FieldSetEvent>()
				{
					@Override
					public void handleEvent(FieldSetEvent be) {
						setUseProxy(false);
						setProxyFieldsEnabled(false);	
					}
				});
		
		TableData td = new TableData();
		td.setColspan(2);
		
		addressLabel = new LabelField();
		addressLabel.setValue(UIContext.Constants.settingsProxyServer());
		proxyFieldSet.add(addressLabel);
		
		{
			LayoutContainer tablePasswordContainer = new LayoutContainer();
			TableLayout tablePasswordLayout = new TableLayout();
			tablePasswordLayout.setCellPadding(0);
			tablePasswordLayout.setCellSpacing(0);
			tablePasswordLayout.setColumns(5);
			tablePasswordLayout.setWidth("100%");		
			tablePasswordContainer.setLayout(tablePasswordLayout);	

			addressTF = new TextField<String>();
			addressTF.ensureDebugId("8f71c7c3-687d-44c8-a9a9-a15468da1e4a");
			addressTF.setMaxLength(128);		// Fix (18709243)
			Utils.addToolTip(addressTF, UIContext.Messages.settingsProxyServerTooltip(UIContext.productNameD2D));
			addressTF.setValidator(new Validator() {
				@Override
				public String validate(Field<?> field, String value) {
					// User Name validation format. (kimwo01): 10-26-2009
					//if (value.indexOf("\"[]:|<>+=;,?*@") >= 0) // total 14 character set.
					return ValidationInvalidChar (value);
				}
			});
			addressTF.setWidth(MIN_FIELD_WIDTH - 103);
			TableData td3 = new TableData();
			td3.setColspan(3);
			tablePasswordContainer.add(addressTF,td3);
		
			portLabel = new LabelField();
			portLabel.setValue(UIContext.Constants.settingsProxyPort());
			tablePasswordContainer.add(portLabel);
			portTF = new NumberField();
			portTF.ensureDebugId("29d25cdb-609d-40ab-a517-cd7709c9f14d");
			portTF.setValue(1080);
//			portTF.setMaxLength(128);		// Fix (18709243)
			portTF.setMaxLength(5);
			portTF.setAllowDecimals(false);
			portTF.setMaxValue(65535);
			portTF.setMinValue(0);
			portTF.setWidth(60);
			TableData td4 = new TableData();
			td4.setWidth(""+60);
			tablePasswordContainer.add(portTF,td4);
			proxyFieldSet.add(tablePasswordContainer);
		}
		
		proxyAuth = new CheckBox();
		proxyAuth.ensureDebugId("948f739b-5962-4173-8c87-4e6b00a4e232");
		proxyAuth.setBoxLabel(UIContext.Constants.requiresAuth());
		
		proxyAuth.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				proxyAuth_b = proxyAuth.getValue();
				proxyAuthEnable();
			}
		});
		proxyFieldSet.add(proxyAuth,td);
		
		usernameLabel = new LabelField();
		usernameLabel.setValue(UIContext.Constants.settingsProxyUsername());
		proxyFieldSet.add(usernameLabel);
		TableData data = new TableData();
		data.setStyleName("proxyTextFieldWidth");
		usernameTF = new TextField<String>();
		usernameTF.ensureDebugId("6e5fe8cc-6356-41c9-8617-1c4624d7e52b");
		usernameTF.setMaxLength(128);		// Fix (18709243)
		Utils.addToolTip(usernameTF, UIContext.Constants.settingsProxyUserTooltip());
		usernameTF.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				// E-mail validation format. (kimwo01): 10-26-2009
				//if (!value.toUpperCase().matches("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}")) 
				//	return "This field must be a valid email address";

				// User Name validation format. (kimwo01): 10-26-2009
				//if (value.indexOf("\"[]:|<>+=;,?*@") >= 0) // total 14 character set.
				return ValidationInvalidChar (value);
			}
		});

		usernameTF.setWidth("100%");
		proxyFieldSet.add(usernameTF, data);
		
		passwordLabel = new LabelField();
		passwordLabel.setValue(UIContext.Constants.settingsProxyPassword());
		proxyFieldSet.add(passwordLabel);
		passwordTF= new PasswordTextField();
		passwordTF.ensureDebugId("c5cda69d-7129-4502-93de-f3d6d466dccb");
		passwordTF.setMaxLength(128);		// Fix (18709243)
		Utils.addToolTip(passwordTF, UIContext.Constants.settingsProxyPasswordTooltip());
		passwordTF.setWidth("100%");
		passwordTF.setPassword(true);
		proxyFieldSet.add(passwordTF, data);
		mailCon.add(proxyFieldSet);
	}
	
	private void setProxyFieldsEnabled(boolean enabled)
	{
		setUseProxy(enabled);
		
		addressLabel.setEnabled(enabled);
		portLabel.setEnabled(enabled);
		addressTF.setEnabled(enabled);
		portTF.setEnabled(enabled);
		proxyAuth.setEnabled(enabled);
		
		if(enabled)
		{
			if(proxyAuth.getValue())
			{
				usernameLabel.setEnabled(enabled);
				passwordLabel.setEnabled(enabled);
				usernameTF.setEnabled(enabled);
				passwordTF.setEnabled(enabled);
			}
		}else{
			usernameLabel.setEnabled(enabled);
			passwordLabel.setEnabled(enabled);
			usernameTF.setEnabled(enabled);
			passwordTF.setEnabled(enabled);
		}
	}
	
	public void setSettings(IEmailConfigModel model)
	{
		if (model != null)
		{
			/**alert mail pr */
			Integer mailService = model.getMailService();
			if(mailService==null) 
				serviceCombo.setValue(other_info);	
			else
				if(model.getMailService()== yahoo_info.getMailServerValue())
					serviceCombo.setValue(yahoo_info);
					else
						if(model.getMailService() == live_info.getMailServerValue())
							serviceCombo.setValue(live_info);
						else
							if(model.getMailService() == google_info.getMailServerValue())
								serviceCombo.setValue(google_info);
							else
								if(model.getMailService() == other_info.getMailServerValue())
									serviceCombo.setValue(other_info);			
			
			pwdTF.setValue(model.getMailPwd());
			mailserverTF.setValue(model.getSMTP());
			
			if(model.isEnableSsl()!=null){
				this.useSsl.setValue(model.isEnableSsl());
			}else{
				this.useSsl.setValue(false);	
			}
			
			if(model.getMailUser()!=null) 
				mailUserTF.setValue(model.getMailUser());
			
			if(model.isEnableTls()!=null) {
				this.starttls.setValue(model.isEnableTls());
			}else 
				this.starttls.setValue(false);
			
			if(model.isEnableMailAuth()!=null)
				mailAuth.setValue(model.isEnableMailAuth());
			else 
				mailAuth.setValue(false);

			if(model.getSmtpPort()!=null && model.getSmtpPort().intValue() != 0)
			{
				mailserverPortTF.setValue(model.getSmtpPort());
			}
			
			if (model.getSubject() != null && !model.getSubject().isEmpty())
			{
				subjectTF.setValue(model.getSubject());
			}
			else
			{
				String defaultSubject = "";
				if(appType == AppType.D2D){
					defaultSubject = UIContext.Messages.advancedDefaultEmailSubject(UIContext.productNameD2D);
				}else if(appType == AppType.VSPHERE){
					defaultSubject = UIContext.Messages.advancedDefaultEmailSubject(UIContext.productNamevSphere);
				}else if(appType == AppType.VCM){
					defaultSubject = UIContext.Messages.advancedDefaultEmailSubject(UIContext.productNameVCM);
				}
				subjectTF.setValue(defaultSubject);
			}
			
			fromTF.setValue(model.getFromAddress());
			
			if (model.getEnableHTMLFormat() != null)
			{
				useHTMLFormat.setValue(model.getEnableHTMLFormat());
			}
			else
			{
				useHTMLFormat.setValue(true);
			}
			
			try{
				StringBuilder rValue = new StringBuilder();
				if (model.getRecipients() != null)
				{
					for (int i = 0; i < model.getRecipients().size(); i++)
					{
						rValue.append(model.getRecipients().get(i));
						if (i<(model.getRecipients().size()-1))
							rValue.append(";");
					}
					recipientsTF.setValue(rValue.toString());
				}
			}
			catch (Exception e)
			{
				
			}
			
			//Proxy Settings
			
			if (model.isEnableProxy() != null){
				if (model.isEnableProxy().booleanValue()){
					proxyFieldSet.collapse();
					proxyFieldSet.expand();
				}else{
					proxyFieldSet.collapse();
				}
			}
			else
				proxyFieldSet.setExpanded(false);

			if(model.getProxyAddress()!=null) 
				addressTF.setValue(model.getProxyAddress());
			
			if(model.getProxyPassword()!=null) 
				passwordTF.setValue(model.getProxyPassword());
			
			if(model.getProxyPort()!=null && model.getProxyPort().intValue() != 0) 
				portTF.setValue(model.getProxyPort());
			
			if(model.getProxyUsername()!=null) 
				usernameTF.setValue(model.getProxyUsername());
			
			if(model.isEnableProxyAuth()!=null) 
				proxyAuth.setValue(model.isEnableProxyAuth());
			else 
				proxyAuth.setValue(false);
			
//			if(isTrueValue(model.getEnableHTMLFormat()))
//				useHTMLFormat.setValue(true);
//			else {
//				useHTMLFormat.setValue(false);
//			}

		}
		else
		{			
			//Defaults
			proxyFieldSet.setExpanded(false);
			useHTMLFormat.setValue(true);
			mailAuth.setValue(false);
			proxyAuth.setValue(false);
		}
		
		
		if (serviceCombo.getValue() !=other_info)
		{
			mailserverTF.setEnabled(false);
		} else {
			mailserverTF.setEnabled(true);
		}
		
		HTMLFormat = useHTMLFormat.getValue();
		mailAuth_b = mailAuth.getValue();
		mailAuthEnable();
		
		useSsl_b = useSsl.getValue();
		starttls_b = starttls.getValue();
		UseProxy = proxyFieldSet.isExpanded();
		proxyAuth_b = proxyAuth.getValue();
		proxyAuthEnable();
		
		setProxyFieldsEnabled(UseProxy);
	}

	private boolean isTrueValue(Boolean b) {
		return b != null && b;
	}
	
	private void proxyAuthEnable() {
		usernameTF.setEnabled(proxyAuth_b);
		usernameLabel.setEnabled(proxyAuth_b);
		passwordLabel.setEnabled(proxyAuth_b);
		passwordTF.setEnabled(proxyAuth_b);
	}
	
	public void saveSettings(IEmailConfigModel model)
	{
		Boolean ep = isUseProxy();		 
		model.setEnableProxy(ep);
		if (ep)
		{
			model.setProxyAddress(addressTF.getValue());
			model.setProxyPassword(passwordTF.getValue());
			Number port = portTF.getValue();
			if (port != null)
			{
				model.setProxyPort(port.intValue());
			}
			model.setProxyUsername(usernameTF.getValue());
		}
		
		/**alert mail pr */
		model.setMailService(serviceCombo.getValue().getMailServerValue());
		model.setMailPwd(pwdTF.getValue());
		model.setEnableSsl(this.isUseSsl_b());
		Number number = this.mailserverPortTF.getValue();
		
		if(number!=null)
		{
		model.setSmtpPort(number.intValue());
		}
		model.setMailUser(mailUserTF.getValue());
		model.setEnableTls(starttls_b);
		model.setEnableMailAuth(mailAuth_b);
		model.setEnableProxyAuth(proxyAuth_b);
		
		model.setSMTP(mailserverTF.getValue());
		model.setSubject(subjectTF.getValue());
		model.setContent("");
		model.setFromAddress(fromTF.getValue());
		
		
		model.setEnableHTMLFormat(isHTMLFormat());
		
		//Parse the recipients
		String allRecipients = recipientsTF.getValue();
		String[] r = new String[0];
		if (allRecipients != null)
			r = allRecipients.split(";");
		
		ArrayList<String> recipients = new ArrayList<String>();
		for (int i = 0; i < r.length; i++)
		{
			recipients.add(r[i]);
		}		
		
		model.setRecipients(recipients);
	}

	public void setUseProxy(boolean useProxy) {
		UseProxy = useProxy;
	}

	public boolean isUseProxy() {
		return UseProxy;
	}
	
	private void mailAuthEnable() {
		mailUserLabel.setEnabled(mailAuth_b);
		mailPwdLabel.setEnabled(mailAuth_b);
		mailUserTF.setEnabled(mailAuth_b);
		pwdTF.setEnabled(mailAuth_b);
	}
}
