package com.ca.arcflash.ui.client.common;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.ca.arcflash.ha.model.ReplicationStatus;
import com.ca.arcflash.ha.model.VirtualMachineInfo;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.model.BackupScheduleIntervalUnitModel;
import com.ca.arcflash.ui.client.model.BackupStatusModel;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.CloudModel;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.ui.client.model.LogEntryType;
import com.ca.arcflash.ui.client.model.MergeJobMonitorModel;
import com.ca.arcflash.ui.client.model.MergeStatusModel;
import com.ca.arcflash.ui.client.model.VMStatusModel;
import com.ca.arcflash.ui.client.model.WeekModel;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.button.TextButton;

@SuppressWarnings("deprecation")
public class Utils {
	
	public static final NumberFormat number = NumberFormat.getFormat("0.00");
	public static final NumberFormat simpleNumberFormat = NumberFormat.getFormat("00");
	/*public static final DateTimeFormat dateFormat = 
			DateTimeFormat.getFormat(UIContext.dataFormat.timeDateFormat());
	public static final DateTimeFormat timeFormat = 
		DateTimeFormat.getFormat(UIContext.dataFormat.timeFormat());
	public static final DateTimeFormat shortTimeFormat = 
		DateTimeFormat.getFormat(UIContext.dataFormat.shortTimeFormat());
	public static final DateTimeFormat shortDateFormat = 
		DateTimeFormat.getFormat(UIContext.dataFormat.dateFormat());*/
	public static final Date maxDate = new Date(199,1,1);
	public static final Date minDate = new Date(80,1,1);
	//This is for convert server string to local date
	public static final DateTimeFormat serverDate = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
	public static final int EncryptionPwdLen = 23;
	
	private static final String busyImagePath = "images/gxt/icons/grid-loading.gif";
	public static final int NUM_MILLISECONDS_IN_DAY = 24 * 60 * 60000;

	//archive jobs status
	public final static int ScheduleNotApplicable = 0 ;// no need to run archive for this backup session.
	public final static int ArchiveJobReady = 1     ;   // archive needs run for this backup session, and this is is not submited to scheduler.
	public final static int ScheduleScheduled = 2   ;  // archive needs run for this backup session, and this is submited to scheduler
	public final static int ScheduleRunning = 3    ;   // archive needs run for this backup session, and this job is currently running.
	public final static int ArchiveJobFinished = 4    ;  // archive needs run for this backup session, and this job is Finished.
	public final static int ArchiveScheduleAll = 5    ;  // archive needs run for this backup session, and this job is Finished.
	public final static int ArchiveJobCancelled = 6;
	public final static int ArchiveJobFailed = 7;
	public final static int ArchiveJobIncomplete = 8;
	public final static int ArchiveJobCrashed = 9;
	
	public static final String ARCHIVE_MODE= "archive";
	public static final String RESTORE_MODE= "restore";
	
	public final static int LEAST_RECOMMENDED_HEIGHT = 768;
	public final static int LEAST_RECOMMENDED_WIDTH = 1024;
	
	public static final int UI_TYPE_D2D = 0;
	public static final int UI_TYPE_VSPHERE = 1;
	
	public static Map<String, String[]> connectionCache = null;
	
	public final static int MAKEUP_DST_STARTS = 2;
	
	public final static int FieldWidth=250;
	public final static String LabelWidth="200";
	public final static Double PROGRESS_VALUE_0 = 0.0026;
	
	public static Image getBusyImage() {
		return new Image(busyImagePath);
	}
	private static TimeZone serverTimeZone;
	
	public static String formatDate(Date date){
		return FormatUtil.getTimeDateFormat().format(date);
	}
	
	public static String formatTime(Date date){
		return FormatUtil.getTimeFormat().format(date);
	}
	
	public static String formatShorTime(Date date){
		return FormatUtil.getShortTimeFormat().format(date);
	}
	
	public static String formatDateToServerTime(Date date) {
		if(date == null)
			return "";
		if(serverTimeZone == null)
		  serverTimeZone = TimeZone.createTimeZone(getServerTimeZoneOffset());
	   return FormatUtil.getTimeDateFormat().format(date, serverTimeZone);
	}
	
	public static String formatDateToServerTime(Date date, long serverTimezoneOffset) {
		if(date == null)
			return "";		
		if(serverTimezoneOffset == 0) {
			serverTimezoneOffset = UIContext.serverVersionInfo.getTimeZoneOffset();
		}
		TimeZone serverTimeZone = TimeZone
				.createTimeZone((int) serverTimezoneOffset / (-1000 * 60));
	   return FormatUtil.getTimeDateFormat().format(date, serverTimeZone);
	}
	
	public static String formatTimeToServerTime(Date date)
	{
		if(date == null)
			return "";
		if(serverTimeZone == null)
		  serverTimeZone = TimeZone.createTimeZone(getServerTimeZoneOffset());
	   return FormatUtil.getTimeFormat().format(date, serverTimeZone);
	}

	private static int getServerTimeZoneOffset() {
		return UIContext.serverVersionInfo.getTimeZoneOffset()/(-1000*60);
	}
	
	/**
	 * Converts the local time to server time. 
	 * Note: This method is only used to set a server time in the component DateField, 
	 *       which makes the DateField look like that it is showing the server time. 
	 *       You can not set a time got from server directly to DateField to make it show time server
	 *       because date represent UTC time and is transformed to local time automatically. 
	 *       That is this method only makes the transformed time likes like the server time in textual
	 *       representation "yyyy-MM-dd HH:mm:ss".
	 * @param localDate localDate which shows the same textual representation with the server time.
	 * @return
	 */
	public static Date localTimeToServerTime(Date localDate) {
		long timeDiffLocalAndServer = localDate.getTimezoneOffset() * 60 * 1000
				+ UIContext.serverVersionInfo.getTimeZoneOffset();
		// make the date in client look like server time in text.
		Date serverDate = new Date(localDate.getTime() + timeDiffLocalAndServer);
		return serverDate;
    }
	
	public static Date localTimeToServerTime(Date localDate, long serverTimeZoneOffset) {
		if(serverTimeZoneOffset == 0 || serverTimeZoneOffset == -1) {
			return localTimeToServerTime(localDate);
		}
		
		long timeDiffLocalAndServer = localDate.getTimezoneOffset() * 60 * 1000
				+ serverTimeZoneOffset;
		// make the date in client look like server time in text.
		Date serverDate = new Date(localDate.getTime() + timeDiffLocalAndServer);
		return serverDate;
    }
	
	
	/**
	 * Converts the server time to local time. This method can only be used in pair with 
	 * <code>localTimeToServerTime</code>.  
	 * @see localTimeToServerTime
	 * 
	 * @param serverDate
	 * @return
	 */
	public static Date serverTimeToLocalTime(Date serverDate) {
		DateWrapper wrapper = new DateWrapper(serverDate);
		long timeDiffLocalAndServer = (- new Date().getTimezoneOffset()*60*1000 - UIContext.serverVersionInfo.getTimeZoneOffset() ); 
		wrapper = wrapper.addMillis((int)timeDiffLocalAndServer);
		return wrapper.asDate();
	}
	
	public static Date serverTimeToLocalTime(Date serverDate, long serverTimeZoneOffset) {
		if(serverTimeZoneOffset == 0 || serverTimeZoneOffset == -1) {
			return serverTimeToLocalTime(serverDate);
		}
		
		DateWrapper wrapper = new DateWrapper(serverDate);
		long timeDiffLocalAndServer = (- serverDate.getTimezoneOffset()*60*1000 - serverTimeZoneOffset); 
		wrapper = wrapper.addMillis((int)timeDiffLocalAndServer);
		return wrapper.asDate();
	}
	
	public static Date serverString2LocalDate(String serverTime){
		return serverDate.parse(serverTime);
	}
	public static String localDate2LocalString(Date localDate){
		return FormatUtil.getTimeDateFormat().format(localDate);
	}
	
	public static String seconds2String(long seconds){
		long hours = seconds/3600;
		long minutes = (seconds-(hours*3600))/60;
		seconds = seconds % 60;
		return simpleNumberFormat.format(hours)+":"+simpleNumberFormat.format(minutes)+":"+simpleNumberFormat.format(seconds);
	}
	
	public static String milseconds2String(long value){
		long seconds = value/1000;
		return seconds2String(seconds);
	}
	
	public static long MBPerMin2BytePerSec(long mbPerMin){
		return (mbPerMin<<20)/60;
	}
	
	public static String backupType2String(long backupType){
		if (backupType == BackupTypeModel.Full)
			return UIContext.Constants.backupTypeFull();
		else if (backupType == BackupTypeModel.Incremental)
			return UIContext.Constants.backupTypeIncremental();
		else if (backupType == BackupTypeModel.Resync)
			return UIContext.Constants.backupTypeResync();
		else if (backupType == BackupTypeModel.Archive)
			return UIContext.Constants.ArchiveFilesLabel();
		else if (backupType == BackupTypeModel.Copy)
			return UIContext.Constants.jobMonitorTypeCopy();
		else
			return UIContext.Constants.backupTypeUnknown();
	}
	
	public static String schedFlag2String(int schedFlag){	
		switch(schedFlag){
		case 0:
			return UIContext.Constants.schedTypeRegular();
		case 1:
			return UIContext.Constants.schedTypeDaily();
		case 2:
			return UIContext.Constants.schedTypeWeekly();
		case 4:
			return UIContext.Constants.schedTypeMonthly();		
		default:
			return "";
		}
	}
	
	public static String backupStatus2String(long backupStatus){
		if (backupStatus == BackupStatusModel.Active)
			return UIContext.Constants.backupStatusActive();
		else if (backupStatus == BackupStatusModel.Canceled)
			return UIContext.Constants.backupStatusCanceled();
		else if (backupStatus == BackupStatusModel.Crashed)
			return UIContext.Constants.backupStatusCrashed();
		else if (backupStatus == BackupStatusModel.Failed)
			return UIContext.Constants.backupStatusFailed();
		else if (backupStatus == BackupStatusModel.Finished)
			return UIContext.Constants.backupStatusFinished();
		else if (backupStatus == BackupStatusModel.Missed)
			return UIContext.Constants.backupStatusMissed();
		else
			return UIContext.Constants.backupStatusUnknown();
	}
	
	public static String activityLogType2String(int type){
		if (type == LogEntryType.Information)
			return UIContext.Constants.activityLogTypeInformation();
		else if (type == LogEntryType.Warning)
			return UIContext.Constants.activityLogTypeWarning();
		else if (type == LogEntryType.Error)
			return UIContext.Constants.activityLogTypeError();
		
		return UIContext.Constants.activityLogTypeUnknown();
	}
	
