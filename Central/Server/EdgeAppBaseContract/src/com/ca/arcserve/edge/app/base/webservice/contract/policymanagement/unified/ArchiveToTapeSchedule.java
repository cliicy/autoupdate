package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaPoolSet;

public class ArchiveToTapeSchedule implements Serializable{
	private static final long serialVersionUID = 8701075820845685912L;
	private ASBUMediaPoolSet mediaPoolSet;
	private boolean sharedMediaPool;
	
	
	public ASBUMediaPoolSet getMediaPoolSet() {
		return mediaPoolSet;
	}
	public void setMediaPoolSet(ASBUMediaPoolSet mediaPoolSet) {
		this.mediaPoolSet = mediaPoolSet;
	}
	public boolean isSharedMediaPool() {
		return sharedMediaPool;
	}
	public void setSharedMediaPool(boolean sharedMediaPool) {
		this.sharedMediaPool = sharedMediaPool;
	}
}
