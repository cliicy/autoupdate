#include "stdafx.h"
#include "RPSCoreInterface.h"
#include "..\Common\CommonUtils.h"
#include "RPSCOMM\network_throttling.h"
#include <BrowseFolder.h>

#define TRY __try{
#define CATCH(procName) }__except(HandleSEH(L"RPSCoreInterface.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GET_PROC_ADDRESS(procName) DynGetProcAddress(L"RPSCoreInterface.dll", procName)
#define CATCH_CORE_FUNC(procName) }__except(HandleSEH(L"RPSCoreFunction.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GET_PROC_ADDRESS_CORE_FUNC(procName) DynGetProcAddress(L"RPSCoreFunction.dll", procName)


#define AFCATCH(procName) }__except(HandleSEH(L"AFCoreInterface.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define AFGET_PROC_ADDRESS(procName) DynGetProcAddress(L"AFCoreInterface.dll", procName)
#define AFCATCH_CORE_FUNC(procName) }__except(HandleSEH(L"AFCoreInterface.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define AFGET_PROC_ADDRESS_CORE_FUNC(procName) DynGetProcAddress(L"AFCoreInterface.dll", procName)

#define NTPCATCH(procName) }__except(HandleSEH(L"network_throttling.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define NTPGET_PROC_ADDRESS(procName) DynGetProcAddress(L"network_throttling.dll", procName)
#define NTPCATCH_CORE_FUNC(procName) }__except(HandleSEH(L"network_throttling.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define NTPGET_PROC_ADDRESS_CORE_FUNC(procName) DynGetProcAddress(L"network_throttling.dll", procName)

#define GMBCATCH(procName) }__except(HandleSEH(L"GRTMntBrowser.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GMBGET_PROC_ADDRESS(procName) DynGetProcAddress(L"GRTMntBrowser.dll", procName)
#define GMBCATCH_CORE_FUNC(procName) }__except(HandleSEH(L"GRTMntBrowser.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GMBGET_PROC_ADDRESS_CORE_FUNC(procName) DynGetProcAddress(L"GRTMntBrowser.dll", procName)

#define AFFUNCTIONCATCH(procName) }__except(HandleSEH(L"AFCoreFunction.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define AFGET_COREFUNC_PROC_ADDRESS(procName) DynGetProcAddress(L"AFCoreFunction.dll", procName)


BOOL WINAPI RPSGetJobId(DWORD *pId)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(DWORD*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSGetJobId");

	return pfun(pId);
	CATCH("RPSGetJobId")
}

BOOL WINAPI GetRPSRegRootPathByProduct(wstring &wstr)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(wstring&);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetRPSRegRootPathByProduct");

	return pfun(wstr);
	CATCH("GetRPSRegRootPathByProduct");
}

DWORD WINAPI CreateRPSJobMonitor(DWORD dwShrMemID, IJobMonitor **ppIJobMonitor)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(DWORD, IJobMonitor **);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "CreateRPSJobMonitor" );

	return pfun(dwShrMemID, ppIJobMonitor);
	CATCH("CreateRPSJobMonitor")
}

VOID WINAPI DestroyRPSJobMonitor(IJobMonitor **ppIJobMonitor)
{
	TRY
	typedef VOID (WINAPI* LPFUN)(IJobMonitor **);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "DestroyRPSJobMonitor" );

	return pfun(ppIJobMonitor);
	CATCH("DestroyRPSJobMonitor")
}


DWORD CreateIFileListHandler(IFileListHandler **ppIFileList)
{
	TRY
		typedef DWORD (* LPFUN)(IFileListHandler **);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("CreateIFileListHandler");

	return pfun(ppIFileList);
	AFCATCH("CreateIFileListHandler")
}



BOOL WINAPI AFGetErrorMsg(DWORD dwErr, std::wstring &strMsg)
{
	TRY
		typedef BOOL (WINAPI* LPFUN)(DWORD, std::wstring &);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("AFGetErrorMsg");

	return pfun(dwErr, strMsg);
	AFCATCH("AFGetErrorMsg")
}


