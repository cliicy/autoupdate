#ifndef _LOG_H
#define _LOG_H
#include "windows.h"
#include "AFJob.h"
#include "FlashDB.h"

#define AFINFO					0
#define AFWARNING				1
#define AFERROR					2
#define AUTOUPDATE_AFINFO		3
#define AUTOUPDATE_AFWARNING	4
#define AUTOUPDATE_AFERROR		5

#pragma pack(1)
typedef struct _tagHbbuCookieAclog
{
	DWORD cbSize;
	long  index;
	DWORD Flags;
	DWORD JobNo;
	WCHAR Msg[4096];
}HbbuCookieAclog, *PHbbuCookieAclog;
#pragma  pack()

typedef struct _tagActiveSyncLog
{
	WCHAR *Oper; // ADD ; PRUNE
	WCHAR *uniqueID;
	DWORD Flags;
	DWORD JobNo;
	WCHAR *strTime;
	WCHAR *strLog;
} ActiveSyncLog, *PActiveSyncLog;

typedef struct _tagSyncLogFiles
{
	WCHAR* szFilePath;
}SyncLogFiles, *PSyncLogFiles;

//
// the strcuture of BMR activity log
//
typedef struct _tagBMRActivityLog
{
	DWORD	dwFlags;	// the log level: 0-info, 1-warning, 2-error
	WCHAR   szMessage[4096];
}BMRActivityLog, *PBMRActivityLog;
// the event to tell BMR the activity log is read to read
#define BMR_LOG_EVT_NAME  L"381416A0-989E-4A37-9621-280B710CE46D"
//
// the job type of each activity log
//
enum ACTLOG_JOB_TYPE
{
	//
	// Job specific log
	//
	AJT_BACKUP						= AF_JOBTYPE_BACKUP,					// 0 backup job
	AJT_RESTORE						= AF_JOBTYPE_RESTORE,					// 1 restore job
	AJT_COPY						= AF_JOBTYPE_COPY,						// 2 copy job - recovery point management
	AJT_BACKUP_VMWARE				= AF_JOBTYPE_BACKUP_VMWARE,				// 3 VSphere vmware backup
	AJT_BACKUP_HYPERV				= AF_JOBTYPE_BACKUP_HYPERV,				// 4 VSphere hyperv backup
	AJT_RESTORE_VMWARE				= AF_JOBTYPE_RESTORE_VMWARE,			// 5
	AJT_RESTORE_HYPERV				= AF_JOBTYPE_RESTORE_HYPERV,			// 6
	AJT_ARCHIVE						= AF_JOBTYPE_ARCHIVE,					// 8 arhicve job
	AJT_ARCHIVE_PURGE				= AF_JOBTYPE_ARCHIVE_PURGE,				// 9 archive purge job
	AJT_ARCHIVE_RESTORE				= AF_JOBTYPE_ARCHIVE_RESTORE,			// 10 archive
	AJT_FS_CATALOG_GEN				= AF_JOBTYPE_FS_CATALOG_GEN,			// 11
	AJT_APP_CATALOG_GEN				= AF_JOBTYPE_APP_CATALOG_GEN,			// 12
	AJT_EXCH_GRT_CATALOG_GEN		= AF_JOBTYPE_EXCH_GRT_CATALOG_GEN,		// 13
	AJT_ARCHIVE_CATALOGRESYNC		= AF_JOBTYPE_ARCHIVE_CATALOGRESYNC,		// 14 catalog resync job
	AJT_FS_CATALOG_GEN_VM			= AF_JOBTYPE_FS_CATALOG_GEN_VM,			// 15
	AJT_FS_CATALOG_DISABLE			= AF_JOBTYPE_FS_CATALOG_DISABLE,		// 16
	AJT_DIRECT_RESTORE_VM_PROXY		= AF_JOBTYPE_DIRECT_RESTORE_VM_PROXY,	// 17
	AJT_DIRECT_RESTORE_VM_STUB		= AF_JOBTYPE_DIRECT_RESTORE_VM_STUB,	// 18
	AJT_FS_CATALOG_DISABLE_VM		= AF_JOBTYPE_FS_CATALOG_DISABLE_VM,		// 20
	AJT_INTER_MERGE					= AF_JOBTYPE_INTER_MERGE,				// 21
	AJT_RPS_REPLICATION				= AF_JOBTYPE_RPS_REPLICATION,			// 22
	AJT_MERGE                       = AF_JOBTYPE_MERGE,                     // 23 Merge Job
	AJT_CONVERSION                  = AF_JOBTYPE_CONVERSION,                // 40, VSB job
	AJT_START_INSTANTVM				= AF_JOBTYPE_START_INSTANTVM,			// 60, start instant vm job
	AJT_STOP_INSTANTVM				= AF_JOBTYPE_STOP_INSTANTVM,			// 61, stop instant vm job
	AJT_ASSURED_RECOVERY			= AF_JOBTYPE_ASSURED_RECOVERY,			// 62, assured recovery
	AJT_START_INSTANT_VHD			= AF_JOBTYPE_START_INSTANT_VHD,         // 63, start instant vhd job
	AJT_STOP_INSTANT_VHD			= AF_JOBTYPE_STOP_INSTANT_VHD,          // 64, stop instant vhd job
    AJT_FILECOPY_BACKUP 			= AF_JOBTYPE_FILECOPY_BACKUP,		    // 70, FileCopyBackup job
	//
	// Component specific activity log
	//
	AJT_COMMON		= 1000,     // common activity log, like from web service
	AJT_MOUNT		= 1001,	    // activity log from mounting driver
	AJT_AUTOUPDATE	= 1002,     // activity log from auto update
	AJT_UNKNOWN     = 0XFFFF,
};

