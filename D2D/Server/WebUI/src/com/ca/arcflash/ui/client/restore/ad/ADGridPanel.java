package com.ca.arcflash.ui.client.restore.ad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.restore.PagingContext;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ADGridPanel extends LayoutContainer {
	private static final int PAGESIZE = PagingContext.DEFAULTPAGESIZE;
	private ActiveDirectoryExplorerPanel parentPanel;
	private LabelField labelPath;
	private BasePagingLoader<PagingLoadResult<GridTreeNode>> loader;
	private ListStore<GridTreeNode> store;
	private TriggerField<String> searchField;
	protected Grid<GridTreeNode> grid;
	private PagingToolBar pagingBar;
	private LabelToolItem labelSelectedNodes;
	private GridTreeNode parentNode;
	private ContentPanel gridPanel;
	protected ADAttributePanel attributePanel;
	protected HashMap<Integer, GridTreeNode> table = new HashMap<Integer, GridTreeNode>();
	protected PagingLoadResult<GridTreeNode> pageInfo;

	public ADGridPanel(ActiveDirectoryExplorerPanel activeDirectoryExplorerPanel) {
		this.parentPanel = activeDirectoryExplorerPanel;
		setLayout(new BorderLayout()); 
		labelPath = new LabelField();
		labelPath.setBorders(true);
		labelPath.setStyleName("x-panel-header");
		labelPath.setStyleAttribute("white-space", "nowrap");
		labelPath.setStyleAttribute("overflow", "hidden");
		labelPath.setStyleAttribute("text-overflow","ellipsis"); 
		this.add(labelPath,new BorderLayoutData(LayoutRegion.NORTH, 25));
		this.add(renderMainPanel(),new BorderLayoutData(LayoutRegion.CENTER));
	}

	private LayoutContainer renderMainPanel() {
		LayoutContainer lc = new LayoutContainer();
		lc.setLayout(new BorderLayout());
		lc.setBorders(true);
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
//		centerData.setMargins(new Margins(0));
		lc.add(renderCenterNodePanel(),centerData);
		
		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 200);  
		eastData.setCollapsible(false);
		eastData.setFloatable(true);
		eastData.setHideCollapseTool(false);
		eastData.setSplit(true);
		eastData.setMargins(new Margins(0, 0, 0, 0));
		attributePanel = new ADAttributePanel(parentPanel);
		lc.add(attributePanel,eastData);
		return lc;
	}

	private LayoutContainer renderCenterNodePanel() {
		gridPanel = new ContentPanel();
		gridPanel.setHeaderVisible(false);
		gridPanel.setLayout(new FitLayout());
		gridPanel.setCollapsible(false);
		gridPanel.setScrollMode(Scroll.AUTOY);
//		panel.setScrollMode(Scroll.AUTO);
		gridPanel.setBorders(false);
		
		createPagingGrid();
		gridPanel.add(grid);

		ToolBar toolBar = createSearchBar();
		gridPanel.setTopComponent(toolBar);

		// paging toolbar
		createPagingBar();
		gridPanel.setBottomComponent(pagingBar);
		
		return gridPanel;
	}

	private void createPagingBar() {
		pagingBar = new PagingToolBar(PAGESIZE) {
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				// this.pageText.setWidth("68px");
				this.pageText.addKeyPressHandler(new KeyPressHandler() {
					@Override
					public void onKeyPress(KeyPressEvent event) {
						char key = event.getCharCode();
						if (event.isControlKeyDown()
								|| key == KeyCodes.KEY_ENTER
								|| key == KeyCodes.KEY_BACKSPACE
								|| key == KeyCodes.KEY_DELETE) {
							return;
						}

						if (!Character.isDigit(key)) {
							pageText.cancelKey();
						}
					}

				});

				first.ensureDebugId("cca4452a-9530-44de-abea-08bf95cd76c2");
				last.ensureDebugId("fde40a0a-4439-4f03-8901-67d3277bacd1");
				prev.ensureDebugId("faf2d315-d9e5-4fb5-8dfc-3169a08cb50b");
				next.ensureDebugId("c0bb8e89-ffed-4ef3-80c4-1684aa4f3ff6");
				refresh.ensureDebugId("4ceec401-c18f-42de-9d25-1b5eae59b930");
				displayText.hide();
			}
		};
		pagingBar.ensureDebugId("64cae02f-1db4-4655-ba7a-e8d31791b551");
		pagingBar.bind(loader);
		pagingBar.setBorders(false);
		
