package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.ProtectionInformationModel;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
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
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;

public class ProtectionInformationPanel extends LayoutContainer implements IRefreshable{
	protected Timer timer;
	protected final HomepageServiceAsync service = GWT.create(HomepageService.class);
	protected ListStore<ProtectionInformationModel> store;
	protected ColumnModel cm;
	protected Grid<ProtectionInformationModel> grid;
	protected int loadingTime = 0;
	
	public void render(Element target, int index) {
		super.render(target, index);
		
		store = new ListStore<ProtectionInformationModel>();  
		
		GridCellRenderer<ProtectionInformationModel> typeRenderer = new GridCellRenderer<ProtectionInformationModel>() {

			@Override
			public Object render(ProtectionInformationModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProtectionInformationModel> store, Grid<ProtectionInformationModel> grid) {
				return Utils.backupType2String(model.getBackupType());
			}
			
		};
		
		GridCellRenderer<ProtectionInformationModel> totalLogicalSizeRenderer = new GridCellRenderer<ProtectionInformationModel>() {

			@Override
			public Object render(ProtectionInformationModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProtectionInformationModel> store, Grid<ProtectionInformationModel> grid) {
				
				// when file copy, can not get total logical size, 
				// temporarily hard code it as "N/A".
				if (model.getBackupType() == 4)
					return UIContext.Constants.NA();
				
				String totalLogicalSize = Utils.bytes2String(model.getTotalLogicalSize());
				if(totalLogicalSize == null)
					return "";
				LabelField messageLabel = new LabelField();
				messageLabel.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
				messageLabel.setValue(totalLogicalSize);
				Utils.addToolTip(messageLabel, UIContext.Messages.homepageDataProtectedColumnToolTip());
				return messageLabel;

			}
		};
		
		GridCellRenderer<ProtectionInformationModel> sizeRenderer = new GridCellRenderer<ProtectionInformationModel>() {

			@Override
			public Object render(ProtectionInformationModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProtectionInformationModel> store, Grid<ProtectionInformationModel> grid) {
				
				if (model.getBackupType() == 0 || model.getBackupType() == 1 || model.getBackupType() == 2)
					if (model.isDedupe())
					    return UIContext.Constants.NA();
				
				String size = Utils.bytes2String(model.getSize());
				if(size == null)
					return "";
				LabelField messageLabel = new LabelField();
				messageLabel.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
				messageLabel.setValue(size);
				Utils.addToolTip(messageLabel, UIContext.Messages.homepageSpaceOccupiedColumnToolTip());
				return messageLabel;

			}
		};
		
		lastBackupTimeRenderer = new GridCellRenderer<ProtectionInformationModel>() {

			@Override
			public Object render(ProtectionInformationModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProtectionInformationModel> store, Grid<ProtectionInformationModel> grid) {
				if (model.getLastBackupTime()!=null && model.getLastBackupTime()!=""){
					
					if(model.getSchedule() != null && model.getBackupType() == BackupTypeModel.Archive)
					{
						//return model.getLastBackupTime();
						Date date = Utils.serverString2LocalDate(model.getLastBackupTime());
						return Utils.localDate2LocalString(date);
					}
					
					if((model.getSchedule() != null) && (model.getSchedule().getIntervalUnit() != 3 || model.getBackupType() == BackupTypeModel.Copy)){
						Date date = Utils.serverString2LocalDate(model.getLastBackupTime());
						return Utils.localDate2LocalString(date);
					}
				}
				return "";
			}
			
		};
		
		GridCellRenderer<ProtectionInformationModel> nextEventRenderer = new GridCellRenderer<ProtectionInformationModel>() {

			@Override
			public Object render(ProtectionInformationModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProtectionInformationModel> store, Grid<ProtectionInformationModel> grid) {
				if (model.getNextRunTime()!=null)
					return Utils.formatDateToServerTime(model.getNextRunTime(), 
							model.getNextTimeZoneOffset());
				return "";
			}
			
		};
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("backupType", UIContext.Constants.homepageProtectionInformationColumnTypeHeader(), 115, typeRenderer));
		configs.add(Utils.createColumnConfig("count", UIContext.Constants.homepageProtectionInformationColumnCountHeader(), 60));
		configs.add(Utils.createColumnConfig("totalLogicalSize", UIContext.Constants.homepageProtectionInformationColumnTotalLogicalSizeHeader(), 95, totalLogicalSizeRenderer));
		configs.add(Utils.createColumnConfig("size", UIContext.Constants.homepageProtectionInformationColumnSizeHeader(), 125, sizeRenderer));
		configs.add(Utils.createColumnConfig("lastBackupTime", UIContext.Constants.homepageProtectionInformationColumnLastBackupTimeHeader(), 160, lastBackupTimeRenderer));		
		configs.add(Utils.createColumnConfig("nextEvent", UIContext.Constants.homepageProtectionInformationColumnEventHeader(), 100,nextEventRenderer));
		
