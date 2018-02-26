package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;
import java.io.Serializable;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ArcserveConnectInfo;

public class ASBUServerInfo implements Serializable{
	private static final long serialVersionUID = -2245868331384878471L;
	private String hostName;
	private String displayName;
	private ASBUServerClass serverClass;
	private int serverId;
	private int domainId;
	private String domainName;
	private ArcserveConnectInfo connectInfo;
	private List<ASBUMediaGroupInfo> groups;
	private int planCount;
	private ASBUServerStatus status;
	// siteName, node belongs to which site/gateway
	private String siteName;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public ASBUServerClass getServerClass() {
		return serverClass;
	}

	public void setServerClass(ASBUServerClass serverClass) {
		this.serverClass = serverClass;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getDomainId() {
		return domainId;
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}
	
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public ArcserveConnectInfo getConnectInfo() {
		return connectInfo;
	}

	public void setConnectInfo(ArcserveConnectInfo connectInfo) {
		this.connectInfo = connectInfo;
	}

	public List<ASBUMediaGroupInfo> getGroups() {
		return groups;
	}

	public void setGroups(List<ASBUMediaGroupInfo> groups) {
		this.groups = groups;
	}
	
	public String getUsername() {
		return connectInfo.getCauser();
	}

	public void setUsername(String username) {
		this.connectInfo.setCauser(username);
	}

	public String getPassword() {
		return connectInfo.getCapasswd();
	}

	public void setPassword(String password) {
		this.connectInfo.setCapasswd(password);
	}

	public int getPlanCount() {
		return planCount;
	}

	public void setPlanCount(int planCount) {
		this.planCount = planCount;
	}

	public ASBUServerStatus getStatus() {
		return status;
	}

	public void setStatus(ASBUServerStatus status) {
		this.status = status;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
