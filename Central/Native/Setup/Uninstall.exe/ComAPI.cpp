#include "stdafx.h"
#include "ComAPI.h"

#define MAX_BUF_SIZE          4096
#define MAX_MES_BUS			  2048
#define MAX_BUF_BUS			  2048

#define KERNEL32_DLL					_T("kernel32.dll")

typedef LONG(WINAPI  *PFRegDelEx) (__in HKEY hKey, __in LPCTSTR lpSubKey, __in REGSAM samDesired, __reserved DWORD Reserved);

/******************************start Common API *************************************/

/******************************start for service*************************************/


BOOL StartService(SC_HANDLE schService, BOOL bBlockOperation)
{
	SERVICE_STATUS ServiceStatus;
	DWORD dwLastCheckPoint;

	if (!::StartService(schService, 0, NULL))
	{
		if (GetLastError() == ERROR_SERVICE_ALREADY_RUNNING)
			return TRUE;

		return FALSE;
	}

	if (!bBlockOperation)
		return TRUE;

	ControlService(schService, SERVICE_CONTROL_INTERROGATE, &ServiceStatus);

	while (ServiceStatus.dwCurrentState == SERVICE_START_PENDING)
	{ //wait unitl service starts
		dwLastCheckPoint = ServiceStatus.dwCheckPoint;
		Sleep(ServiceStatus.dwWaitHint);

		if (!QueryServiceStatus(schService, &ServiceStatus))
			break;

		if (dwLastCheckPoint >= ServiceStatus.dwCheckPoint)
			break;
	}

	return ServiceStatus.dwCurrentState == SERVICE_RUNNING || ServiceStatus.dwCurrentState == SERVICE_START_PENDING;
}


BOOL StartSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation /*= TRUE*/)
{
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	BOOL bStartService = FALSE;

	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);

	if (schSCManager == NULL)
		return FALSE;

	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService != NULL)
	{
		bStartService = StartService(schService, bBlockOperation);

		CloseServiceHandle(schService);
	}

	CloseServiceHandle(schSCManager);

	return bStartService;
}

BOOL StopDependentServices(LPCTSTR lpszServiceName, BOOL bBlockOperation)
{
	//Checks all services' "DependOnService" value and if lpszServiceName is in the DependOnService value 
	//then stop that service.

	HKEY hKey;
	HKEY hDependentKey;
	TCHAR szDependentService[256];
	DWORD dwSize = sizeof(szDependentService);
	TCHAR szData[256];
	DWORD dwDataSize;
	DWORD dwIndex = 0;
	BOOL bStopDependentServices = TRUE;

	if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, _T("SYSTEM\\CurrentControlSet\\Services"), 0, KEY_ALL_ACCESS, &hKey) != ERROR_SUCCESS) 
	{
		return FALSE;   //unable to enumerate services.
	}

	dwSize = _countof(szDependentService);
	while (RegEnumKeyEx(hKey, dwIndex++, szDependentService, &dwSize, NULL, NULL, NULL, NULL) == ERROR_SUCCESS)
	{
		dwSize = _countof(szDependentService);

		if (RegOpenKeyEx(hKey, szDependentService, 0, KEY_ALL_ACCESS, &hDependentKey) != ERROR_SUCCESS)
			continue;

		dwDataSize = sizeof(szData);

		if (RegQueryValueEx(hDependentKey, _T("DependOnService"), NULL, NULL, (LPBYTE)szData, &dwDataSize) == ERROR_SUCCESS)
		{
			LPCTSTR lpszService = szData;
			while (lstrlen(lpszService) > 0)
			{
				if (!lstrcmp(lpszService, lpszServiceName))
				{  //found a dependency so stop it.
					if (ERROR_SUCCESS !=StopSpecService(szDependentService, bBlockOperation, TRUE))
					{
						bStopDependentServices = FALSE;
					}

					break;
				}

				lpszService += lstrlen(lpszService)+1;
			}
		}

		RegCloseKey(hDependentKey);
	}

	RegCloseKey(hKey);

	return bStopDependentServices;
}

