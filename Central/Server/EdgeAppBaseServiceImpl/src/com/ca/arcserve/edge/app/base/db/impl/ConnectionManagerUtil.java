package com.ca.arcserve.edge.app.base.db.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.db.IConfiguration;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.util.EdgeImpersonation;

/**
 * @author gonro07
 *
 *         <pre>
 * To initilize DB env:
 * <code>
 * String filepath = ....;
 * IConfiguration configuration = Configuration.getInstance(filepath);
 * ConnectionManagerUtil.initDBPool(configuration);
 * </code>
 * To close it:
 * <code>
 * ConnectionManagerUtil.destroyPool();
 * </code>
 * </pre>
 */
public class ConnectionManagerUtil {
	private static Logger log = Logger.getLogger(ConnectionManagerUtil.class);
	private static AtomicBoolean connectionThreadExitFlag = new AtomicBoolean(false);

	public static DataSource getDs() {
		return ds;
	}

	private final static ArrayBlockingQueue<EdgeDriverConnectionFactory> request = new ArrayBlockingQueue<EdgeDriverConnectionFactory>(
			1);
	private final static ArrayBlockingQueue<ConnectionResult> response = new ArrayBlockingQueue<ConnectionResult>(
			1);

	private static DataSource ds = null;
	// private static GenericObjectPool pool = null;
	private static IConfiguration config = null;

	private ConnectionManagerUtil() {

	}
	/**
	 * Before and After the method exits, the request and response list should empty.
	 * @param fact
	 * @return
	 */
	public static synchronized ConnectionResult getConnection(
			EdgeDriverConnectionFactory fact) {
		try {
			request.remove();
		} catch (Throwable t) {
		}
		try {
			response.remove();
		} catch (Throwable t) {
		}
		ConnectionResult result = new ConnectionResult();
		try {
			request.put(fact);
			result = response.take();
			return result;
		} catch (InterruptedException e) {
			result.setCon(null);
			result.setE(e);
			return result;
		} finally {
			try {
				request.remove();
			} catch (Throwable t) {
			}
			try {
				response.remove();
			} catch (Throwable t) {
			}
		}
	}

	private static Runnable createConnectionTask() {
		connectionThreadExitFlag.set(false);
		return new Runnable() {
			boolean impersonated = false;
			@Override
			public void run() {

					log.info("Connection Creation Thread for Integration Security is running");
					while (!connectionThreadExitFlag.get()) {
						try {
							EdgeDriverConnectionFactory facotry = request.take();
							if(facotry.isWindowsAuth() && !impersonated){
								String user = facotry.getWindowUser();
								String pass = facotry.getWindowPass();
								int x;
								if(user==null || user.trim().length()==0){
									x = EdgeImpersonation.getInstance().impersonate();
								} else {
									x = EdgeImpersonation.getInstance().impersonate(user, null, pass);
								}
								
								if(x !=0){
									log.error("impersonate Windows User:"+user + " failed");
								}else{
									impersonated = true;
								}
							}
							
							try {
								response.remove();
							} catch (Throwable t) {
							}
							
							ConnectionResult result = new ConnectionResult();
							try {
								Connection connect = null;
								try {
									connect = facotry.get_driver().connect(
											facotry.get_connectUri(),
											facotry.get_props());
									result.setCon(connect);
									result.setE(null);
								} catch (Throwable t) {
									result.setE(t);
									result.setCon(connect);
								}
							} finally {
								try {
									request.remove();
								} catch (Throwable t) {
								}
								log.info("Connection Creation Thread returns a Connection");
								response.put(result);
							}
						} catch (InterruptedException e) {
							break;
						}
					}
					log.info("Connection Creation Thread for Integration Security exits");

			}
		};
	}

	public static synchronized void initDBPool(final IConfiguration config) throws Exception {

		String dbURI = config.getDbURI();
		String dbUser = config.getDbUser();
		String dbPassword = config.getDbPassword();
		int minSize = config.getDbPoolMinSize();
		int maxSize = config.getDbPoolMaxSize();
		String validateSql = config.getValidateSql();
		final String firstSqls = config.getFirstStatements();
		BasicDataSource dataSource = new BasicDataSource() {
			protected ConnectionFactory createConnectionFactory()
					throws SQLException {

				// Create a JDBC driver instance
				Driver driver = null;

				driver = DriverManager.getDriver(url);

				// Can't test without a validationQuery
				if (validationQuery == null) {
					setTestOnBorrow(false);
					setTestOnReturn(false);
					setTestWhileIdle(false);
				}

				// Set up the driver connection factory we will use

				String user = username;
				if (user != null) {
					connectionProperties.put("user", user);
				} else {
					log("DBCP DataSource configured without a 'username'");
				}

				String pwd = password;
				if (pwd != null) {
					connectionProperties.put("password", pwd);
				} else {
					log("DBCP DataSource configured without a 'password'");
				}
				ConnectionFactory driverConnectionFactory = null;
				try{
				driverConnectionFactory = new EdgeDriverConnectionFactory(
						driver, url, connectionProperties);
				}catch(Throwable t){
					log.error(t.getMessage(), t);
				}
				return driverConnectionFactory;

			}
		};
		dataSource.setUrl(dbURI);
		dataSource.setUsername(dbUser);
		dataSource.setPassword(dbPassword);

		dataSource.setInitialSize(minSize);

		dataSource.setMinIdle((minSize));

		dataSource.setMaxIdle((maxSize));

		dataSource.setMaxWait(IConfiguration.maxWait);

		dataSource.setMaxActive(maxSize);

		dataSource.setLogAbandoned(false);
		dataSource.setRemoveAbandoned(true);
		dataSource
				.setRemoveAbandonedTimeout(IConfiguration.softMinEvictableIdleTimeMillis);
		dataSource
				.setMinEvictableIdleTimeMillis(IConfiguration.softMinEvictableIdleTimeMillis);
		dataSource.setValidationQuery(validateSql);
		dataSource.setValidationQueryTimeout(3);

		dataSource.setTestOnBorrow(true);

		ds = dataSource;
		if (firstSqls != null) {
			List<String> sqllist = new ArrayList<String>();
			String[] sqls = firstSqls.split(IConfiguration.SQL_SEPARATOR);
			for (int i = 0; i < sqls.length; i++) {
				if (sqls[i].trim().length() > 0) {
					sqllist.add(sqls[i]);
					;
				}
			}
			dataSource.setConnectionInitSqls(sqllist);
		}

		try {
			request.remove();
		} catch (Throwable t) {
		}
		try {
			response.remove();
		} catch (Throwable t) {
		}
		EdgeExecutors.getCachedPool().submit(createConnectionTask());
		ConnectionManagerUtil.config = config;
	}

	public static IConfiguration getConfig() {
		return config;
	}

	public static synchronized void destroyPool() {
		if (ds != null)
			try {
				((BasicDataSource) ds).close();
			} catch (SQLException e) {
				log.debug(e.getMessage(), e);
			}
			connectionThreadExitFlag.set(true);
			ds=null;
	}

}
