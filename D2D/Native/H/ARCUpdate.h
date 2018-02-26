#pragma once
#include <Windows.h>
#include "brandname.h"
#include <string>

#define MUTEX_NAME_MAIL_ALERT			L"Global\\MAIL_ALERT_E5C78840-AE87-4B9D-BF73-ADE57D011F0B"

//
// following macroes are used in web service.
//
#define UPDATE_TYPE_AGENT			0
#define UPDATE_TYPE_CONSOLE			1
#define UPDATE_TYPE_GATEWAY			2

//
// This is to define the updates folder strcuture on ca server or staging server
//
#define ARCUPDATE_SITE_ROOT				L"/UDPUpdates"
#define ARCUPDATE_SITE_FULL_UPDATES		L"FullUpdates"
#define ARCUPDATE_SITE_ENGINE_UPDATES	L"EngineUpdates"
#define ARCUPDATE_SITE_GATEWAY_UPDATES	L"GatewayUpdates"
#define ARCUPDATE_SITE_SELF_UPDATES		L"SelfUpdates"
#define ARCUPDATE_SITE_PATCH_UPDATES	L"UDPPatches"
#define ARCUPDATE_SITE_RELEASE_VERSION	L"r6.0"

//
// Define which product to update
// It is the product code defined in "UpdateURL.xml" and "AvailableUpdateInfo.xml"
//
enum ARCUPDATE_PRODUCT
{
	ARCUPDATE_PRODUCT_AUTOSELECT	= 0,	 // automatically select product to update
	ARCUPDATE_PRODUCT_AGENT			= 1000,  // Agent Update
	ARCUPDATE_PRODUCT_FULL			= 1001,  // FULL update
	ARCUPDATE_PRODUCT_SELFUPDATE	= 1002,  // self update
	ARCUPDATE_PRODUCT_PATCHUPDATE	= 1003,  // patch update
	ARCUPDATE_PRODUCT_GATEWAY		= 1004,  // gateway
};

//
// define the check update phase
// the phase will returned to caller through interface IUpdateSync
//
enum ARCUPDATE_JOB_PHASE
{
	AJP_UNKNOWN		= 0,			// Job phase - unknown
	AJP_INITIALIZE	= 1,			// job phase - initializing
	AJP_CONNECTING	= 2,			// job phase - test connection
	AJP_DOWNLOADING = 3,			// job phase - downloading
	AJP_END			= 4,			// job phase - end
};

enum ARCUPDATE_JOB_STATUS
{
	AJS_UNKNOWN		= 0,		// job status - unknown
	AJS_RUNNING		= 1,		// job status - running
	AJS_FAILED		= 2,		// job status - failed
	AJS_CANCELED	= 3,		// job status - canceled
	AJS_COMPLETED	= 4,		// job status - completed
};

//
// define the http server type : Arcserve Default Server or Stage Server
//
enum ARCUPDATE_SERVER_TYPE
{
	ARCUPDATE_SERVER_DEFAULT = 0,  // Default Server
	ARCUPDATE_SERVER_STAGE = 1,  // Stage Server     
};

//
// defination of update job monitor
//
#define BASENAME_UPDATE_JOB_MONITOR    L"D12FC97E-5911-4DB5-8A91-4165F594DD39"
typedef struct _UPDATE_JOB_MONITOR_
{
	DWORD		dwProcessID;			// the update job process ID
	DWORD		dwJobStatus;			// the job status - running, finished, failed ....
	DWORD		dwJobPhase;				// the job phase - test conntion, downloading
	DWORD		dwCancelFlag;			// the flag used to cancel a job
	LONG		lLastError;				// the last error of the job
	ULONGLONG   ullStartTime;			// the utc time of job start time
	ULONGLONG	ullEndTime;				// the utc time of job end time
	ULONGLONG	ullTotalSize;			// the total size to download
	ULONGLONG	ullDownloadedSize;		// the downloaded size
}UPDATE_JOB_MONITOR, *PUPDATE_JOB_MONITOR;

