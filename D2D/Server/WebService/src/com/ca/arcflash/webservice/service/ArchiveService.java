package com.ca.arcflash.webservice.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.locks.Lock;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.BucketNameEncoder;
import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.rps.webservice.data.RpsArchiveConfiguationWrapper;
import com.ca.arcflash.service.common.WebServiceErrorMessages;
import com.ca.arcflash.service.jni.model.JActLogDetails;
import com.ca.arcflash.service.jni.model.JActLogDetails.JActProductType;
import com.ca.arcflash.webservice.ArchiveToCloudErrors;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.common.HostNameUtil;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.NextArchiveScheduleEvent;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationConstants;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDiskDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveFileItem;
import com.ca.arcflash.webservice.data.archive.ArchiveJobInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.ArchiveScheduleStatus;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceInfoConfiguration;
import com.ca.arcflash.webservice.data.archive.CloudProviderInfo;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupVolumes;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.job.rps.ArchiveJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.model.JObjRet;
import com.ca.arcflash.webservice.scheduler.AdvancedScheduleTrigger;
import com.ca.arcflash.webservice.scheduler.ArchiveBackupJob;
import com.ca.arcflash.webservice.scheduler.BaseArchiveJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.DSTWeeklyTrigger;
import com.ca.arcflash.webservice.service.internal.ArchiveConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.ArchiveJobConverter;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.service.validator.ArchiveConfigurationValidator;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.util.ArchiveUtil;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class ArchiveService extends BaseService implements IJobDependency {
	private static final Logger logger = Logger.getLogger(ArchiveService.class);
	private static final ArchiveService archiveInstance = new ArchiveService();
	private ArchiveConfigurationXMLDAO archiveConfigurationXMLDAO = new ArchiveConfigurationXMLDAO();
	private ArchiveConfigurationValidator archiveConfigurationValidator = new ArchiveConfigurationValidator();
	private ArchiveConfiguration archiveConfiguration;
	private static final String volumeGuid = "\\\\?\\Volume{";

	public static final int EFST_FAT16 = 3;
	public static final int EFST_FAT32 = 4;
	public static final int EVLT_RAID5 = 5;
	
	private static final String bucketPrefix_R16_5 = "d2d-filecopy-";// it is for 16.5 
	private static final String bucketPrefix_R16 = "d2dfilecopy-";// it is for 16
	private static String bucketPrefix_F2C = "d2dfc-v2-";
	private static final String bucketPrefix_ARCServe = "arcserve-"; // it is for 17 or above
	
	public final static int CATALOG_JOBSTATUS_FINISHED = 1;

	public static int NTFS = 2;
	public static int REFS = 8;
	
	private Scheduler scheduler;

	//private static final JobMonitor archiveBackupJobMonitor = new JobMonitor(-1);

	private Object lock = new Object();
	private List<ArchiveJobInfo> listofArchiveJobs = null;
	protected ArchiveJobConverter jobConverter = new ArchiveJobConverter();
	public boolean bRequestedArchiveJobsInfo = false;
	
	private Set<String> jobNames = new HashSet<String>();
	private Set<String> jobNames4Adv = new HashSet<String>();
	private volatile boolean submitFileCopy = false;	

	private static int LAST_WEEK = 0;
	private static int FIRST_WEEK = 1;

	public static ArchiveService getInstance(){
		return archiveInstance;
	}

	private ArchiveService(){
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}
	
	public void destory(){
		try {
			if(scheduler != null && scheduler.isStarted()){
				scheduler.shutdown();
			}
		} catch (SchedulerException e) {
			logger.warn("destory error, Fail to stop Quartz Scheduler: " + e.getMessage());
		}
	}

	public Scheduler getScheduler(){
		return scheduler;
	}
	
	public synchronized void addMakeUpJobNames(String jobName) {
		jobNames.add(jobName);			
	}
	
	public synchronized void deleteMakeupSchedules()
	{   
//		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
//		nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,new String[]{WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_FILECOPY_DELETE),"","","",""});
		try{			
			for(String jobName : jobNames) {
				scheduler.deleteJob(new JobKey(jobName, BaseService.JOB_GROUP_ARCHIVE_MAKEUP_NAME));
			}
			jobNames.clear();
		 }
		catch(Exception e) {
			logger.error("Failed to delete file copy makeup Schedules", e);
		}
	}
	
	private void addJobNames(String name){
		jobNames4Adv.add(name);
	}
	
	
	private void cleanArchiveSchedule(){
		for(String name : jobNames4Adv){
		
			try {
				getScheduler().deleteJob(new JobKey(name, JOB_GROUP_ARCHIVE_BACKUP_NAME));
			} catch (SchedulerException e) {
				logger.error("Failed to remove job " + name);
			}
		}
		jobNames4Adv.clear();
		logger.debug("The old file copy schedule has been removed");
	}

	public ArchiveConfiguration getArchiveConfiguration() throws ServiceException{
		logger.debug("getArchiveConfiguration - start");

		if (BackupService.getInstance().getBackupConfiguration()==null){
			logger.error("There is no backup configuration, return error code");
			throw generateAxisFault(FlashServiceErrorCode.Backup_NoBackupConfiguration);
		}
		try {
			synchronized (lock) {

				WindowsRegistry registry = new WindowsRegistry();
				int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
				String sAuto = registry.getValue(handle, "Auto");
				registry.closeKey(handle);
				registry = null;
				boolean bAutoEnabled = false;
				if(sAuto != null)
				{ 
					if(sAuto.equalsIgnoreCase("0") || sAuto.equalsIgnoreCase("1"))				
					bAutoEnabled = Integer.parseInt(sAuto) == 1 ? true : false;			
					else
					bAutoEnabled = false;
				}
				if ((archiveConfiguration == null) || bAutoEnabled)
				{
					if (StringUtil.isExistingPath(ServiceContext.getInstance().getArchiveConfigurationFilePath()))
					{
						archiveConfiguration = null;
						archiveConfiguration = archiveConfigurationXMLDAO.get(ServiceContext.getInstance().getArchiveConfigurationFilePath());
					}
				}
			}

			logger.debug("getArchiveConfiguration - end");
			return archiveConfiguration;
		}
		catch (Throwable e) {
			logger.error("getArchiveConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public void clearCachedConfiguration()
	{
		archiveConfiguration = null;
	}

	public Volume[] getBackupVolumeDetails()
	{
		List<Volume> selectedBackupVolumes = getSelectedBackupVolumes(null,null);
		
		if(selectedBackupVolumes == null) 
			return null;
		else
			return selectedBackupVolumes.toArray(new Volume[0]);
	}
	
	private List<Volume> getSelectedBackupVolumes(BackupVolumes in_backupVolumes,String in_backupDestination) {
		
		List<Volume> Volumes = null;
		BackupConfiguration backupConfig = null;
		try {
			backupConfig = BackupService.getInstance().getBackupConfiguration();
		
				if(backupConfig != null)
				{
					String backupDestination = in_backupDestination == null ? backupConfig.getDestination() : in_backupDestination;
					logger.debug("Backup Destination " + backupDestination);
					
					BackupVolumes backupVolumes;
					if(in_backupVolumes == null)
						backupVolumes = backupConfig.getBackupVolumes();
					else
						backupVolumes = in_backupVolumes;
					
					Volume[] volumesList = BrowserService.getInstance().getVolumes(true, backupConfig.getDestination(), backupConfig.getUserName(), backupConfig.getPassword());
	
					Volumes = new ArrayList<Volume>();
					for(int iIndex = 0;iIndex < volumesList.length;iIndex++)
					{
						boolean bAddVolume = true;
						Volume volume = volumesList[iIndex];
						switch(volume.getFsType())
						{
							case EFST_FAT16:
							case EFST_FAT32:
								bAddVolume = false;
								continue;
						}
	
						if(!backupVolumes.isFullMachine())
						{
							logger.debug("Full machine is not selected ");
							for (String backupVolume : backupVolumes.getVolumes()) {
								logger.debug("Comparing Backup Volume " + backupVolume + " with " + volume.getDisplayName() + "name " +volume.getName() );
								//backupVolume += "\\";
								if(backupVolume.compareToIgnoreCase(volume.getDisplayName()) == 0)
								{
									logger.debug("Backup volume matched with volume detail" + backupVolume);
									bAddVolume = true;
									break;
								}
								else
									bAddVolume = false;
							}
						}
						else
						{
							logger.debug("Full machine is selected ");
							if(backupDestination != null)
							{
								int iindex = backupDestination.indexOf(":");
	
								if(iindex != -1)
								{
									backupDestination = backupDestination.substring(0, iindex + 1);
									//backupDestination += "\\";
									logger.debug("backup destination volume name" + backupDestination);
									if(backupDestination.compareToIgnoreCase(volume.getDisplayName()) == 0)
									{
										logger.debug("backup destination matched with volume name");
										bAddVolume = false;
										continue;
									}
								}
							}
						}
	
						if(bAddVolume)
						{
							logger.debug("Volume added " + volume);
							Volumes.add(volume);
						}
					}
				}
				else
				{
					if(in_backupVolumes != null && !in_backupVolumes.isFullMachine())
					{
						for (String volume : in_backupVolumes.getVolumes()) {
							Volume vol = new Volume();
							vol.setName(volume+"\\");
							
							if(Volumes == null)
								Volumes = new ArrayList<Volume>();
							
							Volumes.add(vol);
						}
					}
				}
			} catch (ServiceException e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return Volumes;
	}
	
	public Volume[] getFATVolumesList() {
		
		BackupConfiguration backupConfig;
		List<Volume> Volumes = null;
		try {
			
		backupConfig = BackupService.getInstance().getBackupConfiguration();
		Volume[] volumesList = null;			
		if(backupConfig == null)	
			volumesList = BrowserService.getInstance().getVolumes(true, null, null, null);
		else
			volumesList = BrowserService.getInstance().getVolumes(true, backupConfig.getDestination(), backupConfig.getUserName(), backupConfig.getPassword());

		Volumes = new ArrayList<Volume>();
		for(int iIndex = 0;iIndex < volumesList.length;iIndex++)
		{
			boolean bAddVolume = false;
			Volume volume = volumesList[iIndex];
			switch(volume.getFsType())
			{
				case EFST_FAT16:
				case EFST_FAT32:
					bAddVolume = true;
					break;
			}
			//raid5 validation
			switch(volume.getLayOut())
			{
				case EVLT_RAID5:
					bAddVolume = true;
					break;
			}
			//USB drives validation.
			if(volume.getIsShow() == 0)
			{
				bAddVolume = true;
				
			}
			if(volume.getName().startsWith(volumeGuid) || volume.getName().length() > 3)
			{
				bAddVolume = true;
			}
			if(bAddVolume)
			{
				logger.debug("Volume added " + volume);
				Volumes.add(volume);
			}
		}
		
		} catch (ServiceException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		
		return Volumes.toArray(new Volume[0]);
	}
	
	// add for issue 141113
	public List<Volume> getRefsAndNTdedupVolumesList(){
		BackupConfiguration backupConfig;
		List<Volume> Volumes = null;
		try {
			
			backupConfig = BackupService.getInstance().getBackupConfiguration();
			Volume[] volumesList = null;			
			if(backupConfig == null)	
				volumesList = BrowserService.getInstance().getVolumes(true, null, null, null);
			else
				volumesList = BrowserService.getInstance().getVolumes(true, backupConfig.getDestination(), backupConfig.getUserName(), backupConfig.getPassword());

			Volumes = new ArrayList<Volume>();
			for(int iIndex = 0;iIndex < volumesList.length;iIndex++)
			{
				Volume volume = volumesList[iIndex];
				if(volume.getFsType()==REFS || volume.getFsType()==NTFS && (volume.getIsDeduped().equalsIgnoreCase("1")) ){
					logger.debug("REFS or  Volume NTFS Deduplication Volume added " + volume);
					Volumes.add(volume);
				}
			}
		
		} catch (ServiceException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return Volumes;
	}
	
	// add for issue 141113
	public void validateArchiveSourceIsRefsOrDedupeForEdge( ArchiveConfiguration in_archiveConfig ) throws ServiceException
	{
		if(in_archiveConfig.isbArchiveAfterBackup() || (in_archiveConfig.getArchiveSources() != null && in_archiveConfig.getArchiveSources().length != 0))
		{	
			List<Volume> refsOrNtdedupeVolumes = getRefsAndNTdedupVolumesList();
			
			List<String> refsAndNtdedupeSrc = new ArrayList<String>();
			
			if(refsOrNtdedupeVolumes==null)
			{
				return;
			}
			
			for(int i=0 ; i < in_archiveConfig.getArchiveSources().length; i++){
				ArchiveSourceInfoConfiguration archiveSource = in_archiveConfig.getArchiveSources()[i];
				int iIndex = archiveSource.getStrSourcePath().indexOf(":");
				String source = archiveSource.getStrSourcePath();
				if(iIndex != -1)
				{
					source = archiveSource.getStrSourcePath().substring(0, iIndex + 1);
					source += "\\";		
					for (Volume volume : refsOrNtdedupeVolumes) {
						if(source.compareToIgnoreCase(volume.getName()) == 0)
						{
							refsAndNtdedupeSrc.add(source.substring(0, iIndex));
							break;
						}
					}
				}
			}
			
			if(refsAndNtdedupeSrc.size()==1)
				throw new ServiceException(refsAndNtdedupeSrc.get(0),FlashServiceErrorCode.ArchiveConfig_Src_NO_Refs_Or_NTdedupe);
			else if(refsAndNtdedupeSrc.size()>1){
				String temp = "";
				String  exceptionMsg = "";
				for(String refs : refsAndNtdedupeSrc)
					temp+=refs;
				for (int j = 0; j < temp.length(); j++) {
					if(j==temp.length()-1)
						exceptionMsg += temp.substring(j).toUpperCase();
					else
						exceptionMsg += temp.substring(j,j+1).toUpperCase()+",";
				}
				throw new ServiceException(exceptionMsg,FlashServiceErrorCode.ArchiveConfig_Src_NO_Refs_Or_NTdedupe);
			}
		}
	}
	
	public void validateArchiveSource( ArchiveConfiguration in_archiveConfig ) throws Exception
	{
		if(in_archiveConfig.isbArchiveAfterBackup() || (in_archiveConfig.getArchiveSources() != null && in_archiveConfig.getArchiveSources().length != 0))
		{	
			if(ValidateSourceAgainstBackupVolumes(in_archiveConfig) == false)
				throw new ServiceException(FlashServiceErrorCode.ArchiveConfig_ERR_ARCHIVESOURCE_INVALID);
		}
	}
	
	private void validateArchiveSourceFile( ArchiveConfiguration in_archiveConfig ) throws ServiceException
	{
		if(in_archiveConfig.isbArchiveAfterBackup() || (in_archiveConfig.getArchiveSources() != null && in_archiveConfig.getArchiveSources().length != 0))
		{	
			if(in_archiveConfig.getArchiveSources() == null)
				return;
			
			//wanqi06
			StringBuffer sb = new StringBuffer();
			boolean flag = false;
			boolean isFirst = true;
			
			ArchiveSourceInfoConfiguration[] asic =  in_archiveConfig.getArchiveSources();
			for(int i = 0; i < asic.length; i ++) {
				String sourcePath = asic[i].getStrSourcePath();
				File file = new File(sourcePath);
				boolean exists = file.exists();
				if(!exists){
					flag = true;
					if(isFirst){
						sb.append(sourcePath);
						isFirst = false;
					}
					else {
						sb.append(", ");
						sb.append(sourcePath);
					}
				}
			}
			if(flag) {
				throw new ServiceException(WebServiceMessages.getResource("SourcePathNotExistMessage",sb.toString()), 
						FlashServiceErrorCode.Common_General_Message);
			}
		}
	}
	
	public String getSymbolicLinkActualPath(String sourcePath) throws Exception
	{
		return this.getNativeFacade().getSymbolicLinkActualPath(sourcePath);
	}
	
	public void validateEncryptionSettings( ArchiveConfiguration in_archiveConfig ) throws Exception
	{	
		ArchiveJobScript result = jobConverter.convertToArchive(in_archiveConfig, BaseArchiveJob.Job_Type_Archive, ServiceContext.getInstance().getLocalMachineName(),0,"","");
	    ArrayList errorList;
		try {
			errorList = this.getNativeFacade().validateEncryptionSettings(
					result);
		} catch (Exception e) {
			throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_ProcessingError);
		}
	    long Errorcode = Long.parseLong(errorList.get(0).toString());	 
	    long CCIErrorCode = Long.parseLong(errorList.get(1).toString());
	    
	    if(Errorcode == 0)
		{
			if(CCIErrorCode != 0 )
			{
				if(CCIErrorCode == 424) //CCI internal error code
				     throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_PasswordDoesnotMatch);
				else if(CCIErrorCode == 601)
				     throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_NoDiskSpace);				
				else if(CCIErrorCode == 420)
					 throw new ServiceException(FlashServiceErrorCode.REQ_TIME_TOOSKEWED);
				else if(CCIErrorCode == 602)
					 throw new ServiceException(FlashServiceErrorCode.SESSION_CREDENTIAL_CONFLICT);
				else if(CCIErrorCode == 603)
					 throw new ServiceException(FlashServiceErrorCode.BAD_USERNAME);
				else if(CCIErrorCode == 604)
					 throw new ServiceException(FlashServiceErrorCode.INVALID_PASSWORD);
				else if(CCIErrorCode == 605)
					 throw new ServiceException(FlashServiceErrorCode.NO_NETWORK);
				else
				 throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_CCIInternalError);
				
			}
		}
	    else if(Errorcode == -1)
		{
	    	if(CCIErrorCode == 601)
			     throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_NoDiskSpace);
			throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_ProcessingError);
		}
		else if(Errorcode == 1)
		{
			if(CCIErrorCode == 0)
			{
				throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_AlreayEncrypted);
			}
			else if(CCIErrorCode == 1)
			{
				throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_NotEncryptedBefore);
			}
				
		}
		
	}

	public long validateArchiveConfiguration(ArchiveConfiguration in_archiveConfig) throws Exception{
		logger.debug("validateArchiveConfiguration - start");
		String newArchiveConfDetails = null;
		String oldArchiveConfDetails = null;
		ArchiveCloudDestInfo cloudinfo = null;
		//validate archive source
		//madra04
		if(in_archiveConfig.isbArchiveAfterBackup())
		{
			validateArchiveSource( in_archiveConfig );
			validateArchiveSourceFile(in_archiveConfig);
			//don't check license on GUI
//			String license = this.getD2D2DLicenseCom(in_archiveConfig.isbArchiveToCloud(),in_archiveConfig.isbEncryption());
//			if(!license.isEmpty()) {
//				throw new ServiceException(license, FlashServiceErrorCode.Common_License_Failure);
//			}

		//Validate archive destination
		if(in_archiveConfig.isbArchiveToDrive() && in_archiveConfig.getStrArchiveToDrivePath() != null && in_archiveConfig.getStrArchiveToDrivePath().length() != 0)
		{
			
			try {
				
				//validate the archive configuration
				int pathMaxWithoutHostName = archiveConfigurationValidator.Validate(in_archiveConfig);				
				String originalDest = in_archiveConfig.getStrArchiveToDrivePath();				
				
				InetAddress localMachine = InetAddress.getLocalHost();				
				String hostName = localMachine.getHostName();
				String destination = originalDest;
				if(destination != null && (!destination.endsWith(hostName)))
				{
				if (hostName != null && hostName.trim().length() > 0) {
					if (destination.endsWith("\\") || destination.endsWith("/")) {
						destination += hostName;
					} else {
						destination += "\\" + hostName;
					}
				}}
				if(!StringUtil.isEmptyOrNull(destination) &&  !StringUtil.isEmptyOrNull(originalDest)
					&& destination.length() > originalDest.length()) {
					//int maxLength = pathMaxWithoutHostName - ArchiveConfigurationValidator.WINDOWS_HOST_NAME_MAX_LENGTH;
					int backslash = 1;
					if(originalDest.endsWith("\\") || originalDest.endsWith("/")){
						backslash = 0;
					}
					if(destination.length() > pathMaxWithoutHostName + backslash)
						archiveConfigurationValidator.generatePathExeedLimitException(pathMaxWithoutHostName);
				}
                
				String dest = null;
				dest = appendHostNameIfNeeded(originalDest, null, in_archiveConfig.getStrArchiveDestinationUserName(), in_archiveConfig.getStrArchiveDestinationPassword());
				
				//validating whether same destination is used for archive
				BackupConfiguration BackupConfig = BackupService.getInstance().getBackupConfiguration();
				String strBackupdest = BackupConfig != null ? BackupConfig.getDestination() : null;
				if(strBackupdest != null)
				{
					if(dest.compareToIgnoreCase(strBackupdest) == 0)
					{
						throw new ServiceException(FlashServiceErrorCode.ArchiveConfig_ERR_DEST_SAME_BACKUPDEST);
					}
				}
				//validating done

				in_archiveConfig.setStrArchiveToDrivePath(dest);

			} catch (ServiceException ex) {
				
				if((ex.getErrorCode().compareToIgnoreCase("17179869199") == 0))
				{
					throw new ServiceException(ex.getMessage(),FlashServiceErrorCode.ArchiveConfig_CannotAccessDestination);
				}
				if((ex.getErrorCode().compareToIgnoreCase("17179869201") == 0))
				{
					if(StringUtil.isEmptyOrNull(ex.getMessage()))
						throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_NoDiskSpace);
					throw new ServiceException(ex.getMessage(),FlashServiceErrorCode.ArchiveConfig_CannotAccessDestination);
				}

				logger.error(ex.getMessage(), ex);
				throw ex;
			}

		}
		if(isManagedByEdge())			
		{			
			long isBucketCreated = 0;
			  if(in_archiveConfig.isbArchiveToCloud() && (in_archiveConfig.getCloudConfig()!=null) && (in_archiveConfig.getCloudConfig().getcloudBucketName() != null))
			  {
				 adjustBucketName(in_archiveConfig.getCloudConfig());
			     
			     if(in_archiveConfig.getCloudConfig().getcloudBucketName().length() > 63 )
			     {
			    	 if(in_archiveConfig.getCloudConfig().getcloudVendorType() == 0 || in_archiveConfig.getCloudConfig().getcloudVendorType() == 5)
						  throw new ServiceException(ArchiveToCloudErrors.getMessage("Max_length_err"));
					  else
						  throw new ServiceException(ArchiveToCloudErrors.getMessage("Max_length_err_Azure"));
			     }
			     
				 isBucketCreated = handleEdgeProcess(in_archiveConfig);
				 
				  if(isBucketCreated != 0)
				  {
					  if(isBucketCreated != 419 && isBucketCreated !=500 )
					  {
						  if(in_archiveConfig.getCloudConfig().getcloudVendorType() == 0 || in_archiveConfig.getCloudConfig().getcloudVendorType() == 5)
							  throw new ServiceException(ArchiveToCloudErrors.getMessage("Error_"+isBucketCreated));
						  else
							  throw new ServiceException(ArchiveToCloudErrors.getMessage("AzError_"+isBucketCreated));
					  }
					  
				  }
			  }  
				validateArchiveSourceIsRefsOrDedupeForEdge(in_archiveConfig);			  
		}
		//new configuration
		/*if(in_archiveConfig != null)
		{
			if(in_archiveConfig.isbArchiveToDrive())
				newArchiveConfDetails = "0"+in_archiveConfig.getStrArchiveToDrivePath()+in_archiveConfig.isbEncryption()+in_archiveConfig.getEncryptionPassword();
			else
			{
				cloudinfo =	in_archiveConfig.getCloudConfig();
				newArchiveConfDetails = cloudinfo.getcloudVendorType()+cloudinfo.getcloudVendorURL()+cloudinfo.getcloudBucketName()+in_archiveConfig.isbEncryption()+in_archiveConfig.getEncryptionPassword();
			}
		}*/

			//old configuration
			//if(in_archiveConfig.isbArchiveAfterBackup())
			{
				 if((isEmptyDestinationConfig(archiveConfiguration) && !isEmptyDestinationConfig(in_archiveConfig)))			
					validateEncryptionSettings(in_archiveConfig);
				else if((!isEmptyDestinationConfig(archiveConfiguration) && !isEmptyDestinationConfig(in_archiveConfig)))
				{
					if(isConfigurationChanged(archiveConfiguration,in_archiveConfig))
						validateEncryptionSettings(in_archiveConfig);
				}
			}
		
		}
