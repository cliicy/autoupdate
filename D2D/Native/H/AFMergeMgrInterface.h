#pragma once

#ifdef MERGEMGRDLL_EXPORTS
#define DLLZZ __declspec(dllexport)
#else
#define DLLZZ __declspec(dllimport)
#endif

#ifndef WINAPI
#define WINAPI __stdcall
#endif

#include <Windows.h>
#include "Log.h"
#include "JobMonitor.h"
#include "DbgLog.h"
#include <string>
#include <vector>
using namespace std;

/////////////////////////////////////////////////////////////////////////////////////////////////////
#define MERGE_JOB_IPC_OBJECT_NAME_FORMAT  	                   L"Global\\AFMergeJMName$%08X$"
#define MERGE_MGR_DLL_MODULE_NAME                              L"MergeMgrDll"
#define DEFAULT_JOB_ID_MERGE_PROCESS                           0xFFFFFFFC
#define DEFAULT_SESS_TYPE_FOR_MERGE_JOB                        EST_LOCAL_D2D
//#define DEFAULT_MERGE_JOB_METHOD                               EMM_INC_2_FUL
#define DEFAULT_MERGE_JOB_METHOD                               EMM_MULTI_INC_2H
#define DEFAULT_MERGE_JOB_OPTION                               EMO_NONE
#define USE_DEBUG_LOG_PER_JOB                                  1

#define JOB_RETURN_IF_STOPPED_EX(JobMonPtr)                    if (AFJMIsJobCtrl(JobMonPtr, EMO_STOP)) return EJS_JOB_STOPPED
#define JOB_BREAK_IF_STOPPED_EX(JobMonPtr)                     if (AFJMIsJobCtrl(JobMonPtr, EMO_STOP)) break
#define JOB_RETURN_CODE_IF_STOPPED_EX(JobMonPtr, ErrCode)      if (AFJMIsJobCtrl(JobMonPtr, EMO_STOP)) { ErrCode = D2DMERGE_E_JOB_IS_STOPPED_OR_CACELED; return ErrCode; }
#define JOB_BREAK_CODE_IF_STOPPED_EX(JobMonPtr, ErrCode)       if (AFJMIsJobCtrl(JobMonPtr, EMO_STOP)) { ErrCode = D2DMERGE_E_JOB_IS_STOPPED_OR_CACELED; break; }

#define SET_JM_PHASE(JobMonitor, Phase)                        if (JobMonitor) JobMonitor->stWField.stMergeStatus.dwMergePhase = (Phase)
#define SET_JM_STATUS(JobMonitor, Status)                      if (JobMonitor) JobMonitor->stWField.stMergeStatus.dwJobStatus = (Status)

#define ACTLOG_JS(MergeJS, LogLvl, ResID, ...)                 AFJMActLog((MergeJS).wsVMGUID.c_str(), LogLvl, (MergeJS).dwJobID, ResID, __VA_ARGS__)

// wansh11
// Define some valuable error code to indicate the merge job errors.
#define E_MERGE_JOB_FAILED_TO_INIT_BACKUP_DESTINATION          -1000
// END

//ZZ: We will reserve memory for session ranges in job monitor. 
#define MAX_SESS_RANGE_COUNT_IN_JOB_MONITOR                    10

/////////////////////////////////////////////////////////////////////////////////////////////////////
///ZZ: Definition of some enumeration value.
///ZZ: This enumeration must be kept consistent with Web Service side
typedef enum
{
    EJR_JOB_UNKNOWN		= JS_ACTIVE,	///ZZ: Unknown status, usually exist when job monitor is just initialized. 
    EJR_JOB_FINISH		= JS_FINISHED,  ///ZZ: Job finishes successfully
    EJR_JOB_FAILED		= JS_FAILED,    ///ZZ: Job failed because some internal error.
    EJR_JOB_STOPPED		= JS_STOPPED,   ///ZZ: Job is stopped by user or other job.
    EJR_JOB_SKIPPED		= JS_SKIPPED,   ///ZZ: Job is skipped because no need to merge.
	EJR_JOB_FAILLOCK	= JS_FAILLOCK,  ///ZZ: Job is skipped because failed to lock session, it is a bit different from EJR_JOB_SKIPPED
    EJR_JOB_CRASH       = JS_CRASHED    ///ZZ: Job crashes. We consider no EJP_END_OF_JOB phase when process exit.
}E_JOB_RESULT;

