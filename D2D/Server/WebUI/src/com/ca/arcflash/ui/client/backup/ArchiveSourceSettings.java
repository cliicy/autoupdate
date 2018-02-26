package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Iterator;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveConstantsModel;
import com.ca.arcflash.ui.client.model.ArchiveSettingsModel;
import com.ca.arcflash.ui.client.model.ArchiveSourceFilterModel;
import com.ca.arcflash.ui.client.model.ArchiveSourceInfoModel;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.VolumeModel;
import com.ca.arcflash.ui.client.model.VolumeSubStatus;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.IntegerField;

public class ArchiveSourceSettings{
	
	private LayoutContainer ArchiveContainer;

	//Source Settings
	 Grid<ArchiveSourceInfoModel> ArchiveSourcesGrid;
	 ListStore<ArchiveSourceInfoModel> gridStore;
	 ColumnModel ArchiveSourceColumnsModel;
	//madra04
	private FieldSet notificationSet;
	private FieldSet displaySourceNotificationSet;
	
	//without duplicates
	private ArrayList<String> notificationMessages = new ArrayList<String>();
	
	//with duplicates
	private ArrayList<String> allNotificationMessages = new ArrayList<String>();
	
	private LoadingStatus warnLoadingStatus;
	
	private Button btSourceAdd;
	private Button btSourceFiltersConfigure;
	private Button btSourceRemove;
	
	ToolTipConfig tipConfig = null;
	private Listener<BaseEvent> archiveSettingsListener;

	boolean bExistingSourceSelected;
	
	private List<FileModel> volumes;
	
	int iSelectedPolicyType = 1;
	//comment out to use them from backup settings, then the two panels will be consistent.
	//zhawe03
//	private List<FileModel> NonSelectableVolumes = null;
	private BackupVolumeModel backupSelectedVolumesModel;
	private BackupVolumeModel backupSelectedRefsVolumesModel;
	private BackupVolumeModel backupSelectedDedupeVolumesModel;
	
	
	//Global options
	//CheckBox cbArchiveExcludeSystemFiles;
	//CheckBox cbArchiveExcludeApplicationFiles;
	/*public BackupVolumeModel backupVolumes = null;
	public String backupDestination;*/
	//public List<FileModel> backupVolumes = null;
	private ArchiveSettingsContent parentWind = null;
	public CheckBox cbArchiveAfterBackup;
	private Listener<BaseEvent> archiveScheduleSettingsListener;
    private boolean isSystemOrBootVolumeFlag;
     
     public Radio backupFreqRadio;
     public Radio backupSchedRadio;
     public IntegerField backupFreqInputBox;
     public CheckBox dailyBackupCB;
     public CheckBox weeklyBackupCB;
     public CheckBox monthlyBackupCB;
     public LayoutContainer recoveryPointFieldContainer;
//     private LayoutContainer backupScheduleContentContainer;
     
	public ArchiveSourceSettings(ArchiveSettingsContent in_archiveSettingsWindow)
	{
		parentWind = in_archiveSettingsWindow;
		iSelectedPolicyType = parentWind.isArchiveTask() ? 2 : 1;
		BackupContext.setArchiveSourceSettings(this);
	}
	
	final LoginServiceAsync loginService = GWT.create(LoginService.class);
	
	private ArchiveSourceInfoModel archiveSourceModel;	
	
	String oldDispalySourcePath ;
	String oldActualSourcePath ;
	
	public LayoutContainer Render()
	{
		ArchiveContainer = new LayoutContainer();
		TableLayout tlArchivePageLayout = new TableLayout();
		tlArchivePageLayout.setWidth("87%");
		tlArchivePageLayout.setHeight("45%");
		ArchiveContainer.setLayout(tlArchivePageLayout);
		
		defineArchiveSettingsListener();
		
		defineArchiveSourceSettings();	
		
		//defineArchiveGlobalOptions();
		
		return ArchiveContainer;
	}
	
	private void addWaringIcon() {
		Image warningImage = getWaringIcon();
//		Image warningImage = UIContext.IconBundle.status_small_warning().createImage();
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet.add(warningImage, tableData);
	}
	
	private void addDisplaySourceWaringIcon() {
		Image warningImage = getInformationIcon();
//		Image warningImage = UIContext.IconBundle.status_small_warning().createImage();
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		displaySourceNotificationSet.add(warningImage, tableData);
	}

	private Image getWaringIcon() {
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
		return warningImage;
	}
	
