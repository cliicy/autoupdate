package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.MessageBox.MessageBoxType;

abstract class WizardPage extends LayoutContainer {
	
	protected boolean validate(){
		return true;
	}
	
	protected void activate(){
		
	}
	
	abstract public String getTitle();
		
	abstract public String getDescription();
	
	protected void showMessageBox(String debugID, String errorMsg){
		MessageBox messageBox = new MessageBox();
		messageBox.getDialog().ensureDebugId(debugID);
		messageBox.setMinWidth(200);
		messageBox.setType(MessageBoxType.ALERT);
		messageBox.setIcon(MessageBox.ERROR);
		messageBox.setTitleHtml(UIContext.Constants.failed());
		messageBox.setMessage(errorMsg);
		Utils.setMessageBoxDebugId(messageBox);
		messageBox.show();
	}
}
