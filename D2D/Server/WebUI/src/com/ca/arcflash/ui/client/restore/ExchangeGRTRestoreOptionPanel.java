package com.ca.arcflash.ui.client.restore;

import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.AccountModel;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.EncrypedRecoveryPoint;
import com.ca.arcflash.ui.client.model.ExchVersion;
import com.ca.arcflash.ui.client.model.ExchangeGRTOptionModel;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.restore.cas.CASSelectionPanel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ExchangeGRTRestoreOptionPanel extends RestoreOptionsPanel {
	// service
	final LoginServiceAsync service = GWT.create(LoginService.class);

	AsyncCallback<Boolean> callbackValidate;

	private RadioGroup radioGroupTo;
	private Radio radioToOriginalLocation;
	private Radio radioToFileSystem;
	private Radio radioToExchangeServer;

	// to original
	LayoutContainer containerOrg;
	private TextField<String> textFieldOriginalUser;
	private PasswordTextField textFieldOriginalPassword;	

	// to file system
	LayoutContainer containerFS;
	private PathSelectionPanel pathSelection;
	private Radio radioOverwrite;
	private Radio radioRename;

	// to alternate
	LayoutContainer containerAlter;
	private TextField<String> textFieldExchangeServer;
	private TextField<String> textFieldExchangeUser;
	private PasswordTextField textFieldExchangePassword;

	private Button buttonBrowseExchOrg;
	private TextField<String> textFieldExchangeDestination;
	private BrowseExchOrgDialog dialogBrowseExchOrg;
	private int nDestinationServerVersion;
	private String strDestination = new String("");
	private String strFolder = new String("");

	
	private Html htmlLine;

	// common
	private ExchangeGRTRestoreOptionPanel thisPanel;
	private static int MIN_WIDTH = 90;
	private static int MIN_FIELD_WIDTH = 250;

	private LabelField noteLabel;
	private LabelField lableFieldNote2003;
	private LabelField lableFieldNote2007;
	private LabelField lableFieldNote2010;
	private LabelField lableFieldNote2013UserCaution;
	private LabelField lableFieldNote64Bit;
	private LabelField lableFieldNoteSameDomain;

	public static final long RESTORE_TO_ORIGINAL_LOCATION = 0x00000000;
	public static final long AFDDO_R_EXCH_ALTERNATE_LOCATION = 0x00000001;
	public static final long AFDDO_R_EXCH_RESTORE_TO_DISK = 0x00000002;
	public static final long AFDDO_R_EXCH_RESOLVE_NAME_COLLISION = 0x00010000;

	private Validator validatorUserName = new Validator() {
		@Override
		public String validate(Field<?> field, String value) {
			if (value == null || value.indexOf('\\') < 1 || (value.trim().indexOf('\\') == (value.trim().length() - 1))) {
				return UIContext.Constants.restoreExchangeInvalidUserName();
			}

			return null;
		}
	};

	private LabelField labelCas_original;
	private CASSelectionPanel casSelection_original;
	private CASSelectionPanel casSelection_alternate;
	private LabelField labelCas_alternate;

	public ExchangeGRTRestoreOptionPanel(RestoreWizardContainer restoreWizardWindow) {
		super(restoreWizardWindow);
		thisPanel = this;
		
		// set the default account after render
		addListener(Events.Render, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{				
				//rwWindow.mask(UIContext.Constants.restoreGRTLoadingRestoreOptions());
				String productName = UIContext.productNameD2D;
				if(UIContext.uiType == 1){
					productName = UIContext.productNamevSphere;
				}
				final MessageBox validatingBox = MessageBox.wait(UIContext.Messages.messageBoxTitleInformation(productName), UIContext.Constants.restoreGRTLoadingRestoreOptions(), "");
				Utils.setMessageBoxDebugId(validatingBox);
				// step 1: get the admin account 
				service.getAdminAccount(new BaseAsyncCallback<AccountModel>() {
					@Override
					public void onFailure(Throwable caught) {
						//the administrator account may does not exist.	
						if (validatingBox != null) 
						{
							validatingBox.close();
						}
					}
					@Override
					public void onSuccess(AccountModel result) {
						if (result != null && result.getUserName() != null && result.getPassword() != null)
						{							
							
							textFieldOriginalUser.setValue(result.getUserName());
							textFieldOriginalPassword.setValue(result.getPassword());
							textFieldExchangeUser.setValue(result.getUserName());
							textFieldExchangePassword.setValue(result.getPassword());
							validatingBox.close();
							
//							final AccountModel adminAccount = result;
//							
//							// step 2: validate the admin account
//							service.d2dExCheckUser("", result.getUserName(), result.getPassword(), new BaseAsyncCallback<Long>()
//							{
//								@Override
//								public void onFailure(Throwable caught)
//								{
//									if (validatingBox != null)
//									{
//										validatingBox.close();
//									}
//								}
//
//								@Override
//								public void onSuccess(Long result)
//								{
//									GWT.log("d2dExCheckUser Successfully", null);
//									if (result == 0)
//									{
//										// step 3: set the admin account if it is valid
//										textFieldOriginalUser.setValue(adminAccount.getUserName());
//										textFieldOriginalPassword.setValue(adminAccount.getPassword());
//										textFieldExchangeUser.setValue(adminAccount.getUserName());
//										textFieldExchangePassword.setValue(adminAccount.getPassword());
//									}
//									
//									if (validatingBox != null) 
//									{
//										validatingBox.close();
//									}
//								}
//							});							
							
						}
					}					
				});
			}

		});
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setStyleAttribute("margin", "5px");
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(1);
		tableLayout.setCellPadding(2);
		tableLayout.setCellSpacing(2);
		this.setLayout(tableLayout);
		this.setHeight("100%");

		this.add(renderHeaderSection());
		renderDestinations(this);

		htmlLine = new Html("<HR>");
		this.add(htmlLine);
		
		this.add(pwdPane);
		this.add(recoveryPointPasswordPanel);

		this.add(new Html("<HR>"));

		renderNotes(this);

		radioToOriginalLocation.setValue(true);
		
		showCasSelection();
	}

	private void renderNotes(LayoutContainer layoutContainer) {
		noteLabel = new LabelField(UIContext.Constants.restoreExchangeGRTNoteLabel());
		layoutContainer.add(noteLabel);
		noteLabel.addStyleName("restoreWizardLeftSpacing");
		noteLabel.setStyleAttribute("padding-bottom", "0px");
		
		LayoutContainer lc = new LayoutContainer();
		
		TableLayout tl = new TableLayout();
		tl.setWidth("95%");
		tl.setColumns(1);
		tl.setCellPadding(0);
		tl.setCellSpacing(0);
		lc.setLayout(tl);
		
		lc.setScrollMode(Scroll.AUTO);		
		//lc.setHeight(40);
		lc.setStyleAttribute("padding-top", "0px");

		lableFieldNote2003 = new LabelField(UIContext.Constants.restoreToExchange2003Note());
		lc.add(lableFieldNote2003);
		lableFieldNote2003.setStyleAttribute("padding-left", "16px");

		lableFieldNote2007 = new LabelField(UIContext.Constants.restoreToExchange2007Note());
		lc.add(lableFieldNote2007);
		lableFieldNote2007.setStyleAttribute("padding-left", "16px");

		lableFieldNote2010 = new LabelField(UIContext.Constants.restoreToExchange2010Note());
		lc.add(lableFieldNote2010);
		lableFieldNote2010.setStyleAttribute("padding-left", "16px");
		
		lableFieldNote2013UserCaution = new LabelField(UIContext.Constants.lableFieldNote2013UserCaution());
		lc.add(lableFieldNote2013UserCaution);
		lableFieldNote2013UserCaution.setStyleAttribute("padding-left", "16px");
		
		lableFieldNote64Bit = new LabelField(UIContext.Constants.scheduleCatalogExch64BitNotes());
		lc.add(lableFieldNote64Bit);
		lableFieldNote64Bit.setStyleAttribute("padding-left", "16px");
		
		lableFieldNoteSameDomain = new LabelField(UIContext.Constants.scheduleCatalogExchSameDomainNotes());
		lc.add(lableFieldNoteSameDomain);
		lableFieldNoteSameDomain.setStyleAttribute("padding-left", "16px");
		
		layoutContainer.add(lc);		
	}

	private void renderDestinations(LayoutContainer layoutContainer) {

		// Title
		Label label = new Label(UIContext.Constants.restoreDestination());
		label.addStyleName("restoreWizardSubItem");
		layoutContainer.add(label);

		// Description
		label = new Label(UIContext.Constants.restoreDestinationDescription());
		label.addStyleName("restoreWizardSubItemDescription");
		layoutContainer.add(label);

		// to Original Location
		renderToOriginalLocation(layoutContainer);

		// to File System
		renderToFileSystem(layoutContainer);		

		// to Alternate location		
		renderToAlternateLocation(layoutContainer);

		radioGroupTo = new RadioGroup();
		radioGroupTo.add(radioToOriginalLocation);
		radioGroupTo.add(radioToFileSystem);
		radioGroupTo.add(radioToExchangeServer);

		radioGroupTo.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				updateControlStatus();
				showNote();
			}
		});
	}
	
	private void renderToOriginalLocation(LayoutContainer layoutContainer) {
		radioToOriginalLocation = new Radio();
		radioToOriginalLocation.ensureDebugId("94b1156b-6e01-4631-b950-45ea38b536ee");
		radioToOriginalLocation.setBoxLabel(UIContext.Constants.restoreToOriginalLocation());
		radioToOriginalLocation.addStyleName("restoreWizardLeftSpacing");
		Utils.addToolTip(radioToOriginalLocation, UIContext.Constants.restoreToOriginalTooltip());
		layoutContainer.add(radioToOriginalLocation);
		
		containerOrg = new LayoutContainer();
		TableLayout layoutAccount = new TableLayout();
		layoutAccount.setColumns(2);
		layoutAccount.setCellPadding(4);
		layoutAccount.setCellSpacing(0);
		containerOrg.setLayout(layoutAccount);
		containerOrg.setStyleAttribute("padding-left", "20px");
		containerOrg.setStyleAttribute("padding-top", "0px");

		LabelField labelOrg = new LabelField(UIContext.Constants.destinationSettingsUserName());
		TableData tableDataAccount = new TableData();
		containerOrg.add(labelOrg, tableDataAccount);

		textFieldOriginalUser = new TextField<String>();
		textFieldOriginalUser.ensureDebugId("50f0613c-2240-4c14-b848-1614c0523139");
		textFieldOriginalUser.setWidth(MIN_FIELD_WIDTH);
		textFieldOriginalUser.setAllowBlank(false);
		textFieldOriginalUser.setValidator(validatorUserName);

		tableDataAccount = new TableData();
		containerOrg.add(textFieldOriginalUser, tableDataAccount);

		labelOrg = new LabelField(UIContext.Constants.destinationSettingsPassword());
		tableDataAccount = new TableData();
		containerOrg.add(labelOrg, tableDataAccount);

		textFieldOriginalPassword = new PasswordTextField();
		textFieldOriginalPassword.ensureDebugId("1bdafe8c-49f9-4d22-8f56-1b61f3e6390c");
		textFieldOriginalPassword.setPassword(true);
		textFieldOriginalPassword.setWidth(MIN_FIELD_WIDTH);
		tableDataAccount = new TableData();
		containerOrg.add(textFieldOriginalPassword, tableDataAccount);
		
		// cas server
		labelCas_original = new LabelField(UIContext.Constants.casServer());
		containerOrg.add(labelCas_original, tableDataAccount);
		casSelection_original = new CASSelectionPanel(this, CASSelectionPanel.CAS_TYPE_ORIGINAL);
		containerOrg.add(casSelection_original, tableDataAccount);
		

		layoutContainer.add(containerOrg);
	}
	
	private void renderToFileSystem(LayoutContainer layoutContainer) {
		radioToFileSystem = new Radio();
		radioToFileSystem.ensureDebugId("372f0c2f-7dbe-4eda-bab1-05a747ea12ef");
		Utils.addToolTip(radioToFileSystem, UIContext.Constants.restoreToFileSystemTooltip());
		radioToFileSystem.setBoxLabel(UIContext.Constants.restoreDumpEmailOnly());
		radioToFileSystem.addStyleName("restoreWizardLeftSpacing");

		pathSelection = new PathSelectionPanel(null);
		pathSelection.getDestinationTextField().ensureDebugId("9b38d9ab-0ab8-48b6-afa8-b85fe9f163ab");
		pathSelection.getDestinationBrowseButton().ensureDebugId("74c5457a-0805-4a23-9d0e-4a68b9027a00");

		//pathSelection.getDestinationTextField().setRegex(AbsoluteDirReg);
		//pathSelection.getDestinationTextField().getMessages().setRegexText(UIContext.Constants.restoreInvalidPath());		
		
		pathSelection.setWidth("450");
		pathSelection.setMode(PathSelectionPanel.RESTORE_ALT_MODE);
		pathSelection.setTooltipMode(PathSelectionPanel.TOOLTIP_RESTORE_ALT_MODE);
		//pathSelection.setAllowBlank(false);
		pathSelection.setPathFieldLength(290);
		pathSelection.addDebugId("5118CAC5-2C5B-490c-A2E8-CD85470770D1", 
				"AAA82064-BBEA-42c9-9256-097FC14190AA", "986B5B4A-692F-4cee-9358-0711CA125BDA");
		pathSelection.setChangeListener(new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {

			}
		});
		pathSelection.addListener(PathSelectionPanel.onDisconnectionEvent,
				new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {

					}

				});
		pathSelection.addStyleName("restoreWizardLeftSpacing");
		
		LayoutContainer containerToDisk = new LayoutContainer();		
		TableLayout layoutFS = new TableLayout();
		layoutFS.setColumns(2);
		layoutFS.setCellPadding(0);
		layoutFS.setCellSpacing(0);		
		containerToDisk.setLayout(layoutFS);
		
		containerToDisk.add(radioToFileSystem);
		containerToDisk.add(pathSelection);
		
		layoutContainer.add(containerToDisk);
		
		// options
		radioOverwrite = new Radio();
		radioOverwrite.ensureDebugId("a375172f-2470-4ec1-a028-00dafcd08c76");
		radioOverwrite.setBoxLabel(UIContext.Constants.restoreResolveConflictOverwrite());
		Utils.addToolTip(radioOverwrite, UIContext.Messages.restoreToFileSystemOverwriteTooltip(UIContext.productNameD2D));
		
		radioRename = new Radio();
		radioRename.ensureDebugId("39e44f8b-99c4-4d6d-a528-baf2663d5de4");
		radioRename.setBoxLabel(UIContext.Constants.restoreResolveConflictRename());
		Utils.addToolTip(radioRename, UIContext.Messages.restoreToFileSystemRenameTooltip(UIContext.productNameD2D));
		radioRename.setValue(true);
		
		RadioGroup radioGroup = new RadioGroup();   
		radioGroup.add(radioOverwrite);
		radioGroup.add(radioRename);
		
		containerFS = new LayoutContainer();
		TableLayout tableLayout = new TableLayout();
		tableLayout.setColumns(1);
		tableLayout.setCellPadding(2);
		tableLayout.setCellSpacing(2);
		this.setLayout(tableLayout);
		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == 1){
			productName = UIContext.productNamevSphere;
		}
		// description
		LabelField labelDesc = new LabelField(UIContext.Messages.restoreToFileSystemOverwriteDesc(productName));
		labelDesc.setStyleAttribute("padding-left", "18px");
		containerFS.add(labelDesc);
		
		LayoutContainer containerOptions = new LayoutContainer();
		
		TableLayout layoutOptions = new TableLayout();
		layoutOptions.setColumns(2);
		layoutOptions.setCellPadding(4);
		layoutOptions.setCellSpacing(0);
		containerOptions.setLayout(layoutOptions);
		containerOptions.setStyleAttribute("padding-left", "20px");
		containerOptions.setStyleAttribute("padding-top", "0px");

		TableData tableData = new TableData();
		containerOptions.add(radioRename, tableData);
		containerOptions.add(radioOverwrite, tableData);
		
		containerFS.add(containerOptions);
		layoutContainer.add(containerFS);		
	}
	
	private void renderToAlternateLocation(LayoutContainer layoutContainer) {
		radioToExchangeServer = new Radio();
		radioToExchangeServer.ensureDebugId("a2b7e201-2e08-4652-b4bb-9720afa25128");
		radioToExchangeServer.setBoxLabel(UIContext.Constants.restoreToExchangeServer());
		radioToExchangeServer.addStyleName("restoreWizardLeftSpacing");
		Utils.addToolTip(radioToExchangeServer, UIContext.Constants.restoreToAlternateTooltip());
		layoutContainer.add(radioToExchangeServer);
		
		containerAlter = new LayoutContainer();
		TableLayout layoutAlter = new TableLayout();
		layoutAlter.setColumns(1);
		layoutAlter.setCellPadding(4);
		layoutAlter.setCellSpacing(0);
		containerAlter.setLayout(layoutAlter);
		containerAlter.setStyleAttribute("padding-left", "20px");
		containerAlter.setStyleAttribute("padding-top", "0px");
		
		// description
		LabelField labelDesc = new LabelField(UIContext.Constants.restoreToAlternateExchangeDesc());
		containerAlter.add(labelDesc);

		LayoutContainer containerAccount = new LayoutContainer();

		TableLayout layoutExchange = new TableLayout();
		layoutExchange.setColumns(2);
		layoutExchange.setCellPadding(4);
		layoutExchange.setCellSpacing(0);
		containerAccount.setLayout(layoutExchange);
		containerAccount.setStyleAttribute("padding-top", "0px");

		// AD Server
		LabelField labelAlter = new LabelField(UIContext.Constants.restoreToAlternateExchange());
		labelAlter.setVisible(false);
		TableData tableDataExchange = new TableData();
		containerAccount.add(labelAlter, tableDataExchange);

		textFieldExchangeServer = new TextField<String>();
		textFieldExchangeServer.ensureDebugId("6ee7a774-aacf-4434-82af-d7a6589afbc2");
		textFieldExchangeServer.setWidth(MIN_FIELD_WIDTH);
		textFieldExchangeServer.setAllowBlank(false);
		textFieldExchangeServer.setVisible(false);
		tableDataExchange = new TableData();
		containerAccount.add(textFieldExchangeServer, tableDataExchange);

		// User
		labelAlter = new LabelField(UIContext.Constants.destinationSettingsUserName());
		tableDataExchange = new TableData();
		containerAccount.add(labelAlter, tableDataExchange);

		textFieldExchangeUser = new TextField<String>();
		textFieldExchangeUser.ensureDebugId("9565bac8-7de2-461b-a92b-e21c1300534f");
		textFieldExchangeUser.setWidth(MIN_FIELD_WIDTH);
		textFieldExchangeUser.setAllowBlank(false);
		textFieldExchangeUser.setValidator(validatorUserName);
		tableDataExchange = new TableData();
		containerAccount.add(textFieldExchangeUser, tableDataExchange);

		// password
		labelAlter = new LabelField(UIContext.Constants.destinationSettingsPassword());
		tableDataExchange = new TableData();
		containerAccount.add(labelAlter, tableDataExchange);

		textFieldExchangePassword = new PasswordTextField();
		textFieldExchangePassword.ensureDebugId("69654708-f56e-4ac7-af79-3dbcfd340767");
		textFieldExchangePassword.setPassword(true);
		textFieldExchangePassword.setWidth(MIN_FIELD_WIDTH);
		tableDataExchange = new TableData();
		containerAccount.add(textFieldExchangePassword, tableDataExchange);

		// destination
		LabelField labelDest = new LabelField(UIContext.Constants.restoreDestination());
		containerAccount.add(labelDest, tableDataExchange);

		// choose destination
		LayoutContainer containerDest = new LayoutContainer();

		TableLayout layoutDest = new TableLayout();
		layoutDest.setWidth("380px");
		layoutDest.setColumns(2);
		layoutDest.setCellPadding(0);
		layoutDest.setCellSpacing(0);
		layoutDest.setBorder(0);
		containerDest.setLayout(layoutDest);
		containerDest.setStyleAttribute("padding-top", "0px");
		containerDest.setStyleAttribute("padding-left", "0px");
				
		textFieldExchangeDestination = new TextField<String>();
		textFieldExchangeDestination.ensureDebugId("de376a30-85fa-4416-812f-c5f76d88ee8c");
		textFieldExchangeDestination.setWidth(MIN_FIELD_WIDTH);
		textFieldExchangeDestination.setReadOnly(true);
		textFieldExchangeDestination.setAllowBlank(false);
		textFieldExchangeDestination.getMessages().setBlankText(UIContext.Constants.restoreToAlternateError1());		
		textFieldExchangeDestination.setBorders(false);
		
		TableData tableDataDest1 = new TableData();
		containerDest.add(textFieldExchangeDestination, tableDataDest1);

		buttonBrowseExchOrg = new Button();
		buttonBrowseExchOrg.setText(UIContext.Constants.restoreBrowse() /*restoreToAlternateChooseDestination()*/);
		buttonBrowseExchOrg.setMinWidth(MIN_WIDTH);

		buttonBrowseExchOrg.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				// liuwe05 2011-01-10 fix Issue: 19964554    Title: GRT EXCH:BROWSE EXCH ORG
				// Even though we don't use the user & password to browse Exchange Organization
				// To avoid user confused, we will validate the user & password before launch the browse dialog				
				String domain = "";
				String user = textFieldExchangeUser.getValue();
				String password = textFieldExchangePassword.getValue();
				String productName = UIContext.productNameD2D;
				if(UIContext.uiType == 1){
					productName = UIContext.productNamevSphere;
				}
				final MessageBox validatingBox = MessageBox.wait(UIContext.Messages.messageBoxTitleInformation(productName), UIContext.Constants
						.validating(), "");
				Utils.setMessageBoxDebugId(validatingBox);
				// validate the account
				service.d2dExCheckUser(domain, user, password, new BaseAsyncCallback<Long>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						if (validatingBox != null) 
						{
							validatingBox.close();
						}
						
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(Long result)
					{
						if (validatingBox != null) 
						{
							validatingBox.close();
						}
						
						// show error message if validation failed
						GWT.log("d2dExCheckUser Successfully", null);
						if (result == 0)
						{
							dialogBrowseExchOrg = new BrowseExchOrgDialog(UIContext.Constants.restoreExchOrgDestinationTitle());
							dialogBrowseExchOrg.ensureDebugId("0f19b6d4-93f9-4c7b-b1b0-9bfc6ece1bb6");
							dialogBrowseExchOrg.setResizable(false);
							dialogBrowseExchOrg.setModal(true);
							
							dialogBrowseExchOrg.setADServerAccount(textFieldExchangeServer.getValue(), textFieldExchangeUser
									.getValue(), textFieldExchangePassword.getValue());
							dialogBrowseExchOrg.setDestination(strDestination);
							dialogBrowseExchOrg.setFolder(strFolder);	
							dialogBrowseExchOrg.setDestinationServerVersion(nDestinationServerVersion);
							boolean[] destionationSettings = getDestinationFolderSettings(); 
							dialogBrowseExchOrg.setDestinationFolderEnable(destionationSettings[0]);
							dialogBrowseExchOrg.setDestinationFolderNoteShowed(destionationSettings[1]);
							dialogBrowseExchOrg.show();

							dialogBrowseExchOrg.addWindowListener(new WindowListener() {
								public void windowHide(WindowEvent we) {
									if (dialogBrowseExchOrg.getDialogResult() == Dialog.OK) 
									{
										strDestination = dialogBrowseExchOrg.getDestination();
										strFolder = dialogBrowseExchOrg.getFolder();
										textFieldExchangeDestination.setValue(strDestination + "\\" + strFolder);
										nDestinationServerVersion = dialogBrowseExchOrg.getDestinationServerVersion();							
									}
								}
							});
						}
						else
						{
							thisPanel.showErrorMessage(result.intValue());
						}
					}
				});
			}

		});

		TableData tableDataDest2 = new TableData();
