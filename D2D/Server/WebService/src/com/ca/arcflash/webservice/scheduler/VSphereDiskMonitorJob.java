package com.ca.arcflash.webservice.scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.ThreshHoldEmailContentTemplate;
import com.ca.arcflash.webservice.util.WebServiceMessages;


public class VSphereDiskMonitorJob implements Job{
	
	private static final Logger logger = Logger
	.getLogger(VSphereDiskMonitorJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		logger.debug("VSphereDiskMonitorJob-------start");
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		try{
			VirtualMachine vm = null;
			VMBackupConfiguration configuration = null;
			//BackupInformationSummary backupSummary = null;
			Map<String,List<VMBackupConfiguration>> map = new HashMap<String,List<VMBackupConfiguration>>();
			String instanceUUID = null;
			for (File one : files) {
				String filename = one.getName();
				if(filename.length() != 40){
					continue;
				}
				vm = new VirtualMachine();
				configuration = new VMBackupConfiguration();
				
				instanceUUID = new String();
				instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
				vm.setVmInstanceUUID(instanceUUID);
				configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
				//backupSummary = VSphereService.getInstance().getBackupInformationSummary(vm);
				if(configuration !=null){
					if (configuration.getGenerateType() == GenerateType.MSPManualConversion) {
						logger.info("The configuration is for remote nodes.");
						continue;
					}
					if(configuration.isEnableSpaceNotification()){
						//detectDiskSpace(configuration,backupSummary);
						addToEmailGroup(map,getKeyFromConfiguration(configuration),configuration);
					}
				}
			}
			detectDiskSpaceForGourp(map);
		}catch(Exception se){
			logger.error("Failed to VSphereDiskMonitorJob",se);
		}
		
		logger.debug("VSphereDiskMonitorJob-------stop");
		
	}
	/**
	 * key format:
	 * destination-email(info)-trigger condition
	 * @param configuration
	 * @return
	 */
	private String getKeyFromConfiguration(VMBackupConfiguration configuration){
		BackupEmail email = configuration.getEmail();
		StringBuffer key = new StringBuffer();
		String destination = configuration.getBackupVM().getDestination();
		key.append(destination.substring(0, destination.lastIndexOf("\\")));
		key.append("-"+email.getFromAddress()+"-"+email.getSmtp()+"-"+email.getSmtpPort()+"-"+email.isEnableHTMLFormat()+"-"+email.isEnableSsl()+"-"+email.isEnableTls()+"-"+email.getSubject());
		for(String receiver : email.getRecipientsAsArray()){
			key.append("-"+receiver);
		}
		key.append("-"+configuration.getSpaceMeasureUnit()+"-"+configuration.getSpaceMeasureNum());
		return key.toString();
	}
	/**
	 * add configuration to email group which can be sent as one email
	 * condition: 
	 * 1. same destination
	 * 2. same email
	 * 3. same trigger condition	
	 * @param map
	 * @return
	 */
	private void addToEmailGroup(Map<String,List<VMBackupConfiguration>> map,String key,VMBackupConfiguration configuration){
		List<VMBackupConfiguration> configList = map.get(key);
		if(configList == null){
			configList = new ArrayList<VMBackupConfiguration>();
		}
		configList.add(configuration);
		map.put(key, configList);
	}
	
	private void detectDiskSpaceForGourp(Map<String,List<VMBackupConfiguration>> map){
		for(List<VMBackupConfiguration> configList : map.values()){
			VMBackupConfiguration configuration = configList.get(0);
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(configuration.getBackupVM().getInstanceUUID());
			try {
				BackupInformationSummary backupSummary = VSphereService.getInstance().getBackupInformationSummary(vm);
				detectDiskSpace(configuration,backupSummary,configList);
			} catch (ServiceException e) {
				logger.debug("failed to getBackupInformationSummary" + e);
			}
		}
	}
	
