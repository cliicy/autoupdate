// ComAPI.h : main header file for the ComAPI
//

#pragma once

#include <Msi.h>			// must be in this directory or on INCLUDE path
#include <MsiQuery.h>		// must be in this directory or on INCLUDE path
#include <winsvc.h>
#include <shlwapi.h>
#include "ADRDefine.h"

extern TCHAR g_szLogPath[MAX_PATH];
extern TCHAR g_szLogFile[MAX_PATH];

// return code 
#define ERROR_NOADMINISTRATOR			0x1001	
#define ERROR_CANNOTINSTALLASM			0x1002
#define ERROR_CANNOTUPGRADE				0x1003
#define ERROR_RENAMEDB					0x1004
#define	ERROR_JOBRUNNING				0x1005
#define	ERROR_SERVICERUNNUNG			0x1006
#define	ERROR_CREATEJOBQ				0x1007
#define ERROR_INSTALLCALICENSE			0x1008
#define ERROR_REMOVE_COMPONENTS			0x1009
#define ERROR_CREATE_SYSACCOUNT			0x1010
#define ERROR_CANNOTCONTINUE			0x1011
#define ERROR_COMMON					0x1012

//Start for log
//define log file
#define LOG_FILE_DEFAULT_PREFIX         _T("AS_Setup")
#define LOG_FILE_SETUPCOMMON_PREFIX     _T("AS_SetupCommon")
#define LOG_FILE_D2D_PREFIX				 _T("AS_Agent")
#define LOG_FILE_RPS_PREFIX		        _T("AS_RPS")

#define LOG_FILE_EXT_LOG   _T(".log")
//end define log file

enum LOG_TYPE
{
	LOG_INFO=0,
	LOG_WARNING,
	LOG_ERROR
};


#define PARAMETER_D2D  _T("D2D")
#define PARAMETER_RPS    _T("RPS")


#define LONG_PATH_PRE	L"\\\\?\\"

typedef DWORD (WINAPI *PGetProcessImageFileName)(HANDLE hProcess,LPTSTR lpImageFileName,DWORD nSize);

#define DLL_KERNEL32			_T("kernel32.dll")

#ifndef PRODUCT_DATACENTER_SERVER_CORE 
#define PRODUCT_DATACENTER_SERVER_CORE	0x0000000C
#endif

#ifndef PRODUCT_STANDARD_SERVER_CORE 
#define PRODUCT_STANDARD_SERVER_CORE	0x0000000D
#endif

#ifndef PRODUCT_ENTERPRISE_SERVER_CORE 
#define PRODUCT_ENTERPRISE_SERVER_CORE	0x0000000E
#endif

#ifndef PRODUCT_DATACENTER_SERVER_CORE_V
#define PRODUCT_DATACENTER_SERVER_CORE_V 0x00000027
#endif

#ifndef PRODUCT_STANDARD_SERVER_CORE_V
#define PRODUCT_STANDARD_SERVER_CORE_V 0x00000028
#endif

#ifndef PRODUCT_ENTERPRISE_SERVER_CORE_V
#define PRODUCT_ENTERPRISE_SERVER_CORE_V 0x00000029
#endif

#ifndef PRODUCT_WEB_SERVER_CORE
#define PRODUCT_WEB_SERVER_CORE 0x0000001D
#endif


//end for log

typedef LONG (WINAPI  *PFRegDelEx) (__in HKEY hKey,__in LPCTSTR lpSubKey,__in REGSAM samDesired,__reserved DWORD Reserved);


//***********************************************************//
//***          please add non-custom action	here   	       **//
//***********************************************************//

#define __WFUNCTION__           TO_UNICODE(__FUNCTION__)

#ifdef UNICODE
#define __TFUNCTION__		__WFUNCTION__
#else
#define __TFUNCTION__		__FUNCTION__
#endif

#define LOG_FUNCTION_BEGIN(hMsi,szLogFile) \
	SendErrorMessageToMSI(hMsi,szLogFile, LOG_INFO, _T("%s begin.\n"), __TFUNCTION__);

#define LOG_FUNCTION_END(hMsi,szLogFile) \
	SendErrorMessageToMSI(hMsi,szLogFile, LOG_INFO, _T("%s end.\n"), __TFUNCTION__);


//write the log
DWORD SendErrorMessageToMSI(MSIHANDLE hInstall,LPCTSTR pszLogFileName,LOG_TYPE type,TCHAR *format,...);