//		pagingBar.add(new SeparatorToolItem());

		// to display the number of selected nodes
		labelSelectedNodes = new LabelToolItem("");
		pagingBar.add(labelSelectedNodes);
	}

	private ToolBar createSearchBar() {
		// search bar
		searchField = new TriggerField<String>() {
			@Override
			protected void onTriggerClick(ComponentEvent ce) {
				super.onTriggerClick(ce);
				refreshPagingGrid();
			}

			@Override
			protected void onKeyDown(FieldEvent fe) {
				if (fe.getKeyCode() == KeyCodes.KEY_ENTER) {
					refreshPagingGrid();
				}
				super.onKeyDown(fe);
			}

		};
		searchField.ensureDebugId("6fa685d1-2fec-473d-a0b7-598147ebea17");
		searchField.setTriggerStyle("x-form-search-trigger"); // use a search
		searchField.setWidth(150);

		ToolBar toolBar = new ToolBar();
//		toolBar.setAlignment(HorizontalAlignment.LEFT);
		toolBar.setBorders(false);
		toolBar.add(new FillToolItem());
//		toolBar.add(new LabelToolItem(UIContext.Constants
//				.restoreSearchSubject()));
		toolBar.add(searchField);
		
		return toolBar;
	}
	
	private void createPagingGrid() {
		loader = createPagingLoader();
		store = new ListStore<GridTreeNode>(loader);
//		store.setSortField("displayName");
		ColumnModel cm = this.createColumnModel();
		grid = new Grid<GridTreeNode>(store, cm);

		grid.ensureDebugId("14277944-b7dc-409e-8823-c95fc13e4acd");

		// set id for automation
		grid.addListener(Events.ViewReady,
				new Listener<GridEvent<GridTreeNode>>() {

					@Override
					public void handleEvent(GridEvent<GridTreeNode> be) {
						if (be != null && be.getGrid() != null) {
							Grid<GridTreeNode> g = be.getGrid();
							if (g.getView() != null) {
								if (g.getView().getHeader() != null) {
									g.getView()
											.getHeader()
											.ensureDebugId(
													"dba38365-2848-4d55-a825-92a96f7f9131");
								}

								if (g.getView().getBody() != null) {
									g.getView()
											.getBody()
											.setId("83045ffc-2685-47aa-b239-5b5fe5856168");
								}
							}

						}
					}
				});

//		grid.addListener(Events.CellDoubleClick,
//				new Listener<GridEvent<GridTreeNode>>() {
//					@Override
//					public void handleEvent(GridEvent<GridTreeNode> be) {
//					}
//				});
		
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<GridTreeNode>(){

			@Override
			public void selectionChanged(SelectionChangedEvent<GridTreeNode> se) {
				final GridTreeNode node = se.getSelectedItem();
				if(node==null){
					return;
				}else{
					parentPanel.treePanel.tree.getSelectionModel().deselectAll();
					attributePanel.refresh(node);
				}			
				
			}
	    	
	    });

//		grid.setLoadMask(true);
		grid.setAutoExpandColumn("displayName");
		grid.setBorders(false);
		grid.setView(new ADGridView());
		grid.getView().setAutoFill(true);
	}

	private ColumnModel createColumnModel() {
		ColumnConfig name = new ColumnConfig("displayName", UIContext.Constants.restoreADObjectColumn(), 150);
		name.setMenuDisabled(true);
		name.setRenderer(new GridCellRenderer<GridTreeNode>(){
				@Override
				public Object render(final GridTreeNode node, String property, ColumnData config, int rowIndex, int colIndex,
						ListStore<GridTreeNode> store, final Grid<GridTreeNode> grid){
					ADGridCell cell = new ADGridCell(node);
					final FlashCheckBox fcb = cell.getFlashCheckBox();
					fcb.addSelectionListener(new SelectionListener<IconButtonEvent>() {
						@Override
						public void componentSelected(IconButtonEvent ce) {
							if (fcb.isEnabled() == false)
								return;
								node.setSelectionType(fcb.getSelectedState());
								updateNode(node);
								grid.getSelectionModel().select(node, false);
							}
						});
					cell.addClickListener(new Listener<BaseEvent>(){

						@Override
						public void handleEvent(BaseEvent be) {
							if (grid != null ) {
								grid.getSelectionModel().select(node, false);
							}
						}});
					return cell;
			}

		});

		return new ColumnModel(Arrays.asList( name));
	}

	private BasePagingLoader<PagingLoadResult<GridTreeNode>> createPagingLoader() {
		RpcProxy<PagingLoadResult<GridTreeNode>> proxy = new RpcProxy<PagingLoadResult<GridTreeNode>>()
				{

					@Override
					protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<GridTreeNode>> callback) {
						if (!(loadConfig instanceof PagingLoadConfig)) {
							return;
						}
						if(parentNode==null){
							return;
						}
						AsyncCallback<PagingLoadResult<GridTreeNode>> adCallback = new AsyncCallback<PagingLoadResult<GridTreeNode>>(){

							@Override
							public void onFailure(Throwable caught) {
								gridPanel.unmask();
								callback.onFailure(caught);
							}

							@Override
							public void onSuccess(PagingLoadResult<GridTreeNode> result) {
								gridPanel.unmask();
								if(result!=null&&result.getData()!=null){
									for(GridTreeNode node : result.getData()){
										loadStateIfNeed(node);
										addOrUpdateCache(node);
									}
								}
								callback.onSuccess(result);
							}
							
						};
						gridPanel.mask(UIContext.Constants.loadingIndicatorText());
						PagingLoadConfig pagingLoadConfig = (PagingLoadConfig) loadConfig;
						parentPanel.loadADPagingNodes(parentNode, pagingLoadConfig, searchField.getValue(), adCallback);
					}
				};

		// loader
		BasePagingLoader<PagingLoadResult<GridTreeNode>> loader = new BasePagingLoader<PagingLoadResult<GridTreeNode>>(proxy);