typedef enum
{
    EJP_UNKNOWN = 0,            ///ZZ: Unknown phase, usually exist when job monitor is just initialized.  
    EJP_PROC_ENTER,             ///ZZ: Enter merge job process.
    EJP_INIT_BKDEST,            ///ZZ: Merge job is initializing backup destination.
    EJP_ENUM_SESS,              ///ZZ: Merge job is enumerating session to decide if any session should be merged.
    EJP_CONTINUE_FAILED_MERGE,  ///ZZ: Repair sessions which are merged failed in last merge job.
    EJP_LOCK_SESS,              ///ZZ: Lock session for merge.
    EJP_WAIT_4_LOCK,            ///ZZ: Session is used by other operation, wait session lock for merge.
    EJP_MERGE_SESS,             ///ZZ: Merge session data.
    EJP_MERGE_PREPROCESS,       ///ZZ: Pre-process session data for merge.
    EJP_MERGE_DISK_INIT,        ///ZZ: Initialization of merging disk.
    EJP_MERGE_DISK_DATA,        ///ZZ: Merging data.
    EJP_UNINIT_BKDES,           ///ZZ: Un-initialize backup destination, including cut exist network connection.
    EJP_WAIT_STOP,              ///ZZ: Merge job receive stop command and release resource before exit.
    EJP_END_OF_JOB,             ///ZZ: Merge job is end and process will exit soon.
    EJP_PROC_EXIT,              ///ZZ: Merge job process exit.
}E_JOB_PHASE;

typedef enum
{
    EMP_UNKNOWN = 0,
    EMP_MERGE_PREPROCESS,       ///ZZ: Pre-process session data for merge.
    EMP_MERGE_DISK_INIT,        ///ZZ: Initialization of merging disk.
    EMP_MERGE_DISK_DATA,        ///ZZ: Merging data.
    EMP_MERGE_DISK_UNINIT       ///ZZ: UnInitialization of merge data.
}E_MERGE_PHASE;

///ZZ: Low 16bit indicates normal operation, while high 16 bit indicates extra option.
typedef enum
{
    EMO_NONE = 0,                     ///ZZ: Do nothing.
    EMO_START,                        ///ZZ: Merge option: start merge job.
    EMO_STOP,                         ///ZZ: Merge option: stop merge job.
    EMO_CANCEL,                       ///ZZ: Merge option: cancel merge job.
    EMO_PAUSE,                        ///ZZ: Merge option: pause merge job.
    EMO_RESUME,                       ///ZZ: Merge option: resume merge job.
    EMO_MERGE_BKSET,                  ///ZZ: Merge option: merge backup set.
    EMO_MERGE_LATEST_PLAN = 0x10000   ///ZZ: By default merge will only continue incomplete merge if last merge is stopped. With this option merge will be based on changed merge plan.
}E_MERGE_OPT;

typedef enum
{
    EMM_1_SESS = 0,     ///ZZ: Merge method: merge multiple session one by one in a merge job. Other value will merge multiple session data in same time.
    EMM_INC_2_FUL,      ///ZZ: Merge method: merge incremental session to full session. 
    EMM_MULTI_INC_2H,   ///ZZ: Merge method: merge multiple sessions, may include full session, to latest session in merge range. 
    EMM_MULTI_INC_2L,   ///ZZ: Merge method: merge multiple sessions, may include full session, to oldest session in merge range.
    EMM_RMV_SESS,       ///ZZ: Merge method: remove session range begin with full session for backup set or some special use.
    EMM_SESS_RANGES     ///ZZ: Merge method: merge one or multiple session ranges in one merge job. Only supported in advanced session format.
}E_MERGE_METHOD;

typedef enum
{
    EMF_NO_FLAGS = 0,
    EMF_ONLY_MERGE_REPLICATED_SESS = 0x00000001,
    EMF_ONLY_FINISHED_SESS = 0x00000002,
    EMF_ENUM_ALL_SESS_AS_RESULT = 0x00000004          
}E_MERGE_FLAGS;

typedef enum
{
    EST_LOCAL_D2D = 1,   ///ZZ: Session backed up by local D2D
    EST_VSPHERE          ///ZZ: Session backed up by vsphere
}E_SESS_TYPE;

