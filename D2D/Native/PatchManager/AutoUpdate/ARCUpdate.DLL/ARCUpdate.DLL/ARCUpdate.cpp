// UDPUpdate.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"
#include "ArcUpdate.h"
#include "PipeClient.h"
#include "..\include\UpLib.h"
#include "..\include\UpdateError.h"
#include "..\UpdateRes\ARCUpdateRes.h"
#include "XXmlNode.h"
#include "Log.h"
#include "..\UpdateRes\ARCUpdateRes.h"



static DWORD handleErrors(LONG lRet)
{
	// convert error before return to the caller
	switch (lRet)
	{
	case 0:
	case inter_error_already_download:
		return ARCUPDATE_SUCCEED;
	case inter_error_no_update_found:
		return ARCUPDATE_ERROR_NO_UPDATE_FOUND;
	case inter_error_update_to_date:
		return ARCUPDATE_ERROR_UP_TO_DATE;
	case inter_error_update_canceled:
		return ARCUPDATE_ERROR_CANCELED;
	case inter_error_update_busy:
		return ARCUPDATE_ERROR_ANOTHER_IS_RUNNING;
	case inter_error_server_unavailable:
		return ARCUPDATE_ERROR_SERVER_UNAVAILABLE;
	case inter_error_service_stopped:
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	case inter_error_selfupdate_required:
		return ARCUPDATE_ERROR_SELF_UPDATE_REQUIRED;
	case inter_error_failed_download:
	case inter_error_invalid_config_file:
	case inter_error_invalid_signature:
	case inter_error_file_damaged:
	case inter_error_folder_not_found:
	case inter_error_failed_trigger_job:
	case inter_error_update_crash:
	case error_pipe_unsupported_cmd:
	case error_pipe_invalid_parameter:
		return ARCUPDATE_ERROR_FAILED_TO_DOWNLOAD;
	default:
		return ARCUPDATE_ERROR_FAILED_TO_DOWNLOAD;
	}
}

static BOOL isUpdateServiceStopped()
{
	CDbgLog log;
	SC_HANDLE hScm = ::OpenSCManager(NULL, NULL, GENERIC_READ);
	if (hScm == NULL)
	{
		log.LogW(LL_ERR, GetLastError(), L"%s: Failed to open service manager.", __WFUNCTION__ );
		return TRUE;
	}

	SC_HANDLE hService = NULL;
	BOOL bStopped = TRUE;
	do
	{
		SC_HANDLE hService = OpenServiceW(hScm, L"CAARCUpdateSvc", SERVICE_QUERY_STATUS);
		if (hService == NULL)
		{
			if (ERROR_SERVICE_DOES_NOT_EXIST == GetLastError())
			{
				log.LogW(LL_INF, 0, L"%s: Auto update service does not exists", __WFUNCTION__);
				break;
			}
			else
			{
				log.LogW(LL_ERR, GetLastError(), L"%s: Failed to open service CAARCUpdateSvc.", __WFUNCTION__);
				break;
			}
		}

		SERVICE_STATUS_PROCESS ssStatus;
		DWORD dwBytesNeeded = 0;
		if (!QueryServiceStatusEx(hService, SC_STATUS_PROCESS_INFO, (LPBYTE)&ssStatus, sizeof(SERVICE_STATUS_PROCESS), &dwBytesNeeded))
		{
			log.LogW(LL_ERR, GetLastError(), L"%s: Failed to query auto update service.", __WFUNCTION__);
			break;
		}

		if (ssStatus.dwCurrentState != SERVICE_RUNNING)
		{
			log.LogW(LL_INF, 0, L"%s: Auto update service is not running.", __WFUNCTION__);
			break;
		}
		bStopped = FALSE;
	} while (0);

	return bStopped;
}

BOOL WINAPI IsUpdateServiceRunning()
{
	CDbgLog log;
	if (isUpdateServiceStopped()){
		log.LogW(LL_ERR, 0, L"%s: UDP Auto update service is disabled or not started. Please start this service and try again.", __WFUNCTION__);
		return FALSE;
	}
	return TRUE;
}