DWORD LaunchProcess(TCHAR *pCmdLine, TCHAR *pWorkingDir, DWORD *dwExitCode,DWORD dwTime=INFINITE,DWORD dwCreationFlags=CREATE_NO_WINDOW);

BOOL Is_64Bit_System(LPTSTR lpMachine = NULL);

BOOL IsLocalMachine(LPCTSTR lpMachine);

BOOL IsServiceExist(LPCTSTR lpszServiceName);

BOOL IsSilentMode(MSIHANDLE hInstall);

BOOL IsServerCoreOS();

void ChangeShortCutFile(LPCTSTR lpctNumber, LPCTSTR lpctShortCutFile);

BOOL MakeSurePathExists(LPCTSTR lpctPath, BOOL FilenameIncluded);

BOOL InitLogPath(MSIHANDLE hInstall);

DWORD CreateDirectoryRecursively(LPCTSTR lpPath);

BOOL CopyFilesRecursive(MSIHANDLE hInstall,LPCTSTR lpszSrcPath, LPCTSTR lpszDesPath);

BOOL RegKeyExist(HKEY hKey, LPCTSTR lpSubKey);

//get the registry value
BOOL GetRegValue(HKEY hKey, LPCTSTR lpSubKey, LPCTSTR lpValueName, CString & strReturnValue,DWORD dwType=REG_SZ);

//set the registry value
BOOL SetRegValue(HKEY hKey, LPCTSTR lpSubKey, LPCTSTR lpValueName, LPCTSTR lpValue, DWORD dwType=REG_SZ);

BOOL DeleteTree(LPCTSTR lpszPath, LPCTSTR pszLogFileName = NULL);

BOOL IsEmptyKey(LPCTSTR lpKey);

BOOL RemoveRegistry(MSIHANDLE hInstall,LPCTSTR pszLogFileName,HKEY hKeyParent, LPCTSTR lpszKeyChild);

//only remove input empty key, not Recursive
BOOL RemoveEmptyRegistry(MSIHANDLE hInstall,LPCTSTR pszLogFileName,HKEY hKeyParent, LPCTSTR lpszKey);

LONG RecursiveDeleteKey(MSIHANDLE hInstall,LPCTSTR pszLogFileName,HKEY hKeyParent, LPCTSTR lpszKeyChild, BOOL bNative);

BOOL GetProgramFilesFolder(MSIHANDLE hInstall,BOOL bIsLocal, LPCTSTR lpMachine, LPTSTR lpPath, DWORD dwSize,LPCTSTR pszLogFileName,BOOL bGet32On64OS = FALSE);

BOOL GetSetupCommonPath(MSIHANDLE hInstall,CString& strPath,LPCTSTR pszLogFileName);

//for service
BOOL  StartSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation);
DWORD StopSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation, BOOL bStopDependents);
BOOL  IsServiceStopped(LPCTSTR lpszServiceName);
BOOL  IsServiceStarted(LPCTSTR lpszServiceName);
BOOL  GetServiceFileName(LPCTSTR lpszServiceName, LPTSTR lpServiceFile, DWORD ccBuffer);
BOOL  GetServiceDisplayName(LPCTSTR lpszServiceName, LPTSTR lpServiceDisplayName, DWORD ccBuffer);
BOOL  ChangeSvcStartType(LPCTSTR lpszServiceName, const DWORD dwStartType);
BOOL  IsServiceExist(LPCTSTR lpszServiceName);
BOOL  ChangeServiceDisplayName(LPCTSTR lpctServiceName, LPCTSTR lpctDisplayName);
BOOL  ChangeServiceDescription(LPCTSTR lpctServiceName, LPCTSTR lpctDescription);
BOOL  ChangeSvcFailureActions(LPCTSTR lpszServiceName, UINT uFirstAction, UINT uSecondAction, UINT uThirdAction, DWORD dwDelay);
//end for service

DWORD HandleApplicationService(MSIHANDLE hInstall,LPCTSTR lpszServiceName,LPCTSTR pszLogFileName);

//handle the service with flag, this is for uninstallation
DWORD StopServiceForUninstall(MSIHANDLE hInstall,LPCTSTR lpszServiceName,LPCTSTR pszLogFileName);
BOOL StartServiceForUninstall(MSIHANDLE hInstall,LPCTSTR lpszServiceName,LPCTSTR pszLogFileName);
//end

//get the product name according to product code
CString GetProductName(LPCTSTR lpProductCode);

//get the product name according to the property "ProductName"
CString GetProductName(MSIHANDLE hInstall);

