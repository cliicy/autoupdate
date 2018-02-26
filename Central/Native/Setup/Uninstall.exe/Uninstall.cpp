// Uninstall.cpp : Defines the class behaviors for the application.

#include "stdafx.h"
#include "Uninstall.h"
#include "SetupSheet.h"
#include "UninstallSimpleDlg.h"
#include "ComAPI.h"

#include <msi.h>
#include <time.inl>
#include <psapi.h>
#include "Shlwapi.h"
#include <Winsvc.h>

#ifndef UNICODE
#include <io.h>
#endif

#pragma comment(lib, "msi.lib")
#pragma comment(lib, "Psapi.lib") 


#ifdef _DEBUG
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////////
#define MAX_LIST_DATA           100
#define MAX_BUFSIZE				2048
#define LOG_FILE_NAME_PREFIX		   _T("Uninstall")
#define LOG_UINSTALL_FILE_NAME_PREFIX  _T("Uninstall-History")
#define LOG_FILE_EXT			    _T(".log")

#define FILE_ROLLBACK              _T("-Rollback")

#define UNINSTALL_DLLA			"uninstallres.dll"
#define UNINSTALL_DLLW			L"uninstallres.dll"

#ifdef UNICODE
#define UNINSTALL_DLL			UNINSTALL_DLLW
#else
#define UNINSTALL_DLL			UNINSTALL_DLLA
#endif


//section
#define INI_PRODUCTCODE				 _T("ProductCode")
#define INI_PRODUCTNAME				 _T("ProductName")
#define INI_PRODUCTVERSION			 _T("ProductVersion")
#define INI_RESULT					 _T("Result")
#define INI_STATUS					 _T("Status")
#define INI_KEY_REMOVEREGISTRY	     _T("KEY_RemoveRegistry")
//end section
#define INI_CONFIGUREA			"uninstall.ini"
#define INI_CONFIGUREW			L"uninstall.ini"

#define SELDEL_BAT_NAME			"askillme.bat"
#define SELDEL_BAT_NAME_T			_T("askillme.bat")

#ifdef UNICODE
#define INI_CONFIGURE			INI_CONFIGUREW
#else
#define INI_CONFIGURE			INI_CONFIGUREA
#endif

#define REBOOT_FLAG_FILE_UNINSTALL _T("as_reboot_d2d.ini")

#define PRODUCT_SPLITER	_T(",")
#define PRODUCT_SPLITER_OR	_T("|")
#define FILE_ACCESS_RETRY_COUNT_UNINSTALL	600
#define MIN_DISK_SIZE 50  //(MB) required min system disk space for uninstallation

//////////////////////////////////////////////////////////////////////////
//Command line parameters:

//Remove all installed product
#define FLAG_REMOVEALL			_T("All")

//ProductCode
#define FLAG_PRODUCTCODE		_T("P")

//Silent module
#define FLAG_SILENT				_T("q")

//rollback flag from silentinstall.exe
#define FLAG_R					_T("r")

//log path, if exit, uninstall will write the log to this folder
#define FLAG_L					_T("l")

//Help
#define FLAG_HELP				_T("?")

//Skip BlockCheck
#define FLAG_SKIP_BLOCK_CHECK	_T("SBC")

//MSIUI module
#define FLAG_MSIUI				_T("MSIUI")

//show original UI, user can seletct every components to uninstall
#define FLAG_FULLUI				_T("FULLUI")

//show simple UI, user cannot seletct every components to uninstall, setup will remove all installed components
#define FLAG_SIMPLEUI			_T("SUI")

//The following flags are used only by uninstall.exe itself, not for user.

//#define FLAG_DEPENDENCEBLOCK	_T("d")
//////////////////////////////////////////////////////////////////////////

#define REGKEY_MSI_INSTALLER_RUNNING			_T("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Installer\\InProgress")
#define REGKEY_ARCSERVEBACKUP_BASE		_T("SOFTWARE\\ComputerAssociates\\CA ARCserve Backup\\Base")
#define ETPKI_UNINSTALL_EXE             _T("uninstaller.exe")

//The flag that UA is using D2D
#define MUTEX_UA_SETUP		_T("Global\\CA_ARC_BKP_UA_SETUP_20B441EB-8798-468d-A69C-C14C15730F52")

/////////////////////////////////////////////////////////////////////////


BOOL GetProgramFilesPath(LPTSTR lpszProgFilesDir, DWORD& dwSize, BOOL bGet32On64OS)
{
	HKEY hKey = NULL;
	BOOL bIs64BitOS = theApp.Is64BitOS();

	BOOL bRet = FALSE;
	HRESULT hr = ERROR_SUCCESS;

	if (bIs64BitOS)
		hr = ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, REGKEY_MICROSOFT_WINDOWS, 0, KEY_READ|KEY_WOW64_64KEY, &hKey);
	else
		hr = ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, REGKEY_MICROSOFT_WINDOWS, 0, KEY_READ, &hKey);

	if (ERROR_SUCCESS != hr)
		return FALSE;

	if (bIs64BitOS && bGet32On64OS)
		hr = ::RegQueryValueEx(hKey, REGVALUE_PROGRAM_FILES_DIR_X86, NULL, NULL, (LPBYTE)lpszProgFilesDir, &dwSize);
	else
		hr = ::RegQueryValueEx(hKey, REGVALUE_PROGRAM_FILES_DIR, NULL, NULL, (LPBYTE)lpszProgFilesDir, &dwSize);

	::RegCloseKey(hKey);

	return ERROR_SUCCESS == hr;
}

//////////////////////////////////////////////////////////////////////////
void GetFullComLine(CUninstallConfigure &obj, CString &strCmdLine)
{
	strCmdLine.Format(_T("%s %s"), obj.m_strExecutable, obj.m_strCommandLine);

	LPCTSTR lpctProdCode = _T("[ProductCode]");
	strCmdLine.Replace(lpctProdCode, obj.m_strProductCode);

	obj.m_strProductLogFile.Replace(_T("<LOGPATH>"), theApp.m_strLogPathWithTime);

	strCmdLine.Replace(_T("<LOGPATH>"), theApp.m_strLogPathWithTime);

	if(theApp.IsRollBack())
	{
		//rollback, need append the rollback string for log file, like Uninstall-Rollback.log
		strCmdLine.Replace(_T("<ROLLBACK>"), FILE_ROLLBACK);
		obj.m_strProductLogFile.Replace(_T("<ROLLBACK>"), FILE_ROLLBACK);
	}
	else
	{
		//no rollback, no need append the rollback string for log file, like Uninstall.log
		strCmdLine.Replace(_T("<ROLLBACK>"), _T(""));
		obj.m_strProductLogFile.Replace(_T("<ROLLBACK>"), _T(""));
	}

	strCmdLine.Replace(_T("<LOGFILE>"), obj.m_strProductLogFile);

	TCHAR szTemp[MAX_PATH] = {0};
	::GetTempPath(_countof(szTemp), szTemp);
	LPCTSTR lpctTempFolderMacro = _T("%temp%");
	strCmdLine.Replace(lpctTempFolderMacro, szTemp);
}

void GetBeforeUnisntallCommandLine(CUninstallConfigure &obj, CString &strCmdLine)
{
	strCmdLine = obj.m_strBeforeUnisntall;
	strCmdLine.Trim();

	if (!strCmdLine.IsEmpty())
	{
		strCmdLine.Replace(_T("<INSTALLDIR>"), obj.m_strInstallPath);
		strCmdLine.Replace(_T("<WORKINGDIR>"), theApp.GetWorkingDir());
	}
}

void GetAfterUninstallCommandLine(CUninstallConfigure &obj, CString &strCmdLine)
{
	strCmdLine = obj.m_strAfterUnisntall;
	strCmdLine.Trim();

	if (!strCmdLine.IsEmpty())
	{
		strCmdLine.Replace(_T("<INSTALLDIR>"), obj.m_strInstallPath);
		strCmdLine.Replace(_T("<WORKINGDIR>"), theApp.GetWorkingDir());
	}
}

BOOL SetRebootFlagFile(const CString &stName)
{
	//keep the defaul value 0 for D2D msi.
	static int nIndex=1;


	TCHAR szFile[MAX_PATH];
	::GetSystemWindowsDirectory(szFile, MAX_PATH);
	::PathAppend(szFile, _T("Temp"));
	::PathAppend(szFile, REBOOT_FLAG_FILE_UNINSTALL);

	theApp.WriteLog(_T("SetRebootFlagFile %s."),_T("Start"));

	if (_taccess(szFile, 0) != 0)
	{
		HANDLE hFile = ::CreateFile(szFile, GENERIC_ALL, 0, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
		if (hFile != INVALID_HANDLE_VALUE)
		{
			theApp.WriteLog(_T("Create the file(%s) successfully."), szFile);
			::CloseHandle(hFile);
		}
		else
		{
			theApp.WriteLog(_T("Fail to create the file(%s)."), szFile);
		}
	}
	else
	{
		theApp.WriteLog(_T("This file(%s) exist. No need to create it."), szFile);
	}

	//write the reboot message into file
	if (_taccess(szFile, 0) != -1)
	{
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
		TCHAR szData[MAX_PATH*2];
		TCHAR szRebootTime[60];
		TCHAR szReason[60];

		ZeroMemory(szData,sizeof(szData));
		ZeroMemory(szRebootTime,sizeof(szRebootTime));
		ZeroMemory(szReason,sizeof(szReason));

		_stprintf_s(szData,_countof(szData),_T("To complete uninstalling %s, Windows Installer requires a system restart."),stName);
		_stprintf_s(szRebootTime,_countof(szRebootTime),_T("RebootTime%d"),nIndex);
		_stprintf_s(szReason,_countof(szReason),_T("RebootReason%d"),nIndex);
		nIndex++;
		theApp.SetupWritePrivateProfileString(_T("Uninstall"),szRebootTime, szTime, szFile);
		theApp.SetupWritePrivateProfileString(_T("Uninstall"),szReason, szData, szFile);

	}
	else
	{
		theApp.WriteLog(_T("Cannot access the file(%s)."),szFile);
		return FALSE;
	}


	BOOL bRet = ::MoveFileEx(szFile, NULL, MOVEFILE_DELAY_UNTIL_REBOOT);

	if(bRet)
	{
		theApp.WriteLog(_T("Call MoveFileEx with MOVEFILE_DELAY_UNTIL_REBOOT successfully."));
	}
	else
	{
		theApp.WriteLog(_T("Fail to call MoveFileEx with MOVEFILE_DELAY_UNTIL_REBOOT. ErrorCode:%d"), ::GetLastError());
	}

	theApp.WriteLog(_T("SetRebootFlagFile %s."),_T("End"));

	return bRet;
}

DWORD CallCommandLine(const CString strCommandLine,DWORD dwMilliseconds)
{
	if(strCommandLine.IsEmpty())
	{
		return ERROR_SUCCESS;
	}

	CString strWorkDir = theApp.GetWorkingDir();
	CString strTempCommandLine = strCommandLine;
	CString strFile = strCommandLine;
	//get the exe file by removing the parameter
	int k = strFile.Find(_T(".exe"));
	int nLength = k + 4;

	if (nLength < strFile.GetLength())
	{
		strFile = strFile.Left(nLength);
	}

	//remove " char
	strFile.Replace(_T("\""),_T(""));

	DWORD dwRet = 0;
	DWORD dwExitCode = 0;
	TCHAR szFilePath[MAX_PATH];
	memset(szFilePath,0,sizeof(szFilePath));

	if(!GetFilePath(strFile,szFilePath,_countof(szFilePath)))
	{
		theApp.WriteLog(_T("CallCommandLine: cannot get the file path of (%s)."),strFile);
	}
	else
	{
		strWorkDir = szFilePath;
	}

	dwRet = theApp.LaunchProcess(strTempCommandLine.GetBuffer(strTempCommandLine.GetLength()), strWorkDir, dwExitCode, dwMilliseconds, CREATE_NO_WINDOW);
	strTempCommandLine.ReleaseBuffer();

	theApp.WriteLog(_T("Call the command(%s) return [%d]"), strTempCommandLine,dwExitCode);
	
	if(dwRet == 0)
	{
		return dwExitCode;
	}

	return dwRet;
}

BOOL SaveNodeID()
{
	BOOL bRet = FALSE;
	REGSAM samDesired = KEY_READ;

	if (Is64BitSystem())
		samDesired |= KEY_WOW64_64KEY;

	HKEY hKey = NULL;
	LPCTSTR lpSubKey = _T("SOFTWARE\\Arcserve\\Unified Data Protection\\Engine");
	LSTATUS nRet = ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, lpSubKey, 0, samDesired, &hKey);

	if (ERROR_SUCCESS == nRet)
	{
		const int nSize = MAX_PATH;
		TCHAR* lpData = new TCHAR[nSize];
		DWORD dwSize = sizeof(TCHAR) * nSize;
		DWORD dwType = REG_SZ;
		memset(lpData, 0, dwSize);

		LPCTSTR lpValueName = _T("NodeID");
		nRet = ::RegQueryValueEx(hKey, lpValueName, NULL, &dwType, (LPBYTE)lpData, &dwSize);
		if (ERROR_SUCCESS == nRet && _tcslen(lpData))
		{
			TCHAR szFile[MAX_PATH];
			::GetSystemWindowsDirectory(szFile, _countof(szFile));
			::PathAppend(szFile, _T("Temp\\Arcserve\\Setup\\UDP\\Uninstall\\Settings.ini"));
			::WritePrivateProfileString(_T("Agent"), lpValueName, lpData, szFile);
		}
		else
		{
			theApp.WriteLog(_T("Cannot query the registry value %s. Error: %d"), lpValueName, nRet);
		}

		delete[]lpData;

		::RegCloseKey(hKey);
	}
	else
	{
		theApp.WriteLog(_T("Cannot open the registry key %s. Error: %d"), lpSubKey, nRet);
	}

	return bRet;
}

BOOL SaveSettings(const CUninstallConfigure& objItem)
{
	BOOL bRet = TRUE;

	if (objItem.m_strShortName.CompareNoCase(_T("D2DX86")) == 0 || objItem.m_strShortName.CompareNoCase(_T("D2DX64")) == 0)
	{
		bRet = SaveNodeID();
	}

	return bRet;
}

