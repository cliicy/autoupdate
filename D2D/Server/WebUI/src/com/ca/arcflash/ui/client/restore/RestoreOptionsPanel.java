package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.RecoverVMOptionModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.vsphere.vmrecover.SetCredentialWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class RestoreOptionsPanel extends LayoutContainer implements
		RestoreValidator {
	protected RecoveryPontPasswordPanel recoveryPointPasswordPanel = new RecoveryPontPasswordPanel();
	protected PasswordPane pwdPane = new PasswordPane();
	protected RestoreWizardContainer restoreWizardWindow;      ///D2D Lite Integration
	protected BackupVMModel backupVMModel;
	protected BackupVMModel targetVMModel;
	protected SetCredentialWindow win;
	
	public RestoreOptionsPanel(RestoreWizardContainer restoreWizardWindow) {      ///D2D Lite Integration
		this.restoreWizardWindow = restoreWizardWindow;
		pwdPane.ensureDebugId("075E3B9C-B75D-4b4b-8FC2-8051C48316D5");
	}

	public int processOptions() {
		return 0;
	}

	public boolean validate() {
		return true;
	}

	public String Directroy = "[^\\\\/:\\*\\?\"<>\\|]+";
	public String AbsoluteDirReg = "^([A-Za-z]{1}:)" + "(\\\\" + Directroy
			+ ")*(\\\\)?$";
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setScrollMode(Scroll.AUTOY);
		backupVMModel = RestoreContext.getVMModel();
	}
	
	@Override
	public void repaint() {
		super.repaint();
		updateVisibleOfPwdPane();
	}
	
	protected void updateVisibleOfPwdPane() {
		if (!RestoreContext.getRecoveryPointModel().isEncrypted())
			pwdPane.setVisible(false);
		else {
			pwdPane.setVisible(true);
			pwdPane.autoFillPassword(restoreWizardWindow.getSelectedRecoveryPoint().getSessionGuid());
		}
	}
	
	public void checkSessionPassword(final AsyncCallback<Boolean> callback) {
		if(pwdPane.isVisible()) {
			String password = pwdPane.getPassword();
			if(password != null && password.length() > 0) {
				CommonServiceAsync service = GWT.create(CommonService.class);
				final String sessionPwdHashValue = RestoreContext.getRecoveryPointModel().getEncryptPwdHashKey();
				service.validateSessionPasswordByHash(password, password.length(), 
						sessionPwdHashValue, sessionPwdHashValue.length(),new BaseAsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						callback.onSuccess(Boolean.FALSE);
					}

					@Override
					public void onSuccess(Boolean isValid) {
						if(isValid != null && isValid) {
							callback.onSuccess(Boolean.TRUE);
						}
						else {							
							if(RestoreContext.isBackupToDataStore){// backup to Datastore, only use session pwd, the session pwd used for encryption pwd as well.
								RestoreOptionsPanel.this.showErrorMessage(callback, UIContext.Constants.recoveryPointsInvalidSessionPassword());
							}else{ // backup to share folder, only use encryption pwd.
								RestoreOptionsPanel.this.showErrorMessage(callback, UIContext.Constants.recoveryPointsInvalidEncryptionPassword());
							}
						}
					}
				});
			}
			else {
				showErrorMessage(callback, UIContext.Constants.recoveryPointsNeedSessionPassword());
			}
		}else {
			callback.onSuccess(Boolean.TRUE);
		}
		
	}
	
	protected void showErrorMessage(final AsyncCallback<Boolean> callback,
			final String errorMsg) {
		if(errorMsg != null && errorMsg.length() > 0) {
			String productName = UIContext.productNameD2D;
			if(UIContext.uiType == 1){
				productName = UIContext.productNamevSphere;
			}
			MessageBox messageBox = new MessageBox();
			messageBox.addCallback(new Listener<MessageBoxEvent> () {

				@Override
				public void handleEvent(MessageBoxEvent be) {
					if(errorMsg.equals(UIContext.Constants.recoveryPointsInvalidSessionPassword()))
						pwdPane.setFocus();
				}
				
			});
			messageBox.setModal(true);
			messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
			messageBox.setMessage(errorMsg);
			messageBox.setIcon(MessageBox.ERROR);
			Utils.setMessageBoxDebugId(messageBox);
			messageBox.show();
		}
		
		if(callback != null)
			callback.onSuccess(Boolean.FALSE);
	}
	
	public BackupVMModel getBackupVMModel(){
		return backupVMModel;
	}
	
	public void processBackupVM(boolean isTarget){
		RestoreJobModel model = RestoreContext.getRestoreModel();
		if(backupVMModel != null){
			if(!isTarget){
				model.recoverVMOption = new RecoverVMOptionModel(); 
				model.recoverVMOption.setOriginalLocation(false);
				model.recoverVMOption.setVMName(backupVMModel.getVMName());
				model.recoverVMOption.setVMUsername(backupVMModel.getUsername());
				model.recoverVMOption.setVMPassword(backupVMModel.getPassword());
				model.recoverVMOption.setESXServerName(backupVMModel.getEsxServerName());
				model.recoverVMOption.setVMUUID(backupVMModel.getUUID());
				model.recoverVMOption.setVMInstanceUUID(backupVMModel.getVmInstanceUUID());
				model.recoverVMOption.setVmDiskCount(0);
				VirtualCenterModel vcModel = new VirtualCenterModel();
				vcModel.setVcName(backupVMModel.getEsxServerName());
				vcModel.setProtocol(backupVMModel.getProtocol());
				vcModel.setPort(backupVMModel.getPort());
				vcModel.setPassword(backupVMModel.getEsxPassword());
				vcModel.setUsername(backupVMModel.getEsxUsername());
				model.recoverVMOption.setVCModel(vcModel);
				model.recoverVMOption.setOverwriteExistingVM(false);
				model.recoverVMOption.setPowerOnAfterRestore(false);
				model.recoverVMOption.setSessionNumber(-1);
			}else{
				model.recoverVMOption = new RecoverVMOptionModel(); 
				model.recoverVMOption.setOriginalLocation(false);
				model.recoverVMOption.setVMName(targetVMModel.getVMName());
				model.recoverVMOption.setVMUsername(targetVMModel.getUsername());
				model.recoverVMOption.setVMPassword(targetVMModel.getPassword());
				model.recoverVMOption.setESXServerName(targetVMModel.getEsxServerName());
				model.recoverVMOption.setVMUUID(targetVMModel.getUUID());
				model.recoverVMOption.setVMInstanceUUID(targetVMModel.getVmInstanceUUID());
				model.recoverVMOption.setVmDiskCount(0);
				VirtualCenterModel vcModel = new VirtualCenterModel();
				vcModel.setVcName(targetVMModel.getEsxServerName());
				vcModel.setProtocol(targetVMModel.getProtocol());
				vcModel.setPort(targetVMModel.getPort());
				vcModel.setPassword(targetVMModel.getEsxPassword());
				vcModel.setUsername(targetVMModel.getEsxUsername());
				model.recoverVMOption.setVCModel(vcModel);
				model.recoverVMOption.setOverwriteExistingVM(false);
				model.recoverVMOption.setPowerOnAfterRestore(false);
				model.recoverVMOption.setSessionNumber(-1);
			}
		}
	}
	
	public void popUpSetCredentialWindow(RestoreOptionsPanel panel,boolean isBroswe,final AsyncCallback<Boolean> callback){
		win = new SetCredentialWindow(panel,callback,isBroswe);
		win.setModal(true);
		win.show();
		callback.onSuccess(false);
	}
	
	protected void setDefaults() {
		if(UIContext.uiType == Utils.UI_TYPE_VSPHERE && !(restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE)){
			String destination = getParentFolder(restoreWizardWindow.getSessionPath());
			String username = "";
			String password = "";
			if(restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH){
				username = restoreWizardWindow.getRestoreSearchPanel().getUserName();
				password = restoreWizardWindow.getRestoreSearchPanel().getPassword();
			}else{
				username = restoreWizardWindow.getRecoveryPointsPanel().getUserName();
				password = restoreWizardWindow.getRecoveryPointsPanel().getPassword();
			}
			String domain = "";
			if (username != null) {
				int index = username.indexOf('\\');
				if (index > 0) {
					domain = username.substring(0, index);
					username = username.substring(index + 1);
				}
			}
			LoginServiceAsync service = GWT.create(LoginService.class);
			service.checkServerEqualsVMHostName(destination, domain, username,password, new BaseAsyncCallback<Boolean>(){
				@Override
				public void onFailure(Throwable caught){
					super.onFailure(caught);
				}
				
				@Override
				public void onSuccess(Boolean result){
					if(result){
						setDefaultValue();
					}else{
						setVSphereDefaultValue();
					}
				}
			});
		}else{
			setDefaultValue();
		}
	}
	
	private String getParentFolder(String destination){
		if(destination==null || destination.equals("")){
			return null;
		}
		int index = destination.lastIndexOf("\\");
		if(index>0){
			return destination.substring(0,index);
		}else{
			return null;
		}
	}
	
	protected void setDefaultValue(){
	}
	
	protected void setVSphereDefaultValue(){
		
	}
	
	public boolean doesOverwriteVM()
	{
		return false;
	}
}
