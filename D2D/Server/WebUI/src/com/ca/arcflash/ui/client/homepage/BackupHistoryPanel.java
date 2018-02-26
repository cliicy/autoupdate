package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.EnhancedHightlightedDataPicker;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupStatusModel;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class BackupHistoryPanel extends ContentPanel implements IRefreshable{
	
	protected final HomepageServiceAsync service = GWT.create(HomepageService.class);
	protected final LoginServiceAsync loginService = GWT.create(LoginService.class);
	
	protected EnhancedHightlightedDataPicker datePicker;
	protected Date previousSelectedMonth = null;
	
	protected Grid<RecoveryPointModel> grid;
	protected ListStore<RecoveryPointModel> store;
	protected ColumnModel cm;
	protected GridCellRenderer<RecoveryPointModel> typeRenderer;
	protected GridCellRenderer<RecoveryPointModel> statusRenderer;
	protected GridCellRenderer<RecoveryPointModel> logicalSizeRenderer;
	protected GridCellRenderer<RecoveryPointModel> sizeRenderer;
	protected GridCellRenderer<RecoveryPointModel> timeRenderer;
	protected GridCellRenderer<RecoveryPointModel> nameRenderer;
	protected GridCellRenderer<RecoveryPointModel> archiveJobRenderer;
	protected GridCellRenderer<RecoveryPointModel> schedTypeRenderer;
	
	@Override
	protected void onRender(Element parent, int index)
	{
		UIContext.recentBackupPanel = this;		
		
		this.setCollapsible(true);
		this.setHeadingHtml(UIContext.Constants.homepageRecentBackupTableHeader());
		
		this.addListener(Events.Expand, new Listener<ComponentEvent>()
		{
			@Override
			public void handleEvent(ComponentEvent be)
			{
				if (be != null && be.getComponent() != null && be.getComponent() instanceof ContentPanel)
				{
					((ContentPanel)(be.getComponent())).layout(true);
				}
			}

		});
		
		super.onRender(parent, index);		

		// date picker
		datePicker = createDatePicker();
		
		// grid		
		store = new ListStore<RecoveryPointModel>();
		cm = createColumnModel();			
		grid = new Grid<RecoveryPointModel>(store, cm);		
		grid.setLoadMask(true);		
		grid.setAutoExpandColumn("Name");
		grid.setAutoExpandMax(3000);
		grid.setAutoExpandMin(75);	    
		grid.setStripeRows(true);
	
//		if (!withVCM)
//		{   
			if(GXT.isChrome)
				this.setHeight("232px");
			else
				this.setHeight("223px");
			this.setLayout(new RowLayout(Orientation.HORIZONTAL));
			
			// add date picker
			if(GXT.isChrome)
				this.add(datePicker, new RowData(-1, 0, new Margins(0,2,0,2))); 
			else
				this.add(datePicker, new RowData(-1, 0, new Margins(2))); 
			// add grid
			this.add(grid, new RowData(1, 1, new Margins(2))); 
//		}
//		else
//		{
//			this.setHeight(222);
//
//			TableLayout tl = new TableLayout(2);
//			tl.setWidth("100%");
//			this.setLayout(tl);
//			
//			TableData td = new TableData();
//			td.setWidth("10px");
//			this.add(datePicker, td);
//
//			// add grid
//			LayoutContainer lcGridWrapper = new LayoutContainer(new FitLayout());
//			lcGridWrapper.add(grid);
//			lcGridWrapper.setHeight(195);
//			this.add(lcGridWrapper);
//		}
	    
		refresh(null);

	}
	
	@Override
	public void refresh(Object data) {
		
		loginService.getServerTime(new BaseAsyncCallback<Date>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				super.onFailure(caught);
				refreshUI(new Date());
			}

			@Override
			public void onSuccess(Date result)
			{
				refreshUI(result);
			}

			private void refreshUI(Date date)
			{
				Date serverDate = Utils.localTimeToServerTime(date);
				previousSelectedMonth = formateToMonth(serverDate);
				highlightMostRecentMonthRPDates();
			}
		});
	}

	@Override
	public void refresh(Object data, int changeSource) {
		// TODO Auto-generated method stub		
	}
	
	private ColumnModel createColumnModel()
	{
		
		typeRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				return Utils.backupType2String(model.getBackupType());
			}
			
		};
		
		statusRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				Image image = null;
				if (model.getBackupStatus() == BackupStatusModel.Finished)
					image = AbstractImagePrototype.create(UIContext.IconBundle.status_small_finish()).createImage();
				else if (model.getBackupStatus() == BackupStatusModel.Canceled
						|| model.getBackupStatus() == BackupStatusModel.Missed)
					image = AbstractImagePrototype.create(UIContext.IconBundle.status_small_warning()).createImage();
				else 
					image = AbstractImagePrototype.create(UIContext.IconBundle.status_small_error()).createImage();
				
				image.setTitle(Utils.backupStatus2String(model.getBackupStatus()));
				
				Image image2 = IconHelper.create("images/recoverySetFlag.png").createImage();
				image2.setTitle(UIContext.Constants.recoverySetStartTooltip());
				LayoutContainer container = new LayoutContainer();
				container.add(image);
				if(model.getBackupSetFlag() == 1)
					container.add(image2);
				return container;
