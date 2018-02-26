/**
 * 
 */
package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.annotations.WildcardConversion;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAControlService;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHASourceNode;

/**
 * @author lijwe02
 * 
 */
public interface IEdgeRHADao {
	
	@StoredProcedure(name = "dbo.as_edge_rha_cu")
	void as_edge_rha_cu(int id, 
			@In(jdbcType = Types.VARCHAR) String hostname, 
			int protocol, int port, int visible,
			@In(jdbcType = Types.VARCHAR) String userName,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String password,
			@Out(jdbcType = Types.INTEGER) int[] newId);
	
	@StoredProcedure(name = "dbo.as_edge_rha_scenario_cu")
	void as_edge_rha_scenario_cu(int id, int rhaId, long scenarioId, 
			@In(jdbcType = Types.NVARCHAR) String scenarioName, 
			int scenarioType, 
			@Out(jdbcType = Types.INTEGER) int[] newId);
	
	@StoredProcedure(name = "dbo.as_edge_rha_scenario_host_map_cu")
	void as_edge_rha_scenario_host_map_cu(int rhaScenarioId, int hostId,
			@In(jdbcType = Types.NVARCHAR) String recoveryPointFolder,
			@In(jdbcType = Types.NVARCHAR) String vmInstanceUUID,
			@In(jdbcType = Types.NVARCHAR) String vmName, 
			@In(jdbcType = Types.NVARCHAR) String hypervisorName, 
			@In(jdbcType = Types.NVARCHAR) String masterHost, 
			@In(jdbcType = Types.NVARCHAR) String masterIp, 
			@In(jdbcType = Types.NVARCHAR) String replicaHost, 
			@In(jdbcType = Types.NVARCHAR) String replicaIp,
			int converterId);
	
	@StoredProcedure(name = "dbo.as_edge_source_node_list")
	void as_edge_source_node_list(@In(jdbcType = Types.VARCHAR) String hostName,
			@ResultSet List<RHASourceNode> nodeList);
	
	@StoredProcedure(name = "dbo.as_edge_rha_list")
	void as_edge_rha_list(@WildcardConversion @In(jdbcType = Types.VARCHAR) String serverNamePrefix,
			@ResultSet List<RHAControlService> controlServiceList);
	
	void as_edge_host_offsitevcmconverters_getByHostId(int rhostId,
			@ResultSet List<EdgeOffsiteVCMConverterInfo> converterList);
	
	@StoredProcedure(name = "dbo.as_edge_rha_getHostIdForNodeImportedFromRHA")
	void as_edge_rha_getHostIdForNodeImportedFromRHA(String rhaServerName, long scenarioId, 
			String sourceNodeName, String sourceVmInstanceUuid, @Out(jdbcType = Types.INTEGER) int[] hostId);
}