//		loader.setRemoteSort(true);
		LoadListener loadListener = new LoadListener() {
			 public void loaderLoad(LoadEvent le) {
				    pageInfo = le.getData();
				    int total = pageInfo.getTotalLength();
				    parentNode.setChildrenCount(Long.valueOf(total));
				    //refreshLabelSelectedNodes();
				    refreshLabelSelectedNodes4CurrentPage();
			 }
		};
		loader.addLoadListener(loadListener);
		return loader;
	}

	public void populateGrid(GridTreeNode node) {
		this.parentNode=node;
		this.labelPath.setValue(UIContext.Messages.ADNodePath(node.getPath()));
		Utils.addToolTip(labelPath, node.getPath());
		refreshPagingGrid();
		this.attributePanel.refresh(node);
	}


	private void refreshPagingGrid() {
		if(parentNode == null){
			return;
		}
		pagingBar.first();
		pagingBar.setEnabled(true);
	}
	
	private void loadStateIfNeed(final GridTreeNode node) {
		if(node.getSelectionType()!=null && node.getSelectionType()==GridTreeNode.SELECTION_TYPE_PARTIAL){
			GridTreeNode exist = parentPanel.treePanel.findModel(node.getId());
			if (exist == null) {
				exist = table.get(node.getId());
			}
			if(exist!=null){
				node.setSelectionType(exist.getSelectionType());
			} else {
				node.setSelectionType(GridTreeNode.SELECTION_TYPE_NONE);
			}
		}
	}
	
	private void addOrUpdateCache(GridTreeNode node) {
		table.remove(node.getId());
		table.put(node.getId(), node);
	}
	
	/*private void refreshLabelSelectedNodes() {
		int total = parentNode.getChildrenCount().intValue();
		Iterator<GridTreeNode> i = table.values().iterator();
		int fullyCount=0;
		while(i.hasNext()){
			GridTreeNode node = i.next();
			if(node.getParentID()==parentNode.getId()&&node.getSelectionType()==GridTreeNode.SELECTION_TYPE_FULL){
				fullyCount++;
			}
		}
		updateLabelSelectedNodes(fullyCount, total);
	}*/
	
	private void refreshLabelSelectedNodes4CurrentPage() {
		int total = pageInfo.getData().size();
		int fullyCount=0;
		for(GridTreeNode node : pageInfo.getData()){
			GridTreeNode exist = table.get(node.getId());
			node.setSelectionType(exist.getSelectionType());
			if(node.getParentID()==parentNode.getId()&&node.getSelectionType()==GridTreeNode.SELECTION_TYPE_FULL){
				fullyCount++;
			}
		}
		updateLabelSelectedNodes(fullyCount, total);
	}
	
	private void updateLabelSelectedNodes(int nSelected, int nTotal){
		if (nTotal == 0) {
			labelSelectedNodes.setHtml("");
			labelSelectedNodes.removeToolTip();
			//labelSelectedAttributes.removeStyleName("labelSelectedNumber");
		} else {
			labelSelectedNodes.setHtml(UIContext.Messages.selectedNumberAndTotalNumber(nSelected, nTotal));
			ToolTipConfig config = new ToolTipConfig();
			config.setText(UIContext.Messages.ADNodeFullySelectedTooltip(nSelected));
			config.setShowDelay(50);
			labelSelectedNodes.setToolTip(config);
			//labelSelectedAttributes.addStyleName("labelSelectedNumber");
		}
	}

	public void updateNode(GridTreeNode node) {
		Integer state = node.getSelectionType();
		GridTreeNode model = findModel(node.getId());
		if(model == null)
			return;
		model.setSelectionType(state);
		addOrUpdateCache(model);
		grid.getView().refresh(false);
		//refreshLabelSelectedNodes();
		refreshLabelSelectedNodes4CurrentPage();
		boolean flag = parentPanel.treePanel.updateNode(model);
		if(!flag){//model is not in tree panel
			List<GridTreeNode> list = getChildren(parentNode);
			int parent_state=GridTreeNode.SELECTION_TYPE_NONE;
			for(GridTreeNode item : list){
				if(item.getSelectionType()!= GridTreeNode.SELECTION_TYPE_NONE){
					parent_state = GridTreeNode.SELECTION_TYPE_PARTIAL;
					break;
				}
			}
			// if none, need to check attribute of parent node.
			if(parent_state == GridTreeNode.SELECTION_TYPE_NONE){
				List<GridTreeNode> childAttributes = attributePanel.getChildren(parentNode);
				for(GridTreeNode attr : childAttributes){
					if (attr.getSelectionType() == GridTreeNode.SELECTION_TYPE_FULL){
						parent_state = GridTreeNode.SELECTION_TYPE_PARTIAL;
						break;
					}
				}
			}
			parentNode.setSelectionType(parent_state);
			parentPanel.treePanel.updateNode(parentNode);
		}
	}
	
	public GridTreeNode findModel(int id) {
		List<GridTreeNode> list = store.getModels();
		for(GridTreeNode model : list){
			if(model.getId()==id){
				GridTreeNode exist = table.get(id);
				model.setSelectionType(exist.getSelectionType());
				return model;
			}
		}
		return null;
	}
	
	public List<GridTreeNode> getChildren(GridTreeNode parent) {
		List<GridTreeNode> childs = new ArrayList<GridTreeNode>();
		Iterator<GridTreeNode> i = table.values().iterator();
		while(i.hasNext()){
			GridTreeNode node = i.next();
			if(node.getParentID()==parent.getId()){
				childs.add(node);
			}
		}
		return childs;
	}

	public List<GridTreeNode> getAllSelectedNodes() {
		List<GridTreeNode> selectedList= new ArrayList<GridTreeNode>();
		Iterator<GridTreeNode> i = table.values().iterator();
		while(i.hasNext()){
			GridTreeNode node = i.next();
			Integer state = node.getSelectionType();
			if(state == GridTreeNode.SELECTION_TYPE_FULL){
				selectedList.add(node);
			}else if(state == GridTreeNode.SELECTION_TYPE_PARTIAL){
				selectedList.add(node);
			}
		}
		List<GridTreeNode> attrs = attributePanel.getFullySelectedNodes();
		selectedList.addAll(attrs);
		return selectedList;
	}
	
	@Deprecated
	public List<GridTreeNode> getSelectedNodes() {
		List<GridTreeNode> selectedList= new ArrayList<GridTreeNode>();
		List<GridTreeNode> attrs = attributePanel.getFullySelectedNodes();
		Iterator<GridTreeNode> i = table.values().iterator();
		while(i.hasNext()){
			GridTreeNode node = i.next();
			Integer state = node.getSelectionType();
			if(state == GridTreeNode.SELECTION_TYPE_FULL){
				selectedList.add(node);
				removeAttribute(node, attrs);
			}else if(state == GridTreeNode.SELECTION_TYPE_PARTIAL){
				selectedList.add(node);
			}else{
				removeAttribute(node, attrs);
			}
		}
		selectedList.addAll(attrs);
		ADRestoreUtils.mergSelectedList(selectedList);
		return selectedList;
	}

	private void removeAttribute(GridTreeNode parent, List<GridTreeNode> attrs) {
		List<GridTreeNode> childs = new ArrayList<GridTreeNode>();
		for(GridTreeNode attr : attrs){
			if(attr.getParentID()==parent.getId()){
				childs.add(attr);
			}
		}
		attrs.removeAll(childs);
	}

	public void clear() {
		table.clear();
		parentNode = null;
		labelPath.setValue("");
		searchField.setValue("");
		store.removeAll();
		grid.getView().refresh(false);
		pagingBar.clear();
		updateLabelSelectedNodes(0, 0);
		attributePanel.clear();
	}
}
