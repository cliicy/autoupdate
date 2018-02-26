package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.homepage.MergePanel;
import com.ca.arcflash.ui.client.model.MergeStatusModel;

public class VSphereMergePanel extends MergePanel {	
	public VSphereMergePanel(MergeStatusModel model) {
		super(model);
	}

	@Override
	protected void resumeMerge(BaseAsyncCallback<Integer> callback) {
		service.resumeMerge(UIContext.backupVM.getVmInstanceUUID(), callback);
	}
}
