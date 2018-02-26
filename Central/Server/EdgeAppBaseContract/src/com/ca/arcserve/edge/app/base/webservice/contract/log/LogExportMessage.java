package com.ca.arcserve.edge.app.base.webservice.contract.log;

import java.io.Serializable;

public class LogExportMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private MessageType type;
	private boolean requestCancel = false;
	private LogExportProgress progress;
	
	public MessageType getType() {
		return type;
	}
	public void setType(MessageType type) {
		this.type = type;
	}
	public boolean isRequestCancel() {
		return requestCancel;
	}
	public void setRequestCancel(boolean requestCancel) {
		this.requestCancel = requestCancel;
	}
	public LogExportProgress getProgress() {
		return progress;
	}
	public void setProgress(LogExportProgress progress) {
		this.progress = progress;
	}
	
	public static enum MessageType {  
		REQUEST, RESPONSE;
	}
}
