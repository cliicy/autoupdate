package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseCommonSettingTab;
import com.ca.arcflash.ui.client.common.ISettingsContent;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.PreferencePresenter;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.exception.ClientException;
import com.ca.arcflash.ui.client.model.EmailAlertsModel;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.ca.arcflash.ui.client.model.PreferencesModel;
import com.ca.arcflash.ui.client.model.StagingServerModel;
import com.ca.arcflash.ui.client.model.UpdateSettingsModel;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class PreferencesSettingsContent extends LayoutContainer
	implements ISettingsContent, IUpdateSettingHost
{
	//private PreferencesWindow thisWindow;
	private PreferencesSettingsContent outerThis;
	private ISettingsContentHost contentHost;
//	private boolean isEditable = true;
	
	public ISettingsContentHost getContentHost() {
		return contentHost;
	}

	public static final int DEFAULT_RETENTION_COUNT = 31;
	
	//private GeneralSettings generalSettings;
	private EmailAlerts emailAlerts;
	private UpdateSettings updateSettings;
	private GeneralSettingsWindow generalSettings;
	
	LayoutContainer emailAlertsContainer;
	LayoutContainer UpdateSettingsContainer;
	LayoutContainer GeneralSettingsContainer;
	
	public DeckPanel deckPanel;
	
	private ToggleButton emailAlertsButton;
	private ToggleButton UpdateSettingsButton;
	private ToggleButton GeneralSettingsButton;
	
	private ToggleButton emailAlertsLabel;
	private ToggleButton UpdateSettingsLabel;
	private ToggleButton GeneralSettingsLabel;
		
	private ClickHandler emailAlertsButtonHandler;
	private ClickHandler UpdateButtonHandler;
	private ClickHandler GeneralSettingsButtonHandler;
	
	private Button SaveSettingsButton;
	private Button cancelButton;
	
	private VerticalPanel toggleButtonPanel;
	public PreferencesModel model;
	
	public static final int STACK_GENERAL = 0;
	public static final int STACK_EMAILALERTS = 1;
	public static final int STACK_SELFUPDATE = 2;
	
	public static final long AF_ERR_DEST_SYSVOL = 3758096417l;
	public static final long AF_ERR_DEST_BOOTVOL = 3758096418l; 
	public static final String ERR_REMOTE_DEST_WINSYSMSG = "17179869199";
	private static boolean initialTestConnection = false;
	
	public void setInitialTestConnection(boolean initialTestConnection) {
		this.initialTestConnection = initialTestConnection;
	}

	private static int buttonSelected;	
	

	private PreferencePresenter preferencePresenter;
	
	public static int getButtonSelected() {
		return buttonSelected;
	}

	public static void setButtonSelected(int buttonSelected) {
		PreferencesSettingsContent.buttonSelected = buttonSelected;
	}

	public PreferencesSettingsContent()
	{
		this.outerThis = this;
		preferencePresenter = new PreferencePresenter(this);
	}
	
	public List<String> itemsToDisplay = new ArrayList<String>();
	
	public void doInitialization() {
		
		this.setLayout( new RowLayout( Orientation.VERTICAL ) );

		LayoutContainer contentPanel = new LayoutContainer();
		contentPanel.setLayout( new RowLayout( Orientation.HORIZONTAL ) );
		
		//this.setResizable(false);
		//thisWindow = this;
		//TableLayout topLayout = new TableLayout();
		//topLayout.setHeight("100%");
		//topLayout.setWidth("100%");
		//topLayout.setCellVerticalAlign(VerticalAlignment.TOP);
		//topLayout.setColumns(2);
		//this.setLayout(topLayout);
		this.setStyleAttribute("background-color","#DFE8F6");
				
		//this.setWidth(750);
		//this.setHeight(500);
		
		deckPanel = new DeckPanel();
		deckPanel.setStyleName("backupSettingCenter");
		//Changed for UI adjustment
		//deckPanel.setHeight("520px");
		
//		deckPanel.setWidth("99%");
//		deckPanel.setHeight("100%");
		
		generalSettings = new GeneralSettingsWindow(this);
		GeneralSettingsContainer = new LayoutContainer();
		GeneralSettingsContainer.add(generalSettings.Render());
		GeneralSettingsContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(GeneralSettingsContainer);
		
		emailAlerts = new EmailAlerts(this);
		emailAlertsContainer = new LayoutContainer();
		emailAlertsContainer.add(emailAlerts.Render());
		emailAlertsContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(emailAlertsContainer);
		
		updateSettings = new UpdateSettings(this);
		updateSettings.setForEdge(isForEdge);
		initialTestConnection = false;
		UpdateSettingsContainer = new LayoutContainer();
		UpdateSettingsContainer.add(updateSettings.Render());
		UpdateSettingsContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(UpdateSettingsContainer);
		
		toggleButtonPanel = new VerticalPanel();
		toggleButtonPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleButtonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		toggleButtonPanel.setTableWidth("100%");
		//Changed for UI adjustment
		toggleButtonPanel.setHeight(520);
		toggleButtonPanel.setStyleAttribute("background-color","#DFE8F6");
		
		//General settings
		GeneralSettingsButtonHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_GENERAL);
				
				GeneralSettingsButton.setDown(true);
				emailAlertsButton.setDown(false);
				UpdateSettingsButton.setDown(false);
				
				GeneralSettingsLabel.setDown(true);
				emailAlertsLabel.setDown(false);
				UpdateSettingsLabel.setDown(false);
				setButtonSelected(1);
				contentHost.setCaption(UIContext.Messages.preferencesWindowWithTap(UIContext.Constants.preferencesGeneral()));
			}
			
		};

