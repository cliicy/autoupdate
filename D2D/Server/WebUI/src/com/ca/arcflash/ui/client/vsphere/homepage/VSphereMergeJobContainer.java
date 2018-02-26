package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.homepage.MergeJobContainer;
import com.ca.arcflash.ui.client.homepage.MergePanel;
import com.ca.arcflash.ui.client.homepage.MergeRunningPanel;
import com.ca.arcflash.ui.client.model.MergeStatusModel;

public class VSphereMergeJobContainer extends MergeJobContainer {	
	
	public VSphereMergeJobContainer() {
		setupCallback();
	}
	
	@Override
	protected MergePanel createMergePanel(MergeStatusModel model) {
		return new VSphereMergePanel(model);
	}

	@Override
	protected MergeRunningPanel createMergeRunningPanel(MergeStatusModel model) {
		return new VSphereMergeRunningPanel(model);
	}

	@Override
	public void refresh() {
		service.getMergeStatus(UIContext.backupVM.getVmInstanceUUID(), callback);
	}
	
	@Override
	protected void refreshHostPage() {
		if(UIContext.vSphereHomepagePanel != null)
			UIContext.vSphereHomepagePanel.refresh(null);
	}
}
