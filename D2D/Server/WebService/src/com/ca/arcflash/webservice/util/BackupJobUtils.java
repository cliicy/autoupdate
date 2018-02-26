package com.ca.arcflash.webservice.util;

import com.ca.arcflash.webservice.data.backup.BackupType;

public class BackupJobUtils {
	private static final int BASE_FULL_BACKUP_TYPE = 0x00000001;
	private static final int BASE_INCREMENTAL_TYPE = 0x00000002;
	private static final int BASE_RESYNC_TYPE =0x00000004;
	
	public static final int BACKUPJOB_SCHEDULE_FLAG = 0x00000010;
	public static final int BACKUPJOB_NOW_FLAG = 0x00000020;
	public static final int BACKUPJOB_MISS_FLAG = 0x00000040;
	public static final int BACKUPJOB_SKIP_FLAG = 0x00000080;
	public static final int BACKUPJOB_MAKEUP_FLAG = 0x00000100;
	
	public static int getBackupTypeFlag(int backupType, int backupFlag){
		int flags = 0;
		if(backupType == BackupType.Full){
			flags = BASE_FULL_BACKUP_TYPE;
		}
		else if(backupType == BackupType.Incremental){
			flags = BASE_INCREMENTAL_TYPE;
		}
		else if(backupType == BackupType.Resync){
			flags = BASE_RESYNC_TYPE;
		}
		
		if(flags==0)
			return flags;
		
		return flags|backupFlag;
	}
	
	public static boolean isMakeupJob(int backupFlag){
		if((backupFlag & BACKUPJOB_MAKEUP_FLAG) > 0){
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean isBackupNowJob(int backupFlag){
		if((backupFlag & BACKUPJOB_NOW_FLAG) > 0){
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean isScheduleJob(int backupFlag){
		if((backupFlag & BACKUPJOB_SCHEDULE_FLAG) > 0){
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean isFullBackupJob(int backupFlag){
		if((backupFlag & BASE_FULL_BACKUP_TYPE) > 0){
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean isIncBackupJob(int backupFlag){
		if((backupFlag & BASE_INCREMENTAL_TYPE) > 0){
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean isResyncBackupJob(int backupFlag){
		if((backupFlag & BASE_RESYNC_TYPE) > 0){
			return true;
		}
		else {
			return false;
		}
	}
	
	public static int getBackupJobType(int backupFlag){
		if(isFullBackupJob(backupFlag)){
			return BackupType.Full;
		}
		else if(isResyncBackupJob(backupFlag)){
			return BackupType.Resync;
		}
		else if(isIncBackupJob(backupFlag)){
			return BackupType.Incremental;
		}
		else{
			return BackupType.Unknown;
		}
	}
}
