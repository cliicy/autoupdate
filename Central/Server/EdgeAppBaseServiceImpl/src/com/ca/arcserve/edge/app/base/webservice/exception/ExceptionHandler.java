package com.ca.arcserve.edge.app.base.webservice.exception;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public abstract class ExceptionHandler {
	
	private ExceptionHandler nextHandler;
	
	public ExceptionHandler() {
		this(null);
	}
	
	public ExceptionHandler(ExceptionHandler nextHandler) {
		this.nextHandler = nextHandler;
	}
	
	public boolean handleException(Throwable exception) throws EdgeServiceFault {
		if (handleTargetException(exception)) {
			return true;
		} else if (nextHandler != null) {
			return nextHandler.handleException(exception);
		} else {
			return false;
		}
	}
	
	protected abstract boolean handleTargetException(Throwable exception) throws EdgeServiceFault;
	
	public void add(ExceptionHandler handler) {
		if (nextHandler == null) {
			nextHandler = handler;
		} else {
			nextHandler.add(handler);
		}
	}
	
}
