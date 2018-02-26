#include "stdafx.h"
#include "UpdateJobManager.h"
#include "..\include\UpdateError.h"

CUpdateJobManager g_upJobManager;

CDaemonThread::CDaemonThread(DWORD dwProdOn, DWORD dwProdFor, PARCUPDATE_SERVER_INFO pSvrInfo, IUpdateJobMonitor* pJobMonitor, HANDLE hJobProcess)
	: CThreadBase(TRUE)
	, m_dwProdOn(dwProdOn)
	, m_dwProdFor(dwProdFor)
	, m_pSvrInfo(NULL)
	, m_pJobMonitor(pJobMonitor)
	, m_hUpProcess(hJobProcess)
{
	if (pSvrInfo){
		m_pSvrInfo = new ARCUPDATE_SERVER_INFO();
		(*m_pSvrInfo) = (*pSvrInfo);
	}
}

CDaemonThread::~CDaemonThread()
{
	::CloseHandle(m_hUpProcess);
	m_hUpProcess = NULL;

	SAFE_RELEASE(m_pJobMonitor);
	SAFE_DELETE(m_pSvrInfo);
}

DWORD CDaemonThread::Main()
{
	m_log.LogW(LL_DET, 0, L"Start a daemon thread for update job %d", m_dwProdFor);

	while (1)
	{
		if ( m_pJobMonitor->IsJobCanceled())
			break;

		if (WAIT_TIMEOUT == WaitForSingleObject(m_hUpProcess, 3000))
		{
			continue;
		}
		else
		{
			if (m_pJobMonitor->GetJobPhase() != AJP_END)
			{
				m_pJobMonitor->EndUpdateJob(AJS_FAILED, inter_error_update_crash );
			}
			break;
		}
	}

	UPDATE_JOB_MONITOR lastJm; ZeroMemory(&lastJm, sizeof(lastJm));
	m_pJobMonitor->GetDataOfJobMonitor(lastJm);
	g_upJobManager.CacheLastJobStatus(m_dwProdFor, lastJm);

	if (lastJm.lLastError == inter_error_selfupdate_required)
		g_upJobManager.StartUpdateJob(m_dwProdOn, ARCUPDATE_PRODUCT_SELFUPDATE, m_pSvrInfo );

	m_log.LogW(LL_DET, 0, L"Exit daemon thread. Update job returned %d", lastJm.lLastError );
	return 0;
}

//
// --------------------------------------------------------------------------------------
//
CUpdateJobManager::CUpdateJobManager()
	: m_stopFlag(FALSE)
{
	::InitializeCriticalSection(&m_cs);
}

CUpdateJobManager::~CUpdateJobManager()
{
	EnterCriticalSection(&m_cs);

	std::map<DWORD, PUPDATE_JOB_MONITOR>::iterator it;
	for (it = m_mapLastJobStatus.begin(); it != m_mapLastJobStatus.end(); it++)
		delete it->second;
	m_mapLastJobStatus.clear();

	::LeaveCriticalSection(&m_cs);

	::DeleteCriticalSection(&m_cs);
}

DWORD CUpdateJobManager::CancelUpdateJob(DWORD dwProd)
{
	m_log.LogW(LL_INF, 0, L"Cancel update job for product %d", dwProd);

	IUpdateJobMonitor* pJobMonitor = NULL;
	OpenIUpdateJobMonitor(dwProd, &pJobMonitor);
	if (pJobMonitor)
	{
		pJobMonitor->CancelUpdateJob();
		SAFE_RELEASE(pJobMonitor);
	}
	return 0;
}

