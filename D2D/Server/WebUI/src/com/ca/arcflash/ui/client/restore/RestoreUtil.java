package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.CatalogItemModel;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.EncrypedRecoveryPoint;
import com.ca.arcflash.ui.client.model.ExchVersion;
import com.ca.arcflash.ui.client.model.GRTCatalogItemModel;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.MsgSearchRecModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.RestoreJobExchSubItemModel;
import com.ca.arcflash.ui.client.model.RestoreJobItemEntryModel;
import com.ca.arcflash.ui.client.model.RestoreJobItemModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.RestoreJobNodeModel;
import com.ca.arcflash.ui.client.model.RestoreJobType;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.i18n.client.NumberFormat;

public class RestoreUtil {

	public final static NumberFormat sizeFormat = NumberFormat
			.getFormat("###,###.##");

	private static boolean nodeIsRSG(GridTreeNode node) {
		Integer nodeType = node.getType();
		if (nodeType != null
				&& CatalogModelType.OT_VSS_EXCH_LOGICALPATH == nodeType) {
			return true;
		}
		return false;
	}

	public static List<GridTreeNode> packageRSGChildren() {

		RestoreJobModel model = RestoreContext.getRestoreModel();

		Integer destType = model.getDestType();

		if (destType == null
				|| destType.intValue() != DestType.ExchRestore2RSG.getValue()) {
			return null;
		}

		ExchVersion exchVersion = RestoreContext.getExchVersion();

		if (exchVersion == null
				|| exchVersion.getVersion() != ExchVersion.Exch2007
						.getVersion()) {
			return null;
		}

		boolean isPackageRSGChildren = false;

		List<GridTreeNode> selectedNodes = RestoreContext
				.getRestoreRecvPointSources();

		TreeStore<GridTreeNode> treeStore = RestoreContext
				.getRestoreRecvPointTreeStore();

		List<GridTreeNode> publicFolderDbs = new ArrayList<GridTreeNode>();
		List<GridTreeNode> childrenExceptPublic = new ArrayList<GridTreeNode>();

		if (selectedNodes != null) {
			if (selectedNodes.size() == 1) {
				isPackageRSGChildren = nodeIsRSG(selectedNodes.get(0));
				if (isPackageRSGChildren) {
					isPackageRSGChildren = false;
					List<GridTreeNode> children = treeStore
							.getChildren(selectedNodes.get(0));

					if (children != null && children.size() > 0) {
						for (GridTreeNode item : children) {
							if (CatalogModelType.OT_VSS_EXCH_COMPONENT_PUBLIC == item.getType() 
									|| CatalogModelType.OT_GRT_EXCH_PUBLIC_FOLDERS == item.getType()) {
								publicFolderDbs.add(item);
							} else {
								childrenExceptPublic.add(item);
							}
						}
					}
					if (publicFolderDbs.size() > 0) {
						isPackageRSGChildren = true;
					}
				}
			}
		}

		if (isPackageRSGChildren) {
			return childrenExceptPublic;
		} else {
			return null;
		}
	}

