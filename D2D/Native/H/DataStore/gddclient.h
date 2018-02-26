#ifndef _GDDCLIENT_H_
#define _GDDCLIENT_H_



#if defined(WIN32)
#include <windows.h>
#elif defined(LINUX)
#include "DefWrapper.h"
#endif

#ifdef GDD_REPLICATION_OPTIMIZATION
#include "netcmd.h"
#endif //GDD_REPLICATION_OPTIMIZATION

#if defined(WIN32)

#ifdef GDDCLIENT_EXPORTS
#define API_EXPORTS __declspec(dllexport)
#else
#define API_EXPORTS __declspec(dllimport)
#endif

#elif defined(LINUX)
#define API_EXPORTS __attribute__ ((visibility ("default")))
#endif


typedef VOID (*WRITELOG)(UINT32 , CHAR* pszLog, ...);
typedef VOID (*WRITELOGW)(UINT32 , WCHAR* pszLog, ...);

//@@ the following define is used for GDDOpenFile Flag parameter
//do backup job, create a new file
#define GDD_CREATE_NEW 1 
//do restore job, open one existed file
#define GDD_OPEN_EXIST 2
//do merge job, open merge destination file
#define GDD_MERGE_DATA 3
#define GDD_MERGE_DELETE 4 
//don't used at current time
#define DEDUPE 5
// define just for ensure no compile error, this will be removed after he,zhe merge code afstore code
#define GDD_MERGE		6

//we now, make fast purge interface as GDDClient API style: Open-->Purge-->Close,Not Just one API to do all things. 
//This new style give upper level more control. if new feature want to use purge function, try to use this.
// not use
//API_EXPORTS INT32 GDDDeleteFile(DWORD* pFileNo,HANDLE hDataStorCfg);
//API_EXPORTS INT32 GDDDeleteFiles(DWORD* pFileNOs, UINT32 cbFileNo, HANDLE hDataStorCfg);
#define GDD_PURGE_SESSION_INDEX (7)

#define GDD_FLAG_OLD_SYNC_READ	0   //old sync read mode, support backup job copy-on-write 
#define GDD_FLAG_OLD_PRE_READ	1	//old per read mode, support replication job
#define GDD_FLAG_NEW_ASYNC_READ	2	//new async read mode, support restore and catalog job
#define GDD_FLAG_NEW_FULLY_ASYNC_READ 3	//fully async read mode, support ASBU lite integration and copy recovery point job

extern int (*g_getErrMsgFromCommLib)(int error_code, wchar_t* pBuffer, int bufferSize);


typedef struct stInitInfo
{
	char GDDServerIp[64];   //the primary server ip address
	INT32 GDDServerPort;     //the primary server listen port
	INT32 nCommTimeout;      //re connect to server time out value when network error happened
    PBYTE pDataStoreConfig; // Configuration items of current data store  

	// for backup, wzDataStore1Name is name of destination data store, 
	// for restore, catalog, merge wzDataStore1Name is name of source data store
	// and wzDataStore2Name is NULL.
	// for replication, wzDataStore1Name is name of source data store, 
	// wzDataStore2Name is destination data store.
	// so we care both wzDataStore1/2Name for replication, other jobs we care only 
	// about wzDataStore1Name
	wchar_t wzDataStore1Name[MAX_PATH];
	wchar_t wzDataStore2Name[MAX_PATH];

	// we care both RPS1/2HostName for replication, other jobs we care only 
	// about RPS1HostName
	wchar_t wzRPS1HostName[MAX_PATH];	
	wchar_t wzRPS2HostName[MAX_PATH];	

	GUID CallerGuid;
	UINT32 uCallerJobType;
	void *pfnActLogCallback;
	char *CallbackInfo;
	DWORD dwCallbackInfoLen;
	char GDDServerIp_WAN[64];
}INITINFO,*PINITINFO;

typedef struct _GDDJobStatistic
{
	UINT64	TotalDataSize;
	UINT64  UniqueDataSize;
	UINT64  CompressedDataSize;
	DWORD	m_dwDataIsVaild; //0 for Invalid, 1 for valid
	
	_GDDJobStatistic()
	{
		TotalDataSize = 0;
		UniqueDataSize = 0;
		CompressedDataSize = 0;
		m_dwDataIsVaild = 0;
	}

}GDDJOBSTAT, *PGDDJOBSTAT;

