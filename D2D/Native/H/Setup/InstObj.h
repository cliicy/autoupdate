////////////////////////////////////////////////////////////////////////////////////////////
// InstObj.h - Class definition for Abstract class CInstallationObject
//
// 8/9/97 - John Gargiulo

#ifndef __CLASS_CInstallationObject_DEFINED__
#define __CLASS_CInstallationObject_DEFINED__

#include <windows.h>

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CInstallationObject
#else
class __declspec(dllimport) CInstallationObject
#endif
{
public:
	virtual DWORD	Install(void *pData) = 0;
	virtual DWORD	UnInstall() = 0;
	virtual DWORD	IsInstalled(BOOL *pResult) = 0;
	virtual DWORD	IsInstalledVersionNewer(BOOL *pResult) = 0;
	virtual DWORD	GetDebugErrorString(DWORD dwErr, LPTSTR pszBuffer, DWORD dwSize) = 0;
	virtual DWORD	GetInstallPath(LPTSTR szBuffer, DWORD dwSize) = 0;
};

#endif // __CLASS_CInstallationObject_DEFINED__