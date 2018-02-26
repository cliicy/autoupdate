#include "stdafx.h"
#include "ComAPI.h"
#include "..\..\RCModules\Setup\AsetupRes.dll\resource.h"
#include "ADRDefine.h"

#include <io.h>
#include <shlwapi.h>
#include <Psapi.h>
#include <tlhelp32.h>  
#include <atlbase.h>


#define MAX_BUF_SIZE          4096
#define MAX_MES_BUS			  2048
#define MAX_BUF_BUS			  2048
#define FILE_ACCESS_RETRY_COUNT_FLSHGUI	600

#define MAX_ARRAY_SIZE        100

#define SETUP_TIMEOUT_ERROR    100000

#define REGKEY_MICROSOFT_WINDOWS			_T("SOFTWARE\\Microsoft\\Windows\\CurrentVersion")

#define REGVALUE_PROGRAM_FILES_DIR			_T("ProgramFilesDir")

#define REGVALUE_PROGRAM_FILES_DIR_X86		_T("ProgramFilesDir (x86)")

#define PATH_UNINSTALL						DEFAULT_AGENT_LOG_SUBPATH _T("\\Uninstall\\")

#pragma comment( lib, "Psapi.lib")

TCHAR g_szLogPath[MAX_PATH];
TCHAR g_szLogFile[MAX_PATH];
typedef BOOL (WINAPI * pfn_GetProductInfo)(DWORD, DWORD, DWORD, DWORD, PDWORD);


///////////////////////////////////////////////////////////////////////////////////

//***********************************************************//
//***          please add non-custom action	here   	       **//
//***********************************************************//

void WriteTLog(LPCTSTR pszLogFileName,LOG_TYPE type,LPCTSTR pszFormat, ...)
{
	TCHAR szTempDir[MAX_PATH];
	TCHAR szTimeForFile[MAX_PATH];
	memset(szTempDir, 0, sizeof(szTempDir));
	memset(szTimeForFile, 0, sizeof(szTimeForFile));
	
	if (g_szLogPath != NULL && _taccess(g_szLogPath,0) == 0)
	{
		_stprintf_s(szTempDir,_countof(szTempDir),_T("%s"),g_szLogPath);
	}
	else
	{
		//first get the system temp
		::GetSystemWindowsDirectory(szTempDir, _countof(szTempDir));
		
		if (_tcslen(szTempDir) < 1)
		{
			//get the user temp if the system don't exist
			memset(szTempDir, 0, sizeof(szTempDir));
			::GetTempPath(MAX_PATH, szTempDir);
		}
		else
		{
			//get the system path
			PathAppend(szTempDir, _T("Temp"));
			PathAppend(szTempDir, PATH_UNINSTALL);
			MakeSurePathExists(szTempDir, FALSE);
		}
	}

	if (_tcslen(szTempDir) > 0)
	{
		TCHAR szMessage[MAX_BUF_SIZE];
		TCHAR szFinalMessage[MAX_BUF_SIZE];
		memset(szMessage, 0, sizeof(szMessage));
		memset(szFinalMessage, 0, sizeof(szFinalMessage));

		// Get current time
		//---------------------------------------------------------------
		CString sMessage;
		TCHAR szText[MAX_PATH];
		memset(szText, NULL, sizeof(szText));
		memset(szFinalMessage, NULL, sizeof(szFinalMessage));

		//-----------------------------------------------------------------

		TCHAR szTime[MAX_PATH];
		memset(szTime,0,sizeof(szTime));

		struct tm newTime;
		__time64_t long_time;

		// Get time as 64-bit integer.
		_time64(&long_time); 
		// Convert to local time.
		_localtime64_s(&newTime, &long_time);

		_stprintf_s(szTime,_countof(szTime),_T("%4d-%02d-%02d %02d:%02d:%02d"),
			newTime.tm_year+1900,newTime.tm_mon+1, newTime.tm_mday, newTime.tm_hour, 
			newTime.tm_min, newTime.tm_sec);

		_stprintf_s(szTimeForFile,_countof(szTimeForFile),_T("%4d%02d"),newTime.tm_year+1900,newTime.tm_mon+1);

		va_list arglist;
		va_start(arglist, pszFormat);
		_vstprintf_s(szMessage,_countof(szMessage), pszFormat, arglist);
		va_end(arglist);

		TCHAR szType[20];
		memset(szType,0,sizeof(szType));

		switch(type)
		{
		case LOG_INFO:
			_stprintf_s(szType,_countof(szType),_T("INFO   "));
			break;
		case LOG_WARNING:
			_stprintf_s(szType,_countof(szType),_T("WARNING"));
			break;
		case LOG_ERROR:
			_stprintf_s(szType,_countof(szType),_T("ERROR  "));
			break;
		}

		_sntprintf_s(szFinalMessage,_countof(szFinalMessage),_TRUNCATE,_T("%s - |%s| %s"),szTime,szType,szMessage);
		_tcscat_s(szFinalMessage, _countof(szFinalMessage), _T("\r\n"));

		TCHAR szTmpFileName[MAX_PATH + 1];
		ZeroMemory(szTmpFileName,sizeof(szTmpFileName));

		if (pszLogFileName == NULL || _tcslen(pszLogFileName) <=0 )
		{
			_stprintf_s(szTmpFileName, _countof(szTmpFileName), _T("%s"), szTempDir);
			PathAppend(szTmpFileName,LOG_FILE_DEFAULT_PREFIX);
		}
		else
		{
			_stprintf_s(szTmpFileName, _countof(szTmpFileName), _T("%s"), szTempDir);
			PathAppend(szTmpFileName,pszLogFileName);
		}
		
		TCHAR szLogFileName[MAX_PATH + 1];
		ZeroMemory(szLogFileName,sizeof(szLogFileName));
		_stprintf_s(szLogFileName, _countof(szLogFileName), _T("%s_%s%s"), szTmpFileName,szTimeForFile,LOG_FILE_EXT_LOG);

		//open  file
		HANDLE hFile = ::CreateFile(szLogFileName,
			GENERIC_WRITE,
			FILE_SHARE_WRITE,
			NULL,
			OPEN_EXISTING,
			FILE_ATTRIBUTE_NORMAL,
			NULL);

		// file does not exits
		DWORD dwPos,dwBytesWritten;
		if(hFile == NULL || hFile == INVALID_HANDLE_VALUE)
		{
			//create temp header file
			hFile = ::CreateFile(szLogFileName,
				GENERIC_WRITE,
				FILE_SHARE_WRITE,
				NULL,
				CREATE_NEW,
				FILE_ATTRIBUTE_NORMAL,
				NULL);

			//unicode
	#ifdef _UNICODE
			WORD wUnicode = 0xFEFF;
			dwPos = ::SetFilePointer( hFile, 0, NULL, FILE_END ); 
			::LockFile(hFile, dwPos, 0, sizeof(WORD), 0);
			::WriteFile(hFile, &wUnicode,sizeof(WORD) , &dwBytesWritten, NULL); 
			::UnlockFile(hFile, dwPos, 0, sizeof(WORD), 0);
	#endif
		}

		if (hFile == INVALID_HANDLE_VALUE)
		{
			return;
		}


		DWORD dwBytesToWrite = (DWORD) _tcslen(szFinalMessage)*sizeof(TCHAR);
		DWORD dwFileOffset = SetFilePointer(hFile, 0, NULL, FILE_END);
		LockFile(hFile, dwFileOffset, 0, dwFileOffset + dwBytesToWrite, 0);
		WriteFile(hFile, szFinalMessage, dwBytesToWrite, &dwBytesWritten, NULL);
		UnlockFile(hFile, dwFileOffset, 0, dwFileOffset + dwBytesToWrite, 0);

		CloseHandle(hFile);
	}
}

//write the log
DWORD SendErrorMessageToMSI(MSIHANDLE hInstall,LPCTSTR pszLogFileName,LOG_TYPE type,TCHAR *format,...)
{
	PMSIHANDLE hRecord;
	TCHAR szFinalMsgBuffer[MAX_BUF_SIZE];
	TCHAR szMsgBuffer[MAX_BUF_SIZE];

	memset(szMsgBuffer,0,sizeof(szMsgBuffer));
	memset(szFinalMsgBuffer,0,sizeof(szFinalMsgBuffer));

	va_list ap;
	va_start(ap, format); 
	int len = _vsctprintf(format, ap)+ 1; // // _vscprintf doesn't count,terminating '\0'
	if (len*sizeof(TCHAR) > MAX_BUF_SIZE)
	{
		//the log string is too long.
		_sntprintf_s(szMsgBuffer,_countof(szMsgBuffer),_TRUNCATE,_T("The input log string is too long. Ignore it(%s)."),format);
	}
	else
	{
		_vstprintf_s(szMsgBuffer,_countof(szMsgBuffer), format, ap); 
	}
	va_end(ap);


	if (hInstall != NULL)
	{
		//get the log path
		InitLogPath(hInstall);
	}
	
	//write it to temp
	WriteTLog(pszLogFileName,type,szMsgBuffer);

	if (hInstall == NULL)
	{
		return ERROR_SUCCESS;
	}

	TCHAR szTime[MAX_PATH];
	memset(szTime,0,sizeof(szTime));

	struct tm newTime;
	__time64_t long_time;

	// Get time as 64-bit integer.
	_time64(&long_time); 
	// Convert to local time.
	_localtime64_s(&newTime, &long_time);

	_stprintf_s(szTime,_countof(szTime),_T("%4d-%02d-%02d %02d:%02d:%02d"),
		newTime.tm_year+1900,newTime.tm_mon+1, newTime.tm_mday, newTime.tm_hour, 
		newTime.tm_min, newTime.tm_sec);

	
	_sntprintf_s(szFinalMsgBuffer,_countof(szFinalMsgBuffer),_TRUNCATE,_T("%s %s"),szTime,szMsgBuffer);

	hRecord = MsiCreateRecord(1);
	MsiRecordSetString(hRecord, 0, szFinalMsgBuffer);

	int rc = MsiProcessMessage(
		hInstall, 
		INSTALLMESSAGE(INSTALLMESSAGE_INFO), 
		hRecord);

	return rc;
}
//end write log

DWORD LaunchProcess(TCHAR *pCmdLine, 
					TCHAR *pWorkingDir, 
					DWORD *dwExitCode,
					DWORD dwTime/*=INFINITE*/,
					DWORD dwCreationFlags/*=CREATE_NO_WINDOW*/)
{
	DWORD dwRet = 0;
	STARTUPINFO	si = {0};
	PROCESS_INFORMATION pi = {0};

	si.cb = sizeof(STARTUPINFO);

	if (CreateProcess(NULL,
		pCmdLine,
		NULL,
		NULL,
		TRUE,
		dwCreationFlags,
		NULL, 
		pWorkingDir,  
		&si, 
		&pi))
	{	
		WaitForSingleObject(pi.hProcess, dwTime);
		GetExitCodeProcess(pi.hProcess, dwExitCode);
		CloseHandle (pi.hThread);
		CloseHandle (pi.hProcess);
	}
	else
		dwRet = GetLastError();
	return dwRet;
}

void ChangeShortCutFile(LPCTSTR lpctNumber, LPCTSTR lpctShortCutFile)
{
	const CString strKeyName = _T("InternetShortcut");
	const CString strValueName1 = _T("URL");
	const CString strValueName2 = _T("IconFile");

	if (_taccess(lpctShortCutFile, 0)==0)
	{
		CString strNewString;
		TCHAR szOldValue[MAX_PATH];

		ZeroMemory(szOldValue, sizeof(szOldValue));
		::GetPrivateProfileString(strKeyName, strValueName1, _T(""), szOldValue, _countof(szOldValue)-1, lpctShortCutFile);
		strNewString = szOldValue;
		strNewString.Replace(WEB_VALUE_PORT, lpctNumber);
		::WritePrivateProfileString(strKeyName, strValueName1, strNewString, lpctShortCutFile);

		ZeroMemory(szOldValue, sizeof(szOldValue));
		::GetPrivateProfileString(strKeyName, strValueName2, _T(""), szOldValue, _countof(szOldValue)-1, lpctShortCutFile);
		strNewString = szOldValue;
		strNewString.Replace(WEB_VALUE_PORT, lpctNumber);
		::WritePrivateProfileString(strKeyName, strValueName2, strNewString, lpctShortCutFile);
	}
}

BOOL InitLogPath(MSIHANDLE hInstall)
{
	TCHAR szFile[MAX_PATH];
	DWORD dwSize = _countof(szFile);
	memset(szFile,0,sizeof(szFile));

	MsiGetProperty(hInstall, PROP_ICFPATH, szFile, &dwSize);

	if (_taccess(szFile,0) == 0)
	{
		ZeroMemory(g_szLogPath, sizeof(g_szLogPath));
		::GetPrivateProfileString(ICF_SECTION_INSTALL, ICF_VALUE_LOG_DIR, _T(""), g_szLogPath, _countof(g_szLogPath)-1, szFile);

		BOOL bRes = (_taccess(g_szLogPath,0) != -1);

		if(!bRes)
		{
			//don't exist, create it
			bRes = MakeSurePathExists(g_szLogPath, FALSE);
		}

		if (bRes)
		{

			return TRUE;
		}
		
	}

	dwSize = _countof(g_szLogPath)-1;

	//get it from property if exist
	MsiGetProperty(hInstall, _T("LOGFOLDER"), g_szLogPath, &dwSize);

	if(_tcslen(g_szLogPath) > 1)
	{
		BOOL bRes = (_taccess(g_szLogPath,0) != -1);

		if(!bRes)
		{
			//don't exist, create it
			bRes = MakeSurePathExists(g_szLogPath, FALSE);
		}

		if (bRes)
		{

			return TRUE;
		}
	}

	return FALSE;
}

BOOL IsSilentMode(MSIHANDLE hInstall)
{
	TCHAR szUI[10];
	DWORD ccValue = sizeof(szUI)/sizeof(TCHAR);
	int nUI = 0;

	MsiGetProperty(hInstall, _T("UILevel"), szUI, &ccValue);

	nUI = _ttoi(szUI);

	return(nUI == INSTALLUILEVEL_NONE);
}

