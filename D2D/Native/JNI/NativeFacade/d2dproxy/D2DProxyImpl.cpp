#include "./../stdafx.h"
#include "D2DProxyImpl.h"
#include "d2dproxyiDef.h"
#include "D2DIPCItem.h"
#include "Basecodec.h"
#include "./../JNIConv.h"

#include "SigForD2Dcallback.h"
#include "..\..\..\H\UDPResource.h"
#include "..\..\..\H\Log.h"
#include "..\..\..\H\DRCommonlib.h"
#include "..\..\..\H\XXmlNode.h"
#include "TaskLicense.h"
#include "WMIService.h"

#define  D2D_TASK_TIMEOUT 1000*60*12
extern CDbgLog logObj;

CD2DProxyImpl CD2DProxyImpl::g_objD2dPxy;

CD2DProxyImpl::CD2DProxyImpl(void)
{
	m_pvJEnv = NULL;
	m_hQuitEvent =FALSE;
	m_hSignal =NULL;
	InitializeCriticalSection(&m_csTask);
}

CD2DProxyImpl::~CD2DProxyImpl(void)
{
	DeleteCriticalSection(&m_csTask);
}

CD2DProxyImpl * CD2DProxyImpl::GetInstance()
{
	return &g_objD2dPxy;
}

HRESULT CD2DProxyImpl::Init(JNIEnv *pJnienv)
{
	if(NULL==pJnienv)
	{
		return E_INVALIDARG;
	}
	 
	HRESULT hr=0;
	m_pvJEnv = pJnienv;
	SECURITY_DESCRIPTOR sd;
	SECURITY_ATTRIBUTES sa;
	DWORD dwErr=0;

	InitializeSecurityDescriptor(&sd,SECURITY_DESCRIPTOR_REVISION);
	SetSecurityDescriptorDacl(&sd,TRUE,(PACL)NULL,FALSE); //set all the user can access the object
	sa.nLength=sizeof(SECURITY_ATTRIBUTES);
	sa.bInheritHandle=FALSE;
	sa.lpSecurityDescriptor=&sd;
 
	m_hQuitEvent = CreateEvent(&sa, TRUE, FALSE, NULL);
	if(NULL == m_hQuitEvent)
	{
		dwErr = GetLastError();
		hr = dwErr;
	}

	ComItemImpl::GetInstance()->Init(this);

	m_hSignal = CreateMutex(&sa, FALSE, D2D_PROXY_OBJECT_COMM);
	if(NULL==m_hSignal)
	{
		hr = E_FAIL;
	}

	GetCurProcessToken();

	return hr;
}

DWORD CD2DProxyImpl::GetCurProcessToken()
{
	DWORD dwRet =0;
	HANDLE hTok = NULL;
	TOKEN_STATISTICS *pInfo = NULL;
	DWORD dwLen = 0;
	LUID id = {0};
	 
    do
    {
		if(!OpenProcessToken(GetCurrentProcess(), TOKEN_QUERY, &hTok) )
		{

			dwRet = GetLastError();
			logObj.LogW(LL_WAR, dwRet, L"%s: Failed to OpenProcessToken", __WFUNCTION__);
			break;
		}
   
        GetTokenInformation(hTok, 
            TokenStatistics,
            NULL,
            0,
            &dwLen);

        if(!dwLen)
        {
            dwRet = GetLastError();
            logObj.LogW(LL_WAR, dwRet, L"%s: Fail to get length of token info", __WFUNCTION__);
            break;
        }

        pInfo = (TOKEN_STATISTICS *)new char[dwLen];
        ZeroMemory(pInfo, dwLen);

        if(!GetTokenInformation(hTok,
            TokenStatistics,
            pInfo,
            dwLen,
            &dwRet))
        {
            dwRet = GetLastError();
            logObj.LogW(LL_WAR, dwRet, L"%s: Fail to query token information", __WFUNCTION__);
            break;
        }
        
        dwRet = 0; 
        memcpy_s(&id, sizeof(LUID), &pInfo->AuthenticationId, sizeof(LUID));

    }while(0);

	logObj.LogW(LL_WAR, dwRet, L"***Current Process Token[0x%08x:0x%08x] Named Pipe Timeout:%d", id.HighPart, id.LowPart, D2D_PROXY_TIMEOUT);

	if(hTok)
	{
		CloseHandle(hTok);
		hTok = NULL;
	}

    if(pInfo)
    {
        delete[] pInfo;
        pInfo = NULL;
    }

	return dwRet;
}
HRESULT CD2DProxyImpl::UnInit()
{
	if(m_hSignal)
	{
		CloseHandle(m_hSignal);
		m_hSignal = NULL;
	}

	ComItemImpl::GetInstance()->UnInit();
	m_isQuit = TRUE;

	if(m_hQuitEvent)
	{
		SetEvent(m_hQuitEvent);
		CloseHandle(m_hQuitEvent);
		m_hQuitEvent = NULL;
	}

	return S_OK;
}

HRESULT CD2DProxyImpl::Run()
{
	while (1)
	{
		HRESULT hrTask = S_OK;
		if(TRUE==m_isQuit)  break;

		DWORD dwRet;
		dwRet=::WaitForSingleObject(m_hQuitEvent, 100);
		if(WAIT_TIMEOUT !=dwRet)
		{ 	
			break;
		}  

		hrTask = ExcuteTask();
		Sleep(100);
		 
	}

	return S_OK;
}

HRESULT CD2DProxyImpl::ExcuteTask()
{
	CLockHelper m_lock(&m_csTask);
	HRESULT hr=0;
	do 
	{

		if(0==m_Task.size())
		{
			Sleep(500);
			hr = S_FALSE;
			break;
		}

		ITaskItem * pvItem;
		pvItem = m_Task.front();
		m_Task.pop_front();
		if(pvItem)
		{
			pvItem->SetJNIItem(m_pvJEnv);
			hr =pvItem->DoWork();
			pvItem->Notify();
			ULONG ulRes=pvItem->Release();
	 
		}

	} while (0);

	return hr;
}

HRESULT CD2DProxyImpl::AddTask(ITaskItem * pvTask)
{
	CLockHelper m_lock(&m_csTask);

	if(NULL==pvTask)
	{
		return E_INVALIDARG;
	}
	pvTask->AddRef();
	m_Task.push_back(pvTask);

	return S_OK;
}

