package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ClientException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.ca.arcflash.ui.client.model.FileModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

class VirtualizationPanel extends LayoutContainer {
	private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	private final LoginServiceAsync  loginService = GWT.create(LoginService.class);
//	private static final int WIDTH_TEXTFIELD = 200;
	private static final int REFRESH_INTERVAL = 10000;
	
	private Radio radioVMware;
	private Radio radioHyperV;
	
	BaseComboBox<BaseModel> 	esxNodeBox;
	
	LayoutContainer				virtualServerLayout;
	VirtualizationVMWarePanel 	vmwarePanel;
	VirtualizationHyperVPanel 	hyperVPanel;
	MonitorPanel 				monitorPanel;
	Widget						monitorDisclosurePanel;
	
	private VirtualizationHost 	lastHost = new VirtualizationHost();
	//private VirtualizationHost  lastMonitor= new VirtualizationHost();
	private VirtualizationHost 	uiHost = new VirtualizationHost();
	private FailoverJobScript 	oldFailoverJobScript = null;
	
	SelectionChangedListener<BaseModel> esxNodeSelectedChangeHandler = null;
	Listener<ComponentEvent> 			esxNodeFocusChangeHandler = null;
	boolean								isDealwithFailed = false;
	boolean								isNeedTriggerFocusEvent = true;
	private String						monitorUUID;
	private Timer						timer;
	
	Object obj= new Object();
	int count = 0;
	
	boolean isForRemoteVCM;
	
	public VirtualizationPanel( boolean isForRemoteVCM ){
		this.ensureDebugId("8c6aa1f1-6f7a-4ae0-b350-f672bfd3af4c");
		this.isForRemoteVCM = isForRemoteVCM;
	}
	
	@Override
	public void render(Element target, int index) {
		super.render(target, index);
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setCellPadding(2);
		tl.setWidth("100%");
		container.ensureDebugId("4210d5b9-b213-4638-96a4-0788c96dd24a");
		container.setLayout(tl);
		container.setScrollMode(Scroll.AUTO);
		
		//title and description
		Label titleLabel = new Label(UIContext.Constants.coldStandbySettingVirtualizationTitle());
		titleLabel.ensureDebugId("02a89fb3-7f13-4a37-9ace-38625a39a2c3");
		titleLabel.setStyleName("coldStandbySettingTitle");
		titleLabel.getElement().getStyle().setPadding(8, Unit.PX);
		container.add(titleLabel);

		Label descriptionLabel = new Label(UIContext.Constants.coldStandbySettingVirtualizationDescription());
		descriptionLabel.ensureDebugId("a2d8a129-ad3d-4207-b8a5-39db686d8b7a");
		descriptionLabel.setStyleName("coldStandbySettingDescription");
		descriptionLabel.getElement().getStyle().setPaddingLeft(8, Unit.PX);
		container.add(descriptionLabel);
		
		container.add(getVirtulizationServerLayout());
		
		monitorDisclosurePanel = getMonitorLayout();
		container.add(monitorDisclosurePanel);
		
		//validate button
		/*Button validateButton = new Button(UIContext.Constants.coldStandbySettingVirtualizationValidate());
		validateButton.setWidth(100);
		validateButton.setStyleAttribute("margin","5px,0px,0px,15px");
		validateButton.addSelectionListener(getValidateButtonHandler());
		container.add(validateButton);*/
		
		this.add(container);
		
		renderVMwareVPanel();
	}
	
	@Override
	protected void doDetachChildren() {
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
		
		super.doDetachChildren();
	}

	@SuppressWarnings("deprecation")
	private Widget getVirtulizationServerLayout() {

		LayoutContainer virtulizationServerLayout = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("100%");
		virtulizationServerLayout.setLayout(tl);
		
		DisclosurePanel serverSettingPanel = new DisclosurePanel(
				(DisclourePanelImageBundles)GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingVirtualizationServerSetting(), false);
		serverSettingPanel.ensureDebugId("50083c32-68ab-4c33-a5c8-dc8e1724e03a");
		serverSettingPanel.setWidth("100%"); 
		serverSettingPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby"); 
		serverSettingPanel.setOpen(true);
		
		virtulizationServerLayout.add(setupVirtulizationType());
		
		virtualServerLayout = new LayoutContainer();
		vmwarePanel = new VirtualizationVMWarePanel();
		virtualServerLayout.add(vmwarePanel);
		setESXNode(vmwarePanel);
		
		virtulizationServerLayout.add(virtualServerLayout);
		virtulizationServerLayout.add(new Html("<HR>"));
		serverSettingPanel.add(virtulizationServerLayout);
		
		
		return serverSettingPanel;
	}
	
	private void setESXNode(VirtualizationVMWarePanel vmWarePanel){
		if((vmWarePanel==null)||(vmWarePanel.esxNodeBox == null))
			return;
		
		esxNodeBox = vmWarePanel.esxNodeBox;
		esxNodeBox.addListener(Events.Focus, getEsxNodeBoxFocusListener());
	}