//get the registry value
BOOL GetRegValue(HKEY hKey, LPCTSTR lpSubKey, LPCTSTR lpValueName, CString & strReturnValue,DWORD dwType/*=REG_SZ*/)
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

	if (Is_64Bit_System(NULL))
	{
		samDesired = KEY_READ|KEY_WOW64_64KEY;
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

	if (Is_64Bit_System(NULL))
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
		// Don't open key '%s'
	}

	return bRet;
}

BOOL Is_64Bit_System(LPTSTR lpMachine)
{
	TCHAR szBuffer[MAX_PATH] = {0};
	TCHAR szData[MAX_PATH] = {0};
	DWORD dwBufSize = _countof(szBuffer);
	DWORD dwDataSize;
	DWORD dwType = REG_SZ;
	BOOL bIsLocal = TRUE;
	HKEY hConnectKey, hKey;

	if (lpMachine && ::GetComputerName(szBuffer, &dwBufSize))
	{
		if (_tcsnicmp(lpMachine, szBuffer, _countof(szBuffer)))
		{
			bIsLocal = FALSE;
		}
	}

	if (!bIsLocal)
	{
		if (::RegConnectRegistry(lpMachine, HKEY_LOCAL_MACHINE, &hConnectKey) != ERROR_SUCCESS)
			return FALSE;

		if (::RegOpenKeyEx(hConnectKey, REGKEY_MICROSOFT_WINDOWS, 0, KEY_READ, &hKey) != ERROR_SUCCESS)
			return FALSE;
	}
	else
	{

		if (::RegOpenKeyEx(HKEY_LOCAL_MACHINE,REGKEY_MICROSOFT_WINDOWS,0,KEY_READ,&hKey) != ERROR_SUCCESS)
			return FALSE;
	}
	dwDataSize = sizeof(szData);

	if (::RegQueryValueEx(hKey, _T("ProgramFilesDir (x86)"), NULL, NULL, (LPBYTE)szData, &dwDataSize)==ERROR_SUCCESS)
		return TRUE;

	return FALSE;
}


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

DWORD StopService(SC_HANDLE schService, LPCTSTR lpszServiceName, BOOL bBlockOperation, BOOL bStopDependents)
{
	DWORD dwRet = ERROR_SUCCESS;
	SERVICE_STATUS ServiceStatus;
	//DWORD dwLastCheckPoint;
	DWORD 	dwMaxWaitTime;

	WriteTLog(NULL,LOG_INFO, _T("StopSpecService: prepare to stop service(%s)."), lpszServiceName);

	QueryServiceStatus(schService, &ServiceStatus);

	if (ServiceStatus.dwCurrentState == SERVICE_STOPPED || ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING)
	{
		WriteTLog(NULL, LOG_ERROR, _T("StopSpecService: the service(%s) is stopped or stopping.  No need handle it."), lpszServiceName);
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
				dwTemp = GetLastError();
				WriteTLog(NULL, LOG_ERROR, _T("StopSpecService: Fail to stop service(%s) by calling ControlService(). Error:%d"), lpszServiceName,dwTemp);
				return dwTemp;
			}
		}
		else
		{
			WriteTLog(NULL, LOG_ERROR, _T("StopSpecService: Fail to stop service(%s) because of depneding on the other service."), lpszServiceName);
			return dwTemp;
		}
	}

	if (bBlockOperation)
	{ //Wait until service stops
		dwMaxWaitTime = 180; // Maximum wait time

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
			Sleep(2000); // Loop 1 second at a time

			if (!QueryServiceStatus(schService, &ServiceStatus))
			{
				dwRet = GetLastError();
				WriteTLog(NULL,LOG_WARNING,_T("StopService: Cannot get the status of the service(%s). Error:%d"),lpszServiceName,dwRet);
				break;
			}
			//if (dwLastCheckPoint >= ServiceStatus.dwCheckPoint)
			//   break;

			// Do not wait more than suggested
			if (dwMaxWaitTime <= 0)
			{
				dwRet = SETUP_TIMEOUT_ERROR;
				break;
			}

			dwMaxWaitTime--;
		}
	}
	else
	{
		SendErrorMessageToMSI(NULL,LOG_FILE_DEFAULT_PREFIX, LOG_INFO, _T("StopSpecService: The block flag is false, No need wait to stop service(%s)."), lpszServiceName);
	}


	//ISC 11/16/98: According to Chie's request to return TRUE only when STOPPED
	//return ServiceStatus.dwCurrentState == SERVICE_STOPPED || ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING;
	if(ServiceStatus.dwCurrentState == SERVICE_STOPPED)
	{
		WriteTLog(NULL,LOG_INFO,_T("StopService: stop the service(%s) successfully."),lpszServiceName);
	}
	else if(ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING)
	{
		WriteTLog(NULL, LOG_INFO, _T("StopSpecService: the service(%s) is still stopping. its satatus is SERVICE_STOP_PENDING."), lpszServiceName);
		return ERROR_SUCCESS;
	}
	else
	{
		WriteTLog(NULL,LOG_WARNING,_T("StopService: fail to stop the service(%s). Its status is %d"),lpszServiceName,ServiceStatus.dwCurrentState);
	}

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
		dwRet = GetLastError();
		WriteTLog(NULL,LOG_INFO, _T("StopSpecService: Fail to call OpenSCManager() for service(%s). Error:%d"), lpszServiceName,dwRet);
		return dwRet;
	}

	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService == NULL)
	{
		dwRet = GetLastError();
		WriteTLog(NULL,LOG_INFO, _T("StopSpecService: Fail to call OpenService() for service(%s). Error:%d"), lpszServiceName,dwRet);
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

	WriteTLog(NULL,LOG_INFO, _T("StopSpecService: return code(%d)"),dwRet);
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

BOOL ChangeServiceDisplayName(LPCTSTR lpctServiceName, LPCTSTR lpctDisplayName)
{
	BOOL bRet = FALSE;

	if (lpctServiceName && lpctDisplayName && _tcslen(lpctServiceName)>0 && _tcslen(lpctDisplayName)>0)
	{
		SC_HANDLE schSCManager;
		SC_HANDLE schService;

		// Get a handle to the SCM database. 
		schSCManager = OpenSCManager( 
			NULL,                    // local computer
			NULL,                    // ServicesActive database 
			SC_MANAGER_ALL_ACCESS);  // full access rights 

		if (NULL == schSCManager) 
		{
			return bRet;
		}

		// Get a handle to the service.
		schService = OpenService( 
			schSCManager,            // SCM database 
			lpctServiceName,               // name of service 
			SERVICE_CHANGE_CONFIG);  // need change config access 

		if (schService == NULL)
		{ 
			CloseServiceHandle(schSCManager);

			return bRet;
		}    

		// Change the service display name.
		bRet = ChangeServiceConfig(schService, SERVICE_NO_CHANGE,	SERVICE_NO_CHANGE, SERVICE_NO_CHANGE, NULL, NULL, NULL, NULL, NULL, NULL, lpctDisplayName);

		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
	}

	return bRet;
}

BOOL ChangeServiceDescription(LPCTSTR lpctServiceName, LPCTSTR lpctDescription)
{
	BOOL bRet = FALSE;

	if (lpctServiceName && lpctDescription && _tcslen(lpctServiceName)>0 && _tcslen(lpctDescription)>0)
	{
		SC_HANDLE schSCManager;
		SC_HANDLE schService;
		SERVICE_DESCRIPTION sd;

		// Get a handle to the SCM database. 
		schSCManager = OpenSCManager( 
			NULL,                    // local computer
			NULL,                    // ServicesActive database 
			SC_MANAGER_ALL_ACCESS);  // full access rights 

		if (NULL == schSCManager) 
		{
			return bRet;
		}

		// Get a handle to the service.

		schService = OpenService( 
			schSCManager,            // SCM database 
			lpctServiceName,               // name of service 
			SERVICE_CHANGE_CONFIG);  // need change config access 

		if (schService == NULL)
		{ 
			CloseServiceHandle(schSCManager);
			return bRet;
		}    

		// Change the service description.

		sd.lpDescription = (LPTSTR)lpctDescription;

		bRet = ChangeServiceConfig2(schService, SERVICE_CONFIG_DESCRIPTION, &sd);

		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
	}

	return bRet;
}

BOOL ChangeSvcFailureActions(LPCTSTR lpszServiceName, UINT uFirstAction, UINT uSecondAction, UINT uThirdAction, DWORD dwDelay)
{
	if (NULL == lpszServiceName)
		return FALSE;

	BOOL bRet = TRUE;
	SC_HANDLE schSCManager = NULL;
	SC_HANDLE schService = NULL;
	SERVICE_STATUS ServiceStatus;
	ZeroMemory(&ServiceStatus, sizeof(SERVICE_STATUS));

	SC_ACTION SCActionArray[3];
	ZeroMemory(SCActionArray, sizeof(SC_ACTION));

	SCActionArray[0].Delay = dwDelay;
	SCActionArray[0].Type = (SC_ACTION_TYPE)uFirstAction;

	SCActionArray[1].Delay = dwDelay;
	SCActionArray[1].Type = (SC_ACTION_TYPE)uSecondAction;

	SCActionArray[2].Delay = dwDelay;
	SCActionArray[2].Type = (SC_ACTION_TYPE)uThirdAction;

	SERVICE_FAILURE_ACTIONS SvcFailureAction;
	ZeroMemory(&SvcFailureAction, sizeof(SERVICE_FAILURE_ACTIONS));

	SvcFailureAction.dwResetPeriod = 604800;
	SvcFailureAction.cActions = 3;
	SvcFailureAction.lpsaActions = SCActionArray;

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

	//change the service failure action
	if (0 == ChangeServiceConfig2(schService, SERVICE_CONFIG_FAILURE_ACTIONS, &SvcFailureAction))
	{
		bRet = FALSE;
		goto End;
	}

End:
	if (NULL != schService)
		CloseServiceHandle(schService);

	if (NULL != schSCManager)
		CloseServiceHandle(schSCManager);

	return bRet;
}
/******************************end for service*************************************/

/******************************start for process*************************************/

typedef DWORD (WINAPI *PGetProcessImageFileName)(HANDLE hProcess,LPTSTR lpImageFileName,DWORD nSize);

/*++
Routine Description:

Provides an API for getting a list of tasks running at the time of the
API call.  This function uses the registry performance data to get the
task list and is therefore straight WIN32 calls that anyone can call.

Arguments:

dwNumTasks       - maximum number of tasks that the pTask array can hold

Return Value:

Number of tasks placed into the pTask array.
--*/
DWORD GetTaskListNT(PTASK_LIST pTask,DWORD dwNumTasks, BOOL bOutputAllProcListTolog)
{
	HANDLE hProcessSnap = NULL;
	DWORD dwTempNum = 0;//the number of the process write into the task list
	PROCESSENTRY32 pe32;
	LUID luId;
	HANDLE hToken;
	TOKEN_PRIVILEGES tkpNew;
	TOKEN_PRIVILEGES tkpPrev;
	DWORD dwPrevLen = sizeof(tkpPrev);

	//if the buff length is 0, then return 0;
	if (dwNumTasks <= 0)
		return 0;

	ZeroMemory(&pe32, sizeof(PROCESSENTRY32));
	pe32.dwSize = sizeof(PROCESSENTRY32);

	//clear the input buffer first
	ZeroMemory(pTask, sizeof(PTASK_LIST)*dwNumTasks);

	hProcessSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
	if (NULL == hProcessSnap)//if we can not get the Help32 snapshot, then return 0;
		return 0;

	
	if (OpenProcessToken(GetCurrentProcess(), TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hToken))
	{
		if (LookupPrivilegeValue(NULL, SE_DEBUG_NAME, &luId))
		{
			tkpNew.PrivilegeCount = 1;
			tkpNew.Privileges[0].Luid = luId;
			tkpNew.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;

			if (AdjustTokenPrivileges(hToken, FALSE, &tkpNew, sizeof(TOKEN_PRIVILEGES), &tkpPrev, &dwPrevLen))
			{

				if (Process32First(hProcessSnap,&pe32))
				{
					do
					{
						ZeroMemory(pTask->szExePath,sizeof(pTask->szExePath));

						//the process name
						if (_tcslen(pe32.szExeFile) <= PROCESS_SIZE)
						{
							_tcscpy_s(pTask->ProcessName,_countof(pTask->ProcessName), pe32.szExeFile);
						}

						//get the path
						HANDLE hProcess = OpenProcess( PROCESS_QUERY_INFORMATION |
							PROCESS_VM_READ,
							FALSE, pe32.th32ProcessID);

						if (NULL != hProcess)
						{
							if (Is_64Bit_System(NULL))
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
										(*lpGetProcessImageFileName)(hProcess, pTask->szExePath, MAX_PATH);
									}
									else
									{
										WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_WARNING,_T("Can not call the API GetProcessImageFileName for %s."),pe32.szExeFile);
									}
								}
								else
								{
									WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_WARNING,_T("Can not loadlibrary (Psapi.dll) for process (%s). Error:%d"),pe32.szExeFile,::GetLastError());
								}
							}
							else
							{
								GetModuleFileNameEx(hProcess, NULL, pTask->szExePath, MAX_PATH);
					
							}

							CloseHandle(hProcess);
						}
						else
						{
							WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_WARNING,_T("Can not Open process (%s). Error:%d"),pe32.szExeFile,::GetLastError());
						}

						if(bOutputAllProcListTolog)
						{
							WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_INFO,_T("Process: ID=%d; Name=%s, Path=%s"),pe32.th32ProcessID,pe32.szExeFile,pTask->szExePath);
						}

						//end path
						//the process ID
						pTask->flags = 0;
						pTask->dwProcessId = pe32.th32ProcessID;
						if (pTask->dwProcessId == 0)
						{
							pTask->dwProcessId = (DWORD)-2;
						}

						//clear the temp PROCESSENTRY32 again
						ZeroMemory(&pe32, sizeof(PROCESSENTRY32));
						pe32.dwSize = sizeof(PROCESSENTRY32);

						++pTask;
						++dwTempNum;
					}
					while(Process32Next(hProcessSnap,&pe32) && dwTempNum < dwNumTasks);
				}

				AdjustTokenPrivileges(hToken, FALSE, &tkpPrev, sizeof(TOKEN_PRIVILEGES), NULL, NULL);
			}
			else
			{
				LPVOID lpMsgBuf;
				TCHAR  szTempMesg[MAX_BUF]={0};
				FormatMessage(
					FORMAT_MESSAGE_ALLOCATE_BUFFER | 
					FORMAT_MESSAGE_FROM_SYSTEM | 
					FORMAT_MESSAGE_IGNORE_INSERTS,
					NULL,
					GetLastError(),
					MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
					(LPTSTR) &lpMsgBuf,
					0,
					NULL 
					);

				WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_WARNING,_T("GetTaskListNT: Fail to AdjustTokenPrivileges(%s)."),(LPCTSTR)lpMsgBuf);
				//Free the buffer.
				LocalFree(lpMsgBuf);
			}
		}
		else
		{
			LPVOID lpMsgBuf;
			TCHAR  szTempMesg[MAX_BUF]={0};
			FormatMessage(
				FORMAT_MESSAGE_ALLOCATE_BUFFER | 
				FORMAT_MESSAGE_FROM_SYSTEM | 
				FORMAT_MESSAGE_IGNORE_INSERTS,
				NULL,
				GetLastError(),
				MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
				(LPTSTR) &lpMsgBuf,
				0,
				NULL 
				);

			WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_WARNING,_T("GetTaskListNT: fail to LookupPrivilegeValue(%s)."),(LPCTSTR)lpMsgBuf);
			// Free the buffer.
			LocalFree(lpMsgBuf);
		}
	}
	else
	{
		LPVOID lpMsgBuf;
		TCHAR  szTempMesg[MAX_BUF]={0};
		FormatMessage(
			FORMAT_MESSAGE_ALLOCATE_BUFFER | 
			FORMAT_MESSAGE_FROM_SYSTEM | 
			FORMAT_MESSAGE_IGNORE_INSERTS,
			NULL,
			GetLastError(),
			MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
			(LPTSTR) &lpMsgBuf,
			0,
			NULL 
			);

		WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_WARNING,_T("GetTaskListNT: fail to OpenProcessToken(%s)."),(LPCTSTR)lpMsgBuf);
		// Free the buffer.
		LocalFree(lpMsgBuf);
	}

	CloseHandle(hProcessSnap);

	return dwTempNum;
}

