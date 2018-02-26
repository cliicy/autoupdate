#include "D2DComponent.h"
#include "Golbals.h"
#include "MSXMLParserWrapper.h"
#include "Utility.h"

#define EDGE_APM_D2D_PIPE_NAME_GUID			L"\\\\.\\PIPE\\0EC961A0-32EA-4eff-82A8-D1D08B77A053"

CD2DComponent::CD2DComponent(void)
{
}

CD2DComponent::~CD2DComponent(void)
{
}
void CD2DComponent::Initialize(LPCTSTR lpctFile, LPCTSTR lpctSection)
{
	CCAComponent::Initialize(lpctFile, lpctSection);
}
BOOL CD2DComponent::BackendExeRun()
{
	TCHAR szWorkingDir[MAX_PATH];
	CString m_strCmd = _T("");
	memset(szWorkingDir, 0, sizeof(szWorkingDir));
	_tcscpy_s(szWorkingDir, _countof(szWorkingDir), m_strInstallPath);
	::PathRemoveBackslash(szWorkingDir);
	::PathAddBackslash(szWorkingDir);
	m_strCmd = szWorkingDir;
	m_strCmd = _T("\"")+ m_strCmd + _T("Update Manager\\D2DUpdateManager.exe") + _T("\"") 
						+ _T(" ") + _T("\\\\.\\PIPE\\0EC961A0-32EA-4eff-82A8-D1D08B77A053") + 
						+ _T(" ") + _T("Global\\56E363E4-1CD4-4c2e-ADE2-71A2257DEB95") +
						+ _T(" ") + _T("Global\\B35FAC3C-66A6-467f-B37D-567503A52A86");
	::PathAppend(szWorkingDir, _T("BIN"));
	DWORD m_dwExitCode;
	LaunchProcess(m_strCmd, szWorkingDir, m_dwExitCode, INFINITE, DETACHED_PROCESS|CREATE_NO_WINDOW);
	if(m_dwExitCode != 0)
	{
		return FALSE;
	}
	return TRUE;
}
DWORD CD2DComponent::TestServerConnection(const APMSetting &apmSetting)
{
	//SetEnvPath();
	DWORD dwRet = 0;
		
	//wstring strSubDir;
	wstring strSubDir = m_strInstallPath;
	//strSubDir += wstring(FOLDER_UPDATEMANAGER)  + L"\\";
	strSubDir += L"\\" + wstring(FOLDER_UPDATEMANAGER)  + L"\\";
	CoInitialize(NULL);
	wstring strUri = strSubDir + FILE_D2D_PMCLIENT_XML;
	HXMLDOCUMENT hDoc = JobCreateDocumentFromXML(strUri.c_str());
	HXMLELEMENT hElement = JobGetElement(hDoc, L"/client/product[@Name='CA ARCserve D2D']/downloadinfo/Protocol/pathonsource");
	WCHAR chUri[128] = {L'\0'};
	DWORD bufSize = _countof(chUri);
	JobGetElementValue(hElement, chUri, &bufSize);
	JobDestroyElement(hElement);
	hElement = JobGetElement(hDoc, L"/client/product[@Name='CA ARCserve D2D']/downloadinfo/Protocol/ServerName");
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
	if( apmSetting.m_nServerType == APM_SERVERTYPE_CA ){
		strServerName = wstring(chCaServer);
		if( apmSetting.m_bProxy )
		{
			strProxy = apmSetting.m_proxyServer.m_strServer;
			_itow( apmSetting.m_proxyServer.m_nPort, chProxyPort, 10 );
			if( apmSetting.m_bProxyAuth )
			{
			strProxyUser = apmSetting.m_strProxyUsername;
			strProxyPassword = apmSetting.m_strProxyPwd;
			}
		}
	}else
	{
		strServerName = apmSetting.m_vecStaging[0].m_strServer;
		_itow( apmSetting.m_vecStaging[0].m_nPort, chPort, 10 );
	}


	wstring strDllPath = strSubDir + FILE_D2DPMCOMMANDBASE_DLL;
	HMODULE dllHandle;
	typedef INT (*PFunDownloadServerStatus)(TCHAR*, UINT, WCHAR*, WCHAR*, WCHAR*, WCHAR*, WCHAR*, WCHAR*);
	PFunDownloadServerStatus pfntestDownloadServerStatus = (PFunDownloadServerStatus)DynGetProcAddress( strDllPath, "IS_SERVER_AVBL2", dllHandle);
	if( pfntestDownloadServerStatus == NULL )
	{
		WriteLog(_T("Failed to call IS_SERVER_AVBL2 in %s. ec=%d"), strDllPath.c_str(), GetLastError());
		if(dllHandle)
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
	WriteLog(_T("d2d testDownloadServerStatus return %d"), nStatus);
	if( nStatus == 0 )
		dwRet = 0;
	else
	{
		dwRet = 2;
	}
	if(dllHandle != NULL)
	{
		FreeLibrary(dllHandle);
	}

		//BOOL bRet = FreeLibrary(hDll);
	return dwRet;
}

DWORD CD2DComponent::downloadPatch(HANDLE handle, Response& response)
{
	//HANDLE hPipe = NULL;
	DWORD dwError;
	hPipe = CreateFile( EDGE_APM_D2D_PIPE_NAME_GUID,
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
		WriteLog(_T("fail to create named pipe, ec=%d "), dwError);
		//::CloseHandle(hPipe);
		return 2;
	}
	wstring strRequestId, request;
	WriteLog(_T("create d2d named pipe successfully."));
	strRequestId = generateRequestId();
	dwError = createCheckUpdateRequest(strRequestId, request, APM_D2D, m_nMajor, m_nMinor);
	if( dwError )
	{
		WriteLog(_T("fail to create request xml, ec=%d"), dwError);
		//CloseHandle(hPipe);
		return 3;
	}
	WriteLog(_T("send request xml: %s"), request.c_str() );

	DWORD dwWrite;
	BOOL bResult = WriteFile(hPipe, request.c_str(), request.length()*sizeof(WCHAR), &dwWrite, NULL);
	if(!bResult){
		dwError = GetLastError();
		WriteLog(_T("fail to write to named pipe, %d"), dwError);
		//CloseHandle(hPipe);
		return 4;
	}

	WCHAR buf[MAX_PATH*40];
	WCHAR buf2[MAX_PATH*40];
	memset(buf, 0, sizeof(buf));
	int nBufUsed = 0;
	DWORD dwRead;
	wstring strResponse;
	//struct Response response;
	while( true ){
		memset( buf2, 0, sizeof(buf2));
		dwError = ReadFileEx( hPipe, buf2, sizeof(buf2), &dwRead, handle, dwMilliseconds);
		if( dwError ){
			if( 2 == dwError ){ // timeout
				WriteLog(_T("time-out when read from named pipe"));					
			}else if( 3 == dwError ){
				WriteLog(_T("receive cancel from ui"));
			}else{
				WriteLog(_T("read file from named pipe error, %d"), GetLastError() );
			}
			//pDlg->PostMessage( WM_APM_STATUS_CHANGED, IDS_ERROR_BACKEND, APM_STATUS_ERROR );
			//CloseHandle(hPipe);
			return 5;
		}
		wcscpy_s(buf + wcslen(buf), _countof(buf)-wcslen(buf), buf2 );

		strResponse.clear();
		dwError = getLastXmlDoc( buf, strResponse );
		if(dwError){
			WriteLog(_T("Response string is not a valid xml document. length=%d"), dwRead );
			//pDlg->PostMessage( WM_APM_STATUS_CHANGED, IDS_ERROR_BACKEND, APM_STATUS_ERROR );
			//CloseHandle(hPipe);
			return 6;
		}

		memset( &response, 0, sizeof(response) );
		dwError = ProcessResponse( strResponse, response );
		if( dwError == 0 && 
			CompareStringIgnoreCase( response.m_strType, APM_REQUEST_TYPE_UICOMMAND ) == 0 && 
			CompareStringIgnoreCase( response.m_strId, strRequestId ) == 0 )
			break;
	}
	if( !response.m_strError.empty() )
	{
		WriteLog(_T("Fail to download D2D patch , %s"), response.m_strError.c_str());
		//CloseHandle(hPipe);
		return 7;
	}
	//CloseHandle(hPipe);
	return ERROR_SUCCESS;
}

DWORD CD2DComponent::PatchInstall()
{
	return ERROR_SUCCESS;
}

DWORD CD2DComponent::HandleWindowsService()
{
	bServerRuningBeforeUpdate =	IsServiceStart(SERVICE_AGENT);
	if( bServerRuningBeforeUpdate ){
		ChangeSvcStartType(SERVICE_ARCAPP, SERVICE_AUTO_START);
		BOOL dwError = StopSpecService(SERVICE_AGENT, TRUE, TRUE);
		if( !dwError){
			WriteLog(_T("Fail to stop agent service before update, ec=%d"), dwError);
			//pDlg->PostMessage( WM_APM_STATUS_CHANGED, IDS_FAIL_STOP_EDGE, APM_STATUS_ERROR );
			return dwError;
		}
		WriteLog(_T("stop agent service before update"));
	}
	else
	{
		WriteLog(_T("%s is not running before update."), SERVICE_AGENT);
	}
	return ERROR_SUCCESS;
}
void CD2DComponent::DoExitThings()
{
	//terminate D2DUpdateManager.exe process
	wstring strRequestId, request;
	strRequestId = generateRequestId();
	DWORD dwError = createTerminateRequest(strRequestId, request, APM_D2D, m_nMinor, m_nBuild);
	if( dwError ){
		WriteLog(_T("fail to create Terminate request xml, ec=%d"), dwError);
	}
	DWORD dwWrite;
	BOOL bResult = WriteFile(hPipe, request.c_str(), request.length()*sizeof(WCHAR), &dwWrite, NULL);
	if(!bResult){
		dwError = GetLastError();
		WriteLog(_T("fail to write Terminate to named pipe, %d"), dwError);
	}
	if(bServerRuningBeforeUpdate)
	{
		WriteLog(_T("start d2d service"));
		StartSpecService(SERVICE_AGENT, TRUE);
	}
}