package com.ca.arcflash.ui.client.coldstandby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HighlightedDatePicker;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.TimeRangeModel;
import com.ca.arcflash.ui.client.monitor.MonitorService;
import com.ca.arcflash.ui.client.monitor.MonitorServiceAsync;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelMarker;
import com.extjs.gxt.ui.client.data.BeanModelMarker.BEAN;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
public class ProvisionPointsContainer extends LayoutContainer {

	private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	private final MonitorServiceAsync monitorService = GWT.create(MonitorService.class);
	protected BeanModelFactory factory = BeanModelLookup.get().getFactory(VMSnapshotsInfo.class);
	protected Grid<BeanModel> grid;
	protected ColumnModel columnModel;
	protected ListStore<BeanModel> store;

	protected BaseAsyncCallback<VMSnapshotsInfo[]> callback;
	protected ContentPanel contentPanel;
	protected static final String TimeColumn = "time";
	protected static final String ProvisionColumn = "provision";
	protected static final String timeRendererControl = "timeRendererControl";
	protected Date latestProvisionPointsDate;
	protected RadioGroup rg = new RadioGroup();
	protected VMSnapshotsInfo selectedSnapshotInfo;
	protected BeanModel currentHighligthedModel = null;
	//currentHighligthedUUID == null if highlight snapshot fetching function does not returns.
	//currentHighligthedUUID == NULL_STRING or other non-null value if highlight snapshot fetching function returns.
	protected String currentHighligthedUUID = null;
	protected static String NULL_STRING = new String();
	
	protected HighlightedDatePicker datePicker;
	protected Map<String, Set<BeanModel>> dateToSnapshotSet = new HashMap<String, Set<BeanModel>>();
	protected DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
	protected RowGridView view;
	protected Grid<TimeRangeModel> timeGrid;
	protected int timeRangeCount[] = new int[8];
	
	protected ProvisionWindow provWindow = null;
	
	protected int gridHeight = 403;
	protected BaseAsyncCallback<Void> failoverCallback;
	protected BaseAsyncCallback<String> currentSnapShotcallback;
	
	protected HorizontalPanel textPanel = new HorizontalPanel();
	protected final Anchor link = new Anchor();
	protected final Label textLabel = new Label();
	protected boolean usingTCPIPSetting = false;
	
	public ProvisionPointsContainer(ProvisionWindow provWindow) {
		if (provWindow != null) {
			init(provWindow);
		}
	}
	
	protected void init(ProvisionWindow provWindow) {
		this.provWindow = provWindow;
		
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		setLayout(layout);
		
		Label label = getTitleLabel();
		TableData data = new TableData();
		data.setPadding(4);
		data.setColspan(2);
		add(label, data);
		
		VerticalPanel leftPanel = new VerticalPanel();
		
		initDatePicker();
		leftPanel.add(datePicker);
		
		initTimeRangeGrid();
		leftPanel.add(timeGrid);
		
		initSnapshotGrid();
	    contentPanel = new ContentPanel();
	    contentPanel.setHeaderVisible(false);
	    contentPanel.setLayout(new FitLayout());
	    contentPanel.add(grid);
	    contentPanel.setHeight(gridHeight);
	    
	    data = new TableData();
		data.setVerticalAlign(VerticalAlignment.TOP);
		data.setPadding(4);
		add(leftPanel, data);
	    
	    add(contentPanel, data);
	    
	    initCallbackFunction();
		
		update();
		link.ensureDebugId("1a9bf070-f0e4-4df6-8fa7-e88c57be7e16");

		textPanel.add(link);
		TableData textData = new TableData();
		textData.setColspan(2);
		add(textPanel, textData);
		updateLinkText();
	}

