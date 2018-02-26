// ComAPI.h : main header file for the ComAPI
//

#include <winsvc.h>
#define SERVICE_CASUNIVERSALAGENT		    _T("CASUniversalAgent")
#define REGKEY_MICROSOFT_WINDOWS		    _T("SOFTWARE\\Microsoft\\Windows\\CurrentVersion")
#define REGVALUE_PROGRAM_FILES_DIR		    _T("ProgramFilesDir")
#define REGVALUE_PROGRAM_FILES_DIR_X86	    _T("ProgramFilesDir (x86)")
#define REGKEY_MICROSOFT_WINDOWS_RUNONCE    _T("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\RunOnce")

//1 MB for converting size
#define BASE_MB		(1024*1024)

//Common API
//for service
BOOL  StartSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation);
DWORD StopSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation, BOOL bStopDependents);
BOOL  IsServiceStopped(LPCTSTR lpszServiceName);
BOOL  IsServiceStarted(LPCTSTR lpszServiceName);
BOOL  GetServiceFileName(LPCTSTR lpszServiceName, LPTSTR lpServiceFile, DWORD ccBuffer);
BOOL  GetServiceDisplayName(LPCTSTR lpszServiceName, LPTSTR lpServiceDisplayName, DWORD ccBuffer);
BOOL  IsServiceExist(LPCTSTR lpszServiceName);
//end for service

BOOL IsEmptyKey(HKEY hKeyParent, LPCTSTR lpKey, BOOL bNative);

//only remove current empty key, not Recursive
BOOL RemoveEmptyRegistry(HKEY hKeyParent, LPCTSTR lpszKey, BOOL bNative);

//delete the registry key
LONG RecursiveDeleteKey(HKEY hKeyParent, LPCTSTR lpszKeyChild, BOOL bNative);

//delete the registry value
BOOL DeleteRegValue(HKEY hKey, LPCTSTR lpSubKey, LPCTSTR lpValueName, BOOL bNative);

BOOL SetRegValue(HKEY hKey, LPCTSTR lpSubKey, LPCTSTR lpValueName, LPCTSTR lpValue, DWORD dwType=REG_SZ);

//get the registry value
BOOL GetRegValue(HKEY hKey, LPCTSTR lpSubKey, LPCTSTR lpValueName, CString & strReturnValue,BOOL bNative=TRUE,DWORD dwType=REG_SZ);

BOOL GetLocalOSVersion(DWORD &dwMajorVersion, DWORD &dwMinorVersion, WORD &wServicePackMajor, WORD &wSuiteMask);

//get OS version, if the OS is windows 2003 or below, return  TRUE;
BOOL IsOSUnderW2k3();

BOOL Is64BitSystem();

//get the file path (without file name)
BOOL  GetFilePath(LPCTSTR lpFullFileName,LPTSTR lpPath,DWORD ccSize);

//get free disk size with bytes
DWORD GetFreeDiskSize(LPCTSTR lpszDrive,DWORD64& dw64FreeDiskSize);

//check the need size, and return the free diskspace with MB
DWORD ValidateDiskSpace(LPCTSTR lpPath,DWORD64 dwNeedSize,DWORD64& dwFreeDiskSizeWithMB);

//End Common API