DWORD CUpdateJobManager::QueryUpdateJobStatus(DWORD dwProd, UPDATE_JOB_MONITOR& lastJm)
{
	m_log.LogW(LL_DET, 0, L"Query update status of job %d", dwProd );

	IUpdateJobMonitor* pJobMonitor = NULL;
	OpenIUpdateJobMonitor(dwProd, &pJobMonitor);
	if (pJobMonitor)
	{
		DWORD dwRet = pJobMonitor->GetDataOfJobMonitor(lastJm);
		SAFE_RELEASE(pJobMonitor);
		return dwRet;
	}
	
	::EnterCriticalSection(&m_cs);
	BOOL bFound = FALSE;
	std::map<DWORD, PUPDATE_JOB_MONITOR>::iterator it;
	for (it = m_mapLastJobStatus.begin(); it != m_mapLastJobStatus.end(); it++)
	{
		if (it->first == dwProd)
		{
			memcpy_s(&lastJm, sizeof(UPDATE_JOB_MONITOR), it->second, sizeof(UPDATE_JOB_MONITOR));
			bFound = TRUE;
			break;
		}
	}
	if (!bFound)
	{
		ZeroMemory(&lastJm, sizeof(lastJm));
	}
	::LeaveCriticalSection(&m_cs);
	return 0;
}

LONG CUpdateJobManager::StartUpdateJob(DWORD dwProdOn, DWORD dwProdFor, PARCUPDATE_SERVER_INFO pSvrInfo)
{
	m_log.LogW(LL_DET, 0, L"Start a new update job for %d. SvrInfo=%0xp", dwProdFor, pSvrInfo );

	LONG lRet = 0;
	IUpdateJobMonitor* pJobMonitor = NULL;
	EnterCriticalSection(&m_cs);
	do
	{
		if (m_stopFlag){
			m_log.LogW(LL_ERR, 0, L"UDP update service is stopping...");
			lRet = inter_error_service_stopped;
			break;
		}

		OpenIUpdateJobMonitor(dwProdFor, &pJobMonitor);
		if (pJobMonitor){
			m_log.LogW(LL_ERR, 0, L"Another instance is running for checking update %d", dwProdFor );
			lRet = inter_error_update_busy;
			break;
		}

		CreateIUpdateJobMonitor(dwProdFor, &pJobMonitor);
		if (!pJobMonitor){
			m_log.LogW(LL_ERR, 0, L"Failed to create job monitor for update %d", dwProdFor);
			lRet = inter_error_failed_trigger_job;
			break;
		}

		pJobMonitor->StartUpdateJob();
		//
		// create job script
		//
		wstring strJobScriptFile = STRUTILS::fstr(L"update_%d_%d.xml", dwProdFor, GetTickCount());
		strJobScriptFile = PATHUTILS::path_join(PATHUTILS::home_dir(), strJobScriptFile);
		wstring strDownloadDir = UPUTILS::GetDownloadHomeDirectory(dwProdFor);

		CXXmlNode* pRoot = CXXmlNode::CreateXmlNode(XML_UpdateJobScript);
		AddChildNode(pRoot, XML_Product, STRUTILS::fstr(L"%d", dwProdFor));
		AddChildNode(pRoot, XML_TriggerProduct, STRUTILS::fstr(L"%d", dwProdOn));
		
		if ( dwProdFor==ARCUPDATE_PRODUCT_AGENT)
			AddChildNode(pRoot, XML_MailAlert, L"true" );
		else
			AddChildNode(pRoot, XML_MailAlert, L"false");
		
		if (dwProdFor == dwProdOn)
			AddChildNode(pRoot, XML_IgnoreVersionCheck, L"false");
		else
			AddChildNode(pRoot, XML_IgnoreVersionCheck, L"true");

		wstring strUsername = L"", strPassword = L"";
		UPUTILS::GetAdminUserOfProduct(dwProdOn, strUsername, strPassword);
		wstring strEncPassword = L"";
		ENCUTILS::EncryptToString(strPassword.c_str(), strEncPassword);
		AddChildNode(pRoot, XML_AdminUser, strUsername);
		AddChildNode(pRoot, XML_AdminPassword, strEncPassword);
		AddChildNode(pRoot, XML_DownloadDir, strDownloadDir);
		if (pSvrInfo)
		{
			CXXmlNode* pDownloadServer = AddChildNode(pRoot, XML_DownloadServer, L"");
			pDownloadServer->SetAttributeInt(XML_type, (int)pSvrInfo->serverType);
			if (pSvrInfo->serverType == ARCUPDATE_SERVER_DEFAULT)
			{
				CXXmlNode* pProxy = AddChildNode(pDownloadServer, XML_Proxy, L"");
				pProxy->SetAttribute(XML_useProxy, pSvrInfo->bDefaultIEProxy ? L"false" : L"true");
				if (!pSvrInfo->bDefaultIEProxy)
				{
					AddChildNode(pProxy, XML_ProxyServerName, pSvrInfo->proxyServerName);
					AddChildNode(pProxy, XML_ProxyUserName, pSvrInfo->proxyUserName);
					AddChildNode(pProxy, XML_ProxyPassword, pSvrInfo->proxyPassword);
					AddChildNode(pProxy, XML_ProxyServerPort, STRUTILS::fstr(L"%d", pSvrInfo->proxyServerPort));
				}
			}
			else
			{
				AddChildNode(pDownloadServer, XML_StagingServer, pSvrInfo->downloadServer);
				AddChildNode(pDownloadServer, XML_StagingServerPort, STRUTILS::fstr(L"%d", pSvrInfo->nServerPort));
			}
		}

		if (0 != pRoot->SaveToFile2(strJobScriptFile))
		{
			m_log.LogW(LL_ERR, 0, L"Failed to save job script for update %d", dwProdFor);
			SAFE_DELETE(pRoot);
			lRet = inter_error_failed_trigger_job;
			break;
		}
		SAFE_DELETE(pRoot);

		//
		// start job
		//
		wstring strExe = PATHUTILS::path_join(PATHUTILS::home_dir(), L"UpdateJob.exe");
		WCHAR szCommand[2048] = { 0 };
		swprintf_s(szCommand, L"\"%s\" \"%s\"", strExe.c_str(), strJobScriptFile.c_str());

		BOOL bCreateProcess = FALSE;
		STARTUPINFO si = { 0 };
		PROCESS_INFORMATION pi = { 0 };
		si.cb = sizeof(si);
		si.dwFlags = STARTF_USESHOWWINDOW;
		si.wShowWindow = SW_HIDE;

		m_log.LogW(LL_DET, 0, L"Start a update job with command: %s", szCommand);
		bCreateProcess = CreateProcess(NULL,   // No module name (use command line)
			szCommand,      // Command line
			NULL,           // Process handle not inheritable
			NULL,           // Thread handle not inheritable
			FALSE,          // Set handle inheritance to FALSE
			0,              // No creation flags
			NULL,           // Use parent's environment block
			NULL,		    // Use parent's starting directory 
			&si,            // Pointer to STARTUPINFO structure
			&pi);           // Pointer to PROCESS_INFORMATION structure

		if (!bCreateProcess)
		{
			m_log.LogW(LL_ERR, GetLastError(), L"Failed to start process with command: %s", szCommand);
			lRet = inter_error_failed_trigger_job;
			break;
		}
		CloseHandle(pi.hThread);

		CDaemonThread* pDaemonThread = new CDaemonThread( dwProdOn, dwProdFor, pSvrInfo, pJobMonitor, pi.hProcess);
		pDaemonThread->Start();

	} while (0);
	::LeaveCriticalSection(&m_cs);

	if (lRet != 0)
		SAFE_RELEASE(pJobMonitor);

	return lRet;
}

