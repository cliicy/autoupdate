package com.ca.arcserve.edge.app.asbu.dao;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.EdgeASBUServer;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;

public interface IASBUDao
{
	@StoredProcedure(name = "dbo.as_edge_InsertASBUDomain")
	void insertASBUDomain(@In(jdbcType = Types.VARCHAR) String domainName, @ResultSet List<IntegerId> domainIdList);
	
	@StoredProcedure(name = "dbo.as_edge_InsertASBUServerExtInfo")
	void insertASBUServerExtInfo(int serverId, int serverClass, int domainId, int serverStatus);
	
	@StoredProcedure(name = "dbo.as_edge_asbu_deleteDomainById")
	void deleteDomainById(int domainId);
	
	@StoredProcedure(name = "dbo.as_edge_updateASBUServerExtInfo")
	void updateASBUServerExtInfo(int serverId, int serverClass, int domainId, int serverStatus);
	
	@StoredProcedure(name = "dbo.as_edge_asbu_updateDeletedServerStatus")
	void updateDeletedServerStatus(int domainId, int serverId, int status);
	
	@StoredProcedure(name = "dbo.as_edge_asbu_get_server")
	void getASBUServer(int serverId, @ResultSet List<EdgeHost> serverList);
	
	@StoredProcedure(name = "dbo.as_edge_FindASBUDomain")
	void findASBUDomain(@In(jdbcType = Types.VARCHAR) String domainName, int gatewayId, @ResultSet List<IntegerId> idList);
	
	@StoredProcedure(name = "dbo.as_edge_asbu_findServerByDomainId")
	void findServersByDomainId(int domainId, @ResultSet List<EdgeASBUServer> serverList);
	
	@StoredProcedure(name = "dbo.as_edge_asbu_findAllServers")
	void findAllServers(int gatewayId, @ResultSet List<EdgeASBUServer> serverList);
	
	@StoredProcedure(name = "dbo.as_edge_asbu_findConnectionInfoByUUID")
	void findConnectionInfoByUUID(@In(jdbcType = Types.VARCHAR) String uuid, @ResultSet List<EdgeASBUServer> servers);
	
	@StoredProcedure(name = "dbo.as_edge_asbu_findConnectionInfoByHostId")
	void findConnectionInfoByHostId(int hostId, @ResultSet List<ConnectionContext> context);
	
	@StoredProcedure(name = "dbo.as_edge_asbu_findDomainIdByHostnameAndGatewayId")
	void findDomainIdByHostnameAndGatewayId(int gatewayId, @In(jdbcType = Types.VARCHAR) String hostname, @ResultSet List<IntegerId> idList);
}
