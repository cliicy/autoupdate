package com.ca.arcflash.ui.client.coldstandby.setting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.HyperVNetworkAdapter;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareNetworkAdapter;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ClientException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.ca.arcflash.ui.client.model.VCMDataStoreModel;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.ui.client.model.NetworkAdapterModel;
import com.ca.arcflash.ui.client.restore.BrowseWindow;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox.MessageBoxType;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class VirtualMachinePanel extends WizardPage {
	private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	private final CommonServiceAsync commonService = GWT.create(CommonService.class);

	private static final int WIDTH_TEXTFIELD = 200;
	private static final int DEFAULT_CPU_COUNT = 1;
	private static final int DEFAULT_MEMORY = 1024;
	private static final int REFRESH_INTERVAL = 3000;
	private static final int MAX_INPUT_LENGTH = 260;
	private long GB=1024*1024*1024;
	
	boolean isInit=false;
	VirtualizationType lastSelectedVirtualizationType;

	DisclosurePanel esxSettingPanel;
	BaseComboBox<BaseModel> esxNodeBox;
	TextField<String> textFieldName;
	BaseSimpleComboBox<Integer> textFieldCPU;
	NumberField textFieldMemory;
	private FailoverJobScript originalFailoverScript;
	private ReplicationJobScript originalReplicationJobScript;
	private Label maxMermoryLabel;

	private ColumnModel adapterColumnModel;
	private ListStore<NetworkAdapterModel> adapterStore = new ListStore<NetworkAdapterModel>();
	Grid<NetworkAdapterModel> adapterGrid;

	private ColumnModel datastoreColumnModel;
	private ListStore<VCMDataStoreModel> datastoreStore = new ListStore<VCMDataStoreModel>();
	Grid<VCMDataStoreModel> datastoreGrid;

	private String[] adapterConnection;
	private String[] adapterType;
	private VMStorage[] vmStorages;

	private Timer timer;

	@SuppressWarnings("deprecation")
	public VirtualMachinePanel() {

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.ensureDebugId("d4d2f89d-740e-469e-a308-dd7269878a21");
		verticalPanel.setTableWidth("100%");
		verticalPanel.setWidth("100%");
		verticalPanel.setScrollMode(Scroll.AUTO);

		// add the ESX Settings
		esxSettingPanel = new DisclosurePanel((DisclourePanelImageBundles) GWT
				.create(DisclourePanelImageBundles.class), UIContext.Constants
				.coldStandbySettingESXSetting(), false);
		esxSettingPanel.ensureDebugId("99e12c3c-69ba-46c9-8112-e415b94694f6");
		esxSettingPanel.setWidth("100%");
		esxSettingPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		esxSettingPanel.setOpen(true);

		FlexTable esxSettingTable = new FlexTable();
		esxSettingTable.ensureDebugId("889e4434-1e46-465d-8c78-3ea0d1814808");
		esxSettingTable.setCellPadding(4);
		esxSettingTable.setCellSpacing(4);

		Label titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingVirtualizationESXNode());
		esxSettingTable.setWidget(0, 0, titleLabel);
		esxSettingTable.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		ListStore<BaseModel> esxNodeStore = new ListStore<BaseModel>();
		esxNodeBox = new BaseComboBox<BaseModel>();
		esxNodeBox.setEditable(false);
		esxNodeBox.setAllowBlank(false);
		esxNodeBox.setTriggerAction(TriggerAction.ALL);
		esxNodeBox.setStore(esxNodeStore);
		esxNodeBox.ensureDebugId("613de673-01ea-4378-8d43-50deca2bcee6");
		esxNodeBox.setDisplayField("esxNode");
		esxNodeBox.setWidth(WIDTH_TEXTFIELD);
		esxSettingTable.setWidget(0, 1, esxNodeBox);
		esxSettingTable.getCellFormatter().setVerticalAlignment(0, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		esxSettingPanel.add(esxSettingTable);
		verticalPanel.add(esxSettingPanel);

		// VM Settings
		DisclosurePanel serverSettingPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingVMSetting(), false);
		serverSettingPanel
				.ensureDebugId("27e6b7b7-6989-4d3d-b40a-acc1ef6082ee");
		serverSettingPanel.setWidth("100%");
		serverSettingPanel
				.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		serverSettingPanel.setOpen(true);

		FlexTable flexTableContainer = new FlexTable();
		flexTableContainer
				.ensureDebugId("d0385b01-578b-4681-a7c9-8d61391d1c7a");
		flexTableContainer.setCellPadding(4);
		flexTableContainer.setCellSpacing(4);

		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMName());
		flexTableContainer.setWidget(0, 0, titleLabel);
		flexTableContainer.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		textFieldName = new TextField<String>();
		textFieldName.ensureDebugId("2ee726e7-b08a-4144-b5cb-0e6642b70c82");
		if (UIContext.serverVersionInfo != null
				&& UIContext.serverVersionInfo.getLocalHostName() != null)
			textFieldName.setValue("ColdStandby-"
					+ UIContext.serverVersionInfo.getLocalHostName());
		textFieldName.setMaxLength(32);
		textFieldName.setWidth(WIDTH_TEXTFIELD);
		textFieldName.setAllowBlank(false);
		flexTableContainer.setWidget(0, 1, textFieldName);
		flexTableContainer.getCellFormatter().setVerticalAlignment(0, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		// set the data store
		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingDataStoreLable());
		flexTableContainer.setWidget(2, 0, titleLabel);
		flexTableContainer.getCellFormatter().setVerticalAlignment(2, 0,
				HasVerticalAlignment.ALIGN_TOP);

		flexTableContainer.setWidget(2, 1, setupDatastoreTable());

		// set the network
		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMNetworks());
		flexTableContainer.setWidget(3, 0, titleLabel);
		flexTableContainer.getCellFormatter().setVerticalAlignment(3, 0,
				HasVerticalAlignment.ALIGN_TOP);

		flexTableContainer.setWidget(3, 1, setupNetworkTable());
		// flexTableContainer.setWidget(2, 2, setupNetworkButton());
		// flexTableContainer.getCellFormatter().setVerticalAlignment(2, 2,
		// HasVerticalAlignment.ALIGN_TOP);

		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMCPU());
		flexTableContainer.setWidget(4, 0, titleLabel);
		flexTableContainer.getCellFormatter().setVerticalAlignment(4, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		textFieldCPU = new BaseSimpleComboBox<Integer>();
		textFieldCPU.ensureDebugId("9485ab56-ce79-4bf7-9dd7-2a0808e97da4");
		textFieldCPU.setEditable(false);
		textFieldCPU.setAllowBlank(false);
		textFieldCPU.setSimpleValue(DEFAULT_CPU_COUNT);
		textFieldCPU.setWidth(80);
		textFieldCPU.setEditable(false);
		flexTableContainer.setWidget(4, 1, textFieldCPU);
		flexTableContainer.getCellFormatter().setVerticalAlignment(4, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMMemory());
		flexTableContainer.setWidget(5, 0, titleLabel);
		flexTableContainer.getCellFormatter().setVerticalAlignment(5, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel memoryPanel = new HorizontalPanel();

		textFieldMemory = new NumberField();
		textFieldMemory.ensureDebugId("8a15c7b2-81a1-49ad-ba07-65d2eecc9fb8");
		textFieldMemory.setAllowBlank(false);
		textFieldMemory.setAllowDecimals(false);
		textFieldMemory.setAllowNegative(false);
		textFieldMemory.setRegex("[1-9][0-9]*");
		textFieldMemory.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		textFieldMemory.setValue(DEFAULT_MEMORY);
		textFieldMemory.setWidth(80);
		memoryPanel.add(textFieldMemory);

		titleLabel = new Label();
		titleLabel.setStyleName("panel-text-value");
		titleLabel.setText("MB");
		memoryPanel.add(titleLabel);

		flexTableContainer.setWidget(5, 1, memoryPanel);
		flexTableContainer.getCellFormatter().setVerticalAlignment(5, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		maxMermoryLabel = new Label();
		maxMermoryLabel.setStyleName("setting-text-label");
		maxMermoryLabel.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		maxMermoryLabel.getElement().getStyle().setFontSize(8, Unit.PT);
		maxMermoryLabel.getElement().getStyle().setColor("DarkGray");
		flexTableContainer.setWidget(6, 1, maxMermoryLabel);
		flexTableContainer.getCellFormatter().setVerticalAlignment(6, 1,
				HasVerticalAlignment.ALIGN_TOP);

		serverSettingPanel.add(flexTableContainer);
		verticalPanel.add(serverSettingPanel);
		this.add(verticalPanel);
	}

	public void showBrowseDialog(final TextField<String> textFieldPath) {

		String title = UIContext.Constants.coldStandbySettingHypervLocation();
		String path = textFieldPath.getValue();
		final BrowseWindow browseDlg = new BrowseWindow(false, title);
		browseDlg.setMode(0);
		browseDlg.setUser(WizardContext.getWizardContext().getHypervUsername());
		browseDlg.setPassword(WizardContext.getWizardContext().getHypervPassword());
		browseDlg.setInputFolder(path);
		browseDlg.setBrowseClient(1);
		
		browseDlg.setModal(true);
		browseDlg.addWindowListener(new WindowListener() {
			public void windowHide(WindowEvent we) {
				if (browseDlg.getLastClicked() != Dialog.CANCEL) {
					String newDest = browseDlg.getDestination() == null ? ""
							: browseDlg.getDestination();
					textFieldPath.setValue(newDest);
				}
			}
		});

		browseDlg.show();
	}

	private void connectToMonitor(){
		WizardContext.getWizardContext().mask();
		
		if((WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.VMwareESX)||
				(WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.VMwareVirtualCenter))
		{
			getVMDetail();
			return;
		}
		
		final String monitorServer=WizardContext.getWizardContext().getMonitorServer();
		final int port=WizardContext.getWizardContext().getMonitorPort();
		final String protocol=WizardContext.getWizardContext().getMonitorProtocol();
		
		//TestMonitorConnectionCallback
		service.testMonitorConnection(monitorServer,port,
									 ConnectionProtocol.string2Protocol(protocol),
									 WizardContext.getWizardContext().getMonitorUsername(),
									 WizardContext.getWizardContext().getMonitorPassword(),
									 true, new BaseAsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught) {
				
				WizardContext.getWizardContext().unmask();
				String errorMessage = null;
				if (caught instanceof ServiceConnectException)
					errorMessage = UIContext.Messages.testMonitorConnectionError(
							monitorServer, port , protocol);
				else
					errorMessage = ((ClientException) caught).getDisplayMessage();

				MessageBox messageBox = new MessageBox();
				messageBox.getDialog().ensureDebugId("84e3cfdb-947e-47d2-8923-933ba088a668");
				messageBox.setMinWidth(200);
				messageBox.setType(MessageBoxType.ALERT);
				messageBox.setIcon(MessageBox.ERROR);
				messageBox.setTitleHtml(UIContext.Constants.failed());
				messageBox.setMessage(errorMessage);
				messageBox.show();
				
			}

			@Override
			public void onSuccess(String result) {
				
			    timer = new Timer() {
					public void run() {
						refresh();
					}
				};
				timer.schedule(REFRESH_INTERVAL);
				timer.scheduleRepeating(REFRESH_INTERVAL);
				
				getVMDetail();
			}
			
		});
	}

	private void refresh(){
		/*Date date = new Date();
		//String dateStr2 = new Timestamp(date.getTime()).toString();

		GWT.log("Refresh:"+date.getTime());
		commonService.monitorGetJobMonitor(new BaseAsyncCallback<JobMonitorModel>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}
	
			@Override
			public void onSuccess(JobMonitorModel jobMonitorModel) {
				
			}
		});*/
	}
	
	protected void cancelTimer() {
		timer.cancel();
	}

	private void setDefaultHyperVPath(TextField<String> hyperVPath, int diskID){
		
		if(originalReplicationJobScript==null){
			return;
		}
		
		if(WizardContext.getWizardContext().getVirtulizationType()!=originalFailoverScript.getVirtualType()){
			return;
		}

		String defaultPathString="";
		for(int i=0;i<originalReplicationJobScript.getReplicationDestination().size();i++){
			ReplicationDestination replicationDestination=originalReplicationJobScript.getReplicationDestination().get(i);
			
			for(int j=0;j<replicationDestination.getDiskDestinations().size();j++){
				DiskDestination diskDestination=replicationDestination.getDiskDestinations().get(j);
				if(j==0){
					defaultPathString=diskDestination.getStorage().getName();
				}
				if(diskDestination.getDisk().getDiskNumber()==diskID){
					
					hyperVPath.setValue(diskDestination.getStorage().getName());
					return;
					
				}
			}
		}
		
		//set the default HyperV path for the new disk
		hyperVPath.setValue(defaultPathString);
	}
	
	private void setDefaultDatastoreConfigured(ComboBox<ModelData> esxDatastore, int diskID){
		
		if(originalReplicationJobScript==null){
			return;
		}
		
		if(WizardContext.getWizardContext().getVirtulizationType()!=originalFailoverScript.getVirtualType()){
			return;
		}
		
		ListStore<ModelData> listStore=esxDatastore.getStore();
		for(int i=0;i<originalReplicationJobScript.getReplicationDestination().size();i++){
			ReplicationDestination replicationDestination=originalReplicationJobScript.getReplicationDestination().get(i);
			
			for(int j=0;j<replicationDestination.getDiskDestinations().size();j++){
				DiskDestination diskDestination=replicationDestination.getDiskDestinations().get(j);
				if(diskDestination.getDisk().getDiskNumber()==diskID){
					
					String oldStorageName= diskDestination.getStorage().getName();
					
					for(int m=0;m<listStore.getCount();m++){
						ModelData baseModel=listStore.getAt(m);
						String newStorageName=(String)baseModel.get("name");
						if(oldStorageName.compareToIgnoreCase(newStorageName)==0){
							esxDatastore.setValue(baseModel);
							return;
						}
					}
					
				}
			}
		}
	}
	private Widget setupDatastoreTable() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig sourcedisk = Utils.createColumnConfig("diskNumber",
				UIContext.Constants.coldStandbySettingSourceDisk(), 150, null);
		ColumnConfig sourcevolumes = Utils.createColumnConfig("volumes",
				UIContext.Constants.coldStandbySettingSourceVolumes(), 160,	null);
		ColumnConfig datastoreLable = Utils.createColumnConfig(
				"esxDataStoreComboBox", UIContext.Constants
						.coldStandbySettingDataStore(), 210, null);

		GridCellRenderer<VCMDataStoreModel> sourceDiskRenderer = new GridCellRenderer<VCMDataStoreModel>() {

			@Override
			public Object render(VCMDataStoreModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VCMDataStoreModel> store, Grid<VCMDataStoreModel> grid) {
				// TODO Auto-generated method stub
				DiskModel diskModel=model.getDiskModel();
				String stringDisk = "Disk" + diskModel.getDiskNumber() + "("
						+ diskModel.getSize() / GB + " GB)";
				return getImageHtml("disk.gif")+stringDisk;
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

					LayoutContainer layoutContainer = new LayoutContainer();
					//layoutContainer.setWidth("100%");
					HBoxLayout hBoxLayout = new HBoxLayout();
					layoutContainer.setLayout(hBoxLayout);

					final TextField<String> hyperVPath = model.getHyperVPath();
					hyperVPath.ensureDebugId("hyperVPath-"+rowIndex+"-"+colIndex);
					hyperVPath.setAllowBlank(false);
					hyperVPath.setWidth(150);
					hyperVPath.setMaxLength(MAX_INPUT_LENGTH);
					layoutContainer.add(hyperVPath);
					Button button = new Button("...");
					button.setWidth(30);

					button.addSelectionListener(new SelectionListener<ButtonEvent>() {
								@Override
								public void componentSelected(ButtonEvent ce) {
									showBrowseDialog(hyperVPath);
								}
							});
					
					setDefaultHyperVPath(hyperVPath, model.getDiskModel().getDiskNumber());
					
					layoutContainer.add(hyperVPath);
					layoutContainer.add(button);
					return layoutContainer;
				} else {
					
					ComboBox<ModelData> esxDatastore=model.getEsxDataStoreComboBox();
					esxDatastore.setWidth(180);
					esxDatastore.setAllowBlank(false);
					esxDatastore.setTriggerAction(TriggerAction.ALL);
					esxDatastore.ensureDebugId("esxDatastoreComboBox-"+rowIndex+"-"+colIndex);
					ListStore<ModelData> listStore=esxDatastore.getStore();
					esxDatastore.setEditable(false);
					esxDatastore.setDisplayField("DisplayName");
					
					if (vmStorages == null) {
						GWT.log("vmStorages is null");
						return esxDatastore;
					}
					
					for (int i = 0; i < vmStorages.length; i++) {
						ModelData baseModel=new BaseModelData();
						baseModel.set("name", vmStorages[i].getName());
						baseModel.set("DisplayName", vmStorages[i].getName()+
								"("+vmStorages[i].getFreeSize()/GB+" GB Free)");
						
						listStore.add(baseModel);
					}
					
					if (vmStorages.length > 0) {
						esxDatastore.setValue(listStore.getAt(0));
					}
					
					setDefaultDatastoreConfigured(esxDatastore,model.getDiskModel().getDiskNumber());
					
					return esxDatastore;
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
		datastoreGrid = new Grid<VCMDataStoreModel>(datastoreStore,
				datastoreColumnModel);
		datastoreGrid.setTrackMouseOver(false);
		datastoreGrid.setAutoExpandColumn("esxDataStoreComboBox");
		datastoreGrid.setAutoWidth(true);
		datastoreGrid.getSelectionModel()
				.setSelectionMode(SelectionMode.SINGLE);

		ContentPanel panel = new ContentPanel();
		panel.ensureDebugId("1cb496e7-4cc2-48fd-a0f1-23803138720c");
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.setHeight(95);
		panel.setWidth(520);
		panel.add(datastoreGrid);

		return panel;
	}

	private native String getFlagTemplate() /*-{
		return  [  
		'<tpl for=".">',  
		'<div class="x-combo-list-item"><img src="images/virtualNIC.gif"> {[values.value]}</div>',  
		'</tpl>'  
		].join("");
	 }-*/;
	

	private String getImageHtml(String picName) {
		return "<img src=\"images/"+picName+"\"> ";
	}
	
	private List<NetworkAdapter> getConfigutedNetworkAdapter(){
		if(originalFailoverScript==null){
			return null;
		}

		if(WizardContext.getWizardContext().getVirtulizationType()!=originalFailoverScript.getVirtualType()){
			return null;
		}
		
		if (originalFailoverScript.getVirtualType() == VirtualizationType.VMwareESX) {
			VMwareESX esx = (VMwareESX) originalFailoverScript
					.getFailoverMechanism().get(0);
			return esx.getNetworkAdapters();

		} else if ( originalFailoverScript.getVirtualType()== VirtualizationType.VMwareVirtualCenter) {
			VMwareVirtualCenter esx = (VMwareVirtualCenter) originalFailoverScript
					.getFailoverMechanism().get(0);
			return esx.getNetworkAdapters();
		} else {
			HyperV hyperV = (HyperV) originalFailoverScript
					.getFailoverMechanism().get(0);
			return hyperV.getNetworkAdapters();
		}

	}
	
	private void setDefaultAdapterConnect(SimpleComboBox<String> adapterConnectComboBox,String macAddress){
		List<NetworkAdapter> networkAdaptersList=getConfigutedNetworkAdapter();
		if(networkAdaptersList==null){
			
			if(adapterConnection.length>0){
				adapterConnectComboBox.setSimpleValue(adapterConnection[0]);
			}
			return;
		}
		for(int i=0;i<networkAdaptersList.size();i++){
			if(networkAdaptersList.get(i).getMACAddress().compareToIgnoreCase(macAddress)==0){
				String configuredAdapterConnect=networkAdaptersList.get(i).getNetworkLabel();
				for(int j=0;j<adapterConnection.length;j++){
					if(configuredAdapterConnect.compareToIgnoreCase(adapterConnection[j])==0){
						adapterConnectComboBox.setSimpleValue(adapterConnection[j]);
						return;
					}
				}
			}
		}
		if(adapterConnection.length>0){
			adapterConnectComboBox.setSimpleValue(adapterConnection[0]);
		}
		
	}
	
	private void setDefaultAdapterType(SimpleComboBox<String> adapterTypeComboBox,String macAddress){
		List<NetworkAdapter> networkAdaptersList=getConfigutedNetworkAdapter();
		if(networkAdaptersList==null){
			
			if(adapterType.length>0){
				adapterTypeComboBox.setSimpleValue(adapterType[0]);
			}
			return;
		}
		for(int i=0;i<networkAdaptersList.size();i++){
			if(networkAdaptersList.get(i).getMACAddress().compareToIgnoreCase(macAddress)==0){
				String configuredAdapterType=networkAdaptersList.get(i).getAdapterType();
				for(int j=0;j<adapterType.length;j++){
					if(configuredAdapterType.compareToIgnoreCase(adapterType[j])==0){
						adapterTypeComboBox.setSimpleValue(adapterType[j]);
						return;
					}
				}
			}
		}
		if(adapterType.length>0){
			adapterTypeComboBox.setSimpleValue(adapterType[0]);
		}
		
	}
	
	private NetworkAdapter getDefaultIP(String macAddress){
		List<NetworkAdapter> networkAdaptersList=getConfigutedNetworkAdapter();
		if(networkAdaptersList==null){
			return null;
		}
		
		for(int i=0;i<networkAdaptersList.size();i++){
			if(networkAdaptersList.get(i).getMACAddress().compareToIgnoreCase(macAddress)==0){
				return networkAdaptersList.get(i);
			}
		}
		
		return null;
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
				String strAdapterNameString=getImageHtml("adapter.gif")+
					model.getNetworkAdapter().getAdapterName();
				
				return strAdapterNameString;
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
				adapterTypeComboBox.ensureDebugId("adapterTypeComboBox-"+rowIndex+"-"+colIndex);
				adapterTypeComboBox.setWidth("100%");
				adapterTypeComboBox.setAllowBlank(false);
				adapterTypeComboBox.setEditable(false);
				adapterTypeComboBox.setTemplate(getFlagTemplate());
				if (adapterType == null) {
					return adapterTypeComboBox;
				}
				
				for (int i = 0; i < adapterType.length; i++) {
					adapterTypeComboBox.add(adapterType[i]);
				}
				
				setDefaultAdapterType(adapterTypeComboBox,model.getNetworkAdapter().getMACAddress());

				return adapterTypeComboBox;
			}
		};
		adapterTypeConfig.setRenderer(adapterTypeCellRenderer);

		ColumnConfig networkConnectComboBox = Utils.createColumnConfig(
				"networkLabel", UIContext.Constants.coldStandbySettingVMAdapterConnection(), 150, null);
		GridCellRenderer<NetworkAdapterModel> adapterConnectionCellRenderer = new GridCellRenderer<NetworkAdapterModel>() {

			@Override
			public Object render(NetworkAdapterModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NetworkAdapterModel> store,
					Grid<NetworkAdapterModel> grid) {
				// TODO Auto-generated method stub
				SimpleComboBox<String> adapterConnectionBox = model.getNetworkConnectComboBox();
				adapterConnectionBox.ensureDebugId("adapterConnectionBox-"+rowIndex+"-"+colIndex);
				adapterConnectionBox.setWidth("100%");
				adapterConnectionBox.setEditable(false);
				adapterConnectionBox.setAllowBlank(false);
				if (adapterConnection == null) {
					return adapterConnectionBox;
				}
				for (int i = 0; i < adapterConnection.length; i++) {
					adapterConnectionBox.add(adapterConnection[i]);
				}
				
				setDefaultAdapterConnect(adapterConnectionBox,model.getNetworkAdapter().getMACAddress());
				
				return adapterConnectionBox;
			}
		};
		networkConnectComboBox.setRenderer(adapterConnectionCellRenderer);
		
		ColumnConfig advanceButtonConfig = Utils.createColumnConfig("advance",UIContext.Constants.coldStandbySettingIP(), 65, null);
		GridCellRenderer<NetworkAdapterModel> advanceButtonCellRenderer=new GridCellRenderer<NetworkAdapterModel>() {
			
			@Override
			public Object render(final NetworkAdapterModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NetworkAdapterModel> store, Grid<NetworkAdapterModel> grid) {
				// TODO Auto-generated method stub
				Button button=new Button();
				button.ensureDebugId("advanceButton-"+rowIndex+"-"+colIndex);
				button.setWidth("100%");
				button.setText(UIContext.Constants.coldStandbySettingIP());
				button.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
						NetworkAdapter configuredNetworkAdapter=getDefaultIP(model.getNetworkAdapter().getMACAddress());
						NetworkAdapter currentNetworkAdapter=model.getNetworkAdapter();
						if(configuredNetworkAdapter==null){
							configuredNetworkAdapter=new NetworkAdapter();
							copyNetworkAdapter(configuredNetworkAdapter,currentNetworkAdapter);
						}
						NetworkAdapterWindow networkAdapterWindow=new NetworkAdapterWindow();
						networkAdapterWindow.setAdapter(configuredNetworkAdapter,currentNetworkAdapter);
						model.setNetworkAdapter(configuredNetworkAdapter);
						networkAdapterWindow.setModal(true);
						networkAdapterWindow.show();
						
					}
				});
				return button;
			}
		};
		advanceButtonConfig.setRenderer(advanceButtonCellRenderer);

		configs.add(adapterName);
		configs.add(adapterTypeConfig);
		configs.add(networkConnectComboBox);
		configs.add(advanceButtonConfig);

		adapterColumnModel = new ColumnModel(configs);

		adapterGrid = new Grid<NetworkAdapterModel>(adapterStore,adapterColumnModel);
		// adapterGrid.ensureDebugId("707a7324-5043-403a-a9cb-038285890391");
		adapterGrid.setTrackMouseOver(false);
		adapterGrid.setAutoExpandColumn("networkLabel");
		adapterGrid.setAutoWidth(true);
		adapterGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		ContentPanel panel = new ContentPanel();
		panel.ensureDebugId("762407f2-9e29-4221-9162-814ed8c836cc");
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.setHeight(95);
		panel.setWidth(520);
		panel.add(adapterGrid);

		return panel;
	}

	private void getNetworkTypeAndConnection() {

		GWT.log("Entry getNetworkTypeAndConnection()");
		adapterType = null;
		adapterConnection = null;

		VirtualizationType vmType = WizardContext.getWizardContext().getVirtulizationType();
		if (vmType == VirtualizationType.VMwareESX
				|| vmType == VirtualizationType.VMwareVirtualCenter) {

			BaseModel esxNodeModel = esxNodeBox.getValue();
			service.getESXNodeNetworkAdapterTypes(WizardContext.getWizardContext().getVMwareHost(), 
					WizardContext.getWizardContext().getVMwareUsername(), 
					WizardContext.getWizardContext().getVMwarePassword(), 
					WizardContext.getWizardContext().getVMwareProtocol(),
					WizardContext.getWizardContext().getVMwarePort(),
					esxNodeModel, new BaseAsyncCallback<String[]>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("ESX Failed to getHypervNetworkAdapterTypes()");
							isInit=false;
							super.onFailure(caught);
							
						}

						@Override
						public void onSuccess(String[] result) {
							GWT.log("ESX Successfully getHypervNetworkAdapterTypes()");
							
							adapterType = result;
							
							service.getESXNodeNetworkConnections(
									WizardContext.getWizardContext().getVMwareHost(),
									WizardContext.getWizardContext().getVMwareUsername(),
									WizardContext.getWizardContext().getVMwarePassword(),
									WizardContext.getWizardContext().getVMwareProtocol(),
									WizardContext.getWizardContext().getVMwarePort(),
									esxNodeBox.getValue(), new BaseAsyncCallback<String[]>() {

										@Override
										public void onFailure(Throwable caught) {
											isInit=false;
											super.onFailure(caught);
											GWT.log("ESX Failed to getESXNodeNetworkConnections()");
											getNetwork();
										}

										@Override
										public void onSuccess(String[] result) {
											adapterConnection = result;
											GWT.log("ESX Successfully getESXNodeNetworkConnections()");
											
											getNetwork();
										}

									});
						}
					});

		} else if (vmType == VirtualizationType.HyperV) {
			service
					.getHypervNetworkAdapterTypes(new BaseAsyncCallback<String[]>() {
						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							isInit=false;
							GWT.log("HyperV Failed to getHypervNetworkAdapterTypes()");
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(String[] result) {
							// TODO Auto-generated method stub
							GWT.log("HyperV Successfully getHypervNetworkAdapterTypes()");
							adapterType = result;
							
							service.getHypervNetworks("","","",
									//WizardContext.getWizardContext().getHypervHost(), 
									//WizardContext.getWizardContext().getHypervUsername(), 
									//WizardContext.getWizardContext().getHypervPassword(),
									new BaseAsyncCallback<String[]>() {

								@Override
								public void onFailure(Throwable caught) {
									isInit=false;
									GWT.log("HyperV failed to getHyperVNetworkConnections()");
									super.onFailure(caught);
									getNetwork();
								}

								@Override
								public void onSuccess(String[] result) {
									GWT.log("HyperV Successfully getHyperVNetworkConnections()");
									adapterConnection = result;
									getNetwork();
								}

							});
						}
					});

		}

	}

	@Override
	public String getDescription() {
		return UIContext.Constants.coldStandbySettingVMDescription();
	}

	@Override
	public String getTitle() {
		return UIContext.Constants.coldStandbySettingVMTitle();
	}

	@Override
	protected boolean validate() {
		boolean result=textFieldName.validate() && textFieldCPU.validate()
			&& textFieldMemory.validate();
		
		if(!result){
			return false;
		}
		
		if(WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV){
			
			for(int i=0; i < datastoreStore.getCount(); i++){
				VCMDataStoreModel store = datastoreStore.getAt(i);
				if(!store.getHyperVPath().validate()){
					return false;
				}
			}
			
		}
		else{
			
			if(!(result&&esxNodeBox.validate())){
				return false;
			}
			
			for(int i=0; i < datastoreStore.getCount(); i++){
				VCMDataStoreModel store = datastoreStore.getAt(i);
				if(!store.getEsxDataStoreComboBox().validate()){
					return false;
				}
			}
		}
		
		for(int i = 0; i < adapterStore.getCount(); i++){
			NetworkAdapterModel model = adapterStore.getAt(i);
			BaseSimpleComboBox<String> adapterType=model.getAdapterTypeComboBox();
			BaseSimpleComboBox<String> adatperConnect=model.getNetworkConnectComboBox();
			if(!(adapterType.validate()&&adatperConnect.validate())){
				return false;
			}
		}
		
		return true;
		
	}

	protected void populateFailoverJobScript(FailoverJobScript failoverScript) {
		Virtualization vm = null;

		if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV) {
			vm = new HyperV();
		} else if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.VMwareESX) {
			vm = new VMwareESX();
		} else if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.VMwareVirtualCenter) {
			vm = new VMwareVirtualCenter();
		}

		vm.setVirtualMachineDisplayName(textFieldName.getValue());
		vm.setMemorySizeInMB(textFieldMemory.getValue().intValue());
		vm.setVirtualMachineProcessorNumber(textFieldCPU.getSimpleValue());
		failoverScript.getFailoverMechanism().add(vm);

		/*for(int i=0; i<adapterStore.getCount();++i){
			NetworkAdapterModel adapterModel=adapterStore.getAt(i);
			NetworkAdapter adapter=adapterModel.getNetworkAdapter();
			adapter.setAdapterType(adapterModel.getAdapterTypeComboBox().getSimpleValue());
			adapter.setNetworkLabel(adapterModel.getNetworkConnectComboBox().getSimpleValue());
			
			vm.getNetworkAdapters().add(adapter);
		}*/
		
		setDiskDestination(vm.getDiskDestinations(),WizardContext.getWizardContext().getVirtulizationType());
		
		setNetworkAdapter(vm.getNetworkAdapters(),WizardContext.getWizardContext().getVirtulizationType());
		
	}

	protected void populateUI(FailoverJobScript failoverScript,ReplicationJobScript replicationJobScript) {
		originalFailoverScript = failoverScript;
		originalReplicationJobScript=replicationJobScript;
		//virtualizationType = failoverScript.getVirtualType();
		Virtualization vm = null;
		if (failoverScript.getVirtualType() == VirtualizationType.HyperV) {
			vm = failoverScript.getFailoverMechanism().get(0);
		} else if (failoverScript.getVirtualType() == VirtualizationType.VMwareESX) {
			vm = failoverScript.getFailoverMechanism().get(0);
		} else if (failoverScript.getVirtualType() == VirtualizationType.VMwareVirtualCenter) {
			vm = failoverScript.getFailoverMechanism().get(0);
		}
		textFieldName.setValue(vm.getVirtualMachineDisplayName());
		textFieldMemory.setValue(vm.getMemorySizeInMB());
		textFieldCPU.setSimpleValue(vm.getVirtualMachineProcessorNumber());

		adapterStore.removeAll();
	}

	private void setDiskDestination(List<DiskDestination> destinations,VirtualizationType virtualizationType){
		if ((virtualizationType == VirtualizationType.VMwareVirtualCenter)||
				(virtualizationType==VirtualizationType.VMwareESX)){
			
			for(int i=0; i < datastoreStore.getCount(); i++){
				
				VCMDataStoreModel store = datastoreStore.getAt(i);
				ComboBox<ModelData> row = store.getEsxDataStoreComboBox(); 
				VMStorage vmstore = new VMStorage();
				vmstore.setName((String)row.getValue().get("name"));
				
				DiskDestination diskDest = new DiskDestination();
				diskDest.setDisk(store.getDiskModel());
				diskDest.setStorage(vmstore);
				destinations.add(diskDest);
				
			}
			
		}else if (virtualizationType == VirtualizationType.HyperV){
			
			for(int i=0; i < datastoreStore.getCount(); i++){
				
				VCMDataStoreModel store = datastoreStore.getAt(i);
				String path = store.getHyperVPath().getValue();
				//convertNetworkPath2LocalPath(store.getHyperVPath().getValue());
				VMStorage vmstore = new VMStorage();
				vmstore.setName(path);				
				DiskDestination diskDest = new DiskDestination();
				diskDest.setDisk(store.getDiskModel());
				diskDest.setStorage(vmstore);
				destinations.add(diskDest);
				//storage.getDiskDestinations().add(diskDest);

			}
			
		}
	}
	
	private void copyNetworkAdapter(NetworkAdapter desAdapter,NetworkAdapter orgNetworkAdapter){
		if((desAdapter==null)||(orgNetworkAdapter==null)){
			return;
		}
		
		//NetworkAdapter adapter=new NetworkAdapter();
		desAdapter.setAdapterName(orgNetworkAdapter.getAdapterName());
		desAdapter.setAdapterType(orgNetworkAdapter.getAdapterType());
		desAdapter.setNetworkLabel(orgNetworkAdapter.getNetworkLabel());
		desAdapter.setMACAddress(orgNetworkAdapter.getMACAddress());
		
		desAdapter.setDynamicIP(orgNetworkAdapter.isDynamicIP());
		for (String ip : orgNetworkAdapter.getIP()) {
			desAdapter.getIP().add(ip);
		}
		desAdapter.setSubnetMask(orgNetworkAdapter.getSubnetMask());
		desAdapter.setGateway(orgNetworkAdapter.getGateway());
		
		desAdapter.setDynamicDNS(orgNetworkAdapter.isDynamicDNS());
		desAdapter.setPreferredDNS(orgNetworkAdapter.getPreferredDNS());
		desAdapter.setAlternateDNS(orgNetworkAdapter.getAlternateDNS());
	}
	
	private void setNetworkAdapter(List<NetworkAdapter> listNetworkAdapters,VirtualizationType virtualizationType){
		if ((virtualizationType == VirtualizationType.VMwareVirtualCenter)||
				(virtualizationType==VirtualizationType.VMwareESX)){
			
			for(int idx = 0; idx < adapterStore.getCount(); idx++){
				NetworkAdapterModel model = adapterStore.getAt(idx);
				VMwareNetworkAdapter adapter=new VMwareNetworkAdapter();
				copyNetworkAdapter(adapter,model.getNetworkAdapter());
				adapter.setAdapterType(model.getAdapterTypeComboBox().getSimpleValue());
				adapter.setNetworkLabel(model.getNetworkConnectComboBox().getSimpleValue());
				listNetworkAdapters.add(adapter);
			}
			
		}else if (virtualizationType == VirtualizationType.HyperV){
			for(int idx = 0; idx < adapterStore.getCount(); idx++){
				NetworkAdapterModel model = adapterStore.getAt(idx);
				HyperVNetworkAdapter adapter=new HyperVNetworkAdapter();
				copyNetworkAdapter(adapter,model.getNetworkAdapter());
				adapter.setAdapterType(model.getAdapterTypeComboBox().getSimpleValue());
				adapter.setNetworkLabel(model.getNetworkConnectComboBox().getSimpleValue());
				listNetworkAdapters.add(adapter);
			}
			
		}
	}
	protected void populateReplicationJobScript(ReplicationJobScript replicationScript) {
		if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.VMwareVirtualCenter){
			VMwareVirtualCenterStorage storage = (VMwareVirtualCenterStorage)replicationScript.getReplicationDestination().get(0);
			storage.setVirtualMachineDisplayName(textFieldName.getValue());
			storage.setVirtualMachineProcessorNumber(textFieldCPU.getSimpleValue());
			storage.setMemorySizeInMB(textFieldMemory.getValue().intValue());
			
			/*for(int idx = 0; idx < adapterStore.getCount(); idx++){
				NetworkAdapterModel model = adapterStore.getAt(idx);
				NetworkAdapter adapter = model.getNetworkAdapter();
				adapter.setAdapterType(model.getAdapterTypeComboBox().getSimpleValue());
				adapter.setNetworkLabel(model.getNetworkConnectComboBox().getSimpleValue());
				storage.getNetworkAdapters().add(adapter);
			}*/
			
			setDiskDestination(storage.getDiskDestinations(), 
					WizardContext.getWizardContext().getVirtulizationType());
			
			setNetworkAdapter(storage.getNetworkAdapters(),
					WizardContext.getWizardContext().getVirtulizationType());
			
		}else if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.VMwareESX){
			VMwareESXStorage storage = (VMwareESXStorage)replicationScript.getReplicationDestination().get(0);
			storage.setVirtualMachineDisplayName(textFieldName.getValue());
			storage.setVirtualMachineProcessorNumber(textFieldCPU.getSimpleValue());
			storage.setMemorySizeInMB(textFieldMemory.getValue().intValue());
			
			/*for(int idx = 0; idx < adapterStore.getCount(); idx++){
				NetworkAdapterModel model = adapterStore.getAt(idx);
				NetworkAdapter adapter = model.getNetworkAdapter();
				adapter.setAdapterType(model.getAdapterTypeComboBox().getSimpleValue());
				adapter.setNetworkLabel(model.getNetworkConnectComboBox().getSimpleValue());
				storage.getNetworkAdapters().add(adapter);
			}*/
			
			setDiskDestination(storage.getDiskDestinations(), 
					WizardContext.getWizardContext().getVirtulizationType());
			
			setNetworkAdapter(storage.getNetworkAdapters(),
					WizardContext.getWizardContext().getVirtulizationType());
			
		}else if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV){
			ARCFlashStorage storage = (ARCFlashStorage)replicationScript.getReplicationDestination().get(0);
			storage.setVirtualMachineDisplayName(textFieldName.getValue());
			storage.setVirtualMachineProcessorNumber(textFieldCPU.getSimpleValue());
			storage.setMemorySizeInMB(textFieldMemory.getValue().intValue());
			
			for(int idx = 0; idx < adapterStore.getCount(); idx++){
				NetworkAdapterModel model = adapterStore.getAt(idx);
				NetworkAdapter adapter = model.getNetworkAdapter();
				adapter.setAdapterType(model.getAdapterTypeComboBox().getSimpleValue());
				adapter.setNetworkLabel(model.getNetworkConnectComboBox().getSimpleValue());
				storage.getNetworkAdapters().add(adapter);
			}
			
			setDiskDestination(storage.getDiskDestinations(), 
					WizardContext.getWizardContext().getVirtulizationType());
		}
	}
	

	@Override
	protected void activate() {
		//connectToMonitor();
		if(isNeedRefresh()){
			
			if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV) {
				esxSettingPanel.setVisible(false);
				datastoreColumnModel.getColumnById("esxDataStoreComboBox").
					setHeaderHtml(UIContext.Constants.coldStandbySettingHypervPath());
			} else {
				esxSettingPanel.setVisible(true);
				datastoreColumnModel.getColumnById("esxDataStoreComboBox").
					setHeaderHtml(UIContext.Constants.coldStandbySettingDataStore());
				
			}

			connectToMonitor();
			//getVMDetail();
		}

	}
	
	private boolean isNeedRefresh() {
		if(!isInit){
			isInit=true;
			lastSelectedVirtualizationType=WizardContext.getWizardContext().getVirtulizationType();
			return true;
		}
		else{
			if(lastSelectedVirtualizationType!=WizardContext.getWizardContext().getVirtulizationType()){
				lastSelectedVirtualizationType=WizardContext.getWizardContext().getVirtulizationType();
				return true;
			}
			else{
				return false;
			}
		}
		
	}
	
	private void setDefaultESXNode(BaseModel[] result){
		if (result.length > 0) {
			esxNodeBox.setValue(result[0]);
		}
		
		if(originalFailoverScript==null){
			return;
		}

		if(WizardContext.getWizardContext().getVirtulizationType()!=originalFailoverScript.getVirtualType()){
			return;
		}

		if ( originalFailoverScript.getVirtualType()== VirtualizationType.VMwareVirtualCenter) {
			VMwareVirtualCenter esx = (VMwareVirtualCenter) originalFailoverScript.getFailoverMechanism().get(0);
			//esx.setEsxName((String)esxNode.get("esxNode"));
			String esxName=esx.getEsxName();
			for(int i=0;i<result.length;i++){
				String configureEsxName=(String)result[i].get("esxNode");
				if(configureEsxName.compareToIgnoreCase(esxName)==0){
					esxNodeBox.setValue(result[i]);
					return;
				}
			}
			
		}
	}

	private void getVMDetail() {

		GWT.log("Entry getVMDetail()");
		
		if(WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.HyperV)
		{
			GWT.log("The VM is hyperV, don't need to get the esx node");
			getNetworkTypeAndConnection();
		}
		else{
			
			service.getESXNodeList(
					WizardContext.getWizardContext().getVMwareHost(),
					WizardContext.getWizardContext().getVMwareUsername(),
					WizardContext.getWizardContext().getVMwarePassword(),
					WizardContext.getWizardContext().getVMwareProtocol(),
					WizardContext.getWizardContext().getVMwarePort(),
					new BaseAsyncCallback<BaseModel[]>() {
						@Override
						public void onFailure(Throwable caught) {
							isInit=false;
							GWT.log("Failed to getESXNodeList()");
							MessageBox messageBox = new MessageBox();
							messageBox.setMinWidth(200);
							// messageBox.setType(MessageBoxType.ALERT);
							messageBox.setIcon(MessageBox.ERROR);
							messageBox.setTitleHtml(UIContext.Constants.failed());
							messageBox.setMessage(UIContext.Constants
											.coldStandbySettingVirtualizationFailedToConnectVM());
	
							WizardContext.getWizardContext().unmask();
							messageBox.show();
						}
	
						@Override
						public void onSuccess(BaseModel[] result) {
							GWT.log("Successfully getESXNodeList()");
							
							if (result == null) {
								isInit=false;
								MessageBox msg = new MessageBox();
								msg.setModal(true);
								msg.setMessage(UIContext.Constants
												.coldStandbySettingVirtualizationNoESXNode());
								WizardContext.getWizardContext().unmask();
								msg.show();
								return;
							}
	
							ListStore<BaseModel> nodes = esxNodeBox.getStore();
							nodes.removeAll();
							for (BaseModel model : result) {
								nodes.add(model);
							}
							
							setDefaultESXNode(result);
							
							if((WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.VMwareESX)||
						    		(WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.VMwareVirtualCenter)){
						    	
						    	esxNodeBox.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {
	
									@Override
									public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
										GWT.log("esxNodeBox selected chaged");
										WizardContext.getWizardContext().mask();
										getNetworkTypeAndConnection();
									}
	
								});
						    }
							
						    getNetworkTypeAndConnection();
	
						}
					});
		}
	}

	private void failedToGetVMDiskMessage(){
		WizardContext.getWizardContext().unmask();
		
		MessageBox messageBox = new MessageBox();
		messageBox.setButtons(MessageBox.YESNO);
		messageBox.setIcon(MessageBox.WARNING);
		messageBox.getDialog().ensureDebugId("49631caa-3ee9-43e4-8f44-4503e2636184");
		messageBox.setMinWidth(200);
		messageBox.setType(MessageBoxType.ALERT);
		messageBox.setTitleHtml(UIContext.Constants.failed());
		messageBox.setMessage(UIContext.Constants.coldStandbySettingVMFailedToGetDisk());
		
		messageBox.addCallback(new Listener<MessageBoxEvent>() {

			@Override
			public void handleEvent(MessageBoxEvent be) {
				
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)){
					WizardContext.getWizardContext().mask();
					getVMDisk();
				}else {
					isInit=false;
				}
			}

		});
		messageBox.show();
	}
	private void getVMDisk() {
		
		GWT.log("Entry getVMDisk");
		service.getProductionServerDiskList(new BaseAsyncCallback<DiskModel[]>(){
			@Override
			public void onFailure(Throwable caught) {
				
				GWT.log("Failed to getVMDisk()");
				failedToGetVMDiskMessage();
			}

			@Override
			public void onSuccess(DiskModel[] result) {
				GWT.log("Successfully getVMDisk() with disk size:"+result.length);
				ListStore<VCMDataStoreModel> dataStoreList = datastoreGrid.getStore();
				dataStoreList.removeAll();
				
				
				if(result.length==0){
					failedToGetVMDiskMessage();
					return;
					
				}
				for(int i=0;i<result.length;i++){
					dataStoreList.add(new VCMDataStoreModel(result[i]));
				}

				getVirtualizationSuppoortInformation();
			}
		});
		

	}

	private void getESXDatastore(){
		GWT.log("Entry getESXDatastore()");
		vmStorages=null;
		BaseModel esxNode = esxNodeBox.getValue();
		String dataCenter= (String)esxNode.get("dataCenter");
		String esxName= (String)esxNode.get("esxNode");
		
		service.getVmStorages(WizardContext.getWizardContext().getVMwareHost(),
				WizardContext.getWizardContext().getVMwareUsername(),
				WizardContext.getWizardContext().getVMwarePassword(),
				WizardContext.getWizardContext().getVMwareProtocol(),
				true,
				WizardContext.getWizardContext().getVMwarePort(),
				esxName,
				dataCenter,
				null,
				new BaseAsyncCallback<VMStorage[]>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to getESXDatastore()");
				WizardContext.getWizardContext().unmask();
			}

			@Override
			public void onSuccess(VMStorage[] result) {
				GWT.log("Successfylly getESXDatastore()");
				for(int i=0;i<result.length;i++){
					GWT.log("getESXDatastore():get the ESX Datastore name:"+result[i].getName());
				}
				
				vmStorages=result;
				
				getVMDisk();
			}
		});
	}
	
	private void failedToGetNeworkMessage(){
		
		WizardContext.getWizardContext().unmask();
		
		MessageBox messageBox = new MessageBox();
		messageBox.setButtons(MessageBox.YESNO);
		messageBox.setIcon(MessageBox.WARNING);
		messageBox.getDialog().ensureDebugId("1139c796-eb15-4c3e-9914-d1ef532b976a");
		messageBox.setMinWidth(200);
		messageBox.setType(MessageBoxType.ALERT);
		messageBox.setTitleHtml(UIContext.Constants.failed());
		messageBox.setMessage(UIContext.Constants.coldStandbySettingVMFailedToGetAdapter());
		messageBox.setButtons(MessageBox.YESNO);
		messageBox.addCallback(new Listener<MessageBoxEvent>() {

			@Override
			public void handleEvent(MessageBoxEvent be) {
				
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)){
					WizardContext.getWizardContext().mask();
					getNetwork();
				}else {
					isInit=false;
				}
			}

		});
		messageBox.show();
	}
	private void getNetwork() {

		GWT.log("Entry getNetwork()");
		
		service.getProdServerNetworkAdapters(new BaseAsyncCallback<NetworkAdapter[]>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to getProdServerNetworkAdapters()");
				failedToGetNeworkMessage();
			}

			@Override
			public void onSuccess(NetworkAdapter[] result) {
				
				GWT.log("Successfully  getProdServerNetworkAdapters() with size:"+result.length);
				ListStore<NetworkAdapterModel> networkList = adapterGrid.getStore();
				networkList.removeAll();
				
				if(result.length==0){
					failedToGetNeworkMessage();
					return;
					
				}
				
				for(int i=0;i<result.length;i++){

					NetworkAdapterModel networkAdapterModel=new NetworkAdapterModel(result[i]);
					networkList.add(networkAdapterModel);
					
					GWT.log("Get the Network:"+result[i].getAdapterName());
				}
				
				if(WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.HyperV){
					getVMDisk();
				}
				else{
					getESXDatastore();
				}
				
			}
		});

		
	}

	private void getVirtualizationSuppoortInformation() {
		
		GWT.log("Entry getVirtualizationSuppoortInformation()");

		if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV) {
			service.getHyperVSupportedInfo(
					"","","",
					/*WizardContext.getWizardContext()
					.getHypervHost(), WizardContext.getWizardContext()
					.getHypervUsername(), WizardContext.getWizardContext()
					.getHypervPassword(),*/
					new BaseAsyncCallback<ESXServerInfo>() {
						@Override
						public void onFailure(Throwable caught) {
							onFailGetSupportInformation(caught);
						}

						@Override
						public void onSuccess(ESXServerInfo result) {
							OnSuccessGetSupportInformation(result);
						}
					});
		} else {

			service.getESXNodeSupportedInfo(WizardContext.getWizardContext()
					.getVMwareHost(), WizardContext.getWizardContext()
					.getVMwareUsername(), WizardContext.getWizardContext()
					.getVMwarePassword(), WizardContext.getWizardContext()
					.getVMwareProtocol(), WizardContext.getWizardContext()
					.getVMwarePort(), esxNodeBox.getValue(),// WizardContext.getWizardContext().getESXHostModel(),
					new BaseAsyncCallback<ESXServerInfo>() {
						@Override
						public void onFailure(Throwable caught) {
							onFailGetSupportInformation(caught);
						}

						@Override
						public void onSuccess(ESXServerInfo result) {
							OnSuccessGetSupportInformation(result);
						}
					});
		}
	}

	private void OnSuccessGetSupportInformation(ESXServerInfo info) {
		
		GWT.log("Entry OnSuccessGetSupportInformation()");
		
		if (info == null) {
			return;
		}

		textFieldCPU.removeAll();
		if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV)
			for (int i = 1; i <= info.getCpuCount(); i *= 2)
				textFieldCPU.add(i);
		else {
			for (int i = 1; i <= info.getCpuCount(); i++)
				textFieldCPU.add(i);
		}
		if (originalFailoverScript != null) {
			Virtualization virtualization = originalFailoverScript
					.getFailoverMechanism().get(0);
			if (virtualization != null
					&& virtualization.getVirtualMachineProcessorNumber() <= info
							.getCpuCount())
				textFieldCPU.setSimpleValue(virtualization.getVirtualMachineProcessorNumber());
			else
				textFieldCPU.setSimpleValue(DEFAULT_CPU_COUNT);
		} else
			textFieldCPU.setSimpleValue(DEFAULT_CPU_COUNT);

		GWT.log(String.valueOf(info.getMemorySize()));
		long maxMemorySize = ((long) info.getMemorySize() / (1024 * 1024));
		maxMermoryLabel.setText(UIContext.Messages.coldStandbyMaxMemorysize(maxMemorySize));
		textFieldMemory.setMaxValue(maxMemorySize);
		
		if(DEFAULT_MEMORY>maxMemorySize){
			textFieldMemory.setValue(maxMemorySize);
		}else{
			textFieldMemory.setValue(DEFAULT_MEMORY);
		}

		WizardContext.getWizardContext().unmask();

	}

	private void onFailGetSupportInformation(Throwable caught) {

		GWT.log("Entry onFailGetSupportInformation()");
		
		WizardContext.getWizardContext().unmask();

		if (caught instanceof BusinessLogicException
				&& ((BusinessLogicException) caught).getErrorCode().equals(
						"4294967303")) {
			MessageBox messageBox = new MessageBox();
			messageBox.setButtons(MessageBox.YESNO);
			messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameVCM));
			messageBox.setMessage("Can't retrieve CPU/Memory information, click YES to try again. Click No to exist wizard.");
			messageBox.setIcon(MessageBox.ERROR);
			messageBox.setModal(true);
			messageBox.addCallback(new Listener<MessageBoxEvent>() {

				@Override
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
						getVirtualizationSuppoortInformation();
					} else{
						isInit=false;
						WizardContext.getWizardContext().exitWizard();
					}
						
				}

			});
			messageBox.show();

		}
	}
	
	protected boolean isVirtualizationHostChanged() {
		//first time configure the VCM setting
		if((originalFailoverScript==null)||(originalReplicationJobScript==null)){
			return true;
		}
		
		boolean result=false;
		VirtualizationType currentVirtualizationType=WizardContext.getWizardContext().getVirtulizationType();
		VirtualizationType configuredviVirtualizationType=originalFailoverScript.getVirtualType();
		
		String currentVMName=textFieldName.getValue().trim();
		Virtualization virtualization=originalFailoverScript.getFailoverMechanism().get(0);
		if(virtualization==null){
			return result; 
		}
		String configuredVMName=virtualization.getVirtualMachineDisplayName();
		
		//change the VM name
		if(currentVMName.compareToIgnoreCase(configuredVMName)!=0){
			return true;
		}

		if(currentVirtualizationType==VirtualizationType.HyperV){
			if((configuredviVirtualizationType==VirtualizationType.VMwareESX)||
				(configuredviVirtualizationType==VirtualizationType.VMwareVirtualCenter)){
				result=true;
			}
			else{
				//get HyperV Host
				String currentHyperVHost=WizardContext.getWizardContext().getHypervHost();
				
				HyperV hyperV = (HyperV)originalFailoverScript.getFailoverMechanism().get(0);
				if(hyperV!=null){
					String configuredHyperVHost=hyperV.getHostName();
					//change HyperV host name
					if(currentHyperVHost.compareToIgnoreCase(configuredHyperVHost)!=0){
						result=true;
					}
				}
				
			}

		}
		
		else if(currentVirtualizationType==VirtualizationType.VMwareESX){
			if(configuredviVirtualizationType==VirtualizationType.HyperV){
				result=true;
			}
			else if(configuredviVirtualizationType==VirtualizationType.VMwareESX){
				String configuredESXName="";
				VMwareESX esx = (VMwareESX)originalFailoverScript.getFailoverMechanism().get(0);
				if(esx!=null){
					configuredESXName=esx.getEsxName();
				}
				//change the ESX Name
				String currentESXName=(String)esxNodeBox.getValue().get("esxNode");
				if(currentESXName.compareTo(configuredESXName)!=0){
					result=true;
				}
			}
			else if(configuredviVirtualizationType==VirtualizationType.VMwareVirtualCenter){
				String configuredESXName = "";
				VMwareVirtualCenter vCenter = (VMwareVirtualCenter)originalFailoverScript.getFailoverMechanism().get(0);
				if(vCenter!=null){
					configuredESXName=vCenter.getEsxName();
				}
				String currentESXName = WizardContext.getWizardContext().getVMwareHost();
				if(currentESXName.compareTo(configuredESXName)!=0){
					result=true;
				}
			}
			
		}
		
		else if(currentVirtualizationType==VirtualizationType.VMwareVirtualCenter){
			if(configuredviVirtualizationType==VirtualizationType.HyperV){
				result=true;
			}
			else if(configuredviVirtualizationType==VirtualizationType.VMwareVirtualCenter){
				String configuredESXName="";
				VMwareVirtualCenter vCenter = (VMwareVirtualCenter)originalFailoverScript.getFailoverMechanism().get(0);
				if(vCenter!=null){
					configuredESXName=vCenter.getEsxName();
				}
				String currentESXName=(String)esxNodeBox.getValue().get("esxNode");
				if(currentESXName.compareTo(configuredESXName)!=0){
					result=true;
				}
			}
			else if(configuredviVirtualizationType==VirtualizationType.VMwareESX){
				String configuredESXName = "";
				VMwareESX esx = (VMwareESX)originalFailoverScript.getFailoverMechanism().get(0);
				if(esx!=null){
					configuredESXName=esx.getHostName();
				}
				String currentESXHost = (String)esxNodeBox.getValue().get("esxNode");
				if(currentESXHost.compareTo(configuredESXName)!=0){
					result=true;
				}
			}
			
		}
		
		return result;		
	}
}
