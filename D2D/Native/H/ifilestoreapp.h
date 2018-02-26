#pragma once
#include <windows.h>
#include "AFFileStoreGlobals.h"
#include "ArcCloudInterfaceExport.h"

/* This interface will currently creates new files */
/*
Note: Versioning will start with "1" for all the below mentioned files except for MasterMetaData
1. Creates or appends to the existing datafile which is uploaded/downloaded uisng CCI interface.
	1.1 New DataFile is created if the Encry/Compression Changes.
	FILE.TXT.[1...N]
2. Creates/Modifies MasterMeataData File. For all versions same file is maintained.
	FILE.TXT.MASTERMETADATA
3. Creates FileMetaData for each version
	FILE.TXT.[1..N].FILEMETADATA
4. Creates BlockAllocMetaData for each version
	FILE.TXT.[1..N].BLOCKALLOCMETADATA
*/
class AFSTOREAPI IFileStoreApp
{
public:
	/*Index #01 */
	virtual BOOL InitFileStore() = 0;
	virtual BOOL EndFileStore() = 0;
	/* Index #03 */
	virtual BOOL CreateFile(_WRITE_INIT_ & in_WriteInit) = 0;
	/*Index #03 */
	virtual BOOL InitStandardInfo(_STANDARD_INFO_ & in_StandardInfo) = 0;
	/*Index #04 */
	virtual BOOL EndStandardInfo()= 0;
	/* Index #05*/
	virtual BOOL InitBlockWrite(_FILE_METADATA_BLOCK_HEADER_ & in_BlockInfo) =0;
	/* Index #06*/
	virtual BOOL StartBlockWrite(_START_BLOCK_WRITE_ & in_StartBlockWrite) = 0;
	virtual BOOL EndBlockWrite() = 0;
	/* Index #07 */
	virtual BYTE * GetBuffer() =0;
	virtual BOOL WriteFile(BYTE * in_pBuffer,__int64 in_lFileSizeToWriteInBYTES,DWORD * in_pSourceBlocksList,DWORD in_dwSourceBlockCount)= 0;
	//virtual BOOL WriteFile(BYTE * in_pBuffer,__int64 in_lFileSizeToWriteInBYTES,_START_BLOCK_WRITE_ &) = 0;
	/* Index # Last Call */
	virtual BOOL CloseFile() = 0;
	virtual DWORD getErrorVal() = 0;
	virtual BOOL canContinueArchive(DWORD errorVal) = 0;

	//avikh01
	virtual BOOL WriteFileEx(BYTE * in_pBuffer,__int64 in_lFileSizeToWriteInBYTES,DWORD * in_pSourceBlocksList,DWORD in_dwSourceBlockCount, unsigned __int64& actualBytesWritten)=0;	
};
/* 
1.This is the Handle of the File Archive.
2.This Handle is Thread Specific/File Specific all the IFileStore interface 
function should be called on this pointer/reference.
*/
extern "C" AFSTOREAPI IFileStoreApp * InitFileStore();
extern "C" AFSTOREAPI BOOL EndFileStore(IFileStoreApp ** in_pIFileStoreApp);