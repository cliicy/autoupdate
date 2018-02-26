package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.ha.model.VCMConfigStatus;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.event.HeartBeatStateChangedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.HeartBeatStateChangedEventHandler;
import com.ca.arcflash.ui.client.coldstandby.event.OfflineCopyAutoChangedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.OfflineCopyAutoChangedEventHandler;
import com.ca.arcflash.ui.client.coldstandby.event.ReplicationJobFinishedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.ReplicationJobFinishedEventHandler;
import com.ca.arcflash.ui.client.coldstandby.event.ServerSelectionChangedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.ServerSelectionChangedEventHandler;
import com.ca.arcflash.ui.client.coldstandby.event.SettingChangedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.SettingChangedEventHandler;
import com.ca.arcflash.ui.client.coldstandby.VirtualConversionServerNavigator.NODE_TYPE;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

public class ColdStandbyManager {
	
	private static ColdStandbyManager instance;
	
	private ColdStandbyServiceAsync coldStandByService = GWT.create(ColdStandbyService.class);
	private int HEARTBEAT_REFRESH_INTERVAL = 3000;
	private HandlerManager eventBus;
	private Timer heartBeatStateTimer;
	private ColdStandbyHomepage homepage;
	private ColdStandbyTaskPanel taskPanel;
	private VirtualConversionServerNavigator vcNavigator;
	private VirtualConversionTabPanel coldstandbyTabPanel = null;
	private VCMConfigStatus vcmStatus = null;
	
	public static String CONNECT_VCM_CLIENT_FAIL = "38654705665";
	
	private ColdStandbyManager(){
		eventBus = new HandlerManager(null);
		heartBeatStateTimer = new Timer() {
			public void run() {
				refreshState(null);
			}
		};
	}
	
	public static ColdStandbyManager getInstance(){
		if (instance == null)
			instance = new ColdStandbyManager();
		
		return instance;
	}
	
	public VirtualConversionTabPanel getColdstandbyTabPanel() {
		return coldstandbyTabPanel;
	}

	public void setColdstandbyTabPanel(VirtualConversionTabPanel coldstandbyTabPanel) {
		this.coldstandbyTabPanel = coldstandbyTabPanel;
	}
	
	public HandlerManager getEventBus() {
		return eventBus;
	}

	public void startHeartBeatStateTimer(){
		heartBeatStateTimer.schedule(HEARTBEAT_REFRESH_INTERVAL);
		heartBeatStateTimer.scheduleRepeating(HEARTBEAT_REFRESH_INTERVAL);
	}
	
	public void stopHeartBeatStateTimer(){
		heartBeatStateTimer.cancel();
	}
	
	public void registerEventHandler(HeartBeatStateChangedEventHandler handler){
		eventBus.addHandler(HeartBeatStateChangedEvent.TYPE, handler);
	}
	
	public void unregisterEventHandler(HeartBeatStateChangedEventHandler handler){
		eventBus.removeHandler(HeartBeatStateChangedEvent.TYPE, handler);
	}
	
	public void registerEventHandler(OfflineCopyAutoChangedEventHandler handler){
		eventBus.addHandler(OfflineCopyAutoChangedEvent.TYPE, handler);
	}
	
	public void unregisterEventHandler(OfflineCopyAutoChangedEventHandler handler){
		eventBus.removeHandler(OfflineCopyAutoChangedEvent.TYPE, handler);
	}
	
	public void registerEventHandler(SettingChangedEventHandler handler){
		eventBus.addHandler(SettingChangedEvent.TYPE, handler);
	}
	
	public void unregisterEventHandler(SettingChangedEventHandler handler){
		eventBus.removeHandler(SettingChangedEvent.TYPE, handler);
	}
	
	public void registerEventHandler(ReplicationJobFinishedEventHandler handler){
		eventBus.addHandler(ReplicationJobFinishedEvent.TYPE, handler);
	}
	
	public void unregisterEventHandler(ReplicationJobFinishedEventHandler handler){
		eventBus.removeHandler(ReplicationJobFinishedEvent.TYPE, handler);
	}
	
