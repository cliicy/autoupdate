package com.ca.arcflash.ui.client.restore.mailboxexplorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.restore.ExchangeGRTRecoveryPointsPanel;
import com.ca.arcflash.ui.client.restore.ExtEditorTreeGrid;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;

public class MailboxExplorerContext
{
	private GridTreeNode edbNode;            // EDB node 
	private FlashCheckBox edbCheckbox;       // EDB check box
	private ExtEditorTreeGrid extEditorTreeGrid;    	 // outer TreeGrid
	private boolean isRestoreManager = true; // true for restore manager, false
	private ExchangeGRTRecoveryPointsPanel recoveryPointsPanel;

	// global map, for easy getting the check box
	private HashMap<GridTreeNode, FlashCheckBox> mapNodeToCheckBox = new HashMap<GridTreeNode, FlashCheckBox>();

	// children map, keep the hierarchy for easy finding the children
	private HashMap<GridTreeNode, HashMap<GridTreeNode, FlashCheckBox>> mapNodeToChildrenMap = new HashMap<GridTreeNode, HashMap<GridTreeNode, FlashCheckBox>>();

	// for export recovery point
	public static void expandEDB(final GridTreeNode clickedTreeNode, final FlashCheckBox clickedNodeCheckbox,
			final HashMap<GridTreeNode, MailboxExplorerContext> mailboxContextMap, final ExtEditorTreeGrid treeGrid)
	{
		MailboxExplorerContext context = mailboxContextMap.get(clickedTreeNode);

		if (context == null)
		{
			context = new MailboxExplorerContext();
			context.setEdbNode(clickedTreeNode);
			context.setEdbCheckBox(clickedNodeCheckbox);
			context.setExtEditorTreeGrid(treeGrid);
			mailboxContextMap.put(clickedTreeNode, context);
		}

		final MailboxExplorerContext contextClone = context.clone();
		final MailboxExplorerWindow mbxWindow = new MailboxExplorerWindow(context);

		mbxWindow.setModal(true);
		mbxWindow.addWindowListener(new WindowListener()
		{
			@Override
			public void windowHide(WindowEvent we)
			{
				// restore the state if cancelled
				if (mbxWindow.isCancelled())
				{
					mailboxContextMap.put(clickedTreeNode, contextClone);
					return;
				}
				
				// update parent edb
				mbxWindow.getContext().updateParentSelection();
				
				// STEP 2 - clean useless nodes from the maps.
				mbxWindow.getContext().cleanRedandant(mbxWindow.getContext().getEdbNode());
			}			
		});

		mbxWindow.show();
	}
	
	public static void initExplorer(final MailboxExplorerPanel explorerPanel,
			final ExchangeGRTRecoveryPointsPanel recoveryPointsPanel)
	{
		if (explorerPanel != null && recoveryPointsPanel != null)
		{
			MailboxExplorerParameter para = new MailboxExplorerParameter();
			recoveryPointsPanel.getInfoForMailboxExplorer(para);			

			if (para.isValid())
			{
				MailboxExplorerContext context = para.mailboxContextMap.get(para.edbNode);

				if (context == null)
				{
					context = new MailboxExplorerContext();
					context.setEdbNode(para.edbNode);
					context.setEdbCheckBox(para.edbCheckbox);
					context.setExtEditorTreeGrid(para.treeGrid);
					context.setRecoveryPointsPanel(recoveryPointsPanel);
					para.mailboxContextMap.put(para.edbNode, context);
				}

				explorerPanel.setContext(context);

				explorerPanel.addListener(Events.Hide, new Listener<BaseEvent>()
				{
					@Override
					public void handleEvent(BaseEvent be)
					{
						// update parent edb
						//explorerPanel.getContext().updateParentSelection();						
					}
				});
			}
		}
	}
	
	public void updateParentSelection()
	{
		// calculate the parent node (EDB) state
		int newEdbState = calcParentNodeState(getEdbNode(),getEdbCheckBox().getSelectedState());

		// set the parent state if changed
		if (newEdbState != -1	&& newEdbState != getEdbCheckBox().getSelectedState())
		{
			getEdbCheckBox().setSelectedState(newEdbState);

			if (getExtEditorTreeGrid() != null)
			{
				getExtEditorTreeGrid().selectTreeNodeParent(getEdbNode());
			}
		}
	}