	private void detectDiskSpace(VMBackupConfiguration configuration,BackupInformationSummary backupSummary,List<VMBackupConfiguration> configList) {
		boolean ALLOW_SEND_EMAIL = false;
		boolean HAS_SENT_EMAIL = false;
		logger.debug("VSphereDiskMonitorJob----detectDiskSpace-----start");
		try {
			DestinationCapacity diskCapacity = backupSummary.getDestinationCapacity();
			long totalValumeSize = diskCapacity.getTotalVolumeSize();
			long totalFreeSize = diskCapacity.getTotalFreeSize();

			// If enableSpaceNotification is on and free space unit is MB
			if ("MB".equals(configuration.getSpaceMeasureUnit())) {
				// convert MB to bytes; 1024 * 1024 = pow(2,20)
				double measureSpaceNum = configuration.getSpaceMeasureNum();
				if ((totalFreeSize/1024/1024) <= measureSpaceNum) {
					ALLOW_SEND_EMAIL = true;
				} else {
					ALLOW_SEND_EMAIL = false;
					HAS_SENT_EMAIL = false;
				}
			}
			// If enableSpaceNotification is on and free space unit is %
			if ("%".equals(configuration.getSpaceMeasureUnit())) {
				// convert MB to %
				double totalFreeSizeInPercentage = ((double) totalFreeSize)
						/ totalValumeSize *100;
				if (totalFreeSizeInPercentage <= configuration.getSpaceMeasureNum()) {
					ALLOW_SEND_EMAIL = true;
				} else {
					ALLOW_SEND_EMAIL = false;
					HAS_SENT_EMAIL = false;
				}
			}
			if(backupSummary.getErrorCode() == 0){
				if (ALLOW_SEND_EMAIL && !HAS_SENT_EMAIL ) {
					sendEmail(configuration,backupSummary,configList);
					HAS_SENT_EMAIL = true;
				}
			}else{
				logger.debug("Do not send volume alert email, because failed to get backup summary info from backend!");
			}
			
		} catch (Exception e) {
			logger.error("detectDiskSpace--------Fail" + e.getMessage());
		}
		
		for(VMBackupConfiguration config : configList){
			try {
				VSphereService.getInstance().saveHasSendEmail(HAS_SENT_EMAIL, ALLOW_SEND_EMAIL,config.getBackupVM(),config);
			} catch (Exception e) {
				logger.error("VSphereDiskMonitorJob save backup-------Fail--- " + e.getMessage());
			}
		}
	}
	
	private void sendEmail(VMBackupConfiguration configuration,BackupInformationSummary backupSummary,List<VMBackupConfiguration> configList) {
		logger.debug("VSphereDiskMonitorJob sendEmail ------ start");
		try {

			EmailSender emailSender = new EmailSender();
			BackupEmail email = configuration.getEmail();
			
			String hostName = ServiceContext.getInstance().getLocalMachineName();
			String subject = email.getSubject() + "-" + WebServiceMessages.getResource("ThreshHoldSuffixName")+"("+hostName+")";;
			logger.debug("subject:" + subject);
			emailSender.setSubject(subject);

			DestinationCapacity capacity;

			capacity = backupSummary.getDestinationCapacity();
			
			String url = BaseJob.getBackupSettingsURL();
			if(email.isEnableHTMLFormat()){
				emailSender.setContent(ThreshHoldEmailContentTemplate
										    .getVSphereHtmlContent(configuration,capacity.getTotalFreeSize(), 
													    capacity.getTotalVolumeSize(), 
													    configuration.getSpaceMeasureNum(), 
													    new Date(), url,configList));
			}
			else
			{
				emailSender.setContent(ThreshHoldEmailContentTemplate
											.getVSpherePlainTextContent(configuration,capacity.getTotalFreeSize(), 
																 capacity.getTotalVolumeSize(), 
																 configuration.getSpaceMeasureNum(), 
																 new Date(), url,configList));
			}
			
			/** for promoting alert message  to edge server */
			emailSender.setJobStatus(CommonEmailInformation.EVENT_TYPE.DISK_ALERT.getValue());
			emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.VSPHERE.getValue());
			

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
			
			emailSender.setProtectedNode(configuration.getBackupVM().getVmName() );
			emailSender.sendEmail(email.isEnableHTMLFormat());
		} catch(Throwable e) {
			logger.error("VSphereDiskMonitorJobSendMail-----Fail-----" + e.getMessage(),e);
		}

	}

}