//		GeneralSettingsButton = new ToggleButton(UIContext.IconBundle.backupDestination().createImage());
		GeneralSettingsButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.pref_settings_general()).createImage());
		GeneralSettingsButton.ensureDebugId("91ddd787-cca8-4865-8633-91ba0a646239");
		GeneralSettingsButton.setStylePrimaryName("demo-ToggleButton");
		GeneralSettingsButton.addClickHandler(GeneralSettingsButtonHandler);
		GeneralSettingsButton.setDown(true);
		toggleButtonPanel.add(GeneralSettingsButton);
		
		GeneralSettingsLabel = new ToggleButton(UIContext.Constants.preferencesGeneralLabel());
		GeneralSettingsLabel.ensureDebugId("bd71434a-d587-45bc-90f9-fd8e69ed985c");
		GeneralSettingsLabel.setStylePrimaryName("tb-settings");
		GeneralSettingsLabel.setDown(true);	
		//Changed for UI adjustment
		//GeneralSettingsLabel.setWidth("130px");
		GeneralSettingsLabel.addClickHandler(GeneralSettingsButtonHandler);		
		toggleButtonPanel.add(GeneralSettingsLabel);

		
		itemsToDisplay.add(UIContext.Constants.preferencesGeneralLabel());
		// adding email alerts
		emailAlertsButtonHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_EMAILALERTS);
				GeneralSettingsButton.setDown(false);
				emailAlertsButton.setDown(true);
				UpdateSettingsButton.setDown(false);
				
				GeneralSettingsLabel.setDown(false);
				emailAlertsLabel.setDown(true);
				UpdateSettingsLabel.setDown(false);
				setButtonSelected(2);
				// Updating heading.
				contentHost.setCaption(UIContext.Messages.preferencesWindowWithTap(UIContext.Constants.preferencesEmailAlerts()));
			}
			
		};
		
