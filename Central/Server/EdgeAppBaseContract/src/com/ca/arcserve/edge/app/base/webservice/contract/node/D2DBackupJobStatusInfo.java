package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;


public class D2DBackupJobStatusInfo implements Serializable{
	
	private static final long serialVersionUID = 4867427257158703362L;

	private int nodeId;
	private long id;
	private long sessionID;
	private long flags;
	private long jobPhase;
	private long jobStatus;
	private long jobType;
	private long jobMethod;
	private long volMethod;
	private long estimateBytesJob;
	private long transferBytesJob;
	private long estimateBytesDisk;
	private long transferBytesDisk;
	private String currentProcessDiskName;
	private long backupStartTime;
	private long elapsedTime;
	private long	nProgramCPU;  // CPU usage of Afbackend.exe    Numbers, should be 0 ~ 100
	private long    nSystemCPU;        // Total system CPU usage         Numbers, should be 0 ~ 100, and should be no less than CPU usage of afbackend.exe
	private long	nReadSpeed;        // Read I/O speed of afbackend.exe    MB/min, 
	private long    nWriteSpeed;  // Write I/O speed of afbackend.exe     MB/min
	private long    nSystemReadSpeed;// Total system read speed MB/Min
	private long    nSystemWriteSpeed;//Total system write speed MB/Min
	private long    throttling;       //ZZ: Backup throughout throttling, MB/min.
    private long    encInfoStatus;    //ZZ: Encryption algorithm ID, AES-128, 192 and 256
    private long    totalSizeRead;    //ZZ: Total size has been read from snapshot. Means data size before compression,
    private long    totalSizeWritten; //ZZ: Total size has been written to VHD. Means data size after compression. [Compression rate]=ulTotalSizeWritten/ulTotalSizeRead;
    private String  curVolMntPoint;  //ZZ: Current volume being backed up. Maybe mount point, driver letter or GUID.
    private long compressLevel;	// compress level which is defined
    private long transferMode; // vSphere transfer mode San or NBD
	private long ctDWBKJobID;// ZZ: for catalog Job ID used for backup job when
	private String ctBKStartTime;// ZZ: for catalog Session time.
	private String ctBKJobName;// ZZ: for catalog Backup job name.
	private String ctCurCatVol;// ZZ: for catalog Information for volume or
								// Exchange database is generated catalog.
	private long dwBKSessNum;                 //ZZ: Session number for which catalog is created.
    private String wzBKBackupDest;    //ZZ: Backup destination for the session for which catalog is created.
    private String wzBKDestUsrName;   //ZZ: User name of backup destination when it is remote folder.
    private String wzBKDestPassword;  //ZZ: Password of backup destination when it is remote folder.
	//For GRT catalog	
    private String wszEDB;// current EDB file
    private String wszMailFolder;// current Mail folder
    private long ulTotalFolder;
    private long ulProcessedFolder;
    private long ulTotalMergedSessions; // the number of total to be merged sessions
	private long ulMergedSession;     // the sequence of being merged sessions
	private long productType; //product type vsphere/d2d
	private String vmInstanceUUID;
	private com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo d2DStatusInfo;
	
