package com.ca.arcflash.webservice.jni.model;

import java.util.List;

public class JJobScriptVolAppItem {
	private String pwszFileorDir;
	private int fOptions;
	private String pwszCompRestPath;
	private String pwszCompRestName;
	private long   nExchSubItemList;			              // For Exchange GRT restore, subitem count.
	private List<JJobScriptExchSubItem>	pExchSubItemList; // For Exchange GRT restore, subitem list.
	private long   uADItemNum;
	private List<JJobScriptADItem> pADItemList;  //For AD restore
	
	public JJobScriptVolAppItem() {
		super();
	}
	public JJobScriptVolAppItem(String pwszFileorDir, int options) {
		super();
		this.pwszFileorDir = pwszFileorDir;
		fOptions = options;
	}
	public JJobScriptVolAppItem(String pwszFileorDir, int options, String pwszCompRestPath, String pwszCompRestName) {
		super();
		this.pwszFileorDir = pwszFileorDir;
		fOptions = options;
		this.pwszCompRestPath = pwszCompRestPath;
		this.pwszCompRestName = pwszCompRestName;
	}
	public String getPwszFileorDir() {
		return pwszFileorDir;
	}
	public void setPwszFileorDir(String pwszFileorDir) {
		this.pwszFileorDir = pwszFileorDir;
	}
	public int getFOptions() {
		return fOptions;
	}
	public void setFOptions(int options) {
		fOptions = options;
	}
	public String getPwszCompRestPath() {
		return pwszCompRestPath;
	}
	public void setPwszCompRestPath(String pwszCompRestPath) {
		this.pwszCompRestPath = pwszCompRestPath;
	}
	public String getPwszCompRestName() {
		return pwszCompRestName;
	}
	public void setPwszCompRestName(String pwszCompRestName) {
		this.pwszCompRestName = pwszCompRestName;
	}
	
	public long getnExchSubItemList() {
		return nExchSubItemList;
	}
	public void setnExchSubItemList(long nExchSubItemList) {
		this.nExchSubItemList = nExchSubItemList;
	}
	
	public List<JJobScriptExchSubItem> getpExchSubItemList() {
		return pExchSubItemList;
	}
	public void setpExchSubItemList(List<JJobScriptExchSubItem> pExchSubItemList) {
		this.pExchSubItemList = pExchSubItemList;
	}
	public long getuADItemNum() {
		return uADItemNum;
	}
	public void setuADItemNum(long uADItemNum) {
		this.uADItemNum = uADItemNum;
	}
	public List<JJobScriptADItem> getpADItemList() {
		return pADItemList;
	}
	public void setpADItemList(List<JJobScriptADItem> pADItemList) {
		this.pADItemList = pADItemList;
	}
	
}
