package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class OndemandInfo4RPS extends BaseModelData{

	private static final long serialVersionUID = -2438671287732325984L;

	public List<RecoveryPointModel> sessions;
	
	public RpsHostModel rpsHostInfo;
	
	private String dest;
	
	private String dataStoreName;	
	private String dataStoreUUID;
	
	private String destUserName;
	private String destPassword;
	
	private String agentName;
	private String agentUUID;
	private String agentSID;

	private String vmInstanceUUID;
	
	
	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getDataStoreName() {
		return dataStoreName;
	}

	public void setDataStoreName(String dataStoreName) {
		this.dataStoreName = dataStoreName;
	}

	public String getDataStoreUUID() {
		return dataStoreUUID;
	}

	public void setDataStoreUUID(String dataStoreUUID) {
		this.dataStoreUUID = dataStoreUUID;
	}

	public String getDestUserName() {
		return destUserName;
	}

	public void setDestUserName(String destUserName) {
		this.destUserName = destUserName;
	}

	public String getDestPassword() {
		return destPassword;
	}

	public void setDestPassword(String destPassword) {
		this.destPassword = destPassword;
	}

	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}

	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}

	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentUUID() {
		return agentUUID;
	}
	public void setAgentUUID(String agentUUID) {
		this.agentUUID = agentUUID;
	}

	public String getAgentSID() {
		return agentSID;
	}

	public void setAgentSID(String agentSID) {
		this.agentSID = agentSID;
	}
}
