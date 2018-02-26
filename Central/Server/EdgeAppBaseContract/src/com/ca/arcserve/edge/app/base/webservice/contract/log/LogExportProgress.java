package com.ca.arcserve.edge.app.base.webservice.contract.log;

import java.io.Serializable;

public class LogExportProgress implements Serializable {

	private static final long serialVersionUID = 1L;

	private int exportProgress;
	private LogExportStatus exportStatus;

	private String errorMsg;
	private String exportFileName;
	
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getExportFileName() {
		return exportFileName;
	}
	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}
	public int getExportProgress() {
		return exportProgress;
	}
	public void setExportProgress(int exportProgress) {
		this.exportProgress = exportProgress;
	}
	public LogExportStatus getExportStatus() {
		return exportStatus;
	}
	public void setExportStatus(LogExportStatus exportStatus) {
		this.exportStatus = exportStatus;
	}
	
	public static enum LogExportStatus {
		Initial(0),
		FetchDB(1),
		GenerateFile(2),
		SUCCESS(3),
		FAIL(4);
		private int value;
		private LogExportStatus(int value) {
			this.value = value;
		}
		public int getValue() {
			return this.value;
		}

	}
}