DWORD WINAPI RPSBrowseVolumeInforamtion(IN OUT LPWSTR* ppBrowseInfo, IN OUT DWORD* pdwBrowseInfoSize, BOOL bSaveAsFile, BOOL bBrowseDetail = FALSE, WCHAR* pwzBackupDest = NULL)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(IN OUT LPWSTR*, IN OUT DWORD*, BOOL, BOOL,WCHAR*);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS( "BrowseVolumeInforamtion" );

	return pfun(ppBrowseInfo, pdwBrowseInfoSize,bSaveAsFile, bBrowseDetail,pwzBackupDest);
	AFCATCH("BrowseVolumeInforamtion")
}

DWORD WINAPI ReleaseBrowseInformation(IN LPWSTR* ppBrowseInfo)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(LPWSTR*);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("ReleaseBrowseInformation");

	return pfun(ppBrowseInfo);
	AFCATCH("ReleaseBrowseInformation")
}

DWORD WINAPI AFCGRTSkipDisk(IN const PWCHAR pwzVolName, OUT BOOL * pbSkipped)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(const PWCHAR pwzVolName, OUT BOOL * pbSkipped);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("AFCGRTSkipDisk");

	return pfun(pwzVolName, pbSkipped);
	AFCATCH("BrowseVolumeInforamtion")
}
DWORD WINAPI AFCreateDir( const std::wstring &strParent, const std::wstring &strSubFolder )
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(const std::wstring &, const std::wstring &);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS( "AFCreateDir" );

	return pfun(strParent, strSubFolder);
	AFCATCH("AFCreateDir")
}

DWORD WINAPI AFCreateAllDirs( const std::wstring &strFullFolder)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(const std::wstring &);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS( "AFCreateAllDirs" );

	return pfun(strFullFolder);
	AFCATCH("AFCreateAllDirs")
}

HANDLE WINAPI AFGetControlRPSJobHandle(DWORD dwFlag, BOOL bAsync, JobFilter filter, JNIEnv *env) 
{
	TRY
		typedef HANDLE (WINAPI* LPFUN)(DWORD, BOOL, JobFilter, JNIEnv*, ILogger*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetControlRPSJobHandle");

	return pfun(dwFlag, bAsync, filter, env, NULL);
	CATCH("AFGetControlRPSJobHandle")
}

DWORD WINAPI AFControlRPSJobs(HANDLE handle) 
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(HANDLE);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("ControlRPSJobs");

	return pfun(handle);
	CATCH("AFControlRPSJobs")
}

void WINAPI AFFreeControlRPSJobs(HANDLE handle)
{
	TRY
		typedef void (WINAPI* LPFUN)(HANDLE);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("FreeControlRPSJobHandle");
	pfun(handle);
	CATCH("AFFreeControlRPSJobs")
}

DWORD AFGetControlRPSJobStatus(HANDLE handle)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(HANDLE);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetControlRPSJobStatus");
	return pfun(handle);
	CATCH("AFGetControlRPSJobStatus")
}

BOOL WINAPI RPSDecryptStrWithEarlyVersion(const wchar_t *pszStr, wchar_t* pszBuf, DWORD *pBufLen)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(const wchar_t *pszStr, wchar_t* pszBuf, DWORD *pBufLen);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSDecryptStrWithEarlyVersion");

	return pfun(pszStr, pszBuf, pBufLen);
	CATCH("RPSDecryptStrWithEarlyVersion")
}



DWORD WINAPI AFCutConnection( const NET_CONN_INFO &info, BOOL bForce /*= TRUE*/ )
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &, BOOL);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS( "AFCutConnection" );

	return pfun( info, bForce );
	AFCATCH("AFCutConnection")
}

DWORD WINAPI AFCreateConnection( const NET_CONN_INFO &info)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS( "AFCreateConnection" );

	return pfun( info);
	AFCATCH("AFCreateConnection")
}