//				return image;
			}
			
		};
		
		logicalSizeRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				
				String logicalSize = Utils.bytes2String(model.getLogicalSize());
				if(logicalSize == null)
					return "";
				LabelField messageLabel = new LabelField();
				messageLabel.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
				messageLabel.setValue(logicalSize);
				Utils.addToolTip(messageLabel, UIContext.Messages.homepageRecentBackupLogicalSizeColumnToolTip(logicalSize));
				return messageLabel;
				
			}
			
		};
		
		sizeRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				
				String size = Utils.bytes2String(model.getDataSize());
				if(size == null)
					return "";
				LabelField messageLabel = new LabelField();
				messageLabel.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
				messageLabel.setValue(size);
				Utils.addToolTip(messageLabel, UIContext.Messages.homepageRecentBackupColumnToolTip(size));
				return messageLabel;
				
			}
			
		};
		
		timeRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				if (model.getTime()!=null){
					return Utils.formatDateToServerTime(model.getTime(), model
							.getTimeZoneOffset().longValue());
				}
				return "";
			}
			
		};
		
		nameRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				if(model.getName()!=null && model.getName()!="")
					return UIContext.escapeHTML(model.getName());
				return "";
			}
			
		};
		
		archiveJobRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				if(model.getArchiveJobStatus()!=null)
					return Utils.ConvertArchiveJobStatusToString(model.getArchiveJobStatus());
				return "";
			}
			
		};
		
		schedTypeRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				return Utils.schedFlag2String(model.getPeriodRetentionFlag());
			}
			
		};
		
		List<ColumnConfig> configs = createColumnConfigList();
		
		ColumnModel columnModel = new ColumnModel(configs);	
		columnModel.addListener(Events.WidthChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				grid.reconfigure(store, cm);
			}
	    	
	    });
		
		return columnModel;
	}

	protected List<ColumnConfig> createColumnConfigList() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("BackupStatus", UIContext.Constants.homepageRecentBackupColumnStatusHeader(), 60, statusRenderer));
		configs.add(Utils.createColumnConfig("Type", UIContext.Constants.homepageRecentBackupColumnSchedTypeHeader(), 115, schedTypeRenderer));
		configs.add(Utils.createColumnConfig("backupType", UIContext.Constants.homepageRecentBackupColumnTypeHeader(), 115, typeRenderer));
		configs.add(Utils.createColumnConfig("Time", UIContext.Constants.homepageRecentBackupColumnDateTimeHeader(), 155, timeRenderer));
		configs.add(Utils.createColumnConfig("logicalSize", UIContext.Constants.homepageRecentBackupColumnLogicalSizeHeader(), 115, logicalSizeRenderer));
		configs.add(Utils.createColumnConfig("dataSize", UIContext.Constants.homepageRecentBackupColumnSizeHeader(), 145, sizeRenderer));
		
		CustomizationModel customizedModel = UIContext.customizedModel;
		Boolean isFileCopyEnabled = customizedModel.get("FileCopy");
		
		if(isFileCopyEnabled)
		{	
		configs.add(Utils.createColumnConfig("archiveJob", UIContext.Constants.homepageRecentBackupColumnNameArchiveStatus(), 135, archiveJobRenderer));
		}
		configs.add(Utils.createColumnConfig("Name", UIContext.Constants.homepageRecentBackupColumnNameHeader(), 80, nameRenderer));
		return configs;
	}
	
	private EnhancedHightlightedDataPicker createDatePicker()
	{
		EnhancedHightlightedDataPicker picker = new EnhancedHightlightedDataPicker()
		{
			private int mpSelMonth = -1;
			private int mpSelYear = -1;
			
			@Override
			  public void onComponentEvent(ComponentEvent be) {
				super.onComponentEvent(be);
				if (be.getEventTypeInt() == Event.ONMOUSEUP)
				{
					be.stopEvent();
					El target = be.getTargetEl();
					String cls = target.getStyleName();
					if (cls.indexOf("x-date-left-icon") >= 0)
					{
						DateWrapper wrapper = new DateWrapper(previousSelectedMonth);
						Date newDate = wrapper.addMonths(-1).asDate();
						highlightDatesIfMonthChange(newDate, true);
					}
					else if (cls.indexOf("x-date-right-icon") >= 0)
					{
						DateWrapper wrapper = new DateWrapper(previousSelectedMonth);
						Date newDate = wrapper.addMonths(1).asDate();
						highlightDatesIfMonthChange(newDate, true);
					}
				}
			}
			@Override
			protected void onClick(ComponentEvent be)
			{
				super.onClick(be);
				be.stopEvent();
				El target = be.getTargetEl();
				El pn = null;
				if ((pn = target.findParent("td.x-date-mp-month", 2)) != null)
				{
					mpSelMonth = pn.dom.getPropertyInt("xmonth");
				}
				else if ((pn = target.findParent("td.x-date-mp-year", 2)) != null)
				{
					mpSelYear = pn.dom.getPropertyInt("xyear");
				}
				else if (target.is("button.x-date-mp-ok"))
				{
					DateWrapper wrap = new DateWrapper(getValue());
					if (mpSelYear == -1)
						mpSelYear = wrap.getFullYear();
					if (mpSelMonth == -1)
						mpSelMonth = wrap.getMonth();
					DateWrapper d = new DateWrapper(mpSelYear, mpSelMonth, 1);
					highlightDatesIfMonthChange(d.asDate(), true);
					mpSelYear = -1;
					mpSelMonth = -1;
				}
			}
		};
		
		picker.addListener(Events.Select, new Listener<ComponentEvent>()
		{

			public void handleEvent(ComponentEvent be)
			{
				Date newDate = datePicker.getValue();
				highlightDatesIfMonthChange(newDate, false);
				previousSelectedMonth = formateToMonth(newDate);
				DateWrapper startDR = new DateWrapper(newDate);
				String startDate = getServerDateString(startDR, false);
				//DateWrapper endDR = startDR.addDays(1);
				// koyto02 startDR.addDays(1).addMillis(-1) results in 11:59:59, so manually set 23:59:59
				String endDate = startDate.replace("00:00:00", "23:59:59"); 
				updateRecoveryPointList(startDate, endDate);
			}

		});
		picker.addStyleName("homepage-date-picker");
		return picker;
	}
	

	
	protected String getServerDateString(DateWrapper beginDateWrap,
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
	
	protected Date recPointModel2ServerDate(RecoveryPointModel rpmodel) {
		long timeDiffLocalAndServer = rpmodel.getTime().getTimezoneOffset()
				* 60 * 1000 + rpmodel.getTimeZoneOffset();
		Date serverDate = new Date(rpmodel.getTime().getTime()
				+ timeDiffLocalAndServer);
		return serverDate;
	}
	
	protected Date formateToMonth(Date newDate) {
		DateWrapper wrapper = new DateWrapper(newDate);
		wrapper = new DateWrapper(wrapper.getFullYear(), wrapper.getMonth(), 1);
		return wrapper.asDate();
	}
	
	protected void highlightMostRecentMonthRPDates() {
		if (datePicker == null) {
			return;
		}

		datePicker.clearAll();
		datePicker.setEnabled(false);
		clearRecoveryPoints();

		DateWrapper startDate = new DateWrapper(new Date(0));
		DateWrapper endDate = startDate.addYears(8029);

		String asStartDate = getServerDateString(startDate, false);
		String asEndDate = getServerDateString(endDate, false);

		getRecentBackupsByServerTime(BackupTypeModel.All, BackupStatusModel.All, asStartDate, asEndDate, false,
				new BaseAsyncCallback<RecoveryPointModel[]>()
				{

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						datePicker.setEnabled(true);
					}

					@Override
					public void onSuccess(RecoveryPointModel[] result) {
						Date latestHighlightedDate = null;
						int mostRecentMonth = -1;
						if (result == null || result.length == 0)
						{
							datePicker.setEnabled(true);
							datePicker.showToday();
						}
						else
						{
							for (int i = 0; i < result.length; i++)
							{

								Date serverDate = recPointModel2ServerDate(result[i]);
								if (mostRecentMonth == -1)
								{
									mostRecentMonth = serverDate.getMonth();
									previousSelectedMonth = formateToMonth(serverDate);
								}
//								
								datePicker.addSelectedDate(serverDate, result[i].getBackupStatus() == BackupStatusModel.Finished, 
										serverDate.getMonth() == mostRecentMonth);
								if(result[i].getBackupSetFlag() > 0) {
									datePicker.addBackupSetFlag(serverDate);
								}

								if ((latestHighlightedDate == null || latestHighlightedDate.before(serverDate)))
								{
									latestHighlightedDate = serverDate;
								}
							}

							if (latestHighlightedDate != null)
							{
								datePicker.setValue(latestHighlightedDate, true);

								DateWrapper beginDateWrap = new DateWrapper(datePicker.getValue());
								Date endDate = beginDateWrap.addDays(1).asDate();
								DateWrapper endDateWrap = new DateWrapper(endDate);

								String strSvrBeginDate = getServerDateString(beginDateWrap, false);

								String strSvrEndDate = getServerDateString(endDateWrap, false);

								updateRecoveryPointList(strSvrBeginDate, strSvrEndDate);
							}
						}

						datePicker.repaint();
						datePicker.highlightSelectedDates();
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
	
	private void highlightDates(Date date, final boolean useLastDayInMonth)
	{
		if (datePicker != null)
		{
			datePicker.setEnabled(false);
			DateWrapper dr = new DateWrapper(date);

			final int currentYear = dr.getFullYear();
			final int currentMonth = dr.getMonth();

			int maxExtraDays = 42 - dr.getDaysInMonth();

			DateWrapper startDate = new DateWrapper(currentYear, currentMonth, 1);
			startDate = startDate.addDays(-maxExtraDays);
			DateWrapper endDate = new DateWrapper(currentYear, currentMonth + 1, 1);
			endDate = endDate.addDays(maxExtraDays);

			clearRecoveryPoints();
			
			{
				datePicker.clearAll();
				String asStartDate = this.getServerDateString(startDate, false);// Utils.serverTimeToLocalTime(startDate.asDate());
				String asEndDate = getServerDateString(endDate, false);
				getRecentBackupsByServerTime(BackupTypeModel.All, BackupStatusModel.All, asStartDate, asEndDate, false,
						new BaseAsyncCallback<RecoveryPointModel[]>()
						{

							@Override
							public void onFailure(Throwable caught)
							{
								super.onFailure(caught);
								datePicker.setEnabled(true);
							}

							@Override
							public void onSuccess(RecoveryPointModel[] result)
							{
								Date latestHighlightedDate = null;
								for (int i = 0, count = result == null ? 0 : result.length; i < count; i++)
								{
									RecoveryPointModel m = result[i];

									Date serverDate = recPointModel2ServerDate(m);

									boolean isInCurrentMonth = serverDate.getMonth() == currentMonth;
//									datePicker.addSelectedDate(serverDate, isInCurrentMonth);
									datePicker.addSelectedDate(serverDate, m.getBackupStatus() == BackupStatusModel.Finished, isInCurrentMonth);
									if(m.getBackupSetFlag() > 0) {
										datePicker.addBackupSetFlag(serverDate);
									}

									if ((latestHighlightedDate == null || latestHighlightedDate.before(serverDate))
											&& isInCurrentMonth)
									{
										latestHighlightedDate = serverDate;
									}
								}

								if (latestHighlightedDate != null)
								{

									Date serverDate = null;
									if (useLastDayInMonth)
									{
										serverDate = latestHighlightedDate;
										datePicker.setValue(serverDate, true);
									}
									else
									{
										serverDate = datePicker.getValue();
									}

									DateWrapper beginDateWrap = new DateWrapper(datePicker.getValue());
									Date endDate = beginDateWrap.addDays(1).asDate();
									DateWrapper endDateWrap = new DateWrapper(endDate);

									String strSvrBeginDate = getServerDateString(beginDateWrap, false);

									String strSvrEndDate = getServerDateString(endDateWrap, false);

									updateRecoveryPointList(strSvrBeginDate, strSvrEndDate);
								}
								else
								{
									datePicker.setEnabled(true);
									Date selectedDate = datePicker.getValue();
									if (selectedDate != null)
									{
										DateWrapper dateWrapper = new DateWrapper(currentYear, currentMonth,
												new DateWrapper(datePicker.getValue()).getDate());
										datePicker.setValue(dateWrapper.asDate(), true);
									}
								}

								datePicker.repaint();
								datePicker.highlightSelectedDates();
							}

						});
			}

		}
	}
	
	
	protected void updateRecoveryPointList(String beginDate, String endDate)
	{
		datePicker.setEnabled(false);
		grid.mask(UIContext.Constants.loadingIndicatorText());	
		
		getRecentBackupsByServerTime(BackupTypeModel.All, BackupStatusModel.All, beginDate, endDate, true,
				new BaseAsyncCallback<RecoveryPointModel[]>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						clearRecoveryPoints();
						grid.unmask();						
						super.onFailure(caught);
						datePicker.setEnabled(true);
					}

					@Override
					public void onSuccess(RecoveryPointModel[] result)
					{
						clearRecoveryPoints();
						if (result != null)
						{
							for (int i = 0; i < result.length; i++)
							{
								store.add(result[i]);
							}

//							if (result.length > 0)
//							{
//								grid.getSelectionModel().select(0, false);
//								grid.getView().scrollToTop();
//							}
						}
						
						grid.setAutoExpandColumn("Name");
						grid.reconfigure(store, cm);
						grid.unmask();
						datePicker.setEnabled(true);
					}

				});
	}
	
	protected void getRecentBackupsByServerTime(int type, int status, String beginDate, String endDate, boolean needCatalogStatus, AsyncCallback<RecoveryPointModel[]> callback){
		service.getRecentBackupsByServerTime(type, status, beginDate, endDate, needCatalogStatus, callback);
	}
	
	protected void clearRecoveryPoints() {
		store.removeAll();
	}
	
	public void refreshLayout()
	{
		DeferredCommand.addCommand(new Command()
		{
			@Override
			public void execute()
			{
				if (grid != null && store != null && cm != null)
				{
					grid.reconfigure(store, cm);
				}
			}
		});
	}

}