DWORD StopService(SC_HANDLE schService, LPCTSTR lpszServiceName, BOOL bBlockOperation, BOOL bStopDependents)
{
	DWORD dwRet = ERROR_SUCCESS;
	SERVICE_STATUS ServiceStatus;
	//DWORD dwLastCheckPoint;
	DWORD 	dwMaxWaitTime;

	QueryServiceStatus(schService, &ServiceStatus);

	if (ServiceStatus.dwCurrentState == SERVICE_STOPPED || ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING)
	{
		return ERROR_SUCCESS;
	}

	if (!ControlService(schService, SERVICE_CONTROL_STOP, &ServiceStatus))
	{
		DWORD dwTemp =  GetLastError();
		if (dwTemp == ERROR_DEPENDENT_SERVICES_RUNNING && bStopDependents)
		{
			// Stop dependent services and try it again.
			StopDependentServices(lpszServiceName, bBlockOperation);

			if (!ControlService(schService, SERVICE_CONTROL_STOP, &ServiceStatus))
			{
				return GetLastError();
			}
		}
		else
		{
			return dwTemp;
		}
	}

	if (bBlockOperation)
	{ //Wait until service stops
		dwMaxWaitTime = 90; // Maximum wait time seconds

		// wait at least 1 second
		if (dwMaxWaitTime < 1)
			dwMaxWaitTime = 1;

		QueryServiceStatus(schService, &ServiceStatus);

		while (ServiceStatus.dwCurrentState != SERVICE_STOPPED)
		{
			//dwLastCheckPoint = ServiceStatus.dwCheckPoint;
			//avoid the service sleep too long, set min to 1500ms
			//Sleep(__min(ServiceStatus.dwWaitHint, 1500));

			//Sleep(ServiceStatus.dwWaitHint);
			Sleep(1000); // Loop 1 second at a time

			if (!QueryServiceStatus(schService, &ServiceStatus))
			{
				dwRet = GetLastError();
				break;
			}
			//if (dwLastCheckPoint >= ServiceStatus.dwCheckPoint)
			//   break;

			// Do not wait more than suggested
			if (dwMaxWaitTime <= 0)
			{
				dwRet = 100000;
				break;
			}

			dwMaxWaitTime--;
		}
	}


	//ISC 11/16/98: According to Chie's request to return TRUE only when STOPPED
	//return ServiceStatus.dwCurrentState == SERVICE_STOPPED || ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING;
	return ServiceStatus.dwCurrentState == SERVICE_STOPPED?ERROR_SUCCESS:dwRet;
}


DWORD StopSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation /* = TRUE*/, BOOL bStopDependents /*= FALSE*/)
{
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	DWORD dwRet = ERROR_SUCCESS;

	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_CONNECT);
	if (schSCManager == NULL)
	{
		return GetLastError();
	}

	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService == NULL)
	{
		dwRet = GetLastError();
		if (dwRet == ERROR_SERVICE_DOES_NOT_EXIST)
		{
			dwRet = ERROR_SUCCESS;
		}
	}
	else
	{
		dwRet = StopService(schService, lpszServiceName, bBlockOperation, bStopDependents);
		CloseServiceHandle(schService);
	}

	CloseServiceHandle(schSCManager);

	return dwRet;
}

BOOL IsServiceStopped(LPCTSTR lpszServiceName)
{
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	SERVICE_STATUS ServiceStatus;
	BOOL bIsStopped = TRUE;

	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);
	if (schSCManager != NULL)
	{
		schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

		if (schService != NULL)
		{  // Service exists
			QueryServiceStatus(schService, &ServiceStatus);  
			CloseServiceHandle(schService);

			if (ServiceStatus.dwCurrentState != SERVICE_STOPPED)   //check if service is running.
				bIsStopped = FALSE;
		}

		CloseServiceHandle(schSCManager);
	}

	return bIsStopped;
}