//		emailAlertsButton = new ToggleButton(UIContext.IconBundle.backupSchedule().createImage());
		emailAlertsButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.pref_settings_emailalert()).createImage());
		emailAlertsButton.ensureDebugId("075da001-590e-455f-99cf-53603384e63c");
		emailAlertsButton.setStylePrimaryName("demo-ToggleButton");
		emailAlertsButton.addClickHandler(emailAlertsButtonHandler);
		toggleButtonPanel.add(emailAlertsButton);
		
		emailAlertsLabel = new ToggleButton(UIContext.Constants.preferencesEmailAlertsLabel());
		emailAlertsLabel.ensureDebugId("af109788-f662-4812-a3d9-bc065effbf09");
		emailAlertsLabel.setStylePrimaryName("tb-settings");
		//Changed for UI adjustment
		//emailAlertsLabel.setWidth("130px");
		emailAlertsLabel.addClickHandler(emailAlertsButtonHandler);		
		toggleButtonPanel.add(emailAlertsLabel);
				
		itemsToDisplay.add(UIContext.Constants.preferencesEmailAlertsLabel());
		//Adding updates button to toggle panel
		
		UpdateButtonHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_SELFUPDATE);
				GeneralSettingsButton.setDown(false);
				emailAlertsButton.setDown(false);	
				UpdateSettingsButton.setDown(true);
				
				GeneralSettingsLabel.setDown(false);
				emailAlertsLabel.setDown(false);
				UpdateSettingsLabel.setDown(true);
				setButtonSelected(3);
				// Updating heading.
				contentHost.setCaption(UIContext.Messages.preferencesWindowWithTap(UIContext.Constants.preferencesUpdatesLabel()));
				if(!initialTestConnection)
				{
					updateSettings.bInitialTestConnection = true;
					/*On Selection of " Auto Update" Tab, Perform test connection */
					//updateSettings.TestDownloadServerConnections(false); //marked by cliicy.luo
					updateSettings.TestBIDownloadServerConnections(false); //added by cliicy.luo
					initialTestConnection = true;
				}
			}
			
		};
		
//		UpdateSettingsButton = new ToggleButton(UIContext.IconBundle.backupSettings().createImage());
		UpdateSettingsButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.pref_settings_update()).createImage());
		UpdateSettingsButton.ensureDebugId("f066d262-19f1-437e-a7f9-7186c5fa3e14");
		UpdateSettingsButton.setStylePrimaryName("demo-ToggleButton");
		UpdateSettingsButton.addClickHandler(UpdateButtonHandler);
		toggleButtonPanel.add(UpdateSettingsButton);
		
		UpdateSettingsLabel = new ToggleButton(UIContext.Constants.preferencesUpdatesLabel());
		UpdateSettingsLabel.ensureDebugId("d7d6934f-7ea2-4e58-8139-d93f191ec429");
		UpdateSettingsLabel.setStylePrimaryName("tb-settings");
		//Changed for UI adjustment
		//UpdateSettingsLabel.setWidth("130px");
		UpdateSettingsLabel.addClickHandler(UpdateButtonHandler);		
		toggleButtonPanel.add(UpdateSettingsLabel);
		
		itemsToDisplay.add(UIContext.Constants.preferencesUpdatesLabel());
		//this.add(toggleButtonPanel);
		
		//TableData tableData = new TableData();
		//tableData.setWidth("95%");
		//this.add(deckPanel, tableData);
		
		contentPanel.add( toggleButtonPanel, new RowData( 140, 1 ) );
		contentPanel.add( deckPanel, new RowData( 1, 1 ) );
		
		this.add( contentPanel, new RowData( 1, 1 ) );		
		
		//Load General
		deckPanel.showWidget(STACK_GENERAL);
		contentHost.setCaption(UIContext.Messages.preferencesWindowWithTap(UIContext.Constants.preferencesGeneral()));