	public com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo getD2DStatusInfo() {
		return d2DStatusInfo;
	}
	public void setD2DStatusInfo(com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo d2dStatusInfo) {
		d2DStatusInfo = d2dStatusInfo;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getSessionID() {
		return sessionID;
	}
	public void setSessionID(long sessionID) {
		this.sessionID = sessionID;
	}
	public long getFlags() {
		return flags;
	}
	public void setFlags(long flags) {
		this.flags = flags;
	}
	public long getJobPhase() {
		return jobPhase;
	}
	public void setJobPhase(long jobPhase) {
		this.jobPhase = jobPhase;
	}
	public long getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(long jobStatus) {
		this.jobStatus = jobStatus;
	}
	public long getJobType() {
		return jobType;
	}
	public void setJobType(long jobType) {
		this.jobType = jobType;
	}
	public long getJobMethod() {
		return jobMethod;
	}
	public void setJobMethod(long jobMethod) {
		this.jobMethod = jobMethod;
	}
	public long getVolMethod() {
		return volMethod;
	}
	public void setVolMethod(long volMethod) {
		this.volMethod = volMethod;
	}
	public long getEstimateBytesJob() {
		return estimateBytesJob;
	}
	public void setEstimateBytesJob(long estimateBytesJob) {
		this.estimateBytesJob = estimateBytesJob;
	}
	public long getTransferBytesJob() {
		return transferBytesJob;
	}
	public void setTransferBytesJob(long transferBytesJob) {
		this.transferBytesJob = transferBytesJob;
	}
	public long getEstimateBytesDisk() {
		return estimateBytesDisk;
	}
	public void setEstimateBytesDisk(long estimateBytesDisk) {
		this.estimateBytesDisk = estimateBytesDisk;
	}
	public long getTransferBytesDisk() {
		return transferBytesDisk;
	}
	public void setTransferBytesDisk(long transferBytesDisk) {
		this.transferBytesDisk = transferBytesDisk;
	}
	public String getCurrentProcessDiskName() {
		return currentProcessDiskName;
	}
	public void setCurrentProcessDiskName(String currentProcessDiskName) {
		this.currentProcessDiskName = currentProcessDiskName;
	}
	public long getBackupStartTime() {
		return backupStartTime;
	}
	public void setBackupStartTime(long backupStartTime) {
		this.backupStartTime = backupStartTime;
	}
	public long getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	public long getnProgramCPU() {
		return nProgramCPU;
	}
	public void setnProgramCPU(long nProgramCPU) {
		this.nProgramCPU = nProgramCPU;
	}
	public long getnSystemCPU() {
		return nSystemCPU;
	}
	public void setnSystemCPU(long nSystemCPU) {
		this.nSystemCPU = nSystemCPU;
	}
	public long getnReadSpeed() {
		return nReadSpeed;
	}
	public void setnReadSpeed(long nReadSpeed) {
		this.nReadSpeed = nReadSpeed;
	}
	public long getnWriteSpeed() {
		return nWriteSpeed;
	}
	public void setnWriteSpeed(long nWriteSpeed) {
		this.nWriteSpeed = nWriteSpeed;
	}
	public long getnSystemReadSpeed() {
		return nSystemReadSpeed;
	}
	public void setnSystemReadSpeed(long nSystemReadSpeed) {
		this.nSystemReadSpeed = nSystemReadSpeed;
	}
	public long getnSystemWriteSpeed() {
		return nSystemWriteSpeed;
	}
	public void setnSystemWriteSpeed(long nSystemWriteSpeed) {
		this.nSystemWriteSpeed = nSystemWriteSpeed;
	}
	public long getThrottling() {
		return throttling;
	}
	public void setThrottling(long throttling) {
		this.throttling = throttling;
	}
	public long getEncInfoStatus() {
		return encInfoStatus;
	}
	public void setEncInfoStatus(long encInfoStatus) {
		this.encInfoStatus = encInfoStatus;
	}
	public long getTotalSizeRead() {
		return totalSizeRead;
	}
	public void setTotalSizeRead(long totalSizeRead) {
		this.totalSizeRead = totalSizeRead;
	}
	public long getTotalSizeWritten() {
		return totalSizeWritten;
	}
	public void setTotalSizeWritten(long totalSizeWritten) {
		this.totalSizeWritten = totalSizeWritten;
	}
	public String getCurVolMntPoint() {
		return curVolMntPoint;
	}
	public void setCurVolMntPoint(String curVolMntPoint) {
		this.curVolMntPoint = curVolMntPoint;
	}
	public long getCompressLevel() {
		return compressLevel;
	}
	public void setCompressLevel(long compressLevel) {
		this.compressLevel = compressLevel;
	}
	public long getTransferMode() {
		return transferMode;
	}
	public void setTransferMode(long transferMode) {
		this.transferMode = transferMode;
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

}
