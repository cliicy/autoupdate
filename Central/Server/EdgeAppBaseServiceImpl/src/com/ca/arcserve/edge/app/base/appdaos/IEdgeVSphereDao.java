package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EdgeConnectInfoVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EdgeHostVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EdgePolicyVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EdgeVMPolicyMap;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EsxVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VmEsxMap;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VmHyperVMap;

public interface IEdgeVSphereDao {
	@StoredProcedure(name = "dbo.as_edge_vsphere_vmlist")
	void as_edge_vsphere_vmlist(@ResultSet List<EdgeHostVSphere> vmList);
	
	@StoredProcedure(name = "dbo.as_edge_vsphere_vmConnectInfolist")
	void as_edge_vsphere_vmConnectInfolist(@ResultSet List<EdgeConnectInfoVSphere> vmConnectInfoList);
	
	@StoredProcedure(name = "dbo.as_edge_vsphere_vmESXInfolist")
	void as_edge_vsphere_vmESXInfolist(int gatewayid, @In(jdbcType = Types.NVARCHAR) String serverTypes
			, @ResultSet List<EsxVSphere> vmESXInfoList);
	
	@StoredProcedure(name = "dbo.as_edge_vsphere_vmHyperVInfolist")
	void as_edge_vsphere_vmHyperVInfolist(int gatewayid,@ResultSet List<EsxVSphere> vmESXInfoList);
	
	@StoredProcedure(name = "dbo.as_edge_vsphere_vmHyperVMaplist")
	void as_edge_vsphere_vmHyperVMaplist(@ResultSet List<VmHyperVMap> vmESXMapList);
	
	@StoredProcedure(name = "dbo.as_edge_vsphere_vmPolicylist")
	void as_edge_vsphere_vmPolicylist(@ResultSet List<EdgePolicyVSphere> vmPolicyList);
	
	@StoredProcedure(name = "dbo.as_edge_vsphere_vmPolicyMaplist")
	void as_edge_vsphere_vmPolicyMaplist(@ResultSet List<EdgeVMPolicyMap> vmPolicyMapList);
	
	@StoredProcedure(name = "dbo.as_edge_vsphere_updateVmAppStatus")
	void as_edge_vsphere_updateVmAppStatus(
			@In(jdbcType = Types.NVARCHAR) String vmInstanceUuid,
			int appStatus);
	
	@StoredProcedure(name = "dbo.as_edge_vsphere_get_vms_from_proxy")
	void as_edge_vsphere_get_vms_from_proxy(@EncryptSave @In(jdbcType = Types.VARCHAR)String uuidEN, @ResultSet List<VmEsxMap> vmInstanceUUIDList);
}