DWORD WINAPI AFVerifyDestUser(const wchar_t * pwszPath, const wchar_t * pwszUser, const wchar_t * pwszpPsw)
{
	if(NULL == pwszPath || NULL == pwszUser || NULL == pwszpPsw)
	{
		return -1;
	}
	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(const wchar_t*, const wchar_t*, const wchar_t*);
		LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("AFIVerifyDestUser");
		return pfun(pwszPath, pwszUser, pwszpPsw);
	}
	AFCATCH ("AFIVerifyDestUser")
}


DWORD WINAPI AFRetrieveConnections(vector<NET_CONN> &vConn)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(vector<NET_CONN>&);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("AFRetrieveConnections");

	return pfun( vConn);
	AFCATCH("AFRetrieveConnections");
}

DWORD WINAPI RPSCancelJob(DWORD dwJobId, PWCHAR pwszNodeName)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(DWORD dwJobId, PWCHAR pwszNodeName);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSCancelJob");

	return pfun(dwJobId, pwszNodeName);
	CATCH("RPSCancelJob");
}

DWORD WINAPI AFCheckFolderAccess(const NET_CONN_INFO *pDest, FILE_INFO &info)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO *, const FILE_INFO&);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("AFCheckFolderAccess");

	return pfun(pDest, info);
	AFCATCH("AFCheckFolderAccess")
}

BOOL WINAPI CheckNewSessionsToReplicate(const wchar_t* pSessionRootPath, const wchar_t* pUser, const wchar_t* pPass) 
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(const wchar_t*, const wchar_t*, const wchar_t*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("CheckNewSessionsToReplicate");

	return pfun(pSessionRootPath, pUser, pPass);
	CATCH("CheckNewSessionsToReplicate");
}

void WINAPI AFGetPathMaxLength(DWORD *pLen)
{
	TRY
		typedef void (WINAPI* LPFUN)(DWORD *);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS( "AFGetPathMaxLength" );

	return pfun(pLen);
	CATCH("AFGetPathMaxLength")
}

VOID* WINAPI RPSCreateClientJobSharedQueue()
{
	TRY
	typedef VOID* (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSCreateClientJobSharedQueue");
	
	return pfun();
	CATCH("RPSCreateClientJobSharedQueue");
}

VOID WINAPI RPSDestroyClientJobSharedQueue(IN VOID* pSharedJobQueue)
{
	TRY
	typedef VOID (WINAPI* LPFUN)(VOID*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSDestroyClientJobSharedQueue");

	return pfun(pSharedJobQueue);
	CATCH("RPSDestroyClientJobSharedQueue");
}

BOOL WINAPI RPSWaitForNewClientJobArrival(IN VOID* pSharedJobQueue, OUT RPS_JOB_INFO_S* pstJobInfo)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(VOID *, RPS_JOB_INFO_S *);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSWaitForNewClientJobArrival");

	return pfun(pSharedJobQueue, pstJobInfo);
	CATCH("RPSWaitForNewClientJobArrival");
}

DWORD WINAPI RPSRetrieveActiveJobs(vector<JOB_CONTEXT> &activeJobs)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(vector<JOB_CONTEXT>&);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFRetrieveActiveJobs");

		return pfun(activeJobs);
	CATCH("AFRetrieveActiveJobs");
}

