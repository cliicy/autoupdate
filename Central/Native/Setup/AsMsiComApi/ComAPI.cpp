#include "stdafx.h"
#include "ComAPI.h"
#include "..\..\RCModules\Setup\AsetupRes.dll\resource.h"
#include "EdgeSetupDefine.h"

#include <io.h>
#include <shlwapi.h>
#include <Psapi.h>
#include <tlhelp32.h>  
#include <atlbase.h>


#define MAX_BUF_SIZE          4096
#define MAX_MES_BUS			  2048
#define MAX_BUF_BUS			  2048

#define MAX_ARRAY_SIZE        100

#define SETUP_TIMEOUT_ERROR    100000

#pragma comment( lib, "Psapi.lib")

TCHAR g_szLogPath[MAX_PATH];
TCHAR g_szLogFile[MAX_PATH];
typedef LONG (WINAPI  *PFRegDelEx) (__in HKEY hKey,__in LPCTSTR lpSubKey,__in REGSAM samDesired,__reserved DWORD Reserved);

///////////////////////////////////////////////////////////////////////////////////

//***********************************************************//
//***          please add non-custom action	here   	       **//
//***********************************************************//

void WriteTLog(const TCHAR* pszLogFileName,LOG_TYPE type,const TCHAR* pszFormat, ...)
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
			PathAppend(szTempDir, PATH_UDP_UNINSTALL);
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
DWORD SendErrorMessageToMSI(MSIHANDLE hInstall,const TCHAR* pszLogFileName,LOG_TYPE type,TCHAR *format,...)
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
		if (_tcsicmp(lpMachine, szBuffer))
		{
			bIsLocal = FALSE;
		}
	}

	if (!bIsLocal)
	{
		if (::RegConnectRegistry(lpMachine, HKEY_LOCAL_MACHINE, &hConnectKey) != ERROR_SUCCESS)
			return FALSE;

		if (::RegOpenKeyEx(hConnectKey, EDGE_REGKEY_MICROSOFT_WINDOWS, 0, KEY_READ, &hKey) != ERROR_SUCCESS)
			return FALSE;
	}
	else
	{

		if (::RegOpenKeyEx(HKEY_LOCAL_MACHINE,EDGE_REGKEY_MICROSOFT_WINDOWS,0,KEY_READ,&hKey) != ERROR_SUCCESS)
			return FALSE;
	}
	dwDataSize = sizeof(szData);

	if (::RegQueryValueEx(hKey, _T("ProgramFilesDir (x86)"), NULL, NULL, (LPBYTE)szData, &dwDataSize)==ERROR_SUCCESS)
		return TRUE;

	return FALSE;
}

HMODULE GetSetupRC(MSIHANDLE hInstall,const TCHAR* pszLogFileName)
{
	HMODULE hModule = NULL;
	TCHAR szFile[MAX_PATH];
	TCHAR szInstallDir[MAX_PATH];
	DWORD dwSize;
	memset(szFile, 0, sizeof(szFile));
	memset(szInstallDir,0,sizeof(szInstallDir));

	//get install path
	dwSize = _countof(szInstallDir);
	MsiGetProperty(hInstall, _T("INSTALLDIR"), szInstallDir, &dwSize);
	_stprintf_s(szFile,_countof(szFile),_T("%s"),szInstallDir);
	PathAppend(szFile,COMMONSETUP_PATH);
	PathAppend(szFile,FILE_ASETUPRES_DLL);

	if (_taccess(szFile,0) !=0)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING, _T("Can not access the file (%s). Fail to load the file."), szFile);
	}
	else
	{
		hModule = LoadLibraryEx(szFile, NULL, LOAD_WITH_ALTERED_SEARCH_PATH);

		if (!hModule)
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING, _T("Fail to load the file(%s). ErrorCode:%d"), szFile,GetLastError());
			return NULL;
		}
		else
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO, _T("Load the file(%s) successfully."), szFile);
		}
	}

	return hModule;
}

