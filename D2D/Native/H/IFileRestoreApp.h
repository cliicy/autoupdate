#pragma once

#include <windows.h>
#include <map>
#include "AFFilestoreGlobals.h"

using namespace std;
#define MAX_DIR_PATH_SIZE 512
#define RESTORE_NO_ERROR 0
#define RESTORE_FAILURE -1
#define RESTORE_NO_DATA 1
 
/* Init Restoring */
typedef struct RestoreInit
{
	/* RESTORE or MERGE */
	DWORD m_dwRestoreType;
	/*Only source file Dir */
	TCHAR m_szSourceFileAbsolutePath[MAX_DIR_PATH_SIZE];
	/* Version Number*/
	DWORD m_dwVersionNum;
	
}_RESTORE_INIT_;

class IFileRestoreApp
{
public:
	/* Index #01 */
	virtual BOOL InitFileRestore(_RESTORE_INIT_ & in__RESTORE_INIT_) = 0;
	/*Index #02 */
	virtual BOOL GetSTDinformation(/*TCHAR * in_pszFileName, DWORD in_dwVersion,*/_STANDARD_INFO_ & out__STANDARD_INFO_)= 0;
	/* Index #03 */
	virtual BOOL GetALLBlockHeaders(FileMetaDataBlockHeadersVec ** pout_FileMetaDataBlockHeadersMap)=0;
	/* Index #04 */
	virtual DWORD getNextFileFragment(OUT ULONG & clusterNum, OUT ULONG &clusterCount, OUT BYTE ** pBuffer)=0;
	virtual DWORD restoreDataInit(_BLOCK_HEADER_MAP_VALUE_ * p_BLOCK_HEADER_MAP_VALUE_,DWORD &out_dwBlockSize)= 0;
	virtual DWORD restoreDataDeInit()= 0;
	virtual BOOL CloseFile() = 0;
//avikh01
	virtual DWORD getNextFileFragmentEx(OUT ULONG & clusterNum, OUT ULONG &clusterCount, OUT BYTE ** pBuffer, ULONGLONG& actualBytesRead)=0;
	virtual void GetMetaDataVersion(DWORD& dwVersion) = 0;
};
/* 
1.This is the Handle of the File Restore.
2.This Handle is Thread Specific/File Specific all the IFileRestore interface 
function should be called on this pointer/reference.
*/
extern "C" AFSTOREAPI IFileRestoreApp * InitFileRestore(_RESTORE_INIT_ & in__RESTORE_INIT_);
extern "C" AFSTOREAPI BOOL EndFileRestore(IFileRestoreApp ** in_pIFileRestoreApp);