//check if the process is running (if szPath is NULL, will ignore it)
UINT IsProcessRunning(LPTSTR szKillProc, LPTSTR szFilePath, BOOL bOutputAllProcListTolog /*= FALSE*/)
{
	TASK_LIST *tlist = new TASK_LIST [MAX_TASKS];
	TASK_LIST_ENUM te;
	CString strPath;
	CString strFilePath = szFilePath;
	TCHAR szFileShortPath[MAX_PATH];
	memset(szFileShortPath,0,sizeof(szFileShortPath));

	DWORD numTasks,i;
	DWORD ThisPid;
	UINT nCount = 0;

	
	WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_INFO,_T("/******************* IsProcessRunning Start (Process: Name=%s, Path=%s) *********************/"),szKillProc,szFilePath);

	if (_tcslen(szKillProc) < 1)
	{
		return FALSE;
	}


	GetShortPathName(szFilePath,szFileShortPath,MAX_PATH);
	CString strFileShortPath = szFileShortPath;

	numTasks = GetTaskListNT(tlist, MAX_TASKS,bOutputAllProcListTolog);

	
	//
	// enumerate all windows and try to get the window
	// titles for each task
	//
	te.tlist = tlist;
	te.numtasks = numTasks;

	ThisPid = GetCurrentProcessId();

	for(i = 0; i < numTasks; i++) 
	{
		//
		// this prevents the user from killing KILL.EXE and
		// it's parent cmd window too
		//
		if (ThisPid == tlist[i].dwProcessId) 
		{
			continue;
		}

		if (_tcsnicmp(tlist[i].ProcessName, szKillProc, _countof(tlist[i].ProcessName)) == 0) 
		{
			//check the path
			strPath = tlist[i].szExePath;
			if (Is_64Bit_System(NULL))
			{
				//the path is like "\device\xxx\"
				if (strFilePath.IsEmpty())
				{
					nCount++;
					continue;
				}

				//remove the driver
				CString strFilePathTemp = strFilePath.Right(strFilePath.GetLength() - 2);
				CString strFileShortPathTemp = strFileShortPath.Right(strFileShortPath.GetLength() - 2);
				if (strPath.Find(strFilePathTemp) >= 0 || (!strFileShortPathTemp.IsEmpty() && strPath.Find(strFileShortPathTemp) >= 0))
				{
					nCount++;
				}
			}
			else
			{
				if (NULL == szFilePath ||
					strPath.CompareNoCase(szFilePath) == 0 ||
					(!strFileShortPath.IsEmpty() && strPath.CompareNoCase(strFileShortPath) == 0)
					)
				{
					nCount++;
					continue;
				}
			}
		}
	}

	if (tlist != NULL)
	{
		delete [] tlist;
	}

	if(nCount > 0)
	{
		WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_INFO,_T("The process (Name=%s, Path=%s, Count=%d) is running."),szKillProc,szFilePath,nCount);
	}
	else
	{
		WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_INFO,_T("The process (Name=%s, Path=%s) is NOT running."),szKillProc,szFilePath);
	}

	WriteTLog(LOG_FILE_DEFAULT_PREFIX,LOG_INFO,_T("/******************* IsProcessRunning End (Process: Name=%s, Path=%s) *********************/"),szKillProc,szFilePath);


	return nCount;
}

//kill process
DWORD KillProcess(DWORD dwId,LPCTSTR pszLogFileName)
{
	LUID luId;
	HANDLE hToken;
	HANDLE hProc;
	TOKEN_PRIVILEGES tkpNew;
	TOKEN_PRIVILEGES tkpPrev;
	DWORD dwPrevLen = sizeof(tkpPrev);
	DWORD dwRet = ERROR_SUCCESS;

	if (OpenProcessToken(GetCurrentProcess(), TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hToken))
	{
		if (LookupPrivilegeValue(NULL, SE_DEBUG_NAME, &luId))
		{
			tkpNew.PrivilegeCount = 1;
			tkpNew.Privileges[0].Luid = luId;
			tkpNew.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;

			if (AdjustTokenPrivileges(hToken, FALSE, &tkpNew, sizeof(TOKEN_PRIVILEGES), &tkpPrev, &dwPrevLen))
			{
				hProc = OpenProcess(PROCESS_ALL_ACCESS, FALSE, dwId);

				if (hProc)
				{
					if (TerminateProcess(hProc, 0))
					{
						//TerminateProcess initiates termination and returns immediately. So it don't make sure that this process is terminated normally.
						DWORD dwSleepTime = 120000; //2 mininutes timeout
						DWORD dwRetKillProcess = WaitForSingleObject(hProc,dwSleepTime);
						WriteTLog(pszLogFileName,LOG_INFO,_T("KillProcess: return code (0x%x) for process id (%d) by calling WaitForSingleObject API."),dwRetKillProcess,dwId);
						if(dwRetKillProcess == WAIT_TIMEOUT)
						{
							//timeout
							dwRet = WAIT_TIMEOUT;
							WriteTLog(pszLogFileName,LOG_WARNING,_T("KillProcess: Call API TerminateProcess() successfully. but the process(%d) doesn't exit within timeout(%ds)."),dwId,(dwSleepTime/1000));
						}
						else if( WAIT_OBJECT_0 == dwRetKillProcess)
						{
							//success
							dwRet = ERROR_SUCCESS;
						}
						else
						{
							dwRet = GetLastError();
							LPVOID lpMsgBuf;
							TCHAR  szTempMesg[MAX_BUF]={0};
							FormatMessage(
								FORMAT_MESSAGE_ALLOCATE_BUFFER | 
								FORMAT_MESSAGE_FROM_SYSTEM | 
								FORMAT_MESSAGE_IGNORE_INSERTS,
								NULL,
								dwRet,
								MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
								(LPTSTR) &lpMsgBuf,
								0,
								NULL 
								);
										
							WriteTLog(pszLogFileName,LOG_WARNING,_T("KillProcess: Call API TerminateProcess() successfully. but fail to call WaitForSingleObject. Error:%s"),(LPCTSTR)lpMsgBuf);
					
							//Free the buffer.
							LocalFree(lpMsgBuf);
						}
					}
					else
					{
						dwRet = GetLastError();
						LPVOID lpMsgBuf;
						TCHAR  szTempMesg[MAX_BUF]={0};
						FormatMessage(
							FORMAT_MESSAGE_ALLOCATE_BUFFER | 
							FORMAT_MESSAGE_FROM_SYSTEM | 
							FORMAT_MESSAGE_IGNORE_INSERTS,
							NULL,
							dwRet,
							MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
							(LPTSTR) &lpMsgBuf,
							0,
							NULL 
							);

						WriteTLog(pszLogFileName,LOG_WARNING,_T("KillProcess: fail to terminate the process id(%d) by calling TerminateProcess API. Error:%s"),dwId,(LPCTSTR)lpMsgBuf);
						//Free the buffer.
						LocalFree(lpMsgBuf);
					}

					CloseHandle(hProc);
				}
				else
				{
					dwRet = GetLastError();
						LPVOID lpMsgBuf;
						TCHAR  szTempMesg[MAX_BUF]={0};
						FormatMessage(
							FORMAT_MESSAGE_ALLOCATE_BUFFER | 
							FORMAT_MESSAGE_FROM_SYSTEM | 
							FORMAT_MESSAGE_IGNORE_INSERTS,
							NULL,
							dwRet,
						MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
						(LPTSTR) &lpMsgBuf,
						0,
						NULL 
						);

					WriteTLog(pszLogFileName,LOG_WARNING,_T("KillProcess: fail to open the process id(%d) by calling OpenProcess API. Error:%s"),dwId,(LPCTSTR)lpMsgBuf);
					//Free the buffer.
					LocalFree(lpMsgBuf);
				}

				AdjustTokenPrivileges(hToken, FALSE, &tkpPrev, sizeof(TOKEN_PRIVILEGES), NULL, NULL);
			}
			else
			{
				dwRet = GetLastError();
						LPVOID lpMsgBuf;
						TCHAR  szTempMesg[MAX_BUF]={0};
						FormatMessage(
							FORMAT_MESSAGE_ALLOCATE_BUFFER | 
							FORMAT_MESSAGE_FROM_SYSTEM | 
							FORMAT_MESSAGE_IGNORE_INSERTS,
							NULL,
							dwRet,
					MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
					(LPTSTR) &lpMsgBuf,
					0,
					NULL 
					);

				WriteTLog(pszLogFileName,LOG_WARNING,_T("KillProcess: Fail to AdjustTokenPrivileges(%s)."),(LPCTSTR)lpMsgBuf);
				//Free the buffer.
				LocalFree(lpMsgBuf);
			}
		}
		else
		{
			dwRet = GetLastError();
						LPVOID lpMsgBuf;
						TCHAR  szTempMesg[MAX_BUF]={0};
						FormatMessage(
							FORMAT_MESSAGE_ALLOCATE_BUFFER | 
							FORMAT_MESSAGE_FROM_SYSTEM | 
							FORMAT_MESSAGE_IGNORE_INSERTS,
							NULL,
							dwRet,
				MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
				(LPTSTR) &lpMsgBuf,
				0,
				NULL 
				);

			WriteTLog(pszLogFileName,LOG_WARNING,_T("KillProcess: fail to LookupPrivilegeValue(%s)."),(LPCTSTR)lpMsgBuf);
			// Free the buffer.
			LocalFree(lpMsgBuf);
		}
	}
	else
	{
		dwRet = GetLastError();
						LPVOID lpMsgBuf;
						TCHAR  szTempMesg[MAX_BUF]={0};
						FormatMessage(
							FORMAT_MESSAGE_ALLOCATE_BUFFER | 
							FORMAT_MESSAGE_FROM_SYSTEM | 
							FORMAT_MESSAGE_IGNORE_INSERTS,
							NULL,
							dwRet,
			MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
			(LPTSTR) &lpMsgBuf,
			0,
			NULL 
			);

		WriteTLog(pszLogFileName,LOG_WARNING,_T("KillProcess: fail to OpenProcessToken(%s)."),(LPCTSTR)lpMsgBuf);
		// Free the buffer.
		LocalFree(lpMsgBuf);
	}

	return dwRet;
}

BOOL KillProcesses(LPTSTR szKillProc, LPTSTR szFilePath)
{
	return KillProcessesEx(szKillProc,szFilePath,NULL);
}

//find and kill (if szPath is NULL, will ignore it)