IJobMonInterface* WINAPI RPSICreateMergeJM(
						DWORD dwJobID,
						DWORD* pdwErrCode)
{
	TRY 
	{
		typedef IJobMonInterface* (WINAPI* LPFUN)(DWORD, DWORD*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSICreateMergeJM");
		return pfun(dwJobID, pdwErrCode);
	}
	CATCH ("RPSICreateMergeJM")
}

void WINAPI RPSIReleaseMergeJM(
					IJobMonInterface** ppJobMonMgr)
{
	TRY 
	{
		typedef void (WINAPI* LPFUN)(IJobMonInterface**);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSIReleaseMergeJM");
		return pfun(ppJobMonMgr);
	}
	CATCH ("RPSIReleaseMergeJM")
}

long WINAPI RPSISaveMergeJS(
					CMergeJS& MergeJS, 
					const WCHAR* pwzJSPath)
{
	TRY 
	{
		typedef long (WINAPI* LPFUN)(CMergeJS&, const WCHAR*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSISaveMergeJS");
		return pfun(MergeJS, pwzJSPath);
	}
	CATCH ("RPSISaveMergeJS")
}

long WINAPI RPSIStopMergeJob(
					DWORD dwJobID)
{
	TRY 
	{
		typedef long (WINAPI* LPFUN)(DWORD);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSIStopMergeJob");
		return pfun(dwJobID);
	}
	CATCH ("RPSIStopMergeJob")
}

long WINAPI RPSIStartJob(
					const WCHAR* pwzJSPath)
{
	TRY 
	{
		typedef long (WINAPI* LPFUN)(const WCHAR*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSIStartJob");
		return pfun(pwzJSPath);
	}
	CATCH ("RPSIStartJob")
}

long WINAPI RPSIIsMergeJobAvailable(
					DWORD dwRetentionCnt, 
					const WCHAR* pwzBKDest, 
					const WCHAR* pwzVMGUID, 
					const WCHAR* pwzBKUsr, 
					const WCHAR* pwzBKPwd,
					const WCHAR* pwzDS4Replication)
{
	TRY 
	{
		typedef long (WINAPI* LPFUN)(DWORD, const WCHAR*, const WCHAR*, 
			const WCHAR*, const WCHAR*, const WCHAR*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSIIsMergeJobAvailable");
		return pfun(dwRetentionCnt, pwzBKDest, pwzVMGUID, pwzBKUsr, pwzBKPwd, pwzDS4Replication);
	}
	CATCH ("RPSIIsMergeJobAvailable")
}

long WINAPI RPSIRetrieveMergeJM(
					ActJobVector& vecActiveJob)
{
	TRY 
	{
		typedef long (WINAPI* LPFUN)(ActJobVector&);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSIRetrieveMergeJM");
		return pfun(vecActiveJob);
	}
	CATCH ("RPSIRetrieveMergeJM")
}

BOOL WINAPI RPSSendClientJobRunCmdOnJobArrival(const RPS_JOB_INFO_S* pstJobInfo)
{
	TRY 
	{
		typedef long (WINAPI* LPFUN)(const RPS_JOB_INFO_S*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSSendClientJobRunCmdOnJobArrival");
		return pfun(pstJobInfo);
	}
	CATCH ("RPSSendClientJobRunCmdOnJobArrival")
}

BOOL WINAPI RPSSendClientJobStopCmdOnJobArrival(const RPS_JOB_INFO_S* pstJobInfo, DWORD dwReasonCode)
{
	TRY 
	{
		typedef long (WINAPI* LPFUN)(const RPS_JOB_INFO_S*, DWORD);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSSendClientJobStopCmdOnJobArrival");
		return pfun(pstJobInfo, dwReasonCode);
	}
	CATCH ("RPSSendClientJobStopCmdOnJobArrival")
}

DWORD WINAPI RPSIGetUserSID(
	wstring& wsUserSID, 
	const WCHAR* pwzUserName /* = NULL */, 
	wstring* pwsSIDAccount /* = NULL */, 
	wstring* pwsDomain4SID /* = NULL */, 
	DWORD* pdwSIDAccountType /* = NULL */,
	DWORD  dwHashAlg /* = EHAT_SHA1 */)
{
	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(wstring&, const WCHAR*, wstring*, wstring*, DWORD*, DWORD);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSIGetUserSID");
		return pfun(wsUserSID, pwzUserName, pwsSIDAccount, pwsDomain4SID, pdwSIDAccountType, dwHashAlg);
	}
	CATCH ("RPSIGetUserSID")	
}

DWORD WINAPI RPSIGetNodeID( wstring& strNodeID )
{
	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(wstring&);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSIGetNodeID");
		return pfun(strNodeID);
	}
	CATCH ("RPSIGetNodeID")	
}


DWORD WINAPI RPSStartCatalogGenerator(
    DWORD        dwQueueType,           // Job in which queue will be run. 1 = regular, 2 = ondemand
    DWORD        dwJobNum,              // Job number for job monitor.
    DWORD*       pdwProcID /* = NULL */,      // Return the new process if needed.
    const WCHAR* pwzUsrName /* = NULL */,     // User name for security requirement when start process.
    const WCHAR* pwzPassword /* = NULL */,    // Password for security requirement when start process.
    const WCHAR* pwzJobQIdentity /* = NULL */,  // Job queue name, Empty for local D2D, VM GUID for vsphere.
    const NET_CONN_INFO* stBKDest /* = NULL */, //// Backup destination information, including account information for remote folder.
    const WCHAR* pwzCatalogModeID /* = NULL */)
{
	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(DWORD, DWORD, DWORD*, const WCHAR*, 
			const WCHAR*, const WCHAR*, const NET_CONN_INFO*, const WCHAR*);
		LPFUN fpun = (LPFUN)AFGET_PROC_ADDRESS("AFStartCatalogGenerator");
		return fpun(dwQueueType, dwJobNum, pdwProcID, pwzUsrName, 
			pwzPassword, pwzJobQIdentity, stBKDest, pwzCatalogModeID);
	}
	AFCATCH ("AFStartCatalogGenerator")
}



DWORD WINAPI RPSSaveJS4FSOndemand(
	const NET_CONN_INFO& stBKDest, 
	DWORD dwSessNum, 
	const WCHAR* pwzJobQIdentity, 
	DWORD dwSubSessNum, 
	const WCHAR* pwzSessPWD ) 
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN) (const NET_CONN_INFO&, DWORD, const WCHAR*, DWORD, const WCHAR*);
		LPFUN fpun = (LPFUN)AFGET_PROC_ADDRESS("AFSaveJS4FSOndemand");
		return fpun(stBKDest, dwSessNum, pwzJobQIdentity, dwSubSessNum, pwzSessPWD);
	}
	AFCATCH("AFSaveJS4FSOndemand");
}

