#pragma once
/********************************************************************************
*AFDefine.h: Created by zouyu01 on 07/17/2009
*Purpose: Constants definition which will be used by multi-modules.
*Restriction: Only constants definition can be put here, 
*             interface APIs should not be put here.
*
********************************************************************************/
#include <string>
#include <vector>
#ifndef LINUX
#include <Windows.h>
#endif

#define AF_JOBMETHOD_FULL                 0                         //full backup
#define AF_JOBMETHOD_INCR                 1                         //incremental backup
#define AF_JOBMETHOD_RESYNC               2                         //resync backup

#define AF_JOBMETHOD_FULL_STRING          L"Full"                   //full backup
#define AF_JOBMETHOD_INCR_STRING          L"Incremental"            //incremental backup
#define AF_JOBMETHOD_RESYNC_STRING        L"Resync"                 //resync backup

#define AF_JOB_STATUS_ACTIVE              0
#define AF_JOB_STATUS_FAILED              1
#define AF_JOB_STATUS_FINISHED            2
#define AF_JOB_STATUS_CRASHED             3
#define AF_JOB_STATUS_CANCELED            4
#define AF_JOB_STATUS_SKIPPED             5
#define AF_JOB_STATUS_MISSED              6
#define AF_JOB_STATUS_INCOMPLETE          7

#define AF_JOB_STATUS_ACTIVE_STRING			L"Active"                   //Active
#define AF_JOB_STATUS_FAILED_STRING			L"Failed"					//Failed
#define AF_JOB_STATUS_FINISHED_STRING		L"Finished"                //Finished
#define AF_JOB_STATUS_CRASHED_STRING		L"Crashed"                //Crashed
#define AF_JOB_STATUS_CANCELED_STRING		L"Canceled"               //canceled
#define AF_JOB_STATUS_SKIPPED_STRING		L"Skipped"               //Skipped //<sonmi01>2010-1-14 BLI license check
#define AF_JOB_STATUS_MISSED_STRING         L"Missed"                   //Missed
#define AF_JOB_STATUS_INCOMPLETE_STRING     L"Incomplete"                   //Incomplete


#define AF_COMPRESSION_LEVEL_NONE			0
#define AF_COMPRESSION_LEVEL_STANDARD		1
#define AF_COMPRESSION_LEVEL_MAXIMUM		9
#define AF_COMPRESSION_LEVEL_VHD			0X1000 // VHD format, no compression

#define BAKINFO_ROOTITEM_TYPE_VOLUME		L"Volume"
#define BAKINFO_ROOTITEM_TYPE_APP			L"Application"
#define ITEM_TYPE_VOLUME					BAKINFO_ROOTITEM_TYPE_VOLUME
#define ITEM_TYPE_APP						BAKINFO_ROOTITEM_TYPE_APP

/********************ARCFLASH FAILED BACKUP FOLDER******************************/
#define AF_BACKUP_HISTORY_FOLDER           L"BackupHistory"
#define AF_MERGE_HISTORY_FOLDER            L"MergeHistory" //<sonmi01>2011-8-12 ###???
#define AF_RPSREP_HISTORY_FOLDER           L"ReplicateHistory"
#define AF_CATALOG_HISTORY_FOLDER          L"CatalogHistory"
#define AF_FILECOPY_HISTORY_FOLDER         L"FileCopyHistory"

/*******ARCFLASH BACKUP DESTINATION SIGNATURE FILE******************************/
#define AF_BACKUPDEV_SIG                  L"BackupDev.sig"
#define AF_BACKUPDEV_SIG_BAK              L"BackupDev.sig.bak"
#define AF_BACKUPDEV_SIG_ATTR             FILE_ATTRIBUTE_NORMAL
#define AF_D2D_NODEINFO                   L"NodeInfo"

/***************ARCFLASH BACKUP DESTINATION LOCK FILE***************************/
#define AF_BACKUPDEST_LCK                 L"Dest.lck"
#define AF_BACKUPDEST_LCK_ATTR           (FILE_ATTRIBUTE_HIDDEN | FILE_ATTRIBUTE_SYSTEM | FILE_FLAG_DELETE_ON_CLOSE)

/*******FOLDER NAME FOR OLD BAKCUP**********************************************/
#define AF_OLDBACKUP_FOLDER_NAME L"14DB3A5D-2849-4b39-8E0F-E0C589324BAF"

/*******************BACKUP INFO XML FILE****************************************/
#define AF_BACKUP_INFO_XML              L"BackupInfo.XML"
#define AF_BACKUP_INFO_XML_BAK          L"BackupInfo.XML_BAK"
#define AF_VM_INFO_XML					L"VMInfo.XML"

//<sonmi01>2014-7-7 #
//////////////////////////////////////////////////////////////////////////
#define AF_VAPP_METADATA_XML			L"VAppMetadata.XML"

/**********************Mutex for getting job id*********************************/
#define AF_MUTEX_JOB_ID                 L"MutexJobId"
/**********************Job Id Registry Name*************************************/
#define AF_REG_JOB_ID                   L"JobId"

