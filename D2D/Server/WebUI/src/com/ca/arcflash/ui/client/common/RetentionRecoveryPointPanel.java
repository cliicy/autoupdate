package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.RetentionPolicyModel;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class RetentionRecoveryPointPanel extends LayoutContainer {

	private NumberField recoveryPointsNumber;

	public int getRetentionCount() {
		return recoveryPointsNumber.getValue().intValue();
	}
	
	
	public NumberField getRetentionCountField() {
		return recoveryPointsNumber;
	}

	public RetentionRecoveryPointPanel(BackupSettingsContent w) {
		this.setStyleAttribute("margin-left", "5px");

		TableLayout totalLayout = new TableLayout();
		totalLayout.setColumns(1);
		totalLayout.setCellPadding(2);
		totalLayout.setCellSpacing(0);
		totalLayout.setWidth("97%");
		this.setLayout(totalLayout);

		LabelField labelRecoveryPoint = new LabelField();
		labelRecoveryPoint.setValue(UIContext.Constants
				.settingRecoveryPointsNumCon());
		// rpDesp.setStyleAttribute("margin-left", "4px");
		this.add(labelRecoveryPoint);

		recoveryPointsNumber = new NumberField();
		if (GXT.isIE)
			recoveryPointsNumber.setStyleAttribute("margin-left", "8px");
		else
			recoveryPointsNumber.setStyleAttribute("margin-left", "15px");
		recoveryPointsNumber.setMaxValue(UIContext.maxRPLimit);
		recoveryPointsNumber.setMinValue(1);
		recoveryPointsNumber.setValue(31);
		recoveryPointsNumber.setAllowBlank(false);
		recoveryPointsNumber.setAllowDecimals(false);
		recoveryPointsNumber.setValidateOnBlur(true);
		recoveryPointsNumber.setWidth(100);
		recoveryPointsNumber.getMessages().setMaxText(
				UIContext.Messages
						.settingsRetentionCountExceedMax(UIContext.maxRPLimit));
		recoveryPointsNumber.getMessages().setMinText(
				UIContext.Constants.settingsRetentionCountErrorTooLow());

		this.add(recoveryPointsNumber);
	}

	public boolean validate() {

		// verify retention count
		Number n = recoveryPointsNumber.getValue();
		if (n == null || n.intValue() == 0) {
			String title = UIContext.Constants.backupSettingsDestination();
			String msgStr = UIContext.Constants
					.settingsRetentionCountErrorTooLow();
			recoveryPointsNumber.setValue(1);
			this.popupMessage(title, msgStr, MessageBox.ERROR, null, null);
			return false;
		} else if (n.intValue() > UIContext.maxRPLimit) {
			String title = UIContext.Constants.backupSettingsDestination();
			String msgStr = UIContext.Messages
					.settingsRetentionCountExceedMax(UIContext.maxRPLimit);
			recoveryPointsNumber.setValue(UIContext.maxRPLimit);
			recoveryPointsNumber.fireEvent(Events.Change);
			this.popupMessage(title, msgStr, MessageBox.ERROR, null, null);
			return false;
		}

		return true;
	}

	public void setEditable(boolean isEditable) {
		this.recoveryPointsNumber.setEnabled(isEditable);
	}

	private void popupMessage(String title, String message, String icon,
			String buttons, Listener<MessageBoxEvent> callback) {
		MessageBox msg = new MessageBox();
		msg.setIcon(icon);
		msg.setTitleHtml(title);
		msg.setMessage(message);
		if (buttons != null && !buttons.isEmpty()) {
			msg.setButtons(buttons);
		}
		if (callback != null)
			msg.addCallback(callback);
		msg.setModal(true);
		Utils.setMessageBoxDebugId(msg);
		msg.show();
	}
	
	public void refreshData(BackupSettingsModel model) {
		if (model == null)
			return;

		if (model.getRetentionCount() != null && model.getRetentionCount() > 0) {
			recoveryPointsNumber.setValue(model.getRetentionCount());
		}
	}

	public RetentionPolicyModel saveData() {
		RetentionPolicyModel model = new RetentionPolicyModel();
		model.setUseBackupSet(false);
		model.setRetentionCount(recoveryPointsNumber.getValue().intValue());
		return model;
	}

}