BOOL KillProcessesEx(LPTSTR szKillProc, LPTSTR szFilePath,LPCTSTR pszLogFileName)
{
	BOOL bRet = FALSE;
	DWORD numTasks;
	TASK_LIST *tlist = new TASK_LIST [MAX_TASKS];
	int i; 
	CString strPath;
	CString strFilePath = szFilePath;

	TCHAR szFileShortPath[MAX_PATH];
	memset(szFileShortPath, 0, sizeof(szFileShortPath));

	if (_tcslen(szKillProc) < 1)
	{
		return FALSE;
	}

	
	GetShortPathName(szFilePath, szFileShortPath, MAX_PATH);
	CString strFileShortPath = szFileShortPath;

	numTasks = GetTaskListNT(tlist, MAX_TASKS,FALSE);

	for(i=0; i<(int)numTasks; i++)
	{
		if (_tcsnicmp(tlist[i].ProcessName, szKillProc, _countof(tlist[i].ProcessName))==0)
		{
			//check the path
			strPath = tlist[i].szExePath;

			if (Is_64Bit_System(NULL))
			{
				//the path is like "\device\xxx\"
				if (strFilePath.IsEmpty())
				{
					WriteTLog(pszLogFileName,LOG_INFO,_T("KillProcesses1: start to terminate the process (ID:%d,Name:%s,Path:%s)."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					if (ERROR_SUCCESS == KillProcess(tlist[i].dwProcessId,pszLogFileName))
					{
						bRet = TRUE;
						WriteTLog(pszLogFileName,LOG_INFO,_T("KillProcesses1: terminate the process (ID:%d,Name:%s,Path:%s) successfully."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					}
					else
					{
						WriteTLog(pszLogFileName,LOG_WARNING,_T("KillProcesses1: fail to terminate the process (ID:%d,Name:%s,Path:%s)."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					}
					WriteTLog(pszLogFileName,LOG_INFO,_T("KillProcesses1: end to terminate the process (ID:%d,Name:%s,Path:%s)."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					continue;
				}

				//remove the driver
				CString strFilePathTemp = strFilePath.Right(strFilePath.GetLength() - 2);
				CString strFileShortPathTemp = strFileShortPath.Right(strFileShortPath.GetLength() - 2);
				if (strPath.Find(strFilePathTemp) >= 0 || (!strFileShortPathTemp.IsEmpty() && strPath.Find(strFileShortPathTemp) >= 0))
				{
					WriteTLog(pszLogFileName,LOG_INFO,_T("KillProcesses2: start to terminate the process (ID:%d,Name:%s,Path:%s)."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					if (ERROR_SUCCESS == KillProcess(tlist[i].dwProcessId,pszLogFileName))
					{
						bRet = TRUE;
						WriteTLog(pszLogFileName,LOG_INFO,_T("KillProcesses2: terminate the process (ID:%d,Name:%s,Path:%s) successfully."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					}
					else
					{
						WriteTLog(pszLogFileName,LOG_WARNING,_T("KillProcesses2: fail to terminate the process (ID:%d,Name:%s,Path:%s)."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					}

					WriteTLog(pszLogFileName,LOG_INFO,_T("KillProcesses2: end to terminate the process (ID:%d,Name:%s,Path:%s)."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					continue;
				}
			}
			else
			{
				if (NULL == szFilePath || 
					strPath.CompareNoCase(szFilePath) == 0 ||
					(!strFileShortPath.IsEmpty() && strPath.CompareNoCase(strFileShortPath) == 0)
					)
				{
					WriteTLog(pszLogFileName,LOG_INFO,_T("KillProcesses3: start to terminate the process (ID:%d,Name:%s,Path:%s)."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					if (ERROR_SUCCESS == KillProcess(tlist[i].dwProcessId,pszLogFileName))
					{
						bRet = TRUE;
						WriteTLog(pszLogFileName,LOG_INFO,_T("KillProcesses3: terminate the process (ID:%d,Name:%s,Path:%s) successfully."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					}
					else
					{
						WriteTLog(pszLogFileName,LOG_WARNING,_T("KillProcesses3: fail to terminate the process (ID:%d,Name:%s,Path:%s)."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
					}
					WriteTLog(pszLogFileName,LOG_INFO,_T("KillProcesses3: start to terminate the process (ID:%d,Name:%s,Path:%s)."),tlist[i].dwProcessId,tlist[i].ProcessName,tlist[i].szExePath);
				}

				continue;
			}
		}
	}

	if (tlist != NULL)
	{
		delete [] tlist;
	}

	return bRet;
}

/******************************end for process*************************************/

BOOL CopyFilesRecursive(MSIHANDLE hInstall,LPCTSTR lpszSrcPath, LPCTSTR lpszDesPath)
{
	WIN32_FIND_DATA FindData;
	HANDLE			hFile = NULL;
	TCHAR           szNewSrcPath[_MAX_PATH] = {0};
	TCHAR           wild[] = _T("*.*");
	TCHAR           szNewDesPath[_MAX_PATH] = {0};
	BOOL			bRet = TRUE;

	const int		dMsgLen	= 256;
	TCHAR			szMessage[MAX_PATH*2] = {0};
	CString			sMsg;

	_stprintf_s(szNewSrcPath, _countof(szNewSrcPath),_T("%s"), lpszSrcPath);
	PathAppend(szNewSrcPath,wild);
	if ( (hFile = FindFirstFile(szNewSrcPath, &FindData)) != INVALID_HANDLE_VALUE )
	{
		do
		{
			_stprintf_s(szNewSrcPath, _countof(szNewSrcPath),_T("%s"), lpszSrcPath);
			PathAppend(szNewSrcPath,FindData.cFileName);

			_stprintf_s(szNewDesPath, _countof(szNewDesPath),_T("%s"), lpszDesPath);
			PathAppend(szNewDesPath,FindData.cFileName);


			if ( !(FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) )
			{
				if (ERROR_SUCCESS != CreateDirectoryRecursively(lpszDesPath))
				{

					SendErrorMessageToMSI(hInstall,NULL, LOG_WARNING, _T("CopyFilesRecursive:fail to create the folder(%s)"), (LPTSTR)lpszDesPath);
					FindClose(hFile);
					return FALSE;
				}

				if(_taccess(szNewDesPath,0) == 0)
				{
					SetFileAttributes(szNewDesPath,FILE_ATTRIBUTE_NORMAL);
					DeleteFile(szNewDesPath);
					SendErrorMessageToMSI(hInstall,NULL, LOG_WARNING, _T("CopyFilesRecursive: The file(%s) exist, Need overwrite it."), szNewDesPath);
				}

				if ( !CopyFile(szNewSrcPath, szNewDesPath, FALSE ) )
				{
					LPVOID lpMsgBuf;
					DWORD dwRet = GetLastError();
					FormatMessage( 
						FORMAT_MESSAGE_ALLOCATE_BUFFER | 
						FORMAT_MESSAGE_FROM_SYSTEM | 
						FORMAT_MESSAGE_IGNORE_INSERTS,
						NULL,
						dwRet,
						0, // Default language
						(LPTSTR) &lpMsgBuf,
						0,
						NULL 
						);

					memset(szMessage,0,sizeof(szMessage));
					_sntprintf_s(szMessage,_countof(szMessage),_TRUNCATE,_T("%d,%s"),dwRet,lpMsgBuf);
					SendErrorMessageToMSI(hInstall,NULL, LOG_WARNING, _T("CopyFilesRecursive: fail to copy file from (%s) to (%s). Error:%s."),szNewSrcPath,szNewDesPath,szMessage);	
					// Free the buffer.
					LocalFree( lpMsgBuf );	
				}
			}
			else if ( lstrcmp(FindData.cFileName, _T("."))  != 0 &&
				lstrcmp(FindData.cFileName, _T("..")) != 0 &&
				(FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) )
			{

				if (ERROR_SUCCESS != CreateDirectoryRecursively(szNewDesPath))
				{

					SendErrorMessageToMSI(hInstall,NULL, LOG_WARNING, _T("CopyFilesRecursive:fail to create the folder(%s)"), (LPTSTR)szNewDesPath);
					FindClose(hFile);
					return FALSE;
				}

				if (!CopyFilesRecursive(hInstall,szNewSrcPath, szNewDesPath))
				{
					SendErrorMessageToMSI(hInstall,NULL, LOG_WARNING, _T("CopyFilesRecursive: fail to copy files under(%s)"),szNewSrcPath);
					FindClose(hFile);
					return FALSE;
				}
			}
		} while( FindNextFile(hFile, &FindData) == TRUE );

		FindClose(hFile);
	}
	else
	{
		SendErrorMessageToMSI(hInstall,NULL, LOG_WARNING, _T("CopyFilesRecursive: cannot access the source path(%s)"),lpszSrcPath);
		return FALSE;
	}

	return TRUE;
}

DWORD CreateDirectoryRecursively(LPCTSTR lpPath)
{ 
	SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("CreateDirectoryRecursively: Start(%s)"),lpPath);

	TCHAR *pNext_token = NULL;
	TCHAR szBuffer[MAX_PATH];
	TCHAR szNewPath[MAX_PATH];

	memset(szBuffer,0,sizeof(szBuffer));
	memset(szNewPath,0,sizeof(szNewPath));

	if (lpPath == NULL || _tcslen(lpPath) ==0)
	{
		return(-1);
	}

	if (_taccess((TCHAR*)lpPath,0) == 0)
	{
		//path exist
		return ERROR_SUCCESS;
	}

	_stprintf_s(szNewPath, _countof(szNewPath), _T("%s"), lpPath);


	TCHAR *p = _tcstok_s(szNewPath, _T("\\"), &pNext_token);

	if (p)
	{
		_stprintf_s(szBuffer, _countof(szBuffer), _T("%s"), p);
	}

	while (p)
	{ 
		p = _tcstok_s(pNext_token, _T("\\"),&pNext_token);

		if (p)
		{ 
			PathAppend(szBuffer,p);

			if (_taccess((TCHAR*)szBuffer,0) == 0)
			{
				//path exist
				continue;
			}

			if (!CreateDirectory((TCHAR*)szBuffer, NULL))
			{
				LPVOID lpMsgBuf;
				DWORD dw = GetLastError(); 

				FormatMessage(
					FORMAT_MESSAGE_ALLOCATE_BUFFER | 
					FORMAT_MESSAGE_FROM_SYSTEM |
					FORMAT_MESSAGE_IGNORE_INSERTS,
					NULL,
					dw,
					MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
					(LPTSTR) &lpMsgBuf,
					0, NULL );

				SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("CreateDirectoryRecursively: fail to create the folder(%s),Error:%d,%s"),(TCHAR*)szBuffer,dw,lpMsgBuf);
				SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("CreateDirectoryRecursively: End"));
				LocalFree(lpMsgBuf);
				return -1;
			}
		}   
	}

	SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("CreateDirectoryRecursively: End"));
	return ERROR_SUCCESS;
}

/***************************************************************************/
/*This function deletes all files and sub-directories of a directory
/***************************************************************************/
BOOL DeleteTree(LPCTSTR lpszPath, LPCTSTR pszLogFileName)
{
	WIN32_FIND_DATA FindData;
	TCHAR szPath[MAX_PATH] = {0};
	TCHAR szNewPath[_MAX_PATH] = {0};
	TCHAR wild[] = _T("*.*");
	HANDLE hFile = NULL;
	BOOL bDeleteTree = TRUE;

	swprintf_s(szPath, _countof(szPath), _T("%s"), lpszPath);
	PathAddBackslash(szPath);

	swprintf_s(szNewPath, _countof(szNewPath), _T("%s%s"), szPath, wild);

	if ((hFile = FindFirstFile(szNewPath, &FindData)) != INVALID_HANDLE_VALUE)
	{
		do
		{
			swprintf_s(szNewPath, _countof(szNewPath), _T("%s\\%s"), lpszPath, FindData.cFileName);

			if (!(FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY))
			{
				SetFileAttributes(szNewPath,FILE_ATTRIBUTE_NORMAL);

				if (!DeleteFile(szNewPath))
				{
					LPVOID lpMsgBuf;

					FormatMessage(
						FORMAT_MESSAGE_ALLOCATE_BUFFER | 
						FORMAT_MESSAGE_FROM_SYSTEM | 
						FORMAT_MESSAGE_IGNORE_INSERTS,
						NULL,
						GetLastError(),
						MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
						(LPTSTR) &lpMsgBuf,
						0,
						NULL 
						);

					SendErrorMessageToMSI(NULL,pszLogFileName, LOG_WARNING,_T("DeleteTree: fail to delete the file(%s),Error:%s"),szNewPath,(LPCTSTR)lpMsgBuf);
					// Free the buffer.
					LocalFree(lpMsgBuf);
					bDeleteTree = FALSE;
				}
			}
			else if (lstrcmp(FindData.cFileName, _T("."))  != 0 &&
				lstrcmp(FindData.cFileName, _T("..")) != 0 &&
				FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
			{
				if (!DeleteTree(szNewPath))
				{
					bDeleteTree = FALSE;
				}
			}
		} while (FindNextFile(hFile, &FindData) == TRUE);

		FindClose(hFile);
	}

	SetFileAttributes(lpszPath,FILE_ATTRIBUTE_NORMAL);

	if (!RemoveDirectory(lpszPath))
	{
		LPVOID lpMsgBuf;

		FormatMessage(
			FORMAT_MESSAGE_ALLOCATE_BUFFER | 
			FORMAT_MESSAGE_FROM_SYSTEM | 
			FORMAT_MESSAGE_IGNORE_INSERTS,
			NULL,
			GetLastError(),
			MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
			(LPTSTR) &lpMsgBuf,
			0,
			NULL 
			);

		SendErrorMessageToMSI(NULL,pszLogFileName, LOG_WARNING,_T("DeleteTree: fail to delete the folder(%s),Error:%s"),lpszPath,(LPCTSTR)lpMsgBuf);
		// Free the buffer.
		LocalFree(lpMsgBuf);
		bDeleteTree = FALSE;
	}

	return bDeleteTree;
}

LONG RecursiveDeleteKey(MSIHANDLE hInstall,LPCTSTR pszLogFileName,HKEY hKeyParent, LPCTSTR lpszKeyChild, BOOL bNative)
{
	DWORD dwRetValue = ERROR_SUCCESS;

	TCHAR szMsg[MAX_PATH*2];

	BOOL b64BitOS = Is_64Bit_System();

	HKEY hKeyChild;

	REGSAM regSam;

	if (hKeyParent == NULL) 
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("%s"),_T("recursiveDeleteKeyEx: hKeyParent is NULL!"));
		return ERROR_INVALID_PARAMETER;
	}

	if (lpszKeyChild==NULL || _tcslen(lpszKeyChild)==0)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("%s"),_T("recursiveDeleteKeyEx: lpszKeyChild is NULL!"));
		return ERROR_INVALID_PARAMETER;
	}

	if (b64BitOS)
	{
		if (bNative)
		{
			regSam = KEY_ALL_ACCESS|KEY_WOW64_64KEY;
		}
		else
		{
			regSam = KEY_ALL_ACCESS|KEY_WOW64_32KEY;
		}
	}
	else
	{
		regSam = KEY_ALL_ACCESS;
	}

	LONG lRes = ::RegOpenKeyEx(hKeyParent, lpszKeyChild, 0, regSam, &hKeyChild);
	if (lRes != ERROR_SUCCESS)
	{
		_stprintf_s(szMsg, _countof(szMsg),_T("recursiveDeleteKeyEx: can't access the key %s!"), lpszKeyChild);
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("%s"),szMsg);
		return lRes;
	}

	FILETIME time;
	TCHAR szBuffer[1024];
	DWORD dwSize = 1024;

	ZeroMemory(szBuffer, sizeof(szBuffer));
	while (ERROR_SUCCESS == RegEnumKeyEx(hKeyChild, 0, szBuffer, &dwSize, NULL, NULL, NULL, &time))
	{
		DWORD dwRes = ERROR_SUCCESS;
		dwRes = RecursiveDeleteKey(hInstall,pszLogFileName,hKeyChild, szBuffer, bNative);
		if (dwRes != ERROR_SUCCESS)
		{
			::RegCloseKey(hKeyChild);
			return lRes;
		}

		ZeroMemory(szBuffer, sizeof(szBuffer));
		dwSize = _countof(szBuffer);
	}
	::RegCloseKey(hKeyChild);

	_stprintf_s(szMsg, _countof(szMsg), _T("recursiveDeleteKeyEx: remove registry %s"), lpszKeyChild);
	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"),szMsg);

	if (b64BitOS)
	{
		HMODULE hAdvapi32Dll = LoadLibrary(_T("ADVAPI32.DLL"));

		if (hAdvapi32Dll == NULL)
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("can't Load ADVAPI32.dll.%s"),_T(""));
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
			dwRetValue = lpfRegDelEx(hKeyParent, lpszKeyChild, regSam,0);
			FreeLibrary(hAdvapi32Dll);
			return dwRetValue;
		}
		else
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("can't get the API RegDeleteKeyEx.%s"),_T(""));
			FreeLibrary(hAdvapi32Dll);
			return ERROR_ACCESS_DENIED;
		}

	}
	else
	{
		return RegDeleteKey(hKeyParent, lpszKeyChild);
	}

}

BOOL IsEmptyKey(HKEY hKeyParent,LPCTSTR lpKey)
{
	BOOL bRet = FALSE;
	CRegKey rk;
	REGSAM regMask = KEY_READ;
	if (Is_64Bit_System(NULL))
	{
		regMask |= KEY_WOW64_64KEY;
	}

	//check if the parent key is null
	if (rk.Open(hKeyParent,lpKey,regMask) == ERROR_SUCCESS)
	{
		//Registry key
		DWORD dwIndex = 0;
		DWORD dwNameLength1 = MAX_PATH;
		DWORD dwNameLength2 = MAX_PATH;
		TCHAR szBuffer1[MAX_PATH] = {0};
		TCHAR szBuffer2[MAX_PATH] = {0};

		//Registry Value data and type
		DWORD dwType = 0;
		DWORD dwValueIndex = 0;
		BYTE pData[MAX_PATH] = {0};
		DWORD dwDataLength = MAX_PATH;

		if (RegEnumValue(rk,dwValueIndex,szBuffer1,&dwNameLength1,NULL,&dwType, pData, &dwDataLength) != ERROR_SUCCESS
			&&rk.EnumKey(dwIndex,szBuffer2,&dwNameLength2) != ERROR_SUCCESS)
		{
			//the key is empty key
			bRet = TRUE;
		}

		rk.Close();
	}

	return bRet;
}


BOOL RemoveRegistry(MSIHANDLE hInstall,LPCTSTR pszLogFileName,HKEY hKeyParent, LPCTSTR lpszKeyChild)
{
	BOOL bRet = TRUE;

	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Cleaning up registry key(%s).... "),lpszKeyChild);

	bRet = RecursiveDeleteKey(hInstall,pszLogFileName,hKeyParent, lpszKeyChild, TRUE);

	CRegKey rk;
	BOOL b64BitOS = Is_64Bit_System();
	REGSAM regSam = KEY_ALL_ACCESS;
	if (b64BitOS)
	{
		regSam = KEY_ALL_ACCESS|KEY_WOW64_64KEY;
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("OS is 64bit, add the flag KEY_WOW64_64KEY.%s"), _T(""));
	}

	//fix issue 19052674
	if (IsEmptyKey(hKeyParent, REG_ARCSERVE))
	{
		SendErrorMessageToMSI(hInstall, pszLogFileName, LOG_INFO, _T("The key (%s) is empty, need remove."), REG_ARCSERVE);

		if (b64BitOS)
		{
			DWORD dwRet = ERROR_SUCCESS;
			HMODULE hAdvapi32Dll = LoadLibrary(_T("ADVAPI32.DLL"));

			if (hAdvapi32Dll == NULL)
			{
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Can't Load ADVAPI32.dll.%s"),_T(""));
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
				dwRet = lpfRegDelEx(HKEY_LOCAL_MACHINE, REG_ARCSERVE, regSam, 0);
				FreeLibrary(hAdvapi32Dll);
			}
			else
			{
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("Can't get the API RegDeleteKeyEx.%s"),_T(""));
				FreeLibrary(hAdvapi32Dll);
				return FALSE;
			}

			if (ERROR_SUCCESS == dwRet)
			{
				SendErrorMessageToMSI(hInstall, pszLogFileName, LOG_INFO, _T("Remove the empty key (%s) successfully."), REG_ARCSERVE);
			}
			else
			{
				TCHAR szBuffer[1024];
				memset(szBuffer,0,sizeof(szBuffer));
				int nLastError = GetLastError();
				_stprintf_s(szBuffer, _countof(szBuffer), _T("Fail to remove the empty key (%s). Error:%d"), REG_ARCSERVE, nLastError);
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"), szBuffer);
				bRet = FALSE;
			}
		}
		else
		{
			if (ERROR_SUCCESS == RegDeleteKey(HKEY_LOCAL_MACHINE, REG_ARCSERVE))
			{
				SendErrorMessageToMSI(hInstall, pszLogFileName, LOG_INFO, _T("Remove the empty key (%s) successfully."), REG_ARCSERVE);
			}
			else
			{
				TCHAR szBuffer[1024];
				memset(szBuffer,0,sizeof(szBuffer));
				int nLastError = GetLastError();
				_stprintf_s(szBuffer, _countof(szBuffer), _T("Fail to remove the empty key (%s). Error:%d"), REG_ARCSERVE, nLastError);
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"), szBuffer);
				bRet = FALSE;
			}
		}
	}
	else
	{
		SendErrorMessageToMSI(hInstall, pszLogFileName, LOG_INFO, _T("The key (%s) is not empty, no need remove."), REG_ARCSERVE);
	}
	//end fix 19052674

	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"),_T("End of cleaning up registry"));

	return bRet;
}

//only remove current empty key, not Recursive
BOOL RemoveEmptyRegistry(MSIHANDLE hInstall,LPCTSTR pszLogFileName,HKEY hKeyParent, LPCTSTR lpszKey)
{
	BOOL bRet = TRUE;

	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("RemoveEmptyRegistry: Cleaning up the empty registry key(%s).... "),lpszKey);

	CRegKey rk;
	BOOL b64BitOS = Is_64Bit_System();
	REGSAM regSam = KEY_ALL_ACCESS;
	if (b64BitOS)
	{
		regSam = KEY_ALL_ACCESS|KEY_WOW64_64KEY;
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("OS is 64bit, add the flag KEY_WOW64_64KEY.%s"), _T(""));
	}

	//fix issue 19052674
	if (IsEmptyKey(hKeyParent,lpszKey))
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("The key (%s) is empty, need remove."), lpszKey);

		if (b64BitOS)
		{
			DWORD dwRet = ERROR_SUCCESS;
			HMODULE hAdvapi32Dll = LoadLibrary(_T("ADVAPI32.DLL"));

			if (hAdvapi32Dll == NULL)
			{
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Can't Load ADVAPI32.dll.%s"),_T(""));
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"),_T("RemoveEmptyRegistry: End."));
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
				dwRet = lpfRegDelEx(HKEY_LOCAL_MACHINE, lpszKey, regSam,0);
				FreeLibrary(hAdvapi32Dll);
			}
			else
			{
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("Can't get the API RegDeleteKeyEx.%s"),_T(""));
				FreeLibrary(hAdvapi32Dll);
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"),_T("RemoveEmptyRegistry: End."));
				return FALSE;
			}

			if (ERROR_SUCCESS == dwRet)
			{
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Remove the empty key (%s) successfully."), lpszKey);
			}
			else
			{
				TCHAR szBuffer[1024];
				memset(szBuffer,0,sizeof(szBuffer));
				int nLastError = GetLastError();
				_stprintf_s(szBuffer,_countof(szBuffer), _T("Fail to remove the empty key (%s). Error:%d"),lpszKey,nLastError);
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"), szBuffer);
				bRet = FALSE;
			}
		}
		else
		{
			if (ERROR_SUCCESS == RegDeleteKey(HKEY_LOCAL_MACHINE, lpszKey))
			{
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Remove the empty key (%s) successfully."), lpszKey);
			}
			else
			{
				TCHAR szBuffer[1024];
				memset(szBuffer,0,sizeof(szBuffer));
				int nLastError = GetLastError();
				_stprintf_s(szBuffer,_countof(szBuffer), _T("Fail to remove the empty key (%s). Error:%d"),lpszKey,nLastError);
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"), szBuffer);
				bRet = FALSE;
			}
		}
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("The key (%s) is not empty, no need remove."), lpszKey);
	}
	//end fix 19052674

	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"),_T("RemoveEmptyRegistry: End."));

	return bRet;
}

DWORD StopServiceForUninstall(MSIHANDLE hInstall,LPCTSTR lpszServiceName,LPCTSTR pszLogFileName)
{
	DWORD dwRet = ERROR_SUCCESS;

	//stop web service
	CString strFlag;
	strFlag.Format(_T("%s_FLAG"),lpszServiceName);
	strFlag.MakeUpper();

	if (IsServiceStarted(lpszServiceName))
	{
		dwRet = StopSpecService(lpszServiceName,TRUE,TRUE);
		if(dwRet == ERROR_SUCCESS)
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Stop service %s successfully."), lpszServiceName);
			MsiSetProperty(hInstall,strFlag,_T("1"));
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Set the property %s=1."), strFlag);
		}
		else if(dwRet == ERROR_BROKEN_PIPE)
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Stop service(%s) return code is %d."), lpszServiceName,ERROR_BROKEN_PIPE);
			MsiSetProperty(hInstall,strFlag,_T("1"));
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Set the property %s=1."), strFlag);
		}
		else
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("Fail to stop service %s. ErrorCode:%d"), lpszServiceName,dwRet);
		}
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("No need to stop service(%s). It is not running."),lpszServiceName);
	}

	return dwRet;
}