//
// check update from web service or setup
//
DWORD WINAPI CheckUpdate(DWORD dwProdOn, DWORD dwProdFor, ARCUPDATE_SERVER_INFO* pSvrInfo, BOOL bSync/*=TRUE*/)
{
	CDbgLog log;
	log.LogW(LL_INF, 0, L"ARCUpdateDLL CheckUpdate ####begin.");

	if (isUpdateServiceStopped()){
		log.LogW(LL_ERR, 0, L"%s: UDP Auto update service is disabled or not started. Please start this service and try again.", __WFUNCTION__);
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	}


	LONG lRet = 0;
	CPipeClient pClient;
	lRet = pClient.Connect();
	if (lRet != 0)
	{
		log.LogW(LL_ERR, lRet, L"%s: Failed to connect to update service. Please check if UDP auto update service is running.", __WFUNCTION__);
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	}
	
	if (dwProdFor != 0)
	{
		log.LogW(LL_INF, 0, L"ARCUpdateDLL will triggerUpdate ####.");
		//
		// check update for specified product
		//
		lRet = pClient.triggerUpdate(dwProdOn, dwProdFor, pSvrInfo,false);
		if (lRet != 0)
			return handleErrors(lRet);

		if (!bSync)
			return 0;

		// wait for end of agent checking for updates
		UPDATE_JOB_MONITOR jobMonitor;
		while (1){
			ZeroMemory(&jobMonitor, sizeof(jobMonitor));
			lRet = pClient.queryUpdateStatus(dwProdFor, &jobMonitor);
			if (lRet != 0)
				break;
			if (jobMonitor.dwJobPhase == AJP_END){
				lRet = jobMonitor.lLastError;
				break;
			}
			Sleep(2000);
		}
		return handleErrors(lRet);
	}
	else
	{
		if (dwProdOn == ARCUPDATE_PRODUCT_FULL)
		{
			//
			// When trigger an update on console, check updates for all products
			//
			pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_AGENT, pSvrInfo,false);
			//pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_SELFUPDATE, pSvrInfo);
			//pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_PATCHUPDATE, pSvrInfo);
			lRet = pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_FULL, pSvrInfo,false);
			if (lRet != 0)
				return handleErrors(lRet);

			if (!bSync)
				return 0;

			// wait for end of agent checking for updates
			UPDATE_JOB_MONITOR jobMonitor;
			while (1){
				ZeroMemory(&jobMonitor, sizeof(jobMonitor));
				lRet = pClient.queryUpdateStatus(ARCUPDATE_PRODUCT_FULL, &jobMonitor);
				if (lRet != 0)
					break;
				if (jobMonitor.dwJobPhase == AJP_END){
					lRet = jobMonitor.lLastError;
					break;
				}
				Sleep(2000);
			}
			return handleErrors(lRet);
		}
		/*
		if (dwProdOn == ARCUPDATE_PRODUCT_GATEWAY)
		{
			//
			// When trigger an update on console, check updates for all products
			//
			pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_AGENT, pSvrInfo);
			lRet = pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_GATEWAY, pSvrInfo);
			if (lRet != 0)
				return handleErrors(lRet);

			if (!bSync)
				return 0;

			// wait for end of agent checking for updates
			UPDATE_JOB_MONITOR jobMonitor;
			while (1){
				ZeroMemory(&jobMonitor, sizeof(jobMonitor));
				lRet = pClient.queryUpdateStatus(ARCUPDATE_PRODUCT_GATEWAY, &jobMonitor);
				if (lRet != 0)
					break;
				if (jobMonitor.dwJobPhase == AJP_END){
					lRet = jobMonitor.lLastError;
					break;
				}
				Sleep(2000);
			}
			return handleErrors(lRet);
		}
		*/
		if (dwProdOn == ARCUPDATE_PRODUCT_AGENT)
		{
			//
			// When trigger an update on agent, check updates for all products
			//
			lRet = pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_AGENT, pSvrInfo,false);
			if (lRet != 0)
				return handleErrors(lRet);

			if (!bSync)
				return 0;

			// wait for end of agent checking for updates
			UPDATE_JOB_MONITOR jobMonitor;
			while (1){
				ZeroMemory(&jobMonitor, sizeof(jobMonitor));
				lRet = pClient.queryUpdateStatus(ARCUPDATE_PRODUCT_AGENT, &jobMonitor);
				if (lRet != 0)
					break;
				if (jobMonitor.dwJobPhase == AJP_END){
					lRet = jobMonitor.lLastError;
					break;
				}
				Sleep(2000);
			}
			return handleErrors(lRet);
		}
		else
		{
			return ERROR_INVALID_PARAMETER;
		}
	}
}

DWORD WINAPI SaveUpdateServerInfo(DWORD dwProd, ARCUPDATE_SERVER_INFO* pSvrInfo)
{
	if (dwProd != ARCUPDATE_PRODUCT_AGENT && dwProd != ARCUPDATE_PRODUCT_FULL && dwProd != ARCUPDATE_PRODUCT_GATEWAY)
		return ERROR_INVALID_PARAMETER;
	if (!pSvrInfo)
		return ERROR_INVALID_PARAMETER;

	wstring strFile = UPUTILS::GetUpdateSettingXmlFile(dwProd, 0);
	UDP_UPDATE_SETTINGS updateSettings;
	UPUTILS::ReadUpdateSettingsFromFile(strFile, updateSettings);
	updateSettings.nServerType = pSvrInfo->serverType;
	if (pSvrInfo->serverType == ARCUPDATE_SERVER_STAGE)
	{
		UDP_STAGING_SVR stageServer;
		stageServer.nPort = pSvrInfo->nServerPort;
		stageServer.strServerName = wstring(pSvrInfo->downloadServer);
		stageServer.nIndex = 0;
		updateSettings.vecStagingServers.insert(updateSettings.vecStagingServers.begin(), stageServer);
		return UPUTILS::SaveUpdateSettingsToFile(strFile, updateSettings);
	}
	else
	{
		if (pSvrInfo->bDefaultIEProxy)
		{
			updateSettings.ieProxy.bDefaultIEProxy = TRUE;
		}
		else
		{
			updateSettings.ieProxy.bDefaultIEProxy = FALSE;
			updateSettings.ieProxy.nProxyPort = pSvrInfo->proxyServerPort;
			updateSettings.ieProxy.proxyPassword = pSvrInfo->proxyPassword;
			updateSettings.ieProxy.proxyServer = pSvrInfo->proxyServerName;
			updateSettings.ieProxy.proxyUserName = pSvrInfo->proxyUserName;

		}
		return UPUTILS::SaveUpdateSettingsToFile(strFile, updateSettings);
	}
}