//
// the product type of each activity log
//
enum ACTLOG_PRODUCT_TYPE
{
	APT_UNKNOWN   = 0, 
	APT_D2D       = 1,  // activity log from D2D
	APT_CPM       = 2,  // activity log from central apps
	APT_RPS       = 3,  // activity log from RPS
};

//
// the job context of activity log. You should update job context before writing activity logs
//
#define MAX_THREAD_CTXS					1024
#pragma pack(1)
//
// the job context defination used to write activity log
//
struct JOBCTX
{
	ULONG				   jobID;			// the job ID
	DWORD                  jobType;         // the job type, see ACTLOG_JOB_TYPE
	DWORD                  prdType;         // the product type, see ACTLOG_PRODUCT_TYPE
	WCHAR				   vmUUID[512];     // the VM instance ID for HBBU  
	JOBCTX( ){
		jobID = 0;
		jobType = AJT_UNKNOWN;
		prdType = APT_UNKNOWN;
		ZeroMemory(vmUUID, sizeof(vmUUID) );
	}
	JOBCTX( const JOBCTX& other){
		jobID = other.jobID;
		jobType = other.jobType;
		prdType = other.prdType;
		memcpy_s( vmUUID, sizeof(vmUUID), other.vmUUID, sizeof(vmUUID) );
	}
	JOBCTX& operator = ( const JOBCTX& other){
		jobID = other.jobID;
		jobType = other.jobType;
		prdType = other.prdType;
		memcpy_s( vmUUID, sizeof(vmUUID), other.vmUUID, sizeof(vmUUID) );
		return (*this);
	}
};
//
// the job context defination of a thread, used to write activity log associated with specific thread
//
struct JOB_THREAD_CTX : JOBCTX
	{
	DWORD  threadID;
	JOB_THREAD_CTX( ){
		threadID = 0;
	}
	JOB_THREAD_CTX( const JOB_THREAD_CTX& other){
		threadID = other.threadID;
		jobID = other.jobID;
		jobType = other.jobType;
		prdType = other.prdType;
		memcpy_s( vmUUID, sizeof(vmUUID), other.vmUUID, sizeof(vmUUID) );		
	}
	JOB_THREAD_CTX& operator = ( const JOB_THREAD_CTX& other){
		threadID = other.threadID;
		jobID = other.jobID;
		jobType = other.jobType;
		prdType = other.prdType;
		memcpy_s( vmUUID, sizeof(vmUUID), other.vmUUID, sizeof(vmUUID) );
		return *this;
	}
};
//
// the job context defination of a process, used to write activity log associated with specific process
//
struct JOB_PROCESS_CTX
{
	JOBCTX             jobCtx;
	JOB_THREAD_CTX     threadCtx[MAX_THREAD_CTXS];