void FreeSetupRC(HINSTANCE hModule)
{
	if (hModule)
	{
		FreeLibrary(hModule);
	}
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
	WriteTLog(NULL, LOG_INFO, _T("StopSpecService: prepare to stop service(%s)."), lpszServiceName);
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
			SendErrorMessageToMSI(NULL,LOG_FILE_DEFAULT_PREFIX, LOG_ERROR, _T("StopSpecService: Fail to stop service(%s) because of depneding on the other service."), lpszServiceName);
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
				break;
			}
			//if (dwLastCheckPoint >= ServiceStatus.dwCheckPoint)
			//   break;

			// Do not wait more than suggested
			if (dwMaxWaitTime <= 0)
			{
				dwRet = SETUP_TIMEOUT_ERROR;
				WriteTLog(NULL, LOG_INFO, _T("StopSpecService: Fail to stop service(%s) with timeout."), lpszServiceName);
				break;
			}

			dwMaxWaitTime--;
		}
	}
	else
	{
		WriteTLog(NULL, LOG_INFO, _T("StopSpecService: The block flag is false, No need wait to stop service(%s)."), lpszServiceName);
	}


	//ISC 11/16/98: According to Chie's request to return TRUE only when STOPPED
	//return ServiceStatus.dwCurrentState == SERVICE_STOPPED || ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING;
	if(ServiceStatus.dwCurrentState == SERVICE_STOPPED)
	{
		WriteTLog(NULL, LOG_INFO, _T("StopSpecService: stop the service(%s) successfully."), lpszServiceName);
		return ERROR_SUCCESS;
	}
	else if(ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING)
	{
		WriteTLog(NULL, LOG_INFO, _T("StopSpecService: the service(%s) is still stopping. its satatus is SERVICE_STOP_PENDING."), lpszServiceName);
		return ERROR_SUCCESS;
	}
	else
	{
		WriteTLog(NULL, LOG_WARNING, _T("StopSpecService: fail to stop the service(%s), the service status(%d)."), lpszServiceName,ServiceStatus.dwCurrentState);
	}
	
	return dwRet;
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
		WriteTLog(NULL, LOG_INFO, _T("StopSpecService: Fail to call OpenSCManager() for service(%s). Error:%d"), lpszServiceName,dwRet);
		return dwRet;
	}

	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService == NULL)
	{
		dwRet = GetLastError();
		WriteTLog(NULL, LOG_INFO, _T("StopSpecService: Fail to call OpenService() for service(%s). Error:%d"), lpszServiceName,dwRet);
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

	WriteTLog(NULL, LOG_INFO, _T("StopSpecService: return code(%d)"),dwRet);
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
UINT IsProcessRunning(LPTSTR szKillProc, LPTSTR szFilePath,BOOL bOutputAllProcListTolog)
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

		if (_tcsicmp(tlist[i].ProcessName, szKillProc) == 0) 
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
DWORD KillProcess(DWORD dwId, LPCTSTR pszLogFileName)
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
						DWORD dwRetKillProcess = WaitForSingleObject(hProc, dwSleepTime);
						WriteTLog(pszLogFileName, LOG_INFO, _T("KillProcess: return code (0x%x) for process id (%d) by calling WaitForSingleObject API."), dwRetKillProcess, dwId);
						if (dwRetKillProcess == WAIT_TIMEOUT)
						{
							//timeout
							dwRet = WAIT_TIMEOUT;
							WriteTLog(pszLogFileName, LOG_WARNING, _T("KillProcess: Call API TerminateProcess() successfully. but the process(%d) doesn't exit within timeout(%ds)."), dwId, (dwSleepTime / 1000));
						}
						else if (WAIT_OBJECT_0 == dwRetKillProcess)
						{
							//success
							dwRet = ERROR_SUCCESS;
						}
						else
						{
							dwRet = GetLastError();
							LPVOID lpMsgBuf;
							TCHAR  szTempMesg[MAX_BUF] = { 0 };
							FormatMessage(
								FORMAT_MESSAGE_ALLOCATE_BUFFER |
								FORMAT_MESSAGE_FROM_SYSTEM |
								FORMAT_MESSAGE_IGNORE_INSERTS,
								NULL,
								dwRet,
								MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
								(LPTSTR)&lpMsgBuf,
								0,
								NULL
								);

							WriteTLog(pszLogFileName, LOG_WARNING, _T("KillProcess: Call API TerminateProcess() successfully. but fail to call WaitForSingleObject. Error:%s"), (LPCTSTR)lpMsgBuf);

							//Free the buffer.
							LocalFree(lpMsgBuf);
						}
					}
					else
					{
						dwRet = GetLastError();
						LPVOID lpMsgBuf;
						TCHAR  szTempMesg[MAX_BUF] = { 0 };
						FormatMessage(
							FORMAT_MESSAGE_ALLOCATE_BUFFER |
							FORMAT_MESSAGE_FROM_SYSTEM |
							FORMAT_MESSAGE_IGNORE_INSERTS,
							NULL,
							dwRet,
							MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
							(LPTSTR)&lpMsgBuf,
							0,
							NULL
							);

						WriteTLog(pszLogFileName, LOG_WARNING, _T("KillProcess: fail to terminate the process id(%d) by calling TerminateProcess API. Error:%s"), dwId, (LPCTSTR)lpMsgBuf);
						//Free the buffer.
						LocalFree(lpMsgBuf);
					}

					CloseHandle(hProc);
				}
				else
				{
					dwRet = GetLastError();
					LPVOID lpMsgBuf;
					TCHAR  szTempMesg[MAX_BUF] = { 0 };
					FormatMessage(
						FORMAT_MESSAGE_ALLOCATE_BUFFER |
						FORMAT_MESSAGE_FROM_SYSTEM |
						FORMAT_MESSAGE_IGNORE_INSERTS,
						NULL,
						dwRet,
						MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
						(LPTSTR)&lpMsgBuf,
						0,
						NULL
						);

					WriteTLog(pszLogFileName, LOG_WARNING, _T("KillProcess: fail to open the process id(%d) by calling OpenProcess API. Error:%s"), dwId, (LPCTSTR)lpMsgBuf);
					//Free the buffer.
					LocalFree(lpMsgBuf);
				}

				AdjustTokenPrivileges(hToken, FALSE, &tkpPrev, sizeof(TOKEN_PRIVILEGES), NULL, NULL);
			}
			else
			{
				dwRet = GetLastError();
				LPVOID lpMsgBuf;
				TCHAR  szTempMesg[MAX_BUF] = { 0 };
				FormatMessage(
					FORMAT_MESSAGE_ALLOCATE_BUFFER |
					FORMAT_MESSAGE_FROM_SYSTEM |
					FORMAT_MESSAGE_IGNORE_INSERTS,
					NULL,
					dwRet,
					MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
					(LPTSTR)&lpMsgBuf,
					0,
					NULL
					);

				WriteTLog(pszLogFileName, LOG_WARNING, _T("KillProcess: Fail to AdjustTokenPrivileges(%s)."), (LPCTSTR)lpMsgBuf);
				//Free the buffer.
				LocalFree(lpMsgBuf);
			}
		}
		else
		{
			dwRet = GetLastError();
			LPVOID lpMsgBuf;
			TCHAR  szTempMesg[MAX_BUF] = { 0 };
			FormatMessage(
				FORMAT_MESSAGE_ALLOCATE_BUFFER |
				FORMAT_MESSAGE_FROM_SYSTEM |
				FORMAT_MESSAGE_IGNORE_INSERTS,
				NULL,
				dwRet,
				MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
				(LPTSTR)&lpMsgBuf,
				0,
				NULL
				);

			WriteTLog(pszLogFileName, LOG_WARNING, _T("KillProcess: fail to LookupPrivilegeValue(%s)."), (LPCTSTR)lpMsgBuf);
			// Free the buffer.
			LocalFree(lpMsgBuf);
		}
	}
	else
	{
		dwRet = GetLastError();
		LPVOID lpMsgBuf;
		TCHAR  szTempMesg[MAX_BUF] = { 0 };
		FormatMessage(
			FORMAT_MESSAGE_ALLOCATE_BUFFER |
			FORMAT_MESSAGE_FROM_SYSTEM |
			FORMAT_MESSAGE_IGNORE_INSERTS,
			NULL,
			dwRet,
			MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
			(LPTSTR)&lpMsgBuf,
			0,
			NULL
			);

		WriteTLog(pszLogFileName, LOG_WARNING, _T("KillProcess: fail to OpenProcessToken(%s)."), (LPCTSTR)lpMsgBuf);
		// Free the buffer.
		LocalFree(lpMsgBuf);
	}

	return dwRet;
}