//
// test connection from web service or setup
//
DWORD WINAPI TestConnection(DWORD dwProd, ARCUPDATE_SERVER_INFO* pSvrInfo)
{
	CDbgLog logObj;
	logObj.LogW(LL_INF, 0, L"%s: oooo dwProd =%d. proxyUserNmae=%s ", __WFUNCTION__, dwProd, pSvrInfo->proxyUserName);

	if (!pSvrInfo)
		return ERROR_INVALID_PARAMETER;
	if (dwProd != ARCUPDATE_PRODUCT_AGENT && dwProd != ARCUPDATE_PRODUCT_FULL && dwProd != ARCUPDATE_PRODUCT_GATEWAY )
		return ERROR_INVALID_PARAMETER;


	wstring strUser = L"", strPassword = L"";
	UPUTILS::GetAdminUserOfProduct(dwProd, strUser, strPassword);

	CImpersonate doImpersonate;
	doImpersonate.LogonOnWithUser(strUser, strPassword);

	if (pSvrInfo->serverType == ARCUPDATE_SERVER_DEFAULT)
	{
		CA_UPDATE_SERVER_INFO caUpServer;
		UPUTILS::GetDefaultUpdateServerInfo(dwProd, caUpServer);
		wcsncpy_s(pSvrInfo->downloadServer, _countof(pSvrInfo->downloadServer), caUpServer.strServerName.c_str(), _TRUNCATE);
	}

	wstring strBaseURL = UPUTILS::GetURLOfFileOnServer(dwProd, pSvrInfo, AVAILABLE_UPDATE_INFO_DLL);

	logObj.LogW(LL_INF, 0, L"%s: oooo ARCUpdate.cpp Cliicy.Luo Download file from URL=%s", __WFUNCTION__, strBaseURL.c_str());
	IDownloader* pDownloader = NULL;
	CreateHttpDownloader(pSvrInfo, NULL, &pDownloader);
	LONG lLastError = pDownloader->TestHttpConnection(strBaseURL);
	SAFE_RELEASE(pDownloader);
	if (lLastError == HTTP_STATUS_OK || lLastError == HTTP_STATUS_NOT_FOUND )
		lLastError = 0;
	return lLastError;
}

//added by cliicy.luo
//
// check update from web service or setup
//
DWORD WINAPI CheckBIUpdate(DWORD dwProdOn, DWORD dwProdFor, ARCUPDATE_SERVER_INFO* pSvrInfo, BOOL bSync/*=TRUE*/)
{
	CDbgLog log;
	log.LogW(LL_INF, 0, L"ARCUpdateDLL CheckBIUpdate ####begin.");

	if (isUpdateServiceStopped()){
		log.LogW(LL_ERR, 0, L"%s: UDP Auto update service is disabled or not started. Please start this service and try again.", __WFUNCTION__);
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	}


	LONG lRet = 0;
	CPipeClient pClient;
	lRet = pClient.Connect();
	if (lRet != 0)
	{
		log.LogW(LL_ERR, lRet, L"%s: Failed to connect to update service. Please check if UDP auto update service is running.", __WFUNCTION__);
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	}

	if (dwProdFor != 0)
	{
		log.LogW(LL_INF, 0, L"ARCUpdateDLL will BI triggerUpdate ####.");
		//
		// check update for specified product
		//
		lRet = pClient.triggerUpdate(dwProdOn, dwProdFor, pSvrInfo,true);
		if (lRet != 0)
			return handleErrors(lRet);

		if (!bSync)
			return 0;

		// wait for end of agent checking for updates
		UPDATE_JOB_MONITOR jobMonitor;
		while (1){
			ZeroMemory(&jobMonitor, sizeof(jobMonitor));
			lRet = pClient.queryUpdateStatus(dwProdFor, &jobMonitor);
			if (lRet != 0)
				break;
			if (jobMonitor.dwJobPhase == AJP_END){
				lRet = jobMonitor.lLastError;
				break;
			}
			Sleep(2000);
		}
		return handleErrors(lRet);
	}
	else
	{
		if (dwProdOn == ARCUPDATE_PRODUCT_FULL)
		{
			//
			// When trigger an update on console, check updates for all products
			//
			log.LogW(LL_INF, 0, L"oooo ARCUpdateDLL will triggehotfixrUpdate ARCUPDATE_PRODUCT_AGENT and ARCUPDATE_PRODUCT_FULL ####.");
			//this line below will create the download folder for agent and download hotfix or pathes of agent added by cliicy.luo
			pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_AGENT, pSvrInfo, true); // marked by cliicy.luo for test to only download updates for console server not including the EngineUpdates folder
			//pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_SELFUPDATE, pSvrInfo);
			//pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_PATCHUPDATE, pSvrInfo);
			lRet = pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_FULL, pSvrInfo,true);
			if (lRet != 0)
				return handleErrors(lRet);

			if (!bSync)
				return 0;

			// wait for end of agent checking for updates
			UPDATE_JOB_MONITOR jobMonitor;
			while (1){
				ZeroMemory(&jobMonitor, sizeof(jobMonitor));
				lRet = pClient.queryUpdateStatus(ARCUPDATE_PRODUCT_FULL, &jobMonitor);
				if (lRet != 0)
					break;
				if (jobMonitor.dwJobPhase == AJP_END){
					lRet = jobMonitor.lLastError;
					break;
				}
				Sleep(2000);
			}
			return handleErrors(lRet);
		}
		/*
		if (dwProdOn == ARCUPDATE_PRODUCT_GATEWAY)
		{
		//
		// When trigger an update on console, check updates for all products
		//
		pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_AGENT, pSvrInfo);
		lRet = pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_GATEWAY, pSvrInfo);
		if (lRet != 0)
		return handleErrors(lRet);

		if (!bSync)
		return 0;

		// wait for end of agent checking for updates
		UPDATE_JOB_MONITOR jobMonitor;
		while (1){
		ZeroMemory(&jobMonitor, sizeof(jobMonitor));
		lRet = pClient.queryUpdateStatus(ARCUPDATE_PRODUCT_GATEWAY, &jobMonitor);
		if (lRet != 0)
		break;
		if (jobMonitor.dwJobPhase == AJP_END){
		lRet = jobMonitor.lLastError;
		break;
		}
		Sleep(2000);
		}
		return handleErrors(lRet);
		}
		*/
		if (dwProdOn == ARCUPDATE_PRODUCT_AGENT)
		{
			//
			// When trigger an update on agent, check updates for all products
			//
			lRet = pClient.triggerUpdate(dwProdOn, ARCUPDATE_PRODUCT_AGENT, pSvrInfo,true);
			if (lRet != 0)
				return handleErrors(lRet);

			if (!bSync)
				return 0;

			// wait for end of agent checking for updates
			UPDATE_JOB_MONITOR jobMonitor;
			while (1){
				ZeroMemory(&jobMonitor, sizeof(jobMonitor));
				lRet = pClient.queryUpdateStatus(ARCUPDATE_PRODUCT_AGENT, &jobMonitor);
				if (lRet != 0)
					break;
				if (jobMonitor.dwJobPhase == AJP_END){
					lRet = jobMonitor.lLastError;
					break;
				}
				Sleep(2000);
			}
			return handleErrors(lRet);
		}
		else
		{
			return ERROR_INVALID_PARAMETER;
		}
	}
}

