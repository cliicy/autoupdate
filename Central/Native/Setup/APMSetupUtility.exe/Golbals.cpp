#include "stdafx.h"
#include "Golbals.h"
#include "Tlhelp32.h"
#include "Psapi.h"
#include "APMSetupUtility.h"
#include "CAComponent.h"
//#include "MSXMLParserWrapper.h"

extern CUpdateCheckApp theApp;

typedef DWORD (WINAPI *PGetProcessImageFileName)(HANDLE hProcess,LPTSTR lpImageFileName,DWORD nSize);

const CString TYPECOMPONENT = _T("ComponentType");
void WriteLogFile(LPCTSTR lpMessage)
{
	TCHAR szDumpPath[MAX_PATH];
	CString sMessage, sStr;
	
	TCHAR szWrtBuf[MAX_PATH * 40];
	LPCTSTR lpWrtBuf = szWrtBuf;
	
	memset(szDumpPath, 0, sizeof(szDumpPath));
	memset(szWrtBuf, 0, sizeof(szWrtBuf));
	
	CTime time = CTime::GetCurrentTime();
	sMessage = time.Format(_T("%Y-%m-%d %H:%M:%S    "));
	sMessage += lpMessage;
	
	sMessage.TrimRight();
	sStr = sMessage.Right(2);

	if (sStr.CompareNoCase(_T("\r\n")))
		sMessage += _T("\r\n");
	
	_tcscpy_s(szWrtBuf, _countof(szWrtBuf), sMessage);

	_tcscpy(szDumpPath, theApp.m_strLogFolder);
	if (_tcslen(szDumpPath)==0)
	{
		::GetTempPath(MAX_PATH, szDumpPath);
	}
	else
	{
		::CreateDirectory(szDumpPath, NULL);
	}

	::PathAppend(szDumpPath, LOG_FILE);
	theApp.m_strLogFile = szDumpPath;
	
	DWORD dwBytesWritten;
	HANDLE hFile;
	
	// Open/Create the file
	hFile = ::CreateFile(szDumpPath,
		GENERIC_WRITE,
		0,
		NULL,
		OPEN_ALWAYS,
		FILE_ATTRIBUTE_NORMAL,
		NULL);
	
	if (INVALID_HANDLE_VALUE == hFile)
		return;
	
	// Append the message to the end of the file
	DWORD dwBytesToWrite = DWORD(_tcslen(lpWrtBuf) * sizeof(TCHAR));
	DWORD dwFileOffset = SetFilePointer(hFile, 0, NULL, FILE_END);
	::LockFile(hFile, dwFileOffset, 0, dwFileOffset + dwBytesToWrite, 0);
	::WriteFile(hFile, lpWrtBuf, dwBytesToWrite, &dwBytesWritten, NULL);
	::UnlockFile(hFile, dwFileOffset, 0, dwFileOffset + dwBytesToWrite, 0);
	
	// Close handle
	::CloseHandle(hFile);
}
BOOL IsServiceStart(LPCTSTR lpszServiceName)
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
					if (!StopSpecService(szDependentService, bBlockOperation, FALSE))
						bStopDependentServices = FALSE;

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
BOOL StopService(SC_HANDLE schService, LPCTSTR lpszServiceName, BOOL bBlockOperation, BOOL bStopDependents)
{
	SERVICE_STATUS ServiceStatus;
	//DWORD dwLastCheckPoint;
	DWORD 	dwMaxWaitTime;

	QueryServiceStatus(schService, &ServiceStatus);

	if (ServiceStatus.dwCurrentState == SERVICE_STOPPED || ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING)
		return TRUE;

	if (!ControlService(schService, SERVICE_CONTROL_STOP, &ServiceStatus))
	{
		if (GetLastError() == ERROR_DEPENDENT_SERVICES_RUNNING && bStopDependents)
		{
			// Stop dependent services and try it again.
			StopDependentServices(lpszServiceName, bBlockOperation);

			if (!ControlService(schService, SERVICE_CONTROL_STOP, &ServiceStatus))
				return FALSE;
		}
		else
			return FALSE;
	}

	if (bBlockOperation)
	{ //Wait until service stops
		dwMaxWaitTime = 31; // Maximum wait time is 31 seconds

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
				break;
			//if (dwLastCheckPoint >= ServiceStatus.dwCheckPoint)
			//   break;

			// Do not wait more than suggested
			if (dwMaxWaitTime <= 0)
				break;

			dwMaxWaitTime--;
		}
	}


	//ISC 11/16/98: According to Chie's request to return TRUE only when STOPPED
	//return ServiceStatus.dwCurrentState == SERVICE_STOPPED || ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING;
	return ServiceStatus.dwCurrentState == SERVICE_STOPPED;
}

