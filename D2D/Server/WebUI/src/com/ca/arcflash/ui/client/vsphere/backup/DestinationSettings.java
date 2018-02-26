package com.ca.arcflash.ui.client.vsphere.backup;


import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.EncryptionPane;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.ErrorAlignedNumberField;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.ca.arcflash.ui.client.model.VSphereProxyModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.HTML;

public class DestinationSettings {
	private VSphereBackupSettingWindow parentWindow;
	
	private PathSelectionPanel pathSelection;

	private Radio destChangedFullBackup;
	private Radio destChangedIncrementalBackup;
	private LayoutContainer destChangedBackupTypeCont;

	private SimpleComboBox<String> compressionOption;

	private NumberField retentionCount;

	private String oldDestinationPath;
	//private Radio enable_enumeration;

	//private Radio disable_enumeration;
	
	private TextField<String> vSphereProxyName;
	
	private TextField<String> vSphereProxyUsername;
	
	private PasswordTextField vSphereProxyPassword;
	
	private NumberField vSphereProxyPort;
	
	private Radio vSphereProxyHttp;
	
	private Radio vSphereProxyHttps;

	private CheckBox throttleCheckBox = new CheckBox();
	
	private ErrorAlignedNumberField throttleField = new ErrorAlignedNumberField();
	
	private DestinationSettings thisWindow;
	
	private EncryptionPane encryptionAlgContainer;
	
	private static Integer allowMaxThrottleValue = new Integer(99999);
	private static Integer allowMinThrottleValue = new Integer(1);

	public DestinationSettings(VSphereBackupSettingWindow window) {
		this.parentWindow = window;
		this.thisWindow = this;
		throttleCheckBox.ensureDebugId("1174FF2D-CA7B-43ff-B458-2E40036E902D");
		throttleField.ensureDebugId("70CC0A9F-BB80-4e0a-B521-6011DE771FF6");
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
		
		initDestinationContainer(container);

		container.add(new HTML("<hr>"));
		
		initVSphereProxyContainer(container);
		
		container.add(new HTML("<hr>"));
		
		initRetentionCountContainer(container);

		container.add(new HTML("<hr>"));
		initCompressionContainer(container);
		
		container.add(new HTML("<hr>"));
		
		initEncryptionContainer(container);
		container.add(new Html("<HR>"));
		
		//Throttle
		initThrottleContainer(container);
		//container.add(new Html("<HR>"));

		/*LayoutContainer enumerationContainer = new LayoutContainer();
		TableLayout enumerationTable = new TableLayout();
		enumerationTable.setColumns(3);
		enumerationTable.setCellPadding(5);
		enumerationTable.setWidth("100%");
		enumerationContainer.setLayout(enumerationTable);

		rg = new RadioGroup();
		enable_enumeration = new Radio();
		enable_enumeration.setBoxLabel(UIContext.Constants.settingsLabelEnableEnumeration());
		enable_enumeration.setValue(false);

		rg.add(enable_enumeration);
		enumerationContainer.add(enable_enumeration);

		disable_enumeration = new Radio();
		disable_enumeration.setBoxLabel(UIContext.Constants.settingsLabelDisableEnumeration());
		disable_enumeration.setValue(true);
		rg.add(disable_enumeration);
		enumerationContainer.add(disable_enumeration);
		container.add(enumerationContainer);*/
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
		
		initDestSelectPane();
		container.add(pathSelection);
		
		destChangedBackupTypeCont = getBackupDestChangedTypePanel();
		destChangedBackupTypeCont.disable();
		container.add(destChangedBackupTypeCont);
	}

	private void initVSphereProxyContainer(LayoutContainer container){
		
		LabelField label = new LabelField();
		label.setValue("D2D VM Backup Proxy");
		label.addStyleName("restoreWizardSubItem");
		container.add(label);
		
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
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vSphereProxyName());
		vSphereProxyContainer.add(label,td1);
		
