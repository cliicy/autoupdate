#pragma once
#include "AFDefine.h"
#include "D2dbaseDef.h"

namespace AFMOUNTMGR
{
#ifdef __cplusplus
    extern "C"{
#endif

#define STR_DATE_LEN 12
#define STR_DETAILTIME_LEN 10
#ifndef MNT_MACHINE_UUID_LENGHT
#define MNT_MACHINE_UUID_LENGHT 512
#endif
        typedef enum MNT_FLAG
        {
            MNT_UNKNOWN = 0,
            MNT_MOUNTED = 0x01,
            MNT_UNMOUNTED = 0x02,
            MNT_INVALID = 0x04
        };

        typedef struct _AFMOUNTINFO
        {
            DWORD dwMntFlag;                //MNT_FLAG.
            DWORD dwSessNo;                 //session No.
            DWORD dwDiskSig;                //mounted disk signature.
            ULONGLONG ullVolSize;           //source volume size.
            GUID sessionId;                 //session GUID.
            wchar_t szMnt[MAX_PATH];        //To mount drive letter or path.
            wchar_t szSourceMnt[MAX_PATH];  //F:
            wchar_t szSourceGuid[MAX_PATH]; //\\?\Volume{c49fdae9-05ab-4b62-8de7-70917b57ca38}
            wchar_t szDest[MAX_PATH];       //d:\bakdest\machine, this destination must contains this session.
            wchar_t szDate[STR_DATE_LEN];
            wchar_t szDetailTime[STR_DETAILTIME_LEN];
        }AFMOUNTINFO;

        typedef struct _MOUNT_PARAM
        {
            NET_CONN_INFO pathInfo;
            wchar_t szSessSubPath[MAX_PATH];     //session relative path, such as vstore\s000000001.
            wchar_t szVolGuidName[MAX_PATH];
            wchar_t szMnt[MAX_PATH];
            PD2D_ENCRYPTION_INFO pEncryptInfo; //the encryption information, it can be null or set the uiCryptoType = 0 to idicate is no encrypted session
			DWORD   dwJobId;             
			DWORD   dwJobType;           
			DWORD   dwProductType;       
			WCHAR   szMachineUUID[MNT_MACHINE_UUID_LENGHT];
			WCHAR   szRPSServer[MAX_PATH];
			WCHAR   szDataStore[MAX_PATH];
        }MOUNT_PARAM;

#ifdef __cplusplus
    }
#endif

class IMountMgr
{
public:
    virtual void Release() = 0;

    virtual DWORD GetMntInfoCount(const NET_CONN_INFO &info, const wchar_t *pszRelative) = 0;

    virtual DWORD GetMntInfo(const NET_CONN_INFO &info, const wchar_t *pszRelative, AFMOUNTINFO *pMntList, DWORD dwNum) = 0;

    virtual DWORD GetAllMntInfoCount() = 0;

    virtual DWORD GetAllMntInfo(AFMOUNTINFO *pMntList, DWORD dwNum) = 0;

    virtual DWORD MountSession(const MOUNT_PARAM &mntParam, const BOOL bReadOnly, AFMOUNTINFO &mntInfo) = 0;

    virtual DWORD Dismount(const wchar_t *pszMnt, DWORD dwDiskSig) = 0;
};

}

DWORD WINAPI CreateIMountMgr(AFMOUNTMGR::IMountMgr **ppMountMgr);

DWORD WINAPI AFGetMntInfoCount(const NET_CONN_INFO &info, const wchar_t *pszRelative);

DWORD WINAPI AFGetMntInfo(const NET_CONN_INFO &info, const wchar_t *pszRelative, AFMOUNTMGR::AFMOUNTINFO *pMntList, DWORD dwNum);

DWORD WINAPI AFGetMntInfoForVolumeCount(const NET_CONN_INFO &info, const wchar_t *pszRelative, const wchar_t *pszVolGuid);

DWORD WINAPI AFGetMntInfoForVolume(const NET_CONN_INFO &info, const wchar_t *pszRelative, const wchar_t *pszVolGuid, AFMOUNTMGR::AFMOUNTINFO *pMntInfo, DWORD dwNum);

DWORD WINAPI AFGetAllMntInfoCount();

DWORD WINAPI AFGetAllMntInfo(AFMOUNTMGR::AFMOUNTINFO *pMntList, DWORD dwNum);

DWORD WINAPI AFMountSession(const AFMOUNTMGR::MOUNT_PARAM *pMntParam, AFMOUNTMGR::AFMOUNTINFO *pMntInfo = NULL);
DWORD WINAPI AFMountSessionWritable(const AFMOUNTMGR::MOUNT_PARAM *pMntParam, AFMOUNTMGR::AFMOUNTINFO *pMntInfo = NULL);

DWORD WINAPI AFDismount(const wchar_t *pszMnt, DWORD dwDiskSig);

DWORD WINAPI AFGetD2DBinTmpDir(wchar_t *pszDir, DWORD dwLen);