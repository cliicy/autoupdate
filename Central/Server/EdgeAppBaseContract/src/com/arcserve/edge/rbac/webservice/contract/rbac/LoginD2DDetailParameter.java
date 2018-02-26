package com.arcserve.edge.rbac.webservice.contract.rbac;

import java.util.ArrayList;
import java.util.List;


public class LoginD2DDetailParameter {
	private String username;
	private String role;
	private List<String> permissions = new ArrayList<>();;
	private long currentTime;
	private long expired;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<String> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
	public long getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	public long getExpired() {
		return expired;
	}
	public void setExpired(long expired) {
		this.expired = expired;
	}
}
