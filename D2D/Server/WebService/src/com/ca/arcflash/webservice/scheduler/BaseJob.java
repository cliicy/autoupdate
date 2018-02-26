package com.ca.arcflash.webservice.scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.service.common.CatalogQueueType;
import com.ca.arcflash.service.common.CommonUtils;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.job.rps.BaseJobArg;
import com.ca.arcflash.webservice.data.job.rps.BaseJobArgWithSourceInfo;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyUsageMarker;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyUsageMarker.SettingsTypes;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobContext;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.service.AbstractMergeService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CatalogService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.service.RegConstants;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.service.rps.SettingsService;
import com.ca.arcflash.webservice.servlet.Util;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public abstract class BaseJob extends Observable implements Job {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(BaseJob.class);	
	protected static JobExecutionContext missedJobContext;
	public static JobExecutionContext getMissedJobContext() {
		return missedJobContext;
	}

	public static void setMissedJobContext(JobExecutionContext missedJobContext) {
		BaseJob.missedJobContext = missedJobContext;
	}
	
	protected static Object missJobLock = new Object();
	
	protected JJobScript js;
	
	protected String jobStatusSyncUuid = UUID.randomUUID().toString(); //20110603

	
	protected volatile boolean isStopJM = false;
	public final static ExecutorService pool = Executors.newFixedThreadPool(10);


	protected long shrmemid;
	protected long jobStatus = -1;
	
	public Object phaseLock = new Object();
	protected long jobPhase = 0;
	
	protected String rpsPolicyUUID = null;
	protected String planUUID = null;
	protected String rpsDataStoreUUID = null;
	protected String rpsDataStoreName = null;
	protected boolean dedupeEnabled = false;
	protected RpsHost rpsHost = null;
	protected String name = "";
	protected boolean isOnDemand = false;
	protected boolean isMaunal = false;

	/**
	 * If d2d backup to rps data store, or restore from rps data store, we need first check the rps policy and data store,
	 * then submit the job to rps job queue.
	 * @param runNow
	 * @param toRPS
	 * @param rpsSetting
	 * @param jobDetail
	 * @param jobType
	 * @return
	 */
	protected boolean checkRPS4Job(Boolean runNow,
													boolean toRPS,
													RpsHost rpsHost,
													String dataStoreUUID,
													JobExecutionContext jobContext,
													long jobType,
													NativeFacade nativeFacade) 
							throws ServiceException{
		RpsPolicy4D2D rpsPolicy = null;
		String message = null;
		String sJobType = ServiceUtils.jobType2String(jobType);
		if(toRPS){
			//check whether rps policy exist
			try {
				rpsPolicy = checkRPSDataStore4Job(rpsHost, dataStoreUUID, jobType);
			} catch(ServiceException se) {
				logger.error("Check RPS for job got exception ", se);
				message = se.getMessage();
				if(StringUtil.isEmptyOrNull(message)) {
					message = WebServiceMessages.getResource("jobNotRunPolicy", sJobType);
				}else {
					message = WebServiceMessages.getResource("jobNotRunRPSError", sJobType, message);
				}
			}catch(Throwable t) {
				message = WebServiceMessages.getResource("jobNotRunPolicy", sJobType);
				logger.error("Check RPS for job got exception ", t);
			}
			
			if(rpsPolicy == null){
				long datastoreVersion = SettingsService.instance().getRPSDataStoreVersion(rpsHost, dataStoreUUID);
                addMissedJobHistory(jobType, dataStoreUUID, datastoreVersion, 0, 0);
				logErrorAct(nativeFacade, message);
				sendEmailOnRpsInvalid(jobContext, getBackupDestination(null));
				throw new ServiceException(FlashServiceErrorCode.Backup_RPS_Get_Policy_Failed);
			}
		}
		
		if(toRPS && (runNow == null || !runNow)){
			return true;
		}else {
			return false;
		}
	}
	
	protected void addMissedJobHistory(long jobType, String dataStoreUUID, long datastoreVersion, long jobMethod, int periodRetentionFlag){
        JJobHistory jobHistory = new JJobHistory();
        jobHistory.setJobId(shrmemid);
        jobHistory.setJobType( (int)jobType );
        jobHistory.setJobMethod((int)jobMethod);
        jobHistory.setJobStatus(JobStatus.JOBSTATUS_MISSED);
        jobHistory.setDatastoreUUID(dataStoreUUID);
        jobHistory.setDatastoreVersion(datastoreVersion);
        jobHistory.setJobName(name);                
        jobHistory.setTargetUUID(getRPSServerID());
        jobHistory.setPlanUUID(isOnDemand ? "" : getPlanUUID(null));
        jobHistory.setPeriodRetentionFlag(periodRetentionFlag);
        BackupService.getInstance().getNativeFacade().addMissedJobHistory(jobHistory);
	}
	
	protected void logErrorAct(NativeFacade nativeFacade, String msg){
		if(!StringUtil.isEmptyOrNull(msg)){
			nativeFacade.addLogActivityWithJobID(Constants.AFRES_AFALOG_ERROR,
					shrmemid,
					Constants.AFRES_AFJWBS_GENERAL, 
					new String[]{msg, "","","",""});
		}
	}
	
	protected String getBackupDestination(String vmInstanceUUID) {
		try {
			BackupConfiguration conf = BackupService.getInstance().getBackupConfiguration();
			if(conf != null)
				return conf.getDestination();
		} catch (ServiceException e) {
			logger.error("Failed to get backup destination");
		}
		return null;
	}
	protected void getJobArg(JobDetailImpl jobDetail,
			String policyUUID,
			String datastoreUUID,
			long jobType, BaseJobArg jobArg, String dataStoreName) {
		jobArg.setJobDetailName(jobDetail.getName());
		jobArg.setJobDetailGroup(jobDetail.getGroup());
		jobArg.setPolicyUUID(policyUUID);
		jobArg.setDataStoreUUID(datastoreUUID);
		Object jobName = jobDetail.getJobDataMap().get("jobName");
		if(jobName != null){
			jobArg.setJobName((String)jobName);
		}
		jobArg.setD2dServerName(ServiceContext.getInstance().getLocalMachineName());
		jobArg.setD2dServerUUID(CommonService.getInstance().getNodeUUID());
		jobArg.setDataStoreName(dataStoreName);
		jobArg.setJobType(jobType);		
		jobArg.setJobId(shrmemid);
	}
	
	protected void getJobArgWithSrc(JobDetail jobDetail,
			String policyUUID,
			String datastoreUUID,
			long jobType, RpsHost rpsHost, BaseJobArgWithSourceInfo jobArg, String dataStoreName){
		getJobArg((JobDetailImpl)jobDetail, policyUUID, datastoreUUID, jobType, jobArg, dataStoreName);
		jobArg.setD2dLoginUUID(CommonService.getInstance().getLoginUUID());
		jobArg.setLocalD2DName(ServiceContext.getInstance().getLocalMachineName());
		jobArg.setLocalD2DPort(CommonService.getInstance().getServerPort());
		jobArg.setLocalD2DProtocol(CommonService.getInstance().getServerProtocol());
		jobArg.setSrcRps(rpsHost);
		jobArg.setIpList(CommonService.getInstance().getNativeFacade().getD2DIPList());
		jobArg.setOnDemand(isOnDemand);
	}

	protected RpsPolicy4D2D checkRPSDataStore4Job(RpsHost rpsHost, String dataStoreUUID, long jobType) 
			throws ServiceException {
		return SettingsService.instance().checkDataStore4Job(rpsHost, dataStoreUUID, jobType);
	}
	
	protected boolean toRPS(){
		return BackupService.getInstance().isBackupToRPS();
	}
	
	protected BackupRPSDestSetting getRpsSettings(){
		try {
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			
			if(configuration == null)
				return null;
			else 
				return configuration.getBackupRpsDestSetting();
		}catch(ServiceException se) {
			logger.error("Failed to get rps settings for backup");
			return null;
		}
	}
	
	protected BackupEmail getEmailSetting() {
		try {
			BackupConfiguration configuration = BackupService.getInstance()
					.getBackupConfiguration();
			if (configuration == null)
				return null;

			// BackupEmail email = configuration.getEmail();
			PreferencesConfiguration preferencesConfig = CommonService.getInstance().getPreferences();
			if (preferencesConfig == null) {
				return null;
			}
			
			return preferencesConfig.getEmailAlerts();
		}catch(Throwable t) {
			logger.error("Failed to get email settings", t);
			return null;
		}
	}
	
	protected void sendEmailOnRpsInvalid(JobExecutionContext context, String destination){
		BackupEmail email = getEmailSetting();
		if(email == null || !email.isEnableEmail()){
			return;
		}
		sendMissedJobEmail(email, context, destination, true);
	}
	
	protected void sendMissedJobEmail(BackupEmail email, JobExecutionContext context, String destination, boolean causedByRPS) {
		if (email.isEnableEmailOnMissedJob()) {
			EmailSender emailSender = new EmailSender();
			String jobStatus = EmailContentTemplate.jobStatus2String(Constants.JOBSTATUS_MISSED);
			String emailJobStatus = WebServiceMessages
					.getResource("EmailJobStatus");
			String hostName = ServiceContext.getInstance()
					.getLocalMachineName();
			String emailSubject = email.getSubject() + "-" + emailJobStatus
					+ jobStatus + "(" + hostName + ")";

			/** for promoting alert message  to edge server */
			emailSender.setJobStatus(Constants.JOBSTATUS_MISSED);
			emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue());

			emailSender.setSubject(emailSubject);

			emailSender.setContent(getMissedJobContent(email
					.isEnableHTMLFormat(), context, destination, causedByRPS));

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
			emailSender.setJobType(this.getDefaultJobType());
			emailSender.sendEmail(email.isEnableHTMLFormat());
		}
	}

	protected String getMissedJobContent(boolean isEnableHTMLFormat,
			JobExecutionContext context, String dest, boolean causedByRPS) {
		String exeDate = BackupConverterUtil.dateToString(context.getFireTime());		
		String url = getBackupSettingsURL();
		ActivityLogResult result= null;
		if(causedByRPS) {
			try {
				if (this.shrmemid > 0)
				    result = CommonService.getInstance().getJobActivityLogs(this.shrmemid, 0, 5);
				else
					result = CommonService.getInstance().getActivityLogs(0, 5);
			} catch (Exception e) {
				logger.error("Failed to get activity log", e);
			}
		}
		if (isEnableHTMLFormat) {
			return EmailContentTemplate.getHtmlContent(Constants.JOBSTATUS_MISSED,
					this.getDefaultJobType(), 0, this.shrmemid, exeDate, null, dest,
					result, url);
		} else {
			return EmailContentTemplate.getPlainTextContent(Constants.JOBSTATUS_MISSED,
					this.getDefaultJobType(), 0, this.shrmemid, exeDate, dest, dest,
					result, url);
		}
	}
	
	public static void resumeJobAfterRestart(JJobContext context) {
		logger.debug("resumeJobAfterRestart - start, type is " + 
				context.getDwJobType() + ", Id is " + context.getDwJobId());
		switch(new Long(context.getDwJobType()).intValue()) {
		case Constants.AF_JOBTYPE_BACKUP:
			new BaseBackupJob().resumeAfterRestarted(context.getDwJobId(), 
					BaseBackupJob.observers, Constants.AF_JOBTYPE_BACKUP);
			break;
		case Constants.AF_JOBTYPE_COPY:
			if(context.getDwLauncher() == VSphereJobContext.JOB_LAUNCHER_D2D)
			new CopyJob().resumeAfterRestarted(context.getDwJobId(), CopyJob.observers, Constants.AF_JOBTYPE_COPY);
			break;
		case Constants.AF_JOBTYPE_RESTORE:
			if(context.getDwLauncher() == VSphereJobContext.JOB_LAUNCHER_D2D)
			new RestoreJob().resumeAfterRestarted(context.getDwJobId(), RestoreJob.observers, Constants.AF_JOBTYPE_RESTORE);
			break;
		case Constants.JOBTYPE_CATALOG_FS:
		case Constants.JOBTYPE_CATALOG_FS_ONDEMAND:
		case Constants.JOBTYPE_CATALOG_GRT:
			if(context.getDwLauncher() == VSphereJobContext.JOB_LAUNCHER_D2D){
				if(context.getDwQueueType() == CatalogQueueType.ONDEMAND_JOB)
					CatalogService.getInstance().resumeOnDemandCatalogJob(context.getDwJobId());
				else if(context.getDwQueueType() == CatalogQueueType.REGULAR_JOB
						|| context.getDwQueueType() == CatalogQueueType.MAKEUP_JOB)
					CatalogService.getInstance().resumeCatalogJob(context.getDwJobId(),
							context.getDwJobType());
			}
			break;
		default:
			logger.warn("We don't support resume job for archive now");
			break;
		}
		
		logger.debug("preprocess(JJobScript) - end");
	}
   	public String getDestination(){
   		BackupConfiguration configuration=null;
		try {
			configuration = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			logger.error("Error in geting BackupConfiguration", e);
		}
   		if(configuration==null){
   			return null;
   		}else{
   			return configuration.getDestination();
   		}
   	}
   	
   	public String getDataStoreDisplayName(){
   		BackupConfiguration configuration = null;
		try {
			configuration = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			logger.error("Error in geting BackupConfiguration", e);
		}
   		if(configuration == null){
   			return null;
   		}else{
   			if (configuration.getBackupRpsDestSetting() == null)
   				return null;
   			return configuration.getBackupRpsDestSetting().getRPSDataStoreDisplayName();
   		}
   	}
   	
   	public String getRPSServerID(){
   		if(rpsHost == null)
   			return null;
   		else
   			return SettingsService.instance().getRpsServerID(rpsHost);
   	}
   	
   	public RPSDataStoreInfo getRpsDataStoreInfo(String dataStoreUUID){
   		if(rpsHost == null)
   			return new RPSDataStoreInfo();
   		else
   			return SettingsService.instance().getRpsDataStoreInfo(rpsHost, dataStoreUUID);
   	}
   	
   	protected String getPlanUUID(String vmInstanceUUID) {
   		planUUID = BackupService.getInstance().getPlanUUID(vmInstanceUUID);
   		return planUUID;
   	}

	protected void resumeAfterRestarted(long jobID, Observer[] observers){ 
		if(!getJobLock()){
			logger.warn("resume job " + jobID + " failed, job is running in webservice");
			return;
		}
		try {
			shrmemid = jobID;
//			js.setUlJobID(shrmemid);
			logger.info("resume job jobid(shrmemid):" + shrmemid);

			if(observers != null){
				for(Observer observer : observers){
		        	addObserver(observer);
		        }
			}
			
	        pool.submit(new JobMonitorThread(this));
		}catch (RuntimeException e) {
			releaseJobLock();
			throw e;
		}
	}
	
	protected void resumeAfterRestarted(long jobID, Observer[] observers, int jobType){ 
		if(!getJobLock()){
			logger.warn("resume job " + jobID + " failed, job is running in webservice");
			return;
		}
		try {
			shrmemid = jobID;
//			js.setUlJobID(shrmemid);
			logger.info("resume job jobid(shrmemid):" + shrmemid);

			if(observers != null){
				for(Observer observer : observers){
		        	addObserver(observer);
		        }
			}
			this.pauseMergeJob(jobType);
			
			this.resumeSerialization();
	        pool.submit(new JobMonitorThread(this));
		}catch (RuntimeException e) {
			releaseJobLock();
			throw e;
		}
	}
	
	public static String getServerURL() {
		String url = "";
		try{
			InetAddress localHost = InetAddress.getLocalHost();
			String hostname = localHost.getHostName();

			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(RegConstants.REGISTRY_WEBSERVICE);
			url = registry.getValue(handle, RegConstants.REGISTRY_URL);
			registry.closeKey(handle);

			url = url.replaceAll("localhost", hostname);
			logger.debug("URL:"+url);
		}catch(Exception e){
			logger.error(e);
		}
		return url;
	}
	
	protected String getSettingsURL() {
		return getBackupSettingsURL();	
    }
	
	public static String getBackupSettingsURL() {
		String url = getServerURL();
		if(url!=null && !url.endsWith("/")){
			url+="/";
		}
		url+="?location=backup";
		
		// if is managed by Edge, open edge url instead.
		boolean isUseEdgePolicySetting = PolicyUsageMarker.getInstance().isUsingEdgePolicySettings(SettingsTypes.BackupSettings);
		if(isUseEdgePolicySetting){
			D2DEdgeRegistration d2dReg = new D2DEdgeRegistration();		
			url = d2dReg.getConsoleUrl();
//			String edgewdsl = d2dReg.GetEdgeWSDL();	
//			if(edgewdsl != null && edgewdsl.trim().length()>0){
//				url = edgewdsl.split("services")[0];
//			}
		}
		return url;
	}

	public long getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(long jobStatus) {
		this.jobStatus = jobStatus;
	}

	/**
	 * Get the job lock.
	 *
	 * @return true if successfully getting the lock.
	 */
	protected boolean getJobLock(){return true;}

	protected void releaseJobLock() {};
	
	protected abstract long getDefaultJobType();
	
	protected void afterComplete(){}

	protected void initShrmemid() {
		try{
			shrmemid = CommonService.getInstance().getNativeFacade().getJobID();
			logger.info("Get Job ID:" + shrmemid);
		}
		catch (RuntimeException e) {
			releaseJobLock();
			throw e;
		}
	}
	
	/**
	 * Start job monitor thread.
	 *
	 * @param true no other job is running.
	 */
	protected boolean preprocess(JJobScript js, Observer[] observers) {
		logger.debug("preprocess(JJobScript) - start");

		if(!getJobLock())
			return false;
		try {
//			shrmemid = CommonService.getInstance().getNativeFacade().getJobID();
			int jobType = js != null ? js.getUsJobType() : 0; 
			
			JJobHistory jobHistory = getJobHistory(jobType);		
			long startTime = BackupService.getInstance().getNativeFacade().updateJobHistory( jobHistory );
			
			js.setUlJobID(shrmemid);
			logger.info("Processing jobid(shrmemid):" + shrmemid);
			int flag = js.getFOptions() & Constants.QJDTO_B_DISABLE_CATALOG;
			if(observers != null){
				for(Observer observer : observers){
					if(!observer.toString().contains("CopyService") || (flag == Constants.QJDTO_B_DISABLE_CATALOG))
					addObserver(observer);
		        }
			}
			pauseMergeJob(jobType);
			JobMonitorThread jt = new JobMonitorThread(this);
			jt.startTime = startTime;
	        pool.submit(jt);
		}catch (RuntimeException e) {
			releaseJobLock();
			throw e;
		}

		logger.debug("preprocess(JJobScript) - end");
		return true;
	}
	
	protected JJobHistory getJobHistory(int jobType) {

		JJobHistory jobHistory = new JJobHistory();
		jobHistory.setJobId(shrmemid);
		jobHistory.setJobType(jobType);
		jobHistory.setDatastoreUUID(getRPSDatastoreUUID());			
		jobHistory.setJobName(name);
		RPSDataStoreInfo rpsDataStoreInfo = getRpsDataStoreInfo(getRPSDatastoreUUID());
		jobHistory.setDatastoreVersion(rpsDataStoreInfo.getVersion());
		jobHistory.setTargetUUID(rpsDataStoreInfo.getRpsServerId());
		jobHistory.setPlanUUID(isOnDemand ? "" : getPlanUUID(null));			
		//jobHistory.setPlanUUID(getPlanUUID(null));
		return jobHistory;
	}
	
	protected String getRPSDatastoreUUID() {
		return rpsDataStoreUUID;
	}

	protected void pauseMergeJob(int jobType) {
		try {
//			MergeService.getInstance().pauseMerge(false, true);
			String strType = ServiceUtils.jobType2String(jobType);
			MergeService.getInstance().pauseMerge(
					AbstractMergeService.MergeEvent.OTHER_JOB_START, 
					strType,
					this);
			MergeService.getInstance().waitForJobEnd();			
		}catch(Exception e) {
			logger.error("Failed to pause merge job", e);
		}
		
	}
	
   	public void stopJobMonitor() {
		isStopJM = true;
	}

   	@Override
	public void notifyObservers(Object jJM) {
		setChanged();
		super.notifyObservers(jJM);
	}

   	protected boolean isJobDone(long jobPhase, long jobStatus){
   		if (jobPhase == Constants.JobExitPhase
				|| jobPhase == Constants.CatalogDone
				|| isJobFinishedWithStatus(new Long(jobStatus).intValue())) {
			return true;
		}else {
			return false;
		}
   	}
   	
	protected boolean isJobFinishedWithStatus(int status) {
		switch(status){
		case Constants.JOBSTATUS_CRASH:
		case Constants.JOBSTATUS_FAILED:
		case Constants.JOBSTATUS_FINISHED:
			return true;
			default:
				return false;
		}
	}
	
	private String getSerilizeFileName() {
		String agentHomePath = Util.getAgentHomePath();
		if (!agentHomePath.endsWith("\\"))
			agentHomePath += "\\";

		String fileName = agentHomePath + "BIN\\JobQueue\\D2D\\" + shrmemid
				+ ".xml";
		return fileName;
	}

	public void serializeToDisk() {
		
		BaseJobSerilizeContext context = new BaseJobSerilizeContext();
		context.setPlanUUID(this.planUUID);
		context.setRpsDataStoreName(this.rpsDataStoreName);
		context.setRpsDataStoreUUID(this.rpsDataStoreUUID);
		context.setRpsPolicyUUID(this.rpsPolicyUUID);
		try {
			
			CommonUtils.serializedToDisk(context, getSerilizeFileName());
		} catch (ClassNotFoundException | FileNotFoundException | JAXBException e) {
			logger.error(e);
		}
	}

	public void removeJobSerializationFromDisk() {
		if (!ServiceContext.getInstance().isServiceStoped()) {
			File f = new File(getSerilizeFileName());

			if (f.exists())
				f.delete();
		}

	}

	public void resumeSerialization() {
		try {
			BaseJobSerilizeContext context = CommonUtils.deSerializedFromDisk(
					getSerilizeFileName(), BaseJobSerilizeContext.class);

			this.rpsDataStoreName = context.getRpsDataStoreName();
			this.rpsDataStoreUUID = context.getRpsDataStoreUUID();
			this.rpsPolicyUUID = context.getRpsPolicyUUID();
			this.planUUID = context.getPlanUUID();
		} catch (FileNotFoundException | JAXBException e) {
			logger.error(e);
		}
	}
	
	public String getRPSName() {
		if (rpsHost == null)
   			return null;
   		
		return rpsHost.getRhostname();
		
	}

	public boolean isMaunal() {
		return isMaunal;
	}
}
	
