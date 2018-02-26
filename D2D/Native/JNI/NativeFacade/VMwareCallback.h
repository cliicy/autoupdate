#pragma once
#include "HAClientProxy.h"

#include "jni.h"

typedef struct _REPJOB_MONITOR
{
	wchar_t* pwstrMonitorID;
	wchar_t* pwstrJobID;
	unsigned long ulTotalSize;
	unsigned long ulTransSize;

}REPJOB_MONITOR,*PREPJOB_MONITOR;

class VMwareCallback : public IHADTCallback
{

public:

	VMwareCallback(JNIEnv *env,wstring vmuuid);
	~VMwareCallback(void);

public:

	virtual long ReportJobProgress (const wchar_t* pwszJobID, 
                                    unsigned __int64 uliTotal, 
                                    unsigned __int64 uliTrans);

	virtual long ReportDiskProgress(const wchar_t* pwszJobID, unsigned long ulDiskSig,
        unsigned __int64 uliTotal, unsigned __int64 uliTrans);


	virtual long ReportMessage(const wchar_t* pwszJobID, HADT_SEVERITY_LEVEL eLevel, 
							   unsigned long ulMsgID, const wchar_t** ppParams, 
							   unsigned long ulParamCnt/*, void* pUserData*/);

	virtual long ReportDiskSanModeStatus(const wchar_t* pwszJobID,unsigned long ulDiskSig, int nStatus) ;


private:

	JavaVM *jvm;
	jclass cls;
	wstring vmuuid;
	jmethodID mid_HyperVUpdateRepJobMonitorProgress;
	jmethodID mid_HyperVUpdateRepJobMonitorReportMsg;

};