//added by cliicy.luo to single binaries updates
LONG CUpdateJobManager::StartUpdateBIJob(DWORD dwProdOn, DWORD dwProdFor, PARCUPDATE_SERVER_INFO pSvrInfo)
{
	m_log.LogW(LL_DET, 0, L"%s Start a new update job for %d. SvrInfo=%0xp", __WFUNCTION__,dwProdFor, pSvrInfo);
	LONG lRet = 0;
	IUpdateJobMonitor* pJobMonitor = NULL;
	EnterCriticalSection(&m_cs);
	do
	{
		if (m_stopFlag){
			m_log.LogW(LL_ERR, 0, L"UDP update service is stopping...");
			lRet = inter_error_service_stopped;
			break;
		}

		OpenIUpdateJobMonitor(dwProdFor, &pJobMonitor);
		if (pJobMonitor){
			m_log.LogW(LL_ERR, 0, L"Another instance is running for checking update %d", dwProdFor);
			lRet = inter_error_update_busy;
			break;
		}

		CreateIUpdateJobMonitor(dwProdFor, &pJobMonitor);
		if (!pJobMonitor){
			m_log.LogW(LL_ERR, 0, L"Failed to create job monitor for update %d", dwProdFor);
			lRet = inter_error_failed_trigger_job;
			break;
		}

		pJobMonitor->StartUpdateJob();
		//
		// create job script
		//
		wstring strJobScriptFile = STRUTILS::fstr(L"update_BI_%d_%d.xml", dwProdFor, GetTickCount());
		strJobScriptFile = PATHUTILS::path_join(PATHUTILS::home_dir(), strJobScriptFile);
		wstring strDownloadDir = STRUTILS::fstr(L"%s%s", UPUTILS::GetDownloadHomeDirectory(dwProdFor).c_str(), BINARY_PATCH_DIR);

		CXXmlNode* pRoot = CXXmlNode::CreateXmlNode(XML_UpdateJobScript);
		AddChildNode(pRoot, XML_Product, STRUTILS::fstr(L"%d", dwProdFor));
		AddChildNode(pRoot, XML_TriggerProduct, STRUTILS::fstr(L"%d", dwProdOn));

		if (dwProdFor == ARCUPDATE_PRODUCT_AGENT)
			AddChildNode(pRoot, XML_MailAlert, L"true");
		else
			AddChildNode(pRoot, XML_MailAlert, L"false");

		if (dwProdFor == dwProdOn)
			AddChildNode(pRoot, XML_IgnoreVersionCheck, L"false");
		else
			AddChildNode(pRoot, XML_IgnoreVersionCheck, L"true");

		wstring strUsername = L"", strPassword = L"";
		UPUTILS::GetAdminUserOfProduct(dwProdOn, strUsername, strPassword);
		wstring strEncPassword = L"";
		ENCUTILS::EncryptToString(strPassword.c_str(), strEncPassword);
		AddChildNode(pRoot, XML_AdminUser, strUsername);
		AddChildNode(pRoot, XML_AdminPassword, strEncPassword);
		AddChildNode(pRoot, XML_DownloadDir, strDownloadDir);
		if (pSvrInfo)
		{
			CXXmlNode* pDownloadServer = AddChildNode(pRoot, XML_DownloadServer, L"");
			pDownloadServer->SetAttributeInt(XML_type, (int)pSvrInfo->serverType);
			if (pSvrInfo->serverType == ARCUPDATE_SERVER_DEFAULT)
			{
				CXXmlNode* pProxy = AddChildNode(pDownloadServer, XML_Proxy, L"");
				pProxy->SetAttribute(XML_useProxy, pSvrInfo->bDefaultIEProxy ? L"false" : L"true");
				if (!pSvrInfo->bDefaultIEProxy)
				{
					AddChildNode(pProxy, XML_ProxyServerName, pSvrInfo->proxyServerName);
					AddChildNode(pProxy, XML_ProxyUserName, pSvrInfo->proxyUserName);
					AddChildNode(pProxy, XML_ProxyPassword, pSvrInfo->proxyPassword);
					AddChildNode(pProxy, XML_ProxyServerPort, STRUTILS::fstr(L"%d", pSvrInfo->proxyServerPort));
				}
			}
			else
			{
				AddChildNode(pDownloadServer, XML_StagingServer, pSvrInfo->downloadServer);
				AddChildNode(pDownloadServer, XML_StagingServerPort, STRUTILS::fstr(L"%d", pSvrInfo->nServerPort));
			}
		}

		if (0 != pRoot->SaveToFile2(strJobScriptFile))
		{
			m_log.LogW(LL_ERR, 0, L"Failed to save job script for update %d", dwProdFor);
			SAFE_DELETE(pRoot);
			lRet = inter_error_failed_trigger_job;
			break;
		}
		SAFE_DELETE(pRoot);

		//
		// start job
		//
		wstring strExe = PATHUTILS::path_join(PATHUTILS::home_dir(), L"UpdateBinaryJob.exe");
		WCHAR szCommand[2048] = { 0 };
		swprintf_s(szCommand, L"\"%s\" \"%s\"", strExe.c_str(), strJobScriptFile.c_str());

		BOOL bCreateProcess = FALSE;
		STARTUPINFO si = { 0 };
		PROCESS_INFORMATION pi = { 0 };
		si.cb = sizeof(si);
		si.dwFlags = STARTF_USESHOWWINDOW;
		si.wShowWindow = SW_HIDE;

		m_log.LogW(LL_DET, 0, L"Start a update job with command: %s", szCommand);
		bCreateProcess = CreateProcess(NULL,   // No module name (use command line)
			szCommand,      // Command line
			NULL,           // Process handle not inheritable
			NULL,           // Thread handle not inheritable
			FALSE,          // Set handle inheritance to FALSE
			0,              // No creation flags
			NULL,           // Use parent's environment block
			NULL,		    // Use parent's starting directory 
			&si,            // Pointer to STARTUPINFO structure
			&pi);           // Pointer to PROCESS_INFORMATION structure

		if (!bCreateProcess)
		{
			m_log.LogW(LL_ERR, GetLastError(), L"Failed to start process with command: %s", szCommand);
			lRet = inter_error_failed_trigger_job;
			break;
		}
		CloseHandle(pi.hThread);

		CDaemonThread* pDaemonThread = new CDaemonThread(dwProdOn, dwProdFor, pSvrInfo, pJobMonitor, pi.hProcess);
		pDaemonThread->Start();

	} while (0);
	::LeaveCriticalSection(&m_cs);

	if (lRet != 0)
		SAFE_RELEASE(pJobMonitor);

	m_log.LogW(LL_INF, 0, L"oooo %s finish to call: %s", __WFUNCTION__, L"UpdateBinaryJob.exe");
	return lRet;
}
//added by cliicy.luo to single binaries updates


