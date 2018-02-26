package com.ca.arcflash.ui.client.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.edge.setting.EmailPanel;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.ca.arcflash.ui.client.vsphere.setting.EmailAlertSettings;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class CommonPreferenceSettings extends LayoutContainer implements ISettingsContent
{
	
	public DeckPanel 					deckPanel;
	private VerticalPanel 				toggleButtonPanel;
	
	private ISettingsContent   			emailSettingsContent;
	private ToggleButton 				emailButton;
	private ToggleButton 				emailLabel;
	private ClickHandler 				emailButtonHandler;
	private SettingsGroupType			settingsGroupType;
	private AppType						appType;
	
	public Map<String, LayoutContainer> map = new FastMap<LayoutContainer>();
	public List<String> settingNameList = new ArrayList<String>();

	@SuppressWarnings("unused")
	private ISettingsContentHost 		contentHost;
	
	public CommonPreferenceSettings(SettingsGroupType settingsGroupType)
	{
		this.settingsGroupType = settingsGroupType;
	}
	
	public CommonPreferenceSettings(AppType appType)
	{
		this.appType = appType;
	}
	
	private void doInitialization() {
		
		EmailPanel emailPanel = null;
		EmailAlertSettings emailAlertSettings = null;
		
		if(settingsGroupType == SettingsGroupType.VCMSettings) {
			emailPanel = new EmailPanel( false );
			this.emailSettingsContent = emailPanel;
			/*if(!isForEdge) {
				emailPanel.setEnabled(false);
			}
			else {
				emailPanel.setEnabled(true);
			}*/
		}
		else if(settingsGroupType == SettingsGroupType.RemoteVCMSettings) {
			emailPanel = new EmailPanel( true );
			this.emailSettingsContent = emailPanel;
			/*if(!isForEdge) {
				emailPanel.setEnabled(false);
			}
			else {
				emailPanel.setEnabled(true);
			}*/
		}
		else if(settingsGroupType == SettingsGroupType.VMBackupSettings) {
			emailAlertSettings = new EmailAlertSettings(); 
			this.emailSettingsContent = emailAlertSettings;
		}
		
		
		LayoutContainer contentPanel = new LayoutContainer();
		
		this.setLayout( new RowLayout( Orientation.VERTICAL ) );
		contentPanel.setLayout( new RowLayout( Orientation.HORIZONTAL ) );
		this.setStyleAttribute("background-color","#DFE8F6");
				
		deckPanel = new DeckPanel();
		deckPanel.setStyleName("backupSettingCenter");
		
		if(emailSettingsContent!=null) {
			deckPanel.add(emailSettingsContent.getWidget());
			String settingName = UIContext.Constants.preferencesEmailAlertsLabel(); 
			map.put(settingName, (LayoutContainer)emailSettingsContent);
			settingNameList.add(settingName);
		}
		
		toggleButtonPanel = new VerticalPanel();
		toggleButtonPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleButtonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		toggleButtonPanel.setTableWidth("100%");
		toggleButtonPanel.setStyleAttribute("background-color","#DFE8F6");
		
		emailButtonHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				emailButton.setDown(true);
				emailLabel.setDown(true);
			}
		};
		emailButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupAdvanced()).createImage());
		emailButton.ensureDebugId("f8ddb2c3-4130-4b5e-be84-9b073ef081af");
		emailButton.setStylePrimaryName("demo-ToggleButton");
		emailButton.setDown(true);
		emailButton.addClickHandler(emailButtonHandler);
		toggleButtonPanel.add(emailButton);
		
		
		emailLabel = new ToggleButton(UIContext.Constants.coldStandbySettingEmailAlertTitle());
		emailLabel.ensureDebugId("db1a9718-182f-4b7c-9b52-30045c3dba6e");
		emailLabel.setStylePrimaryName("tb-settings");
		emailLabel.setDown(true);	
		emailLabel.addClickHandler(emailButtonHandler);
		toggleButtonPanel.add(emailLabel);
		
		//contentPanel.add( toggleButtonPanel, new RowData( -1, 1 ) );
		//change the -1 to 140 in order to fix the ff by the index.css
		contentPanel.add( toggleButtonPanel, new RowData( 140, 1 ) );
		
		contentPanel.add( deckPanel, new RowData( 1, 1 ) );
		
		this.add( contentPanel, new RowData( 1, 1 ) );
		
		// Default Tab - destination.
		//contentHost.setCaption(UIContext.Messages.coldStandbySettingTitle(UIContext.Constants.coldStandbySettingVirtualizationTitle()));
		emailSettingsContent.initialize(this.contentHost, this.isForEdge);
		
		if((settingsGroupType==SettingsGroupType.VCMSettings)&&(emailPanel!=null)) {
			emailPanel.setEnabled(isForEdge);
		}
		else if((settingsGroupType==SettingsGroupType.VMBackupSettings)&&(emailAlertSettings!=null)) {
			emailAlertSettings.setEditable(isForEdge);
		}
		
		deckPanel.showWidget(0);
		

	}
	

	//////////////////////////////////////////////////////////////////////////
	//
	//  ADDED FOR EDGE
	//
	//////////////////////////////////////////////////////////////////////////

	private boolean isForEdge = false;
	private int settingsContentId = -1;
	
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
		this.emailSettingsContent.setId(settingsContentId);
	}

	@Override
	public Widget getWidget()
	{
		return this;
	}

	@Override
	public void loadData()
	{
		if(emailSettingsContent !=null) {
			emailSettingsContent.loadData();
		}
	}

	@Override
	public void loadDefaultData()
	{
		if(emailSettingsContent !=null) {
			emailSettingsContent.loadDefaultData();
		}
	}

	@Override
	public void saveData()
	{
		if(emailSettingsContent !=null) {
			emailSettingsContent.saveData();
		}
	}
	
	@Override
	public void validate()
	{
		if(emailSettingsContent !=null) {
			emailSettingsContent.validate();
		}
	}
	
	@Override
	public void setDefaultEmail(IEmailConfigModel iEmailConfigModel)
	{
		if(emailSettingsContent!=null) {
			emailSettingsContent.setDefaultEmail(iEmailConfigModel);
		}
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

	public List<String> getSettingNameList() {
		return settingNameList;
	}
}