package com.ca.arcflash.webservice.jni.model;

import java.util.List;

public class JApplicationComponect {
	private String name;
	private List<String> affectedMnt;
	private List<String> fileList;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getFileList() {
		return fileList;
	}
	public void setFileList(List<String> fileList) {
		this.fileList = fileList;
	}
	public List<String> getAffectedMnt() {
		return affectedMnt;
	}
	public void setAffectedMnt(List<String> affectedMnt) {
		this.affectedMnt = affectedMnt;
	}
}

