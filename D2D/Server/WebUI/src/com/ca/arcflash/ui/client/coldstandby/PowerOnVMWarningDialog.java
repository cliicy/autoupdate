package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;

public class PowerOnVMWarningDialog extends Dialog {
	private Label warningMessageLabel = new Label();
	private CheckBox usingIPConfiguration = new CheckBox();
	private boolean isOK;
	public PowerOnVMWarningDialog() {
		this.setHeadingHtml(UIContext.Constants.provistionPointPowerOnWarning());
		this.setButtons(Dialog.YESNO);
		usingIPConfiguration.setBoxLabel(UIContext.Constants.applyCustomizeNetwork());
		usingIPConfiguration.setValue(true);
		warningMessageLabel.setStyleAttribute("font-size", "12px");
		this.add(warningMessageLabel);
		this.add(usingIPConfiguration);
		this.setUsingIPConfigurationEnable(false);
		this.setModal(true);
		this.setWidth(400);
		addStyleName("x-window-dlg");
	}
	
	public void setMessage(String message) {
		warningMessageLabel.setHtml(message);
	}
	
	public void setUsingIPConfigurationEnable(boolean enableFlag) {
		usingIPConfiguration.setVisible(enableFlag);
	}
	
	public boolean getUsingIPConfigurationEnable() {
		return usingIPConfiguration.getValue();
	}
	
	@Override
	protected void onButtonPressed(Button button) {
		super.onButtonPressed(button);
		if (button == getButtonBar().getItemByItemId(YES)) {
			isOK = true;
		} else {
			isOK = false;
		}
		hide();
	}
	
	public boolean isOKButtonClick() {
		return isOK;
	}
}
