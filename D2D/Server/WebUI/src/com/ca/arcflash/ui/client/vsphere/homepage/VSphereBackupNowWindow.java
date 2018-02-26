package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.homepage.BackupNowWindow;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class VSphereBackupNowWindow extends BackupNowWindow{
	
	public VSphereBackupNowWindow(){
		super();
		
		nameTextField.ensureDebugId("6669C7EC-7187-455f-BD6F-D071980F0707");

		radioIncremental.ensureDebugId("B038151F-D893-4458-BB40-59D35B00D180");
		radioResync.ensureDebugId("AFC66AA5-9EDB-4e2f-A1CD-346FDA852817");
		radioFull.ensureDebugId("23CBF84E-6949-4f45-BE97-F823FBD83BED");
		
		cancelButton.ensureDebugId("52969715-DDA4-4ca0-A28A-677654DF6F47");
		okButton.ensureDebugId("B4DD24E2-4DAB-46f8-AC33-505B0E623CE4");
	}
	
	@Override
	protected void doBackup(final int backupType, final boolean convertForBackupSet) {
		service.backupVM(backupType, nameTextField.getValue(),
				UIContext.backupVM,  convertForBackupSet, new BaseAsyncCallback<Void>(){
			
			@Override
			public void onFailure(Throwable caught) {
				if(caught instanceof BusinessLogicException 
						&& "68719476738".equals(((BusinessLogicException)caught).getErrorCode())) {
					MessageBox.confirm(UIContext.Messages.messageBoxTitleInformation(
							Utils.getProductName()), ((BusinessLogicException)caught).getDisplayMessage(), 
							new Listener<MessageBoxEvent>(){
						@Override
						public void handleEvent(MessageBoxEvent be) {
							if(be.getButtonClicked().getItemId().equals(Dialog.YES)){
								doBackup(BackupTypeModel.Full, convertForBackupSet);
							}else {
								doBackup(backupType, false);
							}
							window.hide();
						}
					});
					return;
				}else if(caught instanceof BusinessLogicException) {
					BusinessLogicException ble = (BusinessLogicException)caught;
					if(("4294967314".equals((ble).getErrorCode()))) {
						MessageBox.info(UIContext.Messages.messageBoxTitleInformation(
								Utils.getProductName()), 
								(ble).getDisplayMessage(), null);
						window.hide();
						return ;
					}else if(("4294967326".equals((ble).getErrorCode()))) {
						MessageBox.confirm(UIContext.Messages.messageBoxTitleInformation(
								Utils.getProductName()), 
								(ble).getDisplayMessage(), new Listener<MessageBoxEvent>() {
									@Override
									public void handleEvent(MessageBoxEvent be) {
										if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
											final BaseAsyncCallback<Void> callback = new BaseAsyncCallback<Void>() {
												@Override
												public void onSuccess(Void result) {
													service.waitUntilvAppChildJobCancelled(UIContext.backupVM.getVmInstanceUUID(), backupType, new BaseAsyncCallback<Void>() {
														@Override
														public void onSuccess(Void result) {
															doBackup(backupType, convertForBackupSet);
														}
														@Override
														public void onFailure(Throwable caught) {
															super.onFailure(caught);
														}
													});
												}
												@Override
												public void onFailure(Throwable caught) {
													super.onFailure(caught);
												}
											};
											service.cancelvAppChildJobs(UIContext.backupVM.getVmInstanceUUID(), backupType, callback);
										}
										window.hide();
									}
						});
						return ;
					}
				}
				super.onFailure(caught);
				okButton.setEnabled(true);
				cancelButton.setEnabled(true);
			}

			@Override
			public void onSuccess(Void result) {
				Info.display(UIContext.Messages.messageBoxTitleInformation(UIContext.productNamevSphere), UIContext.Constants.backupNowWindowSubmitSuccessful());
				window.hide();
			}
			
		});
	}

	@Override
	protected String getHelpButtonURL() {
		return UIContext.externalLinks.getVMBackupNowHelp();
	}
}
