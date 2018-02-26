package com.ca.arcflash.webservice.scheduler;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class MakeUp {	
	private HashMap<Long, ConfilctData> conflictTimeAndFlag = new HashMap<Long, ConfilctData>();	
//	private long conflictTime = 0;
//	private int conflictFlag = 0;	
//
//	public int getConflictFlag() {
//		return conflictFlag;
//	}
//	public void setConflictFlag(int conflictFlag) {
//		this.conflictFlag = conflictFlag;
//	}
//	public long getConflictTime() {
//		return conflictTime;
//	}
//	public void setConflictTime(long conflictTime) {
//		this.conflictTime = conflictTime;
//	}
	@XmlJavaTypeAdapter(MakeupXmlAdapter.class)
	public HashMap<Long, ConfilctData> getConflictTimeAndFlag() {
		return conflictTimeAndFlag;
	}

	public void setConflictTimeAndFlag(HashMap<Long, ConfilctData> conflictTimeAndFlag) {
		this.conflictTimeAndFlag = conflictTimeAndFlag;
	}
	
}
