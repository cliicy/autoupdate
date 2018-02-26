package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.advschedule.ScheduleItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseCommonSettingTab;
import com.ca.arcflash.ui.client.common.ISettingsContent;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.FileCopyPresenter;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.model.ArchiveCloudDestInfoModel;
import com.ca.arcflash.ui.client.model.ArchiveSettingsModel;
import com.ca.arcflash.ui.client.model.ArchiveSourceInfoModel;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.ca.arcflash.ui.client.model.CloudVendorInfoModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class ArchiveSettingsContent extends LayoutContainer implements ISettingsContent {
	private ArchiveSettingsContent thisWindow;
	public DeckPanel archiveDeckPanel;
	
	public static final int STACK_ARCHIVE_SOURCE = 0;
	public static final int STACK_ARCHIVE_DESTINATION = 1;
	public static final int STACK_ARCHIVE_SCHEDULE = 2;
	//public final int STACK_ARCHIVE_ADVANCED = 3;
	
	private ArchiveSourceSettings archiveSourceSettings;
	private LayoutContainer lcArchiveSourceContainer;
	private ToggleButton tbArchiveSourceButton;
	private ToggleButton tbArchiveSourceLabel;
	private ClickHandler chArchiveSourceButtonHandler;
	
	private ArchiveDestinationSettings archiveDestSettings;
	public ArchiveDestinationSettings getArchiveDestSettings() {
		return archiveDestSettings;
	}

	private LayoutContainer lcArchiveDestinationContainer;
	private ToggleButton tbArchiveDestinationButton;
	private ToggleButton tbArchiveDestinationLabel;
	private ClickHandler chArchiveDestinationButtonHandler;
	
	private ArchiveScheduleSettings archiveScheduleSettings;
	private LayoutContainer lcArchiveScheduleContainer;
	private ToggleButton tbArchiveScheduleButton;
	private ToggleButton tbArchiveScheduleLabel;
	private ClickHandler chArchiveScheduleButtonHandler;
	
	//private final String strErrorMessageTitle = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
	
	private VerticalPanel toggleButtonPanel;
	
	public DestinationCapacityModel destModel;
	ArchiveSettingsModel archiveConfigModel = null;
	public ArchiveSettingsModel getArchiveConfigModel() {
		return archiveConfigModel;
	}

	boolean archiveSettingFileExist_b = false;
	public static final String ERR_REMOTE_DEST_WINSYSMSG = "51539607559";
	public static final String INVALID_ARCHIVE_SOURCEFOUND = "51539607557";
	public static final String DEST_IN_USE = "20937965751";
	
	/////purge schedule
	boolean bEnablePurge = false;
	boolean bPurgeScheduleAvailable = false;
	int iPurgeAfterNDays;
	long lPurgeStartTime;
	private String cloudBucket;
	
	public List<String> itemsToDisplay = new ArrayList<String>();
	
//	protected boolean isEditable = true;
	
	private static int buttonSelected ;	
	
	BackupSettingsContent backupContent;
	
	Boolean isFileCopyToCloudEnabled;
	Boolean isFileCopyEnabled;
	Boolean isFileArchiveEnabled;
	
	//When validate backend failed with 17179869199, we let user try to input username/password again.
	private boolean firstTry = true;
	private FileCopyPresenter fileCopyPresenter;
	private boolean isArchiveTask;
	private int rootTabId;
	public ArchiveSettingsContent(BackupSettingsContent in_backupsettinContent, boolean isArchiveTask)
	{
		backupContent = in_backupsettinContent;
		this.isArchiveTask = isArchiveTask;
		rootTabId = this.isArchiveTask ? BaseCommonSettingTab.fileArchiveSettingID : BaseCommonSettingTab.archiveSettingID;
		CustomizationModel customizedModel = UIContext.customizedModel;
		isFileCopyToCloudEnabled = customizedModel.get("FileCopyToCloud");
		isFileCopyEnabled = customizedModel.get("FileCopy");
		isFileArchiveEnabled = customizedModel.get("FileArchive");
		
		fileCopyPresenter = new FileCopyPresenter(this);
	}
	
	public void focusPanel(int index){
		setDeckPanel(index);
	}

	public static int getButtonSelected() {
		return buttonSelected;
	}

	public static void setButtonSelected(int buttonSelected) {
		ArchiveSettingsContent.buttonSelected = buttonSelected;
	}
	
	public boolean isArchiveTask() {
		return isArchiveTask;
	}

	private void doInitialization()
	{
		thisWindow = this;

		this.setLayout( new RowLayout( Orientation.VERTICAL ) );

		LayoutContainer contentPanel = new LayoutContainer();
		contentPanel.setLayout( new RowLayout( Orientation.HORIZONTAL ) );
		this.setStyleAttribute("background-color","#DFE8F6");
	
		archiveDeckPanel = new DeckPanel();
		archiveDeckPanel.setStyleName("backupSettingCenter");
//		//Changed for UI adjustment
//		archiveDeckPanel.setHeight("520px");
		//archiveDeckPanel.setWidth("100%");
		//archiveDeckPanel.setHeight("100%");
		
		toggleButtonPanel = new VerticalPanel();
		toggleButtonPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleButtonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		toggleButtonPanel.setTableWidth("100%");
		//Changed for UI adjustment
		toggleButtonPanel.setHeight(520);
		toggleButtonPanel.setStyleAttribute("background-color","#DFE8F6");
		
		//Archive Source 
		archiveSourceSettings = new ArchiveSourceSettings(this);
		lcArchiveSourceContainer = new LayoutContainer();
		lcArchiveSourceContainer.add(archiveSourceSettings.Render());
		lcArchiveSourceContainer.setStyleAttribute("padding", "10px");
		archiveDeckPanel.add(lcArchiveSourceContainer);
		
		chArchiveSourceButtonHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				archiveDeckPanel.showWidget(STACK_ARCHIVE_SOURCE);
				tbArchiveDestinationButton.setDown(false);
				tbArchiveScheduleButton.setDown(false);
				tbArchiveSourceButton.setDown(true);
				
				tbArchiveDestinationLabel.setDown(false);
				tbArchiveScheduleLabel.setDown(false);
				tbArchiveSourceLabel.setDown(true);
				//tbArchiveAdvancedButton.setDown(false);
				//tbArchiveAdvancedLabel.setDown(false);
				setButtonSelected(1);
				// Updating heading.
				setHeading(UIContext.Messages.archiveSettingsWindowWithTap(UIContext.Constants.ArchiveSource()));
			}			
		};
		
//		tbArchiveSourceButton = new ToggleButton(UIContext.IconBundle.backupAdvanced().createImage());
		tbArchiveSourceButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.filecopy_settings_source()).createImage());
		tbArchiveSourceButton.ensureDebugId("35963CD0-3E34-4c55-8436-B4A987703FAE");
		tbArchiveSourceButton.setStylePrimaryName("demo-ToggleButton");
		tbArchiveSourceButton.setDown(true);
		tbArchiveSourceButton.addClickHandler(chArchiveSourceButtonHandler);
		toggleButtonPanel.add(tbArchiveSourceButton);
		
		tbArchiveSourceLabel = new ToggleButton(UIContext.Constants.ArchiveSource());
		tbArchiveSourceLabel.ensureDebugId("EC4E00F2-6E16-4eca-ADF6-5D1014F2BA19");
		tbArchiveSourceLabel.setStylePrimaryName("tb-settings");
		tbArchiveSourceLabel.setDown(true);
		//Changed for UI adjustment
		//tbArchiveSourceLabel.setWidth("130px");
		tbArchiveSourceLabel.addClickHandler(chArchiveSourceButtonHandler);
		toggleButtonPanel.add(tbArchiveSourceLabel);
		
		itemsToDisplay.add(UIContext.Constants.ArchiveSource());
		
		//Archive Destination 
		archiveDestSettings = new ArchiveDestinationSettings(this,isForEdge);
		lcArchiveDestinationContainer = new LayoutContainer();
		lcArchiveDestinationContainer.add(archiveDestSettings.Render());
		lcArchiveDestinationContainer.setStyleAttribute("padding", "10px");
		archiveDeckPanel.add(lcArchiveDestinationContainer);
		
		chArchiveDestinationButtonHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				archiveDeckPanel.showWidget(STACK_ARCHIVE_DESTINATION);
				tbArchiveDestinationButton.setDown(true);
				tbArchiveScheduleButton.setDown(false);
				tbArchiveSourceButton.setDown(false);
				
				tbArchiveDestinationLabel.setDown(true);
				tbArchiveScheduleLabel.setDown(false);
				tbArchiveSourceLabel.setDown(false);
				//tbArchiveAdvancedButton.setDown(false);
				//tbArchiveAdvancedLabel.setDown(false);
				setButtonSelected(2);
				// Updating heading.
				setHeading(UIContext.Messages.archiveSettingsWindowWithTap(UIContext.Constants.ArchiveDestination()));
			}			
		};
		
