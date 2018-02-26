package com.ca.arcflash.ui.client.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.model.D2DSettingModel;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
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

public abstract class BaseCommonSettingTab extends LayoutContainer {
	protected boolean d2dUsingEdgePolicy = false;
	public void setD2dUsingEdgePolicy(boolean d2dUsingEdgePolicy) {
		this.d2dUsingEdgePolicy = d2dUsingEdgePolicy;
	}

	protected boolean isForEdge;
	public boolean isForEdge() {
		return isForEdge;
	}

	protected ISettingsContentHost contentHost;
	public ISettingsContentHost getContentHost() {
		return contentHost;
	}

	private int loadCount;
	protected SettingsGroupType settingsGroupType;
	protected ArrayList<SettingsContentEntry> settingsContentList;
	protected TabPanel tabPanel;
	protected final  String	tabIndex = "tabIndex";
	protected BaseCommonSettingTab outThis;
	private int settingsContentId;
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
	
	protected D2DSettingModel d2dSettings;
	public void setD2dSettings(D2DSettingModel d2dSettings) {
		this.d2dSettings = d2dSettings;
	}

	protected String helpURL;
	
	protected Queue<ISettingsContent> saveQueue;
	protected Queue<ISettingsContent> validateQueue;	
	protected SettingPresenter settingPresenter;

	public BaseCommonSettingTab(SettingsGroupType settingsGroupType, boolean isForEdge, ISettingsContentHost contentHost) {
		this(settingsGroupType, isForEdge, contentHost,true);		
	}
	
	public BaseCommonSettingTab(SettingsGroupType settingsGroupType, boolean isForEdge, ISettingsContentHost contentHost, boolean isTabUI) {	
		this.settingsGroupType		= settingsGroupType;
		this.isForEdge				= isForEdge;
		this.contentHost			= contentHost;
		this.settingsContentList	= new ArrayList<SettingsContentEntry>();
		this.loadCount				= 1;
		outThis                     = this;	
		this.setLayout( new FitLayout() );
		this.saveQueue				= new LinkedList<ISettingsContent>();
		this.validateQueue			= new LinkedList<ISettingsContent>();
		
		if(isTabUI){
			LayoutContainer panel = new LayoutContainer();
			panel.setLayout(new RowLayout(Orientation.VERTICAL));
			this.tabPanel = new TabPanel();
			tabPanel.setTabScroll(true);
			this.tabPanel
					.ensureDebugId("{A8B02ED7-A5CE-421a-9444-0FE71DB219FC}");
			this.tabPanel.setDeferredRender(false);
			panel.add(this.tabPanel, new RowData(1, 1, new Margins(0, 0, 0, 0)));
			this.tabPanel.addListener(Events.Select,
					new Listener<TabPanelEvent>() {
						public void handleEvent(TabPanelEvent be) {
							Integer tabIndex = be.getItem().getData(
									outThis.tabIndex);
							presentTabSelectionIndex = tabIndex;

						}
					});

			this.add(panel);
			addSettingsContent();	
		}
		settingPresenter = SettingPresenter.getInstance();
		settingPresenter.setCommonSettings(this);
	}
	
//	@Override
//	public void close() {
//		if(contentHost!=null) {
//			contentHost.close();
//		}
//	}

//	@Override
//	public void decreaseBusyCount() {
//		if(contentHost!=null) {
//			contentHost.decreaseBusyCount();
//		}
//	}
//
//	@Override
//	public void focusWidge(Widget widget) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void increaseBusyCount(String message) {
//		if(contentHost!=null) {
//			contentHost.increaseBusyCount(message);
//		}
//	}
//
//	@Override
//	public void increaseBusyCount() {
//		if(contentHost!=null) {
//			contentHost.increaseBusyCount();
//		}
//	}

	protected void onSaveQueueEmpty() {
		onSavingCompleted(true);
		contentHost.close();
	}
	
//	@Override
	public void onAsyncOperationCompleted(int operation, int result,
			int settingsContentId) {
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
		}else if (operation == ISettingsContentHost.Operations.SaveData)
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
					onSaveQueueEmpty();
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

	
//	@Override
//	public void setCaption(String text) {
//		// TODO Auto-generated method stub
//		
//	}

	//@Override
	public void showSettingsContent(int settingsContentId) {
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
	
	public void loadSetting() {
		initD2DSettings();
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
	
	public void setId(int settingsContentId) {
		this.settingsContentId = settingsContentId;
	}
	
	
	public String getCurrentHelpURL() {
		return this.helpURL;
	}
	
	public void setCurrentHelpURL(String helpURL) {
		this.helpURL = helpURL;
	}
	
	
	public D2DSettingModel getD2dSettings() {
		if(d2dSettings == null) {
			d2dSettings = new D2DSettingModel();
		}
		return d2dSettings;
	}
	
	///////////////////////////////////////////////////////////////////////////////////	
	protected abstract String initSettingsContentList();
	
	protected abstract void saveSetting();
	
	public void initD2DSettings() {
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{	
			contentHost.increaseBusyCount();
			ISettingsContent settingsContent = contentEntry.getContentObject();
			settingsContent.loadData();
		}
		
	}	

	public void onLoadingCompleted( boolean isSuccessful )
	{
		
		this.contentHost.onAsyncOperationCompleted(
				ISettingsContentHost.Operations.LoadData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	
	}
	

	public void onSavingCompleted( boolean isSuccessful )
	{
		this.contentHost.onAsyncOperationCompleted(
				ISettingsContentHost.Operations.SaveData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}
	
	@Override
	protected void onUnload() {
		Utils.clearConnectionCache();
	}
	
	protected void enableEdit(int settingsId, ISettingsContent content) {};
	
	private void enableEdit(int settingsId) {
		ISettingsContent content = getSettingContentByID(settingsId);
		if(content == null) return;
		enableEdit(settingsId, content);
	}
	
	private ISettingsContent getSettingContentByID(int settingsId) {
		for(SettingsContentEntry entry : settingsContentList) {
			if(entry.getId() == settingsId){
				return entry.getContentObject();
			}
		}
		return null;
	}
	
	
	protected void enableEdit(int settingsId, ISettingsContent content, boolean isEnabled){}
	
	
	public void enableEdit(boolean isEnable) {
		for(SettingsContentEntry entry : settingsContentList) {
			enableEdit(entry.getId(), entry.getContentObject(), isEnable);
		}		
	}
	
	protected void addSettingsContent() {
		String hostTitle = initSettingsContentList();
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			ISettingsContent settingsContent = contentEntry.getContentObject();
			settingsContent.initialize( this.contentHost, this.isForEdge );
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
		
		setHostTitle(hostTitle);
	}
	
	protected void setHostTitle(String text) {
		if(contentHost!=null) {
			contentHost.setCaption(text);
		}
	}

	protected void saveItem() {
		contentHost.increaseBusyCount();
		ISettingsContent settingsContent = this.saveQueue.poll();
		settingsContent.saveData();
	}

	protected void validateItem() {
		contentHost.increaseBusyCount();
		ISettingsContent settingsContent = this.validateQueue.poll();
		settingsContent.validate();
	}
}
