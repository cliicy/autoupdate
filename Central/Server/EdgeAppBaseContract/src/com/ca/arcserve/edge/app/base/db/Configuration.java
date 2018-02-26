package com.ca.arcserve.edge.app.base.db;


import java.io.File;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ca.arcserve.edge.app.base.jaxbadapter.CDataAdapter;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.StringUtil;

@XmlRootElement(name = "EdgeDBConfiguration")
public class Configuration implements IConfiguration {
	public static final String DBCONFIGURATION_FILE = "db_configuration.xml";
	
	private String validateSql = IConfiguration.Default_validateSql;

	private String dbUser = null;
	private String dbPassword = null;
	
	private int version = 0; 
	
	/**
	 * jdbc:sqlserver://localhost;instanceName=MYMSSQLSERVER;integratedSecurity=
	 * false;databaseName=asedge
	 */
	private String dbURI = DEFAULT_URL;
	/**
	 * Sets the minimum number of objects allowed in the pool before the evictor
	 * thread (if active) spawns new objects. Note that no objects are created
	 * when <code>numActive + numIdle >= maxActive.</code> This setting has no
	 * effect if the idle object evictor is disabled (i.e. if
	 * <code>timeBetweenEvictionRunsMillis <= 0</code>). setMinIdle
	 */
	private int dbPoolMinSize = 3;
	/**
	 * Sets the cap on the number of objects that can be allocated by the pool
	 * (checked out to clients, or idle awaiting checkout) at a given time. Use
	 * a negative value for no limit.
	 *
	 * maxActive The cap on the total number of object instances managed by the
	 * pool. Negative values mean that there is no limit to the number of
	 * objects allocated by the pool. setMaxActive
	 */
	private int dbPoolMaxSize = 40;

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#getDbUser()
	 */
	@XmlElement(nillable = true)
	public String getDbUser() {
		return dbUser;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#setDbUser(java.lang.String)
	 */
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#getDbPassword()
	 */
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	public String getDbPassword() {
		return dbPassword;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#setDbPassword(java.lang.String)
	 */
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#getDbURI()
	 */
	@XmlElement
	public String getDbURI() {
		return dbURI;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#setDbURI(java.lang.String)
	 */
	public void setDbURI(String dbURI) {
		this.dbURI = dbURI;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#getDbPoolMinSize()
	 */
	@XmlElement
	public int getDbPoolMinSize() {
		return dbPoolMinSize;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#setDbPoolMinSize(int)
	 */
	public void setDbPoolMinSize(int dbPoolMinSize) {
		this.dbPoolMinSize = dbPoolMinSize;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#getDbPoolMaxSize()
	 */
	@XmlElement
	public int getDbPoolMaxSize() {
		return dbPoolMaxSize;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#setDbPoolMaxSize(int)
	 */
	public void setDbPoolMaxSize(int dbPoolMaxSize) {
		this.dbPoolMaxSize = dbPoolMaxSize;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Configuration))
			return false;
		boolean result = false;
		IConfiguration dest = (IConfiguration) obj;
		result = StringUtil.isEqual(this.getDbPassword(), dest.getDbPassword())
				&& StringUtil.isEqual(this.getDbURI(), dest.getDbURI())
				&& StringUtil.isEqual(this.getDbUser(), dest.getDbUser())
				&& (this.getDbPoolMaxSize() == dest.getDbPoolMaxSize())
				&& (this.getDbPoolMinSize() == dest.getDbPoolMinSize());

		return result;
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */

	//private static IConfiguration cfg = null;
/**
 *
 * @param filePath
 * @return if the file does not exist, the default value will be used to create a configuration
 * @throws Exception
 */
	public synchronized static IConfiguration getInstance(String filePath) throws Exception {
		IConfiguration cfg = null;
		if (cfg == null) {
			File f = new File(filePath);
			if (!f.exists()) {
				//cfg = new Configuration();
				return null;
			} else {

				String readFileAsString = CommonUtil.readFileAsString(filePath);
				if (!StringUtil.isEmptyOrNull(readFileAsString)) {
					cfg = JAXB.unmarshal(new StringReader(readFileAsString),
							Configuration.class);

					if (!StringUtil.isEmptyOrNull(cfg.getDbPassword())) {
						if(cfg.getVersion()>=IConfiguration.D2D_ENCRYPT_VERSION){
							cfg.setDbPassword(BaseWSJNI.AFDecryptString(cfg
									.getDbPassword()));
						}else //this is old dbconfiguration file, we use java DES to decrypt it. 
						cfg.setDbPassword(CommonUtil.decrypt(cfg
								.getDbPassword()));
					}
					
					cfg.setFirstStatements( "SET DATEFORMAT ymd;" +
						((cfg.getFirstStatements() == null) ? "" : cfg.getFirstStatements()) );
				}
			}
		}

		return cfg;
	}

	public synchronized  void saveConfiguration(String filepath)
			throws Exception {

		String dbPassword = getDbPassword();
		this.setVersion(D2D_ENCRYPT_VERSION);
		if (dbPassword != null) {
			setDbPassword(BaseWSJNI.AFEncryptString(dbPassword));
		}
		
		String marshal = CommonUtil.marshal(this);
		CommonUtil.saveStringToFile(marshal, filepath);

		this.setDbPassword(dbPassword);
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#getServerName()
	 */
	public String getServerName() {
		parseURI();
		return serverName;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#getDatabaseName()
	 */
	public String getDatabaseName() {
		parseURI();
		return databaseName;
	}
	
	private void parseURI() {
		if (dbURI != null) {
			String[] tokens = dbURI.split(";");

			if (tokens != null && tokens.length > 0) {
				String serverAndInstance = tokens[0].replaceFirst(
						"jdbc:sqlserver://", "");
				if (serverAndInstance.trim().length() > 0) {
					int portIndx = serverAndInstance.indexOf(':');
					if (portIndx > 0) {
						port = Integer.parseInt(serverAndInstance
								.substring(portIndx + 1).trim());
						serverAndInstance = serverAndInstance.substring(0,
								portIndx);
					}
					int indx = serverAndInstance.indexOf('\\');

					if (indx > 0) {
						serverName = serverAndInstance.substring(0, indx);
						instanceName = serverAndInstance.substring(indx + 1);
					} else {
						serverName = serverAndInstance;
					}
					if ("localhost".equalsIgnoreCase(serverName)) {
						try {
							serverName = InetAddress.getLocalHost()
									.getHostName();
						} catch (UnknownHostException e) {

						}
					}

					for (String token : tokens) {
						String[] ss = token.split("=");
						if (ss != null && ss.length == 2) {
							if (("instanceName").equalsIgnoreCase(ss[0])) {
								instanceName = ss[1];
								break;
							}
							else if(("databaseName").equalsIgnoreCase(ss[0])) {
								databaseName = ss[1];
							}
						}
					}

				} else {
					try {
						serverName = InetAddress.getLocalHost().getHostName();
					} catch (UnknownHostException e) {

					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#getInstanceName()
	 */
	public String getInstanceName() {
		parseURI();
		return instanceName;
	}

	/* (non-Javadoc)
	 * @see com.ca.arcserve.edge.app.base.db.IConfiguration#getPort()
	 */
	public int getPort() {
		parseURI();
		return port;
	}

	private String serverName = null;
	private String instanceName = null;
	private String databaseName = null;
	private int port = -1;
	private String firstStatements = null;
	@Override
	@XmlElement
	public String getValidateSql() {
		// TODO Auto-generated method stub
		return this.validateSql;
	}

	@Override
	public void setValidateSql(String validateSql) {
		this.validateSql = validateSql;

	}

	@Override
	@XmlElement
	public String getFirstStatements() {
		// TODO Auto-generated method stub
		return firstStatements;
	}

	@Override
	public void setFirstStatements(String firstStatements) {
		this.firstStatements = firstStatements;

	}

}
