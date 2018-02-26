package com.ca.arcflash.ui.client.coldstandby.setting;

import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.model.VCMDataStoreModel;
import com.ca.arcflash.ui.client.model.NetworkAdapterModel;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;

class WizardContext {
	private static WizardContext instance = new WizardContext();
	private VirtualizationPanel virtulizationPanel;
	private StandinPanel standinPanel;
	private VirtualMachinePanel virtualMachinePanel;
	private SummaryPanel summaryPanel;
	private Window parentWindow;
	private boolean isAMD64Arch=false;

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
	protected void  setSummaryPanel(SummaryPanel summaryPanel) {
		this.summaryPanel=summaryPanel;
	}
	
	protected VirtualizationType getVirtulizationType(){
		return virtulizationPanel.getVirtulizationType();
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
	
	protected String getHypervHost(){
		return virtulizationPanel.hyperVPanel.textFieldServer.getValue();
	}
	protected String getHypervUsername(){
		return virtulizationPanel.hyperVPanel.textFieldUserName.getValue();
	}
	
	protected String getHypervPassword(){
		return virtulizationPanel.hyperVPanel.textFieldPassword.getValue();
	}
	
	/*protected  int getDataTransferPort() {
		return virtulizationPanel.hyperVPanel.textFieldPort.getValue().intValue();
		
	}*/
	
	//Get the property for settig page 2
	protected String getMonitorServer(){
		return standinPanel.textFieldMonitorServer.getValue();
	}
	
	protected String getMonitorUsername(){
		return standinPanel.textFieldUserName.getValue();
	}
	
	protected String getMonitorPassword(){
		return standinPanel.textFieldPassword.getValue();
	}
	
	protected String getMonitorProtocol(){
		if(standinPanel.httpRadio.getValue())
		{
			return "http";
		}
		else {
			return "https";
		}
	}
	
	protected int getMonitorPort(){
		return standinPanel.textFieldPort.getValue().intValue();
	}
	
	protected boolean isProxyForDataTransfer(){
		if(standinPanel.checkBoxProxy.getValue()){
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
		return virtualMachinePanel.esxNodeBox.getValue();
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
	//Get the property for summary panel
	protected boolean isStartOfflineCopyNow(){
		if(summaryPanel.startImmediateButton.getValue()){
			return true;
		}
		else{
			return false;
		}
	}
	
	protected void addAMD64ArchTip() {
		virtulizationPanel.vmwarePanel.addAMD64ArchTip();
	}
	
	protected boolean isVirtualizationHostChanged() {
		return virtualMachinePanel.isVirtualizationHostChanged();
	}
	
	protected void mask(){
		parentWindow.mask(UIContext.Constants.loadingIndicatorText());
	}
	
	protected void unmask(){
		parentWindow.unmask();
	}

	protected void setParentWindow(Window parentWindow) {
		this.parentWindow = parentWindow;
	}
	
	protected void exitWizard(){
		if (parentWindow!=null)
			parentWindow.hide();
	}
}
