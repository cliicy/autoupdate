package com.ca.arcflash.webservice.service.validator;

public enum PurgeLog {
	PURGE_LOG_NONE(0),PURGE_LOG_DAILY(1),PURGE_LOG_WEEKLY(7),PURGE_LOG_MONTHLY(30);	
	PurgeLog(int value){this.value = value;}
	private int value;
	
	public static PurgeLog valueOf(int value){
			for(PurgeLog pl: PurgeLog.values()){
					if (pl.value == value)
						return pl;
				}
		return null;
		
	}
	
	public static boolean validate(long value){
		return valueOf((int)value) != null;
	}
}