BOOL StopSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation /* = TRUE*/, BOOL bStopDependents /*= FALSE*/)
{
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	BOOL bStopService = FALSE;

	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_CONNECT);
	if (schSCManager == NULL)
		return FALSE;

	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService == NULL)
	{
		if (GetLastError() == ERROR_SERVICE_DOES_NOT_EXIST)
			bStopService = TRUE;
	}
	else
	{
		bStopService = StopService(schService, lpszServiceName, bBlockOperation, bStopDependents);
		CloseServiceHandle(schService);
	}

	CloseServiceHandle(schSCManager);

	return bStopService;
}

BOOL ChangeSvcStartType(LPCTSTR lpszServiceName, const DWORD dwStartType)
{
	if (NULL == lpszServiceName)
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
		//the service do not existed
		bRet = FALSE;
		goto End;
	}

	if (!ChangeServiceConfig(
		schService,        // handle of service 
		SERVICE_NO_CHANGE, // service type: no change 
		dwStartType,       // change service start type 
		SERVICE_NO_CHANGE, // error control: no change 
		NULL,              // binary path: no change 
		NULL,              // load order group: no change 
		NULL,              // tag ID: no change 
		NULL,              // dependencies: no change 
		NULL,              // account name: no change 
		NULL,              // password: no change 
		NULL))            // display name: no change
	{
		bRet = FALSE; 
	}

End:
	if (NULL != schService)
		CloseServiceHandle(schService);

	if (NULL != schSCManager)
		CloseServiceHandle(schSCManager);

	return bRet;
}
void WriteLog(const TCHAR* pszFormat, ...)
{
	TCHAR szMessage[1024];
	memset(szMessage, 0, sizeof(szMessage));

	va_list arglist = NULL;
	va_start(arglist, pszFormat);
	_vstprintf_s(szMessage, _countof(szMessage), pszFormat, arglist);
	va_end(arglist);

	WriteLogFile(szMessage);
}
DWORD SetupGetPrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpDefault, LPTSTR lpReturnedString, DWORD nSize, LPCTSTR lpFileName)
{
	DWORD dwRet = ERROR_SUCCESS;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT; i++)
	{
		dwRet = ::GetPrivateProfileString(lpAppName, 
			lpKeyName, 
			lpDefault,
			lpReturnedString,
			nSize,
			lpFileName);

		if (ERROR_SHARING_VIOLATION == GetLastError())
		{
			WriteLog(_T("SetupGetPrivateProfileString(%s, %s,...%s) failed. Err=%d"), lpAppName, lpKeyName, lpFileName, ERROR_SHARING_VIOLATION);
			Sleep(1000);
		}
		else
		{
			break;
		}
	}

	return dwRet;
}

