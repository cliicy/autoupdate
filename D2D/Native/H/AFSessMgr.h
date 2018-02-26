#pragma once

#include <string>
#include <vector>
#include <map>
#include "drcommonlib.h"

using namespace std;
#include "UDPResource.h"

typedef enum AF_OP_TYPE
{
    OP_INVALID = 0,
    OP_BACKUP,
    OP_MERGE,
    OP_CATALOG_CREATE,
    OP_RESTORE,
    OP_EXPORT,
    OP_OFFLINECOPY,
    OP_FILECOPY_BACKUP,
    OP_LITE_INTEGRATION,
    OP_MOUNT,
    OP_BMR,
	OP_VCM,
	OP_REPLICATION,      //ZZ: Lock type for replication out
    OP_REPLICATION_IN,    //ZZ: Lock type for replication in.
    OP_DELETE,             //ZZ: Lock type for replication in.
	OP_REPLICATION_CATLOG, //ZZ: Lock type for replication catlog
	OP_START_IVM,
	OP_STOP_IVM,
	OP_ARCHIVE_BACKUP
};

typedef struct _AFSESSLCK_PARAM
{
    DWORD dwWait; //wait time(ms) if lock failed.
    DWORD dwReserveTime; //[Zou, Yu] 2010-11-19(archive), valid period for this lock.
    DWORD dwBakMethod; //Just for backup: AF_JOBMETHOD_FULL, AF_JOBMETHOD_INCR, AF_JOBMETHOD_RESYNC
    wstring strDest;
    wstring strDesc;
    vector<DWORD> vSessNo;
}AFSESSLCK_PARAM, *PAFSESSLCK_PARAM;

typedef struct _AF_FILE_ITEM
{
    wstring strFile;
}AF_FILE_ITEM, *PAF_FILE_ITEM;

typedef struct _AFLCK_FILE
{
    DWORD dwWait;
    DWORD dwReserveTime;
    DWORD dwBakMethod;
    wstring strDest;
    wstring strDesc;
    vector<AF_FILE_ITEM> vFile;
}AFLCK_FILE, *PAFLCK_FILE;

typedef struct _AFSESSLCK_ERR_ITEM
{
    DWORD dwProcessId;
    DWORD dwOp;
    DWORD dwErrCode;
    wstring strDomain;
    wstring strComputer;
    wstring strUser;
    wstring strDesc;
    wstring strFile;
}AFSESSLCK_ERR_ITEM, *PAFSESSLCK_ERR_ITEM;

typedef struct _SESS_INFO
{
    DWORD dwSessNo;
    vector<wstring> vFile;
}SESS_INFO, *PSESS_INFO;

typedef struct _DEST_SESS
{
    wstring strDest;
    vector<SESS_INFO> vSess;
}DEST_SESS, *PDEST_SESS;

#define BLOCK_2_CTF L"block0000000002.ctf"


inline BOOL Operation2ResourceId(DWORD dwOpCode, DWORD& dwResourceId)
{
	BOOL bRet = TRUE;
	switch(dwOpCode)
	{
	case OP_BACKUP:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_BACKUP;
		}
		break;
	case OP_MERGE:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_MERGE;
		}
		break;
	case OP_CATALOG_CREATE:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_CATALOG;
		}
		break;

	case OP_RESTORE:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_RESTORE;
		}
		break;
	case OP_EXPORT:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_EXPORT;
		}
		break;
	case OP_OFFLINECOPY:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_HBBU;
		}
		break;
	case OP_ARCHIVE_BACKUP:
	case OP_FILECOPY_BACKUP:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_ARCHIVE;
		}
		break;
	case OP_LITE_INTEGRATION:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_LITE;
		}
		break;
	case OP_MOUNT:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_MOUNT;
		}
		break;
	case OP_BMR:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_BMR;
		}
		break;
	case OP_VCM:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_VCM;
		}
		break;
	case OP_REPLICATION:
		{
			dwResourceId = AFRES_COMMON_INF_JOB_REPLICATION;
		}
    case OP_REPLICATION_IN:
        {
            dwResourceId = AFRES_COMMON_INF_JOB_REPLICATION;
        }
	case OP_REPLICATION_CATLOG:
		{
		dwResourceId = AFRES_COMMON_INF_JOB_REPLICATION;
		}
		break;
	case OP_START_IVM:
		{
		    dwResourceId = AFRES_COMMON_INF_JOB_START_IVM;
		}
		break;
	case OP_STOP_IVM:
	{
		dwResourceId = AFRES_COMMON_INF_JOB_STOP_IVM;
	}
		break;
	default:
		bRet = FALSE;
	}
	return bRet;
}