	// set the parent state directly
	public void updateParentSelection(int newEdbState)
	{
		// set the parent state if changed
		if (newEdbState != -1	&& newEdbState != getEdbCheckBox().getSelectedState())
		{
			getEdbCheckBox().setSelectedState(newEdbState);

			if (getExtEditorTreeGrid() != null)
			{
				getExtEditorTreeGrid().selectTreeNodeParent(getEdbNode());
			}
		}
	}

	////////////////////////////////////////////////
	// get and set
	public GridTreeNode getEdbNode()
	{
		return edbNode;
	}

	public void setEdbNode(GridTreeNode edbNode)
	{
		this.edbNode = edbNode;
	}

	public FlashCheckBox getEdbCheckBox()
	{
		return edbCheckbox;
	}

	public void setEdbCheckBox(FlashCheckBox edbCheckBox)
	{
		this.edbCheckbox = edbCheckBox;
		
		// put the edb check box into the map
		this.mapNodeToCheckBox.put(this.edbNode, this.edbCheckbox);
	}

	public ExtEditorTreeGrid getExtEditorTreeGrid()
	{
		return extEditorTreeGrid;
	}

	public void setExtEditorTreeGrid(ExtEditorTreeGrid treeGrid)
	{
		this.extEditorTreeGrid = treeGrid;
	}

	public boolean isRestoreManager()
	{
		return isRestoreManager;
	}

	public void setRestoreManager(boolean isRestoreManager)
	{
		this.isRestoreManager = isRestoreManager;
	}
	
	public HashMap<GridTreeNode, FlashCheckBox> getMapNodeToCheckBox()
	{
		return mapNodeToCheckBox;
	}

	public void setMapNodeToCheckBox(HashMap<GridTreeNode, FlashCheckBox> mapNodeToCheckBox)
	{
		this.mapNodeToCheckBox = mapNodeToCheckBox;
	}

	public HashMap<GridTreeNode, HashMap<GridTreeNode, FlashCheckBox>> getMapNodeToChildrenMap()
	{
		return mapNodeToChildrenMap;
	}

	public void setMapNodeToChildrenMap(HashMap<GridTreeNode, HashMap<GridTreeNode, FlashCheckBox>> mapNodeToChildrenMap)
	{
		this.mapNodeToChildrenMap = mapNodeToChildrenMap;
	}
	////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////
	// sync the check box state, call this when a new check box is added
	public FlashCheckBox createFlashCheckbox(GridTreeNode node, GridTreeNode parentNode, int parentNodeState)
	{
		FlashCheckBox fcb = new FlashCheckBox();
		FlashCheckBox temp = getCheckbox(node);
		
		int parentState = -1;
		// if the parentNodeState is not set, get the parent check box
		if (parentNodeState == -1)
		{
			// get parent node check box
			FlashCheckBox parentCheckBox = getCheckbox(parentNode);

			if (parentCheckBox != null)
			{
				parentState = parentCheckBox.getSelectedState();
			}
		}
		// if the parentNodeState is set, use it directly
		else
		{
			parentState = parentNodeState;
		}

				
		if (temp == null)
		{
			mapNodeToCheckBox.put(node, fcb);
			putChild(parentNode, node, fcb); // save to the child map too			

			// sync with parent check box
			if (parentState == FlashCheckBox.FULL)
			{
				fcb.setSelectedState(FlashCheckBox.FULL);
			}
		}
		else
		{
			mapNodeToCheckBox.remove(node);
			removeChild(parentNode, node);

			// sync with parent check box
			if (parentState != FlashCheckBox.PARTIAL)
			{
				fcb.setSelectedState(parentState);
			}
			else
			{
				fcb.setSelectedState(temp.getSelectedState());
			}

			fcb.setEnabled(temp.isEnabled());

			mapNodeToCheckBox.put(node, fcb);
			putChild(parentNode, node, fcb);
		}

		return fcb;
	}

	// get check box from the global map
	public FlashCheckBox getCheckbox(GridTreeNode node)
	{
		FlashCheckBox fcb = null;
		
		if (node != null)
		{
			fcb = mapNodeToCheckBox.get(node);
		}

		return fcb;
	}
	///////////////////////////////////////////////////
	

