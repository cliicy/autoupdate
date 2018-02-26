#pragma once

#ifdef CATALOGMGRDLL_EXPORTS
#define CATALOG_EXPORT __declspec(dllexport)
#else
#define CATALOG_EXPORT __declspec(dllimport)
#endif

#include <Windows.h>
#include "DbgLog.h"
#ifndef WINAPI
#define WINAPI __stdcall
#endif

#ifndef WCHAR
typedef wchar_t WCHAR;
#endif

#define BACKUPINFO_CATALOG_STATUS_FAILED        L"$FAILED$"
#define BACKUPINFO_CATALOG_STATUS_PENDING       L""

//ZZ: Use to enable offline catalog logic.
#define __OFFLINE_CATALOG_FS__

#include "afjob.h"
#include <vector>
#include <string>

using namespace std;

typedef std::vector<std::wstring> WSVector;
typedef std::vector<DWORD>        DWVector;

class CDiskExtInfo
{
public:
    CDiskExtInfo() : llStartOffset(0), llVolumeOffset(0), llDiskExtLength(0) {}
    wstring  wsVMDKFilePah;
    LONGLONG llStartOffset;
    LONGLONG llVolumeOffset;
    LONGLONG llDiskExtLength;
};
typedef std::vector<CDiskExtInfo> DiskExtVector;

typedef enum
{
    ESST_UNKNOWN = 0,
    ESST_VOL,
    ESST_EXCH,
    ESST_SQL
}E_SUBSESS_TYPE;

typedef enum
{
    EJSP_USED_COMMON = 0,
    EJSP_USED_BY_AGENT,
    EJSP_USED_BY_RPS
}E_JS_PURPOSE;

class CVolumeInfo
{
public:
    CVolumeInfo() { Clear(); }
    void Clear()
    {
		bIsFilecopySource = false;
        bCatalogCreated = false;
        dwSubSessType = ESST_VOL;
        dwSubSessNum = 0;
        dwVolumeType = 0;
        hActiveJob = INVALID_HANDLE_VALUE;
        wsIndexGUID.clear();
        wsVolumeName.clear();
        wsVolumeGUID.clear();
        wsVolumeType.clear();
        wsCatalogName.clear();
        wsSnapshotPath.clear();
        wsTempFolder.clear();
        vecDiskExtList.clear();
    }
	bool		  bIsFilecopySource;
    bool          bCatalogCreated;
    DWORD         dwSubSessType;
    DWORD         dwSubSessNum;
    DWORD         dwVolumeType;
    HANDLE        hActiveJob;     //ZZ: To make all temp folder be removed, we put delete-on-close file under each folder.
    wstring       wsIndexGUID;    //ZZ: Make relationship between meta file for sub session and volume information.
    wstring       wsVolumeName;
    wstring       wsVolumeGUID;
    wstring       wsVolumeType;
    wstring       wsCatalogName;
    wstring       wsSnapshotPath;
    wstring       wsTempFolder;
    DiskExtVector vecDiskExtList;
};

typedef std::vector<CVolumeInfo> VolInfoVector;

class CFolderInfo
{
public:
    CFolderInfo() { Clear(); }
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

class CJobParam
{
public:
    CJobParam()  { Clear(); }
    void Clear()
    {
        bDoArchive = false;
        dwJobNum = 0;
        dwSessNum = 0;
        dwCryptoInfo = 0;
        dwCompressInfo = 0;
        dwOption = 0;
        dwJobMethod = 0;
        dwJobType = 0;
        ulVersion = 0;
        ullBackupTime = 0;
		llCataLogSize = 0;
        dwJSPurpose = EJSP_USED_COMMON;
        wsSessPWD.clear();
        wsVMName.clear();
        wsVMIP.clear();
        wsVMGUID.clear();
        wsScriptPath.clear();
        wsBKStartTime.clear();
        BackupDest.Clear();
        CurBackupDest.Clear();
        wsBKJobName.clear();
        wsBKSessGUID.clear();
        wsPolicyGUID.clear();
        wsPolicyName.clear();
        wsD2DIdentity.clear();
        wsCatalogModeID.clear();
        wsRPSName.clear();
	    wsSourceDataStore.clear();
		wsSourceDataStoreName.clear();
		wsTargetDataStore.clear();
		wsTargetDataStoreName.clear();
		gInfo.CompressedSize = 0;
		gInfo.RawSize = 0;
		gInfo.CompressPercentage = 0;
		gInfo.CompressRatio = 0;
        BackupDest.Clear();
        CurBackupDest.Clear();
		EDBlist.clear();
    }