BOOL UninstallOneProduct(INT_PTR nIndex, HWND hwnd)
{
	theApp.WriteLog(_T("UninstallOneProduct Begin (index:%d)."),nIndex);
	if(theApp.m_aryProducts.GetCount() <= 0)
	{
		theApp.WriteLog(_T("There is no product list in the array. No need to handle it."));
		theApp.WriteLog(_T("UninstallOneProduct End."));
		return TRUE;
	}

	if (theApp.m_aryProducts.GetAt(nIndex).m_nStatus == M_STATUS_COMPLETED)
	{
		theApp.WriteLog(_T("This product(%s) has already been removed. No need handle it."),theApp.m_aryProducts.GetAt(nIndex).m_strProductName);
		theApp.WriteLog(_T("UninstallOneProduct End."));
		return TRUE;
	}

	DWORD dwRet, dwExitCode;

	if (hwnd)
	{
		theApp.WriteLog(_T("Post the start message."));
		::PostMessage(hwnd, WM_CHANGEITEMSTATUS, nIndex, M_STATUS_WORKING);
	}
	else
	{
		theApp.WriteLog(_T("No need post the working message for silent mode."));
	}

	SaveSettings(theApp.m_aryProducts.GetAt(nIndex));

	CString strCmdLine(_T(""));

	GetFullComLine(theApp.m_aryProducts.GetAt(nIndex), strCmdLine);

	//before un-installation
	if (!theApp.m_aryProducts.GetAt(nIndex).m_strBeforeUnisntall.IsEmpty())
	{
		CString strBeforeCommandLine;
		GetBeforeUnisntallCommandLine(theApp.m_aryProducts.GetAt(nIndex), strBeforeCommandLine);

		DWORD dwTimeOut = 600000;
		theApp.WriteLog(_T("prepare to call the command line(%s) with timeout(%ds)."),strBeforeCommandLine,dwTimeOut/1000);
		CallCommandLine(strBeforeCommandLine,dwTimeOut);
	}
	else
	{
		theApp.WriteLog(_T("There is no beforeuninstall command line."));
	}

	//stop services before un-installation
	CString strService;
	for(int i=0; i < theApp.m_aryProducts.GetAt(nIndex).m_strStopServices.GetCount(); i++)
	{
		strService = theApp.m_aryProducts.GetAt(nIndex).m_strStopServices[i];
		if (IsServiceStarted(strService))
		{
			DWORD dwServiceRet = StopSpecService(strService,TRUE,TRUE);
			if(dwServiceRet == ERROR_SUCCESS)
			{
				theApp.WriteLog(_T("Stop service(%s) succesfully."), strService);
			}
			else
			{
				theApp.WriteLog(_T("Fail to stop service(%s). Error:%d"), strService,dwServiceRet);
			}
		}
		else
		{
			theApp.WriteLog(_T("No need to stop service(%s). It is not running."),strService);
		}
	}

	//end stop
	//end before un-installation

	int n = 0;
	int count = 120;
	while (theApp.IsMSIInstallerRunning() && n < count)
	{
		Sleep(5000);
		n++;
		theApp.WriteLog(_T("There is another windows installer which is runing(count:%d), wait(%d)."),count,n);
		continue;
	}

	if(!theApp.IsMSIInstallerRunning())
	{
		theApp.WriteLog(_T("There isn't another windows installer running."));
	}

	theApp.WriteLog(strCmdLine);
	dwRet = theApp.LaunchProcess(strCmdLine.GetBuffer(strCmdLine.GetLength()), theApp.GetWorkingDir(), dwExitCode, INFINITE, CREATE_NO_WINDOW);
	strCmdLine.ReleaseBuffer();

	CString strRet;
	if(dwRet == 0)
	{
		theApp.m_aryProducts.GetAt(nIndex).m_dwExitCode = dwExitCode;
		strRet.Format(_T("%d"),dwExitCode);
		theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_PRODUCTNAME, theApp.m_aryProducts.GetAt(nIndex).m_strProductName, theApp.m_strUinstallStatusFile);
		theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_PRODUCTCODE, theApp.m_aryProducts.GetAt(nIndex).m_strProductCode, theApp.m_strUinstallStatusFile);
		theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_PRODUCTVERSION, theApp.m_aryProducts.GetAt(nIndex).m_strProductVersion, theApp.m_strUinstallStatusFile);
		
		if(dwExitCode == ERROR_SUCCESS)
		{
			theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_RESULT, _T("Uninstall Success"), theApp.m_strUinstallStatusFile);
		}
		else if(dwExitCode == ERROR_SUCCESS_REBOOT_REQUIRED)
		{
			theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_RESULT, _T("Uninstall Success And A Reboot Required"), theApp.m_strUinstallStatusFile);
		}
		else
		{
			theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_RESULT, _T("Uninstall Failed"), theApp.m_strUinstallStatusFile);
		}
		
		theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_STATUS, strRet, theApp.m_strUinstallStatusFile);
		theApp.WriteLog(_T("Uninstall [%s] to return [%d]"), theApp.m_aryProducts.GetAt(nIndex).m_strProductName, dwExitCode);
		if(dwExitCode == ERROR_SUCCESS)
		{
			theApp.WriteLog(_T("Uninstall [%s] successfully."), theApp.m_aryProducts.GetAt(nIndex).m_strProductName);
		}
		else if(dwExitCode == ERROR_SUCCESS_REBOOT_REQUIRED || dwExitCode == ERROR_SUCCESS_REBOOT_INITIATED)
		{
			theApp.WriteLog(_T("Uninstall [%s] successfully and a reboot required; Return Code [%d]; Detailed log: %s."), theApp.m_aryProducts.GetAt(nIndex).m_strProductName,dwExitCode,theApp.m_aryProducts.GetAt(nIndex).m_strProductLogFile);
		}
		else
		{
			theApp.WriteLog(_T("Fail to uninstall [%s]; Error Code [%d]; Detailed log: %s."), theApp.m_aryProducts.GetAt(nIndex).m_strProductName,dwExitCode,theApp.m_aryProducts.GetAt(nIndex).m_strProductLogFile);
		}
	}
	else
	{
		theApp.m_aryProducts.GetAt(nIndex).m_dwExitCode = dwRet;

		strRet.Format(_T("%d"),dwRet);
		theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_PRODUCTNAME, theApp.m_aryProducts.GetAt(nIndex).m_strProductName, theApp.m_strUinstallStatusFile);
		theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_PRODUCTCODE, theApp.m_aryProducts.GetAt(nIndex).m_strProductCode, theApp.m_strUinstallStatusFile);
		theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_PRODUCTVERSION, theApp.m_aryProducts.GetAt(nIndex).m_strProductVersion, theApp.m_strUinstallStatusFile);
		theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_RESULT, _T("Uninstall Failed"), theApp.m_strUinstallStatusFile);
		theApp.SetupWritePrivateProfileString(theApp.m_aryProducts.GetAt(nIndex).m_strComponentName, INI_STATUS, strRet, theApp.m_strUinstallStatusFile);
		theApp.WriteLog(_T("Fail to call command line to uninstall %s, Error:%d"), theApp.m_aryProducts.GetAt(nIndex).m_strProductName, dwRet);
	}

	
	if (theApp.m_aryProducts[nIndex].IsUninstallNeedReboot(dwExitCode))
	{
		theApp.m_aryProducts.GetAt(nIndex).m_blNeedReboot = TRUE;
		theApp.SetReboot(TRUE);
		SetRebootFlagFile(theApp.m_aryProducts.GetAt(nIndex).m_strProductName);
	}

	if (dwRet == 0 && theApp.m_aryProducts[nIndex].IsUninstallSuccess(dwExitCode))
	{
		theApp.m_aryProducts.GetAt(nIndex).m_nStatus = M_STATUS_COMPLETED;

		//Launch afteruninstall command line
		if (!theApp.m_aryProducts.GetAt(nIndex).m_strAfterUnisntall.IsEmpty())
		{
			Sleep(100);

			GetAfterUninstallCommandLine(theApp.m_aryProducts.GetAt(nIndex), strCmdLine);

			if (!strCmdLine.IsEmpty())
			{
				dwRet = theApp.LaunchProcess(strCmdLine.GetBuffer(strCmdLine.GetLength()), theApp.GetWorkingDir(), dwExitCode, INFINITE, DETACHED_PROCESS|CREATE_NO_WINDOW);
				strCmdLine.ReleaseBuffer();

				if (dwExitCode == ERROR_SUCCESS_REBOOT_REQUIRED)
				{
					theApp.SetReboot(TRUE);
				}

				theApp.WriteLog(_T("AfterUninstall: Call the command(%s) return [%d]"), strCmdLine,dwExitCode);
			}
		}

		//theApp.ChangeDisplaySize(-theApp.m_aryProducts.GetAt(nIndex).m_nSize);

		if (hwnd)
		{
			theApp.WriteLog(_T("Post the complete message."));
			::PostMessage(hwnd, WM_CHANGEITEMSTATUS, nIndex, M_STATUS_COMPLETED);
		}
		else
		{
			theApp.WriteLog(_T("No need post the complete message for silent mode."));
		}

		//Check the installed status again
		INSTALLSTATE isRet = MsiQueryProductState(theApp.m_aryProducts.GetAt(nIndex).m_strProductCode);
		if (INSTALLSTATE_DEFAULT == isRet)
		{
			theApp.m_aryProducts.GetAt(nIndex).m_nStatus = M_STATUS_PENDING;
		}

		theApp.WriteLog(_T("UninstallOneProduct End."));

		return TRUE;
	}
	else
	{
		theApp.m_aryProducts.GetAt(nIndex).m_nStatus = M_STATUS_FAILED;

		if(dwRet ==0)
		{
			theApp.SetErrorCode(dwExitCode);
		}
		else
		{
			theApp.SetErrorCode(dwRet);
		}

		if (hwnd)
			::PostMessage(hwnd, WM_CHANGEITEMSTATUS, nIndex, M_STATUS_FAILED);

		theApp.WriteLog(_T("UninstallOneProduct End."));
		return FALSE;
	}
}

void RestartServices()
{
	CStringArray aryServices;
	INT_PTR nSize = theApp.m_aryProducts.GetSize();

	for (INT_PTR i=0; i<nSize; i++)
	{
		if (theApp.m_aryProducts[i].m_blSelected)
		{
			for (INT_PTR j=0; j<theApp.m_aryProducts[i].m_strRestartServices.GetSize(); j++)
			{
				BOOL bAdded = FALSE;
				for (INT_PTR k=0; k<aryServices.GetSize(); k++)
				{
					if (aryServices[k].CompareNoCase(theApp.m_aryProducts[i].m_strRestartServices[j]) == 0)
					{
						bAdded = TRUE;
						break;
					}
				}

				if (!bAdded)
				{
					aryServices.Add(theApp.m_aryProducts[i].m_strRestartServices[j]);
				}
			}
		}
	}

	nSize = aryServices.GetSize();
	for (INT_PTR m=0; m<nSize; m++)
	{
		theApp.RestartService(aryServices[m]);
	}
}

void BeforeUninstall()
{
	theApp.m_aryNeedRestartSpecService.RemoveAll();

	//before un-installation, stop UA if need.
	if(theApp.IsNeedHandleUAService())
	{
		if(IsServiceStarted(SERVICE_CASUNIVERSALAGENT))
		{
			SERVICEITEM si;
			si.bNeedRestartStart = TRUE;
			si.strServiceName = SERVICE_CASUNIVERSALAGENT;
			theApp.m_aryNeedRestartSpecService.Add(si);
			theApp.WriteLog(_T("BeforeUninstall: prepare to stop service(%s)."), SERVICE_CASUNIVERSALAGENT);
			DWORD dwServiceRet = StopSpecService(SERVICE_CASUNIVERSALAGENT,TRUE,TRUE);

			if(dwServiceRet == ERROR_SUCCESS)
			{
				theApp.WriteLog(_T("BeforeUninstall: Stop service(%s) succesfully."), SERVICE_CASUNIVERSALAGENT);
			}
			else
			{
				theApp.WriteLog(_T("BeforeUninstall: Fail to stop service(%s). Error:%d"), SERVICE_CASUNIVERSALAGENT,dwServiceRet);
			}
		}
		else
		{
			theApp.WriteLog(_T("BeforeUninstall: The service (%s) isn't running, no need handle it."), SERVICE_CASUNIVERSALAGENT);
		}
	}
	
	//check the service which need to be stopped and add them to restart list
	for(int i=0; i<theApp.m_strRestartSpecServices.GetCount(); i++)
	{
		CString strService = theApp.m_strRestartSpecServices[i];
		if(IsServiceStarted(strService))
		{
			SERVICEITEM si;
			si.bNeedRestartStart = TRUE;
			si.strServiceName = strService;
			theApp.m_aryNeedRestartSpecService.Add(si);
			DWORD dwServiceRet = StopSpecService(strService,TRUE,TRUE);
			if(dwServiceRet == ERROR_SUCCESS)
			{
				theApp.WriteLog(_T("BeforeUninstall: Stop service(%s) succesfully."), strService);
			}
			else
			{
				theApp.WriteLog(_T("BeforeUninstall: Fail to stop service(%). Error:%d"), strService,dwServiceRet);
			}
		}
		else
		{
			theApp.WriteLog(_T("BeforeUninstall: service (%s) isn't running, no need handle it."), strService);
		}
	}


}

void AfterUninstall()
{
	DWORD dwServiceRet = 0;
	//start the service which need to be started
	for(int i=0; i<theApp.m_aryNeedRestartSpecService.GetCount();i++)
	{
		CString strService = theApp.m_aryNeedRestartSpecService[i].strServiceName;
		if(theApp.m_aryNeedRestartSpecService[i].bNeedRestartStart)
		{
			//first stop it if it is still running
			if(IsServiceStarted(strService))
			{
				theApp.WriteLog(_T("AfterUninstall: stop service %s first."), strService);
				dwServiceRet = StopSpecService(strService,TRUE,TRUE);

				if(dwServiceRet == ERROR_SUCCESS)
				{
					theApp.WriteLog(_T("AfterUninstall: Stop service(%s) succesfully."), strService);
				}
				else
				{
					theApp.WriteLog(_T("AfterUninstall: Fail to stop service(%s). Error:%d"), strService,dwServiceRet);
				}
			}

			//start the service
			if(StartSpecService(strService,TRUE))
			{
				theApp.WriteLog(_T("AfterUninstall: start service (%s) successfully."), strService);
			}
			else
			{
				theApp.WriteLog(_T("AfterUninstall: fail to start service (%s)."), strService);
			}
		}
	}
}

DWORD WINAPI UninstallThreadFunc(LPVOID pParam)
{
	HWND hwnd = (HWND)pParam;

	theApp.WriteLog(_T("UninstallThreadFunc Begin."));

	if(theApp.m_aryProducts.GetCount() <=0)
	{
		theApp.WriteLog(_T("UninstallThreadFunc: There is no product list in the array. No need to handle it."));
		return ERROR_SUCCESS;
	}

	INT_PTR nSize = theApp.m_aryProducts.GetSize();
	int nIndex;

	//before un-installation, stop the service etc.
	BeforeUninstall();

	BOOL bRet;
	for (INT_PTR i=0; i<nSize; i++)
	{
		if (theApp.m_aryProducts.GetAt(i).m_blSelected)
		{
			if (theApp.m_aryProducts.GetAt(i).m_nStatus != M_STATUS_COMPLETED || theApp.m_aryProducts.GetAt(i).m_nStatus != M_STATUS_WORKING)
			{
				//Uninstall preuninstall components
				for (INT_PTR k=0; k<theApp.m_aryProducts[i].m_strPreUninstallComponents.GetSize(); k++)
				{
					nIndex = theApp.GetIndexByShortName(theApp.m_aryProducts[i].m_strPreUninstallComponents[k]);
					if (nIndex >= 0)
					{
						theApp.WriteLog(_T("PreUninstall %s..."), theApp.m_aryProducts[i].m_strPreUninstallComponents[k]);

						UninstallOneProduct(nIndex, hwnd);

						Sleep(100);
					}
				}

				//Uninstall current product
				bRet = UninstallOneProduct(i, hwnd);

				Sleep(100);

				if (bRet)
				{
					//Uninstall share components
					for (INT_PTR j=0; j<theApp.m_aryProducts[i].m_strSharedComponents.GetSize(); j++)
					{
						if (theApp.IsAllowUninstallSharedComponent(theApp.m_aryProducts[i].m_strSharedComponents[j]))
						{
							nIndex = theApp.GetIndexByShortName(theApp.m_aryProducts[i].m_strSharedComponents[j]);
							if (nIndex >= 0)
							{
								theApp.WriteLog(_T("AfterUninstall: %s is not used, unisntall it"), theApp.m_aryProducts[i].m_strSharedComponents[j]);
								UninstallOneProduct(nIndex, hwnd);

								Sleep(100);
							}
						}
					}
				}
			}
		}
	}

	theApp.WriteLog(_T("Call AfterUninstall."));
	//after un-installation, start the service etc.
	AfterUninstall();

	theApp.WriteLog(_T("Call RestartServices."));
	//Restart Service.
	RestartServices();
	
	theApp.WriteLog(_T("Call ExecPostUninstall."));
	//run postuninstall
	theApp.ExecPostUninstall();

	if (hwnd)
		::PostMessage(hwnd, WM_INSTALLFINISH, 0, 0);

	theApp.WriteLog(_T("UninstallThreadFunc End."));

	return 0;
}

//////////////////////////////////////////////////////////////////////////
CUninstallConfigure::CUninstallConfigure()
{
	m_strShortName = _T("");
	m_strProductCode = _T("");
	m_strComponentName = _T("");
	m_strProductName = _T("");
	m_strInstallPath = _T("");
	m_strExecutable = _T("");
	m_strCommandLine = _T("");
	m_strProductLogFile = _T("");
	m_strBeforeUnisntall = _T("");
	m_strAfterUnisntall = _T("");
	m_strVersionCheck = _T("");
	m_dwComponentType = CAPRODUCT;
	m_blSelected = FALSE;
	m_blNeedReboot = FALSE;
	m_nStatus = M_STATUS_PENDING;
	m_nEstInstallTime = 120;
	m_nSize = 0;
	m_nUAServiceHandle = 0;
	m_dwExitCode = 0;
	
}


CUninstallConfigure::CUninstallConfigure(const CUninstallConfigure& other)
{
	*this = other;
}

CUninstallConfigure::~CUninstallConfigure()
{
}

BOOL CUninstallConfigure::IsUninstallSuccess(DWORD dwValue)
{
	for (INT_PTR i=0; i<m_arySuccessValues.GetSize(); i++)
	{
		if (m_arySuccessValues.GetAt(i) == dwValue)
		{
			return TRUE;
		}
	}

	return FALSE;
}

BOOL CUninstallConfigure::IsUninstallNeedReboot(DWORD dwValue)
{
	for (INT_PTR i=0; i<m_aryRebootValues.GetSize(); i++)
	{
		if (m_aryRebootValues.GetAt(i) == dwValue)
		{
			return TRUE;
		}
	}

	return FALSE;
}

const CUninstallConfigure& CUninstallConfigure::operator = (const CUninstallConfigure& other)
{
	if (&other == this)
		return *this;

	m_strShortName = other.m_strShortName;
	m_strProductCode = other.m_strProductCode;
	m_strProductName = other.m_strProductName;
	m_strProductVersion = other.m_strProductVersion;
	m_strComponentName = other.m_strComponentName;
	m_strInstallPath = other.m_strInstallPath;

	m_strDependentFeatures.RemoveAll();
	m_strDependentFeatures.Append(other.m_strDependentFeatures);

	m_strSharedComponents.RemoveAll();
	m_strSharedComponents.Append(other.m_strSharedComponents);

	m_strPreUninstallComponents.RemoveAll();
	m_strPreUninstallComponents.Append(other.m_strPreUninstallComponents);

	m_strRestartServices.RemoveAll();
	m_strRestartServices.Append(other.m_strRestartServices);

	m_strStopServices.RemoveAll();
	m_strStopServices.Append(other.m_strStopServices);

	m_strExecutable = other.m_strExecutable;
	m_strCommandLine = other.m_strCommandLine;
	m_strProductLogFile = other.m_strProductLogFile;
	m_strBeforeUnisntall = other.m_strBeforeUnisntall;
	m_strAfterUnisntall = other.m_strAfterUnisntall;
	m_strVersionCheck = other.m_strVersionCheck;
	
	m_dwComponentType = other.m_dwComponentType;

	m_dwExitCode = other.m_dwExitCode;

	m_blSelected = other.m_blSelected;
	
	m_blNeedReboot = other.m_blNeedReboot;

	m_nUAServiceHandle = other.m_nUAServiceHandle;

	m_nStatus = other.m_nStatus;

	m_nEstInstallTime = other.m_nEstInstallTime;

	m_nSize = other.m_nSize;

	m_arySuccessValues.RemoveAll();
	m_arySuccessValues.Append(other.m_arySuccessValues);

	m_aryRebootValues.RemoveAll();
	m_aryRebootValues.Append(other.m_aryRebootValues);

	return *this;
}
//////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////
// CUninstallApp

BEGIN_MESSAGE_MAP(CUninstallApp, CWinApp)
	ON_COMMAND(ID_HELP, &CWinApp::OnHelp)
END_MESSAGE_MAP()


// CUninstallApp construction