/*******************************Mutex for AFBackend.exe*************************/
#define AF_MUTEX_AFBACKEND                   L"Global\\{8D902EBD-CD79-4f53-B33C-53B80F626322}.Mutex"
#define AF_MUTEX_AFBACKEND_SESSRECLAIM       L"Global\\SessReclaim{DAC0F541-0636-4047-A7DE-3C61579366C7}.Mutex"
#define AF_MUTEX_AFBACKEND_JOB               L"Global\\Job{2619A2F1-D50B-41dc-9D4A-6E74A7FE39E5}.Mutex"
#define AF_MUTEX_AFBACKEND_RECOVERYVM_JOB	 L"Global\\JobVMR{980E8F47-00AB-49a2-BDA2-9E4E6749ADFD}.Mutex" //<sonmi01>2011-1-20 ###???



/*******************************Mutex for AF Archive job *************************/
#define AF_MUTEX_AFARCHIVE_JOB               L"Global\\{953E00D6-DE31-46c9-A56B-B7717C23CA5F}.Mutex"

#define AF_MUTEX_AFFILECOPY_JOB              L"Global\\{31496db0-20ab-11e5-b5f7-727283247c7f}.Mutex"

/*******************************Mutex for AF Restore job *************************/
#define AF_MUTEX_AFARCHIVERESTORE_JOB        L"Global\\{369CC166-A227-409f-A372-CB92EAC95410}.Mutex"

/*******************************Mutex for AF Purge job *************************/
#define AF_MUTEX_AFARCHIVEPURGE_JOB          L"Global\\{1904F7E5-40E6-42f8-8815-D7079361812E}.Mutex"

/*******************************Mutex for AF CatalogResync job *************************/
#define AF_MUTEX_AFARCHIVECATALOGRESYNC_JOB  L"Global\\{C17557A1-5EB4-4487-87B2-82E15E109B75}.Mutex"


/*******************************Mutex for AF CatalogResync job *************************/
#define AF_MUTEX_AFSOURCEFILEDELETE_JOB  L"Global\\{28866849-68D6-475D-BDB5-D31A35D3860C}.Mutex"

/*******************************Mutex for AFLicCheck API************************/
#define AF_LICENSE_LOCKER                 L"Global\\$LicenseLock$"

/*******************************Crashed job flag********************************/
//<sonmi01>2012-12-20 ###???
#define REG_ARCFLASH_D2D_ROOT_KEY			CST_REG_HEADER_L L"\\" CST_PRODUCT_REG_NAME_L
#define REG_ARCFLASH_BACKUP_SUB_KEY			CST_REG_HEADER_L L"\\" CST_PRODUCT_REG_NAME_L L"\\AFBackupDll"
#define REG_IS_ACTIVE_JOB_CRASHED			L"IsActiveJobCrashed"
#define REG_SESSION_NUMBER_ACTIVE_JOB_CRASHED L"SessionNumberActiveJobCrashed"
#define REG_SESSION_HW_SNAPSHOT_VMFS L"HWSnapshotForVMFS"
#define REG_SESSION_HW_SNAPSHOT_NFS L"HWSnapshotForNFS"
/*******************************rps replication job********************************/ //<sonmi01>2012-3-7 ###???
#define REG_ARCFLASH_RPS_REP_SUB_KEY      CST_REG_HEADER_L L"\\" CST_PRODUCT_REG_NAME_L L"\\RPSReplication"

/*******************************wait and retry time for session lock************/
#define REG_LOCK_WAIT_TIME                  L"LockWaitTime"         // the lock wait time
#define REG_LOCK_MAX_RETRY                  L"LockMaxRetry"         // the max retry times
#define REG_LOCK_WAIT_TIME_MERGE            L"LockWaitTime_Merge"   // the lock wait time for merge job    
#define REG_LOCK_MAX_RETRY_MERGE            L"LockMaxRetry_Merge"   // the max retry times for merge job

/*******************the replication running time and status for AQA use********/
#define REG_REP_DBG_JOBSTATUS               L"RepDbgJobStatus"      // the rep status, such as AF_JOB_STATUS_FAILED_STRING
#define REP_REP_DBG_RUNNINGTIME             L"RepDbgRunningTime"    // the running time, in MS


/**************Specified Error code for AFCoreFunction.dll**********************/
#define AF_ERR_DEST_INUSE                 0xE00000B7//0x11100000000000000000000000B7
#define AF_ERR_DEST_SYSVOL                0xE0000022
#define AF_ERR_DEST_BOOTVOL               0xE0000021
#define AF_ERR_NOT_BAKDEST                0xE0000002
#define AF_ERR_NO_RESTPOINT               0xE0000103
#define AF_ERR_DEST_FOR_OTHER             0xE0000005
#define AF_ERR_DEST_UNDER_BAKDEV          0xE0000004
#define AF_ERR_FORMER_DEST_MISSING        0xE0000003
#define AF_ERR_INVALID_USER               0xE0000525
#define AF_ERR_USER_NOT_IN_GROUP          0xE0000529
#define AF_ERR_INVALID_BMRLIC             0xE0000056
#define AF_ERR_INVALID_BMRAHWLIC          0xE0000057
#define AF_ERR_COPY_DEST_INUSE            0xE0000058
#define AF_ERR_MULTI_USER                 0xE0000059

