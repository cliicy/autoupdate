package com.ca.arcflash.ui.client.coldstandby.setting;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.VCMDataStoreModel;
import com.ca.arcflash.ui.client.model.NetworkAdapterModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

class SummaryPanel extends WizardPage {

	VerticalPanel summaryPanel;
	int currentRow=-1;
	FlexTable summaryTable;
	
	private ColumnModel adapterColumnModel;
	Grid<NetworkAdapterModel> adapterGrid;

	private ColumnModel datastoreColumnModel;
	Grid<VCMDataStoreModel> datastoreGrid;
	
	private long GB=1024*1024*1024;
	
	RadioButton startImmediateButton;
	RadioButton startNextButton;
	
	@SuppressWarnings("deprecation")
	public SummaryPanel() {

		this.ensureDebugId("1c238191-11b1-4182-adf9-4158246ec328");

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.ensureDebugId("c27d65aa-a970-41ae-8752-cdc82f9ad8a2");
		verticalPanel.setTableWidth("100%");
		verticalPanel.setWidth("100%");
		verticalPanel.setScrollMode(Scroll.AUTO);
		verticalPanel.setSpacing(10);
	

		summaryPanel=new VerticalPanel();
		summaryPanel.setWidth("100%");
		summaryPanel.setTableWidth("100%");
		summaryPanel.setScrollMode(Scroll.AUTO);
		summaryPanel.setBorders(true);
		//summaryPanel.setHeight(320);
		summaryPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		
		summaryTable = new FlexTable();
		summaryTable.setCellPadding(10);
		summaryTable.setCellSpacing(4);
		
		summaryPanel.add(summaryTable);
		verticalPanel.add(summaryPanel);
		
		startImmediateButton=new RadioButton("start");
		startImmediateButton.ensureDebugId("ffc38d32-c051-4879-b1b5-7935b1937551");
		startImmediateButton.setText(UIContext.Constants.coldStandbySettingStartImmediately());
		startImmediateButton.setValue(true);
		startImmediateButton.setStyleName("setting-text-label");
		
		verticalPanel.add(startImmediateButton);
		
		
		startNextButton=new RadioButton("start");
		startNextButton.ensureDebugId("c91ab69d-1e63-4772-b6c1-685d0175d9cf");
		startNextButton.setText(UIContext.Constants.coldStandbySettingStartNext());
		startNextButton.setStyleName("setting-text-label");
	    verticalPanel.add(startNextButton);
		
		
		this.add(verticalPanel);
	}

	public boolean validate() {
		return true;
	}

	@Override
	public String getDescription() {
		return UIContext.Constants.coldStandbySettingSummaryDescription();
	}

	@Override
	public String getTitle() {
		return UIContext.Constants.coldStandbySettingSummaryTitle();
	}

	@Override
	protected void activate() {

		currentRow=-1;
		summaryTable.removeAllRows();
		
		getVirtualizationSummary();
		getStandinSummary();
		getVirtualMachineSummary();
		setRadioButtonsVisible();
	}