	public void registerEventHandler(ServerSelectionChangedEventHandler handler){
		eventBus.addHandler(ServerSelectionChangedEvent.TYPE, handler);
	}
	
	public void unregisterEventHandler(ServerSelectionChangedEventHandler handler){
		eventBus.removeHandler(ServerSelectionChangedEvent.TYPE, handler);
	}
	
	public void fireSetingChangedEvent(JobScriptCombo jobScriptCombo){
		eventBus.fireEvent(new SettingChangedEvent(jobScriptCombo));
	}
	
	public void fireReplicationJobFinishedEvent(){
		eventBus.fireEvent(new ReplicationJobFinishedEvent());
	}
	
	public void fireServerSelectionChangedEvent(ARCFlashNode node, NODE_TYPE nodeType){
		eventBus.fireEvent(new ServerSelectionChangedEvent(node, nodeType));
	}
	
	public ColdStandbyHomepage getHomepage() {
		return homepage;
	}

	public void setHomepage(ColdStandbyHomepage homepage) {
		this.homepage = homepage;
	}
	
	public VirtualConversionServerNavigator getVCNavigator() {
		return vcNavigator;
	}

	public void setVCNavigator(VirtualConversionServerNavigator navigator) {
		this.vcNavigator = navigator;
	}
	
	public ColdStandbyTaskPanel getTaskPanel() {
		return taskPanel;
	}

	public void setTaskPanel(ColdStandbyTaskPanel taskPanel) {
		this.taskPanel = taskPanel;
	}

	private void refreshState(Object object) {
		if(!ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerAccessible()
			&& !ColdStandbyManager.getInstance().getVCNavigator().isSelectedVShpereManagedVM() 
			&& !ColdStandbyManager.getInstance().getVCNavigator().isSelectedRemote())
			return;
		
		String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
		coldStandByService.getStates(vmInstanceUUID,new BaseAsyncCallback<Integer[]>(UIContext.productNameVCM){

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof BusinessLogicException){
					BusinessLogicException exception = (BusinessLogicException)caught;
					if(CONNECT_VCM_CLIENT_FAIL.equals(exception.getErrorCode())) {
						if(getTaskPanel().isButtonsEnabled()) {
							showErrorMessage(exception);
							getTaskPanel().setTaskPanelButtonsStatus(false);
						}
					}
				}
			}

			@Override
			public void onSuccess(Integer[] result) {
//				GWT.log("refreshState:"+result);
				int len = result.length;
				if(len>0) {
					if(!getTaskPanel().isButtonsEnabled())
						getTaskPanel().setTaskPanelButtonsStatus(true);
					
					eventBus.fireEvent(new HeartBeatStateChangedEvent(result[0]));
				}
				// gaosa01
				if(len>1)
					eventBus.fireEvent(new OfflineCopyAutoChangedEvent(result[1]));
			}
			
		});
	}
	
	/**
	 * Return the managed vSpere uuid to which the current GUI is connecting.
	 * @return null if logging in proxy server; otherwise, the vm instance uuid
	 */
	public static String getVMInstanceUUIDFromURL() {
		String vmInstanceUUID = null; 
		if(UIContext.uiType == 1 && UIContext.backupVM != null)
			vmInstanceUUID = UIContext.backupVM.getVmInstanceUUID();
		return vmInstanceUUID;
	}
	
	/**
	 * Return the managed vSpere uuid to which the current GUI is connecting.
	 * @return null if logging in proxy server; otherwise, the vm instance uuid
	 */
	public static String getVMInstanceUUID() {
		if(ColdStandbyManager.getInstance().getVCNavigator() != null 
				&& ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()
				&& ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().isVSphereManagedVM())
			return ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().getUuid();
		else
			return getVMInstanceUUIDFromURL();
	}
	

	public VCMConfigStatus getVcmStatus() {
		return vcmStatus;
	}

	public void setVcmStatus(VCMConfigStatus vcmStatus) {
		this.vcmStatus = vcmStatus;
	}
}
