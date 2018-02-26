package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.DataStoreModel;
import com.ca.arcflash.ui.client.model.DiskDataStoreModel;
import com.ca.arcflash.ui.client.model.DiskModel;
import com.ca.arcflash.ui.client.model.ESXServerModel;
import com.ca.arcflash.ui.client.model.HyperVTypes;
import com.ca.arcflash.ui.client.model.RecoverVMOptionModel;
import com.ca.arcflash.ui.client.model.ResourcePoolModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.VDSInfoModel;
import com.ca.arcflash.ui.client.model.VMNetworkConfigGridModel;
import com.ca.arcflash.ui.client.model.VMNetworkConfigInfoModel;
import com.ca.arcflash.ui.client.model.VMNetworkStandardConfigInfoModel;
import com.ca.arcflash.ui.client.model.VMStorage;
import com.ca.arcflash.ui.client.model.VMVolumeModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.model.vDSPortGroupModel;
import com.ca.arcflash.ui.client.restore.PasswordPane;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.ca.arcflash.ui.client.restore.RestoreOptionsPanel;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
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
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class VMRecoveryOptionsPanel extends RestoreOptionsPanel{
	
	final LoginServiceAsync service = GWT.create(LoginService.class);
	
	private Radio originalLocation;
	private Radio alternateLocation;
	private RadioGroup locationGroup;
	
	private Radio httpProtocol;
	private Radio httpsProtocol;
	private RadioGroup protocolGroup;
	
	private TextField<String> vcTextField;
	private TextField<String> vmNameTextField;
	private NumberField portTextField;
	private TextField<String> usernameTextField;
	private PasswordTextField passwordTextField;
	
	private Button connectToVC;
	
	private ComboBox<ESXServerModel> esxList;
	
	private ComboBox<VMStorage> vmDataStore;
	
	private LabelField totalDiskSizelabel = null;
	
	//private LayoutContainer overwriteContainer;
	private CheckBox overwriteVM = null;
	private CheckBox generateNewVMInstanceID = null;//<huvfe01>###
	private CheckBox powerOn;
	private boolean isOverwriteIgnore = false;
	
	private ColumnModel datastoreColumnModel;
	private ListStore<DataStoreModel> datastoreStore = new ListStore<DataStoreModel>();
	private Grid<DataStoreModel> datastoreGrid;
	
	private VMRecoveryOptionsPanel thisPanel;
	
	private BackupVMModel backupVMModel;
	
	private VMStorage[] vmStorages;

	private LayoutContainer renderVirtualMachineContainer;

	private LayoutContainer setupDatastoreTableContainer = null;
	
	private ResourcePoolPanel poolPanel;
	
	private AvalibaleNetworkConfigHelper configHelper = new AvalibaleNetworkConfigHelper();
	private ListStore<VMNetworkConfigGridModel> networkStore = new ListStore<VMNetworkConfigGridModel>();
	private Grid<VMNetworkConfigGridModel> networkGrid;
	private String netWorkNotConnected = UIContext.Constants.hyperVNetworkNotConnect();
	
	private MyTextMetrics comboboxMetrics = new MyTextMetrics();
	
	private HyperVAlternativeLocationPanel hyperVOptionPanel;
	
	private LayoutContainer standaloneVMOptionsContainer;
	private VAppRecoveryOptionsWizard vAppOptionsWizard;
	
	public VMRecoveryOptionsPanel(RestoreWizardContainer restoreWizardWindow) {
		super(restoreWizardWindow);
		thisPanel = this;
		comboboxMetrics.bind();
	}
	
	public boolean isNewVM(BackupVMModel newBackupVMModel){
		if ((backupVMModel == null) ||
			(!newBackupVMModel.getVmInstanceUUID().equalsIgnoreCase(backupVMModel.getVmInstanceUUID())) ||
			(!newBackupVMModel.getVMName().equalsIgnoreCase(backupVMModel.getVMName())) ||
			(newBackupVMModel.getVMType() != backupVMModel.getVMType())){
				return true;
		}
		
		return false;
	}
	
	public void updateConflictLabel() {//<huvfe01>### preserve the instance UUID
		if ((null != overwriteVM) && (null != generateNewVMInstanceID)){
			overwriteVM.setBoxLabel(UIContext.Constants.resolvingConflictsOverwriteExistingVM());
			Utils.addToolTip(overwriteVM, UIContext.Constants.resolvingConflictsOverwriteExistingVMToolTip());
			
			generateNewVMInstanceID.setReadOnly(false);
			generateNewVMInstanceID.enable();

			if (RestoreContext.getBackupVMModel().getVMType() != BackupVMModel.Type.VMware
					.ordinal()) {
				generateNewVMInstanceID.setValue(true);
				generateNewVMInstanceID.setReadOnly(true);
				generateNewVMInstanceID.disable();
			}

			generateNewVMInstanceID.setBoxLabel(UIContext.Constants
					.resolvingConflictsGenerateNewVMInstanceID());

			Utils.addToolTip(generateNewVMInstanceID, UIContext.Constants.resolvingConflictsGenerateNewVMInstanceIDToolTip());
		}		
	}
	
	@Override
	protected void onShow() {
		super.onShow();
		if ((backupVMModel==null) ||
			(!RestoreContext.getBackupVMModel().getVmInstanceUUID().equalsIgnoreCase(backupVMModel.getVmInstanceUUID())) ||
			(!RestoreContext.getBackupVMModel().getVMName().equalsIgnoreCase(backupVMModel.getVMName())) ||
			(RestoreContext.getBackupVMModel().getVMType() != backupVMModel.getVMType())){
						
			this.backupVMModel = RestoreContext.getBackupVMModel();
			initVCDefaultValue();
			originalLocation.setValue(true);
			generateNewVMInstanceID.setValue(false);
			overwriteVM.setValue(false);
			powerOn.setValue(false);
			
			esxList.getStore().removeAll();
			esxList.clear();
			vmDataStore.getStore().removeAll();
			vmDataStore.clear();
			esxList.setEmptyText("");
			vmDataStore.setEmptyText("");
			vmStorages = null;
			
			fillDataStoreGrid();
			fillNetworkGrid();
			
			enableStandaloneOptions(false);
		}
		
		this.backupVMModel = RestoreContext.getBackupVMModel();
		
		boolean isVApp = (backupVMModel != null && (backupVMModel.getVMType() == BackupVMModel.Type.VMware_VApp.ordinal()));
		showOptionsContent(isVApp);
	}
	
	private void showOptionsContent(boolean isVApp) {
		if (isVApp) {
			pwdPane.getEncrptPasswordlabel().setWidth("122px");
			pwdPane.getEncrptlabel().setVisible(false);
			standaloneVMOptionsContainer.setVisible(false);
			vAppOptionsWizard.setVisible(true);
			if (vAppOptionsWizard.isRendered()) {
				vAppOptionsWizard.repaint();
			}
		} else {
			pwdPane.getEncrptPasswordlabel().setWidth("20px");
			pwdPane.getEncrptlabel().setVisible(true);
			standaloneVMOptionsContainer.setVisible(true);
			vAppOptionsWizard.setVisible(false);
			showStandaloneVMOptionsContent();
		}
	}
	
	public PasswordPane getSessionPwdPanel() {
		return pwdPane;
	}
	
	public RestoreWizardContainer getRestoreWizardContainer() {
		return restoreWizardWindow;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		backupVMModel = RestoreContext.getBackupVMModel();
		
		standaloneVMOptionsContainer = new LayoutContainer();
		standaloneVMOptionsContainer.ensureDebugId("7f8ae638-31d1-4860-b01a-a094a9263575");
		this.add(standaloneVMOptionsContainer);
		
		vAppOptionsWizard = new VAppRecoveryOptionsWizard(this);
		vAppOptionsWizard.ensureDebugId("8a8c4e6d-2b2a-4149-bdad-6ec976403c72");
		this.add(vAppOptionsWizard);
		
		TableLayout tl = new TableLayout();
		tl.setWidth("95%");
		tl.setHeight("100%");
		tl.setColumns(3);
		tl.setCellPadding(2);
		tl.setCellSpacing(2);		
		this.standaloneVMOptionsContainer.setLayout(tl);
		TableData td = new TableData();
		td.setColspan(3);
		td.setWidth("100%");
		
		// Header Section
		this.standaloneVMOptionsContainer.add(renderHeaderSection(), td);

		// Destination Section
		Label label = new Label(UIContext.Constants.restoreDestination());
		label.addStyleName("restoreWizardSubItem");
		this.standaloneVMOptionsContainer.add(label, td);

		label = new Label(UIContext.Constants.restoreDestinationDescription());
		label.addStyleName("restoreWizardSubItemDescription");
		this.standaloneVMOptionsContainer.add(label, td);

		originalLocation = new Radio();
		originalLocation.ensureDebugId("106570DD-C107-40de-9E7E-01FEF2839808");
		originalLocation.setBoxLabel(UIContext.Constants
				.restoreToOriginalLocation());
		Utils.addToolTip(originalLocation, UIContext.Constants
				.recoverToOriginalLocationToolTip());
		originalLocation.setValue(true);
		//originalLocation.setTabIndex(1);
		originalLocation.focus();

		//originalLocation.addStyleName("restoreWizardLeftSpacing");
		this.standaloneVMOptionsContainer.add(originalLocation, td);
		
		renderVirtualMachineContainer=renderVC();
		
		setupDatastoreTableContainer = renderDataStore();
		
		//hyperVOptionPanel = new HyperVAlternativeLocationPanel(this);
		//fix for 209742
		hyperVOptionPanel = new HyperVAlternativeLocationPanel(this,this.restoreWizardWindow);
		hyperVOptionPanel.setVisible(false);

		alternateLocation = new Radio();
		alternateLocation.ensureDebugId("5BE418EB-BB20-4968-A2CC-0F87D5BFC697");
		alternateLocation.setBoxLabel(UIContext.Constants.vmRecoverytoAltLoc());
		Utils.addToolTip(alternateLocation, UIContext.Constants.vmRecoverytoAltLocToolTip());
		//alternateLocation.setTabIndex(2);
		alternateLocation.addListener(Events.Change,
				new Listener<FieldEvent>() {
					@Override
					public void handleEvent(FieldEvent be) {
						showStandaloneVMOptionsContent();
					}
				});
		//alternateLocation.addStyleName("restoreWizardLeftSpacing");
		//alternateLocation.setValue(false);			
		alternateLocation.fireEvent(Events.Change);
		this.standaloneVMOptionsContainer.add(alternateLocation,td);

		locationGroup = new RadioGroup();
		locationGroup.setOrientation(Orientation.VERTICAL);
		locationGroup.add(originalLocation);
		locationGroup.add(alternateLocation);		
		
		this.standaloneVMOptionsContainer.add(renderVirtualMachineContainer,td);	
		
		this.standaloneVMOptionsContainer.add(setupDatastoreTableContainer, td);
		
		this.standaloneVMOptionsContainer.add(hyperVOptionPanel, td);
		
		renderOverWrite(this.standaloneVMOptionsContainer, td);
		//this.standaloneVMOptionsContainer.add(overwriteContainer,td);
		
		this.standaloneVMOptionsContainer.add(new HTML("<hr>"), td);
		
		label = new Label(UIContext.Constants.postRecover());
		label.addStyleName("restoreWizardSubItem");
		this.standaloneVMOptionsContainer.add(label, td);
		
		powerOn = new CheckBox();
		powerOn.ensureDebugId("2FBDCDA0-D7FD-48da-B9B3-4398CB9520D6");
		/*if(originalLocation.getValue()==true){
			powerOn.setTabIndex(4);
		}else{
			powerOn.setTabIndex(16);
		}*/
		powerOn.setBoxLabel(UIContext.Constants.postRecoverPowerOnVM());
		Utils.addToolTip(powerOn, UIContext.Constants.postRecoverPowerOnVMToolTip());
		this.standaloneVMOptionsContainer.add(powerOn, td);
		this.standaloneVMOptionsContainer.add(new HTML("<hr>"), td);
		
		pwdPane.setWidth("95%");
		this.add(pwdPane, td);
		initVCDefaultValue();
		
		enableStandaloneOptions(false);
		showOptionsContent(backupVMModel != null && (backupVMModel.getVMType() == BackupVMModel.Type.VMware_VApp.ordinal()));
	}
	
	private void enableStandaloneOptions(boolean isEnable){
		if (isEnable){
			esxList.enable();			
			vmDataStore.enable();
			datastoreGrid.enable();
			networkGrid.enable();
		}else{
			esxList.disable();			
			vmDataStore.disable();
			datastoreGrid.disable();
			networkGrid.disable();
		}
	}
	
	private void showStandaloneVMOptionsContent() {
		hideVMwareOptions();
		hyperVOptionPanel.setVisible(false);
		
		if (RestoreContext.getBackupVMModel().getVMType() == BackupVMModel.Type.VMware.ordinal()){
			if(alternateLocation.getValue()){
				showVMwareOptions();
			}else{
				hideVMwareOptions();
			}
		}else{
			hyperVOptionPanel.setVisible(alternateLocation.getValue());
		}
	}
	
	private void hideVMwareOptions() {
		/*if(overwriteVM!=null){
			overwriteVM.setTabIndex(3);
		}
		if(powerOn!=null){
			powerOn.setTabIndex(4);
		}*/
		
		renderVirtualMachineContainer.setVisible(false);
		setupDatastoreTableContainer.setVisible(false);
		//overwriteContainer.setVisible(true);
//		esxList.disable();			
//		vmDataStore.disable();
//		datastoreGrid.disable();
//		networkGrid.disable();
	}

	private void showVMwareOptions() {
		//overwriteVM.setTabIndex(15);
		//powerOn.setTabIndex(16);
		renderVirtualMachineContainer.setVisible(true);
		setupDatastoreTableContainer.setVisible(true);
		//overwriteContainer.setVisible(false);
//		esxList.disable();	
//		vmDataStore.disable();
//		datastoreGrid.disable();
//		networkGrid.disable();
	}
	
	private void renderOverWrite(LayoutContainer container, TableData td){
//		TableLayout tl = new TableLayout();
//		//tl.setCellPadding(4);
//		tl.setCellSpacing(4);
//		tl.setColumns(3);
//		tl.setWidth("100%");
		
		LayoutContainer overwriteContainer = container;
		//overwriteContainer = new LayoutContainer();
		//overwriteContainer.setLayout(tl);
		
//		TableData td = new TableData();
//		td.setColspan(3);
//		td.setWidth("100%");
		
		overwriteContainer.add(new HTML("<hr>"), td);
		
		Label label = new Label(UIContext.Constants.resolvingConflicts());
		label.addStyleName("restoreWizardSubItem");
		overwriteContainer.add(label, td);
		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == 1){
			productName = UIContext.productNamevSphere;
		}
		label = new Label(UIContext.Messages.resolvingConflictsDescription());
		label.addStyleName("restoreWizardSubItemDescription");
		overwriteContainer.add(label, td);
		
		overwriteVM = new CheckBox();
		overwriteVM.ensureDebugId("F07B01AF-7918-4a9c-9CFC-2FD07D2933B8");
		
		//<huvfe01>### preserve the instance UUID
		generateNewVMInstanceID = new CheckBox();
		generateNewVMInstanceID.ensureDebugId("27145084-B6C8-45AB-AC7C-48FC1AC2BAAD");
		
		updateConflictLabel();
		/*if(originalLocation.getValue()==true){
			overwriteVM.setTabIndex(3);
		}else{
			overwriteVM.setTabIndex(15);
		}*/
				
//		overwriteVM.setBoxLabel(UIContext.Constants.resolvingConflictsOverwriteExistingVM());
//		Utils.addToolTip(overwriteVM, UIContext.Constants.resolvingConflictsOverwriteExistingVMToolTip());
		overwriteContainer.add(overwriteVM, td);
		
//		generateNewVMInstanceID.setValue(false);
//		generateNewVMInstanceID.setReadOnly(false);
//		generateNewVMInstanceID.enable();
//		
//		if (RestoreContext.getBackupVMModel().getVMType() != BackupVMModel.Type.VMware.ordinal()){
//			generateNewVMInstanceID.setValue(true);
//			generateNewVMInstanceID.setReadOnly(true);
//			generateNewVMInstanceID.disable();
//		}
//		
//		generateNewVMInstanceID.setBoxLabel(UIContext.Constants.resolvingConflictsGenerateNewVMInstanceID());
//		
//		Utils.addToolTip(generateNewVMInstanceID, UIContext.Constants.resolvingConflictsGenerateNewVMInstanceIDToolTip());
		
		overwriteContainer.add(generateNewVMInstanceID, td);
		
		//return overwriteContainer;
	}
	
	
	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_options()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreOptions());
		label.setStyleName("restoreWizardTitle");
		container.add(label);

		return container;
	}
	
	private LayoutContainer renderDataStore(){
		FieldSet fieldSet = new FieldSet();
		fieldSet.ensureDebugId("A864A7F5-58A7-4a39-95D3-A10EB989D9C9");
	    fieldSet.setHeadingHtml(UIContext.Constants.vmRecoveryOtherInfo());
	    
	    LayoutContainer container = new LayoutContainer();
	    setupDatastoreTable(container);
	    
	    fieldSet.add(container);
	    
	    return fieldSet;
	}

	private LayoutContainer renderVC(){
		FieldSet fieldSet = new FieldSet();  
		fieldSet.ensureDebugId("5E09302E-D56F-4835-997D-4341A8EDDCC3");
	    fieldSet.setHeadingHtml(UIContext.Constants.vmRecoveryVCInfo()); 
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(4);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		container.setLayout(tl);
		
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		td.setWidth("22%");
		TableData td35 = new TableData();
		td35.setWidth("28%");
		td35.setVerticalAlign(VerticalAlignment.MIDDLE);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryVirtualCenterLabel());
		container.add(label,td);
		
		vcTextField = new TextField<String>();
		vcTextField.ensureDebugId("AD8EF69A-A53D-47a1-8FFF-C6CA0D24874F");
		vcTextField.setWidth(150);
		vcTextField.setAllowBlank(false);
		vcTextField.setValidateOnBlur(false);
		//vcTextField.setTabIndex(3);
		container.add(vcTextField, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryProtocolLabel());
		td.setStyle("padding-top:0");
		container.add(label,td);
		
		LayoutContainer protocolContainer = new LayoutContainer();
		TableLayout protocolTL = new TableLayout();
		protocolTL.setWidth("100%");
		protocolTL.setColumns(2);
		protocolContainer.setLayout(protocolTL);
		
		TableData proData = new TableData();
		proData.setWidth("50%");
		
		httpProtocol = new Radio();
		httpProtocol.ensureDebugId("614E4AFC-E9B8-4c7a-8494-557C63E72A25");
		httpProtocol.setHideLabel(true);
		//httpProtocol.setTabIndex(7);
		httpProtocol.setBoxLabel(UIContext.Constants.vmRecoveryProtocolHttp());		
		httpProtocol.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(httpProtocol.getValue()==true){
					portTextField.setValue(80);
				}
			}
		});
		protocolContainer.add(httpProtocol, proData);
		httpsProtocol = new Radio();
		httpsProtocol.ensureDebugId("7B73F796-E9C9-408b-8CD9-68D265D555DE");
		//httpsProtocol.setTabIndex(8);
		httpsProtocol.setHideLabel(true);
		httpsProtocol.setBoxLabel(UIContext.Constants.vmRecoveryProtocolHttps());	
		httpsProtocol.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(httpsProtocol.getValue()==true){
					portTextField.setValue(443);
				}
			}
		});
		//httpsProtocol.setValue(true);
		protocolContainer.add(httpsProtocol, proData);
		
		protocolGroup = new RadioGroup();
		protocolGroup.add(httpProtocol);
		protocolGroup.add(httpsProtocol);
		
		container.add(protocolContainer, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryUsernameLabel());
		container.add(label,td);
		
		usernameTextField = new TextField<String>();
		usernameTextField.ensureDebugId("05413BFC-6FF4-4978-8A19-127850D1E733");
		usernameTextField.setWidth(150);
		usernameTextField.setAllowBlank(false);
		usernameTextField.setValidateOnBlur(false);
		//usernameTextField.setValue("administrator");
		//usernameTextField.setTabIndex(4);
		container.add(usernameTextField, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryPortLabel());
		container.add(label,td);
		
		portTextField = new NumberField();
		portTextField.ensureDebugId("97E8E55A-9A43-43ec-9412-5B1AC4453067");
		portTextField.setWidth(100);
		portTextField.setAllowBlank(false);
		portTextField.setValidateOnBlur(false);
		portTextField.setMinValue(0);
		portTextField.setValue(443);
		//portTextField.setTabIndex(9);
		container.add(portTextField, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryPasswordLabel());
		container.add(label,td);
		
		passwordTextField = new PasswordTextField();
		passwordTextField.ensureDebugId("D13F0D04-9D3D-416a-B030-973109C8DABB");
		passwordTextField.setWidth(150);
		passwordTextField.setPassword(true);
		//passwordTextField.setAllowBlank(false);
		passwordTextField.setValidateOnBlur(false);
		//passwordTextField.setTabIndex(5);
		container.add(passwordTextField, td35);
		
