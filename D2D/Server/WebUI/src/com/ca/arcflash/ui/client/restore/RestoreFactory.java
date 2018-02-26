package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.model.RestoreJobType;
import com.ca.arcflash.ui.client.restore.ad.ActiveDirectoryOptionPanel;
import com.ca.arcflash.ui.client.restore.ad.ActiveDirectorySummaryPanel;
import com.ca.arcflash.ui.client.vsphere.vmrecover.VMRecoveryOptionsPanel;
import com.ca.arcflash.ui.client.vsphere.vmrecover.VMRecoverySummaryPanel;

public class RestoreFactory {
	private static RestoreFactory fac = new RestoreFactory();

	public static RestoreFactory getInstance() {
		return fac;
	}

	private RestoreOptionsPanel optionsPanal;

	public RestoreOptionsPanel getRestoreOptionPanal(RestoreJobType kind,        ///D2D Lite Integration
		RestoreWizardContainer wiz) {
		switch (kind) {
		case FileSystem:
			optionsPanal = new FSRestoreOptionsPanel(wiz);
			break;
		case VSS_SQLServer:
			optionsPanal = new SQLRestoreOptionsPanel(wiz);
			break;
		case VSS_Exchange:
			optionsPanal = new ExchangeRestoreOptionsPanel(wiz);
			break;
		case VM_Recovery:
			optionsPanal = new VMRecoveryOptionsPanel(wiz);
			break;
        case GRT_Exchange: 
			optionsPanal = new ExchangeGRTRestoreOptionPanel(wiz);
			break;
        case ActiveDirectory:
        	optionsPanal = new ActiveDirectoryOptionPanel(wiz);
		}
		return optionsPanal;
	}

	private RestoreSummaryPanel summaryPanal;

	public RestoreSummaryPanel getRestoreSummaryPanel(RestoreJobType kind,       ///D2D Lite Integration
			RestoreWizardContainer wiz) {

		switch (kind) {
		case FileSystem:
			summaryPanal = new FSRestoreSummaryPanel(wiz);
			break;
		case VSS_SQLServer:
			summaryPanal = new SQLRestoreSummaryPanel(wiz);
			break;
		case VSS_Exchange:
			summaryPanal = new ExchangeRestoreSummaryPanel(wiz);
			break;
		case VM_Recovery:
			summaryPanal = new VMRecoverySummaryPanel(wiz);
			break;	
        case GRT_Exchange: 
			summaryPanal = new ExchangeGRTRestoreSummaryPanel(wiz);
			break;
        case ActiveDirectory:
        	summaryPanal = new ActiveDirectorySummaryPanel(wiz);
		}
		return summaryPanal;
	}
}
