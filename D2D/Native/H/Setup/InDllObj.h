////////////////////////////////////////////////////////////////////////////////////////////
// InDllObj.h - Class definition for CInstallationDll
//
// 8/9/97	-	John Gargiulo
//

/* _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/

	The CInstallationDll class encapsulates installation Dlls using
	CInstallationObject derived classes. The pseudo code below shows
	it's use for installation and uninstall.  The object just wraps
	the functions in the DLL and will dynamically load and unload
	the library in the Load() and Unload() functions. (See InDllObj.cpp)
	SOME_INSTALL_STRUCT is a structure specific to the DLL being used,
	and should be provided by the DLL implementor in a seperate h file.
...................................................................................

	void InstallAppX()
	{
		CInstallationDll	InstDll;
		DWORD				dwErr						= 0;
		BOOL				bInstalled					= FALSE;
		BOOL				bInstall					= TRUE;
		BOOL				bNewer						= FALSE;

		SOME_INSTALL_STRUCT	InstallData;

		dwErr =InstDll.Load("InstAppX.DLL");

		if (!dwErr)
		{
			dwErr = InstDll.IsInstalled(&bInstalled);

			if (!dwErr && bInstalled)
				dwErr = InstDll.IsInstalledVersionNewer(&bNewer);

			if (!dwErr && bNewer)
				bInstall = DoesUserWantToOverwriteNewerVersion();

			if (bInstall)
				dwErr = InstDll.Install(&InstallData);

			dwErr = InstDll.Unload();
		}
	}

...................................................................................

  	void UnInstallAppX()
	{
		CInstallationDll	InstDll;
		DWORD				dwErr						= 0;
		BOOL				bInstalled					= FALSE;
		char				szInstallPath[_MAX_PATH]	= "";

		dwErr =InstDll.Load("InstAppX.DLL");

		if (!dwErr)
		{
			dwErr = InstDll.IsInstalled(&bInstalled);

			if (!dwErr && bInstalled)
			{
				dwErr = InstDll.GetInstallPath(szInstallPath, sizeof(szInstallPath));
				dwErr = InstDll.UnInstall();
			}
			dwErr = InstDll.Unload();
		}

		if (!dwErr && bInstalled)
			DeleteFilesAndRemoveDirectory(szInstallPath);
	}

 _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/ */

#ifndef __CLASS_CInstallationDll_DEFINED__
#define __CLASS_CInstallationDll_DEFINED__

#include <windows.h>
#include "instdll.h"
#include "instobj.h"

#define INSTDLL_NOT_LOADED	0xFFFFFFFF

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CInstallationDll : public CInstallationObject
#else
class __declspec(dllimport) CInstallationDll : public CInstallationObject
#endif
{
public:
	virtual DWORD	Install(void *pData);
	virtual DWORD	UnInstall();
	virtual DWORD	IsInstalled(BOOL *pResult);
	virtual DWORD	IsInstalledVersionNewer(BOOL *pResult);
	virtual DWORD	GetDebugErrorString(DWORD dwErr, LPTSTR pszBuffer, DWORD dwSize);
	virtual DWORD	GetInstallPath(LPTSTR pszBuffer, DWORD dwSize);

public:
	CInstallationDll();
	~CInstallationDll();

	DWORD Load(LPCTSTR pszDllPath);
	DWORD Unload();

protected:
	void InitNull();

protected:
	HINSTANCE							m_hDll;
	LPFN_DLL_INSTALL					m_pfnInstall;
	LPFN_DLL_UNINSTALL					m_pfnUnInstall;
	LPFN_DLL_ISINSTALLED				m_pfnIsInstalled;
	LPFN_DLL_ISINSTALLEDVERSIONNEWER	m_pfnIsInstalledNewer;
	LPFN_DLL_GETDEBUGERRORSTRING		m_pfnGetDebugString;
	LPFN_DLL_GETINSTALLPATH				m_pfnGetInstallPath;
};

#endif // __CLASS_CInstallationDll_DEFINED__