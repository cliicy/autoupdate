package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.HighlightedDateField;
import com.ca.arcflash.ui.client.common.HighlightedDateMenu;
import com.ca.arcflash.ui.client.common.HighlightedDatePicker;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveCloudDestInfoModel;
import com.ca.arcflash.ui.client.model.ArchiveDestinationModel;
import com.ca.arcflash.ui.client.model.ArchiveDiskDestInfoModel;
import com.ca.arcflash.ui.client.model.ArchiveSettingsModel;
import com.ca.arcflash.ui.client.model.BackupD2DModel;
import com.ca.arcflash.ui.client.model.BackupRPSDestSettingsModel;
import com.ca.arcflash.ui.client.model.CatalogItemModel;
import com.ca.arcflash.ui.client.model.CloudVendorType;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.OndemandInfo4RPS;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.vsphere.vmrecover.VMRestoreSourcePanel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class RestoreSearchPanel extends LayoutContainer implements
		RestoreValidator, IRestoreSourceListener {

	private RestoreWizardContainer wizard;      ///D2D Lite Integration
	protected final LoginServiceAsync service = GWT.create(LoginService.class);	
	final CommonServiceAsync commonService = GWT.create(CommonService.class);
	
	private RestoreSearchPanel thisPanel;
	private final static int PAGESIZE = PagingContext.DEFAULTPAGESIZE;

//	protected PathSelectionPanel pathSelection;
	//private PathSelectionPanel ArchivePathSelectionForSearch;
	public TextField<String> txtArchiveDestination;
	public Button btChangeArchiveDestination;
	private ArchivePathSelectionWindow archivePathSelectionWind = null;
	
	public String strBackupDestination;
	public String strBackupDestinationUsername;
	public String strBackupDestinationPassword;
	
	public ArchiveDestinationModel archiveDestinationInfo = null;
	private ArchiveDiskDestInfoModel archiveDiskInfo = new ArchiveDiskDestInfoModel();
	private ArchiveCloudDestInfoModel archiveCloudInfo = null;
	private PagingLoader<PagingLoadResult<ModelData>> loader;
	
	public boolean bSearchBackups = false;
	public boolean bSearchArchives = false;
//	protected CheckBox cbSelectBackupForSearch;
//	protected CheckBox cbSelectArchiveForSearch;
	protected LabelField backupLocation;
	protected FieldSet fileCopyField;
	private FieldSet backupField;
	private RestoreSourcePanel backupSourcePanel = null;

	protected boolean isWndClosed = false;
	
//	private Listener<BaseEvent> pathSelectionListener = null;
    private List<RecoveryPointModel> selectedSessions = new ArrayList<RecoveryPointModel>();
    private List<RecoveryPointModel> filteredSessions = new ArrayList<RecoveryPointModel>();
    private Map<RecoveryPointModel, FlashCheckBox> fcbCache = new HashMap<RecoveryPointModel, FlashCheckBox>();
    
	
    static String archiveDestination;
    protected LayoutContainer lcArchiveDestinationContainer;
	
	private RadioGroup sessionRadioGP;
	private Radio allSessionRd;
	private Radio selectSessionRd;
	
	private HighlightedDateField startTime;
	private HighlightedDateField endTime;
	private Button filterButton;
	private ContentPanel sessionContainer;
	private Grid<RecoveryPointModel> grid;
	
	private ListStore<RecoveryPointModel> sessions;
	private List<RecoveryPointModel> recoveryPoints = new ArrayList<RecoveryPointModel>();
	private Map<String, String> pwdsByHash = new HashMap<String, String>();
	
	private static final long ONEDAYMILLISENC = 24 * 3600 * 1000;
	
	private boolean isSelectSessionRadio = false;
	protected Boolean isFileCopyEnabled;
	
	public RestoreSearchPanel(RestoreWizardContainer restoreWizardWindow) {       ///D2D Lite Integration
		wizard = restoreWizardWindow;
		thisPanel = this;
		
		wizard.addListener(Events.Hide, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				isWndClosed = true;
			}

		});
		
		wizard.addListener(Events.Show, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				isWndClosed = false;
			}

		});
		
		CustomizationModel customizedModel = UIContext.customizedModel;
		isFileCopyEnabled = customizedModel.get("FileCopy");
	}
	
	private final AsyncCallback<RecoveryPointModel[]> rpCallback 
		= new BaseAsyncCallback<RecoveryPointModel[]>() {

		@Override
		public void onFailure(Throwable caught) {
			grid.unmask();
			setFilterElementEnabled(true);
			//No need to popup error in this case
//			thisPanel.popUpError(UIContext.Constants.restoreSearchNoRPValidation());					
		}

		@Override
		public void onSuccess(RecoveryPointModel[] result) {
			for(int i = 0; i < result.length; i ++) {
				if(result[i].listOfRecoveryPointItems == null || result[i].listOfRecoveryPointItems.size() == 0){
					result[i].setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
				}
				recoveryPoints.add(result[i]);
			}
			if(recoveryPoints.size() > 0) {
				Date serverDate = RestoreUtil.RecPointModel2ServerDate(result[0]);
				startTime.setValue(serverDate);
				endTime.setValue(serverDate);
			}/*else {//No need to popup error in this case
				thisPanel.popUpError(UIContext.Constants.restoreSearchNoRPValidation());
			}*/
			Date date = new Date();
			for(RecoveryPointModel rp : result) {
				Date serverDate = RestoreUtil.RecPointModel2ServerDate(rp);
				if (rp.getBackupSetFlag() > 0) {
					((InternalHighlightedDateField) startTime).addBackupSetFlag(serverDate);
					((InternalHighlightedDateField) endTime).addBackupSetFlag(serverDate);
				}
				startTime.addSelectedDate(serverDate, serverDate.getMonth() == date.getMonth());
				endTime.addSelectedDate(serverDate, serverDate.getMonth() == date.getMonth());
			}
			thisPanel.getRecoveryPoints(startTime.getValue(), endTime.getValue());
			grid.unmask();
			setFilterElementEnabled(true);
		}
	};
	
	private void renderArchiveSource(LayoutContainer container){
		fileCopyField = new FieldSet(){

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);
				bSearchArchives = fileCopyField.isExpanded();
				txtArchiveDestination.setEnabled(bSearchArchives);
				btChangeArchiveDestination.setEnabled(bSearchArchives);
				thisPanel.refreshSearchContainer();
			}
		};
		
		fileCopyField.setHeadingHtml(UIContext.Constants.restoreArchiveSource());
//		fileCopyField.setWidth("95%");
		fileCopyField.setWidth(670);
		
		lcArchiveDestinationContainer = new LayoutContainer();
		TableLayout tlArchiveDestination = new TableLayout(2);
		tlArchiveDestination.setCellSpacing(0);
		tlArchiveDestination.setCellPadding(4);
		tlArchiveDestination.setWidth("100%");
		lcArchiveDestinationContainer.setLayout(tlArchiveDestination);
		
		TableData td2 = new TableData();
		txtArchiveDestination = new TextField<String>();
		txtArchiveDestination.ensureDebugId("8A73372A-1450-4b41-A923-093B70A45649");
		txtArchiveDestination.setReadOnly(true);
		txtArchiveDestination.setWidth(500);