DWORD WINAPI TestBIConnection(DWORD dwProd, ARCUPDATE_SERVER_INFO* pSvrInfo)
{
	CDbgLog logObj;
	logObj.LogW(LL_INF, 0, L"%s: oooo dwProd =%d. proxyUserNmae=%s ", __WFUNCTION__, dwProd, pSvrInfo->proxyUserName);

	if (!pSvrInfo)
		return ERROR_INVALID_PARAMETER;
	if (dwProd != ARCUPDATE_PRODUCT_AGENT && dwProd != ARCUPDATE_PRODUCT_FULL && dwProd != ARCUPDATE_PRODUCT_GATEWAY)
		return ERROR_INVALID_PARAMETER;


	wstring strUser = L"", strPassword = L"";
	UPUTILS::GetAdminUserOfProduct(dwProd, strUser, strPassword);

	CImpersonate doImpersonate;
	doImpersonate.LogonOnWithUser(strUser, strPassword);

	if (pSvrInfo->serverType == ARCUPDATE_SERVER_DEFAULT)
	{
		CA_UPDATE_SERVER_INFO caUpServer;
		UPUTILS::GetDefaultUpdateServerInfo(dwProd, caUpServer);
		wcsncpy_s(pSvrInfo->downloadServer, _countof(pSvrInfo->downloadServer), caUpServer.strServerName.c_str(), _TRUNCATE);
	}

		
	wstring strextendpath = STRUTILS::construct_url(L"%s%s", ARCUPDATE_SITE_RELEASE_VERSION, BINARY_PATCH_DIR);
	wstring strBaseURL = UPUTILS::GetURLOfFileOnServer(dwProd, pSvrInfo, AVAILABLE_PATCHBINARY_DLL, strextendpath.c_str());
	
	logObj.LogW(LL_INF, 0, L"%s: oooo ARCUpdate.cpp Cliicy.Luo Download file from URL=%s", __WFUNCTION__, strBaseURL.c_str());
	IDownloader* pDownloader = NULL;
	CreateHttpDownloader(pSvrInfo, NULL, &pDownloader);
	LONG lLastError = pDownloader->TestHttpConnection(strBaseURL);
	SAFE_RELEASE(pDownloader);
	if (lLastError == HTTP_STATUS_OK || lLastError == HTTP_STATUS_NOT_FOUND)
		lLastError = 0;
	return lLastError;
}

