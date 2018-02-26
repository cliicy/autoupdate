#ifndef _ARCUTIL_H_
#define _ARCUTIL_H_

#ifdef __cplusplus
extern "C" {
#endif
BOOL	WINAPI GetRemoteWindowsDirectory(OUT LPTSTR);
BOOL	WINAPI IsNTVersion_3_51();
BOOL	WINAPI IsNTVersion_4_0();
BOOL	WINAPI SendStatusBack(IN UINT nStatus);
void	WINAPI WriteToErrLog(IN LPTSTR format,...);
DWORD	WINAPI IsCheyProductInstalled (IN DWORD dwProdID);
BOOL	WINAPI UninstallASOption (IN DWORD dwProductID);
BOOL	WINAPI KeepAlert();
#ifdef __cplusplus
}
#endif

#define	ARCSERVE_NOTIFIER	"ARCserve"
#define	TNG_EM_NOTIFIER		"Enterprise Management"
#define TNGSETUP_DLL		"tngsetup.dll"

#endif//_ARCUTIL_H_