// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the AFFILECATALOG_EXPORTS
// symbol defined on the command line. this symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// AFFILECATALOG_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.

#pragma once

#ifdef AFFILECATALOG_EXPORTS
#define AFFILECATALOG_API __declspec(dllexport)
#else
#define AFFILECATALOG_API __declspec(dllimport)
#endif



#include <vector>
#include "AFFileStoreGlobals.h"
using namespace std;

#include "DbgLog.h"

typedef StandardInfo FileVersionInfo, *pFileVersionInfo;

// One catalog file may contain more than 1 record
typedef struct CATALOG_CHILD_ITEM 
{
	TCHAR	szName[MAX_PATH];
	DWORD   dwFileAttributes;
	pFileVersionInfo versions;
	TCHAR	szFullPath[MAX_PATH*2];
	DWORD  dwVersionCount;
}CatalogChildItem,*pCatalogChildItem;

class FILE_CATALOG_INIT_PARAM
{
public:
	wstring wsCatalogBaseDirPath;
	wstring wsD2DHomePath;
	wstring wsHostName;
	wstring wsDestGUID;
	FILE_CATALOG_INIT_PARAM()
		:wsCatalogBaseDirPath(L""), wsD2DHomePath(L""), wsHostName(L""), wsDestGUID(L"")
	{
	}
};



//start exports
AFFILECATALOG_API HANDLE OpenAFFileCatalogFile(PTCHAR szVolumeName);
AFFILECATALOG_API DWORD  GetChildrenCount(HANDLE VolumeHandle, PTCHAR szPath);
AFFILECATALOG_API DWORD  GetChildren(HANDLE VolumeHandle, PTCHAR szPath,vector <pCatalogChildItem> &versions);
AFFILECATALOG_API DWORD  AddNewVersionToMetaData(HANDLE VolumeHandle,PTCHAR szFileName,pFileVersionInfo stInfoTemp);
AFFILECATALOG_API DWORD  AddNewVersionToMetaData(HANDLE VolumeHandle,PTCHAR szFileName,pFileVersionInfo stInfoTemp);
AFFILECATALOG_API DWORD  AFGetArchiveDestinationVolumeList(PTCHAR szHostName, std::vector<PTCHAR> &VolumeList);//kappr01
AFFILECATALOG_API DWORD FreeCatalogItems(vector <pCatalogChildItem> &versions);
AFFILECATALOG_API DWORD FreeVolumeItems(std::vector<PTCHAR> &VolumeList);
AFFILECATALOG_API void CloseAFFileCatalogFile(HANDLE VolumeHandle);
AFFILECATALOG_API DWORD  InitFileCatalogModule(FILE_CATALOG_INIT_PARAM& param);
//end exports


class IFileCopyCatalogMgr
{
public:

	IFileCopyCatalogMgr() {};
	virtual ~IFileCopyCatalogMgr() {};

	virtual DWORD Init(FILE_CATALOG_INIT_PARAM& param)=0;
	virtual DWORD OpenMachine(wstring& catalogDirBasePath, wstring& catalogDirUserName, wstring& catalogDirPassword, PTCHAR szHostName, wstring& destGUID, HANDLE& machineCatalogHandle) = 0;
	virtual DWORD GetVolumeList(HANDLE machineCatalogHandle, std::vector<PTCHAR> &VolumeList) = 0;
	virtual DWORD OpenVolume(HANDLE machineCatalogHandle, PTCHAR szVolumeName, HANDLE& volumeHandle)=0;
	virtual DWORD GetChildItemCount(HANDLE volumeHandle, PTCHAR szPath, DWORD& count) = 0;
	virtual DWORD GetChildItems(HANDLE volumeHandle, PTCHAR szPath, vector <pCatalogChildItem> &versions) = 0;
	virtual DWORD GetChildItemsEx(HANDLE volumeHandle, PTCHAR szPath, vector <pCatalogChildItem> &versions, DWORD nStart, DWORD nReqItemCount) = 0;
	virtual void  CloseVolumeHandle(HANDLE volumeHandle) = 0;
	virtual void  CloseHandle(HANDLE machineCatalogHandle) = 0;

	virtual DWORD SearchChildItems(wstring& catalogDirBasePath, PTCHAR szHostName, wstring& destGUID, TCHAR *in_szFileName, TCHAR *in_szSearchPath, DWORD in_lSearchOptions, DWORD in_lIndex, DWORD in_lCount, vector <pCatalogChildItem> &vecCatalogList) = 0;

	virtual DWORD FreeCatalogChildItems(vector <pCatalogChildItem> &versions) = 0;
	virtual	DWORD FreeVolumeListItems(std::vector<PTCHAR> &VolumeList) = 0;
	virtual void  Release()=0;
};

AFFILECATALOG_API IFileCopyCatalogMgr* CreateInstanceFileCopyCatalog();
 