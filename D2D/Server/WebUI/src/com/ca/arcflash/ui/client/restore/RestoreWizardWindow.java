package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class RestoreWizardWindow extends Window {

	protected RestoreWizardContainer rwContainer;

	public static final int RESTORE_WIZARD_WIDTH = new Float(1.04 * RestoreWizardContainer.RESTORE_WIZARD_WIDTH).intValue();
	public static final int RESTORE_WIZARD_HEIGHT = new Float(1.08 * RestoreWizardContainer.RESTORE_WIZARD_HEIGHT).intValue();

	public RestoreWizardWindow() {
		
		this(RestoreWizardContainer.PAGE_INTRO);		
	}
	
	// specify the default page
	public RestoreWizardWindow(int nPageIndex) {
		
		// make restore wizard resizable
		this.setLayout(new FitLayout());
		this.setResizable(false);
		//this.setMaximizable(true);
		this.setWidth(RESTORE_WIZARD_WIDTH);
		this.setHeight(RESTORE_WIZARD_HEIGHT);
		if(Utils.getScreenHeight()<= Utils.LEAST_RECOMMENDED_HEIGHT)
		{				
			this.setMinHeight(RESTORE_WIZARD_HEIGHT-300);			
		}	
		
		this.setHeadingHtml(UIContext.Constants.restoreWindowTitle());

		rwContainer = new RestoreWizardContainer(nPageIndex);

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

	@Override
	protected void onLoad() {
		super.onLoad();
		resetSize();
	}

	@Override
	protected void onWindowResize(int width, int height) {
		super.onWindowResize(width, height);
		resetSize();
	}
	
	private void resetSize(){
		if(this.getHeight() > Utils.getScreenHeight() * 0.75) {
			this.setHeight((int)(Utils.getScreenHeight() * 0.75));
		}
	}
}
