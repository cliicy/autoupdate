package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.DiskModel;
import com.ca.arcflash.ui.client.model.HyperVHostStorageModel;
import com.ca.arcflash.ui.client.model.HyperVTypes;
import com.ca.arcflash.ui.client.model.VMNetworkConfigGridModel;
import com.ca.arcflash.ui.client.model.VMNetworkConfigInfoModel;
import com.ca.arcflash.ui.client.model.VMVolumeModel;
import com.ca.arcflash.ui.client.restore.BrowseHypervHostWindow;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
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
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
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
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class HyperVAlternativeLocationPanel extends LayoutContainer {
	private static String hypervServer;
	private static String hypervUserName;
	private static String hypervPassword;
	private Button vmDiskPanel_browseButton = new Button(UIContext.Constants.restoreBrowse());
	private Button vmBasicPanel_browseButton = new Button(UIContext.Constants.restoreBrowse());
	private static LoginServiceAsync service = GWT.create(LoginService.class);
	private HyperVConnectionPanel connectionPanel;
	private Grid<VMNetworkConfigGridModel> networkGrid;
	private static ListStore<VMPathModel> diskListStore = new ListStore<VMPathModel>();
	private Grid<VMPathModel> datastoreGrid;
	private Radio radioSameDatastore;
	private Radio radioDiffDatastore;
	private BackupVMModel backupVM;
	private TextField<String> vmNameTextField;
	private SimpleComboBox<String> hyperClusterServerList;
	private TextField<String> vmDiskTextField;
	private TextField<String> vmPathTextField;
	private boolean hyperVServerValidated;
	private LayoutContainer parent;
	private ListStore<VMNetworkConfigGridModel> networkStore = new ListStore<VMNetworkConfigGridModel>();
	private Map<String, VMNetworkConfigInfoModel> availableConnectionListStore = new HashMap<String, VMNetworkConfigInfoModel>();
	private LabelField hyperClusterServerLabel;
	private CheckBox registerCluster;
	private Integer hyperVType;
	private ComboBox<BaseModelData> diskTypeCombo;
	protected RestoreWizardContainer restoreWizardWindow;      ///D2D Lite Integration
	
	public static String getHypervServer() {
		return hypervServer;
	}

	public static void setHypervServer(String hypervServer) {
		HyperVAlternativeLocationPanel.hypervServer = hypervServer;
	}

	public static String getHypervUserName() {
		return hypervUserName;
	}

	public static void setHypervUserName(String hypervUserName) {
		HyperVAlternativeLocationPanel.hypervUserName = hypervUserName;
	}

	public static String getHypervPassword() {
		return hypervPassword;
	}

	public static void setHypervPassword(String hypervPassword) {
		HyperVAlternativeLocationPanel.hypervPassword = hypervPassword;
	}

	public void enableBrowseButtons(boolean enable) {
		for(VMPathModel model:diskListStore.getModels()) {
			model.getDiskPathContainer().getBrowseButton().setEnabled(enable);
		}
		vmDiskPanel_browseButton.setEnabled(enable);
		vmBasicPanel_browseButton.setEnabled(enable);
	}
	
	public static class PathSelectionPanel extends LayoutContainer{
		private TextField<String> vmDiskTextField;
		private Button browseButton = new Button(UIContext.Constants.restoreBrowse());	
		
		public Button getBrowseButton() {
			return browseButton;
		}

		public TextField<String> getVmDiskTextField() {
			return vmDiskTextField;
		}

		public PathSelectionPanel(){
			setStyleAttribute("padding-top", "4px");
			setStyleAttribute("padding-bottom", "4px");
			setLayout(new TableLayout(2));
			
			vmDiskTextField = new TextField<String>();
			vmDiskTextField.setAllowBlank(false);
			vmDiskTextField.setValidateOnBlur(false);
			vmDiskTextField.setWidth("180");
			vmDiskTextField.setEmptyText(UIContext.Messages.sampleTextMessage("C:\\DiskPath"));
			add(vmDiskTextField);

			browseButton.setStyleAttribute("padding-left", "10px");
			browseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					showBrowseDialog(vmDiskTextField, getHypervServer(), getHypervUserName(), getHypervPassword());
				}
			});
			
			add(browseButton);
		}
		
		public boolean validate() {
			if(vmDiskTextField.getValue() == null || vmDiskTextField.getValue().isEmpty())
				return false;
			return vmDiskTextField.validate();
		}
		
		public String getDiskPath(){
			return appendSlash(vmDiskTextField.getValue());
		}
	}
	
	public static class VMPathModel extends BaseModelData{
		private ComboBox<BaseModelData> diskTypeCombo;
		
		public VMPathModel(DiskModel diskModel){
			set("diskModel", diskModel);
			set("diskPathContainer", new PathSelectionPanel());
		}
		
		public PathSelectionPanel  getDiskPathContainer(){
			return get("diskPathContainer");
		}
		
		public DiskModel getDiskModel(){
			return get("diskModel");
		}
		
		public void setDiskModel(DiskModel model){
			set("diskModel",model);
		}
		
		public ComboBox<BaseModelData> getDiskTypeComboBox(DiskModel disk){
			diskTypeCombo = GenerateDiskTypeCombo(disk);
			return diskTypeCombo;
		}
		
		public long getDiskType(){
			long type = diskTypeCombo.getValue().get("value");
			if (type == DiskModel.HYPERV_VDISK_TYPE_FIXED || type == DiskModel.HYPERV_VDISK_TYPE_FIXED_QUICK)
				return DiskModel.HYPERV_VDISK_TYPE_FIXED;
			else
				return type;
		}
		
		public long getQuickRecovery(){
			long type = diskTypeCombo.getValue().get("value");
			return type == DiskModel.HYPERV_VDISK_TYPE_FIXED_QUICK?1:0;
		}
	}
	
	/*public HyperVAlternativeLocationPanel(LayoutContainer parent){
		this.parent = parent;
		this.add(createHyperVServerFieldSet());
	    this.add(createVMSettingFieldSet());
	}*/
	//fix for 209742
	public HyperVAlternativeLocationPanel(LayoutContainer parent, RestoreWizardContainer restoreWizardWindow) {
		this.parent = parent;
		this.restoreWizardWindow = restoreWizardWindow;
		this.add(createHyperVServerFieldSet());
	    this.add(createVMSettingFieldSet());	    
	}
	
	@Override
	protected void onShow() {
		super.onShow();		

		if ((null == this.backupVM) || 
			(!RestoreContext.getBackupVMModel().getVmInstanceUUID().equalsIgnoreCase(this.backupVM.getVmInstanceUUID())) ||
			(!RestoreContext.getBackupVMModel().getVMName().equalsIgnoreCase(this.backupVM.getVMName()))){
			
			this.backupVM = RestoreContext.getBackupVMModel();
			
			connectionPanel.getHostField().setValue(backupVM.getEsxServerName());
			connectionPanel.getUsernameField().setValue(backupVM.getEsxUsername());
			connectionPanel.clearPassword();
			vmNameTextField.setValue(backupVM.getVMName());
			
			List<VMPathModel> datastoreList = new ArrayList<VMPathModel>();
			if(backupVM.diskList!=null){
				for(DiskModel diskModel : backupVM.diskList){
					datastoreList.add(new VMPathModel(diskModel));
				}
			}
			datastoreGrid.getStore().removeAll();
			datastoreGrid.getStore().add(datastoreList);
			
			this.fillNetworkGrid();
			
			datastoreGrid.setVisible(!radioSameDatastore.getValue());
			
			networkGrid.mask(UIContext.Constants.hyperVNetworkGridMaskMessage());
			enableBrowseButtons(false);
		}
		
		this.backupVM = RestoreContext.getBackupVMModel();
		
//		networkGrid.mask(UIContext.Constants.hyperVNetworkGridMaskMessage());
//		enableBrowseButtons(false);
		
//		connectionPanel.getHostField().setValue(backupVM.getEsxServerName());
//		connectionPanel.getUsernameField().setValue(backupVM.getEsxUsername());
//		vmNameTextField.setValue(backupVM.getVMName());
		
//		List<VMPathModel> datastoreList = new ArrayList<VMPathModel>();
//		if(backupVM.diskList!=null){
//			for(DiskModel diskModel : backupVM.diskList){
//				datastoreList.add(new VMPathModel(diskModel));
//			}
//		}
//		datastoreGrid.getStore().removeAll();
//		datastoreGrid.getStore().add(datastoreList);
//		
//		this.fillNetworkGrid();
//		
//		networkGrid.mask(UIContext.Constants.hyperVNetworkGridMaskMessage());
//		enableBrowseButtons(false);
//		
//		datastoreGrid.setVisible(!radioSameDatastore.getValue());
	}

	private FieldSet createVMSettingFieldSet(){
		FieldSet fieldSet = new FieldSet();
	    fieldSet.setHeadingHtml(UIContext.Constants.vmRecoveryOtherInfo());
	    fieldSet.setLayout(new TableLayout(1));
		
		fieldSet.add(createVMBasicPanel());
		
		fieldSet.add(createVMDiskPanel());
		
		LabelField label = new LabelField(UIContext.Constants.restoreVDSNetworkLable());
		label.setStyleAttribute("padding-top", "8px");
		fieldSet.add(label);
		
		fieldSet.add(createNetworkPanel());
		
	    return fieldSet;
	}
	
	private LayoutContainer createVMDiskPanel() {
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new RowLayout());
		container.setStyleAttribute("padding-top", "4px");
		
		radioSameDatastore = new Radio();
		radioSameDatastore.setBoxLabel(UIContext.Constants.restoreHyperVSameDataPath()); 
		radioSameDatastore.setValue(true);
		container.add(radioSameDatastore);
		
		final LayoutContainer vmPathContainer = new LayoutContainer();
		vmPathContainer.setStyleAttribute("padding-top", "4px");
		vmPathContainer.setStyleAttribute("padding-bottom", "4px");
		TableLayout layout = new TableLayout(3);
		layout.setCellPadding(4);
		vmPathContainer.setLayout(layout);
		
		LabelField label = new LabelField(UIContext.Messages.rpsTrustHostServerLabel(UIContext.Constants.coldStandbySettingHypervPathLabel()));
		vmPathContainer.add(label);
		
		vmDiskTextField = new TextField<String>();
		vmDiskTextField.setAllowBlank(false);
		vmDiskTextField.setValidateOnBlur(false);
		vmDiskTextField.setWidth("200");
		vmDiskTextField.setEmptyText(UIContext.Messages.sampleTextMessage("C:\\DiskPath"));
		vmPathContainer.add(vmDiskTextField);
		
		vmDiskPanel_browseButton.setStyleAttribute("padding-left", "20px");
		vmDiskPanel_browseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				showBrowseDialog(vmDiskTextField, getHypervServer(), getHypervUserName(), getHypervPassword());
			}
		});
		vmPathContainer.add(vmDiskPanel_browseButton);
		
		label = new LabelField(UIContext.Messages.rpsTrustHostServerLabel(UIContext.Constants.recoverVMHyperVDiskTypeLabel()));
		vmPathContainer.add(label);
		
		diskTypeCombo = GenerateDiskTypeCombo(null);
		diskTypeCombo.setWidth(200);
		vmPathContainer.add(diskTypeCombo);
		
		container.add(vmPathContainer);
		
		radioDiffDatastore = new Radio(); 
		radioDiffDatastore.setBoxLabel(UIContext.Constants.restoreHyperVDifferentDataPath()); 
		container.add(radioDiffDatastore);
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig sourcedisk = Utils.createColumnConfig("diskNumber",UIContext.Constants.vmRecoveryDataStoreGridSoruceDiskColumn(), 80, null);
		ColumnConfig diskSize = Utils.createColumnConfig("diskSize",UIContext.Constants.vmRecoveryDataStoreGridDiskSizeColumn(), 60, null);
		ColumnConfig sourcevolumes = Utils.createColumnConfig("volumes",UIContext.Constants.vmRecoveryDataStoreGridSoruceVolumesColumn(), 80,	null);
		ColumnConfig datastoreLable = Utils.createColumnConfig("esxDataStoreComboBox", UIContext.Constants.coldStandbySettingHypervPathLabel(), 300, null);
		ColumnConfig diskFormatOption = Utils.createColumnConfig("diskFormatOption", UIContext.Constants.recoverVMHyperVDiskTypeLabel(), 150, null);

		GridCellRenderer<VMPathModel> sourceDiskRenderer = new GridCellRenderer<VMPathModel>() {

			@Override
			public Object render(VMPathModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VMPathModel> store, Grid<VMPathModel> grid) {
				DiskModel diskModel=model.getDiskModel();
				String stringDisk = UIContext.Constants.vmRecoveryDisk() + diskModel.getDiskNumber();
				return stringDisk;
			}
		};
		sourcedisk.setRenderer(sourceDiskRenderer);
		
		GridCellRenderer<VMPathModel> diskSizeRenderer = new GridCellRenderer<VMPathModel>() {
			
			@Override
			public Object render(VMPathModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VMPathModel> store, Grid<VMPathModel> grid) {
				DiskModel diskModel=model.getDiskModel();
				if(diskModel!=null && diskModel.getSize()!=null){
					return Utils.bytes2String(diskModel.getSize());
				}
				//return getImageHtml("disk.gif")+stringDisk;
				return "";
			}
		};
		diskSize.setRenderer(diskSizeRenderer);
		
		GridCellRenderer<VMPathModel> sourceVolumesRenderer = new GridCellRenderer<VMPathModel>() {

			@Override
			public Object render(VMPathModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VMPathModel> store, Grid<VMPathModel> grid) {
				String volumes = "";
				if(model.getDiskModel().volumeModelList!=null && model.getDiskModel().volumeModelList.size()>0){
					for(VMVolumeModel volumeModel : model.getDiskModel().volumeModelList){
						volumes = volumes + ";" + volumeModel.getDriveLetter();
					}
				}
				if(!volumes.isEmpty() && volumes.charAt(0) == ';') {
					volumes = volumes.substring(1);
				}
				return volumes;
			}
		};
		sourcevolumes.setRenderer(sourceVolumesRenderer);
		
		GridCellRenderer<VMPathModel> datastoRenderer = new GridCellRenderer<VMPathModel>() {

			@Override
			public Object render(VMPathModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VMPathModel> store, Grid<VMPathModel> grid) {
				return model.get("diskPathContainer");
			}
			
		};
		datastoreLable.setRenderer(datastoRenderer);
		
		GridCellRenderer<VMPathModel> diskFormatOptionRenderer = new GridCellRenderer<VMPathModel>() {
			@Override
			public Object render(VMPathModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<VMPathModel> store, Grid<VMPathModel> grid) {
				return model.getDiskTypeComboBox(model.getDiskModel());
			}
		};
		diskFormatOption.setRenderer(diskFormatOptionRenderer);

		configs.add(sourcedisk);
		configs.add(diskSize);
		configs.add(sourcevolumes);
		configs.add(diskFormatOption);
		configs.add(datastoreLable);

		ColumnModel datastoreColumnModel = new ColumnModel(configs);
		
		// datastoreGrid.ensureDebugId("7f45b322-e910-4d64-bdb6-94a9ada202fd");
		datastoreGrid = new Grid<VMPathModel>(diskListStore,
				datastoreColumnModel);
		datastoreGrid.setAutoExpandMin(300);
		datastoreGrid.setAutoExpandColumn("esxDataStoreComboBox");
		datastoreGrid.getSelectionModel()
				.setSelectionMode(SelectionMode.SINGLE);
		datastoreGrid.setColumnLines(true);
		datastoreGrid.setBorders(true);
		datastoreGrid.setSize(600, 200);
		container.add(datastoreGrid);
		
		RadioGroup radioGroup = new RadioGroup();
		radioGroup.add(radioSameDatastore);
		radioGroup.add(radioDiffDatastore);
		radioGroup.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				datastoreGrid.setVisible(!radioSameDatastore.getValue());
				vmPathContainer.setVisible(radioSameDatastore.getValue());
			}
		});
		
		return container;
	}

	private FieldSet createHyperVServerFieldSet(){
		FieldSet fieldSet = new FieldSet();
	    fieldSet.setHeadingHtml(UIContext.Constants.vmRecoveryHyperVInfo());
	    fieldSet.setLayout(new TableLayout(2));
		
	    TableData tb = new TableData();
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
	    
	    connectionPanel = new HyperVConnectionPanel(null, HorizontalAlignment.LEFT);
		fieldSet.add(connectionPanel, tb);
		
		tb = new TableData();
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		tb.setVerticalAlign(VerticalAlignment.BOTTOM);
		
		Button connectButton = new Button(UIContext.Constants.restoreHyperVConnectServerButton());
		connectButton.setStyleAttribute("padding-left", "10px");
		connectButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				parent.mask(UIContext.Constants.connectToHyperV());
				service.validateHyperV(connectionPanel.getHyperVHostName(), connectionPanel.getHyperVUserName(), connectionPanel.getHyperVPassword(), new BaseAsyncCallback<Void>(){

					@Override
					public void onFailure(Throwable caught) {
						enableBrowseButtons(false);
						super.onFailure(caught);
						parent.unmask();
					}

					@Override
					public void onSuccess(Void result) {
						setHypervServer(connectionPanel.getHyperVHostName());
						setHypervUserName(connectionPanel.getHyperVUserName());
						setHypervPassword(connectionPanel.getHyperVPassword());
						enableBrowseButtons(hyperVServerValidated);
						
						BackupVMModel backupVM = RestoreContext.getBackupVMModel();
						
						/*service.CompareHyperVVersion(connectionPanel.getHyperVHostName(), connectionPanel.getHyperVUserName(), connectionPanel.getHyperVPassword(),
								RestoreContext.getBackupModel().getDestUserName(), RestoreContext.getBackupModel().getDestPassword(),
								backupVM.getBrowseDestination(), RestoreContext.getRecoveryPointModel().getSessionID(), new AsyncCallback<Integer>() {*/
						// zendesk ticket: 11630 &  13242 & RTC 209742
						// RestoreContext.getBackupModel() is null if there is no default backup setting
						
						service.CompareHyperVVersion(connectionPanel.getHyperVHostName(), connectionPanel.getHyperVUserName(), connectionPanel.getHyperVPassword(),
								restoreWizardWindow.getVMRecoveryPointsPanel().getUserName(), restoreWizardWindow.getVMRecoveryPointsPanel().getPassword(),
								backupVM.getBrowseDestination(), RestoreContext.getRecoveryPointModel().getSessionID(), new AsyncCallback<Integer>() {
								
								@Override
								public void onSuccess(Integer result) {
									parent.unmask();
									hyperVServerValidated = true;
									enableBrowseButtons(hyperVServerValidated);
									if(result > 0) {
										MessageBox box = new MessageBox();
										box.setIcon(MessageBox.WARNING);
										box.setButtons(MessageBox.YESNO);
										box.setTitleHtml(UIContext.Constants.vmRecoveryHyperVInfo());
										box.setMessage(UIContext.Constants.recoverVMLowerVersionHyperVWarning());
										box.setModal(true);
										Utils.setMessageBoxDebugId(box);
										box.show();
										box.addCallback(new Listener<MessageBoxEvent>() {
											@Override
											public void handleEvent(MessageBoxEvent be) {
												if (be.getButtonClicked().getItemId()
														.equals(com.extjs.gxt.ui.client.widget.Dialog.NO))
												{
													hyperVServerValidated = false;
													enableBrowseButtons(hyperVServerValidated);
												}
											}
										});
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									parent.unmask();
									hyperVServerValidated = true;
									enableBrowseButtons(hyperVServerValidated);
								}
						});
						
						service.GetHyperVDefaultFolderOfVM(connectionPanel.getHyperVHostName(), connectionPanel.getHyperVUserName(), connectionPanel.getHyperVPassword(), new BaseAsyncCallback<String>(){
							@Override
							public void onSuccess(String result) {
								vmPathTextField.setValue(result);
							}
						});
						
						service.getHyperVDefaultFolderOfVHD(connectionPanel.getHyperVHostName(), connectionPanel.getHyperVUserName(), connectionPanel.getHyperVPassword(), new BaseAsyncCallback<String>(){
							@Override
							public void onSuccess(String result) {
								vmDiskTextField.setValue(result);
								for(VMPathModel model:diskListStore.getModels()) {
									model.getDiskPathContainer().getVmDiskTextField().setValue(result);
								}
							}
						});
						
						service.getHyperVServerType(connectionPanel.getHyperVHostName(), connectionPanel.getHyperVUserName(), connectionPanel.getHyperVPassword(), new BaseAsyncCallback<Integer>(){

							@Override
							public void onSuccess(Integer result) {
								hyperVType = result;
								if (result == HyperVTypes.Cluster_Virutal_Node.ordinal()){
									registerCluster.disable();
									registerCluster.setValue(true);
									hyperClusterServerList.setVisible(true);
									hyperClusterServerLabel.setVisible(true);
									service.getHyperVClusterNodes(connectionPanel.getHyperVHostName(), connectionPanel.getHyperVUserName(), connectionPanel.getHyperVPassword(), new BaseAsyncCallback<List<String>>(){
										@Override
										public void onSuccess(List<String> result) {
											hyperClusterServerList.getStore().removeAll();
											hyperClusterServerList.add(result);
											if (result.size()>0)
												hyperClusterServerList.setSimpleValue(result.get(0));
										}
									});
								}else {
									getHyperVAvailabeNetworkList(connectionPanel.getHyperVHostName(), connectionPanel.getHyperVUserName(), connectionPanel.getHyperVPassword());
									hyperClusterServerList.setVisible(false);
									hyperClusterServerLabel.setVisible(false);
									if (result == HyperVTypes.Cluster_Physical_Node.ordinal()){
										registerCluster.setValue(true);
										registerCluster.enable();
									}else{
										registerCluster.setValue(false);
										registerCluster.disable();
									}
								}
							}
							
						});
					}
				});
			}
			
		});
		fieldSet.add(connectButton, tb);
		
		tb = new TableData();
		tb.setColspan(2);
		
		FlexTable panel = new FlexTable();
		panel.setWidth("100%");
		
		registerCluster = new CheckBox();
		registerCluster.setBoxLabel(UIContext.Constants.restoreHyperVVMAsCluster());
		registerCluster.disable();
		Label emptyLable = new Label("");
		emptyLable.setWidth("118");
		panel.setWidget(0, 0, emptyLable);
		panel.getCellFormatter().setWidth(0, 0, "118px");
		panel.setWidget(0, 1, registerCluster);
		panel.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE );
		panel.getCellFormatter().setHeight(0, 1, "30");
		fieldSet.add(panel, tb);
		
		return fieldSet;
	}
	
	private LayoutContainer createVMBasicPanel(){
		LayoutContainer container = new LayoutContainer();
		
		TableLayout layout = new TableLayout(2);
		layout.setCellPadding(4);
		container.setLayout(layout);
		
		TableData tb = new TableData();
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryVMNameLabel());
		container.add(label, tb);
		
		tb = new TableData();
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		vmNameTextField = new TextField<String>();
		vmNameTextField.setAllowBlank(false);
		vmNameTextField.setValidateOnBlur(false);
		vmNameTextField.setWidth("180");
		container.add(vmNameTextField, tb);
		
		tb = new TableData();
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		hyperClusterServerLabel = new LabelField();
		hyperClusterServerLabel.setValue(UIContext.Constants.hyperVServerNameCaption());
		container.add(hyperClusterServerLabel, tb);
		
		tb = new TableData();
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		hyperClusterServerList = new BaseSimpleComboBox<String>();
		hyperClusterServerList.setEditable(false);
		hyperClusterServerList.setWidth("180");
		container.add(hyperClusterServerList, tb);
		
		hyperClusterServerList.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>(){

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> value) {
				getHyperVAvailabeNetworkList(value.getSelectedItem().getValue(), connectionPanel.getHyperVUserName(), connectionPanel.getHyperVPassword());
			}
			
		});
		
		hyperClusterServerList.setVisible(false);
		hyperClusterServerLabel.setVisible(false);
		
		tb = new TableData();
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreHyperVVMPathLabel());
		container.add(label, tb);
		
		
		LayoutContainer vmPathContainer = new LayoutContainer();
		vmPathContainer.setLayout(new TableLayout(2));
		
		vmPathTextField = new TextField<String>();
		vmPathTextField.setAllowBlank(false);
		vmPathTextField.setValidateOnBlur(false);
		vmPathTextField.setWidth("180");
		vmPathContainer.add(vmPathTextField);
		vmPathTextField.setEmptyText(UIContext.Messages.sampleTextMessage("C:\\VMPath"));
		
		vmBasicPanel_browseButton.setStyleAttribute("padding-left", "20px");
		vmBasicPanel_browseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				showBrowseDialog(vmPathTextField, getHypervServer(), getHypervUserName(), getHypervPassword());
			}
		});
		vmPathContainer.add(vmBasicPanel_browseButton);
		
		container.add(vmPathContainer, tb);
		
		return container;
	}
	
	private LayoutContainer createNetworkPanel(){
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig adapter = Utils.createColumnConfig("adapter", UIContext.Constants.hyperVNetworkColumnAdapter(), 200, null);
		ColumnConfig connectNetwork = Utils.createColumnConfig("connectNetwork", UIContext.Constants.hyperVNetworkColumnConnnection(), 280, null);
		
		GridCellRenderer<VMNetworkConfigGridModel> adapterRenderer = new GridCellRenderer<VMNetworkConfigGridModel>() {
			@Override
			public Object render(VMNetworkConfigGridModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<VMNetworkConfigGridModel> store,
					Grid<VMNetworkConfigGridModel> grid) {
				VMNetworkConfigInfoModel infoModel = model.getInfoModel();
				return infoModel.getLabel();
			}
		};
		adapter.setRenderer(adapterRenderer);
		
		GridCellRenderer<VMNetworkConfigGridModel> configRenderer = new GridCellRenderer<VMNetworkConfigGridModel>() {
			@Override
			public Object render(VMNetworkConfigGridModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<VMNetworkConfigGridModel> store, Grid<VMNetworkConfigGridModel> grid) {
				return model.getAvaliableConfigInfoComboBox();
			}
		};
		connectNetwork.setRenderer(configRenderer);
		
		configs.add(adapter);
		configs.add(connectNetwork);
		
		ColumnModel networkColumnModel = new ColumnModel(configs);
		networkGrid = new Grid<VMNetworkConfigGridModel>(networkStore, networkColumnModel);
		/*if (fillNetworkGrid()) {
			networkGrid.unmask();
		}*/
		networkGrid.setTrackMouseOver(false);
		networkGrid.setAutoExpandColumn("adapter");
		networkGrid.setAutoWidth(true);
		networkGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		networkGrid.setBorders(true);
		networkGrid.setColumnLines(true);
		networkGrid.setHeight(100);
		
		final ContentPanel contentPanel = new ContentPanel();
		contentPanel.setBodyBorder(false);
		contentPanel.setBorders(false);
		contentPanel.setHeaderVisible(false);
		contentPanel.setSize(600, 100);
		contentPanel.setScrollMode(Scroll.AUTO);
		contentPanel.add(networkGrid);
		
		return contentPanel;
	}
	
	private boolean fillNetworkGrid() {
		if (backupVM.adapterList == null || backupVM.adapterList.isEmpty()) {
			networkStore.removeAll();
			//networkGrid.mask(UIContext.Constants.restoreVDSNoAdapters());
			return false;
		}
		
		int newtworkCount = 1;
		int legacyNewtworkCount = 1; 
		for (VMNetworkConfigInfoModel model:backupVM.adapterList){
			model.setLabel(model.getHyperVAdapterType() == 0? UIContext.Messages.hyperVNetwork(newtworkCount++): UIContext.Messages.hyperVLegacyNetwork(legacyNewtworkCount++));
		}

		/*if (configHelper.isEmpty()) {
			List<VMNetworkConfigGridModel> gridModels = networkStore.getModels();
			for (VMNetworkConfigGridModel gridModel : gridModels) {
				gridModel.getAvaliableConfigInfoComboBox().getStore().removeAll();
			}
		}*/

		//Map<String, NetworkConfigInCell> configMap = configHelper.getAllConfigCells();
		//List<NetworkConfigInCell> cofigList = new ArrayList<NetworkConfigInCell>(configMap.values());
		//Collections.sort(cofigList);
		
		//String longgestStr = getLonggestConfigString(cofigList);
		List<VMNetworkConfigGridModel> gridList = new ArrayList<VMNetworkConfigGridModel>();
		for (VMNetworkConfigInfoModel infoModel : backupVM.adapterList) {
			VMNetworkConfigGridModel gridModel = new VMNetworkConfigGridModel(infoModel);
			
			ComboBox<BaseModelData> configComboBox = gridModel.getAvaliableConfigInfoComboBox();
			configComboBox.setWidth(245);
			configComboBox.setAllowBlank(true);
			configComboBox.setTriggerAction(TriggerAction.ALL);
			configComboBox.setEditable(false);
			configComboBox.setDisplayField("label");
			//configComboBox.setTemplate(getTemplate());
			ListStore<BaseModelData> configStore = configComboBox.getStore();
			
			configStore.removeAll();
			//configStore.add(cofigList);
			//String name = getMatchedDiplayNameFromNetwork(gridModel.getInfoModel(), cofigList);
			//if (configMap.containsKey(name)) {
			//	configComboBox.setValue(configMap.get(name));
			//} else {
			//	configComboBox.setEmptyText("");
			//}
			//setDropdownListWidth(configComboBox, longgestStr);
			
			gridList.add(gridModel);
		}
		
		networkStore.removeAll();
		networkStore.add(gridList);
		
		/*if (cofigList.isEmpty()) {
			return false;
		}*/
		return true;
	}
	
	public void validate(final AsyncCallback<Boolean> callback){
		if(!connectionPanel.validate()) {
			callback.onSuccess(Boolean.FALSE);
			return;
		}
		
		if(!hyperVServerValidated){
			validateError(UIContext.Constants.hyperVFailValidateConnection());
			callback.onSuccess(Boolean.FALSE);
			return;
		}
		
		if(!vmNameTextField.validate()) {
			callback.onSuccess(Boolean.FALSE);
			return;
		}
		
		if(vmNameTextField.getValue() == null || vmNameTextField.getValue().isEmpty()) {
			callback.onSuccess(Boolean.FALSE);
			return;
		}
		
		if(!vmPathTextField.validate()) {
			callback.onSuccess(Boolean.FALSE);
			return;
		}
		
		if(vmPathTextField.getValue() == null || vmPathTextField.getValue().isEmpty()) {
			callback.onSuccess(Boolean.FALSE);
			return;
		}
		
		if(radioSameDatastore.getValue() && !vmDiskTextField.validate()) {
			callback.onSuccess(Boolean.FALSE);
			return;
		}
		
		if(radioDiffDatastore.getValue()){
			for (VMPathModel model:diskListStore.getModels()){
				if (!model.getDiskPathContainer().validate()) {
					callback.onSuccess(Boolean.FALSE);
					return;
				}
			}
		}

		if (!((VMRecoveryOptionsPanel)parent).doesOverwriteVM())
		{
			String newVMName = vmNameTextField.getValue();
			service.validateHyperVAndCheckIfVMExist(hypervServer, hypervUserName, hypervPassword,
				RestoreContext.getBackupVMModel().getVmInstanceUUID(), newVMName,
				new BaseAsyncCallback<Void>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
					    super.onFailure(caught);
						callback.onSuccess(false);
					}

					@Override
					public void onSuccess(Void result)
					{
						validateStorage(callback);
					}
				});
		}
		else
		    validateStorage(callback);
	}
	
	private void validateError(String message) {
		MessageBox box = new MessageBox();
		box.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductName()));
		box.setMessage(message);
		box.setIcon(MessageBox.ERROR);
		box.setButtons(Dialog.OK);
		box.show();
	}
	
	private String getProductName() {
		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == 1){
			productName = UIContext.productNamevSphere;
		}
		return productName;
	}

	public HyperVConnectionPanel getConnectionPanel() {
		return connectionPanel;
	}
	
	public String getVMName(){
		return vmNameTextField.getValue();
	}
	
	public String getVMPath(){
		return vmPathTextField.getValue();
	}
	
	public String getVMDiskPath(){
		return vmDiskTextField.getValue();
	}

	public ListStore<VMPathModel> getDatastoreStore() {
		return diskListStore;
	}
	
	public boolean isSameVMDiskPath(){
		return radioSameDatastore.getValue();
	}

	public ListStore<VMNetworkConfigGridModel> getNetworkStore() {
		return networkStore;
	}
	
	protected static void showBrowseDialog(final TextField<String> textFieldPath, String hypervServer, String hypervUserName, String hypervPassword) {

		String title = UIContext.Constants.restoreDestinationTitle();
		String path = textFieldPath.getValue();
		if(path == null) path = "";
		final BrowseHypervHostWindow browseDlg = new BrowseHypervHostWindow(title, hypervServer, hypervUserName, hypervPassword);
		//shaji02: for adding debug id.
		browseDlg.ensureDebugId("E64D924D-7160-4E1B-A877-80606A25E157");
		browseDlg.setInputFolder(path);
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

	public CheckBox getRegisterCluster() {
		return registerCluster;
	}

	public Integer getHyperVType() {
		return hyperVType;
	}
	
	public String getVirtualClusterName(){
		return hyperClusterServerList.getSimpleValue();
	}
	
	public long getDiskType(){
		long type = diskTypeCombo.getValue().get("value");
		if (type == DiskModel.HYPERV_VDISK_TYPE_FIXED || type == DiskModel.HYPERV_VDISK_TYPE_FIXED_QUICK)
			return DiskModel.HYPERV_VDISK_TYPE_FIXED;
		else
			return type;
	}
	
	public long getQuickRecovery(){
		long type = diskTypeCombo.getValue().get("value");
		return type == DiskModel.HYPERV_VDISK_TYPE_FIXED_QUICK?1:0;
	}
	
	private static ComboBox<BaseModelData> GenerateDiskTypeCombo(DiskModel disk) {
		BaseModelData fixed;
		BaseModelData quickFixed;
		BaseModelData dynamic;
		BaseModelData original;
		
		ListStore<BaseModelData> conigurationStore = new ListStore<BaseModelData>();
		ComboBox<BaseModelData> diskTypeCombo = new ComboBox<BaseModelData>();
		diskTypeCombo.setWidth(145);
		diskTypeCombo.setStore(conigurationStore);
		diskTypeCombo.setDisplayField("display");
		diskTypeCombo.setEditable(false);
		diskTypeCombo.setTriggerAction(TriggerAction.ALL);
		
		fixed = new BaseModelData();
		fixed.set("display", UIContext.Constants.recoverVMHyperVDiskTypeFixed());
		fixed.set("value", DiskModel.HYPERV_VDISK_TYPE_FIXED);
		conigurationStore.add(fixed);
		
		quickFixed = new BaseModelData();
		quickFixed.set("display", UIContext.Constants.recoverVMHyperVDiskTypeFixedQuick());
		quickFixed.set("value", DiskModel.HYPERV_VDISK_TYPE_FIXED_QUICK);
		conigurationStore.add(quickFixed);
		
		dynamic = new BaseModelData();
		dynamic.set("display", UIContext.Constants.recoverVMHyperVDiskTypeDynamic());
		dynamic.set("value", DiskModel.HYPERV_VDISK_TYPE_DYNAMIC);
		conigurationStore.add(dynamic);
		
		if (disk!=null){
			if (disk.getDiskType() == DiskModel.HYPERV_VDISK_TYPE_FIXED)
				diskTypeCombo.setValue(fixed);
			else if (disk.getDiskType() == DiskModel.HYPERV_VDISK_TYPE_DYNAMIC)
				diskTypeCombo.setValue(dynamic);
		}else{
			original= new BaseModelData();
			original.set("display", UIContext.Constants.recoverVMHyperVDiskTypeKeepSame());
			original.set("value", DiskModel.HYPERV_VDISK_TYPE_ORIGINAL);
			conigurationStore.add(original);
			diskTypeCombo.setValue(original);
		}
		
		return diskTypeCombo;
	}
	
	private static String appendSlash(String path){
		if (path==null)
			return path;
		if (!path.endsWith("\\"))
			path+="\\";
		
		return path;
	}
	
	private String isInClusterVolume(List<String> shareVolumeSet, String diskPath) {
		String diskPathTemp = diskPath.toLowerCase();
		for (String path : shareVolumeSet){
			String pathTemp = path.toLowerCase();
			if (diskPathTemp.startsWith(pathTemp)){
				return path;
			}
		}

		return null;
	}
	
	private void getHyperVAvailabeNetworkList(String hostname, String username, String password){
		networkGrid.mask(UIContext.Constants.hyperVNetworkGridMaskMessage());
		service.getHyperVAvailabeNetworkList(hostname, username, password, new BaseAsyncCallback<VMNetworkConfigInfoModel[]>(){

			@Override
			public void onSuccess(VMNetworkConfigInfoModel[] result) {
				availableConnectionListStore.clear();
				for(VMNetworkConfigInfoModel model:result)
					availableConnectionListStore.put(model.getSwitchUUID(), model);
				
				VMNetworkConfigInfoModel notConnect = new VMNetworkConfigInfoModel();
				notConnect.setSwitchUUID("");
				notConnect.setLabel(UIContext.Constants.hyperVNetworkNotConnect());
				availableConnectionListStore.put(notConnect.getSwitchUUID(), notConnect);
				
				for (VMNetworkConfigGridModel model: networkStore.getModels()){
					model.getAvaliableConfigInfoComboBox().setValue(notConnect);
					
					List<VMNetworkConfigInfoModel> list = new LinkedList<VMNetworkConfigInfoModel>();
					list.addAll(availableConnectionListStore.values());
					model.getAvaliableConfigInfoComboBox().getStore().removeAll();
					model.getAvaliableConfigInfoComboBox().getStore().add(list);
					
					if (availableConnectionListStore.containsKey(model.getInfoModel().getSwitchUUID()))
						model.getAvaliableConfigInfoComboBox().setValue(availableConnectionListStore.get(model.getInfoModel().getSwitchUUID()));
				}
				
				
				networkGrid.unmask();
			}
		}
		);
	}
	
	void validateStorage(final AsyncCallback<Boolean> callback)
	{
		service.getHyperVHostStorage(hypervServer, hypervUserName, hypervPassword, 
		    new AsyncCallback<List<HyperVHostStorageModel>>()
		    {

				@Override
				public void onFailure(Throwable caught) 
				{
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(List<HyperVHostStorageModel> result) 
				{
					boolean isDatastoreEnough = true;
					boolean isDriveExist = true;
					String currentDrive = "";
					Map<String, Long> volumeSizeMap = new HashMap<String, Long>();
								
					List<String> shareVolumeSet = new LinkedList<String>();
								
					for (HyperVHostStorageModel storage: result)
					{
						if (storage.getDrive()==null)
						{
							shareVolumeSet.add(storage.getPath().toLowerCase());
						}
					}
								
					if(diskListStore != null) 
					{
						if(!radioSameDatastore.getValue())
						{ //set disk on different datastores
						    //calculate the request size on different drives and store into map
						    for (VMPathModel model:diskListStore.getModels())
						    {
						        Long size = 0L;
						        String diskPath = model.getDiskPathContainer().getDiskPath();
						        String drive = null;
						        drive = isInClusterVolume(shareVolumeSet, diskPath);
						        if (drive == null)
						            drive = diskPath.substring(0, 1);
						        if(volumeSizeMap.containsKey(drive)) 
						        {
						            size = volumeSizeMap.get(drive) + model.getDiskModel().getSize();
						        }
						        else 
						        {
						            size = model.getDiskModel().getSize();
							    }
							    volumeSizeMap.put(drive, size);
						    }
						}
						else
						{
							for (VMPathModel model:diskListStore.getModels()){
							Long size = 0L;
							String diskPath = appendSlash(vmDiskTextField.getValue()).toLowerCase();
							String drive = null;
							drive = isInClusterVolume(shareVolumeSet, diskPath);
							if (drive == null)
									drive = diskPath.substring(0, 1);
							
							if(volumeSizeMap.containsKey(drive)) 
							{
								size = volumeSizeMap.get(drive) + model.getDiskModel().getSize();
							}
							else
							{
								size = model.getDiskModel().getSize();
							}
							
							volumeSizeMap.put(drive, size);
						}
					}
									
					// check if VM path exist on hyper-v server
					String vmPath = vmPathTextField.getValue();
					String vmPathDrive = vmPath.substring(0, 1);
					boolean exist = false;
					for (HyperVHostStorageModel storageModel : result) 
					{
					    String drive = null;
						if (storageModel.getDrive() == null)
						    drive = storageModel.getPath();
						else
						    drive = storageModel.getDrive().substring(0, 1);
						
						if(vmPathDrive.equalsIgnoreCase(drive)) 
						{
						    exist = true;
							break;
						}
					}
					
					if(!exist) 
					{
					    currentDrive = vmPathDrive;
						isDriveExist = false;
					}
					else  //check if vm disk exist on hyper-v server
					{
						Iterator<Entry<String, Long>> it = volumeSizeMap.entrySet().iterator();
						while (it.hasNext()) 
						{
						    Map.Entry<String, Long> pairs = (Map.Entry<String, Long>)(it.next());
							exist = false;
							for (HyperVHostStorageModel storageModel : result) 
							{
							    String drive = null;
								if (storageModel.getDrive() == null)
								    drive = storageModel.getPath();
								else
								    drive = storageModel.getDrive().substring(0, 1);
								
								if(pairs.getKey().equalsIgnoreCase(drive)) 
								{
								    exist = true;
									if(pairs.getValue() > storageModel.getFreeSize()) 
									{
									    isDatastoreEnough = false;
										currentDrive = drive;
										break;
									}
								}
							}
							
							if(!exist) 
							{
							    isDriveExist = false;
								currentDrive = pairs.getKey();
								break;
							}
						}
					}
				}
								
				if(!isDriveExist) 
				{
				    validateError(UIContext.Messages.vSphereHyperVHostDriveNotExist());
					callback.onSuccess(Boolean.FALSE);
			    }
				else if (!isDatastoreEnough) 
				{
				    String msg = UIContext.Messages.vSphereHyperVHostDiskSizeNotEnough(currentDrive);
					MessageBox mb = new MessageBox();
					mb.setIcon(MessageBox.WARNING);
					mb.setButtons(MessageBox.YESNO);
					mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(getProductName()));
					mb.setMessage(msg);
					Utils.setMessageBoxDebugId(mb);
					mb.addCallback(new Listener<MessageBoxEvent>(){
					    public void handleEvent(MessageBoxEvent be)
					    {
						    if (!be.getButtonClicked().getItemId().equals(Dialog.YES)) 
						    {
							    callback.onSuccess(Boolean.FALSE);
							} else 
							{
							    ((VMRecoveryOptionsPanel)parent).checkSessionPassword(callback);
							}
						}
				    });
					mb.show();
				}
				else
				{
				    ((VMRecoveryOptionsPanel)parent).checkSessionPassword(callback);
				}
			}
		});
	}
}
