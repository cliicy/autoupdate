package com.ca.arcflash.ui.client.restore;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class RestoreRecoveryPointIndividual extends LayoutContainer {
	
	private RestoreWizardContainer rwContainer;
	
	public static final String D2DPageClose = "http://ClosePageRequest";
	
	public RestoreRecoveryPointIndividual()
	{
		this.setWidth(705);
		
		rwContainer = new RestoreWizardContainer(RestoreWizardContainer.PAGE_RECOVERY);
		
		rwContainer.addListener(RestoreWizardContainer.CloseMessage, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				com.google.gwt.user.client.Window.Location.replace(D2DPageClose);		
			}
			
		});		

		this.add(rwContainer);
	}
}