//find and kill (if szPath is NULL, will ignore it)
BOOL KillProcesses(LPTSTR szKillProc, LPTSTR szFilePath)
{
	return KillProcessesEx(szKillProc, szFilePath, NULL);
}

BOOL KillProcessesEx(LPTSTR szKillProc, LPTSTR szFilePath, LPCTSTR pszLogFileName)
{
	BOOL bRet = FALSE;
	DWORD numTasks;
	TASK_LIST *tlist = new TASK_LIST[MAX_TASKS];
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

	numTasks = GetTaskListNT(tlist, MAX_TASKS, FALSE);

	for (i = 0; i<(int)numTasks; i++)
	{
		if (_tcsnicmp(tlist[i].ProcessName, szKillProc, _countof(tlist[i].ProcessName)) == 0)
		{
			//check the path
			strPath = tlist[i].szExePath;

			if (Is_64Bit_System(NULL))
			{
				//the path is like "\device\xxx\"
				if (strFilePath.IsEmpty())
				{
					WriteTLog(pszLogFileName, LOG_INFO, _T("KillProcesses1: start to terminate the process (ID:%d,Name:%s,Path:%s)."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
					if (ERROR_SUCCESS == KillProcess(tlist[i].dwProcessId, pszLogFileName))
					{
						bRet = TRUE;
						WriteTLog(pszLogFileName, LOG_INFO, _T("KillProcesses1: terminate the process (ID:%d,Name:%s,Path:%s) successfully."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
					}
					else
					{
						WriteTLog(pszLogFileName, LOG_WARNING, _T("KillProcesses1: fail to terminate the process (ID:%d,Name:%s,Path:%s)."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
					}
					WriteTLog(pszLogFileName, LOG_INFO, _T("KillProcesses1: end to terminate the process (ID:%d,Name:%s,Path:%s)."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
					continue;
				}

				//remove the driver
				CString strFilePathTemp = strFilePath.Right(strFilePath.GetLength() - 2);
				CString strFileShortPathTemp = strFileShortPath.Right(strFileShortPath.GetLength() - 2);

				if (strPath.Find(strFilePathTemp) >= 0 || (!strFileShortPathTemp.IsEmpty() && strPath.Find(strFileShortPathTemp) >= 0))
				{
					WriteTLog(pszLogFileName, LOG_INFO, _T("KillProcesses2: start to terminate the process (ID:%d,Name:%s,Path:%s)."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
					if (ERROR_SUCCESS == KillProcess(tlist[i].dwProcessId, pszLogFileName))
					{
						bRet = TRUE;
						WriteTLog(pszLogFileName, LOG_INFO, _T("KillProcesses2: terminate the process (ID:%d,Name:%s,Path:%s) successfully."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
					}
					else
					{
						WriteTLog(pszLogFileName, LOG_WARNING, _T("KillProcesses2: fail to terminate the process (ID:%d,Name:%s,Path:%s)."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
					}

					WriteTLog(pszLogFileName, LOG_INFO, _T("KillProcesses2: end to terminate the process (ID:%d,Name:%s,Path:%s)."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
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
					WriteTLog(pszLogFileName, LOG_INFO, _T("KillProcesses3: start to terminate the process (ID:%d,Name:%s,Path:%s)."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
					if (ERROR_SUCCESS == KillProcess(tlist[i].dwProcessId, pszLogFileName))
					{
						bRet = TRUE;
						WriteTLog(pszLogFileName, LOG_INFO, _T("KillProcesses3: terminate the process (ID:%d,Name:%s,Path:%s) successfully."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
					}
					else
					{
						WriteTLog(pszLogFileName, LOG_WARNING, _T("KillProcesses3: fail to terminate the process (ID:%d,Name:%s,Path:%s)."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
					}
					WriteTLog(pszLogFileName, LOG_INFO, _T("KillProcesses3: start to terminate the process (ID:%d,Name:%s,Path:%s)."), tlist[i].dwProcessId, tlist[i].ProcessName, tlist[i].szExePath);
				}

				continue;
			}
		}
	}

	if (tlist != NULL)
	{
		delete[] tlist;
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
	TCHAR			szMessage[MAX_PATH*2];
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

UINT GetWebPort()
{
	UINT nRet = 0;
	HKEY	hKey, hConnectKey;
	BOOL	bReturn = TRUE;
	TCHAR	szValue[256];
	DWORD	dwSize = sizeof(DWORD);
	//TCHAR	szRegKey[MAX_PATH] = REG_ARCFLASH_VERSION;


	hConnectKey = HKEY_LOCAL_MACHINE;

	REGSAM samDesired = KEY_READ;

	if (Is_64Bit_System(NULL))
	{
		samDesired = KEY_READ|KEY_WOW64_64KEY;
	}

	if ((RegOpenKeyEx(hConnectKey, REG_MANAGEMENT_WEBSERVER, 0, samDesired, &hKey)) == ERROR_SUCCESS)
	{
		dwSize = sizeof(szValue);
		memset(szValue, 0, dwSize);
		if (RegQueryValueEx(hKey, REG_VALUE_PORT, NULL, NULL, (LPBYTE)szValue, &dwSize) != ERROR_SUCCESS)
		{
			SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("Fail to open the key value(%s) under the key(%s)."), REG_VALUE_PORT,REG_MANAGEMENT_WEBSERVER);
			bReturn = FALSE;
		}
		else
		{
			nRet = _ttoi(szValue);
		}

		RegCloseKey(hKey);
	}
	else
	{
		SendErrorMessageToMSI(NULL,NULL, LOG_WARNING,_T("Fail to open the key(%s)."), REG_MANAGEMENT_WEBSERVER);
	}
	
	return nRet;
}

BOOL UpdateRegUninstallString(MSIHANDLE hInstall,LPCTSTR szInstallDir,const TCHAR* pszLogFileName)
{

	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_ERROR,_T("UpdateRegUninstallString: No need to handle it."));
	return FALSE;

	TCHAR szUninstallString[MAX_PATH*4];
	TCHAR szUninstallExe[MAX_PATH];
	TCHAR szProductCode[MAX_PATH];
	TCHAR szRegistryKey[MAX_PATH];

	ZeroMemory(szUninstallString,sizeof(szUninstallString));
	ZeroMemory(szUninstallExe,sizeof(szUninstallExe));
	ZeroMemory(szProductCode,sizeof(szProductCode));
	ZeroMemory(szRegistryKey,sizeof(szRegistryKey));

	DWORD dwSize = _countof(szProductCode);
	MsiGetProperty(hInstall, _T("ProductCode"), szProductCode, &dwSize);
	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO, _T("UpdateRegUninstallString: Get property ProductCode(%s)"), szProductCode);

	_stprintf_s(szUninstallExe,_countof(szUninstallExe),_T("%s"),szInstallDir);
	PathAppend(szUninstallExe,NAME_SETUP);
	PathAppend(szUninstallExe,_T("Uninstall.exe"));

	if (_taccess(szUninstallExe,0) != 0)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_ERROR,_T("UpdateRegUninstallString: Cannot access the file (%s). It will affect uninstallation."), szUninstallExe);
		return FALSE;
	}

	_stprintf_s(szUninstallString,_countof(szUninstallString),_T("\"%s\" /p %s /q /msiui"),szUninstallExe,szProductCode);

	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO, _T("UpdateRegUninstallString: The uninstall string is (%s)"), szUninstallString);

	_stprintf_s(szRegistryKey,_countof(szRegistryKey),_T("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\%s"),szProductCode);

	BOOL bRet=SetRegValue(HKEY_LOCAL_MACHINE,szRegistryKey,_T("UninstallString"),szUninstallString,REG_SZ);
	if (!bRet)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_ERROR,_T("UpdateRegUninstallString: Fail to change the registry UninstallString under %s."), szRegistryKey);
		return FALSE;
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("UpdateRegUninstallString: Change the registry UninstallString under %s successfully."), szRegistryKey);
	}

	bRet=SetRegValue(HKEY_LOCAL_MACHINE,szRegistryKey,_T("WindowsInstaller"),_T("0"),REG_DWORD);
	if (!bRet)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_ERROR,_T("UpdateRegUninstallString: Fail to change the registry WindowsInstaller under %s."), szRegistryKey);
		return FALSE;
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("UpdateRegUninstallString: Change the registry WindowsInstaller under %s successfully."), szRegistryKey);
	}

	
	return TRUE;
}

/***************************************************************************/
/*This function deletes all files and sub-directories of a directory
/***************************************************************************/
BOOL DeleteTree(LPCTSTR lpszPath, const TCHAR* pszLogFileName)
{
	WIN32_FIND_DATA FindData;
	TCHAR szPath[MAX_PATH] = {0};
	TCHAR szNewPath[_MAX_PATH] = {0};
	TCHAR wild[] = _T("*.*");
	HANDLE hFile = NULL;
	BOOL bDeleteTree = TRUE;

	_stprintf_s(szPath, _countof(szPath), _T("%s"), lpszPath);
	PathAddBackslash(szPath);

	_stprintf_s(szNewPath, _countof(szNewPath), _T("%s%s"), szPath, wild);

	if ((hFile = FindFirstFile(szNewPath, &FindData)) != INVALID_HANDLE_VALUE)
	{
		do
		{
			_stprintf_s(szNewPath, _countof(szNewPath), _T("%s\\%s"), lpszPath, FindData.cFileName);

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

BOOL GetSetupResIni(MSIHANDLE hInstall,const TCHAR* pszLogFileName,TCHAR* szSetupResIniFile,DWORD csSize)
{
	BOOL bRet = FALSE;
	TCHAR szIniFile[MAX_PATH];
	TCHAR szOriginalDatabase[MAX_PATH];
	DWORD dwSize = _countof(szOriginalDatabase);
	ZeroMemory(szIniFile,sizeof(szIniFile));
	ZeroMemory(szOriginalDatabase,sizeof(szOriginalDatabase));

	LOG_FUNCTION_BEGIN(hInstall,pszLogFileName);

	MsiGetProperty(hInstall, PROP_ORIGINALDATABASE, szOriginalDatabase, &dwSize);
	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Get the property %s(%s)."),PROP_ORIGINALDATABASE, szOriginalDatabase);

	_tcscpy_s(szIniFile,_countof(szIniFile), szOriginalDatabase);
	::PathRemoveFileSpec(szIniFile);
	PathAppend(szIniFile, _T("..\\..\\Install\\setupres.ini"));
	
	if (_taccess(szIniFile,0) == 0)
	{
		_tcscpy_s(szSetupResIniFile,csSize, szIniFile);
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Get the ini file(%s)."), szIniFile);
		bRet = TRUE;
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("cannot access the file(%s)."), szIniFile);
	}

	LOG_FUNCTION_END(hInstall,pszLogFileName);
	return bRet;
}

LONG RecursiveDeleteKey(MSIHANDLE hInstall,const TCHAR* pszLogFileName,HKEY hKeyParent, LPCTSTR lpszKeyChild, BOOL bNative)
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


BOOL RemoveRegistry(MSIHANDLE hInstall,const TCHAR* pszLogFileName,HKEY hKeyParent, LPCTSTR lpszKeyChild)
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
	if (IsEmptyKey(hKeyParent, NAME_ARCSERVE))
	{
		SendErrorMessageToMSI(hInstall, pszLogFileName, LOG_INFO, _T("The key (%s) is empty, need remove."), NAME_ARCSERVE);

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
				dwRet = lpfRegDelEx(HKEY_LOCAL_MACHINE, NAME_ARCSERVE, regSam, 0);
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
				SendErrorMessageToMSI(hInstall, pszLogFileName, LOG_INFO, _T("Remove the empty key (%s) successfully."), NAME_ARCSERVE);
			}
			else
			{
				TCHAR szBuffer[1024];
				memset(szBuffer,0,sizeof(szBuffer));
				int nLastError = GetLastError();
				_stprintf_s(szBuffer, _countof(szBuffer), _T("Fail to remove the empty key (%s). Error:%d"), NAME_ARCSERVE, nLastError);
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"), szBuffer);
				bRet = FALSE;
			}
		}
		else
		{
			if (ERROR_SUCCESS == RegDeleteKey(HKEY_LOCAL_MACHINE, NAME_ARCSERVE))
			{
				SendErrorMessageToMSI(hInstall, pszLogFileName, LOG_INFO, _T("Remove the empty key (%s) successfully."), NAME_ARCSERVE);
			}
			else
			{
				TCHAR szBuffer[1024];
				memset(szBuffer,0,sizeof(szBuffer));
				int nLastError = GetLastError();
				_stprintf_s(szBuffer, _countof(szBuffer), _T("Fail to remove the empty key (%s). Error:%d"), NAME_ARCSERVE, nLastError);
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"), szBuffer);
				bRet = FALSE;
			}
		}
	}
	else
	{
		SendErrorMessageToMSI(hInstall, pszLogFileName, LOG_INFO, _T("The key (%s) is not empty, no need remove."), NAME_ARCSERVE);
	}
	//end fix 19052674

	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("%s"),_T("End of cleaning up registry"));

	return bRet;
}

DWORD StopServiceForUninstall(MSIHANDLE hInstall,LPCTSTR lpszServiceName,const TCHAR* pszLogFileName)
{
	DWORD dwRet = ERROR_SUCCESS;

	//stop web service
	CString strFlag;
	strFlag.Format(_T("%s_FLAG"),lpszServiceName);
	strFlag.MakeUpper();

	if (IsServiceStarted(lpszServiceName))
	{
		dwRet = StopSpecService(lpszServiceName,TRUE,FALSE);
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

BOOL GetSetupCommonInstallPath(LPTSTR lpszDir, DWORD& dwSize)
{
	
	if(!GetProgramFilesPath(lpszDir, dwSize, TRUE))
	{
		return FALSE;
	}

	PathAppend(lpszDir,SETUPCOMMON_SUBPATH);

	return TRUE;

}

BOOL GetProgramFilesPath(LPTSTR lpszProgFilesDir, DWORD& dwSize, BOOL bGet32On64OS)
{
	HKEY hKey = NULL;
	BOOL bIs64BitOS = Is_64Bit_System(NULL);

	BOOL bRet = FALSE;
	HRESULT hr = ERROR_SUCCESS;

	if (bIs64BitOS)
		hr = ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, EDGE_REGKEY_MICROSOFT_WINDOWS, 0, KEY_READ|KEY_WOW64_64KEY, &hKey);
	else
		hr = ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, EDGE_REGKEY_MICROSOFT_WINDOWS, 0, KEY_READ, &hKey);

	if (ERROR_SUCCESS != hr)
		return FALSE;

	if (bIs64BitOS && bGet32On64OS)
		hr = ::RegQueryValueEx(hKey, EDGE_REGVALUE_PROGRAM_FILES_DIR_X86, NULL, NULL, (LPBYTE)lpszProgFilesDir, &dwSize);
	else
		hr = ::RegQueryValueEx(hKey, EDGE_REGVALUE_PROGRAM_FILES_DIR, NULL, NULL, (LPBYTE)lpszProgFilesDir, &dwSize);

	::RegCloseKey(hKey);

	return ERROR_SUCCESS == hr;
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

DWORD GetFolderList(MSIHANDLE hInstall,const TCHAR* pszLogFileName,LPCTSTR szInf,CStringArray& strSrcPathArray,CStringArray& strDesPathArray)
{
	if(_taccess(szInf,0) != 0)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("cannot access the file (%s)."), szInf);
		return ERROR_FILE_NOT_FOUND;
	}
	
	CString strSrcItem,strDestItem;
	TCHAR szSrcValue[MAX_PATH];
	TCHAR szDestValue[MAX_PATH];
	
	strSrcPathArray.RemoveAll();
	strDesPathArray.RemoveAll();

	for(int i=0; i<MAX_ARRAY_SIZE; i++)
	{
		ZeroMemory(szSrcValue,sizeof(szSrcValue));
		ZeroMemory(szDestValue,sizeof(szDestValue));

		strSrcItem.Format(_T("%s%d"),SETUPINF_VALUE_SRCPATH,i);
		strDestItem.Format(_T("%s%d"),SETUPINF_VALUE_DESTPATH,i);
		
		//get source path
		GetPrivateProfileString(SETUPINF_SECTION_FOLDERCOPY, strSrcItem, _T(""), szSrcValue, _countof(szSrcValue), szInf);

		//get dest path
		GetPrivateProfileString(SETUPINF_SECTION_FOLDERCOPY, strDestItem, _T(""), szDestValue, _countof(szDestValue), szInf);

		if(_tcslen(szSrcValue) <= 0)
		{
			//no data
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("There is no source path data for (%s) in setup.inf. No need to continue check."), strSrcItem);
			break;
		}
		else
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Get data: %s=%s;%s=%s from section(%s) in setup.inf."), strSrcItem,szSrcValue,strDestItem,szDestValue,SETUPINF_SECTION_FOLDERCOPY);

			if(_tcslen(szDestValue) <= 0)
			{
				//no data
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("There is no destination path data for (%s) in setup.inf. No need to continue check."), strDestItem);
				break;
			}
			else
			{
				//source path and dest path are correct
				strSrcPathArray.Add(szSrcValue);
				strDesPathArray.Add(szDestValue);
			}
		}
	}
	
	return ERROR_SUCCESS;
}

//Copy the folders from setup.inf, hInstall must not be NULL
DWORD CopyFolderFromImage(MSIHANDLE hInstall,LPCTSTR szInstallDir,const TCHAR* pszLogFileName)
{
	DWORD dwRet = ERROR_SUCCESS;
	TCHAR szSrc[MAX_PATH];
	TCHAR szInf[MAX_PATH];
	TCHAR szOriginalDatabase[MAX_PATH];
	DWORD dwSize = _countof(szOriginalDatabase);
	ZeroMemory(szSrc,sizeof(szSrc));
	ZeroMemory(szInf,sizeof(szInf));
	ZeroMemory(szOriginalDatabase,sizeof(szOriginalDatabase));


	LOG_FUNCTION_BEGIN(hInstall,pszLogFileName);

	MsiGetProperty(hInstall, PROP_ORIGINALDATABASE, szOriginalDatabase, &dwSize);
	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Get the property OriginalDatabase(%s)."), szOriginalDatabase);

	_tcscpy_s(szSrc,_countof(szSrc), szOriginalDatabase);
	::PathRemoveFileSpec(szSrc);

	if(_taccess(szSrc,0) == 0)
	{
		_tcscpy_s(szInf,_countof(szInf), szSrc);
		PathAppend(szInf, _T("Setup.inf"));
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("get the setup.inf file(%s)."), szInf);
		
		if(_taccess(szInf,0) != 0)
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("cannot access the file (%s)."), szInf);
			LOG_FUNCTION_END(hInstall,pszLogFileName);
			return ERROR_FILE_NOT_FOUND;
		}

		CStringArray strSrcPathArray,strDestPathArray;
		GetFolderList(hInstall,pszLogFileName,szInf,strSrcPathArray,strDestPathArray);

		if(strSrcPathArray.GetCount() <=0 || strDestPathArray.GetCount() <=0)
		{
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("No folder need to be copied."));
			LOG_FUNCTION_END(hInstall,pszLogFileName);
			return ERROR_SUCCESS;
		}
		
		CString strSrcPath, strDestPath;
		TCHAR szOriginalSrc[MAX_PATH] = { 0 };

		//keep the root path
		_tcscpy_s(szOriginalSrc, _countof(szOriginalSrc), szSrc);
		for(int i=0; i<strSrcPathArray.GetCount() ; i++)
		{
			_tcscpy_s(szSrc, _countof(szSrc), szOriginalSrc);
			strSrcPath = strSrcPathArray.GetAt(i);
			strSrcPath.Trim();

			strDestPath = strDestPathArray.GetAt(i);
			strDestPath.Trim();

			if(strSrcPath.IsEmpty() || strDestPath.IsEmpty())
			{
				continue;
			}
			
			PathAppend(szSrc,strSrcPath);

			strDestPath.Replace(_T("<INSTALLPATH>"),szInstallDir);
			strDestPath.Replace(_T("\\\\"),_T("\\"));

			if(_taccess(szSrc,0) !=0)
			{
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING, _T("cannot access the source path(%s)."),szSrc);
				continue;
			}

			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Source Path(%s)."), szSrc);
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Destination path:(%s)."), strDestPath);

			if(!CopyFilesRecursive(hInstall,szSrc,strDestPath))
			{
				dwRet = ERROR_CANNOT_COPY;
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING, _T("fail to copy files."), _T(""));
			}
			else
			{
				SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO, _T("copy files successfully."), _T(""));
			}
		}
		
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("fail to access the path(%s), and cannot get the source path."), szSrc);
	}

	LOG_FUNCTION_END(hInstall,pszLogFileName);

	return dwRet;
}

