#include "stdafx.h"
#include "AFCoreAPIInterface.h"
#include "UpdateManager.h"

#define REG_K_ROOTPATH			L"RootPath"

static void path_ensure_end_with_slash( std::wstring& strPath )
{
	wstring::reverse_iterator it = strPath.rbegin() ;
	if(it != strPath.rend() && *it != '\\')      
		strPath += '\\'; 
}

static void path_ensure_end_without_slash( std::wstring& strPath )
{
	wstring::reverse_iterator it = strPath.rbegin() ;
	if(it != strPath.rend() && *it == '\\')       
		strPath.erase (--strPath.end()); 
}

static bool is_folder_exist( const std::wstring& strFolder )
{
	DWORD dwAttrs = ::GetFileAttributes( strFolder.c_str() );
	return (dwAttrs!=INVALID_FILE_ATTRIBUTES) &&( (dwAttrs&FILE_ATTRIBUTE_DIRECTORY)!=0 );
}

static bool is_file_exist( const std::wstring& strFile )
{
	WIN32_FIND_DATA wfd;
	HANDLE hFile = ::FindFirstFile( strFile.c_str(), &wfd );
	if( hFile==INVALID_HANDLE_VALUE )
		return false;
	::FindClose( hFile );
	return true;
}

static std::wstring path_join( const std::wstring& parent, const std::wstring& sub )
{
	if( sub.empty() ) return parent;

	std::wstring sRes = parent;
	if( sub.at(0)==L'\\' )
		path_ensure_end_without_slash( sRes );
	else
		path_ensure_end_with_slash( sRes );
	return sRes.append(sub);
}

static std::wstring get_product_home( DWORD dwProduct )
{
	WCHAR szPath[1024] = {0};
	GetInstallPathByProduct( dwProduct, szPath, _ARRAYSIZE(szPath) );
	return wstring(szPath);
}

static std::wstring get_update_manager_home()
{
	CRegistry reg;
	LONG lRet = reg.Open(CST_BRAND_REG_ROOT);
	if (lRet != 0)
		return L"";

	wstring strPath = L"";
	reg.QueryStringValue(REG_K_ROOTPATH, strPath);
	if (strPath.empty())
	{
		reg.Close();
		return L"";
	}

	reg.Close();

	strPath = path_join(strPath, L"Update Manager\\");
	return strPath;
}

static bool isGatewayInstalled()
{
	WCHAR szRegHome[MAX_PATH] = { 0 };
	GetRegRootPathByProduct(PRODUCT_CENTRAL, szRegHome, _ARRAYSIZE(szRegHome));
	if (wcslen(szRegHome) == 0)
	{
		return FALSE;
	}

	CSysInfo sys;
	REGSAM sam = KEY_READ;
	if (sys.IsWow64())
		sam |= KEY_WOW64_64KEY;
	CRegistry reg;
	if (0 != reg.Open(szRegHome, HKEY_LOCAL_MACHINE, sam))
	{
		return false;
	}

	wstring strValue = L"";
	reg.QueryStringValue(L"GatewayFlag", strValue);
	if (_wcsicmp(strValue.c_str(), L"1")==0)
		return true;
	return false;
}

static DWORD getProductCode(DWORD dwType)
{
	DWORD dwProd = ARCUPDATE_PRODUCT_AGENT;
	if (dwType == UPDATE_TYPE_CONSOLE)
	{
		if (isGatewayInstalled())
			dwProd = ARCUPDATE_PRODUCT_GATEWAY;
		else
			dwProd = ARCUPDATE_PRODUCT_FULL;
	}
	else if (dwType == UPDATE_TYPE_GATEWAY)
		dwProd = ARCUPDATE_PRODUCT_GATEWAY;

	return dwProd;
}

DWORD DeleteUpdateAccount( )
{
	CDbgLog logObj;
	wstring strFilePath = path_join( get_update_manager_home(), L"\\Config\\Account.sig");
	logObj.LogW(LL_INF, 0, L"%s: AUTOUPDATE - Delete File [%s]", __WFUNCTION__, strFilePath.c_str());
	::DeleteFile( strFilePath.c_str() );
	return 0;
}