HRESULT CD2DProxyImpl::OnMessage( XIN BYTE * pinBUF, XIN DWORD ninSIZE, XOUT BYTE * poutBUF, XINOUT DWORD & noutSIZE)
{
	logObj.LogW(LL_INF, 0, L"OnMessage CMD: start()...");

	HRESULT hr = S_OK;
	D2DIPC_MSG_HEADER header = {0};
	D2DIPC_MSG_HEADER response = {0};
	// decode the buffer
	DWORD dwRet=0;
	
	response.version  = IPC_CALLBACK_VER_1;
	response.oricmdid = header.cmdid;
	response.signature= D2D_PXY_CMD_RES_SIGNATURE;

	BYTE * pbufResponse =NULL;

	dwRet =CMDcodec::UnpackHeader(pinBUF, ninSIZE,& header);
	if(0!=dwRet)
	{
		logObj.LogW(LL_ERR, 0, L"unpack header failed %d", dwRet);
		response.retval = E_INVALIDARG;
		dwRet =CMDcodec::PackResponse(&response, pbufResponse, noutSIZE);
		if(0==dwRet && pbufResponse)
		{
			memcpy(poutBUF, pbufResponse, noutSIZE);
			delete []pbufResponse;
		}

		return  E_FAIL;
	}

	response.oricmdid = header.cmdid;
  
	switch(header.cmdid)
	{
	case CMD_SEND_MERGE_MALI:
		{
			D2DIPC_MERGE_INFO stMerge ={0};
			dwRet = CMDcodec::UnpackMerge(pinBUF, ninSIZE, &stMerge);

			if(0!=dwRet)
			{
				logObj.LogW(LL_ERR, 0, L"unpack merge info failed %d", dwRet);
				response.retval = E_INVALIDARG;
			}
			else
			{	
				hr = OnSendMergeMail(&stMerge);
				response.retval =hr;
			}	
			
			CMDcodec::PackResponse(&response, pbufResponse, noutSIZE);
			memcpy(poutBUF, pbufResponse, noutSIZE);
			delete []pbufResponse;
		}
		break;

	case CMD_BACKUP_VM_FAILED:
		{
			D2DIPC_VMBACKUP_INFO stVM_info = {0};
			dwRet = CMDcodec::UnpackVMINFO(pinBUF, ninSIZE, &stVM_info);
			if(0!=dwRet)
			{
				logObj.LogW(LL_ERR, 0, L"unpack vm info failed %d", dwRet);
				response.retval = E_INVALIDARG;
			}
			else
			{	
				hr = OnFailedToBackupVM(&stVM_info);
				response.retval =hr;
			}	

			CMDcodec::PackResponse(&response, pbufResponse, noutSIZE);
			memcpy(poutBUF, pbufResponse, noutSIZE);
			delete []pbufResponse;
		}
		break;

	case CMD_CHECK_LICENSE:
		{
			D2D_CHKLIC_LISTENTRY listEntry = {0};
			dwRet = CMDcodec::UnpackLicList(pinBUF, ninSIZE, &header, &listEntry);
			response.cmdid	 =CMD_RES_CHECK_LICENSE;

			hr = OnCheckLicense(&listEntry);

			response.retval =hr;
			dwRet = CMDcodec::PackLicList(&response, &listEntry, pbufResponse, noutSIZE);
			CMDcodec::ReleaseLicList(&listEntry);
			if(0!=dwRet || NULL==pbufResponse)
			{
				logObj.LogW(LL_ERR, 0, L"unpack header failed %d", dwRet);
				if(pbufResponse)
				{
					delete pbufResponse;
				}

				dwRet= CMDcodec::PackResponse(&response, pbufResponse, noutSIZE);
				memcpy(poutBUF, pbufResponse, noutSIZE);
				delete []pbufResponse;

			}
			else
			{
				memcpy(poutBUF, pbufResponse, noutSIZE);
				delete []pbufResponse;
			} 
		}
		break;
		//<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm
	case CMD_CHILD_VM_JOB_CONTEXT:
	{
		D2D_CPP_VM_JOB_CONTEXT_LIST listEntry;
		dwRet = CMDcodec::Unpack_D2D_CPP_VM_JOB_CONTEXT_LIST(&listEntry, pinBUF, ninSIZE);
		response.cmdid = CMD_RES_CHILD_VM_JOB_CONTEXT;

		hr = OnSendChildJobsToProxy(&listEntry);

		response.retval = hr;
		CMDcodec::Release_D2D_CPP_VM_JOB_CONTEXT_LIST(&listEntry);
		if (0 != dwRet || NULL == pbufResponse)
		{
			logObj.LogW(LL_ERR, 0, L"unpack header failed %d", dwRet);
			if (pbufResponse)
			{
				delete pbufResponse;
			}

			dwRet = CMDcodec::PackResponse(&response, pbufResponse, noutSIZE);
			memcpy(poutBUF, pbufResponse, noutSIZE);
			delete[]pbufResponse;

		}
		else
		{
			memcpy(poutBUF, pbufResponse, noutSIZE);
			delete[]pbufResponse;
		}
	}
		break;
	}
	
	logObj.LogW(LL_INF, 0, L"OnMessage end(0x%08x)...", hr);
	return  hr;
}

HRESULT CD2DProxyImpl::OnSendMergeMail(PD2DIPC_MERGE_INFO pstInfo)
{
	if(NULL==pstInfo) return E_INVALIDARG;

	CTaskMergeMail *pTask = CTaskMergeMail::CreateInstance();
	if(NULL==pTask)
	{
		return E_OUTOFMEMORY;
	}
	pTask->SetJNIItem(m_pvJEnv);
	pTask->SetMergeInfo(pstInfo);
	HRESULT hr = FinishTask(pTask);
	hr = pTask->GetRetValue();
	pTask->Release();

	//Debug
	//pTask->Release();

	return hr;
}

HRESULT CD2DProxyImpl::OnFailedToBackupVM(PD2DIPC_VMBACKUP_INFO pstInfo)
{
	return E_NOTIMPL;
}

HRESULT CD2DProxyImpl::FinishTask(ITaskItem *pvTask)
{
	HRESULT hr = 0;
	SECURITY_DESCRIPTOR sd;
	SECURITY_ATTRIBUTES sa;
	InitializeSecurityDescriptor(&sd,SECURITY_DESCRIPTOR_REVISION);
	SetSecurityDescriptorDacl(&sd,TRUE,(PACL)NULL,FALSE); //set all the user can access the object
	sa.nLength=sizeof(SECURITY_ATTRIBUTES);
	sa.bInheritHandle=FALSE;
	sa.lpSecurityDescriptor=&sd;

	//HANDLE hSignal = CreateMutex(&sa, FALSE, NULL);
	HANDLE hSignal = CreateEvent(&sa, TRUE, FALSE ,NULL);

	if(NULL==hSignal)
	{
		logObj.LogW(LL_INF, 0,  L"Failed to CreateMutex()...." );
		return E_FAIL;
	}

	pvTask->SetNotifier(hSignal);
	this->AddTask(pvTask);

	HANDLE hEvent[2] = {0} ;
	hEvent[0] = hSignal;
	hEvent[1] = m_hQuitEvent;
	DWORD dwRet = WaitForMultipleObjects(2 ,hEvent, FALSE, D2D_TASK_TIMEOUT);

	logObj.LogW(LL_INF, 0, L"FinishTask Event return %d . timeout is:%d", dwRet, D2D_TASK_TIMEOUT);
	switch(dwRet)
	{
	case WAIT_OBJECT_0 + 0:
		{
			hr = S_OK;
			break;
		}
	case WAIT_OBJECT_0 + 1:
		{
			logObj.LogW(LL_INF, 0,  L"RECEIVE PROCESS EXIT CMD");
			hr = E_FAIL;
			break;
		} 
	case WAIT_TIMEOUT:
		{ 
			logObj.LogW(LL_INF, 0,  L"TIMEOUT HAPPEN...");
			hr = S_FALSE;
			break;
		}
	default:
		{ 
			logObj.LogW(LL_INF, 0,  L"UNKNOW REASON FOR WaitForMultipleObjects");
			hr = E_FAIL + 10;
			break;
		}
	}

	if(NULL!=hSignal)
	{
		CloseHandle(hSignal);
		hSignal =NULL;
	}

	return hr;
}

HRESULT CD2DProxyImpl::OnCheckLicense(PD2D_CHKLIC_LISTENTRY pListEntry)
{
	if(NULL==pListEntry) 
	{
		return E_INVALIDARG;
	}

	CTaskLicense *pTask = CTaskLicense::CreateInstance();
	if(NULL==pTask)
	{
		return E_OUTOFMEMORY;
	}

	pTask->Init(pListEntry);
	pTask->SetJNIItem(m_pvJEnv);
	HRESULT hr = FinishTask(pTask);
	if(S_OK ==hr)
	{
		pTask->GetCheckReslut(pListEntry);
	}
	hr = pTask->GetRetValue();
	 

	pTask->Release();
	
	return hr;
}

