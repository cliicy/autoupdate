/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Result information of adding nodes.
 * 
 * @author lijwe02
 * 
 */
public class AddNodeResult implements Serializable {
	private static final long serialVersionUID = 1823240211503660503L;

	private List<Integer> converterIdList = new ArrayList<Integer>();
	//the group ids is not from as_edge_host, but as_edge_esx
	private List<Integer> vCloudGroupIds = new ArrayList<Integer>();
	
	private List<AddNodeInfo> nodeIdList = new ArrayList<AddNodeInfo>();

	/**
	 * Get node ID list for converter nodes.
	 * 
	 * @return
	 */
	public List<Integer> getConverterIdList() {
		return converterIdList;
	}

	/**
	 * Set node ID list for converter nodes.
	 * 
	 * @param	converterIdList
	 */
	public void setConverterIdList(List<Integer> converterIdList) {
		this.converterIdList = converterIdList;
	}

	/**
	 * Add node ID to the converter node ID list.
	 * 
	 * @param	converterId
	 */
	public void addConverterId(Integer converterId) {
		converterIdList.add(converterId);
	}

	/**
	 * Add all node ID in specified node ID list to the converter node ID list.
	 * 
	 * @param	idList
	 * 			List contains node IDs to be added to the converter list.
	 */
	public void addAllConverterIds(Collection<Integer> idList) {
		if (idList != null) {
			for (Integer id : idList) {
				if (!converterIdList.contains(id)) {
					converterIdList.add(id);
				}
			}
		}
	}

	public List<Integer> getvCloudGroupIds() {
		return vCloudGroupIds;
	}

	public void setvCloudGroupIds(List<Integer> vCloudGroupIds) {
		this.vCloudGroupIds = vCloudGroupIds;
	}

	/**
	 * Get the list of result of adding particular type of nodes.
	 * 
	 * @return
	 */
	public List<AddNodeInfo> getNodeIdList() {
		return nodeIdList;
	}

	/**
	 * Set the list of result of adding particular type of nodes.
	 * 
	 * @param nodeIdList
	 */
	public void setNodeIdList(List<AddNodeInfo> nodeIdList) {
		this.nodeIdList = nodeIdList;
	}
	
	/**
	 * Node types for adding nodes result.
	 * 
	 * @author panbo01
	 *
	 */
	public static enum NodeEnum{
		Windows,
		Linux,
		LinuxBackupServer,
		VM_Vmware,
		VM_HyperV,
		RHA,
	}
	
	/**
	 * Information of the result of adding a particular type of nodes.
	 * 
	 * @author panbo01
	 *
	 */
	public static class AddNodeInfo implements Serializable{
		private static final long serialVersionUID = 5202024624106496614L;
		private NodeEnum nodeType;
		private List<Integer> nodeIds=new ArrayList<Integer>();
		
		/**
		 * Construct a empty result. The node type is not defined.
		 */
		public AddNodeInfo() {
		}
		
		/**
		 * Construct a result for the specified node type.
		 * 
		 * @param	nodeType
		 * 			See {@link NodeEnum} for available values for this
		 * 			parameter.
		 * 
		 * @see		NodeEnum
		 */
		public AddNodeInfo(NodeEnum nodeType) {
			this.nodeType = nodeType;
		}
		
		/**
		 * Get the node type this result is for.
		 * 
		 * @return
		 */
		public NodeEnum getNodeType() {
			return nodeType;
		}
		
		/**
		 * Set the node type this result is for.
		 * 
		 * @param nodeType
		 */
		public void setNodeType(NodeEnum nodeType) {
			this.nodeType = nodeType;
		}
		
		/**
		 * Get the ID list of nodes that were added.
		 * 
		 * @return
		 */
		public List<Integer> getNodeIds() {
			return nodeIds;
		}
		
		/**
		 * Set the ID list of nodes that were added.
		 * 
		 * @param nodeIds
		 */
		public void setNodeIds( List<Integer> nodeIds )
		{
			if (nodeIds == null)
				nodeIds = new ArrayList<Integer>();
			this.nodeIds = nodeIds;
		}
	}
}