//ZZ: Low 16 bits indicate job type, such as merge, backup and so on, while high 16 bit is control option.
//ZZ: So far we only define one control option to specify if job is running for RPS.
typedef enum
{
    EJT_AGENT_SIDE_JOB = 0,
    EJT_MERGE = 0x00000001,
    EJT_RPS_SIDE_JOB = 0x00010000
}E_JOB_TYPE;

typedef enum
{
    EJR_READY_RUN = 0,           //ZZ: Merge job should be launched.
    EJR_NONEEED_RUN = 2012,      //ZZ: It is no need to launch merge job.
    EJR_EXCEED_JOB_COUNT,        //ZZ: Exceed maximum merge job count allowed in parallel. This merge job will be missed. 
    EJR_NO_ENOUGH_FREE_SPACE,    //ZZ: No enough free space on destination for merge.
    EJR_SKIP_MERGE_INTERNAL_ERR, //ZZ: Meet some error when check if merge job is available. For safe, we skip merge job.
    EJR_SKIP_MERGE_4_OTHER_JOB,  //ZZ: Merge job cannot be launched because some sessions are still used for other job in future.
    EJR_SKIP_MERGE_WHEN_NO_BK    //ZZ: Merge job should not be launched because there is no valid session.
}E_JOB_READY_TYPE;

typedef enum 
{
    EMOP_UNKNOWN = 0,
    EMOP_MERGE_DATA,
    EMOP_RMV_DIRECTLY
}E_MERGE_OP;

// Begin [8/19/2014 zhahu03]
typedef enum
{
	EAEF_DISABLED_ALL = 0,
	EAEF_DAILY_ENABLE = 0x00000001,
	EAEF_WEEKLY_ENABLE = 0x00000002,
	EAEF_MONTHLY_ENABLE = 0x00000004,
	EAEF_CUSTOM_ENABLE = 0x00000008
}E_ARCHIVE_ENABLE_FLG;
// END [8/19/2014 zhahu03] 

typedef struct _stMergeParam
{
    ULONGLONG ullBytesMerged;
    ULONGLONG ullBytes2Merge;
    DWORD     dwStartSess;
    DWORD     dwEndSess;
    DWORD     dwSessCnt;
    DWORD     dwMergeOP;

    bool operator < (const _stMergeParam& stMergeRange) const { return dwStartSess < stMergeRange.dwStartSess; }
}ST_MERGE_PARM;

typedef std::vector<ST_MERGE_PARM> MergeParamVector;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
///ZZ: Job monitor definition for control merge job.
typedef struct _stReadField
{
    DWORD dwJobID;                   ///ZZ: Job ID assigned to merge job, which is also used to compose share memory name.
    DWORD dwMergeOpt;                ///ZZ: Merge option. Refer to E_MERGE_OPT
    DWORD dwMergeMethod;             ///ZZ: Which merge logic will be used to merge. Refer to E_MERGE_METHOD.
    DWORD dwRetentionCnt;            ///ZZ: Retention count. Depend on dwOperateType, this value may indicate session count or backup set count.
    DWORD dwDailyCnt;                ///ZZ: Daily backup count to keep.
    DWORD dwWeeklyCnt;               ///ZZ: Weekly backup count to keep.
    DWORD dwMonthlyCnt;              ///ZZ: Monthly backup count to keep.
    DWORD dwSessStart;               ///ZZ: Start session number to be merged.
    DWORD dwEndStart;                ///ZZ: End session number to be merged.
	DWORD dwPrdType;                 ///ZZ: The product type of merge job ( RPS or D2D ), used to write activity log when merge job crashed
    WCHAR wzD2DIdentity[MAX_PATH];   ///ZZ: Unique string to indicate D2D server.
    WCHAR wzD2DAgentName[MAX_PATH];  ///ZZ: D2D agent name.
}ST_R_FIELD, *PST_R_FIELD;