BOOL StartServiceForUninstall(MSIHANDLE hInstall,LPCTSTR lpszServiceName,LPCTSTR pszLogFileName)
{
	BOOL bRet = TRUE;

	//start UA service once uninstall process stop it.
	CString strValue;
	CString strFlag;
	TCHAR szValue[MAX_PATH];
	ZeroMemory(szValue,sizeof(szValue));
	DWORD dwSize = _countof(szValue);
	strFlag.Format(_T("%s_FLAG"),lpszServiceName);
	strFlag.MakeUpper();
	MsiGetProperty(hInstall, strFlag, szValue, &dwSize);
	strValue = szValue;

	if(IsServiceStarted(lpszServiceName))
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("The service(%s) is running, no need start it again."), lpszServiceName);
		return TRUE;
	}

	if(strValue.CompareNoCase(_T("1")) == 0)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Need to start the service(%s)."),lpszServiceName);
		if(StartSpecService(lpszServiceName,TRUE))
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Start the service(%s) successfully."), lpszServiceName);
		}
		else
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("Fail to start service(%s)."), lpszServiceName);
			bRet = FALSE;
		}
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("The service(%s) is not stopped by Uninstall process. No need to start it."),lpszServiceName);
	}

	return bRet;
}	

BOOL RegKeyExist(HKEY hKey, LPCTSTR lpSubKey)
{
	BOOL bRet = FALSE;
	HKEY hConnectKey = hKey;
	HKEY hTempKey;

	
	if (::RegOpenKeyEx(hConnectKey, lpSubKey, 0, KEY_READ, &hTempKey) == ERROR_SUCCESS) 
	{
		bRet = TRUE;
		::RegCloseKey(hTempKey);
	}
	else if (Is_64Bit_System())
	{
		//check 64bit registry
		if (::RegOpenKeyEx(hConnectKey, lpSubKey, 0, KEY_READ|KEY_WOW64_64KEY, &hTempKey) == ERROR_SUCCESS) 
		{
			bRet = TRUE; 
			::RegCloseKey(hTempKey);
		}
		else if (::RegOpenKeyEx(hConnectKey, lpSubKey, 0, KEY_READ|KEY_WOW64_32KEY, &hTempKey) == ERROR_SUCCESS)
		{
			//check 32bit registry on 64bit
			bRet = TRUE; 
			::RegCloseKey(hTempKey);
		}
	}

	return bRet;
}

CString GetProductName(MSIHANDLE hInstall)
{
	CString strRet = _T("");
	TCHAR szProductName[MAX_PATH];
	memset(szProductName,0,sizeof(szProductName));
	//get ProductName
	DWORD dwSize = _countof(szProductName);
	MsiGetProperty(hInstall, _T("ProductName"), szProductName, &dwSize);

	strRet = szProductName;

	return strRet;
}

CString GetProductName(LPCTSTR lpProductCode)
{
	CString strRet = _T("");

	if(lpProductCode == NULL)
	{
		return strRet;
	}

	TCHAR szData[MAX_PATH];
	DWORD ccSize = _countof(szData);
	ZeroMemory(szData,sizeof(szData));

	if( ERROR_SUCCESS == MsiGetProductInfo(lpProductCode,_T("ProductName"),szData,&ccSize))
	{
		strRet = szData;
		return strRet;
	}
	
	return	strRet;
}