BOOL DecryptString(const CString &strIn, CString &strOut, CString & instrworkdir)
{
	typedef BOOL (__stdcall *DECRYPTSTRING)(const wchar_t *, wchar_t *, DWORD *);

	TCHAR strFile[MAX_PATH];
	_tcscpy_s(strFile, _countof(strFile), instrworkdir);
	::PathAppend(strFile, _T("BIN\\AFCoreInterface.dll"));

	HMODULE hModule = NULL;
	//hModule = GetModuleHandle( strFile);
	if(!hModule)
	{
		hModule = ::LoadLibraryEx(strFile, NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
		WriteLog(_T("error code is: %d"), ::GetLastError());
	}


	BOOL bRet = FALSE;

	if (hModule)
	{
		DECRYPTSTRING lpfn;
		lpfn = (DECRYPTSTRING)::GetProcAddress(hModule, "AFDecryptString");

		if (lpfn)
		{
			DWORD dwSize = 1024 * 4;
			LPTSTR lptValue = new TCHAR[dwSize];
			memset(lptValue, 0, sizeof(TCHAR)*dwSize);

			bRet = (*lpfn)(strIn, lptValue, &dwSize);
			if (bRet)
				strOut = lptValue;

			delete []lptValue;
		}
		else
		{
			WriteLog(_T("Unable to GetProcAddress AFDecryptString"));
		}

		::FreeLibrary(hModule);
	}
	else
		WriteLog(_T("Unable to load %s"), strFile);

	return bRet;
}

BOOL EncryptString(const CString &strIn, CString &strOut, CString &instrworkdir)
{
	typedef BOOL (__stdcall *ENCRYPTSTRING)(const wchar_t *, wchar_t *, DWORD *);

	TCHAR strFile[MAX_PATH];
	_tcscpy_s(strFile, _countof(strFile), instrworkdir);
	::PathAppend(strFile, _T("BIN\\AFCoreInterface.dll"));

	HMODULE hModule = NULL;
	//hModule = GetModuleHandle( strFile);
	if(!hModule)
	{
		hModule = ::LoadLibraryEx(strFile, NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
		WriteLog(_T("error code is: %d"), ::GetLastError());
	}

	BOOL bRet = FALSE;

	if (hModule)
	{
		ENCRYPTSTRING lpfn;

		lpfn = (ENCRYPTSTRING)::GetProcAddress(hModule, "AFEncryptString");

		if (lpfn)
		{
			DWORD dwSize = 1024 * 4;
			LPTSTR lptValue = new TCHAR[dwSize];
			memset(lptValue, 0, sizeof(TCHAR)*dwSize);

			bRet = (*lpfn)(strIn, lptValue, &dwSize);
			if (bRet)
				strOut = lptValue;

			delete []lptValue;
		}
		else
		{
			WriteLog(_T("Unable to GetProcAddress AFEncryptString"));
		}

		::FreeLibrary(hModule);
	}
	else
	{
		WriteLog(_T("Unable to load %s"), strFile);
	}

	return bRet;
}

BOOL SetupWritePrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpString, LPCTSTR lpFileName)
{
	BOOL bRet = FALSE;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT; i++)
	{
		bRet = ::WritePrivateProfileString(lpAppName, lpKeyName, lpString, lpFileName);

		 if (ERROR_SHARING_VIOLATION == GetLastError())
		 {
			 WriteLog(_T("SetupWritePrivateProfileString(%s, %s,...%s) failed. Err=%d"), lpAppName, lpKeyName, lpFileName, ERROR_SHARING_VIOLATION);
			 Sleep(1000);
		 }
		 else
		 {
			 break;
		 }
	}

	if (!bRet)
	{
		WriteLog(_T("SetupWritePrivateProfileString(%s %s %s %s) failed with error %d"),
			lpAppName, lpKeyName, lpString, lpFileName, ::GetLastError());
	}

	return bRet;
}

UINT SetupGetPrivateProfileInt(LPCTSTR lpAppName, LPCTSTR lpKeyName, INT nDefault, LPCTSTR lpFileName)
{
	UINT nRet = 0;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT; i++)
	{
		nRet = ::GetPrivateProfileInt(lpAppName, 
			lpKeyName, 
			nDefault,
			lpFileName);

		if (ERROR_SHARING_VIOLATION == GetLastError())
		{
			WriteLog(_T("SetupGetPrivateProfileInt(%s, %s,...%s) failed. Err=%d"), lpAppName, lpKeyName, lpFileName, ERROR_SHARING_VIOLATION);
			Sleep(1000);
		}
		else
		{
			break;
		}
	}

	return nRet;
}
DWORD SetupGetPrivateProfileSection(LPCTSTR lpSectionName, LPTSTR szSecBuffer, DWORD nSize, LPCTSTR lpFileName)
{
	DWORD dwRet = ERROR_SUCCESS;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT; i++)
	{
		dwRet = ::GetPrivateProfileSection(lpSectionName, 
			szSecBuffer, 
			nSize,
			lpFileName);
		if (ERROR_SHARING_VIOLATION == GetLastError())
		{
			WriteLog(_T("SetupGetPrivateProfileSection(%s,...%s) failed. Err=%d"), lpSectionName, lpFileName, ERROR_SHARING_VIOLATION);
			Sleep(1000);
		}
		else
		{
			break;
		}
	}

	return dwRet;
}