//		txtArchiveDestination.setStyleAttribute("margin-left", "15px");
		td2.setWidth("82%");
		lcArchiveDestinationContainer.add(txtArchiveDestination, td2);

		btChangeArchiveDestination = new Button(UIContext.Constants.ArchiveChangeDestinationButton());
		Utils.addToolTip(btChangeArchiveDestination, UIContext.Constants.ArchiveChangeDestinationButtonTooltip());
//		btChangeArchiveDestination.setStyleAttribute("margin-left", "2");
		btChangeArchiveDestination.ensureDebugId("72675E9E-594C-48f1-952D-F182B04DF024");
		btChangeArchiveDestination.setMinWidth(80);
		btChangeArchiveDestination.setAutoWidth(true);
		btChangeArchiveDestination.addListener(Events.Select, defineFileCopyChangeListener());
		TableData td15 = new TableData();
		td15.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td15.setWidth("18%");
		lcArchiveDestinationContainer.add(btChangeArchiveDestination, td15);
		
//		container.add(lcArchiveDestinationContainer, td2);
		fileCopyField.add(lcArchiveDestinationContainer);
		fileCopyField.setCheckboxToggle(true);
//		fileCopyField.setStyleAttribute("padding", "5px");
		container.add(fileCopyField);
		getDefaultSourceValue();
	}
	
	private void renderBackupSource(LayoutContainer container){
		backupField = new FieldSet(){

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);
				
				bSearchBackups = backupField.isExpanded();
				if(selectSessionRd.getValue())
					sessionContainer.setEnabled(bSearchBackups);
				sessionRadioGP.setEnabled(bSearchBackups);
				selectSessionRd.setEnabled(bSearchBackups);
				allSessionRd.setEnabled(bSearchBackups);
				thisPanel.refreshSearchContainer();
			}
		};
		//backupField.setStyleAttribute("padding", "5px");
//		backupField.setWidth("95%");
		backupField.setWidth(670);
		
		TableData td = new TableData();
//		td.setColspan(2);
		td.setWidth("100%");
		//For backup location alignment
		LayoutContainer tempCon = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		tl.setCellPadding(0);
		tl.setCellSpacing(0);
		tempCon.setLayout(tl);
		
		backupField.setHeadingHtml(UIContext.Constants.restoreBackupSource());
		if(isFileCopyEnabled) {	
			backupField.setCheckboxToggle(true);
		}			

		Listener<BaseEvent> changeListener = new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				backupPathChanged();
			}
			
		};
		if(UIContext.backupVM == null){
			backupSourcePanel = new RestoreSourcePanel();
			backupSourcePanel.setPanelWidth("100%");
//			backupSourcePanel.setPadding(0);
			backupSourcePanel.setButtonHAlign(HorizontalAlignment.RIGHT);
		}else
			backupSourcePanel = new VMRestoreSourcePanel();
		backupSourcePanel.setSourceListener(thisPanel);
		backupSourcePanel.setPathChangeListener(changeListener);
		
		tempCon.add(backupSourcePanel);
		backupField.add(tempCon);
		container.add(backupField);
	}
	
	private void renderSessionRadio(LayoutContainer container){
		TableData tdr = new TableData();
		tdr.setWidth("98%");
		sessionRadioGP = new RadioGroup();
		sessionRadioGP.setSelectionRequired(true);
		sessionRadioGP.setOrientation(Orientation.VERTICAL);
		
		allSessionRd = new Radio(){

			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				isSelectSessionRadio = false;
				sessionContainer.disable();
			}
		};
		Utils.addToolTip(allSessionRd, UIContext.Constants.restoreSearchAllSessionToolTip());
		allSessionRd.ensureDebugId("71C41338-9ADA-43b7-87AF-C976D50A448F");
		allSessionRd.setBoxLabel(UIContext.Constants.restoreSearchAllSessions());
		allSessionRd.setValue(true);
		sessionRadioGP.add(allSessionRd);
		container.add(allSessionRd, tdr);		
		
		selectSessionRd = new Radio(){

			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				if(!sessionContainer.isEnabled())
					sessionContainer.enable();
				if(recoveryPoints.isEmpty() && !isSelectSessionRadio) {
					isSelectSessionRadio = true;
					retriveAllRPS(rpCallback, UIContext.Constants.restoreSearchLoading());
				}else {
					if(sessions.getCount() == 0) {
						thisPanel.getRecoveryPoints(startTime.getValue(), endTime.getValue());
					}
				}
			}
		};
		Utils.addToolTip(selectSessionRd, UIContext.Constants.restoreSearchSelectSessionToolTip());
		selectSessionRd.ensureDebugId("00C1EDAA-5CFF-43a3-A220-E1ABCD28D139");
		selectSessionRd.setBoxLabel(UIContext.Constants.restoreSearchSelectionSession());
		sessionRadioGP.add(selectSessionRd);
		tdr = new TableData();
		tdr.setWidth("98%");
		container.add(selectSessionRd, tdr);	
	}
	
	private LayoutContainer renderSourceSection() {
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		tl.setWidth("100%");
		container.setLayout(tl);	
		
		if(isFileCopyEnabled)
		{
			renderArchiveSource(container);
		}		
		
		renderBackupSource(container);
		
		renderSessionRadio(container);
		
		return container;
	}
	
	private void backupPathChanged() {
		sessions.removeAll();
		selectedSessions.clear();
		recoveryPoints.clear();
		//
		if(this.selectSessionRd.getValue()) {
			//retrive all recovery points
			this.retriveAllRPS(rpCallback, UIContext.Constants.restoreSearchLoading());
		}
	}
	
	private void definePathSelectionListener()
	{/*
		pathSelectionListener = new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent event) {
			
				if(event.getSource() == cbSelectBackupForSearch){
					pathSelection.setEnabled(cbSelectBackupForSearch.getValue());
					bSearchBackups = cbSelectBackupForSearch.getValue();
					if(selectSessionRd.getValue())
						sessionContainer.setEnabled(bSearchBackups);
					sessionRadioGP.setEnabled(bSearchBackups);
					selectSessionRd.setEnabled(bSearchBackups);
					allSessionRd.setEnabled(bSearchBackups);
				}
				
				if(event.getSource() == cbSelectArchiveForSearch)
				{
					txtArchiveDestination.setEnabled(cbSelectArchiveForSearch.getValue());
					btChangeArchiveDestination.setEnabled(cbSelectArchiveForSearch.getValue());
					bSearchArchives = cbSelectArchiveForSearch.getValue();
				}
				
				wizard.nextButton.setEnabled(bSearchBackups || bSearchArchives);
				if((event.getSource() == cbSelectBackupForSearch) || (event.getSource() == cbSelectArchiveForSearch))
				{
					refreshSearchContainer();
				}
			}
		};
	*/}

	
	protected void getDefaultSourceValue() {
		// 1) Get the backup configuration and set the source accordingly
		this.mask(UIContext.Constants.restoreSearchGettingDefaultSource());
		if(isFileCopyEnabled)
		{	
			service.getArchiveConfiguration(new AsyncCallback<ArchiveSettingsModel>() {

				@Override
				public void onFailure(Throwable caught) {
					thisPanel.unmask();
					//wizard.nextButton.setEnabled(false);
					bSearchArchives = false;
					wizard.archiveConfig = null;
//					cbSelectArchiveForSearch.setValue(false);
					fileCopyField.collapse();
					txtArchiveDestination.setEnabled(false);
					btChangeArchiveDestination.setEnabled(false);
				}

				@Override
				public void onSuccess(ArchiveSettingsModel in_archiveConfig) {
					thisPanel.unmask();
					thisPanel.fireSearchedDataChanged(1);
					if(in_archiveConfig != null)
					{
						wizard.archiveConfig = in_archiveConfig;
						bSearchArchives = true;
//						cbSelectArchiveForSearch.setValue(true);
						fileCopyField.expand();
						txtArchiveDestination.setEnabled(true);
						btChangeArchiveDestination.setEnabled(true);
						if(in_archiveConfig.getArchiveToDrive())
						{
							txtArchiveDestination.setValue(in_archiveConfig.getArchiveToDrivePath());

							if(archiveDiskInfo == null)
								archiveDiskInfo = new ArchiveDiskDestInfoModel();
							archiveDiskInfo.setArchiveDiskDestPath(in_archiveConfig.getArchiveToDrivePath());
							archiveDiskInfo.setArchiveDiskUserName(in_archiveConfig.getDestinationPathUserName());
							archiveDiskInfo.setArchiveDiskPassword(in_archiveConfig.getDestinationPathPassword());
							archiveDestination = in_archiveConfig.getArchiveToDrivePath();
							ArchivePathSelectionWindow.archiveDestination = in_archiveConfig.getArchiveToDrivePath();

							if(archiveDestinationInfo == null)
								archiveDestinationInfo = new ArchiveDestinationModel();
							archiveDestinationInfo.setArchiveDiskDestInfoModel(archiveDiskInfo);
							archiveDestinationInfo.setArchiveToCloud(false);
							archiveDestinationInfo.setArchiveToDrive(true);

							if(in_archiveConfig.getArchiveToDrivePath() != null 
									&& in_archiveConfig.getArchiveToDrivePath().length() != 0)
								wizard.nextButton.setEnabled(true);
						}
						else if(in_archiveConfig.getArchiveToCloud())
						{
							ArchiveCloudDestInfoModel cloudConfig = in_archiveConfig.getCloudConfigModel();
							if(cloudConfig != null)
							{
								archiveCloudInfo = in_archiveConfig.getCloudConfigModel();

								if(archiveDestinationInfo == null)
									archiveDestinationInfo = new ArchiveDestinationModel();
								archiveDestinationInfo.setCloudConfigModel(archiveCloudInfo);
								archiveDestinationInfo.setArchiveToCloud(true);
								archiveDestinationInfo.setArchiveToDrive(false);

								txtArchiveDestination.setValue(cloudConfig.getcloudVendorURL());
								refreshTxtArchiveDestination(cloudConfig);
								archiveDestination = cloudConfig.getcloudVendorURL()+cloudConfig.getcloudBucketName();
								ArchivePathSelectionWindow.archiveDestination = cloudConfig.getcloudVendorURL()+cloudConfig.getcloudBucketName();

							}
						}
					}
					else
					{
						bSearchArchives = false;
						wizard.archiveConfig = null;
//						cbSelectArchiveForSearch.setValue(false);
						fileCopyField.collapse();
						txtArchiveDestination.setEnabled(false);
						btChangeArchiveDestination.setEnabled(false);
					}

					refreshSearchContainer();
				}
			});
		}
	}

	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_search()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreFind());
		label.setStyleName("restoreWizardTitle");
		container.add(label);

		return container;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		//setStyleAttribute("margin", "10px");
		this.setHeight("100%");
		this.setScrollMode(Scroll.AUTOY);
		RowLayout rl = new RowLayout();
		this.setLayout(rl);

		add(renderHeaderSection());
		
		definePathSelectionListener();
		RowData data = new RowData();
		data.setMargins(new Margins(5, 0, 0, 0));
		LabelField findLabel = new LabelField();
		findLabel.addStyleName("restoreWizardSubItem");
		findLabel.setValue(UIContext.Constants.restoreLocations());
		add(findLabel, data);
		
		data = new RowData();
		data.setMargins(new Margins(0, 4, 0, 0));
		add(renderSourceSection(), data);
		
		LabelField sessionLabel = new LabelField();
		sessionLabel.addStyleName("restoreWizardSubItem");
		sessionLabel.setValue(UIContext.Constants.restoreSessions());
		add(sessionLabel);
		//add session selection part
		data = new RowData();
