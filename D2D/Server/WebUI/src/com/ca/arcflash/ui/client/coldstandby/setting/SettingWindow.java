package com.ca.arcflash.ui.client.coldstandby.setting;

import java.util.ArrayList;
import java.util.List;

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
import com.ca.arcflash.ui.client.coldstandby.VCMMessages;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.exception.ClientException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.ui.client.model.VCMDataStoreModel;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.MessageBox.MessageBoxType;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


public class SettingWindow extends Window {
	private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	private final CommonServiceAsync commonService = GWT.create(CommonService.class);
	private static final int HEIGHT = 550;
	private static final int WIDTH = 700;
	private static final int HEIGHT_BUTTON_PANEL = 40;
	private static final int WIDTH_BUTTON = 80;
	private static final int HEIGHT_TITLE_PANEL = 50;

	private SettingWindow thisWindow;
	private WizardPage[] pages = new WizardPage[5];
	private BorderLayout rootBorderLayout;
	private BorderLayoutData centerData;
	private ScrollPanel centerScrollPanel;
	private Button cancelButton;
	private Button nextButton;
	private Button previousButton;
	private Label titleLabel;
	private Label descriptionLabel;

	private VirtualizationPanel virtualizationPanel = new VirtualizationPanel();
	private StandinPanel standinPanel = new StandinPanel();
	private VirtualMachinePanel virtualMachinePanel = new VirtualMachinePanel();
	private EmailPanel	emailPanel =new EmailPanel();
	private SummaryPanel summaryPanel=new SummaryPanel();

	private int currentIndex;
	
	private Timer timer;