	// //////////////////////////////////
	// manage check box in children map
	protected void putChild(GridTreeNode parentNode, GridTreeNode childNode, FlashCheckBox childCheckbox)
	{
		if (parentNode != null && childNode != null && childCheckbox != null)
		{
			HashMap<GridTreeNode, FlashCheckBox> map = mapNodeToChildrenMap.get(parentNode);

			if (map == null)
			{
				map = new HashMap<GridTreeNode, FlashCheckBox>();
				mapNodeToChildrenMap.put(parentNode, map);
			}

			if (map != null)
			{
				map.put(childNode, childCheckbox);
			}
		}
	}

	public Iterator<GridTreeNode> getChildrenIterator(GridTreeNode parentNode)
	{
		Iterator<GridTreeNode> iterator = null;

		if (parentNode != null)
		{
			HashMap<GridTreeNode, FlashCheckBox> map = mapNodeToChildrenMap.get(parentNode);

			if (map != null)
			{
				iterator = map.keySet().iterator();
			}
		}

		return iterator;
	}

	protected void removeChild(GridTreeNode parentNode, GridTreeNode childNode)
	{
		if (parentNode != null && childNode != null)
		{
			HashMap<GridTreeNode, FlashCheckBox> map = mapNodeToChildrenMap.get(parentNode);

			if (map != null)
			{
				map.remove(childNode);
			}
		}
	}
	
	public void cleanRedandant(GridTreeNode node)
	{
		if (node != null)
		{
			FlashCheckBox fcb = getCheckbox(node);
			
			if (fcb != null)
			{
				int selectState = fcb.getSelectedState();
				
				if (selectState != FlashCheckBox.PARTIAL)
				{
					removeChildrenFromMaps(node, true);				
				}
				else
				{
					Iterator<GridTreeNode> iterator = getChildrenIterator(node);
					while (iterator != null && iterator.hasNext())
					{
						cleanRedandant(iterator.next());
					}
				}
			}
		}
		
	}
	
	protected void removeChildrenFromMaps(GridTreeNode node, boolean keepCurrent)
	{
		if (node != null)
		{	
			if (!keepCurrent)
			{
				mapNodeToCheckBox.remove(node);
			}
			
			Iterator<GridTreeNode> iterator = getChildrenIterator(node);
			while (iterator != null && iterator.hasNext())
			{
				removeChildrenFromMaps(iterator.next(), false);
			}
			
			mapNodeToChildrenMap.remove(node);
		}
	}
	
	// this calculate the node state based on the children and itself
	// it is used when the children are fetched in paging (we don't have all children) 
	public int calcParentNodeState(GridTreeNode parentNode, int parentNodeState)
	{
		int retParentState = -1;
		
		// need the parent node state
		int parentNodeInitState = -1;

		// if the parentNodeState is not set, get the parent check box
		if (parentNodeState == -1)
		{
			// get parent node check box
			FlashCheckBox parentCheckBox = getCheckbox(parentNode);

			if (parentCheckBox != null)
			{
				parentNodeInitState = parentCheckBox.getSelectedState();
			}
		}
		// if the parentNodeState is set, use it directly
		else
		{
			parentNodeInitState = parentNodeState;
		}
		
		
		// calculate the parent state
		if (!hasFullorPartialSelected(parentNode))
		{
			// all current opened children are not selected.
			// there may have some nodes not fetched, so the result may not be none
			// but we cannot package those not fetched node either, so make the result none.
			retParentState = FlashCheckBox.NONE;			
			
//			switch (parentNodeInitState)
//			{
//			case FlashCheckBox.FULL:
//				retParentState = FlashCheckBox.PARTIAL;
//				break;
//			case FlashCheckBox.PARTIAL:
//				retParentState = FlashCheckBox.PARTIAL;
//				break;
//			case FlashCheckBox.NONE:
//				retParentState = FlashCheckBox.NONE;
//				break;
//			default:
//				retParentState = parentNodeInitState;
//				break;
//			}			
		}
		else 
		{
			Iterator<GridTreeNode> iterator = getChildrenIterator(parentNode);
			if (iterator != null && iterator.hasNext())
			{
				if (!isAllFullSelected(parentNode))
				{
					retParentState = FlashCheckBox.PARTIAL;
				}
				else
				{
					// all current opened children are full selected.
					switch (parentNodeInitState)
					{
					case FlashCheckBox.FULL:
						retParentState = FlashCheckBox.FULL;
						break;
					case FlashCheckBox.PARTIAL:
						retParentState = FlashCheckBox.PARTIAL;
						break;
					case FlashCheckBox.NONE:
						retParentState = FlashCheckBox.PARTIAL;
						break;
					default:
						retParentState = parentNodeInitState;
						break;
					}					
				}
			}			
		}
		
		return retParentState;
	}
	
