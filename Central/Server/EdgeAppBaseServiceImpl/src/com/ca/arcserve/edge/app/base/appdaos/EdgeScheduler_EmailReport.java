package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeScheduler_EmailReport {
	private int EmailID;
	private int ReportID;
	private String ReportParam;

	public int getEmailID() {
		return EmailID;
	}

	public void setEmailID(int emailID) {
		EmailID = emailID;
	}

	public int getReportID() {
		return ReportID;
	}

	public void setReportID(int reportID) {
		ReportID = reportID;
	}

	public String getReportParam() {
		return ReportParam;
	}

	public void setReportParam(String reportParam) {
		ReportParam = reportParam;
	}
}