/////////////////////////////////////////////////////////////////////////////////////
DWORD WINAPI AFTestBIUpdateServerConnection(  DWORD dwType, DWORD dwServerType, const wstring& strServer, int nPort, 
											  const wstring& strProxyServer, int nProxyServerPort, 
											  const wstring& strProxyUser, const wstring& strProxyPassword )
{
	CDbgLog logObj;
	DWORD dwRet = 0;
	logObj.LogW(LL_INF, dwRet, L"%s:wwww enter AFTestBIUpdateServerConnection ", __WFUNCTION__);

	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);	
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE -  Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	ARCUPDATE_SERVER_INFO server;
	if (dwServerType == ARCUPDATE_SERVER_DEFAULT)
	{
		server.serverType = ARCUPDATE_SERVER_DEFAULT;
		if (strProxyServer.empty())
		{
			server.bDefaultIEProxy = TRUE;
		}
		else
		{
			wcsncpy_s(server.proxyServerName, _countof(server.proxyServerName), strProxyServer.c_str(), _TRUNCATE);
			wcsncpy_s(server.proxyUserName, _countof(server.proxyUserName), strProxyUser.c_str(), _TRUNCATE);
			wcsncpy_s(server.proxyPassword, _countof(server.proxyPassword), strProxyPassword.c_str(), _TRUNCATE);
			server.proxyServerPort = nProxyServerPort;
		}
	}
	else if (dwServerType == ARCUPDATE_SERVER_STAGE)
	{
		server.serverType = ARCUPDATE_SERVER_STAGE;
		wcsncpy_s(server.downloadServer, _countof(server.downloadServer), strServer.c_str(), _TRUNCATE);
		server.nServerPort = nPort;
	}
	else
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Invalid server type %d", __WFUNCTION__, dwServerType);
		return 87;
	}

	FUNC_TestConnection func = FuncOfDll<FUNC_TestBIConnection>(dll.Dll(), "TestBIConnection");
	logObj.LogW(LL_INF, dwRet, L"%s:wwww will call TestBIConnection", __WFUNCTION__);
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of TestBIConnection", __WFUNCTION__ );
		return 87;
	}
	
	return func(dwProd, &server);
}

DWORD WINAPI AFInstallBIUpdate(DWORD dwType)
{
	CDbgLog	logObj;
	logObj.LogW(LL_INF, 0, L"%s: Start to install update of product %d", __WFUNCTION__, dwType);

	DWORD dwRet = 0;

	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_InstallBIUpdate func = FuncOfDll<FUNC_InstallBIUpdate>(dll.Dll(), "InstallBIUpdate");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of InstallBIUpdate", __WFUNCTION__);
		return 87;
	}

	return func(dwProd);
}

DWORD WINAPI AFGetBIUpdateStatusFile(DWORD dwType, wstring& strFile)
{
	
	CDbgLog logObj;
	DWORD dwRet = 0;
	logObj.LogW(LL_INF, 0, L"%s: enter BI StatusFile", __WFUNCTION__);
	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Binary Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Binary Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_GetBIUpdateStatusXmlFile func = FuncOfDll<FUNC_GetBIUpdateStatusXmlFile>(dll.Dll(), "GetBIUpdateStatusXmlFile");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Binary Failed to get proc address of CheckUpdate", __WFUNCTION__);
		return 87;
	}

	strFile = func(dwProd);
	logObj.LogW(LL_INF, 0, L"%s: GetUpdateStatusXmlFile=%s", __WFUNCTION__, strFile.c_str());
	return 0;
}


DWORD WINAPI AFCheckBIUpdate(DWORD dwType)
{
	CDbgLog logObj;
	DWORD dwRet = 0;

	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_CheckBIUpdate func = FuncOfDll<FUNC_CheckBIUpdate>(dll.Dll(), "CheckBIUpdate");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: oooohh AUTOUPDATE - Failed to get proc address of CheckBIUpdate", __WFUNCTION__);
		return 87;
	}

	return func(dwProd, 0, NULL, TRUE);
}

//added by cliicy.luo 

/////////////////////////////////////////////////////////////////////////////////////
DWORD WINAPI AFTestUpdateServerConnection(DWORD dwType, DWORD dwServerType, const wstring& strServer, int nPort,
	const wstring& strProxyServer, int nProxyServerPort,
	const wstring& strProxyUser, const wstring& strProxyPassword)
{
	CDbgLog logObj;
	DWORD dwRet = 0;
	logObj.LogW(LL_INF, dwRet, L"%s: enter AFTestUpdateServerConnection ", __WFUNCTION__);
	DWORD dwProd = getProductCode(dwType);

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE -  Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	ARCUPDATE_SERVER_INFO server;
	if (dwServerType == ARCUPDATE_SERVER_DEFAULT)
	{
		server.serverType = ARCUPDATE_SERVER_DEFAULT;
		if (strProxyServer.empty())
		{
			server.bDefaultIEProxy = TRUE;
		}
		else
		{
			wcsncpy_s(server.proxyServerName, _countof(server.proxyServerName), strProxyServer.c_str(), _TRUNCATE);
			wcsncpy_s(server.proxyUserName, _countof(server.proxyUserName), strProxyUser.c_str(), _TRUNCATE);
			wcsncpy_s(server.proxyPassword, _countof(server.proxyPassword), strProxyPassword.c_str(), _TRUNCATE);
			server.proxyServerPort = nProxyServerPort;
			server.bDefaultIEProxy = FALSE;
		}
	}
	else if (dwServerType == ARCUPDATE_SERVER_STAGE)
	{
		server.serverType = ARCUPDATE_SERVER_STAGE;
		wcsncpy_s(server.downloadServer, _countof(server.downloadServer), strServer.c_str(), _TRUNCATE);
		server.nServerPort = nPort;
	}
	else
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Invalid server type %d", __WFUNCTION__, dwServerType);
		return 87;
	}

	FUNC_TestConnection func = FuncOfDll<FUNC_TestConnection>(dll.Dll(), "TestConnection");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of TestConnection", __WFUNCTION__);
		return 87;
	}

	return func(dwProd, &server);
}

