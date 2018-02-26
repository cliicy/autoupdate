package com.ca.arcflash.webservice.service;

import java.io.File;
import java.util.Date;
import java.util.Observable;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.jni.model.JProtectionInfo;
import com.ca.arcflash.webservice.data.ProtectionInformation;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;
import com.ca.arcflash.webservice.data.restore.CopyJob;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.model.JVMJobMonitorDetail;
import com.ca.arcflash.webservice.scheduler.BaseBackupJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.service.internal.ScheduledExportConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.service.validator.ScheduledExportConfigurationValidator;
import com.ca.arcflash.webservice.util.ScheduleUtils;


public class VMCopyService extends BaseService implements IJobDependency{
	Logger logger = Logger.getLogger(VMCopyService.class);
	 
	private static VMCopyService instance = new VMCopyService();
	private VMScheduledExportConfigurationXMLDAO scheduledExportConfigurationXMLDAO = new VMScheduledExportConfigurationXMLDAO();
	private ScheduledExportConfigurationValidator scheduledExportConfigValidator = new ScheduledExportConfigurationValidator();
	private boolean submitCopy = false;
	
	private VMCopyService(){
		
	}
	
	public static VMCopyService getInstance(){
		return instance;
	}
	
	public static String getVMScheduledExportRPFile(BackupVM vm){
		return ServiceContext.getInstance().getVsphereScheduleExportConfigurationFolderPath()+"\\"+vm.getInstanceUUID()+".xml";
	}

	public void clearCachedConfiguration() {
		
	}
	
	public void removeScheduledExportConfiguration(BackupVM vm) throws ServiceException
	{		
		String cfgPath = ServiceContext.getInstance().getVsphereScheduleExportConfigurationFolderPath();
		String cfgFilePath = cfgPath + "\\"+vm.getInstanceUUID()+".xml";
		
		File cfgFile = new File(cfgFilePath);
		if (cfgFile.exists())
		{
			logger.info("deleting scheduled export config file: " + cfgFilePath);
			cfgFile.delete();
		}		

		cfgFilePath = cfgPath + "\\"+vm.getInstanceUUID()+".SHExportCfg.ini";
		cfgFile = new File(cfgFilePath);
		if (cfgFile.exists())
		{
			logger.info("deleting scheduled export config file: " + cfgFilePath);
			cfgFile.delete();
		}
	}

