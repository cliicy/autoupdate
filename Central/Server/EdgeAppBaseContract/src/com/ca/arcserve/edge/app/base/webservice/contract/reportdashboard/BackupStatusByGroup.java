package com.ca.arcserve.edge.app.base.webservice.contract.reportdashboard;

import java.io.Serializable;

public class BackupStatusByGroup implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int successful=0;
	private int missed=0;
	private int failed=0;
	private int noBkpJobHistory=0;
	private int cancelled=0;
	
	public int getSuccessful() {
		return successful;
	}
	public void setSuccessful(int successful) {
		this.successful = successful;
	}
	public void addSuccessfulNumber(){
		this.successful++;
	}
	
	public int getMissed() {
		return missed;
	}
	public void setMissed(int missed) {
		this.missed = missed;
	}
	public void addMissedNumber(){
		this.missed++;
	}
	
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
	public void addFailedNumber(){
		this.failed++;
	}
	
	public int getNoBkpJobHistory() {
		return noBkpJobHistory;
	}
	public void setNoBkpJobHistory(int noJobHistory) {
		this.noBkpJobHistory = noJobHistory;
	}
	public void addNoBkpJobHistoryNumber(){
		this.noBkpJobHistory++;
	}
	
	public int getCancelled() {
		return cancelled;
	}
	public void setCancelled(int cancelled) {
		this.cancelled = cancelled;
	}
	public void addCanceledNumber(){
		this.cancelled++;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public static enum NodeStatusType {
		Failed,
		Cancelled,
		Missed,
		NoJobHistory,
		Successful		
	}

}