BOOL WINAPI AFIsUpdateServiceRunning()
{
	CDbgLog logObj;
	DWORD dwRet = 0;
	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return FALSE;
	}

	FUNC_IsUpdateServiceRunning func = FuncOfDll<FUNC_IsUpdateServiceRunning>(dll.Dll(), "IsUpdateServiceRunning");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of IsUpdateServiceRunning", __WFUNCTION__);
		return FALSE;
	}

	return func();
}

BOOL WINAPI AFIsUpdateBusy( DWORD dwType )
{
	CDbgLog logObj;
	DWORD dwRet = 0;
	
	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return FALSE;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_IsUpdateBusy func = FuncOfDll<FUNC_IsUpdateBusy>(dll.Dll(), "IsUpdateBusy");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of IsUpdateBusy", __WFUNCTION__);
		return TRUE;
	}

	BOOL bBusy = TRUE;
	func(dwProd, &bBusy);
	return bBusy;
}

DWORD WINAPI AFCheckUpdate( DWORD dwType )
{
	CDbgLog logObj;
	DWORD dwRet = 0;

	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_CheckUpdate func = FuncOfDll<FUNC_CheckUpdate>(dll.Dll(), "CheckUpdate");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of CheckUpdate", __WFUNCTION__);
		return 87;
	}

	return func(dwProd, 0, NULL, TRUE);
}

DWORD WINAPI AFGetUpdateStatusFile( DWORD dwType,  wstring& strFile)
{
	CDbgLog logObj;
	DWORD dwRet = 0;
	logObj.LogW(LL_INF, 0, L"%s: enter oo StatusFile", __WFUNCTION__);
	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_GetUpdateStatusXmlFile func = FuncOfDll<FUNC_GetUpdateStatusXmlFile>(dll.Dll(), "GetUpdateStatusXmlFile");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of CheckUpdate", __WFUNCTION__);
		return 87;
	}

	strFile = func(dwProd);
	logObj.LogW(LL_INF, 0, L"%s: GetUpdateStatusXmlFile=%s", __WFUNCTION__, strFile.c_str());
	return 0;
}

DWORD WINAPI AFGetUpdateSettingsFile( DWORD dwType, wstring& strFile)
{
	CDbgLog logObj;
	DWORD dwRet = 0;

	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_GetUpdateSettingsFile func = FuncOfDll<FUNC_GetUpdateSettingsFile>(dll.Dll(), "GetUpdateSettingsFile");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of CheckUpdate", __WFUNCTION__);
		return 87;
	}

	strFile = func(dwProd);
	return 0;
}

DWORD WINAPI AFInstallUpdate( DWORD dwType )
{
	CDbgLog	logObj;
	logObj.LogW(LL_INF, 0, L"%s: Start to install update of product %d", __WFUNCTION__, dwType);

	DWORD dwRet = 0;

	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_InstallUpdate func = FuncOfDll<FUNC_InstallUpdate>(dll.Dll(), "InstallUpdate");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of InstallUpdate", __WFUNCTION__);
		return 87;
	}

	return func(dwProd);
}

