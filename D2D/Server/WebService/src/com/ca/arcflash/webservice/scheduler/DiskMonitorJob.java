package com.ca.arcflash.webservice.scheduler;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.DiskMonitorService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.ThreshHoldEmailContentTemplate;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class DiskMonitorJob implements Job {

	private static final Logger logger = Logger
			.getLogger(DiskMonitorService.class);
	private BackupConfiguration backupConfig;
	private PreferencesConfiguration preferencesConfig;
	private BackupInformationSummary  backupSummary;
	private boolean ALLOW_SEND_EMAIL;
	private boolean HAS_SENT_EMAIL;

	public DiskMonitorJob() {
		try {
			backupConfig = BackupService.getInstance().getBackupConfiguration();
			backupSummary = BackupService.getInstance().getBackupInformationSummary();
			preferencesConfig = CommonService.getInstance().getPreferences();
			if(backupConfig != null) {
				ALLOW_SEND_EMAIL = backupConfig.isAllowSendEmail();
				HAS_SENT_EMAIL = backupConfig.isHasSendEmail();
			}
		} catch (ServiceException e) {
			logger.error("DiskMonitorJob constructor---- Fail----"
					+ e.getMessage());
		}
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		logger.debug("DiskMonitorJob-------start");
		
		// If enableSpaceNotification is off. Do not set send email flag.
		if (preferencesConfig == null || !preferencesConfig.getEmailAlerts().isEnableSettings() || !preferencesConfig.getEmailAlerts().isEnableSpaceNotification()){
			return;
		}

		detectDiskSpace();
		
		if(backupSummary.getErrorCode() == 0){
			if (ALLOW_SEND_EMAIL && !HAS_SENT_EMAIL ) {
				sendEmail();
			}
		}else{
			logger.debug("Do not send volume alert email, because failed to get backup summary info from backend!");
		}

		try {
			BackupService.getInstance().saveHasSendEmail(HAS_SENT_EMAIL, ALLOW_SEND_EMAIL);
		} catch (Exception e) {
			logger.error("DiskMonitorJob save backup-------Fail--- " + e.getMessage());
		}

		logger.debug("DiskMonitorJob-------stop");
	}

	private void detectDiskSpace() {
		
		logger.debug("DiskMonitorJob----detectDiskSpace-----start");
		try {
			DestinationCapacity diskCapacity = BackupService.getInstance()
					.getBackupInformationSummary().getDestinationCapacity();
			long totalValumeSize = diskCapacity.getTotalVolumeSize();
			long totalFreeSize = diskCapacity.getTotalFreeSize();

			// If enableSpaceNotification is on and free space unit is MB
			if ("MB".equals(preferencesConfig.getEmailAlerts().getSpaceMeasureUnit())) {
				// convert MB to bytes; 1024 * 1024 = pow(2,20)
				double measureSpaceNum = preferencesConfig.getEmailAlerts().getSpaceMeasureNum();
				if ((totalFreeSize/1024/1024) <= measureSpaceNum) {
					ALLOW_SEND_EMAIL = true;
				} else {
					ALLOW_SEND_EMAIL = false;
					HAS_SENT_EMAIL = false;
				}
			}
			// If enableSpaceNotification is on and free space unit is %
			if ("%".equals(preferencesConfig.getEmailAlerts().getSpaceMeasureUnit())) {
				// convert MB to %
				double totalFreeSizeInPercentage = ((double) totalFreeSize)
						/ totalValumeSize *100;
				if (totalFreeSizeInPercentage <= preferencesConfig.getEmailAlerts()
						.getSpaceMeasureNum()) {
					ALLOW_SEND_EMAIL = true;
				} else {
					ALLOW_SEND_EMAIL = false;
					HAS_SENT_EMAIL = false;
				}
			}
		} catch (ServiceException e) {
			logger.error("detectDiskSpace--------Fail" + e.getMessage());
		}
	}

	private void sendEmail() {
		logger.debug("DiskMonitorJob sendEmail ------ start");
		try {

			EmailSender emailSender = new EmailSender();
			BackupEmail email = preferencesConfig.getEmailAlerts();
			
			String hostName = ServiceContext.getInstance().getLocalMachineName();
			String subject = email.getSubject() + "-" + WebServiceMessages.getResource("ThreshHoldSuffixName")+"("+hostName+")";;
			logger.debug("subject:" + subject);
			emailSender.setSubject(subject);

			DestinationCapacity capacity;

			capacity = BackupService.getInstance()
					.getBackupInformationSummary().getDestinationCapacity();
			
			String url = BaseJob.getBackupSettingsURL();
			if(email.isEnableHTMLFormat()){
				emailSender.setContent(ThreshHoldEmailContentTemplate
										    .getHtmlContent(capacity.getTotalFreeSize(), 
													    capacity.getTotalVolumeSize(), 
													    preferencesConfig.getEmailAlerts().getSpaceMeasureNum(), 
													    new Date(), url));
			}
			else
			{
				emailSender.setContent(ThreshHoldEmailContentTemplate
											.getPlainTextContent(capacity.getTotalFreeSize(), 
																 capacity.getTotalVolumeSize(), 
																 preferencesConfig.getEmailAlerts().getSpaceMeasureNum(), 
																 new Date(), url));
			}
			
			/** for promoting alert message  to edge server */
			emailSender.setJobStatus(CommonEmailInformation.EVENT_TYPE.DISK_ALERT.getValue());
			emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue());
			

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
			
			emailSender.sendEmail(email.isEnableHTMLFormat());

			HAS_SENT_EMAIL = true;
		} catch(Throwable e) {
			logger.error("sendEmail-----Fail-----" + e.getMessage(),e);
		}

	}

}