/************Specified Error code for Shprovd***********************************/
#define AF_ERR_MOUNT_MOUNTED_SESS         0xE0000080 //The session has been mounted to a mnt, cannot mount it to another mnt.
#define AF_ERR_MOUNTTO_FAT32              0xE0000081 //cannot mount the session to a fat32 folder.
#define AF_ERR_MOUNTTO_NET_DRIVE_LETTER   0xE0000082 //the drive letter may be used by other user, cannot use this kind of mount point.
#define AF_ERR_MOUNT_FAIL_LOCK_SESS       0xE0000083 //fail to lock the session during mount.
#define AF_ERR_OPEN_MOUNTING_DRIVER       0xE0000084 //fail to open mounting driver, the driver may not be installed correctly.
#define AF_ERR_MOUNT_INVALID_SESS         0xE0000085 //fail to mount the invalid session. maybe full is missing.

/***************module name for AFMsg.enu.dll***********************************/
//#define AF_MODULE_AFMSG                   L"AFMSG.ENU.dll"

/***************Registry of days to purge SQL Log*******************************/
#define AF_REG_PURGE_SQL_LOG_DAYS         L"PurgeSqlLogDays"
#define AF_REG_PURGE_SQL_LOG_SUCCESSDATE  L"PurgeSqlLogSuccessDate"

/***************Registry of days to purge Exchange Log**************************/
#define AF_REG_PURGE_EXCHANGE_LOG_DAYS    L"PurgeExchangeLogDays"
#define AF_REG_PURGE_EXCHANGE_LOG_SUCCESSDATE  L"PurgeExchangeLogSuccessDate"

/*******************Time interval for purging APP log***************************/
#define AF_PURGE_LOG_DISABLE              0
#define AF_PURGE_LOG_DAILY                1
#define AF_PURGE_LOG_WEEKLY               7
#define AF_PURGE_LOG_MONTHLY              30

/**************************Backup destination threshold*************************/
#define AF_REG_DEST_THRESHOLD             L"DestThreshold"

/**************************Backup destination BDI********************************/
#define AF_DEST_BDI_FILE                  L"Bdi.ini"

/**************************Backup information DB on Backup Destination***********/
#define AF_BACKUP_INFO_DB_FILE            L"BackupInfoDB.xml"

/**************************Backup destination structure *************************/
#define AF_BACKUP_DEST_INDEX              L"Index"
#define AF_BACKUP_DEST_CATALOG            L"Catalog"
#define AF_BACKUP_DEST_VSTORE             L"VStore"

#define AF_ARCHIVE_CONFIG_FILE            L"ArchiveConfiguration.xml"
#define AF_FILECOPY_INFO_DB_FILE          L"ARchiveInfoDB.xml"
#define AF_FILEARCHIVE_INFO_DB_FILE       L"FileArchiveInfoDB.xml"
/****************************BOOT VOLUME OR SYSTEM VOLUME*************************/
#define AF_VOLUME_BOOT                    0x01
#define AF_VOLUME_SYSTEM                  0x02
#define AF_VOLUME_UEFI_RECOVERY			  0x04	

/**************************** Computer name length for D2D************************/
//the max length for windows dns computer name is 63 characters.
#define AF_COMPUTER_NAME_LENGTH           64

#define AF_DEST_MAX_LEN                   170

/**************************** Session information of active job *******************/
#define AF_REG_BACKUP_DLL                              L"AFBackupDll"
#define AF_REG_ACT_SESS_NO                             L"SessionNumberActiveJobCrashed"
#define AF_REG_ACT_JOB_METHOD                          L"ActiveJobMethod"
#define AF_REG_SESSION_GUID                            L"SessionGuid"
#define AF_REG_DATA_STORE_GUID_4_LAST_BACKUP           L"DSGUIDForLastBackup"

#define AF_REG_ARCHIVE_DLL							   L"AFArchiveDLL"
#define AF_REG_ARCHIVE_DEST_SESSION_GUID               L"ArchiveSessionGuid"
#define AF_REG_ARCHIVE_RESYNC_CATALOG				   L"ReSyncCatalog"

//<sonmi01>2010-11-17 vm disk transport mode
#define AF_VMDISK_TRANSPORT_MODE_VALUE_UNKNOWN			0
#define AF_VMDISK_TRANSPORT_MODE_VALUE_SAN				1
#define AF_VMDISK_TRANSPORT_MODE_VALUE_NBD				2
#define AF_VMDISK_TRANSPORT_MODE_VALUE_NBDSSL			3
#define AF_VMDISK_TRANSPORT_MODE_VALUE_HOTADD			4
#define AF_VMDISK_TRANSPORT_MODE_VALUE_FILE				5

#define AF_VMDISK_TRANSPORT_MODE_STRING_UNKNOWN			L""
#define AF_VMDISK_TRANSPORT_MODE_STRING_SAN				L"SAN"
#define AF_VMDISK_TRANSPORT_MODE_STRING_NBD				L"NBD"
#define AF_VMDISK_TRANSPORT_MODE_STRING_NBDSSL			L"NBDSSL"
#define AF_VMDISK_TRANSPORT_MODE_STRING_HOTADD			L"HOTADD"
#define AF_VMDISK_TRANSPORT_MODE_STRING_FILE			L"FILE"