	protected void updateLinkText() {
		if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
			monitorService.getFailoverJobScript(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().getUuid(), new AsyncCallback<FailoverJobScript>() {

				@Override
				public void onFailure(Throwable caught) {
					usingTCPIPSetting = false;
				}

				@Override
				public void onSuccess(FailoverJobScript result) {
					usingTCPIPSetting = result.isIPSettingsFromVCM();
				}
				
			});
		}
		else {
			service.getFailoverJobScript(ColdStandbyManager.getVMInstanceUUID(), new AsyncCallback<FailoverJobScript>() {

				@Override
				public void onFailure(Throwable caught) {
					usingTCPIPSetting = false;
				}

				@Override
				public void onSuccess(FailoverJobScript result) {
					usingTCPIPSetting = result.isIPSettingsFromVCM();
				}
				
			});
		}
	}
	
	protected void initTimeRangeGrid() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setId("rangeString");
		column.setHeaderHtml(UIContext.Constants.restoreTimeRangeColumn());
		column.setWidth(177);
		
		column.setRenderer(new GridCellRenderer<TimeRangeModel>(){

			@Override
			public Object render(TimeRangeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<TimeRangeModel> store, Grid<TimeRangeModel> grid) {
				
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
		
		//3 hour intervals		
		for (int i = 0; i < 24; i = i + 3)
		{
			DateWrapper startDate = new DateWrapper();
			startDate = startDate.clearTime();			
			startDate = startDate.addHours(i);
			DateWrapper endDate = startDate.addHours(3).addSeconds(-1);
			//check whether it's DST start in UI side
			if(startDate.getHours() != i){
				startDate = startDate.addDays(Utils.MAKEUP_DST_STARTS);
				startDate = startDate.clearTime();
				startDate = startDate.addHours(i);
				endDate = startDate.addHours(3).addSeconds(-1);
			}else if(endDate.getHours() - startDate.getHours() != 2) {
				endDate = endDate.addDays(Utils.MAKEUP_DST_STARTS);
				endDate = endDate.clearTime();
				endDate = endDate.addHours(i+3).addSeconds(-1);
			}
			TimeRangeModel model = new TimeRangeModel();
			model.setStartDate(startDate.asDate());
			model.setEndDate(endDate.asDate());
			String strRange = Utils.formatTime(startDate.asDate()) + " - " +  Utils.formatTime(endDate.asDate());			
			model.setRange(strRange);			
			rangeStore.add(model);
		}
		
		timeGrid = new Grid<TimeRangeModel>(rangeStore, columnModel);
		timeGrid.setStyleAttribute("margin-top", "5px");
		timeGrid.setHeight(200);
		timeGrid.setWidth(177);
//		timeGrid.setBorders(true);
//		timeGrid.setAutoExpandColumn("rangeString");
		timeGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		timeGrid.getSelectionModel().addListener(Events.SelectionChange,
				new Listener<SelectionChangedEvent<TimeRangeModel>>() {
					public void handleEvent(
							SelectionChangedEvent<TimeRangeModel> e) {

						// Filter the list
						TimeRangeModel selection = e.getSelectedItem();
						if (selection != null) {
							Date showDate = datePicker.getValue();

							String dateStr = dateFormat.format(showDate);
							store.removeAll();
							Set<BeanModel> snapShotSet = dateToSnapshotSet.get(dateStr);
							if(snapShotSet != null) {
								DateWrapper selectedDate = new DateWrapper(showDate);
								selectedDate = selectedDate.clearTime();
								DateWrapper startTime = new DateWrapper(selection.getStartDate());
								DateWrapper tempStart = selectedDate.addHours(
										startTime.getHours()).addMinutes(
										startTime.getMinutes()).addSeconds(
										startTime.getSeconds());
								
								DateWrapper endTime = new DateWrapper(selection
										.getEndDate());

								DateWrapper tempEnd = selectedDate.addHours(
										endTime.getHours()).addMinutes(
										endTime.getMinutes()).addSeconds(
										endTime.getSeconds());
								
								List<BeanModel> infoList = new ArrayList<BeanModel>();
								for (Iterator<BeanModel> iterator = snapShotSet.iterator(); iterator.hasNext();) {
									BeanModel snapShot = iterator.next();
									VMSnapshotsInfo bean = snapShot.getBean();
									Date date = Utils.localTimeToServerTime(new Date(bean.getTimestamp()), bean.getTimeZoneOffset());
									if(date.compareTo(tempStart.asDate()) >= 0 && date.compareTo(tempEnd.asDate()) <= 0)
										infoList.add(snapShot);
								}
								
								BeanModel[] result = infoList.toArray(new BeanModel[0]);
								Arrays.sort(result, new Comparator<BeanModel>(){
									@Override
									public int compare(BeanModel arg0, BeanModel arg1) {
										final VMSnapshotsInfo bean0 = (VMSnapshotsInfo)arg0.getBean();
										final VMSnapshotsInfo bean1 = (VMSnapshotsInfo)arg1.getBean();
										if (bean0.getTimestamp() > bean1.getTimestamp())
											return -1;
										else if (bean0.getTimestamp() < bean1.getTimestamp())
											return 1;
										else
											return 0;
									}
									
								});
								
								for (int i = 0; i < result.length; i++) {
									store.add(result[i]);
								}
								
								view.renderRowStyle();
							}
						}

					}
				});	
	}
	
	public void addSnapshotModel(BeanModel model) {
		VMSnapshotsInfo bean = model.getBean();
		Date date = new Date(bean.getTimestamp());
		TimeZone timeZone = null;
		
		if(bean.getTimeZoneOffset() > 0) {
			timeZone = TimeZone.createTimeZone((int)bean.getTimeZoneOffset() / (-60000));
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
		int range = hour / 3;
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

	protected Label getTitleLabel() {
		Label label = new Label();
		label.setHtml(UIContext.Constants.coldStandbySnapshotDatePicker());
		label.setStyleName("provisionSubTitle");
		return label;
	}

	protected void initCallbackFunction() {
		callback = new BaseAsyncCallback<VMSnapshotsInfo[]>(){

			@Override
			public void onFailure(Throwable caught) {
				prepareGetSnapshotsFailure();
			}

			@Override
			public void onSuccess(VMSnapshotsInfo[] result) {
				prepareGetSnapshotsSuccess(result);
			}
		};
	}
	
	protected void prepareGetSnapshotsFailure() {
		grid.getView().setEmptyText(UIContext.Constants.NA());
		store = new ListStore<BeanModel>();
		grid.reconfigure(store, columnModel);
	}
	
	protected void prepareGetSnapshotsSuccess(VMSnapshotsInfo[] result) {
		grid.getView().setEmptyText(UIContext.Constants.NA());
		
		if (result == null || result.length == 0){
			grid.getView().setEmptyText(UIContext.Constants.NA());
			store = new ListStore<BeanModel>();
			grid.reconfigure(store, columnModel);
			
			datePicker.clearAll();
			
			return;
		}
		
		dateToSnapshotSet.clear();
		for (final VMSnapshotsInfo snapShot : result){
			Date date = Utils.localTimeToServerTime(new Date(snapShot.getTimestamp()));
		    String dateStr = dateFormat.format(date);
			Set<BeanModel> set = dateToSnapshotSet.get(dateStr);
			if(set == null) {
				set = new HashSet<BeanModel>();
				dateToSnapshotSet.put(dateStr, set);
				datePicker.addSelectedDate(date, true);
				
				if(latestProvisionPointsDate == null || latestProvisionPointsDate.before(date))
					latestProvisionPointsDate = date;
			}
			
			set.add(factory.createModel(snapShot));
		}
		
		datePicker.highlightSelectedDates();
		
		showSnapShotsOfHighlightedDayOrLatest();
	}
	

	protected void initSnapshotGrid() {
		GridCellRenderer<BeanModel> selectRenderer = new GridCellRenderer<BeanModel>() {
			@Override
			public Object render(BeanModel model, String property,	ColumnData config, int rowIndex, int colIndex,
					ListStore<BeanModel> store, Grid<BeanModel> grid) {
				
				Radio selectSnapshot = getSelectSnapshotRadio(model);

				return selectSnapshot;
			}
		};
		
		GridCellRenderer<BeanModel> timeRenderer = new GridCellRenderer<BeanModel>() {

			@Override
			public Object render(BeanModel model, String property,	ColumnData config, int rowIndex, int colIndex,
					ListStore<BeanModel> store, Grid<BeanModel> grid) {
				Label label = new Label();
				model.set(timeRendererControl, label);
				final VMSnapshotsInfo snapShot = (VMSnapshotsInfo)model.getBean();
				
				String formatDateToServerTime = UIContext.Constants.NA();
				if(snapShot.getTimestamp() > 0)
					formatDateToServerTime = Utils.formatDateToServerTime(new Date(snapShot.getTimestamp()), 
								snapShot.getTimeZoneOffset());
				label.setHtml(formatDateToServerTime);
				
				if(currentHighligthedUUID != null && currentHighligthedUUID != NULL_STRING
						&& (currentHighligthedUUID.equals(snapShot.getSnapGuid()) || currentHighligthedUUID.equals(snapShot.getBootableSnapGuid()))) {
					higlightLabel(label, true);
					currentHighligthedModel = model;
				}
				return label;
			}
		};
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig(ProvisionColumn, "", 35, selectRenderer));
		configs.add(Utils.createColumnConfig(TimeColumn, UIContext.Constants.provistionPointColumnTime(), 295, timeRenderer));
		
		store = new ListStore<BeanModel>();  
		columnModel = new ColumnModel(configs);
		
	    grid = new Grid<BeanModel>(store, columnModel);
	    grid.setHeight(gridHeight);
	    grid.setWidth(340);
	    grid.setLoadMask(true);
//	    grid.setAutoExpandColumn(TimeColumn);
		grid.setTrackMouseOver(false);
		view = new RowGridView();
		grid.setView(view);
		grid.getView().setEmptyText(UIContext.Constants.NA());
	}

	protected void initDatePicker() {
		datePicker = new HighlightedDatePicker();
		datePicker.addListener(Events.Select, new Listener<ComponentEvent>() {

			public void handleEvent(ComponentEvent be) {
				
				Date newDate = datePicker.getValue();
				showSnapShotsOfTheDay(newDate);
			}
		});
	}
	
	protected void showSnapShotsOfHighlightedDayOrLatest() {
		if(latestProvisionPointsDate == null || currentHighligthedUUID == null)
			return;
		
		Date showDate = latestProvisionPointsDate;
		if(currentHighligthedUUID != NULL_STRING) {
			for(String date : dateToSnapshotSet.keySet()) {
				Set<BeanModel> models = dateToSnapshotSet.get(date);
				for(BeanModel model : models) {
					VMSnapshotsInfo info = model.getBean();
					if(info.getSnapGuid().equals(currentHighligthedUUID)
							|| info.getBootableSnapGuid().equals(currentHighligthedUUID) ) {
						showDate = Utils.localTimeToServerTime(new Date(info.getTimestamp()));
						break;
					}
				}
				
				if(showDate != latestProvisionPointsDate)
					break;
			}
		}
		
		datePicker.setValue(showDate, true);
		
		showSnapShotsOfTheDay(showDate);
		datePicker.highlightSelectedDates();
	}

	protected void showSnapShotsOfTheDay(Date showDate) {
		String dateStr = dateFormat.format(showDate);
		store.removeAll();
		Set<BeanModel> snapShotSet = dateToSnapshotSet.get(dateStr);
		if(snapShotSet != null) {
			List<BeanModel> infoList = new ArrayList<BeanModel>();
			for (Iterator<BeanModel> iterator = snapShotSet.iterator(); iterator.hasNext();) {
				BeanModel snapShot = iterator.next();
				infoList.add(snapShot);
			}
			
			BeanModel[] result = infoList.toArray(new BeanModel[0]);
			
			resetTimeRange();
			for (int i = 0; i < result.length; i++) {
				addSnapshotModel(result[i]);
//				store.add(result[i]);
			}
			refreshTimeRange();
			
//			view.renderRowStyle();
		}
		else {
			resetTimeRange();
			refreshTimeRange();
		}
	}
	
	protected Radio getSelectSnapshotRadio(BeanModel model) {
		final BeanModel beanModel = model;
		Radio selectRadio = new Radio();
		
		selectRadio = new Radio();
		selectRadio.setValue(false);
		selectRadio.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				if(be.getValue() instanceof Boolean && (Boolean)be.getValue()) {
					selectedSnapshotInfo = beanModel.getBean();
					provWindow.provisionButton.enable();
				}
				
			}
		});
		rg.add(selectRadio);
			
		VMSnapshotsInfo bean = beanModel.getBean(); 
		if(selectedSnapshotInfo != null && bean.getTimestamp() == selectedSnapshotInfo.getTimestamp()) {
			selectRadio.setValue(true);
		}
		
		return selectRadio;
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
//		timer.scheduleRepeating(REFRESH_INTERVAL);
	}
	
	protected void provisionSelectedPoint(){
		if(selectedSnapshotInfo == null) {
			MessageBox.info(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.coldStandbySnapshotSelectOne(), null);
			provWindow.unmask();
			return; 
		}
		final PowerOnVMWarningDialog warningDialog = new PowerOnVMWarningDialog();
		warningDialog.setMessage(getWarningMessage());
		warningDialog.setUsingIPConfigurationEnable(usingTCPIPSetting);
		warningDialog.addListener(Events.BeforeHide, new Listener<WindowEvent>() {

			@Override
			public void handleEvent(WindowEvent be) {
				if(warningDialog.isOKButtonClick()){
					selectedSnapshotInfo.setPowerOnWithIPSettings(warningDialog.getUsingIPConfigurationEnable());
					startVMNow();
				} else {					
					provWindow.unmask();
				}
			}
			
			protected void startVMNow() {
				failoverCallback = new BaseAsyncCallback<Void>(){
					
					@Override
					public void onFailure(Throwable caught) {
						MessageBox msg = new MessageBox();
						msg.setIcon(MessageBox.ERROR);
						msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameVCM));
						msg.setMessage(UIContext.Constants.coldStandbySnapshotStartFailoverFailed());
						msg.setModal(true);
						Utils.setMessageBoxDebugId(msg);
						msg.show();
						provWindow.unmask();
					}
					
					@Override
					public void onSuccess(Void result) {
						afterStartFailoverSuccessful();
					}
					
				};
				
				startFailover();
			}
			
		});
		warningDialog.show();
	}
	
	protected String getWarningMessage() {
		String msgStr = UIContext.Constants.coldStandbySnapshotWhetherToStart();
		if(currentHighligthedModel != null) {
			if( ((VMSnapshotsInfo)currentHighligthedModel.getBean()).getTimestamp() != selectedSnapshotInfo.getTimestamp()) { 
				String onSnapshotBackupedTime =  Utils.formatDateToServerTime(new Date(((VMSnapshotsInfo)currentHighligthedModel.getBean()).getTimestamp()),
						((VMSnapshotsInfo)currentHighligthedModel.getBean()).getTimeZoneOffset());
				String toPowerOn = Utils.formatDateToServerTime(new Date(selectedSnapshotInfo.getTimestamp()),
							selectedSnapshotInfo.getTimeZoneOffset());
				msgStr = UIContext.Messages.virtualConvesionSnapshotPowerOnAnother(onSnapshotBackupedTime, toPowerOn);//"Do you want to power off the snapshot backuped on {0} and then power on the one backuped on {1}?";
			}
			else {
				msgStr = UIContext.Constants.coldStandbySnapshotRevertToSnapshot();
			}
		}
		else if(!ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()
				|| (ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerAccessible() 
						&& ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerPhysical())) {
			msgStr =  UIContext.Constants.coldStandbySnapshotProductionServerRunningQuery();
		}
		else if(ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerAccessible() 
						&& !ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerPhysical()) {
			
			msgStr =  UIContext.Constants.coldStandbySnapshotRevertToSnapshot();
		}
		return msgStr;
	}
	
	protected void afterStartFailoverSuccessful() {
		provWindow.hide();
		refreshHighLightedRunningSnapshot();
	}
	
	protected void refreshHighLightedRunningSnapshot() {
		ColdStandbyManager.getInstance().getHomepage().getProvisionPanel().refreshHighLightedRunningSnapshot();
	}
	
	protected void startFailover() {
		final ARCFlashNode currentNode = ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode();
		if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
			String uuid = currentNode.getUuid();
		
			monitorService.startFailover(uuid, selectedSnapshotInfo, failoverCallback);
		}
		else {
			String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
			service.startFailover(vmInstanceUUID, selectedSnapshotInfo, failoverCallback);
		}
	}
	
	protected void setButtonId(MessageBox messageBox, String buttonId, String id) {
		Button button = messageBox.getDialog().getButtonById(buttonId);
		if(button != null) {
			button.ensureDebugId(id);
		}
	}
	
	public void update() {
		
		grid.getView().setEmptyText(UIContext.Constants.loadingIndicatorText());
		store = new ListStore<BeanModel>();
		grid.reconfigure(store, columnModel);
		getSnapshots();
		
		highlightCurrentSnapShot();
	}
	
	protected void getSnapshots() {
		if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
			monitorService.getSnapshots(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().getUuid(), callback);
		}
		else {
			String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
			service.getSnapshots(vmInstanceUUID, callback);
		}
	}
	
	@BEAN(com.ca.arcflash.ha.model.VMSnapshotsInfo.class)
	public interface VMSnapshotsInfoModel extends BeanModelMarker {
		
	}
	
	public class VMSnapshotsInfoTag implements BeanModelTag, Serializable {

		private static final long serialVersionUID = 5265279894144483588L;
		
	}
	
	protected void highlightCurrentSnapShot() {
		
		 currentSnapShotcallback = new BaseAsyncCallback<String>(){

				@Override
				public void onFailure(Throwable caught) {
					
					clearOldRunning();
					showSnapshotWhenGetHighlight(null);
					setCurrentHighlightUUID(null);
					
				}

				@Override
				public void onSuccess(String result) {
					
					clearOldRunning();
					showSnapshotWhenGetHighlight(result);
					setCurrentHighlightUUID(result);
					
					
					if(result == null) {
						return;
					}
					
					int size = store.getCount();
					for (int i = 0; i < size; i++) {
						BeanModel model = store.getAt(i);
						VMSnapshotsInfo info = model.getBean();
						if(result.equals(info.getSnapGuid())
								|| result.equals(info.getBootableSnapGuid())){
							
							currentHighligthedModel = model;
							Label label = (Label)model.get(timeRendererControl);
							higlightLabel(label, true);
						}
						
					}
				}

				private void showSnapshotWhenGetHighlight(String result) {
					// the currentHighligthedUUID == null can not be removed, because showSnapShotsOfHighlightedDayOrLatest 
					// can be called only once.
					if(currentHighligthedUUID == null) {
						setCurrentHighlightUUID(result);
						
						showSnapShotsOfHighlightedDayOrLatest();
					}
					
				}

				private void setCurrentHighlightUUID(String result) {
					if(result == null || result.length() == 0) {
						currentHighligthedUUID = NULL_STRING;
						provWindow.shutdownButton.disable();
					}
					else {
						currentHighligthedUUID = result;
						provWindow.shutdownButton.enable();
					}
				}

				private void clearOldRunning() {
					
					if(currentHighligthedModel != null) {
						Label label = (Label)currentHighligthedModel.get(timeRendererControl);
						higlightLabel(label, false);
						
						currentHighligthedModel = null;
					}
				}
		 };
				
		 getCurrentRunningSnapShot();
		
	}
	
	protected void getCurrentRunningSnapShot() {
		if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
			monitorService.getCurrentRunningSnapShotGuid(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().getUuid(), currentSnapShotcallback);
		}
		else {
			String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
			service.getRunningSnapShotGuidForProduction(vmInstanceUUID, currentSnapShotcallback);
		}
	}
	
	@Override
	protected void onUnload() {
		  super.onUnload();
//			if (timer != null) {
//				timer.cancel();
//				timer = null;
//			}
	  }

	protected void higlightLabel(Label label, boolean highlight) {
		if(highlight)
			label.setStyleAttribute("font-weight", "bold");
		else
			label.setStyleAttribute("font-weight", "normal");
	}
	
	class RowGridView extends GridView {

		public void renderRowStyle() {
			if(store == null)
				return;
			
			int count = store.getCount();
			if(count > 1)
				for(int i = 1; i < count; i += 2) {
					com.google.gwt.dom.client.Element elem = getRow(i);
					addRowStyle(elem, "table_row_alternative");
				}
		}
		
	}
}