CUninstallApp::CUninstallApp()
{
	m_dwErrorCode = ERROR_SUCCESS;
	m_dwReturnCode = ERROR_SUCCESS;
	m_hProcessEvent = NULL;
	m_blSilent = FALSE;
	m_bCanel = FALSE;
	m_blMsiUI = FALSE;
	m_blFullUI = TRUE;//2014-01-17 change it to new full UI
	m_blSimpleUI = FALSE;
	m_blRollback = FALSE;
	m_blProductCode = FALSE;
	m_bNeedHandleUAService =FALSE;
	SetRemoveAll(FALSE);
	m_strProductCode = _T("");
	m_strInputLogPath = _T("");
	m_strLogPathWithTime = _T("");
	m_bReboot = FALSE;
	m_blExePost = FALSE;
	m_bNeedRunPost = TRUE;
	memset(m_szBatFile, 0, sizeof(m_szBatFile));
	SetBlockCheck(TRUE);
	Set64BitOS(FALSE);
	SetOSPlatform(VER_PLATFORM_X86);
	m_strProgFilesPath32 = _T("");
	m_strProgFilesPath64 = _T("");
	m_bUseDefaultProductName = TRUE;
	m_bWriteHistory = TRUE;
}

CUninstallApp theApp;


// CUninstallApp initialization

BOOL CUninstallApp::InitInstance()
{
	// InitCommonControlsEx() is required on Windows XP if an application
	// manifest specifies use of ComCtl32.dll version 6 or later to enable
	// visual styles.  Otherwise, any window creation will fail.
	INITCOMMONCONTROLSEX InitCtrls;
	InitCtrls.dwSize = sizeof(InitCtrls);
	// Set this to include all the common control classes you want to use
	// in your application.
	InitCtrls.dwICC = ICC_WIN95_CLASSES;
	InitCommonControlsEx(&InitCtrls);

	CWinApp::InitInstance();

//	AfxEnableControlContainer();//For issue 17153693

	// Standard initialization
	// If you are not using these features and wish to reduce the size
	// of your final executable, you should remove from the following
	// the specific initialization routines you do not need
	// Change the registry key under which our settings are stored
	// TODO: You should modify this string to be something appropriate
	// such as the name of your company or organization
	SetRegistryKey(_T("Local AppWizard-Generated Applications"));

	Set64BitOS(Is64BitSystem());

	SetOSPlatform(Get64BitOSType());
	
	//Get working path
	GetModuleFilePath(m_strWorkingDir.GetBuffer(MAX_PATH), MAX_PATH);
	m_strWorkingDir.ReleaseBuffer();	
	m_strWorkingDir.TrimRight(_T("\\"));

	InitLogFileName();

	WriteLog(_T("*************************  Start  *************************"));

	m_blHelp = FALSE;

	if(!ProcessCommandLine())
	{
		m_dwReturnCode = ERROR_INVALID_PARAMETER;
		m_bNeedRunPost = FALSE;
		WriteLog(_T("*************************  End  *************************"));
		return FALSE;
	}

	if(!m_strInputLogPath.IsEmpty())
	{
		BOOL bRes = (_taccess(m_strInputLogPath,0) != -1);

		if(!bRes)
		{
			//don't exist, create it
			bRes = MakeSurePathExists(m_strInputLogPath, FALSE);
		}

		if(bRes)
		{
			//writ the log to current log file.
			WriteLog(_T("All log will be written to the input path (%s)."),m_strInputLogPath);
			WriteLog(_T("*************************  End  *************************"));

			//write the log the new log file
			InitLogFileName(m_strInputLogPath);
			WriteLog(_T("*************************  Start  *************************"));

		}


	}
	
	m_strInifile.Format(_T("%s\\%s"), m_strWorkingDir, INI_CONFIGURE);

	if (_taccess(m_strInifile, 00) == -1)
	{
		m_bNeedRunPost = FALSE;
		m_dwReturnCode = ERROR_FILE_NOT_FOUND;
		WriteLog(_T("Error: %s does NOT exist. Setup cannot continue."), m_strInifile);
		m_bNeedRunPost = FALSE;
		return FALSE;
	}

	TCHAR szUserName[MAX_PATH];
	DWORD dwSize = _countof(szUserName);
	ZeroMemory(szUserName,sizeof(szUserName));

	::GetUserName(szUserName,&dwSize);
	WriteLog(_T("Get the current user: %s"), szUserName);
	WriteLog(_T("The ini File : %s"), m_strInifile);
	
	CString strResDll;

	strResDll.Format(_T("%s\\%s"), m_strWorkingDir, UNINSTALL_DLL);

	if (_taccess(strResDll, 00) == -1)
	{
		m_bNeedRunPost = FALSE;
		m_dwReturnCode = ERROR_FILE_NOT_FOUND;
		WriteLog(_T("Error: %s does NOT exist. Setup cannot continue."), strResDll);
		m_bNeedRunPost = FALSE;
		return FALSE;
	}

	WriteLog(_T("The Resource File : %s"), strResDll);

	WriteLog(_T("The detail msi log folder: %s"), m_strLogPathWithTime);

	afxCurrentResourceHandle = LoadLibrary(strResDll);

	if (!afxCurrentResourceHandle)
	{
		DWORD dwError = GetLastError();
		WriteLog(_T("GetLastError return : %d"), dwError);

		CString sMsg;
		sMsg.Format(_T("LoadLibrary %s Failed!"), strResDll);
		WriteLog(sMsg);

		m_bNeedRunPost = FALSE;
		m_dwReturnCode = dwError;
		m_bNeedRunPost = FALSE;
		return FALSE;
	}

	if (m_blHelp)
	{
		CString strTitle, strContent;

		m_bNeedRunPost = FALSE;

		strContent.LoadString(IDS_STRING_HELP);

		strTitle.LoadString(IDS_STRING_APPLICATION_TITLE);
		::MessageBox(NULL, strContent, strTitle, MB_OK);

		return FALSE;
	}

	if (IsBlockCheck())
	{
		if (ProcessesRunningBlock())
		{
			m_bNeedRunPost = FALSE;
			m_dwReturnCode = ERROR_SHARING_VIOLATION;
			return FALSE;
		}
	}

	//detect the running process
	if (IsOtherInstanceRunning())
	{
		m_bNeedRunPost = FALSE;
		m_dwReturnCode = ERROR_SHARING_VIOLATION;
		WriteLog(_T("The other instance is running now!"));
		m_bNeedRunPost = FALSE;
		
		if(!m_blSilent)
		{
			CString strTitle, strContent;

			m_bNeedRunPost = FALSE;

			strContent.LoadString(IDS_SECOND_INSTANCE);

			strTitle.LoadString(IDS_STRING_APPLICATION_TITLE);
			::MessageBox(NULL, strContent, strTitle, MB_OK);
		}

		return FALSE;
	}
	else
	{
		//if current process is the only instance, then create the Event flag
		CreateRunningFlag();
	}

	//check 

	//check the disk space
	WriteLog(_T("Check free system disk space."));
	if(!IsDiskSpaceOK())
	{
		m_bNeedRunPost = FALSE;
		return FALSE;
	}

	//get the flag for product name
	CString strValueCheck;
	TCHAR szValue[20] = {0}; 
	SetupGetPrivateProfileString(_T("uninstall"),
		_T("UseDefaultProductName"),
		_T("1"),
		szValue,
		_countof(szValue),
		m_strInifile);
	strValueCheck = szValue;
	strValueCheck.Trim();
	if(strValueCheck.CompareNoCase(_T("0")) == 0)
	{
		m_bUseDefaultProductName = FALSE;
		WriteLog(_T("Use the product name on Add/Remove panel."));
	}
	else
	{
		m_bUseDefaultProductName = TRUE;
		WriteLog(_T("Use the default product name."));
	}

	//get the program folder path
	TCHAR szTemp[MAX_PATH] = {0};
	GetProgramFilesPath(szTemp, dwSize, TRUE);
	m_strProgFilesPath32 = szTemp;

	ZeroMemory(szTemp,sizeof(szTemp));
	GetProgramFilesPath(szTemp, dwSize, FALSE);
	m_strProgFilesPath64 = szTemp;

	//remove all the products if the mode is no Full UI,MSI UI.
	if(!m_blFullUI && !m_blMsiUI || m_blSimpleUI)
	{
		WriteLog(_T("This mode is simple uninstallation. Setup will remove all installed components."));
		SetRemoveAll(TRUE);
	}
	else if(m_blFullUI)
	{
		WriteLog(_T("This mode is Full UI."));
	}
	else if(m_blMsiUI)
	{
		WriteLog(_T("This mode is MSI UI."));
	}
	
	//permit to uninstall one product when /q and /p parmaters are inputted on silent mode
	if(m_blSilent && m_blProductCode)
	{
		WriteLog(_T("This mode is changed to the input product code uninstallation mode with /p paramter. Setup will only remove this component."));
		SetRemoveAll(FALSE);
	}

	if (!GetInstalledProducts())
	{
		WriteLog(_T("There is no any product installed to be found."));
		return FALSE;
	}

	InitUDPRootPath();

	//get the restartspec service list
	GetRestartSpecServices();

	//If there is no Products installed, quit and uninstall myself in ExitInstance
	BOOL bProductInstalled = FALSE;
	INT_PTR nSize = m_aryProducts.GetSize();
	for (int i=0; i<nSize; i++)
	{
		if (CAPRODUCT == m_aryProducts.GetAt(i).m_dwComponentType)
		{
			bProductInstalled = TRUE;
			break;
		}
	}

	if (!bProductInstalled)
	{
		WriteLog(_T("No product is installed. Setup will remove the share components"));
		//Uninstall the share components if no AS products are installed
		for (int i=0; i<nSize; i++)
		{
			UninstallOneProduct(i, NULL);
		}

		return FALSE;
	}

	if (m_blSilent)
	{
		WriteLog(_T("Silent uninstall......"));

		SilentUninstallProducts();
	}
	else if (m_blFullUI)
	{
		WriteLog(_T("GUI uninstall......"));
		VERIFY(m_bmpWatermark.LoadBitmap(IDB_BLANK));
		VERIFY(m_bmpHeader.LoadBitmap(IDB_WIZ97_BANNER256));

		CSetupSheet dlg(IDS_STRING_APPLICATION_TITLE, NULL, 0, m_bmpWatermark, NULL, m_bmpHeader);
		dlg.m_psh.hInstance = ::GetModuleHandle(NULL);

		//m_pMainWnd = &dlg;//http://support.microsoft.com/kb/253130
		
		INT_PTR nResponse = dlg.DoModal();

		if (nResponse == IDCANCEL)
		{
			WriteLog(_T("User click Cancel to quit setup"));
			m_bCanel = TRUE;
			m_bNeedRunPost = FALSE;
			return FALSE;
		}
	}
	else  //simple UI
	{
		WriteLog(_T("Simple UI uninstall......"));

		if(theApp.IsUseDefaultProductName())
		{
			m_strProductDisplayName.LoadString(IDS_STRING_DEFAULT_PRODUCTNAME);
			WriteLog(_T("Use the default name:%s"), m_strProductDisplayName);
		}
		else
		{
			//get product display name
			BOOL bRet=GetRegValue(HKEY_LOCAL_MACHINE,REG_UDP,_T("DisplayName"),m_strProductDisplayName,FALSE);
			if(bRet)
			{
				WriteLog(_T("Get the product display name: %s"), m_strProductDisplayName);
			}
			else
			{
				m_strProductDisplayName.LoadString(IDS_STRING_DEFAULT_PRODUCTNAME);
				WriteLog(_T("Fail to get the product display name, use the default name:%s"), m_strProductDisplayName);
			}
		}

		SimpleUIUninstallProducts();
	}

	if (IsNeedReboot())
	{
		WriteLog(_T("Setup has completed the removal which now requires a system reboot"));

		m_dwReturnCode = ERROR_SUCCESS_REBOOT_REQUIRED;
	}

	// Since the dialog has been closed, return FALSE so that we exit the
	//  application, rather than start the application's message pump.
	return FALSE;
}


void CUninstallApp::WriteLog(const TCHAR* pszFormat, ...)
{
	TCHAR szMessage[MAX_BUFSIZE];
	TCHAR szMessageEx[MAX_BUFSIZE];
	memset(szMessage, 0, sizeof(szMessage));
	memset(szMessageEx, 0, sizeof(szMessageEx));

	va_list arglist = NULL;
	va_start(arglist, pszFormat);
	_vstprintf_s(&szMessage[0], MAX_BUFSIZE, pszFormat, arglist);
	va_end(arglist);
	_tcscat_s(szMessage, MAX_BUFSIZE, _T("\r\n"));

	HANDLE hFile = CreateFile(m_strLogFile,
		GENERIC_WRITE,
		0,
		NULL,
		OPEN_ALWAYS,
		FILE_ATTRIBUTE_NORMAL,
		NULL);

	if (hFile == INVALID_HANDLE_VALUE)
		return;

	struct tm newTime;
	__time64_t long_time;

	// Get time as 64-bit integer.
	_time64(&long_time); 
	// Convert to local time.
	_localtime64_s(&newTime, &long_time);

	_sntprintf_s(szMessageEx, MAX_BUFSIZE, _T("%04d-%02d-%02d %02d:%02d:%02d | PID:%d |%s"),
		newTime.tm_year+1900, newTime.tm_mon+1, newTime.tm_mday, newTime.tm_hour, newTime.tm_min, newTime.tm_sec, ::GetCurrentProcessId(),szMessage);

	DWORD dwBytesToWrite = DWORD(_tcslen(szMessageEx) * sizeof(TCHAR));
	DWORD dwBytesWritten;
	DWORD dwFileOffset = SetFilePointer(hFile, 0, NULL, FILE_END);

	LockFile(hFile, dwFileOffset, 0, dwFileOffset + dwBytesToWrite, 0);
	WriteFile(hFile, szMessageEx, dwBytesToWrite, &dwBytesWritten, NULL);
	UnlockFile(hFile, dwFileOffset, 0, dwFileOffset + dwBytesToWrite, 0);

	CloseHandle(hFile);
}


BOOL CUninstallApp::ProcessCommandLine()
{
	WriteLog(_T("The commandline is: %s"), GetCommandLine());
	//Process command line
	TCHAR szProductCode[MAX_PATH];
	memset(szProductCode, 0, sizeof(szProductCode));

	if (1 == __argc)//Without parameter
	{
		return TRUE ;
	}

	for (int i=1; i<__argc; i++)
	{
		if (__targv[i][0] == _T('/') || __targv[i][0] == _T('-'))
		{
			//Get the FLAG_SILENT
			if (_tcsicmp(&__targv[i][1], FLAG_SILENT) == 0)
			{
				m_blSilent = TRUE;
				WriteLog(_T("FLAG_SILENT %s is set"), FLAG_SILENT);
				continue;
			}

			//Get the FLAG_HELP
			if (_tcsicmp(&__targv[i][1], FLAG_HELP) == 0)
			{
				m_blHelp = TRUE;
				WriteLog(_T("FLAG_HELP %s is set"), FLAG_HELP);
				continue;
			}

			//Get the FLAG_MSIUI
			if (_tcsicmp(&__targv[i][1], FLAG_MSIUI) == 0)
			{
				m_blMsiUI = TRUE;
				WriteLog(_T("FLAG_MSIUI %s is set"), FLAG_MSIUI);
				continue;
			}

			//Get the FLAG_FULLUI
			if (_tcsicmp(&__targv[i][1], FLAG_FULLUI) == 0)
			{
				m_blFullUI = TRUE;
				m_blSimpleUI = FALSE;
				WriteLog(_T("FLAG_FULLUI %s is set"), FLAG_FULLUI);
				continue;
			}
			
			//Get the FLAG_SIMPLEUI
			if (_tcsicmp(&__targv[i][1], FLAG_SIMPLEUI) == 0)
			{
				m_blSimpleUI = TRUE;
				m_blFullUI = FALSE; //set FULLUI flag false;
				WriteLog(_T("FLAG_SIMPLEUI %s is set"), FLAG_SIMPLEUI);
				continue;
			}
			
			//Get the FLAG_R
			if (_tcsicmp(&__targv[i][1], FLAG_R) == 0)
			{
				m_blRollback = TRUE;
				WriteLog(_T("FLAG_R %s is set"), FLAG_R);
				continue;
			}

			//Get the FLAG_L
			if (_tcsicmp(&__targv[i][1], FLAG_L) == 0)
			{
				i++;
				m_strInputLogPath = &__targv[i][0];
				m_strInputLogPath.Trim();
				WriteLog(_T("FLAG_L %s is set)"), FLAG_L);
				
				if(m_strInputLogPath.GetLength() < 4)
				{
					WriteLog(_T("The input log path (%s) is invalid. please make sure to input the valid path.)"), m_strInputLogPath);
					return FALSE;
				}
				else
				{
					WriteLog(_T("Input log path is (%s)"), m_strInputLogPath);
				}


				continue;
			}

			//Get the FLAG_SKIP_BLOCK_CHECK
			if (_tcsicmp(&__targv[i][1], FLAG_SKIP_BLOCK_CHECK) == 0)
			{
				SetBlockCheck(FALSE);

				WriteLog(_T("FLAG_SKIP_BLOCK_CHECK %s is set"), FLAG_SKIP_BLOCK_CHECK);
				continue;
			}

			////Get the FLAG_DEPENDENCEBLOCK
			//if (_tcsnicmp(&__targv[i][1], FLAG_DEPENDENCEBLOCK, _tcslen(FLAG_DEPENDENCEBLOCK)) == 0)
			//{
			//	m_blDependenceBlock = TRUE;
			//	WriteLog(_T("FLAG_DEPENDENCEBLOCK %s is set."), FLAG_DEPENDENCEBLOCK);
			//	continue;
			//}

			//Get the FLAG_REMOVEALL
			if (_tcsicmp(&__targv[i][1], FLAG_REMOVEALL) == 0)
			{
				SetRemoveAll(TRUE);
				WriteLog(_T("FLAG_REMOVEALL %s is set"), FLAG_REMOVEALL);
				continue;
			}

			//Get the FLAG_PRODUCTCODE
			if (_tcsnicmp(&__targv[i][1], FLAG_PRODUCTCODE, _tcslen(FLAG_PRODUCTCODE)) == 0)
			{
				WriteLog(_T("FLAG_PRODUCTCODE %s is set"), FLAG_PRODUCTCODE);
				m_blProductCode = TRUE;

				//get the next parameter (ProductCode)
				i++;
				m_strProductCode = &__targv[i][0];
				m_strProductCode.Trim();
				
				WriteLog(_T("ProductCode is %s"), m_strProductCode);

				if (m_strProductCode.IsEmpty())
				{
					WriteLog(_T("ProductCode is empty"));
					m_blHelp = TRUE;
				}

				continue;
			}

			//Other Flags, Uninstall.exe could not understand, show help
			m_blHelp = TRUE;
		}
	}

	//show the help
	if (!m_blHelp && !m_blProductCode && !m_blFullUI && !m_blSimpleUI && !IsRemoveAll() && !m_blSilent)
	{
		m_blHelp = TRUE;
	}

	return TRUE;
}