//////////////////////////////////////////////////////////////////////////////////////////
//	Function:	GetProgramFilesFolder 
//	Parameter:	IN bIsLocal		---		Get local machine's program files folder
//				IN lpMachine	---		The remote machine name
//				OUT lpPath		---		Buffer that used to return program files folder
//				IN dwSize		---		The buffer lpPath's size
//	Purpose:	Get current system's program files folder
//
//  Return:		TRUE	--- Operation successfully
//				FALSE	--- Operation failed
//////////////////////////////////////////////////////////////////////////////////////////
BOOL GetProgramFilesFolder(MSIHANDLE hInstall,BOOL bIsLocal, LPCTSTR lpMachine, LPTSTR lpPath, DWORD dwSize,LPCTSTR pszLogFileName,BOOL bGet32On64OS /*= FALSE*/)
{
	HKEY hKey = NULL, hLocalMachine = HKEY_LOCAL_MACHINE;
	REGSAM samDesired;
	BOOL bLocal = IsLocalMachine(lpMachine);

	if ((!bLocal) && lpMachine)
	{
		if (ERROR_SUCCESS != ::RegConnectRegistry(lpMachine, HKEY_LOCAL_MACHINE, &hLocalMachine))
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING, _T(" Fail to connect to machine %s"), (LPTSTR)lpMachine);
			return FALSE;
		}
	}

	samDesired = KEY_READ;

	if (Is_64Bit_System((LPTSTR)lpMachine) && !bGet32On64OS)
	{
		samDesired = KEY_READ|KEY_WOW64_64KEY;
	}
	else if (Is_64Bit_System((LPTSTR)lpMachine) && bGet32On64OS)
	{
		samDesired = KEY_READ|KEY_WOW64_32KEY;
	}

	if (ERROR_SUCCESS != ::RegOpenKeyEx(hLocalMachine, REGKEY_MICROSOFT_WINDOWS, 0, samDesired, &hKey))
	{
		if (!bLocal)
			::RegCloseKey(hLocalMachine);

		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING, _T(" Fail to open reg  %s"), REGKEY_MICROSOFT_WINDOWS);
		return FALSE;
	}

	BOOL bRet = TRUE;
	if (ERROR_SUCCESS != RegQueryValueEx(hKey, REGVALUE_PROGRAM_FILES_DIR, NULL, NULL, (LPBYTE)lpPath, &dwSize))
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING, _T(" Fail to query value  %s"), REGVALUE_PROGRAM_FILES_DIR);
		bRet = FALSE;
	}

	::RegCloseKey(hKey);

	if (!bLocal)
		::RegCloseKey(hLocalMachine);

	return bRet;
}

BOOL IsLocalMachine(LPCTSTR lpMachine)
{
	if (!lpMachine || !*lpMachine)
		return TRUE;

	TCHAR szLocMachine[MAX_PATH] = {0};
	DWORD dwSize = _countof(szLocMachine);

	if (!::GetComputerName(szLocMachine, &dwSize))
		return FALSE;

	return _tcsnicmp (szLocMachine, lpMachine, _countof(szLocMachine)) == 0;
}


BOOL IsServerCoreOS()
{
	DWORD dwReturnedProductType = 0;

	OSVERSIONINFOEX VersionInfo;
	memset(&VersionInfo, 0, sizeof(OSVERSIONINFOEX));
	VersionInfo.dwOSVersionInfoSize = sizeof(VersionInfo);

	HMODULE hndl = NULL;
	pfn_GetProductInfo GetProductInfo = NULL;
	BOOL result = FALSE;

	GetVersionEx((LPOSVERSIONINFO)&VersionInfo);
	hndl = LoadLibrary(DLL_KERNEL32);
	if (!hndl)
	{
		return FALSE;
	}

	GetProductInfo = (pfn_GetProductInfo) GetProcAddress(hndl, "GetProductInfo");
	if(!GetProductInfo)
	{
		FreeLibrary(hndl);
		return FALSE;
	}

	result = (*GetProductInfo)(VersionInfo.dwMajorVersion, VersionInfo.dwMinorVersion, VersionInfo.wServicePackMajor, VersionInfo.wServicePackMinor, &dwReturnedProductType);
	FreeLibrary(hndl);

	return (PRODUCT_STANDARD_SERVER_CORE == dwReturnedProductType || 
		PRODUCT_DATACENTER_SERVER_CORE == dwReturnedProductType ||
		PRODUCT_ENTERPRISE_SERVER_CORE == dwReturnedProductType ||
		PRODUCT_STANDARD_SERVER_CORE_V == dwReturnedProductType ||
		PRODUCT_DATACENTER_SERVER_CORE_V == dwReturnedProductType ||
		PRODUCT_ENTERPRISE_SERVER_CORE_V == dwReturnedProductType ||
		PRODUCT_WEB_SERVER_CORE == dwReturnedProductType
		);
}

//remove the unused tray icon
void RemoveDeadIcons(MSIHANDLE hInstall,LPCTSTR pszLogFileName)
{
	HWND hTrayWindow;
	RECT rctTrayIcon;
	int nIconWidth;
	int nIconHeight;
	POINT  CursorPos;
	int nRow;
	int nCol;

	LOG_FUNCTION_BEGIN(hInstall,pszLogFileName);

	// Get tray window handle and bounding rectangle
	hTrayWindow = ::FindWindowEx((HWND)::FindWindow(_T("Shell_TrayWnd"), NULL), 0, _T("TrayNotifyWnd"), NULL);

	if(!::GetWindowRect(hTrayWindow, &rctTrayIcon))
		return;


	// Get small icon metrics
	nIconWidth = ::GetSystemMetrics(SM_CXSMICON);
	nIconHeight = ::GetSystemMetrics(SM_CYSMICON);

	// Save current mouse position }
	::GetCursorPos(&CursorPos);

	// Sweep the mouse cursor over each icon in the tray in both dimensions
	for(int i=0; i<1; i++) //fix issue 18988075
	{
		for(nRow=0; nRow<(rctTrayIcon.bottom-rctTrayIcon.top)/nIconHeight; nRow++)
		{
			for(nCol=0; nCol<(rctTrayIcon.right-rctTrayIcon.left)/nIconWidth; nCol++)
			{
				::SetCursorPos(rctTrayIcon.left + nCol * nIconWidth + 5,
					rctTrayIcon.top + nRow * nIconHeight + 5);
				Sleep(5); //fix issue 18988075
			}
		}
	}

	// Restore mouse position
	::SetCursorPos(CursorPos.x, CursorPos.x);

	// Redraw tray window (to fix bug in multi-line tray area)
	if(::RedrawWindow(hTrayWindow, NULL, 0, RDW_INVALIDATE | RDW_ERASE | RDW_UPDATENOW))
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Remove the unused tray icon successfully. %s"), _T(""));
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("Fail to remove the unused tray icon. %s"), _T(""));
	}

	LOG_FUNCTION_END(hInstall,pszLogFileName);
}

DWORD CallRegSvr32( MSIHANDLE hInstall, LPCTSTR lpctDLLPath, BOOL bUninstall,LPCTSTR pszLogFileName)
{
	LOG_FUNCTION_BEGIN(hInstall,pszLogFileName);

	if ((lpctDLLPath == NULL) || (_tcslen(lpctDLLPath)==0))
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO, _T("CallRegSvr32: The input file is empty.") );
		return FALSE;
	}

	TCHAR tcMsg[MAX_PATH] = {0};
	ZeroMemory( tcMsg, sizeof(TCHAR)*MAX_PATH );

	if (_taccess(lpctDLLPath, 0) != 0)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("CallRegSvr32: cannot access the file (%s)"), lpctDLLPath );
		return FALSE;
	}

	CString strCmdline = _T("");
	if(!bUninstall)
	{
		strCmdline.Format(_T("regsvr32 /s \"%s\""), lpctDLLPath);
	}
	else
	{
		strCmdline.Format(_T("regsvr32 /s /u \"%s\""), lpctDLLPath);
	}

	ZeroMemory( tcMsg, sizeof(TCHAR)*MAX_PATH );
	_stprintf_s(tcMsg, _countof(tcMsg),_T("%s"), strCmdline);
	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("CallRegSvr32: CmdLine=%s"), tcMsg );

	DWORD dwExitCode,dwRet;
	dwRet = LaunchProcess((LPTSTR)(LPCTSTR)strCmdline, NULL, &dwExitCode);

	TCHAR szTmp[MAX_PATH];
	ZeroMemory(szTmp,sizeof(szTmp));
	_stprintf_s(szTmp, _countof(szTmp),_T("%d, %d"), dwRet, dwExitCode);
	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO, _T("CallRegSvr32: returncode: %s"), szTmp);

	LOG_FUNCTION_END(hInstall,pszLogFileName);
	return dwExitCode;
}

// this function get the .NET Framework install folder
BOOL GetNET20InstallFolder(LPTSTR szValue, DWORD ccBuffer)
{
	//1 open the .NET root key in the registry
	HKEY hNETRoot = NULL;
	REGSAM samDesired = KEY_READ;

	if (Is_64Bit_System(NULL))
		samDesired |= KEY_WOW64_64KEY;

	CString strNETSetupKey = _T("SOFTWARE\\Microsoft\\.NETFramework");
	if (ERROR_SUCCESS != ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, strNETSetupKey, NULL, samDesired, &hNETRoot))
		return FALSE;

	//2 read the install folder in the registry
	CString	sInstFolder(_T(""));
	DWORD dwSize = _MAX_PATH;

	if (ERROR_SUCCESS != ::RegQueryValueEx(hNETRoot, _T("InstallRoot"), 0, NULL, (LPBYTE)(LPCTSTR)sInstFolder.GetBuffer(dwSize), &dwSize))
	{
		sInstFolder.ReleaseBuffer();
		::RegCloseKey(hNETRoot);
		return FALSE;
	}
	sInstFolder.ReleaseBuffer();
	::RegCloseKey(hNETRoot);

	AppendBackSlash(sInstFolder);
	sInstFolder += _T("v2.0.50727\\");	//.NET Framework 2.0, not beta version

	//3 verify the folder
	CFileFind Finder;
	if (FALSE == Finder.FindFile(sInstFolder + _T("*.*")))
	{
		return FALSE;
	}
	Finder.Close();

	if (sInstFolder.GetAllocLength() > (int)ccBuffer)
	{
		return FALSE;
	}

	_stprintf_s(szValue, ccBuffer,_T("%s"), (LPTSTR)(LPCTSTR)sInstFolder);

	return TRUE;
}


BOOL TrimTailBackSlash(CString &sPath)
{
#ifdef UNICODE
	sPath.TrimRight(_T("\\"));
#else
	int nPathLength = sPath.GetLength();

	if (nPathLength == 0)
		return FALSE;

	BOOL bIsOK = FALSE;
	CString	sTemp;

	while ((nPathLength>0) && sPath[nPathLength - 1] == _T('\\'))
	{
		if (nPathLength == 1)
			bIsOK = TRUE;
		else if ((nPathLength >= 2) && !IsDBCSLeadByte(sPath[nPathLength - 2]))
			bIsOK = TRUE;
		else if (nPathLength >= 3)
		{
			for (int i = nPathLength - 3; i >= 0 ; i--)
			{
				if (!IsDBCSLeadByte(sPath[i]))
					break;
				bIsOK = !bIsOK;
			}

			if (!bIsOK)
				break;
		}
		else 
			break;

		if (bIsOK)
		{
			sTemp = sPath.Left(nPathLength - 1);
			sPath = sTemp; //avoid assertion warning message disply
			nPathLength = sPath.GetLength();
			bIsOK = FALSE;
		}
	}
#endif

	return (sPath.GetLength());
}



BOOL AppendBackSlash(CString &sPath)
{
	if (TrimTailBackSlash(sPath))
	{
		sPath += _T('\\');
	}

	return (sPath.GetLength());
}

//delete the empty folder Recursively
int DeleteEmptyDirectory(MSIHANDLE hInstall,const CString& strDir,LPCTSTR pszLogFileName)
{
	//const int MAX_BUF = 1024;
	CString strDirTemp = strDir;
	CString strFile;
	if(strDirTemp.IsEmpty())
	{
		return (-1);
	}

	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO, _T("DeleteEmptyDirectory: start to remove folder(%s)."),(LPTSTR)(LPCTSTR)strDirTemp);

	//remove the last char '\'
	if(strDirTemp.ReverseFind(_T('\\')) != -1)
	{
		strDirTemp.TrimRight(_T("\\"));
	}

	if(strDirTemp.GetLength() < 3)
	{
		//no need remove like driver root folder (c:\)
		return 0;
	}

	if(_taccess((LPCTSTR)strDirTemp,0) != -1)
	{
		SetFileAttributes(strDirTemp, FILE_ATTRIBUTE_NORMAL);
		if(RemoveDirectory(strDirTemp))
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("DeleteEmptyDirectory: remove folder (%s) successfully;"),(LPTSTR)(LPCTSTR)strDirTemp);
		}
		else
		{		
			LPVOID lpMsgBuf;
			TCHAR  szTempMesg[MAX_BUF]={0};
			FormatMessage( 
				FORMAT_MESSAGE_ALLOCATE_BUFFER | 
				FORMAT_MESSAGE_FROM_SYSTEM | 
				FORMAT_MESSAGE_IGNORE_INSERTS,
				NULL,
				GetLastError(),
				MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
				(LPTSTR) &lpMsgBuf,
				0,
				NULL 
				);

			_stprintf_s(szTempMesg,MAX_BUF,_T("DeleteEmptyDirectory: fail to remove folder (%s) --REASON:%s"),(LPTSTR)(LPCTSTR)strDirTemp,(LPCTSTR)lpMsgBuf);
			// Free the buffer.
			LocalFree( lpMsgBuf );

			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("%s"),szTempMesg);
			return -1;
		}
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("DeleteEmptyDirectory: can not access folder (%s). No need remove it."),(LPTSTR)(LPCTSTR)strDirTemp);
	}

	//remove parent folder
	int nIndex = strDirTemp.ReverseFind(_T('\\'));
	if(nIndex > 2) //except root dir ,e.g. c:
	{
		strDirTemp = strDirTemp.Left(nIndex);
		DeleteEmptyDirectory(hInstall,strDirTemp,pszLogFileName);
	}

	return ERROR_SUCCESS;
}