	public SettingWindow() {
		this.thisWindow = this;
		this.setResizable(false);
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setHeadingHtml(UIContext.Constants.coldStandbySettingTitle());
		this.ensureDebugId("adffac07-66d4-4675-8bc1-c2cc0f23bbb0");

		rootBorderLayout = new BorderLayout();
		setLayout(rootBorderLayout);

		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH,
				HEIGHT_TITLE_PANEL);
		add(setupTitlePanel(), northData);

		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,
				HEIGHT_BUTTON_PANEL);
		add(setupButtonPanel(), southData);

		centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0));

		centerScrollPanel = new ScrollPanel();
		centerScrollPanel.ensureDebugId("a3d03a91-63d2-4f56-a6c0-d3a23e9eb065");
		centerScrollPanel.getElement().getStyle().setBackgroundColor("white");
		centerScrollPanel.getElement().getStyle().setPadding(8, Unit.PX);
		add(centerScrollPanel, centerData);

		pages[0] = virtualizationPanel;
		pages[1] = standinPanel;
		pages[2] = virtualMachinePanel;
		pages[3] = emailPanel;
		pages[4] = summaryPanel;

		virtualizationPanel
				.ensureDebugId("358700f3-10e5-4d97-a804-70b93ee2e02b");
		standinPanel.ensureDebugId("8a9bc966-7238-4bd1-bb31-ed22dc657480");
		virtualMachinePanel
				.ensureDebugId("00165c36-ff38-4d25-905c-b6dab93a3867");
		// vmAssurePanel.ensureDebugId("6ee3bc87-4f29-43c8-983c-88ccb1a865b2");

		WizardContext.getWizardContext().setVirtulizationPanel(
				virtualizationPanel);
		WizardContext.getWizardContext().setStandingPanel(
				(StandinPanel) standinPanel);
		WizardContext.getWizardContext().setVirtualMachinePanel(virtualMachinePanel);
		WizardContext.getWizardContext().setSummaryPanel(summaryPanel);
		WizardContext.getWizardContext().setParentWindow(this);

		startTimerAvoidSessionTimeout();
		
		setActivePage(0);
		
	}


	private Widget setupTitlePanel() {
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setTableWidth("100%");
		verticalPanel.setStyleAttribute("padding", "8px");
		verticalPanel.setStyleAttribute("background-color", "white");
		verticalPanel.setWidth("100%");

		titleLabel = new Label(UIContext.Constants
				.coldStandbySettingVirtualizationTitle());
		titleLabel.ensureDebugId("d618985f-b179-4c28-8f73-2497ec306149");
		titleLabel.setStyleName("coldStandbySettingTitle");
		verticalPanel.add(titleLabel);

		descriptionLabel = new Label(UIContext.Constants
				.coldStandbySettingVirtualizationDescription());
		descriptionLabel.ensureDebugId("31cd16f3-f092-4216-b573-ab64c5e0ad92");
		descriptionLabel.setStyleName("coldStandbySettingDescription");
		verticalPanel.add(descriptionLabel);

		return verticalPanel;
	}

	@Override
	public void render(Element target, int index) {
		super.render(target, index);
		
		thisWindow.mask(UIContext.Constants.loadingIndicatorText());
		
		service.getBackupConfiguration(new BaseAsyncCallback<BackupSettingsModel>(){
			
			@Override
			public void onFailure(Throwable caught) {
				thisWindow.unmask();
				thisWindow.hide();
				super.onFailure(caught);
			}
			@Override
			public void onSuccess(BackupSettingsModel result) {
				// TODO Auto-generated method stub
				if (result==null)
				{
					thisWindow.unmask();
					
					MessageBox messageBox = new MessageBox();
					messageBox.getDialog().ensureDebugId("58bc28a3-da8b-4eff-a97d-bb3bc86fa02e");
					messageBox.setIcon(MessageBox.WARNING);
					messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameVCM));
					messageBox.setMessage(UIContext.Constants.coldStandbySettingConfigureBackup());
					messageBox.setModal(true);
					messageBox.addCallback(new Listener<MessageBoxEvent>() {

						@Override
						public void handleEvent(MessageBoxEvent be) {
							// TODO Auto-generated method stub
							thisWindow.hide();
						}
						
					});
					messageBox.show();
					
				}
				else{
					//full node backup, don't check the whether selected system or bootable volumes.
					if(result.getBackupVolumes().getIsFullMachine()){
						populateUI();
					}
					else{
						MessageBox messageBox = new MessageBox();
						messageBox.getDialog().ensureDebugId("148b80f0-00a2-4929-90de-ac3a12afae1e");
						messageBox.setIcon(MessageBox.WARNING);
						messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameVCM));
						messageBox.setMessage(UIContext.Messages.coldStandbySettingConfigureBackupFullMachine(UIContext.productNameD2D));
						messageBox.addCallback(new Listener<MessageBoxEvent>() {

							@Override
							public void handleEvent(MessageBoxEvent be) {
								// TODO Auto-generated method stub
								thisWindow.hide();
							}
							
						});
						messageBox.show();
						
						
						/*final List<String> backupConfigVolumes=result.getBackupVolumes().selectedVolumesList;
						
						service.getVolumesWithDetails(result.getDestination(), result.getDestUserName(), 
								result.getDestPassword(), new BaseAsyncCallback<List<FileModel>>() {

							@Override
							public void onFailure(Throwable caught) {
								
							}

							@Override
							public void onSuccess(List<FileModel> fileModelList) {
								
								if(fileModelList == null || fileModelList.size() == 0) 
									return;
								
								boolean isBootVolume=false;
								boolean isSystemVolume=false;
								for (FileModel volume : fileModelList) {
									VolumeModel volumeModel=(VolumeModel)volume;
									if(volumeModel==null){
										continue;
									}
									
									for(String volumeName:backupConfigVolumes){
										
										if(removeEndSlash(volume.getName()).compareToIgnoreCase(volumeName)==0)
										{
											if(VolumeSubStatus.isBootVolume(volumeModel.getSubStatus())){
												isBootVolume=true;
											}
											if(VolumeSubStatus.isBootVolume(volumeModel.getSubStatus())){
												isSystemVolume=true;
											}
										}
									}

								}
								
								if(isBootVolume&&isSystemVolume){
									populateUI();
								}
								else{
									
									MessageBox messageBox = new MessageBox();
									messageBox.getDialog().ensureDebugId("856c2186-0fea-4a2b-8c30-a4e0a0a3ff34");
									messageBox.setIcon(MessageBox.WARNING);
									messageBox.setTitle(UIContext.Constants.messageBoxTitleWarning());
									messageBox.setMessage(UIContext.Constants.coldStandbySettingConfigureBackupSysBoot());
									messageBox.addCallback(new Listener<MessageBoxEvent>() {

										@Override
										public void handleEvent(MessageBoxEvent be) {
											// TODO Auto-generated method stub
											thisWindow.hide();
										}
										
									});
									messageBox.show();
									
								}
							}
						});*/
					}


				}
			}
		});
		
	}
	
	private String removeEndSlash(String name) {
		if(name != null && (name.endsWith("\\") || name.endsWith("/")))
			name = name.substring(0, name.length() - 1);
		return name;
	}
	
	private void getHostPlatform(){
		service.isHostAMD64Platform(new BaseAsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				thisWindow.unmask();
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result){
					WizardContext.getWizardContext().addAMD64ArchTip();
				}
				
				thisWindow.unmask();
			}
		});
	}
	private void populateUI(){

		String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
		service.getJobScriptCombo(vmInstanceUUID, new BaseAsyncCallback<JobScriptCombo>() {

			@Override
			public void onFailure(Throwable caught) {
				thisWindow.unmask();
				super.onFailure(caught);
			}
			@Override
			public void onSuccess(JobScriptCombo result) {
				if (result == null || result.getFailoverJobScript() == null
						|| result.getHbJobScript() == null
						|| result.getRepJobScript() == null) {
					thisWindow.unmask();
					getHostPlatform();
					return;
				}

				virtualizationPanel.populateUI(result.getFailoverJobScript(),
						result.getRepJobScript());
				standinPanel.populateUI(result.getHbJobScript(), result.getFailoverJobScript(), 
						result.getRepJobScript());
				virtualMachinePanel.populateUI(result.getFailoverJobScript(),result.getRepJobScript());
				emailPanel.populateUI(result.getAlertJobScript());
				summaryPanel.populateUI(result.getRepJobScript());
				
				getHostPlatform();
			}

		});
	}

	private Widget setupButtonPanel() {
		LayoutContainer bottomButtonPanel = new LayoutContainer();
		HBoxLayout layout = new HBoxLayout();
		layout.setPadding(new Padding(5));
		layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		layout.setPack(BoxLayoutPack.END);
		bottomButtonPanel.setLayout(layout);

		previousButton = new Button(UIContext.Constants.restorePrevious());
		previousButton.ensureDebugId("1c6c9b73-e956-42ee-9d21-4ce5576f7c23");
		previousButton.setWidth(WIDTH_BUTTON);

		nextButton = new Button(UIContext.Constants.restoreNext());
		nextButton.ensureDebugId("da343591-d45d-4a99-ae9b-2b1e06113854");
		nextButton.setWidth(WIDTH_BUTTON);

		cancelButton = new Button(UIContext.Constants.cancel());
		cancelButton.ensureDebugId("dff1ae18-e374-4917-833b-2f833afc9efc");
		cancelButton.setWidth(WIDTH_BUTTON);

		Button helpButton = new Button(UIContext.Constants.help());
		helpButton.ensureDebugId("537d972b-fc63-4d83-abec-1d0c9503e77a");
		helpButton.setWidth(WIDTH_BUTTON);

		HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
		bottomButtonPanel.add(previousButton, layoutData);
		bottomButtonPanel.add(nextButton, layoutData);
		bottomButtonPanel.add(cancelButton, layoutData);
		bottomButtonPanel.add(helpButton, layoutData);

		previousButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						previoustPage();
					}

				});

		nextButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				nextPage();
			}

		});

		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (currentIndex == (pages.length - 1)) {
					if (pages[currentIndex].validate())
						populateScriptAndClose();
				} else {
					MessageBox messageBox = new MessageBox();
					messageBox.setButtons(MessageBox.YESNO);
					messageBox.setIcon(MessageBox.WARNING);
					messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameVCM));
					messageBox.setMessage(UIContext.Constants.coldStandbySettingExitMsg());
					messageBox.addCallback(new Listener<MessageBoxEvent>() {

						@Override
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId().equals(
									Dialog.YES))
								thisWindow.hide();
						}

					});
					messageBox.show();
				}
			}

		});

		return bottomButtonPanel;
	}

	private void populateScriptAndClose() {
		thisWindow.mask(UIContext.Constants.savingIndicatorText());
		final HeartBeatJobScript heartBeatScript = new HeartBeatJobScript();
		standinPanel.populateBeatJobScript(heartBeatScript);
		heartBeatScript.setAFGuid(ColdStandbyManager.getVMInstanceUUID());

		final FailoverJobScript failoverScript = new FailoverJobScript();
		virtualMachinePanel.populateFailoverJobScript(failoverScript);
		virtualizationPanel.populateFailoverJobScript(failoverScript);
		standinPanel.populateFailoverJobScript(failoverScript);
		failoverScript.setAFGuid(ColdStandbyManager.getVMInstanceUUID());
		if(ColdStandbyManager.getVMInstanceUUIDFromURL() != null && ColdStandbyManager.getVMInstanceUUIDFromURL().length() > 0) {
			failoverScript.setProductionServerName(UIContext.backupVM.getVmHostName());
		}
		else if(ColdStandbyManager.getVMInstanceUUID() != null && ColdStandbyManager.getVMInstanceUUID().length() > 0){
			failoverScript.setProductionServerName(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().getHostname());
		}

		final ReplicationJobScript replicationScript = new ReplicationJobScript();
		replicationScript.setJobType(JobType.Replication);
		replicationScript.setVirtualType(virtualizationPanel.getVirtulizationType());
		virtualizationPanel.populateReplicationJobScript(replicationScript);
		standinPanel.populateReplicationJobScript(replicationScript);
		virtualMachinePanel.populateReplicationJobScript(replicationScript);
		summaryPanel.populateReplicationJobScript(replicationScript);
		replicationScript.setAFGuid(ColdStandbyManager.getVMInstanceUUID());
		
		final AlertJobScript alertJobScript = new AlertJobScript();
		emailPanel.populateAlertJobScript(alertJobScript);

		final JobScriptCombo scriptCombo = new JobScriptCombo();
		scriptCombo.setFailoverJobScript(failoverScript);
		scriptCombo.setHbJobScript(heartBeatScript);
		scriptCombo.setRepJobScript(replicationScript);
		scriptCombo.setAlertJobScript(alertJobScript);
		
		saveJobScript(scriptCombo);
		
		/*service.stopHeartBeat(new BaseAsyncCallback<Void>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				//thisWindow.unmask();
				saveJobScript(scriptCombo);
			}

			@Override
			public void onSuccess(Void result) {
				saveJobScript(scriptCombo);
			}
		});*/
	}
	
	private void saveJobScript(final JobScriptCombo scriptCombo){
		service.setJobScriptCombo(scriptCombo, new BaseAsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				thisWindow.unmask();
			}

			@Override
			public void onSuccess(Void result) {
				//ColdStandbyManager.getInstance().startHeartBeatStateTimer();
				//ColdStandbyManager.getInstance().fireSetingChangedEvent(scriptCombo);
				//thisWindow.hide();
				startHeartBeat();
			}
		});
	}
	
	private void startHeartBeat(){
		final String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
		service.startHeartBeat(vmInstanceUUID, new BaseAsyncCallback<Void>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				thisWindow.unmask();
			}

			@Override
			public void onSuccess(Void result) {
				thisWindow.hide();
				if(WizardContext.getWizardContext().isStartOfflineCopyNow()){
					service.startReplication(vmInstanceUUID, new BaseAsyncCallback<Void>(){

						@Override
						public void onSuccess(Void result) {
							Info.display(UIContext.Constants.successful(), VCMMessages.coldStandbyOfflineCopyCommandNowResult()); 
						}
						
					});
				}
				
			}
		});

	}

	private void previoustPage() {
		if (currentIndex > 0)
			setActivePage(currentIndex - 1);
	}

	private void nextPage() {
		if (!pages[currentIndex].validate())
			return;

		if (currentIndex == 0) {
			checkVirtualizationValid();
		}
		
		else if(currentIndex==1){
			checkMonitorServerValid();
		}
		
		else if(currentIndex==2){
			checkVMNameExist();
		}
		
		else if(currentIndex ==3) {
			checkEmailSetting();
		}

		else if(currentIndex==pages.length-1){
				populateScriptAndClose();
		}
		else {

			if (currentIndex < (pages.length - 1))
				setActivePage(currentIndex + 1);

		}

	}
	

	private void setActivePage(int index) {
		
		pages[index].activate();
		titleLabel.setText(pages[index].getTitle());
		descriptionLabel.setText(pages[index].getDescription());

		centerScrollPanel.clear();
		centerScrollPanel.add(pages[index]);
		currentIndex = index;

		if (currentIndex == (pages.length - 1)) {
			cancelButton.setText(UIContext.Constants.finish());
			nextButton.disable();
		} else if (currentIndex == 0) {
			cancelButton.setText(UIContext.Constants.cancel());
			previousButton.disable();
		} else {
			cancelButton.setText(UIContext.Constants.cancel());
			previousButton.enable();
			nextButton.enable();
		}
	}
	
	private void failedConnectESXServer(){
		WizardContext.getWizardContext().unmask();
		
		String msgTitle=UIContext.Constants.coldStandbySettingTitle();
		String msg=UIContext.Constants.coldStandbySettingVirtualizationFailedToConnectVM();
		
		MessageBox messageBox = new MessageBox();
		messageBox.getDialog().ensureDebugId("de0b4dce-73c1-4e79-8edb-3ab0ff33526d");
		messageBox.setMinWidth(200);
		messageBox.setType(MessageBoxType.ALERT);
		messageBox.setIcon(MessageBox.ERROR);
		messageBox.setTitleHtml(msgTitle);
		Button button= messageBox.getDialog().getButtonById(Dialog.OK);
		if(button!=null){
			button.ensureDebugId("f82d7f11-830a-4e8f-b990-a0334ec5c68c");
		}
		messageBox.setMessage(msg);
		messageBox.show();
	}
	private void checkVirtualizationValid(){
		
		if(virtualizationPanel.isSelectHyperV())
		{
			setActivePage(currentIndex+1);
		}
		else {
			
			final String hostName=WizardContext.getWizardContext().getVMwareHost();
			final String userName=WizardContext.getWizardContext().getVMwareUsername();
			final String password=WizardContext.getWizardContext().getVMwarePassword();
			final String protocol=WizardContext.getWizardContext().getVMwareProtocol();
			final int port=WizardContext.getWizardContext().getVMwarePort();
			WizardContext.getWizardContext().mask();

			service.getVMwareServerType(hostName, userName, password,
					protocol,port,new BaseAsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							failedConnectESXServer();
							
						}

						@Override
						public void onSuccess(Integer result) {
							GWT.log("Succesfully getVMwareServerType:"+result);
							virtualizationPanel.setVMWareType(result);
							
								service.getESXServerVersion(hostName, userName, password,
										protocol,port, new BaseAsyncCallback<String>(){

											@Override
											public void onFailure(Throwable caught) {
												super.onFailure(caught);
												failedConnectESXServer();
											}

											@Override
											public void onSuccess(String result) {
												GWT.log("Successfully getESXServerVersion:"+result);
												
												virtualizationPanel.setVMWareVersion(result);
												WizardContext.getWizardContext().unmask();
												setActivePage(currentIndex + 1);
											}
								});
						}

					});
		}
	}
	
	private void checkMonitorServerValid(){
		WizardContext.getWizardContext().mask();
		
		final String monitorServer=WizardContext.getWizardContext().getMonitorServer();
		final int port=WizardContext.getWizardContext().getMonitorPort();
		final String protocol=WizardContext.getWizardContext().getMonitorProtocol();
		
		//TestMonitorConnectionCallback
		service.testMonitorConnection(monitorServer,port,
									 ConnectionProtocol.string2Protocol(protocol),
									 WizardContext.getWizardContext().getMonitorUsername(),
									 WizardContext.getWizardContext().getMonitorPassword(),
									 false, new BaseAsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught) {
				
				WizardContext.getWizardContext().unmask();
				String errorMessage = null;
				if (caught instanceof ServiceConnectException)
					errorMessage = UIContext.Messages.testMonitorConnectionError(
							monitorServer, port , protocol);
				else
					errorMessage = ((ClientException) caught).getDisplayMessage();

				MessageBox messageBox = new MessageBox();
				messageBox.getDialog().ensureDebugId("84e3cfdb-947e-47d2-8923-933ba088a668");
				messageBox.setMinWidth(200);
				messageBox.setType(MessageBoxType.ALERT);
				messageBox.setIcon(MessageBox.ERROR);
				messageBox.setTitleHtml(UIContext.Constants.failed());
				messageBox.setMessage(errorMessage);
				messageBox.show();
				
			}

			@Override
			public void onSuccess(String result) {
				checkIsHyperVRoleInstalled();
			}
		});
	}

	private void showMessageBox(String debugID, String errorMsg){
		MessageBox messageBox = new MessageBox();
		messageBox.getDialog().ensureDebugId(debugID);
		messageBox.setMinWidth(200);
		messageBox.setType(MessageBoxType.ALERT);
		messageBox.setIcon(MessageBox.ERROR);
		messageBox.setTitleHtml(UIContext.Constants.failed());
		messageBox.setMessage(errorMsg);
		messageBox.setModal(true);
		messageBox.show();
	}
	
	private void checkEmailSetting() {
		if(emailPanel.validate()) {
			if(emailPanel.checkMailServer()) {
				setActivePage(currentIndex+1);
			}
			else {
				String msg = UIContext.Constants.mustConfigEmailSettins();
				showMessageBox("75600817-8e14-42b4-a745-b58502de40b9",msg );
			}
		}
		
	}
	private void checkIsHyperVRoleInstalled(){
		if(WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.HyperV){
			
			service.isHyperVRoleInstalled(
					WizardContext.getWizardContext().getMonitorServer(),
					WizardContext.getWizardContext().getMonitorProtocol(),
					WizardContext.getWizardContext().getMonitorPort(),
					new BaseAsyncCallback<Boolean>(){
				@Override
				public void onFailure(Throwable caught) {
					
					WizardContext.getWizardContext().unmask();
					showMessageBox("681d47cd-2eeb-44ec-b771-bbd0cdf79143", caught.getMessage());
					
				}

				@Override
				public void onSuccess(Boolean result) {
					WizardContext.getWizardContext().unmask();
					
					if(result){
						setActivePage(currentIndex+1);
					}else {
						showMessageBox("75600817-8e14-42b4-a745-b58502de40b5", UIContext.Constants.coldStandbySettingIsHyperVRoleInstalled());
					}
					
				}
			});
		}
		else {
			WizardContext.getWizardContext().unmask();
			setActivePage(currentIndex+1);
		}
	}
	
	private void checkVMNameExist(){
		final WizardContext context = WizardContext.getWizardContext();
		final String vmName = context.getVMName();
		
		context.mask();
		if(context.isVirtualizationHostChanged()){
			
			if(context.getVirtulizationType()==VirtualizationType.HyperV){
				service.isHyperVVMNameExist(vmName, new BaseAsyncCallback<Boolean>(){
					
						@Override
						public void onFailure(Throwable caught) {
							checkVirtualMachineValid();
						}

						@Override
						public void onSuccess(Boolean result) {
							if(result){
								context.unmask();
								String errorMsg = UIContext.Messages.coldStandbyVMNameExist(vmName, context.getHypervHost());
								showMessageBox("c7c469d2-a72f-4283-8702-8811e2f3ffdb", errorMsg);
							}
							else{
								checkVirtualMachineValid();
							}
							
						}
				});
			}
			else{
				final String dcName = (String)context.getESXHostModel().get("dataCenter");
				final String esxName = (String)context.getESXHostModel().get("esxNode");
				service.isVMWareVMNameExist(
						context.getVMwareHost(),
						context.getVMwareUsername(),
						context.getVMwarePassword(),
						context.getVMwareProtocol(),
						true,
						context.getVMwarePort(),
						esxName, dcName, vmName, new BaseAsyncCallback<Boolean>(){
							@Override
							public void onFailure(Throwable caught) {
								checkVirtualMachineValid();
							}

							@Override
							public void onSuccess(Boolean result) {
								if(result){
									context.unmask();
									String errorMsg = UIContext.Messages.coldStandbyVMNameExist(vmName, context.getVMwareHost());
									showMessageBox("d362dbba-cf79-4d4a-b573-d577582230c4", errorMsg);
								}
								else{
									checkVirtualMachineValid();
								}
								
							}
						});
			}
		}
		else{
			checkVirtualMachineValid();
		}
	}
	private void checkVirtualMachineValid(){
		
		if(WizardContext.getWizardContext().getVirtulizationType()!=VirtualizationType.HyperV)
		{
			WizardContext.getWizardContext().unmask();
			setActivePage(currentIndex+1);
		}
		else {
			
			//String pathHeaderString="\\\\"+WizardContext.getWizardContext().getHypervHost();
			ListStore<VCMDataStoreModel> dataStore= WizardContext.getWizardContext().getVMDisk();
			List<String> hyperVPathList=new ArrayList<String>();
			
			for(int i=0;i<dataStore.getCount();i++){
				String path=dataStore.getAt(i).getHyperVPath().getValue();
				if((path==null)||path.isEmpty())
					continue;
				
				//coldStandbySettingHypervPathInvalid
				if((path.length()>2)&&(path.charAt(0)=='\\')&&(path.charAt(1)=='\\')){
					
						WizardContext.getWizardContext().unmask();
						MessageBox messageBox = new MessageBox();
						messageBox.setMinWidth(200);
						messageBox.setType(MessageBoxType.ALERT);
						messageBox.setIcon(MessageBox.ERROR);
						String strMsg=UIContext.Constants.coldStandbySettingHypervPathInvalid();
						messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameVCM));
						messageBox.setMessage(strMsg);
						messageBox.show();
						return;
						
					}
					else{
						Boolean isFound=false;
						for(int j=0;j<hyperVPathList.size();j++){
							if(hyperVPathList.get(j).compareToIgnoreCase(path)==0){
								isFound=true;
								break;
							}
						}
						if(!isFound){
							hyperVPathList.add(path);
						}
					}
			}
			
			//WizardContext.getWizardContext().mask();
			//validateSourcePath(hyperVPathList,0);
			
			WizardContext.getWizardContext().unmask();
			setActivePage(currentIndex+1);

		}
		
		
	}
	
	//this method is just to avoid the session timeout.
	public void startTimerAvoidSessionTimeout() {
		if(timer == null) {
			timer = new Timer() {

				@Override
				public void run() {
					commonService.getJobMonitor(String.valueOf(JobMonitorModel.JOBTYPE_BACKUP),-1L, 
							new BaseAsyncCallback<JobMonitorModel>(){
						public void onFailure(Throwable caught) {
							if (caught instanceof SessionTimeoutException){
								
								String msg ="Cannot connect to backend. Please try again";
								
								MessageBox messageBox = new MessageBox();
								messageBox.setButtons(MessageBox.OK);
								messageBox.addCallback(new Listener<MessageBoxEvent>()
										{
											public void handleEvent(MessageBoxEvent be)
											{
												if (be.getButtonClicked().getItemId().equals(Dialog.OK))
													com.google.gwt.user.client.Window.Location.reload();
											}
										});
								
								messageBox.setMinWidth(200);
								messageBox.setType(MessageBoxType.ALERT);
								messageBox.setIcon(MessageBox.ERROR);
								messageBox.setTitleHtml(UIContext.Constants.failed());
								messageBox.setMessage(msg);
								messageBox.setModal(true);
								messageBox.show();
								
							}
						}
						public void onSuccess(JobMonitorModel result) {
							
						}
					});
				}
			};
			
			timer.schedule(3000);
			timer.scheduleRepeating(3000);
		}
	}
}
