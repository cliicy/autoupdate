package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.VirtualConversionServerNavigator.NODE_TYPE;
import com.ca.arcflash.ui.client.coldstandby.event.HeartBeatStateChangedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.HeartBeatStateChangedEventHandler;
import com.ca.arcflash.ui.client.coldstandby.event.OfflineCopyAutoChangedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.OfflineCopyAutoChangedEventHandler;
import com.ca.arcflash.ui.client.coldstandby.event.ServerSelectionChangedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.ServerSelectionChangedEventHandler;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonSettingWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.TaskPanel;
import com.ca.arcflash.ui.client.log.LogWindow;
import com.ca.arcflash.ui.client.vsphere.log.VSphereLogWindow;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ColdStandbyTaskPanel extends ContentPanel {

	private final ColdStandbyServiceAsync coldStandByService = GWT.create(ColdStandbyService.class);
	
//	private LayoutContainer containerStartHeartBeat;
	private LayoutContainer containerPauseHeartBeat;
	private LayoutContainer containerResumeHeartBeat;
//	private LayoutContainer containerStopHeartBeat;
//	private LayoutContainer containerOfflineCopy;
	
	private LayoutContainer containerEnableAutoOffline;
	private LayoutContainer containerDisableAutoOffline;
	private LayoutContainer containerSetting;
	private LayoutContainer containerActivityLog;
	private boolean buttonsEnabled = true;

	private VerticalPanel verticalPanel;
	//this should be moved to HeartBeatJobScript
	private static final int STATE_DOWN = 0x00008000;
	private boolean popupPanel = false;
	
	public ColdStandbyTaskPanel(boolean popupPanel) {
		this.popupPanel = popupPanel;
	
		if(!popupPanel)
			ColdStandbyManager.getInstance().setTaskPanel(this);
		
		setCollapsible(true);
		ensureDebugId("683b0b7b-02f0-4451-ab64-559f7fd5256e");
		setAutoHeight(true);
		setHeadingHtml(VCMMessages.homepageTaskVirtualConversion());
//		setBodyStyle("background-color: white; padding: 6px;");
		setBodyStyle("padding: 4px;");
		getHeader().ensureDebugId("47ccd66a-2f93-4352-90d4-40c3aa64e369");
		
		verticalPanel = new VerticalPanel();
		verticalPanel.setWidth("100%");
		verticalPanel.ensureDebugId("5aaf7b45-12dd-4c31-93b8-62b623b169b1");
		this.add(verticalPanel);
	    
	    ClickHandler setttingHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if(!containerSetting.isEnabled())
					return;
				
				//SettingWindow window = new SettingWindow();
				CommonSettingWindow window = new CommonSettingWindow(AppType.VCM);
				window.setSize(840, 640);
				window.setModal(true);
				window.show();
			}
	    };
	    
//	    ClickHandler startHeartBeatHandler = new ClickHandler(){
//
//			@Override
//			public void onClick(ClickEvent event) {
//				coldStandByService.startHeartBeat(new BaseAsyncCallback<Void>(){
//
//					@Override
//					public void onSuccess(Void result) {
//						Info.display(UIContext.Constants.successful(), UIContext.Constants.coldStandbyHeartBeatCommandStartResult());   
//					}
//					
//				});
//			}
//	    	
//	    };
	    
//	    ClickHandler startReplicationHandler = new ClickHandler(){
//
//			@Override
//			public void onClick(ClickEvent event) {
//				coldStandByService.startReplication(new BaseAsyncCallback<Void>(){
//
//					@Override
//					public void onSuccess(Void result) {
//						Info.display(UIContext.Constants.successful(), UIContext.Constants.coldStandbyOfflineCopyCommandNowResult()); 
//					}
//					
//				});
//			}
//	    	
//	    };
	    
	    ClickHandler pauseHeartBeatHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(!containerPauseHeartBeat.isEnabled())
					return;
				
				containerPauseHeartBeat.setEnabled(false);
				
				String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
				coldStandByService.pauseHeartBeat(vmInstanceUUID, new BaseAsyncCallback<Void>(UIContext.productNameVCM){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						containerPauseHeartBeat.setEnabled(true);
					}

					@Override
					public void onSuccess(Void result) {
						Info.display(UIContext.Constants.successful(), UIContext.Constants.coldStandbyHeartBeatCommandPauseResult());
					}
					
				});
			}
	    	
	    };
	    
