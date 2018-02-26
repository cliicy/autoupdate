package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.D2DRole;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ShowEULAModule;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SortablePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DMergeJobStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.RepJobMonitor4Edge;
import com.ca.arcserve.edge.app.base.webservice.contract.dashboard.RecoveryPointDataItem;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.BaseFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.FilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.HypervisorWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ASBUSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AddNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AdminAccountValidationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AdminAccountValidationResultWithMessage;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AutoDiscoverySetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.CSVObject;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DBackupJobStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVmwareEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHostBackupStats;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ExportNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Hypervisor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDeleteSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.OffsiteVCMConverterInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.OffsiteVCMConverterSavingStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SRMSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ServerInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SourceMachineNetworkAdapterInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.StandbyVMNetworkInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeVcloudSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilterGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResource;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceIdentifier;
import com.ca.arcserve.edge.app.base.webservice.contract.reportdashboard.BackupStatusByGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.Task;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EsxVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VSphereProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityType;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.MonitorHyperVInfo;

public interface INodeService extends INodeAdService, INodeEsxService, INodeRHAService, INodeMergeJobService, ISessionPasswordService {
	/* Node */
/*	String[] registerNode(NodeRegistrationInfo nodeInfo) throws EdgeServiceFault;
	String[] updateNode(NodeRegistrationInfo nodeInfo) throws EdgeServiceFault; */
	int checkNodeExist(int gatewayId, String hostName) throws EdgeServiceFault;
	void markNodeAsManaged(NodeRegistrationInfo nodeInfo, boolean overwrite) throws EdgeServiceFault; 
	int markMultiNodesAsManaged(List<Integer> nodeIds, boolean overWrite) throws EdgeServiceFault;
	void markRpsNodeAsManagedById(int rpsNodeId, boolean overwrite) throws EdgeServiceFault; 
	NodeManageResult queryNodeManagedStatus(NodeRegistrationInfo info) throws EdgeServiceFault;
	
	void setNodesAsManaged(int[] idArray) throws EdgeServiceFault;
	void deleteNode(int id, boolean keepCurrentSettings) throws EdgeServiceFault;
	
	void deleteNodes(int[] ids, boolean keepCurrentSettings) throws EdgeServiceFault;
	
	/* Node Group */
	List<NodeGroup> getNodeGroups() throws EdgeServiceFault;
	
	List<Node> getNodesByGroup(int gatewayId, int groupID, int groupType) throws EdgeServiceFault;
	
	List<Node> getHBBUProxy(int gatewayId) throws EdgeServiceFault;
	
	List<Node> getDeletedNodes()throws EdgeServiceFault;
	
	void deleteNodeGroup(int groupID) throws EdgeServiceFault;
	
	int createNewNodeGroup(GatewayId gatewayId, NodeGroup group, int[] assigedNodes) throws EdgeServiceFault;
	
	void updateNewNodeGroup(NodeGroup group, int[] assigedNodes) throws EdgeServiceFault;
	
	
	
	/* Auto Discovery */
	// Start Discovery Service, any scheduled discovery activity will be handled.
//	void startDiscoveryService() throws EdgeServiceFault;
	
	//Stop Discovery Service, any scheduled discovery activity will be stopped. 
//	void stopDiscoveryService() throws EdgeServiceFault;
	
	//Query Status of Discovery Service
//	DiscoveryServiceStatus queryDiscoveryServiceStatus() throws EdgeServiceFault;
	
//	List<String> getDomainControllerList(String domainName) throws EdgeServiceFault;
	
	//Probe the applications on the specified target machine
	//void probeNode(DiscoveryOption option) throws EdgeServiceFault;
	
	AdminAccountValidationResult validateAdminAccount(GatewayId gatewayId, String computerName, String userName, String password) throws EdgeServiceFault;

