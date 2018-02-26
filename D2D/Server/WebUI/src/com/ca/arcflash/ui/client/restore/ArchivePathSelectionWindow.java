package com.ca.arcflash.ui.client.restore;

import java.util.HashMap;

import com.ca.arcflash.ui.client.FlashUIMessages;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.CloudConfigWindow;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveCloudDestInfoModel;
import com.ca.arcflash.ui.client.model.ArchiveDestinationDetailsModel;
import com.ca.arcflash.ui.client.model.ArchiveDestinationModel;
import com.ca.arcflash.ui.client.model.ArchiveDiskDestInfoModel;
import com.ca.arcflash.ui.client.model.CloudVendorInfoModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ArchivePathSelectionWindow extends Window {

	final LoginServiceAsync service = GWT.create(LoginService.class);
	
	private ArchivePathSelectionWindow thisWindow;
	//for browse
	private LayoutContainer lcArchiveDestinations;
	//Destination Settings
	private RadioGroup rgDestinationOptions;
	private Radio rbLocalDrive;
	private Radio rbCloud;
	private LayoutContainer lcArchiveBrowseDestinationsPanel;
	//private RadioButton rbCloud;
	private TextField<String> txtCloudPathForBrowse;
	private Button btConfigureCloudForBrowse;
	private CloudConfigWindow windCloudConfig;
	private Listener<BaseEvent> restoreSettingsListener = null;
	
	private PathSelectionPanel ArchivePathSelection;
	private LayoutContainer lcCloudDestinationPanel;
	private String strArchiveDestination = "";
	
    static String archiveDestination;
	public int restoreType;
	public final int MAX_WIDTH = 550;
	public final int MAX_HEIGHT = 150;
	
	public final int MIN_WIDTH = 90;
	
	private Button btOK;
	private Button btCancel;
	
	public final static int ARCHIVE_SYNC_CATALOG = 100;
	public final static int ARCHIVE_READ_EXISTING_CATALOG = 101;
	
	public int NextAction = 0;
	
	private ArchiveDestinationModel destInfoConfig = null;
	
	private Boolean isFileCopyToCloudEnabled;
	private LabelField lblLocalDrive;
	
	
	public ArchivePathSelectionWindow()
	{
		this.setSize(MAX_WIDTH, MAX_HEIGHT);
		thisWindow = this;
		
		CustomizationModel customizedModel = UIContext.customizedModel;
		
		isFileCopyToCloudEnabled = customizedModel.get("FileCopyToCloud");
		
		LayoutContainer lcPathSelectionWind = getArchiveDestinationsPanel();
		
		defineArchiveSettingsButtons();
		this.add(lcPathSelectionWind);
	}
	
	private LayoutContainer getArchiveDestinationsPanel()
	{
		this.setHeadingHtml(UIContext.Constants.ArchiveDestination());
		lcArchiveBrowseDestinationsPanel = new LayoutContainer();
		TableLayout tlArchiveBrowse = new TableLayout();
		tlArchiveBrowse.setColumns(1);
		lcArchiveBrowseDestinationsPanel.setLayout(tlArchiveBrowse);
		lcArchiveBrowseDestinationsPanel.setStyleName("RestorePathLayoutStyle");
		//
		
		lcArchiveDestinations = new LayoutContainer();	
		TableLayout tlArchiveDestinationLayout = new TableLayout();
		tlArchiveDestinationLayout.setColumns(2);
		lcArchiveDestinations.setLayout(tlArchiveDestinationLayout);
		
		defineRestoreIntroductionPanelListeners();
		
		TableData tdArchiveDestination = new TableData();
		tdArchiveDestination.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		if(isFileCopyToCloudEnabled)
		{
			rgDestinationOptions = new RadioGroup();
			rbLocalDrive = new Radio(){

				@Override
	            protected void onClick(ComponentEvent be) {
					super.onClick(be);		            
	            }
		    };
			rbLocalDrive.ensureDebugId("0D2E59BC-9137-4999-90FF-BB8D5351265D");
			//rbLocalDrive.setStyleName("ArchivePolicyOptionStyle");
			rbLocalDrive.setBoxLabel(UIContext.Constants.LocalornetworkdriveLabel());
			rbLocalDrive.setValue(true);
			//rbLocalDrive.setWidth(150); // Fix (issue: 148079)
			rgDestinationOptions.add(rbLocalDrive);

			rbCloud = new Radio(){

				@Override
	            protected void onClick(ComponentEvent be) {
					super.onClick(be);		            
	            }
		    };
			rbCloud.ensureDebugId("90E0552F-2A69-4eff-B2EC-FFD13A657698");
			//rbCloud.setStyleName("ArchivePolicyOptionStyle");
			rbCloud.setBoxLabel(UIContext.Constants.cloudLabel());
			rbCloud.setValue(false);
			//rbCloud.setWidth(150); // Fix (issue: 148079)
			rgDestinationOptions.add(rbCloud);
			
			rgDestinationOptions.addListener(Events.Change,restoreSettingsListener);
			
			lcArchiveDestinations.add(rbLocalDrive,tdArchiveDestination);
			lcArchiveDestinations.add(rbCloud,tdArchiveDestination);
		}
		else
		{
			lblLocalDrive = new LabelField(UIContext.Constants.LocalornetworkdriveLabel());
			lblLocalDrive.setStyleName("x-form-field");
			lcArchiveDestinations.add(lblLocalDrive,tdArchiveDestination);
		}
		
		
		lcArchiveBrowseDestinationsPanel.add(lcArchiveDestinations);
		
		ArchivePathSelection = InitDestinationSelectionPanel(); 
		
		tdArchiveDestination = new TableData();
		tdArchiveDestination.setHorizontalAlign(HorizontalAlignment.LEFT);
		ArchivePathSelection.hide();
		ArchivePathSelection.setChangeListener(new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				
				if(ArchivePathSelection.getDestination() != null && ArchivePathSelection.getDestination().length() != 0)
				{
					strArchiveDestination = ArchivePathSelection.getDestination();
				}
				else
				{
					strArchiveDestination = "";
				}
			}
		});
		lcArchiveBrowseDestinationsPanel.add(ArchivePathSelection,tdArchiveDestination);		

		lcCloudDestinationPanel = new LayoutContainer();
		TableLayout tlCloudDestinationPanel = new TableLayout();
		tlCloudDestinationPanel.setColumns(2);
		//tlCloudDestinationPanel.setWidth("100%");
		tlCloudDestinationPanel.setCellSpacing(5);
		lcCloudDestinationPanel.setLayout(tlCloudDestinationPanel);
		
		txtCloudPathForBrowse = new TextField<String>();
		txtCloudPathForBrowse.ensureDebugId("7B0B99C1-0475-4ba1-9BA6-135F27E8FCE8");
		//txtCloudPathForBrowse.setStyleName("WidgetPaddingLeft"); // Fix (issue: 148079)
		txtCloudPathForBrowse.setWidth(350);
		txtCloudPathForBrowse.setReadOnly(true);
	//	txtCloudPathForBrowse.setValue("s3.amazonaws.com");

		tdArchiveDestination = new TableData();
		tdArchiveDestination.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcCloudDestinationPanel.add(txtCloudPathForBrowse,tdArchiveDestination);
		btConfigureCloudForBrowse = new Button(UIContext.Constants.ArchiveToCloudConfigureTitle());
		this.btConfigureCloudForBrowse.ensureDebugId("171668A3-0931-48ef-BF75-06F27FD1E16D");
		TableData tdConfigureCloud = new TableData();
		tdConfigureCloud.setStyleName("ConfigureCloudButton");
		tdConfigureCloud.setHorizontalAlign(HorizontalAlignment.RIGHT);
		
		btConfigureCloudForBrowse.setEnabled(false);
		Utils.addToolTip(btConfigureCloudForBrowse, UIContext.Constants.ArchiveToCloudConfigureTooltip());
		btConfigureCloudForBrowse.addListener(Events.Select, restoreSettingsListener);
		//btConfigureCloudForBrowse.setWidth(80);
		btConfigureCloudForBrowse.setMinWidth(80);
		btConfigureCloudForBrowse.setAutoWidth(true);
		lcCloudDestinationPanel.add(btConfigureCloudForBrowse,tdConfigureCloud);
		
		lcCloudDestinationPanel.hide();
		lcArchiveBrowseDestinationsPanel.add(lcCloudDestinationPanel);
		
		
		
		if((isFileCopyToCloudEnabled && rbLocalDrive.getValue())||!isFileCopyToCloudEnabled)
		{
			if(isFileCopyToCloudEnabled)
			{
				lcCloudDestinationPanel.hide();
			}
			
			ArchivePathSelection.show();
			boolean bLocalDrive;
			if(isFileCopyToCloudEnabled)
			{	
			bLocalDrive = rbLocalDrive.getValue();
			}
			else
			{
				bLocalDrive = true;
			}
			ArchivePathSelection.getDestinationTextField().setEnabled(bLocalDrive);
			ArchivePathSelection.getDestinationBrowseButton().setEnabled(bLocalDrive);
			btConfigureCloudForBrowse.setEnabled(!bLocalDrive);
		}
		else if(isFileCopyToCloudEnabled && rbCloud.getValue())
		{
			ArchivePathSelection.hide();
			lcCloudDestinationPanel.show();
			btConfigureCloudForBrowse.setEnabled(false);
			boolean bCloud = rbCloud.getValue();
			ArchivePathSelection.getDestinationTextField().setEnabled(!bCloud);
			ArchivePathSelection.getDestinationBrowseButton().setEnabled(!bCloud);
			btConfigureCloudForBrowse.setEnabled(bCloud);
		}
		
		
		return lcArchiveBrowseDestinationsPanel;
	}
	
	private PathSelectionPanel InitDestinationSelectionPanel()
	{
		PathSelectionPanel archivePathSelectionPanel = new PathSelectionPanel(null);
		archivePathSelectionPanel.getDestinationTextField().setEnabled(false);
		//archivePathSelectionPanel.getDestinationTextField().setAllowBlank(false);

		archivePathSelectionPanel.addListener(PathSelectionPanel.onDisconnectionEvent,
				new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						//receoveryPointPanel.clearRecoveryPoints();
					}

				});
		archivePathSelectionPanel.setChangeListener(new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				strArchiveDestination =  ArchivePathSelection.getDestination();
			}
		});
		
		archivePathSelectionPanel.setMode(PathSelectionPanel.ARCHIVE_RESTORE_MODE);
		archivePathSelectionPanel.setTooltipMode(PathSelectionPanel.TOOLTIP_ARCHIVE_DESTINATION_MODE);
		archivePathSelectionPanel.setPathFieldLength(MAX_WIDTH-200);
		archivePathSelectionPanel.addDebugId("060914A8-E38F-4b9d-A982-49510E95E91F", 
				"99C19429-BC2C-48c4-BE39-77B8F1F158C2", 
				"2AFFC2DD-5C18-48f3-9F97-53F452003ADD");
		return archivePathSelectionPanel;
	}
	
	private boolean isClickOK = false;
	public boolean getCancelled() {
		return !isClickOK;
	}
	
	
	private void populateCloudConfigWindow()
	{
		windCloudConfig = new CloudConfigWindow(CloudConfigWindow.RESTORE_MODE,false);
		windCloudConfig.addWindowListener(new WindowListener(){
			public void windowHide(WindowEvent we) {
				if(!windCloudConfig.getcancelled())
				{
					if(destInfoConfig == null)
						destInfoConfig = new ArchiveDestinationModel();
					destInfoConfig.setCloudConfigModel(windCloudConfig.getarchiveCloudConfigModel());
					txtCloudPathForBrowse.setValue(destInfoConfig.getCloudConfigModel().getcloudVendorURL());
					archiveDestination = destInfoConfig.getCloudConfigModel().getcloudVendorURL()+destInfoConfig.getCloudConfigModel().getcloudBucketName();
				
					
				}
			}
		});
		windCloudConfig.RefreshData(destInfoConfig != null ? destInfoConfig.getCloudConfigModel() : null);
		windCloudConfig.setModal(true);
		windCloudConfig.show();
		
	}
	
	private void defineRestoreIntroductionPanelListeners() {
		
		restoreSettingsListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent RestoreEvent) {
				if(RestoreEvent.getSource() == btConfigureCloudForBrowse)
				{
					if(windCloudConfig == null)
					{
						 service.getCloudProviderInfo(new AsyncCallback<HashMap<String,CloudVendorInfoModel>>(){

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
						windCloudConfig.RefreshData(destInfoConfig != null ? destInfoConfig.getCloudConfigModel() : null);
						windCloudConfig.setModal(true);
						windCloudConfig.show();
					}
				}
				else if(RestoreEvent.getSource() == rgDestinationOptions)
				{
					if((isFileCopyToCloudEnabled && rbLocalDrive.getValue())||!isFileCopyToCloudEnabled)
					{
						lcCloudDestinationPanel.hide();
						ArchivePathSelection.show();
						boolean bLocalDrive = rbLocalDrive.getValue();
						ArchivePathSelection.getDestinationTextField().setEnabled(bLocalDrive);
						ArchivePathSelection.getDestinationBrowseButton().setEnabled(bLocalDrive);
						btConfigureCloudForBrowse.setEnabled(!bLocalDrive);
					}
					else if(isFileCopyToCloudEnabled && rbCloud.getValue())
					{
						ArchivePathSelection.hide();
						lcCloudDestinationPanel.show();
						btConfigureCloudForBrowse.setEnabled(false);
						boolean bCloud = rbCloud.getValue();
						ArchivePathSelection.getDestinationTextField().setEnabled(!bCloud);
						ArchivePathSelection.getDestinationBrowseButton().setEnabled(!bCloud);
						btConfigureCloudForBrowse.setEnabled(bCloud);
					}
				}
			}
		};
		
	}

	private void defineArchiveSettingsButtons()
	{
		btOK = new Button()
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
		btOK.ensureDebugId("D2CE3366-8BAD-44a5-9AF0-05BAB0073895");
		btOK.setText(UIContext.Constants.ok());
		btOK.setMinWidth(MIN_WIDTH);		
		btOK.addListener(Events.Select,new SelectionListener<ButtonEvent>(){
	
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(Validate() == true)
				{
					Save();
					
					thisWindow.mask(UIContext.Constants.ArchiveLastSyncDate());
					//Get the status of the changed destination 
					service.getArchiveChangedDestinationDetails(destInfoConfig, new BaseAsyncCallback<ArchiveDestinationDetailsModel>() {
						
						@Override
						public void onSuccess(ArchiveDestinationDetailsModel result) {
							thisWindow.unmask();
							
							if(result != null){
								destInfoConfig.setHostName(result.gethostName());
								/*if(result.getLastSyncDate().toString().indexOf("1601")>-1){
									MessageBox messageBox = new MessageBox();
									messageBox.setTitleHtml( UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D) );
									messageBox.setMessage( UIContext.Constants.notValidFCDest() );
									messageBox.setIcon( MessageBox.ERROR );
									messageBox.setButtons( MessageBox.OK );
									messageBox.getDialog().getButtonById(Dialog.OK).ensureDebugId("BB94064E-8453-4175-AA78-C64D2DA7ADDD");
									messageBox.show();
									return;
								}*/
							}
							
							final CatalogSync syncBox = new CatalogSync(result);
							syncBox.setModal(true);
							syncBox.show();
							syncBox.addWindowListener(new WindowListener(){
								public void windowHide(WindowEvent we) {
									if(we.getButtonClicked() == syncBox.btCatalogSyncCancel)
									{
										return;
									}else
									{
										isClickOK = true;
										thisWindow.hide(btOK);
										return;
									}
										
								}
							});
							/*MessageBox msgBox = new MessageBox();
							msgBox.setButtons(MessageBox.YESNO);
							msgBox.addCallback(new Listener<MessageBoxEvent>() {

								@Override
								public void handleEvent(MessageBoxEvent be) {
									if (be.getButtonClicked().getItemId().equals(com.extjs.gxt.ui.client.widget.Dialog.YES)) {
										
									}
									else{
										//do not change the
									}
									
									
									isClickOK = true;
									thisWindow.hide(btOK);
								}
							});
							msgBox.setTitle(UIContext.Constants.messageBoxTitleInformation());
							msgBox.setMessage(UIContext.Messages.NoPoliciesSelectedWarning());
							msgBox.setIcon(MessageBox.INFO);
							msgBox.setModal(true);
							msgBox.setMinWidth(430);
							msgBox.show();*/
						}
						
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							thisWindow.unmask();
						}
					});
				}
				return;
			}

			});		
		this.addButton(btOK);
		
		
		btCancel = new Button()
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
		btCancel.ensureDebugId("1ED93B9D-AE8D-4502-9510-B55D41D6CB34");
		btCancel.setText(UIContext.Constants.cancel());
		btCancel.setMinWidth(MIN_WIDTH);
		btCancel.addListener(Events.Select,new SelectionListener<ButtonEvent>(){
	
			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
				
			}});		
		this.addButton(btCancel);	
	}
	
	private boolean Validate() {
		boolean bValidated = true;
		MessageBox errMessage = new MessageBox();
		 errMessage.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
		errMessage.setModal(true);
		errMessage.setIcon(MessageBox.ERROR);
		if((isFileCopyToCloudEnabled && rbLocalDrive.getValue())||!isFileCopyToCloudEnabled)
		{
			if((ArchivePathSelection.getDestinationTextField() == null) || (ArchivePathSelection.getDestinationTextField().getValue() == null))
			{
				errMessage.setMessage(UIContext.Messages.EnterValidArchiveDestinationMessage());
				Utils.setMessageBoxDebugId(errMessage);
				errMessage.show();
				return false;
			}
			
			if(ArchivePathSelection.getDestinationTextField().getValue().startsWith("\\\\"))
			{
				if(isEmpty(ArchivePathSelection.getUsername())|| isEmpty( ArchivePathSelection.getPassword()))
				{
					errMessage.setMessage(UIContext.Messages.EnterValidCredentialsMessage());
					Utils.setMessageBoxDebugId(errMessage);
					errMessage.show();
					return false;
				}
			}
			archiveDestination = ArchivePathSelection.getDestinationTextField().getValue();
			
			
			
			
		}
		else if(isFileCopyToCloudEnabled && rbCloud.getValue())
		{
			if((txtCloudPathForBrowse == null) || (txtCloudPathForBrowse.getValue() == null) || ((txtCloudPathForBrowse.getValue().length() == 0)))
			{
				errMessage.setMessage(UIContext.Messages.EnterValidCloudConfigMessage());
				Utils.setMessageBoxDebugId(errMessage);
				errMessage.show();
				return false;
			}
		}
		return bValidated;
	}
	
	private void Save() {
		if(destInfoConfig == null)
			destInfoConfig = new ArchiveDestinationModel();
		if((isFileCopyToCloudEnabled && rbLocalDrive.getValue())||!isFileCopyToCloudEnabled)
		{
			destInfoConfig.setArchiveToDrive(true);
			destInfoConfig.setArchiveToCloud(false);
			
			ArchiveDiskDestInfoModel model = new ArchiveDiskDestInfoModel();
			model.setArchiveDiskDestPath(ArchivePathSelection.getDestinationTextField().getValue());
			model.setArchiveDiskUserName(ArchivePathSelection.getUsername());
			model.setArchiveDiskPassword(ArchivePathSelection.getPassword());
			
			destInfoConfig.setArchiveDiskDestInfoModel(model);
		}
		else if(isFileCopyToCloudEnabled && rbCloud.getValue())
		{  		  
			if(windCloudConfig.getarchiveCloudConfigModel() != null)
			destInfoConfig.setCloudConfigModel(windCloudConfig.getarchiveCloudConfigModel());
		    else			
			destInfoConfig.setCloudConfigModel(getArchiveDestinationModel().getCloudConfigModel());		
			destInfoConfig.setArchiveToDrive(false);
			destInfoConfig.setArchiveToCloud(true);
		}
			
	}
	
	private void populateRestoreCloudConfigWindow(ArchiveCloudDestInfoModel cloudConfig)
	{		
		windCloudConfig = new CloudConfigWindow(CloudConfigWindow.RESTORE_MODE,false);
		windCloudConfig.addWindowListener(new WindowListener(){
			public void windowHide(WindowEvent we) {
				if(!windCloudConfig.getcancelled())
				{
					if(destInfoConfig == null)
						destInfoConfig = new ArchiveDestinationModel();
					destInfoConfig.setCloudConfigModel(windCloudConfig.getarchiveCloudConfigModel());
					txtCloudPathForBrowse.setValue(destInfoConfig.getCloudConfigModel().getcloudVendorURL());
					archiveDestination = destInfoConfig.getCloudConfigModel().getcloudVendorURL()+destInfoConfig.getCloudConfigModel().getcloudBucketName();				
					
				}
				
			}
		});		
		windCloudConfig.RefreshData(cloudConfig);					
		txtCloudPathForBrowse.setValue(cloudConfig.getcloudVendorURL());
	
	}

	ArchiveCloudDestInfoModel restoreCloudConfigModel;
	
	public void refresh(ArchiveDestinationModel archiveDestinationInfo) {
		if(archiveDestinationInfo != null)
		{
			if(archiveDestinationInfo.getArchiveToDrive())
			{
				ArchiveDiskDestInfoModel model = archiveDestinationInfo.getArchiveDiskDestInfoModel();
				if(isFileCopyToCloudEnabled)
				{	
					rgDestinationOptions.setValue(rbLocalDrive);
				}	
				ArchivePathSelection.show();
				lcCloudDestinationPanel.hide();
				ArchivePathSelection.setDestination(model.getArchiveDiskDestPath());
				ArchivePathSelection.setUsername(model.getArchiveDiskUserName());
				ArchivePathSelection.setPassword(model.getArchiveDiskPassword());
			}
			else if(isFileCopyToCloudEnabled && archiveDestinationInfo.getArchiveToCloud())
			{
				rgDestinationOptions.setValue(rbCloud);
				lcCloudDestinationPanel.show();
				ArchivePathSelection.hide();
				ArchiveCloudDestInfoModel cloudConfig = archiveDestinationInfo.getCloudConfigModel();
				restoreCloudConfigModel = cloudConfig;
				if(cloudConfig != null)
				{
					if(windCloudConfig == null)
					{
						 service.getCloudProviderInfo(new AsyncCallback<HashMap<String,CloudVendorInfoModel>>(){

								@Override
								public void onFailure(Throwable caught) {
									populateRestoreCloudConfigWindow(restoreCloudConfigModel);									
								}

								@Override
								public void onSuccess(HashMap<String,CloudVendorInfoModel> result) {	
									populateRestoreCloudConfigWindow(restoreCloudConfigModel);									
									if(result != null && result.size() > 0)
									{									
										windCloudConfig.loadCloudContainersWithVendorURL(result);		
									}							
									
								}
								
							});
					}
					else
					{
						windCloudConfig.RefreshData(cloudConfig);					
						txtCloudPathForBrowse.setValue(cloudConfig.getcloudVendorURL());
					}
				}
			}
		}		
		destInfoConfig = archiveDestinationInfo;
	}
	
	public ArchiveDestinationModel getArchiveDestinationModel()
	{
		return destInfoConfig;
	}
	
	
	public class CatalogSync extends Window
	{
		private Button btSubmitSyncJob;
		private Button btReadExistingCatalog;
		private Button btCatalogSyncCancel;
		private ArchiveDestinationDetailsModel destChangeModel;
		
		private CatalogSync catalogSyncWindow;
		public CatalogSync(ArchiveDestinationDetailsModel in_destChangeInfo)
		{
			catalogSyncWindow = this;
			this.setWidth(450);
			this.setHeight(150);
			this.setHeadingHtml(UIContext.Constants.ArchiveDestination());
			
			destChangeModel = in_destChangeInfo;
			LayoutContainer lcCatalogSync = new LayoutContainer();
			TableLayout tlCatalogSync = new TableLayout(1);
			lcCatalogSync.setLayout(tlCatalogSync);
			lcCatalogSync.setStyleAttribute("margin", "5px,5px,5px,5px");
			
			LabelField lblMessage = new LabelField();
			
			if(destChangeModel!= null && destChangeModel.getCatalogAvailable())
			{
				StringBuffer strMessage = new StringBuffer();
				strMessage.append(UIContext.Messages.CatalogSyncJob(Utils.formatDateToServerTime(destChangeModel.getLastSyncDate())));
				lblMessage.setValue(strMessage.toString());
			}
			else
			{
				lblMessage.setValue(UIContext.Messages.catalogDataForSelectedDestination());
			}
			
			lcCatalogSync.add(lblMessage);
			
			this.add(lcCatalogSync);
			defineArchiveSettingsButtons();
		}
		
		private void defineArchiveSettingsButtons()
		{
			
			SelectionListener<ButtonEvent> selectionList = new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(ButtonEvent ce) {
					if(ce.getSource() == btSubmitSyncJob)
					{
						NextAction = ARCHIVE_SYNC_CATALOG;
						catalogSyncWindow.hide(btSubmitSyncJob);
					}
					else if(ce.getSource() == btReadExistingCatalog)
					{
						NextAction = ARCHIVE_READ_EXISTING_CATALOG;
						catalogSyncWindow.hide(btReadExistingCatalog);
					}
					else if(ce.getSource() == btCatalogSyncCancel)
					{
						catalogSyncWindow.hide();
					}
				}
			};
			
			btSubmitSyncJob = new Button()
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
			btSubmitSyncJob.ensureDebugId("DA7343DF-499C-485e-AD1D-69E2BFE39405");
			btSubmitSyncJob.setText(UIContext.Constants.ArchiveSyncJob());
			btSubmitSyncJob.setMinWidth(MIN_WIDTH);		
			btSubmitSyncJob.addListener(Events.Select,selectionList);		
			this.addButton(btSubmitSyncJob);
			
			btReadExistingCatalog = new Button()
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
			btReadExistingCatalog.ensureDebugId("D553051B-1691-458a-B2F4-1D4E8005A2E8");
			btReadExistingCatalog.setText(UIContext.Constants.BrowseExisting());
			btReadExistingCatalog.setMinWidth(MIN_WIDTH);		
			btReadExistingCatalog.addListener(Events.Select,selectionList);		
			this.addButton(btReadExistingCatalog);
			
			if(!destChangeModel.getCatalogAvailable())
				btReadExistingCatalog.setEnabled(false);
			
			
			btCatalogSyncCancel = new Button()
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
			btCatalogSyncCancel.ensureDebugId("58454D9C-901C-463b-B2D0-CA46BCE1A9C4");
			btCatalogSyncCancel.setText(UIContext.Constants.cancel());
			btCatalogSyncCancel.setMinWidth(MIN_WIDTH);
			btCatalogSyncCancel.addListener(Events.Select,selectionList);		
			this.addButton(btCatalogSyncCancel);	
		}
	}
	
	
	private boolean isEmpty(String value)
	{
		final String EMPTY = "";
		if((value == null) || (EMPTY.equals(value) ))
			return true;
		return false;
	}
}