DWORD WINAPI AFGetAlertMailFiles( DWORD dwType, std::vector<wstring>& mailAlertFiles )
{
	CDbgLog		log;

	if (dwType == UPDATE_TYPE_GATEWAY){
		log.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	mailAlertFiles.clear();
	wstring strFolder = L"";
	if (dwType == UPDATE_TYPE_AGENT)
	{
		strFolder = get_product_home(PRODUCT_D2D);
	}
	else if (dwType == UPDATE_TYPE_CONSOLE)
	{
		if (isGatewayInstalled() )
			strFolder = get_product_home(PRODUCT_CENTRAL);
		else
			strFolder = get_product_home(PRODUCT_CENTRAL);
	}		
	else if (dwType == UPDATE_TYPE_GATEWAY)
	{
		strFolder = get_product_home(PRODUCT_CENTRAL);
	}

	if( strFolder.empty() )
	{
		log.LogW(LL_ERR, 0, L"%s: AUTOUPDATE - Failed to get home path of product %d", __WFUNCTION__, dwType );
		return 0;
	}
	strFolder = path_join( strFolder, L"Configuration\\MailAlert\\" );
	if( !is_folder_exist(strFolder) )
		return 0;

	wstring strReadyFolder = path_join( strFolder, L"ReadyForSend\\");
	if( !is_folder_exist(strReadyFolder) && !::CreateDirectory(strReadyFolder.c_str(), NULL) )
	{
		log.LogW(LL_ERR, 0, L"%s: AUTOUPDATE - Failed to create folder %s", __WFUNCTION__, strReadyFolder.c_str() );
		return 0;
	}
	//
	// create Mutex
	//
	SECURITY_DESCRIPTOR sd; 
	SECURITY_ATTRIBUTES sa; 
	InitializeSecurityDescriptor(&sd,SECURITY_DESCRIPTOR_REVISION); 
	SetSecurityDescriptorDacl(&sd,TRUE,(PACL)NULL,FALSE); //set all the user can access the object      
	sa.nLength=sizeof(SECURITY_ATTRIBUTES); 
	sa.bInheritHandle=FALSE; 
	sa.lpSecurityDescriptor=&sd;

	HANDLE hMutex = CreateMutex( &sa, FALSE, MUTEX_NAME_MAIL_ALERT );
	if( hMutex == NULL )
	{
		log.LogW( LL_ERR, GetLastError(), L"%s: AUTOUPDATE - Failed to create mutex with name [%s]", __WFUNCTION__, MUTEX_NAME_MAIL_ALERT );
		return 0;
	}

	if( WAIT_OBJECT_0 != WaitForSingleObject( hMutex, 3000 ) )
	{
		CloseHandle(hMutex);
		hMutex = NULL;
		return 0;
	}

	//
	// firstly to move files to "readyForSend"
	//
	WIN32_FIND_DATA wfd;
	ZeroMemory(&wfd, sizeof(wfd) );
	wstring strTemp = strFolder + L"*.xml";
	HANDLE hFind = ::FindFirstFile( strTemp.c_str(), &wfd );
	if(hFind!=INVALID_HANDLE_VALUE)
	{
		do
		{
			wstring strSrcFile = strFolder + wfd.cFileName;
			wstring strDstFile = strReadyFolder + wfd.cFileName;
			if(!::MoveFile(strSrcFile.c_str(), strDstFile.c_str() ) )
				log.LogW( LL_ERR, GetLastError(), L"%s: AUTOUPDATE - Failed to move file [%s] to [%s]", __WFUNCTION__, strSrcFile.c_str(), strDstFile.c_str() );
		} while(::FindNextFile(hFind,&wfd));

		::FindClose(hFind);
		hFind = INVALID_HANDLE_VALUE;
	}
	::ReleaseMutex( hMutex );
	::CloseHandle( hMutex );

	//
	// get all files under ..\Configuration\MailAlert\readyForSend
	//
	ZeroMemory(&wfd, sizeof(wfd) );
	strTemp = strReadyFolder + L"*.xml";
	hFind = ::FindFirstFile( strTemp.c_str(), &wfd );
	if(hFind!=INVALID_HANDLE_VALUE)
	{
		do
		{
			wstring strFile = strReadyFolder + wfd.cFileName;
			mailAlertFiles.push_back( strFile );
		} while(::FindNextFile(hFind,&wfd));
		::FindClose(hFind);
		hFind = INVALID_HANDLE_VALUE;
	}
	return 0;
}

DWORD WINAPI AFGetUpdateErrorMessage( DWORD dwType, DWORD dwErrCode, wstring& strErrorMessage )
{
	CDbgLog logObj;
	DWORD dwRet = 0;

	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_GetUpdateErrorMessage func = FuncOfDll<FUNC_GetUpdateErrorMessage>(dll.Dll(), "GetUpdateErrorMessage");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of GetUpdateErrorMessage", __WFUNCTION__);
		return 87;
	}

	return func(dwProd, dwErrCode, strErrorMessage);
}

DWORD WINAPI AFDisalbeAutoUpdate( DWORD dwType/*=0*/, BOOL bDisable/*=TRUE*/ )
{
	CDbgLog logObj;
	DWORD dwRet = 0;

	DWORD dwProd = getProductCode(dwType);
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_DisableAutoUpdate func = FuncOfDll<FUNC_DisableAutoUpdate>(dll.Dll(), "DisableAutoUpdate");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: AUTOUPDATE - Failed to get proc address of DisableAutoUpdate", __WFUNCTION__);
		return 87;
	}

	return func(dwProd, bDisable);
}