BOOL IsServiceStarted(LPCTSTR lpszServiceName)
{
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	SERVICE_STATUS ServiceStatus;
	BOOL bStarted = FALSE;

	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);
	if (schSCManager != NULL)
	{
		schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

		if (schService != NULL)
		{  // Service exists
			QueryServiceStatus(schService, &ServiceStatus); 
			//			CloseServiceHandle(schService);//cliicy marked it and move down for issue 14538109

			if (ServiceStatus.dwCurrentState != SERVICE_STOPPED)   //check if service is running.
				bStarted = TRUE;
			CloseServiceHandle(schService);//cliicy change line to here for issue 14538109
		}

		CloseServiceHandle(schSCManager);
	}  

	return bStarted;
}


BOOL GetServiceFileName(LPCTSTR lpszServiceName, LPTSTR lpServiceFile, DWORD ccBuffer)
{
	SC_HANDLE schSCManager = NULL;
	SC_HANDLE schService = NULL;
	LPQUERY_SERVICE_CONFIG lpsc = NULL; 
	DWORD dwBytesNeeded = 0, cbBufSize = 0;

	schSCManager = OpenSCManager(NULL, NULL, SERVICE_QUERY_CONFIG);

	if (schSCManager != NULL)
	{
		schService = OpenService(schSCManager, lpszServiceName, SERVICE_QUERY_CONFIG);

		if (schService != NULL)
		{  // Service exists
			if (!QueryServiceConfig( 
				schService, 
				NULL, 
				0, 
				&dwBytesNeeded))
			{
				DWORD dwError = GetLastError();
				if (ERROR_INSUFFICIENT_BUFFER == dwError)
				{
					cbBufSize = dwBytesNeeded;
					lpsc = (LPQUERY_SERVICE_CONFIG) LocalAlloc(LPTR, cbBufSize);
				}
				else
				{
					CloseServiceHandle(schService);
					CloseServiceHandle(schSCManager);
					return FALSE;
				}
			}

			if (!QueryServiceConfig( 
				schService, 
				lpsc, 
				cbBufSize, 
				&dwBytesNeeded) ) 
			{
				::LocalFree(lpsc);
				CloseServiceHandle(schService);
				CloseServiceHandle(schSCManager);
				return FALSE;
			}

			if (lpsc)
			{
				_tcscpy_s(lpServiceFile, ccBuffer, lpsc->lpBinaryPathName);

				::LocalFree(lpsc);
			}

			CloseServiceHandle(schService);
		}

		CloseServiceHandle(schSCManager);
	}

	return TRUE;
}


BOOL  GetServiceDisplayName(LPCTSTR lpszServiceName, LPTSTR lpServiceDisplayName, DWORD ccBuffer)
{
	SC_HANDLE schSCManager = NULL;
	SC_HANDLE schService = NULL;
	LPQUERY_SERVICE_CONFIG lpsc = NULL; 
	DWORD dwBytesNeeded = 0, cbBufSize = 0;

	schSCManager = OpenSCManager(NULL, NULL, SERVICE_QUERY_CONFIG);

	if (schSCManager != NULL)
	{
		schService = OpenService(schSCManager, lpszServiceName, SERVICE_QUERY_CONFIG);

		if (schService != NULL)
		{  // Service exists
			if (!QueryServiceConfig( 
				schService, 
				NULL, 
				0, 
				&dwBytesNeeded))
			{
				DWORD dwError = GetLastError();
				if (ERROR_INSUFFICIENT_BUFFER == dwError)
				{
					cbBufSize = dwBytesNeeded;
					lpsc = (LPQUERY_SERVICE_CONFIG) LocalAlloc(LPTR, cbBufSize);
				}
				else
				{
					CloseServiceHandle(schService);
					CloseServiceHandle(schSCManager);
					return FALSE;
				}
			}

			if (!QueryServiceConfig( 
				schService, 
				lpsc, 
				cbBufSize, 
				&dwBytesNeeded) ) 
			{
				::LocalFree(lpsc);
				CloseServiceHandle(schService);
				CloseServiceHandle(schSCManager);
				return FALSE;
			}

			if (lpsc)
			{
				_tcscpy_s(lpServiceDisplayName, ccBuffer, lpsc->lpDisplayName);

				::LocalFree(lpsc);
			}

			CloseServiceHandle(schService);
		}

		CloseServiceHandle(schSCManager);
	}

	return TRUE;
}

