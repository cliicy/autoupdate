package com.ca.arcserve.edge.app.rps.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.ITransactionDao;
import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStoreNode;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDeployMessage;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy.PolicyDeployReason;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy.PolicyDeployStatus;

public interface IRpsDataStoreDao extends ITransactionDao{
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_update")
	void as_edge_rps_datastore_setting_update(int Id, int node_id,
			@In(jdbcType = Types.VARCHAR) String datastoreName,
			@In(jdbcType = Types.VARCHAR) String datastoreSetting,
			PolicyDeployStatus status,
			@Out(jdbcType = Types.INTEGER) int[] datastoreId);

	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_update")
	void as_edge_rps_datastore_setting_update(int Id, int node_id,
			@In(jdbcType = Types.VARCHAR) String datastoreName,
			@In(jdbcType = Types.VARCHAR) String datastoreSetting,
			@Out(jdbcType = Types.INTEGER) int[] datastoreId);
	
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_update_by_uuid")
	void as_edge_rps_datastore_setting_update(int Id, int node_id,
			@In(jdbcType = Types.VARCHAR) String uuid,
			@In(jdbcType = Types.VARCHAR) String datastoreName,
			@In(jdbcType = Types.VARCHAR) String datastoreSetting,
			@Out(jdbcType = Types.INTEGER) int[] datastoreId);
	
	@StoredProcedure(name = "as_edge_rps_datastore_setting_updateStatus")
	void as_edge_rps_datastore_setting_updateStatus(int Id, int node_id,
			@In(jdbcType = Types.VARCHAR) String uuid,
			@In(jdbcType = Types.VARCHAR) String message);
	
	
	@StoredProcedure(name = "dbo.as_edge_rps_get_datastore_setting_statusMessage")
	void as_edge_rps_get_datastore_setting_statusMessage(int datastoreId,
			@Out(jdbcType = Types.VARCHAR) String[] message);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_delete")
	void as_edge_rps_datastore_setting_delete(int datastoreId);

	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_delete_by_uuid")
	void as_edge_rps_datastore_setting_delete(int rpsnodeid, String uuid);
	
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_list")
	void as_edge_rps_datastore_setting_list(int datastoreId,
			@ResultSet List<EdgeRpsDataStore> datastoreList);
	
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_list_by_uuid")
	void as_edge_rps_datastore_setting_list(int nodeId, String uuid,
			@ResultSet List<EdgeRpsDataStore> datastoreList);
	
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_history_list_by_uuid")
	void as_edge_rps_datastore_setting_history_list(int nodeId, String uuid, Date timeStamp,
			@ResultSet List<EdgeRpsDataStore> datastoreList);
	
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_list_all")
	void as_edge_rps_dedup_setting_list_all(@ResultSet List<EdgeRpsDataStore> datastoreList);

	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_assign")
	void as_edge_rps_datastore_setting_assign(int datastoreId, int policyId);

	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_unassign")
	void as_edge_rps_datastore_setting_unassign(int policyId);

	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_list_by_policy")
	void as_edge_rps_datastore_setting_list_by_policy(int policyId,
			@ResultSet List<EdgeRpsDataStore> datastore);

	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_list_by_nodeid")
	void as_edge_rps_datastore_setting_list_by_nodeid(int nodeId,
			@ResultSet List<EdgeRpsDataStore> datastore);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_list_by_datastorename")
	void as_edge_rps_datastore_setting_list_by_datastoreName(int nodeId,String datastoreName,
			@ResultSet List<EdgeRpsDataStore> datastoreList);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_update_status")
	void as_edge_rps_dedup_setting_update_status(int datastoreId,int node_id, PolicyDeployStatus status);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_list_by_status")
	void as_edge_rps_datastore_setting_list_by_status(PolicyDeployStatus status,
			@ResultSet List<EdgeRpsDataStore> datastoreList);

	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_setting_get_status")
	void as_edge_rps_datastore_setting_get_status(int datastoreId,
			@Out(jdbcType = Types.INTEGER) int[] status);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_assign")
	void as_edge_rps_datastore_assign(int datastoreId, int nodeId,PolicyDeployStatus deploy_status,PolicyDeployReason depoly_reason);
	
	@Deprecated
	@StoredProcedure(name = "as_edge_rps_datastore_node_map_update")
	void as_edge_rps_datastore_node_map_update(int datastoreId, int nodeId,PolicyDeployStatus deploy_status,PolicyDeployReason depoly_reason);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_unassign")
	void as_edge_rps_datastore_unassign(int datastoreId, int nodeId);
	
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_unassign_by_node")
	void as_edge_rps_datastore_unassign_by_node(int nodeId);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_datastore_unassign_by_datastore")
	void as_edge_rps_datastore_unassign_by_datastore(int datastoreId);
	
	@Deprecated
	@StoredProcedure(name = "as_edge_rps_datastore_list_by_deploy_reason")
	void as_edge_rps_datastore_list_by_deploy_reason(PolicyDeployReason depoly_reason,
			@ResultSet List<EdgeRpsDataStore> datastore);

	@Deprecated
	@StoredProcedure(name = "as_edge_rps_datastore_update_message")
	void as_edge_rps_datastore_update_message(int datastoreId, int nodeId,
			int type, String errCode, String msg);

	@Deprecated
	// DeployMsgType is bit wise type, it can send to this interface to get
	// multiple type message
	@StoredProcedure(name = "as_edge_rps_datastore_get_msg_list")
	void as_edge_rps_datastore_get_msg_list(int datastoreId, int nodeId,
			int type, @ResultSet List<EdgeRpsDeployMessage> msg);
	
	@Deprecated
	@StoredProcedure(name = "as_edge_rps_datastore_delete_msg")
	void as_edge_rps_datastore_delete_msg(int datastoreId, int nodeId);
	
	@Deprecated
	@StoredProcedure(name = "as_edge_rps_datastore_node_list")
	void as_edge_rps_datastore_node_list(int datastoreId, int nodeId, @ResultSet List<EdgeRpsDataStoreNode> map);
}
