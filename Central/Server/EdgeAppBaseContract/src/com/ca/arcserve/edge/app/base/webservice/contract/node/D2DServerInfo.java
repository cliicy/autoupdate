package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;

public class D2DServerInfo implements Serializable {

	private static final long serialVersionUID = 9003895416943999638L;
	
	private int id;
	private String name;
	private String uuid;
	private int port;
	private Protocol protocol;
	private int[] groupIds;
	private D2DBackupType backupType;
	private boolean sqlServerInstalled;
	private boolean exchangeInstalled;
	private boolean vmInstalled;
	private boolean D2DInstalled;
	private boolean D2DODInstalled;
	private int isVisible;
	private NodeManagedStatus managedStatus;
	private List<NodeGroup> restoreNodeGroup;
	
	public boolean isD2DInstalled() {
		return D2DInstalled;
	}
	public void setD2DInstalled(boolean d2dInstalled) {
		D2DInstalled = d2dInstalled;
	}
	public boolean isD2DODInstalled() {
		return D2DODInstalled;
	}
	public void setD2DODInstalled(boolean d2dodInstalled) {
		D2DODInstalled = d2dodInstalled;
	}
	public List<NodeGroup> getRestoreNodeGroup() {
		return restoreNodeGroup;
	}
	public void setRestoreNodeGroup(List<NodeGroup> restoreNodeGroup) {
		this.restoreNodeGroup = restoreNodeGroup;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public Protocol getProtocol() {
		return protocol;
	}
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	public int[] getGroupIds() {
		return groupIds;
	}
	public void setGroupIds(int[] groupIds) {
		this.groupIds = groupIds;
	}
	public D2DBackupType getBackupType() {
		return backupType;
	}
	public void setBackupType(D2DBackupType backupType) {
		this.backupType = backupType;
	}
	public boolean isSqlServerInstalled() {
		return sqlServerInstalled;
	}
	public void setSqlServerInstalled(boolean sqlServerInstalled) {
		this.sqlServerInstalled = sqlServerInstalled;
	}
	public boolean isExchangeInstalled() {
		return exchangeInstalled;
	}
	public void setExchangeInstalled(boolean exchangeInstalled) {
		this.exchangeInstalled = exchangeInstalled;
	}
	public boolean isVmInstalled() {
		return vmInstalled;
	}
	public void setVmInstalled(boolean vmInstalled) {
		this.vmInstalled = vmInstalled;
	}
	public void setManagedStatus(NodeManagedStatus managedStatus) {
		this.managedStatus = managedStatus;
	}
	public NodeManagedStatus getManagedStatus() {
		return managedStatus;
	}
	public int getIsVisible() {
		return isVisible;
	}
	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}

}