typedef struct _stMergeStatus
{
    DWORD         dwProcessID;           ///ZZ: Process ID of merge job.
    DWORD         dwJobStatus;           ///ZZ: Job status. Indicate if merge job is successful, failed, stopped and so on. Refer to E_JOB_STATUS
    DWORD         dwMergePhase;          ///ZZ: Which kind stage of merge, such as lock session, merge data and so on. Refer to E_JOB_PHASE
    DWORD         dwSessCnt2Merge;       ///ZZ: How many sessions will be merged.
    DWORD         dwSessCntMerged;       ///ZZ: How many sessions have been merged.
    DWORD         dwCurSess2Merge;       ///ZZ: Current session number which is being merged.
    DWORD         dwDiskCnt2Merge;       ///ZZ: How many disks will be merged in one session
    DWORD         dwDiskCntMerged;       ///ZZ: How many disks have been merged for some session.
    DWORD         dwCurDiskSig2Merge;    ///ZZ: Disk signature of disk being merged/
    ULONGLONG     ullJobStartTime;       ///ZZ: Start time of merge job, in seconds. When web service read it out from job monitor it is converted to milliseconds in JNI.
    ULONGLONG     ullDiskBytes2Merge;    ///ZZ: How much data on disk to merge.
    ULONGLONG     ullDiskBytesMerged;    ///ZZ: How much data on disk has been merged
    ULONGLONG     ullSessBytes2Merge;    ///ZZ: How much data in session to merge.
    ULONGLONG     ullSessBytesMerged;    ///ZZ: How much data in session has been merged.
    ULONGLONG     ullTotalBytes2Merge;   ///ZZ: Total data size to merge in this job.
    ULONGLONG     ullTotalBytesMerged;   ///ZZ: Total data size has been merged.
    float         fMergePercentage;      ///ZZ: Percentage of merge.
    DWORD         dwSessRangeCnt;        ///ZZ: How many session range to merge. For variable retention feature, we should merge multiple session range in one merge job.
    DWORD         dwSessRangeDoneCnt;    ///ZZ: How many session range has been merged completely.
    DWORD         dwSessRangeInList;     ///ZZ: How many session range store into stSessRangeList.
    ST_MERGE_PARM stSessRangeList[MAX_SESS_RANGE_COUNT_IN_JOB_MONITOR + 1];   ///ZZ: Partial or whole session range list. The first element store current session range.
}ST_MERGE_STATUS;

typedef struct _stWriteField
{
    ST_MERGE_STATUS stMergeStatus;
}ST_W_FIELD, *PST_W_FIELD;

typedef struct _stMergeControl
{
    ST_R_FIELD stRField;  
    ST_W_FIELD stWField; 
}ST_MERGE_CTRL, *PST_MERGE_CTRL;


class IJobMonInterface
{
public:
    virtual bool           IsNewJobMon() = 0;
    virtual DWORD          GetJobType() = 0;
    virtual PST_MERGE_CTRL MergeJobMon() = 0;
    virtual long           StopJob() = 0;
    virtual void           Release() = 0;
};

class IJobMgrInterface
{
public:
    virtual DWORD GetJobType() = 0;
    virtual PST_MERGE_CTRL GetJobMon() const = 0;
    virtual long  RepairSessionForFailedMerge(DWORD* pdwDealSessCount = NULL, DWORD* pdwStatus = NULL) = 0;
    virtual long  RepairSessionForFailedBackup(DWORD* pdwDealSessCount = NULL) = 0;
    virtual long  MergeSession(DWORD* pdwDealSessCount = NULL) = 0; 
    virtual void  Release() = 0;
};

typedef struct _stActiveJob
{
    DWORD dwProcID;                 ///ZZ: Process ID of active merge job.
    DWORD dwJobID;                  ///ZZ: Job ID of active merge job.
    DWORD dwJobType;                ///ZZ: Job type. Refer to E_JOB_TYPE
    WCHAR wzVMGUID[MAX_PATH];       ///ZZ: VM GUID of vsphere backup
    WCHAR wzJSPath[MAX_PATH];       ///ZZ: Current location of merge job script.
    WCHAR wzD2DIdentity[MAX_PATH];  ///ZZ: Unique string to indicate D2D server.
}ST_ACTIVE_JOB;

typedef std::vector<ST_ACTIVE_JOB> ActJobVector;

/////////////////////////////////////////////////////////////////////////////////////////////////////
///ZZ: Definition of job script.
class CPathInfo
{
public:
    CPathInfo() { Clear(); }
    void Clear()
    {
        wsFolderPath.clear();
        wsDomainName.clear();
        wsUserName.clear();
        wsUserPWD.clear();
    }
    wstring wsFolderPath;
    wstring wsDomainName;
    wstring wsUserName;
    wstring wsUserPWD;
};