typedef struct _st_PurgeStatusDataPara
{
	enum _en_OPCode
	{
		STATUS_OP_INVALID,
		STATUS_OP_QUERY,
		STATUS_OP_FORCERECLAIM_START
	};
	_en_OPCode  status_op;
	INT32		m_StatusBufLen;
	char*		m_pStatusData;
}PURGESTATUSDATAPARA, *PPURGESTATUSDATAPARA;


typedef struct  _st_PurgeGDDIndexFileAPara
{
	HANDLE	m_hServerContext;
	UINT32	ui32PurgeFlag;
	UINT32	m_nFileCount;
	UINT32*	m_arrayOfFileNo;
}PURGEGDDINDEXFILEAPARA,*P_PURGEGDDINDEXFILEAPARA;

// danri02: Dedupe Merge optimal, @2013-06, code begin
typedef struct _DedupMergeEntry
{
	INT64   i64OffsetDest;
	INT32    lMoveMethodDest; 
	INT32    lSrcFileIndex;
	INT64   i64OffsetSrc;
	INT32    lMoveMethodSrc; 
}DedupMergeEntry,*PDedupMergeEntry;

typedef struct _DedupMergeChunk
{
	unsigned int		uiClusterSize;
	unsigned int		uiEntryUnitDataSize;
	unsigned int		uiEntryNum;
	unsigned int		uiEntryBufSize;
	UINT64	            ui64BytesMerged;
	PDedupMergeEntry	pMergeEntry;
}DedupMergeChunk,*PDedupMergeChunk;
// danri02: Dedupe Merge optimal, @2013-06, code end

#ifdef GDD_ANSYNC_READ
#pragma pack(1)
typedef struct _GDDBLOCKINFO_1
{
	DWORD nFileNo;
	UINT64 ui64BlockNoInGDDFile;
}GDDBLOCKINFO_1, *P_GDDBLOCKINFO_1;

typedef struct _GDDBLOCKINFO_1_LIST
{
    P_GDDBLOCKINFO_1 pClusterInfoList;
    DWORD dwClusterInfoListLen;
    DWORD dwClusterInfoListCurPos;
}GDDBLOCKINFO_1_LIST, *PGDDBLOCKINFO_1_LIST;
#pragma pack()
#endif //GDD_ANSYNC_READ