BOOL SetupWritePrivateProfileInt(LPCTSTR lpAppName, LPCTSTR lpKeyName, INT Value, LPCTSTR lpFileName)
{
	 TCHAR szBuffer[32];
	 memset(szBuffer, 0, sizeof(szBuffer));
	 _stprintf_s(szBuffer, _countof(szBuffer), _T("%d"), Value);
	 return SetupWritePrivateProfileString(lpAppName, lpKeyName, szBuffer, lpFileName);
}

void GetStringArray(LPCTSTR lpctValue, TCHAR cSplitChar, CStringArray& data)
{
	data.RemoveAll();

	if (NULL == lpctValue)
		return;

	TRACE(_T("\n%s"), lpctValue);

	LPCTSTR lptPre, lptNext;
	lptPre = lpctValue;
	lptNext = lpctValue;

	int nSize;
	CString strTmp;
	TCHAR szTmp[MAX_PATH];
	while (*lptPre != '\0' && (lptNext = _tcschr(lptPre, cSplitChar)) != NULL)
	{
		//Point to the last cSplitChar, like "aa;;;bb", lptNext will point to last ';' before 'b'
		while (*(lptNext+1) == cSplitChar)
			lptNext++;

		//Deal with this case: ";aa;bb"
		nSize = (int)(lptNext - lptPre);
		if (nSize == 0)
		{
			lptPre = lptNext + 1;
			continue;
		}

		//Get the sub string
		_tcsncpy_s(szTmp, _countof(szTmp), lptPre, nSize);
		szTmp[nSize] = '\0';
		strTmp = szTmp;

		strTmp.TrimLeft();
		strTmp.TrimRight();
		strTmp.TrimLeft(cSplitChar);
		strTmp.TrimRight(cSplitChar);

		if (!strTmp.IsEmpty())
		{
			TRACE(_T("\n%s"), strTmp);
			data.Add(strTmp);
		}

		lptPre = lptNext+1;
	}

	//Deal with this case: "aa;bb;cc"
	if (*lptPre != '\0')
	{
		strTmp = lptPre;
		strTmp.TrimLeft();
		strTmp.TrimRight();
		strTmp.TrimLeft(cSplitChar);
		strTmp.TrimRight(cSplitChar);
		if (!strTmp.IsEmpty())
		{
			TRACE(_T("\n%s"), strTmp);
			data.Add(strTmp);
		}
	}
}

DWORD LaunchProcess(LPCTSTR lpctCmdLine, LPCTSTR pWorkingDir, DWORD &dwExitCode, DWORD dwMilliseconds/*=INFINITE*/, DWORD dwCreationFlags/*=0*/)
{
	WriteLog(_T("LaunchProcess Begin"));
	if(lpctCmdLine)
		WriteLog(_T("Commandline: %s"), lpctCmdLine);
	if(pWorkingDir)
		WriteLog(_T("Working Dir: %s"), pWorkingDir);
	
	TCHAR szCmdLine[MAX_PATH*4];
	memset(szCmdLine, 0, sizeof(szCmdLine));
	_tcscpy_s(szCmdLine, _countof(szCmdLine), lpctCmdLine);
	
	DWORD dwRet = 0;
	STARTUPINFO	si = {0};
	PROCESS_INFORMATION pi = {0};
	
	si.cb = sizeof(STARTUPINFO);
	
	if (CreateProcess(NULL,
		szCmdLine,
		NULL,
		NULL,
		TRUE,
		dwCreationFlags,
		NULL, 
		pWorkingDir,  
		&si, 
		&pi))
	{
		WaitForSingleObject(pi.hProcess, dwMilliseconds);
		GetExitCodeProcess(pi.hProcess, &dwExitCode);
		CloseHandle(pi.hThread);
		CloseHandle(pi.hProcess);
	}
	else
	{
		dwExitCode = 1;
		dwRet = GetLastError();
	}
	
	WriteLog(_T("LaunchProcess: return %d with ExitCode %d"), dwRet, dwExitCode);
	
	return dwRet;
}

