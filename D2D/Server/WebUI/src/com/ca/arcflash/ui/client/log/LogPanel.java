package com.ca.arcflash.ui.client.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.comon.widget.ExtLabelField;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
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
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class LogPanel extends LayoutContainer {
	
	private static final int COUNT_EVERY_PAGE = 80;//80 rows is enough for 30inches monitor  //25;
	
	protected Button deleteButton = null;
	protected PagingToolBar toolBar = null;
	private AppType appType;	
	
	public LogPanel(AppType appType) {
		super();
		this.appType = appType;
	}
	
	public void render(Element target, int index) {
		super.render(target, index);

		setStyleAttribute("margin", "4px");
		this.setLayout(new FitLayout());

		RpcProxy<PagingLoadResult<LogEntry>> proxy = new RpcProxy<PagingLoadResult<LogEntry>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<LogEntry>> callback) {
				try {
					LogWindow.LogOperationHelper.getActivityLogs((PagingLoadConfig) loadConfig,
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
				first.ensureDebugId("565C16AE-705F-475f-BB51-5DF96F903BE9");
				next.ensureDebugId("6BFF8524-4177-418b-8295-6B8234760DE9");
				prev.ensureDebugId("826E88EE-5FD9-4be3-978E-1DD41D8CB695");
				last.ensureDebugId("4E3990E6-0F5D-4567-9E45-DB2D308F37A2");
				refresh.ensureDebugId("5D8BB00B-3D34-4e9a-8A0C-F8F24FF1C2B8");
			}			
		};
		
		toolBar.bind(loader);
		
		SeparatorToolItem item = new SeparatorToolItem();
		item.ensureDebugId("2a939141-3cef-422e-b000-7facd8f1dfb8");
		item.setStyleAttribute("margin-left", "4px");
		item.setStyleAttribute("margin-right", "4px");
		toolBar.add(item);
		
		Button deleteButton = new Button();
		deleteButton.ensureDebugId("33bc8f15-428c-4fd0-8ce3-cde00d43b774");
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
				if (model.getType() == LogEntryType.Information
						|| model.getType() == LogEntryType.Information_Update)
					image = AbstractImagePrototype.create(UIContext.IconBundle.logMsg()).createImage();
				else if (model.getType() == LogEntryType.Error
						|| model.getType() == LogEntryType.Error_Update)
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
				LabelField messageLabel = new ExtLabelField();
				String message = model.getMessage();
				if(message != null)
				{
					messageLabel.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
					messageLabel.setStyleAttribute("white-space", "normal");
					String msg = message;//Format.htmlEncode(message);
					messageLabel.setValue(msg);
					//messageLabel.setToolTip(handleMessage(message)); // handle the message so that it can be shown completely in tooltip.
					messageLabel.setToolTip(new ToolTipConfig(UIContext.Constants.activityLogColumnHeaderMessage(), handleMessage(messageLabel.getValue().toString()))); // handle the message so that it can be shown completely in tooltip.
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
	    grid.setAutoExpandMax(5000);
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
				final DeleteActivityLogWindow window = new DeleteActivityLogWindow();
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
	
	/**
	 * handle the message, show it can be shown completely in tooltip.
	 * @param message
	 * @return
	 */
	private String handleMessage(String message) {
		int interval = 45;
		String newMessage = "";
		if(message==null) {
			return message;
		}
		String[] vars = message.split("\\s");
		
		for(int i=0; i<vars.length; i++) {
			if(vars[i].length() > interval) {
				int j=0;
				for(; j<vars[i].length()/interval; j++) {
					newMessage += vars[i].substring((j*interval), (j*interval)+interval) + "\n";
				}
				
				if(vars[i].length()%interval != 0) {
					newMessage += vars[i].substring(j*interval) + "\n";
				}
			} else {
				newMessage += vars[i] + "\n";
			}
		}
		
		return newMessage;
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
