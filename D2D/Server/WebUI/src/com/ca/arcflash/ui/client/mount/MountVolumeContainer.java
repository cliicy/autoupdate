package com.ca.arcflash.ui.client.mount;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.EnhancedHightlightedDataPicker;
import com.ca.arcflash.ui.client.common.FormatUtil;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupStatusModel;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.ca.arcflash.ui.client.model.MountedRecoveryPointItemModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.TimeRangeModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.restore.IRestoreSourceListener;
import com.ca.arcflash.ui.client.restore.RestoreConstants;
import com.ca.arcflash.ui.client.restore.RestoreSourcePanel;
import com.ca.arcflash.ui.client.vsphere.vmrecover.VMRestoreSourcePanel;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MountVolumeContainer extends LayoutContainer implements IRestoreSourceListener{

	private final LoginServiceAsync service = GWT.create(LoginService.class);
	private final CommonServiceAsync commonService = GWT.create(CommonService.class);

	private  EnhancedHightlightedDataPicker picker;
	//private PathSelectionPanel pathSelection;
	private boolean isDefaultSourceInitialized = false; // if the default restore source path is initialized. If not, no need to refresh the calendars. 
	public String strDestination;
	public String strDestinationUserName;
	public String strDestinationPassword;
	
	private ListStore<RecoveryPointModel> recoveryPointStore;
	private Grid<RecoveryPointModel> recoveryPointGrid;
	private ListStore<MountedRecoveryPointItemModel> mountItemStore;
	
	private Grid<MountedRecoveryPointItemModel> mountItemGrid;
	private Grid<TimeRangeModel> timeGrid;
	private TimeRangeModel currentSelectModel = null;
	private Date previousSelectedMonth = null;
	private int timeRangeCount[] = new int[4];
	private MountVolumeContainer thisPanel;
	private MountWindow parentPanel;
	
	// 2015-04-08 fix issue 214818 caused by DST problem
//	private String currentRangeStartTime;
//	private String currentRangeEndTime;
	private D2DTimeModel currentRangeStartTime;
	private D2DTimeModel currentRangeEndTime;
	
	
	private RestoreSourcePanel sourcePanel;
	
	public MountVolumeContainer(MountWindow parentPanel){
		thisPanel = this;
		this.parentPanel = parentPanel;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);	
		LayoutContainer mainContainer = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setWidth("100%");
		mainContainer.setLayout(new RowLayout());
		
		mainContainer.add(renderSourceSection());
		
		mainContainer.add(getLabelText());

		mainContainer.add(renderMainSection());
		
		//mainContainer.setStyleAttribute("padding-left", "5px");
		
		this.add(mainContainer);
		
	//	getDefaultSourceValue();
	}
	
	private LabelField getLabelText(){
		LabelField label = new LabelField();
		label.addStyleName("mount_recovery_point");
		label.setValue(UIContext.Constants.restoreFilesToRestore());
		return label;
	}
	
