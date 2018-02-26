/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lijwe02
 * 
 */
public class RHAScenarioData {
	private long scenarioID;
	private String scenarioName;
	private boolean d2dIntegrated;
	private String d2dName;
	private boolean cntrlAppIntegrated;
	private List<RHAMonitorBackupVM> monitorBackupVMList = null;
	private List<RHAReplNode> replicationTree = null;

	public long getScenarioID() {
		return scenarioID;
	}

	public void setScenarioID(long scenarioID) {
		this.scenarioID = scenarioID;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public boolean isD2dIntegrated() {
		return d2dIntegrated;
	}

	public void setD2dIntegrated(boolean d2dIntegrated) {
		this.d2dIntegrated = d2dIntegrated;
	}

	public String getD2dName() {
		return d2dName;
	}

	public void setD2dName(String d2dName) {
		this.d2dName = d2dName;
	}

	public boolean isCntrlAppIntegrated() {
		return cntrlAppIntegrated;
	}

	public void setCntrlAppIntegrated(boolean cntrlAppIntegrated) {
		this.cntrlAppIntegrated = cntrlAppIntegrated;
	}

	public List<RHAMonitorBackupVM> getMonitorBackupVMList() {
		return monitorBackupVMList;
	}

	public void setMonitorBackupVMList(List<RHAMonitorBackupVM> monitorBackupVMList) {
		this.monitorBackupVMList = monitorBackupVMList;
	}

	public List<RHAReplNode> getReplicationTree() {
		return replicationTree;
	}

	public void addReplNode(RHAReplNode replNode) {
		if (replNode == null) {
			return;
		}
		if (replicationTree == null) {
			replicationTree = new ArrayList<RHAReplNode>();
		}
		replicationTree.add(replNode);
	}
}
