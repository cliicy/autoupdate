package com.ca.arcflash.ui.client.vsphere.vmbackup;


import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.BackupSettingsWindow;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.user.client.ui.HTML;


public class DestinationSettings {	
	private VMBackupSettingWindow parentWindow;

	private Radio destChangedFullBackup;
	private Radio destChangedIncrementalBackup;

	private PathSelectionPanel pathSelection;

	private SimpleComboBox<String> compressionOption;

	private NumberField retentionCount;

	private Radio enable_enumeration;

	private Radio disable_enumeration;

	private DestinationSettings thisWindow;

	private TextField<String> vmNameTF;

	private TextField<String> hostNameTF;

	private TextField<String> serverNameTF;

	private TextField<String> userTF;

	private PasswordTextField pwdTF;

	private NumberField portTF;

	private CheckBox protocolCB;

	public DestinationSettings(VMBackupSettingWindow window) {
		this.parentWindow = window;
		this.thisWindow = this;
	}

	public LayoutContainer Render() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("97%");
		tl.setHeight("95%");
		tl.setCellPadding(0);
		tl.setCellSpacing(0);
		container.setLayout(tl);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsDestination());
		label.addStyleName("restoreWizardTitle");
		container.add(label);

		initVMContainer(container);
		container.add(new HTML("<hr>"));

		initDestinationContainer(container);
		container.add(new Html("<HR>"));

		initRetentionCountContainer(container);

		container.add(new HTML("<hr>"));
		initCompressionContainer(container);

		container.add(new HTML("<hr>"));

		LayoutContainer enumerationContainer = new LayoutContainer();
		TableLayout enumerationTable = new TableLayout();
		enumerationTable.setColumns(3);
		enumerationTable.setCellPadding(5);
		enumerationTable.setWidth("100%");
		enumerationContainer.setLayout(enumerationTable);

		RadioGroup rg = new RadioGroup();
		enable_enumeration = new Radio();
		enable_enumeration.ensureDebugId("F2090BA9-2AAD-4f85-ABAF-B7A571D07F5B");
		enable_enumeration.setBoxLabel(UIContext.Constants.settingsLabelEnableEnumeration());
		enable_enumeration.setValue(false);

		rg.add(enable_enumeration);
		enumerationContainer.add(enable_enumeration);

		disable_enumeration = new Radio();
		disable_enumeration.ensureDebugId("00ACCFD9-55A8-49e2-9C6F-F4F658F0AB2D");
		disable_enumeration.setBoxLabel(UIContext.Constants.settingsLabelDisableEnumeration());
		disable_enumeration.setValue(true);
		rg.add(disable_enumeration);
		enumerationContainer.add(disable_enumeration);
		container.add(enumerationContainer);
		return container;
	}

	private void initDestinationContainer(LayoutContainer container) {
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.destinationBackupDestination());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);

		label = new LabelField();
		label.setValue(UIContext.Constants
				.destinationLabelDestinationDescription());
		container.add(label);

		pathSelection = new PathSelectionPanel(new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				// TODO
			}

		});
		pathSelection.setMode(PathSelectionPanel.BACKUP_MODE);
		pathSelection.setPathFieldLength(440);
		pathSelection.addDebugId("4C0F52CF-493D-4043-8F89-57A1B958F044", 
				"818EF092-BA88-43e2-A03C-8D6B083296E0", "DB244377-E9E3-4218-A42D-C329150C3B90");
		container.add(pathSelection);

		LayoutContainer destChangedBackupTypeCont = getBackupDestChangedTypePanel();
		container.add(destChangedBackupTypeCont);

	}

	private LayoutContainer getBackupDestChangedTypePanel() {
		LayoutContainer tableContainer = new LayoutContainer();
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(4);
		tableLayout.setCellSpacing(4);
		tableLayout.setColumns(2);
		tableLayout.setWidth("95%");
		tableContainer.setLayout(tableLayout);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.destinationChangedSelectBackupType());
		TableData data = new TableData();
		data.setColspan(2);
		tableContainer.add(label, data);

		RadioGroup rg = new RadioGroup();
		destChangedFullBackup = new Radio();
		destChangedFullBackup.ensureDebugId("F5DE7B5C-5161-467e-8310-86AE459D0563");
		destChangedFullBackup.setBoxLabel(UIContext.Constants.backupTypeFull());
		destChangedFullBackup.setValue(false);
		rg.add(destChangedFullBackup);
		tableContainer.add(destChangedFullBackup);

		destChangedIncrementalBackup = new Radio();
		destChangedIncrementalBackup.ensureDebugId("65FB0351-310A-4f68-B3DC-3314A2178BB4");
		destChangedIncrementalBackup.setBoxLabel(UIContext.Constants
				.backupTypeIncremental());
		destChangedIncrementalBackup.setValue(true);
		rg.add(destChangedIncrementalBackup);
		tableContainer.add(destChangedIncrementalBackup);

		return tableContainer;
	}

	private void initVMContainer(LayoutContainer container) {
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmBackupSettingsVirtualMachineInformation());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);

		addVMPanel(container);
	}

	private void addVMPanel(LayoutContainer container) {
		FormPanel panel = new FormPanel();
		panel.setPadding(4);
		panel.setBorders(false);
		panel.setBodyBorder(false);
		panel.setFrame(false);
		panel.setHeaderVisible(false);
		panel.setLabelWidth(150);

		// LabelField label = new LabelField();
		// label
		// .setText("virtual machine information");
		// panel.add(label);

		vmNameTF = new TextField<String>();
		vmNameTF.ensureDebugId("58700210-33F9-4d6e-BA6C-0D4D94335D8E");
		vmNameTF.setAllowBlank(false);
		vmNameTF.setFieldLabel(UIContext.Constants.settingsLabelVMName());
		vmNameTF.setEnabled(false);
		panel.add(vmNameTF);

		hostNameTF = new TextField<String>();
		hostNameTF.ensureDebugId("08611AFB-F660-4cd9-9C0F-08DB5324E3D5");
		hostNameTF.setAllowBlank(false);
		hostNameTF.setFieldLabel(UIContext.Constants.settingsLabelHostName());
		hostNameTF.setEnabled(false);
		panel.add(hostNameTF);

		LabelField label = new LabelField();
		label.setValue("");
		panel.add(label);

		serverNameTF = new TextField<String>();
		serverNameTF.ensureDebugId("7D5E91D9-2A98-48b8-A307-2797D6D47900");
		serverNameTF.setAllowBlank(false);
		serverNameTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerServerNameLabel());