//		tbArchiveDestinationButton = new ToggleButton(UIContext.IconBundle.backupDestination().createImage());
		tbArchiveDestinationButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.filecopy_settings_dest()).createImage());
		tbArchiveDestinationButton.ensureDebugId("AB330B95-5473-46b7-9A75-EEFE0762923C");
		tbArchiveDestinationButton.setStylePrimaryName("demo-ToggleButton");
		tbArchiveDestinationButton.addClickHandler(chArchiveDestinationButtonHandler);
		toggleButtonPanel.add(tbArchiveDestinationButton);
		
		tbArchiveDestinationLabel = new ToggleButton(UIContext.Constants.ArchiveDestination());
		tbArchiveDestinationLabel.ensureDebugId("F4B3F215-1B10-4de7-90FD-9D0A86B275DC");
		tbArchiveDestinationLabel.setStylePrimaryName("tb-settings");
		//Changed for UI adjustment
		//tbArchiveDestinationLabel.setWidth("130px");
		tbArchiveDestinationLabel.addClickHandler(chArchiveDestinationButtonHandler);
		toggleButtonPanel.add(tbArchiveDestinationLabel);
		
		itemsToDisplay.add(UIContext.Constants.ArchiveDestination());
		
		//Archive Schedule 
		archiveScheduleSettings = new ArchiveScheduleSettings(this);
		lcArchiveScheduleContainer = new LayoutContainer();
		lcArchiveScheduleContainer.add(archiveScheduleSettings.render());
		lcArchiveScheduleContainer.setStyleAttribute("padding", "10px");
		archiveDeckPanel.add(lcArchiveScheduleContainer);
		
		chArchiveScheduleButtonHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				archiveDeckPanel.showWidget(STACK_ARCHIVE_SCHEDULE);
				tbArchiveDestinationButton.setDown(false);
				tbArchiveScheduleButton.setDown(true);
				tbArchiveSourceButton.setDown(false);
				
				tbArchiveDestinationLabel.setDown(false);
				tbArchiveScheduleLabel.setDown(true);
				tbArchiveSourceLabel.setDown(false);
			//	tbArchiveAdvancedButton.setDown(false);
			//	tbArchiveAdvancedLabel.setDown(false);
				setButtonSelected(3);
				// Updating heading.
				setHeading(UIContext.Messages.archiveSettingsWindowWithTap(UIContext.Constants.ArchiveSchedule()));
			}			
		};
		
//		tbArchiveScheduleButton = new ToggleButton(UIContext.IconBundle.backupSchedule().createImage());
		tbArchiveScheduleButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.filecopy_settings_schedule()).createImage());
		tbArchiveScheduleButton.ensureDebugId("3B5779B8-E18C-461c-84D1-0AC85AAA4869");
		tbArchiveScheduleButton.setStylePrimaryName("demo-ToggleButton");
		tbArchiveScheduleButton.addClickHandler(chArchiveScheduleButtonHandler);
		toggleButtonPanel.add(tbArchiveScheduleButton);
		
		tbArchiveScheduleLabel = new ToggleButton(UIContext.Constants.backupSettingsSchedule());
		tbArchiveScheduleLabel.ensureDebugId("9B1AE306-D746-4f43-BCD6-7441EEDA70DA");
		tbArchiveScheduleLabel.setStylePrimaryName("tb-settings");
		//Changed for UI adjustment
		//tbArchiveScheduleLabel.setWidth("130px");
		tbArchiveScheduleLabel.addClickHandler(chArchiveScheduleButtonHandler);
		toggleButtonPanel.add(tbArchiveScheduleLabel);
		
		itemsToDisplay.add(UIContext.Constants.backupSettingsSchedule());
		//ArchiveAdvanced options
		//archiveDestSettings = new archiveDestSettings(this);
		//lcArchiveAdvancedContainer = new LayoutContainer();
		//lcArchiveAdvancedContainer.add(archiveDestSettings.Render());
		//lcArchiveAdvancedContainer.setStyleAttribute("padding", "10px");
		//archiveDeckPanel.add(lcArchiveAdvancedContainer);
		
/*		chArchiveAdvancedButtonHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				archiveDeckPanel.showWidget(STACK_ARCHIVE_DESTINATION);
				tbArchiveAdvancedButton.setDown(true);
				tbArchiveDestinationButton.setDown(false);
				tbArchiveScheduleButton.setDown(false);
				tbArchiveSourceButton.setDown(false);
				
				tbArchiveDestinationLabel.setDown(false);
				tbArchiveScheduleLabel.setDown(false);
				tbArchiveSourceLabel.setDown(false);
				tbArchiveAdvancedLabel.setDown(true);
				setButtonSelected(4);
				// Updating heading.
				setHeading(UIContext.Messages.archiveSettingsWindowWithTap(UIContext.Constants.ArchiveAdvanced()));
			}			
		};*/
		
//		tbArchiveAdvancedButton = new ToggleButton(UIContext.IconBundle.backupSettings().createImage());
		//tbArchiveAdvancedButton = new ToggleButton(UIContext.IconBundle.filecopy_settings_advanced().createImage());
		//tbArchiveAdvancedButton.setStylePrimaryName("demo-ToggleButton");
		//tbArchiveAdvancedButton.addClickHandler(chArchiveAdvancedButtonHandler);
		//toggleButtonPanel.add(tbArchiveAdvancedButton);
		
		//tbArchiveAdvancedLabel = new ToggleButton(UIContext.Constants.ArchiveAdvanced());
		//tbArchiveAdvancedLabel.setStylePrimaryName("tb-settings");
		//Changed for UI adjustment
		//tbArchiveAdvancedLabel.setWidth("130px");
		//tbArchiveAdvancedLabel.addClickHandler(chArchiveAdvancedButtonHandler);
		//toggleButtonPanel.add(tbArchiveAdvancedLabel);
		
		contentPanel.add( toggleButtonPanel, new RowData( 140, 1 ) );
		contentPanel.add( archiveDeckPanel, new RowData( 1, 1 ) );
		
		this.add( contentPanel, new RowData( 1, 1 ) );
		
