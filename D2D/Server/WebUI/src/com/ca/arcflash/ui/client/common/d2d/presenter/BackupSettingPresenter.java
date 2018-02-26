package com.ca.arcflash.ui.client.common.d2d.presenter;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.backup.Settings4Backup;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.service.Broker;

public class BackupSettingPresenter {
	private BackupSettingsContent backupSettingsContent;
	public BackupSettingPresenter(BackupSettingsContent backupSettingsContent){
		this.backupSettingsContent = backupSettingsContent;
	}
	
	private ISettingsContentHost getContentHost() {		
		return backupSettingsContent.getContentHost();
	}
	
	public boolean validate() {
		Broker.loginService.validateBackupConfiguration(SettingPresenter.model,
				new BaseAsyncCallback<Long>() {
					@Override
					public void onSuccess(Long result) {
						getContentHost().decreaseBusyCount();
						backupSettingsContent.onValidatingCompleted(true);
					}

					@Override
					public void onFailure(Throwable caught) {

						if (caught instanceof BusinessLogicException
								&& BackupSettingsContent.ERR_REMOTE_DEST_WINSYSMSG
										.equals(((BusinessLogicException) caught)
												.getErrorCode())) {
							if(SettingPresenter.model.isBackupToRps() != null &&
									SettingPresenter.model.isBackupToRps()){
								showErrorMessage(UIContext.Constants.destinationFailedRPS());
								backupSettingsContent.focusPanel(Settings4Backup.STACK_DESTINATION);
								getContentHost().decreaseBusyCount();
								backupSettingsContent.onValidatingCompleted(false);
							}else{
								backupSettingsContent.checkDestDriverType(caught, false);
							}
						} else {

							// Issue: 20231648 Title: FOCUS AFTER ENCRYPTION
							// LIC ERR
							// Go to destination panel for Encryption
							// License error.
							if (caught instanceof BusinessLogicException
									&& "4294967310"
											.equals(((BusinessLogicException) caught)
													.getErrorCode())) {
								backupSettingsContent.focusPanel(Settings4Backup.STACK_DESTINATION);
							}

							if (caught instanceof BusinessLogicException
									&& !((BusinessLogicException) caught)
											.getErrorCode()
											.equals("4294967302")
									&& !((BusinessLogicException) caught)
											.getErrorCode()
											.equals("4294967298")) {
								showErrorMessage((BusinessLogicException) caught);
								getContentHost().decreaseBusyCount();
								backupSettingsContent.onValidatingCompleted(false);
							} else if (caught instanceof BusinessLogicException
									&& ((BusinessLogicException) caught)
									.getErrorCode()
									.equals("17179869217")) {
								showErrorMessage((BusinessLogicException) caught);
								backupSettingsContent.focusPanel(Settings4Backup.STACK_DESTINATION);
								getContentHost().decreaseBusyCount();
								backupSettingsContent.onValidatingCompleted(false);
							} else {
								getContentHost().decreaseBusyCount();
								backupSettingsContent.onValidatingCompleted(false);
								super.onFailure(caught);
							}
						}
					}
				});

		return true;
	}
	
	public void checkDestDriverType(final Throwable caught, final boolean isSave) {		
		
		Broker.commonService.getDestDriveType(SettingPresenter.model.getDestination(), new BaseAsyncCallback<Long>()
	             {
					@Override
					public void onFailure(Throwable caught) {
						getContentHost().decreaseBusyCount();
						backupSettingsContent.onValidatingCompleted(false);
						super.onFailure(caught);
					}
					
			    	@Override
					public void onSuccess(Long result) {
			    		if(result == PathSelectionPanel.REMOTE_DRIVE )
			    		{
			    			backupSettingsContent.popupUserPasswordWindow(isSave);
			    		}
			    		else {
			    			getContentHost().decreaseBusyCount();
			    			backupSettingsContent.onValidatingCompleted(false);
			    			super.onFailure(caught);
			    		}
					}
		    	}
	      	);
	}
	

}
