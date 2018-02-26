package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class DiscoveryMonitor implements Serializable{

	public static class DiscoverServerError implements Serializable, BeanModelTag{
		
		private static final long serialVersionUID = 1L;
		private String errorMsg;
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getErrorMsg() {
			return errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
	}
	
	private static final long serialVersionUID = -523938927546947589L;
	private long elapsedTime;
	private long processedNodeNum;
	private String currentProcessNodeName;
	private DiscoveryPhase discoveryPhase;
	private DiscoveryStatus discoveryStatus;
	private String errorCode;
	private String uuid;
	private List<DiscoverServerError> serverErrors = new ArrayList<DiscoverServerError>();
	private DiscoveryOption option;
	
	public DiscoveryStatus getDiscoveryStatus() {
		return discoveryStatus;
	}
	public void setDiscoveryStatus(DiscoveryStatus discoveryStatus) {
		this.discoveryStatus = discoveryStatus;
	}
	public long getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	public long getProcessedNodeNum() {
		return processedNodeNum;
	}
	public void setProcessedNodeNum(long processedNodeNum) {
		this.processedNodeNum = processedNodeNum;
	}
	public void setCurrentProcessNodeName(String currentProcessNodeName) {
		this.currentProcessNodeName = currentProcessNodeName;
	}
	public String getCurrentProcessNodeName() {
		return currentProcessNodeName;
	}
	public void setDiscoveryPhase(DiscoveryPhase discoveryPhase) {
		this.discoveryPhase = discoveryPhase;
	}
	public DiscoveryPhase getDiscoveryPhase() {
		return discoveryPhase;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public List<DiscoverServerError> getServerErrors() {
		return serverErrors;
	}
	public void setServerErrors(List<DiscoverServerError> _serverErrors) {
		this.serverErrors = _serverErrors ;
	}
	public void setServerError(DiscoverServerError serverError) {
		this.serverErrors.add(serverError);
	}
	public DiscoveryOption getOption() {
		return option;
	}
	public void setOption(DiscoveryOption option) {
		this.option = option;
	}
	
	
}