enum { HYPERVISOR_TYPE_ESX = 0, HYPERVISOR_TYPE_HYPERV, HYPERVISOR_TYPE_VCLOUD = 3 }; //move to afdefine.h //<sonmi01>2014-8-19 #vpp backupinfodb vmbackupinfodb

#ifdef __cplusplus
extern "C"{
#endif
   typedef struct _NET_CONN_INFO
   {
      wchar_t szDomain[MAX_PATH]; //domain name for net resource.
      wchar_t szUsr[MAX_PATH]; //user name for net resource.
      wchar_t szPwd[MAX_PATH]; //password for net resource.
      wchar_t szDir[MAX_PATH]; //remote shared folder. Full path.
   }NET_CONN_INFO, *PNET_CONN_INFO;

   typedef struct _DEV_ACCESS_INFO
   {
      DWORD dwErr;   //access error code which is one of system error code. if access succeeds, dwErr is 0.
      DWORD dwDriveType; //Drive type for backup destination.
      wchar_t szDir[MAX_PATH]; //the folder which is accessed.
   }DEV_ACCESS_INFO, *PDEV_ACCESS_INFO;

   typedef struct _BDI
   {
      DWORD dwType;
      GUID guid;
      NET_CONN_INFO destInfo;
   }BDI, *PBDI;

   typedef enum _D2D_NODE_TYPE
   {
	   DNT_PHYSICAL  = 0,
	   DNT_HYPERV_VM = 1,
	   DNT_ESX_VM    = 2,
	   DNT_HBBU_VM   = 3,
	   DNT_OTHER_VM  = 4
   }D2D_NODE_TYPE;

   #define DNT_HOSTNAME_MAX_LENGTH 128
   typedef struct _D2D_NODE_INFO //r16.5 old version
   {
	   D2D_NODE_TYPE nodeType;
	   WCHAR szHostName[DNT_HOSTNAME_MAX_LENGTH];
	   WCHAR szHyperVisorHostName[DNT_HOSTNAME_MAX_LENGTH];
   }D2D_NODE_INFO, *PD2D_NODE_INFO;

   // the three dwOSProductType
   #define PRODUCT_TYPE_SUBLIC_OS_WORKSTATION  1
   #define PRODUCT_TYPE_SUBLIC_OS_SBS 2
   #define PRODUCT_TYPE_SUBLIC_OS_SERVER 3
   typedef struct _D2D_NODE_INFO_EX //extend for Oolong
   {
	   DWORD dwD2DNodeInfoSize;
	   D2D_NODE_TYPE nodeType;
	   DWORD dwOSMajorVersion;
	   DWORD dwOSMinorVersion;
	   DWORD dwOSProductType;
	   DWORD dwOSSuiteMask;
	   DWORD dwHyperVisorNumberOfProcessors; //used for HBBU node
	   DWORD dwHyperVisorNumberOfLogicalProcessors; //used for HBBU node
	   DWORD dwNumberOfProcessors; //used for local node
	   DWORD dwNumberOfLogicalProcessors; //used for local node
	   WCHAR szHyperVisorHostName[DNT_HOSTNAME_MAX_LENGTH];
	   WCHAR szHostName[DNT_HOSTNAME_MAX_LENGTH];
	   UCHAR aucReserved[256];
   }D2D_NODE_INFO_EX, *PD2D_NODE_INFO_EX;

#ifdef __cplusplus
}
#endif

// Node OS type
enum E_NODE_OS_TYPE
{
	ENOT_UNKNOWN = 0,
	ENOT_WINDOWS,
	ENOT_LINUX,
	ENOT_UNIX,
	ENOT_MAC
};

// Agent Backup type, LocalD2D or HBBU
enum E_BACKUP_TYPE
{
	EBT_UNKNOWN = 0,
	EBT_LOCALD2D_BK,
	EBT_HBBU_BK
};

