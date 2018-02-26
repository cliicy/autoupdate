package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class ArchivePagingContext {
	private ArchiveGridTreeNode parent;
	private FlashCheckBox parentCheckBox;
	private HashMap<ArchiveGridTreeNode, ArchivePagingContext> childrenContextMap = new HashMap<ArchiveGridTreeNode, ArchivePagingContext>();
	private HashMap<ArchiveGridTreeNode, FlashCheckBox> childrenStateMap = new HashMap<ArchiveGridTreeNode, FlashCheckBox>();
	private boolean isRestoreManager;

	public final static int DEFAULTPAGESIZE = 25;
	public final static int PAGETHRESHOLD = 100;

	public int calcPages(int pageSize) {
		long cnt = parent.getChildrenCount();
		int num = (int) Math.ceil(cnt * 1.0 / pageSize);
		return num;
	}

	public HashMap<ArchiveGridTreeNode, ArchivePagingContext> getChildrenContextMap() {
		return childrenContextMap;
	}

	public void setChildrenContextMap(
			HashMap<ArchiveGridTreeNode, ArchivePagingContext> childrenContextMap) {
		this.childrenContextMap = childrenContextMap;
	}

	public HashMap<ArchiveGridTreeNode, FlashCheckBox> getChildrenStateMap() {
		return childrenStateMap;
	}

	public void setChildrenStateMap(
			HashMap<ArchiveGridTreeNode, FlashCheckBox> childrenStateMap) {
		this.childrenStateMap = childrenStateMap;
	}

	public void setParent(ArchiveGridTreeNode parent) {
		this.parent = parent;
	}

	public ArchiveGridTreeNode getParent() {
		return parent;
	}

	public void clearCurrentAndChildren() {
		childrenContextMap.clear();// don't care children
		childrenStateMap.clear();
	}

	public List<ArchiveGridTreeNode> getSelectedNodes() {
		cleanRedandant();

		ArrayList<ArchiveGridTreeNode> selectedNodes = new ArrayList<ArchiveGridTreeNode>();
		if (parentCheckBox != null) {
			if (parentCheckBox.getSelectedState() == FlashCheckBox.PARTIAL) {
				HashMap<ArchiveGridTreeNode, FlashCheckBox> map = this
						.getChildrenStateMap();
				if (map != null && map.size() > 0) {
					Iterator<ArchiveGridTreeNode> it = map.keySet().iterator();
					while (it.hasNext()) {
						ArchiveGridTreeNode node = it.next();
						FlashCheckBox box = map.get(node);
						if (box != null) {
							if (box.getSelectedState() == FlashCheckBox.FULL) {
								selectedNodes.add(node);
							} else if (box.getSelectedState() == FlashCheckBox.PARTIAL) {
								if (this.getChildrenContextMap() != null) {
									ArrayList<ArchiveGridTreeNode> nextLevelChildren = getChildrenContextSelection();
									selectedNodes.addAll(nextLevelChildren);
								}
							}
						}
					}
				}
			}
		}
		return selectedNodes;
	}

	private ArrayList<ArchiveGridTreeNode> getChildrenContextSelection() {
		ArrayList<ArchiveGridTreeNode> selectedChildren = new ArrayList<ArchiveGridTreeNode>();

		HashMap<ArchiveGridTreeNode, ArchivePagingContext> contMap = this
				.getChildrenContextMap();

		if (contMap == null || contMap.size() == 0) {
			return selectedChildren;
		}

		Iterator<ArchiveGridTreeNode> contIt = contMap.keySet().iterator();
		while (contIt.hasNext()) {
			ArchiveGridTreeNode contNode = contIt.next();
			ArchivePagingContext childCont = contMap.get(contNode);
			if (childCont != null) {
				List<ArchiveGridTreeNode> lst = childCont.getSelectedNodes();
				selectedChildren.addAll(lst);
			}
		}
		return selectedChildren;
	}

	private void cleanRedandant() {
		ArrayList<ArchiveGridTreeNode> removeContextNodes = new ArrayList<ArchiveGridTreeNode>();
		ArrayList<ArchiveGridTreeNode> removeNodes = new ArrayList<ArchiveGridTreeNode>();
		if (parentCheckBox != null) {
			if (parentCheckBox.getSelectedState() == FlashCheckBox.FULL
					|| parentCheckBox.getSelectedState() == FlashCheckBox.NONE) {
				clearCurrentAndChildren();
			} else if (parentCheckBox.getSelectedState() == FlashCheckBox.PARTIAL) {
				// parent is Partial
				HashMap<ArchiveGridTreeNode, FlashCheckBox> map = this
						.getChildrenStateMap();
				if (map != null && map.size() > 0) {
					Iterator<ArchiveGridTreeNode> it = map.keySet().iterator();
					while (it.hasNext()) {
						ArchiveGridTreeNode node = it.next();
						FlashCheckBox box = map.get(node);
						if (box != null) {
							if (box.getSelectedState() == FlashCheckBox.FULL) {
								removeContextNodes.add(node);
							} else if (box.getSelectedState() == FlashCheckBox.PARTIAL) {
								cleanChildrenContext();
							} else {// None
								removeContextNodes.add(node);
								removeNodes.add(node);
							}
						}
					}
				}
			}
		}

		for (ArchiveGridTreeNode node : removeContextNodes) {
			childrenContextMap.remove(node);
		}

		for (ArchiveGridTreeNode node : removeNodes) {
			childrenContextMap.remove(node);
			childrenStateMap.remove(node);
		}
	}

	private void cleanChildrenContext() {
		if (this.getChildrenContextMap() == null) {
			return;
		}

		HashMap<ArchiveGridTreeNode, ArchivePagingContext> contMap = this
				.getChildrenContextMap();

		Iterator<ArchiveGridTreeNode> contIt = contMap.keySet().iterator();
		while (contIt.hasNext()) {
			ArchiveGridTreeNode contNode = contIt.next();
			ArchivePagingContext childCont = contMap.get(contNode);
			if (childCont != null) {
				childCont.cleanRedandant();
			}
		}
	}

	public void setParentCheckBox(FlashCheckBox parentCheckBox) {
		this.parentCheckBox = parentCheckBox;
	}

	public FlashCheckBox getParentCheckBox() {
		return parentCheckBox;
	}

	public ArchivePagingContext clone() {
		ArchivePagingContext newCont = new ArchivePagingContext();
		newCont.setParent(parent);
		newCont.setParentCheckBox(parentCheckBox);

		HashMap<ArchiveGridTreeNode, FlashCheckBox> thisNSmap = this
				.getChildrenStateMap();
		if (thisNSmap != null && thisNSmap.size() > 0) {
			HashMap<ArchiveGridTreeNode, FlashCheckBox> newStateMap = cloneMap(thisNSmap);
			newCont.setChildrenStateMap(newStateMap);
		}

		HashMap<ArchiveGridTreeNode, ArchivePagingContext> thisNCmap = this
				.getChildrenContextMap();

		if (thisNCmap != null && thisNCmap.size() > 0) {
			HashMap<ArchiveGridTreeNode, ArchivePagingContext> newContextMap = cloneContextMap(thisNCmap);
			newCont.setChildrenContextMap(newContextMap);
		}
		return newCont;
	}

	public static HashMap<ArchiveGridTreeNode, ArchivePagingContext> cloneContextMap(
			HashMap<ArchiveGridTreeNode, ArchivePagingContext> map) {
		HashMap<ArchiveGridTreeNode, ArchivePagingContext> newMap = new HashMap<ArchiveGridTreeNode, ArchivePagingContext>();
		if (map != null) {
			Iterator<ArchiveGridTreeNode> it = map.keySet().iterator();
			while (it.hasNext()) {
				ArchiveGridTreeNode node = it.next();
				ArchivePagingContext childCont = map.get(node);
				if (childCont != null) {
					ArchivePagingContext newChildCont = childCont.clone();
					newMap.put(node, newChildCont);
				}
			}
		}

		return newMap;
	}

	public static HashMap<ArchiveGridTreeNode, FlashCheckBox> cloneMap(
			HashMap<ArchiveGridTreeNode, FlashCheckBox> map) {
		HashMap<ArchiveGridTreeNode, FlashCheckBox> newMap = new HashMap<ArchiveGridTreeNode, FlashCheckBox>();
		if (map != null) {
			Iterator<ArchiveGridTreeNode> it = map.keySet().iterator();
			while (it.hasNext()) {
				ArchiveGridTreeNode node = it.next();
				FlashCheckBox box = map.get(node);
				if (box != null) {
					FlashCheckBox newBox = new FlashCheckBox();
					newBox.setSelectedState(box.getSelectedState());
					newMap.put(node, newBox);
				}
			}
		}

		return newMap;
	}

	public static Set<ArchiveGridTreeNode> cloneSet(Set<ArchiveGridTreeNode> set) {
		Map<ArchiveGridTreeNode, Object> newMap = new HashMap<ArchiveGridTreeNode, Object>();
		if (set != null) {
			Iterator<ArchiveGridTreeNode> it = set.iterator();
			while (it.hasNext()) {
				newMap.put(it.next(), null);
			}
		}
		return newMap.keySet();
	}

	public static boolean hasFullorPartialSelected(
			HashMap<ArchiveGridTreeNode, FlashCheckBox> map) {
		if (map == null) {
			return false;
		}
		boolean hasFullorPartialSelected = false;
		Iterator<ArchiveGridTreeNode> it = map.keySet().iterator();
		while (it.hasNext()) {
			ArchiveGridTreeNode node = it.next();
			FlashCheckBox box = map.get(node);
			if (box != null
					&& (box.getSelectedState() == FlashCheckBox.FULL || box
							.getSelectedState() == FlashCheckBox.PARTIAL)) {
				hasFullorPartialSelected = true;
				break;
			}
		}

		return hasFullorPartialSelected;
	}

	public static boolean isAllFullSelected(
			HashMap<ArchiveGridTreeNode, FlashCheckBox> map) {
		if (map == null) {
			return false;
		}
		boolean isAllFullSelected = true;
		Iterator<ArchiveGridTreeNode> it = map.keySet().iterator();
		while (it.hasNext()) {
			ArchiveGridTreeNode node = it.next();
			FlashCheckBox box = map.get(node);
			if (box != null && (box.getSelectedState() != FlashCheckBox.FULL)) {
				isAllFullSelected = false;
				break;
			}
		}

		return isAllFullSelected;
	}

	public static HashMap<ArchiveGridTreeNode, Set<ArchiveGridTreeNode>> cloneMapSet(
			HashMap<ArchiveGridTreeNode, Set<ArchiveGridTreeNode>> map) {
		HashMap<ArchiveGridTreeNode, Set<ArchiveGridTreeNode>> newMap = new HashMap<ArchiveGridTreeNode, Set<ArchiveGridTreeNode>>();
		if (map != null) {
			Iterator<ArchiveGridTreeNode> it = map.keySet().iterator();
			while (it.hasNext()) {
				ArchiveGridTreeNode node = it.next();
				Set<ArchiveGridTreeNode> set = map.get(node);
				if (set != null) {
					newMap.put(node, cloneSet(set));
				}
			}
		}

		return newMap;
	}

	public static void handleClick(final ArchiveGridTreeNode clickedTreeNode,
			final HashMap<ArchiveGridTreeNode, ArchivePagingContext> nodeContextMap,
			final ArchivePagingBrowsePanel clickOnPanel,
			final FlashCheckBox clickedNodeCheckBox, final boolean isFromTree,
			boolean isRestoreManager, final ExtEditorArchiveTreeGrid treeGrid) {

		if (clickedTreeNode == null) {
			return;
		}

		if (isFromTree) {
			if (clickedTreeNode.getChildrenCount() <= ArchivePagingContext.PAGETHRESHOLD) {
				return;
			}
		} else {
			if (clickedTreeNode.getType() != CatalogModelType.Folder) {
				return;
			}
		}

		ArchivePagingContext pContext = nodeContextMap.get(clickedTreeNode);

		if (pContext == null) {
			pContext = new ArchivePagingContext();
			pContext.setRestoreManager(isRestoreManager);
			pContext.setParent(clickedTreeNode);
			pContext.setParentCheckBox(clickedNodeCheckBox);
			nodeContextMap.put(clickedTreeNode, pContext);
		}

		final ArchivePagingContext clickedContext = pContext;

		final ArchivePagingContext pContClone = clickedContext.clone();

		final ArchivePagingBrowseWindow pChildWin = new ArchivePagingBrowseWindow(
				clickedContext);
		if (clickOnPanel != null) {
			int x = clickOnPanel.getAbsoluteLeft();
			int y = clickOnPanel.getAbsoluteTop();
			pChildWin.setPagePosition(x, y);
		}
		pChildWin.setModal(true);
		pChildWin.addWindowListener(new WindowListener() {
			@Override
			public void windowHide(WindowEvent we) {
				
				HashMap<ArchiveGridTreeNode, FlashCheckBox> pChildWin_AllOpenedMap = null;
				
				
				if (pChildWin.isCancelled()) {
					
					clickedContext.getChildrenStateMap().clear();
					clickedContext.setChildrenStateMap(pContClone.getChildrenStateMap());
					
					if (clickedContext != null) {					
						
						clickedContext.getChildrenContextMap().put(clickedTreeNode,
								pContClone);
						treeGrid.selectTreeNodeParent(clickedTreeNode);
					}					
					return;
				}

				

				if (clickedContext != null) {
					pChildWin_AllOpenedMap = clickedContext
							.getChildrenStateMap();					
				}
				if (pChildWin_AllOpenedMap == null) {
					pChildWin_AllOpenedMap = new HashMap<ArchiveGridTreeNode, FlashCheckBox>();
				}

				// STEP 1 - check state.
				if (!ArchivePagingContext
						.hasFullorPartialSelected(pChildWin_AllOpenedMap)) {
					// no children selected
					changeCurrentTreeNode(FlashCheckBox.NONE);
				} else if (pChildWin_AllOpenedMap.size() > 0) {
					int clickedNodeState = -1;
					if (clickedNodeCheckBox != null) {
						clickedNodeState = clickedNodeCheckBox
								.getSelectedState();
					}

					if (!isAllFullSelected(pChildWin_AllOpenedMap)) {
						changeCurrentTreeNode(FlashCheckBox.PARTIAL);
					} else {
						// all current opened children are
						// full selected.
						if (FlashCheckBox.FULL == clickedNodeState) {
							// hostnode still should be
							// full. Clear the selected
							// children since host node are
							// full selected
						} else if (FlashCheckBox.PARTIAL == clickedNodeState) {
							// do nothing. hostnode still
							// should be partial.
						} else if (FlashCheckBox.NONE == clickedNodeState) {
							changeCurrentTreeNode(FlashCheckBox.PARTIAL);
						}
					}
				}

				// STEP 2 - clean nouseness.
				clickedContext.cleanRedandant();
			}

			private void changeCurrentTreeNode(int state) {
				
				
				if (clickedNodeCheckBox != null) {
					if ((state == FlashCheckBox.PARTIAL
							|| state == FlashCheckBox.NONE || state == FlashCheckBox.FULL)
							&& state != clickedNodeCheckBox.getSelectedState()) {
						clickedNodeCheckBox.setSelectedState(state);
						
						if(state == FlashCheckBox.PARTIAL || state == FlashCheckBox.FULL)
						{
							if(clickedTreeNode != null)
							{
								clickedTreeNode.setChecked(true);
							}
						}
						
						
						
						if (treeGrid != null) {
							
							DeferredCommand.addCommand(new Command() {

								@Override
								public void execute() {
									treeGrid
											.selectTreeNodeParent(clickedTreeNode);
								}
							});

						}
					}
				}
			}
		});
		pChildWin.show();
	}

	public void setRestoreManager(boolean isRestoreManager) {
		this.isRestoreManager = isRestoreManager;
	}

	public boolean isRestoreManager() {
		return isRestoreManager;
	}
}