	List<AdminAccountValidationResultWithMessage> validateAdminAccountList(List<Node> nodeList) throws EdgeServiceFault;
	
	List<ServerInfo> getServers();
	
	ASBUSetting getASBUSetting(int branchID) throws EdgeServiceFault;
	void saveASBUSetting(int branchID, ASBUSetting setting) throws EdgeServiceFault;
	
	ASBUSetting getGlobalASBUSetting() throws EdgeServiceFault;
	void saveGlobalASBUSetting(ASBUSetting setting) throws EdgeServiceFault;
	
	SRMSetting getGlobalSRMSetting() throws EdgeServiceFault;
	void saveGlobalSRMSetting(SRMSetting setting) throws EdgeServiceFault;
	
	NodeDeleteSetting getGlobalNodeDeleteSetting() throws EdgeServiceFault;
	void saveGlobalNodeDeleteSetting(NodeDeleteSetting setting) throws EdgeServiceFault;
	
	D2DSetting getD2DSetting(int branchID) throws EdgeServiceFault;
	void saveD2DSetting(int branchID, D2DSetting setting) throws EdgeServiceFault;
	
	D2DSetting getGlobalD2DSetting() throws EdgeServiceFault;
	void saveGlobalD2DSetting(D2DSetting setting) throws EdgeServiceFault;
	
	
	RemoteNodeInfo queryRemoteNodeInfo(GatewayId gatewayId, int hostId, String hostname, String username, String password, String protocol, int port) throws EdgeServiceFault;
	RemoteNodeInfo updateRemoteNodeInfo(int hostId, String hostname, String username, String password) throws EdgeServiceFault;
	
	AutoDiscoverySetting getAutoDiscoverySettings(AutoDiscoverySetting.SettingType settingType)  throws EdgeServiceFault;
	void saveAutoDiscoverySettings(AutoDiscoverySetting settings, AutoDiscoverySetting.SettingType settingType)  throws EdgeServiceFault;
	
	/* Deploy */
	//int startDeploy( List<DeployTargetDetail>  details ) throws EdgeServiceFault;
	boolean isLocalHost(String host) throws EdgeServiceFault;
	String getLicenseText() throws EdgeServiceFault;
	/* End of Deploy */
	
