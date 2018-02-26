package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.FormatUtil;
import com.ca.arcflash.ui.client.common.HighlightedDatePicker;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.CatalogInfoModel;
import com.ca.arcflash.ui.client.model.CatalogInfo_EDB_Model;
import com.ca.arcflash.ui.client.model.CatalogJobParaModel;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.ca.arcflash.ui.client.model.ExchVersion;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RecoveryPointItemModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.RecoveryPointResultModel;
import com.ca.arcflash.ui.client.model.RestoreJobType;
import com.ca.arcflash.ui.client.model.TimeRangeModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.restore.mailboxexplorer.MailboxExplorerPanel;
import com.ca.arcflash.ui.client.restore.mailboxexplorer.MailboxExplorerParameter;
import com.ca.arcflash.ui.client.vsphere.vmrecover.VMRestoreSourcePanel;
import com.extjs.gxt.ui.client.GXT;
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
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.IconButton;
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

public class ExchangeGRTRecoveryPointsPanel extends LayoutContainer implements
		RestoreValidator, IRestoreSourceListener {

	private RestoreWizardContainer wizard;
	private HighlightedDatePicker picker;
	final LoginServiceAsync service = GWT.create(LoginService.class);
	final CommonServiceAsync commonService = GWT.create(CommonService.class);
	
	private boolean isDefaultSourceInitialized = false; // if the default restore source path is initialized. If not, no need to refresh the calendars. 
	private ListStore<RecoveryPointModel> store;
	private TreeStore<GridTreeNode> treeStore;
	private ExchangeGRTRecoveryPointsPanel thisPanel;
	private Grid<RecoveryPointModel> grid;
	private Grid<TimeRangeModel> timeGrid;
	private TreeLoader<GridTreeNode> loader;
	private EditorTreeGrid<GridTreeNode> tree;
	private Integer selectedSessionID;
	private Integer vmHypervisor;
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
	
	private RestoreSourcePanel sourcePanel;

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

	public ExchangeGRTRecoveryPointsPanel(RestoreWizardContainer restoreWizardWindow) {
		thisPanel = this;
		wizard = restoreWizardWindow;
	}

	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_browse_exchange_grt()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		if (isRestoreManager) {
			label.setValue(UIContext.Constants.restoreBrowseExchangeGRTButton());
		} else {
			label.setValue(UIContext.Constants.manageRecoveryPoints());
		}
		label.setStyleName("restoreWizardTitle");
		container.add(label);
		return container;
	}

	private LayoutContainer renderSourceSection() {
		if(UIContext.uiType == Utils.UI_TYPE_VSPHERE)
			sourcePanel = new VMRestoreSourcePanel();
		else
			sourcePanel = new RestoreSourcePanel();
		
		Listener<BaseEvent> changeListener = new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				thisPanel.picker.clearAll();
				//If invoke by pathSelection, there is no need to pop error Dialog. Added by wanqi06
				highlightMostRecentMonthRPDates(true);	
			}
		}; 
		sourcePanel.setPathChangeListener(changeListener);
		sourcePanel.setSourceListener(thisPanel);
		return sourcePanel;	
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
				
				// 2010-11-11 fix Issue: 19819211    Title: AUTO-RESTORE BROWSER SHOW NOTH
				// if the default restore source is not initialized yet, no need to make the following calls				
				// otherwise these calls may return later than those calls when source is initialized
				// then the recovery points will be cleaned. The return sequence is not guaranteed.
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
		//timeGrid.setHeight(115);//218
		//timeGrid.setWidth(177);
		timeGrid.setHeight(170);
		timeGrid.setWidth(181);
		timeGrid.setBorders(false);
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

