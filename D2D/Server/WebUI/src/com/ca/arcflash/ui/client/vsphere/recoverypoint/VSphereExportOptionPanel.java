package com.ca.arcflash.ui.client.vsphere.recoverypoint;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.recoverypoint.BaseExportOptionPanel;

public class VSphereExportOptionPanel extends BaseExportOptionPanel {
	private VSphereRecoveryPointWindow recoveryPointWindow;
	
	public VSphereExportOptionPanel(VSphereRecoveryPointWindow recoveryPointWindow) {
		this.recoveryPointWindow = recoveryPointWindow;
	}

	@Override
	protected String getSessionPath() {
		return recoveryPointWindow.getSessionPath();
	}
	
	protected String getProductTitle(){
		return UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
	}
	
	@Override
	protected RecoveryPointModel getSelectedRecoveryPoints() {
		return recoveryPointWindow.getSelectedRecoveryPoint();
	}

	@Override
	protected boolean isBackup2RPS() {
		return recoveryPointWindow.isBackupToRps();
	}
}