class CMergeJS
{
public:
    CMergeJS()  { Clear(); }
    void Clear()
    {
        bDoImpersonate = false;
        dwJobID = DEFAULT_JOB_ID_MERGE_PROCESS;  
        dwSessType = DEFAULT_SESS_TYPE_FOR_MERGE_JOB;
        dwMergeOpt = DEFAULT_MERGE_JOB_OPTION;     
        dwMergeMethod = DEFAULT_MERGE_JOB_METHOD;  
        dwRetentionCnt = 0; 
        dwDailyCnt = 0;  
        dwWeeklyCnt = 0; 
        dwMonthlyCnt = 0;
        dwStartSess = 0;    
        dwEndSess = 0;     
        dwCryptoInfo = 0;
        dwCompressInfo = 0;
		dwPrdType = APT_D2D;
        dwMergeFlags = EMF_NO_FLAGS;
        wsVMGUID.clear();
        wsSessPWD.clear();
        wsD2DIdentity.clear();
        wsD2DAgentName.clear();
        BackupDest.Clear();
		wsRpsSvrName.clear();
		wsRpsSvrGUID.clear();
		wsDsDisplayName.clear();
		wsDsGUID.clear();
		dwArchiveDays = 0;
		dwArchiveTypeFlag = 0;
		llArchiveScheduleTime = 0;
    }

    bool      bDoImpersonate;  ///ZZ: If this job need do impersonation before launch job.
    DWORD     dwJobID;         ///ZZ: Job ID assigned to merge job, which is also used to compose share memory name.
    DWORD     dwSessType;      ///ZZ: Session type to distinguish local D2D session from vsphere session. Refer to E_SESS_TYPE.
    DWORD     dwMergeOpt;      ///ZZ: Merge option. Refer to E_MERGE_OPT
    DWORD     dwMergeMethod;   ///ZZ: Which merge logic will be used to merge. Refer to E_MERGE_METHOD.
    DWORD     dwRetentionCnt;  ///ZZ: Retention count. Depend on dwOperateType, this value may indicate session count or backup set count.
    DWORD     dwDailyCnt;      ///ZZ: Daily session count used to merge. 
    DWORD     dwWeeklyCnt;     ///ZZ: Weekly session count used to merge.
    DWORD     dwMonthlyCnt;    ///ZZ: Monthly session count used to merge.
    DWORD     dwStartSess;     ///ZZ: Start session number to be merged.
    DWORD     dwEndSess;       ///ZZ: End session number to be merged.
    DWORD     dwCryptoInfo;    ///ZZ: Encryption information. High-16 bit indicate encryption crypto library, which low-16 bit indicate algorithm.
    DWORD     dwCompressInfo;  ///ZZ: Compression information.
    DWORD     dwMergeFlags;    ///ZZ: Some extend control flag for merge. Refer to E_MERGE_FLAGS
	DWORD     dwPrdType;       ///ZZ: The product type of merge job. Used to determine write activity log to RPS or D2D
    wstring   wsVMGUID;        ///ZZ: For vsphere session we need vm GUID to identity VM host.
    wstring   wsSessPWD;       ///ZZ: Session password if encrypted.
    wstring   wsD2DIdentity;   ///ZZ: Unique string to indicate D2D server.
    wstring   wsD2DAgentName;  ///ZZ: Agent name of D2D.
    CPathInfo BackupDest;      ///ZZ: Backup destination information. If it is remote shared folder we need user name and password.
	wstring   wsRpsSvrName;    ///ZZ: the RPS server name
	wstring   wsRpsSvrGUID;	   ///ZZ: the RPS server GUID       
	wstring   wsDsDisplayName; ///ZZ: the data store display name
	wstring   wsDsGUID;        ///ZZ: the data store GUID
	DWORD	  dwArchiveDays;		 // [8/20/2014 zhahu03] archive to tape execute day[bit0~bit6:sun~sat]
	DWORD	  dwArchiveTypeFlag;	 // [8/20/2014 zhahu03] archived task enable flag.[Daily/Weekly/Monthly/Custom]
	LONGLONG  llArchiveScheduleTime; // [8/20/2014 zhahu03] archived task schedule applied time
};

class CMergeJobCmdLine
{
public:
    CMergeJobCmdLine() { Clear(); }
    void Clear()
    {
        m_bAutoTest = false;
        m_bShowUsage = false;
        m_bExitConfirm = false;
        m_bDoImpersonate = false;
        m_wsJSPath.clear();
        m_wsJSDumpPath.clear();
    }
    
