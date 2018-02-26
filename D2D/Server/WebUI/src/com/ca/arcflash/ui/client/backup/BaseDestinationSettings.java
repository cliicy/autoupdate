package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.ErrorAlignedNumberField;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UncPath;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.VolumeModel;
import com.ca.arcflash.ui.client.model.encrypt.EncryptionAlgModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Image;

public abstract class BaseDestinationSettings {
	//private static int MIN_FIELD_WIDTH = 250;

	final CommonServiceAsync service = GWT.create(CommonService.class);

	final ColdStandbyServiceAsync coldStandbyService = GWT.create(ColdStandbyService.class);

	protected PathSelectionPanel pathSelection;

	protected BackupSettingsContent parentWindow;

	protected Radio destChangedFullBackup;
	private Radio destChangedIncrementalBackup;
	protected LayoutContainer destChangedBackupTypeCont;	

	protected String oldDestinationPath;	

	protected VolumeModel currentBackupDestVolume;
	protected SimpleComboBox<String> compressionOption;
	public LayoutContainer warningContainer;
	private LabelField warningLabel;
	protected LoadingStatus warnLoadingStatus;	

	protected LabelField unsupportedVolumeDesc = null;

	private EncryptionPane encryptionAlgContainer;
	private CheckBox throttleCheckBox = new CheckBox();

	private ErrorAlignedNumberField throttleField = new ErrorAlignedNumberField();
	private static Integer allowMaxThrottleValue = new Integer(99999);
	private static Integer allowMinThrottleValue = new Integer(1);
	
	protected ISettingsContentHost contentHost = null;
	
	private LayoutContainer destPathContainer = null;
	protected BackupRPSDestSettingsPanel destRpsContainer = null;
	private Radio rpsDestinationRadio;
	private Radio d2dDestinationRadio;

	//session password
	private DisclosurePanel sessionPasswordSettingsPanel;
	private SessionPasswordPanel sessionPasswordPanel;
	
	private DisclosurePanel retentionForStandarFormatPanel;
//	private DisclosurePanel retentionForAdvancedFormatPanel;
	private DisclosurePanel encryptionSettingsPanelForRps;

	private EncryptionPane encryptionAlgContainerForRps;

	private DisclosurePanel encryptionSettingsPanel;
	
	private DisclosurePanel throttleSettingsPanel;
	
	private LayoutContainer backupDataFormatPanelContainer;
	private Radio standardFormat;
	private Radio advancedFormat;
	
	private Html line;
	
//	private RetentionRecoveryPointPanel retentionRecoveryPoint4AdvancedFormat;
	protected BackupSetSettings retentionRecoverySetPanel;
	public BaseDestinationSettings(BackupSettingsContent w) {
		parentWindow = w;
	}
	
	protected abstract void initSourceCotainer(LayoutContainer container);

	public LayoutContainer Render() {
		LayoutContainer container = new LayoutContainer();

		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("97%");
		container.setLayout(tl);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsDestination());
		label.addStyleName("restoreWizardTitle");
		container.add(label);

		initDestinationContainer(container);
		
		initSourceCotainer(container);
		
		initBackupDataFormatContainer(container);	
		
		initRetentionContainer(container);
		
	//	initSimpleRetentionContainer(container);
		//Compression Section
		initCompressionContainer(container);		

		initEncryptionContainer(container);
		
		initEncryptionContainerForRps(container);

		initSessionPasswordContainer(container);
		
		//Throttle
		//if (UIContext.isAdvSchedule==false)
			initThrottleContainer(container);
			
		SettingPresenter.getInstance().addListener(new Listener<AppEvent>() {
			@Override
			public void handleEvent(AppEvent be) {
				if (SettingPresenter.getInstance().isBackupDataFormatNew(be)) {
					throttleSettingsPanel.setVisible(false);
					
			//		retentionForAdvancedFormatPanel.setVisible(true);
					retentionForStandarFormatPanel.setVisible(false);
				} else {
					throttleSettingsPanel.setVisible(true);
					
				//	retentionForAdvancedFormatPanel.setVisible(false);
					retentionForStandarFormatPanel.setVisible(true);
				}
				
				Boolean isDestDataStore = rpsDestinationRadio!= null? rpsDestinationRadio.getValue():null;
				if(isDestDataStore !=null && isDestDataStore){
					hideRetentionSettingsForDataStore();
				}
			}
		});
		return container;
	}
	
	private void hideRetentionSettingsForDataStore(){
//		if(retentionForAdvancedFormatPanel != null)
//			retentionForAdvancedFormatPanel.setVisible(false);
		
		if(retentionForStandarFormatPanel != null)
			retentionForStandarFormatPanel.setVisible(false);
	}
	
	//wanqi06 added
	private void initRetentionContainer(LayoutContainer container) {
		retentionRecoverySetPanel = new BackupSetSettings(parentWindow);
		retentionForStandarFormatPanel = Utils.getDisclosurePanel(UIContext.Constants.backupSettingsBackupSet());		
		LayoutContainer retentionPolicyContainer = new LayoutContainer();		
		retentionPolicyContainer.add(retentionRecoverySetPanel);		
		retentionPolicyContainer.add(new Html("<HR>"));
		retentionForStandarFormatPanel.add(retentionPolicyContainer);				
		container.add(retentionForStandarFormatPanel);
		
	}
	