//		data.setMargins(new Margins(10,10, 10,10));
		add(renderSessionSelection(), data);
	}
	
	private LayoutContainer renderSessionSelection() {
		sessionContainer = new ContentPanel();		
		sessionContainer.setHeaderVisible(false);
		sessionContainer.setWidth(675);
		TableLayout tl = new TableLayout();
		tl.setCellPadding(4);
		tl.setCellSpacing(4);
		tl.setWidth("100%");
		tl.setColumns(3);
		sessionContainer.setLayout(tl);
		
		TableData td2 = new TableData();
		td2.setWidth("30%");		
		startTime = addTimePart(UIContext.Constants.restoreSearchStartTime(),
				sessionContainer, td2, UIContext.Constants.restoreSearchStartTimeToolTip());
		
		td2 = new TableData();
		td2.setWidth("30%");		
		endTime = addTimePart(UIContext.Constants.restoreSearchEndTime(), sessionContainer, td2, 
				UIContext.Constants.restoreSearchEndTimeToolTip());
		
		td2 = new TableData();
		td2.setWidth("21%");
		td2.setHorizontalAlign(HorizontalAlignment.RIGHT);
		filterButton = new Button();
		filterButton.setMinWidth(80);
		filterButton.setAutoWidth(true);
		Utils.addToolTip(filterButton, UIContext.Constants.restoreSearchFilterToolTip());
		filterButton.addListener(Events.Select, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				if(startTime.getValue().getTime() > endTime.getValue().getTime()) {
					popUpError(UIContext.Constants.restoreSearchDateError());
				}else {
					if(recoveryPoints.isEmpty()) {
						/*AsyncCallback<RecoveryPointModel[]> callback = new BaseAsyncCallback<RecoveryPointModel[]> () {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								grid.unmask();
								setFilterElementEnabled(true);
								thisPanel.popUpError(UIContext.Constants.restoreSearchNoRPValidation());
							}

							@Override
							public void onSuccess(RecoveryPointModel[] result) {
									for(int i = 0; i < result.length; i ++) {
										recoveryPoints.add(result[i]);
									}
									setFilterElementEnabled(true);
									getRecoveryPoints(startTime.getValue(), endTime.getValue());
									
								    grid.unmask();
							}};
						thisPanel.validateBackupSource(callback, UIContext.Constants.loadingIndicatorText(),null);*/
					}
					getRecoveryPoints(startTime.getValue(), endTime.getValue());
				}
			}
			
		});
		filterButton.ensureDebugId("B104DEED-44B5-4b38-8B01-8F5BA4D86620");
		filterButton.setText(UIContext.Constants.restoreSearchFilter());
		sessionContainer.add(filterButton, td2);
		
		td2 = new TableData();
		td2.setWidth("98%");
		td2.setColspan(3);
		
		DataProxy<PagingLoadResult<RecoveryPointModel>> proxy = new DataProxy<PagingLoadResult<RecoveryPointModel>>() {

			@Override
			public void load(
					DataReader<PagingLoadResult<RecoveryPointModel>> reader,
					Object loadConfig,
					AsyncCallback<PagingLoadResult<RecoveryPointModel>> callback) {
				final PagingLoadConfig conf = (PagingLoadConfig)loadConfig;
					PagingLoadResult<RecoveryPointModel> models = loadRecoveryPoint(conf);
					callback.onSuccess(models);
				}
		};
		
		// loader
		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(
				proxy);
		
		sessionContainer.add(renderSessionTable(loader), td2);
		
		PagingToolBar toolBar = new PagingToolBar(PAGESIZE){	
			@Override
			protected void onRender(Element target, int index) {				
				super.onRender(target, index);
				this.pageText.setWidth("38px");
				this.pageText.addKeyPressHandler(new KeyPressHandler(){
					@Override
					public void onKeyPress(KeyPressEvent event) {
						char key = event.getCharCode();
						if(event.isControlKeyDown() || key == KeyCodes.KEY_ENTER 
								|| key == KeyCodes.KEY_BACKSPACE || key == KeyCodes.KEY_DELETE){
							return;
						}
						if(!Character.isDigit(key)){
							pageText.cancelKey();
						}
					}
					
				});
				this.next.ensureDebugId("77B8EFD8-8DCD-4189-9BF9-A441E17E6351");
				this.prev.ensureDebugId("C988882D-40D7-4e4a-92FE-AB829061A9B2");
				this.first.ensureDebugId("95447E19-179E-4191-B78B-98B78EA8B059");
				this.last.ensureDebugId("690A5940-0452-4d31-B493-68281BF9C6D8");
				this.refresh.hide();
			}			
		};
		toolBar.bind(loader);
		
		sessionContainer.setBottomComponent(toolBar);
		sessionContainer.disable();
		
		return sessionContainer;
	}
	
	private PagingLoadResult<RecoveryPointModel> loadRecoveryPoint(PagingLoadConfig conf) {
		int start = conf.getOffset();
		int end = start + conf.getLimit();
		end = end > filteredSessions.size() ? filteredSessions.size() : end; 
		
		List<RecoveryPointModel> models = this.filteredSessions.subList(start, end);
		
		PagingLoadResult<RecoveryPointModel> result = 
			new BasePagingLoadResult<RecoveryPointModel>(models, start, filteredSessions.size());
		
		return result;
	}
	
	private void getRecoveryPoints(Date startTime, Date endTime) {
//		grid.mask(UIContext.Constants.loadingIndicatorText());
		sessions.removeAll();
		fcbCache.clear();
		this.selectedSessions.clear();
		this.filteredSessions.clear();
		
		for(RecoveryPointModel rpm : this.recoveryPoints) {
			Date rpTime = Utils.localTimeToServerTime(rpm.getTime(), rpm.getTimeZoneOffset());
			if(rpTime.getTime() >= startTime.getTime() && rpTime.getTime() <= (endTime.getTime() + ONEDAYMILLISENC)) {
				filteredSessions.add(rpm);
			}
		}
		if(recoveryPoints.size() > 0)
			loader.load(0, PAGESIZE);
//		grid.unmask();
	}
	
	public void popUpError(String message) {
		MessageBox box = new MessageBox();
		box.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
		box.setMessage(message);
		box.setIcon(MessageBox.ERROR);
		Utils.setMessageBoxDebugId(box);
		box.show();
	}
	
	protected void disableCheckBox(RecoveryPointModel model, FlashCheckBox box) {
		if(model.getFSCatalogStatus() == RestoreConstants.FSCAT_DISABLED&&!model.isCanCatalog()&&!model.isCanMount()){
			box.setEnabled(false);
			Utils.addToolTip(box, UIContext.Constants.cannotSelectedForRestoreSearch());
		}
		
		if(model.getFSCatalogStatus() == RestoreConstants.FSCAT_NOTCREATE){
			box.setEnabled(false);
			Utils.addToolTip(box, 
					UIContext.Constants.restoerSearchVMPowerOffCatalog());
		}
	}
	
	private Grid<RecoveryPointModel> renderSessionTable(PagingLoader<PagingLoadResult<ModelData>> loader) {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();
		column.setId("Time");
		column.setHeaderHtml(UIContext.Constants.restoreTimeColumn());
		column.setRenderer(new GridCellRenderer<RecoveryPointModel>(){
			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				LayoutContainer con = new LayoutContainer();
				TableLayout tl = new TableLayout();
				tl.setColumns(2);
				tl.setWidth("100%");
				con.setLayout(tl);

				final RecoveryPointModel fModel = model; 
				FlashCheckBox box = fcbCache.get(fModel);
				if(box == null) {
					box = new FlashCheckBox();;
					fcbCache.put(fModel, box);
				}
				disableCheckBox(fModel, box);
				final FlashCheckBox fbox = box;
				box.addSelectionListener(new SelectionListener<IconButtonEvent>() {

					@Override
					public void componentSelected(IconButtonEvent ce) {
						if(fbox.getSelectedState() == FlashCheckBox.NONE)
							sessionSelected(fModel, false);
						else if(fbox.getSelectedState() == FlashCheckBox.FULL)
							sessionSelected(fModel, true);
					}
				});
				
				con.add(box);
				
				LabelField lf = new LabelField();
				String time = "";
				Date datetime = model.getTime();
				if (datetime != null) {
					if (model.getTimeZoneOffset() != null) {
						time = Utils.formatDateToServerTime(datetime, model.getTimeZoneOffset());
					}else
						time =  Utils.formatDateToServerTime(datetime);
				}
				lf.setValue(time);
				con.add(lf);
				return con;
			}
		});
		column.setWidth(170);
		column.setMenuDisabled(true);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("Type");
		column.setHeaderHtml(UIContext.Constants.restoreSchedTypeColumn());
		GridCellRenderer<RecoveryPointModel> schedTypeRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				return Utils.schedFlag2String(model.getPeriodRetentionFlag());
			}
			
		};
		column.setRenderer(schedTypeRenderer);
		column.setWidth(80);
		column.setMenuDisabled(true);
		configs.add(column);		
		
		column = new ColumnConfig();
		column.setId("backupType");
		column.setHeaderHtml(UIContext.Constants.restoreTypeColumn());
		column.setWidth(139);
		column.setRenderer(new GridCellRenderer<RecoveryPointModel> (){

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				return RestoreConstants.getBackupJobType(model.getBackupType());
			}
			
		});
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("Name");
		column.setHeaderHtml(UIContext.Constants.restoreNameColumn());
		column.setRenderer(new GridCellRenderer<RecoveryPointModel> () {
		@Override
		public Object render(RecoveryPointModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid){
				String name = model.getName();
				if(name != null){
					return UIContext.escapeHTML(name);
				}
		
				return "";
			}
		});
		column.setMenuDisabled(true);
		column.setWidth(150);
		configs.add(column);	
		
		column = new ColumnConfig();
		column.setId("CatalogStatus");
		column.setHeaderHtml(UIContext.Constants.restoreCatalogStatusColumn());
		column.setMenuDisabled(true);
		column.setWidth(110);
		column.setRenderer(new GridCellRenderer<RecoveryPointModel> () {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				if(UIContext.uiType == 1 && UIContext.backupVM !=null){
					return RestoreConstants.getFSCatalogStatusMsgForVSphere(model.getFSCatalogStatus());
				}else{
					return RestoreConstants.getFSCatalogStatusMsg(model.getFSCatalogStatus());
				}
			}
		});
		configs.add(column);
		
		ColumnModel cModel = new ColumnModel(configs);
		
		sessions = new ListStore<RecoveryPointModel>(loader);
		grid = new Grid<RecoveryPointModel>(sessions, cModel);
