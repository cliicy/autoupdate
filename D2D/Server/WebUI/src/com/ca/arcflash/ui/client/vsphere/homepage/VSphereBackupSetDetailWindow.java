package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.homepage.BackupSetsDetailWindow;

public class VSphereBackupSetDetailWindow extends BackupSetsDetailWindow {

	@Override
	protected void refresh() {
		service.getBackupSetInfo(UIContext.backupVM.getVmInstanceUUID(), asyncCallback);
	}
}