//	private LayoutContainer renderSourceSection() {
//		LayoutContainer container = new LayoutContainer();
//
//		TableLayout tl = new TableLayout();
//		tl.setWidth("100%");
//		tl.setColumns(2);
//		tl.setCellPadding(0);
//		tl.setCellSpacing(0);
//		container.setLayout(tl);
//	
//		TableData td = new TableData();
//		td.setWidth("167px");
//		LabelField label = new LabelField();
//		label.addStyleName("restoreWizardSubItemDescription");
//		label.setText(UIContext.Constants.restoreBackupLocation());
//		container.add(label,td);
//
//		pathSelection = new PathSelectionPanel(false, null, false);
//		pathSelection.setWidth(482);
//		pathSelection.setMode(PathSelectionPanel.MOUNT_VOLUME_MODEL);
//		pathSelection.setPathFieldLength(350);
//		pathSelection.setChangeListener(new Listener<BaseEvent>() {
//			@Override
//			public void handleEvent(BaseEvent be) {
//				thisPanel.picker.clearAll();
//				//If invoke by pathSelection, there is no need to pop error Dialog. Added by wanqi06
//				highlightMostRecentMonthRPDates(true);		
//			}
//		});
//		pathSelection.addListener(PathSelectionPanel.onDisconnectionEvent,
//				new Listener<BaseEvent>() {
//
//					@Override
//					public void handleEvent(BaseEvent be) {
//						clearRecoveryPoints();
//					}
//
//				});
//		pathSelection.addDebugId("A1A7D243-5969-4786-AA43-47BBD7124D37", 
//				"F2B3939D-C720-4d5a-801A-676BF852C001", 
//				"1AD583D4-78ED-4979-AE0A-4BC3D5F80490");
//		container.add(pathSelection);
//
//		//getDefaultSourceValue();
//
//		return container;
//	}
	
	private void noRecoveryPointsFound(){
		MessageBox box = new MessageBox();
		box.setTitleHtml(Utils.getProductName());
		box.setIcon(MessageBox.INFO);
		box.setButtons(MessageBox.OK);
		box.setMessage(UIContext.Constants.noRecoveryPointsFound());
		box.show();
	}
	
	private LayoutContainer renderSourceSection() {
		if(UIContext.uiType == Utils.UI_TYPE_VSPHERE)
			sourcePanel = new VMRestoreSourcePanel();
		else
			sourcePanel = new RestoreSourcePanel();
			sourcePanel.setMode(PathSelectionPanel.MOUNT_VOLUME_MODEL);
		
		Listener<BaseEvent> changeListener = new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				thisPanel.picker.clearAll();				
				highlightMostRecentMonthRPDates(false);	
			}
		}; 
		sourcePanel.setPathChangeListener(changeListener);
