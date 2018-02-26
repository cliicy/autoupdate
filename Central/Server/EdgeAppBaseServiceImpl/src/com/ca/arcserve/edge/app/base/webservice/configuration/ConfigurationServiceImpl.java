package com.ca.arcserve.edge.app.base.webservice.configuration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoCommonExecuter;
import com.ca.arcserve.edge.app.base.appdaos.EdgeDeployD2DSettings;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.appdaos.ITManagementModel;
import com.ca.arcserve.edge.app.base.cm.CentralManagerInfo;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.SchedulerRegisterUtils;
import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.db.BaseSetupSQL;
import com.ca.arcserve.edge.app.base.db.Configuration;
import com.ca.arcserve.edge.app.base.db.GDBCConnection;
import com.ca.arcserve.edge.app.base.db.IConfiguration;
import com.ca.arcserve.edge.app.base.db.impl.ConnectionManagerUtil;
import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.SchedulerHelp;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeConfigurationService;
import com.ca.arcserve.edge.app.base.webservice.alert.AlertManager;
import com.ca.arcserve.edge.app.base.webservice.client.IBaseService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Account;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.CmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConfigInfo.AuthenticationType;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConnectionPoolConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DeployD2DSettings;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.PreferenceConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.RebootType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting.EmailService;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.email.EdgeEmailService;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.setup.SetupSQL;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;


public class ConfigurationServiceImpl implements IEdgeConfigurationService {

	private String filePath = "";
	private String cmConfigurationFilePath = "";
	private Logger logger = Logger.getLogger(ConfigurationServiceImpl.class);

	IEdgeSettingDao settingDao = DaoFactory.getDao(IEdgeSettingDao.class);
	private NativeFacade nativeCode = new NativeFacadeImpl();
	private IBaseService serviceImpl;
	private IActivityLogService logService = new ActivityLogServiceImpl();	

	public ConfigurationServiceImpl(){
	}

	public ConfigurationServiceImpl(String filePath) {
		super();
		this.filePath = filePath;
	}

	public ConfigurationServiceImpl(String filePath, String cmConfigurationFilePath) {
		super();
		this.filePath = filePath;
		this.cmConfigurationFilePath = cmConfigurationFilePath;
	}
	