	private Image getInformationIcon() {
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logMsg()).createImage();
		return warningImage;
	}

	public void setEditable(boolean editable) {
		cbArchiveAfterBackup.setEnabled(editable);
		if(!parentWind.isArchiveTask())
			recoveryPointFieldContainer.setEnabled(editable);
		ArchiveSourcesGrid.setEnabled(editable);
		btSourceAdd.setEnabled(editable);
		btSourceFiltersConfigure.setEnabled(editable);
		btSourceRemove.setEnabled(editable);
	}
	
	private void defineArchiveSourceSettings()
	{
	
		LayoutContainer lcArchiveSourceContainer = new LayoutContainer();
		TableLayout tlArchiveSourcesLayout = new TableLayout(2);
	//	tlArchiveSourcesLayout.setWidth("96%");
	//	tlArchiveSourcesLayout.setColumns(2);
		tlArchiveSourcesLayout.setCellSpacing(4);
		lcArchiveSourceContainer.setStyleName("ArchiveBrowsePanelStyles");
		lcArchiveSourceContainer.setLayout(tlArchiveSourcesLayout);
		//madra04
		LayoutContainer sourceVolumesContainer = new LayoutContainer();
		
//		DisclosurePanel disPanelForCheckBox = Utils.getDisclosurePanel(UIContext.Constants.ArchiveSourceSelectionLabel());		
		
		cbArchiveAfterBackup = new CheckBox();
		cbArchiveAfterBackup.ensureDebugId("B02C3D3F-2AC2-4554-AD5D-34075120A5C9");
		//cbArchiveAfterBackup.setStyleName("x-form-field");
		String archiveEnableLable = parentWind.isArchiveTask() ? UIContext.Messages.fileArchiveEnableLable() : UIContext.Messages.ArchiveEnableLable();
		cbArchiveAfterBackup.setBoxLabel(archiveEnableLable);
		cbArchiveAfterBackup.addListener(Events.Change,archiveScheduleSettingsListener);
		
		sourceVolumesContainer.add(cbArchiveAfterBackup);
		
		notificationSet = new FieldSet();
		notificationSet.ensureDebugId("CAC9072E-1D1F-4c99-89CB-76BEB1E69AF2");
		notificationSet.setHeadingHtml(UIContext.Messages.ArchiveSettingsNodifications());
		notificationSet.setCollapsible(true);
		TableLayout warningLayout = new TableLayout();
		warningLayout.setWidth("100%");
		warningLayout.setCellSpacing(1);
		warningLayout.setColumns(2);
		notificationSet.setLayout(warningLayout);
		addWaringIcon();
		String archiveFileSourceNotification = parentWind.isArchiveTask() ? UIContext.Messages.fileArchiveFileSourceNotification() : UIContext.Messages.ArchiveFileSourceNotification();
		notificationSet.add(new LabelField(archiveFileSourceNotification));	
		sourceVolumesContainer.add(notificationSet);		
		//disPanelForCheckBox.add(sourceVolumesContainer);
		
		
		displaySourceNotificationSet = new FieldSet();
		displaySourceNotificationSet.setHeadingHtml(UIContext.Messages.ArchiveSettingsNodifications());
		displaySourceNotificationSet.setCollapsible(true);
		TableLayout displayWarningLayout = new TableLayout();
		displayWarningLayout.setWidth("100%");
		displayWarningLayout.setCellSpacing(1);
		displayWarningLayout.setColumns(2);
		displaySourceNotificationSet.setLayout(displayWarningLayout);		
		//displaySourceNotificationSet.add(new LabelField(UIContext.Messages.ActualFileCopySourcePathNotification()));	
		
		
		displaySourceNotificationSet.setVisible(false);
		
		ArchiveContainer.add(sourceVolumesContainer);
		/*Label lblArchiveSource = new Label(UIContext.Constants.ArchiveSourceSelectionLabel());
		lblArchiveSource.setStyleName("restoreWizardSubItem");*/
				
	//	TableData tdArchiveSource = new TableData();
	//	tdArchiveSource.setWidth("100%");
	//	tdArchiveSource.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		
		String archiveSourceSelectionLabel = parentWind.isArchiveTask() ? UIContext.Constants.fileArchiveSourceSelectionLabel() : UIContext.Constants.ArchiveSourceSelectionLabel();
		DisclosurePanel disPanel = Utils.getDisclosurePanel(archiveSourceSelectionLabel);
		
		LayoutContainer settingsContainer = new LayoutContainer();
		
//		ArchiveContainer.add(lblArchiveSource);
		String archiveSourceSelectionDescription = parentWind.isArchiveTask() ? UIContext.Constants.fileArchiveSourceSelectionDescription():UIContext.Constants.ArchiveSourceSelectionDescription();
		LabelField lblArchiveDestinationSummary = new LabelField(archiveSourceSelectionDescription);
		settingsContainer.add(lblArchiveDestinationSummary);
		
		
		gridStore = new ListStore<ArchiveSourceInfoModel>();
		GridCellRenderer<ArchiveSourceInfoModel> SourceRenderer = new GridCellRenderer<ArchiveSourceInfoModel>() {

			@Override
			public Object render(ArchiveSourceInfoModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveSourceInfoModel> store,
					Grid<ArchiveSourceInfoModel> grid) {
				LabelField lblArchiveSource = new LabelField();
				lblArchiveSource.setValue("<pre style=\"font-family: Tahoma,Arial;font-size: 11px;\">"+model.getSourcePath()+"</pre>");
				//LabelField lblArchiveSource = new LabelField(model.getSourcePath());
				Utils.addToolTip(lblArchiveSource, model.getSourcePath());
				return lblArchiveSource;
			}
		};
		
		GridCellRenderer<ArchiveSourceInfoModel> PolicyRenderer = new GridCellRenderer<ArchiveSourceInfoModel>() {

			@Override
			public Object render(ArchiveSourceInfoModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveSourceInfoModel> store,
					Grid<ArchiveSourceInfoModel> grid) {
				String strPoliciesSelected = GetSelectedPolicies(model);
				if(strPoliciesSelected != null){
					
//					LabelField label = new LabelField(new Html("<pre style=\"font-family: Tahoma,Arial;font-size: 11px;\">"+strPoliciesSelected+"</pre>").getHtml());
					LabelField label = new LabelField(strPoliciesSelected);
					tipConfig = new ToolTipConfig();			    
					tipConfig.setTemplate(Utils.getTooltipWrapTemplate(strPoliciesSelected));
					label.setToolTip(tipConfig);
					
					return label;
				}
				return "";
			}
		};
		
		/*GridCellRenderer<ArchiveSourceInfoModel> PolicyType = new GridCellRenderer<ArchiveSourceInfoModel>() {

			@Override
			public Object render(ArchiveSourceInfoModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveSourceInfoModel> store,
					Grid<ArchiveSourceInfoModel> grid) {
				//return model.getArchiveFiles() ? UIContext.Constants.ArchiveFilesLabelDelete() : UIContext.Constants.ArchiveFileCopyLabel();
				String strSourceType = model.getArchiveFiles() ? UIContext.Constants.ArchiveFilesLabelDelete() : UIContext.Constants.ArchiveFileCopyLabel();
//				LabelField label = new LabelField(new Html("<pre style=\"font-family: Tahoma,Arial;font-size: 11px;\">"+strSourceType+"</pre>").getHtml());
				LabelField label = new LabelField(strSourceType);
				return label;
			}
		};*/
		
		List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		columnConfigs.add(Utils.createColumnConfig("Source", UIContext.Constants.path(), 140,SourceRenderer));
		columnConfigs.add(Utils.createColumnConfig("Policy", UIContext.Constants.filters(), 300,PolicyRenderer));
//		columnConfigs.add(Utils.createColumnConfig("Type", UIContext.Constants.ArchivePolicyType(), 120,PolicyType));
		
		ArchiveSourceColumnsModel = new ColumnModel(columnConfigs);
				
		ArchiveSourcesGrid = new Grid<ArchiveSourceInfoModel>(gridStore, ArchiveSourceColumnsModel);
		
		ArchiveSourceColumnsModel.addListener(Events.WidthChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				ArchiveSourcesGrid.reconfigure(gridStore, ArchiveSourceColumnsModel);
			}
	    	
	    });
				
		
		ArchiveSourcesGrid.setLoadMask(true);
		ArchiveSourcesGrid.mask(UIContext.Constants.loadingIndicatorText());
		ArchiveSourcesGrid.setHeight(200);
		ArchiveSourcesGrid.unmask();

		ArchiveSourcesGrid.setTrackMouseOver(true);
		ArchiveSourcesGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		ArchiveSourcesGrid.addListener(Events.RowClick, archiveSettingsListener);
		ArchiveSourcesGrid.setBorders(true);
		ArchiveSourcesGrid.setWidth(450);
		ArchiveSourcesGrid.setAutoExpandMax(3000);
		ArchiveSourcesGrid.getView().setForceFit(false);
		
		
		 LayoutContainer panel = new LayoutContainer();	  
	     panel.setLayout(new FitLayout());
		 panel.setWidth(450);
	     panel.setHeight(200);
		 //panel.setScrollMode(Scroll.AUTOX);
		 panel.add(ArchiveSourcesGrid);
		
		lcArchiveSourceContainer.add(panel);
		//lcArchiveSourceContainer.setScrollMode(Scroll.ALWAYS);
		
		TableData tdFiltersGridButtonsPanel = new TableData();
		tdFiltersGridButtonsPanel.setWidth("15%");
		tdFiltersGridButtonsPanel.setHorizontalAlign(HorizontalAlignment.RIGHT);
		lcArchiveSourceContainer.add(DefineArciveSourceConfigureButtonsPanel(),tdFiltersGridButtonsPanel);
		
		settingsContainer.add(lcArchiveSourceContainer);
		disPanel.add(settingsContainer);
		if(! parentWind.isArchiveTask())
			ArchiveContainer.add(getRecoveryPointsToCopyField());
		ArchiveContainer.add(disPanel);
		ArchiveContainer.add(displaySourceNotificationSet);
		//ArchiveContainer.add(new Html("<HR>"));		
	}
	
	private DisclosurePanel getRecoveryPointsToCopyField(){
		
		recoveryPointFieldContainer = new LayoutContainer();
		recoveryPointFieldContainer.setEnabled(false);
		
		backupFreqRadio = new Radio();
		backupFreqRadio.ensureDebugId("b0cfd186-9ce4-4915-87e0-9f841f0287f3");
		backupFreqRadio.setBoxLabel(UIContext.Constants.scheduleCopyFilesFromTheFirstOfEvery());

		backupFreqInputBox = new IntegerField();
		backupFreqInputBox.ensureDebugId("8f75e39c-84b5-4dbb-a8fd-71a9eb88d377");
		backupFreqInputBox.setWidth(50);
		backupFreqInputBox.setHeight(25);
		backupFreqInputBox.setEnabled(false);

		FieldLabel backupsLabel = new FieldLabel();
		backupsLabel.setLabelSeparator("");

		backupsLabel.setText(UIContext.Constants.backupsLabelWithBraces());
		backupsLabel.ensureDebugId("07410118-609d-4e0e-94d7-229d92b59fbe");

		HBoxLayoutContainer backupFreqContainer = new HBoxLayoutContainer();
		backupFreqContainer.ensureDebugId("43bbd46f-b0f6-45e6-a5aa-feb6825248bf");
		backupFreqContainer.setPadding(new Padding(0,0,5,0));
		backupFreqContainer.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);

		backupFreqContainer.add(backupFreqRadio);
		backupFreqContainer.add(backupFreqInputBox/*,new BoxLayoutData(new Margins(4, 4, 0, 0))*/);
		//backupsLabel.setWidth("100px");
		backupsLabel.setHeight(30);
		backupFreqContainer.add(backupsLabel, new BoxLayoutData(new Margins(0, 0, 0, 10)));

		backupSchedRadio = new Radio();
		backupSchedRadio.ensureDebugId("3a332667-37d4-4b81-9934-7aa3a4fb21b0");
		backupSchedRadio.setFireChangeEventOnSetValue(true);
		backupSchedRadio.setValue(true);
		backupSchedRadio.setBoxLabel(UIContext.Constants.scheduleCopyFilesFromSelectedBackupTypes());

		final LayoutContainer backupTypeContainer = new LayoutContainer();
		backupTypeContainer.ensureDebugId("91d6bbd7-b97f-4da0-9f0b-088258bdeb59");
		
		dailyBackupCB = new CheckBox();
		dailyBackupCB.ensureDebugId("20c9ec8a-1621-4f9b-8ac1-1849d4fb0737");
		dailyBackupCB.setBoxLabel(UIContext.Constants.fileCopydailyBackups());
		backupTypeContainer.add(dailyBackupCB);

		weeklyBackupCB = new CheckBox();
		weeklyBackupCB.ensureDebugId("65a13710-a492-465d-a3ba-2830f9cbb02b");
		weeklyBackupCB.setBoxLabel(UIContext.Constants.fileCopyweeklyBackups());
		backupTypeContainer.add(weeklyBackupCB);

		monthlyBackupCB = new CheckBox();
		monthlyBackupCB.ensureDebugId("650ab878-bc21-487f-8178-fbc107e17f38");
		monthlyBackupCB.setBoxLabel(UIContext.Constants.fileCopymonthlyBackups());
		backupTypeContainer.add(monthlyBackupCB);