//		sourcePanel.addD2DSelectionChangeListener(changeListener);
		/*sourcePanel.addPathListener(PathSelectionPanel.onDisconnectionEvent,
				new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						clearRecoveryPoints();
					}

		});*/
		sourcePanel.setSourceListener(thisPanel);
		return sourcePanel;
	}
	
	private LayoutContainer renderMainSection(){
		LayoutContainer container = new LayoutContainer();
		container.setHeight(320);
		RowLayout tableLayout = new RowLayout(Orientation.HORIZONTAL);
//		tableLayout.setWidth("100%");
//		tableLayout.setCellPadding(0);
//		tableLayout.setCellSpacing(0);
		container.setLayout(tableLayout);
		RowData rd = new RowData(-1, 320);
//		td.setHorizontalAlign(HorizontalAlignment.LEFT);
//		td.setVerticalAlign(VerticalAlignment.TOP);
		container.add(renderMainLeftSection(), rd);
		
		RowData td = new RowData(-1,-1);
//		td.setHorizontalAlign(HorizontalAlignment.LEFT);
//		td.setVerticalAlign(VerticalAlignment.TOP);
		container.add(renderMainRightSection(), td);
		
		return container;
	}
	
	private LayoutContainer renderMainLeftSection() {
		LayoutContainer container = new LayoutContainer();
		container.setWidth(185);
		container.setLayout(new RowLayout());
		container.setStyleAttribute("padding-right", "4px");
		RowData rd = new RowData(-1,-1);
		container.add(getMainLeftRow1(),rd);
		container.add(getMainLeftRow2(),rd);
		return container;
	}
	
	private Widget getMainLeftRow1(){
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
		
		picker.addListener(Events.Select, new Listener<ComponentEvent>() {

			public void handleEvent(ComponentEvent be) {
				
				// 2011-03-24 fix Issue: 20121544 (19819211)    Title: NO DISPLAY OF FILES IN RESTORE
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
		return picker;
	}
	
	private Grid<TimeRangeModel> getMainLeftRow2(){
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setId("rangeString");
		column.setHeaderHtml(UIContext.Constants.restoreTimeRangeColumn());
		column.setWidth(170);
		
		column.setRenderer(new GridCellRenderer<TimeRangeModel>(){

			@Override
			public Object render(TimeRangeModel trModel, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<TimeRangeModel> store, Grid<TimeRangeModel> grid) {
				
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

		ColumnModel columnModel = new ColumnModel(configs);
		ListStore<TimeRangeModel> rangeStore = new ListStore<TimeRangeModel>();
		rangeStore.setStoreSorter(new StoreSorter<TimeRangeModel>(){

			@Override
			public int compare(Store<TimeRangeModel> store, TimeRangeModel m1, TimeRangeModel m2, String property) {
				/*if (m1 == null || m1.getStartDate() == null)
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
		//timeGrid.setHeight(115); //210
		//timeGrid.setWidth(177);
		timeGrid.setHeight(120);
		timeGrid.setWidth(178);
		timeGrid.setBorders(false);
		timeGrid.getView().setAutoFill(true);
		//timeGrid.setBorders(true);
		//timeGrid.setAutoExpandColumn("rangeString");
		timeGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		timeGrid.getSelectionModel().addListener(Events.SelectionChange,
				new Listener<SelectionChangedEvent<TimeRangeModel>>() {
					public void handleEvent(
							SelectionChangedEvent<TimeRangeModel> e) {

						// Filter the list
						TimeRangeModel selection = e.getSelectedItem();
						currentSelectModel = selection;
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
//											currentRangeStartTime = strBegin;
//											currentRangeEndTime = strEnd;
//											thisPanel.refreshRecoveryPointFromTimeRangeByServerTime(strBegin, strEnd);
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

		return timeGrid;
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
//			// filter the other grid by this
//			thisPanel
//					.refreshRecoveryPointFromTimeRangeByServerTime(
//							strBegin, strEnd);
//			currentRangeEndTime = strEnd;
//		}
//	}
	
	private LayoutContainer renderMainRightSection() {
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new BorderLayout());
		container.setSize(490, 316);
		
		BorderLayoutData bldN = new BorderLayoutData(LayoutRegion.NORTH, 197, 60, 265);
		bldN.setMargins(new Margins(0, 0, 4, 0));	
		container.add(getMainRightSectionRow1(),bldN);
		
		BorderLayoutData bldC = new BorderLayoutData(LayoutRegion.CENTER, 116);
		container.add(getMainRightSectionRow2(), bldC);
		
		return container;
	}

	private Grid<RecoveryPointModel> getMainRightSectionRow1(){
		
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
		column.setWidth(80);
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
		column.setWidth(100);
		column.setRenderer(new GridCellRenderer<RecoveryPointModel>() {
			public String render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				String type = UIContext.Constants.backupTypeUnknown();
				switch (model.getBackupType()) {
				case BackupTypeModel.Full:
					type = UIContext.Constants.backupTypeFullText();
					break;
				case BackupTypeModel.Incremental:
					type = UIContext.Constants.backupTypeIncrementalText();
					break;
				case BackupTypeModel.Resync:
					type = UIContext.Constants.backupTypeResyncText();
					break;
				}
				return type;
			}
		});
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("Name");
		column.setHeaderHtml(UIContext.Constants.restoreNameColumn());
		column.setRenderer(new GridCellRenderer<RecoveryPointModel>() {
			public String render(RecoveryPointModel model, String property,
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
		column.setWidth(130);
		configs.add(column);		
		
		/*
		column = new ColumnConfig();
		column.setId("CatalogStatus");
		column.setHeader(UIContext.Constants.restoreCatalogStatusColumn());
		column.setMenuDisabled(true);
		column.setWidth(80);
		column.setRenderer(new GridCellRenderer<RecoveryPointModel> () {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				if(UIContext.uiType == 1 && UIContext.backupVM !=null){
					return RestoreConstants.getFSCatalogStatusMsgForVSphere(model.getFSCatalogStatus());
				}
				return RestoreConstants.getFSCatalogStatusMsg(model.getFSCatalogStatus());
			}
			
		});
		configs.add(column);*/

		ColumnModel columnModel = new ColumnModel(configs);

		recoveryPointStore = new ListStore<RecoveryPointModel>();

		recoveryPointGrid = new Grid<RecoveryPointModel>(recoveryPointStore, columnModel);
		recoveryPointGrid.setAutoExpandColumn("Name");
		recoveryPointGrid.setAutoExpandMax(200);
		recoveryPointGrid.setBorders(true);
		recoveryPointGrid.setStripeRows(true);
		recoveryPointGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		//recoveryPointGrid.setSize(480, 197);
		recoveryPointGrid.getSelectionModel().addListener(Events.SelectionChange,
				new Listener<SelectionChangedEvent<RecoveryPointModel>>() {
					public void handleEvent(
							SelectionChangedEvent<RecoveryPointModel> e) {
						getMountedRecoveryPointItem();
					}
				});
		
		return recoveryPointGrid;
	}
	
	public void getMountedRecoveryPointItem(){

		final RecoveryPointModel sel = getSelectedRecoveryPointModel();
		if (sel != null) {
			String dest = getSessionPath();
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
			String sessionGuid = sel.getSessionGuid();
			
			mountItemGrid.unmask();
			service.getMountedRecoveryPointItems(
							dest,
							domain,
							user,
							pwd,
							subPath,sessionGuid,
							new BaseAsyncCallback<List<MountedRecoveryPointItemModel>>() {
								@Override
								public void onFailure(
										Throwable caught) {
									super.onFailure(caught);
									recoveryPointStore.removeAll();
									refreshRecoveryPointFromTimeRangeByServerTime(currentRangeStartTime, currentRangeEndTime);
									parentPanel.updateRefreshStatus(WindowsMaskAdapter.MOUNT_VOLUME_CONTAINER, true);
								}

								@Override
								public void onSuccess(
										List<MountedRecoveryPointItemModel> result) {
									clearMountVolumeItems();
									mountItemStore.add(result);
									if (result.size()==0)
										mountItemGrid.mask(UIContext.Constants.recoverVMNoVolumeInfo());
									parentPanel.updateRefreshStatus(WindowsMaskAdapter.MOUNT_VOLUME_CONTAINER, true);
								}
							});

		}
		else{
			parentPanel.updateRefreshStatus(WindowsMaskAdapter.MOUNT_VOLUME_CONTAINER, true);
		}

	
	}
	private Grid<MountedRecoveryPointItemModel> getMainRightSectionRow2(){
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();
		column.setId("volumePath");
		column.setHeaderHtml(UIContext.Constants.mountVolLabel());
		column.setMenuDisabled(true);
		column.setWidth(130);
		configs.add(column);		
		
		column = new ColumnConfig();
		column.setId("size");
		column.setHeaderHtml(UIContext.Constants.restoreSizeColumn());
		column.setMenuDisabled(true);
		column.setWidth(120);
		column.setRenderer(new GridCellRenderer<MountedRecoveryPointItemModel> () {

			@Override
			public Object render(MountedRecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MountedRecoveryPointItemModel> store,
					Grid<MountedRecoveryPointItemModel> grid) {
				try {
					Long value = model.getVolumeSize();
					String formattedValue = Utils.bytes2String(value);
					return formattedValue;

				} catch (Exception e) {

				}
				return "";
			}
			
		});
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("mounthPath");
		column.setHeaderHtml(UIContext.Constants.mountVolText());
		column.setMenuDisabled(true);
		column.setWidth(150);
		column.setRenderer(new GridCellRenderer<MountedRecoveryPointItemModel> () {
			@Override
			public Object render(MountedRecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MountedRecoveryPointItemModel> store,
					Grid<MountedRecoveryPointItemModel> grid) {
				if(MountUtils.isVolumeMounted(model.getMountFlag())){
					Label label = new Label();
					label.setText(UIContext.Messages.mountVolText(model.getMountPath()));
					return label;
				}
				else{
					LayoutContainer mountContainer = new LayoutContainer();
					mountContainer.setLayout(new TableLayout(1));
					
					final MountedRecoveryPointItemModel itemModel = model;
					Label label = new Label(UIContext.Constants.mountVolText());
					label.addStyleName("homepage_header_hyperlink_label");
					label.addStyleName("mount_volume_label_icon");
					//label.setTitle(UIContext.Constants.mountVolTextToolTip());
					label.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							MountSubSessionWindow window = new MountSubSessionWindow(thisPanel, itemModel);
							window.setModal(true);
							window.show();
						}
					});
					mountContainer.add(label);
					return mountContainer;
				}
			}
		});
		configs.add(column);
		
		StoreSorter<MountedRecoveryPointItemModel> sorter = new StoreSorter<MountedRecoveryPointItemModel>(){
			public int compare(Store<MountedRecoveryPointItemModel> store, MountedRecoveryPointItemModel m1, MountedRecoveryPointItemModel m2, String property) {
				if( property == null ){
					return m1.getVolumePath().compareToIgnoreCase(m2.getVolumePath());	
				}
				else if( property == "size" ){
					if(m1.getVolumeSize() < m2.getVolumeSize()){
						return -1;
					}
					else if(m1.getVolumeSize() == m2.getVolumeSize()){
						return 0;
					}
					else{
						return 1;
					}
				}
				else
					return super.compare(store, m1, m2, property);
			}
		};

		ColumnModel columnModel = new ColumnModel(configs);
		mountItemStore = new ListStore<MountedRecoveryPointItemModel>();
		mountItemStore.setStoreSorter(sorter);
		mountItemGrid = new Grid<MountedRecoveryPointItemModel>(mountItemStore, columnModel);
		mountItemGrid.setAutoExpandColumn("volumePath");
		mountItemGrid.setAutoExpandMax(5000);
		mountItemGrid.setBorders(true);
		mountItemGrid.setStripeRows(true);
		mountItemGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		//mountItemGrid.setSize(480, 115);
		//mountItemGrid.addStyleName("mount_volume_grid");
		return mountItemGrid;
	}
	
	public MountWindow getMountWindow(){
		return parentPanel;
	}
	
//	public String getUserName() {
//		return pathSelection.getUsername();
//	}
//
//	public String getPassword() {
//		return pathSelection.getPassword();
//	}
	
	public String getUserName() {
		return sourcePanel.getDestUserName();
	}

	public String getPassword() {
		return sourcePanel.getDestPassword();
	}
	
	public String getDestination(){
		return getSessionPath();
	}
	
	public String getRpsHostName(){
		RpsHostModel rpsHost = sourcePanel.getRpsHost();
		if( rpsHost!=null )
			return rpsHost.getHostName();
		else
			return null;		
	}
	
	public String getDatastoreDisplayName(){
		return sourcePanel.getDatastoreDisplayName();		
	}
	
	public RecoveryPointModel getSelectedRecoveryPointModel(){
		return recoveryPointGrid.getSelectionModel().getSelectedItem();
	}
	/*public MountedRecoveryPointItemModel getMountedRecoveryPointItemModel(){
		return mountItemGrid.getSelectionModel().getSelectedItem();
	}*/
	
	private Date RecPointModel2ServerDate(RecoveryPointModel rpmodel) {
		long timeDiffLocalAndServer = rpmodel.getTime().getTimezoneOffset()
				* 60 * 1000 + rpmodel.getTimeZoneOffset();
		Date serverDate = new Date(rpmodel.getTime().getTime()
				+ timeDiffLocalAndServer);
		return serverDate;
	}
	public Date formateToMonth(Date newDate) {
		DateWrapper wrapper = new DateWrapper(newDate);
		wrapper = new DateWrapper(wrapper.getFullYear(), wrapper.getMonth(), 1);
		return wrapper.asDate();
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
//				pwd, beginDate, endDate, true,
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
//								if(result[i].listOfRecoveryPointItems == null || result[i].listOfRecoveryPointItems.size() == 0){
//									result[i].setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
//								}
//								GWT.log(result[i].getName(), null);
//								//if no root item set catalog status to not created.
//								/*if(result[i].listOfRecoveryPointItems == null || result[i].listOfRecoveryPointItems.size()==0){
//									result[i].setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
//								}*/
//								newstore.add(result[i]);
//							}
//							thisPanel.recoveryPointStore.add(newstore);
//							//thisPanel.recoveryPointGrid.getView().refresh(false);
//							//fireRecoveryPointsChanged(newstore.size());
//							//make the count in the time range is the same as that of main grid
//							if(currentSelectModel != null && currentSelectModel.getCount() != result.length) {
//								currentSelectModel.setCount(result.length);
//								timeGrid.getView().refresh(false);
//							}
//							if (result.length > 0) {
//								thisPanel.recoveryPointGrid.getSelectionModel().select(0,false);
//								thisPanel.recoveryPointGrid.getView().scrollToTop();
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
				pwd, beginDate, endDate, true,
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
								if(result[i].listOfRecoveryPointItems == null || result[i].listOfRecoveryPointItems.size() == 0){
									result[i].setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
								}
								GWT.log(result[i].getName(), null);
								//if no root item set catalog status to not created.
								/*if(result[i].listOfRecoveryPointItems == null || result[i].listOfRecoveryPointItems.size()==0){
									result[i].setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
								}*/
								newstore.add(result[i]);
							}
							thisPanel.recoveryPointStore.add(newstore);
							//thisPanel.recoveryPointGrid.getView().refresh(false);
							//fireRecoveryPointsChanged(newstore.size());
							//make the count in the time range is the same as that of main grid
							if(currentSelectModel != null && currentSelectModel.getCount() != result.length) {
								currentSelectModel.setCount(result.length);
								timeGrid.getView().refresh(false);
							}
							if (result.length > 0) {
								thisPanel.recoveryPointGrid.getSelectionModel().select(0,false);
								thisPanel.recoveryPointGrid.getView().scrollToTop();
							}
						}
						timeGrid.unmask();
					}

				});
	}
	
	
	private String getSessionPath() {
		return sourcePanel.getBackupDestination();		
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
						//Decide whether pop error dialog, added by wanqi06
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
							noRecoveryPointsFound();
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
	private void highlightDatesIfMonthChange(Date newDate,  boolean useLastDayInMonth ) {
		newDate = formateToMonth(newDate);
		if(previousSelectedMonth == null || newDate.getTime() != previousSelectedMonth.getTime())
		{
			previousSelectedMonth = newDate;
			highlightDates(newDate, useLastDayInMonth);				    
		}
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
//				String asStartDate = this.GetServerDateString(startDate, false);// Utils.serverTimeToLocalTime(startDate.asDate());
//				String asEndDate = GetServerDateString(endDate, false);
				D2DTimeModel asStartDate = Utils.getD2DTime(startDate, false);
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
									RecoveryPointModel m = result[i];

									Date serverDate = RecPointModel2ServerDate(m);

									boolean isInCurrentMonth = serverDate.getMonth() == currentMonth;
									picker.addSelectedDate(serverDate, result[i].getBackupStatus() == BackupStatusModel.Finished, isInCurrentMonth);

									if(result[i].getBackupSetFlag() > 0) {
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
//				pwd, serverBeginDate, serverEndDate, true,
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
//								if(result[i].listOfRecoveryPointItems == null || result[i].listOfRecoveryPointItems.size() == 0){
//									result[i].setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
//								}
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
				pwd, serverBeginDate, serverEndDate, true,
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
								if(result[i].listOfRecoveryPointItems == null || result[i].listOfRecoveryPointItems.size() == 0){
									result[i].setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
								}
								addRecoveryPointModel(result[i]);
							}
							refreshTimeRange();
						}
						picker.setEnabled(true);
					}

				});
	}
	
	private void clearRecoveryPoints() {
		thisPanel.recoveryPointStore.removeAll();
		clearMountVolumeItems();
		//clearTree();
	}
	
	private void clearMountVolumeItems(){
		thisPanel.mountItemStore.removeAll();
	}
	
	public void addRecoveryPointModel(RecoveryPointModel model) {
		//Since in client side, the DST may start on the date, which means the computed server time is wrong
		//we need to compute the server time with correct client timezone offset
		// 2015-04-23 fix issue 219024 caused by DST problem
//		Date date = model.getTime();
//		TimeZone timeZone = TimeZone.createTimeZone(model.getTimeZoneOffset() / (-60000));
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
		if(lastRangeWithItems >= 0){
			currentSelectModel = timeGrid.getStore().getAt(lastRangeWithItems);
			timeGrid.getSelectionModel().select(lastRangeWithItems, false);
		}
	}

