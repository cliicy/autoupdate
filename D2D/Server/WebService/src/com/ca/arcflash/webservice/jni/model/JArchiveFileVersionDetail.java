package com.ca.arcflash.webservice.jni.model;

public class JArchiveFileVersionDetail {
	private String Version;
	private long FileSizeLow;
	private long FileSizeHigh;
	//private long ModifiedDateTime;
	//private long ArchivedDateTime;
	private int FileType;
	private long modifiedHour;
	private long modifiedMin;
	private long modifiedSec;   
	private long modifiedDay;
	private long modifiedMonth;
	private long modifiedYear;
	private long modDateTime;
	
	private long archivedHour;
	private long archivedMin;
	private long archivedSec;   
	private long archivedDay;
	private long archivedMonth;
	private long archivedYear;
	private long archiveDateTime;
    
	public long getmodDateTime() {
		return modDateTime;
	}

	public void setmodDateTime(long in_modDateTime) {
		this.modDateTime = in_modDateTime;
	}
	
	public long getarchiveDateTime() {
		return archiveDateTime;
	}

	public void setarchiveDateTime(long in_archiveDateTime) {
		this.archiveDateTime = in_archiveDateTime;
	}
	
    public String getVersion() {
		return Version;
	}

	public void setVersion(String in_Version) {
		this.Version = in_Version;
	}
	
	public long getFileSizeLow()
	{
		return FileSizeLow;
	}
	public void setFileSizeLow(long in_FileSizeLow)
	{
		this.FileSizeLow = in_FileSizeLow;
	}
	
	public long getFileSizeHigh()
	{
		return FileSizeHigh;
	}
	public void setFileSizeHigh(long in_FileSizeHigh)
	{
		this.FileSizeHigh = in_FileSizeHigh;
	}

	////modified date and time
	public void setmodifiedHour(long in_lHour) {
		this.modifiedHour = in_lHour;
	}
	public long getmodifiedHour() {
		return modifiedHour;
	}
	
	public void setmodifiedMin(long in_lMin) {
		this.modifiedMin = in_lMin;
	}
	public long getmodifiedMin() {
		return modifiedMin;
	}
	
	public void setmodifiedSec(long in_lSec) {
		this.modifiedSec = in_lSec;
	}
	public long getmodifiedSec() {
		return modifiedSec;
	}
	
	public void setmodifiedDay(long in_lDay) {
		this.modifiedDay = in_lDay;
	}
	public long getmodifiedDay() {
		return modifiedDay;
	}
	
	public void setmodifiedMonth(long in_lMonth) {
		this.modifiedMonth = in_lMonth;
	}
	public long getmodifiedMonth() {
		return modifiedMonth;
	}
	
	public void setmodifiedYear(long in_lYear) {
		this.modifiedYear = in_lYear;
	}
	public long getmodifiedYear() {
		return modifiedYear;
	}
	
	//archive date and time
	public void setarchivedHour(long in_lHour) {
		this.archivedHour = in_lHour;
	}
	public long getarchivedHour() {
		return archivedHour;
	}
	
	public void setarchivedMin(long in_lMin) {
		this.archivedMin = in_lMin;
	}
	public long getarchivedMin() {
		return archivedMin;
	}
	
	public void setarchivedSec(long in_lSec) {
		this.archivedSec = in_lSec;
	}
	public long getarchivedSec() {
		return archivedSec;
	}
	
	public void setarchivedDay(long in_lDay) {
		this.archivedDay = in_lDay;
	}
	public long getarchivedDay() {
		return archivedDay;
	}
	
	public void setarchivedMonth(long in_lMonth) {
		this.archivedMonth = in_lMonth;
	}
	public long getarchivedMonth() {
		return archivedMonth;
	}
	
	public void setarchivedYear(long in_lYear) {
		this.archivedYear = in_lYear;
	}
	public long getarchivedYear() {
		return archivedYear;
	}
	
	public int getFileType() {
		return FileType;
	}

	public void setFileType(int in_FileType) {
		this.FileType = in_FileType;
	}

}
