package com.ca.arcflash.webservice.jni.model;

public class JIVMJobStatus {
	private String ivmJobUUID;
	private int jobPhase;
	private String vmDisplayName;
	private boolean isAgentExist;
	
	public JIVMJobStatus(String ivmJobUUID) {
		this.ivmJobUUID = ivmJobUUID;
	}
	
	public String getIvmJobUUID() {
		return ivmJobUUID;
	}
	public void setIvmJobUUID(String ivmJobUUID) {
		this.ivmJobUUID = ivmJobUUID;
	}
	
	public int getJobPhase() {
		return jobPhase;
	}
	public void setJobPhase(int jobPhase) {
		this.jobPhase = jobPhase;
	}

	public String getVmDisplayName() {
		return vmDisplayName;
	}

	public void setVmDisplayName(String vmDisplayName) {
		this.vmDisplayName = vmDisplayName;
	}

	public boolean isAgentExist() {
		return isAgentExist;
	}

	public void setAgentExist(boolean isAgentExist) {
		this.isAgentExist = isAgentExist;
	}

}
