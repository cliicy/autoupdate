package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.EnhancedHightlightedDataPicker;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.FormatUtil;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupRPSDestSettingsModel;
import com.ca.arcflash.ui.client.model.BackupStatusModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.ca.arcflash.ui.client.model.DiskModel;
import com.ca.arcflash.ui.client.model.ExchVersion;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.OndemandInfo4RPS;
import com.ca.arcflash.ui.client.model.RecoveryPointItemModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.TimeRangeModel;
import com.ca.arcflash.ui.client.model.VAppBackupVMRecoveryPointModelWrapper;
import com.ca.arcflash.ui.client.model.VCloudVirtualDataCenterModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.ca.arcflash.ui.client.model.VMNetworkConfigInfoModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.restore.ExtEditorTreeGrid;
import com.ca.arcflash.ui.client.restore.IRestoreSourceListener;
import com.ca.arcflash.ui.client.restore.PagingContext;
import com.ca.arcflash.ui.client.restore.RecoveryPointsPanel;
import com.ca.arcflash.ui.client.restore.RestoreConstants;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.ca.arcflash.ui.client.restore.RestoreSourcePanel;
import com.ca.arcflash.ui.client.restore.RestoreUtil;
import com.ca.arcflash.ui.client.restore.RestoreValidator;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.ca.arcflash.ui.client.restore.SQLRestoreOptionsPanel;
import com.ca.arcflash.ui.client.restore.SessionPasswordPanel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class VMRecoveryPointsPanel extends LayoutContainer implements
		RestoreValidator, IRestoreSourceListener {

	private EnhancedHightlightedDataPicker picker;
	final LoginServiceAsync service = GWT.create(LoginService.class);
	final CommonServiceAsync commonService = GWT.create(CommonService.class);
	private boolean isDefaultSourceInitialized = false; // if the default restore source path is initialized. If not, no need to refresh the calendars.
	private ComboBox<BackupVMModel> vmList;
	private BackupVMModel selectedBackupVMModel;
	private ListStore<RecoveryPointModel> store;
	private TreeStore<GridTreeNode> treeStore;
	private VMRecoveryPointsPanel thisPanel;
	private Grid<RecoveryPointModel> grid;
	private Grid<TimeRangeModel> timeGrid;
	private TreeLoader<GridTreeNode> loader;
	private EditorTreeGrid<GridTreeNode> tree;
	private Integer selectedSessionID;
	private boolean isRestoreManager = true;
	private HashMap<GridTreeNode, FlashCheckBox> table = new HashMap<GridTreeNode, FlashCheckBox>();
	private HashMap<GridTreeNode, RecoveryPointItemModel> rootItemMap = new HashMap<GridTreeNode, RecoveryPointItemModel>();	
	public static final String GUID_SQLWRITER = "SqlServerWriter";
	public static final String GUID_EXCHANGE_WRITER = "Microsoft Exchange Writer";
	public static final String GUID_EXCHANGE_REPLICA_WRITER = "Microsoft Exchange Replica Writer";
	public static final String EXCHANGE_Writer_Prexix = GUID_EXCHANGE_WRITER
			+ " ";
	public static final String EXCHANGE_Replica_Writer_Prexix = GUID_EXCHANGE_REPLICA_WRITER
			+ " ";
	public static final String GUID_EXCHANGE_2003 = EXCHANGE_Writer_Prexix
			+ ExchVersion.Exch2003.getVersion();
	public static final String GUID_EXCHANGE_2007 = EXCHANGE_Writer_Prexix
			+ ExchVersion.Exch2007.getVersion();
	public static final String GUID_EXCHANGE_REPLICA_2007 = EXCHANGE_Replica_Writer_Prexix
			+ ExchVersion.Exch2007.getVersion();
	public static final String GUID_EXCHANGE_2010 = EXCHANGE_Writer_Prexix
			+ ExchVersion.Exch2010.getVersion();
	public static final String GUID_EXCHANGE_REPLICA_2010 = EXCHANGE_Replica_Writer_Prexix
			+ ExchVersion.Exch2010.getVersion();
	public static final String GUID_EXCHANGE_2013 = EXCHANGE_Writer_Prexix
			+ ExchVersion.Exch2013.getVersion();
	public static final String GUID_EXCHANGE_REPLICA_2013 = EXCHANGE_Replica_Writer_Prexix
			+ ExchVersion.Exch2013.getVersion();
	public static final String GUID_EXCHANGE_2016 = EXCHANGE_Writer_Prexix
			+ ExchVersion.Exch2016.getVersion();
	public static final String GUID_EXCHANGE_REPLICA_2016 = EXCHANGE_Replica_Writer_Prexix
			+ ExchVersion.Exch2016.getVersion();

	// private static int MIN_WIDTH = 90;
	public final static int Unknown = -1;
	public final static int Full = 0;
	public final static int Incremental = 1;
	public final static int Resync = 2;
	private ExchVersion exchVer = null;
	private Date serverDate;
	private Date previousSelectedMonth = null;
	private int timeRangeCount[] = new int[4];

	public static final int BootVol = 0x00000001;
	public static final int SysVol = 0x00000002;
	
	// 2015-04-08 fix issue 214818 caused by DST problem
//	private String currentRangeStartTime;
//	private String currentRangeEndTime;
	private D2DTimeModel currentRangeStartTime;
	private D2DTimeModel currentRangeEndTime;
	
	private RecoveryPointModel selectedSession;
	private String selectedSessionEncryptionKey;
	private int isGotBackupVMAdaptersFlag = 0;
	private int isGotFlasResourceFlag = 0;
	private RestoreSourcePanel sourcePanel;
	
	private boolean isVMModelList = false;
	
	public TreeStore<GridTreeNode> getTreeStore() {
		return treeStore;
	}

	GridCellRenderer<RecoveryPointModel> typeRenderer = new GridCellRenderer<RecoveryPointModel>() {
		public String render(RecoveryPointModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<RecoveryPointModel> store,
				Grid<RecoveryPointModel> grid) {
			String type = UIContext.Constants.backupTypeUnknown();
			switch (model.getBackupType()) {
			case Full:
				type = UIContext.Constants.backupTypeFullText();
				break;
			case Incremental:
				type = UIContext.Constants.backupTypeIncrementalText();
				break;
			case Resync:
				type = UIContext.Constants.backupTypeResyncText();
				break;
			}
			return type;
		}
	};

	public VMRecoveryPointsPanel(Window w) {
		thisPanel = this;
	}

	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_recover()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		if (isRestoreManager) {
			label.setValue(UIContext.Constants.vmRecoverButton());
		} else {
			label.setValue(UIContext.Constants.manageRecoveryPoints());
		}
		label.setStyleName("restoreWizardTitle");
		container.add(label);
		return container;
	}
	
	public void highlightMostRecentMonthRPDates(final boolean value) {
		if (picker == null) {
			return;
		}

		picker.clearAll();
		picker.setEnabled(false);
		clearRecoveryPoints();
		resetTimeRange();
		refreshTimeRange();

		String destination = getSessionPath();
		if (destination == null) {
			picker.setEnabled(true);
			return;
		}

		String domain = "";
		String pwd = "";
		String userName = getUserName();
		if (userName != null) {
			int index = userName.indexOf('\\');
			if (index > 0) {
				domain = userName.substring(0, index);
				userName = userName.substring(index + 1);
			}
			pwd = getPassword();
		}

		DateWrapper startDate = new DateWrapper(new Date(0));
		DateWrapper endDate = startDate.addYears(8029);

		// 2015-04-08 fix issue 214818 caused by DST problem
//		String asStartDate = Utils.GetServerDateString(startDate, startDate, false);
//		String asEndDate = Utils.GetServerDateString(endDate, endDate, false);
		D2DTimeModel asStartDate = Utils.getD2DTime(startDate, false);
		D2DTimeModel asEndDate = Utils.getD2DTime(endDate, false);

		service.getRecoveryPointsByServerTime(destination, domain, userName,
				pwd, asStartDate, asEndDate, false,
				new BaseAsyncCallback<RecoveryPointModel[]>() {

					@Override
					public void onFailure(Throwable caught) {
						//Add a param to decide whether pop error dialog or not, added by wanqi06
						if(!value) {
							super.onFailure(caught);							
						}	
						picker.setEnabled(true);
					}

					@Override
					public void onSuccess(RecoveryPointModel[] result) {
						Date latestHighlightedDate = null;
						int mostRecentMonth = -1;
						if (result == null || result.length == 0) {
							picker.setEnabled(true);
							picker.showToday();
						} else {
							for (int i = 0; i < result.length; i++) {
								Date serverDate = RestoreUtil.RecPointModel2ServerDate(result[i]);
								if (mostRecentMonth == -1) {
									mostRecentMonth = serverDate.getMonth();
									previousSelectedMonth = formateToMonth(serverDate);
								}

								picker.addSelectedDate(serverDate, result[i].getBackupStatus() == BackupStatusModel.Finished, serverDate.getMonth() == mostRecentMonth);
								
								if(result[i].getBackupSetFlag() > 0) {
									picker.addBackupSetFlag(serverDate);
								}
								
								if ((latestHighlightedDate == null || latestHighlightedDate.before(serverDate))) {
									latestHighlightedDate = serverDate;
								}
							}

							if (latestHighlightedDate != null) {							
								picker.setValue(latestHighlightedDate, true);

								DateWrapper beginDateWrap = new DateWrapper(
										picker.getValue());
								Date endDate = beginDateWrap.addDays(1)
										.asDate();
								DateWrapper endDateWrap = new DateWrapper(
										endDate);

								// 2015-04-08 fix issue 214818 caused by DST problem
//								String strSvrBeginDate = GetServerDateString(
//										beginDateWrap, false);
//
//								String strSvrEndDate = GetServerDateString(
//										endDateWrap, false);
//
//								thisPanel.refreshRecoveryPointDataByServerTime(
//										strSvrBeginDate, strSvrEndDate);
								D2DTimeModel strSvrBeginDate = Utils.getD2DTime(
										beginDateWrap, false);

								D2DTimeModel strSvrEndDate = Utils.getD2DTime(
										endDateWrap, false);

								thisPanel.refreshRecoveryPointDataByServerTime(
										strSvrBeginDate, strSvrEndDate);
							}
						}

						picker.repaint();
						picker.highlightSelectedDates();
					}

				});

	}

	private LayoutContainer renderSourceSection() {
		sourcePanel = new VMRestoreSourcePanel();
		Listener<BaseEvent> changeListener = new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				thisPanel.picker.clearAll();
				String domain = "";
				String pwd = "";
				String userName = getUserName();
				if (userName != null) {
					int index = userName.indexOf('\\');
					if (index > 0) {
						domain = userName.substring(0, index);
						userName = userName.substring(index + 1);
					}
					pwd = getPassword();
				}
				final String destination = getSessionPath();
				service.checkVMDestination(destination, domain, userName, pwd,
						new BaseAsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								vmList.clear();
								selectedBackupVMModel = null;
								highlightMostRecentMonthRPDates();
								isVMModelList = false;
							}

							@Override
							public void onSuccess(Boolean result) {
								String domain = "";
								String pwd = "";
								String userName = getUserName();
								if (userName != null) {
									int index = userName.indexOf('\\');
									if (index > 0) {
										domain = userName.substring(0, index);
										userName = userName.substring(index + 1);
									}
									pwd = getPassword();
								}
								if (result) {
									isVMModelList = false;
									service.getBackupVMModel(
											destination,
											domain,
											userName,
											pwd,
											new BaseAsyncCallback<BackupVMModel>() {
												@Override
												public void onFailure(Throwable caught) {
													super.onFailure(caught);
												}

												@Override
												public void onSuccess(BackupVMModel result) {
													if (result != null	&& result.getVmInstanceUUID() != null) {
														vmList.getStore().removeAll();
														vmList.getStore().add(result);
														vmList.setValue(result);
														//fix issue 88792
														selectedBackupVMModel = result;
														if(UIContext.backupVM != null) {
															selectedBackupVMModel.setVmInstanceUUID(UIContext.backupVM.getVmInstanceUUID());
														}
														UIContext.backupVM = selectedBackupVMModel;
													} else {
														vmList.clear();
														selectedBackupVMModel = null;
														highlightMostRecentMonthRPDates();
													}
												}
											});
								} else {
									isVMModelList = true;
									service.getBackupVMModelList(
											destination,
											domain,
											userName,
											pwd,
											new BaseAsyncCallback<List<BackupVMModel>>() {
												@Override
												public void onFailure(Throwable caught) {
													super.onFailure(caught);
												}

												@Override
												public void onSuccess(
														List<BackupVMModel> result) {
													if (result != null && result.size() > 0) {
														vmList.getStore().removeAll();
														selectedBackupVMModel = result.get(0);
														//setSessionPath(selectedBackupVMModel.getDestination());
														//fix for defect: 209759
														setSessionPath(selectedBackupVMModel.getBrowseDestination());
														vmList.getStore().add(result);
														vmList.setValue(result.get(0));
													} else {
														vmList.clear();
														selectedBackupVMModel = null;
														highlightMostRecentMonthRPDates();
													}
												}
											});
								}
							}
						});

			}
		};
		sourcePanel.setPathChangeListener(changeListener);
		sourcePanel.setSourceListener(thisPanel);
		getDefaultSourceValue();
		return sourcePanel;
	}
	
	
	private LayoutContainer renderVMList() {
		LayoutContainer container = new LayoutContainer();

		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		tl.setColumns(2);
		tl.setCellPadding(0);
		tl.setCellSpacing(2);
		container.setLayout(tl);
	
		TableData td = new TableData();
		td.setWidth("187px");
		LabelField label = new LabelField();
		label.addStyleName("restoreWizardSubItemDescription");
		label.setValue(UIContext.Constants.selectVirtualMachine());
		container.add(label,td);
		
		vmList = new ComboBox<BackupVMModel>();
		vmList.ensureDebugId("B3F9956D-C78C-4344-B619-B7E05FA315F6");		
		vmList.setWidth(360);
		vmList.setDisplayField("vmName");
		vmList.setEmptyText(UIContext.Constants.noVMFound());
		Utils.addToolTip(vmList, UIContext.Constants.selectVirtualMachineToolTip());
		vmList.setEditable(false);
		vmList.setTriggerAction(TriggerAction.ALL);
		vmList.setStore(initVMListStore());
		vmList.addSelectionChangedListener(new SelectionChangedListener<BackupVMModel>(){

			@Override
			public void selectionChanged(SelectionChangedEvent<BackupVMModel> se) {
				selectedBackupVMModel = se.getSelectedItem();
				highlightMostRecentMonthRPDates();		
				if(isVMModelList) {
					//setSessionPath(selectedBackupVMModel.getDestination());
					//fix for defect: 209759
					setSessionPath(selectedBackupVMModel.getBrowseDestination());
				}
			}
			
		});
		container.add(vmList);


		return container;
	}

	public ListStore<BackupVMModel> initVMListStore() {
		ListStore<BackupVMModel> vmListStore = new ListStore<BackupVMModel>();
		return vmListStore;
	}
	
	private Widget renderTableSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(0);
		container.setLayout(tableLayout);

		LayoutContainer leftContainer = new LayoutContainer();
		leftContainer.setLayout(new TableLayout(1));
		picker.addListener(Events.Select, new Listener<ComponentEvent>() {

			public void handleEvent(ComponentEvent be) {
				if (!isDefaultSourceInitialized)
				{
					return;
				}
				Date newDate = picker.getValue();
				highlightDatesIfMonthChange(newDate, false);
				previousSelectedMonth = formateToMonth(newDate);
				// Date startDate =
				// Utils.serverTimeToLocalTime(picker.getValue());
				DateWrapper startDR = new DateWrapper(newDate);
				DateWrapper endDR = startDR.addDays(1);
				// 2015-04-08 fix issue 214818 caused by DST problem
//				String startDate = GetServerDateString(startDR, false);
//				String endDate = GetServerDateString(endDR, false);
				D2DTimeModel startDate = Utils.getD2DTime(startDR, false);
				D2DTimeModel endDate = Utils.getD2DTime(endDR, false);
				thisPanel.refreshRecoveryPointDataByServerTime(startDate,
						endDate);
			}

		});
		//picker.setStyleAttribute("margin", "0px, 4px, 6px, 0px");
		leftContainer.add(picker);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setId("rangeString");
		column.setHeaderHtml(UIContext.Constants.restoreTimeRangeColumn());
		column.setWidth(173);
		
		column.setRenderer(new GridCellRenderer(){

			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore store, Grid grid) {
				
				TimeRangeModel trModel = (TimeRangeModel)model;
				StringBuffer sb = new StringBuffer();
				
				sb.append(trModel.getRange());
				
				if (trModel.getCount() != null && trModel.getCount() > 0)
				{
					sb.append("   (");
					sb.append(trModel.getCount());
					sb.append(")");
				}
				
				return sb.toString();
			}
			
		});
		
		configs.add(column);

		columnModel = new ColumnModel(configs);
		ListStore<TimeRangeModel> rangeStore = new ListStore<TimeRangeModel>();
		rangeStore.setStoreSorter(new StoreSorter<TimeRangeModel>(){

			@Override
			public int compare(Store<TimeRangeModel> store, TimeRangeModel m1, TimeRangeModel m2, String property) {
/*				if (m1 == null || m1.getStartDate() == null)
					return -1;
				if (m2 == null || m2.getStartDate() == null)
					return 1;
				return m1.getStartDate().compareTo(m2.getStartDate());*/
				if(m1 == null)
					return -1;
				else 
					return m1.compare(m2);
			}
			
		});
		
		//6 hour intervals	
		// 2015-04-08 fix issue 214818 caused by DST problem