	// -1: invalid
	// FlashCheckBox.NONE:    all not selected
	// FlashCheckBox.PARTIAL: other
	// FlashCheckBox.FULL:    all full selected
	public int calcChildrenState(GridTreeNode parentNode, Iterator<GridTreeNode> childrenIterator)
	{
		int nRet = -1;
		
		if (parentNode != null)
		{	
			int total = 0, full = 0, partial = 0, none = 0, invalid = 0;
			
			Iterator<GridTreeNode> iterator = childrenIterator == null ? getChildrenIterator(parentNode) : childrenIterator;
			
			while (iterator != null && iterator.hasNext())
			{
				total ++;
				FlashCheckBox fcb = getCheckbox(iterator.next());
				
				if (fcb != null)
				{
					switch (fcb.getSelectedState())
					{
					case FlashCheckBox.FULL:
						full++;
						break;
					case FlashCheckBox.PARTIAL:
						partial++;
						break;
					case FlashCheckBox.NONE:
						none++;
						break;
					default:
						invalid++;
						break;
					}	
				}
				else
				{
					invalid++;
				}
				
				// if partial here, no need to check the following nodes 
				if (partial > 0)
				{
					break;
				}
			}	
			
			if (total == full)
			{
				nRet = FlashCheckBox.FULL;
			}
			else if (total == none + invalid)
			{
				nRet = FlashCheckBox.NONE;
			}
			else
			{
				nRet = FlashCheckBox.PARTIAL;
			}			
		}
		
		return nRet;
	}
	
	protected boolean hasFullorPartialSelected(GridTreeNode parentNode)
	{
		if (parentNode == null)
		{
			return false;
		}
		
		boolean hasFullorPartialSelected = false;		
		Iterator<GridTreeNode> iterator = getChildrenIterator(parentNode);
		
		while (iterator != null && iterator.hasNext())
		{				
			FlashCheckBox fcb = getCheckbox(iterator.next());
			if (fcb != null	&& (fcb.getSelectedState() == FlashCheckBox.FULL || fcb.getSelectedState() == FlashCheckBox.PARTIAL))
			{
				hasFullorPartialSelected = true;
				break;
			}			
		}		

		return hasFullorPartialSelected;
	}

	protected boolean isAllFullSelected(GridTreeNode parentNode)
	{
		if (parentNode == null)
		{
			return false;
		}
		
		boolean isAllFullSelected = true;

		Iterator<GridTreeNode> iterator = getChildrenIterator(parentNode);

		while (iterator != null && iterator.hasNext())
		{
			FlashCheckBox fcb = getCheckbox(iterator.next());
			if (fcb != null && (fcb.getSelectedState() != FlashCheckBox.FULL))
			{
				isAllFullSelected = false;
				break;
			}
		}			
		
		return isAllFullSelected;
	}
	
	////////////////////////////////////////////////
	public boolean equals(MailboxExplorerContext other)
	{
		boolean bEqual = false;
		
		if (other != null)
		{
			if (this.edbNode != null && edbNode.equals(other.getEdbNode()))
			{
				if (edbCheckbox != null && other.getEdbCheckBox() != null)
				{
					if (edbCheckbox.getSelectedState() == other.getEdbCheckBox().getSelectedState())
					{
						bEqual = true;
					}
				}
			}
		}
		
		return bEqual;
	}

