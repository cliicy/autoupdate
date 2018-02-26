package com.ca.arcserve.edge.app.base.webservice.contract.destination.cloudaccount;

import java.util.Date;

public class ASCloudAccount {
	private int id;
	private String accountName;
	private int cloudType;
	private int cloudSubType;
	private String details;
	private Date creationTime;
	private Date modificationTime;
	private String name; //this is site or gateway name
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public int getCloudType() {
		return cloudType;
	}
	public void setCloudType(int cloudType) {
		this.cloudType = cloudType;
	}
	public int getCloudSubType() {
		return cloudSubType;
	}
	public void setCloudSubType(int cloudSubType) {
		this.cloudSubType = cloudSubType;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	public Date getModificationTime() {
		return modificationTime;
	}
	public void setModificationTime(Date modificationTime) {
		this.modificationTime = modificationTime;
	}
	
	
	

}
