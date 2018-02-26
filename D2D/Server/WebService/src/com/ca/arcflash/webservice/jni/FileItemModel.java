package com.ca.arcflash.webservice.jni;

public class FileItemModel {
	
	private String filePath; //D2D full file path in session folder. e.g. c:\\S0000000001\4378923457.D2D
	private String fileDestination;  //destination path
	private String fileVMDKUrl;
	private long blockSize = 1024 * 1024; //block size in VMFS datastore in byte,default value 1MB
	
	public String getFileVMDKUrl() {
		return fileVMDKUrl;
	}
	public void setFileVMDKUrl(String fileVMDKUrl) {
		this.fileVMDKUrl = fileVMDKUrl;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileDestination() {
		return fileDestination;
	}
	public void setFileDestination(String fileDestination) {
		this.fileDestination = fileDestination;
	}
	public long getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}
	
}
