#pragma once

#include <string>
#include <vector>

using namespace std;

DWORD String2Int( const wstring &str, int &val );

DWORD ConvertString2Version( wstring strVer, UINT &majorVer, UINT &minorVer );

int CompareStringIgnoreCase( const wstring &str1, const wstring &str2 );

HANDLE DynGetProcAddress( const wstring &dllName, const char* procName , HMODULE & dll);

DWORD RunProcess( const wstring &strPath, HANDLE &handle );

DWORD RunProcessWithWorkingDir( const wstring &strPath, HANDLE &handle, const wstring &strWorkDir);

DWORD KillProcess( const wstring &strProcessName );

//************************************
// Returns:  0: success; 1: other wrong; 2: no process match the name 
// hProcess: if failed hProcess = NULL
//************************************
DWORD GetProcessByName( const wstring &strProcessName, HANDLE &hProcess );

//************************************
// Returns:  0: success; 1: fail; 2: servie is not stopped before this call.
//************************************
DWORD StartWindowsService( const wstring &strServiceName, bool bWait );

DWORD StopWindowsService( const wstring &strServiceName, bool bWait );

DWORD IsWindowsServiceRunning( const wstring &strServiceName, OUT bool &bRuning);

DWORD GetOpt( const wstring &strParamters, vector<wstring> &vecKey, vector<wstring> &vecValue );

//************************************
// Returns:   1.no privilege;
//************************************
DWORD WINAPI RebootSystem( bool force );

DWORD AdjustPrivilege(LPCWSTR lpPrivilege, bool bEnable);

//************************************
// Returns:  0: success read file; 1: other error, 2: time-out; 3: interrupted by hEvent
//************************************
DWORD ReadFileEx(__in       HANDLE hFile,
				 __out_opt  LPVOID lpBuffer,
				 __in       DWORD nNumberOfBytesToRead,
				 __out_opt  LPDWORD lpNumberOfBytesRead,
				 __in_opt   HANDLE hEvent,
				 __in_opt	DWORD dwMilliseconds);

BOOL IsFileExist(const wstring &strPath);

//if want to set SOFTWARE\\CA\\CA ARCserve Unified Application\\Test"
//strKeyPath = "SOFTWARE\\CA\\CA ARCserve Unified Application\\", strValueName=Test
//If test does not exist, it will be created with type REG_SZ
//Returns -2: failed
DWORD SetRegistryValue( const wstring &strKeyPath, const wstring &strValueName, const wstring &strValue );