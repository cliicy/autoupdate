package com.ca.arcserve.edge.app.msp.dao;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.msp.webservice.contract.Customer;

public interface IMspCustomerDao {
	
	@StoredProcedure(name = "as_edge_msp_customer_update")
	void as_edge_msp_customer_update(int id, String name,String description, @Out(jdbcType = Types.INTEGER) int[] newCustomerId);
	
	@StoredProcedure(name = "as_edge_msp_customer_delete")
	void as_edge_msp_customer_delete(int id);
	
	@StoredProcedure(name = "as_edge_msp_customer_get")
	void as_edge_msp_customer_get(int id, @ResultSet List<Customer> customers);
	
	@StoredProcedure(name = "as_edge_msp_customer_getByName")
	void as_edge_msp_customer_getByName(String name, @ResultSet List<Customer> customers);
	
	@StoredProcedure(name = "as_edge_msp_customer_assignPlan")
	void as_edge_msp_customer_assignPlan(int customerId, int planId);
	
	@StoredProcedure(name = "as_edge_msp_customer_unassignPlan")
	void as_edge_msp_customer_unassignPlan(int customerId, int planId);
	
	@StoredProcedure(name = "as_edge_msp_customer_isPlanAssigned")
	void as_edge_msp_customer_isPlanAssigned(int customerId, String planUuid, @Out(jdbcType = Types.BIT) boolean[] assigned);
	
	@StoredProcedure(name = "as_edge_msp_customer_getAssignedPlans")
	void as_edge_msp_customer_getAssignedPlans(int customerId, @ResultSet List<EdgePolicy> plans);
	
	@StoredProcedure(name = "as_edge_msp_customer_getAvailablePlans")
	void as_edge_msp_customer_getAvailablePlans(int customerId, @ResultSet List<EdgePolicy> plans);

}
