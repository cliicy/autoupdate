package com.ca.arcflash.webservice.scheduler;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlRootElement(name = "baseJobSerilizeContext")
public class BaseJobSerilizeContext implements Serializable {

	private static final long serialVersionUID = -256271064716007259L;
	protected String rpsPolicyUUID = null;
	protected String planUUID = null;
	protected String rpsDataStoreUUID = null;
	protected String rpsDataStoreName = null;
	public String getRpsPolicyUUID() {
		return rpsPolicyUUID;
	}
	public void setRpsPolicyUUID(String rpsPolicyUUID) {
		this.rpsPolicyUUID = rpsPolicyUUID;
	}
	public String getPlanUUID() {
		return planUUID;
	}
	public void setPlanUUID(String planUUID) {
		this.planUUID = planUUID;
	}
	public String getRpsDataStoreUUID() {
		return rpsDataStoreUUID;
	}
	public void setRpsDataStoreUUID(String rpsDataStoreUUID) {
		this.rpsDataStoreUUID = rpsDataStoreUUID;
	}
	public String getRpsDataStoreName() {
		return rpsDataStoreName;
	}
	public void setRpsDataStoreName(String rpsDataStoreName) {
		this.rpsDataStoreName = rpsDataStoreName;
	}
}