//	    ClickHandler stopHeartBeatHandler = new ClickHandler(){
//
//			@Override
//			public void onClick(ClickEvent event) {
//				coldStandByService.stopHeartBeat(new BaseAsyncCallback<Void>(){
//
//					@Override
//					public void onSuccess(Void result) {
//						Info.display(UIContext.Constants.successful(), UIContext.Constants.coldStandbyHeartBeatCommandStopResult());
//					}
//					
//				});
//			}
//	    	
//	    };
	    
	    ClickHandler resumeHeartBeatHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(!containerResumeHeartBeat.isEnabled())
					return;
				
				containerResumeHeartBeat.setEnabled(false);
				
				String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
				coldStandByService.resumeHeartBeat(vmInstanceUUID, new BaseAsyncCallback<Void>(UIContext.productNameVCM){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						containerResumeHeartBeat.setEnabled(true);
					}

					@Override
					public void onSuccess(Void result) {
						Info.display(UIContext.Constants.successful(), UIContext.Constants.coldStandbyHeartBeatCommandResumeResult());
					}
					
				});
			}
	    	
	    };
	    
	    ClickHandler enableAutoOfflieCopyHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(!containerEnableAutoOffline.isEnabled())
					return;
				
				containerEnableAutoOffline.setEnabled(false);
				
				final String instanceUUID = ColdStandbyManager.getVMInstanceUUID();
				//If session count>1, Allow user to choose whether to merge all sessions to one
				coldStandByService.getReplicationQueueSize(instanceUUID, new BaseAsyncCallback<Integer>(UIContext.productNameVCM){
					public void onSuccess(Integer result) {
						if( result > 1 ){
							MessageBox box = new MessageBox();
							box.setButtons(MessageBox.YESNOCANCEL);
							box.setIcon(MessageBox.QUESTION);   
					        box.setTitleHtml(UIContext.Constants.coldStandbyTaskMergeBoxTitle());
					        box.setMessage(UIContext.Constants.coldStandbyTaskMergeQuestion());
					        box.getDialog().setWidth(400);
					        Utils.setMessageBoxDebugId(box);
					        box.show();
					        box.addCallback(new Listener<MessageBoxEvent>() {
								@Override
								public void handleEvent(MessageBoxEvent be) {
									if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
										coldStandByService.forceNextReplicationMerge(instanceUUID, true,
												new BaseAsyncCallback<Void>(UIContext.productNameVCM) {
													public void onFailure(Throwable caught) {
														enableAutoOfflineCopy();
													}

													public void onSuccess(Void result) {
														enableAutoOfflineCopy();
													}
												});
									} else if (be.getButtonClicked().getItemId().equals(Dialog.NO)) {
										enableAutoOfflineCopy();
									}
								}
							});
						}else {
							enableAutoOfflineCopy();
						}
					}
					public void onFailure(Throwable caught) {
						enableAutoOfflineCopy();
					}
				});
			}
			private void enableAutoOfflineCopy(){
					final String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
				coldStandByService.enableAutoOfflieCopy(vmInstanceUUID, new BaseAsyncCallback<Void>(UIContext.productNameVCM){
					
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						containerEnableAutoOffline.setEnabled(true);
					}

					@Override
					public void onSuccess(Void result) {
						Info.display(UIContext.Constants.successful(), VCMMessages.coldStandbyenEnableAutoOfflieCopyResult());
						//Start a virtual conversion job right now. 
						//Put this task here because we may prompt a confirmation dialog to let user decide whether to start.
						coldStandByService.startReplication(vmInstanceUUID, new BaseAsyncCallback<Void>(UIContext.productNameVCM){
											@Override
											public void onSuccess(Void result) {
												Info.display(UIContext.Constants.successful(), VCMMessages.coldStandbyOfflineCopyCommandNowResult()); 
											}
											
						});
					}
					
				});
			}
	    	
	    };
	    
	    ClickHandler disableAutoOfflieCopyHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(!containerDisableAutoOffline.isEnabled())
					return;
				
				containerDisableAutoOffline.setEnabled(false);
				coldStandByService.disableAutoOfflieCopy(ColdStandbyManager.getVMInstanceUUID(), new BaseAsyncCallback<Void>(UIContext.productNameVCM){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						containerDisableAutoOffline.setEnabled(true);
					}

					@Override
					public void onSuccess(Void result) {
						Info.display(UIContext.Constants.successful(), VCMMessages.coldStandbyDisableAutoOfflieCopyResult());
					}
					
				});
			}
	    	
	    };
	    
	    ClickHandler logHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if(!containerActivityLog.isEnabled())
					return;
				
				if(ColdStandbyManager.getVMInstanceUUID() == null
					|| ColdStandbyManager.getVMInstanceUUID().length() == 0){
					LogWindow window = new LogWindow(false);
					window.setViewLogsHelp(UIContext.externalLinks.getVirtualStandbyViewLogURL());
					window.setModal(true);
					window.show();
				}else{
					VSphereLogWindow window = new VSphereLogWindow(true);
					window.setModal(true);
					window.show();
				}
			}
	    };
	    
	    ClickHandler provisionsHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				ProvisionWindow window = new ProvisionWindow();
				window.setModal(true);
				window.show();
				
