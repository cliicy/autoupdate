package com.ca.arcflash.webservice.jni.model;

public class JJobMonitor {

	private long ulSessionID; // Session Number
	private long ulFlags; // Indicate the action made to the share memory
	private long ulJobPhase; // BLI_PHASE::PHASE_TAKING_SNAPSHOT.. in
	// "native\h\datatransmitter.h"
	private long ulJobStatus; // correspond to value for
	// AF_JOB_STATUS_ACTIVE_STRING...
	// in afdefine.h
	private long ulJobType; // afjob.h, AF_JOBTYPE_BACKUP, AF_JOBTYPE_RESTORE
	private long ulJobMethod; // afjob.h, AF_JOBMETHOD_FULL, AF_JOBMETHOD_INCR,
	// AF_JOBMETHOD_RESYNC
	// job level, is updated at begin of the job
	private long ulVolMethod; // afjob.h, AF_JOBMETHOD_FULL, AF_JOBMETHOD_INCR,
	// AF_JOBMETHOD_RESYNC
	// volume level, is updated at beginning of each volume
	// job/volume progress; job remaining time; job throughput so far can be
	// calculated through following fields
	private long ulEstBytesJob; // JobLevel Total Bytes estimation
	private long ulXferBytesJob; // JobLevel Bytes processed
	private long ulEstBytesDisk; // Volume Level Total Bytes estimation
	private long ulXferBytesDisk; // Volume Level processed Bytes

	private String wszDiskName; // current process disk name

	private long ulBackupStartTime; // start backup time, update at JIF_INIT
	// phase. In case UI need it
	private long ulElapsedTime; // Time spend on backup/restore, filled by

	// backend. Not include situation that does not counted as backup time.
	// Throughput = ulXferKBJob/ulElaspsedTime
	
	private long	nProgramCPU;  // CPU usage of Afbackend.exe    Numbers, should be 0 ~ 100
	private long   nSystemCPU;        // Total system CPU usage         Numbers, should be 0 ~ 100, and should be no less than CPU usage of afbackend.exe

	private long	nReadSpeed;   // Read I/O speed of afbackend.exe	MB/min     
	private long    nWriteSpeed;  // Write I/O speed of afbackend.exe	MB/min
	private long    nSystemReadSpeed;// Total system read speed	MB/min
	private long    nSystemWriteSpeed;//Total system write speed MB/min
	private long    ulThrottling;       //ZZ: Backup throughout throttling, MB/min.
    private long    ulEncInfoStatus;    //ZZ: Encryption algorithm ID, AES-128, 192 and 256
    private long    ulTotalSizeRead;    //ZZ: Total size has been read from snapshot. Means data size before compression,
    private long    ulTotalSizeWritten; //ZZ: Total size has been written to VHD. Means data size after compression. [Compression rate]=ulTotalSizeWritten/ulTotalSizeRead;
    private String  wzCurVolMntPoint;  //ZZ: Current volume being backed up. Maybe mount point, driver letter or GUID.
    private long ulCompressLevel;	// compress level which is defined
    private long transferMode;    
    private long    ulUniqueData;//the size after deduped
    //File System catalog
	private long ctDWBKJobID;// ZZ: for catalog Job ID used for backup job when
								// this session is backed up.
	private long dwBKSessNum;                 //ZZ: Session number for which catalog is created.
    private String wzBKBackupDest;    //ZZ: Backup destination for the session for which catalog is created.
    private String wzBKDestUsrName;   //ZZ: User name of backup destination when it is remote folder.
    private String wzBKDestPassword;  //ZZ: Password of backup destination when it is remote folder.
	private String ctBKStartTime;// ZZ: for catalog Session time.
	private String ctBKJobName;// ZZ: for catalog Backup job name.
	private String ctCurCatVol;// ZZ: for catalog Information for volume or
								// Exchange database is generated catalog.
	//For GRT catalog	
    private String wszEDB;// current EDB file
    private String wszMailFolder;// current Mail folder
    private long ulTotalFolder;
    private long ulProcessedFolder;
    
    private long ulTotalMergedSessions; // the number of total to be merged sessions
	private long ulMergedSession;     // the sequence of being merged sessions
	
	private long productType;
	private String vmInstanceUUID;
	
	//For vApp
	private long ulTotalVMJobCount;
	private long ulFinishedVMJobCount;
	private long ulCanceledVMJobCount;
	private long ulFailedVMJobCount;
	
	private String vmHostName; // host name of guest OS within VM  ZhangHeng
//	private String planUUID;
	
//	jobSubStatus could be following values in JobMonitor.h
//	enum class enum_ulSubJobStatus : ULONG
//	{
//		SJS_SUCCESS = 0,
//		SJS_CHECK_RP_FAILED = 1,
//	};
	private long jobSubStatus;
	
    public long getUlTotalMergedSessions() {
		return ulTotalMergedSessions;
	}

	public void setUlTotalMergedSessions(long ulTotalMergedSessions) {
		this.ulTotalMergedSessions = ulTotalMergedSessions;
	}