BOOL IsEmptyKey(LPCTSTR lpKey)
{
	BOOL bRet = FALSE;
	CRegKey rk;
	REGSAM regMask = KEY_READ;
	if(Is_64Bit_System(NULL))
	{
		regMask |= KEY_WOW64_64KEY;
	}

	//check if the parent key is null
	if(rk.Open(HKEY_LOCAL_MACHINE,lpKey,regMask) == ERROR_SUCCESS)
	{
		//Registry key
		DWORD dwIndex = 0;
		DWORD dwNameLength1 = MAX_PATH;
		DWORD dwNameLength2 = MAX_PATH;
		TCHAR szBuffer1[MAX_PATH] = {0};
		TCHAR szBuffer2[MAX_PATH] = {0};

		//Registry Value data and type
		DWORD dwType = 0;
		DWORD dwValueIndex = 0;
		BYTE pData[MAX_PATH] = {0};
		DWORD dwDataLength = MAX_PATH;

		if(RegEnumValue(rk,dwValueIndex,szBuffer1,&dwNameLength1,NULL,&dwType, pData, &dwDataLength) != ERROR_SUCCESS
			&&rk.EnumKey(dwIndex,szBuffer2,&dwNameLength2) != ERROR_SUCCESS)
		{
			//the key is empty key
			bRet = TRUE;
		}

		rk.Close();
	}

	return bRet;
}


BOOL RegisterNETDLL(MSIHANDLE hInstall, LPCTSTR szFileName, BOOL bUninst,LPCTSTR pszLogFileName)
{
	LOG_FUNCTION_BEGIN(hInstall,pszLogFileName);

	if (_taccess(szFileName, 0) != 0)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("RegisterNETDLL() function: Can not find file(%s)."), szFileName);
		return FALSE;
	}

	//1 get the .NET Framework installed folder
	TCHAR szRegasmPath[MAX_PATH];
	DWORD	dwSize = _countof(szRegasmPath);

	if (!GetNET20InstallFolder(szRegasmPath, dwSize)) 
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING, _T("RegisterNETDLL() function: the GetNET20InstallFolder() return FALSE.%s"), _T("Can not get the .NetFramwork install path."));
		LOG_FUNCTION_END(hInstall,pszLogFileName);
		return FALSE;
	}

	//2 verify the regasm.exe file path
	PathAppend(szRegasmPath,_T("regasm.exe"));

	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("RegisterNETDLL() function: sRegasmPath = %s"), szRegasmPath);

	if (_taccess(szRegasmPath, 0) != 0)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING, _T("RegisterNETDLL() function: Can not find the regasm.exe file(%s)"), szRegasmPath);
		LOG_FUNCTION_END(hInstall,pszLogFileName);
		return FALSE;
	}

	//3 get the install folder of ARCFlash.
	TCHAR szWorkingDir[MAX_PATH];
	ZeroMemory(szWorkingDir,sizeof(szWorkingDir));

	GetFilePath(szFileName,szWorkingDir,_countof(szWorkingDir));


	//6 try to register the .NET asm files
	TCHAR szCmdLine[MAX_PATH*4] = _T("");
	DWORD dwExitCode, dwRet=0;
	memset(szCmdLine,0,sizeof(szCmdLine));

	if (!bUninst) 
	{	//register the NET component
		wsprintf(szCmdLine,_T("\"%s\" \"%s\" /codebase"), szRegasmPath, szFileName);
	}
	else 
	{
		//unretister the NET component
		wsprintf(szCmdLine,_T("\"%s\" /u \"%s\""), szRegasmPath, szFileName);
	}

	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("RegisterNETDLL() function: CmdLine=%s, workingdir=%s"), szCmdLine,szWorkingDir);
	dwRet = LaunchProcess(szCmdLine,szWorkingDir, &dwExitCode);
	{
		TCHAR szTmp[MAX_PATH];
		memset(szTmp,0,sizeof(szTmp));
		_stprintf_s(szTmp,_countof(szTmp), _T("%d, %d"), dwRet, dwExitCode);
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO, _T("returncode: %s"), szTmp);
	}

	LOG_FUNCTION_END(hInstall,pszLogFileName);

	return TRUE;
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


	_tsplitpath_s(lpFullFileName, drive,_countof(drive), dir,_countof(dir), NULL,0,NULL,0);

	_stprintf_s(lpPath,ccSize,_T("%s%s"),drive,dir);

	return TRUE;
}

BOOL ANSI2Unicode( const char* pAnsiPath, wchar_t** pUnicodePath )
{
	//Get the size of the string by setting the 4th parameter to -1:
	DWORD dwNum = MultiByteToWideChar (CP_ACP, 0, pAnsiPath, -1, NULL, 0);

	//Allocate space for wide char string:
	*pUnicodePath = NULL;
	*pUnicodePath = new wchar_t[dwNum];
	if(!pUnicodePath)
	{
		delete[] *pUnicodePath;
		return FALSE;
	}

	MultiByteToWideChar (CP_ACP, 0, pAnsiPath, -1, *pUnicodePath, dwNum );
	return TRUE;
}


BOOL DeleteTreeExImpl(MSIHANDLE hInstall,LPCWSTR lpszPath,LPCTSTR pszLogFileName)
{
	WIN32_FIND_DATAW FindData;
	ZeroMemory( &FindData, sizeof(WIN32_FIND_DATAW) );

	HANDLE hFile;
	int nSize = 32767;
	WCHAR* szNewPath = new WCHAR[ nSize ];
	ZeroMemory( szNewPath, nSize*sizeof(WCHAR) );

	WCHAR wild[] = L"*.*";
	WCHAR Dir1[] = L".";
	WCHAR Dir2[] = L".";

	BOOL bDeleteTree = TRUE;

	if(lpszPath == NULL)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("DeleteTreeExImpl: The input parameter lpszPath == NULL"));
		return TRUE;
	}

	if(wcsstr(lpszPath,LONG_PATH_PRE) == NULL)
	{
		swprintf_s(szNewPath, nSize, L"\\\\?\\%s\\%s", lpszPath, wild );
	}
	else
	{
		swprintf_s(szNewPath, nSize, L"%s\\%s", lpszPath, wild );
	}

	if ((hFile = FindFirstFileW(szNewPath, &FindData )) != INVALID_HANDLE_VALUE)
	{
		do
		{
			if ( lstrcmpiW( FindData.cFileName, L"." )  == 0 || lstrcmpiW(FindData.cFileName, L"..") == 0 )
			{
				continue;
			}

			if( FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY )
			{
				ZeroMemory( szNewPath, nSize*sizeof(WCHAR) );
				swprintf_s(szNewPath, nSize, L"%s\\%s", lpszPath, FindData.cFileName);
				if (!DeleteTreeExImpl(hInstall,szNewPath,pszLogFileName))
				{
					bDeleteTree = FALSE;
				}

				continue;
			}

			ZeroMemory( szNewPath, nSize*sizeof(WCHAR) );
			if(wcsstr(lpszPath,LONG_PATH_PRE) == NULL)
				swprintf_s(szNewPath, nSize, L"\\\\?\\%s\\%s", lpszPath, FindData.cFileName);
			else
				swprintf_s(szNewPath, nSize, L"%s\\%s", lpszPath, FindData.cFileName);

			SetFileAttributesW(szNewPath,FILE_ATTRIBUTE_NORMAL);
			if(!DeleteFileW(szNewPath))
			{
				LPVOID lpMsgBuf;

				FormatMessage(
					FORMAT_MESSAGE_ALLOCATE_BUFFER | 
					FORMAT_MESSAGE_FROM_SYSTEM | 
					FORMAT_MESSAGE_IGNORE_INSERTS,
					NULL,
					GetLastError(),
					MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
					(LPTSTR) &lpMsgBuf,
					0,
					NULL 
					);

				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("DeleteTreeExImpl: fail to delete the file(%s),Error:%s"),szNewPath,(LPCTSTR)lpMsgBuf);
				// Free the buffer.
				LocalFree(lpMsgBuf);
				bDeleteTree = FALSE;
			}

			ZeroMemory( &FindData, sizeof(WIN32_FIND_DATAW) );

		} while(FindNextFileW(hFile, &FindData) == TRUE);

		FindClose(hFile);
	}

	ZeroMemory( szNewPath, nSize*sizeof(WCHAR) );
	if(wcsstr(lpszPath,LONG_PATH_PRE) == NULL)
		swprintf_s(szNewPath, nSize, L"\\\\?\\%s", lpszPath );
	else
		swprintf_s(szNewPath, nSize, L"%s", lpszPath );

	SetFileAttributesW(szNewPath,FILE_ATTRIBUTE_NORMAL);
	if(!RemoveDirectoryW(szNewPath))
	{
		LPVOID lpMsgBuf;

		FormatMessage(
			FORMAT_MESSAGE_ALLOCATE_BUFFER | 
			FORMAT_MESSAGE_FROM_SYSTEM | 
			FORMAT_MESSAGE_IGNORE_INSERTS,
			NULL,
			GetLastError(),
			MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
			(LPTSTR) &lpMsgBuf,
			0,
			NULL 
			);

		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("DeleteTreeExImpl: fail to delete the folder(%s),Error:%s"),szNewPath,(LPCTSTR)lpMsgBuf);
		// Free the buffer.
		LocalFree(lpMsgBuf);
		bDeleteTree = FALSE;
	}

	delete[] szNewPath;

	return bDeleteTree;
}


/***************************************************************************/
/*This function deletes all files and sub-directories of a directory whose length is > 260(MAX_PATH)
/***************************************************************************/
BOOL DeleteTreeEx(MSIHANDLE hInstall,LPCTSTR lpszPath,LPCTSTR pszLogFileName)
{
	BOOL bRet = TRUE;
	wchar_t* pUni = NULL;
	
	if(lpszPath == NULL)
	{
		return TRUE;
	}

#ifdef UNICODE
		bRet = DeleteTreeExImpl(hInstall,lpszPath,pszLogFileName );
#else
	if( ANSI2Unicode( lpszPath, &pUni ) )
	{
		bRet = DeleteTreeExImpl(hInstall,pUni,pszLogFileName );
		delete[] pUni;
	}
#endif

	return bRet;
}

//hide the cancel buttong on windows installer dialog for un-installation.
DWORD HideCancelButton(MSIHANDLE hInstall)
{
    PMSIHANDLE hRecord = MsiCreateRecord(2);

    if (!hRecord)
        return ERROR_INSTALL_FAILURE;

    MsiRecordSetInteger(hRecord, 1, 2);
    MsiRecordSetInteger(hRecord, 2, 0);

    MsiProcessMessage(hInstall, INSTALLMESSAGE_COMMONDATA, hRecord);

    return ERROR_SUCCESS;
}

//retry to get/set the data for GetPrivateProfile/SetPrivateProfile(BAOF probaly lock the inf file,so use this function to fix the problem)
DWORD SetupGetPrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpDefault, LPTSTR lpReturnedString, DWORD nSize, LPCTSTR lpFileName)
{
	DWORD dwRet = ERROR_SUCCESS;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT_FLSHGUI; i++)
	{
		dwRet = ::GetPrivateProfileString(lpAppName, 
			lpKeyName, 
			lpDefault,
			lpReturnedString,
			nSize,
			lpFileName);

		if (ERROR_SHARING_VIOLATION == GetLastError())
		{
			SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("GetPrivateProfileString(%s, %s,...%s) failed. Err=%d"), lpAppName, lpKeyName, lpFileName, ERROR_SHARING_VIOLATION);
			Sleep(1000);
		}
		else
		{
			break;
		}
	}

	return dwRet;
}


BOOL SetupWritePrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpString, LPCTSTR lpFileName)
{
	BOOL bRet = FALSE;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT_FLSHGUI; i++)
	{
		bRet = ::WritePrivateProfileString(lpAppName, lpKeyName, lpString, lpFileName);

		 if (ERROR_SHARING_VIOLATION == GetLastError())
		 {
			 SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("WritePrivateProfileString(%s, %s,...%s) failed. Err=%d"), lpAppName, lpKeyName, lpFileName, ERROR_SHARING_VIOLATION);
			 Sleep(1000);
		 }
		 else
		 {
			 break;
		 }
	}

	if (!bRet)
	{
		SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("WritePrivateProfileString(%s %s %s %s) failed with error %d"),
			lpAppName, lpKeyName, lpString, lpFileName, ::GetLastError());
	}

	return bRet;
}

UINT SetupGetPrivateProfileInt(LPCTSTR lpAppName, LPCTSTR lpKeyName, INT nDefault, LPCTSTR lpFileName)
{
	UINT nRet = 0;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT_FLSHGUI; i++)
	{
		nRet = ::GetPrivateProfileInt(lpAppName, 
			lpKeyName, 
			nDefault,
			lpFileName);

		if (ERROR_SHARING_VIOLATION == GetLastError())
		{
			SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("SetupGetPrivateProfileInt(%s, %s,...%s) failed. Err=%d"), lpAppName, lpKeyName, lpFileName, ERROR_SHARING_VIOLATION);
			Sleep(1000);
		}
		else
		{
			break;
		}
	}

	return nRet;
}

DWORD SetupGetPrivateProfileSection(LPCTSTR lpAppName,LPTSTR lpReturnedString,DWORD nSize,LPCTSTR lpFileName)
{
	DWORD dwRet = 0;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT_FLSHGUI; i++)
	{
		dwRet = ::GetPrivateProfileSection(lpAppName, 
			lpReturnedString, 
			nSize,
			lpFileName);

		if (ERROR_SHARING_VIOLATION == GetLastError())
		{
			SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("SetupGetPrivateProfileSection(%s,...%s) failed. Err=%d"), lpAppName, lpFileName, ERROR_SHARING_VIOLATION);
			Sleep(1000);
		}
		else
		{
			break;
		}
	}

	return dwRet;
}
//end for retry to get/set the data for GetPrivateProfile/SetPrivateProfile(BAOF probaly lock the inf file,so use this function to fix the problem)


