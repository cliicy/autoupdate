package com.ca.arcflash.ui.client.vsphere.homepage;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.TextMetrics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.HomepageService;
import com.ca.arcflash.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcflash.ui.client.model.BackupStatusModel;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class VSphereRecentBackupPanel extends LayoutContainer implements IRefreshable{
	private final HomepageServiceAsync service = GWT.create(HomepageService.class);
	private final static int TOP	=	10;
	private ListStore<RecoveryPointModel> store;
	private ColumnModel cm;
	private Grid<RecoveryPointModel> grid;
	private TextMetrics metrics = TextMetrics.get();
	private ColumnConfig nameColumn;
    
	public void render(Element target, int index) {
		super.render(target, index);
		metrics.bind();
		store = new ListStore<RecoveryPointModel>();  
		
		GridCellRenderer<RecoveryPointModel> typeRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				return Utils.backupType2String(model.getBackupType());
			}
			
		};
		
		GridCellRenderer<RecoveryPointModel> statusRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				Image image = null;
				if (model.getBackupStatus() == BackupStatusModel.Finished)
					image = AbstractImagePrototype.create(UIContext.IconBundle.status_small_finish()).createImage();
				else if (model.getBackupStatus() == BackupStatusModel.Canceled)
					image = AbstractImagePrototype.create(UIContext.IconBundle.status_small_warning()).createImage();
				else 
					image = AbstractImagePrototype.create(UIContext.IconBundle.status_small_error()).createImage();
				
				image.setTitle(Utils.backupStatus2String(model.getBackupStatus()));
				return image;
			}
			
		};
		
		GridCellRenderer<RecoveryPointModel> sizeRenderer = new GridCellRenderer<RecoveryPointModel>() {

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
		
		GridCellRenderer<RecoveryPointModel> timeRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				if (model.getTime()!=null)
					return Utils.formatDateToServerTime(model.getTime(), model
							.getTimeZoneOffset() != null ? model
							.getTimeZoneOffset() : 0);
				return "";
			}
			
		};
		
		GridCellRenderer<RecoveryPointModel> nameRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store, Grid<RecoveryPointModel> grid) {
				return model.getName();
			}
			
		};
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("BackupStatus", UIContext.Constants.homepageRecentBackupColumnStatusHeader(), 45, statusRenderer));
		configs.add(Utils.createColumnConfig("backupType", UIContext.Constants.homepageRecentBackupColumnTypeHeader(), 115, typeRenderer));
		configs.add(Utils.createColumnConfig("Time", UIContext.Constants.homepageRecentBackupColumnDateTimeHeader(), 125, timeRenderer));
		configs.add(Utils.createColumnConfig("dataSize", UIContext.Constants.homepageRecentBackupColumnSizeHeader(), 70, sizeRenderer));
		nameColumn = Utils.createColumnConfig("Name", UIContext.Constants.homepageRecentBackupColumnNameHeader(), 70, nameRenderer);
		configs.add(nameColumn);
		
		cm = new ColumnModel(configs);
	    grid = new Grid<RecoveryPointModel>(store, cm);
	    grid.setLoadMask(true);
	    grid.mask(UIContext.Constants.loadingIndicatorText());
	    grid.setHeight(310);
	    grid.setAutoWidth(true);
	    grid.setAutoExpandColumn("Name");
	    ContentPanel panel = new ContentPanel();
	    panel.setScrollMode(Scroll.AUTOX);
	    panel.setLayout(new FitLayout());
	    panel.setHeight(310);
	    panel.setHeadingHtml(UIContext.Constants.homepageRecentBackupTableHeader());
	    panel.add(grid);
	   
	    add(panel);

	    refresh(null);
	}

	@Override
	public void refresh(Object data) {
		
			service.getVMRecentBackups(BackupTypeModel.All, BackupStatusModel.All, TOP, UIContext.backupVM,new BaseAsyncCallback<RecoveryPointModel[]>(){
				
				@Override
				public void onFailure(Throwable caught) {
					grid.unmask();
					super.onFailure(caught);
				}
	
				@Override
				public void onSuccess(RecoveryPointModel[] result) {
					store = new ListStore<RecoveryPointModel>();
					/*int max = 0;
				    int temp;
					if (result!=null){
						for (int i = 0; i < result.length; i++)
						{
							store.add(result[i]);
							temp = metrics.getWidth(result[i].getName())+10;
					    	max = temp>max?temp:max;
						}
					}
					
	//				if (max>=120)
	//					//nameColumn.setWidth(max);
	//					cm.setColumnWidth(4, max);					
					if(grid.getWidth() == 0 || max + 355 < grid.getWidth())
					{
						grid.setAutoExpandColumn("Name");
	//					cm.setColumnWidth(4, grid.getWidth()-305);
					}
					else
						cm.setColumnWidth(4, max);*/
					grid.reconfigure(store, cm);
					grid.unmask();
				}
				
			});
	}
	
	@Override
	public void refresh(Object data, int changeSource) {
		// TODO Auto-generated method stub
		
	}
}