DWORD WINAPI RPSMoveJobScript(
	DWORD dwJobQType,			//ZZ: Job queue type, 1:regular queue, 2:ondemand queue, 3:makeup queue. You can always specify 1 so far
	const WCHAR* wsBKDest,		//ZZ: Backup destination. Connection should be created before call this API
	DWORD dwSessNum,			//ZZ: Session number.
	const WCHAR* wsJobQID,		//ZZ: It should be VM GUID for HBBU catalog, while it should be D2D SID or similar identity for D2D on RPS.
	const WCHAR* pwzCatalogNodeID )
{

	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(E_QUEUE_TYPE, const WCHAR*, DWORD, const WCHAR*, const WCHAR*);
		LPFUN fpun = (LPFUN)AFGET_PROC_ADDRESS("AFIMoveJobScript");
		return fpun((E_QUEUE_TYPE)dwJobQType, wsBKDest, dwSessNum, wsJobQID, pwzCatalogNodeID);
	}
	AFCATCH ("AFIMoveJobScript")
} 

DWORD WINAPI RPSIQueryJobQueue(
	DWORD eJobQType,						 //ZZ: Specify job queue type. Refer to E_QUEUE_TYPE(Regular, On-Demand, and Makeup)
	const WCHAR* pwzJobQIdentity /* = NULL */,     //ZZ: Job queue identity. VM GUID for HBBU or computer identity for catalog running on RPS.
	wstring*     pwsJobQPath /* = NULL */,         //ZZ: Return job queue path which contain job scripts. Ignore this parameter when it is NULL.
	WSVector*    pvecJobScriptList /* = NULL */,   //ZZ: Return job script list under job queue. Ignore this parameter when it is NULL.
	bool bCreateJobQFolder /* = false */,			 //ZZ: If create job queue folder when it doesn't exist.
	const WCHAR* pwzCatalogModeID /* = NULL */)    //ZZ: Server identity where catalog should be launched. If this parameter is empty, it will be ignored.
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(E_QUEUE_TYPE, const WCHAR*, wstring*, WSVector*, bool, const WCHAR*);
		LPFUN fpun = (LPFUN)AFGET_PROC_ADDRESS("AFIQueryJobQueue");
		return fpun((E_QUEUE_TYPE)eJobQType, pwzJobQIdentity, pwsJobQPath, pvecJobScriptList, bCreateJobQFolder,pwzCatalogModeID);
	}
	AFCATCH ("AFIQueryJobQueue")
}

