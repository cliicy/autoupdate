package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.HyperVNetworkAdapter;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HyperVPanel extends Composite {
	private FlexTable table;
	private Label typeLabel;
	private Label hyperVHostNameLabel;
	private Label nameLabel;
	private Label processorLabel;
	private Label memoryLabel;
	private Label networkLabel;
	private VerticalPanel networkPanel;
	private Label dataStoreLabel;
	private VerticalPanel dataStorePanel;
	
	public HyperVPanel(){
		table = new FlexTable();
		table.ensureDebugId("42c09714-18ca-4cd6-a7cf-ce7e03f102d9");
		table.getElement().getStyle().setPadding(4, Unit.PX);
		table.getElement().getStyle().setPaddingBottom(12, Unit.PX);
		table.setWidth("100%");
		
		typeLabel = addKeyValue(UIContext.Constants.coldStandbyVMType());
		typeLabel.ensureDebugId("8f126c07-7742-4ee5-be3d-54ca44e28ad3");
		
		hyperVHostNameLabel = addKeyValue(UIContext.Constants.coldStandbyVMHyperVHostName());
		hyperVHostNameLabel.ensureDebugId("9b0c7cbd-09f9-4763-9ef3-e12fdbdf5b17");
		
		insertBlankRow();
		
		nameLabel = addKeyValue(UIContext.Constants.coldStandbyVMName());
		nameLabel.ensureDebugId("36da5d8c-78bf-4f05-ba88-dcb0916a1b5e");
		
		processorLabel = addKeyValue(UIContext.Constants.coldStandbyVMProcessor());
		processorLabel.ensureDebugId("2141932c-66a8-4f53-a305-a4eeae513fdd");
		
		memoryLabel = addKeyValue(UIContext.Constants.coldStandbyVMMemory());
		memoryLabel.ensureDebugId("d75ec1bb-61ca-48c9-a86d-7a3cdaf8ee12");
		
		dataStoreLabel = addKeyValue(UIContext.Constants.coldStandbySettingHypervPathLabel());
		dataStoreLabel.ensureDebugId("17bf05db-be42-49f9-947a-c50ea166651e");
		table.getFlexCellFormatter().setVerticalAlignment(table.getRowCount() - 1, 0, HasVerticalAlignment.ALIGN_TOP);
		
		dataStorePanel = new VerticalPanel();
		table.setWidget(table.getRowCount() - 1, 1, dataStorePanel);
		
		networkLabel = addKeyValue(UIContext.Constants.coldStandbyVMNetworkAdapter());
		networkLabel.ensureDebugId("a8fef0fd-b4b0-4047-9610-de858d78974f");
		
		networkPanel = new VerticalPanel();
		networkPanel.ensureDebugId("458b556c-45b8-474d-9abd-32dae9a04b04");
		networkPanel.setWidth("100%");
		networkPanel.setVisible(false);
		networkPanel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		table.setWidget(table.getRowCount(), 0, networkPanel);
		table.getFlexCellFormatter().setColSpan(table.getRowCount()-1, 0, 2);
		table.getCellFormatter().setWidth(0, 0, "50%");
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
	
	public void update(HyperV hyperV){
		typeLabel.setText(Utils.convert2UILabel(UIContext.Constants.virtualizationTypeHyperV()));
		hyperVHostNameLabel.setText(Utils.convert2UILabel(hyperV.getHostName()));
		
		nameLabel.setText(Utils.convert2UILabel(hyperV.getVirtualMachineDisplayName()));
		processorLabel.setText(Integer.toString(hyperV.getVirtualMachineProcessorNumber()));
		memoryLabel.setText(hyperV.getMemorySizeInMB()+ " "+UIContext.Constants.MB());
		
		ESXPanel.showDataStore(hyperV, dataStorePanel);
		networkPanel.clear();
		if (hyperV.getNetworkAdapters() == null || hyperV.getNetworkAdapters().size()<=0){
			networkLabel.setText(UIContext.Constants.NA());
			networkPanel.setVisible(false);
		}
		else{
			networkPanel.setVisible(true);
			networkLabel.setText("");
			for (int i=0; i<hyperV.getNetworkAdapters().size();i++){
				NetworkAdapter adapter = hyperV.getNetworkAdapters().get(i);
				networkPanel.add(new HyperVNetworkPanel((HyperVNetworkAdapter)adapter));
			}
		}
	}
}