	public static void processRestoreSource(RestoreJobNodeModel nodeModel, Integer jobType) {

		List<GridTreeNode> restoreSources = RestoreContext
				.getRestoreRecvPointSources();

		List<GridTreeNode> children = packageRSGChildren();

		if (children != null) {
			restoreSources = children;
		}

		TreeStore<GridTreeNode> treeStore = RestoreContext
				.getRestoreRecvPointTreeStore();
		if (treeStore == null || restoreSources == null)
			return;

		RestoreJobModel model = RestoreContext.getRestoreModel();
		nodeModel.setEncryptPassword(model.getEncryptPassword());
		
		nodeModel.listOfRestoreJobItems = new ArrayList<com.ca.arcflash.ui.client.model.RestoreJobItemModel>();

		// get this value before the loop, otherwise the source list will be go over many times O(n2)
		RestoreJobType restoreJobType = RestoreContext.getRestoreType();

		for (int i = 0; i < restoreSources.size(); i++) {
			RestoreJobItemModel item = new RestoreJobItemModel();

			GridTreeNode node = restoreSources.get(i);
			String path = node.getPath();

			if (restoreJobType != null
					&& RestoreJobType.FileSystem.getValue() == restoreJobType.getValue()) {
				// If a full drive is selected, its path will be null
				if (path == null) {
					int subSessionNum = node.getSubSessionID();
					item.setSubSessionNum(subSessionNum);
                    if(-1 == node.getName().indexOf(":") || node.getCatalogFilePath().endsWith("$DISABLED$"))
                    {	//This may be the system reserved disk volume, we use guid instead. Robin
                    	//Fix issue 18906917
                          item.setPath(node.getGuid());
                    }
                    else
                    {
                          item.setPath(node.getName());
                    }

				} else {
					path = path + "\\" + node.getComponentName();

					// TODO: CHANGE THIS
					// This is a bit of a hack since the whole path
					// isn't ok in the Catalog
					int pos = path.indexOf("\\");
					if(path.startsWith(node.getGuid())) {
						pos = node.getGuid().length();
					}else if(node.isHasDriverLetter() !=null && !node.isHasDriverLetter() && node.getCatalogFilePath().endsWith("$DISABLED$")){
						pos = node.getVolumeMountPath().length();
					}
					
					String drive = path.substring(0, pos);
					String itemPath = path.substring(pos);

					int subSessionNum = node.getSubSessionID();
					item.setSubSessionNum(subSessionNum);

					item.setPath(drive);
					item.listOfFiles = new ArrayList<RestoreJobItemEntryModel>();

					RestoreJobItemEntryModel iem = new RestoreJobItemEntryModel();
					iem.setPath(itemPath);
					iem.setType(node.getType());

					item.listOfFiles.add(iem);
				}
			}
			else if (restoreJobType != null
					&& RestoreJobType.GRT_Exchange.getValue() == restoreJobType.getValue()) 
			{
				// full selected EDB node
				if (CatalogModelType.rootGRTExchangeTypes.contains(node.getType()))
				{
					// the root is edb after change the tree to list, so the
					// writer doesn't exist on the tree
					String p = node.getPath();
					if(p.startsWith(node.getGuid()))
						p = node.getFullPath();
					
					int pos = p.indexOf("\\");
					String writerPath = path.substring(0, pos);

					item.setSubSessionNum(node.getSubSessionID());
					item.setPath(writerPath);
					item.listOfFiles = new ArrayList<RestoreJobItemEntryModel>();

					RestoreJobItemEntryModel iem = new RestoreJobItemEntryModel();

					// edb level item on job item entry, other deeper level
					// items on exch sub item
					GridTreeNode edbLevelNode = node;
					String itemPath = edbLevelNode.getPath() + "\\" + edbLevelNode.getName();
					itemPath = itemPath.substring(itemPath.indexOf('\\') + 1); 
				
					iem.setPath(itemPath);
					iem.setType(edbLevelNode.getType());

					// package additional info for GRT job					
					RestoreJobExchSubItemModel exchSubModel = packageExchSubItem(null, node);

					iem.listOfExchSubItems = new ArrayList<RestoreJobExchSubItemModel>();
					iem.listOfExchSubItems.add(exchSubModel);

					item.listOfFiles.add(iem);
				}
				// GRT node
				else 
				{
					String p = node.getPath();
					p = p + "\\" + node.getName();

					// the root is edb after change the tree to list, so the writer doesn't exist on the tree
					String writer = null; // root.getPath(); 
					if (writer == null) {
						// Special Exchange Case, root Path is null
						int pos = p.indexOf("\\");
						writer = path.substring(0, pos);
					}
						
					item.setSubSessionNum(node.getSubSessionID());
					item.setPath(writer);
					item.listOfFiles = new ArrayList<RestoreJobItemEntryModel>();
						
					RestoreJobItemEntryModel iem = new RestoreJobItemEntryModel();
						
					// edb level item on job item entry, other deeper level items on exch sub item
					GridTreeNode edbLevelNode = findAncestorEDBItem(treeStore, node);
					if(edbLevelNode == null)
					{
						edbLevelNode = node.getReferNode().get(0);
					}
					String itemPath = edbLevelNode.getPath() + "\\" + edbLevelNode.getName();
					itemPath = itemPath.substring(itemPath.indexOf('\\') + 1);  // remove the vss writer as backend requested
						
					iem.setPath(itemPath);
					iem.setType(edbLevelNode.getType());

					// package additional info for GRT job
					GridTreeNode mailboxLevelNode = findAncestorMailboxLevelItem(treeStore, node);
					if (mailboxLevelNode == null && node.getReferNode().size() >1)
					{
						mailboxLevelNode = node.getReferNode().get(1);
					}
					RestoreJobExchSubItemModel exchSubModel = packageExchSubItem(mailboxLevelNode, node);
						
					iem.listOfExchSubItems = new ArrayList<RestoreJobExchSubItemModel>();
					iem.listOfExchSubItems.add(exchSubModel);

					item.listOfFiles.add(iem);
				}
			} else {
				GridTreeNode root = findAncestor(treeStore, node);

				// Selected Restore Writer Level
				if (root == null || root == node) {
					item.setSubSessionNum(node.getSubSessionID());
					item.setPath(node.getPath());
				} else {
					Boolean b = node.getPackage();
					if (b == null || b == true) {
						String p = node.getPath();
						p = p + "\\" + node.getName();

						String writer = root.getPath();
						if (writer == null) {
							// Special Exchange Case, root Path is null
							int pos = p.indexOf("\\");
							writer = path.substring(0, pos);
						}

						item.setSubSessionNum(node.getSubSessionID());
						item.setPath(writer);
						item.listOfFiles = new ArrayList<RestoreJobItemEntryModel>();

						RestoreJobItemEntryModel iem = new RestoreJobItemEntryModel();
						iem.setPath(p);
						iem.setType(node.getType());
						item.listOfFiles.add(iem);
					}
				}
			}

			if (item != null) {
				nodeModel.listOfRestoreJobItems.add(item);
			}

		}
	}