//		label = new LabelField();
//		label.setText("");
//		container.add(label,td);
//		
//		label = new LabelField();
//		label.setText("");
//		container.add(label,td35);
		
		TableData tb1 =new TableData();
		tb1.setColspan(2);
		tb1.setWidth("50%");
		
//		label = new LabelField();
//		label.setText("");
//		container.add(label,td);
		
		connectToVC = new Button();
		connectToVC.ensureDebugId("D75DF975-1055-4442-A02C-D9FE9745401C");
		//connectToVC.setTabIndex(10);
		connectToVC.setText(UIContext.Constants.vmRecoveryConnectToVCButton());
		connectToVC.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
			    esxList.setEmptyText(UIContext.Constants.vmRecoveryEsxServerLoading());
			    
				vmDataStore.setEmptyText(UIContext.Constants.vmRecoveryEsxServerLoading());
				
				datastoreGrid.mask(UIContext.Constants.vmRecoveryEsxServerLoading());
				datastoreGrid.disable();
				
				networkGrid.mask(UIContext.Constants.restoreVDSNetworkLoading());
				networkGrid.disable();
				
				if(!thisPanel.portTextField.validate()){
					esxList.setEmptyText("");	
					vmDataStore.setEmptyText("");
					datastoreGrid.unmask();
					networkGrid.unmask();
					return;
				}
				if(!thisPanel.vcTextField.validate()){
					esxList.setEmptyText("");
					vmDataStore.setEmptyText("");
					datastoreGrid.unmask();
					networkGrid.unmask();
					return;
				}
				if(!thisPanel.httpsProtocol.validate()){
					esxList.setEmptyText("");	
					vmDataStore.setEmptyText("");
					datastoreGrid.unmask();
					networkGrid.unmask();
					return;
				}
				if(!thisPanel.usernameTextField.validate()){
					esxList.setEmptyText("");	
					vmDataStore.setEmptyText("");
					datastoreGrid.unmask();
					networkGrid.unmask();
					return;
				}
				if(!thisPanel.passwordTextField.validate()){
					esxList.setEmptyText("");	
					vmDataStore.setEmptyText("");
					datastoreGrid.unmask();
					networkGrid.unmask();
					return;
				}
				
				String vcName = vcTextField.getValue();
				int port = portTextField.getValue().intValue();
				String protocol = httpsProtocol.getValue()==true?"https":"http";
				String username = usernameTextField.getValue();
				String password = passwordTextField.getValue();
				if(password == null)
					password = "";
				final VirtualCenterModel vcModel = new VirtualCenterModel();
				vcModel.setPassword(password);
				vcModel.setUsername(username);
				vcModel.setPort(port);
				vcModel.setProtocol(protocol);
				vcModel.setVcName(vcName);
				connectToVC.setEnabled(false);
				service.validateVC(vcModel, new BaseAsyncCallback<Integer>(){
					@Override
					public void onFailure(Throwable caught){
						connectToVC.setEnabled(true);
						super.onFailure(caught);
						esxList.setEmptyText("");	
						vmDataStore.setEmptyText("");
						//esxList.getStore().removeAll();
						//vmDataStore.getStore().removeAll();
						esxList.disable();
						vmDataStore.disable();
						datastoreGrid.unmask();
						datastoreGrid.disable();
						networkGrid.unmask();
						networkGrid.disable();
					}
					
					@Override
					public void onSuccess(Integer result){
						//connectToVC.setEnabled(true);
						String productName = UIContext.productNameD2D;
						if(UIContext.uiType == 1){
							productName = UIContext.productNamevSphere;
						}
						if(result!=0){
							MessageBox msg = new MessageBox();
							msg.setIcon(MessageBox.ERROR);
							msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
							msg.setMessage(UIContext.Constants.messageBoxConnectVCFail());
							msg.setModal(true);
							Utils.setMessageBoxDebugId(msg);
							msg.show();
						}else{
							service.getESXServer(vcModel, new BaseAsyncCallback<List<ESXServerModel>>(){

								@Override
								public void onFailure(Throwable caught) {
									super.onFailure(caught);
									esxList.getStore().removeAll();
									vmDataStore.getStore().removeAll();
									datastoreGrid.unmask();
									networkGrid.unmask();
								}

								@Override
								public void onSuccess(List<ESXServerModel> result) {
									if(result!=null){
										vmNameTextField.setReadOnly(false);
										Collections.sort(result);//749333
										esxList.getStore().removeAll();										
										esxList.getStore().add(result);
										
										//select the esx host
										boolean bValueSet = false;
										for (ESXServerModel esx : result){
											if (0 == esx.getESXName().compareToIgnoreCase(backupVMModel.getSubVMEsxHost())){
												esxList.setValue(esx);
												bValueSet = true;
												break;
											}
										}
										if (!bValueSet){
											esxList.setValue(result.get(0));
										}
										
										//huobe01, fix T5E5168 for issue 20879014
										//We needn't to call initDataStoreTable(=>getVMwareDataStore) in validateVC 
										//because setupDatastoreTable=>selectionChanged=>getVMwareDataStore do the same thing
										//Call getVMwareDataStore twice will hang GUI if there are lots of datastores 
										//30+ datastores will hang the GUI for several minutes
										//initDataStoreTable(vcModel,result.get(0));
										esxList.enable();
										datastoreGrid.unmask();
										datastoreGrid.enable();
										networkGrid.unmask();
										networkGrid.enable();
									}else {
										MessageBox msg = new MessageBox();
										msg.setIcon(MessageBox.ERROR);
										msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
										msg.setMessage(UIContext.Constants.messageBoxConnectVCFail());
										msg.setModal(true);
										Utils.setMessageBoxDebugId(msg);
										msg.show();
										esxList.setEmptyText("");	
										vmDataStore.setEmptyText("");
										//esxList.getStore().removeAll();
										//vmDataStore.getStore().removeAll();
										esxList.disable();
										vmDataStore.disable();
										datastoreGrid.unmask();
										datastoreGrid.disable();
										networkGrid.unmask();
										networkGrid.disable();
									}
								}
								
							});
						}
						connectToVC.setEnabled(true);
					}
				});
				
			}
			
		});
		container.add(connectToVC,tb1);
		fieldSet.add(container);
		return fieldSet;
	}
	
	private void initVCDefaultValue(){
		if(backupVMModel!=null){

			vcTextField.setValue(backupVMModel.getEsxServerName());
			
			if(backupVMModel.getProtocol().equalsIgnoreCase("http")){
				httpProtocol.setValue(true);
			}else{
				httpsProtocol.setValue(true);
			}
			
			usernameTextField.setValue(backupVMModel.getEsxUsername());
			portTextField.setValue(backupVMModel.getPort());
			vmNameTextField.setValue(backupVMModel.getVMName());
			
			//usernameTextField.clear();
			passwordTextField.clear();
		}
	}
	
	private void initDataStoreTable(VirtualCenterModel vcModel,ESXServerModel serverModel){
		service.getVMwareDataStore(vcModel, serverModel, new BaseAsyncCallback<VMStorage[]>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(VMStorage[] result) {
				if(result !=null){
					vmDataStore.enable();
					vmStorages = result;
					vmDataStore.getStore().removeAll();
					for(int i =0 ;i<result.length;i++){
						VMStorage storage = result[i];
						storage.setDisplayName(UIContext.Messages.vSphereDatastoreFreeSize(storage.getName(), Utils.bytes2String(storage.getFreeSize())));
						vmStorages[i] = storage;
						vmDataStore.getStore().add(storage);
					}
					vmDataStore.setValue(vmDataStore.getStore().getAt(0));
					List<DataStoreModel> datastoreList = new ArrayList<DataStoreModel>();
					if(backupVMModel.diskList!=null){
						for(DiskModel diskModel : backupVMModel.diskList){
							datastoreList.add(new DataStoreModel(diskModel));
						}
					}
					datastoreGrid.getStore().removeAll();
					datastoreGrid.getStore().add(datastoreList);
				}
				
			}
			
		});
	}
	
	private String getDataStoreNameFromURL(String url){
		String vmDataStore = "";
		if (null != url){			
			int pos1 = url.indexOf("[");
			int pos2 = url.indexOf("]");
			if ((0 == pos1) && (pos2 > 1)){
				vmDataStore = new String(url.substring(pos1 + 1, pos2));
			}
		}
		
		return vmDataStore;
	}
	
	private void setupDatastoreTable(LayoutContainer container) {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig sourcedisk = Utils.createColumnConfig("diskNumber",UIContext.Constants.vmRecoveryDataStoreGridSoruceDiskColumn(), 80, null);
		ColumnConfig diskSize = Utils.createColumnConfig("diskSize",UIContext.Constants.vmRecoveryDataStoreGridDiskSizeColumn(), 60, null);
		ColumnConfig sourcevolumes = Utils.createColumnConfig("volumes",UIContext.Constants.vmRecoveryDataStoreGridSoruceVolumesColumn(), 100,	null);
		ColumnConfig datastoreLable = Utils.createColumnConfig("esxDataStoreComboBox", UIContext.Constants.vmRecoveryDataStoreGridDataStoreColumn(), 150, null);
		ColumnConfig diskFormatOption = Utils.createColumnConfig("diskFormatOption", UIContext.Constants.recoverVMHyperVDiskTypeLabel(), 160, null);

		GridCellRenderer<DataStoreModel> sourceDiskRenderer = new GridCellRenderer<DataStoreModel>() {

			@Override
			public Object render(DataStoreModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataStoreModel> store, Grid<DataStoreModel> grid) {
				DiskModel diskModel=model.getDiskModel();
				String stringDisk = UIContext.Constants.vmRecoveryDisk() + diskModel.getDiskNumber();
				return stringDisk;
			}
		};
		sourcedisk.setRenderer(sourceDiskRenderer);
		
		GridCellRenderer<DataStoreModel> diskSizeRenderer = new GridCellRenderer<DataStoreModel>() {
			
			@Override
			public Object render(DataStoreModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataStoreModel> store, Grid<DataStoreModel> grid) {
				DiskModel diskModel=model.getDiskModel();
				if(diskModel!=null && diskModel.getSize()!=null){
					return Utils.bytes2String(diskModel.getSize());
				}
				//return getImageHtml("disk.gif")+stringDisk;
				return "";
			}
		};
		diskSize.setRenderer(diskSizeRenderer);
		
		GridCellRenderer<DataStoreModel> sourceVolumesRenderer = new GridCellRenderer<DataStoreModel>() {

			@Override
			public Object render(DataStoreModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataStoreModel> store, Grid<DataStoreModel> grid) {
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
		
		GridCellRenderer<DataStoreModel> datastoRenderer = new GridCellRenderer<DataStoreModel>() {

			@Override
			public Object render(DataStoreModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataStoreModel> store, Grid<DataStoreModel> grid) {
				ComboBox<ModelData> esxDatastore= model.getEsxDataStoreComboBox();
				esxDatastore.setWidth(180);
				esxDatastore.setAllowBlank(false);
				esxDatastore.setTriggerAction(TriggerAction.ALL);
				esxDatastore.ensureDebugId("esxDatastoreComboBox-"+rowIndex+"-"+colIndex);
				esxDatastore.setEditable(false);
				esxDatastore.setDisplayField("DisplayName");
				esxDatastore.setTemplate(getTemplateForGrid());
				ListStore<ModelData> listStore=esxDatastore.getStore();
				if (vmStorages == null) {
					GWT.log("vmStorages is null");
					return esxDatastore;
				}
				
				int defaultDataStoreIndexToSelect = 0;
				String defaultDataStoreToSelect = getDataStoreNameFromURL(model.getDiskModel().getDiskUrl());
				for (int i = 0; i < vmStorages.length; i++) {
					ModelData baseModel=new BaseModelData();
					baseModel.set("name", vmStorages[i].getName());
					/*baseModel.set("DisplayName", vmStorages[i].getName()+
							"("+vmStorages[i].getFreeSize()/GB+" GB Free)");*/
					baseModel.set("DisplayName", vmStorages[i].getDisplayName());
					baseModel.set("freeSize", vmStorages[i].getFreeSize());
					listStore.add(baseModel);
					
					//auto match vm storage
					if (defaultDataStoreToSelect.equalsIgnoreCase(vmStorages[i].getName())){
						defaultDataStoreIndexToSelect = i;
					}
				}
				
				if (vmStorages.length > 0) {
					esxDatastore.setValue(listStore.getAt(defaultDataStoreIndexToSelect));
				}
				
				//setDefaultDatastoreConfigured(esxDatastore,model.getDiskModel().getDiskNumber());
				
				return esxDatastore;
			}
			
		};
		datastoreLable.setRenderer(datastoRenderer);
		
		GridCellRenderer<DataStoreModel> diskFormatOptionRenderer = new GridCellRenderer<DataStoreModel>() {
			@Override
			public Object render(DataStoreModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<DataStoreModel> store, Grid<DataStoreModel> grid) {
				return model.getDiskTypeComboBox(model.getDiskModel());
			}
		};
		diskFormatOption.setRenderer(diskFormatOptionRenderer);

		configs.add(sourcedisk);
		configs.add(diskSize);
		configs.add(sourcevolumes);
		configs.add(diskFormatOption);
		configs.add(datastoreLable);

		datastoreColumnModel = new ColumnModel(configs);
		
		// datastoreGrid.ensureDebugId("7f45b322-e910-4d64-bdb6-94a9ada202fd");
		datastoreGrid = new Grid<DataStoreModel>(datastoreStore,
				datastoreColumnModel);
		datastoreGrid.setTrackMouseOver(false);
		datastoreGrid.setAutoExpandColumn("esxDataStoreComboBox");
		datastoreGrid.setAutoWidth(true);
		datastoreGrid.getSelectionModel()
				.setSelectionMode(SelectionMode.SINGLE);
		datastoreGrid.setBorders(true);
		datastoreGrid.setColumnLines(true);
//				datastoreGrid.setWidth(500);
		datastoreGrid.setHeight(150);
		//datastoreGrid.setTabIndex(14);
				
		TableLayout tl = new TableLayout();
		tl.setCellPadding(2);
		tl.setColumns(2);
		tl.setWidth("100%");
		container.setLayout(tl);
		
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		td.setWidth("22%");
		
		TableData td1 = new TableData();
		td1.setVerticalAlign(VerticalAlignment.MIDDLE);
		td1.setWidth("78%");
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryVMNameLabel());
		container.add(label,td);
		
		vmNameTextField = new TextField<String>();
		vmNameTextField.ensureDebugId("31CD3FBE-5DFC-4d4e-9ADB-21472128F1C1");
		vmNameTextField.setWidth(200);
		vmNameTextField.setAllowBlank(false);
		vmNameTextField.setValidateOnBlur(false);
		vmNameTextField.setReadOnly(true);
		//vmNameTextField.setTabIndex(11);
		//vmNameTextField.setValue(backupVMModel.getVMName());
		container.add(vmNameTextField, td1);
		
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryEsxServerLabel());
		container.add(label,td);
		
		esxList = new ComboBox<ESXServerModel>();
		esxList.ensureDebugId("B53CBD93-B322-464c-A199-738650803C1A");
		//esxList.setTabIndex(12);
		esxList.setWidth(200);
		esxList.setDisplayField("esxName");
		esxList.setAllowBlank(false);
		esxList.setValidateOnBlur(false);
		esxList.setEditable(false);
		esxList.setTriggerAction(TriggerAction.ALL);
		esxList.setStore(new ListStore<ESXServerModel>());
		esxList.addSelectionChangedListener(new SelectionChangedListener<ESXServerModel>(){

			@Override
			public void selectionChanged(
					SelectionChangedEvent<ESXServerModel> se) {
				VirtualCenterModel vcModel = getVCModelFromNewDest();
				vmDataStore.setEmptyText(UIContext.Constants.vmRecoveryEsxServerLoading());
				datastoreGrid.mask(UIContext.Constants.vmRecoveryEsxServerLoading());
				poolPanel.setVcModel(vcModel);
				poolPanel.setEsxServerModel(se.getSelectedItem());
				service.getResoucePool(vcModel, se.getSelectedItem(),null, new BaseAsyncCallback<List<ResourcePoolModel>>(){
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						//datastoreGrid.unmask();
					}
					
					@Override
					public void onSuccess(List<ResourcePoolModel> result) {
						if(result !=null && result.size()>0){
							poolPanel.enable();
						}
					}
					
				});
				
				service.getVMwareDataStore(vcModel, se.getSelectedItem(), new BaseAsyncCallback<VMStorage[]>(){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						datastoreGrid.unmask();
					}

					@Override
					public void onSuccess(VMStorage[] result) {
						if(result !=null){
							Arrays.sort(result);
							vmDataStore.enable();
							vmStorages = result;
							vmDataStore.getStore().removeAll();
							
							int vmStorageIndexToSelect = 0;
							String vmStorageToSelect = getDataStoreNameFromURL(backupVMModel.getVmVMX());
							for(int i =0 ;i<result.length;i++){
								VMStorage storage = result[i];
								storage.setDisplayName(UIContext.Messages.vSphereDatastoreFreeSize(storage.getName(), Utils.bytes2String(storage.getFreeSize())));
								vmStorages[i] = storage;
								vmDataStore.getStore().add(storage);
								
								//auto match vm storage
								if (vmStorageToSelect.equalsIgnoreCase(vmStorages[i].getName())){
									vmStorageIndexToSelect = i;
								}
							}
							vmDataStore.setValue(vmDataStore.getStore().getAt(vmStorageIndexToSelect));
							
							List<DataStoreModel> datastoreList = new ArrayList<DataStoreModel>();
							if(backupVMModel.diskList != null){
								for(DiskModel diskModel : backupVMModel.diskList){
									datastoreList.add(new DataStoreModel(diskModel));
								}
							}
							datastoreGrid.getStore().removeAll();
							datastoreGrid.getStore().add(datastoreList);
							datastoreGrid.unmask();
						}
						
					}
					
				});
			
				retrieveConfigList(vcModel, se.getSelectedItem());
			}
			
		});
		container.add(esxList, td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryResourcePool());
		container.add(label,td);
		
		poolPanel = new ResourcePoolPanel();
		poolPanel.disable();
		poolPanel.setPoolWidth(200);
		container.add(poolPanel,td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryVMDataStoreLabel());
		container.add(label,td);

		vmDataStore = new ComboBox<VMStorage>();
		vmDataStore.ensureDebugId("ADB53D7C-9EDF-49ab-B7A9-FF524ECFB34D");
		//vmDataStore.setTabIndex(13);
		vmDataStore.setWidth(200);
		vmDataStore.setDisplayField("displayName");
		vmDataStore.setAllowBlank(false);
		vmDataStore.setValidateOnBlur(false);
		vmDataStore.setTriggerAction(TriggerAction.ALL);
		vmDataStore.setEditable(false);
		vmDataStore.setStore(new ListStore<VMStorage>());
		vmDataStore.setTemplate(getTemplate());
		container.add(vmDataStore, td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryDataStoreLabel());
		container.add(label, td);
		
		TableData td2 = new TableData();
		td2.setHorizontalAlign(HorizontalAlignment.RIGHT);
		totalDiskSizelabel = new LabelField();		
		container.add(totalDiskSizelabel, td2);
		
		TableData spanTd = new TableData();
		spanTd.setColspan(2);
		container.add(datastoreGrid, spanTd);		
		
		label = new LabelField();
		label.setValue("");
		label.setReadOnly(true);
		label.disable();
		container.add(label,td);
		
		label = new LabelField();
		label.setValue("");
		label.setReadOnly(true);
		label.disable();
		container.add(label,td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreVDSNetworkLable());
		container.add(label,spanTd);
		
		renderNetworkGrid();
		container.add(networkGrid, spanTd);
		
		fillDataStoreGrid();
	}
	
	private void fillDataStoreGrid(){
		
		List<DataStoreModel> datastoreList = new ArrayList<DataStoreModel>();
		long total_disk_size = 0;
		if(backupVMModel.diskList!=null){
			for(DiskModel diskModel : backupVMModel.diskList){
				datastoreList.add(new DataStoreModel(diskModel));
				total_disk_size+=diskModel.getSize();
			}
		}
		
		datastoreGrid.getStore().removeAll();
		datastoreGrid.getStore().add(datastoreList);

		totalDiskSizelabel.setValue(UIContext.Constants.vmRecoveryTotalSourceDiskSize()+Utils.bytes2String(total_disk_size));
	    
	}
		
	private void renderNetworkGrid() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig adapter = Utils.createColumnConfig("label", UIContext.Constants.restoreVDSAdapterLabel(), 200, null);
		ColumnConfig avaliableConfig = Utils.createColumnConfig("configComboBox", UIContext.Constants.restoreVDSConfigLable(), 240, null);

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
				ComboBox<BaseModelData> configComboBox = model.getAvaliableConfigInfoComboBox();
				configComboBox.setWidth(245);
				configComboBox.setAllowBlank(true);
				configComboBox.setTriggerAction(TriggerAction.ALL);
				configComboBox.ensureDebugId("configComboBox-" + rowIndex + "-" + colIndex);
				configComboBox.setEditable(false);
				configComboBox.setDisplayField("displayName");
				configComboBox.setTemplate(getTemplate());
				ListStore<BaseModelData> listStore = configComboBox.getStore();
				listStore.removeAll();
				
				Map<String, NetworkConfigInCell> configMap = configHelper.getAllConfigCells();
				if (configMap.isEmpty()) {
					GWT.log("configHelper is empty");
					configComboBox.setMinListWidth(configComboBox.getMinListWidth());
					return configComboBox;
				}
								
                //add not connected option
				NetworkConfigInCell notConnect = new NetworkConfigInCell();
				notConnect.setSwitchUUID("");
				notConnect.setSwitchName("");
				notConnect.setPgKey("");
				notConnect.setPgName("");
				notConnect.setDisplayName(netWorkNotConnected);
				
				List<NetworkConfigInCell> cofigList = new ArrayList<NetworkConfigInCell>(configMap.values());
				Collections.sort(cofigList);
				cofigList.add(0, notConnect);
				
				listStore.add(cofigList);
				String name = getMatchedDiplayNameFromNetwork(model.getInfoModel(), cofigList);
				if (configMap.containsKey(name)) {
					configComboBox.setValue(configMap.get(name));
				} else {
					configComboBox.setValue(notConnect);
				}
				setDropdownListWidth(configComboBox, getLonggestConfigString(cofigList));
				
				return configComboBox;
			}
		};
		avaliableConfig.setRenderer(configRenderer);

		configs.add(adapter);
		configs.add(avaliableConfig);

		ColumnModel networkColumnModel = new ColumnModel(configs);
		networkGrid = new Grid<VMNetworkConfigGridModel>(networkStore, networkColumnModel);
		if (fillNetworkGrid()) {
			networkGrid.unmask();
		}
		networkGrid.setTrackMouseOver(false);
		networkGrid.setAutoExpandColumn("configComboBox");
		networkGrid.setAutoWidth(true);
		networkGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		networkGrid.setBorders(true);
		networkGrid.setColumnLines(true);
		networkGrid.setHeight(150);
	}
	
	private String getMatchedDiplayNameFromNetwork(VMNetworkConfigInfoModel model, List<NetworkConfigInCell> cellList) {
		String deviceName = model.getDeviceName();
		String switchUUID = model.getSwitchUUID();
		String pgKey = model.getPortgroupKey();
		String switchName = model.getSwitchName();
		String pgName = model.getPortgroupName();
		
		if (deviceName != null) {
			return deviceName;
		}
		
		if (switchUUID != null && pgKey != null) {
			for (NetworkConfigInCell cell : cellList) {
				if (switchUUID.equals(cell.getSwitchUUID()) && pgKey.equals(cell.getPgKey())) {
					return cell.getPgName() + "(" + cell.getSwitchName() + ")";
				}
			}
		}

		// if it failed to match with UUID, it will try to match with name
		if (switchName != null && pgName != null) {
			for (NetworkConfigInCell cell : cellList) {
				if (switchName.equals(cell.getSwitchName()) && pgName.equals(cell.getPgName())) {
					return cell.getPgName() + "(" + cell.getSwitchName() + ")";
				}
			}
		}
		
		return "";
	}
	
	private void retrieveConfigList(final VirtualCenterModel vcModel, final ESXServerModel esxModel) {
		networkGrid.mask(UIContext.Constants.restoreVDSNetworkLoading());
		configHelper.clear();
		if (!validateVCAndESXModel(vcModel, esxModel)) {
			return;
		}
		
		service.getVMwareServerType(vcModel.getVcName(), vcModel.getUsername(), vcModel.getPassword(), vcModel.getProtocol(), vcModel.getPort(), new BaseAsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				if (result == 2) {
					getConfigFromVCAndESX(vcModel, esxModel);
				} else if (result == 1) {
					getStandardConfigFromEsx(vcModel, esxModel);
				} else {
					configHelper.clear();
					fillNetworkGrid();
					networkGrid.unmask();
					GWT.log("Failed to get VMware server type.");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				configHelper.clear();
				fillNetworkGrid();
				networkGrid.unmask();
				GWT.log("Failed to get VMware server type.");
				super.onFailure(caught);
			}
		});
	}
	
	private void getConfigFromVCAndESX(final VirtualCenterModel vcModel, final ESXServerModel esxModel) {
		service.getVDSInfoList(vcModel, esxModel.getESXName(), new BaseAsyncCallback<List<VDSInfoModel>>() {
			@Override
			public void onSuccess(List<VDSInfoModel> result) {
				if (result != null && !result.isEmpty()) {
					configHelper.addVDSConfigs(result);
				}
				getStandardConfigFromEsx(vcModel, esxModel);
			}

			@Override
			public void onFailure(Throwable caught) {
				configHelper.clear();
				fillNetworkGrid();
				networkGrid.unmask();
				GWT.log("Failed to get vds and standard configurations from VCenter and ESX server.");
				super.onFailure(caught);
			}
		});
	}
	
	private void getStandardConfigFromEsx(VirtualCenterModel vcModel, ESXServerModel esxModel) {
		service.getStandardNetworkInfoList(vcModel, esxModel.getESXName(), new BaseAsyncCallback<List<VMNetworkStandardConfigInfoModel>>() {
			@Override
			public void onSuccess(List<VMNetworkStandardConfigInfoModel> result) {
				if (result != null && !result.isEmpty()) {
					configHelper.addStandardConfigs(result);
				}
				if (fillNetworkGrid()) {
					networkGrid.unmask();
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				configHelper.clear();
				fillNetworkGrid();
				networkGrid.unmask();
				GWT.log("Failed to get standard configurations from ESX server.");
				super.onFailure(caught);
			}
		});
	}
	
	private boolean validateVCAndESXModel(VirtualCenterModel vcModel, ESXServerModel esxModel) {
		if (vcModel == null || esxModel == null) {
			return false;
		}
		if (vcModel.getVcName() == null || vcModel.getVcName().trim().isEmpty()) {
			return false;
		}
		if (vcModel.getUsername() == null || vcModel.getUsername().trim().isEmpty()) {
			return false;
		}
		if (vcModel.getPassword() == null || vcModel.getPassword().isEmpty()) {
			return false;
		}
		if (vcModel.getProtocol() == null || vcModel.getProtocol().trim().isEmpty()) {
			return false;
		}
		
		if (esxModel.getESXName() == null || esxModel.getESXName().trim().isEmpty()) {
			return false;
		}
		
		return true;
	}
	
	private boolean fillNetworkGrid() {
		if (backupVMModel.adapterList == null || backupVMModel.adapterList.isEmpty()) {
			networkStore.removeAll();
			networkGrid.mask(UIContext.Constants.restoreVDSNoAdapters());
			return false;
		}

		if (configHelper.isEmpty()) {
			List<VMNetworkConfigGridModel> gridModels = networkStore.getModels();
			for (VMNetworkConfigGridModel gridModel : gridModels) {
				gridModel.getAvaliableConfigInfoComboBox().getStore().removeAll();
			}
		}

		Map<String, NetworkConfigInCell> configMap = configHelper.getAllConfigCells();
		List<NetworkConfigInCell> cofigList = new ArrayList<NetworkConfigInCell>(configMap.values());
		Collections.sort(cofigList);
		
        //add not connected option
		NetworkConfigInCell notConnect = new NetworkConfigInCell();
		notConnect.setSwitchUUID("");
		notConnect.setSwitchName("");
		notConnect.setPgKey("");
		notConnect.setPgName("");
		notConnect.setDisplayName(netWorkNotConnected);
		
		cofigList.add(0, notConnect);
		
		String longgestStr = getLonggestConfigString(cofigList);
		List<VMNetworkConfigGridModel> gridList = new ArrayList<VMNetworkConfigGridModel>();
		for (VMNetworkConfigInfoModel infoModel : backupVMModel.adapterList) {
			VMNetworkConfigGridModel gridModel = new VMNetworkConfigGridModel(infoModel);
			
			ComboBox<BaseModelData> configComboBox = gridModel.getAvaliableConfigInfoComboBox();
			configComboBox.setWidth(245);
			configComboBox.setAllowBlank(true);
			configComboBox.setTriggerAction(TriggerAction.ALL);
			configComboBox.setEditable(false);
			configComboBox.setDisplayField("displayName");
			configComboBox.setTemplate(getTemplate());
			
			ListStore<BaseModelData> configStore = configComboBox.getStore();
			
			configStore.removeAll();
			configStore.add(cofigList);
			String name = getMatchedDiplayNameFromNetwork(gridModel.getInfoModel(), cofigList);
			if (configMap.containsKey(name)) {
				configComboBox.setValue(configMap.get(name));
			} else {
				configComboBox.setValue(notConnect);
			}
			setDropdownListWidth(configComboBox, longgestStr);
			
			gridList.add(gridModel);
		}
		
		networkStore.removeAll();
		networkStore.add(gridList);
		
		if (cofigList.isEmpty()) {
			return false;
		}
		return true;
	}
	
	private String getLonggestConfigString(List<NetworkConfigInCell> cofigList) {
		if (cofigList == null || cofigList.isEmpty()) {
			return "";
		}
		
		String longgestStr = "";
		int length = 0;
		for (NetworkConfigInCell cell : cofigList) {
			String name = cell.getDisplayName();
			if (name != null && name.length() > length) {
				longgestStr = name;
				length = name.length();
			}
		}
		
		return longgestStr;
	}
	
	private void setDropdownListWidth(ComboBox<BaseModelData> configComboBox, String longgestStr) {
		int maxWidth = comboboxMetrics.getWidth(longgestStr) + 15;
		int defaultWidth = configComboBox.getMinListWidth();
		if (maxWidth > defaultWidth){
			configComboBox.setMinListWidth(maxWidth);
		} else {
			configComboBox.setMinListWidth(defaultWidth);
		}
	}
	
	private VirtualCenterModel getVCModelFromNewDest(){
		String vcName = vcTextField.getValue();
		int port = portTextField.getValue().intValue();
		String protocol = httpsProtocol.getValue()==true?"https":"http";
		String username = usernameTextField.getValue();
		String password = passwordTextField.getValue();
		VirtualCenterModel vcModel = new VirtualCenterModel();
		vcModel.setPassword(password);
		vcModel.setUsername(username);
		vcModel.setPort(port);
		vcModel.setProtocol(protocol);
		vcModel.setVcName(vcName);
		return vcModel;
	}
	
	/*private String getImageHtml(String picName) {
		return "<img src=\"images/"+picName+"\"> ";
	}*/
	
	private native String getTemplate() /*-{ 
	    return  [ 
	    '<tpl for=".">', 
	    '<div class="x-combo-list-item" qtip="{displayName}" qtitle="">{displayName}</div>', 
	    '</tpl>' 
	    ].join(""); 
	}-*/;  
	
	private native String getTemplateForGrid() /*-{ 
	    return  [ 
	    '<tpl for=".">', 
	    '<div class="x-combo-list-item" qtip="{DisplayName}" qtitle="">{DisplayName}</div>', 
	    '</tpl>' 
	    ].join(""); 
	}-*/; 
	
	@Override
	public boolean validate(final AsyncCallback<Boolean> callback) {
		if (backupVMModel.getVMType() == BackupVMModel.Type.VMware_VApp.ordinal()) {
			vAppOptionsWizard.validate(callback);
			return true;
		}
		
		if(originalLocation.getValue()){
			if(backupVMModel.getEsxPassword()==null || backupVMModel.getEsxPassword().equals("")){
				if (backupVMModel.getVMType() == 0){
					SetCredentialWindow win = new SetCredentialWindow(thisPanel,callback,false);
					win.setModal(true);
					win.show();
					callback.onSuccess(false);
					return false;
				} else{
					HyperVValidationWindow win = new HyperVValidationWindow(thisPanel, callback);
					win.setModal(true);
					win.show();
					callback.onSuccess(false);
					return false;
				}
			}

			if (!overwriteVM.getValue() && !isOverwriteIgnore) {
				if (backupVMModel.getVMType() == BackupVMModel.Type.VMware.ordinal()) 
				    validateVMWareVMExisting(true, backupVMModel.getVMName(), callback);
				else
					validateHyperVVMExisting(callback);
			} else {
				checkFlashResource(callback);
			}
		}else{
			if (backupVMModel.getVMType() == BackupVMModel.Type.VMware.ordinal()) {
				if (!validateVMWare(callback))
					return false;
			}else{
				hyperVOptionPanel.validate(callback);
			}
		}
		return true;
	}
	
    private void validateHyperVVMExisting(final AsyncCallback<Boolean> callback)
	{
    	String vmName = backupVMModel.getVMName();		
    	String hypverVHost = backupVMModel.getEsxServerName();
    	String hyperVusername = backupVMModel.getEsxUsername();
    	String password = backupVMModel.getEsxPassword();
    	String vmInstUUID = backupVMModel.getVmInstanceUUID();
	    
		service.validateHyperVAndCheckIfVMExist(
						hypverVHost,
						hyperVusername,
						password,
						vmInstUUID,
						vmName,
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
								checkFlashResource(callback);
							}
						});
    }
    
	private boolean validateVMWare(final AsyncCallback<Boolean> callback)
	{	
		if (!this.vcTextField.validate()) {
			callback.onSuccess(false);
			return false;
		} else {
			vcTextField.clearInvalid();
		}
		if (!this.vmNameTextField.validate()) {
			callback.onSuccess(false);
			return false;
		} else {
			vmNameTextField.clearInvalid();
		}
		if (!this.portTextField.validate()) {
			callback.onSuccess(false);
			return false;
		} else {
			portTextField.clearInvalid();
		}
		if (!this.usernameTextField.validate()) {
			callback.onSuccess(false);
			return false;
		} else {
			usernameTextField.clearInvalid();
		}
		if (!this.passwordTextField.validate()) {
			callback.onSuccess(false);
			return false;
		} else {
			passwordTextField.clearInvalid();
		}
		if (!this.esxList.validate()
				|| esxList.getStore().getCount() == 0) {
			callback.onSuccess(false);
			validateError(UIContext.Constants.recoverVMNoConnectESX());
			return false;
		} else {
			esxList.clearInvalid();
		}
		if (!this.vmDataStore.validate()
				|| vmDataStore.getStore().getCount() == 0) {
			callback.onSuccess(false);
			validateError(UIContext.Constants.recoverVMNoConnectESX());
			return false;
		} else {
			vmDataStore.clearInvalid();
		}
		

        if (doesOverwriteVM())
		{
        	validateVMWareStorage(callback);
		}
        else
        {
        	validateVMWareVMExisting(false, vmNameTextField.getValue(), callback);
		}
		
		return true;
	}
	
	private void validateVMWareVMExisting(boolean bOriginal, final String vmName, final AsyncCallback<Boolean> callback)
	{
		String vcName, username, password, protocol, esxName;
		int port;
		if (bOriginal)
		{
			vcName = vcTextField.getValue();		
		    username = backupVMModel.getEsxUsername();
		    password = backupVMModel.getEsxPassword();
		    protocol = backupVMModel.getProtocol();
		    port = backupVMModel.getPort(); 
		    esxName = backupVMModel.getSubVMEsxHost();
		    
		}
		else
		{
			protocol = httpsProtocol.getValue()==true?"https":"http";
			vcName = vcTextField.getValue();
			username = usernameTextField.getValue();
			password = passwordTextField.getValue();			
	        port = portTextField.getValue().intValue();
	        esxName = esxList.getSelection().get(0).getESXName();
		}
		

        //final String esxName = esxList.getSelection().get(0).getESXName();

    	final ColdStandbyServiceAsync coldStandbyService = GWT.create(ColdStandbyService.class);
    	coldStandbyService.isVMWareVMNameExist(		
		vcName, username, password, protocol, true, port,
		esxName, null, vmName,
		new BaseAsyncCallback<Boolean>()
		{
			@Override
			public void onFailure(Throwable caught) 
			{
				MessageBox mb = new MessageBox();
				mb.setIcon(MessageBox.ERROR);
				mb.setButtons(MessageBox.OK);
				mb.setMessage(UIContext.Messages.vmWareVMExists(vmName));
				mb.show();
				callback.onSuccess(false);
			}

			@Override
			public void onSuccess(Boolean result) 
			{
				if (result)
				{
					MessageBox mb = new MessageBox();
					mb.setIcon(MessageBox.ERROR);
					mb.setButtons(MessageBox.OK);
					mb.setMessage(UIContext.Messages.vmWareVMExists(vmName));
					mb.show();
					callback.onSuccess(false);
			    }
			    else
			    {
			    	validateVMWareStorage(callback);
			    }																			
		    }
	    });
	}
	
	private void validateVMWareStorage(final AsyncCallback<Boolean> callback)
	{		
    	boolean isDatastoreEnough = true;
    	if (backupVMModel.diskList != null) 
    	{
			String datastores = "";
			for (VMStorage datastore : vmStorages) {
				datastore.diskList = new ArrayList<DiskModel>();
			}
			for (int i = 0; i < datastoreStore.getCount(); i++) {
				DataStoreModel store = datastoreStore.getAt(i);
				ComboBox<ModelData> row = store
						.getEsxDataStoreComboBox();
				String dataStoreName = (String) row.getValue().get(
						"name");
				for (VMStorage datastore : vmStorages) {
					if (datastore.getName().equals(dataStoreName)) {
						datastore.diskList.add(store.getDiskModel());
						break;
					}
				}
			}							
			
			for (VMStorage datastore : vmStorages) {
				if (datastore.diskList != null
						&& datastore.diskList.size() > 0) {
					long dataStoreSize = datastore.getFreeSize();
					long totalDiskSize = 0L;
					for (DiskModel diskModel : datastore.diskList) {
						totalDiskSize += diskModel.getSize();
					}
					if (dataStoreSize < totalDiskSize) {
						isDatastoreEnough = false;
						datastores += datastore.getName() + ",";
					}

				}
			}
			if (!isDatastoreEnough) {
				datastores = datastores.substring(0,
						datastores.length() - 1);
				String msg = UIContext.Messages
						.vSphereDataStoreNotEnough(datastores);
				MessageBox mb = new MessageBox();
				mb.setIcon(MessageBox.WARNING);
				mb.setButtons(MessageBox.YESNO);
				mb.setTitleHtml(UIContext.Messages
						.messageBoxTitleWarning(getProductName()));
				mb.setMessage(msg);
				Utils.setMessageBoxDebugId(mb);
				mb.addCallback(new Listener<MessageBoxEvent>() {
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId()
								.equals(Dialog.YES)) {
							checkNetWorkConfiguration(callback);
						} else {
							callback.onSuccess(false);
						}

					}
				});
				mb.show();
			}
		}
		if (isDatastoreEnough) {
			checkNetWorkConfiguration(callback);
		}    
		
	}
	
	private void checkFlashResource(final AsyncCallback<Boolean> callback) {
		if (backupVMModel.getVMType() != BackupVMModel.Type.VMware.ordinal() ||
			backupVMModel.getVMType() != BackupVMModel.Type.VMware_VApp.ordinal() ||
			originalLocation.getValue()){
			checkDestinationEsxServer(callback);
			return;
		}
		if (backupVMModel.diskList == null || backupVMModel.diskList.isEmpty()) {
			checkDestinationEsxServer(callback);
			return;
		}
		
		if (backupVMModel.flashReadCacheSize <= 0 ) {
			checkDestinationEsxServer(callback);
			return;
		}

		VirtualCenterModel tempVcModel = null;
		ESXServerModel tempEsxModel = null;
		if (originalLocation.getValue()) {
			tempVcModel = getVCModelFromBackupVM();
			tempEsxModel = new ESXServerModel(); 
			tempEsxModel.setESXName(backupVMModel.getEsxServerName());
			tempEsxModel.setDcName(null);
		} else {
			tempVcModel = getVCModelFromNewDest();
			tempEsxModel = esxList.getSelection().get(0);
		}
		
		final VirtualCenterModel vcModel = tempVcModel;
		final ESXServerModel esxModel = tempEsxModel;
		
		service.getESXVFlashResource(vcModel, esxModel, new BaseAsyncCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				if (result == null || result < 0) {
					String msg = UIContext.Messages.vSphereGetFlashResourceSizeFailed(esxModel.getESXName());
					showFlashResourceConfirmDialog(msg, callback);
				} else if (result == 0 ) {
					String msg = UIContext.Messages.vSphereGetFlashResourceSizeZero(esxModel.getESXName());
					showFlashResourceConfirmDialog(msg, callback);
				} else if (result < backupVMModel.flashReadCacheSize ) {
					String msg = UIContext.Messages.vSphereGetFlashResourceSizeLsReadCache(convertByte2GB(result),
							convertByte2GB(backupVMModel.flashReadCacheSize), esxModel.getESXName());
					showFlashResourceConfirmDialog(msg, callback);
				} else {
					checkDestinationEsxServer(callback);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				String msg = UIContext.Messages.vSphereGetFlashResourceSizeFailed(esxModel.getESXName());
				showFlashResourceConfirmDialog(msg, callback);
			}
		});
	}
	
	private String convertByte2GB(long size) {
		return Double.toString((size*1.0D)/1024/1024/1024);
	}
	
	private void showFlashResourceConfirmDialog(String message, final AsyncCallback<Boolean> callback) {
		MessageBox box = new MessageBox();
		box.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(getProductName()));
		box.setMessage(message);
		box.setModal(true);
		box.setIcon(MessageBox.WARNING);
		box.setButtons(Dialog.YESNO);
		box.addCallback(new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					checkDestinationEsxServer(callback);
				} else {
					callback.onSuccess(Boolean.FALSE);
				}
			}
		});
		box.show();
	}
	
	private void checkNetWorkConfiguration (final AsyncCallback<Boolean> callback){
		boolean isNetWorkConfiged = true;
		List<VMNetworkConfigGridModel> gridModels = networkStore.getModels();
		if (gridModels != null && !gridModels.isEmpty()) {
			String adpaterNames = "";
			for (VMNetworkConfigGridModel model : gridModels) {
				NetworkConfigInCell selectedCell = (NetworkConfigInCell) model.getAvaliableConfigInfoComboBox()
						.getValue();
				if (selectedCell == null || selectedCell.getIsEmpty() == true) {
					isNetWorkConfiged = false;
					adpaterNames += model.getInfoModel().getLabel() + ",";
				}
			}

			if (!isNetWorkConfiged) {
				adpaterNames = adpaterNames.substring(0, adpaterNames.length() - 1);
				String msg = UIContext.Messages.vSphereNetWorkNotConfig(adpaterNames);
				MessageBox mb = new MessageBox();
				mb.setModal(true);
				mb.setIcon(MessageBox.WARNING);
				mb.setButtons(MessageBox.YESNO);
				mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(getProductName()));
				mb.setMessage(msg);
				Utils.setMessageBoxDebugId(mb);
				mb.addCallback(new Listener<MessageBoxEvent>() {
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							checkFlashResource(callback);
						} else {
							callback.onSuccess(false);
						}

					}
				});
				mb.show();
			}
		}
		
		if(isNetWorkConfiged){
			checkFlashResource(callback);
		}
	}
	
	private String getProductName() {
		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == 1){
			productName = UIContext.productNamevSphere;
		}
		return productName;
	}
	
	private void validateError(String message) {
		MessageBox box = new MessageBox();
		box.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductName()));
		box.setMessage(message);
		box.setIcon(MessageBox.ERROR);
		box.setButtons(Dialog.OK);
		box.show();
	}
	
	private void checkDestinationEsxServer (final AsyncCallback<Boolean> callback){
		if(originalLocation.getValue()){
			checkRecoveryVMJobExist(callback);
			return;
		}
		
		VirtualCenterModel vcModel = getVCModelFromNewDest();
		ESXServerModel esxServerModel = esxList.getSelection().get(0);
		String dest =  restoreWizardWindow.vmrecvPointPanel.getSelectedBackupVMModel().getBrowseDestination();
		String userName = restoreWizardWindow.vmrecvPointPanel.getUserName();
		String password = restoreWizardWindow.vmrecvPointPanel.getPassword();
		String subPath = restoreWizardWindow.vmrecvPointPanel.getSelectedRecoveryPoint().getPath();
		String domain = "";
		if (userName != null) {
			int index = userName.indexOf('\\');
			if (index > 0) {
				domain = userName.substring(0, index);
				userName = userName.substring(index + 1);
			}
		}
		
		service.checkVMRecoveryPointESXUefi(vcModel, esxServerModel, dest, domain, userName, password, subPath, new BaseAsyncCallback<Integer>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				callback.onSuccess(Boolean.FALSE);
			}
			
			@Override
			public void onSuccess(Integer result) {
				if(result == 0){
					checkRecoveryVMJobExist(callback);
				}
				else{
					showErrorMessageBox(UIContext.Constants.vmRecoveryESXDoesnotSupportUEFI());
					callback.onSuccess(Boolean.FALSE);
				}
			}
		});
	}
	
	private void checkRecoveryVMJobExist(final AsyncCallback<Boolean> callback){
		String esxServerName = "";
		String vmName = "";
		if(originalLocation.getValue()){
			esxServerName = backupVMModel.getEsxServerName();
			vmName = backupVMModel.getVMName();
		}else{
			esxServerName = esxList.getSelection().get(0).getESXName();
			vmName = vmNameTextField.getValue();
		}
		
		service.checkRecoveryVMJobExist(vmName, esxServerName, new BaseAsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				callback.onSuccess(Boolean.FALSE);
			}
			
			@Override
			public void onSuccess(Boolean isExist) {
				if(isExist !=null && !isExist){
					checkSessionPassword(callback);
				}else{
					showErrorMessageBox(UIContext.Constants.vmRecoveryCannotRun());
					callback.onSuccess(Boolean.FALSE);
				}
			}
		});
	}
	
	private void showErrorMessageBox(String msg){
		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == 1){
			productName = UIContext.productNamevSphere;
		}
		MessageBox messageBox = new MessageBox();
		messageBox.setModal(true);
		messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
		messageBox.setMessage(msg);
		messageBox.setIcon(MessageBox.ERROR);
		Utils.setMessageBoxDebugId(messageBox);
		messageBox.show();
	}
	
	private VirtualCenterModel getVCModelFromBackupVM(){
		VirtualCenterModel vcModel = new VirtualCenterModel();
		vcModel.setVcName(backupVMModel.getEsxServerName());
		vcModel.setProtocol(backupVMModel.getProtocol());
		vcModel.setPort(backupVMModel.getPort());
		vcModel.setPassword(backupVMModel.getEsxPassword());
		vcModel.setUsername(backupVMModel.getEsxUsername());
		return vcModel;
	}
	
	@Override
	public int processOptions() {
		if (backupVMModel.getVMType() == BackupVMModel.Type.VMware_VApp.ordinal()) {
			return vAppOptionsWizard.processOptions();
		}
				
		RestoreJobModel model = RestoreContext.getRestoreModel();
		model.recoverVMOption = new RecoverVMOptionModel();
		model.recoverVMOption.setRegisterAsClusterHyperVVM(false);
		if (alternateLocation.getValue()) {
			model.recoverVMOption.setOriginalLocation(false);
			if (backupVMModel.getVMType() == BackupVMModel.Type.VMware.ordinal()){
				String vcName = vcTextField.getValue();
				int port = portTextField.getValue().intValue();
				String protocol = httpsProtocol.getValue()==true?"https":"http";
				String username = usernameTextField.getValue();
				String password = passwordTextField.getValue();
				VirtualCenterModel vcModel = new VirtualCenterModel();
				vcModel.setPassword(password);
				vcModel.setUsername(username);
				vcModel.setPort(port);
				vcModel.setProtocol(protocol);
				vcModel.setVcName(vcName);
				model.recoverVMOption.setVCModel(vcModel);
			
				String vmName = vmNameTextField.getValue();
				String esxServerName = esxList.getSelection().get(0).getESXName();
				String dataStore = vmDataStore.getSelection().get(0).getName();
				
				model.recoverVMOption.setVMName(vmName);
				model.recoverVMOption.setESXServerName(esxServerName);
				model.recoverVMOption.setVMDataStore(dataStore);
				model.recoverVMOption.setVcName(vcName);
				model.recoverVMOption.setVMUUID(backupVMModel.getUUID());
				model.recoverVMOption.setVMInstanceUUID(backupVMModel.getVmInstanceUUID());
				model.recoverVMOption.setResourcePoolName(poolPanel.getPoolMoref());
				model.recoverVMOption.setResourcePoolShowName(poolPanel.getPoolName());
				
	            List<DiskDataStoreModel> diskDataStoreList = new ArrayList<DiskDataStoreModel>();
	            DiskDataStoreModel diskDataStoreModel = null;
				for(int i=0; i < datastoreStore.getCount(); i++){
					diskDataStoreModel = new DiskDataStoreModel();
					DataStoreModel store = datastoreStore.getAt(i);
					ComboBox<ModelData> row = store.getEsxDataStoreComboBox(); 
					String disk = store.getDiskModel().getDiskUrl();
					String datastore = (String)row.getValue().get("name");
					/*//set vm's datastore same as the first disk's datastore
					if(i==0){
						model.recoverVMOption.setVMDataStore(datastore);
					}*/
					diskDataStoreModel.setDisk(disk);
					diskDataStoreModel.setDataStore(datastore);
					diskDataStoreModel.setDiskName(UIContext.Constants.vmRecoveryDisk()+store.getDiskModel().getDiskNumber());
					diskDataStoreModel.setDiskType(store.getDiskType());
					String volumes = "";
					if(store.getDiskModel().volumeModelList!=null && store.getDiskModel().volumeModelList.size()>0){
						for(VMVolumeModel volumeModel : store.getDiskModel().volumeModelList){
							volumes +=volumeModel.getDriveLetter();
						}
					}
					diskDataStoreModel.set("diskSize", Utils.bytes2String(store.getDiskModel().getSize()));
					diskDataStoreModel.setVolumeName(volumes);
					diskDataStoreList.add(diskDataStoreModel);
				}
				model.recoverVMOption.setVmDiskCount(datastoreStore.getCount());
				model.recoverVMOption.setDiskDataStore(diskDataStoreList);
				
				model.recoverVMOption.setNetworkConfigInfo(getNetworkConfig4RestoreModel());
			}else{
				String hyperVHost = "";
				if (hyperVOptionPanel.getHyperVType() == HyperVTypes.Cluster_Virutal_Node.ordinal())
					hyperVHost = hyperVOptionPanel.getVirtualClusterName();
				else
					hyperVHost = hyperVOptionPanel.getConnectionPanel().getHyperVHostName();
				String vcName = hyperVHost;
				String username = hyperVOptionPanel.getConnectionPanel().getHyperVUserName();
				String password = hyperVOptionPanel.getConnectionPanel().getHyperVPassword();
				VirtualCenterModel vcModel = new VirtualCenterModel();
				vcModel.setPassword(password);
				vcModel.setUsername(username);
				vcModel.setVcName(vcName);
				vcModel.setPort(0);
				vcModel.setProtocol("http");
				model.recoverVMOption.setVCModel(vcModel);
			
				String vmName = hyperVOptionPanel.getVMName();
				String esxServerName = hyperVHost;
				String dataStore = hyperVOptionPanel.getVMPath();
				
				model.recoverVMOption.setVMName(vmName);
				model.recoverVMOption.setESXServerName(esxServerName);
				model.recoverVMOption.setVMDataStore(dataStore);
				model.recoverVMOption.setVcName(vcName);
				model.recoverVMOption.setVMUUID(backupVMModel.getUUID());
				model.recoverVMOption.setVMInstanceUUID(backupVMModel.getVmInstanceUUID());
				model.recoverVMOption.setResourcePoolName(poolPanel.getPoolMoref());
				model.recoverVMOption.setResourcePoolShowName(poolPanel.getPoolName());
				
	            List<DiskDataStoreModel> diskDataStoreList = new ArrayList<DiskDataStoreModel>();
	            DiskDataStoreModel diskDataStoreModel = null;
				for(int i=0; i < hyperVOptionPanel.getDatastoreStore().getCount(); i++){
					diskDataStoreModel = new DiskDataStoreModel();
					HyperVAlternativeLocationPanel.VMPathModel store = hyperVOptionPanel.getDatastoreStore().getAt(i);
					String disk = "";
					if (hyperVOptionPanel.isSameVMDiskPath())
						disk = hyperVOptionPanel.getVMDiskPath();
					else
						disk = store.getDiskPathContainer().getDiskPath();
					
					if (disk.endsWith("\\"))
						disk = disk.substring(0, disk.length()-1);
					
					String datastore = String.valueOf(store.getDiskModel().getSignature());
					diskDataStoreModel.setDisk(datastore);
					diskDataStoreModel.setDataStore(disk);
					
					if (hyperVOptionPanel.isSameVMDiskPath()){
						long diskType = hyperVOptionPanel.getDiskType();
						if (diskType == DiskModel.HYPERV_VDISK_TYPE_ORIGINAL)
							diskDataStoreModel.setDiskType(new Long(store.getDiskModel().getDiskType()));
						else
							diskDataStoreModel.setDiskType(diskType);
						diskDataStoreModel.setQuickRecovery(hyperVOptionPanel.getQuickRecovery());
					}else{
						diskDataStoreModel.setDiskType(store.getDiskType());
						diskDataStoreModel.setQuickRecovery(store.getQuickRecovery());
					}
					
					diskDataStoreModel.setDiskName(UIContext.Constants.vmRecoveryDisk()+store.getDiskModel().getDiskNumber());
					String volumes = "";
					if(store.getDiskModel().volumeModelList!=null && store.getDiskModel().volumeModelList.size()>0){
						for(VMVolumeModel volumeModel : store.getDiskModel().volumeModelList){
							volumes +=volumeModel.getDriveLetter();
						}
					}
					diskDataStoreModel.set("diskSize", Utils.bytes2String(store.getDiskModel().getSize()));
					diskDataStoreModel.setVolumeName(volumes);
					diskDataStoreList.add(diskDataStoreModel);
				}
				model.recoverVMOption.setVmDiskCount(datastoreStore.getCount());
				model.recoverVMOption.setDiskDataStore(diskDataStoreList);
				
				model.recoverVMOption.setNetworkConfigInfo(getHyperVNetworkConfig4RestoreModel());
				
				if (hyperVOptionPanel.getRegisterCluster().getValue()){
					model.recoverVMOption.setRegisterAsClusterHyperVVM(true);
				}else
					model.recoverVMOption.setRegisterAsClusterHyperVVM(false);
			}
		}else{
			model.recoverVMOption.setOriginalLocation(true);
			model.recoverVMOption.setVMName(backupVMModel.getVMName());
			model.recoverVMOption.setESXServerName(backupVMModel.getEsxServerName());
			model.recoverVMOption.setVMUUID(backupVMModel.getUUID());
			model.recoverVMOption.setVMInstanceUUID(backupVMModel.getVmInstanceUUID());
			model.recoverVMOption.setVmDiskCount(0);
			VirtualCenterModel vcModel = new VirtualCenterModel();
			vcModel.setVcName(backupVMModel.getEsxServerName());
			vcModel.setProtocol(backupVMModel.getProtocol());
			vcModel.setPort(backupVMModel.getPort());
			vcModel.setPassword(backupVMModel.getEsxPassword());
			vcModel.setUsername(backupVMModel.getEsxUsername());
			model.recoverVMOption.setVCModel(vcModel);
		}
		if(pwdPane.isVisible()) {
			String password = pwdPane.getPassword();
			model.recoverVMOption.setEncryptPassword(password);
		}
		model.recoverVMOption.setOverwriteExistingVM(overwriteVM.getValue());
		model.recoverVMOption.setGenerateNewInstVMID(generateNewVMInstanceID.getValue());
		model.recoverVMOption.setPowerOnAfterRestore(powerOn.getValue());
		model.recoverVMOption.setSessionNumber(RestoreContext.getRecoveryPointModel().getSessionID());
		return 0;
	}

	public BackupVMModel getBackupVMModel() {
		return backupVMModel;
	}


	public void setBackupVMModel(BackupVMModel backupVMModel) {
		this.backupVMModel = backupVMModel;
	}
	
	protected void updateVisibleOfPwdPane() {
		//encryptionType = 1;
		if (!RestoreContext.getRecoveryPointModel().isEncrypted())
			pwdPane.setVisible(false);
		else {
			pwdPane.setVisible(true);
			VMRecoveryPointsPanel panel = restoreWizardWindow
					.getVMRecoveryPointsPanel();
			if(panel.getSelectedSessionEncyrptionKey() != null
					&& !panel.getSelectedSessionEncyrptionKey().isEmpty()){
				pwdPane.setPassword(panel.getSelectedSessionEncyrptionKey());
			}else
			pwdPane.autoFillPassword(panel.getSelectedRecoveryPoint()
					.getSessionGuid());
		}
	}
	
	private List<VMNetworkConfigInfoModel> getNetworkConfig4RestoreModel() {
		List<VMNetworkConfigInfoModel> infoModelList = new ArrayList<VMNetworkConfigInfoModel>();
		List<VMNetworkConfigGridModel> gridModels = networkStore.getModels();
		
		if (gridModels != null && !gridModels.isEmpty()) {
			for (VMNetworkConfigGridModel model : gridModels) {
				VMNetworkConfigInfoModel orgInfoModel = model.getInfoModel();
				VMNetworkConfigInfoModel infoModel = coloneVMNetworkConfigInfoModel(orgInfoModel);
				
				NetworkConfigInCell selectedCell = (NetworkConfigInCell) model.getAvaliableConfigInfoComboBox().getValue();
				if (selectedCell == null || selectedCell.getIsEmpty() == true) {
					infoModel.setDeviceName(null);
					infoModel.setSwitchName(null);
					infoModel.setSwitchUUID(null);
					infoModel.setPortgroupName(null);
					infoModel.setPortgroupKey(null);
					
				} else {
					if (selectedCell.getIsVDS()) {
						infoModel.setDeviceName(null);
						infoModel.setSwitchName(selectedCell.getSwitchName());
						infoModel.setSwitchUUID(selectedCell.getSwitchUUID());
						infoModel.setPortgroupName(selectedCell.getPgName());
						infoModel.setPortgroupKey(selectedCell.getPgKey());
					} else {
						infoModel.setDeviceName(selectedCell.getDeviceName());
						infoModel.setSwitchName(null);
						infoModel.setSwitchUUID(null);
						infoModel.setPortgroupName(null);
						infoModel.setPortgroupKey(null);
					}
				}
				infoModelList.add(infoModel);
			}
		}
		
		return infoModelList;
	}
	
	private List<VMNetworkConfigInfoModel> getHyperVNetworkConfig4RestoreModel() {
		List<VMNetworkConfigInfoModel> infoModelList = new ArrayList<VMNetworkConfigInfoModel>();
		List<VMNetworkConfigGridModel> gridModels = this.hyperVOptionPanel.getNetworkStore().getModels();
		
		if (gridModels != null && !gridModels.isEmpty()) {
			for (VMNetworkConfigGridModel model : gridModels) {
				VMNetworkConfigInfoModel orgInfoModel = model.getInfoModel();
				VMNetworkConfigInfoModel infoModel = new VMNetworkConfigInfoModel();
				
				infoModel.setLabel(String.valueOf(orgInfoModel.getHyperVAdapterType().intValue()));
				infoModel.setSwitchUUID( ((VMNetworkConfigInfoModel)model.getAvaliableConfigInfoComboBox().getValue()).getSwitchUUID());
				infoModel.setDeviceName( orgInfoModel.getHyperVAdapterID()!=null?orgInfoModel.getHyperVAdapterID().toString():"0");
				
				infoModel.set("networkName", orgInfoModel.getLabel());
				infoModel.set("networkConnection", ((VMNetworkConfigInfoModel)model.getAvaliableConfigInfoComboBox().getValue()).getLabel());
				infoModelList.add(infoModel);
			}
		}
		
		return infoModelList;
	}
	
	private VMNetworkConfigInfoModel coloneVMNetworkConfigInfoModel(VMNetworkConfigInfoModel infoModel) {
		VMNetworkConfigInfoModel newModel = new VMNetworkConfigInfoModel();
		
		newModel.setLabel(infoModel.getLabel());
		newModel.setBackingInfoType(infoModel.getBackingInfoType());
		
		newModel.setDeviceName(infoModel.getDeviceName());
		newModel.setDeviceType(infoModel.getDeviceType());
		
		newModel.setPortgroupName(infoModel.getPortgroupName());
		newModel.setPortgroupKey(infoModel.getPortgroupKey());
		
		newModel.setSwitchName(infoModel.getSwitchName());
		newModel.setSwitchUUID(infoModel.getSwitchUUID());
		
		return newModel;
	}
	
	private class AvalibaleNetworkConfigHelper {
		private Map<String, NetworkConfigInCell> configMap = new HashMap<String, NetworkConfigInCell>();
	
		public NetworkConfigInCell getConfigCellByName(String name) {
			return configMap.get(name);
		}
		
		public Map<String, NetworkConfigInCell> getAllConfigCells() {
			return configMap;
		}
		
		public void addVDSConfigs(List<VDSInfoModel> vdsInfoList) {
			if (vdsInfoList != null && !vdsInfoList.isEmpty()) {
				for (VDSInfoModel vds : vdsInfoList) {
					List<vDSPortGroupModel> pgList = vds.getPortGroups();
					if (pgList != null && !pgList.isEmpty()) {
						for (vDSPortGroupModel pg : pgList) {
							NetworkConfigInCell cell = new NetworkConfigInCell(vds, pg);
							configMap.put(cell.getDisplayName(), cell);
						}
					}
				}
			}
		}
			
		public void addStandardConfigs(List<VMNetworkStandardConfigInfoModel> standardInfoList) {
			if (standardInfoList != null && !standardInfoList.isEmpty()) {
				for (VMNetworkStandardConfigInfoModel model : standardInfoList) {
					NetworkConfigInCell cell = new NetworkConfigInCell(model);
					configMap.put(cell.getDisplayName(), cell);
				}
			}
		}
		
		public void clear() {
			configMap.clear();
		}
		
		public boolean isEmpty() {
			return configMap.isEmpty();
		}
	}
	
	public class NetworkConfigInCell extends BaseModelData implements Comparable<NetworkConfigInCell> {

		private static final long serialVersionUID = 3387454711838071241L;

		public NetworkConfigInCell(VDSInfoModel vds, vDSPortGroupModel pg) {
			setIsEmpty(false);
			setIsVDS(true);
			setSwitchName(vds.getvDSSwitchName());
			setSwitchUUID(vds.getvDSSwitchUUID());
			setPgName(pg.getvDSPortGroupName());
			setPgKey(pg.getvDSPortGroupKey());
			setDeviceName(null);
			generateDisplayName();
		}
		
		public NetworkConfigInCell(VMNetworkStandardConfigInfoModel model) {
			setIsEmpty(false);
			setIsVDS(false);
			setDeviceName(model.getNetworkName());
			setSwitchName(null);
			setSwitchUUID(null);
			setPgName(null);
			setPgKey(null);
			generateDisplayName();
		}
		
		public NetworkConfigInCell() {
			setIsEmpty(true);
			setIsVDS(false);
			setDeviceName(null);
			setSwitchName(null);
			setSwitchUUID(null);
			setPgName(null);
			setPgKey(null);
			generateDisplayName();
		}
		
		public void setIsEmpty(boolean isEmpty) {
			set("isEmpty", isEmpty);
		}
		
		public void setIsVDS(boolean isVDS) {
			set("isVDS", isVDS);
		}
		
		public void setSwitchName(String switchName) {
			set("switchName", switchName);
		}
		
		public void setSwitchUUID(String uuid) {
			set("switchUUID", uuid);
		}
		
		public void setPgName(String pgName) {
			set("pgName", pgName);
		}
		
		public void setPgKey(String pgKey) {
			set("pgKey", pgKey);
		}
		
		public void setDeviceName(String name) {
			set("deviceName", name);
		}
		
		public void setDisplayName(String display) {
			set("displayName", display);
		}
		
		public boolean getIsEmpty() {
			return get("isEmpty");
		}
		
		public boolean getIsVDS() {
			return get("isVDS");
		}
		
		public String getSwitchName() {
			return get("switchName");
		}
		
		public String getSwitchUUID() {
			return get("switchUUID");
		}
		
		public String getPgName() {
			return get("pgName");
		}
		
		public String getPgKey() {
			return get("pgKey");
		}
		
		public String getDeviceName() {
			return get("deviceName");
		}
		
		public String getDisplayName() {
			return get("displayName");
		}
		
		private void generateDisplayName() {
			String display = null;
			if (getIsEmpty()) {
				display = "";
			} else {
				boolean isVDS = getIsVDS();
				if (isVDS == true) {
					display = getPgName() + "(" + getSwitchName() + ")";
				} else {
					display = getDeviceName();
				}
			}
			
			setDisplayName(display);
		}

		@Override
		public int compareTo(NetworkConfigInCell other) {
			String name = getDisplayName();
			if (name == null) {
				return -1;
			} else if (other == null) {
				return 1;
			} else {
				return getDisplayName().compareTo(other.getDisplayName());
			}
		}
	}
	
	private class MyTextMetrics {

		private El el;
		
		public MyTextMetrics() {
			el = new El(DOM.createDiv());
			DOM.appendChild(XDOM.getBody(), el.dom);
			el.makePositionable(true);
			el.setLeftTop(-10000, -10000);
			el.setVisibility(false);
		}

		public void bind() {
			this.el.setStyleAttribute("font-size", "12px");
			this.el.setStyleAttribute("font-family", "Tahoma,Arial");
		}
		
		public int getHeight(String text) {
			return getSize(text).height;
		}

		public Size getSize(String text) {
			el.dom.setInnerHTML(text);
			Size size = el.getSize();
			el.dom.setInnerHTML("");
			return size;
		}

		public int getWidth(String text) {
			el.setStyleAttribute("width", "auto");
			return getSize(text).width;
		}

		public void setFixedWidth(int width) {
			el.setWidth(width);
		}
	}
	
	public boolean doesOverwriteVM()
	{
		return overwriteVM.getValue();
	}
	
	public String getEnteredVMName()
	{
		String t = vmNameTextField.getTitle();
		String v = vmNameTextField.getValue();
		
		return vmNameTextField.getValue();
	}

}
