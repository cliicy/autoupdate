package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.DataStoreModel;
import com.ca.arcflash.ui.client.model.DiskDataStoreModel;
import com.ca.arcflash.ui.client.model.DiskModel;
import com.ca.arcflash.ui.client.model.ESXServerModel;
import com.ca.arcflash.ui.client.model.RecoverVMOptionModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.VCloudDirectorModel;
import com.ca.arcflash.ui.client.model.VCloudStorageProfileModel;
import com.ca.arcflash.ui.client.model.VCloudVirtualDataCenterModel;
import com.ca.arcflash.ui.client.model.VMNetworkConfigGridModel;
import com.ca.arcflash.ui.client.model.VMNetworkConfigInfoModel;
import com.ca.arcflash.ui.client.model.VMStorage;
import com.ca.arcflash.ui.client.model.VMVolumeModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.text.client.LongParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;

public class VAppChildVMRecoveryOptionsPanel extends LayoutContainer {
	private static final LoginServiceAsync service = GWT.create(LoginService.class);
	private static int LABEL_WIDTH = 100;
	private static int COMBOBOX_WIDTH = 225;
	private static int CPU_MEMORY_FIELD_WIDTH = 120;
	
	private VAppRecoveryOptionsWizard vAppOptionsWizard;
	private VAppChildVMRecoveryWizard parentWizard;
	
	private FlexTable bgFlexTable = new FlexTable();

	private SimpleComboBox<String> vmNameComboBox;

	private Integer cpuCountOnVDC = 1;
	private SimpleComboBox<Integer> cpuCountComboxBox;

	private TextField<Long> memorySizeTextField;
	private LabelField memorySizeLabel;
	private Long memoryAllowedMax = 4L; // MB

	private ComboBox<VCloudStorageProfileModel> profileComboBox;
	private ComboBox<VMStorage> vmDataStoreComboBox;

	private CheckBox eachDiskSpecifyingCheckBox;
	private ListStore<DataStoreModel> diskDatastoreListStore = new ListStore<DataStoreModel>();
	private Grid<DataStoreModel> diskDatastoreConfGrid;
	private LabelField datastoreLabelInGrid;
	private FormBinding datastoreLabelFormBindings;

	private List<VMNetworkConfigInfoModel> adapterList = new ArrayList<>();
	private CheckBox eachAdapterSpecifyingCheckBox;
	private ListStore<VMNetworkConfigGridModel> adapterNetworkStore = new ListStore<VMNetworkConfigGridModel>();
	private Grid<VMNetworkConfigGridModel> adapterNetworkGrid;
	private Button addAdapteBtn = new Button();
	private Button removeAdapterBtn = new Button();

	private BackupVMModel backupVMModel;
	private String nodeName;
	private VCloudVirtualDataCenterModel destinationVDCModel;
	private List<VMNetworkConfigInfoModel> vAppNetworkModelList = new ArrayList<VMNetworkConfigInfoModel>();
	private List<VMStorage> vmStorageList = new ArrayList<VMStorage>();
	private List<String> adapterTypeList = new ArrayList<String>();
	private long totalDiskSize; // bytes
	private ESXServerModel destinationESXHost;
	private static List<ModelData> diskTypeModelList = new ArrayList<ModelData>();
	private static VMNetworkConfigInfoModelComparator nwInfoModelComparator = new VMNetworkConfigInfoModelComparator();
	private AsyncCallback<BackupVMModel> vAppChildVMsummaryCallBack;
	private boolean isVDCModelSetting = false;
	
	static {
		String[] diskTypeNames = new String[] { UIContext.Constants.vAppRestoreDiskTypeThickPovisionLazyZeroed(),
				UIContext.Constants.vAppRestoreDiskTypeThinProvision(),
				UIContext.Constants.vAppRestoreDiskTypeThickProvisionEagerZeroed() };
		for (int id = 0; id < 3; id++) {
			ModelData model = new BaseModelData();
			model.set("id", (long)id);
			model.set("name", diskTypeNames[id]);
			diskTypeModelList.add(model);
		}
	}

	public VAppChildVMRecoveryOptionsPanel(VAppRecoveryOptionsWizard vAppOptionsWizard,
			VAppChildVMRecoveryWizard parentWizard, BackupVMModel backupVMModel,
			List<VMNetworkConfigInfoModel> vAppNetworkModelList) {
		this.vAppOptionsWizard = vAppOptionsWizard;
		this.parentWizard = parentWizard;
		this.backupVMModel = backupVMModel;
		this.nodeName = VAppRecoveryOptionsWizard.getNodeName(backupVMModel);
		this.memoryAllowedMax = backupVMModel.getMemorySize();
		this.vAppNetworkModelList.clear();
		this.vAppNetworkModelList.addAll(vAppNetworkModelList);
		this.adapterList.addAll(backupVMModel.adapterList);
		
		calculateTotalDiskSize();

		this.setScrollMode(Scroll.NONE);
		this.setWidth("100%");
		this.setHeight("100%");
		this.setLayout(new RowLayout());
		
		bgFlexTable.setWidth("98%");
		bgFlexTable.setCellPadding(5);
		bgFlexTable.setCellSpacing(5);
		this.add(bgFlexTable);
		
		createBasicConfigTable();
		createDiskConfigTable();
		createAdapterConfigTable();
		createDatastoreBinding();
		
		freshVmNameComboBox();
		freshMemorySizeLabel();
		freshDiskDatastoreConfigGridStore();
		//freshAdapterNetworkConfigGrid();
	}

//	@Override
//	protected void onShow() {
//		super.onShow();
//
//		BackupVMModel originalVMModel = this.backupVMModel;
//		this.backupVMModel = RestoreContext.getBackupVMModel();
//		if (originalVMModel != this.backupVMModel) {
//			freshCpuCountComboBoxStore();
//			freshMemorySizeLabel();
//			freshDiskDatastoreConfigGridStore();
//		}
//	}

//	@Override
//	protected void onRender(Element parent, int index) {
//		super.onRender(parent, index);
//
//		freshVmNameComboBox();
//		freshMemorySizeLabel();
//		freshDiskDatastoreConfigGridStore();
//		freshAdapterNetworkConfigGrid();
//	}

	public BackupVMModel getBackupVMModel() {
		return backupVMModel;
	}

	public String getVMName() {
		String vmName = vmNameComboBox.getRawValue();
		if (Utils.isEmptyOrNull(vmName) && vmNameComboBox.getValue() != null) {
			vmName = vmNameComboBox.getValue().getValue();
		}
		if (!Utils.isEmptyOrNull(vmName)) {
			return vmName;
		}
		return backupVMModel.getVMName();
	}

	public SimpleComboBox<Integer> getCpuComboxBox() {
		return cpuCountComboxBox;
	}

	public TextField<Long> getMmemorySizeTextField() {
		return memorySizeTextField;
	}

	public ComboBox<VCloudStorageProfileModel> getProfileComboBox() {
		return profileComboBox;
	}

	public ComboBox<VMStorage> getVmDataStoreComboBox() {
		return vmDataStoreComboBox;
	}

	public SimpleComboBox<String> getVmNameComboBox() {
		return vmNameComboBox;
	}

	public Grid<DataStoreModel> getDiskDatastoreConfGrid() {
		return diskDatastoreConfGrid;
	}

	public Grid<VMNetworkConfigGridModel> getAdapterNetworkGrid() {
		return adapterNetworkGrid;
	}

	public void setVCloudVDCModel(VCloudVirtualDataCenterModel vDCModel, AsyncCallback<BackupVMModel> callBack) {
		isVDCModelSetting = true;
		destinationVDCModel = vDCModel;
		vAppChildVMsummaryCallBack = callBack;
		
		Integer count = vDCModel.getCPUCount();
		cpuCountOnVDC = (count == null || count <= 0) ? 1 : count;
		freshCpuCountComboBoxStore();

		List<VCloudStorageProfileModel> profileModelList = vDCModel.getStorageProfiles();
		freshProfileComboBoxStore(profileModelList);
	}
	
	public VCloudStorageProfileModel getConfigedStorageProfile() {
		return profileComboBox.getValue();
	}

	public VMStorage getConfigedVMStorage() {
		return vmDataStoreComboBox.getValue();
	}

	/**
	 * Return the total bytes of all the disks.
	 * 
	 * @return
	 */
	public long getTotalDiskSize() {
		return this.totalDiskSize;
	}

	private void calculateTotalDiskSize() {
		Long totalSize = 0L;

		List<DiskModel> diskList = backupVMModel.diskList;
		if (diskList != null && diskList.size() > 0) {
			for (DiskModel diskModel : diskList) {
				totalSize += diskModel.getSize();
			}
		}

		this.totalDiskSize = totalSize;
	}
	