//		contentPanel.add(toggleButtonPanel);
//		
//		TableData tableData = new TableData();
//		tableData.setWidth("90%");
//		contentPanel.add(archiveDeckPanel);
		
		// Default Tab - destination.
		setHeading(UIContext.Messages.archiveSettingsWindowWithTap(UIContext.Constants.ArchiveSource()));
		
		//Load the Backup Settings
		archiveDeckPanel.showWidget(STACK_ARCHIVE_SOURCE);
	}

	private boolean validateUIForEdge()
	{
		boolean isLocalPathOrShareFolder = archiveDestSettings.rbLocalDrive.getValue();
		String destination = this.archiveDestSettings.getPathSelectionPanel().getDestination();
			
		if (isLocalPathOrShareFolder &&
			((destination != null) && (destination.length() > 0)))
		{
			//Check share folder
			if(!this.archiveDestSettings.checkShareFolder()){
				setDeckPanel(STACK_ARCHIVE_DESTINATION);
				onValidatingCompleted(false);
				return false;
			}
		
			//Check remote path . if success , redirect to validateBackend 
			this.archiveDestSettings.validateRemotePath(new BaseAsyncCallback<Boolean>(false)
					{
						@Override
						public void onFailure(Throwable caught)
						{
							super.onFailure(caught);
							setDeckPanel(STACK_ARCHIVE_DESTINATION);
							onValidatingCompleted(false);
							return;
						}
						
						@Override
						public void onSuccess(Boolean result)
						{
							if (!result)
							{
								setDeckPanel(STACK_ARCHIVE_DESTINATION);
								onValidatingCompleted(false);
								return;
							}
							
							contentHost.increaseBusyCount();
							validateBackend();
						}
						
					});
			
			return true;
			
		}else{
			
			contentHost.increaseBusyCount();
			return validateBackend();
			
		}

	}

	private void populateCloudConfigWindow()
	{
		archiveDestSettings.windCloudConfig = new CloudConfigWindow(CloudConfigWindow.ARCHIVE_MODE,isForEdge);
		archiveDestSettings.windCloudConfig.addWindowListener(new WindowListener(){
			public void windowHide(WindowEvent we) {
				if(!archiveDestSettings.windCloudConfig.getcancelled())
				{
					archiveConfigModel.setCloudConfigModel(archiveDestSettings.windCloudConfig.getarchiveCloudConfigModel());
				}
			}
		});
		archiveDestSettings.windCloudConfig.RefreshData(archiveConfigModel.getCloudConfigModel());
	
	}
	
	public void RefreshData()
	{
//		archiveSourceSettings.getFATVolumeInfo();
		
		if(archiveConfigModel == null)
		{
			//archiveDestSettings.browseLocalOrNetworkDestinationPanel.setEnabled(false);
			if(isFileCopyToCloudEnabled)
			{	
				archiveDestSettings.btConfigureCloud.setEnabled(false);				
			}
			return;
		}
		
		int iarchiveSources = archiveConfigModel.getArchiveSources() != null ? archiveConfigModel.getArchiveSources().length : 0;
		ArchiveSourceInfoModel[] archiveSources = archiveConfigModel.getArchiveSources(); 
		
		for(int iarchiveSourceIndex = 0;iarchiveSourceIndex < iarchiveSources;iarchiveSourceIndex++)
		{
			ArchiveSourceInfoModel sourceinfo = archiveSources[iarchiveSourceIndex];
			archiveSourceSettings.gridStore.add(sourceinfo);
			archiveSourceSettings.addNotificationMessages(sourceinfo.getSourcePath(), sourceinfo.getDispalySourcePath());
		}
		archiveSourceSettings.ArchiveSourcesGrid.reconfigure(archiveSourceSettings.gridStore, archiveSourceSettings.ArchiveSourceColumnsModel);
		archiveSourceSettings.checkIfPoliciesFromSystemVolumes();
		//archiveSourceSettings.backupVolumes = archiveConfigModel.getbackupVolumes();
//		/archiveSourceSettings.backupDestination = archiveConfigModel.getbackupDestination();
		
		/*if(archiveConfigModel.getArchivedFileVersionsRetentionCount() != null)
		{
			archiveDestSettings.nfFileVersions.setValue(archiveConfigModel.getArchivedFileVersionsRetentionCount());
		}*/
		
		/*if(archiveConfigModel.getExcludeSystemFiles() != null)
		{
			archiveSourceSettings.cbArchiveExcludeSystemFiles.setValue(archiveConfigModel.getExcludeSystemFiles());
		}
		
		if(archiveConfigModel.getExcludeAppFiles() != null)
		{
			archiveSourceSettings.cbArchiveExcludeApplicationFiles.setValue(archiveConfigModel.getExcludeAppFiles());
		}*/
		
		if(archiveConfigModel.getArchiveToDrive() != null)
		{
			if(isFileCopyToCloudEnabled)
			{	
				archiveDestSettings.rbLocalDrive.setValue(archiveConfigModel.getArchiveToDrive());
			}	
			archiveDestSettings.browseLocalOrNetworkDestinationPanel.getDestinationTextField().setEnabled(archiveConfigModel.getArchiveToDrive());
			
			
			
			if(isFileCopyToCloudEnabled)
			{	
				archiveDestSettings.btConfigureCloud.setEnabled(!archiveConfigModel.getArchiveToDrive());
			}
			
			archiveDestSettings.browseLocalOrNetworkDestinationPanel.setEnabled(archiveConfigModel.getArchiveToDrive());
			if(archiveConfigModel.getArchiveToDrivePath() != null)
			{
				archiveDestSettings.browseLocalOrNetworkDestinationPanel.setDestination(archiveConfigModel.getArchiveToDrivePath());
				archiveDestSettings.browseLocalOrNetworkDestinationPanel.setUsername(archiveConfigModel.getDestinationPathUserName());
				archiveDestSettings.browseLocalOrNetworkDestinationPanel.setPassword(archiveConfigModel.getDestinationPathPassword());
				archiveDestSettings.browseLocalOrNetworkDestinationPanel.cacheInfo();
				archiveDestSettings.strDownloadLocation = archiveConfigModel.getArchiveToDrivePath();
				
				if ((archiveConfigModel.getDestinationPathUserName() != null) &&
					(archiveConfigModel.getDestinationPathUserName().length() > 0))
					archiveDestSettings.setDestinationValidated();
			}
		}
		
		if(isFileCopyToCloudEnabled)
		{	
			if(archiveConfigModel.getArchiveToCloud())
			{
				archiveDestSettings.rbCloud.setValue(archiveConfigModel.getArchiveToCloud());
				archiveDestSettings.btConfigureCloud.setEnabled(archiveConfigModel.getArchiveToCloud());
				archiveDestSettings.browseLocalOrNetworkDestinationPanel.setEnabled(!archiveConfigModel.getArchiveToCloud());
				//cloudBucket = archiveConfigModel.getCloudConfigModel().getcloudBucketName();
				if(archiveDestSettings.windCloudConfig == null)
				{
					if(archiveConfigModel == null)
					{
						archiveConfigModel = new ArchiveSettingsModel();
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
								archiveDestSettings.windCloudConfig.loadCloudContainersWithVendorURL(result);		
							}
						}
					});
				}
				else
				{
					archiveDestSettings.windCloudConfig.RefreshData(archiveConfigModel.getCloudConfigModel());
				}
			}
		}

		if(archiveConfigModel.getArchiveAfterBackup() != null)
		{
			//from r16.5 major version and minor version will be recorded in backup configuraion file. If upgrade from 16 and file copy enable, we will enable catalog generation.
			if(SettingPresenter.model !=null){
				//this configuration is for 16 because before 16.5 no major version
				if(SettingPresenter.model.getMajorVersion() == null){
					if(archiveConfigModel.getArchiveAfterBackup()){
						backupContent.getSimpleSchedule().getCatalogPanel().getCatalogCheckBox().setValue(true);
					}
				}
			}
			//madra04
			archiveSourceSettings.cbArchiveAfterBackup.setValue(archiveConfigModel.getArchiveAfterBackup());

		}
		if(!isArchiveTask()){
			if(archiveConfigModel.isBackupFrequency()){
				archiveSourceSettings.backupFreqRadio.setValue(true);
				archiveSourceSettings.backupFreqInputBox.setValue(archiveConfigModel.getArchiveAfterNumberofBackups() != null ? archiveConfigModel.getArchiveAfterNumberofBackups() : 5);
			}
			else{
				archiveSourceSettings.backupSchedRadio.setValue(true);
				archiveSourceSettings.dailyBackupCB.setValue(archiveConfigModel.isDaily());
				archiveSourceSettings.weeklyBackupCB.setValue(archiveConfigModel.isWeekly());
				archiveSourceSettings.monthlyBackupCB.setValue(archiveConfigModel.isMonthly());
			}
		}
		archiveScheduleSettings.setScheduleData(archiveConfigModel.getAdvanceSchedule());
		
		bPurgeScheduleAvailable = archiveConfigModel.getPurgeScheduleAvailable();
		bEnablePurge = archiveConfigModel.getPurgeArchiveItems();
		iPurgeAfterNDays = archiveConfigModel.getPurgeAfterDays();
		lPurgeStartTime = archiveConfigModel.getPurgeStartTime();
		
		/*if(archiveConfigModel.getPurgeArchiveItems() != null)
		{
			cbPurgeEveryNDays.setValue(archiveConfigModel.getPurgeArchiveItems());
			nfDays.setEnabled(archiveConfigModel.getPurgeArchiveItems());
			Date PurgeStartTime = new Date(archiveConfigModel.getPurgeStartTime());

			//setStartDateTime(PurgeStartTime);
			purgeStartTimeContainer.setStartDateTime(PurgeStartTime);
		}*/
		
		if (archiveConfigModel.getCompressionLevel() != null)
		{			
			if (archiveDestSettings.isStandardCompression(archiveConfigModel))
				archiveDestSettings.compressionOption.setSimpleValue(UIContext.Constants.settingsCompreesionStandard());
			else if (archiveDestSettings.isMaxCompression(archiveConfigModel))
				archiveDestSettings.compressionOption.setSimpleValue(UIContext.Constants.settingsCompressionMax());
			else
				archiveDestSettings.compressionOption.setSimpleValue(UIContext.Constants.settingsCompressionNone());
		}
		else
		{
			archiveDestSettings.compressionOption.setSimpleValue(UIContext.Constants.settingsCompreesionStandard());
		}
		
		if(archiveConfigModel.getEncryption() != null)
		{
			archiveDestSettings.cbEnableEncryption.setValue(archiveConfigModel.getEncryption());
			if(archiveConfigModel.getEncryption())
			{
				archiveDestSettings.EncryptionPassword.setValue(archiveConfigModel.getEncryptionPassword());
				archiveDestSettings.ConfirmPassword.setValue(archiveConfigModel.getEncryptionPassword());
			}
			else
			{
				archiveDestSettings.EncryptionPassword.setEnabled(false);
				archiveDestSettings.ConfirmPassword.setEnabled(false);
			}
		}
		else
		{
			archiveDestSettings.cbEnableEncryption.setValue(false);
		}
		
		/*if(archiveConfigModel.getSpaceUtilizationValue() != null)
		{
			archiveDestSettings.sArchivePerformanceSlider.setValue(archiveConfigModel.getSpaceUtilizationValue());
		}*/
		
		if(isArchiveTask()){
			if(archiveConfigModel.getRetentiontime() != null)
			{
				String strRetentionTime = archiveConfigModel.getRetentiontime();
				String[] strRetentionValues = strRetentionTime.split("\\\\");

				if(strRetentionValues.length != 3)
				{
					/*MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitle("Error");
				msgError.setModal(true);

				msgError.setMessage("Invalid retention time found.");
				msgError.show();
				return;*/
				}
				else
				{
					archiveDestSettings.cbRetentionMons.setSimpleValue(strRetentionValues[0]);
					archiveDestSettings.cbRetentionDays.setSimpleValue(strRetentionValues[1]);
					archiveDestSettings.cbRetentionYrs.setSimpleValue(strRetentionValues[2]);
				}
			}
		}else{
			if(archiveConfigModel.getFilesRetentionTime() != null && !archiveConfigModel.getFilesRetentionTime().isEmpty())
			{
				String strRetentionTime = archiveConfigModel.getFilesRetentionTime();
				String[] strRetentionValues = strRetentionTime.split("\\\\");

				if(strRetentionValues.length == 3)
				{
					archiveDestSettings.retentionValRadio.setValue(true);
					if(Integer.valueOf(strRetentionValues[0]) > 0){
						archiveDestSettings.retentionValField.setValue(Integer.valueOf(strRetentionValues[0]));
						archiveDestSettings.retentionUnitCombo.setSimpleValue(UIContext.Constants.months());
					}else if(Integer.valueOf(strRetentionValues[1]) > 0){
						archiveDestSettings.retentionValField.setValue(Integer.valueOf(strRetentionValues[1]));
						archiveDestSettings.retentionUnitCombo.setSimpleValue(UIContext.Constants.days());
					}else if(Integer.valueOf(strRetentionValues[2]) > 0){
						archiveDestSettings.retentionValField.setValue(Integer.valueOf(strRetentionValues[2]));
						archiveDestSettings.retentionUnitCombo.setSimpleValue(UIContext.Constants.years());
					}
				}
			}else if(archiveConfigModel.getArchivedFileVersionsRetentionCount() != null){
				archiveDestSettings.versionValRadio.setValue(true);
				archiveDestSettings.versionValField.setValue(archiveConfigModel.getArchivedFileVersionsRetentionCount());
			}
		}
		
		
		
		boolean cloudChosenAsDestination = false;
		if(isFileCopyToCloudEnabled)
		{
			cloudChosenAsDestination = archiveDestSettings.rbCloud.getValue();
		}
		
		if(isArchiveTask()){
			if( cloudChosenAsDestination || (archiveConfigModel.getArchiveToDrive()))
			{
				archiveDestSettings.lcRetentionSettings.setEnabled(true);
				//			archiveDestSettings.nfFileVersions.setEnabled(true);
			}
			else if( !cloudChosenAsDestination )
			{
				archiveDestSettings.lcRetentionSettings.setEnabled(true);
				//			archiveDestSettings.nfFileVersions.setEnabled(true);
			}
		}
		
	
		return;
	}
	
	private boolean isArchiveSettingSet()
	{
		boolean isArchiveSourceEmpty = false;
		boolean isArchiveDestEmpty = false;
		if(archiveSourceSettings.gridStore.getCount() == 0) {
			isArchiveSourceEmpty = true;
		}
		if(archiveDestSettings.rbLocalDrive.getValue())
		{
			if((archiveDestSettings.strDownloadLocation == null) || (archiveDestSettings.strDownloadLocation.length() == 0))
			{
				isArchiveDestEmpty = true;
			}
		}
		else if (archiveDestSettings.rbCloud.getValue()) 
		{
			if((archiveConfigModel == null) || (archiveConfigModel.getCloudConfigModel() == null))
			{
				isArchiveDestEmpty = true;
			}
			else if(archiveConfigModel.getCloudConfigModel()!=null)
			{
				ArchiveCloudDestInfoModel cloudConfigModel = archiveConfigModel.getCloudConfigModel();
				String cloudVendorURL = cloudConfigModel.getcloudVendorURL();
				if((cloudVendorURL==null)||(cloudVendorURL.length()==0)){
					isArchiveDestEmpty = true;
				}
			}
		}
		return !(isArchiveSourceEmpty&&isArchiveDestEmpty);
	}
	
	private boolean validateUI()
	{
		boolean bValidated = true;
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
		msgError.setModal(true);
		msgError.setMinWidth(400);
		
		//If user doesn't config the archive source and destination, we don't save the archive the settings.
//		boolean isArchiveSourceEmpty = false;
//		boolean isArchiveDestEmpty = false;
//		if(archiveSourceSettings.gridStore.getCount() == 0) {
//			isArchiveSourceEmpty = true;
//		}
//		if(archiveDestSettings.rbLocalDrive.getValue())
//		{
//			if((archiveDestSettings.strDownloadLocation == null) || (archiveDestSettings.strDownloadLocation.length() == 0))
//			{
//				isArchiveDestEmpty = true;
//			}
//		}
//		else if (archiveDestSettings.rbCloud.getValue()) 
//		{
//			if((archiveConfigModel == null) || (archiveConfigModel.getCloudConfigModel() == null))
//			{
//				isArchiveDestEmpty = true;
//			}
//			else if(archiveConfigModel.getCloudConfigModel()!=null)
//			{
//				ArchiveCloudDestInfoModel cloudConfigModel = archiveConfigModel.getCloudConfigModel();
//				String cloudVendorURL = cloudConfigModel.getcloudVendorURL();
//				if((cloudVendorURL==null)||(cloudVendorURL.length()==0)){
//					isArchiveDestEmpty = true;
//				}
//			}
//		}
//		if(isArchiveSourceEmpty&&isArchiveDestEmpty)
/*		if (!isArchiveSettingSet())
		{
			onValidatingCompleted(true);
			return true;
		}*/
		//end
		
		//validating the Schedules
		//madra04
		if(archiveSourceSettings.cbArchiveAfterBackup.getValue())
			{
				if(archiveSourceSettings.gridStore.getCount() == 0)
				{
					setDeckPanel(STACK_ARCHIVE_SOURCE);
					SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_SOURCE);
					onValidatingCompleted(false);
					String message = isArchiveTask ? UIContext.Messages.SelectCopyAndArchiveSourceErrorMessage()
							:UIContext.Messages.SelectArchiveSourceErrorMessage();
					msgError.setMessage(message);
					Utils.setMessageBoxDebugId(msgError);
					msgError.show();
					bValidated = false;
					return bValidated;
				}else{
					//check if source volumes contains refs or ntfs-dedup volume
					ListStore<ArchiveSourceInfoModel> sourceList = archiveSourceSettings.gridStore;
					BackupVolumeModel selectedRefs = getSelectedRefsVolumes();
					BackupVolumeModel selectedDedup = getSelectedDedupeVolumes();
					StringBuffer refsDedupVolumes = new StringBuffer();
					for(int i = 0 ; i < sourceList.getCount() ; i++){
						ArchiveSourceInfoModel sourceModel = sourceList.getAt(i);
						if(isRefsVolume(selectedRefs,sourceModel) || isDedupVolume(selectedDedup,sourceModel)){
							refsDedupVolumes.append(sourceModel.getDispalySourcePath() + ";");
						}
					}
					if(refsDedupVolumes.length()>0){
						setDeckPanel(STACK_ARCHIVE_SOURCE);
						SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_SOURCE);
						onValidatingCompleted(false);
						msgError.setMessage(UIContext.Messages.ArchiveSourceContainsRefsOrDedup(refsDedupVolumes.substring(0, refsDedupVolumes.length()-1)));
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
						bValidated = false;
						return bValidated;
					}
				}
				
				if(!isArchiveTask()){
					if(archiveSourceSettings.backupFreqRadio.getValue()){
						if( (archiveSourceSettings.backupFreqInputBox.getValue() == null) || (archiveSourceSettings.backupFreqInputBox.getValue() < 1) )
						{
							archiveSourceSettings.backupFreqInputBox.setValue(UIContext.minBackupsForArchiveJob);
							setDeckPanel(STACK_ARCHIVE_SOURCE);
							SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_SOURCE);
							onValidatingCompleted(false);
							msgError.setMinWidth(380);
							msgError.setMessage(UIContext.Messages.EnterValidNumberOfBackupsRangeMessage(UIContext.minBackupsForArchiveJob +" - "+UIContext.maxBackupsForArchiveJob));
							Utils.setMessageBoxDebugId(msgError);
							msgError.show();
							bValidated = false;
							msgError.setMinWidth(400);
							return bValidated;
						}

						if( (archiveSourceSettings.backupFreqInputBox.getValue() > UIContext.maxBackupsForArchiveJob))
						{
							archiveSourceSettings.backupFreqInputBox.setValue(UIContext.maxBackupsForArchiveJob);
							setDeckPanel(STACK_ARCHIVE_SOURCE);
							SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_SOURCE);
							onValidatingCompleted(false);
							msgError.setMessage(UIContext.Messages.EnterValidNumberOfBackupsRangeMessage(UIContext.minBackupsForArchiveJob +" - "+UIContext.maxBackupsForArchiveJob));
							msgError.show();
							bValidated = false;
							return bValidated;
						}
					}

					if(archiveSourceSettings.backupSchedRadio.getValue()){
						Boolean daily = archiveSourceSettings.dailyBackupCB.getValue();
						Boolean weekly = archiveSourceSettings.weeklyBackupCB.getValue();
						Boolean monthly = archiveSourceSettings.monthlyBackupCB.getValue();
						if((Boolean.FALSE.equals(daily)
								&& Boolean.FALSE.equals(weekly)
								&& Boolean.FALSE.equals(monthly))){
							setDeckPanel(STACK_ARCHIVE_SOURCE);
							SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_SOURCE);
							onValidatingCompleted(false);
							msgError.setMessage(UIContext.Messages.enterValidBackupSchedule());
							msgError.show();
							bValidated = false;
							return bValidated;
						}
						else{
							List<ScheduleItemModel> schedModels = this.backupContent.getAdvScheduleItem().getScheduleSettings().getScheduleModels();
							HashMap<Integer,  Boolean> scheduleTypeMap = new HashMap<>();
							for (ScheduleItemModel scheduleItemModel : schedModels) {
								if(scheduleItemModel.getScheduleType().equals(ScheduleTypeModel.OnceDailyBackup) 
										&& !scheduleTypeMap.containsKey(ScheduleTypeModel.OnceDailyBackup))
									scheduleTypeMap.put(ScheduleTypeModel.OnceDailyBackup, true);
								else if(scheduleItemModel.getScheduleType().equals(ScheduleTypeModel.OnceWeeklyBackup)
										&& !scheduleTypeMap.containsKey(ScheduleTypeModel.OnceWeeklyBackup))
									scheduleTypeMap.put(ScheduleTypeModel.OnceWeeklyBackup, true);
								else if(scheduleItemModel.getScheduleType().equals(ScheduleTypeModel.OnceMonthlyBackup)
										&& !scheduleTypeMap.containsKey(ScheduleTypeModel.OnceMonthlyBackup))
									scheduleTypeMap.put(ScheduleTypeModel.OnceMonthlyBackup, true);
							}

							String msg = UIContext.Messages.enterValidBackupSchedule();
							if(daily && !scheduleTypeMap.containsKey(ScheduleTypeModel.OnceDailyBackup)){
								bValidated = false;
								msg = UIContext.Messages.dailyScheduleNotConfiguredInBackup();
								archiveSourceSettings.dailyBackupCB.markInvalid(msg);
							}
							if(weekly && !scheduleTypeMap.containsKey(ScheduleTypeModel.OnceWeeklyBackup)){
								bValidated = false;
								msg = UIContext.Messages.weeklyScheduleNotConfiguredInBackup();;
								archiveSourceSettings.weeklyBackupCB.markInvalid(msg);
							}
							if(monthly && !scheduleTypeMap.containsKey(ScheduleTypeModel.OnceMonthlyBackup)){
								bValidated = false;
								msg = UIContext.Messages.monthlyScheduleNotConfiguredInBackup();;
								archiveSourceSettings.monthlyBackupCB.markInvalid(msg);
							}
							if(!bValidated){
								setDeckPanel(STACK_ARCHIVE_SOURCE);
								SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_SOURCE);
								onValidatingCompleted(false);
								msgError.setMessage(msg);
								msgError.show();
								return bValidated;
							}
						}
					}
				}
				if( !archiveScheduleSettings.validate() )
				{
					setDeckPanel(STACK_ARCHIVE_SCHEDULE);
					SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_SCHEDULE);
					onValidatingCompleted(false);
					msgError.setMinWidth(380);
					msgError.setMessage(UIContext.Constants.invalidSchedule());
					Utils.setMessageBoxDebugId(msgError);
					msgError.show();
					bValidated = false;
					msgError.setMinWidth(400);
					return bValidated;
				}
			
			
			boolean bLocalDriveSelected = false;
			
			
			if((isFileCopyToCloudEnabled && archiveDestSettings.rbLocalDrive.getValue()) || !isFileCopyToCloudEnabled)
			{
				if((archiveDestSettings.strDownloadLocation == null) || (archiveDestSettings.strDownloadLocation.length() == 0))
				{
					setDeckPanel(STACK_ARCHIVE_DESTINATION);
					SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
					onValidatingCompleted(false);
					msgError.setMessage(UIContext.Messages.EnterValidArchiveDestination());
					msgError.show();
					bValidated = false;
					archiveDestSettings.browseLocalOrNetworkDestinationPanel.getDestinationTextField().setCursorPos(0);
					return bValidated;
				}
				
				bLocalDriveSelected = true;
			}
			
			boolean bCloudSelected = false;
						
			if(isFileCopyToCloudEnabled)
			{	
				if(archiveDestSettings.rbCloud.getValue())
				{
					if((archiveConfigModel == null) || (archiveConfigModel.getCloudConfigModel() == null))
					{
						setDeckPanel(STACK_ARCHIVE_DESTINATION);
						SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
						onValidatingCompleted(false);
						msgError.setMessage(UIContext.Messages.ConfigureCouldErrorMessage());
						msgError.show();
						bValidated = false;
						return bValidated;
					}
					bCloudSelected = true;
				}
			}
			
			boolean cloudChosenAsDestination = false;
			if(isFileCopyToCloudEnabled)
			{
				cloudChosenAsDestination = archiveDestSettings.rbCloud.getValue();
			}
			
			if(!isFileCopyToCloudEnabled || archiveDestSettings.rbLocalDrive.getValue() || cloudChosenAsDestination)
			{
				if(archiveSourceSettings.gridStore.getCount() == 0)
				{
					setDeckPanel(STACK_ARCHIVE_SOURCE);
					SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_SOURCE);
					onValidatingCompleted(false);
					String message = isArchiveTask ? UIContext.Messages.SelectCopyAndArchiveSourceErrorMessage()
							:UIContext.Messages.SelectArchiveSourceErrorMessage();
					msgError.setMessage(message);
					msgError.show();
					bValidated = false;
					return bValidated;
				}			
			}
			
			if(archiveSourceSettings.gridStore.getCount() != 0)
			{
				if(!bLocalDriveSelected && !bCloudSelected)
				{
					setDeckPanel(STACK_ARCHIVE_DESTINATION);
					SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
					onValidatingCompleted(false);
					msgError.setMessage(UIContext.Messages.SelectDestinationToArchiveSourceMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
			}
			
			if(isArchiveTask()){
				//validate retention settings
				if((Integer.parseInt(archiveDestSettings.cbRetentionDays.getRawValue()) == 0) && (Integer.parseInt(archiveDestSettings.cbRetentionMons.getRawValue()) == 0) && (Integer.parseInt(archiveDestSettings.cbRetentionYrs.getRawValue()) == 0))
				{
					archiveDestSettings.cbRetentionDays.setSimpleValue("1");
					setDeckPanel(STACK_ARCHIVE_DESTINATION);
					SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
					onValidatingCompleted(false);
					msgError.setMessage(UIContext.Messages.MinimumRetentionTimeForFilesAtDestinationMessage(UIContext.minRetentionTime));
					msgError.show();
					bValidated = false;
					return bValidated;
				}
			}else{
				if(archiveDestSettings.versionValRadio.getValue()){
					//validate File Version settings
					if((archiveDestSettings.versionValField.getValue() == null) || (archiveDestSettings.versionValField.getValue().intValue() <= 0))
					{
						archiveDestSettings.versionValField.setValue(UIContext.minFileVersions);
						setDeckPanel(STACK_ARCHIVE_DESTINATION);
						SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
						onValidatingCompleted(false);
						msgError.setMessage(UIContext.Messages.MinimumNumberFileVersionsMessage(UIContext.minFileVersions));
						msgError.show();
						bValidated = false;
						return bValidated;
					}
					
					if(archiveDestSettings.versionValField.getValue().intValue() > 100)
					{
						archiveDestSettings.versionValField.setValue(UIContext.maxFileVersions);
						setDeckPanel(STACK_ARCHIVE_DESTINATION);
						SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
						onValidatingCompleted(false);
						msgError.setMessage(UIContext.Messages.MaximumNumberFileVersionsMessage(UIContext.maxFileVersions));
						msgError.show();
						bValidated = false;
						return bValidated;
					}
				}
				else{
					if(archiveDestSettings.retentionValField.getValue() != null && archiveDestSettings.retentionUnitCombo.getSelectedIndex() >= 0){
						Integer retentionVal = archiveDestSettings.retentionValField.getValue().intValue();
						switch(archiveDestSettings.retentionUnitCombo.getSelectedIndex()){
						case ArchiveDestinationSettings.DAY_INDEX:
							if(retentionVal < 0 || retentionVal > 31){
								setDeckPanel(STACK_ARCHIVE_DESTINATION);
								SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
								onValidatingCompleted(false);
								msgError.setMessage(UIContext.Constants.invalidFilesRetentionMsg());
								msgError.show();
								bValidated = false;
								return bValidated;
							}
						case ArchiveDestinationSettings.MONTH_INDEX:
							if(retentionVal < 0 || retentionVal > 12){
								setDeckPanel(STACK_ARCHIVE_DESTINATION);
								SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
								onValidatingCompleted(false);
								msgError.setMessage(UIContext.Constants.invalidFilesRetentionMsg());
								msgError.show();
								bValidated = false;
								return bValidated;
							}
						case ArchiveDestinationSettings.YEAR_INDEX:
							if(retentionVal < 0 || retentionVal > 100){
								setDeckPanel(STACK_ARCHIVE_DESTINATION);
								SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
								onValidatingCompleted(false);
								msgError.setMessage(UIContext.Constants.invalidFilesRetentionMsg());
								msgError.show();
								bValidated = false;
								return bValidated;
							}
						}
					}else{
						setDeckPanel(STACK_ARCHIVE_DESTINATION);
						SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
						onValidatingCompleted(false);
						msgError.setMessage(UIContext.Constants.invalidFilesRetentionMsg());
						msgError.show();
						bValidated = false;
						return bValidated;
					}
				}
				
				
			}
			
			
			if(archiveDestSettings.cbEnableEncryption.getValue())
			{
				String encryptKey = archiveDestSettings.EncryptionPassword.getValue();
				encryptKey = encryptKey == null ? "" : encryptKey;
				if(encryptKey.length() == 0)
				{
					setDeckPanel(STACK_ARCHIVE_DESTINATION);
					SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
					onValidatingCompleted(false);
					msgError.setMessage(UIContext.Messages.EnterEncryptionPasswordMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
				
				if(archiveDestSettings.ConfirmPassword.getValue() == null)
				{
					setDeckPanel(STACK_ARCHIVE_DESTINATION);
					SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
					onValidatingCompleted(false);
					msgError.setMessage(UIContext.Messages.EnterConfirmEncryptionPasswordMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
				
				if(encryptKey.compareTo(archiveDestSettings.ConfirmPassword.getValue()) != 0)
				{
					archiveDestSettings.EncryptionPassword.setValue("");
					archiveDestSettings.ConfirmPassword.setValue("");
					setDeckPanel(STACK_ARCHIVE_DESTINATION);
					SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
					onValidatingCompleted(false);
					msgError.setMessage(UIContext.Messages.EncryptionPasswordNotverifiedMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
			
				if(archiveDestSettings.EncryptionPassword.isMaxLengthExceeded())
				{
					archiveDestSettings.EncryptionPassword.setValue("");
					archiveDestSettings.ConfirmPassword.setValue("");
					setDeckPanel(STACK_ARCHIVE_DESTINATION);
					SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
					onValidatingCompleted(false);
					msgError.setMessage(UIContext.Constants.PasswordBeyondLength());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
			}
					
			boolean chkValidateDestination = false;
			if(isFileCopyToCloudEnabled)
			{
				if(archiveDestSettings.rbLocalDrive.getValue())
					chkValidateDestination = true;
			}
			else
			{
				chkValidateDestination = true;
			}
				
			
			
				if(chkValidateDestination)
				{
					if(!ValidateWhetherDestinationIsSource())
					{
						archiveDestSettings.browseLocalOrNetworkDestinationPanel.getDestinationTextField().setValue("");
						setDeckPanel(STACK_ARCHIVE_DESTINATION);
						SettingPresenter.getInstance().setCurrentIndex(rootTabId, STACK_ARCHIVE_DESTINATION);
						onValidatingCompleted(false);
						msgError.setMessage(UIContext.Constants.ArchiveDestinationCannotbeSourceMessage());
						msgError.show();
						bValidated = false;
						return bValidated;
					}
				}			
		}
		if (this.isForEdge){
			return validateUIForEdge();			
		}

		contentHost.increaseBusyCount();
		return validateBackend();
	}
	
	private boolean isRefsVolume(BackupVolumeModel selectedVolumes,ArchiveSourceInfoModel sourceModel){
		if(selectedVolumes == null){
			return false;
		}
		return isExistVolume(selectedVolumes.allRefsVolumesList,sourceModel.getSourcePath());
	}
	
	private boolean isDedupVolume(BackupVolumeModel selectedVolumes,ArchiveSourceInfoModel sourceModel){
		if(selectedVolumes == null){
			return false;
		}
		return isExistVolume(selectedVolumes.allDedupeVolumesList,sourceModel.getSourcePath());
	}
	
	private boolean isExistVolume(List<String> volumeList,String sourceVolume){
		if(volumeList == null || volumeList.size()==0){
			return false;
		}
		for(String refsvolume: volumeList){
			if(sourceVolume.contains(refsvolume)){
				return true;
			}
		}
		return false;
	}
	
	public void popupUserPasswordWindow() {
		final UserPasswordWindow dlg = new UserPasswordWindow(archiveConfigModel.getArchiveToDrivePath(), "", "");
		dlg.setMode(PathSelectionPanel.ARCHIVE_MODE);
		dlg.setModal(true);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we) {
				if (dlg.getCancelled() == false)
				{
					String username = dlg.getUsername();
					String password = dlg.getPassword();
					archiveConfigModel.setDestinationPathUserName(username);
					archiveConfigModel.setDestinationPathPassword(password);
					archiveDestSettings.getPathSelectionPanel().setUsername(username);
					archiveDestSettings.getPathSelectionPanel().setPassword(password);
					//SaveArchiveConfig();
					validateBackend();
				}
				else {
					contentHost.decreaseBusyCount();
					thisWindow.onValidatingCompleted(false);
				}
			}
		});
		dlg.show();
	
	}
	
	private boolean validateBackend() {
		return fileCopyPresenter.validate();
//		this.Save();
//		
//		Broker.loginService.validateArchiveConfiguration(archiveConfigModel, new BaseAsyncCallback<Long>() {
//
//				@Override
//				public void onSuccess(Long result) {
//					contentHost.decreaseBusyCount();
//					thisWindow.onValidatingCompleted(true);
//					//thisWindow.Save();
//					//UIContext.d2dHomepagePanel.refreshProtectionSummary(null);
//				}
//				
//				@Override
//				public void onFailure(Throwable caught) {
//									
//					if(caught instanceof BusinessLogicException 
//							&& ERR_REMOTE_DEST_WINSYSMSG.equals(((BusinessLogicException)caught).getErrorCode()))
//						{
//							final Throwable orginialExc = caught;
//							CommonServiceAsync commonService = GWT.create(CommonService.class);
//							commonService.getDestDriveType(archiveConfigModel.getArchiveToDrivePath(), new BaseAsyncCallback<Long>()
//						    {
//								@Override
//								public void onFailure(Throwable caught) {
//									contentHost.decreaseBusyCount();
//									thisWindow.onValidatingCompleted(false);
//									super.onFailure(orginialExc);
//								}
//							
//								@Override
//								public void onSuccess(Long result) {
//									if(result == PathSelectionPanel.REMOTE_DRIVE )
//								    {
//										popupUserPasswordWindow();
//								    }
//								    else {
//								    	archiveDestSettings.browseLocalOrNetworkDestinationPanel.setDestination("");
//								    	contentHost.decreaseBusyCount();
//										thisWindow.onValidatingCompleted(false);
//								    	super.onFailure(orginialExc);
//								   	}
//								}
//				    	}
//					);
//					}
//						else if(caught instanceof BusinessLogicException 
//								&& INVALID_ARCHIVE_SOURCEFOUND.equals(((BusinessLogicException)caught).getErrorCode()))
//						{
//							setDeckPanel(STACK_ARCHIVE_SOURCE);
//							contentHost.decreaseBusyCount();
//							thisWindow.onValidatingCompleted(false);
//						    super.onFailure(caught);
//						}
//						else if (caught instanceof BusinessLogicException && "4294967295".equals(((BusinessLogicException)caught).getErrorCode()) ) {
//							
//							MessageBox msgError = new MessageBox();
//							msgError.setIcon(MessageBox.ERROR);
//							msgError.setTitleHtml(strErrorMessageTitle);
//							msgError.setModal(true);
//							msgError.setMinWidth(400);
//							msgError.setMessage(((BusinessLogicException)caught).getDisplayMessage());		
//							Utils.setMessageBoxDebugId(msgError);
//							msgError.show();
//							
//							contentHost.decreaseBusyCount();
//							thisWindow.onValidatingCompleted(false);
////							super.onFailure(caught);
//						}
//						else {
//							// Issue: 20231648    Title: FOCUS AFTER ENCRYPTION LIC ERR
//							// Go to destination panel for Encryption License error. 
//							if(caught instanceof BusinessLogicException 
//								&& "4294967310".equals(((BusinessLogicException)caught).getErrorCode()))
//							{
//								setDeckPanel(STACK_ARCHIVE_DESTINATION);
//							}
//							
//							contentHost.decreaseBusyCount();
//							thisWindow.onValidatingCompleted(false);
//						    super.onFailure(caught);
//						}				
//				}
//
//				
//			});
//			
//			return true;
	}
	
	private boolean ValidateWhetherDestinationIsSource() {
		int iarchiveSourcesCount = archiveSourceSettings.gridStore.getCount();
		String strDestination = archiveDestSettings.browseLocalOrNetworkDestinationPanel.getDestination();
		
		if(strDestination == null || strDestination.length() == 0)
			return false;
		
		int iIndex = strDestination.indexOf(":");
		String strDestVolume = ""; 
		if(iIndex != -1)
		{
			strDestVolume = strDestination.substring(0, iIndex + 1);
		}
		
		for(int iSourceIndex = 0;iSourceIndex < iarchiveSourcesCount;iSourceIndex++)
		{
			String strArchiveSource = archiveSourceSettings.gridStore.getAt(iSourceIndex).getSourcePath();
			int iArchiveSourceColonIndex = strArchiveSource.indexOf(":");
			strArchiveSource =  strArchiveSource.substring(0,iArchiveSourceColonIndex+1);
			if(strArchiveSource.compareToIgnoreCase(strDestVolume) == 0)
			{
				return false;
			}
		}
		return true;
	}

	private void setDeckPanel(int in_iDeckPanelNum)
	{
		archiveDeckPanel.showWidget(in_iDeckPanelNum);
		
		tbArchiveDestinationButton.setDown(false);
		tbArchiveDestinationLabel.setDown(false);
		
		tbArchiveScheduleButton.setDown(false);
		tbArchiveScheduleLabel.setDown(false);
		
		tbArchiveSourceButton.setDown(false);		
		tbArchiveSourceLabel.setDown(false);
		
		//tbArchiveAdvancedButton.setDown(false);
		//tbArchiveAdvancedLabel.setDown(false);
		
		switch(in_iDeckPanelNum)
		{
		case STACK_ARCHIVE_SOURCE:
			setHeading(UIContext.Messages.archiveSettingsWindowWithTap(UIContext.Constants.ArchiveSource()));
			tbArchiveSourceButton.setDown(true);		
			tbArchiveSourceLabel.setDown(true);
			break;
		case STACK_ARCHIVE_DESTINATION:
			setHeading(UIContext.Messages.archiveSettingsWindowWithTap(UIContext.Constants.ArchiveDestination()));
			tbArchiveDestinationButton.setDown(true);
			tbArchiveDestinationLabel.setDown(true);
			break;
		case STACK_ARCHIVE_SCHEDULE:
			setHeading(UIContext.Messages.archiveSettingsWindowWithTap(UIContext.Constants.ArchiveSchedule()));
			tbArchiveScheduleButton.setDown(true);
			tbArchiveScheduleLabel.setDown(true);
			break;
		//case STACK_ARCHIVE_ADVANCED:
			//setHeading(UIContext.Messages.archiveSettingsWindowWithTap(UIContext.Constants.ArchiveAdvanced()));
			//tbArchiveAdvancedButton.setDown(true);
			//tbArchiveAdvancedLabel.setDown(true);
			//break;
		}
	}

	public boolean Save()
	{
		//saving archive sources information
		int iarchiveSourcesCount = archiveSourceSettings.gridStore.getCount();
		ArchiveSourceInfoModel[] archivesourcesList = new ArchiveSourceInfoModel[iarchiveSourcesCount];
		
		boolean bSourcesAdded = false;
		for(int iarchiveIndex = 0;iarchiveIndex < iarchiveSourcesCount;iarchiveIndex++)
		{
			ArchiveSourceInfoModel sourceInfoModel = archiveSourceSettings.gridStore.getAt(iarchiveIndex);
			
			archivesourcesList[iarchiveIndex] = new ArchiveSourceInfoModel();
			archivesourcesList[iarchiveIndex].setSourcePath(sourceInfoModel.getSourcePath());
			archivesourcesList[iarchiveIndex].setDisplaySourcePath(sourceInfoModel.getDispalySourcePath());
			archivesourcesList[iarchiveIndex].setArchiveFiles(sourceInfoModel.getArchiveFiles());
			archivesourcesList[iarchiveIndex].setCopyFiles(sourceInfoModel.getCopyFiles());
			archivesourcesList[iarchiveIndex].setArchiveSourceFilters(sourceInfoModel.getArchiveSourceFilters());
			//archivesourcesList[iarchiveIndex].setArchiveSourceCriterias(sourceInfoModel.getArchiveSourceCriterias());
			bSourcesAdded = true;
		}
		
//		if(bSourcesAdded)
//		{
			if(archiveConfigModel == null)
			{
				archiveConfigModel = new ArchiveSettingsModel();
			}
			archiveConfigModel.setArchiveSources(archivesourcesList);
		
			/*//global options
			archiveConfigModel.setExcludeSystemFiles(archiveSourceSettings.cbArchiveExcludeSystemFiles.getValue());
			archiveConfigModel.setExcludeAppFiles(archiveSourceSettings.cbArchiveExcludeApplicationFiles.getValue());
			*/
			//saving schedule
			//madra04
			archiveConfigModel.setArchiveAfterBackup(archiveSourceSettings.cbArchiveAfterBackup.getValue());
			if(!isArchiveTask()){
				if(archiveSourceSettings.backupFreqRadio.getValue()){
					archiveConfigModel.setBackupFrequency(true);
					archiveConfigModel.setBackupSchedule(false);
					archiveConfigModel.setArchiveAfterNumberofBackups(archiveSourceSettings.backupFreqInputBox.getValue());
				}
				else if(archiveSourceSettings.backupSchedRadio.getValue()){
					archiveConfigModel.setBackupFrequency(false);
					archiveConfigModel.setBackupSchedule(true);
					archiveConfigModel.setDaily(archiveSourceSettings.dailyBackupCB.getValue());
					archiveConfigModel.setWeekly(archiveSourceSettings.weeklyBackupCB.getValue());
					archiveConfigModel.setMonthly(archiveSourceSettings.monthlyBackupCB.getValue());
				}
			}
			archiveConfigModel.setAdvanceSchedule(archiveScheduleSettings.getScheduleData());
			
			archiveConfigModel.setPurgeScheduleAvailable(bPurgeScheduleAvailable);
			archiveConfigModel.setPurgeArchiveItems(bEnablePurge);
			archiveConfigModel.setPurgeAfterDays(iPurgeAfterNDays);
			archiveConfigModel.setPurgeStartTime(lPurgeStartTime);
		/*		if(cbPurgeEveryNDays.getValue() != null ? cbPurgeEveryNDays.getValue() : false)
			{
				archiveConfigModel.setPurgeArchiveItems(cbPurgeEveryNDays.getValue());
				archiveConfigModel.setPurgeAfterDays(nfDays.getValue().intValue());
				archiveConfigModel.setPurgeStartTime(purgeStartTimeContainer.getStartDateTime().getTime());
			}*/
			//saving destination information
			if((isFileCopyToCloudEnabled && archiveDestSettings.rbLocalDrive.getValue())|| !isFileCopyToCloudEnabled)
			{
				archiveConfigModel.setArchiveToDrive(true);
				archiveConfigModel.setArchiveToDrivePath(archiveDestSettings.browseLocalOrNetworkDestinationPanel.getDestination());
				if(archiveDestSettings.browseLocalOrNetworkDestinationPanel.isLocalPath() != true)
				{
					String destination=archiveDestSettings.browseLocalOrNetworkDestinationPanel.getDestination();
					String[] info= Utils.getConnectionInfo(destination);
					if(info!=null){
						//domain=info[0];
						archiveConfigModel.setDestinationPathUserName(info[1]);
						archiveConfigModel.setDestinationPathPassword(info[2]);
					}
					else{
					archiveConfigModel.setDestinationPathUserName(archiveDestSettings.browseLocalOrNetworkDestinationPanel.getUsername());
					archiveConfigModel.setDestinationPathPassword(archiveDestSettings.browseLocalOrNetworkDestinationPanel.getPassword());
					}
				}
				else
				{
					archiveConfigModel.setDestinationPathUserName("");
					archiveConfigModel.setDestinationPathPassword("");
				}
			}
			else
			{
				archiveConfigModel.setArchiveToDrive(false);
				archiveConfigModel.setArchiveToDrivePath("");
			}
			
			
			
			
			if( isFileCopyToCloudEnabled && archiveDestSettings.rbCloud.getValue())
			{
				archiveConfigModel.setArchiveToCloud(true);
				//cloud configuration is already set.
			}
			else
			{
				archiveConfigModel.setArchiveToCloud(false);
				archiveConfigModel.setCloudConfigModel(null);
			}
			
			
			if(isArchiveTask()){
				String strRetentionTime = "";
				strRetentionTime = archiveDestSettings.cbRetentionMons.getSimpleValue() + "\\" + archiveDestSettings.cbRetentionDays.getSimpleValue() + "\\" + archiveDestSettings.cbRetentionYrs.getSimpleValue();

				archiveConfigModel.setRetentiontime(strRetentionTime);
			}else{
				if(archiveDestSettings.versionValRadio.getValue()){
					Number fileVersionCount = archiveDestSettings.versionValField.getValue();
					if(fileVersionCount != null){
						archiveConfigModel.setArchivedFileVersionsRetentionCount(fileVersionCount.intValue());
						archiveConfigModel.setFilesRetentionTime("");
					}
				}else{

					int day = 0;
					int month = 0;
					int year = 0;

					if(archiveDestSettings.retentionUnitCombo.getSelectedIndex() == ArchiveDestinationSettings.DAY_INDEX)
						day = archiveDestSettings.retentionValField.getValue().intValue();
					else if(archiveDestSettings.retentionUnitCombo.getSelectedIndex() == ArchiveDestinationSettings.MONTH_INDEX)
						month = archiveDestSettings.retentionValField.getValue().intValue();
					else if(archiveDestSettings.retentionUnitCombo.getSelectedIndex() == ArchiveDestinationSettings.YEAR_INDEX)
						year = archiveDestSettings.retentionValField.getValue().intValue();
					String strRetentionTime = month + "\\" + day + "\\" + year;

					archiveConfigModel.setFilesRetentionTime(strRetentionTime);
				}
			}
			
			int compressionLevel = archiveDestSettings.getCompressionLevel();
			if(compressionLevel != -1){
				archiveConfigModel.setCompressionLevel(compressionLevel);
			}
			
			archiveConfigModel.setEncryption(archiveDestSettings.cbEnableEncryption.getValue());
			if(archiveDestSettings.cbEnableEncryption.getValue())
				archiveConfigModel.setEncryptionPassword(archiveDestSettings.EncryptionPassword.getValue());
			//archiveConfigModel.setSpaceUtilizationValue(archiveDestSettings.sArchivePerformanceSlider.getValue());
			
			//settings backup volumes
			archiveConfigModel.setbackupVolumes(getSelectedBackupVolumes());
			archiveConfigModel.setBackupDestination(this.getbackupDestination());
//		}

		return true;
	}
	
	public void enableEditing( boolean isEnabled )
	{
		this.archiveSourceSettings.setEditable(isEnabled);
		this.archiveDestSettings.setEditable(isEnabled);
		this.archiveScheduleSettings.setEditable(isEnabled);
		//this.lcArchiveAdvancedContainer.setEnabled(isEnabled);
		
	}
	//////////////////////////////////////////////////////////////////////////
	//
	//  ADDED FOR EDGE
	//
	//////////////////////////////////////////////////////////////////////////

	private boolean isForEdge = false;
	private int settingsContentId = -1;
	protected ISettingsContentHost contentHost;
	
	public ISettingsContentHost getContentHost() {
		return contentHost;
	}

	@Override
	public void initialize( ISettingsContentHost contentHost, boolean isForEdge )
	{
		this.contentHost = contentHost;
		this.isForEdge = isForEdge;	
		this.doInitialization();
		
//		if (!this.isForEdge)
//			disableEditingIfUsingEdgePolicy();
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
		ArchiveSettingsModel model = null;
		if(isArchiveTask())
			model = SettingPresenter.getInstance().getD2dSettings().getFileArchiveSettingsModel();
		else
			model = SettingPresenter.getInstance().getD2dSettings().getArchiveSettingsModel();
		if(model != null){
			archiveSettingFileExist_b = true;
			thisWindow.archiveConfigModel = model;
		}
		thisWindow.RefreshData();
		onLoadingCompleted(true);
	}

	@Override
	public void loadDefaultData()
	{
		onLoadingCompleted(true);
	}

	@Override
	public void saveData()
	{
		if(isArchiveTask())
			SettingPresenter.getInstance().getD2dSettings().setFileArchiveSettingsModel(archiveConfigModel);
		else
			SettingPresenter.getInstance().getD2dSettings().setArchiveSettingsModel(archiveConfigModel);
		this.onSavingCompleted(true);
//		SaveSettings();
	}
	
	@Override
	public void validate()
	{
		if(!validateUI())
			repaint();
	}
	
	@Override
	public void setDefaultEmail(IEmailConfigModel iEmailConfigModel)
	{
		
	}
	
	protected void onLoadingCompleted( boolean isSuccessful )
	{
		GWT.log("The archivesettingcontent load compelte:"+isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.LoadData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}
	
	public void onValidatingCompleted( boolean isSuccessful )
	{
		GWT.log("The archivesettingcontent validate compelte:"+isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.Validate,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}
	
	protected void onSavingCompleted( boolean isSuccessful )
	{
		GWT.log("The archivesettingcontent save compelte:"+isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.SaveData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}
	
	protected void setHeading(String title) {
		if(contentHost!=null) {
			contentHost.setCaption(title);
		}
	}

	/*protected boolean validatePath() {
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
		msgError.setModal(true);
		msgError.setMinWidth(400);
      
		if (!isDestinationChanged()) {
			archiveDestSettings.cbEnableEncryption
					.setValue(archiveConfigModel.getEncryption());
			if (archiveConfigModel.getEncryption()) {
				archiveDestSettings.EncryptionPassword
						.setValue(archiveConfigModel.getEncryptionPassword());
				archiveDestSettings.ConfirmPassword
						.setValue(archiveConfigModel.getEncryptionPassword());
			}
			msgError.setMessage(UIContext.Constants
					.messageBoxArchiveEncrypEror());
			msgError.show();
			return false;
		}
		return true;
	}


	protected  boolean isDestinationChanged() {
		if (archiveConfigModel != null && archiveDestSettings != null
				&& archiveConfigModel.getArchiveToDrive()
				&& archiveDestSettings.rbLocalDrive.getValue()) {
			if (archiveConfigModel != null
					&& archiveConfigModel
							.getArchiveToDrivePath()
							.equals(
									archiveDestSettings.browseLocalOrNetworkDestinationPanel
											.getDestination()))
				return false;
		}

		else if (archiveConfigModel != null && archiveDestSettings != null
				&& archiveConfigModel.getArchiveToCloud()
				&& archiveDestSettings.rbCloud.getValue()) {
			if (archiveConfigModel != null
					&& archiveConfigModel.getCloudConfigModel()
							.getcloudBucketName().equals(cloudBucket))
				return false;
		}
		return true;
	}*/
	
	public BackupVolumeModel getSelectedBackupVolumes()
	{
		return ((BackupDestinationSettings)backupContent.getDestination()).getBackupVolumes();
	}
	
	public BackupVolumeModel getSelectedRefsVolumes()
	{
		if( this.isForEdge ){   //resolve issue 141113. if run this code from edge , backupContent.destination is D2DDestinationSettingsForEdge
			return null;
		}
		else{
			return ((D2DDestinationSettings)backupContent.getDestination()).getSelectedRefsVolumes();
		}	
	}
	
	public BackupVolumeModel getSelectedDedupeVolumes()
	{
		if( this.isForEdge ){ //resolve issue 141113. if run this code from edge , backupContent.destination is D2DDestinationSettingsForEdge
			return null;
		}
		else{
			return ((D2DDestinationSettings)backupContent.getDestination()).getSelectedNtfsDedupeVolumes();
		}	
	}


	public String getbackupDestination()
	{
		return backupContent.getDestination().GetBackupDestination();
	}

	public List<FileModel> getFilterModels() {
		return ((BackupDestinationSettings)backupContent.getDestination()).getFilterBackupVolumesForFullMachine();
	}
	//Return all refs and ntfs dedup volumes, added by wanqi06
	public List<FileModel> getRefsNtfsModels() {
		return ((BackupDestinationSettings)backupContent.getDestination()).getRefsNtfsVolumesForFullMachine();
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