	// find GRT root items
	public static GridTreeNode findAncestorEDBItem(TreeStore<GridTreeNode> treeStore, GridTreeNode node)
	{
		GridTreeNode retNode = null;

		if (treeStore != null) {
			GridTreeNode curNode = node;
			while(curNode != null)
			{
				if (CatalogModelType.rootGRTExchangeTypes.contains(curNode.getType().intValue())) {
					retNode = curNode;
					break;
				}
				
				curNode = treeStore.getParent(curNode);
			};
		}
	
		return retNode;
	}
	
	// find the parent mailbox node
	public static GridTreeNode findAncestorMailboxLevelItem(TreeStore<GridTreeNode> treeStore, GridTreeNode node)
	{
		GridTreeNode retNode = null;
		
		if (treeStore != null)
		{
			GridTreeNode curNode = node;
			while(curNode != null)
			{				
				GridTreeNode parentNode = treeStore.getParent(curNode);
				if (parentNode != null)
				{
					if (CatalogModelType.rootGRTExchangeTypes.contains(parentNode.getType().intValue()))
					{
						retNode = curNode;
						break;
					}
				}
				
				curNode = treeStore.getParent(curNode);
			};
		}
	
		return retNode;
	}

	// package additional info on JobItemEntry for GRT job
	public static RestoreJobExchSubItemModel packageExchSubItem(
			GridTreeNode mailboxLevelNode, GridTreeNode node) {
		
		// for full EDB node
		if (mailboxLevelNode == null && node != null)
		{
			RestoreJobExchSubItemModel exchSubItem = new RestoreJobExchSubItemModel();
			exchSubItem.setItemName(node.getDisplayName());
			exchSubItem.setItemType(0L);
			exchSubItem.setMailboxName("");
			exchSubItem.setExchangeObjectID("");
			return exchSubItem;
		}
		
		TreeStore<GridTreeNode> treeStore = RestoreContext.getRestoreRecvPointTreeStore();
		if (treeStore == null || mailboxLevelNode == null || node == null) {
			return null;
		}

		GRTCatalogItemModel grtCatalogItem = node.getGrtCatalogItemModel();
		GRTCatalogItemModel grtMailboxLevelCatalogItem = mailboxLevelNode
				.getGrtCatalogItemModel();
		if (grtCatalogItem == null || grtMailboxLevelCatalogItem == null) {
			return null;
		}

		RestoreJobExchSubItemModel exchSubItem = new RestoreJobExchSubItemModel();

		if (mailboxLevelNode.equals(node)) {
			exchSubItem.setItemName(grtCatalogItem.getObjName());
			exchSubItem.setItemType(1L);
		} else if (CatalogModelType.exchSubItemType_folders
				.contains(grtCatalogItem.getObjType().intValue())
				|| CatalogModelType.exchSubItemType_calendar_contact_item
				.contains(grtCatalogItem.getObjType().intValue())) {
			exchSubItem.setItemName(grtCatalogItem.getObjName());
			Long itemType = 0L;
			switch(grtCatalogItem.getObjType().intValue()) {
				case CatalogModelType.OT_GRT_EXCH_CALENDAR:
					itemType = 4L;
					break;
				case CatalogModelType.OT_GRT_EXCH_CALENDAR_ITEM:
					itemType = 6L;
					break;
				case CatalogModelType.OT_GRT_EXCH_CONTACTS:
					itemType = 5L;
					break;
				case CatalogModelType.OT_GRT_EXCH_CONTACTS_ITEM:				
					itemType = 7L;
					break;
				case CatalogModelType.OT_GRT_EXCH_CONTACTS_GROUP:
					itemType = 11L;
					break;
				default:
					itemType = 2L;
					break;
			}
			exchSubItem.setItemType(itemType);			
		} else if (CatalogModelType.exchSubItemType_messages
				.contains(grtCatalogItem.getObjType().intValue())) {
			exchSubItem.setItemName(grtCatalogItem.getObjName());
			exchSubItem.setItemType(3L);
		}

		exchSubItem.setMailboxName(grtMailboxLevelCatalogItem.getObjName());
		exchSubItem.setExchangeObjectID(grtCatalogItem.getObjInfo());

		return exchSubItem;
	}

