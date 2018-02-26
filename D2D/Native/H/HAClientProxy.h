#ifndef _HA_DATATRANSFER_PROXY_H_
#define _HA_DATATRANSFER_PROXY_H_
#pragma once
#include "hatransjobscript.h"

#ifdef HATRANSCLIENTPROXY_EXPORTS
#define CLIENTPROXY_API __declspec(dllexport)
#else
#define CLIENTPROXY_API __declspec(dllimport)
#endif

#ifdef __cplusplus
extern "C" {
#endif

#pragma pack(push, 4)


//================================================================
// Client proxy
//================================================================

//
// For Replica
//
typedef enum _e_hadt_severity_level
{
    MSG_INFO = 0,
    MSG_WARNING = 1,
    MSG_ERROR = 2,
} HADT_SEVERITY_LEVEL;

typedef enum _e_hadt_status
{
    JOB_FINISHED = 0,
    JOB_FAILED = 1,
    JOB_CANCELED = 2,
    JOB_FAILED_GETSESSPWD = 5,
} HADT_STATUS;

inline bool HADT_JobIsFinished(HADT_STATUS status)
{
    return status == JOB_FINISHED;
}

inline bool HADT_JobIsCanceled(HADT_STATUS status)
{
    return status == JOB_CANCELED;
}

inline bool HADT_JobIsFailed(HADT_STATUS status)
{
    return !HADT_JobIsFinished(status) && !HADT_JobIsCanceled(status);
}

enum
{
    STAT_DISK_FINISHED      = 0,
    STAT_DISK_SAN_FAILED    = 1,
    STAT_DISK_HOTADD_FAILED = 2,
    STAT_DISK_NBD_FAIED     = 3,
    STAT_DISK_FAILED        = 0xFFFFFFFF,
};

struct IHADTCallback
{
    virtual ~IHADTCallback() = 0 {};
    ///*
    //Report the Job Progress
    //@ulJobID[in]    - The unique ID of job
    //@ulPercent[in]  - The present of job has been transfered.
    //*/
    virtual long ReportJobProgress(const wchar_t* pwszJobID, unsigned __int64 uliTotal, 
                                   unsigned __int64 uliTrans) = 0;

    virtual long ReportDiskProgress(const wchar_t* pwszJobID, unsigned long ulDiskSig,
        unsigned __int64 uliTotal, unsigned __int64 uliTrans) = 0;

    /*
    @ulJobID[in]    - The unique ID of job
    @ulMsgID[in]    - the message ID in the resource
    @ppParams[in]   - pointer to array of zero terminated string, those string is the parameter 
                      of the message corresponding to ulMsgID
    @ulParamCnt[in] - the number of strings in the array pointed by ppParams
    */
    virtual long ReportMessage(const wchar_t* pwszJobID, HADT_SEVERITY_LEVEL eLevel, 
                               unsigned long ulMsgID, const wchar_t** ppParams, 
                               unsigned long ulParamCnt/*, void* pUserData*/) = 0;

    /*
    * Status STAT_DISK_FINISHED, STAT_DISK_SAN_FAILED, etc.
    */
    virtual long ReportDiskSanModeStatus(const wchar_t* pwszJobID, 
                                        unsigned long ulDiskSig, int nStatus) = 0;
};


/*
Start transfer the job described by pstJobScript,
This function is synchronous, it will block until the job is finished or failed.
*/
CLIENTPROXY_API
HADT_STATUS HADT_StartReplicaJobEx(const HA_JOBSCRIPT* pstJobScript, 
                                   IHADTCallback* pCallback);
typedef HADT_STATUS (*Proc_HADT_StartReplicaJobEx)(const HA_JOBSCRIPT* pstJobScript, 
                                   IHADTCallback* pCallback);

/*
Get the vm path type (compressed/encrypted/compressed volume)
*/
CLIENTPROXY_API
long HADT_GetHypervVMPathType(const wchar_t* vmPath);
typedef long (*Proc_HADT_GetHypervVMPathType)(const wchar_t* vmPath);

/*
Cancel the job
This function will return immediately, and caller should check the return code of HADT_StartJob 
to look if the job is canceled.
*/
CLIENTPROXY_API
int HADT_CancelJob(const wchar_t* pwszInstID);

typedef int (*Proc_HADT_CancelJob)(const wchar_t* pwszInstID);

/*
Set the Band width, in KB, -1 means does not control throttling.
*/
CLIENTPROXY_API
int HADT_SetBandWidth(const wchar_t* pwszInstID, int nRateKb);
typedef int (*Proc_HADT_SetBandWidth)(const wchar_t* pwszInstID, int nRateKb);


//
// For Get information at server side
//

typedef struct _t_ha_server_info
{
    const wchar_t* pwszName; // "hostname@ip" or "hostname" or "@ip"
    const wchar_t* pwszPort;
    const wchar_t* pwszUser; // username for log on
    const wchar_t* pwszPwd;  // password for log on
} ST_HASRV_INFO, *PST_HASRV_INFO;
/*
Get D2D GUID of server machine.
@pstSrcInfo [IN]  - the server to get UUID.
@pwszUuid   [OUT] - the UUID buffer.
@nBufCnt    [IN]  - the length of pwszUuid buffer, in character.
*/
CLIENTPROXY_API
int HADT_GetUuidOfD2D(const ST_HASRV_INFO* pstSrvInfo, wchar_t* pwszUuid, int nBufCnt);
typedef int (*Proc_HADT_GetUuidOfD2D)(const ST_HASRV_INFO* pstSrvInfo, wchar_t* pwszUuid, int nBufCnt);

/*
Get the last session info transfered.
pwszSessRoot is the root path
1) If root reside in local, input pstSrvInfo == NULL || pstSrvInfo->pwszName == NULL.
2) If root reside in remote share, input pstSrvInfo->pwszName == NULL, and pstSrvInfo->pwszName,
pstSrvInfo->pwszPwd is the user and password to access the remote share folder.
3) If root reside in remote machine with socket server, input pstSrvInfo->pwszName is the remote
machine name, pstSrvInfo->pwszPort is the socket server's port.

@pstSrvInfo  [in]   - the server that the last session reside.
@pwszSessRoot[in]   - the lastest session's root path.
@ppwszInfo   [out]  - the session info string, is a xml buffer.

return value:
0 - success
others fail.
If no last session, this function will return 0 with *ppwszInfo==NULL or wcslen(*ppwszInfo) == 0.

Attention: Call HADT_FreeBuffer() to free the buffer returned.
*/
CLIENTPROXY_API
int HADT_GetLastRepSessInfo(const ST_HASRV_INFO* pstSrvInfo, 
                            const wchar_t* pwszSessRoot,
                            wchar_t** ppwszInfo);
typedef int (*Proc_HADT_GetLastRepSessInfo)(const ST_HASRV_INFO* pstSrvInfo, 
                                            const wchar_t* pwszSessRoot, 
                                            wchar_t** ppwszInfo);

/*
Call this to free the buffer returned by this module.
*/
CLIENTPROXY_API
void HADT_FreeBuffer(void* p);
typedef void (*Proc_HADT_FreeBuffer)(void* p);

/**
* Get the real sub root we will replicated to at the destination side
* For example: If input des root is D:\des, the real root maybe D:\des\sub0002. This function
* will return sub0002

@pstSrvInfo         [in]   - the server that the last session reside.
@pwszProductNode    [in]   - the product node
@pwszSrcPath        [in]   - the source session path, such as C:\src\ARC\vstore\S0000000001
@pwszLastDesRoot    [in]   - the latest root at destination side.
@nDesRootNum        [in]   - count of item in ppwszDesRootList
@ppwszDesRootList   [in]   - array of pwszDesRoot, which are the destinations that we want replicate to
                             support the case that different vhd may be sent to different locations
@pwszBuf            [out]  - buffer to get the returned subroot, zero terminated.
@nLenInWord         [in]   - the capacity of the input pwszBuf, in words.
*/
CLIENTPROXY_API
int HADT_GetDestSubRoot(const ST_HASRV_INFO* pstSrvInfo,
                        const wchar_t* pwszProductNode,
                        const HA_SRC_ITEM* pwszSrcItem,
                        const wchar_t* pwszLastDesRoot,
                        int nDesRootNum,
                        const wchar_t** ppwszDesRootList,
                        wchar_t* pwszBuf,
                        int nLenInWord);
typedef int (*Proc_HADT_GetDestSubRoot)(
                        const ST_HASRV_INFO* pstSrvInfo,
                        const wchar_t* pwszProductNode,
                        const HA_SRC_ITEM* pwszSrcItem,
                        const wchar_t* pwszLastDesRoot,
                        int nDesRootNum,
                        const wchar_t** ppwszDesRootList,
                        wchar_t* pwszBuf,
                        int nLenInWord);

/**
* Convert xml buffer to replication job script
* Caller must call HADT_FreeJSHandle() to free the jobscript.
* @pwszXMLBuf   [in] - the xml buffer.
* @ppJobscript  [out]- point to the converted jobscript struct.
* @phJsHandle   [out]- point to the JS handle. caller HADT_FreeJSHandle by input this handle.
*/
CLIENTPROXY_API
int HADT_ConvertXmlToJobscript(const wchar_t* pwszXMLBuf, HA_JOBSCRIPT** ppJobScript, void** phJsHandle);
typedef int (*Proc_HADT_ConvertXmlToJobscript)(
                        const wchar_t* pwszXMLBuf, 
                        HA_JOBSCRIPT** ppJobScript, 
                        void** pJsHandle);

/**
* Free the buffer hold by JSXML.
*/
CLIENTPROXY_API void HADT_FreeJSHandle(void* hJsHandle);
typedef void (*Proc_HADT_FreeJSHandle)(void* hJsHandle);

#pragma pack(pop)

#ifdef __cplusplus
}
#endif

#endif //_HA_DATATRANSFER_PROXY_H_
