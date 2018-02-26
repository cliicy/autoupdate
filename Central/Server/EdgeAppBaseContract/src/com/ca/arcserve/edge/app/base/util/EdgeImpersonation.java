package com.ca.arcserve.edge.app.base.util;

import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;

public class EdgeImpersonation {
	
	private static EdgeImpersonation instance = new EdgeImpersonation();
	
	// Error code defined in WinError.h
	private static final int ERROR_SUCCESS = 0;
	private static final int ERROR_LOGON_FAILURE = 1326;
	
	private EdgeAccount lastEdgeAccount;
	private int lastErrorCode = ERROR_SUCCESS;	// serialize the last error code if necessary
	
	private EdgeImpersonation() {
	}
	
	public static EdgeImpersonation getInstance() {
		return instance;
	}
	
	public String getLastUsername() {
		if (lastEdgeAccount == null) {
			return "";
		}
		
		if (lastEdgeAccount.getDomain() == null || lastEdgeAccount.getDomain().isEmpty()) {
			return lastEdgeAccount.getUserName();
		} else {
			return lastEdgeAccount.getDomain() + "\\" + lastEdgeAccount.getUserName();
		}
	}
	
	public synchronized int impersonate() {
		EdgeAccount edgeAccount = new EdgeAccount();
		int result = BaseWSJNI.getEdgeAccount(edgeAccount);
		if (result != ERROR_SUCCESS) {
			System.err.println("EdgeImpersonation.impersonate - get edge account failed, error code = " + result);
			return result;
		}
		
		return impersonate(edgeAccount);
	}
	
	public synchronized int impersonate(String username, String domain, String password) {
		EdgeAccount edgeAccount = new EdgeAccount();
		edgeAccount.setUserName(username);
		edgeAccount.setDomain(domain);
		edgeAccount.setPassword(password);
		
		return impersonate(edgeAccount);
	}
	
	private int impersonate(EdgeAccount edgeAccount) {
		boolean edgeAccountChanged = isEdgeAccountChanged(edgeAccount);
		lastEdgeAccount = edgeAccount;
		
		if (!edgeAccountChanged && lastErrorCode == ERROR_LOGON_FAILURE) {
			return ERROR_LOGON_FAILURE;
		}
		
		lastErrorCode = BaseWSJNI.impersonateAccount(edgeAccount.getUserName(), edgeAccount.getDomain(), edgeAccount.getPassword());
		if (lastErrorCode != ERROR_SUCCESS) {
			System.err.println("EdgeImpersonation.impersonate - impersonate edge account failed, error code = " + lastErrorCode);
		}
		
		return lastErrorCode;
	}
	
	private boolean isEdgeAccountChanged(EdgeAccount edgeAccount) {
		if (lastEdgeAccount == null) {
			return false;
		}
		
		return !stringEquals(lastEdgeAccount.getDomain(), edgeAccount.getDomain(), true)
				|| !stringEquals(lastEdgeAccount.getUserName(), edgeAccount.getUserName(), true)
				|| !stringEquals(lastEdgeAccount.getPassword(), edgeAccount.getPassword(), false);
	}
	
	private boolean stringEquals(String s1, String s2, boolean ignoreCase) {
		if (s1 == null) {
			return s2 == null;
		} else {
			return ignoreCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2);
		}
	}
	
}