//		for (int i = 0; i < 24; i = i + 6)
//		{
//			DateWrapper startDate = new DateWrapper();
//			startDate = startDate.clearTime();			
//			startDate = startDate.addHours(i);
//			DateWrapper endDate = startDate.addHours(6).addSeconds(-1);
//			//check whether it's DST start in UI side
//			if(startDate.getHours() != i){
//				startDate = startDate.addDays(Utils.MAKEUP_DST_STARTS);
//				startDate = startDate.clearTime();
//				startDate = startDate.addHours(i);
//				endDate = startDate.addHours(6).addSeconds(-1);
//			}else if(endDate.getHours() - startDate.getHours() != 2) {
//				endDate = endDate.addDays(Utils.MAKEUP_DST_STARTS);
//				endDate = endDate.clearTime();
//				endDate = endDate.addHours(i+6).addSeconds(-1);
//			}
//			TimeRangeModel model = new TimeRangeModel();
//			model.setStartDate(startDate.asDate());
//			model.setEndDate(endDate.asDate());
//			String strRange = Utils.formatShorTime(startDate.asDate()) + " - " +  Utils.formatShorTime(endDate.addSeconds(1).asDate());			
//			model.setRange(strRange);			
//			rangeStore.add(model);
//		}
		int duration = 24 / TimeRangeModel.TIME_PERIODS;
		for (int i = 0; i < 24; i += duration) {			
			DateWrapper startDate = new DateWrapper();
			startDate = startDate.clearTime();			
			startDate = startDate.addHours(i);			
			DateWrapper endDate = startDate.addHours(duration).addSeconds(-1);
			TimeRangeModel model = new TimeRangeModel();
			model.setStartDate(startDate.asDate());
			model.setEndDate(endDate.asDate());
			String strRange = Utils.formatShorTime(startDate.asDate()) + " - " +  Utils.formatShorTime(endDate.addSeconds(1).asDate());			
			model.setRange(strRange);			
			rangeStore.add(model);
		}
		
		timeGrid = new Grid<TimeRangeModel>(rangeStore, columnModel);
		timeGrid.setStyleAttribute("margin-top", "5px");
		//timeGrid.setHeight(115);
		timeGrid.setHeight(170);
		//timeGrid.setWidth(177);
		timeGrid.setWidth(181);
		timeGrid.setBorders(false);
		//timeGrid.setBorders(true);
		//timeGrid.setAutoExpandColumn("rangeString");
		timeGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		timeGrid.getSelectionModel().addListener(Events.SelectionChange,
				new Listener<SelectionChangedEvent<TimeRangeModel>>() {
					public void handleEvent(
							SelectionChangedEvent<TimeRangeModel> e) {

						// Filter the list
						TimeRangeModel selection = e.getSelectedItem();
						if (selection != null) {
							timeGrid.mask(UIContext.Constants.restoreLoading());
							// 2015-04-08 fix issue 214818 caused by DST problem
//							final DateWrapper selectedDate = new DateWrapper(picker
//									.getValue()).clearTime();
//
//							final DateWrapper startTime = new DateWrapper(selection
//									.getStartDate());
//
//							final DateWrapper endTime = new DateWrapper(selection
//									.getEndDate());
//
//							final String strBegin = Utils.GetServerDateString(selectedDate, startTime,true);
//							//First check whether the endTime is server DST start time by check whether 
//							//the timezone offset is same between endTime and the hour before endTime
//							commonService.getServerTimezoneOffset(selectedDate.getFullYear(), 
//									selectedDate.getMonth(), selectedDate.getDate(), 
//									endTime.getHours(), endTime.getMinutes(), new BaseAsyncCallback<Long>(){
//										@Override
//										public void onFailure(Throwable caught) {
//											String strEnd = Utils.GetServerDateString(selectedDate, 
//													endTime, true);
//											currentRangeStartTime = strBegin;
//											currentRangeEndTime = strEnd;
//											// filter the other grid by this
//											thisPanel
//													.refreshRecoveryPointFromTimeRangeByServerTime(
//															strBegin, strEnd);
//										}
//										@Override
//										public void onSuccess(Long result) {
//											final Long aa = result;
//											commonService.getServerTimezoneOffset(selectedDate.getFullYear(), 
//													selectedDate.getMonth(), selectedDate.getDate(), 
//													endTime.getHours() - 1, endTime.getMinutes(), new BaseAsyncCallback<Long>(){
//
//														@Override
//														public void onFailure(Throwable caught) {
//															refresh(aa, UIContext.serverVersionInfo.getTimeZoneOffset(), 
//																	selectedDate, endTime, strBegin);
//														}
//
//														@Override
//														public void onSuccess(Long result1) {
//															refresh(aa, result1, selectedDate, endTime, strBegin);
//														}
//												
//											});
//										}
//								
//							});
							D2DTimeModel begin = Utils.getD2DTime(picker.getValue(), selection.getStartDate());
							D2DTimeModel end = Utils.getD2DTime(picker.getValue(), selection.getEndDate());
							currentRangeStartTime = begin;
							currentRangeEndTime = end;
							
							thisPanel.refreshRecoveryPointFromTimeRangeByServerTime(begin, end);
						}

					}
				});	
		leftContainer.add(timeGrid);	
		
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setVerticalAlign(VerticalAlignment.TOP);
		container.add(leftContainer, td);
		
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setVerticalAlign(VerticalAlignment.TOP);
		container.add(renderMainTableSection(), td);

		return container;
	}

	// 2015-04-08 fix issue 214818 caused by DST problem