	// Traverse up the tree and find the highest ancestor
	public static GridTreeNode findAncestor(TreeStore<GridTreeNode> treeStore,
			GridTreeNode node) {
		GridTreeNode p = treeStore.getParent(node);
		if (p == null)
			return node;

		while (treeStore.getParent(p) != null) {
			p = treeStore.getParent(p);
		}
		return p;
	}

	
	public static RestoreJobType getArchiveJobType(List<ArchiveGridTreeNode> selectedNodes) {

		if (selectedNodes == null)
			return null;

		RestoreJobType type = null;
		for (ArchiveGridTreeNode item : selectedNodes) {
			RestoreJobType currCat = getJobCategory(item.getType());
			if (type != null && currCat != null
					&& type.getValue() != currCat.getValue()) {
				return null;
			}
			type = currCat;
		}

		return type;
	}
	
	

	public static RestoreJobType getArchiveJobCategory(int myType) {
		RestoreJobType type = null;
			
		if (myType == CatalogModelType.File
				|| myType == CatalogModelType.Folder
				|| myType == CatalogModelType.OT_VSS_FILESYSTEM_WRITER) {
			type = RestoreJobType.FileSystem;
		} 

		return type;
			}
	


	public static RestoreJobType getJobType(List<GridTreeNode> selectedNodes) {

		if (selectedNodes == null)
			return null;

		RestoreJobType type = null;
		for (GridTreeNode item : selectedNodes) {
			RestoreJobType currCat = getJobCategory(item.getType());
			
			// the EDB when Restore Exchange Writer and Restore Exchange GRT have the same type, but means different type of restore job
			if (CatalogModelType.rootGRTExchangeTypes.contains(item.getType()))
			{
				// GRT EDB will have display path, so use it to separate "RestoreJobType.VSS_Exchange" and "RestoreJobType.GRT_Exchange"
				if (item.getDisplayPath() != null)
				{
					currCat = RestoreJobType.GRT_Exchange;
				}				
			}
			
			if (type != null && currCat != null
					&& type.getValue() != currCat.getValue()) {
				return null;
			}
			type = currCat;
		}

		return type;
	}

