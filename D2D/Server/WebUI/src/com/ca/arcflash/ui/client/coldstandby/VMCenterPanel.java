package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.VMwareNetworkAdapter;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VMCenterPanel extends Composite {
	private FlexTable table;
	private Label typeLabel;
	private Label nameLabel;
	private Label processorLabel;
	private Label memoryLabel;
	private Label dataCenterLabel;
	private Label dataStoreLabel;
	private Label esxHostNameLabel;
	private Label networkLabel;
	private VerticalPanel networkPanel;
	private Label versionLabel;
	private VerticalPanel dataStorePanel;
	
	public VMCenterPanel(){
		table = new FlexTable();
		table.ensureDebugId("64e744f8-28cc-4e79-92d4-ce134935af82");
		table.getElement().getStyle().setPadding(4, Unit.PX);
		table.getElement().getStyle().setPaddingBottom(12, Unit.PX);
		table.setWidth("auto");
//		table.setBorderWidth(1);
		
		typeLabel = addKeyValue(UIContext.Constants.coldStandbyVMType());
		typeLabel.ensureDebugId("d24de975-dd13-42d5-8d81-4f8489518aca");
		
		dataCenterLabel = addKeyValue(UIContext.Constants.coldStandbyVMDataCenter());
		dataCenterLabel.ensureDebugId("728a36c3-fc62-481d-9690-26a00c5b4aaa");
		
		versionLabel = addKeyValue(UIContext.Constants.coldStandbyVMVersion());
		versionLabel.ensureDebugId("9e2bef6e-10dc-4b70-be55-b3269f8d82c9");
		
		esxHostNameLabel = addKeyValue(UIContext.Constants.coldStandbyVMESXHostName());
		esxHostNameLabel.ensureDebugId("cb18bf3f-6231-4157-932e-805bc721e500");
		
		insertBlankRow();
		
		nameLabel = addKeyValue(UIContext.Constants.coldStandbyVMName());
		nameLabel.ensureDebugId("3c4518c4-ed9a-49c8-aa97-73c90d3e3fb0");
		
		processorLabel = addKeyValue(UIContext.Constants.coldStandbyVMProcessor());
		processorLabel.ensureDebugId("c7f1431f-8e96-4a79-8d7f-7df40facb669");
		
		memoryLabel = addKeyValue(UIContext.Constants.coldStandbyVMMemory());
		memoryLabel.ensureDebugId("2e1986d9-16d1-4237-b95f-2f5b9285577f");
		
		dataStoreLabel = addKeyValue(UIContext.Constants.coldStandbySettingDataStoreLable());
		dataStoreLabel.ensureDebugId("e0632e52-e786-4bac-b7ea-d7b8dcd9480e");
		table.getFlexCellFormatter().setVerticalAlignment(table.getRowCount() - 1, 0, HasVerticalAlignment.ALIGN_TOP);
		
		dataStorePanel = new VerticalPanel();
		table.setWidget(table.getRowCount() - 1, 1, dataStorePanel);
		
		networkLabel = addKeyValue(UIContext.Constants.coldStandbyVMNetworkAdapter());
		networkLabel.ensureDebugId("dcfc6d6f-c0b4-4508-a410-2b571a5079ee");
		
		networkPanel = new VerticalPanel();
		networkPanel.ensureDebugId("c4645939-1b79-4d6f-a555-bb26ee88d728");
		networkPanel.setVisible(false);
		networkPanel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		table.setWidget(table.getRowCount(), 0, networkPanel);
		table.getFlexCellFormatter().setColSpan(table.getRowCount()-1, 0, 2);
		initWidget(table);
	}
	
	private void insertBlankRow() {
		int row = table.getRowCount();
		table.setWidget(row, 0, new HTML("&nbsp"));
		table.setWidget(row, 1, new HTML("&nbsp"));
		
		table.getRowFormatter().addStyleName(row, row%2 == 0?"table_row":"table_row_alternative");
	}
	
	protected Label addKeyValue(String name){
		int row = table.getRowCount();
		Label label = new Label();
		label.setText(name);
		label.setStyleName("panel-text-label");
		table.setWidget(row, 0, label);
		
		label = new Label();
		label.setStyleName("panel-text-value");
		table.setWidget(row, 1, label);
		
		table.getRowFormatter().addStyleName(row, row%2 == 0?"table_row":"table_row_alternative");
		return label;
	}
	
	public void update(VMwareVirtualCenter vmcenter){
		typeLabel.setText(UIContext.Constants.virtualizationTypeVMwareVirtualCenter());
		dataCenterLabel.setText(Utils.convert2UILabel(vmcenter.getESXHostName()));
		versionLabel.setText(Utils.convert2UILabel(vmcenter.getVersion()));
		esxHostNameLabel.setText(Utils.convert2UILabel(vmcenter.getEsxName()));
		
		nameLabel.setText(Utils.convert2UILabel(vmcenter.getVirtualMachineDisplayName()));
		processorLabel.setText(String.valueOf(vmcenter.getVirtualMachineProcessorNumber()));
		memoryLabel.setText(vmcenter.getMemorySizeInMB()+" "+UIContext.Constants.MB());
		
		ESXPanel.showDataStore(vmcenter, dataStorePanel);
		
		networkPanel.clear();
		if (vmcenter.getNetworkAdapters() == null || vmcenter.getNetworkAdapters().size()<=0){
			networkLabel.setText(UIContext.Constants.NA());
			networkPanel.setVisible(false);
		}
		else{
			networkPanel.setVisible(true);
			networkLabel.setText("");
			for (int i=0; i<vmcenter.getNetworkAdapters().size();i++){
				NetworkAdapter adapter = vmcenter.getNetworkAdapters().get(i);
				networkPanel.add(new NetworkPanel((VMwareNetworkAdapter)adapter));
			}
		}
	}
}
