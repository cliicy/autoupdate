package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;


public class EdgeVersionInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7071512879845621410L;
	private String versionString;
	private int majorVersion;
	private int minorVersion;
	private int buildNumber;
	private String language;
	private String country;
	private String timeZoneID;
	private String timeZoneDisplayname;
	private int timeZoneOffset;
	private int dstSavings;
	private String adminName;
	private int localADTPackage;
	private String updateNumber;
	private String updateBuildNumber;
	private int requiredD2DMajorVersion;
	private int requiredD2DMinorVersion;
	private Double requiredD2DVersion;
	private String requiredD2DVersionString;
//	private DateFormatEdge dateFormatEdge;
	
	/**
	 * Get language setting of UPD console.
	 * 
	 * @return
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * Set language setting of UPD console.
	 * 
	 * @param language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * Get country setting of UPD console.
	 * 
	 * @return
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Set country setting of UPD console.
	 * 
	 * @param country
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Get time zone ID of UPD console.
	 * 
	 * @return
	 */
	public String getTimeZoneID() {
		return timeZoneID;
	}
	
	/**
	 * Set time zone ID of UPD console.
	 * 
	 * @param timeZoneID
	 */
	public void setTimeZoneID(String timeZoneID) {
		this.timeZoneID = timeZoneID;
	}
	
	/**
	 * Set the amount of time to be added to local standard time to get local wall clock time.
	 * 
	 * @param dstSavings
	 */
	public void setDSTSavings(int dstSavings) {
		this.dstSavings = dstSavings;
	}
	
	/**
	 * Returns the amount of time to be added to local standard time to get local wall clock time.
	 * 
	 * @return
	 */
	public int getDSTSavings() {
		return dstSavings;
	}
	
	/**
	 * Set the display name of the time zone of UDP console.
	 * 
	 * @param timeZoneDisplayname
	 */
	public void setTimeZoneDisplayname(String timeZoneDisplayname) {
		this.timeZoneDisplayname = timeZoneDisplayname;
	}
	
	/**
	 * Get the display name of the time zone of UDP console.
	 * 
	 * @return
	 */
	public String getTimeZoneDisplayname() {
		return timeZoneDisplayname;
	}
	
	/**
	 * Get time zone offset of the UDP console.
	 * 
	 * @return
	 */
	public int getTimeZoneOffset() {
		return timeZoneOffset;
	}
	
	/**
	 * Set time zone offset of the UDP console.
	 * 
	 * @param timeZoneOffset
	 */
	public void setTimeZoneOffset(int timeZoneOffset) {
		this.timeZoneOffset = timeZoneOffset;
	}
	
	/**
	 * Get the version string of UDP console.
	 * 
	 * @return
	 */
	public String getVersionString()
	{
		return versionString;
	}
	
	/**
	 * Set the version string of UDP console.
	 * 
	 * @param versionString
	 */
	public void setVersionString( String versionString )
	{
		this.versionString = versionString;
	}
	
	/**
	 * Get major version of UDP console.
	 * 
	 * @return
	 */
	public int getMajorVersion() {
		return majorVersion;
	}
	
	/**
	 * Set major version of UDP console.
	 * 
	 * @param majorVersion
	 */
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}
	
	/**
	 * Get minor version of UDP console.
	 * 
	 * @return
	 */
	public int getMinorVersion() {
		return minorVersion;
	}
	
	/**
	 * Set minor version of UDP console.
	 * 
	 * @param minorVersion
	 */
	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}
	
	/**
	 * Get build number of UDP console.
	 * 
	 * @return
	 */
	public int getBuildNumber() {
		return buildNumber;
	}
	
	/**
	 * Set build number of UDP console.
	 * 
	 * @param buildNumber
	 */
	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}
	
	/**
	 * Get update number of UDP console.
	 * 
	 * @return
	 */
	public String getUpdateNumber() {
		return updateNumber;
	}
	
	/**
	 * Set update number of UDP console.
	 * 
	 * @param in_updateNumber
	 */
	public void setUpdateNumber(String in_updateNumber) {
		this.updateNumber = in_updateNumber;
	}
	
	/**
	 * Get build number of current update of UDP console.
	 * 
	 * @return
	 */
	public String getUpdateBuildNumber()
	{
		return updateBuildNumber;
	}
	
	/**
	 * Set build number of current update of UDP console.
	 * 
	 * @param updateBuildNumber
	 */
	public void setUpdateBuildNumber( String updateBuildNumber )
	{
		this.updateBuildNumber = updateBuildNumber;
	}
	
	/**
	 * Get the administrator's user name.
	 */
	public String getAdminName() {
		return adminName;
	}
	
	/**
	 * Set the administrator's user name.
	 * 
	 * @param adminName
	 */
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public void setLocalADTPackage(int localADTPackage) {
		this.localADTPackage = localADTPackage;
	}

	public int getLocalADTPackage() {
		return localADTPackage;
	}
	
	/**
	 * Get required version of UDP agent. UDP agents with lower version cannot
	 * interact with UDP console.
	 * 
	 * @return
	 */
	public int getRequiredD2DMajorVersion()
	{
		return requiredD2DMajorVersion;
	}
	
	/**
	 * Set required version of UDP agent. UDP agents with lower version cannot
	 * interact with UDP console.
	 * 
	 * @param requiredD2DMajorVersion
	 */
	public void setRequiredD2DMajorVersion( int requiredD2DMajorVersion )
	{
		this.requiredD2DMajorVersion = requiredD2DMajorVersion;
	}
	
	/**
	 * Get required version of UDP agent. UDP agents with lower version cannot
	 * interact with UDP console.
	 * 
	 * @return
	 */
	public int getRequiredD2DMinorVersion()
	{
		return requiredD2DMinorVersion;
	}
	
	/**
	 * Set required version of UDP agent. UDP agents with lower version cannot
	 * interact with UDP console.
	 * 
	 * @param requiredD2DMinorVersion
	 */
	public void setRequiredD2DMinorVersion( int requiredD2DMinorVersion )
	{
		this.requiredD2DMinorVersion = requiredD2DMinorVersion;
	}
	
	/**
	 * Get required version of UDP agent. UDP agents with lower version cannot
	 * interact with UDP console.
	 * 
	 * @return
	 */
	public Double getRequiredD2DVersion()
	{
		return requiredD2DVersion;
	}
	
	/**
	 * Set required version of UDP agent. UDP agents with lower version cannot
	 * interact with UDP console.
	 * 
	 * @param requireD2DVersion
	 */
	public void setRequiredD2DVersion( Double requireD2DVersion )
	{
		this.requiredD2DVersion = requireD2DVersion;
	}
	
	/**
	 * Get string from of required version of UDP agent. UDP agents with lower
	 * version cannot interact with UDP console.
	 * 
	 * @return
	 */
	public String getRequiredD2DVersionString()
	{
		return requiredD2DVersionString;
	}
	
	/**
	 * Set string from of required version of UDP agent. UDP agents with lower
	 * version cannot interact with UDP console.
	 * 
	 * @param requiredD2DVersionString
	 */
	public void setRequiredD2DVersionString( String requiredD2DVersionString )
	{
		this.requiredD2DVersionString = requiredD2DVersionString;
	}
//	public DateFormatEdge getDateFormatEdge() {
//		return dateFormatEdge;
//	}
//	public void setDateFormatEdge(DateFormatEdge dateFormatEdge) {
//		this.dateFormatEdge = dateFormatEdge;
//	}
}
