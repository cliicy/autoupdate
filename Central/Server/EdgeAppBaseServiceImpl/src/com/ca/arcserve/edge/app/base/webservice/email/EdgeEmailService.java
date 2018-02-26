package com.ca.arcserve.edge.app.base.webservice.email;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.ReadOnlyFolderException;
import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.SSLException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTaskFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.configuration.ConfigurationServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.email.EmailTemplateFeature;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.sun.mail.smtp.SMTPAddressFailedException;

public class EdgeEmailService {
	private static EdgeEmailService instance = null;
	private static Logger _log = Logger.getLogger(EdgeEmailService.class);

	public static synchronized EdgeEmailService GetInstance() {
		if (instance == null) {
			instance = new EdgeEmailService();
		}
		return instance;
	}
	
	private EdgeEmailService() {
		EdgeTaskFactory etf = EdgeTaskFactory.getInstance();
		EdgeTask task = new EdgeTask();
		etf.Add(EdgeTaskFactory.EDGE_TASK_EMAIL_SERVICE, task);
		etf.LanuchTask(EdgeTaskFactory.EDGE_TASK_EMAIL_SERVICE);
	}
	
	
	public void SendMail(String host, String subject, EmailServerSetting setting, EmailTemplateSetting emailTemplate, String content) {
		EmailSender sender = new EmailSender();
		sender.setSMTP(setting.getSmtp());
		sender.setSmptPort(setting.getPort());
		if (setting.getAuth_flag() == 1) {
			sender.setMailAuth(true);
			sender.setMailUser(setting.getUser_name());
			sender.setMailPassword(setting.getUser_password());
		}
		
		if (subject != null && subject != "") {
			emailTemplate.setSubject(subject);
		} else {
			emailTemplate.setSubject(emailTemplate.getSubject() + " - " + host);
		}
		
		sender.setSubject(emailTemplate.getSubject());
		sender.setFromAddress(emailTemplate.getFrom_addrs());
		
		String []recipients = emailTemplate.getRecipients().split(";");
		sender.setRecipients(recipients);
		

		boolean isHtml = (emailTemplate.getHtml_flag() == 1);
		EdgeEmailTemplate template = new EdgeEmailTemplate();
		template.setHtmlFlag(isHtml);
		template.setProductUrl(getApplicationUrl());
		template.setContent(content);
		
		sender.setContent(template.getFormattedContent());
		if (setting.getSsl_flag() == 1) {
			sender.setUseSsl(true);
		}
		
		if (setting.getTls_flag() == 1) {
			sender.setUseTls(true);
		}
		
		if (setting.getProxy_flag() == 1) {
			sender.setEnableProxy(true);
			sender.setProxyAddress(setting.getProxy_server());
			
			if (setting.getProxy_auth_flag() == 1) {
				sender.setProxyAuth(true);
				sender.setProxyUsername(setting.getProxy_user_name());
				sender.setProxyPassword(setting.getProxy_user_password());
			}
		}
		
		
		EdgeTaskFactory etf = EdgeTaskFactory.getInstance();
		EdgeTask task = etf.getTask(EdgeTaskFactory.EDGE_TASK_EMAIL_SERVICE);
		if (task != null) {
			SendEmailTask item = new SendEmailTask();
			item.setSendHost(host);
			item.setSender(sender);
			item.setHtmlFlag(isHtml);
			item.setRunningModule(featureIdToModule(emailTemplate.getFeature_Id()));

			try {
				task.AddToWaitingQueue(item);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				_log.error(e.getMessage(), e);
			}
		}
	}
	
