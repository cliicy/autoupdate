package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

public class ArchiveToTapeDestinationInfo implements Serializable {

	private static final long serialVersionUID = 1253577013805388345L;
	private String domainName;
	private String destServerName;
	private String groupName;
	private String mediaName;
	private String mediaPoolName;
	private String mediaPoolPrefix;
	private String regularType;
	private int enableMultiplexing;
	private int maxStreamNum;
	private int groupNumber;
	private int groupType;
	private int serverId;
	
	
	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getDomainName(){
		return domainName;
	}

	public void setDomainName(String domainName){
		this.domainName = domainName;
	}
	
	public String getDestServerName(){
		return destServerName;
	}

	public void setDestServerName(String destServerName){
		this.destServerName = destServerName;
	}
	
	public String getGrpName(){
		return groupName;
	}

	public void setGrpName(String groupName){
		this.groupName = groupName;
	}
	
	public String getMediaName(){
		return mediaName;
	}

	public void setMediaName(String mediaName){
		this.mediaName = mediaName;
	}
	
	public String getMediaPoolName(){
		return mediaPoolName;
	}

	public void setMediaPoolName(String mediaPoolName){
		this.mediaPoolName = mediaPoolName;
	}
	
	public String getMediaPoolPrefixName(){
		return mediaPoolPrefix;
	}

	public void setMediaPoolPrefix(String mediaPoolPrefix){
		this.mediaPoolPrefix = mediaPoolPrefix;
	}
	
	public int getMaxStreamNum() {
		return maxStreamNum;
	}

	public void setMaxStreamNum(int maxStreamNum) {
		this.maxStreamNum = maxStreamNum;
	}

	public int getEnableMultiplexing(){
		return enableMultiplexing;
	}

	public void setEnableMultiplexing(int enableMultiplexing){
		this.enableMultiplexing = enableMultiplexing;		
	}
	
	public int getGrpNumber(){
		return groupNumber;
	}

	public void setGrpNumber(int groupNumber){
		this.groupNumber = groupNumber;		
	}
	
	public int getMediaGroupType(){
		return groupType;
	}

	public void setMediaGroupType(int groupType){
		this.groupType = groupType;
	}

	public String getRegularType() {
		return regularType;
	}

	public void setRegularType(String regularType) {
		this.regularType = regularType;
	}	
	
}
