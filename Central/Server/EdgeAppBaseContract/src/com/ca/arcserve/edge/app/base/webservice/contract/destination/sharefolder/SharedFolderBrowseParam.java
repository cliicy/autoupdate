package com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;

public class SharedFolderBrowseParam implements Serializable{
	private static final long serialVersionUID = 1L;
	private DestinationBrowser browserInfo;
	private ProtectedNodeInDestination currentNode;
	
	public SharedFolderBrowseParam(){}
	public SharedFolderBrowseParam(DestinationBrowser browserInfo){
		this.browserInfo = browserInfo;
	}
	public SharedFolderBrowseParam(DestinationBrowser browser, ProtectedNodeInDestination node) {
		this.browserInfo = browser;
		this.currentNode = node;
	}
	
	public DestinationBrowser getBrowserInfo() {
		return browserInfo;
	}
	public void setBrowserInfo(DestinationBrowser browserInfo) {
		this.browserInfo = browserInfo;
	}
	public ProtectedNodeInDestination getCurrentNode() {
		return currentNode;
	}
	public void setCurrentNode(ProtectedNodeInDestination currentNode) {
		this.currentNode = currentNode;
	}



}
