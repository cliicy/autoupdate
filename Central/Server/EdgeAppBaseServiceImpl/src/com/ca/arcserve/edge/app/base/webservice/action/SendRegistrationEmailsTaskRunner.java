package com.ca.arcserve.edge.app.base.webservice.action;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.configuration.ConfigurationServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.action.SendRegistrationEmailsParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.email.EmailTemplateFeature;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GenerateGatewayRegStrParam;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.email.EdgeEmailService;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeGatewayBean;

public class SendRegistrationEmailsTaskRunner extends AbstractTaskRunner<Integer>{
	private static final Logger logger = Logger.getLogger(SendRegistrationEmailsTaskRunner.class);
	private EdgeGatewayBean gatewayBean = new EdgeGatewayBean();
	private ConfigurationServiceImpl configImpl = new ConfigurationServiceImpl();
	
	public SendRegistrationEmailsTaskRunner(Integer key, ActionTaskParameter<Integer> parameter, CountDownLatch doneSignal, ActionTaskManager<Integer> manager){
		super(key, parameter, doneSignal, manager);
	}

	@Override
	protected void excute() {
		try {
			logger.debug("[SendRegistrationEmailsTaskRunner] execute start");	
			SiteInfo site = gatewayBean.getSite(new SiteId(entityKey));					
			if(site==null){
				logger.debug("[SendRegistrationEmailsTaskRunner] siteInfo is null.");
				String message = EdgeCMWebServiceMessages.getMessage("site_UnvalidId", entityKey );
				long logId = generateLog(Severity.Error,message);
				addFailedEntities(entityKey,logId);
				return;
			}
			
			// generate registration key
			SendRegistrationEmailsParameter param = (SendRegistrationEmailsParameter)parameter;
			String registrationText = this.generateKey(param.getConsoleURL(), site);
			if(registrationText.equals(site.getRegistrationText())){
				String message = EdgeCMWebServiceMessages.getMessage("site_NoNeedSendRegEmail",site.getName());
				long logId = generateLog(Severity.Warning,message);
				addFailedEntities(entityKey,logId);
				return;
			}
			
			// save site info
			saveSite(param.getConsoleURL(),registrationText,site);
			
			// send email 			
			if(StringUtil.isEmptyOrNull(site.getEmail())){
				logger.debug("[SendRegistrationEmailsTaskRunner] siteInfo has a null emailAddress");
				String message = EdgeCMWebServiceMessages.getMessage("site_NoConfig_EmailAddress", site.getName() );
				long logId = generateLog(Severity.Error,message);
				addFailedEntities(entityKey,logId);
				return;
			}
			
			EmailServerSetting setting = configImpl.getEmailServerTemplateSetting(EmailTemplateFeature.D2DPolicy);
			if(setting==null){
				logger.debug("[SendRegistrationEmailsTaskRunner] getEmailServerTemplateSetting fail");
				String message = EdgeCMWebServiceMessages.getMessage("EDGEMAIL_NoConfigure");
				long logId = generateLog(Severity.Error,message);
				addFailedEntities(entityKey,logId);
				return;
			}
			
			EmailTemplateSetting templateServerSetting = setting.getTemplateSetting();	

			templateServerSetting.setRecipients(site.getEmail());
			templateServerSetting.setSubject(EdgeCMWebServiceMessages.getMessage("site_SendRegEmailSubject", site.getName()));
			String contectStr = EdgeCMWebServiceMessages.getMessage("site_regirstrationEmailContent",param.getConsoleURL()) 
					+ "\n\n" + EdgeCMWebServiceMessages.getMessage("site_registrationChangeStep1")
					+ "\n\n" + EdgeCMWebServiceMessages.getMessage("site_registrationChangeStep2")
					+ "\n\t" + EdgeCMWebServiceMessages.getMessage("site_registrationAction1")
					+ "\n\t" + EdgeCMWebServiceMessages.getMessage("site_registrationAction2")
					+ "\n\t" + EdgeCMWebServiceMessages.getMessage("site_registrationAction3")
					+ "\n\t" + EdgeCMWebServiceMessages.getMessage("site_registrationAction4")
					+ "\n\t" + registrationText;		
			templateServerSetting.setContent(contectStr);
						
			Boolean result = EdgeEmailService.GetInstance().sendTestMail(setting);	
			if(result){
				String message = EdgeCMWebServiceMessages.getMessage(
						"gateway_SiteSendRegistrationEmailSucc", site.getName());
				generateLog(Severity.Information,message);
				addSucceedEntities(entityKey);
			} else {
				String message = EdgeCMWebServiceMessages.getMessage(
						"gateway_SiteSendRegistrationEmailFail", site.getName());
				long logId = generateLog(Severity.Error,message);
				addFailedEntities(entityKey,logId);
			}			
			logger.debug("[SendRegistrationEmailsTaskRunner] siteInfo send email result:"+result);
			
		} catch (Exception exception) {
			logger.error("[ManageNodeTaskRunner] excute() failed.", exception);			
		
			String errorMessage = "";
			if(exception instanceof EdgeServiceFault){
				EdgeServiceFaultBean fault = ((EdgeServiceFault) exception).getFaultInfo();
				errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),fault);
			}else{
				EdgeServiceFaultBean fault  = new EdgeServiceFaultBean(EdgeServiceErrorCode.Common_Service_General, null);
				errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),fault);
			}
			String logMsg = EdgeCMWebServiceMessages.getMessage("sendRegistrationEmails_Log",errorMessage);
			long logId = generateLog(Severity.Error,logMsg);
			addFailedEntities(entityKey,logId);
			
		}
	}
		
	private String generateKey(String url,SiteInfo site) throws EdgeServiceFault{
		int index1 = url.indexOf( ":" );
		int index2 = url.lastIndexOf( ":" );
		final String protocol = url.substring( 0, index1 );
		final String portStr = url.substring( index2 + 1, url.length() );
		final String hostname = url.substring( index1+3, index2 );
		
		final GenerateGatewayRegStrParam param = new GenerateGatewayRegStrParam();
		param.setGatewayId(site.getGatewayId());
		param.setRegSvrHostName(hostname);
		param.setRegSvrPort(Integer.parseInt(portStr));
		param.setRegSvrProtocol(protocol.toLowerCase());
		
		param.setGatewayProtocol("");
		param.setGatewayPort(0);
		param.setGatewayUsername("");
		param.setGatewayPassword("");
		
		return gatewayBean.generateGatewayRegistrationString(param);
	}
	
	private void saveSite(String url,String regKey,SiteInfo site) throws EdgeServiceFault{
		int index1 = url.indexOf( ":" );
		int index2 = url.lastIndexOf( ":" );
		final String protocol = url.substring( 0, index1 );
		final String portStr = url.substring( index2 + 1, url.length() );
		final String hostname = url.substring( index1+3, index2 );
		
		site.setConsoleHostName(hostname);
		if(protocol.toLowerCase().startsWith("https"))
			site.setConsoleProtocol(2);
		else if(protocol.toLowerCase().startsWith("http"))
			site.setConsoleProtocol(1);		
		site.setConsoleProt(Integer.parseInt(portStr));					
		site.setGatewayProtocol(0);	
		site.setGatewayPort(0);
		site.setGatewayUsername("");
		site.setGatewayPassword("");
		site.setRegistrationText(regKey);		
	
		gatewayBean.updateSite(site.getId(), site);
	}
	
	private long generateLog(Severity severity, String message) {
		ActivityLog log = new ActivityLog();
		log.setModule(Module.SendRegistrationEmails);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(message);		
		try {
			 return logService.addLog(log);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return 0L;
		}
	}

}
