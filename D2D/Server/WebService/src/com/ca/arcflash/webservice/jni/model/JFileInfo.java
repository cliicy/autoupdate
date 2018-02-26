package com.ca.arcflash.webservice.jni.model;

public class JFileInfo {

	public static final long FILE_ATTRIBUTE_DIRECTORY = 0x10;
	public static final long FILE_ATTRIBUTE_READONLY  = 0x1;

	private long dwFileAttributes;
	private long nFileSizeHigh;
	private long nFileSizeLow;
	private long creationDateTime;
	private long lastAccessDateTime;
	private long lastWriteDateTime;
	private String strName;
	private String strPath;

	public long getDwFileAttributes() {
		return dwFileAttributes;
	}

	public void setDwFileAttributes(long dwFileAttributes) {
		this.dwFileAttributes = dwFileAttributes;
	}

	public long getNFileSizeHigh() {
		return nFileSizeHigh;
	}

	public void setNFileSizeHigh(long fileSizeHigh) {
		nFileSizeHigh = fileSizeHigh;
	}

	public long getNFileSizeLow() {
		return nFileSizeLow;
	}

	public void setNFileSizeLow(long fileSizeLow) {
		nFileSizeLow = fileSizeLow;
	}

	public long getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(long creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public long getLastAccessDateTime() {
		return lastAccessDateTime;
	}

	public void setLastAccessDateTime(long lastAccessDateTime) {
		this.lastAccessDateTime = lastAccessDateTime;
	}

	public long getLastWriteDateTime() {
		return lastWriteDateTime;
	}

	public void setLastWriteDateTime(long lastWriteDateTime) {
		this.lastWriteDateTime = lastWriteDateTime;
	}

	public String getStrName() {
		return strName;
	}

	public void setStrName(String strName) {
		this.strName = strName;
	}

	public String getStrPath() {
		return strPath;
	}

	public void setStrPath(String strPath) {
		this.strPath = strPath;
	}

	public boolean isDirectory() {
		return (FILE_ATTRIBUTE_DIRECTORY & getDwFileAttributes()) > 0;
	}

}