//		grid.setAutoExpandColumn("Name");
//		grid.setAutoExpandMax(5000);
		grid.setBorders(true);
//		grid.getView().setAdjustForHScroll(false);
//		grid.getView().setAutoFill(true);
		grid.setStripeRows(true);
		grid.setHeight(getSessionTableHeight());
		return grid;
	}
	
	protected int getSessionTableHeight() {
		return 180;
	}
	
	private void sessionSelected(RecoveryPointModel model, boolean selected) {
		if(selected) {
			selectedSessions.add(model);
		}else {
			selectedSessions.remove(model);
		}
	}
	
	private HighlightedDateField addTimePart(String desp, LayoutContainer parent, LayoutData ldata, String toolTip) {
		LayoutContainer con = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		tl.setColumns(2);
		con.setLayout(tl);
		
		TableData td = new TableData();
		td.setWidth("40%");
		LabelField lf = new LabelField();
		lf.setValue(desp);
		con.add(lf, td);
		
		td = new TableData();
		td.setWidth("60%");
		HighlightedDateField df = new InternalHighlightedDateField();
		df.setValue(new Date());
		Utils.addToolTip(df, toolTip);
		df.ensureDebugId("2DB010EB-62F7-4804-A672-D1DBDE4A2828_" + desp);
		df.setEditable(true);
		con.add(df, td);
		
		parent.add(con, ldata);
		return df;
	}

	List<CatalogItemModel> subList(List<CatalogItemModel> list, int from, int to) {
		List<CatalogItemModel> ret = new ArrayList<CatalogItemModel>();
		for (int i = from; i < to; i++) {
			ret.add(list.get(i));
		}
		return ret;
	}

	public String getSessionPath() {
		return backupSourcePanel.getBackupDestination();
	}

	public String getUserName() {
		return backupSourcePanel.getDestUserName();
	}

	public String getPassword() {
		return backupSourcePanel.getDestPassword();
	}
	private void checkSessionPassword(final List<RecoveryPointModel> sessions, 
			final AsyncCallback<Boolean> callback) {
//		final String[] guids = new String[sessions.size()];
		final List<String> guids = new ArrayList<String>();		
		final Map<String, RecoveryPointModel> models = new HashMap<String, RecoveryPointModel>();
		
		for(int i = 0; i < sessions.size(); i ++) {
			RecoveryPointModel model = sessions.get(i);
			if(model.isEncrypted()) {
				guids.add(model.getSessionGuid());
				models.put(model.getSessionGuid(), model);
			}
		}
		
		commonService.getSessionPasswordBySessionGuid(guids.toArray(new String[0]), 
				new BaseAsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(String[] result) {
				if(result != null) {
					for(int i = 0; i < result.length; i ++) {
						String pass = result[i];
						if(pass != null && !pass.isEmpty()) {
							RecoveryPointModel model = models.get(guids.get(i));
							if(model != null){
								pwdsByHash.put(model.getEncryptPwdHashKey(), pass);
								models.remove(guids.get(i));
							}
						}
					}
				}
				if(!models.isEmpty()) {
					SessionPasswordPanel passwordPanel = new SessionPasswordPanel(getSessionPath(),"",
							getUserName(), getPassword(),
						new ArrayList<RecoveryPointModel>(models.values()), new PasswordCallback(callback));
					passwordPanel.show();
				}else {
					callback.onSuccess(true);
				}
			}
		});
	}
	
	//this method will return "true" if recovery point has all Refs and NTFS Dedupe volumes . if at least one volume is NTFS it will return flase
	private boolean isSessionHasAllRefsAndNtfsVolumes(RecoveryPointModel model)
	{		
		List<com.ca.arcflash.ui.client.model.RecoveryPointItemModel> listOfRecoveryPointItems = model.listOfRecoveryPointItems;
		if(listOfRecoveryPointItems != null && listOfRecoveryPointItems.size() > 0)
		{		
			for(com.ca.arcflash.ui.client.model.RecoveryPointItemModel session : listOfRecoveryPointItems)
			{
			  if(session.getVolAttr() != RecoveryPointsPanel.DedupVol && session.getVolAttr() != RecoveryPointsPanel.RefsVol)	
			  {
				  //if any volume is other than Dedupe or Refs it returns false
				  return false;
			  }
			}
		}
		else
		{
			//if list is empty the return false
			return false;
		}
		//this statement will be executed if all volumes are Refs or Ntfs
		return true;
		
	}
	
	private boolean isSessionContainsRefsVolume(RecoveryPointModel model)
	{		
		List<com.ca.arcflash.ui.client.model.RecoveryPointItemModel> listOfRecoveryPointItems = model.listOfRecoveryPointItems;
		if(listOfRecoveryPointItems != null && listOfRecoveryPointItems.size() > 0)
		{		
			for(com.ca.arcflash.ui.client.model.RecoveryPointItemModel session : listOfRecoveryPointItems)
			{
			  if((session.getVolAttr() & RecoveryPointsPanel.RefsVol) > 0)	
			  {
				  return true;
			  }
			}
		}
		else
		{
			return false;
		}
		return true;
		
	} 
	
	private void promptForCatalog(final List<RecoveryPointModel> sessions, final AsyncCallback<Boolean> callback) {
		final List<RecoveryPointModel> canCatalogSession = new ArrayList<RecoveryPointModel>();
		final List<RecoveryPointModel> pendingCatalogSessions = new ArrayList<RecoveryPointModel>(); 
		final List<RecoveryPointModel> catalogSessions = new ArrayList<RecoveryPointModel>();
		final List<RecoveryPointModel> mountSessions = new ArrayList<RecoveryPointModel>(); 
		final List<RecoveryPointModel> catalogWithRefsSessions = new ArrayList<RecoveryPointModel>();
		final List<RecoveryPointModel> noVolumeSessions = new ArrayList<RecoveryPointModel>(); 
		//check whether there is session without catalog
		for(RecoveryPointModel model : sessions) {
//			if(model.getFSCatalogStatus() == RestoreConstants.FSCAT_DISABLED && !isSessionHasAllRefsAndNtfsVolumes(model)) {
			//check whether a session can do catalog
			if(model.isCanCatalog()) {
				canCatalogSession.add(model);
			}
//			else if (model.getFSCatalogStatus() == RestoreConstants.FSCAT_PENDING && !isSessionHasAllRefsAndNtfsVolumes(model)) {
			else if (model.getFSCatalogStatus() == RestoreConstants.FSCAT_PENDING ) {
				pendingCatalogSessions.add(model);
			}
			if(model.getFSCatalogStatus() == RestoreConstants.FSCAT_FINISH){
				catalogSessions.add(model);
			}
			if(model.getFSCatalogStatus() != RestoreConstants.FSCAT_FINISH&&model.isCanMount()){
				mountSessions.add(model);
			}
			if(model.getFSCatalogStatus() == RestoreConstants.FSCAT_FINISH && isSessionContainsRefsVolume(model)){
				catalogWithRefsSessions.add(model);
			}
			if (model.getFSCatalogStatus() == RestoreConstants.FSCAT_NOTCREATE)
				noVolumeSessions.add(model);
		}
		if (!pendingCatalogSessions.isEmpty()) {
			wizard.closeValidate();
			// no disabled, but there are pending catalog sessions
			final MessageBox box = new MessageBox();
			box.addCallback(new Listener<MessageBoxEvent>() {

				@Override
				public void handleEvent(MessageBoxEvent be) {
					if(be.getButtonClicked() == box.getDialog().getButtonById(Dialog.NO)) {
						// no for exit, we cannot generated catalog for the pending sessions
						//submit catalog jobscript, if success wizard.ClosePage(false);
						callback.onSuccess(false);
					}else {
						if(catalogSessions.size()+mountSessions.size()==0){
							//not do catalog job and no available session to mount() ,warning to block
							Utils.showErrorMessage(UIContext.Constants.noCatalogAndCannotMountMessage());
						}else{
							//we need to handle the encrypted sessions
							//callback.onSuccess(true);
							checkSessionPassword(sessions, callback);
						}
					}
				}
			});
			box.setButtons(MessageBox.YESNO);
			box.setIcon(MessageBox.INFO);
			box.setMessage(UIContext.Constants.restoreSearchPendingCatalog());
			box.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
			Utils.setMessageBoxDebugId(box);
			box.show();
		} else if(!canCatalogSession.isEmpty()) {
	       //wanqi06:Close validatingBox
			wizard.closeValidate();
			final MessageBox box = new MessageBox();
			box.addCallback(new Listener<MessageBoxEvent>() {

				@Override
				public void handleEvent(MessageBoxEvent be) {
					if(be.getButtonClicked() == box.getDialog().getButtonById(Dialog.NO)) {
						//submit catalog jobscript, if success wizard.ClosePage(false);
						RpsHostModel rpsHost = backupSourcePanel.getRpsHost();
						if(rpsHost == null){						
							submitOnDemandCatalog(callback, canCatalogSession);
						}else{
							
							//BackupD2DModel selectAgent =backupSourcePanel.rpsSettingWindow.getSelectedD2D();	
							BackupRPSDestSettingsModel rpsDest = backupSourcePanel.rpsDestSettings;//
							BackupD2DModel selectAgent =backupSourcePanel.currentD2D;
							//TODO: Agent UUID, DataStoreUUID.
							OndemandInfo4RPS info = new OndemandInfo4RPS();
							info.sessions = canCatalogSession;
							info.rpsHostInfo = rpsHost;
							info.setDataStoreName(rpsDest.getRPSDataStoreName());
							info.setDataStoreUUID(rpsDest.getRPSDataStoreUUID());
							info.setDest(getSessionPath());
							info.setDestUserName(getUserName());
							info.setDestPassword(getPassword());
							info.setAgentName(selectAgent.getHostName());
							//info.setAgentUUID(agent.get);
							
							info.setVmInstanceUUID(UIContext.backupVM != null ? UIContext.backupVM.getVmInstanceUUID() : null);							
							submitFSOndemandCatalog(callback, info);
						}						
						
						
					}else {
						if(catalogSessions.size()+ mountSessions.size()==0){
							//not do catalog job and no available session to mount(), just as all dedupe volumes. warning to block
							Utils.showErrorMessage(UIContext.Constants.noCatalogAndCannotMountMessage());
						}else{
							//we need to handle the encrypted sessions
							//callback.onSuccess(true);
							checkSessionPassword(sessions, callback);
						}

					}
				}
			});
			box.setButtons(MessageBox.YESNO);
			box.setIcon(MessageBox.INFO);
			if(backupSourcePanel.getRpsHost() == null)
				box.setMessage(UIContext.Constants.restoreSearchGenCata());
			else
				box.setMessage(UIContext.Constants.restoreSearchGenCataFromRPS());
			box.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
			Utils.setMessageBoxDebugId(box);
			box.show();
		}else if(!catalogWithRefsSessions.isEmpty()){
			wizard.closeValidate();
			checkSessionPassword(sessions, callback);
		}else if (!noVolumeSessions.isEmpty()){
			wizard.closeValidate();
			final MessageBox box = new MessageBox();
			box.addCallback(new Listener<MessageBoxEvent>() {

				@Override
				public void handleEvent(MessageBoxEvent be) {
					if(be.getButtonClicked() == box.getDialog().getButtonById(Dialog.NO)) {
						callback.onSuccess(false);
					}else {
						if(catalogSessions.size()+mountSessions.size()==0){
							Utils.showErrorMessage(UIContext.Constants.noCatalogAndCannotMountMessage());
						}else{
							checkSessionPassword(sessions, callback);
						}
					}
				}
			});
			box.setButtons(MessageBox.YESNO);
			box.setIcon(MessageBox.INFO);
			box.setMessage(UIContext.Constants.restoreSearchNoVolumeCatalog());
			box.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
			Utils.setMessageBoxDebugId(box);
			box.show();
		}else
		{
			callback.onSuccess(Boolean.TRUE);
		}
	}
	
	private void sortSessionByTime() {
		Comparator<RecoveryPointModel> comp = new Comparator<RecoveryPointModel>() {
			@Override
			public int compare(RecoveryPointModel o1, RecoveryPointModel o2) {
				if(o1.getTime().getTime() > o2.getTime().getTime())
					return 1;
				else if(o1.getTime().getTime() < o2.getTime().getTime())
					return -1;
				else
					return 0;
			}
		};
		
		Collections.sort(selectedSessions, comp);
	}

	@Override
	public boolean validate(final AsyncCallback<Boolean> callback) {
		if(this.bSearchArchives) {
			if(this.txtArchiveDestination.getValue() == null || this.txtArchiveDestination.getValue().isEmpty()){
				return validationDone(callback, false, UIContext.Constants.restoresearchArchiveDestNull());
			}
		}
		
		if(this.bSearchBackups) {
			if(getSessionPath() == null || getSessionPath().isEmpty()) {
				return validationDone(callback, false, UIContext.Constants.restoreSearchBackupDestNull());
			}
			
			if(this.selectSessionRd.getValue()) {
				if(this.sessions.getCount() == 0) {
					return validationDone(callback, false, UIContext.Constants.restoreSearchFilterSession());
				}else if(this.selectedSessions.isEmpty()) {
					return validationDone(callback, false, UIContext.Constants.restoreSearchSelectSession());
				}
				sortSessionByTime();
				promptForCatalog(selectedSessions, callback);
			}else {
				if(recoveryPoints.isEmpty()) {
					AsyncCallback<RecoveryPointModel[]> acallback = new BaseAsyncCallback<RecoveryPointModel[]> () {
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							setFilterElementEnabled(true);
							validationDone(callback, false, UIContext.Constants.restoreSearchNoRPValidation());
						}

						@Override
						public void onSuccess(RecoveryPointModel[] result) {
							//wanqi06:If there is no recovery point in destination, pop up a message box.
							if(result.length == 0) {
								wizard.closeValidate();
								final MessageBox box = new MessageBox();
								box.setIcon(MessageBox.ERROR);
								box.setMessage(UIContext.Constants.restoreSearchNoRPValidation());
								box.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
								Utils.setMessageBoxDebugId(box);
								box.show();
							} else {
								for(int i = 0; i < result.length; i ++) {
									recoveryPoints.add(result[i]);
								}
								setFilterElementEnabled(true);
								promptForCatalog(recoveryPoints, callback);
							}							
						}};
						retriveAllRPS(acallback, null);
				}else {
					promptForCatalog(recoveryPoints, callback);
				}
			}
			
		}else if(this.bSearchArchives){
			callback.onSuccess(true);
		}
		
		return true;
	}
	
	public static final String VALIDATE_SOURCE_FAIL = "25769803780";
	
	public static final String VALIDATE_PATH_EXCEEDS_MAX_LENGTH = "12884901896";
	
	/*public void showUsernamePasswordDialog(final AsyncCallback<RecoveryPointModel[]> callback, final String message)
	{	
		final UserPasswordWindow dlg = new UserPasswordWindow(pathSelection.getDestination(), 
					pathSelection.getUsername(), pathSelection.getPassword());		
		dlg.setModal(true);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we) {
				//Only do this on ok
				if (dlg.getCancelled() == false)
				{	
					pathSelection.setUsername(dlg.getUsername());
					pathSelection.setPassword(dlg.getPassword());
					validateBackupSource(callback, message,null);
				}
			}
		});
		dlg.show();
	}
	
	private void validateBackupSource(final AsyncCallback<RecoveryPointModel[]> callback, final String message, final AsyncCallback<Boolean> acallback) {
		setFilterElementEnabled(false);
		commonService.validateSource(pathSelection.getDestination(), pathSelection.getDomain(), 
				pathSelection.getUserWithoutDomain(), pathSelection.getPassword(), 
				PathSelectionPanel.RESTORE_MODE, new BaseAsyncCallback<Long>() {			

			@Override
			public void onFailure(Throwable caught) {
				if(((BusinessLogicException)caught).getErrorCode().equals(VALIDATE_SOURCE_FAIL)){
					commonService.getDestDriveType(pathSelection.getDestination(), new BaseAsyncCallback<Long>()
					{
							@Override
							public void onFailure(Throwable caught) {
								if(acallback != null)
									acallback.onSuccess(false);
								super.onFailure(caught);
								setFilterElementEnabled(true);
							}
							@Override
							public void onSuccess(Long result) {
								if(acallback != null)
									acallback.onSuccess(false);
								if(result == PathSelectionPanel.REMOTE_DRIVE){
									setFilterElementEnabled(true);
									showUsernamePasswordDialog(callback, message);
								}else
								{	
									retriveAllRPS(callback, message);
				    			}
							}
					}
					);
				}else {
					super.onFailure(caught);
					setFilterElementEnabled(true);
					//if we get the Max Path Exception we have to stop the validation
					 if (((BusinessLogicException) caught).getErrorCode().equals(VALIDATE_PATH_EXCEEDS_MAX_LENGTH)) {
						if (acallback != null) 
						{
							acallback.onSuccess(Boolean.FALSE);						
										
						}
					}
				}
			}

			@Override
			public void onSuccess(Long result) {
				retriveAllRPS(callback, message);
			}
		});
	}*/
	
	private void submitOnDemandCatalog(final AsyncCallback<Boolean> callback, List<RecoveryPointModel> sessions) {
		service.submitFSOndemandCatalg(sessions, this.getSessionPath(), this
				.getUserName(), this.getPassword(), 
				UIContext.backupVM != null ? UIContext.backupVM.getVmInstanceUUID() : null,
				new BaseAsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				callback.onSuccess(false);
			}

			@Override
			public void onSuccess(Long result) {
				callback.onSuccess(false);
				Info.display(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.restoreCatalogFSJobSubmitted());
				wizard.ClosePage(true);
			}
		});
	}
	
	private void submitFSOndemandCatalog(final AsyncCallback<Boolean> callback, OndemandInfo4RPS ondemandCatalogModel) {
		service.submitFSOndemandCatalog(ondemandCatalogModel,
				new BaseAsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				callback.onSuccess(false);
			}

			@Override
			public void onSuccess(Long result) {
				callback.onSuccess(false);
				Info.display(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.restoreCatalogFSJobSubmitted());
				wizard.ClosePage(true);
			}
		});
	}
	
	private boolean validationDone(AsyncCallback<Boolean> callback, Boolean result, String message) {
		if(result) {
			return result;
		}else {
			this.popUpError(message);
			callback.onSuccess(Boolean.FALSE);
			return false;
		}
	}
	
	private void retriveAllRPS(AsyncCallback<RecoveryPointModel[]> callback, String loadMsg) {
		if(this.getSessionPath() != null && !getSessionPath().isEmpty()) {
			if(loadMsg != null)
				grid.mask(loadMsg);
			setFilterElementEnabled(false);
			DateWrapper sDateW = new DateWrapper(new Date(0));
			DateWrapper eDateW = sDateW.addYears(8029);
			String sDate = Utils.GetServerDateString(sDateW, sDateW, false);
			String eDate = Utils.GetServerDateString(eDateW, eDateW, false);
			
			RpsHostModel rpsHost = backupSourcePanel.getRpsHost();
			if(rpsHost == null){	
				service.getRecoveryPointsByServerTime(getSessionPath(), "", 
					getUserName(), getPassword(), 
					sDate, eDate, true, callback);
			}else{				
				service.getRecoveryPointsByServerTimeRPSInfo(getSessionPath(), "", 
					getUserName(), getPassword(), 
					sDate, eDate, true, getRPSInfo(), callback);
			}
		}
	}
	
	private OndemandInfo4RPS getRPSInfo(){
		BackupRPSDestSettingsModel rpsDest = backupSourcePanel.rpsDestSettings;//
		BackupD2DModel selectAgent = backupSourcePanel.currentD2D;		
		OndemandInfo4RPS info = new OndemandInfo4RPS();	
		info.rpsHostInfo = backupSourcePanel.getRpsHost();
		info.setDataStoreName(rpsDest.getRPSDataStoreName());
		info.setDataStoreUUID(rpsDest.getRPSDataStoreUUID());
		info.setDest(getSessionPath());
		info.setDestUserName(getUserName());
		info.setDestPassword(getPassword());
		info.setAgentName(selectAgent.getHostName());
		info.setAgentUUID(selectAgent.getAgentUUID());
		
		return info;
	}
	
	public List<RecoveryPointModel> getSelectRecoveryPoint() {
		return selectedSessions;
	}
	
	public ArchiveDestinationModel getArchiveDestinationModel() {
		return this.archiveDestinationInfo;
	}
	
	public boolean isSearchArchive() {
		return this.bSearchArchives;
	}
	
	public boolean isSearchBackup() {
		return this.bSearchBackups;
	}
	
	public String getArchiveDestination() {
		return txtArchiveDestination==null?null:this.txtArchiveDestination.getValue();
	}
	
	private void fireSearchedDataChanged(Integer size) {        ///D2D Lite Integration
		AppEvent event = new AppEvent(RestoreWizardContainer.onRestoreDateChanged, size);
		event.setSource(RestoreWizardContainer.PAGE_SEARCH);
		fireEvent(RestoreWizardContainer.onRestoreDateChanged, event);
	}
	
	@Override
	protected void onUnload() {
	    super.onUnload();
	    clean();
	}

	@Override
	public void show() {
		super.show();
		if(fileCopyField == null || backupField == null)
			return;
		
		if(fileCopyField.isExpanded() || backupField.isExpanded())
			fireSearchedDataChanged(1);
		else
			fireSearchedDataChanged(0);
	}
	
	public void clean() {
		filteredSessions.clear();
		sessions.removeAll();
		this.recoveryPoints.clear();
	}
	