//<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm
HRESULT CD2DProxyImpl::OnSendChildJobsToProxy(PD2D_CPP_VM_JOB_CONTEXT_LIST pListEntry)
{
	//<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm TO DO
	if (NULL == pListEntry) //<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm #2 worker
	{
		return E_INVALIDARG;
	}

	CTaskSendChildJobsToProxy *pTask = CTaskSendChildJobsToProxy::CreateInstance();
	if (NULL == pTask)
	{
		return E_OUTOFMEMORY;
	}

	pTask->SetCTaskSendChildJobsToProxyInfo(pListEntry);
	pTask->SetJNIItem(m_pvJEnv);
	HRESULT hr = FinishTask(pTask);
	//if (S_OK == hr)
	//{
	//	pTask->GetCheckReslut(pListEntry);
	//}
	hr = pTask->GetRetValue();


	pTask->Release();

	return hr;
}

//////////////////////////////////////////////////////////////////////////
jclass CHelper::FindJavaClass(JNIEnv *env, char * pClassName)
{
	try	
	{
		jclass clsJava = env->FindClass(pClassName);
		return clsJava;
	}
	catch(...)
	{
		logObj.LogW(LL_ERR, 0xc0000005, L"FindCentralLicClass cause exception...");
		return NULL;
	}
}

jmethodID CHelper::FindMethodInJniClass(JNIEnv *env, jclass clsLicCHK,  char * methodName, char *funcSignature)
{
	jmethodID methodid = NULL;
	try
	{
		do 
		{ 
			if(clsLicCHK == NULL)
			{
				//DescribeException(m_pJNIEnvRef);
				logObj.LogW(LL_ERR, 0, L"FindMethodInJniClass: clsLicCHK is empty...");
				break;
			}

			methodid = env->GetMethodID(clsLicCHK, methodName, funcSignature);
			if (methodid == NULL)
			{
				DescribeException(env);
				logObj.LogA(LL_ERR, 0, "FindMethodInJniClass:  doesn't find the method %s - %s...",
					methodName, funcSignature);

				methodid = NULL;
				break;
			}
		} while(0); 
	}
	catch(...)
	{
		logObj.LogW(LL_ERR, 0xc0000005, L"FindMethodInJniClass cause exception...");
		methodid = NULL;
	}

	return methodid;
}

BOOL CHelper::DescribeException(JNIEnv *env)
{
	try
	{
		if (env->ExceptionCheck())
		{ 
			jthrowable e = env->ExceptionOccurred();

			char buf[1024];
			strnset(buf, 0, 1024);

			// have to clear the exception before JNI will work again. Not clearing the JNI exception so that we can fail immediately when ever we fail to load the data..
			env->ExceptionClear();

			jclass eclass = env->GetObjectClass(e);

			jmethodID mid = env->GetMethodID(eclass, "toString", "()Ljava/lang/String;");

			jstring jErrorMsg = (jstring) env->CallObjectMethod(e, mid);
			wchar_t* pMessage = JStringToWCHAR(env, jErrorMsg);
			WCHAR szMessage[1024] = {_T('\0')};
			wcscpy_s(szMessage, 1024, (const WCHAR *)pMessage);
			logObj.LogW(LL_ERR, 0, L"DescribeException:%s", szMessage);

			return false;
		}
		else
			return true;
	}

	catch(...)
	{ 
		return false;
	}
}

//////////////////////////////////////////////////////////////////////////
CTaskMergeMail::CTaskMergeMail(void)
{

}

CTaskMergeMail::~CTaskMergeMail(void)
{

}

CTaskMergeMail *  CTaskMergeMail::CreateInstance()
{
	CTaskMergeMail * pobjTask = new CTaskMergeMail;
	ITaskItem *pv;
	pobjTask->QueryInterface(__uuidof(ITaskItem), (void**)&pv);
	return pobjTask;
}

HRESULT CTaskMergeMail::SetMergeInfo(PD2DIPC_MERGE_INFO pstInfo)
{
	if(NULL==pstInfo) return E_INVALIDARG;
	memcpy(&m_mergeInfo, pstInfo, sizeof(D2DIPC_MERGE_INFO) );
	return S_OK;
}

HRESULT CTaskMergeMail::DoWork()
{
	if(NULL==m_pJnienv)
	{
		return E_INVALIDARG;
	}

	logObj.LogA(LL_INF, 0,  "Send Merge Alert Mail:job[%d], MergeSession[%d-%d]", m_mergeInfo.dwJobID, m_mergeInfo.dwFailedStart, m_mergeInfo.dwFailedEnd );
	HRESULT hr = E_NOTIMPL;
	
	jclass clsEntry = NULL;
	jmethodID jFunid= NULL;
	do 
	{
		jclass clsMginfo;
		clsMginfo = CHelper::FindJavaClass(m_pJnienv,CLS_SIG_MERGEINFOR);
		if(NULL==clsMginfo)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find class %s", CLS_SIG_MERGEINFOR);
			break;
		}

		jFunid = NULL;
		jFunid = CHelper::FindMethodInJniClass(m_pJnienv, clsMginfo, FUN_MERGEINFO_CREATE, SIG_MERGEINFO_CREATE);
		if(NULL==jFunid)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find method %s in class", FUN_MERGEINFO_CREATE, CLS_SIG_MERGEINFOR);
			break;
	 	}

		jobject objMerge;
		objMerge = m_pJnienv->NewObject(clsMginfo,jFunid);
		if(NULL==objMerge)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed create instance of class %s",CLS_SIG_MERGEINFOR);
			break;
		}

		// set merge start value
		jFunid = NULL;
		jFunid = CHelper::FindMethodInJniClass(m_pJnienv, clsMginfo, FUN_MINFO_setMergeStartSessionNumber, SIG_MINFO_setMergeStartSessionNumber);

		if(NULL==jFunid)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find method %s in class", FUN_MINFO_setMergeStartSessionNumber, CLS_SIG_MERGEINFOR);
			break;
		}

		m_pJnienv->CallObjectMethod(objMerge, jFunid, (jlong)m_mergeInfo.dwRangeStart);


		// set merge end value
		jFunid = NULL;
		jFunid = CHelper::FindMethodInJniClass(m_pJnienv, clsMginfo, FUN_MINFO_setMergeEndSessionNumber, SIG_MINFO_setMergeEndSessionNumber);

		if(NULL==jFunid)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find method %s in class", FUN_MINFO_setMergeEndSessionNumber, CLS_SIG_MERGEINFOR);
			break;
		}

		m_pJnienv->CallObjectMethod(objMerge, jFunid, (jlong)m_mergeInfo.dwRangeEnd);

		// set failed first value
		jFunid = NULL;
		jFunid = CHelper::FindMethodInJniClass(m_pJnienv, clsMginfo, FUN_MINFO_setFailedStartSession, SIG_MINFO_setFailedStartSession);

		if(NULL==jFunid)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find method %s in class", FUN_MINFO_setFailedStartSession, CLS_SIG_MERGEINFOR);
			break;
		}

		m_pJnienv->CallObjectMethod(objMerge, jFunid, (jlong)m_mergeInfo.dwFailedStart);

		// set failed second value
		jFunid = NULL;
		jFunid = CHelper::FindMethodInJniClass(m_pJnienv, clsMginfo, FUN_MINFO_setFailedEndSession, SIG_MINFO_setFailedEndSession);

		if(NULL==jFunid)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find method %s in class", FUN_MINFO_setFailedEndSession, CLS_SIG_MERGEINFOR);
			break;
		}

		m_pJnienv->CallObjectMethod(objMerge, jFunid, (jlong)m_mergeInfo.dwFailedEnd);

		 
		// set job id
		jFunid = NULL;
		jFunid = CHelper::FindMethodInJniClass(m_pJnienv, clsMginfo, FUN_INFO_SETJOBID, SIG_INFO_SETJOBID);
		if(NULL==jFunid)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find method %s in class", FUN_INFO_SETJOBID, CLS_SIG_MERGEINFOR);
			break;
		}

		m_pJnienv->CallObjectMethod(objMerge, jFunid, (jlong)m_mergeInfo.dwJobID);
		
		// set job source
		jFunid = NULL;
		jFunid = CHelper::FindMethodInJniClass(m_pJnienv, clsMginfo, FUN_INFO_SETSOURCE, SIG_INFO_SETSOURCE);
		if(NULL==jFunid)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find method %s in class", FUN_INFO_SETSOURCE, CLS_SIG_MERGEINFOR);
			break;
		}

		m_pJnienv->CallObjectMethod(objMerge, jFunid, (jlong)m_mergeInfo.dwSource);

		// set VM guid
		if(MERGE_SOURCE_BACKUP_VM==m_mergeInfo.dwSource ||MERGE_SOURCE_CATALOG_VM== m_mergeInfo.dwSource )
		{
			jFunid = NULL;
			jFunid = CHelper::FindMethodInJniClass(m_pJnienv, clsMginfo, FUN_INFO_SET_VMGUID, SIG_INFO_SET_VMGUID);

			if(NULL==jFunid)
			{
				hr = E_FAIL;
				logObj.LogA(LL_INF, 0, "failed to find method %s in class", FUN_INFO_SET_VMGUID, CLS_SIG_MERGEINFOR);
				break;
			}
			jstring jstrVMUUID;
			jstrVMUUID = WCHARToJString(m_pJnienv, m_mergeInfo.VMInstance);
			m_pJnienv->CallObjectMethod(objMerge, jFunid,  jstrVMUUID);
		}
		 
  
		//find entry class
		clsEntry = CHelper::FindJavaClass(m_pJnienv,CLS_SIG_CALLBACKENTRY);
		if(NULL==clsEntry)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find class %s", CLS_SIG_MERGEINFOR);
			break;
		}
		jFunid = NULL;
		jFunid=m_pJnienv->GetStaticMethodID(clsEntry,   FUN_ENTRY_mergeFailureCallback, SIG_ENTRY_mergeFailureCallback);
		if(NULL==jFunid)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find method %s in class", FUN_ENTRY_mergeFailureCallback, CLS_SIG_CALLBACKENTRY);
			break;
		}
		
		jlong jlRet;
		jlRet =(jlong)m_pJnienv->CallStaticLongMethod(clsEntry, jFunid, objMerge);
		m_RetVal = jlRet;
		  
	} while (0);
	
	return hr;
}