//
// install update from web service
//
DWORD WINAPI InstallBIUpdate(DWORD dwProduct)
{

	CDbgLog log;

	wstring strStatusFile = GetBIUpdateStatusXmlFile(dwProduct);
	log.LogW(LL_INF, 0, L"ooooo %s strStatusFile=%s", __WFUNCTION__, strStatusFile.c_str());
	CXXmlNode* pRoot = CXXmlNode::LoadFromFile(strStatusFile);
	if (!pRoot)
	{
		log.LogW(LL_ERR, GetLastError(), L"%s: Failed to parser xml file %s", __WFUNCTION__, strStatusFile.c_str());
		return ARCUPDATE_ERROR_FAILED_INSTALL_UPDATE;
	}

	//
	// get update version number
	//
	UDP_VERSION_INFO version;
	CXXmlNode* pReleaseNode = pRoot->GetNodeByPath(XML_NODEPATH_Release);
	if (!pReleaseNode)
		log.LogW(LL_ERR, 0, L"%s: No release infor defined in available update infor", __WFUNCTION__);
	else
	{
		version.dwMajor = pReleaseNode->GetAttributeDWORD(XML_MajorVersion);
		version.dwMinor = pReleaseNode->GetAttributeDWORD(XML_MinorVersion);
		version.dwServicePack = pReleaseNode->GetAttributeDWORD(XML_ServicePack);
	}

	int index = 0;
	wstring strPackage, strUpdateVersionNumber, strUpdateBuild, strRebootRequired, strDownloadedlocation;
	do
	{	
		strPackage = STRUTILS::fstr(L"%s%d", XML_NODEPATH_Package, index);
		if ( pRoot->GetNodeByPath(strPackage) == NULL) break;

		strUpdateVersionNumber = PATHUTILS::path_join(strPackage, XML_NODESUB_UpdateVersionNumber);
		CXXmlNode* pUpdateVersion = pRoot->GetNodeByPath(strUpdateVersionNumber);
		if (pUpdateVersion)
			version.dwUpdate = _wtoi(pUpdateVersion->GetText().c_str());

		strUpdateBuild = PATHUTILS::path_join(strPackage, XML_NODESUB_UpdateBuild);
		CXXmlNode* pBuild = pRoot->GetNodeByPath(strUpdateBuild);
		if (pBuild)
		{
			wstring strBuild = pBuild->GetText();
			std::vector<wstring> eles;
			STRUTILS::split_str(strBuild, L'.', eles);
			if (eles.size() > 0)
				version.dwBuild = _wtoi(eles[0].c_str());
			if (eles.size() > 1)
				version.dwUpBuild = _wtoi(eles[1].c_str());
		}

		BOOL bReboot = FALSE;
		strRebootRequired = PATHUTILS::path_join(strPackage, XML_NODESUB_RebootRequired);
		CXXmlNode* pNode = pRoot->GetNodeByPath(strRebootRequired);
		if (pNode && STRUTILS::same_str(pNode->GetText(), L"1"))
			bReboot = TRUE;

		strDownloadedlocation = PATHUTILS::path_join(strPackage, XML_NODESUB_Downloadedlocation);
		pNode = pRoot->GetNodeByPath(strDownloadedlocation);
		wstring strExeFile = L"";
		if (pNode)
			strExeFile = pNode->GetText();

		//
		// start to install update
		//

		wstring strProductName = UPUTILS::GetProductName(dwProduct);
		strProductName = STRUTILS::fstr(L"%s(%s)", strProductName.c_str(), STRUTILS::fstr(L"(%d.%d.%d)", version.dwBuild, version.dwUpdate, version.dwUpBuild).c_str());
		log.LogW(LL_INF, 0, L"ARCUpdate: Start to intall binary update for %s", strProductName.c_str());
		if (strExeFile.empty() || !PATHUTILS::is_file_exist(strExeFile))
		{
			log.LogW(LL_ERR, GetLastError(), L"%s: Did not find update binary file %s", __WFUNCTION__, strExeFile.c_str());
			ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_FILE_NOT_FOUND, strProductName.c_str(), strExeFile.c_str());
			return ARCUPDATE_ERROR_FAILED_INSTALL_UPDATE;
		}
		//
		// start to install update
		//
		wstring strCmdLine = L"";
		if (bReboot)
			strCmdLine = STRUTILS::fstr(L"dir \"%s\" >up_aa.txt", strExeFile.c_str());
		else
			strCmdLine = STRUTILS::fstr(L"dir \"%s\" >up_aa.txt", strExeFile.c_str());

		log.LogW(LL_INF, 0, L"%s: Install update with command %s", __WFUNCTION__, strCmdLine.c_str());
		index++;
	} while (1);

	SAFE_DELETE(pRoot);
	return 0;

/*
	BOOL bCreateProcess = FALSE;
	STARTUPINFO si = { 0 };
	PROCESS_INFORMATION pi = { 0 };
	si.cb = sizeof(si);
	si.dwFlags = STARTF_USESHOWWINDOW;
	si.wShowWindow = SW_HIDE;

	
	DWORD dwRet = 0;
	bCreateProcess = CreateProcess(NULL,   // No module name (use command line)
		(LPWSTR)strCmdLine.c_str(),        // Command line
		NULL,           // Process handle not inheritable
		NULL,           // Thread handle not inheritable
		FALSE,          // Set handle inheritance to FALSE
		0,              // No creation flags
		NULL,           // Use parent's environment block
		NULL,           // Use parent's starting directory 
		&si,            // Pointer to STARTUPINFO structure
		&pi);           // Pointer to PROCESS_INFORMATION structure

	if (!bCreateProcess)
	{
		dwRet = ::GetLastError();
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_INF_INSTALL_ERROR, strProductName.c_str(), dwRet);
		log.LogW(LL_ERR, dwRet, L"%s: Failed to create process", __WFUNCTION__);
		return ARCUPDATE_ERROR_FAILED_INSTALL_UPDATE;
	}
	else
	{
		// Wait until child process exits.
		WaitForSingleObject(pi.hProcess, INFINITE);
		GetExitCodeProcess(pi.hProcess, &dwRet);
		CloseHandle(pi.hProcess);
		log.LogW(LL_INF, dwRet, L"%s: Install update returned with error %d", __WFUNCTION__, dwRet);
	}

	switch (dwRet)
	{
	case 0:
		ACTLOGUTILS::ActivityLog(dwProduct, AFINFO, ARCUPDATE_INF_INSTALL_OK, strProductName.c_str());
		break;
	case 3010:
		ACTLOGUTILS::ActivityLog(dwProduct, AFWARNING, ARCUPDATE_WAR_INSTALL_REBOOT_REQUIRED, strProductName.c_str());
		break;
	case -520026879: // D2D is not installed
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_PRODUCT_MISSING, strProductName.c_str());
		break;
	case -520027899:
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_ANOTHER_INSTANCE_RUNNING, strProductName.c_str());
		break;
	case -520026878:
	case -520026877:
	case -520026875:
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_NOT_APPLICABLE, strProductName.c_str());
		break;
	case -520026876: // patch already installed 
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_ALREADY_INSTALLED, strProductName.c_str());
		break;
	case -520026874: // Job is running
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_RUNNING_JOB, strProductName.c_str());
		break;
	case -520026873: // Disk space is not enough
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_INSUFFICIENT_SPACE, strProductName.c_str());
		break;
	case 90000: // self-uncompress failed
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_UNCOMPRESS_FAILED, strProductName.c_str());
		break;
	default:
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_INF_INSTALL_ERROR, strProductName.c_str(), dwRet);
		break;
	}
	return (dwRet == 0 ? 0 : ARCUPDATE_ERROR_FAILED_INSTALL_UPDATE);
	*/
}

