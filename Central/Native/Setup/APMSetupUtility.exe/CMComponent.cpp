#include "CMComponent.h"
#include "Utility.h"
#include "Golbals.h"
#include "ApmFactory.h"
#include "ApmBackendStatus.h"
#include "MSXMLParserWrapper.h"
#include "StatusObserver.h"

//#define SERVICE_ARCAPP								_T("CAArcAppSvc")
#define EDGE_APM_EDGE_PIPE_NAME_GUID	L"\\\\.\\PIPE\\9106a996-d21f-49a6-bc9e-f4fd3ecadc9e"
CCMComponent::CCMComponent(void):CCAComponent()
{
}

CCMComponent::~CCMComponent(void)
{
}

void CCMComponent::Initialize(LPCTSTR lpctFile, LPCTSTR lpctSection)
{
	CCAComponent::Initialize(lpctFile, lpctSection);
}

DWORD CCMComponent::HandleWindowsService()
{
	bServerRuningBeforeUpdate =	IsServiceStart(SERVICE_ARCAPP);

	if (bServerRuningBeforeUpdate)
	{
		ChangeSvcStartType(SERVICE_ARCAPP, SERVICE_AUTO_START);

		BOOL dwError = StopSpecService(SERVICE_ARCAPP, TRUE, TRUE);

		if (!dwError)
		{
			WriteLog(_T("Fail to stop EDGE service before update, ec=%d"), dwError);
			//pDlg->PostMessage(WM_APM_STATUS_CHANGED, IDS_FAIL_STOP_EDGE, APM_STATUS_ERROR);
			return dwError;
		}
		WriteLog(_T("stop console service before update"));
	}
	else
	{
		WriteLog(_T("%s is not running before update."), SERVICE_ARCAPP);
	}

	return ERROR_SUCCESS;
}

BOOL CCMComponent::BackendExeRun()
{
	TCHAR szWorkingDir[MAX_PATH];
	CString m_strCmd = _T("");
	memset(szWorkingDir, 0, sizeof(szWorkingDir));
	_tcscpy_s(szWorkingDir, _countof(szWorkingDir), m_strInstallPath);
	::PathRemoveBackslash(szWorkingDir);
	::PathAddBackslash(szWorkingDir);
	m_strCmd = szWorkingDir;
	::PathAppend(szWorkingDir, _T("BIN"));
	m_strCmd = _T("\"") + m_strCmd + _T("Update Manager\\ArcApp\\ArcAppUpdateManager.exe") + _T("\"");
	DWORD m_dwExitCode;

	LaunchProcess(m_strCmd, szWorkingDir, m_dwExitCode, INFINITE, DETACHED_PROCESS|CREATE_NO_WINDOW);

	if (m_dwExitCode != 0)
	{
		return FALSE;
	}

	return TRUE;
}

