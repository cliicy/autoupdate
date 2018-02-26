package com.ca.arcflash.webservice.jni.model;

public class JJobScriptADItem {
	
	public final static String attrSeparator =";";
	
	private long id;
	private boolean  allChild;    // whether restore all the child items
	private boolean  allAttribute;     // whether restore all the attributes
	private long attrNumber;     // the attribute number
	private String attrNames; // the attributes needed to be restored, each ATTr is separated by ';' For instance: "cn;objectClass;displayname;"
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isAllChild() {
		return allChild;
	}
	public void setAllChild(boolean allChild) {
		this.allChild = allChild;
	}
	public boolean isAllAttribute() {
		return allAttribute;
	}
	public void setAllAttribute(boolean allAttribute) {
		this.allAttribute = allAttribute;
	}
	public long getAttrNumber() {
		return attrNumber;
	}
	public void setAttrNumber(long attrNumber) {
		this.attrNumber = attrNumber;
	}
	public String getAttrNames() {
		return attrNames;
	}
	public void setAttrNames(String attrNames) {
		this.attrNames = attrNames;
	}
	
}