//
// class to monitor network adpater changes.
//
static std::wstring macaddr_from_buffer( const BYTE* pBuf, DWORD dwSize )
{
	WCHAR szBuf[256] = {0};
	WCHAR* p = szBuf; int nLen = 256;
	for( DWORD dw=0;dw<dwSize;dw++ )
	{
		if( dw==dwSize-1 )
		{
			swprintf_s( p, nLen, L"%.2X", (int)(pBuf[dw]) );
			p+=2;
			nLen-=2;
		}
		else
		{
			swprintf_s( p, nLen, L"%.2X-", (int)(pBuf[dw]) );
			p+=3;
			nLen-=3;
		}
	}
	return std::wstring( szBuf );
}

static std::wstring str2wstr( const std::string& strA )
{
	size_t nLen = strA.length()+1;
	WCHAR *szWchar = new WCHAR[nLen];
	ZeroMemory( szWchar, (nLen)*sizeof(WCHAR) );
	size_t nRet = MultiByteToWideChar(CP_ACP, 0, strA.c_str(), -1, szWchar, (int)nLen);
	if( nRet == 0 )
	{
		ZeroMemory( szWchar, (nLen)*sizeof(WCHAR) );
		delete [] szWchar;
		return L"";
	}

	if( (int)nRet <= nLen )
		szWchar[nLen-1] = L'\0';
	else
		ZeroMemory( szWchar, (nLen)*sizeof(WCHAR) );

	std::wstring strW( szWchar );
	delete [] szWchar;
	return strW;
}

static void addr_str_2_ip_address( const PIP_ADDR_STRING pAddr, std::vector<NIC_ADDR>& vecAddrs )
{
	if( !pAddr )
		return;

	PIP_ADDR_STRING p = pAddr;
	while( p )
	{
		NIC_ADDR addr;
		addr.ipAddr = str2wstr( p->IpAddress.String );
		addr.ipMask = str2wstr( p->IpMask.String );
		vecAddrs.push_back( addr );
		p = p->Next;
	}
}

DWORD GetRegistryVal( TCHAR *szValName, DWORD *pdwValue, DWORD dwDefaultValue )
{
    HKEY hKey;
    DWORD dwDisposition, dwValueType, dwBufSize;
    DWORD rc = ERROR_SUCCESS;

    rc = RegCreateKeyEx(HKEY_LOCAL_MACHINE,
        AFSTOR_KEY,
        0,
        NULL,
        REG_OPTION_NON_VOLATILE,
        KEY_ALL_ACCESS,
        NULL,
        &hKey,
        &dwDisposition);
    if (rc != ERROR_SUCCESS) 
    {
        goto exit;
    }

    // no matter if the key exists, query the value, if failed, create the value

    dwBufSize = sizeof(DWORD);

    rc = RegQueryValueEx(hKey,
        szValName,
        0,
        &dwValueType,
        (LPBYTE) pdwValue,
        &dwBufSize);
    if (rc != ERROR_SUCCESS) 
    {
        rc = RegSetValueEx(hKey,
            szValName,
            0,
            REG_DWORD,
            (LPBYTE) &dwDefaultValue,
            sizeof(dwDefaultValue));
        if (rc != ERROR_SUCCESS)
            goto exit;
        else
            *pdwValue = dwDefaultValue;//SECURITY_VULN [false positive]
    }
exit:
    RegCloseKey(hKey);
    if (rc != ERROR_SUCCESS) 
    {
        *pdwValue = dwDefaultValue;//SECURITY_VULN [false positive]
    }
    return(rc);
}

CNICMonitor* CNICMonitor::_instance_ = NULL;

CNICMonitor* CNICMonitor::CreateInstance( )
{
	if( !_instance_ )
	{
		_instance_ = new CNICMonitor( );
		_instance_->Init( );
	}
	return _instance_;
}

void CNICMonitor::DeleteInstance( )
{
	if( _instance_ )
		delete _instance_;
	_instance_ = NULL;
}

void CNICMonitor::OnNetworkChanges( )
{
	std::vector<NIC_INFO> tempNics;
	CNICMonitor::GetNicAdapters( tempNics, FALSE );

	std::vector<NIC_INFO>::iterator itNicNew;
	std::vector<NIC_INFO>::iterator itNicOld;
	for( itNicNew=tempNics.begin();itNicNew!=tempNics.end();itNicNew++ )
	{
		BOOL bFind = FALSE;
		for( itNicOld=m_vecNics.begin();itNicOld!=m_vecNics.end();itNicOld++ )
		{
			if( _wcsicmp( itNicOld->adapterName.c_str(), itNicNew->adapterName.c_str() ) != 0 )
				continue;

			bFind = TRUE;
			if( itNicOld->bConnected != itNicNew->bConnected )
			{
				if( itNicNew->bConnected )
					LogActivityWithDetails( AFINFO, APT_D2D, AJT_COMMON, 0, NULL, AFRES_AFCOMM_NIC_CONNECTED, (*itNicNew).nicDescription.c_str() );
				else
					LogActivityWithDetails( AFWARNING, APT_D2D, AJT_COMMON, 0, NULL, AFRES_AFCOMM_NIC_DISCONNECTED, (*itNicNew).nicDescription.c_str() );

				itNicOld->bConnected = itNicNew->bConnected;
			}
			break;
		}

		if( !bFind )
		{
			m_vecNics.push_back( (*itNicNew) );
		}
	}
}

CNICMonitor::CNICMonitor()
	: m_hThread( NULL )
	, m_hStopEvent(NULL)
	, m_bStopFlag(FALSE)
{
	m_overlap.hEvent = NULL;
	m_vecNics.clear();
}