	public ConfigurationServiceImpl(String filePath, String cmConfigurationFilePath, IBaseService serviceImpl) {
		super();
		this.filePath = filePath;
		this.cmConfigurationFilePath = cmConfigurationFilePath;
		this.serviceImpl = serviceImpl;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getCmConfigurationFilePath() {
		return cmConfigurationFilePath;
	}

	@Override
	public DBConfigInfo getDatabaseConfiguration() throws EdgeServiceFault {
		DBConfigInfo dbCfg = new DBConfigInfo();
		try {
			IConfiguration cfg = Configuration.getInstance(filePath);

			if (StringUtil.isEmptyOrNull(cfg.getDbUser())) {
				dbCfg
						.setAuthentication(AuthenticationType.WindowsAuthentication);
			} else {
				dbCfg.setAuthentication(AuthenticationType.Mixed);
			}

			dbCfg.setInstance(cfg.getInstanceName());
			dbCfg.setSqlServer(cfg.getServerName());
			dbCfg.setAuthPassword(cfg.getDbPassword());
			dbCfg.setAuthUserName(cfg.getDbUser());
			DBConnectionPoolConfig dbConnPoolConfig = new DBConnectionPoolConfig();
			dbConnPoolConfig.setMaxConnections(cfg.getDbPoolMaxSize());
			dbConnPoolConfig.setMinConnections(cfg.getDbPoolMinSize());
			dbCfg.setDbConnPoolConfig(dbConnPoolConfig);
			dbCfg.setServerPort(cfg.getPort());
			
			/*MspDbConfiguration mspDbConfiguration = new MspDbConfiguration();
			mspDbConfiguration.setMspInstallation(cfg.getMspInstallationType()==0 ? ProductType.STAND_ALONE:ProductType.MSP);
			dbCfg.setDbMspConfig(mspDbConfiguration);*/

		} catch (Exception e) {
			logger.debug("getDatabaseConfiguration failed:", e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedReadCfg, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
					.fillInStackTrace());
			throw esf;
		}
		return dbCfg;
	}
	
	@Override
	public PreferenceConfigInfo getPreferenceConfiguration() throws EdgeServiceFault {
		PreferenceConfigInfo pfCfg = new PreferenceConfigInfo();
		try {
			String configurationFolder = CommonUtil.getAppRootKey(EdgeWebServiceContext.getApplicationType());
			String newsFeed = CommonUtil.getApplicationExtentionKey(configurationFolder,WindowsRegistry.VALUE_NAME_APP_NEWSFEED);
			String socialNetworking = CommonUtil.getApplicationExtentionKey(configurationFolder,WindowsRegistry.VALUE_NAME_APP_SOCIALNETWORKING);
			String videoTag = CommonUtil.getApplicationExtentionKey(configurationFolder,WindowsRegistry.VALUE_NAME_APP_VIDEO);
			String pageSize = CommonUtil.getApplicationExtentionKey(configurationFolder,WindowsRegistry.VALUE_PAGE_SIZE);
			if(newsFeed==null){
				newsFeed="0";
			}
			if(socialNetworking==null){
				socialNetworking="0";
			}
			if(videoTag==null){
				videoTag="1";
			}
			if (pageSize == null) {
				WindowsRegistry registry = new WindowsRegistry();
				String rootKey = configurationFolder;
				if (rootKey != null){
					try {
						int handle = registry.openKey(rootKey);
						registry.setValue(handle, WindowsRegistry.VALUE_PAGE_SIZE, String.valueOf(250));
						pageSize = String.valueOf(250);
					} catch (Exception e) {
						logger.debug(e.getMessage(), e);
					}
				}
			}
			pfCfg.setNewsFeedConfigInfo(newsFeed);
            pfCfg.setSocialNetworkingConfigInfo(socialNetworking);
            pfCfg.setVideoTag(videoTag);
            pfCfg.setPageSize(Integer.valueOf(pageSize));
		} catch (Exception e) {
			logger.debug("getPreferenceConfiguration failed:", e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedReadCfg, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
					.fillInStackTrace());
			throw esf;
		}
		return pfCfg;
	}
	
	@Override
	public void setPreferenceConfiguration(PreferenceConfigInfo pfConfig) throws EdgeServiceFault {
		try {
			String newsFeed = pfConfig.getNewsFeedConfigInfo();
			String socialNetworking = pfConfig.getSocialNetworkingConfigInfo();
			String videoTag = pfConfig.getVideoTag();
			if (!CommonUtil.setApplicationExtentionKey(WindowsRegistry.VALUE_NAME_APP_NEWSFEED, newsFeed))
				throw new Exception();
			if (!CommonUtil.setApplicationExtentionKey(WindowsRegistry.VALUE_NAME_APP_SOCIALNETWORKING, socialNetworking))
				throw new Exception();
			if (!CommonUtil.setApplicationExtentionKey(WindowsRegistry.VALUE_NAME_APP_VIDEO, videoTag))
				throw new Exception();
			logger.debug("save Configuration successfully.");
		} catch (Exception e) {
			logger.debug("save Configuration failed:", e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedSaveCfg, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
					.fillInStackTrace());
			throw esf;
		}
		
	}
	
	public void saveDbConfiguration(DBConfigInfo dbConfig) throws EdgeServiceFault{
		Configuration cfg = new Configuration();
		cfg.setDbUser(dbConfig.getAuthUserName());
		if (!StringUtil.isEmptyOrNull(dbConfig.getAuthUserName())) {
			if (dbConfig.getAuthPassword() != null) {
				cfg.setDbPassword(dbConfig.getAuthPassword());
			}
		}
		String jdbcURL = composeJDBCURL(dbConfig, true);
		cfg.setDbURI(jdbcURL);
		if (dbConfig.getDbConnPoolConfig() != null) {
			cfg.setDbPoolMaxSize(dbConfig.getDbConnPoolConfig()
					.getMaxConnections());
			cfg.setDbPoolMinSize(dbConfig.getDbConnPoolConfig()
					.getMinConnections());
		}
		
		/*cfg.setMspInstallationType(dbConfig.getDbMspConfig().getMspInstallation().ordinal());*/

		try {
			cfg.saveConfiguration(filePath);
			logger.debug("save Configuration successfully.");
		} catch (Exception e) {
			logger.debug("save Configuration failed:", e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedSaveCfg, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
					.fillInStackTrace());
			throw esf;
		}
	}

	@Override
	public int setDatabaseConfigurationWithSchemaCreation(
			DBConfigInfo dbConfig, boolean needCreateDbSchema)
			throws EdgeServiceFault {
		if (dbConfig == null)
			return -1;

		logger.debug(StringUtil.convertObject2String(dbConfig,
				new IdentityHashMap<Object, Boolean>()));

		// Step1 check connection
		boolean isValid = testSQLServer(dbConfig.getSqlServer(), dbConfig
				.getInstance(), dbConfig.getServerPort(), dbConfig
				.getAuthUserName(), dbConfig.getAuthPassword());

		logger.debug("testSQLServer:" + isValid);

		String oldServer = null;
		String oldInst = null;

		try {
			IConfiguration oldCfg = Configuration.getInstance(filePath);
			oldServer = oldCfg.getServerName();
			oldInst = oldCfg.getInstanceName();
		} catch (Exception e1) {
			logger.debug("Old Configuration:", e1);
		}

		logger.debug("oldServer:" + oldServer + ",oldInst:" + oldInst);

		// Step2, save the config
		saveDbConfiguration(dbConfig);

		if (needCreateDbSchema) {
			// Step3, create db schema
			String jdbcURL = composeJDBCURL(dbConfig, true);
			String serverInstPort = jdbcURL.replaceFirst("jdbc:sqlserver://", "");
			int indx = serverInstPort.indexOf(";");
			if (indx > 0) {
				serverInstPort = serverInstPort.substring(0, indx);
			}
			// boolean isCreateScheme = true;
			boolean isDBExist = false;

			GDBCConnection con = null;
			BaseSetupSQL setupSql = new BaseSetupSQL();
			try{
				if( StringUtil.isEmptyOrNull(dbConfig.getAuthUserName()) )
					con = GDBCConnection.getGDBCConnection(serverInstPort, "", "");
				else
					con = GDBCConnection.getGDBCConnection(serverInstPort, dbConfig.getAuthUserName(), dbConfig.getAuthPassword() );
				isDBExist = setupSql.isDBExist(con,IConfiguration.DEFAULT_DB);

			} catch (Exception e) {
				isDBExist = false;
			}finally{
				if(con!=null) con.close();
				con=null;
			}
			logger.debug("isDBExist " + isDBExist);

			// if (oldServer != null
			// && oldServer.equalsIgnoreCase(dbConfig.getSqlServer())) {
			//
			// if (!StringUtil.isEmptyOrNull(oldInst)
			// && oldInst.equalsIgnoreCase(dbConfig.getInstance())
			// || (StringUtil.isEmptyOrNull(oldInst) && StringUtil
			// .isEmptyOrNull(dbConfig.getInstance()))) {
			// isCreateScheme = false;
			// }
			// }
			// logger.debug("isCreateScheme " + isCreateScheme);

			if (!isDBExist) {
				try {
					// if DB not exist , we will create schema and insert initialization data
					SetupSQL.createDB_withept(serverInstPort, dbConfig
							.getAuthUserName(), dbConfig.getAuthPassword(),false,IConfiguration.DEFAULT_DB,true,true);
					logger.debug("createDB successfully");
				} catch (Exception e) {
					logger.debug("createDB failed:", e);
					String msg = e.getLocalizedMessage();
					EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
							EdgeServiceErrorCode.Configuration_FailedcreateDB, msg);
					EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
							.fillInStackTrace());
					throw esf;
				}
			}
		}

