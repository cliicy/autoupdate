package com.ca.arcflash.ui.client.coldstandby;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareNetworkAdapter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ESXPanel extends Composite {
	private FlexTable table;
	private Label typeLabel;
	private Label nameLabel;
	private Label processorLabel;
	private Label memoryLabel;
	private Label networkLabel;
	private Label esxHostNameLabel;
	private Label dataStoreLabel;
	private VerticalPanel networkPanel;
	private Label versionLabel;
	private VerticalPanel dataStorePanel;
	
	public ESXPanel(){
		table = new FlexTable();
		table.ensureDebugId("2d2b77db-f4f5-4690-aa40-86e5a69b98db");
		table.getElement().getStyle().setPadding(4, Unit.PX);
		table.getElement().getStyle().setPaddingBottom(12, Unit.PX);
		table.setWidth("100%");
		
		typeLabel = addKeyValue(UIContext.Constants.coldStandbyVMType());
		typeLabel.ensureDebugId("76fd9740-f232-42c1-8da3-95aef9a5bfdf");
		
		esxHostNameLabel = addKeyValue(UIContext.Constants.coldStandbyVMESXHostName());
		esxHostNameLabel.ensureDebugId("8bd793d8-3035-427d-9b5d-3e8dd0bc2bd3");
		
		versionLabel = addKeyValue(UIContext.Constants.coldStandbyVMVersion());
		versionLabel.ensureDebugId("20773b2d-a281-475a-9b12-4de9ba10b5e1");
		
		insertBlankRow();
		
		nameLabel = addKeyValue(UIContext.Constants.coldStandbyVMName());
		nameLabel.ensureDebugId("99300c57-311f-4279-8431-07448df014ac");
		
		processorLabel = addKeyValue(UIContext.Constants.coldStandbyVMProcessor());
		processorLabel.ensureDebugId("eca61619-d292-4dfc-ac31-9b000db09d36");
		
		memoryLabel = addKeyValue(UIContext.Constants.coldStandbyVMMemory());
		memoryLabel.ensureDebugId("13f5bddd-e50b-4d4c-acce-50b9a45bb36d");
		
		dataStoreLabel = addKeyValue(UIContext.Constants.coldStandbySettingDataStoreLable());
		dataStoreLabel.ensureDebugId("17bf05db-be42-49f9-947a-c50ea166651e");
		table.getFlexCellFormatter().setVerticalAlignment(table.getRowCount() - 1, 0, HasVerticalAlignment.ALIGN_TOP);
		
		dataStorePanel = new VerticalPanel();
		table.setWidget(table.getRowCount() - 1, 1, dataStorePanel);
		
		networkLabel = addKeyValue(UIContext.Constants.coldStandbyVMNetworkAdapter());
		networkLabel.ensureDebugId("40f0d796-2f23-4961-83c4-dc3a24557779");
		
		networkPanel = new VerticalPanel();
		networkPanel.ensureDebugId("519dfb98-c90f-4350-958f-868c3948b528");
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
	
	public void update(VMwareESX esx){
		typeLabel.setText(UIContext.Constants.virtualizationTypeVMwareESX());
		versionLabel.setText(Utils.convert2UILabel(esx.getVersion()));
		esxHostNameLabel.setText(Utils.convert2UILabel(esx.getHostName()));
		
		nameLabel.setText(Utils.convert2UILabel(esx.getVirtualMachineDisplayName()));
		processorLabel.setText(String.valueOf(esx.getVirtualMachineProcessorNumber()));
		memoryLabel.setText(Utils.convert2UILabel(esx.getMemorySizeInMB()+" "+UIContext.Constants.MB()));
		
		showDataStore(esx, dataStorePanel);
		
		networkPanel.clear();
		if (esx.getNetworkAdapters() == null || esx.getNetworkAdapters().size()<=0){
			networkLabel.setText(UIContext.Constants.NA());
			networkPanel.setVisible(false);
		}
		else{
			networkPanel.setVisible(true);
			networkLabel.setText("");
			for (int i=0; i<esx.getNetworkAdapters().size();i++){
				NetworkAdapter adapter = esx.getNetworkAdapters().get(i);
				networkPanel.add(new NetworkPanel((VMwareNetworkAdapter)adapter));
			}
		}
	}

	public static void showDataStore(Virtualization esx, VerticalPanel dataPanel) {
		List<DiskDestination> list = esx.getDiskDestinations();
		dataPanel.clear();
		if(list == null || list.size() == 0) {
			Label noLabel = new Label();
			noLabel.setText(UIContext.Constants.NA());
		}
		else {
			Set<String> addedStr = new HashSet<String>();
			for(DiskDestination dest : list) {
				if(dest.getStorage() != null) {
					String name = dest.getStorage().getName();
					if(!addedStr.contains(name.toLowerCase())) {
						addedStr.add(name.toLowerCase());
						Label label = new Label();
						label.setStyleName("panel-text-value");
						label.setText(name);
						dataPanel.add(label);
					}
				}
			}
		}
	}
}
