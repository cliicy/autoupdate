package com.ca.arcflash.ui.client.common;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class CommonSettingContent extends LayoutContainer implements ISettingsContent
{	
	private CommonSettingContent		outerThis;
	private AppType						appType;
	private SettingsGroupType			settingsGroupType;
	protected BaseCommonSettingTab		settingTab;
	protected ISettingsContentHost        contentHost;
	
	public CommonSettingContent(SettingsGroupType settingsGroupType)
	{
		this.outerThis = this;
		this.settingsGroupType = settingsGroupType;
	}
	
	protected CommonSettingTab createSettingTab(AppType appType, boolean isForEdge, ISettingsContentHost contentHost) {
		return new CommonSettingTab(appType, isForEdge,contentHost);
	}
	
	public CommonSettingContent(AppType appType)
	{
		this.outerThis = this;
		this.appType = appType;
	}
	
	private void doInitialization() 
	{
		switch (this.settingsGroupType)
		{
		case D2DSettings:
			this.settingTab = new D2DCommonSettingTab(this.settingsGroupType, isForEdge, contentHost);
			break;
			
		case VCMSettings:
		case RemoteVCMSettings:
			this.settingTab = new VCMCommonSettingTree(this.settingsGroupType, isForEdge, contentHost);
			break;
			
		case VMBackupSettings:
			this.settingTab = new VSphereCommonSettingTree(this.settingsGroupType, isForEdge, contentHost);
			break;
			
		case AgentSettings:
			this.settingTab = new AgentCommonSettingTree(this.settingsGroupType, isForEdge, contentHost);
			break;
		}
		
		
		this.setLayout( new FitLayout() );
		
		this.add(settingTab);
		
	}

	private void loadSetting()
	{
		settingTab.loadSetting();
	}
	
	private void loadDefaultSetting() {
		settingTab.loadDefaultSetting();
	}
	
	private void validateSetting() {
		settingTab.validateSettting();
		
	}
	
	private void setDefaultMailSettting(IEmailConfigModel iEmailConfigModel) {
		settingTab.setDefaultEmail(iEmailConfigModel);
	}

	
	//////////////////////////////////////////////////////////////////////////
	//
	//  ADDED FOR EDGE
	//
	//////////////////////////////////////////////////////////////////////////

	protected boolean isForEdge = false;
	private int settingsContentId = -1;
	private int edgePolicyProductType = -1;
	
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
		this.settingTab.setId( this.settingsContentId );
	}
	
	@Override
	public Widget getWidget()
	{
		return this;
	}

	@Override
	public void loadData()
	{
		loadSetting();
	}

	@Override
	public void loadDefaultData()
	{
		loadDefaultSetting();
	}

	@Override
	public void saveData()
	{
		checkIfWeCanSave();
	}
	
	private void doSaving()
	{
		validate();
	}
	
	@Override
	public void validate()
	{
		validateSetting();
	}
	
	@Override
	public void setDefaultEmail(IEmailConfigModel iEmailConfigModel)
	{
		setDefaultMailSettting(iEmailConfigModel);
	}

	//////////////////////////////////////////////////////////////////////////
	
	protected void checkIfWeCanSave()
	{
		if (this.isForEdge)
		{
			doSaving();
			return;
		}
		
		int settingsType = getSettingsTypeByAppType( this.settingsGroupType );
		Broker.loginService.isUsingEdgePolicySettings( settingsType, new AsyncCallback<Boolean>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				doSaving();
			}

			@Override
			public void onSuccess( Boolean result )
			{
				if (!result) // not using Edge policy settings
				{
					doSaving();
				}
				else
				{
					// notify async operation completed
					outerThis.contentHost.onAsyncOperationCompleted(
						ISettingsContentHost.Operations.SaveData,
						ISettingsContentHost.OperationResults.Failed,
						outerThis.settingsContentId );

					MessageBox msgBox = new MessageBox();
					msgBox.setTitleHtml( UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D) );
					msgBox.setMessage( UIContext.Messages.useEdgePolicyAndCannotSaveSettings(UIContext.productNameD2D,UIContext.productNameD2D) );
					msgBox.setIcon( MessageBox.ERROR );
					Utils.setMessageBoxDebugId(msgBox);
					msgBox.show();
				}
			}
		});
	}
	
	//////////////////////////////////////////////////////////////////////////

	private int getSettingsTypeByAppType( SettingsGroupType settingsGroupType )
	{
		switch (settingsGroupType)
		{
		case D2DSettings:
			return SettingsTypesForUI.BackupSettings;
			
		case VCMSettings:
		case RemoteVCMSettings:
			return SettingsTypesForUI.VCMSettings;
			
		case VMBackupSettings:
			return SettingsTypesForUI.VMBackupSettings;
			
		default:
			return -1;
		}
	}

	public void setEdgePolicyProductType(int edgePolicyProductType) {
		this.edgePolicyProductType = edgePolicyProductType;
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
