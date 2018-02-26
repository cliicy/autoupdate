package com.ca.arcflash.webservice.jni.model;

public class JJobScriptRestoreOptionAD {
	public static int AD_RESTORE_OPTION_RENAMED_OBJECT = 0x00000001;
	public static int AD_RESTORE_OPTION_MOVED_OBJECT = 0x00000002;
	public static int AD_RESTORE_OPTION_LOST_OBJECT = 0x00000004;

	private long ulOptions;

	public long getUlOptions() {
		return ulOptions;
	}
	public void setUlOptions(long ulOptions) {
		this.ulOptions = ulOptions;
	}
	
	
}
