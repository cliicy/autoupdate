package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;

/**
 * Protected Resource can be a node also can be a group, may also be others in the future.
 * @author zhaji22
 *
 */
public class ProtectedResource implements Serializable{
	private static final long serialVersionUID = 1L;
	private ProtectedResourceIdentifier identifier;
	private String name;
	private String vmName;
	private String planName;
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	private String hypervisor;
	private String description;
	private String siteName;
	
	public ProtectedResourceIdentifier getIdentifier() {
		return identifier;
	}
	public void setIdentifier(ProtectedResourceIdentifier identifier) {
		this.identifier = identifier;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public String getHypervisor() {
		return hypervisor;
	}
	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
}