//backupinfo index file.
typedef struct _BACKUP_ITEM_INFO
{   
   DWORD dwHour;
   DWORD dwMin;
   DWORD dwSec;
   DWORD dwYear;
   DWORD dwMonth;
   DWORD dwDay;
   FILETIME bkLocalTime;
   //for copy recover point time
   DWORD dwCpyHour;
   DWORD dwCpyMin;
   DWORD dwCpySec;
   DWORD dwCpyYear;
   DWORD dwCpyMonth;
   DWORD dwCpyDay;
   //end copy recover point time.
   DWORD dwType; //1 for recovery point, 0 for backup history.
   DWORD dwJobMethod; //full, incremental, resync.
   DWORD dwStatus; //success
   DWORD dwMajVer; //major version
   DWORD dwMinVer; //minor verion
   DWORD dwFSCatalog; //status for file catalog. 0 is fail. 1 is have, 2 is pending.
   DWORD dwBKSetFlag;
   DWORD dwBKAdvSchFlag;
   // distinguish agent, guest os, VM hypervisor type. [9/11/2014 zhahu03]
   DWORD dwAgentOSType;		// E_NODE_OS_TYPE: Windows/Linux/Unix/Mac...
   DWORD dwAgentBackupType;	// E_BACKUP_TYPEE: BT_LOCAL_D2D / EBT_HBBU
   DWORD dwVMGuestOsType;	// E_NODE_OS_TYPE, just valid when dwAgentBackupType==EBT_HBBU.
   DWORD dwVMHypervisor;	// HYPERVISOR_TYPE_ESX/HYPERVISOR_TYPE_HYPERV/HYPERVISOR_TYPE_VCLOUD, just valid when dwAgentBackupType==EBT_HBBU.
   
   BOOL  bEnableDedupe;
   ULONGLONG ullProtectedDataSize;	//From BackupInfo.xml ProtectedDataSizeB

   //
   ULONGLONG ullScheduledTime;
   std::wstring strSubPath;
   std::wstring strId; // jobId.
   std::wstring strSessionGUID; //session GUID.
   std::wstring strNodeID; //<sonmi01>2014-3-25 #node id - agent UUID or VM instance UUID
   std::wstring strDataSizeB; // data size in bytes.
   std::wstring strTransferDataSizeB; // the transfer data size in bytes
   std::wstring strCatalogSizeB; //catalog size in bytes.
   std::wstring strGrtCatalogSize; // the grt catalog size
   std::wstring strCommonPathSize; // the common path size
   std::wstring strBackupName; //backup name
   std::wstring strEncryptType;
   std::wstring strEncryptPasswordHash;
   std::wstring strDataStoreKeyHash;
   std::wstring strCurBackupDestination; // Backup destination containing current backup info database. It will not be save in BackupInfoDB.xml

	//<sonmi01>2012-12-20 #buffer is too small to hold large int
   _BACKUP_ITEM_INFO() :
   dwHour(0),
   dwMin(0),
   dwSec(0),
   dwYear(0),
   dwMonth(0),
   dwDay(0),
   //for copy recover point time
   dwCpyHour(0),
   dwCpyMin(0),
   dwCpySec(0),
   dwCpyYear(0),
   dwCpyMonth(0),
   dwCpyDay(0),
   //end copy recover point time.
   dwType(0), //1 for recovery point, 0 for backup history.
   dwJobMethod(0), //full, incremental, resync.
   dwStatus(0), //success
   dwMajVer(0), //major version
   dwMinVer(0), //minor verion
   dwFSCatalog(0), //status for file catalog. 0 is fail. 1 is have, 2 is pending.
   dwBKSetFlag(0),
   dwBKAdvSchFlag(0),
   dwAgentOSType(0),
   dwAgentBackupType(0),
   dwVMGuestOsType(0),
   dwVMHypervisor(0),
   bEnableDedupe(FALSE),
   ullProtectedDataSize(0)
   {
   }

}BACKUP_ITEM_INFO, *PBACKUP_ITEM_INFO;

typedef struct _DAY_BACKUP_INFO
{
   DWORD dwDay;
   std::vector<_BACKUP_ITEM_INFO> vBackupItem;
}DAY_BACKUP_INFO, *PDAY_BACKUP_INFO;

typedef struct _MONTH_BACKUP_INFO
{
   DWORD dwMonth;
   std::vector<DAY_BACKUP_INFO> vDayInfo;
}MONTH_BACKUP_INFO, *PMONTH_BACKUP_INFO;

typedef struct _YEAR_BACKUP_INFO
{
   DWORD dwYear;
   std::vector<MONTH_BACKUP_INFO> vMonthInfo;
}YEAR_BACKUP_INFO, *PYEAR_BACKUP_INFO;

typedef struct _BACKUP_INFO_DB
{
   DWORD dwStartSessNo; //[zouyu01 2011-10-20, RHA Integration]start session no on the backup destination.
   std::vector<YEAR_BACKUP_INFO> vBackupInfo;
}BACKUP_INFO_DB, *PBACKUP_INFO_DB;

//
// Define the version of BackupInfoDB.xml
// Change the version on demand, not exactly same as release version
//
#define BACKUPINFODB_VER_V5U3	MAKEWORD(5, 3)
#define BACKUPINFODB_VER_V6		MAKEWORD(6, 0)


#define SYNC_DATA_TYPE_BACKUP				0
#define SYNC_DATA_TYPE_RPS_REPLICATION		1
#define SYNC_DATA_TYPE_RPS_MERGE			2
#define SYNC_DATA_TYPE_RPS_CATALOG			3
#define SYNC_DATA_TYPE_RPS_SESSION			4

struct _BACKUP_ITEM_INFO_EX : public BACKUP_ITEM_INFO
{
	std::wstring strDataStore;
	std::wstring strNodeName;

	_BACKUP_ITEM_INFO_EX()
	{
	}

