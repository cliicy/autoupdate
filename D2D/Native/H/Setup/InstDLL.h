////////////////////////////////////////////////////////////////////////////////////////////
// InstDLL.h - Installation DLL exported function prototypes and typedefs

////////////////////////////////////////////////////////////////////////////////////////////
// Func:	Install()
// Author:	John Gargiulo	8/9/97
// 
// Desc:	Does everything necessary to install the application EXCEPT
//			copying files (i.e. Install Services, Setup Registry, etc...)
//
// Return:	0 == SUCCESS, otherwise error code.
//
// Params:	void	*pData	-	Pointer to structure with installation info.
//								This structure is DLL dependent and requires
//								the inclusion of a seperate h file.
//
// Rev:
//	1.0	8/9/97	-	Initial. Note: would like to make this function take a void*
//					so different structures can be passed to different installation
//					DLLs. (I don't know if non C/C++ install programs <ie. InstallShield>
//					can handle structure definitions)
//
//  1.1 9/2/97	-	Found out about InstallShield so changed this function to accept
//					a void pointer
//

DWORD APIENTRY Install(void *pData);
typedef DWORD (APIENTRY *LPFN_DLL_INSTALL)(void *pData);


////////////////////////////////////////////////////////////////////////////////////////////
// Func:	UnInstall()
// Author:	John Gargiulo	8/9/97
// 
// Desc:	Does everything necessary to uninstall the application EXCEPT
//			deleting files and directories. Basically undoes what Install()
//			did. (i.e. Remove Services, Remove Registry Settings, etc...)
//
// Return:	0 == SUCCESS, otherwise error code.
//
// Params:	NONE
//
// Rev:
//	1.0	8/9/97	-	Initial.
//

DWORD APIENTRY UnInstall();
typedef DWORD (APIENTRY *LPFN_DLL_UNINSTALL)();


////////////////////////////////////////////////////////////////////////////////////////////
// Func:	IsInstalledVersionNewer()
// Author:	John Gargiulo	8/9/97
// 
// Desc:	Determines if the currently installed version on the application
//			is newer than the one being installed.
//
// Return:	0 == SUCCESS, otherwise error code.
//
// Params:	BOOL *pResult	- pointer to variable that gets set to TRUE/FALSE
//
// Rev:
//	1.0	8/9/97	-	Initial.
//

DWORD APIENTRY IsInstalledVersionNewer(BOOL *pResult);
typedef DWORD (APIENTRY *LPFN_DLL_ISINSTALLEDVERSIONNEWER)(BOOL *pResult);


////////////////////////////////////////////////////////////////////////////////////////////
// Func:	GetDebugErrorString()
// Author:	John Gargiulo	8/9/97
// 
// Desc:	Return a human readable string for an error code returned from any 
//			of the other functions in the DLL. (English only NOT localized)
//
// Return:	0 == SUCCESS, otherwise error code.
//
// Params:	DWORD dwErr			- Error code returned from another function
//			LPSTR pszBuffer		- Pointer to a buffer to receive error string
//			DWORD dwSize		- Size of buffer pointer to by pszBuffer
//
// Rev:
//	1.0	8/9/97	-	Initial.
//

DWORD APIENTRY GetDebugErrorString(DWORD dwErr, LPTSTR pszBuffer, DWORD dwSize);
typedef DWORD (APIENTRY *LPFN_DLL_GETDEBUGERRORSTRING)(DWORD dwErr, LPTSTR pszBuffer, DWORD dwSize);


////////////////////////////////////////////////////////////////////////////////////////////
// Func:	GetInstallPath()
// Author:	John Gargiulo	8/9/97
// 
// Desc:	Return the current installations install path
//
// Return:	0 == SUCCESS, otherwise error code.
//
// Params:	LPSTR pszBuffer		- Pointer to a buffer to receive install path
//			DWORD dwSize		- Size of buffer pointer to by pszBuffer
//
// Rev:
//	1.0	8/9/97	-	Initial.
//

DWORD APIENTRY GetInstallPath(LPTSTR pszBuffer, DWORD dwSize);
typedef DWORD (APIENTRY *LPFN_DLL_GETINSTALLPATH)(LPTSTR pszBuffer, DWORD dwSize);


////////////////////////////////////////////////////////////////////////////////////////////
// Func:	IsInstalled()
// Author:	John Gargiulo	8/10/97
// 
// Desc:	Determines if there is a version currently installed
//
// Return:	0 == SUCCESS, otherwise error code.
//
// Params:	BOOL *pResult	- pointer to variable that gets set to TRUE/FALSE
//
// Rev:
//	1.0	8/9/97	-	Initial.
//

DWORD APIENTRY IsInstalled(BOOL *pResult);
typedef DWORD (APIENTRY *LPFN_DLL_ISINSTALLED)(BOOL *pResult);