//		if(archiveConfiguration != null && in_archiveConfig!=null)
//		{
//			if(isConfigurationChanged(archiveConfiguration,in_archiveConfig))
//				validateEncryptionSettings(in_archiveConfig);
//		}
//			
//		{
//			
//			
//			
//			if(archiveConfiguration.isbArchiveToDrive())
//				oldArchiveConfDetails = "0"+archiveConfiguration.getStrArchiveToDrivePath()+archiveConfiguration.isbEncryption()+archiveConfiguration.getEncryptionPassword();
//			else
//			{
//				cloudinfo =	archiveConfiguration.getCloudConfig();
//				oldArchiveConfDetails = cloudinfo.getcloudVendorType()+cloudinfo.getcloudVendorURL()+cloudinfo.getcloudBucketName()+archiveConfiguration.isbEncryption()+archiveConfiguration.getEncryptionPassword();	
//			}
//		}		
//
//		if(newArchiveConfDetails != null  && !newArchiveConfDetails.equals(oldArchiveConfDetails))	
//			validateEncryptionSettings(in_archiveConfig);

		logger.debug("validateArchiveConfiguration - start");
		return 0;
	}
	
	
	private boolean isEmptyDestinationConfig(ArchiveConfiguration configuration)
	{
		boolean  isEmpty = true;
		
		if(configuration!=null)
		{
			if(configuration.isbArchiveToCloud())
			{
				if(configuration.getCloudConfig() != null)
				{
					if((configuration.getCloudConfig().getcloudBucketName()!=null)
						&& (configuration.getCloudConfig().getcloudVendorURL()!=null))
						isEmpty = false;
				}
			}
			if(configuration.isbArchiveToDrive())
			{
				if(configuration.getStrArchiveToDrivePath()!=null)
					isEmpty=false;
			}
		}	
				
		return isEmpty;
	}
	

	private boolean isConfigurationChanged(ArchiveConfiguration oldConfig, ArchiveConfiguration newConfig)
	{

		if(oldConfig!=null && newConfig!=null)
		{			
			if(isDestConfChanged(oldConfig,newConfig))
				return true;			
			if(oldConfig.isbEncryption() != newConfig.isbEncryption())
				return true;
			if(oldConfig.isbEncryption() == true && newConfig.isbEncryption() == true)
			{
				if(!newConfig.getEncryptionPassword().equals(oldConfig.getEncryptionPassword()))
				return true;
			}
			
			if(oldConfig.isbArchiveAfterBackup() != newConfig.isbArchiveAfterBackup())
			{
				return true;
			}
			
			
		}

		return false;
	}
	
	private boolean isDestConfChanged(ArchiveConfiguration oldConfig, ArchiveConfiguration newConfig)
	{
		if(oldConfig.isbArchiveToCloud() && newConfig.isbArchiveToCloud())
		{
			ArchiveCloudDestInfo oldCloudConfig = oldConfig.getCloudConfig();
			ArchiveCloudDestInfo newCloudConfig = newConfig.getCloudConfig();
			
			if(oldCloudConfig.getcloudVendorType() != newCloudConfig.getcloudVendorType())
				return true;
			
			if(!oldCloudConfig.getcloudVendorURL().equals(newCloudConfig.getcloudVendorURL()))
				return true;
			
			if(!oldCloudConfig.getcloudBucketName().equals(newCloudConfig.getcloudBucketName()))
				return true;				
		}
		
		else if(oldConfig.isbArchiveToDrive() && newConfig.isbArchiveToDrive())
		{
			if(!oldConfig.getStrArchiveToDrivePath().equals(newConfig.getStrArchiveToDrivePath()))
				return true;
		}
		else if((oldConfig.isbArchiveToDrive() && newConfig.isbArchiveToCloud()) 
				|| (oldConfig.isbArchiveToCloud() && newConfig.isbArchiveToDrive()) )
			return true;
		
		return false;
		
	}


	private boolean ValidateSourceAgainstBackupVolumes(ArchiveConfiguration inArchiveConfig) {

		List<Volume> backupVolumes = getSelectedBackupVolumes(inArchiveConfig.getBackupVolumes(),inArchiveConfig.getbackupDestination());
		
		boolean bValid = false;
		
		for (ArchiveSourceInfoConfiguration archiveSource : inArchiveConfig.getArchiveSources()) {
			int iIndex = archiveSource.getStrSourcePath().indexOf(":");
			String source = archiveSource.getStrSourcePath();
			bValid = false;
			
			if(iIndex != -1)
			{
				source = archiveSource.getStrSourcePath().substring(0, iIndex + 1);
				source += "\\";
				//if(backupVolumes != null)

				if(backupVolumes == null)
				{
					return true;
				}
				
				for (Volume volume : backupVolumes) {
					if(source.compareToIgnoreCase(volume.getName()) == 0)
					{
						//logger.info("backup destination matched with volume name");
						bValid = true;
						break;
					}
				}
				if(bValid == false) return bValid;
			}
		}

		return bValid;
	}
	
	public void adjustBucketName(ArchiveCloudDestInfo info) throws Exception {
		logger.info("Enter, display bucket name = " + info.getcloudBucketName() + ", encoded bucket name = " + info.getEncodedCloudBucketName());

		InetAddress localMachine = InetAddress.getLocalHost();
		String hostName = localMachine.getHostName();
		if(hostName!=null)
			hostName = hostName.toLowerCase();
		String bucketName = info.getcloudBucketName();
		if(bucketName.startsWith(bucketPrefix_R16)){
			/*in r16, we don't encode bucket name, that is to say, cloudBucketName=cloudEncodedBucketName=rawBucketName*/
			info.setEncodedCloudBucketName(info.getcloudBucketName());
		}else if(bucketName.startsWith(bucketPrefix_F2C)){
			reEncodeBucketName(info, bucketPrefix_F2C, hostName);
		}else  if(bucketName.startsWith(bucketPrefix_R16_5)){
			reEncodeBucketName(info, bucketPrefix_R16_5, hostName);
		}else if(bucketName.startsWith(bucketPrefix_ARCServe)){
			reEncodeBucketName(info, bucketPrefix_ARCServe, hostName);
		}else{//it is for CPM, the parameter bucket name is raw name
			if(checkIfUpgradeFromOldBuild()&&checkIfUseOldPrefix(bucketName,  hostName)){
				logger.info("Build is upgraded from lower version, so old cloud destination " + archiveConfiguration.getCloudConfig().getcloudBucketName()
						+ " should be used continuously. If want to use new prefix " + bucketPrefix_ARCServe 
						+ ", you can raname new bucket/container name, or remove archiveconfiguration.xml and resave settings.");
				info.setcloudBucketName(archiveConfiguration.getCloudConfig().getcloudBucketName());
				info.setEncodedCloudBucketName(archiveConfiguration.getCloudConfig().getEncodedCloudBucketName());
			}else{
				encodeBucketName(info, bucketPrefix_ARCServe, hostName);
			}
		}
		 
		logger.info("Leave, display bucket name = " + info.getcloudBucketName() + ", encoded bucket name = " + info.getEncodedCloudBucketName());
	}
	
	private boolean checkIfUseOldPrefix(String rawName, String hostName) {
		String oldBucketName = archiveConfiguration.getCloudConfig().getcloudBucketName();
		logger.info("The old cloud destination is " + oldBucketName);
		// check if rawName and hostname are changed
		String temp = hostName + "-" + rawName;
		if(oldBucketName.startsWith(bucketPrefix_R16)){
			return oldBucketName.substring(bucketPrefix_R16.length()).equalsIgnoreCase(temp);
		}else if(oldBucketName.startsWith(bucketPrefix_F2C)){
			return oldBucketName.substring(bucketPrefix_F2C.length()).equalsIgnoreCase(temp);
		}else if(oldBucketName.startsWith(bucketPrefix_R16_5)){
			return oldBucketName.substring(bucketPrefix_R16_5.length()).equalsIgnoreCase(temp);
		}else {
			return false;
		}
		
	}

	private boolean checkIfUpgradeFromOldBuild() {
		try {
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if (configuration == null)
				return false;
			ArchiveConfiguration archiveConfiguration = ArchiveService.getInstance().getArchiveConfiguration();
			if (archiveConfiguration == null||archiveConfiguration.getCloudConfig() == null)
				return false;
			/*in r16, we don't encode bucket name, that is to say, cloudBucketName=cloudEncodedBucketName=rawBucketName
			 *from r16.5, we add display bucket name.
			 */
			String bucketName = archiveConfiguration.getCloudConfig().getcloudBucketName();
			if(bucketName==null){
				return false;
			}else if(bucketName.startsWith(bucketPrefix_R16_5)){
				return true;
			}else if(bucketName.startsWith(bucketPrefix_R16)){
				return true;
			}else if(bucketName.startsWith(bucketPrefix_F2C)){
				return true;
			}else{
				return false;
			}
		} catch (ServiceException e) {
			logger.error("Failed to checkIfUpgradeFromOldBuild, suppose it is r17 build.", e);
			return false;
		}
	}
	
	private void reEncodeBucketName(ArchiveCloudDestInfo info, String prefix, String hostName) throws UnsupportedEncodingException, ServiceException {
		String rawName=extractRawNameWithoutPrefix(prefix, hostName, info.getcloudBucketName(), info.getcloudVendorType());
		setCloudBucketName(info, prefix, hostName, rawName);
	}

	private void encodeBucketName(ArchiveCloudDestInfo info, String prefix, String hostName) throws UnsupportedEncodingException, ServiceException {
		String rawName=info.getcloudBucketName();
		if(rawName==null||rawName.equals("")){
			throwInvalidNameServiceException(info.getcloudVendorType());
		}
		setCloudBucketName(info, prefix, hostName, rawName);
	}
	
	private void setCloudBucketName(ArchiveCloudDestInfo info, String prefix, String hostName, String rawName) throws UnsupportedEncodingException {
		info.setcloudBucketName(prefix + hostName + "-" + rawName);
		info.setEncodedCloudBucketName(prefix + BucketNameEncoder.encodeWithUTF8(hostName + "-" + rawName));
	}
	
	private String extractRawNameWithoutPrefix(String prefix, String hostName, String bucketName, long cloudVendorType) throws ServiceException {
		String prefix_d2d = prefix + hostName + "-";
		if(bucketName.length()<=prefix_d2d.length()){
			throwInvalidNameServiceException(cloudVendorType);
		}
		
		String prefix_temp= bucketName.substring(0, prefix_d2d.length());
		String result=null;
		if(prefix_d2d.equalsIgnoreCase(prefix_temp)){
			result = bucketName.substring(prefix_d2d.length());
		}else{
			throwInvalidNameServiceException(cloudVendorType);
		}
		return result;
	}

	private void throwInvalidNameServiceException(long cloudVendorType) throws ServiceException {
		if(cloudVendorType == 0 || cloudVendorType == 5)
			  throw new ServiceException(ArchiveToCloudErrors.getMessage("Error_415"));
		  else
			  throw new ServiceException(ArchiveToCloudErrors.getMessage("AzError_415"));
	}

	public long saveArchiveConfiguration(ArchiveConfiguration in_archiveConfig) throws Exception{
		logger.info("saveArchiveConfiguration - start, at " + new Date());		
		if(BackupService.getInstance().isBackupToRPS()){
			logger.info("is backup to RPS - return");
			return 0;
		}
        
		//check for edge . If its for the Edge Create bucket 
		if(isManagedByEdge() && in_archiveConfig.isbArchiveAfterBackup()) 
		{			
			long isBucketCreated = 0;
			  if(in_archiveConfig.isbArchiveToCloud() && (in_archiveConfig.getCloudConfig()!=null) && (in_archiveConfig.getCloudConfig().getcloudBucketName() != null))
			  {
				  adjustBucketName(in_archiveConfig.getCloudConfig());
			     
			     if(in_archiveConfig.getCloudConfig().getcloudBucketName().length() > 63 )
			     {
			    	 if(in_archiveConfig.getCloudConfig().getcloudVendorType() == 0 || in_archiveConfig.getCloudConfig().getcloudVendorType() == 5)
						  throw new ServiceException(ArchiveToCloudErrors.getMessage("Max_length_err"));
					  else
						  throw new ServiceException(ArchiveToCloudErrors.getMessage("Max_length_err_Azure"));
			     }
			     
				 isBucketCreated = handleEdgeProcess(in_archiveConfig);
				 
				  if(isBucketCreated != 0)
				  {
					  if(isBucketCreated != 419 && isBucketCreated !=500 )
					  {
						  if(in_archiveConfig.getCloudConfig().getcloudVendorType() == 0 || in_archiveConfig.getCloudConfig().getcloudVendorType() == 5)
							  throw new ServiceException(ArchiveToCloudErrors.getMessage("Error_"+isBucketCreated));
						  else
							  throw new ServiceException(ArchiveToCloudErrors.getMessage("AzError_"+isBucketCreated));
					  }
					  
				  }
			  }
		}
		
		BackupConfiguration backupConfig = BackupService.getInstance().getBackupConfiguration();
		
		checkCatalogPath(in_archiveConfig);
		
		if(in_archiveConfig.isbArchiveAfterBackup())
		{
			//validate advanced schedule
			//liude02
			if(in_archiveConfig.getAdvanceSchedule() != null){
				archiveConfigurationValidator.validateAdvanceSchedule(in_archiveConfig.getAdvanceSchedule(), true);
			}
//			if(!isManagedByEdge())
//			{
				validateArchiveConfiguration(in_archiveConfig);
//			}
			archiveConfigurationXMLDAO.Save(ServiceContext.getInstance()
							.getArchiveConfigurationFilePath(), in_archiveConfig);
			archiveConfiguration = in_archiveConfig;
	
			if(hasEnabledAdvSchedule(in_archiveConfig.getAdvanceSchedule()))
				this.createAdvanceSchedule(in_archiveConfig.getAdvanceSchedule());
		}else 
		{
			if((in_archiveConfig!=null) && (archiveConfiguration!=null))
			{
				archiveConfiguration.setbArchiveAfterBackup(in_archiveConfig.isbArchiveAfterBackup());
				archiveConfigurationXMLDAO.Save(ServiceContext.getInstance()
						.getArchiveConfigurationFilePath(), archiveConfiguration);
				
				this.getNativeFacade().deleteAllPendingFileCopyJobs(backupConfig.getDestination(),
						ArchiveUtil.getDomainName(backupConfig.getUserName()),
						ArchiveUtil.getUserName(backupConfig.getUserName()),
						backupConfig.getPassword() == null ? "" : backupConfig.getPassword());
			}
		}
		
		if(!in_archiveConfig.isbArchiveAfterBackup()){
			this.getNativeFacade().deleteLicError(CommonService.AFLIC_INFO_ID_D2D2D);
		} 		
		
		logger.info("saveArchiveConfiguration end");
		return 0;
	}

	
	public long removeArchiveConfiguration() {
		logger.info("removeArchiveConfiguration - start, at " + new Date());
		long ret = 0;
		String configFilePath = ServiceContext.getInstance()
				.getArchiveConfigurationFilePath();
		File configFile = new File(configFilePath);
		if (configFile.exists()) {
			boolean removed=false;
			try{
				removed=CommonUtil.tryDeleteFile(configFile);
			}
			catch(Exception e){
				logger.error("Error occur on removing configFile",e);
			}
			
			if (removed) {
				clearCachedConfiguration();
				cleanArchiveSchedule();
				deleteMakeupSchedules();
			} else {
				logger.error("fail to remove ArchiveConfiguration file");
				ret = -1;
			}

		}
		logger.info("removeArchiveConfiguration end");
		return ret;
	}
	
	
	public long handleEdgeProcess(ArchiveConfiguration in_archiveConfig)
	{
		long isBucketExits = 0;
		
		try {
			isBucketExits = ArchiveService.getInstance().verifyBucketName(
					in_archiveConfig.getCloudConfig());
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return isBucketExits;		
		
	}
	public String appendHostNameIfNeeded(String destination, String serverName, String userName, String password)
	throws ServiceException {
		JObjRet<String> retObj = this.getNativeFacade()
				.checkDestNeedHostName(destination, serverName, null, userName, password, true);

		logger.debug("JObjRet<String> - hostName:"
				+ retObj.getItem() + ", retCode:"
				+ retObj.getRetCode());

		if (retObj.getRetCode() == 0
				|| retObj.getRetCode() == BackupServiceErrorCode.WARN_FolderWithSIDExist) {
			String hostName = retObj.getItem();
			if (hostName != null && hostName.trim().length() > 0) {
				if (destination.endsWith("\\") || destination.endsWith("/")) {
					destination += hostName;
				} else {
					destination += "\\" + hostName;
				}
			}
		}

		logger.debug("dest" + destination);
		return destination;
	}
	final Object joblock = new Object();

	public Object getObjectLock()
	{
		return joblock;
	}

	public NextArchiveScheduleEvent getNextArchiveScheduleEvent() throws ServiceException{
		logger.debug("getNextScheduleEvent() - start");

		try{
			if (scheduler == null)
				return null;

			Trigger archiveTrigger = scheduler.getTrigger(new TriggerKey(TRIGGER_NAME_ARCHIVE_BACKUP, TRIGGER_GROUP_ARCHIVE_BACKUP));
			Date nextArchive = archiveTrigger==null? null:archiveTrigger.getNextFireTime();

			//NextArchiveScheduleEvent nextEvent = new NextArchiveScheduleEvent(nextArchive, BaseArchiveJob.Job_Type_Archive);
			NextArchiveScheduleEvent nextEvent = new NextArchiveScheduleEvent();
			nextEvent.setDate(nextArchive);
			nextEvent.setJobType(BaseArchiveJob.Job_Type_Archive);

			String strArchiveEvent = "N/A";
			ArchiveConfiguration config = getArchiveConfiguration();
			if((config != null) && (config.isbArchiveAfterBackup()))
			{
				if(listofArchiveJobs == null && (!bRequestedArchiveJobsInfo))
				{
					synchronized (joblock) {
						JArchiveJob archiveJob = new JArchiveJob();
						archiveJob.setScheduleType(ArchiveScheduleStatus.ScheduleAll);
						archiveJob.setbOnlyOneSession(false);

						listofArchiveJobs = GetArchiveJobsInfo(archiveJob);
						bRequestedArchiveJobsInfo = true;
						archiveJob = null;
					}
				}

				int iCount = 0;
				if(listofArchiveJobs != null)
				{
					for(int iIndex = listofArchiveJobs.size()-1;iIndex >=0 ; iIndex--)
					{
						if(listofArchiveJobs.get(iIndex).getarchiveJobStatus() != 0)
						{
							break;
						}
						iCount++;
					}
				}
				int iNBackups = config.getiArchiveAfterNBackups();
				int iNextEvent = iNBackups - iCount;

				switch(iNextEvent)
				{
				case 0:
					strArchiveEvent = WebServiceMessages.getResource("ArchiveJobReady2RunMessage");//"Archive job is ready to run";
					break;
				default:
					strArchiveEvent = WebServiceMessages.getResource("AfterNBackupsMessage",Integer.toString(iNextEvent));//"After " + Integer.toString(iNextEvent) + " backup(s)";
					break;
				}

				logger.debug(strArchiveEvent);
			}
			nextEvent.setarchiveEvent(strArchiveEvent);
			logger.debug("getNextArchiveScheduleEvent() - end");
			return nextEvent;
		}catch(Throwable e){
			logger.error("getNextArchiveScheduleEvent()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public boolean checkSubmitArchiveJob(JArchiveJob out_archiveJob) throws ServiceException{
		//check whether there is any archive job need run
		Lock lock = null;
		try {
			logger.info("In checkSubmitArchiveJob");
			archiveConfiguration = getArchiveConfiguration();
			if(archiveConfiguration == null)
			{
				logger.debug("Failed to check. Please configure the archive settings.");
				return false;
			}
		
			if(!archiveConfiguration.isbArchiveAfterBackup())
				return false;

			BackupConfiguration backupConfig = BackupService.getInstance().getBackupConfiguration();
			if(backupConfig == null)
			{
				logger.debug("Failed to check. Please configure the backup settings.");
				return false;
			}
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupConfig.getDestination());
			if(lock != null){
				lock.lock();
			}
			out_archiveJob.setbackupDestination(backupConfig.getDestination());
			out_archiveJob.setbackupDestinationDomain(ArchiveUtil.getDomainName(backupConfig.getUserName()));
			out_archiveJob.setbackupDestinationUsername(ArchiveUtil.getUserName(backupConfig.getUserName()));
			out_archiveJob.setbackupDestinationPassword(backupConfig.getPassword() == null ? "" : backupConfig.getPassword());
			out_archiveJob.setScheduleType(ArchiveScheduleStatus.ScheduleReady);
			out_archiveJob.setbOnlyOneSession(true);
			out_archiveJob.setD2dHostName(ServiceContext.getInstance().getLocalMachineName());
			out_archiveJob.setJobType(JobType.JOBTYPE_FILECOPY_BACKUP);

			getNativeFacade().CanArchiveJobBeSubmitted(out_archiveJob);

			if(out_archiveJob == null) return false;

			if(out_archiveJob.issubmitArchive())
			{
				logger.info("file copy job can be submitted for the session : " + out_archiveJob.getbackupSessionId());
				return true;
			}

		}
		catch(ArrayIndexOutOfBoundsException ARRAYEx)
		{
			logger.debug(ARRAYEx.getMessage());
		}
		catch (Throwable e) {
			logger.debug(e.getMessage());
			
		}finally {
			if(lock != null){
				lock.unlock();
			}
		}
		logger.info("there is no any file copy jobs can be submitted");
		return false;
	}

	public long archive(ArchiveJobScript jobScript){

		if (jobScript == null)
		{
			logger.error("archive Job script is empty. Not able to submit archive job");
			return 0;
		}

		try {
			logger.info("submitting jobscript to NativeFacade");
			return getNativeFacade().archive(jobScript);
		} catch (Throwable e) {
			
			logger.error("getNativeFacade().archive(jobScript) error");
			return 0;
		}
	}

	public long submitArchiveJob(String jobName, JArchiveJob in_archiveJobDetails) throws ServiceException{
		logger.debug("archive() - start");

		try {
			
			if(this.isArchiveBackupJobRunning()){
				logger.warn("Archive backup is already running, no need to schedule it again");
				JActLogDetails logDetails = new JActLogDetails();
				logDetails.setProductType(JActLogDetails.JActProductType.APT_D2D); // APT_D2D
				logDetails.setJobID(0);
				logDetails.setJobType(new Long(JobType.JOBTYPE_FILECOPY_BACKUP).intValue());
				logDetails.setJobMethod(0);
				logDetails.setLogLevel(new Long(Constants.AFRES_AFALOG_WARNING).intValue());
				logDetails.setIsVMInstance(true);
				String d2duuid = CommonService.getInstance().getNodeUUID();
				String hostname = HostNameUtil.getLocalHostName();
				logDetails.setAgentNodeID(d2duuid);
				logDetails.setSvrNodeName("");
				logDetails.setSvrNodeID("");
				logDetails.setAgentNodeName(hostname);
				logDetails.setSourceRPSID("");
				logDetails.setTargetRPSID("");
				logDetails.setDSUUID("");
				logDetails.setTargetDSUUID("");
				String msg = WebServiceErrorMessages.getServiceError(FlashServiceErrorCode.Common_OtherJobIsRunning, null);
				getNativeFacade().addLogActivityWithDetailsEx(logDetails, new Long(Constants.AFRES_AFJWBS_GENERAL).intValue(), new String[] { msg, "", "", "", "" });
				return 0;
			}
			
			JobDetail jobDetail;
			jobDetail = new JobDetailImpl(JOB_NAME_ARCHIVE_BACKUP,JOB_GROUP_ARCHIVE_BACKUP_NAME,ArchiveBackupJob.class);

			jobDetail.getJobDataMap().put("jobName", jobName);
			jobDetail.getJobDataMap().put(BaseService.JOB_TYPE, JobType.JOBTYPE_FILECOPY_BACKUP);
			jobDetail.getJobDataMap().put("BackupSessionPath", in_archiveJobDetails.getbackupSessionPath());
			jobDetail.getJobDataMap().put("BackupSessionId", in_archiveJobDetails.getbackupSessionId());
			jobDetail.getJobDataMap().put("BackupSessionGUID", in_archiveJobDetails.getBackupSessionGUID());
			
			logger.info("submitting the archive job for the session : "+in_archiveJobDetails.getbackupSessionId());
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(TRIGGER_NAME_ARCHIVE_BACKUP);
			scheduler.scheduleJob(jobDetail, trigger);

			logger.info("archive job submitted - end");
			return 0;
		} catch (Throwable e){
			logger.error("submitArchiveJob failed with error ", e);
			this.resetSubmitFileCopy();
			throw generateInternalErrorAxisFault();
		}
	}

	public boolean isArchiveBackupJobRunning(){

		boolean bRunning = false;
		try {
			bRunning = getNativeFacade().IsArchiveJobRunning();
		} catch (Throwable e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return bRunning;
	}

	public JobMonitor getArchiveBackupJobMonitor() {
		/*synchronized (archiveBackupJobMonitor) {
			if (archiveBackupJobMonitor.getId() == -1L)
				return null;

			return archiveBackupJobMonitor;*/
		JobMonitor jArchiveMonitor = JobMonitorService.getInstance().getJobMonitorInternal(String.valueOf(BaseArchiveJob.Job_Type_Archive), false);
		if(jArchiveMonitor != null && jArchiveMonitor.getJobId() != -1L)
			return jArchiveMonitor;
		else
			return null;
	}

	public JobMonitor getJobMonitorInternal() {
		JobMonitor jArchiveMonitor = JobMonitorService.getInstance().getJobMonitorInternal(String.valueOf(BaseArchiveJob.Job_Type_Archive), false);
		return jArchiveMonitor;
	}

	public boolean IsArchiveEnabled() {
		try {
			ArchiveConfiguration archiveConfig = getArchiveConfiguration();
			if(archiveConfig != null)
				return archiveConfig.isbArchiveAfterBackup();
		} catch (ServiceException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return false;
	}

	public String[] getArchivedVolumesList(String strArchiveDestination,
			String strUserName, String strPassword) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArchiveDestinationConfig getArchiveDestinationConfig() throws ServiceException{
		logger.debug("getArchiveDestinationConfig - start");

		ArchiveDestinationConfig archiveDestConfig = null;
		try {
				synchronized (lock) {

				ArchiveConfiguration archiveConfig = getArchiveConfiguration();

				archiveDestConfig = new ArchiveDestinationConfig();
				archiveDestConfig.setbArchiveToDrive(archiveConfig.isbArchiveToDrive());
				archiveDestConfig.setbArchiveToCloud(archiveConfig.isbArchiveToCloud());
				if(archiveConfig.isbArchiveToDrive())
				{
					archiveDestConfig.setStrArchiveToDrivePath(archiveConfig.getStrArchiveToDrivePath());
					archiveDestConfig.setStrArchiveDestinationUserName(archiveConfig.getStrArchiveDestinationUserName());
					archiveDestConfig.setStrArchiveDestinationPassword(archiveConfig.getStrArchiveDestinationPassword());
				}
				if(archiveConfig.isbArchiveToCloud())
				{
					archiveDestConfig.setCloudConfig(archiveConfig.getCloudConfig());
				}
				else
				{
					archiveDestConfig.setCloudConfig(null);
				}

			}

			logger.debug("getArchiveDestinationConfig - end");
			return archiveDestConfig;
		}
		catch (Exception e) {
			logger.error("getArchiveConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public ArchiveFileItem[] getArchivableFilesList(
			ArchiveSourceInfoConfiguration[] in_ArchiveSourceInfo) {

		ArchiveFileItem[] archiveFilesList = null;
		try {
			archiveConfigurationXMLDAO.saveSourceInfo(ServiceContext.getInstance()
					.getArchiveSourcePoliciesFilePath(), in_ArchiveSourceInfo);

 			List<ArchiveFileItem> filesList = null;

			getNativeFacade().getArchivableFilesInformation(ServiceContext.getInstance().getArchiveSourcePoliciesFilePath(), filesList);

			return ConvertListToArray(filesList);

		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		} catch (Throwable e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}

		return archiveFilesList;
	}

	private ArchiveFileItem[] ConvertListToArray(List<ArchiveFileItem> filesList) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ArchiveJobInfo> GetArchiveJobsInfo(JArchiveJob out_archiveJob) {
		//check whether there is any archive job need run
		Lock lock = null;
		try {
			//JArchiveJob archiveJob = new JArchiveJob();
			logger.debug("In GetArchiveJobsInfo");

			List<ArchiveJobInfo> listofArchiveJobs = null;

			BackupConfiguration backupConfig = BackupService.getInstance().getBackupConfiguration();
			if(backupConfig == null)
			{
				logger.debug("Failed to check. Please configure the backup settings.");
				return null;
			}
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupConfig.getDestination());
			if(lock != null){
				lock.lock();
			}
			out_archiveJob.setbackupDestination(backupConfig.getDestination());
			out_archiveJob.setbackupDestinationDomain(ArchiveUtil.getDomainName(backupConfig.getUserName()));
			out_archiveJob.setbackupDestinationUsername(ArchiveUtil.getUserName(backupConfig.getUserName()));
			out_archiveJob.setbackupDestinationPassword(backupConfig.getPassword() == null ? "" : backupConfig.getPassword());
			out_archiveJob.setD2dHostName(ServiceContext.getInstance().getLocalMachineName());
			out_archiveJob.setJobType(JobType.JOBTYPE_FILECOPY_BACKUP);

			listofArchiveJobs = getNativeFacade().GetArchiveJobsInfo(out_archiveJob);

			if(listofArchiveJobs == null)
			{
				logger.debug("no archive jobs information found. returning null");
				return null;
			}

			if(out_archiveJob.issubmitArchive())
			{
				logger.debug("Last archive job is successful");
				return listofArchiveJobs;
			}
		}
		catch(ArrayIndexOutOfBoundsException ARRAYEx)
		{
			logger.error(ARRAYEx.getMessage());
		}
		catch (Throwable e) {
			logger.error(e.getMessage());
			
		}finally {
			if(lock != null){
				lock.unlock();
			}
		}

		logger.debug("End GetArchiveJobsInfo");
		return null;
	}

	public JArchiveJob GetFinishedArchiveJobsInfo(JArchiveJob out_archiveJob) {
		//check whether there is any archive job need run
		Lock lock = null;
		try {
			//JArchiveJob archiveJob = new JArchiveJob();
			logger.info("In GetArchiveJobsStatus");

			BackupConfiguration backupConfig = BackupService.getInstance().getBackupConfiguration();
			if(backupConfig == null)
			{
				logger.debug("Failed to check. Please configure the backup settings.");
				return out_archiveJob;
			}
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupConfig.getDestination());
			if(lock != null){
				lock.lock();
			}
			out_archiveJob.setbackupDestination(backupConfig.getDestination());
			out_archiveJob.setbackupDestinationDomain(ArchiveUtil.getDomainName(backupConfig.getUserName()));
			out_archiveJob.setbackupDestinationUsername(ArchiveUtil.getUserName(backupConfig.getUserName()));
			out_archiveJob.setbackupDestinationPassword(backupConfig.getPassword() == null ? "" : backupConfig.getPassword());
			out_archiveJob.setScheduleType(ArchiveScheduleStatus.ScheduleFinished);
			out_archiveJob.setbOnlyOneSession(true);
			out_archiveJob.setD2dHostName(ServiceContext.getInstance().getLocalMachineName());
			out_archiveJob.setJobType(JobType.JOBTYPE_FILECOPY_BACKUP);

			getNativeFacade().CanArchiveJobBeSubmitted(out_archiveJob);

			if(out_archiveJob == null) return null;

			if(out_archiveJob.issubmitArchive())
			{
				logger.info("Last archive job is successful");
				return out_archiveJob;
			}
		}
		catch(ArrayIndexOutOfBoundsException ARRAYEx)
		{
			logger.debug(ARRAYEx.getMessage());
		}
		catch (Throwable e) {
			logger.debug(e.getMessage());
		
		}finally {
			if(lock != null){
				lock.unlock();
			}
		}

		logger.info("End GetArchiveJobsStatus");
		return out_archiveJob;
	}

	public long validateArchiveSource(ArchiveDiskDestInfo inArchiveDiskDestConfig) {
		File file= new File(inArchiveDiskDestConfig.getArchiveDiskDestPath());
		boolean exists = file.exists();
		if (!exists) {
			return 1;//source doesn't exist
		}
		else if(!file.isDirectory())
		{
			return 2;//Source is not a directory
		}
		else
		{
			return 0;//source directory exists
		}
	}

	public ArchiveConfiguration getInternalArchiveConfiguration() {
		if(archiveConfiguration == null)
		{
			try {
				archiveConfiguration = getArchiveConfiguration();
			} catch (ServiceException e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
			}
		}
		return archiveConfiguration;
	}

	public ArchiveConfiguration getArchiveConfigurationAlreadyDefined() {
		return archiveConfiguration;
	}

	public void setArchiveJobsInformation(List<ArchiveJobInfo> in_archiveJobsInfo)
	{
		//synchronized (joblock) {
			listofArchiveJobs = in_archiveJobsInfo;
		//}
	}

	public List<ArchiveJobInfo> getArchiveJobsInformation()
	{
		return listofArchiveJobs;
	}

	@Override
	public void update(Observable o, Object arg) {
		// disable plan
		try {
		    BackupConfiguration conf = BackupService.getInstance().getBackupConfiguration();
		    if (conf.isDisablePlan()) {
			    logger.info("The plan is disabled, do not run copy recover point job");
			    return;
		    }
		    
		    if(BackupService.getInstance().isBackupToRPS()){
		    	logger.debug("archive job will run on RPS");
		    	return;
		    }
		    submitArchiveJob();
		}
		catch (ServiceException e) {
			logger.error("Can not get backup configuration");
		}
		finally{
			submitFileCopy = false;		
		}		
	}

	//submit catalog dependency job or advance schedule
		public void submitArchiveJob(boolean isOnDemandFCP) {
			try {
				JArchiveJob archiveJobDetails = new JArchiveJob();
				logger.info("checking to submit archive job");
				if (ArchiveService.getInstance().checkSubmitArchiveJob(archiveJobDetails)) {
					logger.info("submitting archive job");
					if(hasMakeupTriggers()){
						deleteMakeupSchedules();
					}
					submitFileCopy = true;
					if(isOnDemandFCP && !this.isArchiveBackupJobRunning())
					  logPendingFileCopySessions();
					ArchiveService.getInstance().submitArchiveJob("archiveJob", archiveJobDetails);
				}
				else if(isOnDemandFCP){
					ArchiveService.getInstance().getNativeFacade().addLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,
							WebServiceMessages.getResource("fcNoNeedToRun"));
				}
			} catch (ServiceException e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
			}
		}
		
private void logPendingFileCopySessions() {
		//check whether there is any archive job need run
		Lock lock = null;
		try {

			BackupConfiguration backupConfig = BackupService.getInstance().getBackupConfiguration();
			if(backupConfig == null)
			{
				logger.debug("Failed to check. Please configure the backup settings.");
				return ;
			}
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupConfig.getDestination());
			if(lock != null){
				lock.lock();
			}
			JArchiveJob out_archiveJob = new JArchiveJob();
			out_archiveJob.setbackupDestination(backupConfig.getDestination());
			out_archiveJob.setbackupDestinationDomain(ArchiveUtil.getDomainName(backupConfig.getUserName()));
			out_archiveJob.setbackupDestinationUsername(ArchiveUtil.getUserName(backupConfig.getUserName()));
			out_archiveJob.setbackupDestinationPassword(backupConfig.getPassword() == null ? "" : backupConfig.getPassword());
			out_archiveJob.setD2dHostName(ServiceContext.getInstance().getLocalMachineName());
			out_archiveJob.setJobType(JobType.JOBTYPE_FILECOPY_BACKUP);
			out_archiveJob.setbOnlyOneSession(false);

			long pendingFileCopySessions = getNativeFacade().AFGetArchiveJobInfoCount(out_archiveJob) - 1;
			if(pendingFileCopySessions > 0) {
				JActLogDetails logDetails = new JActLogDetails();
				logDetails.setProductType(JActLogDetails.JActProductType.APT_D2D); // APT_D2D
				logDetails.setJobID(0);
				logDetails.setJobType(new Long(JobType.JOBTYPE_FILECOPY_BACKUP).intValue());
				logDetails.setJobMethod(0);
				logDetails.setLogLevel(new Long(Constants.AFRES_AFALOG_INFO).intValue());
				logDetails.setIsVMInstance(true);
				String d2duuid = CommonService.getInstance().getNodeUUID();
				String hostname = HostNameUtil.getLocalHostName();
				logDetails.setAgentNodeID(d2duuid);
				logDetails.setSvrNodeName("");
				logDetails.setSvrNodeID("");
				logDetails.setAgentNodeName(hostname);
				logDetails.setSourceRPSID("");
				logDetails.setTargetRPSID("");
				logDetails.setDSUUID("");
				logDetails.setTargetDSUUID("");
				String msg = WebServiceMessages.getResource("moreFileCopySessions",String.valueOf(pendingFileCopySessions));
				getNativeFacade().addLogActivityWithDetailsEx(logDetails, new Long(Constants.AFRES_AFJWBS_GENERAL).intValue(), new String[] { msg, "", "", "", "" });
			}
		}

		catch (Throwable e) {
			logger.error(e.getMessage());

		}finally {
			if(lock != null){
				lock.unlock();
			}
		}
	}
	
	//submit catalog dependency job or advance schedule
	public void submitArchiveJob() {
		try {
			ArchiveConfiguration archiveConfig = getArchiveConfiguration();
			if(archiveConfig != null && archiveConfig.isbArchiveAfterBackup()){
				if(!hasEnabledAdvSchedule(archiveConfig.getAdvanceSchedule())){
					submitArchiveJob(false);
				}else {
					if(isInArchiveTimeRange(archiveConfig.getAdvanceSchedule())){
						submitArchiveJob(false);
					} else {
						logger.info("Out of time range, the next File copy job will not run");
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("Can not get archive configuration");
		}
	}
	
	public boolean isSubmitFileCopy() {
		return submitFileCopy;
	}
	
	//The method is designed to be called by catalog service to reset submit file copy flag.
	public void resetSubmitFileCopy() {
		submitFileCopy = false;
	}

	//This Method will return the makeUp triggers.
	private  List<Trigger> getMakeUpTriggers(){
		List<Trigger> triggerList = new ArrayList<Trigger>();
		
		try {
			String[] triggerGroupNames = new String[]{
					BaseService.TRIGGER_GROUP_ARCHIVE_MAKEUP_NAME};
			for (String tirggerGroupname : triggerGroupNames) {
				String[] triggerNames = ScheduleUtils.getTriggerNames(scheduler,tirggerGroupname);
				if(triggerNames==null)
					continue;
				
				for (String triggerName : triggerNames) {
					Trigger trigger = scheduler.getTrigger(new TriggerKey(triggerName, tirggerGroupname));
					triggerList.add(trigger);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		
		return triggerList;
	}

	public long testConnection(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException{
		long connectionStatus;
		try {
			connectionStatus = getNativeFacade().testConnection(cloudDestInfo);
		} catch (ServiceException se) {
			throw se;
		}

		return connectionStatus;
	}

	public long verifyBucketName(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException{
		long isBucketExits;
		try {
			isBucketExits = getNativeFacade().verifyBucketName(cloudDestInfo);
		} catch (ServiceException se) {
			throw se;
		}
				return isBucketExits;
	}

	public String getRegionForBucket(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException{
		String region = null;
		try {
			region = getNativeFacade().getRegionForBucket(cloudDestInfo);
		} catch (ServiceException se) {
			throw se;
		}
		return region;
	}

	public String[] getCloudBuckets(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException{
		String[] bucketList = null;
		try {
			bucketList = getNativeFacade().getCloudBuckets(cloudDestInfo);
		} catch (ServiceException se) {
			throw se;
		}
		return bucketList;
	}

	public String[] getCloudRegions(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException{
		String[] bucketList = null;
		try {
			bucketList = getNativeFacade().getCloudRegions(cloudDestInfo);
		} catch (ServiceException se) {
			throw se;
		}
		return bucketList;
	}
	
	public CloudProviderInfo getCloudProviderInfo(long cloudProviderType)throws ServiceException {
		
		CloudProviderInfo cloudProviderInfo = null;
		try {
			cloudProviderInfo = getNativeFacade().getCloudProviderInfo(cloudProviderType);
			
		} catch (ServiceException se) {
			throw se;
		}
		return cloudProviderInfo;
	}
	
	public List<CloudProviderInfo>  getCloudProviderInfo()throws ServiceException {
		
		List<CloudProviderInfo> cloudProviderInfo = null;		
		try {
			//Get the Cloud vendor URLs from the XML ..if it not present in the XMl get default provider URL
			cloudProviderInfo = CommonService.getInstance().getCloudVendor();		
		} catch (Exception se) {
			return null;
		}
		return cloudProviderInfo;
	}
	
	public String GetArchiveDNSHostSID() {
		String hostSID = null;
		try {
			hostSID = getNativeFacade().GetArchiveDNSHostSID();
		} catch (Throwable e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return hostSID;
	}
	
	public String GetArchiveDNSHostName() {
		String hostName = null;
		try {
			hostName = getNativeFacade().GetArchiveDNSHostName();
		} catch (Throwable e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return hostName;
	}
	
	public ArchiveJobInfo getArchiveSummaryInfo() throws ServiceException {
		//retrieving last archive job info
		JArchiveJob archiveJob = new JArchiveJob();
		archiveJob.setScheduleType(ArchiveScheduleStatus.LastJobDetails);
		archiveJob.setbOnlyOneSession(true);
		logger.debug("reading archive job info");
		List<ArchiveJobInfo> archiveFinishedJobsInfoList = ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob);

		ArchiveJobInfo archiveInfo = null;
		if(archiveFinishedJobsInfoList != null)
		{
			logger.debug("archive job info found");
			archiveInfo = new ArchiveJobInfo();
			archiveInfo.setarchiveJobStatus(archiveFinishedJobsInfoList.get(0).getarchiveJobStatus());
			archiveInfo.setArchiveDataSize(archiveFinishedJobsInfoList.get(0).getArchiveDataSize());
			archiveInfo.setCopyDataSize(archiveFinishedJobsInfoList.get(0).getCopyDataSize());
			archiveInfo.setHour(archiveFinishedJobsInfoList.get(0).getHour());
			archiveInfo.setMin(archiveFinishedJobsInfoList.get(0).getMin());
			archiveInfo.setSec(archiveFinishedJobsInfoList.get(0).getSec());
			archiveInfo.setDay(archiveFinishedJobsInfoList.get(0).getDay());
			archiveInfo.setMonth(archiveFinishedJobsInfoList.get(0).getMonth());
			archiveInfo.setYear(archiveFinishedJobsInfoList.get(0).getYear());

			/*String strDay = Long.toString(archiveInfo.getDay());
			String strMonth = Long.toString(archiveInfo.getMonth());
			String strYear = Long.toString(archiveInfo.getYear());
			String strHour = archiveInfo.getHour() > 12 ? Long.toString(archiveInfo.getHour() - 12) : Long.toString(archiveInfo.getHour());
			String strMin = Long.toString(archiveInfo.getMin());
			String strSec = Long.toString(archiveInfo.getSec());

			String strDateTime = strMonth + "/" + strDay + "/" + strYear + " " + strHour + ":" + strMin + ":" + strSec;

			if(archiveInfo.getHour() < 12)
				strDateTime += " AM";
			else
				strDateTime += " PM";*/

			int iDay = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getDay()));
			int iMonth = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getMonth())) - 1;
			int iYear = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getYear()));
			int iHour = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getHour()));
			int iMin = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getMin()));
			int iSec = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getSec()));

			TimeZone timeZone = TimeZone.getDefault();
			java.util.Calendar cal = java.util.Calendar.getInstance(timeZone);
			cal.set(iYear,iMonth,iDay,iHour,iMin,iSec);
			//Date serverDate = BackupConverterUtil.string2Date(strDateTime1);
			//String serverDateString = BackupConverterUtil.dateToString(cal.getTime());
			String serverDateString =  StringUtil.date2String(cal.getTime());

			archiveInfo.setlastArchiveDateTime(serverDateString);
			/*java.util.Calendar cd = java.util.Calendar.getInstance();
			cd.set(java.util.Calendar.HOUR, Integer.parseInt(Long.toString(archiveInfo.getHour())));
			cd.set(java.util.Calendar.MINUTE, Integer.parseInt(Long.toString(archiveInfo.getMin())));
			cd.set(java.util.Calendar.SECOND, Integer.parseInt(Long.toString(archiveInfo.getSec())));
			cd.set(java.util.Calendar.YEAR, Integer.parseInt(Long.toString(archiveInfo.getYear())));
			cd.set(java.util.Calendar.DAY_OF_MONTH, Integer.parseInt(Long.toString(archiveInfo.getDay())));
			cd.set(java.util.Calendar.MONTH, Integer.parseInt(Long.toString(archiveInfo.getMonth())));
			cd.set(java.util.Calendar.MILLISECOND,0);*/
			//int year, int month, int date, int hour, int minute
			//java.util.Calendar cd = new java.util.GregorianCalendar(Integer.parseInt(Long.toString(archiveInfo.getYear()),Long.toString(archiveInfo.getMonth()),Long.toString(archiveInfo.getDay()),Long.toString(archiveInfo.getHour()),Long.toString(archiveInfo.getMin()),Long.toString(archiveInfo.getSec()));
			//java.util.Calendar cd = new java.util.GregorianCalendar((int)archiveInfo.getYear(),(int)archiveInfo.getMonth()-1,(int)archiveInfo.getDay(),(int)archiveInfo.getHour(),(int)archiveInfo.getMin(),(int)archiveInfo.getSec());

			archiveInfo.setbackupSessionPath(archiveFinishedJobsInfoList.get(0).getbackupSessionPath());
			archiveInfo.setbackupSessionId(archiveFinishedJobsInfoList.get(0).getbackupSessionId());
		}
		
		return archiveInfo;
	}

	public void InsertJobInfoToGlobalList(
			List<ArchiveJobInfo> archiveFinishedJobsInfoList) {
	
		logger.info("in InsertJobInfoToGlobalList");
		if(archiveFinishedJobsInfoList == null) return;
	
		if(listofArchiveJobs == null)
			listofArchiveJobs = new ArrayList<ArchiveJobInfo>();
		
		//synchronized (getObjectLock()) {
			for (ArchiveJobInfo archiveJobInfo : archiveFinishedJobsInfoList) {
				logger.info("inserting into list");
				listofArchiveJobs.add(archiveJobInfo);
			}
		//}
		logger.info("updating the list done.");
	}
	
	public String getD2D2DLicenseCom(boolean isToCloud,boolean encryption) throws ServiceException {
		LicInfo lic = this.getNativeFacade().AFGetLicenseEx(false);
		StringBuilder str = new StringBuilder();
		//Don't check license if destination is cloud
		if(!isToCloud){
			if(lic.getDwD2D2D() != 0) {
				str.append(WebServiceMessages.getResource("LicenseD2D2D"));
				str.append("<br/>");
			}
		}
		
		if(encryption && lic.getDwEncryption() != 0) {
			str.append(WebServiceMessages.getResource("LicenseEncryption", 
					WebServiceMessages.getResource("ArchiveJob")));
		}
		
		return str.toString();
	}
	public static boolean isManagedByEdge() {
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		String edgeWSDL = edgeReg.GetEdgeWSDL();
		if ( edgeWSDL != null && edgeWSDL.length() > 0 ) {
			return true;
		}

		return false;
	}
	
	public long archiveNow(ArchiveJobArg jobArg) throws ServiceException {
		if (handleErrorFromRPS(jobArg) == -1)
			return -1;
		
		if(jobArg.getJobType() == JobType.JOBTYPE_FILECOPY_BACKUP){
			return archiveBackupNow(jobArg);
		}else if(jobArg.getJobType() == JobType.JOBTYPE_FILECOPY_CATALOGSYNC){
			return ArchiveCatalogSyncService.getInstance().archveCatalogNow(jobArg);
		}else if(jobArg.getJobType() == JobType.JOBTYPE_FILECOPY_PURGE){
			return PurgeArchiveService.getInstance().archivePurgeNow(jobArg);
		}else if(jobArg.getJobType() == JobType.JOBTYPE_FILECOPY_RESTORE){
			return RestoreArchiveService.getInstance().archiveRestoreNow(jobArg);
		}else {
			logger.error("Wrong job type " + jobArg.getJobType());
			return -2;
		}
	}

	private long archiveBackupNow(ArchiveJobArg jobArg) throws ServiceException {
		logger.debug("archive() - start");

		try {
			if(this.isArchiveBackupJobRunning()){
				logger.warn("Archive backup is already running, no need to schedule it again");
				return -1;
			}
			
			JobDetailImpl jobDetail;
			jobDetail = new JobDetailImpl(jobArg.getJobDetailName(),jobArg.getJobDetailGroup() + Constants.RUN_NOW,ArchiveBackupJob.class);

			jobDetail.getJobDataMap().put("jobName", jobArg.getJobName());
			jobDetail.getJobDataMap().put(
					"BackupSessionPath",
					jobArg.getJobScript().getPAFNodeList().get(0).getPwszSessPath());
			jobDetail.getJobDataMap().put(
					"BackupSessionId",
					String.valueOf(jobArg.getJobScript().getPAFNodeList()
							.get(0).getUlSessNum()));
			jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);
			jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());
			
			logger.info("submitting the archive job ");
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(TRIGGER_NAME_ARCHIVE_BACKUP);
			scheduler.scheduleJob(jobDetail, trigger);

			logger.info("archive job submitted - end");
			return 0;
		} catch (Throwable e){
			logger.error("submitArchiveJob failed with error ", e);
			this.resetSubmitFileCopy();
			throw generateInternalErrorAxisFault();
		}
	}

	@Override
	public boolean needRun(JobDependencySource source) {
		try {
			if(source.getJobType() == JobType.JOBTYPE_CATALOG_FS
				&& source.getJobStatus() == JobStatus.JOBSTATUS_FINISHED){
								
				JArchiveJob archiveJobDetails = new JArchiveJob();
				if (ArchiveService.getInstance().checkSubmitArchiveJob(
						archiveJobDetails)) {
					return true;
				}
			}
		} catch (ServiceException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		
		return false;
	}
	
	public void createAdvanceSchedule(AdvanceSchedule advanceSchedule) throws SchedulerException{
		logger.info("create advanceSchedule - start");
		if(advanceSchedule == null){
			logger.info("No advanced schedule");
			return;
		}
		
		BackupConfiguration backupConfig = null;
		try {
			backupConfig = BackupService.getInstance().getBackupConfiguration();
			if(backupConfig == null)
				return;
		} catch (ServiceException e1) {
			logger.error("Faild to get backup configuration");
			return;
		}
		
		cleanArchiveSchedule();
		
		try {
			JobDetailImpl jobDetail = new JobDetailImpl("regular_" + JOB_NAME_ARCHIVE_BACKUP,
					JOB_GROUP_ARCHIVE_BACKUP_NAME, ArchiveBackupJob.class);
			jobDetail.setDurability(true);
			jobDetail.getJobDataMap().put(BaseService.JOB_TYPE, JobType.JOBTYPE_FILECOPY_BACKUP);
			addJobNames(jobDetail.getName());
			scheduler.addJob(jobDetail, false);
			
			Date startTime = advanceSchedule.getScheduleStartTime() > 0 ? new Date(advanceSchedule.getScheduleStartTime()) : new Date();			
			
			List<DailyScheduleDetailItem> dailySchedules = advanceSchedule.getDailyScheduleDetailItems();
			if(dailySchedules != null && dailySchedules.size() > 0){
				int triggerNumber = 1;
				for (DailyScheduleDetailItem dailySchedule : dailySchedules) {
					ArrayList<ScheduleDetailItem> scheduleDetailItems = dailySchedule.getScheduleDetailItems();
					if (scheduleDetailItems != null) {
						for (ScheduleDetailItem scheduleDetailItem : scheduleDetailItems) {
//							AbstractTrigger trigger = new AdvancedScheduleTrigger(TRIGGER_NAME_ARCHIVE_BACKUP + triggerNumber,
//									TRIGGER_GROUP_ARCHIVE_BACKUP, startTime,scheduleDetailItem, dailySchedule.getDayofWeek());
							AbstractTrigger trigger = new DSTWeeklyTrigger(scheduleDetailItem.getStartTime().getHour(), 
									scheduleDetailItem.getStartTime().getMinute(), scheduleDetailItem.getEndTime().getHour(), 
									scheduleDetailItem.getEndTime().getMinute(), dailySchedule.getDayofWeek());
							trigger.setName(TRIGGER_NAME_ARCHIVE_BACKUP + triggerNumber);
							triggerNumber++;
							trigger.setGroup(TRIGGER_GROUP_ARCHIVE_BACKUP);
							trigger.setJobName(jobDetail.getName());
							trigger.setJobGroup(jobDetail.getGroup());
//							trigger.setMisfireInstruction(AdvancedScheduleTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
							scheduler.scheduleJob(trigger);
						}
					}
				}
				logger.debug("custom schedule is created");
			}
			
			PeriodSchedule periodSchedule = advanceSchedule.getPeriodSchedule();
			if(periodSchedule != null){
				processPeriodSchedule(jobDetail,periodSchedule);
				logger.debug("period schedule is created");
			}
			logger.info("create advanceSchedule - end");
		} catch (SchedulerException se) {
			logger.error("Failed to schedule filecopy job for quartz error" + se.getMessage());
		}catch(Exception e) {
			logger.error("Failed to schedule filecopy job" + e.getMessage());
		}
	}
	
	public void createMakeupSchedule(RetryPolicy retryPolicy){
		if(hasMakeupTriggers()){
			deleteMakeupSchedules();
		}
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, retryPolicy.getTimeToWait());
		Date startDate = cal.getTime();
		trigger.setStartTime(startDate);
		trigger.setRepeatCount(retryPolicy.getMaxTimes() - 1);
		trigger.setRepeatInterval(retryPolicy.getTimeToWait()*ScheduleUtils.MILLISECONDS_IN_MINUTE);
		trigger.setName(ArchiveService.TRIGGER_NAME_ARCHIVE_BACKUP);
		trigger.setGroup(ArchiveService.TRIGGER_GROUP_ARCHIVE_MAKEUP_NAME);

		String jobName = ArchiveService.JOB_NAME_ARCHIVE_BACKUP+"_" + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME ;
		JobDetail jobDetail = new JobDetailImpl(jobName ,ArchiveService.JOB_GROUP_ARCHIVE_MAKEUP_NAME,ArchiveBackupJob.class);
		addMakeUpJobNames(jobName);
		jobDetail.getJobDataMap().put(BaseService.JOB_TYPE, JobType.JOBTYPE_FILECOPY_BACKUP);
		try {
			scheduler.scheduleJob(jobDetail, trigger);
			logger.info("re-schedule file copy job success");
		} catch (SchedulerException e) {
			logger.error("re-schedule file copy job error" + e.getMessage());
		}
	}
	
	public void processPeriodSchedule(JobDetailImpl archiveJobDetail, PeriodSchedule periodSchedule) throws SchedulerException, ParseException{
		EveryDaySchedule daySchedule = periodSchedule.getDaySchedule();		
		if (daySchedule != null && daySchedule.isEnabled()) {
			String triggerName = "daily_" + TRIGGER_NAME_ARCHIVE_BACKUP;
			processDaySchedule(archiveJobDetail, triggerName, TRIGGER_GROUP_ARCHIVE_BACKUP, daySchedule);
		}
		
		EveryWeekSchedule weekSchedule = periodSchedule.getWeekSchedule();
		if (weekSchedule != null && weekSchedule.isEnabled()) {
			String triggerName = "weekly_" + TRIGGER_NAME_ARCHIVE_BACKUP;
			processWeekSchedule(archiveJobDetail, triggerName, TRIGGER_GROUP_ARCHIVE_BACKUP, weekSchedule);
		}
		
		EveryMonthSchedule monthSchedule = periodSchedule.getMonthSchedule();
		if (monthSchedule != null && monthSchedule.isEnabled()) {
			String triggerName = "monthly" + TRIGGER_NAME_ARCHIVE_BACKUP;
			processMonthSchedule(archiveJobDetail, triggerName, TRIGGER_GROUP_ARCHIVE_BACKUP, monthSchedule);
		}
	}
	
	private void processDaySchedule(JobDetailImpl archiveJobDetail, String triggerName, String triggerGroup, EveryDaySchedule daySchedule) throws SchedulerException, ParseException{
		CronTriggerImpl dailyTrigger = null;
		
		String whichDays = "";
		boolean flag = true;
		String cronExpFormat = "";
		
		if(daySchedule.getDayEnabled()!=null && daySchedule.getDayEnabled().length==7){
			if(daySchedule.getDayEnabled()[0])
				whichDays = whichDays.concat(SUNDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[1])
				whichDays = whichDays.concat(MONDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[2])
				whichDays = whichDays.concat(TUESDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[3])
				whichDays = whichDays.concat(WEDNESDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[4])
				whichDays = whichDays.concat(THURSDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[5])
				whichDays = whichDays.concat(FRIDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[6])
				whichDays = whichDays.concat(SATURDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			
			if(whichDays.length()>0 && whichDays.charAt(whichDays.length() - 1) == ',')
				whichDays = whichDays.substring(0, whichDays.length() - 1);							
			if(flag)
				cronExpFormat = "0 %d %d * * ?";
			else
				cronExpFormat = "0 %d %d ? * " + whichDays;
		}					
		else
			cronExpFormat = "0 %d %d * * ?";

		String cronExp = String.format(cronExpFormat, daySchedule.getDayTime().getMinute(), daySchedule.getDayTime().getHour());
		dailyTrigger = new CronTriggerImpl(triggerName,triggerGroup,cronExp);
		dailyTrigger.setJobName(archiveJobDetail.getName());
		dailyTrigger.setJobGroup(archiveJobDetail.getGroup());
		scheduler.scheduleJob(dailyTrigger);
		addJobNames(archiveJobDetail.getName());
	}
	
	private void processWeekSchedule(JobDetailImpl archiveJobDetail, String triggerName, String triggerGroup, EveryWeekSchedule weekSchedule) throws SchedulerException, ParseException{
		CronTriggerImpl weeklyTrigger = null;
		
		String cronExpFormat = "0 %d %d ? * %d";
		String cronExp = String.format(cronExpFormat, weekSchedule.getDayTime().getMinute(), weekSchedule.getDayTime().getHour(),
				weekSchedule.getDayOfWeek());
		weeklyTrigger = new CronTriggerImpl(triggerName,triggerGroup,cronExp);
		weeklyTrigger.setJobName(archiveJobDetail.getName());
		weeklyTrigger.setJobGroup(archiveJobDetail.getGroup());
		scheduler.scheduleJob(weeklyTrigger);
		addJobNames(archiveJobDetail.getName());
	}

	private void processMonthSchedule(JobDetailImpl archiveJobDetail, String triggerName, String triggerGroup, EveryMonthSchedule monthSchedule) throws SchedulerException, ParseException{
		CronTriggerImpl monthlyTrigger = null;
		
		String cronExp = "";
		String cronExpFormat = "";
		if (monthSchedule.isDayOfMonthEnabled()) {
			if (monthSchedule.getDayOfMonth() == 32) {
				cronExpFormat = "0 %d %d L * ?";
				cronExp = String.format(cronExpFormat, monthSchedule.getDayTime().getMinute(), monthSchedule.getDayTime().getHour());
			} else {
				cronExpFormat = "0 %d %d %d * ?";
				cronExp = String.format(cronExpFormat, monthSchedule.getDayTime().getMinute(), monthSchedule.getDayTime().getHour(), 
						monthSchedule.getDayOfMonth());
			}
		} else {
			if (monthSchedule.getWeekNumOfMonth() == 0) { // last
				cronExpFormat = "0 %d %d ? * %dL";
				cronExp = String.format(cronExpFormat, monthSchedule.getDayTime().getMinute(), monthSchedule.getDayTime().getHour(), 
						monthSchedule.getWeekDayOfMonth());
			} else {
				cronExpFormat = "0 %d %d ? * %d#%d";
				cronExp = String.format(cronExpFormat, monthSchedule.getDayTime().getMinute(), monthSchedule.getDayTime().getHour(),
						monthSchedule.getWeekDayOfMonth(), monthSchedule.getWeekNumOfMonth());
			}
		}
		monthlyTrigger = new CronTriggerImpl(triggerName,triggerGroup,cronExp);
		monthlyTrigger.setJobName(archiveJobDetail.getName());
		monthlyTrigger.setJobGroup(archiveJobDetail.getGroup());
		scheduler.scheduleJob(monthlyTrigger);
		addJobNames(archiveJobDetail.getName());
	}
	
	private boolean isInArchiveTimeRange(AdvanceSchedule advanceSchedule) {
		if(advanceSchedule == null)
			return false;
		List<DailyScheduleDetailItem> dailySchedules = advanceSchedule.getDailyScheduleDetailItems();
		if (dailySchedules != null && dailySchedules.size() > 0){
			for (DailyScheduleDetailItem dailySchedule : dailySchedules) {
				ArrayList<ScheduleDetailItem> scheduleDetailItems = dailySchedule.getScheduleDetailItems();
				if (scheduleDetailItems != null && scheduleDetailItems.size() > 0) {
					Calendar now = Calendar.getInstance();
					int day = now.get(Calendar.DAY_OF_WEEK);
					if (day == dailySchedule.getDayofWeek()) {
					    for (ScheduleDetailItem scheduleDetailItem : scheduleDetailItems) {
						    int hour = now.get(Calendar.HOUR_OF_DAY);
						    int minute = now.get(Calendar.MINUTE);
						    DayTime dayTime = new DayTime(hour, minute);
						    if (dayTime.equals(scheduleDetailItem.getStartTime()) 
						    		|| (dayTime.after(scheduleDetailItem.getStartTime()) && dayTime.before(scheduleDetailItem.getEndTime())))
						    	return true;
					    }
					}
				}
			}
		}
		
		PeriodSchedule periodSchedule = advanceSchedule.getPeriodSchedule();
		if(periodSchedule == null)
			return false;			
		EveryMonthSchedule everyMonthSchedule = periodSchedule.getMonthSchedule();
		if(everyMonthSchedule == null || !everyMonthSchedule.isEnabled())
			return false;
		
		Calendar now = Calendar.getInstance();
		DayTime startTime = everyMonthSchedule.getDayTime();
		DayTime endTime = everyMonthSchedule.getEndTime();
		int hour = now.get(Calendar.HOUR_OF_DAY);
	    int minute = now.get(Calendar.MINUTE);
	    DayTime dayTime = new DayTime(hour, minute);
		
		if(everyMonthSchedule.isDayOfMonthEnabled()){
			int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
			int lastDayOfMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			if(dayOfMonth == everyMonthSchedule.getDayOfMonth() 
					|| (32 == everyMonthSchedule.getDayOfMonth() && lastDayOfMonth == dayOfMonth)){
				if (dayTime.equals(startTime) 
			    		|| (dayTime.after(startTime) && dayTime.before(endTime)))
				return true;
			}
				
		}else if(everyMonthSchedule.isWeekOfMonthEnabled()){
			int weekOfMonth = now.get(Calendar.WEEK_OF_MONTH);
			int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
			boolean isFirstWeekOfMonth = now.getActualMinimum(Calendar.WEEK_OF_MONTH) == weekOfMonth;
			boolean isLastWeekOfMonth = now.getActualMaximum(Calendar.WEEK_OF_MONTH) == weekOfMonth;
			if((LAST_WEEK == everyMonthSchedule.getWeekNumOfMonth() && isLastWeekOfMonth && dayOfWeek == everyMonthSchedule.getWeekDayOfMonth())
					|| (FIRST_WEEK == everyMonthSchedule.getWeekNumOfMonth() && isFirstWeekOfMonth && dayOfWeek == everyMonthSchedule.getWeekDayOfMonth())){					
			    if (dayTime.equals(startTime) 
			    		|| (dayTime.after(startTime) && dayTime.before(endTime)))
				return true;
			}
		}
		
		return false;
	}
	
	//archive job initialize when agent service restart
	public void configArchiveSchedule(){
		try {
			ArchiveConfiguration archiveConfig = getArchiveConfiguration();
			if(archiveConfig == null){
				logger.info("no need to configure schedule, because archive configuration is null");
				return;
			}
			if(hasEnabledAdvSchedule(archiveConfig.getAdvanceSchedule()))
				createAdvanceSchedule(archiveConfig.getAdvanceSchedule());
			
		} catch (ServiceException e) {
			logger.error("Failed to initialize archive job, message : " + e.getMessage());
		} catch (SchedulerException e) {
			logger.error("Failed to initialize archive job, message : " + e.getMessage());
		}
		
	}
	
	public boolean hasEnabledAdvSchedule(AdvanceSchedule advanceSchedule){
		if(advanceSchedule == null)
			return false;
		
		if((advanceSchedule.getDailyScheduleDetailItems() == null || advanceSchedule.getDailyScheduleDetailItems().size() <= 0)
				&& !advanceSchedule.getPeriodSchedule().getMonthSchedule().isEnabled())
			return false;
		
		return true;
	}
	
	private String getCatalogPath(){
		return this.getNativeFacade().getFileCopyCatalogPath(ServiceContext.getInstance().getLocalMachineName(), 0);
	}
	
	public void checkCatalogPath(ArchiveConfiguration archiveConfig){
		if(this.archiveConfiguration != null){
			if(!StringUtil.isEmptyOrNull(this.archiveConfiguration.getStrCatalogPath())){
				archiveConfig.setStrCatalogPath(this.archiveConfiguration.getStrCatalogPath());
			}
		}else{
			archiveConfig.setStrCatalogPath(getCatalogPath());
		}
	}
	
	public String getCatalogPath4AllArchiveJobs(){
		if(this.archiveConfiguration == null 
				|| StringUtil.isEmptyOrNull(this.archiveConfiguration.getStrCatalogPath()))
			return CommonUtil.D2DInstallPath;
		
		return this.archiveConfiguration.getStrCatalogPath();
		
	}
	
	public String getSessionPwdByGUID(String sessionGUID){
		if(StringUtil.isEmptyOrNull(sessionGUID))
			return "";
		String[] pwdStrings;
		try {
			pwdStrings = getNativeFacade().getSessionPasswordBySessionGuid(new String[]{sessionGUID});
			if (pwdStrings != null && pwdStrings.length > 0) {
				return StringUtil.isEmptyOrNull(pwdStrings[0]) ? "" : pwdStrings[0];
			}
		} catch (ServiceException e) {
			logger.error("get backup session password error : ", e);
		}
		return "";
	}
	
	public boolean hasMakeupTriggers(){
		return getMakeUpTriggers() != null && getMakeUpTriggers().size() > 0;
	}
	
	public boolean isMakeupJob(){
		return hasMakeupTriggers();
	}
	
	public List<RpsArchiveConfiguationWrapper> getRpsArchiveConfiguationSummary(){
		List<RpsArchiveConfiguationWrapper> result = new ArrayList<RpsArchiveConfiguationWrapper>();
		//perpare the Local File Copy and File Archive.
		RpsArchiveConfiguationWrapper local=new RpsArchiveConfiguationWrapper();
		local.setHost(null);
		ArchiveConfiguration config=null;
		try {
			config=getArchiveConfiguration();
			local.setFileCopyConfiguration(config);
			
		}  catch (Throwable e) {
			logger.error(e.getMessage(), e);

		}
		try {
		config= DeleteArchiveService.getInstance().getArchiveDelConfiguration();
		local.setFileArchiveConfiguration(config);
		}  catch (Throwable e) {	
			logger.error(e.getMessage(), e);
		}
		result.add(local);
		//querying ArchiveConfigurations from Console;
		EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
		if (edgeRegInfo == null){
			logger.debug("This node not manangered by Console");
			return result;
		}
		else {
			String policyUuid = edgeRegInfo.getPolicyUuids().get("Default");
			if (StringUtil.isEmptyOrNull(policyUuid)){
				logger.debug("Invalid Policy UUID");
				return result;
			}
			else {
				policyUuid = policyUuid.split(":")[0];
				String d2duuid = CommonService.getInstance().getNodeUUID();
				String hostname = HostNameUtil.getLocalHostName();
				String des = null;
				try {
					des = BackupService.getInstance().getBackupConfiguration().getDestination();
				} catch (ServiceException e) {
					logger.error("Fail to get Backup Configuration",e);
				}
				if (des != null) {
					int index = des.lastIndexOf("\\");
					des = des.substring(index + 1);
				} else {
					des = hostname + "[" + d2duuid + "]";
				}
				String wsdl = edgeRegInfo.getEdgeWSDL();
				IEdgeD2DService proxy = null;
				if (!StringUtil.isEmptyOrNull(wsdl)){
					try{
						proxy = com.ca.arcflash.webservice.toedge.WebServiceFactory
								.getEdgeService(wsdl, IEdgeD2DService.class);
						
					}catch(SOAPFaultException e){
						logger.error("Fail to get edge client",e);
					}		
				}
				else {
					logger.debug("Fail to get egde wsdl URL");
				}
				if (proxy != null) {
					List<RpsArchiveConfiguationWrapper> wrappers=null;
					
						try {
							proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
							wrappers = proxy.getRpsArchiveConfiguationSummary(policyUuid);
						} catch(Exception e){
							logger.error("Fail to validataUser",e);
						}
					if(wrappers!=null){
						for (RpsArchiveConfiguationWrapper wrapper : wrappers) {
							//ArchiveUtil.decryptRpsHost(wrapper.getHost());
							config = wrapper.getFileCopyConfiguration();
							if (config != null) {
								ArchiveUtil.updateStrDestination(config, des);
								ArchiveUtil.encodeCloudName(config, hostname);
								ArchiveUtil.decryptArchiveDestination(config);
								if(config.getStrCatalogPath()==null)config.setStrCatalogPath("");
							}
							config = wrapper.getFileArchiveConfiguration();
							if (config != null) {
								ArchiveUtil.updateStrDestination(config, des);
								ArchiveUtil.encodeCloudName(config, hostname);
								ArchiveUtil.decryptArchiveDestination(config);
								if(config.getStrCatalogPath()==null)config.setStrCatalogPath("");
							}
						}	
						result.addAll(wrappers);
					}

				}
			}
		}
		return result;
	}
	
	public long deleteAllPendingFileCopyJobs(String backupDestination,
			String domainName, String userName, String password){
		try {
			long ret = getNativeFacade().deleteAllPendingFileCopyJobs(backupDestination, domainName, userName, password);
			logger.info("Delete all pending file copy jobs from destination[" + backupDestination + "], ret is" + ret);
			return ret;
		} catch (ServiceException e) {
			logger.error("Failed to delete all pending file copy jobs from destination[" + backupDestination + "]");
			return -1;
		}
	}
	
}

