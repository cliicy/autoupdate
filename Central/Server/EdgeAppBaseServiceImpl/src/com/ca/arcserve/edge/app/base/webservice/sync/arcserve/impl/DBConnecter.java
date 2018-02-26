package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException; 
/*import java.sql.Savepoint;
 *import java.sql.Statement;
 *import java.sql.CallableStatement;
 */

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.configuration.ConfigurationServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConfigInfo.AuthenticationType;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class DBConnecter implements IDBConnecter{
	private Connection con;
	private String dbName = null;

	public DBConnecter(String dbName) {
		this.dbName = dbName;
	}

	public void Connect()
			throws SyncDB_Exception {
		ConfigurationServiceImpl config = new ConfigurationServiceImpl(ConfigurationOperator._dbConfigfilePath);
		DBConfigInfo dbInfo = null;
		try {
			dbInfo = config.getDatabaseConfiguration();
		} catch (EdgeServiceFault e) {
			// TODO Auto-generated catch block
			ConfigurationOperator.errorMessage(e.getMessage(), e);
			throw new SyncDB_Exception(e);
		}
		dbInfo.getDbConnPoolConfig();
		String url = composeJDBCURL(dbInfo.getSqlServer(),
				dbInfo.getInstance(), dbInfo.getServerPort(), dbInfo
						.getAuthUserName(), dbName);

		if(dbInfo.getAuthentication() == AuthenticationType.WindowsAuthentication)
			ConfigurationOperator.ArcsyncImpersonateUser();
		try {
			if (StringUtil.isEmptyOrNull(dbInfo.getAuthUserName())) {
				con = DriverManager.getConnection(url);
			} else {
				con = DriverManager.getConnection(url,
						dbInfo.getAuthUserName(), dbInfo.getAuthPassword());
			}
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(), "[Driver: url]"
					+ e.getMessage());
		} finally {
			if(dbInfo.getAuthentication() == AuthenticationType.WindowsAuthentication)
				ConfigurationOperator.ArcsyncRevertToSelf();
		}
	}

	public String composeJDBCURL(String serverName, String instance, int port,
			String userName, String dbName) {
		String url = "jdbc:sqlserver://";
		String serverAndInsance = "";
		if (!StringUtil.isEmptyOrNull(serverName)) {
			serverAndInsance = serverName.trim();
		} else {
			serverAndInsance = "localhost";
		}

		if (!StringUtil.isEmptyOrNull(instance)) {
			serverAndInsance += "\\" + instance.trim();
		} /*else {
			serverAndInsance += "\\" + Configuration.DEFAULT_Instance;
		}*/

		url += serverAndInsance;

		if (port > 0) {
			url += ":" + port;
		}

		url += ";databaseName=" + dbName;

		if (StringUtil.isEmptyOrNull(userName)) {
			url += ";integratedSecurity=true";
		}
		return url;
	}

	/*public Statement CreateStmt() throws SyncDB_Exception {
		try {
			return con.createStatement();
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(),
					"Create statement failed " + e.getMessage());
		}
	}*/

	/*public CallableStatement CreateCallableStmt(String s) throws SyncDB_Exception {
		try {
			return con.prepareCall(s);
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(),
					"Create callable statement failed " + e.getMessage());
		}
	}*/

	public PreparedStatement CreatePreparedStmt(String s) throws SyncDB_Exception {
		try {
			return con.prepareStatement(s);
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(),
					"CreatePreparedStmt failed " + e.getMessage());
		}
	}

	protected void finalize() {
		try {
			Disconnect();
		} catch (SyncDB_Exception e1) {
		}
		try {
			super.finalize();
		} catch (Throwable e) {
		}
	}

	public void Disconnect() throws SyncDB_Exception {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				String msg = "Disconnect failed : " + e.getMessage();
				throw new SyncDB_Exception(e.getErrorCode(), msg);
			} 
		}
	}

	public void setAutoCommit(boolean b) throws SyncDB_Exception {
		// TODO Auto-generated method stub
		try {
			con.setAutoCommit(b);
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(), "setAutoCommit failed: "
					+ e.getMessage());
		}
	}

	/*public void setCommit() throws SyncDB_Exception {
		try {
			con.commit();
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(), "setCommit failed"
					+ e.getMessage());
		}
	}*/

	/*public void rollBack() throws SyncDB_Exception {
		try {
			con.rollback();
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(), "rollBack failed"
					+ e.getMessage());
		}
	}*/

	/*public Savepoint setSavePoint() throws SyncDB_Exception {
		try {
			return con.setSavepoint();
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(), "setSavePoint failed"
					+ e.getMessage());
		}
	}*/

	public void commit() throws SyncDB_Exception {
		try {
			con.commit();
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(), "commit failed: "
					+ e.getMessage());
		}
	}

	public DatabaseMetaData GetMedaData() throws SyncDB_Exception {
		try {
			return con.getMetaData();
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(),
					"DatabaseMetaData failed: " + e.getMessage());
		}
	}

}
