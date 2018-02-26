// ComAPI.h : main header file for the ComAPI
//

#pragma once

#include <Msi.h>			// must be in this directory or on INCLUDE path
#include <MsiQuery.h>		// must be in this directory or on INCLUDE path
#include <winsvc.h>
#include <shlwapi.h>
#include "EdgeSetupDefine.h"

extern TCHAR g_szLogPath[MAX_PATH];
extern TCHAR g_szLogFile[MAX_PATH];

#define PARAMETER_WEBSERVER  _T("Webserver")
#define PARAMETER_CM		 _T("CM")
#define PARAMETER_VCM		 _T("VCM")
#define PARAMETER_REPORT     _T("Report")
#define PARAMETER_VSPHERE    _T("vSphere")

#define EDGE_REGKEY_MICROSOFT_WINDOWS		_T("SOFTWARE\\Microsoft\\Windows\\CurrentVersion")
#define EDGE_REGVALUE_PROGRAM_FILES_DIR		_T("ProgramFilesDir")
#define EDGE_REGVALUE_PROGRAM_FILES_DIR_X86	_T("ProgramFilesDir (x86)")

#define PATH_UDP_UNINSTALL						DEFAULT_UDP_LOG_SUBPATH _T("\\Uninstall\\")

//Start for log
//define log file
#define LOG_FILE_DEFAULT_PREFIX          _T("AS_Setup")
#define LOG_FILE_SETUPCOMMON_PREFIX 	 _T("AS_SetupCommon")
#define LOG_FILE_CM_PREFIX 				 _T("AS_CM")
#define LOG_FILE_VCM_PREFIX 			 _T("AS_VCM")
#define LOG_FILE_VSPHERE_PREFIX 		 _T("AS_VSphere")
#define LOG_FILE_WEBSERVER_PREFIX 		 _T("AS_WebServer")
#define LOG_FILE_GATEWAY_PREFIX 		 _T("AS_Gateway")

#define LOG_FILE_EXT_LOG   _T(".log")
//end define log file

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
DWORD SendErrorMessageToMSI(MSIHANDLE hInstall,const TCHAR* pszLogFileName,LOG_TYPE type,TCHAR *format,...);

DWORD LaunchProcess(TCHAR *pCmdLine, TCHAR *pWorkingDir, DWORD *dwExitCode,DWORD dwTime=INFINITE,DWORD dwCreationFlags=CREATE_NO_WINDOW);

BOOL Is_64Bit_System(LPTSTR lpMachine = NULL);

BOOL IsServiceExist(LPCTSTR lpszServiceName);

BOOL IsSilentMode(MSIHANDLE hInstall);

//get the AsetupRes.dll (it is x86 dll)
HMODULE GetSetupRC(MSIHANDLE hInstall,const TCHAR* pszLogFileName);

//csSize is characters size, e.g _countof()
BOOL GetSetupResIni(MSIHANDLE hInstall,const TCHAR* pszLogFileName,TCHAR* szSetupResIniFile,DWORD csSize);

void FreeSetupRC(HINSTANCE hModule);

void ChangeShortCutFile(LPCTSTR lpctNumber, LPCTSTR lpctShortCutFile);

BOOL MakeSurePathExists(LPCTSTR lpctPath, BOOL FilenameIncluded);

BOOL InitLogPath(MSIHANDLE hInstall);

DWORD CreateDirectoryRecursively(LPCTSTR lpPath);

BOOL CopyFilesRecursive(MSIHANDLE hInstall,LPCTSTR lpszSrcPath, LPCTSTR lpszDesPath);

UINT GetWebPort();

//get the setupcommon file path like "<ProgramFilesFolder32>\CA\SharedComponents\ARCserve Backup\Setup"
BOOL GetSetupCommonInstallPath(LPTSTR lpszDir, DWORD& dwSize);

