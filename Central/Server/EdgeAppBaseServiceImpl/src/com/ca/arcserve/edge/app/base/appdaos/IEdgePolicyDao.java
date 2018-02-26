package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.ITransactionDao;
import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.common.D2DRole;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanEnableStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;

public interface IEdgePolicyDao extends ITransactionDao
{
	@StoredProcedure(name = "dbo.as_edge_policy_list")
	void as_edge_policy_list(int id, int flag, @ResultSet List<EdgePolicy> policys);
	
	@StoredProcedure(name = "dbo.as_edge_policy_list_by_uuid")
	void as_edge_policy_list_by_uuid(String uuid, @ResultSet List<EdgePolicy> policys);

	@StoredProcedure(name = "dbo.as_edge_policy_list_bytype")
	void as_edge_policy_list_bytype(int type, int flag, @ResultSet List<EdgePolicy> policys);
	
	@StoredProcedure(name = "dbo.as_edge_policy_list_by_linux_d2d_server")
	void as_edge_policy_list_by_linux_d2d_server(int linuxD2DServerId, @ResultSet List<EdgePolicy> policys);
	
	@StoredProcedure(name="dbo.as_edge_policy_list_byProxyHostId")
	void as_edge_policy_list_byProxyHostId(int proxyHostId, @ResultSet List<EdgePolicy> policys);

	@StoredProcedure(name = "dbo.as_edge_policy_getid_by_name")
	void as_edge_policy_getid_by_name(@In(jdbcType = Types.VARCHAR) String name, @Out(jdbcType = Types.INTEGER) int[] id);

	@StoredProcedure(name = "dbo.as_edge_host_list_bypolicyid")
	void as_edge_host_list_bypolicyid(int olicyid,
			@ResultSet List<EdgeHost> hosts);

	@StoredProcedure(name = "dbo.as_edge_policy_update")
	void as_edge_policy_update(int policyid,                       
			@In(jdbcType = Types.VARCHAR) String name,                        
			@In(jdbcType = Types.VARCHAR) String policyxml,                        
			int type,                       
			int contentflag,                       
			@In(jdbcType = Types.VARCHAR) String version,  
			@In(jdbcType = Types.VARCHAR) String uuid,
			int producttype,
			int enableStatus,
			@Out(jdbcType = Types.INTEGER) int[] id);

	@StoredProcedure(name = "dbo.as_edge_policy_remove")
	void as_edge_policy_remove(int id);
	
	@StoredProcedure(name = "dbo.as_edge_policy_by_hostID")
	void as_edge_policy_by_hostID(int hostID,@Out(jdbcType = Types.VARCHAR)String[] policyName);
	
	@StoredProcedure(name = "dbo.as_edge_policy_AssignPolicy")
	void assignPolicy(
		int hostId,
		int policyType,
		int policyId,
		int deployStatus,
		int nodeDeployType);

	@StoredProcedure(name = "dbo.as_edge_policy_UnassignPolicy")
	void unassignPolicy(
		int hostId,
		int policyType,
		int flags );

	@StoredProcedure(name = "dbo.as_edge_policy_RedeployPolicy")
	void redeployPolicy(
		int policyType,
		int policyId ,
		int deployreason);
	
	@StoredProcedure(name = "dbo.as_edge_policy_GetDeployTasks_Running")
	void getDeployTasks_Running(
		int policyType,
		@ResultSet List<EdgePolicyDeployTask> taskList );
	
	@StoredProcedure(name = "dbo.as_edge_policy_GetDeployTasks_Pending")
	void getDeployTasks_Pending(
		int policyType,
		//int tryCountLimit,
		int retryInterval,
		@ResultSet List<EdgePolicyDeployTask> taskList );
	
	@StoredProcedure(name = "dbo.as_edge_policy_GetDeployTasks_Failed")
	void getDeployTasks_Failed(
		int policyType,
		int tryCountLimit,
		int retryInterval,
		@ResultSet List<EdgePolicyDeployTask> taskList );
	
	@StoredProcedure(name = "dbo.as_edge_policy_deployTasksBy_HostId_PolicyId")
	void as_edge_policy_deployTasksBy_HostId_PolicyId(
		int hostId,
		int policyId,
		int tryCountLimit,
		@ResultSet List<EdgePolicyDeployTask> taskList );
	
	@StoredProcedure(name = "dbo.as_edge_policy_UpdateDeployTaskStatus_by_hostid")
	void updateDeployTasks_UpdateDeployTaskStatus_ByHostId(
		int hostId,
		int status);
	
	@StoredProcedure(name = "dbo.as_edge_policy_UpdateDeployTaskStatus_by_policyid")
	void updateDeployTasks_UpdateDeployTaskStatus_ByPolicyId(
		int hostId,
		int status);
	
	@StoredProcedure(name = "dbo.as_edge_policy_GetHostPolicyMap")
	void getHostPolicyMap(
		int hostId,
		int policyType,
		@ResultSet List<EdgeHostPolicyMap> mapList );
	
