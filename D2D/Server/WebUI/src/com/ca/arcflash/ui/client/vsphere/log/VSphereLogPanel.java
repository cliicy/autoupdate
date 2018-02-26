package com.ca.arcflash.ui.client.vsphere.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyManager;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.LogEntry;
import com.ca.arcflash.ui.client.model.LogEntryType;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class VSphereLogPanel extends LayoutContainer {
	
	private static final int COUNT_EVERY_PAGE = 25;
	//It is true when 1. login vSphere vcm, 2. login VCM monitor and select a vSphere VM on the tree.
	private boolean isOnVCMTab = false;
	
	public VSphereLogPanel(boolean isOnVCMTab) {
		this.isOnVCMTab = isOnVCMTab;
	}
	
	public void render(Element target, int index) {
		super.render(target, index);

		final CommonServiceAsync service = GWT.create(CommonService.class);
		final ColdStandbyServiceAsync coldStandbyService = GWT.create(ColdStandbyService.class);
		setStyleAttribute("margin", "4px");
		this.setLayout(new FitLayout());

		RpcProxy<PagingLoadResult<LogEntry>> proxy = new RpcProxy<PagingLoadResult<LogEntry>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<LogEntry>> callback) {
				try {
					BackupVMModel backupVM = UIContext.backupVM;
					if(isOnVCMTab && backupVM == null && ColdStandbyManager.getVMInstanceUUID() != null) {
						backupVM = new BackupVMModel();
						backupVM.setVmInstanceUUID(ColdStandbyManager.getVMInstanceUUID());
						coldStandbyService.getVMActivityLogs((PagingLoadConfig) loadConfig, backupVM, callback);
					}
					else
						service.getVMActivityLogs((PagingLoadConfig) loadConfig, backupVM,
								callback);
				} catch (BusinessLogicException e) {
					e.printStackTrace();
				} catch (ServiceConnectException e) {
					e.printStackTrace();
				} catch (ServiceInternalException e) {
					e.printStackTrace();
				}
			}
		};

		// loader
		final PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(
				proxy);
		loader.setRemoteSort(true);

		ListStore<LogEntry> store = new ListStore<LogEntry>(loader);

		final PagingToolBar toolBar = new PagingToolBar(COUNT_EVERY_PAGE){	
			@Override
			protected void onRender(Element target, int index) {				
				super.onRender(target, index);
				this.pageText.ensureDebugId("6F6347EB-DF1A-47e3-A024-FF5983D24394");
				this.pageText.setWidth("68px");
				this.pageText.addKeyPressHandler(new KeyPressHandler(){
					@Override
					public void onKeyPress(KeyPressEvent event) {
						int event_key = event.getNativeEvent().getKeyCode();
					    if(event.isControlKeyDown() || event_key == KeyCodes.KEY_ENTER || event_key == KeyCodes.KEY_BACKSPACE || event_key == KeyCodes.KEY_DELETE || event_key == KeyCodes.KEY_LEFT || event_key == KeyCodes.KEY_RIGHT || event_key == KeyCodes.KEY_HOME|| event_key == KeyCodes.KEY_END){
					    	return;
					    }
					    char key = event.getCharCode();
					    if(!Character.isDigit(key)){
					    	pageText.cancelKey();
					    }
					}
					
				});
			}			
		};
		
		toolBar.bind(loader);
		
		SeparatorToolItem item = new SeparatorToolItem();
		item.ensureDebugId("C0C8765E-1A25-4e92-862F-9DDD3BAD3B04");
		item.setStyleAttribute("margin-left", "4px");
		item.setStyleAttribute("margin-right", "4px");
		toolBar.add(item);
		
		Button deleteButton = new Button();
		deleteButton.ensureDebugId("706F3A78-EF55-410a-B401-02A78DDF4B02");
		deleteButton.setIcon(IconHelper.create("images/delete.gif"));
		deleteButton.setText(UIContext.Constants.activityLogDeleteButtonLabel());
		Utils.addToolTip(deleteButton, UIContext.Constants.activityLogDeleteButtonTooltip());
		toolBar.add(deleteButton);
		
		
		GridCellRenderer<LogEntry> renderer = new GridCellRenderer<LogEntry>() {

			@Override
			public Object render(LogEntry model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LogEntry> store, Grid<LogEntry> grid) {
				Image image;
				if (model.getType() == LogEntryType.Information)
					image = AbstractImagePrototype.create(UIContext.IconBundle.logMsg()).createImage();
				else if (model.getType() == LogEntryType.Error)
					image = AbstractImagePrototype.create(UIContext.IconBundle.logError()).createImage();
				else
					image = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
				
				image.setTitle(Utils.activityLogType2String(model.getType()));
				return image;
			}
			
		};
		
		GridCellRenderer<LogEntry> jobIDRenderer = new GridCellRenderer<LogEntry>() {

			@Override
			public Object render(LogEntry model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LogEntry> store, Grid<LogEntry> grid) {
				if (model.getJobID() <=0)
					return "";
				else
					return model.getJobID(); 
			}
			
		};
		
		GridCellRenderer<LogEntry> timeRenderer = new GridCellRenderer<LogEntry>() {

			@Override
			public Object render(LogEntry model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LogEntry> store, Grid<LogEntry> grid) {
				Date time = model.getTime();
				if(time != null)
				{
					return Utils.formatDateToServerTime(time, model.getTimeZoneOffset());
				}
				return "";
			}
			
		};
		
		GridCellRenderer<LogEntry> messageRenderer = new GridCellRenderer<LogEntry>() {

			@Override
			public Object render(LogEntry model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LogEntry> store, Grid<LogEntry> grid) {
				LabelField messageLabel = new LabelField();
				String message = model.getMessage();
				if(message != null)
				{
					messageLabel.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
					messageLabel.setValue(message);
					Utils.addToolTip(messageLabel, message);
					return messageLabel;
				}
				return "";
			}
			
		};
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(createColumnConfig("Type", UIContext.Constants.activityLogColumnHeaderType(), 50, renderer));
		configs.add(createColumnConfig("JobID", UIContext.Constants.activityLogColumnJobID(), 80, jobIDRenderer));
		configs.add(createColumnConfig("Time", UIContext.Constants.activityLogColumnHeaderTime(), 150, timeRenderer));
		configs.add(createColumnConfig("Message", UIContext.Constants.activityLogColumnHeaderMessage(), 100, messageRenderer));
		
		
		ColumnModel cm = new ColumnModel(configs);
	    Grid<LogEntry> grid = new Grid<LogEntry>(store, cm);
	    grid.addListener(Events.Attach, new Listener<GridEvent<LogEntry>>() {
	      public void handleEvent(GridEvent<LogEntry> be) {
	          loader.load(0, COUNT_EVERY_PAGE);
	      }
	    });
	    
	    grid.setHeight(400);
	    grid.setLoadMask(true);
	    grid.setAutoExpandColumn("Message");
	    grid.disableTextSelection(false);

	    ContentPanel panel = new ContentPanel();
	    panel.setHeaderVisible(false);
	    panel.setLayout(new FitLayout());
	    panel.add(grid);
	    panel.setTopComponent(toolBar);
	    panel.setHeight(420);
	    
	    add(panel);
	    
	    deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				final VSphereDeleteActivityLogWindow window = new VSphereDeleteActivityLogWindow(isOnVCMTab);
				window.setModal(true);
				window.show();
				window.addListener(Events.Hide, new Listener<WindowEvent>() {
					@Override
					public void handleEvent(WindowEvent be) {
						if (window.isDeleted()){
							toolBar.first();
						}
					}
			    });
			}

			});
	}

	public static ColumnConfig createColumnConfig(String id, String header,
			int width) {
		return createColumnConfig(id, header, width, null);
	}

	public static ColumnConfig createColumnConfig(String id, String header,
			int width, GridCellRenderer<LogEntry> renderer) {
		ColumnConfig column = new ColumnConfig();
		column.setStyle("vertical-align:middle;");
		column.setGroupable(false);
		column.setSortable(false);
		column.setMenuDisabled(true);
		column.setId(id);
		column.setHeaderHtml(header);
		if (width >= 0)
			column.setWidth(width);
		if (renderer != null)
			column.setRenderer(renderer);
		return column;
	}
}
