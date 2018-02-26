/**
 * Created on Dec 13, 2012 3:57:15 PM
 */
package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SessionPassword;

/**
 * @author lijwe02
 * 
 */
public class EdgeSessionPasswordDaoImpl implements IEdgeSessionPasswordDao {

	@Override
	public void as_edge_session_password_cu(int id, int hostId, String password, String pwdComment, int[] newId) {
		EdgeApplicationType appType = EdgeWebServiceContext.getApplicationType();
		if (appType != EdgeApplicationType.VirtualConversionManager) {
			throw new DaoException("Only VCM is supported.");
		}
		String sqlStr = null;
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		int t_id = id;
		try {
			String encryptPassword = DaoFactory.getEncrypt().encryptString(password);
			if (t_id <= 0) {
				// Insert
				sqlStr = "INSERT INTO as_edge_host_session_password(hostid, password, pwdcomment, createtime, lastupdatetime) values(?, ?, ?, ?, ?)";
				pa.add(new Integer(hostId));
				pa.add(EdgeDaoCommonExecuter.getSafeString(encryptPassword));
				pa.add(EdgeDaoCommonExecuter.getSafeString(pwdComment));
				pa.add(new Timestamp(System.currentTimeMillis()));
				pa.add(new Timestamp(System.currentTimeMillis()));
				commonExecuter.ExecuteDao(sqlStr, pa);

				sqlStr = "VALUES IDENTITY_VAL_LOCAL()";
				List<Integer> ids = new ArrayList<Integer>();
				commonExecuter.ExecuteDao(sqlStr, null, ids, 0);
				t_id = (!ids.isEmpty()) ? ids.get(0) : 0;
			} else {
				// Update
				sqlStr = "update as_edge_host_session_password set hostid=?, password=?, pwdcomment=?, lastupdatetime=? where id=?";
				pa.add(new Integer(hostId));
				pa.add(EdgeDaoCommonExecuter.getSafeString(encryptPassword));
				pa.add(EdgeDaoCommonExecuter.getSafeString(pwdComment));
				pa.add(new Timestamp(System.currentTimeMillis()));
				pa.add(new Integer(t_id));
				commonExecuter.ExecuteDao(sqlStr, pa);
			}
			newId[0] = t_id;
		} catch (DaoException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}

	}

	@Override
	public void as_edge_session_password_getByHostId(int hostId, List<SessionPassword> passwordList) {
		List<Object> pa = new ArrayList<Object>();

		String str = "select id, hostid, password, pwdcomment, createtime, lastupdatetime "
				+ "from as_edge_host_session_password where hostid = ? order by createtime";
		pa.add(new Integer(hostId));

		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDao(str, pa, SessionPassword.class, passwordList);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}

	@Override
	public void as_edge_session_password_getById(int id, List<SessionPassword> passwordList) {
		List<Object> pa = new ArrayList<Object>();

		String str = "select id, hostid, password, pwdcomment, createtime, lastupdatetime "
				+ "from as_edge_host_session_password where id = ? order by createtime";
		pa.add(new Integer(id));

		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDao(str, pa, SessionPassword.class, passwordList);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}

	@Override
	public void as_edge_session_password_deleteByHostId(int hostId) {
		List<Object> pa = new ArrayList<Object>();
		String sqlStr = "DELETE FROM as_edge_host_session_password WHERE hostid = ?";
		pa.add(new Integer(hostId));

		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDao(sqlStr, pa);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}

	@Override
	public void as_edge_session_password_deleteById(int id) {
		List<Object> pa = new ArrayList<Object>();
		String sqlStr = "DELETE FROM as_edge_host_session_password WHERE id = ?";
		pa.add(new Integer(id));

		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDao(sqlStr, pa);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}

}
