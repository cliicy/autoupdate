package com.ca.arcflash.ui.server;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.rps.webservice.RPSWebServiceFactory;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.replication.CAProxy;
import com.ca.arcflash.rps.webservice.replication.CAProxySelector;
import com.ca.arcflash.rps.webservice.replication.HttpProxy;
import com.ca.arcflash.ui.client.common.IConfigRPSInD2DSideService;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
import com.ca.arcflash.ui.client.model.ProxySettingsModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2D;
import com.ca.arcflash.webservice.FlashServiceErrorCode;


public class ConfigRPSInD2DSideServiceImpl extends BaseServiceImpl implements IConfigRPSInD2DSideService{

	private static final long serialVersionUID = -5016736785952601510L;
	private static final Logger logger = Logger.getLogger(ConfigRPSInD2DSideServiceImpl.class);

	@Override
	public List<RpsPolicy4D2D> getRPSPolicyList(String hostName, String userName,
			String password, int port, String protocol,ProxySettingsModel proxy)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		RPSWebServiceClientProxy client = null;
		try
		{
			String usernameNew = getUserName(userName);
			String domain = getDomainNameFromUserName(userName);
			if(proxy != null){
				CAProxy caProxy = new CAProxy();
				caProxy.setTargetHost(hostName);
				caProxy.setHttpProxy(convertToHttpProxy(proxy));
				CAProxySelector.getInstance().registryProxy(caProxy);
			}
			client = RPSWebServiceFactory.getRPSService4CPM(protocol, hostName, port);
			client.getServiceForCPM().validateUser(usernameNew, password, domain);
			RPSPolicy[] rpsPolicyList = client.getServiceForCPM().getRPSPolicySummaries(null);
		
			if(rpsPolicyList == null){
		return null;
	}
	
			DataStoreSettingInfo[] dsList = client.getServiceForCPM().getDataStoreInstance(null);
			List<RpsPolicy4D2D> rpsPolicyModelList = new ArrayList<RpsPolicy4D2D>();
			for (RPSPolicy policy : rpsPolicyList) {
				rpsPolicyModelList.add(convertToModel(policy));
			}
			List<RpsPolicy4D2D> validateList = setDataStoreSettingInfo(rpsPolicyModelList,dsList);
			Collections.sort(validateList, new Comparator<RpsPolicy4D2D>() {

				@Override
				public int compare(RpsPolicy4D2D o1, RpsPolicy4D2D o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			
			return validateList;
		}catch(WebServiceException exception){
			logger.error("Error occurs during getRPSPolicyList");
			String locale = this.getServerLocale();
			if(exception.getCause() instanceof UnknownHostException){
				throw new ServiceConnectException(
						FlashServiceErrorCode.Common_CantConnectRemoteServer,
						ResourcesReader.getResource("ServiceError_"
								+ FlashServiceErrorCode.Common_CantConnectRemoteServer,
								locale)); 
			}
			if(exception.getCause() instanceof ConnectException || exception.getCause() instanceof SSLHandshakeException || exception.getCause() instanceof SocketException|| exception.getCause() instanceof SSLException || exception.getMessage().startsWith("XML reader error") ){
				throw new ServiceConnectException(
						FlashServiceErrorCode.Common_CantConnectService,
						MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
								+ FlashServiceErrorCode.Common_CantConnectService,
								locale),ResourcesReader.getResource("ProductNameRPS", locale))); 
			} 
			/*if (exception.getCause() instanceof Error && exception.getMessage().startsWith(UNDEFINED_OPERATION_NAME)) {
				throw generateException(FlashServiceErrorCode.EDGE_D2D_INTERFACE_MISMATCH);
			}*/
			
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException se = (SOAPFaultException) exception;
				if (se.getFault() != null
						&& (FlashServiceErrorCode.Login_WrongCredential
								.equals(se.getFault().getFaultCodeAsQName()
										.getLocalPart()))) {
					BusinessLogicException ex = this.generateException(se
							.getFault().getFaultCodeAsQName().getLocalPart());
					String errMsg = MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
							+ FlashServiceErrorCode.BackupConfig_RPS_CREDENTIAL_WRONG, locale), hostName);
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
				
				if (se.getFault() != null
						&& (FlashServiceErrorCode.Login_NotAdministrator
										.equals(se.getFault().getFaultCodeAsQName()
												.getLocalPart()))) {
					BusinessLogicException ex = this.generateException(se
							.getFault().getFaultCodeAsQName().getLocalPart());
					String errMsg = MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
							+ FlashServiceErrorCode.VSPHERE_PROXY_USER_NOT_ADMINISTRATOR, locale), hostName);
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
			}
			proccessAxisFaultException(exception);
			
		}
		return null;
	}
	
	private HttpProxy convertToHttpProxy(ProxySettingsModel model){
		HttpProxy proxy = new HttpProxy();
		proxy.setProxyHostname(model.getProxyServerName());
		proxy.setProxyUsername(model.getProxyUserName());
		proxy.setProxyPassword(model.getProxyPassword());
		proxy.setProxyPort(model.getProxyPort());
		proxy.setProxyRequiresAuth(model.getProxyUserName()==null?false:true);
		return proxy;
	}
	private RpsPolicy4D2D convertToModel(RPSPolicy policy) {
		RpsPolicy4D2D model = new RpsPolicy4D2D();
		model.setId(policy.getId());
		model.setName(policy.getName());
		model.setDataStoreName(policy.getRpsSettings().getRpsDataStoreSettings().getDataStoreName());
		model.setDataStoreDisplayName(policy.getRpsSettings().getRpsDataStoreSettings().getDataStoreDisplayName());
		model.setDataStoreId(policy.getRpsSettings().getRpsDataStoreSettings().getDataStoreId());
		model.setPolicyid(policy.getPolicyid());
		
		return model;
	}
	
	private List<RpsPolicy4D2D> setDataStoreSettingInfo(List<RpsPolicy4D2D> policyList,DataStoreSettingInfo[] dsList){
		List<RpsPolicy4D2D> validateList = new ArrayList<RpsPolicy4D2D>();
		for(RpsPolicy4D2D policyModel : policyList){
			for(DataStoreSettingInfo ds : dsList){
				if(policyModel.getDataStoreName().equals(ds.getDatastore_name())){
				    policyModel.setEnableGDD(ds.getEnableGDD()==1?true:false);
				    policyModel.setDataStoreDisplayName(ds.getDisplayName());
				    policyModel.setDataStoreName(ds.getDatastore_name());
				    policyModel.setDataStoreId(ds.getDatastore_id());
				  	validateList.add(policyModel);
					break;
				}
				
			}
		}
		return validateList;
	}
	
	
