package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.jobscript.alert.AlertJobScript;
import com.ca.arcflash.jobscript.base.JobType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyManager;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.ISettingsContent;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.ca.arcflash.ui.client.model.VCMDataStoreModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class VCMSettingsContent extends LayoutContainer implements ISettingsContent
{
	private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	
	private DeckPanel deckPanel;
	private VerticalPanel toggleButtonPanel;
	
	private VirtualizationPanel virtualizationPanel;
	private VirtualMachinePanel virtualMachinePanel;
	private StandinPanel        standinPanel;
	
	private ToggleButton virtulizationButton;
	private ToggleButton virtulMachineButton;
	private ToggleButton standinButton;
	
	private ToggleButton virtulizationLabel;
	private ToggleButton virtulMachineLabel;
	private ToggleButton standinLabel;
		
	private ClickHandler virtulizationButtonHandler;
	private ClickHandler virtulMachineButtonHandler;
	private ClickHandler standinButtonHandler;
	
	public final int STACK_VIRTULIZATION = 0;
	public final int STACK_VIRTULMACHINE = 1;
	public final int STACK_STANDIN = 2;
	
	private VCMSettingsContent outerThis;
	private ISettingsContentHost contentHost;
	private Map<String, LayoutContainer> settingMap = new FastMap<LayoutContainer>();
	private List<String> settingNameList = new ArrayList<String>();
	
	boolean isForRemoteVCM;
	
	private static int buttonSelected;	
	public static int getButtonSelected() {
		return buttonSelected;
	}
	public static void setButtonSelected(int buttonSelected) {
		VCMSettingsContent.buttonSelected = buttonSelected;
	}
	
	public VCMSettingsContent()
	{
		this( false );
	}
	
	public VCMSettingsContent( boolean isForRemoteVCM )
	{
		outerThis = this;
		this.isForRemoteVCM = isForRemoteVCM;
	}
	
	private void doInitialization() {
		
		LayoutContainer contentPanel = new LayoutContainer();
		

		this.setLayout( new RowLayout( Orientation.VERTICAL ) );
		contentPanel.setLayout( new RowLayout( Orientation.HORIZONTAL ) );
		this.setStyleAttribute("background-color","#DFE8F6");
				
		deckPanel = new DeckPanel();
		deckPanel.setStyleName("backupSettingCenter");
		
		virtualizationPanel = new VirtualizationPanel( this.isForRemoteVCM );
		deckPanel.add(virtualizationPanel);
		String settingName = UIContext.Constants.coldStandbySettingVirtualizationTitle();
		settingMap.put(settingName, virtualizationPanel);
		settingNameList.add(settingName);

		virtualMachinePanel = new VirtualMachinePanel(this.isForEdge);
		deckPanel.add(virtualMachinePanel);
		settingName = UIContext.Constants.coldStandbySettingVMTitle();
		settingMap.put(settingName, virtualMachinePanel);
		settingNameList.add(settingName);

		standinPanel = new StandinPanel();
		deckPanel.add(standinPanel);
		settingName = UIContext.Constants.coldStandbySettingStandinTitle();
		settingMap.put(settingName, standinPanel);
		settingNameList.add(settingName);
		
		
		toggleButtonPanel = new VerticalPanel();
		toggleButtonPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleButtonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		//toggleButtonPanel.setHeight(520);
		toggleButtonPanel.setTableWidth("100%");
		toggleButtonPanel.setStyleAttribute("background-color","#DFE8F6");
		
		virtulizationButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupDestination()).createImage());		
		virtulizationButton.setStylePrimaryName("demo-ToggleButton");
		virtulizationButton.ensureDebugId("9af1c9f3-0305-4add-9ae9-4c129dbe5454");
		virtulizationButton.setDown(true);
		
		virtulizationButtonHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				showTabPanel(STACK_VIRTULIZATION);
			
			}			
		};
		virtulMachineButtonHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				showTabPanel(STACK_VIRTULMACHINE);
				//virtualMachinePanel.activate();
			}
			
		};
			
		standinButtonHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				showTabPanel(STACK_STANDIN);
			}
			
		};
		
		
		virtulizationButton.addClickHandler(virtulizationButtonHandler);
		toggleButtonPanel.add(virtulizationButton);
		
		virtulizationLabel = new ToggleButton(UIContext.Constants.coldStandbySettingVirtualizationTitle());
		virtulizationLabel.setStylePrimaryName("tb-settings");
		virtulizationLabel.ensureDebugId("f02fd142-ee59-4657-adce-8e193419adcc");
		virtulizationLabel.setDown(true);	
		virtulizationLabel.addClickHandler(virtulizationButtonHandler);		
		toggleButtonPanel.add(virtulizationLabel);
		
		virtulMachineButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupSchedule()).createImage());
		virtulMachineButton.setStylePrimaryName("demo-ToggleButton");
		virtulMachineButton.ensureDebugId("ecc5f20d-8156-4fc0-a39c-6b8eaa05755e");
		virtulMachineButton.addClickHandler(virtulMachineButtonHandler);
	
		toggleButtonPanel.add(virtulMachineButton);
		virtulMachineLabel = new ToggleButton(UIContext.Constants.coldStandbySettingVMTitle()); 		
		virtulMachineLabel.setStylePrimaryName("tb-settings");
		virtulMachineLabel.ensureDebugId("490e2250-0c08-4ade-aeb6-e8b323e5bc7a");
		virtulMachineLabel.addClickHandler(virtulMachineButtonHandler);
		toggleButtonPanel.add(virtulMachineLabel);
		
		standinButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupSettings()).createImage());
		standinButton.setStylePrimaryName("demo-ToggleButton");
		standinButton.ensureDebugId("06979209-1500-4856-aebf-ca99b4106115");
		standinButton.addClickHandler(standinButtonHandler);
		if (!this.isForRemoteVCM)
			toggleButtonPanel.add(standinButton);
		
		standinLabel = new ToggleButton(UIContext.Constants.coldStandbySettingStandinTitle());
		standinLabel.setStylePrimaryName("tb-settings");
		standinLabel.ensureDebugId("a88d0534-9e76-4967-b60b-50a7578e8a3d");
		standinLabel.addClickHandler(standinButtonHandler);
		if (!this.isForRemoteVCM)
			toggleButtonPanel.add(standinLabel);
		
		contentPanel.add( toggleButtonPanel, new RowData( 140, 1 ) );
		
		contentPanel.add( deckPanel, new RowData( 1, 1 ) );
		
		this.add( contentPanel, new RowData( 1, 1 ) );
		
		// Default Tab - destination.
		contentHost.setCaption(UIContext.Messages.coldStandbySettingTitle(UIContext.Constants.coldStandbySettingVirtualizationTitle()));

		//Load the VCM setting
		deckPanel.showWidget(STACK_VIRTULIZATION);
		
		WizardContext context = WizardContext.getWizardContext();
		context.setVirtulizationPanel(virtualizationPanel);
		context.setVirtualMachinePanel(virtualMachinePanel);
		context.setStandingPanel(standinPanel);
		context.setContentHost(contentHost);
		context.setSettingConent(this);
	}

	public void enableEditing( boolean isEnabled )
	{
		if(!isEnabled) {
			virtualizationPanel.setEnabled(isEnabled);
			virtualMachinePanel.setEnabled(isEnabled);
			standinPanel.setEnabled(isEnabled);
		}
		
	}
	
	protected void showTabPanel(int tabID) {
		boolean isShowVirtualization = false;
		boolean isShowVirtualMachine = false;
		boolean isShowStandin = false;
		String  caption = "";
		
		if(tabID == STACK_VIRTULIZATION) {
			isShowVirtualization = true;
			caption = 	UIContext.Messages.coldStandbySettingTitle(UIContext.Constants.coldStandbySettingVirtualizationTitle());
		}
		else if(tabID == STACK_VIRTULMACHINE) {
			isShowVirtualMachine = true;
			caption = UIContext.Messages.coldStandbySettingTitle(UIContext.Constants.coldStandbySettingVMTitle());
			
		}
		else {
			isShowStandin = true;
			caption = UIContext.Messages.coldStandbySettingTitle(UIContext.Constants.coldStandbySettingStandinTitle());
		}
		
		
		virtulizationButton.setDown(isShowVirtualization);
		virtulMachineButton.setDown(isShowVirtualMachine);
		standinButton.setDown(isShowStandin);
			
		virtulizationLabel.setDown(isShowVirtualization);
		virtulMachineLabel.setDown(isShowVirtualMachine);
		standinLabel.setDown(isShowStandin);
			
		contentHost.setCaption(caption);
		
		if(tabID == STACK_VIRTULMACHINE) {
			WizardContext context = WizardContext.getWizardContext();
			int result = context.processVCMJobscripts(1, false);
			if(result==-1) {
				String msg = UIContext.Constants.coldStandbySettingVirtualizationConfigTip();
				context.showMessageBox("{A2AE1D0B-369B-47e8-96BF-84045D24F5A2}", msg);
				showTabPanel(STACK_VIRTULIZATION);
				return;
			}
			else if(result == 1) {
				return;
			}
			
		}
		
		//deckPanel.clear();
		//deckPanel.add(container);
		deckPanel.showWidget(tabID);
		buttonSelected = tabID;
		
		if(tabID == STACK_VIRTULMACHINE) {
			virtualMachinePanel.refreshGridUI();
		}

	}
	
	public void LoadSettings()
	{
		service.getJobScriptCombo(ColdStandbyManager.getVMInstanceUUID(), new BaseAsyncCallback<JobScriptCombo>(UIContext.productNameVCM) {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				enableEditing(outerThis.isForEdge);
				outerThis.onLoadingCompleted( false );
				
			}
			@Override
			public void onSuccess(JobScriptCombo result) {
				
				if (result != null && result.getFailoverJobScript() != null
						&& result.getHbJobScript() != null
						&& result.getRepJobScript() != null)
				{
					WizardContext.getWizardContext().setValidateStatus(0);
					virtualizationPanel.populateUI(result.getFailoverJobScript(), result.getHbJobScript(),
							result.getRepJobScript(),outerThis.isForEdge);
					virtualMachinePanel.populateUI(result.getFailoverJobScript(),result.getRepJobScript());
					standinPanel.populateUI(result.getHbJobScript(), result.getFailoverJobScript());
					//emailPanel.populateUI(result.getAlertJobScript());
					EmailPanel emailPanel = WizardContext.getWizardContext().getEmailPanel();
					if(emailPanel!=null) {
						emailPanel.populateUI(result.getAlertJobScript());
					}
					
					if(!outerThis.isForEdge) {
						outerThis.onLoadingCompleted( true );
					}
					
				}
				else {
					outerThis.onLoadingCompleted( true );
				}
				
				enableEditing(outerThis.isForEdge);
			}

		});
	}
	
	private void loadDefaultSettings()
	{
		outerThis.onLoadingCompleted( true );
	}
	
	protected void validateSettings(boolean isValidateVirtualizationPanel)
	{
		int result = 0;
		if(isValidateVirtualizationPanel){
			result = virtualizationPanel.validate();
			if(result == -1) {
				showTabPanel(STACK_VIRTULIZATION);
				this.contentHost.showSettingsContent( this.settingsContentId );
				this.onValidatingCompleted(false);
				return;
			}
			else if(result == 1) {
				return;
			}
		}

		
		if(!virtualMachinePanel.validate()) {
			showTabPanel(STACK_VIRTULMACHINE);
			this.contentHost.showSettingsContent( this.settingsContentId );
			this.onValidatingCompleted(false);
			return;
		}
		
		WizardContext context = WizardContext.getWizardContext();
		
		if (!this.isForRemoteVCM)
		{
			result = standinPanel.validate();
			if(result!=0) {
				showTabPanel(STACK_STANDIN);
				this.contentHost.showSettingsContent( this.settingsContentId );
				this.onValidatingCompleted(false);
				
				if(result==1){
					
					String errorMsg = UIContext.Constants.coldStandbySettingStandinHeartBeatRequire();
					context.showMessageBox("{9C376637-7110-4676-970C-6586DA28705F}", errorMsg);
				}
				return;
			}		
		}
		
		if(context.getVirtulizationType() == VirtualizationType.HyperV){
			checkHyperVPathValid();
		}
		else{
			checkResourcePoolValid();
		}
		
	}
	
	private boolean isStringEmpty(String target) {
		if(target==null) {
			return true;
		}
		else if(target.length()==0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void checkResourcePoolValid(){
		final WizardContext context = WizardContext.getWizardContext();
		BaseModel esxNode = context.getESXHostModel();
		String dataCenter= (String)esxNode.get("dataCenter");
		final String esxName= (String)esxNode.get("esxNode");
		String resourcePoolRef = context.getResourcePoolRef();
		if(isStringEmpty(resourcePoolRef)){
			onValidatingCompleted(true);
			return;
		}
		
		service.checkResourcePoolExist(context.getVMwareHost(),
				context.getVMwareUsername(),
				context.getVMwarePassword(),
				context.getVMwareProtocol(),
				context.getVMwarePort(),
				esxName,
				dataCenter,
				resourcePoolRef,
				new BaseAsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to checkResourcePoolExist()");
				//The UI doesn't block
				outerThis.onValidatingCompleted(true);
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result){
					outerThis.onValidatingCompleted(true);
				}
				else{
					outerThis.onValidatingCompleted(false);
					
					String poolName = context.getResourcePoolName();
					String poolRef = context.getResourcePoolRef();
					String esxHost = "";
					if(context.getVirtulizationType() == VirtualizationType.VMwareESX){
						esxHost = context.getVMwareHost();
					}
					else{
						esxHost = esxName;
					}
					String errorMsg = UIContext.Messages.coldStandbyCheckResourcePool(poolName, poolRef, esxHost);
					context.showMessageBox("{8DB689B9-FF26-4872-BA1E-AE6B0A1C38E2}", errorMsg);
					
				}
			}
		});
	}
	
	private void checkHyperVPathValid(){
		
		final WizardContext context = WizardContext.getWizardContext();
		final List<String> monitorPath=new ArrayList<String>();
		if(context.isConfiguredSameDatastore()) {
			monitorPath.add(context.getSameHyperVPath());
		}
		else {
			//get the VM configuration path
			String vmPath = context.getHyperVVMConfigPath();
			Boolean isFoundVMPath=false;
			for (String temp : monitorPath) {
				if(temp.compareToIgnoreCase(vmPath)==0) {
					isFoundVMPath = true;
					break;
				}
			}
			if(!isFoundVMPath){
				monitorPath.add(vmPath);
			}
			
			//get the replication disk path
			ListStore<VCMDataStoreModel> dataStore= context.getVMDisk();
			for(int i=0;i<dataStore.getCount();i++){
				String path=dataStore.getAt(i).getHyperVPath().getValue();
				Boolean isFound=false;
				for (String temp : monitorPath) {
					if(temp.compareToIgnoreCase(path)==0) {
						isFound = true;
						break;
					}
				}
				if(!isFound){
					monitorPath.add(path);
				}
			}
			
		}
		
		service.checkPathIsSupportHyperVVM(monitorPath, new BaseAsyncCallback<int[]>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to checkPathIsSupportHyperVVM()");
				//The UI doesn't block
				outerThis.onValidatingCompleted(true);
			}

			@Override
			public void onSuccess(int[] result) {
				if(result!=null){
					for(int i=0;i<result.length; i++){
						if(result[i]>0){
							outerThis.onValidatingCompleted( false );
							showTabPanel(STACK_VIRTULMACHINE);
							outerThis.contentHost.showSettingsContent( outerThis.settingsContentId );
							String msg = UIContext.Messages.coldStandbyUnSupportHyperVPath(monitorPath.get(i));
							context.showMessageBox("{F985CBF5-4BDF-4192-9D83-210FFFF69C03}", msg);
							return;
						}
					}
				}
				outerThis.onValidatingCompleted( true );
			}
		});
		
	}
	
	private boolean saveSettings()
	{
		//set heart beat job script
		final HeartBeatJobScript heartBeatScript = new HeartBeatJobScript();
		standinPanel.populateBeatJobScript(heartBeatScript);
		virtualizationPanel.populateBeatJobScript(heartBeatScript);
		
		//set the failover job script
		final FailoverJobScript failoverScript = new FailoverJobScript();
		virtualMachinePanel.populateFailoverJobScript(failoverScript);
		virtualizationPanel.populateFailoverJobScript(failoverScript);
		standinPanel.populateFailoverJobScript(failoverScript);

		//set the replication job script
		final ReplicationJobScript replicationScript = new ReplicationJobScript();
		replicationScript.setJobType(JobType.Replication);
		replicationScript.setVirtualType(virtualizationPanel.getVirtulizationType());
		replicationScript.setAutoReplicate(true);
		virtualizationPanel.populateReplicationJobScript(replicationScript);
		//standinPanel.populateReplicationJobScript(replicationScript);
		virtualMachinePanel.populateReplicationJobScript(replicationScript);
		//summaryPanel.populateReplicationJobScript(replicationScript);
		
		//set the alert job script
		final AlertJobScript alertJobScript = new AlertJobScript();
		//emailPanel.populateAlertJobScript(alertJobScript);
		WizardContext context = WizardContext.getWizardContext();
		if(context.getEmailPanel()!=null) {
			context.getEmailPanel().populateAlertJobScript(alertJobScript);
		}
		
		
		final JobScriptCombo scriptCombo = new JobScriptCombo();
		scriptCombo.setFailoverJobScript(failoverScript);
		scriptCombo.setHbJobScript(heartBeatScript);
		scriptCombo.setRepJobScript(replicationScript);
		scriptCombo.setAlertJobScript(alertJobScript);
		
		service.setJobScriptCombo(scriptCombo, new BaseAsyncCallback<Void>(UIContext.productNameVCM) {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				outerThis.onSavingCompleted( false );
			}

			@Override
			public void onSuccess(Void result) {
				outerThis.onSavingCompleted( true );
				contentHost.close();
				//UIContext.homepagePanel.refresh(null);
				//UIContext.hostPage.refresh(null);
			}
		});
		
		return true;

	}

	@Override
	protected void onLoad() {
		super.onLoad();
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
	}

	@Override
	public Widget getWidget()
	{
		return this;
	}

	@Override
	public void loadData()
	{
		LoadSettings();
	}

	@Override
	public void loadDefaultData()
	{
		loadDefaultSettings();
	}

	@Override
	public void saveData()
	{
		saveSettings();
	}
	
	@Override
	public void validate()
	{
		validateSettings(true);
	}
	
	@Override
	public void setDefaultEmail(IEmailConfigModel iEmailConfigModel)
	{
		
	}
	
	protected void onLoadingCompleted( boolean isSuccessful )
	{
		//if (this.isForEdge)
		//{
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.LoadData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
		//}
	}
	
	protected void onSavingCompleted( boolean isSuccessful )
	{
		//if (this.isForEdge)
		//{
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.SaveData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
		//}
	}
	
	protected void onValidatingCompleted( boolean isSuccessful )
	{
		//if (this.isForEdge)
		//{
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.Validate,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
		//}
	}
	@Override
	public boolean isForLiteIT() {
		return false;
	}
	@Override
	public void setisForLiteIT(boolean isForLiteIT) {
		
	}
	@Override
	public List<SettingsTab> getTabList() {
		return null;
	}
	@Override
	public void switchTab(String tabId) {
		
	}

	public Map<String, LayoutContainer> getSettingMap() {
		return settingMap;
	}

	public DeckPanel getDeckPanel() {
		return deckPanel;
	}

	public List<String> getSettingNameList() {
		return settingNameList;
	}
	
	
}