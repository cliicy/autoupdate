#include "stdafx.h"
#include <eh.h>
#include "utils.h"
#include "Catalog.h"
#include <jni.h>
//#include "GRTMntBrowser.h"

#include "iAppBrowse.h"

#define TRY __try{
#define CATCH(procName) }__except(HandleSEH(L"GRTMntBrowser.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GET_PROC_ADDRESS(procName) DynGetProcAddress(L"GRTMntBrowser.dll", procName)

HMODULE h_gGRTMntBser = NULL;

HRESULT XInit()
{
	h_gGRTMntBser =LoadLibraryW(L"GRTMntBrowser.dll");
	if( h_gGRTMntBser == NULL )
	{
		HandleDllMissed(L"GRTMntBrowser.dll");
		return E_FAIL;
	}
	TRY
	typedef HRESULT (* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("XInit");

	return pfun();
	CATCH("Init")
}

HRESULT XUnInit()
{
	TRY
	typedef HRESULT (* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("XUnInit");

	return pfun();
	CATCH("UnInit")
}

HANDLE InitGRTMounter()
{
	TRY
	typedef HANDLE (* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("InitGRTMounter");;

	return pfun();
	CATCH("InitGRTMounter")
}

DWORD MountVolume(HANDLE h,SESSION_INFO si, wchar_t* szVolumeGUID, wchar_t* szEncryptKey, DWORD dwJobId, DWORD dwJobType, DWORD dwProductType, wchar_t* lpMachineUuid,
	wchar_t* szDiskId, wchar_t* szMountedVolumeGUID)
{
	TRY
	typedef DWORD (* LPFUN)(HANDLE, SESSION_INFO,wchar_t*,wchar_t*,DWORD, DWORD, DWORD, wchar_t*, wchar_t*,wchar_t*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("MountVolume");;

	return pfun(h,si,szVolumeGUID,szEncryptKey,dwJobId, dwJobType, dwProductType, lpMachineUuid, szDiskId,szMountedVolumeGUID);
	CATCH("MountVolume")
}

DWORD UpdateCatalogJobScript(SESSION_INFO si, wchar_t* jobScript ,  const WCHAR* pwzJobQIdentity)
{
	TRY
	typedef DWORD (* LPFUN)(SESSION_INFO, wchar_t*, const WCHAR *);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("UpdateCatalogJobScript");;
	
	return pfun(si,jobScript, pwzJobQIdentity);
	CATCH("UpdateCatalogJobScript")
}

DWORD UnMountVolume(HANDLE handle,wchar_t *lpDiskId)
{
	TRY
	typedef DWORD (* LPFUN)(HANDLE,wchar_t*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("UnMountVolume");;

	return pfun(handle,lpDiskId);
	CATCH("UnMountVolume")
}

DWORD UnMountAll(HANDLE handle)
{
	TRY
	typedef DWORD (* LPFUN)(HANDLE);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("UnMountAll");;

	return pfun(handle);
	CATCH("UnMountAll")
}
DWORD ExitGRTMounter(HANDLE handle)
{
	TRY
	typedef DWORD (* LPFUN)(HANDLE);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("ExitGRTMounter");;

	return pfun(handle);
	CATCH("ExitGRTMounter")
} 

PDetailW GetFileChildren(wchar_t *szMountedVolumeGUID,wchar_t *szPath,UINT *Cnt)
{
	TRY
	typedef PDetailW (* LPFUN)(wchar_t*,wchar_t*,UINT*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetFileChildren");;

	return pfun(szMountedVolumeGUID,szPath,Cnt);
	CATCH("GetFileChildren")
}

PDetailW GetFileChildrenEx(wchar_t *szMountedVolumeGUID,wchar_t *szPath,UINT *Cnt, BOOL bGetCount,UINT nStart,UINT nRequest)
{
	TRY
	typedef PDetailW (* LPFUN)(wchar_t*,wchar_t*,UINT*,BOOL,UINT,UINT);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetFileChildrenEx");;

	return pfun(szMountedVolumeGUID,szPath,Cnt,bGetCount,nStart,nRequest);
	CATCH("GetFileChildrenEx")
}

UINT GetFileChildrenCount(wchar_t *szMountedVolumeGUID,wchar_t *szPath)
{
	TRY
	typedef UINT (* LPFUN)(wchar_t*,wchar_t*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetFileChildrenCount");;

	return pfun(szMountedVolumeGUID,szPath);
	CATCH("GetFileChildrenCount")
}

SESSION_INFO Convert2SessionInfo(wchar_t* rootfolder,wchar_t* username,wchar_t* password, DWORD sessionNumber)
{
	SESSION_INFO si = {0};
	si.dwSessNo = sessionNumber;
	si.lpPassword = password ;
	si.lpRootFolder = rootfolder;
	si.lpUserName = username;
	si.pEncryptInfo = NULL;
	return si;
}

HANDLE SearchMountPoint(wchar_t* szBackupDestination, wchar_t* szMountedVolumeGUID, wchar_t* szDriveLetter, wchar_t* szPattern, wchar_t* szSearchDir,BOOL bIncludeSubDir, BOOL bCaseSensitive)
{
	TRY
	typedef HANDLE (* LPFUN)(wchar_t*, wchar_t*, wchar_t*, wchar_t*, wchar_t*, BOOL, BOOL);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("SearchMountPoint");;

	return pfun(szBackupDestination,szMountedVolumeGUID,szDriveLetter,szPattern,szSearchDir,bIncludeSubDir,bCaseSensitive);
	CATCH("SearchMountPoint")
}

INT GetCatalogStatus(wchar_t* sessionPath)
{
	TRY
		typedef INT (* LPFUN)(wchar_t*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetCatalogStatus");;

	return pfun(sessionPath);
	CATCH("GetCatalogStatus")
}

INT SetCatalogStatus(wchar_t* backupDest, wchar_t* sessionPath)
{
	TRY
	typedef INT (* LPFUN)(wchar_t*, wchar_t*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("SetCatalogStatus");;

	return pfun(backupDest, sessionPath);
	CATCH("SetCatalogStatus")
}

DWORD FindNextSearchItems(HANDLE hSearchContext,UINT nRequest, PDetailW *pDetail, UINT *nFound)
{
	TRY
	typedef DWORD (* LPFUN)(HANDLE,UINT,PDetailW*,UINT*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("FindNextSearchItems");;

	return pfun(hSearchContext,nRequest,pDetail,nFound);
	CATCH("FindNextSearchItems")
}
DWORD FindCloseSearchItems(HANDLE hSearchContext)
{	
	TRY
	typedef DWORD (* LPFUN)(HANDLE);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("FindCloseSearchItems");;

	return pfun(hSearchContext);
	CATCH("FindCloseSearchItems")
}

DWORD AppGetItems(PAGRT_PARENT pvParent, PAGRT_ITEMS pvItem)
{
	TRY
	typedef DWORD (* LPFUN)(PAGRT_PARENT pvParent, PAGRT_ITEMS pvItem);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AppGetItems");
	return pfun(pvParent,pvItem);

	CATCH("AppGetItems")
}

DWORD AppRleaseItems(PAGRT_ITEMS pvItems)
{
	TRY
	typedef DWORD (* LPFUN)(PAGRT_ITEMS pvItems);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AppRleaseItems");
	return pfun(pvItems);

	CATCH("AppRleaseItems")
}


DWORD AppRestoreForAD(PAGRT_AD_SESSION pvSession, PAGRT_RESTORE_AD pRestore, IAppCallback * pvCallback)
{
	TRY
		typedef DWORD (* LPFUN)(PAGRT_AD_SESSION pvSession, PAGRT_RESTORE_AD pRestore, IAppCallback * pvCallback);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AppRestoreForAD");
	return pfun(pvSession, pRestore, pvCallback);

	CATCH("AppRestoreForAD")
}
