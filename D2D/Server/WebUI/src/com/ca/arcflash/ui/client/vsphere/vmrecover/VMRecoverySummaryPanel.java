package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.DiskDataStoreModel;
import com.ca.arcflash.ui.client.model.DiskModel;
import com.ca.arcflash.ui.client.model.RecoverVMOptionModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.VMNetworkConfigInfoModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.ca.arcflash.ui.client.restore.RestoreSummaryPanel;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.QuickTip;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class VMRecoverySummaryPanel extends RestoreSummaryPanel {
	private RestoreWizardContainer wizard;
	
	private Label destinationLabel;
	private LabelField destinationLF;
	private LayoutContainer renderVirtualMachineContainer;
	private LayoutContainer setupDatastoreTableContainer;
	private LabelField serverLabel;
	private LabelField vcLF;
	private LabelField protocolLF;
	private LabelField vmVAppLabel;
	private LabelField vmNameLF;
	private LabelField portLF;
	private LabelField usernameLF;
	private LabelField passwordLF;
	private LabelField poolLF;
	
	private FieldSet standaloneVMSettingContainer;
	private LabelField esxServerLabel;
	private LabelField esxListLF;
	private LabelField vmDataStoreLF;
	private ColumnModel datastoreColumnModel;
	private ListStore<DiskDataStoreModel> datastoreStore = new ListStore<DiskDataStoreModel>();
	private Grid<DiskDataStoreModel> datastoreGrid;	
	
	private ListStore<VMNetworkConfigInfoModel> networkStore = new ListStore<VMNetworkConfigInfoModel>();
	private Grid<VMNetworkConfigInfoModel> networkGrid;	
	
	private LabelField resolvingConflictsLF;
	private LabelField resolvingConflictsVMID; //<huvfe01>###
	private LabelField postRecoverLF;

	private LayoutContainer vmwareHyperVisorContainer;
	private LayoutContainer vmwareStandaloneVMSettingContainer;

	private LabelField hyperVHostLabel;
	private LabelField hyperVUsernameLabel;
	private LayoutContainer hyperVHyperVisorContainer;

	private FieldSet hypervisorFieldSet;
	private LayoutContainer hyperVVMSettingPanel;
	private ListStore<DiskDataStoreModel> hyperVDiskDataStore;
	private Grid<DiskDataStoreModel> hyperVDiskGrid;

	private LabelField hyperVVMNameLabel;
	private LabelField hyperVVMPathLabel;
	
	private ListStore<VMNetworkConfigInfoModel> hyperVNetworkStore = new ListStore<VMNetworkConfigInfoModel>();
	private Grid<VMNetworkConfigInfoModel> hyperVNetworkGrid;
	
	private LabelField registerAsClusterVM;
	
	private FieldSet vAppSettingContainer;
	private LabelField vDCField;
	private ListStore<VMNetworkConfigInfoModel> vAppNetworkStore = new ListStore<>();
	private Grid<VMNetworkConfigInfoModel> vAppNetworkGrid;	
	
	private FieldSet vAppChildVMSummaryContainer;
	private ListStore<RecoverVMOptionModel> vAppChildVMOptionStore = new ListStore<>();
	private Grid<RecoverVMOptionModel> vAppChildVMSummaryGrid;	
	
	public VMRecoverySummaryPanel(RestoreWizardContainer restoreWizardWindow) {
		wizard = restoreWizardWindow;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		//setStyleAttribute("margin", "10px");

		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		tl.setColumns(3);
		tl.setCellPadding(4);
		tl.setCellSpacing(4);
		this.setLayout(tl);
		TableData td = new TableData();
		td.setColspan(3);
		
		// Header Section
		this.add(renderHeaderSection(),td);

		// Destination Section
		destinationLabel = new Label(UIContext.Constants.restoreDestination());
		destinationLabel.addStyleName("restoreWizardSubItem");
		this.add(destinationLabel,td);
		
		destinationLF=new LabelField();
		//destinationLF.addStyleName("restoreWizardLeftSpacing");	
		destinationLF.setValue(UIContext.Constants.restoreToOriginalLocation());
		this.add(destinationLF,td);
		
		renderVirtualMachineContainer=renderVirtualMachine();
		renderVirtualMachineContainer.setVisible(false);
		this.add(renderVirtualMachineContainer,td);
		
		vAppSettingContainer = renderVAppSettings();
		vAppSettingContainer.setVisible(false);
		this.add(vAppSettingContainer,td);
		
		vAppChildVMSummaryContainer = renderVAppChildVMSummary();
		vAppChildVMSummaryContainer.setVisible(false);
		this.add(vAppChildVMSummaryContainer,td);
		
		setupDatastoreTableContainer=setupDatastoreTable();
		setupDatastoreTableContainer.setVisible(false);
		this.add(setupDatastoreTableContainer,td);		
		
		//Resolving Conflicts
        Label label = new Label(UIContext.Constants.resolvingConflicts());
		label.addStyleName("restoreWizardSubItem");
		this.add(label,td);
		
		resolvingConflictsLF = new LabelField();
//		resolvingConflictsLF.setText(UIContext.Constants.resolvingConflictsOverwriteExistingVM());
		//resolvingConflictsLF.addStyleName("restoreWizardLeftSpacing");
		this.add(resolvingConflictsLF,td);
		
		//<huvfe01>###
		resolvingConflictsVMID = new LabelField();
		this.add(resolvingConflictsVMID,td);
		
		//Post Recover
		label = new Label(UIContext.Constants.postRecover());
		label.addStyleName("restoreWizardSubItem");
		this.add(label,td);
				
		postRecoverLF = new LabelField();
//		postRecoverLF.setText(UIContext.Constants.postRecoverPowerOnVM());
		//postRecoverLF.addStyleName("restoreWizardLeftSpacing");
		this.add(postRecoverLF,td);
		
//		updateOptionsLabel();
	}
	

	
	private LayoutContainer setupDatastoreTable() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig sourcedisk = Utils.createColumnConfig("diskName",UIContext.Constants.vmRecoveryDataStoreGridSoruceDiskColumn(), 80, null);
		ColumnConfig diskSize = Utils.createColumnConfig("diskSize",UIContext.Constants.vmRecoveryDataStoreGridDiskSizeColumn(), 60, null);
		ColumnConfig sourcevolumes = Utils.createColumnConfig("volumeName",UIContext.Constants.vmRecoveryDataStoreGridSoruceVolumesColumn(), 100,	null);
		ColumnConfig datastoreLable = Utils.createColumnConfig("datastore", UIContext.Constants.vmRecoveryDataStoreGridDataStoreColumn(), 150, null);
		ColumnConfig diskFormatOption = Utils.createColumnConfig("diskFormatOption", UIContext.Constants.recoverVMHyperVDiskTypeLabel(), 150, null);
		
		GridCellRenderer<DiskDataStoreModel> diskFormatOptionRenderer = new GridCellRenderer<DiskDataStoreModel>() {
			@Override
			public Object render(DiskDataStoreModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<DiskDataStoreModel> store, Grid<DiskDataStoreModel> grid) {
				if (model.getDiskType()==DiskModel.VMware_VDISK_TYPE_THICK_LAZY)
					return UIContext.Constants.recoverVMwareVDiskThickLazy();
				else if (model.getDiskType()==DiskModel.VMware_VDISK_TYPE_THICK_EAGER)
					return UIContext.Constants.recoverVMwareVDiskThickEager();
				else if (model.getDiskType()==DiskModel.VMware_VDISK_TYPE_THIN)
					return UIContext.Constants.recoverVMwareVDiskThin();
				else if (model.getDiskType()==DiskModel.VMware_VDISK_TYPE_ORIGINAL)
					return UIContext.Constants.recoverVMHyperVDiskTypeKeepSame();
				
				return "";
			}
		};
		diskFormatOption.setRenderer(diskFormatOptionRenderer);
		
		configs.add(sourcedisk);
		configs.add(diskSize);
		configs.add(sourcevolumes);
		configs.add(diskFormatOption);
		configs.add(datastoreLable);

		datastoreColumnModel = new ColumnModel(configs);		
		
		datastoreStore = new ListStore<DiskDataStoreModel>();
		
		datastoreGrid = new Grid<DiskDataStoreModel>(datastoreStore,datastoreColumnModel);
		datastoreGrid.setTrackMouseOver(false);
		datastoreGrid.setAutoExpandColumn("datastore");
		datastoreGrid.setAutoWidth(true);
		datastoreGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		datastoreGrid.setBorders(true);
		datastoreGrid.setColumnLines(true);
		datastoreGrid.setWidth(600);
		datastoreGrid.setHeight(100);

		standaloneVMSettingContainer = new FieldSet();
		standaloneVMSettingContainer.ensureDebugId("DFCB68CD-7D53-4da9-B1DF-B4BE0CDDB613");
	    standaloneVMSettingContainer.setHeadingHtml(UIContext.Constants.vmRecoveryOtherInfo());
		
		vmwareStandaloneVMSettingContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		tl.setColumns(2);
		tl.setCellPadding(5);
		vmwareStandaloneVMSettingContainer.setLayout(tl);
		
		TableData td = new TableData();
		td.setWidth("15%");
		
		TableData td1 = new TableData();
		td1.setWidth("70%");
		
		esxServerLabel = new LabelField();
		esxServerLabel.setValue(UIContext.Constants.vmRecoveryEsxServerLabel());
		vmwareStandaloneVMSettingContainer.add(esxServerLabel,td);
		
		esxListLF = new LabelField();		
		vmwareStandaloneVMSettingContainer.add(esxListLF, td1);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryResourcePool());
		vmwareStandaloneVMSettingContainer.add(label,td);

		poolLF = new LabelField();
		vmwareStandaloneVMSettingContainer.add(poolLF, td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryVMDataStoreLabel());
		vmwareStandaloneVMSettingContainer.add(label,td);

		vmDataStoreLF = new LabelField();
		vmwareStandaloneVMSettingContainer.add(vmDataStoreLF, td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryDataStoreLabel());
		vmwareStandaloneVMSettingContainer.add(label,td);
		vmwareStandaloneVMSettingContainer.add(datastoreGrid, td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreVDSNetworkLable());
		vmwareStandaloneVMSettingContainer.add(label,td);
		
		setupNetworkTable();
		vmwareStandaloneVMSettingContainer.add(networkGrid, td1);
		
		standaloneVMSettingContainer.add(vmwareStandaloneVMSettingContainer);
		
		hyperVVMSettingPanel = renderHyperVVMInfo();
		standaloneVMSettingContainer.add(hyperVVMSettingPanel);
		
		return standaloneVMSettingContainer;
	}
	
	private void setupNetworkTable() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig adapter = Utils.createColumnConfig("label", UIContext.Constants.restoreVDSAdapterLabel(), 200, null);
		configs.add(adapter);
		
		ColumnConfig config = Utils.createColumnConfig("config", UIContext.Constants.restoreVDSConfigLable(), 240, null);
		GridCellRenderer<VMNetworkConfigInfoModel> cinfigRender = new GridCellRenderer<VMNetworkConfigInfoModel>() {
			@Override
			public Object render(VMNetworkConfigInfoModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<VMNetworkConfigInfoModel> store, Grid<VMNetworkConfigInfoModel> grid) {
				String deviceName = model.getDeviceName();
				String pgName = model.getPortgroupName();
				String switchName = model.getSwitchName();
				String displayName = UIContext.Constants.NA();
				
				if (deviceName != null && !deviceName.isEmpty()) {
					displayName = deviceName;
				} else if (pgName != null && !pgName.isEmpty() && switchName != null && !switchName.isEmpty()) {
					displayName = pgName + "(" + switchName + ")";
				} 
				
				String encodedName = Format.htmlEncode(displayName);
				return "<span qtip=\"" + encodedName + "\">"  + encodedName + "</span>";
			}
		};
		config.setRenderer(cinfigRender);
		configs.add(config);

		ColumnModel columnModel = new ColumnModel(configs);		
		networkStore = new ListStore<VMNetworkConfigInfoModel>();
		
		networkGrid = new Grid<VMNetworkConfigInfoModel>(networkStore, columnModel);
		networkGrid.setTrackMouseOver(false);
		networkGrid.setAutoExpandColumn("config");
		networkGrid.setAutoWidth(true);
		networkGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		networkGrid.setBorders(true);
		networkGrid.setColumnLines(true);
		networkGrid.setWidth(600);
		networkGrid.setHeight(100);
		new QuickTip(networkGrid);
	}

	private LayoutContainer renderVirtualMachine() {
		hypervisorFieldSet = new FieldSet();
		hypervisorFieldSet.ensureDebugId("F9A48D59-B2F0-49ad-986C-2A3744B40BBB");
	    hypervisorFieldSet.setHeadingHtml(UIContext.Constants.vmRecoveryVCInfo()); 
		
		vmwareHyperVisorContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(4);
		tl.setCellPadding(5);
		vmwareHyperVisorContainer.setLayout(tl);
		
		TableData td = new TableData();
		td.setWidth("18%");
		TableData td35 = new TableData();
		td35.setWidth("32%");
		
		serverLabel = new LabelField();
		serverLabel.setValue(UIContext.Constants.vmRecoveryVirtualCenterLabel());
		vmwareHyperVisorContainer.add(serverLabel,td);
		
		vcLF = new LabelField();
		vmwareHyperVisorContainer.add(vcLF, td35);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryProtocolLabel());
		vmwareHyperVisorContainer.add(label,td);
		
		protocolLF=new LabelField();
		vmwareHyperVisorContainer.add(protocolLF, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryUsernameLabel());
		vmwareHyperVisorContainer.add(label,td);
		
		usernameLF = new LabelField();
		vmwareHyperVisorContainer.add(usernameLF, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryPortLabel());
		vmwareHyperVisorContainer.add(label,td);
		
		portLF = new LabelField();
		vmwareHyperVisorContainer.add(portLF, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryPasswordLabel());
		vmwareHyperVisorContainer.add(label,td);
		
		passwordLF = new LabelField();
		vmwareHyperVisorContainer.add(passwordLF, td35);	
		
		vmVAppLabel = new LabelField();
		vmVAppLabel.setValue(UIContext.Constants.vmRecoveryVMNameLabel());
		vmwareHyperVisorContainer.add(vmVAppLabel,td);
		
		vmNameLF = new LabelField();
		vmwareHyperVisorContainer.add(vmNameLF, td35);
		
		hypervisorFieldSet.add(vmwareHyperVisorContainer);
		
		hyperVHyperVisorContainer = renderHyperVInfo();
		hypervisorFieldSet.add(hyperVHyperVisorContainer);
		
		return hypervisorFieldSet;		
	}

	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		container.setLayout(tl);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreSummary());
		label.addStyleName("restoreWizardTitle");
		container.add(label);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreSummaryDescription());
		container.add(label);

