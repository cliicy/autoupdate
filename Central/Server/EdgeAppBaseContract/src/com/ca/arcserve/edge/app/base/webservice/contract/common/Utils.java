package com.ca.arcserve.edge.app.base.webservice.contract.common;

import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;

public class Utils {
	
	public static boolean hasBit(int bitmap, IBit bit) {
		return (bitmap & bit.getValue()) == bit.getValue();
	}
	
	public static boolean hasBit(int bitmap, IBit... bits) {
		for (IBit bit : bits) {
			if (hasBit(bitmap, bit)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static int setBit(int bitmap, IBit bit, boolean value) {
		return value ? (bitmap | bit.getValue()) : (bitmap & (~bit.getValue()));
	}

	public static boolean hasVSBTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.LocalConversion)
				|| hasBit(policyContentFlag, PlanTaskType.RemoteConversion);
	}
	
	public static boolean hasLocalVSBTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.LocalConversion);
	}

	public static boolean hasRemoteVSBTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.RemoteConversion);
	}
	
	public static boolean hasBackupTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.WindowsD2DBackup)
				|| hasBit(policyContentFlag, PlanTaskType.WindowsVMBackup) || hasBit(policyContentFlag, PlanTaskType.LinuxBackup);
	}
	
	public static boolean hasLiunxBackupTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.LinuxBackup);
	}
	
	public static boolean hasReplicationTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.Replication)
				|| hasBit(policyContentFlag, PlanTaskType.MspClientReplication);
			// Bug 761900: remove the "replicate now" for msp plan which do not have further replication task 
			//	|| hasBit(policyContentFlag, PlanTaskType.MspServerReplication);
	}
	
	public static boolean hasFileCopyTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.FileCopy);
	}
	
	public static boolean hasFileArchiveTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.FileArchive);
	}
	
	public static boolean hasCopyRecoveryPointTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.CopyRecoveryPoints);
	}
	
	public static boolean hasMSPServerReplicationTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.MspServerReplication);
	}
	
	public static boolean hasHbbuBackupTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.WindowsVMBackup);
	}
	
	public static boolean hasArchive2TapeTask(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.Archive2Tape);
	}
	
	public static boolean enableFileSystemCatalog(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.FileSystemCatalog);
	}
	
	public static boolean enableGRTCatalog(int policyContentFlag) {
		return hasBit(policyContentFlag, PlanTaskType.GRTCatalog);
	}
	
	public static String getUserNameNoDomain(String userName){
		if(userName.contains("\\"))
			return userName.substring(userName.indexOf("\\")+1);
		return userName;
	}
	
	public static String getDomainByUserName(String userName){
		if(userName.contains("\\"))
			return userName.substring(0,userName.indexOf("\\"));
		return "";
	}
	
	public static boolean simpleObjectEquals(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}
	
	 public static String getProtocolByUrl(String url){
			String protocol = url.substring(0,url.indexOf("://"));
			return protocol;
	}
	 
	public static int getPortByUrl(String url){
		String port = url.substring(url.lastIndexOf(":")+1);
		if(port.contains("/")){
			port=port.substring(0,port.indexOf("/"));
		}
		int portInt = Integer.parseInt(port);
		return portInt;
	}
	
	public static String getHostNameByUrl(String url){
		String host = url.substring(url.lastIndexOf("://")+3,url.lastIndexOf(":"));
		return host;
	}
	
	public static String getMessage(String pattern, Object ... arguments){
		return MessageFormatEx.format(pattern, arguments);
	}
}
