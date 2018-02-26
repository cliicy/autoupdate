// UpdateJob.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "..\UpdateJob\UpdateJobScript.h"
#include "..\include\UpdateDefines.h"
#include "..\include\UpdateError.h"
#include "UDPBIUpdateJob.h"
#include "..\UpdateJob\ExceptionDump.h"

PUPDATE_JOB_SCRIPT    g_pJobBIScript;

DWORD	LoadJobScript(const wstring& strFile);

BOOL	DeleteJobScript();

VOID MySetExceptionHandler(BOOL bFullMemDump)
{
	__try
	{
		ExceptionDump::SetExceptionHandler(bFullMemDump);
	}
	__except (EXCEPTION_EXECUTE_HANDLER)
	{
		_ftprintf_s(stdout, TEXT("SetExceptionHandler EXCEPTION"));
	}
}


int _tmain(int argc, _TCHAR* argv[])
{
	MySetExceptionHandler(FALSE);

	if (argc < 2){
		printf("Invalid parameters.");
		return 0;
	}
	
	//added by cliicy.luo to debug
	
	//CAvailableBIUpdateDll* pUpdateInfo = new CAvailableBIUpdateDll();
	//wstring strLocalDllFile = L"C:\\Program Files\\Arcserve\\Unified Data Protection\\Update Manager\\EngineUpdates\\AvaiablePatchBinary.dll";
	//wstring strLocalDllFile = L"E:\\UDP_3792\\ARCserve\\D2D\\Native\\PatchManager\\AutoUpdate\\AvaiablePatchFilesDll\\x86.rel\\AvaiablePatchBinary.dll";
	//DWORD dwxRet = 0;
	//pUpdateInfo->LoadFromDllFile(strLocalDllFile);
	//HMODULE hDLL = NULL; 
	//hDLL = ::LoadLibrary(strLocalDllFile.c_str());
	//if (hDLL == NULL)
	//{
	//	dwxRet = GetLastError();
	//	CDbgLog logj;
	//	logj.LogW(LL_INF, 0, L"*** %s LoadLibrary failed=%d ***", __WFUNCTION__, dwxRet);
	//}

	//added by cliicy.luo to debug

	wstring strUDPHome = PRODUTILS::GetUDPHome();
	PATHUTILS::path_ensure_end_without_slash(strUDPHome);
	SetEnvironmentVariable(L"UDPHOME", strUDPHome.c_str());

	if ( 0 != LoadJobScript(argv[1]) ){
		return 0;
	}
	if (DeleteJobScript()){
		::DeleteFile(argv[1]);
	}

	
	//
	// set debug log file path
	//
	wstring strDebgLogFileName = L"";
	strDebgLogFileName = STRUTILS::fstr(L"Update_BI_%d.log", g_pJobBIScript->dwProdCode);
	wstring strFullPathOfDebugLog = PATHUTILS::path_join(PATHUTILS::home_dir(), L"\\Logs");
	strFullPathOfDebugLog = PATHUTILS::path_join(strFullPathOfDebugLog, strDebgLogFileName);
	CDbgLog::SetGlobalLogFileName(strFullPathOfDebugLog.c_str());
	
	//
	// output job script infor
	//	
	CDbgLog logObj;
	logObj.LogW(LL_INF, 0, L"*** %s Update job script ***",__WFUNCTION__);
	logObj.LogW(LL_INF, 0, L"    Product Code: %d", g_pJobBIScript->dwProdCode);
	logObj.LogW(LL_INF, 0, L"    Trigger Product Code: %d", g_pJobBIScript->dwTriggerProduct );
	logObj.LogW(LL_INF, 0, L"    Mail Alert: %s", g_pJobBIScript->bMailAlert ? L"Yes" : L"No");
	logObj.LogW(LL_INF, 0, L"    Ignore Version Check: %s", g_pJobBIScript->bIgnoreVersion ? L"Yes" : L"No");
	logObj.LogW(LL_INF, 0, L"    Preference Settings File: %s", g_pJobBIScript->preferenceFile.c_str());
	logObj.LogW(LL_INF, 0, L"    Directory to Save Update: %s", g_pJobBIScript->downloadDir.c_str());
	if (!g_pJobBIScript->pUpdateSvrInfo)
	{
		logObj.LogW(LL_INF, 0, L"    Download Server: NULL");
	}
	else
	{
		logObj.LogW(LL_INF, 0, L"    Download Server Type: %d", g_pJobBIScript->pUpdateSvrInfo->serverType);
		logObj.LogW(LL_INF, 0, L"    Download Server: %s:%d", g_pJobBIScript->pUpdateSvrInfo->downloadServer, g_pJobBIScript->pUpdateSvrInfo->nServerPort);
		logObj.LogW(LL_INF, 0, L"    Default Proxy: %s", g_pJobBIScript->pUpdateSvrInfo->bDefaultIEProxy ? L"Yes" : L"No");
		logObj.LogW(LL_INF, 0, L"    Proxy Server Name: %s", g_pJobBIScript->pUpdateSvrInfo->proxyServerName);
		logObj.LogW(LL_INF, 0, L"    Proxy Server Port: %d", g_pJobBIScript->pUpdateSvrInfo->proxyServerPort);
		logObj.LogW(LL_INF, 0, L"    Proxy User Name: %s", g_pJobBIScript->pUpdateSvrInfo->proxyUserName);
	}
	logObj.LogW(LL_INF, 0, L"*** Update job script %s***",__WFUNCTION__);

	//
	// open job monitor
	//
	DWORD dwRet = OpenIUpdateJobMonitor(g_pJobBIScript->dwProdCode, &(g_pJobBIScript->pJobMonitor));
	if (dwRet != 0)
	{
		logObj.LogW(LL_ERR, dwRet, L"Failed to open job monitor of [%d]", g_pJobBIScript->dwProdCode);
		goto _EXIT;
	}
	g_pJobBIScript->pJobMonitor->UpdateProcessID(::GetCurrentProcessId());

	if (g_pJobBIScript->dwProdCode != ARCUPDATE_PRODUCT_AGENT &&
		g_pJobBIScript->dwProdCode != ARCUPDATE_PRODUCT_FULL &&
		g_pJobBIScript->dwProdCode != ARCUPDATE_PRODUCT_GATEWAY &&
		g_pJobBIScript->dwProdCode != ARCUPDATE_PRODUCT_SELFUPDATE &&
		g_pJobBIScript->dwProdCode != ARCUPDATE_PRODUCT_PATCHUPDATE)
	{
		logObj.LogW(LL_ERR, 0, L"Invalid Product Code: %d", g_pJobBIScript->dwProdCode);
		g_pJobBIScript->pJobMonitor->EndUpdateJob(AJS_FAILED, inter_error_unsupported_update);
		goto _EXIT;
	}

	if (g_pJobBIScript->preferenceFile.empty())
	{
		logObj.LogW(LL_ERR, 0, L"Invalid Preference Settings File");
		g_pJobBIScript->pJobMonitor->EndUpdateJob(AJS_FAILED, inter_error_invalid_config_file);
		goto _EXIT;
	}

	if (!PATHUTILS::is_file_exist(g_pJobBIScript->preferenceFile))
	{
		logObj.LogW(LL_ERR, 0, L"The specified Preference Settings File does not exists. [%s]", g_pJobBIScript->preferenceFile.c_str());
		g_pJobBIScript->pJobMonitor->EndUpdateJob(AJS_FAILED, inter_error_invalid_config_file);
		goto _EXIT;
	}

	if (g_pJobBIScript->downloadDir.empty())
	{
		logObj.LogW(LL_ERR, 0, L"The the download directory was not specified.");
		g_pJobBIScript->pJobMonitor->EndUpdateJob(AJS_FAILED, inter_error_invalid_job_script);
		goto _EXIT;
	}

	logObj.LogW(LL_DET, 0, L"======   Start a update job    ======");
	if (g_pJobBIScript->dwProdCode == ARCUPDATE_PRODUCT_AGENT || g_pJobBIScript->dwProdCode == ARCUPDATE_PRODUCT_FULL ||
		g_pJobBIScript->dwProdCode == ARCUPDATE_PRODUCT_GATEWAY)
	{
		CUDPBIUpdateJob job;
		job.Run();
	}
	else if (g_pJobBIScript->dwProdCode == ARCUPDATE_PRODUCT_SELFUPDATE)
	{
		logObj.LogW(LL_DET, 0, L"======   Start a self update job    ======");
		//CSelfUpdateJob job;
		//job.Run();
	}
	else if (g_pJobBIScript->dwProdCode == ARCUPDATE_PRODUCT_PATCHUPDATE)
	{
		logObj.LogW(LL_DET, 0, L"======   Start a patch update update job    ======");
		//CPatchUpdateJob job;
		//job.Run();
	}
	
_EXIT:
	SAFE_DELETE(g_pJobBIScript->pUpdateSvrInfo);
	SAFE_RELEASE(g_pJobBIScript->pJobMonitor);
	SAFE_RELEASE(g_pJobBIScript->pDownloader);
	SAFE_DELETE(g_pJobBIScript);
	return 0;
}

