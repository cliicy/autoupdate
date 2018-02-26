package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ha.model.VCMConfigStatus;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.BackupSettingsIndividual;
import com.ca.arcflash.ui.client.homepage.HomepagePanel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.restore.RestoreMainIndividual;
import com.ca.arcflash.ui.client.restore.RestoreRecoveryPointIndividual;
import com.ca.arcflash.ui.client.restore.RestoreSearchIndividual;
import com.ca.arcflash.ui.client.vsphere.homepage.VSphereHomepagePanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class HomeContentFactory {
	public static final String LOCATION_BACKUP_SETTINGS = "backupSettingsIndividual";
	public static final String LOCATION_RESTORE_BROWSE = "restoreByBrowseIndividual";
	public static final String LOCATION_RESTORE_SEARCH = "restoreBySearchIndividual";
	public static final String LOCATION_RESTORE_MAIN   = "restoreMainIndividual";
	public static final String LOCATION_VSPHERE = "vm";
	public static final String LOCATION_D2D = "d2d";
	public static final int UI_TYPE_D2D = 0;
	public static final int UI_TYPE_VSPHERE = 1;
	public static final int UI_TYPE_RPS = 2;
	
	public static Widget getHomeContentPanel(VCMConfigStatus vcmConfig) {
		String location = Window.Location.getParameter(D2DHomePageTab.LOCATION_PARAM);
		String instanceUUID = Window.Location.getParameter("instanceuuid");
		String vmName = Window.Location.getParameter("vmname");
		Boolean bvSphere = false;
		
		if(instanceUUID != null && !instanceUUID.isEmpty() && vmName != null && !vmName.isEmpty())
		{
			bvSphere = true;
			
			UIContext.backupVM = new BackupVMModel();
			UIContext.backupVM.setVmInstanceUUID(instanceUUID);
			UIContext.backupVM.setVMName(vmName);
			
			UIContext.uiType = UI_TYPE_VSPHERE;
		}
		
		if(LOCATION_BACKUP_SETTINGS.equalsIgnoreCase(location))
		{
			return new BackupSettingsIndividual(bvSphere);
		}
		else if(LOCATION_RESTORE_BROWSE.equalsIgnoreCase(location))
		{
			return new RestoreRecoveryPointIndividual();
		} 
		else if(LOCATION_RESTORE_SEARCH.equalsIgnoreCase(location))
		{
			return new RestoreSearchIndividual();
		}
		else if(LOCATION_RESTORE_MAIN.equalsIgnoreCase(location))
		{
			return new RestoreMainIndividual();
		}
			
		Viewport viewport = new Viewport();
		viewport.setLayout(new FitLayout());
		HomepagePanel homepagePanel = new HomepagePanel();
		homepagePanel.setVcmConfigStatus(vcmConfig);
		viewport.add(homepagePanel);
		
		return viewport;
	}
	
	public static Widget getVsphereHomeContentPanel(VCMConfigStatus vcmConfig) {
		Widget homeContentPanel = null;
		if(vcmConfig != null && vcmConfig.isMonitor()) {
			VSphereHomepagePanel vsphereHomepagePanel = new VSphereHomepagePanel();
			vsphereHomepagePanel.setVcmConfigStatus(vcmConfig);
			Viewport viewport = new Viewport();
			viewport.setLayout(new FitLayout());
			viewport.add(vsphereHomepagePanel);
			homeContentPanel = viewport;
		}
		else{
			Viewport viewport = new Viewport();
			viewport.setLayout(new FitLayout());
			viewport.add(new VSphereHomepagePanel());
			homeContentPanel = viewport;
		}
		return homeContentPanel;
	}
}
