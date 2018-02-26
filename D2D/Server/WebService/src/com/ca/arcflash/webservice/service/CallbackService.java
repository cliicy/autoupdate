package com.ca.arcflash.webservice.service;


import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.callback.MergeFailureInfo;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.edge.licensing.LicenseUtils;
import com.ca.arcflash.webservice.jni.model.JJobContext;
import com.ca.arcflash.webservice.scheduler.BaseJob;
import com.ca.arcflash.webservice.scheduler.BaseVSphereJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.internal.VSphereConverter;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.WebServiceMessages;


public class CallbackService extends BaseService
{
	private static final Logger logger = Logger.getLogger(CallbackService.class);
	private static final CallbackService instance = new CallbackService();
	
	private MergeFailureInfo cachedMergeFailureInfo;
	private Timer cleanupTimer;
	private VSphereConverter converter = new VSphereConverter();
	
	private CallbackService(){
	}

	public static CallbackService getInstance(){
		return instance;
	}
	
	public void startD2dCallback()
	{
		try
		{
			logger.info("Starting D2D callback...");
			
			Thread callbackStartThread = new Thread("D2DCallbackStartThread")
			{
				@Override
				public void run()
				{
					try
					{
						try {
							LicenseUtils licenseUtils = new LicenseUtils();
							licenseUtils.isManagedByEdge();
						} catch (Throwable t) {
							logger.error("Call license utils failed.", t);
						}
						long result = getNativeFacade().startD2dCallback();
						logger.info("Start D2D callback. ret=" + result);
					}
					catch (Exception e)
					{
						logger.error(e);
					}
				}
			};
			callbackStartThread.setDaemon(true);
			callbackStartThread.start();
			//logger.info("Start D2D callback succeeded");
		}
		catch (Exception e)
		{
			logger.error("Failed to start D2D callback. error:" + e.getMessage(), e);
		}
		
		
	}

	public void stopD2dCallback()
	{
		try
		{
			logger.info("Stopping D2D callback...");
			long result = getNativeFacade().stopD2dCallback();
			logger.info("Stop D2D callback. ret= " + result);
		}
		catch (ServiceException e)
		{
			logger.error("Failed to stop D2D callback. error: " + e.getMessage(), e);
		}
		catch (Exception e)
		{
			logger.error("Failed to stop D2D callback. error: " + e.getMessage(), e);
		}
	}
	
	public void notifyMergeFailure(final MergeFailureInfo info)
	{
		// cache status
		cacheMergeFailureInfo(info);
		
		// send mail
		Thread sendMergeFailureEmailThread = new Thread("SendMergeFailureEmailThread")
		{
			@Override
			public void run()
			{
				try
				{
					sendEmailOnMergeFailure(info);
				}
				catch (Exception e)
				{
					logger.error(e);
				}
			}
		};
		
		sendMergeFailureEmailThread.start();		
	}
	