CNICMonitor::~CNICMonitor()	
{
	if( m_hThread!=NULL )
	{
		logObj.LogW(LL_INF, 0, L"%s: Close thread handle.", __WFUNCTION__);
		::CloseHandle( m_hThread );
		m_hThread=NULL;
	}
	if( m_hStopEvent!=NULL )
	{
		logObj.LogW(LL_INF, 0, L"%s: Close stop event", __WFUNCTION__);
		::CloseHandle( m_hStopEvent );
		m_hStopEvent = NULL;
	}
	if( m_overlap.hEvent!=NULL )
	{
		logObj.LogW(LL_INF, 0, L"%s: Close overlapp event", __WFUNCTION__);
		::CloseHandle( m_overlap.hEvent );
		m_overlap.hEvent = NULL;
	}
	
	m_vecNics.clear();
}

//
// get DNS Suffix of each adapter.
//
void CNICMonitor::GetDnsSuffixes(std::vector<wstring>& dnsSuffixes)
{
	DWORD dwRet = 0;
	DWORD dwBufLen = 0;
	dwRet = GetAdaptersAddresses( AF_INET, GAA_FLAG_INCLUDE_PREFIX, NULL, NULL, &dwBufLen );

	dnsSuffixes.clear();

	if( ERROR_BUFFER_OVERFLOW!=dwRet )
	{
		logObj.LogW(LL_ERR, dwRet, L"%s: Failed to GetAdaptersAddresses", __WFUNCTION__ );
		return;
	}

	PIP_ADAPTER_ADDRESSES pAddresses = (PIP_ADAPTER_ADDRESSES)malloc( dwBufLen );
	if( !pAddresses )
	{
		logObj.LogW(LL_ERR, dwRet, L"%s: Failed to allocate memory for IP addresses. Size=%d", __WFUNCTION__, dwBufLen );
		return;
	}

	ZeroMemory( pAddresses, dwBufLen );
	dwRet = GetAdaptersAddresses( AF_INET, GAA_FLAG_INCLUDE_PREFIX, NULL, pAddresses, &dwBufLen );
	if( dwRet !=0 )
	{
		logObj.LogW(LL_ERR, dwRet, L"%s: Failed to GetAdaptersAddresses with buffer size %d", __WFUNCTION__, dwBufLen );
		free( pAddresses );
		return;
	}

	PIP_ADAPTER_ADDRESSES pOne = pAddresses;
	while( pOne )
	{						
		if( wcslen(pOne->DnsSuffix) > 0 )
		{
			bool bExist = false;
			wstring dns = _wcslwr(pOne->DnsSuffix);			
			for(std::vector<wstring>::iterator itr = dnsSuffixes.begin(); itr != dnsSuffixes.end(); itr++)
			{
				if(0 == _wcsicmp(dns.c_str(), itr->c_str()))
				{
					bExist = true; 
					break;
				}
			}

			if(!bExist)
			{
				dnsSuffixes.push_back(dns);
				logObj.LogW(LL_DBG, 0, L"%s: Added DNS suffix %s", __WFUNCTION__, dns.c_str() );
			}
		}

		pOne = pOne->Next;
	}
	free( pAddresses );
}

void CNICMonitor::GetNicAdapters( std::vector<NIC_INFO>& vecNics, BOOL bDetails )
{
	PIP_ADAPTER_INFO pInfo = NULL;
	DWORD dwLen = 0;

	logObj.LogW(LL_DBG, 0, L"%s: Calling GetAdaptersInfo 1", __WFUNCTION__ );
	DWORD dwRet = GetAdaptersInfo(pInfo, &dwLen);
	logObj.LogW(LL_DBG, 0, L"%s: GetAdaptersInfo returned 1", __WFUNCTION__ );
	
	if(dwRet != ERROR_BUFFER_OVERFLOW)
	{
		logObj.LogW(LL_ERR, dwRet, L"%s: Failed to call function GetAdaptersInfo with NULL parameters", __WFUNCTION__ );
		return;
	}

	pInfo = (PIP_ADAPTER_INFO)malloc(dwLen);
	if(!pInfo)
		return;
	ZeroMemory(pInfo, dwLen);

	logObj.LogW(LL_DBG, 0, L"%s: Calling GetAdaptersInfo 2", __WFUNCTION__ );
	dwRet = GetAdaptersInfo(pInfo, &dwLen);
	logObj.LogW(LL_DBG, 0, L"%s: GetAdaptersInfo returned 2", __WFUNCTION__ );

	if(dwRet)
	{
		logObj.LogW(LL_ERR, dwRet, L"%s: Failed to call function GetAdaptersInfo", __WFUNCTION__ );
		free( pInfo );
		return;
	}

	PIP_ADAPTER_INFO pTmpInfo = pInfo;
	while(pTmpInfo)
	{
		NIC_INFO nic;
		nic.adapterName = str2wstr( pTmpInfo->AdapterName );
		nic.nicDescription = str2wstr( pTmpInfo->Description );
		nic.bDhcpEnabled = pTmpInfo->DhcpEnabled;
		nic.bHaveWins = pTmpInfo->HaveWins;
		nic.macAddr = macaddr_from_buffer( pTmpInfo->Address, pTmpInfo->AddressLength );
		nic.uType = pTmpInfo->Type;
		addr_str_2_ip_address( &(pTmpInfo->IpAddressList), nic.ipAddrList );
		addr_str_2_ip_address( &(pTmpInfo->GatewayList), nic.gatewayList );
		addr_str_2_ip_address( &(pTmpInfo->DhcpServer), nic.dhcpServers );
		addr_str_2_ip_address( &(pTmpInfo->PrimaryWinsServer), nic.primaryWinsServer );
		addr_str_2_ip_address( &(pTmpInfo->SecondaryWinsServer), nic.secondWinsServer );

		logObj.LogW(LL_DBG, 0, L"%s: Calling IsAdapterConnected", __WFUNCTION__ );
		nic.bConnected = IsAdapterConnected( nic.adapterName );
		logObj.LogW(LL_DBG, 0, L"%s: Calling IsAdapterConnected returned", __WFUNCTION__ );

		if( bDetails )
		{
			logObj.LogW(LL_DBG, 0, L"%s: Calling GetAdapterSpeed", __WFUNCTION__ );
			nic.dwSppeed = GetAdapterSpeed( nic.adapterName ) / 10000;
			logObj.LogW(LL_DBG, 0, L"%s: Calling GetAdapterSpeed returned", __WFUNCTION__ );
		}

		vecNics.push_back( nic );
		pTmpInfo = pTmpInfo->Next;
	}

	free( pInfo );


}

void CNICMonitor::Init( )
{
	CNICMonitor::GetNicAdapters( m_vecNics, FALSE );

	for( size_t i=0;i<m_vecNics.size();i++ )
		OutputNICInfo( m_vecNics[i] );

    FilterOutE1000e(m_vecNics);
}

BOOL CNICMonitor::IsAdapterConnected( const wstring& strAdapterName )
{
	WCHAR szDevName[MAX_PATH] = {0};
	wcscpy_s( szDevName, _ARRAYSIZE(szDevName), L"\\\\.\\" );
	wcscat_s( szDevName, _ARRAYSIZE(szDevName), strAdapterName.c_str() );

	HANDLE hFile = CreateFileW(szDevName, GENERIC_READ, FILE_SHARE_READ|FILE_SHARE_WRITE, NULL,
      OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);

   if(INVALID_HANDLE_VALUE == hFile)
	   return FALSE;

   BOOL bConnected = FALSE;
   DWORD dwObj = OID_GEN_MEDIA_CONNECT_STATUS;
   DWORD dwStatus = 0;
   DWORD dwSize = 0;

   if(DeviceIoControl(hFile, IOCTL_NDIS_QUERY_GLOBAL_STATS, &dwObj, sizeof(dwObj), &dwStatus, sizeof(dwStatus), &dwSize, NULL))
   {
      if(NdisMediaStateConnected == dwStatus)
         bConnected = TRUE;
      if(NdisMediaStateDisconnected == dwStatus)
         bConnected = FALSE;
   }

   CloseHandle(hFile);
   return bConnected;   
}