		cm = new ColumnModel(configs);
		//the following listener is added to solve the issue that the grid only shows two rows 
		//with a vertical scroll bar on IE8 browser
		cm.addListener(Events.WidthChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				int width = 0;
				for (int i = 0; i < cm.getColumnCount(); i++) {
					width += cm.getColumnWidth(i);
				}
				
				if(width >= grid.getWidth()) {
					panel.setHeight(130);
				}
				else {
					panel.setHeight(113);
				}
				
				panel.layout(true);
			}
	    	
	    });
	    grid = new Grid<ProtectionInformationModel>(store, cm);
	    grid.setAutoExpandColumn("nextEvent");
	    grid.setAutoExpandMax(3000);
	    grid.setStripeRows(true);
	    grid.setLoadMask(true);
	    grid.mask(UIContext.Constants.loadingIndicatorText());
	    
	    panel = new ContentPanel();
	    panel.setCollapsible(true);
 	    panel.setHeadingHtml(UIContext.Constants.homepageProtectionInformationTableHeader());
 	    
		panel.addListener(Events.Expand, new Listener<ComponentEvent>()
		{
			@Override
			public void handleEvent(ComponentEvent be)
			{
				if (be != null && be.getComponent() != null && be.getComponent() instanceof ContentPanel)
				{
					((ContentPanel) (be.getComponent())).layout(true);
				}
			}

		});
	    
//		if (!withVCM)
//		{
			grid.setAutoHeight(true);

			panel.setAutoHeight(true);
			panel.setLayout(new FitLayout());
			panel.add(grid);

			this.setLayout(new RowLayout(Orientation.VERTICAL));
			add(panel, new RowData(1, -1));