BOOL CUninstallApp::UninstallProducts(HWND hWnd, BOOL blWait)
{
	WriteLog(_T("Create Thread UninstallThreadFunc to uninstall products"));

	DWORD dwThreadId;
	HANDLE hThread = ::CreateThread(
		NULL,              // default security attributes
		0,                 // use default stack size  
		UninstallThreadFunc,        // thread function 
		hWnd,//&m_aryCUninstallConfigure,   // argument to thread function 
		0,                 // use default creation flags 
		&dwThreadId);   // returns the thread identifier 

	if(hThread == NULL)
	{
		WriteLog(_T("Fail to create Thread UninstallThreadFunc to uninstall products. Error:%d"),GetLastError());
		return FALSE;
	}

	BOOL blRet = (hThread != NULL);
	if (blRet && blWait)
	{
		WaitForSingleObject(hThread, INFINITE);
	}

	return blRet;
}


BOOL CUninstallApp::GetModuleFilePath(LPTSTR lptPath, DWORD dwSize)
{
	TCHAR szModule[MAX_PATH];
	TCHAR szDrive[_MAX_DRIVE];
	TCHAR szDir[_MAX_DIR];	

	memset(szModule, 0, sizeof(szModule));
	memset(szDrive, 0, sizeof(szDrive));
	memset(szDir, 0, sizeof(szDir));
	memset(lptPath, 0, sizeof(TCHAR)*dwSize);

	BOOL blRet = FALSE;
	if (GetModuleFileName(NULL, szModule, MAX_PATH))
	{
		_tsplitpath_s(szModule, szDrive, _MAX_DRIVE, szDir, _MAX_DIR, NULL, 0, NULL, 0);
		_stprintf_s(lptPath, dwSize, _T("%s%s"), szDrive, szDir);
		blRet = TRUE;
	}

	return blRet;
}


DWORD CUninstallApp::LaunchProcess(LPTSTR pCmdLine, LPCTSTR pWorkingDir, DWORD &dwExitCode, DWORD dwMilliseconds/*=INFINITE*/, DWORD dwCreationFlags/*=0*/)
{
	WriteLog(_T("LaunchProcess: command line(%s)"), pCmdLine);
	if (pWorkingDir)
	{
		WriteLog(_T("LaunchProcess: Working Dir(%s)"), pWorkingDir);
	}

	TCHAR szCmdLine[MAX_PATH*4];
	memset(szCmdLine,0,sizeof(szCmdLine));

	_stprintf_s(szCmdLine,_countof(szCmdLine),_T("%s"),pCmdLine);

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
		WriteLog(_T("LaunchProcess: Call CreateProcess() successfully."));
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


int CUninstallApp::ExitInstance()
{
	if (m_bNeedRunPost && !m_blExePost)
	{
		ExecPostUninstall();
	}

	BOOL bReboot = FALSE;

	if (IsNeedReboot())
	{
		m_dwReturnCode = ERROR_SUCCESS_REBOOT_REQUIRED;

		if (!m_blSilent)
		{
			CString strTitle, strMsg;

			strTitle.LoadString(IDS_STRING_APPLICATION_TITLE);

			strMsg.LoadString(IDS_MSG_REBOOT);

			int nBoxReturn = 0;
			if(m_blSimpleUI)
			{
				nBoxReturn = ::MessageBox(NULL, strMsg, strTitle, MB_YESNO|MB_ICONQUESTION|MB_SERVICE_NOTIFICATION);
			}
			else
			{
				nBoxReturn = ::MessageBox(NULL, strMsg, strTitle, MB_YESNO|MB_ICONQUESTION|MB_TOPMOST);
			}

			if (IDYES == nBoxReturn)
			{
				WriteLog(_T("ExitInstance: User clicked Yes to reboot OS"));
				bReboot = TRUE;
			}
			else
			{
				WriteLog(_T("ExitInstance: User clicked No to cancel the reboot"));
			}
		}
		else
		{
			WriteLog(_T("The reboot is required to complete the uninstall."));
		}
	}

	::FreeLibrary(afxCurrentResourceHandle);

	if(m_dwErrorCode != ERROR_SUCCESS)
	{
		m_dwReturnCode = m_dwErrorCode;
	}

	//remove the empty log folder
	if(_taccess(m_strLogPathWithTime,0) == 0)
	{
		if(RemoveDirectory(m_strLogPathWithTime))
		{
			WriteLog(_T("Remove the empty log folder(%s)."), m_strLogPathWithTime);
		}
	}

	if(m_dwReturnCode == ERROR_SUCCESS)
	{
		SetupWritePrivateProfileString(_T("Uninstall"), INI_RESULT, _T("Uninstall Success"), m_strUinstallStatusFile);
	}
	else if(m_dwReturnCode == ERROR_SUCCESS_REBOOT_REQUIRED)
	{
		SetupWritePrivateProfileString(_T("Uninstall"), INI_RESULT, _T("Uninstall Success And A Reboot Required"), m_strUinstallStatusFile);
	}
	else
	{
		SetupWritePrivateProfileString(_T("Uninstall"), INI_RESULT, _T("Uninstall Failed"), m_strUinstallStatusFile);
	}

	CString strRet;
	strRet.Format(_T("%d"),m_dwReturnCode);
	SetupWritePrivateProfileString(_T("Uninstall"), INI_STATUS, strRet, m_strUinstallStatusFile);

	UninstallSummary();

	if(m_dwReturnCode == ERROR_SUCCESS_REBOOT_REQUIRED)
	{
		WriteLog(_T("Uninstall.exe return %d. A reboot is required."), m_dwReturnCode);
	}
	else
	{
		WriteLog(_T("Uninstall.exe return %d"), m_dwReturnCode);
	}

	if (m_blExePost)
	{
		SelfDelete(bReboot);

		Sleep(1000);
	}

	if (bReboot && !m_blExePost)
	{
		WriteLog(_T("Setup prepare to reboot the system..."));

		RebootWinnt();
	}

	WriteLog(_T("*************************  End  ***************************\r\n"));
	
	return m_dwReturnCode;
}

void CUninstallApp::UninstallSummary()
{
	WriteLog(_T("------------------ Uninstall Summary Begin ----------------"));

	
	if(m_aryProducts.GetCount() <=0)
	{
		WriteLog(_T("No product is removed."));
		goto summaryend;
	}

	int nRemoveCount = 0;
	for(int nIndex=0; nIndex < m_aryProducts.GetCount(); nIndex++)
	{
		if(m_aryProducts.GetAt(nIndex).m_blSelected && (m_aryProducts.GetAt(nIndex).m_nStatus == M_STATUS_COMPLETED || m_aryProducts.GetAt(nIndex).m_nStatus == M_STATUS_FAILED))
		{
			nRemoveCount++;

			if(nRemoveCount > 1)
			{
				WriteLog(_T("-----------------------------------------------------------"));
			}

			WriteLog(_T("Product: %s"),m_aryProducts.GetAt(nIndex).m_strProductName);
			WriteLog(_T("Version: %s"),m_aryProducts.GetAt(nIndex).m_strProductVersion);
			WriteLog(_T("ExitCode: %d"),m_aryProducts.GetAt(nIndex).m_dwExitCode);
			if(m_aryProducts.GetAt(nIndex).m_dwExitCode == ERROR_SUCCESS)
			{
				WriteLog(_T("Result: Uninstall Success."));
			}
			else if(m_aryProducts.GetAt(nIndex).m_dwExitCode == ERROR_SUCCESS_REBOOT_REQUIRED)
			{
				WriteLog(_T("Result: Uninstall Success And A Reboot Required."));
			}
			else
			{
				WriteLog(_T("Result: Uninstall Failed."));
				WriteLog(_T("LogFile: %s."),m_aryProducts.GetAt(nIndex).m_strProductLogFile);
			}
		}
	}

	if(nRemoveCount == 0)
	{
		WriteLog(_T("No product is removed."));
	}

summaryend:

	WriteLog(_T("------------------ Uninstall Summary End   ----------------"));
}

BOOL CUninstallApp::IsOtherInstanceRunning()
{
	HANDLE hFlage = OpenEvent(EVENT_MODIFY_STATE, FALSE, UNINSTALL_SINGLESTON_FLAG);
	if (NULL != hFlage)
	{
		CloseHandle(hFlage);
		hFlage = NULL;
		return TRUE;
	}

	return FALSE;
}

BOOL CUninstallApp::CreateRunningFlag()
{
	if (NULL != m_hProcessEvent)
		return TRUE;

	m_hProcessEvent = CreateEvent(NULL, TRUE, TRUE, UNINSTALL_SINGLESTON_FLAG);
	if (NULL == m_hProcessEvent)
		return FALSE;

	return TRUE;
}

void CUninstallApp::RemoveRebootFlag()
{
	TCHAR szFile[MAX_PATH];
	::GetSystemWindowsDirectory(szFile, MAX_PATH);
	::PathAppend(szFile, _T("Temp"));
	::PathAppend(szFile, REBOOT_FLAG_FILE_UNINSTALL);
	if (_taccess(szFile,0) == 0)
	{
		BOOL bRet = ::DeleteFile(szFile);
		if(bRet)
		{
			WriteLog(_T("Remove the reboot flag(%s) successfully."), szFile);
		}
		else
		{
			WriteLog(_T("Fail to remove the reboot flag(%s).Error:%d"), szFile,::GetLastError());
		}
	}
	else
	{
		WriteLog(_T("The reboot flag(%s) doesn't exist, No need handle it."), szFile);
	}
}

void CUninstallApp::RebootWinnt(DWORD dwReason)
{
	HANDLE hToken;
	TOKEN_PRIVILEGES tkp;

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

	if (::GetLastError() != ERROR_SUCCESS)
	{
		return;
	}

	//before reboot, remove the flag file
	RemoveRebootFlag();

	WriteLog(_T("Setup is rebooting the system..."));
	// Display the shutdown dialog box and start the time-out countdown.
	::ExitWindowsEx(EWX_REBOOT, dwReason);

	// Disable shutdown privilege. 
	tkp.Privileges[0].Attributes = 0; 
	
	::AdjustTokenPrivileges(hToken, FALSE, &tkp, 0, (PTOKEN_PRIVILEGES) NULL, 0);
}

BOOL CUninstallApp::GetRestartSpecServices()
{
	m_aryRestartSpecService.RemoveAll();

	CString strConfigureFile;
	strConfigureFile.Format(_T("%s\\%s"), m_strWorkingDir, INI_CONFIGURE);

	if (_taccess(strConfigureFile, 00) == -1)
	{
		WriteLog(_T("Warning: %s does NOT exist. Setup will quit"), strConfigureFile);
		return FALSE;
	}

		const int MAX_BUFFER_SIZE = 10240;
	const CString strRestartSpecServiceSec = _T("RestartSpecService");

	LPTSTR lpReturnedString = new TCHAR[MAX_BUFFER_SIZE];

	if (!SetupGetPrivateProfileSection(strRestartSpecServiceSec, lpReturnedString, MAX_BUFFER_SIZE, strConfigureFile))
	{
		delete []lpReturnedString;
		return FALSE;
	}

	const CString strUninstallProduct = _T("UninstallProduct");
	const CString strCondition = _T("Condition");
	const CString strService = _T("Service");


	TCHAR szValue[MAX_PATH];
	TCHAR szSectionName[MAX_PATH];

	LPTSTR lpFilePos = lpReturnedString;
	while (*lpFilePos != 0)
	{
		_tcscpy_s(szSectionName, _countof(szSectionName), lpFilePos);

		lpFilePos = lpFilePos + _tcslen(lpFilePos) + 1;

		TRACE(_T("%s\n"), szSectionName);
		RESTARTSPECSERVICE rs;

		if (_tcslen(szSectionName))
		{
			//UninstallProduct
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strUninstallProduct,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);

			rs.strUninstallProducts = szValue;

			//Condition
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strCondition,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);

			rs.strCondition = szValue;

			//Service
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strService,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);
			rs.strServiceName = szValue;

			if(!rs.strServiceName.IsEmpty())
			{
				m_aryRestartSpecService.Add(rs);
			}
		}
	}

	return m_aryRestartSpecService.GetCount();
}

BOOL CUninstallApp::GetInstalledProducts()
{
	m_aryProducts.RemoveAll();

	CString strConfigureFile;
	strConfigureFile.Format(_T("%s\\%s"), m_strWorkingDir, INI_CONFIGURE);

	if (_taccess(strConfigureFile, 00) == -1)
	{
		WriteLog(_T("Warning: %s does NOT exist. Setup will quit"), strConfigureFile);
		return FALSE;
	}

	const int MAX_BUFFER_SIZE = 10240;
	const CString strCompProductSec = _T("CompleteProductList");

	LPTSTR lpReturnedString = new TCHAR[MAX_BUFFER_SIZE];

	if (!SetupGetPrivateProfileSection(strCompProductSec, lpReturnedString, MAX_BUFFER_SIZE, strConfigureFile))
	{
		delete []lpReturnedString;
		return FALSE;
	}

	const CString strProductCodeKey = _T("ProductCode");
	const CString strComponentNameKey = _T("ComponentName");
	const CString strComponentTypeKey = _T("ComponentType");
	const CString strNewProductName = _T("NewProductName");
	const CString strDependentFeaturesKey = _T("DependentFeatures");
	const CString strShareComponentsKey = _T("ShareComponents");
	const CString strPreUninstallComponentsKey = _T("PreUninstallComponents");
	const CString strExecutableKey = _T("Executable");
	const CString strCommandLineKey = _T("CommandLine");
	const CString strExtendCommandLineKey = _T("ExtendCommandLine");
	const CString strLogFile = _T("LOGFILE");
	const CString strBeforeUnisntallKey = _T("BeforeUnisntall");
	const CString strAfterUnisntallKey = _T("AfterUnisntall");
	const CString strVersionCheckKey = _T("VersionCheck");
	const CString strEstInstallTime = _T("EstInstallTime");
	const CString strFileSize = _T("FileSize");
	const CString strSuccessValues = _T("SuccessValues");
	const CString strRebootValues = _T("RebootValues");
	const CString strRestartServices = _T("RestartServices");
	const CString strUAServiceHandle = _T("UAServiceHandle");
	const CString strStopServices = _T("StopServices");

	TCHAR szValue[MAX_PATH];
	TCHAR szSectionName[MAX_PATH];

	LPTSTR lpFilePos = lpReturnedString;
	while (*lpFilePos != 0)
	{
		_tcscpy_s(szSectionName, _countof(szSectionName), lpFilePos);

		lpFilePos = lpFilePos + _tcslen(lpFilePos) + 1;

		TRACE(_T("%s\n"), szSectionName);

		if (_tcslen(szSectionName))
		{
			//Product Code
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strProductCodeKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);

			if (_tcslen(szValue) <= 0)
			{
				continue;
			}
			else
			{
				INSTALLSTATE isRet = MsiQueryProductState(szValue);
				if (isRet != INSTALLSTATE_DEFAULT)
					continue;
			}

			CUninstallConfigure product;

			product.m_strProductCode = szValue;

			if (IsRemoveAll() || m_strProductCode.CompareNoCase(product.m_strProductCode) == 0)
			{
				product.m_blSelected = TRUE;
			}

			//Product Name
			TCHAR szProductName[MAX_PATH];
			memset(szProductName, 0, sizeof(szProductName));
			DWORD dwSize = _countof(szProductName);
			MsiGetProductInfo(szValue, INSTALLPROPERTY_INSTALLEDPRODUCTNAME, szProductName, &dwSize);

			product.m_strProductName = szProductName;

			WriteLog(_T("Produc:%s"), szProductName);
			//Product Version
			memset(szProductName, 0, sizeof(szProductName));
			dwSize = _countof(szProductName);
			MsiGetProductInfo(szValue, INSTALLPROPERTY_VERSIONSTRING, szProductName, &dwSize);
			product.m_strProductVersion = szProductName;

			WriteLog(_T("Version:%s"), szProductName);

			//InstallPath
			memset(szProductName, 0, sizeof(szProductName));
			dwSize = _countof(szProductName);
			MsiGetProductInfo(szValue, INSTALLPROPERTY_INSTALLLOCATION, szProductName, &dwSize);
			product.m_strInstallPath = szProductName;

			WriteLog(_T("InstallPath:%s"), szProductName);

			//new product name (this is for localizaton)
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strNewProductName,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);

			CString strNewName = szValue;
			if(strNewName.IsEmpty())
			{
				WriteLog(_T("No need get the new product name."));
			}
			else
			{
				WriteLog(_T("Need get the new product name according to the key (%s)."),strNewName);
				CString strNew = GetNewProductName(strNewName);
				
				if(strNew.IsEmpty())
				{
					WriteLog(_T("Not get the new product name from resouce dll. No need handle it."));
				}
				else if(strNew.CompareNoCase(product.m_strProductName) == 0)
				{
					WriteLog(_T("Get the new product name (%s) from resouce dll. it is the same as the current name.No need handle it."),strNew);
				}
				else
				{
					WriteLog(_T("Get the new product name (%s) from resouce dll. Replace the product name (%s) with (%s)"),strNew,product.m_strProductName,strNew);
					product.m_strProductName = strNew;;
				}
			}

			//component name
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strComponentNameKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);

			product.m_strComponentName = szValue;

			//DependentFeatures
			memset(szValue, 0, sizeof(szValue));
			if (SetupGetPrivateProfileString(szSectionName,
				strDependentFeaturesKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile))
			{
				if (_tcslen(szValue))
				{
					GetStringArray(szValue, _T(';'), product.m_strDependentFeatures);
				}
			}

			//ShareComponents
			memset(szValue, 0, sizeof(szValue));
			if (SetupGetPrivateProfileString(szSectionName,
				strShareComponentsKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile))
			{
				if (_tcslen(szValue))
				{
					GetStringArray(szValue, _T(';'), product.m_strSharedComponents);
				}
			}

			//Preuninstall Components
			memset(szValue, 0, sizeof(szValue));
			if (SetupGetPrivateProfileString(szSectionName,
				strPreUninstallComponentsKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile))
			{
				if (_tcslen(szValue))
				{
					GetStringArray(szValue, _T(';'), product.m_strPreUninstallComponents);
				}
			}

			//Executable
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strExecutableKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);

			product.m_strExecutable = szValue;

			//CommandLine
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strCommandLineKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);

			product.m_strCommandLine = szValue;

			//ExtendCommandLine
			if (!m_blMsiUI)
			{
				memset(szValue, 0, sizeof(szValue));
				SetupGetPrivateProfileString(szSectionName,
					strExtendCommandLineKey,
					_T(""),
					szValue,
					MAX_PATH,
					strConfigureFile);
				if (_tcslen(szValue))
				{
					product.m_strCommandLine.Append(_T(" "));
					product.m_strCommandLine.Append(szValue);
				}
			}

			//LogFile
			if (!m_blMsiUI)
			{
				memset(szValue, 0, sizeof(szValue));
				SetupGetPrivateProfileString(szSectionName,
					strLogFile,
					_T(""),
					szValue,
					MAX_PATH,
					strConfigureFile);
				if (_tcslen(szValue))
				{
					product.m_strProductLogFile = szValue;
				}
			}

			
			//AfterUnisntall
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strBeforeUnisntallKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);
			product.m_strBeforeUnisntall = szValue;

			//AfterUnisntall
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strAfterUnisntallKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);
			product.m_strAfterUnisntall = szValue;

			//VersionCheck
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strVersionCheckKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);

			product.m_strVersionCheck = szValue;

			//SuccessValues
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strSuccessValues,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);
			PathRemoveBlanks(szValue);

			if (_tcslen(szValue))
			{
				GetDWordArray(szValue, _T(';'), product.m_arySuccessValues);
			}

			//RebootValues
			memset(szValue, 0, sizeof(szValue));
			SetupGetPrivateProfileString(szSectionName,
				strRebootValues,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile);
			PathRemoveBlanks(szValue);

			if (_tcslen(szValue))
			{
				GetDWordArray(szValue, _T(';'), product.m_aryRebootValues);
			}

			//RestartServices
			memset(szValue, 0, sizeof(szValue));
			if (SetupGetPrivateProfileString(szSectionName,
				strRestartServices,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile))
			{
				if (_tcslen(szValue))
				{
					GetStringArray(szValue, _T(';'), product.m_strRestartServices);
				}
			}

			//StopServices
			memset(szValue, 0, sizeof(szValue));
			if (SetupGetPrivateProfileString(szSectionName,
				strStopServices,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile))
			{
				if (_tcslen(szValue))
				{
					GetStringArray(szValue, _T(';'), product.m_strStopServices);
				}
			}

			//ComponentType
			product.m_dwComponentType = SetupGetPrivateProfileInt(szSectionName, 
			strComponentTypeKey, 
			CAPRODUCT,
			strConfigureFile);

			//UAServiceHandle
			product.m_nUAServiceHandle = SetupGetPrivateProfileInt(szSectionName, 
			strUAServiceHandle, 
			0,
			strConfigureFile);

			//EstInstallTime
			product.m_nEstInstallTime = SetupGetPrivateProfileInt(szSectionName, 
			strEstInstallTime, 
			120,
			strConfigureFile);

			//FileSize
			product.m_nSize = SetupGetPrivateProfileInt(szSectionName, 
				strFileSize, 
				0,
				strConfigureFile) * 1024;
			
			product.m_strShortName = szSectionName;

			//Add the installed component to the Array
			m_aryProducts.Add(product);
		}
	}

	delete []lpReturnedString;

	return m_aryProducts.GetSize() > 0;
}

