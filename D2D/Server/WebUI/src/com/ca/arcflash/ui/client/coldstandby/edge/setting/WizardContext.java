package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import java.util.List;

import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.ESXServerModel;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.NetworkAdapterModel;
import com.ca.arcflash.ui.client.model.VCMDataStoreModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.MessageBox.MessageBoxType;

class WizardContext {
	private static WizardContext instance = new WizardContext();
	private VirtualizationPanel virtulizationPanel;
	private StandinPanel standinPanel;
	private VirtualMachinePanel virtualMachinePanel;
	private EmailPanel  emailPanel;
	private ISettingsContentHost settingContentHost;
	private VCMSettingsContent   vcmSettingsContent;
	private int validateStatus = 0;

	static WizardContext getWizardContext(){
		return instance;
	}
	
	protected void setVirtulizationPanel(VirtualizationPanel virtulizationPanel) {
		this.virtulizationPanel = virtulizationPanel;
	}
	
	protected void setStandingPanel(StandinPanel standingPanel){
		this.standinPanel = standingPanel;
	}
	protected void setVirtualMachinePanel(VirtualMachinePanel virtualMachinePanel) {
		this.virtualMachinePanel=virtualMachinePanel;
	}
	
	protected VirtualizationType getVirtulizationType(){
		return virtulizationPanel.getVirtulizationType();
	}
	
	//get HyperV property
	protected String getHyperVHost() {
		return virtulizationPanel.hyperVPanel.textFieldServer.getValue();
	}
	protected String getHyperVUsername() {
		return virtulizationPanel.hyperVPanel.textFieldUserName.getValue();
		
	}
	protected String getHyperVPassword() {
		return virtulizationPanel.hyperVPanel.textFieldPassword.getValue();
	}
	protected String getHyperVProtocol() {
		return virtulizationPanel.hyperVPanel.getProtocol();
	}
	protected int getHyperVPort() {
		return virtulizationPanel.hyperVPanel.textFieldPort.getValue().intValue();
	}
	
	
	//Get the property for settig page 1
	protected String getVMwareHost(){
		return virtulizationPanel.vmwarePanel.textFieldServer.getValue();
	}
	
	protected String getVMwareProtocol(){
		return virtulizationPanel.vmwarePanel.getProtocol();
	}
	
	protected int getVMwarePort(){
		return virtulizationPanel.vmwarePanel.textFieldPort.getValue().intValue();
	}
	
	protected String getVMwareUsername(){
		return virtulizationPanel.vmwarePanel.textFieldUserName.getValue();
	}
	
	protected String getVMwarePassword(){
		return virtulizationPanel.vmwarePanel.textFieldPassword.getValue();
	}
	
	
	/*protected  int getDataTransferPort() {
		return virtulizationPanel.hyperVPanel.textFieldPort.getValue().intValue();
		
	}*/
	
	//Get the property for settig page 2
	protected String getMonitorServer(){
		return virtulizationPanel.monitorPanel.textFieldMonitorServer.getValue();
	}
	
	protected String getMonitorUsername(){
		return virtulizationPanel.monitorPanel.textFieldUserName.getValue();
	}
	
	protected String getMonitorPassword(){
		return virtulizationPanel.monitorPanel.textFieldPassword.getValue();
	}
	
	protected String getMonitorProtocol(){
		return virtulizationPanel.monitorPanel.getProtocol();
	}
	
	protected int getMonitorPort(){
		return virtulizationPanel.monitorPanel.textFieldPort.getValue().intValue();
	}
	
	//Get the property for settig page 2
	protected void setMonitorServer(String monitor){
		virtulizationPanel.monitorPanel.textFieldMonitorServer.setValue(monitor);
	}
	
	protected void setMonitorUsername(String userName){
		virtulizationPanel.monitorPanel.textFieldUserName.setValue(userName);
	}
	
	protected void setMonitorPassword(String password){
		virtulizationPanel.monitorPanel.textFieldPassword.setValue(password);
	}
	
	protected void setMonitorProtocol(boolean isHttp){
		virtulizationPanel.monitorPanel.setProtocol(isHttp);
	}
	
	protected void setMonitorPort(int port){
		virtulizationPanel.monitorPanel.textFieldPort.setValue(port);
	}
	
	protected boolean isProxyForDataTransfer(){
		if(virtulizationPanel.monitorPanel.checkBoxProxy.getValue()){
			return true;
		}
		else {
			return false;
		}
	}
	
	protected boolean isManualStartVM(){
		if(standinPanel.radioManual.getValue()){
			return true;
		}
		else {
			return false;
		}
	}
	
	protected int getHeartBeatTimeout(){
		return standinPanel.timeoutField.getValue().intValue();
	}
	
	protected int getHeartBeatFrequency(){
		return standinPanel.frequencyField.getValue().intValue();
	}
	
	//Get the property for page 3
	protected BaseModel getESXHostModel(){
		return virtulizationPanel.esxNodeBox.getValue();
	}
	
	protected String getVMName() {
		return virtualMachinePanel.textFieldName.getValue();
	}
	protected int getVMCPU(){
		return virtualMachinePanel.textFieldCPU.getSimpleValue();
	}
	protected int getVMMemory() {
		return virtualMachinePanel.textFieldMemory.getValue().intValue();
		
	}
	protected ListStore<VCMDataStoreModel> getVMDisk() {
		return virtualMachinePanel.datastoreGrid.getStore();
	}
	protected ListStore<NetworkAdapterModel> getVMNetwork() {
		return virtualMachinePanel.adapterGrid.getStore();
	}
	protected boolean isConfiguredSameDatastore() {
		return virtualMachinePanel.radioSameDatastore.getValue();
	}
	protected boolean isConfiguredSameNetwork() {
		return virtualMachinePanel.radioSameNetwork.getValue();
	}
	protected String getSameHyperVPath() {
		return virtualMachinePanel.sameHyperVPathSelection.getHyperVPath();
	}
	protected String getHyperVVMConfigPath() {
		return virtualMachinePanel.hyperVVMPathSelection.getHyperVPath();
	}
	
