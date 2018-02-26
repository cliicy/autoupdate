package com.ca.arcflash.ui.client.common.d2d.presenter;

import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.export.ScheduledExportSettingsContent;
import com.ca.arcflash.ui.client.service.Broker;

public class CopyRecoveryPointPresenter {

	private ScheduledExportSettingsContent copyRecoveryPoinSettingsContent;

	public CopyRecoveryPointPresenter(
			ScheduledExportSettingsContent copyRecoveryPoinSettingsContent) {
		this.copyRecoveryPoinSettingsContent = copyRecoveryPoinSettingsContent;
	}
	
	public void validate() {
		Broker.loginService.validateScheduledExportConfiguration(copyRecoveryPoinSettingsContent.model, new BaseAsyncCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				super.onSuccess(result);
				copyRecoveryPoinSettingsContent.onValidatingCompleted(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				if(caught instanceof BusinessLogicException 
						&& ScheduledExportSettingsContent.ERR_REMOTE_DEST_WINSYSMSG.equals(((BusinessLogicException)caught).getErrorCode()))
					{
						final Throwable orginialExc = caught;					
						Broker.commonService.getDestDriveType(copyRecoveryPoinSettingsContent.model.getDestination(), new BaseAsyncCallback<Long>()
					             {
									@Override
									public void onFailure(Throwable caught) {
										copyRecoveryPoinSettingsContent.onValidatingCompleted(false);
										super.onFailure(orginialExc);
									}
							    	@Override
									public void onSuccess(Long result) {
							    		if(result == PathSelectionPanel.REMOTE_DRIVE )
							    		{
							    			copyRecoveryPoinSettingsContent.popupUserpasswordWindow();
							    		}
							    		else {
							    			copyRecoveryPoinSettingsContent.onValidatingCompleted(false);
							    			super.onFailure(orginialExc);
							    		}
									}
						    	}
					      	);
					}else{
						copyRecoveryPoinSettingsContent.onValidatingCompleted(false);
					    super.onFailure(caught);
					}
			}
		});
	}
	
	public boolean save() {
		getContentHost().increaseBusyCount();
		Broker.loginService.saveScheduledExportConfiguration(copyRecoveryPoinSettingsContent.model,
				new BaseAsyncCallback<Long>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						copyRecoveryPoinSettingsContent.onSavingCompleted(false);
						getContentHost().decreaseBusyCount();
					}

					@Override
					public void onSuccess(Long result) {
						copyRecoveryPoinSettingsContent.onSavingCompleted(true);
						getContentHost().decreaseBusyCount();
					}
				});
		return false;
	}

	private ISettingsContentHost getContentHost() {		
		return copyRecoveryPoinSettingsContent.getContentHost();
	}

}
