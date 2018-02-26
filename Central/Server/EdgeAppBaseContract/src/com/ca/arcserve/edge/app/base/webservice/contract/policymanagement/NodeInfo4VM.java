package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;

public class NodeInfo4VM implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int nodeId;
	private String nodeName;
	private String nodeDisplayName;
    private String hypervisor;
    private String vMName;
    private String userName;
    private Boolean verified;
    private @NotPrintAttribute String password;
	
	public int getNodeId()
	{
		return nodeId;
	}
	
	public void setNodeId( int nodeId )
	{
		this.nodeId = nodeId;
	}
	
	public String getNodeName()
	{
		return nodeName;
	}
	
	public void setNodeName( String nodeName )
	{
		this.nodeName = nodeName;
	}
	
	public String getNodeDisplayName()
	{
		return nodeDisplayName;
	}
	
	public void setNodeDisplayName( String nodeDisplayName )
	{
		this.nodeDisplayName = nodeDisplayName;
	}

	public String getHypervisor() {
		return hypervisor;
	}

	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}

	public String getvMName() {
		return vMName;
	}

	public void setvMName(String vMName) {
		this.vMName = vMName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
