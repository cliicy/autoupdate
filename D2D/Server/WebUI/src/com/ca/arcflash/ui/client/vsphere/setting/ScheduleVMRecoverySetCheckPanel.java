package com.ca.arcflash.ui.client.vsphere.setting;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.HasValidateValue;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class ScheduleVMRecoverySetCheckPanel extends LayoutContainer implements HasValidateValue<BackupSettingsModel>{
	private CheckBox cbRecoverySetCheck = new CheckBox();
	protected static FlashUIConstants uiConstants= UIContext.Constants;
	private LabelField labelCatalogDesctiption;	
	
	public ScheduleVMRecoverySetCheckPanel() {
		DisclosurePanel disclosurePanel = Utils.getDisclosurePanel(uiConstants.planVMBackupVSphereCheckRecoveryPointTitle());
		LayoutContainer container = new LayoutContainer();
		disclosurePanel.add(container);
		
		labelCatalogDesctiption = new LabelField(uiConstants.planVMBackupVSphereCheckRecoveryPointDescription());
		labelCatalogDesctiption.setStyleAttribute("margin-bottom", "1px");
		container.add(labelCatalogDesctiption);

		cbRecoverySetCheck.ensureDebugId("2df941bc-2b87-4f02-a4cd-87095fe4df2e");
		cbRecoverySetCheck.setVisible(true);
		cbRecoverySetCheck.setBoxLabel(uiConstants.planVMBackupVSphereCheckRecoveryPointTitle());
//		.addToolTip(cbRecoverySetCheck, UIContext.Constants.destinationCatalogTooltip());
		cbRecoverySetCheck.setStyleAttribute("white-space", "normal");
		container.add(cbRecoverySetCheck);
		container.disable();
		this.add(disclosurePanel);
	}
	
	@Override
	public void buildValue(BackupSettingsModel value) {
	}

	@Override
	public void applyValue(BackupSettingsModel value) {
		cbRecoverySetCheck.setValue(value.getCheckRecoveryPoint());
	}

	@Override
	public boolean validate() {
		return true;
	}
}

	