//
// get the update status.xml
//
std::wstring WINAPI GetBIUpdateStatusXmlFile(DWORD dwProduct)
{
	return UPUTILS::GetBIUpdateStatusFile(dwProduct);
}

//added by cliicy.luo

//
// detect if update is busy
//
DWORD WINAPI IsUpdateBusy(DWORD dwProduct, PBOOL pbBusy)
{
	if (!pbBusy)
		return ERROR_INVALID_PARAMETER;

	CDbgLog log;

	if (isUpdateServiceStopped()){
		log.LogW(LL_ERR, 0, L"%s: UDP Auto update service is disabled or not started. Please start this service and try again.", __WFUNCTION__);
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	}

	LONG lRet = 0;
	CPipeClient pClient;
	lRet = pClient.Connect();
	if (lRet != 0)
	{
		log.LogW(LL_ERR, lRet, L"%s: Failed to connect to update service. Please check if UDP auto update service is running.", __WFUNCTION__);
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	}

	return pClient.isUpdateBusy(dwProduct, pbBusy);
}

//
// cancel update job of product
//
DWORD WINAPI AUCancelUpdateJob(DWORD dwProduct)
{
	CDbgLog log;
	log.LogW(LL_INF, 0, L"%s: Cancel update job of %d", __WFUNCTION__, dwProduct);

	if (isUpdateServiceStopped()){
		log.LogW(LL_ERR, 0, L"%s: UDP Auto update service is disabled or not started. Please start this service and try again.", __WFUNCTION__);
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	}

	LONG lRet = 0;
	CPipeClient pClient;
	lRet = pClient.Connect();
	if (lRet != 0)
	{
		log.LogW(LL_ERR, lRet, L"%s: Failed to connect to update service. Please check if UDP auto update service is running.", __WFUNCTION__);
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	}

	return pClient.cancelUpdateJob(dwProduct);
}

DWORD WINAPI QueryUpdateStatus(DWORD dwProduct, UPDATE_JOB_MONITOR* pStatus)
{
	if (!pStatus)
		return ERROR_INVALID_PARAMETER;

	CDbgLog log;

	if (isUpdateServiceStopped()){
		log.LogW(LL_ERR, 0, L"%s: UDP Auto update service is disabled or not started. Please start this service and try again.", __WFUNCTION__);
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	}

	LONG lRet = 0;
	CPipeClient pClient;
	lRet = pClient.Connect();
	if (lRet != 0)
	{
		log.LogW(LL_ERR, lRet, L"%s: Failed to connect to update service. Please check if UDP auto update service is running.", __WFUNCTION__);
		return ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED;
	}

	lRet = pClient.queryUpdateStatus(dwProduct, pStatus);
	pStatus->lLastError = handleErrors(pStatus->lLastError);
	return lRet;
}

//
// get the update status.xml
//
std::wstring WINAPI GetUpdateStatusXmlFile(DWORD dwProduct)
{
	return UPUTILS::GetUpdateStatusFile(dwProduct);
}

//
// get the update settings file
//
std::wstring WINAPI GetUpdateSettingsFile(DWORD dwProduct)
{
	return UPUTILS::GetUpdateSettingXmlFile(dwProduct, TRUE);
}