//		backupTypeContainer.setVisible(false);
		/*FieldLabel backupsLabel = new FieldLabel();
		backupsLabel.setLabelSeparator("");
		backupsLabel.setText(UIContext.Constants.backupsLabel());
		backupsLabel.ensureDebugId("62483b9f-b06a-417e-b56c-161baccc78d7");*/

		recoveryPointFieldContainer.add(backupSchedRadio);

		recoveryPointFieldContainer.add(backupTypeContainer, new MarginData(new com.extjs.gxt.ui.client.util.Margins(0, 0, 0, 10)));
		recoveryPointFieldContainer.add(backupFreqContainer);

		
		RadioGroup rg = new RadioGroup();
		rg.add(backupFreqRadio);
		rg.add(backupSchedRadio);
		
		rg.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if(backupFreqRadio.getValue()){
					backupFreqInputBox.setEnabled(true);
					if(backupFreqInputBox.getValue() == null)
						backupFreqInputBox.setValue(5);
					dailyBackupCB.setValue(false);
					weeklyBackupCB.setValue(false);
					monthlyBackupCB.setValue(false);
					backupTypeContainer.setEnabled(false);
//					backupScheduleContentContainer.setEnabled(false);
				}
				else{
					backupFreqInputBox.setEnabled(false);
					backupFreqInputBox.clear();
					backupTypeContainer.setEnabled(true);
//					backupScheduleContentContainer.setEnabled(true);
					
					/*List<ScheduleItemModel> schedModels = parentWind.backupContent.getAdvScheduleItem().getScheduleSettings().getScheduleModels();
					for (ScheduleItemModel scheduleItemModel : schedModels) {
						if(scheduleItemModel.getScheduleType().equals(ScheduleTypeModel.OnceDailyBackup))
							dailyBackupCB.setEnabled(true);
						else if(scheduleItemModel.getScheduleType().equals(ScheduleTypeModel.OnceWeeklyBackup))
							weeklyBackupCB.setEnabled(true);
						else if(scheduleItemModel.getScheduleType().equals(ScheduleTypeModel.OnceMonthlyBackup))
							monthlyBackupCB.setEnabled(true);
					}*/
				}
				
			}
		});
		
		
		DisclosurePanel dp = Utils.getDisclosurePanel(UIContext.Constants.recoveryPointsToCopyHeader());
		dp.add(recoveryPointFieldContainer);
		return dp;
	}
	
	/*private void defineArchiveGlobalOptions() {
		LayoutContainer lcArchiveGlobalOptions = new LayoutContainer();
		TableLayout tlArchiveSourcesLayout = new TableLayout();
		tlArchiveSourcesLayout.setWidth("96%");
		tlArchiveSourcesLayout.setColumns(1);
		tlArchiveSourcesLayout.setCellSpacing(4);
		lcArchiveGlobalOptions.setStyleName("ArchiveBrowsePanelStyles");
		lcArchiveGlobalOptions.setLayout(tlArchiveSourcesLayout);
		
		Label lblGlobalOptions = new Label(UIContext.Constants.GlobalFiltersTitle());
		lblGlobalOptions.setStyleName("restoreWizardSubItem");
		TableData tdGlobalOptions = new TableData();
		tdGlobalOptions.setWidth("100%");
		tdGlobalOptions.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcArchiveGlobalOptions.add(lblGlobalOptions,tdGlobalOptions);
		LabelField lblGlobalOptionsSummary = new LabelField(UIContext.Constants.GlobalOptionsDescription());
		lcArchiveGlobalOptions.add(lblGlobalOptionsSummary,tdGlobalOptions);
		
		cbArchiveExcludeSystemFiles = new CheckBox();
		cbArchiveExcludeSystemFiles.setStyleName("x-form-field");
		cbArchiveExcludeSystemFiles.setBoxLabel(UIContext.Constants.ExcludesystemfilesTitle());
		cbArchiveExcludeSystemFiles.setEnabled(true);
		cbArchiveExcludeSystemFiles.setToolTip(UIContext.Constants.ExcludeSystemFilesTooltip());
		cbArchiveExcludeSystemFiles.addListener(Events.Change,archiveSettingsListener);
		TableData tdArchiveGlobaloption = new TableData();
		tdArchiveGlobaloption.setWidth("100%");
		tdArchiveGlobaloption.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcArchiveGlobalOptions.add(cbArchiveExcludeSystemFiles,tdArchiveGlobaloption);
		
		cbArchiveExcludeApplicationFiles = new CheckBox();
		cbArchiveExcludeApplicationFiles.setStyleName("x-form-field");
		cbArchiveExcludeApplicationFiles.setBoxLabel(UIContext.Constants.ExcludeapplicationfilesTitle());
		cbArchiveExcludeApplicationFiles.setToolTip(UIContext.Constants.ExcludeAppFilesTooltip());
		cbArchiveExcludeApplicationFiles.setEnabled(true);
		cbArchiveExcludeApplicationFiles.addListener(Events.Change,archiveSettingsListener);
		lcArchiveGlobalOptions.add(cbArchiveExcludeApplicationFiles,tdArchiveGlobaloption);
		
		ArchiveContainer.add(lcArchiveGlobalOptions);
		ArchiveContainer.add(new Html("<HR>"));	
	}*/
	
	public static String GetSelectedPolicies(ArchiveSourceInfoModel model) {
		String strPolicies = "";
		
		ArchiveSourceFilterModel[] archiveFilters = model.getArchiveSourceFilters();
		if(archiveFilters != null)
		{
			for(int iArchiveFilterIndex = 0;iArchiveFilterIndex < archiveFilters.length;iArchiveFilterIndex++)
			{
				ArchiveSourceFilterModel archiveFilterOrCriteria = archiveFilters[iArchiveFilterIndex];
				if(archiveFilterOrCriteria.getIsCriteria() == false)
				{
					strPolicies += GetLocalizedFilterType(archiveFilterOrCriteria.getFilterOrCriteriaType()) 
								+ " " + GetCriteriaName(archiveFilterOrCriteria.getFilterOrCriteriaName())
								+ " " + archiveFilterOrCriteria.getLocFilterOrCriteriaLowerValue() + ";\n";
					
				}
				else
				{
					strPolicies += GetLocalizedFilterType(archiveFilterOrCriteria.getFilterOrCriteriaType()) 
								+ " " 
								+ GetCriteriaName(archiveFilterOrCriteria.getFilterOrCriteriaName()) + " ";
					
					if(archiveFilterOrCriteria.getFilterOrCriteriaName().compareToIgnoreCase("FileSize") == 0)
					{
						strPolicies += GetLocalizedFileSizeOperatorByIndex(archiveFilterOrCriteria.getCriteriaOperator()) + " ";
					}
									
					String strCriteriaValues = "";
					strCriteriaValues += GetCriteriaValue(archiveFilterOrCriteria.getFilterOrCriteriaLowerValue()) 
									  + " " 
									  + GetCriteraiValueType(archiveFilterOrCriteria.getFilterOrCriteriaLowerValue(),
											  				archiveFilterOrCriteria.getFilterOrCriteriaName());
					
					if(archiveFilterOrCriteria.getCriteriaOperator().compareToIgnoreCase(ArchiveConstantsModel.OPERATOR_BETWEEN_STRING) == 0)
					{
						strCriteriaValues += " and ";
						strCriteriaValues += GetCriteriaValue(archiveFilterOrCriteria.getFilterOrCriteriaHigherValue())
										  + " " + GetCriteraiValueType(archiveFilterOrCriteria.getFilterOrCriteriaHigherValue(),
												  archiveFilterOrCriteria.getFilterOrCriteriaName());
					}
					strPolicies += strCriteriaValues + ";\n";
				}
			}
		}
		else 
		{
			strPolicies = UIContext.Constants.Nopoliciesbeenconfigured();
		}		
		return strPolicies;
	}

	private static String GetLocalizedFilterType(String filterOrCriteriaType) {
		switch(Integer.parseInt(filterOrCriteriaType))
		{
		case ArchiveConstantsModel.FILTER_TYPE_INCLUDE:
			return UIContext.Constants.ArchiveFilterInclude();
		case ArchiveConstantsModel.FILTER_TYPE_EXCLUDE:
			return UIContext.Constants.ArchiveFilterExclude();
		default:
			return UIContext.Constants.ArchiveFilterInclude();
		}
	}

	private static String GetCriteriaName(String filterOrCriteriaName) {
		
		if(filterOrCriteriaName.compareToIgnoreCase(ArchiveConstantsModel.FILTER_PATTERN_FILE_STRING) == 0)
		{
			return UIContext.Constants.ArchiveFilePattern();
		}
		else if(filterOrCriteriaName.compareToIgnoreCase(ArchiveConstantsModel.FILTER_PATTERN_FOLDER_STRING) == 0)
		{
			return UIContext.Constants.ArchiveFolderPattern();
		}
		else if(filterOrCriteriaName.compareToIgnoreCase("FileSize") == 0)
		{
			return UIContext.Constants.FilesSizeCriteriaName();
		}
		else if(filterOrCriteriaName.compareToIgnoreCase("AccessTime") == 0)
		{
			return UIContext.Constants.FilesNotAccessedCriteriaName();
		}
		else if(filterOrCriteriaName.compareToIgnoreCase("ModifiedTime") == 0)
		{
			return UIContext.Constants.FilesNotModifiedCriteriaName();
		}
		else if(filterOrCriteriaName.compareToIgnoreCase("CreatedTime") == 0)
		{
			return UIContext.Constants.FilesNotCreatedCriteriaName();
		}
		return filterOrCriteriaName;
	}
	
	private static String GetLocalizedFileSizeOperatorByIndex(String iFileSizeIndex) {
		
		switch(Integer.parseInt(iFileSizeIndex))
		{
		case ArchiveConstantsModel.OPERATOR_LESSTHAN:
			return UIContext.Constants.ArchiveFileSizeOperatorlessthan();
		case ArchiveConstantsModel.OPERATOR_GREATERTHAN:
			return UIContext.Constants.ArchiveFileSizeOperatorgreaterthan();
		case ArchiveConstantsModel.OPERATOR_BETWEEN:
			return UIContext.Constants.ArchiveFileSizeOperatorbetween();
		}
		return UIContext.Constants.ArchiveFileSizeOperatorlessthan();
	}

	private static String GetCriteriaValue(String filterOrCriteriaLowerValue) 
	{
		if(filterOrCriteriaLowerValue == null)
		{
			return "";
		}
		String[] StringKeys = filterOrCriteriaLowerValue.split("\\\\");
		
		for(int iIndex = 0;iIndex < StringKeys.length ; iIndex++)
		{
			if(Integer.parseInt(StringKeys[iIndex]) != 0)
			{
				return StringKeys[iIndex];
			}
		}
		return filterOrCriteriaLowerValue;
	}

	private LayoutContainer DefineArciveSourceConfigureButtonsPanel() {
		
		LayoutContainer lcButtonPanel = new LayoutContainer();
		TableLayout tlButtonPanelLayout = new TableLayout();
		tlButtonPanelLayout.setWidth("100%");
		tlButtonPanelLayout.setColumns(1);
		tlButtonPanelLayout.setCellSpacing(10);
		lcButtonPanel.setLayout(tlButtonPanelLayout);

		TableData tdSourceAdd = new TableData();
		//tdSourceAdd.setHorizontalAlign(HorizontalAlignment.RIGHT);
		tdSourceAdd.setVerticalAlign(VerticalAlignment.TOP);
		
		btSourceAdd = new Button(UIContext.Constants.ArchiveAddSource())
		{
			@Override
			protected void onDisable() {
				addStyleName("item-disabled");
				super.onDisable();		   
			}

			@Override
			protected void onEnable() {
				removeStyleName("item-disabled");
				super.onEnable();
			}
		};
		btSourceAdd.ensureDebugId("345EFB2F-1500-4cde-9D1F-23469CFAF8DF");
		//btSourceAdd.setWidth(70);
		btSourceAdd.setMinWidth(70);
		btSourceAdd.setAutoWidth(true);
		Utils.addToolTip(btSourceAdd, UIContext.Constants.ArchiveAddSourceTooltip());
		btSourceAdd.addListener(Events.Select, archiveSettingsListener);
		btSourceAdd.setEnabled(false);
			
		lcButtonPanel.add(btSourceAdd,tdSourceAdd);
		
		btSourceRemove = new Button(UIContext.Constants.ArchiveRemoveSource())
		{
			@Override
			protected void onDisable() {
				addStyleName("item-disabled");
				super.onDisable();		   
			}

			@Override
			protected void onEnable() {
				removeStyleName("item-disabled");
				super.onEnable();
			}
		};
		btSourceRemove.ensureDebugId("9F62BDFE-599B-4d76-9D27-F6F797F1780D");
		//btSourceRemove.setWidth(70);
		btSourceRemove.setMinWidth(70);
		btSourceRemove.setAutoWidth(true);
		btSourceRemove.setEnabled(false);
		Utils.addToolTip(btSourceRemove, UIContext.Constants.ArchiveRemoveSourceTooltip());
		btSourceRemove.addListener(Events.Select,archiveSettingsListener);
		lcButtonPanel.add(btSourceRemove,tdSourceAdd);
		
		btSourceFiltersConfigure = new Button(UIContext.Constants.ArchiveModifySource())
		{
			@Override
			protected void onDisable() {
				addStyleName("item-disabled");
				super.onDisable();		   
			}

			@Override
			protected void onEnable() {
				removeStyleName("item-disabled");
				super.onEnable();
			}
		};
		btSourceFiltersConfigure.ensureDebugId("31276429-135C-487d-BDA3-9D9D10D282B2");
		//btSourceFiltersConfigure.setWidth(70);
		btSourceFiltersConfigure.setMinWidth(70);
		btSourceFiltersConfigure.setAutoWidth(true);
		btSourceFiltersConfigure.setEnabled(false);
		Utils.addToolTip(btSourceFiltersConfigure, UIContext.Constants.ArchiveModifySourceTooltip());
		btSourceFiltersConfigure.addListener(Events.Select, archiveSettingsListener);
		lcButtonPanel.add(btSourceFiltersConfigure,tdSourceAdd);
					
		return lcButtonPanel;
	}
	
	/*private void onGetSelectedBackupVolumesSucceed(
		List<FileModel> result, ArchivePoliciesWindow archivePoliciesWindow )
	{
		if (result != null)
		{
			//add backup dest also so that it will not be shown in archivable volumes
			//addBackupDestToNonSelectableValumesList(result);
			
			String strBackupDest = archivePoliciesWindow.getBackupDestination();
			if(strBackupDest != null && strBackupDest.indexOf(":") != -1)
			{
				strBackupDest = strBackupDest.substring(0,strBackupDest.indexOf(":")+1);
				strBackupDest += "\\";
				FileModel backupDest = new FileModel();
				backupDest.setName(strBackupDest);
				backupDest.setIsNetworkPath(false);
				backupDest.setType(1000);//to represent backup dest type
				result.add(backupDest);
			}
			
			archivePoliciesWindow.setBackupVolumes(result);			
			NonSelectableVolumes = result;
			//ArchiveSourceSelectionPanel.setVolumesList2Filter(backupVolumes);
		}
		parentWind.unmask();
		archivePoliciesWindow.show();
	}*/

	private boolean defineArchiveSettingsListener()
	{
		archiveSettingsListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent ArchiveEvent) {
				
				if(ArchiveEvent.getSource() == btSourceAdd)
				{
					addSource();
				}
				else if(ArchiveEvent.getSource() == btSourceRemove)
				{
					ArchiveSourceInfoModel archiveSourceModel = ArchiveSourcesGrid.getSelectionModel().getSelectedItem();
					if(archiveSourceModel == null)
					{
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.INFO);
						msgError.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
						msgError.setModal(true);
						msgError.setMessage(UIContext.Constants.SelectPolicyToDelete());
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
						return;
					}
					String displaySource = archiveSourceModel.getDispalySourcePath();
					String actualSource = archiveSourceModel.getSourcePath();					
					if(displaySource != null && actualSource!=null)
					{
						if(!displaySource.equalsIgnoreCase(actualSource))
						{
							removeMessageFromNotificationSet(displaySource,actualSource);
						}
					}
					resetFlagsAndUpdateNotificationSection();
					handleNotificationIfSystemVolumeForRemove(actualSource);
					gridStore.remove(archiveSourceModel);
					ArchiveSourcesGrid.reconfigure(gridStore, ArchiveSourceColumnsModel);
				}
				else if(ArchiveEvent.getSource() == btSourceFiltersConfigure)
				{
					archiveSourceModel = null;
					archiveSourceModel = ArchiveSourcesGrid.getSelectionModel().getSelectedItem();	
					
					oldDispalySourcePath = archiveSourceModel.getDispalySourcePath();
					oldActualSourcePath = archiveSourceModel.getSourcePath();
					
					if(archiveSourceModel != null)
					{
						if(archiveSourceModel.getArchiveFiles())
							iSelectedPolicyType = 2;
						else if(archiveSourceModel.getCopyFiles())
							iSelectedPolicyType = 1;
						//Add param parentWind.getRefsNtfsModels() to add all refs and ntfs volumes, added by wanqi06
						final ArchivePoliciesWindow archivePoliciesWindow = new ArchivePoliciesWindow(archiveSourceModel,iSelectedPolicyType,parentWind.getFilterModels(),parentWind.getRefsNtfsModels());
						archivePoliciesWindow.setModal(true);
						archivePoliciesWindow.addWindowListener(new WindowListener() {
							public void windowHide(WindowEvent we) {
								if (archivePoliciesWindow.getButtonClicked().compareToIgnoreCase(Dialog.OK) == 0) {
									archiveSourceModel = archivePoliciesWindow.getSelectedSourceModel();
									boolean isPathChaged = archivePoliciesWindow.isPathSourcePathChanged();	    
									/*if(NonSelectableVolumes == null)
									{
										NonSelectableVolumes = archivePoliciesWindow.getBackupVolumes();
									}*/
									//While modifying the policy we have to remove the notification messages for the old policy									
									if(oldDispalySourcePath!= null && oldActualSourcePath != null)
									{
										if(!(oldDispalySourcePath.equalsIgnoreCase(oldActualSourcePath)))
										{
											removeMessageFromNotificationSet(oldDispalySourcePath,oldActualSourcePath);
										}
										resetFlagsAndUpdateNotificationSection();
										handleNotificationIfSystemVolumeForRemove(oldActualSourcePath);
									}
									
									//configure the notification set for new modified policy
									if(archiveSourceModel != null)
									{
										gridStore.update(archiveSourceModel);
										ArchiveSourcesGrid.reconfigure(gridStore, ArchiveSourceColumnsModel);
										if(isPathChaged)
										{											
    										showNotificationSet(archiveSourceModel.getDispalySourcePath(),archiveSourceModel.getSourcePath());
										}
    									//else
    										//hideNotificationSet();
										resetFlagsAndUpdateNotificationSection();
										handleNotificationIfSystemVolume(archiveSourceModel.getSourcePath());
									}
								}
								return;
							}
						});
						
						backupSelectedVolumesModel = parentWind.getSelectedBackupVolumes();
						archivePoliciesWindow.setSelectedBackupVolumes(backupSelectedVolumesModel);
						
						backupSelectedRefsVolumesModel = parentWind.getSelectedRefsVolumes();						
						archivePoliciesWindow.setSelectedBackupRefsVolumes(backupSelectedRefsVolumesModel);
						
						backupSelectedDedupeVolumesModel = parentWind.getSelectedDedupeVolumes();						
						archivePoliciesWindow.setSelectedBackupDedupeVolumes(backupSelectedDedupeVolumesModel);
						
						archivePoliciesWindow.setBackupDestination(parentWind.getbackupDestination());
						archivePoliciesWindow.show();
						
						/*if(NonSelectableVolumes != null)
						{
							UpdateBackupDestInNonSelectableVolumes(archivePoliciesWindow.getBackupDestination());
							archivePoliciesWindow.setBackupVolumes(NonSelectableVolumes);
							archivePoliciesWindow.show();
						}*/
					}
					else
					{
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.INFO);
						msgError.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
						msgError.setModal(true);
						msgError.setMessage(UIContext.Constants.SelectPolicyToConfigure());
						msgError.getDialog().getButtonById(Dialog.OK).ensureDebugId("FD114C05-2A6C-439d-B5EE-D3E47BBE2C3A");
						msgError.show();
					}
				}
				else if(ArchiveEvent.getSource() == ArchiveSourcesGrid)
				{
					ArchiveSourceInfoModel archiveSource = ArchiveSourcesGrid.getSelectionModel().getSelectedItem();
					List<ArchiveSourceInfoModel> archiveSourcesList = gridStore.getModels();
					int iSelectedIndex = archiveSourcesList.indexOf(archiveSource);
					
					btSourceRemove.setEnabled((iSelectedIndex != -1 && cbArchiveAfterBackup.getValue())? true : false);
					btSourceFiltersConfigure.setEnabled((iSelectedIndex != -1 && cbArchiveAfterBackup.getValue()) ? true : false);
				}
			}
		};
		
		
		//madra04
		archiveScheduleSettingsListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent ArchiveEvent) {
				if(ArchiveEvent.getSource() == cbArchiveAfterBackup)
				{
					BackupContext.setFileCopyEnable(cbArchiveAfterBackup.getValue());
					if (!cbArchiveAfterBackup.getValue())
					{
						notificationSet.setVisible(true);
						notificationSet.expand();
						if(!parentWind.isArchiveTask())
							recoveryPointFieldContainer.setEnabled(false);
						btSourceAdd.setEnabled(false);
						btSourceRemove.setEnabled(false);
						btSourceFiltersConfigure.setEnabled(false);
						
					}						
					else
					{
						notificationSet.setVisible(false);
						if(!parentWind.isArchiveTask()){
							recoveryPointFieldContainer.setEnabled(true);
							/*if(!backupSchedRadio.getValue()){
								backupScheduleContentContainer.setEnabled(false);
							}*/
						}
						btSourceAdd.setEnabled(true);
						ArchiveSourceInfoModel archiveSource = ArchiveSourcesGrid.getSelectionModel().getSelectedItem();
						List<ArchiveSourceInfoModel> archiveSourcesList = gridStore.getModels();
						int iSelectedIndex = archiveSourcesList.indexOf(archiveSource);
						
						btSourceRemove.setEnabled(iSelectedIndex != -1? true : false);
						btSourceFiltersConfigure.setEnabled(iSelectedIndex != -1 ? true : false);
						
						if(parentWind.backupContent.getIsAllVolumeIsRefsOrDedup()){
							cbArchiveAfterBackup.setValue(false);
							MessageBox mb = new MessageBox();
							mb.setIcon(MessageBox.ERROR);
							mb.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
							mb.setModal(true);
							mb.setMinWidth(400);
							mb.setMessage(UIContext.Constants.ArchiveCanNotEnableFileCopy());
							Utils.setMessageBoxDebugId(mb);
							mb.show();
						}
						// File copy (aka "Archive) does not depend on file system catalog any more.
						// so we removed the logic for verifying file system catalog settings.
					}
				
				}				
				
			}
		};
		
		return true;
	}
	
	
	private void addSource() {

		ArchiveSourceInfoModel archiveSourceModel = new ArchiveSourceInfoModel();
		//Add param parentWind.getRefsNtfsModels() to add all refs and ntfs volumes, added by wanqi06
		final ArchivePoliciesWindow archivePoliciesWindow = new ArchivePoliciesWindow(archiveSourceModel,iSelectedPolicyType, parentWind.getFilterModels(),parentWind.getRefsNtfsModels());
		archivePoliciesWindow.setModal(true);
		
		archivePoliciesWindow.addWindowListener(new WindowListener() {
			public void windowHide(WindowEvent we) {
				if (archivePoliciesWindow.getButtonClicked().compareToIgnoreCase(Dialog.OK) == 0) {
					ArchiveSourceInfoModel sourceModel = null;
					sourceModel = archivePoliciesWindow.getSelectedSourceModel();
					boolean isPathChaged = archivePoliciesWindow.isPathSourcePathChanged();	    									
					//seems wrong logic
//					if(NonSelectableVolumes == null)
//					{
//						NonSelectableVolumes = archivePoliciesWindow.getBackupVolumes();
//					}
					if((sourceModel != null) && (sourceModel.IsArchiveSourceConfigured()))
					{
						gridStore.add(sourceModel);
						ArchiveSourcesGrid.reconfigure(gridStore, ArchiveSourceColumnsModel);
						if(isPathChaged)
							showNotificationSet(sourceModel.getSourcePath(),sourceModel.getDispalySourcePath());	
						resetFlagsAndUpdateNotificationSection();
						handleNotificationIfSystemVolume(sourceModel.getSourcePath());
					}
				}
				return;
			}
		});

		backupSelectedVolumesModel = parentWind.getSelectedBackupVolumes();
		archivePoliciesWindow.setSelectedBackupVolumes(backupSelectedVolumesModel);
		
		backupSelectedRefsVolumesModel = parentWind.getSelectedRefsVolumes();						
		archivePoliciesWindow.setSelectedBackupRefsVolumes(backupSelectedRefsVolumesModel);
		
		backupSelectedDedupeVolumesModel = parentWind.getSelectedDedupeVolumes();						
		archivePoliciesWindow.setSelectedBackupDedupeVolumes(backupSelectedDedupeVolumesModel);
		
		archivePoliciesWindow.setBackupDestination(parentWind.getbackupDestination());
		archivePoliciesWindow.show();
		//Uncomment this because we get non backup volumes from BackupSettings
		/*if(NonSelectableVolumes == null)
		{
			if (UIContext.isLaunchedForEdgePolicy)
			{
				NonSelectableVolumes = new ArrayList<FileModel>();
				onGetSelectedBackupVolumesSucceed( NonSelectableVolumes, archivePoliciesWindow );
			}	    							
		}
		else
		{
			UpdateBackupDestInNonSelectableVolumes(archivePoliciesWindow.getBackupDestination());
			archivePoliciesWindow.setBackupVolumes(NonSelectableVolumes);
			archivePoliciesWindow.show();
		}*/
	
		
	}

	public void showNotificationSet(String symbolicPath, String actualPath)
	{
		boolean addToNotifiSet = true;
		if(symbolicPath != null && actualPath != null)
		{
			//displaySourceNotificationSet.removeAll();	
			//Fix issue 109262, replay the message by a generic note.
			String message = UIContext.Messages.notificationMessageGeneric();
			if(notificationMessages != null)
			{
				allNotificationMessages.add(message);
				if(notificationMessages.contains(message))
				{
					addToNotifiSet = false;
					
				}
				else
				{
					addToNotifiSet = true;
					notificationMessages.add(message);
				}
				
			}
			if(addToNotifiSet)
			{
				addDisplaySourceWaringIcon();
				displaySourceNotificationSet.add(new LabelField(message));
				displaySourceNotificationSet.setVisible(true);
				displaySourceNotificationSet.expand();
				updateNotificationPane();
			}
		}
	}
	
	private void handleNotificationSection()
	{		
		boolean addToNotifiSet = true;		
		if(isSystemOrBootVolumeFlag)
		{
			String message = UIContext.Messages.archiveSystemVolumeNotification();
			allNotificationMessages.add(message);				
			if(notificationMessages.contains(message))
			{
				addToNotifiSet = false;
				
			}
			else
			{
				addToNotifiSet = true;
				notificationMessages.add(message);
			}
			if(addToNotifiSet)
			{
				addDisplaySourceWaringIcon();					
				displaySourceNotificationSet.add(new LabelField(message));
				displaySourceNotificationSet.setVisible(true);
				displaySourceNotificationSet.expand();				
				//isSystemVolumeNotifAdded = true;					
				updateNotificationPane();				
			}
					
		}		
		
	}
	
	private void resetFlagsAndUpdateNotificationSection()
	{
		isSystemOrBootVolumeFlag=false;		
	}
	
	public void addNotificationMessages(String symbolicPath, String actualPath)
	{
		if(symbolicPath != null && actualPath!= null)
		{
		   if( !(actualPath.equalsIgnoreCase(symbolicPath))	)
		   {
			   //Fix issue 109262, replay the message by a generic note.
			   //String message = UIContext.Messages.notificationMessage(actualPath,symbolicPath);
			   //String message = UIContext.Messages.notificationMessageGeneric();
			   showNotificationSet(symbolicPath, actualPath);
		   }
	   }
	}
	
	public void removeMessageFromNotificationSet(String symbolicPath, String actualPath)
	{
		//Fix issue 109262, replay the message by a generic note.
		//String message = UIContext.Messages.notificationMessage(symbolicPath,actualPath);
		String message = UIContext.Messages.notificationMessageGeneric();
		if(notificationMessages != null)
		{
			if(notificationMessages.contains(message))
			{
				if(checkForDuplicate(message))
				{
					notificationMessages.remove(message);	
				}
				allNotificationMessages.remove(message);
				if(notificationMessages.size() == 0)	
				{
					hideNotificationSet();
					displaySourceNotificationSet.getItems().clear();				
					
				}
				else
				{					
					displaySourceNotificationSet.getItems().clear();									
					reconfigureNotificationSet();
				}
					
			}
			else if(allNotificationMessages != null && allNotificationMessages.contains(message))
			{
				allNotificationMessages.remove(message);
			}
		}
	}
	
	public void removeMessageFromNotificationSet()
	{
		//Fix issue 109262, replay the message by a generic note.
		//String message = UIContext.Messages.notificationMessage(symbolicPath,actualPath);
		String message = UIContext.Messages.archiveSystemVolumeNotification();
		if(notificationMessages != null)
		{
			if(notificationMessages.contains(message))
			{
				if(checkForDuplicate(message))
				{
					notificationMessages.remove(message);	
				}
				allNotificationMessages.remove(message);
				if(notificationMessages.size() == 0)	
				{
					hideNotificationSet();
					displaySourceNotificationSet.getItems().clear();				
					
				}
				else
				{					
					displaySourceNotificationSet.getItems().clear();									
					reconfigureNotificationSet();
				}
					
			}
			else if(allNotificationMessages != null && allNotificationMessages.contains(message))
			{
				allNotificationMessages.remove(message);
			}
		}
	}
	
	public boolean checkForDuplicate(String message)
	{
		int count = 0;
		for(int i = 0; i<allNotificationMessages.size() ; i++)
		{
			if(allNotificationMessages.get(i).equalsIgnoreCase(message))
				count++;
		}
	
		if(count > 1)
			return false;
		else
			return true;
		
	}
	
	public void reconfigureNotificationSet()
	{
		for(int i = 0; i<notificationMessages.size() ; i++)
		{
			String message =  notificationMessages.get(i);
			addDisplaySourceWaringIcon();
			displaySourceNotificationSet.add(new LabelField(message));
			displaySourceNotificationSet.setVisible(true);
			displaySourceNotificationSet.expand();
			updateNotificationPane();
		
		}
		
	}	

	
	public void hideNotificationSet()
	{
		
		displaySourceNotificationSet.setVisible(false);	
	}
	
	private void updateNotificationPane() {
		displaySourceNotificationSet.setHeadingHtml(UIContext.Messages.ArchiveSettingsNodifications());		
		if(!displaySourceNotificationSet.isExpanded())
			displaySourceNotificationSet.expand();
		displaySourceNotificationSet.layout(true);
	}
	
	
	public void checkIfPoliciesFromSystemVolumes()
	{
			
		loginService.getVolumesWithDetails(0,null, null, null, new BaseAsyncCallback<List<FileModel>>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}
			@Override
			public void onSuccess(List<FileModel> result) {
				for (int i = 0, count = result == null ? 0 : result.size(); i < count; i++) {
					FileModel fileModel = result.get(i);
					fileModel.setIsVolume(true);
				}
				volumes = result;
				if(volumes!=null)
				{
					handleNotificationIfSystemVolume();	
				}
				
			}
		});		
	}
	
	public void handleNotificationIfSystemVolume()
	{	
		if(gridStore.getCount()!=0)
		{
			for (int i = 0; i < gridStore.getCount(); i++) {
				showNotificationIfSystemVolume(gridStore.getAt(i));
			}
		}
	}
	
	public void handleNotificationIfSystemVolume(String path)
	{		
	   showNotificationIfSystemVolume(path);			
		
	}	
	
	private void showNotificationIfSystemVolume(String path)
	{
		String volumeName = Utils.getDriveLetter(path);
		if(isSystemOrBootVolume(volumeName))
		{
			isSystemOrBootVolumeFlag=true;
			handleNotificationSection();			
	    }		
	}
	
	private void showNotificationIfSystemVolume(ArchiveSourceInfoModel model)
	{
		String volumeName = Utils.getDriveLetter(model.getSourcePath());
		if(isSystemOrBootVolume(volumeName))
		{
			isSystemOrBootVolumeFlag=true;
			handleNotificationSection();			
	    }
	
	}
	
	public void handleNotificationIfSystemVolumeForRemove(String source)
	{	
		showNotificationIfSystemVolumeForRemove(source);
	}
	
	private void showNotificationIfSystemVolumeForRemove(String path)
	{		
		String volumeName = Utils.getDriveLetter(path);
		if(isSystemOrBootVolume(volumeName))
		{		
			removeMessageFromNotificationSet();
		}		
	}
	
	private boolean isSystemOrBootVolume(String volumeName) {
		VolumeModel model = (VolumeModel)getVolumeModel(volumeName);
		if(isSystemVolume(model))
			return true;
		if(isBootVolume(model))
			return true;
		return false;
	}
	
	private boolean isSystemVolume(VolumeModel model) {
		return model.getSubStatus() != null && 
				(model.getSubStatus() & VolumeSubStatus.EVSS_SYSTEM) > 0;
	}
	private boolean isBootVolume(VolumeModel model) {
		return model.getSubStatus() != null 
				&& (model.getSubStatus() & VolumeSubStatus.EVSS_BOOT) > 0;
	}
	private FileModel getVolumeModel(String sourcePath)
	{
		VolumeModel volumeModel = new VolumeModel();
		for (int i = 0, count = volumes == null ? 0 : volumes.size(); i < count; i++) {
			FileModel fileModel = volumes.get(i);
			if(fileModel.getName().equalsIgnoreCase(sourcePath))
			{
				return fileModel;
			}
		}
		return volumeModel;
		
	}
	
	/*private void UpdateBackupDestInNonSelectableVolumes(String backupDestination) {
		Iterator itr = (Iterator)NonSelectableVolumes.iterator();
		
		while(itr.hasNext())
		{
			FileModel model = (FileModel)itr.next(); 
			if(model.getType() == 1000)
				itr.remove();
		}
		
		if(backupDestination != null && backupDestination.indexOf(":") != -1)
		{
			backupDestination = backupDestination.substring(0,backupDestination.indexOf(":")+1);
			backupDestination += "\\";
			FileModel backupDest = new FileModel();
			backupDest.setName(backupDestination);
			backupDest.setIsNetworkPath(false);
			backupDest.setType(1000);//to represent backup dest type
			NonSelectableVolumes.add(backupDest);
		}
	}*/
	
