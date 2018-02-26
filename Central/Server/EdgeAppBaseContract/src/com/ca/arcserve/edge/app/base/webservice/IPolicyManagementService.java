package com.ca.arcserve.edge.app.base.webservice;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.rps.webservice.data.RpsArchiveConfiguationWrapper;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.replication.HttpProxy;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.CloudProviderInfo;
import com.ca.arcflash.webservice.data.vsphere.ESXServer;
import com.ca.arcflash.webservice.data.vsphere.ResourcePool;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ItemOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.RebootType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeInfoList4VM;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.AssignPolicyCheckResultCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.BackupPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ParsedBackupPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlolicyPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.msp.webservice.contract.MspReplicationDestination;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.job.filecopyJob.ManualFilecopyParam;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.job.replicationJob.ManualReplicationRPSParam;

public interface IPolicyManagementService
{
	
	
	ParsedBackupPolicy getParsedBackupPolicy(
		int policyId
		) throws EdgeServiceFault;
	
//	List<ItemOperationResult> assignPolicyToNodes(
//		int policyId,
//		List<Integer> nodeIdList
//		) throws EdgeServiceFault;
	
//	List<ItemOperationResult> removePolicyFromNodes(
//		int policyId,
//		List<Integer> nodeIdList,
//		int resvSetting
//		) throws EdgeServiceFault;
	
	List<Node> getNodesByPolicy(
		int policyId
		) throws EdgeServiceFault;
	
	
	
	public List<ItemOperationResult> redeployPolicyToNodes(
		List<Integer> nodeIdList,
		int policyType,
		int policyId
		) throws EdgeServiceFault;
	
	public void redeployPolicies(
		int policyType,
		List<Integer> policyIdList
		) throws EdgeServiceFault;
	
	public void enablePolicies(
			boolean value,
			List<Integer> policyIdList,RebootType nodeInstallType, Date installtime
			) throws EdgeServiceFault;

	public boolean redeployPoliciesEx(
			int policyType
			) throws EdgeServiceFault;
		
	public void doPolicyDeploymentNow(
		) throws EdgeServiceFault;
	
	public NodeInfoList4VM getNodesWhoIsUsingPolicy4VM(
			int policyId
			) throws EdgeServiceFault;
		
	public void regEdgeToProxy(int policyType, int policyId, int hostId, boolean forceFlag) throws EdgeServiceFault;

	public void redeployPolicy2RightNodes(int policyType, int policyId, int hostId) throws EdgeServiceFault;
	
	public String getEdgePolicyName(int policyId) throws EdgeServiceFault;
	public int copyEdgePolicy(int policyId, String newPolicyName) throws EdgeServiceFault;
	public int getPolicyIdByName(String policyName) throws EdgeServiceFault;
	
	// By default, we have policy type, the information is enough to check
	// whether specified nodes can be assigned with policy, but some features,
	// i.e. D2DOD, defined other field to describe policy type, we have to
	// pass in the policy ID. Sometimes later, we should remove the new policy
	// type and define it into old policy type field.
	//
	public AssignPolicyCheckResultCode canNodesBeAssignedWithPolicy(
		List<Integer> nodeIdList,
		int policyType,
		int policyId
		) throws EdgeServiceFault;
	
	public AssignPolicyCheckResultCode canNodesOfGroupsBeAssignedWithPolicy(
		List<Integer> groupIdList,
		int policyType,
		int policyId
		) throws EdgeServiceFault;
	
	BackupPolicy getPolicyInfo(
		int policyId,
		boolean needDetails
		) throws EdgeServiceFault;
	
	public List<PolicyInfo> getPlansByNodeNameIp ( int gatewayid, String name, String ip ) throws EdgeServiceFault; 
	/**
	 * Get policies assigned to the specify host
	 * 
	 * @param hostId
	 *            The host to query
	 * @return The policy list, if no policy assigned to the host, then return empty list
	 */
	public List<PolicyInfo> getHostPolicies(int hostId);
	
	int createUnifiedPolicy(UnifiedPolicy policy) throws EdgeServiceFault;
	
    List<ItemOperationResult> deleteUnifiedPolicies(
            List<Integer> idList
            ) throws EdgeServiceFault;
    
	List<PolicyInfo> getPlanList() throws EdgeServiceFault;
	
	PolicyPagingResult getPlanListByPaging(PlolicyPagingConfig config) throws EdgeServiceFault;

	List<Integer> getPlanIds() throws EdgeServiceFault;
	
	public UnifiedPolicy loadUnifiedPolicyById(int planId) throws EdgeServiceFault;
	
	public UnifiedPolicy loadUnifiedPolicyByUuid(String uuid) throws EdgeServiceFault;
	
	void updateUnifiedPolicy(UnifiedPolicy policy) throws EdgeServiceFault;
	
	long testConnectionToCloud(ArchiveCloudDestInfo in_cloudInfo)  throws EdgeServiceFault ;
	
	String GetArchiveDNSHostName() throws EdgeServiceFault;
	
	List<CloudProviderInfo> getCloudProviderInfos()throws EdgeServiceFault;

	List<MspReplicationDestination> getMspReplicationDestinations(String localFQDNName, RpsHost mspServer, HttpProxy clientHttpProxy) throws EdgeServiceFault;
	
	ResourcePool[] getResourcePool(
		GatewayId gatewayId,
		VirtualCenter vc,
		ESXServer esxServer,
		ResourcePool parentResourcePool
		) throws EdgeServiceFault;
	
	List<Integer> getLowVersionNodeIdsByPlanIds(List<Integer> planIds) throws EdgeServiceFault;

	/**
	 * Get planId and nodeId pair
	 * @param policy
	 * @return <planId,nodeId>
	 * @throws EdgeServiceFault
	 */
	List<ValuePair<Integer, Integer>> getPlanIdsWithTheSameNodeIds(UnifiedPolicy policy) throws EdgeServiceFault;

	List<ManualReplicationRPSParam> getReplicationRpsParamsByPolicyName(String policyName) throws EdgeServiceFault;

	Integer backupNodesByPolicyIdList(List<Integer> policyIdList, int backupType, String jobName) throws EdgeServiceFault;
	
	public List<RpsArchiveConfiguationWrapper> getRpsArchiveConfigSummary(String planUUID, boolean isNeedToEncr);

	public List<Integer> checkRPSVersion(List<Integer> policyIdList) throws EdgeServiceFault;

	List<ManualFilecopyParam> getFilecopyParamsByPolicyId(long policyId) throws EdgeServiceFault;

	List<ManualFilecopyParam> getFileArchiveParamsByPolicyId(long policyId) throws EdgeServiceFault;
}