	@StoredProcedure(name = "dbo.as_edge_policy_SetHostPolicyMap")
	void setHostPolicyMap(
		int hostId,
		int policyType,
		int policyId,
		int deployStatus,
		int deployReason,
		int deployFlags,
		int tryCount,
		@In(jdbcType = Types.TIMESTAMP) Date lastSuccessfulDeployment );
	
	@StoredProcedure(name = "dbo.as_edge_policy_DeleteHostPolicyMap")
	void deleteHostPolicyMap(
		int hostId,
		int policyType );
	
	@StoredProcedure(name = "dbo.as_edge_policy_DeleteHostPolicyMap_ByPolicyId")
	void deleteHostPolicyMapByPolicyId(
		int policyId,
		int policyType );
	
	@StoredProcedure(name = "dbo.as_edge_policy_GetNodesWhoIsUsingPolicy")
	void getNodesWhoIsUsingPolicy(
		int policyId,
		@ResultSet List<EdgePolicyHost> hostList );
	
	@StoredProcedure(name = "dbo.as_edge_policy_setPolicyDeployWarningError")
	void setPolicyDeployWarningErrorMessage(
		int hostId,
		int policyType,
		String warning,
		String error );
	
	@StoredProcedure(name = "dbo.as_edge_policy_deletePolicyDeployWarningError")
	void deletePolicyDeployWarningErrorMessage(
		int hostId,
		int policyType );
	
	@StoredProcedure(name = "dbo.as_edge_policy_getPolicyDeployWarningError")
	void getPolicyDeployWarningErrorMessage(
		int hostId,
		int policyType,
		@ResultSet List<EdgePolicyDeployWarningErrorMessage> warningErrorList );
	

	@StoredProcedure(name="dbo.as_edge_policy_by_hostUuid")
	void as_edge_policy_by_hostUuid(String d2dUuid, String d2dUuidEn, @ResultSet List<EdgePolicyHostUuid> policies);
	
	@StoredProcedure(name="dbo.as_edge_plan_by_hostUuid")
	void findPlanByHostUUID(String d2dUuid, String d2dUuidEn, @ResultSet List<EdgePolicyHostUuid> policies);
	
	@StoredProcedure(name = "as_edge_policy_resettrycount")
	void as_edge_policy_resetTrycount(int hostId, int policyType, int policyId );

	@StoredProcedure(name = "as_edge_policy_findvcmnodesfromvsp")
	void as_edge_policy_findVcmNodesFromVsp(int hostId, int policyId, List<Integer> result );
	
	@StoredProcedure(name = "dbo.as_edge_policy_AddD2DRole")
	void as_edge_policy_AddD2DRole(int policyId, int hostId, D2DRole d2dRole);

	@StoredProcedure(name="dbo.as_edge_policy_list_by_hostId")
	void as_edge_policy_list_by_hostId(int hostId, @ResultSet List<PolicyInfo> policyList);
	
	@StoredProcedure(name="dbo.as_edge_plan_getPlanList")
	void as_edge_plan_getPlanList(@ResultSet List<PolicyInfo> policyList);
	
	@StoredProcedure(name="dbo.as_edge_plan_getPlanList_by_paging")
	void as_edge_plan_getPlanList_by_paging(int pagesize,int startpos,
			@In(jdbcType = Types.VARCHAR) String orderType,
			@In(jdbcType = Types.VARCHAR) String orderCol,
			@In(jdbcType = Types.INTEGER) int gatewayId,
			@Out(jdbcType = Types.INTEGER) int[]totalcount,
			@ResultSet List<PolicyInfo> policyList);
	
	@StoredProcedure(name="dbo.as_edge_plan_getActiveJobCount")
	void as_edge_plan_getActiveJobCount(String planUuid, @ResultSet List<JobHistory> history);
	
	/**
	 * @param policyId
	 * @param nodeDeployType  nodeDeployType=0: deploy from node , nodeDeployType=1: deploy from group , nodeDeployType= -1 all deploy list
	 * @param policyList
	 */
	@StoredProcedure(name="dbo.as_edge_plan_getDeployList")
	void as_edge_plan_getDeployList(int policyId,int nodeDeployType, @ResultSet List<EdgeHostPolicyMap> policyList);
	
	/**
	 * Get the overall deploy status for specified policy.
	 * @param policyId
	 * @param result 0: deploying, 1: deploy failed, 2: deploy success
	 */
	@StoredProcedure(name="dbo.as_edge_policy_getOverallDeployStatus")
	void as_edge_policy_getOverallDeployStatus(int policyId, @Out(jdbcType = Types.INTEGER) int[] result);
	
	@StoredProcedure(name="dbo.as_edge_policy_getStatus")
	void as_edge_policy_getStatus(int policyId, @Out(jdbcType = Types.INTEGER) int[] result);
	
	@StoredProcedure(name="dbo.as_edge_policy_updateStatus")
	void as_edge_policy_updateStatus(int policyId, PlanStatus newStatus);
	
	@StoredProcedure(name="dbo.as_edge_policy_updateStatus_4node")
	void as_edge_policy_updateStatus_4node(int policyId, int newStatus);