	public long getUlMergedSession() {
		return ulMergedSession;
	}

	public void setUlMergedSession(long ulMergedSession) {
		this.ulMergedSession = ulMergedSession;
	}

	public long getDwBKSessNum() {
		return dwBKSessNum;
	}

	public void setDwBKSessNum(long dwBKSessNum) {
		this.dwBKSessNum = dwBKSessNum;
	}

	public String getWzBKBackupDest() {
		return wzBKBackupDest;
	}

	public void setWzBKBackupDest(String wzBKBackupDest) {
		this.wzBKBackupDest = wzBKBackupDest;
	}

	public String getWzBKDestUsrName() {
		return wzBKDestUsrName;
	}

	public void setWzBKDestUsrName(String wzBKDestUsrName) {
		this.wzBKDestUsrName = wzBKDestUsrName;
	}

	public String getWzBKDestPassword() {
		return wzBKDestPassword;
	}

	public void setWzBKDestPassword(String wzBKDestPassword) {
		this.wzBKDestPassword = wzBKDestPassword;
	}

	public long getUlSessionID() {
		return ulSessionID;
	}

	public long getCtDWBKJobID() {
		return ctDWBKJobID;
	}

	public void setCtDWBKJobID(long ctDWBKJobID) {
		this.ctDWBKJobID = ctDWBKJobID;
	}

	public String getCtBKStartTime() {
		return ctBKStartTime;
	}

	public void setCtBKStartTime(String ctBKStartTime) {
		this.ctBKStartTime = ctBKStartTime;
	}

	public String getCtBKJobName() {
		return ctBKJobName;
	}

	public void setCtBKJobName(String ctBKJobName) {
		this.ctBKJobName = ctBKJobName;
	}

	public String getCtCurCatVol() {
		return ctCurCatVol;
	}

	public void setCtCurCatVol(String ctCurCatVol) {
		this.ctCurCatVol = ctCurCatVol;
	}

	public void setUlSessionID(long ulSessionID) {
		this.ulSessionID = ulSessionID;
	}

	public long getUlFlags() {
		return ulFlags;
	}

	public void setUlFlags(long ulFlags) {
		this.ulFlags = ulFlags;
	}

	public long getUlJobPhase() {
		return ulJobPhase;
	}

	public void setUlJobPhase(long ulJobPhase) {
		this.ulJobPhase = ulJobPhase;
	}

	public long getUlJobStatus() {
		return ulJobStatus;
	}

	public void setUlJobStatus(long ulJobStatus) {
		this.ulJobStatus = ulJobStatus;
	}

	public long getUlJobType() {
		return ulJobType;
	}

	public void setUlJobType(long ulJobType) {
		this.ulJobType = ulJobType;
	}

	public long getUlJobMethod() {
		return ulJobMethod;
	}

	public void setUlJobMethod(long ulJobMethod) {
		this.ulJobMethod = ulJobMethod;
	}

	public long getUlVolMethod() {
		return ulVolMethod;
	}

	public void setUlVolMethod(long ulVolMethod) {
		this.ulVolMethod = ulVolMethod;
	}

	public long getUlEstBytesJob() {
		return ulEstBytesJob;
	}

	public void setUlEstBytesJob(long ulEstBytesJob) {
		this.ulEstBytesJob = ulEstBytesJob;
	}

	public long getUlXferBytesJob() {
		return ulXferBytesJob;
	}

	public void setUlXferBytesJob(long ulXferBytesJob) {
		this.ulXferBytesJob = ulXferBytesJob;
	}

	public long getUlEstBytesDisk() {
		return ulEstBytesDisk;
	}

	public void setUlEstBytesDisk(long ulEstBytesDisk) {
		this.ulEstBytesDisk = ulEstBytesDisk;
	}

	public long getUlXferBytesDisk() {
		return ulXferBytesDisk;
	}

	public void setUlXferBytesDisk(long ulXferBytesDisk) {
		this.ulXferBytesDisk = ulXferBytesDisk;
	}

	public String getWszDiskName() {
		return wszDiskName;
	}

	public void setWszDiskName(String wszDiskName) {
		this.wszDiskName = wszDiskName;
	}

	public long getUlBackupStartTime() {
		return ulBackupStartTime;
	}

	public void setUlBackupStartTime(long ulBackupStartTime) {
		this.ulBackupStartTime = ulBackupStartTime;
	}

	public long getUlElapsedTime() {
		return ulElapsedTime;
	}

	public void setUlElapsedTime(long ulElapsedTime) {
		this.ulElapsedTime = ulElapsedTime;
	}

	public void setnProgramCPU(long nProgramCPU) {
		this.nProgramCPU = nProgramCPU;
	}

	public long getnProgramCPU() {
		return nProgramCPU;
	}

	public void setnSystemCPU(long nSystemCPU) {
		this.nSystemCPU = nSystemCPU;
	}

	public long getnSystemCPU() {
		return nSystemCPU;
	}

	public void setnReadSpeed(long nReadSpeed) {
		this.nReadSpeed = nReadSpeed;
	}