	public static RestoreJobType getJobCategory(int myType) {
		RestoreJobType type = null;
			if (myType == CatalogModelType.OT_VSS_SQL_COMPONENT
					|| myType == CatalogModelType.OT_VSS_SQL_COMPONENT_SELECTABLE
					|| myType == CatalogModelType.OT_VSS_SQL_LOGICALPATH
					|| myType == CatalogModelType.OT_VSS_SQL_NODE
					|| myType == CatalogModelType.OT_VSS_SQL_WRITER) {
				type = RestoreJobType.VSS_SQLServer;
		} else if (RestoreContext.AllExchangeTypes.contains(myType)) {
			type = RestoreJobType.VSS_Exchange;
		} else if (myType == CatalogModelType.File
				|| myType == CatalogModelType.Folder
				|| myType == CatalogModelType.OT_VSS_FILESYSTEM_WRITER) {
			type = RestoreJobType.FileSystem;
		} else if (CatalogModelType.allGRTExchangeTypes.contains(myType)) {
			type = RestoreJobType.GRT_Exchange;
		} else if(CatalogModelType.allActiveDirectoryTypes.contains(myType)){
			type = RestoreJobType.ActiveDirectory;
		}

		return type;
	}

	public static RestoreJobType getRestoreSearchType(
			List<CatalogItemModel> restoreSearchSources) {
		RestoreJobType type = null;
		if (restoreSearchSources != null) {
			for (CatalogItemModel item : restoreSearchSources) {
				RestoreJobType currCat = getJobCategory(item.getType());
				if (type != null && currCat != null
						&& type.getValue() != currCat.getValue()) {
					return null;
				}
				type = currCat;
			}
		}
		return type;
	}

