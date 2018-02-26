package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.VCloudVirtualDataCenterModel;
import com.ca.arcflash.ui.client.restore.PasswordPane;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class VAppRecoveryOptionsWizard extends LayoutContainer {
	private VMRecoveryOptionsPanel parentPanel;
	private VAppRecoveryOptionsWizard thisWizard;
	private CardLayout contentCardLayout = new CardLayout();
	private LayoutContainer contentContainer = new LayoutContainer(contentCardLayout);
	private VAppRecoveryOptionsPanel vAppPanel;
	private VAppChildVMRecoveryWizard childVMWiazrd;
	
	private ListStore<BackupVMModel> childBackupVMStore = new ListStore<>();
	
	public VAppRecoveryOptionsWizard(VMRecoveryOptionsPanel parentPanel) {
		this.parentPanel = parentPanel;
		this.thisWizard = this;
		this.vAppPanel = new VAppRecoveryOptionsPanel(this);
		this.setLayout(new RowLayout());
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		contentCardLayout = new CardLayout();
		contentContainer = new LayoutContainer(contentCardLayout);
		contentContainer.ensureDebugId("8b88bde2-1cdd-4ebc-b907-5076f3ae20cd");
		this.add(contentContainer);

		contentContainer.add(vAppPanel);
		childVMWiazrd = new VAppChildVMRecoveryWizard(this);
		contentContainer.add(childVMWiazrd);
		
		PasswordPane pwdPanel = getSessionPwdPanel();
		if (RestoreContext.getRecoveryPointModel().isEncrypted()) {
			pwdPanel.setVisible(true);
		} else {
			pwdPanel.setVisible(false);
		}
	}
	
	@Override
	public void repaint() {
		super.repaint();
		childVMWiazrd.repaint();
		vAppPanel.repaint();
	}
	
	public ListStore<BackupVMModel> getChildBackupVMStore() {
		return childBackupVMStore;
	}
	
	public VAppRecoveryOptionsPanel getVAppPanel() {
		return vAppPanel;
	}
	
	public void activeVAppPanel() {
		PasswordPane pwdPanel = getSessionPwdPanel();
		if (RestoreContext.getRecoveryPointModel().isEncrypted()) {
			pwdPanel.show();
		} else {
			pwdPanel.setVisible(false);
		}
		vAppPanel.freshChildVMConfigGrid(true);
		contentCardLayout.setActiveItem(vAppPanel);
	}
	
	public void activeChildVMWizard(String vmInstanceUuid) {
		PasswordPane pwdPanel = getSessionPwdPanel();
		pwdPanel.setVisible(false);
		childVMWiazrd.activeSpecifiedVM(vmInstanceUuid);
		contentCardLayout.setActiveItem(childVMWiazrd);
	}
	
	public VAppChildVMRecoveryWizard getChildVMWizard(){
		return childVMWiazrd;
	}
	
	public void setVCloudVDCModel(VCloudVirtualDataCenterModel vDCModel, AsyncCallback<List<BackupVMModel>> callBack) {
		childVMWiazrd.setVCloudVDCModel(vDCModel, callBack);
	}
	
	public void validate(final AsyncCallback<Boolean> callback) {
		vAppPanel.validate(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (result != null && result.equals(Boolean.FALSE)) {
					callback.onSuccess(Boolean.FALSE);
				} else {
					callback.onSuccess(Boolean.TRUE);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	public PasswordPane getSessionPwdPanel() {
		return parentPanel.getSessionPwdPanel();
	}
	
	public RestoreWizardContainer getRestoreWizardContainer() {
		return parentPanel.getRestoreWizardContainer();
	}
	
	public int processOptions() {
		RestoreJobModel vAppRestoreJob = vAppPanel.processOptions();
		if (vAppRestoreJob != null) {
			childVMWiazrd.processOptions(vAppRestoreJob);
		}
		return 0;
	}
	
	public static String getNodeName(BackupVMModel vmModel) {
		if (vmModel == null) {
			return null;
		}
		
		String instanceUuid = vmModel.getVmInstanceUUID();
		String hostName = vmModel.getVmHostName();
		String vmName = vmModel.getVMName();
		return getNodeName(instanceUuid, hostName, vmName);
	}
	
	public static String getNodeName(String instanceUuid, String vmHostName, String vmName) {
		String nodeName = vmHostName;
		if (Utils.isEmptyOrNull(nodeName) || nodeName.trim().equals(instanceUuid)) {
			nodeName = UIContext.Messages.vAppRestoreChildVMNodeName(vmName);
		}
		return nodeName;
	}
	
	public static void showInfoMessage(String message) {
		MessageBox box = new MessageBox();
		box.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductName()));
		box.setMessage(message);
		box.setIcon(MessageBox.INFO);
		box.setButtons(Dialog.OK);
		box.show();
	}
	
	public static void showErrorMessage(String message) {
		MessageBox box = new MessageBox();
		box.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductName()));
		box.setMessage(message);
		box.setIcon(MessageBox.ERROR);
		box.setButtons(Dialog.OK);
		box.show();
	}
	
	public static void showErrorMessage(AsyncCallback<Boolean> callBack, String message) {
		if (message != null && !message.isEmpty()) {
			showErrorMessage(message);
		}
		
		if (callBack != null) {
			callBack.onSuccess(Boolean.FALSE);
		}
	}
	
	public static String getProductName() {
		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == 1){
			productName = UIContext.productNamevSphere;
		}
		return productName;
	}
	
	public static native String getComboBoxTemplate(String message) /*-{
		return [ '<tpl for=".">', '<div class="x-combo-list-item" qtip="{',
				message, '}" qtitle="">{', message, '}</div>', '</tpl>' ]
				.join("");
	}-*/;
	
	public static enum VDCMemoryCPUAllocationModel {
		AllocationPool("AllocationPool"),
		AllocationVApp("AllocationVApp"), 
		ReservationPool("ReservationPool");
		
		private String description;
		
		private VDCMemoryCPUAllocationModel(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}
}