BOOL CUninstallApp::SilentUninstallProducts()
{
	if (m_strProductCode.IsEmpty() && !IsRemoveAll())
	{
		WriteLog(_T("Uninstallation could not continue for product code is empty and -all flag is not set!"));
		return FALSE;
	}

	//for silent, we need handle the depended products
	DoProductSelectedWithDepend();

	//Get the UA status before un-install.
	GetUAserviceStatus();

	GetNeedRestartSpecService(m_strRestartSpecServices);

	UninstallProducts(NULL, TRUE);

	return TRUE;
}

BOOL CUninstallApp::SimpleUIUninstallProducts()
{
	if (m_strProductCode.IsEmpty() && !IsRemoveAll())
	{
		WriteLog(_T("Uninstallation could not continue for product code is empty and -all flag is not set!"));
		return FALSE;
	}

	//Get the UA status before un-install.
	GetUAserviceStatus();

	//show confirm dialog
	CString strTitle, strMsg;

	strTitle.LoadString(IDS_STRING_APPLICATION_TITLE);

	strMsg.Format(IDS_STRING_REMOVE_CONFIRM,m_strProductDisplayName);

	if(m_bNeedHandleUAService)
	{
		CString strNote;
		strNote.LoadString(IDS_STRING_NOTE);
		strMsg += strNote;
	}

	if (IDYES == ::MessageBox(NULL, strMsg, strTitle, MB_YESNO|MB_ICONQUESTION|MB_TOPMOST))
	{
		WriteLog(_T("User select Yes to uninstall the product."));
	}
	else
	{
		WriteLog(_T("User select No to uninstall the product."));
		m_bNeedRunPost = FALSE;
		m_bCanel = TRUE;
		return FALSE;
	}
	//end show

	//show simple UI 
	CUninstallSimpleDlg dlg;
	dlg.DoModal();

	strMsg.Format(IDS_STRING_REMOVE_ERROR,m_strProductDisplayName);
	if(dlg.IsUninstallFailed())
	{
		::MessageBox(NULL, strMsg, strTitle, MB_ICONERROR|MB_SERVICE_NOTIFICATION);
	}

	return TRUE;
}

int CUninstallApp::GetIndexByShortName(const CString &strShortName)
{
	INT_PTR nSize = m_aryProducts.GetSize();
	for (int i=0; i<nSize; i++)
	{
		if (m_aryProducts.GetAt(i).m_strShortName == strShortName)
		{
			return i;
		}
	}

	return -1;
}

int CUninstallApp::GetIndexByProductName(const CString &strProdName)
{
	CString strText = strProdName;
	strText.TrimLeft();
	INT_PTR nSize = m_aryProducts.GetSize();
	for (int i=0; i<nSize; i++)
	{
		if (m_aryProducts.GetAt(i).m_strProductName == strText)
		{
			return i;
		}
	}

	return -1;
}

BOOL CUninstallApp::GetNeedRestartSpecService(CStringArray& strServiceArray)
{
	strServiceArray.RemoveAll();
	//check if there is no proudct installed.
	if(m_aryRestartSpecService.GetCount() <= 0 || m_aryProducts.GetCount() <= 0 || IsAllProductsUninstalled())
	{
		//if there is no product, all products are removed.
		return FALSE;
	}

	for(int m=0; m<m_aryRestartSpecService.GetCount();m++)
	{
		CStringArray strProductArray,strConditonArray;
		CString strProduct = m_aryRestartSpecService[m].strUninstallProducts;

		if(!strProduct.IsEmpty())
		{
			GetSplitDataList(strProduct,PRODUCT_SPLITER_OR,strProductArray);
		}

		CString strConditon = m_aryRestartSpecService[m].strCondition;

		if(!strConditon.IsEmpty())
		{
			GetSplitDataList(strConditon,PRODUCT_SPLITER_OR,strConditonArray);
		}

		CUninstallConfigure cconfProductObj;

		BOOL bOk = FALSE;
		if(strProductArray.GetCount() <=0)
		{
			bOk = TRUE;
		}
		else
		{
			//check the product
			for(int j=0; j<strProductArray.GetCount();j++)
			{
				for (int i=0; i<m_aryProducts.GetCount(); i++)
				{
					cconfProductObj = m_aryProducts.GetAt(i);
					
					if (cconfProductObj.m_strShortName.Compare(strProductArray[j]) == 0 && cconfProductObj.m_blSelected && cconfProductObj.m_nStatus != M_STATUS_COMPLETED)
					{
						//there is one of produtcs which is selected, it need add the service
						bOk = TRUE;
						break;
					}
				}
			}
		}

		if(!bOk)
		{
			//continue to check the next service.
			continue;
		}

		//check condition
		CString strName;
		for(int k=0; k<strConditonArray.GetCount(); k++)
		{
			strName = strConditonArray.GetAt(k);
			for (int i=0; i<m_aryProducts.GetCount(); i++)
			{
				cconfProductObj = m_aryProducts.GetAt(i);
				if (cconfProductObj.m_strShortName.Compare(strName) == 0 && !cconfProductObj.m_blSelected && cconfProductObj.m_nStatus != M_STATUS_COMPLETED)
				{
					BOOL bExist = FALSE;
				
					//there is one of produtcs which is not selected, it need add the service
					for(int n=0; n<strServiceArray.GetCount();n++)
					{
						if(strServiceArray[n].CompareNoCase(m_aryRestartSpecService[m].strServiceName) == 0)
						{
							//service has been in the array.
							bExist = TRUE;
							break;
						}
					}

					if(!bExist)
					{
						strServiceArray.Add(m_aryRestartSpecService[m].strServiceName);
					}

					break;
				}
			}
		}
	}

	return strServiceArray.GetCount();
}

BOOL CUninstallApp::IsAllProductsUninstalled()
{
	INT_PTR nSize = m_aryProducts.GetSize();
	for (INT_PTR i=0; i<nSize; i++)
	{
		if (m_aryProducts.GetAt(i).m_dwComponentType == CAPRODUCT && M_STATUS_COMPLETED != m_aryProducts.GetAt(i).m_nStatus)
			return FALSE;
	}

	return TRUE;
}

BOOL CUninstallApp::IsProductsUninstalled(const CString& strProductList)
{
	//check if there is no proudct installed.
	if(m_aryProducts.GetCount() <= 0 || IsAllProductsUninstalled())
	{
		//if there is no product, all products are removed.
		return TRUE;
	}

	if(strProductList.IsEmpty())
	{
		return FALSE;
	}

	CStringArray strArray;

	GetSplitDataList(strProductList,PRODUCT_SPLITER,strArray);

	if(strArray.GetCount() <= 0)
	{
		return FALSE;
	}


	CString strName;
	CUninstallConfigure cconfProductObj;
	for(int k=0; k<strArray.GetCount(); k++)
	{
		strName = strArray.GetAt(k);
		for (int i=0; i<m_aryProducts.GetCount(); i++)
		{
			cconfProductObj = m_aryProducts.GetAt(i);
			if (cconfProductObj.m_strShortName.Compare(strName) == 0 && M_STATUS_COMPLETED != cconfProductObj.m_nStatus)
			{
				//there is one of produtcs which is not uninstalled, it will return FALSE
				return FALSE;
			}
		}
	}

	return TRUE;
}

BOOL CUninstallApp::SelfDelete(BOOL bReboot)
{
	WriteLog(_T("-----------SelfDelete starts-----------"));

	const int nSize = MAX_PATH * 3;
	char *lpszBatContent = new char[nSize];

	TCHAR szModuleEX[MAX_PATH] = { 0 };
	char szDelFileName[MAX_PATH] = { 0 };
	char szModule[MAX_PATH] = { 0 };
	char szDrive[_MAX_DRIVE] = { 0 };
	char szDir[_MAX_DIR] = { 0 };
	char szFileName[_MAX_FNAME] = { 0 };

	BOOL bIsUnderW2k3 = IsOSUnderW2k3();

	GetModuleFileName(NULL, szModuleEX, MAX_PATH);
	GetModuleFileNameA(NULL, szModule, MAX_PATH);

	_splitpath_s(szModule, szDrive, _countof(szDrive), szDir, _countof(szDir), szFileName, _countof(szFileName), NULL, 0);
	RemoveReadOnlyAttibuteA(szModule);

	//Delete INI_CONFIGUREA
	sprintf_s(szDelFileName, _countof(szDelFileName), "%s%s%s", szDrive, szDir, INI_CONFIGUREA);
	RemoveReadOnlyAttibuteA(szDelFileName);
	DeleteFileA(szDelFileName);

	//Delete UNINSTALL_DLLA
	sprintf_s(szDelFileName, _countof(szDelFileName), "%s%s%s", szDrive, szDir, UNINSTALL_DLLA);
	RemoveReadOnlyAttibuteA(szDelFileName);
	DeleteFileA(szDelFileName);

	TCHAR strTempBat[MAX_PATH];
	::GetSystemWindowsDirectory(strTempBat, _countof(strTempBat));
	::PathAppend(strTempBat, _T("Temp"));
	

	//Delete Uninstall.exe and its parent folder which is empty
	char szTempPath[MAX_PATH];
	memset(szTempPath, 0, sizeof(szTempPath));
	::GetSystemWindowsDirectoryA(szTempPath,_countof(szTempPath));
	::PathAppendA(szTempPath, "Temp");
	sprintf_s(m_szBatFile, _countof(m_szBatFile), "%s", szTempPath);

	PathAppendA(m_szBatFile, SELDEL_BAT_NAME);
	::PathAppend(strTempBat, SELDEL_BAT_NAME_T);

	try
	{
		HANDLE hFile = CreateFileA(m_szBatFile,
			GENERIC_ALL,
			0,
			NULL,
			CREATE_ALWAYS,
			FILE_ATTRIBUTE_NORMAL,
			NULL);

		if (hFile == INVALID_HANDLE_VALUE)
		{
			delete []lpszBatContent;
			return FALSE;
		}

		
		if (bReboot && bIsUnderW2k3)
		{
			//fix issue 142898(the new installed uninstall.exe will be deleted on some windows 2003 after reboot.)
			WriteLog(_T("The system does not delete the file(uninstall.exe) until the operating system is restarted."));
			MoveFileEx(szModuleEX, NULL, MOVEFILE_DELAY_UNTIL_REBOOT);
		}
		else
		{
			sprintf_s(lpszBatContent, nSize, ":DLoop\r\ndel \"%s\" /Q\r\nif exist \"%s\" goto DLoop\r\n", szModule, szModule);
		}

		DWORD dwBytesToWrite = DWORD(strlen(lpszBatContent) * sizeof(char));
		DWORD dwBytesWritten;
		DWORD dwFileOffset = SetFilePointer(hFile, 0, NULL, FILE_END);

		LockFile(hFile, dwFileOffset, 0, dwFileOffset + dwBytesToWrite, 0);

		WriteFile(hFile, lpszBatContent, dwBytesToWrite, &dwBytesWritten, NULL);

		char *pParent = NULL;

		while (pParent = strrchr(szModule, '\\'))
		{
			*pParent = '\0';

			if (strlen(szModule) > 2)
			{
				sprintf_s(lpszBatContent, nSize, "rmdir \"%s\" /Q\r\nif exist \"%s\" goto MYEND\r\n", szModule, szModule);
				dwFileOffset = SetFilePointer(hFile, 0, NULL, FILE_END);
				dwBytesToWrite = DWORD(strlen(lpszBatContent) * sizeof(char));
				WriteFile(hFile, lpszBatContent, dwBytesToWrite, &dwBytesWritten, NULL);
			}
		}

		//:MYEND
		sprintf_s(lpszBatContent, nSize, ":MYEND\r\n");
		dwFileOffset = SetFilePointer(hFile, 0, NULL, FILE_END);
		dwBytesToWrite = DWORD(strlen(lpszBatContent) * sizeof(char));

		WriteFile(hFile, lpszBatContent, dwBytesToWrite, &dwBytesWritten, NULL);

		//Reboot(fix issue 12443, this application call the bat which cann't reboot the machine on W2k3 OS  or below using the command line shutdown -f -r -t 0 in the bat file)
		if (bReboot)
		{
			if(!bIsUnderW2k3)
			{
				sprintf_s(lpszBatContent, nSize, "shutdown -f -r -t 0\r\n");
				dwFileOffset = SetFilePointer(hFile, 0, NULL, FILE_END);
				dwBytesToWrite = DWORD(strlen(lpszBatContent) * sizeof(char));
				WriteFile(hFile, lpszBatContent, dwBytesToWrite, &dwBytesWritten, NULL);
				WriteLog(_T("write the shutdown command line to bat file."));
			}
			else
			{
				//under windows 2003 or below, do nothing.
				WriteLog(_T("No need to write the shutdown command line to bat file under w2k3 or below."));
			}

			WriteLog(_T("Setup will reboot the system, please wait a while..."));
		}
	

		//Delete myself
		sprintf_s(lpszBatContent, nSize, "del %%0\r\n");
		dwFileOffset = SetFilePointer(hFile, 0, NULL, FILE_END);
		dwBytesToWrite = DWORD(strlen(lpszBatContent) * sizeof(char));

		WriteFile(hFile, lpszBatContent, dwBytesToWrite, &dwBytesWritten, NULL);

		//The end of writing the bat file

		UnlockFile(hFile, dwFileOffset, 0, dwFileOffset + dwBytesToWrite, 0);

		CloseHandle(hFile);

		delete []lpszBatContent;
		WriteLog(_T("Create BAT file to delete itself"));
	}
	catch(...)
	{
		delete []lpszBatContent;

		WriteLog(_T("Create BAT file failed"));
		return FALSE;
	}

	//fix issue 12443, this application call the bat which cann't reboot the machine on W2k3 OS or below using the command line shutdown -f -r -t 0 in the bat file
	if(bReboot && bIsUnderW2k3)
	{
		//reboot the machine directly and write the bat to runonce
		WriteLog(_T("reboot the machine directly and write the bat to runonce registry on W2k3 OS or below."));
		BOOL bRet=SetRegValue(HKEY_LOCAL_MACHINE,REGKEY_MICROSOFT_WINDOWS_RUNONCE,_T("AS_UNINSTALL_REMOVE"),strTempBat);
		if(!bRet)
		{
			WriteLog(_T("Fail to change the set the runonce registry for bat file successfully."));
		}
		else
		{
			WriteLog(_T("Change the set the runonce registry for bat file successfully."));
		}

		WriteLog(_T("Setup prepare to reboot the system..."));
		RebootWinnt();
		WriteLog(_T("-----------SelfDelete Ends and return TRUE-----------"));

		return TRUE;
	}

	DWORD dwRet = 0;
	STARTUPINFOA si = {0};
	PROCESS_INFORMATION pi = {0};

	si.cb = sizeof(STARTUPINFOA);

	PathQuoteSpacesA(m_szBatFile);

	//launch the bat file
	if (CreateProcessA(NULL,
		m_szBatFile,
		NULL,
		NULL,
		TRUE,
		CREATE_NO_WINDOW,
		NULL,
		szTempPath,
		&si, 
		&pi))
	{	
		if(bReboot)
		{
			//remove the flag file
			RemoveRebootFlag();
		}

		WriteLog(_T("Launch SelfDelete command line successfully."));
		CloseHandle(pi.hThread);
		CloseHandle(pi.hProcess);
	}
	else
	{
		WriteLog(_T("Launch command line failed and return FALSE"));
		return FALSE;
	}

	WriteLog(_T("-----------SelfDelete Ends and return TRUE-----------"));

	return TRUE;
}

