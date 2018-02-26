#ifndef _HA_DATATRANSFER_SERVERPROXY_H_
#define _HA_DATATRANSFER_SERVERPROXY_H_
#pragma once

#ifdef HATRANSSERVERPROXY_EXPORTS
#define SERVERPROXY_API __declspec(dllexport)
#else
#define SERVERPROXY_API __declspec(dllimport)
#endif

#ifdef __cplusplus
extern "C" {
#endif

#pragma pack(push, 4)

//================================================================
// Server proxy
//================================================================
struct IHASrvCallback
{
    virtual ~IHASrvCallback() = 0 {};

    /**
    * Get the bm host name by VmGUID.
    * If pwszHostname == NULL && nCntIn == 0, *CntOut return the length needed, include '\0',
    * and return value is 0.
    * @pwszVmGuid       - [in]  the VM GUID.
    * @pwszHostName     - [out] the host name buffer
    * @nCntIn           - [in]  the input buffer length, in wchar_t
    * @pnCntOut         - [out] the output host name length, in wchar_t, include the terminate L'\0'.
    * return value:
    * 0 success, others fail.
    */
    virtual int GetVmHostName(const wchar_t* pwszVmGuid, wchar_t* pwszHostName,
                              int nCntIn, int* pnCntOut) = 0;
};

/*
Start the server
*/
SERVERPROXY_API
int HADT_S_StartServer(int nPort, IHASrvCallback* pCb, BOOL bRestartServer);

typedef int (*Proc_HADT_S_StartServer)(int nPort, IHASrvCallback* pCb, BOOL bRestartServer); //<huvfe01>2012-11-10 for defect#126017

/*
Stop the server
*/
SERVERPROXY_API
int HADT_S_StopServer();

typedef int (*Proc_HADT_S_StopServer)();

/*
Stop all job from specific product node
*/
SERVERPROXY_API
int HADT_S_StopJobFromNode(const wchar_t* pwszProductNode);

typedef int (*Proc_HADT_S_StopJobFromNode)(const wchar_t* pwszProductNode);


#pragma pack(pop)

#ifdef __cplusplus
}
#endif

#endif //_HA_DATATRANSFER_SERVERPROXY_H_

