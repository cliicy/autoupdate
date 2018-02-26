package com.ca.arcflash.ui.client.vsphere.vmrecover;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.ca.arcflash.ui.client.restore.RestoreSourcePanel;

public class VMRestoreSourcePanel extends RestoreSourcePanel {

	@Override
	protected void getDefaultValue() {

		service.getVMBackupConfiguration(UIContext.backupVM, new BaseAsyncCallback<VMBackupSettingModel>() {
			@Override
			public void onFailure(Throwable caught) {
				// Failed to get a proper value
				// the restore source is initialized
				listener.onDefaultSourceInitialized(false);
				setWidgetsVisible(false);
			}

			@Override
			public void onSuccess(VMBackupSettingModel result) {
				RestoreContext.setBackupModel(result);
				if (result != null && result.getDestination() != null) {
//					listener.onDefaultSourceInitialized(true);
					onGetDefaultValueSucceed(result.getBackupVM().getDestination(), 
							result.getBackupVM().getDesUsername(), 
							result.getBackupVM().getDesPassword(), result.isBackupToRps(), 
							result.rpsDestSettings, 
							result.getBackupVM().getVMName() + "@" + result.getBackupVM().getEsxServerName());
				} else {
					// the restore source is initialized
					listener.onDefaultSourceInitialized(false);
					setWidgetsVisible(false);
				}
			}
		});
	}
}