//				if(i++ %2 == 0)
//					ColdStandbyManager.getInstance().getHomepage().getSummaryPanel().updateLicensePanelFromMonitee();
//				else {
//					EdgeLicenseInfo licenseInfo = new EdgeLicenseInfo();
//					if(j++ % 2 == 0) {
//						licenseInfo.setPhysicalMachineLicense(EdgeLicenseInfo.LICENSE_ERR);
//						licenseInfo.setVSphereVMLicense(EdgeLicenseInfo.LICENSE_ERR);
//					}
//					else
//					{
//						licenseInfo.setPhysicalMachineLicense(EdgeLicenseInfo.LICENSE_SUC);
//						licenseInfo.setVSphereVMLicense(EdgeLicenseInfo.LICENSE_SUC);
//					}
//					ColdStandbyManager.getInstance().getHomepage().getSummaryPanel().updateLicensePanel(licenseInfo );
//				}
			}
	    };
	    
//	    containerStartHeartBeat = TaskPanel.addTask(coldStandByPanel, UIContext.Constants.coldStandbyTaskStartHeartBeat(),UIContext.Constants.coldStandbyTaskStartHeartBeatDescription(), startHeartBeatHandler, UIContext.IconBundle.tasks_backup(), "0eb4dd80-b8db-42e4-b277-02f932131702");
	    //if logging in proxy server other than managed vSpere vm
//	    if(ColdStandbyManager.getVMInstanceUUID() == null) {
	    containerPauseHeartBeat = TaskPanel.addTask(verticalPanel, UIContext.Constants.coldStandbyTaskPauseHeartBeat(),UIContext.Constants.coldStandbyTaskPauseHeartBeatDescription(), pauseHeartBeatHandler, AbstractImagePrototype.create(UIContext.IconBundle.vcm_pause_heart_beat()), "a1949dbf-6bdc-4d36-9a65-0b086cf22024");
	    containerResumeHeartBeat = TaskPanel.addTask(verticalPanel, UIContext.Constants.coldStandbyTaskResumeHeartBeat(),UIContext.Constants.coldStandbyTaskResumeHeartBeatDescription(), resumeHeartBeatHandler, AbstractImagePrototype.create(UIContext.IconBundle.vcm_resume_heart_beat()), "ef320068-40f4-4dc4-a28b-22cebf8c8cf1");