DWORD CNICMonitor::GetAdapterSpeed( const wstring& strAdapterName )
{
	DWORD dwSpeed = 0;
	WCHAR szDevName[MAX_PATH] = {0};
	wcscpy_s( szDevName, _ARRAYSIZE(szDevName), L"\\\\.\\" );
	wcscat_s( szDevName, _ARRAYSIZE(szDevName), strAdapterName.c_str() );

	HANDLE hFile = CreateFileW(szDevName, GENERIC_READ, FILE_SHARE_READ|FILE_SHARE_WRITE, NULL,
      OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);

   if(INVALID_HANDLE_VALUE == hFile)
	   return dwSpeed;

   BOOL bConnected = FALSE;
   DWORD dwObj = OID_GEN_LINK_SPEED;
   DWORD dwSize = 0;

   DeviceIoControl(hFile, IOCTL_NDIS_QUERY_GLOBAL_STATS, &dwObj, sizeof(dwObj), &dwSpeed, sizeof(dwSpeed), &dwSize, NULL);
   CloseHandle(hFile);
   return dwSpeed;   
}

BOOL CNICMonitor::IsTSOEnabled( const wstring& strAdapterName )
{
    WCHAR szDevName[MAX_PATH] = {0};
    wcscpy_s( szDevName, _ARRAYSIZE(szDevName), L"\\\\.\\" );
    wcscat_s( szDevName, _ARRAYSIZE(szDevName), strAdapterName.c_str() );

    HANDLE hFile = CreateFileW(szDevName, GENERIC_READ, FILE_SHARE_READ|FILE_SHARE_WRITE, NULL,
        OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);

    if(INVALID_HANDLE_VALUE == hFile)
    {
        return TRUE; // Use WMI to retry
    }

    BOOL bEnabled = FALSE;
    DWORD dwSize = 0;
    DWORD dwEncapsulation  = 0;
    DWORD dwObj = OID_TCP_OFFLOAD_CURRENT_CONFIG;
    NDIS_OFFLOAD  oidRequest = {0};

    if (FALSE == DeviceIoControl(hFile, IOCTL_NDIS_QUERY_GLOBAL_STATS, &dwObj, sizeof(dwObj), &oidRequest, sizeof(oidRequest), &dwSize, NULL))
    {
        logObj.LogW( LL_ERR, 0, L"%s: Failed to get TSO state by IOCtrl. Ret=[%d].", __WFUNCTION__, GetLastError() );
        bEnabled = TRUE; // Use WMI to retry
    }
    else
    {
        bEnabled = 
            (oidRequest.LsoV2.IPv4.Encapsulation != NDIS_ENCAPSULATION_NOT_SUPPORTED || oidRequest.LsoV2.IPv6.Encapsulation != NDIS_ENCAPSULATION_NOT_SUPPORTED);
    }

    CloseHandle(hFile);
    return bEnabled; 
}

DWORD CNICMonitor::Start( )
{
	m_hThread = ::CreateThread( NULL, 0, (LPTHREAD_START_ROUTINE)(CNICMonitor::MonitorThreadFunc), (this), CREATE_SUSPENDED, NULL );
	if( m_hThread==NULL )
		return GetLastError( );

	::ResumeThread( m_hThread );
	return 0;
}

void CNICMonitor::Stop( )
{
	logObj.LogW( LL_INF, 0, L"%s: Stopping NIC Monitor thread...", __WFUNCTION__ );

	if( m_hThread==NULL )
		return;

	if( m_hStopEvent==NULL )
		return;

	if( m_overlap.hEvent==NULL )
		return;

	m_bStopFlag = TRUE;
	::SetEvent( m_hStopEvent );
	::CancelIPChangeNotify( &m_overlap );

	BOOL bThreadStopped = FALSE;
	for( int i=0;i<5;i++ )
	{
		if( WAIT_OBJECT_0 == ::WaitForSingleObject(m_hThread, 1000) )
		{
			bThreadStopped = TRUE;
			break;
		}
		else
		{
			continue;
		}
	}

	if( !bThreadStopped )
	{
		logObj.LogW( LL_ERR, 0, L"%s: Cannot stop nic mornitor thread in 5 seconds. Terminate it.", __WFUNCTION__);
		::TerminateThread( m_hThread, 0 );
	}

	logObj.LogW( LL_INF, 0, L"%s: NIC Monitor thread stopped.", __WFUNCTION__ );
}

void CNICMonitor::OutputNICInfo( const NIC_INFO& nic )
{
	logObj.LogW(LL_DBG, 0, L"====== Network Adapter ======" );
	logObj.LogW(LL_DBG, 0, L"     Name: %s", nic.adapterName.c_str() );
	logObj.LogW(LL_DBG, 0, L"     Desc: %s", nic.nicDescription.c_str() );
	logObj.LogW(LL_DBG, 0, L"     Connected: %d", nic.bConnected );
	logObj.LogW(LL_DBG, 0, L"     DHCP: %d", nic.bDhcpEnabled );
	logObj.LogW(LL_DBG, 0, L"     Have Wins: %d", nic.bHaveWins );
	logObj.LogW(LL_DBG, 0, L"     Mac Addr: %s", nic.macAddr.c_str() );
	logObj.LogW(LL_DBG, 0, L"     Speed: %d", nic.dwSppeed );
	logObj.LogW(LL_DBG, 0, L"     Type: %d", nic.uType );
	
	logObj.LogW(LL_DBG, 0, L"     IP Address List:" );
	for( size_t i=0;i<nic.ipAddrList.size();i++ )
		logObj.LogW(LL_DBG, 0, L"          IP[%s] Mask[%s]", nic.ipAddrList[i].ipAddr.c_str(), nic.ipAddrList[i].ipMask.c_str() );

	logObj.LogW(LL_DBG, 0, L"     DHCP Servers: " );
	for( size_t i=0;i<nic.dhcpServers.size();i++ )
		logObj.LogW(LL_DBG, 0, L"          IP[%s] Mask[%s]", nic.dhcpServers[i].ipAddr.c_str(), nic.dhcpServers[i].ipMask.c_str() );

	logObj.LogW(LL_DBG, 0, L"     Gateway List: " );
	for( size_t i=0;i<nic.gatewayList.size();i++ )
		logObj.LogW(LL_DBG, 0, L"          IP[%s] Mask[%s]", nic.gatewayList[i].ipAddr.c_str(), nic.gatewayList[i].ipMask.c_str() );

	logObj.LogW(LL_DBG, 0, L"     Primary Win Servers:  " );
	for( size_t i=0;i<nic.primaryWinsServer.size();i++ )
		logObj.LogW(LL_DBG, 0, L"          IP[%s] Mask[%s]", nic.primaryWinsServer[i].ipAddr.c_str(), nic.primaryWinsServer[i].ipMask.c_str() );

	logObj.LogW(LL_DBG, 0, L"     Second Win Servers: " );
	for( size_t i=0;i<nic.secondWinsServer.size();i++ )
		logObj.LogW(LL_DBG, 0, L"          IP[%s] Mask[%s]", nic.secondWinsServer[i].ipAddr.c_str(), nic.secondWinsServer[i].ipMask.c_str() );
}

