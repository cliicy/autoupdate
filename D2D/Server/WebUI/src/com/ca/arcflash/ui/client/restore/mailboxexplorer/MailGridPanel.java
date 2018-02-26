package com.ca.arcflash.ui.client.restore.mailboxexplorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.GRTBrowsingContextModel;
import com.ca.arcflash.ui.client.model.GRTCatalogItemModel;
import com.ca.arcflash.ui.client.model.GRTPagingLoadResult;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.restore.ExchangeGRTRecoveryPointsPanel;
import com.ca.arcflash.ui.client.restore.PagingContext;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MailGridPanel extends LayoutContainer
{
	private MailboxExplorerPanel mailboxExplorerWindow;
	private LoginServiceAsync service = GWT.create(LoginService.class);
	private Grid<GridTreeNode> grid;
	private ListStore<GridTreeNode> store;
	private PagingLoader<PagingLoadResult<ModelData>> loader;
	private PagingToolBar pagingBar;
	private LabelToolItem labelSelectedMails;
	private TriggerField<String> searchField;

	private static final int PAGESIZE = PagingContext.DEFAULTPAGESIZE;
	
	private GridTreeNode parentNode;
	private GRTBrowsingContextModel contextModel;

	public MailGridPanel(MailboxExplorerPanel window)
	{
		super();
		this.setLayout(new FitLayout());
		mailboxExplorerWindow = window;
	}

	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);

		RpcProxy<PagingLoadResult<GridTreeNode>> proxy = new RpcProxy<PagingLoadResult<GridTreeNode>>()
		{
			@Override
			public void load(Object loadConfig, AsyncCallback<PagingLoadResult<GridTreeNode>> callback)
			{
				final PagingLoadConfig plc = (PagingLoadConfig) loadConfig;
				final AsyncCallback<PagingLoadResult<GridTreeNode>> fcb = callback;
				AsyncCallback<GRTPagingLoadResult> cb = new AsyncCallback<GRTPagingLoadResult>()
				{

					@Override
					public void onFailure(Throwable caught)
					{
						fcb.onFailure(caught);
					}

					@Override
					public void onSuccess(GRTPagingLoadResult result)
					{
						if (contextModel != null && result != null && result.getPagingLoadResult() != null)
						{
							contextModel.setTotal(result.getPagingLoadResult().getTotalLength());
							contextModel.setTotalWithoutFilter(result.getTotalWithoutFilter());
						}
						
						BasePagingLoadResult<GridTreeNode> pagingLoadResult = result.getPagingLoadResult();
						
						if (result != null && pagingLoadResult.getData() != null)
						{
							for (int i = pagingLoadResult.getData().size() - 1; i >= 0; i--)
							{
								// set refer node
								GridTreeNode node = pagingLoadResult.getData().get(i);
								node.setReferNode(parentNode.getReferNode()); // this node should have the save refer nodes as its parent
							}
						}
						
						fcb.onSuccess(pagingLoadResult);
						updateSelectedCount();
					}
				};

				if (contextModel == null)
				{
					contextModel = new GRTBrowsingContextModel();	
//					contextModel.setMailOnly(true);
					contextModel.setSearchKeyword(searchField.getValue());
				}				
							
				service.browseGRTCatalog(parentNode, plc, contextModel, cb);
			}
		};

		// loader
		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
		loader.setRemoteSort(true);

		store = new ListStore<GridTreeNode>(loader);
