package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupSetInfoModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;

public class BackupSetsDetailWindow extends Window {
	protected final HomepageServiceAsync service = GWT.create(HomepageService.class);
	private BackupSetsDetailWindow backupSetsDetailWindow;
		
	private Button okButton;
	private Button cancelButton;
	public final int MIN_WIDTH = 90;
	
	private Grid<BackupSetInfoModel> grid;
	private ListStore<BackupSetInfoModel> store;
	private ColumnModel cm;
	protected BaseAsyncCallback<ArrayList<BackupSetInfoModel>> asyncCallback;
	
	public BackupSetsDetailWindow () {
		
		this.setResizable(false);
		backupSetsDetailWindow = this;
		backupSetsDetailWindow.setWidth(550);	
		backupSetsDetailWindow.setHeight(250);
		backupSetsDetailWindow.setHeadingHtml(UIContext.Constants.BackupSetsDetailHeader());
		backupSetsDetailWindow.setLayout(new RowLayout());
		
		store = new ListStore<BackupSetInfoModel>();
		cm = createColumnModel();			
		grid = new Grid<BackupSetInfoModel>(store, cm);		
		grid.setLoadMask(true);		
		grid.setAutoExpandMax(3000);
		grid.setAutoExpandMin(75);	    
		grid.setStripeRows(true);
		//grid.setSize(400, 200);
//		grid.setHeight(200);
		grid.setHeight("98%");
		backupSetsDetailWindow.add(grid, new RowData(1, 1));
		defineBackupSetsDialogButtons();	
		this.defineAsyncCallback();
		updateRecoverySetList();
	}

	
	private void defineBackupSetsDialogButtons()
	{
		okButton = new Button();
		okButton.ensureDebugId("87969F9F-D503-41af-A289-4A887B082104");
		okButton.setText(UIContext.Constants.close());
		okButton.setMinWidth(MIN_WIDTH);		
		okButton.setAutoWidth(true);
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce) {
				backupSetsDetailWindow.hide();
			}
		});		
		backupSetsDetailWindow.addButton(okButton);
		
		
/*		cancelButton = new Button();
		cancelButton.ensureDebugId("8A249B5A-80F0-4424-9AF2-D4ECA9C48091");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.setMinWidth(MIN_WIDTH);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				backupSetsDetailWindow.hide();
				
			}});		
		backupSetsDetailWindow.addButton(cancelButton);	*/
	}
	
	

	private ColumnModel createColumnModel() {
		GridCellRenderer<BackupSetInfoModel> startTimeRenderer = new GridCellRenderer<BackupSetInfoModel>() {

			@Override
			public Object render(BackupSetInfoModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupSetInfoModel> store, Grid<BackupSetInfoModel> grid) {
				RecoveryPointModel start = model.startRecoveryPoint;
				return Utils.formatDateToServerTime(start.getTime(), start
						.getTimeZoneOffset().longValue());
			}
			
		};
		
		GridCellRenderer<BackupSetInfoModel> endTimeRenderer = new GridCellRenderer<BackupSetInfoModel>() {

			@Override
			public Object render(BackupSetInfoModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupSetInfoModel> store, Grid<BackupSetInfoModel> grid) {
				RecoveryPointModel end = model.endRecoveryPoint;
				//for the first row, we think it's always a incomplete recovery set, 
				//and we don't know what is it's last recovery point, so it's "Now"
				if(rowIndex == 0)
//					return UIContext.Constants.backupSetEndTimeNow();
					return UIContext.Constants.NA();
				else if(end != null)
					return Utils.formatDateToServerTime(end.getTime(), end
							.getTimeZoneOffset().longValue());
				else//if there is only one recovery point in the set, then the last recovery point is the same as the first one.
					return Utils.formatDateToServerTime(model.startRecoveryPoint.getTime(), 
							model.startRecoveryPoint.getTimeZoneOffset());
			}
			
		};
		
		GridCellRenderer<BackupSetInfoModel> sizeRenderer = new GridCellRenderer<BackupSetInfoModel>() {

			@Override
			public Object render(BackupSetInfoModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupSetInfoModel> store, Grid<BackupSetInfoModel> grid) {
				
				String size = Utils.bytes2String(model.getTotalSize());
				if(size == null)
					return "";
				else
					return size;
			}
			
		};
		
		GridCellRenderer<BackupSetInfoModel> countRenderer = new GridCellRenderer<BackupSetInfoModel>() {

			@Override
			public Object render(BackupSetInfoModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupSetInfoModel> store, Grid<BackupSetInfoModel> grid) {
				return model.getCount();
			}
			
		};
		
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
				
		configs.add(Utils.createColumnConfig("StartTime", UIContext.Constants.startBackupSetColumnDateTimeHeader(), 170, startTimeRenderer));
		configs.add(Utils.createColumnConfig("EndTime", UIContext.Constants.endBackupSetColumnDateTimeHeader(), 170, endTimeRenderer));
		configs.add(Utils.createColumnConfig("dataSize", UIContext.Constants.homepageRecentBackupColumnSizeHeader(), 100, sizeRenderer));
		configs.add(Utils.createColumnConfig("count", UIContext.Constants.recoveryPointsNumColumnHeader(), 80, countRenderer));
		
		ColumnModel columnModel = new ColumnModel(configs);	
		columnModel.addListener(Events.WidthChange, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				grid.reconfigure(store, cm);
			}
	    	
	    });
		
		return columnModel;
	}
	
	
	private void defineAsyncCallback() {
		asyncCallback = new BaseAsyncCallback<ArrayList<BackupSetInfoModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				grid.unmask();
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(ArrayList<BackupSetInfoModel> result) {
				clearRecoveryPoints();
				if(result != null) {
					for(BackupSetInfoModel model : result)
						store.add(model);
				}
				grid.unmask();
			}
		};
	}
	
	private void updateRecoverySetList()
	{
		grid.mask(UIContext.Constants.loadingIndicatorText());
		refresh();
	}
	
	protected void refresh() {
		service.getBackupSetInfo(null, asyncCallback);;
	}
	
	private void clearRecoveryPoints() {
		store.removeAll();
	}
}