#ifdef __cplusplus
extern "C"{
#endif

/****************
GDD client initial server API, this API is designed to support multiple primary (REPLICATION)
return value: 
success, a valid handle identify the primary server; failed, INVALID_HANDLE_VALUE(-1).
*****************/
API_EXPORTS HANDLE GDDInitServer(PINITINFO pInitInfo, DWORD *dwRet);

/****************
GDD client de-initial server API, this API is designed to support multiple primary
return value: 
success, OK; failed, other value.
*****************/
API_EXPORTS INT32 GDDDeInitServer(HANDLE hServer);


/****************
this API is used to create or open one file on the GDD server

DWORD* pFileNo:
if create one new file  one the server, the pFileId will be output parameter, 
if open one existed file  one the server, the pFileId will be input parameter, the file id must be existed in the server

UINT64 nFlag:
if create one new file  one the server, the flag will be GDD_CREATE_NEW
if open one existed file  one the server, the flag will be GDD_OPEN_EXIST

HANDLE hServer:
the server handle returned from GDDInitServer

return value: 
success, a valid handle value; failed, INVALID_HANDLE_VALUE(-1).
*****************/
API_EXPORTS HANDLE GDDOpenFile(DWORD* pFileNo, UINT64 nFlag,HANDLE hServer, DWORD *dwRet);


/****************
this API is used to write data to the GDD server

HANDLE hFile:
the file handle return by GDDOpenFile

char *pData:
the data buffer to be written
INT32 nToWrite:
the data length to be written
INT32* pWritten:
the real length be written

return value: 
success, 0; failed, the other value.
*****************/
API_EXPORTS INT32 GDDWriteFile(HANDLE hFile, char *pData, INT32 nToWrite, INT32* pWritten);

#ifdef GDD_REPLICATION_OPTIMIZATION
API_EXPORTS INT32 GDDWriteFileByBlockDesc(HANDLE hFile, PGDDBLOCKDESC pBlockDesc, INT32 nBlocksToWrite);
#endif //GDD_REPLICATION_OPTIMIZATION


/****************
this API is used to set the file pointer on the GDD server

HANDLE hFile:
the file handle return by GDDOpenFile

UINT64 nOffset:
the data offset to be seek

UINT64 *pRealOffset:
the real file pointer seeked

INT32 Method
the seek method, FILE_BEGIN,FILE_CURRENT,FILE_END

return value: 
success, 0; failed, the other value.
*****************/
API_EXPORTS INT32 GDDSeekFile(HANDLE hFile, INT64 nOffset, UINT64 *pRealOffset,INT32 Method);

/****************
this API is used to read data from the GDD server

HANDLE hFile:
the file handle return by GDDOpenFile

char *pData:
the data buffer to be readed
INT32 nDataLen:
the data length to be readed
INT32* pWritten:
the real length be readed

return value: 
success, 0; failed, the other value.
*****************/
API_EXPORTS INT32 GDDReadFile(HANDLE hFile, char *pData, INT32 nToRead, INT32 *pReaded);







/****************
this API is used to create or open many incremental files on the GDD server for D2D restore

DWORD* pFileNo:
open many existed file  one the server, the pFileId will be input parameter, the file id must be existed in the server

INT32 nFiles:
files count

HANDLE hServer:
the primary server handle  

return value: 
success, a valid handle value; failed, INVALID_HANDLE_VALUE(-1).
*****************/
API_EXPORTS HANDLE GDDOpenFileEx(DWORD* pFileNo, INT32 nFiles, HANDLE hServer, DWORD dwFlag, DWORD *dwRet);

/****************
this API is used to set the file pointer on the GDD server for D2D restore

HANDLE hFile:
the file handle return by GDDOpenFile

 DWORD nFileNo:
the incremental file id

UINT64 nOffset:
the data offset to be seek

UINT64 *pRealOffset:
the real file pointer seeked

INT32 Method
the seek method, FILE_BEGIN,FILE_CURRENT,FILE_END

return value: 
success, 0; failed, the other value.
*****************/
API_EXPORTS INT32 GDDSeekFileEx(HANDLE hFile, DWORD nFileNo, INT64 nOffset, UINT64 *pRealOffset,INT32 Method);

/****************
this API is used to read data from the GDD server

HANDLE hFile:
the file handle return by GDDOpenFile

 DWORD nFileNo:
the incremental file id

char *pData:
the data buffer to be readed
INT32 nDataLen:
the data length to be readed
INT32* pWritten:
the real length be readed

return value: 
success, 0; failed, the other value.
*****************/
API_EXPORTS INT32 GDDReadFileEx(HANDLE hFile, DWORD nFileNo,char *pData, INT32 nToRead, INT32 *pReaded);

#ifdef GDD_ANSYNC_READ
API_EXPORTS INT32 GDDReadFiles(HANDLE hGDDClient, P_GDDBLOCKINFO_1 pBlockInfo1List, DWORD dwBlockCount, char *pDataBuf);
API_EXPORTS INT32 GDDReadCancel(HANDLE hGDDClient);
API_EXPORTS INT32 GDDReadReset(HANDLE hGDDClient);
#endif //GDD_ANSYNC_READ

/****************
this API is used to close file on the GDD server

HANDLE hFile:
the file handle return by GDDOpenFile


PGDDJOBSTAT pStat:
the job status

return value: 
success, 0; failed, the other value.
*****************/
API_EXPORTS INT32 GDDCloseFileEx(HANDLE hFile,PGDDJOBSTAT pStat = NULL);

//hefzh01
#if GDD_ANSYNC_READ
API_EXPORTS HANDLE NewGDDOpenFile(DWORD* pFileNo, long nFiles, HANDLE hServer, DWORD m_dwNumOfBlocksPerIdxRead, DWORD *dwRet);
API_EXPORTS long NewGDDReadFile(HANDLE hFile, P_GDDBLOCKINFO_1* ppBlockInfo1List, DWORD dwBlockCount, char *pDataBuf, BOOL bPreRead = FALSE);
API_EXPORTS long NewGDDCloseFile(HANDLE hFile);
API_EXPORTS long NewGDDSyncPerRead(HANDLE hFile);
#endif

//hefzh01: Cancel write data
API_EXPORTS INT32 GDDWriteCancel(HANDLE hGDDClient);

/****************
this API is used to open source files to merge

hFile
[in] the destination file handle returned by GDDOpenFile

pFileIdArray:
[in] Pointer to an array of source files to merge.

nNumOfFiles:
[in] Number of source files.

pDestFileSize:
[out] Size of merge destination file.

return value: 
success, 0; failed, the other value.
*****************/
API_EXPORTS INT32 GDDOpenFilesToMerge(HANDLE hFile, DWORD* pFileNoArray, INT32 nNumOfFiles, UINT64* pDestFileSize = NULL);

/****************
this API is used to merge blocks between source and destination file

hFile
[in] the file handle returned by GDDOpenFile

nOffsetDest
[in] Number of bytes to move the destination file pointer. 
     A positive value moves the pointer forward in the file and a negative value moves the file pointer backward.

MoveMethodDest
[in] Starting point for the destination file pointer move. This parameter can be one of the following values.
        Value	        Meaning
        FILE_BEGIN	    The starting point is zero or the beginning of the file.
        FILE_CURRENT	The start point is the current value of the file pointer.
        FILE_END	    The starting point is the current end-of-file position.

nSrcFileIndex
[in] Index of source file in file ID array.

nOffsetSrc
[in] Number of bytes to move the source file pointer. 
     A positive value moves the pointer forward in the file and a negative value moves the file pointer backward.

MoveMethodSrc
[in] Starting point for the source file pointer move. This parameter can be the same values as MoveMethodDest.

nBytesToMerge
[in] Total bytes to merge from source file to destination file. It should be block size aligned.

nBytesMerged
[out] Total bytes merged from source file to destination file.

pbFileEnd
[out] tell the upper application it is reached the file end

return value: 
success, 0; failed, the other value.
*****************/
API_EXPORTS INT32 GDDMergeFile(
                              HANDLE hFile,
                              INT64 nOffsetDest,
                              INT32 MoveMethodDest,
                              INT32 nSrcFileIndex,
                              INT64 nOffsetSrc,
                              INT32 MoveMethodSrc, 
							  UINT64  nBytesToMerge,
							  UINT64* pBytesMerged,
							  BOOL *pbFileEnd
                              );
// danri02: Dedupe Merge optimal, @2013-06, code begin
API_EXPORTS INT32 GDDMergeFileByChunk(HANDLE hFile,PDedupMergeChunk pMergeChunk);
// danri02: Dedupe Merge optimal, @2013-06, code end
API_EXPORTS INT32 GDDMergePreClose(HANDLE hFile);

API_EXPORTS INT32 GDDMergeDeleteOldSession(HANDLE hFile, DWORD* pFileNoArrayToBeDeleted, INT32 nNumOfFiles);

/****************
this API is used to close file on the GDD server

HANDLE hFile:
the file handle return by GDDOpenFile

return value: 
success, 0; failed, the other value.
*****************/
API_EXPORTS INT32 GDDCloseFile(HANDLE hFile,PGDDJOBSTAT pStat = NULL);

/****************
this API is used to delete the file on the GDD server

DWORD nFileNo:
the file id on the GDD server
HANDLE hDataStorCfg:
Is the data store configure

return value: 
success, 0; failed, the other value.
*****************/
API_EXPORTS INT32 GDDDeleteFile(DWORD* pFileNo,HANDLE hDataStorCfg);
API_EXPORTS INT32 GDDDeleteFiles(DWORD* pFileNOs, UINT32 cbFileNo, HANDLE hDataStorCfg);
API_EXPORTS INT32 GDDOPPurgeStatus(HANDLE hDataStoreCfg, PPURGESTATUSDATAPARA pPurgeStatusDataPara);
API_EXPORTS INT32 GDDDeleteFileEx(HANDLE hServer,DWORD* pFileNo);

API_EXPORTS INT32 GDDPurgeSessions(P_PURGEGDDINDEXFILEAPARA pPurgeSessionContext);
/****************
this API is used to redirect the gddclient log to the upper application log

WRITELOG pWriteLog:
the upper log function pointer

*****************/
API_EXPORTS void GDDSetDebug(WRITELOG pWriteLog);

/****************
this API is used to get the session level dedupe ratio at the end
of backup job, but before call GDDCloseFile
HANDLE: the handle is returned by call GDDOpenFile()
return Value:
success, 0; fail, the other value
*****************/

API_EXPORTS INT32 GDDGetJobStat(HANDLE hFile,PGDDJOBSTAT pJobStat);

#if 1
//hefzh01: Move to HashMergeTree module

//////////////////////////////////////////////////////////////////////////
//Get the hash information of a dedupe data store. The API is used for stopped data store, 
//it will analyze the hash data header in the input hash path, get SSD/Memory information.
//Parameters:	lpHashPath: [in] Hash path of a dedupe data store;
//				stHashStatus: [out] the hash information of the dedupe data store.
//Return value: 0, success; other is error code.
//
#ifdef GDD_REPLICATION_OPTIMIZATION
API_EXPORTS DWORD GetDedupeDSHashStatus(LPCTSTR lpHashPath, HASH_STATUS& stHashStatus);
#endif
//////////////////////////////////////////////////////////////////////////
//The API is used when change memory/SSD type, when data store is stopped. 
//If the input dwStoreMode is same to the mode in the hash path(lpHashPath), it will return current used memory/SSD size; 
//if the input dwStoreMode is different to the mode in the hash path(lpHashPath), 
//the API will calculate the preferred minimum memory/SSD size for this change. 
//Parameters:   lpHashPath: [in] Hash path of a dedupe data store;
//				dwStoreMode: [in] the mode of hash path;
//				i64PreMinMemorySize: [out] current memory size when mode is same to hash path; or preferred minimum memory size when data store mode is different;
//				i64PreMinSSDSize: [out] current SSD size when mode is same to hash path; or preferred minimum SSD size when data store mode is different, the parameter is ignored when mode is memory;
//Return value: 0, success; others is error code.
//
API_EXPORTS DWORD GetPreferDSHashStatus(LPCTSTR lpHashPath, DWORD dwStoreMode, INT64& i64PreMinMemorySize, INT64& i64PreMinSSDSize);
#endif

#ifdef __cplusplus
}
#endif

