// buildversion.h
// You can get the version number and build number from the dll you specified
#ifdef _SETUPCLS_DLL 
__declspec(dllexport) CString	SetupCLSGetVersionString(CString sVersionDllPath);
__declspec(dllexport) WORD		SetupCLSGetVersionNumber(CString sVersionDllPath);
__declspec(dllexport) CString	SetupCLSGetBuildString(CString sVersionDllPath);
__declspec(dllexport) WORD		SetupCLSGetBuildNumber(CString sVersionDllPath);
__declspec(dllexport) DWORD		SetupCLSGetVersionBuildNumber(CString sVersionDllPath);
#else
__declspec(dllimport) CString	SetupCLSGetVersionString(CString sVersionDllPath);
__declspec(dllimport) WORD		SetupCLSGetVersionNumber(CString sVersionDllPath);
__declspec(dllimport) CString	SetupCLSGetBuildString(CString sVersionDllPath);
__declspec(dllimport) WORD		SetupCLSGetBuildNumber(CString sVersionDllPath);
__declspec(dllimport) DWORD		SetupCLSGetVersionBuildNumber(CString sVersionDllPath);
#endif
