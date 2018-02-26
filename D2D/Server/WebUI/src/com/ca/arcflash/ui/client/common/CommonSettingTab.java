package com.ca.arcflash.ui.client.common;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ArchiveSettingsContent;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.coldstandby.VCMMessages;
import com.ca.arcflash.ui.client.coldstandby.edge.setting.VCMSettingsContent;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.export.ScheduledExportSettingsContent;
import com.ca.arcflash.ui.client.homepage.PreferencesSettingsContent;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.D2DSettingModel;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.ca.arcflash.ui.client.vsphere.setting.VSphereBackupSettingContent;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class CommonSettingTab extends LayoutContainer implements ISettingsContentHost
{
	final LoginServiceAsync service = GWT.create(LoginService.class);
	/*private final int tabIndexBackUp = 1;
	private final int tabIndexArchive = 2;
	private final int tabIndexPreference = 3;
	private final int tabIndexExport = 4;*/
	
	public static int presentTabSelectionIndex = 0;
	// d2d setting tab id
	public static final int d2dBackupSettingID = 1;
	public static final int d2dPreferenceSettingID = 2;
	public static final int archiveSettingID = 3;
	public static final int fileArchiveSettingID = 5;
	public static final int scheduledExportSettingsID = 4;
	// vcm setting tab id
	public static final int vcmSettingID = 10;
	public static  final int vcmPreferenceID = 11;
	// vsphere setting tab id
	public static  final int vsphereSettingID = 20;
	public static  final int vspherePreferenceID = 21;
	// rps setting tab id
	public static  final int rpsSettingID = 30;
	public static  final int rpsPreferenceID = 31;

	protected AppType appType;
	private TabPanel tabPanel;
	protected List<SettingsContentEntry> settingsContentList;
	private Queue<ISettingsContent> saveQueue;
	private Queue<ISettingsContent> validateQueue;
	
	
	private int loadCount;

	private ISettingsContentHost        contentHost;
	private final  String				tabIndex = "tabIndex";
	protected boolean						isForEdge;
	private CommonSettingTab			outThis;
	
	private boolean 					isShowArchive = true;
	private boolean						isD2DSaved = false;
	
	private int 						settingsContentId = -1;	
	private D2DSettingModel             d2dSettings;
	private boolean                     d2dUsingEdgePolicy = false;
	
	public CommonSettingTab(AppType appType, boolean isForEdge, ISettingsContentHost contentHost)
	{
		this.appType = appType;
		this.isForEdge = isForEdge;
		this.contentHost = contentHost;
		this.settingsContentList	= new ArrayList<SettingsContentEntry>();
		this.saveQueue				= new LinkedList<ISettingsContent>();
		this.validateQueue			= new LinkedList<ISettingsContent>();
		this.loadCount				= 1;
		this.outThis 				= this;
		
		this.setLayout( new FitLayout() );
		
		LayoutContainer panel = new LayoutContainer();
		panel.setLayout( new RowLayout( Orientation.VERTICAL ) );
		this.tabPanel = new TabPanel();
		tabPanel.setTabScroll(true);
		this.tabPanel.ensureDebugId( "{A8B02ED7-A5CE-421a-9444-0FE71DB219FC}" );
		this.tabPanel.setDeferredRender( false );
		panel.add( this.tabPanel, new RowData( 1, 1, new Margins( 0, 0, 0, 0 ) ) );
        this.tabPanel.addListener(Events.Select, new Listener<TabPanelEvent>(){
        	public void handleEvent(TabPanelEvent be) {        		        		
        		Integer tabIndex = be.getItem().getData(outThis.tabIndex);
        		presentTabSelectionIndex = tabIndex;
        		
        	} });
		
		this.add(panel);
		
		initSettingsContentList();
	}
	
	@Override
	public boolean isForCreate() {
		return contentHost.isForCreate();
	}
	
	public void setId( int settingsContentId )
	{
		this.settingsContentId = settingsContentId;
	}
	
	private void initSettingsContentList()
	{
		initHostTitle();
		initContentList();
		
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			ISettingsContent settingsContent = contentEntry.getContentObject();
			settingsContent.initialize( this, this.isForEdge );
			settingsContent.setId( contentEntry.getId() );
			
			TabItem contentTab = new TabItem( contentEntry.getDisplayName() );
			contentTab.ensureDebugId("6BFF8524-4177-418b-8295-6B8234760DE9" + contentEntry.getId());
			contentTab.setIcon( contentEntry.getTabIcon());
			contentTab.setLayout( new FitLayout() );
			contentTab.add( settingsContent.getWidget() );

			contentTab.setData(tabIndex, new Integer(contentEntry.getId()));
			contentEntry.setTabItem( contentTab );
			this.tabPanel.add( contentTab );
		}
		
		this.loadCount = settingsContentList.size();
		this.tabPanel.setSelection( this.tabPanel.getItem( 0 ) );
		
	}

	protected void initContentList() {
		switch (appType)
		{
		case D2D:
			ISettingsContent d2dSettingsContent = new BackupSettingsContent();
			String d2dSettingTab = UIContext.Constants.backupSettingsWindow();
			settingsContentList.add(new SettingsContentEntry(d2dBackupSettingID, d2dSettingTab, d2dSettingsContent, d2dBackupSettingID, AbstractImagePrototype.create(UIContext.IconBundle.d2d_backup_settings())));
			
			ISettingsContent archiveSettingContent = new ArchiveSettingsContent((BackupSettingsContent)d2dSettingsContent, false);
			String archiveSettingTab = UIContext.Constants.homepageTasksArchiveSettingLabel();
			settingsContentList.add(new SettingsContentEntry(archiveSettingID, archiveSettingTab, archiveSettingContent, archiveSettingID, AbstractImagePrototype.create(UIContext.IconBundle.d2d_filecopy_settings())));
			
			ISettingsContent fileArchiveSettingContent = new ArchiveSettingsContent((BackupSettingsContent)d2dSettingsContent, true);
			String fileArchiveSettingTab = UIContext.Constants.homepageTasksFileArchiveSettingLabel();
			settingsContentList.add(new SettingsContentEntry(fileArchiveSettingID, fileArchiveSettingTab, fileArchiveSettingContent, fileArchiveSettingID, AbstractImagePrototype.create(UIContext.IconBundle.d2d_filecopy_settings())));
			
			ISettingsContent scheduledExportSettingsContent = new ScheduledExportSettingsContent();
			String scheduledExportSettingsTab = UIContext.Constants.scheduledExportSettings();
			settingsContentList.add(new SettingsContentEntry(scheduledExportSettingsID, scheduledExportSettingsTab, scheduledExportSettingsContent, scheduledExportSettingsID, AbstractImagePrototype.create(UIContext.IconBundle.d2d_reconverypoints_settings())));
			
			ISettingsContent preferenceconContent = new PreferencesSettingsContent();
			String d2dPrefrenceTab = UIContext.Constants.preferences();
			settingsContentList.add(new SettingsContentEntry(d2dPreferenceSettingID, d2dPrefrenceTab,preferenceconContent , d2dPreferenceSettingID, AbstractImagePrototype.create(UIContext.IconBundle.d2d_preference_settings())));
			
			Utils.connectionCache = new HashMap<String, String[]>();
			break;
			
		case VCM://vcm
			ISettingsContent vcmSettingContent = new VCMSettingsContent();
			String virtualConversionTab = UIContext.Constants.virtualStandyNameTranslate();
			settingsContentList.add(new SettingsContentEntry(vcmSettingID, virtualConversionTab, vcmSettingContent,vcmSettingID, AbstractImagePrototype.create(UIContext.IconBundle.vcm_virtualstandby_settings())));
			
			ISettingsContent vcmPreference = new CommonPreferenceSettings(AppType.VCM);
			String prefrenceTab = UIContext.Constants.preferences();
			settingsContentList.add(new SettingsContentEntry(vcmPreferenceID, prefrenceTab, vcmPreference, vcmPreferenceID, AbstractImagePrototype.create(UIContext.IconBundle.vcm_preference_settings())));
			break;
		
		case VSPHERE:
			ISettingsContent vsphereSettingsContent = new  VSphereBackupSettingContent();
			String vsphereTab =  UIContext.Constants.backupSettingsWindow();
			settingsContentList.add(new SettingsContentEntry(vsphereSettingID, vsphereTab, vsphereSettingsContent, vsphereSettingID, AbstractImagePrototype.create(UIContext.IconBundle.vsphere_backup_settings())));
			
			ISettingsContent vspherePreference = new CommonPreferenceSettings(AppType.VSPHERE);
			String vspherePrefrenceTab = UIContext.Constants.preferences();
			settingsContentList.add(new SettingsContentEntry(vspherePreferenceID, vspherePrefrenceTab, vspherePreference, vspherePreferenceID, AbstractImagePrototype.create(UIContext.IconBundle.vsphere_preference_settings())));
			break;
			
		case RPS:
			throw new RuntimeException("should use RpsCommonSettingTab");
		case RPSCM:
			throw new RuntimeException("should use RpsCommonSettingTab");
		}
	}
	
	private void initD2DSettings() {
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{	
			contentHost.increaseBusyCount();
			ISettingsContent settingsContent = contentEntry.getContentObject();
			settingsContent.loadData();
		}
		
	}

	protected ISettingsContent getSettingContentByID(int settingsId) {
		for(SettingsContentEntry entry : settingsContentList) {
			if(entry.getId() == settingsId){
				return entry.getContentObject();
			}
		}
		return null;
	}
	
	protected void enableEdit(int settingsId) {
		ISettingsContent content = getSettingContentByID(settingsId);
		if(content == null) return;
		
//		if(settingsId==rpsSettingID){
//			((RpsBackupSettingContent)content).enableEditing(this.isForEdge);
//		}
		if(settingsId == vsphereSettingID)
			((VSphereBackupSettingContent)content).enableEditing(this.isForEdge);
		if(d2dUsingEdgePolicy && !this.isForEdge)
			switch(settingsId){
			case archiveSettingID:
				((ArchiveSettingsContent)content).enableEditing(false);
				break;
			case d2dBackupSettingID:
				((BackupSettingsContent)content).enableEditing(false);
				break;
			case d2dPreferenceSettingID:
				((PreferencesSettingsContent)content).enableEditing(false);
				break;
			case scheduledExportSettingsID:
				((ScheduledExportSettingsContent)content).enableEditing(false);
				break;
				
				default:
					break;
			}
	}
	public void loadSetting()
	{
		if(appType == AppType.D2D) {
			try {
				contentHost.increaseBusyCount(UIContext.Constants.settingsLoadingConfigMaskText());
				service.getD2DConfiguration(new BaseAsyncCallback<D2DSettingModel>() {
					public void onFailure(Throwable caught) {
						contentHost.decreaseBusyCount();
						onLoadingCompleted(false);
						super.onFailure(caught);
						
					}
					@Override
					public void onSuccess(D2DSettingModel result) {
						d2dSettings = result;
						service.isUsingEdgePolicySettings(
								SettingsTypesForUI.BackupSettings, new AsyncCallback<Boolean>()
							{
								@Override
								public void onFailure( Throwable caught )
								{
									contentHost.decreaseBusyCount();
									initD2DSettings();
								}

								@Override
								public void onSuccess( Boolean result )
								{
									contentHost.decreaseBusyCount();
									d2dUsingEdgePolicy = result;
									initD2DSettings();
								}
							});
						
						
					}
				});
			
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		else if(appType == AppType.RPSCM) {
			d2dUsingEdgePolicy = true;
			initD2DSettings();
		}
		else {
			initD2DSettings();
		}

	}
	
	public void loadDefaultSetting() {
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			contentHost.increaseBusyCount(UIContext.Constants.settingsLoadingConfigMaskText());
			ISettingsContent settingsContent = contentEntry.getContentObject();
			settingsContent.loadDefaultData();
		}
	}
	
	public void validateSettting() {
		validateQueue.clear();
		
		int currentEntryID = -1;
		/*Integer currentEntryInteger= (Integer)tabPanel.getSelectedItem().getData(key);
		if(currentEntryInteger != null) {
			currentEntryID = currentEntryInteger.intValue();
		}*/
		
		//Step 1: validate the current selected page
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if(contentEntry.getId() == currentEntryID) {
				ISettingsContent settingsContent = contentEntry.getContentObject();
				validateQueue.add(settingsContent);
			}
		}
		//Step 2: validate the other pages
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if(contentEntry.getId() != currentEntryID) {
				ISettingsContent settingsContent = contentEntry.getContentObject();
				validateQueue.add(settingsContent);
			}
		}
		
		validateItem();
	}
	
	private void validateItem() {
		contentHost.increaseBusyCount();
		ISettingsContent settingsContent = this.validateQueue.poll();
		settingsContent.validate();
	}
	
	private void saveSetting() {
		switch (appType) {
		case D2D:
			saveD2DSettings();
			break;
			
		case VCM:
			saveVCMSettings();
			break;
		
		case VSPHERE:
			saveVsphereSettings();
			break;
			
		case RPS:
			//saveRpsSettings();
			onValidatingCompleted(true);
			break;
			
		case RPSCM:
			saveRpsSettings();
			break;
			
			
		default:
			break;
		}
		
	}

	private void saveD2DSettings() {
		isD2DSaved = false;
		saveQueue.clear();
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			ISettingsContent settingsContent = contentEntry.getContentObject();
			this.saveQueue.add(settingsContent);
		}
		
		this.saveItem();
	}
	
	private BackupSettingsContent getBackupSettingContent() {
		BackupSettingsContent backupSettingsContent = null;
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if(contentEntry.getId() == d2dBackupSettingID) {
				backupSettingsContent = (BackupSettingsContent)contentEntry.getContentObject();
				break;
			}
		}
		return backupSettingsContent;
	}
	
	private void saveD2DConfiguration() {
		increaseBusyCount(UIContext.Constants.settingsMaskText());
		final BackupSettingsContent backupSetting = getBackupSettingContent();
		isD2DSaved = true;
		service.saveD2DConfiguration(d2dSettings, new BaseAsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				if (isForEdge) {
					decreaseBusyCount();
					onSavingCompleted(false);
				} else {
					if (caught instanceof BusinessLogicException
							&& backupSetting.ERR_REMOTE_DEST_WINSYSMSG
									.equals(((BusinessLogicException) caught)
											.getErrorCode())) {
						backupSetting.checkDestDriverType(caught, true);
					} else {
						decreaseBusyCount();
						super.onFailure(caught);
						onSavingCompleted(false);
					}
				}
			}

			@Override
			public void onSuccess(Long result) {
				decreaseBusyCount();
				if (isForEdge) {
					onSavingCompleted(true);
				} else {
					backupSetting.launchFirstBackupJobifNeeded();
					contentHost.close();
					// refresh preference settings
					if (UIContext.d2dHomepagePanel != null)
						UIContext.d2dHomepagePanel.refresh(null,
								IRefreshable.CS_CONFIG_CHANGED);
					else if (UIContext.hostPage != null) {
						UIContext.hostPage.refresh(null);
					}
					// refresh archive settings
					UIContext.d2dHomepagePanel.refreshProtectionSummary(null);
				}
			}
		});
	}
	
	private void saveItem() {
		contentHost.increaseBusyCount();
		ISettingsContent settingsContent = this.saveQueue.poll();
		settingsContent.saveData();
	}
	
	
	private void saveVCMSettings() {
		saveQueue.clear();
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if(contentEntry.getId() == vcmSettingID) {
				ISettingsContent vcmSettingsContent = contentEntry.getContentObject();
				saveQueue.add(vcmSettingsContent);
				break;
			}
			
		}
		
		saveItem();
	}
	
	private void saveVsphereSettings() {
		saveQueue.clear();
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if(contentEntry.getId() == vsphereSettingID) {
				ISettingsContent vcmSettingsContent = contentEntry.getContentObject();
				saveQueue.add(vcmSettingsContent);
				break;
			}
			
		}
		
		saveItem();
	}

	public void saveRpsSettings() {
		saveQueue.clear();
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if(contentEntry.getId() == rpsSettingID) {
				ISettingsContent rpsSettingsContent = contentEntry.getContentObject();
				saveQueue.add(rpsSettingsContent);
				break;
			}
			
		}
		
		saveItem();
	}
	
	private void initHostTitle() {
		if(contentHost!=null) {
			String hostTitle = "";
			switch (appType)
			{
			case D2D:
				hostTitle = UIContext.Constants.homepageTasksBackupSettingLabel();
				break;
			case VCM://vcm
				hostTitle =  VCMMessages.coldStandbyTaskSettings();
				break;
			case VSPHERE:
				hostTitle = UIContext.Constants.homepageTasksBackupSettingLabel();
				break;
			case RPS:
				hostTitle = UIContext.Constants.homepageTasksBackupSettingLabel();
				break;
			case RPSCM:
				hostTitle = UIContext.Constants.homepageTasksBackupSettingLabel();
				break;
			}
			contentHost.setCaption(hostTitle);
		}
	}
	
	public void setDefaultEmail(IEmailConfigModel iEmailConfigModel) {
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if(contentEntry.getId() == d2dPreferenceSettingID) {
				ISettingsContent d2dPreferenceContent = contentEntry.getContentObject();
				d2dPreferenceContent.setDefaultEmail(iEmailConfigModel);
				break;
			}
			else if(contentEntry.getId() == vcmPreferenceID) {
				ISettingsContent VCMPreferenceContent = contentEntry.getContentObject();
				VCMPreferenceContent.setDefaultEmail(iEmailConfigModel);
				break;
			}
			else if(contentEntry.getId() == vspherePreferenceID) {
				ISettingsContent VSPherePreferenceContent = contentEntry.getContentObject();
				VSPherePreferenceContent.setDefaultEmail(iEmailConfigModel);
				break;
			}
			
		}
	}
	//////////////////////////////////////////////////////////////////////////
	// ISettingsContentHost Methods 

	@Override
	public void setCaption( String text )
	{
		
	}
	
	@Override
	public void close()
	{
		if(contentHost!=null) {
			contentHost.close();
		}
		
	}

	@Override
	public void increaseBusyCount( String message )
	{
		if(contentHost!=null) {
			contentHost.increaseBusyCount(message);
		}
		
	}

	@Override
	public void increaseBusyCount()
	{
		if(contentHost!=null) {
			contentHost.increaseBusyCount();
		}
		
	}

	@Override
	public void decreaseBusyCount()
	{
		if(contentHost!=null) {
			contentHost.decreaseBusyCount();
		}
		
	}

	
	@Override
	public void showSettingsContent( int settingsContentId )
	{
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if (contentEntry.getId() == settingsContentId)
			{
				if (contentEntry.getTabItem() != null) {
					this.tabPanel.setSelection( contentEntry.getTabItem());
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void focusWidge( Widget widget )
	{
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onAsyncOperationCompleted(
		int operation, int result, int settingsContentId )
	{
		if (operation == ISettingsContentHost.Operations.LoadData)
		{
			contentHost.decreaseBusyCount();
			
			if (result != ISettingsContentHost.OperationResults.Succeeded)
			{
				//decreaseBusyCount();
				onLoadingCompleted(false);
				return;
			}
			
			enableEdit(settingsContentId);
			this.loadCount--;
			if(this.loadCount<=0) {
				onLoadingCompleted(true);
			}
		
		}
		
		else if (operation == ISettingsContentHost.Operations.SaveData)
		{			
			contentHost.decreaseBusyCount();
			
			if (result != ISettingsContentHost.OperationResults.Succeeded)
			{
				// TODO: pop up error message
				showSettingsContent(settingsContentId);
				onSavingCompleted(false);
			}
			else{
				if(saveQueue.size() == 0) {
					if((appType == AppType.D2D)&& (!isD2DSaved)) {
						saveD2DConfiguration();
					}
					else {
						onSavingCompleted(true);
						contentHost.close();
					}
					
				}
				else {
					saveItem();
				}
				
			}

		}
		
		else if(operation == ISettingsContentHost.Operations.Validate) {
			
			contentHost.decreaseBusyCount();
			
			if(result != ISettingsContentHost.OperationResults.Succeeded) {
				showSettingsContent(settingsContentId);
				onSavingCompleted(false);
			}
			else {
				if(validateQueue.size()!=0) {
					validateItem();
				}
				else {
					saveSetting();
				}
			}
		}
		
	}
	
	protected void onLoadingCompleted( boolean isSuccessful )
	{
		
		this.contentHost.onAsyncOperationCompleted(
				ISettingsContentHost.Operations.LoadData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	
	}
	

	protected void onSavingCompleted( boolean isSuccessful )
	{
		this.contentHost.onAsyncOperationCompleted(
				ISettingsContentHost.Operations.SaveData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}

	protected void onValidatingCompleted( boolean isSuccessful )
	{
		this.contentHost.onAsyncOperationCompleted(
				ISettingsContentHost.Operations.Validate,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}

	public D2DSettingModel getD2dSettings() {
		if(d2dSettings == null) {
			d2dSettings = new D2DSettingModel();
		}
		return d2dSettings;
	}

	public void setD2dSettings(D2DSettingModel d2dSettings) {
		this.d2dSettings = d2dSettings;
	}
	
	@Override
	protected void onUnload() {
		Utils.clearConnectionCache();
	}

public class SettingsContentEntry
{
	private int id;
	private String displayName;
	private ISettingsContent contentObject;
	private int contentFlag;
	private TabItem tabItem;
	private AbstractImagePrototype tabIcon;
	
	public SettingsContentEntry(
		int id, String displayName,
		ISettingsContent object, int contentFlag, AbstractImagePrototype tabIcon )
	{
		this.id				= id;
		this.displayName	= displayName;
		this.contentObject	= object;
		this.contentFlag	= contentFlag;
		this.tabItem		= null;
		this.tabIcon 		= tabIcon;
	}
	
	public AbstractImagePrototype getTabIcon() {
		return tabIcon;
	}

	public void setTabIcon(AbstractImagePrototype tabIcon) {
		this.tabIcon = tabIcon;
	}

	public int getId()
	{
		return id;
	}
	
	public void setId( int id )
	{
		this.id = id;
	}
	
	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName( String displayName )
	{
		this.displayName = displayName;
	}

	public ISettingsContent getContentObject()
	{
		return contentObject;
	}
	
	public void setContentObject( ISettingsContent contentObject )
	{
		this.contentObject = contentObject;
	}

	public int getContentFlag()
	{
		return contentFlag;
	}

	public void setContentFlag( int contentFlag )
	{
		this.contentFlag = contentFlag;
	}

	public TabItem getTabItem()
	{
		return tabItem;
	}

	public void setTabItem( TabItem tabItem )
	{
		this.tabItem = tabItem;
	}
}
}