		// Step4, config the connection pool
		try {
			// first we shutdown the job scheduler
			try {
				logger.info("setDatabaseConfigurationWithSchemaCreation shutdownScheduler begin");
				SchedulerUtilsImpl.shutdownScheduler();
				logger.info("setDatabaseConfigurationWithSchemaCreation shutdownScheduler end");
			} catch (EdgeSchedulerException e) {
				logger.debug("Error occurred when shutdown job sheculer!");
			}
			// then we shutdown the DB connection pool
			DaoFactory.setDBConfigurationLock();
			ConnectionManagerUtil.destroyPool();

			// then we start new DB connection pool
			IConfiguration d = Configuration.getInstance(filePath);
			ConnectionManagerUtil.initDBPool(d);
			DaoFactory.initDao(ConnectionManagerUtil.getDs(),new NativeFacadeImpl());
			DaoFactory.releaseDBConfigurationLock();

			// we start the scheduler
			logger.info("setDatabaseConfigurationWithSchemaCreation initScheduler begin");
			SchedulerUtilsImpl.initScheduler(SchedulerUtilsImpl.props);
			logger.info("setDatabaseConfigurationWithSchemaCreation initScheduler end");
			//We configure schedulers here for Central Manager. For Report, they should be configure in Report APP
			if(EdgeWebServiceContext.getApplicationType()==EdgeApplicationType.CentralManagement)
				SchedulerRegisterUtils.registerJobs();
			logger.debug("config pool successfully");
		} catch (Exception e) {
			// logger.debug("config pool failed:", e);
			logger.error("config pool failed:", e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedCfgDataSource, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
					.fillInStackTrace());
			throw esf;
		}