//	    }
//	    containerStopHeartBeat = TaskPanel.addTask(coldStandByPanel, UIContext.Constants.coldStandbyTaskStopHeartBeat(),UIContext.Constants.coldStandbyTaskStopHeartBeatDescription(), stopHeartBeatHandler, UIContext.IconBundle.tasks_backup(), "ffc9ec6b-6569-404f-8c20-217333e960f5");
//	    containerOfflineCopy = TaskPanel.addTask(coldStandByPanel, UIContext.Constants.coldStandbyTaskOfflineCopyNow(),UIContext.Constants.coldStandbyTaskOfflineCopyNowDescription(), startReplicationHandler, UIContext.IconBundle.tasks_restore(),"5b625e34-39f3-44fa-8403-ae2c9cb84d13");
	    
	    containerEnableAutoOffline =  TaskPanel.addTask(verticalPanel, VCMMessages.coldStandbyTaskEnableAutoOfflineCopy(),UIContext.Constants.coldStandbyTaskEnableAutoOfflineCopyDescription(), enableAutoOfflieCopyHandler, AbstractImagePrototype.create(UIContext.IconBundle.vcm_resume_conversion()), "0eb4dd80-b8db-42e4-b277-02f932131703");
	    containerDisableAutoOffline =  TaskPanel.addTask(verticalPanel, VCMMessages.coldStandbyTaskDisableAutoOfflineCopy(),UIContext.Constants.coldStandbyDisableAutoOfflineCopyDescription(), disableAutoOfflieCopyHandler, AbstractImagePrototype.create(UIContext.IconBundle.vcm_pause_conversion()), "0eb4dd80-b8db-42e4-b277-02f932131704");
	  
	    TaskPanel.addTask(verticalPanel, UIContext.Constants.provistionPointName(),"", provisionsHandler, AbstractImagePrototype.create(UIContext.IconBundle.vcm_provision_points()),"745adpjs-438s-kjk3-a012-dsad44343dad");
	    containerSetting = TaskPanel.addTask(verticalPanel, VCMMessages.coldStandbyTaskSettings(),UIContext.Constants.coldStandbyTaskSettingsDescription(), setttingHandler, AbstractImagePrototype.create(UIContext.IconBundle.vcm_conversion_settings()),"33549225-77a1-43d8-a012-3e101402c522");
	    containerActivityLog = TaskPanel.addTask(verticalPanel, UIContext.Constants.coldStandbyTaskLogs(),UIContext.Constants.coldStandbyTaskLogsDescription(), logHandler, AbstractImagePrototype.create(UIContext.IconBundle.tasks_log()), false,"66d69ed6-5fb2-49eb-81ca-f65555495635");
	    
	    renderAsNoScript();
	    
	    //if logging in proxy server other than managed vSpere vm
//		if(ColdStandbyManager.getVMInstanceUUID() == null) {	    
			ColdStandbyManager.getInstance().registerEventHandler(new InternalHeartBeatStateChangedEventHandler());
