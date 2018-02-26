package com.ca.arcflash.ui.client.restore.ad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class ADTreePanel extends ContentPanel {
	private ActiveDirectoryExplorerPanel parentPanel;
	private TreeStore<GridTreeNode> treeStore;
	private BaseTreeLoader<GridTreeNode> loader;
	private ColumnModel treeColModel;
	protected TreeGrid<GridTreeNode> tree;
	private HashMap<GridTreeNode, FlashCheckBox> table = new HashMap<GridTreeNode, FlashCheckBox>();

	public ADTreePanel(ActiveDirectoryExplorerPanel activeDirectoryExplorerPanel) {
		this.parentPanel = activeDirectoryExplorerPanel;
		setHeaderVisible(false);
		setLayout(new FitLayout());
		setCollapsible(false);
		setScrollMode(Scroll.AUTOY);
//		setScrollMode(Scroll.AUTO);
		setBorders(false);
//		this.setSize("100%", "100%");
		defineTree();
		add(tree);
	}
	
	@Override
	protected void onRender(Element parent, int index){
		super.onRender(parent, index);
	}
	
	private void defineTree() {
		treeColModel = createColumnModel();

		loader = createTreeLoader();

		treeStore = new TreeStore<GridTreeNode>(loader);

		tree = new TreeGrid<GridTreeNode>(treeStore, treeColModel);
			
//		tree.addListener(Events.Expand, new Listener<BaseEvent>(){
//
//			@Override
//			public void handleEvent(BaseEvent be) {
//				tree.unmask();
//			}
//		});	

		// liuwe05 2011-01-17 fix Issue: 19972589    Title: FULL SELECTION NOT WORK
		// This issue is caused by the BufferView, buffering is enabled for TreeGridView by default.
		// But turning off the buffering by setBufferEnabled(false) will cause display problem.
		// So I enlarge the buffered row count here, it can make sure the children nodes are all rendered without other impact
		tree.setView(new TreeGridView()
		{
			@Override
			protected int getVisibleRowCount()
			{
				int nVisableRowCount = super.getVisibleRowCount();
				
				if (nVisableRowCount < 10000)
				{
					nVisableRowCount = 10000;
				}
				
				return nVisableRowCount; 
			}

			@Override
			protected void doUpdate() {
				if (grid == null || !grid.isViewReady() || !this.isBufferEnabled()) {
				      return;
				    }
				    int count = getVisibleRowCount();
				    if (count > 0) {
				      ColumnModel cm = grid.getColumnModel();

				      ListStore<ModelData> store = grid.getStore();
				      List<ColumnData> cs = getColumnData();
				      boolean stripe = grid.isStripeRows();
				      int[] vr = getVisibleRows(count);
				      int cc = cm.getColumnCount();
				      for (int i = vr[0]; i <= vr[1]; i++) {
				        // if row is NOT rendered and is visible, render it
				        if (!isRowRendered(i)) {
				          List<ModelData> list = new ArrayList<ModelData>();
				          list.add(store.getAt(i));
				          //fix 149418 
				          //http://www.sencha.com/forum/showthread.php?176844-GXT-2.2.4-Bug-on-GridCellRenderer-for-TreeGrid
				          //widgetList.add(i, new ArrayList<Widget>());
				          widgetList.set(i, new ArrayList<Widget>());
				          String html = doRender(cs, list, i, cc, stripe, true);
				          getRow(i).setInnerHTML(html);
				          renderWidgets(i, i);
				        }
				      }
				      clean();
				    }
			}
		});
		
		((TreeGridView)tree.getView()).setRowHeight(23);
		// Remove the default icons
		tree.setIconProvider(new ModelIconProvider<GridTreeNode>() {

			@Override
			public AbstractImagePrototype getIcon(GridTreeNode model) {
				return AbstractImagePrototype.create(UIContext.IconBundle.blank());
			}

		});
		tree.setBorders(false);	
		tree.setAutoExpandColumn("displayName");
//		tree.setAutoHeight(true);
//		tree.setHeight(487);
		tree.setHeight(500);
		tree.setTrackMouseOver(false);
		tree.setLazyRowRender(0);
		tree.getView().setAutoFill(true);
//		tree.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<GridTreeNode>(){
//
//			@Override
//			public void selectionChanged(SelectionChangedEvent<GridTreeNode> se) {
//				GridTreeNode node = se.getSelectedItem();
//				if(node==null){
//					return;
//				}else{
//					GridTreeNode model = findModel(node.getId());
//					parentPanel.gridPanel.populateGrid(model);
//				}				
//			}
//			
//		});

	}
	
	private ColumnModel createColumnModel() {
		ColumnConfig name = new ColumnConfig("displayName", UIContext.Constants.restoreADObjectColumn(), 150);
		name.setMenuDisabled(true);
		name.setRenderer(new WidgetTreeGridCellRenderer<GridTreeNode>() {

			@Override
			public Widget getWidget(final GridTreeNode node, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GridTreeNode> store, Grid<GridTreeNode> grid) {
				FlashCheckBox exist = table.get(node);
				if(exist==null){
					return null;
				}
				ADGridCell cell = new ADGridCell(node, exist);
				final FlashCheckBox fcb = cell.getFlashCheckBox();
				fcb.addSelectionListener(new SelectionListener<IconButtonEvent>() {
					@Override
					public void componentSelected(IconButtonEvent ce) {
						if (fcb.isEnabled() == false)
							return;
						node.setSelectionType(fcb.getSelectedState());
						selectTreeNodeChildren(node, fcb.getSelectedState(), true);
						parentPanel.gridPanel.populateGrid(node);
//						tree.getSelectionModel().select(node, false);
					}
				});
				cell.addClickListener(new Listener<BaseEvent>(){

					@Override
					public void handleEvent(BaseEvent be) {
						if (tree != null ) {
							GridTreeNode model = findModel(node.getId());
							parentPanel.gridPanel.populateGrid(model);
//							tree.getSelectionModel().select(node, false);
						}
					}});
				return cell;
			}

		});

		return new ColumnModel(Arrays.asList(name));
	}

	private BaseTreeLoader<GridTreeNode> createTreeLoader() {
		RpcProxy<List<GridTreeNode>> proxy = new RpcProxy<List<GridTreeNode>>() {
			@Override
			protected void load(Object loadConfig, final AsyncCallback<List<GridTreeNode>> callback) {
//				mask(UIContext.Constants.loadingIndicatorText());
				
				AsyncCallback<List<GridTreeNode>> adCallback = new AsyncCallback<List<GridTreeNode>>(){

					@Override
					public void onFailure(Throwable caught) {
//						ADTreePanel.this.unmask();
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(List<GridTreeNode> result) {
//						ADTreePanel.this.unmask();
						if(result!=null){
							for(GridTreeNode node : result){
								loadStateIfNeed(node);
								addOrUpdateCache(node);
							}
						}
						callback.onSuccess(result);
					}
					
				};
				
				GridTreeNode node =(GridTreeNode) loadConfig;
				if(node!=null){
					FlashCheckBox fcb = table.get(node);
					node.setSelectionType(fcb.getSelectedState());
				}
				
				parentPanel.loadADNodes(node, adCallback);
			}
		};

		return new BaseTreeLoader<GridTreeNode>(proxy) {
			public boolean hasChildren(GridTreeNode parent) {
				Integer type = parent.getType();
				if (type != null && (type == CatalogModelType.File 
						|| CatalogModelType.rootGRTExchangeTypes.contains(type.intValue())
						|| type == CatalogModelType.OT_VSS_SQL_COMPONENT_SELECTABLE
						|| type == CatalogModelType.AD_ATTRIBUTE)) {
					return false;
				} else {
					return true;
				}
			}
		};
	}

	private void loadStateIfNeed(final GridTreeNode node) {
		if(node.getSelectionType()!=null && node.getSelectionType()==GridTreeNode.SELECTION_TYPE_PARTIAL){
			GridTreeNode exist = parentPanel.gridPanel.table.get(node.getId());
			if (exist == null) {
				node.setSelectionType(GridTreeNode.SELECTION_TYPE_NONE);
			} else {
				node.setSelectionType(exist.getSelectionType());
			}
		}
	}
	
	protected void populateTree() {
		//not display root node
		GridTreeNode root = new GridTreeNode();
		root.setId(0);
		root.setParentID(0L);
		root.setPath("");
		root.setSelectionType(GridTreeNode.SELECTION_TYPE_NONE);
		parentPanel.mask(UIContext.Constants.loadingIndicatorText());
		parentPanel.firePageMasked(0);
		treeStore.removeAll();
		AsyncCallback<List<GridTreeNode>> callback = new BaseAsyncCallback<List<GridTreeNode>>(){

			@Override
			public void onFailure(Throwable caught) {
				parentPanel.unmask();
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(List<GridTreeNode> result) {
				parentPanel.unmask();
				parentPanel.firePageMasked(1);
				if(result!=null){
					for(GridTreeNode node : result){
						addOrUpdateCache(node);
					}
				}
				treeStore.add(result, false);
				tree.getView().refresh(false);
				tree.getView().scrollToTop();
			}
			
		};
		parentPanel.loadADNodes(root, callback);
	}
	
	private void selectTreeNodeChildren(GridTreeNode node, int state, boolean updateParent){
		//Set the parent
		if (updateParent){
			selectTreeNodeParent(node);
		}
		
		if(state==GridTreeNode.SELECTION_TYPE_PARTIAL){
			return;
		}
		//Get the children
		List<GridTreeNode> childNodes = getChildren(node);
//		List<GridTreeNode> childNodes = tree.getTreeStore().getChildren(node);
		//For each call select Children
		for (int i = 0 ; i < childNodes.size(); i++){
			GridTreeNode child = childNodes.get(i);
			child.setSelectionType(state);
			addOrUpdateCache(child);
			selectTreeNodeChildren(child, state, false);
		}
	}
	
	private void selectTreeNodeParent(GridTreeNode node) {
		if(node.getParentID()==0){// root node
			return;
		}
		GridTreeNode parent = findModel(node.getParentID().intValue());
		if(parent==null){
			return;
		}
		int noneCount = 0;
		List<GridTreeNode> childNodes = getChildren(parent);
		for (GridTreeNode child : childNodes) {
			if (child.getSelectionType() == GridTreeNode.SELECTION_TYPE_NONE)
				noneCount++;
		}
		if (noneCount == childNodes.size()) {
			//check attribute items of parent
			List<GridTreeNode> childAttributes = parentPanel.gridPanel.attributePanel.getChildren(parent);
			boolean select_none = true;
			for(GridTreeNode attr : childAttributes){
				if (attr.getSelectionType() == GridTreeNode.SELECTION_TYPE_FULL){
					select_none = false;
					break;
				}
			}
			if(select_none){
				parent.setSelectionType(GridTreeNode.SELECTION_TYPE_NONE);
			}else{
				parent.setSelectionType(GridTreeNode.SELECTION_TYPE_PARTIAL);
			}
		} else {
			parent.setSelectionType(GridTreeNode.SELECTION_TYPE_PARTIAL);
		}
		addOrUpdateCache(parent);
		// Parent changed, change the parent's parent
		selectTreeNodeParent(parent);
	}
	
	public boolean updateNode(GridTreeNode node){
		Integer state = node.getSelectionType();
		GridTreeNode model = findModel(node.getId());
		if(model!=null){
			model.setSelectionType(state);
			addOrUpdateCache(model);
			selectTreeNodeChildren(model, node.getSelectionType(), true);
			return true;
		}else{
			return false;
		}
	}
	
	public GridTreeNode findModel(int id) {
		Iterator<Entry<GridTreeNode, FlashCheckBox>> i = table.entrySet().iterator();
		while(i.hasNext()){
			Entry<GridTreeNode, FlashCheckBox> entry = i.next();
			GridTreeNode model = entry.getKey();
			if(model.getId()==id){
				FlashCheckBox fcb = entry.getValue();
				model.setSelectionType(fcb.getSelectedState());
				return model;
			}
		}
		return null;
	}
	
	public List<GridTreeNode> getChildren(GridTreeNode node) {
		List<GridTreeNode> childs = new ArrayList<GridTreeNode>();
		Iterator<Entry<GridTreeNode, FlashCheckBox>> i = table.entrySet().iterator();
		while(i.hasNext()){
			Entry<GridTreeNode, FlashCheckBox> entry = i.next();
			GridTreeNode model = entry.getKey();
			if(model.getParentID()==node.getId()){
				FlashCheckBox fcb = entry.getValue();
				model.setSelectionType(fcb.getSelectedState());
				childs.add(model);
			}
			
		}
		return childs;
	}

	private void addOrUpdateCache(GridTreeNode node) {
		FlashCheckBox fcb = table.get(node);
		if(fcb==null){
			fcb = new FlashCheckBox();
			fcb.setSelectedState(node.getSelectionType());
			table.put(node, fcb);
		}else{
			fcb.setSelectedState(node.getSelectionType());
		}
	}

	public void clear() {
		table.clear();
		treeStore.removeAll();
		tree.getView().refresh(false);
	}

	public List<GridTreeNode> getSelectedNodes() {
		List<GridTreeNode> selectedList= new ArrayList<GridTreeNode>();
		Iterator<Entry<GridTreeNode, FlashCheckBox>> i = table.entrySet().iterator();
		while(i.hasNext()){
			Entry<GridTreeNode, FlashCheckBox> entry = i.next();
			GridTreeNode node = entry.getKey();
			FlashCheckBox fcb = entry.getValue();
			int state = fcb.getSelectedState();
			node.setSelectionType(state);
			if(state != GridTreeNode.SELECTION_TYPE_NONE){
				selectedList.add(node);
			}
		}
		ADRestoreUtils.mergSelectedList(selectedList);
		return selectedList;
	}
	
	public List<GridTreeNode> getNoneSelectedNodes() {
		List<GridTreeNode> selectedList= new ArrayList<GridTreeNode>();
		Iterator<Entry<GridTreeNode, FlashCheckBox>> i = table.entrySet().iterator();
		while(i.hasNext()){
			Entry<GridTreeNode, FlashCheckBox> entry = i.next();
			GridTreeNode node = entry.getKey();
			FlashCheckBox fcb = entry.getValue();
			int state = fcb.getSelectedState();
			node.setSelectionType(state);
			if(state == GridTreeNode.SELECTION_TYPE_NONE){
				selectedList.add(node);
			}
		}
		return selectedList;
	}
}
