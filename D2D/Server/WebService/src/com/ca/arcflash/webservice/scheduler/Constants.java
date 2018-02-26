package com.ca.arcflash.webservice.scheduler;

public interface Constants {
	public static final String RUN_NOW = "runNow";
	
	public final static long JobExitPhase = 0x20;
	public final static long CatalogDone = 0xBF;
    public final static int FAILED_JOB_RSS_MAX=25;
    
    public final static long BackupJob_Phase_PROC_EXIT = 0xE;
    public final static long RestoreJob_Phase_PROC_EXIT = 0xE;
	//for conversion
	public final static int AF_JOBTYPE_CONVERSION = 40;
	
	public static final int QJDTO_B_DISABLE_CATALOG = 0x00000400;
	
	public final static int CATPROC_PHASE_VALIDATE_CATALOG_SCRIPT   =      0xB0;
	public final static int CATPROC_PHASE_PARSE_CATALOG_SCRIPT      =      0xB1;	
	
	public final static int PHASE_BACKUP_PHASE_WAITING		 		=  0x2000 + 10;
	public final static int AF_JOBTYPE_BACKUP = 0;
	public final static int AF_JOBTYPE_RESTORE = 1;
	public final static int AF_JOBTYPE_COPY = 2;
	public final static int AF_JOBTYPE_VM_BACKUP = 3;
	public final static int AF_JOBTYPE_HYPERV_VM_BACKUP = 4;
	public final static int AF_JOBTYPE_VM_RECOVERY = 5;
	public final static int JOBTYPE_CATALOG_FS = 11;
	public final static int JOBTYPE_CATALOG_APP = 12;
	public final static int JOBTYPE_CATALOG_GRT = 13;
	//for archive
	public final static int AF_JOBTYPE_ARCHIVE_BACKUP = 8;
	public final static int AF_JOBTYPE_ARCHIVE_PURGE = 9;
	public final static int AF_JOBTYPE_ARCHIVE_RESTORE = 10;
	public final static int AF_JOBTYPE_ARCHIVE_CATALOGSYNC = 14;
	public final static int AF_JOBTYPE_ARCHIVE_SOURCEDELETE = 70;
	public final static int AF_JOBTYPE_ARCHIVE_DELETE = 19;
	public final static int AF_JOBTYPE_VM_CATALOG_FS = 15;
	public final static int JOBTYPE_CATALOG_FS_ONDEMAND = 16;
	public final static int AF_JOBTYPE_VM_RESTORE_FILE_TO_ORIGINAL_OR_ALTERVM = 17;
	public final static int JOBTYPE_VM_CATALOG_FS_ONDEMAND = 20;
	public final static int JOBTYPE_RPS_MERGE   = 21;
	public final static int JOBTYPE_RPS_REPLICATE = 22;
	public final static int JOBTYPE_D2D_MERGE = 30;
	public final static int JOBTYPE_VM_MERGE = 31;
	
	//for VAPP backup/restore
	public final static int AF_JOBTYPE_VMWARE_VAPP_BACKUP = 25;
	public final static int AF_JOBTYPE_VMWARE_VAPP_RECOVERY = 26;
	
	public final static int AF_JOBTYPE_HYPERV_VM_RECOVERY = 50;
	
	public final static int COPY_PHASE_COPY_DATA = 82;
	
	//for hyper-v cluster backup and restore
	//we do not change the job type in job script, but for future flexibility, re-define it
	public final static int AF_JOBTYPE_HYPERV_CLUSTER_BACKUP = AF_JOBTYPE_HYPERV_VM_BACKUP;
	public final static int AF_JOBTYPE_HYPERV_CLUSTER_RECOVERY = AF_JOBTYPE_HYPERV_VM_RECOVERY;
	
	//in log.h
		/*
		 * #define AFINFO		0
		   #define AFWARNING	1
		   #define AFERROR		2
		*/
		//fix issue 18712980
		final static long AFRES_AFALOG 					=0;
		public final static long AFRES_AFALOG_INFO				=(AFRES_AFALOG + 0);
		public final static long AFRES_AFALOG_WARNING 			=(AFRES_AFALOG + 1);
		public final static long AFRES_AFALOG_ERROR 			=(AFRES_AFALOG + 2);

