package com.ca.arcflash.ui.client.vsphere.log;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class VSphereLogWindow extends Window {
	public static final int WINDOW_WIDTH 		= 750;
	public static final int WINDOW_HEIGHT		= 500;
	
	private Window window;
	//It is true when 1. login vSphere vcm, 2. login VCM monitor and select a vSphere VM on the tree.
	private boolean isOnVCMTab = false;
	
	public VSphereLogWindow(boolean isOnVCMTab){
		this.isOnVCMTab = isOnVCMTab;
		initialize();
	}
	
	public VSphereLogWindow(){
		initialize();
	}

	private void initialize() {
		this.window = this;
		this.setHeadingHtml(UIContext.Constants.activityLogTableHeader());
		this.setClosable(false);
		//this.setScrollMode(Scroll.AUTOY);
		this.setHeight(WINDOW_HEIGHT);
		this.setWidth(WINDOW_WIDTH);
		this.setResizable(true);
		this.setMaximizable(true);
		this.setClosable(true);
		this.setLayout(new FitLayout());
		
		LayoutContainer container = new LayoutContainer();
		//container.setAutoHeight(true);
		//container.setAutoWidth(true);
		container.setStyleAttribute("padding", "6px");
		container.setLayout(new FitLayout());
		
		container.add(new VSphereLogPanel(isOnVCMTab));
		
		Button okButton = new Button(UIContext.Constants.ok());
		okButton.ensureDebugId("D9092643-0E6B-40fc-AD5E-27C3F63307C8");
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		});
		
		this.addButton(okButton);
		
		Button helpButton = HelpTopics.createHelpButton(UIContext.externalLinks.getVMLogActivityHelp(), -1);
		this.addButton(helpButton);
		
		this.add(container);
	}
	
	/**
	 * It is true when 1. login vSphere vcm, 2. login VCM monitor and select a vSphere VM on the tree.
	 * @return
	 */
	public boolean isOnVCMTab() {
		return isOnVCMTab;
	}

	public void setOnVCMTab(boolean isOnVCMTab) {
		this.isOnVCMTab = isOnVCMTab;
	}
}
