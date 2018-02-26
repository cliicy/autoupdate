#pragma once
#include "AFJob.h"
#include "afdefine.h"
#include "AFCoreAPIInterface.h"

#if 0 //<sonmi01>2013-3-22 #rps job monitor and eliminate the redefinitions 
//sonli02 show job monitor after web service is restarted
//typedef struct _JOB_CONTEXT_Internal
//{
//	DWORD         dwJobId;
//	DWORD         dwQueueType;
//	DWORD         dwJobType;
//	DWORD         dwProcessId;
//	DWORD         dwJMShmId;
//	ULONG         ulJobAttribute;                  //useful for VM job monitor
//	wchar_t       wstrNodeName[512];               //useful for VM job monitor
//	wchar_t       wstrLauncherInstanceUUID[512];   //useful for VM job monitor*/
//	wchar_t       wstrRPSName[64]; 
//	wchar_t		  wstrD2DNodeName[64];
//	wchar_t		  wstrPolicyGuid[512];
//} JOB_CONTEXT_Internal, *PJOB_CONTEXT_Internal;
//typedef struct _JOB_CONTEXT
//{
//	DWORD         dwJobId;
//	DWORD         dwQueueType;
//	DWORD         dwJobType;
//	DWORD         dwProcessId;
//	DWORD         dwJMShmId;
//	ULONG         ulJobAttribute;                  //useful for VM job monitor
//	std::wstring  wstrNodeName;                    //useful for VM job monitor
//	std::wstring  wstrLauncherInstanceUUID;        //useful for VM job monitor*/
//	std::wstring       wstrRPSName; 
//	std::wstring		  wstrD2DNodeName;
//	std::wstring		  wstrPolicyGuid;
//} JOB_CONTEXT, *PJOB_CONTEXT;
#endif //<sonmi01>2013-3-22 #rps job monitor and eliminate the redefinitions 

DWORD WINAPI AFRetrieveActiveJobs(std::vector<JOB_CONTEXT> &vecActiveJobs);
DWORD WINAPI CancelJobImp(DWORD dwJobId, PWCHAR pwszNodeName);