		public final static long AFRES_AFJWBS 					= 0x00002100;
		public final static long AFRES_AFJWBS_JOB_SKIPPED 		= AFRES_AFJWBS + 2;
		public final static long AFRES_AFJWBS_JOB_RETRY			= AFRES_AFJWBS + 3;
		public final static long AFRES_AFJWBS_JOB_FULL_ADAYS 	= AFRES_AFJWBS + 4;
		public final static long AFRES_AFJWBS_JOB_FULL_AHOURS 	= AFRES_AFJWBS + 5;
		public final static long AFRES_AFJWBS_JOB_FULL_AMINS 	= AFRES_AFJWBS + 6;
		public final static long AFRES_AFJWBS_JOB_INC_AHOURS 	= AFRES_AFJWBS + 7;
		public final static long AFRES_AFJWBS_JOB_INC_AMINS 	= AFRES_AFJWBS + 8;
		public final static long AFRES_AFJWBS_VSPHERE_VMTOOL_ERROR 	        = AFRES_AFJWBS + 10;
		public final static long AFRES_AFJWBS_VSPHERE_VMTOOL_NOT_INSTALL 	= AFRES_AFJWBS + 11;
		public final static long AFRES_AFJWBS_VSPHERE_VMTOOL_OUT_OF_DATE 	= AFRES_AFJWBS + 12;
		public final static long AFRES_AFJWBS_JOB_VSPHERE_HOST_NOT_FOUND 	= AFRES_AFJWBS + 13;
		public final static long AFRES_AFJWBS_VSPHERE_VIX_NOT_INSTALL 	= AFRES_AFJWBS + 14;
		public final static long AFRES_AFJWBS_VSPHERE_VIX_OUT_OF_DATE 	= AFRES_AFJWBS + 15;
		public final static long AFRES_AFJWBS_VSPHERE_LICENSE_FAILED_COPY_JOB 	= AFRES_AFJWBS + 19;
		public final static long AFRES_AFJWBS_RPS_POLICY_NOT_EXIST 	= AFRES_AFJWBS + 20;
		public final static long AFRES_AFJWBS_RPS_SERVER_NOT_REACHABLE 	= AFRES_AFJWBS + 21;
		public final static long AFRES_AFJWBS_RPS_SERVER_NO_DATA_PASSWORD 	= AFRES_AFJWBS + 22;
		public final static long AFRES_AFJWBS_RPS_SERVER_FAILED_TO_GET_DATA_PASSWORD 	= AFRES_AFJWBS + 23;
		public final static long AFRES_AFJWBS_RPS_SERVER_NOT_ENOUGH_SESSION_TO_MERGE = AFRES_AFJWBS + 24;
		public final static long AFRES_AFJWBS_RPS_SERVER_MERGE_SESSION_NOT_REPLICATE = AFRES_AFJWBS + 25;
		public final static long AFRES_AFJWBS_RPS_SERVER_REPLICATION_DESTINATION_NOT_CONNECTABLE = AFRES_AFJWBS + 26;
		public final static long AFRES_AFJWBS_VSPHERE_FAILED_COPY_JOB_NO_VOLUMN = AFRES_AFJWBS + 27;
		public final static long AFRES_AFJWBS_VSPHERE_HBBU_PROXY_INSTALL_VOLUME_FREE_SPACE_ALERT = AFRES_AFJWBS + 28;

		public static final String RETRYPOLICY_FOR_FAILED_DISABLED = "retryPolicyForFailedDisabled";
		public static final String RETRYPOLICY_FOR_FAILED_SKIPPED_NEXT = "retryPolicyForFailedSkippedNext";
		public static final String RETRYPOLICY_FOR_FAILED_EXCEED_MAXTIMES = "retryPolicyForFailedExceedMaxTimes";
		public static final String RETRYPOLICY_FOR_FAILED_SCHEDULED = "retryPolicyForFailedScheduled";
		public static final String RETRYPOLICY_FOR_FAILED_SUFFIXNAME = "_RETRY";
		public static final String RETRYPOLICY_FOR_FAILED_DISPLAY_NAME = "_Retry_";
		public static final String RETRYPOLICY_READ_ERROR = "retryPolicyReadError";
		public static final String RETRYPOLICY_SAVE_ERROR = "retryPolicySaveError";
		public static final String RETRYPOLICY_FOR_MISSED_SCHEDULED = "retryPolicyForMissedScheduled";		
		public static final String RETRYPOLICY_FOR_DST_SKIPPED = "retryPolicyForDSTSkipped";
		public static final String RETRYPOLICY_FOR_MISSED_SKIPPED_RUNNING = "retryPolicyForMissedSkippedRunning";
		public static final String RETRYPOLICY_FOR_MISSED_SKIPPED_NEXT = "retryPolicyForMissedSkippedNext";
		public static final String RETRYPOLICY_FOR_MISSED_DISABLED = "retryPolicyForMissedDisabled";
		public static final String RETRYPOLICY_READ_ERROR_NON = "retryPolicyReadErrorNon";
		public static final String RETRYPOLICY_FOR_JOBNAME = "retryPolicyJobName";		
		public static final String RETRYPOLICY_FOR_MISSED = "retryPolicyMissedJob";		
		public static final String RETRYPOLICY_FOR_SKIPPED = "retryPolicySkipJob";		
		public static final String RETRYPOLICY_FOR_DST = "retryPolicyDSTMissedJob";		
		public static final String RETRYPOLICY_FOR_FILECOPY_DELETE = "retryPolicyForFileCopyMakeUpDelete";
		public static final String RETRYPOLICY_FOR_MISSED_SCHEDULETYPE_SCHEDULED = "retryPolicyForMissedScheduleTypeScheduled";
		
		public static final String daily="daily";
		public static final String weekly="weekly";
		public static final String monthly="monthly";
		public static final String regular="regular";

		public final static int JOBSTATUS_ACTIVE = 0;
		public final static int JOBSTATUS_FINISHED = 1;
		public final static int JOBSTATUS_CANCELLED = 2;
		public final static int JOBSTATUS_FAILED = 3;
		public final static int JOBSTATUS_INCOMPLETE = 4;
		public final static int JOBSTATUS_IDLE = 5;
		public final static int JOBSTATUS_WAITING = 6;
		public final static int JOBSTATUS_CRASH = 7;
		public final static int JOBSTATUS_LICENSE_FAILED = 9;	
		public final static long BackupJob_PROC_EXIT = 10;
		public final static int JOBSTATUS_SKIPPED = 11;
		//fanda03 add stop status for Edge Alert, now only adapt to merge job
		public final static int JOBSTATUS_STOP =12;		
		public final static int JOBSTATUS_MISSED = 10000;
		
		//fix issue 167491, the license error are changed from backend
		public final static long AFRES_AFJWBS_GENERAL = 0x401A0005;
		public final static long AFRES_AFJWBS_JOB_VSPHERE_LICENSE_FAILED = 0xC01A000B;
		public final static long AFRES_AFJWBS_VSPHERE_LICENSE_FAILED_CANNOT_CONNECT = 0xC01A0013;
		
		public final static long JOB_SUB_STATUS_SJS_CHECK_RP_FAILED = 1;
}
