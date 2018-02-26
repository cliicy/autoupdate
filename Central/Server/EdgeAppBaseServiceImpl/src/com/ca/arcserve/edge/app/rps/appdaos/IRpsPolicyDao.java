package com.ca.arcserve.edge.app.rps.appdaos;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.WildcardConversion;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDeployMessage;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsPolicy;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsPolicyNode;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy.PolicyDeployReason;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy.PolicyDeployStatus;

public interface IRpsPolicyDao {
	
	@StoredProcedure(name = "dbo.as_edge_rps_policy_delete_by_node")
	void as_edge_rps_policy_delete_by_node(int nodeId);

	@StoredProcedure(name = "dbo.as_edge_rps_policy_delete")
	void as_edge_rps_policy_delete(int policyId);

	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_policy_update")
	void as_edge_rps_policy_update(int id,
			@In(jdbcType = Types.VARCHAR) String policyUUID,
			@In(jdbcType = Types.VARCHAR) String policyName,
			@In(jdbcType = Types.VARCHAR) String fileStorePath, int nodeId,
			long storageLimtSize,
			@In(jdbcType = Types.VARCHAR) String exSetting,
			PolicyDeployStatus status,
			@Out(jdbcType = Types.INTEGER) int[] policyid);
	
	@StoredProcedure(name = "dbo.as_edge_rps_policy_update_by_uuid")
	void as_edge_rps_policy_update_by_uuid(
			@In(jdbcType = Types.VARCHAR) String policyUUID,
			@In(jdbcType = Types.VARCHAR) String policyName, int nodeId,
			@In(jdbcType = Types.VARCHAR) String exSetting,
			@Out(jdbcType = Types.INTEGER) int[] policyid);

	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_policy_update_status")
	void as_edge_rps_policy_update_status(int policyId,
			int nodeID, PolicyDeployStatus status);

	@StoredProcedure(name = "dbo.as_edge_rps_policy_list")
	void as_edge_rps_policy_list(int policyId,
			@ResultSet List<EdgeRpsPolicy> policy);

	@StoredProcedure(name = "dbo.as_edge_rps_policy_list_by_uuid")
	void as_edge_rps_policy_list_by_uuid(String policyUUID,
			@ResultSet List<EdgeRpsPolicy> policy);

	@StoredProcedure(name = "dbo.as_edge_rps_policy_list_all")
	void as_edge_rps_policy_list_all(@ResultSet List<EdgeRpsPolicy> policy);

	@StoredProcedure(name = "dbo.as_edge_rps_policy_list_by_replication_id")
	void as_edge_rps_policy_list_by_replication_id(int replicationId,
			@ResultSet List<EdgeRpsPolicy> policy);

	@StoredProcedure(name = "dbo.as_edge_rps_policy_list_by_status")
	void as_edge_rps_policy_list_by_status(PolicyDeployStatus status,
			@ResultSet List<EdgeRpsPolicy> policy);

	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_policy_get_status")
	void as_edge_rps_policy_get_status(int policyId,
			@Out(jdbcType = Types.INTEGER) int[] status);

	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_policy_assign_replication_setting")
	void as_edge_rps_policy_assign_replication_setting(int policyId,
			int replicationId);

	@StoredProcedure(name = "dbo.as_edge_rps_policy_unassign_replication_setting")
	void as_edge_rps_policy_unassign_replication_setting(int policyId);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_policy_list_by_status_time")
	void as_edge_rps_policy_list_by_status_time(PolicyDeployStatus status,
			Timestamp time, List<EdgeRpsPolicy> policy);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_policy_delete_by_status_time")
	void as_edge_rps_policy_delete_by_status_time(PolicyDeployStatus status,
			Timestamp time);

	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_policy_get_total_upper_limit")
	void as_edge_rps_policy_get_total_upper_limit(int nodeId,
			@WildcardConversion @In(jdbcType = Types.VARCHAR) String path,
			@Out(jdbcType = Types.BIGINT) long[] totalUpperLimit);

	@StoredProcedure(name = "dbo.as_edge_rps_policy_list_by_datastore_id")
	void as_edge_rps_policy_list_by_datastore_id(int datastoreId,
			@ResultSet List<EdgeRpsPolicy> policy);

	@StoredProcedure(name = "dbo.as_edge_rps_policy_list_by_datastore_uuid")
	void as_edge_rps_policy_list_by_datastore_uuid(
			int nodeid,//TODO: not implement
			@In(jdbcType = Types.VARCHAR) String datastoreId,
			@ResultSet List<EdgeRpsPolicy> policy);
	
	@StoredProcedure(name = "dbo.as_edge_rps_policy_assign")
	void as_edge_rps_policy_assign(int policyId, int nodeId,PolicyDeployStatus deploy_status,PolicyDeployReason depoly_reason);
	
	@Deprecated
	@StoredProcedure(name = "as_edge_rps_policy_node_map_update")
	void as_edge_rps_policy_node_map_update(int policyId, int nodeId,PolicyDeployStatus deploy_status,PolicyDeployReason depoly_reason);

	@StoredProcedure(name = "as_edge_rps_datastore_node_map_update")
	void as_edge_rps_dedup_node_map_update(int policyId, int nodeId,PolicyDeployStatus deploy_status,PolicyDeployReason depoly_reason);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_policy_unassign")
	void as_edge_rps_policy_unassign(int policyId, int nodeId);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_policy_unassign_by_node")
	void as_edge_rps_policy_unassign_by_node(int nodeId);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_policy_unassign_by_policy")
	void as_edge_rps_policy_unassign_by_policy(int policyId);
	
	@StoredProcedure(name = "dbo.as_edge_rps_policy_list_by_nodeId")
	void as_edge_rps_policy_list_by_nodeId(int nodeId,
			@ResultSet List<EdgeRpsPolicy> policy);
	
	@StoredProcedure(name = "as_edge_rps_policy_list_by_deploy_reason")
	void as_edge_rps_policy_list_by_deploy_reason(PolicyDeployReason depoly_reason,
			@ResultSet List<EdgeRpsPolicy> policy);

	@StoredProcedure(name = "as_edge_rps_datastore_assign")
	void as_edge_rps_dedup_assign(int policyId, Integer nodeId,
			PolicyDeployStatus undeploy, PolicyDeployReason assign);

	@Deprecated
	@StoredProcedure(name = "as_edge_rps_policy_update_message")
	void as_edge_rps_policy_update_message(int policyId, int nodeId,
			int type, String errCode, String msg);

	// DeployMsgType is bit wise type, it can send to this interface to get
	// multiple type message
	@Deprecated
	@StoredProcedure(name = "as_edge_rps_policy_get_msg_list")
	void as_edge_rps_policy_get_msg_list(int policyId, int nodeId, int type,
			@ResultSet List<EdgeRpsDeployMessage> msg);
	
	@Deprecated
	@StoredProcedure(name = "as_edge_rps_policy_delete_msg")
	void as_edge_rps_policy_delete_msg(int policyId, int nodeId);

	@StoredProcedure(name = "as_edge_rps_policy_node_list")
	void as_edge_rps_policy_node_list(int policyId, int nodeId, @ResultSet List<EdgeRpsPolicyNode> map);
}
