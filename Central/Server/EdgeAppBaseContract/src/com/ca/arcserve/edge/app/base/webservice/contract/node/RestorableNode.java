package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.List;

public class RestorableNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3791441929123154612L;
	private String name;
	private int[] groupIds;
	private boolean sqlServerInstalled;
	private boolean exchangeInstalled;
	private boolean D2DInstalled;
	private boolean D2DODInstalled;
	private boolean vmInstalled;
	private RestorableNodeConnection connect;
	private D2DBackupType backupType;
	private NodeManagedStatus managedStatus;
	private String vmInstanceUUID;
	private List<NodeGroup> restoreNodeGroup;
	private String vmName;
	private String hyperVisor;
	
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
	
	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
	public String getHyperVisor() {
		return hyperVisor;
	}

	public void setHyperVisor(String hyperVisor) {
		this.hyperVisor = hyperVisor;
	}

	public List<NodeGroup> getRestoreNodeGroup() {
		return restoreNodeGroup;
	}

	public void setRestoreNodeGroup(List<NodeGroup> restoreNodeGroup) {
		this.restoreNodeGroup = restoreNodeGroup;
	}

	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}

	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}

	public NodeManagedStatus getManagedStatus() {
		return managedStatus;
	}

	public void setManagedStatus(NodeManagedStatus managedStatus) {
		this.managedStatus = managedStatus;
	}

	public D2DBackupType getBackupType() {
		return backupType;
	}

	public void setBackupType(D2DBackupType backupType) {
		this.backupType = backupType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(int[] groupIds) {
		this.groupIds = groupIds;
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

	public RestorableNodeConnection getConnect() {
		return connect;
	}

	public void setConnect(RestorableNodeConnection connect) {
		this.connect = connect;
	}
}
