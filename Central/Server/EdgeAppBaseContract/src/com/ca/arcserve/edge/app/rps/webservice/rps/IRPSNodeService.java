package com.ca.arcserve.edge.app.rps.webservice.rps;

import java.util.List;

import com.ca.arcflash.rps.webservice.data.ManualFilecopyItem;
import com.ca.arcflash.rps.webservice.replication.ManualMergeItem;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RPSSourceNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.AddRpsNodesResult;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;

public interface IRPSNodeService {
	
	/**
	 * Get all node for one group
	 * @param groupID
	 * @return If groupID is 0 , it will return all nodes.<br>
	 * 		   If groupID is -1, it will return nodes which don't be assigned to any group<br>
	 * 	       Else it will return nodes belongs to specified group
	 * @throws EdgeServiceFault
	 */
	List<RpsNode> getRpsNodesByGroup(int gateway, int groupID) throws EdgeServiceFault;
	
	/**
	 * Registration one Rps node to Rps App
	 * @param failedReadRemoteRegistry  if failed to read  remote node registry
	 * @param registrationNodeInfo information for RpsNode
	 * @return RegistrationNodeResult
	 * @throws EdgeServiceFault
	 */
	RegistrationNodeResult registerRpsNode(boolean failedReadRemoteRegistry,NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault;
	/**
	 * Update Rps Node Information
	 * @param failedReadRemoteRegistry  if failed to read  remote node registry
	 * @param nodeInfo information for RpsNode
	 * @return Error Code for updating Rps Node
	 * @throws EdgeServiceFault
	 */
	String[] updateRpsNode(boolean failedReadRemoteRegistry,NodeRegistrationInfo nodeInfo, boolean overwrite) throws EdgeServiceFault;
	/**
	 * Delete one specified node
	 * @param id
	 * @param keepCurrentSettings
	 * @throws EdgeServiceFault
	 */
	void deleteRpsNodeOnly(int id) throws EdgeServiceFault;
	/**
	 * Delete one specified node
	 * @param id
	 * @param keepCurrentSettings
	 * @throws EdgeServiceFault
	 */
	void deleteRpsNode(int id, boolean keepCurrentSettings) throws EdgeServiceFault;
	/**
	 * Get rps node information for one node
	 * @param hostID
	 * @return RpsNode
	 * @throws EdgeServiceFault
	 */
	RpsNode getRpsNodeDetailInformation(int hostID) throws EdgeServiceFault;
	
	/**
	 * mark RPS Server as managed
	 * @param nodeInfo
	 * @param overwrite if managed it forcely
	 * @throws EdgeServiceFault
	 */
	void markRpsNodeAsManaged(NodeRegistrationInfo nodeInfo,boolean overwrite) throws EdgeServiceFault;
	
	NodeManageResult queryRpsManagedStatus(NodeRegistrationInfo nodeRegistrationInfo)throws EdgeServiceFault;
	
	AddRpsNodesResult importRpsNodes(NodeRegistrationInfo[] nodes, ImportNodeType type) throws EdgeServiceFault;
	
	/**
	 * Retrieve all source nodes from RPS Control Service
	 * 
	 * @param controlService
	 *            The control service information
	 * @return the source node list
	 * @throws EdgeServiceFault
	 */
	List<RPSSourceNode> importNodeFromRpsServer(RpsNode rpsNode) throws EdgeServiceFault;
	
	void deleteDataStoreById(int nodeId, String dedupId) throws EdgeServiceFault;
	List<NetworkPath> getMappedNetworkPath(int nodeID) throws EdgeServiceFault;
	void startMergeNow(int rpsNodeId, List<ManualMergeItem> mergeItems) throws EdgeServiceFault;
	void startFilecopyNow(int rpsNodeId,List<ManualFilecopyItem> filecopyitems) throws EdgeServiceFault;
	void startFileArchiveNow(int rpsNodeId,	List<ManualFilecopyItem> filearchiveItems) throws EdgeServiceFault; 
}