    bool     m_bAutoTest;
    bool     m_bShowUsage;
    bool     m_bExitConfirm;
    bool     m_bDoImpersonate;
    wstring  m_wsJSPath;
    wstring  m_wsJSDumpPath;
    CMergeJS m_MergeJS;
};

class CSessionInfo
{
public:
    CSessionInfo() { Clear(); }
    void Clear()
    {
        bNeedMerge = false;
        bMerging = false;
        dwBKType = 0;
        dwSessNum = 0;
        dwBKSetFlag = 0;
        dwBKAdvSchFlag = 0;
        dwCryptoInfo = 0;
        ullDataSize = 0;
		ullLogicSize = 0;
		ullCatalogSize = 0;
        dwReplicateStatus = 0;
        dwJobStatus = AF_JOB_STATUS_ACTIVE;
        dwSessStatus = 0;
		BackupUTCTime = 0;
        wsSessFullPath.clear();
        wsCatalogPath.clear();
        wsBKDest.clear();
        wsSessGUID.clear();
    }

    bool operator < (const CSessionInfo& SessInfo) { return (dwSessNum < SessInfo.dwSessNum); }

    bool           bNeedMerge;
    bool           bMerging;
    DWORD          dwBKType;
    DWORD          dwSessNum;
    DWORD          dwBKSetFlag;
    DWORD          dwBKAdvSchFlag;
    DWORD          dwJobStatus;     
    DWORD          dwSessStatus;
    DWORD          dwReplicateStatus;
    wstring        wsSessFullPath;
    wstring        wsCatalogPath;
    wstring        wsBKDest;
    wstring        wsSessGUID;
    DWORD          dwCryptoInfo;
    ULONGLONG      ullDataSize;
	ULONGLONG      ullLogicSize;
	ULONGLONG      ullCatalogSize;
	LONGLONG	   BackupUTCTime;	//In seconds // [8/19/2014 zhahu03]
};

typedef std::vector<CSessionInfo> SessInfoVector;