	public static void processRestorSearch() {
		List<CatalogItemModel> restoreSearchSources = RestoreContext.getRestoreSearchSources();
		if (restoreSearchSources == null)
			return;

		for (CatalogItemModel catalogItem : restoreSearchSources) {
			RestoreJobNodeModel nodeModel = null;
			RestoreJobModel model = RestoreContext.getRestoreModel();
			for (RestoreJobNodeModel jobNode : model.listOfRestoreJobNodes) {
				if (jobNode.getSessionNumber().intValue() == catalogItem.getSessionNumber()) {
					nodeModel = jobNode;
				}
			}

			if (nodeModel == null) {
				nodeModel = new RestoreJobNodeModel();
				nodeModel.setSessionNumber(catalogItem.getSessionNumber());
				nodeModel.listOfRestoreJobItems = new ArrayList<RestoreJobItemModel>();
				
				if (catalogItem.isEncrypted()){
					//change since there is no fullsession guid when there is no catalog
					EncrypedRecoveryPoint point = RestoreContext.getEncrypedRecoveryPoints().get(catalogItem.getPasswordHash());
					nodeModel.setEncryptPassword(point.getPassword());
				}
				
				model.listOfRestoreJobNodes.add(nodeModel);
			}

			if (RestoreJobType.GRT_Exchange.getValue() == RestoreUtil.getJobCategory(catalogItem.getType()).getValue()) {
				model.setJobType(RestoreJobType.GRT_Exchange.getValue());// GRT_Exchange: see RestoreJobType.java

				MsgSearchRecModel srm = catalogItem.getMsgRecModel();

				RestoreJobItemModel itemModel = null;
				for (RestoreJobItemModel jobItem : nodeModel.listOfRestoreJobItems) {
					if (jobItem.getSubSessionNum() == catalogItem.getSubSessionNumber()
							&& jobItem.getPath().equalsIgnoreCase(srm.getEdbFullPath())) {
						itemModel = jobItem;
						break;
					}
				}

				// if not, create a new RestoreJobItemModel
				if (itemModel == null) {
					itemModel = new RestoreJobItemModel();
					itemModel.setSubSessionNum(catalogItem.getSubSessionNumber());
					itemModel.setPath(srm.getEdbFullPath());
					itemModel.listOfFiles = new ArrayList<RestoreJobItemEntryModel>();
					nodeModel.listOfRestoreJobItems.add(itemModel);
				}

				RestoreJobItemEntryModel itemEntry = new RestoreJobItemEntryModel();
				itemEntry.setPath(srm.getEdbFullPath());
				itemEntry.setType((int) srm.getEdbType());
				itemEntry.listOfExchSubItems = new ArrayList<RestoreJobExchSubItemModel>();

				RestoreJobExchSubItemModel exchSubItem = new RestoreJobExchSubItemModel();
				exchSubItem.setItemType(3L);
				exchSubItem.setItemName(srm.getMsgRec().getObjName());
				exchSubItem.setMailboxName(srm.getMailboxOrSameLevelName());
				exchSubItem.setExchangeObjectID(srm.getMsgRec().getObjInfo());
				itemEntry.listOfExchSubItems.add(exchSubItem);

				itemModel.listOfFiles.add(itemEntry);
			} else {
				model.setJobType(1);
				String path = catalogItem.getPath();
				path = path + "\\" + catalogItem.getName();
				// TODO: CHANGE THIS
				// This is a bit of a hack since the whole path isn't ok
				// in the Catalog
				int startDrive = -1;
				if(path.startsWith("\\")) {
					startDrive = path.indexOf("\\", 4);
				}
				
				int endDrive = path.indexOf("\\", startDrive + 1);
				String drive = path.substring(startDrive + 1, endDrive);
				String itemPath = path.substring(endDrive);

			// Find if this nodeModel has a RestoreJobItemModel with
			// this drive and session
			RestoreJobItemModel itemModel = null;
			for (int j = 0; j < nodeModel.listOfRestoreJobItems.size(); j++) {
				RestoreJobItemModel temp = (RestoreJobItemModel) nodeModel.listOfRestoreJobItems
						.get(j);

				if (temp.getPath().compareTo(drive) == 0
						&& temp.getSubSessionNum() == catalogItem
								.getSubSessionNumber()) {
					// Found it
					itemModel = temp;
				}
			}

			// if not, create a new RestoreJobItemModel
			if (itemModel == null) {
				itemModel = new RestoreJobItemModel();
				// set the path and subsession
				itemModel.setPath(drive);
				itemModel.setSubSessionNum(catalogItem.getSubSessionNumber());
				itemModel.listOfFiles = new ArrayList<RestoreJobItemEntryModel>();

				nodeModel.listOfRestoreJobItems.add(itemModel);
			}

			RestoreJobItemEntryModel itemEntryModel = new RestoreJobItemEntryModel();
			itemEntryModel.setPath(itemPath);
			itemEntryModel.setType(catalogItem.getType());
			itemModel.listOfFiles.add(itemEntryModel);
		    }
	    }
	}
	