BOOL IsServiceExist(LPCTSTR lpszServiceName)
{
	if (NULL == lpszServiceName || _tcslen(lpszServiceName)==0)
		return FALSE;

	BOOL bRet = TRUE;
	SC_HANDLE schSCManager = NULL;
	SC_HANDLE schService = NULL;

	//Open the service control manager
	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);

	if (schSCManager == NULL)
		return FALSE;

	//open the services
	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService == NULL)
	{
		bRet = FALSE;
	}
	else
	{
		CloseServiceHandle(schService);
	}

	CloseServiceHandle(schSCManager);

	return bRet;
}
/******************************end for service*************************************/

typedef void (WINAPI *PGNSI)(LPSYSTEM_INFO);

typedef BOOL (WINAPI *PGPI)(DWORD, DWORD, DWORD, DWORD, PDWORD);

BOOL GetLocalOSVersion(DWORD &dwMajorVersion, DWORD &dwMinorVersion, WORD &wServicePackMajor, WORD &wSuiteMask)
{

	OSVERSIONINFOEX osvi;
	SYSTEM_INFO si;
	PGNSI pGNSI;
	BOOL bOsVersionInfoEx;
	DWORD dwType = 0;

	memset(&si, 0, sizeof(SYSTEM_INFO));
	memset(&osvi, 0, sizeof(OSVERSIONINFOEX));

	osvi.dwOSVersionInfoSize = sizeof(OSVERSIONINFOEX);

	if (!(bOsVersionInfoEx = GetVersionEx((OSVERSIONINFO *) &osvi)))
	{
		return FALSE;
	}

	// Call GetNativeSystemInfo if supported or GetSystemInfo otherwise.
	pGNSI = (PGNSI) GetProcAddress(GetModuleHandle(_T("kernel32.dll")), "GetNativeSystemInfo");
	if (NULL != pGNSI)
		pGNSI(&si);
	else
		::GetSystemInfo(&si);

	dwMajorVersion = osvi.dwMajorVersion;
	dwMinorVersion = osvi.dwMinorVersion;
	wSuiteMask = osvi.wSuiteMask;
	wServicePackMajor = osvi.wServicePackMajor;

	return TRUE;
}

BOOL IsOSUnderW2k3()
{
	DWORD dwMajorVersion = 0;
	DWORD dwMinorVersion = 0;
	WORD wSuiteMask = 0;
	WORD wServicePackMajor = 0;
	
	BOOL bRet = GetLocalOSVersion(dwMajorVersion,dwMinorVersion,wServicePackMajor,wSuiteMask);
	if(!bRet)
	{
		return FALSE;
	}
	
	if(dwMajorVersion < 6)
	{
		//windows 2003 or below.
		return TRUE;
	}

	return FALSE;
}

