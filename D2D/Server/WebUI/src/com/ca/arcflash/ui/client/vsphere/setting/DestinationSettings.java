package com.ca.arcflash.ui.client.vsphere.setting;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.common.GxtFactory;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.ca.arcflash.ui.client.model.VSphereProxyModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class DestinationSettings extends com.ca.arcflash.ui.client.backup.BaseDestinationSettings{
	
	private final LoginServiceAsync service = GWT.create(LoginService.class);
	private TextField<String> vSphereProxyName;
	
	private TextField<String> vSphereProxyUsername;
	
	private PasswordTextField vSphereProxyPassword;
	
	private NumberField vSphereProxyPort;
	
	private Radio vSphereProxyHttp;
	
	private Radio vSphereProxyHttps;
	
	protected Grid<BaseModelData> transportGrid;
	protected ListStore<BaseModelData> transportStore;
	private Radio transportByVMwareRadio;
	private Radio transportByUserRadio;
	
	private static String SAN		=	"SAN";
	private static String NBDSSL	=	"NBDSSL";
	private static String NBD		=	"NBD";
	private static String HotAdd	=	"HotAdd";
	
	private RadioGroup vmwareQuiescenceMethodGrp;
	private Radio vmwareQuiescenceByVMToolRadio;
	private Radio vmwareQuiescenceByVSSRadio;
	
	private CheckBox vmwareQuiescenceBySnapshotFailsCheckBox;
	private CheckBox hyperVSnapshotVSSMethod;
	private CheckBox hyperVSnapshotSavedVMMethod;
	
	private CheckBox hyperVSeparationIndividually;

	public DestinationSettings(VSphereBackupSettingContent w) {
		super(w);
	}
	
	@Override
	public LayoutContainer Render() {
		LayoutContainer container = super.Render();
		container.add(createVmwareQuiescenceMethodComponent());
		container.add(renderTransportModePanel());
		container.add(createHyperVConsistentSnapshotTypeComponent());
		container.add(createHyperVSnapshotSeparationComponent());
		return container;
	}
	
	
	private void initVSphereContainter(LayoutContainer container) {
		if(parentWindow.isForEdge()) {
			initVSphereProxyContainer(container);
		}
	}
	
	private void initVSphereProxyContainer(LayoutContainer container){
		
		DisclosurePanel proxySettingsPanel = new DisclosurePanel((DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.vmSettingD2DVMBackupProxy(), true);
		
		proxySettingsPanel.ensureDebugId("8513578D-D1D8-49a3-B6A9-96440EFFDE46");
		proxySettingsPanel.setWidth("100%");
		proxySettingsPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		proxySettingsPanel.setOpen(true);
		/*LabelField label = new LabelField();
		label.setText(UIContext.Constants.vmSettingD2DVMBackupProxy());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);*/
		
		LayoutContainer vSphereProxyContainer = new LayoutContainer();
		
		TableLayout tl = new TableLayout();
		tl.setWidth("97%");
		tl.setColumns(3);
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		vSphereProxyContainer.setLayout(tl);
		
		TableData td1 = new TableData();
		td1.setColspan(1);
		td1.setWidth("18%");
		td1.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		TableData td2 = new TableData();
		td2.setColspan(2);
		td2.setWidth("82%");
		td2.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vSphereProxyName());
		vSphereProxyContainer.add(label,td1);
		
		vSphereProxyName = new TextField<String>();
		vSphereProxyName.ensureDebugId("0EBD4086-B5F5-42fa-9049-33CF4959933F");
		vSphereProxyName.setWidth(250);
		vSphereProxyContainer.add(vSphereProxyName,td2);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vSphereProxyUsername());
		vSphereProxyContainer.add(label,td1);
		
		vSphereProxyUsername = new TextField<String>();
		vSphereProxyUsername.ensureDebugId("051EA859-5E8C-42f6-AC70-FABBAA9DE84A");
		vSphereProxyUsername.setWidth(150);
		vSphereProxyContainer.add(vSphereProxyUsername,td2);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vSphereProxyPassword());
		vSphereProxyContainer.add(label,td1);
		
		vSphereProxyPassword = new PasswordTextField();
		vSphereProxyPassword.ensureDebugId("CACF8891-FE20-403f-AFAA-3B638E110408");
		vSphereProxyPassword.setWidth(150);
		vSphereProxyPassword.setPassword(true);
		vSphereProxyContainer.add(vSphereProxyPassword,td2);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vSphereProxyProtocol());
		vSphereProxyContainer.add(label,td1);
		
		
		LayoutContainer protocolContainer = new LayoutContainer();
		
		tl = new TableLayout();
		tl.setWidth("97%");
		tl.setColumns(2);
		protocolContainer.setLayout(tl);
		
		TableData htb = new TableData();
		htb.setWidth("15%");
		
		TableData htb2 = new TableData();
		htb2.setWidth("85%");
		
		RadioGroup rg = new RadioGroup();
		
		vSphereProxyHttp = new Radio();
		vSphereProxyHttp.ensureDebugId("6D639DF7-ED4A-470c-BE9B-522845795860");
		vSphereProxyHttp.setBoxLabel("HTTP");
		vSphereProxyHttp.setValue(true);
		
		rg.add(vSphereProxyHttp);
		
		vSphereProxyHttps = new Radio();
		vSphereProxyHttps.ensureDebugId("B651D007-F8CC-4611-BCBD-B9D5191C5051");
		vSphereProxyHttps.setBoxLabel("HTTPS");
		
		rg.add(vSphereProxyHttps);
		
		protocolContainer.add(vSphereProxyHttp,htb);
		protocolContainer.add(vSphereProxyHttps,htb2);
		
		vSphereProxyContainer.add(protocolContainer,td2);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vSphereProxyPort());
		vSphereProxyContainer.add(label,td1);
		
		vSphereProxyPort = new NumberField();
		vSphereProxyPort.ensureDebugId("4D2BDACB-F2A3-4671-A357-3DA3F71D6C61");
		vSphereProxyPort.setWidth(150);
		vSphereProxyPort.setValue(8014);
		vSphereProxyPort.setAllowBlank(false);
		vSphereProxyPort.setMinValue(0);
		vSphereProxyPort.setAllowDecimals(false);
		vSphereProxyPort.setAllowNegative(false);
		vSphereProxyContainer.add(vSphereProxyPort,td2);
		
		proxySettingsPanel.add(vSphereProxyContainer);
		
		container.add(proxySettingsPanel);
	}
	
	@Override
	protected boolean notForEdge() {
		return false;
	}

	private void refreshDataForVSphere(BackupSettingsModel d2dModel) {
		if(d2dModel instanceof VSphereBackupSettingModel) {
			VSphereBackupSettingModel model = (VSphereBackupSettingModel)d2dModel;
			if(model.vSphereProxyModel !=null){
				vSphereProxyName.setValue(model.vSphereProxyModel.getVSphereProxyName());
				vSphereProxyUsername.setValue(model.vSphereProxyModel.getVSphereProxyUsername());
				vSphereProxyPassword.setValue(model.vSphereProxyModel.getVSphereProxyPassword());
				vSphereProxyPort.setValue(model.vSphereProxyModel.getvSphereProxyPort());
				if(model.vSphereProxyModel.getVSphereProxyProtocol()!=null && model.vSphereProxyModel.getVSphereProxyProtocol().equals("HTTP")){
					vSphereProxyHttp.setValue(true);
				}else{
					vSphereProxyHttps.setValue(true);
				}
			}
		}
	}
	
	@Override
	public void RefreshData(BackupSettingsModel model, boolean isEdit) {
		super.RefreshData(model, isEdit);
		if(model != null){
			refreshDataForVSphere(model);
			refreshTransportModes(model);
			refresh(model);
		}
	}
	
	private void refresh(BackupSettingsModel model) {
		int quiescenceMethod = model.getVmwareQuiescenceMethod();
		int quiescenceMethodlow = quiescenceMethod & 1;
		if(quiescenceMethodlow == 1)
		{
			vmwareQuiescenceMethodGrp.setValue(vmwareQuiescenceByVSSRadio);			
		}else{
			vmwareQuiescenceMethodGrp.setValue(vmwareQuiescenceByVMToolRadio);
		}
        if ((quiescenceMethod & (1 << 16)) != 0){
            vmwareQuiescenceBySnapshotFailsCheckBox.setValue(true); 
        }
        else{
        	vmwareQuiescenceBySnapshotFailsCheckBox.setValue(false);
        }
		int snapshotType = model.getHyperVConsistentSnapshotType();
		if (snapshotType == 1) {
			hyperVSnapshotVSSMethod.setValue(Boolean.TRUE);
			hyperVSnapshotSavedVMMethod.setValue(Boolean.FALSE);
		} else if (snapshotType == 2) {
			hyperVSnapshotVSSMethod.setValue(Boolean.FALSE);
			hyperVSnapshotSavedVMMethod.setValue(Boolean.FALSE);
		} else if (snapshotType == 3) {
			hyperVSnapshotVSSMethod.setValue(Boolean.TRUE);
			hyperVSnapshotSavedVMMethod.setValue(Boolean.TRUE);
		} else if (snapshotType == 4) {
			hyperVSnapshotVSSMethod.setValue(Boolean.FALSE);
			hyperVSnapshotSavedVMMethod.setValue(Boolean.TRUE);
		} else { //default
			hyperVSnapshotVSSMethod.setValue(Boolean.TRUE);
			hyperVSnapshotSavedVMMethod.setValue(Boolean.FALSE);
		}
		
		hyperVSeparationIndividually.setValue(model.getHyperVSnapshotSeparationIndividually());
	}
	
	@Override
	public void Save() {
		super.Save();
		this.saveVSpereProxy();
	}
	
	@Override
	protected String getErrorMessageTitle() {
		return UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
	}
	
	@Override
	protected String validateSource() {
		return validateVSphereProxy();
	}
	
	@Override
	protected void createPathSelectionPanel() {
		pathSelection = new PathSelectionPanel(this.parentWindow.isForEdge(), new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				String newDest = pathSelection.getDestination();
				setDestChangedBackupType(newDest);
			}
		});

	}
	
	@Override
	protected void onBackupTypeChangedPathSame() {
		destChangedBackupTypeCont.disable();
	}

	private String validateVSphereProxy() {
		boolean isValid = true;
		String msgStr = null;
		
		if(isEmpty(vSphereProxyName.getValue()) || isLocalhost(vSphereProxyName.getValue())){
			msgStr = UIContext.Constants.vSphereProxyNameAlert();
			isValid = false;
		}
		
		if(isValid){
			if(isEmpty(vSphereProxyUsername.getValue())){
				msgStr = UIContext.Constants.vSphereProxyUsernameAlert();
				isValid = false;
			}
		}
		
		if(isValid){
			if(isEmpty(vSphereProxyPassword.getValue())){
				msgStr = UIContext.Constants.vSphereProxyPasswordAlert();
			}
		}
		
		
		return msgStr;
	}
	
	private void saveVSpereProxy() {
		if(SettingPresenter.model instanceof VSphereBackupSettingModel) {
			VSphereBackupSettingModel model = (VSphereBackupSettingModel)SettingPresenter.model;
			model.vSphereProxyModel = new VSphereProxyModel();
			model.vSphereProxyModel.setVSphereProxyName(vSphereProxyName.getValue());
			model.vSphereProxyModel.setVSphereProxyUsername(vSphereProxyUsername.getValue());
			model.vSphereProxyModel.setVSphereProxyPassword(vSphereProxyPassword.getValue());
			String vSphereProxyProtocol = "HTTP";
			if(vSphereProxyHttps.getValue() == true){
				vSphereProxyProtocol = "HTTPS";
			}
			model.vSphereProxyModel.setVSphereProxyProtocol(vSphereProxyProtocol);
			model.vSphereProxyModel.setVSphereProxyPort(vSphereProxyPort.getValue().intValue());
		}
	}

	@Override
	protected void initSourceCotainer(LayoutContainer container) {
		initVSphereContainter(container);
	}
	
	private DisclosurePanel renderTransportModePanel() {
		DisclosurePanel transportModePanel = Utils.getDisclosurePanel(UIContext.Constants.planVMBackupVSphereTransportMethodsTitle());
		LayoutContainer container = new LayoutContainer();
		
		transportByVMwareRadio = new Radio();
		transportByVMwareRadio.ensureDebugId("e3514f99-a665-4d26-be52-1ab5524e7778");
		transportByVMwareRadio.setBoxLabel(UIContext.Constants.planVMBackupVSphereChooseMethodByVMware());
		transportByVMwareRadio.setValue(true);
		
		transportByUserRadio = new Radio();
		transportByUserRadio.ensureDebugId("e3514f99-a665-4d26-be52-1ab5524e7669");
		transportByUserRadio.setBoxLabel(UIContext.Constants.planVMBackupVSphereChooseMethodByUser());
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig configJobType = Utils.createColumnConfig("name",UIContext.Constants.planVMBackupVSphereTransport(), 250, null);
		configJobType.setAlignment(HorizontalAlignment.LEFT);
		configJobType.setResizable(true);
		configJobType.setSortable(true);
		configs.add(configJobType);
		ColumnModel columnModel = new ColumnModel(configs);
		transportStore = new ListStore<BaseModelData>();

		transportGrid = GxtFactory.createSingleSelectGrid(transportStore,columnModel);
		transportGrid.setWidth(300);
		transportGrid.setBorders(true);
		transportGrid.setAutoHeight(true);
		transportGrid.setAutoWidth(true);
		transportGrid.setStripeRows(true);
		transportGrid.setAutoExpandColumn("name");
		//transportGrid.disable();
		transportGrid.setStyleAttribute("padding-bottom", "10px");
		
		container.add(transportByVMwareRadio);
		container.add(transportByUserRadio);
		container.add(transportGrid);
		container.disable();
		transportModePanel.add(container);
		
		return transportModePanel;
	}
	
	private void refreshTransportModes(BackupSettingsModel model) {
		List<String> modes = model.getVmwareTransportModes();
		if (modes == null || modes.size()==0){
			transportByVMwareRadio.setValue(true);
			transportGrid.setVisible(false);
		}else{
			transportByUserRadio.setValue(true);
			transportGrid.setVisible(true);
			transportStore.removeAll();
			for (String mode:modes){
				BaseModelData modelData = new BaseModelData();
				String modeValue = null;
				if (NBD.equals(mode)) {
					modeValue = UIContext.Constants.planVMBackupVSphereNBD();
				} else if (NBDSSL.equals(mode)) {
					modeValue = UIContext.Constants.planVMBackupVSphereNBDSSL();
				} else if (SAN.equals(mode)) {
					modeValue = UIContext.Constants.planVMBackupVSphereSAN();
				} else {
					modeValue = UIContext.Constants.planVMBackupVSphereHotAdd();
				}
				modelData.set("name", modeValue);
				transportStore.add(modelData);
			}
		}
	}
	
	private DisclosurePanel createVmwareQuiescenceMethodComponent(){
		DisclosurePanel disclosurePanel = Utils.getDisclosurePanel(UIContext.Constants.planVMBackupVSphereVMwareQuiescenceTitle());
		LayoutContainer container = new LayoutContainer();
		disclosurePanel.add(container);
		
//		label = new LabelField(UIContext.Constants.planVMBackupVSphereVMwareQuiescenceDescription());
//		container.add(label, createLineLayoutData());
		
		vmwareQuiescenceMethodGrp = new RadioGroup();
		vmwareQuiescenceByVMToolRadio = new Radio();
		vmwareQuiescenceByVMToolRadio.ensureDebugId("944cf419-55ea-4bd6-9645-aded2d9c64ad");
		vmwareQuiescenceByVMToolRadio.setBoxLabel(UIContext.Constants.planVMBackupVSphereVMwareQuiescenceByVMTool());
		vmwareQuiescenceByVMToolRadio.setValue(true);
		
		vmwareQuiescenceByVSSRadio = new Radio();
		vmwareQuiescenceByVSSRadio.ensureDebugId("3201b227-6b1e-4750-92a6-7fe13f628173");
		vmwareQuiescenceByVSSRadio.setBoxLabel(UIContext.Constants.planVMBackupVSphereVMwareQuiescenceByVSS());
	
		vmwareQuiescenceBySnapshotFailsCheckBox = new CheckBox();
		vmwareQuiescenceBySnapshotFailsCheckBox.ensureDebugId("3201b227-6b1e-4750-92a6-7fe13f628249"); //lds , it is set with copying the above 
		vmwareQuiescenceBySnapshotFailsCheckBox.setBoxLabel(UIContext.Constants.planVMBackupVSphereVMwareQuiescenceSnapshotFailsContinue());
		vmwareQuiescenceBySnapshotFailsCheckBox.setValue(false);
		vmwareQuiescenceMethodGrp.add(vmwareQuiescenceByVMToolRadio);
		vmwareQuiescenceMethodGrp.add(vmwareQuiescenceByVSSRadio);
		container.add(vmwareQuiescenceByVMToolRadio, createLineLayoutData());
		container.add(vmwareQuiescenceByVSSRadio, createLineLayoutData());
		container.add(vmwareQuiescenceBySnapshotFailsCheckBox,createLineLayoutData());
		container.disable();
		return disclosurePanel;
	}
	
	private DisclosurePanel createHyperVConsistentSnapshotTypeComponent(){
		DisclosurePanel disclosurePanel = Utils.getDisclosurePanel(UIContext.Constants.planVMBackupVSphereHyperVConsistencyTitle());
		LayoutContainer container = new LayoutContainer();
		disclosurePanel.add(container);
		
//		label = new LabelField(UIContext.Constants.planVMBackupVSphereHyperVConsistencyDescription());
//		container.add(label, createLineLayoutData());
		
		hyperVSnapshotVSSMethod = new CheckBox();
		hyperVSnapshotVSSMethod.ensureDebugId("223daa48-c5b0-43c6-bc8c-2a0bf08fccfc");
		hyperVSnapshotVSSMethod.setBoxLabel(UIContext.Constants.planVMBackupVSphereHyperVConsistencyVSSMethod());
		hyperVSnapshotVSSMethod.setValue(false);
		
		hyperVSnapshotSavedVMMethod = new CheckBox();
		hyperVSnapshotSavedVMMethod.ensureDebugId("43f7fb97-7e30-4604-aa8d-c8109232ab0f");
		hyperVSnapshotSavedVMMethod.setBoxLabel(UIContext.Constants.planVMBackupVSphereHyperVConsistencySavedVMMethod());
		hyperVSnapshotSavedVMMethod.setValue(true);
		
		container.add(hyperVSnapshotVSSMethod, Utils.createLineLayoutData());
		container.add(hyperVSnapshotSavedVMMethod, Utils.createLineLayoutData());
		container.disable();
		return disclosurePanel;
	}
	
	private DisclosurePanel createHyperVSnapshotSeparationComponent(){
		DisclosurePanel disclosurePanel = Utils.getDisclosurePanel(UIContext.Constants.planVMBackupVSphereHyperVSeparationTitle());
		LayoutContainer container = new LayoutContainer();
		disclosurePanel.add(container);
		
//		label = new LabelField(UIContext.Constants.planVMBackupVSphereHyperVSeparationDescription());
//		container.add(label, createLineLayoutData());
		
		hyperVSeparationIndividually = new CheckBox();
		hyperVSeparationIndividually.ensureDebugId("2729a494-c75e-49e1-bd33-3eb0bd23308a");
		hyperVSeparationIndividually.setBoxLabel(UIContext.Constants.planVMBackupVSphereHyperVSeparationIndividually());
		
		container.add(hyperVSeparationIndividually, createLineLayoutData());
		container.disable();
		return disclosurePanel;
	
	}
	
	private LayoutData createLineLayoutData(){
		FlowData l=new FlowData(0,0,10,0);
		return l;
	}
}
