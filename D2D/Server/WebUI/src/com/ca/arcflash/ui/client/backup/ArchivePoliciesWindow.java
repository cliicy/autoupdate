package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveConstantsModel;
import com.ca.arcflash.ui.client.model.ArchiveDiskDestInfoModel;
import com.ca.arcflash.ui.client.model.ArchiveSourceFilterModel;
import com.ca.arcflash.ui.client.model.ArchiveSourceInfoModel;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.ca.arcflash.ui.client.model.FileModel;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ArchivePoliciesWindow extends Window {
	
	private ArchivePoliciesWindow thisWindow;
	final CommonServiceAsync commonService = GWT.create(CommonService.class);
	final LoginServiceAsync loginService = GWT.create(LoginService.class);
	
	private LayoutContainer lcArchiveSettingsContainer;
	private LayoutContainer lcArchiveSourceContainer;
	
	ArrayList<ArchiveSourceInfoModel> ArchiveSources;
	
	//source selection panel
	private PathSelectionPanel ArchiveSourceSelectionPanel;
	private static String strArchiveSource="";
	
	private static String originalArchiveSourcePath="";
	
	//Filter Settings
	private BaseSimpleComboBox<String> cbFilter;
	private BaseSimpleComboBox<String> cbFilterType;
	private BaseSimpleComboBox<String> cbFileFilterValue;
	private List<String> defaultFilterList;
	private List<String> defaultEnglishFilterList;
	private TextField<String> cbFolderFilterValue;
	private List<String> FilterTypeList;

	private Button btAddFilter;
	private Button btDeleteFilter;
	//private Button btModifyFilter;
	
	//File size criteria settings
	private LayoutContainer lcFileSizeSettingsContainer;
	private LabelField lblFileSizeLabel;
	private LabelField lblFileSizeDescription;	
	private LayoutContainer lcFileSizeContainer;
	private CheckBox cbFilterBySize;
	private BaseSimpleComboBox<String> cbFilterBySizeOperator;
	private NumberField nfLowerSize;
	private BaseSimpleComboBox<String> cbLowerSizeType;
	private LabelField lblAnd;
	private NumberField nfHigherSize;
	private BaseSimpleComboBox<String> cbHigherSizeType;
	
	//File Age criteria Settings
	private LabelField lblFileAgeLabel;
	private LabelField lblFileAgeDescription;
	private LayoutContainer lcFileAgeContainer;
	private CheckBox cbFileAccessed;
	private CheckBox cbFileModified;
	private CheckBox cbFileCreated;
	
	private NumberField nfAccessed;
	private NumberField nfModified;
	private NumberField nfCreated;
	
	private BaseSimpleComboBox<String> cbFileAccessedAgeType;
	private BaseSimpleComboBox<String> cbFileModifiedAgeType;
	private BaseSimpleComboBox<String> cbFileCreatedAgeType;	
	
	private Button btOK;
	private Button btCancel;
	private Button btPreview;
	private Button btHelp;
	private String strButtonClicked = "";
	
	private Grid<ArchiveSourceFilterModel> ArchiveFiltersGrid;
	private ListStore<ArchiveSourceFilterModel> gridStore;
	private ColumnModel ArchiveFilterColumnsModel;
	private GridCellRenderer<ArchiveSourceFilterModel> FilterRenderer;
	private GridCellRenderer<ArchiveSourceFilterModel> FilterTypeRenderer;
	private GridCellRenderer<ArchiveSourceFilterModel> FilterValueRenderer;

	private ArchiveSourceInfoModel sourceInfoModel;
	
	private boolean isPathChanged = false;
	
	private Listener<BaseEvent> ArchiveSettingsWindowListener = null;
	private Listener<FieldEvent> archiveFieldsListener = null;
	
	ToolTipConfig tipConfig = null;
	
	private final int MIN_WIDTH = 90;
	private final int MAX_WIDTH = 700;
	private int MAX_HEIGHT = 720;
	
	private final int MAX_WIDTH_LESS_LINE = 60;
	
	// Filter Modes
	private final int ADD_FILTER = 0;
	private final int MODIFY_FILTER = 1;
	
	private final int POLICYTYPE_FILECOPY = 1;
	private final int POLICYTYPE_ARCHIVE  = 2;
	
	List<String> filtersMap = new ArrayList<String>();
	
	private final String strErrorMessageTitle = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
	
	//preview
	PreviewWindow archiveFilePreviewWind = null;
	int m_iSelectedPolicyType = -1;
	private List<FileModel> NonSelectableVolumes = null;
	private List<FileModel> RefsNtfsVolumes = null;
	private BackupVolumeModel selectedBackupVolumes;
	private BackupVolumeModel selectedBackupRefsVolumes;
	private BackupVolumeModel selectedBackupDedupeVolumes;
	private String backupDestination;
	
	public ArchivePoliciesWindow(ArchiveSourceInfoModel in_archiveSourceModel, int iSelectedPolicyType,List<FileModel> in_NonSelectableVolumes, List<FileModel> refs_NtfsVolumes)
	{
		NonSelectableVolumes = in_NonSelectableVolumes;
		//Save all refs and ntfs dedup volumes, added by wanqi06
		RefsNtfsVolumes = refs_NtfsVolumes;
		sourceInfoModel = in_archiveSourceModel;
		thisWindow = this;
		m_iSelectedPolicyType = iSelectedPolicyType;
		thisWindow.setHeadingHtml(UIContext.Constants.addFileSourceLabel());
		
		switch(m_iSelectedPolicyType)
		{
		case POLICYTYPE_ARCHIVE:
			MAX_HEIGHT -= 100;
			thisWindow.setScrollMode(Scroll.AUTOY);
			break;
		case POLICYTYPE_FILECOPY:
			if(GXT.isIE8)
				MAX_HEIGHT -= 280;
			else
				MAX_HEIGHT -= 285;
			break;
		}
		
		thisWindow.setResizable(false);
		thisWindow.setWidth(MAX_WIDTH);
		thisWindow.setHeight(MAX_HEIGHT);
		
		lcArchiveSettingsContainer = new LayoutContainer();
		TableLayout tlArchiveSettings = new TableLayout();
		tlArchiveSettings.setColumns(1);
		tlArchiveSettings.setCellPadding(1);
		tlArchiveSettings.setCellSpacing(0);
		lcArchiveSettingsContainer.setLayout(tlArchiveSettings);
		lcArchiveSettingsContainer.setStyleName("Wizard_BackGround");
		lcArchiveSettingsContainer.setWidth(MAX_WIDTH);
		lcArchiveSettingsContainer.setHeight(MAX_HEIGHT+20);

		defineArchiveSourceContainer();
		
		// defining the selection listerner required for all the buttons on Archive filters configuration page
		defineArchiveSettingsWindowListener();
			
		defineArchiveFiltersGrid();	
		
		if(m_iSelectedPolicyType != POLICYTYPE_FILECOPY)
		{
			defineArchiveFileSizeCriteriaSection();
			defineArchiveFileAgeCriteriaSection();
		}
		
		defineArchiveSettingsButtons();
		thisWindow.add(lcArchiveSettingsContainer);
		
		refreshFilterView();
		
		//default focus
//		thisWindow.setFocusWidget(ArchiveSourceSelectionPanel.getDestinationTextField());
	}
	
	private void defineArchiveSourceContainer()
	{
		lcArchiveSourceContainer = new LayoutContainer();
		TableLayout tlSourceLayout = new TableLayout(1);
		lcArchiveSourceContainer.setLayout(tlSourceLayout);
		lcArchiveSourceContainer.setWidth(MAX_WIDTH);
		
		//Adding source selection panel		
		LabelField lblArchiveSourceTitle = null;
		String archiveSourceSelectionDescription = UIContext.Constants.ArchiveSourceSelectionDescription();
		switch(m_iSelectedPolicyType)
		{
		case POLICYTYPE_ARCHIVE:
			lblArchiveSourceTitle = new LabelField(UIContext.Constants.ArchiveFilesLabelDelete());
			archiveSourceSelectionDescription = UIContext.Constants.fileArchiveSourceSelectionDescription();
			break;
		case POLICYTYPE_FILECOPY:
			lblArchiveSourceTitle = new LabelField(UIContext.Constants.ArchiveFilesLabelWithSource());
			break;
		}
		
		lblArchiveSourceTitle.setStyleName("restoreWizardSubItem");
		
		TableData tdArchiveSourceSubTitle = new TableData();
//		tdArchiveSourceSubTitle.setWidth("90%");
		tdArchiveSourceSubTitle.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcArchiveSettingsContainer.add(lblArchiveSourceTitle,tdArchiveSourceSubTitle);
		
		
		LabelField lblArchiveSourceDescription = new LabelField(archiveSourceSelectionDescription);
		lblArchiveSourceDescription.setWidth(MAX_WIDTH - MAX_WIDTH_LESS_LINE);
		lcArchiveSettingsContainer.add(lblArchiveSourceDescription,tdArchiveSourceSubTitle);
		
//		ArchiveSourceSelectionPanel = new PathSelectionPanel(new Listener<FieldEvent>() {
//
//			@Override
//			public void handleEvent(FieldEvent be) {
//				strArchiveSource = ArchiveSourceSelectionPanel.getDestination();
//			}
//		});
		ArchiveSourceSelectionPanel = new PathSelectionPanel(null);
		ArchiveSourceSelectionPanel.addDebugId("B6341246-5258-475e-9038-69EA2AF9E137", 
				"3FC91BCF-37F6-4be0-B3F5-00ABD2AA4834", "78224D4A-D918-4d67-A71E-3CB650085048");
		ArchiveSourceSelectionPanel.SetArchivePoliciesWindow(this);
		ArchiveSourceSelectionPanel.setChangeListener(new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				strArchiveSource = ArchiveSourceSelectionPanel.getDestination();
			}
		});
		ArchiveSourceSelectionPanel.setMode(PathSelectionPanel.ARCHIVE_MODE);
		ArchiveSourceSelectionPanel.setTooltipMode(PathSelectionPanel.TOOLTIP_ARCHIVE_SOURCE_MODE);
		ArchiveSourceSelectionPanel.setPathFieldLength(530);
		//Save refs and ntfs dedup volumes, added by wanqi06
		ArchiveSourceSelectionPanel.setVolumesList2Filter(NonSelectableVolumes, RefsNtfsVolumes);

		lcArchiveSettingsContainer.add(ArchiveSourceSelectionPanel,tdArchiveSourceSubTitle);
			
		Html line = new Html("<HR>");
 		line.setWidth(MAX_WIDTH - MAX_WIDTH_LESS_LINE);
		lcArchiveSettingsContainer.add(line);
		return;
	}
	
	private void defineArchiveFiltersGrid()
	{
		LayoutContainer lcSourceFilterContainer = new LayoutContainer();
		TableLayout tlSourceFilter = new TableLayout(3);
		tlSourceFilter.setCellSpacing(3);
		lcSourceFilterContainer.setLayout(tlSourceFilter);
		lcSourceFilterContainer.setHeight(30);
		lcSourceFilterContainer.setWidth(MAX_WIDTH);
		
		lcSourceFilterContainer.setStyleAttribute("margin", "2px, 2px, 2px, 10px");
		//lcSourceFilterContainer.setStyleName("WidgetPaddingLeft");
		
		TableData tdArchiveFiltersSection = new TableData();
		tdArchiveFiltersSection.setHorizontalAlign(HorizontalAlignment.LEFT);
		//Filters		
		LabelField lblArchiveFilters = new LabelField(UIContext.Constants.ArchiveFiltersLabel());
		lblArchiveFilters.setStyleName("restoreWizardSubItem");
		lblArchiveFilters.setWidth(MAX_WIDTH);
		lcArchiveSettingsContainer.add(lblArchiveFilters,tdArchiveFiltersSection);
		
		LabelField lblArchiveFiltersDescription = null;
		
		lblArchiveFiltersDescription = new LabelField(UIContext.Messages.ArchiveFiltersDescription());
		lblArchiveFiltersDescription.setWidth(MAX_WIDTH - MAX_WIDTH_LESS_LINE);
		lcArchiveSettingsContainer.add(lblArchiveFiltersDescription,tdArchiveFiltersSection);

		cbFilter = new BaseSimpleComboBox<String>();
		cbFilter.ensureDebugId("8B7A5E43-F0E9-4c39-8D30-A87D7714564F");
		cbFilter.setEditable(false);
		cbFilter.setWidth(115);
		cbFilter.setEnabled(true);
		cbFilter.add(UIContext.Constants.ArchiveFilterInclude());
		cbFilter.add(UIContext.Constants.ArchiveFilterExclude());
		
		cbFilter.setSimpleValue(UIContext.Constants.ArchiveFilterInclude());
		
		TableData tdFilter = new TableData();
		tdFilter.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcSourceFilterContainer.add(cbFilter,tdFilter);
				
		cbFilterType = new BaseSimpleComboBox<String>();
		cbFilterType.ensureDebugId("A912D1F1-1117-4eb0-9DA7-A0322DA575A8");
		cbFilterType.setEditable(false);
		cbFilterType.setWidth(130);
		cbFilterType.setEnabled(true);		
		
		cbFilterType.add(UIContext.Constants.ArchiveFilePattern());
		FilterTypeList = new ArrayList<String>();
		FilterTypeList.add(UIContext.Constants.ArchiveFilePattern());
		cbFilterType.add(UIContext.Constants.ArchiveFolderPattern());
		FilterTypeList.add(UIContext.Constants.ArchiveFolderPattern());

		cbFilterType.setSimpleValue(UIContext.Constants.ArchiveFilePattern());
		
		TableData tdFilterType = new TableData();
		tdFilterType.setHorizontalAlign(HorizontalAlignment.LEFT);
		cbFilterType.addListener(Events.Select, ArchiveSettingsWindowListener);
		lcSourceFilterContainer.add(cbFilterType,tdFilterType);
		
		//Defining File Filter Value Combo Box
		cbFileFilterValue = new BaseSimpleComboBox<String>();
		cbFileFilterValue.ensureDebugId("C133673A-8E31-45bf-B914-40DD4E99EE2A");
		cbFileFilterValue.setEditable(true);
		cbFileFilterValue.setWidth(400);
		cbFileFilterValue.setEnabled(true);
		cbFileFilterValue.show();
        cbFileFilterValue.setTemplate(getTemplate());
		
		cbFileFilterValue.addListener(Events.SelectionChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				if(cbFileFilterValue.getSimpleValue()!=null)
				{
					String filterSelected =  cbFileFilterValue.getSimpleValue().trim();
					if(!filterSelected.equals(UIContext.Constants.CustomFilterDisplayText()))
						cbFileFilterValue.setEditable(false);
					else
					{
						cbFileFilterValue.clear();
						cbFileFilterValue.setEditable(true);
					}	
						
				}
				
			}		
			
		});
		
		
		AddDefaultFilters();
		
		//Defining Folder Filter Value Text field
		cbFolderFilterValue = new TextField<String>();
		cbFolderFilterValue.ensureDebugId("09C76BBE-46AB-436b-9960-223808B88CFE");
		cbFolderFilterValue.setWidth(400);
		cbFolderFilterValue.hide();
		
		LayoutContainer lcFilterValuesContainer = new LayoutContainer();
		TableLayout tlFilterValuesContainer = new TableLayout(2);
		lcFilterValuesContainer.setLayout(tlFilterValuesContainer);
		
		TableData tdFilterValue = new TableData();
		tdFilterValue.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcFilterValuesContainer.add(cbFileFilterValue,tdFilterValue);
		lcFilterValuesContainer.add(cbFolderFilterValue,tdFilterValue);
		
		lcSourceFilterContainer.add(lcFilterValuesContainer);
		
		lcArchiveSettingsContainer.add(lcSourceFilterContainer);
		
		gridStore = new ListStore<ArchiveSourceFilterModel>();
		FilterRenderer = new GridCellRenderer<ArchiveSourceFilterModel>() {

			@Override
			public Object render(ArchiveSourceFilterModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<ArchiveSourceFilterModel> store,
					Grid<ArchiveSourceFilterModel> grid) {
				
				return GetLocalizedFilterByIndex(model.getFilterOrCriteriaType());
			}
		};
		
		FilterTypeRenderer = new GridCellRenderer<ArchiveSourceFilterModel>() {

			@Override
			public Object render(ArchiveSourceFilterModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<ArchiveSourceFilterModel> store,
					Grid<ArchiveSourceFilterModel> grid) {
				return GetLocalizedFilterTypeByIndex(model.getFilterOrCriteriaName());
			}
		};
		
		FilterValueRenderer = new GridCellRenderer<ArchiveSourceFilterModel>() {

			@Override
			public Object render(ArchiveSourceFilterModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<ArchiveSourceFilterModel> store,
					Grid<ArchiveSourceFilterModel> grid) {
			//	LabelField label = new LabelField(model.getLocFilterOrCriteriaLowerValue());
                LabelField label = new LabelField();
				label.setValue("<pre style=\"font-family: Tahoma,Arial;font-size: 11px;\">"+model.getLocFilterOrCriteriaLowerValue()+"</pre>");	
				tipConfig = new ToolTipConfig();
				tipConfig.setTemplate(Utils.getTooltipWrapTemplate(model.getLocFilterOrCriteriaLowerValue()));
				label.setToolTip(tipConfig);
				return label;
			}
		};
		
		List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		columnConfigs.add(Utils.createColumnConfig("Filter", UIContext.Constants.destinationVolumeType(), 100,FilterRenderer));
		columnConfigs.add(Utils.createColumnConfig("Pattern", UIContext.Constants.ArchiveFiltersVariableColumn(), 90,FilterTypeRenderer));
		columnConfigs.add(Utils.createColumnConfig("Value", UIContext.Constants.ArchiveFiltersValueColumn(), 330,FilterValueRenderer));
		
		ArchiveFilterColumnsModel = new ColumnModel(columnConfigs);
				
		ArchiveFiltersGrid = new Grid<ArchiveSourceFilterModel>(gridStore, ArchiveFilterColumnsModel);
		ArchiveFiltersGrid.setHeight(120);
		ArchiveFiltersGrid.setWidth(540);
		ArchiveFiltersGrid.unmask();
		ArchiveFiltersGrid.setAutoHeight(false);
		ArchiveFiltersGrid.setTrackMouseOver(true);
		ArchiveFiltersGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		ArchiveFiltersGrid.setBorders(true);
		ArchiveFiltersGrid.addListener(Events.RowClick, ArchiveSettingsWindowListener);

		TableData tdArchiveFilter = new TableData();
		tdArchiveFilter.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LayoutContainer lcFiltersContainer = new LayoutContainer();
		TableLayout tlFiltersGrid = new TableLayout();
		tlFiltersGrid.setCellSpacing(3);
		tlFiltersGrid.setColumns(2);
		lcFiltersContainer.setLayout(tlFiltersGrid);
		lcFiltersContainer.setHeight(120);
		lcFiltersContainer.setWidth(MAX_WIDTH);
		lcFiltersContainer.setStyleAttribute("margin", "2px, 2px, 2px, 10px");
		
		
		TableData tdFiltersGrid = new TableData();
		tdFiltersGrid.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		lcFiltersContainer.add(ArchiveFiltersGrid,tdFiltersGrid);
		
		//Adding the required buttons to control the filters grid.
			
		LayoutContainer lcButtonsContainer = new LayoutContainer();
		TableLayout tlButtonsContainer = new TableLayout();
		tlButtonsContainer.setColumns(1);
		tlButtonsContainer.setCellSpacing(10);
		lcButtonsContainer.setLayout(tlButtonsContainer);
		lcButtonsContainer.setHeight(120);
		
		btAddFilter = new Button(UIContext.Constants.ArchiveAddSource());
		btAddFilter.ensureDebugId("7D5805A5-D9D9-4bac-981A-EE58FCB644C4");
		//btAddFilter.setWidth(70);
		btAddFilter.setMinWidth(70);
		btAddFilter.setAutoWidth(true);
		TableData tdAddFilter = new TableData();
		//tdAddFilter.setHorizontalAlign(HorizontalAlignment.RIGHT);
		btAddFilter.addListener(Events.Select, ArchiveSettingsWindowListener);
		
		lcButtonsContainer.add(btAddFilter,tdAddFilter);
		
		btDeleteFilter = new Button(UIContext.Constants.ArchiveRemoveSource());
		btDeleteFilter.ensureDebugId("8DF07043-7A17-450d-9FBB-B9D2C9EDF291");
		//btDeleteFilter.setWidth(70);
		btDeleteFilter.setMinWidth(70);
		btDeleteFilter.setAutoWidth(true);
		btDeleteFilter.setEnabled(false);
		btDeleteFilter.addListener(Events.Select, ArchiveSettingsWindowListener);
		lcButtonsContainer.add(btDeleteFilter,tdAddFilter);
		
