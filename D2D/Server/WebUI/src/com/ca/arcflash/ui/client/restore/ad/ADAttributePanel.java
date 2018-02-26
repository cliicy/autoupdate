package com.ca.arcflash.ui.client.restore.ad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ADAttributePanel extends ContentPanel{
	
	private ListStore<GridTreeNode> attr_store;
	private Grid<GridTreeNode> attr_grid;
	private ColumnModel attr_colModel;
	private ActiveDirectoryExplorerPanel parentPanel;
	private GridTreeNode parentNode;
	private LabelToolItem labelSelectedAttributes;
	protected HashMap<String, GridTreeNode> table = new HashMap<String, GridTreeNode>();
	private CheckBox selectAllBox;
	
	public ADAttributePanel(ActiveDirectoryExplorerPanel activeDirectoryExplorerPanel) {
		this.parentPanel = activeDirectoryExplorerPanel;
		this.setHeaderVisible(false);
		this.setLayout(new FitLayout());
		this.setBorders(false);
//		this.setScrollMode(Scroll.AUTOY);
//		setScrollMode(Scroll.AUTO);
	    
		ColumnConfig name = new ColumnConfig("displayName", UIContext.Constants.restoreADAttributeColumn(), 150);
		name.setMenuDisabled(true);
		name.setRenderer(new GridCellRenderer<GridTreeNode>(){
				@Override
				public Object render(final GridTreeNode node, String property, ColumnData config, int rowIndex, int colIndex,
						ListStore<GridTreeNode> store, Grid<GridTreeNode> grid){
					ADGridCell cell = new ADGridCell(node);
					final FlashCheckBox fcb = cell.getFlashCheckBox();
					fcb .addSelectionListener(new SelectionListener<IconButtonEvent>() {
								@Override
								public void componentSelected(IconButtonEvent ce) {
									if (fcb.isEnabled() == false)
										return;
									node.setSelectionType(fcb.getSelectedState());
									addOrUpdateCache(node);
									updateParentNode();
								}
							});
					
					return cell;
			}

		});

		attr_colModel = new ColumnModel(Arrays.asList( name));
		
		attr_store = new ListStore<GridTreeNode>();

		attr_grid = new Grid<GridTreeNode>(attr_store, attr_colModel){
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				view.getHeader().setHeight(34);
			}
		};
//		attr_grid.setView(new GridView(){
//			public void refresh(boolean headerToo) {
//				super.refresh(headerToo);
//				Point p=new Point(0, 0);
//				restoreScroll(p);
//			}
//		});
	    attr_grid.setAutoExpandColumn("displayName");
	    attr_grid.getView().setAdjustForHScroll(false);
	    attr_grid.getView().setForceFit(true);  
	    attr_grid.setBorders(false);
//	    attr_grid.setAutoHeight(true);
	    attr_grid.setHeight("500");
	    
	    attr_grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    this.add(attr_grid);
	    
	    ToolBar toolBar = new ToolBar();
		toolBar.setHeight(28);
		toolBar.setAlignment(HorizontalAlignment.LEFT);
		toolBar.setBorders(true);
		selectAllBox = new CheckBox();
		selectAllBox.ensureDebugId("214987bd-6899-444c-8dd1-45490cdbe9bc");
		selectAllBox.setBoxLabel(UIContext.Constants.selectUnselectAll());
		selectAllBox.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				setSelectAll(selectAllBox.getValue());
			}
			
		});
		toolBar.add(selectAllBox);
		toolBar.add(new FillToolItem());
		toolBar.add(new SeparatorToolItem());
		labelSelectedAttributes = new LabelToolItem("");
		toolBar.add(labelSelectedAttributes);
		this.setBottomComponent(toolBar);
	}
	
	private void setSelectAll(boolean select) {
		int state = select ? GridTreeNode.SELECTION_TYPE_FULL : GridTreeNode.SELECTION_TYPE_NONE;
		List<GridTreeNode> models = attr_store.getModels();
		for(GridTreeNode model : models){
			model.setSelectionType(state);
			addOrUpdateCache(model);
		}
		attr_grid.getView().refresh(false);
		updateParentNode();
	}
	
	public void refresh(GridTreeNode parent) {
		if(parent == null){
			return;
		}
//		this.setHeading(parent.getDisplayName());
		this.parentNode = parent;
		if(parent.getSelectable()==null||parent.getSelectable()){
			selectAllBox.enable();
		}else{
			selectAllBox.disable();
		}
		AsyncCallback<List<GridTreeNode>> callback = new AsyncCallback<List<GridTreeNode>>(){

			@Override
			public void onFailure(Throwable caught) {
				ADAttributePanel.this.unmask();		
			}

			@Override
			public void onSuccess(List<GridTreeNode> result) {
				attr_store.removeAll();
				attr_grid.reconfigure(attr_store, attr_colModel);
				ADAttributePanel.this.unmask();
//				if(attr_store.getCount()>0){
//					attr_store.removeAll();
//				}
				if(result==null){
					updateLabelSelectedAttributes(0, 0);
					selectAllBox.setValue(false);
				}else{
					int selectedCount=0;
					for(GridTreeNode node : result){
						loadStateIfNeed(node);
						addOrUpdateCache(node);
						if(node.getSelectionType()==GridTreeNode.SELECTION_TYPE_FULL)
							selectedCount++;
					}
					if(selectedCount==result.size()){
						selectAllBox.setValue(true);
					}else{
						selectAllBox.setValue(false);
					}
					updateLabelSelectedAttributes(selectedCount, result.size());
					attr_store.add(result);
				}
				attr_grid.getView().refresh(false);
//				attr_grid.reconfigure(attr_store, attr_colModel);
//				attr_grid.getView().scrollToTop();
			}
			
		};
		this.mask(UIContext.Constants.loadingIndicatorText());
		parentPanel.loadADAttributes(parent, callback);
	}
	
	private void loadStateIfNeed(GridTreeNode node) {
		if(node.getSelectionType()==null || node.getSelectionType()==GridTreeNode.SELECTION_TYPE_PARTIAL){
			GridTreeNode exist = table.get(node.getPath());
			if(exist==null){
				node.setSelectionType(GridTreeNode.SELECTION_TYPE_NONE);
			} else {
				node.setSelectionType(exist.getSelectionType());
			}
		}
	}
	
	private void addOrUpdateCache(GridTreeNode node) {
		table.remove(node.getPath());
		table.put(node.getPath(), node);
	}
	
	private void updateLabelSelectedAttributes(int nSelected, int nTotal){
		if (nTotal == 0) {
			labelSelectedAttributes.setHtml("");
			labelSelectedAttributes.removeToolTip();
			//labelSelectedAttributes.removeStyleName("labelSelectedNumber");
		} else {
			labelSelectedAttributes.setHtml(UIContext.Messages.selectedNumberAndTotalNumber(nSelected, nTotal));
			ToolTipConfig config = new ToolTipConfig();
			config.setText(UIContext.Messages.ADAttributeSelectedTooltip(nSelected));
			config.setShowDelay(50);
			labelSelectedAttributes.setToolTip(config);
			//labelSelectedAttributes.addStyleName("labelSelectedNumber");
		}
	}
	
	private void updateParentNode() {
		int selectedCount=0;
		for(int i=0; i<attr_store.getCount(); i++){
			GridTreeNode model=attr_store.getAt(i);
			if(table.get(model.getPath()).getSelectionType()==GridTreeNode.SELECTION_TYPE_FULL){
				selectedCount++;
			}
		}
		updateSelectAllBox(selectedCount, attr_store.getCount());
		updateLabelSelectedAttributes(selectedCount, attr_store.getCount());
		if(selectedCount==0){
			//check if child node of parent is selected in tree and grid panel.
			boolean selected=checkBrotherNodeSelectedStatus();
			if(selected){
				parentNode.setSelectionType(GridTreeNode.SELECTION_TYPE_PARTIAL);
			}else{
				parentNode.setSelectionType(GridTreeNode.SELECTION_TYPE_NONE);
			}
		}else{
			parentNode.setSelectionType(GridTreeNode.SELECTION_TYPE_PARTIAL);
		}
		GridTreeNode treeNode=parentPanel.treePanel.tree.getSelectionModel().getSelectedItem();
		GridTreeNode gridNode=parentPanel.gridPanel.grid.getSelectionModel().getSelectedItem();
		if(treeNode==null&&gridNode==null){//no focus. update both tree and grid
			parentPanel.treePanel.updateNode(parentNode);
			parentPanel.gridPanel.updateNode(parentNode);
		}else if(gridNode==null){//focus on tree
			parentPanel.treePanel.updateNode(parentNode);
		}else{//focus on grid
			parentPanel.gridPanel.updateNode(parentNode);
		}
	}

	private void updateSelectAllBox(int selectedCount, int count) {
		if(selectedCount == 0|| count == 0){
			selectAllBox.setValue(false);
		}else if(selectedCount == count){
			selectAllBox.setValue(true);
		}else{
			selectAllBox.setValue(false);
		}
	}

	private boolean checkBrotherNodeSelectedStatus() {
		List<GridTreeNode> brothers = parentPanel.treePanel.getChildren(parentNode);
		for(GridTreeNode bro : brothers){
			if (bro.getSelectionType() != GridTreeNode.SELECTION_TYPE_NONE){
				return true;
			}
		}
		brothers = parentPanel.gridPanel.getChildren(parentNode);
		for(GridTreeNode bro : brothers){
			if (bro.getSelectionType() != GridTreeNode.SELECTION_TYPE_NONE){
				return true;
			}
		}
		return false;
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

	public List<GridTreeNode> getSelectedChildren(GridTreeNode parent) {
		List<GridTreeNode> result = new ArrayList<GridTreeNode>();
		List<GridTreeNode> childs = getChildren(parent);
		for(GridTreeNode child : childs){
			if(child.getSelectionType()==GridTreeNode.SELECTION_TYPE_FULL){
				result.add(child);
			}
		}
		return result;
	}

	public void clear() {
		table.clear();
		parentNode = null;
		attr_store.removeAll();
		attr_grid.getView().refresh(false);
		updateLabelSelectedAttributes(0, 0);
		selectAllBox.setValue(false);
	}

	public List<GridTreeNode> getFullySelectedNodes() {
		List<GridTreeNode> result = new ArrayList<GridTreeNode>();
		Iterator<GridTreeNode> i = table.values().iterator();
		while(i.hasNext()){
			GridTreeNode node = i.next();
			if(node.getSelectionType()==GridTreeNode.SELECTION_TYPE_FULL){
				result.add(node);
			}
		}
		return result;
	}

}