typedef HANDLE (*GDDINITSERVER)(PINITINFO pInitInfo, DWORD *let);

typedef INT32 (*GDDDEINITSERVER)(HANDLE hServer);

typedef HANDLE (*GDDOPENFILE)(DWORD* pFileNo, UINT64 nFlag, HANDLE hServer, DWORD *let);

typedef INT32 (* GDDWRITEFILE)(HANDLE hFile, char *pData, INT32 nToWrite, INT32* pWritten);

#ifdef GDD_REPLICATION_OPTIMIZATION
typedef INT32 (* GDDWRITEFILEBYBLOCKDESC)(HANDLE hFile, PGDDBLOCKDESC pGddBlockDesc, INT32 nGddBlockCount);
#endif //GDD_REPLICATION_OPTIMIZATION

typedef INT32 (* GDDSEEKFILE)(HANDLE hFile,UINT64 nOffset,UINT64 *pRealOffset,INT32 Method);

typedef INT32 (* GDDREADFILE)(HANDLE hFile, char *pData, INT32 nToRead, INT32 *pReaded);

typedef INT32 (* GDDOPENFILESTOMERGE)(HANDLE hFile, DWORD* pFileNoArray, INT32 nNumOfFiles, UINT64* pDestFileSize);

typedef INT32 (* GDDMERGEFILE)(
                              HANDLE hFile,
                              INT64 nOffsetDest,
                              INT32 MoveMethodDest,
                              INT32 nSrcFileIndex,
                              INT64 nOffsetSrc,
                              INT32 MoveMethodSrc, 
                              UINT64 nBytesToMerge,
							  UINT64* pBytesMerged,
							  BOOL *pbFileEnd
                              );
