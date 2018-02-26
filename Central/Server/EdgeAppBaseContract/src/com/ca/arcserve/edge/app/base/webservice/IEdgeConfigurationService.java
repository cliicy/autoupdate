package com.ca.arcserve.edge.app.base.webservice;

import java.util.Date;

import javax.jws.WebService;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Account;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.CmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DeployD2DSettings;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.PreferenceConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;

@WebService(targetNamespace = "http://webservice.srm.edge.arcserve.ca.com/")
public interface IEdgeConfigurationService {

	DBConfigInfo getDatabaseConfiguration() throws EdgeServiceFault;

	void setDatabaseConfiguration(DBConfigInfo dbConfig)
			throws EdgeServiceFault;

	Boolean testSQLServer(String serverName, String instance, int port,
			String userName, @NotPrintAttribute String password) throws EdgeServiceFault;
	
	EmailServerSetting getEmailServerTemplateSetting(int featureId) throws EdgeServiceFault;
	
	EmailServerSetting getEmailServerSetting() throws EdgeServiceFault;
	
	void deleteEmailServerTemplateSetting(int featureId) throws EdgeServiceFault ;
	
	void saveEmailServerTemplateSetting(EmailServerSetting setting) throws EdgeServiceFault ;
	
	void saveEmailServerSetting(EmailServerSetting serverSetting) throws EdgeServiceFault;
	
	boolean testEmailServerTemplateSetting(EmailServerSetting serverSetting) throws EdgeServiceFault;
	
	EmailTemplateSetting getEmailTemplateSetting(int featureId) throws EdgeServiceFault;
	
	void saveEmailTemplateSetting( EmailTemplateSetting templateSetting ) throws EdgeServiceFault;
	
	CmInfo getCmConfiguration() throws EdgeServiceFault;
	
	int setCmConfiguration(CmInfo cmInfo) throws EdgeServiceFault;

	int setDatabaseConfigurationWithSchemaCreation(DBConfigInfo dbConfig,
			boolean needCreateDbSchema) throws EdgeServiceFault;

	PreferenceConfigInfo getPreferenceConfiguration() throws EdgeServiceFault;

	void setPreferenceConfiguration(PreferenceConfigInfo pfConfig)
			throws EdgeServiceFault;
	
	public Account getEdgeAccount() throws EdgeServiceFault;
	public void saveEdgeAccount(Account account) throws EdgeServiceFault;
	
	DeployD2DSettings getDeployD2DSettings() throws EdgeServiceFault;
	void saveDeployD2DSettings( DeployD2DSettings settings ) throws EdgeServiceFault;
	
	void sendDiscoveryNodesAlertToCPM(String send_host, String subject, String content, Date date) throws EdgeServiceFault;

	String getConfigurationParam(int paramId) throws EdgeServiceFault;
	
	void inserOrUpdateConfigurationParam(int paramId, String key, String value) throws EdgeServiceFault;
	
	void deleteConfigurationParam(int paramId) throws EdgeServiceFault;
	
}