	private Listener<ComponentEvent> getEsxNodeBoxFocusListener(){
		if(esxNodeFocusChangeHandler == null) {
			esxNodeFocusChangeHandler = new Listener<ComponentEvent>() {
				@Override
				public void handleEvent(ComponentEvent be) {
					//1)If the focus event from the ESX Node selection change, ignore the focus event
					//2)IE triggers one time focus event
					//3)FF and chrome triggers two times focus events
					if(isNeedTriggerFocusEvent) {
						GWT.log("get the focus");
						processVCMJobscripts(0, false);
					
					}
					else {
						isNeedTriggerFocusEvent = true;
						GWT.log("set the isNeedTriggerFocusEvent");
					}
					
				}
			};
		}
		
		return esxNodeFocusChangeHandler;
	}
	@SuppressWarnings("deprecation")
	private Widget getMonitorLayout() {
		//monitor
		
		DisclosurePanel monitorDisclosurePanel = new DisclosurePanel(
			(DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class),
			UIContext.Constants.coldStandbySettingStandinMonitoring(),false);
		monitorDisclosurePanel.ensureDebugId("db40228e-d620-4efa-9c7e-f08dcca4d934");
		monitorDisclosurePanel.setWidth("100%");
		monitorDisclosurePanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		monitorDisclosurePanel.setOpen(true);
		
		this.monitorPanel = new MonitorPanel();
		monitorDisclosurePanel.add( this.monitorPanel );
		return monitorDisclosurePanel;
	}
	
	
	
	@Override
	public void setEnabled(boolean enabled) {
		//super.setEnabled(enabled);
		
		//virtulizationServerLayout.setEnabled(enabled);
		if(isSelectHyperV()) {
			hyperVPanel.setEnabled(enabled);
		}
		else {
			vmwarePanel.setEnabled(enabled);
		}
		
		radioVMware.setEnabled(enabled);
		radioHyperV.setEnabled(enabled);
		esxNodeBox.setEnabled(enabled);
		
		//esxNodeContainer.setEnabled(enabled);
		if(enabled) {
			if(isSelectHyperV()) {
				monitorPanel.setEnabled(false);
			}
			else {
				monitorPanel.setEnabled(true);
			}
		}
		else {
			monitorPanel.setEnabled(enabled);
		}
		
	}

	private void setDefaultESXNode(BaseModel[] result, FailoverJobScript originalFailoverScript){
		if (result.length > 0) {
			esxNodeBox.setValue(result[0]);
		}
		
		if(originalFailoverScript==null){
			return;
		}

		if ( originalFailoverScript.getVirtualType()== VirtualizationType.VMwareVirtualCenter) {
			VMwareVirtualCenter esx = (VMwareVirtualCenter) originalFailoverScript.getFailoverMechanism().get(0);
			//esx.setEsxName((String)esxNode.get("esxNode"));
			String esxName=esx.getEsxName();
			for(int i=0;i<result.length;i++){
				String configureEsxName=(String)result[i].get("esxNode");
				if(configureEsxName.compareToIgnoreCase(esxName)==0){
					esxNodeBox.setValue(result[i]);
					return;
				}
			}
			
		}
	}
	
	protected int validate() {
		int result = 0;
		result= processVCMJobscripts(2, !this.isForRemoteVCM);
		if((result==0)&&(!isSelectHyperV())&&(!this.isForRemoteVCM)){
			isDealwithFailed = false;
			WizardContext.getWizardContext().mask();
			connectToMonitor();
			result = 1;
		}
		return result;
	}
	