//		serverNameTF.setEnabled(false);
		panel.add(serverNameTF);

		userTF = new TextField<String>();
		userTF.ensureDebugId("233425F6-9421-4e9b-8DC3-6463605836C3");
		userTF.setAllowBlank(false);
		userTF.setValidateOnBlur(false);
		userTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerUserNameLabel());
//		userTF.setEnabled(false);
		panel.add(userTF);

		pwdTF = new PasswordTextField();
		pwdTF.ensureDebugId("EA6BEDC3-8C0F-49b0-9DCE-5C425BBAEF8A");
		pwdTF.setPassword(true);
		pwdTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerPasswordLabel());
//		pwdTF.setEnabled(false);
		panel.add(pwdTF);

		portTF = new NumberField();
		portTF.ensureDebugId("F18D7BE4-7B39-4abd-B43C-CB6804CEDBCB");
		portTF.setPropertyEditorType(Integer.class);
		portTF.setValue(0);
		portTF.setAllowBlank(false);
//		portTF.setEnabled(false);
		portTF.setAllowDecimals(false);
		portTF.setAllowNegative(false);
		portTF.setMaxValue(65535);
		portTF.setMinValue(0);
		portTF.setValidateOnBlur(true);
		portTF.setWidth("50%");
		portTF.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				return null;
			}
		});
		portTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerPortLabel());
		FormData fd = new FormData();
		fd.setWidth(100);
		panel.add(portTF, fd);

		protocolCB = new CheckBox();
		protocolCB.ensureDebugId("F54A9008-7C01-4e2c-A535-2DC44BFECCE2");
		protocolCB.setFieldLabel(UIContext.Constants.remoteDeployAddServerProtocolLabel());
		protocolCB.setWidth("40");
