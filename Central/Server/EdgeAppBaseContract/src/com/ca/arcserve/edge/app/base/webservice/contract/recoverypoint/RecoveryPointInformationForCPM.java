package com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint;

import java.io.Serializable;

import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;
/**
 * @author fanda03
 *
 */
public class RecoveryPointInformationForCPM implements Serializable {
	private static final long serialVersionUID = 1L;
	private DestinationBrowser browser;
	private ProtectedNodeInDestination protectedNode;  
	private RecoveryPoint recoveryPoint;
	private String sessionPassword;
	private boolean isBackByWindowsAgent = true;
	private boolean isWindowsSession = false; 
	public String getNodeBackupDestination() {
		return protectedNode.getDestination();
	}

	public RecoveryPointInformationForCPM() {}

	public RecoveryPointInformationForCPM(DestinationBrowser browser, ProtectedNodeInDestination protectedNodeInDS, RecoveryPoint recoveryPoint ) {
		this.protectedNode = protectedNodeInDS;
		this.recoveryPoint = recoveryPoint; 
		this.browser = browser;
	}
	public String getSessionPassword() {
		return sessionPassword;
	}
	public void setSessionPassword(String sessionPassword) {
		this.sessionPassword = sessionPassword;
	}
	public RecoveryPoint getRecoveryPoint() {
		return recoveryPoint;
	}
	public void setRecoveryPoint(RecoveryPoint recoveryPoint) {
		this.recoveryPoint = recoveryPoint;
	}
	public ProtectedNodeInDestination getProtectedNode() {
		return protectedNode;
	}
	public void setProtectedNode(ProtectedNodeInDestination protectedNode) {
		this.protectedNode = protectedNode;
	}
	@Override
	public String toString() {
		return "RecoveryPointInformationForCPM ["
				+ " browserId=" + browser.getBrowserId()
				+ ", destinationId=" + browser.getDestinationId() 
				+ ", protectedNode=" + protectedNode.getNodeId() + protectedNode.getNodeName()
				+ ", recoveryPoint=" + recoveryPoint.getBackupDest() 
				+ ", recoveryPoint session password hash=" + recoveryPoint.getEncryptPasswordHash() 				
				+ ", nodeBackupDestination=" + getNodeBackupDestination() + "]";
	}

	public boolean isBackByWindowsAgent() {
		return isBackByWindowsAgent;
	}

	public void setBackByWindowsAgent( boolean isBackByWindowsAgent ) {
		this.isBackByWindowsAgent = isBackByWindowsAgent;
	}

	public boolean isWindowsSession() {
		return isWindowsSession;
	}

	public void setWindowsSession(boolean isWindowsSession) {
		this.isWindowsSession = isWindowsSession;
	}

	public DestinationBrowser getBrowser() {
		return browser;
	}

	public void setBrowser(DestinationBrowser browser) {
		this.browser = browser;
	}
}
