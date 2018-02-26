/**
 * Created on Dec 13, 2012 3:40:22 PM
 */
package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SessionPassword;

/**
 * @author lijwe02
 *
 */
public interface IEdgeSessionPasswordDao {
	@StoredProcedure(name = "as_edge_session_password_cu")
	void as_edge_session_password_cu(int id, int hostId,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String password, 
			@In(jdbcType = Types.VARCHAR) String pwdComment,
			@Out(jdbcType = Types.INTEGER) int[] newId);
	
	@StoredProcedure(name = "as_edge_session_password_getByHostId")
	void as_edge_session_password_getByHostId(int hostId, 
			@ResultSet List<SessionPassword> passwordList);
	
	@StoredProcedure(name = "as_edge_session_password_getById")
	void as_edge_session_password_getById(int id, 
			@ResultSet List<SessionPassword> passwordList);
	
	@StoredProcedure(name = "as_edge_session_password_deleteByHostId")
	void as_edge_session_password_deleteByHostId(int hostId);
	
	@StoredProcedure(name = "as_edge_session_password_deleteById")
	void as_edge_session_password_deleteById(int id);
	
}