//		label = new LabelField();
//		label.setText(UIContext.Constants.restoreFilesToBeRestored());
//		label.addStyleName("restoreWizardSubItem");
//		container.add(label);

		return container;
	}	
	
	protected void afterRender(){
		super.afterRender();
		updateOptionsLabel();
	}

	@Override
	protected void updateDestinationLabel() {
		// TODO Auto-generated method stub		

	}

	@Override
	protected void updateOptionsLabel() {
		RecoverVMOptionModel vmOption = RestoreContext.getRestoreModel().recoverVMOption;
		standaloneVMSettingContainer.setVisible(false);
		hypervisorFieldSet.setVisible(false);
		vmwareHyperVisorContainer.setVisible(false);
		vmwareStandaloneVMSettingContainer.setVisible(false);
		hyperVHyperVisorContainer.setVisible(false);
		hyperVVMSettingPanel.setVisible(false);
		vAppSettingContainer.setVisible(false);
		vAppChildVMSummaryContainer.setVisible(false);
		
		if(vmOption.isOverwriteExistingVM()){
			resolvingConflictsLF.setValue(UIContext.Constants.resolvingConflictsOverwriteExistingVM());
		}else{
			resolvingConflictsLF.setValue(UIContext.Constants.resolvingConflictsNotOverwriteExistingVM());
		}
		
		//<huvfe01>###
		if (vmOption.isGenerateNewInstVMID()){
			resolvingConflictsVMID.setValue(UIContext.Constants.resolvingConflictsGenerateNewVMInstanceID());
		}
		else{
			resolvingConflictsVMID.setValue(UIContext.Constants.resolvingConflictsNotGenerateNewVMInstanceID());
		}
		
		if(vmOption.isPowerOnAfterRestore()){
			postRecoverLF.setValue(UIContext.Constants.postRecoverPowerOnVM());
		}else{
			postRecoverLF.setValue(UIContext.Constants.postRecoverNotPowerOnVM());
		}
		
		if(vmOption.getOriginalLocation()){
			destinationLF.setValue(UIContext.Constants.restoreToOriginalLocation());
			
			renderVirtualMachineContainer.setVisible(false);
			setupDatastoreTableContainer.setVisible(false);
		}else{
			destinationLF.setValue(UIContext.Constants.vmRecoverytoAltLoc());
			renderVirtualMachineContainer.setVisible(true);
			setupDatastoreTableContainer.setVisible(true);
			
			int vmType = RestoreContext.getBackupVMModel().getVMType();
			if (vmType == BackupVMModel.Type.VMware.ordinal()){
				updateVMwareStandaloneVMOptions();
			}else if (vmType == BackupVMModel.Type.VMware_VApp.ordinal()) {
				updateVAppOptions();
			} else {
				updateHyperVOptions();
			}
		}
	}
	
	private void updateVMwareStandaloneVMOptions() {
		RecoverVMOptionModel vmOption = RestoreContext.getRestoreModel().recoverVMOption;
		hypervisorFieldSet.setHeadingHtml(UIContext.Constants.vmRecoveryVCInfo());
		standaloneVMSettingContainer.setVisible(true);
		hypervisorFieldSet.setVisible(true);
		vmwareHyperVisorContainer.setVisible(true);
		vmwareStandaloneVMSettingContainer.setVisible(true);
		
		VirtualCenterModel vcModel = vmOption.getVCModel();
		vcLF.setValue(vcModel.getVcName());
		String protocol = vcModel.getProtocol().equals("http")?UIContext.Constants.vmRecoveryProtocolHttp():UIContext.Constants.vmRecoveryProtocolHttps();
		protocolLF.setValue(protocol);
		portLF.setValue(String.valueOf(vcModel.getPort()));
		usernameLF.setValue(vcModel.getUsername());
		passwordLF.setValue("******");			
		vmNameLF.setValue(vmOption.getVMName());
		
		esxListLF.setValue(vmOption.getESXServerName());
		if(vmOption.getResourcePoolShowName() == null || vmOption.getResourcePoolShowName().equals("")){
			poolLF.setValue(UIContext.Constants.NA());
		}else{
			poolLF.setValue(vmOption.getResourcePoolShowName());
		}
		vmDataStoreLF.setValue(vmOption.getVmDataStore());
		
		List<DiskDataStoreModel> diskDataStoreList = vmOption.getDiskDataStore();
		datastoreStore.removeAll();
		datastoreStore.add(diskDataStoreList);			
		datastoreGrid.getView().refresh(false);
		
		List<VMNetworkConfigInfoModel> networkList = vmOption.getVMNetworkConfigInfoList();
		networkStore.removeAll();
		networkStore.add(networkList);
		networkGrid.getView().refresh(false);
	}
	
	private void updateVAppOptions() {
		RecoverVMOptionModel vmOption = RestoreContext.getRestoreModel().recoverVMOption;
		hypervisorFieldSet.setHeadingHtml(UIContext.Constants.vAppRestoreSumVAppInfoLabel());
		hypervisorFieldSet.setVisible(true);
		vmwareHyperVisorContainer.setVisible(true);
		vAppSettingContainer.setVisible(true);
		vAppChildVMSummaryContainer.setVisible(true);
		standaloneVMSettingContainer.setVisible(false);
		
		VirtualCenterModel vcModel = vmOption.getVCModel();
		serverLabel.setValue(UIContext.Constants.vAppRestoreSumVAppVCloud());
		vcLF.setValue(vcModel.getVcName());
		String protocol = "HTTP".equalsIgnoreCase(vcModel.getProtocol()) ? UIContext.Constants.vmRecoveryProtocolHttp()
				: UIContext.Constants.vmRecoveryProtocolHttps();
		protocolLF.setValue(protocol);
		portLF.setValue(String.valueOf(vcModel.getPort()));
		usernameLF.setValue(vcModel.getUsername());
		passwordLF.setValue("******");
		
		vmVAppLabel.setValue(UIContext.Constants.vAppRestoreSumVAppName());
		vmNameLF.setValue(vmOption.getVMName());
		
		vDCField.setValue(vmOption.getVirtualDataCenterName());
		
		List<VMNetworkConfigInfoModel> networkList = vmOption.getVMNetworkConfigInfoList();
		vAppNetworkStore.removeAll();
		vAppNetworkStore.add(networkList);
		vAppNetworkGrid.getView().refresh(false);
		
		List<RestoreJobModel> childVMJobModels = RestoreContext.getRestoreModel().childRestoreJobList;
		List<RecoverVMOptionModel> childVMOptions = new ArrayList<>();
		if (childVMJobModels != null && !childVMJobModels.isEmpty()) {
			for (RestoreJobModel job : childVMJobModels) {
				childVMOptions.add(job.recoverVMOption);
			}
		}
		vAppChildVMOptionStore.removeAll();
		Collections.sort(childVMOptions, new Comparator<RecoverVMOptionModel>() {
			@Override
			public int compare(RecoverVMOptionModel vm1, RecoverVMOptionModel vm2) {
				if (vm1 == null && vm2 == null) {
					return 0;
				}
				if (vm1 == null) {
					return -1;
				}
				if (vm2 == null) {
					return 1;
				}
				
				String vmName1 = vm1.getVMName();
				String vmName2 = vm2.getVMName();
				if (vmName1 == null && vmName2 == null) {
					return 0;
				}
				if (vmName1 == null) {
					return -1;
				}
				if (vmName2 == null) {
					return 1;
				}

				return vmName1.compareToIgnoreCase(vmName2);
			}
		});
		vAppChildVMOptionStore.add(childVMOptions);
		vAppChildVMSummaryGrid.getView().refresh(false);
		
		if(vmOption.isOverwriteExistingVM()){
			resolvingConflictsLF.setValue(UIContext.Constants.vAppRestoreResolveConflictsDescription());
		}else{
			resolvingConflictsLF.setValue(UIContext.Constants.vAppRestoreResolveConflictsNotOverwriteExistingVApp());
		}
		
		if(vmOption.isPowerOnAfterRestore()){
			postRecoverLF.setValue(UIContext.Constants.vAppRestorePostRestoreDescription());
		}else{
			postRecoverLF.setValue(UIContext.Constants.vAppRestorePostRestoreNotPowerOnVapp());
		}
	}
	
	private FieldSet renderVAppSettings() {
		FieldSet vAppFieldSet = new FieldSet();
		vAppFieldSet.ensureDebugId("574f1816-9d20-4278-95e8-54ae45d7775f");
		vAppFieldSet.setHeadingHtml(UIContext.Constants.vAppRestoreSumVAppSettings());

		FlexTable vDCTable = new FlexTable();
		vDCTable.setCellPadding(5);
		vAppFieldSet.add(vDCTable);
		
		LabelField vDCLabel = new LabelField();
		vDCLabel.setValue(UIContext.Constants.vAppRestoreSumVAppVDC());
		vDCTable.setWidget(0, 0, vDCLabel);
		vDCField = new LabelField();
		vDCTable.setWidget(0, 1, vDCField);
		
		LayoutContainer container = new LayoutContainer();
		vAppFieldSet.add(container);

		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setCellPadding(5);
		container.setLayout(tl);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("30%");
		TableData tdField = new TableData();
		tdField.setWidth("70%");

		List<ColumnConfig> columnConfigList = new ArrayList<ColumnConfig>();
		ColumnConfig sourceAdapter = Utils.createColumnConfig("label", UIContext.Constants.vAppRestoreVAppNetworkHeaderNetworkName(),
				250, new GridCellRenderer<VMNetworkConfigInfoModel>() {
					@Override
					public Object render(VMNetworkConfigInfoModel model, String property, ColumnData config,
							int rowIndex, int colIndex, ListStore<VMNetworkConfigInfoModel> store,
							Grid<VMNetworkConfigInfoModel> grid) {
						return model.getLabel();
					}
				});
		columnConfigList.add(sourceAdapter);

		ColumnConfig targetNetwork = Utils.createColumnConfig("config", UIContext.Constants.vAppRestoreVAppNetworkHeaderOrgNetworkName(),
				250, new GridCellRenderer<VMNetworkConfigInfoModel>() {
					@Override
					public Object render(VMNetworkConfigInfoModel model, String property, ColumnData config,
							int rowIndex, int colIndex, ListStore<VMNetworkConfigInfoModel> store,
							Grid<VMNetworkConfigInfoModel> grid) {
						String displayName = UIContext.Constants.NA();
						String deviceName = model.getParentName();
						if (deviceName != null && !deviceName.isEmpty()) {
							displayName = deviceName;
						}
						String encodedName = Format.htmlEncode(displayName);
						return "<span qtip=\"" + encodedName + "\">" + encodedName + "</span>";
					}
				});
		columnConfigList.add(targetNetwork);

		vAppNetworkGrid = new Grid<VMNetworkConfigInfoModel>(vAppNetworkStore, new ColumnModel(columnConfigList));
		vAppNetworkGrid.setTrackMouseOver(false);
		vAppNetworkGrid.setAutoExpandColumn("config");
		vAppNetworkGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		vAppNetworkGrid.setBorders(true);
		vAppNetworkGrid.setColumnLines(true);
		vAppNetworkGrid.setWidth(550);
		vAppNetworkGrid.setHeight(100);
		new QuickTip(vAppNetworkGrid);

		LabelField vAppNetworkLabel = new LabelField(UIContext.Constants.restoreVDSNetworkLable());
		vAppNetworkLabel.setValue(UIContext.Constants.restoreVDSNetworkLable());
		container.add(vAppNetworkLabel, tdLabel);
		container.add(vAppNetworkGrid, tdField);
		
		return vAppFieldSet;
	}
	
	private FieldSet renderVAppChildVMSummary() {
		FieldSet summaryFieldSet = new FieldSet();
		summaryFieldSet.ensureDebugId("574f1816-9d20-4278-95e8-54ae45d7775f");
		summaryFieldSet.setHeadingHtml(UIContext.Constants.vAppRestoreChildVMSummaryGridLabel());
		
		LayoutContainer container = new LayoutContainer();
		summaryFieldSet.add(container);

		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setCellPadding(5);
		container.setLayout(tl);
		
		TableData tdLine = new TableData();
		tdLine.setColspan(2);
		tdLine.setWidth("100%");
		
		List<ColumnConfig> columnConfigList = new ArrayList<ColumnConfig>();
		GridCellRenderer<RecoverVMOptionModel> commmonRender = new GridCellRenderer<RecoverVMOptionModel>() {
			@Override
			public Object render(RecoverVMOptionModel model, String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<RecoverVMOptionModel> store, Grid<RecoverVMOptionModel> grid) {
				String displayName = UIContext.Constants.NA();
				String value = model.get(property);
				if (value != null) {
					displayName = value;
				}
				String encodedName = Format.htmlEncode(displayName);
				return "<span qtip=\"" + encodedName + "\">"  + encodedName + "</span>";
			}
		};
		
		ColumnConfig vmNameColumn = Utils.createColumnConfig("vmName", UIContext.Constants.vAppRestoreChildVMSummaryVMName(), 100, commmonRender);
		columnConfigList.add(vmNameColumn);
		
		ColumnConfig cpuColumn = Utils.createColumnConfig("cpuCount", UIContext.Constants.vAppRestoreChildVMSummaryCPUCount(), 80);
		columnConfigList.add(cpuColumn);
		
		ColumnConfig memorySizeColumn = Utils.createColumnConfig("memorySize", UIContext.Constants.vAppRestoreChildVMSummaryMemorySize(), 80);
		columnConfigList.add(memorySizeColumn);
		
		ColumnConfig storagePolicyColumn = Utils.createColumnConfig("profileName", UIContext.Constants.vAppRestoreChildVMSummaryStoragePolicy(), 100, commmonRender);
		columnConfigList.add(storagePolicyColumn);
		
		ColumnConfig dataStoreColumn = Utils.createColumnConfig("vmDataStore", UIContext.Constants.vAppRestoreChildVMSummaryDataStore(), 100, commmonRender);
		columnConfigList.add(dataStoreColumn);
		
		ColumnConfig networkColumn = Utils.createColumnConfig("network", UIContext.Constants.vAppRestoreChildVMSummaryNetworks(), 120,
				new GridCellRenderer<RecoverVMOptionModel>() {
					@Override
					public Object render(RecoverVMOptionModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<RecoverVMOptionModel> store, Grid<RecoverVMOptionModel> grid) {
						List<VMNetworkConfigInfoModel> nwList = model.getVMNetworkConfigInfoList();
						String displayName = UIContext.Constants.NA();
						if (nwList != null && !nwList.isEmpty()) {
							StringBuilder displayNameSB = new StringBuilder();
							for (VMNetworkConfigInfoModel adapter : nwList) {
								String label = adapter.getLabel();
								String network = adapter.getParentName();
								if (Utils.isEmptyOrNull(network)) {
									network = UIContext.Constants.NA();
								}
								displayNameSB.append(label + ": " + network + "\n");
							}
							if (displayNameSB.length() > 0) {
								int lastIndex = displayNameSB.lastIndexOf("\n");
								displayName = displayNameSB.toString().substring(0, lastIndex);
							}
						}
						String encodedDisplayName = Format.htmlEncode(displayName);
						String encodedTooltip = Format.htmlEncode(displayName.replaceAll("\\n", "</br>"));
						return "<span qtip=\"" + encodedTooltip + "\">"  + encodedDisplayName + "</span>";
					}
				});
		columnConfigList.add(networkColumn);

		vAppChildVMSummaryGrid = new Grid<>(vAppChildVMOptionStore, new ColumnModel(columnConfigList));
		vAppChildVMSummaryGrid.setTrackMouseOver(false);
		vAppChildVMSummaryGrid.setAutoExpandColumn("vmName");
		vAppChildVMSummaryGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		vAppChildVMSummaryGrid.setBorders(true);
		vAppChildVMSummaryGrid.setColumnLines(true);
		vAppChildVMSummaryGrid.setWidth(640);
		vAppChildVMSummaryGrid.setHeight(100);
		new QuickTip(vAppChildVMSummaryGrid);
		
		container.add(vAppChildVMSummaryGrid, tdLine);
		return summaryFieldSet;
	}

	@Override
	protected void updateRecvPointRestoreSource() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateSearchSource() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateArchiveRestoreSource() {
		// TODO Auto-generated method stub
		
	}
	
	private LayoutContainer renderHyperVInfo() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setCellPadding(5);
		container.setLayout(tl);
		
		TableData td = new TableData();
		td.setWidth("18%");
		TableData td35 = new TableData();
		td35.setWidth("32%");
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.hyperVClusterServerNameCaption());
		container.add(label,td);
		
		hyperVHostLabel = new LabelField();
		container.add(hyperVHostLabel, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryUsernameLabel());
		container.add(label,td);
		
		hyperVUsernameLabel = new LabelField();
		container.add(hyperVUsernameLabel, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryPasswordLabel());
		container.add(label,td);
		
		LabelField passwordLF = new LabelField();
		passwordLF.setValue("******");
		container.add(passwordLF, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreHyperVVMAsClusterLabel());
		container.add(label,td);
		
		registerAsClusterVM = new LabelField();
		container.add(registerAsClusterVM, td35);
			
		return container;	
	}
	
	private LayoutContainer renderHyperVVMInfo(){
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig sourcedisk = Utils.createColumnConfig("diskName",UIContext.Constants.vmRecoveryDataStoreGridSoruceDiskColumn(), 80, null);
		ColumnConfig diskSize = Utils.createColumnConfig("diskSize",UIContext.Constants.vmRecoveryDataStoreGridDiskSizeColumn(), 60, null);
		ColumnConfig sourcevolumes = Utils.createColumnConfig("volumeName",UIContext.Constants.vmRecoveryDataStoreGridSoruceVolumesColumn(), 100,	null);
		ColumnConfig datastoreLable = Utils.createColumnConfig("datastore", UIContext.Constants.coldStandbySettingHypervPathLabel(), 200, null);
		
		ColumnConfig diskFormatOption = Utils.createColumnConfig("diskFormatOption", UIContext.Constants.recoverVMHyperVDiskTypeLabel(), 150, null);
		
		GridCellRenderer<DiskDataStoreModel> diskFormatOptionRenderer = new GridCellRenderer<DiskDataStoreModel>() {
			@Override
			public Object render(DiskDataStoreModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<DiskDataStoreModel> store, Grid<DiskDataStoreModel> grid) {
				if (model.getDiskType()==DiskModel.HYPERV_VDISK_TYPE_DYNAMIC){
					return UIContext.Constants.recoverVMHyperVDiskTypeDynamic();
				}					
				else if (model.getDiskType()==DiskModel.HYPERV_VDISK_TYPE_FIXED){
					if (model.getQuickRecovery() > 0){
						return UIContext.Constants.recoverVMHyperVDiskTypeFixedQuick();
					}else{
						return UIContext.Constants.recoverVMHyperVDiskTypeFixed();
					}
				}
				else if (model.getDiskType()==DiskModel.HYPERV_VDISK_TYPE_ORIGINAL){
					return UIContext.Constants.recoverVMHyperVDiskTypeKeepSame();
				}
				
				return "";
			}
		};
		diskFormatOption.setRenderer(diskFormatOptionRenderer);
		
		configs.add(sourcedisk);
		configs.add(diskSize);
		configs.add(sourcevolumes);
		configs.add(diskFormatOption);
		configs.add(datastoreLable);

		ColumnModel datastoreColumnModel = new ColumnModel(configs);		
		
		hyperVDiskDataStore = new ListStore<DiskDataStoreModel>();
		
		hyperVDiskGrid = new Grid<DiskDataStoreModel>(hyperVDiskDataStore,datastoreColumnModel);
		hyperVDiskGrid.setTrackMouseOver(false);
		hyperVDiskGrid.setAutoExpandColumn("datastore");
		hyperVDiskGrid.setAutoWidth(true);
		hyperVDiskGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		hyperVDiskGrid.setBorders(true);
		hyperVDiskGrid.setColumnLines(true);
		hyperVDiskGrid.setWidth(600);
		hyperVDiskGrid.setHeight(100);
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		tl.setColumns(2);
		tl.setCellPadding(5);
		container.setLayout(tl);
		
		TableData td = new TableData();
		td.setWidth("15%");
		
		TableData td1 = new TableData();
		td1.setWidth("70%");
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryVMNameLabel());
		container.add(label,td);
		
		hyperVVMNameLabel = new LabelField();		
		container.add(hyperVVMNameLabel, td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreHyperVVMPathLabel());
		container.add(label,td);

		hyperVVMPathLabel = new LabelField();
		container.add(hyperVVMPathLabel, td1);
		
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreHyperVVirtualDisks());
		container.add(label,td);
		container.add(hyperVDiskGrid, td1);
		
		configs = new ArrayList<ColumnConfig>();
		ColumnConfig adapter = Utils.createColumnConfig("adapter", UIContext.Constants.hyperVNetworkColumnAdapter(), 200, null);
		ColumnConfig connectNetwork = Utils.createColumnConfig("connectNetwork", UIContext.Constants.hyperVNetworkColumnConnnection(), 200, null);
		
		GridCellRenderer<VMNetworkConfigInfoModel> adapterRenderer = new GridCellRenderer<VMNetworkConfigInfoModel>() {
			@Override
			public Object render(VMNetworkConfigInfoModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<VMNetworkConfigInfoModel> store,
					Grid<VMNetworkConfigInfoModel> grid) {
				return model.get("networkName");
			}
		};
		adapter.setRenderer(adapterRenderer);
		
		GridCellRenderer<VMNetworkConfigInfoModel> configRenderer = new GridCellRenderer<VMNetworkConfigInfoModel>() {
			@Override
			public Object render(VMNetworkConfigInfoModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<VMNetworkConfigInfoModel> store, Grid<VMNetworkConfigInfoModel> grid) {
				return model.get("networkConnection");
			}
		};
		connectNetwork.setRenderer(configRenderer);
		
		configs.add(adapter);
		configs.add(connectNetwork);
		
		ColumnModel networkColumnModel = new ColumnModel(configs);
		hyperVNetworkGrid = new Grid<VMNetworkConfigInfoModel>(hyperVNetworkStore, networkColumnModel);
		hyperVNetworkGrid.setTrackMouseOver(false);
		hyperVNetworkGrid.setAutoWidth(true);
		hyperVNetworkGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		hyperVNetworkGrid.setBorders(true);
		hyperVNetworkGrid.setColumnLines(true);
		hyperVNetworkGrid.setWidth(600);
		hyperVNetworkGrid.setHeight(100);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreVDSNetworkLable());
		container.add(label,td);
		container.add(hyperVNetworkGrid, td1);
		
		return container;
	}
	
	private void updateHyperVOptions(){
		RecoverVMOptionModel vmOption = RestoreContext.getRestoreModel().recoverVMOption;
		hypervisorFieldSet.setHeadingHtml(UIContext.Constants.vmRecoveryHyperVInfo());
		hyperVHyperVisorContainer.setVisible(true);
		hyperVVMSettingPanel.setVisible(true);
		
		VirtualCenterModel vcModel = vmOption.getVCModel();
		hyperVHostLabel.setValue(vcModel.getVcName());
		hyperVUsernameLabel.setValue(vcModel.getUsername());
		registerAsClusterVM.setValue(vmOption.isRegisterAsClusterHyperVVM()?UIContext.Constants.yes():UIContext.Constants.no());
		
		List<DiskDataStoreModel> diskDataStoreList = vmOption.getDiskDataStore();
		hyperVDiskDataStore.removeAll();
		hyperVDiskDataStore.add(diskDataStoreList);			
		hyperVDiskGrid.getView().refresh(false);
		
		hyperVVMNameLabel.setValue(vmOption.getVMName());
		hyperVVMPathLabel.setValue(vmOption.getVmDataStore());
		
		List<VMNetworkConfigInfoModel> networkStoreList = vmOption.getVMNetworkConfigInfoList();
		hyperVNetworkStore.removeAll();
		hyperVNetworkStore.add(networkStoreList);			
		hyperVNetworkGrid.getView().refresh(false);
	}
}
