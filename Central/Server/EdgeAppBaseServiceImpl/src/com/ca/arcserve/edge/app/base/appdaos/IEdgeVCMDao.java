package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.storageappliance.HBBUProxy;
import com.ca.arcserve.edge.app.base.webservice.storageappliance.HBBUProxyForStorageAppliance;

public interface IEdgeVCMDao {
	//delete or not?
	void as_edge_vsphere_proxy_getList(@In(jdbcType = Types.VARCHAR) String hostName, @ResultSet List<EdgeVSphereProxyInfo> proxyList);

	/**
	 * List all the proxies in this table. These host is assumed managed
	 * @param hosts
	 */
	@StoredProcedure(name = "dbo.as_edge_vsphere_proxy_list")
	void as_edge_vsphere_proxy_list(@ResultSet List<EdgeD2DHost> hosts);
	//Feb Sprint part2
	@StoredProcedure(name = "dbo.as_edge_vsphere_proxy_Info_list")
	void as_edge_vsphere_proxy_Info_list(int gatewayId, @ResultSet List<HBBUProxy> proxys);
	
	//Dec Sprint
	@StoredProcedure(name = "dbo.as_edge_vsphere_proxy_Info_list_for_StorageAppliance")
	void as_edge_vsphere_proxy_Info_list_for_StorageAppliance(@ResultSet List<HBBUProxyForStorageAppliance> proxys);

	@StoredProcedure(name = "dbo.as_edge_vsphere_proxy_getByHostId")
	void as_edge_vsphere_proxy_getByHostId(int rhostId,@ResultSet List<EdgeVSphereProxyInfo> proxyList);
	
	@StoredProcedure(name = "dbo.as_edge_proxy_connect_info_getAuthUuid")
	void as_edge_proxy_connect_info_getAuthUuid(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			@ResultSet List<AuthUuidWrapper> authUuids);
}