	_BACKUP_ITEM_INFO_EX(const BACKUP_ITEM_INFO& info)
	{
		dwHour = info.dwHour;
		dwMin = info.dwMin;
		dwSec = info.dwSec;
		dwYear = info.dwYear;
		dwMonth = info.dwMonth;
		dwDay = info.dwDay;
		bkLocalTime = info.bkLocalTime;
		dwCpyHour = info.dwCpyDay;
		dwCpyMin = info.dwCpyMin;
		dwCpySec = info.dwCpySec;
		dwCpyYear = info.dwCpyMonth;
		dwCpyMonth = info.dwCpyMonth;
		dwCpyDay = info.dwCpyDay;
		dwType = info.dwType;
		dwJobMethod = info.dwJobMethod;
		dwStatus = info.dwStatus;
		dwMajVer = info.dwMajVer;
		dwMinVer = info.dwMinVer;
		dwFSCatalog = info.dwFSCatalog;
		dwBKSetFlag = info.dwBKSetFlag;
		strSubPath = info.strSubPath;
		strId = info.strId;
		strSessionGUID = info.strSessionGUID;
		strDataSizeB = info.strDataSizeB;
		strTransferDataSizeB = info.strTransferDataSizeB;
		strCatalogSizeB = info.strCatalogSizeB;
		strCommonPathSize = info.strCommonPathSize;
		strGrtCatalogSize = info.strGrtCatalogSize;
		strBackupName = info.strBackupName;
		strEncryptType = info.strEncryptType;
		strEncryptPasswordHash = info.strEncryptPasswordHash;
		strCurBackupDestination = info.strCurBackupDestination;

		// BEGIN [9/11/2014 zhahu03]
		dwAgentBackupType = info.dwAgentBackupType;
		dwAgentOSType = info.dwAgentOSType;
		dwVMGuestOsType = info.dwVMGuestOsType;
		dwVMHypervisor = info.dwVMHypervisor;
	}
};
typedef _BACKUP_ITEM_INFO_EX BACKUP_ITEM_INFOEX, *PBACKUP_ITEM_INFO_EX;

typedef struct _ARCHIVE_ITEM_INFO
{   
   DWORD dwHour;
   DWORD dwMin;
   DWORD dwSec;
   DWORD dwDay;
   DWORD dwMonth;
   DWORD dwYear;
   DWORD dwType; //1 for recovery point, 0 for backup history.
   DWORD dwJobMethod; //full, incremental, resync.
   DWORD dwStatus; // NA/scheduled/running/Finished
   std::wstring strSubPath;
   std::wstring strId; // jobId.
   std::wstring strArchiveDataSizeB; // data size in bytes.     
   std::wstring strFileCopyDataSizeB; // data size in bytes. 
   DWORD CompressionType;
   DWORD dwEncryptedStatus;
   DWORD dwScheduleCount;
   DWORD dwArchiveJobID;
   DWORD dwArchiveDestinationType;
   std::wstring stArchiveDestinationPath;
   std::wstring stArchiveDestinationUser;
   std::wstring strBackupSessGUID;

}ARCHIVE_ITEM_INFO, *PARCHIVE_ITEM_INFO;
typedef enum _ARCHIVEJOBSTATUS
{
	ScheduleNotApplicable = 0,
	ScheduleReady = 1,
	ScheduleScheduled = 2,
	ScheduleRunning = 3,
	ScheduleFinished = 4,
	ScheduleAll = 5,
	ScheduleCancel = 6,
	ScheduleFailed = 7,
	ScheduleIncomplete = 8,
	ScheduleCrashed = 9,
	ScheduleDeletePending = 10,
	LastJobDetails = 255 //reserved
}ARCHIVEJOBSTATUS;
typedef struct _ARCHIVE_JOB_INFO
{
   std::vector<ARCHIVE_ITEM_INFO> vArchiveJobItem;
}ARCHIVE_JOB_INFO, *PARCHIVE_JOB_INFO;
typedef struct _ARCHIVE_INFO_DB
{
   std::vector<ARCHIVE_JOB_INFO> vArchiveJobInfo;
   DWORD dwCurrentUnscheduledSessionCount;
   DWORD dwLastUpdateSessionNumber;
}ARCHIVE_INFO_DB, *PARCHIVE_INFO_DB;
typedef struct _ARCHIVE_SCHEDULER_SETTINGS
{
   DWORD dwSubmitArchiveAfterNBackups;
   BOOL  bArchiveEnabled;
}ARCHIVE_SCHEDULER_SETTINGS, *PARCHIVE_SCHEDULER_SETTINGS;

typedef ARCHIVE_SCHEDULER_SETTINGS FILECOPY_SCHEDULER_SETTINGS;
typedef struct _FILECOPYJOB_SCHEDULER_POLICY
{
	FILECOPY_SCHEDULER_SETTINGS filecopySchedulerSettings;
	std::vector<std::wstring>   policyVolumesList;
}FILECOPYJOB_SCHEDULER_POLICY, *PFILECOPYJOB_SCHEDULER_POLICY;

typedef struct _ARCHIVE_DESTINATION_PROPERTIES
{
   BOOL  bEncryptionEnabled;
   DWORD dwCompressionLevel;
}ARCHIVE_DESTINATION_PROPERTIES, *PARCHIVE_DESTINATION_PROPERTIES;

typedef struct _FILECOPTY_JOB_STATS
{
	LONGLONG llTotalFilesCopied;
	LONGLONG llTotalFilesArchived;
	LONGLONG llTotalDataCopied;
	LONGLONG llTotalDataSavedOnSource;
}FILECOPY_JOB_STATS, *PFILECOPY_JOB_STATS;

typedef struct _FILECOPY_JOB_DETAILS_DEST
{
	std::wstring		DestinationLocation;
	std::wstring		DestinationName;
	std::wstring		DestinationGUID;
	DWORD				DestinationType;
	std::wstring		CatalogServerName;
	std::wstring		CatalogLocation;
	DWORD				DestinationAccountId;
}FILECOPY_JOB_DETAILS_DEST, *PFILECOPY_JOB_DETAILS_DEST;

