#pragma once
#include <string>
#include "winsvc.h"
using namespace std;

#pragma once

#define WM_APM_STATUS_CHANGED (WM_USER + 100)

#define APM_STATUS_BEGIN					0
#define APM_STATUS_INSTALL_FAILED		1
#define APM_STATUS_INSTALL_FINISHED			2
#define APM_STATUS_ERROR					3
#define APM_STATUS_PROCESS_PERCENT			4
#define APM_STATUS_BACKEND_MESSAGE			5
#define APM_STATUS_APPLICATION_STOP         6
#define APM_STATUS_SERVICE_STOP             7
#define APM_STATUS_DOWNLOADING              8
#define APM_STATUS_INSTALL_START            9
#define APM_STATUS_INSTALL_PATCH            10
#define APM_STATUS_PATCHVERIFY              11
#define APM_STATUS_VERIFY                   12

#define FILE_APM_RESOURCE_DLL				L"APMSetupUtilityRes.dll"
#define FILE_ACCESS_RETRY_COUNT  300
void WriteLog(const TCHAR* pszFormat, ...);
#define WRITE_SEPARATOR_LOG		WriteLog(_T(""))


DWORD SetupGetPrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpDefault, LPTSTR lpReturnedString, DWORD nSize, LPCTSTR lpFileName);
BOOL SetupWritePrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpString, LPCTSTR lpFileName);
BOOL SetupWritePrivateProfileInt(LPCTSTR lpAppName, LPCTSTR lpKeyName, INT Value, LPCTSTR lpFileName);
UINT SetupGetPrivateProfileInt(LPCTSTR lpAppName, LPCTSTR lpKeyName, INT nDefault, LPCTSTR lpFileName);
DWORD SetupGetPrivateProfileSection(LPCTSTR lpSectionName, LPTSTR szSecBuffer, DWORD nSize, LPCTSTR lpFileName);
void GetStringArray(LPCTSTR lpctValue, TCHAR cSplitChar, CStringArray& data);

DWORD LaunchProcess(LPCTSTR lpctCmdLine, LPCTSTR pWorkingDir, DWORD &dwExitCode, DWORD dwMilliseconds=INFINITE, DWORD dwCreationFlags=0);
BOOL FindProcesses(const CStringArray &strPrcArray, CArray<DWORD> &dwPidArray);
BOOL EnableDebugPrivNT();
void TranslateDeviceName(LPCTSTR lpctDevicename, LPTSTR lpBuf);
BOOL KillProcess(DWORD dwProcessId);
BOOL Is64BitMachine(LPCTSTR lpMachine);
BOOL DecryptString(const CString &strIn, CString &strOut, CString & instrworkdir);
BOOL EncryptString(const CString &strIn, CString &strOut, CString &instrworkdir);
void RebootSystem();
void GetComponents(LPCTSTR lpctFile);
BOOL IsServiceStart(LPCTSTR lpszServiceName);
BOOL StartService(SC_HANDLE schService, BOOL bBlockOperation);
BOOL StopDependentServices(LPCTSTR lpszServiceName, BOOL bBlockOperation);
BOOL StopService(SC_HANDLE schService, LPCTSTR lpszServiceName, BOOL bBlockOperation, BOOL bStopDependents);
BOOL ChangeSvcStartType(LPCTSTR lpszServiceName, const DWORD dwStartType);
BOOL StopSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation, BOOL bStopDependents);
BOOL StartSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation /*= TRUE*/);
void RebootWinnt();
DWORD getLastXmlDoc( IN const wstring &strSource, OUT wstring &strLastXml );

#define REGKEY_MICROSOFT_WINDOWS			_T("SOFTWARE\\Microsoft\\Windows\\CurrentVersion")
#define REGVALUE_PROGRAM_FILES_DIR			_T("ProgramFilesDir")
#define REGVALUE_PROGRAM_FILES_DIR_X86		_T("ProgramFilesDir (x86)")
