package com.ca.arcserve.edge.app.base.webservice.contract.destination;

import java.io.Serializable;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.ShareFolderDestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlanDestinationType;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;
/**
 * this browser data can handle both rps and shread folder; 
 *but now the browser only used to provide information to browse sharedfolder; for RPS datastore, reserve old code;
 *but when do instant vm; For both rps and shared folder; the session validation information and  adr config information are all based on this browse to fetch;  
 * @author fanda03
 *
 */
public class DestinationBrowser implements Serializable {
	public static enum RPBrowserType {
		SharedFolderUsingRPS, SharedFolderUsingLinuxServer, DataStoreUsingRPS, LocalDiskUsingD2D, LocalDiskUsingHBBUProxy, LocalDiskUsingLinuxServer;
	}
	
	private static final long serialVersionUID = 1L;
	private PlanDestinationType destinationType;
	private int destinationId;
	private String subDest;
	
	private int browserId;
	private String browserName;
	private RPBrowserType browserType;
	
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	
	public DestinationBrowser(){}
	
	public DestinationBrowser(RpsNode rpsNode, DataStoreStatusListElem datastore) {
		this.setBrowserType(RPBrowserType.DataStoreUsingRPS);
		this.setBrowserId(rpsNode.getNode_id());
		this.setBrowserName(rpsNode.getNode_name());
		this.setDestinationType(PlanDestinationType.RPS);
		this.setDestinationId(rpsNode.getNode_id());
		this.setSubDest(datastore.getDataStoreSetting().getDatastore_name());
	}
	
	public DestinationBrowser(RpsNode rps,ShareFolderDestinationInfo shareFolder) {
		this.setBrowserType(RPBrowserType.SharedFolderUsingRPS); 
		this.setBrowserId(rps.getNode_id());
		this.setBrowserName(rps.getNode_name());
		this.setDestinationType(PlanDestinationType.SharedFolder);
		this.setDestinationId(shareFolder.getDestinationId());
		this.setSubDest(null);
	}
	
	public DestinationBrowser(Node linuxServer, ShareFolderDestinationInfo shareFolder) {
		this.setBrowserType(RPBrowserType.SharedFolderUsingLinuxServer); 
		this.setBrowserId(linuxServer.getId());
		this.setBrowserName(linuxServer.getHostname());
		this.setDestinationType(PlanDestinationType.SharedFolder);
		this.setDestinationId(shareFolder.getDestinationId());
		this.setSubDest(null);
	}
	
	public DestinationBrowser(Node d2d, String localPath) {
		this.setBrowserType(RPBrowserType.LocalDiskUsingD2D); 
		this.setBrowserId(d2d.getId());
		this.setBrowserName(d2d.getHostname());
		this.setDestinationType(PlanDestinationType.LocalDisk);
		this.setDestinationId(0);
		this.setSubDest(null);
	}
	
	public int getBrowserId() {
		return browserId;
	}
	public void setBrowserId(int browserId) {
		this.browserId = browserId;
	}
	public String getBrowserName() {
		return browserName;
	}
	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}
	public RPBrowserType getBrowserType() {
		return browserType;
	}
	public void setBrowserType(RPBrowserType browserType) {
		this.browserType = browserType;
	}
	public PlanDestinationType getDestinationType() {
		return destinationType;
	}
	public void setDestinationType(PlanDestinationType destinationType) {
		this.destinationType = destinationType;
	}
	public int getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(int destinationId) {
		this.destinationId = destinationId;
	}
	public String getSubDest() {
		return subDest;
	}
	public void setSubDest(String subDest) {
		this.subDest = subDest;
	}

	public GatewayId getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(GatewayId gatewayId) {
		this.gatewayId = gatewayId;
	}
	
}