DWORD CNICMonitor::FilterOutE1000e( std::vector<NIC_INFO>& vecNics )
{
    BOOL bTSODisabled = FALSE;
    wstring wsE1000eName = L"Intel(R) 82574"; // Part of name identifier of E1000e NIC

    DWORD dwSkipNICCheck = 0;
    GetRegistryVal(L"SkipNICCheck", &dwSkipNICCheck, 0);
    if (dwSkipNICCheck != 0)
    {
        logObj.LogW( LL_WAR, 0, L"%s: Skip NIC check and disable logic according to registry.", __WFUNCTION__ );
        return ERROR_SUCCESS;
    }

    for( size_t i=0;i<vecNics.size();i++ )
    {
        if (vecNics[i].nicDescription.find(wsE1000eName) != wstring::npos)
        {
            logObj.LogW(LL_WAR, 0, L"!!!Caution: Found E1000e NIC which may incur data corruption. Index[%d], Name[%s], Desc[%s], MAC[%s].",
                i, vecNics[i].adapterName.c_str(), vecNics[i].nicDescription.c_str(), vecNics[i].macAddr.c_str());

            if (IsTSOEnabled(vecNics[i].adapterName))
            {
                BOOL bRet = FALSE;
                WCHAR wzComputerName[MAX_PATH] = { 0 };
                DWORD dwComputerNameLen = _countof(wzComputerName);
                GetComputerNameW(wzComputerName, &dwComputerNameLen);
                CWMIService* pWMIService = new CWMIService();
                if ((bRet = pWMIService->Initialize(L"ROOT\\StandardCimv2")) == FALSE)
                {
                    logObj.LogW( LL_ERR, 0, L"%s: Failed to init CWMIService on Namespace[%s]. Ret=[%d].", __WFUNCTION__, L"ROOT\\StandardCimv2", pWMIService->GetLastError() );
                    LogActivityWithDetails(AFERROR, APT_D2D, AJT_COMMON, 0, NULL, AFRES_AFCOMM_E1000E_DISABLE_FAIL, vecNics[i].nicDescription.c_str(), wzComputerName);
                }
                else if ((bRet = pWMIService->DisableTSO(vecNics[i].adapterName)) == FALSE)
                {
                    logObj.LogW( LL_ERR, 0, L"%s: Failed to disable TSO. Ret=[%d].", __WFUNCTION__, pWMIService->GetLastError() );
                    LogActivityWithDetails(AFERROR, APT_D2D, AJT_COMMON, 0, NULL, AFRES_AFCOMM_E1000E_DISABLE_FAIL, vecNics[i].nicDescription.c_str(), wzComputerName);
                }
                else if (IsTSOEnabled(vecNics[i].adapterName))
                {
                    // For some old Intel driver, DisableTSO succeeds but it's actually not disabled,
                    // so we have to check TSO state again
                    logObj.LogW( LL_ERR, 0, L"%s: This driver doesn't support disabling TSO programmatically.", __WFUNCTION__ );
                    LogActivityWithDetails(AFERROR, APT_D2D, AJT_COMMON, 0, NULL, AFRES_AFCOMM_E1000E_DISABLE_FAIL, vecNics[i].nicDescription.c_str(), wzComputerName);
                }
                else
                {
                    logObj.LogW(LL_WAR, 0, L"Disable TSO successfully for this adapter. Index[%d], Name[%s], Desc[%s], MAC[%s].",
                        i, vecNics[i].adapterName.c_str(), vecNics[i].nicDescription.c_str(), vecNics[i].macAddr.c_str());
                    LogActivityWithDetails(AFWARNING, APT_D2D, AJT_COMMON, 0, NULL, AFRES_AFCOMM_E1000E_DISABLED, vecNics[i].nicDescription.c_str(), wzComputerName);
                    bTSODisabled = TRUE;
                }
                pWMIService->Uninitialize();
                delete pWMIService;
                pWMIService = NULL;
            }
            else
            {
                logObj.LogW(LL_WAR, 0, L"TSO has already been disabled for this adapter. Index[%d], Name[%s], Desc[%s], MAC[%s].",
                    i, vecNics[i].adapterName.c_str(), vecNics[i].nicDescription.c_str(), vecNics[i].macAddr.c_str());
            }
        }
    }

    // Wait a while for network connectivity to recover if TSO is disabled for at least one NIC
    if (bTSODisabled == TRUE)
    {
        Sleep(20000);
    }

    return ERROR_SUCCESS;
}

DWORD CNICMonitor::Run( )
{
	m_overlap.hEvent = ::CreateEvent( NULL, TRUE, FALSE, NULL );
	if( m_overlap.hEvent==NULL )
	{
		logObj.LogW( LL_ERR, GetLastError(), L"%s: Failed to create a overlapped event.", __WFUNCTION__ );
		return 0;
	}

	m_hStopEvent = ::CreateEvent( NULL, TRUE, FALSE, NULL );
	if( m_hStopEvent==NULL )
	{
		logObj.LogW( LL_ERR, GetLastError(), L"%s: Failed to create a stop event.", __WFUNCTION__ );
		return 0;
	}

	while( !m_bStopFlag )
	{
		logObj.LogW( LL_INF, 0, L"%s: Start to monitor NIC changes.", __WFUNCTION__ );

		HANDLE hand = NULL;
		DWORD ret = NotifyAddrChange(&hand, &m_overlap);
		if(ret != NO_ERROR && ret!= ERROR_IO_PENDING)
		{
			logObj.LogW( LL_ERR, GetLastError(), L"%s: Failed to call API NotifyAddrChange. err=%d", __WFUNCTION__, ret );
			return 0;
		}

		BOOL bStopThread = FALSE;
		while( !m_bStopFlag )
		{
			DWORD dwWait = ::WaitForSingleObject( m_overlap.hEvent, 3000 );
			if( WAIT_OBJECT_0 == dwWait )
			{
				if( WAIT_OBJECT_0 == ::WaitForSingleObject( m_hStopEvent, 0) )
					bStopThread = TRUE;
				break;			
			}
			else if ( WAIT_TIMEOUT == dwWait )
			{
				if( WAIT_OBJECT_0 == ::WaitForSingleObject( m_hStopEvent, 0) )
				{
					bStopThread = TRUE;
					break;			
				}
			}
			else
			{
				logObj.LogW( LL_ERR, GetLastError(), L"%s: Wait event returned %d. Exit monitor thread.", __WFUNCTION__, dwWait );
				return 0;
			}
		}

		if( bStopThread )
		{
			logObj.LogW( LL_INF, GetLastError(), L"%s: NIC thread func exit.", __WFUNCTION__ );
			break;
		}

		logObj.LogW( LL_INF, 0, L"%s: NIC changes detected.", __WFUNCTION__ );

		if( !m_bStopFlag )
		{
			OnNetworkChanges( );
			::ResetEvent( m_overlap.hEvent );
		}
	}

	return 0;
}

DWORD WINAPI CNICMonitor::MonitorThreadFunc( LPVOID pArg )
{
	CNICMonitor* pMonitor = (CNICMonitor*)pArg;
	if( !pMonitor )
		return 87;

	return pMonitor->Run( );
}

//<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm #2 worker
CTaskSendChildJobsToProxy::CTaskSendChildJobsToProxy(void)
{

}

CTaskSendChildJobsToProxy::~CTaskSendChildJobsToProxy(void)
{
	CMDcodec::Release_D2D_CPP_VM_JOB_CONTEXT_LIST(&m_Info);
}

CTaskSendChildJobsToProxy * CTaskSendChildJobsToProxy::CreateInstance()
{
	CTaskSendChildJobsToProxy * pobjTask = new CTaskSendChildJobsToProxy;
	ITaskItem *pv;
	pobjTask->QueryInterface(__uuidof(ITaskItem), (void**)&pv);
	return pobjTask;
}

HRESULT CTaskSendChildJobsToProxy::SetCTaskSendChildJobsToProxyInfo(PD2D_CPP_VM_JOB_CONTEXT_LIST pstInfo)
{
	m_Info.listsize = pstInfo->listsize;
	m_Info.items = new D2D_CPP_VM_JOB_CONTEXT_ITEM[m_Info.listsize];
	CopyMemory(m_Info.items, pstInfo->items, sizeof(D2D_CPP_VM_JOB_CONTEXT_ITEM) * m_Info.listsize);
	return S_OK;
}