	public long getnReadSpeed() {
		return nReadSpeed;
	}

	public void setnWriteSpeed(long nWriteSpeed) {
		this.nWriteSpeed = nWriteSpeed;
	}

	public long getnWriteSpeed() {
		return nWriteSpeed;
	}

	public void setnSystemReadSpeed(long nSystemReadSpeed) {
		this.nSystemReadSpeed = nSystemReadSpeed;
	}

	public long getnSystemReadSpeed() {
		return nSystemReadSpeed;
	}

	public void setnSystemWriteSpeed(long nSystemWriteSpeed) {
		this.nSystemWriteSpeed = nSystemWriteSpeed;
	}

	public long getnSystemWriteSpeed() {
		return nSystemWriteSpeed;
	}

	public void setUlThrottling(long ulThrottling) {
		this.ulThrottling = ulThrottling;
	}

	public long getUlThrottling() {
		return ulThrottling;
	}

	public void setUlEncInfoStatus(long ulEncInfoStatus) {
		this.ulEncInfoStatus = ulEncInfoStatus;
	}

	public long getUlEncInfoStatus() {
		return ulEncInfoStatus;
	}

	public void setUlTotalSizeRead(long ulTotalSizeRead) {
		this.ulTotalSizeRead = ulTotalSizeRead;
	}

	public long getUlTotalSizeRead() {
		return ulTotalSizeRead;
	}

	public void setUlTotalSizeWritten(long ulTotalSizeWritten) {
		this.ulTotalSizeWritten = ulTotalSizeWritten;
	}

	public long getUlTotalSizeWritten() {
		return ulTotalSizeWritten;
	}

	public void setWzCurVolMntPoint(String wzCurVolMntPoint) {
		this.wzCurVolMntPoint = wzCurVolMntPoint;
	}

	public String getWzCurVolMntPoint() {
		return wzCurVolMntPoint;
	}

	public long getUlCompressLevel() {
		return ulCompressLevel;
	}

	public void setUlCompressLevel(long ulCompressLevel) {
		this.ulCompressLevel = ulCompressLevel;
	}

	public long getTransferMode() {
		return transferMode;
	}

	public void setTransferMode(long transferMode) {
		this.transferMode = transferMode;
	}

	public String getWszEDB() {
		return wszEDB;
	}

	public void setWszEDB(String wszEDB) {
		this.wszEDB = wszEDB;
	}

	public String getWszMailFolder() {
		return wszMailFolder;
	}

	public void setWszMailFolder(String wszMailFolder) {
		this.wszMailFolder = wszMailFolder;
	}

	public long getUlTotalFolder() {
		return ulTotalFolder;
	}

	public void setUlTotalFolder(long ulTotalFolder) {
		this.ulTotalFolder = ulTotalFolder;
	}

	public long getUlProcessedFolder() {
		return ulProcessedFolder;
	}

	public void setUlProcessedFolder(long ulProcessedFolder) {
		this.ulProcessedFolder = ulProcessedFolder;
	}

	public long getProductType() {
		return productType;
	}

	public void setProductType(long productType) {
		this.productType = productType;
	}

	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}

	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}

	//SHUZH02NOTIMPLEMENTED
	public long getUlReplicationType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getUlUniqueData() {
		return ulUniqueData;
	}

	public void setUlUniqueData(long ulUniqueData) {
		this.ulUniqueData = ulUniqueData;
	}
	
	public long getUlTotalVMJobCount() {
		return ulTotalVMJobCount;
	}

	public void setUlTotalVMJobCount(long ulTotalVMJobCount) {
		this.ulTotalVMJobCount = ulTotalVMJobCount;
	}

	public long getUlFinishedVMJobCount() {
		return ulFinishedVMJobCount;
	}

	public void setUlFinishedVMJobCount(long ulFinishedVMJobCount) {
		this.ulFinishedVMJobCount = ulFinishedVMJobCount;
	}

	public long getUlCanceledVMJobCount() {
		return ulCanceledVMJobCount;
	}

	public void setUlCanceledVMJobCount(long ulCanceledVMJobCount) {
		this.ulCanceledVMJobCount = ulCanceledVMJobCount;
	}

	public long getUlFailedVMJobCount() {
		return ulFailedVMJobCount;
	}

	public void setUlFailedVMJobCount(long ulFailedVMJobCount) {
		this.ulFailedVMJobCount = ulFailedVMJobCount;
	}
	
	public void setVmHostName(String vmHostName)
	{
		this.vmHostName = vmHostName;
	}
	
	public String getVmHostName()
	{
		return this.vmHostName;	
	}
	
	public void setJobSubStatus(long jobSubStatus)
	{
		this.jobSubStatus = jobSubStatus;
	}
	
	public long getJobSubStatus()
	{
		return this.jobSubStatus;
	}
	
//	public void setPlanUUID(String planUUID)
//	{
//		this.planUUID = planUUID;
//	}
//	
//	public String getPlanUUID()
//	{
//		return this.planUUID;
//	}
}