//	private void getDefaultSourceValue() {
//		// 1) Get the backup configuration and set the source accordingly
//		if(UIContext.uiType == 1){
//			service.getVMBackupConfiguration(UIContext.backupVM, new BaseAsyncCallback<VMBackupSettingModel>() {
//
//				@Override
//				public void onFailure(Throwable caught) {
//					// TODO Auto-generated method stub
//					
//					// the restore source is initialized
//					isDefaultSourceInitialized = true;
//					
//				}
//
//				@Override
//				public void onSuccess(VMBackupSettingModel result) {
//					// TODO Auto-generated method stub
//					if (result != null && result.getBackupVM().getDestination() != null) {
//						
//						
//						thisPanel.pathSelection.setDestination(result.getBackupVM().getDestination());
//						thisPanel.pathSelection.setUsername(result.getBackupVM().getDesUsername());
//						thisPanel.pathSelection.setPassword(result.getBackupVM().getDesPassword());
//						
//						// the restore source is initialized
//						isDefaultSourceInitialized = true;
//						
//						service.getServerTime(new BaseAsyncCallback<Date>() {
//							@Override
//							public void onFailure(Throwable caught) {
//								super.onFailure(caught);
//								refreshUI(new Date());
////								thisPanel.picker.setValue(getServerDate());
//							}
//
//							private void refreshUI(Date date) {
//								Date serverDate = Utils.localTimeToServerTime(date);
//								previousSelectedMonth = formateToMonth(serverDate);
//								highlightMostRecentMonthRPDates(false);
//							}
//
//							@Override
//							public void onSuccess(Date result) {
//								refreshUI(result);
////								//thisPanel.picker.setValue(getServerDate());
//							}
//						});
//					}
//					else
//					{
//						// the restore source is initialized
//						isDefaultSourceInitialized = true;
//					}
//				}
//				
//			});
//		}else{
//		service
//				.getBackupConfiguration(new AsyncCallback<BackupSettingsModel>() {
//
//					@Override
//					public void onFailure(Throwable caught) {
//						// Failed to get a proper value
//						
//						// the restore source is initialized
//						isDefaultSourceInitialized = true;
//					}
//
//					@Override
//					public void onSuccess(BackupSettingsModel result) {
//						if (result != null && result.getDestination() != null) {
//							
//							
//							thisPanel.pathSelection.setDestination(result.getDestination());
//							thisPanel.pathSelection.setUsername(result.getDestUserName());
//							thisPanel.pathSelection.setPassword(result.getDestPassword());
//							
//							// the restore source is initialized
//							isDefaultSourceInitialized = true;
//							
//							service.getServerTime(new BaseAsyncCallback<Date>() {
//								@Override
//								public void onFailure(Throwable caught) {
//									super.onFailure(caught);
//									refreshUI(new Date());
////									thisPanel.picker.setValue(getServerDate());
//								}
//
//								private void refreshUI(Date date) {
//									Date serverDate = Utils.localTimeToServerTime(date);
//									previousSelectedMonth = formateToMonth(serverDate);
//									highlightMostRecentMonthRPDates(false);
//								}
//
//								@Override
//								public void onSuccess(Date result) {
//									refreshUI(result);
////									//thisPanel.picker.setValue(getServerDate());
//								}
//							});
//						}
//						else
//						{
//							// the restore source is initialized
//							isDefaultSourceInitialized = true;
//						}
//					}
//				});
//		}
//	}
	
	private Date serverDate;
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