//		}
//		else
//		{
//			grid.setAutoHeight(true);
//			grid.setAutoWidth(true);
//			grid.setWidth("100%");
//
//			panel.setWidth("100%");
//			panel.setAutoHeight(true);
//			panel.setAutoWidth(true);
//
//			panel.setLayout(new FitLayout());
//			panel.add(grid);
//
//			add(panel);
//		}

	    if(!Utils.checkIE6(com.extjs.gxt.ui.client.GXT.getUserAgent()))
	    	refresh(null);
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();
		if(Utils.checkIE6(com.extjs.gxt.ui.client.GXT.getUserAgent()))
	    	refresh(null);
	}

	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		if(store != null && store.getCount() > 0)
			grid.reconfigure(store, cm);
	}


	protected ContentPanel panel;
	protected GridCellRenderer<ProtectionInformationModel> lastBackupTimeRenderer;

	@Override
	public void refresh(Object data){	
		if(data == null){
			service.getProtectionInformation(new BaseAsyncCallback<ProtectionInformationModel[]>(){
				@Override
				public void onFailure(Throwable caught) {
					grid.unmask();
					super.onFailure(caught);
				}
				@Override
				public void onSuccess(ProtectionInformationModel[] result) {
					if(result==null || result.length == 0){
//						if(!panel.isCollapsed())
//							panel.collapse();
						grid.unmask();
						return;
					}
					refresh(result);
					grid.unmask();
				}
			});
		}else {
			if(!(data instanceof ProtectionInformationModel[]))
				return;
			
			ProtectionInformationModel[] result = (ProtectionInformationModel[])data;
			if(store==null){
				store = new ListStore<ProtectionInformationModel>();
			}
			
			boolean isNeedReconfig = store.getCount() > 0 ? false : true;
			
			CustomizationModel customizedModel = UIContext.customizedModel;
			Boolean isFileCopyEnabled = customizedModel.get("FileCopy");
			
//			store.removeAll();
			for (int i = 0; i < result.length; i++){
				
				ProtectionInformationModel model = result[i];
				
				if(!isFileCopyEnabled && (model.getBackupType()==BackupType.Archive))
				{
				}
				else
				{	
					if(store.getCount() > i)
						store.update(result[i]);
					else
						store.add(result[i]);
				}
//				store.add(result[i]);
			}
			
			if(!panel.isExpanded())
				panel.expand();
			
			if(isNeedReconfig)
				grid.reconfigure(store, cm);
			
			//To solve issue 19148598: when using IE6 and https protocol, this pane does't expand.
			//This issue can be solved by reloading the protection summary after the UI is loaded.
			//if we do not support IE6 or IE6 can expand. Remove the following if cause.
			String protocal = Location.getProtocol();
			if(Utils.checkIE6(com.extjs.gxt.ui.client.GXT.getUserAgent()) && protocal != null && protocal.toLowerCase().startsWith("https")){
				if(loadingTime <= 2) { 
					loadingTime++;
					if(loadingTime == 1) {
						final ProtectionInformationModel[] result1 = result;
						Timer timer1 = new Timer() {
							public void run() {
								if(loadingTime == 1) {
									store.removeAll();
									for (int i = 0; i < result1.length; i++){
										store.add(result1[i]);
									}
									grid.reconfigure(store, cm);
									panel.expand();
								}
							}
						};
						timer1.schedule(8000);
					}
				}
			}
			if(timer == null){
				timer = new Timer() {
					public void run() {
						updatePartialProtectionInfo();
					}
				};
				timer.schedule(3000);
				timer.scheduleRepeating(3000); 
			}
		}
	}
	
	private void updatePartialProtectionInfo(){
		
		if(store == null || store.getCount()==0)
			return;
		
		service.updateProtectionInformation(new BaseAsyncCallback<ProtectionInformationModel[]>(){
			
			@Override
			public void onFailure(Throwable caught) {
				if(UIContext.homepagePanel.processServerDown(caught, "updateProtectionInformation")) {
    				showMessageBoxForReload(UIContext.Constants.cantConnectToServer());
    			}else
    				super.onFailure(caught);
			}

			@Override
			public void onSuccess(ProtectionInformationModel[] result) {
				if(result == null||result.length==0)
					return;
				for (int i = 0; i < result.length; i++)
				{
					ProtectionInformationModel model = store.getAt(i);
					CustomizationModel customizedModel = UIContext.customizedModel;
					Boolean isFileCopyEnabled = customizedModel.get("FileCopy");

					if(model!=null)
					{	
						if(!isFileCopyEnabled && (model.getBackupType()==BackupType.Archive))
						{
						}
						else
						{	
							model.setSchedule(result[i].getSchedule());
							model.setNextRunTime(result[i].getNextRunTime());
							model.setNextTimeZoneOffset(result[i].getNextTimeZoneOffset());
							store.update(model);
						}
					}
				}
//				grid.reconfigure(store, cm);
			}
		});
	}

	@Override
	public void refresh(Object data, int changeSource) {
		// TODO Auto-generated method stub
		
	}
}