	public static Map<String, EncrypedRecoveryPoint> filterEncryptedRecoveryPoint(List<CatalogItemModel> items, Map<String, EncrypedRecoveryPoint> oldModels){
		if (items == null || items.size()==0)
			return null;
		
		Map<String, EncrypedRecoveryPoint> result = new HashMap<String, EncrypedRecoveryPoint>();
		for(CatalogItemModel item: items){
			if (item.isEncrypted())
				if (!result.containsKey(item.getPasswordHash()) ){
					EncrypedRecoveryPoint point = new EncrypedRecoveryPoint();
					point.setSessionNumber(item.getSessionNumber());
					point.setBackupDate(item.getBackupDate());
					point.setBackupTimeZoneOffset(item.getBKServerTimeZoneOffset());
					point.setBackupJobName(item.getBackupJobName());
					point.setBackupDestination(item.getBackupDestination());
					point.setPasswordHash(item.getPasswordHash());
					point.setSessionGuid(item.getSessionGuid());
					point.setFullSessionGuid(item.getFullSessionGuid());
					result.put(item.getPasswordHash(), point);
				}else{
					EncrypedRecoveryPoint existingPoint = result.get(item.getPasswordHash());
					if (existingPoint.getBackupDate().compareTo(item.getBackupDate())>0){
						existingPoint.setSessionNumber(item.getSessionNumber());
						existingPoint.setBackupDate(item.getBackupDate());
						existingPoint.setBackupTimeZoneOffset(item.getBKServerTimeZoneOffset());
						existingPoint.setBackupJobName(item.getBackupJobName());
						existingPoint.setBackupDestination(item.getBackupDestination());
						existingPoint.setPasswordHash(item.getPasswordHash());
						existingPoint.setSessionGuid(item.getSessionGuid());
					}
				}
		}
		
		if (oldModels!=null && !oldModels.isEmpty()){
			Collection<EncrypedRecoveryPoint> points = oldModels.values();
			for(EncrypedRecoveryPoint point: points){
				if (result.containsKey(point.getPasswordHash())){
					EncrypedRecoveryPoint existingPoint = result.get(point.getPasswordHash());
					existingPoint.setPassword(point.getPassword());
					existingPoint.setPasswordVerified(point.getPasswordVerified());
				}
			}
		}
		return result;
	}
	
	public static Date RecPointModel2ServerDate(RecoveryPointModel rpmodel) {
		long timeDiffLocalAndServer = rpmodel.getTime().getTimezoneOffset()
				* 60 * 1000 + rpmodel.getTimeZoneOffset();
		Date serverDate = new Date(rpmodel.getTime().getTime()
				+ timeDiffLocalAndServer);
		return serverDate;
	}

	public static void processRestoreSource4AD(RestoreJobNodeModel nodeModel) {
		GridTreeNode adDB = null;
		Iterator<GridTreeNode> i = RestoreContext.getRootItemMap().keySet().iterator();
		while(i.hasNext()){
			GridTreeNode node = i.next();
			if(node.getType()==CatalogModelType.ActiveDirectory){
				adDB=node;
				break;
			}
		}
		List<GridTreeNode> restoreSources = RestoreContext.getRestoreRecvPointSources();
		if (adDB == null || restoreSources == null)
			return;
		
		RestoreJobModel model = RestoreContext.getRestoreModel();
		nodeModel.setEncryptPassword(model.getEncryptPassword());
		
		nodeModel.listOfRestoreJobItems = new ArrayList<com.ca.arcflash.ui.client.model.RestoreJobItemModel>();
		
		RestoreJobItemModel item = new RestoreJobItemModel();
		item.setPath(adDB.getPath());
		item.setSubSessionNum(adDB.getSubSessionID());
		item.listOfFiles = new ArrayList<RestoreJobItemEntryModel>();
		
		RestoreJobItemEntryModel iem = new RestoreJobItemEntryModel();
		iem.setPath(adDB.getPath());
		iem.setType(adDB.getType());
		iem.listOfADItems = new ArrayList<GridTreeNode>();
		iem.listOfADItems.addAll(restoreSources);
		
		item.listOfFiles.add(iem);
		
		nodeModel.listOfRestoreJobItems.add(item);
	}
	