//		ColumnModel cm = this.createColumnModel();
		ColumnModel cm = new ColumnModel(new ArrayList<ColumnConfig>());
		grid = new Grid<GridTreeNode>(store, cm);
		
		// The columns of importance, icon, attachment are too narrow, so they don't need a sort icon, this is same as Outlook
		grid.setView(new GridView()
		{
			@Override
			protected void updateSortIcon(int colIndex, SortDir dir)
			{
				if (!(colIndex == 1 || colIndex == 2 || colIndex == 3))
				{
					super.updateSortIcon(colIndex, dir);
				}				
			}

			@Override
			protected void doSort(int colIndex, SortDir sortDir)
			{
				GridTreeNode parent = parentNode;
				clearPanel();
				parentNode = parent;
				
				super.doSort(colIndex, sortDir);
			}			
		});
		
		grid.ensureDebugId("1fb37b40-f6cc-4d71-8c8c-3b9bdf21b497");

		// set id for automation
		grid.addListener(Events.ViewReady, new Listener<GridEvent<GridTreeNode>>(){
			
			@Override
			public void handleEvent(GridEvent<GridTreeNode> be)
			{
				if (be != null && be.getGrid() != null)
				{
					Grid<GridTreeNode> g = be.getGrid();
					if (g.getView() != null)
					{
						if (g.getView().getHeader() != null)
						{
							g.getView().getHeader().ensureDebugId("281b4ba1-574e-42b5-a232-2ff9ee51071c");
						}
						
						if (g.getView().getBody() != null)
						{
							g.getView().getBody().setId("b3c80af2-734d-4114-9972-60f6a63bbf41");
						}
					}

				}				
			}});
		
		
		grid.addListener(Events.CellDoubleClick, new Listener<GridEvent<GridTreeNode>>()
		{
			@Override
			public void handleEvent(GridEvent<GridTreeNode> be)
			{
			}
		});

		grid.setLoadMask(true);
		grid.setBorders(false);