/*		btModifyFilter = new Button(UIContext.Constants.ArchiveModifySource());
		btModifyFilter.ensureDebugId("c77751e7-7314-4308-9f59-843a5ff0ee71");
		btModifyFilter.setWidth(70);
		btModifyFilter.setEnabled(false);
		btModifyFilter.addListener(Events.Select, ArchiveSettingsWindowListener);
		lcButtonsContainer.add(btModifyFilter,tdAddFilter);*/
//		lcButtonsContainer.setWidth(90);
		
		TableData tdFiltersGridButtonsPanel = new TableData();
		tdFiltersGridButtonsPanel.setHorizontalAlign(HorizontalAlignment.RIGHT);
		lcFiltersContainer.add(lcButtonsContainer,tdFiltersGridButtonsPanel);
		
		lcArchiveSettingsContainer.add(lcFiltersContainer);
		
		
		LabelField lblArchiveFilterslbl = new LabelField(UIContext.Constants.ArchiveSourcePattern());
		lblArchiveFilters.setStyleName("restoreWizardSubItem");
		lblArchiveFilters.setWidth(MAX_WIDTH);
		lcArchiveSettingsContainer.add(lblArchiveFilterslbl,tdArchiveFiltersSection);
		
		
		if(m_iSelectedPolicyType != POLICYTYPE_FILECOPY)
		{
			Html line = new Html("<HR>");
			line.setWidth(MAX_WIDTH - MAX_WIDTH_LESS_LINE);
			lcArchiveSettingsContainer.add(line);
		}
		return;
	}

	private void AddDefaultFilters() {
		
		defaultFilterList = new ArrayList<String>();
		defaultEnglishFilterList = new ArrayList<String>();
		
		cbFileFilterValue.add(UIContext.Constants.CustomFilterDisplayText());
		defaultFilterList.add(UIContext.Constants.CustomFilterDisplayText());
		defaultEnglishFilterList.add("");
		
		cbFileFilterValue.add(UIContext.Constants.AllFiles());
		defaultFilterList.add(UIContext.Constants.AllFiles());
		defaultEnglishFilterList.add("All Files(*;*.*)");
		
		cbFileFilterValue.add(UIContext.Constants.AudioFiles());
		defaultFilterList.add(UIContext.Constants.AudioFiles());
		defaultEnglishFilterList.add("Audio Files(*.wav;*.mp3;*.rm;*.ram;*.rma;*.wma;)");
		
		cbFileFilterValue.add(UIContext.Constants.ExecutableFiles());
		defaultFilterList.add(UIContext.Constants.ExecutableFiles());
		defaultEnglishFilterList.add("Executable Files(*.exe;*.com;*.sys;*.dll;*.ocx;*.386;*.vxd;*.cmd;*.vbs;*.js;*.jar;*.ps1;*.psc1;*.ps1xml;*.scr;*.bat)");
		
		cbFileFilterValue.add(UIContext.Constants.HelpFiles());
		defaultFilterList.add(UIContext.Constants.HelpFiles());
		defaultEnglishFilterList.add("Help Files(*.hlp;*.chm;)");
		
		cbFileFilterValue.add(UIContext.Constants.HyperVFiles());
		defaultFilterList.add(UIContext.Constants.HyperVFiles());
		defaultEnglishFilterList.add("Hyper-V Files(*.vhd;*.avhd;*.vsv;)");
		
		cbFileFilterValue.add(UIContext.Constants.ImageFiles());
		defaultFilterList.add(UIContext.Constants.ImageFiles());
		defaultEnglishFilterList.add("Image Files(*.jpg;*.jpeg;*.bmp;*.gif;*.png;*.tiff;*.tif;*.mdi;*.eml;*.jfif)");
		
		cbFileFilterValue.add(UIContext.Constants.InternetFiles());
		defaultFilterList.add(UIContext.Constants.InternetFiles());
		defaultEnglishFilterList.add("Internet Files(*.css;*.dlm;*.323;*.htm;*.html;)");
		
		cbFileFilterValue.add(UIContext.Constants.OfficeFiles());
		defaultFilterList.add(UIContext.Constants.OfficeFiles());
		defaultEnglishFilterList.add("Office Files(*.txt;*.rtf;*.doc;*.xls;*.ppt;*.pps;*.docx;*.xlsx;*.pptx;*.ppsx;*.mdb;*.mht;*.mpp;*.mpw;*.mpx;*.msg;*.pdf;*.vsd;*.xps)");
		
		cbFileFilterValue.add(UIContext.Constants.SQLFiles());
		defaultFilterList.add(UIContext.Constants.SQLFiles());
		defaultEnglishFilterList.add("SQL Files(*.sdf;*.sql;*.sqlce;*.bcp;*.dri;*.ftx;*.idx;*.ldf;*.mdx;*.ndf;*.prc;*.pre;*.rdl;*.rll;*.sch;*.tdf;*.trg;*.trn;*.wrk;*.xmla;*.xpp;*.msf;)");
		
		cbFileFilterValue.add(UIContext.Constants.TempFiles());
		defaultFilterList.add(UIContext.Constants.TempFiles());
		defaultEnglishFilterList.add("Temp Files(*.tmp;*.temp;)");
		
		cbFileFilterValue.add(UIContext.Constants.VideoFiles());
		defaultFilterList.add(UIContext.Constants.VideoFiles());
		defaultEnglishFilterList.add("Video Files(*.avi;*.mpg;*.rmvb;*.rm;*.wmv;*.wm;*.wmx;*.swf;*.mp4;*.asf;*.asx;)");
		
		cbFileFilterValue.add(UIContext.Constants.VMWareFiles());
		defaultFilterList.add(UIContext.Constants.VMWareFiles());
		defaultEnglishFilterList.add("VMware Files(*.vmxa;*.vmac;*.vmba;*.vmt;*.vmtm;*.vmx;*vmhf;*.vmhr;*.vmsn;*.sv2i;*.vmdk;*.vmc;*.nvram;*.vmem;*.lck;*.vmss;)");
		
		cbFileFilterValue.add(UIContext.Constants.ZipFiles());
		defaultFilterList.add(UIContext.Constants.ZipFiles());
		defaultEnglishFilterList.add("Zip Files(*.bz;*.bz2;*.gz;*.cab;*.img;*.iso;*.lzh;*.rar;*.taz;*.tbz;*.tbz2;*.tgz;*.tz;*.zip;)");
		return;
	}

	private void defineArchiveFileSizeCriteriaSection()
	{
		lcFileSizeContainer = new LayoutContainer();
		TableLayout tlFileSizeContainer = new TableLayout(1);
		tlFileSizeContainer.setCellSpacing(3);
		lcFileSizeContainer.setLayout(tlFileSizeContainer);
		lcFileSizeContainer.setWidth(MAX_WIDTH);
		lcFileSizeContainer.setHeight(73);
		lcFileSizeContainer.setStyleAttribute("margin", "2px, 2px, 2px, 10px");
		
		TableData tdArchiveFileSizeSection = new TableData();
		tdArchiveFileSizeSection.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		lblFileSizeLabel = new LabelField(UIContext.Constants.ArchiveFileSizeFiltersLabel());
		lblFileSizeLabel.setStyleName("restoreWizardSubItem");
		lblFileSizeLabel.setWidth(MAX_WIDTH);
		lcArchiveSettingsContainer.add(lblFileSizeLabel,tdArchiveFileSizeSection);
		
		lblFileSizeDescription = new LabelField(UIContext.Constants.ArchiveFileSizeFiltersDescription());
		lblFileSizeDescription.setWidth(MAX_WIDTH - MAX_WIDTH_LESS_LINE);
		lcArchiveSettingsContainer.add(lblFileSizeDescription,tdArchiveFileSizeSection);
		
		cbFilterBySize = new CheckBox();
		cbFilterBySize.ensureDebugId("847C629B-8DDF-469b-B0FF-DC704B66BAE2");
		cbFilterBySize.setBoxLabel(UIContext.Constants.ArchiveFilterFileSizeLabel());
//		cbFilterBySize.setStyleName("x-form-field");
		cbFilterBySize.addListener(Events.Change, ArchiveSettingsWindowListener);
//		cbFilterBySize.setWidth(180);
		lcFileSizeContainer.add(cbFilterBySize);
		
		lcFileSizeSettingsContainer = new LayoutContainer();
		TableLayout tlFileSizeSettingsContainer = new TableLayout(6);
		tlFileSizeSettingsContainer.setCellSpacing(10);
		lcFileSizeSettingsContainer.setLayout(tlFileSizeSettingsContainer);
//		lcFileSizeSettingsContainer.setWidth(MAX_WIDTH - 160);
		
		cbFilterBySizeOperator = new BaseSimpleComboBox<String>();
		cbFilterBySizeOperator.ensureDebugId("0D9976A1-9862-4a49-8166-20A9A071D0E1");
		cbFilterBySizeOperator.setEditable(false);
//		cbFilterBySizeOperator.setWidth(80);
		//cbFilterBySizeOperator.setMaxHeight(2);
		cbFilterBySizeOperator.setEnabled(false);
		cbFilterBySizeOperator.add(UIContext.Constants.ArchiveFileSizeOperatorlessthan());
		cbFilterBySizeOperator.add(UIContext.Constants.ArchiveFileSizeOperatorgreaterthan());
		cbFilterBySizeOperator.add(UIContext.Constants.ArchiveFileSizeOperatorbetween());
		cbFilterBySizeOperator.setAllowBlank(false);
		cbFilterBySizeOperator.addListener(Events.Select, ArchiveSettingsWindowListener);
		
		lcFileSizeSettingsContainer.add(cbFilterBySizeOperator);
		
		nfLowerSize = new NumberField();
		nfLowerSize.ensureDebugId("4F94BD23-2177-484d-846A-2D3620E84076");
		nfLowerSize.setEnabled(false);
		nfLowerSize.setWidth(60);
		nfLowerSize.setAllowBlank(false);
		nfLowerSize.setAllowNegative(false);
		nfLowerSize.setAllowDecimals(false);
		nfLowerSize.setStyleAttribute("margin-left", "10px");
		nfLowerSize.addListener(Events.Blur, archiveFieldsListener);
		lcFileSizeSettingsContainer.add(nfLowerSize);
		
		cbLowerSizeType = new BaseSimpleComboBox<String>();
		cbLowerSizeType.ensureDebugId("FE42BA27-AA54-43d4-884F-6FDEB787B5CE");
		cbLowerSizeType.setEditable(false);
		cbLowerSizeType.setWidth(60);
//		cbLowerSizeType.setMaxHeight(2);
		cbLowerSizeType.setEnabled(false);
		cbLowerSizeType.setStyleAttribute("margin-left", "10px");
		cbLowerSizeType.add(UIContext.Constants.KB());
		cbLowerSizeType.add(UIContext.Constants.MB());
		cbLowerSizeType.add(UIContext.Constants.GB());
		lcFileSizeSettingsContainer.add(cbLowerSizeType);
		
		cbLowerSizeType.setSimpleValue(UIContext.Constants.MB());
		
		lblAnd = new LabelField(UIContext.Constants.and());
		lblAnd.setWidth(40);
		lblAnd.setStyleAttribute("margin-left", "20px");
		lblAnd.setVisible(false);
		lcFileSizeSettingsContainer.add(lblAnd);
		
		nfHigherSize = new NumberField();
		nfHigherSize.ensureDebugId("48B47092-E1D7-4841-967F-28CFA89F3A83");
		nfHigherSize.setVisible(false);
		nfHigherSize.setWidth(60);
		nfHigherSize.setAllowBlank(false);
		nfHigherSize.setAllowNegative(false);
		nfHigherSize.setAllowDecimals(false);
		nfHigherSize.addListener(Events.Blur, archiveFieldsListener);
		lcFileSizeSettingsContainer.add(nfHigherSize);
		
		cbHigherSizeType = new BaseSimpleComboBox<String>();
		cbHigherSizeType.ensureDebugId("B1AFDD0C-A24E-45a7-9519-F687A5035BBE");
		cbHigherSizeType.setEditable(false);
		cbHigherSizeType.setWidth(60);
		cbHigherSizeType.setVisible(false);
		cbHigherSizeType.setStyleAttribute("margin-left", "10px");		
		cbHigherSizeType.add(UIContext.Constants.KB());
		cbHigherSizeType.add(UIContext.Constants.MB());
		cbHigherSizeType.add(UIContext.Constants.GB());
		cbHigherSizeType.setAllowBlank(false);
		lcFileSizeSettingsContainer.add(cbHigherSizeType);
	
		cbHigherSizeType.setSimpleValue(UIContext.Constants.MB());
		
        lcFileSizeSettingsContainer.setStyleAttribute("padding-left", "15px");
		lcFileSizeContainer.add(lcFileSizeSettingsContainer);
		
		lcArchiveSettingsContainer.add(lcFileSizeContainer);
		
		Html line = new Html("<HR>");
 		line.setWidth(MAX_WIDTH - MAX_WIDTH_LESS_LINE);
		lcArchiveSettingsContainer.add(line);
		
		return;
	}
	
	private void defineArchiveFileAgeCriteriaSection()
	{
		lcFileAgeContainer = new LayoutContainer();
		TableLayout tlFileAgeContainer = new TableLayout(3);
		tlFileAgeContainer.setCellSpacing(3);
		lcFileAgeContainer.setLayout(tlFileAgeContainer);
		lcFileAgeContainer.setWidth(MAX_WIDTH);
		lcFileAgeContainer.setStyleAttribute("margin", "2px, 2px, 2px, 10px");
		
		lblFileAgeLabel = new LabelField(UIContext.Constants.ArchiveFileAgeFiltersLabel());
		lblFileAgeLabel.setStyleName("restoreWizardSubItem");
		lblFileAgeLabel.setWidth(MAX_WIDTH);
		lcArchiveSettingsContainer.add(lblFileAgeLabel);
		
		lblFileAgeDescription = new LabelField(UIContext.Constants.ArchiveFileAgeFiltersDescription());
		lblFileAgeDescription.setWidth(MAX_WIDTH - MAX_WIDTH_LESS_LINE);
		lcArchiveSettingsContainer.add(lblFileAgeDescription);
		
		cbFileAccessed = new CheckBox();
		cbFileAccessed.ensureDebugId("41262C7A-1FEC-4f23-9A36-D167578C1E69");
		cbFileAccessed.setBoxLabel(UIContext.Constants.ArchiveFileAccessedFilterLabel());
//		cbFileAccessed.setStyleName("x-form-field");
		cbFileAccessed.addListener(Events.Change, ArchiveSettingsWindowListener);
//		cbFileAccessed.setWidth(150);
		lcFileAgeContainer.add(cbFileAccessed);
		
		nfAccessed = new NumberField();
		nfAccessed.ensureDebugId("838629F7-EED2-453b-9421-B723C4D16648");
		nfAccessed.setEnabled(false);
		nfAccessed.setWidth(50);
		nfAccessed.setAllowBlank(false);
		nfAccessed.setAllowNegative(false);
		nfAccessed.setAllowDecimals(false);
		nfAccessed.setStyleName("x-form-field");
		nfAccessed.addListener(Events.Blur, archiveFieldsListener);
		lcFileAgeContainer.add(nfAccessed);
		
		cbFileAccessedAgeType = new BaseSimpleComboBox<String>();
		cbFileAccessedAgeType.ensureDebugId("E4D69F82-988E-4f57-A86B-88B4832058EA");
		cbFileAccessedAgeType.setEditable(false);
		cbFileAccessedAgeType.setWidth(100);
//		cbFileAccessedAgeType.setMaxHeight(2);
		cbFileAccessedAgeType.setEnabled(false);
		cbFileAccessedAgeType.setStyleAttribute("margin-left", "15px");
		cbFileAccessedAgeType.add(UIContext.Constants.days());
		cbFileAccessedAgeType.add(UIContext.Constants.months());
		cbFileAccessedAgeType.add(UIContext.Constants.years());
		lcFileAgeContainer.add(cbFileAccessedAgeType);
		
		cbFileAccessedAgeType.setSimpleValue(UIContext.Constants.months());
		
		cbFileModified = new CheckBox();
		cbFileModified.ensureDebugId("1481A816-2FDC-48e1-B0D5-1783902708BE");
		cbFileModified.setBoxLabel(UIContext.Constants.ArchiveFileModifiedFilterLabel());
//		cbFileModified.setStyleName("x-form-field");
		cbFileModified.addListener(Events.Change, ArchiveSettingsWindowListener);
//		cbFileModified.setWidth(150);
		lcFileAgeContainer.add(cbFileModified);
		
		nfModified = new NumberField();
		nfModified.ensureDebugId("4AA4D6CA-0C9A-4a1a-8AE3-005F2960F67C");
		nfModified.setEnabled(false);
		nfModified.setWidth(50);
		nfModified.setAllowBlank(false);
		nfModified.setAllowNegative(false);
		nfModified.setStyleName("x-form-field");
		nfModified.addListener(Events.Blur, archiveFieldsListener);
		lcFileAgeContainer.add(nfModified);
		
		cbFileModifiedAgeType = new BaseSimpleComboBox<String>();
		cbFileModifiedAgeType.ensureDebugId("F35F22A2-3B11-42c1-8A8E-050AAB84C553");
		cbFileModifiedAgeType.setEditable(false);
		cbFileModifiedAgeType.setWidth(100);
//		cbFileModifiedAgeType.setMaxHeight(2);
		cbFileModifiedAgeType.setEnabled(false);
		cbFileModifiedAgeType.setStyleAttribute("margin-left", "15px");
		cbFileModifiedAgeType.add(UIContext.Constants.days());
		cbFileModifiedAgeType.add(UIContext.Constants.months());
		cbFileModifiedAgeType.add(UIContext.Constants.years());
		lcFileAgeContainer.add(cbFileModifiedAgeType);
		
		cbFileModifiedAgeType.setSimpleValue(UIContext.Constants.months());
		
		cbFileCreated = new CheckBox();
		cbFileCreated.ensureDebugId("A63E1DE1-0EE3-4134-80C9-D31BB184ED8F");
		cbFileCreated.setBoxLabel(UIContext.Constants.ArchiveFileCreatedFilterLabel());
//		cbFileCreated.setStyleName("x-form-field");
		cbFileCreated.addListener(Events.Change, ArchiveSettingsWindowListener);
//		cbFileCreated.setWidth(150);
		lcFileAgeContainer.add(cbFileCreated);
		
		nfCreated = new NumberField();
		nfCreated.ensureDebugId("5F150865-D6DF-42af-A5A8-7AAFACDEF47D");
		nfCreated.setEnabled(false);
		nfCreated.setWidth(50);
		nfCreated.setAllowBlank(false);
		nfCreated.setAllowNegative(false);
		nfCreated.setStyleName("x-form-field");
		nfCreated.addListener(Events.Blur, archiveFieldsListener);
		lcFileAgeContainer.add(nfCreated);
		
		cbFileCreatedAgeType = new BaseSimpleComboBox<String>();
		cbFileCreatedAgeType.ensureDebugId("97BD5A2D-7B2C-4ff0-8E17-68537DD826D8");
		cbFileCreatedAgeType.setEditable(false);
		cbFileCreatedAgeType.setWidth(100);
//		cbFileCreatedAgeType.setMaxHeight(2);
		cbFileCreatedAgeType.setEnabled(false);
		cbFileCreatedAgeType.setStyleAttribute("margin-left", "15px");
		cbFileCreatedAgeType.add(UIContext.Constants.days());
		cbFileCreatedAgeType.add(UIContext.Constants.months());
		cbFileCreatedAgeType.add(UIContext.Constants.years());
		lcFileAgeContainer.add(cbFileCreatedAgeType);
		
		cbFileCreatedAgeType.setSimpleValue(UIContext.Constants.months());
		
		lcArchiveSettingsContainer.add(lcFileAgeContainer);
		return;
	}

	private void defineArchiveSettingsButtons()
	{
		btOK = new Button();
		btOK.ensureDebugId("87274EAA-243A-4c28-A39F-51BEEED376F0");
		btOK.setText(UIContext.Constants.ok());
		btOK.setMinWidth(MIN_WIDTH);
		btOK.addListener(Events.Select, ArchiveSettingsWindowListener);
		this.addButton(btOK);
		
		btCancel = new Button();
		btCancel.ensureDebugId("FC734BBB-234E-4a27-A93B-8D1BFC814C9A");
		btCancel.setText(UIContext.Constants.cancel());
		btCancel.setMinWidth(MIN_WIDTH);
		btCancel.addListener(Events.Select, ArchiveSettingsWindowListener);
		this.addButton(btCancel);	

		btPreview = new Button();
		btPreview.ensureDebugId("FBCD407C-F6CF-4f23-B1A1-24FD940ED5DF");
		btPreview.setText(UIContext.Constants.ArchiveSettingsButtonPreview());
		btPreview.setMinWidth(MIN_WIDTH);
		btPreview.addListener(Events.Select, ArchiveSettingsWindowListener);
		//this.addButton(btPreview);
		
		btHelp = new Button();
		btHelp.ensureDebugId("A1486385-AF5E-480b-87B1-47061EB1906B");
		btHelp.setText(UIContext.Constants.help());
		btHelp.setMinWidth(MIN_WIDTH);
		btHelp.addListener(Events.Select, ArchiveSettingsWindowListener);
		this.addButton(btHelp);	
	}
	
	public class cDatePicker extends Window{
		private cDatePicker thisWindow;
		DatePicker dpDatePicker;
		
		private String strDateSelected;
		
		public cDatePicker(){
			thisWindow = this;
			
			thisWindow.setResizable(false);
			thisWindow.setHeadingHtml("Date Picker");
			thisWindow.setWidth(195);
			thisWindow.setHeight(260);
			
			dpDatePicker = new DatePicker();
			dpDatePicker.addListener(Events.Select, new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent be) {
					strDateSelected = dpDatePicker.getValue().toString();
				}
			});
			thisWindow.add(dpDatePicker);
			
			final Button btDatePickerOK = new Button();
			btDatePickerOK.ensureDebugId("63A6531F-374D-4d4d-BA42-CD281145F5CC");
			btDatePickerOK.setText(UIContext.Constants.ok());
			btDatePickerOK.setMinWidth(40);
			btDatePickerOK.addSelectionListener(new SelectionListener<ButtonEvent>(){

				@Override
				public void componentSelected(ButtonEvent ce) {
					
					Date dtSelectedDate = dpDatePicker.getValue();
					Date dtCurrentDate = new Date();
					
					if(dtSelectedDate.compareTo(dtCurrentDate) > 0)
					{
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.ERROR);
						msgError.setTitleHtml(strErrorMessageTitle);
						msgError.setModal(true);
						msgError.setMessage("Please select a Date earlier than current date");
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
						return;
					}
					
					strDateSelected = dpDatePicker.getValue().toString();
					thisWindow.hide(btDatePickerOK);
				}

				});		
			this.addButton(btDatePickerOK);
			
			
			final Button btDatePickerCancel = new Button();
			btDatePickerCancel.ensureDebugId("A7BEDDEE-A5A9-49d1-A461-6619B87AEF2E");
			btDatePickerCancel.setText(UIContext.Constants.cancel());
			btDatePickerCancel.setMinWidth(60);
			btDatePickerCancel.addSelectionListener(new SelectionListener<ButtonEvent>(){

				@Override
				public void componentSelected(ButtonEvent ce) {
					thisWindow.hide();
					
				}});		
			this.addButton(btDatePickerCancel);	
		}
		
		public String GetDateSelected()
		{
			return strDateSelected;
		}
	}
	
	boolean bValidated = true;
	private boolean ValidateAndSave(boolean bJustPackageInformation)
	{
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(strErrorMessageTitle);
		msgError.setModal(true);
		msgError.setMinWidth(400);
		msgError.getDialog().getButtonById(Dialog.OK).ensureDebugId("05F1CD28-DB8F-4e5e-9515-A23825C0E00A");
		
		strArchiveSource = strArchiveSource.trim();
		
		if((strArchiveSource == null) || (strArchiveSource.length() == 0))
		{
			String message = m_iSelectedPolicyType == POLICYTYPE_FILECOPY ? UIContext.Messages.SelectArchiveSourceErrorMessage()
					: UIContext.Messages.SelectCopyAndArchiveSourceErrorMessage();
			msgError.setMessage(message);
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			bValidated = false;
			return bValidated;
		}
		
		if(isRemote(strArchiveSource))
		{
			msgError.setMessage(UIContext.Messages.SelectLocalArchiveSourceErrorMessage());
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			bValidated = false;
			return bValidated;
		}
		
		if(!ValidateSourceAgainstBackupVolumes(strArchiveSource))
		{
			ArchiveSourceSelectionPanel.getDestinationTextField().setValue("");
			if(strArchiveSource != null && actualSourcePath != null && !strArchiveSource.equalsIgnoreCase(actualSourcePath))
			{
				msgError.setMessage(UIContext.Messages.ArchiveSymbolicSourceisNotBackupVolumeMessage(actualSourcePath,strArchiveSource,UIContext.productNameD2D));	
			}
			else
			{			
				msgError.setMessage(UIContext.Messages.ArchiveSourceisNotBackupVolumeMessage(UIContext.productNameD2D));			
			}
			msgError.show();
			return false;
		}
		
		if(!ValidateSourceAgainstBackupDestination(strArchiveSource))
		{
			ArchiveSourceSelectionPanel.getDestinationTextField().setValue("");
			msgError.setMessage(UIContext.Messages.ArchiveSourceCannotbeBackupDestination());
			msgError.show();
			return false;
		}
		
		if(ValidateSourceAgainstRefsVolumes(strArchiveSource))
		{
			ArchiveSourceSelectionPanel.getDestinationTextField().setValue("");
			msgError.setMessage(UIContext.Messages.ArchiveRefsSourceCannotbeBackupDestination());
			msgError.show();
			return false;
		}
		
		if(ValidateSourceAgainstDedupeVolumes(strArchiveSource))
		{
			ArchiveSourceSelectionPanel.getDestinationTextField().setValue("");
			msgError.setMessage(UIContext.Messages.ArchiveDedupeSourceCannotbeBackupDestination());
			msgError.show();
			return false;
		}
		
		if(ValidateFileSizeFilter() == false)
		{
			bValidated = false;
			return bValidated;
		}
		
		if(ValidateFileAgeFilters() == false)
		{
			bValidated = false;
			return bValidated;
		}
		
		if((strArchiveSource != null) ||(strArchiveSource.length() != 0))
		{
			boolean bFilterAndCriteriaAdded = true;
			
			switch(m_iSelectedPolicyType)
			{
				case POLICYTYPE_FILECOPY:
					if(gridStore.getCount() == 0)
						bFilterAndCriteriaAdded = false;
					break;
				case POLICYTYPE_ARCHIVE:
					if((gridStore.getCount() == 0) && !(cbFilterBySize.getValue()) && !(cbFileAccessed.getValue()) && !(cbFileModified.getValue()) && !(cbFileCreated.getValue()))
						bFilterAndCriteriaAdded = false;
					break;
			}
			
			if(!bFilterAndCriteriaAdded)
			{
				if(!bJustPackageInformation)
				{
					MessageBox msgBox = new MessageBox();
					msgBox.setButtons(MessageBox.YESNO);
					msgBox.addCallback(new Listener<MessageBoxEvent>() {

						@Override
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId().equals(com.extjs.gxt.ui.client.widget.Dialog.YES)) {
								Save(false);
							}
						}
					});
					msgBox.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
					msgBox.setMessage(UIContext.Messages.NoPoliciesSelectedWarning());
					msgBox.setIcon(MessageBox.INFO);
					msgBox.setModal(true);
					msgBox.setMinWidth(600);
					msgBox.getDialog().getButtonById(Dialog.YES).ensureDebugId("F4421A65-BEB1-4b75-ADA5-7D14537A9972");
					msgBox.getDialog().getButtonById(Dialog.NO).ensureDebugId("8F57FE27-B86D-418c-89FD-1758F133CAA9");
					msgBox.show();
				}
			}
			else
			{
				Save(bJustPackageInformation);
			}
		}
		else
		{
			Save(bJustPackageInformation);
		}
		return bValidated;
	}
	
	
	String actualSourcePath =  "";	
	private boolean validateAndSave(String in_ArchiveSource,final boolean bJustValidate)
	{
		actualSourcePath = in_ArchiveSource;
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(strErrorMessageTitle);
		msgError.setModal(true);
		msgError.setMinWidth(400);
		msgError.getDialog().getButtonById(Dialog.OK).ensureDebugId("7598E0DB-556F-4bc7-982D-8B831CDD329F");
		
		if((in_ArchiveSource == null) || (in_ArchiveSource.length() == 0))
		{
			String message = m_iSelectedPolicyType == POLICYTYPE_FILECOPY ? UIContext.Messages.SelectArchiveSourceErrorMessage()
					: UIContext.Messages.SelectCopyAndArchiveSourceErrorMessage();
			msgError.setMessage(message);
			msgError.show();
			bValidated = false;
			return bValidated;
		}
		
        commonService.getSymbolicLinkActualPath(in_ArchiveSource, new AsyncCallback<String>() {			
			@Override
			public void onSuccess(String result) {				
				strArchiveSource = result;
				ValidateArchiveSourceAndSave(result,false);
				if(actualSourcePath.equalsIgnoreCase(strArchiveSource))
					isPathChanged = false;
				else
					isPathChanged = true;				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setMinWidth(400);
				msgError.setTitleHtml(strErrorMessageTitle);
				msgError.setModal(true);				
				String strMessage = UIContext.Constants.ValidatingArchiveActualSourcePathMessage();
				msgError.setMessage(strMessage);
				msgError.show();
				return;
			}
		});
        return false;
	}
	
	private boolean ValidateArchiveSourceAndSave(String in_ArchiveSource,final boolean bJustValidate) {

		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(strErrorMessageTitle);
		msgError.setModal(true);
		msgError.setMinWidth(400);
		
		if((in_ArchiveSource == null) || (in_ArchiveSource.length() == 0))
		{
			String message = m_iSelectedPolicyType == POLICYTYPE_FILECOPY ? UIContext.Messages.SelectArchiveSourceErrorMessage()
					: UIContext.Messages.SelectCopyAndArchiveSourceErrorMessage();
			msgError.setMessage(message);
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			bValidated = false;
			return bValidated;
		}
		
		/*if(!PathSelectionPanel.isLocalPathValid(in_ArchiveSource)){
			msgError.setMessage(UIContext.Messages.SelectArchiveSourceErrorMessage());
			msgError.show();
			return false;
		}	*/
		
		if(isRemote(in_ArchiveSource))
		{
			ArchiveSourceSelectionPanel.getDestinationTextField().setValue("");
			msgError.setMessage(UIContext.Messages.ArchiveSourceisNotRemote(in_ArchiveSource));
			msgError.show();
			return false;
			
		}	

		if (UIContext.isLaunchedForEdgePolicy)
		{//For Edge Policy , it also needs those check above  
			if(!bJustValidate)
			{
				if(!validatePolicySource(in_ArchiveSource))
				{	
					msgError.setMessage(UIContext.Messages.ArchiveSourceisNotBackupVolumeMessage(UIContext.productNameD2D));
					msgError.show();
					return false;
				}
				else
				{
					ValidateAndSave(false);
				}	
			}	
			return false;
		}
		
		ArchiveDiskDestInfoModel sourceInfo = new ArchiveDiskDestInfoModel();
		sourceInfo.setArchiveDiskDestPath(in_ArchiveSource);
		
		commonService.ValidateArchiveSource(sourceInfo, new AsyncCallback<Long>() {
			
			@Override
			public void onSuccess(Long result) {
				if(result == 0)
				{
					if(!bJustValidate)
						ValidateAndSave(false);
					return;
				}
				else
				{
					MessageBox msgError = new MessageBox();
					msgError.setIcon(MessageBox.ERROR);
					msgError.setTitleHtml(strErrorMessageTitle);
					msgError.setModal(true);
					msgError.setMinWidth(400);
					switch(Integer.parseInt(Long.toString(result)))
					{
					case 1:
						//msgError.setMessage(UIContext.Constants.SourceNotExistMessage());
						msgError.setMessage(UIContext.Messages.SourcePathNotExistMessage(strArchiveSource));
						break;
					case 2:
						msgError.setMessage(UIContext.Constants.SourceCannotBeFileMessage());
						break;
					}
					Utils.setMessageBoxDebugId(msgError);
					msgError.show();
					return;
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setMinWidth(400);
				msgError.setTitleHtml(strErrorMessageTitle);
				msgError.setModal(true);
				msgError.getDialog().getButtonById(Dialog.OK).ensureDebugId("42258A30-4A31-48d1-ABD5-3219AF5B6EA8");
				String strMessage = UIContext.Constants.ValidatingArchiveSourceMessage()+" " + caught.getMessage();
				msgError.setMessage(strMessage);
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				return;
			}
		});
		return false;
	}

	// Javascript method
	public native boolean validatePolicySource(String path) /*-{
	    var filePathPattern = /^(([a-zA-Z]:))(\\)?((\\([^\*\?\$|<>\"\:]*))?)+$/;
	    return filePathPattern.test(path);
	}-*/;

	
	
	private boolean ValidateSourceAgainstBackupDestination(String in_ArchiveSource) {
		if(backupDestination == null || backupDestination.length() == 0)
			return true;
		
		String backupDestVolume = backupDestination.substring(0,backupDestination.indexOf(":")+1);
		
		int iIndex = in_ArchiveSource.indexOf(":");
		String source = in_ArchiveSource;
		if(iIndex != -1)
		{
			source = in_ArchiveSource.substring(0, iIndex + 1);
			
			if(source.compareToIgnoreCase(backupDestVolume) == 0)
			{
				//logger.info("backup destination matched with volume name");
				return false;
			}	
		}
		return true;
	}
	
	public boolean validatingSource()
	{
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(strErrorMessageTitle);
		msgError.setModal(true);
		msgError.setMinWidth(400);
		msgError.getDialog().getButtonById(Dialog.OK).ensureDebugId("C9DEFC2B-9028-4e6e-90D9-1112DAE1390F");
		if(isRemote(strArchiveSource))
		{
			//ArchiveSourceSelectionPanel.getDestinationTextField().setValue("");
			msgError.setMessage(UIContext.Messages.ArchiveSourceisNotRemote(strArchiveSource));
			msgError.show();
			return false;
			
		}
			
		if(!ValidateSourceAgainstBackupVolumes(strArchiveSource))
		{
			//ArchiveSourceSelectionPanel.getDestinationTextField().setValue("");
			msgError.setMessage(UIContext.Messages.ArchiveSourceisNotBackupVolumeMessage(UIContext.productNameD2D));
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			return false;
		}
		
		if(!ValidateSourceAgainstBackupDestination(strArchiveSource))
		{
			//ArchiveSourceSelectionPanel.getDestinationTextField().setValue("");
			msgError.setMessage(UIContext.Messages.ArchiveSourceCannotbeBackupDestination());
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			return false;
		}
		
		return true;

	}
	
	
	private boolean ValidateSourceAgainstBackupVolumes(String in_ArchiveSource) {

		if(selectedBackupVolumes == null || selectedBackupVolumes.getIsFullMachine())
			return true;
		
		int iIndex = in_ArchiveSource.indexOf(":");
		String source = in_ArchiveSource;
		if(iIndex != -1)
		{
			source = in_ArchiveSource.substring(0, iIndex + 1);
			//source += "\\";
			//if(backupVolumes != null)
			
			for (String volume : selectedBackupVolumes.selectedVolumesList) {
				if(source.compareToIgnoreCase(volume) == 0)
				{
					//logger.info("backup destination matched with volume name");
					return true;
				}	
			}
		}
		return false;
	}
	
	
	
	private boolean ValidateSourceAgainstRefsVolumes(String in_ArchiveSource) {

		if(selectedBackupRefsVolumes == null)
			return false;
		
		int iIndex = in_ArchiveSource.indexOf(":");
		String source = in_ArchiveSource;
		if(iIndex != -1)
		{
			source = in_ArchiveSource.substring(0, iIndex + 1);
			//source += "\\";
			//if(backupVolumes != null)
			
			for (String volume : selectedBackupRefsVolumes.allRefsVolumesList) {
				if(source.compareToIgnoreCase(volume) == 0)
				{
					//logger.info("backup destination matched with volume name");
					return true;
				}	
			}
		}
		return false;
	}
	
	
	private boolean ValidateSourceAgainstDedupeVolumes(String in_ArchiveSource) {

		if(selectedBackupDedupeVolumes == null)
			return false;
		
		int iIndex = in_ArchiveSource.indexOf(":");
		String source = in_ArchiveSource;
		if(iIndex != -1)
		{
			source = in_ArchiveSource.substring(0, iIndex + 1);
			//source += "\\";
			//if(backupVolumes != null)
			
			for (String volume : selectedBackupDedupeVolumes.allDedupeVolumesList) {
				if(source.compareToIgnoreCase(volume) == 0)
				{
					//logger.info("backup destination matched with volume name");
					return true;
				}	
			}
		}
		return false;
	}

	private boolean isRemote(String inputFolder) {
		return inputFolder != null && inputFolder.startsWith("\\\\");
	}
	
	private void Save(boolean bJustPackageInformation)
	{
		if(strArchiveSource.length() == 0)
		{
			sourceInfoModel.setArchiveSourceConfigured(false);
			thisWindow.hide(btOK);
		}
		sourceInfoModel.setArchiveSourceConfigured(true);
		
		//saving archive source filters information
		int iarchiveSourceFiltersCount = gridStore.getCount();
		
		if(m_iSelectedPolicyType == POLICYTYPE_ARCHIVE)
		{
			if(cbFilterBySize.getValue()) iarchiveSourceFiltersCount++;
			if(cbFileAccessed.getValue()) iarchiveSourceFiltersCount++;
			if(cbFileModified.getValue()) iarchiveSourceFiltersCount++;
			if(cbFileCreated.getValue()) iarchiveSourceFiltersCount++;
		}				
		if(iarchiveSourceFiltersCount != 0)
		{
			ArchiveSourceFilterModel[] archivesourceFiltersList = new ArchiveSourceFilterModel[iarchiveSourceFiltersCount];
			int iarchiveFilterIndex = 0;
			for(iarchiveFilterIndex = 0;iarchiveFilterIndex < gridStore.getCount();iarchiveFilterIndex++)
			{
				ArchiveSourceFilterModel sourcefilterModel = gridStore.getAt(iarchiveFilterIndex);
				
				archivesourceFiltersList[iarchiveFilterIndex] = setArchiveSourceFiltersItem(sourcefilterModel.getFilterOrCriteriaType(),
						sourcefilterModel.getFilterOrCriteriaName(), "", false, sourcefilterModel.getIsDefaultFilter());
				archivesourceFiltersList[iarchiveFilterIndex].setFilterOrCriteriaLowerValue(sourcefilterModel.getFilterOrCriteriaLowerValue());
				archivesourceFiltersList[iarchiveFilterIndex].setLocFilterOrCriteriaLowerValue(sourcefilterModel.getLocFilterOrCriteriaLowerValue());													
			}

			if(m_iSelectedPolicyType == POLICYTYPE_ARCHIVE)
			{
				if(cbFileAccessed.getValue()) 
				{					
					archivesourceFiltersList[iarchiveFilterIndex] = setArchiveSourceFiltersItem(ArchiveConstantsModel.FILTER_TYPE_INCLUDE_STRING,
							"AccessTime", "0", true, false);
										
					String strCriteriaValue = "";							
					String[] stringKeys = new String[3];
					stringKeys[0] = "0";
					stringKeys[1] = "0";
					stringKeys[2] = "0";
					stringKeys[cbFileAccessedAgeType.getSelectedIndex()] = Integer.toString(nfAccessed.getValue().intValue());
					strCriteriaValue = stringKeys[1] + "\\" + stringKeys[0] + "\\" + stringKeys[2]; 
					archivesourceFiltersList[iarchiveFilterIndex].setFilterOrCriteriaLowerValue(strCriteriaValue);
					iarchiveFilterIndex++;
				}
				
				if(cbFileModified.getValue()) 
				{					
					archivesourceFiltersList[iarchiveFilterIndex] = setArchiveSourceFiltersItem(ArchiveConstantsModel.FILTER_TYPE_INCLUDE_STRING,
							"ModifiedTime", "0", true, false);
					
					String strCriteriaValue = "";							
					String[] stringKeys = new String[3];
					stringKeys[0] = "0";
					stringKeys[1] = "0";
					stringKeys[2] = "0";
					stringKeys[cbFileModifiedAgeType.getSelectedIndex()] = Integer.toString(nfModified.getValue().intValue());
					strCriteriaValue = stringKeys[1] + "\\" + stringKeys[0] + "\\" + stringKeys[2];
					archivesourceFiltersList[iarchiveFilterIndex].setFilterOrCriteriaLowerValue(strCriteriaValue);
					iarchiveFilterIndex++;
				}
				
				if(cbFileCreated.getValue()) 
				{					
					archivesourceFiltersList[iarchiveFilterIndex] = setArchiveSourceFiltersItem(ArchiveConstantsModel.FILTER_TYPE_INCLUDE_STRING,
							"CreatedTime", "0", true, false);
										
					String strCriteriaValue = "";							
					String[] stringKeys = new String[3];
					stringKeys[0] = "0";
					stringKeys[1] = "0";
					stringKeys[2] = "0";
					stringKeys[cbFileCreatedAgeType.getSelectedIndex()] = Integer.toString(nfCreated.getValue().intValue());
					strCriteriaValue = stringKeys[1] + "\\" + stringKeys[0] + "\\" + stringKeys[2];
					archivesourceFiltersList[iarchiveFilterIndex].setFilterOrCriteriaLowerValue(strCriteriaValue);
					iarchiveFilterIndex++;
				}
				
				if(cbFilterBySize.getValue()) 
				{					
					archivesourceFiltersList[iarchiveFilterIndex] = setArchiveSourceFiltersItem(ArchiveConstantsModel.FILTER_TYPE_INCLUDE_STRING,
							"FileSize", Integer.toString(cbFilterBySizeOperator.getSelectedIndex()), true, false);
					
					String strCriteriaLowerValue = "";							
					String[] stringLowerValueKeys = new String[3];
					stringLowerValueKeys[0] = "0";
					stringLowerValueKeys[1] = "0";
					stringLowerValueKeys[2] = "0";
					stringLowerValueKeys[cbLowerSizeType.getSelectedIndex()] = Integer.toString(nfLowerSize.getValue().intValue());
					strCriteriaLowerValue = stringLowerValueKeys[0] + "\\" + stringLowerValueKeys[1] + "\\" + stringLowerValueKeys[2];
					archivesourceFiltersList[iarchiveFilterIndex].setFilterOrCriteriaLowerValue(strCriteriaLowerValue);
					
					if(cbFilterBySizeOperator.getSelectedIndex() == ArchiveConstantsModel.OPERATOR_BETWEEN)
					{
						String strCriteriaHigherValue = "";							
						String[] stringHigherValueKeys = new String[3];
						stringHigherValueKeys[0] = "0";
						stringHigherValueKeys[1] = "0";
						stringHigherValueKeys[2] = "0";
						stringHigherValueKeys[cbHigherSizeType.getSelectedIndex()] = Integer.toString(nfHigherSize.getValue().intValue());
						strCriteriaHigherValue = stringHigherValueKeys[0] + "\\" + stringHigherValueKeys[1] + "\\" + stringHigherValueKeys[2];
						archivesourceFiltersList[iarchiveFilterIndex].setFilterOrCriteriaHigherValue(strCriteriaHigherValue);
					}
				}
			}
		
			sourceInfoModel.setArchiveSourceFilters(archivesourceFiltersList);
		}
		else
			sourceInfoModel.setArchiveSourceFilters(null);
		
		sourceInfoModel.setSourcePath(strArchiveSource);
		sourceInfoModel.setDisplaySourcePath(actualSourcePath);
		sourceInfoModel.setArchiveFiles(m_iSelectedPolicyType == POLICYTYPE_ARCHIVE ? true : false);
		sourceInfoModel.setCopyFiles(m_iSelectedPolicyType == POLICYTYPE_FILECOPY ? true : false);
		
		if(!bJustPackageInformation)
		{
			strButtonClicked = "OK";
			thisWindow.hide(btOK);
		}
	}
	
	private ArchiveSourceFilterModel setArchiveSourceFiltersItem(String filterOrCriteriaType, String FilterOrCriteriaName, String operator, boolean isCriteria, boolean isDefaultFilter) {
		// TODO Auto-generated method stub
		ArchiveSourceFilterModel asfm = new ArchiveSourceFilterModel();
		asfm.setFilterOrCriteriaType(filterOrCriteriaType);
		asfm.setFilterOrCriteriaName(FilterOrCriteriaName);
		asfm.setCriteriaOperator(operator);
		asfm.setIsCriteria(isCriteria);
		asfm.setIsDefaultFilter(isDefaultFilter);		
		return asfm;		
	}

	private boolean ValidateFilter()
	{
		boolean bValidated = true;
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(strErrorMessageTitle);
		msgError.setModal(true);
		msgError.getDialog().getButtonById(Dialog.OK).ensureDebugId("002D6FAF-B01C-470e-A775-133A15AF2BBF");
		if(cbFilter.getSimpleValue().length() == 0)
		{
			thisWindow.setFocusWidget(cbFilter);
			msgError.setMessage(UIContext.Messages.SelectValidFilterMessage());
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			bValidated = false;
		}
		
		if(cbFilterType.getSimpleValue().length() == 0)
		{
			thisWindow.setFocusWidget(cbFilterType);
			msgError.setMessage(UIContext.Messages.SelectValidFilterTypeMessage());
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			bValidated = false;
		}
		
		String strSelectedValue = null;
		switch(cbFilterType.getSelectedIndex())
		{
		case 0://file filter
		{
			//strSelectedValue = cbFileFilterValue.getSimpleValue();
			strSelectedValue = cbFileFilterValue.getRawValue() != null ? cbFileFilterValue.getRawValue().trim() : cbFileFilterValue.getRawValue();
			if(strSelectedValue.length() <= 0)
			{
				thisWindow.setFocusWidget(cbFileFilterValue);
				msgError.setMessage(UIContext.Messages.SelectValidFilterValueMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(strSelectedValue.contains(",") || strSelectedValue.contains("<") || strSelectedValue.contains(">") || strSelectedValue.contains("/")||
			   strSelectedValue.contains("\\") || strSelectedValue.contains(":") ||
			   strSelectedValue.contains("|") || strSelectedValue.contains("\""))
			{
				thisWindow.setFocusWidget(cbFileFilterValue);
				msgError.setMessage(UIContext.Messages.SelectValidFilterValueMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			String[] patterns = strSelectedValue.split(";");
			for(int i=0;i<patterns.length;i++)
			{
				if(patterns[i].length()> 256)
				{
					thisWindow.setFocusWidget(cbFileFilterValue);
					msgError.setMessage(UIContext.Messages.SelectValidFilterLenghtMessage());
					bValidated = false;
					msgError.show();
					break;
				}
			}
			break;
		}
		case 1://folder filter
			strSelectedValue = cbFolderFilterValue.getValue() != null ? cbFolderFilterValue.getValue().trim() : cbFolderFilterValue.getValue();
			if((strSelectedValue == null) || (strSelectedValue.length() <= 0))
			{
				thisWindow.setFocusWidget(cbFolderFilterValue);
				msgError.setMessage(UIContext.Messages.SelectValidFolderFilterValueMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			if(strSelectedValue.contains("<") || strSelectedValue.contains(">") || strSelectedValue.contains("/")||
					   strSelectedValue.contains("\\") || strSelectedValue.contains(":") ||
					   strSelectedValue.contains("|") || strSelectedValue.contains("\""))
					{
						thisWindow.setFocusWidget(cbFileFilterValue);
						msgError.setMessage(UIContext.Messages.SelectValidFolderFilterValueMessage());
						msgError.show();
						bValidated = false;
						return bValidated;
					}
			String[] patterns = strSelectedValue.split(";");
			for(int i=0;i<patterns.length;i++)
			{
				if(patterns[i].length()> 256)
				{
					thisWindow.setFocusWidget(cbFolderFilterValue);
					msgError.setMessage(UIContext.Messages.SelectValidFilterLenghtMessage());
					bValidated = false;
					msgError.show();
					break;
				}
			}
			break;
		}
		
		
		return bValidated;
	}
	
	private boolean ValidateFileSizeFilter()
	{
		boolean bValidated = true;
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(strErrorMessageTitle);
		msgError.setModal(true);
		msgError.getDialog().getButtonById(Dialog.OK).ensureDebugId("50A35AC3-45B6-41c0-B794-431CA02F6E6E");
		
		boolean bArchiveEnabled = m_iSelectedPolicyType == 1 ? false : true;
		
		if(!bArchiveEnabled)
			return bValidated;
		
		boolean bFilterBySizeEnabled = cbFilterBySize.getValue() != null ? cbFilterBySize.getValue() : false;
		
		if(bFilterBySizeEnabled)
		{
			if(cbFilterBySizeOperator.getSelectedIndex() == -1)
			{
				msgError.setMessage(UIContext.Messages.SelectValidFileSizeMessage());
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if((nfLowerSize.getValue() == null) || (nfLowerSize.getValue().intValue() == 0))
			{
				msgError.setMessage(UIContext.Messages.SelectValidFileSizeLowerMessage());
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				bValidated = false;
				return bValidated;
			}

			if(cbLowerSizeType.getSelectedIndex() == -1)
			{
				msgError.setMessage(UIContext.Messages.SelectValidFileSizeLowerUnitMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(cbFilterBySizeOperator.getSelectedIndex() == ArchiveConstantsModel.OPERATOR_BETWEEN)//between operator
			{
				if((nfHigherSize.getValue() == null) || (nfHigherSize.getValue().intValue() == 0))
				{
					msgError.setMessage(UIContext.Messages.SelectValidFileSizeHigherMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}

				if(cbHigherSizeType.getSelectedIndex() == -1)
				{
					msgError.setMessage(UIContext.Messages.SelectValidFileSizeHigherUnitMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
				if(nfLowerSize.getValue() != null && nfHigherSize.getValue() != null)
				{
					long lowerSize = nfLowerSize.getValue().longValue();					
					long higherSize = nfHigherSize.getValue().longValue();
					
					// Convert both lower and higher selections to KB i.e. MB and GB units are converted to KB
					
					if(cbLowerSizeType.getSelectedIndex() == 1 )
					{
						lowerSize = lowerSize*1024;
					}
					if(cbLowerSizeType.getSelectedIndex() == 2 )
					{
						lowerSize = lowerSize*1024*1024;
					}

					if(cbHigherSizeType.getSelectedIndex() == 1)
					{
						higherSize = higherSize*1024;
					}
					if(cbHigherSizeType.getSelectedIndex() == 2)
					{
						higherSize = higherSize*1024*1024;
					}

					if(lowerSize >= higherSize)
					{
						msgError.setMessage(UIContext.Messages.SelectValidFileSizeLowerAndHigher());
						msgError.show();
						bValidated = false;
						return bValidated;
					}
				}
			}
		}
		return bValidated;
	}
	
	private boolean ValidateFileAgeFilters() 
	{
		boolean bValidated = true;
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(strErrorMessageTitle);
		msgError.setModal(true);
		msgError.getDialog().getButtonById(Dialog.OK).ensureDebugId("70D45AF0-3770-4531-A862-967F2EFDEFA0");
		
		boolean bFilterByAgeEnabled = m_iSelectedPolicyType == 1 ? false : true;
		
		if(!bFilterByAgeEnabled)
			return bValidated;
		
		boolean bAccessTimeFilterEnabled = cbFileAccessed.getValue() != null ? cbFileAccessed.getValue() : false;
		
		if(bAccessTimeFilterEnabled)
		{
			if((nfAccessed.getValue() == null) || (nfAccessed.getValue().intValue() == 0))
			{
				msgError.setMessage(UIContext.Messages.SelectValidFileAccessTimeMessage());
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(cbFileAccessedAgeType.getSelectedIndex() == -1)
			{
				msgError.setMessage(UIContext.Messages.SelectValidFileAccessTypeMessage());
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				bValidated = false;
				return bValidated;
			}
		}
		
		boolean bModifiedTimeFilterEnabled = cbFileModified.getValue() != null ? cbFileModified.getValue() : false;
		
		if(bModifiedTimeFilterEnabled)
		{
			if((nfModified.getValue() == null) || (nfModified.getValue().intValue() == 0))
			{
				msgError.setMessage(UIContext.Messages.SelectValidFileModifiedTimeMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(cbFileModifiedAgeType.getSelectedIndex() == -1)
			{
				msgError.setMessage(UIContext.Messages.SelectValidFileModifiedTypeMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
		}
		
		boolean bCreationTimeFilterEnabled = cbFileCreated.getValue() != null ? cbFileCreated.getValue() : false;
		
		if(bCreationTimeFilterEnabled)
		{
			if((nfCreated.getValue() == null) || (nfCreated.getValue().intValue() == 0))
			{
				msgError.setMessage(UIContext.Messages.SelectValidFileCreationTimeMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(cbFileCreatedAgeType.getSelectedIndex() == -1)
			{
				msgError.setMessage(UIContext.Messages.SelectValidFileCreationTypeMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
		}
		
		return bValidated;
	}
	
	private void refreshFilterView()
	{
		if(sourceInfoModel == null)
			return;

		ArchiveSourceSelectionPanel.getDestinationTextField().setValue(sourceInfoModel.getSourcePath());
		strArchiveSource = sourceInfoModel.getSourcePath();
	//	rbArchive.setValue(sourceInfoModel.getArchiveFiles());
	//	rbFilecopy.setValue(sourceInfoModel.getCopyFiles());

		if(sourceInfoModel.getArchiveSourceFilters() != null)
		{
		
			ArchiveSourceFilterModel[] filters = null;
			filters = GetFiltersFromList(sourceInfoModel.getArchiveSourceFilters());
			
			if(filters != null)
			{
				for (ArchiveSourceFilterModel archiveSourceFilterModel : filters) 
				{
					if(!filtersMap.contains(archiveSourceFilterModel.getFilterOrCriteriaType()+archiveSourceFilterModel.getFilterOrCriteriaName()+archiveSourceFilterModel.getLocFilterOrCriteriaLowerValue()))
					{
						String strFilterType = GetLocalizedFilterTypeByIndex(archiveSourceFilterModel.getFilterOrCriteriaName());
						filtersMap.add(archiveSourceFilterModel.getFilterOrCriteriaType()+strFilterType+archiveSourceFilterModel.getLocFilterOrCriteriaLowerValue());
						
						if(cbFileFilterValue.findModel(archiveSourceFilterModel.getLocFilterOrCriteriaLowerValue()) == null)
							archiveSourceFilterModel.setIsDefaultFilter(false);
						else
							archiveSourceFilterModel.setIsDefaultFilter(true);
						
						gridStore.add(archiveSourceFilterModel);
					}
					//gridStore.add(archiveSourceFilterModel);
				}
				ArchiveFiltersGrid.reconfigure(gridStore, ArchiveFilterColumnsModel);
			}
			
			if(m_iSelectedPolicyType == POLICYTYPE_ARCHIVE)
			{
				cbFilterBySize.setValue(false);
				cbFileAccessed.setValue(false);
				cbFileModified.setValue(false);
				cbFileCreated.setValue(false);
			}
			
			ArchiveSourceFilterModel[]  criteria = getArchiveSourceCriteria(sourceInfoModel.getArchiveSourceFilters());
			
			if(criteria != null)
			{
				for (ArchiveSourceFilterModel archiveSourceCriteriaModel : criteria) 
				{
					if(archiveSourceCriteriaModel.getFilterOrCriteriaName().compareToIgnoreCase("FileSize") == 0)
					{
						cbFilterBySize.setValue(true);
						cbFilterBySizeOperator.setSimpleValue(GetLocalizedFileSizeOperatorByIndex(archiveSourceCriteriaModel.getCriteriaOperator()));
						
						String strFileLowerSize = archiveSourceCriteriaModel.getFilterOrCriteriaLowerValue();
						String[] strFileLowerSizes = strFileLowerSize.split("\\\\");
						int iFileLowerSize = -1;
						for(int iSizeIndex = 0;iSizeIndex<strFileLowerSizes.length;iSizeIndex++)
						{
							if(Integer.parseInt(strFileLowerSizes[iSizeIndex]) != 0)
							{
								iFileLowerSize = Integer.parseInt(strFileLowerSizes[iSizeIndex]);
								break;
							}
						}
												
						nfLowerSize.setValue(iFileLowerSize);
						cbLowerSizeType.setSimpleValue(GetCriteraiValueType(strFileLowerSize,archiveSourceCriteriaModel.getFilterOrCriteriaName()));
						
						if(archiveSourceCriteriaModel.getCriteriaOperator().compareToIgnoreCase(ArchiveConstantsModel.OPERATOR_BETWEEN_STRING) == 0)
						{
							String strFileHigherSize = archiveSourceCriteriaModel.getFilterOrCriteriaHigherValue();
							String[] strHigherSizes = strFileHigherSize.split("\\\\");
							int iFileHigherSize = -1;
							for(int iSizeIndex = 0;iSizeIndex<strHigherSizes.length;iSizeIndex++)
							{
								if(Integer.parseInt(strHigherSizes[iSizeIndex]) != 0)
								{
									iFileHigherSize = Integer.parseInt(strHigherSizes[iSizeIndex]);
									break;
								}
							}
							nfHigherSize.setVisible(true);
							nfHigherSize.setValue(iFileHigherSize);
							cbHigherSizeType.setVisible(true);
							cbHigherSizeType.setSimpleValue(GetCriteraiValueType(strFileHigherSize,archiveSourceCriteriaModel.getFilterOrCriteriaName()));
							lblAnd.setVisible(true);
						}
					}
					else if(archiveSourceCriteriaModel.getFilterOrCriteriaName().compareToIgnoreCase("AccessTime") == 0)
					{
						cbFileAccessed.setValue(true);
						String strAccessTime = archiveSourceCriteriaModel.getFilterOrCriteriaLowerValue();
						String[] strAccessTimeKeys = strAccessTime.split("\\\\");
						int iAccessedTime = -1;
						for(int iSizeIndex = 0;iSizeIndex<strAccessTimeKeys.length;iSizeIndex++)
						{
							if(Integer.parseInt(strAccessTimeKeys[iSizeIndex]) != 0)
							{
								iAccessedTime = Integer.parseInt(strAccessTimeKeys[iSizeIndex]);
								break;
							}
						}
						nfAccessed.setValue(iAccessedTime);
						cbFileAccessedAgeType.setSimpleValue(GetCriteraiValueType(strAccessTime,archiveSourceCriteriaModel.getFilterOrCriteriaName()));
					}
					else if(archiveSourceCriteriaModel.getFilterOrCriteriaName().compareToIgnoreCase("ModifiedTime") == 0)
					{
						cbFileModified.setValue(true);
						String strModifiedTime = archiveSourceCriteriaModel.getFilterOrCriteriaLowerValue();
						String[] strModifiedTimeKeys = strModifiedTime.split("\\\\");
						int iModifiedTime = -1;
						for(int iSizeIndex = 0;iSizeIndex<strModifiedTimeKeys.length;iSizeIndex++)
						{
							if(Integer.parseInt(strModifiedTimeKeys[iSizeIndex]) != 0)
							{
								iModifiedTime = Integer.parseInt(strModifiedTimeKeys[iSizeIndex]);
								break;
							}
						}
						nfModified.setValue(iModifiedTime);
						cbFileModifiedAgeType.setSimpleValue(GetCriteraiValueType(strModifiedTime,archiveSourceCriteriaModel.getFilterOrCriteriaName()));
					}
					else if(archiveSourceCriteriaModel.getFilterOrCriteriaName().compareToIgnoreCase("CreatedTime") == 0)
					{
						cbFileCreated.setValue(true);
						String strCreationTime = archiveSourceCriteriaModel.getFilterOrCriteriaLowerValue();
						String[] strCreationTimeKeys = strCreationTime.split("\\\\");
						int iCreationTime = -1;
						for(int iSizeIndex = 0;iSizeIndex<strCreationTimeKeys.length;iSizeIndex++)
						{
							if(Integer.parseInt(strCreationTimeKeys[iSizeIndex]) != 0)
							{
								iCreationTime = Integer.parseInt(strCreationTimeKeys[iSizeIndex]);
								break;
							}
						}
						nfCreated.setValue(iCreationTime);
						cbFileCreatedAgeType.setSimpleValue(GetCriteraiValueType(strCreationTime,archiveSourceCriteriaModel.getFilterOrCriteriaName()));
					}
				}
			}
		}
		
		return;
	}
	
	private String GetLocalizedFilterTypeByIndex(String iFilterTypeIndex) {
		
		switch(Integer.parseInt(iFilterTypeIndex))
		{
		case 0:
			return UIContext.Constants.ArchiveFilePattern();
		case 1:
			return UIContext.Constants.ArchiveFolderPattern();
		}
		return UIContext.Constants.ArchiveFilePattern();
	}
	
	private String GetLocalizedFilterByIndex(String in_iFilter) {
		switch(Integer.parseInt(in_iFilter))
		{
		case 0:
			return UIContext.Constants.ArchiveFilterInclude();
		case 1:
			return UIContext.Constants.ArchiveFilterExclude();
		}
		return UIContext.Constants.ArchiveFilterInclude();
	}
	
	private String GetLocalizedFileSizeOperatorByIndex(String iFileSizeIndex) {
		
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

	private String GetCriteraiValueType(String strFileSize,
			String criteriaOperator) {
		
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

	private ArchiveSourceFilterModel[] getArchiveSourceCriteria(
			ArchiveSourceFilterModel[] archiveSourceFilters) {
		ArchiveSourceFilterModel[] Criterias = null;
		int iCriteriasCount = 0;
		
		for(int iFilterIndex = 0;iFilterIndex < archiveSourceFilters.length ; iFilterIndex++ )
		{
			if(archiveSourceFilters[iFilterIndex].getIsCriteria() == true)
			{
				iCriteriasCount++;
			}
		}
		
		int iCriteria = 0;
		Criterias = new ArchiveSourceFilterModel[iCriteriasCount];
		for(int iCriteriaIndex = 0;iCriteriaIndex < archiveSourceFilters.length ; iCriteriaIndex++ )
		{
			if(archiveSourceFilters[iCriteriaIndex].getIsCriteria() == true)
			{
				ArchiveSourceFilterModel filterModel = archiveSourceFilters[iCriteriaIndex];
				
				Criterias[iCriteria] = new ArchiveSourceFilterModel();
				Criterias[iCriteria].setFilterOrCriteriaName(filterModel.getFilterOrCriteriaName());
				Criterias[iCriteria].setFilterOrCriteriaType(filterModel.getFilterOrCriteriaType());
				Criterias[iCriteria].setFilterOrCriteriaLowerValue(filterModel.getFilterOrCriteriaLowerValue());
				Criterias[iCriteria].setFilterOrCriteriaHigherValue(filterModel.getFilterOrCriteriaHigherValue());
				Criterias[iCriteria].setCriteriaOperator(filterModel.getCriteriaOperator());
				Criterias[iCriteria].setIsCriteria(filterModel.getIsCriteria());
				Criterias[iCriteria].setIsDefaultFilter(filterModel.getIsDefaultFilter());
				
				iCriteria++;
			}
		}
		
		return Criterias;
	}

	private ArchiveSourceFilterModel[] GetFiltersFromList(
			ArchiveSourceFilterModel[] archiveSourceFilters) {
		
		ArchiveSourceFilterModel[] filters = null;
		int iFiltersCount = 0;
		
		for(int iFilterIndex = 0;iFilterIndex < archiveSourceFilters.length ; iFilterIndex++ )
		{
			if(archiveSourceFilters[iFilterIndex].getIsCriteria() == false)
			{
				iFiltersCount++;
			}
		}
		
		int iFilter=0;
		filters = new ArchiveSourceFilterModel[iFiltersCount];
		for(int iFilterIndex = 0;iFilterIndex < archiveSourceFilters.length ; iFilterIndex++ )
		{
			if(archiveSourceFilters[iFilterIndex].getIsCriteria() == false)
			{
				ArchiveSourceFilterModel filterModel = archiveSourceFilters[iFilterIndex];
				
				filters[iFilter] = new ArchiveSourceFilterModel();
				filters[iFilter].setFilterOrCriteriaName(filterModel.getFilterOrCriteriaName());
				filters[iFilter].setFilterOrCriteriaType(filterModel.getFilterOrCriteriaType());
				filters[iFilter].setFilterOrCriteriaLowerValue(filterModel.getFilterOrCriteriaLowerValue());
				filters[iFilter].setLocFilterOrCriteriaLowerValue(filterModel.getLocFilterOrCriteriaLowerValue());
				filters[iFilter].setFilterOrCriteriaHigherValue(filterModel.getFilterOrCriteriaHigherValue());
				filters[iFilter].setCriteriaOperator(filterModel.getCriteriaOperator());
				filters[iFilter].setIsCriteria(filterModel.getIsCriteria());
				filters[iFilter].setIsDefaultFilter(filterModel.getIsDefaultFilter());
				
				iFilter++;
			}
		}
		
		return filters;
	}

	private void defineArchiveSettingsWindowListener()
	{
		archiveFieldsListener = new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent archiveFieldEvent) {
				if(archiveFieldEvent.getSource() == nfAccessed)
				{
					try{
							nfAccessed.getValue().intValue();
						}
						catch(Exception ex1)
						{
							nfAccessed.setRawValue("");
						}
				}
				else if(archiveFieldEvent.getSource() == nfModified)
				{
					try{
							nfModified.getValue().intValue();
						}
						catch(Exception ex1)
						{
							nfModified.setRawValue("");
						}
				}
				else if(archiveFieldEvent.getSource() == nfCreated)
				{
					try{
						nfCreated.getValue().intValue();
						}
						catch(Exception ex1)
						{
							nfCreated.setRawValue("");
						}
				}
				else if(archiveFieldEvent.getSource() == nfLowerSize)
				{
					try{
						nfLowerSize.getValue().intValue();
						}
						catch(Exception ex1)
						{
							nfLowerSize.setRawValue("");
						}
				}
				else if(archiveFieldEvent.getSource() == nfHigherSize)
				{
					try{
						nfHigherSize.getValue().intValue();
						}
						catch(Exception ex1)
						{
							nfHigherSize.setRawValue("");
						}
				}
			}
		};
		
		ArchiveSettingsWindowListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				if(be.getSource() == ArchiveFiltersGrid)
				{
					ArchiveSourceFilterModel archiveFilter = ArchiveFiltersGrid.getSelectionModel().getSelectedItem();
					List<ArchiveSourceFilterModel> archiveFiltersList = gridStore.getModels();
					int iSelectedIndex = archiveFiltersList.indexOf(archiveFilter);
					
					btDeleteFilter.setEnabled(iSelectedIndex != -1 ? true : false);
					//btModifyFilter.setEnabled(iSelectedIndex != -1 ? true : false);
					
					//populating the selected filter to ui for user to modify
					if(archiveFilter != null)
					{
						cbFilter.setSimpleValue(GetLocalizedFilterByIndex(archiveFilter.getFilterOrCriteriaType()));
						cbFilterType.setSimpleValue(GetLocalizedFilterTypeByIndex(archiveFilter.getFilterOrCriteriaName()));
						
						switch(cbFilterType.getSelectedIndex())
						{
						case 0:
							cbFileFilterValue.show();
							cbFolderFilterValue.hide();
							
							if(!IsThisFilterAlreadyAdded(archiveFilter.getLocFilterOrCriteriaLowerValue()))
							{
								defaultFilterList.add(defaultFilterList.size(),archiveFilter.getLocFilterOrCriteriaLowerValue());
								cbFileFilterValue.removeAll();
								cbFileFilterValue.add(defaultFilterList);
							}
							cbFileFilterValue.setRawValue(archiveFilter.getLocFilterOrCriteriaLowerValue());
							break;
						case 1:
							cbFileFilterValue.hide();
							cbFolderFilterValue.show();
							cbFolderFilterValue.setValue(archiveFilter.getLocFilterOrCriteriaLowerValue());
							break;
						}
					}
				}
				else if(be.getSource() == btAddFilter)
				{
					if(ValidateFilter() == false)
						return;
					
					ArchiveSourceFilterModel Filterinfo = new ArchiveSourceFilterModel();
					
					AddOrUpdateFilter(Filterinfo,ADD_FILTER);
				}
				else if(be.getSource() == btDeleteFilter)
				{
					ArchiveSourceFilterModel Filterinfo = ArchiveFiltersGrid.getSelectionModel().getSelectedItem();
					
					if(Filterinfo == null)
					{
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.ERROR);
						msgError.setTitleHtml(strErrorMessageTitle);
						msgError.setModal(true);
						
						msgError.setMessage(UIContext.Messages.SelectFilterToDeleteMessage());
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
						return;
					}
					
					int iIndex = defaultFilterList.indexOf(Filterinfo.getFilterOrCriteriaLowerValue());
					if(iIndex > 13)
					{
						defaultFilterList.remove(iIndex);
						cbFileFilterValue.remove(Filterinfo.getFilterOrCriteriaLowerValue());
					}
					
					filtersMap.remove(Filterinfo.getFilterOrCriteriaType()+Filterinfo.getFilterOrCriteriaName()+Filterinfo.getLocFilterOrCriteriaLowerValue());
					cbFileFilterValue.setSimpleValue("");	
					cbFolderFilterValue.setValue("");
					gridStore.remove(Filterinfo);
					ArchiveFiltersGrid.reconfigure(gridStore, ArchiveFilterColumnsModel);
				}
				/*else if(be.getSource() == btModifyFilter)
				{
					ArchiveSourceFilterModel Filterinfo = null;
					Filterinfo = ArchiveFiltersGrid.getSelectionModel().getSelectedItem();
					if(Filterinfo == null)
					{
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.ERROR);
						msgError.setTitleHtml(strErrorMessageTitle);
						msgError.setModal(true);
						
						msgError.setMessage(UIContext.Messages.PleaseselectthefilterMessage());
						msgError.show();
						return;
					}
					
					if(ValidateFilter())
					{
						AddOrUpdateFilter(Filterinfo,MODIFY_FILTER);
					}
				}*/
				else if(be.getSource() == btOK)
				{
					//ValidateArchiveSourceAndSave(strArchiveSource,false);
					validateAndSave(strArchiveSource,false);
				}
				else if(be.getSource() == btCancel)
				{
					sourceInfoModel.setArchiveSourceConfigured(false);
					strButtonClicked = "CANCEL";
					thisWindow.hide(btCancel);
				}
				else if(be.getSource() == btPreview)
				{
					sourceInfoModel.setArchiveSourceConfigured(false);
					strButtonClicked = "PREVIEW";
					ShowPreviewOfArchivableFiles();
					//thisWindow.hide(btPreview);
				}
				else if(be.getSource() == btHelp)
				{
					HelpTopics.showHelpURL(UIContext.externalLinks.getArchivePoliciesHelp());
				}
				else if(be.getSource() == cbFilterBySize)
				{
					lcFileSizeSettingsContainer.setEnabled(cbFilterBySize.getValue() != null ? cbFilterBySize.getValue() : false);
				}
				else if(be.getSource() == cbFileAccessed)
				{
					boolean bEnabled = cbFileAccessed.getValue() != null ? cbFileAccessed.getValue() : false;
					nfAccessed.setEnabled(bEnabled);
					cbFileAccessedAgeType.setEnabled(bEnabled);
				}
				else if(be.getSource() == cbFileModified)
				{
					boolean bEnabled = cbFileModified.getValue() != null ? cbFileModified.getValue() : false;
					nfModified.setEnabled(bEnabled);
					cbFileModifiedAgeType.setEnabled(bEnabled);
				}
				else if(be.getSource() == cbFileCreated)
				{
					boolean bEnabled = cbFileCreated.getValue() != null ? cbFileCreated.getValue() : false;
					nfCreated.setEnabled(bEnabled);
					cbFileCreatedAgeType.setEnabled(bEnabled);
				}
				else if(be.getSource() == cbFilterBySizeOperator)
				{
					boolean bBetweenOperatorselected = false;
					
					bBetweenOperatorselected = cbFilterBySizeOperator.getSelectedIndex() == ArchiveConstantsModel.OPERATOR_BETWEEN ? true : false; 
					
					nfHigherSize.setVisible(bBetweenOperatorselected);
					cbHigherSizeType.setVisible(bBetweenOperatorselected);
					lblAnd.setVisible(bBetweenOperatorselected);
				}
				else if(be.getSource() == cbFilterType)
				{
					switch(cbFilterType.getSelectedIndex())
					{
					case 0:
						cbFolderFilterValue.hide();
						cbFileFilterValue.show();
						break;
					case 1:
						cbFileFilterValue.hide();
						cbFolderFilterValue.show();						
						break;
					}
				}
				
			}

			private boolean IsThisFilterAlreadyAdded(String filterOrCriteriaLowerValue) 
			{
				for (String strFilter : defaultFilterList) {
					if(filterOrCriteriaLowerValue.compareToIgnoreCase(strFilter) == 0)
						return true;
				}
				return false;
			}
		};
		return;
	}

	private void AddOrUpdateFilter(ArchiveSourceFilterModel Filterinfo,int iFilterMode) 
	{
		String strFilter = Integer.toString(cbFilter.getSelectedIndex());
		String strFilterType = Integer.toString(cbFilterType.getSelectedIndex());
		String strFilterValue = null;

		switch(cbFilterType.getSelectedIndex())
		{
		case 0:
			strFilterValue = cbFileFilterValue.getRawValue();
			
			if(cbFileFilterValue.getSelectedIndex() != -1)
			{
				Filterinfo.setIsDefaultFilter(true);
			}
			break;
		case 1:
			strFilterValue = cbFolderFilterValue.getValue();
			break;
		}
		
		if(strFilterValue!=null && strFilterValue.length()>0) {
			strFilterValue = strFilterValue.trim();
		}
		
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.INFO);
		msgError.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
		msgError.setModal(true);

		if(!filtersMap.contains(strFilter+strFilterType+strFilterValue))
		{
			filtersMap.remove(Filterinfo.getFilterOrCriteriaType()+Filterinfo.getFilterOrCriteriaName()+Filterinfo.getLocFilterOrCriteriaLowerValue());
			filtersMap.add(strFilter+strFilterType+strFilterValue);
			Filterinfo.setFilterOrCriteriaType(strFilter);
			Filterinfo.setFilterOrCriteriaName(strFilterType);
			Filterinfo.setLocFilterOrCriteriaLowerValue(strFilterValue);
			
			int iIndex = defaultFilterList.indexOf(strFilterValue);
			
			if(iIndex != -1)
				Filterinfo.setFilterOrCriteriaLowerValue(defaultFilterList.get(iIndex));//if default filter.
			else
				Filterinfo.setFilterOrCriteriaLowerValue(strFilterValue);// if user given filter
		}
		else
		{
			msgError.setMessage(UIContext.Messages.FilterAlreadyAddedMessage());
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			return;
		}
		
		switch(iFilterMode)
		{
		case ADD_FILTER:
			gridStore.add(Filterinfo);
			break;
		case MODIFY_FILTER:
			gridStore.update(Filterinfo);
			break;
		}
		ArchiveFiltersGrid.reconfigure(gridStore, ArchiveFilterColumnsModel);
	}
		
	public String getButtonClicked()
	{
		return strButtonClicked;
	}
	
	public ArchiveSourceInfoModel getSelectedSourceModel()
	{
		return sourceInfoModel;
	}
	
	public boolean isPathSourcePathChanged()
	{
		return isPathChanged;
	}
	
	private void ShowPreviewOfArchivableFiles()
	{
		packageSourceInformation();
		if(archiveFilePreviewWind == null)
		{
			archiveFilePreviewWind = new PreviewWindow(sourceInfoModel);
			//archiveFilePreviewWind.RefreshData(parentWindow.archiveConfigModel != null ? parentWindow.archiveConfigModel.getCloudConfigModel():null);
		}
		archiveFilePreviewWind.setModal(true);
		//archiveFilePreviewWind.loadArchivableFiles();
		archiveFilePreviewWind.show();
	}
	
	private ArchiveSourceInfoModel packageSourceInformation()
	{
		ValidateAndSave(true);
		
		return sourceInfoModel;
	}
	
//	public List<FileModel> getBackupVolumes()
//	{
//		return NonSelectableVolumes;
//	}
//	
//	public void setBackupVolumes(List<FileModel> in_volumes)
//	{
//		NonSelectableVolumes = in_volumes;
//		ArchiveSourceSelectionPanel.setVolumesList2Filter(NonSelectableVolumes);
//	}
	
	public void setSelectedBackupVolumes(BackupVolumeModel in_Selectedvolumes)
	{
		selectedBackupVolumes = in_Selectedvolumes;
		ArchiveSourceSelectionPanel.setSelectedVolumesList2Filter(in_Selectedvolumes);
	}
	
	public void setSelectedBackupRefsVolumes(BackupVolumeModel in_Selectedvolumes)
	{
		selectedBackupRefsVolumes = in_Selectedvolumes;		
	}
	
	public void setSelectedBackupDedupeVolumes(BackupVolumeModel in_Selectedvolumes)
	{
		selectedBackupDedupeVolumes = in_Selectedvolumes;		
	}
	
	public void setBackupDestination(String in_backupDestinatio)
	{
		backupDestination = in_backupDestinatio;
	}
	
	public String getBackupDestination()
	{
		return backupDestination;
	}
	
	//seems no body use it.
//	public void getSelectedBackupVolumes()
//	{
//		if(NonSelectableVolumes == null)
//		{
//			thisWindow.mask(UIContext.Constants.LoadingbackupVolumesinformationMessage());
//			loginService.getSelectedBackupVolumesInfo(new BaseAsyncCallback<List<FileModel>>() {
//				
//				@Override
//				public void onSuccess(List<FileModel> result) {
//					if (result != null)
//					{
//						NonSelectableVolumes = result;
//						ArchiveSourceSelectionPanel.setVolumesList2Filter(NonSelectableVolumes);
//					}
//					thisWindow.unmask();
//				}
//				
//				@Override
//				public void onFailure(Throwable caught) {
//					thisWindow.unmask();
//					super.onFailure(caught);
//				}
//			});
//		}
//		else
//			ArchiveSourceSelectionPanel.setVolumesList2Filter(NonSelectableVolumes);
//	}
	private native String getTemplate() /*-{ 
    return  [ 
    '<tpl for=".">', 
      '<div class="x-combo-list-item" qtip="<div class=\'tooltip-item\'>{value}</div>" qtitle="">{values.value}</div>', 
    '</tpl>' 
    ].join(""); 
  }-*/;
}