//		protocolCB.setEnabled(false);
		FormData fdP = new FormData();
		fdP.setWidth(15);
		panel.add(protocolCB, fdP);

		container.add(panel);
	}

	private void initCompressionContainer(LayoutContainer container) {
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelCompression());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);

		label = new LabelField();
		label
				.setValue(UIContext.Constants
						.settingsLabelCompressionDescription());
		container.add(label);

		TableLayout tableCompressionLayout = new TableLayout();
		tableCompressionLayout.setWidth("100%");
		tableCompressionLayout.setCellPadding(4);
		tableCompressionLayout.setCellSpacing(4);
		tableCompressionLayout.setColumns(1);

		LayoutContainer compressionContainer = new LayoutContainer();
		compressionContainer.setLayout(tableCompressionLayout);

		compressionOption = new BaseSimpleComboBox<String>();
		compressionOption.ensureDebugId("D990AC03-9E38-4e6b-BA4D-AEA74C5096A0");
		compressionOption.setEditable(false);
		compressionOption.add(UIContext.Constants.settingsCompressionNone());
		compressionOption
				.add(UIContext.Constants.settingsCompreesionStandard());
		compressionOption.add(UIContext.Constants.settingsCompressionMax());
		compressionOption.setSimpleValue(UIContext.Constants
				.settingsCompreesionStandard());
		Utils.addToolTip(compressionOption, UIContext.Constants
				.settingsLabelCompressionStandardTooltip());
		compressionContainer.add(compressionOption);
		compressionOption.setWidth(200);
		compressionContainer.setWidth(250);
		container.add(compressionContainer);

	}

	private void initRetentionCountContainer(LayoutContainer container) {
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelRetentionCount());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);

		label = new LabelField();
		label.setValue(UIContext.Messages.settingsLabelRetentionDescription(UIContext.productNamevSphere));
		container.add(label);

		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(4);
		tableLayout.setCellSpacing(4);
		tableLayout.setColumns(1);

		LayoutContainer retentionCont = new LayoutContainer();

		retentionCount = new NumberField();
		retentionCount.ensureDebugId("CD8E9370-4390-4620-8395-91913AF2259F");
		retentionCount.setAllowDecimals(false);
		retentionCount.setAllowNegative(false);
		retentionCount.setAllowBlank(false);
		retentionCount.setFieldLabel(UIContext.Constants.settingsLabelCount());
		retentionCount.setWidth(250);
		// TODO Default data-2
		retentionCount.setValue(BackupSettingsWindow.DEFAULT_RETENTION_COUNT);

		// Tool tip
		ToolTipConfig tipConfig = new ToolTipConfig(UIContext.Constants
				.settingsLabelRetentionTooltip());
		ToolTip tip = new ToolTip(retentionCount, tipConfig);
		tip.ensureDebugId("8B849B86-2926-4631-A31B-4C6292148D99");
		tip.setHeaderVisible(false);

		TableData td = new TableData();
		td.setWidth("40%");
		retentionCont.setLayout(tableLayout);
		retentionCont.add(retentionCount, td);

		container.add(retentionCont);
	}

	public void RefreshData(VMBackupSettingModel model) {
		if (model != null && model.backupVM != null) {
			BackupVMModel vmModel = model.backupVM;
			if (vmModel.getVMName() != null)
				vmNameTF.setValue(vmModel.getVMName());
			if (vmModel.getVmHostName() != null)
				hostNameTF.setValue(vmModel.getVmHostName());
			if (vmModel.getDestination() != null)
				pathSelection.setDestination(vmModel.getDestination());
			// TODO share folder
			if (vmModel.getDesUsername() != null)
				pathSelection.setUsername(vmModel.getDesUsername());
			if (vmModel.getDesPassword() != null)
				pathSelection.setPassword(vmModel.getDesPassword());
			
			if (vmModel.getEsxServerName()!= null)
				serverNameTF.setValue(vmModel.getEsxServerName());
			if (vmModel.getEsxUsername() != null)
				userTF.setValue(vmModel.getEsxUsername());
			if (vmModel.getEsxPassword() != null)
				pwdTF.setValue(vmModel.getEsxPassword());
			if (vmModel.getProtocol() != null
					&& vmModel.getProtocol().equals("https"))
				protocolCB.setValue(true);			
			if (vmModel.getPort() > 0)
				portTF.setValue(vmModel.getPort());
		}


		if (model != null && model.getChangedBackupDestType() != null) {
			if (model.getChangedBackupDestType() == BackupTypeModel.Full)
				destChangedFullBackup.setValue(true);
		}

		if (model != null && model.getRetentionCount() != null) {
			retentionCount.setValue(model.getRetentionCount());
		} else {
			retentionCount
					.setValue(BackupSettingsWindow.DEFAULT_RETENTION_COUNT);
		}

		if (model != null && model.getCompressionLevel() != null) {
			if (isStandardCompression(model))
				compressionOption.setSimpleValue(UIContext.Constants
						.settingsCompreesionStandard());
			else if (isMaxCompression(model))
				compressionOption.setSimpleValue(UIContext.Constants
						.settingsCompressionMax());
			else
				compressionOption.setSimpleValue(UIContext.Constants
						.settingsCompressionNone());
		} else {
			compressionOption.setSimpleValue(UIContext.Constants
					.settingsCompreesionStandard());
		}

	}

	private boolean isMaxCompression(VMBackupSettingModel model) {
		if (model == null || model.getCompressionLevel() == null)
			return false;

		return model.getCompressionLevel() == 9;
	}

	private boolean isStandardCompression(VMBackupSettingModel model) {
		if (model == null || model.getCompressionLevel() == null)
			return false;

		return model.getCompressionLevel() == 1;
	}
	
}
