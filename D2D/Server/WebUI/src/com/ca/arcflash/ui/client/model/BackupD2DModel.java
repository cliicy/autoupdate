package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupD2DModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4956711438951030179L;

	
	public void setHostName(String hostname){
		set("hostname",hostname);
	}
	
	public String getHostName(){
		return get("hostname");
	}
	
	public void setDestination(String destination){
		set("destination",destination);
	}

	public String getDestination(){
		return get("destination");
	}
	
	public void setDesPassword(String desPassword){
		set("desPassword",desPassword);
	}
	
	public String getDesPassword(){
		return get("desPassword");
	}
	
	public void setDesUsername(String desUsername){
		set("desUsername",desUsername);
	}
	
	public String getDesUsername(){
		return get("desUsername");
	}
	
	public void setDataStoreName(String dataStoreName){
		set("dataStoreName",dataStoreName);
	}
	
	public String getDataStoreName(){
		return get("dataStoreName");
	}
	
	public void setDataStoreUUID(String dataStoreUUID){
		set("dataStoreUUID",dataStoreUUID);
	}
	
	public String getDataStoreUUID(){
		return get("dataStoreUUID");
	}
	
	public void setRpsPolicyUUID(String rpsPolicyUUID){
		set("policyUUID", rpsPolicyUUID);
	}
	
	public String getRpsPolicyUUID(){
		return (String)get("policyUUID");
	}
	

	public void setAgentSID(String agentSID){
		set("agentSID", agentSID);
	}
	
	public String getAgentSID(){
		return (String)get("agentSID");
	}
	
	public void setAgentUUID(String agentUUID){
		set("agentUUID", agentUUID);
	}
	
	public String getAgentUUID(){
		return (String)get("agentUUID");
	}

	public String getUsername() {
		return (String)get("username");
	}

	public void setUsername(String username) {
		set("username", username);
	}

	public String getDestPlanName() {
		return (String)get("destPlanName");
	}

	public void setDestPlanName(String destPlanName) {
		set("destPlanName", destPlanName);
	}

	public String getSourceRPSServerName() {
		return (String)get("sourceRPSServerName");
	}

	public void setSourceRPSServerName(String sourceRPSServerName) {
		set("sourceRPSServerName", sourceRPSServerName);
	}
	
	
}
