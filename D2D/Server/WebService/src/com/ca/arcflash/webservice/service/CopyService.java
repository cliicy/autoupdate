package com.ca.arcflash.webservice.service;

import java.util.Date;
import java.util.Observable;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.jni.model.JProtectionInfo;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.ProtectionInformation;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.job.rps.CopyJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;
import com.ca.arcflash.webservice.data.restore.CopyJob;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.scheduler.BaseBackupJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.service.internal.ScheduledExportConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.validator.ScheduledExportConfigurationValidator;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class CopyService extends BaseService implements IJobDependency{
	Logger logger = Logger.getLogger(CopyService.class);
//	private CopyJobValidator copyJobValidator = new CopyJobValidator();
	private ScheduledExportConfigurationValidator scheduledExportConfigValidator = new ScheduledExportConfigurationValidator();
	
	private static final CopyService INSTANCE = new CopyService();
	
	private ScheduledExportConfiguration scheduledExportConfiguration = null;
	private ScheduledExportConfigurationXMLDAO scheduledExportConfigurationXMLDAO = new ScheduledExportConfigurationXMLDAO();
	
	private boolean submitCopy = false;
	
	private CopyService() {}
	
	public static CopyService getInstance() {
		return INSTANCE;
	}
	
//	public void submitCopyJob(CopyJob job) throws ServiceException {
//		logger.debug("submitCopyJob - start");
//		printCopyJob(job);
//		
//		try{
//			//The following two lines's position cannot be exchanged because the validateDestPath will establish
//			//a connection on which appendHostNameIfNeeded depends.   
//			copyJobValidator.validate(job);
//			copyJobValidator.validateDestPath(job);
//
//		} catch (ServiceException e) {
//			logger.debug("submitCopyJob Validation Failed:" + e.getErrorCode());
//			throw e;
//		}
//		
//		//check whether there is running jobs
//		Scheduler scheduler = BackupService.getInstance().getScheduler();
//		if (scheduler==null)
//			return;
//			
//		if (com.ca.arcflash.webservice.scheduler.CopyJob.isJobRunning()
//				|| this.getNativeFacade().checkJobExist()){
//			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
//		}
//		
//		checkSameSourceDestPath(job);
//			
//		String sourceServer = getLastFolderName(job.getSessionPath());
//		logger.debug("Server name(folder name) :" + sourceServer);
//		if(sourceServer == null || sourceServer.length() == 0)
//			throw new ServiceException(FlashServiceErrorCode.CopyJob_InvalidSessionPath);
//		
//		String username = job.getDestinationUserName();
//		String password = job.getDestinationPassword();
//		
//		if (username == null)
//			username = "";
//		if (password == null)
//			password = "";
//		
//		String destPath = BackupService.getInstance().appendHostNameIfNeeded(job.getDestinationPath(), sourceServer, username, password, 1);
//		job.setDestinationPath(destPath);
//		
//		checkSameSourceDestPath(job);
//		
//		
//		
//		logger.debug("username" + username);
//		String domain = "";
//		int indx = username.indexOf('\\');
//		if (indx > 0 && indx < username.length() - 1) {
//			domain = username.substring(0, indx);
//			username = username.substring(indx + 1);
//		}
//		
//		getNativeFacade().initCopyDestination(destPath, domain, username, password);
//		
//		submitCopyJob(job, scheduler);
//		logger.debug("submitCopyJob - end");
//	}
	
	private void submitCopyJob(CopyJob job, Scheduler scheduler) throws ServiceException {
		try {
			logger.debug("submitCopyJob(CopyJob job, Scheduler scheduler) - start");
			if(scheduler == null) {
				return;
			}
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_COPYJOB,null,com.ca.arcflash.webservice.scheduler.CopyJob.class);
			jobDetail.getJobDataMap().put("Job", job);
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			jobDetail.getJobDataMap().put(RPS_POLICY_UUID, job.getRpsPolicy());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, job.getRpsDataStore());
			jobDetail.getJobDataMap().put(RPS_HOST, job.getRpsHost());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, job.getRpsDataStoreDisplayName());
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(jobDetail.getName()+"Trigger");
			scheduler.scheduleJob(jobDetail, trigger);
			
			logger.debug("submitCopyJob(CopyJob job, Scheduler scheduler) - end");
		} catch (Throwable e) {
			logger.error("submitCopyJob()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
//	private void printCopyJob(CopyJob job) {
//		if (logger.isDebugEnabled()){
//			logger.debug(StringUtil.convertObject2String(job));
//		}
//	}
//	
//	private String getLastFolderName(String sourcePath) {
//		if(sourcePath == null || sourcePath.isEmpty())
//			return null;
//		if(sourcePath.endsWith("\\") || sourcePath.endsWith("/"))
//			sourcePath = sourcePath.substring(0, sourcePath.length() - 1);
//		
//		int indexBSlash = sourcePath.lastIndexOf("\\");
//		int indexSlash = indexBSlash < 0 || sourcePath.lastIndexOf("/") > indexBSlash ? sourcePath.lastIndexOf("/")  : indexBSlash;
//		if(indexSlash >= 0)
//			return sourcePath.substring(indexSlash + 1);
//		return null;
//	}
//
//	private String checkSameSourceDestPath(CopyJob job) throws ServiceException {
//		String destinationPath = getNormalizedPath(job.getDestinationPath());
//		String sourcePath = getNormalizedPath(job.getSessionPath());
//		if(destinationPath.equalsIgnoreCase(sourcePath))
//			throw new ServiceException(FlashServiceErrorCode.CopyJob_SameSourceDestPath);
//		return destinationPath;
//	}

//	private String getNormalizedPath(String destinationPath) {
//		String path = destinationPath == null ? "" : destinationPath;
//		if(path.endsWith("\\") || path.endsWith("/"))
//			path = path.substring(0, path.length() - 1);
//		return path;
//	}
	
	public void saveScheduledExportConfiguration(
			ScheduledExportConfiguration config) throws ServiceException {
		logger.info("saveScheduledExportConfiguration(config) - start, at " + new Date());
		String username = config.getDestUserName();
		String password = config.getDestPassword();
		
		if (username == null)
			username = "";
		if (password == null)
			password = "";
		
		String domain = "";
		int indx = username.indexOf('\\');
		if (indx > 0 && indx < username.length() - 1) {
			domain = username.substring(0, indx);
			username = username.substring(indx + 1);
		}
		// validate configuration
		if(config.isEnableScheduledExport()) {
			//It has been validated when validate the GUI.
//			scheduledExportConfigValidator.validateDestPath(config);
			String path = config.getDestination();
			getNativeFacade().validateDestUser(path, domain, username, password);
			path = BackupService.getInstance().appendHostNameIfNeeded(path, null, username, password, 1);
			config.setDestination(path);
		}
		
		// whether need to initCopyDestination
		String destination = config.getDestination();
		
		scheduledExportConfiguration = getScheduledExportConfiguration();
		boolean isNeedInitCopyDestination = false;
		if(scheduledExportConfiguration==null) {
			isNeedInitCopyDestination = true;
		} else {
			if(config.isEnableScheduledExport()) {
				if(scheduledExportConfiguration.getDestination() != null && !config.getDestination().equals(scheduledExportConfiguration.getDestination())) {
					isNeedInitCopyDestination = true;
				}else if(scheduledExportConfiguration.getDestination() == null && config.getDestination() != null) {
					isNeedInitCopyDestination = true;
				}
			}
		}
		
		if(isNeedInitCopyDestination && config.isEnableScheduledExport()) {
			getNativeFacade().initCopyDestination(destination, domain, username, password);
		}

		try {
			long ret = getNativeFacade().enabledScheduledExport(config.isEnableScheduledExport());
			if(ret == 0) { // call success. so can save cofig file.
				scheduledExportConfigurationXMLDAO.save(ServiceContext.getInstance().getScheduledExportConfigurationPath(), config);
				scheduledExportConfiguration = config;
			}
			
		} catch(Exception e) {
			logger.error("saveScheduledExportConfiguration()", e);
			if(e instanceof ServiceException) {
				throw (ServiceException)e;
			} else {
				throw generateInternalErrorAxisFault();
			}
		}

		if(!config.isEnableScheduledExport()){
			this.getNativeFacade().deleteLicError(CommonService.AFLIC_INFO_ID_SCHEDULE_EXPORT);
		}
		
		logger.info("saveScheduledExportConfiguration(config) - end");
	}
	
	public ScheduledExportConfiguration getScheduledExportConfiguration()
			throws ServiceException {
		logger.debug("getScheduledExportConfiguration() - start");
		try {
			if (scheduledExportConfiguration == null) {
				if (!StringUtil.isExistingPath(ServiceContext.getInstance()
						.getScheduledExportConfigurationPath()))
					return null;

				scheduledExportConfiguration = scheduledExportConfigurationXMLDAO
						.get(ServiceContext.getInstance().getScheduledExportConfigurationPath());

			}
			logger.debug("getScheduledExportConfiguration() - end");
			return scheduledExportConfiguration;
		} catch (ServiceException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception e) {
			logger.error("getScheduledExportConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public void clearCachedConfiguration()
	{
		scheduledExportConfiguration = null;
	}
	
	private boolean needSubmitJob() throws Exception {
		if(scheduledExportConfiguration == null)
			return false;
		
		boolean isEnabled = scheduledExportConfiguration.isEnableScheduledExport();
		if(!isEnabled) {
			return false;
		}
		// add backup num.
		/*long addBackupRet = getNativeFacade().addSucceedBackupNum();
		if(addBackupRet != 0) {
			throw new Exception("adding Scucceed backup number error.");
		}
		int exportInterval = scheduledExportConfiguration
				.getExportInterval();
		boolean ret = getNativeFacade().checkScheduledExportInterval(exportInterval-1);*/
		
		
		return isSubmitCopy();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		// disable plan
		try {
		    BackupConfiguration conf = BackupService.getInstance().getBackupConfiguration();
		    if (conf.isDisablePlan()) {
			    logger.info("The plan is disabled, do not run file copy job");
			    return;
		    }
		}
		catch (ServiceException e) {
			logger.error("Can not get backup configuration");
		}
		
		if(arg == null)
			return;
		submitCopy = false;
		try {
			JJobMonitor jobMonitor = null;
			//boolean isCatalogDisabled = false;
			boolean checkSchedule = false;
			if (arg instanceof JJobMonitor) {
				jobMonitor = (JJobMonitor) arg;
				
				//schedule export after file system catalog job
				//if catalog is disabled, also schedule it after successfull backup job
				//fix for 144821, zhawe03
				if(jobMonitor.getUlJobType() == Constants.JOBTYPE_CATALOG_FS
						|| jobMonitor.getUlJobType() == Constants.AF_JOBTYPE_BACKUP 
								&& jobMonitor.getUlJobStatus() == BaseBackupJob.BackupJob_PROC_EXIT){
					checkSchedule = true;
					logger.info("Current job is " + jobMonitor.getUlJobType() + " status is " 
							+ jobMonitor.getUlJobStatus() + " need to check whether run copy job");
				}
			}else {
				logger.error("arg is not JJobMonitor instance " + arg);
				return;
			}
			
			if(!checkSchedule){
				logger.warn("Current job is " + jobMonitor.getUlJobType() + " status is " 
						+ jobMonitor.getUlJobStatus() + " no need to check wehether run copy job");
				return;
			}
			
			
			submitCopyJob(jobMonitor.getDwBKSessNum(),
					jobMonitor.getWzBKBackupDest(),
					jobMonitor.getWzBKDestUsrName(),
					jobMonitor.getWzBKDestPassword());
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
/*			if(!submitCopy)
				try {
					logger.info("No need to start copy, start merge job now");
//					MergeService.getInstance().resumeMerge(false, true);
					MergeService.getInstance().resumeMerge(AbstractMergeService.MergeEvent.OTHER_JOB_END);
				}catch(Exception e) {
					logger.error("Failed to start merge job " + e.getMessage());
				}*/
				
		}
	}

	//submit catalog dependency job
	public void submitCopyJob(long bkSessNum, String backupDest, String bkUser,
			String bkPassword){
		submitCopy = false;
		try {
			scheduledExportConfiguration = getScheduledExportConfiguration();
			if (scheduledExportConfiguration == null) {
				logger.info("scheduledExportConfiguration is null don't need to do schedule");
				return;
			}

			boolean isEnabled = scheduledExportConfiguration
					.isEnableScheduledExport();
			if (!isEnabled) {
				return;
			}
			// add backup num.
			long addBackupRet = getNativeFacade().addSucceedBackupNum();
			if (addBackupRet != 0) {
				throw new Exception("adding Scucceed backup number error.");
			}
			int exportInterval = scheduledExportConfiguration
					.getExportInterval();
			boolean ret = getNativeFacade().checkScheduledExportInterval(
					exportInterval);

			if (!ret) {
				logger.info("checkScheduledExportInterval - " + exportInterval
						+ " return false. Donnot need schedule");
				return;
			}
			// need to submit a export.
			// needn't check
			// if(!IsContinueJob()){
			// logger.warn("Job is skipped, because policy is changed");
			// }

			// create a copyjob.
			CopyJob job = new CopyJob();
			job.setJobType(2);
			job.setDestinationPath(scheduledExportConfiguration
					.getDestination());
			job.setDestinationUserName(scheduledExportConfiguration
					.getDestUserName());
			job.setDestinationPassword(scheduledExportConfiguration
					.getDestPassword());
			job.setCompressionLevel(scheduledExportConfiguration
					.getCompressionLevel());
			// if(scheduledExportConfiguration.getCompressionLevel()==7)
			// { // VHD
			// job.setJobType(scheduledExportConfiguration.getCompressionLevel());
			// } else {
			// job.setJobType(2);
			// }
			job.setEncryptTypeCopySession(scheduledExportConfiguration
					.getEncryptionAlgorithm());
			job.setEncryptPasswordCopySession(scheduledExportConfiguration
					.getEncryptionKey());
			job.setRestPoint(scheduledExportConfiguration
					.getKeepRecoveryPoints());
			long sessionNum = bkSessNum;
			job.setSessionNumber((int) bkSessNum);
			// String sessionPath = jobMonitor.getWzBKBackupDest();
			String sessionPath = backupDest;
			job.setSessionPath(sessionPath);
			// job.setUserName(jobMonitor.getWzBKDestUsrName());
			// job.setPassword(jobMonitor.getWzBKDestPassword());
			job.setUserName(bkUser);
			job.setPassword(bkPassword);
			BackupRPSDestSetting rpsSetting = BackupService.getInstance()
					.getRpsSetting();
			if (rpsSetting != null) {
				job.setRpsHost(rpsSetting.getRpsHost());
				job.setRpsDataStore(rpsSetting.getRPSDataStore());
				job.setRpsPolicy(rpsSetting.getRPSPolicyUUID());
				job.setRpsDataStoreDisplayName(rpsSetting
						.getRPSDataStoreDisplayName());
			}
			Lock lock = null;
			try {
				lock = RemoteFolderConnCache.getInstance().getLockByPath(
						sessionPath);
				if (lock != null) {
					lock.lock();
				}
				getNativeFacade().NetConn(bkUser, bkPassword, sessionPath);
				// for source backup encryption.
				String encryptPwd = getNativeFacade()
						.getSessPwdFromKeyMgmtBySessNum(sessionNum, sessionPath);
				job.setEncryptPassword(encryptPwd);
			} finally {
				if (lock != null)
					lock.unlock();
				try {
					getNativeFacade().disconnectRemotePath(sessionPath, null,
							bkUser, bkPassword, false);
				} catch (Exception e) {
					logger.error("Disconnection " + sessionPath + " failed");
				}

			}

			Scheduler scheduler = BackupService.getInstance()
					.getOtherScheduler();
			if (scheduler == null) {
				logger.error("cannot get scheduler...");
				return;
			}
			// submit the copyjob.
			submitCopy = true;
			submitCopyJob(job, scheduler);

		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	
	/*public boolean checkIfCatalogDisabled(JJobMonitor jobMonitor) throws Exception
	{
		boolean bRet = false;
		FileInputStream fIStream = null;
		Lock lock = null;
		int handle = 0;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(jobMonitor.getWzBKBackupDest());
			if(jobMonitor.getWzBKBackupDest().startsWith("\\\\") && lock == null) {
				logger.error("Failed to acquire the lock to path.");
				throw new Exception("Failed to acquire the lock to path.");
			}
			logger.debug("Getting lock to " + jobMonitor.getWzBKBackupDest() + ", userName:" + jobMonitor.getWzBKDestUsrName());
			if(lock != null )
				lock.lock();
			long netConn = BrowserService.getInstance().getNativeFacade()
					.NetConn(jobMonitor.getWzBKDestUsrName(), jobMonitor.getWzBKDestPassword(),
							jobMonitor.getWzBKBackupDest());
			if (netConn != 0) {
				logger.error("Failed to connect to remote configuration destination");
				logger.error("RemotePath: " + jobMonitor.getWzBKBackupDest());
				throw new Exception(
						"Failed to connect to remote configuration destination.");
			}
			String sessionNumber = "S0000000001";
			sessionNumber = sessionNumber.substring(0,sessionNumber.length()-String.valueOf(jobMonitor.getDwBKSessNum()).length())+jobMonitor.getDwBKSessNum();
			List<String> dests = new ArrayList<String>();

			long ret = BrowserService.getInstance().getNativeFacade()
					.GetAllBackupDestinations(jobMonitor.getWzBKBackupDest(), dests);
			// the dests contains the dest in the time order, from old to new
			if (ret != 0)
			{
				logger.error("No backup destinations have been found.");
				throw new Exception("No backup destinations have been found.");
			}
				
			String targetDest = null;
			for (String dest : dests) {
				if (!dest.endsWith("\\"))
					dest += "\\";
				File f = new File(dest + "VStore");
				File[] listSessions = f.listFiles();
				if (listSessions == null)
					continue;
				Arrays.sort(listSessions, new Comparator<File>() {

					@Override
					public int compare(File o1, File o2) {
						return o1.getName().compareTo(o2.getName());

					}
				});
				
				for (File session : listSessions) {
					if(session.getName().contentEquals(sessionNumber))
					{
						targetDest = dest + "VStore\\" + sessionNumber;
						break;
					}
				}
				if(targetDest != null)
					break;
			}
			if(targetDest == null)
			{
				logger.error("Failed to find the target session destination.");
			}
			File f = new File(targetDest);
			if(!f.exists())
			{
				logger.error("The specified session folder was not found.");
				throw new Exception("The specified session folder was not found.");
			}
			File[] fileList = f.listFiles();
			for (File file : fileList) {
				if(file.getName().startsWith("JS"))
				{
					fIStream =  new FileInputStream(file);
					if(fIStream == null)
					{
						logger.error("Unable to read the catalog job script.");
					}
					break;
				}
			}
			if(fIStream == null)
			{
				logger.error("Failed to locate the job script inside the target session folder.");
				bRet =  false;
			}
			else
			{
				bRet = true;
			}

		} catch (ServiceException e) {
			throw new Exception("Failed to check the catalog status for session.");
		}
		finally{
			if(fIStream != null)
				fIStream.close();
			if(lock != null)
				lock.unlock();
		}
		return bRet;
	}*/
	
//	private boolean IsContinueJob() {
//		int status=BackupService.getInstance().RefreshBackupConfigSettingWithEdge();
//		switch(status){
//		case PolicyCheckStatus.UNKNOWN:
//			// check may failed
//		case PolicyCheckStatus.SAMEPOLICY:
//			// check ok
//		case PolicyCheckStatus.NOPOLICY:
//			// unassigned policy, keep setting and continue job
//			return true;
//		case PolicyCheckStatus.POLICYDEPLOYING:
//			// policy deploying
//			int r=BackupService.getInstance().CheckBackupConfigSettingWithEdge();
//			return true;
//		case PolicyCheckStatus.DIFFERENTPOLICY:
//			// policy changed
//			r=BackupService.getInstance().CheckBackupConfigSettingWithEdge();
//			if(r==PolicyQueryStatus.FAIL)
//				BackupService.getInstance().getNativeFacade().addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_GENERAL, 
//					new String[]{WebServiceMessages.getResource("autoRedeployPolicySkip"),"","","",""});
//			return true;
//		case PolicyCheckStatus.POLICYFAILED:
//			// policy failed before
//			r=BackupService.getInstance().CheckBackupConfigSettingWithEdge();
//			if(r==PolicyQueryStatus.FAIL)
//				BackupService.getInstance().getNativeFacade().addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_GENERAL, 
//					new String[]{WebServiceMessages.getResource("autoRedeployFailedPolicySkip"),"","","",""});
//			return true;
//		}
//		
//		return true;
//	}
	
	public long validateScheduledExportConfiguration(ScheduledExportConfiguration configuration) throws ServiceException {
		try {
			boolean isEnabled = configuration.isEnableScheduledExport();
			if(isEnabled) {
				if(!edgeFlag){
					//don't check license
//					String licCom = this.getLicenseComp(configuration.getEncryptionAlgorithm() != 0);
//					if(!licCom.isEmpty()) {
//						throw new ServiceException(licCom, FlashServiceErrorCode.Common_License_Failure);
//					}
				}
				scheduledExportConfigValidator.validateDestPath(configuration);
			}
		} catch (ServiceException e) {
			logger.debug("ScheduledExportConfiguration Validation Failed:"
					+ e.getErrorCode());
			throw e;
		}
		return 0;
	}
	
	public ProtectionInformation getScheduledExportProtectionInfo() throws Throwable {
		ProtectionInformation protInfo = new ProtectionInformation();
		protInfo.setBackupType(BackupType.Copy);
		ScheduledExportConfiguration conf = getScheduledExportConfiguration();
		if(conf != null) {
			JProtectionInfo[] jpInfos = this.getNativeFacade().GetCopyProtectionInformation(conf.getDestination(), 
					null, conf.getDestUserName(), conf.getDestPassword());
			for(JProtectionInfo jpInfo : jpInfos){
				if(jpInfo.getType().equalsIgnoreCase("Full")) {
					int count = Integer.parseInt(jpInfo.getCount());
					protInfo.setCount(count);
					if(count > 0){
						Date serverDate = BackupConverterUtil.string2Date(jpInfo.getLastBackupTime());
						String serverDateString = StringUtil.date2String(serverDate);
						protInfo.setLastBackupTime(serverDateString);
						protInfo.setSize(Long.parseLong(jpInfo.getTotalSize()));
						protInfo.setTotalLogicalSize(Long.parseLong(jpInfo.getTotalLogicalSize()));
					}
				}
			}
			protInfo.setShedule(convertToBackupSchedule(conf));
		}
		else {
			protInfo.setShedule(convertToBackupSchedule(null));
		}
		return protInfo;
	}
	
	public BackupSchedule convertToBackupSchedule(ScheduledExportConfiguration conf) {
		BackupSchedule schedule = new BackupSchedule();
		//other places may pass conf to be null
		if(conf != null) {
			schedule.setEnabled(conf.isEnableScheduledExport());
			schedule.setInterval(conf.getExportInterval());
			schedule.setIntervalUnit(3);//unit 3 resembles "no of backups" in ui.
		}
		else {
			schedule.setEnabled(false);
			schedule.setInterval(0);
			schedule.setIntervalUnit(3);
		}
		return schedule;
	}
	
	public String getLicenseComp(boolean encryption) throws ServiceException {
		LicInfo lic = this.getNativeFacade().AFGetLicenseEx(false);
		StringBuilder str = new StringBuilder();
		if(lic.getDwScheduledExport() != 0) {
			str.append(WebServiceMessages.getResource("LicenseScheduledExport"));
			str.append("<br/>");
		}
		
		if(encryption && lic.getDwEncryption() != 0) {
			str.append(WebServiceMessages.getResource("LicenseEncryption", 
					WebServiceMessages.getResource("ScheduledCopy")));
		}
		
		return str.toString();
	}

	public boolean isSubmitCopy() {
		return submitCopy;
	}
	
	@SuppressWarnings("deprecation")
	public long copyNow(CopyJobArg jobArg) throws ServiceException {
		if (handleErrorFromRPS(jobArg) == -1)
			return -1;
		
		try {
			if (com.ca.arcflash.webservice.scheduler.CopyJob.isJobRunning()
					|| this.getNativeFacade().checkJobExist()) {
				throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
			}
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_COPYJOB,
					Constants.RUN_NOW,
					com.ca.arcflash.webservice.scheduler.CopyJob.class);
			jobDetail.getJobDataMap().put("Job", jobArg.getJobScript());
			jobDetail.getJobDataMap().put("NativeFacade",
					this.getNativeFacade());
			jobDetail.getJobDataMap().put(RPS_POLICY_UUID,
					jobArg.getPolicyUUID());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID,
					jobArg.getDataStoreUUID());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME,
					jobArg.getDataStoreName());
			jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());
			jobDetail.getJobDataMap().put(RPS_HOST, jobArg.getSrcRps());
			jobDetail.getJobDataMap().put(ON_DEMAND_JOB, jobArg.isOnDemand());

			jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);

			SimpleTriggerImpl trigger = ScheduleUtils
					.makeImmediateTrigger(0, 0);
			trigger.setName(jobDetail.getName() + "Trigger");
			trigger.setGroup(Constants.RUN_NOW);
			BackupService.getInstance().getOtherScheduler()
					.scheduleJob(jobDetail, trigger);
			
			logger.debug("submitCopyJob - end");
			return 0;
		} catch (ServiceException se) {
			throw se;
		} catch (Throwable e) {
			logger.error("submitCopyJob()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	@Override
	public boolean needRun(JobDependencySource source) {
		try {
			if(needSubmitJob()){
				return true;
			}else
				return false;
		}catch(Throwable t) {
			logger.error("Failed to check scheduled export job", t);
			return false;
		}
	}
	
	public void resetSubmitCopy(){
		this.submitCopy = false;
	}
}
