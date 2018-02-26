// NativeFacade.h : main header file for the NativeFacade DLL
//

#pragma once
#define D2D2VMDK_SUCCESS	0
#define D2D2VMDK_FAIL	1

#include <string>
#include <tchar.h>
#include <windows.h>
#include <vector>
#include "afjob.h"
#include "AFFileStoreGlobals.h"
#include "afdefine.h"
#include "affilecatalog.h"
#include "..\MyUpdateDNSTool\UpdateDNSTool.h" //<sonmi01>2012-9-20 ###???
using namespace std;
typedef struct LastModifiedDateTime
{
	wstring wsLastModifiedDate;
	wstring wsLastModifiedTime;
}_LAST_MODIFIED_DATE_TIME;
typedef struct DownloadRequestStatus
{
	DWORD dwLastErrorCode;
	wstring wsLastErrorsMessage;
	DWORD dwActivityLogMessageType;
	BOOL bIsreqActivityLogMessage;
	wstring wsFinalStatus;
	_LAST_MODIFIED_DATE_TIME stLastModifiedDateTime;
}_DOWNLOAD_REQUEST_STATUS_INFO;

typedef INT __declspec(dllimport) (*pfntestDownloadServerStatus)(_DOWNLOAD_REQUEST_STATUS_INFO &out_stDownloadRequestStatusInfo,TCHAR *in_wsResourceIdentifierURI,
		UINT in_uiServerType,TCHAR *in_wsServerName,TCHAR *in_wsServerPort,
		TCHAR *in_wsProxyIP,TCHAR *in_wsProxyPort ,TCHAR *in_wsProxyUserName,TCHAR *in_wsProxyPassword);

struct Adapter_Map_InfoW
{
	bool						replicate_mac;
	std::wstring				device_id;
	std::wstring				mac_address;

	bool						ip_use_dhcp; //<sonmi01>2012-10-19 ###???
	bool						dns_use_dhcp;
	bool						use_original_setting;
	std::wstring				wins_p;
	std::wstring				wins_s;
	std::vector<std::wstring>	ips;
	std::vector<std::wstring>	subnets;
	std::vector<std::wstring>	gateways;
	std::vector<std::wstring>	dns;
};

typedef struct _HyperVInfo
{
	std::wstring				vmUUID;
	std::wstring				vmName;
	std::wstring				vmHostName;
	std::wstring				vmOSName;
}HYPERVINFO;

enum enumGetHypervInfoErrCode
{
	GET_HYPERV_SUCCESS = 0,
	GET_HYPERV_ACCESS_DENIED = 1,
	GET_HYPERV_NOT_INSTALL_HYPERV_ROLE = 2,
	GET_HYPERV_SERVER_UNAVAILABLE = 3,
	GET_HYPERV_OTHER_ERROR = 4
};

#define WIN_SERVER_UNAVAILABLE   0x800706ba

//
//  Helper class to load function pointer for DLL dynamically
//
class FuncLoader
{
public:
	FuncLoader(CDbgLog* pDbgLog = NULL)
	{
		m_pDbgLog = pDbgLog;
	}
	~FuncLoader()
	{
		for (size_t idx = 0; idx < m_hModules.size(); idx++)
		{
			FreeLibrary(m_hModules[idx]);
			m_hModules[idx] = NULL;
		}
		m_hModules.clear();
	}

private:
	CDbgLog*				m_pDbgLog;
	std::vector<HMODULE>	m_hModules;

	HMODULE LoadLib(const WCHAR* pLibraryName)
	{
		HMODULE hLib = LoadLibraryW(pLibraryName);
		if (hLib == NULL)
		{
			DWORD dwRet = GetLastError();
			if (m_pDbgLog != NULL)
				m_pDbgLog->LogW(LL_ERR, dwRet, __FUNCTIONW__ L": Failed to load library [%s].", pLibraryName);
		}
		else
		{
			m_hModules.push_back(hLib);
		}

		return hLib;
	}

public:
	FARPROC GetFuncPtr(const WCHAR* pLibraryName, const char* pFuncName)
	{
		DWORD dwRet = 0;
		FARPROC pFunPtr = NULL;

		if (pLibraryName == NULL || pFuncName == NULL)
		{
			dwRet = ERROR_INVALID_PARAMETER;
			if (m_pDbgLog)
				m_pDbgLog->LogW(LL_ERR, dwRet, __FUNCTIONW__ L": Invalid parameter [%s :: %S].", pLibraryName, pFuncName);

			return pFunPtr;
		}

		HMODULE h = LoadLib(pLibraryName);
		if (h)
		{
			pFunPtr = GetProcAddress(h, pFuncName);
			if (pFunPtr == NULL)
			{
				dwRet = GetLastError();
				if (m_pDbgLog)
					m_pDbgLog->LogW(LL_ERR, dwRet, __FUNCTIONW__ L": Failed to get function [%S] pointer.", pFuncName);
			}
		}

		return pFunPtr;
	}
};



/* !!! To Delete
extern "C" 
{
typedef BOOL (*IsPatchManagerRunning)(PWCHAR runningMutexName);
typedef BOOL (*IsPatchManagerBusy)(PWCHAR busyMutexName);

typedef HANDLE (*PMCreateFile)(PWCHAR fileName, int desiredAccess, int sharedMode,
			   int creationDisposition, int flags, int templateFile);
typedef int (*PMReadFile)(HANDLE hPipeHandle, PWCHAR buffer, int bufferSizeInWords);
typedef int (*PMWriteFile)(HANDLE hPipeHandle, PWCHAR pCheckUpdatesReq, int sizeInWords);
typedef BOOL (*PMCloseFile)(HANDLE hPipeHandle);

typedef int (*TestDownloadServerConnection)(_DOWNLOAD_REQUEST_STATUS_INFO &out_stDownloadRequestStatusInfo,
		WCHAR *in_wsResourceIdentifierURI, UINT in_uiServerType, WCHAR *in_wsServerName, WCHAR *in_wsServerPort,
		WCHAR *in_wsProxyIP, WCHAR *in_wsProxyPort ,WCHAR *in_wsProxyUserName, WCHAR *in_wsProxyPassword);
}
*/


/* END: ARCHIVE */