DWORD CCMComponent::TestServerConnection(const APMSetting &apmSetting)
{
	//SetEnvPath();
	DWORD dwRet = 0;
		
	wstring strSubDir = m_strInstallPath;
	strSubDir += L"\\" + wstring(FOLDER_UPDATEMANAGER) + L"\\" + FOLDER_UPDATEMANAGER_ARCAPP + L"\\";
		
	CoInitialize(NULL);
	wstring strUri = strSubDir + FILE_EDGE_PMClient_XML;
	HXMLDOCUMENT hDoc = JobCreateDocumentFromXML(strUri.c_str());
	HXMLELEMENT hElement = JobGetElement(hDoc, L"/client/product[@Name='CA ARCserve Edge Common']/downloadinfo/Protocol/pathonsource");
	WCHAR chUri[128] = {L'\0'};
	DWORD bufSize = _countof(chUri);
	JobGetElementValue(hElement, chUri, &bufSize);
	JobDestroyElement(hElement);
	hElement = JobGetElement(hDoc, L"/client/product[@Name='CA ARCserve Edge Common']/downloadinfo/Protocol/ServerName");
	WCHAR chCaServer[128] ={L'\0'};
	bufSize = _countof(chCaServer);
	JobGetElementValue(hElement, chCaServer, &bufSize);
	JobDestroyElement(hElement);
	JobDestroyDocument(hDoc);
	CoUninitialize();

	wstring strServerName;
	WCHAR chPort[10] = L"80";
	wstring strProxy;
	WCHAR chProxyPort[10] = L"";
	wstring strProxyUser;
	wstring	strProxyPassword;

	if (apmSetting.m_nServerType == APM_SERVERTYPE_CA)
	{
		strServerName = wstring(chCaServer);
		if (apmSetting.m_bProxy)
		{
			strProxy = apmSetting.m_proxyServer.m_strServer;
			_itow(apmSetting.m_proxyServer.m_nPort, chProxyPort, 10);
			if (apmSetting.m_bProxyAuth)
			{
				strProxyUser = apmSetting.m_strProxyUsername;
				strProxyPassword = apmSetting.m_strProxyPwd;
			}
		}
	}
	else
	{
		strServerName = apmSetting.m_vecStaging[0].m_strServer;
		_itow(apmSetting.m_vecStaging[0].m_nPort, chPort, 10);
	}

	//CString strFile = _T("C:\\Program Files\\CA\ARCserve Unified Data Protection\\Management\\Update Manager\\ArcApp\\EdgePMCommandBase.dll");
	//HMODULE hModule = ::LoadLibraryEx(strFile, NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
	wstring strDllPath = strSubDir + FILE_EdgePMCommandBase_dll;
	HMODULE dllHandle;
	typedef INT (*PFunDownloadServerStatus)(TCHAR*, UINT, WCHAR*, WCHAR*, WCHAR*, WCHAR*, WCHAR*, WCHAR*);
	PFunDownloadServerStatus pfntestDownloadServerStatus = (PFunDownloadServerStatus)DynGetProcAddress(strDllPath, "IS_SERVER_AVBL2", dllHandle);
	if (pfntestDownloadServerStatus == NULL)
	{
		WriteLog(_T("Failed to call IS_SERVER_AVBL2 in %s. ec=%d"), strDllPath.c_str(), GetLastError());
		if (dllHandle)
		{
			FreeLibrary(dllHandle);
		}
		return 1;
	}

	WriteLog(_T("Begin to test connection: server type:%d, server name:%s, server port:%s, proxy server:%s, proxy port:%s, proxy user:%s"),
		apmSetting.m_nServerType,
		strServerName.c_str(),
		chPort,
		strProxy.c_str(),
		chProxyPort,
		strProxyUser.c_str());

	int nStatus = pfntestDownloadServerStatus(chUri, 
		apmSetting.m_nServerType,
		(TCHAR *)strServerName.c_str(),
		chPort,
		(TCHAR *)strProxy.c_str(),
		(TCHAR *)chProxyPort,
		(TCHAR *)strProxyUser.c_str(),
		(TCHAR *)strProxyPassword.c_str());

	if (nStatus == 0)
		dwRet = 0;
	else
	{
		dwRet = 2;
	}

	if (dllHandle != NULL)
	{
		FreeLibrary(dllHandle);
	}
		//BOOL bRet = FreeLibrary(hDll);
	return dwRet;
}

DWORD CCMComponent::downloadPatch(HANDLE handle, Response& response)
{
	WriteLog(_T("start downloading patch."));
	const CStatusObserver & statusObserver = CApmFactory::GetEdgeStatusObserver();
	if (statusObserver.WaitForOk(10))
	{
		WriteLog(_T("Back end is busy or not running."));
		return 1;
	}

	DWORD dwError;
	hPipe = CreateFile(EDGE_APM_EDGE_PIPE_NAME_GUID,
			GENERIC_READ |  // read and write access 
			GENERIC_WRITE,
			FILE_SHARE_WRITE|FILE_SHARE_READ,              // no sharing 
			NULL,           // default security attributes
			OPEN_EXISTING,  // opens existing pipe 
			FILE_ATTRIBUTE_NORMAL|FILE_FLAG_OVERLAPPED,              // default attributes 
			NULL);          // no template file 

	if (hPipe == INVALID_HANDLE_VALUE)
	{
		dwError = GetLastError();
		WriteLog(_T("fail to create named pipe, ec=%d."), dwError);
		return 2;
	}

	wstring strRequestId, request;
	strRequestId = generateRequestId();
	dwError = createCheckUpdateRequest(strRequestId, request, APM_EDGE_CM, m_nMajor, m_nMinor);
	if (dwError)
	{
		WriteLog(_T("fail to create request xml, ec=%d"), dwError);
			//pDlg->PostMessage(WM_APM_STATUS_CHANGED, IDS_UNKNOW_ERROR, APM_STATUS_ERROR);
		//CloseHandle(hPipe);
		return 3;
	}
	WriteLog(_T("send request xml: %s"), request.c_str());
	DWORD dwWrite;
	BOOL bResult = WriteFile(hPipe, request.c_str(), request.length()*sizeof(WCHAR), &dwWrite, NULL);
	if (!bResult)
	{
		dwError = GetLastError();
		WriteLog(_T("fail to write to named pipe, %d"), dwError);
		//pDlg->PostMessage(WM_APM_STATUS_CHANGED, IDS_ERROR_BACKEND, APM_STATUS_ERROR);
		//CloseHandle(hPipe);
		return 4;
	}

	WCHAR buf[MAX_PATH*40];
	WCHAR buf2[MAX_PATH*40];
	DWORD dwRead;
	memset(buf, 0, sizeof(buf));
	wstring strResponse;
	//struct Response response;
	while(true)
	{
		memset(buf2, 0, sizeof(buf2));
		dwError = ReadFileEx(hPipe, buf2, sizeof(buf2), &dwRead, handle, dwMilliseconds);
		if (dwError)
		{
			if (2 == dwError)
			{ // timeout
				WriteLog(_T("time-out when read from named pipe"));					
			}
			else if (3 == dwError)
			{
				WriteLog(_T("receive cancel from ui"));
			}
			else
			{
				WriteLog(_T("read file from named pipe error, %d"), GetLastError());
				
			}
			//pDlg->PostMessage(WM_APM_STATUS_CHANGED, IDS_ERROR_BACKEND, APM_STATUS_ERROR);
			//CloseHandle(hPipe);
			return 5;
		}

		wcscpy_s(buf + wcslen(buf), _countof(buf)-wcslen(buf), buf2);

		strResponse.clear();
		dwError = getLastXmlDoc(buf, strResponse);
		if (dwError)
		{
			WriteLog(L"Response string is not a valid xml document. length=%d", dwRead);
			//pDlg->PostMessage(WM_APM_STATUS_CHANGED, IDS_ERROR_BACKEND, APM_STATUS_ERROR);
			//CloseHandle(hPipe);
			return 6;
		}

		memset(&response, 0, sizeof(response));
		dwError = ProcessResponse(strResponse, response);
		if (dwError == 0 && 
			CompareStringIgnoreCase(response.m_strType, APM_REQUEST_TYPE_UICOMMAND) == 0 && 
			CompareStringIgnoreCase(response.m_strId, strRequestId) == 0)
			break;
	}

	if (!response.m_strError.empty())
	{
		WriteLog(L"Fail to download patch for product %d, %s", APM_EDGE_CM, response.m_strError.c_str());
		//CloseHandle(hPipe);
		return 7;
	}
		
	CloseHandle(hPipe);
	Sleep(1000);

	return ERROR_SUCCESS;
}