	// Send email immediately for report model
	private void sendMailForReport(EmailServerSetting setting,
			String mailSubject, int priority, String mailTo, String mailCC,
			String from, MimeMultipart multipart) throws Exception {
		
		EmailSender sender = new EmailSender();
		sender.setSMTP(setting.getSmtp());
		sender.setSmptPort(setting.getPort());
		if (setting.getAuth_flag() == 1) {
			sender.setMailAuth(true);
			sender.setMailUser(setting.getUser_name());
			sender.setMailPassword(setting.getUser_password());
		}

		sender.setSubject(mailSubject);
		sender.setFromAddress(from);
		sender.setEmailPriority(priority);
		sender.setTurnOnHtmlMode(true);

		if (mailTo != null) {
			String[] recipients = mailTo.split(";");
			sender.setRecipients(recipients);
		} else {
			sender.setRecipients(null);
		}

		if (mailCC != null) {
			String[] recipientsCC = mailCC.split(";");
			sender.setRecipientsCC(recipientsCC);
		} else {
			sender.setRecipientsCC(null);
		}
		sender.setContent("");
		sender.setMultipart(multipart);

		if (setting.getSsl_flag() == 1) {
			sender.setUseSsl(true);
		}

		if (setting.getTls_flag() == 1) {
			sender.setUseTls(true);
		}

		if (setting.getProxy_flag() == 1) {
			sender.setEnableProxy(true);
			sender.setProxyAddress(setting.getProxy_server());
			if (setting.getProxy_auth_flag() == 1) {
				sender.setProxyAuth(true);
				sender.setProxyUsername(setting.getProxy_user_name());
				sender.setProxyPassword(setting.getProxy_user_password());
			}
		}

		sender.sendMail(true);

	}
	
	/**
	 * Send email alert using the global email settings.
	 * @return false if global email setting is not configured. Otherwise, return true.
	 */
	public boolean SendMailWithGlobalSetting(String host, String subject, String content, int emailTemplateId) {
		try{
			ConfigurationServiceImpl configurationServiceImpl = new ConfigurationServiceImpl();
			EmailServerSetting emailServer = configurationServiceImpl.getEmailServerSetting();
			EmailTemplateSetting emailTemplate = configurationServiceImpl.getEmailTemplateSetting(emailTemplateId);
			if ( emailServer != null && emailTemplate != null) {
				SendMail(host, subject, emailServer, emailTemplate, content);
			} else {
				writeEmailActivityLog(getHostName(), 
						EdgeCMWebServiceMessages.getResource("EDGEMAIL_NoConfigure"), 
						Severity.Warning,
						Module.Common, 
						0);
				return false;
			}
		}catch (Exception e) {
			_log.debug( e.getMessage() );
		}
		
		return true;
	}
	
