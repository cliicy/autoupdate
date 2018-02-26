package com.ca.arcflash.webservice.jni.model;

public class JCatalogJob
{
	private long ulShrMemID; 	// Unique ID -- used to compose unique share memory filename
	private long ulSessNum; 	// Session number
	private long ulSubSessNum; 	// Sub session number
	private String pwszDestPath; // backup:STOR's destination folder, restore: the alternative location use selected
	private String pwszUserName; // user name for accessing backup destination;restore and copy source //<sonmi01>2009-8-25 ###???
	private String pwszPassword;

	public long getUlShrMemID()
	{
		return ulShrMemID;
	}
	public void setUlShrMemID(long ulShrMemID)
	{
		this.ulShrMemID = ulShrMemID;
	}
	public long getUlSessNum()
	{
		return ulSessNum;
	}
	public void setUlSessNum(long ulSessNum)
	{
		this.ulSessNum = ulSessNum;
	}
	public long getUlSubSessNum()
	{
		return ulSubSessNum;
	}
	public void setUlSubSessNum(long ulSubSessNum)
	{
		this.ulSubSessNum = ulSubSessNum;
	}
	public String getPwszDestPath()
	{
		return pwszDestPath;
	}
	public void setPwszDestPath(String pwszDestPath)
	{
		this.pwszDestPath = pwszDestPath;
	}
	public String getPwszUserName()
	{
		return pwszUserName;
	}
	public void setPwszUserName(String pwszUserName)
	{
		this.pwszUserName = pwszUserName;
	}
	public String getPwszPassword()
	{
		return pwszPassword;
	}
	public void setPwszPassword(String pwszPassword)
	{
		this.pwszPassword = pwszPassword;
	}
}
