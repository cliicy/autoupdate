package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.configuration.ConfigurationServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.email.EmailTemplateFeature;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.email.EdgeEmailService;

public class DiscoveryUtil {

	private static ConfigurationServiceImpl configureService = new ConfigurationServiceImpl();
	private static Logger logger = Logger.getLogger(EsxDiscoveryTask.class);
	
	public static List<String> getIpAdressByHostName(String hostName) {
		return getIpAdressByHostName(hostName, false);
	}
	
	public static List<String> getIpAdressAndHostNames(String hostName) {
		return getIpAdressByHostName(hostName, true);
	}
	
	private static List<String> getIpAdressByHostName(String hostName, boolean includeHostName) {
		List<String> ipList = new ArrayList<String>();
		if (hostName == null || hostName.trim().isEmpty()) {
			return ipList;
		}
		hostName = hostName.trim();
		ipList.addAll(getIpsByHostName(hostName, includeHostName));
		
		int firstIndex = hostName.indexOf(".");
		String hostNameWithoutDomain = hostName;
		if (firstIndex >0 && firstIndex < hostName.length()) {
			hostNameWithoutDomain = hostName.substring(0, firstIndex);
			ipList.addAll(getIpsByHostName(hostNameWithoutDomain, includeHostName));
		}
		
		Set<String> set  = new HashSet<String>(ipList);
		return new ArrayList<String>(set);
	}
	
	private static List<String> getIpsByHostName(String hostName, boolean includeHostName) {
		List<String> ipList = new ArrayList<String>();
		try {
			InetAddress[] addressArray = InetAddress.getAllByName(hostName);
			if (addressArray != null && addressArray.length != 0) {
				for (InetAddress adress : addressArray) {
					String ipString = adress.getHostAddress();
					if (ipString != null && !ipList.contains(ipString) ) {
						ipList.add(ipString);
					}
					
					if (includeHostName) {
						String aHostName = adress.getHostName();
						if (aHostName != null && !ipList.contains(aHostName)) {
							ipList.add(aHostName);
						}
					}
				}
			}
		} catch (UnknownHostException e) {
			logger.warn("Failed to get ip address by host name of '" + hostName + "'");
		}
		
		return ipList;
	}
	
	public static boolean getEnableAutoDiscoveryEmailAlert() throws EdgeServiceFault {
		
		ConfigurationServiceImpl configurationServiceImpl = new ConfigurationServiceImpl();
		EmailServerSetting emailSetting = null;
		try {
			emailSetting = configurationServiceImpl.getEmailServerSetting();			
		} catch (EdgeServiceFault e) {
			/*this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"getAutoDiscoveryEmailAlertSubject()." );*/
			
			/*activityLog.setSeverity(Severity.Error);
			activityLog.setTime(new Date(System.currentTimeMillis()));
			activityLog.setMessage(e.getFaultInfo().getMessage());
			activityLogService.addLog(activityLog);*/
		}
		
		boolean bEnable = false;
		
		if(emailSetting != null && emailSetting.getAuto_discovery_flag() == 1)
			bEnable = true;
		
		return 	bEnable;
	}
	
	private static int getEmailTemplateId()
	{
		EdgeApplicationType edgeAppType = EdgeWebServiceContext.getApplicationType();
		int emailTemplateId = EmailTemplateFeature.D2DPolicy;
		switch (edgeAppType) {
		case CentralManagement:
			emailTemplateId = EmailTemplateFeature.D2DPolicy;
			break;

		case VirtualConversionManager:
			emailTemplateId = EmailTemplateFeature.VCMPolicy;
			break;

		case vShpereManager:
			emailTemplateId = EmailTemplateFeature.VSpherePolicy;
			break;

		case Report:
			emailTemplateId = EmailTemplateFeature.Report;
			break;
		}
		
		return emailTemplateId;
	}
	
	public static EmailTemplateSetting getEmailTemplateSetting() throws EdgeServiceFault {
		ConfigurationServiceImpl configurationServiceImpl = new ConfigurationServiceImpl();
		int emailTemplateId = getEmailTemplateId();
		
		EmailTemplateSetting emailTemplate = null;
		try {
			emailTemplate = configurationServiceImpl.getEmailTemplateSetting(emailTemplateId);			
		} catch (EdgeServiceFault e) {
			throw e;		
		}
		
		return 	emailTemplate;
	}		
	
	public static void sendAutoDiscoveryEmailWithHost(String subject, String content) {

		int emailTemplateId = getEmailTemplateId();
		
		EdgeEmailService emailSrv = EdgeEmailService.GetInstance();
							
		emailSrv.SendMailWithGlobalSetting(emailSrv.getHostName(),
					subject, content, emailTemplateId);		

	}
	
	
	public static void sendAutoDiscoveryEmailWithHostToCPM(String subject, String content)
		throws EdgeServiceFault {
		
		try {
			configureService.sendDiscoveryNodesAlertToCPM(
					EdgeEmailService.GetInstance().getHostName().toLowerCase(),
					subject,
					content, 
					new Date(System.currentTimeMillis()));
			
		} catch (EdgeServiceFault e) {		
			
			throw e;
		}
		
	}
	
	/**
	 * If original hostname is not null/empty new name is null/empty, then return orignal name
	 * If original hostname is fqdn name, new name is short name then return original name
	 * If original hostname is short name, new name is fqdn name then return original name 
	 * other case, retun new host name
	 * @param originalHostName
	 * @param newHostName
	 * @return
	 */
	public static String getToUpdatedHostName(String originalHostName, String newHostName){
		if(StringUtil.isEmptyOrNull(originalHostName) && StringUtil.isEmptyOrNull(newHostName)){
			return "";
		}
		if(StringUtil.isEmptyOrNull(originalHostName) && !StringUtil.isEmptyOrNull(newHostName)){
			return newHostName;
		}
		if(!StringUtil.isEmptyOrNull(originalHostName) && StringUtil.isEmptyOrNull(newHostName)){
			return originalHostName;
		}
		
		originalHostName = originalHostName.toLowerCase();
		newHostName = newHostName.toLowerCase();
		
		if (originalHostName.contains(".") && !newHostName.contains(".")) {
			String originalShortName = originalHostName.substring(0, originalHostName.indexOf("."));
			if(newHostName.equalsIgnoreCase(originalShortName)){
				return originalHostName;
			}
		}else if (!originalHostName.contains(".") && newHostName.contains(".")) {
			String newShortName = newHostName.substring(0, newHostName.indexOf("."));
			if(originalHostName.equalsIgnoreCase(newShortName)){
				return originalHostName;
			}
		}
		
		return newHostName;
	}
}
