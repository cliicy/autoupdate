package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.cloudaccount.ASCloudAccount;

public interface ICloudAccountDao {
	@StoredProcedure(name = "dbo.as_edge_cloud_add_cloud_account")
	void as_edge_cloud_add_cloud_account(int id,@In(jdbcType = Types.VARCHAR) String accountName,int cloudType,int cloudSubType,
			@In(jdbcType = Types.VARCHAR) String details, @Out(jdbcType = Types.INTEGER) int[] newId);

	@StoredProcedure(name = "as_edge_cloud_delete")
	void as_edge_cloud_delete(int id);
	
	@StoredProcedure(name = "as_edge_cloud_get_by_id")
	void as_edge_cloud_get_by_id(int id, @ResultSet List<ASCloudAccount> cloudAccounts);
	
	@StoredProcedure(name = "as_edge_cloud_getCloudAccountsForDetails")
	void as_edge_cloud_getCloudAccountsForDetails(@In(jdbcType = Types.VARCHAR) String accountName , int id, @ResultSet List<ASCloudAccount> cloudAccounts);

	@StoredProcedure(name = "dbo.as_edge_cloud_getCloudAccounts_by_paging_with_gatewayId")
	void as_edge_cloud_getCloudAccounts_by_paging(int pagesize,int startpos, @In(jdbcType = Types.VARCHAR) String orderType,
			@In(jdbcType = Types.VARCHAR) String orderCol,int gatewayId, @Out(jdbcType = Types.INTEGER) int[] totalcount,@ResultSet List<ASCloudAccount> cloudAccounts);

	@StoredProcedure(name = "dbo.as_edge_cloud_getCloudAccounts_with_gatewayId")
	void as_edge_cloud_getCloudAccounts(int cloudType, int cloudSubType,
			int currentGatewayId, @ResultSet List<ASCloudAccount> cloudAccounts);
}