	///////////////////////////////////////////////
	// clone the context
	public MailboxExplorerContext clone()
	{
		MailboxExplorerContext newCont = new MailboxExplorerContext();

		newCont.setEdbNode(edbNode);
		newCont.setEdbCheckBox(edbCheckbox);
		newCont.setRestoreManager(isRestoreManager);
		newCont.setExtEditorTreeGrid(extEditorTreeGrid);

		HashMap<GridTreeNode, FlashCheckBox> map = mapNodeToCheckBox;
		if (map != null && map.size() > 0)
		{
			newCont.setMapNodeToCheckBox(cloneMap(map));
		}

		HashMap<GridTreeNode, HashMap<GridTreeNode, FlashCheckBox>> mapmap = mapNodeToChildrenMap;

		if (mapmap != null && mapmap.size() > 0)
		{
			newCont.setMapNodeToChildrenMap(cloneMapmap(mapmap));
		}
		return newCont;
	}

	protected HashMap<GridTreeNode, FlashCheckBox> cloneMap(HashMap<GridTreeNode, FlashCheckBox> map)
	{
		HashMap<GridTreeNode, FlashCheckBox> newMap = new HashMap<GridTreeNode, FlashCheckBox>();
		if (map != null)
		{
			Iterator<GridTreeNode> it = map.keySet().iterator();
			while (it.hasNext())
			{
				GridTreeNode node = it.next();
				FlashCheckBox box = map.get(node);
				if (box != null)
				{
					FlashCheckBox newBox = new FlashCheckBox();
					newBox.setSelectedState(box.getSelectedState());
					newMap.put(node, newBox);
				}
			}
		}

		return newMap;
	}

	protected HashMap<GridTreeNode, HashMap<GridTreeNode, FlashCheckBox>> cloneMapmap(
			HashMap<GridTreeNode, HashMap<GridTreeNode, FlashCheckBox>> mapmap)
	{
		HashMap<GridTreeNode, HashMap<GridTreeNode, FlashCheckBox>> newMapmap = new HashMap<GridTreeNode, HashMap<GridTreeNode, FlashCheckBox>>();
		if (mapmap != null)
		{
			Iterator<GridTreeNode> it = mapmap.keySet().iterator();
			while (it.hasNext())
			{
				GridTreeNode node = it.next();
				HashMap<GridTreeNode, FlashCheckBox> map = mapmap.get(node);
				if (map != null)
				{
					HashMap<GridTreeNode, FlashCheckBox> newMap = cloneMap(map);
					newMapmap.put(node, newMap);
				}
			}
		}

		return newMapmap;
	}
	////////////////////////////////////////////////////////
	
	public List<GridTreeNode> getSelectedNodes()
	{
		//cleanRedandant(edbNode);		
		return getSelectedNodes(edbNode, false);
	}
	
	protected ArrayList<GridTreeNode> getSelectedNodes(GridTreeNode node, boolean addCurrent)
	{
		ArrayList<GridTreeNode> selectedNodes = new ArrayList<GridTreeNode>();
		
		FlashCheckBox fcb = getCheckbox(node);
		
		if (fcb != null)
		{
			if (fcb.getSelectedState() == FlashCheckBox.FULL && addCurrent)
			 {
				 selectedNodes.add(node);						 
			 }
			 else if (fcb.getSelectedState() == FlashCheckBox.PARTIAL)
			 {
				 Iterator<GridTreeNode> iterator = getChildrenIterator(node);
				 
				 while (iterator != null && iterator.hasNext())
				 {
					 selectedNodes.addAll(getSelectedNodes(iterator.next(), true));
				 }				 
			 }
		}		
		
		return selectedNodes;
	}

	// return the selected count of direct children
	public int getSelectedChildrenCount(GridTreeNode parentNode)
	{
		int nSelected = 0;

		Iterator<GridTreeNode> iterator = getChildrenIterator(parentNode);

		while (iterator != null && iterator.hasNext())
		{
			FlashCheckBox fcb = getCheckbox(iterator.next());
			if (fcb != null && (fcb.getSelectedState() == FlashCheckBox.FULL))
			{
				nSelected++;
			}
		}

		return nSelected;
	}

	public ExchangeGRTRecoveryPointsPanel getRecoveryPointsPanel()
	{
		return recoveryPointsPanel;
	}

	public void setRecoveryPointsPanel(ExchangeGRTRecoveryPointsPanel recoveryPointsPanel)
	{
		this.recoveryPointsPanel = recoveryPointsPanel;
	}
	
}