BOOL Is64BitMachine(LPCTSTR lpMachine)
{
	HKEY hKey, hConnectKey;
	BOOL bReturn = FALSE;
	CString sMsg;

	if (ERROR_SUCCESS == ::RegConnectRegistry(lpMachine, HKEY_LOCAL_MACHINE, &hConnectKey))
	{
		TCHAR szPrgX86Path[MAX_PATH];
		DWORD dwSize = sizeof(TCHAR) * MAX_PATH;
		memset(szPrgX86Path, 0, dwSize);

		if (ERROR_SUCCESS == ::RegOpenKeyEx(hConnectKey, REGKEY_MICROSOFT_WINDOWS, 0, KEY_READ, &hKey))
		{
			if (ERROR_SUCCESS == ::RegQueryValueEx(hKey, 
				REGVALUE_PROGRAM_FILES_DIR_X86, 
				NULL, 
				NULL, 
				(LPBYTE)szPrgX86Path, 
				&dwSize))
			{
				if (_tcslen(szPrgX86Path) > 0)
				{
					bReturn = TRUE;
				}
			}

			::RegCloseKey(hKey);
		}

		::RegCloseKey(hConnectKey);
	}

	return bReturn;
}

void TranslateDeviceName(LPCTSTR lpctDevicename, LPTSTR lpBuf)
{
	TCHAR szTemp[1024] = {0};

	TCHAR pszFilename[MAX_PATH] = {0};
	_tcscpy(pszFilename, lpctDevicename);

	CStringArray strLogicalDriveAry;
	CStringArray strDeviceDriveAry;
	
	GetLogicalDriveStrings(_countof(szTemp), szTemp);
	LPTSTR lpPos = szTemp;
	while(*lpPos != NULL)
	{
		strLogicalDriveAry.Add(lpPos);
		lpPos = lpPos + _tcslen(lpPos)+1;
	}

	for(int i=0; i<strLogicalDriveAry.GetCount(); i++)
	{
		strLogicalDriveAry[i].TrimRight(_T("\\"));

		TCHAR szName[MAX_PATH] = {0};
		QueryDosDevice(strLogicalDriveAry[i], szName, _countof(szName));
		strDeviceDriveAry.Add(szName);
	}

	CString strDeviceFileName = lpctDevicename;
	for(int i=0; i<strLogicalDriveAry.GetCount(); i++)
	{
		strDeviceFileName.Replace(strDeviceDriveAry[i], strLogicalDriveAry[i]);
	}

	_tcscpy(lpBuf, strDeviceFileName);
}

BOOL EnableDebugPrivNT()
{
	WriteLog(_T("begin to enable debug privilege."));
    HANDLE hToken;
    LUID DebugValue;
    TOKEN_PRIVILEGES tkp;

    if (!OpenProcessToken(GetCurrentProcess(),
            TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY,
            &hToken))
	{
		WriteLog(_T("OpenProcessToken failed with %d"), GetLastError());
        return FALSE;
    }

    if (!LookupPrivilegeValue(NULL,
            SE_DEBUG_NAME,
            &DebugValue))
	{
        WriteLog(_T("LookupPrivilegeValue failed with %d"), GetLastError());
        return FALSE;
    }

    tkp.PrivilegeCount = 1;
    tkp.Privileges[0].Luid = DebugValue;
    tkp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;

    AdjustTokenPrivileges(hToken,
        FALSE,
        &tkp,
        sizeof(TOKEN_PRIVILEGES),
        (PTOKEN_PRIVILEGES) NULL,
        (PDWORD) NULL);

    if (GetLastError() != ERROR_SUCCESS)
	{
        WriteLog(_T("AdjustTokenPrivileges failed with %d"), GetLastError());
        return FALSE;
    }
    return TRUE;
}