//	private void refresh(long newOffset, long oldOffset, DateWrapper selectedDate, 
//			DateWrapper endTime, String strBegin){
//		if(newOffset > oldOffset) {
//			String strEnd = Utils.GetServerDateString(selectedDate, 
//					endTime.addHours(-1), true);
//			// filter the other grid by this
//			thisPanel
//					.refreshRecoveryPointFromTimeRangeByServerTime(
//							strBegin, strEnd);
//		}else {
//			//same one
//			String strEnd = Utils.GetServerDateString(selectedDate, 
//					endTime, true);
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
		container.setSize(475, 420);

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
				if(model.isEncrypted())
					return getIncryptionImage("recoverypoint_encryption_icon");
				else {
					return getIncryptionImage("recoverypoint_noencryption_icon");
				}
			}

			private IconButton getIncryptionImage(String style) {
//				Image image = UIContext.IconBundle.logWarning().createImage();
				IconButton image = new IconButton(style);
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
				new Listener<SelectionChangedEvent<RecoveryPointModel>>()
				{
					public void handleEvent(SelectionChangedEvent<RecoveryPointModel> e)
					{
						clearTree();
						final RecoveryPointModel sel = e.getSelectedItem();
						if (sel != null)
						{
							// if any GRT catalog is not created, we need to reload the data, since it may be created after a while
							boolean bIsAnyGRTCatalogNotCreated = false;
							if (sel.listOfCatalogInfo != null)
							{
								for (int i=0; i<sel.listOfCatalogInfo.size(); i++)
								{
									CatalogInfoModel catalogInfoModel = sel.listOfCatalogInfo.get(i);
									if (catalogInfoModel != null && catalogInfoModel.getEdbCatalogInfoList() != null)
									{
										for (int j=0; j<catalogInfoModel.getEdbCatalogInfoList().size(); j++)
										{
											CatalogInfo_EDB_Model catalogInfoEdbModel = catalogInfoModel.getEdbCatalogInfoList().get(j);
											
											if (catalogInfoEdbModel.getIsCatalogCreated() != null 
													&& !(catalogInfoEdbModel.getIsCatalogCreated().booleanValue()))
											{
												bIsAnyGRTCatalogNotCreated = true;
												break;
											}											
										}
										
										if (bIsAnyGRTCatalogNotCreated)
										{
											break;
										}
										
									}
								}
							}
								
							if (sel.listOfRecoveryPointItems == null || sel.listOfRecoveryPointItems.size() == 0
									|| sel.listOfEdbNodes == null || sel.listOfEdbNodes.size() == 0
							    	|| sel.listOfCatalogInfo == null || sel.listOfCatalogInfo.size() == 0
							    	|| bIsAnyGRTCatalogNotCreated)							
							{
								String dest = getSessionPath();
								String domain = "";
								String pwd = "";
								String user = getUserName();
								if (user != null)
								{
									int index = user.indexOf('\\');
									if (index > 0)
									{
										domain = user.substring(0, index);
										user = user.substring(index + 1);
									}
									pwd = getPassword();
								}
								else
								{
									user = "";
								}

								String subPath = sel.getPath();
								long sessionNumber = sel.getSessionID();

								tree.mask(GXT.MESSAGES.loadMask_msg());
								service.getRecoveryPointItems_EDB(dest, domain, user, pwd, subPath, sessionNumber, 
										new BaseAsyncCallback<RecoveryPointResultModel>()
										{

											@Override
											public void onFailure(Throwable caught)
											{
												tree.unmask();
												super.onFailure(caught);
											}

											@Override
											public void onSuccess(RecoveryPointResultModel result)
											{
												tree.unmask();
												sel.listOfRecoveryPointItems = result.getListRecoveryPointItems();
												sel.listOfCatalogInfo = result.getListCatalogInfo();
												sel.listOfEdbNodes = result.getListEdbNodes();
												
												thisPanel.PopulateTreeGrid(sel);
											}
									
										});
							}
							else
							{
								thisPanel.PopulateTreeGrid(e.getSelectedItem());
							}
						}

					}
				});


		/***********************/
		
		CheckColumnConfig checkColumn = new CheckColumnConfig("checked", "", 40);
		checkColumn.setHidden(true);

		ColumnConfig name = new ColumnConfig("displayName", UIContext.Constants.restoreDatabaseColumn(), 185);
		name.setMenuDisabled(true);
		name.setRenderer(new WidgetTreeGridCellRenderer<ModelData>()
		{

			@Override
			public Widget getWidget(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{

				LayoutContainer lc = new LayoutContainer();

				TableLayout layout = new TableLayout();
				layout.setColumns(3);
				lc.setLayout(layout);
				final FlashCheckBox fcb = new FlashCheckBox();

				final GridTreeNode node = (GridTreeNode) model;

				if (exchVer != null)
				{
					if (CatalogModelType.NonSelectExchangeTypes.contains(node.getType()))
					{
						node.setSelectable(false);
					}
					
//					if (CatalogModelType.allExchangeTypes.contains(node.getType()))
//					{
//						node.setSelectable(false);
//					}
				}

				if (node.getSelectable() != null && node.getSelectable() == false)
				{
					fcb.setEnabled(false);
				}
				else
				{
					if (node.getChecked() != null)
					{
						if (node.getChecked())
							fcb.setSelectedState(FlashCheckBox.FULL);
						else
							fcb.setSelectedState(FlashCheckBox.NONE);

					}
					else
					{
						fcb.setSelectedState(FlashCheckBox.NONE);
					}
				}

				fcb.addSelectionListener(new SelectionListener<IconButtonEvent>()
				{
					@Override
					public void componentSelected(IconButtonEvent ce)
					{
						if (fcb.isEnabled() == false)
							return;

						if (tree instanceof ExtEditorTreeGrid<?>)
						{
							ExtEditorTreeGrid<GridTreeNode> extTree = (ExtEditorTreeGrid<GridTreeNode>) tree;
							extTree.getNodeContextMap().remove(node);
							//extTree.getMailboxContextMap().remove(node);
						}

						// Select SQL Instance, should load all its dbs
						// as to be packaged.
						if ((nodeIsSQLInstance(node) || needPackageRSGChildren(node)) && !tree.isExpanded(node))
						{
							List<GridTreeNode> childNodes = tree.getTreeStore().getChildren(node);
							if (childNodes == null || childNodes.size() == 0)
							{
								if (treeStore.getLoader() != null)
								{
									treeStore.getLoader().loadChildren(node);
								}
							}
						}

						selectTreeNodeChildren(node, fcb.getSelectedState(), true);

					}
				});

				FlashCheckBox temp = table.get(node);
				if (temp == null)
				{
					table.put(node, fcb);
					// Check the parent's status
					GridTreeNode parent = tree.getTreeStore().getParent(node);
					FlashCheckBox parentCheckBox = table.get(parent);
					if (parentCheckBox != null)
					{
						if (parentCheckBox.getSelectedState() == FlashCheckBox.FULL)
						{
							fcb.setSelectedState(FlashCheckBox.FULL);
						}
					}
				}
				else
				{
					table.remove(node);
					fcb.setSelectedState(temp.getSelectedState());
					fcb.setEnabled(temp.isEnabled());
					table.put(node, fcb);
				}

				if (isRestoreManager)
				{
					// liuwe05 2011-02-17 fix Issue: 20062702    Title: MULTI MAILBOX RESTORE UI
					// hide the check box here to avoid confusing 
					fcb.setVisible(false);
					lc.add(fcb);
				}

				IconButton image = getNodeIcon(node);
				if (image != null)
					lc.add(image);

				LabelField lf = new LabelField();
				lf.setValue(node.getDisplayName());
				lf.setTitle(node.getDisplayName());
				lc.add(lf);

				return lc;
			}

		});
		
		ColumnConfig path = new ColumnConfig("displayPath", UIContext.Constants.restorePathColumn(), 210);
		path.setRenderer(new GridCellRenderer<BaseModelData>() {
			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex, 
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				
				try
				{
					LabelField messageLabel = new LabelField();
					
					messageLabel.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last "); 
					messageLabel.setStyleAttribute("white-space", "nowrap");
					
					String path = model.get(property);
					//path = ((GridTreeNode)model).getPath();
					if (path != null && path.trim().length() > 0)
					{
						messageLabel.setValue(path);
						messageLabel.setTitle(path);
						return messageLabel;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
                    System.out.println("Error:" + e.getMessage());
				}				
				return "";
			}
		});
		path.setMenuDisabled(true);
		
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
						Long value = ((GridTreeNode) model).getSize();
						String formattedValue = Utils.bytes2String(value);
						return formattedValue;
					}
				} catch (Exception e) {

				}

				return "";
			}

		});

		ColumnConfig catalog = new ColumnConfig("catalogStatus", UIContext.Constants.restoreCatalogStatusColumn(), 90);
		catalog.setMenuDisabled(true);
		catalog.setAlignment(HorizontalAlignment.LEFT);
		catalog.setRenderer(new GridCellRenderer<BaseModelData>() {

			@Override
			public Object render(BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid)
			{
				try
				{	
					// find out if the catalog of the current sub session has been generated
					boolean catalogStatus = false;
					if (model != null && (((GridTreeNode) model).getSubSessionID() != null))
					{					
						int subSessionNumber = ((GridTreeNode) model).getSubSessionID().intValue();
						String edbName = ((GridTreeNode) model).getDisplayName();
					
						// use the list in recovery point model
						RecoveryPointModel recoveryPointModel = getSelectedRecoveryPoint();
						if (recoveryPointModel != null && recoveryPointModel.listOfCatalogInfo != null)
						{							
							for (int i=0; i< recoveryPointModel.listOfCatalogInfo.size(); i++)
							{
								CatalogInfoModel catalogInfoModel = recoveryPointModel.listOfCatalogInfo.get(i);
								if (catalogInfoModel != null && 
										catalogInfoModel.getSubSessNo() !=null && 
										catalogInfoModel.getSubSessNo().intValue() == subSessionNumber &&
										catalogInfoModel.getEdbCatalogInfoList() != null)
								{
									for (int j=0; j<catalogInfoModel.getEdbCatalogInfoList().size(); j++)
									{
										CatalogInfo_EDB_Model catalogInfoEdbModel = catalogInfoModel.getEdbCatalogInfoList().get(j);
										String key = catalogInfoEdbModel.getEdbName();
										if (key != null && key.compareTo(edbName) == 0)
										{
											catalogStatus = catalogInfoEdbModel.getIsCatalogCreated().booleanValue();
										}
									}
									
								}
								
							}
						}
						
						String formattedValue = "";
						if (catalogStatus)
						{
							formattedValue = UIContext.Constants.restoreCatalogCreated();
						}
						else
						{
							formattedValue = UIContext.Constants.restoreCatalogNotCreated();
						}									
						
						return formattedValue;
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});

		treeColModel = new ColumnModel(Arrays.asList(checkColumn, name, path/*,
				date, size*/, catalog));

		RpcProxy<List<GridTreeNode>> proxy = new RpcProxy<List<GridTreeNode>>() {
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<GridTreeNode>> callback) {
				
//				if (loadConfig != null)
//				{
//					// set the parameters for Exchange GRT
//					String backupDestination = pathSelection.getDestination();
//					backupDestination = backupDestination == null ? "" : backupDestination;
//					((GridTreeNode) loadConfig).setBackupDestination(backupDestination);
//
//					Long sessionID = selectedSessionID.longValue();
//					((GridTreeNode) loadConfig).setSessionID(sessionID);
//				}

				service.getTreeGridChildrenEx((GridTreeNode) loadConfig, callback);
			}
		};

		loader = new BaseTreeLoader<GridTreeNode>(proxy) {
			public boolean hasChildren(GridTreeNode parent) {
				Integer type = parent.getType();
				if (type != null && (type == CatalogModelType.File 
						|| CatalogModelType.rootGRTExchangeTypes.contains(type.intValue())))
				{
					return false;
				}
				else
				{
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
				// default sorting
				if (property == null)
				{
					// path first
					if (m1.getDisplayPath() != null && m2.getDisplayPath() != null
							&& !m1.getDisplayPath().equalsIgnoreCase(m2.getDisplayPath()))
					{
						return m1.getDisplayPath().compareToIgnoreCase(m2.getDisplayPath());
					}
					// then type
					else if (m1.getType() != null && m2.getType() != null && !m1.getType().equals(m2.getType()))
					{
						return m1.getType().compareTo(m2.getType());
					}
					// then name
					else if (m1.getDisplayName() != null && m2.getDisplayName() != null)
					{
						return m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName());
					}
					else
					{
						return 0;
					}

				}
				else if (property.equalsIgnoreCase("displayName"))
				{
					return fileNameCompare(m1, m2);
				}
				else
				{
					return super.compare(store, m1, m2, property);
				}
			}
		};
		treeStore = new TreeStore<GridTreeNode>(loader);
		treeStore.setStoreSorter(sorter);

		tree = new ExtEditorTreeGrid<GridTreeNode>(treeStore, treeColModel,
				table, this.isRestoreManager, true);
		tree.ensureDebugId("71b054be-ad99-404f-a2ba-239bf7f3292a");
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
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		if (!this.isRestoreManager) {
			GridSelectionModel<GridTreeNode> sm = new GridSelectionModel<GridTreeNode>();
			sm.setLocked(true);
			tree.setSelectionModel(sm);
			tree.setTrackMouseOver(true);
		}
		
		BorderLayoutData bldN = new BorderLayoutData(LayoutRegion.NORTH, 150, 60, 320);
		bldN.setMargins(new Margins(0, 0, 4, 0));	
		container.add(grid,bldN);
		
		// add a description above the grid
		LabelField labelDesc = new LabelField(UIContext.Constants.restoreGRTSelectEDB());
		LayoutContainer lcEdb = new LayoutContainer();
		{
			BorderLayout layoutEdb = new BorderLayout();
			lcEdb.setLayout(layoutEdb);
			
			BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 35, 25, 25);
			northData.setCollapsible(true);
			northData.setFloatable(true);
			northData.setHideCollapseTool(false);
			northData.setSplit(true);
			northData.setMargins(new Margins(3, 0, 0, 0));
			
			lcEdb.add(labelDesc, northData);
			
			BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
			centerData.setMargins(new Margins(0));			
			
			lcEdb.add(tree, centerData);
		}	
		
		
		BorderLayoutData bldC = new BorderLayoutData(LayoutRegion.CENTER, 316);
		container.add(lcEdb, bldC);
	

		return container;
	}
	
	private void selectTreeNodeChildren(GridTreeNode node, int state, boolean updateParent)
	{
		//Select this node
		FlashCheckBox fcb = table.get(node);
		if (fcb != null)
		{
			fcb.setSelectedState(state);
		}
		
		if (tree.isExpanded(node))
		{
			
			//Get the children
			List<GridTreeNode> childNodes = tree.getTreeStore().getChildren(node);
			//For each call select Children
			for (int i = 0 ; i < childNodes.size(); i++)
			{
				selectTreeNodeChildren(childNodes.get(i), state, false);
			}
		} 
		
		//Set the parent
		if (updateParent)
		{
			selectTreeNodeParent(node);
		}
	}
	
	private void selectTreeNodeParent(GridTreeNode node)
	{
		GridTreeNode parent = tree.getTreeStore().getParent(node);
		int parentState = FlashCheckBox.NONE;
		if (parent != null)
		{
			int fullCount = 0;
			int partialCount = 0;
			int emptyCount = 0;
			int nullCount = 0;
			
			List<GridTreeNode> childNodes = tree.getTreeStore().getChildren(parent);
			//For each call select Children
			for (int i = 0 ; i < childNodes.size(); i++)
			{
				FlashCheckBox fcb = table.get(childNodes.get(i));
				if (fcb != null)
				{
					switch (fcb.getSelectedState())
					{
						case FlashCheckBox.FULL:
							fullCount++;
							break;
						case FlashCheckBox.PARTIAL:
							partialCount++;
							break;
						case FlashCheckBox.NONE:
						default:
							emptyCount++;
							break;
					}
				}
				else 
				{
					nullCount++;
				}
			}
			
			if (emptyCount + nullCount == childNodes.size())
			{
				parentState = FlashCheckBox.NONE;
			}
			else
			{
				parentState = FlashCheckBox.PARTIAL;
			}
			
			FlashCheckBox fcb = table.get(parent);
			if (fcb != null)
			{
				fcb.setSelectedState(parentState);
				//Parent changed, change the parent's parent
				selectTreeNodeParent(parent);
			}
		}
	}	

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

		this.add(renderSourceSection(), new RowData(1, -1));

		label = new LabelField();
		label.addStyleName("restoreWizardSubItem");
		label.setValue(UIContext.Constants.restoreFilesToRestore());
		this.add(label, new RowData(1, -1));

		//override onClick method to highlight Days with recovery points.
		picker = new HighlightedDatePicker()
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
		this.add(renderTableSection(), new RowData(1, 1));

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
	private List<GridTreeNode> getPagedSelectedNodes()
	{
		List<GridTreeNode> nodes = new ArrayList<GridTreeNode>();
		if (tree instanceof ExtEditorTreeGrid)
		{
			ExtEditorTreeGrid extTree = (ExtEditorTreeGrid) tree;
			nodes = extTree.getPagedSelectedNodes();
			nodes.addAll(extTree.getExchangeGRTSelectedNodes());
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

	protected void PopulateTreeGrid(RecoveryPointModel selection)
	{

		treeStore.removeAll();
		// 1) Create tree nodes from the selection.items
		if (selection != null)
		{
			try
			{
				rootItemMap.clear();
				selectedSessionID = selection.getSessionID();
				vmHypervisor = selection.getVMHypervisor();
				List<GridTreeNode> newstore = new ArrayList<GridTreeNode>();
				if (selection.listOfRecoveryPointItems != null)
				{
					for (int i = 0; i < selection.listOfRecoveryPointItems.size(); i++)
					{
						RecoveryPointItemModel rpm = (RecoveryPointItemModel) selection.listOfRecoveryPointItems.get(i);
						GridTreeNode node = ConvertToGridTreeNode(rpm);

						// add only the Exchange recovery point items
						if (node != null && CatalogModelType.allExchangeTypes.contains(node.getType()))
						{
							newstore.add(node);
							rootItemMap.put(node, rpm);
						}
					}
				}
				//treeStore.add(newstore, false);
				
				// add the EDB nodes directly
				//if (selection.listOfEdbNodes != null)
				//we don't support restoring mail of exchange version 2013, so we don't display the EDB of 2013 
//				if (exchVer != ExchVersion.Exch2013 && selection.listOfEdbNodes != null)
				if (selection.listOfEdbNodes != null)
				{
					treeStore.add(selection.listOfEdbNodes, false);
				}
				
				// update the status of next button
				fireRecoveryPointsChanged(treeStore.getChildCount());
				
				// select the first by default
				tree.getSelectionModel().select(0, false);
				
				tree.getView().scrollToTop();
			}
			catch (Exception e)
			{

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
		
		if (m.getGuid().equals(GUID_SQLWRITER)) {
			node.setType(CatalogModelType.OT_VSS_SQL_WRITER);
			node.setPath(m.getGuid());
		} 
		
		// use name to identify Exchange versions
		else if (m.getDisplayName().equals(GUID_EXCHANGE_2003))
		{
			exchVer = ExchVersion.Exch2003;
			setExchCommon(node, GUID_EXCHANGE_WRITER);
		}
		else if (m.getDisplayName().equals(GUID_EXCHANGE_2007))
		{
			exchVer = ExchVersion.Exch2007;
			setExchCommon(node, GUID_EXCHANGE_WRITER);
		}
		else if (m.getDisplayName().equals(GUID_EXCHANGE_REPLICA_2007))
		{
			exchVer = ExchVersion.Exch2007;
			setExchCommon(node, GUID_EXCHANGE_REPLICA_WRITER);
		}
		else if (m.getDisplayName().equals(GUID_EXCHANGE_2010))
		{
			exchVer = ExchVersion.Exch2010;
			setExchCommon(node, GUID_EXCHANGE_WRITER);
		}
		else if (m.getDisplayName().equals(GUID_EXCHANGE_REPLICA_2010))
		{
			exchVer = ExchVersion.Exch2010;
			setExchCommon(node, GUID_EXCHANGE_REPLICA_WRITER);
		}
		else if (m.getDisplayName().equals(GUID_EXCHANGE_2013))
		{
			exchVer = ExchVersion.Exch2013;
			setExchCommon(node, GUID_EXCHANGE_WRITER);
		}
		else if (m.getDisplayName().equals(GUID_EXCHANGE_REPLICA_2013))
		{
			exchVer = ExchVersion.Exch2013;
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
				D2DTimeModel asStartDate = Utils.getD2DTime(startDate, false);// Utils.serverTimeToLocalTime(startDate.asDate());
				D2DTimeModel asEndDate = Utils.getD2DTime(endDate, false);
				service.getRecoveryPointsByServerTime(destination, domain,
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
									
									// skip the old recovery points, they don't have GRT catalog and it cannot be generated by design
									if (result[i] != null && result[i].getSessionVersion() != null)
									{
										if (result[i].getSessionVersion().longValue() < 0)
										{
											continue;
										}
									}
									
									RecoveryPointModel m = result[i];

									Date serverDate = RecPointModel2ServerDate(m);

									boolean isInCurrentMonth = serverDate.getMonth() == currentMonth;
									picker.addSelectedDate(serverDate, isInCurrentMonth);

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

	private void noRecoveryPointsFound(){
		MessageBox box = new MessageBox();
		box.setTitleHtml(Utils.getProductName());
		box.setIcon(MessageBox.INFO);
		box.setButtons(MessageBox.OK);
		box.setMessage(UIContext.Constants.noRecoveryPointsFound());
		box.show();
	}
	
	//Add a param to decide whether pop error dialog or not, added by wanqi06
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
//		String asStartDate = this.GetServerDateString(startDate, false);
//		String asEndDate = GetServerDateString(endDate, false);
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
							noRecoveryPointsFound();
						} else {
							for (int i = 0; i < result.length; i++) {
								
								// skip the old recovery points, they don't have GRT catalog and it cannot be generated by design
								if (result[i] != null && result[i].getSessionVersion() != null)
								{
									if (result[i].getSessionVersion().longValue() < 0)
									{
										continue;
									}
								}
								
								Date serverDate = RecPointModel2ServerDate(result[i]);
								if (mostRecentMonth == -1) {
									mostRecentMonth = serverDate.getMonth();
									previousSelectedMonth = formateToMonth(serverDate);
								}

								picker.addSelectedDate(serverDate, serverDate.getMonth() == mostRecentMonth);
								
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
//		// From the Time Grid Only
//		String destination = getSessionPath();
//		if(destination == null){
//			timeGrid.unmask();
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
//
//		service.getRecoveryPointsByServerTime(destination, domain, userName,
//				pwd, beginDate, endDate, false,
//				new BaseAsyncCallback<RecoveryPointModel[]>() {
//					@Override
//					public void onFailure(Throwable caught) {
//						clearRecoveryPoints();
//						timeGrid.unmask();
//						super.onFailure(caught);
//					}
//
//					@Override
//					public void onSuccess(RecoveryPointModel[] result) {
//						clearRecoveryPoints();
//						if (result != null) {
//							
//							List<RecoveryPointModel> newstore = new ArrayList<RecoveryPointModel>();
//							for (int i = 0; i < result.length; i++) {
//								
//								// skip the old recovery points, they don't have GRT catalog and it cannot be generated by design
//								if (result[i] != null && result[i].getSessionVersion() != null)
//								{
//									if (result[i].getSessionVersion().longValue() < 0)
//									{
//										continue;
//									}
//								}
//								
//								GWT.log(result[i].getName(), null);
//								newstore.add(result[i]);
//							}
//							thisPanel.store.add(newstore);
//							fireRecoveryPointsChanged(newstore.size());
//							if (result.length > 0) {
//								thisPanel.grid.getSelectionModel().select(0,
//										false);
//								thisPanel.grid.getView().scrollToTop();
//							}
//						}
//						timeGrid.unmask();
//					}
//
//				});
//	}
	public void refreshRecoveryPointFromTimeRangeByServerTime(D2DTimeModel beginDate,
			D2DTimeModel endDate) {
		// From the Time Grid Only
		String destination = getSessionPath();
		if(destination == null){
			timeGrid.unmask();
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

		service.getRecoveryPointsByServerTime(destination, domain, userName,
				pwd, beginDate, endDate, false,
				new BaseAsyncCallback<RecoveryPointModel[]>() {
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
								
								// skip the old recovery points, they don't have GRT catalog and it cannot be generated by design
								if (result[i] != null && result[i].getSessionVersion() != null)
								{
									if (result[i].getSessionVersion().longValue() < 0)
									{
										continue;
									}
								}
								
								GWT.log(result[i].getName(), null);
								newstore.add(result[i]);
							}
							thisPanel.store.add(newstore);
							fireRecoveryPointsChanged(newstore.size());
							if (result.length > 0) {
								thisPanel.grid.getSelectionModel().select(0,
										false);
								thisPanel.grid.getView().scrollToTop();
							}
						}
						timeGrid.unmask();
					}

				});
	}

	// 2015-04-08 fix issue 214818 caused by DST problem
//	public void refreshRecoveryPointDataByServerTime(String serverBeginDate,
//			String serverEndDate) {
//		String destination = getSessionPath();
//		if(destination == null){
//			return;
//		}
//		picker.setEnabled(false);
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
//
//		service.getRecoveryPointsByServerTime(destination, domain, userName,
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
//								// skip the old recovery points, they don't have GRT catalog and it cannot be generated by design
//								if (result[i] != null && result[i].getSessionVersion() != null)
//								{
//									if (result[i].getSessionVersion().longValue() < 0)
//									{
//										continue;
//									}
//								}
//								GWT.log(result[i].getName(), null);
//								addRecoveryPointModel(result[i]);
//							}
//							refreshTimeRange();
//						}
//						picker.setEnabled(true);
//					}
//
//				});
//	}
	public void refreshRecoveryPointDataByServerTime(D2DTimeModel serverBeginDate,
			D2DTimeModel serverEndDate) {
		String destination = getSessionPath();
		if(destination == null){
			return;
		}
		picker.setEnabled(false);
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

		service.getRecoveryPointsByServerTime(destination, domain, userName,
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
								// skip the old recovery points, they don't have GRT catalog and it cannot be generated by design
								if (result[i] != null && result[i].getSessionVersion() != null)
								{
									if (result[i].getSessionVersion().longValue() < 0)
									{
										continue;
									}
								}
								GWT.log(result[i].getName(), null);
								addRecoveryPointModel(result[i]);
							}
							refreshTimeRange();
						}
						picker.setEnabled(true);
					}

				});
	}

	private void clearRecoveryPoints() {
		thisPanel.store.removeAll();
		fireRecoveryPointsChanged(0);
		clearTree();
	}

	private void clearTree() {
		thisPanel.treeStore.removeAll();
		
		fireRecoveryPointsChanged(0);
		
		table.clear();
		if (tree instanceof ExtEditorTreeGrid) {
			if (isRestoreManager) {
				((ExtEditorTreeGrid) tree).getNodeContextMap().clear();
				((ExtEditorTreeGrid) tree).getMailboxContextMap().clear();
				
				// clear the mailbox explorer panel together with the ExtEditorTreeGrid
				MailboxExplorerPanel explorer = wizard.getMailboxExplorerPanel();
				if (explorer != null)
				{
					explorer.clearMailboxExplorerPanel();
				}
			}
		}
	}

	private void fireRecoveryPointsChanged(Integer size) {
		AppEvent event = new AppEvent(RestoreWizardContainer.onRestoreDateChanged, size);
		event.setSource(RestoreWizardContainer.PAGE_EXCHANGE_GRT_RECOVERY);
		fireEvent(RestoreWizardContainer.onRestoreDateChanged, event);
	}
	
	@Override
	public void show() {
		super.show();
				
		fireRecoveryPointsChanged(treeStore == null ? 0 : treeStore.getChildCount());
	}

/*	public void addRecoveryPointModel(RecoveryPointModel model) {
		Date date = model.getTime();
		TimeZone timeZone = null;
		if(model.getTimeZoneOffset() > 0) {
			timeZone = TimeZone.createTimeZone((int)model.getTimeZoneOffset() / (-60000));
		}else {
			timeZone = TimeZone.createTimeZone(UIContext.serverVersionInfo.getTimeZoneOffset()
					/ (-60000));
		}
		int diff = (date.getTimezoneOffset() - timeZone.getOffset(date)) * 60000;
	    Date keepDate = new Date(date.getTime() + diff);
	    Date keepTime = keepDate;
	    if (keepDate.getTimezoneOffset() != date.getTimezoneOffset()) {
	      if (diff > 0) {
	        diff -= Utils.NUM_MILLISECONDS_IN_DAY;
	      } else {
	        diff += Utils.NUM_MILLISECONDS_IN_DAY;
	      }
	      keepTime = new Date(date.getTime() + diff);
	    }
//		Date date = RecPointModel2ServerDate(model);
//		DateWrapper modelDate = new DateWrapper(date);
		int hour = keepTime.getHours();
		int range = hour / 6;
		timeRangeCount[range]++;	
	}*/
	public void addRecoveryPointModel(RecoveryPointModel model){
		// 2015-04-23 fix issue 219024 caused by DST problem
//		Date date = model.getTime();
//		Date keepTime=Utils.formatTimeToServerTime(date, model.getTimeZoneOffset() == null ? 0 : model.getTimeZoneOffset());
//	    int hour = keepTime.getHours();
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
	public boolean validate(AsyncCallback<Boolean> callback)
	{
		boolean bValid = false;
		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == 1){
			productName = UIContext.productNamevSphere;
		}
		// check if the selection is an EDB
		final GridTreeNode selectedNode = this.tree.getSelectionModel().getSelectedItem();
		
		if (selectedNode != null)
		{
			if (selectedNode.getType() != null
					&& CatalogModelType.rootGRTExchangeTypes.contains(selectedNode.getType()))
			{
				bValid = true;
			}
		}
		
		if (!bValid)
		{
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.INFO);
			msg.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(productName));
			msg.setMessage(UIContext.Constants.restoreMsgSelectEDB());
			msg.setModal(true);
			// set the debug id for automation testing
			msg.getDialog().ensureDebugId("27715c6f-2299-41f2-b824-ff6471ce24eb");
			Utils.setMessageBoxDebugId(msg);
			msg.show();
			callback.onSuccess(bValid);
		}
		
		// check if the grt catalog file exists, if not, it needs to generate the catalog before browsing into EDB
		if (bValid)
		{
			final AsyncCallback<Boolean> cbReturn = callback;

			// set the parameters for Exchange GRT 
			String backupDestination = getSessionPath();
			backupDestination = backupDestination == null ? "" : backupDestination;
			selectedNode.setBackupDestination(backupDestination);

			Long sessionID = selectedSessionID.longValue();
			selectedNode.setSessionID(sessionID);
			
			
			
			if(vmHypervisor!=0){
				cbReturn.onSuccess(false);
				{
					MessageBox box = new MessageBox();
					box.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(productName));
					box.setMessage(UIContext.Constants.disableExchangeGRTForHypervVM());
					box.setIcon(MessageBox.INFO);
					box.setButtons(MessageBox.OK);
					box.setModal(true);
					box.setMinWidth(400); // avoid the text showing not aligned
					// set the debug id for automation testing
					Utils.setMessageBoxDebugId(box);
					//box.getDialog().ensureDebugId("374ce17b-4862-4038-b0c3-59b9ab742157");
					box.show();
					//wizard.nextButton.setEnabled(false);
					return false;
				}
			}
			
			
			// validate the catalog file
			service.validateCatalogFileExist(selectedNode, new BaseAsyncCallback<Long>(productName)
			{
				@Override
				public void onFailure(Throwable caught)
				{
					super.onFailure(caught);
					cbReturn.onSuccess(false);
				}

				@Override
				public void onSuccess(Long result)
				{
					switch (result.intValue())
					{
					case 1:  // catalog exist
						cbReturn.onSuccess(true);
						break;
					case 2: // catalog job is running, just waiting
						cbReturn.onSuccess(false);
						{
							MessageBox box = new MessageBox();
							box.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(productName));
							box.setMessage(UIContext.Messages.restoreCatalogJobIsRunning(productName));
							box.setIcon(MessageBox.INFO);
							box.setButtons(MessageBox.OK);
							box.setModal(true);
							box.setMinWidth(400); // avoid the text showing not aligned
							Utils.setMessageBoxDebugId(box);
							// set the debug id for automation testing
							box.show();
						}
						break;
					case 3: // catalog job is in queue, just waiting
						cbReturn.onSuccess(false);
						{
							MessageBox box = new MessageBox();
							box.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(productName));
							box.setMessage(UIContext.Constants.restoreCatalogJobIsInQueue());
							box.setIcon(MessageBox.INFO);
							box.setButtons(MessageBox.OK);
							box.setModal(true);
							box.setMinWidth(400); // avoid the text showing not aligned
							// set the debug id for automation testing
							Utils.setMessageBoxDebugId(box);
							//box.getDialog().ensureDebugId("18e5fd36-2b9b-40aa-af0c-20947a7bffcd");
							box.show();
						}
						break;
					case 4: // OS mismatch, the GRT restore will fail.
						cbReturn.onSuccess(false);
						{
							MessageBox box = new MessageBox();
							box.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(productName));
							box.setMessage(UIContext.Constants.restoreGRTOSMismatch());
							box.setIcon(MessageBox.INFO);
							box.setButtons(MessageBox.OK);
							box.setModal(true);
							box.setMinWidth(400); // avoid the text showing not aligned
							// set the debug id for automation testing
							Utils.setMessageBoxDebugId(box);
							//box.getDialog().ensureDebugId("374ce17b-4862-4038-b0c3-59b9ab742157");
							box.show();
						}
						break;
					case 0: // catalog file doesn't exist, submit the job
						{
							cbReturn.onSuccess(false);

                            final ExchangeGRTSubmitCatalogJobDialog dialogCatalogJob = new ExchangeGRTSubmitCatalogJobDialog(
									getSelectedRecoveryPoint(), getSessionPath());
							dialogCatalogJob.addWindowListener(new WindowListener()
							{
								public void windowHide(WindowEvent we)
								{
									if (dialogCatalogJob.getDialogResult() == Dialog.OK)
									{
										String encryptionPassword = dialogCatalogJob.getPassword();										
										
										CatalogJobParaModel catalogJobModel = new CatalogJobParaModel();
										catalogJobModel.setBackupDestination(getSessionPath());
										catalogJobModel.setUserName(getUserName());
										catalogJobModel.setPassword(getPassword());
										catalogJobModel.setSessionNumber(selectedSessionID.longValue());
										catalogJobModel.setSubSessionNumber(selectedNode.getSubSessionID().longValue());
										catalogJobModel.setSessionGUID(getSelectedRecoveryPoint().getSessionGuid());
										catalogJobModel.setEncryptionPassword(encryptionPassword);
										List<String> grtEdbList = new ArrayList<String>();
										grtEdbList.add(selectedNode.getName());
										catalogJobModel.setGRTEdbList(grtEdbList);
										//for vsphere
										if(UIContext.uiType == Utils.UI_TYPE_VSPHERE && UIContext.backupVM !=null)											
											catalogJobModel.setVMInstanceUUID(UIContext.backupVM.getVmInstanceUUID());									
										
										wizard.mask(UIContext.Constants.restoreExchangeGRTSubmitGRTMask());
										
										service.submitCatalogJob(catalogJobModel,
												new BaseAsyncCallback<Void>()
												{

													@Override
													public void onFailure(Throwable caught)
													{
														wizard.unmask();
														if(caught instanceof BusinessLogicException) {
															BusinessLogicException ble = (BusinessLogicException)caught;
															if(("4294967314".equals((ble).getErrorCode()))) {
																MessageBox.info(UIContext.Messages.messageBoxTitleInformation(
																		Utils.getProductName()), 
																		(ble).getDisplayMessage(), null);
																return ;
															}
														}
														super.onFailure(caught);
													}

													@Override
													public void onSuccess(Void result)
													{
														wizard.unmask();
														wizard.ClosePage(true);
														Info.display(UIContext.Messages.messageBoxTitleInformation(productName),
																UIContext.Constants.restoreCatalogJobSubmitted());
													}

												});
									}
								}
							});
							dialogCatalogJob.show();
						}
						break;
					default:
						{
							cbReturn.onSuccess(true);
						}
						break;
					}
					
					

				}
			});

		}

		
		return bValid;		
	}
	
	public boolean validateForExplorer(AsyncCallback<Boolean> callback) {
		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == 1){
			productName = UIContext.productNamevSphere;
		}
		List<GridTreeNode> selectedNodes = GetSelectedNodes();
		
		//Check 1: No selection
		if (selectedNodes.size() == 0)
		{
			final MessageBox errMessage = MessageBox.info(UIContext.Messages.messageBoxTitleInformation(productName),
					UIContext.Constants.restoreMsgNoSelection(), null);
			errMessage.setModal(true);
			errMessage.setIcon(MessageBox.ERROR);
			Utils.setMessageBoxDebugId(errMessage);
			errMessage.show();

			callback.onSuccess(Boolean.FALSE);
			return false;
		}
		
		//Check 2: BOOT/System Volume Restore not allow full volume restore.
		for(GridTreeNode node : selectedNodes)
		{
			if(rootItemMap.containsKey(node))
			{
				RecoveryPointItemModel rpm = rootItemMap.get(node);
				if(rpm.getVolAttr() != null)
				{
					int volAttr = rpm.getVolAttr();
					if( (volAttr& BootVol) > 0 || (volAttr & SysVol) > 0)
					{
						MessageBox errMsg = new MessageBox();
						errMsg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
						errMsg.setMessage(UIContext.Constants.restoreNotAllowFullVolumeRestore4BootOrSystem());
						errMsg.setModal(true);
						errMsg.setIcon(MessageBox.ERROR);
						Utils.setMessageBoxDebugId(errMsg);
						errMsg.show();
						callback.onSuccess(Boolean.FALSE);						
						return false;						
					}
				}				
			}			
		}


		//Check 3: Volume(Disk) and App not allow in one job. only can select one type: File/SQL/Exchange
		RestoreJobType jobType = RestoreUtil.getJobType(selectedNodes);
		boolean isValid = true;
		if (jobType == null)
		{
			isValid = false;
		}
		else if (jobType == RestoreJobType.GRT_Exchange)
		{
			boolean isReplica = false;
			boolean isNormalExch = false;
			for (int i = 0; i < selectedNodes.size(); i++)
			{
				GridTreeNode node = selectedNodes.get(i);
				GridTreeNode parent = tree.getTreeStore().getParent(node);
				while (parent != null)
				{
					node = parent;
					parent = tree.getTreeStore().getParent(node);
				}
				
				// considering the GUID for Exchange 2007, check the prefix instead of the whole string
				if (node != null && node.getPath() != null && 
						node.getPath().startsWith(GUID_EXCHANGE_REPLICA_WRITER))
				{
					isReplica = true;
				}
				else if (node != null && node.getPath() != null && 
						node.getPath().startsWith(GUID_EXCHANGE_WRITER)) 
				{
					isNormalExch = true;
				}
			}

			if (isReplica && isNormalExch)
			{
				isValid = false;
			}
		}

		if (!isValid)
		{
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.INFO);
			msg.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(productName));
			msg.setMessage(UIContext.Constants.restoreGRTSelectNormalAndReplica());
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
		}
		callback.onSuccess(isValid);
		return isValid;
	}
	
	public static IconButton getNodeIcon(GridTreeNode node){
		
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
				
			// -- begin of Exchange GRT	
			case CatalogModelType.OT_GRT_EXCH_MBSDB:
				image = new IconButton("exchange_grt_edb_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_CALENDAR:				
				image = new IconButton("exchange_grt_folder_icon");
				break;
			case CatalogModelType.OT_GRT_EXCH_CALENDAR_ITEM:				
				image = new IconButton("exchange_grt_calendar_item_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_CONTACTS:
				image = new IconButton("exchange_grt_folder_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_CONTACTS_ITEM:
				image = new IconButton("exchange_grt_contact_item_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_CONTACTS_GROUP:
				image = new IconButton("exchange_grt_contact_group_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_DRAFT:
				image = new IconButton("exchange_grt_draft_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_JOURNAL:
				image = new IconButton("exchange_grt_journal_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_NOTES:
				image = new IconButton("exchange_grt_notes_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_TASKS:
				image = new IconButton("exchange_grt_tasks_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_PUBLIC_FOLDERS:
				image = new IconButton("exchange_grt_public_folder_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_MAILBOX:
				image = new IconButton("exchange_grt_mailbox_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_DELETED_ITEMS:
				image = new IconButton("exchange_grt_deleted_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_INBOX:
				image = new IconButton("exchange_grt_inbox_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_OUTBOX:
				image = new IconButton("exchange_grt_outbox_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_SENT_ITEMS:
				image = new IconButton("exchange_grt_sent_items_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_FOLDER:
				image = new IconButton("exchange_grt_folder_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_MESSAGE:
				image = new IconButton("exchange_grt_message_icon");
				break;
			
			// -- end of Exchange GRT icons
				
				// -- begin of SharePoint GRT	
			case CatalogModelType.OT_GRT_SP_DB:
				image = new IconButton("sql_server_database");
				break;
				
			case CatalogModelType.OT_GRT_SP_SITE:
				image = new IconButton("sharepoint_grt_site_icon");
				break;
				
			case CatalogModelType.OT_GRT_SP_WEB:
				image = new IconButton("sharepoint_grt_web_icon");
				break;
				
			case CatalogModelType.OT_GRT_SP_LIST:
				image = new IconButton("sharepoint_grt_list_icon");
				break;
				
			case CatalogModelType.OT_GRT_SP_FOLDER:
				image = new IconButton("sharepoint_grt_folder_icon");
				break;
				
			case CatalogModelType.OT_GRT_SP_FILE:
				image = new IconButton("sharepoint_grt_file_icon");
				break;
				
			case CatalogModelType.OT_GRT_SP_VERSION:
				image = new IconButton("sharepoint_grt_version_icon");
				break;
				
			// -- end of SharePoint GRT icons
				
			default:
				break;
		}
		if(image != null){
			image.setWidth(20);
			image.setStyleAttribute("font-size", "0");
		}
		
		return image;
	
	}
	
	
	public void getInfoForMailboxExplorer(MailboxExplorerParameter para)
	{
		if (para != null && tree != null)
		{
			para.edbNode = tree.getSelectionModel().getSelectedItem();

			if (para.edbNode != null && table != null)
			{
				para.edbCheckbox = table.get(para.edbNode);
			}

			para.treeGrid = ((ExtEditorTreeGrid) tree);
			para.mailboxContextMap = ((ExtEditorTreeGrid) tree).getMailboxContextMap();

			// get the backup destination and session id from parent node
			para.edbNode.setBackupDestination(getSessionPath());
			
			if (selectedSessionID != null)
			{
				para.edbNode.setSessionID(selectedSessionID.longValue());
			}
		}
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
}
