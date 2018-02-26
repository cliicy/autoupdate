package com.ca.arcflash.webservice.jni.model;

public class JMsgSearchRec {
	private JMsgRec msgRec;	
	private long  SessionNumber;
	private long  SubSessionNumber;
	private String mailboxOrSameLevelName;
	private String edbFullPath;//edb real restore path
	private long edbType;// // Root Public folder (254)or edb(255)
	private String edbDisplayName;// edb display full path name
	private String mailFullDisplayPath;
	
	// fields related to the recovery point, for Encryption use
	private long ulFullSessNum;       //ZZ: Full session number, if current session is full, it equals to SessionNumber.
    private long  ulEncryptInfo;      //ZZ: Encryption information. Currently non-zero mean encrypted session.
    private long  ulBKTime;           //ZZ: Current recovery point time.
    private String wzBKDest;  		  //ZZ: Current backup destination for current session.
    private String wzJobName;         //ZZ: Current backup job number.
    private String wzPWDHash;         //ZZ: Hash value string for current session password
    private String wzSessGUID;
    private String wzFullSessGUID;
	
	public JMsgRec getMsgRec() {
		return msgRec;
	}
	public void setMsgRec(JMsgRec msgRec) {
		this.msgRec = msgRec;
	}
	public long getSessionNumber() {
		return SessionNumber;
	}
	public void setSessionNumber(long sessionNumber) {
		SessionNumber = sessionNumber;
	}
	public long getSubSessionNumber() {
		return SubSessionNumber;
	}
	public void setSubSessionNumber(long subSessionNumber) {
		SubSessionNumber = subSessionNumber;
	}
	public String getMailboxOrSameLevelName() {
		return mailboxOrSameLevelName;
	}
	public void setMailboxOrSameLevelName(String mailboxOrSameLevelName) {
		this.mailboxOrSameLevelName = mailboxOrSameLevelName;
	}
	public String getEdbFullPath() {
		return edbFullPath;
	}
	public void setEdbFullPath(String edbFullPath) {
		this.edbFullPath = edbFullPath;
	}
	public long getEdbType() {
		return edbType;
	}
	public void setEdbType(long edbType) {
		this.edbType = edbType;
	}
	public String getEdbDisplayName() {
		return edbDisplayName;
	}
	public void setEdbDisplayName(String edbDisplayName) {
		this.edbDisplayName = edbDisplayName;
	}
	public void setMailFullDisplayPath(String mailFullDisplayPath) {
		this.mailFullDisplayPath = mailFullDisplayPath;
	}
	public String getMailFullDisplayPath() {
		return mailFullDisplayPath;
	}
	public long getUlFullSessNum()
	{
		return ulFullSessNum;
	}
	public void setUlFullSessNum(long ulFullSessNum)
	{
		this.ulFullSessNum = ulFullSessNum;
	}
	public long getUlEncryptInfo()
	{
		return ulEncryptInfo;
	}
	public void setUlEncryptInfo(long ulEncryptInfo)
	{
		this.ulEncryptInfo = ulEncryptInfo;
	}
	public long getUlBKTime()
	{
		return ulBKTime;
	}
	public void setUlBKTime(long ulBKTime)
	{
		this.ulBKTime = ulBKTime;
	}
	public String getWzBKDest()
	{
		return wzBKDest;
	}
	public void setWzBKDest(String wzBKDest)
	{
		this.wzBKDest = wzBKDest;
	}
	public String getWzJobName()
	{
		return wzJobName;
	}
	public void setWzJobName(String wzJobName)
	{
		this.wzJobName = wzJobName;
	}
	public String getWzPWDHash()
	{
		return wzPWDHash;
	}
	public void setWzPWDHash(String wzPWDHash)
	{
		this.wzPWDHash = wzPWDHash;
	}
	public String getWzSessGUID()
	{
		return wzSessGUID;
	}
	public void setWzSessGUID(String wzSessGUID)
	{
		this.wzSessGUID = wzSessGUID;
	}
	public String getWzFullSessGUID()
	{
		return wzFullSessGUID;
	}
	public void setWzFullSessGUID(String wzFullSessGUID)
	{
		this.wzFullSessGUID = wzFullSessGUID;
	}
}