	JOB_PROCESS_CTX( ){
		ZeroMemory( threadCtx, sizeof(threadCtx) );
	}
	JOB_PROCESS_CTX( const JOB_PROCESS_CTX& other){
		memcpy_s( &jobCtx, sizeof(jobCtx), &(other.jobCtx), sizeof(jobCtx) );	
		memcpy_s( threadCtx, sizeof(threadCtx), other.threadCtx, sizeof(threadCtx) );
	}
	JOB_PROCESS_CTX& operator = ( const JOB_PROCESS_CTX& other){
		memcpy_s( &jobCtx, sizeof(jobCtx), &(other.jobCtx), sizeof(jobCtx) );	
		memcpy_s( threadCtx, sizeof(threadCtx), other.threadCtx, sizeof(threadCtx) );
		return *this;
	}
};
#pragma pack()

typedef JOBCTX*				PJOBCTX;
typedef JOB_THREAD_CTX*		PJOB_THREAD_CTX;
typedef JOB_PROCESS_CTX*	PJOB_PROCESS_CTX;

/*
* the details information of an activity log
*/
typedef struct _ACTLOG_DETAILS
{
	DWORD			dwProductType;                              // the product type - APT_D2D, APT_RPS
	DWORD   		dwJobID;									// the job ID
	DWORD			dwJobType;									// the job type - backup, restore, merge.... 
	DWORD			dwJobMethod;								// the job method - full, incremental
	DWORD			dwLogLevel;									// the log level - AFINFO, AFWARNING, AFERROR
	BOOL            bIsVMInstance;                              // True, when 'wszAgentNodeID' is a vm instance UUID, otherwise it is false.
	wchar_t			wszSvrNodeName[LENGTH_OF_NODENAME];			// the node name of where the job is running       
	wchar_t			wszSvrNodeID[LENGTH_OF_NODEID];				// the node ID of where the job is running
	wchar_t			wszAgentNodeName[LENGTH_OF_NODENAME];		// the node name of what the job is running for
	wchar_t			wszAgentNodeID[LENGTH_OF_NODEID];			// the node ID of what the job is running for	
	wchar_t			wszSourceRPSID[LENGTH_OF_NODEID];			// the node ID of the source RPS ( for Replication job only )
	wchar_t			wszTargetRPSID[LENGTH_OF_NODEID];			// the node UUID of the destination RPS ( for Replication job and backup job to RPS )
	wchar_t			wszDSUUID[LENGTH_OF_NODEID];			    // the data store UUID of RPS
	wchar_t			wszTargetDSUUID[LENGTH_OF_NODEID];			// the data store UUID of destination RPS ( for Replication job only )
	wchar_t         wszPlanUUID[LENGTH_OF_NODEID];			    // the plan UUID
	wchar_t         wszTargetPlanUUID[LENGTH_OF_NODEID];		// the target plan UUID ( for Replication job only )

	_ACTLOG_DETAILS()
	{
		dwProductType=APT_D2D;
		dwJobID = 0;
		dwJobType = AJT_COMMON;
		dwJobMethod = 0;
		dwLogLevel = AFINFO;
		bIsVMInstance = FALSE;
		ZeroMemory( wszSvrNodeName,sizeof(wszSvrNodeName) );
		ZeroMemory( wszSvrNodeID,sizeof(wszSvrNodeID) );
		ZeroMemory( wszAgentNodeName,sizeof(wszAgentNodeName) );
		ZeroMemory( wszAgentNodeID,sizeof(wszAgentNodeID) );
		ZeroMemory( wszSourceRPSID,sizeof(wszSourceRPSID) );
		ZeroMemory( wszTargetRPSID,sizeof(wszTargetRPSID) );
		ZeroMemory( wszDSUUID,sizeof(wszDSUUID) );
		ZeroMemory( wszTargetDSUUID,sizeof(wszTargetDSUUID) );
		ZeroMemory( wszPlanUUID, sizeof(wszPlanUUID) );
		ZeroMemory( wszTargetPlanUUID, sizeof(wszTargetPlanUUID) );
	}
}ACTLOG_DETAILS, *PACTLOG_DETAILS;

