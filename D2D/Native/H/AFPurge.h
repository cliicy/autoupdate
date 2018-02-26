// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the AFPURGE_EXPORTS
// symbol defined on the command line. this symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// AFPURGE_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.
#ifdef AFPURGE_EXPORTS
#define AFPURGE_API __declspec(dllexport)
#else
#define AFPURGE_API __declspec(dllimport)
#endif

#include "afjob.h"
#include "AFFileCatalog.h"

extern HINSTANCE m_hArchiveCatalogHandle;

typedef DWORD  __declspec(dllimport) (*pfnGetChildren)(HANDLE VolumeHandle, PTCHAR szPath,vector <pCatalogChildItem> &versions);
typedef HANDLE __declspec(dllimport) (*pfnOpenFileCatalogFile)(TCHAR *in_wsProxyPassword);
typedef DWORD __declspec(dllimport) (*pfnGetArchiveDestinationVolumes)(PTCHAR szHostName, std::vector<PTCHAR> &VolumeList);
typedef void __declspec(dllimport)	(*pfnCloseFileCatalogFile) (HANDLE VolumeHandle);
typedef AFFILECATALOG_API DWORD (*FN_FREECATALOGITEMS)(vector <pCatalogChildItem> &versions);
typedef AFFILECATALOG_API DWORD (*FN_INITFILECATALOGMODULE)(FILE_CATALOG_INIT_PARAM &param);

extern pfnGetChildren pGetChildren;
extern pfnOpenFileCatalogFile pOpenArchiveFileCatalog;
extern pfnGetArchiveDestinationVolumes pGetArchiveDestinationVolumes;

extern WCHAR AFPurgeHostName[255];
BOOL AFPurgeGetHostName ();
DWORD ApplyPurgePolicy (HANDLE hArchiveVolumeHandle, wchar_t* szVolume, vector <pCatalogChildItem> *pvecCatalogList, long lPurgeDate);

// EXPORTS
AFPURGE_API DWORD AFPurgeEntry (PAFARCHIVEJOBSCRIPT* ppAFjobscript);