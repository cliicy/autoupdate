package com.ca.arcflash.ui.client.restore.mailboxexplorer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.GRTBrowsingContextModel;
import com.ca.arcflash.ui.client.model.GRTPagingLoadResult;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.restore.ExchangeGRTRecoveryPointsPanel;
import com.ca.arcflash.ui.client.restore.PagingContext;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
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
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LiveGridEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.LiveGridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LiveToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

public class MailboxListPanel extends LayoutContainer
{
	public class LiveGridViewEx  extends LiveGridView
	{
		// reset the variables of live view, to reuse the livegridview for different edb
		public void reset()
		{
			// remove ui store
			if (this.liveStore != null)
			{
				this.liveStore.removeAll();
			}

			liveStoreOffset = 0;
			// totalCount = 0;
			viewIndex = 0;

			scrollToTop();
		}

//		@Override
//		protected int getVisibleRowCount()
//		{
//			int rh = getCalculatedRowHeight();
//			int visibleHeight = getLiveScrollerHeight();
//			// override this method because it may leave a blank row at the bottom, change floor() to ceil()
//			return (int) ((visibleHeight < 1) ? 0 : Math.ceil((double) visibleHeight / rh));
//		}

		@Override
		protected void updateRows(int newIndex, boolean reload)
		{
			// liuwe05 2011-3-9 fix Issue: 20112555    Title: MBX DUPLICATED IN RESTOR UI
			// It's a bug of LiveGridView in GXT 2.1.3: The first/last item will be added to the view again and again when scrolling
			// so adjust the newIndex in advance
			int rowCount = getVisibleRowCount();  			  
		    newIndex = Math.min(newIndex, Math.max(0, totalCount - rowCount));  
		    
			super.updateRows(newIndex, reload);
		}

			
	}
	
	private MailboxExplorerPanel mailboxExplorerWindow;
	private Grid<GridTreeNode> grid;
	private ListStore<GridTreeNode> store;
	private PagingLoader<PagingLoadResult<ModelData>> loader;
	private LiveGridViewEx liveView;
	private GridTreeNode parentNode;
	private LoginServiceAsync service = GWT.create(LoginService.class);
	private static final int PAGESIZE = PagingContext.DEFAULTPAGESIZE;
	
	private boolean initialized = false;

	private GridTreeNode selectedNode;
	
	private TriggerField<String> filterField;
	private GRTBrowsingContextModel contextModel;
	private CheckBox checkBoxAll;
	private LiveToolItem liveToolItem;
	private LabelToolItem labelSelectedMails;

	public MailboxListPanel(MailboxExplorerPanel window)
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
						mailboxExplorerWindow.updateNextButtonStatus(0);
						
						fcb.onFailure(caught);
						
						// select the first data at the first load
						if (!initialized)
						{
							initialized = true;
							grid.unmask();
						}
					}

					@Override
					public void onSuccess(GRTPagingLoadResult result)
					{
						if (contextModel != null && result != null && result.getPagingLoadResult() != null)
						{
							contextModel.setTotal(result.getPagingLoadResult().getTotalLength());
							contextModel.setTotalWithoutFilter(result.getTotalWithoutFilter());
							mailboxExplorerWindow.updateNextButtonStatus(Integer.valueOf((int)result.getTotalWithoutFilter()));
						}
						else							
							mailboxExplorerWindow.updateNextButtonStatus(0);
						
						BasePagingLoadResult<GridTreeNode> pagingLoadResult = result.getPagingLoadResult();
						if (result != null && pagingLoadResult != null && pagingLoadResult.getData() != null)
						{
							for (int i = pagingLoadResult.getData().size() - 1; i >= 0; i--)
							{
								// set refer node
								GridTreeNode node = pagingLoadResult.getData().get(i);
								node.getReferNode().add(parentNode); // edb node
								node.getReferNode().add(node);       // mailbox node
							}
							
						}						
						
						fcb.onSuccess(pagingLoadResult);
						
						// select the first data at the first load
						if (!initialized)
						{
							initialized = true;
							grid.getSelectionModel().select(0, false);	
							grid.unmask();
						}
						
						updateSelectedCount();
					}
				};

				if (!initialized)
				{
					grid.mask(GXT.MESSAGES.loadMask_msg());
				}
				
				if (contextModel == null)
				{
					contextModel = new GRTBrowsingContextModel();
					contextModel.setSearchKeyword(filterField.getValue());					
				}	

				// sort the mailbox
				plc.setSortField("subject");
				plc.setSortDir(SortDir.ASC);
							
				service.browseGRTCatalog(parentNode, plc, contextModel, cb);				
			}
		};

		// loader
		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
		loader.setRemoteSort(true);

		ColumnModel cm = createColumnModel();

		store = new ListStore<GridTreeNode>(loader);

		grid = new Grid<GridTreeNode>(store, cm);
		grid.ensureDebugId("fa0da35e-3340-4d20-8445-2d44bd235352");

		// select listener
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<GridTreeNode>()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent<GridTreeNode> se)
			{
				if (se.getSelectedItem() != null)
				{
					GridTreeNode node = se.getSelectedItem();
					selectedNode = node; // remember the selected node
					onNodeSelected(node);
				}
			}
		});

		grid.setAutoExpandColumn("name");
		grid.setBorders(true);  
		grid.setStripeRows(true);
		grid.setHideHeaders(true);	
		
		// use live view to avoid get all data together
		liveView = new LiveGridViewEx();

	    liveView.setRowHeight(23);
	    liveView.setLoadDelay(500);
	    liveView.setCacheSize(PAGESIZE);
	    
	    // select the selected item after update, otherwise the selection in liveView will lost after hide and show
		liveView.addListener(Events.LiveGridViewUpdate, new Listener<LiveGridEvent<GridTreeNode>>()
		{
			@Override
			public void handleEvent(LiveGridEvent<GridTreeNode> be)
			{
				// TODO Auto-generated method stub
				if (selectedNode != null && grid != null)
				{
					grid.getSelectionModel().select(selectedNode, false);
				}

			}

		});
	    grid.setView(liveView);

		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.setCollapsible(false);