		return 0;
	}

	@Override
	public Boolean testSQLServer(String serverName, String instance, int port,
			String userName, @NotPrintAttribute String password) throws EdgeServiceFault {
		BaseSetupSQL baseSetupSQL = new BaseSetupSQL();
		return baseSetupSQL.testSQLServer(serverName, instance, port, userName, password, null);
	}

	private String composeJDBCURL(DBConfigInfo dbConfig, boolean isAddDefaultDB) {
		String serverName = dbConfig.getSqlServer();
		String instance = dbConfig.getInstance();
		int port = dbConfig.getServerPort();
		String userName = dbConfig.getAuthUserName();
		return composeJDBCURL(serverName, instance, port, userName,
				isAddDefaultDB);
	}

	private String composeJDBCURL(String serverName, String instance, int port,
			String userName, boolean isAddDefaultDB) {
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
//		else {
//			serverAndInsance += "\\" + Configuration.DEFAULT_Instance;
//		}

		url += serverAndInsance;

		if (port > 0) {
			url += ":" + port;
		}

		if (isAddDefaultDB) {
			url += ";databaseName=" + Configuration.DEFAULT_DB;
		}

		if (StringUtil.isEmptyOrNull(userName)) {
			url += ";integratedSecurity=true";
		}

		logger.debug("url:" + url);
		return url;
	}

	@Override
	public CmInfo getCmConfiguration() throws EdgeServiceFault {
		CentralManagerInfo centralManagerInfo = null;
		CmInfo cmInfo = null;

		try {
			centralManagerInfo = CentralManagerInfo.getInstance(cmConfigurationFilePath);
			cmInfo = new CmInfo();
			
			boolean bVShpere = false;		
			if(EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.vShpereManager)
				bVShpere = true;
			
			NativeFacade nativeFacade = new NativeFacadeImpl();				
			EdgeAccount acc = nativeFacade.getEdgeAccount();
			String edgeUser = acc.getUserName();
			String edgePassword = acc.getPassword();
			String edgeDomain = acc.getDomain();
			String edgeProto = EdgeCommonUtil.getEdgeWebServiceProtocol();
			int edgePort = EdgeCommonUtil.getEdgeWebServicePort();
			
			String host = centralManagerInfo.getHost();
			String userName = centralManagerInfo.getUserName();
			String password = centralManagerInfo.getPassword();
			int port = centralManagerInfo.getPort();
			String proto = centralManagerInfo.getProtocol();
			
			boolean bCPMInstalled = CommonUtil.isAppInstalled(EdgeApplicationType.CentralManagement);
			if(StringUtil.isEmptyOrNull(host) && bVShpere && bCPMInstalled)
			{
				host = EdgeEmailService.GetInstance().getHostName().toLowerCase();
				userName = edgeUser;
				password = edgePassword;
				if(!StringUtil.isEmptyOrNull(edgeDomain) && !edgeDomain.equalsIgnoreCase("."))
					userName = edgeDomain+"\\"+userName;
				port = edgePort;
				proto = edgeProto;
			}
			
			cmInfo.setHost(host);
			cmInfo.setPassword(password);
			cmInfo.setPort(port);
			cmInfo.setProtocol(proto);
			cmInfo.setUserName(userName);
		} catch (Exception e) {
			logger.debug("getCmConfiguration failed:", e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedReadCfg, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
					.fillInStackTrace());
			throw esf;
		}

		return cmInfo;
	}	
	public String convertDate2String(Date date) {
		StringBuilder sb = new StringBuilder();
		sb.append(date.getHours())
		  .append(":")
		  .append(date.getMinutes());
		return sb.toString();
	}
	
	private ScheduleData getItManagementSchedule(ITManagementModel scheduleSetting) {
		ScheduleData scheduleData = new ScheduleData();

		scheduleData.setScheduleID(scheduleSetting.getID());
		scheduleData.setScheduleName(scheduleSetting.getName());
		scheduleData.setScheduleDescription(scheduleSetting.getDescription());
		scheduleData.getRepeatMethodData().setRepeatMethodType(
				ScheduleData.RepeatMethodType.parseInt(scheduleSetting.getScheduleType()));
		SchedulerHelp.setRepeartData(scheduleSetting.getScheduleParam(), scheduleData);
		scheduleData.setScheduleTime(scheduleSetting.getActionTime());
		scheduleData.setStartFromDate(scheduleSetting.getRepeatFrom());
		scheduleData.setRepeatUntilType(ScheduleData.RepeatUnitlType.parseInt(scheduleSetting.getRepeatType()));
		SchedulerHelp.setRepeatUntilParameterRelatedValues(scheduleSetting.getRepeatParam(), scheduleData);
		scheduleData.setScheduleTimeStr(convertDate2String(scheduleData.getScheduleTime()));
		return scheduleData;
	}


	@Override
	public int setCmConfiguration(CmInfo cmInfo) throws EdgeServiceFault {
		CentralManagerInfo centralManagerInfo = new CentralManagerInfo();
		centralManagerInfo.setHost(cmInfo.getHost());
		centralManagerInfo.setPassword(cmInfo.getPassword());
		centralManagerInfo.setPort(cmInfo.getPort());
		centralManagerInfo.setProtocol(cmInfo.getProtocol());
		centralManagerInfo.setUserName(cmInfo.getUserName());

		try {
			centralManagerInfo.saveInfo(cmConfigurationFilePath);
		} catch (Exception e) {
			logger.debug("setCmConfiguration failed:", e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedReadCfg, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
					.fillInStackTrace());
			throw esf;
		}

		return 0;
	}

	@Override
	public void setDatabaseConfiguration(DBConfigInfo dbConfig)
			throws EdgeServiceFault {
		setDatabaseConfigurationWithSchemaCreation(dbConfig, true);
	}

	@Override
	public EmailServerSetting getEmailServerTemplateSetting(int featureId) throws EdgeServiceFault {
		
		EmailServerSetting serverSetting = getEmailServerSetting();
		if(serverSetting == null){
			return new EmailServerSetting();
		}
		EmailTemplateSetting templateSetting = getEmailTemplateSetting(featureId);
		if(templateSetting == null){
			return new EmailServerSetting();
		}
		serverSetting.setTemplateSetting(templateSetting);
		
		return serverSetting;
		
	}
	
	
	@Override
	public EmailServerSetting getEmailServerSetting() throws EdgeServiceFault {
		
		List<EmailServerSetting> settingList = new ArrayList<EmailServerSetting>();
		settingDao.as_edge_email_server_setting_get(null, 0, settingList);
		
		if ( settingList.size() > 0 ) {
			
			EmailServerSetting es = settingList.get(0);
			String tmpStr = es.getUser_name();
			if (!tmpStr.isEmpty())
				es.setUser_name(nativeCode.AFDecryptString(tmpStr));

			tmpStr = es.getUser_password();
			if (!tmpStr.isEmpty())
				es.setUser_password(nativeCode.AFDecryptString(tmpStr));

			tmpStr = es.getProxy_user_name();
			if (!tmpStr.isEmpty())
				es.setProxy_user_name(nativeCode.AFDecryptString(tmpStr));

			tmpStr = es.getProxy_user_password();
			if (!tmpStr.isEmpty())
				es.setProxy_user_password(nativeCode.AFDecryptString(tmpStr));
			
			
			return es;
			
		}else{
			
			return null;
			
		}
		
	}
	
	@Override
	public void deleteEmailServerTemplateSetting(int featureId)throws EdgeServiceFault{
		
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			
			ede.BeginTrans();
			
			this.deleteEmailServerSetting(ede);
			this.deleteEmailTemplateSetting(featureId, ede);
			
			ede.CommitTrans();
			
		} catch (Exception e) {
			
			ede.RollbackTrans();
			
			logger.error(e.getMessage(),e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedReadCfg, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
					.fillInStackTrace());
			throw esf;
		}finally{
			ede.CloseDao();
		}
		
		
	}

	@Override
	public void saveEmailServerTemplateSetting(EmailServerSetting serverSetting)throws EdgeServiceFault{
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			
			ede.BeginTrans();
			
			this.saveEmailServerSetting(
					serverSetting.getMail_server(),
					serverSetting.getSmtp(),
					serverSetting.getPort(),
					serverSetting.getAuth_flag(),
					nativeCode.AFEncryptString(serverSetting.getUser_name()),
					nativeCode.AFEncryptString(serverSetting.getUser_password()),
					serverSetting.getSsl_flag(),
					serverSetting.getTls_flag(),
					serverSetting.getProxy_flag(),
					serverSetting.getProxy_server(),
					serverSetting.getProxy_port(),
					serverSetting.getProxy_auth_flag(),
					nativeCode.AFEncryptString(serverSetting.getProxy_user_name()),
					nativeCode.AFEncryptString(serverSetting.getProxy_user_password()),
					serverSetting.getAuto_discovery_flag(),
					serverSetting.isEnableEmailAlerts(),
					ede);
			
			EmailTemplateSetting templateSetting = serverSetting.getTemplateSetting();
			
			this.saveEmailTemplateSetting(
					templateSetting.getFrom_addrs(),
					templateSetting.getRecipients(),
					templateSetting.getSubject(),
					templateSetting.getHtml_flag(),
					templateSetting.getFeature_Id(),
					ede);
			
			
			ede.CommitTrans();
			
			
		} catch (Exception e) {
			
			ede.RollbackTrans();
			
			logger.error(e.getMessage(),e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedReadCfg, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
					.fillInStackTrace());
			throw esf;
		}finally{
			ede.CloseDao();
		}
		
		
	}
	
	
	@Override
	public void saveEmailServerSetting(EmailServerSetting serverSetting)
			throws EdgeServiceFault{
		
		settingDao.as_edge_email_server_setting_set(
					serverSetting.getMail_server(),
					serverSetting.getSmtp(),
					serverSetting.getPort(),
					serverSetting.getAuth_flag(),
					nativeCode.AFEncryptString(serverSetting.getUser_name()),
					nativeCode.AFEncryptString(serverSetting.getUser_password()),
					serverSetting.getSsl_flag(),
					serverSetting.getTls_flag(),
					serverSetting.getProxy_flag(),
					serverSetting.getProxy_server(),
					serverSetting.getProxy_port(),
					serverSetting.getProxy_auth_flag(),
					nativeCode.AFEncryptString(serverSetting.getProxy_user_name()),
					nativeCode.AFEncryptString(serverSetting.getProxy_user_password()));
			
	}
	
	@Override
	public boolean testEmailServerTemplateSetting(EmailServerSetting serverSetting)
			throws EdgeServiceFault {
		logger.info("[ConfigurationServiceImpl] testEmailServerTemplateSetting() begin to test sending email. server is: "+
			serverSetting.getMail_server()+" port is: "+serverSetting.getPort()+" use SSL: "+serverSetting.getSsl_flag()
			+" use tls: "+serverSetting.getTls_flag()+" use proxy: "+serverSetting.getProxy_flag()+" proxy: "+serverSetting.getProxy_server()
			+" proxy auth: "+serverSetting.getProxy_auth_flag()+ " proxy username: "+serverSetting.getProxy_user_name());
		boolean result = EdgeEmailService.GetInstance().sendTestMail(serverSetting);
		logger.info("[ConfigurationServiceImpl] testEmailServerTemplateSetting() test email sending finished. the result is: "+result);
		return result;
	}

	@Override
	public EmailTemplateSetting getEmailTemplateSetting(int featureId)
			throws EdgeServiceFault {
		List<EmailTemplateSetting> settingList = new ArrayList<EmailTemplateSetting>();
		settingDao.as_edge_email_template_setting_get(featureId, settingList);
		if ( settingList.size() > 0 )
			return settingList.get(0);
		else
			return null;
	}

	@Override
	public void saveEmailTemplateSetting(EmailTemplateSetting templateSetting)
			throws EdgeServiceFault {
		
		
		settingDao.as_edge_email_template_setting_set(
					templateSetting.getFrom_addrs(),
					templateSetting.getRecipients(),
					templateSetting.getSubject(),
					templateSetting.getHtml_flag(),
					templateSetting.getFeature_Id());
			
	}
	
	@Override
	public Account getEdgeAccount() throws EdgeServiceFault {
		EdgeAccount account = nativeCode.getEdgeAccount();
		Account result = new Account();
		result.setDomain(account.getDomain());
		result.setUsername(account.getUserName());
		result.setPassword(account.getPassword());
		
		return result;
	}
	
	@Override
	public void saveEdgeAccount(Account account) throws EdgeServiceFault {
		serviceImpl.validateUser(account.getUsername(), account.getPassword(), account.getDomain());
		
		EdgeAccount edgeAccount = new EdgeAccount();
		edgeAccount.setDomain(account.getDomain());
		edgeAccount.setUserName(account.getUsername());
		edgeAccount.setPassword(account.getPassword());
		nativeCode.saveEdgeAccount(edgeAccount);
	}
	public Date convertString2Date(String str) {
		Date date = new Date();
		date.setHours(Integer.valueOf(str.split(":")[0]));
		date.setMinutes(Integer.valueOf(str.split(":")[1]));
		return date;
	}
	
	public void setNativeCode(NativeFacade nativeCode) {
		this.nativeCode = nativeCode;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public DeployD2DSettings getDeployD2DSettings() throws EdgeServiceFault
	{
		List<EdgeDeployD2DSettings> settingsList = new LinkedList<EdgeDeployD2DSettings>();
		settingDao.getDeployD2DSettings( settingsList );
		if (settingsList.size() == 0)
			return DeployD2DSettings.getDefaultSettings();
		
		EdgeDeployD2DSettings daoSettings = settingsList.get( 0 );
		
		DeployD2DSettings settings = new DeployD2DSettings();
		settings.setPort( daoSettings.getPort() );
		if(daoSettings.getInstallPath().equalsIgnoreCase("%ProgramFiles%\\CA\\arcserve Unified Data Protection")){
			settings.setInstallPath(DeployD2DSettings.getDefaultSettings().getInstallPath());
		} else {
			settings.setInstallPath( daoSettings.getInstallPath() );
		}
		settings.setAllowInstallDriver( daoSettings.getAllowInstallDriver() != 0 );
		settings.setProtocol( Protocol.parse(daoSettings.getProtocol()) );
		settings.setProductType(daoSettings.getProductType());
		return settings;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void saveDeployD2DSettings( DeployD2DSettings settings ) throws EdgeServiceFault
	{
		settingDao.saveDeployD2DSettings(
			settings.getPort(),
			settings.getInstallPath(),
			settings.isAllowInstallDriver() ? 1 : 0,
			RebootType.RebootAtOnce.ordinal(),
			settings.getProtocol().ordinal() ,
			settings.getProductType(),
			new Date()); //in the new UI , there is no reboot related option (reboot type and reboot time), it should be removed from setting table and procedure.
	}
	
	private void deleteEmailServerSetting(EdgeDaoCommonExecuter ede){
		
		try {
			
			String sqlStrDelete = "DELETE FROM as_edge_email_setting ";
			ede.ExecuteDao(sqlStrDelete, null);
			
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		}
		
	}
	
	private void saveEmailServerSetting(EmailService mailServer,
			String smtp, int port, short authFlag, String userName,
			String userPassword, short sslFlag, short tlsFlag, short proxyFlag,
			String proxyServer, int proxyPort, short proxyAuthFlag,
			String proxyUserName, String proxyUserPassword,short autoDiscoveryFlag, boolean isEnableEmailAlerts, EdgeDaoCommonExecuter ede) {
		
		
		String sqlStrDelete = "DELETE FROM as_edge_email_setting ";
	    String sqlStrInsert = "INSERT INTO as_edge_email_setting( "
			        +   "mail_server, smtp, port, auth_flag, user_name, user_password, "
			        +   "ssl_flag, tls_flag, "
			        +   "proxy_flag, proxy_server, proxy_port, proxy_auth_flag, proxy_user_name, "
			        +	"proxy_user_password, auto_discovery_flag, update_time, is_enabled) "
			        +   "VALUES (?, ?, " + port + ", " + authFlag + ", ?, ?, "
			        +   sslFlag + ", " + tlsFlag + ", "
			        +   proxyFlag + ", ?, " + proxyPort + ", " + proxyAuthFlag + ", "
			        +	"?, ?,"+autoDiscoveryFlag+",? , ?)";

	    List<Object> pa = new ArrayList<Object>();

		try {
			pa.add(mailServer.ordinal());
			pa.add(EdgeDaoCommonExecuter.getSafeString(smtp));
			pa.add(EdgeDaoCommonExecuter.getSafeString(userName));
			pa.add(EdgeDaoCommonExecuter.getSafeString(userPassword));
			pa.add(EdgeDaoCommonExecuter.getSafeString(proxyServer));
			pa.add(EdgeDaoCommonExecuter.getSafeString(proxyUserName));
			pa.add(EdgeDaoCommonExecuter.getSafeString(proxyUserPassword));
			pa.add(new Timestamp(Calendar.getInstance().getTime().getTime()));
			pa.add(isEnableEmailAlerts);
			ede.ExecuteDao(sqlStrDelete, null);
			ede.ExecuteDao(sqlStrInsert, pa);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		}
	}
	
	private void deleteEmailTemplateSetting(int featureId,EdgeDaoCommonExecuter ede) {
		
		try {
			String sql = "DELETE FROM as_edge_email_info WHERE feature_id = ?";
			List<Object> pa = new ArrayList<Object>();
			pa.add(new Integer(featureId));
			ede.ExecuteDao(sql, pa);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		}
		
	}
	
	private void saveEmailTemplateSetting(String from,
			String recipients, String subject, short htmlFlag, int featureId,EdgeDaoCommonExecuter ede) {
		
		List<EmailTemplateSetting> settingList = new LinkedList<EmailTemplateSetting>();
		String sqlStr = "";

		List<Object> pa = new ArrayList<Object>();
		try {
			
			sqlStr = "SELECT from_addrs, recipients, subject, html_flag from as_edge_email_info " +
	    		"WHERE feature_id = ?";

			pa.add(new Integer(featureId));
			ede.ExecuteDao(sqlStr, pa, EmailTemplateSetting.class, settingList);


			if( null != settingList && settingList.size() > 0 ){
				sqlStr = "UPDATE as_edge_email_info SET " +
					"from_addrs = ?, recipients = ?, subject = ?, html_flag = ?, update_time=? " +
					"WHERE feature_id = ?";
			}else{
				sqlStr = "INSERT INTO as_edge_email_info " +
						"(from_addrs, recipients, subject,  html_flag, update_time, feature_id) " +
						"VALUES(?, ?, ?, ?, ?, ?)";
			}
			pa.clear();
			pa.add(EdgeDaoCommonExecuter.getSafeString(from));
			pa.add(EdgeDaoCommonExecuter.getSafeString(recipients));
			pa.add(EdgeDaoCommonExecuter.getSafeString(subject));
			pa.add(new Short(htmlFlag));
			pa.add(new Timestamp(Calendar.getInstance().getTime().getTime()));
			pa.add(new Integer(featureId));
			ede.ExecuteDao(sqlStr, pa);
			
			
		}catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		} 
		
	}
	
	private Date generateRepeatFromDateByScheduleTime(Date scheduleTime) {
		Date repeatFromDate = new Date();
		repeatFromDate.setYear(scheduleTime.getYear());
		repeatFromDate.setMonth(scheduleTime.getMonth());
		repeatFromDate.setDate(scheduleTime.getDate());
		repeatFromDate.setHours(0);
		repeatFromDate.setMinutes(0);
		repeatFromDate.setSeconds(0);
		return repeatFromDate;
	}
	
	public void addLog(ActivityLog log) throws EdgeServiceFault {
		try {
			logService.addLog(log);
		} catch (Exception e) {
			System.err.println("logService error:" + e.getMessage());
		}

	}
	
	public void sendDiscoveryNodesAlertToCPM(String send_host, String subject, String content, Date date) throws EdgeServiceFault
	{	
		AlertManager.getInstance().saveAlertToDB(send_host, send_host, -1L, CommonEmailInformation.EVENT_TYPE.CPM_DISCOVERY_EVENT.getValue(),
				subject, content, date, CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );
		
	}

	@Override
	public String getConfigurationParam(int paramId)
			throws EdgeServiceFault {
		try {			
			String[] paramValue = new String[1];
			settingDao.as_edge_configuration_getById(paramId, paramValue);
			return paramValue[0];
		} catch (Exception e) {
			System.err.println("as_edge_configuration_getById error:" + e.getMessage());
		}
		return null;
	}

	@Override
	public void inserOrUpdateConfigurationParam(int paramId,String key, String value)
			throws EdgeServiceFault {
		try {
			settingDao.as_edge_configuration_insertOrUpdate(paramId, key, value);
		} catch (Exception e) {
			System.err.println("as_edge_configuration_insertOrUpdate error:" + e.getMessage());
		}		
	}

	@Override
	public void deleteConfigurationParam(int paramId) throws EdgeServiceFault {
		try {
			settingDao.as_edge_configuration_delete(paramId);
		} catch (Exception e) {
			System.err.println("as_edge_configuration_delete error:" + e.getMessage());
		}		
	}
	
}
