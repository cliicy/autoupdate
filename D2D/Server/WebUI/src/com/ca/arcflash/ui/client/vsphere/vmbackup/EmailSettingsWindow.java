package com.ca.arcflash.ui.client.vsphere.vmbackup;

import java.util.ArrayList;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.EmailServerType;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;

public class EmailSettingsWindow extends Window {
	
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
	
	private CheckBox useHTMLFormat; 
	
	private EmailSettingsWindow thisWindow;
	
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
	
	
	private BackupSettingsModel tempModel;
	private boolean HTMLFormat = true;
	private boolean UseProxy = false;
	
	/** Alert Email enhancement PR */
	private boolean useSsl_b = false;
	private boolean starttls_b =false;
	private boolean mailAuth_b =false;
	private boolean proxyAuth_b = false;
	
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

	public EmailSettingsWindow()
	{
		this.setResizable(false);
		thisWindow = this;
		thisWindow.setHeadingHtml(UIContext.Constants.settingsMailTitle());
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(1);
		layout.setCellPadding(0);
		layout.setCellSpacing(0);
		this.setLayout(layout);
		

		
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
		}
		addEmailSection(mailCon);
		mailCon.add(new Html("<HR>"));
		addProxySection(mailCon);
		
		ScrollPanel panel = new ScrollPanel();
		panel.add(mailCon);
		panel.setHeight("400");
		this.add(panel);
		
		okButton = new Button();
		okButton.ensureDebugId("C4446D61-0E6B-43d8-9FA9-1B5BA3099CDE");
		okButton.setText(UIContext.Constants.ok());
		okButton.setMinWidth(MIN_WIDTH);		
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if ((!mailserverTF.validate()) ||
					(!subjectTF.validate()) ||
					(!fromTF.validate()) ||
					(!recipientsTF.validate()) ||
					(!addressTF.validate()) ||
					(!portTF.validate()) ||
					(!usernameTF.validate()) ||
					(!passwordTF.validate())) 
				{
					return;
				}
				
				int ret = validate();
				String message = "";
				
				switch(ret)
				{
					case 0:
						thisWindow.hide(okButton);
						break;
					case ERR_MAILSERVER_INVALID:
						message = UIContext.Constants.settingsMailServerError();
						break;
					case ERR_FROM_INVALID:
						message = UIContext.Constants.settingsFromFieldError();
						break;
					case ERR_RECIPIENTS_INVALID:
						message = UIContext.Constants.settingsRecipientsError();
						break;
					case ERR_MAILUSERNAME_INVALID:
						message = UIContext.Constants.mailUserNameEmptyError();
						break;
					case ERR_PROXYUSERNAME_INVALID:
						message = UIContext.Constants.proxyUserNameEmptyError();
						break;
					case ERR_PROXYSERVER_INVALID:
						message = UIContext.Constants.proxyServerEmptyError();
						break;
				}
				