		vSphereProxyName = new TextField<String>();
		vSphereProxyName.ensureDebugId("7B01EF91-3C3E-4882-B5FD-C7B343CAF473");
		vSphereProxyName.setWidth(250);
		vSphereProxyName.setValue("For Edge");
		vSphereProxyContainer.add(vSphereProxyName,td2);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vSphereProxyUsername());
		vSphereProxyContainer.add(label,td1);
		
		vSphereProxyUsername = new TextField<String>();
		vSphereProxyUsername.ensureDebugId("BE4310C5-6441-41b5-823B-74E0C5FF3A82");
		vSphereProxyUsername.setWidth(150);
		vSphereProxyUsername.setValue("For Edge");
		vSphereProxyContainer.add(vSphereProxyUsername,td2);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vSphereProxyPassword());
		vSphereProxyContainer.add(label,td1);
		
		vSphereProxyPassword = new PasswordTextField();
		vSphereProxyPassword.ensureDebugId("FC28F08A-6884-4e78-80B5-FCF25C9F1611");
		vSphereProxyPassword.setWidth(150);
		vSphereProxyPassword.setPassword(true);
		vSphereProxyPassword.setValue("For Edge");
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
		
		vSphereProxyHttp = new Radio();
		vSphereProxyHttp.ensureDebugId("87216C41-262C-4943-832D-14266017D595");
		vSphereProxyHttp.setBoxLabel("HTTP");
		vSphereProxyHttp.setValue(true);
		
		vSphereProxyHttps = new Radio();
		vSphereProxyHttps.ensureDebugId("BF0C9E0A-BD32-4de5-BACD-3A056BB29C6B");
		vSphereProxyHttps.setBoxLabel("HTTPS");
		
		protocolContainer.add(vSphereProxyHttp,htb);
		protocolContainer.add(vSphereProxyHttps,htb2);
		
		vSphereProxyContainer.add(protocolContainer,td2);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vSphereProxyPort());
		vSphereProxyContainer.add(label,td1);
		
		vSphereProxyPort = new NumberField();
		vSphereProxyPort.ensureDebugId("66C053DD-F515-4703-85BB-8885653E9ED5");
		vSphereProxyPort.setWidth(150);
		vSphereProxyPort.setValue(8014);
		vSphereProxyPort.setAllowBlank(false);
		vSphereProxyPort.setMinValue(0);
		vSphereProxyPort.setAllowDecimals(false);
		vSphereProxyPort.setAllowNegative(false);
		vSphereProxyContainer.add(vSphereProxyPort,td2);
		
		container.add(vSphereProxyContainer);
		
		
	}
	
	private void initDestSelectPane() {
		pathSelection = new PathSelectionPanel(false, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				String newDest = pathSelection.getDestination();	
				setDestChangedBackupType(newDest);
			}
		});
	
		pathSelection.setMode(PathSelectionPanel.BACKUP_MODE);
		pathSelection.setPathFieldLength(440);
		pathSelection.addDebugId("E1206F2A-07DF-40a0-A124-7D8F9D2A97B8", 
				"28C3020C-4B4D-4074-B798-981553301994", 
				"D88EF764-BDA1-41aa-941F-04E31AAA05CD");
	}
	
	private void setDestChangedBackupType(String newDest) {
		if(!isPathChanged(newDest, oldDestinationPath)) {
			destChangedBackupTypeCont.disable();
		}
		else if(!destChangedBackupTypeCont.isEnabled())
		{
			destChangedBackupTypeCont.enable();
			destChangedIncrementalBackup.setValue(true);
			destChangedFullBackup.setValue(false);
		}
	}
	public static boolean isPathChanged(String newDest, String oldDest) {
		if(oldDest == null || oldDest.trim().length() == 0)
			return false;
		
		oldDest = oldDest.trim();
		if(newDest != null)
		{
			newDest = newDest.trim();
			if(newDest.endsWith("/") || newDest.endsWith("\\"))
				newDest = newDest.substring(0, newDest.length() - 1);
		}
		return !oldDest.equalsIgnoreCase(newDest);
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
		destChangedFullBackup.ensureDebugId("00A7B28B-1F63-46d0-9FE0-C49FB2FB57C5");
		destChangedFullBackup.setBoxLabel(UIContext.Constants.backupTypeFull());		
		destChangedFullBackup.setValue(false);
		
		rg.add(destChangedFullBackup);
		tableContainer.add(destChangedFullBackup);
		
		destChangedIncrementalBackup = new Radio();
		destChangedIncrementalBackup.ensureDebugId("CD74119C-1541-4994-893D-49BDD0933A61");
		destChangedIncrementalBackup.setBoxLabel(UIContext.Constants.backupTypeIncremental());		
		destChangedIncrementalBackup.setValue(true);
		rg.add(destChangedIncrementalBackup);
		tableContainer.add(destChangedIncrementalBackup);
		
		return tableContainer;
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
		compressionOption.ensureDebugId("15F3031A-2F39-4e5f-B1DF-9B223A416D19");
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

	private void initEncryptionContainer(LayoutContainer container) {
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelEncryption());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);
		
		//label = new LabelField();
		//label.setText(UIContext.Constants.settingsLabelCompressionDescription());
		//container.add(label);
		
		TableLayout tableEncryptionLayout = new TableLayout();
		tableEncryptionLayout.setWidth("100%");
		tableEncryptionLayout.setCellPadding(4);
		tableEncryptionLayout.setCellSpacing(4);
		tableEncryptionLayout.setColumns(3);
		
		LayoutContainer encryptionContainer = new LayoutContainer();
		encryptionContainer.setLayout(tableEncryptionLayout);
		
		encryptionAlgContainer = new EncryptionPane();
		container.add(encryptionAlgContainer);
		container.add(encryptionAlgContainer.getWarningContainer());
	}
	
	private void initThrottleContainer(LayoutContainer container) {
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.destinationThrottleTitle());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);
		
		LayoutContainer internalContainer = new LayoutContainer();
		internalContainer.setLayout(new HBoxLayout());
		
		throttleCheckBox.setBoxLabel(UIContext.Constants.destinationThrottleDescription());
		throttleCheckBox.addListener(Events.Change, new Listener<FieldEvent>()
				{
					@Override
					public void handleEvent(FieldEvent be) {
						throttleField.setEnabled(throttleCheckBox.getValue());
						if (!throttleCheckBox.getValue())
							throttleField.clear();
					}
			
				});
		internalContainer.add(throttleCheckBox);
		
		throttleField.setWidth(100);
		throttleField.setAllowBlank(false);
		throttleField.setMinValue(1);
		throttleField.setEnabled(false);
		throttleField.setAllowDecimals(false);
		throttleField.setAllowNegative(false);
		throttleField.setMaxValue(allowMaxThrottleValue);
		throttleField.setMinValue(allowMinThrottleValue);
