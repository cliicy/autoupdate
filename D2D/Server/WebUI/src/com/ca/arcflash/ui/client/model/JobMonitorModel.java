package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class JobMonitorModel extends BaseModelData {

	public final static int JOBTYPE_BACKUP = 0;
	public final static int JOBTYPE_RESTORE = 1;
	public final static int JOBTYPE_COPY = 2;
	public final static int JOBTYPE_VM = 3;
	public final static int JOBTYPE_RECOVERY_VM = 5;
	public final static int JOBTYPE_ARCHIVE = 8;
	public final static int JOBTYPE_ARCHIVEPURGE = 9;
	public final static int JOBTYPE_ARCHIVERESTORE = 10;
	public final static int JOBTYPE_FILEARCHIVE = 70;
	public final static int JOBTYPE_FILECOPYDELETE = 19;
	public final static int JOBTYPE_EXPORT = 6;
	public final static int JOBTYPE_CATALOG_FS = 11;
	public final static int JOBTYPE_CATALOG_APP = 12;
	public final static int JOBTYPE_CATALOG_GRT = 13;
	public final static int JOBTYPE_ARCHIVE_CATALOG_SYNC = 14;
	public final static int JOBTYPE_VM_CATALOG_FS = 15;
	public final static int JOBTYPE_CATALOG_FS_ONDEMAND = 16;
	public final static int JOBTYPE_VM_RESTORE_FILE_TO_ORIGINAL = 17;
	public final static int JOBTYPE_VM_CATALOG_FS_ONDEMAND = 20;

	public final static int JOBTYPE_RPS_REPLICATE = 22;

	public final static int JOBTYPE_VMWARE_VAPP_BACKUP = 25;
	public final static int JOBTYPE_VMWARE_VAPP_RECOVERY = 26;

	public final static int JOBTYPE_D2D_MERGE = 30;
	public final static int JOBTYPE_VM_MERGE = 31;
	public final static int JOBTYPE_RPS_REPLICATE_IN_BOUND = 24;
	public final static int JOBTYPE_RPS_MERGE = 32;
	public final static int JOBTYPE_RPS_CONVERSION = 41;
	public final static int JOBTYPE_BMR = 42;
	public final static int JOBTYPE_RPS_DATA_SEEDING = 43;
	public final static int JOBTYPE_RPS_DATA_SEEDING_IN = 44;

	public final static int JOBSTATUS_ACTIVE = 0;
	public final static int JOBSTATUS_FINISHED = 1;
	public final static int JOBSTATUS_CANCELLED = 2;
	public final static int JOBSTATUS_FAILED = 3;
	public final static int JOBSTATUS_INCOMPLETE = 4;
	public final static int JOBSTATUS_IDLE = 5;
	public final static int JOBSTATUS_WAITING = 6;
	public final static int JOBSTATUS_CRASH = 7;
	public final static int JOBSTATUS_NEEDREBOOT = 8;
	public final static int JOBSTATUS_FAILED_NO_LICENSE = 9; // <sonmi01>2010-1-21
																// mark no
																// license job
																// as FAILED and
																// update
																// JS_FAILED_NO_LICENSE
																// for Java
	public final static int JOBSTATUS_PROC_EXIT = 10; // ZZ: Process of job
														// exits.
	public final static int JOBSTATUS_SKIPPED = 11; // Job is skipped due to
													// merge operation in
													// progress

	public final static int JOBSTATUS_ARCHIVE_TO_TAPE = 71;

	public static long ENCRYPTION_UNKNOWN = 0;
	public static long ENCRYPTION_AES_128BIT = 1;
	public static long ENCRYPTION_AES_192BIT = 2;
	public static long ENCRYPTION_AES_256BIT = 3;
	public static long ENCRYPTION_3DES = 4;
	public static long ENCRYPTION_PASSWORD_PROTECTION = 9999;

	public static long TRANSFERMODE_UNKNOWN = 0;
	public static long TRANSFERMODE_SAN = 1;
	public static long TRANSFERMODE_NBD = 2;
	public static long TRANSFERMODE_NBDSSL = 3;
	public static long TRANSFERMODE_HOTADD = 4;
	public static long TRANSFERMODE_FILE = 5;

	public static int LINUX_PHASE_UNKNOW = 0;
	public static int LINUX_PHASE_BACKUP_VOLUME = 15;
	public static int LINUX_PHASE_CONNECT_TARGET = 65;
	public static int LINUX_PHASE_MOUNT_SESSION = 67;
	public static int LINUX_PHASE_CREATE_DISK = 68;
	public static int LINUX_PHASE_RESTORE_VOLUME = 69;
	public static int LINUX_PHASE_DATA_END = 71;
	public static int LINUX_PHASE_WRAPUP = 72;
	public static int LINUX_PHASE_DIAGNOSTIC = 73;
	public static int LINUX_PHASE_CONNECT_NODE = 97;
	public static int LINUX_PHASE_RESTORE_FILE = 99;
	public static int LINUX_PHASE_START = 209;
	public static int LINUX_PHASE_JOB_END = 210;
	public static int LINUX_PHASE_CONVERT_JOBSCRIPT_TO_JOBHISTORY = 211;
	public static int LINUX_PHASE_INSTALL_BUILD = 212;
	public static int LINUX_PHASE_UNINSTALL_BUILD = 213;
	public static int LINUX_PHASE_CANCEL_JOB = 214;

	public static int ARCHIVE_PHASE_CATALOG_UPDATE = 0x21;//

	public static int PHASE_BACKUP_PHASE_START_BACKUP = 0x01;
	public static int PHASE_BACKUP_PHASE_TAKING_SNAPSHOT = 0x02;
	public static int PHASE_BACKUP_PHASE_CREATING_VIRTUAL_DISKS = 0x03;
	public static int PHASE_BACKUP_PHASE_REPLICATIING_VOLUMES = 0x04;
	public static int PHASE_BACKUP_PHASE_DELETING_SNAPSHOT = 0x05;
	public static int PHASE_CANCELING = 0x06;
	public static int PHASE_RESTORE_PHASE_START_RESTORE = 0x07;
	public static int PHASE_BACKUP_PHASE_CREATE_METADATA = 0x08;
	public static int PHASE_BACKUP_PHASE_COLLECT_DR_INFO = 0x09;
	public static int PHASE_BACKUP_PHASE_PURGE_SESSION = 0x0A;
	public static int PHASE_BACKUP_PHASE_JOB_END = 0x20;
	public static int PHASE_BACKUP_PHASE_PROCESS_EXIT = 0xE;
	public static int PHASE_BACKUP_PHASE_PRE_JOB = 0x0B;
	public static int PHASE_BACKUP_PHASE_POST_SNAPSHOT = 0x0C;
	public static int PHASE_BACKUP_PHASE_POST_JOB = 0x0D;
	public static int RESTORE_PHASE_DUMP_METADATA = 0x40;
	public static int RESTORE_PHASE_RESTORE_DATA = 0x41;
	public static int RESTORE_PHASE_VM_DIRECT_RESTORE_DETECT_VM_STATUS = 0x45;
	public static int RESTORE_PHASE_VM_DIRECT_RESTORE_CONNECT_VM_HOST = 0x46;
	public static int RESTORE_PHASE_VM_DIRECT_RESTORE_CONNECT_VM = 0x47;
	public static int RESTORE_PHASE_VM_DIRECT_RESTORE_CREATE_MINID2D = 0x48;

	// check recovery point job phase (hbbu)
	public final static int BACKUP_PHASE_CHECK_RECOVERY_POINT = 0x14;
	
	// hardware snapshot (vm)
	public static int BACKUP_PHASE_CREATE_HW_SNAPSHOT = 0x15;
	public static int BACKUP_PHASE_DELETE_HW_SNAPSHOT = 0x16;

	// reserve 0x60 -> 0x6f for vApp recovery
	public static int RESTORE_PHASE_CREATE_VAPP = 0x60;
	public static int RESTORE_PHASE_VAPP_RESTORE_VM = 0x61;
	public static int RESTORE_PHASE_VAPP_IMPORT_VM = 0x62;
	public static int RESTORE_PHASE_VAPP_CLEANUP = 0x6f;

	public static int BMR_PHASE_START_BMR = 0x01;
	public static int BMR_PHASE_RESTORE_DATA = 0x02;
	public static int BMR_PHASE_END_BMR = 0x03;

	public static int RESTORE_PHASE_GATHER_WRITERS_INFO = 0xA1;
	public static int RESTORE_PHASE_INIT_VSS = 0xA2;
	public static int RESTORE_PHASE_SELECT_COMPONENTS_TO_RESTORE = 0xA3;
	public static int RESTORE_PHASE_DISMOUNT_EXCHANGE_DATABASE = 0xA4;
	public static int RESTORE_PHASE_GATHER_DB_INFO_FROM_AD = 0xA5;
	public static int RESTORE_STOP_SQL_SERVICE_RESTORE_MASTER = 0xA6;
	public static int RESTORE_PHASE_START_SQL_SERVICE = 0xA7;
	public static int RESTORE_PHASE_PRERESTORE = 0xA8;
	public static int RESTORE_PHASE_RESTORE_FILE = 0xA9;
	public static int RESTORE_PHASE_POSTRESTORE = 0xAA;
	public static int RESTORE_PHASE_MOUNT_EXCHANGE_DATABASE = 0xAB;
	public static int RESTORE_PHASE_RESTORE_EXCHGRT_DATA = 0x42;
	public static int RESTORE_PHASE_RESTORE_MOUNT_VOLUME = 0x43;
	public static int RESTORE_PHASE_RESTORE_UNMOUNT_VOLUME = 0x44;
	public static int RESTORE_PHASE_PROCESS_EXIT = 0xE;
	public static int COPY_PHASE_START_COPY = 0x51;
	public static int COPY_PHASE_COPY_DATA = 0x52;
	public static int COPY_PHASE_ESTIMATE_DATA = 0x53;
	public static int COPY_PHASE_LOCK_SESSION = 0x54;
	public static int COPY_PHASE_LOCK_SESSION_SUCCESSFUL = 0x55;
	public static int COPY_PHASE_LOCK_SESSION_FAILED = 0x56;
	public static int COPY_PHASE_JOB_END = PHASE_BACKUP_PHASE_JOB_END;
	public static int BACKUP_PHASE_CONTINUR_FAILED_MERGE = 0x0F;
	// Catalog phase
	// ZZ: Validate if catalog script is valid.
	public final static int CATPROC_PHASE_VALIDATE_CATALOG_SCRIPT = 0xB0;
	// ZZ: Parse catalog script
	public final static int CATPROC_PHASE_PARSE_CATALOG_SCRIPT = 0xB1;
	// ZZ: Lock session for intergration with client agent.
	public final static int CATPROC_PHASE_LOCK_SESS_INTERGRATION = 0xB2;
	// ZZ: Prepare for catalog generation.
	public final static int CATPROC_PHASE_PREPARE_FOR_CATALOG = 0xB3;
	// ZZ: Delete last failed session.
	public final static int CATPROC_PHASE_DELETE_FAILED_SESSION = 0xB4;
	// ZZ: Continue merging failed session when last merge.
	public final static int CATPROC_PHASE_CONTINUR_FAILED_MERGE = 0xB5;
	// ZZ: Merge session based on recovery point count configured.
	public final static int CATPROC_PHASE_MERGE_SESS_BY_SETTING = 0xB6;
	// ZZ: Lock session for catalog in read mode.
	public final static int CATPROC_PHASE_LOCK_SESS_FOR_CATALOG_READ = 0xB7;
	// ZZ: Begin to generate catalog file.
	public final static int CATPROC_PHASE_BEGIN_TO_GENERATE_CATALOG = 0xB8;
	// ZZ: Generate catalog file for specified volume.
	public final static int CATPROC_PHASE_GENERATE_CATALOG_FOR_VOLUME = 0xB9;
	// ZZ: Creating index file for catalog file.
	public final static int CATPROC_PHASE_GENERATE_CAT_INDEX_FOR_VOLUME = 0xBA;
	// ZZ: Update catalog information for session.
	public final static int CATPROC_PHASE_UPDATE_SESSION_INFORMATIIN = 0xBB;
	// ZZ: Lock session for data update in write mode.
	public final static int CATPROC_PHASE_LOCK_SESS_FOR_CATALOG_WRITE = 0xBC;
	// ZZ: Update cluster map for session header.
	public final static int CATPROC_PHASE_UPDATE_SESS_BLOCK_2 = 0xBD;
	// ZZ: Start next schedule catalog job. This maybe not occur in normal mode.
	public final static int CATPROC_PHASE_START_NEXT_CATLOG_JOB = 0xBE;
	// ZZ: Catalog generation finishes.
	public final static int CATPROC_PHASE_CATALOG_GENERATE_FINISH = 0xBF;

	public final static int EXGRT_PHASE_CATALOB_BASE = 0x1000;

	public final static int EXGRT_PHASE_MOUNTING_DRIVER = EXGRT_PHASE_CATALOB_BASE + 1; // mounte
																						// driver
																						// phase

	public final static int EXGRT_PHASE_DISMOUNTING_DRIVER = EXGRT_PHASE_CATALOB_BASE + 2; // dismounte
																							// driver
																							// phase

	public final static int EXGRT_PHASE_ESTIMATE = EXGRT_PHASE_CATALOB_BASE + 3; // estimate
																					// mailbox
																					// account

	public final static int EXGRT_PHASE_START_CATALOG = EXGRT_PHASE_CATALOB_BASE + 4; // generating
																						// catalog
																						// file
																						// phase

	public final static int EXGRT_PHASE_GENERAT_CATALOG = EXGRT_PHASE_CATALOB_BASE + 5;

	public final static int EXGRT_PHASE_GENERATE_CATALOG_END = EXGRT_PHASE_CATALOB_BASE + 6;

	public final static int EXGRT_PHASE_GENERATE_INDEX_FILE = EXGRT_PHASE_CATALOB_BASE + 7;// end

	public final static int EXGRT_PHASE_DEGRAGMENT = EXGRT_PHASE_CATALOB_BASE + 8;// Checking
																					// database
																					// defragment
																					// status

	public final static int MERGESESSION_PHASE_BASE = 0x2000;
	public final static int PHASE_BACKUP_PHASE_LOCKSESSION = MERGESESSION_PHASE_BASE + 1;
	public final static int PHASE_BACKUP_PHASE_MERGINGSESSION = MERGESESSION_PHASE_BASE + 2;

	public final static int PHASE_BACKUP_PHASE_WAITING = 0x2000 + 10;
	// InterMerge job phase
	public final static int IMJ_PHASE_BASE = 0x3000;
	public final static int IMJ_PHASE_START = (IMJ_PHASE_BASE + 0);
	public final static int IMJ_PHASE_CONTINUE_FAILED_MERGE = CATPROC_PHASE_CONTINUR_FAILED_MERGE;
	public final static int IMJ_PHASE_PREPARE_SESSION = (IMJ_PHASE_BASE + 1);
	public final static int IMJ_PHASE_MERGE_DISK = (IMJ_PHASE_BASE + 2);
	public final static int IMJ_PHASE_MOVE_DISK = (IMJ_PHASE_BASE + 3);
	public final static int IMJ_PHASE_DELETE_SESSION = (IMJ_PHASE_BASE + 4);
	public final static int IMJ_PHASE_END = PHASE_BACKUP_PHASE_JOB_END;

	// RPSReplication job phase
	public final static int RPSREP_PHASE_BASE = 0x3800;
	public final static int RPSREP_PHASE_START = (RPSREP_PHASE_BASE + 0);
	public final static int RPSREP_PHASE_PREPARE = (RPSREP_PHASE_BASE + 1);
	public final static int RPSREP_PHASE_REPLICATE = (RPSREP_PHASE_BASE + 2);
	public final static int RPSREP_PHASE_CANCELING = PHASE_CANCELING; // 0x06
	public final static int RPSREP_PHASE_END = PHASE_BACKUP_PHASE_JOB_END; // 0x20

	public final static int BACKUP_PHASE_CONNECT_TO_STUB = 0x10;
	public final static int BACKUP_PHASE_UPGRADE_CBT = 0x11;
	public final static int BACKUP_PHASE_INITIALIZE_STUB = 0x12;
	public final static int BACKUP_PHASE_COLLECT_DATA = 0x13;
	
	public String destinationType;
	public String destinationPath;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6721967949874006611L;

	public Long getSessionID() {
		return (Long) get("SessionID");
	}

	public void setSessionID(Long sessionID) {
		set("SessionID", sessionID);
	}

	public long getUlBeginSessID() {
		return (Long) get("ulBeginSessID");
	}

	public void setUlBeginSessID(long ulBeginSessID) {
		set("ulBeginSessID", ulBeginSessID);
	}

	public long getUlEndSessID() {
		return (Long) get("ulEndSessID");
	}

	public void setUlEndSessID(long ulEndSessID) {
		set("ulEndSessID", ulEndSessID);
	}

	public Long getFlags() {
		return (Long) get("Flags");
	}

	public void setFlags(Long flags) {
		set("Flags", flags);
	}

	public Long getJobPhase() {
		return (Long) get("JobPhase");
	}

	public void setJobPhase(Long jobPhase) {
		set("JobPhase", jobPhase);
	}

	public Long getJobStatus() {
		return (Long) get("JobStatus");
	}

	public void setJobStatus(Long jobStatus) {
		set("JobStatus", jobStatus);
	}

	public Long getJobType() {
		return (Long) get("JobType");
	}

	public void setJobType(Long jobType) {
		set("JobType", jobType);
	}

	public Long getJobMethod() {
		return (Long) get("JobMethod");
	}

	public void setJobMethod(Long jobMethod) {
		set("JobMethod", jobMethod);
	}

	public Long getVolMethod() {
		return (Long) get("VolMethod");
	}

	public void setVolMethod(Long volMethod) {
		set("VolMethod", volMethod);
	}

	public Long getEstimateBytesJob() {
		return (Long) get("EstimateBytesJob");
	}

	public void setEstimateBytesJob(Long estimateBytesJob) {
		set("EstimateBytesJob", estimateBytesJob);
	}

	public Long getTransferBytesJob() {
		return (Long) get("TransferBytesJob");
	}

	public void setTransferBytesJob(Long transferBytesJob) {
		set("TransferBytesJob", transferBytesJob);
	}

	public Long getEstimateBytesDisk() {
		return (Long) get("EstimateBytesDisk");
	}

	public void setEstimateBytesDisk(Long estimateBytesDisk) {
		set("EstimateBytesDisk", estimateBytesDisk);
	}

	public Long getTransferBytesDisk() {
		return (Long) get("TransferBytesDisk");
	}

	public void setTransferBytesDisk(Long transferBytesDisk) {
		set("TransferBytesDisk", transferBytesDisk);
	}

	public String getCurrentProcessDiskName() {
		return (String) get("CurrentProcessDiskName");
	}

	public void setCurrentProcessDiskName(String currentProcessDiskName) {
		set("CurrentProcessDiskName", currentProcessDiskName);
	}

	public Long getBackupStartTime() {
		return (Long) get("BackupStartTime");
	}

	public void setBackupStartTime(Long backupStartTime) {
		set("BackupStartTime", backupStartTime);
	}

	public Long getElapsedTime() {
		return (Long) get("ElapsedTime");
	}

	public void setElapsedTime(Long elapsedTime) {
		set("ElapsedTime", elapsedTime);
	}

	public Long getID() {
		return (Long) get("ID");
	}

	public void setID(Long id) {
		set("ID", id);
	}

	public Long getProgramCPUPercentage() {
		return (Long) get("programCPUPercentage");
	}

	public void setProgramCPUPercentage(Long programCPUPercentage) {
		set("programCPUPercentage", programCPUPercentage);
	}

	public Long getSystemCPUPercentage() {
		return (Long) get("systemCPUPercentage");
	}

	public void setSystemCPUPercentage(Long systemCPUPercentage) {
		set("systemCPUPercentage", systemCPUPercentage);
	}

	public Long getReadSpeed() {
		return (Long) get("readSpeed");
	}

	public void setReadSpeed(Long readSpeed) {
		set("readSpeed", readSpeed);
	}

	public Long getWriteSpeed() {
		return (Long) get("writeSpeed");
	}

	public void setWriteSpeed(Long writeSpeed) {
		set("writeSpeed", writeSpeed);
	}

	public Long getSystemReadSpeed() {
		return (Long) get("systemReadSpeed");
	}

	public void setSystemReadSpeed(Long systemReadSpeed) {
		set("systemReadSpeed", systemReadSpeed);
	}

	public Long getSystemWriteSpeed() {
		return (Long) get("systemWriteSpeed");
	}

	public void setSystemWriteSpeed(Long systemWriteSpeed) {
		set("systemWriteSpeed", systemWriteSpeed);
	}

	public void setThrottling(Long throttling) {
		set("throttling", throttling);
	}

	public Long getThrottling() {
		return (Long) get("throttling");
	}

	public void setEncInfoStatus(Long encInfoStatus) {
		set("encInfoStatus", encInfoStatus);
	}

	public Long getEncInfoStatus() {
		return (Long) get("encInfoStatus");
	}

	public void setTotalSizeRead(Long totalSizeRead) {
		set("totalSizeRead", totalSizeRead);
	}

	public Long getTotalSizeRead() {
		return (Long) get("totalSizeRead");
	}

	public void setTotalSizeWritten(Long totalSizeWritten) {
		set("totalSizeWritten", totalSizeWritten);
	}

	public Long getTotalSizeWritten() {
		return (Long) get("totalSizeWritten");
	}

	public void setCurVolMntPoint(String curVolMntPoint) {
		set("curVolMntPoint", curVolMntPoint);
	}

	public String getCurVolMntPoint() {
		return (String) get("curVolMntPoint");
	}

	public void setCompressLevel(Long compressLevel) {
		set("compressLevel", compressLevel);
	}

	public Long getCompressLevel() {
		return (Long) get("compressLevel");
	}

	public void setTransferMode(Long transferModel) {
		set("transferMode", transferModel);
	}

	public Long getTransferMode() {
		return get("transferMode");
	}

	public void setCTCurCatVol(String curVol) {
		set("ctCurCatVol", curVol);
	}

	public String getCTCurCatVol() {
		return get("ctCurCatVol");
	}

	public void setCTBKJobName(String jobName) {
		set("ctBKJobName", jobName);
	}

	public String getCTBKJobName() {
		return get("ctBKJobName");
	}

	public void setCTBKStartTime(String startTime) {
		set("ctBKStartTime", startTime);
	}

	public String getCTBKStartTime() {
		return get("ctBKStartTime");
	}

	public void setCTDWBKJobID(Long dwJobID) {
		set("ctDWBKJobID", dwJobID);
	}

	public Long getCTDWBKJobID() {
		return get("ctDWBKJobID");
	}

	public Long getGRTProcessedFolder() {
		return get("GRTProcessedFolder");
	}

	public void setGRTProcessFolder(Long processedFolder) {
		set("GRTProcessedFolder", processedFolder);
	}

	public Long getGRTTotalFolder() {
		return get("GRTTotalFolder");
	}

	public void setGRTTotalFolder(Long totalFolder) {
		set("GRTTotalFolder", totalFolder);
	}

	public String getGRTMailFolder() {
		return get("GRTMailFolder");
	}

	public void setGRTMailFolder(String mailFolder) {
		set("GRTMailFolder", mailFolder);
	}

	public String getGRTEDB() {
		return get("GRTEDB");
	}

	public void setGRTEDB(String edb) {
		set("GRTEDB", edb);
	}

	public Long getUlTotalMergedSessions() {
		return get("TotalMergedSession");
	}

	public void setUlTotalMegedSessions(Long ulTotalMergedSessions) {
		set("TotalMergedSession", ulTotalMergedSessions);
	}

	public Long getUlMergedSession() {
		return get("MergedSession");
	}

	public void setUlMergedSession(Long ulMergedSession) {
		set("MergedSession", ulMergedSession);
	}

	public String getd2dServerName() {
		return get("d2dServerName");
	}

	public void setd2dServerName(String d2dServerName) {
		set("d2dServerName", d2dServerName);
	}

	public String getPolicyName() {
		return get("policyName");
	}

	public void setPolicyName(String policyName) {
		set("policyName", policyName);
	}

	public String getJobMonitorId() {
		return get("jobMonitorId");
	}

	public void setJobMonitorId(String jobMonitorId) {
		set("jobMonitorId", jobMonitorId);
	}

	public String getD2dUuid() {
		return get("d2dUuid");
	}

	public void setD2dUuid(String d2dUuid) {
		set("d2dUuid", d2dUuid);
	}

	public String getVmInstanceUUID() {
		return get("vmInstanceUUID");
	}

	public void setVmInstanceUUID(String vmInstanceUUID) {
		set("vmInstanceUUID", vmInstanceUUID);
	}

	public Long getTotalUniqueData() {
		return (Long) get("TotalUniqueData");
	}

	public void setTotalUniqueData(Long uniqueData) {
		set("TotalUniqueData", uniqueData);
	}

	public Boolean isDedupe() {
		return (Boolean) get("DedupeEnabled");
	}

	public void setDedupe(Boolean dedupe) {
		set("DedupeEnabled", dedupe);
	}

	public String getSrcRPS() {
		return get("srcRPS");
	}

	public void setSrcRPS(String srcRPS) {
		set("srcRPS", srcRPS);
	}

	public String getDestRPS() {
		return get("destRPS");
	}

	public void setDestRPS(String destRPS) {
		set("destRPS", destRPS);
	}

	public String getSrcDataStore() {
		return get("srcDataStore");
	}

	public void setSrcDataStore(String srcDataStore) {
		set("srcDataStore", srcDataStore);
	}

	public String getDestDataStore() {
		return get("destDataStore");
	}

	public void setDestDataStore(String destDataStore) {
		set("destDataStore", destDataStore);
	}

	public String getSrcCommonPath() {
		return get("srcCommonPath");
	}

	public void setSrcCommonPath(String srcCommonPath) {
		set("srcCommonPath", srcCommonPath);
	}

	public String getDestCommonPath() {
		return get("destCommonPath");
	}

	public void setDestCommonPath(String destCommonPath) {
		set("destCommonPath", destCommonPath);
	}

	public Integer getHistoryProductType() {
		return (Integer) get("historyProductType");
	}

	public void setHistoryProductType(Integer historyProductType) {
		set("historyProductType", historyProductType);
	}

	public Integer getSourceRPSId() {
		return (Integer) get("sourceRPSId");
	}

	public void setSourceRPSId(Integer sourceRPSId) {
		set("sourceRPSId", sourceRPSId);
	}

	public Integer getTargetRPSId() {
		return (Integer) get("targetRPSId");
	}

	public void setTargetRPSId(Integer targetRPSId) {
		set("targetRPSId", targetRPSId);
	}

	public Integer getRunningServerId() {
		return (Integer) get("runningServerId");
	}

	public void setRunningServerId(Integer runningServerId) {
		set("runningServerId", runningServerId);
	}

	public Integer getNodeId() {
		return (Integer) get("nodeId");
	}

	public void setNodeId(Integer nodeId) {
		set("nodeId", nodeId);
	}

	public String getAgentNodeName() {
		return (String) get("agentNodeName");
	}

	public void setAgentNodeName(String agentNodeName) {
		set("agentNodeName", agentNodeName);
	}

	public String getServerNodeName() {
		return (String) get("serverNodeName");
	}

	public void setServerNodeName(String serverNodeName) {
		set("serverNodeName", serverNodeName);
	}

	public Float getProgress() {
		return (Float) get("progress");
	}

	public void setProgress(Float progress) {
		set("progress", progress);
	}

	public Long getStartTime() {
		return (Long) get("startTime");
	}

	public void setStartTime(Long startTime) {
		set("startTime", startTime);
	}

	public long getRemainTime() {
		return (Long) get("remainTime");
	}

	public void setRemainTime(long remainTime) {
		set("remainTime", remainTime);
	}

	public Boolean isRunningOnRPS() {
		return (Boolean) get("isRunningOnRPS");
	}

	public void setRunningOnRPS(Boolean isRunningOnRPS) {
		set("isRunningOnRPS", isRunningOnRPS);
	}

	public void setReplicationSavedBandWidth(long savedBandWidth) {
		set("ulReplicationSavedBandWidth", savedBandWidth);
	}

	public long getReplicationSavedBandWidth() {
		return (Long) get("ulReplicationSavedBandWidth");
	}

	public void setTotalVMJobCount(long ulTotalVMJobCount) {
		set("ulTotalVMJobCount", ulTotalVMJobCount);
	}

	public long getTotalVMJobCount() {
		return (Long) get("ulTotalVMJobCount");
	}

	public void setFinishedVMJobCount(long ulFinishedVMJobCount) {
		set("ulFinishedVMJobCount", ulFinishedVMJobCount);
	}

	public long getFinishedVMJobCount() {
		return (Long) get("ulFinishedVMJobCount");
	}

	public void setCanceledVMJobCount(long ulCanceledVMJobCount) {
		set("ulCanceledVMJobCount", ulCanceledVMJobCount);
	}

	public long getCanceledVMJobCount() {
		return (Long) get("ulCanceledVMJobCount");
	}

	public void setFailedVMJobCount(long ulFailedVMJobCount) {
		set("ulFailedVMJobCount", ulFailedVMJobCount);
	}

	public long getFailedVMJobCount() {
		return (Long) get("ulFailedVMJobCount");
	}

	public long getLogicSpeed() {
		return (Long) get("nLogicSpeed");
	}

	public void setLogicSpeed(long logicSpeed) {
		set("nLogicSpeed", logicSpeed);
	}

	public String getVmHostName() {
		return (String) get("vmHostName");
	}

	public void setVmHostName(String vmHostName) {
		set("vmHostName", vmHostName);
	}

	public Boolean isLinuxNode() {
		return (Boolean) get("isLinuxNode");
	}

	public void setLinuxNode(Boolean isLinuxNode) {
		set("isLinuxNode", isLinuxNode);
	}
	
	public String getRpsServerName() {
		return (String) get("rpsServerName");
	}

	public void setRpsServerName(String rpsServerName) {
		set("rpsServerName", rpsServerName);
	}
	
	public String getRpsDataStoreName() {
		return (String) get("rpsDataStoreName");
	}

	public void setRpsDataStoreName(String rpsDataStoreName) {
		set("rpsDataStoreName", rpsDataStoreName);
	}
	
	public String getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}
}