DWORD CCMComponent::PatchInstall()
{
	WriteLog(_T("Edge path installation."));
	PackInfo pack;
	wstring strStatusPath = m_strInstallPath;
	strStatusPath += L"Update Manager\\ArcApp\\CM\\Status.xml";
	if (!IsFileExist(strStatusPath))
	{
		return 2;
	}

	DWORD dwError = GetPackInfoFromXmlFile(strStatusPath, pack);
	if (dwError)
	{
		WriteLog(_T("fail to get pack info for packId APM_EDGE_CM"));
		return 3;
	}

	int nPatchVersion;
	UINT nCurrentPatchVersion;
	nPatchVersion = _wtoi(pack.m_strUpdateVersionNumber.c_str());
	GetProductPatchVersion(APM_EDGE_CM, nCurrentPatchVersion);
	if (nPatchVersion <= nCurrentPatchVersion 
			|| pack.m_iDownload != 1 // Not downloaded
			|| pack.m_iInstall == 1 // Already installed
			)
	{
		return 4;
	}
	/*
	CString m_strLocation, m_strCmd = _T("");
	m_strLocation.Format(_T("%s"), pack.m_strDownloadLocation);
	m_strCmd = _T("\"") + m_strLocation + _T("\"") + _T(" /s /v\"/s /m\"");
	LaunchProcess(m_strCmd, NULL, m_dwExitCode, INFINITE, DETACHED_PROCESS|CREATE_NO_WINDOW);
	*/
	dwError = InstallPatch(pack);
	if (dwError != 0)
	{
		WriteLog(_T("Fail to launch CM patch."));
	}
	wstring strUpdateExitCode;
	strUpdateExitCode.clear();
	BOOL bRebootRequired = FALSE;
	GetProductExtentionKey(APM_EDGE_CM, REG_EDGE_UpdateExitCode, strUpdateExitCode);
	WriteLog(_T("Update exit code is %s"), strUpdateExitCode.c_str());
	if (CompareStringIgnoreCase(strUpdateExitCode, SETUP_UPDATE_EXITCODE_REBOOT) == 0)
	{	
		bRebootRequired = TRUE;
	}
	else if (CompareStringIgnoreCase(strUpdateExitCode, SETUP_UPDATE_EXITCODE_SUCCESS) != 0)
	{
		WriteLog(_T("fail to install patch with error code %s"), strUpdateExitCode.c_str());
	}

	if (bRebootRequired == TRUE)
	{
		return 3010;
	}

	return ERROR_SUCCESS;
}

void CCMComponent::DoExitThings()
{
	//CloseHandle(hPipe);
	if (bServerRuningBeforeUpdate)
	{
		WriteLog(_T("start edge service"));
		StartSpecService(SERVICE_ARCAPP, TRUE);
	}
}

void CCMComponent::downloadD2DPatch(HANDLE handle, Response& response)
{
	;
}