//		panel.setScrollMode(Scroll.AUTOX);
		panel.setBorders(false);
		panel.setHeadingHtml("Mailbox");

		panel.add(grid);

		// filter
		filterField = new TriggerField<String>()
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
		filterField.ensureDebugId("89c7b4d8-50e5-486c-80e7-03ffa9574fe5");
		//filterField.setTriggerStyle("x-form-filter-trigger"); // use a filter icon
		filterField.setTriggerStyle("x-form-search-trigger"); // use a search icon
		filterField.setWidth(125);
		// liuwe05 2011-02-17 fix Issue: 20062702    Title: MULTI MAILBOX RESTORE UI
		// add a check box for user to select all mailboxes
		checkBoxAll = new CheckBox()
		{

			@Override
			protected void onClick(ComponentEvent ce)
			{
				super.onClick(ce);
				
				int edbState = checkBoxAll.getValue() ? FlashCheckBox.FULL : FlashCheckBox.NONE;
				
				// update the check box of parent EDB
				mailboxExplorerWindow.getContext().updateParentSelection(edbState);
				
				// update the check box of mailbox/folder/mail
				onParentChecked(parentNode, edbState);					
			}
			
		};
		checkBoxAll.ensureDebugId("7DD44B26-6CA4-4596-998B-97D9C87B6CA7");
		
		Utils.addToolTip(checkBoxAll, UIContext.Constants.restoreSelectAllMailboxesTooltip());
		
		ToolBar toolBar = new ToolBar();
		toolBar.setBorders(true);
		toolBar.add(checkBoxAll);
		toolBar.add(new SeparatorToolItem());
//		toolBar.add(new FillToolItem());
		toolBar.add(new LabelToolItem(UIContext.Constants.restoreFilterMailbox()));
		toolBar.add(filterField);
		
		panel.setTopComponent(toolBar);
		
		// live grid tool
		liveToolItem = new LiveToolItem();
		liveToolItem.bindGrid(grid);
		
		ToolBar toolBar2 = new ToolBar();
