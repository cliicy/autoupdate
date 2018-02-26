package com.ca.arcserve.edge.app.base.webservice.common;
public class DeadLockInfo {
	private String blockingSpid;
	private String blockedSpid;
	private String blockingText;
	private String blockedText;
	public String getBlockingSpid() {
		return blockingSpid;
	}
	public void setBlockingSpid(String blockingSpid) {
		this.blockingSpid = blockingSpid;
	}
	public String getBlockedSpid() {
		return blockedSpid;
	}
	public void setBlockedSpid(String blockedSpid) {
		this.blockedSpid = blockedSpid;
	}
	public String getBlockingText() {
		return blockingText;
	}
	public void setBlockingText(String blockingText) {
		this.blockingText = blockingText;
	}
	public String getBlockedText() {
		return blockedText;
	}
	public void setBlockedText(String blockedText) {
		this.blockedText = blockedText;
	}
}