//		grid.setAutoExpandColumn("subject");
		grid.setAutoExpandMax(5000);
		grid.getView().setAutoFill(false);

		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.setCollapsible(false);
		panel.setScrollMode(Scroll.AUTOX);
		panel.setBorders(false);
		panel.setHeadingHtml("Mail");

		panel.add(grid);

		// search bar
		searchField = new TriggerField<String>()
		{
			@Override
			protected void onTriggerClick(ComponentEvent ce)
			{
				super.onTriggerClick(ce);
				
				refresh();
			}
				
			@Override
			protected void onKeyDown(FieldEvent fe)
			{
				if (fe.getKeyCode() == KeyCodes.KEY_ENTER)
				{
					refresh();
				}
				super.onKeyDown(fe);
			}
			
		};
		searchField.ensureDebugId("9979e6e5-9907-4636-b98f-1aa01187ad0f");
		searchField.setTriggerStyle("x-form-search-trigger"); // use a search icon
		searchField.setWidth(220);

		ToolBar toolBar = new ToolBar();
		toolBar.setBorders(true);
		toolBar.add(new FillToolItem());  
		toolBar.add(new LabelToolItem(UIContext.Constants.restoreSearchSubject()));
		toolBar.add(searchField);

		panel.setTopComponent(toolBar);

		// paging toolbar
		pagingBar = new PagingToolBar(PAGESIZE)
		{
			@Override
			protected void onRender(Element target, int index)
			{
				super.onRender(target, index);
//				this.pageText.setWidth("68px");
				this.pageText.addKeyPressHandler(new KeyPressHandler()
				{
					@Override
					public void onKeyPress(KeyPressEvent event)
					{
						char key = event.getCharCode();
						if (event.isControlKeyDown() || key == KeyCodes.KEY_ENTER || key == KeyCodes.KEY_BACKSPACE
								|| key == KeyCodes.KEY_DELETE)
						{
							return;
						}
						
						if (!Character.isDigit(key))
						{
							pageText.cancelKey();
						}
					}

				});
				
				first.ensureDebugId("839ABB23-2FD8-4f59-BD1E-E366E4898AC0");
				last.ensureDebugId("A1A83371-AB2B-4bdd-917B-B59BD669B45B");
				prev.ensureDebugId("DA3ED662-278A-4e74-84E0-B721847332C8");
				next.ensureDebugId("461338D4-806A-4d26-8821-EE1DF7F5B5A0");
				refresh.ensureDebugId("2913ACCB-407A-4033-AB19-8D845E4FE764");
			}
		};
		pagingBar.ensureDebugId("209e12a7-b9b6-4080-aeaf-8d716dd798d0");
		pagingBar.bind(loader);
		
		pagingBar.add(new SeparatorToolItem()); 		  
		 		
		// to display the number of selected mails
		labelSelectedMails = new LabelToolItem("");	
		pagingBar.add(labelSelectedMails);
		
		panel.setBottomComponent(pagingBar);

		this.add(panel);
	}

	private ColumnModel createColumnModel()
	{
		ColumnConfig checkboxColumn = createCheckBoxColumn();				
		ColumnConfig importanceColumn = createImportanceColumn();		
		ColumnConfig attachmentColumn = createAttachmentColumn();
		
		ColumnConfig subjectColumn = new ColumnConfig("subject", UIContext.Constants.restoreSubjectColumn(), 100);
		subjectColumn.setMenuDisabled(true);
		subjectColumn.setStyle("vertical-align:middle;");
		subjectColumn.setAlignment(HorizontalAlignment.LEFT);
		subjectColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getObjName() != null)
					{
						String value = grtModel.getObjName();
						return value;
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});

		ColumnConfig fromColumn = new ColumnConfig("sender", UIContext.Constants.restoreFromColumn(), 80);
		fromColumn.setMenuDisabled(true);
		fromColumn.setStyle("vertical-align:middle;");
		fromColumn.setAlignment(HorizontalAlignment.LEFT);
		fromColumn.setRenderer(new GridCellRenderer<BaseModelData>()
		{
			@Override
			public Object render(BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid)
			{
				try
				{
					if (model != null)
					{
						GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
						if (grtModel != null && grtModel.getSender() != null)
						{
							String value = grtModel.getSender();
							return value;
						}
					}
				}
				catch (Exception e)
				{
				}

				return "";
			}

		});

		ColumnConfig toColumn = new ColumnConfig("receiver", UIContext.Constants.restoreToColumn(), 80);
		toColumn.setMenuDisabled(true);
		toColumn.setHidden(true);  // hide this column by default
		toColumn.setStyle("vertical-align:middle;");
		toColumn.setAlignment(HorizontalAlignment.LEFT);
		toColumn.setRenderer(new GridCellRenderer<BaseModelData>()
		{

			@Override
			public Object render(BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getReceiver() != null)
					{
						String value = grtModel.getReceiver();
						return value;
					}
				}
				catch (Exception e)
				{
				}

				return "";
			}

		});
		
		ColumnConfig sentColumn = new ColumnConfig("senttime", UIContext.Constants.restoreSentColumn(), 100);
		sentColumn.setMenuDisabled(true);
		sentColumn.setStyle("vertical-align:middle;");
		sentColumn.setHidden(true); // hide this column by default
		sentColumn.setRenderer(new GridCellRenderer<BaseModelData>()
		{
			@Override
			public Object render(BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid)
			{
				try
				{
					if (model != null)
					{
						GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();

						if (grtModel != null && grtModel.getSentTime() != null)
						{
							Date sent = grtModel.getSentTime();
							return Utils.formatDateToServerTime(sent,
									grtModel.getSendTZOffset() != null?
											grtModel.getSendTZOffset() : 0);
						}								
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

		ColumnConfig receivedColumn = new ColumnConfig("receivedtime", UIContext.Constants.restoreReceivedColumn(), 100);
		receivedColumn.setMenuDisabled(true);
		receivedColumn.setStyle("vertical-align:middle;");
		receivedColumn.setRenderer(new GridCellRenderer<BaseModelData>()
		{
			@Override
			public Object render(BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid)
			{
				try
				{
					if (model != null)
					{
						GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();

						if (grtModel != null && grtModel.getReceivedTime() != null)
						{
							Date received = grtModel.getReceivedTime();
							return Utils.formatDateToServerTime(received, 
									grtModel.getReceivedTZOffset() != null ?
											grtModel.getReceivedTZOffset() : 0);
						}
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

		ColumnConfig sizeColumn = new ColumnConfig("size", UIContext.Constants.restoreSizeColumn(), 60);
		sizeColumn.setMenuDisabled(true);
		sizeColumn.setStyle("vertical-align:middle;");
		sizeColumn.setAlignment(HorizontalAlignment.RIGHT);
		sizeColumn.setRenderer(new GridCellRenderer<BaseModelData>()
		{

			@Override
			public Object render(BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid)
			{
				try
				{
					
					if (model != null)
					{
						GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
						
						if (grtModel != null && grtModel.getItemSize() != null)
						{
							String formattedValue = Utils.bytes2String(grtModel.getItemSize());
						return formattedValue;
					}
				}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});

		//Remove one column in Exchange, wanqi06 added
		List<ColumnConfig> lst = Arrays.asList(checkboxColumn, importanceColumn, attachmentColumn,
				fromColumn, toColumn, subjectColumn, sentColumn, receivedColumn, sizeColumn);
		
		ColumnModel cm = new ColumnModel(lst);

		return cm;
	}
	
	private ColumnModel createContactsColumnModel() {
		ColumnConfig checkboxColumn = createCheckBoxColumn();
		
		ColumnConfig iconColumn = new ColumnConfig("icon", "", 20);
		iconColumn.setMenuDisabled(true);
		iconColumn.setStyle("vertical-align:middle;");
		iconColumn.setAlignment(HorizontalAlignment.LEFT);
		iconColumn.setResizable(false);
		iconColumn.setFixed(true);
		iconColumn.setStyle("padding-left: 0px;");
//		iconColumn.setColumnStyleName("exchange_grt_header_of_importance");
		iconColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					LayoutContainer lc = new LayoutContainer();
					lc.setStyleAttribute("margin-left", "-3px");
					TableLayout layout = new TableLayout();
					layout.setCellPadding(0);
					layout.setCellSpacing(0);
					layout.setBorder(0);
					layout.setColumns(1);					
					lc.setLayout(layout);

					final Grid<ModelData> thisGrid = grid;
					final GridTreeNode node = (GridTreeNode) model;

					IconButton image = ExchangeGRTRecoveryPointsPanel.getNodeIcon(node);								
					
					if (image != null)
					{
						image.setWidth(16);
						image.setStyleAttribute("font-size", "0");
						
						image.addSelectionListener(new SelectionListener<IconButtonEvent>()
						{
							@Override
							public void componentSelected(IconButtonEvent ce)
							{
								if (node != null)
								{
									thisGrid.getSelectionModel().select(node, false);
								}
							}
						});
							
						lc.add(image);
					}						

					return lc;
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
					
		ColumnConfig fullNameColumn =  new ColumnConfig("fullName", UIContext.Constants.restoreFullNameColumn(), 100);
		fullNameColumn.setMenuDisabled(true);
		fullNameColumn.setStyle("vertical-align:middle;");
		fullNameColumn.setAlignment(HorizontalAlignment.LEFT);
		fullNameColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getSender() != null)
					{
						return getContactColumnValue(grtModel.getObjType(), 0, grtModel.getSender());												
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		
		ColumnConfig companyColumn =  new ColumnConfig("company", UIContext.Constants.restoreCompanyColumn(), 100);
		companyColumn.setMenuDisabled(true);
		companyColumn.setStyle("vertical-align:middle;");
		companyColumn.setAlignment(HorizontalAlignment.LEFT);
		companyColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getSender() != null)
					{
						return getContactColumnValue(grtModel.getObjType(), 2, grtModel.getSender());												
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		
		ColumnConfig jobTitleColumn =  new ColumnConfig("jobTitle", UIContext.Constants.restoreJobTitleColumn(), 100);
		jobTitleColumn.setMenuDisabled(true);
		jobTitleColumn.setStyle("vertical-align:middle;");
		jobTitleColumn.setAlignment(HorizontalAlignment.LEFT);
		jobTitleColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getSender() != null)
					{
						return getContactColumnValue(grtModel.getObjType(), 3, grtModel.getSender());												
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		
		ColumnConfig emailAddressColumn =  new ColumnConfig("emailAddress", UIContext.Constants.restoreEmailColumn(), 100);
		emailAddressColumn.setMenuDisabled(true);
		emailAddressColumn.setStyle("vertical-align:middle;");
		emailAddressColumn.setAlignment(HorizontalAlignment.LEFT);
		emailAddressColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getSender() != null)
					{
						return getContactColumnValue(grtModel.getObjType(), 4, grtModel.getSender());												
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		
		ColumnConfig mobileColumn =  new ColumnConfig("mobile", UIContext.Constants.restoreMobileColumn(), 100);
		mobileColumn.setMenuDisabled(true);
		mobileColumn.setStyle("vertical-align:middle;");
		mobileColumn.setAlignment(HorizontalAlignment.LEFT);
		mobileColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getSender() != null)
					{
						return getContactColumnValue(grtModel.getObjType(), 5, grtModel.getSender());												
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});

		return new ColumnModel(Arrays.asList(checkboxColumn, iconColumn,
				fullNameColumn, companyColumn, jobTitleColumn,
				emailAddressColumn, mobileColumn));
	}
	
	private ColumnModel createCalendarColumnModel() {
		ColumnConfig checkboxColumn = createCheckBoxColumn();
		ColumnConfig importanceColumn = createImportanceColumn();
		ColumnConfig attachmentColumn = createAttachmentColumn();
		
		ColumnConfig subjectColumn = new ColumnConfig("subject", UIContext.Constants.restoreSubjectColumn(), 100);
		subjectColumn.setMenuDisabled(true);
		subjectColumn.setStyle("vertical-align:middle;");
		subjectColumn.setAlignment(HorizontalAlignment.LEFT);
		subjectColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getObjName() != null)
					{
						String value = grtModel.getObjName();
						return value;
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		
		ColumnConfig startTimeColumn = new ColumnConfig("startTime", UIContext.Constants.restoreStartTimeColumn(), 100);
		startTimeColumn.setMenuDisabled(true);
		startTimeColumn.setStyle("vertical-align:middle;");
		startTimeColumn.setAlignment(HorizontalAlignment.LEFT);
		startTimeColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getSender() != null)
					{
						String value = getCalendarColumnValue(grtModel.getObjType(), 1, grtModel.getSender());
						// This date is a UTC time.
						Date date = convertStringToDate(value);
						long offset = calculateUTCOffset(date, grtModel.getSendTZOffset());
						return Utils.formatDateToServerTime(date, offset);																		
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		
		ColumnConfig endTimeColumn =  new ColumnConfig("endTime", UIContext.Constants.restoreEndTimeColumn(), 100);
		endTimeColumn.setMenuDisabled(true);
		endTimeColumn.setStyle("vertical-align:middle;");
		endTimeColumn.setAlignment(HorizontalAlignment.LEFT);
		endTimeColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getSender() != null)
					{
						String value = getCalendarColumnValue(grtModel.getObjType(), 2, grtModel.getSender());
						// This date is a UTC time.
						Date date = convertStringToDate(value);
						long offset = calculateUTCOffset(date, grtModel.getSendTZOffset());
						return Utils.formatDateToServerTime(date, offset);																							
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		
		ColumnConfig organizerColumn =  new ColumnConfig("organizer", UIContext.Constants.restoreOrganizerColumn(), 100);
		organizerColumn.setMenuDisabled(true);
		organizerColumn.setStyle("vertical-align:middle;");
		organizerColumn.setAlignment(HorizontalAlignment.LEFT);
		organizerColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getSender() != null)
					{
						return getCalendarColumnValue(grtModel.getObjType(), 0, grtModel.getSender());												
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});			
		
		ColumnConfig attendeesColumn =  new ColumnConfig("attendees", UIContext.Constants.restoreAttendeesColumn(), 100);
		attendeesColumn.setMenuDisabled(true);
		attendeesColumn.setStyle("vertical-align:middle;");
		attendeesColumn.setAlignment(HorizontalAlignment.LEFT);
		attendeesColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();
					if (grtModel != null && grtModel.getReceiver() != null)
					{
						return getCalendarColumnValue(grtModel.getObjType(), 3, grtModel.getSender());
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		
		return new ColumnModel(Arrays.asList(checkboxColumn, importanceColumn, 
				attachmentColumn, subjectColumn, startTimeColumn, 
				endTimeColumn, organizerColumn, attendeesColumn));
	}
	
	private ColumnConfig createCheckBoxColumn() {
		ColumnConfig checkboxColumn = new ColumnConfig("checkbox", "", 16);
		checkboxColumn.setMenuDisabled(true);
		checkboxColumn.setStyle("vertical-align:middle;");
		checkboxColumn.setAlignment(HorizontalAlignment.LEFT);
		checkboxColumn.setResizable(false);
		checkboxColumn.setFixed(true);
		checkboxColumn.setSortable(false);
		checkboxColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					LayoutContainer lc = new LayoutContainer();
					lc.setStyleAttribute("margin-left", "-5px");
					TableLayout layout = new TableLayout();
					layout.setCellPadding(0);
					layout.setCellSpacing(0);
					layout.setBorder(0);
					layout.setColumns(0);
					
					lc.setLayout(layout);

					final GridTreeNode node = (GridTreeNode) model;

					// check box
					if (mailboxExplorerWindow.getContext().isRestoreManager())
					{
						layout.setColumns(1);

						FlashCheckBox fcb = mailboxExplorerWindow.getContext().createFlashCheckbox(node, parentNode, -1);

						final FlashCheckBox ffcb = fcb;
						fcb.addSelectionListener(new SelectionListener<IconButtonEvent>()
						{
							@Override
							public void componentSelected(IconButtonEvent ce)
							{
								if (ffcb.isEnabled() == false)
									return;
								
								onNodeChecked(node, ffcb.getSelectedState());	
							}
						});

						lc.add(ffcb);
					}

					return lc;
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		return checkboxColumn;
	}
	
	private ColumnConfig createImportanceColumn() {
		ColumnConfig importanceColumn = new ColumnConfig("importance", "", 12);
		importanceColumn.setMenuDisabled(true);
		importanceColumn.setStyle("vertical-align:middle;");
		importanceColumn.setAlignment(HorizontalAlignment.LEFT);
		importanceColumn.setResizable(false);
		importanceColumn.setFixed(true);
		importanceColumn.setStyle("padding-left: 0px;");
		importanceColumn.setColumnStyleName("exchange_grt_header_of_importance");
		importanceColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					LayoutContainer lc = new LayoutContainer();
					lc.setStyleAttribute("margin-left", "-5px");
					TableLayout layout = new TableLayout();
					layout.setCellPadding(0);
					layout.setCellSpacing(0);
					layout.setBorder(0);
					layout.setColumns(1);					
					lc.setLayout(layout);

					final Grid<ModelData> thisGrid = grid;
					final GridTreeNode node = (GridTreeNode) model;

					Long flag = node.getGrtCatalogItemModel().getFlag();
					
					if (flag != null)
					{
						Long importance = flag.longValue() >> 1;
						
						IconButton image = null;
						
						switch (importance.intValue())
						{
						case 0:
							image = new IconButton("exchange_grt_low_importance");
							break;
						case 2:
							image = new IconButton("exchange_grt_high_importance");
							break;
						case 1:
							default:								
							break;
						};						
						
					if (image != null)
					{
						image.setWidth(10);
						image.setStyleAttribute("font-size", "0");
						
						image.addSelectionListener(new SelectionListener<IconButtonEvent>()
						{
							@Override
							public void componentSelected(IconButtonEvent ce)
							{
								if (node != null)
								{
									thisGrid.getSelectionModel().select(node, false);
								}
							}
						});
							
							lc.add(image);
						}
					}

					return lc;
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		return importanceColumn;
	}
	
	private ColumnConfig createAttachmentColumn() {
		ColumnConfig attachmentColumn = new ColumnConfig("attachment", "", 13);
		attachmentColumn.setMenuDisabled(true);
		attachmentColumn.setStyle("vertical-align:middle;");
		attachmentColumn.setAlignment(HorizontalAlignment.LEFT);
		attachmentColumn.setResizable(false);
		attachmentColumn.setFixed(true);
		attachmentColumn.setColumnStyleName("exchange_grt_header_of_attachment");
		attachmentColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					LayoutContainer lc = new LayoutContainer();
					lc.setStyleAttribute("margin-left", "-4px");
					lc.setStyleAttribute("margin-top", "-2px");
					TableLayout layout = new TableLayout();
					layout.setCellPadding(0);
					layout.setCellSpacing(0);
					layout.setBorder(0);
					layout.setColumns(1);					
					lc.setLayout(layout);

					final Grid<ModelData> thisGrid = grid;
					final GridTreeNode node = (GridTreeNode) model;

					Long flag = node.getGrtCatalogItemModel().getFlag();
					if ( flag != null && (flag.longValue() & 0x01) == 0x01) // has attachment
					{
					IconButton image = new IconButton("exchange_grt_attachment");
					if (image != null)
					{
						image.setWidth(10);
						image.setStyleAttribute("font-size", "0");
						image.addSelectionListener(new SelectionListener<IconButtonEvent>()
						{
							@Override
							public void componentSelected(IconButtonEvent ce)
							{
								if (node != null)
								{
									thisGrid.getSelectionModel().select(node, false);
								}
							}
						});
							
							lc.add(image);
						}					
					}

					return lc;
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});
		return attachmentColumn;
	}
	
	private String getContactColumnValue(Long type, int index, String values) {
		if (CatalogModelType.OT_GRT_EXCH_CONTACTS_ITEM != type.intValue()
				&& CatalogModelType.OT_GRT_EXCH_CONTACTS_GROUP != type.intValue()) {
			return "";
		}
		return getColumnValue(index, values);
	}
	
	private String getCalendarColumnValue(Long type, int index, String values) {
		if (CatalogModelType.OT_GRT_EXCH_CALENDAR_ITEM != type.intValue()) {
			return "";
		}
		return getColumnValue(index, values);
	}
	
	private Date convertStringToDate(String date) {
		int year = Integer.valueOf(date.substring(0, 4).trim());
		int month = Integer.valueOf(date.substring(4, 6).trim());
		int day = Integer.valueOf(date.substring(6, 8).trim());
		int hour = Integer.valueOf(date.substring(9, 11).trim());
		int minute = Integer.valueOf(date.substring(11, 13).trim());
		int second = Integer.valueOf(date.substring(13, 15).trim());
		
		return new Date(year - 1900, month - 1, day, hour, minute, second);
	}
	
	private long calculateUTCOffset(Date date, Long offset) {
		long result = offset != null ? offset.longValue() : 0;		
		return result - date.getTimezoneOffset() * 60 * 1000;		
	}
	
//	For calendar, the sender value is: organizer + start time + end time + attendees  (the concat token is “[{|}{|}]”)    
//	For contact, the sender value is: full name + company + job title + email address + mobile (the concat token is “[{|}{|}]”)    
	public static String getColumnValue(int index, String values) {
		if (null == values) {
			return "";
		}		
		String[] valueArray = values.split("\\[\\{\\|\\}\\{\\|\\}\\]");			
		return index > valueArray.length - 1 ? "" : valueArray[index]; 
	}
	
	private void configureGridColumn(GridTreeNode node) {
		if (null == node) {
			return;
		}
		if (parentNode != null && node.getGrtCatalogItemModel().getObjType().equals(parentNode.getGrtCatalogItemModel().getObjType())) {
			return;
		}				
		grid.setAutoExpandColumn(null);
		if (CatalogModelType.OT_GRT_EXCH_CALENDAR == node.getGrtCatalogItemModel().getObjType().intValue()) {
			grid.reconfigure(store, createCalendarColumnModel());
//			grid.setAutoExpandColumn("subject");
		} else if (CatalogModelType.OT_GRT_EXCH_CONTACTS == node.getGrtCatalogItemModel().getObjType().intValue()) {
			grid.reconfigure(store, createContactsColumnModel());
//			grid.setAutoExpandColumn("fullName");
		} else {			
			grid.reconfigure(store, createColumnModel());
			grid.setAutoExpandColumn("subject");
		}
		grid.getView().refresh(true);
	}

	public void setParentNode(GridTreeNode node)
	{			
		if (node == null)
		{
			clearPanel();
		}
		else
		{
			if (!node.equals(parentNode))
			{
				configureGridColumn(node);
				clearPanel();				
				// adjust the header

				if (grid != null && grid.getColumnModel() != null)
				{
					if ((node.getDisplayName() != null && node.getDisplayName().compareTo("Sent Items") == 0 || 
							(node.getType() != null && node.getType().intValue() == CatalogModelType.OT_GRT_EXCH_SENT_ITEMS)))
					{
						grid.getColumnModel().setHidden(grid.getColumnModel().getIndexById("sender"), true);
						grid.getColumnModel().setHidden(grid.getColumnModel().getIndexById("receiver"), false);
							
						grid.getColumnModel().setHidden(grid.getColumnModel().getIndexById("senttime"), false);
						grid.getColumnModel().setHidden(grid.getColumnModel().getIndexById("receivedtime"), true);
					}
					else
					{
						grid.getColumnModel().setHidden(grid.getColumnModel().getIndexById("sender"), false);
						grid.getColumnModel().setHidden(grid.getColumnModel().getIndexById("receiver"), true);
							
						grid.getColumnModel().setHidden(grid.getColumnModel().getIndexById("senttime"), true);
						grid.getColumnModel().setHidden(grid.getColumnModel().getIndexById("receivedtime"), false);
					}
				}
				
				parentNode = node;
				pagingBar.first();
			}
		}
	}
	
	public void clearPanel()
	{		
		if (parentNode != null && mailboxExplorerWindow != null && mailboxExplorerWindow.getContext() != null)
		{
			// It may clean the sub folders together, it would be a problem.
			// mailboxExplorerWindow.getContext().cleanRedandant(parentNode);
		}
		
		// close the handle
		if (contextModel != null)
		{
			contextModel = null;
		}
		
		if (store != null)
		{
			store.removeAll();
		}
		
		parentNode = null;
		
		if (pagingBar != null)
		{
			pagingBar.clear();
		}
		
		if (loader != null)
		{
			loader.setOffset(0);
		}
		
		updateSelectedCount();
	}
	
	protected void refresh()
	{

		// uncheck all mails
		Iterator<GridTreeNode> iterator = mailboxExplorerWindow.getContext().getChildrenIterator(parentNode);

		while (iterator != null && iterator.hasNext())
		{
			GridTreeNode node = iterator.next();

			// if it is mail
			if (CatalogModelType.exchSubItemType_messages.contains(node.getType()))
			{
				FlashCheckBox fcb = mailboxExplorerWindow.getContext().getCheckbox(node);
				if (fcb != null)
				{
					fcb.setSelectedState(FlashCheckBox.NONE);
				}
			}
		}

		// update parent status
		onNodeChecked(null, -1);
			
		
		GridTreeNode parent = parentNode;
		clearPanel();
		setParentNode(parent);
	}
	
	// called when a folder is checked
	public void onParentChecked(GridTreeNode node, int selectState)
	{
		if (node != null && node.equals(parentNode))
		{
			Iterator<GridTreeNode> iterator = mailboxExplorerWindow.getContext().getChildrenIterator(parentNode);
			
			while (iterator != null && iterator.hasNext())
			{				
				FlashCheckBox fcb = mailboxExplorerWindow.getContext().getCheckbox(iterator.next());
				if (fcb != null)
				{
					fcb.setSelectedState(selectState);			
				}				
			}
		}
		
		updateSelectedCount();	
	}
	
	// called when a node is checked
	protected void onNodeChecked(GridTreeNode node, int selectState)
	{		
		// calculate the parent state
		int parentState = mailboxExplorerWindow.getContext().calcParentNodeState(parentNode, -1/*parentNodeInitState*/);	
		
		if (parentState != -1)
		{
			FlashCheckBox fcb = mailboxExplorerWindow.getContext().getCheckbox(parentNode);
			if (fcb != null && fcb.getSelectedState() != parentState)
			{
				mailboxExplorerWindow.checkFolderAndParent(parentNode, parentState);
			}		
		}		
		
		updateSelectedCount();			
	}	
	
	// update the selected number to the bottom of the toolbar
	public void updateSelectedCount()
	{
		int nSelected = 0;
		int nTotalCount = 0;
//		if (loader != null)
//		{
//			nTotalCount = loader.getTotalCount();
//		}
		
		if (contextModel != null)
		{
			nTotalCount = (int) contextModel.getTotalWithoutFilter();
		}
		
		FlashCheckBox fcb = mailboxExplorerWindow.getContext().getCheckbox(parentNode);
		if (fcb != null)
		{
			switch (fcb.getSelectedState())
			{
			case FlashCheckBox.FULL:
				nSelected = nTotalCount;
				break;
			case FlashCheckBox.PARTIAL:
				nSelected = mailboxExplorerWindow.getContext().getSelectedChildrenCount(parentNode);				
				break;
			case FlashCheckBox.NONE:
				nSelected = 0;
				break;
			default:
				nSelected = 0;
				break;
			}		
		}
		
		if (labelSelectedMails != null)
		{
			if (nTotalCount == 0 || nSelected == 0)
			{
				labelSelectedMails.setHtml("");
				labelSelectedMails.removeToolTip();
				labelSelectedMails.removeStyleName("labelSelectedNumber");
			}			
			else
			{
				labelSelectedMails.setHtml("  (" + nSelected + ")");
				ToolTipConfig config = new ToolTipConfig();
				config.setText(UIContext.Messages.restoreExchangeGRTSelectedMailsTooltip(nSelected));
				config.setShowDelay(50);
				labelSelectedMails.setToolTip(config);
				labelSelectedMails.addStyleName("labelSelectedNumber");
			}
		}
	}	
}
