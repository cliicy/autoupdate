package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class RestoreWizardWindowForEdge extends Window {
	
	protected RestoreWizardContainerForEdge rwContainer;
	public static final int RESTORE_WIZARD_WIDTH = new Float(1.04 * RestoreWizardContainer.RESTORE_WIZARD_WIDTH).intValue();
	public static final int RESTORE_WIZARD_HEIGHT = new Float(1.1 * RestoreWizardContainer.RESTORE_WIZARD_HEIGHT).intValue();
	
	public RestoreWizardWindowForEdge(int restoreType) {
		
		this.setLayout(new FitLayout());
		this.setMinWidth(RESTORE_WIZARD_WIDTH);
		this.setMinHeight(RESTORE_WIZARD_HEIGHT);
		
		this.setHeadingHtml(UIContext.Constants.restoreWindowTitle());
		
		rwContainer = new RestoreWizardContainerForEdge(RestoreWizardContainerForEdge.PAGE_INTRO);
		rwContainer.restoreType = restoreType;
		
		switch (restoreType) {
		case RestoreWizardContainer.RESTORE_BY_BROWSE:
			rwContainer.SetPage(RestoreWizardContainer.PAGE_RECOVERY);
			break;
		case RestoreWizardContainer.RESTORE_BY_SEARCH:
			rwContainer.SetPage(RestoreWizardContainer.PAGE_SEARCH);
			break;
		case RestoreWizardContainer.RECOVER_VM:
			rwContainer.SetPage(RestoreWizardContainer.PAGE_VM_RECOVERY);
			break;
		case RestoreWizardContainer.RESTORE_BY_BROWSE_EXCHANGE_GRT:
			rwContainer.SetPage(RestoreWizardContainer.PAGE_EXCHANGE_GRT_RECOVERY);
			break;
		case RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE:
			rwContainer.SetPage(RestoreWizardContainer.PAGE_ARCHIVE_RECOVERY);
			break;
		default:
			break;
		}
		
		rwContainer.addListener(RestoreWizardContainer.CloseMessage, new Listener<BaseEvent>(){
			
			@Override
			public void handleEvent(BaseEvent be) {

				hide();	
				
				if (rwContainer != null)
				{
					rwContainer.hide();
				}
			}

		});

		this.addListener(Events.Hide, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				if (rwContainer != null)
				{
					rwContainer.hide();
				}
			}
		});


		this.add(rwContainer);

//		this.setWidth(RESTORE_WIZARD_WIDTH);
//		this.setHeight(RESTORE_WIZARD_HEIGHT);		
	}

	public boolean isCancel() {
		return rwContainer.isCancel();
	}
	
}
