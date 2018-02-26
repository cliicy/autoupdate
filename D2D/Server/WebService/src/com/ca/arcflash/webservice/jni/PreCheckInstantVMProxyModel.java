package com.ca.arcflash.webservice.jni;

import com.ca.arcflash.instantvm.PrecheckCriteria;

public class PreCheckInstantVMProxyModel {

	final public static long HYPERVISOR_HYPERV = 0;
	final public static long HYPERVISOR_VMWARE = 1;

	private String vmConfigPath;
	private String node_uuid;
	private long job_type;
	private long jobID;
	private long hypervisor_type;

	//By default, the highest bit value is set to check all possible conditions.
	public PrecheckCriteria precheckCriteria = new PrecheckCriteria();

	public PreCheckInstantVMProxyModel(String vmConfigPath, String node_uuid,
			long job_type, long jobID, long hypervisor_type) {
		super();
		this.vmConfigPath = vmConfigPath;
		this.node_uuid = node_uuid;
		this.job_type = job_type;
		this.jobID = jobID;
		this.hypervisor_type = hypervisor_type;
	}

	public String getVmConfigPath() {
		return vmConfigPath;
	}

	public void setVmConfigPath(String vmConfigPath) {
		this.vmConfigPath = vmConfigPath;
	}

	public String getNode_uuid() {
		return node_uuid;
	}

	public void setNode_uuid(String node_uuid) {
		this.node_uuid = node_uuid;
	}

	public long getJob_type() {
		return job_type;
	}

	public void setJob_type(long job_type) {
		this.job_type = job_type;
	}

	public long getJobID() {
		return jobID;
	}

	public void setJobID(long jobID) {
		this.jobID = jobID;
	}

	public long getHypervisor_type() {
		return hypervisor_type;
	}

	public void setHypervisor_type(long hypervisor_type) {
		this.hypervisor_type = hypervisor_type;
	}

	public int getCheckMask() {
		return precheckCriteria.getCheckMask();
	}

	public void setErrorMask(int errorMask) {
		precheckCriteria.setErrorMask(errorMask);
	}
	
	public boolean isExitsOnceError() {
		return precheckCriteria.isExitsOnceError();
	}
}