//
// the proxy info used to download update
//
#define ARCUPDATE_SERVER_NAME_LENGTH		256
#define ARCUPDATE_PROXY_USER_LENGTH			256
#define ARCUPDATE_PROXY_PASSWORD_LENGTH		256
#define ARCUPDATE_PATH_LENGTH				256
typedef struct _ARCUPDATE_SERVER_INFO_
{
	ARCUPDATE_SERVER_TYPE   serverType;			// servert type: default server or staging server	

	// for staging server, below is the staging server name and port
	// for arcserve server, below is the proxy server name and port
	int						nServerPort;
	WCHAR					downloadServer[ARCUPDATE_SERVER_NAME_LENGTH];
	//WCHAR					downloadPath[ARCUPDATE_PATH_LENGTH]; //added by cliicy.luo
	// for staging server, below is ignored.
	// for arcserver server, below is the proxy username and password.
	BOOL					bDefaultIEProxy;	// default proxy, same as IExplorer
	WCHAR					proxyUserName[ARCUPDATE_PROXY_USER_LENGTH];
	WCHAR					proxyPassword[ARCUPDATE_PROXY_PASSWORD_LENGTH];
	WCHAR					proxyServerName[ARCUPDATE_SERVER_NAME_LENGTH];
	int						proxyServerPort;
	_ARCUPDATE_SERVER_INFO_()
	{
		serverType = ARCUPDATE_SERVER_DEFAULT;
		bDefaultIEProxy = TRUE;
		nServerPort = 80;
		proxyServerPort = 80;
		ZeroMemory(downloadServer, sizeof(downloadServer));
		ZeroMemory(proxyUserName, sizeof(proxyUserName));
		ZeroMemory(proxyPassword, sizeof(proxyPassword));
		ZeroMemory(proxyServerName, sizeof(proxyServerName));
	}

	_ARCUPDATE_SERVER_INFO_(const _ARCUPDATE_SERVER_INFO_& other)
	{
		serverType = other.serverType;
		bDefaultIEProxy = other.bDefaultIEProxy;
		nServerPort = other.nServerPort;
		proxyServerPort = other.proxyServerPort;
		wcscpy_s(downloadServer, _countof(downloadServer), other.downloadServer);
		wcscpy_s(proxyUserName, _countof(proxyUserName), other.proxyUserName);
		wcscpy_s(proxyPassword, _countof(proxyPassword), other.proxyPassword);
		wcscpy_s(proxyServerName, _countof(proxyServerName), other.proxyServerName);
	}
	_ARCUPDATE_SERVER_INFO_& operator=(const _ARCUPDATE_SERVER_INFO_& other)
	{
		serverType = other.serverType;
		bDefaultIEProxy = other.bDefaultIEProxy;
		nServerPort = other.nServerPort;
		proxyServerPort = other.proxyServerPort;
		wcscpy_s(downloadServer, _countof(downloadServer), other.downloadServer);
		wcscpy_s(proxyUserName, _countof(proxyUserName), other.proxyUserName);
		wcscpy_s(proxyPassword, _countof(proxyPassword), other.proxyPassword);
		wcscpy_s(proxyServerName, _countof(proxyServerName), other.proxyServerName);
		return (*this);
	}
}ARCUPDATE_SERVER_INFO, *PARCUPDATE_SERVER_INFO;

//
// check update from web service or setup
//
DWORD WINAPI CheckUpdate(DWORD dwProdOn, DWORD dwProdFor, ARCUPDATE_SERVER_INFO* pSvrInfo, BOOL bSync = TRUE);
typedef DWORD(WINAPI *FUNC_CheckUpdate) (DWORD, DWORD, ARCUPDATE_SERVER_INFO*, BOOL);

//
// save the auto update server information
//
DWORD WINAPI SaveUpdateServerInfo(DWORD dwProd, ARCUPDATE_SERVER_INFO* pSvrInfo);
typedef DWORD(WINAPI *FUNC_SaveUpdateServerInfo) (DWORD, ARCUPDATE_SERVER_INFO* );

