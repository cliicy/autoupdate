package com.ca.arcflash.webservice.util;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.scheduler.VSphereBackupJob;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.VSphereService;

public class VSphereJobQueueEmailAlert {
	
	private static final Logger logger = Logger.getLogger(VSphereJobQueueEmailAlert.class);
	private static final VSphereJobQueueEmailAlert instance = new VSphereJobQueueEmailAlert();
	
	private VSphereJobQueueEmailAlert(){}
	
	public static VSphereJobQueueEmailAlert getInstance(){
		return instance;
	}
	
	public void sendEmailOnJobQueue(VirtualMachine vm, boolean isMergeJobQueue, String activeLogs, int newBackupType) {
		logger.debug("sendEmail - start");
		try {
			VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if (configuration == null)
				return;

			BackupEmail email = configuration.getEmail();
			if (email == null)
				return;

			if (email.isEnableEmail() && email.isEnableEmailOnJobQueue()) {
				EmailSender emailSender = new EmailSender();
				String jobStatus = getJobStatus(isMergeJobQueue);
				String emailJobStatus = WebServiceMessages.getResource("EmailJobStatus");
				String vmName = configuration.getBackupVM().getVmName();
				String nodeName = configuration.getBackupVM().getVmHostName();
				if(nodeName==null||nodeName.isEmpty()){
					nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
				}
				String vmJobQueueTitle = WebServiceMessages.getResource("vsphereJobQueueAlertSubTitle");
				String emailSubject = WebServiceMessages.getResource("EmailSubject",
							email.getSubject(), vmJobQueueTitle+"-"+emailJobStatus+jobStatus ,vmName, nodeName);

				/** for promoting alert message  to edge server */
				emailSender.setJobStatus( CommonEmailInformation.EVENT_TYPE.VSPHERE_MERGE_JOBQUEUE.getValue());
				emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.VSPHERE.getValue());
				
				
				emailSender.setSubject(emailSubject);

				emailSender.setContent(getEmailContent(isMergeJobQueue, email.isEnableHTMLFormat(), configuration, activeLogs, newBackupType));

				emailSender.setUseSsl(email.isEnableSsl());
				emailSender.setSmptPort(email.getSmtpPort());
				emailSender.setMailPassword(email.getMailPassword());
				emailSender.setMailUser(email.getMailUser());
				emailSender.setUseTls(email.isEnableTls());
				emailSender.setProxyAuth(email.isProxyAuth());
				emailSender.setMailAuth(email.isMailAuth());

				emailSender.setFromAddress(email.getFromAddress());
				emailSender.setRecipients(email.getRecipientsAsArray());
				emailSender.setSMTP(email.getSmtp());
				emailSender.setEnableProxy(email.isEnableProxy());
				emailSender.setProxyAddress(email.getProxyAddress());
				emailSender.setProxyPort(email.getProxyPort());
				emailSender.setProxyUsername(email.getProxyUsername());
				emailSender.setProxyPassword(email.getProxyPassword());
				
				if( isMergeJobQueue ) {
					emailSender.setJobType(JobType.JOBTYPE_VM_MERGE );
				}
				else {
					emailSender.setJobType(JobType.JOBTYPE_VM_BACKUP );
				}
				emailSender.setProtectedNode(vmName);

				emailSender.sendEmail(email.isEnableHTMLFormat());
			}
		} catch (Exception e) {
			logger.error("Error in sending email", e);
		}

	}
	
	private  String getJobStatus(boolean isMergeJobQueue){
		if(isMergeJobQueue){
			return WebServiceMessages.getResource("vsphereJobQueueMergeAlertStatus");
		}
		else{
			return WebServiceMessages.getResource("vsphereJobQueueSkipAlertStatus");
		}
	}
	
	private String getEmailContent(boolean isMergeJobQueue, boolean isEnableHtml, VMBackupConfiguration configuration, 
			String activeLogs, int newBackupType){
		String jobTypeString = String.format(WebServiceMessages.getResource("vsphereJobQueueAlert"), ServiceUtils.backupType2String(newBackupType));
		String URL = VSphereBackupJob.getEdgeUrl();
		Date startTime = new Date();
		String executionTime = EmailContentTemplate.formatDate(startTime);
		if(isEnableHtml){
			return getVSphereJobQueueHtmlContent(jobTypeString,getJobStatus(isMergeJobQueue), executionTime, configuration, activeLogs,URL);
		}
		else{
			return getVSphereJobQueuePlainTextContent(jobTypeString,getJobStatus(isMergeJobQueue), executionTime, configuration, activeLogs,URL);
		}
	}
	
	private  String getVSphereJobQueueHtmlContent(String jobTypeString,
			String jobStatusString,
			String executionTime,
			VMBackupConfiguration configuration,
			String activeLogs,
			String URL)
	{
		String template = "";
		String destination = configuration.getBackupVM().getDestination();
		try
		{
			logger.debug("getHtmlContent - start");
			
			//HTML format
			//String destinationHead = null;
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			
			StringBuffer htmlTemplate = new StringBuffer();
			htmlTemplate.append("<HTML>");
			htmlTemplate.append(EmailContentTemplate.getHTMLHeaderSection());
			htmlTemplate.append("	<BODY>");
			htmlTemplate.append("	<h1>%s</h1>");
			htmlTemplate.append("   <p/><p/>");
			htmlTemplate.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			
			
			if(!EmailContentTemplate.isRemote(destination)){
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");				
			}else{
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href=\"file://%s\" target=\"_blank\">%s</a></TD></TR>");
			}
			
			htmlTemplate.append("	</TABLE>");
			
			htmlTemplate.append("<P/><P/>%s");
			htmlTemplate.append("</BODY>");
			htmlTemplate.append("</HTML>");
			
			String clickHere = null;
			String nodeName = configuration.getBackupVM().getVmHostName();
			if(nodeName == null || nodeName.isEmpty()){
				nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
			}
					
			if(!EmailContentTemplate.isRemote(destination)){
				clickHere = WebServiceMessages.getResource("clickhere",URL);
				template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
							WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
							WebServiceMessages.getResource("EmailNodeName"), nodeName, 
							WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							WebServiceMessages.getResource("vsphereJobQueueActiveLogs"), activeLogs, 
							WebServiceMessages.getResource("EmailDestination"), destination,
							clickHere);
				
			}else{//network path
				clickHere = WebServiceMessages.getResource("clickhere",URL);
				template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
							WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
							WebServiceMessages.getResource("EmailNodeName"), nodeName,		
							WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							WebServiceMessages.getResource("vsphereJobQueueActiveLogs"), activeLogs, 
							WebServiceMessages.getResource("EmailDestination"), destination,destination,
							clickHere);
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		logger.debug(template);
		logger.debug("getHtmlContent - end");
		return template;
	}
	
	
	private String getVSphereJobQueuePlainTextContent(String jobTypeString,
			String jobStatusString,
			String executionTime,
			VMBackupConfiguration configuration,
			String activeLogs,
			String URL)
	{
		logger.debug("getPlainTextContent - start");
		String template = "";
		try {
			
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			String nodeName = configuration.getBackupVM().getVmHostName();
			if(nodeName == null || nodeName.isEmpty()){
				nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
			}
			StringBuffer plainTemplate = new StringBuffer();
			
			plainTemplate.append(WebServiceMessages.getResource("EmailVMName"));
			plainTemplate.append(configuration.getBackupVM().getVmName());
			plainTemplate.append("   |   ");

			plainTemplate.append(WebServiceMessages.getResource("EmailNodeName"));
			plainTemplate.append(nodeName);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("VSphereEmailServerName"));
			plainTemplate.append(serverName);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailJobStatus"));
			plainTemplate.append(jobStatusString);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailJobType"));
			plainTemplate.append(jobTypeString);
			plainTemplate.append("   |   ");

			plainTemplate.append(WebServiceMessages.getResource("EmailExecutionTime"));
			plainTemplate.append(executionTime);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailDestination"));
			plainTemplate.append(configuration.getBackupVM().getDestination());
			
			plainTemplate.append("\n\n");
			plainTemplate.append(WebServiceMessages.getResource("vsphereJobQueueActiveLogs"));
			plainTemplate.append("\n");
			plainTemplate.append(activeLogs);
			
			//Alert Email PR
			plainTemplate.append("\n\n");
			plainTemplate.append(WebServiceMessages.getResource("clickhere_text",URL));
			
			template = plainTemplate.toString();
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		logger.debug(template);
		logger.debug("getPlainTextContent - end");
		return template;
	}
	
}