CXXmlNode*	CUpdateJobManager::AddChildNode(CXXmlNode* pParentNode, const wstring& strTag, const wstring& strText)
{
	CXXmlNode* pNode = CXXmlNode::CreateXmlNode(strTag);
	if (!strText.empty())
		pNode->SetText(strText);
	pParentNode->InsertNode(pNode, XXN_LAST);
	return pNode;
}

void CUpdateJobManager::StopAllUpdateJobs()
{
	m_stopFlag = TRUE;

	m_log.LogW(LL_INF, 0, L"Stop all update jobs" );
	IUpdateJobMonitor* pJobMonitor = NULL;
	OpenIUpdateJobMonitor(ARCUPDATE_PRODUCT_AGENT, &pJobMonitor);
	if (pJobMonitor){
		pJobMonitor->CancelUpdateJob();
		SAFE_RELEASE(pJobMonitor);
	}

	OpenIUpdateJobMonitor(ARCUPDATE_PRODUCT_FULL, &pJobMonitor);
	if (pJobMonitor){
		pJobMonitor->CancelUpdateJob();
		SAFE_RELEASE(pJobMonitor);
	}

	OpenIUpdateJobMonitor(ARCUPDATE_PRODUCT_GATEWAY, &pJobMonitor);
	if (pJobMonitor){
		pJobMonitor->CancelUpdateJob();
		SAFE_RELEASE(pJobMonitor);
	}

	OpenIUpdateJobMonitor(ARCUPDATE_PRODUCT_SELFUPDATE, &pJobMonitor);
	if (pJobMonitor){
		pJobMonitor->CancelUpdateJob();
		SAFE_RELEASE(pJobMonitor);
	}
}

void CUpdateJobManager::CacheLastJobStatus(DWORD dwProd, const UPDATE_JOB_MONITOR& lastJm)
{
	::EnterCriticalSection(&m_cs);
	BOOL bFound = FALSE;
	std::map<DWORD, PUPDATE_JOB_MONITOR>::iterator it;
	for (it = m_mapLastJobStatus.begin(); it != m_mapLastJobStatus.end(); it++)
	{
		if (it->first == dwProd)
		{
			memcpy_s(it->second, sizeof(UPDATE_JOB_MONITOR), &lastJm, sizeof(UPDATE_JOB_MONITOR));
			bFound = TRUE;
			break;
		}
	}
	if (!bFound)
	{
		PUPDATE_JOB_MONITOR pJm = new UPDATE_JOB_MONITOR();
		memcpy_s(pJm, sizeof(UPDATE_JOB_MONITOR), &lastJm, sizeof(UPDATE_JOB_MONITOR));
		m_mapLastJobStatus.insert(std::make_pair(dwProd, pJm));
	}
	::LeaveCriticalSection(&m_cs);
}