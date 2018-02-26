package com.ca.arcflash.ui.client.restore.ad;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.extjs.gxt.ui.client.widget.button.IconButton;

public class ADRestoreUtils {
	
	public static final String pathSeparator =  " > ";

	public static IconButton getNodeIcon(GridTreeNode node){
		
		if(node == null)
			return null;
		
		IconButton image = null;
		int nodeType = node.getType();
		switch (nodeType) {
			case CatalogModelType.ActiveDirectory:	
				image = new IconButton("active_directory_icon");
				break;
			case CatalogModelType.AD_GENERAL:	
				image = new IconButton("ad_general_icon");
				break;
			case CatalogModelType.AD_USERS:	
				image = new IconButton("ad_users_icon");
				break;
			case CatalogModelType.AD_USER:	
				image = new IconButton("ad_user_icon");
				break;
			case CatalogModelType.AD_COMPUTER:	
				image = new IconButton("ad_computer_icon");
				break;
			case CatalogModelType.AD_OU:	
				image = new IconButton("ad_ou_icon");
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
	
	/**
	 * Returns the children of the parent.
	 * 
	 * @param parent
	 *            the parent
	 * @return return all children recursively
	 */
	public static List<GridTreeNode> getChildren(GridTreeNode parent, List<GridTreeNode> subNodes) {
		List<GridTreeNode> children = new ArrayList<GridTreeNode>();
		if(parent.getType()==CatalogModelType.AD_ATTRIBUTE){
			return children;
		}
		for(GridTreeNode node : subNodes){
			if(node.getPath().startsWith(parent.getPath() + pathSeparator)){
				children.add(node);
			}
		}
		return children;
	}
	
	public static void removeChildren(GridTreeNode parent, List<GridTreeNode> subNodes) {
		List<GridTreeNode> children = getChildren(parent, subNodes);
		subNodes.removeAll(children);
	}
	
	public static void mergSelectedList(List<GridTreeNode> selectedList) {
		List<GridTreeNode> fullList= new ArrayList<GridTreeNode>();
		List<GridTreeNode> partList= new ArrayList<GridTreeNode>();
		for(GridTreeNode node: selectedList){
			if (node.getSelectionType() == GridTreeNode.SELECTION_TYPE_FULL) {
				fullList.add(node);
			} else if (node.getSelectionType() == GridTreeNode.SELECTION_TYPE_PARTIAL) {
				partList.add(node);
			}
		}
		for(GridTreeNode node: fullList){
			removeChildren(node, selectedList);
		}
		for(GridTreeNode node: partList){
			boolean flag = hasChildren(node, selectedList);
			if(flag){
				selectedList.remove(node);
			}
		}
	}
	
	public static boolean hasChildren(GridTreeNode parent, List<GridTreeNode> subNodes){
		for(GridTreeNode node : subNodes){
			if(node.getPath().startsWith(parent.getPath() + pathSeparator)){
				return true;
			}
		}
		return false;
	}
	
	public static void deduplicateNode(GridTreeNode parent, List<GridTreeNode> subNodes) {
		GridTreeNode dedup=null;
		for(GridTreeNode node : subNodes){
			if(node.getPath().equals(parent.getPath())){
				dedup = node;
			}
		}
		subNodes.remove(dedup);
	}
}
