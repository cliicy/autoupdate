package com.ca.arcserve.edge.app.base.util;

import javax.servlet.http.HttpSession;

public class SessionWrapper {
	
	private HttpSession session;
	
	public SessionWrapper(HttpSession session) {
		this.session = session;
	}
	
	public boolean isValid() {
		return session != null;
	}
	
	private <E extends Enum<E>> String getAttributeName(E attributeName) {
		return attributeName.getClass().getName() + "." + attributeName.toString();
	}
	
	public <E extends Enum<E>> Object getAttribute(E attributeName) {
		return session.getAttribute(getAttributeName(attributeName));
	}
	
	public <E extends Enum<E>> void setAttribute(E attributeName, Object value) {
		session.setAttribute(getAttributeName(attributeName), value);
	}
	
	public <E extends Enum<E>> void removeAttribute(E attributeName) {
		session.removeAttribute(getAttributeName(attributeName));
	}

}