/*	private void UpdatePurgeSettings(boolean bEnable)
	{
		nfDays.setEnabled(bEnable);
		//PurgeTimeContainer.setEnabled(bEnable);
		purgeStartTimeContainer.setEnabled(bEnable);
		return;
	}*/
	
	private static String GetCriteraiValueType(String strFileSize,
			String criteriaOperator) {
		
		if((strFileSize == null) || (criteriaOperator == null))
		{
			return "";
		}
		String[] StringKeys = strFileSize.split("\\\\");
		
		for(int iIndex = 0;iIndex < StringKeys.length ; iIndex++)
		{
			if(Integer.parseInt(StringKeys[iIndex]) != 0)
			{
				switch(iIndex)
				{
				case 0:
					if(criteriaOperator.compareToIgnoreCase("FileSize") == 0)
					{
						return UIContext.Constants.KB();
					}else return UIContext.Constants.months(); 
				case 1:
					if(criteriaOperator.compareToIgnoreCase("FileSize") == 0)
					{
						return UIContext.Constants.MB();
					}else return UIContext.Constants.days();
				case 2:
					if(criteriaOperator.compareToIgnoreCase("FileSize") == 0)
					{
						return UIContext.Constants.GB();
					}else return UIContext.Constants.years();
				}
				break;
			}
		}
		return null;
	}
	
	/*
	private class PolicySelectWindow extends Window
	{
		private PolicySelectWindow thisWindow;
		private final static int MAX_WIDTH = 400;
		private final static int MAX_HEIGHT = 200;
		
		private Button btOK;
		private Button btCancelJob;
		private Button btHelp;
		
		private LayoutContainer lcPolicyTypeSelection;
		
		//Archive and File Copy options container
		private RadioGroup rgArchiveOptions;
		private Radio rbArchive;
		private Radio rbFilecopy;
		
		private boolean isClickOK = false;
		public PolicySelectWindow()
		{
			thisWindow = this;
			thisWindow.setScrollMode(Scroll.NONE);
			thisWindow.setResizable(false);
			thisWindow.setHeadingHtml(UIContext.Constants.ArchivePolicyType());
			thisWindow.setWidth(MAX_WIDTH);
			thisWindow.setHeight(MAX_HEIGHT);
			
			lcPolicyTypeSelection = new LayoutContainer();
			TableLayout tlPolicyTypeSelection = new TableLayout(1);
			tlPolicyTypeSelection.setCellSpacing(5);
			lcPolicyTypeSelection.setLayout(tlPolicyTypeSelection);
			lcPolicyTypeSelection.setWidth("100%");
			lcPolicyTypeSelection.setHeight("100%");
			lcPolicyTypeSelection.setStyleName("Wizard_BackGround");
			
			definePolicyType();
			thisWindow.add(lcPolicyTypeSelection);
			
			definePolicySelectButtons();
			
			iSelectedPolicyType = 1;
			thisWindow.setFocusWidget(btOK);
		}

		private void definePolicyType() {
			
			Listener<BaseEvent> policyTypeListener = new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent sourceEvent) {
					if(sourceEvent.getSource() == rbFilecopy)
					{
						if(rbFilecopy.getValue())
							iSelectedPolicyType = 1;
					}
					else if(sourceEvent.getSource() == rbArchive)
					{
						if(rbArchive.getValue())
						{
							iSelectedPolicyType = 2;
							MessageBox msgError = new MessageBox();
							msgError.setIcon(MessageBox.WARNING);
							msgError.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameD2D));
							msgError.setModal(true);
							msgError.setMinWidth(500);
							
							msgError.setMessage(UIContext.Messages.ArchiveDeletedFilesFoldersMessage());
							Utils.setMessageBoxDebugId(msgError);
							msgError.show();
						}
					}
				}

			};
			
			rgArchiveOptions = new RadioGroup();
			rbArchive = new Radio();
			rbArchive.ensureDebugId("1157A9A5-B608-45f2-949B-2C62D9B0E356");
			rbArchive.setBoxLabel(UIContext.Constants.ArchiveFilesLabelDelete());
			rbArchive.addStyleName("ArchivePolicyOptionStyle");
			rbArchive.setWidth(200);
			rbArchive.addListener(Events.Change,policyTypeListener);
			rgArchiveOptions.add(rbArchive);
			
			rbFilecopy = new Radio();
			rbFilecopy.ensureDebugId("C55B4EAC-0784-4c77-B8C2-F2B6751B638C");
			rbFilecopy.setBoxLabel(UIContext.Constants.ArchiveFileCopyLabel());
			rbFilecopy.setWidth(200);
			rbFilecopy.setValue(true);
			rbFilecopy.addStyleName("ArchivePolicyOptionStyle");
			rbFilecopy.addListener(Events.Change,policyTypeListener);
			
			rgArchiveOptions.add(rbFilecopy);			
			lcPolicyTypeSelection.add(rbFilecopy);
			LabelField lblFileCopyDesc = new LabelField(UIContext.Constants.ArchiveFileCopyDescription());
			lblFileCopyDesc.addStyleName("ArchivePolicyTypeStyle");
			lblFileCopyDesc.setWidth(340);
			//lblFileCopyDesc.addStyleName("restoreWizardSubItem");
			lcPolicyTypeSelection.add(lblFileCopyDesc);
			
			lcPolicyTypeSelection.add(rbArchive);
			LabelField lblArchiveDesc = new LabelField(UIContext.Constants.ArchiveFilesDescription());
			lblArchiveDesc.addStyleName("ArchivePolicyTypeStyle");
			lblArchiveDesc.setWidth(370);
			//lblArchiveDesc.addStyleName("restoreWizardSubItem");
			lcPolicyTypeSelection.add(lblArchiveDesc);
			//rgArchiveOptions.setValue(rbFilecopy);
			rgArchiveOptions.addListener(Events.Change,new Listener<FieldEvent>() {

				@Override
				public void handleEvent(FieldEvent archiveEvent) {
					if(rbFilecopy.getValue())
					{
						iSelectedPolicyType = 1;
					}
					else if(rbArchive.getValue())
					{
						iSelectedPolicyType = 2;
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.WARNING);
						msgError.setTitleHtml(UIContext.Constants.messageBoxTitleWarning());
						msgError.setModal(true);
						msgError.setMinWidth(500);
						
						msgError.setMessage(UIContext.Messages.ArchiveDeletedFilesFoldersMessage());
						msgError.show();
					}
				}
			});
		}
		
		public final int MIN_WIDTH = 50;
		private void definePolicySelectButtons()
		{
			Listener<ButtonEvent> ButtonListeenr = new Listener<ButtonEvent>() {
				@Override
				public void handleEvent(ButtonEvent be) {
					if(be.getSource() == btCancelJob)
					{
						isClickOK = false;
						thisWindow.hide();
					}
					else if(be.getSource() == btOK)
					{
						isClickOK = true;
						thisWindow.hide();
					}
						
				}
			};
			
			btOK = new Button();
			btOK.ensureDebugId("2E89AD3C-A5A4-4e7a-970B-5AB1949E096A");
			btOK.setText(UIContext.Constants.ok());
			btOK.setMinWidth(MIN_WIDTH);		
			btOK.addListener(Events.Select,ButtonListeenr);		
			thisWindow.addButton(btOK);
			
			btCancelJob = new Button();
			btCancelJob.ensureDebugId("34172103-289A-4df3-A5AD-800ABC2A466C");
			btCancelJob.setText(UIContext.Constants.cancel());
			btCancelJob.setMinWidth(MIN_WIDTH);
			btCancelJob.addListener(Events.Select, ButtonListeenr);
			
			thisWindow.addButton(btCancelJob);
			
			btHelp = new Button();
			btHelp.ensureDebugId("40792684-954C-4e42-B502-838088CFA2F2");
			btHelp.setText(UIContext.Constants.help());
			btHelp.setMinWidth(MIN_WIDTH);
			btHelp.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					HelpTopics.showHelpURL(UIContext.externalLinks.getArchivePoliciesHelp());
				}
			});
			thisWindow.addButton(btHelp);	
		}
		
		public boolean getCancelled() {
			return !isClickOK;
		}
	}*/
	
	public BackupVolumeModel getSelectedBackupVolumes()
	{
		return backupSelectedVolumesModel;
	}
	
	/*public void getFATVolumeInfo() {
		parentWind.contentHost.increaseBusyCount();
		loginService.getFATVolumesInfo(new BaseAsyncCallback<List<FileModel>>() {
			
			@Override
			public void onSuccess(List<FileModel> result) {
				if (result != null)
				{	
					NonSelectableVolumes = result;
				}
				parentWind.contentHost.decreaseBusyCount();
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				parentWind.contentHost.decreaseBusyCount();
				super.onFailure(caught);
			}
		});
	}*/
	
}