#define	ACTLOG_VERSION					L"R5.0"
#define SYNC_ACTIVITY_LOG_FORMAT		L"%s\t%d\t%I64d\t%I64d\t%I64d\t%d\t%d\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s$$$\r\n"

/*
*  APIs to read / write job context.
*/ 
DWORD UpdateJBContext( const PJOBCTX pCtx, DWORD dwProcessId=0 );

DWORD UpdateJBContextOfThread( const PJOBCTX pCtx, DWORD dwThreadId=0 );

DWORD GetJBContext( PJOBCTX pCtx );

/*
*  APIs to write activity log 
*/ 

// ------------------------------------------------------- 
// It is strongly to use following APIs to write activity log. 
// With them you dont have to care about the Job NO, Product Type, Job Type, VM UUID....
// NOTE: To use them you need to update job context in your process firstly, so that 
//       each activity log will be associated with the job context.
// ------------------------------------------------------- 
DWORD EActLog( DWORD Flags, DWORD ResouceID, ...);

DWORD EActLogEx( DWORD Flags, DWORD ResouceID, va_list Start);

DWORD EActLogMessage( DWORD Flags, LPCWSTR lpszMsg );

/*
*  Write activity log with the detailed information. This routinue does not depend on the global job context.
*  Normally, it can be used to write activity log in web service or multiple jobs run in one single EXE
*/
DWORD LogActivityWithDetails( DWORD dwFlags, DWORD dwPrdType, DWORD dwJobType, DWORD dwJobID, WCHAR* nodeUUID, DWORD dwResourceID, ... );

DWORD LogActivityWithDetails2( PACTLOG_DETAILS pLogDetails, DWORD dwResourceID, ... );

DWORD LogActivityWithDetailsEx( DWORD dwFlags, DWORD dwPrdType, DWORD dwJobType, DWORD dwJobID, WCHAR* nodeUUID, DWORD dwResourceID, va_list Start );

DWORD LogActivityWithDetailsEx2( PACTLOG_DETAILS pLogDetails, DWORD dwResourceID, va_list Start );

// -------------------------------------------------------
// The legacy APIs  to write acitivity logs
// -------------------------------------------------------
DWORD LogActivity(DWORD Flags, DWORD ResouceID, ...);

DWORD JobLogActivity(DWORD Flags, DWORD JobNo, DWORD ResouceID, ...);

DWORD VMLogActivity(WCHAR* nodeUUID,DWORD Flags, DWORD ResouceID, ...);

DWORD VMJobLogActivity(WCHAR* nodeUUID,DWORD Flags, DWORD JobNo, DWORD ResouceID, ...);

DWORD JobLogActivityEx(WCHAR* nodeUUID,DWORD Flags, DWORD JobNo, DWORD ResouceID, va_list Start);

DWORD AutoupdateLogActivity(DWORD Flags, DWORD ResouceID, va_list Start);

DWORD AutoupdateLogActivityMSG(DWORD Flags, WCHAR * in_LogMsg);

void JobLogSetMask(const DWORD dwMask);
DWORD JobLogGetMask();

#define JOB_MASK_DEFAULT		0x00
#define JOB_MASK_NO_LOG			0x01  // don't show any active log
/*
*  APIs to get activity log 
*/
//
// get the activity logs of where is job is running.
// nodeUUID: it is the node id ( or vm instance uuid of where the job is running )
//           if node UUID is NULL or Empty, it is to get the activity logs running on current UDP agent node
//
int  GetLog( DWORD dwProdType, DWORD JobNo, ULONGLONG ullStart, ULONGLONG ullRequest, PFLASHDB_ACTIVITY_LOG* ppLogs, ULONGLONG *pUllCnt, ULONGLONG *pUllTotalCnt, WCHAR* nodeUUID = NULL );

//
// get the activity log of what is the job is running for.
// nodeUUID: it is the node if ( or vm instance UUID of what is the job is running for )
//			 the nodeUUID MUST NOT be empty of NULL
//
int  GetLogOfAgent( DWORD dwProdType, DWORD JobNo, ULONGLONG ullStart, ULONGLONG ullRequest, PFLASHDB_ACTIVITY_LOG* ppLogs, ULONGLONG *pUllCnt, ULONGLONG *pUllTotalCnt, WCHAR* agentNodeUUID );