// danri02: Dedupe Merge optimal, @2013-06, code begin
typedef INT32 (*GDDMERGEFILEBYCHUNK)(HANDLE hFile,PDedupMergeChunk pMergeChunk);
typedef INT32 (*GDDDELETEFILES)(DWORD* pFileNOs, UINT32 cbFileNOs, HANDLE hDataStorCfg);
typedef INT32 (*GDDPURGESTATUSOP)(HANDLE hDataStoreCfg, PPURGESTATUSDATAPARA pPurgeStatusDataPara);
typedef INT32 (*GDDPURGESESSIONS)(P_PURGEGDDINDEXFILEAPARA pPurgeSessionContext);
// danri02: Dedupe Merge optimal, @2013-06, code end

typedef INT32 (* GDDMERGEPRECLOSE)(HANDLE hFile);
typedef INT32 (* GDDMERGEDELETEOLDSESSION)(HANDLE hFile, DWORD* pFileNoArrayToBeDeleted, INT32 nNumOfFiles);

typedef INT32 (* GDDCLOSEFILE)(HANDLE hFile,PGDDJOBSTAT pStat);

typedef INT32 (* GDDDELETEFILE)(DWORD* pFileNo, HANDLE hDataStorCfg);
typedef INT32 (* GDDDELETEFILEEX)(HANDLE hServer,DWORD* pFileNo);

