package com.ca.arcflash.ha.alert;

import com.ca.arcflash.jobscript.alert.AlertCommand;
import com.ca.arcflash.jobscript.alert.AlertJobScript;
import com.ca.arcflash.jobscript.alert.AlertType;
import com.ca.arcflash.jobscript.alert.EmailModel;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class EmailAlertCommand extends AlertCommand{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8324152567509274549L;
	private static final String COLDSTANDBY_ALERTSUBJECT_AUTO_FAILOVER="COLDSTANDBY_ALERTSUBJECT_AUTO_FAILOVER";
	private static final String COLDSTANDBY_ALERTSUBJECT_MANUAL_FAILOVER="COLDSTANDBY_ALERTSUBJECT_MANUAL_FAILOVER";
	private static final String COLDSTANDBY_ALERTSUBJECT_FREE_SPACE="COLDSTANDBY_ALERTSUBJECT_FREE_SPACE";
	private static final String COLDSTANDBY_ALERTSUBJECT_CONVERSION_ERROR="COLDSTANDBY_ALERTSUBJECT_CONVERSION_ERROR";
	private static final String COLDSTANDBY_ALERTSUBJECT_CONVERSION_SUCCESS="COLDSTANDBY_ALERTSUBJECT_CONVERSION_SUCCESS";
	private static final String COLDSTANDBY_ALERTSUBJECT_LICENSE_FIAL="COLDSTANDBY_ALERTSUBJECT_LICENSE_FIAL";
	private static final String COLDSTANDBY_ALERTSUBJECT_FAILED_CONNECT_VIRTUALIZATION_SERVER="COLDSTANDBY_ALERTSUBJECT_FAILED_CONNECT_VIRTUALIZATION_SERVER";
	private static final String COLDSTANDBY_ALERTSUBJECT_MISS_HEATBEAT="COLDSTANDBY_ALERTSUBJECT_MISS_HEATBEAT";
	private static final String COLDSTANDBY_ALERTSUBJECT_FAILOVER_FAILURE="COLDSTANDBY_ALERTSUBJECT_FAILOVER_FAILURE";
	private static final String COLDSTANDBY_ALERTSUBJECT_CONVERSION_CANCEL_JOB="COLDSTANDBY_ALERTSUBJECT_CONVERSION_CANCEL_JOB";
	
	@Override
	public int executeAlert( AlertJobScript alertJobScript, String alertMessage, AlertType alertType ) {
		
		EmailModel email = alertJobScript.getEmailModel();
		if(email == null) {
			return 1;
		}
		FailoverJobScript failoverJobScript = HAService.getInstance().getFailoverJobScript( alertJobScript.getAFGuid() );
		
		EmailSender emailSender = new EmailSender();
		emailSender.setSubject( getAlertSubject( failoverJobScript, email.getSubject(), alertType));
		emailSender.setContent(alertMessage);
		emailSender.setUseSsl(email.isUseSsl());
		emailSender.setSmptPort(email.getSmptPort());
		emailSender.setMailPassword(email.getMailPassword());
		emailSender.setMailUser(email.getMailUser());
		emailSender.setUseTls(email.isUseTls());
		emailSender.setProxyAuth(email.isProxyAuth());
		emailSender.setMailAuth(email.isMailAuth());

		emailSender.setFromAddress(email.getFromAddress());
		emailSender.setRecipients(email.getRecipients());
		emailSender.setSMTP(email.getSMTP());
		emailSender.setEnableProxy(email.isEnableProxy());
		emailSender.setProxyAddress(email.getProxyAddress());
		emailSender.setProxyPort(email.getProxyPort());
		emailSender.setProxyUsername(email.getProxyUsername());
		emailSender.setProxyPassword(email.getProxyPassword());

        emailSender.setJobStatus(getAlertEventCode(alertType));
        
        emailSender.setJobType(JobType.JOBTYPE_CONVERSION);
        if(  failoverJobScript != null) {
        	emailSender.setProtectedNode( failoverJobScript.getProductionServerName() );
        }
        
        if(isHighPriority(alertType)){
        	emailSender.setHighPriority(true);
        }
        emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.VCM.getValue());

		emailSender.sendEmail(email.isHtmlFormat());
		
		return 0;
	}
	
	private boolean isHighPriority(AlertType type) {
		boolean ret = false;
		switch(type) {
		case ConversionFailed:
		case FailoverFailure:
		case LicenseFailed:
		case VMHostNotReachable:{
			ret = true;			
			break;
		}
		default:
			ret = false;
		}
		
		return ret;
	}
	
	private long getAlertEventCode(AlertType alertType) {
		long vcmAlertCode = 0;
		if(alertType == AlertType.AutoFaiover) {
			vcmAlertCode = CommonEmailInformation.EVENT_TYPE.VCM_AUTOFAIOVER.getValue();
		}
		else if((alertType == AlertType.ConversionFailed)||(alertType == AlertType.CancelConversionJob)) {
			vcmAlertCode = CommonEmailInformation.EVENT_TYPE.VCM_CONVERSIONFAILED.getValue();
		}
		else if(alertType == AlertType.LicenseFailed) {
			vcmAlertCode = CommonEmailInformation.EVENT_TYPE.VCM_LICENSEFAILED.getValue();
		}
		else if(alertType == AlertType.MaualFaiover) {
			vcmAlertCode = CommonEmailInformation.EVENT_TYPE.VCM_MAUALFAIOVER.getValue();
		}
		else if(alertType == AlertType.MissHeatBeat) {
			vcmAlertCode = CommonEmailInformation.EVENT_TYPE.VCM_MISSHEATBEAT.getValue();
		}
		else if(alertType == AlertType.ReplicationSpaceWarning) {
			vcmAlertCode = CommonEmailInformation.EVENT_TYPE.VCM_REPLICATIONSPACEWARNING.getValue();
		}
		else if(alertType == AlertType.VMHostNotReachable) {
			vcmAlertCode = CommonEmailInformation.EVENT_TYPE.VCM_VMHOSTNOTREACHABLE.getValue();
		}
		else if(alertType == AlertType.ConversionSuccess){
			vcmAlertCode = CommonEmailInformation.EVENT_TYPE.VCM_CONVERSIONSUCCESS.getValue();
		}
		else if(alertType == AlertType.FailoverFailure){
			vcmAlertCode = CommonEmailInformation.EVENT_TYPE.VCM_FAILOVERFAILED.getValue();
		}
		
		return vcmAlertCode;
	}
	
	private String getAlertSubject(FailoverJobScript failoverJobScript,String subject, AlertType alertType) {
		String moniteeHostName = "";
	
		if(failoverJobScript==null) {
			return subject;
		}
		
		moniteeHostName = failoverJobScript.getProductionServerName();
		String alertSubject = "";
		
		if(alertType == AlertType.AutoFaiover) {
			alertSubject = WebServiceMessages.getResource(COLDSTANDBY_ALERTSUBJECT_AUTO_FAILOVER, subject, moniteeHostName);
		}
		else if(alertType == AlertType.ConversionFailed) {
			alertSubject = WebServiceMessages.getResource(COLDSTANDBY_ALERTSUBJECT_CONVERSION_ERROR, subject, moniteeHostName);
		}
		else if(alertType == AlertType.LicenseFailed) {
			alertSubject = WebServiceMessages.getResource(COLDSTANDBY_ALERTSUBJECT_LICENSE_FIAL, subject, moniteeHostName);
		}
		else if(alertType == AlertType.MaualFaiover) {
			alertSubject = WebServiceMessages.getResource(COLDSTANDBY_ALERTSUBJECT_MANUAL_FAILOVER, subject, moniteeHostName);
		}
		else if(alertType == AlertType.MissHeatBeat) {
			alertSubject = WebServiceMessages.getResource(COLDSTANDBY_ALERTSUBJECT_MISS_HEATBEAT, subject, moniteeHostName);
		}
		else if(alertType == AlertType.ReplicationSpaceWarning) {
			alertSubject = WebServiceMessages.getResource(COLDSTANDBY_ALERTSUBJECT_FREE_SPACE, subject, moniteeHostName);
		}
		else if(alertType == AlertType.VMHostNotReachable) {
			alertSubject = WebServiceMessages.getResource(COLDSTANDBY_ALERTSUBJECT_FAILED_CONNECT_VIRTUALIZATION_SERVER, subject, moniteeHostName);
		}
		else if(alertType == AlertType.ConversionSuccess){
			alertSubject = WebServiceMessages.getResource(COLDSTANDBY_ALERTSUBJECT_CONVERSION_SUCCESS, subject, moniteeHostName);
		}
		else if(alertType == AlertType.FailoverFailure){
			alertSubject = WebServiceMessages.getResource(COLDSTANDBY_ALERTSUBJECT_FAILOVER_FAILURE, subject, moniteeHostName);
		}
		else if(alertType == AlertType.CancelConversionJob){
			alertSubject = WebServiceMessages.getResource(COLDSTANDBY_ALERTSUBJECT_CONVERSION_CANCEL_JOB, subject, moniteeHostName);
		}
		else {
			alertSubject = subject;
		}
		
		return alertSubject;
	}
	
}