	public static IconButton getNodeIcon(GridTreeNode node){
		
		if(node == null)
			return null;
		
		IconButton image = null;
		int nodeType = node.getType();
		switch (nodeType) {
			case CatalogModelType.Folder:
				image = new IconButton("folder-icon");
				break;
			case CatalogModelType.File:
				image = new IconButton("file-icon");
				break;
			case CatalogModelType.OT_VSS_FILESYSTEM_WRITER:
				image = new IconButton("drive-icon");
				break;
			case CatalogModelType.OT_VSS_SQL_WRITER:
				image = new IconButton("sql_server_writer_icon");
				break;
			case CatalogModelType.OT_VSS_SQL_NODE:
				image = new IconButton("sql_server_node_icon");
				break;
			case CatalogModelType.OT_VSS_SQL_LOGICALPATH:
				image = new IconButton("sql_server_icon");
				break;
			case CatalogModelType.OT_VSS_SQL_COMPONENT_SELECTABLE:
				image = new IconButton("sql_server_database");
				break;
			case CatalogModelType.OT_VSS_EXCH_WRITER:
				image = new IconButton("exchange_writer_icon");
				break;	
			case CatalogModelType.OT_VSS_EXCH_SERVER:
				image = new IconButton("exchange_server_name_icon");
				break;
			case CatalogModelType.OT_VSS_EXCH_INFOSTORE:
				image = new IconButton("exchange_store_icon");
				break;
			case CatalogModelType.OT_VSS_EXCH_NODE:
				image = new IconButton("exchange_node_icon");
				break;
			case CatalogModelType.OT_VSS_EXCH_LOGICALPATH:
				image = new IconButton("exchange_storage_group_icon");
				break;
			case CatalogModelType.OT_VSS_EXCH_COMPONENT_SELECTABLE:
				image = new IconButton("exchange_mailbox_icon");
				break;
			case CatalogModelType.OT_VSS_EXCH_COMPONENT_PUBLIC:
				image = new IconButton("exchange_publicfolder_icon");
				break;
				
			// -- begin of Exchange GRT	
			case CatalogModelType.OT_GRT_EXCH_MBSDB:
				image = new IconButton("exchange_grt_edb_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_CALENDAR:
				image = new IconButton("exchange_grt_calendar_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_CONTACTS:
				image = new IconButton("exchange_grt_contact_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_DRAFT:
				image = new IconButton("exchange_grt_draft_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_JOURNAL:
				image = new IconButton("exchange_grt_journal_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_NOTES:
				image = new IconButton("exchange_grt_notes_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_TASKS:
				image = new IconButton("exchange_grt_tasks_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_PUBLIC_FOLDERS:
				image = new IconButton("exchange_grt_public_folder_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_MAILBOX:
				image = new IconButton("exchange_grt_mailbox_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_DELETED_ITEMS:
				image = new IconButton("exchange_grt_deleted_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_INBOX:
				image = new IconButton("exchange_grt_inbox_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_OUTBOX:
				image = new IconButton("exchange_grt_outbox_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_SENT_ITEMS:
				image = new IconButton("exchange_grt_sent_items_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_FOLDER:
				image = new IconButton("exchange_grt_folder_icon");
				break;
				
			case CatalogModelType.OT_GRT_EXCH_MESSAGE:
				image = new IconButton("exchange_grt_message_icon");
				break;
			
			// -- end of Exchange GRT icons
				
				// -- begin of SharePoint GRT	
			case CatalogModelType.OT_GRT_SP_DB:
				image = new IconButton("sql_server_database");
				break;
				
			case CatalogModelType.OT_GRT_SP_SITE:
				image = new IconButton("sharepoint_grt_site_icon");
				break;
				
			case CatalogModelType.OT_GRT_SP_WEB:
				image = new IconButton("sharepoint_grt_web_icon");
				break;
				
			case CatalogModelType.OT_GRT_SP_LIST:
				image = new IconButton("sharepoint_grt_list_icon");
				break;
				
			case CatalogModelType.OT_GRT_SP_FOLDER:
				image = new IconButton("sharepoint_grt_folder_icon");
				break;
				
			case CatalogModelType.OT_GRT_SP_FILE:
				image = new IconButton("sharepoint_grt_file_icon");
				break;
				
			case CatalogModelType.OT_GRT_SP_VERSION:
				image = new IconButton("sharepoint_grt_version_icon");
				break;
			// -- end of SharePoint GRT icons
			case CatalogModelType.ActiveDirectory:	
				image = new IconButton("active_directory_icon");
				break;
			
			default:
				break;
		}
		if(image != null){
			image.setWidth(20);
			image.setStyleAttribute("font-size", "0");
		}
		
		return image;
	
	}
	
	
}