package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;


public class ArchiveToTapeSettings implements Serializable {

	private static final long serialVersionUID = 1253577013805388789L;

	private ArchiveToTapeDestinationInfo destinationInfo = new ArchiveToTapeDestinationInfo();
	private ArchiveConfig archiveConfig = new ArchiveConfig();
	private AdvanceSchedule advanceSchedule = new AdvanceSchedule();
	private int archiveMediaUsageMode = 0;
	private String planGlobalUUID = "";
	//used for find selected task, then can find destination information for deploy
	private ArchiveToTapeSource source = new ArchiveToTapeSource();
	//used save media pool info
	private ArchiveToTapeSchedule schedule = new ArchiveToTapeSchedule();
	private ArchiveToTapeAdvance advance = new ArchiveToTapeAdvance();
	
	public ArchiveToTapeDestinationInfo getArchiveToTapeDestinationInfo(){
		return destinationInfo;
	}
	
	public void setArchiveToTapeDestinationInfo(ArchiveToTapeDestinationInfo destinationInfo){
		this.destinationInfo = destinationInfo;
	}
	
	public ArchiveConfig getArchiveConfig(){
		return archiveConfig;
	}
	
	public void setArchiveConfig(ArchiveConfig archiveConfig){
		this.archiveConfig = archiveConfig;
	}
	
	public AdvanceSchedule getAdvanceSchedule(){
		return advanceSchedule;
	}
	
	public void setAdvanceSchedule(AdvanceSchedule advanceSchedule){
		this.advanceSchedule = advanceSchedule;
	}
	
	public void setArchiveMediaUsageMode(int archiveMediaUsageMode){
		this.archiveMediaUsageMode = archiveMediaUsageMode;
	}
	
	public int getArchiveMediaUsageMode(){
		return archiveMediaUsageMode;
	}
	
	public void setPlanGlobalUUID(String planGlobalUUID){
		this.planGlobalUUID = planGlobalUUID;
	}
	
	public String getPlanGlobalUUID(){
		return planGlobalUUID;
	}

	public ArchiveToTapeSource getSource() {
		return source;
	}
	
	

	public void setSource(ArchiveToTapeSource source) {
		this.source = source;
	}

	public ArchiveToTapeSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(ArchiveToTapeSchedule schedule) {
		this.schedule = schedule;
	}

	public ArchiveToTapeAdvance getAdvance() {
		return advance;
	}

	public void setAdvance(ArchiveToTapeAdvance advance) {
		this.advance = advance;
	}
	
	
}
