/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;

/**
 * @author lvwch01
 * 
 */
public class RPSSourceNode implements Serializable {
	
	private static final long serialVersionUID = -6223324799399327320L;
	
	private String datastoreName;
	private String datastoreUUID;
	private String desPassword;
	private String destination;
	private String desUsername;
	private String hostname;
	private String hostUUID;
	private String policyUUID;
	private boolean isVM;
	private String loginUUID;//if the node is vm then login UUID is proxy UUID , if the node is not VM , loginUUID = null, 
	private RPSConverterNode converterNode;
	private boolean isReplicationNode;
	private Protocol hostProtocal;
	private int hostPort;
	public boolean isReplicationNode() {
		return isReplicationNode;
	}
	public void setReplicationNode(boolean isReplicationNode) {
		this.isReplicationNode = isReplicationNode;
	}
	public boolean isVM() {
		return isVM;
	}
	public void setVM(boolean isVM) {
		this.isVM = isVM;
	}
	public String getLoginUUID() {
		return loginUUID;
	}
	public void setLoginUUID(String loginUUID) {
		this.loginUUID = loginUUID;
	}
	public String getDatastoreName() {
		return datastoreName;
	}
	public void setDatastoreName(String datastoreName) {
		this.datastoreName = datastoreName;
	}
	public String getDatastoreUUID() {
		return datastoreUUID;
	}
	public void setDatastoreUUID(String datastoreUUID) {
		this.datastoreUUID = datastoreUUID;
	}
	public String getDesPassword() {
		return desPassword;
	}
	public void setDesPassword(String desPassword) {
		this.desPassword = desPassword;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getDesUsername() {
		return desUsername;
	}
	public void setDesUsername(String desUsername) {
		this.desUsername = desUsername;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getHostUUID() {
		return hostUUID;
	}
	public void setHostUUID(String hostUUID) {
		this.hostUUID = hostUUID;
	}
	public String getPolicyUUID() {
		return policyUUID;
	}
	public void setPolicyUUID(String policyUUID) {
		this.policyUUID = policyUUID;
	}
	public RPSConverterNode getConverterNode() {
		return converterNode;
	}
	public void setConverterNode(RPSConverterNode converterNode) {
		this.converterNode = converterNode;
	}
	public Protocol getHostProtocal() {
		return hostProtocal;
	}
	public void setHostProtocal(Protocol hostProtocal) {
		this.hostProtocal = hostProtocal;
	}
	public int getHostPort() {
		return hostPort;
	}
	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}
}
