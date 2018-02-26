package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;

import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;

public interface IEdgeHypervisorDao {
	
	@StoredProcedure(name = "dbo.as_edge_hypervisor_vm_update")
	void as_edge_hypervisor_vm_update(int hostId, String hypervisorHostname, int socketCount);
	
	@StoredProcedure(name = "dbo.as_edge_hypervisor_vm_getLicenseInfo")
	void as_edge_hypervisor_vm_getLicenseInfo(int hostId,
			@Out(jdbcType = Types.VARCHAR) String[] hypervisor,
    		@Out(jdbcType = Types.INTEGER) int[] socketCount);

	@StoredProcedure(name = "dbo.as_edge_hypervisor_vm_mark")
	void as_edge_hypervisor_vm_mark(String hypervisor, int hostId);
	
	@StoredProcedure(name = "dbo.as_edge_hypervisor_vm_unmark")
	void as_edge_hypervisor_vm_unmark(String hypervisor, int hostId);
	
	@StoredProcedure(name = "dbo.as_edge_hypervisor_vm_checkstate")
	void as_edge_hypervisor_vm_checkstate(String hypervisor,
			@Out(jdbcType = Types.INTEGER) int[] usednum);
	
}