typedef struct _FILECOPY_JOB_DETAILS
{
	DWORD dwJobStatus;
	DWORD dwJobId;
	FILECOPY_JOB_DETAILS_DEST destination;
	FILECOPY_JOB_STATS   stats;
}FILECOPY_JOB_DETAIL, *PFILECOPY_JOB_DETAIL;
typedef std::vector<_BACKUP_ITEM_INFO> VBACKUP_ITEM_INFO;
typedef std::vector<DAY_BACKUP_INFO> VDAY_BACKUP_INFO;
typedef std::vector<MONTH_BACKUP_INFO> VMONTH_BACKUP_INFO;
typedef std::vector<YEAR_BACKUP_INFO> VYEAR_BACKUP_INFO;
typedef std::vector<ARCHIVE_JOB_INFO> VARCHIVE_JOB_INFO;
typedef std::vector<ARCHIVE_ITEM_INFO> VARCHIVE_ITEM_INFO;

typedef std::vector<BACKUP_INFO_DB> VBACKUP_INFO_DB;

//structure for mapped newwork drive.
typedef struct _MAPPED_DRV_PATH
{
   std::wstring strMnt; //drive letter for network path like 'Z:'
   std::wstring strPath; //network path like '\\zouyu01\output'.
}MAPPED_DRV_PATH, *PMAPPED_DRV_PATH;

//structure for storing informtion of  backed up VM
typedef struct _ESX_INFO
{
	std::wstring serverName;
	std::wstring userName;
	//std::wstring password;
	LONG viPort;
	std::wstring protocol;
	BOOL ignoreCertificate;
}ESX_INFO, PESX_INFO;

typedef  struct _VM_BACKUP_INFO
{
	std::wstring OSVersion; //<sonmi01>2015-12-22 ###???
	int MajorVersion;
	int MinorVersion;
	std::wstring hostName;
	std::wstring uuid;
	std::wstring vmName;
	std::wstring vmx;
	BOOL powerState;
	std::wstring userName;
	std::wstring instanceUuid;
	std::wstring destionation;
	std::wstring browsedDestionation; //<sonmi01>2011-3-18 ###???
	ESX_INFO esxInfo;
	int hypervisorType;
	std::wstring password;
	std::wstring OSName;
	std::wstring connectablePoint;

}VM_BACKUP_INFO, *PVM_BACKUP_INFO;

typedef std::vector<VM_BACKUP_INFO> VMInfoList;

//license flag for GUI use.
#define AF_LICENSE_SUC          0x00
#define AF_LICENSE_ERR          0x01
#define AF_LICENSE_WAR          0x02

#ifdef __cplusplus
extern "C"{
#endif

typedef struct _LICENSE_INFO
{
   DWORD dwBase;  //base license.
   DWORD dwBLI;   //license for BLI.
   DWORD dwAllowBMR;  //license for BMR.
   DWORD dwAllowBMRAlt; //license for BMR Alternate Hardware.
   DWORD dwProtectSql;  //license for SQL server. !! in r16 SQLE does not require a license
   DWORD dwProtectExchange; //license for exchange server. !! in r16 we do not use it
   DWORD dwProtectHyperV; //license for Hiper-V.
      // new license components in r16
   DWORD dwEncryption;		// license for encryption
   DWORD dwScheduledExport;	// license for Simple Virtual Conversion (AKA scheduled export)
   DWORD dwExchangeDB;		// license for Exchange DB level recovery
   DWORD dwExchangeGR;		// license for Exchange Granular Recovery
   DWORD dwD2D2D;			// license for File Copy to local disk
   // end -- new license components in r16
}LICENSE_INFO, *PLICENSE_INFO;

//ZZ: Added by zhoyu03 for storing encryption information in ADR configuration.
typedef struct _CRYPTO_INFO_
{
    DWORD   dwCryptoLibType;  // Encryption library type. e.g. MS or ETPKI. 0 means not enryption.
    DWORD   dwCryptoAlgType;  // Encryption algorithm type. e.g. AES 128, 192 or 256. 0 means not enryption.
    std::wstring wsCryptoPwdHash;  // Hash value string for session password.
    std::wstring wsCryptoPwd;      // Session password, used for DR context only.
}ST_CRYPTO_INFO, *PST_CRYPTO_INFO;

#ifdef __cplusplus
}
#endif

class CSessPwdWrap
{
public:
    CSessPwdWrap() : bFound(false) {}
    std::wstring wsSessGUID;
    std::wstring wsSessPwd;
    bool    bFound;
};

//********************************************************************
//<sonmi01>2010-11-18 vminfo xml for edge DB info
typedef struct tagVMInfoXml_VC_ESX
{
	std::wstring ServerName;
	std::wstring Username;
	//std::wstring Password;
	LONG  VIport;
	std::wstring Protocol;
	BOOL ignoreCertificate;
}VMInfoXml_VC_ESX, *PVMInfoXml_VC_ESX;

typedef struct tagVMInfoXml_VM
{
	std::wstring vmName;	
	std::wstring vmUUID;
	std::wstring vmHost;
	std::wstring vmVMX;
	std::wstring vmESXHost;
	std::wstring vmInstUUID;
	std::wstring vmGuestOS;
	std::wstring vmIP;
    std::wstring vmResPool; // baide02 for vm resource pool
	BOOL powerState;
}VMInfoXml_VM, *PVMInfoXml_VM;