				if (message.length() > 0)
				{
					MessageBox msg = new MessageBox();
					msg.setIcon(MessageBox.ERROR);
					msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
					msg.setMessage(message);
					msg.setModal(true);
					Utils.setMessageBoxDebugId(msg);
					msg.show();
				}
				
			}

			});		
		this.addButton(okButton);
		
		
		cancelButton = new Button();
		cancelButton.ensureDebugId("919DACA7-BC28-4a37-96E3-5CE455F35C85");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.setMinWidth(MIN_WIDTH);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
				
			}});		
		this.addButton(cancelButton);	
		
		helpButton = new Button();
		helpButton.ensureDebugId("DA74CC83-8255-4c30-96AE-A7E7F4C336E8");
		helpButton.setText(UIContext.Constants.help());
		helpButton.setMinWidth(MIN_WIDTH);
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				HelpTopics.showHelpURL(UIContext.externalLinks.getBackupSettingEmailHelp());
			}
		});
		this.addButton(helpButton);	
		
		this.setMinWidth(500);
	}
	
	protected int validate() {
		int ret = 0;
		
		if (mailserverTF == null ||
				mailserverTF.getValue() == null ||
				mailserverTF.getValue().trim().length() == 0)
		{
			ret = ERR_MAILSERVER_INVALID;
		}
		else if(mailAuth_b && (mailUserTF.getValue() == null || mailUserTF.getValue().trim().isEmpty())){

				ret = ERR_MAILUSERNAME_INVALID;
			
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
			ret = ERR_RECIPIENTS_INVALID;
		} 
		else if(UseProxy && (addressTF.getValue() == null || addressTF.getValue().trim().isEmpty()))
		{

			ret = ERR_PROXYSERVER_INVALID;
		}
		else if( UseProxy  && proxyAuth_b && (usernameTF.getValue() == null || usernameTF.getValue().trim().isEmpty()))
		{
			ret = ERR_PROXYUSERNAME_INVALID;
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
	class MailServiceInfo extends BaseModelData
	{
		private static final long serialVersionUID = 8832291240524409486L;
		public static final String NAMEFIELD = "Name";
		public static final String MAILSERVER = "MailServer";
		public static final String SSL = "ssl";
		public static final String TLS = "tls";
		public static final String SMTPPort = "smtpport";
		public static final String MAILSERVERVALUE = "mailServerValue";
		/**
		 * Google, Live, Yahoo, other Yahoo Outgoing Mail Server (SMTP) - smtp.mail.yahoo.com (port 25)
		 * Hotmail Outgoing Mail Server (SMTP) - smtp.live.com (SSL disabled, Tls enable, port 25)
		 *  smtp.gmail.com (SSL enabled, port 465)
			http://www.emailaddressmanager.com/tips/mail-settings.html
		 * @return
		 */
		public String getName() {
			return get(NAMEFIELD);
		}
		public void setName(String name) {
			set(NAMEFIELD, name);
		}
		
		public String getMailServer()
		{
			return get(MAILSERVER);
		}
		public void setMailServer(String mailServer)
		{
			set(MAILSERVER, mailServer);
		}
		public Boolean isUseSSL()
		{
			return get(SSL);
		}
		public void setUseSSL(Boolean useSSL)
		{
			set(SSL, useSSL);
		}
		public Boolean isUseTLS()
		{
			return get(TLS);
		}
		public void setUseTLS(Boolean useTLS)
		{
			set(TLS, useTLS);
		}
		public Integer getSmtpPort(){
			return get(SMTPPort);
		}
		public void setSmtpPort(Integer port){
			 set(SMTPPort,port);
		}
		public Integer getMailServerValue(){
			return get(MAILSERVERVALUE);
		}
		
		public void setMailServerValue(Integer mailServerValue){
			set(MAILSERVERVALUE,mailServerValue);
		}
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
	    mailFieldSet.ensureDebugId("89CA4313-E879-43d9-8EAC-23E6F3E25A24");
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
		serviceCombo.ensureDebugId("EBADD3A4-D3B5-4a4d-AE11-C809D382C1F6");
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
			mailserverTF.ensureDebugId("C909CB1A-BEF0-44f8-B17D-71E0529B99DA");
			mailserverTF.setMaxLength(128);		// Fix (18709243)
			// Tooltip
			Utils.addToolTip(mailserverTF, UIContext.Messages.settingsMailServerTooltip(UIContext.productNamevSphere));
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
			mailserverPortTF.ensureDebugId("FAFC7752-7964-4a75-A446-CEFD81A3F3D8");
			mailserverPortTF.setValue(25);
			mailserverPortTF.setMaxLength(6);
			mailserverPortTF.setWidth(60);
			TableData td4 = new TableData();
			td4.setWidth(""+60);
			tablePasswordContainer.add(mailserverPortTF,td4);
			
			mailFieldSet.add(tablePasswordContainer);
		}
		
		mailAuth = new CheckBox();
		mailAuth.ensureDebugId("1AE86BCB-15D7-49b9-A6E2-FD7EC07DDCD8");
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
		mailUserTF.ensureDebugId("4166315D-79F7-47a9-A44F-D9EF9ED6B90D");
		mailUserTF.setMaxLength(128);		
		// Tooltip
		Utils.addToolTip(mailUserTF, UIContext.Constants.settingsMailUserTooltip());
		//mailUserTF.setWidth(MIN_FIELD_WIDTH);
		mailUserTF.setWidth("99%");
		mailFieldSet.add(mailUserTF, data);
		
		
		mailPwdLabel = new LabelField();
		mailPwdLabel.setValue(UIContext.Constants.mailPassword());		
		mailFieldSet.add(mailPwdLabel);
		pwdTF = new PasswordTextField();
		pwdTF.ensureDebugId("2FD9B79F-BF45-4d1c-B976-E9CFEE4DF934");
		Utils.addToolTip(pwdTF, UIContext.Constants.settingsMailPasswdTooltip());
		pwdTF.setMaxLength(128);		
		pwdTF.setPassword(true);

		pwdTF.setWidth("99%");
		mailFieldSet.add(pwdTF, data);
		
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsSubject());		
		mailFieldSet.add(label);
		
		subjectTF = new TextField<String>();
		subjectTF.ensureDebugId("4E96BFCD-28E6-4f59-BC0F-FC94AE40A708");
		subjectTF.setMaxLength(128);		// Fix (18709243)
		// Tooltip
		Utils.addToolTip(subjectTF, UIContext.Messages.settingsSubjectTooltip(UIContext.productNamevSphere));
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
		subjectTF.setWidth("99%");
		mailFieldSet.add(subjectTF, data);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsFrom());		
		mailFieldSet.add(label);
		
		fromTF = new TextField<String>();
		fromTF.ensureDebugId("B3E18065-E912-4a86-AEAF-FE7AB9523029");
		fromTF.setMaxLength(128);		// Fix (18709243)
		// Tooltip
		Utils.addToolTip(fromTF, UIContext.Messages.settingsFromTooltip(UIContext.productNamevSphere));
		/*// It could be email and server. so we cannot define ONLY email.
		fromTF.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				// E-mail validation format. (kimwo01): 10-26-2009
				return ValidationEmailFormat (value);
			}
		});
		*/
		fromTF.setWidth("99%");
		mailFieldSet.add(fromTF, data);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsRecipients());		
		mailFieldSet.add(label);	
		
		recipientsTF = new TextField<String>();
		recipientsTF.ensureDebugId("C9D03872-3334-44a5-BD40-70D145BD7BF8");
		recipientsTF.setMaxLength(128);		// Fix (18709243)
		// Tooltip
		Utils.addToolTip(recipientsTF, UIContext.Constants.settingsRecipientsTooltip());
		recipientsTF.setValidator(new Validator() { // Fix (18663653)
			@Override
			public String validate(Field<?> field, String value) {
				// E-mail validation format. (kimwo01): 10-26-2009
				return ValidationEmailFormat (value);
			}
		});
		recipientsTF.setWidth("99%");
		mailFieldSet.add(recipientsTF, data);

		FlexTable optionsTable = new FlexTable();
		optionsTable.setWidth("98%");
		useSsl = new CheckBox();
		useSsl.ensureDebugId("2DE6D3EB-E67E-4a64-9142-47EA11FA3028");
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
			starttls.ensureDebugId("AE6BACEA-AA82-4d76-997F-580362D8EEE4");
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
			useHTMLFormat.ensureDebugId("9FC196D6-C5F4-42a5-8B9C-5A3BA2F55DE2");
			useHTMLFormat.setBoxLabel(UIContext.Constants.advancedUseHTMLFormat());
			Utils.addToolTip(useHTMLFormat, UIContext.Constants.useHTMLFormatToolTip());
			useHTMLFormat.addListener(Events.Change, new Listener<FieldEvent>()
			{
				@Override
				public void handleEvent(FieldEvent be) {
					HTMLFormat = useHTMLFormat.getValue();
				}
			});
			optionsTable.setWidget(0, 2, useHTMLFormat);