    bool		   bDoArchive;
    DWORD          dwJobNum;
    DWORD          dwSessNum;
    DWORD          dwCryptoInfo;
    DWORD          dwCompressInfo;
    DWORD          dwOption;
    DWORD          dwJobMethod;
    DWORD          dwJobType;
	ULONG		   ulVersion;	     // xml version--zouju01-2012/2/10		
    DWORD          dwJSPurpose;      //ZZ: This is retrieved from job script's name. e.g. JS20131025102410804_11_3@2$1.xml
	ULONGLONG	   ullBackupTime;	 // backup time
	ULONGLONG	   llCataLogSize;
    wstring        wsSessPWD;
    wstring        wsVMName;
    wstring        wsVMIP;
    wstring        wsVMGUID;         //ZZ: Currently only for vsphere.
    wstring        wsScriptPath;
    wstring        wsBKStartTime;
    wstring        wsBKJobName;
    wstring        wsBKSessGUID;
	wstring		   wsPolicyGUID;
	wstring		   wsPolicyName;
    wstring		   wsD2DIdentity;    //ZZ: Unique string to indicate D2D server.
    wstring        wsCatalogModeID;  //ZZ: Unique string to indicate server where catalog will be launhced
	wstring		   wsRPSName;
	wstring        wsSourceDataStore;
	wstring        wsSourceDataStoreName;
	wstring        wsTargetDataStore;
	wstring        wsTargetDataStoreName;
	GDDInformation gInfo;
    CFolderInfo    BackupDest;       ///ZZ: Backup destination information when session backup.
    CFolderInfo    CurBackupDest;    ///ZZ: Backup destination information when catalog is launched.
	WSVector       EDBlist;          //wanmi12
};

class CJobScript
{
public:
    void Clear()
    {
        JobParam.Clear();
        vecVolumeInfo.clear();
    }