	@StoredProcedure(name="dbo.as_edge_policy_updateMapStatus")
	void as_edge_policy_updateMapStatus(int policyId, int nodeId, int deployReason, int deployStatus, int deployFlags, int enableStatus);
	
	@StoredProcedure(name = "dbo.getHostPolicyMapByHostAndPlanTaskType")
	void getHostPolicyMapByHostAndPlanTaskType(
		int hostId,
		int planTaskType,
		@ResultSet List<EdgeHostPolicyMap> mapList );
	
	@StoredProcedure(name = "dbo.as_edge_policy_getGlobalUuid")
	void as_edge_policy_getGlobalUuid(int policyId, @Out(jdbcType = Types.VARCHAR) String[] policyGlobalUuid);
	
	@StoredProcedure(name = "as_edge_policy_getId")
	void as_edge_policy_getId(@In(jdbcType = Types.VARCHAR) String policyGlobalUuid, @Out(jdbcType = Types.INTEGER) int[] policyId);

	@StoredProcedure(name = "as_edge_policy_uuid_by_vminstanceuuid")
	void as_edge_policy_uuid_by_vminstanceuuid(String vmInstanceUUID, @Out(jdbcType = Types.VARCHAR) String[] policyUUID);
	
	@StoredProcedure(name = "as_edge_policy_enable_by_plan_id")
	void as_edge_policy_enable_by_plan_id(int planId, PlanEnableStatus status);
	
	@StoredProcedure(name = "as_edge_policy_enable_nodes_by_plan_id")
	void as_edge_policy_enable_nodes_by_plan_id(int planId, int deployReason, int enablestatus);
	
	
	@StoredProcedure(name = "as_edge_policy_getDeployTaskByHostId")
	void as_edge_policy_getDeployTaskByHostId(int hostId,
			@ResultSet List<EdgePolicyDeployTask> taskList);
	
	@StoredProcedure(name = "as_edge_policy_getDeployTaskByProxyId")
	void as_edge_policy_getDeployTaskByProxyId(int proxyHostId ,
			@ResultSet List<EdgePolicyDeployTask> taskList);
	
	@StoredProcedure(name = "as_edge_policy_getDeployTaskByMonitorHostId")
	void as_edge_policy_getDeployTaskByMonitorHostId(int monitorHostId ,
			@ResultSet List<EdgePolicyDeployTask> taskList);
	
	@StoredProcedure(name = "as_edge_policy_setDeployErrorMessage")
	void as_edge_policy_setDeployErrorMessage(int planId, String deployErrorMessage);
	
	@StoredProcedure(name = "as_edge_plan_group_map_update")
	void as_edge_plan_group_map_update(int groupType, int groupId, int planId);
	
	@StoredProcedure(name = "as_edge_plan_group_map_getProtecteGroupResource")
	void as_edge_plan_group_map_getProtecteGroupResource(int planId, int groupType , @ResultSet List<EdgePolicyGroup> groups);
	
	@StoredProcedure(name = "as_edge_plan_group_map_delete")
	void as_edge_plan_group_map_delete(int groupType, int groupId, int planId);
	
	@StoredProcedure(name = "as_edge_plan_group_map_getPlanByGroup")
	void as_edge_plan_group_map_getPlanByGroup(int groupType,int groupId , @Out int[] planId);

	@StoredProcedure(name = "as_edge_policy_getPlanIdsByNodeIds")
	void as_edge_policy_getPlanIdsByNodeIds(		
			int id,
			@Out int[] planIds );
	
	@StoredProcedure(name = "as_edge_plan_addOrUpdatePlanDestinationMap")
	void addOrUpdatePlanDestinationMap(int planId, int destinationId, int destinationType, String mediaGroupName, int taskType);
	
	@StoredProcedure(name = "as_edge_plan_deletePlanDestinationMap")
	void deletePlanDestinationMap(int planId, int destinationId, int destinationType, int taskType);
	
	@StoredProcedure(name = "as_edge_plan_deletePlanDestinationMapByPlanId")
	void deletePlanDestinationMapByPlanId(int planId);
	
	@StoredProcedure(name = "dbo.as_edge_getPlanCountByTypeAndDesitinationId")
	void getPlanCountByDestinationIdAndType(int destinationId, int destinationType, String mediaGroupName, @Out int[] planCount);
	@StoredProcedure(name = "dbo.as_edge_getPlanNamesByASBUDomainId")
	void getPlanNamesByASBUDomainId(int domainId, @ResultSet List<UnifiedPolicy> planNames);
	
	@StoredProcedure(name = "dbo.as_edge_getPlanNamesByDestinationId")
	void getPlanNamesByDestinationId(int destinationId, @ResultSet List<UnifiedPolicy> planNames);

	@StoredProcedure(name = "dbo.as_edge_policy_GetHostByUUIDPerPlanUsage")
	void getHostByUUIDPerPlanUsage(
		@EncryptSave @In(jdbcType = Types.VARCHAR) String d2dUuid,
		@ResultSet List<EdgeD2DHost> hostList );
	
	@StoredProcedure(name="dbo.as_edge_policy_warning_updateAcknowledge")
	void as_edge_policy_warning_updateAcknowledge(int hostId,int acknowledge);
}