//set the registry value
BOOL SetRegValue(HKEY hKey, LPCTSTR lpSubKey, LPCTSTR lpValueName, LPCTSTR lpValue, DWORD dwType/*=REG_SZ*/)
{
	BOOL bRet = FALSE;
	TCHAR csValue[MAX_BUF_BUS]={0};
	TCHAR szMesg[MAX_BUF_BUS] ={0};
	TCHAR szData[MAX_BUF_BUS] ={0};
	DWORD dwSize = MAX_BUF_BUS;
	HKEY hConKey, hSubKey;

	memset(csValue, 0, sizeof(csValue));
	memset(szMesg, 0, sizeof(szMesg));
	memset(szData, 0, sizeof(szData));

	if (lpSubKey == NULL || _tcslen(lpSubKey) < 1 
		||lpValueName == NULL || _tcslen(lpValueName) < 1 
		||lpValue == NULL || _tcslen(lpValue) < 1)
	{
		return bRet;
	}

	hConKey = hKey;

	REGSAM samDesired = KEY_WRITE;

	if (Is64BitSystem())
	{
		samDesired = KEY_WRITE|KEY_WOW64_64KEY;
	}

	//get the registry value (hConKey, lpSubKey, 0, KEY_ALL_ACCESS, &hSubKey)
	if (ERROR_SUCCESS == RegOpenKeyEx(hConKey,
		lpSubKey, 
		0,
		samDesired, 
		&hSubKey))
	{
		DWORD dwlen;

		if (dwType == REG_DWORD)
		{
			DWORD dwValue;
			dwValue = (DWORD)_tstoi(lpValue);
			if (ERROR_SUCCESS == RegSetValueEx(hSubKey,lpValueName,0,dwType,(const BYTE*)&dwValue,4))
			{
				bRet = TRUE;
			}
		}
		else
		{
			dwlen = (DWORD)_tcslen(lpValue)*sizeof(TCHAR);
			if (ERROR_SUCCESS == RegSetValueEx(hSubKey, lpValueName, 0, dwType, (const BYTE*)lpValue, dwlen))
			{
				bRet = TRUE;
			}
		}

		//_sntprintf(szMesg,sizeof(szMesg),_T("SetRegValue: set %s=%s in %s"),lpValueName,lpValue,lpSubKey);

		RegCloseKey(hSubKey);
	}
	else
	{
		// Don't open key '%s',create it	
	}

	return bRet;
}

//get the registry value
BOOL GetRegValue(HKEY hKey, LPCTSTR lpSubKey, LPCTSTR lpValueName, CString & strReturnValue,BOOL bNative,DWORD dwType/*=REG_SZ*/)
{
	BOOL bRet = FALSE;
	TCHAR csValue[MAX_BUF_BUS];
	DWORD dwSize =MAX_BUF_BUS;
	HKEY hConKey,hSubKey;
	memset(csValue,0,sizeof(csValue));

	if(lpSubKey == NULL || _tcslen(lpSubKey) < 1 
		||lpValueName == NULL || _tcslen(lpValueName) < 1 )
	{
		return bRet;
	}

	hConKey = hKey;

	REGSAM samDesired = KEY_READ;

	if (Is64BitSystem())
	{
		if(bNative)
		{
			//read true key node
			samDesired = KEY_READ|KEY_WOW64_64KEY;
		}
		else
		{
			//read Wow6432Node
			samDesired = KEY_READ|KEY_WOW64_32KEY;
		}
	}

	//get the registry value
	if(RegOpenKeyEx (hConKey, lpSubKey, 0, samDesired, &hSubKey) == ERROR_SUCCESS)
	{
		if(RegQueryValueEx(hSubKey,lpValueName,NULL,&dwType,(LPBYTE)csValue,&dwSize) == ERROR_SUCCESS)
		{
			if(dwType == REG_DWORD)
			{
				strReturnValue.Format(_T("%d"),*csValue);
			}
			else
			{
				strReturnValue = CString(csValue);
			}

			bRet = TRUE;
		}

		RegCloseKey(hSubKey);
	}

	return bRet;

}

BOOL Is64BitSystem()
{
	TCHAR szData[MAX_PATH];
	DWORD dwDataSize;
	HKEY hKey;

	if (::RegOpenKeyEx(HKEY_LOCAL_MACHINE, REGKEY_MICROSOFT_WINDOWS, 0, KEY_READ, &hKey) != ERROR_SUCCESS)
		return FALSE;

	BOOL bRet = FALSE;
	ZeroMemory(szData, sizeof(szData));
	dwDataSize = sizeof(szData);

	if (::RegQueryValueEx(hKey, REGVALUE_PROGRAM_FILES_DIR_X86, NULL, NULL, (LPBYTE)szData, &dwDataSize) == ERROR_SUCCESS)
		bRet = TRUE;

	::RegCloseKey(hKey);

	return bRet;
}

