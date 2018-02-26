package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import java.util.ArrayList;
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
import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.NetworkAdapterModel;
import com.ca.arcflash.ui.client.model.VCMDataStoreModel;
import com.ca.arcflash.ui.client.model.VolumeModel;
import com.ca.arcflash.ui.client.vsphere.vmrecover.ResourcePoolPanel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class VirtualMachinePanel extends LayoutContainer{
	private static final int WIDTH_TEXTFIELD = 200;
	private static final int DEFAULT_CPU_COUNT = 1;
	private static final String MAC_ADDRESS = "MacAddress";
	private static final String ADAPTER = "Adapter";
	
	private int 					DEFAULT_MEMORY = 1024;
	private long 					GB=1024*1024*1024;
	
	TextField<String> 				textFieldName;
	BaseSimpleComboBox<Integer> 	textFieldCPU;
	NumberField 					textFieldMemory;
	private FailoverJobScript 		originalFailoverScript;
	private ReplicationJobScript 	originalReplicationJobScript;
	private Label 					maxMermoryLabel;
	private Label					vmNameLabel;
	private Label					resourcePoolLable;
	ResourcePoolPanel				resourcePoolPanel;

	Grid<NetworkAdapterModel> 		adapterGrid;
	Grid<VCMDataStoreModel> 		datastoreGrid;

	String[] 				adapterConnection;
	String[] 				adapterType;
	VMStorage[] 			vmStorages;
	ESXServerInfo 			vmSupportInfo;
	List<FileModel> 		hyperVVolumes;
	
	Radio radioSameDatastore;
	Radio radioDiffDatastore;
	Radio radioSameNetwork;
	Radio radionDiffNetwork;
	
	HyperVPathSelectionPanel    sameHyperVPathSelection;
	String						defaultHypervPath;
	BaseComboBox<ModelData> 	esxSameDatastore;
	LayoutContainer        	 	sameDatastoreLayout;
	
	LayoutContainer         	vmDatastorePanel;
	Label						vmDatastoreLabel;
	BaseComboBox<ModelData> 	esxVMDatatore;
	HyperVPathSelectionPanel    hyperVVMPathSelection;
	
	LayoutContainer			hDatatoreGridPanel;
	Widget                  wDatastoreGrid;
	Widget                  wDatastoreButtons;
	
	SimpleComboBox<String>  sameNetworkType;
	SimpleComboBox<String>  sameNetworkConnection;
	LayoutContainer 		hSameNetworkPanel;
	LayoutContainer     	hNetworkGridPanel; 
	Widget                  wNetworkGrid;
	Widget                  wNetworkButtons;
	
	DisclosurePanel 		datastoreDisclosurePanel; 
	
	Button                  dataStoreAddButton;
	Button                  dataStoreDelButton;
	Button					networkAddButton;
	Button					networkDelButton;
	
	boolean 				isConfiguredSameDatstore;
	boolean 				isConfiguredSameNetwork;
	boolean					isForEdge = true;
	private LayoutContainer datastoreChangedWarningContainer;
	private NumberField standbySnapshotsField;
	
	public VirtualMachinePanel(boolean isForEdge) {

		this.isForEdge = isForEdge;
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		container.ensureDebugId("d4d2f89d-740e-469e-a308-dd7269878a21");
		container.setLayout(tl);
		container.setScrollMode(Scroll.AUTO);
		
		//title and description
		Label titleLabel = new Label(UIContext.Constants.coldStandbySettingVMTitle());
		titleLabel.ensureDebugId("c4de2979-ad93-41b5-96e1-e40864ee57c8");
		titleLabel.setStyleName("coldStandbySettingTitle");
		container.add(titleLabel);

		Label descriptionLabel = new Label(UIContext.Constants.coldStandbySettingVMDescription());
		descriptionLabel.ensureDebugId("d35352a5-8882-4ef5-af51-1af6c8a2cb54");
		descriptionLabel.setStyleName("coldStandbySettingDescription");
		container.add(descriptionLabel);
		
		container.add(getVMBasicSettingsContainer());
		
		container.add(getVMDatastoreContainer());
		
		container.add(getVMNetworkContainer());
		
		this.add(container);
		
	}

	@SuppressWarnings("deprecation")
	private Widget getVMBasicSettingsContainer(){
		
		// VM Settings
		DisclosurePanel serverSettingPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingVMPanelBasic(), false);
		serverSettingPanel.ensureDebugId("27e6b7b7-6989-4d3d-b40a-acc1ef6082ee");
		serverSettingPanel.setWidth("100%");
		serverSettingPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		serverSettingPanel.setOpen(true);

		LayoutContainer vmContainer = new LayoutContainer();
		TableLayout vmTableLayout = new TableLayout();
		vmTableLayout.setColumns(2);
		vmTableLayout.setCellPadding(2);
		//vmTableLayout.setWidth("100%");
		vmContainer.ensureDebugId("d0385b01-578b-4681-a7c9-8d61391d1c7a");
		vmContainer.setLayout(vmTableLayout);
		
		vmNameLabel = new Label();
		vmNameLabel.setStyleName("setting-text-label");
		vmNameLabel.setText(UIContext.Constants.coldStandbySettingVMNamePrefix());
		vmContainer.add(vmNameLabel);

		textFieldName = new TextField<String>();
		textFieldName.ensureDebugId("2ee726e7-b08a-4144-b5cb-0e6642b70c82");
		textFieldName.setValue(FlashUIConstants.DEFAULT_VM_PREFIX);
		if(isForEdge){
			textFieldName.setMaxLength(32);
		}
		textFieldName.setWidth(WIDTH_TEXTFIELD);
		textFieldName.setAllowBlank(false);
		Utils.addToolTip(textFieldName, UIContext.Constants.coldStandbySettingVMNotice());
		vmContainer.add(textFieldName);
		
		resourcePoolLable = new Label();
		resourcePoolLable.setStyleName("setting-text-label");
		resourcePoolLable.setText(UIContext.Constants.coldStandbySettingVMResourcePool());
		vmContainer.add(resourcePoolLable);
		
		resourcePoolPanel = new ResourcePoolPanel();
//		resourcePoolPanel.setPoolWidth(WIDTH_TEXTFIELD);
		Utils.addToolTip(resourcePoolPanel, UIContext.Constants.coldStandbySettingVMResourcePoolTip());
		vmContainer.add(resourcePoolPanel);

		Label titleLabel = new Label();
		titleLabel.ensureDebugId("b711fbec-5875-48d7-ad25-e4995e84136f");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.provistionPointName());
		vmContainer.add(titleLabel);

		standbySnapshotsField = new NumberField();
		standbySnapshotsField.setPropertyEditorType(Integer.class);
		standbySnapshotsField.ensureDebugId("78cd8d69-e3da-45e7-a8f5-3c9af3581e7e");
		standbySnapshotsField.setAllowBlank(true);//always validate failed when not rendered
		standbySnapshotsField.setAllowDecimals(false);
		standbySnapshotsField.setAllowNegative(false);
		standbySnapshotsField.setWidth(80);
		Utils.addToolTip(standbySnapshotsField, UIContext.Constants.coldStandbySettingRecoveryPointCountTip());
		
		vmContainer.add(standbySnapshotsField);
		
		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMCPU());
		vmContainer.add(titleLabel);

		textFieldCPU = new BaseSimpleComboBox<Integer>();
		textFieldCPU.ensureDebugId("9485ab56-ce79-4bf7-9dd7-2a0808e97da4");
		textFieldCPU.setEditable(false);
		textFieldCPU.setAllowBlank(false);
		textFieldCPU.setSimpleValue(DEFAULT_CPU_COUNT);
		textFieldCPU.setWidth(80);
		textFieldCPU.setEditable(false);
		textFieldCPU.add(1);
		textFieldCPU.setSimpleValue(1);
		Utils.addToolTip(textFieldCPU, UIContext.Constants.coldStandbySettingVMCPUCountTip());
		vmContainer.add(textFieldCPU);

		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMMemory());
		vmContainer.add(titleLabel);

		LayoutContainer memoryContainer = new LayoutContainer();
		TableLayout memoryLayout = new TableLayout(3);
		memoryContainer.setLayout(memoryLayout);
		textFieldMemory = new NumberField();
		textFieldMemory.ensureDebugId("8a15c7b2-81a1-49ad-ba07-65d2eecc9fb8");
		textFieldMemory.setAllowBlank(false);
		textFieldMemory.setAllowDecimals(false);
		textFieldMemory.setAllowNegative(false);
		textFieldMemory.setRegex("[1-9][0-9]*");
		textFieldMemory.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		textFieldMemory.setValue(DEFAULT_MEMORY);
		textFieldMemory.setMinValue(8);
		textFieldMemory.setWidth(80);
		Utils.addToolTip(textFieldMemory, UIContext.Constants.coldStandbySettingVMMemoryTip());
		
		memoryContainer.add(textFieldMemory);

		titleLabel = new Label();
		titleLabel.setStyleName("panel-text-value");
		titleLabel.setText("MB");
		memoryContainer.add(titleLabel);

		maxMermoryLabel = new Label();
		maxMermoryLabel.setStyleName("setting-text-label");
		maxMermoryLabel.getElement().getStyle().setPaddingLeft(8, Unit.PX);
		maxMermoryLabel.getElement().getStyle().setFontSize(8, Unit.PT);
		maxMermoryLabel.getElement().getStyle().setColor("DarkGray");
		memoryContainer.add(maxMermoryLabel);
		
		vmContainer.add(memoryContainer);
		
		LayoutContainer basicSettingsContainer = new LayoutContainer();
		TableLayout basicSettingsLayout = new TableLayout(1);
		basicSettingsLayout.setWidth("100%");
		basicSettingsContainer.setLayout(basicSettingsLayout);
		basicSettingsContainer.add(vmContainer);
		TableData td = new TableData();
		td.setWidth("100%");
		basicSettingsContainer.add(new Html("<HR>"), td);
		
		serverSettingPanel.add(basicSettingsContainer);
		if(!this.isForEdge) {
			vmNameLabel.setText(UIContext.Constants.coldStandbySettingVMName());
			maxMermoryLabel.setVisible(false);
		}
		return serverSettingPanel;
	}
	
	@SuppressWarnings("deprecation")
	private Widget getVMDatastoreContainer(){
		// VM Settings
		datastoreDisclosurePanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingVMPanelDatastore(), false);
		datastoreDisclosurePanel.ensureDebugId("e9fbda43-7362-4396-9e1d-4743ae5e5091");
		datastoreDisclosurePanel.setWidth("100%");
		datastoreDisclosurePanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		datastoreDisclosurePanel.setOpen(true);

		LayoutContainer datastoreContainer = new LayoutContainer();
		TableLayout datastoreTableLayout = new TableLayout();
		datastoreTableLayout.setWidth("100%");
		datastoreTableLayout.setCellPadding(2);
		datastoreContainer.ensureDebugId("4b6ff629-7b30-4b6e-bd76-4887fc759d1a");
		datastoreContainer.setLayout(datastoreTableLayout);
		
		datastoreContainer.add(setupDatastoreTable());
		datastoreContainer.add(new Html("<HR>"));
		
		datastoreDisclosurePanel.add(datastoreContainer);
		
		sameHyperVPathSelection = new HyperVPathSelectionPanel(150, isForEdge, "30c9158c-c7d6-443c-9494-5b64d8cb769c");
		esxSameDatastore = new BaseComboBox<ModelData>();
		esxSameDatastore.setTemplate(getESXDatastoreTemplate());
		hyperVVMPathSelection = new HyperVPathSelectionPanel(150, isForEdge, "7ccf937f-86e6-414d-b48f-9ebe6b46bc04");
		hyperVVMPathSelection.setHyperVPathToolTip(UIContext.Constants.coldStandbySettingHyperVVMPathTip());
		
		return datastoreDisclosurePanel;
	}
	
	@SuppressWarnings("deprecation")
	private Widget getVMNetworkContainer(){
		// VM Settings
		DisclosurePanel networkPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingVMPanelNetwork(), false);
		networkPanel.ensureDebugId("15cde2ad-6d86-4be8-a3ad-b5e9e54ff814");
		networkPanel.setWidth("100%");
		networkPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		networkPanel.setOpen(true);

		LayoutContainer networkContainer = new LayoutContainer();
		TableLayout networkTableLayout = new TableLayout();
		networkTableLayout.setWidth("100%");
		networkTableLayout.setCellPadding(2);
		networkContainer.ensureDebugId("254cf8ba-4374-45f2-84eb-02d2cb693471");
		networkContainer.setLayout(networkTableLayout);
		
		networkContainer.add(setupNetworkTable());
		networkPanel.add(networkContainer);
		return networkPanel;
	}
	
	private String getESXDatastoreTemplate() {
		return getCommonBoxTemplate("DisplayName");
	}
	
	private native String getCommonBoxTemplate(String displayName) /*-{  
    return  [  
    '<tpl for=".">',  
    '<div class="x-combo-list-item" qtip="{'+displayName+'}">{'+displayName+'}</div>',  
    '</tpl>'  
    ].join("");  
 }-*/;
	  
	@Override
	public void setEnabled(boolean enabled) {
		//super.setEnabled(enabled);
		
		textFieldName.setEnabled(enabled);
		textFieldCPU.setEnabled(enabled);
		textFieldMemory.setEnabled(enabled);
		
		radioSameDatastore.setEnabled(enabled);
		radioDiffDatastore.setEnabled(enabled);
		radioSameNetwork.setEnabled(enabled);
		radionDiffNetwork.setEnabled(enabled);
		
		esxSameDatastore.setEnabled(enabled);
		
		sameNetworkType.setEnabled(enabled);
		sameNetworkConnection.setEnabled(enabled);
		
		esxVMDatatore.setEnabled(enabled);
		resourcePoolPanel.setEnabled(enabled);
		
		dataStoreAddButton.setEnabled(enabled);
		dataStoreDelButton.setEnabled(enabled);
		
		networkAddButton.setEnabled(enabled);
		networkDelButton.setEnabled(enabled);
		standbySnapshotsField.setEnabled(enabled);
	}
	
	private boolean isStringEmpty(String target) {
		if(target==null) {
			return true;
		}
		else if(target.length()==0) {
			return true;
		}
		else {
			return false;
		}
	}
	private String getMaxFreeVolPath() {
		
		if(isStringEmpty(defaultHypervPath)) {
			
			String volPath = "c:\\" + FlashUIConstants.DEFAULT_PATH_NAME;
			if((hyperVVolumes!=null)&&(hyperVVolumes.size()>0)) {
				FileModel fileModel = hyperVVolumes.get(0);
				if(fileModel instanceof VolumeModel) {
					VolumeModel volModel = (VolumeModel)fileModel;
					for (FileModel fileItem : hyperVVolumes) {
						if(fileItem instanceof VolumeModel) {
							VolumeModel tempVol = (VolumeModel)fileItem;
							if(volModel.getFreeSize()<tempVol.getFreeSize()) {
								volModel=tempVol;
							}
						}
					}
					
					String volName = volModel.getName().toLowerCase();
					if(volName.endsWith("\\")) {
						volPath = volName +FlashUIConstants.DEFAULT_PATH_NAME;
					}
					else {
						volPath = volName + "\\" + FlashUIConstants.DEFAULT_PATH_NAME;
					}
				}
			}
			defaultHypervPath = volPath;
		}

		return defaultHypervPath;
	}
	private void setDefaultHyperVPath(HyperVPathSelectionPanel selectionPanel, int diskID){
		
		if(originalReplicationJobScript==null){
			selectionPanel.setHyperVPath(getMaxFreeVolPath());
			return;
		}
		
		if(WizardContext.getWizardContext().getVirtulizationType()!=originalFailoverScript.getVirtualType()){
			selectionPanel.setHyperVPath(getMaxFreeVolPath());
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
					
					selectionPanel.setHyperVPath(diskDestination.getStorage().getName());
					return;
					
				}
			}
		}
		
		//set the default HyperV path for the new disk
		selectionPanel.setHyperVPath(defaultPathString);
	}
	
	private void setDefaultVMHyperVPath(){
		
		if(originalReplicationJobScript==null){
			hyperVVMPathSelection.setHyperVPath(getMaxFreeVolPath());
			return;
		}
		
		if(WizardContext.getWizardContext().getVirtulizationType()!=originalReplicationJobScript.getVirtualType()){
			hyperVVMPathSelection.setHyperVPath(getMaxFreeVolPath());
			return;
		}

		String defaultPathString="";
		ReplicationDestination destination = originalReplicationJobScript.getReplicationDestination().get(0);
		if((destination!=null)&&(destination instanceof ARCFlashStorage)){
			ARCFlashStorage arcFlashStorage = (ARCFlashStorage)destination;
			defaultPathString = arcFlashStorage.getVMLocationPath();
		}
		//set the default HyperV path for the new disk
		hyperVVMPathSelection.setHyperVPath(defaultPathString);
	}
	
	private void setDefaultDatastoreConfigured(ComboBox<ModelData> esxDatastore, int diskID){
		
		if(originalReplicationJobScript==null){
			return;
		}
		
		if(WizardContext.getWizardContext().getVirtulizationType()!=originalFailoverScript.getVirtualType()){
			return;
		}
		
		ListStore<ModelData> listStore=esxDatastore.getStore();
		if(isConfiguredSameDatstore) {
			if(originalReplicationJobScript.getReplicationDestination().size()>= 0) {
				ReplicationDestination replicationDestination=originalReplicationJobScript.getReplicationDestination().get(0);
				String oldStorageName= replicationDestination.getDiskDestinations().get(0).getStorage().getName();
				
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
		else {
			
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
	}
	
	private void setESXDatastore(ComboBox<ModelData> esxDatastore, String debugID,int width){
		esxDatastore.clear();
		esxDatastore.setWidth(width);
		esxDatastore.setAllowBlank(false);
		esxDatastore.setTriggerAction(TriggerAction.ALL);
		//esxDatastore.ensureDebugId("esxDatastoreComboBox-"+rowIndex+"-"+colIndex);
		esxDatastore.ensureDebugId(debugID);
		ListStore<ModelData> listStore=esxDatastore.getStore();
		esxDatastore.setEditable(false);
		esxDatastore.setDisplayField("DisplayName");
		
		if (vmStorages == null) {
			GWT.log("vmStorages is null");
			return;
		}
		
		for (int i = 0; i < vmStorages.length; i++) {
			ModelData baseModel=new BaseModelData();
			baseModel.set("name", vmStorages[i].getName());
			String displayString = "";
			if(isForEdge) {
				long storageTotolGB = vmStorages[i].getFreeSize()/GB;
				displayString =UIContext.Messages.coldStandbyDatastoreSize(vmStorages[i].getName(),storageTotolGB);
			}
			else {
				displayString = vmStorages[i].getName();
			}
			
			baseModel.set("DisplayName",displayString);
			
			listStore.add(baseModel);
		}
		
		if (vmStorages.length > 0) {
			esxDatastore.setValue(listStore.getAt(0));
		}
		
		if(!isForEdge) {
			esxDatastore.setEnabled(isForEdge);
		}
	}
	
	private Widget renderDatastoreGrid(){
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig sourcedisk = Utils.createColumnConfig("diskNumber",
				UIContext.Constants.coldStandbySettingSourceDisk(), 90, null);
		
		ColumnConfig datastoreLable = Utils.createColumnConfig(
				"esxDataStoreComboBox", UIContext.Constants
						.coldStandbySettingDataStore(), 300, null);

		GridCellRenderer<VCMDataStoreModel> sourceDiskRenderer = new GridCellRenderer<VCMDataStoreModel>() {

			@Override
			public Object render(VCMDataStoreModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VCMDataStoreModel> store, Grid<VCMDataStoreModel> grid) {
				DiskModel diskModel=model.getDiskModel();
				String stringDisk = "Disk" + diskModel.getDiskNumber();
				return getImageHtml("disk.gif")+stringDisk;
			}
		};
		sourcedisk.setRenderer(sourceDiskRenderer);
		
		GridCellRenderer<VCMDataStoreModel> datastoRenderer = new GridCellRenderer<VCMDataStoreModel>() {

			@Override
			public Object render(VCMDataStoreModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VCMDataStoreModel> store, Grid<VCMDataStoreModel> grid) {
				if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV) {
					final TextField<String> hyperVPath = model.getHyperVPath();
					String debugID = "hyperVPath-"+rowIndex+"-"+colIndex;
					Utils.addToolTip(hyperVPath, UIContext.Constants.coldStandbySettingVMDiffPathTip());
					hyperVPath.ensureDebugId(debugID);
					
					HyperVPathSelectionPanel selectionPanel = new HyperVPathSelectionPanel(hyperVPath, isForEdge);
					setDefaultHyperVPath(selectionPanel, model.getDiskModel().getDiskNumber());
					
					return selectionPanel;
					
				} else {
					
					ComboBox<ModelData> esxDatastore=model.getEsxDataStoreComboBox();
					Utils.addToolTip(esxDatastore, UIContext.Constants.coldStandbySettingVMDiffDatastoreTip());
					
					if(esxDatastore.getStore().getCount()<=0){
						String debugID = "esxDatastoreComboBox-"+rowIndex+"-"+colIndex;
						esxDatastore.setTemplate(getESXDatastoreTemplate());
						setESXDatastore(esxDatastore, debugID, 220);
						setDefaultDatastoreConfigured(esxDatastore,model.getDiskModel().getDiskNumber());
					}
					esxDatastore.addSelectionChangedListener(new SelectionChangedListener<ModelData>(){

						@Override
						public void selectionChanged(SelectionChangedEvent<ModelData> se) {
							setDataStoreWarningVisible(true);				
						}});
					return esxDatastore;
				}

				// return null;
			}
		};
		datastoreLable.setRenderer(datastoRenderer);

		configs.add(sourcedisk);
		configs.add(datastoreLable);

		ColumnModel datastoreColumnModel = new ColumnModel(configs);
		ListStore<VCMDataStoreModel> datastoreStore = new ListStore<VCMDataStoreModel>();
		// datastoreGrid.ensureDebugId("7f45b322-e910-4d64-bdb6-94a9ada202fd");
		
		datastoreGrid = new Grid<VCMDataStoreModel>(datastoreStore,	datastoreColumnModel);
		datastoreGrid.setTrackMouseOver(false);
		datastoreGrid.setAutoExpandColumn("esxDataStoreComboBox");
		//datastoreGrid.setSize(400, 85);
		//datastoreGrid.setHeight(85);
		datastoreGrid.setAutoWidth(true);
		datastoreGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		datastoreGrid.setBorders(true);
		
		
		ContentPanel cp = new ContentPanel();  
		cp.setLayout(new FitLayout());   
	    cp.setBodyBorder(false);   
	    cp.setHeaderVisible(false);
	    cp.setStyleName("setting-text-label");
	    cp.setScrollMode(Scroll.AUTO);
	    //cp.setWidth("100%");
	    cp.setSize(450, 85);
	    cp.add(datastoreGrid);

		return cp;
	}
	
	private void setESXDatatoreControl(){
		sameDatastoreLayout.removeAll();
		WizardContext context = WizardContext.getWizardContext();
		VirtualizationType virtualizationType = context.getVirtulizationType();
		if( virtualizationType== VirtualizationType.HyperV){
			sameHyperVPathSelection.setHyperVPathToolTip(UIContext.Constants.coldStandbySettingVMSamePathTip());
			setDefaultHyperVPath(sameHyperVPathSelection, -1);
			sameDatastoreLayout.add(sameHyperVPathSelection);
			
		}
		else{
			//esxSameDatastore = new BaseComboBox<ModelData>();
			ListStore<ModelData> esxSameStore = new ListStore<ModelData>();
			esxSameDatastore.setStore(esxSameStore);
			esxSameDatastore.setFieldLabel("esxSameDatastoreLabel");
			esxSameDatastore.setDisplayField("DisplayName");
			esxSameDatastore.setAllowBlank(false);
			esxSameDatastore.setEditable(false);
			Utils.addToolTip(esxSameDatastore, UIContext.Constants.coldStandbySettingVMSameDatastoreTip());
			sameDatastoreLayout.add(esxSameDatastore);
		}
		
		if(virtualizationType == VirtualizationType.HyperV){
			resourcePoolLable.setVisible(false);
			resourcePoolPanel.setVisible(false);
		}
		else{
			resourcePoolLable.setVisible(true);
			resourcePoolPanel.setVisible(true);
			context.setESXResourcePool();
		}
		
		sameDatastoreLayout.layout();
		sameDatastoreLayout.repaint();
		
	}
	
	private void setVMDefaultDatastoreORPath(){
		VirtualizationType virtualizationType = WizardContext.getWizardContext().getVirtulizationType();
		if( virtualizationType== VirtualizationType.HyperV){
			setDefaultVMHyperVPath();
		}
		else{
			setVMDefaultDatatore();
		}
	}
	private void setVMDefaultDatatore() {
		VirtualizationType virtualizationType = WizardContext.getWizardContext().getVirtulizationType();
		
		ListStore<ModelData> vmStore = esxVMDatatore.getStore();
		vmStore.removeAll();
		setESXDatastore(esxVMDatatore, "{152080C4-86AC-4b75-91E9-EFC7DC2A3544}", 220);
			
		if(originalReplicationJobScript!= null) {
				
			if(virtualizationType!=originalReplicationJobScript.getVirtualType()){
				return;
			}
			
			String configurdVMStoreName ="";
			if(virtualizationType == VirtualizationType.VMwareVirtualCenter){
				VMwareVirtualCenterStorage storage = (VMwareVirtualCenterStorage)originalReplicationJobScript.getReplicationDestination().get(0);
				configurdVMStoreName = storage.getvmDatastore();
			}
			else if(virtualizationType == VirtualizationType.VMwareESX){
				VMwareESXStorage storage = (VMwareESXStorage)originalReplicationJobScript.getReplicationDestination().get(0);
				configurdVMStoreName = storage.getvmDatastore();
			}
			
			if(isStringEmpty(configurdVMStoreName))
				return;
			
			vmStore = esxVMDatatore.getStore();
			for(int m=0;m<vmStore.getCount();m++){
				ModelData baseModel=vmStore.getAt(m);
				String newStorageName=(String)baseModel.get("name");
				if(configurdVMStoreName.compareToIgnoreCase(newStorageName)==0){
					esxVMDatatore.setValue(baseModel);
					break;
				}
			}
			
			esxVMDatatore.addSelectionChangedListener(new SelectionChangedListener<ModelData>(){

				@Override
				public void selectionChanged(SelectionChangedEvent<ModelData> se) {
					setDataStoreWarningVisible(true);				
				}});
			}
		
	}
	
	private LayoutContainer setupDatastoreTable() {
		LayoutContainer datastoreContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		datastoreContainer.setLayout(tl);
		datastoreContainer.setScrollMode(Scroll.AUTO);
		
		LayoutContainer sameDatastoreContainer = new LayoutContainer();
		TableLayout sameDatastoretableLayout = new TableLayout();
		sameDatastoretableLayout.setColumns(1);
		//sameDatastoretableLayout.setCellPadding(2);
		sameDatastoreContainer.setLayout(sameDatastoretableLayout);
		
		RadioGroup rgDatastoreType = new RadioGroup();
		radioSameDatastore = new Radio(); //new RadioButton("DatastoreType"); 
		radioSameDatastore.ensureDebugId("1a1fefde-2947-4e93-bbd5-d88825c1a0a8");
//		radioSameDatastore.setStyleName("setting-text-label"); 
		radioSameDatastore.setBoxLabel(UIContext.Constants.coldStandbySettingVMSameDatastore()); 
		radioSameDatastore.setValue(true);
		sameDatastoreContainer.add(radioSameDatastore);
		rgDatastoreType.add(radioSameDatastore);
		
		sameDatastoreLayout = new LayoutContainer();
		sameDatastoreLayout.setStyleAttribute("padding-left", "28px");
		sameDatastoreLayout.setStyleAttribute("padding-top", "2px");
		sameDatastoreLayout.setStyleAttribute("padding-bottom", "2px");
		sameDatastoreContainer.add(sameDatastoreLayout);
				
		radioDiffDatastore = new Radio(); // new RadioButton("DatastoreType"); 
		radioDiffDatastore.ensureDebugId("7e92735a-bf38-47e9-a24c-4ac5d3be5ae3");
		radioDiffDatastore.setBoxLabel(UIContext.Constants.coldStandbySettingVMDiffDatastore()); 
//		radioDiffDatastore.setStyleName("setting-text-label"); 
		rgDatastoreType.add(radioDiffDatastore);
	
		vmDatastoreLabel = new Label();
		vmDatastoreLabel.setText(UIContext.Constants.coldStandbySettingVMDatastore());
		vmDatastoreLabel.setStyleName("setting-text-label");
		esxVMDatatore = new BaseComboBox<ModelData>();
		ListStore<ModelData> esxVMStore = new ListStore<ModelData>();
		esxVMDatatore.setStore(esxVMStore);
		esxVMDatatore.setFieldLabel("esxVMLabel");
		esxVMDatatore.setDisplayField("DisplayName");
		esxVMDatatore.setEditable(false);
		esxVMDatatore.setAllowBlank(false);
		Utils.addToolTip(esxVMDatatore, UIContext.Constants.coldStandbySettingVMESXVMDatastoreTip());
		esxVMDatatore.setTemplate(getESXDatastoreTemplate());
		
		vmDatastorePanel = new LayoutContainer();
		vmDatastorePanel.setStyleAttribute("padding-left", "20px");
		TableLayout vmDatastoreLayout = new TableLayout();
		vmDatastoreLayout.setCellPadding(2);
		vmDatastoreLayout.setColumns(2);
		vmDatastorePanel.setLayout(vmDatastoreLayout);
		vmDatastorePanel.add(vmDatastoreLabel);
		vmDatastorePanel.add(esxVMDatatore);
		
		
	    hDatatoreGridPanel = new LayoutContainer();
	    hDatatoreGridPanel.setStyleAttribute("padding-left", "20px");
	    TableLayout tableLayout = new TableLayout();
	    tableLayout.setColumns(2);
	    //tableLayout.setWidth("100%");
	    //tableLayout.setCellPadding(2);
		hDatatoreGridPanel.setLayout(tableLayout);
		wDatastoreGrid = renderDatastoreGrid();
		wDatastoreButtons = renderDatastoreButtons();
		//hDatatoreGridPanel.add(wDatastoreButtons);
		//hDatatoreGridPanel.add(wDatastoreButtons);
		
		// when esx/vc datastore change ,showing some warning info (the change just affect new disk).   
		initDatastoreChangedWarningContainer();
		
		datastoreContainer.add(sameDatastoreContainer);
		datastoreContainer.add(radioDiffDatastore);
		datastoreContainer.add(vmDatastorePanel);
		datastoreContainer.add(hDatatoreGridPanel);
		datastoreContainer.add(datastoreChangedWarningContainer);
		//setDatastoreVisible(true);
		sameDatastoreLayout.setVisible(true);
		hDatatoreGridPanel.setVisible(false);
		vmDatastorePanel.setVisible(false);
		datastoreChangedWarningContainer.setVisible(false);

		rgDatastoreType.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (radioSameDatastore.getValue()) {
					setDatastoreVisible(true);
				} else {
					setDatastoreVisible(false);
				}
			}
		});

		return datastoreContainer;
	}
	
	private void initDatastoreChangedWarningContainer() {
		datastoreChangedWarningContainer = new LayoutContainer();
		datastoreChangedWarningContainer.ensureDebugId("a72144e3-eb88-42bb-810f-b1af9a3dd3ca");
		datastoreChangedWarningContainer.setStyleName("setting-text-label");
		TableLayout warningLayout = new TableLayout(2);
		warningLayout.setCellPadding(0);
		warningLayout.setCellSpacing(0);
		datastoreChangedWarningContainer.setLayout(warningLayout);
		Image image = AbstractImagePrototype.create(UIContext.IconBundle.network_adapter_warning()).createImage();
		datastoreChangedWarningContainer.add(image);
		Label warningLabel = new Label(UIContext.Constants.coldStandbySettingDatastoreChangedWarning());
		warningLabel.ensureDebugId("075792ee-746b-4e1a-affb-6c53bec54e2b");
		datastoreChangedWarningContainer.add(warningLabel);
	}

	public void refreshGridUI() {
		if(radioDiffDatastore.getValue()) {
			setDatastoreVisible(false);
		}
		if(radionDiffNetwork.getValue()) {
			setNetworkVisible(false);
		}
		setDataStoreWarningVisible(false);
	}
	private void setDatastoreVisible(boolean isVisible){
		sameDatastoreLayout.setVisible(isVisible);
		hDatatoreGridPanel.setVisible(!isVisible);
		vmDatastorePanel.setVisible(!isVisible);
		if(!isVisible) {
			hDatatoreGridPanel.removeAll();
			hDatatoreGridPanel.add(wDatastoreGrid);
			hDatatoreGridPanel.add(wDatastoreButtons);
			hDatatoreGridPanel.layout();
			hDatatoreGridPanel.repaint();
		}
		
		if(!isVisible){
			if(WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV) {
				vmDatastorePanel.removeAll();
				vmDatastoreLabel.setText(UIContext.Constants.coldStandbySettingVMPath());
//				TableData td = new TableData();
				
				vmDatastorePanel.add(vmDatastoreLabel);
				vmDatastorePanel.add(hyperVVMPathSelection);
			}
			else {
				vmDatastorePanel.removeAll();
				vmDatastoreLabel.setText(UIContext.Constants.coldStandbySettingVMDatastore());
				vmDatastorePanel.add(vmDatastoreLabel);
				vmDatastorePanel.add(esxVMDatatore);
			}
			
			vmDatastorePanel.layout();
			vmDatastorePanel.repaint();
		}
		
	}
	
	private void setDataStoreWarningVisible(boolean isVisible){
		if(this.datastoreChangedWarningContainer!=null){
			if(this.originalReplicationJobScript != null){ // only for edit policy
				datastoreChangedWarningContainer.setVisible(isVisible);
				datastoreChangedWarningContainer.layout(true);			
			}
		}
	}
	
	private void setDatastoreDelButtonStatus() {
		ListStore<VCMDataStoreModel> vcmDatastoreList = datastoreGrid.getStore();
		if(vcmDatastoreList.getCount()>1) {
			dataStoreDelButton.enable();
		}
		else {
			dataStoreDelButton.disable();
		}
	}
	
	private void setNetworkDelButtonStatus() {
		ListStore<NetworkAdapterModel> networkListStore = adapterGrid.getStore();
		if(networkListStore.getCount()>1) {
			networkDelButton.enable();
		}
		else {
			networkDelButton.disable();
		}
	}
	
	private LayoutContainer renderDatastoreButtons(){
		LayoutContainer dataStoreButtonsLayout = new LayoutContainer();
		TableLayout tableLayout = new TableLayout();
		tableLayout.setColumns(1);
		tableLayout.setWidth("100%");
		dataStoreButtonsLayout.setLayout(tableLayout);
		
		dataStoreAddButton = new Button(UIContext.Constants.coldStandbySettingVMButtonAdd());
		dataStoreAddButton.ensureDebugId("fd04e0eb-eff2-4de9-adc2-82eca7151682");
		dataStoreAddButton.setWidth(85);
		dataStoreDelButton = new Button(UIContext.Constants.coldStandbySettingVMButtonDel());
		dataStoreDelButton.ensureDebugId("22becf61-9c96-4af0-8045-ca69711f055f");
		dataStoreDelButton.setWidth(85);
		
		TableData td = new TableData();
		td.setPadding(5);
		dataStoreButtonsLayout.add(dataStoreAddButton, td);
		td = new TableData();
		td.setPadding(5);
		dataStoreButtonsLayout.add(dataStoreDelButton, td);
		
		dataStoreAddButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				ListStore<VCMDataStoreModel> vcmDatastoreList = datastoreGrid.getStore();
				int count = vcmDatastoreList.getCount();
				DiskModel diskModel = new DiskModel();
				diskModel.setDiskNumber(count);
				diskModel.setDiskType(0);
				diskModel.setSignature("");
				
				VCMDataStoreModel vcmDataStoreModel = new VCMDataStoreModel(diskModel);
				vcmDatastoreList.add(vcmDataStoreModel);
				
				setDatastoreDelButtonStatus();
				
			}
		});
		
		dataStoreDelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				ListStore<VCMDataStoreModel> vcmDatastoreList = datastoreGrid.getStore();
				VCMDataStoreModel selectedStoreModel = datastoreGrid.getSelectionModel().getSelectedItem();
				if(selectedStoreModel != null){
					vcmDatastoreList.remove(selectedStoreModel);
					for(int i=0;i<vcmDatastoreList.getCount();i++){
						VCMDataStoreModel model = vcmDatastoreList.getAt(i);
						model.getDiskModel().setDiskNumber(i);
						
						vcmDatastoreList.update(model);
						
					}
					
					setDatastoreDelButtonStatus();
					
				}
				else{
					WizardContext.getWizardContext().showMessageBox("815a94ce-9af6-42bf-a910-96117ed4c6ae",UIContext.Constants.coldStandbySettingVMDiskDel());
				}
				
			}
		});
		
		return dataStoreButtonsLayout;
		
	}
	
	private LayoutContainer renderNetworkButtons(){
		LayoutContainer networkButtonsLayout = new LayoutContainer();
		TableLayout tableLayout = new TableLayout();
		tableLayout.setColumns(1);
		tableLayout.setWidth("100%");
		networkButtonsLayout.setLayout(tableLayout);

		networkAddButton = new Button(UIContext.Constants.coldStandbySettingVMButtonAdd());
		networkAddButton.ensureDebugId("b25a866b-62f4-4b3e-ab1c-a6097ae345d6");
		networkAddButton.setWidth(85);
		//networkAddButton.setStyleAttribute("margin","0px,0px,0px,10px");
		networkDelButton = new Button(UIContext.Constants.coldStandbySettingVMButtonDel());
		networkDelButton.ensureDebugId("272e3f18-bfa7-4838-82eb-b654efe52266");
		networkDelButton.setWidth(85);
		//networkDelButton.setStyleAttribute("margin","5px,0px,0px,10px");
		TableData td = new TableData();
		td.setPadding(5);
		networkButtonsLayout.add(networkAddButton,td);
		td = new TableData();
		td.setPadding(5);
		networkButtonsLayout.add(networkDelButton,td);
		
		networkAddButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				ListStore<NetworkAdapterModel> networkListStore = adapterGrid.getStore();
				NetworkAdapter networkAdapter = new NetworkAdapter();
				int temp = networkListStore.getCount()+1;
				networkAdapter.setAdapterName(ADAPTER+temp);
				networkAdapter.setMACAddress(MAC_ADDRESS+temp);
				
				NetworkAdapterModel networkAdapterModel = new NetworkAdapterModel(networkAdapter);
				networkListStore.add(networkAdapterModel);
				
				setNetworkDelButtonStatus();
				
			}
		});
		
		networkDelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				ListStore<NetworkAdapterModel> networkListStore = adapterGrid.getStore();
				NetworkAdapterModel selectedNetworkAdapterModel = adapterGrid.getSelectionModel().getSelectedItem();
				if(selectedNetworkAdapterModel != null){
					networkListStore.remove(selectedNetworkAdapterModel);
					for(int i=0;i<networkListStore.getCount();i++){
						int temp=i+1;
						NetworkAdapterModel model = networkListStore.getAt(i);
						model.getNetworkAdapter().setAdapterName(ADAPTER+temp);
						model.getNetworkAdapter().setMACAddress(MAC_ADDRESS+temp);
						networkListStore.update(model);
					}
					
					setNetworkDelButtonStatus();
				}
				else{
					WizardContext.getWizardContext().showMessageBox("59c18fcb-2dc5-467a-90ca-c6b20c23d629",UIContext.Constants.coldStandbySettingVMNetworkDel());
				}
			}
		});
		
		return networkButtonsLayout;
		
	}
	
	/*private native String getFlagTemplate() -{
		return  [  
		'<tpl for=".">',  
		'<div class="x-combo-list-item"><img src="images/virtualNIC.gif"> {[values.value]}</div>',  
		'</tpl>'  
		].join("");
	 }-;*/
	

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
			VMwareESX esx = (VMwareESX) originalFailoverScript.getFailoverMechanism().get(0);
			return esx.getNetworkAdapters();

		} else if ( originalFailoverScript.getVirtualType()== VirtualizationType.VMwareVirtualCenter) {
			VMwareVirtualCenter esx = (VMwareVirtualCenter) originalFailoverScript.getFailoverMechanism().get(0);
			return esx.getNetworkAdapters();
		} else {
			HyperV hyperV = (HyperV) originalFailoverScript.getFailoverMechanism().get(0);
			return hyperV.getNetworkAdapters();
		}

	}
	
	private void setDefaultAdapterConnect(SimpleComboBox<String> adapterConnectComboBox,String macAddress){
		List<NetworkAdapter> networkAdaptersList=getConfigutedNetworkAdapter();
		if(networkAdaptersList==null){
			
			if((adapterConnection!=null)&&(adapterConnection.length>0)){
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
			
			if((adapterType!=null)&&(adapterType.length>0)){
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
	
	private void setNetworkAdapterType(SimpleComboBox<String> adapterTypeComboBox, String debugID){
		adapterTypeComboBox.removeAll();
		adapterTypeComboBox.ensureDebugId(debugID);
		//adapterTypeComboBox.setWidth("70%");
		adapterTypeComboBox.setAllowBlank(false);
		adapterTypeComboBox.setEditable(false);
		
		if (adapterType == null) {
			return;
		}
		
		for (int i = 0; i < adapterType.length; i++) {
			adapterTypeComboBox.add(adapterType[i]);
		}
	}
	
	private void setNetworkConnection(SimpleComboBox<String> adapterConnectionBox, String debugID){
		adapterConnectionBox.removeAll();
		adapterConnectionBox.ensureDebugId(debugID);
		//adapterConnectionBox.setWidth("70%");
		adapterConnectionBox.setEditable(false);
		adapterConnectionBox.setAllowBlank(false);
		if (adapterConnection == null) {
			return ;
		}
		for (int i = 0; i < adapterConnection.length; i++) {
			adapterConnectionBox.add(adapterConnection[i]);
		}
	}
	private Widget renderNetworkGrid(){
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig adapterName = Utils.createColumnConfig("adapterName",
				UIContext.Constants.coldStandbySettingVMAdapterName(), 90,	null);
		GridCellRenderer<NetworkAdapterModel> adapterNameCellRenderer = new GridCellRenderer<NetworkAdapterModel>() {

			@Override
			public Object render(NetworkAdapterModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NetworkAdapterModel> store,
					Grid<NetworkAdapterModel> grid) {
				String strAdapterNameString=getImageHtml("adapter.gif")+
					model.getNetworkAdapter().getAdapterName();
				
				return strAdapterNameString;
			}
		};
		adapterName.setRenderer(adapterNameCellRenderer);
		
		ColumnConfig adapterTypeConfig = Utils.createColumnConfig(
				"adapterType", UIContext.Constants.coldStandbySettingVMAdapterType(), 150, null);
		GridCellRenderer<NetworkAdapterModel> adapterTypeCellRenderer = new GridCellRenderer<NetworkAdapterModel>() {

			@Override
			public Object render(NetworkAdapterModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NetworkAdapterModel> store,
					Grid<NetworkAdapterModel> grid) {
				SimpleComboBox<String> adapterTypeComboBox = model.getAdapterTypeComboBox();
				Utils.addToolTip(adapterTypeComboBox, UIContext.Constants.coldStandbySettingVMDiffNetworkTypeTip());
				
				if(adapterTypeComboBox.getStore().getCount()<=0){
					//adapterTypeComboBox.setTemplate(getFlagTemplate());
					String debugID ="adapterTypeComboBox-"+rowIndex+"-"+colIndex;
					adapterTypeComboBox.setTemplate(getCommonBoxTemplate(adapterTypeComboBox.getDisplayField()));
					setNetworkAdapterType(adapterTypeComboBox,debugID);
					setDefaultAdapterType(adapterTypeComboBox,model.getNetworkAdapter().getMACAddress());
				}
				
				if(!isForEdge) {
					adapterTypeComboBox.setEnabled(isForEdge);
				}

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
				SimpleComboBox<String> adapterConnectionBox = model.getNetworkConnectComboBox();
				Utils.addToolTip(adapterConnectionBox, UIContext.Constants.coldStandbySettingVMDiffNetworkConnectionTip());
				
				if(adapterConnectionBox.getStore().getCount()<=0){
					String debugID = "adapterConnectionBox-"+rowIndex+"-"+colIndex;
					adapterConnectionBox.setTemplate(getCommonBoxTemplate(adapterConnectionBox.getDisplayField()));
					setNetworkConnection(adapterConnectionBox, debugID);
					setDefaultAdapterConnect(adapterConnectionBox,model.getNetworkAdapter().getMACAddress());
				}
				
				if(!isForEdge) {
					adapterConnectionBox.setEnabled(isForEdge);
				}
				
				return adapterConnectionBox;
			}
		};
		networkConnectComboBox.setRenderer(adapterConnectionCellRenderer);
		

		configs.add(adapterName);
		configs.add(adapterTypeConfig);
		configs.add(networkConnectComboBox);

		ColumnModel adapterColumnModel = new ColumnModel(configs);
		ListStore<NetworkAdapterModel> adapterStore = new ListStore<NetworkAdapterModel>();
		adapterGrid = new Grid<NetworkAdapterModel>(adapterStore,adapterColumnModel);
		// adapterGrid.ensureDebugId("707a7324-5043-403a-a9cb-038285890391");
		adapterGrid.setTrackMouseOver(false);
		adapterGrid.setAutoExpandColumn("networkLabel");
		adapterGrid.setAutoWidth(true);
		adapterGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		//adapterGrid.setHeight(85);
		adapterGrid.setBorders(true);
		
		ContentPanel cp = new ContentPanel();  
		cp.setScrollMode(Scroll.AUTO);
	    cp.setBodyBorder(false);   
	    cp.setHeaderVisible(false);
	    cp.setStyleName("setting-text-label");
	    //cp.setWidth("100%");
	    cp.setSize(450, 85);
	    cp.setLayout(new FitLayout());   
	    cp.add(adapterGrid);

		return cp;
		
	}
	private Widget setupNetworkTable() {

		LayoutContainer networkContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		networkContainer.setLayout(tl);
		networkContainer.setScrollMode(Scroll.AUTO);
		
		RadioGroup rgNetworkType = new RadioGroup();
		//set the fist row
		radioSameNetwork = new Radio(); //new RadioButton("NetworkType"); 
		radioSameNetwork.ensureDebugId("0cb07440-6800-4303-b335-479b2839c6f1");
//		radioSameNetwork.setStyleName("setting-text-label"); 
		radioSameNetwork.setBoxLabel(UIContext.Constants.coldStandbySettingVMSameNetwork()); 
		radioSameNetwork.setValue(true);
		rgNetworkType.add(radioSameNetwork);
		
		//set  the second row
		hSameNetworkPanel = new LayoutContainer();
		hSameNetworkPanel.setStyleAttribute("padding-left", "20px");
		TableLayout sameNetworkLayout = new TableLayout(4);
		sameNetworkLayout.setCellPadding(2);
		hSameNetworkPanel.setLayout(sameNetworkLayout);
		//hSameNetworkPanel.setWidth("100%");
		
		Label titleLabel = new Label();
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMAdapterType());
		titleLabel.setStyleName("setting-text-label");
		hSameNetworkPanel.add(titleLabel);
		
		sameNetworkType = new SimpleComboBox<String>();
		sameNetworkType.setStore(new ListStore<SimpleComboValue<String>>());
		//sameNetworkType.setTemplate(getFlagTemplate());
		sameNetworkType.setWidth(150);
		sameNetworkType.setEditable(false);
		sameNetworkType.setAllowBlank(false);
		sameNetworkType.setTriggerAction(TriggerAction.ALL);
		sameNetworkType.setTemplate(getCommonBoxTemplate(sameNetworkType.getDisplayField()));
		Utils.addToolTip(sameNetworkType, UIContext.Constants.coldStandbySettingVMSameNetworkTypeTip());
		hSameNetworkPanel.add(sameNetworkType);
		
		titleLabel = new Label();
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMAdapterConnection());
		titleLabel.setStyleName("setting-text-label");
		hSameNetworkPanel.add(titleLabel);
		
		sameNetworkConnection = new SimpleComboBox<String>();
		sameNetworkConnection.setWidth(150);
		sameNetworkConnection.setStore(new ListStore<SimpleComboValue<String>>());
		sameNetworkConnection.setAllowBlank(false);
		sameNetworkConnection.setEditable(false);
		sameNetworkConnection.setTriggerAction(TriggerAction.ALL);
		sameNetworkConnection.setTemplate(getCommonBoxTemplate(sameNetworkConnection.getDisplayField()));
		Utils.addToolTip(sameNetworkConnection, UIContext.Constants.coldStandbySettingVMSameNetworkConnectionTip());
		hSameNetworkPanel.add(sameNetworkConnection);
		
		//set the third row
		radionDiffNetwork = new Radio(); // new RadioButton("NetworkType"); 
		radionDiffNetwork.ensureDebugId("9ad154d3-ea54-47ee-b70b-e08918c3508d");
		radionDiffNetwork.setBoxLabel(UIContext.Constants.coldStandbySettingVMDiffNetwork()); 
//		radionDiffNetwork.setStyleName("setting-text-label"); 
		rgNetworkType.add(radionDiffNetwork);
		
		//set the fourth row
		hNetworkGridPanel = new LayoutContainer();
		TableLayout tableLayout = new TableLayout();
		//tableLayout.setCellPadding(2);
		tableLayout.setColumns(2);
		
		hNetworkGridPanel.setLayout(tableLayout);
		hNetworkGridPanel.setStyleAttribute("padding-left", "20px");
		wNetworkGrid = renderNetworkGrid();
		wNetworkButtons = renderNetworkButtons();
		//hNetworkGridPanel.add(wNetworkGrid);
		//hNetworkGridPanel.add(wNetworkButtons);
		
		Label descLabel = new Label(UIContext.Constants.coldStandbySettingVMNetworkDesc());
		descLabel.ensureDebugId("7eebbe8b-127b-4c76-ac43-15448b74324a");
		descLabel.setStyleName("setting-text-label");
		networkContainer.add(descLabel);
		
		networkContainer.add(radioSameNetwork);
		networkContainer.add(hSameNetworkPanel);
		TableData tData = new TableData();
		tData.setPadding(2);
		networkContainer.add(radionDiffNetwork, tData);
		networkContainer.add(hNetworkGridPanel);
		
		LayoutContainer warningContainer = new LayoutContainer();
		warningContainer.ensureDebugId("c5cb6d79-95bd-430e-b9b7-b75c7c6480e8");
		warningContainer.setStyleName("setting-text-label");
		TableLayout warningLayout = new TableLayout(2);
		warningLayout.setCellPadding(0);
		warningLayout.setCellSpacing(0);
		warningContainer.setLayout(warningLayout);
		Image image = AbstractImagePrototype.create(UIContext.IconBundle.network_adapter_warning()).createImage();
		warningContainer.add(image);
		Label warningLabel = new Label(UIContext.Constants.coldStandbySettingVMNetworkWarning());
		warningLabel.ensureDebugId("36a0adc1-95cc-4921-ab3a-2be21d28f8d8");
		warningContainer.add(warningLabel);
		networkContainer.add(warningContainer);
		
		hSameNetworkPanel.setVisible(true);
		hNetworkGridPanel.setVisible(false);

		rgNetworkType.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (radioSameNetwork.getValue())
					setDatastoreVisible(true);
				else
					setDatastoreVisible(false);
			}
		});

		return networkContainer;
	}

	private void setNetworkVisible(boolean isVisible){
		hSameNetworkPanel.setVisible(isVisible);
		hNetworkGridPanel.setVisible(!isVisible);
		if(!isVisible) {
			hNetworkGridPanel.removeAll();
			hNetworkGridPanel.add(wNetworkGrid);
			hNetworkGridPanel.add(wNetworkButtons);
			hNetworkGridPanel.layout();
			hNetworkGridPanel.repaint();
		}
	}

	protected boolean checkHyperVPathValidte(String path, boolean isCheckVolume){
		if(path!=null){
			if(isShareFolder(path)){
				return false;
			}
			if((isCheckVolume)&&(!isValidateVolumes(path))){
				return false;
			}
			return true;
		}
		return true;		
	}
	protected boolean isShareFolder(String path) {
		if(path==null){
			return false;
		}
		if((path.length()>2)&&(path.charAt(0)=='\\')&&(path.charAt(1)=='\\')){
			String strMsg=UIContext.Constants.coldStandbySettingHypervPathInvalid();
			WizardContext.getWizardContext().showMessageBox("{6007D9A6-F167-47ca-93EB-1D833DF7C632}",strMsg);
			return true;
		}
		
		return false;
		
	}
	protected boolean isValidateVolumes(String path) {
		boolean bRet = false;
		if(!path.endsWith("\\"))
			path = path +"\\";
		
		if(hyperVVolumes!=null){
			for (FileModel fileModel : hyperVVolumes) {
				if(path.toLowerCase().startsWith(fileModel.getPath().toLowerCase())){
					bRet = true;
					break;
				}
			}
		}
		if(!bRet){
			String volPath = path;
			if(path.length()>3){
				volPath = path.substring(0, 3);
			}
			String msg = UIContext.Messages.coldStandbyInvalidHyperVPath(volPath);
			WizardContext.getWizardContext().showMessageBox("{0F0E1B55-5300-4252-9A26-81AF1A0F3B6C}", msg);
		}
		return bRet;
	}
	protected boolean validate() {
		boolean result=textFieldName.validate() && textFieldCPU.validate()
			&& textFieldMemory.validate();
		
		if(!result){
			return false;
		}
		
		
		ListStore<VCMDataStoreModel> datastoreStore = datastoreGrid.getStore();
		ListStore<NetworkAdapterModel> adapterStore = adapterGrid.getStore();
		
		int memorySize = textFieldMemory.getValue().intValue();
		int mod = 4;
		WizardContext context = WizardContext.getWizardContext();
		VirtualizationType virtualType = context.getVirtulizationType();
		if(virtualType == VirtualizationType.HyperV){
			mod = 2;
		}
		if(memorySize % mod !=0){
			String msg = "";
			if(mod == 4)
				msg = UIContext.Constants.coldStandbyMemorySizeLimitHyperV();
			else
				msg = UIContext.Constants.coldStandbyMemorySizeLimitESX();
				
			context.showMessageBox("{A0FCE44D-4FD3-47de-90D1-9F32175A2AF4}", msg);
			return false;
		}
		
		
		if(virtualType == VirtualizationType.HyperV){
			if(radioSameDatastore.getValue()) {
				if(!sameHyperVPathSelection.validate()) {
					return false;
				}
				else if(!checkHyperVPathValidte(sameHyperVPathSelection.getHyperVPath(), true)){
					return false;
				}
			}
			else {
				if(!hyperVVMPathSelection.validate()){
					return false;
				}
				else if(!checkHyperVPathValidte(hyperVVMPathSelection.getHyperVPath(), true)){
					return false;
				}
				
				for(int i=0; i < datastoreStore.getCount(); i++){
					VCMDataStoreModel store = datastoreStore.getAt(i);
					if(!store.getHyperVPath().validate()){
						return false;
					}
					else if(!checkHyperVPathValidte(store.getHyperVPath().getValue(), true)){
						return false;
					}
				}
			}
			
			
		}
		else{
			if(radioSameDatastore.getValue()) {
				if(!esxSameDatastore.validate()) {
					return false;
				}
			}
			else {
				for(int i=0; i < datastoreStore.getCount(); i++){
					VCMDataStoreModel store = datastoreStore.getAt(i);
					if(!store.getEsxDataStoreComboBox().validate()){
						return false;
					}
				}
			}

		}
		
		
		if(radioSameNetwork.getValue()) {
			
			if(!(sameNetworkType.validate()&&sameNetworkConnection.validate())) {
				return false;
			}
		}
		else {
			for(int i = 0; i < adapterStore.getCount(); i++){
				NetworkAdapterModel model = adapterStore.getAt(i);
				BaseSimpleComboBox<String> adapterType=model.getAdapterTypeComboBox();
				BaseSimpleComboBox<String> adatperConnect=model.getNetworkConnectComboBox();
				if(!(adapterType.validate()&&adatperConnect.validate())){
					return false;
				}
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
		failoverScript.setConfiguredSameDatastore(radioSameDatastore.getValue());
		failoverScript.setConfiguredSameNetwork(radioSameNetwork.getValue());

		setDiskDestination(vm.getDiskDestinations(),WizardContext.getWizardContext().getVirtulizationType());
		
		setNetworkAdapter(vm.getNetworkAdapters(),WizardContext.getWizardContext().getVirtulizationType());
		
	}

	protected void populateUI(FailoverJobScript failoverScript,ReplicationJobScript replicationJobScript) {
		originalFailoverScript = failoverScript;
		originalReplicationJobScript=replicationJobScript;
		//virtualizationType = failoverScript.getVirtualType();
		Virtualization vm = failoverScript.getFailoverMechanism().get(0);
		
		textFieldName.setValue(vm.getVirtualMachineDisplayName());
		textFieldMemory.setValue(vm.getMemorySizeInMB());
		textFieldCPU.setSimpleValue(vm.getVirtualMachineProcessorNumber());
		DEFAULT_MEMORY = vm.getMemorySizeInMB();
		standbySnapshotsField.setValue(replicationJobScript.getStandbySnapshots());

		VirtualizationType virType = originalReplicationJobScript.getVirtualType();
		if(virType!=VirtualizationType.HyperV){
			ReplicationDestination dest = originalReplicationJobScript.getReplicationDestination().get(0);
			resourcePoolPanel.setPoolName(dest.getResourcePool());
			resourcePoolPanel.setPoolMoref(dest.getResourcePoolRef());
		}
		
		this.isConfiguredSameDatstore = failoverScript.isConfiguredSameDatastore();
		this.isConfiguredSameNetwork = failoverScript.isConfiguredSameNetwork();
		
		radioSameDatastore.setValue(isConfiguredSameDatstore);
		radioDiffDatastore.setValue(!isConfiguredSameDatstore);
		setDatastoreVisible(isConfiguredSameDatstore);
		
		if (isConfiguredSameDatstore && virType == VirtualizationType.HyperV) {
			// Update vm path
			ReplicationDestination destination = originalReplicationJobScript.getReplicationDestination().get(0);
			if((destination!=null)&&(destination instanceof ARCFlashStorage)){
				ARCFlashStorage arcFlashStorage = (ARCFlashStorage)destination;
				sameHyperVPathSelection.setHyperVPath(arcFlashStorage.getVMLocationPath());
			}
		}
		setVMDefaultDatastoreORPath();

		radioSameNetwork.setValue(isConfiguredSameNetwork);
		radionDiffNetwork.setValue(!isConfiguredSameNetwork);
		setNetworkVisible(isConfiguredSameNetwork);
		
		//setDatastoreLabel(failoverScript.getVirtualType());
		
	}

	private void setDiskDestination(List<DiskDestination> destinations,VirtualizationType virtualizationType){
		ListStore<VCMDataStoreModel> datastoreStore = datastoreGrid.getStore();
		if ((virtualizationType == VirtualizationType.VMwareVirtualCenter)||
				(virtualizationType==VirtualizationType.VMwareESX)){
			
			if(radioSameDatastore.getValue()) {
				int defaultCount = 1;
				for(int i=0; i < defaultCount; i++){
					
					VCMDataStoreModel store = datastoreStore.getAt(i);
					ComboBox<ModelData> row = esxSameDatastore;
					VMStorage vmstore = new VMStorage();
					vmstore.setName((String)row.getValue().get("name"));
					
					DiskDestination diskDest = new DiskDestination();
					diskDest.setDisk(store.getDiskModel());
					diskDest.setStorage(vmstore);
					destinations.add(diskDest);
					
				}
			}
			else {
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
			}

			
		}else if (virtualizationType == VirtualizationType.HyperV){
			
			if(radioSameDatastore.getValue()) {
				int defaultCount = 1;
				for(int i=0; i < defaultCount; i++){
					
					VCMDataStoreModel store = datastoreStore.getAt(i);
					String path = sameHyperVPathSelection.getHyperVPath();
					VMStorage vmstore = new VMStorage();
					vmstore.setName(path);				
					DiskDestination diskDest = new DiskDestination();
					diskDest.setDisk(store.getDiskModel());
					diskDest.setStorage(vmstore);
					destinations.add(diskDest);

				}
			}
			else {
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
		ListStore<NetworkAdapterModel> adapterStore = adapterGrid.getStore();
		if ((virtualizationType == VirtualizationType.VMwareVirtualCenter)||
				(virtualizationType==VirtualizationType.VMwareESX)){
			
			if(radioSameNetwork.getValue()) {
				int defaultCount = 1;
				for(int idx = 0; idx < defaultCount; idx++){
					NetworkAdapterModel model = adapterStore.getAt(idx);
					VMwareNetworkAdapter adapter=new VMwareNetworkAdapter();
					copyNetworkAdapter(adapter,model.getNetworkAdapter());
					adapter.setAdapterType(sameNetworkType.getSimpleValue());
					adapter.setNetworkLabel(sameNetworkConnection.getSimpleValue());
					listNetworkAdapters.add(adapter);
				}
			}
			else {
				for(int idx = 0; idx < adapterStore.getCount(); idx++){
					NetworkAdapterModel model = adapterStore.getAt(idx);
					VMwareNetworkAdapter adapter=new VMwareNetworkAdapter();
					copyNetworkAdapter(adapter,model.getNetworkAdapter());
					adapter.setAdapterType(model.getAdapterTypeComboBox().getSimpleValue());
					adapter.setNetworkLabel(model.getNetworkConnectComboBox().getSimpleValue());
					listNetworkAdapters.add(adapter);
				}
			}

		}else if (virtualizationType == VirtualizationType.HyperV){
			if(radioSameNetwork.getValue()) {
				int defaultCount = 1;
				for(int idx = 0; idx < defaultCount; idx++){
					NetworkAdapterModel model = adapterStore.getAt(idx);
					HyperVNetworkAdapter adapter=new HyperVNetworkAdapter();
					copyNetworkAdapter(adapter,model.getNetworkAdapter());
					adapter.setAdapterType(sameNetworkType.getSimpleValue());
					adapter.setNetworkLabel(sameNetworkConnection.getSimpleValue());
					listNetworkAdapters.add(adapter);
				}
			}
			else {
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
	}
	
	protected void populateResourcePool(ReplicationDestination storage) {
		if(storage == null)
			return;
		
		String resourcePoolName = resourcePoolPanel.getPoolName();
		if(!isStringEmpty(resourcePoolName)){
			storage.setResourcePool(resourcePoolName);
		}
		String resourcePoolRef = resourcePoolPanel.getPoolMoref();
		if(!isStringEmpty(resourcePoolRef)){
			storage.setResourcePoolRef(resourcePoolRef);
		}
		
	}
	
	protected void populateReplicationJobScript(ReplicationJobScript replicationScript) {
		replicationScript.getReplicationDestination().get(0).setConfiguredSameNetwork(radioSameNetwork.getValue());
		if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.VMwareVirtualCenter){
			VMwareVirtualCenterStorage storage = (VMwareVirtualCenterStorage)replicationScript.getReplicationDestination().get(0);
			storage.setVirtualMachineDisplayName(textFieldName.getValue());
			storage.setVirtualMachineProcessorNumber(textFieldCPU.getSimpleValue());
			storage.setMemorySizeInMB(textFieldMemory.getValue().intValue());
			
			if(radioSameDatastore.getValue()) {
				storage.setvmDatastore((String)esxSameDatastore.getValue().get("name"));
			}
			else {
				storage.setvmDatastore((String)esxVMDatatore.getValue().get("name"));
			}
			
			populateResourcePool(storage);
			
			setDiskDestination(storage.getDiskDestinations(), 
					WizardContext.getWizardContext().getVirtulizationType());
			
			setNetworkAdapter(storage.getNetworkAdapters(),
					WizardContext.getWizardContext().getVirtulizationType());
			
		}else if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.VMwareESX){
			VMwareESXStorage storage = (VMwareESXStorage)replicationScript.getReplicationDestination().get(0);
			storage.setVirtualMachineDisplayName(textFieldName.getValue());
			storage.setVirtualMachineProcessorNumber(textFieldCPU.getSimpleValue());
			storage.setMemorySizeInMB(textFieldMemory.getValue().intValue());
			
			if(radioSameDatastore.getValue()) {
				storage.setvmDatastore((String)esxSameDatastore.getValue().get("name"));
			}
			else {
				storage.setvmDatastore((String)esxVMDatatore.getValue().get("name"));
			}
			
			populateResourcePool(storage);
			
			setDiskDestination(storage.getDiskDestinations(), 
					WizardContext.getWizardContext().getVirtulizationType());
			
			setNetworkAdapter(storage.getNetworkAdapters(),
					WizardContext.getWizardContext().getVirtulizationType());
			
		}else if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV){
			ARCFlashStorage storage = (ARCFlashStorage)replicationScript.getReplicationDestination().get(0);
			storage.setVirtualMachineDisplayName(textFieldName.getValue());
			storage.setVirtualMachineProcessorNumber(textFieldCPU.getSimpleValue());
			storage.setMemorySizeInMB(textFieldMemory.getValue().intValue());
			
			if(radioSameDatastore.getValue()){
				storage.setVMLocationPath(sameHyperVPathSelection.getHyperVPath());
			}
			else{
				storage.setVMLocationPath(hyperVVMPathSelection.getHyperVPath());
			}
			
			
			setDiskDestination(storage.getDiskDestinations(), 
					WizardContext.getWizardContext().getVirtulizationType());
			
			setNetworkAdapter(storage.getNetworkAdapters(),
					WizardContext.getWizardContext().getVirtulizationType());
		}
	}
	

	protected void activate(boolean isForEdge) {
	
		WizardContext context = WizardContext.getWizardContext();
		
		setDatastoreLabel(context.getVirtulizationType());
		
		setESXDatatoreControl();
			
		//setDefaultVMHyperVPath();
			
		//setVMDatatore();
		setVMDefaultDatastoreORPath();
			
		getVMDisk();
			
		getNetwork();
			
		setSupportInformation();
			
	}
	
	protected void setDatastoreLabel(VirtualizationType virtualizationType) {
		ColumnModel datastoreColumnModel = datastoreGrid.getColumnModel();
		HasText hasText = datastoreDisclosurePanel.getHeaderTextAccessor();
		if (virtualizationType == VirtualizationType.HyperV) {
			datastoreColumnModel.setColumnHeader(1, UIContext.Constants.coldStandbySettingHypervPath());	
			if(hasText!=null)
				hasText.setText(UIContext.Constants.coldStandbySettingVMPanelPath());
			radioSameDatastore.setBoxLabel(UIContext.Constants.coldStandbySettingVMSamePath()); 
			radioDiffDatastore.setBoxLabel(UIContext.Constants.coldStandbySettingVMDiffPath()); 
			
		} else {
			datastoreColumnModel.setColumnHeader(1, UIContext.Constants.coldStandbySettingDataStore());
			if(hasText!=null)
				hasText.setText(UIContext.Constants.coldStandbySettingVMPanelDatastore());
			radioSameDatastore.setBoxLabel(UIContext.Constants.coldStandbySettingVMSameDatastore()); 
			radioDiffDatastore.setBoxLabel(UIContext.Constants.coldStandbySettingVMDiffDatastore()); 
		}
	}
	
	
	private void getVMDisk() {
		int defaultDiskCount = 5;
		if((originalReplicationJobScript != null)&& (!isConfiguredSameDatstore)) {
			
			defaultDiskCount = originalReplicationJobScript.getReplicationDestination().get(0).getDiskDestinations().size();
		}
		
		ListStore<VCMDataStoreModel> vcmDatastoreList = datastoreGrid.getStore();
		vcmDatastoreList.removeAll();
		for(int i=0;i < defaultDiskCount;i++){
			DiskModel diskModel = new DiskModel();
			diskModel.setDiskNumber(i);
			diskModel.setDiskType(0);
			diskModel.setSignature("");
			
			VCMDataStoreModel vcmDataStoreModel = new VCMDataStoreModel(diskModel);
			vcmDatastoreList.add(vcmDataStoreModel);
		}
		
		setDatastoreDelButtonStatus();
		
	}

	
	private void getNetwork() {
		
		int defaultNetworkCount = 1;
		if(originalReplicationJobScript != null) {
			List<NetworkAdapter> configuredNetworkAdapters = getConfigutedNetworkAdapter();
			if(configuredNetworkAdapters != null) {
				defaultNetworkCount = configuredNetworkAdapters.size();
			}
			
		}
		
		ListStore<NetworkAdapterModel> networkList = adapterGrid.getStore();
		networkList.removeAll();
		for(int i=0;i<defaultNetworkCount;i++){
			NetworkAdapter networkAdapter = new NetworkAdapter();
			int temp = i+1;
			networkAdapter.setAdapterName(ADAPTER+temp);
			networkAdapter.setMACAddress(MAC_ADDRESS+temp);
			NetworkAdapterModel networkAdapterModel=new NetworkAdapterModel(networkAdapter);
			networkList.add(networkAdapterModel);
			
		}
		
		setNetworkDelButtonStatus();
	}


	private void setSupportInformation() {
		
		GWT.log("Entry setSupportInformation()");
		
		ESXServerInfo info = vmSupportInfo;
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
			Virtualization virtualization = originalFailoverScript.getFailoverMechanism().get(0);
			if (virtualization != null
					&& virtualization.getVirtualMachineProcessorNumber() <= info.getCpuCount())
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

		if(WizardContext.getWizardContext().getVirtulizationType() != VirtualizationType.HyperV){
			setESXDatastore(esxSameDatastore, "142ac722-5ac3-4fe2-bdce-4a4e6a4a05a9", 220);
			setDefaultDatastoreConfigured(esxSameDatastore, 0);
			esxSameDatastore.addSelectionChangedListener(new SelectionChangedListener<ModelData>(){

				@Override
				public void selectionChanged(SelectionChangedEvent<ModelData> se) {
					setDataStoreWarningVisible(true);				
				}});
		}

		setNetworkConnection(sameNetworkConnection, "8aa51df4-4f82-4890-b255-38f72015ae34");
		setDefaultAdapterConnect(sameNetworkConnection, MAC_ADDRESS+"1");
		
		setNetworkAdapterType(sameNetworkType, "d51c4f26-6ac3-454c-b57f-95ea91cbd90d");
		setDefaultAdapterType(sameNetworkType, MAC_ADDRESS+"1");
		
		//WizardContext.getWizardContext().unmask();

	}

	
	protected boolean isVirtualizationHostChanged() {
		//first time configure the VCM setting
		if((originalFailoverScript==null)||(originalReplicationJobScript==null)){
			return true;
		}
		
		boolean result=false;
		BaseModel baseModel = WizardContext.getWizardContext().getESXHostModel();
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
				String currentHyperVHost=WizardContext.getWizardContext().getMonitorServer();
				
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
				String currentESXName=(String)baseModel.get("esxNode");
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
				String currentESXName=(String)baseModel.get("esxNode");
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
				String currentESXHost = (String)baseModel.get("esxNode");
				if(currentESXHost.compareTo(configuredESXName)!=0){
					result=true;
				}
			}
			
		}
		
		return result;		
	}
}
