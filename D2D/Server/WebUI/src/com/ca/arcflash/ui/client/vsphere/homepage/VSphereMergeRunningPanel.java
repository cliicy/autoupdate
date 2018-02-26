package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.homepage.MergeRunningPanel;
import com.ca.arcflash.ui.client.model.MergeStatusModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class VSphereMergeRunningPanel extends MergeRunningPanel {
	public VSphereMergeRunningPanel(MergeStatusModel model) {
		super(model);
	}

	@Override
	protected void pauseMerge(AsyncCallback<Integer> callback) {
		service.pauseMerge(UIContext.backupVM.getVmInstanceUUID(), callback);
	}

	@Override
	protected void refreshHostPage(int refreshSource) {
		if(UIContext.vSphereHomepagePanel != null)
			UIContext.vSphereHomepagePanel.refresh(null);
	}
}