//		}
	    
	    ColdStandbyManager.getInstance().registerEventHandler(new InternalOfflineCopyAutoChangedEventHandler());
	    
	    ColdStandbyManager.getInstance().registerEventHandler(new InternalServerSelectionChangedEvenHandler());
	    
	    if(popupPanel)
	    	setTaskPanelButtonsStatus(false);
	}
	
	public ContentPanel getColdStandByPanel() {
		return this;
	}
	
	public void renderAsNoScript(){
//		if(ColdStandbyManager.getVMInstanceUUID() == null) {
			containerPauseHeartBeat.setVisible(false);
			containerResumeHeartBeat.setVisible(false);
//		}
//		containerStopHeartBeat.setVisible(false);
		
//		containerStartHeartBeat.setVisible(true);
//		containerStartHeartBeat.disable();
		
		containerEnableAutoOffline.disable();
		containerEnableAutoOffline.setVisible(true);
//		
//		containerDisableAutoOffline.setVisible(false);
		
//		containerOfflineCopy.disable();
	}
	
	class InternalHeartBeatStateChangedEventHandler implements HeartBeatStateChangedEventHandler{

		@Override
		public void onHeartBeatStateChanged(HeartBeatStateChangedEvent event) {
			if (UIContext.isRemoteVCM) {
				// hide heartbeat menu item for Remote VCM.
				return;
			}
			int heartBeatState = event.getState();
			if (heartBeatState == HeartBeatJobScript.STATE_NO_EXIST){
//				containerOfflineCopy.disable();
//				containerStartHeartBeat.disable();
//				
//				containerStartHeartBeat.setVisible(true);
				containerPauseHeartBeat.setVisible(false);
				containerResumeHeartBeat.setVisible(false);
//				containerStopHeartBeat.setVisible(false);
			}else if (heartBeatState == (HeartBeatJobScript.STATE_PENDING|HeartBeatJobScript.STATE_UNREGISTERED)
					|| heartBeatState == HeartBeatJobScript.STATE_PENDING){
//				containerOfflineCopy.enable();
//				containerStartHeartBeat.enable();
//				
//				containerStartHeartBeat.setVisible(true);
				containerPauseHeartBeat.setVisible(false);
				containerResumeHeartBeat.setVisible(false);
//				containerStopHeartBeat.setVisible(false);
			}else if (heartBeatState == (HeartBeatJobScript.STATE_ACTIVE|HeartBeatJobScript.STATE_REGISTERED)
					|| heartBeatState == HeartBeatJobScript.STATE_ACTIVE){
//				containerOfflineCopy.enable();
				
//				containerStartHeartBeat.setVisible(false);
				containerPauseHeartBeat.setVisible(true);
				containerPauseHeartBeat.setEnabled(true);
				containerResumeHeartBeat.setVisible(false);
//				containerStopHeartBeat.setVisible(true);
			}else if (heartBeatState == (HeartBeatJobScript.STATE_CANCELED|HeartBeatJobScript.STATE_REGISTERED)
					|| heartBeatState == HeartBeatJobScript.STATE_CANCELED){
//				containerOfflineCopy.enable();
				
//				containerStartHeartBeat.setVisible(false);
				containerPauseHeartBeat.setVisible(false);
				containerResumeHeartBeat.setVisible(true);
				containerResumeHeartBeat.enable();
//				containerStopHeartBeat.setVisible(true);
			}
		}
		
	}
	class InternalOfflineCopyAutoChangedEventHandler implements OfflineCopyAutoChangedEventHandler{

		@Override
		public void onOfflineCopyAutoChanged(OfflineCopyAutoChangedEvent event) {
			//offline copy state(0 auto disabled, 1 auto enabled)
			int state = event.getState();

			if(state == ReplicationJobScript.AUTO_OFFLINE_COPY_NO_EXIST ){
				containerEnableAutoOffline.disable();
				containerEnableAutoOffline.setVisible(true);
				containerDisableAutoOffline.setVisible(false);
			}else if(state == ReplicationJobScript.AUTO_OFFLINE_COPY_ENABLED ){
				containerEnableAutoOffline.setVisible(false);
				containerDisableAutoOffline.enable();
				containerDisableAutoOffline.setVisible(true);
			}else if(state == ReplicationJobScript.AUTO_OFFLINE_COPY_DISABLED ){
				containerEnableAutoOffline.enable();
				containerEnableAutoOffline.setVisible(true);
				containerDisableAutoOffline.setVisible(false);
			}
				
		}

	}
	
	class InternalServerSelectionChangedEvenHandler implements ServerSelectionChangedEventHandler{

		@Override
		public void onServerChanged(ServerSelectionChangedEvent event) {
			ColdStandbyTaskPanel.this.mask(UIContext.Constants.refreshing());
			final ARCFlashNode node = event.getCurrentNode();
			final NODE_TYPE nodeType = event.getType();
			if(!ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerAccessible() 
					&& !ColdStandbyManager.getInstance().getVCNavigator().isSelectedVShpereManagedVM()) {
				node.setState(STATE_DOWN);
			}
			//Note: if isSelectedServerAccessible is false, go to clear the old web service client on UI Server 
			
			coldStandByService.connectMoniteeServer(node, new AsyncCallback<Void>(){
				
				@Override
				public void onSuccess(Void result) {
					ColdStandbyTaskPanel.this.unmask();
					setTaskPanelButtonsStatus(true);
					
					if(!popupPanel) {
						ColdStandbyManager.getInstance().getVCNavigator().unlockNavigator();
	//					selectedNode = node;
	//					ColdStandbyManager.getInstance().getTaskPanel().enable();
						
						String str = null;
						if(NODE_TYPE.STATUS_CHANGE == nodeType)
							str = UIContext.Messages.virtualConversionTaskPanelReconnectServer(node.getHostname());
						else
							str = UIContext.Messages.virtualConversionTaskPanelSwithServer(node.getHostname());
							
						Info.display(UIContext.Constants.successful(), str);
	//					ColdStandbyManager.getInstance().getHomepage().refreshUI();
						
						ARCFlashNode selectServerNode = ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode();
						if(selectServerNode != null && selectServerNode.isMonitor())
							ColdStandbyManager.getInstance().getHomepage().renderHomepage();
							
						ColdStandbyManager.getInstance().getHomepage().getSummaryPanel().setMoniteeConnectedAfterSelectNode(true);
					}

				}
	
				@Override
				public void onFailure(Throwable caught) {
					ColdStandbyTaskPanel.this.unmask();
					setTaskPanelButtonsStatus(false);
					
					if(!popupPanel) {
						ColdStandbyManager.getInstance().getVCNavigator().unlockNavigator();
						ColdStandbyManager.getInstance().getHomepage().getSummaryPanel().setMoniteeConnectedAfterSelectNode(true);
	//					selectedNode = node;
						if(!ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerAccessible())
							return;
						
						MessageBox messageBox = new MessageBox();
						messageBox.setIcon(MessageBox.ERROR);
						messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameVCM));
						String str = null;
						if(NODE_TYPE.STATUS_CHANGE == nodeType)
							str = UIContext.Messages.virtualConversionTaskPanelReconnectFails(node.getHostname());
						else
							str = UIContext.Messages.virtualConversionTaskPanelConnectFails(node.getHostname());
						messageBox.setMessage(str);
						messageBox.addCallback(null);
						messageBox.setModal(true);
						Utils.setMessageBoxDebugId(messageBox);
						//BaseAsyncCallback.setButtonId(messageBox, Dialog.OK, "mmxjg4xp-hgw3-tm8f-x83e-uvnlndj014uy");
						messageBox.show();
					}
				}

			});
		}
		
	}
	
	public void setTaskPanelButtonsStatus(boolean enabled) {
		buttonsEnabled = enabled;
		//if logging in proxy server other than managed vSpere vm
//		if(ColdStandbyManager.getVMInstanceUUID() == null) {
			containerPauseHeartBeat.setEnabled(enabled);
			containerResumeHeartBeat.setEnabled(enabled);
//		}
		containerEnableAutoOffline.setEnabled(enabled);
		containerDisableAutoOffline.setEnabled(enabled);
		containerSetting.setEnabled(enabled);
		containerActivityLog.setEnabled(enabled);
	}

	public boolean isButtonsEnabled() {
		return buttonsEnabled;
	}

	public LayoutContainer getContainerSetting() {
		return containerSetting;
	}

	public LayoutContainer getContainerActivityLog() {
		return containerActivityLog;
	}
	
}