int CUninstallApp::GetRunningProcess(LPCTSTR lpctProcessName)
{
    // Get the list of process identifiers.
    DWORD aProcesses[1024], cbNeeded, cProcesses, i;

    if (!EnumProcesses(aProcesses, sizeof(aProcesses), &cbNeeded))
        return 0;

    // Calculate how many process identifiers were returned.
    cProcesses = cbNeeded / sizeof(DWORD);

	int nSize = 0;
    // Get the name and process identifier for each process.
    for (i = 0; i < cProcesses; i++)
	{
		TCHAR szProcessName[MAX_PATH] = _T("<unknown>");

		// Get a handle to the process.
		HANDLE hProcess = ::OpenProcess(PROCESS_QUERY_INFORMATION |
									   PROCESS_VM_READ,
									   FALSE, aProcesses[i]);

		// Get the process name.
		if (NULL != hProcess)
		{
			HMODULE hMod;
			DWORD cbNeeded;

			if (::EnumProcessModules(hProcess, &hMod, sizeof(hMod), 
				 &cbNeeded))
			{
				::GetModuleBaseName(hProcess, hMod, szProcessName, 
								   sizeof(szProcessName)/sizeof(TCHAR));
			}
		}

		// Print the process name and identifier.
		//TRACE(_T("%s  (PID: %u)\n"), szProcessName, aProcesses[i]);

		if (_tcsicmp(lpctProcessName, szProcessName) == 0)
		{
			nSize++;
		}

		::CloseHandle(hProcess);
	}

	return nSize;
}

BOOL CUninstallApp::ProcessesRunningBlock()
{
	TCHAR szFileName[MAX_PATH];
	_stprintf_s(szFileName , _countof(szFileName), _T("%s\\%s"), m_strWorkingDir, INI_CONFIGURE);

	LPCTSTR lpAppName = _T("ProcessBlock");

	const int MAX_BUFFER_SIZE = 1024;
	LPTSTR lpReturnedString = new TCHAR[MAX_BUFFER_SIZE];

	//Q198906: GetPrivateProfileSection has bug
	SetupGetPrivateProfileString(lpAppName, NULL, NULL,
				lpReturnedString, MAX_BUFFER_SIZE, szFileName);
				
	if (!SetupGetPrivateProfileSection(lpAppName, lpReturnedString, MAX_BUFFER_SIZE, szFileName))
	{
		delete []lpReturnedString;
		return FALSE;
	}

	LPTSTR lpFilePos = lpReturnedString;
	while (*lpFilePos != 0)
	{
		TRACE(_T("%s\n"), lpFilePos);

		if (_tcslen(lpFilePos))
		{
			if (GetRunningProcess(lpFilePos) > 0)
			{
				CString strMsg, strMsgFormat;
				strMsgFormat.LoadString(IDS_STRING_PROCESS_RUNNING);
				if (strMsgFormat.Find(_T("%s")) >= 0)
				{
					strMsg.Format(strMsgFormat, lpFilePos);
					WriteLog(strMsg);

					if (!m_blSilent || m_blMsiUI)
					{
						CString strTitle;
						strTitle.LoadString(IDS_STRING_APPLICATION_TITLE);

						::MessageBox(NULL, strMsg, strTitle, MB_OK|MB_ICONWARNING);
					}
				}

				delete []lpReturnedString;

				return TRUE;
			}
		}

		lpFilePos = lpFilePos + _tcslen(lpFilePos) + 1;
	}

	delete []lpReturnedString;

	return FALSE;
}

void CUninstallApp::RemoveReadOnlyAttibuteA(LPCSTR lpcFile)
{
	DWORD dwFileAttributes = ::GetFileAttributesA(lpcFile);

	if ((dwFileAttributes & FILE_ATTRIBUTE_READONLY) == FILE_ATTRIBUTE_READONLY)
	{
		dwFileAttributes ^= FILE_ATTRIBUTE_READONLY;

		::SetFileAttributesA(lpcFile, dwFileAttributes);
	}
}

void CUninstallApp::GetStringArray(LPCTSTR lpctValue, TCHAR cSplitChar, CStringArray& data)
{
	if (NULL == lpctValue)
		return;

	TRACE(_T("\n%s"), lpctValue);

	LPCTSTR lptPre, lptNext;
	lptPre = lpctValue;
	lptNext = lpctValue;

	int nSize;
	CString strTmp;
	while (*lptPre != '\0' && (lptNext = _tcschr(lptPre, cSplitChar)) != NULL)
	{
		//Point to the last cSplitChar, like "aa;;;bb", lptNext will point to last ';' before 'b'
		while (*(lptNext+1) == cSplitChar)
			lptNext++;

		//Deal with this case: ";aa;bb"
		nSize = lptNext - lptPre;
		if (nSize == 0)
		{
			lptPre = lptNext + 1;
			continue;
		}

		//Get the sub string
		LPTSTR pBuff = strTmp.GetBuffer(nSize+1);
		_tcsncpy_s(pBuff, nSize+1, lptPre, nSize);
		pBuff[nSize] = '\0';
		strTmp.ReleaseBuffer();
		strTmp.Trim();
		strTmp.Trim(cSplitChar);

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
		strTmp.Trim();
		strTmp.Trim(cSplitChar);
		if (!strTmp.IsEmpty())
		{
			TRACE(_T("\n%s"), strTmp);
			data.Add(strTmp);
		}
	}
}

void CUninstallApp::RemoveRegistry(CString strConfigureFile)
{
	const CString strRegRootKey = _T("RegRoot");
	const CString strRegSubKey = _T("RegSubKey");
	const CString strRegValueKey = _T("RegValue");

	WriteLog(_T("RemoveRegistry Begin"));

	if (_taccess(strConfigureFile, 00) == -1)
	{
		WriteLog(_T("Warning: %s does NOT exist."), strConfigureFile);
		WriteLog(_T("RemoveRegistry End"));
		return;
	}

	HKEY hRoot = NULL;
	TCHAR szValue[MAX_PATH] = { 0 };
	CString strTempRegRootKey;
	CString strTempRegSubKey;
	CString strTempRegValueKey;

	CString strRegRoot, strRegSub, strRegValue;

	for (int i=0; i < MAX_LIST_DATA; i++)
	{
		strTempRegRootKey.Format(_T("%s%d"), strRegRootKey, i);
		strTempRegSubKey.Format(_T("%s%d"), strRegSubKey, i);
		strTempRegValueKey.Format(_T("%s%d"), strRegValueKey, i);

		//get the Root
		memset(szValue, 0, sizeof(szValue));
		SetupGetPrivateProfileString(INI_KEY_REMOVEREGISTRY,
			strTempRegRootKey,
			_T(""),
			szValue,
			MAX_PATH,
			strConfigureFile);

		strRegRoot = szValue;
		strRegRoot.Trim();
		WriteLog(_T("Get the RegRoot: RegRoot=%s"), strRegRoot);

		if (strRegRoot.IsEmpty())
		{
			WriteLog(_T("The key is empty, no need handle it."));
			break;
		}
		else
		{
			if (strRegRoot.CompareNoCase(_T("HKEY_LOCAL_MACHINE")) == 0)
			{
				hRoot = HKEY_LOCAL_MACHINE;
			}
			else if (strRegRoot.CompareNoCase(_T("HKEY_CURRENT_USER")) == 0)
			{
				hRoot = HKEY_CURRENT_USER;
			}
			else if (strRegRoot.CompareNoCase(_T("HKEY_CLASSES_ROOT")) == 0)
			{
				hRoot = HKEY_CLASSES_ROOT;
			}
			else if (strRegRoot.CompareNoCase(_T("HKEY_CURRENT_USER")) == 0)
			{
				hRoot = HKEY_CURRENT_USER;
			}
			else if (strRegRoot.CompareNoCase(_T("HKEY_USERS")) == 0)
			{
				hRoot = HKEY_USERS;
			}
			else
			{
				WriteLog(_T("The key(%s) is invalid, no need handle it."), strRegRoot);
				continue;
			}
		}

		//get the sub key
		memset(szValue, 0, sizeof(szValue));
		SetupGetPrivateProfileString(INI_KEY_REMOVEREGISTRY,
			strTempRegSubKey,
			_T(""),
			szValue,
			MAX_PATH,
			strConfigureFile);

		strRegSub = szValue;
		strRegSub.Trim(_T("\\ "));
		WriteLog(_T("Get the RegSubKey: RegSubKey=%s"), strRegSub);

		if (strRegSub.IsEmpty())
		{
			WriteLog(_T("The key is empty, no need handle it."));
			break;
		}

		//get the registry value
		memset(szValue, 0, sizeof(szValue));
		SetupGetPrivateProfileString(INI_KEY_REMOVEREGISTRY,
			strTempRegValueKey,
			_T(""),
			szValue,
			MAX_PATH,
			strConfigureFile);

		strRegValue = szValue;
		strRegValue.Trim(_T("\\ "));
		WriteLog(_T("Get the RegValue: RegSubKey=%s"), strRegValue);

		//check if the registry is under WOW6432NODE
		BOOL bNative = TRUE;
		CString strTemp = strRegSub;
		strTemp.MakeUpper();

		if(strTemp.Find(_T("SOFTWARE\\WOW6432NODE\\")) == 0)
		{
			bNative = FALSE;
		}

		if (!strRegValue.IsEmpty())
		{
			//handle value
			TCHAR szRegKey[MAX_PATH] = { 0 };
			_stprintf_s(szRegKey, _countof(szRegKey), _T("%s\\%s"), strRegSub, strRegValue);
			if (DeleteRegValue(hRoot, strRegSub, strRegValue, bNative))
			{
				WriteLog(_T("Remove the registry value (%s) successfully."), szRegKey);
			}
			else
			{
				WriteLog(_T("Fail to remove the registry value (%s)."), szRegKey);
			}
		}
		else
		{
			//handle key
			 if (ERROR_SUCCESS == RecursiveDeleteKey(hRoot, strRegSub, bNative))
			{
				WriteLog(_T("Remove the registry key (%s) successfully."), strRegSub);
			}
			else
			{
				WriteLog(_T("Fail to remove the registry key (%s)."), strRegSub);
			}

			strTemp = strRegSub;
			for (int k = 0; k < MAX_LIST_DATA; k++)
			{
				int nIndex = strTemp.ReverseFind(_T('\\'));
				if (nIndex > 0)
				{
					CString strKey = strTemp.Left(nIndex);

					strTemp = strKey;

					//remove the UDP root etmpy key
					if (RemoveEmptyRegistry(hRoot, strKey, bNative))
					{
						WriteLog(_T("Remove the empty key (%s) successfully."), strKey);
					}
					else
					{
						WriteLog(_T("It is not empty key(%s). No need remove it."), strKey);
						break;
					}
				}
				else
				{
					break;
				}
			}//end for for (;;)
		}//end if
	}//for (int i=0; i < MAX_LIST_DATA; i++)

	WriteLog(_T("RemoveRegistry End"));
}

void CUninstallApp::ExecPostUninstall()
{
	WriteLog(_T("ExecPostUninstall Begin"));

	if (m_blExePost)
	{
		WriteLog(_T("ExecPostUninstall has run"));
		return;
	}

	CString strConfigureFile;
	strConfigureFile.Format(_T("%s\\%s"), m_strWorkingDir, INI_CONFIGURE);

	if (_taccess(strConfigureFile, 00) == -1)
	{
		WriteLog(_T("Warning: %s does NOT exist. ExecPostUninstall will quit"), strConfigureFile);
		return;
	}

	const int MAX_BUFFER_SIZE = 10240;
	const CString strCompProductSec = _T("PostUninstall");
	CString strKeyName;

	LPTSTR lpReturnedString = new TCHAR[MAX_BUFFER_SIZE];

	if (!SetupGetPrivateProfileSection(strCompProductSec, lpReturnedString, MAX_BUFFER_SIZE, strConfigureFile))
	{
		delete []lpReturnedString;
		return;
	}

	const CString strProductCodeKey = _T("CommandLine");
	const CString strConditioanKey = _T("Condition");
	const CString strErrorCheckKey = _T("ErrorCheck");

	

	TCHAR szValue[MAX_PATH];
	TCHAR szSectionName[MAX_PATH];
	DWORD dwRet, dwExitCode;
	BOOL bUnisntallError = FALSE;

	LPTSTR lpFilePos = lpReturnedString;
	while (*lpFilePos != 0)
	{
		_tcscpy_s(szSectionName, _countof(szSectionName), lpFilePos);

		lpFilePos = lpFilePos + _tcslen(lpFilePos) + 1;

		TRACE(_T("%s\n"), szSectionName);

		strKeyName = szSectionName;

		if (strKeyName.CompareNoCase(INI_KEY_REMOVEREGISTRY) == 0)
		{
			if (!IsAllProductsUninstalled())
			{
				WriteLog(_T("Products are not all uninstalled. No need call RemoveRegistry"));
				continue;
			}

			//remove registry when all products are uninstalled
			RemoveRegistry(strConfigureFile);
			continue;
		}

		if (_tcslen(szSectionName))
		{
			//CommandLine
			memset(szValue, 0, sizeof(szValue));
			if  (SetupGetPrivateProfileString(szSectionName,
				strProductCodeKey,
				_T(""),
				szValue,
				MAX_PATH,
				strConfigureFile))
			{
				if (_tcslen(szValue) <= 0)
				{
					continue;
				}

				CString strCmdLine;
				strCmdLine = szValue;

				if (!strCmdLine.IsEmpty())
				{
					strCmdLine.Replace(_T("<WORKINGDIR>"), GetWorkingDir());

					strCmdLine.Replace(_T("<LOGPATH>"), m_strLogPathWithTime);

					if (!m_strUDPRoot.IsEmpty())
						strCmdLine.Replace(_T("<UDPPATH>"), m_strUDPRoot);

					if(theApp.IsRollBack())
					{
						//rollback, need append the rollback string for log file, like Uninstall-Rollback.log
						strCmdLine.Replace(_T("<ROLLBACK>"), FILE_ROLLBACK);
					}
					else
					{
						//no rollback, no need append the rollback string for log file, like Uninstall.log
						strCmdLine.Replace(_T("<ROLLBACK>"), _T(""));
					}
				}

				CString strErrorCheck;
				//get the ErrorChec
				memset(szValue, 0, sizeof(szValue));
				SetupGetPrivateProfileString(szSectionName,
					strErrorCheckKey,
					_T(""),
					szValue,
					MAX_PATH,
					strConfigureFile);

				strErrorCheck = szValue;
				strErrorCheck.Trim();
				WriteLog(_T("Get the ErrorCheck: ErrorCheck=%s"),strErrorCheck);

				//if ErrorCheck is set 1, setup will check the return code
				BOOL bErrorCheck = FALSE;
				if(strErrorCheck.CompareNoCase(_T("1"))==0)
				{
					WriteLog(_T("Need check the return code."));
					bErrorCheck = TRUE;
				}

				CString strCondition;
				//get the condtion
				memset(szValue, 0, sizeof(szValue));
				SetupGetPrivateProfileString(szSectionName,
					strConditioanKey,
					_T(""),
					szValue,
					MAX_PATH,
					strConfigureFile);

				strCondition = szValue;
				WriteLog(_T("Get the condition: condition=%s"),strCondition);
				
				//end condition
				
				//check the condition
				if (strCondition.IsEmpty())
				{
					if(!IsAllProductsUninstalled())
					{
						WriteLog(_T("Products are not all uninstalled. No need call the command line(%s)"),strCmdLine);
						continue;
					}
				}
				else
				{
					if(!IsProductsUninstalled(strCondition))
					{
						WriteLog(_T("Products(%s) are not all uninstalled. No need call the command line(%s)"),strCondition,strCmdLine);
						continue;
					}
				}
				//EN

				theApp.WriteLog(strCmdLine);
				dwRet = theApp.LaunchProcess(strCmdLine.GetBuffer(strCmdLine.GetLength()), theApp.GetWorkingDir(), dwExitCode, INFINITE, DETACHED_PROCESS|CREATE_NO_WINDOW);
				strCmdLine.ReleaseBuffer();

				theApp.WriteLog(_T("Launch %s return [%d]"), strCmdLine, dwExitCode);

				if (dwExitCode == ERROR_SUCCESS_REBOOT_REQUIRED)
				{
					theApp.SetReboot(TRUE);
					SetRebootFlagFile(szSectionName);
				}
				
				if(bErrorCheck && dwExitCode != ERROR_SUCCESS_REBOOT_REQUIRED && dwExitCode != ERROR_SUCCESS)
				{
					//check the return code
					bUnisntallError = TRUE;
					SetErrorCode(dwExitCode);
				}

			}
		}
		//end command line
	}

	delete []lpReturnedString;

	if(IsAllProductsUninstalled())
	{
		if( bUnisntallError)
		{
			WriteLog(_T("The common package isn't uninstalled."));
			m_blExePost = FALSE;
		}
		else
		{
			DeleteEmptyDirectories(m_strUDPRoot);

			WriteLog(_T("All products are uninstalled."));
			m_blExePost = TRUE;
		}
	}

	WriteLog(_T("ExecPostUninstall End"));
}