long WINAPI RPSIsCatalogAvailable(
	DWORD dwQueueType, 
	const WCHAR* pwzJobQIdentity /* = NULL */, 
	const WCHAR* pwzRPSSvrIdentity /* = NULL */)
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(DWORD, const WCHAR*, const WCHAR*);
		LPFUN fpun = (LPFUN)AFGET_PROC_ADDRESS("AFIsCatalogAvailable");
		return fpun(dwQueueType, pwzJobQIdentity, pwzRPSSvrIdentity);
	}
	AFCATCH ("AFIsCatalogAvailable")
}

int WINAPI RPSNTP_AddPolicy(PNETWORK_THROTTLING_POLICY pPolicy)
{
	TRY
	{
		typedef DWORD (*LPFUN)(PNETWORK_THROTTLING_POLICY);

		LPFUN fpun = (LPFUN)NTPGET_PROC_ADDRESS("NTP_AddPolicy");
		return fpun(pPolicy);
	}
	NTPCATCH ("RPSNTP_AddPolicy")
}

int WINAPI RPSNTP_UpdatePolicy(PNETWORK_THROTTLING_POLICY pPolicy)
{
	TRY
	{
		typedef DWORD (*LPFUN)(PNETWORK_THROTTLING_POLICY);

		LPFUN fpun = (LPFUN)NTPGET_PROC_ADDRESS("NTP_UpdatePolicy");
		return fpun(pPolicy);
	}
	NTPCATCH ("RPSNTP_UpdatePolicy")
}

int WINAPI RPSNTP_RemovePolicy(GUID* pGuid)
{
	TRY
	{
		typedef DWORD (*LPFUN)(GUID*);

		LPFUN fpun = (LPFUN)NTPGET_PROC_ADDRESS("NTP_RemovePolicy");
		return fpun(pGuid);
	}
	NTPCATCH ("RPSNTP_RemovePolicy")
}

DWORD WINAPI RPSIGetAllMappedDrvPath(const std::wstring &strUser, std::vector<MAPPED_DRV_PATH> &vDrvPath)
{
	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(const wstring&, std::vector<MAPPED_DRV_PATH>&);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPSIGetAllMappedDrvPath");
		return pfun(strUser, vDrvPath);
	}
	CATCH ("RPSIGetAllMappedDrvPath")	
}

int WINAPI RPSGetCatalogStatus(LPWSTR sessionPath)
{
	TRY 
	{
		typedef INT (WINAPI *LPFUN)(LPWSTR sessionPath);
		LPFUN pfun = (LPFUN)GMBGET_PROC_ADDRESS("GetCatalogStatus");
		return pfun(sessionPath);
	}
	CATCH ("RPSGetCatalogStatus")	
}


DWORD WINAPI AFIGetCatalogStatus(
	const WCHAR*       pwzBKDest,
	PST_CATALOG_STATUS pstCatalogStatusList,
	DWORD              dwCatalogStatus,
	const WCHAR*       pwzJobQIdentity)
{

	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(const WCHAR*, PST_CATALOG_STATUS, DWORD, const WCHAR*);
		LPFUN fpun = (LPFUN)AFGET_PROC_ADDRESS("AFIGetCatalogStatus");
		return fpun(pwzBKDest, pstCatalogStatusList, dwCatalogStatus, pwzJobQIdentity);
	}
	AFCATCH ("AFIGetCatalogStatus")
} 