//	private void initSimpleRetentionContainer(LayoutContainer container) {			
//		LayoutContainer lc = new LayoutContainer();	
//		retentionRecoveryPoint4AdvancedFormat = new RetentionRecoveryPointPanel(parentWindow);	
//		lc.add(retentionRecoveryPoint4AdvancedFormat);		
//		lc.add(new Html("<HR>"));
//		
//		retentionForAdvancedFormatPanel = Utils.getDisclosurePanel(UIContext.Constants.backupSettingsBackupSet());	
//		retentionForAdvancedFormatPanel.add(lc);	
//		
//		container.add(retentionForAdvancedFormatPanel);
//		
//	}
	
	private void initBackupDataFormatContainer(LayoutContainer container) {		
		DisclosurePanel backupDataFormatPanel = Utils.getDisclosurePanel(UIContext.Constants.backupDataFormat());		
		backupDataFormatPanelContainer = new LayoutContainer();		
		backupDataFormatPanelContainer.add(getBackupDataFormatPanel());		
		backupDataFormatPanelContainer.add(new Html("<HR>"));
		backupDataFormatPanel.add(backupDataFormatPanelContainer);				
		container.add(backupDataFormatPanel);
		
	}
	
	private LayoutContainer getBackupDataFormatPanel() {
		LayoutContainer tableContainer = new LayoutContainer();
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(4);
		tableLayout.setCellSpacing(0);
		tableLayout.setColumns(2);
		tableLayout.setWidth("100%");
		tableContainer.setLayout(tableLayout);	

		final RadioGroup rgDataFormat = new RadioGroup();
		
		standardFormat = new Radio();		
		standardFormat.setBoxLabel(UIContext.Constants.Standard());
		standardFormat.setValue(false);
		rgDataFormat.add(standardFormat);
		tableContainer.add(standardFormat);	
		
		advancedFormat = new Radio();		
		advancedFormat.setBoxLabel(UIContext.Constants.Advanced());
		advancedFormat.setValue(true);
		rgDataFormat.add(advancedFormat);
		tableContainer.add(advancedFormat);
		
		rgDataFormat.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if((Boolean)be.getValue()){
					if(rgDataFormat.getValue() == standardFormat)
						fireFormatChangeEvent(0);
					else{
						fireFormatChangeEvent(1);
					}
				}
			}
		});	
		
		return tableContainer;
	}
	
	private void fireFormatChangeEvent(int format){
		SettingPresenter.backupDataFormatEvent.setData("format", format);
		SettingPresenter.getInstance().fireEvent(SettingPresenter.backupDataFormatEvent);	
	}

	private void initThrottleContainer(LayoutContainer container) {		
		throttleSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.destinationThrottleTitle());
		throttleSettingsPanel.ensureDebugId("8513578D-D1D8-49a3-B6A9-96440EFFDE46");
		throttleSettingsPanel.setWidth("100%");
		throttleSettingsPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		throttleSettingsPanel.setOpen(true);
		
		LayoutContainer throttleSettingsContainer = new LayoutContainer();

		LayoutContainer internalContainer = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(3);
		internalContainer.setLayout(tableLayout);

		throttleCheckBox.ensureDebugId("5648D570-34E8-4cfe-A80B-2C1FA0586F65");
		throttleCheckBox.setBoxLabel(UIContext.Constants.destinationThrottleDescription());
		throttleCheckBox.setToolTip(UIContext.Constants.destinationThrottleTooltip());
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

		throttleField.ensureDebugId("CD0AF3A7-5525-4dac-BF49-288767104BCF");
		throttleField.setWidth(100);
		throttleField.setAllowBlank(false);
		throttleField.setMinValue(1);
		throttleField.setEnabled(false);
		throttleField.setAllowDecimals(false);
		throttleField.setAllowNegative(false);
		throttleField.setMaxValue(allowMaxThrottleValue);
		throttleField.setMinValue(allowMinThrottleValue);
//		throttleField.setStyleAttribute("margin-right", "20px");
		internalContainer.add(throttleField);

		LabelField label = new LabelField();
		label.setStyleAttribute("margin-left", "5px");
		label.setValue(UIContext.Constants.destinationThrottleUnit());
		internalContainer.add(label);
		throttleSettingsContainer.add(internalContainer);
		if (notForEdge())
		{
			throttleSettingsContainer.add(new Html("<HR>"));
		}
		else // for Edge
		{
			throttleSettingsContainer.add(new Html("<BR>"));
		}
		throttleSettingsPanel.add(throttleSettingsContainer);
		container.add(throttleSettingsPanel);
	}	

	private void initCompressionContainer(LayoutContainer container) {		
		DisclosurePanel compressionSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.settingsLabelCompression());
		compressionSettingsPanel.ensureDebugId("52FE3ED5-6738-4d87-B34B-D3B41AC465F0");
		compressionSettingsPanel.setWidth("100%");
		compressionSettingsPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		compressionSettingsPanel.setOpen(true);
		
		LayoutContainer comprSettingsContainer = new LayoutContainer();
	
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelCompressionDescription());
		comprSettingsContainer.add(label);

		TableLayout tableCompressionLayout = new TableLayout();
		tableCompressionLayout.setWidth("100%");
		tableCompressionLayout.setCellPadding(4);
		tableCompressionLayout.setCellSpacing(4);
		tableCompressionLayout.setColumns(3);

		LayoutContainer compressionContainer = new LayoutContainer();
		compressionContainer.setLayout(tableCompressionLayout);

		compressionOption = new BaseSimpleComboBox<String>();
		compressionOption.ensureDebugId("75D99A0E-1366-41af-9003-F7C719B86037");
		compressionOption.setEditable(false);
		compressionOption.add(UIContext.Constants.settingsCompressionNone());
		compressionOption.add(UIContext.Constants.settingsCompreesionStandard());
		compressionOption.add(UIContext.Constants.settingsCompressionMax());
		compressionOption.setSimpleValue(UIContext.Constants.settingsCompreesionStandard());