	public static String jobMonitorType2String(JobMonitorModel monitor){
		if (monitor.getJobType() == JobMonitorModel.JOBTYPE_BACKUP || monitor.getJobType()== JobMonitorModel.JOBTYPE_VM){
			if (monitor.getJobMethod() == BackupTypeModel.Full)
				return UIContext.Constants.jobMonitorFullBackup();
			else if (monitor.getJobMethod() == BackupTypeModel.Incremental)
				return UIContext.Constants.jobMonitorIncrementBackup();
			else if (monitor.getJobMethod() == BackupTypeModel.Resync)
				return UIContext.Constants.jobMonitorResynctBackup();
			else
				return UIContext.Constants.unknown();
		} else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_RESTORE)
			return UIContext.Constants.jobMonitorTypeRestore();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_COPY)
			return UIContext.Constants.jobMonitorTypeCopy();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_RECOVERY_VM)
			return UIContext.Constants.jobMonitorTypeRecoveryVM();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_APP)
			return UIContext.Constants.jobMonitorTypeCatalogApp();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS 
				|| monitor.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND
				|| monitor.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
				|| monitor.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
			return UIContext.Constants.jobMonitorTypeCatalogFS();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT)
			return UIContext.Constants.jobMonitorTypeCatalogGRT();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVE)
			return UIContext.Constants.jobMonitorArchive();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_FILEARCHIVE)
			return UIContext.Constants.jobMonitorFileArchive();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_FILECOPYDELETE)
			return UIContext.Constants.jobMonitorArchiveDelete();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVERESTORE)
			return UIContext.Constants.jobMonitorArchiveRestore();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVEPURGE)
			return UIContext.Constants.jobMonitorArchivePurge();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVE_CATALOG_SYNC)
			return UIContext.Constants.jobMonitorArchiveCatalogSync();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE) 
			return UIContext.Constants.jobMonitorArchiveReplicate();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND)
			return UIContext.Constants.jobMonitorArchiveInReplicate();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING)
			return UIContext.Constants.jobMonitorArchiveDataSeeding();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN)
			return UIContext.Constants.jobMonitorArchiveDataSeedingIn();
		else if (monitor.getJobType() == JobMonitorModel.JOBTYPE_BMR) {
			return UIContext.Constants.BMRJobType();
		}
		else
			return UIContext.Constants.unknown();
	}
	
	public static String jobType2StringWithoutJobMethod(long jobType) {
		return jobType2String(jobType, -1);
	}
	
	public static String jobType2String(long jobType, long jobMethod) {
		String jobTypeString = null;
		
		if (jobType==JobType.JOBTYPE_CATALOG_FS){
			jobTypeString = UIContext.Constants.jobMonitorTypeCatalogFS();
		} else if (jobType == JobType.JOBTYPE_VM_CATALOG_FS) {
			jobTypeString = UIContext.Constants.jobMonitorTypeCatalogFSForVM();
		} else if (jobType == JobType.JOBTYPE_CATALOG_FS_ONDEMAND || jobType == JobType.JOBTYPE_VM_CATALOG_FS_ONDEMAND) {
			jobTypeString = UIContext.Constants.jobMonitorTypeCatalogFSOnDemand();
		} else if(jobType==JobType.JOBTYPE_CATALOG_GRT){
			jobTypeString = UIContext.Constants.jobMonitorTypeCatalogGRT();
		}
		else if (jobType == JobType.JOBTYPE_BACKUP
				|| jobType == JobType.JOBTYPE_VM_BACKUP){
			
			if (jobMethod == BackupTypeModel.Full)
				jobTypeString = UIContext.Constants.jobMonitorFullBackup();
			else if (jobMethod == BackupTypeModel.Incremental)
				jobTypeString = UIContext.Constants.jobMonitorIncrementBackup();
			else if (jobMethod == BackupTypeModel.Resync)
				jobTypeString = UIContext.Constants.jobMonitorResynctBackup();
			else
				jobTypeString = UIContext.Constants.backup();
		}
		else if (jobType == JobType.JOBTYPE_VM_RECOVERY)
		{
			jobTypeString = UIContext.Constants.recoveryVMJob();
		}
		else if (jobType == JobType.JOBTYPE_RESTORE)
		{
			jobTypeString = UIContext.Constants.jobMonitorTypeRestore();
		}
		else if (jobType == JobType.JOBTYPE_COPY)
		{
			jobTypeString = UIContext.Constants.jobMonitorTypeCopy();
		}
		else if (jobType == JobType.JOBTYPE_FILECOPY_BACKUP)
		{
			jobTypeString = UIContext.Constants.jobMonitorArchive();
		}
		
		else if (jobType == JobType.JOBTYPE_FILECOPY_SOURCEDELETE)
		{
			jobTypeString = UIContext.Constants.jobMonitorFileArchive();
		}
		else if (jobType == JobType.JOBTYPE_FILECOPY_DELETE)
		{
			jobTypeString = UIContext.Constants.jobMonitorArchiveDelete();
		}
		else if (jobType == JobType.JOBTYPE_FILECOPY_RESTORE)
		{
			jobTypeString = UIContext.Constants.jobMonitorArchiveRestore();
		}
		else if (jobType == JobType.JOBTYPE_FILECOPY_PURGE)
		{
			jobTypeString = UIContext.Constants.archivePurgeJob();
		} else if (jobType == JobType.JOBTYPE_FILECOPY_CATALOGSYNC) {
			jobTypeString = UIContext.Constants.jobMonitorTypeArchiveCatalogSync();
		} else if (jobType == JobType.JOBTYPE_MERGE || jobType == JobType.JOBTYPE_VM_MERGE) {
			jobTypeString = UIContext.Constants.jobMonitorArchiveMerge();
		}  else if (jobType == JobType.JOBTYPE_RPS_MERGE) {
			jobTypeString = UIContext.Constants.jobMonitorArchiveMergeRPS();		
		} else if (jobType == JobType.JOBTYPE_RPS_REPLICATE) {
			jobTypeString = UIContext.Constants.jobMonitorArchiveReplicate();
		} else if (jobType == JobType.JOBTYPE_RPS_REPLICATE_IN_BOUND) {
			jobTypeString = UIContext.Constants.jobMonitorArchiveInReplicate();
		} else if (jobType == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING) {
			jobTypeString = UIContext.Constants.jobMonitorArchiveDataSeeding();
		} else if (jobType == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN) {
			jobTypeString = UIContext.Constants
					.jobMonitorArchiveDataSeedingIn();
		} else if (jobType == JobType.JOBTYPE_RPS_CONVERSION) {
			jobTypeString = UIContext.Constants.virtualStandyNameTranslate();
		} else if (jobType == JobType.JOBTYPE_BMR) {
			jobTypeString = UIContext.Constants.BMRJob();
		} else if (jobType == JobType.JOBTYPE_CONVERSION
				|| jobType == JobType.JOBTYPE_RPS_CONVERSION) {
			jobTypeString = UIContext.Constants.virtualStandyNameTranslate();
		} else if (jobType == JobType.JOBTYPE_START_INSTANT_VM) {
			jobTypeString = UIContext.Constants.StartInstantVMJobType();
		} else if (jobType == JobType.JOBTYPE_STOP_INSTANT_VM) {
			jobTypeString = UIContext.Constants.StopInstantVMJobType();
		}else if (jobType == JobType.JOBTYPE_START_INSTANT_VHD) {
			jobTypeString = UIContext.Constants.StartInstantVHDJobType();
		} else if (jobType == JobType.JOBTYPE_STOP_INSTANT_VHD) {
			jobTypeString = UIContext.Constants.StopInstantVHDJobType();
		} else if(jobType == JobType.JOBTYPE_ARCHIVE_TO_TAPE){
			jobTypeString = UIContext.Constants.archiveToTape();
		} else if (jobType == JobType.JOBTYPE_RPS_PURGE_DATASTORE) {
			jobTypeString = UIContext.Constants.purgeJob();
		} else if (jobType == JobType.JOBTYPE_LINUX_INSTANT_VM) {
			jobTypeString = UIContext.Constants.instantVMJobType();
		} 

		return jobTypeString;
	}
	
	public static String jobMonitorEncrytionAlgorithm2String(long algorithm){
		if( algorithm == JobMonitorModel.ENCRYPTION_UNKNOWN)
			return UIContext.Constants.jobMonitorNoEncryption();
		else if( algorithm == JobMonitorModel.ENCRYPTION_AES_128BIT)
			return UIContext.Messages.jobMonitorEncryptionAlgorithm(UIContext.Constants.AES128());
		else if( algorithm == JobMonitorModel.ENCRYPTION_AES_192BIT)
			return UIContext.Messages.jobMonitorEncryptionAlgorithm(UIContext.Constants.AES192());
		else if( algorithm == JobMonitorModel.ENCRYPTION_AES_256BIT )
			return UIContext.Messages.jobMonitorEncryptionAlgorithm(UIContext.Constants.AES256());
		else if( algorithm == JobMonitorModel.ENCRYPTION_3DES )
			return UIContext.Messages.jobMonitorEncryptionAlgorithm("3DES");
		else if( algorithm == JobMonitorModel.ENCRYPTION_PASSWORD_PROTECTION){
			return UIContext.Constants.encryptionProtection();
		}
		
		return "";
	}
	
	public static String jobMonitorStatus2String(long status){
		if (status == JobMonitorModel.JOBSTATUS_ACTIVE)
			return UIContext.Constants.jobMonitorStatusActive();
		else if (status == JobMonitorModel.JOBSTATUS_FINISHED)
			return UIContext.Constants.jobMonitorStatusFinished(); 
		else if (status == JobMonitorModel.JOBSTATUS_CANCELLED)
			return UIContext.Constants.jobMonitorStatusCancelled();
		else if (status == JobMonitorModel.JOBSTATUS_FAILED)
			return UIContext.Constants.jobMonitorStatusFailed();
		else if (status == JobMonitorModel.JOBSTATUS_INCOMPLETE)
			return UIContext.Constants.jobMonitorStatusIncomplete();
		else if (status == JobMonitorModel.JOBSTATUS_IDLE)
			return UIContext.Constants.jobMonitorStatusIdle();
		else if (status == JobMonitorModel.JOBSTATUS_WAITING)
			return UIContext.Constants.jobMonitorStatusWaiting();
		else
			return UIContext.Constants.unknown();
	}
	
	public static String jobMonitorPhase2String(JobMonitorModel monitor){
		return jobVsphereMonitorPhase2String(monitor.getJobPhase(), monitor.getCurrentProcessDiskName());
	}
	
	public static String jobVsphereMonitorPhase2String(long phase, String currentProcessDiskName) {
		if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_START_BACKUP)
			return UIContext.Constants.jobMonitorPhaseBackupStartBackup();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_TAKING_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseBackupTakingSnapShot();
		else if (phase == JobMonitorModel.BACKUP_PHASE_CREATE_HW_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseVMBackupTakingHWSnapShot();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_CREATING_VIRTUAL_DISKS)
			return UIContext.Constants.jobMonitorPhaseBackupCreatingVirtualDisks();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_REPLICATIING_VOLUMES)
			return UIContext.Constants.jobMonitorPhaseBackupReplicatingDisks();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_DELETING_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseBackupDeletingSnapshot();
		else if (phase == JobMonitorModel.BACKUP_PHASE_DELETE_HW_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseVMBackupDeletingHWSnapshot();
		else if (phase == JobMonitorModel.PHASE_CANCELING)
			return UIContext.Constants.jobMonitorPhaseCanceling();
		else if (phase == JobMonitorModel.PHASE_RESTORE_PHASE_START_RESTORE)
			return UIContext.Constants.jobMonitorPhaseRestoreStart();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_CREATE_METADATA)
			return UIContext.Constants.jobMonitorPhaseBackupCreateMetadata();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_COLLECT_DR_INFO)
			return UIContext.Constants.jobMonitorPhaseBackupCollectDRInfo();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_PURGE_SESSION)
			return UIContext.Constants.jobMonitorPhaseBackupPurgeSession();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_JOB_END)
			return UIContext.Constants.jobMonitorPhaseBackupJobEnd();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_PRE_JOB)
			return UIContext.Constants.jobMonitorPhaseBackupPreJob();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_POST_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseBackupPostSnapshot();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_POST_JOB)
			return UIContext.Constants.jobMonitorPhaseBackupPostJob();
		else if (phase == JobMonitorModel.RESTORE_PHASE_DUMP_METADATA)
			return UIContext.Constants.jobMonitorPhaseRestoreDumpMetadata();
		else if (phase == JobMonitorModel.RESTORE_PHASE_RESTORE_DATA)
			return UIContext.Constants.jobMonitorPhaseRestoreDisk() +" "+ currentProcessDiskName;
		else if (phase == JobMonitorModel.RESTORE_PHASE_VM_DIRECT_RESTORE_DETECT_VM_STATUS)
			return UIContext.Constants.jobMonitorPhaseRestoreDetectVMStatus();
		else if (phase == JobMonitorModel.RESTORE_PHASE_VM_DIRECT_RESTORE_CONNECT_VM_HOST)
			return UIContext.Constants.jobMonitorPhaseRestoreConnectVMHost();
		else if (phase == JobMonitorModel.RESTORE_PHASE_VM_DIRECT_RESTORE_CONNECT_VM)
			return UIContext.Constants.jobMonitorPhaseRestoreConnectVM();
		else if (phase == JobMonitorModel.RESTORE_PHASE_VM_DIRECT_RESTORE_CREATE_MINID2D)
			return UIContext.Constants.jobMonitorPhaseRestoreCreateMiniD2D();
		else if (phase == JobMonitorModel.RESTORE_PHASE_CREATE_VAPP)
			return UIContext.Constants.jobMonitorPhaseRestoreCreatingVApp();
		else if (phase == JobMonitorModel.RESTORE_PHASE_VAPP_RESTORE_VM)
			return UIContext.Constants.jobMonitorPhaseRestoreVM();
		else if (phase == JobMonitorModel.RESTORE_PHASE_VAPP_IMPORT_VM)
			return UIContext.Constants.jobMonitorPhaseRestoreImportingVM();
		else if (phase == JobMonitorModel.RESTORE_PHASE_VAPP_CLEANUP)
			return UIContext.Constants.jobMonitorPhaseRestoreCleaningEnv();
		else if (phase == JobMonitorModel.RESTORE_PHASE_GATHER_WRITERS_INFO)
			return UIContext.Constants.jobMonitorPhaseRestoreGatherWriterInf();
		else if (phase == JobMonitorModel.RESTORE_PHASE_INIT_VSS)
			return UIContext.Constants.jobMonitorPhaseRestoreInitVSS();
		else if (phase == JobMonitorModel.RESTORE_PHASE_SELECT_COMPONENTS_TO_RESTORE)
			return UIContext.Constants.jobMonitorPhaseRestoreSelectComponect();
		else if (phase == JobMonitorModel.RESTORE_PHASE_DISMOUNT_EXCHANGE_DATABASE)
			return UIContext.Constants.jobMonitorPhaseRestoreDismountExchange();
		else if (phase == JobMonitorModel.RESTORE_PHASE_GATHER_DB_INFO_FROM_AD)
			return UIContext.Constants.jobMonitorPhaseRestoreDBInfoFromAD();
		else if (phase == JobMonitorModel.RESTORE_STOP_SQL_SERVICE_RESTORE_MASTER)
			return UIContext.Constants.jobMonitorPhaseRestoreStopSQLService();
		else if (phase == JobMonitorModel.RESTORE_PHASE_START_SQL_SERVICE)
			return UIContext.Constants.jobMonitorPhaseRestoreStartSQLService();
		else if (phase == JobMonitorModel.RESTORE_PHASE_PRERESTORE)
			return UIContext.Constants.jobMonitorPhaseRestorePreRestore();
		else if (phase == JobMonitorModel.RESTORE_PHASE_RESTORE_FILE)
			return UIContext.Constants.jobMonitorPhaseRestoreRestoreFile();
		else if (phase == JobMonitorModel.RESTORE_PHASE_POSTRESTORE)
			return UIContext.Constants.jobMonitorPhaseRestorePostRestore();
		else if (phase == JobMonitorModel.RESTORE_PHASE_MOUNT_EXCHANGE_DATABASE)
			return UIContext.Constants.jobMonitorPhaseRestoreMountExchange();
		else if (phase == JobMonitorModel.COPY_PHASE_START_COPY)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseStartCopy();
		else if (phase == JobMonitorModel.COPY_PHASE_COPY_DATA)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseCopyData();
		else if (phase == JobMonitorModel.COPY_PHASE_LOCK_SESSION)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseLockSession();
		else if (phase == JobMonitorModel.COPY_PHASE_LOCK_SESSION_SUCCESSFUL)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseLockSessionSuccessful();
		else if (phase == JobMonitorModel.COPY_PHASE_LOCK_SESSION_FAILED)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseLockSessionFailed();
		else if (phase == JobMonitorModel.COPY_PHASE_ESTIMATE_DATA)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseEstimateData();
		else if(phase == JobMonitorModel.PHASE_BACKUP_PHASE_WAITING)
			return UIContext.Constants.jobMonitorPhaseVsphereWaiting();
		else if(phase == JobMonitorModel.BACKUP_PHASE_CONTINUR_FAILED_MERGE) {
			return UIContext.Constants.jobMonitorPhaseContinueFailedMerge();
		}else if (phase == JobMonitorModel.BACKUP_PHASE_CONNECT_TO_STUB){
			return UIContext.Constants.hyperVBackupPhaseCONNECT_TO_STUB();
		}else if (phase == JobMonitorModel.BACKUP_PHASE_UPGRADE_CBT){
			return UIContext.Constants.hyperVBackupPhaseUPGRADE_CBT();
		}else if (phase == JobMonitorModel.BACKUP_PHASE_INITIALIZE_STUB){
			return UIContext.Constants.hyperVBackupPhaseINITIALIZE_STUB();
		}else if (phase == JobMonitorModel.BACKUP_PHASE_COLLECT_DATA){
			return UIContext.Constants.hyperVBackupPhaseCOLLECT_DATA();
		}
		else if (phase == JobMonitorModel.BACKUP_PHASE_CHECK_RECOVERY_POINT)
			return UIContext.Constants.jobMonitorPhaseBackupCheckingRecoveryPoint();
		else if (phase == JobMonitorModel.RESTORE_PHASE_RESTORE_EXCHGRT_DATA)
			return UIContext.Constants.jobMonitorPhaseRestoreExchangeGRTData();
		else if (phase == JobMonitorModel.EXGRT_PHASE_DISMOUNTING_DRIVER)
			return UIContext.Constants.jobMonitorPhaseGRTDisMountingDriver();
		else if (phase == JobMonitorModel.EXGRT_PHASE_ESTIMATE || phase == JobMonitorModel.EXGRT_PHASE_START_CATALOG)
			return UIContext.Constants.jobMonitorPhasePrepareGRTCatalog();
		else if (phase == JobMonitorModel.EXGRT_PHASE_GENERAT_CATALOG)
			return UIContext.Constants.jobMonitorPhaseGRTGene();
		else if (phase == JobMonitorModel.EXGRT_PHASE_GENERATE_CATALOG_END)
			return UIContext.Constants.jobMonitorPhaseGRTEnd();
		else if(phase == JobMonitorModel.EXGRT_PHASE_MOUNTING_DRIVER)
			return UIContext.Constants.jobMonitorPhaseGRTMountingDriver();
		else if(phase == JobMonitorModel.EXGRT_PHASE_GENERATE_INDEX_FILE)
			return UIContext.Constants.jobMonitorPhaseGenerateIndexFile();
		else if(phase == JobMonitorModel.EXGRT_PHASE_DEGRAGMENT)
			return UIContext.Constants.jobMonitorPhaseDefragment();
		else{
			String catalog = getCatalogPhase(phase);
			if((catalog) != null)
				return catalog;
			else
				return null;
		}
	}
	
	public static String jobMonitorPhase2String(long phase){
		if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_START_BACKUP)
			return UIContext.Constants.jobMonitorPhaseBackupStartBackup();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_TAKING_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseBackupTakingSnapShot();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_CREATING_VIRTUAL_DISKS)
			return UIContext.Constants.jobMonitorPhaseBackupCreatingVirtualDisks();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_REPLICATIING_VOLUMES)
			return UIContext.Constants.jobMonitorPhaseBackupReplicatingVolumes();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_DELETING_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseBackupDeletingSnapshot();
		else if (phase == JobMonitorModel.PHASE_CANCELING)
			return UIContext.Constants.jobMonitorPhaseCanceling();
		else if (phase == JobMonitorModel.PHASE_RESTORE_PHASE_START_RESTORE)
			return UIContext.Constants.jobMonitorPhaseRestoreStart();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_CREATE_METADATA)
			return UIContext.Constants.jobMonitorPhaseBackupCreateMetadata();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_COLLECT_DR_INFO)
			return UIContext.Constants.jobMonitorPhaseBackupCollectDRInfo();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_PURGE_SESSION)
			return UIContext.Constants.jobMonitorPhaseBackupPurgeSession();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_JOB_END)
			return UIContext.Constants.jobMonitorPhaseBackupJobEnd();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_PRE_JOB)
			return UIContext.Constants.jobMonitorPhaseBackupPreJob();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_POST_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseBackupPostSnapshot();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_POST_JOB)
			return UIContext.Constants.jobMonitorPhaseBackupPostJob();
		else if (phase == JobMonitorModel.RESTORE_PHASE_DUMP_METADATA)
			return UIContext.Constants.jobMonitorPhaseRestoreDumpMetadata();
		else if (phase == JobMonitorModel.RESTORE_PHASE_RESTORE_DATA)
			return UIContext.Constants.jobMonitorPhaseRestoreRestoreData();
		else if (phase == JobMonitorModel.RESTORE_PHASE_VM_DIRECT_RESTORE_DETECT_VM_STATUS)
			return UIContext.Constants.jobMonitorPhaseRestoreDetectVMStatus();
		else if (phase == JobMonitorModel.RESTORE_PHASE_VM_DIRECT_RESTORE_CONNECT_VM_HOST)
			return UIContext.Constants.jobMonitorPhaseRestoreConnectVMHost();
		else if (phase == JobMonitorModel.RESTORE_PHASE_VM_DIRECT_RESTORE_CONNECT_VM)
			return UIContext.Constants.jobMonitorPhaseRestoreConnectVM();
		else if (phase == JobMonitorModel.RESTORE_PHASE_VM_DIRECT_RESTORE_CREATE_MINID2D)
			return UIContext.Constants.jobMonitorPhaseRestoreCreateMiniD2D();
		else if (phase == JobMonitorModel.RESTORE_PHASE_CREATE_VAPP)
			return "Creating vApp...";
		else if (phase == JobMonitorModel.RESTORE_PHASE_VAPP_RESTORE_VM)
			return "Restoring VMs...";
		else if (phase == JobMonitorModel.RESTORE_PHASE_VAPP_IMPORT_VM)
			return "Importing VMs...";
		else if (phase == JobMonitorModel.RESTORE_PHASE_VAPP_CLEANUP)
			return "Cleanup environment...";
		else if (phase == JobMonitorModel.RESTORE_PHASE_GATHER_WRITERS_INFO)
			return UIContext.Constants.jobMonitorPhaseRestoreGatherWriterInf();
		else if (phase == JobMonitorModel.RESTORE_PHASE_INIT_VSS)
			return UIContext.Constants.jobMonitorPhaseRestoreInitVSS();
		else if (phase == JobMonitorModel.RESTORE_PHASE_SELECT_COMPONENTS_TO_RESTORE)
			return UIContext.Constants.jobMonitorPhaseRestoreSelectComponect();
		else if (phase == JobMonitorModel.RESTORE_PHASE_DISMOUNT_EXCHANGE_DATABASE)
			return UIContext.Constants.jobMonitorPhaseRestoreDismountExchange();
		else if (phase == JobMonitorModel.RESTORE_PHASE_GATHER_DB_INFO_FROM_AD)
			return UIContext.Constants.jobMonitorPhaseRestoreDBInfoFromAD();
		else if (phase == JobMonitorModel.RESTORE_STOP_SQL_SERVICE_RESTORE_MASTER)
			return UIContext.Constants.jobMonitorPhaseRestoreStopSQLService();
		else if (phase == JobMonitorModel.RESTORE_PHASE_START_SQL_SERVICE)
			return UIContext.Constants.jobMonitorPhaseRestoreStartSQLService();
		else if (phase == JobMonitorModel.RESTORE_PHASE_PRERESTORE)
			return UIContext.Constants.jobMonitorPhaseRestorePreRestore();
		else if (phase == JobMonitorModel.RESTORE_PHASE_RESTORE_FILE)
			return UIContext.Constants.jobMonitorPhaseRestoreRestoreFile();
		else if (phase == JobMonitorModel.RESTORE_PHASE_POSTRESTORE)
			return UIContext.Constants.jobMonitorPhaseRestorePostRestore();
		else if (phase == JobMonitorModel.RESTORE_PHASE_RESTORE_EXCHGRT_DATA)
			return UIContext.Constants.jobMonitorPhaseRestoreExchangeGRTData();		
		else if (phase == JobMonitorModel.RESTORE_PHASE_MOUNT_EXCHANGE_DATABASE)
			return UIContext.Constants.jobMonitorPhaseRestoreMountExchange();
		else if (phase == JobMonitorModel.COPY_PHASE_START_COPY)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseStartCopy();
		else if (phase == JobMonitorModel.COPY_PHASE_COPY_DATA)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseCopyData();
		else if (phase == JobMonitorModel.COPY_PHASE_LOCK_SESSION)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseLockSession();
		else if (phase == JobMonitorModel.COPY_PHASE_LOCK_SESSION_SUCCESSFUL)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseLockSessionSuccessful();
		else if (phase == JobMonitorModel.COPY_PHASE_LOCK_SESSION_FAILED)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseLockSessionFailed();
		else if (phase == JobMonitorModel.COPY_PHASE_ESTIMATE_DATA)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseEstimateData();
		else if (phase == JobMonitorModel.EXGRT_PHASE_DISMOUNTING_DRIVER)
			return UIContext.Constants.jobMonitorPhaseGRTDisMountingDriver();
		else if (phase == JobMonitorModel.EXGRT_PHASE_ESTIMATE || phase == JobMonitorModel.EXGRT_PHASE_START_CATALOG)
			return UIContext.Constants.jobMonitorPhasePrepareGRTCatalog();
		else if (phase == JobMonitorModel.EXGRT_PHASE_GENERAT_CATALOG)
			return UIContext.Constants.jobMonitorPhaseGRTGene();
		else if (phase == JobMonitorModel.EXGRT_PHASE_GENERATE_CATALOG_END)
			return UIContext.Constants.jobMonitorPhaseGRTEnd();
		else if(phase == JobMonitorModel.EXGRT_PHASE_MOUNTING_DRIVER)
			return UIContext.Constants.jobMonitorPhaseGRTMountingDriver();
		else if(phase == JobMonitorModel.PHASE_BACKUP_PHASE_LOCKSESSION)
			return UIContext.Constants.jobMonitorPhaseBackupLockSession();
		else if(phase == JobMonitorModel.PHASE_BACKUP_PHASE_WAITING)
			return UIContext.Constants.jobMonitorPhaseVsphereWaiting();
		else if(phase == JobMonitorModel.BACKUP_PHASE_CONTINUR_FAILED_MERGE) {
			return UIContext.Constants.jobMonitorPhaseContinueFailedMerge();
		}else if(phase == JobMonitorModel.EXGRT_PHASE_GENERATE_INDEX_FILE)
			return UIContext.Constants.jobMonitorPhaseGenerateIndexFile();
		else if(phase == JobMonitorModel.RESTORE_PHASE_RESTORE_MOUNT_VOLUME)
			return UIContext.Constants.jobMonitorPhaseMountVolume();
		else if(phase == JobMonitorModel.RESTORE_PHASE_RESTORE_UNMOUNT_VOLUME)
			return UIContext.Constants.jobMonitorPhaseUnMountVolume();
		else if(phase == JobMonitorModel.LINUX_PHASE_CONNECT_TARGET)
			return UIContext.Constants.linuxJobMonitorConnectTarget();
		else if(phase == JobMonitorModel.LINUX_PHASE_CREATE_DISK)
			return UIContext.Constants.linuxJobMonitorCreateDiskLayout();
		else if(phase == JobMonitorModel.LINUX_PHASE_WRAPUP)
			return UIContext.Constants.linuxJobMonitorWrapup();
		else if(phase == JobMonitorModel.LINUX_PHASE_CONNECT_NODE)
			return UIContext.Constants.linuxJobMonitorConnectNode();
		else if(phase == JobMonitorModel.LINUX_PHASE_RESTORE_FILE)
			return UIContext.Constants.linuxJobMonitorRestoreFiles();
		else if(phase == JobMonitorModel.LINUX_PHASE_START)
			return UIContext.Constants.linuxJobMonitorStart();
		else if(phase == JobMonitorModel.LINUX_PHASE_INSTALL_BUILD)
			return UIContext.Constants.linuxJobMonitorInstallBuild();
		else if(phase == JobMonitorModel.LINUX_PHASE_CANCEL_JOB)
			return  UIContext.Constants.linuxJobMonitorCancelJob();
		else {
			String catalog = getCatalogPhase(phase);
			if((catalog) != null)
				return catalog;
			else
				return null;
		}	
	}
	
	public static String bytes2String(long bytes){	
		if (bytes <1024)
			return UIContext.Messages.bytes(bytes+"");
		else if (bytes<(1024*1024)) {
			String kb = number.format(((double)bytes)/1024);
			if(kb.startsWith("1024"))
				return UIContext.Messages.MB("1");
			
			return UIContext.Messages.KB(kb);
		}
		else if (bytes<(1024*1024*1024)) {
			String mb = number.format(((double)bytes)/(1024*1024));
			if(mb.startsWith("1024"))
				return UIContext.Messages.GB("1");
			
			return UIContext.Messages.MB(mb);
		}
		else
			return UIContext.Messages.GB(number.format(((double)bytes)/(1024*1024*1024)));
	}
	
	public static String bytes2MBString(long bytes){	
		String mb = number.format(((double)bytes)/(1024*1024));
		if(mb.startsWith("1024"))
			return UIContext.Messages.jobMonitorThroughoutGPerMin("1");
		
		return UIContext.Messages.jobMonitorThroughout(mb);
	}
	
	public static String bytes2GBString(long bytes){	
		return UIContext.Messages.GB(number.format(((double)bytes)/(1024*1024*1024)));
	}
	
	public static String scheduleIntervalUnitModel2String(int type){
		if (type == BackupScheduleIntervalUnitModel.Minute)
			return UIContext.Constants.minutes();
		else if (type == BackupScheduleIntervalUnitModel.Hour)
			return UIContext.Constants.hours();
		else if (type == BackupScheduleIntervalUnitModel.Day)
			return UIContext.Constants.days();
		else if (type == BackupScheduleIntervalUnitModel.Backups)
			return UIContext.Constants.ArchiveScheduleUnit();
		return "";
	}
	
	public static ColumnConfig createColumnConfig(String id, String header,
			int width) {
		return createColumnConfig(id, header, width, null);
	}

	@SuppressWarnings("unchecked")
	public static ColumnConfig createColumnConfig(String id, String header,
			int width, GridCellRenderer renderer) {
		ColumnConfig column = new ColumnConfig();
		column.setGroupable(false);
		column.setSortable(false);
		
		column.setId(id);
		column.setHeaderHtml(header);
		column.setMenuDisabled(true);
		if (width >= 0)
			column.setWidth(width);
		if (renderer != null)
			column.setRenderer(renderer);
		return column;
	}
	
	@SuppressWarnings("unchecked")
	public static ColumnConfig createColumnConfig(String id, String header,
			int width, GridCellRenderer renderer,HorizontalAlignment horizontalAlignment) {
		ColumnConfig column = new ColumnConfig();
		column.setGroupable(false);
		column.setSortable(false);
		
		column.setId(id);
		column.setHeaderHtml(header);
		column.setMenuDisabled(true);
		if (width >= 0)
			column.setWidth(width);
		if (renderer != null)
			column.setRenderer(renderer);
		
		if(horizontalAlignment!=null)
			column.setAlignment(horizontalAlignment);
		return column;
	}
	
	public static boolean isValidRemotePath(String path) {
		return isValidRemotePath(path, true);
	}
	
	public static boolean isValidRemotePath(String path, boolean forBrowse) {
		String serverNameReg = "\\\\\\\\[^`~!@#\\$\\^&\\*\\(\\)=\\+\\[\\]{}\\\\\\|;:'\",<>/\\?]+";
		String directroy = "[^\\\\/:\\*\\?\"<>\\|]+";
		String c=forBrowse?"*":"+";
		String absoluteDirReg = "(\\\\"	+ directroy + ")"+c+"(\\\\)?$";
		
		if(path != null){
			return path.trim().matches(serverNameReg + absoluteDirReg);
		}
		return false;

	}
	/**
	 * This function will replace all of "/" to "\" in the path.
	 * @return
	 */
	public static String getNormalizedPath(String path) {
		if(path == null)
			return path;
		
		return path.replaceAll("/", "\\\\").trim();
	}
	
	/**
	 * This function check if the agent is IE6.
	 * @param agent
	 * @return
	 */
	public static boolean checkIE6(String agent){
		if(agent==null || agent.equals("")){
			return false;
		}
        if(agent.indexOf("msie 6.0")>-1){
        	return true;
        }
		return false;
	}
	

	public static String convert2UILabel(String value){
		if (value == null || value.length() == 0)
			return UIContext.Constants.NA();
		return value;
	}
	
	public static String virtualizationType2String(VirtualizationType type){
		if (type == VirtualizationType.HyperV)
			return UIContext.Constants.virtualizationTypeHyperV();
		else if (type == VirtualizationType.VMwareESX)
			return UIContext.Constants.virtualizationTypeVMwareESX();
		else if (type == VirtualizationType.VMwareVirtualCenter)
			return UIContext.Constants.virtualizationTypeVMwareVirtualCenter();
		else
			return UIContext.Constants.unknown();
	}
	
	public static String replicaJobPhase2String(int phase){
		if (phase == RepJobMonitor.REP_JOB_PHASE_START)
			return UIContext.Constants.replicaJobPhaseStart();
		else if (phase == RepJobMonitor.REP_JOB_PHASE_DATA_TRANSFER)
			return UIContext.Constants.replicaJobPhaseDataTransfer();
		else if (phase == RepJobMonitor.REP_JOB_PHASE_POST_DATA_TRANSFER)
			return UIContext.Constants.replicaJobPhasePostDataTransfer();
		else if (phase == RepJobMonitor.REP_JOB_PHASE_CANCELLING)
			return UIContext.Constants.replicaJobPhaseCancelling();
		else if (phase == RepJobMonitor.REP_JOB_PHASE_STOPPING)
			return UIContext.Constants.replicaJobPhaseStopping();
		else if (phase == RepJobMonitor.REP_JOB_PHASE_EXIT)
			return UIContext.Constants.replicaJobPhaseExit();
		else if (phase == RepJobMonitor.REP_JOB_PHASE_SESSION_START)
			return UIContext.Constants.replicaJobPhaseSessionStart();
		else if (phase == RepJobMonitor.REP_JOB_PHASE_SESSION_END)
			return UIContext.Constants.replicaJobPhaseSessionEnd();
		else if (phase == RepJobMonitor.REP_JOB_CREATE_VM)
			return UIContext.Constants.replicaJobPhaseCreateVM();
		else if (phase == RepJobMonitor.REP_JOB_TAKE_SNAPSHOT)
			return UIContext.Constants.replicaJobPhaseTakeSnapshot();
		else if (phase == RepJobMonitor.REP_JOB_UPLOAD_META_DATA)
			return UIContext.Constants.replicaJobPhaseUploadMetadata();
		else if (phase == RepJobMonitor.REP_JOB_GET_CONNECTION)
			return UIContext.Constants.replicaJobPhaseGetConnection();
		else if (phase == RepJobMonitor.REP_JOB_CRATE_BOOTABLESNAPSHOT)
			return UIContext.Constants.replicaJobPhaseCreateBootableSnapshot();
		else 
			return UIContext.Constants.unknown();
	}
	
	public static String replicationStatus2Strign(String status){
		if (ReplicationStatus.valueOf(status) == ReplicationStatus.NOT_STARTED)
			return UIContext.Constants.replicationStatusNotStarted();
		else if (ReplicationStatus.valueOf(status) == ReplicationStatus.STARTING)
			return UIContext.Constants.replicationStatusStarting();
		else if (ReplicationStatus.valueOf(status) == ReplicationStatus.RUNNING)
			return UIContext.Constants.replicationStatusRunning();
		else if (ReplicationStatus.valueOf(status) == ReplicationStatus.FAILED)
			return UIContext.Constants.replicationStatusFailed();
		else
			return UIContext.Constants.unknown();
	}

	public static String formatVmInfo(VirtualMachineInfo vmInfo) {
		if(vmInfo == null) return UIContext.Constants.vmTypeNA();
		switch(vmInfo.getType()){
		case VirtualMachineInfo.VIRTUAL_TYPE_HYPERV:
			return UIContext.Constants.vmTypeHyperV();
		case VirtualMachineInfo.VIRTUAL_TYPE_VMWARE:
			return UIContext.Constants.vmTypeVMWare();	

		case VirtualMachineInfo.VIRTUAL_TYPE_UNKNOWN:
			return UIContext.Constants.unknown();				
		}
		return UIContext.Constants.unknown();	
	}

	public static String getRemainTime(JobMonitorModel model){

		// liuwe05 2015-01-29 fix defect 205763: HBBU backup progress dialog chkdsk phase stay at 100% user can not know what is it doing and will think it hangs if chkdsk takes a long time
		// for checking recovery point, showing a fake (auto) progress bar, set remain time to N/A
		if (model.getJobPhase() == JobMonitorModel.BACKUP_PHASE_CHECK_RECOVERY_POINT)
		{
			return UIContext.Constants.NA();
		}		
		
		if (model.getJobType() == JobMonitorModel.JOBTYPE_BMR) {
			return Utils.milseconds2String(model.getRemainTime());
		}
		if (model.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND
			||model.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE
		    ||model.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING
			||model.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN) {
			return Utils.milseconds2String(model.getRemainTime());
		}
		if(model.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT)
			return String.valueOf(model.getGRTProcessedFolder());
		if(model.getCTBKStartTime() != null && !model.getCTBKStartTime().isEmpty())
			return String.valueOf(model.getBackupStartTime());
		
		long totalSize = model.getEstimateBytesJob();
		long processedSize = model.getTransferBytesJob();			
		if (model.getElapsedTime() > 1000) {
			
			if (totalSize > 0) {
				
				if(processedSize >= totalSize) {
					return "0";
				}
				
				long remainSize = totalSize - processedSize;
				long temp = (processedSize / model.getElapsedTime());
				long remainTime = 0;
				if(temp == 0) {					
					return UIContext.Constants.NA();
				}
				//base on average throughput
				//remainTime = remainSize/temp;
				//base on instant throughput
				// defect 101464
				if (model.getReadSpeed() <= 0) {
					return UIContext.Constants.NA();
				}
				remainTime = (remainSize*60*1000)/(model.getReadSpeed()*1024*1024);
				return Utils.milseconds2String(remainTime);
				
			} else {				
				return UIContext.Constants.NA();
			}
		} else {
			if (processedSize >= totalSize && processedSize > 0 ) {
				return "0";
			} else {						
				return UIContext.Constants.NA();
			}
		}
	}
	
	public static String getCatalogStatusString() {
		return null;
	}
	
	public static String getGRTCatalogStatusString() {
		return null;
	}
	
	public static String getCatalogPhase(long jobPhase){
		String phase = null;
		switch((int)jobPhase){
			case JobMonitorModel.CATPROC_PHASE_VALIDATE_CATALOG_SCRIPT:
			case JobMonitorModel.CATPROC_PHASE_PARSE_CATALOG_SCRIPT:
			case JobMonitorModel.CATPROC_PHASE_LOCK_SESS_INTERGRATION:
			case JobMonitorModel.CATPROC_PHASE_PREPARE_FOR_CATALOG:
				phase = UIContext.Constants.jobMonitorPhasePrepareCatalog();
			    break;
			case JobMonitorModel.CATPROC_PHASE_DELETE_FAILED_SESSION:
			case JobMonitorModel.CATPROC_PHASE_CONTINUR_FAILED_MERGE:
			case JobMonitorModel.CATPROC_PHASE_MERGE_SESS_BY_SETTING:
			case JobMonitorModel.CATPROC_PHASE_LOCK_SESS_FOR_CATALOG_READ:
				phase = UIContext.Constants.jobMonitorPhasePrepareSessionForCatalog();
				break;
			case JobMonitorModel.CATPROC_PHASE_BEGIN_TO_GENERATE_CATALOG:
			case JobMonitorModel.CATPROC_PHASE_GENERATE_CATALOG_FOR_VOLUME:
			case JobMonitorModel.CATPROC_PHASE_GENERATE_CAT_INDEX_FOR_VOLUME:
			case JobMonitorModel.CATPROC_PHASE_UPDATE_SESSION_INFORMATIIN:
			case JobMonitorModel.CATPROC_PHASE_LOCK_SESS_FOR_CATALOG_WRITE:
			case JobMonitorModel.CATPROC_PHASE_UPDATE_SESS_BLOCK_2:
				phase = UIContext.Constants.jobMonitorPhaseGenerateCatalog();
				break;
			case JobMonitorModel.CATPROC_PHASE_START_NEXT_CATLOG_JOB:
			case JobMonitorModel.CATPROC_PHASE_CATALOG_GENERATE_FINISH:
				phase = UIContext.Constants.jobMonitorPhaseFinshCatalog();
				break;
		default:
				break;
		}
		return phase;
	}
	
	public static String getCatalogProgress(JobMonitorModel jm){
		if(jm.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT) {
			return UIContext.Constants.jobMonitorGRTCatalogCreating() + " " + formatBackupTimeForCatalog(jm);
		}
		String status = null;
		switch(jm.getJobPhase().intValue()){
			case JobMonitorModel.CATPROC_PHASE_VALIDATE_CATALOG_SCRIPT:
			case JobMonitorModel.CATPROC_PHASE_PARSE_CATALOG_SCRIPT:
			case JobMonitorModel.CATPROC_PHASE_LOCK_SESS_INTERGRATION:
			case JobMonitorModel.CATPROC_PHASE_PREPARE_FOR_CATALOG:
			case JobMonitorModel.CATPROC_PHASE_DELETE_FAILED_SESSION:
			case JobMonitorModel.CATPROC_PHASE_CONTINUR_FAILED_MERGE:
			case JobMonitorModel.CATPROC_PHASE_MERGE_SESS_BY_SETTING:
			case JobMonitorModel.CATPROC_PHASE_LOCK_SESS_FOR_CATALOG_READ:
				if(jm.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_APP)
					status = UIContext.Constants.jobMonitorAppCatalogPreparing() + " " + formatBackupTimeForCatalog(jm);
				else if(jm.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS 
						|| jm.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND 
						|| jm.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
						|| jm.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
					status = UIContext.Constants.jobMonitorCatalogPreparing() + " " + formatBackupTimeForCatalog(jm);
				else 
					status = UIContext.Constants.jobMonitorGRTCatalogPreparing() + " " + formatBackupTimeForCatalog(jm);
				break;
			case JobMonitorModel.CATPROC_PHASE_BEGIN_TO_GENERATE_CATALOG:
			case JobMonitorModel.CATPROC_PHASE_GENERATE_CATALOG_FOR_VOLUME:
			case JobMonitorModel.CATPROC_PHASE_GENERATE_CAT_INDEX_FOR_VOLUME:
			case JobMonitorModel.CATPROC_PHASE_UPDATE_SESSION_INFORMATIIN:
			case JobMonitorModel.CATPROC_PHASE_LOCK_SESS_FOR_CATALOG_WRITE:
			case JobMonitorModel.CATPROC_PHASE_UPDATE_SESS_BLOCK_2:				
			case JobMonitorModel.CATPROC_PHASE_START_NEXT_CATLOG_JOB:
			case JobMonitorModel.CATPROC_PHASE_CATALOG_GENERATE_FINISH:
				if(jm.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_APP)
					status = UIContext.Constants.jobMonitorAppCatalogCreating() + " " + formatBackupTimeForCatalog(jm);
				else if(jm.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS || jm.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND 
						|| jm.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS || jm.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
					status = UIContext.Constants.jobMonitorCatalogCreating() + " " + formatBackupTimeForCatalog(jm);
				else 
					status = UIContext.Constants.jobMonitorGRTCatalogCreating() + " " + formatBackupTimeForCatalog(jm);
				break;
		default:
				break;
		}
		
		
		return status;
	}
	
	public static String getJobStatusLabelString(long jobtype) {
		if(jobtype == JobMonitorModel.JOBTYPE_BACKUP || jobtype == JobMonitorModel.JOBTYPE_VM)
			return UIContext.Constants.jobMonitorJobTypeBackup();
		else if(jobtype == JobMonitorModel.JOBTYPE_RESTORE)
			return UIContext.Constants.jobMonitorTypeRestore();
		else if(jobtype == JobMonitorModel.JOBTYPE_COPY)
			return UIContext.Constants.jobMonitorJobTypeExport();
		else if(jobtype == JobMonitorModel.JOBTYPE_CATALOG_FS
				|| jobtype == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
				|| jobtype == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND
				|| jobtype == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND) {			
			return UIContext.Constants.jobMonitorTypeCatalogFS();
		} else if (jobtype == JobMonitorModel.JOBTYPE_CATALOG_APP) {
			return UIContext.Constants.jobMonitorTypeCatalogApp();
		} else if (jobtype == JobMonitorModel.JOBTYPE_CATALOG_GRT) {
			return UIContext.Constants.jobMonitorTypeCatalogGRT();
		} else if(jobtype == JobMonitorModel.JOBTYPE_ARCHIVE)
			return UIContext.Constants.jobMonitorArchive();
		else if (jobtype == JobMonitorModel.JOBTYPE_FILEARCHIVE)
			return UIContext.Constants.jobMonitorFileArchive();
		else if (jobtype == JobMonitorModel.JOBTYPE_FILECOPYDELETE)
			return UIContext.Constants.jobMonitorArchiveDelete();
		else if(jobtype == JobMonitorModel.JOBTYPE_ARCHIVEPURGE)
			return UIContext.Constants.jobMonitorArchivePurge();
		else if(jobtype == JobMonitorModel.JOBTYPE_ARCHIVERESTORE)
			return UIContext.Constants.jobMonitorArchiveRestore();
		else if(jobtype == JobMonitorModel.JOBTYPE_ARCHIVE_CATALOG_SYNC)
			return UIContext.Constants.jobMonitorArchiveCatalogSync();	
		else if(jobtype == JobMonitorModel.JOBTYPE_RECOVERY_VM)
			return UIContext.Constants.jobMonitorTypeRecoveryVM();
		else if (jobtype == JobMonitorModel.JOBTYPE_RPS_REPLICATE) 
			return UIContext.Constants.jobMonitorArchiveReplicate();
		else if (jobtype == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND) 
			return UIContext.Constants.jobMonitorArchiveInReplicate();
		else if (jobtype == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING)
			return UIContext.Constants.jobMonitorArchiveDataSeeding();
		else if (jobtype == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN)
			return UIContext.Constants.jobMonitorArchiveDataSeedingIn();
		else if (jobtype == JobType.JOBTYPE_CONVERSION
				|| jobtype == JobType.JOBTYPE_RPS_CONVERSION) {
			return UIContext.Constants.jobTypeVirtualStandby();
		} else if (jobtype == JobType.JOBTYPE_BMR) {
			return UIContext.Constants.BMRJobType();
		} else if (jobtype == JobType.JOBTYPE_START_INSTANT_VM) {
			return UIContext.Constants.StartInstantVMJobType();
		} else if (jobtype == JobType.JOBTYPE_STOP_INSTANT_VM) {
			return UIContext.Constants.StopInstantVMJobType();
		} else if (jobtype == JobType.JOBTYPE_RPS_PURGE_DATASTORE) {
			return UIContext.Constants.purgeJob();
		}else if (jobtype == JobMonitorModel.JOBTYPE_RPS_MERGE){
			return UIContext.Constants.jobMonitorArchiveMergeRPS();
		}else {
			return UIContext.Constants.NA();
		}
	}
	
	public static void updateProgress(ProgressBar widget, JobMonitorModel model){
		
		if(model.isLinuxNode()!=null&&model.isLinuxNode()){
			widget.updateProgress(model.getProgress()*0.01,NumberFormat.getPercentFormat().format(model.getProgress()*0.01));	
		}else
		
		// liuwe05 2015-01-29 fix defect 205763: HBBU backup progress dialog chkdsk phase stay at 100% user can not know what is it doing and will think it hangs if chkdsk takes a long time
		// for checking recovery point, showing a fake (auto) progress bar
		if (model.getJobPhase() == JobMonitorModel.BACKUP_PHASE_CHECK_RECOVERY_POINT)
		{
			if (!widget.isRunning()) // if the auto progress is not enabled
			{
				widget.auto();
				widget.updateText(Utils.jobMonitorPhase2String(model));
			}
		}
		else
		{
			if (widget.isRunning()) // once check recovery point is done, restore the original progress bar
			{
				widget.reset();
			}
					
			double[] value = new double[1];
			String[] text = new String[1];
			
			if (getProgress(model, value, text)) {
				widget.updateProgress(value[0], text[0]);
			}
		}			
		
	}
	
	public static boolean getProgress(JobMonitorModel model, double[] value, String[] text) {
		long totalSize = model.getEstimateBytesJob();
		long processedSize = model.getTransferBytesJob();
		/*String transferedSizeLabel = Utils.bytes2String(processedSize);
		String totalSizeLabel = Utils.bytes2String(totalSize);*/
		
		String status = getCatalogProgress(model);
		if(status != null){
//			((Label)widget).setHtml(status);
			return false;
		} else if (model.getJobType() == JobType.JOBTYPE_BMR) {
			if (model.getProgress() > 0) {
				String textString = NumberFormat.getPercentFormat().format(model.getProgress()*0.01);
				value[0] = model.getProgress()*0.01;
				text[0] = textString;
			} else {
				value[0] = 0.0026;
				text[0] = "";
			}
		} else if(model.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_START_BACKUP || 
				model.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_TAKING_SNAPSHOT ||
				model.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_CREATING_VIRTUAL_DISKS)
		{
			// to deal with the immediately started new job after first job fails. 
			//We need to refresh the process bar when it is just beginning				
			value[0] = 0.0026;
			text[0] = "";
		}
		else if (totalSize>0){
			getProgress(totalSize, processedSize, value, text);
			/*double percent = ((double)processedSize)/totalSize;
			if (percent>=1)
				((ProgressBar)widget).updateProgress(1, UIContext.Messages.jobMonitorProgressBarLabel(100, transferedSizeLabel, transferedSizeLabel));
			else
				((ProgressBar)widget).updateProgress(percent, UIContext.Messages.jobMonitorProgressBarLabel((int)(percent*100), transferedSizeLabel, totalSizeLabel));*/
		}
		
		return true;
	}
	
	private static void getProgress(long totalBytes, long processedBytes, double[] value, String[] text) {
		String transferedSizeLabel = Utils.bytes2String(processedBytes);
		String totalSizeLabel = Utils.bytes2String(totalBytes);
	
		if (totalBytes>0){
			double percent = ((double)processedBytes)/totalBytes;
			if (percent>=1) {
				value[0] = 1;
				text[0] = UIContext.Messages.jobMonitorProgressBarLabel(100, transferedSizeLabel, transferedSizeLabel);
			} else {
				value[0] = percent;
				text[0] = UIContext.Messages.jobMonitorProgressBarLabel((int)(percent*100), transferedSizeLabel, totalSizeLabel);
			}
		}else {
			value[0] = 0.0026;
			text[0] = "";
		}
	}
	
	public static String ArchivejobMonitorPhase2String(long phase){
		if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_START_BACKUP)
			return UIContext.Constants.jobMonitorPhaseArchiveStartBackup();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_TAKING_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseBackupTakingSnapShot();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_CREATING_VIRTUAL_DISKS)
			return UIContext.Constants.jobMonitorPhaseBackupCreatingVirtualDisks();
		else if (phase == JobMonitorModel.ARCHIVE_PHASE_CATALOG_UPDATE)
			return UIContext.Constants.jobMonitorPhaseArchiveUpdateCatalog();
		else if (phase == JobMonitorModel.COPY_PHASE_ESTIMATE_DATA)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseEstimateData();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_REPLICATIING_VOLUMES)
			return UIContext.Constants.jobMonitorPhaseArchiveReplicatingVolumes();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_DELETING_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseBackupDeletingSnapshot();
		else if (phase == JobMonitorModel.PHASE_CANCELING)
			return UIContext.Constants.jobMonitorPhaseCanceling();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_CREATE_METADATA)
			return UIContext.Constants.jobMonitorPhaseBackupCreateMetadata();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_COLLECT_DR_INFO)
			return UIContext.Constants.jobMonitorPhaseBackupCollectDRInfo();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_PURGE_SESSION)
			return UIContext.Constants.jobMonitorPhaseBackupPurgeSession();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_JOB_END)
			return UIContext.Constants.jobMonitorPhaseBackupJobEnd();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_PRE_JOB)
			return UIContext.Constants.jobMonitorPhaseBackupPreJob();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_POST_SNAPSHOT)
			return UIContext.Constants.jobMonitorPhaseBackupPostSnapshot();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_POST_JOB)
			return UIContext.Constants.jobMonitorPhaseBackupPostJob();		
		else
			return UIContext.Constants.unknown();
	}
	
	public static String ArchiveCataogjobMonitorPhase2String(long phase){
		if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_START_BACKUP)
			return UIContext.Constants.jobMonitorPhaseArchiveCatalogJobStart();
		else if (phase == JobMonitorModel.ARCHIVE_PHASE_CATALOG_UPDATE)
			return UIContext.Constants.jobMonitorPhaseArchiveUpdateCatalog();
		else if (phase == JobMonitorModel.PHASE_BACKUP_PHASE_JOB_END)
			return UIContext.Constants.jobMonitorPhaseArchiveCatalogPhaseEnd();		
		else if (phase == JobMonitorModel.COPY_PHASE_ESTIMATE_DATA)
			return UIContext.Constants.jobMonitorPhaseCopyPhaseEstimateData();
		else
			return UIContext.Constants.unknown();
	}
		
	public static String bmrJobMonitorPhase2String(long phase, String diskName) {
		if (phase == JobMonitorModel.BMR_PHASE_START_BMR) {
			return UIContext.Constants.jobMonitorBMRPhaseStart();
		} else if (phase == JobMonitorModel.BMR_PHASE_RESTORE_DATA) {
			return UIContext.Constants.jobMonitorBMRPhaseRunning();
		} else if (phase == JobMonitorModel.BMR_PHASE_END_BMR) {
			return UIContext.Constants.jobMonitorBMRPhaseEnd();
		} else if (phase == JobMonitorModel.LINUX_PHASE_RESTORE_VOLUME) {
			return UIContext.Messages.linuxJobMonitorRestoreVolume(diskName);
		} else if (phase == JobMonitorModel.LINUX_PHASE_CONNECT_TARGET) {
			return UIContext.Constants.linuxJobMonitorConnectTarget();
		} else if (phase == JobMonitorModel.LINUX_PHASE_CREATE_DISK) {
			return UIContext.Constants.linuxJobMonitorCreateDiskLayout();
		} else if (phase == JobMonitorModel.LINUX_PHASE_WRAPUP) {
			return UIContext.Constants.linuxJobMonitorWrapup();
		} else if (phase == JobMonitorModel.LINUX_PHASE_CONNECT_NODE) {
			return UIContext.Constants.linuxJobMonitorConnectNode();
		} else if (phase == JobMonitorModel.LINUX_PHASE_RESTORE_FILE) {
			return UIContext.Constants.linuxJobMonitorRestoreFiles();
		} else if (phase == JobMonitorModel.LINUX_PHASE_START) {
			return UIContext.Constants.linuxJobMonitorStart();
		} else if (phase == JobMonitorModel.LINUX_PHASE_INSTALL_BUILD) {
			return UIContext.Constants.linuxJobMonitorInstallBuild();
		} else if (phase == JobMonitorModel.LINUX_PHASE_CANCEL_JOB) {
			return UIContext.Constants.linuxJobMonitorCancelJob();
		} else {
			return UIContext.Constants.unknown();
		}
	}
	
	public static String replicationJobMonitorPhase2String(long phase, int jobType) {
		if (phase == JobMonitorModel.RPSREP_PHASE_START) {
			return UIContext.Constants.replicaJobPhaseStart();
		} else if (phase == JobMonitorModel.RPSREP_PHASE_PREPARE) {
			return UIContext.Constants.replicaJobPhasePrepare();
		} else if (phase == JobMonitorModel.RPSREP_PHASE_REPLICATE) {
			if (jobType == JobMonitorModel.JOBTYPE_RPS_REPLICATE
					|| jobType == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING) {				
				return UIContext.Constants.replicaJobPhaseDataTransfer();
			} else {				
				return UIContext.Constants.replicaJobPhaseDataReceiver();
			}
		} else if (phase == JobMonitorModel.RPSREP_PHASE_CANCELING) {
			return UIContext.Constants.replicaJobPhaseCancelling();
		} else if (phase == JobMonitorModel.RPSREP_PHASE_END) {
			return UIContext.Constants.replicaJobPhaseExit();
		} else {
			return UIContext.Constants.unknown();
		}
	}
	
	public static String VSphereJobMonitorTransferMode(long transferMode){
		if(transferMode == JobMonitorModel.TRANSFERMODE_SAN){
			return UIContext.Constants.jobMonitorTransferMode_San();
		}else if (transferMode == JobMonitorModel.TRANSFERMODE_NBD){
			return UIContext.Constants.jobMonitorTransferMode_Nbd();
		}else if (transferMode == JobMonitorModel.TRANSFERMODE_NBDSSL){
			return UIContext.Constants.jobMonitorTransferMode_Nbdssl();
		}else if (transferMode == JobMonitorModel.TRANSFERMODE_HOTADD){
			return UIContext.Constants.jobMonitorTransferMode_Hotadd();
		}else if (transferMode == JobMonitorModel.TRANSFERMODE_FILE){
			return UIContext.Constants.jobMonitorTransferMode_File();
		}else {
			return UIContext.Constants.unknown();
		}
	}
	
	public static String ConvertArchiveJobStatusToString(
			Integer archiveJobStatus) {
		switch(archiveJobStatus)
		{
		case ArchiveJobReady:
			return UIContext.Constants.readyStatus();
		case ScheduleScheduled:
			return UIContext.Constants.scheduledStatus();
		case ScheduleRunning:
			return UIContext.Constants.progressStatus();
		case ArchiveJobFinished:
			return UIContext.Constants.finishedStatus();
		case ArchiveJobIncomplete:
			return UIContext.Constants.incompleteStatus();
		case ArchiveJobCancelled:
			return UIContext.Constants.cancelledStatus();
		case ArchiveJobFailed:
			return UIContext.Constants.failed();
		case ArchiveJobCrashed:
			return UIContext.Constants.jobMonitorStatusCrashed();
		default:
			return UIContext.Constants.notApplicable();
		}
	}
	
	public static String getCatalogEclipsedTime(String value) {
//		long millseconds = new Date().getTime() - Utils.serverTimeToLocalTime(new Date(Long.parseLong(value) * 1000)).getTime();
		long millseconds = new Date().getTime() - new Date(Long.parseLong(value) * 1000).getTime();
		return Utils.milseconds2String(millseconds);
	}
	
	public static String getCurrentVolumn(JobMonitorModel jModel) {
		String volumeName = null;
		if(jModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT)
			volumeName = jModel.getGRTMailFolder();
		else if(jModel.getCTCurCatVol() == null || jModel.getCTCurCatVol().isEmpty())
			volumeName = jModel.getCurVolMntPoint();
		else
			volumeName = jModel.getCTCurCatVol();
		
		if (volumeName==null || volumeName.isEmpty())
			return UIContext.Constants.NA(); 
		else
			return volumeName;
	}
	
	private static String formatBackupTimeForCatalog(JobMonitorModel model) {
		if(model.getCTBKStartTime()!=null && model.getCTBKStartTime().length()>0)
		{
			Date time = Utils.serverString2LocalDate(model.getCTBKStartTime());
			DateWrapper wrapper = new DateWrapper(time);
			long serverTimeDiffToUTC = UIContext.serverVersionInfo.getTimeZoneOffset(); 
			wrapper = wrapper.addMillis((int)serverTimeDiffToUTC);
			return FormatUtil.getTimeDateFormat().format(wrapper.asDate());
		}
		else		
			return "";
		
	}
	
	public static boolean isJobFinishedWithStatus(int status) {
		switch(status){
		case JobMonitorModel.JOBSTATUS_CRASH:
		case JobMonitorModel.JOBSTATUS_FAILED:
		case JobMonitorModel.JOBSTATUS_FINISHED:
			return true;
			default:
				return false;
		}
	}
	
	public static boolean isJobDone(long jobType, long jobPhase, int jobStatus) {
		if(jobType == JobMonitorModel.JOBTYPE_BACKUP || jobType == JobMonitorModel.JOBTYPE_VM) {
			if(jobPhase == JobMonitorModel.PHASE_BACKUP_PHASE_PROCESS_EXIT) 
				return true;
		}else if (jobType == JobMonitorModel.JOBTYPE_RESTORE){
			if(jobPhase == JobMonitorModel.RESTORE_PHASE_PROCESS_EXIT) 
				return true;
		}else {
			if(jobPhase == JobMonitorModel.PHASE_BACKUP_PHASE_JOB_END
					|| jobPhase == JobMonitorModel.CATPROC_PHASE_CATALOG_GENERATE_FINISH
					|| Utils.isJobFinishedWithStatus(jobStatus)){
				return true;
			}
		}
		
		return false;
	}
	
	public static String getCompressLevel(long level) {
		String strLevel = null;
		switch(new Long(level).intValue()) {
		case IConstants.COMPRESSIONNONE:
			strLevel = UIContext.Constants.settingsCompressionNone();
			break;
		case IConstants.COMPRESSIONNONEVHD:
			strLevel = UIContext.Constants.settingsNoCompressionVHD();
			break;
		case IConstants.COMPRESSIONSTANDARD:
			strLevel = UIContext.Constants.settingsCompreesionStandard();
			break;
		case IConstants.COMPRESSIONMAX:
			strLevel = UIContext.Constants.settingsCompressionMax();
			break;
			default:
				break;
		}
		return strLevel;
	}
	
	public static String getStatusMessage(int statusType,int subType,int status,String[] parameter){
		String statusMessage = null;
		switch(statusType){
		case VMStatusModel.VM_STATUS_TYPE_ERROR:
			statusMessage = getErrorMessage(subType,status,parameter);
			break;
		case VMStatusModel.VM_STATUS_TYPE_WARNING:
			statusMessage = getWarningMessage(subType,status,parameter);
			break;
			default:
				break;
		}
		return statusMessage;
	}
	
	public static String getErrorMessage(int subType,int status,String[] parameter){
		String errorMessage = null;
		switch(subType){
		case VMStatusModel.VM_STATUS_ERROR_TYPE_VC:
			errorMessage = getVCErrorMessage(status,parameter);
			break;
		case VMStatusModel.VM_STATUS_ERROR_TYPE_VCLOUD_DIRECTOR:
			errorMessage = getVCloudErrorMessage(status,parameter);
			break;	
		default:
				break;
		}
		return errorMessage;
	}
	
	public static String getVCErrorMessage(int status,String[] parameter){
		String vcError = null;
		switch(status){
		case VMStatusModel.VM_STATUS_ERROR_VC_CANNOT_CONNECT:
			vcError = UIContext.Messages.vSphereVCCannotConnect(parameter[0]);
			break;
		case VMStatusModel.VM_STATUS_ERROR_VC_CREDENTIAL_WRONG:
			vcError = UIContext.Messages.vSphereVCCredentialWrong(parameter[0]);
			break;
			default:
				break;
		}
		return vcError;
	}
	
	public static String getVCloudErrorMessage(int status,String[] parameter){
		String vcError = null;
		switch(status){
		case VMStatusModel.VM_STATUS_ERROR_VC_CANNOT_CONNECT:
			vcError = UIContext.Messages.vSphereVCloudDirectorCannotConnect(parameter[0]);
			break;
		default:
				break;
		}
		return vcError;
	}
	
	public static String getWarningMessage(int subType,int status,String[] parameter){
		String warningMessage = null;
		switch(subType){
		case VMStatusModel.VM_STATUS_WARNING_TYPE_VIX:
			warningMessage = getVixWarningMessage(status,parameter);
			break;
		case VMStatusModel.VM_STATUS_WARNING_TYPE_VM_TOOL:
			if(parameter == null || parameter.length == 0) {
				parameter = new String[]{UIContext.backupVM.getVmHostName()};
			}
			warningMessage = getVMToolWarningMessage(status,parameter);
			break;
		case VMStatusModel.VM_STATUS_WARNING_TYPE_VM_POWER:
			if(parameter == null || parameter.length == 0) {
				parameter = new String[]{UIContext.backupVM.getVmHostName()};
			}
			warningMessage = getVMPowerWarningMessage(status,parameter);
			break;
			default:
				break;
		}
		return warningMessage;
	}
	
	public static String getVixWarningMessage(int warningStatus,String[] parameters){
		String warningMessage = null;
		if(warningStatus == VMStatusModel.VM_STATUS_WARNING_VIX_STATUS_NOT_INSTALL){
			warningMessage = UIContext.Messages.vSphereVixNotInstallWarning(parameters[0]);
		}else if(warningStatus == VMStatusModel.VM_STATUS_WARNING_VIX_STATUS_OUT_OF_DATE){
			warningMessage = UIContext.Messages.vSphereVixOutOfDateWarning(parameters[0]);
		}
		return warningMessage;
	}
	
	public static String getVMToolWarningMessage(int warningStatus,String[] parameters){
		String warningMessage = null;
		if(warningStatus == VMStatusModel.VM_STATUS_WARNING_VM_TOOL_STATUS_ERROR){
			warningMessage = UIContext.Messages.vSphereVMToolNotInstall(parameters[0]);
		}else if(warningStatus == VMStatusModel.VM_STATUS_WARNING_VM_TOOL_STATUS_NOT_INSTALL){
			warningMessage = UIContext.Messages.vSphereVMToolNotInstall(parameters[0]);
		}else if(warningStatus == VMStatusModel.VM_STATUS_WARNING_VM_TOOL_STATUS_OUTOFDATE){
			warningMessage = UIContext.Messages.vSphereVMToolOutOfDate(parameters[0]);
		}
		return warningMessage;
	}
	
	public static String getVMPowerWarningMessage(int warningStatus,String[] parameters){
		String warningMessage = null;
		if(warningStatus == VMStatusModel.VM_STATUS_WARNING_VM_POWER_OFF){
			warningMessage = UIContext.Messages.vSphereVMPowerOff(parameters[0]);
		}else if(warningStatus == VMStatusModel.VM_STATUS_WARNING_VM_POWER_ERROR){
			warningMessage = UIContext.Messages.vSphereVMNotPowerOn(parameters[0]);
		}else if(warningStatus == VMStatusModel.VM_STATUS_WARNING_VM_SUSPENDED){
			warningMessage = UIContext.Messages.vSphereVMSuspended(parameters[0]);
		}
		return warningMessage;
	}
	
	 public static Template getTooltipWrapTemplate(String tipText) {
		 		 
			String modTip = "<div class='tooltip-item'><pre>"+tipText+"</pre></div>";
			
			if(isFirefoxBrowser())
				modTip = "<div class='tooltip-item'>"+tipText+"</div>";
			
			Template template = new Template(modTip);
		    return template; 
		  };
		  
	public static String getJobMonitorKey(long jobtype, long jobID){
		if(jobtype == JobMonitorModel.JOBTYPE_CATALOG_GRT)
			return jobID + "_" + jobtype;
		else
			return String.valueOf(jobtype);
	}
	
	public static DisclosurePanel getDisclosurePanel(String title)
	{
		DisclosurePanel disclosurePanel; 
		disclosurePanel =  new DisclosurePanel((DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class),
				title, true);
		disclosurePanel.setWidth("100%");
		disclosurePanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		disclosurePanel.setOpen(true);
		return disclosurePanel;
	}
	
	public static native int getScreenWidth() /*-{ 
    return $wnd.screen.width;
 }-*/;
	
	public static native int getScreenHeight() /*-{ 
    return $wnd.screen.height;
 }-*/;
	
	/**
	* Gets the name of the used browser.
	*/
	public static native String getBrowserName() /*-{
	    return navigator.userAgent.toLowerCase();
	}-*/;
	
	/**
	* Returns true if the current browser is Firefox.
	*/
	public static boolean isFirefoxBrowser() {
	    return getBrowserName().toLowerCase().contains("firefox");
	}
	
	// Add tool tip without showing header
	public static ToolTip addToolTip(Component widget, String toolTip)
	{
		ToolTip tp = widget.getToolTip();
		if(tp == null) {
			ToolTipConfig tpConfig = new ToolTipConfig(toolTip);
//			tp = new ToolTip(widget, tpConfig);
//			tp.setHeaderVisible(false);
			widget.setToolTip(tpConfig);
			tp = widget.getToolTip();
		}else {
			ToolTipConfig tpConfig = tp.getToolTipConfig();
			tpConfig.setText(toolTip);
			tp.update(tpConfig);
		}		
		
		return tp;
	}
	
	// Add tool tip without showing header
	public static ToolTip addToolTip(Component widget, String toolTip,int delayTime)
	{
		ToolTip tp = addToolTip(widget,toolTip);
		tp.getToolTipConfig().setDismissDelay(delayTime);		
		return tp;
	}
	
	public static void updateToolTip(ToolTip tip, String msg) {
		if(tip != null) {
			ToolTipConfig tpConfig = tip.getToolTipConfig();
			tpConfig.setText(msg);
			tip.update(tpConfig);
		}
	}
	
	public static String getMachineName(String remotePath) {
		if(remotePath == null)
			return null;
		if(remotePath.startsWith("\\\\") && remotePath.length() > 2) {
			int indexBackSlash = remotePath.indexOf("\\", 3);
			int indexSlash = remotePath.indexOf("/", 3);
			int index = indexBackSlash == -1 || indexSlash > 0 && indexBackSlash > indexSlash ? indexSlash : indexBackSlash;
			if(index < 0)
				index = remotePath.length();

			return remotePath.substring(2, index);
		}
		
		return null;
	}
	
	
	public static void cacheConnectionInfo(String dest, String userName, String password) {
		if(connectionCache != null) {
			if(!isEmptyOrNull(dest)&&!isEmptyOrNull(password)&&!isEmptyOrNull(userName)){
				String[] info=new String[]{"",userName,password};
				connectionCache.put(Utils.getMachineName(dest),info);
			}
		}
		
	}
	
	public static boolean isEmptyOrNull(String target){		
		if (target == null || target.equals("") || target.trim().equals(""))
			return true;
		return false;
	}
	
	public static String[] getConnectionInfo(String path) {
		if(connectionCache != null) {
			return connectionCache.get(getMachineName(path));
		}
		
		return null;
	}
	
	public static void clearConnectionCache() {
		if(connectionCache != null) {
			connectionCache.clear();
			connectionCache = null;
		}	
	}
	
	public static void setMessageBoxDebugId(MessageBox messageBox) {
		if (messageBox == null)
			return;

		messageBox.getDialog().ensureDebugId("60b952c8-a510-4b91-bf47-1469d8cb71f0");
		Button OKButton = messageBox.getDialog().getButtonById(Dialog.OK);
		if (OKButton != null) {
			OKButton.ensureDebugId("44cb254a-6123-44bf-82e3-34e695b33890");
		}
		Button CancelButton = messageBox.getDialog().getButtonById(Dialog.CANCEL);
		if (CancelButton != null) {
			CancelButton.ensureDebugId("d2a6708a-8f3a-40a7-9452-3c41c3c71737");
		}
		Button YesButton = messageBox.getDialog().getButtonById(Dialog.YES);
		if (YesButton != null) {
			YesButton.ensureDebugId("06fcf1d7-fe34-499c-90ba-b16b1f57affa");
		}
		Button NoButton = messageBox.getDialog().getButtonById(Dialog.NO);
		if (NoButton != null) {
			NoButton.ensureDebugId("167cb576-6616-4044-aa4e-f8d20df77125");
		}
		Button CloseButon = messageBox.getDialog().getButtonById(Dialog.CLOSE);
		if (CloseButon != null) {
			CloseButon.ensureDebugId("2f25848f-08bd-48e7-9590-da57507263fc");
		}
	}
	
	public static void setMessageBoxDebugId3(com.sencha.gxt.widget.core.client.box.MessageBox messageBox) {
		if (messageBox == null)
			return;
		
		messageBox.ensureDebugId("60b952c8-a510-4b91-bf47-1469d8cb71f0");
		TextButton OKButton = messageBox.getButton(PredefinedButton.OK);
		if (OKButton != null) {
			OKButton.ensureDebugId("44cb254a-6123-44bf-82e3-34e695b33890");
		}
		TextButton CancelButton = messageBox.getButton(PredefinedButton.CANCEL);
		if (CancelButton != null) {
			CancelButton.ensureDebugId("d2a6708a-8f3a-40a7-9452-3c41c3c71737");
		}
		TextButton YesButton = messageBox.getButton(PredefinedButton.YES);
		if (YesButton != null) {
			YesButton.ensureDebugId("06fcf1d7-fe34-499c-90ba-b16b1f57affa");
		}
		TextButton NoButton = messageBox.getButton(PredefinedButton.NO);
		if (NoButton != null) {
			NoButton.ensureDebugId("167cb576-6616-4044-aa4e-f8d20df77125");
		}
		TextButton CloseButon = messageBox.getButton(PredefinedButton.CLOSE);
		if (CloseButon != null) {
			CloseButon.ensureDebugId("2f25848f-08bd-48e7-9590-da57507263fc");
		}
	}
	
	public static String GetServerDateString(DateWrapper date,
			DateWrapper time,
			boolean isKeepHoursMinSec) {
		String strSvrBeginDate = "";
		strSvrBeginDate += date.getFullYear();
		strSvrBeginDate += "-" + (1 + date.getMonth());
		strSvrBeginDate += "-" + date.getDate();

		if (!isKeepHoursMinSec) {
			strSvrBeginDate += " 00:00:00";
		} else {
			strSvrBeginDate += " " + time.getHours();
			strSvrBeginDate += ":" + time.getMinutes();
			strSvrBeginDate += ":" + time.getSeconds();
		}
		return strSvrBeginDate;
	}
	
	public static boolean is24Hours()
	{
		String fmt = FormatUtil.getTimeDateFormatPattern();
		if( fmt.indexOf('H') > -1 || fmt.indexOf('k') > -1 )
			return true;
		return false;
	}
	
	public static int minHour()
	{
		String fmt = FormatUtil.getTimeDateFormatPattern();
		if( fmt.indexOf('H') > -1 || fmt.indexOf('K') > -1 )
			return 0;
		else
			return 1;
	}
	
	public static int maxHour()
	{
		String fmt = FormatUtil.getTimeDateFormatPattern();
		if( is24Hours())
		{
			if( fmt.indexOf('H') > -1 )
				return 23;
			else
				return 24;
		}
		else
		{
			if( fmt.indexOf('h') > -1 )
				return 12;
			else
				return 11;
		}
	}
	
	public static boolean isHourPrefix()
	{
		String fmt = FormatUtil.getTimeDateFormatPattern();
		if( fmt.indexOf("HH") > -1 || fmt.indexOf("hh") > -1 ||
				fmt.indexOf("KK") > -1 || fmt.indexOf("kk") > -1 )
			return true;
		return false;
	}
	
	public static boolean isMinutePrefix()
	{
		String fmt = FormatUtil.getTimeDateFormatPattern();
		if( fmt.indexOf("mm") > -1 )
			return true;
		return false;
	}
	
	public static String prefixZero( int val, int digit )
	{
		String str = Integer.toString(val);
		int pre = digit - str.length();
		for( int i = 0; i < pre; i++ )
			str = '0' + str;
		return str;
	}
	
	public static String getProductName() {
		String prodName = "";
		if(UIContext.uiType == 1){
			prodName = UIContext.productNamevSphere;
		}else{
			prodName = UIContext.productNameD2D;
		}
		return prodName;
	}
	
	public static String getMergeStatusMessage(int status) {
		String msg = UIContext.Constants.mergeJobPanelStatusNoJob();
		switch(status) {
		case MergeStatusModel.NOTRUNNING:
			msg = UIContext.Constants.mergeJobPanelStatusNoJob();
			break;
		case MergeStatusModel.PAUSED:
			msg = UIContext.Constants.mergeJobPanelStatusJobPaused();
			break;
		case MergeStatusModel.PAUSED_MANUALLY:
			msg = UIContext.Constants.mergeJobPanelStatusJobPausedManually();
			break;
		case MergeStatusModel.RUNNING:
			msg = "";
			break;
		case MergeStatusModel.PAUSED_NO_SCHEDULE:
			msg = UIContext.Constants.mergeJobPausedNoSchedule();
			break;
		case MergeStatusModel.FAILED:
			msg = UIContext.Constants.mergeJobPanelStatusNoJob();
			break;
		}
		
		return msg;
	}
	
	public static String getMergePhaseMessage(MergeJobMonitorModel mjm) {
		if (mjm == null || mjm.getJobPhase() == null) {
			return UIContext.Constants.mergeJobPhaseUNKNOWN();
		} else {
			return getMergePhaseMessage(mjm.getJobPhase().intValue());
		}
	}
	
	public static String getMergePhaseMessage(int mergeJobPhase) {
		if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_CONTINUE_FAILED_MERGE.ordinal()) {
			return UIContext.Constants.mergeJobPhaseContinueFailedMerge();
		}else if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_PROC_EXIT.ordinal()){
			return UIContext.Constants.mergeJobPhaseProcessExit();
		}else if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_PROC_ENTER.ordinal()){
			return UIContext.Constants.mergeJobPhaseEnterProcess();
		}else if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_INIT_BKDEST.ordinal()){
			return UIContext.Constants.mergeJobPhaseInitDest();
		}else if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_ENUM_SESS.ordinal()){
			return UIContext.Constants.mergeJobPhaseEnumSession();
		}else if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_LOCK_SESS.ordinal()){
			return UIContext.Constants.mergeJobPhaseLockSession();
		}else if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_WAIT_4_LOCK.ordinal()){
			return UIContext.Constants.mergeJobPhaseWaitLock();
		}else if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_MERGE_SESS.ordinal()){
			return UIContext.Constants.mergeJobPhaseMergeSession();
		}else if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_UNINIT_BKDES.ordinal()){
			return UIContext.Constants.mergeJobPhaseUninitDest();
		}else if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_WAIT_STOP.ordinal()){
			return UIContext.Constants.mergeJobPhaseWaitStop();
		}else if(mergeJobPhase == MergeJobMonitorModel.JobPhase.EJP_END_OF_JOB.ordinal()){
			return UIContext.Constants.mergeJobPhaseEndJob();
		}else {
			return UIContext.Constants.mergeJobPhaseUNKNOWN();
		}
	}
	
	public static void updateMergeProgress(ProgressBar bar, MergeJobMonitorModel model) {
		double[] value = new double[1];
		String[] text = new String[1];
		
		getMergeProgress(model, value, text);
		
		bar.updateProgress(value[0], text[0]);
	}
	
	public static void getMergeProgress(MergeJobMonitorModel model, double[] value, String text[]) {
		float perct = model.getMergePercentage();
		if (perct > 0) {
			NumberFormat nf = NumberFormat.getPercentFormat();
			value[0] = perct;
			text[0] = nf.format(perct);
		} else {
			value[0] = 0.0026;
			text[0] = "";
		}
	}
	
	public static void updateVHDMergeProgress(LabelField progressLabel, MergeJobMonitorModel model) {
		progressLabel.setValue(getVHDMergeProgress(model));
	}
	
	public static String getVHDMergeProgress(MergeJobMonitorModel model) {
		if (model.getSessCnt2Merge() > 0) {
			return UIContext.Messages.mergeVHDProgress(model.getSessCntMerged(), model.getSessCnt2Merge());
		} else {
			return UIContext.Constants.noRecoveryPointMerged();
		}
	}
	
	public static void updateVHDMergeProgress(Label progressLabel, MergeJobMonitorModel model) {
		if(model.getSessCnt2Merge() > 0){
			progressLabel.setHtml(UIContext.Messages.mergeVHDProgress(
					model.getSessCntMerged(), model.getSessCnt2Merge()));
		}else {
			progressLabel.setHtml(UIContext.Constants.noRecoveryPointMerged());
		}
	}
	
	public static MessageBox showMessage(String title, String iconStyle, String message) {
		MessageBox msgBox = new MessageBox();
		msgBox.setIcon(iconStyle);
		msgBox.setTitleHtml(title);
		msgBox.setModal(true);
//		msgBox.setMinWidth(400);
		msgBox.setMessage(message);
		msgBox.show();
		Utils.setMessageBoxDebugId(msgBox);
		return msgBox;
	}
	public static MessageBox showErrorMessage(String message) {
		return showMessage(UIContext.productNameD2D,MessageBox.ERROR,message);
	}
	
	public static String getDriveLetter(String path)
	{
		String driveLetter = "";
		if(path!=null)
		{
			if(path.length()>=3)
			{
				driveLetter = path.substring(0, 3);
			}
			else if(path.length()==2)
			{
				driveLetter = path.substring(0, 2)+"\\";
			}

		}
		return driveLetter;
	}
	
	public static boolean isValidLocalPath(String path){
		if(path == null)
			return false;
		path=path.trim();
//		if(path.length()<2)
//			return false;
//		if(path.length() >= 2 && path.charAt(1) != ':')
//			return false;
//		if(path.length() >= 3 && path.charAt(1) == ':' && path.charAt(2) != '\\')
//			return false;
		return path.matches("[a-zA-Z]:(\\\\[^:]*)?");

	}
	
	public static String calUnitSize(double value, String currentunit, String format){
		String[] units=new String[]{"B","KB","MB","GB","TB"};
		String[] unitLabels=new String[]{UIContext.Constants.bytes(), UIContext.Constants.KB(), UIContext.Constants.MB(),
				UIContext.Constants.GB(),UIContext.Constants.TB()};
		while(true){
			if(value>1024){
				int i = 0;
				for (; i < units.length-1; i++) {
					if(currentunit.equals(units[i])){
						currentunit=units[i+1];
						break;
					}
				}
				if(i<units.length-1){
					value=value/1024;
				}else{
					break;
				}
			}else{
				break;
			}
		}
		String currentunitLable=null;
		for (int i=0; i < units.length; i++) {
			if(currentunit.equals(units[i])){
				currentunitLable=unitLabels[i];
				break;
			}
		}
		if(currentunitLable==null){
			throw new RuntimeException(currentunit);
		}
		return NumberFormat.getFormat(format).format(value)+currentunitLable;
	}
	
	public static String getDayofWeek(Integer dayofWeek) {
		  String ret;
		  switch(dayofWeek.intValue()) {
		  case WeekModel.SUNDAY:
			  ret = UIContext.Constants.weekSunday();
			  break;
		  case WeekModel.MONDAY:
			  ret = UIContext.Constants.weekMonday();
			  break;
		  case WeekModel.TUESDAY:
			  ret = UIContext.Constants.weekTuesday();
			  break;
		  case WeekModel.WEDNESDAY:
			  ret = UIContext.Constants.weekWednesday();
			  break;
		  case WeekModel.THURSDAY:
			  ret = UIContext.Constants.weekThursday();
			  break;
		  case WeekModel.FRIDAY:
			  ret = UIContext.Constants.weekFriday();
			  break;
		  case WeekModel.SATURDAY:
			  ret = UIContext.Constants.weekSaturday();
			  break;
			  default:
				  ret = UIContext.Constants.weekSunday();
		  }
		  return ret;
	  }
	
	public static void setComboboxValue(ComboBox comboBox, int index){
		comboBox.setValue(comboBox.getStore().getAt(index));
 	}
	
	public static void popupError(String message, String title){
		MessageBox box = new MessageBox();
		box.setTitleHtml(title);
		box.setIcon(MessageBox.ERROR);
		box.setButtons(MessageBox.OK);
		box.setModal(true);
		box.setMessage(message);
		box.show();
	}
	
	public static CloudModel[] filterBuckets(CloudModel[] buckets, String mode, String hostName)
	{
		Vector<CloudModel> vector = new Vector<CloudModel>(buckets.length);
		
		StringBuffer filterToken = new StringBuffer(UIContext.cloudBucketD2DArchiveLabel);
		StringBuffer filterTokenV2 = new StringBuffer(UIContext.cloudBucketD2DF2CLabel);
		StringBuffer filterTokenV3 = new StringBuffer(UIContext.cloudBucketD2DLabel);
		StringBuffer filterTokenV4 = new StringBuffer(UIContext.cloudBucketARCserveLabel);
		
		if(mode.equals(ARCHIVE_MODE))
		{
			filterToken.append(hostName);
			filterTokenV2.append(hostName);
			filterTokenV3.append(hostName);
			filterTokenV4.append(hostName);
		}
		
		for (int i = 0; i < buckets.length; i++) {
			if(buckets[i].getBucketName().startsWith(filterToken.toString())|| buckets[i].getBucketName().startsWith(filterTokenV2.toString())
					|| buckets[i].getBucketName().startsWith(filterTokenV3.toString())|| buckets[i].getBucketName().startsWith(filterTokenV4.toString()))
			{
				CloudModel model = new CloudModel();
				model.setBucketName(buckets[i].getBucketName());
				model.setEncodedBucketName(buckets[i].getEncodedBucketName());
				vector.add(model);
			}
		}

		CloudModel[] filteredBuckets = new CloudModel[vector.size()];
		vector.copyInto(filteredBuckets);
		return filteredBuckets;
	}
	
	public static Date formatTimeToServerTime(Date date, long serverTimezoneOffset ){
		if(serverTimezoneOffset == 0) {
			serverTimezoneOffset = UIContext.serverVersionInfo.getTimeZoneOffset();
		}
		TimeZone serverTimeZone = TimeZone
				.createTimeZone((int) serverTimezoneOffset / (-1000 * 60));
		int diff = (date.getTimezoneOffset() - serverTimeZone.getOffset(date)) * 60000;
	    Date keepDate = new Date(date.getTime() + diff);
	    Date keepTime = keepDate;
	    if (keepDate.getTimezoneOffset() != date.getTimezoneOffset()) {
			if (diff > 0) {
				diff -= NUM_MILLISECONDS_IN_DAY;
			} else {
				diff += NUM_MILLISECONDS_IN_DAY;
			}
			keepTime = new Date(date.getTime() + diff);
	    }
	    return keepTime;
	}
	
	public static LayoutData createLineLayoutData(){
		FlowData l=new FlowData(0,0,10,0);
		return l;
	}
	
	public static LayoutContainer createFormLayout(LabelField label, Widget comp){
		HorizontalPanel p=new HorizontalPanel();
		//shaji02: for adding debug id.
		p.ensureDebugId("133be4d9-0fb4-467c-a6d2-f022eb5a3c19");
		TableData td = new TableData();
		td.setWidth(LabelWidth);
		td.setVerticalAlign(VerticalAlignment.TOP);
		p.add(label, td);
		p.add(comp);
		return p;
	}
	
	public static LayoutContainer createFormLayout(String name, Widget comp){
		HorizontalPanel p=new HorizontalPanel();
//shaji02: for adding debug id.
p.ensureDebugId("133be4d9-0fb4-467c-a6d2-f022eb5a3c07");
		TableData td = new TableData();
		td.setWidth(LabelWidth);
		td.setVerticalAlign(VerticalAlignment.TOP);
		p.add(new LabelField(name), td);
		p.add(comp);
		return p;
	}
	
	public static Layout createLineLayout() {
		return new FlowLayout();
	}
	
	public static D2DTimeModel getD2DTime(DateWrapper dw, boolean isKeepHoursMinSec) {		
		D2DTimeModel time = new D2DTimeModel();
		time.setYear(dw.getFullYear());
		time.setMonth(dw.getMonth());
		time.setDay(dw.getDate());
		
		if (!isKeepHoursMinSec) {
			time.setHour(0);
			time.setHourOfDay(0);
			time.setMinute(0);
			time.setSecond(0);
		} else {
			time.setHour(dw.getHours());
			time.setHourOfDay(dw.getHours());
			time.setMinute(dw.getMinutes());
			time.setSecond(dw.getSeconds());
//			time.setAmPM(-1);	
		}		
		return time;
	}
	
	public static D2DTimeModel getD2DTime(DateWrapper dw) {		
		return getD2DTime(dw, true);
	}		

	public static D2DTimeModel getD2DTime(Date date, Date time) {
		DateWrapper dateWrapper = new DateWrapper(date);
		DateWrapper timeWrapper = new DateWrapper(time);
		
		dateWrapper = dateWrapper.clearTime();
		dateWrapper = dateWrapper.addHours(timeWrapper.getHours());
		dateWrapper = dateWrapper.addMinutes(timeWrapper.getMinutes());
		dateWrapper = dateWrapper.addSeconds(timeWrapper.getSeconds());
		
		return getD2DTime(dateWrapper);
	}
	
	public static JobMonitorModel convert2JobMonitorModel(JobMonitor jobMonitor) {
		if (jobMonitor == null)
			return null;

		JobMonitorModel model = new JobMonitorModel();
		model.setID(jobMonitor.getJobId());
		model.setBackupStartTime(jobMonitor.getBackupStartTime());
		model.setCurrentProcessDiskName(jobMonitor.getCurrentProcessDiskName());
		model.setElapsedTime(jobMonitor.getElapsedTime());
		model.setEstimateBytesDisk(jobMonitor.getEstimateBytesDisk());
		model.setEstimateBytesJob(jobMonitor.getEstimateBytesJob());
		model.setFlags(jobMonitor.getFlags());
		model.setJobMethod(jobMonitor.getJobMethod());
		model.setJobPhase(jobMonitor.getJobPhase());
		model.setJobStatus(jobMonitor.getJobStatus());
		model.setJobType(jobMonitor.getJobType());
		model.setSessionID(jobMonitor.getSessionID());
		model.setUlBeginSessID(jobMonitor.getUlBeginSessID());
		model.setUlEndSessID(jobMonitor.getUlEndSessID());
		model.setTransferBytesDisk(jobMonitor.getTransferBytesDisk());
		model.setTransferBytesJob(jobMonitor.getTransferBytesJob());
		model.setTransferMode(jobMonitor.getTransferMode());
		model.setVolMethod(jobMonitor.getVolMethod());
		model.setProgramCPUPercentage(jobMonitor.getnProgramCPU());
		model.setSystemCPUPercentage(jobMonitor.getnSystemCPU());
		model.setReadSpeed(jobMonitor.getnReadSpeed());
		model.setWriteSpeed(jobMonitor.getnWriteSpeed());
		model.setSystemReadSpeed(jobMonitor.getnSystemReadSpeed());
		model.setSystemWriteSpeed(jobMonitor.getnSystemWriteSpeed());
		model.setThrottling(jobMonitor.getThrottling());
		model.setTotalSizeRead(jobMonitor.getTotalSizeRead());
		model.setTotalSizeWritten(jobMonitor.getTotalSizeWritten());
		model.setEncInfoStatus(jobMonitor.getEncInfoStatus());
		model.setCurVolMntPoint(jobMonitor.getCurVolMntPoint());
		model.setCompressLevel(jobMonitor.getCompressLevel());
		model.setCTBKJobName(jobMonitor.getCtBKJobName());
		model.setCTBKStartTime(jobMonitor.getCtBKStartTime());
		model.setCTCurCatVol(jobMonitor.getCtCurCatVol());
		model.setCTDWBKJobID(jobMonitor.getCtDWBKJobID());
		model.setGRTEDB(jobMonitor.getWszEDB());
		model.setGRTMailFolder(jobMonitor.getWszMailFolder());
		model.setGRTProcessFolder(jobMonitor.getUlProcessedFolder());
		model.setGRTTotalFolder(jobMonitor.getUlTotalFolder());
		model.setUlMergedSession(jobMonitor.getUlMergedSessions());
		model.setUlTotalMegedSessions(jobMonitor.getUlTotalMegedSessions());
		model.setd2dServerName(jobMonitor.getD2dServerName() !=null ? jobMonitor.getD2dServerName().toLowerCase() : null);
		model.setPolicyName(jobMonitor.getRpsPolicyName());
		model.setJobMonitorId(jobMonitor.getJobMonitorId());
		model.setD2dUuid(jobMonitor.getD2dUuid());
		model.setVmInstanceUUID(jobMonitor.getVmInstanceUUID());
		model.setDedupe(jobMonitor.isEnableDedupe());
		model.setTotalUniqueData(jobMonitor.getUniqueData());

		model.setSrcRPS(jobMonitor.getSrcRPS());
		model.setDestRPS(jobMonitor.getDestRPS());
		model.setSrcDataStore(jobMonitor.getSrcDataStore());
		model.setDestDataStore(jobMonitor.getDestDataStore());
		model.setSrcCommonPath(jobMonitor.getSrcCommonPath());
		model.setDestCommonPath(jobMonitor.getDestCommonPath());
		model.setHistoryProductType(jobMonitor.getHistoryProductType());
		model.setSourceRPSId(jobMonitor.getSourceRPSId());
		model.setTargetRPSId(jobMonitor.getTargetRPSId());
		model.setNodeId(jobMonitor.getNodeId());
		model.setRunningServerId(jobMonitor.getRunningServerId());
		model.setAgentNodeName(jobMonitor.getAgentNodeName());
		model.setServerNodeName(jobMonitor.getServerNodeName());
		model.setProgress(jobMonitor.getProgress());
		model.setLinuxNode(jobMonitor.isLinuxNode());
		model.setStartTime(jobMonitor.getStartTime());
		model.setRemainTime(jobMonitor.getRemainTime());
		model.setRunningOnRPS(jobMonitor.isRunningOnRPS());
		model.setReplicationSavedBandWidth(jobMonitor.getReplicationSavedBandWidth());
		
		model.setTotalVMJobCount(jobMonitor.getUlTotalVMJobCount());
        model.setFinishedVMJobCount(jobMonitor.getUlFinishedVMJobCount());
        model.setCanceledVMJobCount(jobMonitor.getUlCanceledVMJobCount());
        model.setFailedVMJobCount(jobMonitor.getUlFailedVMJobCount());
        model.setLogicSpeed(jobMonitor.getnLogicSpeed());
        model.setVmHostName(jobMonitor.getVmHostName());
		return model;
	}
	
	public static MergeStatusModel mergeStatus2MergeStatusModel(MergeStatus status) {
		MergeStatusModel model = new MergeStatusModel();
		if(status == null)
			return model;
		switch(status.getStatus()) {
		case FAILED:
			model.setStatus(MergeStatusModel.FAILED);
			break;
		case NOTRUNNING:
			model.setStatus(MergeStatusModel.NOTRUNNING);
			break;
		case TORUNN:
			model.setStatus(MergeStatusModel.TO_RUN);
			break;
		case PAUSED:			
			model.setStatus(MergeStatusModel.PAUSED);
			break;
		case PAUSED_MANUALLY:
			model.setStatus(MergeStatusModel.PAUSED_MANUALLY);
			break;	
		case RUNNING:
			model.setStatus(MergeStatusModel.RUNNING);
			break;
		case PAUSING:
			model.setStatus(MergeStatusModel.PAUSING);
			break;
		case PAUSED_NO_SCHEDULE:
			model.setStatus(MergeStatusModel.PAUSED_NO_SCHEDULE);
			break;
		}
		
		if(status.getJobMonitor() != null) {
			model.jobMonitor = convertToMergeJobMonitorModel(status.getJobMonitor(), status.getServerNodeName());
		}
		model.setCanResume(status.isCanResume());
		model.setInSchedule(status.isInSchedule());
		model.setUUID(status.getUUID());
		model.setRecoverySet(status.isRecoverySet());
		model.setUpdateTime(status.getUpdateTime());
		model.setJobType(status.getJobType());
		model.setJobMonitorId(status.getJobMonitorId());
		model.setD2dUuid(status.getD2dUuid());
		model.setVmInstanceUUID(status.getVmInstanceUUID());
		model.setd2dServerName(status.getD2dServerName() !=null ? status.getD2dServerName().toLowerCase() : null);
		model.setNodeId(status.getNodeId());
		model.setRunningServerId(status.getRunningServerId());
		model.setAgentNodeName(status.getAgentNodeName());
		model.setServerNodeName(status.getServerNodeName());
		model.setHistoryProductType(status.getHistoryProductType());
		model.setRunningOnRPS(status.isRunningOnRPS());
		model.setStartTime(status.getStartTime());
		model.setJobId(status.getJobId());
		model.setVmHostName(status.getVmHostName());
		return model;
	}
	
	public static MergeJobMonitorModel convertToMergeJobMonitorModel(MergeJobMonitor jm, String serverNodeName) {
		if(jm == null)
			return null;
		MergeJobMonitorModel model = new MergeJobMonitorModel();
		model.setJobId(jm.getDwJobID());
		model.setJobPhase(jm.getDwMergePhase());
		model.setJobStatus(jm.getJobStatus());
		model.setCurDiskSig2Merge(jm.getDwCurDiskSig2Merge());
		model.setCurSess2Merge(jm.getDwCurSess2Merge());
		model.setDiskBytes2Merge(jm.getUllDiskBytes2Merge());
		model.setDiskBytesMerged(jm.getUllDiskBytesMerged());
		model.setDiskCnt2Merge(jm.getDwDiskCnt2Merge());
		model.setDiskCntMerged(jm.getDwDiskCntMerged());
		model.setEndStart(jm.getDwEndStart());
		model.setMergeMethod(jm.getDwMergeMethod());
		model.setMergeOpt(jm.getDwMergeOpt());
		model.setMergePercentage(jm.getfMergePercentage());
		model.setRetentionCnt(jm.getDwRetentionCnt());
		model.setSessBytes2Merge(jm.getUllSessBytes2Merge());
		model.setSessBytesMerged(jm.getUllSessBytesMerged());
		model.setSessCnt2Merge(jm.getDwSessCnt2Merge());
		model.setSessCntMerged(jm.getDwSessCntMerged());
		model.setSessStart(jm.getDwSessStart());
		model.setElapsedTime(jm.getUllElapsedTime());
		model.setStartTime(jm.getUllStartTime());
		model.setTotalBytes2Merge(jm.getUllTotalBytes2Merge());
		model.setTotalBytesMerged(jm.getUllTotalBytesMerged());
		model.setVmInstanceUUID(jm.getVmInstanceUUID());
		model.setTimeRemain(jm.getTimeRemain());
		model.setVHDMerge(jm.isVHDMerge());
		model.setSessRangeCnt(jm.getDwSessRangeCnt());
		model.setCurrentMergeRangeStart(jm.getCurrentMergeRangeStart());
		model.setCurrentMergeRangeEnd(jm.getCurrentMergeRangeEnd());
		model.setServerNodeName(serverNodeName);
		return model;
	}
	
	public static D2DTimeModel getD2DTime(Date date) {
		DateWrapper dateWrapper = new DateWrapper(date);				
		return getD2DTime(dateWrapper);
	}
	
	public static Date convertD2DTime(D2DTimeModel time) {
		if (time == null) {
			return new Date();
		} else if (time.getYear() > 1900) {
			DateWrapper dw = new DateWrapper(time.getYear(), time.getMonth(), time.getDay());		
			dw = dw.clearTime();
			dw = dw.addHours(time.getHourOfDay());
			dw = dw.addMinutes(time.getMinute());
			dw = dw.addSeconds(time.getSecond());						
			return dw.asDate();
		} else {
			return new Date();
		}
	}
	
	public static String trimUselessSplashFromRemotePath(String remotePath) {
		if (null == remotePath
				|| remotePath.length() < 5
				|| !remotePath.startsWith("\\\\")
				|| !remotePath.endsWith("\\\\")
				|| Utils.isValidRemotePath(remotePath)) {
			return remotePath;
		}			
		remotePath = remotePath.substring(0, remotePath.length() - 1);		
		return trimUselessSplashFromRemotePath(remotePath);				
	}
}