DWORD WINAPI RPSRemoveCatalogJS(E_QUEUE_TYPE eJobQType,
	const WCHAR* pwzDataStoreGUID,
	const WCHAR* pwzJobQIdentity /* = NULL */
	)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(E_QUEUE_TYPE, const WCHAR*, const WCHAR*);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("AFIRemoveCatalogJS");

	return pfun(eJobQType, pwzDataStoreGUID, pwzJobQIdentity);
	AFCATCH("AFIRemoveCatalogJS")
}

DWORD WINAPI CreateIBackupSumm(const NET_CONN_INFO &info, IBackupSumm **ppIBackupSumm)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &info, IBackupSumm **ppIBackupSumm);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("CreateIBackupSumm");

	return pfun(info, ppIBackupSumm);
	AFCATCH("CreateIBackupSumm")
}

DWORD WINAPI CreateIRestorePoint(const NET_CONN_INFO &info, IRestorePoint **ppIRestorePoint)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &info, IRestorePoint **ppIRestorePoint);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("CreateIRestorePoint");

	return pfun(info, ppIRestorePoint);
	AFCATCH("CreateIRestorePoint")
}

int WINAPI AFCollectSystemInfo(PD2D_SYSINFO pstObj)
{
	TRY
		typedef int (WINAPI* LPFUN)(PD2D_SYSINFO pstObj);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("AFCollectSystemInfo");

	return pfun(pstObj);
	AFCATCH("AFCollectSystemInfo")
}

BOOL WINAPI CheckFileFolderExist(const wstring &strFile)
{
	TRY
		typedef BOOL (WINAPI* LPFUN)(const wstring &strFile);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("CheckFileFolderExist");

	return pfun(strFile);
	AFCATCH("CheckFileFolderExist")
}


DWORD WINAPI AFVerifyDSDestFreeSpace(const wchar_t * pwszPath, const wchar_t * pwszUser, const wchar_t * pwszpPsw, const DWORD64 dwMinFreeSpaceSize)
{
	if(NULL == pwszPath || NULL == pwszUser || NULL == pwszpPsw)
	{
		return -1;
	}
	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(const wchar_t*, const wchar_t*, const wchar_t*, DWORD64);
		LPFUN pfun = (LPFUN)AFGET_COREFUNC_PROC_ADDRESS("AFVerifyDSDestFreeSpace");
		return pfun(pwszPath, pwszUser, pwszpPsw, dwMinFreeSpaceSize);
	}
	AFFUNCTIONCATCH ("AFVerifyDSDestFreeSpace")
}

BOOL WINAPI AFValidateSessPasswordByHash(const WCHAR* pwszPwd, DWORD dwPwdLen, const WCHAR* pwszPwdHash, DWORD dwHashLen)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const WCHAR*, DWORD, const WCHAR*, DWORD);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS( "AFValidateSessPasswordByHash" );

	return pfun( pwszPwd, dwPwdLen, pwszPwdHash, dwHashLen);
	AFCATCH("AFValidateSessPasswordByHash")
}

DWORD WINAPI RPSAFIEnumBackupDestinations( const NET_CONN_INFO &info, std::vector<wstring>& vecBackupDestinations )
{
	TRY
	typedef DWORD (WINAPI *LPFUN)(const NET_CONN_INFO &info, std::vector<wstring>& vecBackupDestinations);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("AFIEnumBackupDestinations");

	return pfun(info, vecBackupDestinations);
	AFCATCH("AFIEnumBackupDestinations");
}

DWORD WINAPI RPSAFGetAFLockListByOpType(
	vector<LCK_ERR_INFO>& vLockLst,
	const WCHAR* pwzBKDest, 
	DWORD opType)
{

	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(vector<LCK_ERR_INFO>&, const WCHAR*, DWORD);
		LPFUN fpun = (LPFUN)AFGET_COREFUNC_PROC_ADDRESS("AFGetAFLockListByOpType");
		return fpun(vLockLst, pwzBKDest, opType);
	}
	AFFUNCTIONCATCH ("AFGetAFLockListByOpType")
} 