//	private void refresh(long newOffset, long oldOffset, DateWrapper selectedDate, 
//			DateWrapper endTime, String strBegin){
//		currentRangeStartTime = strBegin;
//		if(newOffset > oldOffset) {
//			String strEnd = Utils.GetServerDateString(selectedDate, 
//					endTime.addHours(-1), true);
//			currentRangeEndTime = strEnd;
//			// filter the other grid by this
//			thisPanel
//					.refreshRecoveryPointFromTimeRangeByServerTime(
//							strBegin, strEnd);
//		}else {
//			//same one
//			String strEnd = Utils.GetServerDateString(selectedDate, 
//					endTime, true);
//			currentRangeEndTime = strEnd;
//			// filter the other grid by this
//			thisPanel
//					.refreshRecoveryPointFromTimeRangeByServerTime(
//							strBegin, strEnd);
//		}
//	}
	
	private Date formateToMonth(Date newDate) {
		DateWrapper wrapper = new DateWrapper(newDate);
		wrapper = new DateWrapper(wrapper.getFullYear(), wrapper.getMonth(), 1);
		return wrapper.asDate();
	}

	private LayoutContainer renderMainTableSection() {
		final LayoutContainer container = new LayoutContainer();
		container.setLayout(new BorderLayout());
		container.setSize(475, 400);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("encryptionType");
//		column.setHeader("");
		GridCellRenderer<RecoveryPointModel> encryptRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				IconButton iconButton = null;
				if(model.isEncrypted()){
					iconButton = getIncryptionImage("recoverypoint_encryption_icon");
					Utils.addToolTip(iconButton, UIContext.Constants.restoreBrowseRecoveryPointsToolTipEncrytion());
					return iconButton;
				}else {
					iconButton = getIncryptionImage("recoverypoint_noencryption_icon");
					Utils.addToolTip(iconButton, UIContext.Constants.restoreBrowseRecoveryPointsToolTipNoEncrytion());
					return iconButton;
				}
			}

			private IconButton getIncryptionImage(String style) {
//				Image image = UIContext.IconBundle.logWarning().createImage();
				IconButton image = new IconButton(style);
				image.ensureDebugId("118DE2B2-2D09-4f8c-A31B-F16132E99D11");
				image.setWidth(20);
				return image;
			}
			
		};
		column.setRenderer(encryptRenderer);
		column.setWidth(24);
		column.setMenuDisabled(true);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("Time");
		column.setHeaderHtml(UIContext.Constants.restoreTimeColumn());
		GridCellRenderer<RecoveryPointModel> timeRenderer = new GridCellRenderer<RecoveryPointModel>() {
			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				// Recovery Point table - Modified time column.
				// 2015-04-23 fix issue 219024 caused by DST problem
//				Date datetime = model.getTime();
//				if (datetime == null) {
//					return "";
//				}
//
//				if (model.getTimeZoneOffset() != null) {
//					TimeZone serverTZ = TimeZone.createTimeZone(model
//							.getTimeZoneOffset()
//							/ (-1000 * 60));
//					return FormatUtil.getTimeFormat().format(datetime, serverTZ);
//				}
//
//				return Utils.formatTimeToServerTime(datetime);
				D2DTimeModel datetime = model.getD2DTime();
				if (datetime == null) {
					return "";
				}
				return FormatUtil.getTimeFormat().format(Utils.convertD2DTime(datetime));
			}
		};
		column.setRenderer(timeRenderer);
		column.setWidth(120);
		column.setMenuDisabled(true);
		configs.add(column);
		
		GridCellRenderer<RecoveryPointModel> schedTypeRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				return Utils.schedFlag2String(model.getPeriodRetentionFlag());
			}
			
		};
		configs.add(Utils.createColumnConfig("Type", UIContext.Constants.homepageRecentBackupColumnSchedTypeHeader(), 80, schedTypeRenderer));

		column = new ColumnConfig();
		column.setId("backupType");
		column.setHeaderHtml(UIContext.Constants.restoreTypeColumn());
		column.setWidth(100);
		column.setRenderer(typeRenderer);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("Name");
		column.setHeaderHtml(UIContext.Constants.restoreNameColumn());
		column.setRenderer(new GridCellRenderer<RecoveryPointModel>() {
			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				String name = model.getName();
				if(name != null){
					return UIContext.escapeHTML(name);
				}
			
				return "";
			}
		});
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("CatalogStatus");
		column.setHeaderHtml(UIContext.Constants.restoreCatalogStatusColumn());
		column.setMenuDisabled(true);
		column.setWidth(80);
		column.setRenderer(new GridCellRenderer<RecoveryPointModel> () {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				
				return RestoreConstants.getFSCatalogStatusMsgForVSphere(model.getFSCatalogStatus());
				
			}
			
		});
		configs.add(column);
		
		columnModel = new ColumnModel(configs);

		store = new ListStore<RecoveryPointModel>();

		grid = new Grid<RecoveryPointModel>(store, columnModel);
		grid.setAutoExpandColumn("Name");
		grid.setAutoExpandMax(5000);
		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getSelectionModel().addListener(Events.SelectionChange,
				new Listener<SelectionChangedEvent<RecoveryPointModel>>() {
					public void handleEvent(
							SelectionChangedEvent<RecoveryPointModel> e) {
						clearTree();
						final RecoveryPointModel sel = e.getSelectedItem();
						if (sel != null) {
							if (BackupVMModel.Type.VMware_VApp.ordinal() == selectedBackupVMModel.getVMType()) {
								handleVAppRecoveryPointSelectionChange(sel);
								return;
							} else 	if (sel.listOfRecoveryPointItems == null
									|| sel.listOfRecoveryPointItems.size() == 0) {
								String domain = "";
								String pwd = "";
								String user = getUserName();
								if (user != null) {
									int index = user.indexOf('\\');
									if (index > 0) {
										domain = user.substring(0, index);
										user = user.substring(index + 1);
									}
									pwd = getPassword();
								} else {
									user = "";
								}
								String subPath = sel.getPath();

								service
										.getRecoveryPointItems(
												selectedBackupVMModel.getBrowseDestination(),
												domain,
												user,
												pwd,
												subPath,
												new BaseAsyncCallback<List<RecoveryPointItemModel>>() {

													@Override
													public void onFailure(
															Throwable caught) {
														super.onFailure(caught);
														store.removeAll();
														refreshRecoveryPointFromTimeRangeByServerTime(currentRangeStartTime, 
																currentRangeEndTime);
													}

													@Override
													public void onSuccess(
															List<RecoveryPointItemModel> result) {
														sel.listOfRecoveryPointItems = result;
														thisPanel.PopulateTreeGrid(sel);
														setDiskToBackupVM(sel);
													}
												});

							} else {
								thisPanel.PopulateTreeGrid(e.getSelectedItem());
								setDiskToBackupVM(e.getSelectedItem());
							}
						}

					}
				});


		/***********************/
		
		CheckColumnConfig checkColumn = new CheckColumnConfig("checked", "", 40);
		checkColumn.setHidden(true);

		ColumnConfig name = new ColumnConfig("displayName", UIContext.Constants.restoreNameColumn(), 100);
		name.setMenuDisabled(true);
		name.setRenderer(new WidgetTreeGridCellRenderer<ModelData>() {

			@Override
			public Widget getWidget(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				
				LayoutContainer lc = new LayoutContainer();
				
				TableLayout layout = new TableLayout();
				layout.setColumns(2);
				lc.setLayout(layout);
				//final FlashCheckBox fcb = new FlashCheckBox();

				final GridTreeNode node = (GridTreeNode) model;

				if (exchVer != null) {
					if (CatalogModelType.NonSelectExchangeTypes.contains(node
							.getType())) {
						node.setSelectable(false);
					}
				}

				//if this is root item(volume), we will check if this volume is refs volume. If yes and this server is not windows 2012, this node will be not selectable
				if(node.getParentID() == -1){
					RecoveryPointItemModel rpm = rootItemMap.get(node);
					if(rpm !=null && rpm.getVolAttr()!=null){
						int volAttr = rpm.getVolAttr();
						if((volAttr & RecoveryPointsPanel.RefsVol) > 0 && !UIContext.serverVersionInfo.isWin8()){
							node.setSelectable(false);
							node.setIsRefs(true);
						}
					}
				}
				
				IconButton image = getNodeIcon(node);
				if(image != null)
					lc.add(image);

				LabelField lf = new LabelField();
				lf.setValue(node.getDisplayName());
				lc.add(lf);

				return lc;
			}

		});
		
		ColumnConfig date = new ColumnConfig("date", UIContext.Constants.restoreDateModifiedColumn(), 120);
		date.setRenderer(new GridCellRenderer<BaseModelData>() {
			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex, 
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				try {
					if (model != null) {
						Date dateModifed = ((GridTreeNode)model).getDate();
						return Utils.formatDateToServerTime(dateModifed,
								((GridTreeNode)model).getServerTZOffset() != null?
										((GridTreeNode)model).getServerTZOffset() : 0);
					}
				} catch (Exception e) {
					e.printStackTrace();
                      System.out.println("Error:" + e.getMessage());
				}
				return "";
			}
		});
		date.setMenuDisabled(true);
		
		ColumnConfig size = new ColumnConfig("size", UIContext.Constants.restoreSizeColumn(), 100);
		size.setMenuDisabled(true);
		size.setAlignment(HorizontalAlignment.RIGHT);
		size.setRenderer(new GridCellRenderer<BaseModelData>() {

			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				try {
					if (model != null
							&& (((GridTreeNode) model).getSize()!=null)) {
						GridTreeNode node = (GridTreeNode)model;
						if ( node.getType() != CatalogModelType.OT_VSS_FILESYSTEM_WRITER && node.getSize() == 0)
							return "";
						
						Long value = ((GridTreeNode) model).getSize();
						String formattedValue = Utils.bytes2String(value);
						return formattedValue;
					}
				} catch (Exception e) {

				}

				return "";
			}

		});

		treeColModel = new ColumnModel(Arrays.asList(checkColumn, name,
				date, size));

		RpcProxy<List<GridTreeNode>> proxy = new RpcProxy<List<GridTreeNode>>() {
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<GridTreeNode>> callback) {
				loadCatalogTree((GridTreeNode) loadConfig, callback);
			}
		};

		loader = new BaseTreeLoader<GridTreeNode>(proxy) {
			public boolean hasChildren(GridTreeNode parent) {
				Integer type = parent.getType();
				if (type != null && type == CatalogModelType.File) {
					return false;
				} else {
					return true;
				}
			}
		};

		StoreSorter<GridTreeNode> sorter = new StoreSorter<GridTreeNode>(){
			private int fileNameCompare( GridTreeNode m1, GridTreeNode m2 )
			{
				int r = 0;
				if( m1.getDisplayName() == null )
				{
					if( m2.getDisplayName() == null )
						return 0;
					else
						return -1;
				}
				else
				{
					if( m2.getDisplayName() == null )
						return 1;
					else
					{
						r = m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName());
						if( r == 0 )
							r = m1.getDisplayName().compareTo(m2.getDisplayName());
						return r;
					}
				}
			}
			public int compare(Store<GridTreeNode> store, GridTreeNode m1, GridTreeNode m2, String property) {
				if( m1.getType() != m2.getType() )
					return (int)(m1.getType() - m2.getType());
				else if( property == null ){
					return m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName());	
				}
				else if( property == "displayName" ){
					return fileNameCompare(m1, m2);
				}
				else if( property == "date" )
				{
					if( m1.getDate() == null)
					{
						if( m2.getDate() == null )
							return fileNameCompare(m1,m2);
						else
							return -1;
					}
					else
					{
						if( m2.getDate() == null )
							return 1;
						else
							return m1.getDate().compareTo(m2.getDate());
					}
				}
				else if( property == "size" ){
					if( m1.getSize() == null )
					{
						if( m2.getSize() == null )
							return fileNameCompare(m1,m2);
						else
							return -1;
					}
					else
					{
						if(m2.getSize() == null)
							return 1;
						else
						{
							if( m1.getSize() == m2.getSize() )
							{
								return fileNameCompare(m1, m2);
							}
							else if( m1.getSize() < m2.getSize() )
								return -1;
							else
								return 1;
						}
					}
				}
				else
					return super.compare(store, m1, m2, property);
			}
		};
		treeStore = new TreeStore<GridTreeNode>(loader);
		treeStore.setStoreSorter(sorter);

		tree = new ExtEditorTreeGrid<GridTreeNode>(treeStore, treeColModel,
				table, false,false){

					@Override
					public void setExpanded(GridTreeNode model, boolean expand) {
						if(model.getChildrenCount() != 0){
							if(expand && (model.getChildrenCount() > 0 || model.getParentID() == -1)) {
								tree.mask(UIContext.Constants.loadingIndicatorText());
							}
						}
						super.setExpanded(model, expand);
					}
					
					protected void handleFSNodeClick(final GridTreeNode treeNode){
						AsyncCallback callback = new AsyncCallback(){
							@Override
							public void onFailure(Throwable caught) {}

							@Override
							public void onSuccess(Object result) {
								handleFSNodeClick2(treeNode);
							}
							
						};
						if(this.isFileSystem(treeNode) && treeNode.getParentID() == -1 
								&& treeNode.getChildrenCount() <= 0) {
							checkAndUpdateSessionPassword(treeNode, callback);
						}else {
							treeNode.setEncryptedKey(selectedSessionEncryptionKey);
 							handleFSNodeClick2(treeNode);
						}
					}
			
		};
		
		tree.addListener(Events.Expand, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				tree.unmask();
			}
		});	
		
		tree.setView(new TreeGridView()
		{
			@Override
			protected int getVisibleRowCount()
			{
				int nVisableRowCount = super.getVisibleRowCount();
				
				if (nVisableRowCount < PagingContext.PAGETHRESHOLD)
				{
					nVisableRowCount = PagingContext.PAGETHRESHOLD;
				}
				
				return nVisableRowCount; 
			}
			
			@Override
			protected void doUpdate() {
				if (grid == null || !grid.isViewReady() || !this.isBufferEnabled()) {
				      return;
				    }
				    int count = getVisibleRowCount();
				    if (count > 0) {
				      ColumnModel cm = grid.getColumnModel();

				      ListStore<ModelData> store = grid.getStore();
				      List<ColumnData> cs = getColumnData();
				      boolean stripe = grid.isStripeRows();
				      int[] vr = getVisibleRows(count);
				      int cc = cm.getColumnCount();
				      for (int i = vr[0]; i <= vr[1]; i++) {
				        // if row is NOT rendered and is visible, render it
				        if (!isRowRendered(i)) {
				          List<ModelData> list = new ArrayList<ModelData>();
				          list.add(store.getAt(i));
				          //fix 149418 
				          //http://www.sencha.com/forum/showthread.php?176844-GXT-2.2.4-Bug-on-GridCellRenderer-for-TreeGrid
				          //widgetList.add(i, new ArrayList<Widget>());
				          widgetList.set(i, new ArrayList<Widget>());
				          String html = doRender(cs, list, i, cc, stripe, true);
				          getRow(i).setInnerHTML(html);
				          renderWidgets(i, i);
				        }
				      }
				      clean();
				    }
			}
			
		});
		
		((TreeGridView)tree.getView()).setRowHeight(23);
		// Remove the default icons
		tree.setIconProvider(new ModelIconProvider<GridTreeNode>() {

			@Override
			public AbstractImagePrototype getIcon(GridTreeNode model) {
				return AbstractImagePrototype.create(UIContext.IconBundle.blank());
			}

		});
		tree.setBorders(true);		
		tree.setAutoExpandColumn("displayName");
		tree.setAutoExpandMax(5000);
		tree.setTrackMouseOver(false);

		if (!this.isRestoreManager) {
			GridSelectionModel<GridTreeNode> sm = new GridSelectionModel<GridTreeNode>();
			sm.setLocked(true);
			tree.setSelectionModel(sm);
			tree.setTrackMouseOver(true);
		}
		
		BorderLayoutData bldN = new BorderLayoutData(LayoutRegion.NORTH, 150, 60, 320);
		bldN.setMargins(new Margins(0, 0, 4, 0));	
		container.add(grid,bldN);
		
		BorderLayoutData bldC = new BorderLayoutData(LayoutRegion.CENTER, 266);
		container.add(tree, bldC);
	

		return container;
	}
	
	private void handleVAppRecoveryPointSelectionChange(RecoveryPointModel selectedRecoveryPoint) {
		tree.mask(UIContext.Constants.vAppRestoreNoVolumeInfo());
		isGotBackupVMAdaptersFlag = 0;
		selectedSessionID = selectedRecoveryPoint.getSessionID();
		
		if (BackupStatusModel.Finished == selectedRecoveryPoint.getBackupStatus()) {
			getVAppVDCFromSession(selectedRecoveryPoint);
		}
	}
	
	private void getVAppVDCFromSession(final RecoveryPointModel selectedRecoveryPoint) {
		selectedBackupVMModel.setVirtualDataCenterName(null);
		selectedBackupVMModel.setVirtualDataCenterId(null);

		String fullUser = getUserName();
		String password = "";
		if (fullUser != null) {
			password = getPassword();
		} else {
			fullUser = "";
		}
		service.getVAppVDCFromSession(selectedBackupVMModel.getBrowseDestination(), selectedRecoveryPoint.getSessionID(), fullUser , password, new BaseAsyncCallback<VCloudVirtualDataCenterModel>() {
			@Override
			public void onSuccess(VCloudVirtualDataCenterModel result) {
				if (result != null) {
					selectedBackupVMModel.setVirtualDataCenterName(result.getName());
					selectedBackupVMModel.setVirtualDataCenterId(result.getId());
				}
				getVAppChildBackupVMsAndRecoveryPoints(selectedRecoveryPoint);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}
		});
	}
	
	private void getVAppChildBackupVMsAndRecoveryPoints(final RecoveryPointModel selectedRecoveryPoint) {
		String domainTmp = "";
		String pwd = "";
		String user = getUserName();
		final String fullUserName = user;
		if (user != null) {
			int index = user.indexOf('\\');
			if (index > 0) {
				domainTmp = user.substring(0, index);
				user = user.substring(index + 1);
			}
			pwd = getPassword();
		} else {
			user = "";
		}

		final String domain = domainTmp;
		final String userName = user;
		final String password = pwd;
		service.getVAppChildBackupVMsAndRecoveryPoints(selectedBackupVMModel.getBrowseDestination(),
				selectedRecoveryPoint.getSessionID(), domainTmp, user, pwd,
				new BaseAsyncCallback<List<VAppBackupVMRecoveryPointModelWrapper>>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(List<VAppBackupVMRecoveryPointModelWrapper> wrapperList) {
						if (wrapperList != null && wrapperList.size() > 0) {
							Map<String, BackupVMModel> childVMMap = new HashMap<String, BackupVMModel>();
							Map<String, List<String>> pathSubPathMap = new HashMap<String, List<String>>();
							Map<String, List<Object>> pathSessionIdMap = new HashMap<String, List<Object>>();

							saveAndMergeChildBackupAndRecoveryPointInfo(wrapperList, selectedRecoveryPoint, childVMMap,
									pathSubPathMap, pathSessionIdMap);
							setDiskAndNetworkToVAppChildBackupVMs(domain, userName, fullUserName,
									password, childVMMap, pathSubPathMap, pathSessionIdMap);
						} else {
							showErrorMessage(UIContext.Constants.vAppRestoreNoChildVMs());
						}
					}
				});
	}

	private void saveAndMergeChildBackupAndRecoveryPointInfo(List<VAppBackupVMRecoveryPointModelWrapper> wrapperList,
			RecoveryPointModel selectedVAppRP, Map<String, BackupVMModel> childVMMap,
			Map<String, List<String>> pathSubPathMap, Map<String, List<Object>> pathSessionIdMap) {
		selectedBackupVMModel.memberVMList.clear();
		selectedVAppRP.childVMRecoveryPointModelMap.clear();
		
		for (VAppBackupVMRecoveryPointModelWrapper childVM : wrapperList) {
			BackupVMModel childVmModel = childVM.getBackupVMModel();
			String instanceUuid = childVmModel.getVmInstanceUUID();

			selectedBackupVMModel.memberVMList.add(childVmModel);
			selectedVAppRP.childVMRecoveryPointModelMap.put(instanceUuid, childVM.getRecoveryPointModel());

			childVMMap.put(instanceUuid, childVmModel);
			pathSubPathMap.put(instanceUuid, getPathSubPathList4VMDisk(childVM));
			pathSessionIdMap.put(instanceUuid, getPathSessionId4VMAdapter(childVM));
		}
	}

	private void setDiskAndNetworkToVAppChildBackupVMs(String domain, String userName, final String fullUserName,
			final String password, final Map<String, BackupVMModel> childVMMap,
			Map<String, List<String>> pathSubPathMap, final Map<String, List<Object>> pathSessionIdMap) {
		service.getVAppChildVMDisks(pathSubPathMap, domain, userName, password,
				new BaseAsyncCallback<Map<String, List<DiskModel>>>() {
					@Override
					public void onSuccess(Map<String, List<DiskModel>> diskMap) {
						if (diskMap != null && diskMap.size() > 0) {
							Set<String> keySet4Disk = diskMap.keySet();
							for (String vmInstanceUuid : keySet4Disk) {
								BackupVMModel vmModel4Disk = childVMMap.get(vmInstanceUuid);
								vmModel4Disk.diskList = diskMap.get(vmInstanceUuid);
							}
						}

						getVAppAndChildVMNetworkConfigLists();
					}

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}
				});
	}
	
	private void getVAppAndChildVMNetworkConfigLists() {
		String fullUserName = getUserName();
		String password = "";
		if (fullUserName != null) {
			password = getPassword();
		}

		service.getVAppAndChildVMNetworkConfigLists(getSessionPath(), selectedSessionID, fullUserName, password,
				new BaseAsyncCallback<Map<String, List<VMNetworkConfigInfoModel>>>() {
					@Override
					public void onSuccess(Map<String, List<VMNetworkConfigInfoModel>> modelList) {
						if (selectedBackupVMModel != null) {
							selectedBackupVMModel.adapterList.clear();
							selectedBackupVMModel.adapterList.addAll(modelList.get(selectedBackupVMModel.getVMName()));
							
							for (BackupVMModel childVMModel : selectedBackupVMModel.memberVMList) {
								String vmName = childVMModel.getVMName();
								List<VMNetworkConfigInfoModel> childVMNetworks = modelList.get(vmName);
								if (childVMNetworks == null || childVMNetworks.isEmpty()) {
									continue;
								}
								
								childVMModel.adapterList.clear();
								childVMModel.adapterList.addAll(childVMNetworks);
							}
						}
						
						isGotBackupVMAdaptersFlag = 1;
						fireRecoveryPointsChanged(isGotBackupVMAdaptersFlag);
					}

					@Override
					public void onFailure(Throwable caught) {
						if (selectedBackupVMModel != null) {
							selectedBackupVMModel.adapterList.clear();
							
							for (BackupVMModel childVMModel : selectedBackupVMModel.memberVMList) {
								childVMModel.adapterList.clear();
							}
						}
					}
				});
	}

	private List<String> getPathSubPathList4VMDisk(VAppBackupVMRecoveryPointModelWrapper childVM) {
		List<String> pathSubPathList = new ArrayList<String>();
		
		BackupVMModel vmModel = childVM.getBackupVMModel();
		RecoveryPointModel rpModel = childVM.getRecoveryPointModel();
		pathSubPathList.add(vmModel.getBrowseDestination());
		
		String sessionPath = rpModel.getPath();
		String subPath = "";
		if(sessionPath!=null && !sessionPath.equals("")){
			subPath = sessionPath.substring(sessionPath.indexOf("\\")+1);
		}
		pathSubPathList.add(subPath);
		
		return pathSubPathList;
	}
	
	private List<Object> getPathSessionId4VMAdapter(VAppBackupVMRecoveryPointModelWrapper childVM) {
		List<Object> pathSessionIdList = new ArrayList<Object>();
		
		BackupVMModel vmModel = childVM.getBackupVMModel();
		RecoveryPointModel rpModel = childVM.getRecoveryPointModel();
		pathSessionIdList.add(vmModel.getDestination());
		pathSessionIdList.add(rpModel.getSessionID());
		
		return pathSessionIdList;
	}
	
	private void checkAndUpdateSessionPassword(final GridTreeNode currentGridTreeNode, 
			final AsyncCallback pwdCallback) {
		//To mount a volume, we must first have the session password
		if(currentGridTreeNode.getParentID() == -1 &&selectedSession != null 
				&& selectedSession.isEncrypted()) {
			commonService.getSessionPasswordBySessionGuid(new String[]{selectedSession.getSessionGuid()}, 
					new BaseAsyncCallback<String[]>(){
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							pwdCallback.onFailure(caught);
						}

						@Override
						public void onSuccess(String[] result) {
							if(result != null & result.length > 0 && result[0] != null) {
								currentGridTreeNode.setEncryptedKey(result[0]);
								selectedSessionEncryptionKey = result[0];
								pwdCallback.onSuccess(null);
							}else {
								List<RecoveryPointModel> rps = new ArrayList<RecoveryPointModel>();
								rps.add(selectedSession);
								SessionPasswordPanel pwdPanel = new SessionPasswordPanel(getSessionPath(),
										"", getUserName(), getPassword(),
										rps, new PasswordPanelCallback(currentGridTreeNode, pwdCallback));
								pwdPanel.show();
							}
						}
			});
		}else {
			pwdCallback.onSuccess(null);
		}

	}
	
	private final class RecoveryPointCallback extends
			BaseAsyncCallback<RecoveryPointModel[]> {
		@Override
		public void onFailure(Throwable caught) {
			clearRecoveryPoints();
			timeGrid.unmask();
			super.onFailure(caught);
		}

		@Override
		public void onSuccess(RecoveryPointModel[] result) {
			clearRecoveryPoints();
			if (result != null) {
				
				List<RecoveryPointModel> newstore = new ArrayList<RecoveryPointModel>();
				for (int i = 0; i < result.length; i++) {
					GWT.log(result[i].getName(), null);
					//if no root item set catalog status to not created.
					if(result[i].listOfRecoveryPointItems == null || result[i].listOfRecoveryPointItems.size()==0){
						result[i].setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
					}
					newstore.add(result[i]);
				}
				thisPanel.store.add(newstore);
				if (result.length > 0) {
					thisPanel.grid.getSelectionModel().select(0, false);
					thisPanel.grid.getView().scrollToTop();
				}
			}
			timeGrid.unmask();
		}
	}

	private class PasswordPanelCallback implements SessionPasswordPanel.PanelCallback{
		private GridTreeNode currentNode;
		private AsyncCallback<List<GridTreeNode>> callback;
		private AsyncCallback cb;

		public PasswordPanelCallback(final GridTreeNode currentGridTreeNode, AsyncCallback cb) {
			this.cb = cb;
			currentNode = currentGridTreeNode;
		}
		
		@Override
		public void onUpdatePasswordSuccessfull(Map<String, String> pwdsByHash) {
			selectedSessionEncryptionKey = pwdsByHash.values().toArray(new String[0])[0];
			currentNode.setEncryptedKey(selectedSessionEncryptionKey);
			cb.onSuccess(null);
		}
		
		@Override
		public void onUpdatePasswordFailed(Throwable caught) {
			String message = null;
			if(caught instanceof BusinessLogicException) {
				message = ((BusinessLogicException)caught).getDisplayMessage();
			}else
				message = caught.getMessage();
			
			MessageBox box = new MessageBox();
			box.setIcon(MessageBox.ERROR);
			box.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
			box.setMessage(message);
			box.show();
			grid.unmask();
		}
		
		@Override
		public void onCancelClicked() {
			grid.unmask();
		}
	};

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);	
		RowLayout rl = new RowLayout();
		this.setLayout(rl);
		this.setHeight("100%");
		this.setScrollMode(Scroll.AUTOY);
		
		this.add(renderHeaderSection(), new RowData(1, -1));

		LabelField label = new LabelField();
		label.addStyleName("restoreWizardSubItem");
		label.setValue(UIContext.Constants.restoreBackupSource());
		this.add(label, new RowData(1, -1));

		this.add(renderSourceSection());
		
		label = new LabelField();
		label.addStyleName("restoreWizardSubItem");
		label.setValue(UIContext.Constants.virtualMachine());
		this.add(label, new RowData(1, -1));
		
		this.add(renderVMList());

		label = new LabelField();
		label.addStyleName("restoreWizardSubItem");
		label.setValue(UIContext.Constants.restoreFilesToRestore());
		this.add(label, new RowData(1, -1));
		

		//override onClick method to highlight Days with recovery points.
		picker = new EnhancedHightlightedDataPicker()
		{
			private int mpSelMonth = -1;
			private int mpSelYear = -1;
			@Override
			  public void onComponentEvent(ComponentEvent be) {
				super.onComponentEvent(be);
				if(be.getEventTypeInt() == Event.ONMOUSEUP)
				{
					be.stopEvent();
					El target = be.getTargetEl();
					String cls = target.getStyleName();
					if (cls.indexOf("x-date-left-icon") >= 0) {
						DateWrapper wrapper = new DateWrapper(previousSelectedMonth);
						Date newDate = wrapper.addMonths(-1).asDate();
						highlightDatesIfMonthChange(newDate, true);
					} else if (cls.indexOf("x-date-right-icon") >= 0) {
						DateWrapper wrapper = new DateWrapper(previousSelectedMonth);
						Date newDate = wrapper.addMonths(1).asDate();
						highlightDatesIfMonthChange(newDate, true);
					}
				}
			}
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				be.stopEvent();
			    El target = be.getTargetEl();
			    El pn = null;
			    if ((pn = target.findParent("td.x-date-mp-month", 2)) != null) {
			        mpSelMonth = pn.dom.getPropertyInt("xmonth");
			      } else if ((pn = target.findParent("td.x-date-mp-year", 2)) != null) {
			        mpSelYear = pn.dom.getPropertyInt("xyear");
			      } else if (target.is("button.x-date-mp-ok")) {
			    	  DateWrapper wrap = new DateWrapper(getValue()); 
			    	  if(mpSelYear == -1)
			    		  mpSelYear = wrap.getFullYear();
			    	  if(mpSelMonth == -1)
			    		  mpSelMonth = wrap.getMonth();
			        DateWrapper d = new DateWrapper(mpSelYear, mpSelMonth, 1);
			        highlightDatesIfMonthChange(d.asDate(), true);
			        mpSelYear = -1;
			        mpSelMonth = -1;
			      }
			}
		};
		picker.ensureDebugId("ECBA57E7-A5DF-41ce-9CDF-88C543A0E3FC");
		if (!isRestoreManager)
		{
			// set hard code size for non-restore wizard
			this.add(renderTableSection(), new RowData(660, 420));
		}
		else
		{
			this.add(renderTableSection(), new RowData(1, 1));
		}

	}
	
	private void getDefaultSourceValue() {
		// 1) Get the backup configuration and set the source accordingly
		service.getVMBackupConfiguration(UIContext.backupVM,new AsyncCallback<VMBackupSettingModel>() {

			@Override
			public void onFailure(Throwable caught) {
				// Failed to get a proper value
			}

			@Override
			public void onSuccess(VMBackupSettingModel result) {
				if (result != null && result.getBackupVM().getDestination() != null) {
					
					sourcePanel.setBackupDestination(result.getBackupVM().getDestination());
					sourcePanel.setDestUserName(result.getBackupVM().getDesUsername());
					sourcePanel.setDestPassword(result.getBackupVM().getDesPassword());
					
					String domain = "";
					String pwd = "";
					String userName = getUserName();
					if (userName != null) {
						int index = userName.indexOf('\\');
						if (index > 0) {
							domain = userName.substring(0, index);
							userName = userName.substring(index + 1);
						}
						pwd = getPassword();
					}
					final String destination = getSessionPath();
					service.getBackupVMModel(destination, domain,userName, pwd, new BaseAsyncCallback<BackupVMModel>(){
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
						}
						
						@Override
						public void onSuccess(BackupVMModel result) {
							vmList.getStore().removeAll();
							if(result !=null && result.getVmInstanceUUID()!=null){
								vmList.getStore().add(result);
								vmList.setValue(result);
								selectedBackupVMModel = result;
							}	
								
							service.getServerTime(new BaseAsyncCallback<Date>() {
								@Override
								public void onFailure(Throwable caught) {
									super.onFailure(caught);
									refreshUI(new Date());
//									thisPanel.picker.setValue(getServerDate());
								}

								private void refreshUI(Date date) {
									serverDate = Utils.localTimeToServerTime(date);
									previousSelectedMonth = formateToMonth(serverDate);
									//highlightMostRecentMonthRPDates();
								}

								@Override
								public void onSuccess(Date result) {
									refreshUI(result);
//									//thisPanel.picker.setValue(getServerDate());
								}
							});
						}
					});
				}
			}
		});
	}
	private void highlightDatesIfMonthChange(Date newDate,  boolean useLastDayInMonth ) {
		newDate = formateToMonth(newDate);
		if(previousSelectedMonth == null || newDate.getTime() != previousSelectedMonth.getTime())
		{
			previousSelectedMonth = newDate;
			highlightDates(newDate, useLastDayInMonth);				    
		}
	}
	
	List<GridTreeNode> selectionList = new ArrayList<GridTreeNode>();
	private ColumnModel columnModel;
	private ColumnModel treeColModel;
	
	public List<GridTreeNode> GetSelectedNodesFromGridTree() {
		selectionList = new ArrayList<GridTreeNode>();
		getSelectedSubNodes(null);
		return selectionList;
	}
	
	public void getSelectedSubNodes(GridTreeNode parent)
	{
		List<GridTreeNode> roots;
		if (parent == null)
		{
			roots = tree.getTreeStore().getRootItems();
		}
		else
		{
			roots = tree.getTreeStore().getChildren(parent);
		}
		for (int i = 0; i < roots.size() ; i++)
		{
			GridTreeNode node = roots.get(i);
			FlashCheckBox cb = table.get(node);
			
			if (cb != null)
			{
				if (cb.getSelectedState() == FlashCheckBox.FULL && cb.isEnabled())
				{
					//Package it!
					selectionList.add(node);
				}
				else if (cb.getSelectedState() == FlashCheckBox.PARTIAL || 
						(cb.getSelectedState() == FlashCheckBox.FULL && !cb.isEnabled()))
				{
					//get this node's children
					getSelectedSubNodes(node);
				}
			}
			else
			{
				//Error
				
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<GridTreeNode> getPagedSelectedNodes() {
		List<GridTreeNode> nodes = new ArrayList<GridTreeNode>();
		if (tree instanceof ExtEditorTreeGrid) {
			ExtEditorTreeGrid extTree = (ExtEditorTreeGrid) tree;
			nodes = extTree.getPagedSelectedNodes();
		}
		return nodes;
	}

	public List<GridTreeNode> GetSelectedNodes() {
		List<GridTreeNode> nodes = new ArrayList<GridTreeNode>();

		List<GridTreeNode> selectedNodesFromGridTree = GetSelectedNodesFromGridTree();
		if (selectedNodesFromGridTree != null
				&& selectedNodesFromGridTree.size() > 0) {
			nodes.addAll(selectedNodesFromGridTree);
		}
		List<GridTreeNode> selectedPagedNodes = getPagedSelectedNodes();
		if (selectedPagedNodes != null && selectedPagedNodes.size() > 0) {
			nodes.addAll(selectedPagedNodes);
		}

		Map<String, GridTreeNode> existsMap = new FastMap<GridTreeNode>();
		for (int i = 0; i < nodes.size(); i++) {
			GridTreeNode item = nodes.get(i);

			if (this.nodeIsSQLDB(item)) {
				GridTreeNode parent = treeStore.getParent(item);
				String instName = parent.getDisplayName();
				String dbName = item.getDisplayName();
				String key = dbName + "@" + instName;
				if (!existsMap.containsKey(key)) {
					existsMap.put(key, item);
				}
			}
		}
		ArrayList<GridTreeNode> children = new ArrayList<GridTreeNode>();

		for (int i = 0; i < nodes.size(); i++) {
			GridTreeNode node = nodes.get(i);

			if (nodeIsSQLInstance(node)) {
				// Special Case, don't add this node, add the children
				node.setPackage(false);
				children.addAll(purgeDupChildren(node, existsMap));
			}
		}

		nodes.addAll(children);

		return nodes;
	}

	private Collection<GridTreeNode> purgeDupChildren(GridTreeNode parent,
			Map<String, GridTreeNode> existsMap) {
		List<GridTreeNode> children = treeStore.getChildren(parent);
		Map<String, GridTreeNode> map = new FastMap<GridTreeNode>();
		for (GridTreeNode item : children) {
			String instName = parent.getDisplayName();
			String dbName = item.getDisplayName();
			String key = dbName + "@" + instName;
			if (!map.containsKey(key) && !existsMap.containsKey(key)) {
				map.put(key, item);
			}
		}
		return map.values();
	}

	private boolean nodeIsSQLInstance(GridTreeNode node) {

		Integer nodeType = node.getType();
		if (nodeType != null
				&& nodeType == SQLRestoreOptionsPanel.SQLNodeType.INSTANCE
						.getValue()) {
			return true;
		}
		return false;
	}

	private boolean needPackageRSGChildren(GridTreeNode node) {

		if (nodeIsRSG(node) && exchVer == ExchVersion.Exch2007) {
			return true;
		}
		return false;
	}

	private boolean nodeIsRSG(GridTreeNode node) {
		Integer nodeType = node.getType();
		if (nodeType != null
				&& CatalogModelType.OT_VSS_EXCH_LOGICALPATH == nodeType) {
			return true;
		}
		return false;
	}

	private boolean nodeIsSQLDB(GridTreeNode node) {
		Integer nodeType = node.getType();
		if (nodeType != null
				&& nodeType == SQLRestoreOptionsPanel.SQLNodeType.DB.getValue()) {
			return true;
		}
		return false;
	}

	protected void PopulateTreeGrid(RecoveryPointModel selection) {

		treeStore.removeAll();
		// 1) Create tree nodes from the selection.items
		if (selection != null) {
			try {
				rootItemMap.clear();
				selectedSessionID = selection.getSessionID();
				selectedSession = selection;
				selectedSessionEncryptionKey = null;
				List<GridTreeNode> newstore = new ArrayList<GridTreeNode>();
				if (selection.listOfRecoveryPointItems != null) {
					if(selection.listOfRecoveryPointItems.size()>0){
						tree.unmask();
						for (int i = 0; i < selection.listOfRecoveryPointItems
								.size(); i++) {
							RecoveryPointItemModel rpm = (RecoveryPointItemModel) selection.listOfRecoveryPointItems
									.get(i);
							GridTreeNode node = ConvertToGridTreeNode(rpm);
							newstore.add(node);
							rootItemMap.put(node, rpm);
						}
					}else{
						tree.mask(UIContext.Constants.recoverVMNoVolumeInfo());
					}
				}
				treeStore.add(newstore, false);
				tree.getView().scrollToTop();
			} catch (Exception e) {

			}
		}
	}

	private GridTreeNode ConvertToGridTreeNode(RecoveryPointItemModel m) {
		GridTreeNode node = new GridTreeNode();
		node.setDate(null);
		node.setSize(m.getVolDataSizeB());
		node.setName(m.getDisplayName());
		node.setDisplayName(m.getDisplayName());
		node.setCatalofFilePath(m.getCatalogFilePath());
		node.setChildrenCount(m.getChildrenCount());
		// First Level use -1
		node.setParentID(-1l);
		node.setChecked(false);
		node.setSubSessionID(new Long(m.getSubSessionID()).intValue());
		node.setDestPwd(getPassword());
		node.setDestUser(getUserName());
		node.setBackupDestination(getSessionPath());
		if(selectedSessionID != null)
			node.setSessionID(this.selectedSessionID.longValue());
		
		if (m.getGuid().equals(GUID_SQLWRITER)) {
			node.setType(CatalogModelType.OT_VSS_SQL_WRITER);
			node.setPath(m.getGuid());
		} else if (m.getGuid().equals(GUID_EXCHANGE_2003)) {
			exchVer = ExchVersion.Exch2003;
			setExchCommon(node, GUID_EXCHANGE_WRITER);
		} else if (m.getGuid().equals(GUID_EXCHANGE_2007)) {
			exchVer = ExchVersion.Exch2007;
			setExchCommon(node, GUID_EXCHANGE_WRITER);
		}else if(m.getGuid().equals(GUID_EXCHANGE_REPLICA_2007)){
			exchVer = ExchVersion.Exch2007;
			setExchCommon(node, GUID_EXCHANGE_REPLICA_WRITER);
		}
		else if (m.getGuid().equals(GUID_EXCHANGE_2010)) {
			exchVer = ExchVersion.Exch2010;
			setExchCommon(node, GUID_EXCHANGE_WRITER);
		}
		else if(m.getGuid().equals(GUID_EXCHANGE_REPLICA_2010))
		{
			exchVer = ExchVersion.Exch2010;
			setExchCommon(node, GUID_EXCHANGE_REPLICA_WRITER);
		}
		else if (m.getGuid().equals(GUID_EXCHANGE_2013)) {
			exchVer = ExchVersion.Exch2013;
			setExchCommon(node, GUID_EXCHANGE_WRITER);
		}
		else if(m.getGuid().equals(GUID_EXCHANGE_REPLICA_2013))
		{
			exchVer = ExchVersion.Exch2013;
			setExchCommon(node, GUID_EXCHANGE_REPLICA_WRITER);
		}
		else if (m.getGuid().equals(GUID_EXCHANGE_2016)) {
			exchVer = ExchVersion.Exch2016;
			setExchCommon(node, GUID_EXCHANGE_WRITER);
		}
		else if(m.getGuid().equals(GUID_EXCHANGE_REPLICA_2016))
		{
			exchVer = ExchVersion.Exch2016;
			setExchCommon(node, GUID_EXCHANGE_REPLICA_WRITER);
		}
		else {
            //@FIXME  we should save the GUID of the top node. so that if user has not assigned a driver letter to 
            //the volume, we can use the GUID instead of driver letter
			// Fix issue 18906917
            node.setGuid(m.getGuid());

			node.setType(CatalogModelType.OT_VSS_FILESYSTEM_WRITER);
		}
		return node;
	}

	private void setExchCommon(GridTreeNode node, String path) {
		RestoreContext.setExchVersion(exchVer);
		node.setType(CatalogModelType.OT_VSS_EXCH_WRITER);
		node.setPath(path);
	}

	public Integer getSelectedSessionID() {
		return selectedSessionID;
	}

	public String getSessionPath() {
		return sourcePanel.getBackupDestination();	
	}

	public void setSessionPath(String path) {
		sourcePanel.setBackupDestination(path);
	}
	
	public String getUserName() {
		return sourcePanel.getDestUserName();
	}

	public String getPassword() {
		return sourcePanel.getDestPassword();
	}

	public void highlightDates(Date date, final boolean useLastDayInMonth) {
		if (picker != null) {
			picker.setEnabled(false);
			DateWrapper dr = new DateWrapper(date);

			final int currentYear = dr.getFullYear();
			final int currentMonth = dr.getMonth();
			
			int maxExtraDays = 42 - dr.getDaysInMonth();

			DateWrapper startDate = new DateWrapper(currentYear, currentMonth, 1);
			startDate = startDate.addDays(-maxExtraDays);
			DateWrapper endDate = new DateWrapper(currentYear, currentMonth + 1, 1);
			endDate = endDate.addDays(maxExtraDays);

			String destination = getSessionPath();

			String domain = "";
			String pwd = "";
			String userName = getUserName();
			if (userName != null) {
				int index = userName.indexOf('\\');
				if (index > 0) {
					domain = userName.substring(0, index);
					userName = userName.substring(index + 1);
				}
				pwd = getPassword();
			}
			clearRecoveryPoints();
			resetTimeRange();
			refreshTimeRange();
			
			if (destination != null) {
				picker.clearAll();
				// 2015-04-08 fix issue 214818 caused by DST problem
//				String asStartDate = this.GetServerDateString(startDate, false);// Utils.serverTimeToLocalTime(startDate.asDate());
//				String asEndDate = GetServerDateString(endDate, false);
				D2DTimeModel asStartDate = Utils.getD2DTime(startDate, false);
				D2DTimeModel asEndDate = Utils.getD2DTime(endDate, false);
				String browseDest =  destination;
				if(selectedBackupVMModel != null){
					browseDest = selectedBackupVMModel.getBrowseDestination();
				}
				service.getRecoveryPointsByServerTime(browseDest, domain,
						userName, pwd, asStartDate, asEndDate, false,
						new BaseAsyncCallback<RecoveryPointModel[]>() {

							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								picker.setEnabled(true);
							}

							@Override
							public void onSuccess(RecoveryPointModel[] result) {
								// RecoveryPointModel lateRPModel = null;
								Date latestHighlightedDate = null;
								for (int i = 0, count = result == null ? 0 : result.length; i < count; i++) {
									RecoveryPointModel m = result[i];

									Date serverDate = RecPointModel2ServerDate(m);

									boolean isInCurrentMonth = serverDate.getMonth() == currentMonth;
									picker.addSelectedDate(serverDate, result[i].getBackupStatus() == BackupStatusModel.Finished, isInCurrentMonth);
									
									if(m.getBackupSetFlag() > 0) {
										picker.addBackupSetFlag(serverDate);
									}

									if ((latestHighlightedDate == null
											|| latestHighlightedDate.before(serverDate)) && isInCurrentMonth) {
										latestHighlightedDate = serverDate;
										// lateRPModel = m;
									}
									
								}

								if (latestHighlightedDate != null) {
									
									Date serverDate = null;
									if(useLastDayInMonth) {
										serverDate = latestHighlightedDate;
										picker.setValue(serverDate, true);
									}
									else
										serverDate = picker.getValue();
									

									DateWrapper beginDateWrap = new DateWrapper(
											picker.getValue());
									Date endDate = beginDateWrap.addDays(1)
											.asDate();
									DateWrapper endDateWrap = new DateWrapper(
											endDate);

									// 2015-04-08 fix issue 214818 caused by DST problem
//									String strSvrBeginDate = GetServerDateString(
//											beginDateWrap, false);
//
//									String strSvrEndDate = GetServerDateString(
//											endDateWrap, false);
//
//									thisPanel
//											.refreshRecoveryPointDataByServerTime(
//													strSvrBeginDate,
//													strSvrEndDate);
									D2DTimeModel strSvrBeginDate = Utils.getD2DTime(
											beginDateWrap, false);

									D2DTimeModel strSvrEndDate = Utils.getD2DTime(
											endDateWrap, false);

									thisPanel
											.refreshRecoveryPointDataByServerTime(
													strSvrBeginDate,
													strSvrEndDate);
								} else {
									picker.setEnabled(true);
									Date selectedDate = picker.getValue();
									if(selectedDate != null ) {
										DateWrapper dateWrapper = new DateWrapper(currentYear, currentMonth, new DateWrapper(picker.getValue()).getDate());
										picker.setValue(dateWrapper.asDate(), true);
									}
								}
								
								picker.repaint();
								picker.highlightSelectedDates();
							}

						});
			}
			else
				 picker.setEnabled(true);
		}
	}

	public void highlightMostRecentMonthRPDates() {
		if (picker == null) {
			return;
		}
		
		picker.clearAll();
		picker.setEnabled(false);
		clearRecoveryPoints();
		resetTimeRange();
		refreshTimeRange();
		if(tree !=null){
			tree.unmask();
		}
		if (selectedBackupVMModel == null) {
			picker.setEnabled(true);
			picker.showToday();
			return;
		}

		String destination = getSessionPath();
		if (destination == null) {
			picker.setEnabled(true);
			return;
		}
		
		String domain = "";
		String pwd = "";
		String userName = getUserName();
		if (userName != null) {
			int index = userName.indexOf('\\');
			if (index > 0) {
				domain = userName.substring(0, index);
				userName = userName.substring(index + 1);
			}
			pwd = getPassword();
		}

		DateWrapper startDate = new DateWrapper(new Date(0));
		DateWrapper endDate = startDate.addYears(8029);

		// 2015-04-08 fix issue 214818 caused by DST problem
//		String asStartDate = this.GetServerDateString(startDate, false);
//		String asEndDate = GetServerDateString(endDate, false);
		D2DTimeModel asStartDate = Utils.getD2DTime(startDate, false);
		D2DTimeModel asEndDate = Utils.getD2DTime(endDate, false);

		service.getRecoveryPointsByServerTime(selectedBackupVMModel.getBrowseDestination(), domain, userName,
				pwd, asStartDate, asEndDate, false,
				new BaseAsyncCallback<RecoveryPointModel[]>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						picker.setEnabled(true);
					}

					@Override
					public void onSuccess(RecoveryPointModel[] result) {
						Date latestHighlightedDate = null;
						int mostRecentMonth = -1;
						if (result == null || result.length == 0) {
							picker.setEnabled(true);
							picker.showToday();
						} else {
							for (int i = 0; i < result.length; i++) {
								Date serverDate = RecPointModel2ServerDate(result[i]);
								if (mostRecentMonth == -1) {
									mostRecentMonth = serverDate.getMonth();
									previousSelectedMonth = formateToMonth(serverDate);
								}

								picker.addSelectedDate(serverDate, result[i].getBackupStatus() == BackupStatusModel.Finished, serverDate.getMonth() == mostRecentMonth);
								
								if(result[i].getBackupSetFlag() > 0) {
									picker.addBackupSetFlag(serverDate);
								}
								
								if ((latestHighlightedDate == null || latestHighlightedDate.before(serverDate))) {
									latestHighlightedDate = serverDate;
								}
								
							}

							if (latestHighlightedDate != null) {							
								picker.setValue(latestHighlightedDate, true);

								DateWrapper beginDateWrap = new DateWrapper(
										picker.getValue());
								Date endDate = beginDateWrap.addDays(1)
										.asDate();
								DateWrapper endDateWrap = new DateWrapper(
										endDate);

								// 2015-04-08 fix issue 214818 caused by DST problem
//								String strSvrBeginDate = GetServerDateString(
//										beginDateWrap, false);
//
//								String strSvrEndDate = GetServerDateString(
//										endDateWrap, false);
//
//								thisPanel.refreshRecoveryPointDataByServerTime(
//										strSvrBeginDate, strSvrEndDate);
								D2DTimeModel strSvrBeginDate = Utils.getD2DTime(
										beginDateWrap, false);

								D2DTimeModel strSvrEndDate = Utils.getD2DTime(
										endDateWrap, false);

								thisPanel.refreshRecoveryPointDataByServerTime(
										strSvrBeginDate, strSvrEndDate);
							}
						}

						picker.repaint();
						picker.highlightSelectedDates();
					}

				});

	}

	// 2015-04-08 fix issue 214818 caused by DST problem