//		compressionOption.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>(){
//			@Override
//			public void selectionChanged(
//					SelectionChangedEvent<SimpleComboValue<String>> se) {
//				SettingPresenter.backupDataFormatEvent.setData("format", getCompressionLevel());
//				SettingPresenter.getInstance().fireEvent(SettingPresenter.backupDataFormatEvent);				
//			}});
		Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
		compressionContainer.add(compressionOption);
		compressionOption.setWidth(200);
		compressionContainer.setWidth(250);
		comprSettingsContainer.add(compressionContainer);

		warningContainer = new LayoutContainer();
		TableLayout warningLayout = new TableLayout();
		warningLayout.setCellSpacing(4);
		warningLayout.setColumns(2);
		warningContainer.setLayout(warningLayout);
		
		if(!parentWindow.isShowForVSphere())			
			addPanelForCompressionChanged(comprSettingsContainer);
		
		comprSettingsContainer.add(new Html("<HR>"));
		compressionSettingsPanel.add(comprSettingsContainer);
		container.add(compressionSettingsPanel);
		
	}

	private void initSessionPasswordContainer(LayoutContainer container){
		sessionPasswordSettingsPanel 
			= Utils.getDisclosurePanel(UIContext.Constants.settingsLabelSessionPassword());
		
		LayoutContainer sessionPasswordSettingsContainer = new LayoutContainer();
		TableLayout tableEncryptionLayout = new TableLayout();
		tableEncryptionLayout.setWidth("100%");
		tableEncryptionLayout.setCellPadding(4);
		tableEncryptionLayout.setCellSpacing(4);
		tableEncryptionLayout.setColumns(3);

		LayoutContainer encryptionContainer = new LayoutContainer();
		encryptionContainer.setLayout(tableEncryptionLayout);

		sessionPasswordPanel = new SessionPasswordPanel();
		sessionPasswordSettingsContainer.add(sessionPasswordPanel);
		line = new Html("<HR>");
		sessionPasswordSettingsContainer.add(line);
		sessionPasswordSettingsPanel.add(sessionPasswordSettingsContainer);
		container.add(sessionPasswordSettingsPanel);
		sessionPasswordSettingsPanel.setVisible(false);
	}
	
	private LayoutContainer addPanelForCompressionChanged(LayoutContainer comprSettingsContainer) {
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.status_small_warning()).createImage();
		warningContainer.add(warningImage,new TableData());
		warningLabel = new LabelField();
		warningLabel.setValue(UIContext.Constants.backupCompressionChanged());
		warningContainer.add(warningLabel,new TableData());
		warningContainer.setVisible(false);
		comprSettingsContainer.add(warningContainer);

		compressionOption.addListener(Events.Select, new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				String selString = compressionOption.getSimpleValue();
				if (selString.compareTo(UIContext.Constants.settingsCompressionNone()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionNoneTooltip());					
				}
				else if (selString.compareTo(UIContext.Constants.settingsCompreesionStandard()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
				}
				else if (selString.compareTo(UIContext.Constants.settingsCompressionMax()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionMaxTooltip());
				}

			}
		});
		
		return warningContainer;
	}


	private void initEncryptionContainer(LayoutContainer container) {		
		encryptionSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.settingsLabelEncryption());
		encryptionSettingsPanel.ensureDebugId("1EFADB80-E05C-4165-8104-8F9D06E403F4");
		encryptionSettingsPanel.setWidth("100%");
		encryptionSettingsPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		encryptionSettingsPanel.setOpen(true);
		
		LayoutContainer encryptSettingsContainer = new LayoutContainer();

		TableLayout tableEncryptionLayout = new TableLayout();
		tableEncryptionLayout.setWidth("100%");
		tableEncryptionLayout.setCellPadding(4);
		tableEncryptionLayout.setCellSpacing(4);
		tableEncryptionLayout.setColumns(3);

		LayoutContainer encryptionContainer = new LayoutContainer();
		encryptionContainer.setLayout(tableEncryptionLayout);

		encryptionAlgContainer = new EncryptionPane();
		encryptSettingsContainer.add(encryptionAlgContainer);
		encryptSettingsContainer.add(encryptionAlgContainer.getWarningContainer());
		encryptSettingsContainer.add(new Html("<HR>"));

		encryptionSettingsPanel.add(encryptSettingsContainer);
		container.add(encryptionSettingsPanel);
	}
	
	public void addEncAlgSelectionChangedHandler(SelectionChangedListener<EncryptionAlgModel> selectionChangedListener){
		encryptionAlgContainerForRps.addEncAlgSelectionChangedHandler(selectionChangedListener);
		encryptionAlgContainer.addEncAlgSelectionChangedHandler(selectionChangedListener);
	}

	private void initEncryptionContainerForRps(LayoutContainer container){
		encryptionSettingsPanelForRps = Utils.getDisclosurePanel(UIContext.Constants.settingsLabelEncryption());		
		LayoutContainer encryptSettingsContainer = new LayoutContainer();
		
		TableLayout tableEncryptionLayout = new TableLayout();
		tableEncryptionLayout.setWidth("100%");
		tableEncryptionLayout.setCellPadding(4);
		tableEncryptionLayout.setCellSpacing(4);
		tableEncryptionLayout.setColumns(3);

		LayoutContainer encryptionContainer = new LayoutContainer();
		encryptionContainer.setLayout(tableEncryptionLayout);

		encryptionAlgContainerForRps = new EncryptionPane();
		encryptionAlgContainerForRps.setDebugID("6bf0aa6d-51b9-47da-80ca-7e456af9b602", "41105fe6-df6e-439f-b780-25dfe79d6e99", "10436db3-7e02-42d0-996b-33a7d3d03df4");
		encryptionAlgContainerForRps.setEncryptionAlgorithmEnable(false);
		encryptionAlgContainerForRps.setEncryptionKeyVisable(false);
		encryptSettingsContainer.add(encryptionAlgContainerForRps);
		encryptSettingsContainer.add(encryptionAlgContainerForRps.getWarningContainer());
		encryptSettingsContainer.add(new Html("<HR>"));

		encryptionSettingsPanelForRps.add(encryptSettingsContainer);
		container.add(encryptionSettingsPanelForRps);
		encryptionSettingsPanelForRps.setVisible(false);
	}
	
	private void initDestinationContainer(LayoutContainer container) {
		DisclosurePanel destSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.destinationBackupDestination());
		
		destSettingsPanel.ensureDebugId("A94F7326-21ED-4403-BC64-9D9A5A6CF990");
		destSettingsPanel.setWidth("100%");
		destSettingsPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		destSettingsPanel.setOpen(true);
		LayoutContainer destSettingContainer = new LayoutContainer();
		renderDestPath();
		renderDestRPS();

		destChangedBackupTypeCont = getBackupDestChangedTypePanel();
		destChangedBackupTypeCont.disable();
		renderDestOption();
		//destSettingContainer.add(renderDestOption());
		destSettingContainer.add(destPathContainer);
		destSettingContainer.add(destRpsContainer);
		destSettingContainer.add(destChangedBackupTypeCont);
		destSettingContainer.add(new Html("<HR>"));
		destSettingsPanel.add(destSettingContainer);
		container.add(destSettingsPanel);
	}	
	private LayoutContainer renderDestOption() {
		RadioGroup destinationRadioGroup = new RadioGroup();
		initD2DDestinationRadio(destinationRadioGroup);
		initRpsDestinationRadio(destinationRadioGroup);
		
		LayoutContainer destRadioContainer = new LayoutContainer();
		destRadioContainer.setLayout(new TableLayout(2));
		destRadioContainer.add(d2dDestinationRadio);
		destRadioContainer.add(rpsDestinationRadio);
		destinationRadioGroup.setValue(d2dDestinationRadio);
		return destRadioContainer;
	}
	
	private LayoutContainer renderDestPath(){
		destPathContainer = new LayoutContainer();		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants
				.destinationLabelDestinationDescription());
		destPathContainer.add(label);

		initDestSelectPane();
		TableData data = new TableData();
		data.setStyle("padding-left:10px;");
		destPathContainer.add(pathSelection, data);

		return destPathContainer;
	}
	
	protected LayoutContainer renderDestRPS(){
		destRpsContainer = new BackupRPSDestSettingsPanel(this);
		destRpsContainer.setVisible(false);
		return destRpsContainer;
	}
	
	private void initD2DDestinationRadio(RadioGroup destinationRadioGroup) {
		d2dDestinationRadio = new Radio();
		d2dDestinationRadio.setBoxLabel(UIContext.Constants.UseLocalDiskOrFolder());
		d2dDestinationRadio.ensureDebugId("33228e3a-50c8-4d66-8a1b-91dff396a17b");
		d2dDestinationRadio.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				distinguishD2DRadioSelection();
			}
	
		});
		destinationRadioGroup.add(d2dDestinationRadio);
	}
	
	private void initRpsDestinationRadio(RadioGroup destinationRadioGroup) {
		rpsDestinationRadio = new Radio();
		rpsDestinationRadio.setBoxLabel(UIContext.Constants.UseRPS());
		rpsDestinationRadio.ensureDebugId("c717793d-812f-4d59-bdd6-c4a18a7e2c68");		
		rpsDestinationRadio.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
			
			}
		});
		destinationRadioGroup.add(rpsDestinationRadio);
	}
	
	private void distinguishD2DRadioSelection() {
		if(d2dDestinationRadio.getValue()){
			destRpsContainer.hide();
			destPathContainer.show();
			
			if(SettingPresenter.getInstance().isAdvSchedule()){
//				if(this.retentionForAdvancedFormatPanel != null){
//					retentionForAdvancedFormatPanel.setVisible(true);
//				}
			}else{
				if(retentionForStandarFormatPanel != null)
					retentionForStandarFormatPanel.setVisible(true);
			}
			
			if(sessionPasswordSettingsPanel != null)
				sessionPasswordSettingsPanel.setVisible(false);
			if(compressionOption != null){
				compressionOption.setEnabled(true);
			}
			if(encryptionSettingsPanel != null)
				encryptionSettingsPanel.setVisible(true);
			if(encryptionAlgContainerForRps != null){
				encryptionAlgContainerForRps.setVisible(false);
			}
			if(destChangedBackupTypeCont != null){
				destChangedBackupTypeCont.show();
				destChangedBackupTypeCont.setEnabled(false);
			}
			if (parentWindow.getAdScheduleSettings()!=null)
				parentWindow.getAdScheduleSettings().GetBackupSchedulePanel().SetScheduleDetailsMergeShow();
		}else { // for DataStore.
			destRpsContainer.show();
			destPathContainer.hide();
			
			//DataStore store the retention count in its policy, so we should not show retention setting here.
			hideRetentionSettingsForDataStore();
			
			if(sessionPasswordSettingsPanel != null)
				sessionPasswordSettingsPanel.setVisible(true);
			if(compressionOption != null){
				compressionOption.setEnabled(false);
			}
			if(encryptionSettingsPanel != null)
				encryptionSettingsPanel.setVisible(false);
			
			if(encryptionAlgContainerForRps != null){
				encryptionAlgContainerForRps.setVisible(true);
			}
			
			if(destChangedBackupTypeCont != null){
				destChangedBackupTypeCont.hide();
				destChangedBackupTypeCont.setEnabled(false);
			}
			if (parentWindow.getAdScheduleSettings()!=null)
				parentWindow.getAdScheduleSettings().GetBackupSchedulePanel().SetScheduleDetailsMergeNotShow();
		}
	}
	
	protected abstract void createPathSelectionPanel();
	
	private void initDestSelectPane() {
		createPathSelectionPanel();

		pathSelection.setMode(PathSelectionPanel.BACKUP_MODE);
		pathSelection.setPathFieldLength(440);
		pathSelection.addDebugId("A30EB6F5-A4C5-4ea6-8863-B14013BB7C40", 
				"EEB74D74-11E0-4cba-B19B-974301580B06", 
				"5CF9814C-DACA-4cd5-B475-3D5D305CBFEB");
	}

	protected boolean notForEdge() {
		return !this.parentWindow.isForEdge();
	}		

	protected LayoutContainer getBackupDestChangedTypePanel() {
		LayoutContainer tableContainer = new LayoutContainer();
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(4);
		tableLayout.setCellSpacing(0);
		tableLayout.setColumns(2);
		tableLayout.setWidth("100%");
		tableContainer.setLayout(tableLayout);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.destinationChangedSelectBackupType());
		TableData data = new TableData();
		data.setColspan(2);
		tableContainer.add(label, data);

		RadioGroup rg = new RadioGroup();
		destChangedFullBackup = new Radio();
		destChangedFullBackup.ensureDebugId("C3AB0628-D500-42ea-BD7E-8D12E71D1C94");
		destChangedFullBackup.setBoxLabel(UIContext.Constants.backupTypeFull());
		destChangedFullBackup.setValue(true);
		rg.add(destChangedFullBackup);
		tableContainer.add(destChangedFullBackup);

		destChangedIncrementalBackup = new Radio();
		destChangedIncrementalBackup.ensureDebugId("4604CCB7-A802-4f43-802B-EFDFF774A6F0");
		destChangedIncrementalBackup.setBoxLabel(UIContext.Constants.backupTypeIncremental());
		destChangedIncrementalBackup.setValue(false);
		rg.add(destChangedIncrementalBackup);
		tableContainer.add(destChangedIncrementalBackup);

		return tableContainer;
	}

	boolean isValidRemotePath(String path) {
		if (path != null) {
			path = path.trim();
			if (path.startsWith("\\\\") && path.length() >= 5
					&& path.indexOf("\\", 2) > 0
					&& path.indexOf("\\", 2) + 1 < path.length()) {
				return true;
			}
		}
		return false;
	}

	protected abstract void onBackupTypeChangedPathSame();
	
	protected void setDestChangedBackupType(String newDest) {
		if(!isPathChanged(newDest, oldDestinationPath)) {
			onBackupTypeChangedPathSame();
		}
		else if(!destChangedBackupTypeCont.isEnabled())
		{
			destChangedBackupTypeCont.enable();
			destChangedIncrementalBackup.setValue(false);
			destChangedFullBackup.setValue(true);
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
	
	public void setDestChangedBackupType() {
		if(!destChangedBackupTypeCont.isEnabled()){
			destChangedBackupTypeCont.enable();
			destChangedIncrementalBackup.setValue(false);
			destChangedFullBackup.setValue(true);
		}
	}

	public boolean isLocalDisk()
	{
		return pathSelection.isLocalPath();
	}

	public void RefreshData(BackupSettingsModel model, boolean isEdit) {
		if (model != null) {
			destChangedBackupTypeCont.disable();

			if(model.getChangedBackupDestType()!=null){
				if(model.getChangedBackupDestType()==BackupTypeModel.Full){
					destChangedFullBackup.setValue(true);
					destChangedIncrementalBackup.setValue(false);
				}
				else{
					destChangedFullBackup.setValue(false);
					destChangedIncrementalBackup.setValue(true);
				}
			}
			
			if (isEdit && (model.getBackupDataFormat() != null && model.getBackupDataFormat() == 0)) {				
				standardFormat.setValue(true);		
				throttleSettingsPanel.setVisible(true);
				retentionForStandarFormatPanel.setVisible(true);
//				retentionForAdvancedFormatPanel.setVisible(false);
			}else{
				advancedFormat.setValue(true);
				throttleSettingsPanel.setVisible(false);
				retentionForStandarFormatPanel.setVisible(false);
//				retentionForAdvancedFormatPanel.setVisible(true);
			}
			
			SettingPresenter.getInstance().setAdvSchedule(!(model.getBackupDataFormat() != null && model.getBackupDataFormat() == 0));
			
			if(model.isBackupToRps() != null && model.isBackupToRps()) {
				this.line.hide();
				this.destRpsContainer.refreshData(model.rpsDestSettings);
				compressionOption.disable();
				encryptionAlgContainerForRps.setEncryptAlogrithm(model.getEncryptionAlgorithm());
				sessionPasswordPanel.setPassword(model.getEncryptionKey()==null?"":model.getEncryptionKey());
				this.rpsDestinationRadio.setValue(true);
			}else {
				//wanqi06 added
				retentionRecoverySetPanel.RefreshData(model, isEdit);
		//		retentionRecoveryPoint4AdvancedFormat.refreshData(model);
				refreshEncryptContainer();
				oldDestinationPath = model.getDestination();
				if (oldDestinationPath != null && !PathSelectionPanel.isLocalPath(oldDestinationPath)) {
					pathSelection.setDestination(model.getDestination());
					pathSelection.setUsername(model.getDestUserName());
					pathSelection.setPassword(model.getDestPassword());
					pathSelection.cacheInfo();
					if ((model.getDestUserName() != null) &&
						(model.getDestUserName().length() > 0))
						pathSelection.setValidated();
					
				} else {
					pathSelection.setDestination(model.getDestination());
				}
				this.d2dDestinationRadio.setValue(true);
			}
			//
			if (model != null && model.getCompressionLevel() != null)
			{
				if (isStandardCompression(model)){
					compressionOption.setSimpleValue(UIContext.Constants.settingsCompreesionStandard());
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
					/*encryptionAlgContainer.setVisible(true)*/;
				}
				else if (isMaxCompression(model)){
					compressionOption.setSimpleValue(UIContext.Constants.settingsCompressionMax());
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionMaxTooltip());
					/*encryptionAlgContainer.setVisible(true)*/;
				}
				else{
					compressionOption.setSimpleValue(UIContext.Constants.settingsCompressionNone());
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionNoneTooltip());
					/*encryptionAlgContainer.setVisible(false)*/;
				}
			}
			else
			{
				compressionOption.setSimpleValue(UIContext.Constants.settingsCompreesionStandard());
				Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
			}

			if (model != null && model.getThrottling()!=null){
				throttleCheckBox.setValue(model.getThrottling() == 0L?false:true);
				if(model.getThrottling()>0) {
					this.throttleField.setValue(model.getThrottling().intValue());
				}
			}

			
		}
	}

	private void refreshEncryptContainer() {
		BackupSettingsModel model = SettingPresenter.model;
		if (model != null)
		{
			encryptionAlgContainer.setVisible(true);
			boolean enableEncryption = model.getEnableEncryption() == null ? false : model.getEnableEncryption();
			encryptionAlgContainer.setEncryptAlogrithm(model.getEncryptionAlgorithm());
			if(enableEncryption) {
				encryptionAlgContainer.setEncryptPassword(model.getEncryptionKey());
			}
		}
/*		else {
			encryptionAlgContainer.setVisible(false);
		}*/
		encryptionAlgContainer.addEncryptionAlgorithmAndKeyChangedHandler();
	}	

	private boolean isMaxCompression(BackupSettingsModel model) {
		if(model == null || model.getCompressionLevel() == null)
			return false;

		return model.getCompressionLevel() == 9;
	}

	private boolean isStandardCompression(BackupSettingsModel model) {
		if(model == null || model.getCompressionLevel() == null)
			return false;

		return model.getCompressionLevel() == 1;
	}

	private int getCompressionLevel(){
		int compressionLevel = -1;
		if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompressionNone())
			compressionLevel = 0;
		else if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompreesionStandard())
			compressionLevel = 1;
		else if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompressionMax())
			compressionLevel = 9;
		return compressionLevel;
	}

	

	protected boolean isEmpty(final String remoteDest) {
		return remoteDest == null || remoteDest.length() == 0;
	}

	protected boolean isLocalhost(String proxyName) {
		if ("localhost".equalsIgnoreCase(proxyName) || "127.0.0.1".equalsIgnoreCase(proxyName)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	protected int getEncryptAlgType(){
		Integer encryptAlgType ;
		if(rpsDestinationRadio.getValue()) {
			 encryptAlgType = encryptionAlgContainerForRps.getEncryptAlgorithm();
		}else{
			 encryptAlgType = encryptionAlgContainer.getEncryptAlgorithm();
		}
		return encryptAlgType == null? 0: encryptAlgType;
	}
	public void Save() {
		if(rpsDestinationRadio.getValue()) {
			SettingPresenter.model.setBackupToRps(true);
			this.destRpsContainer.saveData(SettingPresenter.model);
			Integer encryptAlgType = encryptionAlgContainerForRps.getEncryptAlgorithm();
			SettingPresenter.model.setEncryptionAlgorithm(encryptAlgType==null?0:encryptAlgType);
			Boolean enableEncryption = encryptAlgType != null && encryptAlgType > 0/* && compressionLevel > 0*/;
			SettingPresenter.model.setEnableEncryption(enableEncryption);
			SettingPresenter.model.setEncryptionKey(sessionPasswordPanel.getPassword()==null?"":sessionPasswordPanel.getPassword());
		}else {
			SettingPresenter.model.setBackupToRps(false);			
			
			if(SettingPresenter.getInstance().isAdvSchedule()){
				//SettingPresenter.model.retentionPolicy = this.retentionRecoveryPoint4AdvancedFormat.saveData();
			}else{
				SettingPresenter.model.retentionPolicy = retentionRecoverySetPanel.Save();
				if(SettingPresenter.model.retentionPolicy != null)
					SettingPresenter.model.setRetentionCount(
							SettingPresenter.model.retentionPolicy.getRetentionCount());
			}
			
			//
			String path = pathSelection.getDestination();
			SettingPresenter.model.setDestination(path);
			if(!PathSelectionPanel.isLocalPath(path)) {
				String destination=pathSelection.getDestination();
				String[] info= Utils.getConnectionInfo(destination);
				if(info!=null){
					SettingPresenter.model.setDestUserName(info[1]);
					SettingPresenter.model.setDestPassword(info[2]);
				}
				else{
					SettingPresenter.model.setDestUserName(pathSelection.getUsername());
					SettingPresenter.model.setDestPassword(pathSelection.getPassword());
				}	
			}
			else {
				SettingPresenter.model.setDestUserName(null);
				SettingPresenter.model.setDestPassword(null);
			}
			Integer encryptAlgType = encryptionAlgContainer.getEncryptAlgorithm();
			Boolean enableEncryption = encryptAlgType != null && encryptAlgType > 0/* && compressionLevel > 0*/;
			SettingPresenter.model.setEnableEncryption(enableEncryption);
			if(/*compressionLevel > 0 && */enableEncryption) {
				SettingPresenter.model.setEncryptionAlgorithm(encryptAlgType);
				SettingPresenter.model.setEncryptionKey(encryptionAlgContainer.getEncryptPassword());
			}
			else {
				SettingPresenter.model.setEncryptionAlgorithm(0);
				SettingPresenter.model.setEncryptionKey("");
			}
		}

		if(destChangedBackupTypeCont.isEnabled())
		{
			SettingPresenter.model.setChangedBackupDest(Boolean.TRUE);
			if(Boolean.TRUE.equals(destChangedFullBackup.getValue()))
				SettingPresenter.model.setChangedBackupDestType(BackupTypeModel.Full);
			else
				SettingPresenter.model.setChangedBackupDestType(BackupTypeModel.Incremental);
		}
		else
		{
			SettingPresenter.model.setChangedBackupDest(Boolean.FALSE);
			SettingPresenter.model.setChangedBackupDestType(BackupTypeModel.Full);
		}	

		int compressionLevel = this.getCompressionLevel();
		if(compressionLevel != -1){
			SettingPresenter.model.setCompressionLevel(compressionLevel);
			
		}			

		if (Boolean.TRUE.equals(destChangedFullBackup.getValue()))
			SettingPresenter.model.setChangedBackupDestType(BackupTypeModel.Full);
		else
			SettingPresenter.model
					.setChangedBackupDestType(BackupTypeModel.Incremental);
		if (this.throttleCheckBox.getValue()){
			SettingPresenter.model.setThrottling(this.throttleField.getValue().longValue());
		}else
			SettingPresenter.model.setThrottling(0L);
		
		if(this.standardFormat.getValue()){
			SettingPresenter.model.setBackupDataFormat(0);	
		}else{
			SettingPresenter.model.setBackupDataFormat(1);
		}
	}	

	public String GetBackupDestination()
	{
		return pathSelection.getDestination();
	}
	
	protected abstract String validateSource();

	public boolean Validate() {
		boolean isValid = true;
		String msgStr = null;

		msgStr = validateSource();
		if(!isEmpty(msgStr)){
			isValid = false;
		}
		
		if(rpsDestinationRadio.getValue()){
			if(isValid){
				isValid = this.destRpsContainer.validate(true);
				if(!isValid){
					msgStr = destRpsContainer.getValidationError();
				}
				
				if(isValid) {
					Integer encryptAlgType = encryptionAlgContainerForRps.getEncryptAlgorithm();
					if(encryptAlgType != null && encryptAlgType > 0){
						sessionPasswordPanel.setEnableEncryption(true);
						
					}else {
						sessionPasswordPanel.setEnableEncryption(false);
					}
					msgStr = sessionPasswordPanel.validate();
					if(!isEmpty(msgStr))
						isValid = false;
				}
			}
		}else {
			if(isValid){
				if (pathSelection.getDestination() == null
							|| pathSelection.getDestination().trim().length() == 0) {
					msgStr = UIContext.Constants.backupSettingsDestinationCannotBeBlank();
					isValid = false;
				}
			}
			
			if(isValid) {
				String path=pathSelection.getDestination();
				if(!PathSelectionPanel.isLocalPathValid(path)){
					msgStr = UIContext.Constants.backupDest_Error_InvalidUncPath();
					isValid = false;
				}	
			}

			if(isValid) {
				if(!encryptionAlgContainer.validate())
					return false;
			}
		}
		//wanqi06 add
		if(isValid) 
		{
			if(!SettingPresenter.getInstance().isAdvSchedule()){
				if(!retentionRecoverySetPanel.Validate())
					return false;
			}else{
//				if(!this.retentionRecoveryPoint4AdvancedFormat.validate())
//					return false;
			}
		}
		//
		if (isValid){
			if (this.throttleCheckBox.getValue()){
				isValid = this.throttleField.validate();
				if(!isValid) {
					msgStr = UIContext.Constants.destinationThrottleValueErrorTip();
				}
			}
		}

		if(!isValid && msgStr != null){
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(getErrorMessageTitle());
			msg.setMessage(msgStr);
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
			return false;
		}
		return true;
	}
	
	protected abstract String getErrorMessageTitle();

	public PathSelectionPanel getPathSelectionPanel() {
		return pathSelection;
	}	
	
	public void validateRemotePath(final AsyncCallback<Boolean> callback) {
		if (pathSelection.isLocalPath()) {
			callback.onSuccess(true);
			return;
		}

		if (!pathSelection.needValidate()) {
			callback.onSuccess(true);
			return;
		}

		final UserPasswordWindow dlg = new UserPasswordWindow(pathSelection.getDestination(), "", "");
		dlg.setModal(true);

		dlg.addWindowListener(new WindowListener() {

			@Override
			public void windowHide(WindowEvent we) {
				if (dlg.getCancelled()) {
					callback.onSuccess(false);
					return;
				}

				pathSelection.setUsername(dlg.getUsername());
				pathSelection.setPassword(dlg.getPassword());
				pathSelection.setValidated();
				SettingPresenter.model.setDestUserName(dlg.getUsername());
				SettingPresenter.model.setDestPassword(dlg.getPassword());

				callback.onSuccess(true);
			}

		});

		dlg.show();
	}

	public boolean checkShareFolder()
	{
		if(rpsDestinationRadio != null && this.rpsDestinationRadio.getValue())
			return true;
		try
		{
			if (pathSelection.isLocalPath())
				return true;
			
			String title = "";
			if(parentWindow.isShowForVSphere()){
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
			}else{
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
			}

			String destPath = pathSelection.getDestination();
			UncPath uncPath = new UncPath();
			uncPath.setUncPath( destPath );
			if (uncPath.getShareFolder().length() == 0)
			{
				MessageBox messageBox = new MessageBox();
				messageBox.setTitleHtml( title );
				messageBox.setMessage( UIContext.Constants.backupDest_Error_NoShareFolder() );
				messageBox.setIcon( MessageBox.ERROR );
				messageBox.setButtons( MessageBox.OK );
				Utils.setMessageBoxDebugId(messageBox);
				messageBox.show();				
				return false;
			}
			
			if (uncPath.getComputerName().equalsIgnoreCase( "localhost" ) ||
				uncPath.getComputerName().equalsIgnoreCase( "127.0.0.1" ))
			{
				MessageBox messageBox = new MessageBox();
				messageBox.setTitleHtml( title );
				messageBox.setMessage( UIContext.Constants.backupDest_Error_NotAllowLocalHost() );
				messageBox.setIcon( MessageBox.ERROR );
				messageBox.setButtons( MessageBox.OK );
				Utils.setMessageBoxDebugId(messageBox);
				messageBox.show();
				return false;
			}

			return true;
		}
		catch (UncPath.InvalidUncPathException e)
		{
			MessageBox messageBox = new MessageBox();
			String title = "";
			if(parentWindow.isShowForVSphere()){
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
			}else{
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
			}
			messageBox.setTitleHtml( title );
			messageBox.setMessage( UIContext.Constants.backupDest_Error_InvalidUncPath() );
			messageBox.setIcon( MessageBox.ERROR );
			messageBox.setButtons( MessageBox.OK );
			Utils.setMessageBoxDebugId(messageBox);
			messageBox.show();
			return false;
		}
	}

	public void setEditable(boolean isEditable) {
		pathSelection.setEnabled(isEditable);	
		retentionRecoverySetPanel.setEnabled(isEditable);
//		this.retentionRecoveryPoint4AdvancedFormat.setEditable(isEditable);
		if(rpsDestinationRadio.getValue())
			compressionOption.setEnabled(false);
		else
			compressionOption.setEnabled(isEditable);
		encryptionAlgContainer.setEnabled(isEditable);
		throttleCheckBox.setEnabled(isEditable);
		throttleField.setEnabled(isEditable);
		destRpsContainer.setEditable(isEditable);
		rpsDestinationRadio.setEnabled(isEditable);
		d2dDestinationRadio.setEnabled(isEditable);
		if(this.sessionPasswordPanel != null){
			sessionPasswordPanel.setEnablePassword(isEditable);
		}
		destChangedBackupTypeCont.setEnabled(isEditable);
		backupDataFormatPanelContainer.setEnabled(isEditable);
	}
	
	public void setContentHost(ISettingsContentHost host) {
		contentHost = host;
	}
	
	/*public LayoutContainer getDestChangePanel() {
		return destChangedBackupTypeCont;
	}*/
	
	public String getBackupDestination() {
		if (d2dDestinationRadio.getValue()) {
			return pathSelection.getDestination();
		} else {
			return this.destRpsContainer.getBackupDestination();
		}
	}
	
	public String getOldDestination() {
		return oldDestinationPath;
	}
	
	public void updateCompression4Rps(String compression){
		compressionOption.setSimpleValue(compression);
		compressionOption.disable();
	}
	
	public void updateEncryption4Rps(int alg){
		encryptionAlgContainerForRps.setEncryptAlogrithm((encryptionAlgContainerForRps.getLibType() << 16)|alg);
	}
	
	public boolean backupToRPS(){
		return rpsDestinationRadio.getValue();
	}
	
	public void clearSessionPassword() {
		if(this.sessionPasswordPanel != null)
			this.sessionPasswordPanel.clear();
	}
	
	public int getRetentionCount(){
		if(SettingPresenter.getInstance().isAdvSchedule()){
			//return this.retentionRecoveryPoint4AdvancedFormat.getRetentionCount();
		}
		return this.retentionRecoverySetPanel.getRetentionCount();
	}
	
	protected List<NumberField> getRetentionCountField(){	
		 List<NumberField> nf = new  ArrayList<NumberField>();
		 nf.add(retentionRecoverySetPanel.getRetentionCountField());
//		 nf.add(retentionRecoveryPoint4AdvancedFormat.getRetentionCountField());		 
		 return nf;		
	}
}
