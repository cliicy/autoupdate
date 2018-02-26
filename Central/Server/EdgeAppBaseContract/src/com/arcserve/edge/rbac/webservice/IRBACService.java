package com.arcserve.edge.rbac.webservice;

import java.util.List;

import com.arcserve.edge.rbac.model.Permission;
import com.arcserve.edge.rbac.model.Role;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface IRBACService {
	List<Permission> getUserAllPermissions(String username) throws EdgeServiceFault;
	
	List<Role> getUserAllRoles(String username) throws EdgeServiceFault;
}
