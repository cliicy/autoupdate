package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;

public interface IEdgeProductDeployDao {
	@StoredProcedure(name = "dbo.as_edge_deploy_target_update")
	void as_edge_deploy_target_update(int targetid, 
			@In(jdbcType = Types.VARCHAR) String serverName,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			@In(jdbcType = Types.VARCHAR) String username,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String password,
			int protocol,
			int port, int productType, 
			@In(jdbcType = Types.VARCHAR) String installDirectory,
			int installDriver,
			int autoStartRRService, 
			int rebootType,
			int selected, 
			@In(jdbcType = Types.TIMESTAMP)Date startDeploymentTime,
			int status, int taskstatus, long msgCode,
			int nodeID, int percentage, 
			@In(jdbcType = Types.VARCHAR) String progressMessage,
			@In(jdbcType = Types.VARCHAR) String warningMessage, 
			@In(jdbcType = Types.VARCHAR) String planIds, 
			@Out(jdbcType = Types.INTEGER) int[] id);
	
	@StoredProcedure(name = "dbo.as_edge_deploy_target_list_by_nodeId")
	void getDeployTargetByNodeId(
			@In(jdbcType = Types.INTEGER) int nodeId,
			@ResultSet List<DeployTargetDetail> targets);
	
	@StoredProcedure(name = "dbo.as_edge_deploy_target_list_by_serverName")
	void getDeployTargetsByServerName(
			@In(jdbcType = Types.VARCHAR) String serverName,
			@ResultSet List<DeployTargetDetail> targets);
	
	@StoredProcedure(name = "dbo.as_edge_deploy_target_update_status")
	void updateDeployTargetStatus(
			@In(jdbcType = Types.INTEGER) int nodeId,
			int protocol,int port,
			int status, int taskstatus,
			@In(jdbcType = Types.VARCHAR) String progressMessage,
			@In(jdbcType = Types.VARCHAR) String warningMessage);
	
	@StoredProcedure(name = "dbo.as_edge_deploy_target_list")
	void as_edge_deploy_target_list(@ResultSet List<DeployTargetDetail> targets);
	
	@StoredProcedure(name = "as_edge_deploy_target_query_by_uuid")
	void as_edge_deploy_target_query_by_uuid(@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid, @ResultSet List<DeployTargetDetail> targets );
	
	@StoredProcedure(name = "dbo.as_edge_deploy_target_set_selected")
	void as_edge_deploy_target_set_selected(int selected, int nodeId);
	
//	@StoredProcedure(name = "dbo.as_edge_deploy_target_clear_selected")
//	void as_edge_deploy_target_clear_selected();
//	
//	@StoredProcedure(name = "dbo.as_edge_deploy_target_list_by_serverName")
//	void as_edge_deploy_target_list_by_serverName(
//			@In(jdbcType = Types.VARCHAR) String serverName,
//			@ResultSet List<DeployTargetDetail> targets);
//	
//	@StoredProcedure(name = "dbo.as_edge_deploy_target_list_by_nodeId")
//	void as_edge_deploy_target_list_by_nodeId(
//			@In(jdbcType = Types.INTEGER) int nodeId,
//			@ResultSet List<DeployTargetDetail> targets);
//	
//	@StoredProcedure(name = "dbo.as_edge_deploy_target_list_scheduleType")
//	void as_edge_deploy_target_list_scheduleType(@ResultSet List<DeployTargetDetail> targets);
//	
//	@StoredProcedure(name = "dbo.as_edge_deploy_target_delete")
//	void as_edge_deploy_target_delete();
//	
//	@StoredProcedure(name = "dbo.as_edge_deploy_target_update_credential")
//	void as_edge_deploy_target_update_credential(@In(jdbcType = Types.VARCHAR) String serverName,
//			@In(jdbcType = Types.VARCHAR) String username,
//			@EncryptSave @In(jdbcType = Types.VARCHAR) String password);
//
//	@StoredProcedure(name = "dbo.as_edge_deploy_target_update_nodeID")
//	void as_edge_deploy_target_update_nodeID(String serverName, int hostID);
//	
//	@StoredProcedure(name = "dbo.as_edge_deploy_target_update_planIds")
//	void as_edge_deploy_target_update_planIds(int targetId ,
//			@In(jdbcType = Types.VARCHAR) String planIds);
//	
//	@StoredProcedure(name = "dbo.as_edge_deploy_target_update_partinfo")
//	void as_edge_deploy_target_update_partinfo(int targetId,
//			int protocol,int port,int selected,int status,int taskstatus, long msgCode,int percentage, 
//			@In(jdbcType = Types.VARCHAR) String progressMessage,
//			@In(jdbcType = Types.VARCHAR) String warningMessage, 
//			@In(jdbcType = Types.VARCHAR) String planIds);
}
