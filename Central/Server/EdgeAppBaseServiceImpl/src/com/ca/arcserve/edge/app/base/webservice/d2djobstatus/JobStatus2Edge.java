package com.ca.arcserve.edge.app.base.webservice.d2djobstatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "D2DJobStatus", propOrder = {
	"jobId",
    "startTime",
    "curProcessDiskName",
    "estimateBytesDisk",
    "estimateBytesJob",
    "flags",
    "jobMethod",
    "jobPhase", 
    "jobStatus",
    "jobType",
    "sessionID",
    "transferBytesDisk",
    "transferBytesJob",
    "elapsedTime", 
    "volMethod",
    
    "readSpeed",
    "writeSpeed",
    "totalSizeRead",
    "totalSizeWritten",
    "ulMergedSession", 
    "ulTotalMergedSessions",
    "throttling",
    "ctCurCatVol",
    "compressLevel",
    "encInfoStatus",
    "ctBKStartTime",
    "ulProcessedFolder",
    "ulTotalFolder",
    "curVolMntPoint",
    "wszMailFolder",
    
    "isDriverInstalled",
	"isRestarted",
	"estimatedValue",
	"isBackupConfiged",
	"lastBackupTime",
	"lastBackupJobStatus",
	"lastBackupType",
	"lastBackupStatus",
	"recoveryPointRetentionCount",
	"recoveryPointCount",
	"recoveryPointMounted",
	"recoveryPointStatus",
	"isUseBackupSets",
	"destinationPath",
	"isDestinationAccessible",
	"destinationFreeSpace",
	"destinationEstimatedBackupCount",
	"destinationStatus",
	"overallStatus"
})

@XmlRootElement(name = "D2DJobStatus")
public class JobStatus2Edge {
	@XmlElement(name = "JobId", required = true)
    protected long jobId;
    
	@XmlElement(name = "StartTime", required = true)
    protected long startTime;
	
    @XmlElement(name = "CurProcessDiskName", required = true)
    protected String curProcessDiskName;
    
    @XmlElement(name = "EstimateBytesDisk", required = true)
    protected long estimateBytesDisk;
    
    @XmlElement(name = "EstimateBytesJob", required = true)
    protected long estimateBytesJob;
    
    @XmlElement(name = "Flags", required = true)
    protected long flags;
    
    @XmlElement(name = "JobMethod", required = true)
    protected long jobMethod;
    
    @XmlElement(name = "JobPhase", required = true)
    protected long jobPhase;
    
    @XmlElement(name = "JobStatus", required = true)
    protected long jobStatus;
    
    @XmlElement(name = "JobType", required = true)
    protected long jobType;
    
    @XmlElement(name = "SessionID", required = true)
    protected long sessionID;
    
    @XmlElement(name = "TransferBytesDisk", required = true)
    protected long transferBytesDisk;
    
    @XmlElement(name = "TransferBytesJob", required = true)
    protected long transferBytesJob;
    
    @XmlElement(name = "ElapsedTime", required = true)
    protected long elapsedTime;
    
    @XmlElement(name = "VolMethod", required = true)
    protected long volMethod;
    
    @XmlElement(name = "ReadSpeed", required = true)
    protected long	readSpeed;
    @XmlElement(name = "WriteSpeed", required = true)
    protected long    writeSpeed;
    @XmlElement(name = "TotalSizeRead", required = true)
    protected long    totalSizeRead;
    @XmlElement(name = "TotalSizeWritten", required = true)
    protected long    totalSizeWritten;
    @XmlElement(name = "UlMergedSession", required = true)
    protected long    ulMergedSession; 
    @XmlElement(name = "UlTotalMergedSessions", required = true)
    protected long    ulTotalMergedSessions;
    @XmlElement(name = "Throttling", required = true)
    protected long    throttling;
    @XmlElement(name = "CtCurCatVol", required = true)
    protected String  ctCurCatVol;
    @XmlElement(name = "CompressLevel", required = true)
    protected long    compressLevel;
    @XmlElement(name = "EncInfoStatus", required = true)
    protected long    encInfoStatus;
    @XmlElement(name = "CtBKStartTime", required = true)
    protected String  ctBKStartTime;
    @XmlElement(name = "UlProcessedFolder", required = true)
    protected long    ulProcessedFolder;
    @XmlElement(name = "UlTotalFolder", required = true)
    protected long    ulTotalFolder;
    @XmlElement(name = "CurVolMntPoint", required = true)
    protected String  curVolMntPoint;
    @XmlElement(name = "WszMailFolder", required = true)
    protected String  wszMailFolder;
    
    @XmlElement(name = "isDriverInstalled", required = true)
    protected int isDriverInstalled;
	@XmlElement(name = "isRestarted", required = true)
    protected int isRestarted;
    @XmlElement(name = "estimatedValue", required = true)
    protected int estimatedValue;
    @XmlElement(name = "isBackupConfiged", required = true)
    protected int isBackupConfiged;
    @XmlElement(name = "lastBackupTime", required = true)
    protected long lastBackupTime;
    @XmlElement(name = "lastBackupJobStatus", required = true)
    protected int  lastBackupJobStatus;
    @XmlElement(name = "lastBackupType", required = true)
    protected int lastBackupType;
    @XmlElement(name = "lastBackupStatus", required = true)
    protected int lastBackupStatus;

