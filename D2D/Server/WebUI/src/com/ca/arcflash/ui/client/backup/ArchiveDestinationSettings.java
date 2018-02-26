package com.ca.arcflash.ui.client.backup;

import java.util.HashMap;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.GxtFactory;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UncPath;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.ArchiveSettingsModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.CloudVendorInfoModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.IntegerField;
import com.sencha.gxt.widget.core.client.form.StringComboBox;

public class ArchiveDestinationSettings {

	//final LoginServiceAsync service = GWT.create(LoginService.class);	
	
	ArchiveSettingsContent parentWindow = null;
	
	//Destination Settings
	private LayoutContainer lcArchiveDestinationContainer;
	Radio rbLocalDrive;
	LabelField lblLocalDrive;
	PathSelectionPanel browseLocalOrNetworkDestinationPanel;
	
	BaseSimpleComboBox<String> retentionUnitCombo;
	NumberField retentionValField;
	NumberField versionValField;
	static final int DAY_INDEX=0;
	static final int MONTH_INDEX=1;
	static final int YEAR_INDEX=2;
	
	public PathSelectionPanel getBrowseLocalOrNetworkDestinationPanel() {
		return browseLocalOrNetworkDestinationPanel;
	}

	String strDownloadLocation = "";
	
	  Radio rbCloud;
	  Button btConfigureCloud;
	  CloudConfigWindow windCloudConfig;
	 
	 private RadioGroup rgArchiveOptions;
	 
	//retention settings
	  LayoutContainer lcRetentionSettings = null;
	  BaseSimpleComboBox<String> cbRetentionYrs;
	  BaseSimpleComboBox<String> cbRetentionMons;
	  BaseSimpleComboBox<String> cbRetentionDays;
	//Archive global settings
//	  NumberField nfFileVersions;
	 
	//private ValueChangeHandler<Boolean> ArchiveDestinationSettingsChangeHandler = null;
	private Listener<BaseEvent> ArchiveDestinationSettingsChangeHandler = null;
	
	private Listener<BaseEvent> archiveSettingsListener;
	
	//advanced settings
	
	 SimpleComboBox<String> compressionOption;
	
	 CheckBox cbEnableEncryption;
	 LabelField lblEncryptionPassword;
	 PasswordTextField EncryptionPassword;
	
	LabelField lblReenterPassword;
	PasswordTextField ConfirmPassword;

	LabelField lblMatched = new LabelField();
	//Slider sArchivePerformanceSlider;// = new Slider();
	
	ToolTipConfig tipConfig = null;
	ToolTip tip = null;
	
	private Listener<BaseEvent> archiveAdvancedSettingsListener = null;
	
	private final static int MAX_LABEL_WIDTH = 150;
	private final static int MAX_FIELD_WIDTH = 250;

	private final boolean isForEdge;
	private Boolean isFileCopyToCloudEnabled;

	private RadioGroup retentionGrp;

	public Radio versionValRadio;

	public Radio retentionValRadio;

	private LabelField lblConfirmEncryptionPassword;
	
	public ArchiveDestinationSettings(ArchiveSettingsContent in_wind, boolean isForEdge)
	{
		parentWindow = in_wind;
		this.isForEdge = isForEdge;
		CustomizationModel customizedModel = UIContext.customizedModel;
		
		isFileCopyToCloudEnabled = customizedModel.get("FileCopyToCloud");
	}
	
	public LayoutContainer Render()
	{
		lcArchiveDestinationContainer = new LayoutContainer();
		TableLayout tlArchivePageLayout = new TableLayout();
		tlArchivePageLayout.setWidth("87%");
//		tlArchivePageLayout.setHeight("55%");
		tlArchivePageLayout.setCellSpacing(2);
		lcArchiveDestinationContainer.setLayout(tlArchivePageLayout);
		
		defineArchiveSettingsListener();
		
		defineArchiveDestinationSettings();
		
		defineArchiveAdvanacedSettingsListener();
		
		defineArchiveCompressionPolicy();
		
		defineArchiveEncryptionPolicy();	
		
		if(parentWindow.isArchiveTask())
			defineArchiveDestinationRetentionSettings();
		else
			defineFileCopyRetentionSettings();
		
		//Advanced settings
		
		
		return lcArchiveDestinationContainer;
	}
	
	public void setEditable(boolean editable) {
		if(isFileCopyToCloudEnabled)
		{	
			rbLocalDrive.setEnabled(editable);
		}	
		browseLocalOrNetworkDestinationPanel.setEnabled(editable);
		rbCloud.setEnabled(editable);
		btConfigureCloud.setEnabled(editable);

		compressionOption.setEnabled(editable);
		cbEnableEncryption.setEnabled(editable);
		EncryptionPassword.setEnabled(editable);
		ConfirmPassword.setEnabled(editable);

		if(parentWindow.isArchiveTask()){
			cbRetentionMons.setEnabled(editable);
			cbRetentionDays.setEnabled(editable);
			cbRetentionYrs.setEnabled(editable);
		}else{
			retentionValField.setEnabled(editable);
			retentionUnitCombo.setEnabled(editable);
			versionValField.setEnabled(editable);
		}
//		nfFileVersions.setEnabled(editable);
	}
	
	private void populateCloudConfigWindow()
	{		
		windCloudConfig = new CloudConfigWindow(CloudConfigWindow.ARCHIVE_MODE,isForEdge);
		windCloudConfig.addWindowListener(new WindowListener(){
		public void windowHide(WindowEvent we) {
			if(!windCloudConfig.getcancelled())
			{
					parentWindow.archiveConfigModel.setCloudConfigModel(windCloudConfig.getarchiveCloudConfigModel());
			}
				}
		});		
		windCloudConfig.RefreshData(parentWindow.archiveConfigModel != null ? parentWindow.archiveConfigModel.getCloudConfigModel():null);
		windCloudConfig.setModal(true);
		windCloudConfig.show();
	}
	