BOOL WINAPI RPSAFIsPathUnderDatastore( const wstring& strPath) 
{
	TRY

	typedef BOOL (WINAPI *LPFUN)(const wstring& strPath);
	LPFUN fpun = (LPFUN)AFGET_PROC_ADDRESS("AFIsPathUnderDatastore");
	return fpun(strPath);
	
	AFCATCH ("AFIsPathUnderDatastore");
}


DWORD WINAPI RPSAFBuildBackupInfoDBWrap(WCHAR* pszBkDest, BOOL bUpdateAll)
{
	TRY

	typedef DWORD(WINAPI *LPFUN)(WCHAR* szDest, BOOL bUpdateAll);
	LPFUN pFun = (LPFUN)AFGET_COREFUNC_PROC_ADDRESS("AFBuildBackupInfoDBWrap");
	return pFun(pszBkDest, bUpdateAll);

	AFFUNCTIONCATCH("AFBuildBackupInfoDBWrap");
}


DWORD WINAPI RPSAFGetAllBackupInfoDB(const wstring &strDest, BACKUP_INFO_DB &infoDb)
{
	TRY

	typedef DWORD(WINAPI *LPFUN)(const wstring &strDest, BACKUP_INFO_DB &infoDb);
	LPFUN pFun = (LPFUN)AFGET_COREFUNC_PROC_ADDRESS("AFGetAllBackupInfoDB");
	return pFun(strDest, infoDb);

	AFFUNCTIONCATCH("AFGetAllBackupInfoDB");
}


DWORD WINAPI RPSAFGetDataSizeFromShareFolder(NET_CONN_INFO netInfo, VBACKUP_NODES_SIZE& vBkNodesSize)
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(NET_CONN_INFO netInfo, VBACKUP_NODES_SIZE& vBkNodesSize);
		LPFUN pFun = (LPFUN)AFGET_PROC_ADDRESS("AFGetDataSizeFromShareFolder");
		return pFun(netInfo, vBkNodesSize);
	}
	AFCATCH("AFGetDataSizeFromShareFolder");
}

DWORD WINAPI RPSGetMaxStorageCapacity(std::vector<wstring>& destList, BK_STORAGE_MAX_CAP& stMaxCap)
{
	TRY

	typedef DWORD(*LPFUN)(std::vector<wstring>&, BK_STORAGE_MAX_CAP&);
	LPFUN pfun = (LPFUN)AFGET_PROC_ADDRESS("AFGetMaxStorageCapacity");
	return pfun(destList, stMaxCap);

	AFCATCH("AFGetMaxStorageCapacity")
}
DWORD WINAPI RPS_AFArchiveJob(PAFARCHIVEJOBSCRIPT pAFJOBSCRIPT, pfnUserCallProc UserCallBack, LPVOID lpParameter)
{
	TRY
	
	typedef DWORD(WINAPI *LPFUN)(PAFARCHIVEJOBSCRIPT pAFJOBSCRIPT,	pfnUserCallProc UserCallBack, LPVOID lpParameter);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("RPS_AFArchiveJob");
	return pfun(pAFJOBSCRIPT, UserCallBack, lpParameter);

	CATCH("RPS_AFArchive")
}


DWORD WINAPI RPSAFEnumNodesUnderDatastore( const wchar_t* pDSPath, std::vector<UDP_NODE_INFO>& vecNodes )
{	
	TRY
		typedef DWORD (WINAPI* LPFUN)(const wchar_t* pDSPath, std::vector<UDP_NODE_INFO>& vecNodes );
		LPFUN pfun = (LPFUN)AFGET_COREFUNC_PROC_ADDRESS("AFEnumNodesUnderDatastore");

	return pfun(pDSPath, vecNodes);
	AFFUNCTIONCATCH("AFEnumNodesUnderDatastore");
	return 0;
}