	// recovery point info
    @XmlElement(name = "recoveryPointRetentionCount", required = true)
    protected int recoveryPointRetentionCount;
    @XmlElement(name = "recoveryPointCount", required = true)
    protected int recoveryPointCount;
    @XmlElement(name = "recoveryPointMounted", required = true)
    protected String recoveryPointMounted;
    @XmlElement(name = "recoveryPointStatus", required = true)
    protected int recoveryPointStatus;
    @XmlElement(name = "isUseBackupSets", required = true)
    protected boolean isUseBackupSets;
	
	// destination info
    @XmlElement(name = "destinationPath", required = true)
    protected String destinationPath;
    @XmlElement(name = "isDestinationAccessible", required = true)
    protected int isDestinationAccessible;
    @XmlElement(name = "destinationFreeSpace", required = true)
    protected long destinationFreeSpace;
    @XmlElement(name = "destinationEstimatedBackupCount", required = true)
    protected int destinationEstimatedBackupCount;
    @XmlElement(name = "destinationStatus", required = true)
	protected int destinationStatus;
	
	// overall info
    @XmlElement(name = "overallStatus", required = true)
	protected int overallStatus;
    
    public int getIsDriverInstalled() {
		return isDriverInstalled;
	}

	public void setIsDriverInstalled(int isDriverInstalled) {
		this.isDriverInstalled = isDriverInstalled;
	}

	public int getIsRestarted() {
		return isRestarted;
	}

	public void setIsRestarted(int isRestarted) {
		this.isRestarted = isRestarted;
	}

	public int getEstimatedValue() {
		return estimatedValue;
	}

	public void setEstimatedValue(int estimatedValue) {
		this.estimatedValue = estimatedValue;
	}

	public int getIsBackupConfiged() {
		return isBackupConfiged;
	}

	public void setIsBackupConfiged(int isBackupConfiged) {
		this.isBackupConfiged = isBackupConfiged;
	}

	public long getLastBackupTime() {
		return lastBackupTime;
	}

	public void setLastBackupTime(long lastBackupTime) {
		this.lastBackupTime = lastBackupTime;
	}

	public int getLastBackupJobStatus() {
		return lastBackupJobStatus;
	}

	public void setLastBackupJobStatus(int lastBackupJobStatus) {
		this.lastBackupJobStatus = lastBackupJobStatus;
	}

	public int getLastBackupType() {
		return lastBackupType;
	}

	public void setLastBackupType(int lastBackupType) {
		this.lastBackupType = lastBackupType;
	}

	public int getLastBackupStatus() {
		return lastBackupStatus;
	}

	public void setLastBackupStatus(int lastBackupStatus) {
		this.lastBackupStatus = lastBackupStatus;
	}

	public int getRecoveryPointRetentionCount() {
		return recoveryPointRetentionCount;
	}

	public void setRecoveryPointRetentionCount(int recoveryPointRetentionCount) {
		this.recoveryPointRetentionCount = recoveryPointRetentionCount;
	}

	public int getRecoveryPointCount() {
		return recoveryPointCount;
	}

	public void setRecoveryPointCount(int recoveryPointCount) {
		this.recoveryPointCount = recoveryPointCount;
	}

	public boolean getIsUseBackupSets() {
		return isUseBackupSets;
	}

	public void setIsUseBackupSets(boolean isUseBackupSets) {
		this.isUseBackupSets = isUseBackupSets;
	}
	
	public String getRecoveryPointMounted() {
		return recoveryPointMounted;
	}

	public void setRecoveryPointMounted(String recoveryPointMounted) {
		this.recoveryPointMounted = recoveryPointMounted;
	}

	public int getRecoveryPointStatus() {
		return recoveryPointStatus;
	}