int  PrepareSyncLogOfCPM( DWORD dwProdType, WCHAR* nodeUUID, PSyncLogFiles* pSyncLogFiles, UINT* nCnt);

int  PrepareSyncLogOfVCM( DWORD dwProdType, WCHAR* nodeUUID, PSyncLogFiles* pSyncLogFiles, UINT* nCnt);

int  PrepareFullSyncLogOfCPM( DWORD dwProdType, WCHAR* nodeUUID, PSyncLogFiles* pSyncLogFiles, UINT* nCnt);

int  PrepareFullSyncLogOfVCM( DWORD dwProdType, WCHAR* nodeUUID, PSyncLogFiles* pSyncLogFiles, UINT* nCnt);

int  PrepareSyncJobHistoryOfCPM( DWORD dwProdType, PSyncLogFiles* pSyncLogFiles, UINT* nCnt);

int  PrepareFullSyncJobHistoryOfCPM( DWORD dwProdType, PSyncLogFiles* pSyncLogFiles, UINT* nCnt);

void FreeSyncLogFiles( PSyncLogFiles pSyncLogFiles );

/*
*  Misc APIs
*/
void FreeLog(PFLASHDB_ACTIVITY_LOG pLog);

int  PruneLog( DWORD dwProdType, SYSTEMTIME utcTime, WCHAR* nodeUUID = NULL);

DWORD JobLoadString( DWORD ResouceID, TCHAR* szBuf, DWORD dwBufCount);

DWORD SetJobTypeForLog(UINT jobType,UINT ProxyD2DLanguageID = 0, void* pReserved = NULL);

DWORD UpdateNodeNameInActivityLog( DWORD dwPrdType, DWORD dwJobID, const WCHAR * newNodeName );

DWORD VM_Direct_Restore_Merge_VMACTLOG(WCHAR* nodeUUID);

DWORD VM_Direct_Restore_Log_VM_ActLog(DWORD Flags, DWORD JobNo, WCHAR *str, WCHAR *newNodeName);

DWORD GetWindowsErrorMsg( DWORD dwErr, LANGID lid, LPWSTR pszMessage, DWORD* pdwSizeInCharacters );

/*
*  APIs for job history
*/
DWORD StartNewJob( DWORD dwProdType, PFLASHDB_JOB_HISTORY pJobHistory );

// lpszJobDetails: must be a base64 encoded string
DWORD MarkJobEnd( DWORD dwProdType, ULONGLONG ullJobID, DWORD dwJobStatus, LPCWSTR lpszJobDetails=NULL );

// lpszJobDetails: must be a base64 encoded string
DWORD MarkJobEndEx( DWORD dwJobStatus, LPCWSTR lpszJobDetails=NULL ); // use the job context to get job id and product type

DWORD UpdateJobMethodOfJobHistory( DWORD dwProdType, ULONGLONG ullJobID, DWORD dwJobMethod );

DWORD UpdateJobMethodOfJobHistoryEx( DWORD dwJobMethod );

DWORD UpdateJobDetails( DWORD dwProdType, ULONGLONG ullJobID, void* pJobDetails, DWORD dwBufSize );

DWORD UpdateJobDetailsEx( void* pJobDetails, DWORD dwBufSize );

DWORD GetJobDetails( DWORD dwProdType, ULONGLONG ullJobID, void* pJobDetails, DWORD* pdwBufSize );

DWORD GetJobHistory( DWORD dwProdType, ULONGLONG ullJobID, PFLASHDB_JOB_HISTORY pHistory );

DWORD GetJobHistories( DWORD dwProdType, ULONGLONG ullStart, ULONGLONG ullRequest, 
		                            PFLASHDB_JOB_HISTORY_FILTER_COL pFilter, 
									ULONGLONG* pUllCnt, 
									ULONGLONG* pUllTotal, 
									PFLASHDB_JOB_HISTORY* pHistories );

/*
*  APIs to sync up activity logs 
*/ 
int		SwitchSyncLog4Trans();