//		tableDataDest2.setWidth("100px");
		tableDataDest2.setHorizontalAlign(HorizontalAlignment.RIGHT);
		containerDest.add(buttonBrowseExchOrg, tableDataDest2);

		containerAccount.add(containerDest, tableDataExchange);
		
		// cas server
		labelCas_alternate = new LabelField(UIContext.Constants.casServer());
		containerAccount.add(labelCas_alternate, tableDataExchange);
		casSelection_alternate = new CASSelectionPanel(this, CASSelectionPanel.CAS_TYPE_ALTERNATIVE);
		containerAccount.add(casSelection_alternate, tableDataExchange);
		
		containerAlter.add(containerAccount);
		layoutContainer.add(containerAlter);
	}
	
	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_options()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreOptions());
		label.setStyleName("restoreWizardTitle");
		container.add(label);

		return container;
	}

	@Override
	public int processOptions() {
		RestoreJobModel model = RestoreContext.getRestoreModel();
		model.exchangeGRTOption = new ExchangeGRTOptionModel();
		model.setDestinationPath("");

		if (radioToOriginalLocation.getValue()) {
			model.setDestType(DestType.OrigLoc.getValue());
			model.exchangeGRTOption.setOption(RESTORE_TO_ORIGINAL_LOCATION);
			model.exchangeGRTOption.setUserName(textFieldOriginalUser.getValue());
			model.exchangeGRTOption.setPassword(textFieldOriginalPassword.getValue());
			if(isExch2013()){
				model.exchangeGRTOption.setDefaultE15CAS(casSelection_original.getClientAccessServer());
			}
		} else if (radioToFileSystem.getValue()) {
			
			model.setDestType(DestType.DumpFile.getValue());			
			//model.setDestinationPath(Utils.getNormalizedPath(textFieldDestination.getValue()));
			model.exchangeGRTOption.setFolder(Utils.getNormalizedPath(pathSelection.getDestination()));
			
			model.setDestinationPath(pathSelection.getDestination());
			model.setDestUser(pathSelection.getUsername());
			model.setDestPass(pathSelection.getPassword());
			
			long options = AFDDO_R_EXCH_RESTORE_TO_DISK;
			if (radioRename.getValue()) {
				options |= AFDDO_R_EXCH_RESOLVE_NAME_COLLISION;
			}
			model.exchangeGRTOption.setOption(options);

		} else if (radioToExchangeServer.getValue()) {
			model.setDestType(DestType.AlterLoc.getValue());
			model.exchangeGRTOption.setOption(AFDDO_R_EXCH_ALTERNATE_LOCATION);
			//model.exchangeGRTOption.setAlternateServer(textFieldExchangeServer.getValue());
			model.exchangeGRTOption.setAlternateServer(textFieldExchangeDestination.getValue());
			model.exchangeGRTOption.setUserName(textFieldExchangeUser.getValue());
			model.exchangeGRTOption.setPassword(textFieldExchangePassword.getValue());
			
			// set server version					
			model.exchangeGRTOption.setServerVersion((long)nDestinationServerVersion);
			if(isExch2013()){
				model.exchangeGRTOption.setDefaultE15CAS(casSelection_alternate.getClientAccessServer());
			}
		}
		
		
		if(pwdPane != null && pwdPane.isVisible()) 
		{
			String password = pwdPane.getPassword();
			model.setEncryptPassword(password);
		}
		
		return 0;
	}

	@Override
	public void repaint() {
		super.repaint();
		showNote();
		showCasSelection();
	}

	private void showNote() {
		//we don't support exchange 2003 officially, drop the information about exchange 2003
		if (radioToOriginalLocation.getValue()) {
			ExchVersion exchVersion = RestoreContext.getExchVersion();
			if (exchVersion != null) {
				switch (exchVersion) {
				case Exch2003: 
					this.noteLabel.setVisible(true);
					this.lableFieldNote2003.setVisible(true);
					this.lableFieldNote2007.setVisible(false);
					this.lableFieldNote2010.setVisible(false);
					this.lableFieldNote2013UserCaution.setVisible(false);
					break;
				case Exch2007: 
					this.noteLabel.setVisible(true);
					this.lableFieldNote2003.setVisible(false);
					this.lableFieldNote2007.setVisible(true);
					this.lableFieldNote2010.setVisible(false);
					this.lableFieldNote2013UserCaution.setVisible(false);
					break;
				case Exch2010: 
					this.noteLabel.setVisible(true);
					this.lableFieldNote2003.setVisible(false);
					this.lableFieldNote2007.setVisible(false);
					this.lableFieldNote2010.setVisible(true);
					this.lableFieldNote2013UserCaution.setVisible(false);
					break;
				case Exch2013:
					this.noteLabel.setVisible(true);
					this.lableFieldNote2003.setVisible(false);
					this.lableFieldNote2007.setVisible(false);
					this.lableFieldNote2010.setVisible(true);
					this.lableFieldNote2013UserCaution.setVisible(true);
					break;
				default:
					this.noteLabel.setVisible(true);
					this.lableFieldNote2003.setVisible(false);
					this.lableFieldNote2007.setVisible(true);
					this.lableFieldNote2010.setVisible(true);
					this.lableFieldNote2013UserCaution.setVisible(true);
				}
			}
			
			this.lableFieldNote64Bit.setVisible(true);
			this.lableFieldNoteSameDomain.setVisible(true);
		} else if (radioToFileSystem.getValue()) {
			this.noteLabel.setVisible(true);
			this.lableFieldNote2003.setVisible(false);
			this.lableFieldNote2007.setVisible(false);
			this.lableFieldNote2010.setVisible(false);
			this.lableFieldNote2013UserCaution.setVisible(false);
			this.lableFieldNote64Bit.setVisible(true);
			this.lableFieldNoteSameDomain.setVisible(false);
		} else if (radioToExchangeServer.getValue()) {
			this.noteLabel.setVisible(true);
			this.lableFieldNote2003.setVisible(false);
			this.lableFieldNote2007.setVisible(true);
			this.lableFieldNote2010.setVisible(true);
			this.lableFieldNote2013UserCaution.setVisible(true);
			this.lableFieldNote64Bit.setVisible(true);
			this.lableFieldNoteSameDomain.setVisible(false);
		}
	}
	
	private void showCasSelection(){
		ExchVersion exchVersion = RestoreContext.getExchVersion();
		if (exchVersion != null) {
			switch (exchVersion) {
			case Exch2013: 
				this.labelCas_original.setVisible(true);
				this.casSelection_original.setVisible(true);
				this.labelCas_alternate.setVisible(true);
				this.casSelection_alternate.setVisible(true);
				break;
			default:
				this.labelCas_original.setVisible(false);
				this.casSelection_original.setVisible(false);
				this.labelCas_alternate.setVisible(false);
				this.casSelection_alternate.setVisible(false);
				break;
			}
		}
	}
	
	private boolean isExch2013(){
		ExchVersion exchVersion = RestoreContext.getExchVersion();
		if(exchVersion != null&&exchVersion.getVersion()==ExchVersion.Exch2013.getVersion()){
			return true;
		}else{
			return false;
		}
	}

	private void updateControlStatus() {

		Radio rd = radioGroupTo.getValue();
		
		textFieldOriginalUser.clearInvalid();
		pathSelection.getDestinationTextField().clearInvalid();
		textFieldExchangeServer.clearInvalid();
		textFieldExchangeUser.clearInvalid();
		textFieldExchangeDestination.clearInvalid();

		if (rd == radioToOriginalLocation) {	
			containerOrg.setEnabled(true);
			
			containerFS.setEnabled(false);
			
			pathSelection.setEnabled(false);
			
			containerAlter.setEnabled(false);		

		} else if (rd == radioToFileSystem) {
			containerOrg.setEnabled(false);
			
			containerFS.setEnabled(true);
			
			pathSelection.setEnabled(true);
			
			containerAlter.setEnabled(false);	
		} else if (rd == radioToExchangeServer) {			
			containerOrg.setEnabled(false);
			
			containerFS.setEnabled(false);
			
			pathSelection.setEnabled(false);
			
			containerAlter.setEnabled(true);	
		}
	}

	protected void showErrorMessage(int errCode)
	{
		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == 1){
			productName = UIContext.productNamevSphere;
		}
		String error = "";
		boolean bFocusPassword = false;
		switch (errCode)
		{
		case 0xFFF00004:
			error = UIContext.Constants.restoreValidateUserError3();
			 bFocusPassword = false;
			break;			
		case 0xFFF00006:
			error = UIContext.Constants.restoreValidateUserError4();
			 bFocusPassword = false;
			break;
		case 0xFFF0000B:
			error = UIContext.Messages.restoreValidateUserError5(UIContext.productNameD2D);
			 bFocusPassword = false;
			break;
		case 0xFFF00001:
		case 0xFFF00002:
		case 0xFFF00003:
		default:
			error = UIContext.Constants.restoreValidateUserError1();
			bFocusPassword = true;
			break;
		}

		// error += " (EC=" + Integer.toHexString(result.intValue()) + ")";
		MessageBox msg = new MessageBox();
		msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
		msg.setMessage(error);
		msg.setIcon(MessageBox.ERROR);
		
		final boolean tempBFocusPassword = bFocusPassword;
		
		// focus the corresponding text field after message box closed
		msg.addCallback(new Listener<MessageBoxEvent>()
		{
			public void handleEvent(MessageBoxEvent be)
			{
				if (radioToOriginalLocation.getValue())
				{
					if (tempBFocusPassword)
					{
						textFieldOriginalPassword.clear();
						textFieldOriginalPassword.focus();
					}
					else
					{
						textFieldOriginalUser.selectAll();
						textFieldOriginalUser.focus();
					}
				}
				else if (radioToExchangeServer.getValue())
				{
					if (tempBFocusPassword)
					{
						textFieldExchangePassword.clear();
						textFieldExchangePassword.focus();
					}
					else
					{
						textFieldExchangeUser.selectAll();
						textFieldExchangeUser.focus();
					}
				}
			}
		});

		Utils.setMessageBoxDebugId(msg);
		msg.show();
	}

    @Override
	public boolean validate(AsyncCallback<Boolean> callback)
	{

		if (restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH)
		{ 
			boolean isPassed = this.recoveryPointPasswordPanel.validate();
			if (!isPassed)
			{
				String errorMsg = UIContext.Constants.restoreAllSessionPasswordRequired();
				showErrorMessage(callback, errorMsg);
				return false;
			}
			else
			{
				RestoreContext.setEncrypedRecoveryPoints(recoveryPointPasswordPanel.getModel());
			}
		}

		boolean isValid = true;

		if (radioToOriginalLocation.getValue())
		{
			// 2010-11-22 liuwe05 fix Issue: 19727369    Title: GRT EXCH:ARCH MBX RESTORE
			// Archived mailbox (and folder/mail in it) cannot be restored to original location
			List<GridTreeNode> restoreSources = RestoreContext.getRestoreRecvPointSources();
			for (int i=0; i<restoreSources.size(); i++)
			{
				GridTreeNode node = restoreSources.get(i);
				if (node != null && node.getReferNode() != null && node.getReferNode().size() > 1)
				{
					GridTreeNode mailboxNode = node.getReferNode().get(1);
					if (mailboxNode != null && mailboxNode.getGrtCatalogItemModel() != null && mailboxNode.getGrtCatalogItemModel().getFlag() != null)
					{
						long flag = mailboxNode.getGrtCatalogItemModel().getFlag().longValue();
											
						//  0:   It is normal mailbox
		                //  1:   It is a archive mailbox
						if (flag == 1)
						{
							String errorMsg = UIContext.Messages.restoreExchangeGRTArchiveMailboxToOriginalLocation(mailboxNode.getGrtCatalogItemModel().getObjName());
							showErrorMessage(callback, errorMsg);
							return false;
						}						
					}
				}
			}
			
			isValid = textFieldOriginalUser.validate();
			if(isExch2013()){
				isValid &= casSelection_original.validate();
			}
			
			if (!isValid)
			{
				callback.onSuccess(isValid);
			}
			else if (isValid)
			{
				String domain = "";
				String user = textFieldOriginalUser.getValue();
				String password = textFieldOriginalPassword.getValue();

				// validate the account
				callbackValidate = callback; // set the field for use
				service.d2dExCheckUser(domain, user, password, new BaseAsyncCallback<Long>()
				{

					@Override
					public void onFailure(Throwable caught)
					{
						callbackValidate.onFailure(caught);
					}

					@Override
					public void onSuccess(Long result)
					{
						// show error message if validation failed
						GWT.log("d2dExCheckUser Successfully", null);
						if (result == 0)
						{
							//callbackValidate.onSuccess(true);
							
							// continue to check the encryption password
							checkSessionPassword(callbackValidate);
						}
						else
						{
							thisPanel.showErrorMessage(result.intValue());							
							callbackValidate.onSuccess(false);
						}
					}
				});
			}
		}
		else if (radioToFileSystem.getValue())
		{
			String destination = thisPanel.pathSelection.getDestination();
			String userName = thisPanel.pathSelection.getUsername();
			String password = thisPanel.pathSelection.getPassword();
			String errorMsg = null;

			if (destination == null || destination.length() == 0)
			{
				errorMsg = UIContext.Constants.restoreEmailToDiskCannotBeBlank();
			}
			else if (destination.length() > 2 && destination.startsWith("\\\\"))
			{
				int index = destination.indexOf("\\", 2);
				if (index < 0 || index == 2 || index == destination.length() - 1)
				{
					errorMsg = UIContext.Constants.invalidRemotePath();
				}
			}

			if (errorMsg != null)
			{
				showErrorMessage(callback, errorMsg);
				return false;
			}
			else if ((userName == null || userName == "" || password == null || password == "")
					&& Utils.isValidRemotePath(pathSelection.getDestination()))
			{
				callbackValidate = callback; // set the field for use

				final UserPasswordWindow dlg = new UserPasswordWindow(pathSelection.getDestination(), "", "");
				dlg.setModal(true);

				dlg.addWindowListener(new WindowListener()
				{
					public void windowHide(WindowEvent we)
					{
						if (dlg.getCancelled() == false)
						{
							String username = dlg.getUsername();
							String password = dlg.getPassword();
							pathSelection.setUsername(username);
							pathSelection.setPassword(password);

							// continue to check the encryption password
							checkSessionPassword(callbackValidate);
						}
						else
						{
							callbackValidate.onSuccess(Boolean.FALSE);
						}
					}
				});
				dlg.show();
			}
			else
			{
				// continue to check the encryption password
				checkSessionPassword(callback);
			}
			
		}
		else if (radioToExchangeServer.getValue())
		{
			// isValid = textFieldExchangeServer.validate();
			isValid &= textFieldExchangeUser.validate();
			isValid &= textFieldExchangeDestination.validate();
			if(isExch2013()){
				isValid &= casSelection_alternate.validate();
			}

			if (!isValid)
			{
				callback.onSuccess(isValid);
			}
			else
			{
				String domain = "";
				String user = textFieldExchangeUser.getValue();
				String password = textFieldExchangePassword.getValue();

				// validate the account
				callbackValidate = callback; // set the field for use
				service.d2dExCheckUser(domain, user, password, new BaseAsyncCallback<Long>()
				{

					@Override
					public void onFailure(Throwable caught)
					{
						callbackValidate.onFailure(caught);
					}

					@Override
					public void onSuccess(Long result)
					{
						// show error message if validation failed
						GWT.log("d2dExCheckUser Successfully", null);
						if (result == 0)
						{
							//callbackValidate.onSuccess(true);
							
							// continue to check the encryption password
							checkSessionPassword(callbackValidate);
						}
						else
						{
							thisPanel.showErrorMessage(result.intValue());
							callbackValidate.onSuccess(false);
						}
					}
				});
			}
		}

		return isValid;
	}

    @Override
	protected void updateVisibleOfPwdPane()
	{
		if (restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH)
		{ 
			pwdPane.setVisible(false);

			Map<String, EncrypedRecoveryPoint> encrypedRecoveryPoint = RestoreUtil.filterEncryptedRecoveryPoint(
					RestoreContext.getRestoreSearchSources(), recoveryPointPasswordPanel.getModel());
			if (encrypedRecoveryPoint.isEmpty())
			{
				recoveryPointPasswordPanel.setVisible(false);
			}
			else
			{
				recoveryPointPasswordPanel.setVisible(true);
			}
			recoveryPointPasswordPanel.setModel(encrypedRecoveryPoint);
			
			return;
		}
		
		recoveryPointPasswordPanel.setVisible(false);
		super.updateVisibleOfPwdPane();		
		
		// hide / display the line
		if (htmlLine != null && ( pwdPane.isVisible() || recoveryPointPasswordPanel.isVisible() ))
		{
			htmlLine.setVisible(true);
		}	
		else
		{
			htmlLine.setVisible(false);
		}
	}
    
    public TextField<String> getTextFieldOriginalUser() {
		return textFieldOriginalUser;
	}
	
	public PasswordTextField getTextFieldOriginalPassword() {
		return textFieldOriginalPassword;
	}
	
	public TextField<String> getTextFieldExchangeUser() {
		return textFieldExchangeUser;
	}
	
	public PasswordTextField getTextFieldExchangePassword() {
		return textFieldExchangePassword;
	}
	
	private boolean[] getDestinationFolderSettings() {
		// Index 0 for whether sets destination folder enabled or disabled.
		// Index 1 for whether shows destination folder note or not.
		boolean[] results = new boolean[] { false, false }; 
		List<GridTreeNode> restoreSources = RestoreContext.getRestoreRecvPointSources();
		if (restoreSources.size() == 0) {
			return results;
		}
		for (int i = 0; i < restoreSources.size(); i++) {
			GridTreeNode node = restoreSources.get(i);
			if (null == node || null == node.getGrtCatalogItemModel()) {
				continue;
			}
			
			// Contains mail item
			if (!results[0] && !CatalogModelType.exchSubItemType_non_email_item.contains(node.getGrtCatalogItemModel().getObjType().intValue())) {
				results[0] = true; 
			}
			// Contains non-mail item
			if (!results[1] && CatalogModelType.exchSubItemType_non_email_item.contains(node.getGrtCatalogItemModel().getObjType().intValue())) {
				results[1] = true;
			} 
			
			if (results[0] && results[1]) {
				break;
			}
		}
		return results;
	}
}