//			tablePasswordContainer.add(useHTMLFormat);
			data = new TableData();
			data.setWidth("98%");
			data.setColspan(2);
			mailFieldSet.add(optionsTable, data);
		}
		
		mailCon.add(mailFieldSet);
	}
	
	public String ValidationEmailFormat (String value) 
	{
		String emailStr = value;
		String subStr   = "", retStr = "";
		
		int pos = emailStr.indexOf(";");
		if (pos == -1) // If not exist.
		{
			// E-mail validation format. (kimwo01): 10-26-2009. 
			//E-mail address is composed of local part and domain part. According to 
			//http://en.wikipedia.org/wiki/Domain_name_registry, domain name is not limited to 4 characters.
			if (!value.toUpperCase().matches("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}")) 
				return UIContext.Constants.settingsInvalidEmailFormatErrorMsg();
		}
		else if (pos == 0) // ex)xxx.ca.com; --> invalid.
			return UIContext.Constants.settingsInvalidEmailFormatErrorMsg();
		else	// ex) xxx.ca.com;yyy.ca.com;zzz.ca.com  (multi emails)
		{
			if (pos > 0)
			{
				subStr = emailStr.substring(0, pos);
				if ((retStr = ValidationEmailFormat (subStr)) != null)
					return retStr;
				
				subStr = emailStr.substring(pos+1);
				if ((retStr = ValidationEmailFormat (subStr)) != null)
					return retStr;
			}
			else
			{
				return UIContext.Constants.settingsInvalidEmailFormatErrorMsg();
			}
		}

		return null;
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
	    proxyFieldSet.ensureDebugId("265889F1-BFAE-493a-A5D0-0B17DFB15355");
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
			addressTF.ensureDebugId("26B553C4-6E8D-473f-8588-B1B7BA43D6D8");
			addressTF.setMaxLength(128);		// Fix (18709243)
			Utils.addToolTip(addressTF, UIContext.Messages.settingsProxyServerTooltip(UIContext.productNamevSphere));
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
			portTF.ensureDebugId("5A270790-1A58-4571-AD88-8A2568D88A0E");
			portTF.setValue(1080);
			portTF.setMaxLength(128);		// Fix (18709243)
			portTF.setWidth(60);
			TableData td4 = new TableData();
			td4.setWidth(""+60);
			tablePasswordContainer.add(portTF,td4);
			proxyFieldSet.add(tablePasswordContainer);
		}
		
		proxyAuth = new CheckBox();
		proxyAuth.ensureDebugId("280D103B-D7E0-43c5-B737-71770F896CF9");
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
		usernameTF.ensureDebugId("B523148B-CA35-4f59-9DB5-9BFFC92132A4");
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
		passwordTF.ensureDebugId("06AA23F5-51CA-47ee-AD80-FE02D4E16114");
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
	
	public void setSettings(VSphereBackupSettingModel model)
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
				subjectTF.setValue(UIContext.Messages.advancedDefaultEmailSubject(UIContext.productNamevSphere));
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
				if (model.Recipients != null)
				{
					for (int i = 0; i < model.Recipients.size(); i++)
					{
						rValue.append(model.Recipients.get(i));
						if (i<(model.Recipients.size()-1))
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
			
			if(!isTrueValue(model.getEnableEmail()) 
					&& !isTrueValue(model.getEnableEmailOnSuccess()) 
					&& !isTrueValue(model.getEnableSpaceNotification()))
				useHTMLFormat.setValue(true);

		}
		else
		{			
			//Defaults
			tempModel.setEnableProxy(false);
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
	
	public void saveSettings(VSphereBackupSettingModel model)
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
		String[] r = allRecipients.split(";");
		
		model.Recipients = new ArrayList<String>();
		for (int i = 0; i < r.length; i++)
		{
			model.Recipients.add(r[i]);
		}		
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