	private boolean defineArchiveSettingsListener()
	{
		archiveSettingsListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent ArchiveEvent) {
				if(ArchiveEvent.getSource() == btConfigureCloud)
				{
					if(windCloudConfig == null)
					{
						if(parentWindow.archiveConfigModel == null)
						{
							parentWindow.archiveConfigModel = new ArchiveSettingsModel();
						}
					    Broker.loginService.getCloudProviderInfo(new AsyncCallback<HashMap<String,CloudVendorInfoModel>>(){

							@Override
							public void onFailure(Throwable caught) {
								populateCloudConfigWindow();									
							}

							@Override
							public void onSuccess(HashMap<String,CloudVendorInfoModel> result) {	
								populateCloudConfigWindow();								
								if(result != null && result.size() > 0)
								{									
									windCloudConfig.loadCloudContainersWithVendorURL(result);		
								}
							
								
							}
							
						});

					}
					else
					{
						windCloudConfig.RefreshData(parentWindow.archiveConfigModel != null ? parentWindow.archiveConfigModel.getCloudConfigModel():null);
						windCloudConfig.setModal(true);
						windCloudConfig.show();
					}
				}
			}
		};
		
		ArchiveDestinationSettingsChangeHandler = new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent ScheduleChangeEvent) {
				if(ScheduleChangeEvent.getSource() == rbLocalDrive)
				{
					boolean bLocalDrive = rbLocalDrive.getValue();
					//browseLocalOrNetworkDestinationPanel.getDestinationTextField().setEnabled(bLocalDrive);
					//browseLocalOrNetworkDestinationPanel.getDestinationBrowseButton().setEnabled(bLocalDrive);
					browseLocalOrNetworkDestinationPanel.setEnabled(bLocalDrive);
					btConfigureCloud.setEnabled(!bLocalDrive);
					if(parentWindow.isArchiveTask())
						lcRetentionSettings.setEnabled(true);
//					nfFileVersions.setEnabled(true);
				}
				else if(ScheduleChangeEvent.getSource() == rbCloud)
				{
					btConfigureCloud.setEnabled(false);
					boolean bCloud = rbCloud.getValue();
					/*browseLocalOrNetworkDestinationPanel.getDestinationTextField().setEnabled(!bCloud);
					browseLocalOrNetworkDestinationPanel.getDestinationBrowseButton().setEnabled(!bCloud);*/
					browseLocalOrNetworkDestinationPanel.setEnabled(!bCloud);
					btConfigureCloud.setEnabled(bCloud);
					if(parentWindow.isArchiveTask())	
						lcRetentionSettings.setEnabled(true);
//					nfFileVersions.setEnabled(true);
				}
			}
		};
		return true;
	}
	
	private void defineArchiveDestinationSettings()
	{
		
		LayoutContainer destinationSettings = new LayoutContainer();
		TableLayout tlArchivePageLayout = new TableLayout();
		tlArchivePageLayout.setWidth("87%");
		tlArchivePageLayout.setHeight("55%");
		tlArchivePageLayout.setCellSpacing(2);
		destinationSettings.setLayout(tlArchivePageLayout);
		
		
		DisclosurePanel destSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.ArchiveDestination());
		
		/*Label lblArchiveDestination = new Label(UIContext.Constants.ArchiveDestination());
		lblArchiveDestination.setStyleName("restoreWizardSubItem");
		
		
		destinationSettings.add(lblArchiveDestination,tdArchiveDestination);*/
		
		TableData tdArchiveDestination = new TableData();
		tdArchiveDestination.setWidth("80%");
		tdArchiveDestination.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField lblArchiveDestinationSummary = new LabelField(UIContext.Constants.ArchiveDestinationDescription());
		destinationSettings.add(lblArchiveDestinationSummary,tdArchiveDestination);

		//rbLocalDrive = new RadioButton("Destination", UIContext.Constants.ArchiveToLocalDriveTitle());
		//rbLocalDrive.setStyleName("x-form-field");
		//rbLocalDrive.addValueChangeHandler(ArchiveDestinationSettingsChangeHandler);
		
		rgArchiveOptions = new RadioGroup();
		
		if(isFileCopyToCloudEnabled)
		{	
		rbLocalDrive = new Radio();
		rbLocalDrive.ensureDebugId("A9A9FAAB-651F-430f-B7D6-B7826C63BD69");
		rbLocalDrive.setBoxLabel(UIContext.Constants.ArchiveToLocalDriveTitle());
		rbLocalDrive.addStyleName("x-form-field");	
		rbLocalDrive.setValue(true);
		rbLocalDrive.addListener(Events.Change, ArchiveDestinationSettingsChangeHandler);			
		rgArchiveOptions.add(rbLocalDrive);
		destinationSettings.add(rbLocalDrive,tdArchiveDestination);
		}
		else
		{
			lblLocalDrive = new LabelField();
			lblLocalDrive.setValue(UIContext.Constants.ArchiveToLocalDriveTitle());
			lblLocalDrive.addStyleName("x-form-field");
			destinationSettings.add(lblLocalDrive,tdArchiveDestination);
		}
		
		InitDestinationSelectionPanel(); 
			
		tdArchiveDestination = new TableData();
		/*tdArchiveDestination.setWidth("80%");
		tdArchiveDestination.setHorizontalAlign(HorizontalAlignment.LEFT);*/
		destinationSettings.add(browseLocalOrNetworkDestinationPanel,tdArchiveDestination);		
		
		//rbCloud = new RadioButton("Destination", UIContext.Constants.ArchiveToCloud());
		//rbCloud.setStyleName("x-form-field");
		//rbCloud.addValueChangeHandler(ArchiveDestinationSettingsChangeHandler);
		
		
		
		if(isFileCopyToCloudEnabled)
		{
		rbCloud = new Radio();
		rbCloud.setBoxLabel(UIContext.Constants.ArchiveToCloud());
		rbCloud.ensureDebugId("9E5AE752-C40D-4c3a-9410-53226DE7B791");
		rbCloud.addStyleName("x-form-field");
		rbCloud.addListener(Events.Change, ArchiveDestinationSettingsChangeHandler);
		rgArchiveOptions.add(rbCloud);
		tdArchiveDestination = new TableData();
		tdArchiveDestination.setWidth("40%");
		tdArchiveDestination.setHorizontalAlign(HorizontalAlignment.LEFT);
		destinationSettings.add(rbCloud,tdArchiveDestination);
		
		btConfigureCloud = new Button(UIContext.Constants.ArchiveToCloudConfigureTitle())
		{
			@Override
			protected void onDisable() {
				addStyleName("item-disabled");
				super.onDisable();		   
			}

			@Override
			protected void onEnable() {
				removeStyleName("item-disabled");
				super.onEnable();
			}
		};
		btConfigureCloud.ensureDebugId("C5F112E2-A615-4963-91B5-B5D1169C9428");
		Utils.addToolTip(btConfigureCloud, UIContext.Constants.ArchiveToCloudConfigureTooltip());
		btConfigureCloud.addListener(Events.Select, archiveSettingsListener);
		
		LayoutContainer lcConfigureCloudContainer = new LayoutContainer();
		lcConfigureCloudContainer.add(btConfigureCloud);
		btConfigureCloud.setEnabled(false);
		rbCloud.setValue(false);
		lcConfigureCloudContainer.setStyleName("archiveRetentionLayout");
		destinationSettings.add(lcConfigureCloudContainer);
		}
		destinationSettings.add(new Html("<HR>"));
		destSettingsPanel.add(destinationSettings);
		
		lcArchiveDestinationContainer.add(destSettingsPanel);
	}	
	
	private void InitDestinationSelectionPanel()
	{
		browseLocalOrNetworkDestinationPanel = new PathSelectionPanel(new Listener<FieldEvent>() {
			
			@Override
			public void handleEvent(FieldEvent be) {
				strDownloadLocation = browseLocalOrNetworkDestinationPanel.getDestination();
			}
		});
		browseLocalOrNetworkDestinationPanel.addDebugId(
				"39075E71-45EE-4c3f-9D02-4DC57FA7196B",
				"739AD42F-D050-41aa-A985-4F5952142A5A",
				"7FF7837D-8C07-4127-A87F-53AFB8D76977");
		browseLocalOrNetworkDestinationPanel.setStyleAttribute("padding-left", "15px");
		browseLocalOrNetworkDestinationPanel.getDestinationTextField().setWidth(300);
		//browseLocalOrNetworkDestinationPanel.getDestinationTextField().setAllowBlank(false);
		browseLocalOrNetworkDestinationPanel.setChangeListener(new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				strDownloadLocation =  browseLocalOrNetworkDestinationPanel.getDestination();
				if(!isEmpty(strDownloadLocation)) {
					
					BackupSettingsModel model = new BackupSettingsModel();
					String[] conn = Utils.getConnectionInfo(strDownloadLocation);
					if(conn != null) {
						model.setDestPassword(conn[2]);
						model.setDestUserName(conn[1]);
					}else {
						model.setDestPassword(browseLocalOrNetworkDestinationPanel.getPassword());
						model.setDestUserName(browseLocalOrNetworkDestinationPanel.getUsername());
					}
					model.setDestination(strDownloadLocation);
					Broker.homeService.getDestSizeInformation(model, new BaseAsyncCallback<DestinationCapacityModel>(){
						@Override
						public void onSuccess(DestinationCapacityModel result) {
							parentWindow.destModel = result;
						}
					});
				}
				/*else
				{
					msgError.setMessage(UIContext.Messages.EnterValidArchiveDestination());
					msgError.show();
					return;
				}*/
			}
		});
		
		browseLocalOrNetworkDestinationPanel.setMode(PathSelectionPanel.ARCHIVE_DEST_MODE);
		browseLocalOrNetworkDestinationPanel.setTooltipMode(PathSelectionPanel.TOOLTIP_ARCHIVE_MODE);
		browseLocalOrNetworkDestinationPanel.setPathFieldLength(380);
		browseLocalOrNetworkDestinationPanel.setPathSelectionArchiveMode(true);
		return;
	}
	
	private boolean isEmpty(final String remoteDest) {
		return remoteDest == null || remoteDest.length() == 0;
	}
	
	private void defineFileCopyRetentionSettings(){
		
		retentionGrp = new RadioGroup();
		
		versionValRadio = new Radio();
		versionValRadio.ensureDebugId("6dbfd2c5-c615-4898-a8e7-6e713007620d");
		versionValRadio.setBoxLabel(UIContext.Constants.fileVersionLabel1());
		versionValRadio.setValue(true);
		versionValRadio.setStyleAttribute("padding-top", "5px");
		retentionGrp.add(versionValRadio);
		
		versionValField = new NumberField();
		versionValField.ensureDebugId("83f22b48-f27d-4713-8fef-460fa16663ae");
		versionValField.setAllowNegative(false);
		versionValField.setAllowDecimals(false);
		versionValField.setValue(15);
		versionValField.setWidth(40);
		
		LabelField versionValLabel = new LabelField();
		versionValLabel.setValue(UIContext.Constants.fileVersionLabel2());
		versionValLabel.setStyleAttribute("padding-top", "5px");
		versionValLabel.setStyleAttribute("padding-left", "5px");
		
		HBoxLayoutContainer versionContainer = new HBoxLayoutContainer();
		versionContainer.setPadding(new Padding(0,0,5,0));
		//versionContainer.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		versionContainer.add(versionValRadio);
		versionContainer.add(versionValField);
		versionContainer.add(versionValLabel);
		
		
		
		retentionValRadio = new Radio();
		retentionValRadio.ensureDebugId("6dbfd2c5-c615-4898-a8e7-6e713007620d");
		retentionValRadio.setBoxLabel(UIContext.Constants.fileCreatedWithinLabel());
		retentionValRadio.setValue(false);
		retentionValRadio.setStyleAttribute("padding-top", "5px");
		retentionGrp.add(retentionValRadio);
		retentionGrp.setValue(versionValRadio);
		
		HBoxLayoutContainer retentionFieldContainer = new HBoxLayoutContainer();
		retentionFieldContainer.ensureDebugId("9f20b00d-b44b-4505-9ad1-ba6b2a990a5e");
		
		retentionValField = new NumberField();
		retentionValField.ensureDebugId("327a2f47-c719-41cb-9934-5f6f4ba7f303");
		retentionValField.setAllowNegative(false);
		retentionValField.setAllowDecimals(false);
		retentionValField.setWidth(40);
		retentionFieldContainer.add(retentionValField);
		
		retentionUnitCombo = new BaseSimpleComboBox<String>();
		retentionUnitCombo.ensureDebugId("34df380c-1530-40e3-97f1-5d3f24bd405d");
		retentionUnitCombo.setWidth(80);
		retentionUnitCombo.add(UIContext.Constants.days());
		retentionUnitCombo.add(UIContext.Constants.months());
		retentionUnitCombo.add(UIContext.Constants.years());
		retentionUnitCombo.setStyleAttribute("padding-left", "10px");
		retentionFieldContainer.add(retentionUnitCombo);
		
		HBoxLayoutContainer retentionContainer = new HBoxLayoutContainer();
		retentionContainer.add(retentionValRadio,  new BoxLayoutData(new Margins(2, 0, 0, 0)));
		retentionContainer.add(retentionFieldContainer);

		VerticalLayoutContainer rententionSettingContainer = new VerticalLayoutContainer();
		rententionSettingContainer.add(versionContainer);
		rententionSettingContainer.add(retentionContainer);

//		this.add(PlanGuiUtil.createFormLayout3(uiConstants.fileCopyRetentionHeader(), rententionSettingContainer), PlanGuiUtil.createLineLayoutData3());
		/*VerticalLayoutContainer mainContainer = new VerticalLayoutContainer();
		mainContainer.ensureDebugId("59fa008e-6404-41bb-ad12-90024f2d5e48");
		
		VerticalLayoutContainer childContainer = new VerticalLayoutContainer();
		childContainer.ensureDebugId("83a35101-f9cb-40e7-a6ee-119df6855691");
		
		HBoxLayoutContainer rententionSettingContainer = new HBoxLayoutContainer();
		rententionSettingContainer.ensureDebugId("552a89e7-9aed-466d-bde0-2db7179cfbf9");
		rententionSettingContainer.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);


		LayoutContainer retentionFieldContainer = new LayoutContainer();
		retentionFieldContainer.ensureDebugId("9f20b00d-b44b-4505-9ad1-ba6b2a990a5e");
		
		TableLayout tl = new TableLayout(3);
		retentionFieldContainer.setLayout(tl);
		
		Label lblRetentionDescription = new Label(UIContext.Constants.fileRetentionLabel());
		lblRetentionDescription.ensureDebugId("33a53ead-61d7-4d0c-9c8b-3fdef788a61b");
		lblRetentionDescription.setStyleName("restoreWizardSubItemDescription");
		
		
		retentionValField = new NumberField();
		retentionValField.ensureDebugId("327a2f47-c719-41cb-9934-5f6f4ba7f303");
		retentionValField.setAllowNegative(false);
		retentionValField.setAllowDecimals(false);
		retentionValField.setWidth(40);
		retentionFieldContainer.add(retentionValField);
		
		retentionUnitCombo = new BaseSimpleComboBox<String>();
		retentionUnitCombo.ensureDebugId("34df380c-1530-40e3-97f1-5d3f24bd405d");
		retentionUnitCombo.setWidth(80);
		retentionUnitCombo.add(UIContext.Constants.days());
		retentionUnitCombo.add(UIContext.Constants.months());
		retentionUnitCombo.add(UIContext.Constants.years());
		retentionUnitCombo.setStyleAttribute("padding-left", "10px");
		retentionFieldContainer.add(retentionUnitCombo);
		
		FieldLabel rententionFieldLabel = new FieldLabel(retentionFieldContainer,UIContext.Constants.fileCreatedWithinLabel());
		rententionFieldLabel.ensureDebugId("535c320b-751a-4f43-98d5-29fc710989e0");
		rententionFieldLabel.setLabelSeparator("");
		rententionFieldLabel.setLabelWidth(150);
		
		childContainer.add(rententionFieldLabel);
		
		versionValField = new NumberField();
		versionValField.ensureDebugId("83f22b48-f27d-4713-8fef-460fa16663ae");
		versionValField.setAllowNegative(false);
		versionValField.setAllowDecimals(false);
		versionValField.setWidth(40);

		FieldLabel versionFieldLabel = new FieldLabel(versionValField,UIContext.Constants.fileVersionLessThanLabel());
		versionFieldLabel.ensureDebugId("5832495c-a479-4165-b518-698435ddfdc2");
		versionFieldLabel.setLabelSeparator("");
		versionFieldLabel.setLabelWidth(150);
		childContainer.add(versionFieldLabel);

		rententionSettingContainer.add(childContainer,new BoxLayoutData(new Margins(5)));
		rententionSettingContainer.forceLayout();
		rententionSettingContainer.setBorders(true);
		

		mainContainer.add(lblRetentionDescription);
		mainContainer.add(rententionSettingContainer);
		mainContainer.setWidth(400);*/
		
		DisclosurePanel fileVersionSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.filesRetentionHeader());
		fileVersionSettingsPanel.add(rententionSettingContainer);
		lcArchiveDestinationContainer.add(fileVersionSettingsPanel);
	}
	private void defineArchiveDestinationRetentionSettings()
	{
		
		LayoutContainer retentionSettings = new LayoutContainer();
		TableLayout tlArchivePageLayout = new TableLayout();
		tlArchivePageLayout.setWidth("87%");
		tlArchivePageLayout.setHeight("55%");
		tlArchivePageLayout.setCellSpacing(2);
		retentionSettings.setLayout(tlArchivePageLayout);
		
		
		DisclosurePanel retSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.RetentionTimeLabel());
		
		lcRetentionSettings =  new LayoutContainer();
		TableLayout tlRetentionLayout = new TableLayout(1);
		tlRetentionLayout.setWidth("80%");
		tlRetentionLayout.setCellSpacing(2);
		tlRetentionLayout.setColumns(6);
		lcRetentionSettings.setLayout(tlRetentionLayout);
		
		
		TableData tdRetention = new TableData();
		tdRetention.setWidth("100%");
		tdRetention.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		/*LabelField lblRetentionLabel = new LabelField(UIContext.Constants.RetentionTimeLabel());
		lblRetentionLabel.setStyleName("restoreWizardSubItem");
		
		retentionSettings.add(lblRetentionLabel,tdRetention);*/
		
		LabelField lblRetentionDescription = new LabelField(UIContext.Constants.RetentionTimeDescription());
		retentionSettings.add(lblRetentionDescription,tdRetention);
		
		TableData tdRetentionContainer = new TableData();
		tdRetentionContainer.setHorizontalAlign(HorizontalAlignment.RIGHT);
		TableData tdRetentionLabel= new TableData();
		tdRetentionContainer.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		cbRetentionMons = new BaseSimpleComboBox<String>();
		cbRetentionMons.ensureDebugId("9504DDF8-66FD-4afe-955A-D7C137C339F9");
		cbRetentionMons.setWidth(60);
		cbRetentionMons.setEditable(false);
		cbRetentionMons.add("0");
		cbRetentionMons.setSimpleValue("0");
		for(int iMonth = 1;iMonth <=12;iMonth++)
		{
			cbRetentionMons.add(Integer.toString(iMonth));
		}
		
		/*lcRetentionSettings.add(cbRetentionMons,tdRetentionContainer);
		LabelField lblMons = new LabelField(UIContext.Constants.months());
		lblMons.setWidth(60);
		lcRetentionSettings.add(lblMons,tdRetentionLabel);*/
		
		cbRetentionDays = new BaseSimpleComboBox<String>();
		cbRetentionDays.ensureDebugId("7B643E05-6DA6-4094-84F6-846CD459AA13");
		cbRetentionDays.setWidth(60);
		cbRetentionDays.setEditable(false);
		cbRetentionDays.add("0");
		cbRetentionDays.setSimpleValue("0");
		for(int iday = 1;iday <= 31;iday++)
		{
			cbRetentionDays.add(Integer.toString(iday));
		}
		/*lcRetentionSettings.add(cbRetentionDays,tdRetentionContainer);
		LabelField lblDays = new LabelField(UIContext.Constants.days());
		lblDays.setWidth(60);
		lcRetentionSettings.add(lblDays,tdRetentionLabel);*/

		cbRetentionYrs = new BaseSimpleComboBox<String>();
		cbRetentionYrs.ensureDebugId("EAE977CF-2F48-410d-BCF3-3683FA6BB1A7");
		cbRetentionYrs.setWidth(60);
		cbRetentionYrs.setEditable(false);
		cbRetentionYrs.add("0");
		for(int iYear = 1;iYear <= 100;iYear++)
		{
			cbRetentionYrs.add(Integer.toString(iYear));
		}
		cbRetentionYrs.setSimpleValue("4");
		lcRetentionSettings.add(cbRetentionYrs,tdRetentionContainer);		
		LabelField lblYears = new LabelField(UIContext.Constants.years());
		lblYears.setWidth(60);
		lcRetentionSettings.add(lblYears,tdRetentionLabel);		
		
		lcRetentionSettings.add(cbRetentionMons,tdRetentionContainer);
		LabelField lblMons = new LabelField(UIContext.Constants.months());
		lblMons.setWidth(60);
		lcRetentionSettings.add(lblMons,tdRetentionLabel);		
		
		lcRetentionSettings.add(cbRetentionDays,tdRetentionContainer);
		LabelField lblDays = new LabelField(UIContext.Constants.days());
		lblDays.setWidth(60);
		lcRetentionSettings.add(lblDays,tdRetentionLabel);	
		
		//lcRetentionSettings.setStyleName("archiveRetentionLayout");
		retentionSettings.add(lcRetentionSettings);
		
		retentionSettings.add(new Html("<HR>"));
		
		
		LayoutContainer fileVersionSettings = new LayoutContainer();
		tlArchivePageLayout = new TableLayout();
		tlArchivePageLayout.setWidth("87%");
		tlArchivePageLayout.setHeight("55%");
		tlArchivePageLayout.setCellSpacing(2);
		fileVersionSettings.setLayout(tlArchivePageLayout);
		
		
		/*DisclosurePanel fileVersionSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.ArchiveFileVersionsLabel());
		
		LabelField lblFileVersionsLabel = new LabelField(UIContext.Constants.ArchiveFileVersionsLabel());
		lblFileVersionsLabel.setStyleName("restoreWizardSubItem");
		fileVersionSettings.add(lblFileVersionsLabel);
		
		LabelField lblFileVersionsDescription = new LabelField(UIContext.Constants.MaxFileVersionsDesription());
		fileVersionSettings.add(lblFileVersionsDescription);
		
		nfFileVersions = new NumberField();
		nfFileVersions.ensureDebugId("FA8EBD79-49E8-47df-9A9F-35EB0CAFA993");
		nfFileVersions.setValue(15);
		nfFileVersions.setMaxValue(UIContext.maxFileVersions);
		nfFileVersions.setEnabled(true);
		nfFileVersions.setWidth(80);
		nfFileVersions.setAllowBlank(false);
		nfFileVersions.setAllowNegative(false);
		nfFileVersions.setAllowDecimals(false);
		//nfFileVersions.setStyleName("archiveRetentionLayout");
		//nfFileVersions.setStyleAttribute("padding-left", "15px");
		nfFileVersions.setValidator(new Validator(){
			@Override
			public String validate(Field<?> field, String value) {
				Number iFileVersionsRetentionCount = nfFileVersions.getValue();
				
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
				msgError.setModal(true);
				msgError.setMinWidth(350);
				
				if(iFileVersionsRetentionCount == null || iFileVersionsRetentionCount.intValue() == 0)
				{
					msgError.setMessage(UIContext.Messages.MinimumNumberFileVersionsMessage(UIContext.minFileVersions));
					Utils.setMessageBoxDebugId(msgError);
					nfFileVersions.setValue(UIContext.minFileVersions);
					msgError.show();
				}
				
				if(iFileVersionsRetentionCount.intValue() > UIContext.maxFileVersions)
				{
					msgError.setMessage(UIContext.Messages.MaximumNumberFileVersionsMessage(UIContext.maxFileVersions));
					nfFileVersions.setValue(UIContext.maxFileVersions);
					msgError.show();
				}

				return null;
			}
			
		});
		
		nfFileVersions.setValidateOnBlur(true);
		fileVersionSettings.add(nfFileVersions);
		fileVersionSettings.add(new Html("<HR>"));
		
		fileVersionSettingsPanel.add(fileVersionSettings);*/
		
		
		retSettingsPanel.add(retentionSettings);
		lcArchiveDestinationContainer.add(retSettingsPanel);