	public void setRecoveryPointStatus(int recoveryPointStatus) {
		this.recoveryPointStatus = recoveryPointStatus;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public int getIsDestinationAccessible() {
		return isDestinationAccessible;
	}

	public void setIsDestinationAccessible(int isDestinationAccessible) {
		this.isDestinationAccessible = isDestinationAccessible;
	}

	public long getDestinationFreeSpace() {
		return destinationFreeSpace;
	}

	public void setDestinationFreeSpace(long destinationFreeSpace) {
		this.destinationFreeSpace = destinationFreeSpace;
	}

	public int getDestinationEstimatedBackupCount() {
		return destinationEstimatedBackupCount;
	}

	public void setDestinationEstimatedBackupCount(
			int destinationEstimatedBackupCount) {
		this.destinationEstimatedBackupCount = destinationEstimatedBackupCount;
	}

	public int getDestinationStatus() {
		return destinationStatus;
	}

	public void setDestinationStatus(int destinationStatus) {
		this.destinationStatus = destinationStatus;
	}

	public int getOverallStatus() {
		return overallStatus;
	}

	public void setOverallStatus(int overallStatus) {
		this.overallStatus = overallStatus;
	}
	
    public long getReadSpeed() {
		return readSpeed;
	}

	public void setReadSpeed(long readSpeed) {
		this.readSpeed = readSpeed;
	}

	public long getWriteSpeed() {
		return writeSpeed;
	}

	public void setWriteSpeed(long writeSpeed) {
		this.writeSpeed = writeSpeed;
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

	public long getUlMergedSession() {
		return ulMergedSession;
	}

	public void setUlMergedSession(long ulMergedSession) {
		this.ulMergedSession = ulMergedSession;
	}

	public long getUlTotalMergedSessions() {
		return ulTotalMergedSessions;
	}

	public void setUlTotalMergedSessions(long ulTotalMergedSessions) {
		this.ulTotalMergedSessions = ulTotalMergedSessions;
	}

	public long getThrottling() {
		return throttling;
	}

	public void setThrottling(long throttling) {
		this.throttling = throttling;
	}

	public String getCtCurCatVol() {
		return ctCurCatVol;
	}

	public void setCtCurCatVol(String ctCurCatVol) {
		this.ctCurCatVol = ctCurCatVol;
	}

	public long getCompressLevel() {
		return compressLevel;
	}

	public void setCompressLevel(long compressLevel) {
		this.compressLevel = compressLevel;
	}

	public long getEncInfoStatus() {
		return encInfoStatus;
	}

	public void setEncInfoStatus(long encInfoStatus) {
		this.encInfoStatus = encInfoStatus;
	}

	public String getCtBKStartTime() {
		return ctBKStartTime;
	}

	public void setCtBKStartTime(String ctBKStartTime) {
		this.ctBKStartTime = ctBKStartTime;
	}

	public long getUlProcessedFolder() {
		return ulProcessedFolder;
	}

	public void setUlProcessedFolder(long ulProcessedFolder) {
		this.ulProcessedFolder = ulProcessedFolder;
	}

	public long getUlTotalFolder() {
		return ulTotalFolder;
	}

	public void setUlTotalFolder(long ulTotalFolder) {
		this.ulTotalFolder = ulTotalFolder;
	}

	public String getCurVolMntPoint() {
		return curVolMntPoint;
	}

	public void setCurVolMntPoint(String curVolMntPoint) {
		this.curVolMntPoint = curVolMntPoint;
	}

	public String getWszMailFolder() {
		return wszMailFolder;
	}

	public void setWszMailFolder(String wszMailFolder) {
		this.wszMailFolder = wszMailFolder;
	}
	
	public void setJobId(long value)
	{
		this.jobId = value;
	}
	
	public long getJobId()
	{
		return jobId;
	}
	
	public void setStartTime(long value)
	{
		this.startTime = value;
	}
	
	public long getStartTime()
	{
		return startTime;
	}
	
    public void setCurProcessDiskName(String value)
    {
    	this.curProcessDiskName = value;
    }
    
    public String getCurProcessDiskName()
    {
    	return curProcessDiskName;
    }
    
    public void setEstimateBytesDisk(long value)
    {
    	this.estimateBytesDisk = value;
    }
    
    public long getEstimateBytesDisk()
    {
    	return estimateBytesDisk;
    }
    
    public void setEstimateBytesJob(long value)
    {
    	this.estimateBytesJob = value;
    }
    
    public long getEstimateBytesJob()
    {
    	return estimateBytesJob;
    }
    
    public void setFlags(long value)
    {
    	this.flags = value;
    }
    
    public long getFlags()
    {
    	return flags;
    }
    
    public void setJobMethod(long value)
    {
    	this.jobMethod = value;
    }
    
    public long getJobMethod()
    {
    	return jobMethod;
    }
    
    public void setJobPhase(long value)
    {
    	this.jobPhase = value;
    }
    
    public long getJobPhase()
    {
    	return jobPhase;
    }
    
    public void setJobStatus(long value)
    {
    	this.jobStatus = value;
    }
    
    public long getJobStatus()
    {
    	return jobStatus;
    }
    
    public void setJobType(long value)
    {
    	this.jobType = value;
    }
    
    public long getJobType()
    {
    	return jobType;
    }
    
    public void setSessionID(long value)
    {
    	this.sessionID = value;
    }
    
    public long getSessionID()
    {
    	return sessionID;
    }
    
    public void setTransferBytesDisk(long value)
    {
    	this.transferBytesDisk = value;
    }
    
    public long getTransferBytesDisk()
    {
    	return transferBytesDisk;
    }
    
    public void setTransferBytesJob(long value)
    {
    	this.transferBytesJob = value;
    }
    
    public long getTransferBytesJob()
    {
    	return transferBytesJob;
    }
    
    public void setElapsedTime(long value)
    {
    	this.elapsedTime = value;
    }
    
    public long getElapsedTime()
    {
    	return elapsedTime;
    }
    
    public void setVolMethod(long value)
    {
    	this.volMethod = value;
    }
    
    public long getVolMethod()
    {
    	return volMethod;
    }
}