HINSTANCE GetRC(MSIHANDLE hInstall,LPCTSTR pszLogFileName)
{
	//get install path
	TCHAR szRCFile[MAX_PATH];
	DWORD dwSize = _countof(szRCFile);
	MsiGetProperty(hInstall, _T("INSTALLDIR"), szRCFile, &dwSize);
	
	/*if(Is_64Bit_System(NULL))
	{
		PathAppend(szRCFile,SETUPPATCH_COMMON_PATH);
		PathAppend(szRCFile,_T("AMD64"));
	}*/

	PathAppend(szRCFile,_T("ASetupRes.dll"));

	if(_taccess(szRCFile,0) != 0)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_ERROR,_T("GetRC: cannot access the file(%s), and canot parse the default message."),szRCFile);
		return NULL;
	}
	else
	{
		HINSTANCE hInstDll = LoadLibraryEx(szRCFile, NULL, LOAD_WITH_ALTERED_SEARCH_PATH);

		if(hInstDll == NULL)
		{
			LPVOID lpMsgBuf;
			DWORD dw = GetLastError(); 

			FormatMessage(
				FORMAT_MESSAGE_ALLOCATE_BUFFER | 
				FORMAT_MESSAGE_FROM_SYSTEM |
				FORMAT_MESSAGE_IGNORE_INSERTS,
				NULL,
				dw,
				MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
				(LPTSTR) &lpMsgBuf,
				0, NULL );

			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_ERROR,_T("GetRC: cannot load the file(%s), Error:%d,%s"),(TCHAR*)lpMsgBuf,dw,lpMsgBuf);
			LocalFree(lpMsgBuf);
		}

		return hInstDll;
	}
}

BOOL GetLogDir(MSIHANDLE hInstall,LPTSTR lptValue, size_t size)
{
	BOOL bRet = FALSE;
	TCHAR szFile[MAX_PATH];
	TCHAR szTempPath[MAX_PATH];
	DWORD dwSize = _countof(szFile);
	memset(szFile,0,sizeof(szFile));
	memset(szTempPath,0,sizeof(szTempPath));

	SendErrorMessageToMSI(hInstall,LOG_FILE_D2D_PREFIX, LOG_INFO,_T("GetLogDir %s"), _T("Begin"));
	if(hInstall != NULL)
	{
		MsiGetProperty(hInstall, PROP_ICFPATH, szFile, &dwSize);
		SendErrorMessageToMSI(hInstall,NULL, LOG_INFO,_T("GetLogDir: Get ICF file(%s)"),szFile);

		if (_taccess(szFile,0) == 0)
		{
			::SetupGetPrivateProfileString(ICF_SECTION_INSTALL, ICF_VALUE_LOG_DIR, _T(""), szTempPath, _countof(szTempPath), szFile);
			SendErrorMessageToMSI(hInstall,NULL, LOG_INFO,_T("GetLogDir: Get the log path(%s) from icf file."),szTempPath);
		}
		else
		{
			dwSize = _countof(szTempPath);
			//get it from property if exist
			MsiGetProperty(hInstall, _T("LOGFOLDER"), szTempPath, &dwSize);
			SendErrorMessageToMSI(hInstall,NULL, LOG_INFO,_T("GetLogDir: Get the log path(%s) from the property LOGFOLDER."),szTempPath);
		}
	}

	if (_tcslen(szTempPath) < 1)
	{
		//get the temp path
		//first get the system temp
		::GetSystemWindowsDirectory(szTempPath, _countof(szTempPath));
		
		if (_tcslen(szTempPath) < 1)
		{
			//get the user temp if the system don't exist
			memset(szTempPath, 0, sizeof(szTempPath));
			::GetTempPath(MAX_PATH, szTempPath);
		}
		else
		{
			//get the system path
			PathAppend(szTempPath, _T("Temp"));
			PathAppend(szTempPath, PATH_UNINSTALL);
			MakeSurePathExists(szTempPath, FALSE);
		}

		SendErrorMessageToMSI(hInstall,NULL, LOG_INFO,_T("GetLogDir: Get the log path(%s) from system."),szTempPath);
	}

	PathAppend(szTempPath, _T("\\"));
	_stprintf_s(lptValue,size, _T("%s"),szTempPath);

	
	if (_tcslen(lptValue) > 2)
	{
		bRet = TRUE;
	}

	SendErrorMessageToMSI(hInstall,LOG_FILE_D2D_PREFIX, LOG_INFO,_T("GetLogDir %s"), _T("End"));
	return bRet;
}

DWORD CopyFileUltra( LPCTSTR lpExistingFileName,LPCTSTR lpNewFileName,BOOL bFailIfExists)
{
	DWORD dwRet = ERROR_SUCCESS;
	TCHAR szFilePath[MAX_PATH];
	memset(szFilePath,0,sizeof(szFilePath));

	if(!GetFilePath(lpNewFileName,szFilePath,_countof(szFilePath)))
	{
		SendErrorMessageToMSI(NULL,NULL, LOG_ERROR,_T("CopyFileUltra: fail to get the file path of (%s)."),lpExistingFileName);
		dwRet = ERROR_COMMON;
		return dwRet;
	}

	if(_taccess(szFilePath,0) == -1)
	{
		//path does not exist, create it.
		if(ERROR_SUCCESS !=  CreateDirectoryRecursively(szFilePath))
		{
			SendErrorMessageToMSI(NULL,NULL, LOG_ERROR,_T("CopyFileUltra: fail to create the folder(%s) by calling CreateDirectoryRecursively."),szFilePath);
			dwRet = ERROR_FILE_NOT_FOUND;
			return dwRet;
		}
	}

	ChangeFileAttributes(lpNewFileName);

	if(!CopyFile(lpExistingFileName,lpNewFileName,bFailIfExists))
	{
		LPVOID lpMsgBuf;
		dwRet = GetLastError(); 

		FormatMessage(
			FORMAT_MESSAGE_ALLOCATE_BUFFER | 
			FORMAT_MESSAGE_FROM_SYSTEM |
			FORMAT_MESSAGE_IGNORE_INSERTS,
			NULL,
			dwRet,
			MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
			(LPTSTR) &lpMsgBuf,
			0, NULL );

		SendErrorMessageToMSI(NULL,NULL, LOG_ERROR,_T("CopyFileUltra: fail to copy the temp file(%s) to the output exe file(%s),Error:%d,%s"),lpExistingFileName,lpNewFileName,dwRet,lpMsgBuf);
		LocalFree(lpMsgBuf);
		return dwRet;
	}

	return dwRet;

}

void ChangeFileAttributes(const TCHAR* szFileName)
{
	if(_taccess(szFileName,0) != 0)
	{
		//file not exist.
		return ;
	}

	DWORD dwFileAttributes = GetFileAttributes(szFileName);

	if ((dwFileAttributes & FILE_ATTRIBUTE_READONLY) == 
		FILE_ATTRIBUTE_READONLY)
	{
		dwFileAttributes ^= FILE_ATTRIBUTE_READONLY;
		SetFileAttributes(szFileName, dwFileAttributes);
	}	
}

// get the user profile paths for all the users
void AddUserProfilesFolder(CStringArray& strArray)
{
	TCHAR szProfilePath[MAX_PATH];
	TCHAR szTempPath[MAX_PATH];
	TCHAR szTempString[255];
	DWORD dwDataSize=255;
	HKEY hKey;
	HKEY hKeyID;
	int nCount = 0;

	if (::RegOpenKey(HKEY_LOCAL_MACHINE, _T("Software\\Microsoft\\Windows NT\\CurrentVersion\\ProfileList"), &hKey) == ERROR_SUCCESS)
	{
		memset(szTempString, 0, sizeof(szTempString));
		for (int i = 0; ::RegEnumKey(hKey, i, szTempString, 255) != ERROR_NO_MORE_ITEMS; i++)
		{
			if ('\0' == szTempString[0])
				continue;

			if (::RegOpenKeyEx(hKey, szTempString, 0, KEY_READ, &hKeyID) != ERROR_SUCCESS)
				continue;

			dwDataSize = sizeof(szProfilePath);
			if (ERROR_SUCCESS != ::RegQueryValueEx(hKeyID, _T("ProfileImagePath"), NULL, NULL, (LPBYTE)szProfilePath, &dwDataSize))
			{
				::RegCloseKey(hKeyID);
				continue;
			}

			// replace the "%SystemDrive%" with the actual parameter in profile path
			if (_tcschr(szProfilePath, '%'))
			{
				_tcscpy_s(szTempPath,_countof(szTempPath), szProfilePath);
				memset(szProfilePath, 0, sizeof(szProfilePath));
				::ExpandEnvironmentStrings(szTempPath, szProfilePath, _countof(szProfilePath));
			}

			if (_tcslen(szProfilePath) > 0)
			{
				strArray.Add(szProfilePath);
			}

			::RegCloseKey(hKeyID);
		}

		::RegCloseKey(hKey);
	}
	else
	{
		SendErrorMessageToMSI(NULL,NULL,LOG_ERROR,_T("RegOpenKey failed with %d"), ::GetLastError());
	}
}

BOOL SetRebootFlagFile(MSIHANDLE hInstall,LPCTSTR pszLogFileName)
{
	TCHAR szFile[MAX_PATH];
	::GetSystemWindowsDirectory(szFile, MAX_PATH);
	::PathAppend(szFile, _T("Temp"));
	::PathAppend(szFile, REBOOT_FLAG_FILE);

	SendErrorMessageToMSI(hInstall,pszLogFileName,LOG_INFO,_T("SetRebootFlagFile %s."),_T("Start"));

	if (_taccess(szFile, 0) != 0)
	{
		HANDLE hFile = ::CreateFile(szFile, GENERIC_ALL, 0, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
		if (hFile != INVALID_HANDLE_VALUE)
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName,LOG_INFO,_T("Create the file(%s) successfully."), szFile);
			::CloseHandle(hFile);
		}
		else
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName,LOG_WARNING,_T("Fail to create the file(%s)."), szFile);
		}
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName,LOG_INFO,_T("This file(%s) exist. No need to create it."), szFile);
	}

	//write the reboot message into file
	if (_taccess(szFile, 0) != -1)
	{
		TCHAR szData[MAX_PATH*2];
		memset(szData, 0, sizeof(szData));
		DWORD cchValue = _countof(szData);
		MsiGetProperty(hInstall, PROP_REBOOTMSG, szData, &cchValue);

		if(_tcsclen(szData) < 1)
		{
			//Windows Installer requires a reboot
			_stprintf_s(szData,_countof(szData),_T("Windows Installer requires a system restart."));
		}

		// Get current time
		//---------------------------------------------------------------
		TCHAR szTime[MAX_PATH];
		memset(szTime, 0, sizeof(szTime));

		struct tm newTime;
		__time64_t long_time;

		// Get time as 64-bit integer.
		_time64(&long_time); 
		// Convert to local time.
		_localtime64_s(&newTime, &long_time);

		_stprintf_s(szTime,_countof(szTime),_T("%4d-%02d-%02d %02d:%02d:%02d"),
			newTime.tm_year+1900,newTime.tm_mon+1, newTime.tm_mday, newTime.tm_hour, 
			newTime.tm_min, newTime.tm_sec);
		::WritePrivateProfileString(_T("Uninstall"),_T("RebootTime"), szTime, szFile);
		::WritePrivateProfileString(_T("Uninstall"),_T("RebootReason"), szData, szFile);
	}


	BOOL bRet = ::MoveFileEx(szFile, NULL, MOVEFILE_DELAY_UNTIL_REBOOT);

	if(bRet)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName,LOG_INFO,_T("Call MoveFileEx with MOVEFILE_DELAY_UNTIL_REBOOT successfully."));
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName,LOG_WARNING,_T("Fail to call MoveFileEx with MOVEFILE_DELAY_UNTIL_REBOOT. ErrorCode:%d"), ::GetLastError());
	}

	SendErrorMessageToMSI(hInstall,pszLogFileName,LOG_INFO,_T("SetRebootFlagFile %s."),_T("End"));

	return bRet;
}

BOOL MakeSurePathExists(LPCTSTR lpctPath, BOOL FilenameIncluded)
{
	LPTSTR lptPath = (LPTSTR)lpctPath;
	LPTSTR lptTmpPath = lptPath;
	LPTSTR lptLastDPtr = NULL;
	TCHAR TmpSmb = 0;

	while ((lptTmpPath = _tcspbrk(lptTmpPath + 1, _T("\\/"))))
	{
		TmpSmb = lptPath[lptTmpPath - lptPath];
		lptPath[lptTmpPath - lptPath] = 0;
		::CreateDirectory(lptPath, NULL);
		lptPath[lptTmpPath - lptPath] = TmpSmb;
		lptLastDPtr = lptTmpPath;
	}

	BOOL bRes = FALSE;
	if (!FilenameIncluded)
	{
		::CreateDirectory(lpctPath, NULL);

		DWORD dwAtt = ::GetFileAttributes(lpctPath);
		if (INVALID_FILE_ATTRIBUTES != dwAtt)
			bRes = (FILE_ATTRIBUTE_DIRECTORY & dwAtt);
	}
	else
	{
		if (lptLastDPtr)
		{
			lptPath = (TCHAR*)lpctPath;
			TmpSmb = lptPath[lptLastDPtr - lptPath];
			lptPath[lptLastDPtr - lptPath] = 0;

			DWORD dwAtt = GetFileAttributes(lptPath);
			if (INVALID_FILE_ATTRIBUTES != dwAtt)
				bRes = (FILE_ATTRIBUTE_DIRECTORY & dwAtt);
		}
	}

	return bRes;
}

BOOL GetSetupCommonPath(MSIHANDLE hInstall,CString& strPath,LPCTSTR pszLogFileName)
{
	TCHAR szProgramFilesDir[MAX_PATH] = {0};
	if (GetProgramFilesFolder(hInstall,TRUE, NULL, szProgramFilesDir, MAX_PATH,pszLogFileName,TRUE))
	{
		strPath.Format(_T("%s\\%s"), szProgramFilesDir,DEFAULT_SETUPCOMMON_SUBPATH);
		SendErrorMessageToMSI(hInstall,pszLogFileName,LOG_INFO,_T("GetSetupCommonPath: get the setupcommon path(%s)."),strPath);
		return TRUE;
	}
	
	SendErrorMessageToMSI(hInstall,pszLogFileName,LOG_INFO,_T("GetSetupCommonPath: fail to get the setupcommon path."));

	return FALSE;
}
