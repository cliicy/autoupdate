package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageApplianceInfo;

public interface IStorageApplianceDao {
	@StoredProcedure(name = "dbo.as_edge_infrastructure_add_storage_appliance")
	void as_edge_infrastructure_add_storage_appliance(int id,@In(jdbcType = Types.VARCHAR) String hostname,@In(jdbcType = Types.VARCHAR) String dataIp,
			@In(jdbcType = Types.VARCHAR) String username,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String password,
			int protocol, int port, int mode,@Out(jdbcType = Types.INTEGER) int[] newId);

	@StoredProcedure(name = "dbo.as_edge_infrastructure_getInfrastructureList_by_paging")
	void as_edge_infrastructure_getInfrastructureList_by_paging(int pagesize,
			int startpos, @In(jdbcType = Types.VARCHAR) String orderType,
			int gatewayId, @In(jdbcType = Types.VARCHAR) String orderCol,
			@Out(jdbcType = Types.INTEGER) int[] totalcount,
			@ResultSet List<StorageApplianceInfo> infrastructureList);
	//Feb Sprint
	
	@StoredProcedure(name = "dbo.as_edge_infrastructure_getInfrastructureList")
	void as_edge_infrastructure_getInfrastructureList(@ResultSet List<StorageApplianceInfo> infrastructureList);
	
	@StoredProcedure(name = "as_edge_infrastructure_delete")
	void as_edge_infrastructure_delete(int id);
	
	@StoredProcedure(name = "as_edge_infrastructure_get_by_id")
	void as_edge_infrastructure_get_by_id(int id, @ResultSet List<StorageApplianceInfo> infrastructureList);
	
	// FOR AQA to use
	@StoredProcedure(name = "as_edge_infrastructure_get_by_Hostnames")
	void as_edge_infrastructure_get_by_Hostnames(@In(jdbcType = Types.VARCHAR) String ServerIp, @In(jdbcType = Types.VARCHAR) String DataIp, @ResultSet List<StorageApplianceInfo> infrastructureList);
	//210009	
	@StoredProcedure(name = "as_edge_infrastructure_get_duplicate")
	void as_edge_infrastructure_get_duplicate(@In(jdbcType = Types.VARCHAR) String hostname,@In(jdbcType = Types.VARCHAR) String dataIp,
			@In(jdbcType = Types.VARCHAR) String username, @EncryptSave @In(jdbcType = Types.VARCHAR) String password, int protocol, int port, @ResultSet List<StorageApplianceInfo> infrastructureList);
}
