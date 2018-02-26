package com.ca.arcserve.edge.app.base.db;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ca.arcserve.edge.app.base.jaxbadapter.CDataAdapter;





public interface IConfiguration {
	/**
	we will use it 1 to distinguish the  encrypt algorithm,
	if there is no this flag, it should be java's DES, else we are using D2D's way 
	*/
	public static final int D2D_ENCRYPT_VERSION = 1;
	
	/**
	 * D2D's configuration dir
	 */
	public static final String dbDriverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String DEFAULT_Instance = "ARCSERVE_APP";
	public static final String DEFAULT_DB = "arcserveUDP";
	public static final String DEFAULT_URL = "jdbc:sqlserver://localhost\\"
			+ DEFAULT_Instance + ";integratedSecurity=true;databaseName="
			+ DEFAULT_DB;

	public static final String Default_validateSql = "select 1";

	public static final String JAVADB_validateSql = "VALUES 1";

	public static final String VCM_DBUSER = "EDGEVCM_DB";
	public static final String VCM_DBNAME = "VCM_DB";
	public static final String VCM_DBPASSWORD = "EDGEVCM_DB";

	//public static final String DBNAME = "EDGEVSPHERE_DB";
	public static final String VSPHERE_DBNAME = "VSPHERE_DB";
	public static final String VSPHERE_DBUSER = "EDGEVSPHERE_DB";
	public static final String VSPHERE_DBPASSWORD = "EDGEVSPHERE__DB";

	public static final String JAVADB_SCHEMA = "dbo";
	public String getValidateSql();
	public void setValidateSql(String DBPASSWORD);
	/**
	 * {@link PoolableObjectFactory#validateObject validated}
	 */
	public final static boolean testWhileIdle = true;
	public final static int timeBetweenEvictionRunsMillis = 10 * 1000;
	public final static int softMinEvictableIdleTimeMillis = 5 * 1000;
	public static final String WINDOWS_AUTHENTICATE = "integratedSecurity=true";
	/**
	 * Sets the action to take when the {@link ObjectPool#borrowObject} method is invoked
	 * when the pool is exhausted (the maximum number of "active" objects has
	 * been reached).
	 *
	 * whenExhaustedAction the action code, which must be one of
	 * WHEN_EXHAUSTED_BLOCK WHEN_EXHAUSTED_FAIL,WHEN_EXHAUSTED_GROW
	 */
	public final static byte whenExhaustedAction = 0x1;

	public static long maxWait = 20 * 1000;
	@XmlElement(nillable = true)
	public abstract String getDbUser();

	public abstract void setDbUser(String dbUser);

	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	public abstract String getDbPassword();

	public abstract void setDbPassword(String dbPassword);

	@XmlElement
	public abstract String getDbURI();

	public abstract void setDbURI(String dbURI);

	@XmlElement
	public abstract int getDbPoolMinSize();

	public abstract void setDbPoolMinSize(int dbPoolMinSize);

	@XmlElement
	public abstract int getDbPoolMaxSize();

	public abstract void setDbPoolMaxSize(int dbPoolMaxSize);

	public abstract String getServerName();

	public abstract String getInstanceName();
	
	public abstract String getDatabaseName();

	public abstract int getPort();
    /**
     *
     * @param filepath E.G. c:\program file\CA\EDGE\configuration\edge_dbconfiguration.xml
     * @throws Exception
     */
	public  void saveConfiguration(String filepath) throws Exception;
	public static final String SQL_SEPARATOR = ";";
	/**
	 * the Sql statements that will be executed just before the connection is given to application.
	 * They should be separated by SQL_SEPARATOR separator if multiple statements are needed
	 * @return
	 */
	public String getFirstStatements();

	public void setFirstStatements(String firstStatements);
	public int getVersion();

	public void setVersion(int version);
}