void UpdateD2DRegistry(MSIHANDLE hInstall,const TCHAR* pszLogFileName)
{
	DWORD dwD2DMajorVer = 0;
	DWORD dwD2DMinorVer = 0;
	DWORD dwD2DMajorBuild = 0;
	DWORD dwD2DMinorBuild = 0;

	TCHAR szD2DVersionInfo[MAX_PATH];
	TCHAR szD2DMasterSetupInf[MAX_PATH];
	TCHAR szOriginalDatabase[MAX_PATH];
	DWORD dwSize = _countof(szOriginalDatabase);
	ZeroMemory(szD2DMasterSetupInf,sizeof(szD2DMasterSetupInf));
	ZeroMemory(szOriginalDatabase,sizeof(szOriginalDatabase));
	ZeroMemory(szD2DVersionInfo,sizeof(szD2DVersionInfo));

	LOG_FUNCTION_BEGIN(hInstall,pszLogFileName);

	MsiGetProperty(hInstall, PROP_ORIGINALDATABASE, szOriginalDatabase, &dwSize);
	SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Get the property %s(%s)."),PROP_ORIGINALDATABASE, szOriginalDatabase);

	_tcscpy_s(szD2DMasterSetupInf,_countof(szD2DMasterSetupInf), szOriginalDatabase);
	::PathRemoveFileSpec(szD2DMasterSetupInf);

	if(_taccess(szD2DMasterSetupInf,0) != 0)
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_WARNING,_T("fail to access the path(%s), and cannot get the source path."), szD2DMasterSetupInf);
		return;
	}

	//To set the version info for D2D which is managed by edge
	::PathAppend(szD2DMasterSetupInf, _T("..\\..\\Install\\mastersetup.inf"));

	if(_taccess(szD2DMasterSetupInf,0) == 0)
	{
		::GetPrivateProfileString(_T("BuildNumber"), _T("CurrentVer"), _T(""), szD2DVersionInfo, _countof(szD2DVersionInfo), szD2DMasterSetupInf);
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Get the version(%s) from the file (%s)"), szD2DVersionInfo,szD2DMasterSetupInf);
		_stscanf_s(szD2DVersionInfo, _T("%d.%d.%d.%d"), &dwD2DMajorVer, &dwD2DMinorVer, &dwD2DMajorBuild, &dwD2DMinorBuild);
		if(dwD2DMajorVer>0)
		{
			CString strBuffer;
			strBuffer.Format(_T("%d"), dwD2DMajorVer);
			::MsiSetProperty(hInstall, _T("D2DMAJORVERSION"), strBuffer);
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Set the property D2DMAJORVERSION=%s"),strBuffer);

			strBuffer.Format(_T("%d"), dwD2DMinorVer);
			::MsiSetProperty(hInstall, _T("D2DMINORVERSION"), strBuffer);
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Set the property D2DMINORVERSION=%s"),strBuffer);

			strBuffer.Format(_T("%d"), dwD2DMajorBuild);
			::MsiSetProperty(hInstall, _T("D2DMAJORBUILD"), strBuffer);
			SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("Set the property D2DMAJORBUILD=%s"),strBuffer);
		}
	}
	else
	{
		SendErrorMessageToMSI(hInstall,pszLogFileName, LOG_INFO,_T("fail to access the file(%s), and cannot get the source path."), szD2DMasterSetupInf);
	}

	LOG_FUNCTION_END(hInstall,pszLogFileName);

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



