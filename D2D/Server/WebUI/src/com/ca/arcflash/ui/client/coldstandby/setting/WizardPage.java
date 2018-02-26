package com.ca.arcflash.ui.client.coldstandby.setting;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

abstract class WizardPage extends LayoutContainer {
	
	protected boolean validate(){
		return true;
	}
	
	protected void activate(){
		
	}
	
	abstract public String getTitle();
		
	abstract public String getDescription();
}
