package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.jobscript.failover.Gateway;
import com.ca.arcflash.jobscript.failover.IPAddressInfo;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.ui.client.UIContext;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;

public class NetworkPanel extends Composite {
	protected FlexTable table = new FlexTable();
	
	@SuppressWarnings("deprecation")
	public NetworkPanel(NetworkAdapter adapter){
		DisclosurePanel networkDisclosurePanel = new DisclosurePanel(
				(DisclourePanelImageBundles)GWT.create(DisclourePanelImageBundles.class),
				adapter.getAdapterName(), false);
		networkDisclosurePanel.ensureDebugId("47c04eeb-fc57-43c4-9ad8-3c79bb4a21e2");
		networkDisclosurePanel.setStylePrimaryName("gwt-DisclosurePanel-network");
		
		FlexTable internalTable = new FlexTable();
		internalTable.ensureDebugId("0096a420-bffb-4d47-9732-99fa839429b2");
		internalTable.setWidth("100%");
		
		table.setWidth("100%");
		
		table.setText(0, 0, UIContext.Constants.coldStandbyAdapterType());
		table.getCellFormatter().setStyleName(0, 0, "panel-text-label");
		table.setText(0, 1, adapter.getAdapterType());
		table.getCellFormatter().setStyleName(0, 1, "panel-text-value");
		
		table.setText(1, 0, UIContext.Constants.coldStandbyNetworkConnection());
		table.getCellFormatter().setStyleName(1, 0, "panel-text-label");
		table.setText(1, 1, adapter.getNetworkLabel());
		table.getCellFormatter().setStyleName(1, 1, "panel-text-value");
		int row = 0;
		if (adapter.getIpSettings() != null) {			
			for (IPSetting ipSetting : adapter.getIpSettings()) {
				if(ipSetting.isDhcp()) {
					row = table.getRowCount();
					table.setText(row, 0, UIContext.Constants.coldStandbyIpAndSubnet());
					table.getCellFormatter().setStyleName(row, 0, "panel-text-label");
					table.setText(row, 1, UIContext.Constants.coldStandbyDHCPEnabled());
					table.getCellFormatter().setStyleName(row, 1, "panel-text-value");
				} else {
					boolean showTitle = false;
					for (IPAddressInfo ipAddressInfo : ipSetting.getIpAddresses()) {
						row = table.getRowCount();
						String ipAndSubnet = ipAddressInfo.getIp() + "/" + ipAddressInfo.getSubnet();
						if (!showTitle) {
							showTitle = true;
							table.setText(row, 0, UIContext.Constants.coldStandbyIpAndSubnet());
							table.getCellFormatter().setStyleName(row, 0, "panel-text-label");
						}
						table.setText(row, 1, ipAndSubnet);
						table.getCellFormatter().setStyleName(row, 1, "panel-text-value");
					}
				}
				if (ipSetting.getGateways().size() == 0) {				
					row = table.getRowCount();
					table.setText(row, 0, UIContext.Constants.coldStandbyGateways());
					table.getCellFormatter().setStyleName(row, 0, "panel-text-label");
					table.setText(row, 1, UIContext.Constants.coldStandbyAutomatic());
					table.getCellFormatter().setStyleName(row, 1, "panel-text-value");
				} else {
					boolean showTitle = false;
					for (Gateway gateway : ipSetting.getGateways()) {
						row = table.getRowCount();
						if (!showTitle) {
							showTitle = true;
							table.setText(row, 0, UIContext.Constants.coldStandbyGateways());
							table.getCellFormatter().setStyleName(row, 0, "panel-text-label");
						}
						table.setText(row, 1, gateway.getGatewayAddress());
						table.getCellFormatter().setStyleName(row, 1, "panel-text-value");
					}
				} 
				if (ipSetting.getDnses().size() == 0) {
					row = table.getRowCount();
					table.setText(row, 0, UIContext.Constants.coldStandbyDNSServers());
					table.getCellFormatter().setStyleName(row, 0, "panel-text-label");
					table.setText(row, 1, UIContext.Constants.coldStandbyAutomatic());
					table.getCellFormatter().setStyleName(row, 1, "panel-text-value");
				} else {
					boolean showTitle = false;
					for (String  dns : ipSetting.getDnses()) {
						row = table.getRowCount();
						if (!showTitle) {
							showTitle = true;
							table.setText(row, 0, UIContext.Constants.coldStandbyDNSServers());
							table.getCellFormatter().setStyleName(row, 0, "panel-text-label");
						}
						table.setText(row, 1, dns);
						table.getCellFormatter().setStyleName(row, 1, "panel-text-value");
					}
				}
				if (ipSetting.getWins().size() == 0) {
					row = table.getRowCount();
					table.setText(row, 0, UIContext.Constants.coldStandbyWINSServers());
					table.getCellFormatter().setStyleName(row, 0, "panel-text-label");
					table.setText(row, 1, UIContext.Constants.coldStandbyAutomatic());
					table.getCellFormatter().setStyleName(row, 1, "panel-text-value");
				} else {
					boolean showTitle = false;
					for (String  wins : ipSetting.getWins()) {
						row = table.getRowCount();
						if (!showTitle) {
							showTitle = true;
							table.setText(row, 0, UIContext.Constants.coldStandbyWINSServers());
							table.getCellFormatter().setStyleName(row, 0, "panel-text-label");
						}
						table.setText(row, 1, wins);
						table.getCellFormatter().setStyleName(row, 1, "panel-text-value");
					}
				}
			}
		}
		
		internalTable.setWidget(0, 0, new HTML("<div style=\"width: 30px;\"/>"));
		internalTable.setWidget(0, 1, table);
		
		networkDisclosurePanel.setOpen(true);
		networkDisclosurePanel.add(internalTable);
		this.initWidget(networkDisclosurePanel);
	}
}