//
// install update from web service
//
DWORD WINAPI InstallUpdate(DWORD dwProduct)
{

	CDbgLog log;
	log.LogW(LL_INF, 0, L"oooo ARCUpdateDLL InstallUpdate ####begin.");

	wstring strStatusFile = GetUpdateStatusXmlFile(dwProduct);
	log.LogW(LL_INF, 0, L"ooooo %s strStatusFile=%s", __WFUNCTION__, strStatusFile.c_str());

	CXXmlNode* pRoot = CXXmlNode::LoadFromFile(strStatusFile);
	if (!pRoot)
	{
		log.LogW(LL_ERR, GetLastError(), L"%s: Failed to parser xml file %s", __WFUNCTION__, strStatusFile.c_str());
		return ARCUPDATE_ERROR_FAILED_INSTALL_UPDATE;
	}

	//
	// get update version number
	//
	UDP_VERSION_INFO version;
	CXXmlNode* pReleaseNode = pRoot->GetNodeByPath(XML_NODEPATH_Release);
	if (!pReleaseNode)
		log.LogW(LL_ERR, 0, L"%s: No release infor defined in available update infor", __WFUNCTION__);
	else
	{
		version.dwMajor = pReleaseNode->GetAttributeDWORD(XML_MajorVersion);
		version.dwMinor = pReleaseNode->GetAttributeDWORD(XML_MinorVersion);
		version.dwServicePack = pReleaseNode->GetAttributeDWORD(XML_ServicePack);
	}
	
	CXXmlNode* pUpdateVersion = pRoot->GetNodeByPath(XML_NODEPATH_UpdateVersionNumber);
	if (pUpdateVersion)
		version.dwUpdate = _wtoi(pUpdateVersion->GetText().c_str());

	CXXmlNode* pBuild = pRoot->GetNodeByPath(XML_NODEPATH_UpdateBuild);
	if (pBuild)
	{
		wstring strBuild = pBuild->GetText();
		std::vector<wstring> eles;
		STRUTILS::split_str(strBuild, L'.', eles);
		if (eles.size() > 0)
			version.dwBuild = _wtoi(eles[0].c_str());
		if (eles.size() > 1)
			version.dwUpBuild = _wtoi(eles[1].c_str());
	}

	BOOL bReboot = FALSE;
	CXXmlNode* pNode = pRoot->GetNodeByPath(XML_NODEPATH_RebootRequired);
	if ( pNode && STRUTILS::same_str(pNode->GetText(), L"1"))
		bReboot = TRUE;

	pNode = pRoot->GetNodeByPath(XML_NODEPATH_Downloadedlocation);
	wstring strExeFile = L"";
	if (pNode)
		strExeFile = pNode->GetText();

	SAFE_DELETE(pRoot);

	//
	// start to install update
	//

	wstring strProductName = UPUTILS::GetProductName(dwProduct);
	strProductName = STRUTILS::fstr(L"%s(%s)", strProductName.c_str(), STRUTILS::fstr(L"(%d.%d.%d)", version.dwBuild, version.dwUpdate, version.dwUpBuild).c_str() );
	log.LogW(LL_INF, 0, L"ARCUpdate: Start to intall update for %s", strProductName.c_str());
	if (strExeFile.empty() || !PATHUTILS::is_file_exist(strExeFile))
	{
		log.LogW(LL_ERR, GetLastError(), L"%s: Did not find update file %s", __WFUNCTION__, strExeFile.c_str());
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_FILE_NOT_FOUND, strProductName.c_str(), strExeFile.c_str());
		return ARCUPDATE_ERROR_FAILED_INSTALL_UPDATE;
	}


	//
	// start to install update
	//
	wstring strCmdLine = L"";
	if (bReboot)
		strCmdLine = STRUTILS::fstr(L"\"%s\" /s /v\"/s /AutoReboot\"", strExeFile.c_str());
	else
		strCmdLine = STRUTILS::fstr(L"\"%s\" /s /v\"/s\"", strExeFile.c_str());

	log.LogW(LL_INF, 0, L"%s: Install update with command %s", __WFUNCTION__, strCmdLine.c_str());


	BOOL bCreateProcess = FALSE;
	STARTUPINFO si = { 0 };
	PROCESS_INFORMATION pi = { 0 };
	si.cb = sizeof(si);
	si.dwFlags = STARTF_USESHOWWINDOW;
	si.wShowWindow = SW_HIDE;

	DWORD dwRet = 0;
	bCreateProcess = CreateProcess(NULL,   // No module name (use command line)
		(LPWSTR)strCmdLine.c_str(),        // Command line
		NULL,           // Process handle not inheritable
		NULL,           // Thread handle not inheritable
		FALSE,          // Set handle inheritance to FALSE
		0,              // No creation flags
		NULL,           // Use parent's environment block
		NULL,           // Use parent's starting directory 
		&si,            // Pointer to STARTUPINFO structure
		&pi);           // Pointer to PROCESS_INFORMATION structure

	if (!bCreateProcess)
	{
		dwRet = ::GetLastError();
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_INF_INSTALL_ERROR, strProductName.c_str(), dwRet);
		log.LogW(LL_ERR, dwRet, L"%s: Failed to create process", __WFUNCTION__);
		return ARCUPDATE_ERROR_FAILED_INSTALL_UPDATE;
	}
	else
	{
		// Wait until child process exits.
		WaitForSingleObject(pi.hProcess, INFINITE);
		GetExitCodeProcess(pi.hProcess, &dwRet);
		CloseHandle(pi.hProcess);
		log.LogW(LL_INF, dwRet, L"%s: Install update returned with error %d", __WFUNCTION__, dwRet);
	}

	switch (dwRet)
	{
	case 0:
		ACTLOGUTILS::ActivityLog(dwProduct, AFINFO, ARCUPDATE_INF_INSTALL_OK, strProductName.c_str());
		break;
	case 3010:
		ACTLOGUTILS::ActivityLog(dwProduct, AFWARNING, ARCUPDATE_WAR_INSTALL_REBOOT_REQUIRED, strProductName.c_str());
		break;
	case -520026879: // D2D is not installed
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_PRODUCT_MISSING, strProductName.c_str());
		break;
	case -520027899:
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_ANOTHER_INSTANCE_RUNNING, strProductName.c_str());
		break;
	case -520026878:
	case -520026877:
	case -520026875:
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_NOT_APPLICABLE, strProductName.c_str());
		break;
	case -520026876: // patch already installed 
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_ALREADY_INSTALLED, strProductName.c_str());
		break;
	case -520026874: // Job is running
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_RUNNING_JOB, strProductName.c_str());
		break;
	case -520026873: // Disk space is not enough
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_INSUFFICIENT_SPACE, strProductName.c_str());
		break;
	case 90000: // self-uncompress failed
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_ERR_INSTALL_UNCOMPRESS_FAILED, strProductName.c_str());
		break;
	default:
		ACTLOGUTILS::ActivityLog(dwProduct, AFERROR, ARCUPDATE_INF_INSTALL_ERROR, strProductName.c_str(), dwRet);
		break;
	}
	return (dwRet == 0 ? 0 : ARCUPDATE_ERROR_FAILED_INSTALL_UPDATE);
}