DWORD LoadJobScript(const wstring& strFile)
{
	CDbgLog logObj;
	logObj.LogW(LL_INF, 0, L"*** oooo %s strFile=%s ***",__WFUNCTION__,strFile.c_str());

	if (!PATHUTILS::is_file_exist(strFile))
		return ERROR_FILE_NOT_FOUND;

	CXXmlNode* pRoot = CXXmlNode::LoadFromFile(strFile);
	if (!pRoot)		
		return ERROR_INVALID_PARAMETER;
	
	g_pJobBIScript = new UPDATE_JOB_SCRIPT();
	do
	{
		CXXmlNode* pNodeProduct = pRoot->GetChildNode(XML_Product);
		if (!pNodeProduct){
			break;
		}
		// get product code
		g_pJobBIScript->dwProdCode = _wtoi(pNodeProduct->GetText().c_str());

		// get the trigger product code
		CXXmlNode* pNodeTriggerProduct = pRoot->GetChildNode(XML_TriggerProduct);
		if (!pNodeTriggerProduct){
			break;
		}
		g_pJobBIScript->dwTriggerProduct = _wtoi(pNodeTriggerProduct->GetText().c_str());
		g_pJobBIScript->preferenceFile = UPUTILS::GetUpdateSettingXmlFile(g_pJobBIScript->dwTriggerProduct);

		// get option of mail alter
		CXXmlNode* pNodeMailAlert = pRoot->GetChildNode(XML_MailAlert);
		if (pNodeMailAlert)
			g_pJobBIScript->bMailAlert = STRUTILS::str2boolean(pNodeMailAlert->GetText(), false);
		else
			g_pJobBIScript->bMailAlert = FALSE;

		// get option of version check
		CXXmlNode* pNodeVersionCheck = pRoot->GetChildNode(XML_IgnoreVersionCheck);
		if (pNodeVersionCheck)
			g_pJobBIScript->bIgnoreVersion = STRUTILS::str2boolean(pNodeVersionCheck->GetText(), false);
		else
			g_pJobBIScript->bIgnoreVersion = TRUE;

		// get admin username 
		CXXmlNode* pNodeAdminUser = pRoot->GetChildNode(XML_AdminUser);
		if (pNodeAdminUser)
			g_pJobBIScript->adminUsername = pNodeAdminUser->GetText();

		// get admin password
		CXXmlNode* pNodeAdminPwd = pRoot->GetChildNode(XML_AdminPassword);
		if (pNodeAdminPwd)
		{
			wstring strEncPassword = pNodeAdminPwd->GetText();
			wstring strPlainPassword = L"";
			ENCUTILS::DecryptFromString(strEncPassword.c_str(), strPlainPassword);
			g_pJobBIScript->adminPassword = strPlainPassword;
		}

		// get the download dir
		CXXmlNode* pNodeDownloadDir = pRoot->GetChildNode(XML_DownloadDir);
		if (pNodeDownloadDir)
			g_pJobBIScript->downloadDir = pNodeDownloadDir->GetText();
		

		// get the download server info
		CXXmlNode* pNodeDownloadServer = pRoot->GetChildNode(XML_DownloadServer);
		if (pNodeDownloadServer)
		{
			g_pJobBIScript->pUpdateSvrInfo = new ARCUPDATE_SERVER_INFO();

			// server type - staging server or arcserve server
			g_pJobBIScript->pUpdateSvrInfo->serverType = (ARCUPDATE_SERVER_TYPE)pNodeDownloadServer->GetAttributeDWORD(XML_type, ARCUPDATE_SERVER_DEFAULT);
			if (g_pJobBIScript->pUpdateSvrInfo->serverType == ARCUPDATE_SERVER_DEFAULT) 
			{
				// for the default server, we need to get server infor from UpdateURL.xml
				CA_UPDATE_SERVER_INFO defSvrInfo;
				UPUTILS::GetDefaultUpdateServerInfo(g_pJobBIScript->dwProdCode, defSvrInfo);
				wcsncpy_s(g_pJobBIScript->pUpdateSvrInfo->downloadServer, _ARRAYSIZE(g_pJobBIScript->pUpdateSvrInfo->downloadServer), defSvrInfo.strServerName.c_str(), _TRUNCATE);
				g_pJobBIScript->pUpdateSvrInfo->nServerPort = defSvrInfo.nPort;

				// get proxy infor
				CXXmlNode* pNodeProxy = pNodeDownloadServer->GetChildNode(XML_Proxy);
				if (pNodeProxy)
				{
					g_pJobBIScript->pUpdateSvrInfo->bDefaultIEProxy = !STRUTILS::str2boolean( pNodeProxy->GetAttribute(XML_useProxy), false );
					if (!g_pJobBIScript->pUpdateSvrInfo->bDefaultIEProxy)
					{
						CXXmlNode* pNode = pNodeProxy->GetChildNode(XML_ProxyServerName);
						if (pNode)
							wcsncpy_s(g_pJobBIScript->pUpdateSvrInfo->proxyServerName, _ARRAYSIZE(g_pJobBIScript->pUpdateSvrInfo->proxyServerName), pNode->GetText().c_str(), _TRUNCATE);

						pNode = pNodeProxy->GetChildNode(XML_ProxyUserName);
						if (pNode)
							wcsncpy_s(g_pJobBIScript->pUpdateSvrInfo->proxyUserName, _ARRAYSIZE(g_pJobBIScript->pUpdateSvrInfo->proxyUserName), pNode->GetText().c_str(), _TRUNCATE);

						pNode = pNodeProxy->GetChildNode(XML_ProxyPassword);
						if (pNode)
							wcsncpy_s(g_pJobBIScript->pUpdateSvrInfo->proxyPassword, _ARRAYSIZE(g_pJobBIScript->pUpdateSvrInfo->proxyPassword), pNode->GetText().c_str(), _TRUNCATE);

						pNode = pNodeProxy->GetChildNode(XML_ProxyServerPort);
						if (pNode)
							g_pJobBIScript->pUpdateSvrInfo->proxyServerPort = _wtoi(pNode->GetText().c_str());
						else
							g_pJobBIScript->pUpdateSvrInfo->proxyServerPort = 80;

					}
				}
			}
			else
			{
				// for staging server, we need to get staging server info
				CXXmlNode* pNode = pNodeDownloadServer->GetChildNode(XML_StagingServer);
				if (pNode)
					wcsncpy_s(g_pJobBIScript->pUpdateSvrInfo->downloadServer, _ARRAYSIZE(g_pJobBIScript->pUpdateSvrInfo->downloadServer), pNode->GetText().c_str(), _TRUNCATE);

				pNode = pNodeDownloadServer->GetChildNode(XML_StagingServerPort);
				if (pNode)
					g_pJobBIScript->pUpdateSvrInfo->nServerPort = _wtoi( pNode->GetText().c_str() );
			}
		}
		else
		{
			g_pJobBIScript->pUpdateSvrInfo = NULL;
		}
	} while (0);

	SAFE_DELETE(pRoot);
	return 0;	
}

BOOL DeleteJobScript()
{
	wstring strCfgFile = UPUTILS::GetUpdateCfgFile();
	int n = ::GetPrivateProfileInt(L"Settings", L"DeleteJobScript", 1, strCfgFile.c_str());
	if (n == 0)
		return FALSE;
	return TRUE;
}