//		if (!this.isForEdge)
//			disableEditingIfUsingEdgePolicy();
		if(!this.isForEdge) {
			hideUpdateIsNeeded();
		}
	}
	
	private void hideUpdateIsNeeded() {
		if(UIContext.serverVersionInfo.isShowUpdate() != null 
    			&& !UIContext.serverVersionInfo.isShowUpdate()){
			UpdateSettingsButton.setVisible(false);		
			UpdateSettingsLabel.setVisible(false);
		}
	}
	
	
	/**
	 * This method can be only used for disable edit. 
	 * Don't use method to enable edit. Enable this edit using this method will make UI value wrong.
	 */
	public void enableEditing( boolean isEnabled )
	{
		//this.GeneralSettings..setEnabled( isEnabled );
		//this.emailAlertsContainer.setEnabled( isEnabled );
		//this.UpdateSettingsContainer.setEnabled( isEnabled );
		emailAlerts.setEditable(isEnabled);
		updateSettings.setEditable(isEnabled);
		generalSettings.setEditable(isEnabled);
		
		if (this.SaveSettingsButton != null)
			this.SaveSettingsButton.setEnabled( isEnabled );
	}

	protected boolean validateSettings() {
		if (generalSettings.Validate())
		{
			this.generalSettings.save();
		}
		else
		{
			deckPanel.showWidget(STACK_GENERAL);
			SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dPreferenceSettingID, STACK_GENERAL);
			this.contentHost.showSettingsContent( this.settingsContentId );
			this.onValidatingCompleted( false );
			return false;
		}
		
		if (emailAlerts.Validate())
		{
			this.emailAlerts.Save();
		}
		else
		{
			deckPanel.showWidget(STACK_EMAILALERTS);
			SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dPreferenceSettingID, STACK_EMAILALERTS);
			this.contentHost.showSettingsContent( this.settingsContentId );
			this.onValidatingCompleted( false );
			return false;
		}
		
		if (updateSettings.Validate())
		{
			this.updateSettings.save();
		}
		else
		{
			deckPanel.showWidget(STACK_SELFUPDATE);
			SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dPreferenceSettingID, STACK_SELFUPDATE);
			this.contentHost.showSettingsContent( this.settingsContentId );
			this.onValidatingCompleted( false );
			return false;
		}
		
		contentHost.increaseBusyCount();
		Broker.loginService.validatePreferences(model, new AsyncCallback<Long>() {
			
			@Override
			public void onSuccess(Long result) {
				contentHost.decreaseBusyCount();
				initialTestConnection = false;
				
				outerThis.onValidatingCompleted(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				contentHost.decreaseBusyCount();

				outerThis.onValidatingCompleted(false);
				
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
				msgError.setModal(true);
				String strMsg = UIContext.Messages.failedToSavePreferences();
				strMsg += (caught instanceof ClientException) ? ((ClientException) caught)
						.getDisplayMessage() : caught.getMessage();
				msgError.setMessage(strMsg);
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
			}
		});
		return true;
	}
	
	public void ToggleWindowButtons(boolean bEnable)
	{
		SaveSettingsButton.setEnabled(bEnable);
		cancelButton.setEnabled(bEnable);
	}
	
	public void RefreshData()
	{
		if(model == null)
		{
			model = new PreferencesModel();
		}
		generalSettings.RefreshData(model.getGeneralSettings());
		emailAlerts.RefreshData(model.getEmailAlerts());
		updateSettings.RefreshData(model.getupdateSettings());
		
//		if (!this.isEditable)
//			enableEditing( this.isEditable );
	}

	public void LoadDefaultSettings()
	{
		PreferencesModel defaultModel = createDefaultModel();
		onLoadSettingsComplete( defaultModel );
	}
	
	private PreferencesModel createDefaultModel()
	{
		PreferencesModel model = new PreferencesModel();
		
		// General
		
		// The general settings UI will set default values on the UI, and
		// this function is used currently only for set default values. To
		// make the code easy to maintain, we don't set default values for
		// general settings here, and let the UI set it.
		
		//GeneralSettingsModel generalSettingsModel = new GeneralSettingsModel();
		//generalSettingsModel.setNewsFeed( true );
		//generalSettingsModel.setSocialNetworking( true );
		//generalSettingsModel.setTrayNotificationType( 0 );
		//generalSettingsModel.setUseVideos( 1 );
		//model.setGeneralSettings( generalSettingsModel );
		
		// Email Alerts
		
		EmailAlertsModel emailAlertModel = new EmailAlertsModel();
		emailAlertModel.setEnableSettings(false);
		emailAlertModel.setEnableEmail(false); // for default value.
		emailAlertModel.setEnableSrmPkiAlert(false);
		model.setEmailAlerts( emailAlertModel );
		
		// Auto Update
		
		UpdateSettingsModel updateSettingsModel = new UpdateSettingsModel();
		updateSettingsModel.setDownloadServerType( 0 ); // CA server
		updateSettingsModel.setproxySettings( null );
		updateSettingsModel.setAutoCheckupdate( true );
		updateSettingsModel.setScheduledHour( null );
		updateSettingsModel.setScheduledWeekDay( null );
		
		if (this.isForEdge)
		{
			if( "http:".equalsIgnoreCase(Window.Location.getProtocol()) ){
				try
				{
					StagingServerModel stagingServer = new StagingServerModel();
					stagingServer.setStagingServerId( -1 );
					if( UIContext.hostName != null && !"".equals(UIContext.hostName) )
						stagingServer.setStagingServer(UIContext.hostName);
					else
						stagingServer.setStagingServer( Window.Location.getHostName() );
					stagingServer.setStagingServerPort( Integer.parseInt( Window.Location.getPort() ) );
					stagingServer.setStagingServerStatus( -1 );
					
					StagingServerModel[] stagingSvrArray = new StagingServerModel[1];
					stagingSvrArray[0] = stagingServer;
		
					updateSettingsModel.setDownloadServerType( 1 ); // staging server
					updateSettingsModel.setStagingServers( stagingSvrArray );
				}
				catch (Exception e)
				{
					MessageBox messageBox = new MessageBox();
					messageBox.setTitleHtml( UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D) );
					messageBox.setMessage( "Error adding Edge server as auto update staging server." );
					messageBox.setIcon( MessageBox.ERROR );
					messageBox.setButtons( MessageBox.OK );
					Utils.setMessageBoxDebugId(messageBox);
					messageBox.show();
				}
			}
		}
		
		model.setupdateSettings( updateSettingsModel );
		
		return model;
	}
	
	private void onLoadSettingsComplete( PreferencesModel model )
	{
		outerThis.model = model;
		outerThis.RefreshData();
		
		onLoadingCompleted(true);
	}

	@Override
	public UpdateSettingsModel getUpdateSettingModel() {
		return model == null ? null : model.getupdateSettings();
	}

	@Override
	public void setUpdateSettingModel(UpdateSettingsModel updateSettingsModel) {
		if (model != null) {
			model.setupdateSettings(updateSettingsModel);
		}
	}

	//////////////////////////////////////////////////////////////////////////
	//
	//  ADDED FOR EDGE
	//
	//////////////////////////////////////////////////////////////////////////

	private boolean isForEdge = false;
	private int settingsContentId = -1;
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void initialize( ISettingsContentHost contentHost, boolean isForEdge )
	{
		this.contentHost = contentHost;
		this.isForEdge = isForEdge;
		
		this.doInitialization();
	}

	@Override
	public boolean isForEdge()
	{
		return this.isForEdge;
	}

	@Override
	public void setIsForEdge( boolean isForEdge )
	{
		this.isForEdge = isForEdge;
	}
	
	@Override
	public void setId( int settingsContentId )
	{
		this.settingsContentId = settingsContentId;
	}
	
	@Override
	public Widget getWidget()
	{
		return this;
	}

	@Override
	public void loadData()
	{
		PreferencesModel pModel = SettingPresenter.getInstance().getD2dSettings().getPreferencesModel();
		outerThis.model = pModel;
		outerThis.RefreshData();	
		onLoadingCompleted(true);
	}

	@Override
	public void loadDefaultData()
	{
		LoadDefaultSettings();
	}

	@Override
	public void saveData()
	{
		SettingPresenter.getInstance().getD2dSettings().setPreferencesModel(model);
		initialTestConnection = false;
		this.onSavingCompleted(true);
	}
	
	@Override
	public void validate()
	{
		validateSettings();
	}
	
	@Override
	public void setDefaultEmail(IEmailConfigModel iEmailConfigModel)
	{
		emailAlerts.setDefaultEmail(iEmailConfigModel);
	}

	private void onSavingCompleted( boolean isSuccessful )
	{
		GWT.log("The preference save compelte:"+isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.SaveData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}
	
	public void onValidatingCompleted( boolean isSuccessful )
	{
		GWT.log("The preference validate compelte:"+isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.Validate,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}
	
	private void onLoadingCompleted( boolean isSuccessful )
	{
		GWT.log("The preference load compelte:"+isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.LoadData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}

	@Override
	public boolean isForLiteIT() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setisForLiteIT(boolean isForLiteIT) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<SettingsTab> getTabList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void switchTab(String tabId) {
		// TODO Auto-generated method stub
		
	}
}