//		throttleField.setStyleAttribute("margin-right", "20px");
		HBoxLayoutData data = new HBoxLayoutData(0, 10, 0, 0);
		internalContainer.add(throttleField, data);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.destinationThrottleUnit());
		internalContainer.add(label);
		container.add(internalContainer);
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
		retentionCount.ensureDebugId("DDE1C147-DB80-4beb-95EF-D1B287F57ACC");
		retentionCount.setAllowDecimals(false);
		retentionCount.setAllowNegative(false);
		retentionCount.setAllowBlank(false);
		retentionCount.setFieldLabel(UIContext.Constants.settingsLabelCount());
		retentionCount.setWidth(250);
		//TODO Default data-2
		retentionCount.setValue(VSphereBackupSettingWindow.DEFAULT_RETENTION_COUNT);

		// Tool tip
		/*ToolTipConfig tipConfig = new ToolTipConfig(UIContext.Constants
				.settingsLabelRetentionTooltip());
		ToolTip tip = new ToolTip(retentionCount, tipConfig);*/

		TableData td = new TableData();
		td.setWidth("40%");
		retentionCont.setLayout(tableLayout);
		retentionCont.add(retentionCount, td);

		container.add(retentionCont);
	}

	/*private boolean isEmpty(final String remoteDest) {
		return remoteDest == null || remoteDest.length() == 0;
	}*/

	public void RefreshData(VSphereBackupSettingModel model) {
		if(model!=null && model.getChangedBackupDestType()!=null){
			if(model.getChangedBackupDestType()==BackupTypeModel.Full)
				destChangedFullBackup.setValue(true);			
		}		
			
		if (model != null && model.getRetentionCount() != null) {
			retentionCount.setValue(model.getRetentionCount());
		} else {
			retentionCount
					.setValue(VSphereBackupSettingWindow.DEFAULT_RETENTION_COUNT);
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

	private boolean isMaxCompression(VSphereBackupSettingModel model) {
		if (model == null || model.getCompressionLevel() == null)
			return false;

		return model.getCompressionLevel() == 9;
	}

	private boolean isStandardCompression(VSphereBackupSettingModel model) {
		if (model == null || model.getCompressionLevel() == null)
			return false;

		return model.getCompressionLevel() == 1;
	}


	public boolean Validate() {
		boolean isValid = true;
		String title = null;
		String msgStr = null;

		Number n = retentionCount.getValue();
		if (n == null || n.intValue() == 0) {
			title = UIContext.Constants.backupSettingsSettings();
			msgStr = UIContext.Constants.settingsRetentionCountErrorTooLow();
			retentionCount.setValue(1);
			isValid = false;
		} else if (n.intValue() > UIContext.maxRPLimit) {
			title = UIContext.Constants.backupSettingsSettings();
			msgStr = UIContext.Messages
					.settingsRetentionCountExceedMax(UIContext.maxRPLimit);
			retentionCount.setValue(UIContext.maxRPLimit);
			retentionCount.fireEvent(Events.Change);
			isValid = false;
		}		
		
		if(isValid){
			if(isNullorEmpty(vSphereProxyName.getValue())){
				title = UIContext.Constants.backupSettingsSettings();
				msgStr = UIContext.Constants.vSphereProxyNameAlert();
				isValid = false;
			}
		}
		
		if(isValid){
			if(isNullorEmpty(vSphereProxyUsername.getValue())){
				title = UIContext.Constants.backupSettingsSettings();
				msgStr = UIContext.Constants.vSphereProxyUsernameAlert();
				isValid = false;
			}
		}
		
		if(isValid){
			if(isNullorEmpty(vSphereProxyPassword.getValue())){
				title = UIContext.Constants.backupSettingsSettings();
				msgStr = UIContext.Constants.vSphereProxyPasswordAlert();
				isValid = false;
			}
		}
		
		if(isValid){
			isValid = this.vSphereProxyPort.validate();
			if(!isValid) {
				title = UIContext.Constants.backupSettingsSettings();
				msgStr = UIContext.Constants.vSphereProxyPortAlert();
			}
		}
		
		if(isValid) {
			if(!encryptionAlgContainer.validate())
				return false;
		}

		if (isValid){
			if (this.throttleCheckBox.getValue()){
				isValid = this.throttleField.validate();
				if(!isValid) {
					title = UIContext.Constants.backupSettingsSettings();
					msgStr = UIContext.Constants.destinationThrottleValueErrorTip();
				}
			}
		}
		
		if (!isValid) {
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(title);
			msg.setMessage(msgStr);
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
			return false;
		}
		return true;
	}

	private boolean isNullorEmpty(String input){
		if(input == null || input.equals("")){
			return true;
		}
		return false;
	}
	
	public void Save() {
		
		String path = pathSelection.getDestination();
		parentWindow.model.setDestination(path);
		if(!PathSelectionPanel.isLocalPath(path)) {
			parentWindow.model.setDestUserName(pathSelection.getUsername());
			parentWindow.model.setDestPassword(pathSelection.getPassword());
		}
		else {
			parentWindow.model.setDestUserName(null);
			parentWindow.model.setDestPassword(null);
		}
		
		if(destChangedBackupTypeCont.isEnabled())
		{
			parentWindow.model.setChangedBackupDest(Boolean.TRUE);
			if(Boolean.TRUE.equals(destChangedFullBackup.getValue()))
				parentWindow.model.setChangedBackupDestType(BackupTypeModel.Full);
			else
				parentWindow.model.setChangedBackupDestType(BackupTypeModel.Incremental);
		}
		else
		{
			parentWindow.model.setChangedBackupDest(Boolean.FALSE);
			parentWindow.model.setChangedBackupDestType(BackupTypeModel.Full);
		}
		
		
		parentWindow.model.setRetentionCount(retentionCount.getValue()
				.intValue());

		int compressionLevel = this.getCompressionLevel();
		if (compressionLevel != -1) {
			parentWindow.model.setCompressionLevel(compressionLevel);
			Integer encryptAlgType = encryptionAlgContainer.getEncryptAlgorithm();
			Boolean enableEncryption = encryptAlgType != null && encryptAlgType > 0/* && compressionLevel > 0*/;
			parentWindow.model.setEnableEncryption(enableEncryption);
			if(/*compressionLevel > 0 && */enableEncryption) {
				parentWindow.model.setEncryptionAlgorithm(encryptAlgType);
				parentWindow.model.setEncryptionKey(encryptionAlgContainer.getEncryptPassword());
			}
			else {
				parentWindow.model.setEncryptionAlgorithm(0);
				parentWindow.model.setEncryptionKey("");
			}
		}

		if (Boolean.TRUE.equals(destChangedFullBackup.getValue()))
			parentWindow.model.setChangedBackupDestType(BackupTypeModel.Full);
		else
			parentWindow.model
					.setChangedBackupDestType(BackupTypeModel.Incremental);

		/*if (Boolean.TRUE.equals(enable_enumeration.getValue()))
			parentWindow.model.setEnableEnumeration(true);
		else
			parentWindow.model.setEnableEnumeration(false);*/
		
		if (this.throttleCheckBox.getValue()){
			parentWindow.model.setThrottling(this.throttleField.getValue().longValue());
		}else
			parentWindow.model.setThrottling(0L);
		
		//These info is for edge
		parentWindow.model.vSphereProxyModel = new VSphereProxyModel();
		parentWindow.model.vSphereProxyModel.setVSphereProxyName(vSphereProxyName.getValue());
		parentWindow.model.vSphereProxyModel.setVSphereProxyUsername(vSphereProxyUsername.getValue());
		parentWindow.model.vSphereProxyModel.setVSphereProxyPassword(vSphereProxyPassword.getValue());
		String vSphereProxyProtocol = "HTTP";
		if(vSphereProxyHttps.getValue() == true){
			vSphereProxyProtocol = "HTTPS";
		}
		parentWindow.model.vSphereProxyModel.setVSphereProxyProtocol(vSphereProxyProtocol);
		parentWindow.model.vSphereProxyModel.setVSphereProxyPort(vSphereProxyPort.getValue().intValue());
		
		
	}

	private int getCompressionLevel() {
		int compressionLevel = -1;
		if (compressionOption.getSimpleValue() == UIContext.Constants
				.settingsCompressionNone())
			compressionLevel = 0;
		else if (compressionOption.getSimpleValue() == UIContext.Constants
				.settingsCompreesionStandard())
			compressionLevel = 1;
		else if (compressionOption.getSimpleValue() == UIContext.Constants
				.settingsCompressionMax())
			compressionLevel = 9;
		return compressionLevel;
	}
}