//
// test connection from web service or setup
//
DWORD WINAPI TestConnection(DWORD dwProd, ARCUPDATE_SERVER_INFO* pSvrInfo);
typedef DWORD(WINAPI *FUNC_TestConnection) (DWORD, ARCUPDATE_SERVER_INFO*);

//added by cliicy.luo
// test connection from Binary web service or setup added by cliicy.luo
//
DWORD WINAPI TestBIConnection(DWORD dwProd, ARCUPDATE_SERVER_INFO* pSvrInfo);
typedef DWORD(WINAPI *FUNC_TestBIConnection) (DWORD, ARCUPDATE_SERVER_INFO*);

//
// install update from web service
//
DWORD WINAPI InstallBIUpdate(DWORD dwProduct);
typedef DWORD(WINAPI* FUNC_InstallBIUpdate) (DWORD);

//
// check update from web service or setup
//
DWORD WINAPI CheckBIUpdate(DWORD dwProdOn, DWORD dwProdFor, ARCUPDATE_SERVER_INFO* pSvrInfo, BOOL bSync = TRUE);
typedef DWORD(WINAPI *FUNC_CheckBIUpdate) (DWORD, DWORD, ARCUPDATE_SERVER_INFO*, BOOL);

//added by cliicy.luo

//
// detect if update service is running
//
BOOL WINAPI IsUpdateServiceRunning();
typedef BOOL(WINAPI* FUNC_IsUpdateServiceRunning) ();

//
// detect if update is busy
//
DWORD WINAPI IsUpdateBusy(DWORD dwProduct, PBOOL pbBusy);
typedef DWORD(WINAPI* FUNC_IsUpdateBusy) (DWORD, PBOOL);

//
// cancel update job
//
DWORD WINAPI AUCancelUpdateJob(DWORD dwProduct);
typedef DWORD(WINAPI* FUNC_AUCancelUpdateJob)(DWORD);

//
// query update job status
//
DWORD WINAPI QueryUpdateStatus(DWORD dwProduct, UPDATE_JOB_MONITOR* pStatus);
typedef DWORD(WINAPI* FUNC_QueryUpdateStatus)(DWORD, UPDATE_JOB_MONITOR*);

//
// get the update status.xml
//
std::wstring WINAPI GetUpdateStatusXmlFile(DWORD dwProduct);
typedef std::wstring(WINAPI* FUNC_GetUpdateStatusXmlFile) (DWORD);

//
// get the update status.xml added by cliicy.luo
//
std::wstring WINAPI GetBIUpdateStatusXmlFile(DWORD dwProduct);
typedef std::wstring(WINAPI* FUNC_GetBIUpdateStatusXmlFile) (DWORD);

//
// get the update settings file
//
std::wstring WINAPI GetUpdateSettingsFile(DWORD dwProduct);
typedef std::wstring(WINAPI* FUNC_GetUpdateSettingsFile) (DWORD);

//
// install update from web service
//
DWORD WINAPI InstallUpdate(DWORD dwProduct);
typedef DWORD(WINAPI* FUNC_InstallUpdate) (DWORD);

//
// get the update error message
//
DWORD WINAPI GetUpdateErrorMessage(DWORD dwProduct, DWORD dwErrorCode, std::wstring& strErrMsg);
typedef DWORD(WINAPI* FUNC_GetUpdateErrorMessage) (DWORD, DWORD, std::wstring&);

//
// disable auto update from custom tool
//
DWORD WINAPI DisableAutoUpdate(DWORD dwProduct, BOOL bDisable);
typedef DWORD(WINAPI* FUNC_DisableAutoUpdate) (DWORD, BOOL);

//
// get the last available update of given product
// 
DWORD WINAPI GetLastAvailableUpdate(DWORD dwProduct, WCHAR* pszVersion, PDWORD pdwSizeOfVersion, WCHAR* pszFilePath, PDWORD pdwSizeOfFilePath, PBOOL pbRebootRequired);
typedef DWORD(WINAPI* FUNC_GetLastAvailableUpdate)(DWORD, WCHAR*, PDWORD, WCHAR*, PDWORD, PBOOL);