typedef struct tagVMInfoXml_GuestOS
{
	std::wstring VMUsername;
	//std::wstring VMPassword;
	std::wstring OSTypeString;
	ULONG OSTypeValue;
	//std::wstring reserved1;
	//std::wstring reserved2;
	//std::wstring reserved3;
}VMInfoXml_GuestOS, *PVMInfoXml_GuestOS;

typedef struct tagVMInfoXml
{
	std::wstring SessionUniqueID;
	int appType; //0 Raw    1 File Level
	VMInfoXml_VC_ESX VCenter;
	VMInfoXml_VC_ESX Esx;
	VMInfoXml_VM VM;
	VMInfoXml_GuestOS GuestOS;
}VMInfoXml, *PVMInfoXml;
//********************************************************************

//JNI web service.
#define RPS_JAR_LIST	"rps-webservice-contract.jar;flash-webservice-impl.jar;flash-common.jar;"
#define HYPERV_JAR_LIST "flash-client.jar;flash-webservice-contract.jar;flash-jobscript.jar;"
#define ESX_JAR_LIST "vmwaremanager.jar;axis.jar;commons-discovery-0.2.jar;commons-logging-1.0.4.jar;jaxrpc.jar;log4j-1.2.8.jar;saaj.jar;vim25.jar;wsdl4j-1.5.1.jar;"

#define VDDK_VER_HIGH 1
#define VDDK_VER_LOW  2
#define VDDK_VER_SUB  0
#define VDDK_BINARY  L"vixDiskLib.dll"

#define VM_SNAPSHOT_NOW_POWER_ON L"CurrentState"
#define VM_SNAPSHOT_NOW_POWER_OFF L"LatestState"



//fs catalog status. zouyu01 on 2011-5-12.
#define FSCAT_FINISH 0x01
#define FSCAT_FAIL   0x00
#define FSCAT_PENDING 0x02
#define FSCAT_DISABLED 0x03
#define FSCAT_FAIL_STR L"$FAILED$"
#define FSCAT_DISABLE_STR L"$DISABLED$"

//added by zouyu01 on 2011-6-22.
#define AF_COPY_DEST_SIG L"CopyDev.sig"

//added by zouyu01 on 2011-6-28.
#define AF_BMR_FLAG_YES 0
///ZZ: Added for advanced schedule
#define MAX_NUM_STR_LEN                              64  ///ZZ: Add for advanced schedule

#define AF_INIT_RETENTION_FLAG                       0

#define AF_FIRMWARE_BIOS_STR L"BIOS"
#define AF_FIRMWARE_UEFI_STR L"UEFI"

//definition for hidden volume information.
namespace AF_VOL_INFO
{
#ifdef __cplusplus
extern "C"{
#endif
    typedef struct _LIST
    {
        _LIST *prev;
        _LIST *next;
    }LIST;

    typedef enum VOL_FLAG
    {
        VOL_UNKNOWN = 0,
        VOL_NORMAL, //volume which contains valid volume GUID path. This volume may be in GPT or MBR partition.
        VOL_ESP,
        VOL_OEM
    };
    typedef struct _VOL_INFO
    {
        DWORD dwFlag;
        DWORD dwDiskNo; //disk No. for the volume.
        DWORD dwDiskSig; //disk signature for the volume.
        ULONGLONG ullOffset; //offset for the extent of the volume in the disk.
        ULONGLONG ullSize; //length for the extent of the volume in the disk.
        wchar_t szDosDeviceName[MAX_PATH];
        wchar_t szVolGuidName[MAX_PATH];
    }VOL_INFO;

    typedef struct _VOL_INFO_LIST
    {
        LIST *list;
        VOL_INFO *pVolInfo;
    }VOL_INFO_LIST;

#ifdef __cplusplus
}
#endif

#define GET_NEXT_ITEM(p, type) (type *)((LIST *)p->next)
#define GET_PREV_ITEM(p, type) (type *)((LIST *)p->prev)
}

// For BackupDetail.ReplicationStatus
#define ATTR_REPLICATION_STATUS_FINISHED			L"Finished"
#define ATTR_REPLICATION_STATUS_CANCELLED			L"Cancelled"
#define ATTR_REPLICATION_STATUS_FAILED				L"Failed"
#define ATTR_REPLICATION_STATUS_NOT_REPLICATED		L"NotAttempted"
//#define ATTR_REPLICATION_STATUS					L"ReplicationStatus" //<sonmi01>2013-4-9 #replication status

//ZZ: Default key file name to record information under session folder
#define DEFAULT_KEY_FILE_NAME_4_SESSION             L"D2DSess.KF"
//ZZ: Default key file name to record information under data store 
#define DEFAULT_KEY_FILE_NAME_4_DATA_STORE          L"RPSDS.KF"


#define JOB_MON_NAME_PREFIX_RPS                     L"RPS"
#define JOB_MON_NAME_PREFIX_D2D                     L"D2D"
#define __USE_PREFIX_2_DISTINGUISH_RPS_D2D_JM__     0
#define FILEARCHIVE_TEMP_DBNAME                     L"FileArchiveDB"