//
// get the update error message
//
DWORD WINAPI GetUpdateErrorMessage(DWORD dwProduct, DWORD dwErrorCode, std::wstring& strErrMsg)
{
	strErrMsg = L"";
	wstring strProductName = UPUTILS::GetProductName(dwProduct);
	switch (dwErrorCode)
	{
	case ARCUPDATE_SUCCEED:
		break;
	case ARCUPDATE_ERROR_UP_TO_DATE:
		strErrMsg = UPUTILS::GetUpdateResourceString(ARCUPDATE_INF_PRODUCT_UPTODATE, strProductName.c_str());
		break;
	case ARCUPDATE_ERROR_NO_UPDATE_FOUND:
		strErrMsg = UPUTILS::GetUpdateResourceString(ARCUPDATE_INF_NO_UPDATE_FOUND, strProductName.c_str());
		break;
	case ARCUPDATE_ERROR_SERVER_UNAVAILABLE:
		strErrMsg = UPUTILS::GetUpdateResourceString(ARCUPDATE_ERR_SERVER_UNAVAILABLE);
		break;
	case ARCUPDATE_ERROR_FAILED_TO_DOWNLOAD:
		strErrMsg = UPUTILS::GetUpdateResourceString(ARCUPDATE_ERR_FAILED_DOWNLOAD_UPDATE, strProductName.c_str(), STRUTILS::fstr(L"\\Update Manager\\Log\\Update_%d.log", dwProduct).c_str());
		break;
	case ARCUPDATE_ERROR_CANCELED:
		strErrMsg = UPUTILS::GetUpdateResourceString(ARCUPDATE_WAR_CANCELED);
		break;
	case ARCUPDATE_ERROR_ANOTHER_IS_RUNNING:
		strErrMsg = UPUTILS::GetUpdateResourceString(ARCUPDATE_ERR_BUSY);
		break;
	case ARCUPDATE_ERROR_FAILED_INSTALL_UPDATE:
		strErrMsg = UPUTILS::GetUpdateResourceString(ARCUPDATE_ERR_INSTALL_FAILED, strProductName.c_str());
		break;
	case ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED:
		strErrMsg = UPUTILS::GetUpdateResourceString(ARCUPDATE_ERR_SERVICE_NOT_STARTED);
		break;
	case ARCUPDATE_ERROR_SELF_UPDATE_REQUIRED:
		strErrMsg = UPUTILS::GetUpdateResourceString(ARCUPDATE_WAR_SELFUPDATE_REQUIRED);
		break;
	default:
		strErrMsg = UPUTILS::GetUpdateResourceString(ARCUPDATE_ERR_UNKNOWN, dwErrorCode);
		break;
	}
	return 0;
}

//
// disable auto update from custom tool
//
DWORD WINAPI DisableAutoUpdate(DWORD dwProduct, BOOL bDisable)
{
	CDbgLog logObj;
	logObj.LogW(LL_INF, 0, L"%s: Product[%d], Disable[%d]", __WFUNCTION__, dwProduct, bDisable);
	if (dwProduct == ARCUPDATE_PRODUCT_AGENT && PRODUTILS::IsAgentManagedByConsole() )
	{
		logObj.LogW(LL_INF, 0, L"%s: Agent is managed by console. Don't have to do anything.", __WFUNCTION__);
		return 0;
	}

	wstring strFile = UPUTILS::GetUpdateSettingXmlFile(dwProduct, TRUE);
	UDP_UPDATE_SETTINGS updateSettings;
	UPUTILS::ReadUpdateSettingsFromFile(strFile, updateSettings);
	if ((updateSettings.scheduler.bDisabled && bDisable) ||
		(!updateSettings.scheduler.bDisabled && !bDisable))
	{
		return 0;
	}
	updateSettings.scheduler.bDisabled = bDisable;
	return UPUTILS::SaveUpdateSettingsToFile(strFile, updateSettings);
}

DWORD WINAPI GetLastAvailableUpdate(DWORD dwProduct, WCHAR* pszVersion, PDWORD pdwSizeOfVersion, WCHAR* pszFilePath, PDWORD pdwSizeOfFilePath, PBOOL pbRebootRequired)
{
	if (!pdwSizeOfVersion || !pdwSizeOfFilePath)
		return ERROR_INVALID_PARAMETER;

	if (dwProduct != ARCUPDATE_PRODUCT_AGENT && dwProduct != ARCUPDATE_PRODUCT_FULL && dwProduct != ARCUPDATE_PRODUCT_GATEWAY)
		return ERROR_INVALID_PARAMETER;

	wstring strVersion = L"";
	wstring strFilePath = L"";
	UDP_VERSION_INFO version;
	DWORD dwRet = UPUTILS::GetLastAvailableUpdateOfProduct(dwProduct, version, strFilePath, pbRebootRequired );
	if (dwRet != 0)
		return dwRet;
	strVersion = UPUTILS::VersionToString(version);
	if (!pszVersion || (*pdwSizeOfVersion) <= strVersion.length())
	{
		(*pdwSizeOfVersion) = strVersion.length() + 1;
		(*pdwSizeOfFilePath) = strFilePath.length() + 1;
		return ERROR_INSUFFICIENT_BUFFER;
	}
	if (!pszFilePath || (*pdwSizeOfFilePath) <= strFilePath.length())
	{
		(*pdwSizeOfVersion) = strVersion.length() + 1;
		(*pdwSizeOfFilePath) = strFilePath.length() + 1;
		return ERROR_INSUFFICIENT_BUFFER;
	}

	wcscpy_s(pszVersion, (*pdwSizeOfVersion), strVersion.c_str());
	wcscpy_s(pszFilePath, (*pdwSizeOfFilePath), strFilePath.c_str());
	return 0;
}