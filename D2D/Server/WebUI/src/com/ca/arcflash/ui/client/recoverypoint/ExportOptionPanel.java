package com.ca.arcflash.ui.client.recoverypoint;

import com.ca.arcflash.ui.client.model.RecoveryPointModel;

public class ExportOptionPanel extends BaseExportOptionPanel {

	private RecoveryPointWindow recoveryPointWindow;
	
	public ExportOptionPanel(RecoveryPointWindow recoveryPointWindow) {
		this.recoveryPointWindow = recoveryPointWindow;
	}

	@Override
	protected RecoveryPointModel getSelectedRecoveryPoints() {
		return recoveryPointWindow.getSelectedRecoveryPoint();
	}

	@Override
	protected boolean isBackup2RPS() {
		return recoveryPointWindow.isBackupToRps();
	}

	@Override
	protected String getSessionPath() {
		return recoveryPointWindow.getSessionPath();
	}

	public boolean isValid() {
		return encryptionPane.isValid(null);
	}
}