	private int processVCMJobscriptsCallBackType = 0;
	protected int processVCMJobscripts(int callBackType, boolean isValidateMonitor) {
		boolean bRet = false;
		this.processVCMJobscriptsCallBackType = callBackType;
		if(isSelectHyperV()) {
			bRet = hyperVPanel.validate();
		}
		else {
			bRet = vmwarePanel.validate();
			if(isValidateMonitor){
				bRet = bRet & monitorPanel.validate();
			}
		}
		
		WizardContext context = WizardContext.getWizardContext();
		if(!bRet) {
			return -1;
		}
		else {
			
			//callBackType 0: the event from the ESX node focus;
			//callBacktype 1: the event from 'click the virtual machine panel';
			//callBacktype 2: the event from the 'save' button event.
			
			//validateStatus 0: get the event form load VCM settings event
			//validateStatus 1: get the event from 'click the virtual machine panel' or 'get the ESX node focus'
			//validateStatus 2: get the event from 'save button' event
			if((callBackType == 0)||(callBackType == 1)) {
				context.setValidateStatus(1);
			}
			else if(callBackType == 2) {
				context.setValidateStatus(2);
			}
		}
		
		if((callBackType == 0)&&(!isUIVirtualizationHostChange())) {
			GWT.log("The UI host doesn't change");
			return 0;
		}
		
		if(isVirtualizationHostChange()) {
			connectVirtualizationServer();
			if(!isSelectHyperV()){
				context.cleanESXResourcePool();
			}
			
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	protected boolean isUIVirtualizationHostChange() {
		VirtualizationHost currentHost = getCurrentHost();
		boolean result = uiHost.equals(currentHost);
		if(!result) {
			uiHost.copyVirtualHost(currentHost);
		}
		return (!result);
	}
	
	private void connectVirtualizationServer() {
		isDealwithFailed = false;
		WizardContext.getWizardContext().mask();
		if(isSelectHyperV()) {
			connectToHyperVHost();
		}
		else {
			getESXNode();

		}
		
	}
	
	private void connectToHyperVHost() {
		final WizardContext context = WizardContext.getWizardContext();
		final String hyperVHost=context.getHyperVHost();
		final String hyperVUsername = context.getHyperVUsername();
		final String hyperVPassword = context.getHyperVPassword();
		final int	port = context.getHyperVPort();
		final String	protocol = context.getHyperVProtocol();
		
		service.testMonitorConnection(hyperVHost, port,
				 ConnectionProtocol.string2Protocol(protocol),
				 hyperVUsername, hyperVPassword,
				 true, new BaseAsyncCallback<String>(){
					@Override
					public void onFailure(Throwable caught) {
						
						String errorMessage = null;
						if (caught instanceof ServiceConnectException){
							ServiceConnectException ex = (ServiceConnectException)caught;
							if(ex.getErrorCode().equals("38654705706")){//Unkown host
								errorMessage = UIContext.Messages.testMonitorConnectionUnkownHost(hyperVHost);
							}else{
								errorMessage = UIContext.Messages.testMonitorConnectionFail(UIContext.productNameD2D, hyperVHost);								
							}
						}
						else if(caught instanceof BusinessLogicException) {
							BusinessLogicException businessLogicException = (BusinessLogicException)caught;
							if(businessLogicException.getErrorCode().equals("4294967298")) {
								errorMessage = UIContext.Messages.coldStandbySettingVirtualizationFailedConnectHyperVMonitor(UIContext.productNameD2D);
							}
							else {
								errorMessage = businessLogicException.getDisplayMessage();
							}
						}
						else {
							errorMessage = ((ClientException) caught).getDisplayMessage();
						}
							
						
						handleFail("84e3cfdb-947e-47d2-8923-933ba088a668", errorMessage);
						handleUI();
					}
				
					@Override
					public void onSuccess(String result) {
						GWT.log("Successfully connect to the HypervHost. uuid="+result);
						monitorUUID = result;
						checkIsHyperVRoleInstalled();
					}
				});
		
	}
	
	private void connectToMonitor() {
		final WizardContext context = WizardContext.getWizardContext();
		final String monitorHost = context.getMonitorServer();
		final String monitorUsername =context.getMonitorUsername();
		final String monitorPassword = context.getMonitorPassword();
		final int port= context.getMonitorPort();
		final String protocol = context.getMonitorProtocol();

		service.testMonitorConnection(monitorHost, port,
				 ConnectionProtocol.string2Protocol(protocol),
				 monitorUsername, monitorPassword,
				 false, new BaseAsyncCallback<String>(){
					@Override
					public void onFailure(Throwable caught) {

						String errorMessage = null;
						if (caught instanceof ServiceConnectException){
							ServiceConnectException ex = (ServiceConnectException)caught;
							if(ex.getErrorCode().equals("38654705706")){//Unkown host
								errorMessage = UIContext.Messages.testMonitorConnectionUnkownHost(monitorHost);
							}else{
								errorMessage = UIContext.Messages.testMonitorConnectionFail(UIContext.productNameD2D, monitorHost);								
							}
						}
						else if(caught instanceof BusinessLogicException) {
							BusinessLogicException businessLogicException = (BusinessLogicException)caught;
							String errorCode = businessLogicException.getErrorCode();
							
							if(errorCode.equals("8589934593")) {
								errorMessage = UIContext.Constants.coldStandbySettingVirtualizationMonitorErrorPassword();
							}
							else if(errorCode.equals("8589934594")) {
								errorMessage = UIContext.Constants.coldStandbySettingVirtualizationMonitorErrorPrivilege();
							}
							else if(errorCode.equals("38654705692")){
								//SaaS node, block it
								errorMessage = businessLogicException.getDisplayMessage();
								handleFail("84e3cfdb-947e-47d2-8923-933ba088a668", errorMessage);
								handleUI();
								return;
							}
							else {
								errorMessage = UIContext.Messages.coldStandbySettingVirtualizationFailedConnectESXMonitor(UIContext.productNameD2D);
							}
						}
						else {
							errorMessage = UIContext.Messages.coldStandbySettingVirtualizationFailedConnectESXMonitor(UIContext.productNameD2D);
						}
							
						
						MessageBox mb = new MessageBox();
						mb.setIcon(MessageBox.WARNING);
						mb.setButtons(MessageBox.YESNO);
						mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameVCM));
						mb.setMessage(errorMessage);
						mb.addCallback(new Listener<MessageBoxEvent>()
						{
							public void handleEvent(MessageBoxEvent be)
							{
								if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
									context.unmask();
									context.validateVCMSettings(false);
								}
								else {
									handleFail();
									context.showSettingTab(0);
								}
									
							}
						});
						Utils.setMessageBoxDebugId(mb);
						mb.show();
					}
				
					@Override
					public void onSuccess(String result) {
						GWT.log("Successfully connect to the monitor");
						context.unmask();
						context.validateVCMSettings(false);
					}
				});
	}
	
	private void checkIsHyperVRoleInstalled(){
		final WizardContext context=WizardContext.getWizardContext();
		service.isHyperVRoleInstalled(
					context.getHyperVHost(),
					context.getHyperVProtocol(),
					context.getHyperVPort(),
					new BaseAsyncCallback<Boolean>(){
				@Override
				public void onFailure(Throwable caught) {
					
					handleFail("681d47cd-2eeb-44ec-b771-bbd0cdf79143", caught.getMessage());
					handleUI();
				}

				@Override
				public void onSuccess(Boolean result) {
					
					GWT.log("checkIsHyperVRoleInstalled:"+result);
					if(result) {
						isHostOSGreaterEqualW2K8SP2();
					}
					else {
						handleFail("75600817-8e14-42b4-a745-b58502de40b5",
								UIContext.Constants.coldStandbySettingIsHyperVRoleInstalled());
						handleUI();
					}
				}
			});
		
	}
	
	private void isHostOSGreaterEqualW2K8SP2(){
		final WizardContext context=WizardContext.getWizardContext();
		
		service.isHostOSGreaterEqualW2K8SP2(
					context.getHyperVHost(),
					context.getHyperVProtocol(),
					context.getHyperVPort(),
					new BaseAsyncCallback<Boolean>(){
				@Override
				public void onFailure(Throwable caught) {
					handleFail("6a7d433c-e9de-4c05-8c88-de4752b35ce4", caught.getMessage());
					handleUI();
				}

				@Override
				public void onSuccess(Boolean result) {
					GWT.log("isHostOSGreaterEqualW2K8SP2:"+result);
					if(result) {
						refreshHyperVMonitor();
						
						VirtualizationHost currentHost = getCurrentHost();
						lastHost.copyVirtualHost(currentHost);
						
						count = 4;
						getNetworkType();
						getNetworkConnection();
						getVirtualizationSuppoortInformation();
						getHyperVVolumes();
					}
					else {
						handleFail("02d1af05-c307-4d97-9fbc-d0b57614e014", UIContext.Constants.coldStandbySettingIsHostOSGreaterEqualW2K8SP2());
						handleUI();
					}
				}
			});
		
	}
	
	
	private void refreshHyperVMonitor(){
		
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
		
		timer = new Timer() {
			public void run() {
				Date date = new Date();
				GWT.log("Refresh:"+date.getTime());
				service.vcmValidateUserByUUID(monitorUUID, new BaseAsyncCallback<Integer>(){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}
			
					@Override
					public void onSuccess(Integer result) {
						
					}
				});
			}
		};
		timer.schedule(REFRESH_INTERVAL);
		timer.scheduleRepeating(REFRESH_INTERVAL);
		
	}
	
	private void getESXNode() {
		final WizardContext context = WizardContext.getWizardContext();
		final ListStore<BaseModel> nodes = esxNodeBox.getStore();
		nodes.removeAll();
		
		service.getESXNodeList(context.getVMwareHost(),context.getVMwareUsername(),
				context.getVMwarePassword(), context.getVMwareProtocol(), context.getVMwarePort(), 
				new BaseAsyncCallback<BaseModel[]>(){
					
					@Override
					public void onFailure(Throwable caught) {
						uiHost.clean();
						handleFail("{70D4CC51-2382-4f79-A5FB-75E7DA7B8768}",
								UIContext.Constants.coldStandbySettingVirtualizationFailedToConnectVM());
						handleUI();
					}

					@Override
					public void onSuccess(BaseModel[] result) {
						GWT.log("Successfully getESXNodeList()");
						
						if (result == null) {
							handleFail("{3B26D48B-B4CB-4bf8-B943-B3048D862E17}",
									UIContext.Constants.coldStandbySettingVirtualizationNoESXNode());
							handleUI();
							return;
						}
						for (BaseModel model : result) {
							nodes.add(model);
						}
						
						if(esxNodeSelectedChangeHandler!=null) {
							esxNodeBox.removeSelectionListener(esxNodeSelectedChangeHandler);
						}
						setDefaultESXNode(result, oldFailoverJobScript);
						
						VirtualizationHost currentHost = getCurrentHost();
						lastHost.copyVirtualHost(currentHost);
						esxNodeBox.addSelectionChangedListener(getESXNodeSelectedChangeHandler());
						
						count = 6;
						getVMWareVirtualType();
						getESXServerVersion();
						getESXDatastore();
						getNetworkType();
						getNetworkConnection();
						getVirtualizationSuppoortInformation();
						
					}
					
				});
	}
	
	private SelectionChangedListener<BaseModel> getESXNodeSelectedChangeHandler(){
		
		if(esxNodeSelectedChangeHandler==null) {
			esxNodeSelectedChangeHandler = new SelectionChangedListener<BaseModel>() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
					GWT.log("esxNodeBox selected chaged");
					
					isNeedTriggerFocusEvent = false;
					WizardContext context = WizardContext.getWizardContext();
					context.mask();
					context.cleanESXResourcePool();
					
					count = 4;
					getESXDatastore();
					getNetworkType();
					getNetworkConnection();
					getVirtualizationSuppoortInformation();
					
				}
			};
		}
		
		return esxNodeSelectedChangeHandler;
		

	}
	
	private void handleFail() {
		if(!isDealwithFailed) {
			isDealwithFailed = true;
			WizardContext context = WizardContext.getWizardContext();
			context.unmask();
			context.onValidatingCompleted(false);
		}
	}
	
	private void handleFail(String debugID, String msg) {
		handleFail();
		WizardContext.getWizardContext().showMessageBox(debugID, msg);
	}
	
	//If failed to validate the username and password, show the virtualization panel
	private void handleUI() {
		lastHost.clean();
		WizardContext.getWizardContext().showSettingTab(0);
	}

	private void decreaseCount() {
		synchronized(obj) {
			count--;
			if(count<=0) {
				GWT.log("Call the setVirtualMachineActive method");
				
				WizardContext context = WizardContext.getWizardContext();
				context.setVirtualMachineActive(true);
				context.unmask();
				
				if(processVCMJobscriptsCallBackType == 0) {
					context.onValidatingCompleted(true);
				}
				else if(processVCMJobscriptsCallBackType == 1) {
					//call from click the virtual machine panel
					context.showSettingTab(1);
				}
				else if(processVCMJobscriptsCallBackType == 2) {
					//call from the save button
					if(isSelectHyperV() || this.isForRemoteVCM){
						context.validateVCMSettings(false);
					}
					else{
						context.mask();
						connectToMonitor();
					}
					
				}
				
			}
		}
	}
	
	private VirtualizationHost getCurrentHost() {
		VirtualizationHost currentHost = null;
		if(isSelectHyperV()) {
			currentHost = hyperVPanel.getCurrentHost();
		}
		else {
			currentHost = vmwarePanel.getCurrentHost();
		}
		return currentHost;
	}
	
	protected boolean isVirtualizationHostChange() {
		VirtualizationHost currentHost = getCurrentHost();
		boolean result = lastHost.equals(currentHost);
		return (!result);
	}
	
	private void getVMWareVirtualType() {
		final WizardContext context = WizardContext.getWizardContext();
		final String hostName=context.getVMwareHost();
		final String userName=context.getVMwareUsername();
		final String password=context.getVMwarePassword();
		final String protocol=context.getVMwareProtocol();
		final int port=context.getVMwarePort();
		
		service.getVMwareServerType(hostName, userName, password,
				protocol,port,new BaseAsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						handleFail("{453C4192-A760-4859-B993-CAB929A4274A}",
								UIContext.Constants.coldStandbySettingVirtualizationFailedGetESXServerType());
						
					}

					@Override
					public void onSuccess(Integer result) {
						GWT.log("Succesfully getVMwareServerType:"+result);
						setVMWareType(result);
						decreaseCount();
						
					}

				});
	}
	
	private void getESXServerVersion() {
		final WizardContext context = WizardContext.getWizardContext();
		final String hostName=context.getVMwareHost();
		final String userName=context.getVMwareUsername();
		final String password=context.getVMwarePassword();
		final String protocol=context.getVMwareProtocol();
		final int port=context.getVMwarePort();
		
		service.getESXServerVersion(hostName, userName, password,
				protocol,port, new BaseAsyncCallback<String>(){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						handleFail("{30DC8571-515C-49f6-8314-1312A753ACF2}",
								UIContext.Constants.coldStandbySettingVirtualizationFailedGetESXVersion());
					}

					@Override
					public void onSuccess(String result) {
						GWT.log("Successfully getESXServerVersion:"+result);
						setVMWareVersion(result);
						decreaseCount();
					}
		});
	}
	
	private void getESXDatastore(){
		GWT.log("Entry getESXDatastore()");
		
		BaseModel esxNode = WizardContext.getWizardContext().getESXHostModel();
		String dataCenter= (String)esxNode.get("dataCenter");
		String esxName= (String)esxNode.get("esxNode");

		service.getVmStorages(WizardContext.getWizardContext().getVMwareHost(),
				WizardContext.getWizardContext().getVMwareUsername(),
				WizardContext.getWizardContext().getVMwarePassword(),
				WizardContext.getWizardContext().getVMwareProtocol(),
				true,
				WizardContext.getWizardContext().getVMwarePort(),
				esxName,
				dataCenter,
				null,
				new BaseAsyncCallback<VMStorage[]>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to getESXDatastore()");
				handleFail();
			}

			@Override
			public void onSuccess(VMStorage[] result) {
				GWT.log("Successfylly getESXDatastore()");
				for(int i=0;i<result.length;i++){
					GWT.log("getESXDatastore():get the ESX Datastore name:"+result[i].getName());
				}
				
				WizardContext.getWizardContext().setESXDatastore(result);
				
				decreaseCount();
	
			}
		});
	}
	
	private void getNetworkConnection() {
		GWT.log("Entry getNetworkConnection()");
		
		if (!isSelectHyperV()) {
			final BaseModel esxNodeModel = WizardContext.getWizardContext().getESXHostModel();
			service.getESXNodeNetworkConnections(
					WizardContext.getWizardContext().getVMwareHost(),
					WizardContext.getWizardContext().getVMwareUsername(),
					WizardContext.getWizardContext().getVMwarePassword(),
					WizardContext.getWizardContext().getVMwareProtocol(),
					WizardContext.getWizardContext().getVMwarePort(),
					esxNodeModel, new BaseAsyncCallback<String[]>(UIContext.productNameVCM) {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							GWT.log("ESX Failed to getESXNodeNetworkConnections()");
							
							handleFail();
						}

						@Override
						public void onSuccess(String[] result) {
							GWT.log("ESX Successfully getESXNodeNetworkConnections()");
							WizardContext.getWizardContext().setAdapterConnection(result);
							decreaseCount();
						}

					});
		}
		else {
			service.getHypervNetworks("","","",
					//WizardContext.getWizardContext().getHypervHost(), 
					//WizardContext.getWizardContext().getHypervUsername(), 
					//WizardContext.getWizardContext().getHypervPassword(),
					new BaseAsyncCallback<String[]>(UIContext.productNameVCM) {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("HyperV failed to getHyperVNetworkConnections()");
					super.onFailure(caught);
					
					handleFail();
				}

				@Override
				public void onSuccess(String[] result) {
					GWT.log("HyperV Successfully getHyperVNetworkConnections()");
					WizardContext.getWizardContext().setAdapterConnection(result);
					decreaseCount();
				}

			});
		}
	}
	
	private void getHyperVVolumes() {
		//check the volume valid, browse the monitor volumes
		final WizardContext context = WizardContext.getWizardContext();
		loginService.getVolumesWithDetails(1, null, null, null, new BaseAsyncCallback<List<FileModel>>(UIContext.productNameVCM) {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to get the HyperV volumes");
				super.onFailure(caught);
				
				handleFail();
			}

			@Override
			public void onSuccess(List<FileModel> result) {
				GWT.log("Successfully getHyperVVolumes");
			
				context.setHyperVVolumes(result);
				decreaseCount();
			}
		});
	}
	private void getNetworkType() {

		GWT.log("Entry getNetworkType()");

		if (!isSelectHyperV()) {

			final BaseModel esxNodeModel = WizardContext.getWizardContext().getESXHostModel();
			service.getESXNodeNetworkAdapterTypes(WizardContext.getWizardContext().getVMwareHost(), 
					WizardContext.getWizardContext().getVMwareUsername(), 
					WizardContext.getWizardContext().getVMwarePassword(), 
					WizardContext.getWizardContext().getVMwareProtocol(),
					WizardContext.getWizardContext().getVMwarePort(),
					esxNodeModel, new BaseAsyncCallback<String[]>(UIContext.productNameVCM) {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("ESX Failed to getESXNodeNetworkAdapterTypes()");
							super.onFailure(caught);
							
							handleFail();
							
						}

						@Override
						public void onSuccess(String[] result) {
							GWT.log("ESX Successfully getESXNodeNetworkAdapterTypes()");
							
							WizardContext.getWizardContext().setAdapterType(result);
							
							decreaseCount();
							
						}
					});

		} else {
			service.getHypervNetworkAdapterTypes(new BaseAsyncCallback<String[]>(UIContext.productNameVCM){
				
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("HyperV Failed to getHypervNetworkAdapterTypes()");
					super.onFailure(caught);
					
					handleFail();
				}

				@Override
				public void onSuccess(String[] result) {
					GWT.log("HyperV Successfully getHypervNetworkAdapterTypes()");
					WizardContext.getWizardContext().setAdapterType(result);
					
					decreaseCount();
				
				}
			});
			
		}
	}

	private void getVirtualizationSuppoortInformation() {
			
			GWT.log("Entry getVirtualizationSuppoortInformation()");
	
			if (isSelectHyperV()) {
				service.getHyperVSupportedInfo(
						"","","",
						/*WizardContext.getWizardContext().getHyperVHost(),
						WizardContext.getWizardContext().getHyperVUsername(), 
						WizardContext.getWizardContext().getHyperVPassword(),*/
						new BaseAsyncCallback<ESXServerInfo>() {
							@Override
							public void onFailure(Throwable caught) {
								onFailGetSupportInformation(caught);
							}
	
							@Override
							public void onSuccess(ESXServerInfo result) {
								GWT.log("Succesfully getHyperVSupportedInfo");
								WizardContext.getWizardContext().setSupportInformation(result);
								decreaseCount();
							}
						});
			} else {
				service.getESXNodeSupportedInfo(WizardContext.getWizardContext()
						.getVMwareHost(), WizardContext.getWizardContext()
						.getVMwareUsername(), WizardContext.getWizardContext()
						.getVMwarePassword(), WizardContext.getWizardContext()
						.getVMwareProtocol(), WizardContext.getWizardContext()
						.getVMwarePort(), WizardContext.getWizardContext().getESXHostModel(),
						new BaseAsyncCallback<ESXServerInfo>() {
							@Override
							public void onFailure(Throwable caught) {
								onFailGetSupportInformation(caught);
							}
	
							@Override
							public void onSuccess(ESXServerInfo result) {
								GWT.log("Succesfully getESXNodeSupportedInfo");
								WizardContext.getWizardContext().setSupportInformation(result);
								decreaseCount();
							}
						});
			}
		}

	private void onFailGetSupportInformation(Throwable caught) {
	
		GWT.log("Entry onFailGetSupportInformation()");
		
		if (caught instanceof BusinessLogicException
				&& ((BusinessLogicException) caught).getErrorCode().equals(
						"4294967303")) {
			MessageBox messageBox = new MessageBox();
			messageBox.setButtons(MessageBox.YESNO);
			messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameVCM));
			messageBox.setMessage(UIContext.Constants.coldStandbySettingVMFailedGetInfo());
			messageBox.setIcon(MessageBox.ERROR);
			messageBox.setModal(true);
			messageBox.addCallback(new Listener<MessageBoxEvent>() {
	
				@Override
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
						getVirtualizationSuppoortInformation();
					} 
					else {
						handleFail();
					}
						
				}
	
			});
			Utils.setMessageBoxDebugId(messageBox);
			messageBox.show();
	
		}
		else {
			handleFail();
		}
	}

	
	
	private LayoutContainer setupVirtulizationType() {
		LayoutContainer virtulizationTypeContainer = new LayoutContainer();	
		TableLayout tl = new TableLayout();
		tl.setColumns(3);
		virtulizationTypeContainer.setLayout(tl);
		
		Label titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationType()); 
		virtulizationTypeContainer.add(titleLabel);
		
		RadioGroup rgVirtualizationType = new RadioGroup();
		radioVMware = new Radio(); //new RadioButton("VirtualizationType");
		rgVirtualizationType.add(radioVMware);
		radioVMware.ensureDebugId("36772876-44c7-428b-b6d0-b2114731a533");