//Get program files path
BOOL GetProgramFilesPath(LPTSTR lpszProgFilesDir, DWORD& dwSize, BOOL bGet32On64OS);

BOOL RegKeyExist(HKEY hKey, LPCTSTR lpSubKey);

BOOL SetRegValue(HKEY hKey, LPCTSTR lpSubKey, LPCTSTR lpValueName, LPCTSTR lpValue, DWORD dwType=REG_SZ);

BOOL UpdateRegUninstallString(MSIHANDLE hInstall,LPCTSTR szInstallDir,const TCHAR* pszLogFileName);

BOOL DeleteTree(LPCTSTR lpszPath, const TCHAR* pszLogFileName = NULL);

//delete the empty folder Recursively
int DeleteEmptyDirectory(MSIHANDLE hInstall,const CString& strDir,LPCTSTR pszLogFileName);

BOOL RemoveRegistry(MSIHANDLE hInstall,const TCHAR* pszLogFileName,HKEY hKeyParent, LPCTSTR lpszKeyChild);

// this function get the .NET Framework install folder
BOOL GetNET20InstallFolder(LPTSTR szValue, DWORD ccBuffer);

BOOL RegisterNETDLL(MSIHANDLE hInstall, LPCTSTR szFileName, BOOL bUninst,LPCTSTR pszLogFileName);

BOOL TrimTailBackSlash(CString &sPath);

BOOL AppendBackSlash(CString &sPath);

BOOL  GetFilePath(LPCTSTR lpFullFileName,LPTSTR lpPath,DWORD ccSize);

//for service
BOOL  StartSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation);
DWORD  StopSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation, BOOL bStopDependents);
BOOL  IsServiceStopped(LPCTSTR lpszServiceName);
BOOL  IsServiceStarted(LPCTSTR lpszServiceName);
BOOL  GetServiceFileName(LPCTSTR lpszServiceName, LPTSTR lpServiceFile, DWORD ccBuffer);
BOOL  ChangeSvcStartType(LPCTSTR lpszServiceName, const DWORD dwStartType);
BOOL  IsServiceExist(LPCTSTR lpszServiceName);
BOOL  ChangeServiceDisplayName(LPCTSTR lpctServiceName, LPCTSTR lpctDisplayName);
BOOL  ChangeServiceDescription(LPCTSTR lpctServiceName, LPCTSTR lpctDescription);
BOOL  ChangeSvcFailureActions(LPCTSTR lpszServiceName, UINT uFirstAction, UINT uSecondAction, UINT uThirdAction, DWORD dwDelay);
//end for service

//For D2D
void UpdateD2DRegistry(MSIHANDLE hInstall,const TCHAR* pszLogFileName);
DWORD CallUninstallUtilityForD2D(MSIHANDLE hInstall,LPCTSTR szInstallDir,const TCHAR* pszLogFileName);
//end for D2D

//handle the service with flag, this is for uninstallation
DWORD StopServiceForUninstall(MSIHANDLE hInstall,LPCTSTR lpszServiceName,const TCHAR* pszLogFileName);
//end

DWORD CallUninstallUtilityForEdge(MSIHANDLE hInstall,LPCTSTR szInstallDir,LPCTSTR szParamter,const TCHAR* pszLogFileName);

//Copy the folders from setup.inf, hInstall must not be NULL
DWORD CopyFolderFromImage(MSIHANDLE hInstall,LPCTSTR szInstallDir,const TCHAR* pszLogFileName);


/****************************************** for process ***************************************/
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
BOOL KillProcessesEx(LPTSTR szKillProc, LPTSTR szFilePath = NULL, LPCTSTR pszLogFileName = NULL);
UINT IsProcessRunning(LPTSTR szKillProc, LPTSTR szFilePath = NULL,BOOL bOutputAllProcListTolog = FALSE);

/****************************************** end process *****************************************/

//***********************************************************//
//***          end add non-custom action  	               **//
//***********************************************************//