BOOL  GetFilePath(LPCTSTR lpFullFileName,LPTSTR lpPath,DWORD ccSize)
{
	TCHAR drive[_MAX_DRIVE];
	TCHAR dir[_MAX_DIR];
	memset(drive,0,sizeof(drive));
	memset(dir,0,sizeof(dir));

	if(lpFullFileName == NULL || _tcslen(lpFullFileName) == 0 || ccSize == 0)
	{
		return FALSE;
	}

	_tsplitpath_s(lpFullFileName, drive, _MAX_DRIVE, dir, _MAX_DIR, NULL, 0, NULL, 0);

	_stprintf_s(lpPath,ccSize,_T("%s%s"),drive,dir);

	return TRUE;
}

DWORD ValidateDiskSpace(LPCTSTR lpPath,DWORD64 dwNeedSize,DWORD64& dwFreeDiskSizeWithMB)
{
	DWORD dwRet = ERROR_SUCCESS;
	DWORD64 dw64FreeSize = 0;
	dwFreeDiskSizeWithMB = 0;
	
	if(lpPath == NULL || _tcslen(lpPath) <= 0)
	{
		return ERROR_INVALID_PARAMETER;
	}

	dwRet = GetFreeDiskSize(lpPath,dw64FreeSize);

	if(dwRet != ERROR_SUCCESS)
	{
		return dwRet;
	}

	dwFreeDiskSizeWithMB = dw64FreeSize/BASE_MB;

	if( dwFreeDiskSizeWithMB < dwNeedSize)
	{
		return ERROR_DISK_FULL;
	}
	//end check system disk space

	return dwRet;
}

DWORD GetFreeDiskSize(LPCTSTR lpszDrive,DWORD64& dw64FreeDiskSize)
{
	TCHAR szTempPath[MAX_PATH];
	DWORD dwRet = ERROR_SUCCESS;
	BOOL bResult = FALSE;
	__int64 i64FreeBytesToCaller, i64TotalBytes, i64FreeBytes;
	DWORD dwSectPerClust, dwBytesPerSect, dwFreeClusters, dwTotalClusters;

	//get the path if the input is file
	memset(szTempPath,0,sizeof(szTempPath));
	_stprintf_s(szTempPath,_countof(szTempPath),_T("%s"),lpszDrive);

	//get the exist path
	BOOL bLoop = TRUE;
	while(bLoop)
	{
		bLoop = PathRemoveFileSpec(szTempPath);
		if(bLoop && _taccess(szTempPath,0) == 0)
		{
			//exit, and permit
			break;
		}
	}
	//end get


#ifdef UNICODE
	FARPROC pGetDiskFreeSpaceEx = ::GetProcAddress(GetModuleHandle(KERNEL32_DLL),
		"GetDiskFreeSpaceExW");
#else
	FARPROC pGetDiskFreeSpaceEx = ::GetProcAddress(GetModuleHandle(KERNEL32_DLL),
		"GetDiskFreeSpaceExA");
#endif

	if (pGetDiskFreeSpaceEx)
	{
		bResult = ::GetDiskFreeSpaceEx(szTempPath,
			(PULARGE_INTEGER) &i64FreeBytesToCaller,
			(PULARGE_INTEGER) &i64TotalBytes,
			(PULARGE_INTEGER) &i64FreeBytes);

		// Process GetDiskFreeSpaceEx results.
		if(bResult) 
		{
			dw64FreeDiskSize = i64FreeBytes;
		}
		else
		{
			dwRet = GetLastError();
			dw64FreeDiskSize = 0;
		}
	}
	else 
	{
		bResult = GetDiskFreeSpace(szTempPath, 
			&dwSectPerClust, 
			&dwBytesPerSect,
			&dwFreeClusters, 
			&dwTotalClusters);

		// Process GetDiskFreeSpace results.
		if(bResult) 
		{
			dw64FreeDiskSize = dwFreeClusters*dwSectPerClust*dwBytesPerSect;
		}
		else
		{
			dwRet = GetLastError();
			dw64FreeDiskSize = 0;
		}	
	}
	
	return dwRet;
}

