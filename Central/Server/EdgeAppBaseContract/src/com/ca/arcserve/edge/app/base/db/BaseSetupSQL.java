package com.ca.arcserve.edge.app.base.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.UUID;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.StringUtil;

public class BaseSetupSQL {
	public void runSQLFile(GDBCConnection con, String filename)
			throws Exception {
		runSQLFile(con, filename, "\n\\s*GO");
	}

	public void runSQLFile(GDBCConnection con, String filename,
			String separators) throws Exception {
		System.out.println("runSQLFile:" + filename);
		InputStream is = con.getClass().getResourceAsStream(filename);
		if(is == null) throw new Exception("Failed to read file:"+filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s;
		boolean failflag = false;
		StringBuffer sb = new StringBuffer();
		while ((s = br.readLine()) != null) {
			sb.append(s + " \n");
		}
		br.close();

		String[] inst = sb.toString().split(separators);
		for (int i = 0; i < inst.length; i++) {
			try {
				if(!inst[i].trim().isEmpty())
					con.Execute(inst[i]);
			} catch (Exception e) {
				System.out.println(inst[i]);
				System.out.println(e.getMessage());
				failflag = true;
			}
		}
		if(failflag) throw new Exception("There is exception to run SQL in " + filename);
	}

	/**
	 * BEGIN ALTER DATABASE asedge SET SINGLE_USER WITH ROLLBACK IMMEDIATE DROP
	 * DATABASE asedge END
	 *
	 * @param con
	 * @param dbName
	 * @throws Exception
	 */
	public void runSQLToCreateDB(GDBCConnection con, String dbName)
			throws Exception {
		// step 1: USE master
		con.Execute("USE master");
		// step 2:IF EXISTS (SELECT name FROM sys.databases WHERE name =
		// 'asedgeTest') DROP DATABASE asedgeTest
		con.Execute("IF EXISTS (SELECT name FROM sys.databases WHERE name = '"
				+ dbName + "')  BEGIN ALTER DATABASE " + dbName
				+ " SET SINGLE_USER  WITH ROLLBACK IMMEDIATE; DROP DATABASE "
				+ dbName + "; END");
		// step 3 create DB
		//con.Execute("CREATE DATABASE " + dbName);
		// Fix issue 20364647
		UUID uuid = UUID.randomUUID();
		con
				.Execute(" DECLARE @collation NVARCHAR(64) "
						+ " DECLARE @data_path nvarchar(260) "
						+ " DECLARE @full_file_path nvarchar(MAX) "
						+ " SET @data_path = (SELECT SUBSTRING(physical_name, 1, CHARINDEX(N'master.mdf', LOWER(physical_name)) - 1) FROM master.sys.master_files WHERE database_id = 1 AND file_id = 1) "
						+ " SET @full_file_path = @data_path + '" + dbName + "_" + uuid.toString() + "'"
						+ " SET @collation = REPLACE(REPLACE(CONVERT(VARCHAR(128), SERVERPROPERTY('Collation')), '_CS_', '_CI_'), '_BIN', '_CI_AI') "
						+ " EXEC ('CREATE DATABASE " + dbName + " ON (NAME = " + dbName + "_Data, FILENAME = ''' + @full_file_path + '.mdf'') "
						+ "      LOG ON (NAME = " + dbName + "_Log, FILENAME = ''' + @full_file_path + '.ldf'')"
						+ " collate '+@collation)");
		// step 4 use it
		con.Execute("USE [" + dbName + "]");

	}

	public boolean isDBExist(GDBCConnection con, String dbName) {
		try {
			String s = "SELECT name FROM sys.databases WHERE name = '" + dbName
					+ "';";
			if (con.Execute(s)) {
				return true;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return false;
	}

	public void deleteDB(GDBCConnection con, String dbName) throws Exception {
		// step 1: USE master
		con.Execute("USE master");
		// step 2:IF EXISTS (SELECT name FROM sys.databases WHERE name =
		// 'asedgeTest') DROP DATABASE asedgeTest
		con.Execute("IF EXISTS (SELECT name FROM sys.databases WHERE name = '"
				+ dbName + "')  BEGIN ALTER DATABASE " + dbName
				+ " SET SINGLE_USER  WITH ROLLBACK IMMEDIATE; DROP DATABASE "
				+ dbName + "; END");
	}

	public void SaveConfiguration(String url, String user, String password,
			String Dbname, String filePath) throws Exception {
		String tmpUrl = url;
		String serverName = "";
		String instName = "";
		String targetURL = "";
		StringTokenizer tokenizer = new StringTokenizer(tmpUrl, "\\");
		if (tokenizer.hasMoreTokens()) {
			serverName = tokenizer.nextToken();
			if (tokenizer.hasMoreTokens())
				instName = tokenizer.nextToken();
		}

		Configuration cfg = new Configuration();
		cfg.setDbUser(user);
		if (!StringUtil.isEmptyOrNull(user)) {
			if (password != null) {
				cfg.setDbPassword(password);
			}
		}

		targetURL = composeJDBCURL(serverName, instName, 0, user, Dbname);

		cfg.setDbURI(targetURL);

		cfg.saveConfiguration(filePath);
		System.out.println("save Configuration successfully.");

	}
	/**
	 * return the url for sqlserver. 
	 * @param serverName, localhost is used if it is null or empty
	 * @param instance, the instance name integrated into URL if it is not null or empty
	 * @param port, the port appended into url string if it is more than 0
	 * @param userName, if it is null or empty, the integratedSecurity auth mode will be used, or the username will be in URL
	 * @param Dbname, the DB name used in URL
	 * @return
	 */
	public String getJDBCURL(String serverName, String instance, int port,
			String userName, String Dbname){
		return composeJDBCURL(serverName,instance, port,userName,Dbname);
	}
	private String composeJDBCURL(String serverName, String instance, int port,
			String userName, String Dbname) {
		String url = "jdbc:sqlserver://";
		String serverAndInsance = "";
		if (!StringUtil.isEmptyOrNull(serverName)) {
			serverAndInsance = serverName.trim();
		} else {
			serverAndInsance = "localhost";
		}

		if (!StringUtil.isEmptyOrNull(instance)) {
			serverAndInsance += "\\" + instance.trim();
		}

		url += serverAndInsance;

		if (port > 0) {
			url += ":" + port;
		}
		
		if (Dbname!=null){
			url += ";databaseName=" + Dbname;
		}

		if (StringUtil.isEmptyOrNull(userName)) {
			url += ";integratedSecurity=true";
		}

		return url;
	}

	public  void printSQLException(SQLException e) {
		// Unwraps the entire exception chain to unveil the real cause of the
		// Exception.
		while (e != null) {
			System.out.println("\n----- SQLException -----");
			System.out.println("  SQL State:  " + e.getSQLState());
			System.out.println("  Error Code: " + e.getErrorCode());
			System.out.println("  Message:    " + e.getMessage());
			// for stack traces, refer to derby.log or uncomment this:
			// e.printStackTrace(System.err);
			e = e.getNextException();
		}
	}
/**
 *
 * @param remoteFlag
 * @return 0 success, non 0 failure
 */
	public int shutdownJavaDB(boolean remoteFlag) throws SQLException{
		if (remoteFlag)
			return 0;
		try {
			// the shutdown=true attribute shuts down Derby
			DriverManager.getConnection("jdbc:derby:;shutdown=true");

			// To shut down a specific database only, but keep the
			// engine running (for example for connecting to other
			// databases), specify a database in the connection URL:
			// DriverManager.getConnection("jdbc:derby:" + dbName +
			// ";shutdown=true");
			return 0;
		} catch (SQLException se) {
			if (((se.getErrorCode() == 50000) && ("XJ015".equals(se
					.getSQLState())))) {
				// we got the expected exception
				return 0;
				// Note that for single database shutdown, the expected
				// SQL state is "08006", and the error code is 45000.
			} else {
				// if the error code or SQLState is different, we have
				// an unexpected exception (shutdown failed)
				throw se;

			}
		}
	}

	/**
	 * To call this method, user is responsible to set derby.system.home system
	 * property
	 *
	 * @param username
	 * @param password
	 * @param dbname
	 * @param url
	 * @param remoteFlag
	 * @param closeFlag
	 * @return
	 */
	public int createJavaDB(String username, String password, String dbname,
			String url, boolean remoteFlag, boolean closeFlag) {

		GDBCConnection con = null;
		try {
			con = GDBCConnection.getGDBCConnection_JAVADB(url, username, password);
			// first create DB
			runSQLFile(con, "/resources/javadb/java_db_script.sql", ";");
			con.close();
			con = null;
			if (closeFlag)
				shutdownJavaDB(remoteFlag);
		} catch (Exception e) {
			//System.out.println("Fail to create DB:" + e.getMessage());
			return -1;
		} finally {
			if (con != null)
				con.close();
		}
		return 0;
	}

	/**
	 * To call this method with upgrade flag, user is responsible to set derby.system.home system
	 * property
	 *
	 * @param username
	 * @param password
	 * @param dbname
	 * @param url
	 * @param remoteFlag
	 * @param closeFlag
	 * @param upgradeFlag
	 * @return
	 */
	public int createJavaDBEx(String username, String password, String dbname,
			String url, boolean remoteFlag, boolean closeFlag, boolean upgradeFlag) {

		GDBCConnection con = null;
		try {
			con = GDBCConnection.getGDBCConnection_JAVADB(url, username, password);
			if (upgradeFlag) {
				try{
				runSQLFile(con, "/resources/javadb/java_db_drop_funcs.sql", ";");
				}
				catch(Exception e){
					//ignore the exception thrown by it since it does not harm to system.
				}
			}

			runSQLFile(con, "/resources/javadb/java_db_script.sql", ";");
			con.close();
			con = null;
			if (closeFlag)
				shutdownJavaDB(remoteFlag);
		} catch (Exception e) {
			//System.out.println("Fail to create DB:" + e.getMessage());
			return -1;
		} finally {
			if (con != null)
				con.close();
		}
		return 0;
	}

	/**
	 * Save JavaDB Conf
	 *
	 * @param filePath
	 * @param username
	 * @param passwd
	 * @param dbName
	 * @param dbSchema
	 * @return
	 */
	public int saveJavaDBConf(String filePath, String username, String passwd,
			String dbName, String dbSchema,String url) throws Exception {

		Configuration cfg = new Configuration();
		cfg.setFirstStatements("SET SCHEMA " + dbSchema + ";");
		cfg.setValidateSql(IConfiguration.JAVADB_validateSql);
		cfg.setDbUser(dbName);
		cfg.setDbPassword(passwd);
		cfg.setDbURI(url);

		try {
			cfg.saveConfiguration(filePath);
			//System.out.println("save Configuration successfully.");
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}
	/**
	 *
	 * @param url
	 * @param username
	 * @param passwd
	 * @param testSql
	 * @return
	 */
	public boolean testJavaDB(String url, String username, String passwd,
			String testSql) {
		Connection connection = null;
		Statement stmt = null;

		try {
			connection = java.sql.DriverManager.getConnection(url, username,
					passwd);
			stmt = connection.createStatement();
			stmt.execute(testSql);
			return true;
		} catch (SQLException e) {
			return false;
		} finally {
			if(stmt != null){
				try {
					stmt.close();
				} catch (Exception e) {

				}
			}
			if (connection != null)
				try {
					connection.close();
				} catch (Exception e) {

				}
		}

	}
	// public static IConfiguration getJavaDBConfiguration(EdgeApplicationType
	// type){
	//
	// String framework = "embedded";
	// String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	// String protocol = "jdbc:derby:";
	// boolean remote = Boolean.getBoolean("jdbcremote");
	// if(remote){
	// framework = "derbyclient";
	// driver = "org.apache.derby.jdbc.ClientDriver";
	// protocol = "jdbc:derby://localhost:15270/";
	// }
	//
	// }

	public void useDB(GDBCConnection con, String dbName) throws Exception {
		con.Execute("USE "+dbName);

	}
	
	public Boolean testSQLServer(String serverName, String instance, int port,
			String userName, @NotPrintAttribute String password, String dbName) throws EdgeServiceFault {
		if(!StringUtil.isEmptyOrNull(userName)) userName = userName.trim();

		String url = composeJDBCURL(serverName, instance, port, userName, dbName);
		try {
			Class.forName(Configuration.dbDriverName);
			if (-1 == url.indexOf(IConfiguration.WINDOWS_AUTHENTICATE) ) {
				DriverManager.getConnection(url,userName,password);
			} else {
				if (password == null) {
					password = "";
				}
				GDBCConnection.testToConnectWithImpersonate(url,userName,password);
			}
		} catch (SQLException e) {
			if ("S0001".equalsIgnoreCase(e.getSQLState())
					|| (userName != null && userName.trim().length() > 128)) {
				// String msg = String.format("Login failed for user '%s'",
				// userName);
				EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Configuration_FailConnectCMDb, e
								.getLocalizedMessage());
				EdgeServiceFault esf = new EdgeServiceFault(e
						.getLocalizedMessage(), bean, e.fillInStackTrace());
				throw esf;
				// }else if("08S01".equalsIgnoreCase(e.getSQLState())){
				// EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
				// EdgeServiceErrorCode.Configuration_EnableTCPIP4SpecifiedInst,
				// e
				// .getLocalizedMessage());
				// EdgeServiceFault esf = new EdgeServiceFault(e
				// .getLocalizedMessage(), bean, e.fillInStackTrace());
				// throw esf;
			} else {
				EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Configuration_CanotConnectToSpecifiedInst,
						e.getLocalizedMessage());
				EdgeServiceFault esf = new EdgeServiceFault(e
						.getLocalizedMessage(), bean, e.fillInStackTrace());
				throw esf;
			}
		} catch (Exception e) {
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_CanotConnectToSpecifiedInst,
					e.getLocalizedMessage());
			EdgeServiceFault esf = new EdgeServiceFault(
					e.getLocalizedMessage(), bean, e.fillInStackTrace());
			throw esf;
		}

		return true;
	}

}