	public void saveScheduledExportConfiguration(BackupVM vm, ScheduledExportConfiguration config, VSphereBackupConfiguration backupConfiguration) throws ServiceException{
		if (config == null)
		{
			getNativeFacade().enabledScheduledExport(false, vm.getInstanceUUID());
			removeScheduledExportConfiguration(vm);
			logger.error("saveScheduledExportConfiguration: ScheduledExportConfiguration is NULL");
			return;
		}
		
		if (backupConfiguration == null)
		{
			logger.error("saveScheduledExportConfiguration: VSphereBackupConfiguration is NULL");
			return;
		}
		
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
		
		BrowserService.getInstance().getNativeFacade().validateDestUser(config.getDestination(), domain, username, password);
		
		// validate configuration
		String originalDest = config.getDestination();
		if(config.isEnableScheduledExport()) {			
			String dest = config.getDestination();
			if (!originalDest.contains(vm.getVmName() + "@"
					+ vm.getEsxServerName().trim())) {
				dest = appendVMInfoIfNeeded(originalDest, filterVMName(vm
						.getVmName())
						+ "@" + vm.getEsxServerName().trim(), vm
						.getInstanceUUID(), true);
				config.setDestination(dest);
			}
		}
		
		// whether need to initCopyDestination
		String destination = config.getDestination();
		
		ScheduledExportConfiguration scheduledExportConfiguration = getScheduledExportConfiguration(vm);
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
			long ret = getNativeFacade().enabledScheduledExport(config.isEnableScheduledExport(), vm.getInstanceUUID());
			if(ret == 0) { // call success. so can save cofig file.
				scheduledExportConfigurationXMLDAO.save(getVMScheduledExportRPFile(vm), config);
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

		//TODO
		/*if(!config.isEnableScheduledExport()){
			this.getNativeFacade().deleteLicError(CommonService.AFLIC_INFO_ID_SCHEDULE_EXPORT);
		}*/
		
		config.setDestination(originalDest);
		
		logger.info("saveScheduledExportConfiguration(config) - end");
	}

	public ScheduledExportConfiguration getScheduledExportConfiguration(BackupVM vm) throws ServiceException{
		logger.debug("getScheduledExportConfiguration() - start");
		try {
			if (!StringUtil.isExistingPath(getVMScheduledExportRPFile(vm)))
					return null;

			ScheduledExportConfiguration scheduledExportConfiguration = scheduledExportConfigurationXMLDAO.get(getVMScheduledExportRPFile(vm));
			return scheduledExportConfiguration;
		} catch (ServiceException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception e) {
			logger.error("getScheduledExportConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public ScheduledExportConfiguration getScheduledExportConfiguration(VirtualMachine vm) throws ServiceException{
		BackupVM backupVM = new BackupVM();
		backupVM.setInstanceUUID(vm.getVmInstanceUUID());
		return getScheduledExportConfiguration(backupVM);
	}
	
	/*private boolean needSubmitJob(String instanceUUID) throws Exception {
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(instanceUUID);
		ScheduledExportConfiguration scheduledExportConfiguration = getScheduledExportConfiguration(vm);
		boolean isEnabled = scheduledExportConfiguration.isEnableScheduledExport();
		if(!isEnabled) {
			return false;
		}
		//TODO
		//return isSubmitCopy();
		return true;
	}*/
	
	
	private boolean needSubmitJob(String instanceUUID) throws Exception {
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(instanceUUID);
		ScheduledExportConfiguration scheduledExportConfiguration = getScheduledExportConfiguration(vm);
		if(scheduledExportConfiguration == null)
			return false;
		
		boolean isEnabled = scheduledExportConfiguration.isEnableScheduledExport();
		if(!isEnabled) {
			return false;
		}
		
		long addBackupRet = getNativeFacade().addSucceedBackupNum(vm.getVmInstanceUUID());
		if (addBackupRet != 0) {
			throw new Exception("adding Scucceed backup number error.");
		}
		int exportInterval = scheduledExportConfiguration.getExportInterval();
		boolean ret = getNativeFacade().checkScheduledExportInterval(exportInterval, vm.getVmInstanceUUID());
		
		setSubmitCopy(ret);
		logger.info("submitCopyFlag is: " + ret);
		return ret;
		
	}
	
	public boolean isSubmitCopy() {
		return submitCopy;
	}
	
	public void setSubmitCopy(boolean value)
	{
		this.submitCopy = value;
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		if (arg instanceof JVMJobMonitorDetail) {
			JVMJobMonitorDetail jobMonitorDetail = (JVMJobMonitorDetail) arg;
			JJobMonitor jJM = jobMonitorDetail.getjJobMonitor();
			VSphereJobContext jobContext = jobMonitorDetail.getJobContext();
			String vmInstanceUUID = jobContext.getExecuterInstanceUUID();
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUUID);
			// disable plan
			try {
				VMBackupConfiguration conf = VSphereService.getInstance().getVMBackupConfiguration(vm);;
				if (conf.isDisablePlan()) {
					logger.info("The plan is disabled, do not run copy job");
					return;
				}
			} catch (ServiceException e) {
				logger.error("Can not get backup configuration");
			}
			
			//boolean submitCopy = false;
			try {
				JJobMonitor jobMonitor = jobMonitorDetail.getjJobMonitor();
				// boolean isCatalogDisabled = false;
				boolean checkSchedule = false;
				
				// schedule export after file system catalog job
				// if catalog is disabled, also schedule it after
				// successfull
				// backup job
				// fix for 144821, zhawe03
				if ((jobMonitor.getUlJobType() == Constants.AF_JOBTYPE_VM_CATALOG_FS
						|| jobMonitor.getUlJobType() == Constants.AF_JOBTYPE_VM_BACKUP
						|| jobMonitor.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP
						)&& jobMonitor.getUlJobStatus() == BaseBackupJob.BackupJob_PROC_EXIT) {
					checkSchedule = true;
					logger.info("Current job is "
							+ jobMonitor.getUlJobType() + " status is "
							+ jobMonitor.getUlJobStatus()
							+ " need to check whether run copy job");
				}

				if (!checkSchedule) {
					logger.warn("Current job is " + jobMonitor.getUlJobType()
							+ " status is " + jobMonitor.getUlJobStatus()
							+ " no need to check wehether run copy job");
					return;
				}

				submitCopyJob(vm, jobMonitor.getDwBKSessNum(),
						jobMonitor.getWzBKBackupDest(),
						jobMonitor.getWzBKDestUsrName(),
						jobMonitor.getWzBKDestPassword());
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
			}
		}
	}

	@Override
	public boolean needRun(JobDependencySource source) {
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(source.getClientUUID());
		try{
			ScheduledExportConfiguration scheduledExportConfiguration = getScheduledExportConfiguration(vm);
			if(scheduledExportConfiguration == null)
				return false;
		
			boolean isEnabled = scheduledExportConfiguration.isEnableScheduledExport();
			if(isEnabled) {
				return true;
			}
		}catch(Throwable t) {
			logger.error("Failed to check scheduled export job", t);
			return false;
		}
		return false;
		/*try {
			if(needSubmitJob(source.getClientUUID())){
				return true;
			}else
				return false;
		}catch(Throwable t) {
			logger.error("Failed to check scheduled export job", t);
			return false;
		}*/
	}
	
	static class VMScheduledExportConfigurationXMLDAO extends ScheduledExportConfigurationXMLDAO{

		@Override
		public synchronized void save(String filePath,
				ScheduledExportConfiguration configuration) throws Exception {
			File file = new File(ServiceContext.getInstance().getVsphereScheduleExportConfigurationFolderPath());
			if (!file.exists()){
				try {
					file.mkdir();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.save(filePath, configuration);
		}

		
	}
	

	public long validateScheduledExportConfiguration(ScheduledExportConfiguration configuration, BackupVM vm) throws ServiceException {
		try {
			boolean isEnabled = configuration.isEnableScheduledExport();
			if(isEnabled) {
				scheduledExportConfigValidator.validateDestPath(configuration, vm);
			}
		} catch (ServiceException e) {
			logger.debug("ScheduledExportConfiguration Validation Failed:"
					+ e.getErrorCode());
			throw e;
		}
		return 0;
	}
	
	// submit catalog dependency job
	public void submitCopyJob(VirtualMachine vm, long bkSessNum, String backupDest, String bkUser,
			String bkPassword) {
	//	boolean submitCopy = false;
		try {
			ScheduledExportConfiguration scheduledExportConfiguration = getScheduledExportConfiguration(vm);

			if (scheduledExportConfiguration != null) {
				boolean isEnabled = scheduledExportConfiguration.isEnableScheduledExport();
				if (!isEnabled) {
					return;
				}
				/*if(!submitCopy){
					logger.info("Don't need to start the copy now");
					return;
				}*/
				if(!needSubmitJob(vm.getVmInstanceUUID())){
					logger.info("Don't need to start the copy now");
					return;
				}
				// add backup num.

				/*long addBackupRet = getNativeFacade().addSucceedBackupNum(vm.getVmInstanceUUID());
				if (addBackupRet != 0) {
					throw new Exception("adding Scucceed backup number error.");
				}
				int exportInterval = scheduledExportConfiguration.getExportInterval();
				boolean ret = getNativeFacade().checkScheduledExportInterval(exportInterval, vm.getVmInstanceUUID());
		*/	//	if (ret) { // need to submit a export.
					// needn't check
					// if(!IsContinueJob()){
					// logger.warn("Job is skipped, because policy is changed");
					// }
				if(isEnabled){
					// create a copyjob.
					CopyJob job = new CopyJob();
					job.setJobType(2);
					job.setVmInstanceUUID(vm.getVmInstanceUUID());
					job.setJobLauncher(1);
					job.setDestinationPath(scheduledExportConfiguration.getDestination());
					job.setDestinationPath(scheduledExportConfiguration.getDestination());
					job.setDestinationUserName(scheduledExportConfiguration.getDestUserName());
					job.setDestinationPassword(scheduledExportConfiguration.getDestPassword());
					job.setCompressionLevel(scheduledExportConfiguration.getCompressionLevel());
					job.setEncryptTypeCopySession(scheduledExportConfiguration.getEncryptionAlgorithm());
					job.setEncryptPasswordCopySession(scheduledExportConfiguration.getEncryptionKey());
					job.setRestPoint(scheduledExportConfiguration.getKeepRecoveryPoints());
					long sessionNum = bkSessNum;
					job.setSessionNumber((int) bkSessNum);
					// String sessionPath = jobMonitor.getWzBKBackupDest();
					String sessionPath = backupDest;
					job.setSessionPath(sessionPath);
					// job.setUserName(jobMonitor.getWzBKDestUsrName());
					// job.setPassword(jobMonitor.getWzBKDestPassword());
					job.setUserName(bkUser);
					job.setPassword(bkPassword);
					BackupRPSDestSetting rpsSetting = VSphereService.getInstance().getRpsSetting(vm.getVmInstanceUUID());
					if (rpsSetting != null) {
						job.setRpsHost(rpsSetting.getRpsHost());
						job.setRpsDataStore(rpsSetting.getRPSDataStore());
						job.setRpsPolicy(rpsSetting.getRPSPolicyUUID());
						job.setRpsDataStoreDisplayName(rpsSetting
								.getRPSDataStoreDisplayName());
					}
					Lock lock = null;
					try {
						lock = RemoteFolderConnCache.getInstance()
								.getLockByPath(sessionPath);
						if (lock != null) {
							lock.lock();
						}
						getNativeFacade().NetConn(bkUser, bkPassword,
								sessionPath);
						// for source backup encryption.
						String encryptPwd = getNativeFacade()
								.getSessPwdFromKeyMgmtBySessNum(sessionNum,
										sessionPath);
						job.setEncryptPassword(encryptPwd);
					} finally {
						if (lock != null)
							lock.unlock();
						try {
							getNativeFacade().disconnectRemotePath(sessionPath,
									null, bkUser, bkPassword, false);
						} catch (Exception e) {
							logger.error("Disconnection " + sessionPath
									+ " failed");
						}

					}

					Scheduler scheduler = VSphereService.getInstance().getOtherScheduler();
					if (scheduler == null)
						return;
					
					submitCopyJob(job, scheduler);
				}
			}
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public ProtectionInformation getScheduledExportProtectionInfo(VirtualMachine vm) throws Throwable {
		ProtectionInformation protInfo = new ProtectionInformation();
		protInfo.setBackupType(BackupType.Copy);
		ScheduledExportConfiguration conf = getScheduledExportConfiguration(vm);
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
	
	private void submitCopyJob(CopyJob job, Scheduler scheduler) throws ServiceException {
		try {
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_COPYJOB
					+ job.getVmInstanceUUID(), null,
					com.ca.arcflash.webservice.scheduler.VSphereCopyJob.class);
			jobDetail.getJobDataMap().put("Job", job);
			jobDetail.getJobDataMap().put("NativeFacade",
					this.getNativeFacade());
			jobDetail.getJobDataMap().put(RPS_POLICY_UUID, job.getRpsPolicy());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, job.getRpsDataStore());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, job.getRpsDataStoreDisplayName());
			jobDetail.getJobDataMap().put(RPS_HOST, job.getRpsHost());;
			jobDetail.getJobDataMap().put(ON_DEMAND_JOB, false);
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(jobDetail.getName() + "Trigger");
			scheduler.scheduleJob(jobDetail, trigger);

			logger.debug("submitVMCopyJob - end");
		} catch (Throwable e) {
			logger.error("submitVMCopyJob()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	public void resetSubmitCopy(){
		logger.info("Resetting the submitCopy flag. Original value: " + this.submitCopy );
		setSubmitCopy(false);
	}
	
}
