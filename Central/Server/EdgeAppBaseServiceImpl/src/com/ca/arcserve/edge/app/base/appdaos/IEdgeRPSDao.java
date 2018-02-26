package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.webservice.data.RPSInfo;
import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;

/**
 * 
 * @author lvwch01
 *
 */
public interface IEdgeRPSDao {
	
	int getHostIdForNodeImportedFromRPS(
			@In(jdbcType = Types.VARCHAR) String uuid,
			@In(jdbcType = Types.VARCHAR) String rpsuuid
			);
	
	int getHostIdByVMINSTUUIDAndRPSUUID(
			@In (jdbcType = Types.VARCHAR) String instanceuuid,
			@In (jdbcType = Types.VARCHAR) String rpsuuid
			);
	
	int getHostIdByVMINSTUUID(
			@In (jdbcType = Types.VARCHAR) String instanceuuid
			);
	
	String getHostNameByVMINSTUUID(
			@In (jdbcType = Types.VARCHAR) String instanceuuid
			);
	
	void as_edge_rps_update(
			@In(jdbcType = Types.VARCHAR) String hostname,
			@In(jdbcType = Types.INTEGER) int port,
			@In(jdbcType = Types.INTEGER) int protocol,
			@In(jdbcType = Types.VARCHAR) String username,
			@In(jdbcType = Types.VARCHAR) String password,
			@In(jdbcType = Types.VARCHAR) String rpsuuid,
			@Out(jdbcType = Types.INTEGER) int[] result
			);
	
	void as_edge_host_update_ImportFromRPS(
			int rhostid, 
			@In(jdbcType = Types.TIMESTAMP) Date lastupdated,
			@In(jdbcType = Types.VARCHAR) String rhostname,
			int isVisible, int appStatus, 
			int rhostType, 
			String hostuuid,
			boolean isVm,
			@Out(jdbcType = Types.INTEGER) int[] result);
	
	void as_edge_node_dest_update(
			int rhostid,
			@In(jdbcType = Types.INTEGER) int rpsId,
			@In(jdbcType = Types.INTEGER) int converterId,
			@In(jdbcType = Types.VARCHAR) String datastoreName,
			@In(jdbcType = Types.VARCHAR) String datastoreUUID,
			@In(jdbcType = Types.VARCHAR) String policyUUID,
			@In(jdbcType = Types.VARCHAR) String rhostname,
			@In(jdbcType = Types.VARCHAR) String vmName
			);

	void as_edge_rps_info_by_hostID(
			@In(jdbcType = Types.INTEGER) int rhostid, 
			@ResultSet List<RPSInfo> rpsInfo
			);
}