//	public void refreshRecoveryPointFromTimeRangeByServerTime(String beginDate,
//			String endDate) {
//		if(selectedBackupVMModel == null || selectedBackupVMModel.getDestination() == null){
//			timeGrid.unmask();
//			return;
//		}
//			
//		// From the Time Grid Only
//		String domain = "";
//		String pwd = "";
//		String userName = getUserName();
//		if (userName != null) {
//			int index = userName.indexOf('\\');
//			if (index > 0) {
//				domain = userName.substring(0, index);
//				userName = userName.substring(index + 1);
//			}
//			pwd = getPassword();
//		}
//		if (this.sourcePanel.getRpsHost() == null)
//			service.getRecoveryPointsByServerTimeWithFSCatalogStatus(selectedBackupVMModel.getBrowseDestination(), domain, userName,
//				pwd, beginDate, endDate, false,
//				new RecoveryPointCallback());
//		else
//			service.getRecoveryPointsByServerTimeRPSInfo(selectedBackupVMModel.getBrowseDestination(), domain, userName,
//					pwd, beginDate, endDate, true, getRPSInfo(),
//					new RecoveryPointCallback());
//	}
	public void refreshRecoveryPointFromTimeRangeByServerTime(D2DTimeModel beginDate,
			D2DTimeModel endDate) {
		if(selectedBackupVMModel == null || selectedBackupVMModel.getDestination() == null){
			timeGrid.unmask();
			return;
		}
			
		// From the Time Grid Only
		String domain = "";
		String pwd = "";
		String userName = getUserName();
		if (userName != null) {
			int index = userName.indexOf('\\');
			if (index > 0) {
				domain = userName.substring(0, index);
				userName = userName.substring(index + 1);
			}
			pwd = getPassword();
		}
		if (this.sourcePanel.getRpsHost() == null)
			service.getRecoveryPointsByServerTimeWithFSCatalogStatus(selectedBackupVMModel.getBrowseDestination(), domain, userName,
				pwd, beginDate, endDate, false,
				new RecoveryPointCallback());
		else
			service.getRecoveryPointsByServerTimeRPSInfo(selectedBackupVMModel.getBrowseDestination(), domain, userName,
					pwd, beginDate, endDate, true, getRPSInfo(),
					new RecoveryPointCallback());
	}
	
	private OndemandInfo4RPS getRPSInfo(){
		BackupRPSDestSettingsModel rpsDest = sourcePanel.rpsDestSettings;//		
		OndemandInfo4RPS info = new OndemandInfo4RPS();	
		info.rpsHostInfo = sourcePanel.getRpsHost();
		info.setDataStoreName(rpsDest.getRPSDataStoreName());
		info.setDataStoreUUID(rpsDest.getRPSDataStoreUUID());
		info.setDest(getSessionPath());
		info.setDestUserName(getUserName());
		info.setDestPassword(getPassword());
		info.setAgentUUID(selectedBackupVMModel.getVmInstanceUUID());
		info.setVmInstanceUUID(selectedBackupVMModel.getVmInstanceUUID());
		
		return info;
	}

	// 2015-04-08 fix issue 214818 caused by DST problem
