#pragma once
#include "HAClientProxy.h"
#include "HAServerProxy.h"
#include "VMDKIo.h"
#include "jni.h"
#include "VMwareVolumeInfo.h"
class HyperVRepCallBack : public IHADTCallback, public IHASrvCallback
{
public:
	HyperVRepCallBack(void);
	~HyperVRepCallBack(void);

public:
    virtual long ReportJobProgress (const wchar_t* pwszJobID, 
                                    unsigned __int64 uliTotal, 
                                    unsigned __int64 uliTrans);
	
	virtual long ReportDiskProgress(const wchar_t* pwszJobID, unsigned long ulDiskSig,
        unsigned __int64 uliTotal, unsigned __int64 uliTrans);


	virtual long ReportMessage(const wchar_t* pwszJobID, HADT_SEVERITY_LEVEL eLevel, unsigned long ulMsgID, 
		const wchar_t** ppParams, unsigned long ulParamCnt/*, void* pUserData*/);

    virtual int GetVmHostName(const wchar_t* pwszVmGuid, wchar_t* pwszHostName,
                              int nCntIn, int* nCntOut);

	virtual long ReportDiskSanModeStatus(const wchar_t* pwszJobID,unsigned long ulDiskSig, int nStatus) ;
	wstring vmuuid;
	JNIEnv *env;
};

typedef HADT_STATUS (* Proc_HADT_StartReplicaJobEx)(const HA_JOBSCRIPT* pstJobScript, 
                                   IHADTCallback* pCallback);
DWORD WINAPI Native_HADT_StartReplicaJobEx(const HA_JOBSCRIPT* pstJobScript, 
                                   IHADTCallback* pCallback);

DWORD WINAPI Native_HADT_S_StartServer(int nPort, IHASrvCallback* pCallback, BOOL bRestartServer = TRUE); //<huvfe01>2012-11-10 for defect#126017
DWORD WINAPI Native_HADT_S_StopServer();
DWORD WINAPI Native_HADT_GetDestSubRoot(const ST_HASRV_INFO* pstSrvInfo,
							   const wchar_t* pwszProductNode,
							   const HA_SRC_ITEM* pwszSrcItem,
							   const wchar_t* pwszLastDesRoot,
							   int nDesRootNum,
							   const wchar_t** ppwszDesRootList,
							   wchar_t* pwszBuf,
							   int nLenInWord);



/*
Stop the server
*/
SERVERPROXY_API
int HADT_S_StopServer();
int RepJobMonitor2RepJobScript(JNIEnv *env, jobject& jobMonitor, HA_JOBSCRIPT& jobScript);
int ConvertVMDKConnParams(JNIEnv *env, jobject& connParams, VMDK_CONNECT_MORE_PARAMS& moreParams);
void clear_jobscript(HA_JOBSCRIPT& jobScript);
int ConvertVMwareVolumeInfo(JNIEnv *env, jobject& bootVol, VMwareVolumeInfo& volumeInfo);