	protected int processVCMJobscripts(int callBackType,boolean isValidateMonitor) {
		return virtulizationPanel.processVCMJobscripts(callBackType,isValidateMonitor);
	}
	
	protected void showSettingTab(int tabID) {
		vcmSettingsContent.showTabPanel(tabID);
	}
	
	protected void validateVCMSettings(boolean isValidateVirtualizationPanel) {
		vcmSettingsContent.validateSettings(isValidateVirtualizationPanel);
	}
	

	protected void mask(){
		//parentWindow.mask(UIContext.Constants.loadingIndicatorText());
		settingContentHost.increaseBusyCount(UIContext.Constants.coldStandbySettingVirtualizationValidating());
	}

	protected void unmask(){
		//parentWindow.unmask();
		settingContentHost.decreaseBusyCount();
	}
	
	protected void setSettingConent(VCMSettingsContent vcmSettingsContent) {
		this.vcmSettingsContent = vcmSettingsContent;
	}
	
	//validateStatus 0: get the event form load VCM settings event
	//validateStatus 1: get the event from 'click the virtual machine panel' or 'get the ESX node focus'
	//validateStatus 2: get the event from 'save button' event
	protected void setValidateStatus(int validateStatus) {
		this.validateStatus = validateStatus;
	}
	
	protected void onValidatingCompleted(boolean isSuccessful){
		if(vcmSettingsContent!=null) {
			if(validateStatus==0) {
				vcmSettingsContent.onLoadingCompleted(isSuccessful);
			}
			else if(validateStatus == 1) {
				//vcmSettingsContent.onValidatingCompleted(isSuccessful);
			}
			else if(validateStatus == 2) {
				vcmSettingsContent.onValidatingCompleted(isSuccessful);
			}
			
		}
		
	}
	
	protected void onSavingCompleted(boolean isSuccessful){
		if(vcmSettingsContent!=null) {
			vcmSettingsContent.onSavingCompleted(isSuccessful);
		}
	}
	
	protected void setContentHost(ISettingsContentHost settingContentHost) {
		this.settingContentHost = settingContentHost;
	}
	
	protected void exitWizard(){
		if(settingContentHost!=null) {
			settingContentHost.close();
		}
	}
	
	protected void showMessageBox(String debugID, String errorMsg){
		MessageBox messageBox = new MessageBox();
		//messageBox.getDialog().ensureDebugId(debugID);
		//messageBox.setMinWidth(400);
		messageBox.setType(MessageBoxType.ALERT);
		messageBox.setIcon(MessageBox.ERROR);
		messageBox.setTitleHtml(UIContext.Constants.failed());
		messageBox.setMessage(errorMsg);
		
		messageBox.setModal(true);
		messageBox.getDialog().setWidth(400);
		Utils.setMessageBoxDebugId(messageBox);
		messageBox.show();
	}
	
	protected void setESXDatastore(VMStorage[] result) {
		virtualMachinePanel.vmStorages = result;
	}
	
	protected void setAdapterConnection(String[] adapterConnection) {
		virtualMachinePanel.adapterConnection = adapterConnection;
	}
	
	protected void setAdapterType(String[] adapterType) {
		virtualMachinePanel.adapterType = adapterType;
	}
	
	protected void setSupportInformation(ESXServerInfo vmSupportInfo) {
		virtualMachinePanel.vmSupportInfo = vmSupportInfo;
	}
	
	protected void setHyperVVolumes(List<FileModel> result) {
		virtualMachinePanel.hyperVVolumes = result;
	}
	
	protected void  setESXResourcePool() {
		VirtualCenterModel vcModel = new VirtualCenterModel();
		vcModel.setVcName(getVMwareHost());
		vcModel.setUsername(getVMwareUsername());
		vcModel.setPassword(getVMwarePassword());
		vcModel.setProtocol(getVMwareProtocol());
		vcModel.setPort(getVMwarePort());
		
		ESXServerModel esxServerModel = new ESXServerModel();
		BaseModel esxNode = getESXHostModel();
		String dataCenter= (String)esxNode.get("dataCenter");
		String esxName= (String)esxNode.get("esxNode");
		esxServerModel.setESXName(esxName);
		esxServerModel.setDcName(dataCenter);
		
		virtualMachinePanel.resourcePoolPanel.setVcModel(vcModel);
		virtualMachinePanel.resourcePoolPanel.setEsxServerModel(esxServerModel);
	}
	
	protected void cleanESXResourcePool() {
		virtualMachinePanel.resourcePoolPanel.setPoolMoref("");
		virtualMachinePanel.resourcePoolPanel.setPoolName("");
		virtualMachinePanel.resourcePoolPanel.setVcModel(new VirtualCenterModel());
		virtualMachinePanel.resourcePoolPanel.setEsxServerModel(new ESXServerModel());
	}
	
	protected String getResourcePoolName() {
		return virtualMachinePanel.resourcePoolPanel.getPoolName();
	}
	
	protected String getResourcePoolRef() {
		return virtualMachinePanel.resourcePoolPanel.getPoolMoref();
	}
	
	
	protected void setVirtualMachineActive(boolean isForEdge) {
		virtualMachinePanel.activate(isForEdge);
	}
	
	protected EmailPanel getEmailPanel() {
		return emailPanel;
	}
	
	protected void setEmailPanel(EmailPanel emailPanel) {
		this.emailPanel = emailPanel;
	}
	
}
