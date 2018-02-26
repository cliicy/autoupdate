package com.ca.arcserve.edge.app.base.webservice.syncmonitor;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;

public class EdgeNodesMapItem {
	
	public EdgeNodesMapItem() {
		components = new HashSet<EdgeSyncComponents>();
	}
	
	/**
	 * @return the host
	 */
	public EdgeHost getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(EdgeHost host) {
		this.host = host;
	}
	/**
	 * @return the lastPingSucceedTime
	 */
	public Calendar getLastSendAlertSucceedTime() {
		return lastSendAlertSucceedTime;
	}
	/**
	 * @param lastSendAlertSucceedTime the lastPingSucceedTime to set
	 */
	public void setLastSendAlertSucceedTime(Calendar lastSendAlertSucceedTime) {
		this.lastSendAlertSucceedTime = lastSendAlertSucceedTime;
	}
	/**
	 * @return the component
	 */
	public EdgeSyncComponents getComponent(int index) {
		return (components.isEmpty() || index >= components.size()) 
			? null : (EdgeSyncComponents) (components.toArray())[index];
	}
	/**
	 * @param component the component to set
	 */
	public void setComponent(EdgeSyncComponents component) {
		this.components.add(component);
	}
	
	public Set<EdgeSyncComponents> getAllComponents() {
		return components;
	}
	
	public void clearComponents() {
		this.components.clear();
	}
	
	private EdgeHost host = null;
	private Calendar lastSendAlertSucceedTime = null;
	private Set<EdgeSyncComponents> components = null;
}
