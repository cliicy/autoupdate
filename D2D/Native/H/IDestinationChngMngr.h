#pragma once
#include <windows.h>
#include "AFFileStoreGlobals.h"
#include "ArcCloudInterfaceExport.h"

//////////////File Catalog ///////////////////////////////
enum DEST_CHNG_ERROR
{
	/* Operation is Failed */
	DEST_ERROR_FAILED = -1,
	/* The operation is Success*/
	DEST_ERROR_SUCCESS = 0,
	/* Operation is Success and The Destination has already GUID Information */
	DEST_ALREADY_EXIST ,
	/* Operation is Success and the Destination has no GUID Information */
	DEST_NOT_EXIST,
	
};
/* Init Restoring */
typedef struct DestinationChngInit
{
	/*Only source file Dir */
	/*<D2DHomePath>\Catalog\machineName\E\TRemp\File.txt.mastermetadatafile. */
	/*<D2DHomePath>\Catalog\machineName\E\TRemp\File.txt.mastermetadatafile2.mastermetadatafile */
	/*E:\temp\file.txt*/
	TCHAR m_szSourceFileAbsolutePath[MAX_DIR_PATH_SIZE];
	/* Version Number reserved*/
	DWORD m_dwVersionNum;
	
}_DESTINATION_CHNG_INIT_;


typedef struct __declspec(novtable)  tagIDestinationChngMngt
{
	/*Index #01 */
	virtual BOOL InitDestinationChngMngt(/*_DESTINATION_CHNG_INIT_ & in__CATALOG_INIT_*/) = 0;
	virtual BOOL EndDestinationChngMngt() = 0;
	/* This API identifies whether GUID Folder exists or not. If exists it will return FALSE, 
	if not returns TRUE*/
	virtual DEST_CHNG_ERROR IsDestinationChanged(_GUID_HEADER_ & out__GUID_HEADER_)= 0;
	virtual DEST_CHNG_ERROR SetGUIDInformation(_GUID_HEADER_ & in__GUID_HEADER_) = 0;
}IDestinationChngMngt,*pIDestinationChngMngt;

/* 
1.This is the Handle of the estinationChngMngt.
2.This Handle is Thread Specific/File Specific all the IDestinationChngMngt interface 
function should be called on this pointer/reference.
*/
extern "C" AFSTOREAPI IDestinationChngMngt * InitDestinationChngMngt(/*_DESTINATION_CHNG_INIT_ & in__DESTINATION_CHNG_INIT_*/);
extern "C" AFSTOREAPI BOOL EndDestinationChngMngt(IDestinationChngMngt ** in_pIDestinationChngMngt);

