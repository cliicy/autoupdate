package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.DiskModel;
import com.ca.arcflash.ui.client.model.RecoverVMOptionModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.VCloudDirectorModel;
import com.ca.arcflash.ui.client.model.VCloudStorageProfileModel;
import com.ca.arcflash.ui.client.model.VCloudVirtualDataCenterModel;
import com.ca.arcflash.ui.client.model.VMNetworkConfigGridModel;
import com.ca.arcflash.ui.client.model.VMNetworkConfigInfoModel;
import com.ca.arcflash.ui.client.model.VMStorage;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.restore.PasswordPane;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.QuickTip;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class VAppRecoveryOptionsPanel extends LayoutContainer {
	private static LoginServiceAsync service = GWT.create(LoginService.class);
	private static ChildVMSummaryStoreSorter childVMStoreSorter = new ChildVMSummaryStoreSorter();
	private VAppRecoveryOptionsWizard parentWizard;
	private VAppRecoveryOptionsPanel thisPanel;
	private FlexTable mainFlexTable = new FlexTable();

	private SimpleComboBox<String> vAppNameComboBox;
	private TextField<String> destinationVDCField;
	private Button destSpecifyButton;
	private SpecifyDestinationDialog specifyDestinationDialog;

	private ListStore<VMNetworkConfigGridModel> adapterNetworkStore = new ListStore<VMNetworkConfigGridModel>();
	private Grid<VMNetworkConfigGridModel> adapterNetworkGrid;

	private ListStore<BackupVMModel> childVMSummaryStore;
	private Grid<BackupVMModel> childVMSummaryGrid;

	private CheckBox overwriteCheckBox;
	private CheckBox powerOnVAppCheckBox;

	private BackupVMModel vAppBackupVMModel;
	private VCloudDirectorModel vCloudDirectorModel;
	private VCloudVirtualDataCenterModel vCloudVDCModel;

	public VAppRecoveryOptionsPanel(VAppRecoveryOptionsWizard parentWizard) {
		this.parentWizard = parentWizard;
		this.thisPanel = this;
		this.setScrollMode(Scroll.NONE);
		this.setSize("100%", "100%");
		this.setLayout(new RowLayout());

		this.childVMSummaryStore = parentWizard.getChildBackupVMStore();
		this.mainFlexTable.setCellPadding(5);
		this.mainFlexTable.setCellSpacing(5);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.vAppBackupVMModel = RestoreContext.getBackupVMModel();
		sortChildVMs();
		setNextButtonStatus(false);

		this.add(createHeader());
		this.add(createBasicConfigTable());
		this.add(createAdapterTable());
		this.add(createChildVMConfigTable());
		this.add(createFootConfigTable());

		freshBasicConfigTable();
		freshAdapterConfigGrid();
		initChildVMSummaryStore();
	}

	@Override
	public void repaint() {
		super.repaint();
		
		BackupVMModel originalVMModel = this.vAppBackupVMModel;
		this.vAppBackupVMModel = RestoreContext.getBackupVMModel();
		sortChildVMs();

		if (originalVMModel != this.vAppBackupVMModel) {
			freshBasicConfigTable();
			freshFootConfigs();
			if (vCloudVDCModel != null) {
				setSelectedVDC(vCloudVDCModel, true);
			} else {
				setNextButtonStatus(false);
				freshAdapterConfigGrid();
				freshChildVMConfigGrid(false);
				adapterNetworkGrid.mask();
				childVMSummaryGrid.mask();
			}
		} else if (childVMSummaryGrid.isMasked()) {
			setNextButtonStatus(false);
		}
	}

	public VCloudDirectorModel getVCloudDirectorModel() {
		return vCloudDirectorModel;
	}

	public ListStore<VMNetworkConfigGridModel> getAdapterNetworkStore() {
		return adapterNetworkStore;
	}
	
	private void sortChildVMs() {
		if (vAppBackupVMModel == null || vAppBackupVMModel.memberVMList == null
				|| vAppBackupVMModel.memberVMList.isEmpty()) {
			return;
		}
		
		Collections.sort(vAppBackupVMModel.memberVMList, new Comparator<BackupVMModel>() {
			@Override
			public int compare(BackupVMModel vm1, BackupVMModel vm2) {
				if (vm1 == null && vm2 == null) {
					return 0;
				}
				if (vm1 == null) {
					return -1;
				}
				if (vm2 == null) {
					return 1;
				}
				
				String nodeName1 = VAppRecoveryOptionsWizard.getNodeName(vm1);
				String nodeName2 = VAppRecoveryOptionsWizard.getNodeName(vm2);
				if (nodeName1 == null && nodeName2 == null) {
					return 0;
				}
				if (nodeName1 == null) {
					return -1;
				}
				if (nodeName2 == null) {
					return 1;
				}

				return nodeName1.compareToIgnoreCase(nodeName2);
			}
		});
	}

	private Widget createHeader() {
		LayoutContainer container = new LayoutContainer();
		FlexTable headerTable = new FlexTable();
		container.add(headerTable);

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_options()).createImage();
		headerTable.setWidget(0, 0, image);

		headerTable.getFlexCellFormatter().setColSpan(0, 1, 6);
		LabelField label = new LabelField();
		label.ensureDebugId("47916ad7-1bb2-478b-b07b-c8401610fea7");
		label.setValue(UIContext.Constants.restoreOptions());
		label.setStyleName("restoreWizardTitle");
		headerTable.setWidget(0, 1, label);

		return container;
	}

	private Widget createBasicConfigTable() {
		LabelField sectionLabel = new LabelField(UIContext.Constants.vAppRestoreBasicSettingsLabel());
		sectionLabel.addStyleName("restoreWizardSubItem");
		mainFlexTable.setWidget(0, 0, sectionLabel);

		mainFlexTable.setWidget(1, 0, new LabelField(UIContext.Constants.vAppRestoreVAppNameLabel()));
		FlexTable table = new FlexTable();
		mainFlexTable.setWidget(1, 3, table);
		vAppNameComboBox = new SimpleComboBox<String>();
		vAppNameComboBox.ensureDebugId("cc186e9b-b3c6-4964-8819-35e444ed4175");
		vAppNameComboBox.setAllowBlank(false);
		vAppNameComboBox.setValidateOnBlur(false);
		vAppNameComboBox.setTriggerAction(TriggerAction.ALL);
		vAppNameComboBox.setEditable(true);
		vAppNameComboBox.setWidth(250);
		vAppNameComboBox.setTemplate(VAppRecoveryOptionsWizard.getComboBoxTemplate("value"));
		table.setWidget(0, 0, vAppNameComboBox);

		mainFlexTable.setWidget(2, 0, new LabelField(UIContext.Constants.vAppRestoreOrganizationVDCLabel()));
		FlexTable desctinationLine = new FlexTable();
		mainFlexTable.setWidget(2, 3, desctinationLine);
		
		destinationVDCField = new TextField<String>();
		destinationVDCField.ensureDebugId("7c0f503a-3978-4002-9ba4-af5acdd20e2e");
		destinationVDCField.setAllowBlank(false);
		destinationVDCField.setEnabled(false);
		destinationVDCField.setWidth(250);
		destinationVDCField.setEmptyText(UIContext.Constants.vAppRestoreVDCEmptyText());
		desctinationLine.setWidget(0, 0, destinationVDCField);

		LabelField seperator = new LabelField("");
		seperator.ensureDebugId("58a233af-4345-4f10-a3f7-f0b4b4fca499");
		seperator.setWidth(20);
		desctinationLine.setWidget(0, 1, seperator);
		
		destSpecifyButton = new Button();
		destSpecifyButton.ensureDebugId("53e21eb1-f9ca-4498-9a92-8b82045e9b9e");
		destSpecifyButton.setText(UIContext.Constants.vAppRestoreVDCSpecifyBtn());
		desctinationLine.setWidget(0, 2, destSpecifyButton);
		destSpecifyButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				BaseAsyncCallback<Integer> callback = new BaseAsyncCallback<Integer>() {
					@Override
					public void onSuccess(Integer result) {
						if (result != null && result == 0) {
							VCloudVirtualDataCenterModel vDC = vCloudDirectorModel.getTargetVDC();
							if (vDC == null) {
								VAppRecoveryOptionsWizard.showErrorMessage(UIContext.Constants.vAppRestoreNoVDCSepcifiedError());
							} else {
								setSelectedVDC(vDC, false);
							}
						} else {
							if (vCloudVDCModel == null) {
								// ignore
							} else {
								setSelectedVDC(vCloudVDCModel, false);
							}
						}
					}
				};
				if (specifyDestinationDialog == null) {
					specifyDestinationDialog = new SpecifyDestinationDialog(callback, initDirectorModel(), BackupVMModel.Type.VMware_VApp);
				}
				specifyDestinationDialog.show();
			}
		});

		return mainFlexTable;
	}

	private void freshBasicConfigTable() {
		vAppNameComboBox.removeAll();
		vAppNameComboBox.add(vAppBackupVMModel.getVMName());
		vAppNameComboBox.setSimpleValue(vAppBackupVMModel.getVMName());
	}

	private Widget createAdapterTable() {
		mainFlexTable.setWidget(3, 0, new LabelField(UIContext.Constants.vAppRestoreVAppNetworkGridLabel()));
		mainFlexTable.getFlexCellFormatter().setColSpan(4, 0, 4);
		mainFlexTable.setWidget(4, 0, createAdapterNetworkGrid());

		return mainFlexTable;
	}

	private Widget createAdapterNetworkGrid() {
		List<ColumnConfig> columnConfigList = new ArrayList<ColumnConfig>();

		ColumnConfig sourceAdapter = Utils.createColumnConfig("label", UIContext.Constants.vAppRestoreVAppNetworkHeaderNetworkName(),
				335, new GridCellRenderer<VMNetworkConfigGridModel>() {
					@Override
					public Object render(VMNetworkConfigGridModel model, String property, ColumnData config,
							int rowIndex, int colIndex, ListStore<VMNetworkConfigGridModel> store,
							Grid<VMNetworkConfigGridModel> grid) {
						VMNetworkConfigInfoModel infoModel = model.getInfoModel();
						return infoModel.getLabel();
					}
				});
		columnConfigList.add(sourceAdapter);

		ColumnConfig targetNetwork = Utils.createColumnConfig("configComboBox", UIContext.Constants.vAppRestoreVAppNetworkHeaderOrgNetworkName(),
				335, new GridCellRenderer<VMNetworkConfigGridModel>() {
					@Override
					public Object render(VMNetworkConfigGridModel model, String property, ColumnData config,
							int rowIndex, int colIndex, ListStore<VMNetworkConfigGridModel> store,
							Grid<VMNetworkConfigGridModel> grid) {
						ComboBox<BaseModelData> networkConfigComboBox = model.getAvaliableConfigInfoComboBox();
						return networkConfigComboBox;
					}
				});
		columnConfigList.add(targetNetwork);

		adapterNetworkGrid = new Grid<VMNetworkConfigGridModel>(adapterNetworkStore, new ColumnModel(columnConfigList));
		adapterNetworkGrid.setTrackMouseOver(false);
		adapterNetworkGrid.setAutoExpandColumn("configComboBox");
		adapterNetworkGrid.setAutoWidth(true);
		adapterNetworkGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		adapterNetworkGrid.setBorders(true);
		adapterNetworkGrid.setColumnLines(true);
		adapterNetworkGrid.setHeight(125);
		// adapterNetworkGrid.setStripeRows(true);

		adapterNetworkGrid.mask();
		return adapterNetworkGrid;
	}

	private void freshAdapterConfigGrid() {
		List<VMNetworkConfigGridModel> adapterConfigList = new ArrayList<>();
		if (vAppBackupVMModel.adapterList != null && !vAppBackupVMModel.adapterList.isEmpty()) {
			for (VMNetworkConfigInfoModel adapter : vAppBackupVMModel.adapterList) {
				VMNetworkConfigGridModel configModel = new VMNetworkConfigGridModel(adapter);
				adapterConfigList.add(configModel);

				ComboBox<BaseModelData> networkConfigComboBox = configModel.getAvaliableConfigInfoComboBox();
				networkConfigComboBox.setWidth(295);
				networkConfigComboBox.setAllowBlank(false);
				networkConfigComboBox.setTriggerAction(TriggerAction.ALL);
				networkConfigComboBox.ensureDebugId("73ec6def-3e1c-4099-a849-6ad7b48ad738");
				networkConfigComboBox.setEditable(false);
				networkConfigComboBox.setDisplayField("label");
				networkConfigComboBox.setTemplate(VAppRecoveryOptionsWizard.getComboBoxTemplate("label"));
				ListStore<BaseModelData> networkConfigStore = networkConfigComboBox.getStore();
				networkConfigStore.removeAll();
			}
		}
		adapterNetworkStore.removeAll();
		adapterNetworkStore.add(adapterConfigList);
	}

	private Widget createChildVMConfigTable() {
		LabelField sectionLabel = new LabelField(UIContext.Constants.vAppRestoreChildVMSummaryGridLabel());
		sectionLabel.addStyleName("restoreWizardSubItem");
		mainFlexTable.setWidget(5, 0, sectionLabel);

		mainFlexTable.getFlexCellFormatter().setColSpan(6, 0, 6);
		mainFlexTable.setWidget(6, 0, createChildVMConfigGrid());

		return mainFlexTable;
	}

	private Widget createChildVMConfigGrid() {
		List<ColumnConfig> columnConfigList = new ArrayList<ColumnConfig>();

		ColumnConfig nodeNameColumn = Utils.createColumnConfig("nodeName", UIContext.Constants.vAppRestoreChildVMSummaryNodeName(),
				120, new GridCellRenderer<BackupVMModel>() {
					@Override
					public Object render(final BackupVMModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<BackupVMModel> store, Grid<BackupVMModel> grid) {
						String nodeName = model.getVmHostName();

						LabelField vmNameLinkLable = new LabelField(nodeName) {
							@Override
							protected void onRender(Element parent, int index) {
								super.onRender(parent, index);
								com.google.gwt.dom.client.Element ele = getElement();
								Style style = ele.getStyle();
								style.setVerticalAlign(VerticalAlign.MIDDLE);
								style.setTextDecoration(TextDecoration.UNDERLINE);
								style.setCursor(Cursor.POINTER);
							}
						};
						vmNameLinkLable.ensureDebugId("61d45885-a460-44ee-b8e3-0375ddc4bf8e");
						if (!Utils.isEmptyOrNull(nodeName)) {
							vmNameLinkLable.setToolTip(nodeName);
						}

						final String vmInstanceUuid = model.getVmInstanceUUID();
						vmNameLinkLable.addListener(Events.OnClick, new Listener<BaseEvent>() {
							@Override
							public void handleEvent(BaseEvent be) {
								parentWizard.activeChildVMWizard(vmInstanceUuid);
							}
						});

						return vmNameLinkLable;
					}
				});
		columnConfigList.add(nodeNameColumn);
		
		ColumnConfig vmNameColumn = Utils.createColumnConfig("vmName", UIContext.Constants.vAppRestoreChildVMSummaryVMName(),
				120, new GridCellRenderer<BackupVMModel>() {
					@Override
					public Object render(final BackupVMModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<BackupVMModel> store, Grid<BackupVMModel> grid) {
						String vmName = model.getVMName();
						LabelField label = new LabelField();
						if (!Utils.isEmptyOrNull(vmName)) {
							label.setValue(vmName);
							label.setToolTip(vmName);
						}
						return label;
					}
				});
		columnConfigList.add(vmNameColumn);

		ColumnConfig cpuCountColumn = Utils.createColumnConfig("cpuCount", UIContext.Constants.vAppRestoreChildVMSummaryCPUCount(),
				50, new GridCellRenderer<BackupVMModel>() {
					@Override
					public Object render(BackupVMModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<BackupVMModel> store, Grid<BackupVMModel> grid) {
						Integer cpuCount = model.getCPUCount();
						if (cpuCount != null) {
							return cpuCount;
						} else {
							return "";
						}
					}
				});
		columnConfigList.add(cpuCountColumn);

		ColumnConfig memorySizeColumn = Utils.createColumnConfig("memorySize", UIContext.Constants.vAppRestoreChildVMSummaryMemorySize(),
				70, new GridCellRenderer<BackupVMModel>() {
					@Override
					public Object render(BackupVMModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<BackupVMModel> store, Grid<BackupVMModel> grid) {
						Long size = model.getMemorySize();
						if (size != null) {
							return size;
						} else {
							return "";
						}
					}
				});
		columnConfigList.add(memorySizeColumn);

		ColumnConfig storageProfileColumn = Utils.createColumnConfig("storagePolicy", UIContext.Constants.vAppRestoreChildVMSummaryStoragePolicy(),
				70, new GridCellRenderer<BackupVMModel>() {
					@Override
					public Object render(BackupVMModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<BackupVMModel> store, Grid<BackupVMModel> grid) {
						String policyName = model.getStorageProfileName();
						LabelField label = new LabelField();
						if (!Utils.isEmptyOrNull(policyName)) {
							label.setValue(policyName);
							label.setToolTip(policyName);
						}
						return label;
					}
				});
		columnConfigList.add(storageProfileColumn);

		ColumnConfig dataStoreColumn = Utils.createColumnConfig("dataStore", UIContext.Constants.vAppRestoreChildVMSummaryDataStore(),
				100, new GridCellRenderer<BackupVMModel>() {
					@Override
					public Object render(BackupVMModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<BackupVMModel> store, Grid<BackupVMModel> grid) {
						List<String> dsList = new ArrayList<String>();
						StringBuilder sb = new StringBuilder();

						String vmStoreName = model.getVMXDataStoreName();
						if (!Utils.isEmptyOrNull(vmStoreName)) {
							dsList.add(vmStoreName);
							sb.append(vmStoreName);
							sb.append("\n");
						}

						if (model.diskList != null && model.diskList.size() > 0) {
							for (DiskModel disk : model.diskList) {
								String diskStoreName = disk.getDiskDataStore();
								if (!Utils.isEmptyOrNull(diskStoreName)) {
									if (dsList.contains(diskStoreName)) {
										continue;
									}
									dsList.add(diskStoreName);
									sb.append(diskStoreName);
									sb.append("\n");
								}
							}
						}

						String dsName = "";
						if (sb.length() > 0) {
							int lastIndex = sb.lastIndexOf("\n");
							dsName = sb.substring(0, lastIndex);
						}
						LabelField label = new LabelField();
						if (!Utils.isEmptyOrNull(dsName)) {
							label.setValue(dsName);
							label.setToolTip(dsName);
						}
						return label;
					}
				});
		columnConfigList.add(dataStoreColumn);

		ColumnConfig networkColumn = Utils.createColumnConfig("network", UIContext.Constants.vAppRestoreChildVMSummaryNetworks(),
				130, new GridCellRenderer<BackupVMModel>() {
					@Override
					public Object render(BackupVMModel model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<BackupVMModel> store, Grid<BackupVMModel> grid) {
						StringBuilder sb = new StringBuilder();

						if (model.adapterList != null && model.adapterList.size() > 0) {
							for (VMNetworkConfigInfoModel adapter : model.adapterList) {
								String label = adapter.getLabel();
								String network = adapter.getParentName();
								if (Utils.isEmptyOrNull(network)) {
									network = UIContext.Constants.NA();
								}
								sb.append(label + ": " + network + "\n");
							}
						}

						String networks = "";
						if (sb.length() > 0) {
							int lastIndex = sb.lastIndexOf("\n");
							networks = sb.substring(0, lastIndex);
							
							String encodedDisplayName = Format.htmlEncode(networks);
							String encodedTooltip = Format.htmlEncode(networks.replaceAll("\\n", "</br>"));
							return "<span qtip=\"" + encodedTooltip + "\">"  + encodedDisplayName + "</span>";
						} else {
							return "<span>"  + networks + "</span>";
						}
					}
				});
		columnConfigList.add(networkColumn);

		childVMSummaryGrid = new Grid<BackupVMModel>(childVMSummaryStore, new ColumnModel(columnConfigList));
		childVMSummaryGrid.setTrackMouseOver(false);
		childVMSummaryGrid.setAutoExpandColumn("nodeName");
		childVMSummaryGrid.setAutoWidth(true);
		childVMSummaryGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		childVMSummaryGrid.setBorders(true);
		childVMSummaryGrid.setColumnLines(true);
		childVMSummaryGrid.setHeight(145);
		new QuickTip(childVMSummaryGrid);

		childVMSummaryGrid.mask();
		return childVMSummaryGrid;
	}

	private void initChildVMSummaryStore() {
		childVMSummaryStore.removeAll();
		if (vAppBackupVMModel.memberVMList != null && !vAppBackupVMModel.memberVMList.isEmpty()) {
			List<BackupVMModel> childVMModelList = new ArrayList<>();
			for (BackupVMModel childVMModel : vAppBackupVMModel.memberVMList) {
				BackupVMModel proxyVMModel = new BackupVMModel();
				childVMModelList.add(proxyVMModel);

				String instanceUuid = childVMModel.getVmInstanceUUID();
				String vmName = childVMModel.getVMName();
				String nodeName = VAppRecoveryOptionsWizard.getNodeName(instanceUuid, childVMModel.getVmHostName(), vmName);
				proxyVMModel.setVmInstanceUUID(instanceUuid);
				proxyVMModel.setVmHostName(nodeName);
				proxyVMModel.setVMName(vmName);
			}
			Collections.sort(childVMModelList, childVMStoreSorter);
			childVMSummaryStore.add(childVMModelList);
			if (vCloudVDCModel == null) {
				childVMSummaryGrid.mask();
			}
		} else {
			childVMSummaryGrid.mask(UIContext.Constants.vAppRestoreNoChildVMs());
		}
	}

	public void freshChildVMConfigGrid(boolean isNeedUnMask) {
		childVMSummaryStore.removeAll();
		
		VAppChildVMRecoveryWizard childVMWizard = parentWizard.getChildVMWizard();
		if (childVMWizard == null) {
			childVMSummaryGrid.mask(UIContext.Constants.vAppRestoreNoChildVMs());
			return;
		}
		Map<String, VAppChildVMRecoveryOptionsPanel> childPanelMap = childVMWizard.getChildVMPanelMap();
		if (childPanelMap.isEmpty()) {
			childVMSummaryGrid.mask(UIContext.Constants.vAppRestoreNoChildVMs());
			return;
		}

		String[] vmInstanceIdArray = childPanelMap.keySet().toArray(new String[0]);
		List<BackupVMModel> proxyVMModelList = new ArrayList<>();
		for (String instanceUuid : vmInstanceIdArray) {
			VAppChildVMRecoveryOptionsPanel panel = childPanelMap.get(instanceUuid);
			if (panel == null) {
				continue;
			}
			
			BackupVMModel proxyVMModel = new BackupVMModel();
			proxyVMModelList.add(proxyVMModel);
			
			String nodeName = VAppRecoveryOptionsWizard.getNodeName(panel.getBackupVMModel());
			proxyVMModel.setVmInstanceUUID(instanceUuid);
			proxyVMModel.setVmHostName(nodeName);
			proxyVMModel.setVMName(panel.getVMName());

			SimpleComboValue<Integer> simpleValue = panel.getCpuComboxBox().getValue();
			if (simpleValue != null) {
				proxyVMModel.setCPUCount(simpleValue.getValue());
			} else {
				proxyVMModel.setCPUCount(null);
			}

			Long memorySize = panel.getMmemorySizeTextField().getValue();
			proxyVMModel.setMemorySize(memorySize);

			VCloudStorageProfileModel profileModel = panel.getProfileComboBox().getValue();
			if (profileModel != null) {
				proxyVMModel.setStorageProfileId(profileModel.getId());
				proxyVMModel.setStorageProfileName(profileModel.getName());
			}

			VMStorage storage = panel.getVmDataStoreComboBox().getValue();
			if (storage != null) {
				proxyVMModel.setVMXDataStoreId(storage.getId());
				proxyVMModel.setVMXDataStoreName(storage.getName());
			}

//			Grid<DataStoreModel> diskGrid = panel.getDiskDatastoreConfGrid();
//			ListStore<DataStoreModel> diskGirdStore = diskGrid.getStore();
//			if (diskGirdStore != null && diskGirdStore.getCount() > 0) {
//				List<DataStoreModel> diskList = diskGirdStore.getModels();
//				List<DiskModel> proxyDiskList = new ArrayList<>();
//				proxyVMModel.diskList = proxyDiskList;
//
//				for (DataStoreModel rowModel : diskList) {
//					VMStorage diskStorage = (VMStorage) rowModel.getEsxDataStoreComboBox().getValue();
//					if (diskStorage != null) {
//						DiskModel disk = new DiskModel();
//						proxyDiskList.add(disk);
//
//						disk.setDiskDataStoreId(diskStorage.getId());
//						disk.setDiskDataStore(diskStorage.getName());
//					}
//				}
//			}

			Grid<VMNetworkConfigGridModel> adapterGrid = panel.getAdapterNetworkGrid();
			ListStore<VMNetworkConfigGridModel> adapterGirdStore = adapterGrid.getStore();
			if (adapterGirdStore != null && adapterGirdStore.getCount() > 0) {
				List<VMNetworkConfigGridModel> adapterList = adapterGirdStore.getModels();
				proxyVMModel.adapterList.clear();

				for (VMNetworkConfigGridModel rowModel : adapterList) {
					VMNetworkConfigInfoModel proxyNetwork = new VMNetworkConfigInfoModel();
					proxyVMModel.adapterList.add(proxyNetwork);
					
					VMNetworkConfigInfoModel originalAdapter = rowModel.getInfoModel();
					proxyNetwork.setNetworkId(originalAdapter.getNetworkId());
					proxyNetwork.setLabel(originalAdapter.getLabel());
					
					VMNetworkConfigInfoModel targetNetworkModel = (VMNetworkConfigInfoModel) rowModel.getAvaliableConfigInfoComboBox().getValue();
					if (targetNetworkModel != null) {
						proxyNetwork.setParentId(targetNetworkModel.getNetworkId());
						proxyNetwork.setParentName(targetNetworkModel.getLabel());
					}
				}
			}
		}
		Collections.sort(proxyVMModelList, childVMStoreSorter);
		childVMSummaryStore.add(proxyVMModelList);
		if (childVMSummaryStore.getCount() <= 0) {
			childVMSummaryGrid.mask(UIContext.Constants.vAppRestoreNoChildVMs());
		} else if (isNeedUnMask && vCloudVDCModel != null) {
			childVMSummaryGrid.unmask();
		}
	}

	private Widget createFootConfigTable() {
		LabelField sectionLabel = new LabelField(UIContext.Constants.vAppRestoreOptionsLabel());
		sectionLabel.addStyleName("restoreWizardSubItem");
		mainFlexTable.setWidget(7, 0, sectionLabel);

		mainFlexTable.setWidget(8, 0, new LabelField(UIContext.Constants.vAppRestoreResolveConflictsLabel()));
		overwriteCheckBox = new CheckBox();
		overwriteCheckBox.ensureDebugId("b10c8b9c-c828-47f3-bdde-cf6e806060ad");
		overwriteCheckBox.setBoxLabel(UIContext.Constants.vAppRestoreResolveConflictsDescription());
		mainFlexTable.setWidget(8, 3, overwriteCheckBox);

		mainFlexTable.setWidget(9, 0, new LabelField(UIContext.Constants.vAppRestorePostRestoreLabel()));
		powerOnVAppCheckBox = new CheckBox();
		powerOnVAppCheckBox.ensureDebugId("4db952bb-fbf4-4180-bd1e-db6d3081fc45");
		powerOnVAppCheckBox.setBoxLabel(UIContext.Constants.vAppRestorePostRestoreDescription());
		mainFlexTable.setWidget(9, 3, powerOnVAppCheckBox);

		return mainFlexTable;
	}

	private void freshFootConfigs() {
		overwriteCheckBox.setValue(Boolean.FALSE);
		powerOnVAppCheckBox.setValue(Boolean.FALSE);
	}

	private VCloudDirectorModel initDirectorModel() {
		vCloudDirectorModel = new VCloudDirectorModel();
		vCloudDirectorModel.setName(vAppBackupVMModel.getEsxServerName());
		vCloudDirectorModel.setUsername(vAppBackupVMModel.getEsxUsername());
		vCloudDirectorModel.setPassword(vAppBackupVMModel.getEsxPassword());
		vCloudDirectorModel.setProtocol(vAppBackupVMModel.getProtocol());
		vCloudDirectorModel.setPort(vAppBackupVMModel.getPort());
		
		VCloudVirtualDataCenterModel originalVDC = new VCloudVirtualDataCenterModel();
		originalVDC.setId(vAppBackupVMModel.getVirtualDataCenterId());
		originalVDC.setName(vAppBackupVMModel.getVirtualDataCenterName());
		vCloudDirectorModel.setOriginalVDC(originalVDC);

		return vCloudDirectorModel;
	}

	private void setSelectedVDC(VCloudVirtualDataCenterModel vDC, boolean isForceChange) {
		if ((vDC == this.vCloudVDCModel) && (isForceChange == false)) {
			return;
		}
		
		this.vCloudVDCModel = vDC;
		setNextButtonStatus(false);
		destSpecifyButton.disable();
		adapterNetworkGrid.mask(UIContext.Constants.vAppRestoreLoading());
		childVMSummaryGrid.mask(UIContext.Constants.vAppRestoreLoading());
		destinationVDCField.setValue(vDC.getName());
		freshNetworkComboboxStore();
		
		parentWizard.setVCloudVDCModel(vDC, new BaseAsyncCallback<List<BackupVMModel>>() {
			@Override
			public void onFailure(Throwable caught) {
				setNextButtonStatus(true);
				childVMSummaryGrid.mask(UIContext.Constants.vAppRestoreChildVMSummaryError());
				destSpecifyButton.enable();
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(List<BackupVMModel> result) {
				setNextButtonStatus(true);
				freshChildVMConfigGrid(true);
				destSpecifyButton.enable();
			}
		});
	}

	public void freshNetworkComboboxStore() {
		List<VMNetworkConfigInfoModel> vDCNetworkList = null;
		if (this.vCloudVDCModel != null) {
			vDCNetworkList = this.vCloudVDCModel.getAvailableNetworks();
		} else {
			vDCNetworkList = new ArrayList<>();
		}

		List<VMNetworkConfigGridModel> rowModels = adapterNetworkStore.getModels();
		if (rowModels != null && !rowModels.isEmpty()) {
			for (VMNetworkConfigGridModel model : rowModels) {
				ComboBox<BaseModelData> targetComboBox = model.getAvaliableConfigInfoComboBox();
				ListStore<BaseModelData> store = targetComboBox.getStore();
				store.removeAll();
				store.add(vDCNetworkList);
				
				targetComboBox.setRawValue("");
				VMNetworkConfigInfoModel matchedModel = getMatchedNetwork(model);
				if (matchedModel != null) {
					targetComboBox.setValue(matchedModel);
				}
			}
		}
		if (!vDCNetworkList.isEmpty()) {
			adapterNetworkGrid.unmask();
		} else {
			String vDCName = this.vCloudVDCModel.getName();
			String maskMsg = UIContext.Constants.vAppRestoreNoVDCNetworks();
			if (vDCName != null && !vDCName.trim().isEmpty()) {
				maskMsg = UIContext.Messages.vAppRestoreNoVDCNetworks(vDCName);
			}
			adapterNetworkGrid.mask(maskMsg);
		}
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

	public void validate(AsyncCallback<Boolean> callback) {
		if (Utils.isEmptyOrNull(vAppNameComboBox.getRawValue())) {
			vAppNameComboBox.markInvalid(UIContext.Constants.vAppRestoreRequiredFieldMark());
			callback.onSuccess(Boolean.FALSE);
			parentWizard.activeVAppPanel();
			return;
		} else {
			vAppNameComboBox.clearInvalid();
		}

		if (Utils.isEmptyOrNull(destinationVDCField.getRawValue())) {
			destinationVDCField.markInvalid(UIContext.Constants.vAppRestoreRequiredFieldMark());
			callback.onSuccess(Boolean.FALSE);
			parentWizard.activeVAppPanel();
			return;
		} else {
			destinationVDCField.clearInvalid();
		}

		checkVAppNetWorkConfiguration(callback);
	}

	private void checkVAppNetWorkConfiguration(final AsyncCallback<Boolean> callback) {
		boolean isNetWorkConfiged = true;
		List<VMNetworkConfigGridModel> gridModels = adapterNetworkStore.getModels();
		
		String adpaterNames = "";
		if (gridModels != null && !gridModels.isEmpty()) {
			for (VMNetworkConfigGridModel model : gridModels) {
				VMNetworkConfigInfoModel selectedCell = (VMNetworkConfigInfoModel) model.getAvaliableConfigInfoComboBox()
						.getValue();
				if (selectedCell == null ) {
					isNetWorkConfiged = false;
					adpaterNames += model.getInfoModel().getLabel() + ",";
				}
			}
		} 
		
		if (!isNetWorkConfiged) {
			adpaterNames = adpaterNames.substring(0, adpaterNames.length() - 1);
			String msg = UIContext.Messages.vAppRestoreVAppNetworkNotConfig(adpaterNames);
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
						checkDestination(callback);
					} else {
						callback.onSuccess(Boolean.FALSE);
						parentWizard.activeVAppPanel();
					}
				}
			});
			mb.show();
		} else {
			checkDestination(callback);
		}
	}

	private void checkDestination(final AsyncCallback<Boolean> outterCallback) {
		if (vCloudDirectorModel == null) {
			destinationVDCField.markInvalid(UIContext.Constants.vAppRestoreInvalidValue());
			outterCallback.onSuccess(Boolean.FALSE);
			parentWizard.activeVAppPanel();
			return;
		} else {
			destinationVDCField.clearInvalid();
		}

		if (vCloudVDCModel == null) {
			destinationVDCField.markInvalid(UIContext.Constants.vAppRestoreInvalidValue());
			outterCallback.onSuccess(Boolean.FALSE);
			parentWizard.activeVAppPanel();
			return;
		} else {
			List<VirtualCenterModel> vcList = vCloudVDCModel.getVCenters();
			if (vcList == null || vcList.isEmpty()) {
				destinationVDCField.markInvalid(UIContext.Constants.vAppRestoreInvalidValue());
				outterCallback.onSuccess(Boolean.FALSE);
				parentWizard.activeVAppPanel();
				return;
			} else {
				destinationVDCField.clearInvalid();
			}
		}

		checkChildVMContent(outterCallback);
	}

	private void checkChildVMContent(final AsyncCallback<Boolean> outterCallback) {
		parentWizard.getChildVMWizard().validate(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (result != null && result.equals(Boolean.FALSE)) {
					outterCallback.onSuccess(Boolean.FALSE);
				} else {
					checkAllChildVMNames(outterCallback);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				outterCallback.onFailure(caught);
			}
		});
	}

	private void checkAllChildVMNames(final AsyncCallback<Boolean> outCallback) {
		parentWizard.getChildVMWizard().checkAllChildVMName(new AsyncCallback<Map<String, ArrayList<String>>>() {
			@Override
			public void onFailure(Throwable caught) {
				outCallback.onFailure(caught);
			}

			@Override
			public void onSuccess(Map<String, ArrayList<String>> result) {
				if (result == null || result.isEmpty()) {
					checkTotalDatastoreSize(outCallback);
				} else {
					outCallback.onSuccess(Boolean.FALSE);
					showDuplicatedVMNameMessage(result);
				}				
			}
		});
	}

	private void showDuplicatedVMNameMessage(Map<String, ArrayList<String>> nameHostMap) {
		StringBuilder sb = new StringBuilder();
		Set<String> vmNameSet = nameHostMap.keySet();
		for (String vmName : vmNameSet) {
			List<String> nodeList = nameHostMap.get(vmName);
			if (nodeList != null && !nodeList.isEmpty()) {
				String nodes = nodeList.toString();
				String msg = UIContext.Messages.vAppRestoreDuplicatedVMNames(vmName, nodes);
				sb.append(msg + "\n");
			}
		}
		
		String temp = sb.toString();
		String message = temp.substring(0, temp.lastIndexOf("\n"));
		
		MessageBox mb = new MessageBox();
		mb.setIcon(MessageBox.ERROR);
		mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(VAppRecoveryOptionsWizard.getProductName()));
		mb.setMessage(message);
		mb.show();
	}

	private void checkTotalDatastoreSize(final AsyncCallback<Boolean> outCallback) {
		parentWizard.getChildVMWizard().checkTotalDatastoreSize(new AsyncCallback<List<String[]>>() {
			@Override
			public void onSuccess(List<String[]> result) {
				if (result == null || result.isEmpty()) {
					checkTotalDatastoreSizeOnProfile(outCallback);
				} else {
					showInsufficientDatastoreMessage(result, outCallback, true);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// should not happen
				outCallback.onFailure(caught);
			}
		});
	}
	
	private void checkTotalDatastoreSizeOnProfile(final AsyncCallback<Boolean> outCallback) {
		parentWizard.getChildVMWizard().checkProfileTotalLimitedSizeAfterRequested(new AsyncCallback<List<String[]>>() {
			@Override
			public void onSuccess(List<String[]> result) {
				if (result == null || result.isEmpty()) {
					checkTotalMemorySize(outCallback);
				} else {
					showInsufficientDatastoreMessage(result, outCallback, false);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// should not happen
				outCallback.onFailure(caught);
			}
		});
	}

	private void showInsufficientDatastoreMessage(List<String[]> datastores, final AsyncCallback<Boolean> outCallback, boolean isDS) {
		StringBuilder msgSB = new StringBuilder();
		for (String[] nameFreeSize : datastores) {
			String msg = "";
			if (isDS) {
				msg = UIContext.Messages.vAppRestoreDataStoreNotEnough(nameFreeSize[0], nameFreeSize[1], nameFreeSize[2]);
			} else {
				msg = UIContext.Messages.vAppRestorePolicyNotEnough(nameFreeSize[0], nameFreeSize[1], nameFreeSize[2]);
			}
			msgSB.append(msg + "\n");
		}
		
		String temp = msgSB.toString();
		String msg = temp.substring(0, temp.lastIndexOf("\n"));
		MessageBox mb = new MessageBox();
		mb.setIcon(MessageBox.WARNING);
		mb.setButtons(MessageBox.YESNO);
		mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(VAppRecoveryOptionsWizard.getProductName()));
		mb.setMessage(msg);
		Utils.setMessageBoxDebugId(mb);
		mb.addCallback(new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					checkVAppRestoreJobExist(outCallback);
				} else {
					outCallback.onSuccess(Boolean.FALSE);
				}
			}
		});
		mb.show();
	}
	
	private void checkTotalMemorySize(final AsyncCallback<Boolean> outCallback) {
		if (VAppRecoveryOptionsWizard.VDCMemoryCPUAllocationModel.AllocationPool.getDescription().equalsIgnoreCase(
				vCloudVDCModel.getAllocationModel())) {
			parentWizard.getChildVMWizard().checkTotalMemorySize(new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {
					if (result == null || result.isEmpty()) {
						checkVAppRestoreJobExist(outCallback);
					} else {
						VAppRecoveryOptionsWizard.showErrorMessage(result);
						outCallback.onSuccess(Boolean.FALSE);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					// should not happen
					outCallback.onFailure(caught);
				}
			});	
		} else {
			checkVAppRestoreJobExist(outCallback);
		}
	}
	
	private void checkVAppRestoreJobExist(final AsyncCallback<Boolean> outCallback) {
		service.checkRecoveryVMJobExist(vAppNameComboBox.getRawValue(), vCloudDirectorModel.getName(),
				new BaseAsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						outCallback.onSuccess(Boolean.FALSE);
					}

					@Override
					public void onSuccess(Boolean isExist) {
						if (isExist != null && !isExist) {
							checkSessionPassword(outCallback);
						} else {
							VAppRecoveryOptionsWizard.showErrorMessage(outCallback, UIContext.Constants.vmRecoveryCannotRun());
						}
					}
				});
	}

	private void checkSessionPassword(final AsyncCallback<Boolean> outCallback) {
		final PasswordPane pwdPanel = parentWizard.getSessionPwdPanel();
		pwdPanel.clearInvalid();
		if (pwdPanel.isVisible()) {
			String password = pwdPanel.getPassword();
			if (password != null && password.length() > 0) {
				CommonServiceAsync service = GWT.create(CommonService.class);
				final String sessionPwdHashValue = RestoreContext.getRecoveryPointModel().getEncryptPwdHashKey();
				service.validateSessionPasswordByHash(password, password.length(), sessionPwdHashValue,
						sessionPwdHashValue.length(), new BaseAsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								if(RestoreContext.isBackupToDataStore) {
									String msg = UIContext.Constants.recoveryPointsInvalidSessionPassword();
									pwdPanel.setFocus();
									pwdPanel.forceInvalid(msg);
									thisPanel.scrollIntoView(pwdPanel);
									VAppRecoveryOptionsWizard.showErrorMessage(outCallback, msg);
								} else {
									String msg = UIContext.Constants.recoveryPointsInvalidEncryptionPassword();
									pwdPanel.setFocus();
									pwdPanel.forceInvalid(msg);
									thisPanel.scrollIntoView(pwdPanel);
									VAppRecoveryOptionsWizard.showErrorMessage(outCallback, msg);
								}
								outCallback.onSuccess(Boolean.FALSE);
								super.onFailure(caught);
							}

							@Override
							public void onSuccess(Boolean isValid) {
								if (isValid != null && isValid) {
									outCallback.onSuccess(Boolean.TRUE);
								} else {
									if(RestoreContext.isBackupToDataStore) {
										String msg = UIContext.Constants.recoveryPointsInvalidSessionPassword();
										pwdPanel.setFocus();
										pwdPanel.forceInvalid(msg);
										thisPanel.scrollIntoView(pwdPanel);
										VAppRecoveryOptionsWizard.showErrorMessage(outCallback, msg);
									} else {
										String msg = UIContext.Constants.recoveryPointsInvalidEncryptionPassword();
										pwdPanel.setFocus();
										pwdPanel.forceInvalid(msg);
										thisPanel.scrollIntoView(pwdPanel);
										VAppRecoveryOptionsWizard.showErrorMessage(outCallback, msg);
									}
								}
							}
						});
			} else {
				String msg = UIContext.Constants.recoveryPointsNeedSessionPassword();
				pwdPanel.setFocus();
				pwdPanel.forceInvalid(msg);
				thisPanel.scrollIntoView(pwdPanel);
				VAppRecoveryOptionsWizard.showErrorMessage(outCallback, msg);
			}
		} else {
			outCallback.onSuccess(Boolean.TRUE);
		}
	}

	public RestoreJobModel processOptions() {
		RestoreJobModel restoreModel = RestoreContext.getRestoreModel();
		RecoverVMOptionModel recoverVMOption = new RecoverVMOptionModel();
		restoreModel.recoverVMOption = recoverVMOption;

		recoverVMOption.setRegisterAsClusterHyperVVM(false);
		recoverVMOption.setOriginalLocation(false);
		recoverVMOption.setOverwriteExistingVM(overwriteCheckBox.getValue());
		recoverVMOption.setPowerOnAfterRestore(powerOnVAppCheckBox.getValue());
		
		PasswordPane sessionPwdPanel = parentWizard.getSessionPwdPanel();
		if (sessionPwdPanel != null && sessionPwdPanel.isVisible()) {
			String password = sessionPwdPanel.getPassword();
			recoverVMOption.setEncryptPassword(password);
		}
		Integer vAppSessionID = RestoreContext.getRecoveryPointModel().getSessionID();
		recoverVMOption.setSessionNumber(vAppSessionID);

		// VCloud
		VirtualCenterModel vcModel = new VirtualCenterModel();
		vcModel.setVcName(vCloudDirectorModel.getName());
		vcModel.setUsername(vCloudDirectorModel.getUsername());
		vcModel.setPassword(vCloudDirectorModel.getPassword());
		vcModel.setProtocol(vCloudDirectorModel.getProtocol());
		vcModel.setPort(vCloudDirectorModel.getPort());
		recoverVMOption.setVCModel(vcModel);

		recoverVMOption.setVcName(vCloudDirectorModel.getName()); // VCloud name
		recoverVMOption.setESXServerName(vCloudVDCModel.getId()); // VDC id, for re-use the field in job script
		recoverVMOption.setVirtualDataCenterName(vCloudVDCModel.getName()); // VDC name
		recoverVMOption.setVMName(vAppNameComboBox.getRawValue()); // vApp name
		recoverVMOption.setVMInstanceUUID(vAppBackupVMModel.getVmInstanceUUID()); // vApp id
		recoverVMOption.setVmIdInVApp(vAppBackupVMModel.getVmIdInVApp()); // vApp UUID in VDC

		// vApp network
		recoverVMOption.setNetworkConfigInfo(getNetworkConfig4RestoreModel());

		return restoreModel;
	}

	private List<VMNetworkConfigInfoModel> getNetworkConfig4RestoreModel() {
		List<VMNetworkConfigInfoModel> infoModelList = new ArrayList<VMNetworkConfigInfoModel>();

		List<VMNetworkConfigGridModel> adapterNetworkModelList = adapterNetworkStore.getModels();
		if (adapterNetworkStore != null && !adapterNetworkModelList.isEmpty()) {
			for (VMNetworkConfigGridModel gridModel : adapterNetworkModelList) {
				VMNetworkConfigInfoModel adapter = gridModel.getInfoModel();
				VMNetworkConfigInfoModel selectedCellModel = (VMNetworkConfigInfoModel) gridModel.getAvaliableConfigInfoComboBox().getValue();

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
	
	private void setNextButtonStatus(boolean isEnable) {
		if (isEnable) {
			parentWizard.getRestoreWizardContainer().nextButton.enable();
		} else if (vAppBackupVMModel != null && (vAppBackupVMModel.getVMType() == BackupVMModel.Type.VMware_VApp.ordinal())){
			parentWizard.getRestoreWizardContainer().nextButton.disable();
		}
	}
	
	private static class ChildVMSummaryStoreSorter implements Comparator<BackupVMModel> {
		@Override
		public int compare(BackupVMModel o1, BackupVMModel o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if(o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}
			
			String hostName1 = o1.getVmHostName();
			String hostName2 = o2.getVmHostName();
			if (hostName1 == null && hostName2 == null) {
				return 0;
			}
			if(hostName1 == null) {
				return -1;
			}
			if (hostName2 == null) {
				return 1;
			}
			
			return hostName1.trim().compareToIgnoreCase(hostName2.trim());
		}
	}
}