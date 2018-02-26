package com.ca.arcflash.webservice.jni.model;

import java.util.List;

public class JJobScriptBackupVol {
	private String pwszVolName;
	private int ulFileSystem;
	private int ulSessionMethod;
	private int nVolItemAppComp;
	private List<JJobScriptVolAppItem> pVolItemAppCompList;
	private int fOptions;
	public String getPwszVolName() {
		return pwszVolName;
	}
	public void setPwszVolName(String pwszVolName) {
		this.pwszVolName = pwszVolName;
	}
	public int getUlFileSystem() {
		return ulFileSystem;
	}
	public void setUlFileSystem(int ulFileSystem) {
		this.ulFileSystem = ulFileSystem;
	}
	public int getUlSessionMethod() {
		return ulSessionMethod;
	}
	public void setUlSessionMethod(int ulSessionMethod) {
		this.ulSessionMethod = ulSessionMethod;
	}
	public int getNVolItemAppComp() {
		return nVolItemAppComp;
	}
	public void setNVolItemAppComp(int volItemAppComp) {
		nVolItemAppComp = volItemAppComp;
	}
	public List<JJobScriptVolAppItem> getPVolItemAppCompList() {
		return pVolItemAppCompList;
	}
	public void setPVolItemAppCompList(List<JJobScriptVolAppItem> volItemAppCompList) {
		pVolItemAppCompList = volItemAppCompList;
	}
	public int getFOptions() {
		return fOptions;
	}
	public void setFOptions(int options) {
		fOptions = options;
	}
	
}