//		lcArchiveDestinationContainer.add(fileVersionSettingsPanel);
		return;
	}
	
	public PathSelectionPanel getPathSelectionPanel() {
		return browseLocalOrNetworkDestinationPanel;
	}
	
	public boolean checkShareFolder()
	{
		try
		{
			PathSelectionPanel pathSelectionPanel = browseLocalOrNetworkDestinationPanel;
			String destPath = pathSelectionPanel.getDestination();
			if(!PathSelectionPanel.isLocalPathValid(destPath)){
				MessageBox messageBox = new MessageBox();
				messageBox.setTitleHtml( UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D) );
				messageBox.setMessage( UIContext.Constants.archiveDest_Error_InvalidUncPath() );
				messageBox.setIcon( MessageBox.ERROR );
				messageBox.setButtons( MessageBox.OK );
				messageBox.getDialog().getButtonById(Dialog.OK).ensureDebugId("BB94064E-8453-4175-AA78-C64D2DA7ADDD");
				messageBox.show();
				return false;
			}	
			
			if (pathSelectionPanel.isLocalPath())
				return true;
			
			String title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);

			UncPath uncPath = new UncPath();
			uncPath.setUncPath( destPath );
			if (uncPath.getShareFolder().length() == 0)
			{
				MessageBox messageBox = new MessageBox();
				messageBox.setTitleHtml( title );
				messageBox.setMessage( UIContext.Constants.archiveDest_Error_NoShareFolder() );
				messageBox.setIcon( MessageBox.ERROR );
				messageBox.setButtons( MessageBox.OK );
				messageBox.getDialog().getButtonById(Dialog.OK).ensureDebugId("B91739F5-5162-4007-B8E7-DF0A9F746100");
				messageBox.show();
				return false;
			}

			if (uncPath.getComputerName().equalsIgnoreCase( "localhost" ) ||
				uncPath.getComputerName().equalsIgnoreCase( "127.0.0.1" ))
			{
				MessageBox messageBox = new MessageBox();
				messageBox.setTitleHtml( title );
				messageBox.setMessage( UIContext.Constants.archiveDest_Error_NotAllowLocalHost() );
				messageBox.setIcon( MessageBox.ERROR );
				messageBox.setButtons( MessageBox.OK );
				messageBox.getDialog().getButtonById(Dialog.OK).ensureDebugId("9080FDEF-F51A-437b-9C56-047D4DF861B3");
				messageBox.show();
				return false;
			}

			return true;
		}
		catch (UncPath.InvalidUncPathException e)
		{
			MessageBox messageBox = new MessageBox();
			messageBox.setTitleHtml( UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D) );
			messageBox.setMessage( UIContext.Constants.archiveDest_Error_InvalidUncPath() );
			messageBox.setIcon( MessageBox.ERROR );
			messageBox.setButtons( MessageBox.OK );
			messageBox.getDialog().getButtonById(Dialog.OK).ensureDebugId("AAE74C9C-6810-4d67-83F0-DD7438ECD887");
			messageBox.show();
			return false;
		}
	}
	
	public void validateRemotePath( final AsyncCallback<Boolean> callback )
	{
		final PathSelectionPanel pathSelectionPanel = browseLocalOrNetworkDestinationPanel;
		
		if (pathSelectionPanel.isLocalPath()) {
			callback.onSuccess(true);
			return;
		}
		
		if (!pathSelectionPanel.needValidate()) {
			callback.onSuccess(true);
			return;
		}

		final UserPasswordWindow dlg =
			new UserPasswordWindow( pathSelectionPanel.getDestination(), "", "" );
		dlg.setModal(true);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we)
			{
				if (dlg.getCancelled() == false)
				{
					String username = dlg.getUsername();
					String password = dlg.getPassword();
					pathSelectionPanel.setUsername(username);
					pathSelectionPanel.setPassword(password);
					pathSelectionPanel.setValidated();
					
					callback.onSuccess(true);
				}
				else // canceled
				{
					callback.onSuccess(false);
				}
				
			}
		});
		
		dlg.show();

	}
	
	public void setDestinationValidated()
	{
		this.browseLocalOrNetworkDestinationPanel.setValidated();
	}
	
	private void defineArchiveAdvanacedSettingsListener() {
		archiveAdvancedSettingsListener  = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent archiveEvent) {
			  		
				if(archiveEvent.getSource() == cbEnableEncryption)
				{
					Boolean enable = cbEnableEncryption.getValue();
					lblEncryptionPassword.setEnabled(enable);
					EncryptionPassword.setEnabled(enable);
					EncryptionPassword.clear();
					EncryptionPassword.clearInvalid();
					lblConfirmEncryptionPassword.setEnabled(enable);
					ConfirmPassword.setEnabled(enable);
					ConfirmPassword.clear();
					ConfirmPassword.clearInvalid();
					lblMatched.clear();
					if(enable)
					{
						lblMatched.setEnabled(true);
					}
					else 
					{
						lblMatched.setEnabled(false);
						EncryptionPassword.disable();		
					    ConfirmPassword.disable();
						
					}
				}
				/*else if(archiveEvent.getSource() == sArchivePerformanceSlider)
				{}*/
				else if(archiveEvent.getSource() == EncryptionPassword)
				{
					if(EncryptionPassword.getValue() != null)
					{
						if(EncryptionPassword.getValue().length() == 0)
						{
							ConfirmPassword.setEnabled(false);
						}
						else
						{
							ConfirmPassword.setEnabled(true);
						}
					}
				}
				else if(archiveEvent.getSource() == ConfirmPassword)
				{
					if(ConfirmPassword.getValue() == null)
					{
						lblMatched.setValue(UIContext.Constants.notMatched());
						return;
					}
						
					if(EncryptionPassword.getValue().compareTo(ConfirmPassword.getValue()) == 0)
					{
						lblMatched.setStyleAttribute("color", "black");
						lblMatched.setValue(UIContext.Constants.matched());
					}
					else
					{
						
						lblMatched.setStyleAttribute("color", "red");
						lblMatched.setValue(UIContext.Constants.notMatched());
					}
				}
			}
		};
	}
	private void defineArchiveCompressionPolicy() {
		
		
		LayoutContainer compressionSettings = new LayoutContainer();
		TableLayout tlArchivePageLayout = new TableLayout();
		tlArchivePageLayout.setWidth("87%");
		tlArchivePageLayout.setHeight("55%");
		tlArchivePageLayout.setCellSpacing(2);
		compressionSettings.setLayout(tlArchivePageLayout);
		
		
		DisclosurePanel compSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.settingsLabelCompression());
		
		
		LabelField label = new LabelField();
		/*label.setText(UIContext.Constants.settingsLabelCompression());
		label.addStyleName("restoreWizardSubItem");
		compressionSettings.add(label);*/
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelCompressionDescription());
		compressionSettings.add(label);
		
		LayoutContainer lcCompressionContainer = new LayoutContainer();
		TableLayout tableCompressionLayout = new TableLayout();
		tableCompressionLayout.setWidth("100%");
		tableCompressionLayout.setHeight("10%");
		tableCompressionLayout.setCellSpacing(4);
		tableCompressionLayout.setColumns(1);
		lcCompressionContainer.setLayout(tableCompressionLayout);
		//lcCompressionContainer.setStyleAttribute("padding-bottom", "15px");
		
		compressionOption = new BaseSimpleComboBox<String>();
		compressionOption.ensureDebugId("D7706C78-3545-4559-A992-DDC80D4B9420");
		compressionOption.setEditable(false);
		compressionOption.add(UIContext.Constants.settingsCompressionNone());
		compressionOption.add(UIContext.Constants.settingsCompreesionStandard());
		compressionOption.add(UIContext.Constants.settingsCompressionMax());
		compressionOption.setSimpleValue(UIContext.Constants.settingsCompreesionStandard());
		Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
		//compressionOption.setMaxHeight(2);
		//compressionOption.setStyleAttribute("padding-left", "28px");
		//compressionOption.setEnabled(false);
		lcCompressionContainer.add(compressionOption);
		compressionOption.setWidth(200);
		lcCompressionContainer.setWidth(250);
		compressionSettings.add(lcCompressionContainer);
		
		compressionOption.addListener(Events.Select, new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				String selString = compressionOption.getSimpleValue();
				if (selString.compareTo(UIContext.Constants.settingsCompressionNone()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionNoneTooltip());
				}
				else if (selString.compareTo(UIContext.Constants.settingsCompreesionStandard()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
				}
				else if (selString.compareTo(UIContext.Constants.settingsCompressionMax()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionMaxTooltip());
				}
				
			}
		});
		
		compressionSettings.add(new Html("<HR>"));
		compSettingsPanel.add(compressionSettings);
		
		lcArchiveDestinationContainer.add(compSettingsPanel);
	}
	private void defineArchiveEncryptionPolicy() {
		
		LayoutContainer encryptionSettings = new LayoutContainer();
		TableLayout tlArchivePageLayout = new TableLayout();
		tlArchivePageLayout.setWidth("87%");
		tlArchivePageLayout.setHeight("55%");
		tlArchivePageLayout.setCellSpacing(2);
		encryptionSettings.setLayout(tlArchivePageLayout);
		
		
		DisclosurePanel encSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.settingsLabelEncryption());
				
		
		LabelField label = new LabelField();
		/*label.setText(UIContext.Constants.settingsLabelEncryption());
		label.addStyleName("restoreWizardSubItem");
		encryptionSettings.add(label);*/
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelEncryptionDescription());
		encryptionSettings.add(label);
		
		LayoutContainer lcEncryptionContainer = new LayoutContainer();
		TableLayout tableEncryptionLayout = new TableLayout();
		tableEncryptionLayout.setWidth("100%");
		tableEncryptionLayout.setHeight("10%");
		tableEncryptionLayout.setCellSpacing(4);
		tableEncryptionLayout.setColumns(1);
		//lcEncryptionContainer.setStyleAttribute("padding-bottom", "15px");
		lcEncryptionContainer.setLayout(tableEncryptionLayout);
		
		cbEnableEncryption = new CheckBox();
		cbEnableEncryption.ensureDebugId("BAC38E17-0DC3-48b1-A13B-CD589635BFB7");
		cbEnableEncryption.setStyleName("x-form-field");
		cbEnableEncryption.setBoxLabel(UIContext.Constants.ArchiveEnableEncryptionDescription());
		cbEnableEncryption.addListener(Events.OnClick,archiveAdvancedSettingsListener);
		TableData tdEncryption = new TableData();
		tdEncryption.setWidth("100%");
		tdEncryption.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		lcEncryptionContainer.add(cbEnableEncryption,tdEncryption);
		
		LayoutContainer lcPasswordContainer = new LayoutContainer();
		TableLayout tlEncryptionPassword = new TableLayout();
		tlEncryptionPassword.setWidth("100%");
		tlEncryptionPassword.setHeight("10%");
		tlEncryptionPassword.setCellSpacing(5);
		tlEncryptionPassword.setColumns(3);
		lcPasswordContainer.setStyleAttribute("padding-left", "28px");
		lcPasswordContainer.setLayout(tlEncryptionPassword);
		
		lblEncryptionPassword = new LabelField(UIContext.Constants.encryptionPassword());
		lblEncryptionPassword.setWidth(MAX_LABEL_WIDTH);
		lblEncryptionPassword.setEnabled(false);
		TableData tdEncryptionPassword = new TableData();
		//tdCloudConfigLabel.setWidth("30%");
		tdEncryptionPassword.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcPasswordContainer.add(lblEncryptionPassword,tdEncryptionPassword);
		EncryptionPassword = new PasswordTextField();
		EncryptionPassword.ensureDebugId("479BC4FA-6DCA-4c93-89FB-1C14F39FE3A0");
		EncryptionPassword.setWidth(MAX_FIELD_WIDTH);
		EncryptionPassword.setAllowBlank(false);
		EncryptionPassword.setPassword(true);
		EncryptionPassword.setEnabled(false);
		EncryptionPassword.setMaxLength(Utils.EncryptionPwdLen);
		Utils.addToolTip(EncryptionPassword, UIContext.Constants.ProvidePasswordforEncryption());
		EncryptionPassword.addListener(Events.OnBlur, archiveAdvancedSettingsListener);
		
		lcPasswordContainer.add(EncryptionPassword,tdEncryptionPassword);
		LabelField lblPass = new LabelField();
		lcPasswordContainer.add(lblPass);
		
		lblConfirmEncryptionPassword = new LabelField(UIContext.Constants.ConfirmPassword());
		lblConfirmEncryptionPassword.setWidth(MAX_LABEL_WIDTH);
		lblConfirmEncryptionPassword.setEnabled(false);
		lcPasswordContainer.add(lblConfirmEncryptionPassword,tdEncryptionPassword);
		ConfirmPassword = new PasswordTextField();
		ConfirmPassword.ensureDebugId("A32CEA7F-FB39-469c-B422-42DD2C2919CD");
		ConfirmPassword.setWidth(MAX_FIELD_WIDTH);
		ConfirmPassword.setAllowBlank(false);
		ConfirmPassword.setValidateOnBlur(true);
		ConfirmPassword.setEnabled(false);
		ConfirmPassword.setValidator(new Validator(){
			@Override
			public String validate(Field<?> field, String value) {
				if(value == null || !value.equals(EncryptionPassword.getValue())) {	
					lblMatched.setVisible(false);
					return UIContext.Constants.notMatched();
				}else {
					lblMatched.setVisible(true);
					lblMatched.setStyleAttribute("color", "black");
					lblMatched.setValue(UIContext.Constants.matched());
					return null;
				}
			}
		});
		ConfirmPassword.setPassword(true);
		ConfirmPassword.setMaxLength(Utils.EncryptionPwdLen);
		Utils.addToolTip(ConfirmPassword, UIContext.Constants.ReenterPassowrdforValidate());