	// Send email for report module (scheduler/UI email) with default email setting
	// the function will add activity log and also throw an exception when error occur  
	public void sendMailForReportEmail(String subject, int priority,
			String mailTo, String mailCC, MimeMultipart multipart, Module module)
			throws EdgeServiceFault {

		_log.info("send report email begin.");
		
		// Get default email setting
		ConfigurationServiceImpl configurationServiceImpl = new ConfigurationServiceImpl();
		EmailServerSetting emailSetting = configurationServiceImpl.getEmailServerSetting();
		EmailTemplateSetting emailTemplate = configurationServiceImpl.getEmailTemplateSetting(
				EmailTemplateFeature.Report);

		if (emailSetting != null && emailTemplate != null) {

			try {
				sendMailForReport(emailSetting, subject, priority, mailTo, mailCC,
						emailTemplate.getFrom_addrs(), multipart);
			} catch (Exception e) {
				_log.error(e);
				if (e instanceof ReadOnlyFolderException) {
					writeEmailActivityLog(getHostName(),
							EdgeCMWebServiceMessages.getResource("Email_FolderError"),
							Severity.Error, module, 0);
					throw EdgeServiceFault.getFault(
							EdgeServiceErrorCode.Email_FolderError,"Folder error.");
				} else if (e instanceof MethodNotSupportedException) {
					writeEmailActivityLog(getHostName(),
							EdgeCMWebServiceMessages.getResource("Email_MethodNotSupport"),
							Severity.Error, module, 0);
					throw EdgeServiceFault.getFault(
							EdgeServiceErrorCode.Email_MethodNotSupport,"Method not support.");
				} else if (e instanceof SMTPAddressFailedException) {
					writeEmailActivityLog(getHostName(),
							EdgeCMWebServiceMessages.getResource("Email_SendFailed_RecipientAddresses"),
							Severity.Error, module, 0);
					throw EdgeServiceFault.getFault(
							EdgeServiceErrorCode.Email_SendFailed_RecipientAddresses,"Send mail failed.");
				} else {
					writeEmailActivityLog(getHostName(),
							EdgeCMWebServiceMessages.getResource("EDGEMAIL_FailedToSend"),
							Severity.Warning, module, 0);
					throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Email_SendFailed,"Send mail failed.");
				}
			}

		} else {
			// No email setting
			_log.warn("email setting not configured .");
			writeEmailActivityLog(getHostName(),
					EdgeCMWebServiceMessages.getResource("EDGEMAIL_NoConfigure"),
					Severity.Warning, module, 0);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Email_SendFailed, "Send mail failed.");
		}
		_log.info("send report email end.");
	}
	
	public boolean sendTestMail(EmailServerSetting serverSetting) throws EdgeServiceFault {
		try {
			EmailSender sender = new EmailSender();
			StringBuilder sb = new StringBuilder(serverSetting.getTemplateSetting().getSubject());
			sb.append(" (");
			sb.append(InetAddress.getLocalHost().getHostName().toUpperCase());
			sb.append(")");
			sender.setSubject(sb.toString());
			sender.setContent(serverSetting.getTemplateSetting().getContent());
			sender.setFromAddress(serverSetting.getTemplateSetting().getFrom_addrs());
			String []recipients = serverSetting.getTemplateSetting().getRecipients().split(";");
			sender.setRecipients(recipients);
			
			sender.setSMTP(serverSetting.getSmtp());
			sender.setSmptPort(serverSetting.getPort());
			if (serverSetting.getAuth_flag() == 1) {
				sender.setMailAuth(true);
				sender.setMailUser(serverSetting.getUser_name());
				sender.setMailPassword(serverSetting.getUser_password());
			}
			
			if (serverSetting.getSsl_flag() == 1) {
				sender.setUseSsl(true);
			}
			
			if (serverSetting.getTls_flag() == 1) {
				sender.setUseTls(true);
			}
			
			if (serverSetting.getProxy_flag() == 1) {
				sender.setEnableProxy(true);
				sender.setProxyAddress(serverSetting.getProxy_server());
				
				if (serverSetting.getProxy_auth_flag() == 1) {
					sender.setProxyAuth(true);
					sender.setProxyUsername(serverSetting.getProxy_user_name());
					sender.setProxyPassword(serverSetting.getProxy_user_password());
				}
			}
	
			sender.sendMail(false);
		}catch(Exception e) {
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean();
			bean.setFaultType(FaultType.Edge);
			EdgeServiceFault esf = new EdgeServiceFault("sendTestMailError", bean, e
					.fillInStackTrace());
			if(e instanceof ReadOnlyFolderException) {
				bean.setCode(EdgeServiceErrorCode.TestMailFolderError);
			}else if(e instanceof AuthenticationFailedException) {
				bean.setCode(EdgeServiceErrorCode.TestMailUserError);
			}else if(e instanceof SendFailedException) {
				StringBuilder message = new StringBuilder();
				SendFailedException se = (SendFailedException)e;
				if(se.getNextException() != null && (se.getNextException() instanceof SMTPAddressFailedException)){
					if(se.getInvalidAddresses() != null
							&& se.getInvalidAddresses().length > 0){						
						for(Address ad : se.getInvalidAddresses()) {
							message.append(ad);
							message.append(",");
						}
						message.deleteCharAt(message.length() - 1);					
					}
					bean.setCode(EdgeServiceErrorCode.TestMailRecError);
					bean.setMessageParameters(new Object[]{message.toString()});
				}
			}else if(e instanceof AddressException) {
				bean.setCode(EdgeServiceErrorCode.TestMailFromError);
			}else if(e instanceof MessagingException) {
				MessagingException me = (MessagingException)e;
				if (me.getNextException() != null) {
					Exception nextException = me.getNextException();
					if (nextException instanceof UnknownHostException) {
						bean.setCode(EdgeServiceErrorCode.TestMailHostError);
						bean.setMessageParameters(new Object[] { serverSetting.getSmtp() });
					} else if (nextException instanceof ConnectException) {
						bean.setCode(EdgeServiceErrorCode.TestMailPortError);
						bean.setMessageParameters(new Object[] { serverSetting.getSmtp(), serverSetting.getPort() });
					} else if (nextException instanceof SocketException) {
						if (serverSetting.getProxy_flag() == 1) {
							bean.setCode(EdgeServiceErrorCode.TestMailProxyError);
							bean.setMessageParameters(new Object[] { serverSetting.getSmtp(), serverSetting.getPort(),
									serverSetting.getProxy_server() });
						} else {
							bean.setCode(EdgeServiceErrorCode.TestMailPortError);
							bean.setMessageParameters(new Object[] { serverSetting.getSmtp(), serverSetting.getPort() });
						}
					} else if (nextException instanceof SSLException){
						bean.setCode(EdgeServiceErrorCode.TestMailSSLError);
						bean.setMessageParameters(new Object[] {serverSetting.getSmtp()});
					}
				}
			}
			
			if(bean.getCode() == null || bean.getCode().isEmpty()) {
				bean.setCode(EdgeServiceErrorCode.TestMailCommonError);
				bean.setMessageParameters(new Object[]{e.getLocalizedMessage()});
			}
			
			throw esf;
		}
		
		return true;
	}
	
	public String getHostName()
	{
		String hostname="";
		try {
		    InetAddress addr = InetAddress.getLocalHost();
		    hostname = addr.getHostName();
		} catch (UnknownHostException e) {

		}
		return hostname.toUpperCase();
	}
	
	public String getApplicationUrl() {
		EdgeApplicationType edgeAppType = EdgeWebServiceContext
				.getApplicationType();
		String edgeProtocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
		if (!edgeProtocol.contains(":")) {
			edgeProtocol += ":";
		}
		
		int edgePort = EdgeCommonUtil.getEdgeWebServicePort();
		String url = edgeProtocol + "//" + getHostName() + ":" + edgePort;
		switch (edgeAppType) {
		case CentralManagement:
			url += CommonUtil.CENTRAL_MANAGER_CONTEXT_PATH + "/";
			break;

		case VirtualConversionManager:
			url += CommonUtil.VCM_CONTEXT_PATH + "/";
			break;

		case vShpereManager:
			url += CommonUtil.VSphere_CONTEXT_PATH + "/";
			break;

		case Report:
			url += CommonUtil.REPORT_CONTEXT_PATH + "/";
			break;
		}

		return url;
	}
	
	public void writeEmailActivityLog(String nodeName, String message, Severity severity, Module module, int jobid){
		IActivityLogService iActivityLog = new ActivityLogServiceImpl();
		ActivityLog log = new ActivityLog();
		log.setModule(module);
		log.setSeverity(severity);
		log.setNodeName(nodeName);
		log.setTime(new Date(System.currentTimeMillis()));
		
		String finalMessage = message;
		switch(module) {
		case ReportEmail:
			finalMessage = String.format(
					message,
					EdgeCMWebServiceMessages.getResource("EDGEMAIL_EMAIL_CONFIGURATION"));
			break;
		
		case PolicyManagement:
		default:
			finalMessage = String.format(
					message,
					EdgeCMWebServiceMessages.getResource("EDGEMAIL_EMAIL_POLICY_CONFIGURATION"));
			break;
		}
		
		log.setMessage(finalMessage);
		log.setJobId(jobid);
		try {
			iActivityLog.addLog(log);
		} catch (EdgeServiceFault e) {
			_log.debug(e.getMessage());
		}
	}
	
	private Module featureIdToModule(int emailTemplateId) {
		switch(emailTemplateId) {
		case EmailTemplateFeature.D2DPolicy:
		case EmailTemplateFeature.VCMPolicy:
		case EmailTemplateFeature.VSpherePolicy:
			return Module.PolicyManagement;
		//case EmailTemplateFeature.Report:
		//	return Module.ReportEmail;
		default:
			return Module.Common;
		}
	}
}