BOOL FindProcesses(const CStringArray &strPrcArray, CArray<DWORD> &dwPidArray)
{
	HANDLE hProcessSnap = NULL;
	PROCESSENTRY32 pe32;

	ZeroMemory(&pe32, sizeof(PROCESSENTRY32));
	pe32.dwSize = sizeof(PROCESSENTRY32);

	EnableDebugPrivNT();

	hProcessSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
	if (NULL == hProcessSnap)
		return FALSE;

	dwPidArray.RemoveAll();

	BOOL bMoreProcess = Process32First(hProcessSnap,&pe32);

	while(bMoreProcess)
	{
		//get the process binary name
		TCHAR szModuleName[MAX_PATH] = {0};

		HANDLE hProcess = OpenProcess( PROCESS_QUERY_INFORMATION|PROCESS_VM_READ, FALSE, pe32.th32ProcessID);

		if (NULL == hProcess)
		{
			bMoreProcess = Process32Next(hProcessSnap, &pe32);
			continue;
		}
		if (Is64BitMachine(NULL))
		{
			HINSTANCE hinstDll = ::LoadLibraryEx(_T("Psapi.dll"), NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
			if (hinstDll)
			{
				PGetProcessImageFileName lpGetProcessImageFileName;
				#ifdef UNICODE
				//#define GetProcessImageFileName  GetProcessImageFileNameW
				lpGetProcessImageFileName = (PGetProcessImageFileName)::GetProcAddress(hinstDll, "GetProcessImageFileNameW");
				#else
						//#define GetProcessImageFileName  GetProcessImageFileNameA
				lpGetProcessImageFileName = (PGetProcessImageFileName)::GetProcAddress(hinstDll, "GetProcessImageFileNameA");
				#endif // !UNICODE
						
				if (lpGetProcessImageFileName != NULL)
				{
					if(!(*lpGetProcessImageFileName)(hProcess, szModuleName, MAX_PATH))
					{
						WriteLog(_T("fail to get process image name for %s."), pe32.szExeFile);
					}
				}
				else
				{
					WriteLog(_T("Can not call the API GetProcessImageFileName for %s."),pe32.szExeFile);
				}
			}
			else
			{
				WriteLog(_T("Can not loadlibrary (Psapi.dll)for %s."), pe32.szExeFile);
			}
		}
		else
		{
			if(GetModuleFileNameEx(hProcess, NULL, szModuleName, MAX_PATH) == MAX_PATH)
			{
				WriteLog(_T("fail to get module file name for %s."), pe32.szExeFile);
			}
		}
		CloseHandle(hProcess);
		TranslateDeviceName(szModuleName, szModuleName);

		//compare the binary path
		TCHAR szShortName[MAX_PATH] = {0};
		TCHAR szLongName[MAX_PATH] = {0};
		::GetShortPathName(szModuleName, szShortName, _countof(szShortName));
		::GetLongPathName(szModuleName, szLongName, _countof(szLongName));
		if(_tcslen(szLongName)==0)
			_tcscpy_s(szLongName, _countof(szLongName), szModuleName);
		if(_tcslen(szShortName)==0)
			_tcscpy_s(szShortName, _countof(szShortName), szModuleName);

		WriteLog(_T("DEBUG                   compare file name: %s."), szModuleName);
		for(int i=0; i<strPrcArray.GetCount(); i++)
		{
			if(_tcsicmp(szShortName, strPrcArray[i])==0 
				|| _tcsicmp(szLongName, strPrcArray[i])==0)
			{
				dwPidArray.Add(pe32.th32ProcessID);
				WriteLog(_T("Process %d exist: %s"), pe32.th32ProcessID, szLongName);
				break;
			}
		}

		ZeroMemory(&pe32, sizeof(PROCESSENTRY32));
		pe32.dwSize = sizeof(PROCESSENTRY32);
			
		bMoreProcess = Process32Next(hProcessSnap, &pe32);
	}
	return TRUE;
}

BOOL KillProcess(DWORD dwProcessId)
{
    HANDLE            hProcess;

	hProcess = OpenProcess(PROCESS_TERMINATE, FALSE, dwProcessId);

	if (hProcess)
	{
		if (!TerminateProcess(hProcess, 1))
		{
			WriteLog(_T("Fail to terminate process pid=%d. LastError=%d"), dwProcessId, GetLastError());
			CloseHandle(hProcess);
			return FALSE;
		}
		else
		{
			WriteLog(_T("Success to kill process pid=%d"), dwProcessId);
		}
	}
	else
	{
		WriteLog(_T("Fail to open process to terminate it. LastError=%d"), GetLastError());
		return FALSE;
	}

    return TRUE;
}

void GetComponents(LPCTSTR lpctFile)
{
	if(!theApp.objComponents.GetSize() != 0)
	{
		for(int i = 0; i < theApp.objComponents.GetSize(); i++)
		{
			delete (CCAComponent*)theApp.objComponents[i];
		}
		theApp.objComponents.RemoveAll();
	}

	if (_taccess(lpctFile, 00) == -1)
	{
		TRACE(_T("GetComponents Could not find file: %s\n"), lpctFile);
		return;
	}

	const int MAX_BUFFER_SIZE = 1024;
	const CString strCompProductSec = _T("Ca component List");
	
	LPTSTR lpReturnedString = new TCHAR[MAX_BUFFER_SIZE];
	if (!::GetPrivateProfileSection(strCompProductSec, lpReturnedString, MAX_BUFFER_SIZE, lpctFile))
	{
		delete []lpReturnedString;
		return;
	}
	LPTSTR lpFilePos = lpReturnedString;
	TCHAR szSectionName[MAX_PATH];
	int nTemp = 0;
	while (*lpFilePos != _T('\0'))
	{
		_tcscpy_s(szSectionName, _countof(szSectionName), lpFilePos);
		lpFilePos = lpFilePos + _tcslen(lpFilePos) + 1;
		if (_tcslen(szSectionName))
		{
			//WriteLog(_T("GetComponents 22"));
			nTemp = SetupGetPrivateProfileInt(szSectionName, 
				TYPECOMPONENT,
				0,
				lpctFile);
			if(!nTemp)
			{
				continue;
			}
			CCAComponent* p = CCAComponent::CreateComponent(nTemp);
			if(p)
			{
				p->Initialize(lpctFile, szSectionName);
				if(p->CheckInstallStatus())
				{
					theApp.objComponents.Add(p);
				}
			}
		}
	}
}

DWORD getLastXmlDoc( IN const wstring &strSource, OUT wstring &strLastXml )
{
	wstring strXmlBegin = L"<?xml";
	wstring::size_type idx1 = 0;
	//HXMLDOCUMENT hDoc = NULL;

	idx1 = strSource.rfind( strXmlBegin );
	if( idx1 == wstring::npos ){
		return 1;
	}
	strLastXml = strSource.substr( idx1, strSource.length() - idx1 );
	
	return 0;
}

void RebootSystem()
{
	//Delay reboot machine 5 seconds for issue 12631962
	Sleep(5000);

	OSVERSIONINFO	osvi;
	osvi.dwOSVersionInfoSize = sizeof(OSVERSIONINFO);
	
	::GetVersionEx(&osvi);

	if (osvi.dwPlatformId == VER_PLATFORM_WIN32_NT)
		RebootWinnt();
	else
		::ExitWindowsEx(EWX_REBOOT, 0); 
}
void RebootWinnt()
{
	HANDLE hToken;              // handle to process token 
	TOKEN_PRIVILEGES tkp;       // pointer to token structure 

	// Get a token for this process. 
	if (!::OpenProcessToken(::GetCurrentProcess(), 
		TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hToken))
	{
		return;
	}
	// Get the LUID for the shutdown privilege. 

	::LookupPrivilegeValue(NULL, SE_SHUTDOWN_NAME, &tkp.Privileges[0].Luid); 

	tkp.PrivilegeCount = 1;  // one privilege to set    
	tkp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED; 

	// Get the shutdown privilege for this process. 
	::AdjustTokenPrivileges(hToken, FALSE, &tkp, 0, 
		(PTOKEN_PRIVILEGES) NULL, 0); 

	// Cannot test the return value of AdjustTokenPrivileges. 
	if (::GetLastError() != ERROR_SUCCESS)
	{
		return;
	}

	::ExitWindowsEx(EWX_REBOOT, 0);

	// Disable shutdown privilege. 
	tkp.Privileges[0].Attributes = 0; 
	
	::AdjustTokenPrivileges(hToken, FALSE, &tkp, 0, 
		(PTOKEN_PRIVILEGES) NULL, 0);
}