//		toolBar2.add(new FillToolItem());
		toolBar2.add(liveToolItem);
		
		// to display the number of selected mails
		toolBar2.add(new SeparatorToolItem()); 	
		labelSelectedMails = new LabelToolItem("");	
		toolBar2.add(labelSelectedMails);
		
		panel.setBottomComponent(toolBar2);

		this.add(panel);
		
		if (mailboxExplorerWindow != null && mailboxExplorerWindow.getContext() != null)
		{
			setParentNode(mailboxExplorerWindow.getContext().getEdbNode());
		}				
	}

	private ColumnModel createColumnModel()
	{
		ColumnConfig nameColumn = new ColumnConfig("name", UIContext.Constants.restoreNameColumn(), 50);
		nameColumn.setAlignment(HorizontalAlignment.LEFT);
		nameColumn.setMenuDisabled(true);
		nameColumn.setStyle("vertical-align:middle;");
		nameColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{

				LayoutContainer lc = new LayoutContainer();
				TableLayout layout = new TableLayout();
				layout.setColumns(3);
				lc.setLayout(layout);
				
				final Grid<ModelData> thisGrid = grid;

				final GridTreeNode node = (GridTreeNode) model;

				// check box
				{										
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

				// icon
				IconButton image = ExchangeGRTRecoveryPointsPanel.getNodeIcon(node);
				if (image != null)
				{
					lc.add(image);

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
				}

				// text
				Label la = new Label();	
				if (la != null)
				{
					la.setStyleName("popupFileText");
					la.setText(node.getDisplayName());	
					la.setTitle(node.getDisplayName());
					la.addClickHandler(new ClickHandler()
					{
						@Override
						public void onClick(ClickEvent event)
						{
							if (node != null)
							{
								thisGrid.getSelectionModel().select(node, false);
							}
						}
					});
					
					lc.add(la);
				}				
				
				return lc;
			}		
		});

		List<ColumnConfig> lst = Arrays.asList(nameColumn);
		for (ColumnConfig cfg : lst)
		{
			cfg.setSortable(false);
		}
		ColumnModel cm = new ColumnModel(lst);

		return cm;
	}
	
	public void setParentNode(GridTreeNode node)
	{
		if (node == null)
		{
			clearPanel();			
			
			if (liveView != null)
			{
				liveView.refresh();
			}
		}
		else
		{
			if (!node.equals(parentNode))
			{
				clearPanel();
				
				parentNode = node;				
				
				if (liveView != null && grid.isRendered())
				{
					liveView.refresh();
				}
				
				// update the checkbox status if edb status changed
				int edbState = mailboxExplorerWindow.getContext().getEdbCheckBox().getSelectedState();
				checkBoxAll.setValue(edbState == FlashCheckBox.FULL);
			}
		}
	}
	
	public void clearPanel()
	{
		// clear the buffer
		if (parentNode != null)
		{
			//mailboxExplorerWindow.getContext().cleanRedandant(parentNode);
		}
		
		// close the handle
		if (contextModel != null)
		{
			contextModel = null;
		}
		
		// clear the member fields
		parentNode = null;
		selectedNode = null;		
		
		if (loader != null)
		{
			loader.setOffset(0);
		}
		
		// reset the view
		if (liveView != null && grid.isRendered())
		{
			liveView.reset();
		}		
		
		initialized = false;		
		onNodeSelected(null); // update other panels
		
		checkBoxAll.setValue(false);
		
		updateSelectedCount();
	}
	
	public boolean isCleared()
	{
		boolean isCleared = false;
		
		if (parentNode == null)
		{
			isCleared = true;
		}
		
		return isCleared;
	}
	
	public void refresh()
	{
		// clear the selection if using search, update the check box of parent EDB
		mailboxExplorerWindow.getContext().updateParentSelection(FlashCheckBox.NONE);
		// clear the cached nodes since they might be filtered out
		mailboxExplorerWindow.getContext().cleanRedandant(parentNode);
		
		GridTreeNode parent = parentNode;
		
		clearPanel();		
		
		setParentNode(parent);
	}

	protected void onNodeSelected(GridTreeNode node)
	{
		mailboxExplorerWindow.onSelectedMailbox(node);
	}
	
	protected void onNodeChecked(GridTreeNode node, int selectState)
	{
		mailboxExplorerWindow.onCheckedMailbox(node, selectState);
		
		mailboxExplorerWindow.getContext().updateParentSelection();		
		
		// not full edb restore now
		checkBoxAll.setValue(false);
		
		updateSelectedCount();	
	}	
	
	// called when an edb is checked
	public void onParentChecked(GridTreeNode node, int selectState)
	{
		if (node != null && node.equals(parentNode) && selectState != FlashCheckBox.PARTIAL)
		{
			Iterator<GridTreeNode> iterator = mailboxExplorerWindow.getContext().getChildrenIterator(parentNode);
			
			while (iterator != null && iterator.hasNext())
			{				
				GridTreeNode curNode = iterator.next();
				FlashCheckBox fcb = mailboxExplorerWindow.getContext().getCheckbox(curNode);
				if (fcb != null)
				{
					fcb.setSelectedState(selectState);			
				}
				
				// update folder
				if (curNode.equals(grid.getSelectionModel().getSelectedItem()))
				{
					mailboxExplorerWindow.onCheckedMailbox(curNode, selectState);
				}		
			}
		}
		
		checkBoxAll.setValue(selectState == FlashCheckBox.FULL);		
		
		updateSelectedCount();	
	}
	
	// called when a folder is checked
	public void checkNodeAndParent(GridTreeNode node, int selectState)
	{
		if (node != null)
		{
			FlashCheckBox fcb =  mailboxExplorerWindow.getContext().getCheckbox(node);
			if (fcb != null)
			{
				fcb.setSelectedState(selectState);				
			}
			
			mailboxExplorerWindow.getContext().updateParentSelection();
			
			// not full edb restore now
			checkBoxAll.setValue(false);
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
				config.setText(UIContext.Messages.restoreExchangeGRTSelectedMailboxesTooltip(nSelected));
				config.setShowDelay(50);
				labelSelectedMails.setToolTip(config);
				labelSelectedMails.addStyleName("labelSelectedNumber");
			}
		}
	}	
}