//<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm #2 worker
HRESULT CTaskSendChildJobsToProxy::DoWork()
{
	if (NULL == m_pJnienv)
	{
		return E_INVALIDARG;
	}

	for (DWORD ii = 0; ii < m_Info.listsize; ++ ii)
	{
		logObj.LogA(LL_INF, 0, "Send ChildJobsToProxy:MasterJob[%d], job[%d]", m_Info.items[ii].dwMasterJobId, m_Info.items[ii].dwJobId);
	}
	
	HRESULT hr = E_NOTIMPL;

	jclass clsEntry = NULL;
	jmethodID jFunid = NULL;

	do
	{
		clsEntry = CHelper::FindJavaClass(m_pJnienv, CLS_SIG_CALLBACKENTRY);
		if (NULL == clsEntry)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find class %s", CLS_SIG_CALLBACKENTRY);
			break;
		}

		jFunid = NULL;
		jFunid = m_pJnienv->GetStaticMethodID(clsEntry, "submitVAppChildVMBackupCallback", "(Ljava/util/List;)J");
		if (NULL == jFunid)
		{
			hr = E_FAIL;
			logObj.LogA(LL_INF, 0, "failed to find method %s in class", "submitVAppChildVMBackupCallback", "(Ljava/util/ArrayList;)J");
			break;
		}

		jobject jobjList = NULL;
		NewList(m_pJnienv, jobjList, m_Info);

		jlong jlRet;
		jlRet = (jlong)m_pJnienv->CallStaticLongMethod(clsEntry, jFunid, jobjList);
		m_RetVal = jlRet;


	} while (0);

	return hr;
}

//<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm #2 worker
HRESULT CTaskSendChildJobsToProxy::NewObject(JNIEnv *env, jobject & JObjOut, const D2D_CPP_VM_JOB_CONTEXT_ITEM & item)
{
	jclass class_type = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobContext");

	//////////////////////////////////////////////////////////////////////////
	jmethodID mid_constructor = env->GetMethodID(class_type, "<init>", "()V");
	jobject jObj = env->NewObject(class_type, mid_constructor);

	//////////////////////////////////////////////////////////////////////////
#define CTaskSendChildJobsToProxy_NewObject_SetFieldLong(fieldName) \
					{ \
			jfieldID field_##fieldName = env->GetFieldID(class_type, #fieldName, "J"); \
			env->SetLongField(jObj, field_##fieldName, item.##fieldName); \
					}

#define CTaskSendChildJobsToProxy_NewObject_SetFieldString(fieldName) \
					{ \
		jfieldID field_##fieldName = env->GetFieldID(class_type, #fieldName, "Ljava/lang/String;"); \
		jstring jstr_##fieldName = WCHARToJString(env, item.##fieldName); \
		env->SetObjectField(jObj, field_##fieldName, jstr_##fieldName); \
		if (jstr_##fieldName != NULL) env->DeleteLocalRef(jstr_##fieldName); \
					}

	//////////////////////////////////////////////////////////////////////////
	//CTaskSendChildJobsToProxy_NewObject_SetFieldLong(dwJobId);
	//CTaskSendChildJobsToProxy_NewObject_SetFieldLong(dwQueueType);
	//CTaskSendChildJobsToProxy_NewObject_SetFieldLong(dwJobType);
	//CTaskSendChildJobsToProxy_NewObject_SetFieldLong(dwProcessId);
	//CTaskSendChildJobsToProxy_NewObject_SetFieldLong(dwJMShmId);
	//CTaskSendChildJobsToProxy_NewObject_SetFieldLong(dwLauncher);
	//CTaskSendChildJobsToProxy_NewObject_SetFieldLong(dwPriority);
	//CTaskSendChildJobsToProxy_NewObject_SetFieldLong(dwMasterJobId);

	jfieldID field_dwJobId = env->GetFieldID(class_type, "dwJobId", "J");
	env->SetLongField(jObj, field_dwJobId, item.dwJobId);

	jfieldID field_dwQueueType = env->GetFieldID(class_type, "dwQueueType", "J");
	env->SetLongField(jObj, field_dwQueueType, item.dwQueueType);


	jfieldID field_dwJobType = env->GetFieldID(class_type, "dwJobType", "J");
	env->SetLongField(jObj, field_dwJobType, item.dwJobType);

	jfieldID field_dwProcessId = env->GetFieldID(class_type, "dwProcessId", "J");
	env->SetLongField(jObj, field_dwProcessId, item.dwProcessId);

	jfieldID field_dwJMShmId = env->GetFieldID(class_type, "dwJMShmId", "J");
	env->SetLongField(jObj, field_dwJMShmId, item.dwJMShmId);

	jfieldID field_dwLauncher = env->GetFieldID(class_type, "dwLauncher", "J");
	env->SetLongField(jObj, field_dwLauncher, item.dwLauncher);

	jfieldID field_dwPriority = env->GetFieldID(class_type, "dwPriority", "J");
	env->SetLongField(jObj, field_dwPriority, item.dwPriority);

	jfieldID field_dwMasterJobId = env->GetFieldID(class_type, "dwMasterJobId", "J");
	env->SetLongField(jObj, field_dwMasterJobId, item.dwMasterJobId);


	//CTaskSendChildJobsToProxy_NewObject_SetFieldString(executerInstanceUUID);
	//CTaskSendChildJobsToProxy_NewObject_SetFieldString(launcherInstanceUUID);
	//CTaskSendChildJobsToProxy_NewObject_SetFieldString(generatedDestination);

	jfieldID field_executerInstanceUUID = env->GetFieldID(class_type, "executerInstanceUUID", "Ljava/lang/String;");
	jstring jstr_executerInstanceUUID = WCHARToJString(env, item.executerInstanceUUID);
	env->SetObjectField(jObj, field_executerInstanceUUID, jstr_executerInstanceUUID);
	if (jstr_executerInstanceUUID != NULL) env->DeleteLocalRef(jstr_executerInstanceUUID);

	jfieldID field_launcherInstanceUUID = env->GetFieldID(class_type, "launcherInstanceUUID", "Ljava/lang/String;");
	jstring jstr_launcherInstanceUUID = WCHARToJString(env, item.launcherInstanceUUID);
	env->SetObjectField(jObj, field_launcherInstanceUUID, jstr_launcherInstanceUUID);
	if (jstr_launcherInstanceUUID != NULL) env->DeleteLocalRef(jstr_launcherInstanceUUID);

	jfieldID field_generatedDestination = env->GetFieldID(class_type, "generatedDestination", "Ljava/lang/String;");
	jstring jstr_generatedDestination = WCHARToJString(env, item.generatedDestination);
	env->SetObjectField(jObj, field_generatedDestination, jstr_generatedDestination);
	if (jstr_generatedDestination != NULL) env->DeleteLocalRef(jstr_generatedDestination);


	JObjOut = jObj;

	if(class_type != NULL) env->DeleteLocalRef(class_type);

	return S_OK;
}

HRESULT CTaskSendChildJobsToProxy::NewList(JNIEnv *env, jobject& JObjOut, const D2D_CPP_VM_JOB_CONTEXT_LIST & items)
{
	jclass arrayListClass = env->FindClass("java/util/ArrayList");
	jmethodID arrayListConstr = env->GetMethodID(arrayListClass, "<init>", "()V");
	jmethodID listAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
	jobject jobjectList = env->NewObject(arrayListClass, arrayListConstr);

	for (DWORD32 ii = 0; ii < items.listsize; ++ii)
	{
		jobject jitem = NULL;
		NewObject(env, jitem, items.items[ii]);
		env->CallBooleanMethod(jobjectList, listAddMethod, jitem);
	}

	JObjOut = jobjectList;
	return S_OK;
}
