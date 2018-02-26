package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class PagingContext {
	private GridTreeNode parent;
	private FlashCheckBox parentCheckBox;
	private HashMap<GridTreeNode, PagingContext> childrenContextMap = new HashMap<GridTreeNode, PagingContext>();
	private HashMap<GridTreeNode, FlashCheckBox> childrenStateMap = new HashMap<GridTreeNode, FlashCheckBox>();
	private boolean isRestoreManager;

	public final static int DEFAULTPAGESIZE = 25;
	public final static int PAGETHRESHOLD = 50;

	public int calcPages(int pageSize) {
		long cnt = parent.getChildrenCount();
		int num = (int) Math.ceil(cnt * 1.0 / pageSize);
		return num;
	}

	public HashMap<GridTreeNode, PagingContext> getChildrenContextMap() {
		return childrenContextMap;
	}

	public void setChildrenContextMap(
			HashMap<GridTreeNode, PagingContext> childrenContextMap) {
		this.childrenContextMap = childrenContextMap;
	}

	public HashMap<GridTreeNode, FlashCheckBox> getChildrenStateMap() {
		return childrenStateMap;
	}

	public void setChildrenStateMap(
			HashMap<GridTreeNode, FlashCheckBox> childrenStateMap) {
		this.childrenStateMap = childrenStateMap;
	}

	public void setParent(GridTreeNode parent) {
		this.parent = parent;
	}

	public GridTreeNode getParent() {
		return parent;
	}

	public void clearCurrentAndChildren() {
		childrenContextMap.clear();// don't care children
		childrenStateMap.clear();
	}

	public List<GridTreeNode> getSelectedNodes() {
		cleanRedandant();

		ArrayList<GridTreeNode> selectedNodes = new ArrayList<GridTreeNode>();
		if (parentCheckBox != null) {
			if (parentCheckBox.getSelectedState() == FlashCheckBox.PARTIAL) {
				HashMap<GridTreeNode, FlashCheckBox> map = this
						.getChildrenStateMap();
				if (map != null && map.size() > 0) {
					Iterator<GridTreeNode> it = map.keySet().iterator();
					while (it.hasNext()) {
						GridTreeNode node = it.next();
						FlashCheckBox box = map.get(node);
						if (box != null) {
							if (box.getSelectedState() == FlashCheckBox.FULL) {
								selectedNodes.add(node);
							} else if (box.getSelectedState() == FlashCheckBox.PARTIAL) {
								if (this.getChildrenContextMap() != null) {
									ArrayList<GridTreeNode> nextLevelChildren = getChildrenContextSelection();
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

	private ArrayList<GridTreeNode> getChildrenContextSelection() {
		ArrayList<GridTreeNode> selectedChildren = new ArrayList<GridTreeNode>();

		HashMap<GridTreeNode, PagingContext> contMap = this
				.getChildrenContextMap();

		if (contMap == null || contMap.size() == 0) {
			return selectedChildren;
		}

		Iterator<GridTreeNode> contIt = contMap.keySet().iterator();
		while (contIt.hasNext()) {
			GridTreeNode contNode = contIt.next();
			PagingContext childCont = contMap.get(contNode);
			if (childCont != null) {
				List<GridTreeNode> lst = childCont.getSelectedNodes();
				selectedChildren.addAll(lst);
			}
		}
		return selectedChildren;
	}

	private void cleanRedandant() {
		ArrayList<GridTreeNode> removeContextNodes = new ArrayList<GridTreeNode>();
		ArrayList<GridTreeNode> removeNodes = new ArrayList<GridTreeNode>();
		if (parentCheckBox != null) {
			if (parentCheckBox.getSelectedState() == FlashCheckBox.FULL
					|| parentCheckBox.getSelectedState() == FlashCheckBox.NONE) {
				clearCurrentAndChildren();
			} else if (parentCheckBox.getSelectedState() == FlashCheckBox.PARTIAL) {
				// parent is Partial
				HashMap<GridTreeNode, FlashCheckBox> map = this
						.getChildrenStateMap();
				if (map != null && map.size() > 0) {
					Iterator<GridTreeNode> it = map.keySet().iterator();
					while (it.hasNext()) {
						GridTreeNode node = it.next();
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

		for (GridTreeNode node : removeContextNodes) {
			childrenContextMap.remove(node);
		}

		for (GridTreeNode node : removeNodes) {
			childrenContextMap.remove(node);
			childrenStateMap.remove(node);
		}
	}

	private void cleanChildrenContext() {
		if (this.getChildrenContextMap() == null) {
			return;
		}

		HashMap<GridTreeNode, PagingContext> contMap = this
				.getChildrenContextMap();

		Iterator<GridTreeNode> contIt = contMap.keySet().iterator();
		while (contIt.hasNext()) {
			GridTreeNode contNode = contIt.next();
			PagingContext childCont = contMap.get(contNode);
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

	public PagingContext clone() {
		PagingContext newCont = new PagingContext();
		newCont.setParent(parent);
		newCont.setParentCheckBox(parentCheckBox);

		HashMap<GridTreeNode, FlashCheckBox> thisNSmap = this
				.getChildrenStateMap();
		if (thisNSmap != null && thisNSmap.size() > 0) {
			HashMap<GridTreeNode, FlashCheckBox> newStateMap = cloneMap(thisNSmap);
			newCont.setChildrenStateMap(newStateMap);
		}

		HashMap<GridTreeNode, PagingContext> thisNCmap = this
				.getChildrenContextMap();

		if (thisNCmap != null && thisNCmap.size() > 0) {
			HashMap<GridTreeNode, PagingContext> newContextMap = cloneContextMap(thisNCmap);
			newCont.setChildrenContextMap(newContextMap);
		}
		return newCont;
	}

	public static HashMap<GridTreeNode, PagingContext> cloneContextMap(
			HashMap<GridTreeNode, PagingContext> map) {
		HashMap<GridTreeNode, PagingContext> newMap = new HashMap<GridTreeNode, PagingContext>();
		if (map != null) {
			Iterator<GridTreeNode> it = map.keySet().iterator();
			while (it.hasNext()) {
				GridTreeNode node = it.next();
				PagingContext childCont = map.get(node);
				if (childCont != null) {
					PagingContext newChildCont = childCont.clone();
					newMap.put(node, newChildCont);
				}
			}
		}

		return newMap;
	}

	public static HashMap<GridTreeNode, FlashCheckBox> cloneMap(
			HashMap<GridTreeNode, FlashCheckBox> map) {
		HashMap<GridTreeNode, FlashCheckBox> newMap = new HashMap<GridTreeNode, FlashCheckBox>();
		if (map != null) {
			Iterator<GridTreeNode> it = map.keySet().iterator();
			while (it.hasNext()) {
				GridTreeNode node = it.next();
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

	public static Set<GridTreeNode> cloneSet(Set<GridTreeNode> set) {
		Map<GridTreeNode, Object> newMap = new HashMap<GridTreeNode, Object>();
		if (set != null) {
			Iterator<GridTreeNode> it = set.iterator();
			while (it.hasNext()) {
				newMap.put(it.next(), null);
			}
		}
		return newMap.keySet();
	}

	public static boolean hasFullorPartialSelected(
			HashMap<GridTreeNode, FlashCheckBox> map) {
		if (map == null) {
			return false;
		}
		boolean hasFullorPartialSelected = false;
		Iterator<GridTreeNode> it = map.keySet().iterator();
		while (it.hasNext()) {
			GridTreeNode node = it.next();
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
			HashMap<GridTreeNode, FlashCheckBox> map) {
		if (map == null) {
			return false;
		}
		boolean isAllFullSelected = true;
		Iterator<GridTreeNode> it = map.keySet().iterator();
		while (it.hasNext()) {
			GridTreeNode node = it.next();
			FlashCheckBox box = map.get(node);
			if (box != null && (box.getSelectedState() != FlashCheckBox.FULL)) {
				isAllFullSelected = false;
				break;
			}
		}

		return isAllFullSelected;
	}

	public static HashMap<GridTreeNode, Set<GridTreeNode>> cloneMapSet(
			HashMap<GridTreeNode, Set<GridTreeNode>> map) {
		HashMap<GridTreeNode, Set<GridTreeNode>> newMap = new HashMap<GridTreeNode, Set<GridTreeNode>>();
		if (map != null) {
			Iterator<GridTreeNode> it = map.keySet().iterator();
			while (it.hasNext()) {
				GridTreeNode node = it.next();
				Set<GridTreeNode> set = map.get(node);
				if (set != null) {
					newMap.put(node, cloneSet(set));
				}
			}
		}

		return newMap;
	}

	public static void handleClick(final GridTreeNode clickedTreeNode,
			final HashMap<GridTreeNode, PagingContext> nodeContextMap,
			final PagingBrowsePanel clickOnPanel,
			final FlashCheckBox clickedNodeCheckBox, final boolean isFromTree,
			boolean isRestoreManager, final ExtEditorTreeGrid treeGrid) {

		if (clickedTreeNode == null) {
			return;
		}

		if (isFromTree) {
			if (clickedTreeNode.getChildrenCount() <= PagingContext.PAGETHRESHOLD) {
				return;
			}
		} else {
			if (clickedTreeNode.getType() != CatalogModelType.Folder && !(CatalogModelType.allGRTExchangeTypes.contains(clickedTreeNode.getType()) 
					&& clickedTreeNode.getType() != CatalogModelType.OT_GRT_EXCH_MESSAGE)) {
				return;
			}
		}

		PagingContext pContext = nodeContextMap.get(clickedTreeNode);

		if (pContext == null) {
			pContext = new PagingContext();
			pContext.setRestoreManager(isRestoreManager);
			pContext.setParent(clickedTreeNode);
			pContext.setParentCheckBox(clickedNodeCheckBox);
			nodeContextMap.put(clickedTreeNode, pContext);
		}

		final PagingContext clickedContext = pContext;

		final PagingContext pContClone = clickedContext.clone();

		final PagingBrowseWindow pChildWin = new PagingBrowseWindow(
				clickedContext);
		if (isFromTree
				&& CatalogModelType.allGRTExchangeTypes
						.contains(clickedTreeNode.getType())) {
			String s = "";
			String heading = pChildWin.getHeadingHtml();
			if (heading != null) {
				int indx = heading.lastIndexOf("\\");
				if (indx > 0) {
					s = heading.substring(0, indx);
					indx = s.lastIndexOf("\\");
				}
				if (indx > 0) {
					s = heading.substring(indx + 1);
				}
			}
			if (s != null && s.trim().length() > 0) {
				pChildWin.setHeadingHtml(UIContext.Messages.browse(s));
			}
		}
		if (clickOnPanel != null) {
			int x = clickOnPanel.getAbsoluteLeft();
			int y = clickOnPanel.getAbsoluteTop();
			pChildWin.setPagePosition(x, y);
		}
		pChildWin.setModal(true);
		pChildWin.addWindowListener(new WindowListener() {
			@Override
			public void windowHide(WindowEvent we) {
				if (pChildWin.isCancelled()) {
					clickedContext.getChildrenContextMap().put(clickedTreeNode,
							pContClone);
					return;
				}

				HashMap<GridTreeNode, FlashCheckBox> pChildWin_AllOpenedMap = null;

				if (clickedContext != null) {
					pChildWin_AllOpenedMap = clickedContext
							.getChildrenStateMap();
				}
				if (pChildWin_AllOpenedMap == null) {
					pChildWin_AllOpenedMap = new HashMap<GridTreeNode, FlashCheckBox>();
				}

				// STEP 1 - check state.
				if (!PagingContext
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
