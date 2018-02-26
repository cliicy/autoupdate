package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.jobscript.failover.HyperVNetworkAdapter;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;

public class HyperVNetworkPanel  extends NetworkPanel {

	public HyperVNetworkPanel(HyperVNetworkAdapter adapter) {
		super(adapter);
		int row = table.getRowCount();
		
		HyperVNetworkAdapter hyperVAdaper = (HyperVNetworkAdapter)adapter;
		table.setText(row, 0, UIContext.Constants.coldStandbyNetworkVirtualLanID());
		table.getCellFormatter().setStyleName(row, 0, "panel-text-label");
		table.setText(row, 1, Utils.convert2UILabel(hyperVAdaper.getVirtualLanID()));
		table.getCellFormatter().setStyleName(row, 1, "panel-text-value");
	}
}