BOOL CUninstallApp::IsAllowUninstallSharedComponent(LPCTSTR lpctSharedComponent)
{
	INT_PTR nSize = theApp.m_aryProducts.GetSize();

	for (int i=0; i<nSize; i++)
	{
		INT_PTR nShareSize = m_aryProducts[i].m_strSharedComponents.GetSize();

		for (int j=0; j<nShareSize; j++)
		{
			if (m_aryProducts[i].m_strSharedComponents[j].CompareNoCase(lpctSharedComponent) == 0)
			{
				if (m_aryProducts[i].m_nStatus != M_STATUS_COMPLETED)
				{
					return FALSE;
				}
				else
					break;
			}
		}
	}

	return TRUE;
}

void CUninstallApp::GetUAserviceStatus()
{
	m_bNeedHandleUAService = FALSE;
	INT_PTR nSize = m_aryProducts.GetSize();

	for (INT_PTR i=0; i<nSize; i++)
	{
		if (m_aryProducts[i].m_blSelected && m_aryProducts[i].m_nUAServiceHandle == 1)
		{
			//Check the UA mutex, if exist, UA block D2D files, need stop it.
			HANDLE hMutex = ::OpenMutex(GENERIC_READ, FALSE, MUTEX_UA_SETUP);
			if (!hMutex)
			{
				WriteLog(_T("%s"), _T("UA service is not using D2D. No need handle it."));
				return;
			}
			else
			{
				::CloseHandle(hMutex);
				WriteLog(_T("%s"), _T("UA service is using D2D"));

				if(IsServiceStarted(SERVICE_CASUNIVERSALAGENT))
				{
					WriteLog(_T("The UA service(%s) is running. Need handle it."), SERVICE_CASUNIVERSALAGENT);
					m_bNeedHandleUAService = TRUE;
				}
				else
				{
					WriteLog(_T("The UA service(%s) isn't running. No need handle it."), SERVICE_CASUNIVERSALAGENT);
				}
			}
		}
	}

}

void CUninstallApp::AddMessage(UINT uID, UINT nIconIndex)
{
	CString strMsg;
	strMsg.LoadString(uID);

	if (!strMsg.IsEmpty())
	{
		AddMessage(strMsg, nIconIndex);
	}
}

void CUninstallApp::AddMessage(CString & strMsg, UINT nIconIndex)
{
	if (!strMsg.IsEmpty())
	{
		BOOL bFound = FALSE;
		for (int i=0; i<theApp.m_aryMessages.GetSize(); i++)
		{
			if (theApp.m_aryMessages[i].strMsg.Compare(strMsg) == 0)
			{
				bFound = TRUE;
				break;
			}
		}

		if (!bFound)
		{
			MSG_ITEM msg;
			msg.strMsg = strMsg;
			msg.nIconIndex = nIconIndex;
			theApp.m_aryMessages.Add(msg);
		}
	}
}

void CUninstallApp::RemoveMessage(UINT uID)
{
	CString strMsg;
	strMsg.LoadString(uID);

	if (!strMsg.IsEmpty())
	{
		RemoveMessage(strMsg);
	}
}

void CUninstallApp::RemoveMessage(CString & strMsg)
{
	if (!strMsg.IsEmpty())
	{
		for (int i=0; i<theApp.m_aryMessages.GetSize(); i++)
		{
			if (theApp.m_aryMessages[i].strMsg.Compare(strMsg) == 0)
			{
				theApp.m_aryMessages.RemoveAt(i);
				break;
			}
		}
	}
}


DWORD CUninstallApp::Get64BitOSType()
{
	DWORD dwRet = VER_PLATFORM_X86;
	CString sRegProcessor = _T("HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0");
	HKEY hKey = NULL;
	TCHAR szType[MAX_PATH] = {0};
	DWORD dwSize = MAX_PATH;

	if (ERROR_SUCCESS == ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, sRegProcessor, 0, KEY_READ, &hKey))
	{
		if (::RegQueryValueEx(hKey, _T("Identifier"), NULL, NULL, (LPBYTE)szType, &dwSize) == ERROR_SUCCESS)
		{
			if (_tcsstr(szType, _T("AMD64")) || _tcsstr(szType, _T("EM64T")) || _tcsstr(szType, _T("Intel64")) || _tcsstr(szType, _T("VIA64")))
				dwRet = VER_PLATFORM_X64;
			else if (_tcsstr(szType, _T("ia64")))
				dwRet = VER_PLATFORM_IA64;
		}

		::RegCloseKey(hKey);
	}

	return dwRet;
}

void CUninstallApp::GetDWordArray(LPCTSTR lpctValue, TCHAR cSplitChar, CDWordArray& data)
{
	data.RemoveAll();

	if (NULL == lpctValue)
		return;

	LPCTSTR lptPre, lptNext;
	lptPre = lpctValue;
	lptNext = lpctValue;

	int nSize, nValue;
	CString strTmp;
	TCHAR szTmp[MAX_PATH];
	while (*lptPre != '\0' && (lptNext = _tcschr(lptPre, cSplitChar)) != NULL)
	{
		//Point to the last cSplitChar, like "aa;;;bb", lptNext will point to last ';' before 'b'
		while (*(lptNext+1) == cSplitChar)
			lptNext++;

		//Deal with this case: ";aa;bb"
		nSize = lptNext - lptPre;
		if (nSize == 0)
		{
			lptPre = lptNext + 1;
			continue;
		}

		//Get the sub string
		_tcsncpy_s(szTmp, lptPre, nSize);
		szTmp[nSize] = '\0';
		strTmp = szTmp;

		strTmp.Trim();
		strTmp.Trim(cSplitChar);

		if (!strTmp.IsEmpty())
		{
			nValue = _ttoi(strTmp);
			data.Add(nValue);
		}

		lptPre = lptNext + 1;
	}

	//Deal with this case: "aa;bb;cc"
	if (*lptPre != '\0')
	{
		strTmp = lptPre;
		strTmp.Trim();
		strTmp.Trim(cSplitChar);
		if (!strTmp.IsEmpty())
		{
			nValue = _ttoi(strTmp);
			data.Add(nValue);
		}
	}
}

BOOL CUninstallApp::IsNeedRestartService(LPCTSTR lpctService)
{
	INT_PTR nSize = m_aryProducts.GetSize();

	for (INT_PTR i=0; i<nSize; i++)
	{
		if (m_aryProducts[i].m_blSelected)
		{
			for (INT_PTR j=0; j<m_aryProducts[i].m_strRestartServices.GetSize(); j++)
			{
				if (m_aryProducts[i].m_strRestartServices[j].CompareNoCase(lpctService) == 0)
				{
					return TRUE;
				}
			}
		}
	}

	return FALSE;
}

BOOL CUninstallApp::RestartService(LPCTSTR lpctServie)
{
	if (lpctServie)
	{
		WriteLog(_T("Restart Service: %s"), lpctServie);

		if (DoStopService(lpctServie))
		{
			return DoStartService(lpctServie);
		}
		else
		{
			WriteLog(_T("DoStopService (%s) failed"), lpctServie);
		}
	}

	return FALSE;
}


BOOL CUninstallApp::DoStartService(LPCTSTR lpctServie)
{
	WriteLog(_T("DoStartService (%s) begin"), lpctServie);
	
	SC_HANDLE schSCManager;
	SC_HANDLE schService;

	SERVICE_STATUS_PROCESS ssStatus;
	DWORD dwOldCheckPoint;
	DWORD dwStartTickCount;
	DWORD dwWaitTime;
	DWORD dwBytesNeeded;

	// Get a handle to the SCM database. 

	schSCManager = OpenSCManager(
		NULL,                    // local computer
		NULL,                    // servicesActive database 
		SC_MANAGER_ALL_ACCESS);  // full access rights 

	if (NULL == schSCManager) 
	{
		WriteLog(_T("OpenSCManager failed (%d)"), GetLastError());
		return FALSE;
	}

	// Get a handle to the service.
	schService = OpenService(
		schSCManager,         // SCM database 
		lpctServie,            // name of service 
		SERVICE_ALL_ACCESS);  // full access 

	if (schService == NULL)
	{ 
		WriteLog(_T("OpenService failed (%d)"), GetLastError()); 
		CloseServiceHandle(schSCManager);
		return FALSE;
	}    

	// Check the status in case the service is not stopped. 

	if (!QueryServiceStatusEx(
		schService,                     // handle to service 
		SC_STATUS_PROCESS_INFO,         // information level
		(LPBYTE) &ssStatus,             // address of structure
		sizeof(SERVICE_STATUS_PROCESS), // size of structure
		&dwBytesNeeded))              // size needed if buffer is too small
	{
		WriteLog(_T("QueryServiceStatusEx failed (%d)"), GetLastError());
		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
		return FALSE;
	}

	// Check if the service is already running. It would be possible
	// to stop the service here, but for simplicity this example just returns.
	if (ssStatus.dwCurrentState != SERVICE_STOPPED && ssStatus.dwCurrentState != SERVICE_STOP_PENDING)
	{
		WriteLog(_T("Cannot start the service because it is already running"));
		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
		return TRUE;
	}

	// Wait for the service to stop before attempting to start it.
	while (ssStatus.dwCurrentState == SERVICE_STOP_PENDING)
	{
		// Save the tick count and initial checkpoint.
		dwStartTickCount = GetTickCount();
		dwOldCheckPoint = ssStatus.dwCheckPoint;

		// Do not wait longer than the wait hint. A good interval is 
		// one-tenth of the wait hint but not less than 1 second  
		// and not more than 10 seconds. 

		dwWaitTime = ssStatus.dwWaitHint / 10;

		if (dwWaitTime < 1000)
			dwWaitTime = 1000;
		else if (dwWaitTime > 10000)
			dwWaitTime = 10000;

		Sleep(dwWaitTime);

		// Check the status until the service is no longer stop pending. 

		if (!QueryServiceStatusEx(
			schService,                     // handle to service 
			SC_STATUS_PROCESS_INFO,         // information level
			(LPBYTE) &ssStatus,             // address of structure
			sizeof(SERVICE_STATUS_PROCESS), // size of structure
			&dwBytesNeeded))              // size needed if buffer is too small
		{
			WriteLog(_T("QueryServiceStatusEx failed (%d)"), GetLastError());
			CloseServiceHandle(schService); 
			CloseServiceHandle(schSCManager);
			return FALSE;
		}

		if (ssStatus.dwCheckPoint > dwOldCheckPoint)
		{
			// Continue to wait and check.

			dwStartTickCount = GetTickCount();
			dwOldCheckPoint = ssStatus.dwCheckPoint;
		}
		else
		{
			if (GetTickCount()-dwStartTickCount > ssStatus.dwWaitHint)
			{
				WriteLog(_T("Timeout waiting for service to stop"));
				CloseServiceHandle(schService); 
				CloseServiceHandle(schSCManager);
				return FALSE;
			}
		}
	}

	// Attempt to start the service.

	if (!StartService(
		schService,  // handle to service 
		0,           // number of arguments 
		NULL))      // no arguments 
	{
		WriteLog(_T("StartService failed (%d)"), GetLastError());
		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
		return FALSE;
	}
	else
		WriteLog(_T("Service start pending..."));

	// Check the status until the service is no longer start pending. 

	if (!QueryServiceStatusEx(
		schService,                     // handle to service 
		SC_STATUS_PROCESS_INFO,         // info level
		(LPBYTE) &ssStatus,             // address of structure
		sizeof(SERVICE_STATUS_PROCESS), // size of structure
		&dwBytesNeeded))              // if buffer too small
	{
		WriteLog(_T("QueryServiceStatusEx failed (%d)"), GetLastError());
		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
		return FALSE;
	}

	// Save the tick count and initial checkpoint.

	dwStartTickCount = GetTickCount();
	dwOldCheckPoint = ssStatus.dwCheckPoint;

	while (ssStatus.dwCurrentState == SERVICE_START_PENDING) 
	{ 
		// Do not wait longer than the wait hint. A good interval is 
		// one-tenth the wait hint, but no less than 1 second and no 
		// more than 10 seconds. 

		dwWaitTime = ssStatus.dwWaitHint / 10;

		if (dwWaitTime < 1000)
			dwWaitTime = 1000;
		else if (dwWaitTime > 10000)
			dwWaitTime = 10000;

		Sleep(dwWaitTime);

		// Check the status again. 

		if (!QueryServiceStatusEx(
			schService,             // handle to service 
			SC_STATUS_PROCESS_INFO, // info level
			(LPBYTE) &ssStatus,             // address of structure
			sizeof(SERVICE_STATUS_PROCESS), // size of structure
			&dwBytesNeeded))              // if buffer too small
		{
			WriteLog(_T("QueryServiceStatusEx failed (%d)"), GetLastError());
			break; 
		}

		if (ssStatus.dwCheckPoint > dwOldCheckPoint)
		{
			// Continue to wait and check.

			dwStartTickCount = GetTickCount();
			dwOldCheckPoint = ssStatus.dwCheckPoint;
		}
		else
		{
			if (GetTickCount()-dwStartTickCount > ssStatus.dwWaitHint)
			{
				// No progress made within the wait hint.
				break;
			}
		}
	} 

	// Determine whether the service is running.

	BOOL bRet = FALSE;
	if (ssStatus.dwCurrentState == SERVICE_RUNNING) 
	{
		WriteLog(_T("Service started successfully"));
		bRet = TRUE;
	}
	else 
	{
		WriteLog(_T("Service not started"));
		WriteLog(_T("Current State: %d"), ssStatus.dwCurrentState); 
		WriteLog(_T("Exit Code: %d"), ssStatus.dwWin32ExitCode); 
		WriteLog(_T("Check Point: %d"), ssStatus.dwCheckPoint); 
		WriteLog(_T("Wait Hint: %d"), ssStatus.dwWaitHint);
	}

	CloseServiceHandle(schService); 
	CloseServiceHandle(schSCManager);

	return bRet;
}

BOOL CUninstallApp::DoStopService(LPCTSTR lpctServie)
{
	WriteLog(_T("DoStopService (%s) begin"), lpctServie);

	BOOL bRet = FALSE;
	SC_HANDLE schSCManager;
	SC_HANDLE schService;

	SERVICE_STATUS_PROCESS ssp;
	DWORD dwStartTime = GetTickCount();
	DWORD dwBytesNeeded;
	DWORD dwTimeout = 30000; // 30-second time-out

	// Get a handle to the SCM database. 

	schSCManager = OpenSCManager(
		NULL,                    // local computer
		NULL,                    // ServicesActive database 
		SC_MANAGER_ALL_ACCESS);  // full access rights 

	if (NULL == schSCManager) 
	{
		WriteLog(_T("OpenSCManager failed (%d)"), GetLastError());
		return FALSE;
	}

	// Get a handle to the service.

	schService = OpenService(
		schSCManager,         // SCM database 
		lpctServie,            // name of service 
		SERVICE_STOP | 
		SERVICE_QUERY_STATUS | 
		SERVICE_ENUMERATE_DEPENDENTS);  

	if (schService == NULL)
	{ 
		WriteLog(_T("OpenService failed (%d)"), GetLastError()); 
		CloseServiceHandle(schSCManager);
		return FALSE;
	}    

	// Make sure the service is not already stopped.

	if (!QueryServiceStatusEx(
		schService, 
		SC_STATUS_PROCESS_INFO,
		(LPBYTE)&ssp, 
		sizeof(SERVICE_STATUS_PROCESS),
		&dwBytesNeeded))
	{
		WriteLog(_T("QueryServiceStatusEx failed (%d)"), GetLastError()); 
		goto stop_cleanup;
	}

	if (ssp.dwCurrentState == SERVICE_STOPPED)
	{
		WriteLog(_T("Service is already stopped"));
		goto stop_cleanup;
	}

	// If a stop is pending, wait for it.

	while (ssp.dwCurrentState == SERVICE_STOP_PENDING) 
	{
		WriteLog(_T("Service stop pending..."));
		Sleep(ssp.dwWaitHint);
		if (!QueryServiceStatusEx(
			schService, 
			SC_STATUS_PROCESS_INFO,
			(LPBYTE)&ssp, 
			sizeof(SERVICE_STATUS_PROCESS),
			&dwBytesNeeded))
		{
			WriteLog(_T("QueryServiceStatusEx failed (%d)"), GetLastError());
			goto stop_cleanup;
		}

		if (ssp.dwCurrentState == SERVICE_STOPPED)
		{
			WriteLog(_T("Service stopped successfully"));
			goto stop_cleanup;
		}

		if (GetTickCount() - dwStartTime > dwTimeout)
		{
			WriteLog(_T("Service stop timed out"));
			goto stop_cleanup;
		}
	}

	// If the service is running, dependencies must be stopped first.
	//StopDependentServices()

	// Send a stop code to the service.
	if (!ControlService(
		schService, 
		SERVICE_CONTROL_STOP, 
		(LPSERVICE_STATUS) &ssp))
	{
		WriteLog(_T("ControlService failed (%d)"), GetLastError());
		goto stop_cleanup;
	}

	// Wait for the service to stop.

	while (ssp.dwCurrentState != SERVICE_STOPPED) 
	{
		Sleep(ssp.dwWaitHint);
		if (!QueryServiceStatusEx(
			schService, 
			SC_STATUS_PROCESS_INFO,
			(LPBYTE)&ssp, 
			sizeof(SERVICE_STATUS_PROCESS),
			&dwBytesNeeded))
		{
			WriteLog(_T("QueryServiceStatusEx failed (%d)"), GetLastError());
			goto stop_cleanup;
		}

		if (ssp.dwCurrentState == SERVICE_STOPPED)
		{
			bRet = TRUE;
			break;
		}

		if (GetTickCount() - dwStartTime > dwTimeout)
		{
			WriteLog(_T("Wait timed out"));
			goto stop_cleanup;
		}
	}

	WriteLog(_T("Service stopped successfully"));

stop_cleanup:
	CloseServiceHandle(schService); 
	CloseServiceHandle(schSCManager);

	return bRet;
}

