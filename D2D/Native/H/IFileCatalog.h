#pragma once
#include <windows.h>
#include "AFFileStoreGlobals.h"
#include "ArcCloudInterfaceExport.h"

//////////////File Catalog ///////////////////////////////

/* Init Restoring */
typedef struct CatalogInit
{
	/*Only source file Dir */
	/*<D2DHomePath>\Catalog\machineName\E\TRemp\File.txt.mastermetadatafile. */
	/*<D2DHomePath>\Catalog\machineName\E\TRemp\File.txt.mastermetadatafile2.mastermetadatafile */
	/*E:\temp\file.txt*/
	TCHAR m_szSourceFileAbsolutePath[MAX_DIR_PATH_SIZE*2];
	/* Version Number reserved*/
	DWORD m_dwVersionNum;

	
}_CATALOG_INIT_;


typedef struct __declspec(novtable)  tagIFileCatalog
{
	/*Index #01 */
	virtual BOOL InitFileCatalog(_CATALOG_INIT_ & in__CATALOG_INIT_) = 0;
	virtual BOOL EndFileCatalog() = 0;
	virtual BOOL GetVersionCount(DWORD * out_pdwVersionCount) = 0;
	virtual BOOL GetSTDInformation(TCHAR * in_pszFileName, DWORD in_dwVersion,_STANDARD_INFO_ & out__STANDARD_INFO_)= 0;
	virtual BOOL GetRealSourceFileAbsolutePath(wstring &out_wsRealSourceFileAbsolutePath)=0;
}IFileCatalog,*pIFileCatalog;


/* 
1.This is the Handle of the File Catalog.
2.This Handle is Thread Specific/File Specific all the IFileCatalog interface 
function should be called on this pointer/reference.
*/
extern "C" AFSTOREAPI IFileCatalog * InitFileCatalog(_CATALOG_INIT_ & in__CATALOG_INIT_);
extern "C" AFSTOREAPI BOOL EndFileCatalog(IFileCatalog ** in_pIFileCatalog);

