package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;


public enum MediaPoolType {
	UDP_ARCHIVE_JOB_TIMEUNIT_MINUTE(1), 
	UDP_ARCHIVE_JOB_TIMEUNIT_HOUR(2), 
	UDP_ARCHIVE_JOB_TIMEUNIT_DAY(3), 
	UDP_ARCHIVE_JOB_TIMEUNIT_WEEK(4), 
	UDP_ARCHIVE_JOB_TIMEUNIT_MONTH(5),
	UDP_ARCHIVE_JOB_APPEND(6),
	UDP_ARCHIVE_JOB_SEPARATE(7);

	private int value;

	private MediaPoolType(int val) {
		this.value = val;
	}

	public int getValue() {
		return this.value;
	}
	
	public static MediaPoolType fromValue(int value){
		MediaPoolType[] types = MediaPoolType.values();
		for(MediaPoolType type : types){
			if(type.getValue() == value){
				return type;
			}
		}
		return null;
	}
}