//delete the registry value
BOOL DeleteRegValue(HKEY hKey, LPCTSTR lpSubKey, LPCTSTR lpValueName, BOOL bNative)
{
	BOOL bRet = FALSE;
	BOOL b64BitOS = Is64BitSystem();
	HKEY hConKey, hSubKey;


	if (lpSubKey == NULL || _tcslen(lpSubKey) < 1
		|| lpValueName == NULL || _tcslen(lpValueName) < 1)
	{
		return bRet;
	}

	hConKey = hKey;

	REGSAM samDesired = KEY_WRITE;

	if (b64BitOS)
	{
		if (bNative)
		{
			samDesired = KEY_ALL_ACCESS | KEY_WOW64_64KEY;
		}
		else
		{
			samDesired = KEY_ALL_ACCESS | KEY_WOW64_32KEY;
		}
	}
	else
	{
		samDesired = KEY_ALL_ACCESS;
	}

	//get the registry value (hConKey, lpSubKey, 0, KEY_ALL_ACCESS, &hSubKey)
	if (ERROR_SUCCESS == RegOpenKeyEx(hConKey,
		lpSubKey,
		0,
		samDesired,
		&hSubKey))
	{


		if (ERROR_SUCCESS == RegDeleteValue(hSubKey, lpValueName))
		{
			bRet = TRUE;
		}


		RegCloseKey(hSubKey);
	}
	else
	{
		// Don't open key '%s'
	}

	return bRet;
}


LONG RecursiveDeleteKey(HKEY hKeyParent, LPCTSTR lpszKeyChild, BOOL bNative)
{
	DWORD dwRetValue = ERROR_SUCCESS;

	BOOL b64BitOS = Is64BitSystem();

	HKEY hKeyChild;

	REGSAM regSam;

	if (hKeyParent == NULL)
	{
		return ERROR_INVALID_PARAMETER;
	}

	if (lpszKeyChild == NULL || _tcslen(lpszKeyChild) == 0)
	{
		return ERROR_INVALID_PARAMETER;
	}

	if (b64BitOS)
	{
		if (bNative)
		{
			regSam = KEY_ALL_ACCESS | KEY_WOW64_64KEY;
		}
		else
		{
			regSam = KEY_ALL_ACCESS | KEY_WOW64_32KEY;
		}
	}
	else
	{
		regSam = KEY_ALL_ACCESS;
	}

	LONG lRes = ::RegOpenKeyEx(hKeyParent, lpszKeyChild, 0, regSam, &hKeyChild);
	if (lRes != ERROR_SUCCESS)
	{
		return lRes;
	}

	FILETIME time;
	TCHAR szBuffer[1024];
	DWORD dwSize = 1024;

	ZeroMemory(szBuffer, sizeof(szBuffer));
	while (ERROR_SUCCESS == RegEnumKeyEx(hKeyChild, 0, szBuffer, &dwSize, NULL, NULL, NULL, &time))
	{
		DWORD dwRes = ERROR_SUCCESS;
		dwRes = RecursiveDeleteKey(hKeyChild, szBuffer, bNative);
		if (dwRes != ERROR_SUCCESS)
		{
			::RegCloseKey(hKeyChild);
			return lRes;
		}

		ZeroMemory(szBuffer, sizeof(szBuffer));
		dwSize = _countof(szBuffer);
	}
	::RegCloseKey(hKeyChild);

	if (b64BitOS)
	{
		HMODULE hAdvapi32Dll = LoadLibrary(_T("ADVAPI32.DLL"));

		if (hAdvapi32Dll == NULL)
		{
			return ERROR_ACCESS_DENIED;
		}

		PFRegDelEx lpfRegDelEx = NULL;

#ifdef UNICODE
		lpfRegDelEx = (PFRegDelEx)GetProcAddress(hAdvapi32Dll, "RegDeleteKeyExW");
#else
		lpfRegDelEx = (PFRegDelEx)GetProcAddress(hAdvapi32Dll, "RegDeleteKeyExA");
#endif

		if (lpfRegDelEx != NULL)
		{
			dwRetValue = lpfRegDelEx(hKeyParent, lpszKeyChild, regSam, 0);
			FreeLibrary(hAdvapi32Dll);
			return dwRetValue;
		}
		else
		{
			FreeLibrary(hAdvapi32Dll);
			return ERROR_ACCESS_DENIED;
		}

	}
	else
	{
		return RegDeleteKey(hKeyParent, lpszKeyChild);
	}

}

