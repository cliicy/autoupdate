package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

public class StopInstantVHDOperation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String jobUUID;

	public String getJobUUID() {
		return jobUUID;
	}

	public void setJobUUID(String jobUUID) {
		this.jobUUID = jobUUID;
	}
	
	private String proxyNameOrIP;
	public String getProxyNameOrIP() {
		return proxyNameOrIP;
	}
	public void setProxyNameOrIP(String proxyNameOrIP) {
		this.proxyNameOrIP = proxyNameOrIP;
	}
}