BOOL CUninstallApp::IsProductSelected(const CString &strShortName)
{
	INT_PTR nSize = m_aryProducts.GetSize();
	
	if(strShortName.IsEmpty())
	{
		return FALSE;
	}

	for (int i=0; i<nSize; i++)
	{
		if(m_aryProducts[i].m_strShortName.CompareNoCase(strShortName) == 0 && m_aryProducts[i].m_blSelected)
		{
			//the depended product is selected.
			return TRUE;
		}
	}

	return FALSE;
}

CString CUninstallApp::GetProductName(const CString &strShortName)
{
	INT_PTR nSize = m_aryProducts.GetSize();
	
	if(strShortName.IsEmpty())
	{
		return _T("");
	}

	for (int i=0; i<nSize; i++)
	{
		if(m_aryProducts[i].m_strShortName.CompareNoCase(strShortName) == 0)
		{
			//the depended product is selected.
			return m_aryProducts[i].m_strProductName;
		}
	}

	return _T("");
}

void CUninstallApp::DoProductSelectedWithDepend()
{
	CString strDependedProdct;
	INT_PTR nSize = theApp.m_aryProducts.GetSize();
	
	for (int i=0; i<nSize; i++)
	{
		for (int j=0; j<m_aryProducts.GetAt(i).m_strDependentFeatures.GetSize(); j++)
		{
			strDependedProdct = m_aryProducts.GetAt(i).m_strDependentFeatures[j];
			if(!strDependedProdct.IsEmpty())
			{
				if(IsProductSelected(strDependedProdct))
				{
					//if the depended product is selected, the related product depened on it will be selected for un-installation
					m_aryProducts.GetAt(i).m_blSelected = TRUE;

					WriteLog(_T("DoProductSelectedWithDepend: The depended product(%s) is selected to uninstall, so the related product(%s) depended on it will be selected automatically to uninstall."),GetProductName(strDependedProdct),m_aryProducts.GetAt(i).m_strProductName);
				}
			}
		}
	}
}

BOOL CUninstallApp::IsNeedRemoveSharedComponent(LPCTSTR lpctSharedComponent)
{
	INT_PTR nSize = m_aryProducts.GetSize();

	for (int i=0; i<nSize; i++)
	{
		INT_PTR nShareSize = theApp.m_aryProducts[i].m_strSharedComponents.GetSize();

		for (int j=0; j<nShareSize; j++)
		{
			if (theApp.m_aryProducts[i].m_strSharedComponents[j].CompareNoCase(lpctSharedComponent) == 0)
			{
				//if there is any product that is using the share component, but it is not uninstalled and it is not selected, we could not remove the share component.
				if (theApp.m_aryProducts[i].m_nStatus != M_STATUS_COMPLETED && !theApp.m_aryProducts[i].m_blSelected)
				{
					return FALSE;
				}
			}
		}
	}

	return TRUE;
}

BOOL CUninstallApp::MakeSurePathExists(LPCTSTR lpctPath, BOOL FilenameIncluded)
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

void CUninstallApp::InitLogFileName(const CString &strLogPath)
{
	TCHAR strTemp[MAX_PATH] = {0};
	TCHAR strHistoryTime[MAX_PATH] = {0};
	TCHAR strRootPath[MAX_PATH] = {0};

	struct tm newTime;
	__time64_t long_time;

	// Get time as 64-bit integer.
	_time64(&long_time); 
	// Convert to local time.
	_localtime64_s(&newTime, &long_time);
	BOOL bRes = FALSE;

	_stprintf_s(strHistoryTime,_countof(strHistoryTime),_T("%4d_%02d_%02d %02d:%02d:%02d"),
			newTime.tm_year+1900,newTime.tm_mon+1, newTime.tm_mday, newTime.tm_hour, 
			newTime.tm_min, newTime.tm_sec);

	if(!strLogPath.IsEmpty())
	{
		_stprintf_s(strRootPath,_countof(strRootPath),_T("%s"),strLogPath);
		_stprintf_s(strTemp,_countof(strTemp),_T("%s"),strLogPath);

		bRes = (_taccess(strTemp,0) != -1);

		if(!bRes)
		{
			//don't exist, create it
			bRes = MakeSurePathExists(strTemp, FALSE);
		}
	}

	if(!bRes)
	{
		::GetSystemWindowsDirectory(strRootPath, _countof(strRootPath));
		::PathAppend(strRootPath, _T("Temp\\Arcserve\\Setup\\UDP\\Uninstall\\"));
		MakeSurePathExists(strRootPath, FALSE);

		_stprintf_s(strTemp,_countof(strTemp),_T("%s"),strRootPath);

		//when the input path is empty, we need output the log to the path with time.
		m_strLogPathWithTime.Format(_T("%sUninstall_%4d_%02d_%02d_%02d_%02d_%02d\\"),strTemp,
			newTime.tm_year+1900,newTime.tm_mon+1, newTime.tm_mday, newTime.tm_hour, 
			newTime.tm_min, newTime.tm_sec);

		MakeSurePathExists(m_strLogPathWithTime, FALSE);

	}
	else
	{
		PathAddBackslash(strTemp);

		//when the input path is not empty, we need output the log to the input path.
		m_strLogPathWithTime = strTemp;
	}

	m_strLogPath = strTemp;
	
	PathAddBackslash(strRootPath);

	if(IsRollBack())
	{  
		//this is for silentinstall.exe
		m_strLogFile.Format(_T("%s%s_%04d%02d%s%s"), m_strLogPath,LOG_FILE_NAME_PREFIX,newTime.tm_year+1900, newTime.tm_mon+1,FILE_ROLLBACK,LOG_FILE_EXT);
		m_strLogSetupFile.Format(_T("%s%s%s"), strRootPath,LOG_UINSTALL_FILE_NAME_PREFIX,LOG_FILE_EXT);
		m_strUinstallStatusFile.Format(_T("%sUninstall%s.ini"),strRootPath,FILE_ROLLBACK);
	}
	else
	{
		//this is for uninstall.exe
		m_strLogFile.Format(_T("%s%s%s"), m_strLogPathWithTime,LOG_FILE_NAME_PREFIX,LOG_FILE_EXT);
		m_strLogSetupFile.Format(_T("%s%s%s"), strRootPath,LOG_UINSTALL_FILE_NAME_PREFIX,LOG_FILE_EXT);

		//write the history
		if(m_bWriteHistory)
		{
			//only write one time
			m_bWriteHistory = FALSE;
			theApp.SetupWritePrivateProfileString(_T("History"),strHistoryTime, m_strLogPathWithTime, m_strLogSetupFile);
		}

		m_strUinstallStatusFile.Format(_T("%sUninstall.ini"),m_strLogPathWithTime);
	}


	//remove the previous ini file
	if(_taccess(theApp.m_strUinstallStatusFile,0) == 0)
	{
		DeleteFile(theApp.m_strUinstallStatusFile);
	}

	TRACE(_T("Log file is : %s\n"), m_strLogFile);
}

UINT CUninstallApp::GetSplitDataList(const CString  &strListData, const CString& strSpliter, CStringArray& strArray)
{
	if(strListData.IsEmpty() || strSpliter.IsEmpty())
	{
		return 0;
	}

	CString strData = strListData;
	CString strTemp;

	BOOL bLoop = TRUE;
	int nIndex = -1;
	while(bLoop)
	{
		nIndex = strData.Find(strSpliter);
		if(nIndex != -1)
		{
			strTemp = strData.Left(nIndex);
			if(!strTemp.IsEmpty())
			{
				strArray.Add(strTemp);
			}

			if(nIndex == strData.GetLength())
			{
				// no data
				bLoop = FALSE;
				break;
			}

			strTemp = strData.Right(strData.GetLength() - nIndex-1);
			strData = strTemp;
		}
		else if(!strData.IsEmpty())
		{
			strArray.Add(strData);
			break;
		}
		else
		{
			bLoop = FALSE;
			break;
		}
	}

	return (UINT)strArray.GetCount();
}

//check if another windows installer is running
BOOL CUninstallApp::IsMSIInstallerRunning()
{

	REGSAM samDesired = KEY_READ;

	if (Is64BitOS())
	{
		samDesired |= KEY_WOW64_64KEY;
	}

	HKEY hKey = NULL;

	if (ERROR_SUCCESS == ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, REGKEY_MSI_INSTALLER_RUNNING, 0, samDesired, &hKey))
	{
		::RegCloseKey(hKey);
		return TRUE;
	}
	return FALSE;
}

//retry to get/set the data for GetPrivateProfile/SetPrivateProfile(BAOF probaly lock the inf file,so use this function to fix the problem)
DWORD CUninstallApp::SetupGetPrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpDefault, LPTSTR lpReturnedString, DWORD nSize, LPCTSTR lpFileName)
{
	DWORD dwRet = ERROR_SUCCESS;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT_UNINSTALL; i++)
	{
		dwRet = ::GetPrivateProfileString(lpAppName, 
			lpKeyName, 
			lpDefault,
			lpReturnedString,
			nSize,
			lpFileName);

		if (ERROR_SHARING_VIOLATION == GetLastError())
		{
			WriteLog(_T("GetPrivateProfileString(%s, %s,...%s) failed. Err=%d"), lpAppName, lpKeyName, lpFileName, ERROR_SHARING_VIOLATION);
			Sleep(1000);
		}
		else
		{
			break;
		}
	}

	return dwRet;
}

BOOL CUninstallApp::SetupWritePrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpString, LPCTSTR lpFileName)
{
	BOOL bRet = FALSE;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT_UNINSTALL; i++)
	{
		bRet = ::WritePrivateProfileString(lpAppName, lpKeyName, lpString, lpFileName);

		 if (ERROR_SHARING_VIOLATION == GetLastError())
		 {
			 WriteLog(_T("WritePrivateProfileString(%s, %s,...%s) failed. Err=%d"), lpAppName, lpKeyName, lpFileName, ERROR_SHARING_VIOLATION);
			 Sleep(1000);
		 }
		 else
		 {
			 break;
		 }
	}

	if (!bRet)
	{
		WriteLog(_T("WritePrivateProfileString(%s %s %s %s) failed with error %d"),lpAppName, lpKeyName, lpString, lpFileName, ::GetLastError());
	}

	return bRet;
}

UINT CUninstallApp::SetupGetPrivateProfileInt(LPCTSTR lpAppName, LPCTSTR lpKeyName, INT nDefault, LPCTSTR lpFileName)
{
	UINT nRet = 0;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT_UNINSTALL; i++)
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

DWORD CUninstallApp::SetupGetPrivateProfileSection(LPCTSTR lpAppName,LPTSTR lpReturnedString,DWORD nSize,LPCTSTR lpFileName)
{
	DWORD dwRet = 0;

	for (int i = 0; i < FILE_ACCESS_RETRY_COUNT_UNINSTALL; i++)
	{
		dwRet = ::GetPrivateProfileSection(lpAppName, 
			lpReturnedString, 
			nSize,
			lpFileName);

		if (ERROR_SHARING_VIOLATION == GetLastError())
		{
			WriteLog(_T("SetupGetPrivateProfileSection(%s,...%s) failed. Err=%d"), lpAppName, lpFileName, ERROR_SHARING_VIOLATION);
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

BOOL CUninstallApp::IsDiskSpaceOK()
{
	//get the system temp for log file
	DWORD dwRet = 0;
	TCHAR szSysTempDir[MAX_PATH] = {0};
	DWORD64 dwFreeDiskSizeWithMB = 0;
	CString strDrive;
	
	//get the disk space from configuration file
	int nMimiDsikValue = SetupGetPrivateProfileInt(_T("uninstall"),
		_T("MinSysFreeDiskSpace"),
		MIN_DISK_SIZE,
		m_strInifile);

	if(nMimiDsikValue == 0)
	{
		WriteLog(_T("The disk space check configuration value is 0, no need check disk space."));
		return TRUE;
	}
	else if (nMimiDsikValue < 0 || nMimiDsikValue > 1024)
	{
		nMimiDsikValue = MIN_DISK_SIZE;
		WriteLog(_T("The disk space check configuration value is < 0 or > 1024 MB, use the default value %d MB as the minimum free disk space check."),nMimiDsikValue);
	}
	//end

	::GetSystemWindowsDirectory(szSysTempDir, _countof(szSysTempDir));
	strDrive = szSysTempDir;
	//get the drive e.g c:
	strDrive = strDrive.Left(2);

	dwRet = ValidateDiskSpace(szSysTempDir,nMimiDsikValue,dwFreeDiskSizeWithMB);

	if(ERROR_SUCCESS != dwRet)
	{
		CString strTitle,strMsg;
		WriteLog(_T("There is an insufficient amount of free disk space to run the uninstall process on drive (%s). Need at least %d MB free disk space. the current free disk space is %d MB or so."),strDrive,nMimiDsikValue,dwFreeDiskSizeWithMB);
		
		strMsg.Format(IDS_SYS_NOT_ENOUGH_SPACE,strDrive,nMimiDsikValue);
		
		WriteLog(strMsg);
	   
		if(!m_blSilent)
		{
			strTitle.LoadString(IDS_STRING_APPLICATION_TITLE);
			MessageBox(NULL,strMsg,strTitle,MB_OK|MB_ICONSTOP|MB_TOPMOST);
		}

		m_dwReturnCode = ERROR_DISK_FULL;
		return FALSE;
	}
	else
	{
		if(dwFreeDiskSizeWithMB > BASE_MB)
		{
			WriteLog(_T("The current free disk space of drive(%s) is %d TB or so."),strDrive,(dwFreeDiskSizeWithMB/BASE_MB));
		}
		else if(dwFreeDiskSizeWithMB > 1024)
		{
			WriteLog(_T("The current free disk space of drive(%s) is %d GB or so."),strDrive,(dwFreeDiskSizeWithMB/1024));
		}
		else
		{
			WriteLog(_T("The current free disk space of drive(%s) is %d MB or so."),strDrive,dwFreeDiskSizeWithMB);
		}
	}
	//end check disk space

	return TRUE;
}

//this is for product name localization, because MSI only support a laugang. So we need get the localizatoin product name from uninstall RC dll if exists
//ProductNameKey is in uninstall.ini like "NewProductName=PRODUCTNAME_AGENT"

CString CUninstallApp::GetNewProductName(CString strProductNameKey)
{
	CString strRet = _T("");
	strProductNameKey.Trim();
	if(strProductNameKey.IsEmpty())
	{
		return strRet;
	}

	if(strProductNameKey.CompareNoCase(_T("PRODUCTNAME_AGENT")) == 0)
	{
		//UDP Agent
		strRet.LoadString(IDS_STRING_PRODUCTNAME_AGENT);
	}
	else if(strProductNameKey.CompareNoCase(_T("PRODUCTNAME_SERVER")) == 0)
	{
		//UDP RPS
		strRet.LoadString(IDS_STRING_PRODUCTNAME_SERVER);
	}
	else if(strProductNameKey.CompareNoCase(_T("PRODUCTNAME_CONSOLE")) == 0)
	{
		//UDP Console
		strRet.LoadString(IDS_STRING_PRODUCTNAME_CONSOLE);
	}
	else if (strProductNameKey.CompareNoCase(_T("PRODUCTNAME_GATEWAY")) == 0)
	{
		//UDP Gateway
		strRet.LoadString(IDS_STRING_PRODUCTNAME_GATEWAY);
	}

	return strRet;
}

void CUninstallApp::InitUDPRootPath()
{
	LPCTSTR lpctSubKey = _T("SOFTWARE\\Arcserve\\Unified Data Protection\\");

	if (GetRegValue(HKEY_LOCAL_MACHINE, lpctSubKey, _T("RootPath"), m_strUDPRoot, TRUE))
		m_strUDPRoot.TrimRight(_T("\\"));
}

void CUninstallApp::DeleteEmptyDirectories(LPCTSTR lpctDir)
{
	TCHAR szDir[MAX_PATH];
	_tcscpy_s(szDir, _countof(szDir), lpctDir);
	::PathAddBackslash(szDir);

	if (_taccess(szDir, 0) != 0)
	{
		WriteLog(_T("The directory %s does not exist"), szDir);
		return;
	}

	BOOL bContinue = TRUE;

	while (bContinue)
	{
		SetFileAttributes(szDir, FILE_ATTRIBUTE_NORMAL);

		if (!::RemoveDirectory(szDir))
		{
			DWORD dwError = ::GetLastError();

			if (dwError == ERROR_FILE_NOT_FOUND)
			{
				WriteLog(_T("The system cannot find the file specified [%s]."), szDir);
				bContinue = TRUE;
			}
			else if (dwError == ERROR_DIR_NOT_EMPTY)
			{
				WriteLog(_T("The directory [%s] is not empty."), szDir);
				bContinue = FALSE;
				break;
			}
			else
			{
				WriteLog(_T("Failed to delete the directory %s. Error=%d"), szDir, dwError);
				bContinue = FALSE;
				break;
			}
		}
		else
		{
			WriteLog(_T("Successfully delete the directory %s"), szDir);
		}

		if (bContinue)
		{
			::PathRemoveBackslash(szDir);
			::PathRemoveFileSpec(szDir);
		}
	}
}