    CJobParam     JobParam;
    VolInfoVector vecVolumeInfo;
};

typedef enum
{
    EQT_UNKNOWN = 0,
    EQT_REGULAR,
    EQT_ONDEMAND,
    EQT_MAKEUP
}E_QUEUE_TYPE;

typedef enum
{
    ESQT_FAILED = 0,
    ESQT_SUCCEED,
    ESQT_INVALID,
    ESQT_CORRUPT,
    ESQT_ACTIVE,
    ESQT_CANCELED
}E_SUB_JOBQ_TYPE;

typedef enum
{
    EDT_UNKNOWN = 0,
    EDT_GEN_CATALOG,
    EDT_SAVE_SCRIPT,
    EDT_START_JOB
}E_DBG_TYPE;

typedef enum
{
    ECO_NONE = 0,
    ECO_ACTIVE_JS = 0x00000001,
    ECO_RMV_JS = 0x00000002
}E_CATALOG_OPTION;

typedef struct _IMAGE_JOB IMAGE_JOB, *PIMAGE_JOB; 
class ICatalogInterface
{
public:
    virtual void    Release() = 0;
    virtual void    SetJobID(DWORD dwJobID) = 0;
    virtual wstring GetJSPath() const = 0;
    virtual void    SetOption(DWORD dwOption, bool bAppendOption = false) = 0;
};

class ICatalogReaderInterface : public ICatalogInterface
{
public:
    virtual long GenerateCatalog(E_QUEUE_TYPE eQueueType, const WCHAR* pwzJobScriptPath) = 0;
    virtual void EnableScheduleJob(bool bEnabled = false) = 0;
};

class ICatalogWriterInterface : public ICatalogInterface
{
public:
    //ZZ: Should be called after AddImageJobInfo, because AddImageJobInfo will parse most of information.
    virtual void SetCatalogParam(CJobParam& JobParam) = 0;
    virtual long AddImageJobInfo(IMAGE_JOB* pImgJobInfo) = 0;
    virtual long AddVolumeInfo(CVolumeInfo& VolInfo) = 0;
    virtual void ResetCatalogWriter() = 0;
    virtual long GenerateCatalogScript(E_QUEUE_TYPE eQueueType, const WCHAR* pwzJobScriptName) = 0;
	virtual long GenerateCatalogScriptForDisabled(E_QUEUE_TYPE eQueueType, const WCHAR* pwzJobScriptName, const WCHAR* pwzDestPath, const WCHAR* pwzUserName, const WCHAR* pwzPassword, ULONG ulsessionNumber) = 0;
	//wanmi12
	virtual void SetEDBList(std::vector<std::wstring> &vecEDB) =0;
};

#ifndef L
#define _L(x)                                        L ## x
#define L(x)                                         _L(x)
#endif

#ifndef __WFUNCTION__
#define __WFUNCTION__                                L(__FUNCTION__)
#endif

#ifndef __WFUNC__
#define __WFUNC__                                    L("[")##L(__FUNCTION__)##L("] ")
#endif

inline const WCHAR* WS_S(const wstring& wsInStr) { return (wsInStr.empty() ? L"null" : wsInStr.c_str()); }
inline const WCHAR* WS1_S(const wstring& wsInStr) { return (wsInStr.empty() ? L"N/A" : wsInStr.c_str()); }
inline const WCHAR* WZ_S(const WCHAR* pwzInStr) { return (pwzInStr ? pwzInStr : L"null"); }
inline const WCHAR* WZ1_S(const WCHAR* pwzInStr) { return (pwzInStr ? pwzInStr : L""); }
inline const WCHAR* VOLWS_S(const CVolumeInfo& VolInfo) { return WS_S(VolInfo.wsVolumeName.empty() ? VolInfo.wsVolumeGUID : VolInfo.wsVolumeName); }
inline WCHAR* WS_NC(const wstring& wsInStr) { return const_cast<WCHAR*>(wsInStr.c_str()); }
inline long  WSICMP(const wstring& wsStr1, const wstring& wsStr2) { return _wcsicmp(wsStr1.c_str(), wsStr2.c_str()); }
inline bool  WSISAME(const wstring& wsStr1, const wstring& wsStr2) { return (0 == _wcsicmp(wsStr1.c_str(), wsStr2.c_str())); }
inline const WCHAR* BOOLSTR(bool bBoolVal) { return (bBoolVal ? L"true" : L"false"); } 

#define DEFAULT_JOB_ID_CATALOG_PROCESS                   0xFFFFFFFE
#define DEFAULT_JOB_ID_CATALOG_GRT                       0xFFFFFFFD

#define CATALOG_PROCESS_SWITCH_SCRIPT_PATH               L"CatScript"
#define CATALOG_PROCESS_SWITCH_JOB_ID                    L"JobID"
#define CATALOG_PROCESS_SWITCH_JOB_QUEUE_TYPE            L"JobQType"
#define CATALOG_PROCESS_SWITCH_JOB_QUEUE_IDENTITY        L"JobQID"
#define CATALOG_PROCESS_SWITCH_DO_IMPERSONATE            L"Impersonate"
#define CATALOG_PROCESS_SWITCH_RUN_NEXT                  L"RunNext"
#define CATALOG_PROCESS_SWITCH_EXIT_CONFIRM              L"ExitConfirm"
#define CATALOG_PROCESS_SWITCH_SHOW_USAGE                L"?"


struct _NET_CONN_INFO;
typedef struct _NET_CONN_INFO NET_CONN_INFO;

typedef struct
{
    DWORD dwSessNum;
    DWORD dwSubSessNum;
    DWORD dwCatalogStatus;
}ST_CATALOG_STATUS, *PST_CATALOG_STATUS;

#ifdef __cplusplus
extern "C"
{
#endif

    CATALOG_EXPORT long AFOCCreateCatalogReader(
        ICatalogReaderInterface** ppCatalogReader,
        DWORD                     dwJobNum = DEFAULT_JOB_ID_CATALOG_PROCESS,
        const WCHAR*              pwzScriptPath = NULL,
        const WCHAR*              pwzJobQIdentity = NULL);

    CATALOG_EXPORT long AFOCCreateCatalogWriter(
        ICatalogWriterInterface** ppCatalogWriter, 
        DWORD                     dwJobNum = DEFAULT_JOB_ID_CATALOG_PROCESS,
        const WCHAR*              pwzScriptPath = NULL,
        const WCHAR*              pwzJobQIdentity = NULL);

    CATALOG_EXPORT long AFOCStartCatalogGenerator(
        E_QUEUE_TYPE eQueueType,
        DWORD        dwJobNum = DEFAULT_JOB_ID_CATALOG_PROCESS,
        DWORD*       pdwProcID = NULL,		// process ID of catalog job
		HANDLE*		 phProc = NULL,			// process handle of catalog job
        const WCHAR* pwzUsrName = NULL, 
        const WCHAR* pwzPassword = NULL,
        bool         bLaunchbyService = true,
        const WCHAR* pwzJobQIdentity = NULL,
		const CFolderInfo* pBackupDest = NULL,
        const WCHAR* pwzCatalogModeID = NULL);

    CATALOG_EXPORT long AFOCInitBKDestByScript(
        const WCHAR* pwzCatScriptPath);

    CATALOG_EXPORT long AFOCInitBackupDest(
        CFolderInfo& BackupDest,
        bool         bDoImpersonate = false);

    CATALOG_EXPORT long AFOCUnInitBackupDest(
        CFolderInfo& BackupDest);

    CATALOG_EXPORT long AFOCParseJobScript(
        CJobScript&  JobScript, 
        const WCHAR* pwzScriptPath);

	CATALOG_EXPORT long AFOCSaveJobScript(
		CJobScript& JobScript, 
		const WCHAR* pwzScriptPath);

    CATALOG_EXPORT long AFOCGetNewJobScrptName(
        wstring& wsScriptName,
        DWORD    dwJobType,
        DWORD    dwSessNum);

    CATALOG_EXPORT long AFOCGenerateExchGrtCatalog(
        E_QUEUE_TYPE eQueueType,
        const WCHAR* pwzJobScriptPath, 
        DWORD        dwJobID,
        const WCHAR* pwzJobQIdentity = NULL);

    CATALOG_EXPORT long AFOCCatalogDebugWait(
        E_DBG_TYPE eDBGType);

    CATALOG_EXPORT long AFOCSetGlobalLogMgr(
		CDbgLog* pCatalogLogMgr);

    CATALOG_EXPORT long AFOCMarkJobFailed(
        E_QUEUE_TYPE eJobQType,    
        const WCHAR* pwzScriptPath, 
        const WCHAR* pwzJobQIdentity = NULL);

    //ZZ: [2013/11/11 15:58] This API will copy job script from session folder to target job queue folder. API name is a little confused.
    CATALOG_EXPORT long AFOCMoveJobScript(
        E_QUEUE_TYPE eJobQType,    
        const WCHAR* pwzBKDest ,
        DWORD        dwSessNum,
        const WCHAR* pwzJobQIdentity = NULL,
        const WCHAR* pwzCatalogNodeID = NULL);

    CATALOG_EXPORT long AFOCQueryJobQueue(
        E_QUEUE_TYPE eJobQType,                  //ZZ: Specify job queue type. Refer to E_QUEUE_TYPE(Regular, On-Demand, and Makeup)
        const WCHAR* pwzJobQIdentity = NULL,     //ZZ: Job queue identity. VM GUID for HBBU or computer identity for catalog running on RPS.
        wstring*     pwsJobQPath = NULL,         //ZZ: Return job queue path which contain job scripts. Ignore this parameter when it is NULL.
        WSVector*    pvecJobScriptList = NULL,   //ZZ: Return job script list under job queue. Ignore this parameter when it is NULL.
        bool         bCreateJobQFolder = false,  //ZZ: If create job queue folder when it doesn't exist.
        const WCHAR* pwzCatalogModeID = NULL,    //ZZ: Server identity where catalog should be launched. If this parameter is empty, it will be ignored.
        bool         bIncludeGRT = false         //ZZ: If GRT catalog job script will be included to result.
        );

    CATALOG_EXPORT long AFOCRetryFailedJob(
        E_QUEUE_TYPE eJobQType,    
        const WCHAR* pwzScriptPath, 
        const WCHAR* pwzJobQIdentity = NULL,
        bool         bFailedB4Parse = true);

    CATALOG_EXPORT long AFOCIsCatalogAvailable(
        E_QUEUE_TYPE eQueueType, 
        const WCHAR* pwzJobQIdentity = NULL,
        const WCHAR* pwzCatalogModeID = NULL);

	CATALOG_EXPORT bool AFOCIsCatalogJobInQueue(
		E_QUEUE_TYPE eQueueType, 
		WCHAR*       pwzBKDestOrSessGUID,
		DWORD        dwSessNum,
		DWORD        dwSubSessNum);

	//wanmi12 <
	CATALOG_EXPORT BOOL AFIsCatalogJobInQueueForASBU(WCHAR* pwszSessPath);
	//wanmi12 >

    ///ZZ: Used for on-demand catalog. When select a session with failed catalog to generate catalog. 
    CATALOG_EXPORT long AFOCSaveJS4FSOndemand(
        const NET_CONN_INFO& stBKDest,
        DWORD                dwSessNum, 
        const WCHAR*         pwzJobQIdentity = NULL,
        DWORD                dwSubSessNum = 0,
        const WCHAR*         pwzSessPWD = NULL);

    ///ZZ: Used for catalog-less backup. When select a session with disabled catalog to generate catalog, we need update destination information using what in current restore GUI.
	CATALOG_EXPORT long AFOCSaveJS4FSOndemandCatalog(
		const NET_CONN_INFO& stBKDest,
		DWORD dwSessNum, 
		const WCHAR* pwzJobQIdentity = NULL,
		DWORD dwSubSessNum = 0,
		const WCHAR* pwzSessPWD = NULL,
		const WCHAR* pwzJobScript = NULL);

    CATALOG_EXPORT long AFOCSaveJS4GRTOndemand(
        const NET_CONN_INFO& stBKDest,
        DWORD                dwSessNum, 
        DWORD                dwSubSessNum,
        const WCHAR*         pwzSessPWD = NULL,
        const WCHAR*         pwzJobQIdentity = NULL);

	CATALOG_EXPORT long AFOCSaveJS4GRTOndemand2(
		const NET_CONN_INFO& stBKDest,
		DWORD                dwSessNum, 
		DWORD                dwSubSessNum,
		const WCHAR*         pwzSessPWD ,
		const WCHAR*         pwzJobQIdentity,
		WSVector&            vecEDBGUID);

    CATALOG_EXPORT long AFOCGetSessGUIDInJobQueue(
        WSVector&    vecSessGUID,
        E_QUEUE_TYPE eQueueType = EQT_MAKEUP,
        const WCHAR* pwzJobQIdentity = NULL);

    CATALOG_EXPORT long AFOCPrepare4Catalog(
        const WCHAR* pwzJobScript,
        bool         bUpdateJobScriptFile = true,
        bool         bDoImpersonate = false);

    CATALOG_EXPORT long AFOCPrepare4CatalogEx(
        CJobScript& JobScript,
        bool        bUpdateJobScriptFile = true,
        bool        bDoImpersonate = false);

    CATALOG_EXPORT bool AFOCIsJSValid(
        CJobParam*   pJobParam,                //ZZ: Should pass either pJobParam or pwzJSPath. 
        const WCHAR* pwzJSPath,                //ZZ: Should be set as NULL when pJobParam is not empty.
        E_QUEUE_TYPE eJobQType = EQT_UNKNOWN,
        const WCHAR* pwzJobQIdentity = NULL,
        bool         bMoveInvalidJS = true);

    CATALOG_EXPORT long AFOCGetCatalogStatus(
        const WCHAR*       pwzBKDest,
        PST_CATALOG_STATUS pstCatalogStatusList,
        DWORD              dwCatalogStatus,
        const WCHAR*       pwzJobQIdentity = NULL);

    CATALOG_EXPORT long AFOCRemoveJobScript(
        E_QUEUE_TYPE eJobQType,
        const WCHAR* pwzDataStoreGUID,
        const WCHAR* pwzJobQIdentity = NULL,
        bool         bMoveInsteadDelete = true);

#ifdef __cplusplus
};
#endif