	void importNodes(NodeRegistrationInfo[] nodes, ImportNodeType type) throws EdgeServiceFault;
	void importVMs(DiscoveryESXOption esxOption, VMRegistrationInfo[] vms, ImportNodeType type, boolean addEsxToADList) throws EdgeServiceFault;
	void importHyperVVMs(DiscoveryHyperVOption hyperVOption, VMRegistrationInfo[] vms, ImportNodeType type, boolean addEsxToADList) throws EdgeServiceFault;
	HypervProtectionType getHyperVProtectionType(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault;
	List<DiscoveryVirtualMachineInfo> getHypervVMList(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault;
	
	NodeDetail getNodeDetailInformation(int hostID) throws EdgeServiceFault;
	Node[] getNodesByGroupAndType(int groupID, int[] type)
			throws EdgeServiceFault;
	void updateEdgeConnectionCredential(int hostID, String userName, String password);
	List<Node> getNodesByGDBId(int GDBId) throws EdgeServiceFault;
	VSphereProxyInfo getUUIDforD2DLogin(int hostid) throws EdgeServiceFault;
	VSphereProxyInfo getRps4RemoteNode(int nodeId) throws EdgeServiceFault;
	RemoteNodeInfo tryConnectD2D(GatewayId gatewayId, String d2dProtocol, String d2dHost,int d2dPort, String d2dUserName, String d2dPassword) throws EdgeServiceFault;
	RegistrationNodeResult registerNode(boolean failedReadRemoteRegistry,	NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault;
	String[] updateNode(boolean failedReadRemoteRegistry,NodeRegistrationInfo nodeInfo) throws EdgeServiceFault;

	List<NodeGroup> getNodeESXGroups(int gatewayId) throws EdgeServiceFault;
	List<NodeGroup> getNodeGroupsByLayer(int gatewayid, NodeGroup parentGroup) throws EdgeServiceFault;

	Node[] getNodesESXByGroupAndType(int groupID, int[] types, int grouptype)	throws EdgeServiceFault;

	void deleteNodeESXGroup(int groupID, int type) throws EdgeServiceFault; 
	void deleteNodeESXGroupAndNodes(int groupID, int type) throws EdgeServiceFault;
	
	
	public void saveVMNodeESXSettings(int hostID, DiscoveryESXOption esxSetting) throws EdgeServiceFault;
	
	public DiscoveryESXOption getVMNodeESXSettings(int hostID) throws EdgeServiceFault;
	public DiscoveryESXOption getVMNodeESXSettingsFromDB(int hostID) throws EdgeServiceFault;
	public DiscoveryHyperVOption getVMNodeHyperVSettings(int hostID) throws EdgeServiceFault;
	
	public VSphereProxyInfo getVSphereProxyInfoByHostId(int hostId) throws EdgeServiceFault;

    public List<Boolean> getEULAStatus(ShowEULAModule module)  throws EdgeServiceFault;
	public void setEULAStatus(ShowEULAModule module, List<Boolean> list)  throws EdgeServiceFault;
	
	public NodePagingResult getNodesESXByGroupAndTypePaging(int groupID,
			int grouptype, EdgeNodeFilter nodeFilter, NodePagingConfig np) throws EdgeServiceFault;
	
	public List<ExportNode> getExportNodeList(int groupID, int grouptype, EdgeNodeFilter nodeFilter) throws EdgeServiceFault;
	public String generateExportNodeFile(List<Integer> nodeIds) throws EdgeServiceFault;
	public String generateExportNodeFileForGroup(int gatewayId, int groupType, int groupId) throws EdgeServiceFault;
	public int importNodesFromFile(String filePath) throws EdgeServiceFault;
	public List<Node> getSRMNodes() throws EdgeServiceFault;
	
	public void backupForEDGE(int id, String hostname, int backupType, String value, boolean convert) throws EdgeServiceFault;
	public int backupNodesForEDGE(int[] ids, int backupType, String value) throws EdgeServiceFault;
	public D2DBackupJobStatusInfo getBackupJobStatusById(int nodeId) throws EdgeServiceFault;
	public List<D2DBackupJobStatusInfo> getBackupJobStatusAll(List<String> nodeIdList) throws EdgeServiceFault;
	public List<FlashJobMonitor> getJobStatusInfoList(String jobStatusKey) throws EdgeServiceFault;
	public List<FlashJobMonitor> getJobMonitorForDashboard(int productType, int nodeId, int rpsNodeId, long jobType, long jobId, String jobUUID) throws EdgeServiceFault;
	public boolean cancelJob(int nodeId, String hostName, long jobId) throws EdgeServiceFault;
	public int cancelJobById(int nodeId, String hostName, long jobId, long jobType, String d2dUuid, String vmInstanceUuid, boolean isCancelJobFromRPS) throws EdgeServiceFault;
	public void cancelJobByGroup(int gatewayId, int groupId, int groupType) throws EdgeServiceFault;
	public int resumeMergeJob4RPS(int rpsNodeId, String uuid) throws EdgeServiceFault;
	public boolean cancelVMJob(int nodeId, String hostName, long jobId) throws EdgeServiceFault;
	public boolean cancelWaitingJob(Node node, String vmInstanceUuid) throws EdgeServiceFault;
	public RepJobMonitor4Edge getRepJobMonitorById(int nodeId) throws EdgeServiceFault;
	public boolean cancelReplication(int nodeId, String hostName, String vmInstanceUUID) throws EdgeServiceFault;
	public List<RepJobMonitor4Edge> getConversionJobStatusAll(List<String> nodeIdList) throws EdgeServiceFault;
	
	void backupVM(int nodeID, int backupType, String jobName) throws EdgeServiceFault;
	void backupVMWithFlag(int nodeID,int backupType, String jobName,boolean convertForBackupSet)throws EdgeServiceFault;
	void backupVMs(int[] nodeIDs, int backupType, String jobName) throws EdgeServiceFault;
	
	public int submitBackupJob(int gatewayId, int groupID, int groupType, int backupType, String jobName) throws EdgeServiceFault;
	public void verifyVMs(int[] nodeIDs)  throws EdgeServiceFault;
	
	String queryVMHostName(int hostID) throws EdgeServiceFault;
	
	void submitVerifyVMJobForGroup(int gatewayId, int groupID, int groupType) throws EdgeServiceFault;
	
	void changeHeartBeatStatus(int[] nodeID, boolean enabled) throws EdgeServiceFault;
	void changeAutoOfflieCopyStatus(int[] nodeID, boolean enabled, boolean forceSmartCopy) throws EdgeServiceFault;
	
	void changeHeartBeatStatusForGroup(int groupID, int groupType, boolean enabled) throws EdgeServiceFault;
	void changeAutoOfflieCopyStatusForGroup(int groupID, int groupType, boolean enabled) throws EdgeServiceFault;
	
	void redeployPolicyByESX(int esxID) throws EdgeServiceFault;
	DiscoveryESXOption getESXInformation(int id) throws EdgeServiceFault;
	
	int updateMultipleNodeByIds(int[] nodeID, String globalUsername, String globalPassword, boolean forceManaged, boolean usingOrignalCredential) throws EdgeServiceFault;
	int updateMultipleNodeForGroup(int gatewayId, int groupID, int groupType, String globalUsername, String globalPassword, boolean forceManaged, boolean usingOrignalCredential) throws EdgeServiceFault;
	
	VMSnapshotsInfo[] getVMSnapshots(Node node) throws EdgeServiceFault;
	int shutDownVM(Node node) throws EdgeServiceFault;
	String getCurrentRunningSnapshot(Node node) throws EdgeServiceFault; 
	void startFailover(Node node, VMSnapshotsInfo vmSnapInfo) throws EdgeServiceFault;
	boolean isFailoverJobFinish(Node node) throws EdgeServiceFault;
	ARCFlashNode getARCFlashNodeInfo(Node node) throws EdgeServiceFault;
	List<Node> getVMRunningList(List<Node> nodeList) throws EdgeServiceFault;
	String getInstalldHbbuServer() throws EdgeServiceFault;
	
	// Manage Off-site VCM Converters
	
	List<OffsiteVCMConverterInfo> getOffsiteVCMConverters(
		List<Integer> specificConverters
		) throws EdgeServiceFault;
	
	List<OffsiteVCMConverterSavingStatus> updateOffsiteVCMConverters(
		List<OffsiteVCMConverterInfo> converterInfoList
		) throws EdgeServiceFault;
	
	void cancelUpdatingOffsiteVCMConverters(
		) throws EdgeServiceFault;
	
	String updateOffsiteVCMConvertersAsync(
		List<OffsiteVCMConverterInfo> converterInfoList
		) throws EdgeServiceFault;
	
	List<OffsiteVCMConverterSavingStatus> getOffsiteVCMConverterUpdatingStatus(
		String savingSessionId
		) throws EdgeServiceFault;
	
	void deleteOffsiteVCMConverterUpdatingSession(
		String savingSessionId
		) throws EdgeServiceFault;
	
	List<SourceMachineNetworkAdapterInfo> getSourceMachineNetworkAdapterInfoList(Node node) throws EdgeServiceFault;
	void saveSourceMachineNetworkAdapterInfo(Node node, List<SourceMachineNetworkAdapterInfo> networkAdapterList) throws EdgeServiceFault;
	int getSourceMachineNetworkAdapterSize(Node node) throws EdgeServiceFault;
	
	StandbyVMNetworkInfo getStandbyVMNetworkInfo(Node node) throws EdgeServiceFault;
	void saveStandbyVMNetworkInfo(Node node, StandbyVMNetworkInfo standbyVMNetworkInfo) throws EdgeServiceFault; 

	D2DMergeJobStatus getMergeJobStatusById(int nodeId) throws EdgeServiceFault;
	List<D2DMergeJobStatus> getMergeJobStatus(List<Integer> nodeIds) throws EdgeServiceFault;

	void specifyESXServerForRVCM(int hostID, DiscoveryESXOption option,
			boolean isForceSave) throws EdgeServiceFault;

	List<Task> getTaskList() throws EdgeServiceFault;
	void deleteTask( Integer taskID ) throws EdgeServiceFault;
	
	int saveNodeFilters(NodeFilterGroup filterGroup) throws EdgeServiceFault;
	
	int saveFilters(BaseFilter filter) throws EdgeServiceFault;
	
	List<BaseFilter> getFilters(FilterType filterType) throws EdgeServiceFault;
	
	void deleteFilter(int id) throws EdgeServiceFault;

	HostConnectInfo getVCMConverterByHostId(int hostId) throws EdgeServiceFault;
	
	HostConnectInfo getMonitorConnectInfoByHostId(int hostId) throws EdgeServiceFault;
	
	List<JobHistory> getLatestJobHistoriesByNodeId(int nodeId) throws EdgeServiceFault;
	
	AddNodeResult addNodes(List<NodeRegistrationInfo> nodeList) throws EdgeServiceFault;

	List<Node> getNodeListByIDs(List<Integer> ids) throws EdgeServiceFault;
	
	PagingResult<Node> getNodePagingListByIDs(List<Integer> ids, PagingConfig config) throws EdgeServiceFault;
	
	int getCountOfHostWithVSBTask() throws EdgeServiceFault;
	
	int getReplicationQueueSize(int nodeId) throws EdgeServiceFault;
	
	void doManualDiscovery() throws EdgeServiceFault;
	
	void doAutoDiscovery() throws EdgeServiceFault;

	DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(DiscoveryESXOption esxOption, boolean recursive)	throws EdgeServiceFault;
	List<Integer> getNodesNeedRemoteDeploy(List<Integer> nodeIds)throws EdgeServiceFault;
	
	NodeDetail getNodeDetailInformationByVMID(String vmInstanceUUID) throws EdgeServiceFault;
	
	String getNodeAuthUuid(String uuid) throws EdgeServiceFault;
	
	DiscoveryHyperVOption getHyperVInformation(int id) throws EdgeServiceFault;
	void updateHyperVSource(DiscoveryHyperVOption hypervOption) throws EdgeServiceFault;
	void redeployPolicyByHyperV(int hyperVID) throws EdgeServiceFault;
	
	List<EsxVSphere> getEsxInfoList(int gatewayid, List<VsphereEntityType> types) throws EdgeServiceFault;
	List<EsxVSphere> getHyperVInfoList(int gatewayid) throws EdgeServiceFault;
	
	HostConnectInfo getVsbMonitorByHostId(int hostId) throws EdgeServiceFault;
	
	boolean specifyHypervisor(LicenseMachineType machineType, Hypervisor hypervisor, List<Integer> nodeIds) throws EdgeServiceFault;
	Hypervisor getSpecifiedHypervisor(int hostId) throws EdgeServiceFault;
	
	/**
	 * added by tonyzhai
	 * export CSV object which include header property
	 * 
	 * @param groupID
	 * @param grouptype
	 * @param nodeFilter
	 * @return
	 * @throws EdgeServiceFault
	 */
	public CSVObject<ExportNode> getHostNodeCSVObject(int groupID, int grouptype,EdgeNodeFilter nodeFilter) throws EdgeServiceFault;
	
	List<ProtectedResource> getProtectedResources(List<ProtectedResourceIdentifier> resourceIds)throws EdgeServiceFault;
	
	PagingResult<NodeEntity> getPagingNodes(NodeGroup group, List<NodeFilter> nodeFilters, SortablePagingConfig<NodeSortCol> config) throws EdgeServiceFault;
	List<NodeVcloudSummary> getVcloudPropertiesByNodeIds(List<Integer> nodeIds)throws EdgeServiceFault;
	
	List<EdgeHostBackupStats> getBackupStats(int offSet) throws EdgeServiceFault;
	
	HostConnectInfo getD2DConnectionInfo() throws EdgeServiceFault;
	
	int validateProxyInfo( GatewayId gatewayId,
		String hostName, String protocol, int port,
		String userName, String password,
		boolean isUseTimeRange, boolean isUseBackupSet ) throws EdgeServiceFault;

	List<RecoveryPointDataItem> getRecoveryPointData() throws EdgeServiceFault;
	long getRPSDatastoreVolumeMaxSize() throws EdgeServiceFault;
	void changeNodesCredentials(List<Integer> nodeIds, String userName, String password)throws EdgeServiceFault;
        BackupStatusByGroup getLastBackupStatusByGroup(int groupType, int groupId)throws EdgeServiceFault;

        MonitorHyperVInfo getAdapterForInstantVM(HypervisorWrapper hw) throws EdgeServiceFault;
        @Deprecated
    	void cutAllRemoteConnections4VM(ConnectionContext context) throws EdgeServiceFault;
    	@Deprecated
    	FileFolderItem getFileItems4VM(ConnectionContext context, String inputFolder,
    			String username, String password, boolean bIncludeFiles,
    			int browseClient) throws EdgeServiceFault;
    	@Deprecated
    	void createFolder4VM(ConnectionContext context, String parentPath, String subDir)
    			throws EdgeServiceFault;
    	@Deprecated
    	Volume[] getVolumes4VM(ConnectionContext context) throws EdgeServiceFault;
      
    	NodeDetail getNodetailByIpOrHostName(HypervisorWrapper hw) throws EdgeServiceFault;
    	
    	int getHostIdByHostNameOrIP(int gateway, String name, String ip, int isVisible);
	//May sprint
    int triggerCollectDiagnosticData(Node node, DiagInfoCollectorConfiguration diagObj) throws EdgeServiceFault;
    int triggerCollectDiagnosticDataForNodes(List<Node> nodes, DiagInfoCollectorConfiguration diagObj) throws EdgeServiceFault;	
	int triggerCollectDiagnosticDataForLinuxNode(Node node,
			DiagInfoCollectorConfiguration diagObj) throws EdgeServiceFault;
	int triggerCollectDiagnosticDataForConsoleNode(DiagInfoCollectorConfiguration diagObj) throws EdgeServiceFault;
	
	void setWarnningAcknowledged(List<Integer> nodeIds) throws EdgeServiceFault;
	int isASBUAgentInstalled(int nodeId) throws EdgeServiceFault;
	GatewayEntity getGatewayByHostId(int nodeId) throws EdgeServiceFault;
	
	DiscoveryHyperVEntityInfo getHyperVTreeRootEntity(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault;
	void verifyFCPFC(List<Integer> nodeIDs) throws EdgeServiceFault;
	List<RecoveryPointDataItem> getD2DBackupData() throws EdgeServiceFault;
	List<Node> getLinuxBackupserverList(int gatewayId) throws EdgeServiceFault;
	void startAgentFilecopyNow(List<Integer> nodeIdList)throws EdgeServiceFault;
	void startAgentFileArchiveNow(List<Integer> nodeIdList) throws EdgeServiceFault;
	void bindPolicyD2DRole(int policyId, int hostId, D2DRole d2dRole)throws EdgeServiceFault;
}
