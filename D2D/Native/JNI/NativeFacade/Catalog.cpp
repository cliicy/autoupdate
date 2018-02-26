#include "stdafx.h"
#include <eh.h>
#include "utils.h"
#include "Catalog.h"
#include <jni.h>

#define TRY __try{
#define CATCH(procName) }__except(HandleSEH(L"Catalog.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GET_PROC_ADDRESS(procName) DynGetProcAddress(L"Catalog.dll", procName)


DWORD GenerateIndexFiles(wchar_t *Catalogfile)
{
	TRY
	typedef DWORD (* LPFUN)(wchar_t *);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GenerateIndexFiles");

	return pfun(Catalogfile);
	CATCH("GenerateIndexFiles")
}

HANDLE OpenCatalogFile(wchar_t *Catalogname)
{
	TRY
	typedef HANDLE (* LPFUN)(wchar_t *);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("OpenCatalogFile");;

	return pfun(Catalogname);
	CATCH("GenerateIndexFiles")
}

PDetailW GetChildren(HANDLE handle, unsigned long LongNameID, UINT *Cnt)
{
	TRY
	typedef PDetailW (* LPFUN)(HANDLE, unsigned long, UINT*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetChildren");;

	return pfun(handle, LongNameID, Cnt);
	CATCH("GenerateIndexFiles")
}

wchar_t *GetFullPath(HANDLE handle, unsigned long PathID, wchar_t *lpString, UINT sBufSize)
{
	TRY
	typedef wchar_t * (* LPFUN)(HANDLE, unsigned long, wchar_t *, UINT);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetFullPath");;

	return pfun(handle, PathID, lpString, sBufSize);
	CATCH("GenerateIndexFiles")
}

void CloseCatalogFile(HANDLE handle)
{
	TRY
	typedef void (* LPFUN)(HANDLE);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("CloseCatalogFile");;

	return pfun(handle);
	CATCH("GenerateIndexFiles")
}

HANDLE SearchCatalogFile(wchar_t *sDestination, unsigned long begin_sesstime, unsigned long end_sesstime, wchar_t *sDir, BOOL bCaseSensitive, BOOL bIncludeSubDir, wchar_t *pattern, UINT *TotalCnt)
{
	TRY
	typedef HANDLE (* LPFUN)(wchar_t *, unsigned long, unsigned long, wchar_t*, BOOL, BOOL, wchar_t*, UINT*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("SearchCatalogFile");;

	return pfun(sDestination, begin_sesstime, end_sesstime, sDir, bCaseSensitive, bIncludeSubDir, pattern, TotalCnt);
	CATCH("GenerateIndexFiles")
}

UINT FindNextCatalogFile(HANDLE handle, UINT nRequest, PDetailW *pDetail, UINT *nFound)
{
	TRY
	typedef UINT (* LPFUN)(HANDLE, UINT, PDetailW*, UINT*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("FindNextCatalogFile");;

	return pfun(handle, nRequest, pDetail, nFound);
	CATCH("GenerateIndexFiles")
}

wchar_t *GetFindFullPath(HANDLE handle, unsigned long PathID, wchar_t *lpString, UINT sBufSize)
{
	TRY
	typedef wchar_t * (* LPFUN)(HANDLE, unsigned long, wchar_t*, UINT);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetFindFullPath");;

	return pfun(handle, PathID, lpString, sBufSize);
	CATCH("GenerateIndexFiles")
}
void FindCloseCatalogFile(HANDLE handle)
{
	TRY
	typedef void (* LPFUN)(HANDLE);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("FindCloseCatalogFile");;

	return pfun(handle);
	CATCH("GenerateIndexFiles")
}

UINT GetChildrenCount(HANDLE handle, unsigned long LongNameID)
{
	TRY
	typedef UINT (* LPFUN)(HANDLE, unsigned long);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetChildrenCount");;

	return pfun(handle, LongNameID);
	CATCH("GenerateIndexFiles")
}

PDetailW GetChildrenEx(HANDLE handle, unsigned long LongNameID, UINT nStart, UINT nRequest, UINT *Cnt)
{
	TRY
	typedef PDetailW (* LPFUN)(HANDLE, unsigned long, UINT, UINT, UINT*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("GetChildrenEx");;

	return pfun(handle, LongNameID, nStart, nRequest, Cnt);
	CATCH("")
}

HANDLE SearchCatalogFileEx(wchar_t *sCatalogSession, unsigned long begin_sesstime, unsigned long end_sesstime, wchar_t *sDir, BOOL bCaseSensitive, BOOL bIncludeSubDir, wchar_t *pattern, UINT *TotalCnt)
{
	TRY
	typedef HANDLE (* LPFUN)(wchar_t *sCatalogSession, unsigned long begin_sesstime, unsigned long end_sesstime, wchar_t *sDir, BOOL bCaseSensitive, BOOL bIncludeSubDir, wchar_t *pattern, UINT *TotalCnt);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("SearchCatalogFileEx");;

	return pfun(sCatalogSession, begin_sesstime, end_sesstime, sDir, bCaseSensitive, bIncludeSubDir, pattern, TotalCnt);
	CATCH("SearchCatalogFileEx")
}

UINT FindNextCatalogFileEx(HANDLE handle, UINT nRequest, PDetailW *pDetail, UINT *nFound)
{
	TRY
	typedef UINT (* LPFUN)(HANDLE handle, UINT nRequest, PDetailW *pDetail, UINT *nFound);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("FindNextCatalogFileEx");;

	return pfun(handle, nRequest, pDetail, nFound);
	CATCH("FindNextCatalogFileEx")
}