typedef void (*GDDHANDLESTATISTICSMSG)(GDDJOBSTAT& jobStat, UINT64 jobHandle);

typedef VOID (* GDDSETDEBUG)(WRITELOG pWriteLog);
typedef INT32 (*GDDGETJOBSTAT)(HANDLE hFile,PGDDJOBSTAT pJobStat);

#ifdef GDD_REPLICATION_OPTIMIZATION
typedef INT32 (*GDDENABLEREPREADOPTIMIZATION)(HANDLE hFile);
typedef INT32 (*GDDENABLEREPWRITEOPTIMIZATION)(HANDLE hFile, void * pvGDDReader);
#endif //GDD_REPLICATION_OPTIMIZATION

typedef HANDLE (*GDDOPENFILEEX)(DWORD* pFileNo, INT32 nFiles,HANDLE hServer, DWORD dwFlag, DWORD *let);
typedef INT32 (*GDDSEEKFILEEX)(HANDLE hFile, DWORD nFileNo, INT64 nOffset,UINT64 *pRealOffset,INT32 Method);
typedef INT32 (*GDDREADFILEEX)(HANDLE hFile, DWORD nFileNo,char *pData, INT32 nToRead, INT32 *pReaded);
#ifdef GDD_ANSYNC_READ
typedef INT32 (*GDDREADFILES)(HANDLE hGDDClient, P_GDDBLOCKINFO_1 pBlockInfo1List, DWORD dwBlockCount, char *pDataBuf);
typedef INT32 (*GDDREADCANCEL)(HANDLE hGDDClient);
typedef INT32 (*GDDREADRESET)(HANDLE hGDDClient);
#endif //GDD_ANSYNC_READ
//hefzh01
#if GDD_ANSYNC_READ
typedef HANDLE (*NEWGDDOPENFILE)(DWORD* pFileNo, long nFiles,HANDLE hServer, DWORD m_dwNumOfBlocksPerIdxRead, DWORD *let);
typedef long (*NEWGDDREADFILE)(HANDLE hGDDClient, P_GDDBLOCKINFO_1* ppBlockInfo1List, DWORD dwBlockCount, char *pDataBuf, BOOL bPreRead);
typedef long (*NEWGDDCLOSEFILE)(HANDLE hFile);
typedef long (*NEWGDDSYNCPERREAD)(HANDLE hFile);
#endif //GDD_ANSYNC_READ
typedef INT32 (*GDDCLOSEFILEEX)(HANDLE hFile,PGDDJOBSTAT pStat);
typedef INT32 (*GDDGETJOBSTAT)(HANDLE hFile,PGDDJOBSTAT pJobStat);

typedef INT32(*GDDWRITECANCEL)(HANDLE hGDDClient);

#define GDDCLIENT_CALLER_ARCSERVE_BACKUP \
        { 0x94779599, 0x900b, 0x4ffb, { 0x8f, 0xe1, 0x5e, 0x8c, 0x87, 0x59, 0xb4, 0x89 } }

#define GDDCLIENT_CALLER_ARCSERVE_D2D \
        { 0xd22cc0d0, 0x26d7, 0x4536, { 0xaf, 0xd2, 0xfc, 0xfb, 0xdb, 0x59, 0xa4, 0x38 } }

#endif