BOOL IsEmptyKey(HKEY hKeyParent, LPCTSTR lpKey, BOOL bNative)
{
	BOOL bRet = FALSE;
	CRegKey rk;
	REGSAM regMask = KEY_READ;
	if (bNative)
	{
		regMask |= KEY_WOW64_64KEY;
	}

	//check if the parent key is null
	if (rk.Open(hKeyParent, lpKey, regMask) == ERROR_SUCCESS)
	{
		//Registry key
		DWORD dwIndex = 0;
		DWORD dwNameLength1 = MAX_PATH;
		DWORD dwNameLength2 = MAX_PATH;
		TCHAR szBuffer1[MAX_PATH] = { 0 };
		TCHAR szBuffer2[MAX_PATH] = { 0 };

		//Registry Value data and type
		DWORD dwType = 0;
		DWORD dwValueIndex = 0;
		BYTE pData[MAX_PATH] = { 0 };
		DWORD dwDataLength = MAX_PATH;

		if (RegEnumValue(rk, dwValueIndex, szBuffer1, &dwNameLength1, NULL, &dwType, pData, &dwDataLength) != ERROR_SUCCESS
			&&rk.EnumKey(dwIndex, szBuffer2, &dwNameLength2) != ERROR_SUCCESS)
		{
			//the key is empty key
			bRet = TRUE;
		}

		rk.Close();
	}

	return bRet;
}

//only remove current empty key, not Recursive
BOOL RemoveEmptyRegistry(HKEY hKeyParent, LPCTSTR lpszKey, BOOL bNative)
{
	BOOL bRet = FALSE;

	CRegKey rk;
	BOOL b64BitOS = Is64BitSystem();
	REGSAM regSam = KEY_ALL_ACCESS;
	if (bNative)
	{
		regSam = KEY_ALL_ACCESS | KEY_WOW64_64KEY;
	}

	//fix issue 19052674
	if (IsEmptyKey(hKeyParent, lpszKey, bNative))
	{
		if (b64BitOS)
		{
			DWORD dwRet = ERROR_SUCCESS;
			HMODULE hAdvapi32Dll = LoadLibrary(_T("ADVAPI32.DLL"));

			if (hAdvapi32Dll == NULL)
			{
				return FALSE;
			}

			PFRegDelEx lpfRegDelEx = NULL;

#ifdef UNICODE
			lpfRegDelEx = (PFRegDelEx)GetProcAddress(hAdvapi32Dll, "RegDeleteKeyExW");
#else
			lpfRegDelEx = (PFRegDelEx)GetProcAddress(hAdvapi32Dll, "RegDeleteKeyExA");
#endif

			if (lpfRegDelEx != NULL)
			{
				dwRet = lpfRegDelEx(HKEY_LOCAL_MACHINE, lpszKey, regSam, 0);
				FreeLibrary(hAdvapi32Dll);
			}
			else
			{
				FreeLibrary(hAdvapi32Dll);
				return FALSE;
			}

			if (ERROR_SUCCESS == dwRet)
			{
				bRet = TRUE;
			}
		}
		else
		{
			if (ERROR_SUCCESS == RegDeleteKey(HKEY_LOCAL_MACHINE, lpszKey))
			{
				bRet = TRUE;
			}
		}
	}
	else
	{
		//not empty key
		bRet = FALSE;
	}

	return bRet;
}