	protected void cacheMergeFailureInfo(MergeFailureInfo info)
	{
		cachedMergeFailureInfo = info;
		logger.debug("merge failure info cached. MergeFailureInfo=" + info);
		
		if (cleanupTimer != null)
		{
			cleanupTimer.cancel();
			cleanupTimer = null;
		}
		
		cleanupTimer = new Timer();
		cleanupTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				if (cachedMergeFailureInfo != null)
				{
					cachedMergeFailureInfo = null;
					logger.debug("merge failure info removed from cacheMergeFailureInfo=" + cachedMergeFailureInfo);
				}
				
				this.cancel();
			}
			
		}, 5000, 5000);
		
		logger.debug("cleanup timer scheduled. MergeFailureInfo=" + info);
	}
	
	private void sendEmailOnMergeFailure(MergeFailureInfo info)
	{
		if (info != null)
		{
			switch ((int)info.getMergeSource())
			{
			case MergeFailureInfo.MERGE_SOURCE_BACKUP:
			case MergeFailureInfo.MERGE_SOURCE_CATALOG:
				{
					sendEmailOnMergeFailure_D2D(info);
					break;
				}
			case MergeFailureInfo.MERGE_SOURCE_BACKUP_VM:
			case MergeFailureInfo.MERGE_SOURCE_CATALOG_VM:
				{
					sendEmailOnMergeFailure_VM(info);
					break;
				}
			}
		}
	}
	
	private ActivityLogResult getActivityLogResult(long jobid)
	{
		try
		{
			logger.debug("getActivityLogResult - jobid = " + jobid);
			return CommonService.getInstance().getJobActivityLogs(jobid, 0, 512);
		}
		catch (Exception e)
		{

			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return null;
	}

	private ActivityLogResult getActivityLogResult(long jobid, VirtualMachine vm)
	{
		try
		{
			logger.debug("getActivityLogResult - jobid = " + jobid);
			return VSphereService.getInstance().getVMJobActivityLogs(jobid, 0, 512, vm);
		}
		catch (Exception e)
		{

			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return null;
	}
	
	protected void sendEmailOnMergeFailure_D2D(MergeFailureInfo info)
	{
		PreferencesConfiguration preference = null;
		try
		{
			preference = CommonService.getInstance().getPreferences();

			if (preference != null && preference.getEmailAlerts() != null && preference.getEmailAlerts().isEnableSettings()
					&& preference.getEmailAlerts().isEnableEmailOnMergeFailure())
			{
				logger.debug("sendEmail - start");
				try
				{
					BackupEmail email = preference.getEmailAlerts();

					EmailSender emailSender = new EmailSender();
					emailSender.setHighPriority(true);					
					emailSender.setSubject(getEmailSubject(email.getSubject(), info));
					
					/** for promoting alert message to edge server */
					// Use "Missed Jobs" as a quick fix for Central Reports to display alert type. (Should define a new type to describe this alert later) 
					emailSender.setJobStatus(Constants.JOBSTATUS_MISSED); 
					emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue());
					
					// email content
					String destination =  getDestination();
					//String executionTime =  EmailContentTemplate.formatDate(new Date());
					String url = BaseJob.getBackupSettingsURL();;
					boolean enableHtml = email.isEnableHTMLFormat();
					ActivityLogResult logs = getActivityLogResult(info.getJobID());
					
					emailSender.setContent(EmailContentTemplate.getContentOfMergeFailure(info, destination, "", logs, url, enableHtml));

					// email settings
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

					emailSender.setJobType(JobType.JOBTYPE_MERGE);
					emailSender.sendEmail(email.isEnableHTMLFormat());

					logger.info("Email alert on merge failure is sent.");
				}
				catch (Throwable e)
				{
					logger.error("Error in sending email on merge failure", e);
				}
			}
			else
			{
				logger.info("Email alert on merge failure is not sent since the alert is not enabled.");
			}
		}
		catch (ServiceException e)
		{
			logger.error("Failed to get email alert setting on merge failure.");
			
		}
	}
	
	protected void sendEmailOnMergeFailure_VM(MergeFailureInfo info)
	{
		VMBackupConfiguration configuration = null;
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(info.getVmInstanceUUID());
				
		try
		{
			configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if (configuration == null)
			{
				logger.error("Cannot get VMBackupConfiguration. VmInstanceUUID=" + info.getVmInstanceUUID());
				return;
			}

			if (configuration.getEmail() != null && configuration.getEmail().isEnableEmailOnMergeFailure())
			{
				BackupEmail email = configuration.getEmail();
				
				if (email.isEnableEmailOnMergeFailure())
				{
					// try to get edge url
					String url = BaseVSphereJob.getEdgeUrl();

					EmailSender emailSender = new EmailSender();
					
					emailSender.setHighPriority(true);
					/** for promoting alert message to edge server */
					// Use "Missed Jobs" as a quick fix for Central Reports to display alert type. (Should define a new type to describe this alert later) 
					emailSender.setJobStatus(Constants.JOBSTATUS_MISSED); 
					emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.VSPHERE.getValue());
					
					String vmName = configuration.getBackupVM().getVmName();
					String nodeName = configuration.getBackupVM().getVmHostName();
					if (nodeName == null || nodeName.isEmpty())
					{
						nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
					}

					emailSender.setSubject(getEmailSubject_VM(email.getSubject(), info, vmName, nodeName));

					
					// email content
					boolean enableHtml = email.isEnableHTMLFormat();
					ActivityLogResult logs = getActivityLogResult(info.getJobID(), vm);					
					
					emailSender.setContent(EmailContentTemplate.getContentOfMergeFailureVSphere(info, configuration, logs, url, enableHtml));

					/** Alert email ehan PR */
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

					emailSender.setJobType(JobType.JOBTYPE_VM_MERGE );
					emailSender.setProtectedNode( vmName );
					emailSender.sendEmail(email.isEnableHTMLFormat());
				}
			}
			else
			{
				logger.info("Email alert on merge failure is not sent since the alert is not enabled.");
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to get email alert setting on merge failure.");
		}
	}
	
	private String getDestination()
	{
		BackupConfiguration configuration = null;
		
		try
		{
			configuration = BackupService.getInstance().getBackupConfiguration();
		}
		catch (ServiceException e)
		{
			logger.error("Error in geting BackupConfiguration", e);
		}
		
		if (configuration == null)
		{
			return null;
		}
		else
		{
			return configuration.getDestination();
		}
	}
	
	protected String getEmailSubject(String baseSubject, MergeFailureInfo info)
	{
		String mergeFailed = WebServiceMessages.getResource("mergeFailureEmailSubject");
		
		if (mergeFailed != null && !mergeFailed.isEmpty())
		{
			mergeFailed = MessageFormatEx.format(mergeFailed, info.getFailedStartSession(), info.getFailedEndSession());
		}

		String hostName = ServiceContext.getInstance().getLocalMachineName();
		
		return baseSubject + " - " + mergeFailed + " ("+hostName+")";

	}
	
	protected String getEmailSubject_VM(String baseSubject, MergeFailureInfo info, String vmName, String nodeName)
	{
		String mergeFailed = WebServiceMessages.getResource("mergeFailureEmailSubject");
		
		if (mergeFailed != null && !mergeFailed.isEmpty())
		{
			mergeFailed = MessageFormatEx.format(mergeFailed, info.getFailedStartSession(), info.getFailedEndSession());
		}

		String machines = WebServiceMessages.getResource("mergeFailureEmailSubjectVSphere", vmName, nodeName);
		
		return baseSubject + " - " + mergeFailed + " " +machines;

	}
	
	public MergeFailureInfo getMergeFailureInfo()
	{
		return cachedMergeFailureInfo;		
	}
	
	public long submitVAppChildVMBackup(List<JJobContext> jobs) {
		for(JJobContext job : jobs) {
			VSphereJobContext context = converter.ConvertToVSphereJobContext(job);
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(context.getLauncherInstanceUUID());
			vm.setVmName(context.getVmName());
			try {
				VMBackupConfiguration vmBackupConfig = VSphereService.getInstance().getVMBackupConfiguration(vm);
				if(vmBackupConfig == null) {
					//TODO: call console web service to discover the vCloud Director change
					
					//TODO: call backup function
					
				} else {
					VSphereService.getInstance().backupVM(context.getJobType(),
							VSphereService.getJobName((int)job.getDwJobType(), context.getLauncherInstanceUUID()), 
							vm, true, job.getGeneratedDestination(), context.getJobId());
				}
			} catch (ServiceException e) {
				logger.error("Submit vApp Child VM backup failed. VM name is " + vm.getVmName() + ". " + e.getMessage());
			}
		}	
		return 0;
	}
}
