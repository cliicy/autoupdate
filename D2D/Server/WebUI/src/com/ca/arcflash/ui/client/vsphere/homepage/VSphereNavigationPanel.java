package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.homepage.navigation.NavigationPanel;

public class VSphereNavigationPanel extends NavigationPanel {
	
	public VSphereNavigationPanel(){
		taskPanel = new VSphereTaskPanel();
	}
}