	private void createBasicConfigTable() {
		bgFlexTable.getFlexCellFormatter().setColSpan(0, 0, 6);
		LabelField sectionLabel = new LabelField(UIContext.Constants.vAppRestoreBasicSettingsLabel());
		sectionLabel.addStyleName("restoreWizardSubItem");
		bgFlexTable.setWidget(0, 0, sectionLabel);

		LabelField lable = new LabelField(UIContext.Constants.vAppRestoreChildVMSummaryVMName());
		lable.setWidth(LABEL_WIDTH);
		bgFlexTable.setWidget(1, 0, lable);
		vmNameComboBox = new SimpleComboBox<String>();
		vmNameComboBox.ensureDebugId("53b2abb0-4083-4bca-8db7-639a35f3730e");
		vmNameComboBox.setAllowBlank(false);
		vmNameComboBox.setWidth(COMBOBOX_WIDTH);
		vmNameComboBox.setEditable(true);
		vmNameComboBox.setValidateOnBlur(true);
		bgFlexTable.setWidget(1, 1, vmNameComboBox);
		
		vmNameComboBox.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value == null || value.trim().isEmpty()) {
					return UIContext.Constants.vAppRestoreRequiredFieldMark();
				} else {
					return null;
				}
			}
		});

		lable = new LabelField(UIContext.Constants.vAppRestoreChildVMSummaryCPUCount());
		lable.setWidth(LABEL_WIDTH);
		bgFlexTable.setWidget(2, 0, lable);
		cpuCountComboxBox = new SimpleComboBox<Integer>();
		cpuCountComboxBox.ensureDebugId("12a897cc-3f2b-4007-bd4c-94cb9341ca10");
		cpuCountComboxBox.setAllowBlank(false);
		cpuCountComboxBox.setValidateOnBlur(false);
		cpuCountComboxBox.setTriggerAction(TriggerAction.ALL);
		cpuCountComboxBox.setEditable(false);
		cpuCountComboxBox.setWidth(CPU_MEMORY_FIELD_WIDTH);
		bgFlexTable.setWidget(2, 1, cpuCountComboxBox);

		lable = new LabelField(UIContext.Constants.vAppRestoreChildVMSummaryMemorySize());
		lable.setWidth(LABEL_WIDTH);
		bgFlexTable.setWidget(2, 4, lable);
		memorySizeTextField = new TextField<Long>();
		memorySizeTextField.ensureDebugId("12754c23-c0bf-400e-b2cf-8e4008a2e44e");
		memorySizeTextField.setAllowBlank(false);
		memorySizeTextField.setWidth(CPU_MEMORY_FIELD_WIDTH);
		memorySizeTextField.setValidateOnBlur(true);
		memorySizeTextField.setToolTip(UIContext.Constants.vAppRestoreMemorySize4MBMultiple());
		memorySizeTextField.setPropertyEditor(new PropertyEditor<Long>() {
			@Override
			public String getStringValue(Long value) {
				if (value != null) {
					return value.toString();
				} else {
					return "";
				}
			}

			@Override
			public Long convertStringValue(String value) {
				try {
					return LongParser.instance().parse(value);
				} catch (ParseException e) {
					return null;
				}
			}
		});
		memorySizeTextField.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				try {
					Long size = LongParser.instance().parse(value);
					if (size == null) {
						return UIContext.Constants.vAppRestoreRequiredFieldMark();
					}
					if (size <= 0) {
						return UIContext.Constants.vAppRestoreMemorySize0MBLarge();
					} else if (size > memoryAllowedMax) {
						return UIContext.Messages.vAppRestoreMemorySizeLargerMax(memoryAllowedMax, size);
					} else if (size % 4 != 0){
						return UIContext.Constants.vAppRestoreMemorySize4MBMultiple();
					} else {
						return null;
					}
				} catch (ParseException e) {
					return UIContext.Constants.vAppRestoreInvalidValue();
				}
			}
		});
		FlexTable memoryTable = new FlexTable();
		memoryTable.setWidget(0, 0, memorySizeTextField);
		String text = UIContext.Messages.vAppRestoreMaxMemorySize(memoryAllowedMax);
		memorySizeLabel = new LabelField(text);
		memorySizeLabel.setToolTip(text);
		memoryTable.setWidget(0, 1, memorySizeLabel);
		memoryTable.setWidth(COMBOBOX_WIDTH + "px");
		bgFlexTable.setWidget(2, 5, memoryTable);

		lable = new LabelField(UIContext.Constants.vAppRestoreChildVMSummaryStoragePolicy());
		lable.setWidth(LABEL_WIDTH);
		bgFlexTable.setWidget(3, 0, lable);
		profileComboBox = new ComboBox<VCloudStorageProfileModel>();
		profileComboBox.ensureDebugId("dd938f93-bee1-4310-bf93-840ef17db776");
		profileComboBox.setWidth(COMBOBOX_WIDTH);
		profileComboBox.setDisplayField("name");
		profileComboBox.setAllowBlank(false);
		profileComboBox.setValidateOnBlur(false);
		profileComboBox.setTriggerAction(TriggerAction.ALL);
		profileComboBox.setEditable(false);
		profileComboBox.setStore(new ListStore<VCloudStorageProfileModel>());
		profileComboBox.setTemplate(VAppRecoveryOptionsWizard.getComboBoxTemplate("name"));
		bgFlexTable.setWidget(3, 1, profileComboBox);

		profileComboBox.addSelectionChangedListener(new SelectionChangedListener<VCloudStorageProfileModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<VCloudStorageProfileModel> se) {
				vmStorageList.clear();

				VCloudStorageProfileModel selectedItem = se.getSelectedItem();
				String emptyErrorMsg = UIContext.Constants.vAppRestoreNoDataStoreOnPolicy();
				if (selectedItem == null) {
					vmDataStoreComboBox.markInvalid(emptyErrorMsg);
					freshDatastoreComboBoxStore();
					freshAdapterNetworkConfigGrid();
					if (isVDCModelSetting) {
						vAppChildVMsummaryCallBack.onSuccess(getVMSummary());
						isVDCModelSetting = false; 
					}
					return;
				}

				List<VMStorage> avaStorageList = selectedItem.getStorages();
				if (avaStorageList == null || avaStorageList.isEmpty()) {
					vmDataStoreComboBox.markInvalid(emptyErrorMsg);
				} else {
					preprocessVMStorageList(avaStorageList);
					vmStorageList.addAll(avaStorageList);
				}
				freshDatastoreComboBoxStore();
			}
		});
		
		lable = new LabelField(UIContext.Constants.vAppRestoreVMDataStore());
		lable.setWidth(LABEL_WIDTH);
		bgFlexTable.setWidget(3, 4, lable);
		vmDataStoreComboBox = new ComboBox<VMStorage>();
		vmDataStoreComboBox.ensureDebugId("65ff0832-0cd6-4b60-8ed7-7d47365fa00a");
		vmDataStoreComboBox.setWidth(COMBOBOX_WIDTH);
		vmDataStoreComboBox.setDisplayField("displayName");
		vmDataStoreComboBox.setAllowBlank(false);
		vmDataStoreComboBox.setValidateOnBlur(false);
		vmDataStoreComboBox.setTriggerAction(TriggerAction.ALL);
		vmDataStoreComboBox.setEditable(false);
		vmDataStoreComboBox.setStore(new ListStore<VMStorage>());
		vmDataStoreComboBox.setTemplate(VAppRecoveryOptionsWizard.getComboBoxTemplate("displayName"));
		bgFlexTable.setWidget(3, 5, vmDataStoreComboBox);

		vmDataStoreComboBox.addSelectionChangedListener(new SelectionChangedListener<VMStorage>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<VMStorage> se) {
				if (se != null) {
					VMStorage vmxDS = se.getSelectedItem();
					if (vmxDS != null) {
						datastoreLabelFormBindings.bind(vmxDS);  
						getDestinationESXHost(vmxDS);
						return;
					}
				}
				
				freshDiskDatastoreConfigGridStore();
				freshAdapterNetworkConfigGrid();
				if (isVDCModelSetting) {
					vAppChildVMsummaryCallBack.onSuccess(getVMSummary());
					isVDCModelSetting = false; 
				}
			}
		});
	}
	
	private void createDatastoreBinding() {
		FormPanel panel = new FormPanel();
		panel.ensureDebugId("737b6023-7bcc-4722-aff7-905b79dc38a0");
		panel.setHeaderVisible(false);
		
		datastoreLabelInGrid = new LabelField();
		datastoreLabelInGrid.setWidth(240);
		datastoreLabelInGrid.ensureDebugId("d6649fd0-0695-47a9-a9a7-55ec32533424");
		datastoreLabelInGrid.setName("displayName");
		panel.add(datastoreLabelInGrid);
		
		datastoreLabelFormBindings = new FormBinding(panel, true);
	}

	private void freshCpuCountComboBoxStore() {
		cpuCountComboxBox.removeAll();

		Integer temp = backupVMModel.getMaxCPUCount();
		int cpuCountOnVM = temp == null ? 0 : temp;
		int maxCount = cpuCountOnVDC > cpuCountOnVM ? cpuCountOnVM : cpuCountOnVDC;

		for (int i = 1; i <= maxCount; i++) {
			cpuCountComboxBox.add(i);
		}
		cpuCountComboxBox.setSimpleValue(getMatchedCpuCount(backupVMModel.getCPUCount(), maxCount));
	}

	private void freshVmNameComboBox() {
		vmNameComboBox.removeAll();
		vmNameComboBox.add(backupVMModel.getVMName());
		vmNameComboBox.setSimpleValue(backupVMModel.getVMName());
	}

	private void freshMemorySizeLabel() {
		Long temp = backupVMModel.getMaxMemorySizeGB();
		long maxMeomorySize = temp == null ? 0L : temp * 1024;

		memoryAllowedMax = maxMeomorySize;
		String text = UIContext.Messages.vAppRestoreMaxMemorySize(memoryAllowedMax);
		memorySizeLabel.setValue(text);
		memorySizeLabel.setToolTip(text);
		
		memorySizeTextField.setValue(getMatchedMemorySize(backupVMModel.getMemorySize()));
	}

	private void freshProfileComboBoxStore(List<VCloudStorageProfileModel> profileModelList) {
		ListStore<VCloudStorageProfileModel> store = profileComboBox.getStore();
		store.removeAll();
		VCloudStorageProfileModel matchedProfile = null;
		if (profileModelList != null && !profileModelList.isEmpty()) {
			store.add(profileModelList);
			matchedProfile = getMatchedStoragePofile(backupVMModel.getStorageProfileId(),
					backupVMModel.getStorageProfileName(), profileModelList);
		} 
		
		profileComboBox.setValue(matchedProfile);
		if (matchedProfile == null) {
			freshDiskDatastoreConfigGridStore();
			freshAdapterNetworkConfigGrid();
			if (isVDCModelSetting) {
				vAppChildVMsummaryCallBack.onSuccess(getVMSummary());
				isVDCModelSetting = false;
			}
		}
	}

	private void preprocessVMStorageList(List<VMStorage> modelList) {
		if (modelList != null && !modelList.isEmpty()) {
			for (VMStorage storage : modelList) {
				storage.setDisplayName(UIContext.Messages.vSphereDatastoreFreeSize(storage.getName(),
						Utils.bytes2String(storage.getFreeSize())));
			}
		}
	}

	private void freshDatastoreComboBoxStore() {
		ListStore<VMStorage> dsStore = vmDataStoreComboBox.getStore();
		dsStore.removeAll();
		
		VMStorage matchedStorage = null;
		if (vmStorageList != null && !vmStorageList.isEmpty()) {
			dsStore.add(vmStorageList);
			matchedStorage = getMatchedDatastore(backupVMModel.getVMXDataStoreId(),
					backupVMModel.getVMXDataStoreName(), vmStorageList);
		}
		
		vmDataStoreComboBox.setValue(matchedStorage);
		if (matchedStorage == null) {
			freshDiskDatastoreConfigGridStore();
			freshAdapterNetworkConfigGrid();
			if (isVDCModelSetting) {
				vAppChildVMsummaryCallBack.onSuccess(getVMSummary());
				isVDCModelSetting = false;
			}
		}
	}

	private void createDiskConfigTable() {
		bgFlexTable.getFlexCellFormatter().setColSpan(4, 0, 6);
		LabelField sectionLabel = new LabelField(UIContext.Constants.vAppRestoreVMDiskGridLabel());
		sectionLabel.addStyleName("restoreWizardSubItem");
		bgFlexTable.setWidget(4, 0, sectionLabel);

		bgFlexTable.getFlexCellFormatter().setColSpan(5, 0, 6);
		eachDiskSpecifyingCheckBox = new CheckBox();
		eachDiskSpecifyingCheckBox.ensureDebugId("54f266bd-3511-4f54-ac59-a21cbd600f0e");
		eachDiskSpecifyingCheckBox.setBoxLabel(UIContext.Constants.vAppRestoreVMDiskConfigWay());
		eachDiskSpecifyingCheckBox.setValue(Boolean.TRUE);
		bgFlexTable.setWidget(5, 0, eachDiskSpecifyingCheckBox);
		
		eachDiskSpecifyingCheckBox.addListener(Events.OnChange, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				freshDiskDatastoreConfigGridStore();
			}
		});

		createDiskDatastoreConfigGrid();
		bgFlexTable.getFlexCellFormatter().setColSpan(6, 0, 6);
		bgFlexTable.setWidget(6, 0, diskDatastoreConfGrid);
	}

	private void createDiskDatastoreConfigGrid() {
		List<ColumnConfig> columnConfigList = new ArrayList<ColumnConfig>();

		ColumnConfig sourceDisk = Utils.createColumnConfig("diskNumber", UIContext.Constants.vAppRestoreVMDiskHeader(),
				80, new GridCellRenderer<DataStoreModel>() {
					@Override
					public Object render(DataStoreModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<DataStoreModel> store, Grid<DataStoreModel> grid) {
						if (model.isSpecifyAll()) {
							return UIContext.Messages.vAppRestoreVMAllDisks(store.getCount());
						}
						
						DiskModel diskModel = model.getDiskModel();
						String stringDisk = UIContext.Constants.vmRecoveryDisk() + diskModel.getDiskNumber();
						return stringDisk;
					}
				});
		columnConfigList.add(sourceDisk);

		ColumnConfig sourceVolumn = Utils.createColumnConfig("diskSize",
				UIContext.Constants.vAppRestoreVMSoruceVolumesHeader(), 145,
				new GridCellRenderer<DataStoreModel>() {
					@Override
					public Object render(DataStoreModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<DataStoreModel> store, Grid<DataStoreModel> grid) {
						String volumes = "";
						String sizeStr = "";

						DiskModel diskModel = model.getDiskModel();
						if (diskModel != null) {
							if (diskModel.volumeModelList != null && diskModel.volumeModelList.size() > 0) {
								for (VMVolumeModel volumeModel : diskModel.volumeModelList) {
									volumes = volumes + "/" + volumeModel.getDriveLetter();
								}
							}
							if (!volumes.isEmpty() && volumes.charAt(0) == '/') {
								volumes = volumes.substring(1);
							}
							sizeStr = Utils.bytes2String(diskModel.getSize());
						}

						if (volumes.isEmpty()) {
							volumes = UIContext.Constants.vAppRestoreUnknown();
						}
						return volumes + "(" + sizeStr + ")";
					}
				});
		columnConfigList.add(sourceVolumn);

		ColumnConfig diskType = Utils.createColumnConfig("diskType", UIContext.Constants.vAppRestoreVMTypeHeader(),
				200, new GridCellRenderer<DataStoreModel>() {
					@Override
					public Object render(DataStoreModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<DataStoreModel> store, Grid<DataStoreModel> grid) {
						ComboBox<ModelData> diskTypeComboBox = model.getDiskTypeComboBox();
						diskTypeComboBox.setWidth(170);
						diskTypeComboBox.setAllowBlank(false);
						diskTypeComboBox.setTriggerAction(TriggerAction.ALL);
						diskTypeComboBox.ensureDebugId("diskTypeComboBox-" + rowIndex + "-" + colIndex);
						diskTypeComboBox.setEditable(false);
						diskTypeComboBox.setDisplayField("name");
						diskTypeComboBox.setTemplate(VAppRecoveryOptionsWizard.getComboBoxTemplate("name"));

						return diskTypeComboBox;
					}
				});
		columnConfigList.add(diskType);

		ColumnConfig targetDatastore = Utils.createColumnConfig("esxDataStoreComboBox",
				UIContext.Constants.vAppRestoreVMTargetDataStoreHeader(), 200,
				new GridCellRenderer<DataStoreModel>() {
					@Override
					public Object render(DataStoreModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<DataStoreModel> store, Grid<DataStoreModel> grid) {
						return datastoreLabelInGrid;
					}
				});
		columnConfigList.add(targetDatastore);

		diskDatastoreListStore.removeAll();
		diskDatastoreConfGrid = new Grid<DataStoreModel>(diskDatastoreListStore, new ColumnModel(columnConfigList));
		diskDatastoreConfGrid.ensureDebugId("64cdfa7a-10d9-4f15-acfc-755d56943480");
		diskDatastoreConfGrid.setTrackMouseOver(false);
		diskDatastoreConfGrid.setAutoExpandColumn("esxDataStoreComboBox");
		diskDatastoreConfGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		diskDatastoreConfGrid.setBorders(true);
		diskDatastoreConfGrid.setColumnLines(true);
		diskDatastoreConfGrid.setWidth(672);
		diskDatastoreConfGrid.setHeight(120);
	}

	private void freshDiskDatastoreConfigGridStore() {
		diskDatastoreListStore.removeAll();

		if (backupVMModel.diskList != null && !backupVMModel.diskList.isEmpty()) {
			if (eachDiskSpecifyingCheckBox.getValue()) {
				for (DiskModel diskModel : backupVMModel.diskList) {
					DataStoreModel dsModel = new DataStoreModel(diskModel);
					diskDatastoreListStore.add(dsModel);
	
					ComboBox<ModelData> diskTypeComboxBox = dsModel.getDiskTypeComboBox();
					ListStore<ModelData> diskTypeStore = diskTypeComboxBox.getStore();
					diskTypeStore.removeAll();
					diskTypeStore.add(diskTypeModelList);
					if (diskTypeStore.getCount() > 0) {
						diskTypeComboxBox.setValue(getMatchedDiskType(diskModel.getDiskType()));
					}
				}
			} else {
				DiskModel totalDiskModel = new DiskModel(); 
				totalDiskModel.volumeModelList  = new ArrayList<>();
				long totalSize = 0L;
				for (DiskModel diskModel : backupVMModel.diskList) {
					if (diskModel.volumeModelList != null && diskModel.volumeModelList.size() > 0) {
						totalDiskModel.volumeModelList.addAll(diskModel.volumeModelList);
					}
					totalSize += diskModel.getSize();
				}
				
				totalDiskModel.setSize(totalSize);
				DataStoreModel dsModel = new DataStoreModel(totalDiskModel);
				dsModel.setSpecifyAll(true);
				diskDatastoreListStore.add(dsModel);

				ComboBox<ModelData> diskTypeComboxBox = dsModel.getDiskTypeComboBox();
				ListStore<ModelData> diskTypeStore = diskTypeComboxBox.getStore();
				diskTypeStore.removeAll();
				diskTypeStore.add(diskTypeModelList);
				if (diskTypeStore.getCount() > 0) {
					diskTypeComboxBox.setValue(getMatchedDiskType(backupVMModel.diskList.get(0).getDiskType()));
				}
			}
		}
	}

	/*
	private void freshDiskGridTargetDatastore() {
		List<DataStoreModel> dsConfigModelList = diskDatastoreListStore.getModels();
		if (dsConfigModelList != null && !dsConfigModelList.isEmpty() && vmStorageList != null
				&& !vmStorageList.isEmpty()) {
			for (DataStoreModel dsModel : dsConfigModelList) {
				ComboBox<ModelData> targetDSComboxBox = dsModel.getEsxDataStoreComboBox();
				ListStore<ModelData> targetDSStore = targetDSComboxBox.getStore();
				targetDSStore.removeAll();
				targetDSStore.add(vmStorageList);

				if (targetDSStore.getCount() > 0) {
					targetDSComboxBox.setValue(targetDSStore.getAt(0));
				}
			}
		}
	}
	*/

	private void createAdapterConfigTable() {
		bgFlexTable.getFlexCellFormatter().setColSpan(7, 0, 6);
		LabelField sectionLabel = new LabelField(UIContext.Constants.vAppRestoreVMAdapterGridLabel());
		sectionLabel.addStyleName("restoreWizardSubItem");
		bgFlexTable.setWidget(7, 0, sectionLabel);

		bgFlexTable.getFlexCellFormatter().setColSpan(8, 0, 6);
		eachAdapterSpecifyingCheckBox = new CheckBox();
		eachAdapterSpecifyingCheckBox.ensureDebugId("fd2b3d0e-e71a-435f-a2b8-22ff1ba03f8c");
		eachAdapterSpecifyingCheckBox.setBoxLabel(UIContext.Constants.vAppRestoreVMAdapterConfigWay());
		eachAdapterSpecifyingCheckBox.setValue(Boolean.TRUE);
		bgFlexTable.setWidget(8, 0, eachAdapterSpecifyingCheckBox);
		
		eachAdapterSpecifyingCheckBox.addListener(Events.OnChange, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				freshAdapterNetworkConfigGrid();
			}
		});
		
		bgFlexTable.getFlexCellFormatter().setColSpan(9, 0, 6);
		bgFlexTable.setWidget(9, 0, createAdapterCROperationLine());
		
		createAdapterNetworkConfigGrid();
		bgFlexTable.getFlexCellFormatter().setColSpan(10, 0, 6);
		bgFlexTable.setWidget(10, 0, adapterNetworkGrid);
		
	}

	private LayoutContainer createAdapterCROperationLine() {
		LayoutContainer btnContainer = new LayoutContainer();
		HBoxLayout layout = new HBoxLayout();
		layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		btnContainer.setLayout(layout);

		addAdapteBtn.setHtml(UIContext.Constants.vAppRestoreVMAddAdapterBtn());
		addAdapteBtn.ensureDebugId("c8e23637-c41f-4b30-939f-f95e0a410a53");
		addAdapteBtn.setWidth(70);
		btnContainer.add(addAdapteBtn, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
		addAdapteBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addAdapterNetworkConfigGrid();
			}
		});;
			
		removeAdapterBtn.setHtml(UIContext.Constants.vAppRestoreVMRemoveAdapterBtn());
		removeAdapterBtn.ensureDebugId("b78d6e43-572e-467c-9a68-1fb3dbe90e3f");
		removeAdapterBtn.setWidth(70);
		removeAdapterBtn.setEnabled(false);
		btnContainer.add(removeAdapterBtn, new HBoxLayoutData(new Margins(0)));
		removeAdapterBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				removeAdapterNetworkConfigGrid();
			}
		});
		
		return btnContainer;
	}

	private void createAdapterNetworkConfigGrid() {
		List<ColumnConfig> columnConfigList = new ArrayList<ColumnConfig>();

		ColumnConfig sourceAdapter = Utils.createColumnConfig("label", UIContext.Constants.vAppRestoreVMAdapterHeader(),
				220, new GridCellRenderer<VMNetworkConfigGridModel>() {
					@Override
					public Object render(VMNetworkConfigGridModel model, String property, ColumnData config,
							int rowIndex, int colIndex, ListStore<VMNetworkConfigGridModel> store,
							Grid<VMNetworkConfigGridModel> grid) {
						VMNetworkConfigInfoModel infoModel = model.getInfoModel();
						return infoModel.getLabel();
					}
				});
		columnConfigList.add(sourceAdapter);

		ColumnConfig adapterType = Utils.createColumnConfig("adapterTypeComboBox", UIContext.Constants.vAppRestoreVMTypeHeader(),
				220, new GridCellRenderer<VMNetworkConfigGridModel>() {
					@Override
					public Object render(VMNetworkConfigGridModel model, String property, ColumnData config,
							int rowIndex, int colIndex, ListStore<VMNetworkConfigGridModel> store,
							Grid<VMNetworkConfigGridModel> grid) {
						ComboBox<BaseModelData> adapterTypeConfigComboBox = model.getAdapterTypeConfigComboBox();
						adapterTypeConfigComboBox.setWidth(190);
						adapterTypeConfigComboBox.setAllowBlank(true);
						adapterTypeConfigComboBox.setTriggerAction(TriggerAction.ALL);
						adapterTypeConfigComboBox.ensureDebugId("adapterTypeConfigComboBox-" + rowIndex + "-" + colIndex);
						adapterTypeConfigComboBox.setEditable(false);
						adapterTypeConfigComboBox.setDisplayField("displayName");
						adapterTypeConfigComboBox.setTemplate(VAppRecoveryOptionsWizard.getComboBoxTemplate("displayName"));

						return adapterTypeConfigComboBox;
					}
				});
		columnConfigList.add(adapterType);

		ColumnConfig targetNetwork = Utils.createColumnConfig("configComboBox", UIContext.Constants.vAppRestoreVMVirtualNetworkHeader(),
				220, new GridCellRenderer<VMNetworkConfigGridModel>() {
					@Override
					public Object render(VMNetworkConfigGridModel model, String property, ColumnData config,
							int rowIndex, int colIndex, ListStore<VMNetworkConfigGridModel> store,
							Grid<VMNetworkConfigGridModel> grid) {
						ComboBox<BaseModelData> networkConfigComboBox = model.getAvaliableConfigInfoComboBox();
						networkConfigComboBox.setWidth(190);
						networkConfigComboBox.setAllowBlank(true);
						networkConfigComboBox.setTriggerAction(TriggerAction.ALL);
						networkConfigComboBox.ensureDebugId("configComboBox-" + rowIndex + "-" + colIndex);
						networkConfigComboBox.setEditable(false);
						networkConfigComboBox.setDisplayField("label");
						networkConfigComboBox.setTemplate(VAppRecoveryOptionsWizard.getComboBoxTemplate("label"));
						
						return networkConfigComboBox;
					}
				});
		columnConfigList.add(targetNetwork);

		adapterNetworkStore.removeAll();
		adapterNetworkGrid = new Grid<VMNetworkConfigGridModel>(adapterNetworkStore, new ColumnModel(columnConfigList));
		adapterNetworkGrid.setTrackMouseOver(false);
		adapterNetworkGrid.setAutoExpandColumn("configComboBox");
		adapterNetworkGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		adapterNetworkGrid.setBorders(true);
		adapterNetworkGrid.setColumnLines(true);
		adapterNetworkGrid.setWidth(672);
		adapterNetworkGrid.setHeight(120);
		
		adapterNetworkGrid.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<VMNetworkConfigGridModel>() {
					@Override
					public void selectionChanged(SelectionChangedEvent<VMNetworkConfigGridModel> se) {
						if (se != null && se.getSelectedItem() != null) {
							removeAdapterBtn.setEnabled(!adapterList.isEmpty());
						} else {
							removeAdapterBtn.setEnabled(false);
						}
					}
				});
	}

	private void freshAdapterNetworkConfigGrid() {
		adapterNetworkStore.removeAll();

		List<BaseModelData> adapterTypeModelList = new ArrayList<BaseModelData>();
		for (String type : adapterTypeList) {
			BaseModelData typeModel = new BaseModelData();
			typeModel.set("displayName", type);
			adapterTypeModelList.add(typeModel);
		}

		if (adapterList != null && !adapterList.isEmpty()) {
			if (eachAdapterSpecifyingCheckBox.getValue()) {
				for (VMNetworkConfigInfoModel adapter : adapterList) {
					VMNetworkConfigGridModel configModel = new VMNetworkConfigGridModel(adapter);
					adapterNetworkStore.add(configModel);
	
					ComboBox<BaseModelData> adapterTypeComboBox = configModel.getAdapterTypeConfigComboBox();
					ListStore<BaseModelData> adapterTypeStore = adapterTypeComboBox.getStore();
					adapterTypeStore.removeAll();
					adapterTypeStore.add(adapterTypeModelList);
					if (adapterTypeStore.getCount() > 0) {
						adapterTypeComboBox.setValue(getMatchedAdapterType(adapter.getAdapterType()));
					}
	
					ComboBox<BaseModelData> targetNWComboBox = configModel.getAvaliableConfigInfoComboBox();
					ListStore<BaseModelData> targetNWStore = targetNWComboBox.getStore();
					targetNWStore.removeAll();
					targetNWStore.add(vAppNetworkModelList);
					if (targetNWStore.getCount() > 0) {
						targetNWComboBox.setValue(getMatchedNetwork(configModel));
					}
				}
			} else {
				VMNetworkConfigInfoModel firstAdapter = adapterList.get(0);
				
				VMNetworkConfigInfoModel totalAdapter = new VMNetworkConfigInfoModel();
				totalAdapter.setAdapterType(firstAdapter.getAdapterType());
				totalAdapter.setParentId(firstAdapter.getParentId());
				totalAdapter.setParentName(firstAdapter.getParentName());
				totalAdapter.setLabel(UIContext.Messages.vAppRestoreVMAllAdapters(adapterList.size()));
				VMNetworkConfigGridModel configModel = new VMNetworkConfigGridModel(totalAdapter);
				adapterNetworkStore.add(configModel);

				ComboBox<BaseModelData> adapterTypeComboBox = configModel.getAdapterTypeConfigComboBox();
				ListStore<BaseModelData> adapterTypeStore = adapterTypeComboBox.getStore();
				adapterTypeStore.removeAll();
				adapterTypeStore.add(adapterTypeModelList);
				if (adapterTypeStore.getCount() > 0) {
					adapterTypeComboBox.setValue(getMatchedAdapterType(totalAdapter.getAdapterType()));
				}

				ComboBox<BaseModelData> targetNWComboBox = configModel.getAvaliableConfigInfoComboBox();
				ListStore<BaseModelData> targetNWStore = targetNWComboBox.getStore();
				targetNWStore.removeAll();
				targetNWStore.add(vAppNetworkModelList);
				if (targetNWStore.getCount() > 0) {
					targetNWComboBox.setValue(getMatchedNetwork(configModel));
				}
			}
		}
	}
	
	private void addAdapterNetworkConfigGrid() {
		List<BaseModelData> adapterTypeModelList = new ArrayList<BaseModelData>();
		for (String type : adapterTypeList) {
			BaseModelData typeModel = new BaseModelData();
			typeModel.set("displayName", type);
			adapterTypeModelList.add(typeModel);
		}
		
		Collections.sort(adapterList, nwInfoModelComparator);
		int index = 0;
		String name = null;
		VMNetworkConfigInfoModel newAdapter = new VMNetworkConfigInfoModel();
		for (VMNetworkConfigInfoModel infoModel : adapterList) {
			name = UIContext.Messages.vAppRestoreVMAdapterName(index++);
			newAdapter.setLabel(name);
			int findIndex = Collections.binarySearch(adapterList, newAdapter, nwInfoModelComparator);
			if (findIndex >= 0) {
				name = null;
				continue;
			} else {
				break;
			}
		}
		if (name == null) {
			name = UIContext.Messages.vAppRestoreVMAdapterName(adapterList.size());
		}
		newAdapter.setLabel(name);
		adapterList.add(newAdapter);

		if (eachAdapterSpecifyingCheckBox.getValue()) {
				VMNetworkConfigGridModel configModel = new VMNetworkConfigGridModel(newAdapter);
				adapterNetworkStore.add(configModel);

				ComboBox<BaseModelData> adapterTypeComboBox = configModel.getAdapterTypeConfigComboBox();
				ListStore<BaseModelData> adapterTypeStore = adapterTypeComboBox.getStore();
				adapterTypeStore.removeAll();
				adapterTypeStore.add(adapterTypeModelList);

				ComboBox<BaseModelData> targetNWComboBox = configModel.getAvaliableConfigInfoComboBox();
				ListStore<BaseModelData> targetNWStore = targetNWComboBox.getStore();
				targetNWStore.removeAll();
				targetNWStore.add(vAppNetworkModelList);
		} else {
			if (adapterNetworkStore.getCount() > 0) {
				VMNetworkConfigInfoModel firstAdapter = adapterNetworkStore.getAt(0).getInfoModel();
				newAdapter.setAdapterType(firstAdapter.getAdapterType());
				newAdapter.setParentId(firstAdapter.getParentId());
				newAdapter.setParentName(firstAdapter.getParentName());
				
				VMNetworkConfigInfoModel totalAdapter = new VMNetworkConfigInfoModel();
				totalAdapter.setAdapterType(firstAdapter.getAdapterType());
				totalAdapter.setParentId(firstAdapter.getParentId());
				totalAdapter.setParentName(firstAdapter.getParentName());
				totalAdapter.setLabel(UIContext.Messages.vAppRestoreVMAllAdapters(adapterList.size()));
				VMNetworkConfigGridModel configModel = new VMNetworkConfigGridModel(totalAdapter);
				adapterNetworkStore.removeAll();
				adapterNetworkStore.add(configModel);
				
				ComboBox<BaseModelData> adapterTypeComboBox = configModel.getAdapterTypeConfigComboBox();
				ListStore<BaseModelData> adapterTypeStore = adapterTypeComboBox.getStore();
				adapterTypeStore.removeAll();
				adapterTypeStore.add(adapterTypeModelList);
				if (adapterTypeStore.getCount() > 0) {
					adapterTypeComboBox.setValue(getMatchedAdapterType(totalAdapter.getAdapterType()));
				}

				ComboBox<BaseModelData> targetNWComboBox = configModel.getAvaliableConfigInfoComboBox();
				ListStore<BaseModelData> targetNWStore = targetNWComboBox.getStore();
				targetNWStore.removeAll();
				targetNWStore.add(vAppNetworkModelList);
				if (targetNWStore.getCount() > 0) {
					targetNWComboBox.setValue(getMatchedNetwork(configModel));
				}
			} else {
				VMNetworkConfigInfoModel totalAdapter = new VMNetworkConfigInfoModel();
				totalAdapter.setLabel(UIContext.Messages.vAppRestoreVMAllAdapters(adapterList.size()));
				VMNetworkConfigGridModel configModel = new VMNetworkConfigGridModel(totalAdapter);
				adapterNetworkStore.add(configModel);
	
				ComboBox<BaseModelData> adapterTypeComboBox = configModel.getAdapterTypeConfigComboBox();
				ListStore<BaseModelData> adapterTypeStore = adapterTypeComboBox.getStore();
				adapterTypeStore.removeAll();
				adapterTypeStore.add(adapterTypeModelList);
	
				ComboBox<BaseModelData> targetNWComboBox = configModel.getAvaliableConfigInfoComboBox();
				ListStore<BaseModelData> targetNWStore = targetNWComboBox.getStore();
				targetNWStore.removeAll();
				targetNWStore.add(vAppNetworkModelList);
			}
		}
	}
	
	private void removeAdapterNetworkConfigGrid() {
		VMNetworkConfigGridModel selectedGridModel = adapterNetworkGrid.getSelectionModel().getSelectedItem();
		if (selectedGridModel == null) {
			VAppRecoveryOptionsWizard.showInfoMessage(UIContext.Constants.vAppRestoreVMRemoveAdapterTip());
			return;
		} 
		
		adapterNetworkStore.remove(selectedGridModel);
		if (eachAdapterSpecifyingCheckBox.getValue()) {
			adapterList.remove(selectedGridModel.getInfoModel());
		} else {
			adapterList.clear();
		}
		
		if (adapterList.isEmpty()) {
			removeAdapterBtn.setEnabled(false);
		}
	}
	
	private void getDestinationESXHost(VMStorage vmxDS) {
		masking4GetAdapterTypes();
		destinationESXHost = null;

		VCloudDirectorModel vCloudModel = vAppOptionsWizard.getVAppPanel().getVCloudDirectorModel();
		final VirtualCenterModel vcModel = destinationVDCModel.getVCenters().get(0);

		VCloudDirectorModel vCloudInfo = new VCloudDirectorModel();
		vCloudInfo.setName(vCloudModel.getName());
		vCloudInfo.setUsername(vCloudModel.getUsername());
		vCloudInfo.setPassword(vCloudModel.getPassword());
		vCloudInfo.setPort(vCloudModel.getPort());
		vCloudInfo.setProtocol(vCloudModel.getProtocol());

		VMStorage storage = vmDataStoreComboBox.getValue();
		service.getESXHosts4VAppChildVM(vCloudInfo, vcModel, destinationVDCModel.getId(), storage.getMoRef(),
				new BaseAsyncCallback<List<ESXServerModel>>() {
					@Override
					public void onSuccess(List<ESXServerModel> esxList) {
						if (esxList != null && !esxList.isEmpty()) {
							destinationESXHost = esxList.get(0);
							final ESXServerModel esxModel = destinationESXHost;
							getServerSupportedAdapterTypes(vcModel, esxModel);
						} else {
							freshAdapterNetworkConfigGrid();
							unmasking4GetAdapterTypes(UIContext.Constants.vAppRestoreVMNoAdapterTypes());
							if (isVDCModelSetting) {
								vAppChildVMsummaryCallBack.onSuccess(getVMSummary());
								isVDCModelSetting = false;
							}
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						destinationESXHost = null;
						unmasking4GetAdapterTypes(UIContext.Constants.vAppRestoreVMGetAdapterTypesFail());
						freshAdapterNetworkConfigGrid();
						if (isVDCModelSetting) {
							vAppChildVMsummaryCallBack.onSuccess(getVMSummary());
							isVDCModelSetting = false;
						}
						super.onFailure(caught);
					}
				});
	}
	
	private void getServerSupportedAdapterTypes(VirtualCenterModel vcModel, ESXServerModel esxModel) {
		adapterTypeList.clear();
		service.getVMAdapterTypes(vcModel, esxModel, new BaseAsyncCallback<List<String>>() {
			@Override
			public void onSuccess(List<String> typeList) {
				if (typeList != null && !typeList.isEmpty()) {
					adapterTypeList.addAll(typeList);
				}
				freshAdapterNetworkConfigGrid();
				unmasking4GetAdapterTypes();
				if (isVDCModelSetting) {
					vAppChildVMsummaryCallBack.onSuccess(getVMSummary());
					isVDCModelSetting = false;
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				freshAdapterNetworkConfigGrid();
				unmasking4GetAdapterTypes(UIContext.Constants.vAppRestoreVMNoAdapterTypes());
				if (isVDCModelSetting) {
					vAppChildVMsummaryCallBack.onSuccess(getVMSummary());
					isVDCModelSetting = false;
				}
				super.onFailure(caught);
			}
		});
	}
	
	private void masking4GetAdapterTypes() {
		parentWizard.disableNavigatorAndSwitch();
		profileComboBox.disable();
		vmDataStoreComboBox.disable();
		addAdapteBtn.disable();
		removeAdapterBtn.disable();
		adapterNetworkGrid.mask(UIContext.Constants.vAppRestoreLoading());
	}
	
	private void unmasking4GetAdapterTypes() {
		unmasking4GetAdapterTypes(null);
	}
	
	private void unmasking4GetAdapterTypes(String message) {
		if (message != null) {
			adapterNetworkGrid.mask(message);
		} else {
			adapterNetworkGrid.unmask();
		}
		addAdapteBtn.enable();
		removeAdapterBtn.disable();
		vmDataStoreComboBox.enable();
		profileComboBox.enable();
		parentWizard.enableNavigatorAndSwitch();
	}
	
	private BackupVMModel getVMSummary() {
		BackupVMModel vmSummary = new BackupVMModel();
		vmSummary.setVMName(getVMName());
		vmSummary.setVmInstanceUUID(backupVMModel.getVmInstanceUUID()); 
		
		if (cpuCountComboxBox.getValue() != null) {
			vmSummary.setCPUCount(cpuCountComboxBox.getValue().getValue());
		}
		if (memorySizeTextField.getValue() != null) {
			vmSummary.setMemorySize(memorySizeTextField.getValue());
		}

		VCloudStorageProfileModel profile = profileComboBox.getValue();
		if (profile != null) {
			vmSummary.setStorageProfileName(profile.getName());
			vmSummary.setStorageProfileId(profile.getId());
		}

		if (vmDataStoreComboBox.getValue() != null) {
			VMStorage vmxStorage = (VMStorage) vmDataStoreComboBox.getValue();
			vmSummary.setVMXDataStoreName(vmxStorage.getName());
			vmSummary.setVMXDataStoreId(vmxStorage.getId());
		}

		vmSummary.adapterList = getNetworkConfig4RestoreModel();
		return vmSummary;
	}

	public void validate(AsyncCallback<Boolean> outterCallback) {
		String errMsg = UIContext.Constants.vAppRestoreRequiredFieldMark();
		
		if (!Utils.isEmptyOrNull(vmNameComboBox.getRawValue())
				|| (vmNameComboBox.getValue() != null && !Utils.isEmptyOrNull(vmNameComboBox.getValue().getValue()))) {
			vmNameComboBox.clearInvalid();
		} else {
			vmNameComboBox.markInvalid(errMsg);
			outterCallback.onSuccess(Boolean.FALSE);
			vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
			return;
		}

		if (cpuCountComboxBox.getValue() != null && cpuCountComboxBox.getValue().getValue() > 0) {
			cpuCountComboxBox.clearInvalid();
		} else {
			cpuCountComboxBox.markInvalid(errMsg);
			outterCallback.onSuccess(Boolean.FALSE);
			vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
			return;
		}

		if (memorySizeTextField.getValue() != null) {
			memorySizeTextField.clearInvalid();
		} else {
			memorySizeTextField.markInvalid(errMsg);
			outterCallback.onSuccess(Boolean.FALSE);
			vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
			return;
		}

		if (profileComboBox.getValue() != null && !Utils.isEmptyOrNull(profileComboBox.getValue().getName())) {
			profileComboBox.clearInvalid();
		} else {
			profileComboBox.markInvalid(errMsg);
			outterCallback.onSuccess(Boolean.FALSE);
			vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
			return;
		}

		if (vmDataStoreComboBox.getValue() != null && !Utils.isEmptyOrNull(vmDataStoreComboBox.getValue().getName())) {
			vmDataStoreComboBox.clearInvalid();
		} else {
			vmDataStoreComboBox.markInvalid(errMsg);
			outterCallback.onSuccess(Boolean.FALSE);
			vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
			return;
		}
		
		if (destinationESXHost != null && !Utils.isEmptyOrNull(destinationESXHost.getESXName())) {
			vmDataStoreComboBox.clearInvalid();
		} else {
			vmDataStoreComboBox.markInvalid(UIContext.Constants.vAppRestoreVMNoServerOnDataStoreError());
			outterCallback.onSuccess(Boolean.FALSE);
			vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
			return;
		}

		checkDiskTypeConfig(outterCallback);
	}

	private void checkDiskTypeConfig(AsyncCallback<Boolean> outterCallback) {
		List<DataStoreModel> diskDSList = diskDatastoreListStore.getModels();
		if (diskDSList == null || diskDSList.isEmpty()) {
			checkDatastoreSize(outterCallback);
		} else {
			for (DataStoreModel dsModel : diskDSList) {
				ComboBox<ModelData> diskTypeComboBox = dsModel.getDiskTypeComboBox();
				if (diskTypeComboBox.getValue() != null) {
					diskTypeComboBox.clearInvalid();
				} else {
					diskTypeComboBox.markInvalid(UIContext.Constants.vAppRestoreRequiredFieldMark());
					outterCallback.onSuccess(Boolean.FALSE);
					vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
					return;
				}
			}

			checkDatastoreSize(outterCallback);
		}
	}

	private void checkDatastoreSize(AsyncCallback<Boolean> outterCallback) {
		VMStorage storage = vmDataStoreComboBox.getValue();
		long freeSize = storage.getFreeSize();
		long totalDiskSize = getTotalDiskSize();
		if (freeSize < totalDiskSize) {
			showInsufficientDatastoreMessage(storage.getName(), freeSize, totalDiskSize, outterCallback);
		} else {
			checkProfileLimitedSizeAfterRequested(outterCallback);
		}
	}
	
	private void checkProfileLimitedSizeAfterRequested(AsyncCallback<Boolean> outterCallback) {
		VCloudStorageProfileModel profile = profileComboBox.getValue();
		long freeSize = profile.getFreeSize();
		long totalDiskSize = getTotalDiskSize();
		if (freeSize < totalDiskSize) {
			showInsufficientProfileMessage(profile.getName(), freeSize, totalDiskSize, outterCallback);
		} else {
			checkMemorySize(outterCallback);
		}
	}
	
	private void checkMemorySize(AsyncCallback<Boolean> outterCallback) {
		String errMsg = null;
		if (memorySizeTextField.getValue() == null) { 
			errMsg = UIContext.Constants.vAppRestoreRequiredFieldMark();
		} else {
			Long size = memorySizeTextField.getValue();
			if (size == null) {
				errMsg = UIContext.Constants.vAppRestoreRequiredFieldMark();
			} else if (size <= 0) {
				errMsg = UIContext.Constants.vAppRestoreMemorySize0MBLarge();
			} else if (size > memoryAllowedMax) {
				errMsg = UIContext.Messages.vAppRestoreMemorySizeLargerMax(memoryAllowedMax, size);
			} else if (size % 4 != 0){
				errMsg = UIContext.Constants.vAppRestoreMemorySize4MBMultiple();
			} else {
				errMsg = null;
			}
		}
		if (errMsg != null) {
			memorySizeTextField.markInvalid(errMsg);
			outterCallback.onSuccess(Boolean.FALSE);
			vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
			return;
		}
		
		if (VAppRecoveryOptionsWizard.VDCMemoryCPUAllocationModel.AllocationVApp.getDescription().equalsIgnoreCase(
				destinationVDCModel.getAllocationModel())) {
			long size = memorySizeTextField.getValue() * 1024 * 1024;
			long limitedSize = destinationVDCModel.getMemoryLimit();
			if (size > limitedSize) {
				memorySizeTextField.markInvalid(UIContext.Messages
						.vAppRestoreMemorySizeLargeVDCLimit(destinationVDCModel.getName(), limitedSize, size));
				outterCallback.onSuccess(Boolean.FALSE);
				vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
			} else {
				memorySizeTextField.clearInvalid();
				checkNetWorkConfiguration(outterCallback);
			}
		} else {
			memorySizeTextField.clearInvalid();
			checkNetWorkConfiguration(outterCallback);
		}
	}

	private void showInsufficientDatastoreMessage(String datastoreName, long freeSize, long totalDiskSize, final AsyncCallback<Boolean> outCallback) {
		String msg = UIContext.Messages.vAppRestoreVMDataStoreNotEnough(datastoreName, Utils.bytes2String(freeSize),
				Utils.bytes2String(totalDiskSize), nodeName);
		MessageBox mb = new MessageBox();
		mb.setIcon(MessageBox.WARNING);
		mb.setButtons(MessageBox.YESNO);
		mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(VAppRecoveryOptionsWizard.getProductName()));
		mb.setMessage(msg);
		mb.addCallback(new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					checkProfileLimitedSizeAfterRequested(outCallback);
				} else {
					outCallback.onSuccess(false);
					vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
				}
			}
		});
		mb.show();
	}
	
	private void showInsufficientProfileMessage(String profileName, long freeSize, long totalDiskSize, final AsyncCallback<Boolean> outCallback) {
		String msg = UIContext.Messages.vAppRestoreVMPolicyNotEnough(profileName, Utils.bytes2String(freeSize),
				Utils.bytes2String(totalDiskSize), nodeName);
		MessageBox mb = new MessageBox();
		mb.setIcon(MessageBox.WARNING);
		mb.setButtons(MessageBox.YESNO);
		mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(VAppRecoveryOptionsWizard.getProductName()));
		mb.setMessage(msg);
		mb.addCallback(new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					checkNetWorkConfiguration(outCallback);
				} else {
					outCallback.onSuccess(false);
					vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
				}
			}
		});
		mb.show();
	}

	private void checkNetWorkConfiguration(final AsyncCallback<Boolean> outCallback) {
		List<VMNetworkConfigGridModel> gridModels = adapterNetworkStore.getModels();
		if (gridModels == null || gridModels.isEmpty()) {
			checkVMRecoveryPointESXUefi(outCallback);
		}

		boolean isNetWorkConfiged = true;
		String adpaterNames = "";
		for (VMNetworkConfigGridModel model : gridModels) {
			VMNetworkConfigInfoModel selectedCell = (VMNetworkConfigInfoModel) model.getAvaliableConfigInfoComboBox().getValue();
			if (selectedCell == null) {
				isNetWorkConfiged = false;
				adpaterNames += model.getInfoModel().getLabel() + ",";
			}
		}

		if (isNetWorkConfiged) {
			checkVMRecoveryPointESXUefi(outCallback);
		} else {
			adpaterNames = adpaterNames.substring(0, adpaterNames.length() - 1);
			String msg = UIContext.Messages.vAppRestoreVMNetworkNotConfig(adpaterNames, nodeName);
			MessageBox mb = new MessageBox();
			mb.setModal(true);
			mb.setIcon(MessageBox.WARNING);
			mb.setButtons(MessageBox.YESNO);
			mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(VAppRecoveryOptionsWizard.getProductName()));
			mb.setMessage(msg);
			Utils.setMessageBoxDebugId(mb);
			mb.addCallback(new Listener<MessageBoxEvent>() {
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
						checkVMRecoveryPointESXUefi(outCallback);
					} else {
						outCallback.onSuccess(Boolean.FALSE);
						vAppOptionsWizard.activeChildVMWizard(backupVMModel.getVmInstanceUUID());
					}
				}
			});
			mb.show();
		}
	}

	private void checkVMRecoveryPointESXUefi(final AsyncCallback<Boolean> callback) {
		VirtualCenterModel vcModel = destinationVDCModel.getVCenters().get(0);
		
		ESXServerModel esxServerModel = new ESXServerModel();
		esxServerModel.setESXName(destinationESXHost.getESXName());

		String dest = backupVMModel.getBrowseDestination();
		RestoreWizardContainer restoreContainer = vAppOptionsWizard.getRestoreWizardContainer();
		String userName = restoreContainer.vmrecvPointPanel.getUserName();
		String password = restoreContainer.vmrecvPointPanel.getPassword();
		String subPath = restoreContainer.vmrecvPointPanel.getSelectedRecoveryPoint().childVMRecoveryPointModelMap.get(
				backupVMModel.getVmInstanceUUID()).getPath();
		String domain = "";
		if (userName != null) {
			int index = userName.indexOf('\\');
			if (index > 0) {
				domain = userName.substring(0, index);
				userName = userName.substring(index + 1);
			}
		}
		service.checkVMRecoveryPointESXUefi(vcModel, esxServerModel, dest, domain, userName, password, subPath,
				new BaseAsyncCallback<Integer>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						callback.onSuccess(Boolean.FALSE);
					}

					@Override
					public void onSuccess(Integer result) {
						if (result == 0) {
							checkRecoveryVMJobExist(destinationESXHost.getESXName(), callback);
						} else {
							VAppRecoveryOptionsWizard.showErrorMessage(callback, UIContext.Constants.vmRecoveryESXDoesnotSupportUEFI());
						}
					}
				});
	}

	private void checkRecoveryVMJobExist(String esxServerName, final AsyncCallback<Boolean> callback) {
		service.checkRecoveryVMJobExist(getVMName(), esxServerName, new BaseAsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				callback.onSuccess(Boolean.FALSE);
			}

			@Override
			public void onSuccess(Boolean isExist) {
				if (isExist != null && !isExist) {
					callback.onSuccess(Boolean.TRUE);
				} else {
					VAppRecoveryOptionsWizard.showErrorMessage(callback, UIContext.Constants.vmRecoveryCannotRun());
				}
			}
		});
	}

	public RestoreJobModel processOptions() {
		RestoreJobModel restoreModel = new RestoreJobModel();
		RecoverVMOptionModel recoverVMOption = new RecoverVMOptionModel();
		restoreModel.recoverVMOption = recoverVMOption;

		String instanceUuid = backupVMModel.getVmInstanceUUID();

		restoreModel.setJobType(1);
		restoreModel.setVMInstanceUUID(instanceUuid);
		restoreModel.setDestinationPath(backupVMModel.getBrowseDestination());

		Map<String, RecoveryPointModel> childRPMap = RestoreContext.getRecoveryPointModel().childVMRecoveryPointModelMap;
		recoverVMOption.setSessionNumber(childRPMap.get(instanceUuid).getSessionID());

		List<VirtualCenterModel> vcList = destinationVDCModel.getVCenters();
		VirtualCenterModel vCenterModel = vcList.get(0);
		VirtualCenterModel vcModel = new VirtualCenterModel();
		vcModel.setVcName(vCenterModel.getVcName());
		vcModel.setUsername(vCenterModel.getUsername());
		vcModel.setPassword(vCenterModel.getPassword());
		vcModel.setProtocol(vCenterModel.getProtocol());
		vcModel.setPort(vCenterModel.getPort());
		recoverVMOption.setVCModel(vcModel);

		recoverVMOption.setVcName(vCenterModel.getVcName()); // VC name
		recoverVMOption.setESXServerName(destinationESXHost.getESXName()); // ESX Server Name
		recoverVMOption.setVirtualDataCenterName(destinationVDCModel.getName());
		recoverVMOption.setVMName(getVMName()); // VM Name
		recoverVMOption.setVMUUID(backupVMModel.getUUID()); // VM UUID
		recoverVMOption.setVMInstanceUUID(backupVMModel.getVmInstanceUUID()); 
		recoverVMOption.setCPUCount(cpuCountComboxBox.getSimpleValue());
		recoverVMOption.setMemorySize(memorySizeTextField.getValue());
		recoverVMOption.setVmIdInVApp(backupVMModel.getVmIdInVApp()); // VM UUID in VApp

		VCloudStorageProfileModel profile = profileComboBox.getValue();
		recoverVMOption.setStorageProfileName(profile.getName());
		recoverVMOption.setStorageProfileId(profile.getId());

		VMStorage vmxStorage = (VMStorage) vmDataStoreComboBox.getValue();
		recoverVMOption.setVMDataStore(vmxStorage.getName());
		recoverVMOption.setVMDataStoreId(vmxStorage.getId());

		// VM disk data stores
		List<DiskDataStoreModel> diskConfList = getDiskConfig4RestoreModel();
		recoverVMOption.setVmDiskCount(diskConfList.size());
		recoverVMOption.setDiskDataStore(diskConfList);

		// VM network
		recoverVMOption.setNetworkConfigInfo(getNetworkConfig4RestoreModel());

		return restoreModel;
	}

	private List<VMNetworkConfigInfoModel> getNetworkConfig4RestoreModel() {
		List<VMNetworkConfigInfoModel> infoModelList = new ArrayList<VMNetworkConfigInfoModel>();
		if (adapterList.isEmpty() || adapterNetworkStore.getCount() == 0) {
			return infoModelList;
		}

		boolean is1By1 = eachAdapterSpecifyingCheckBox.getValue();
		if (is1By1) {
			List<VMNetworkConfigGridModel> adapterNetworkModelList = adapterNetworkStore.getModels();
			for (VMNetworkConfigGridModel gridModel : adapterNetworkModelList) {
				VMNetworkConfigInfoModel adapter = gridModel.getInfoModel();
				VMNetworkConfigInfoModel selectedCellModel = (VMNetworkConfigInfoModel) gridModel
						.getAvaliableConfigInfoComboBox().getValue();

				VMNetworkConfigInfoModel infoModel = new VMNetworkConfigInfoModel();
				infoModelList.add(infoModel);

				infoModel.setNetworkId(adapter.getNetworkId());
				infoModel.setLabel(adapter.getLabel());
				if (selectedCellModel != null) {
					infoModel.setParentId(selectedCellModel.getNetworkId());
					infoModel.setParentName(selectedCellModel.getLabel());
				}
			}
		} else {
			VMNetworkConfigGridModel gridModel = adapterNetworkStore.getModels().get(0);
			for (VMNetworkConfigInfoModel adapter : adapterList) {
				VMNetworkConfigInfoModel selectedCellModel = (VMNetworkConfigInfoModel) gridModel
						.getAvaliableConfigInfoComboBox().getValue();

				VMNetworkConfigInfoModel infoModel = new VMNetworkConfigInfoModel();
				infoModelList.add(infoModel);

				infoModel.setNetworkId(adapter.getNetworkId());
				infoModel.setLabel(adapter.getLabel());
				if (selectedCellModel != null) {
					infoModel.setParentId(selectedCellModel.getNetworkId());
					infoModel.setParentName(selectedCellModel.getLabel());
				}
			}
		}

		return infoModelList;
	}

	private List<DiskDataStoreModel> getDiskConfig4RestoreModel() {
		List<DiskDataStoreModel> diskDataStoreList = new ArrayList<DiskDataStoreModel>();

		if (backupVMModel.diskList == null || backupVMModel.diskList.isEmpty()
				|| diskDatastoreListStore.getCount() <= 0) {
			return diskDataStoreList;
		}

		boolean is1By1 = eachDiskSpecifyingCheckBox.getValue();
		if (is1By1) {
			List<DataStoreModel> storeList = diskDatastoreListStore.getModels();
			for (DataStoreModel dsModel : storeList) {
				DiskDataStoreModel diskDataStoreModel = new DiskDataStoreModel();

				VMStorage datastore = (VMStorage) vmDataStoreComboBox.getValue();
				ModelData diskType = (ModelData) dsModel.getDiskTypeComboBox().getValue();
				DiskModel disk = dsModel.getDiskModel();

				diskDataStoreModel.setDisk(disk.getDiskUrl());
				diskDataStoreModel.setDiskName(UIContext.Constants.vmRecoveryDisk() + disk.getDiskNumber());
				diskDataStoreModel.setDiskType((Long) diskType.get("id"));
				diskDataStoreModel.setDataStore(datastore.getName());
				diskDataStoreModel.setDataStoreId(datastore.getId());
				String volumes = "";
				if (dsModel.getDiskModel().volumeModelList != null && dsModel.getDiskModel().volumeModelList.size() > 0) {
					for (VMVolumeModel volumeModel : disk.volumeModelList) {
						volumes += volumeModel.getDriveLetter();
					}
				}
				diskDataStoreModel.setVolumeName(volumes);
				diskDataStoreModel.set("diskSize", Utils.bytes2String(disk.getSize()));

				diskDataStoreList.add(diskDataStoreModel);
			}
		} else {
			DataStoreModel dsModel = diskDatastoreListStore.getModels().get(0);
			for (DiskModel disk : backupVMModel.diskList) {
				DiskDataStoreModel diskDataStoreModel = new DiskDataStoreModel();

				VMStorage datastore = (VMStorage) vmDataStoreComboBox.getValue();
				ModelData diskType = (ModelData) dsModel.getDiskTypeComboBox().getValue();

				diskDataStoreModel.setDisk(disk.getDiskUrl());
				diskDataStoreModel.setDiskName(UIContext.Constants.vmRecoveryDisk() + disk.getDiskNumber());
				diskDataStoreModel.setDiskType((Long) diskType.get("id"));
				diskDataStoreModel.setDataStore(datastore.getName());
				diskDataStoreModel.setDataStoreId(datastore.getId());
				String volumes = "";
				if (dsModel.getDiskModel().volumeModelList != null && dsModel.getDiskModel().volumeModelList.size() > 0) {
					for (VMVolumeModel volumeModel : disk.volumeModelList) {
						volumes += volumeModel.getDriveLetter();
					}
				}
				diskDataStoreModel.setVolumeName(volumes);
				diskDataStoreModel.set("diskSize", Utils.bytes2String(disk.getSize()));

				diskDataStoreList.add(diskDataStoreModel);
			}
		}

		return diskDataStoreList;
	}
	
	private Integer getMatchedCpuCount(Integer countInSession, int maxCout) {
		if (countInSession != null && countInSession >= 0 && countInSession <= maxCout) {
			return countInSession;
		}
		
		return null;
	}
	
	private Long getMatchedMemorySize(Long sizeInSession) {
		if (sizeInSession != null && sizeInSession >=0 && sizeInSession <= memoryAllowedMax) {
			return sizeInSession;
		}
		return null;
	}
	
	private VCloudStorageProfileModel getMatchedStoragePofile(String idInsession, String nameInSession, List<VCloudStorageProfileModel> vDCProfileList) {
		if (idInsession == null && nameInSession == null) {
			return null;
		}
		
		VCloudStorageProfileModel matchedByIdProfile = null;
		VCloudStorageProfileModel matchedByNameProfile = null;
		for (VCloudStorageProfileModel model : vDCProfileList) {
			if (matchedByIdProfile != null && matchedByNameProfile != null) {
				break;
			}
			
			if (matchedByIdProfile == null && idInsession != null) {
				if (idInsession.equalsIgnoreCase(model.getId())) {
					matchedByIdProfile = model;
				}
			}
			if (matchedByNameProfile == null && nameInSession != null) {
				if (nameInSession.equalsIgnoreCase(model.getName())) {
					matchedByNameProfile = model;
				}
			}
		}
		
		if (matchedByIdProfile != null) {
			return matchedByIdProfile;
		} else if (matchedByNameProfile != null) {
			return matchedByNameProfile;
		} else {
			return null;
		}
	}
	
	private VMStorage getMatchedDatastore(String idInsession, String nameInSession, List<VMStorage> profileStorageList) {
		if (idInsession == null && nameInSession == null) {
			return null;
		}
		
		VMStorage matchedByIdStorage = null;
		VMStorage matchedByNameStorage = null;
		for (VMStorage storage : profileStorageList) {
			if (matchedByIdStorage != null && matchedByNameStorage != null) {
				break;
			}
			
			if (matchedByIdStorage == null && idInsession != null) {
				if (idInsession.equalsIgnoreCase(storage.getId())) {
					matchedByIdStorage = storage;
				}
			}
			if (matchedByNameStorage == null && nameInSession != null) {
				if (nameInSession.equalsIgnoreCase(storage.getName())) {
					matchedByNameStorage = storage;
				}
			}
		}
		
		if (matchedByIdStorage != null) {
			return matchedByIdStorage;
		} else if (matchedByNameStorage != null) {
			return matchedByNameStorage;
		} else {
			return null;
		}
	}
	
	private ModelData getMatchedDiskType(Long typeInsession) {
		if (typeInsession instanceof Long) {
			int size = diskTypeModelList.size();
			int index = typeInsession.intValue();
			if (index >= 0 && index < size) {
				return diskTypeModelList.get(index);
			}
		}
		return null;
	}
	
	private BaseModelData getMatchedAdapterType(String typeInsession) {
		if (adapterTypeList.contains(typeInsession)) {
			BaseModelData typeModel = new BaseModelData();
			typeModel.set("displayName", typeInsession);
			return typeModel;
		}
		return null;
	}
	
	private VMNetworkConfigInfoModel getMatchedNetwork(VMNetworkConfigGridModel model) {
		VMNetworkConfigInfoModel adapter = model.getInfoModel();
		if (adapter == null) {
			return null;
		}
		
		ComboBox<BaseModelData> targetComboBox = model.getAvaliableConfigInfoComboBox();
		if (targetComboBox == null) {
			return null;
		}
		
		ListStore<BaseModelData> store = targetComboBox.getStore();
		if (store == null) {
			return null;
		}
		List<BaseModelData> selectionList = store.getModels();
		if (selectionList == null || selectionList.size() == 0) {
			return null;
		}
		
		VMNetworkConfigInfoModel matchedById = null;
		VMNetworkConfigInfoModel matchedByName = null;
		String adapterId = adapter.getParentId();
		String adapterName = adapter.getParentName();
		for (BaseModelData data : selectionList) {
			if (data instanceof VMNetworkConfigInfoModel) {
				VMNetworkConfigInfoModel configData = (VMNetworkConfigInfoModel) data;
				if (matchedById == null && adapterId != null) {
					if (adapterId.equalsIgnoreCase(configData.getNetworkId())) {
						matchedById = configData;
					}
				}
				
				if (matchedByName == null && adapterName != null) {
					if (adapterName.equals(configData.getLabel())) {
						matchedByName = configData;
					}
				}
				
				if (matchedById != null && matchedByName != null) {
					break;
				}
			} else {
				continue;
			}
		}
		
		if (matchedById != null) {
			return matchedById;
		}
		
		if (matchedByName != null) {
			return matchedByName;
		}
		
		return null;
	}
	
	private static class VMNetworkConfigInfoModelComparator implements Comparator<VMNetworkConfigInfoModel> {
		@Override
		public int compare(VMNetworkConfigInfoModel o1, VMNetworkConfigInfoModel o2) {
			if (o1 == null || o1.getLabel() == null) {
				return -1;
			} 
			
			if (o2 == null || o2.getLabel() == null) {
				return 1;
			}
			
			return o1.getLabel().compareTo(o2.getLabel());
		}
	}
}