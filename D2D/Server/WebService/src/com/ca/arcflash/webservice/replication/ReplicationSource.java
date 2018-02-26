package com.ca.arcflash.webservice.replication;

public class ReplicationSource {
	private String baseDir;
	private String[] fileOrDirsRelativeToBase;
	private String userName;
	private String pwd;

	public ReplicationSource() {
		baseDir = "";
		fileOrDirsRelativeToBase = new String[0];
		userName = "";
		pwd = "";
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public String[] getFileOrDirsRelativeToBase() {
		return fileOrDirsRelativeToBase;
	}

	public void setFileOrDirsRelativeToBase(String[] fileOrDirsRelativeToBase) {
		this.fileOrDirsRelativeToBase = fileOrDirsRelativeToBase;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public boolean hasFilesOrDirsRelativeToBase() {
		return this.getFileOrDirsRelativeToBase() != null
				&& this.getFileOrDirsRelativeToBase().length > 0;
	}
}
