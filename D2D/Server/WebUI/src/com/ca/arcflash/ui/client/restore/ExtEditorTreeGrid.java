package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RestoreJobType;
import com.ca.arcflash.ui.client.restore.mailboxexplorer.MailboxExplorerContext;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class ExtEditorTreeGrid<M extends ModelData> extends EditorTreeGrid<M> {
	LoginServiceAsync service = GWT.create(LoginService.class);	

	@SuppressWarnings("unchecked")
	public ExtEditorTreeGrid(TreeStore store, ColumnModel cm,
			HashMap<GridTreeNode, FlashCheckBox> table, boolean isRestManager, boolean isExchangeGRTPanel) {
		super(store, cm);
		
		// set a normal selection model, otherwise the it will be CellTreeGridSelectionModel
		if (isExchangeGRTPanel)
		{
			setSelectionModel(new TreeGridSelectionModel<M>());
		}
		
		this.table = table;
		this.isRestManager = isRestManager;
		this.isExchangeGRTPanel = isExchangeGRTPanel;
	}

	private HashMap<GridTreeNode, FlashCheckBox> table;
	private boolean isRestManager;

	private HashMap<GridTreeNode, PagingContext> nodeContextMap = new HashMap<GridTreeNode, PagingContext>();
	private HashMap<GridTreeNode, MailboxExplorerContext> mailboxContextMap = new HashMap<GridTreeNode, MailboxExplorerContext>();
	private boolean isExchangeGRTPanel = false;	

	public void clean(GridTreeNode node) {
		nodeContextMap.remove(node);
		mailboxContextMap.remove(node);
	}

	@Override
	protected void onClick(GridEvent<M> e) {
		M m = e.getModel();
		
		if (m != null) {
			TreeNode node = findNode(m);
			if (node != null)
			{
				Element jointEl = treeGridView.getJointElement(node);
				if (jointEl != null && e.within(jointEl))
				{
					if (m instanceof GridTreeNode)
					{
						final GridTreeNode treeNode = (GridTreeNode) m;
						
						// show Mailbox Explorer here
						if (isExchGRTRoot(treeNode))
						{
							if (isExchangeGRTPanel)
							{
								//showMailboxExplorer(treeNode);
							}							
						}else if(isRefsNotMount(treeNode) == true){
							showErrorMessage(treeNode.getName());
						}
						else
						{
							handleFSNodeClick(treeNode);
						}

					}
					else
					{
						toggle(m);
					}
				}
				else
				{					
					super.onClick(e);
				}
			}
		}
	}

	private boolean isRefsNotMount(GridTreeNode node){
		if(node.getIsRefs() == null){
			return false;
		}
		return node.getIsRefs() && !UIContext.serverVersionInfo.isReFsSupported();// UIContext.serverVersionInfo.isWin8();
	}
	
	private void showErrorMessage(String volumeName){
		MessageBox errMsg = new MessageBox();
		errMsg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
		errMsg.setMessage(UIContext.Messages.restoreSourceIsRefsVolumeCannotMount(volumeName,UIContext.serverVersionInfo.getOsName()));
		errMsg.setModal(true);
		errMsg.setIcon(MessageBox.ERROR);
		Utils.setMessageBoxDebugId(errMsg);
		errMsg.show();
	}
	
	protected void handleFSNodeClick(final GridTreeNode treeNode) {
		handleFSNodeClick2(treeNode);
	}
	
	public void handleFSNodeClick2(final GridTreeNode treeNode){
		if(this.isFileSystem(treeNode) && treeNode.getParentID() == -1 && treeNode.getChildrenCount() <= 0) {
			TreeNode node = this.findNode((M)treeNode);
			if(node != null && !node.isExpanded()){
				if(treeNode.getCatalogFilePath().endsWith("$DISABLED$")){
					this.mask(UIContext.Constants.waitForMountingFinish());
				}else{
					this.mask(UIContext.Constants.loadingIndicatorText());
				}
			}
			
			service.getRecoveryPointItemChildrenCount(treeNode,
					new BaseAsyncCallback<Long>() {
					
						@Override
						public void onFailure(Throwable caught) {
							unmask();
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(Long result) {
							treeNode.setChildrenCount(result);
							if(result == 0 || result > PagingContext.PAGETHRESHOLD){
								unmask();
							}
							handleFSPaging(treeNode);
						}
				
			});
		}else {
			handleFSPaging(treeNode);
		}
	}
	
	protected void handleFSPaging(final GridTreeNode treeNode) {		
		if (isPagingRequired(treeNode))
		{
			handlePaging(treeNode);
		}
		else
		{
			toggle((M)treeNode);
		}
	}
	
	private boolean isExchGRTRoot(GridTreeNode treeNode) {
		boolean bRoot = false;
		
		if (treeNode != null && treeNode.getType() != null) 
		{
			bRoot = CatalogModelType.rootGRTExchangeTypes.contains(treeNode.getType().intValue());
		}
		
		return bRoot;
	}
	
	private void showMailboxExplorer(GridTreeNode treeNode)
	{
		FlashCheckBox clickedNodeCheckBox = table.get(treeNode);
		if (clickedNodeCheckBox == null) {
			clickedNodeCheckBox = new FlashCheckBox();
			table.put(treeNode, clickedNodeCheckBox);
		}
		
		// get the backup destination and session id from parent node (cannot access the RecoveryPointsPanel here)
		GridTreeNode parentNode = (GridTreeNode) this.getTreeStore().getParent((M) treeNode);
		if (parentNode != null)
		{
			treeNode.setBackupDestination(parentNode.getBackupDestination());
			treeNode.setSessionID(parentNode.getSessionID());
		}		

		MailboxExplorerContext.expandEDB(treeNode, clickedNodeCheckBox, mailboxContextMap, this);		
	}

	private boolean isPagingRequired(final GridTreeNode treeNode) {
		return (isFileSystem(treeNode) || isExchGRT(treeNode))
				&& treeNode.getChildrenCount() != null
				&& treeNode.getChildrenCount() > PagingContext.PAGETHRESHOLD;
	}	

	private boolean isExchGRT(GridTreeNode treeNode) {
		if (treeNode != null && treeNode.getType() != null) {
			return CatalogModelType.allGRTExchangeTypes.contains(treeNode
					.getType());
		}
		return false;
	}

	private void handlePaging(GridTreeNode treeNode) {
		FlashCheckBox clickedNodeCheckBox = table.get(treeNode);
		if (clickedNodeCheckBox == null) {
			clickedNodeCheckBox = new FlashCheckBox();
			table.put(treeNode, clickedNodeCheckBox);
		}

		GridTreeNode edbNode = RestoreUtil.findAncestorEDBItem((TreeStore<GridTreeNode>)treeStore, treeNode);
		GridTreeNode mbNode =  RestoreUtil.findAncestorMailboxLevelItem((TreeStore<GridTreeNode>)treeStore, treeNode);
		if(edbNode != null){
			treeNode.getReferNode().add(edbNode);
		}
		if(mbNode != null){
			treeNode.getReferNode().add(mbNode);
		}
		PagingContext.handleClick(treeNode, nodeContextMap, null,
				clickedNodeCheckBox, true, isRestManager, this);		
	}

	public HashMap<GridTreeNode, FlashCheckBox> getTable() {
		return table;
	}

	public void setTable(HashMap<GridTreeNode, FlashCheckBox> table) {
		this.table = table;
	}

	public HashMap<GridTreeNode, PagingContext> getNodeContextMap() {
		return nodeContextMap;
	}

	public void setNodeContextMap(
			HashMap<GridTreeNode, PagingContext> nodeContextMap) {
		this.nodeContextMap = nodeContextMap;
	}

	public List<GridTreeNode> getPagedSelectedNodes() {
		Set<GridTreeNode> pagedSelected = new HashSet<GridTreeNode>();
		Iterator<GridTreeNode> contIt = nodeContextMap.keySet().iterator();
		while (contIt.hasNext()) {
			GridTreeNode contNode = contIt.next();
			PagingContext childCont = nodeContextMap.get(contNode);
			if (childCont != null) {
				List<GridTreeNode> lst = childCont.getSelectedNodes();
				pagedSelected.addAll(lst);
			}
		}
		List<GridTreeNode> ret = new ArrayList<GridTreeNode>();
		ret.addAll(pagedSelected);
		return ret;
	}

	protected boolean isFileSystem(GridTreeNode node) {
		List<GridTreeNode> selectedNodes = new ArrayList<GridTreeNode>();
		selectedNodes.add(node);
		RestoreJobType type = RestoreUtil.getJobType(selectedNodes);
		if (type != null && type == RestoreJobType.FileSystem) {
			return true;
		}
		return false;
	}

	@Override
	protected void onDoubleClick(GridEvent<M> e) 
    {
		if (editSupport.onDoubleClick(e))
		{
			return;
		}
		M m = e.getModel();

//		boolean isOpenPaging = false;
//		boolean isOpenMailboxExplorer = false;
		if (m instanceof GridTreeNode)
		{
			final GridTreeNode treeNode = (GridTreeNode) m;

			// show Mailbox Explorer here
			if (isExchGRTRoot(treeNode))
			{
				if (isExchangeGRTPanel)
				{
//					isOpenMailboxExplorer = true;					
				}else {
					super.onDoubleClick(e);
				}
			}
			else if(isRefsNotMount(treeNode) == true){
				showErrorMessage(treeNode.getName());
			}
			else
			{
				handleFSNodeClick(treeNode);
			}
		}else {
			super.onDoubleClick(e);
		}

/*		if (isOpenPaging)
		{
			handlePaging((GridTreeNode) m);
		}
		else if (isOpenMailboxExplorer)
		{
			//showMailboxExplorer((GridTreeNode) m);
		}
		else
		{
			super.onDoubleClick(e);
		}
*/	}
	
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
				// Parent changed, change the parent's parent
				selectTreeNodeParent(parent);
			}
		}
	}

	public HashMap<GridTreeNode, MailboxExplorerContext> getMailboxContextMap()
	{
		return mailboxContextMap;
	}

	public void setMailboxContextMap(HashMap<GridTreeNode, MailboxExplorerContext> mailboxContextMap)
	{
		this.mailboxContextMap = mailboxContextMap;
	}
	
	public List<GridTreeNode> getExchangeGRTSelectedNodes()
	{
		List<GridTreeNode> grtSelected = new ArrayList<GridTreeNode>();
		Iterator<GridTreeNode> contIt = mailboxContextMap.keySet().iterator();
		while (contIt.hasNext())
		{
			GridTreeNode contNode = contIt.next();
			MailboxExplorerContext childCont = mailboxContextMap.get(contNode);
			if (childCont != null)
			{
				List<GridTreeNode> lst = childCont.getSelectedNodes();
				grtSelected.addAll(lst);
			}
		}
		return grtSelected;
	}
}
