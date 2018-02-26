package com.ca.arcflash.ui.client.vsphere.vmrecover;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.restore.RestoreConstants;
import com.ca.arcflash.ui.client.restore.RestoreSearchPanel;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.google.gwt.user.client.Element;

public class VMRestoreSearchPanel extends RestoreSearchPanel {
	private VMRestoreSearchPanel thisPanel;

	public VMRestoreSearchPanel(RestoreWizardContainer restoreWizardWindow) {
		super(restoreWizardWindow);
		thisPanel = this;
		isFileCopyEnabled = false;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
//		cbSelectArchiveForSearch.hide();
		if(fileCopyField != null)
			fileCopyField.setVisible(false);
		if(lcArchiveDestinationContainer != null)
			lcArchiveDestinationContainer.hide();
	}

	/*@Override
	protected void getDefaultSourceValue() {
		service
		.getVMBackupConfiguration(UIContext.backupVM, new BaseAsyncCallback<VMBackupSettingModel>() {

			@Override
			public void onFailure(Throwable caught) {
				if (!isWndClosed) {
					super.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(VMBackupSettingModel result) {
				if (result != null && result.getBackupVM().getDestination() != null) {
					
					thisPanel.pathSelection.setDestination(result.getBackupVM().getDestination());
					thisPanel.pathSelection.setUsername(result.getBackupVM().getDesUsername());
					thisPanel.pathSelection.setPassword(result.getBackupVM().getDesPassword());
					bSearchBackups = true;
					
					strBackupDestination = result.getDestination();
					strBackupDestinationUsername = result.getDestUserName();
					strBackupDestinationPassword = result.getDestPassword();
												
				}
				else
				{
					bSearchBackups = false;
					cbSelectBackupForSearch.setValue(false);
					pathSelection.setEnabled(false);
				}
				
				refreshSearchContainer();
			}

		});
	}*/
	
	@Override
	protected int getSessionTableHeight() {
		return 240;
	}

	@Override
	protected void disableCheckBox(RecoveryPointModel model, FlashCheckBox box) {
		if(model.getFSCatalogStatus() == RestoreConstants.FSCAT_NOTCREATE){
			box.setEnabled(false);
			Utils.addToolTip(box, 
					UIContext.Constants.restoerSearchVMPowerOffCatalog());
		}
	}
}
