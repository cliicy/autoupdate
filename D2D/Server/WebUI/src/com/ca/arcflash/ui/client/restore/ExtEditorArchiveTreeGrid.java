package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.RestoreJobType;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class ExtEditorArchiveTreeGrid<M extends ModelData> extends EditorTreeGrid<M> {

	final RestoreArchiveBrowsePanel browsePanel;
	
	
	
	@SuppressWarnings("unchecked")
	public ExtEditorArchiveTreeGrid(TreeStore store, ColumnModel cm,
			HashMap<ArchiveGridTreeNode, FlashCheckBox> table, boolean isRestManager, RestoreArchiveBrowsePanel browsePanel) {
		super(store, cm);
		this.table = table;
		this.isRestManager = isRestManager;
		this.browsePanel = browsePanel;
	}
	
	private HashMap<ArchiveGridTreeNode, FlashCheckBox> table;
	private boolean isRestManager;

	private HashMap<ArchiveGridTreeNode, ArchivePagingContext> nodeContextMap = new HashMap<ArchiveGridTreeNode, ArchivePagingContext>();
	public void clean(ArchiveGridTreeNode node) {
		nodeContextMap.remove(node);
	}
	
	final LoginServiceAsync service = GWT.create(LoginService.class);

	@Override
	protected void onClick(GridEvent<M> e) {
		M m = e.getModel();
		if (m != null) {
			TreeNode node = findNode(m);
			if (node != null) {
				Element jointEl = treeGridView.getJointElement(node);
				if (jointEl != null && e.within(jointEl)){
					if (m instanceof ArchiveGridTreeNode) 
					{
						final ArchiveGridTreeNode treeNode = (ArchiveGridTreeNode) m;
						browsePanel.mask(UIContext.Constants.loadingIndicatorText());
						service.getArchiveTreeGridChildrenCount(browsePanel.archiveDestinationInfo,treeNode, new BaseAsyncCallback<Long>() {
							
							@Override
							public void onSuccess(Long result) {
								browsePanel.unmask();
								long childCount = result.longValue();
								treeNode.setChildrenCount(childCount);
								if (childCount > ArchivePagingContext.PAGETHRESHOLD) {
									
									handlePaging(treeNode);
								} else 
								{
									toggle((M) treeNode);
								}
								
							}
							
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								browsePanel.unmask();
							}
						});
						
						
						
					} else 
					{
						toggle(m);
					}
				} else {
					super.onClick(e);
				}
			}
		}
	}

	
	/*@Override
	protected void onClick(GridEvent<M> e) {
		M m = e.getModel();
		if (m != null) {
			TreeNode node = findNode(m);
			if (node != null) {
				Element jointEl = treeGridView.getJointElement(node);
				if (jointEl != null && e.within(jointEl)){
					if (m instanceof ArchiveGridTreeNode) 
					{
						final ArchiveGridTreeNode treeNode = (ArchiveGridTreeNode) m;
						if (treeNode.getChildrenCount() != null
								&& treeNode.getChildrenCount() > ArchivePagingContext.PAGETHRESHOLD) {
							handlePaging(treeNode);
						} else 
						{
							toggle(m);
						}
					} else 
					{
						toggle(m);
					}
				} else {
					super.onClick(e);
				}
			}
		}
	}*/

	private void handlePaging(ArchiveGridTreeNode treeNode) {
		FlashCheckBox clickedNodeCheckBox = table.get(treeNode);
		if (clickedNodeCheckBox == null) {
			clickedNodeCheckBox = new FlashCheckBox();
			table.put(treeNode, clickedNodeCheckBox);
		}

		ArchivePagingContext.handleClick(treeNode, nodeContextMap, null,
				clickedNodeCheckBox, true, isRestManager, this);		
	}

	public HashMap<ArchiveGridTreeNode, FlashCheckBox> getTable() {
		return table;
	}

	public void setTable(HashMap<ArchiveGridTreeNode, FlashCheckBox> table) {
		this.table = table;
	}

	public HashMap<ArchiveGridTreeNode, ArchivePagingContext> getNodeContextMap() {
		return nodeContextMap;
	}

	public void setNodeContextMap(
			HashMap<ArchiveGridTreeNode, ArchivePagingContext> nodeContextMap) {
		this.nodeContextMap = nodeContextMap;
	}

	public List<ArchiveGridTreeNode> getPagedSelectedNodes() {
		List<ArchiveGridTreeNode> pagedSelected = new ArrayList<ArchiveGridTreeNode>();
		Iterator<ArchiveGridTreeNode> contIt = nodeContextMap.keySet().iterator();
		while (contIt.hasNext()) {
			ArchiveGridTreeNode contNode = contIt.next();
			ArchivePagingContext childCont = nodeContextMap.get(contNode);
			if (childCont != null) {
				List<ArchiveGridTreeNode> lst = childCont.getSelectedNodes();
				pagedSelected.addAll(lst);
			}
		}
		return pagedSelected;
	}

	private boolean isFileSystem(ArchiveGridTreeNode node) {
		List<ArchiveGridTreeNode> selectedNodes = new ArrayList<ArchiveGridTreeNode>();
		selectedNodes.add(node);
		
		RestoreJobType type = RestoreUtil.getArchiveJobType(selectedNodes);
		if (type != null && type == RestoreJobType.FileSystem) {
			return true;
		} 
		return false;
	}

	@Override
	protected void onDoubleClick(GridEvent<M> e) {
		if (editSupport.onDoubleClick(e)) {
			return;
		}
		M m = e.getModel();

		boolean isOpenPaging = false;
		if (m instanceof ArchiveGridTreeNode) {
			final ArchiveGridTreeNode treeNode = (ArchiveGridTreeNode) m;

			if (treeNode.getChildrenCount() != null
					&& treeNode.getChildrenCount() > ArchivePagingContext.PAGETHRESHOLD) {
				isOpenPaging = true;
			}
		}

		if (isOpenPaging) {
			handlePaging((ArchiveGridTreeNode) m);
		} else {
			super.onDoubleClick(e);
		}
	}
	
	public void selectTreeNodeParent(M node) {
		M parent = this.getTreeStore().getParent(node);
		int parentState = FlashCheckBox.NONE;
		if (parent != null) {
			int fullCount = 0;
			int partialCount = 0;
			int emptyCount = 0;
			int nullCount = 0;

			List<M> childNodes = this.getTreeStore().getChildren(
					parent);
			// For each call select Children
			for (int i = 0; i < childNodes.size(); i++) {
				FlashCheckBox fcb = table.get(childNodes.get(i));
				if (fcb != null) {
					switch (fcb.getSelectedState()) {
					case FlashCheckBox.FULL:
						fullCount++;
						break;
					case FlashCheckBox.PARTIAL:
						partialCount++;
						break;
					case FlashCheckBox.NONE:
					default:
						emptyCount++;
						break;
					}
				} else {
					nullCount++;
				}
			}

			if (emptyCount + nullCount == childNodes.size()) {
				parentState = FlashCheckBox.NONE;
			} else {
				parentState = FlashCheckBox.PARTIAL;
			}

			FlashCheckBox fcb = table.get(parent);
			if (fcb != null) {
				fcb.setSelectedState(parentState);
					if (parent instanceof ArchiveGridTreeNode) {
					
					final ArchiveGridTreeNode parentNode = (ArchiveGridTreeNode) parent;
					
					if(parentState == FlashCheckBox.PARTIAL)
						parentNode.setChecked(true);
					else
						parentNode.setChecked(false);
				
					// Parent changed, change the parent's parent
					selectTreeNodeParent(parent);
			}
		}
	}
	}
}
