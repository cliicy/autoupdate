package com.ca.arcflash.webservice.edge.srmagent;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.backup.SRMPkiAlertSetting;

public class SrmJniCaller {
	public static native String getSysHardwareInfo();
	public static native String getSysSoftwareInfo();
	public static native String getServerPkiInfo(int intervalInHour);
	public static native boolean isARCInstalled();
	public static native boolean startPkiMonitor();
	public static native boolean stopPkiMonitor();
	public static native boolean isSRMEnabled();
	public static native int savePkiAlertPolicy(SRMPkiAlertSetting setting);
	public static native int getAlertRecords(int[] alertTypes, String[] alertHeaders, int[] thresholds, int[] curUtils, int recordCount);
	public static native boolean enableAlert(boolean enable);
	public static native boolean enablePkiUtl(boolean enable);
	static {
		try{
			System.loadLibrary("SRMFacade");
		}catch(Throwable t){
			Logger.getLogger(SrmJniCaller.class).error(t.getMessage() == null ? t : t.getMessage());
		}
	}
}
