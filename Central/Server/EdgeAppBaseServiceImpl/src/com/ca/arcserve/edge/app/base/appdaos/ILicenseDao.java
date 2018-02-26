package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedVmInfo;


public interface ILicenseDao {
	@StoredProcedure(name = "dbo.as_edge_license_assign")
	void assign_license(String binded_machine, int client_type, String code, long required_feature, int used_num);

	@StoredProcedure(name = "dbo.as_edge_license_release")
	void release_license(String binded_machine, int client_type);
	
	@StoredProcedure(name = "dbo.as_edge_license_need")
	void need_license(String binded_machine, int client_type, String code, long required_feature, int used_num);
	
	@StoredProcedure(name = "dbo.as_edge_license_find")
	void find_license(String binded_machine, int client_type, @Out(jdbcType = Types.CHAR)String[] code, @Out(jdbcType = Types.INTEGER)int[] out_lic_id);
	
	@StoredProcedure(name = "dbo.as_edge_license_need_find")
	void find_need_license(String binded_machine, int client_type, @Out(jdbcType = Types.CHAR)String[] code);
	
	@StoredProcedure(name = "dbo.as_edge_license_used_num")
	void get_license_used_num(String code, @Out(jdbcType = Types.INTEGER)int[] result);

	@StoredProcedure(name = "dbo.as_edge_license_used_release")
	void release_license_used(String binded_machine, int client_type, String code);

	@StoredProcedure(name = "dbo.as_edge_license_needed_num")
	void get_license_needed_num(String code, @Out(jdbcType = Types.INTEGER)int[] result);

	@StoredProcedure(name = "dbo.as_edge_license_used_machine")
	void get_license_used_machine(String code, @ResultSet List<LicensedNodeInfo> result);
	
	@StoredProcedure(name = "dbo.as_edge_license_needed_machine")
	void get_license_needed_machine(String code, @ResultSet List<LicensedNodeInfo> result);

	@StoredProcedure(name = "dbo.as_edge_license_delete_by_machine")
	void delete_license_by_machine(String bindNodeName, int client_type);
	
	@StoredProcedure(name = "dbo.as_edge_hypervisor_vm_unmark_all")
	void as_edge_hypervisor_vm_unmark_all(String hypervisor);
	
	@StoredProcedure(name = "dbo.as_edge_license_nce_update")
	void as_edge_license_nce_update(String name);
	
	@StoredProcedure(name = "dbo.as_edge_license_nce_delete")
	void as_edge_license_nce_delete(String name);
	
	@StoredProcedure(name = "dbo.as_edge_license_nce_find")
	void as_edge_license_nce_find(String name, @Out(jdbcType = Types.INTEGER)int[] id);
	
	@StoredProcedure(name = "dbo.as_edge_license_nce_used_num")
	void as_edge_license_nce_used_num(@Out(jdbcType = Types.INTEGER)int[] result);

	@StoredProcedure(name = "dbo.as_edge_license_nce_used_machine")
	void as_edge_license_nce_used_machine(@ResultSet List<LicensedNodeInfo> result);

	@StoredProcedure(name = "dbo.as_edge_hypervisor_vm_save")
	void as_edge_hypervisor_vm_save(int lic_id, int node_id, String vm_name);

	@StoredProcedure(name = "dbo.as_edge_hypervisor_vm_find")
	void as_edge_hypervisor_vm_find(int lic_id, @ResultSet List<LicensedVmInfo> result);

}