//		ConfirmPassword.addListener(Events.OnKeyUp, archiveAdvancedSettingsListener);
		
		lcPasswordContainer.add(ConfirmPassword,tdEncryptionPassword);
		
		
		lblMatched.setWidth(120);
		lblMatched.show();
		lcPasswordContainer.add(lblMatched,tdEncryptionPassword);
		/*LabelField label = new LabelField();
		label.setText(UIContext.Constants.settingsLabelEncryptionDescription());
		label.setStyleAttribute("padding-left", "28px");
		lcEnvryptionContainer.add(label);*/
		lcEncryptionContainer.add(lcPasswordContainer);
		encryptionSettings.add(lcEncryptionContainer);
		encryptionSettings.add(new Html("<HR>"));
		
		encSettingsPanel.add(encryptionSettings);	
		
		lcArchiveDestinationContainer.add(encSettingsPanel);
		
	}
	
	
	public boolean isMaxCompression(ArchiveSettingsModel model) {
		if(model == null || model.getCompressionLevel() == null)
			return false;
		
		return model.getCompressionLevel() == 9;
	}

	public boolean isStandardCompression(ArchiveSettingsModel model) {
		if(model == null || model.getCompressionLevel() == null)
			return false;
		
		return model.getCompressionLevel() == 1;
	}
	
	public int getCompressionLevel(){
		int compressionLevel = -1;
		if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompressionNone())
			compressionLevel = 0;
		else if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompreesionStandard())
			compressionLevel = 1;
		else if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompressionMax())
			compressionLevel = 9;
		return compressionLevel;
	}
}
