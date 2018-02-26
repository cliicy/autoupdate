package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

public class SaveJobOption implements Serializable{
	private static final long serialVersionUID = -6703401155592307618L;
	private boolean appendData;
	private boolean appendJobScript;
	private boolean appendCatalogFile;
	public boolean isAppendData() {
		return appendData;
	}
	public void setAppendData(boolean appendData) {
		this.appendData = appendData;
	}
	public boolean isAppendJobScript() {
		return appendJobScript;
	}
	public void setAppendJobScript(boolean appendJobScript) {
		this.appendJobScript = appendJobScript;
	}
	public boolean isAppendCatalogFile() {
		return appendCatalogFile;
	}
	public void setAppendCatalogFile(boolean appendCatalogFile) {
		this.appendCatalogFile = appendCatalogFile;
	}
	
	
}
