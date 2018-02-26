package com.ca.arcserve.edge.app.base.configuration.cm;

import java.util.Date;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.dao.ICommunicateCM;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeCM4EdgeReport;
import com.ca.arcserve.edge.app.base.webservice.IEdgeConfigurationService;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.CmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class CommunicateCMImpl implements ICommunicateCM {

	private Module logModule;
	private IActivityLogService logService = new ActivityLogServiceImpl();
	private IEdgeConfigurationService configService = null;
	private BaseWebServiceFactory serviceFactory = new BaseWebServiceFactory();
	
	public CommunicateCMImpl(Module logModule, IEdgeConfigurationService configService){
		this.logModule = logModule;
		this.configService = configService;
	}
	
	@Override
	public boolean reConnectCmDatabase() throws EdgeServiceFault {
		ActivityLog log;
		CmInfo cmInfo = getCmConfiguration();
		if( null == cmInfo ){
			log = new ActivityLog();
			log.setModule(logModule);
			log.setSeverity(Severity.Error);
			log.setMessage(EdgeCMWebServiceMessages.getResource("Common_Service_General"));
			log.setTime(new Date());
			addLog(log);
			String errMsg = "Fail to get CM configuration";
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(EdgeServiceErrorCode.Common_Service_General, errMsg);
			throw new EdgeServiceFault(errMsg, bean);
		}

		DBConfigInfo oldDbConfig;
		try {
			oldDbConfig = configService.getDatabaseConfiguration();
		} catch (Exception e) {
			oldDbConfig = null;
		}
		DBConfigInfo newDbConfig = getCmDatabaseConfiguration(cmInfo.getHost(), cmInfo.getPort(), cmInfo.getProtocol(),
				EdgeCommonUtil.getDomainName(cmInfo.getUserName()), EdgeCommonUtil.getUserName(cmInfo.getUserName()), cmInfo.getPassword());
		if( null == newDbConfig ){
			log = new ActivityLog();
			log.setModule(logModule);
			log.setSeverity(Severity.Error);
			log.setMessage(EdgeCMWebServiceMessages.getResource("Common_Service_General"));
			log.setTime(new Date());
			addLog(log);
			String errMsg = "Fail to get DB configuration from CM";
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(EdgeServiceErrorCode.Common_Service_General, errMsg);
			throw new EdgeServiceFault(errMsg, bean);
		}

		if( !newDbConfig.equals(oldDbConfig) ){
			configService.setDatabaseConfigurationWithSchemaCreation(newDbConfig, false);
			return true;
		}else {
			return false;
		}
	}
	
	private void addLog(ActivityLog log) throws EdgeServiceFault {
		try {
			logService.addLog(log);
		} catch (Exception e) {
			System.err.println("logService error:" + e.getMessage());
		}

	}
	
	public CmInfo getCmConfiguration() throws EdgeServiceFault {
		CmInfo cmInfo = null;
		ActivityLog log = new ActivityLog();
		log.setModule(logModule);
		log.setSeverity(Severity.Error);

		try {
			cmInfo = configService.getCmConfiguration();
		} catch (EdgeServiceFault e) {
			log.setMessage(e.getMessage());
			log.setTime(new Date());
			//logService.addLog(log);
			throw e;
		}

		return cmInfo;
	}
	
	public DBConfigInfo getCmDatabaseConfiguration(String host, int port,
			String protocol, String domain, String userName, @NotPrintAttribute String password)
			throws EdgeServiceFault {
		BaseWebServiceClientProxy webService;
		try {
			webService = serviceFactory.getEdgeWebService(
					protocol, host, port, IEdgeCM4EdgeReport.class);
		}catch (Exception e) {
			EdgeServiceFaultBean faultInfo = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailConnect_CM, "Fail connect to CM web service");
			throw new EdgeServiceFault("", faultInfo);
		}

		DBConfigInfo dbConfigInfo = null;
		ActivityLog log = new ActivityLog();
		log.setModule(logModule);
		log.setSeverity(Severity.Error);

		try {
			if( StringUtil.isEmptyOrNull(domain) ){
				domain = host;
			}
			int result = webService.getBaseService().validateUser(userName,
					password, domain);

			if (result == 0) {
				dbConfigInfo = ((IEdgeCM4EdgeReport) webService.getService())
						.getDatabaseConfiguration();
			}
		} catch (EdgeServiceFault e) {
			log.setMessage(e.getMessage());
			log.setTime(new Date());
			//logService.addLog(log);
			throw e;
		}

		return dbConfigInfo;
	}

	public Boolean testSQLServer(String serverName, String instance, int port, String userName, String password) throws EdgeServiceFault {
		ActivityLog log = new ActivityLog();
		log.setModule(logModule);
		log.setSeverity(Severity.Error);
		try{
			return configService.testSQLServer(serverName, instance, port,
					userName, password);
		}catch (EdgeServiceFault e) {
			
			if( EdgeServiceErrorCode.Configuration_FailConnectCMDb.equals(
					e.getFaultInfo().getCode()) ){
				e.getFaultInfo().setCode(EdgeServiceErrorCode.Configuration_Report_FailConnectCMDb);
				log.setMessage(EdgeCMWebServiceMessages.getResource("Configuration_Report_FailConnectCMDb"));
				log.setTime(new Date());
				addLog(log);
			}
			throw e;
		}
	}

	public int setDatabaseConfigurationWithSchemaCreation(
			DBConfigInfo dbConfig, boolean needCreateDbSchema)
			throws EdgeServiceFault {
		ActivityLog log = new ActivityLog();
		log.setModule(logModule);
		log.setSeverity(Severity.Error);
	
		try {
			int result = configService.setDatabaseConfigurationWithSchemaCreation(
					dbConfig, needCreateDbSchema);
			return result;
		} catch (EdgeServiceFault e) {
			log.setMessage(e.getMessage());
			log.setTime(new Date());
			//logService.addLog(log);
			throw e;
		}
	}

	public int setCmConfiguration(CmInfo cmInfo)
			throws EdgeServiceFault {
		ActivityLog log = new ActivityLog();
		log.setModule(Module.ReportCommon);
		log.setSeverity(Severity.Error);
	
		try {
			return configService.setCmConfiguration(cmInfo);
		} catch (EdgeServiceFault e) {
			log.setMessage(e.getMessage());
			log.setTime(new Date());
	//		addLog(log);
			throw e;
		}
	}

}