/*	public PathSelectionPanel getBackupPath() {
		return this.pathSelection;
	}
	*/
	public Date getStartTime() {
		return startTime.getValue();
	}
	
	public Date getEndTime() {
		return endTime.getValue();
	}
	
	public boolean isSearchInAll() {
		return this.allSessionRd.getValue();
	}
	
	public Map<String, String> getEncryptedPwd() {
		return pwdsByHash;
	}
	
	private class PasswordCallback implements SessionPasswordPanel.PanelCallback {
		private AsyncCallback<Boolean> callback;
		
		public PasswordCallback(AsyncCallback<Boolean> callback) {
			this.callback = callback;
		}
		
		@Override
		public void onCancelClicked() {
			callback.onSuccess(false);
		}

		@Override
		public void onUpdatePasswordFailed(Throwable caught) {
			callback.onFailure(caught);
		}

		@Override
		public void onUpdatePasswordSuccessfull(Map<String, String> pwdsByHash) {
			thisPanel.pwdsByHash.putAll(pwdsByHash);
			callback.onSuccess(true);
		}
	}
	private void refreshTxtArchiveDestination(ArchiveCloudDestInfoModel cloudConfig) {
		if(cloudConfig.getcloudVendorType()==CloudVendorType.CACloud.getValue()){
			txtArchiveDestination.setValue(cloudConfig.getcloudBucketName().split(UIContext.CACLOUD_SEPARATOR)[0]);
		}else{
			txtArchiveDestination.setValue(cloudConfig.getcloudVendorURL());
		}
	}
	
	protected void refreshSearchContainer(){
		if(!bSearchBackups && !bSearchArchives) {
			wizard.nextButton.setEnabled(false);
		} else {
			wizard.nextButton.setEnabled(true);
		}
	}
	
	private void setFilterElementEnabled(boolean enabled){
		startTime.setEnabled(enabled);
		endTime.setEnabled(enabled);
		filterButton.setEnabled(enabled);
	}
	
	private Listener<BaseEvent> defineFileCopyChangeListener() {
		return new Listener<BaseEvent>() {
			
			@Override
			public void handleEvent(BaseEvent be) {
				if(archivePathSelectionWind == null)
					archivePathSelectionWind = new ArchivePathSelectionWindow();
				archivePathSelectionWind.addWindowListener(new WindowListener(){
					public void windowHide(WindowEvent we) {
						if (archivePathSelectionWind.getCancelled() == false)
						{
							archiveDestinationInfo = archivePathSelectionWind.getArchiveDestinationModel();
							
							switch (archivePathSelectionWind.NextAction) {
							case ArchivePathSelectionWindow.ARCHIVE_READ_EXISTING_CATALOG:
							{
								if(archiveDestinationInfo.getArchiveToDrive())
								{
									txtArchiveDestination.setValue(archiveDestinationInfo.getArchiveDiskDestInfoModel().getArchiveDiskDestPath());
								}
								else if(archiveDestinationInfo.getArchiveToCloud())
								{
									refreshTxtArchiveDestination(archiveDestinationInfo.getCloudConfigModel());							
								}
								break;
							}
							case ArchivePathSelectionWindow.ARCHIVE_SYNC_CATALOG:
								wizard.submitD2DArchiveCatalogSyncJob(archiveDestinationInfo);
								break;
							default:
								break;
							}
						}
				  }
				});
				archivePathSelectionWind.refresh(archiveDestinationInfo);
				archivePathSelectionWind.setModal(true);
				archivePathSelectionWind.show();
				bSearchArchives = true;
				thisPanel.refreshSearchContainer();
			}
		};
	}

	@Override
	public void onDefaultSourceInitialized(boolean succeed) {
		if(succeed){		
			bSearchBackups = true;
			backupField.expand();
			refreshSearchContainer();
		}else {
			bSearchBackups = false;
			backupField.collapse();
		}
	}

	@Override
	public void onRestoreSourceTypeChanged() {
		
	}
	
	public RpsHostModel getRPSHost(){
		return backupSourcePanel.getRpsHost();
	}
	
	public String getRpsDataStore(){
		return backupSourcePanel.getRpsDataStore();
	}
	
	public String getRpsPolicy(){
		return backupSourcePanel.getRpsPolicy();
	}
	
	public String getRpsDSDisplayName(){
		return backupSourcePanel.getRpsDataStoreDisplayName();
	}
	
	private class InternalHighlightedDateField extends HighlightedDateField {
		private Set<String> backupSetFlag = new HashSet<String>();
		
		@Override
		protected HighlightedDateMenu cretaeHighlightedDateMenu() {
			return new HighlightedDateMenu() {
				@Override
				protected HighlightedDatePicker createHighlightedDatePicker() {
					return new HighlightedDatePicker() {
						@Override
						public void highlightSelectedDates() {
							cells = Util.toElementArray(el().select("table.x-date-inner tbody td"));

							for (int i = 0; i < cells.length; i++) {
								El cellEl = new El(cells[i]);
								String date = cellEl.firstChild().dom.getPropertyString("dateValue");

								if (selectedDates.contains(date)) {
									cellEl.addStyleName("x-date-highlighted");
								} else if (neighbourDates.contains(date)) {
									cellEl.addStyleName("x-date-highlighted-grey");
								}

								if (backupSetFlag.contains(date)) {
									cellEl.addStyleName("backupSetStartDate");
								}
							}
						}
						
						@Override
						public void clearAll() {
							  super.clearAll();
							  backupSetFlag.clear();
						  }
					};
				};
			};
		}
		
		public void addBackupSetFlag(Date d) {
			DateWrapper dr = new DateWrapper(d);
			int year = dr.getFullYear();
			int month = dr.getMonth();
			int day = dr.getDate();
			String dateStr = year + "," + month + "," + day;
			backupSetFlag.add(dateStr);
		}
	}
}

