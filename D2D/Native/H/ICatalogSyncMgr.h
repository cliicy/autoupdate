#pragma once
#include <windows.h>
#include <string>
#include <tchar.h>

using namespace std;

class AFSTOREAPI ICatalogSyncMgr
{
public:
	virtual BOOL InitCatalogSyncMgr(wstring& szLocalCatalogPath)=0;
	virtual void DeInitCatalogSyncMgr ()=0;
	virtual DWORD DoCatalogSync(BOOL bRecursive, BOOL bIsCatalogResyncJobForAlternateLocation,wstring szFolder) = 0;	
};

extern "C" AFSTOREAPI ICatalogSyncMgr* InitCSM();
extern "C" AFSTOREAPI BOOL EndCSM(ICatalogSyncMgr** in_pICatalogSyncMgr);