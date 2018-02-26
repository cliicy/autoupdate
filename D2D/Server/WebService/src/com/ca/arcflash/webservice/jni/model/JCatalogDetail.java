package com.ca.arcflash.webservice.jni.model;

public class JCatalogDetail {
	private long longNameID;
	private long pathID;
	private int sessType;
	private int dataType;
	private long fileDate;
	private long fileSizeHigh; // High File Size
	private long fileSize; // Low File Size
	private int sessionNumber;
	private int subSessionNumber;
	private String longName;
	private String path;
	private String displayName;
	private long childrenCount;
	
	private long  fullSessNum;       //ZZ: Full session number, if current session is full, it equals to SessionNumber.
    private long  encryptInfo;       //ZZ: Encryption information. Currently non-zero mean encrypted session.
    private String backupDest;  	//ZZ: Backup destination for current session.
    private String jobName; 			//ZZ: Backup job number.
    private long backupTime;  		//ZZ: Recovery point time.
    private String pwdHash;
    private boolean isDefaultSessPwd;
    private String sessionGuid;
    private String fullSessionGuid;
    private int volAttr;// volume type 
    private int driverLeterAttr = 1; //if 1 driver leter exists when backup 0 not exists

	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public int getSessionNumber() {
		return sessionNumber;
	}
	public void setSessionNumber(int sessionNumber) {
		this.sessionNumber = sessionNumber;
	}
	public int getSubSessionNumber() {
		return subSessionNumber;
	}
	public void setSubSessionNumber(int subSessionNumber) {
		this.subSessionNumber = subSessionNumber;
	}
	public long getLongNameID() {
		return longNameID;
	}
	public void setLongNameID(long longNameID) {
		this.longNameID = longNameID;
	}
	public long getPathID() {
		return pathID;
	}
	public void setPathID(long pathID) {
		this.pathID = pathID;
	}
	public int getSessType() {
		return sessType;
	}
	public void setSessType(int sessType) {
		this.sessType = sessType;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public long getFileDate() {
		return fileDate;
	}
	public void setFileDate(long fileDate) {
		this.fileDate = fileDate;
	}
	public long getFileSizeHigh() {
		return fileSizeHigh;
	}
	public void setFileSizeHigh(long fileSizeHigh) {
		this.fileSizeHigh = fileSizeHigh;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getLongName() {
		return longName;
	}
	public void setLongName(String longName) {
		this.longName = longName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setChildrenCount(long childrenCount) {
		this.childrenCount = childrenCount;
	}
	public long getChildrenCount() {
		return childrenCount;
	}
	public void setFullSessNum(long fullSessNum) {
		this.fullSessNum = fullSessNum;
	}
	public long getFullSessNum() {
		return fullSessNum;
	}
	public void setEncryptInfo(long encryptInfo) {
		this.encryptInfo = encryptInfo;
	}
	public long getEncryptInfo() {
		return encryptInfo;
	}
	public void setBackupDest(String backupDest) {
		this.backupDest = backupDest;
	}
	public String getBackupDest() {
		return backupDest;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobName() {
		return jobName;
	}
	public void setBackupTime(long backupTime) {
		this.backupTime = backupTime;
	}
	public long getBackupTime() {
		return backupTime;
	}
	public void setPwdHash(String pwdHash) {
		this.pwdHash = pwdHash;
	}
	public String getPwdHash() {
		return pwdHash;
	}
	public void setSessionGuid(String sessionGuid) {
		this.sessionGuid = sessionGuid;
	}
	public String getSessionGuid() {
		return sessionGuid;
	}
	public void setFullSessionGuid(String fullSessionGuid) {
		this.fullSessionGuid = fullSessionGuid;
	}
	public String getFullSessionGuid() {
		return fullSessionGuid;
	}
	public int getVolAttr() {
		return volAttr;
	}
	public void setVolAttr(int volAttr) {
		this.volAttr = volAttr;
	}
	public int getDriverLeterAttr() {
		return driverLeterAttr;
	}
	public void setDriverLeterAttr(int driverLeterAttr) {
		this.driverLeterAttr = driverLeterAttr;
	}
	public boolean isDefaultSessPwd() {
		return isDefaultSessPwd;
	}
	public void setDefaultSessPwd(boolean isDefaultSessPwd) {
		this.isDefaultSessPwd = isDefaultSessPwd;
	}
}