//
// error defines...
//
#define ARCUPDATE_SUCCEED								0
#define ARCUPDATE_ERROR_UP_TO_DATE						1  // product is already up to date
#define ARCUPDATE_ERROR_NO_UPDATE_FOUND					2  // no update(s) found
#define ARCUPDATE_ERROR_SERVER_UNAVAILABLE				3  // update server is unavailable
#define ARCUPDATE_ERROR_FAILED_TO_DOWNLOAD				4  // failed to download update
#define ARCUPDATE_ERROR_CANCELED						5  // canceled by user
#define ARCUPDATE_ERROR_ANOTHER_IS_RUNNING				6  // another update process is running
#define ARCUPDATE_ERROR_FAILED_INSTALL_UPDATE			7  // failed to install update
#define ARCUPDATE_ERROR_UPDATE_SERVICE_NOT_STARTED		8  // the udp update service is not running
#define ARCUPDATE_ERROR_SELF_UPDATE_REQUIRED			9  // the auto update manager self update is required


//
// followings is a wrapper class of ARCUpdate.dll
//
template<class _TFunc>
_TFunc FuncOfDll(HMODULE hDll, const char* funcName){
	return (_TFunc)::GetProcAddress(hDll, funcName);
}

class _CARCUpdateDLL
{
public:
	_CARCUpdateDLL() : m_hDll(NULL){	
	};

	~_CARCUpdateDLL(){ 
		if (m_hDll){ 
			FreeLibrary(m_hDll); 
			m_hDll = NULL; 
		} 
	}
	HMODULE Dll(){
		return m_hDll;	
	}
	DWORD   Load(LPCWSTR lpszPath){
		
		if (m_hDll)
		{
			FreeLibrary(m_hDll); 
			m_hDll = NULL;
		}
		
		if (lpszPath && !(*lpszPath))
		{
			m_hDll = LoadLibrary(lpszPath);
			if (m_hDll)
				return 0;
			return GetLastError();
		}
		else
		{
			typedef BOOL(WINAPI *LPFN_ISWOW64PROCESS) (HANDLE, PBOOL);
			LPFN_ISWOW64PROCESS fnIsWow64Process = FuncOfDll<LPFN_ISWOW64PROCESS>(GetModuleHandleW(L"kernel32"), "IsWow64Process");
			BOOL bIsWow64 = FALSE;
			if (fnIsWow64Process)
				fnIsWow64Process(GetCurrentProcess(), &bIsWow64);
			
			REGSAM sam = KEY_READ;
			if (bIsWow64)
				sam |= KEY_WOW64_64KEY;
			WCHAR szFilePath[1024] = { 0 };
			HKEY hKey = NULL;
			LONG lRet = ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, CST_BRAND_REG_ROOT_L, 0, sam, &hKey);
			if (lRet != 0)
				return lRet;
			DWORD dwSize = sizeof(szFilePath);
			DWORD dwType = REG_SZ;
			lRet = ::RegQueryValueEx(hKey, L"RootPath", 0, &dwType,  (LPBYTE)szFilePath, &dwSize);
			::RegCloseKey(hKey);
			if (lRet != 0)
				return lRet;
			
			if (wcslen(szFilePath) == 0)
				return ERROR_INVALID_PARAMETER;

			if (szFilePath[wcslen(szFilePath) - 1] != L'\\')
				wcscat_s(szFilePath, _countof(szFilePath), L"\\");
			
			if (bIsWow64)
				wcscat_s(szFilePath, _countof(szFilePath), L"Update Manager\\x86\\ARCUpdate.dll");
			else
				wcscat_s(szFilePath, _countof(szFilePath), L"Update Manager\\ARCUpdate.dll");

			m_hDll = LoadLibrary(szFilePath);
			if (m_hDll)
				return 0;
			return GetLastError();
		}
	}
protected:
	HMODULE m_hDll;
};