int		GetNextSyncLogs(UINT nRequest, PActiveSyncLog *pLog, UINT *nCnt);

void	FreeSyncLog(PActiveSyncLog pLog);

void	GetLogSyncFileTransXMLName(WCHAR* fileSyncTransXMLLog, int length);

void	GetLogSyncFileTransName(WCHAR* fileSyncTransLog, int length);

void	GetLogSyncFileTransNameFull(WCHAR* fileSyncTransLogFull, int length);

int		GetNextSyncLogsFull(UINT nRequest, PActiveSyncLog *pLog, UINT *nCnt);

DWORD	MergeAllActiveLog2LogTransFull();

DWORD	GetLastMessage( DWORD dwLevel, LPWSTR pszBuffer, DWORD* pdwSizeOfCharacters );

/*
* Defines and APIs to sync up data
*/
enum SYNC_DATA_TO
{
	SYNC_DATA_TO_NONE	= 0, 
	SYNC_DATA_TO_CPM	= 1,  
	SYNC_DATA_TO_VCM	= 2,  
	SYNC_DATA_TO_RPS	= 3,  
};

enum SYNC_DATA_FROM
{
	SYNC_DATA_FROM_UNKNOWN	= 0, 
	SYNC_DATA_FROM_D2D		= APT_D2D,  
	SYNC_DATA_FROM_CPM		= APT_CPM,  
	SYNC_DATA_FROM_RPS		= APT_RPS, 
};

enum SYNC_DATA_TYPE
{
	SYNC_DATA_UNKNOWN				= 0,
	SYNC_DATA_REPLICATION			= 1,	// sync replication data to CPM
	SYNC_DATA_ACTIVITYLOG			= 2,	// sync activity log to cpm / vcm
	SYNC_DATA_JOBHISTORY			= 3,	// sync job history to cpm
	SYNC_DATA_FULL_ACTIVITYLOG		= 4,	// sync full data of activtity logs to cpm / vcm
	SYNC_DATA_FULL_JOBHISTORY		= 5,	// sync full data of job history to cpm
	SYNC_DATA_UPDATE_ACTLOG			= 6,	// sync activity log of auto update
	SYNC_DATA_RECOVERYPOINT_INFO	= 7,	// sync recovery point infor
};

typedef struct _DATASYNC_ARGS
{
	DWORD	dwSyncTo;						//where the data sync to. see SYNC_DATA_TO
	DWORD	dwSyncFrom;						//where the sync data from, that is where the job is running. see SYNC_DATA_FROM
	DWORD	dwSyncType;						//what is the sync data for, see SYNC_DATA_TYPE
	wchar_t	szNodeUUID[LENGTH_OF_NODEID];	//the node uuid of what the job is running for.

	_DATASYNC_ARGS()
	{
		dwSyncTo=SYNC_DATA_TO_NONE;
		dwSyncFrom = SYNC_DATA_FROM_UNKNOWN;
		dwSyncType = SYNC_DATA_UNKNOWN;
		ZeroMemory( szNodeUUID,sizeof(szNodeUUID) );
		wcsncpy_s( szNodeUUID, _countof(szNodeUUID), L"NULL", _TRUNCATE );
	}
}DATASYNC_ARGS, *PDATASYNC_ARGS;

// sync a file. In this function, it just copy the file to a specified folder.
// szFilePath : the full path.
DWORD DataSyncFile(const DATASYNC_ARGS &dataSyncArgs, const WCHAR* szFilePath);

//sync a record. It doesn't do anything for the input buffer and just add the record to a specified file.A record is a line in the file.
DWORD DataSyncRecord(const DATASYNC_ARGS &dataSyncArgs, const WCHAR* szRecord);

DWORD GetDataSyncFolder( const DATASYNC_ARGS &dataSyncArgs, WCHAR* szFolder, DWORD dwSizeInCharacters);

//Get a file list which will be sync. It is called by web service.
//pSyncLogFiles: the file list which will be sync.
//nCnt : the count of the file list.
DWORD PrepareSyncData(const DATASYNC_ARGS &dataSyncArgs, PSyncLogFiles* pSyncLogFiles, UINT* nCnt);

#endif