private String getDomainNameFromUserName(String strUserInput){
		
		String strDomain = "";
		
		if (strUserInput == null || strUserInput.isEmpty())
			return strDomain;
		
		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
		}
		else 
		{
			// Extract domain part
			strDomain = strUserInput.substring(0, pos);
		}
		return strDomain;
	}
	
	private String getUserName(String strUserInput)
	{
		String strUser = "";
		
		if (strUserInput == null || strUserInput.isEmpty())
			return strUser;
		
		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
			strUser = strUserInput;
		}
		else 
		{
			// Extract user name part
			strUser = strUserInput.substring(pos+1);
		}
		return strUser;
	}
	@Override
	public List<RpsHostModel> getRPSHostList() {
		List<RpsHostModel> rpsHostModelList = new ArrayList<RpsHostModel>();
//		List<RpsHost> hostList = getServiceClient_RpsService().getServiceForCPM().getRpsNodes();
//		for(RpsHost host : hostList){
//			rpsHostModelList.add(ConvertRpsHostToRpsHostModel(host));
//		}
		return rpsHostModelList;
	}
//	
//	private RpsHostModel ConvertRpsHostToRpsHostModel(RpsHost host){
//		RpsHostModel hostModel = new RpsHostModel();
//		hostModel.setHostName(host.getRhostname());
//		hostModel.setUserName(host.getUsename());
//		hostModel.setPassword(host.getPassword());
//		hostModel.setPort(host.getPort());
//		hostModel.setIsHttpProtocol(host.getIsHttpProtocol());
//		return hostModel;
//	}
}