	private void addTableTitle(String title){
		currentRow++;
		Label titleLabel = new Label();
		titleLabel.setStyleName("coldStandbySettingSummary");
		titleLabel.setText(title);
		summaryTable.setWidget(currentRow, 0, titleLabel);
		summaryTable.getFlexCellFormatter().setColSpan(currentRow, 0, 2);
	}
	private void addTableTitle(String title,String styleNameString){
		currentRow++;
		Label titleLabel = new Label();
		titleLabel.setStyleName(styleNameString);
		titleLabel.setText(title);
		summaryTable.setWidget(currentRow, 0, titleLabel);
		summaryTable.getFlexCellFormatter().setColSpan(currentRow, 0, 2);
	}
	private void addTableRow(String property,String value){
		currentRow++;
		Label titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(property); 
		summaryTable.setWidget(currentRow, 0, titleLabel);
		summaryTable.getCellFormatter().setVerticalAlignment(currentRow, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		
		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(value); 
		summaryTable.setWidget(currentRow, 1, titleLabel);
		summaryTable.getCellFormatter().setVerticalAlignment(currentRow, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
	}
	
	private void addTableRowWidget(String property,Widget widget){
		currentRow++;
		Label titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(property); 
		summaryTable.setWidget(currentRow, 0, titleLabel);
		summaryTable.getCellFormatter().setVerticalAlignment(currentRow, 0,
				HasVerticalAlignment.ALIGN_TOP);
		
		summaryTable.setWidget(currentRow, 1, widget);
		summaryTable.getCellFormatter().setVerticalAlignment(currentRow, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
	}
	
	private void getVirtualizationSummary(){
		//get page 1 summary
		addTableTitle(UIContext.Constants.coldStandbySettingVirtualizationServerSetting());
		
	    String virtualTypeString=UIContext.Constants.coldStandbySettingVirtualizationType();
		
		if(WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.HyperV){
			
			addTableRow(virtualTypeString,UIContext.Constants.coldStandbySettingVirtualizationHyperV());
			
		    addTableRow(UIContext.Constants.coldStandbySettingVirtualizationServerNameHyperV(),
		    		WizardContext.getWizardContext().getHypervHost());
			
		    addTableRow(UIContext.Constants.coldStandbySettingVirtualizationUserName(),
		    		WizardContext.getWizardContext().getHypervUsername());
			
		    //addTableRow(UIContext.Constants.coldStandbySettingVirtualizationDataTransferPort(),
		    //		Integer.toString(WizardContext.getWizardContext().getDataTransferPort()));
			
		}
		else{
			
			addTableRow(virtualTypeString, UIContext.Constants.coldStandbySettingVirtualizationTypeVMware());
			
			addTableRow(UIContext.Constants.coldStandbySettingVirtualizationServerName(),
					WizardContext.getWizardContext().getVMwareHost());
			
			addTableRow(UIContext.Constants.coldStandbySettingVirtualizationUserName(),
					WizardContext.getWizardContext().getVMwareUsername());
			
			addTableRow(UIContext.Constants.coldStandbySettingStandinProtocol(),
					WizardContext.getWizardContext().getVMwareProtocol());
			
			addTableRow(UIContext.Constants.coldStandbySettingStandinPort(),
					Integer.toString(WizardContext.getWizardContext().getVMwarePort()));
			
			String esxNodeName=(String)WizardContext.getWizardContext().getESXHostModel().get("esxNode");
			addTableRow(UIContext.Constants.coldStandbySettingVirtualizationESXNode(), 
					esxNodeName);
			
		}
		
	}
	
	private void getStandinSummary() {
		//get page 2 summary
		addTableTitle(UIContext.Constants.coldStandbySettingStandinMonitoring());
		
		addTableRow(UIContext.Constants.coldStandbySettingStandinMonitorServer(),
				WizardContext.getWizardContext().getMonitorServer());
		
		addTableRow(UIContext.Constants.coldStandbySettingStandinUserName(),
				WizardContext.getWizardContext().getMonitorUsername());
		
		addTableRow(UIContext.Constants.coldStandbySettingStandinProtocol(),
				WizardContext.getWizardContext().getMonitorProtocol());
		
		addTableRow(UIContext.Constants.coldStandbySettingStandinPort(),
				Integer.toString( WizardContext.getWizardContext().getMonitorPort()));
		
		addTableTitle(UIContext.Constants.coldStandbySettingStandinRecovery());
		
		String strRecovry="";
		if(WizardContext.getWizardContext().isManualStartVM()){
			strRecovry=UIContext.Constants.coldStandbySettingStandinRecoveryTypeManual();
		}
		else{
			strRecovry=UIContext.Constants.coldStandbySettingStandinRecoveryTypeAutomatic();
		}
		addTableTitle(strRecovry,"setting-text-label");
		
		addTableTitle(UIContext.Constants.coldStandbySettingStandinHeartBeatProperties());
		
		
		String timeOut=Integer.toString(WizardContext.getWizardContext().getHeartBeatTimeout())
			+" "+UIContext.Constants.seconds();
		addTableRow(UIContext.Constants.HeartBeatPropertiesWindowTimeout(),
				timeOut);
	
		String frequent=Integer.toString(WizardContext.getWizardContext().getHeartBeatFrequency())
			+" "+UIContext.Constants.seconds();
		addTableRow(UIContext.Constants.HeartBeatPropertiesWindowFrequency(), frequent);
		
	}
	private void getVirtualMachineSummary(){
		 addTableTitle(UIContext.Constants.coldStandbySettingVMSetting());
		 
		 addTableRow(UIContext.Constants.coldStandbySettingVMName(),
				 WizardContext.getWizardContext().getVMName());
		 
		 addTableRowWidget(UIContext.Constants.coldStandbySettingDataStoreLable(), setupDatastoreTable());
		 
		 addTableRowWidget(UIContext.Constants.coldStandbySettingVMNetworks(), setupNetworkTable());
		 
		 addTableRow(UIContext.Constants.coldStandbySettingVMCPU(),
				 Integer.toString(WizardContext.getWizardContext().getVMCPU()));
		 
		 addTableRow(UIContext.Constants.coldStandbySettingVMMemory(),
				 Integer.toString(WizardContext.getWizardContext().getVMMemory())+" MB");
	}
	
	private Widget setupDatastoreTable() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig sourcedisk = Utils.createColumnConfig("diskNumber",
				UIContext.Constants.coldStandbySettingSourceDisk(), 150, null);
		ColumnConfig sourcevolumes = Utils.createColumnConfig("volumes",
				UIContext.Constants.coldStandbySettingSourceVolumes(), 150,
				null);
		String columnHeader="";
		if(WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.HyperV){
			columnHeader=UIContext.Constants.coldStandbySettingHypervPath();
		}
		else{
			columnHeader=UIContext.Constants.coldStandbySettingDataStore();
		}
		ColumnConfig datastoreLable = Utils.createColumnConfig("esxDataStoreComboBox",columnHeader, 150, null);

		GridCellRenderer<VCMDataStoreModel> sourceDiskRenderer = new GridCellRenderer<VCMDataStoreModel>() {

			@Override
			public Object render(VCMDataStoreModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VCMDataStoreModel> store, Grid<VCMDataStoreModel> grid) {
				// TODO Auto-generated method stub
				DiskModel diskModel=model.getDiskModel();
				String stringDisk = "Disk" + diskModel.getDiskNumber() + "("
						+ diskModel.getSize() / GB + " GB)";
				return stringDisk;
			}
		};
		sourcedisk.setRenderer(sourceDiskRenderer);
		
		GridCellRenderer<VCMDataStoreModel> sourceVolumesRenderer = new GridCellRenderer<VCMDataStoreModel>() {

			@Override
			public Object render(VCMDataStoreModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VCMDataStoreModel> store, Grid<VCMDataStoreModel> grid) {
				// TODO Auto-generated method stub
				return model.getVolumes();
			}
		};
		sourcevolumes.setRenderer(sourceVolumesRenderer);
		
		GridCellRenderer<VCMDataStoreModel> datastoRenderer = new GridCellRenderer<VCMDataStoreModel>() {

			@Override
			public Object render(VCMDataStoreModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VCMDataStoreModel> store, Grid<VCMDataStoreModel> grid) {
				// TODO Auto-generated method stub
				if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV) {

					TextField<String> hyperVPath = model.getHyperVPath();
					return hyperVPath.getValue();
					
				} else {
					
					ComboBox<ModelData> esxDatastore=model.getEsxDataStoreComboBox();
					return (String)esxDatastore.getValue().get("DisplayName");
				}

				// return null;
			}
		};
		datastoreLable.setRenderer(datastoRenderer);

		configs.add(sourcedisk);
		configs.add(sourcevolumes);
		configs.add(datastoreLable);

		datastoreColumnModel = new ColumnModel(configs);

		// datastoreGrid.ensureDebugId("7f45b322-e910-4d64-bdb6-94a9ada202fd");
		datastoreGrid = new Grid<VCMDataStoreModel>(WizardContext.getWizardContext().getVMDisk(),
				datastoreColumnModel);
		datastoreGrid.setTrackMouseOver(false);
		datastoreGrid.setAutoExpandColumn("esxDataStoreComboBox");
		datastoreGrid.setAutoWidth(true);
		datastoreGrid.getSelectionModel()
				.setSelectionMode(SelectionMode.SINGLE);

		ContentPanel panel = new ContentPanel();
		//panel.ensureDebugId("1cb496e7-4cc2-48fd-a0f1-23803138720c");
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.setHeight(80);
		panel.setWidth(450);
		panel.add(datastoreGrid);

		return panel;
	}

	private Widget setupNetworkTable() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig adapterName = Utils.createColumnConfig("adapterName",
				UIContext.Constants.coldStandbySettingVMAdapterName(), 150,
				null);
		GridCellRenderer<NetworkAdapterModel> adapterNameCellRenderer = new GridCellRenderer<NetworkAdapterModel>() {

			@Override
			public Object render(NetworkAdapterModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NetworkAdapterModel> store,
					Grid<NetworkAdapterModel> grid) {
				// TODO Auto-generated method stub
				NetworkAdapter networkAdapter=model.getNetworkAdapter();
				
				return networkAdapter.getAdapterName();
			}
		};
		adapterName.setRenderer(adapterNameCellRenderer);

		ColumnConfig adapterTypeConfig = Utils.createColumnConfig(
				"adapterType", UIContext.Constants
						.coldStandbySettingVMAdapterType(), 150, null);
		GridCellRenderer<NetworkAdapterModel> adapterTypeCellRenderer = new GridCellRenderer<NetworkAdapterModel>() {

			@Override
			public Object render(NetworkAdapterModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NetworkAdapterModel> store,
					Grid<NetworkAdapterModel> grid) {
				// TODO Auto-generated method stub
				SimpleComboBox<String> adapterTypeComboBox = model.getAdapterTypeComboBox();
				return adapterTypeComboBox.getSimpleValue();
			}
		};
		adapterTypeConfig.setRenderer(adapterTypeCellRenderer);

		ColumnConfig networkConnectComboBox = Utils.createColumnConfig(
				"networkLabel", UIContext.Constants
						.coldStandbySettingVMAdapterConnection(), 150, null);
		GridCellRenderer<NetworkAdapterModel> adapterConnectionCellRenderer = new GridCellRenderer<NetworkAdapterModel>() {

			@Override
			public Object render(NetworkAdapterModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NetworkAdapterModel> store,
					Grid<NetworkAdapterModel> grid) {
				// TODO Auto-generated method stub
				SimpleComboBox<String> adapterConnectionBox = model
						.getNetworkConnectComboBox();
				return adapterConnectionBox.getSimpleValue();
			}
		};
		networkConnectComboBox.setRenderer(adapterConnectionCellRenderer);

		configs.add(adapterName);
		configs.add(adapterTypeConfig);
		configs.add(networkConnectComboBox);

		adapterColumnModel = new ColumnModel(configs);

		adapterGrid = new Grid<NetworkAdapterModel>(WizardContext.getWizardContext().getVMNetwork(),
				adapterColumnModel);
		// adapterGrid.ensureDebugId("707a7324-5043-403a-a9cb-038285890391");
		adapterGrid.setTrackMouseOver(false);
		adapterGrid.setAutoExpandColumn("networkLabel");
		adapterGrid.setAutoWidth(true);
		adapterGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		ContentPanel panel = new ContentPanel();
		//panel.ensureDebugId("762407f2-9e29-4221-9162-814ed8c836cc");
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.setHeight(80);
		panel.setWidth(450);
		panel.add(adapterGrid);

		return panel;
	}
	protected void populateReplicationJobScript(ReplicationJobScript replicationScript){
		replicationScript.setAutoReplicate(true);
	}
	
	protected void populateUI(ReplicationJobScript replicationScript) {
		if(replicationScript!=null){
			startImmediateButton.setValue(!replicationScript.getAutoReplicate());
			startNextButton.setValue(replicationScript.getAutoReplicate());
		}
	}
	
	protected void setRadioButtonsVisible() {
		
		if(WizardContext.getWizardContext().isVirtualizationHostChanged()){
			startImmediateButton.setVisible(true);
			startNextButton.setVisible(true);
			startImmediateButton.setValue(true);
			startNextButton.setValue(false);
			summaryPanel.setHeight(320);
		}
		else{
			startImmediateButton.setVisible(false);
			startNextButton.setVisible(false);
			startImmediateButton.setValue(false);
			startNextButton.setValue(true);
			summaryPanel.setHeight(360);
		}
		
	}
	
}