//	public void refreshRecoveryPointDataByServerTime(String serverBeginDate,
//			String serverEndDate) {
//		if(selectedBackupVMModel == null || selectedBackupVMModel.getDestination() == null)
//			return;
//		
//		picker.setEnabled(false);
//		String destination = getSessionPath();
//		if(destination == null){
//			return;
//		}
//		String domain = "";
//		String pwd = "";
//		String userName = getUserName();
//		if (userName != null) {
//			int index = userName.indexOf('\\');
//			if (index > 0) {
//				domain = userName.substring(0, index);
//				userName = userName.substring(index + 1);
//			}
//			pwd = getPassword();
//		}
//		if(destination !=null){
//		service.getRecoveryPointsByServerTime(selectedBackupVMModel.getBrowseDestination(), domain, userName,
//				pwd, serverBeginDate, serverEndDate, false,
//				new BaseAsyncCallback<RecoveryPointModel[]>() {
//					@Override
//					public void onFailure(Throwable caught) {
//						clearRecoveryPoints();
//						super.onFailure(caught);
//						picker.setEnabled(true);
//					}
//
//					@Override
//					public void onSuccess(RecoveryPointModel[] result) {
//						clearRecoveryPoints();
//						if (result != null) {
//
//							resetTimeRange();
//
//							// List<RecoveryPointModel> newstore = new
//							// ArrayList<RecoveryPointModel>();
//							for (int i = 0; i < result.length; i++) {
//								GWT.log(result[i].getName(), null);
//								addRecoveryPointModel(result[i]);
//							}
//							refreshTimeRange();
//						}
//						picker.setEnabled(true);
//					}
//
//				});
//		}
//	}
	public void refreshRecoveryPointDataByServerTime(D2DTimeModel serverBeginDate,
			D2DTimeModel serverEndDate) {
		if(selectedBackupVMModel == null || selectedBackupVMModel.getDestination() == null)
			return;
		
		picker.setEnabled(false);
		String destination = getSessionPath();
		if(destination == null){
			return;
		}
		String domain = "";
		String pwd = "";
		String userName = getUserName();
		if (userName != null) {
			int index = userName.indexOf('\\');
			if (index > 0) {
				domain = userName.substring(0, index);
				userName = userName.substring(index + 1);
			}
			pwd = getPassword();
		}
		if(destination !=null){
		service.getRecoveryPointsByServerTime(selectedBackupVMModel.getBrowseDestination(), domain, userName,
				pwd, serverBeginDate, serverEndDate, false,
				new BaseAsyncCallback<RecoveryPointModel[]>() {
					@Override
					public void onFailure(Throwable caught) {
						clearRecoveryPoints();
						super.onFailure(caught);
						picker.setEnabled(true);
					}

					@Override
					public void onSuccess(RecoveryPointModel[] result) {
						clearRecoveryPoints();
						if (result != null) {

							resetTimeRange();

							// List<RecoveryPointModel> newstore = new
							// ArrayList<RecoveryPointModel>();
							for (int i = 0; i < result.length; i++) {
								GWT.log(result[i].getName(), null);
								addRecoveryPointModel(result[i]);
							}
							refreshTimeRange();
						}
						picker.setEnabled(true);
					}

				});
		}
	}

	private void clearRecoveryPoints() {
		thisPanel.store.removeAll();
		fireRecoveryPointsChanged(0);
		clearTree();
	}

	private void clearTree() {
		thisPanel.treeStore.removeAll();
		table.clear();
		if (tree instanceof ExtEditorTreeGrid) {
			if (isRestoreManager) {
				((ExtEditorTreeGrid) tree).getNodeContextMap().clear();
			}
		}
	}

	private void fireRecoveryPointsChanged(Integer size) {
		if (selectedBackupVMModel!=null && (selectedBackupVMModel.getVMType() == BackupVMModel.Type.HyperV.ordinal() ||  selectedBackupVMModel.getVMType() == BackupVMModel.Type.VMware_VApp.ordinal()) ) {
			isGotFlasResourceFlag = 1;
		}
		AppEvent event = new AppEvent(RestoreWizardContainer.onRestoreDateChanged, size * isGotBackupVMAdaptersFlag * isGotFlasResourceFlag);
		event.setSource(RestoreWizardContainer.PAGE_VM_RECOVERY);
		fireEvent(RestoreWizardContainer.onRestoreDateChanged, event);
		if(grid != null) {
			grid.mask();
			if(isGotBackupVMAdaptersFlag * isGotFlasResourceFlag != 0)
				grid.unmask();
		}
	}
	
	@Override
	public void show() {
		super.show();
		fireRecoveryPointsChanged(store == null ? 0 : store.getCount());
	}

	public void addRecoveryPointModel(RecoveryPointModel model) {
		// 2015-04-23 fix issue 219024 caused by DST problem
//		Date date = model.getTime();
//		TimeZone timeZone = null;
//		if(model.getTimeZoneOffset() > 0) {
//			timeZone = TimeZone.createTimeZone((int)model.getTimeZoneOffset() / (-60000));
//		}else {
//			timeZone = TimeZone.createTimeZone(UIContext.serverVersionInfo.getTimeZoneOffset()
//					/ (-60000));
//		}
//		int diff = (date.getTimezoneOffset() - timeZone.getOffset(date)) * 60000;
//	    Date keepDate = new Date(date.getTime() + diff);
//	    Date keepTime = keepDate;
//	    if (keepDate.getTimezoneOffset() != date.getTimezoneOffset()) {
//	      if (diff > 0) {
//	        diff -= Utils.NUM_MILLISECONDS_IN_DAY;
//	      } else {
//	        diff += Utils.NUM_MILLISECONDS_IN_DAY;
//	      }
//	      keepTime = new Date(date.getTime() + diff);
//	    }
////		Date date = RecPointModel2ServerDate(model);
////		DateWrapper modelDate = new DateWrapper(date);
//		int hour = keepTime.getHours();
//		int range = hour / 6;
		if (null == model.getD2DTime()) {
			return;
		}
		int range = model.getD2DTime().getHourOfDay() * TimeRangeModel.TIME_PERIODS / 24;
		timeRangeCount[range]++;			
	}
	public void resetTimeRange()
	{
		for (int i = 0; i < timeRangeCount.length; i++)
			timeRangeCount[i] = 0;
	}
	public void refreshTimeRange()
	{
		timeGrid.getSelectionModel().deselectAll();
		int lastRangeWithItems = -1;
		for (int i = 0; i < timeRangeCount.length; i++)
		{
			TimeRangeModel trModel = timeGrid.getStore().getAt(i);
			trModel.setCount(timeRangeCount[i]);
			
			if (timeRangeCount[i] > 0)
			{
				lastRangeWithItems = i;
			}
		}		
		timeGrid.getView().refresh(false);	
		if(lastRangeWithItems >= 0)
			timeGrid.getSelectionModel().select(lastRangeWithItems, false);
	}

	public boolean isRestoreManager() {
		return isRestoreManager;
	}

	public void setRestoreManager(boolean isRestoreManager) {
		this.isRestoreManager = isRestoreManager;
	}

	public RecoveryPointModel getSelectedRecoveryPoint() {
		return grid.getSelectionModel().getSelectedItem();
	}

	public String getSelectedSessionEncyrptionKey() {
		return this.selectedSessionEncryptionKey;
	}
	
	public Date getServerDate() {
		return serverDate;
	}

	private String GetServerDateString(DateWrapper beginDateWrap,
			boolean isKeepHoursMinSec) {
		String strSvrBeginDate = "";
		strSvrBeginDate += beginDateWrap.getFullYear();
		strSvrBeginDate += "-" + (1 + beginDateWrap.getMonth());
		strSvrBeginDate += "-" + beginDateWrap.getDate();

		if (!isKeepHoursMinSec) {
			strSvrBeginDate += " 00:00:00";
		} else {
			strSvrBeginDate += " " + beginDateWrap.getHours();
			strSvrBeginDate += ":" + beginDateWrap.getMinutes();
			strSvrBeginDate += ":" + beginDateWrap.getSeconds();
		}
		return strSvrBeginDate;
	}

	private Date RecPointModel2ServerDate(RecoveryPointModel rpmodel) {
		long timeDiffLocalAndServer = rpmodel.getTime().getTimezoneOffset()
				* 60 * 1000 + rpmodel.getTimeZoneOffset();
		Date serverDate = new Date(rpmodel.getTime().getTime()
				+ timeDiffLocalAndServer);
		return serverDate;
	}

	@Override
	public boolean validate(AsyncCallback<Boolean> callback) {
		boolean isValid = true;
		if(vmList.getStore()==null || vmList.getStore().getCount()==0 ||vmList.getValue() == null){
			isValid = false;
		}

		if (!isValid) {
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			String title = "";
			if(UIContext.uiType == 0){
				title = UIContext.productNameD2D;
			}else{
				title = UIContext.productNamevSphere;
			}
			msg.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(title));
			msg.setMessage(UIContext.Constants
					.vmRecoverySelectVMError());
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
		}
		callback.onSuccess(isValid);
		return isValid;
	}
	
	private void loadCatalogTree(final GridTreeNode currentGridTreeNode, 
			final AsyncCallback<List<GridTreeNode>> callback){
		if(currentGridTreeNode != null){
			currentGridTreeNode.setEncryptedKey(selectedSessionEncryptionKey);
			service.getTreeGridChildren(currentGridTreeNode, getUserName(), 
					getPassword(), callback);
			}

	}
	private IconButton getNodeIcon(GridTreeNode node){
		
		if(node == null)
			return null;
		
		IconButton image = null;
		int nodeType = node.getType();
		switch (nodeType) {
			case CatalogModelType.Folder:
				image = new IconButton("folder-icon");
				break;
			case CatalogModelType.File:
				image = new IconButton("file-icon");
				break;
			case CatalogModelType.OT_VSS_FILESYSTEM_WRITER:
				image = new IconButton("drive-icon");
				break;
			case CatalogModelType.OT_VSS_SQL_WRITER:
				image = new IconButton("sql_server_writer_icon");
				break;
			case CatalogModelType.OT_VSS_SQL_NODE:
				image = new IconButton("sql_server_node_icon");
				break;
			case CatalogModelType.OT_VSS_SQL_LOGICALPATH:
				image = new IconButton("sql_server_icon");
				break;
			case CatalogModelType.OT_VSS_SQL_COMPONENT_SELECTABLE:
				image = new IconButton("sql_server_database");
				break;
			case CatalogModelType.OT_VSS_EXCH_WRITER:
				image = new IconButton("exchange_writer_icon");
				break;	
			case CatalogModelType.OT_VSS_EXCH_SERVER:
				image = new IconButton("exchange_server_name_icon");
				break;
			case CatalogModelType.OT_VSS_EXCH_INFOSTORE:
				image = new IconButton("exchange_store_icon");
				break;
			case CatalogModelType.OT_VSS_EXCH_NODE:
				image = new IconButton("exchange_node_icon");
				break;
			case CatalogModelType.OT_VSS_EXCH_LOGICALPATH:
				image = new IconButton("exchange_storage_group_icon");
				break;
			case CatalogModelType.OT_VSS_EXCH_COMPONENT_SELECTABLE:
				image = new IconButton("exchange_mailbox_icon");
				break;
			case CatalogModelType.OT_VSS_EXCH_COMPONENT_PUBLIC:
				image = new IconButton("exchange_publicfolder_icon");
				break;
			default:
				break;
		}
		if(image != null){
			image.ensureDebugId("F2550482-1AEE-4be5-80D7-C39432F83E4A");
			image.setWidth(20);
			image.setStyleAttribute("font-size", "0");
		}
		
		return image;
	
	}

	public void setDiskToBackupVM(RecoveryPointModel selection){
		isGotBackupVMAdaptersFlag = 0;
		String sessionPath = getSelectedRecoveryPoint().getPath();
		String subPath = "";
		if(sessionPath!=null && !sessionPath.equals("")){
			subPath = sessionPath.substring(sessionPath.indexOf("\\")+1);
		}
		String domain = "";
		String pwd = "";
		String userName = getUserName();
		if (userName != null) {
			int index = userName.indexOf('\\');
			if (index > 0) {
				domain = userName.substring(0, index);
				userName = userName.substring(index + 1);
			}
			pwd = getPassword();
		}
		getBackupVMAdapters(domain, getUserName(), pwd);
		
		////added for vm recovery, update the vm vCenter/esx/
		if (null != selection){
			selectedBackupVMModel.setSubVMEsxHost(selection.getESXHost());
			if((null != selection.getvCenter()) && (!selection.getvCenter().isEmpty())){
				selectedBackupVMModel.setEsxServerName(selection.getvCenter());
			}else{
				selectedBackupVMModel.setEsxServerName(selection.getESXHost());
			}
			
			selectedBackupVMModel.setVMName(selection.getVMName());
		}
		
		if (selectedBackupVMModel.getVMType() == BackupVMModel.Type.VMware.ordinal())
			service.getBackupVMDisk(selectedBackupVMModel.getBrowseDestination(), subPath, domain, userName, pwd, new BaseAsyncCallback<List<DiskModel>>(){
				@Override
				public void onFailure(Throwable caught){
					super.onFailure(caught);
				}
				
				@Override
				public void onSuccess(List<DiskModel> result){
					if(selectedBackupVMModel != null) {
						selectedBackupVMModel.diskList = result;
						getVMVFlashReadCache();
					}
				}
			});
		else{
			service.getHyperVBackupVMDisk(selectedBackupVMModel.getBrowseDestination(), subPath, domain, userName, pwd, new BaseAsyncCallback<List<DiskModel>>(){
				@Override
				public void onFailure(Throwable caught){
					super.onFailure(caught);
				}
				
				@Override
				public void onSuccess(List<DiskModel> result){
					if(selectedBackupVMModel != null)
						selectedBackupVMModel.diskList = result;
				}
			});
		}
	}
	
	private void getBackupVMAdapters(final String domain, final String userName, final String password) {
		isGotBackupVMAdaptersFlag = 0;
		if (selectedBackupVMModel != null) {
			selectedBackupVMModel.adapterList.clear();
		}
		
		if (selectedBackupVMModel.getVMType() == BackupVMModel.Type.VMware.ordinal()){
			service.getVMNetworkConfigList(getSessionPath(), getSelectedSessionID(), userName, password, new BaseAsyncCallback<List<VMNetworkConfigInfoModel>>() {
				@Override
				public void onSuccess(List<VMNetworkConfigInfoModel> modelList) {
					if (selectedBackupVMModel != null) {
						selectedBackupVMModel.adapterList.clear();
						selectedBackupVMModel.adapterList.addAll(modelList);
					}
					isGotBackupVMAdaptersFlag = 1;
					fireRecoveryPointsChanged(isGotBackupVMAdaptersFlag);
				}
	
				@Override
				public void onFailure(Throwable caught) {
					isGotBackupVMAdaptersFlag = 1;
					if (selectedBackupVMModel != null) {
						selectedBackupVMModel.adapterList.clear();
					}
					//super.onFailure(caught);
					fireRecoveryPointsChanged(isGotBackupVMAdaptersFlag);
				}
			});
		}else{
			String sessionPath = getSelectedRecoveryPoint().getPath();
			String subPath = "";
			if(sessionPath!=null && !sessionPath.equals("")){
				subPath = sessionPath.substring(sessionPath.indexOf("\\")+1);
			}
			service.getHyperVVMNetworkConfigList(selectedBackupVMModel.getBrowseDestination(), subPath, domain, userName, password, new BaseAsyncCallback<List<VMNetworkConfigInfoModel>>(){
				@Override
				public void onSuccess(List<VMNetworkConfigInfoModel> modelList) {
					if (selectedBackupVMModel != null) {
						selectedBackupVMModel.adapterList.clear();
						selectedBackupVMModel.adapterList.addAll(modelList);
					}
					isGotBackupVMAdaptersFlag = 1;
					fireRecoveryPointsChanged(isGotBackupVMAdaptersFlag);
				}
	
				@Override
				public void onFailure(Throwable caught) {
					isGotBackupVMAdaptersFlag = 1;
					if (selectedBackupVMModel != null) {
						selectedBackupVMModel.adapterList.clear();
					}
					super.onFailure(caught);
					fireRecoveryPointsChanged(isGotBackupVMAdaptersFlag);
				}
			});
		}
	}
	
	private void getVMVFlashReadCache() {
		isGotFlasResourceFlag = 0;
		selectedBackupVMModel.flashReadCacheSize = -1;
		if (selectedBackupVMModel.getVMType() != BackupVMModel.Type.VMware.ordinal()){
			isGotFlasResourceFlag = 1;
			return;
		}
		
		final String sessionPath = getSessionPath();
		final int sessionID = getSelectedSessionID();
		final String userName = getUserName();
		String tempPwd = "";
		if (userName != null) {
			tempPwd = getPassword();
		}
		final String password = tempPwd;
		service.getVMVFlashReadCache(sessionPath, sessionID, userName, password, new BaseAsyncCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				if (selectedBackupVMModel != null) {
					if (result != null) {
						selectedBackupVMModel.flashReadCacheSize = result;
					} else {
						selectedBackupVMModel.flashReadCacheSize = -1;
					}
				}
				isGotFlasResourceFlag = 1;
				fireRecoveryPointsChanged(isGotFlasResourceFlag);
			}

			@Override
			public void onFailure(Throwable caught) {
				if (selectedBackupVMModel != null) {
					selectedBackupVMModel.flashReadCacheSize = -1;
				}
				//super.onFailure(caught);
				isGotFlasResourceFlag = 1;
				fireRecoveryPointsChanged(isGotFlasResourceFlag);
			}
		});
	}
	
	public BackupVMModel getSelectedBackupVMModel() {
		return selectedBackupVMModel;
	}

	public void setSelectedBackupVMModel(BackupVMModel selectedBackupVMModel) {
		this.selectedBackupVMModel = selectedBackupVMModel;
	}
	
	public RpsHostModel getSrcRPSHost() {
		return sourcePanel.getRpsHost();
	}
	
	public String getRpsPolicy() {
		return sourcePanel.getRpsPolicy();
	}
	
	public String getRpsDataStore() {
		return sourcePanel.getRpsDataStore();
	}
	
	public String getRpsDSDisplayName(){
		return sourcePanel.getRpsDataStoreDisplayName();
	}

	@Override
	public void onDefaultSourceInitialized(boolean succeed) {
		isDefaultSourceInitialized = true;
		if(!succeed)
			return;
		service.getServerTime(new BaseAsyncCallback<Date>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				refreshUI(new Date());
			}

			private void refreshUI(Date date) {
				serverDate = Utils.localTimeToServerTime(date);
				previousSelectedMonth = formateToMonth(serverDate);
				highlightMostRecentMonthRPDates(false);
			}

			@Override
			public void onSuccess(Date result) {
				refreshUI(result);
			}
		});	
		
	}

	@Override
	public void onRestoreSourceTypeChanged() {
		highlightMostRecentMonthRPDates(false);
	}
}
