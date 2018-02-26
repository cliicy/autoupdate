package com.ca.arcserve.edge.app.rps.webservice.rps;

import java.util.Date;
import java.util.List;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.rps.webservice.data.ds.HashRoleEnvInfo;
import com.ca.arcflash.rps.webservice.replication.ManualReplicationItem;
import com.ca.arcflash.webservice.data.ConnectionInfo;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SessionPassword;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.datastore.DataSeedingJobScript;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.PlanInDestination;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;

public interface IRPSDataStoreService {
	
	/**
	 * Get Dedup list by node id
	 * @param nodeId
	 * @return
	 * @throws EdgeServiceFault
	 */
	List<DataStoreSettingInfo> getDataStoreListByNode(int nodeId) throws EdgeServiceFault;

	/**
	 * get dedup detail info
	 * @param dedupId
	 * @return
	 * @throws EdgeServiceFault
	 */
	DataStoreSettingInfo getDataStoreById(int dedupId) throws EdgeServiceFault;
	
	HashRoleEnvInfo getHashRoleEnvInfo(int nodeID) throws EdgeServiceFault;
	
	void createFolder(int nodeID, NodeRegistrationInfo rpsInfo, String parentPath, String subDir, String username, String password) throws EdgeServiceFault;
	
	FileFolderItem getFileItems(int nodeID,NodeRegistrationInfo rpsInfo, String inputFolder,String username, String password, boolean bIncludeFiles,int browseClient) 
    throws EdgeServiceFault;
	
	Volume[] getVolumes(int nodeID,NodeRegistrationInfo rpsInfo, int browseClient) throws EdgeServiceFault;
	
	void cutAllRemoteConnections(int nodeID, NodeRegistrationInfo rpsInfo) throws EdgeServiceFault;
	
	long validateDestOnly(int nodeID,NodeRegistrationInfo rpsInfo, String path, String domain, String user,
			String pwd, int mode) throws EdgeServiceFault;
	
	String saveDataStoreSetting(DataStoreSettingInfo settingInfo) throws EdgeServiceFault;
	DataStoreSettingInfo getDataStoreByGuid(int nodeId, String guid) throws EdgeServiceFault;
	List<DataStoreSettingInfo> getDataStoreHistoryByGuid(int nodeId, String guid, Date timeStamp) throws EdgeServiceFault;
	List<DataStoreStatusListElem> getDataStoreSummariesByNode(int nodeId) throws EdgeServiceFault;
	DataStoreStatusListElem getDataStoreSummary(int nodeId, String guid) throws EdgeServiceFault;
	
	void startDataStoreInstance(int nodeId, String dataStoreUuid) throws EdgeServiceFault;
	void stopDataStoreInstance(int nodeId, String dataStoreUuid) throws EdgeServiceFault;
	
	List<DataStoreStatusListElem> getDataStoreSummariesByNodefromCache(int nodeId ) throws EdgeServiceFault;
    /**
     * sync datastorestatus from all rps server
     * @throws EdgeServiceFault
     */
	void triggerDataStoreSummarySync() throws EdgeServiceFault;
	
	DataStoreSettingInfo importDataStoreInstance(
			int nodeID, DataStoreSettingInfo storeSettings, boolean bOverWrite, boolean bForceTakeOwnership) throws EdgeServiceFault;
	DataStoreSettingInfo getDataStoreInfoFromDisk(int nodeID, String strPath,
			String strUser, String strPassword, String strDataStorePassword) throws EdgeServiceFault;

	long getDataStoreDedupeRequiredMinMemSizeByte(int nodeId, String dataStoreId) throws EdgeServiceFault;
	
	List<PlanInDestination> getNodesFromDataStroe(int rpsNodeId, String DataStoreUUID, boolean filterNullClientUuid)throws EdgeServiceFault;
	List<ProtectedNodeInDestination> getNodesDetailFromDataStore(int rpsNodeId, List<ProtectedNodeInDestination> originalNodeList) throws EdgeServiceFault;
	List<ProtectedNodeInDestination> getDataSeedingNodes(int sourceRpsNodeId, String sourceDataStoreUuid) throws EdgeServiceFault;
	void submitDataSeedingJob(DataSeedingJobScript script) throws EdgeServiceFault;
	List<SessionPassword> validateSessionPassword(int rpsNodeId, List<SessionPassword> list) throws EdgeServiceFault;
	void deleteRecoveryPointsFromDataStore(int rpsNodeId, String dataStoreUUID, List<String> nodeUUIDList) throws EdgeServiceFault;
	List<ProtectedNodeInDestination> getNodesFromShareFolder(int rpsNodeId, ConnectionInfo connectionInfo)throws EdgeServiceFault;
	boolean checkDataStoreDuplicate(DataStoreSettingInfo settingInfo) throws EdgeServiceFault;
	void startReplicationNow(int rpsNodeId, List<ManualReplicationItem> replicationitems) throws EdgeServiceFault;
}
