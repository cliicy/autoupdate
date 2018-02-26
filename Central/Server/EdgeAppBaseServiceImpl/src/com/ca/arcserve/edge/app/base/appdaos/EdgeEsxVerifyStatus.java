package com.ca.arcserve.edge.app.base.appdaos;


public class EdgeEsxVerifyStatus {
	
	private int hostId;
	private int status;
	private String detail;
	
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
}