#ifdef __cplusplus
extern "C"
{
#endif
    DLLZZ long AFJMActLog(
        const WCHAR* pwzVMGUID,
        DWORD        dwLogLvl,
        DWORD        dwJobNum,
        DWORD        dwResID,
        ...
        );

    DLLZZ long AFJMComposeSessRange(
        CMergeJS&         MergeJS,                //ZZ: Merge job script information.
        MergeParamVector* pvecSessMergeRanges,    //ZZ: Get session merge range based on backup destination, retention policy and merge method. Can be NULL.
        SessInfoVector*   pvecAllSessInfo = NULL, //ZZ: Get all session information based on backup destination. Can be NULL.
        bool              bWithDetailLogs = true, //ZZ: If this API print debug logs to dump session information.
        bool              bAcceptConfig = true   //ZZ: If we will consider configuration file of merge when compose merge range. 
        );                 

    //ZZ: When job is launched on RPS side, please OR dwJobType by EJT_RPS_SIDE_JOB.
    DLLZZ IJobMgrInterface* AFJMCreateJobMgr(
        const CMergeJS& JobParam, 
        PST_MERGE_CTRL  pJobMonitor = NULL,
        DWORD*          pdwErrCode = NULL, 
        DWORD           dwJobType = EJT_MERGE
        );  

    //ZZ: When job is launched on RPS side, please OR dwJobType by EJT_RPS_SIDE_JOB.
    DLLZZ IJobMonInterface* AFJMCreateJobMon(
        DWORD  dwJobID, 
        DWORD* pdwErrCode = NULL, 
        DWORD  dwJobType = EJT_MERGE
        );

    DLLZZ long AFJMEndExpHandler();

    DLLZZ long AFJMGetLogNamePerJob(
        WCHAR*    pwzLogNameBuf,
        DWORD     dwLogNameBufLen,    ///ZZ: In character.
        CMergeJS& MergeJS
        );

    DLLZZ CDbgLog& AFJMGetLogger();

    DLLZZ long AFJMGetSessDetails(
        SessInfoVector& vecAllSessInfo,       //ZZ: Session details list.
        const WCHAR*    pwzBKDest,            //ZZ: Backup destination for querying session details
        DWORD           dwSessNum = 0,        //ZZ: Session number to query, if this value is zero, all sessions will be queried.
        DWORD           dwFlag = EMF_NO_FLAGS //ZZ: Filter for session result. Refer to E_MERGE_FLAGS.
        );

    DLLZZ long AFJMInitBackupDest(
        const WCHAR* pwzBKDest,
        const WCHAR* pwzBKUsr,
        const WCHAR* pwzBKPwd,
        bool         bDoImpersonate = false
        );

    DLLZZ bool AFJMIsJobCtrl(
        PST_MERGE_CTRL pJobMonitor, 
        E_MERGE_OPT    eMergeOpt
        );

    ///ZZ: Check if merge can/need be launched. Result value refer to E_JOB_READY_TYPE.
    DLLZZ long AFJMIsJobAvailable(
        const CPathInfo& BKDest, 
        DWORD dwRetentionCnt,
        const WCHAR* pwzVMGUID = NULL,
        const WCHAR* pwzDS4Replication = NULL
        );

    ///ZZ: Check if merge can/need be launched. Result value refer to E_JOB_READY_TYPE.
    DLLZZ long AFJMIsJobAvailableEx(
        const WCHAR* pwzBKDest, 
        DWORD dwRetentionCnt,
        DWORD dwDailyCnt = 0,
        DWORD dwWeeklyCnt = 0,
        DWORD dwMonthlyCnt = 0,
        const WCHAR* pwzDS4Replication = NULL,
        const WCHAR* pwzVMGUID = NULL,
		DWORD	  dwArchiveDays = 0,		 // [8/28/2014 zhahu03] archive to tape execute day[bit0~bit6:sun~sat]
		DWORD	  dwArchiveTypeFlag = 0,	 // [8/28/2014 zhahu03] archived task enable flag.[Daily/Weekly/Monthly/Custom]
		LONGLONG  llArchiveScheduleTime = 0  // [8/28/2014 zhahu03] archived task schedule applied time)
        );

    DLLZZ long AFJMParseCmdLine(
        CMergeJobCmdLine& JobCmdLine,
        DWORD             dwArgCnt, 
        WCHAR**           ppArgList
        );

    DLLZZ long AFJMParseJS(
        CMergeJS&    MergeJS,
        const WCHAR* pwzJSPath
        );

    DLLZZ void AFJMReleaseJobMgr(
        IJobMgrInterface** ppJobMon
        ); 

    DLLZZ void AFJMReleaseJobMon(
        IJobMonInterface** ppJobMon
        );    

    DLLZZ long AFJMRetrieveActiveJob(
        ActJobVector& vecActJob
        );
    
    DLLZZ long AFJMRunMergeJob(
        CMergeJS* pMergeJS,
        const WCHAR* pwzJSPath = NULL
        );

    DLLZZ long AFJMSaveJS(
        CMergeJS&    MergeJS,
        const WCHAR* pwzJSPath);

    //DLLZZ void AFJMSetLogSetting(
    //    const WCHAR* pwzModuleName,
    //    const WCHAR* pwzLogFileName,
    //    bool         bUnicode
    //    );

    DLLZZ void AFJMShowUsage();

    DLLZZ long AFJMStartExpHandler();

    //ZZ: When job is launched on RPS side, please OR dwJobType by EJT_RPS_SIDE_JOB.
    DLLZZ long AFJMStartJob(
        const WCHAR* pwzJSPath, 
        HANDLE*      phJobProc = NULL,
        DWORD*       pdwProcID = NULL,
        const WCHAR* pwzUsrName = NULL, 
        const WCHAR* pwzPassword = NULL,
        DWORD        dwJobType = EJT_MERGE
        );

    //ZZ: When job is launched on RPS side, please OR dwJobType by EJT_RPS_SIDE_JOB.
    DLLZZ long AFJMStopJob(
        DWORD dwJobID,
        DWORD dwJobType = EJT_MERGE
        );

    DLLZZ long AFJMUnInitBackupDest(
        const WCHAR* pwzBKDest
        );

    DLLZZ void AFJMWaitDebuggerAttach(DWORD dwWaitTime = 0);

    DLLZZ void AFJMWaitDebuggerDetach(DWORD dwWaitTime = 0);

	DLLZZ long AFJMGetSessNumListForNextMerge(
		const CPathInfo& BKDest, 
		DWORD            dwRetentionCnt, 
		vector<DWORD>&   vecSessList
		);

#ifdef __cplusplus
};
#endif