class IAFSessLck
{
public:
    virtual void Release() = 0;

    virtual DWORD LckSess(PAFSESSLCK_PARAM pparam, vector<AFSESSLCK_ERR_ITEM> &vErrItem) = 0;

    virtual DWORD LckFile(PAFLCK_FILE pparam, vector<AFSESSLCK_ERR_ITEM> &vErrItem) = 0;

    virtual DWORD ExcludeLckFile(const wstring &strDest, const AF_FILE_ITEM &file) = 0;

    virtual void UnlckSess() = 0;
	
	virtual DWORD LckSessWrap(WCHAR* D2DDestination, WCHAR* szDescription, DWORD* sessionArray, int Count, std::vector<AFSESSLCK_ERR_ITEM> &vErrItem) = 0;

	virtual DWORD LckSessWrap2(DWORD dwWait, DWORD dwBakMethod, const WCHAR* D2DDestination, const WCHAR* szDescription, DWORD* sessionArray, int Count) = 0;
	virtual DWORD ExcludeLckFile2(const WCHAR* D2DDestination, const WCHAR* szFile) = 0;
};

DWORD WINAPI CreateIAFSessLck( AF_OP_TYPE type, IAFSessLck **ppLck);

#ifdef __cplusplus
extern "C"{
#endif
///check whether the given session contains catalog or not.
typedef struct _CATALOG_INFO
{
    DWORD dwSubSessNo; //subsession number.
    DWORD dwFlag; //For subsession catalog. 2 means catalog disabled; 1 means catalog exists; 0 means catalog doesn't exist.
    DWORD dwAppFlag; //For app catalog. Like exchange GRT catalog. 1 means exists. 0 means non-exist.
}CATALOG_INFO, *PCATALOG_INFO;

typedef struct _CATALOG_INFO_EX
{
	DWORD dwSubSessNo; //subsession number.
	DWORD dwFlag; //For subsession catalog. 1 means catalog exists; 0 means catalog doesn't exist.
	map<wstring, BOOL> mapEDBCatalogStatus;
}CATALOG_INFO_EX, *PCATALOG_INFO_EX;

#ifdef __cplusplus
}
#endif



typedef vector<CATALOG_INFO> CATALOG_INFO_LIST;

typedef vector<CATALOG_INFO_EX> CATALOG_INFO_LIST_EX;

/*
*Purpose: check whether the given session contains catalog.
*@strBakDest:[Input] One backup destination.
*@dwSessNo:[Input] The master session number.
*@Return: 0 for success, otherwise, system standard error code will be returned.
*/
DWORD WINAPI AFCheckCatalogExist(const wstring &strBakDest, DWORD dwSessNo, CATALOG_INFO_LIST &vInfo);

DWORD WINAPI AFCheckCatalogExistWrap(WCHAR* strBakDest, DWORD dwSessNo, PCATALOG_INFO vInfo, PDWORD size);

DWORD WINAPI AFCheckCatalogExistEx(const wstring &strBakDest, DWORD dwSessNo, CATALOG_INFO_LIST_EX &vInfoEx);

/*
*Purpose: Check which session is valid under given backup destination.
*@strDest: [IN] The backup destination.
*@dwSessNo: [OUT] Last master session number which is a valid session.
*@Return: 0 for success, otherwise, system standard error code will be returned.
*/
DWORD WINAPI AFGetLastValidSessFromDest(const wstring &strDest, DWORD &dwSessNo);

DWORD WINAPI AFAddFullSessCfg(const wstring &strDest, DWORD dwSessNo);

DWORD WINAPI AFRmvFullSessCfg(const wstring &strDest, DWORD dwSessNo);

DWORD WINAPI AFMarkFullSessAsMerge(const wstring &strDest, DWORD dwSessNo);

DWORD WINAPI AFMarkSessAsFull(const wstring &strDest, DWORD dwSessNo);

DWORD WINAPI AFGetFullSessCfg(const wstring &strDest, vector<DWORD> &vSess, BOOL bMerge = FALSE);

DWORD WINAPI AFGetFullSess4Inc(DWORD& dwFullSess, DWORD dwIncSess, const WCHAR* pwzBKDest);