//		radioVMware.setStyleName("panel-text-value"); 
		radioVMware.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		radioVMware.setBoxLabel(UIContext.Constants.coldStandbySettingVirtualizationTypeVMware()); 
		radioVMware.setValue(true);
		
		radioHyperV = new Radio(); //new RadioButton("VirtualizationType"); 
		rgVirtualizationType.add(radioHyperV);
		radioHyperV.ensureDebugId("3e234718-745c-4358-8f6b-ffe8d39c8f8f");
		radioHyperV.setBoxLabel(UIContext.Constants.coldStandbySettingVirtualizationHyperV()); 
//		radioHyperV.setStyleName("panel-text-value"); 
		radioHyperV.getElement().getStyle().setPaddingLeft(4, Unit.PX);
		
		
		rgVirtualizationType.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (radioVMware.getValue()) {
					renderVMwareVPanel();
				} else {
					renderHyperVPanel();
				}

			}
		});
		
		virtulizationTypeContainer.add(radioVMware);
		virtulizationTypeContainer.add(radioHyperV);
		
		return virtulizationTypeContainer;
	}
	
	private void renderHyperVPanel(){
		virtualServerLayout.removeAll();
		
		if(hyperVPanel == null) {
			hyperVPanel = new VirtualizationHyperVPanel();
			hyperVPanel.ensureDebugId("e83245f3-b923-482d-8f7d-95d4771e856b");
		}
		virtualServerLayout.add(hyperVPanel);
		virtualServerLayout.layout();
		virtualServerLayout.repaint();
		
		vmwarePanel.setVisible(false);
		monitorDisclosurePanel.setVisible(false);
		monitorPanel.setESXProxyVisible(false);
		monitorPanel.setEnabled(false);
		monitorPanel.setInputPanelVisible( false );
		monitorPanel.setMessagePanelVisiable( true, VirtualizationType.HyperV );
	}
	
	private void renderVMwareVPanel(){
		virtualServerLayout.removeAll();
		
		if (vmwarePanel == null){
			vmwarePanel = new VirtualizationVMWarePanel();
			vmwarePanel.ensureDebugId("daa5f7ef-a13a-489f-859e-e4abb998a009");
			setESXNode(vmwarePanel);
		}
		virtualServerLayout.add(vmwarePanel);
		virtualServerLayout.layout();
		virtualServerLayout.repaint();
		
		vmwarePanel.setVisible(true);
		monitorDisclosurePanel.setVisible(!this.isForRemoteVCM);
		monitorPanel.setEnabled(true);
		monitorPanel.setESXProxyVisible(true);
		monitorPanel.setInputPanelVisible( !this.isForRemoteVCM );
		monitorPanel.setMessagePanelVisiable( this.isForRemoteVCM, VirtualizationType.VMwareESX );
	}
	
	protected VirtualizationType getVirtulizationType(){
		if (radioHyperV.getValue())
			return VirtualizationType.HyperV;
		else
			return vmwarePanel.getVirtulizationType();
	}
	
	protected boolean isSelectHyperV() {
		if(radioHyperV.getValue()){
			return true;
		}
		else {
			return false;
		}
	}
	
	protected void setVMWareType(int type) {
		vmwarePanel.setVMWareType(type);
	}
	protected void setVMWareVersion(String version) {
		vmwarePanel.setVMWareVersion(version);
	}
	
	protected void populateBeatJobScript(HeartBeatJobScript script) {
		if(isSelectHyperV()) {
			script.setHeartBeatMonitorHostName(hyperVPanel.textFieldServer.getValue());
			script.setHeartBeatMonitorPort(hyperVPanel.textFieldPort.getValue().intValue());
			script.setHeartBeatMonitorUserName(hyperVPanel.textFieldUserName.getValue());
			script.setHeartBeatMonitorPassword(hyperVPanel.textFieldPassword.getValue());
			script.setHeartBeatMonitorProtocol(hyperVPanel.getProtocol());
		}
		else {
			script.setHeartBeatMonitorHostName(monitorPanel.textFieldMonitorServer.getValue());
			script.setHeartBeatMonitorPort(monitorPanel.textFieldPort.getValue().intValue());
			script.setHeartBeatMonitorUserName(monitorPanel.textFieldUserName.getValue());
			script.setHeartBeatMonitorPassword(monitorPanel.textFieldPassword.getValue());
			script.setHeartBeatMonitorProtocol(monitorPanel.getProtocol());
		}

		
	}
	
	protected void populateFailoverJobScript(FailoverJobScript failoverScript){
		if (radioHyperV.getValue())
			hyperVPanel.populateFailoverJobScript(failoverScript);
		else{
			vmwarePanel.populateFailoverJobScript(failoverScript);
		}
	}
	protected void setDefaultVCMSettings() {
		VirtualizationHost currentHost = getCurrentHost();
		lastHost.copyVirtualHost(currentHost);
		
		if(oldFailoverJobScript == null)
			return;
		
		Virtualization virtualization = oldFailoverJobScript.getFailoverMechanism().get(0);
		if(virtualization == null)
			return;
		
		VirtualizationType vType = oldFailoverJobScript.getVirtualType();
		List<DiskDestination> listDestinations = null;
		List<NetworkAdapter> listAdapters = null;
		if( vType == VirtualizationType.VMwareESX) {
			VMwareESX vmwareESX = (VMwareESX)virtualization;
			listDestinations = vmwareESX.getDiskDestinations();
			listAdapters = vmwareESX.getNetworkAdapters();
			
			BaseModel model = new BaseModel();
			model.set("dataCenter", vmwareESX.getDataCenter());
			model.set("esxNode", vmwareESX.getEsxName());
			esxNodeBox.getStore().add(model);
			esxNodeBox.setValue(model);
		}
		else if(vType == VirtualizationType.VMwareVirtualCenter) {
			VMwareVirtualCenter vCenter = (VMwareVirtualCenter)virtualization;
			listDestinations = vCenter.getDiskDestinations();
			listAdapters = vCenter.getNetworkAdapters();
			
			BaseModel model = new BaseModel();
			model.set("dataCenter", vCenter.getDataCenter());
			model.set("esxNode", vCenter.getEsxName());
			esxNodeBox.getStore().add(model);
			esxNodeBox.setValue(model);
		}
		else if(vType == VirtualizationType.HyperV) {
			HyperV hyprV = (HyperV)virtualization;
			listDestinations = hyprV.getDiskDestinations();
			listAdapters =  hyprV.getNetworkAdapters();
		}
		
		if((listAdapters ==null)||(listDestinations ==null)) {
			return;
		}
		
		ArrayList<String> adapterConnection = new ArrayList<String>();
		ArrayList<String> adapterType = new ArrayList<String>();
		ArrayList<VMStorage> vmStorages = new ArrayList<VMStorage>();
		//get the network connections
		for (NetworkAdapter adapter : listAdapters) {
			String connection = adapter.getNetworkLabel();
			boolean isFound = false;
			for (String str : adapterConnection) {
				if(str.equals(connection)) {
					isFound = true;
					break;
				}
			}
			if(!isFound) {
				adapterConnection.add(connection);
			}
		}
		
		//get the network type
		for (NetworkAdapter adapter : listAdapters) {
			String type= adapter.getAdapterType();
			boolean isFound = false;
			for (String str : adapterType) {
				if(str.equals(type)) {
					isFound = true;
					break;
				}
			}
			if(!isFound) {
				adapterType.add(type);
			}
		}
		
		//get the datastore for ESX or VC
		for (DiskDestination destination : listDestinations) {
			VMStorage oldVMStorage = destination.getStorage();
			boolean isFound = false;
			for (VMStorage vmStorage : vmStorages) {
				if(vmStorage.getName().endsWith(oldVMStorage.getName())) {
					isFound = true;
					break;
				}
			}
			if(!isFound) {
				vmStorages.add(oldVMStorage);
			}
		}
		
		ESXServerInfo esxServerInfo = new ESXServerInfo();
		esxServerInfo.setCpuCount(virtualization.getVirtualMachineProcessorNumber());
		long memorySizeBytes = ((long)virtualization.getMemorySizeInMB())*1024*1024;
		esxServerInfo.setMemorySize(memorySizeBytes);
		
		String[] EMPTY = new String[0];
		WizardContext context = WizardContext.getWizardContext();
		context.setAdapterConnection(adapterConnection.toArray(EMPTY));
		context.setAdapterType(adapterType.toArray(EMPTY));
		context.setSupportInformation(esxServerInfo);
		if(vType!=VirtualizationType.HyperV) {
			VMStorage[] EMPTY_STORAGE = new VMStorage[0];
			context.setESXDatastore(vmStorages.toArray(EMPTY_STORAGE));
		}
		context.setVirtualMachineActive(false);

	}
	protected void populateUI(FailoverJobScript failoverScript, HeartBeatJobScript heartBeatJobScript,
			ReplicationJobScript replicationJobScript, boolean isForEdge){
		
		oldFailoverJobScript = failoverScript;
		processVCMJobscriptsCallBackType = 0;
		if (failoverScript.getVirtualType() == VirtualizationType.HyperV){
			radioHyperV.setValue(true);
			renderHyperVPanel();
			hyperVPanel.populateUI(failoverScript, replicationJobScript);
			monitorPanel.populateUI(heartBeatJobScript,replicationJobScript);
			if(isForEdge)
				connectVirtualizationServer();
			else
				setDefaultVCMSettings();
		}else{
			radioVMware.setValue(true);
			renderVMwareVPanel();
			vmwarePanel.populateUI(failoverScript);
			//populateESXNode(failoverScript);
			monitorPanel.populateUI(heartBeatJobScript,replicationJobScript);
			
			if(isForEdge)
				connectVirtualizationServer();
			else
				setDefaultVCMSettings();
		}
		
	}

	
	public void populateReplicationJobScript(ReplicationJobScript replicationScript) {
		if (radioHyperV.getValue()){
			hyperVPanel.populateReplicationJobScript(replicationScript);
		}
		else{
			vmwarePanel.populateReplicationJobScript(replicationScript);
		}
		
		if (this.isForRemoteVCM)
		{
			ReplicationDestination repDest = replicationScript.getReplicationDestination().get( 0 );
			repDest.setProxyEnabled( false );
		}
	}
	
	

}