HINSTANCE GetRC(MSIHANDLE hInstall,LPCTSTR pszLogFileName);

//remove the unused tray icon
void RemoveDeadIcons(MSIHANDLE hInstall,LPCTSTR pszLogFileName);

DWORD CallRegSvr32( MSIHANDLE hInstall, LPCTSTR lpctDLLPath, BOOL bUninstall,LPCTSTR pszLogFileName);

// this function get the .NET Framework install folder
BOOL GetNET20InstallFolder(LPTSTR szValue, DWORD ccBuffer);

BOOL RegisterNETDLL(MSIHANDLE hInstall, LPCTSTR szFileName, BOOL bUninst,LPCTSTR pszLogFileName);

BOOL TrimTailBackSlash(CString &sPath);

BOOL AppendBackSlash(CString &sPath);

BOOL  GetFilePath(LPCTSTR lpFullFileName,LPTSTR lpPath,DWORD ccSize);

BOOL DeleteTreeEx(MSIHANDLE hInstall,LPCTSTR lpszPath,LPCTSTR pszLogFileName);

//delete the empty folder Recursively
int DeleteEmptyDirectory(MSIHANDLE hInstall,const CString& strDir,LPCTSTR pszLogFileName);

/*
this function will replace the following list
- KEY_PR_DISPLAYNAME
- KEY_PR_PRODUCT_OFFICIAL_NAME
*/
CString GetOEMString(MSIHANDLE hInstall,const CString& strSource,LPCTSTR pszLogFileName);


//hide the cancel buttong on windows installer dialog for un-installation.
DWORD HideCancelButton(MSIHANDLE hInstall);

//retry to get/set the data for GetPrivateProfile/SetPrivateProfile(BAOF probaly lock the inf file,so use this function to fix the problem)
DWORD SetupGetPrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpDefault, LPTSTR lpReturnedString, DWORD nSize, LPCTSTR lpFileName);
BOOL SetupWritePrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpString, LPCTSTR lpFileName);
UINT SetupGetPrivateProfileInt(LPCTSTR lpAppName, LPCTSTR lpKeyName, INT nDefault, LPCTSTR lpFileName);
DWORD SetupGetPrivateProfileSection(LPCTSTR lpAppName,LPTSTR lpReturnedString,DWORD nSize,LPCTSTR lpFileName);
//end for retry to get/set the data for GetPrivateProfile/SetPrivateProfile

BOOL GetLogDir(MSIHANDLE hInstall,LPTSTR lptValue, size_t size);

DWORD CopyFileUltra( LPCTSTR lpExistingFileName,LPCTSTR lpNewFileName,BOOL bFailIfExists);

void ChangeFileAttributes(const TCHAR* szFileName);

// get the user profile paths for all the users
void AddUserProfilesFolder(CStringArray& strArray);

//create the reboot flag file under system temp like "c:\wiondows\temp\as_reboot_d2d.ini"
BOOL SetRebootFlagFile(MSIHANDLE hInstall,LPCTSTR pszLogFileName);

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////  Process Functions  //////////////////////////////////////////////////////////

//for process
#define MAX_BUF             1024
#define MAX_TASKS           256
#define TITLE_SIZE          64
#define PROCESS_SIZE        MAX_PATH

//
// task list structure
//

typedef struct _TASK_LIST
{
	DWORD       dwProcessId;
	DWORD       dwInheritedFromProcessId;
	BOOL        flags;
	HANDLE      hwnd;
	TCHAR       ProcessName[PROCESS_SIZE];
	TCHAR       WindowTitle[TITLE_SIZE];
	TCHAR		szExePath[MAX_PATH];
} TASK_LIST, *PTASK_LIST;

typedef struct _TASK_LIST_ENUM
{
	PTASK_LIST  tlist;
	DWORD       numtasks;
} TASK_LIST_ENUM, *PTASK_LIST_ENUM;

//API
//szKillProc is process name(e.g javaw.exe), szFilePath is full path(e.g c:\system\javaw.exe)
BOOL KillProcesses(LPTSTR szKillProc, LPTSTR szFilePath = NULL);
BOOL KillProcessesEx(LPTSTR szKillProc, LPTSTR szFilePath = NULL,LPCTSTR pszLogFileName = NULL);
UINT IsProcessRunning(LPTSTR szKillProc, LPTSTR szFilePath = NULL, BOOL bOutputAllProcListTolog = FALSE);

//////////////////////////  End Process Functions  /////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////



//***********************************************************//
//***          end add non-custom action  	               **//
//***********************************************************//


