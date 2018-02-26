#ifndef __ASDBAPI_H
#define __ASDBAPI_H

#include <brstruct.h>
#include <asdb.h>
#include <asdbntdf.h>
#include <unidb.h>
#include <cstool.h>
#include <ctf.h>
#include <cheyprod.h>

#include "bebenabled.h"

#ifdef TASK_ENHANCEMENT_FOR_BAB_DATABASE_CATALOG_PROTECTION
#include <LMCons.h>
#endif

#ifdef __cplusplus            
  extern "C" {                     /* avoid name-mangling if used from C++ */
#endif /* __cplusplus */

// MikeT
/*****************************************************************************
***               METHOD FOR CENTRAL DATABASE
***
Summary = Tape, Session, Job, Pool
Full    = Summary + Detail
------------------------------------------------------------------------------
| ID | Description              | Kepp  | Detail to | Others                 |  
|    |                          | Local | Central   |                        |
|----------------------------------------------------------------------------|
| 0  | No Central, Local only   |       |           | Pool depends on Local  |
|----------------------------------------------------------------------------|
| 1  | Summary in Central Only  |       |           | Pool depends on Central|
|----------------------------------------------------------------------------|
| 2  | No Local, Full Central   |       |    X      | Pool depends on Central|
|----------------------------------------------------------------------------|
| 3  | Local, Summary in Central|   X   |           | Pool depends on Local  |
|----------------------------------------------------------------------------|
| 4  | Local, Full Central      |   X   |    X      | Pool depends on Local  |
|----------------------------------------------------------------------------|

******************************************************************************/





#ifndef RDSBASIC_H
// Common typedefs for prototypes 
//typedef unsigned short RDM_SESS;   // Session handle 
//typedef unsigned long  RDM_DB;     // Database handle
#endif

#define ASDB_SUCCESS 0
#define ASDB_NODATA  100
#define ASDB_ERROR	 -1

//new API for normalization
// return non-zero if successful
BOOL ASDB_IsRunning(char *pComputerName);
HANDLE ASDB_GetDBSemaphoreHandle();
void ASDB_Crypt(void * pBuffer, ULONG ulSize);

// return 0 if successful.
short ASDB_InitRPCServer();
short ASDB_DeinitRPCServer();

//shada07 2008-11-17
//Currently, ASDB_SetGlobalOptions is used to set the value of time out for ODBC APIs.
//[in]: pComputerName - Reserved, NULL is expected.
//[in]: option - the type of option
//[in]: value - the value of option
//Return value: NO_ERROR - if successful.
//              ERROR_INVALID_PARAMETER - if failed
short ASDB_SetGlobalOption(char *pComputerName, ASDB_GlobalOptions option, void *value);

short ASDB_Login(PASDB_SESS phSESS);
short ASDB_LoginToComputer(char *pComputerName,PASDB_SESS phSESS);
short ASDB_LoginToComputerWriteMode(char *pComputerName,PASDB_SESS phSESS);
short ASDB_Logout(PASDB_SESS phSESS);
short ASDB_ConnectToDBServer(char *pComputerName, PASDB_SESS phSESS);
short ASDB_DisconnectFromDBServer(PASDB_SESS phSESS);
short ASDB_ShutDownServer();
char *  ASDB_GetPrimaryName(char *  sBuf, short length, DWORD *method);
//DWORD ASDB_SetCentralDBInfo(char * machine, PSERVER_ACCOUNT_INFO pAccInfo);
//DWORD ASDB_GetCentralDBInfo(char * machine, PSERVER_ACCOUNT_INFO pAccInfo);
long  ASDB_SetPrimaryDBInfo(char * machine, 
                           long machineType,                  // in
                           char * user,                       // in
                           char * pwd,                        // in
                           PSERVER_ACCOUNT_INFO_EX pdbInfo);  // in
long  ASDB_GetPrimaryDBInfo(char * machine, 
                           long machineType,                  // in
                           char * user,                       // in
                           char * pwd,                        // in
                           PSERVER_ACCOUNT_INFO_EX pdbInfo);  // in
short ASDB_CleanDBHandle(PASDB_SESS phSESS);
BOOL ASDB_IsConnected(char * netDrive);
//short ASDB_Open(PASDB_SESS phSESS, short nameid, short modeid, DWORD *hDB);
//short ASDB_Close(DWORD hDB);

short ASDB_GetDiskSpace(PASDB_SESS phSESS,char *pDrive,double *pdbTotal,double *pdbOthers,double *pdbDB,double *pdbFree);
PCOMPUTERNAME ASDB_GetComputerList(PASDB_SESS phSESS,UINT *Cnt);

short ASDB_GetMediaProperty3(PASDB_SESS phSESS,char *pTapeName,short RandomID,short SeqNum,PASDB_TapeRec pASMedia);
char  *ASDB_GetString(PASDB_SESS phSESS,unsigned long ulID,char *lpString,unsigned short sBufSize);
char  *ASDB_GetRestoreString(PASDB_SESS phSESS,unsigned long ulID,char *lpString,unsigned short sBufSize, char * seperator);
char  *ASDB_GetSessionString(PASDB_SESS phSESS,unsigned long ulID,char *lpString,unsigned short sBufSize);
char  *ASDB_GetHostString(PASDB_SESS phSESS,unsigned long ulID,char *lpString,unsigned short sBufSize);
char  *ASDB_GetVolumeString(PASDB_SESS phSESS,unsigned long ulID,char *lpString,unsigned short sBufSize);
char  *ASDB_GetHostStringBySession(PASDB_SESS phSESS,unsigned long ulID,char *lpString,unsigned short sBufSize);
unsigned long ASDB_GetStringIDOnly(PASDB_SESS phSESS, char *lpString);
unsigned long ASDB_GetHostStringIDOnly(PASDB_SESS phSESS, char *lpString);
unsigned long ASDB_GetStringID(PASDB_SESS phSESS, char *lpString);
char  *ASDB_GetName(PASDB_SESS phSESS,unsigned long ulID,char *lpString,unsigned short sBufSize);
unsigned long ASDB_GetNameIDOnly(PASDB_SESS phSESS,char *lpName);
unsigned long ASDB_GetNameID(PASDB_SESS phSESS,char *lpName);
#ifdef BEB_CATALOG_DB
char  *ASDB_GetNameFromSession(PASDB_SESS phSESS, long SessionID, unsigned long ulID,char *lpString,unsigned short sBufSize);
short ASDB_CATLOGDB_QueryFirst(PASDB_SESS phSESS, char *  sComputerName, char *  sDir, BOOL bCaseSensitive,
									  BOOL bIncludeSubDir, char *pattern, unsigned long *handle);
short ASDB_CATLOGDB_QueryFirstEx(PASDB_SESS phSESS, char *  sComputerName, char *  sDir, BOOL bCaseSensitive,
									  BOOL bIncludeSubDir, unsigned long begin_sesstime, unsigned long end_sesstime, char *pattern, unsigned long *handle);
short ASDB_CATLOGDB_QueryNext(PASDB_SESS phSESS, unsigned long handle, DWORD nRequest, PASDB_DetailExtRec *pExt, DWORD *nFound);
short ASDB_CATLOGDB_QueryNextEx(PASDB_SESS phSESS, unsigned long handle, DWORD nRequest, PASDB_DetailExtRecEX *pExt, DWORD *nFound);
short ASDB_CATLOGDB_QueryClose(PASDB_SESS phSESS, unsigned long handle);
#endif

PNAMELIST ASDB_GetNameList(PASDB_SESS phSESS, PNAMELIST pNameList,unsigned long *nItems);


PVERSIONDATA ASDB_GetVersionHistory(PASDB_SESS phSESS, PTAPEDATA lpData,UINT *Cnt);
PVERSIONDATA ASDB_GetTapeSessionInfo(PASDB_SESS phSESS,PTAPEDATA lpData);


short ASDB_GetDevTotalErrNumByID(PASDB_SESS phSESS, ASDB_ADDR DeviceID, long * TotalErrors);
short ASDB_GetTotalFilesInSessionBySesID(PASDB_SESS phSESS, long sesID, DWORD* TotalFiles);
short ASDB_GetDeviceRecByAddr(PASDB_SESS phSESS, DWORD dbAddr, PASDB_TapeDrvRec tDRec);
short ASDB_DeleteDeviceList(PASDB_SESS phSESS, DWORD *addrList, DWORD nCnt);
short ASDB_GetSessionAndTapeRecBySesID(PASDB_SESS phSESS, long SessionID, PASDB_SessRec sRec, PASDB_TapeRec tRec);
short ASDB_GetLastJobID(PASDB_SESS phSESS, DWORD *jobID);
short ASDB_DeleteJobList(PASDB_SESS phSESS, DWORD *addrList, DWORD nCnt);
short ASDB_DeleteJobListEx(PASDB_SESS phSESS, DWORD **addrList, DWORD nCnt);
short ASDB_GetJobRecByID(PASDB_SESS phSESS, DWORD jobID, PASDB_JobRec jRec);
short ASDB_GetSessionAndTapeRecByJobID(PASDB_SESS phSESS, long JobID, PASDB_SessRec sRec, PASDB_TapeRec tRec);
short ASDB_GetSesAndSrcpathBySesID(PASDB_SESS phSESS, long SessionID, PASDB_SessRec sRec, char *  path, short len);
short ASDB_GetTapeMediaRecByTapeID(PASDB_SESS phSESS, long TapeID, PASDB_TapeRec tRec);
short ASDB_GetTapeFirstAndLastKey(PASDB_SESS phSESS, DWORD * firstID, DWORD * lastID);
short ASDB_GetTapeRecord(PASDB_SESS phSESS, char * pTapeName,short randomID,short seqnum, PASDB_TapeRec ptRec);
short ASDB_GetTapeRecordList(PASDB_SESS			 phSESS,			// IN
							 TapeRecList		 tapeRecList,		// IN
							 PASDB_TapeRecExList ASDB_tapeRecList); // OUT
short ASDB_DBCleanSession(PASDB_SESS phSESS,LONG JobID, long SesID,DWORD *pdwDeleted);
short ASDB_AddDetailList(PASDB_SESS phSESS, PASDB_DetailExtRec FTS, UINT nFTS, short *pdstat);
short ASDB_DBCleanUp(PASDB_SESS phSESS, DWORD *pdwDeleted);
short ASDB_CleanUpDetailByTapeIDSesNum(PASDB_SESS phSESS, DWORD tapeID, DWORD SesNum, PASDB_SessRec psDBRec);
short ASDB_CleanUpMBO(PASDB_SESS phSESS, DWORD *pdwDeleted, PASDB_MBOTapeName * pMBOTapeList);


//APIs that use callback function
//short ASDB_GetTapeMediaRecList(PASDB_SESS phSESS, PVOID pvData, PROCESS_LIST_FUNCTION_PTR funcPtr);
short ASDB_GetTapeMediaRecList(PASDB_SESS phSESS, BOOL bFirst, PASDB_TapeRec * pTapeList, UINT * nCnt);
//short ASDB_GetJobRecList(PASDB_SESS phSESS, PVOID pvData, PROCESS_LIST_FUNCTION_PTR funcPtr);
short ASDB_GetJobRecList(PASDB_SESS phSESS, BOOL bFirst, PASDB_JobRec *pjDBRec, UINT * nCnt);
//short ASDB_GetSessRecList(PASDB_SESS phSESS, long JobTapeID, BOOL bType, PVOID pvData, PROCESS_LIST_FUNCTION_PTR funcPtr);
short ASDB_GetSessRecList(PASDB_SESS phSESS, long JobTapeID, BOOL bType, PASDB_SessRec *pdbSRec, UINT * nCnt, BOOL bFirst);
//short ASDB_GetTapeErrList(PASDB_SESS phSESS, DB_ADDR DeviceID, PVOID pvData, PROCESS_LIST_FUNCTION_PTR funcPtr);
short ASDB_GetTapeErrList(PASDB_SESS phSESS, ASDB_ADDR DeviceID, PASDB_TapeErrRec *pData, UINT * nCnt);
//short ASDB_GetSessionDetailListBySesID(PASDB_SESS phSESS, long sesID, PVOID pvData, PROCESS_LIST_FUNCTION_PTR funcPtr);
short ASDB_GetSessionDetailListBySesID(PASDB_SESS phSESS, long sesID, BOOL bFirst, PASDB_DetailExtRec * pData, UINT * nCnt);
//short ASDB_GetDetailListByAddrList(PASDB_SESS phSESS, PDWORD AddrList, DWORD nCnt, PVOID pvData, PROCESS_LIST_FUNCTION_PTR funcPtr);
short ASDB_GetDetailListByCompoundKeyList(PASDB_SESS phSESS, PASDB_DetailCompoundKey KeyList, DWORD nCnt, PASDB_DetailRec *pExt);
//short ASDB_SQLFindFileAddrList(PASDB_SESS phSESS, char *  sPattern, char *  sHost, BOOL bCaseSensitive, char *  DefStr, PDWORD * m_pFList, DWORD * nFound, PVOID pvData, PROCESS_LIST_FUNCTION_PTR funcPtr);
short ASDB_SQLFindFileAddrList(PASDB_SESS phSESS, char *  sPattern, char *  sHost, BOOL bCaseSensitive, char *  DefStr, PDWORD * m_pFList, DWORD * nFound, BOOL bFirst);
//short ASDB_FindFileList(PASDB_SESS phSESS, DWORD * pFList, DWORD nFound, PVOID pvData, PROCESS_LIST_FUNCTION_PTR funcPtr);
short ASDB_FindFileList(PASDB_SESS phSESS, DWORD NameID, BOOL bFirst, DWORD nRequest, PASDB_DetailExtRec * pExt, DWORD * nFound);
short ASDB_QueryFileList(PASDB_SESS phSESS, 
                         char *  sComputerName, 
                         char *  sDir, 
                         BOOL bCaseSensitive,
                         BOOL bIncludeSubDir,
                         DWORD NameID, 
                         BOOL bFirst,
                         DWORD nRequest, 
                         PASDB_DetailExtRec * pExt,
                         DWORD *nFound);
//short ASDB_DeleteTapeList(PASDB_SESS phSESS, DWORD *TapeIDList, DWORD nCnt, PVOID pvData, PROCESS_LIST_FUNCTION_PTR funcPtr);
short ASDB_DeleteTapeByID(PASDB_SESS phSESS, DWORD ID, DWORD *currentDeleted, short * status);
//short ASDB_GetDetailRecUnderDirName(PASDB_SESS phSESS, 
//                                    char * pTapeName,
//                                    short randomID,
//                                    short seqnum,
//                                    short sesnum,
//                                    char *  path,
//                                    PVOID pvData, 
//                                    PROCESS_LIST_FUNCTION_PTR funcPtr);
#ifdef SESSNUM_INCREASE
short ASDB_GetDetailRecUnderDirName(PASDB_SESS phSESS, 
                                    char * pTapeName,
                                    short randomID,
                                    short seqnum,
                                    long sesnum,
                                    char *  path,
                                    BOOL bFirst,
                                    PASDB_QFAData *pData,
                                    DWORD *nCount);
#else
short ASDB_GetDetailRecUnderDirName(PASDB_SESS phSESS, 
                                    char * pTapeName,
                                    short randomID,
                                    short seqnum,
                                    short sesnum,
                                    char *  path,
                                    BOOL bFirst,
                                    PASDB_QFAData *pData,
                                    DWORD *nCount);
#endif
//short ASDB_PruneSessionByTime(PASDB_SESS phSESS, 
//                                 LONG TaskID, 
//                                 DWORD PruneCutOffDate, 
//                                 DWORD * dwDeleted, 
//                                 HANDLE handle, 
//                                 PVOID pvData, 
//                                 PROCESS_LIST_FUNCTION_PTR funcPtr1, 
//                                 PROCESS_LIST_FUNCTION_PTR funcPtr2);
#ifdef SESSNUM_INCREASE
short ASDB_PruneSessionByTime(PASDB_SESS phSESS, 
                                 LONG TaskID, 
                                 DWORD PruneCutOffDate, 
                                 DWORD * dwDeleted, 
                                 char * tapeName, 
                                 short * randomID,
                                 long * sesNum,
                                 BOOL bFirst,
                                 short * status);
#else
short ASDB_PruneSessionByTime(PASDB_SESS phSESS, 
                                 LONG TaskID, 
                                 DWORD PruneCutOffDate, 
                                 DWORD * dwDeleted, 
                                 char * tapeName, 
                                 short * randomID,
                                 short * sesNum,
                                 BOOL bFirst,
                                 short * status);
#endif

short ASDB_GetTotalFilesInTapeByTapeID(PASDB_SESS phSESS, long TapeID, DWORD *nCnt);


//APIs that return memory block
short ASDB_GetDeviceList(PASDB_SESS phSESS, PASDB_TapeDrvRec * dList, DWORD * nCnt);
PASDB_LocationRec ASDB_GetLocationList(PASDB_SESS phSESS,int *nCnt);
PASDB_PoolRec ASDB_GetPoolList(PASDB_SESS phSESS,short *pCnt);
PASDB_TapeRec ASDB_GetSaveSetList(PASDB_SESS phSESS,char *pMediaPool,short *pCnt);
PASDB_TapeRec ASDB_GetScratchSetList(PASDB_SESS phSESS,char *pMediaPool,short *pCnt);

short ASDB_RemoveMediaFromPool(PASDB_SESS phSESS,PASDB_TapeRec pASMedia, short cnt);
short ASDB_GetLocationByID(PASDB_SESS phSESS,long LocationID,PASDB_LocationRec pASLoc);
short ASDB_AssignLocationID(PASDB_SESS phSESS,char *pTapeName,short randomid,long LocationID);
BOOL  ASDB_CheckLocationInUse(PASDB_SESS phSESS,long LocationID);
short ASDB_DeleteLocation(PASDB_SESS phSESS,PASDB_LocationRec pASLoc);
short ASDB_AssignMediaToPool(PASDB_SESS phSESS,PASDB_TapeRec pASMedia, short cnt);
short ASDB_NewLocation(PASDB_SESS phSESS,PASDB_LocationRec pASLoc);
short ASDB_ModifyLocation(PASDB_SESS phSESS,PASDB_LocationRec pASLoc);
int  ASDB_GetSaveOrScratchCount(PASDB_SESS phSESS,char *pMediaPool,int SaveOrScratchSet);
DWORD ASDB_GetDefaultSerialNoRange(PASDB_SESS phSESS,char *pBase,char *pRange);
BOOL  ASDB_ValidatePoolRange(PASDB_SESS phSESS,char *pMediaPool,char *pBase,char *pRange);
DWORD ASDB_SetPoolProperty(PASDB_SESS phSESS,PASDB_PoolRec pMediaPool);
DWORD ASDB_CreatePool(PASDB_SESS phSESS,PASDB_PoolRec pASPool);
short ASDB_SetMediaProperty(PASDB_SESS phSESS,PASDB_TapeRec pASMedia);
DWORD ASDB_DeletePool(PASDB_SESS phSESS,char *pMediaPool);
DWORD ASDB_DeleteAllPool(PASDB_SESS phSESS);
int  ASDB_SetMediaSetStatus(PASDB_SESS phSESS,char *pTapeName,short RandomID,short SeqNum,short SetStatus);
int  ASDB_SetMediaStatus(PASDB_SESS phSESS,char *pTapeName,short RandomID,short SeqNum,short mStatus);

DWORD ASDB_GetPoolProperty(PASDB_SESS phSESS,char *pMediaPool, PASDB_PoolRec pASPool);
int  ASDB_ExistTapeInPool(PASDB_SESS phSESS,char *pMediaPool,char *pTapeName,short RandomID,short SeqNum);
//short ASDB_GetMediaRecByKey(PASDB_SESS phSESS, char *pTapeName,short RandomID, short SeqNum, struct media_record * mRec);
short ASDB_GetMediaRecByKey(PASDB_SESS phSESS, char *pTapeName,short RandomID, short SeqNum, PASDB_TapeRec mRec);
DWORD ASDB_ExistPool(PASDB_SESS phSESS,char *pMediaPool);
int  ASDB_SetRotationPool(PASDB_SESS phSESS,char *pSetName,int nPools,int nDaily,int nWeekly,int nMonthly);
DWORD ASDB_GetPoolProperty2(PASDB_SESS phSESS,char *pMediaPool,PASDB_PoolRec pASPool);
BOOL ASDB_ValidateSerialNo(PASDB_SESS phSESS,char *pComputerName,char *pMediaPool,char *pSerialNo);
DWORD ASDB_GetNextSerialNo(PASDB_SESS phSESS,char *pComputerName,char *pMediaPool,char *pNextSerialNo);
int ASDB_GetPoolNameBySerialNo(PASDB_SESS phSESS,char *pComputerName,char *pSerialNo,char *pMediaPool);
DWORD ASDB_ReturnNextSerialNo(PASDB_SESS phSESS,char *pMediaPool,char *pReturnedNextSerialNo);
DWORD ASDB_DeActivatePool(PASDB_SESS phSESS,char *pMediaPool);
DWORD ASDB_ActivatePool(PASDB_SESS phSESS,char *pMediaPool);
int  ASDB_MoveMediaToScratchSet(PASDB_SESS phSESS,char *pMediaPool,int nRetentionTime,int nMinSaveCopies,int *pnMoved);
int  ASDB_GetRotationStartDate(PASDB_SESS phSESS, char *pSetName,short usJobType,unsigned short *pDosStartDate);
int  ASDB_SetMediaBKType(PASDB_SESS phSESS,char *pTapeName,short RandomID,short SeqNum,short BKType);
short ASDB_GetMediaProperty2(PASDB_SESS phSESS,char *pSerialNum,PASDB_TapeRec pASMedia);

//PASDB_DetailExtRec ASDB_GetDetailListBySessID(PASDB_SESS phSESS,short SessID,short nRequest,short bFirst,UINT *Cnt,short *pdstat);

PTAPEDATA ASDB_GetVolumeList(PASDB_SESS phSESS, PTAPEDATA lpData,UINT *Cnt);
PTAPEDATA ASDB_GetServerList(PASDB_SESS phSESS,UINT nType,UINT *Cnt);
PTAPEDATA ASDB_GetSessionList(PASDB_SESS phSESS,PTAPEDATA lpData,UINT *Cnt);
PASDB_DetailRec ASDB_GetFileByTapeView(PASDB_SESS phSESS, short SessID,short nRequest,short bFirst,UINT *Cnt,short *pdstat);
PTAPEDATA ASDB_GetDataList(PASDB_SESS phSESS,PTAPEDATA lpData,UINT *Cnt);
PTAPEDATA ASDB_GetDataList1(PASDB_SESS phSESS,PTAPEDATA lpData,UINT *Cnt);
PTAPEDATA ASDB_GetDBADatabaseList(PASDB_SESS phSESS,PTAPEDATA lpData,UINT *Cnt);
PTAPEDATA ASDB_GetTapeList(PASDB_SESS phSESS, UINT *Cnt);

long ASDB_GetAgentHostnameList(PASDB_SESS phSESS, PASDB_AGENTHOST_NAME *pNameList, UINT *nCnt);

//new TapeDataEx and VersionDataEx
PTAPEDATAEX ASDB_GetVolumeListEx(PASDB_SESS phSESS, PTAPEDATAEX lpData,UINT *Cnt);

//sonle01 20080218
PTAPEDATAEX ASDB_GetVolumeListExW(PASDB_SESSW phSESSW, PTAPEDATAEX lpData,UINT *Cnt);

PTAPEDATAEX ASDB_GetServerListEx(PASDB_SESS phSESS,UINT nType,UINT *Cnt);
PTAPEDATAEX ASDB_GetSessionListEx(PASDB_SESS phSESS,PTAPEDATAEX lpData,UINT *Cnt);
// 10/20/2008
PTAPEDATAEX ASDB_GetSessionListEx2(PASDB_SESS phSESS,PTAPEDATAEX lpData, long begin_sesstime, long end_sesstime, UINT *Cnt, long lFlag);

PTAPEDATAEX ASDB_GetDataListEx(PASDB_SESS phSESS,PTAPEDATAEX lpData,UINT *Cnt);
PTAPEDATAEX ASDB_GetDataList1Ex(PASDB_SESS phSESS,PTAPEDATAEX lpData,UINT *Cnt);
PTAPEDATAEX ASDB_GetDBADatabaseListEx(PASDB_SESS phSESS,PTAPEDATAEX lpData,UINT *Cnt);

//sonle01 20080218
PTAPEDATAEX ASDB_GetDBADatabaseListExW(PASDB_SESSW phSESSW,PTAPEDATAEX lpData,UINT *Cnt);

PTAPEDATAEX ASDB_GetDBATableListEx(PASDB_SESS phSESS,PTAPEDATAEX lpData,UINT *Cnt);
PTAPEDATAEX ASDB_GetTapeListEx(PASDB_SESS phSESS, UINT *Cnt);
PTAPEDATAEX2 ASDB_GetTapeListEx2(PASDB_SESS phSESS, UINT *Cnt); //FALCA01
PVERSIONDATAEX ASDB_GetVersionHistoryEx(PASDB_SESS phSESS, PTAPEDATAEX lpData,UINT *Cnt);
PVERSIONDATAEX2 ASDB_GetVersionHistoryEx2(PASDB_SESS phSESS, PTAPEDATAEX2 lpData,UINT *Cnt);
PVERSIONDATAEX ASDB_GetTapeSessionInfoEx(PASDB_SESS phSESS,PTAPEDATAEX lpData);
short ASDB_GetJobRecByIDEx(PASDB_SESS phSESS, DWORD jobID, PASDB_JobExRec jRec);
short ASDB_GetJobRecListEx(PASDB_SESS phSESS, BOOL bFirst, PASDB_JobExRec *pjDBRec, UINT * nCnt);
#ifdef BAB_ICBC_SUPPORT
short ASDB_GetSessRecListByJobNo(PASDB_SESS phSESS, long JobNo, BOOL bType, PASDB_SessRec *pdbSRec, UINT * nCnt);
#endif
PASDB_DetailRec ASDB_GetFileByTapeViewEx(PASDB_SESS phSESS, unsigned long SessID,short nRequest,short bFirst,UINT *Cnt,short *pdstat);
PVERSIONDATAEX ASDB_GetRestoreItemList(PASDB_SESS phSESS, PVERSIONDATAEX lpData,UINT *Cnt);

//PASDB_DetailExtRec ASDB_GetDetailListBySessIDEx(PASDB_SESS phSESS,long SessID,short nRequest,short bFirst,UINT *Cnt,short *pdstat);

PASDB_TapeRec ASDB_GetTapeRecList(PASDB_SESS phSESS, UINT *Cnt,BOOL bFirstTime,short *pdstat);

//short ASDB_GetNewSessionID(PASDB_SESS phSESS, long * sessID, short *status);
short ASDB_MergeCreateSession(PASDB_SESS phSESS, 
                                 long tapeid,
                                 long starttime,
                                 long endtime,
                                 long sessionType,
                                 long sessnum,
                                 short sessionMethod,
                                 long  sessionFlags,
                                 char *  rootDirectory, 
                                 char *  wsAddress,
                                 char *  description,
                                 char *  ownerName,
                                 char *  g_Path,
                                 PASDB_SessRec psDBRec);
short ASDB_MergeCatRegisterTape (PASDB_SESS phSESS, PASDB_KeyTapeName ptDBKey, short *pTapeID, short * status);
short ASDB_MergeCatRegisterTapeEx (PASDB_SESS phSESS, PASDB_KeyTapeName ptDBKey, long *pTapeID, short * status);
short ASDB_MergeCatModifySessInfoBySessID (PASDB_SESS phSESS, long SessID, long  totalkb, long  totalfiles);
short ASDB_MergeCatDummyEntry(PASDB_SESS phSESS, PASDB_DetailExtRec FTS, long g_nFTS, short * status);


short ASDB_RegisterTape(PASDB_SESS phSESS,FORMATTAPE *pTape);
short ASDB_FormatTape(PASDB_SESS phSESS,FORMATTAPE *pTape);
short ASDB_EraseTape(PASDB_SESS phSESS,FORMATTAPE *pTape);
short ASDB_UpdateTape(PASDB_SESS phSESS,UPDATETAPE *pTape);
short ASDB_RegisterTapeDrv(PASDB_SESS phSESS,REGISTERTAPEDRV *pReg);
short ASDB_HeadCleanTapeDrv(PASDB_SESS phSESS,char AdapterID,char AdapterBusID,char scsiid,char lun);
short ASDB_UsageTimeTapeDrv(PASDB_SESS phSESS,char AdapterID,char AdapterBusID,char scsiid,char lun,long *pUsagetime);
short ASDB_RegisterTapeDrvErr(PASDB_SESS phSESS,REGISTERTAPEDRVERROR *pReg);
short ASDB_GetTapeInfo(PASDB_SESS phSESS,char * pTapeName,short randomID,short seqnum, PASDB_TapeRec ptDBRec);
short ASDB_GetTapeInfoBySerialNo(PASDB_SESS phSESS,char *pSerialNo, PASDB_TapeRec ptDBRec);
short ASDB_FormatTapeEx(char *pComputerName, PASDB_SESS phSESS,FORMATTAPE *pTape);
short ASDB_EraseTapeEx(char *pComputerName, PASDB_SESS phSESS,FORMATTAPE *pTape);
short ASDB_UpdateTapeEx(char *pComputerName, PASDB_SESS phSESS,UPDATETAPEEX *pTape);
short ASDB_RegisterTapeEx(char *pComputerName, PASDB_SESS phSESS,FORMATTAPE *pTape);

PASDB_TapeErrRec ASDB_GetTapeErrListByTapeID(PASDB_SESS phSESS,  long tapeid, short * nCnt);
PASDB_TapeErrRec ASDB_GetAllTapeErrListByTapeID(PASDB_SESS phSESS,  long tapeid, short * nCnt);
PASDB_TapeRec ASDB_GetLogicTapeRec(PASDB_SESS phSESS, char * pTapeName, short RandomID, short * nCnt);
char * ASDB_GetSessionPathExt(PASDB_SESS phSESS, long hostid, long pathid, char *  path, short len);

DWORD ASDB_GetDatabaseType(char *  pComputerName);
char *  ASDB_GetDatabaseName(char * pServer, char *  dbname, long size);
DWORD ASDB_IsDatabaseInstalled(char *  pComputerName);
int ASDB_StartDatabaseService(PASDB_SESS phSESS);

//from ASDBNT.H
BOOL ASDB_StartBACKUPDAEMON();
BOOL ASDB_EndBACKUPDAEMON();

BOOL ASDB_StartCOPYDAEMON();
BOOL ASDB_EndCOPYDAEMON();

BOOL ASDB_StartDBBACKUP();
BOOL ASDB_EndDBBACKUP();

short ASDB_GetJobID(unsigned long *pusJobID);
short ASDB_UpdateJob(LPBACKUPJOB lpJob);
short ASDB_StartDBRESTORE();
short ASDB_EndDBRESTORE();

short ASDB_BeginJOB(LPBACKUPJOB lpJob);
short ASDB_EndJOB(LPBACKUPJOB lpJob);
short ASDB_BeginSESSION(LPBACKUPJOB lpJob);
short ASDB_EndSESSION(LPBACKUPJOB lpJob);
short ASDB_SubmitDETAIL(LPBACKUPJOB lpJob);

short ASDB_BeginCOPYJOB(LPCOPYJOB lpJob);
short ASDB_EndCOPYJOB(LPCOPYJOB lpJob);
short ASDB_BeginCOPYSESSION(LPCOPYJOB lpJob);
short ASDB_EndCOPYSESSION(LPCOPYJOB lpJob);
short ASDB_SubmitCOPYDETAIL(LPCOPYJOB lpJob);

//------------------------------------------------------------------------------------------
// ASDBGetEstimateForRestore() should be called after ASDBBeginJob() and before ASDBEndJob().
//   Input:    lpJob->TapeName
//         lpJob->RandomID
//         lpJob->SeqNum
//         lpJob->SesNum
//   Output:   lpJob->TotalKb
//         lpJob->TotalFiles
//    Return 0 if successful, otherwise Non-Zero .
//------------------------------------------------------------------------------------------
short ASDB_GetEstimateForRestore(LPBACKUPJOB lpJob);
//unsigned long ASDBGetStringID(RDM_DB hASDB,char *lpString);
//char *ASDBGetString(RDM_DB hASDB,unsigned long ulID,char *lpString,unsigned short sBufSize);

//unsigned long ASDBGetNameID(RDM_DB hASDB,char *lpName);
//char *ASDBGetName(RDM_DB hASDB,unsigned long ulID,char *lpName,unsigned short sBufSize);

//void ASDBStoreComment(RDM_DB hASDB,unsigned short usID,char *lpComments);
//char *ASDBReadComment(RDM_DB hASDB,unsigned short usID,char *lpComments);

void ASDB_GetDBServerStatus(TCHAR *pServerName,DB_STATUS* pdbStatus);
void ASDB_GetDBServerConfig(TCHAR *pServerName,DB_CONFIG* pdbConfig);
void ASDB_SetDBServerConfig(TCHAR *pServerName,DB_CONFIG* pdbConfig);
short ASDB_InitializeDatabase(char *pServerName,char *dbname);
short ASDB_GetDatabaseAccessMode(char *pServerName,int* pAccessMode);
short ASDB_SetDatabaseAccessMode(char *pServerName,int AccessMode);
short ASDB_SetMinDiskFreeSpace(char *pServerName,unsigned long ulMinDiskThreshold);
unsigned long ASDB_GetMinDiskFreeSpace(char *pServerName);

char * ASDB_GetLastDBErrorText(PASDB_SESS phSESS, long sz, char *sBuf, unsigned short sBufSize);


//for Tandem
long ASDB_ASAPI_DeleteFile(PASDB_SESS phSESS,PASDB_FILEITEM pFileItemBuffer);
long ASDB_ASAPI_QueryFileItemList(PASDB_SESS phSESS, 
                                  char *  sComputerName, 
                                  char *  sDir, 
                                  BOOL bCaseSensitive,
                                  BOOL bIncludeSubDir,
                                  DWORD NameID, 
                                  DWORD tCutoffTime,
                                  BOOL bFirst,
                                  DWORD nRequest, 
                                  PASDB_FILEITEM * pExt,
                                  DWORD *nFound);


// CUI New Functions
unsigned long ASDB_GetNumberOfRecordsInDB(PASDB_SESS phSESS, short nDatabaseID);

#define   ASJOBID        0
#define   ASTAPEID       1
#define   ASTPSESID      2
#define   ASTPSDATID     3
#define   ASTPDRVID      4
#define   ASTPDERRID     5
#define   ASDISKID       6
#define   ASDIRID        7
#define   ASDIRDATID     8
#define   ASOBJECTID     9
#define   ASNODEID       10
#define   ASTPPOOLID     11
#define   ASLOCIDID      12
#define   ASPROFILID     13
#define   ASPROUSRID     14
#define   ASPROGRPID     15
#define   ASUSRPRFID     16
#define   ASUSRSVRID     17
#define   ASPASSWDID     18

//The following API use NOS_OS_TYPE define in ASDB.H;
//typedef enum  _tag_NOS_OS_TYPE 
//{
//   NOS_UNKNOWN          = 0,       // The operating system is Unknown
//   NOS_DOS              ,          // The operating system is DOS
//   NOS_OS2              ,          // The operating system is OS/2
//   NOS_NETWARE          ,          // The operating system is NetWare
//   NOS_WINDOWS          ,          // The operating system is Windows (NetWare 3.1 or WIN95)
//   NOS_UNIX             ,          // The operating system is UNIX
//   NOS_MAC              ,          // The operating system is Macintosh
//   NOS_NT               ,          // The operating system is Windows NT
//   NOS_WIN95            ,          // The operating system is Windows 95
//   NOS_LAST_TYPE                   // Any new os type should add before this type
//} NOS_OS_TYPE;



/*
struct _UNIASPROFILE {
	long ulID;
	char profName[16];
	char profile[32];
	char profDesc[252];
	long Reserved[4];
};
typedef struct _UNIASPROFILE _UNIASPROFILE;
typedef _UNIASPROFILE UNIASPROFILE;
typedef UNIASPROFILE *pUNIASPROFILE;

struct _UNIASUSERPROFILE {
	long ulProfID;
	long ulUserNameID;
	char *pUserName;
	long Reserved[4];
};
typedef struct _UNIASUSERPROFILE _UNIASUSERPROFILE;
typedef _UNIASUSERPROFILE UNIASUSERPROFILE;
typedef UNIASUSERPROFILE *pUNIASUSERPROFILE;

struct _UNIASGROUPPROFILE {
	long ulProfID;
	char GrpOrPoolName[32];
	char GrpNameUse;
	long Reserved;
};
typedef struct _UNIASGROUPPROFILE _UNIASGROUPPROFILE;
typedef _UNIASGROUPPROFILE UNIASGROUPPROFILE;
typedef UNIASGROUPPROFILE *pUNIASGROUPPROFILE;
*/

short ASDB_UpdateNodeList(PASDB_SESS phSESS, PASDB_NodeRec pNodeList, short nCnt);
short ASDB_InsertNodeList(PASDB_SESS phSESS, PASDB_NodeRec pNodeList, short nCnt);
short ASDB_DeleteNodeList(PASDB_SESS phSESS, PASDB_NodeRec pNodeList, short nCnt);
PASDB_NodeRec ASDB_GetNodeList(PASDB_SESS phSESS, int nOSType, int *nCnt);
short ASDB_GetNodeCountByOS(PASDB_SESS phSESS, long *nCnt);
PASDB_NodeRec ASDB_FindNodeByOS(PASDB_SESS phSESS, int nOSType, char *pNodeName, char *pNodeAddress);
long  ASDB_GetProfileRightsIDList(PASDB_SESS phSESS, long nInCnt, long *nOutCnt, long *pProfileIDList);
long  ASDB_GetProfileRightsList(PASDB_SESS phSESS, long nInCnt, long *nOutCnt, long *pProfileIDList, UNIASPROFILE *pProfileRightsList);
long  ASDB_SetProfileRightsList(PASDB_SESS phSESS, long nInCnt, UNIASPROFILE *pProfileRightsList);
long  ASDB_DeleteProfileRightsByProfileIDList(PASDB_SESS phSESS, long nInCnt, long *pProfileIDList);
long  ASDB_GetProfileUserIDListByProfileIDList(PASDB_SESS phSESS, long nInCnt, long *nOutCnt, long *pProfileIDList, UNIASUSERPROFILE *pProfileUserList);
long  ASDB_SetProfileUserList(PASDB_SESS phSESS, long nInCnt, UNIASUSERPROFILE *pProfileUserList);
long  ASDB_DeleteProfileUserList(PASDB_SESS phSESS, long nInCnt, UNIASUSERPROFILE *pProfileUserList);
long  ASDB_GetProfileGroupListByProfileIDList(PASDB_SESS phSESS, long nInCnt, long *nOutCnt, long *pProfileIDList, UNIASGROUPPROFILE *pProfileGroupList);
long  ASDB_SetProfileGroupList(PASDB_SESS phSESS, long nInCnt, UNIASGROUPPROFILE *pProfileGroupList);
long  ASDB_DeleteProfileGroupList(PASDB_SESS phSESS, long nInCnt, UNIASGROUPPROFILE *pProfileGroupList);
long  ASDB_Open(PASDB_SESS phSESS, long ulDatabaseID, long lOpenMode);
long  ASDB_Close(PASDB_SESS phSESS, long ulDatabaseID);

// CUI end

//**************************************************
//Beginning of SAP support
//**************************************************
short       ASDB_SAPGetDBJobID(PASDB_SESS phSESS, char *pSAPJobID);
long        ASDB_SAPGetDBJobIDEx(PASDB_SESS phSESS, char *pSAPJobID);// For ARCserveIT v7.0
PHSAPFILE   ASDB_SAPGetFileList(PASDB_SESS phSESS, char *pSAPJobID, short *nCnt);
PHSAPJOBID  ASDB_SAPGetSAPJobIDListByFileName(PASDB_SESS phSESS, char *pComputerName,char *pUserID,char *pPath,char *pFileName,short *nCnt);
PHSAPJOBID  ASDB_SAPGetSAPJobIDListByUserID(PASDB_SESS phSESS, char *pComputerName,char *pUserID,short *nCnt);
short       ASDB_SAPGetJobInfo(PASDB_SESS phSESS, char *pSAPJobID,PHSAPJOB phSAPJob);
short       ASDB_SAPGetSessionInfo(PASDB_SESS phSESS, char *pSAPJobID,char *pComputerName,char *pPath,char *pFileName,PHSAPSESSION phSAPSession);
//**************************************************
//End of SAP support
//**************************************************

//new common tape header
short ASDB_GetTapeMediaRecListEx(PASDB_SESS phSESS, long isValid, long cutofftime, BOOL bFirst, long * nCnt, PASDB_TapeExRec * pTapeList);
short ASDB_GetTapeMediaRecByTapeIDEx(PASDB_SESS phSESS, long TapeID, PASDB_TapeExRec tRec);
short ASDB_GetTapeRecordEx(PASDB_SESS phSESS, char * pTapeName,short randomID,short seqnum, short raidid, char *serialno, PASDB_TapeExRec ptRec);
short ASDB_SetTapeRecordEx(PASDB_SESS phSESS, PASDB_TapeExRec ptRec);
short ASDB_SetTapeLocationStatus(PASDB_SESS phSESS, char * pTapeName,short randomID,short seqnum, short raidid, char *serialno, long status);

//<li$we04 desc="new api for cadbd">
short ASDB_GetTapeRecordExByRandomID(PASDB_SESS phSESS, short randomID,short seqnum, short raidno, char *serialno, PASDB_TapeExRec ptRec);
short ASDB_GetTotalCount(PASDB_SESS phSESS, char* querytable, int nType, long* nCnt1, long* nCnt2);
short ASDB_GetDetailExpireAttr(PASDB_SESS phSESS, int SessionID, int PathID, int FileNameID, short* ExpireAttr);
short ASDB_SetDetailExpireAttr(PASDB_SESS phSESS, int SessionID, int PathID, int FileNameID, short ExpireAttr);
//</li$we04>
//<li$we04 desc="new table aslogerr">
short ASDB_WriteAsLogErr(PASDB_SESS phSESS, PASDB_LogErrRec pLogErrRec);
//</li$we04>

//**************************************************
//Start MMO support
//**************************************************
//enum {ENUM_BY_NONE=0, ENUM_BY_NAME, ENUM_BY_ID};
long ASDB_MMO_Login(char *pComputerName,PASDB_SESS phSESS);
long ASDB_MMO_Logout(PASDB_SESS phSESS);


//**************************************************
//End of MMO support
//**************************************************

long ASDB_SetRemoteDatabaseMachine (char* computer,
                                    char* user,
                                    char* pwd,
                                    unsigned long machinetype,
                                    PSERVER_ACCOUNT_INFO_EX remoteInfo);
long ASDB_GetRemoteDatabaseMachine (char* computer,
                                    char* user,
                                    char* pwd,
                                    unsigned long machinetype,
                                    PSERVER_ACCOUNT_INFO_EX remoteInfo);

//ASNT.71 support
short ASDB_RegisterTape_71(PASDB_SESS phSESS,FORMATTAPE71 *pTape);
short ASDB_FormatTape_71(PASDB_SESS phSESS,FORMATTAPE71 *pTape);
short ASDB_EraseTape_71(PASDB_SESS phSESS,FORMATTAPE71 *pTape);
short ASDB_UpdateTape71(PASDB_SESS phSESS,UPDATETAPE71 *pTape);
short ASDB_RegisterTapeDrv_71(PASDB_SESS phSESS,REGISTERTAPEDRV71 *pReg);
BOOL ASDB_CanThisTapeBeOverwrite (PASDB_SESS phSESS, PTapeKey ptapekey, short*status);
BOOL ASDB_CanThisTapeBeAppend (PASDB_SESS phSESS, PTapeKey ptapekey, short*status);

short ASDB_GetTapeRecSet (PASDB_SESS phSESS, char *pTapeName, short randomID, short *nCnt, PASDB_TapeExRec *ptRec);
DWORD ASDB_GetTapeRetiredPolicy(PASDB_SESS phSESS, PTapeRetiredPolicy pPolicy);
DWORD ASDB_SetTapeRetiredPolicy(PASDB_SESS phSESS, long nCnt, PTapeRetiredPolicy pPolicy);

char  *ASDB_GetOwnerByID(PASDB_SESS phSESS,unsigned long ulID,char *lpString,unsigned short sBufSize);

PTSTR ASDB_GetLastErrorText(PDWORD pdwError);
DWORD ASDB_GetProductBuildNumber(const PTCHAR pComputerName, SERVER_ACCOUNT_INFO *pAccInfo);

// cst function
UINT   WINAPI ASDB_CstGetSettingBinary(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                PVOID defaultBuffer, PVOID buffer, 
                                ULONG bufferSize);
UINT   WINAPI ASDB_CstGetSettingInt(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                UINT defaultValue);
ULONG  WINAPI ASDB_CstGetSettingLong(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                ULONG defaultValue);
UINT   WINAPI ASDB_CstGetSettingString(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                PTCHAR defaultString, PTCHAR buffer, 
                                ULONG bufferSize);
UINT   WINAPI ASDB_CstGetGlobalSettingString(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                PTCHAR defaultString, PTCHAR buffer, 
                                ULONG bufferSize);
UINT   WINAPI ASDB_CstGetUserBinary(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                PVOID defaultBuffer, PVOID buffer, 
                                ULONG bufferSize);
USHORT WINAPI ASDB_CstGetLogDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
UINT WINAPI ASDB_CstSetSettingString(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                PTCHAR string);
UINT WINAPI ASDB_CstSetSettingBinary(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                PVOID buffer, ULONG bufferSize);
UINT WINAPI ASDB_CstSetSettingInt(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                UINT value);


//Dbconfig.exe
USHORT WINAPI ASDB_CstGetDiscoveryTblString(PTCHAR name, USHORT limit);
DWORD ASDB_DSGetProductDescriptorEx(unsigned char *serverName, DWORD dwProdId, ASProduct *pASProduct, PSERVER_ACCOUNT_INFO pAccInfo);
DWORD ASDB_IsDatabaseInstalled2(PASDB_SESS phSESS, char *computer, DWORD *pdwIsInstalled);


//3/13/2001
UINT   WINAPI ASDB_CstDelSetting(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key);
ULONG  WINAPI ASDB_CstSetSettingLong(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                ULONG value);
UINT   WINAPI ASDB_CstDelUser(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key);

UINT   WINAPI ASDB_CstGetUserInt(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                UINT defaultValue);
UINT   WINAPI ASDB_CstGetUserString(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                PTCHAR defaultString, PTCHAR buffer, 
                                ULONG bufferSize);
UINT   WINAPI ASDB_CstSetUserBinary(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                PVOID buffer, ULONG bufferSize);
UINT   WINAPI ASDB_CstSetUserInt(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                UINT value);
UINT   WINAPI ASDB_CstSetUserString(PTCHAR serverName, PTCHAR section, 
                                PTCHAR subSection, PTCHAR key, 
                                PTCHAR string);

/*
 *      Cheyenne Setup Specifics
 */
USHORT WINAPI ASDB_CstGetDatabaseDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
USHORT WINAPI ASDB_CstGetHomeDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
//USHORT WINAPI ASDB_CstGetHostServer(PTCHAR serverName);

USHORT WINAPI ASDB_CstGetProgramDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
USHORT WINAPI ASDB_CstGetRegistryDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);

USHORT WINAPI ASDB_CstGetSystemDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
USHORT WINAPI ASDB_CstGetTempDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
USHORT WINAPI ASDB_CstGetUserDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
USHORT WINAPI ASDB_CstGetUserName(PTCHAR serverName, PTCHAR name, USHORT limit);
BOOL   WINAPI ASDB_CstGetUserAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI ASDB_CstSetUserAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI ASDB_CstGetSQLAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI ASDB_CstSetSQLAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI ASDB_CstGetRemoteSQLAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI ASDB_CstSetRemoteSQLAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI ASDB_CstGetIngresAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI ASDB_CstSetIngresAccount(PTCHAR serverName, PCSI_ACCOUNT account);

/*   CA license checking function (cheyprod.dll) */
ASLIC_RETURN WINAPI ASDB_CPca_license_check(PTCHAR computer, DWORD prodId); // Product ID
ASLIC_RETURN WINAPI ASDB_CPca_license_check2(PTCHAR computer,
                                             DWORD prodId,
                                             unsigned int wg_count,
                                             LIC_ERRFLAG process_error,
                                             LIC_RETURN* pca_lic_error);

long ASDB_GetSessionProperty(PASDB_SESS phSESS,long SessionID,PASDB_SessRec pSessRec);
long ASDB_SetSessionProperty(PASDB_SESS phSESS,long SessionID,PASDB_SessRec pSessRec);
PASDB_TapeRec ASDB_GetGlobalScratchSetList(PASDB_SESS phSESS,short *pCnt);

#ifdef SESSNUM_INCREASE
long ASDB_GetTapeSeqEnd  (PASDB_SESS phSESS, char *pTapeName, long RandomID, long SesNum, ULONG *pSeqNum);
long ASDB_GetTapeSequence(PASDB_SESS phSESS, char *tapename,  long randomid, long SesNum, unsigned long *pSeqNum);
long ASDB_SetTapeSeqEnd  (PASDB_SESS phSESS, char *pTapeName, long Randomid, long SesNum, ULONG   SeqNum);
#else
long ASDB_GetTapeSeqEnd  (PASDB_SESS phSESS, char *pTapeName, long RandomID, short SesNum, ULONG *pSeqNum);
long ASDB_SetTapeSeqEnd  (PASDB_SESS phSESS, char *pTapeName, long Randomid, short SesNum, ULONG   SeqNum);
#endif

long ASDB_GetTapeSpanSize(PASDB_SESS phSESS, char *pTapeName, long RandomID, short SeqNum, ULONG *phSize, ULONG *plSize );
long ASDB_SetTapeSpanSize(PASDB_SESS phSESS, char *pTapeName, long RandomID, short SeqNum, ULONG   hSize, ULONG   lSize );

long ASDB_GetCatalogFileName(char    *serverName, 
							 char    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 BOOL bCreateFolder, 
							 char *tszCatalog, 
							 UINT nLength);
long ASDB_GetCatalogFileNameEx(char    *serverName, 
							 char    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 BOOL bCreateFolder, 
							 ULONG   ulFlags,
							 char *tszCatalog, 
							 UINT nLength);
long ASDB_DeleteCatalogFile( char *serverName, 
							 char    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber);
long ASDB_PruneCatalogFileByTime(char *serverName, 
								 DWORD PruneCutOffDate);
long ASDB_PurgeCatalogFiles(char    *serverName, 
							char    *szTapeName,
							ULONG   ulTapeNumber,
							ULONG   ulRandomID,
							ULONG   ulSessionNumber);
long ASDB_IsCatalogFileExist(char    *serverName, 
							 char    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber);
long ASDB_CopyCatalogFile(char    *serverName, 
						  char    *szTapeNameDest,
						  ULONG   ulTapeNumberDest,
						  ULONG   ulRandomIDDest,
						  ULONG   ulSessionNumberDest,
						  char    *szTapeNameSrc,
						  ULONG   ulTapeNumberSrc,
						  ULONG   ulRandomIDSrc,
						  ULONG   ulSessionNumberSrc);
long ASDB_RenameCatalogFile(char    *serverName, 
							char    *szTapeNameDest,
							ULONG   ulTapeNumberDest,
							ULONG   ulRandomIDDest,
							ULONG   ulSessionNumberDest,
							char    *szTapeNameSrc,
							ULONG   ulTapeNumberSrc,
							ULONG   ulRandomIDSrc,
							ULONG   ulSessionNumberSrc,
							int nDisplayFormat);
BOOL ASDB_IsCatalogDBEnabled(char *serverName);
long ASDB_GetCatalogDBHome(char *serverName, char *path, long limit);
long ASDB_GetCatalogTapeFolder(char    *serverName, 
							   char    *szTapeName,
							   ULONG   ulTapeNumber,
							   ULONG   ulRandomID,
							   ULONG   ulSessionNumber,
							   char    *tszTapeFolder, 
							   long    limit);

long ASDB_MergeCatalogFile(char *computer, 
				   char    *szTapeName,
				   ULONG   ulTapeNumber,
				   ULONG   ulRandomID,
				   ULONG   ulSessionNumber);


long ASDB_SetMux(PTCHAR computer, 
					 CHAR    *szTapeName,
					 ULONG   ulRandomID,
					 ULONG   ulSessionNumber,
					 char    *pMuxData, 
					 ULONG   ulLength);

long ASDB_GetMux(PTCHAR computer, 
							 CHAR    *szTapeName,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 char    **pMuxData, 
							 ULONG   *pulLength);

long ASDB_PurgeMuxFiles(char    *serverName, 
							char    *szTapeName,
							ULONG   ulTapeNumber,
							ULONG   ulRandomID,
							ULONG   ulSessionNumber);

BOOL ASDB_HeapFree( LPVOID lpMem );


short ASDB_MergeCatMessageDummyEntry(PASDB_SESS phSESS, PASDB_MsgRec MsgRecord, long g_nMsgRecord, short * status);
short ASDB_MergeCatMsgDatDummyEntry(PASDB_SESS phSESS, PASDB_MsgDatRec MsgDatRecord, long g_nMsgDatRecord, short * status);
short ASDB_MsgGetObject(PASDB_SESS phSESS, long sessid, long lselfid, long hselfid, PASDB_MsgRec pRec);
short ASDB_MsgGetChildren(PASDB_SESS phSESS, long sessid, long lselfid, long hselfid, BOOL bFirst, PASDB_MsgRec * pData, UINT * nCnt);
short ASDB_MsgGetData(PASDB_SESS phSESS, long sessid, long lselfid, long hselfid, long * nCnt, char * pData);
short ASDB_MsgSetData(PASDB_SESS phSESS, long sessid, long lselfid, long hselfid, long * nCnt, char * pData);
short ASDB_MsgUpdate(PASDB_SESS phSESS, PASDB_MsgRec MsgRecord, long g_nMsgRecord, long ulFlags, short * status);
short ASDB_MsgFindFile(PASDB_SESS phSESS, long ulFlags, char *sHost, char *sPath, char *sPattern, BOOL bFirst, PASDB_MsgRec * pData, UINT * nCnt);

DWORD ASDB_ReportRemoteExecuteCML( 
					PTCHAR computer,
					CHAR *Cmdline,
					CHAR *CmdArgs,
					CHAR *sReportName,			// Source File Name
					unsigned long InNumLines,	// Size of Source File 
					char * InputBuffer,			// Data Buffer of Source File 
					unsigned long uFlag,
					unsigned long *NumLines,	// Size of Output File		
					char **ResultOut);			// Data Buffer of Output File 

ULONG ASDB_SetDebug( DWORD dwDebug );
ULONG ASDB_DBLog( IN char *Str,... );

/// For 11.01 (for serialnumber)
int  ASDB_SetMediaSetStatus2(PASDB_SESS phSESS,char *pTapeName,short RandomID,short SeqNum,char *pSerialNum,short SetStatus);
int  ASDB_SetMediaStatus2(PASDB_SESS phSESS,char *pTapeName,short RandomID,short SeqNum,char *pSerialNum,short mStatus);
int  ASDB_SetMediaBKType2(PASDB_SESS phSESS,char *pTapeName,short RandomID,short SeqNum,char *pSerialNum,short BKType);
int  ASDB_ExistTapeInPool2(PASDB_SESS phSESS,char *pMediaPool,char *pTapeName,short RandomID,short SeqNum,char *pSerialNum);

#ifdef SESSNUM_INCREASE
long ASDB_GetTapeSeqEnd2  (PASDB_SESS phSESS, char *pTapeName, long RandomID, long SesNum, char *pSerialNum, ULONG *pSeqNum);
long ASDB_SetTapeSeqEnd2  (PASDB_SESS phSESS, char *pTapeName, long Randomid, long SesNum, char *pSerialNum, ULONG   SeqNum);
#else
long ASDB_GetTapeSeqEnd2  (PASDB_SESS phSESS, char *pTapeName, long RandomID, short SesNum, char *pSerialNum, ULONG *pSeqNum);
long ASDB_SetTapeSeqEnd2  (PASDB_SESS phSESS, char *pTapeName, long Randomid, short SesNum, char *pSerialNum, ULONG   SeqNum);
#endif

long ASDB_GetTapeSpanSize2(PASDB_SESS phSESS, char *pTapeName, long RandomID, short SeqNum, char *pSerialNum, ULONG *phSize, ULONG *plSize );
long ASDB_SetTapeSpanSize2(PASDB_SESS phSESS, char *pTapeName, long RandomID, short SeqNum, char *pSerialNum, ULONG   hSize, ULONG   lSize );

#ifdef ASDB_D2D2T

short ASDB_SetSessionMap(PASDB_SESS phSESS, PASDB_SessionMap pFromSession, PASDB_SessionMap pToSession);
short ASDB_DeleteSessionMap(PASDB_SESS phSESS, PASDB_SessionMap pSession);
short ASDB_GetSessionMap(PASDB_SESS phSESS, PASDB_SessionMap pSession, unsigned long ulFlag, PASDB_SessionMap pSessionOut);

short ASDB_SetStaging(PASDB_SESS phSESS, PASDB_Staging pStaging);
short ASDB_GetStaging(PASDB_SESS phSESS, PASDB_StagingParm pStagingParm, BOOL bFirst, PASDB_Staging *pStaging, UINT * nCnt);
short ASDB_DeleteStaging(PASDB_SESS phSESS, PASDB_Staging pStaging);
short ASDB_DeleteStagingEx(PASDB_SESS phSESS, PASDB_Staging pStaging,ULONG ulFlags);
short ASDB_GetSessionMapList(PASDB_SESS phSESS, PASDB_SessionMap pSession, unsigned long ulFlag, UINT * nCnt, PASDB_SessionMap *pSessionOut, UINT uReserved);

short ASDB_SetJobMapInfo(PASDB_SESS phSESS, PASDB_InJobMapInfo pJobMapInfo, unsigned long ulFlag);
short ASDB_GetMasterJobs(PASDB_SESS phSESS, PASDB_MasterJobMapInfoParm pMasterJobMapParm, UINT * nCnt, PASDB_MasterJobMapInfo *pMasterJobMap);
short ASDB_GetChildJobs(PASDB_SESS phSESS, PASDB_ChildJobMapInfoParm pChildJobMapParm, UINT * nCnt, PASDB_ChildJobMapInfo *pChildJobMap);

long ASDB_CopyCatalogFileEx(char    *serverName, 
						  char    *szTapeNameDest,
						  ULONG   ulTapeNumberDest,
						  ULONG   ulRandomIDDest,
						  ULONG   ulSessionNumberDest,
						  char    *szTapeNameSrc,
						  ULONG   ulTapeNumberSrc,
						  ULONG   ulRandomIDSrc,
						  ULONG   ulSessionNumberSrc,
						  ULONG   ulFlags);

long ASDB_SetSessionGUID (PASDB_SESS phSESS, PASDB_SessionGUID pSessGUID, ULONG uFlag);
long ASDB_GetSessionGUID (PASDB_SESS phSESS, PASDB_SessionGUIDParm pSessGUIDParm, PASDB_SessionGUID *pSessGUID, UINT * nCnt);
long ASDB_DeleteSessionGUID (PASDB_SESS phSESS, PASDB_SessionGUID pSessGUID, ULONG ulFlags);						  
#ifdef SESSNUM_INCREASE
long ASDB_GetSessionSPAPath (PASDB_SESS phSESS, char *TapeName, short randomID, short seqNum, char *pSerialNum, long sessionNum, char* outPath, unsigned short sBufSize, ULONG uFlag);
long ASDB_GetSessionGUIDByMediaAndSessNum (PASDB_SESS phSESS, char *TapeName, short randomID, short seqNum, char *pSerialNum, long sessionNum, PASDB_SessionGUID pSessGUID);
#else
long ASDB_GetSessionSPAPath (PASDB_SESS phSESS, char *TapeName, short randomID, short seqNum, char *pSerialNum, short sessionNum, char* outPath, unsigned short sBufSize, ULONG uFlag);
long ASDB_GetSessionGUIDByMediaAndSessNum (PASDB_SESS phSESS, char *TapeName, short randomID, short seqNum, char *pSerialNum, short sessionNum, PASDB_SessionGUID pSessGUID);
#endif
long ASDB_LPMergePath (PASDB_SESS phSESS, PASDB_LPMergePathParm pMergePath);
short ASDB_SetMigratedSessionsInStaging (PASDB_SESS phSESS, PASDB_TapeInfoParm pStagingTapeInfo, PASDB_TapeInfoParm pMigratedTapeInfo, long lastValidDest);
long ASDB_GetMediaAndSessGUIDInfoByGUID (PASDB_SESS phSESS, char*  sessguid, PASDB_SessionGUID *pSessGUID, UINT * nCnt, UINT uFlag);

short ASDB_ValidGetServerHost(PASDB_SESS phSESS, char* pHostName, ULONG uFlag);
short ASDB_GetServerHost(PASDB_SESS phSESS, PASDB_SeverHost *pServerHost, UINT * nCnt, ULONG uFlag);

// R11.1 + sp1						  
#ifdef SESSNUM_INCREASE
long ASDB_DeleteSessionByMediaAndSessNum (PASDB_SESS phSESS, char *TapeName, short randomID, short seqNum, char *pSerialNum, long sessionNum, ULONG uFlag);
#else
long ASDB_DeleteSessionByMediaAndSessNum (PASDB_SESS phSESS, char *TapeName, short randomID, short seqNum, char *pSerialNum, short sessionNum, ULONG uFlag);
#endif

short ASDB_GetTapeRecord2 (PASDB_SESS phSESS,char *pTapeName,short randomID,short seqnum, char *pSerialNum, PASDB_TapeRec ptRec);
short ASDB_MergeCatRegisterTapeEx2 (PASDB_SESS phSESS, PASDB_KeyTapeName ptDBKey, char *pSerialNum, long *pTapeID, short * status, char* fromHost);


//////////////////////////////////
DWORD WINAPI ASDB_DSSetProductConfigInfo	(LPCTSTR lpszServerName, DWORD ProdId, LPBYTE pbsConfigInf, LPDWORD lpdwSize);
DWORD WINAPI ASDB_DSGetProductConfigInfo	(LPCTSTR lpszServerName, DWORD ProdId, LPBYTE pbsConfigInf, LPDWORD lpdwSize);
BOOL ASDB_DeleteFile	(LPCTSTR lpszServerName, LPCTSTR lpszFilePath);
DWORD ASDB_GetPrivateProfileString (LPCTSTR lpszServerName, LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpDefault, LPTSTR lpReturnedString, DWORD nSize, LPCTSTR lpFileName);
//BOOL ASDB_WritePrivateProfileSection (LPCTSTR lpszServerName, LPCTSTR lpAppName,  LPCTSTR lpString, LPCTSTR lpFileName); 
BOOL ASDB_WritePrivateProfileSection (LPCTSTR lpszServerName, LPCTSTR lpAppName,  LPCTSTR lpString, DWORD nSize, LPCTSTR lpFileName); 
LONG ASDB_RegCreateKeyEx (LPCTSTR lpszServerName, HKEY hKey, LPCTSTR lpSubKey, DWORD Reserved, LPTSTR lpClass, DWORD dwOptions, REGSAM samDesired, LPSECURITY_ATTRIBUTES lpSecurityAttributes, PHKEY phkResult, LPDWORD lpdwDisposition);
LONG ASDB_RegSetValueEx (LPCTSTR lpszServerName, HKEY hKey, LPCTSTR lpValueName, DWORD Reserved, DWORD dwType, CONST BYTE *lpData, DWORD cbData);
LONG ASDB_RegQueryValueEx (LPCTSTR lpszServerName, HKEY hKey, LPCTSTR lpValueName, LPDWORD lpReserved, LPDWORD lpType, LPBYTE lpData, LPDWORD lpcbData);
LONG ASDB_RegQueryInfoKey (LPCTSTR lpszServerName, HKEY hKey, LPTSTR lpClass, LPDWORD lpcClass, LPDWORD lpReserved, LPDWORD lpcSubKeys, LPDWORD lpcMaxSubKeyLen, LPDWORD lpcMaxClassLen, LPDWORD lpcValues, LPDWORD lpcMaxValueNameLen, LPDWORD lpcMaxValueLen, LPDWORD lpcbSecurityDescriptor, PFILETIME lpftLastWriteTime);
LONG ASDB_RegDeleteKey (LPCTSTR lpszServerName, HKEY hKey, LPCTSTR lpSubKey);
LONG ASDB_RegOpenKeyEx (LPCTSTR lpszServerName, HKEY hKey, LPCTSTR lpSubKey, DWORD ulOptions, REGSAM samDesired, PHKEY phkResult);
LONG  ASDB_RegCloseKey (LPCTSTR lpszServerName, HKEY hKey);
LONG ASDB_RegEnumValue (LPCTSTR lpszServerName, HKEY hKey, DWORD dwIndex, LPTSTR lpValueName,LPDWORD lpcValueName,LPDWORD lpReserved,LPDWORD lpType,LPBYTE lpData,LPDWORD lpcbData);   
LONG ASDB_RegDeleteValue (LPCTSTR lpszServerName, HKEY hKey, LPCTSTR lpValueName);  
int ASDB_tcreat	(LPCTSTR lpszServerName, char *filename, int pmode ); 
int ASDB_close	(LPCTSTR lpszServerName, int handle ); 
int ASDB_OSLogin (LPCTSTR lpszServerName, LPCTSTR lpszUserName, LPCTSTR lpszPassword);

DWORD ASDB_GetPrivateProfileStringW (LPCWSTR lpszServerNameW, LPCWSTR lpAppNameW, LPCWSTR lpKeyNameW, LPCWSTR lpDefaultW, LPCWSTR lpReturnedStringW, DWORD nSize, LPCWSTR lpFileNameW);
BOOL ASDB_WritePrivateProfileSectionW (LPCWSTR lpszServerNameW, LPCWSTR lpAppNameW,  LPCWSTR lpStringW, DWORD nSize, LPCWSTR lpFileNameW);
LONG ASDB_RegCreateKeyExW (LPCWSTR lpszServerNameW, HKEY hKey, LPCWSTR lpSubKeyW, DWORD Reserved, LPWSTR lpClassW, DWORD dwOptions, REGSAM samDesired, LPSECURITY_ATTRIBUTES lpSecurityAttributes, PHKEY phkResult, LPDWORD lpdwDisposition);
LONG ASDB_RegSetValueExW (LPCWSTR lpszServerNameW, HKEY hKey, LPCWSTR lpValueNameW, DWORD Reserved, DWORD dwType, CONST BYTE *lpData, DWORD cbData);
LONG ASDB_RegQueryValueExW (LPCWSTR lpszServerNameW, HKEY hKey, LPCWSTR lpValueNameW, LPDWORD lpReserved, LPDWORD lpType, LPBYTE lpData, LPDWORD lpcbData);
LONG ASDB_RegQueryInfoKeyW (LPCWSTR lpszServerNameW, HKEY hKey, LPWSTR lpClassW, LPDWORD lpcClass, LPDWORD lpReserved, LPDWORD lpcSubKeys, LPDWORD lpcMaxSubKeyLen, LPDWORD lpcMaxClassLen, LPDWORD lpcValues, LPDWORD lpcMaxValueNameLen, LPDWORD lpcMaxValueLen, LPDWORD lpcbSecurityDescriptor, PFILETIME lpftLastWriteTime);
LONG ASDB_RegDeleteKeyW (LPCWSTR lpszServerNameW, HKEY hKey, LPCWSTR lpSubKeyW);
LONG ASDB_RegOpenKeyExW (LPCWSTR lpszServerNameW, HKEY hKey, LPCWSTR lpSubKeyW, DWORD ulOptions, REGSAM samDesired, PHKEY phkResult);
LONG  ASDB_RegCloseKeyW (LPCWSTR lpszServerNameW, HKEY hKey);
LONG ASDB_RegEnumValueW (LPCWSTR lpszServerNameW, HKEY hKey, DWORD dwIndex, LPWSTR lpValueNameW,LPDWORD lpcValueName,LPDWORD lpReserved,LPDWORD lpType,LPBYTE lpData,LPDWORD lpcbData);
LONG ASDB_RegDeleteValueW (LPCWSTR lpszServerNameW, HKEY hKey, LPCWSTR lpValueNameW);

long ASDB_GetCatalogFileNameWithSerial(PTCHAR computer, 
							 CHAR    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
 							 CHAR    *szSerialNo,
							 BOOL bCreateFolder, 
							 char *tszCatalog, 
							 UINT nLength);

long ASDB_GetCatalogFileNameExWithSerial(PTCHAR computer, 
							 CHAR    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 CHAR    *szSerialNo,
							 BOOL bCreateFolder,
							 ULONG   ulFlags,
							 char *tszCatalog, 
							 UINT nLength);

long ASDB_IsCatalogFileExistWithSerial(PTCHAR computer, 
							 CHAR    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 CHAR	 *szSerialNo);

long ASDB_CopyCatalogFileExWithSerial(PTCHAR computer, 
							 CHAR    *szTapeNameDest,
							 ULONG   ulTapeNumberDest,
							 ULONG   ulRandomIDDest,
							 ULONG   ulSessionNumberDest,
							 CHAR    *szSerialNoDest,
							 CHAR    *szTapeNameSrc,
							 ULONG   ulTapeNumberSrc,
							 ULONG   ulRandomIDSrc,
							 ULONG   ulSessionNumberSrc,
							 CHAR    *szSerialNoSrc,
							 ULONG   ulFlags);

long ASDB_RenameCatalogFileWithSerial(PTCHAR computer, 
							 CHAR    *szTapeNameDest,
							 ULONG   ulTapeNumberDest,
							 ULONG   ulRandomIDDest,
							 ULONG   ulSessionNumberDest,
							 CHAR    *szSerialNoDest,
							 CHAR    *szTapeNameSrc,
							 ULONG   ulTapeNumberSrc,
							 ULONG   ulRandomIDSrc,
							 ULONG   ulSessionNumberSrc,
							 CHAR    *szSerialNoSrc,
							int nDisplayFormat);

long ASDB_DeleteCatalogFileWithSerial(PTCHAR computer, 
					 CHAR    *szTapeName,
					 ULONG   ulTapeNumber,
					 ULONG   ulRandomID,
					 ULONG   ulSessionNumber,
					 CHAR    *szSerialNo);

long ASDB_PurgeCatalogFilesWithSerial(PTCHAR computer, 
					 CHAR    *szTapeName,
					 ULONG   ulTapeNumber,
					 ULONG   ulRandomID,
					 ULONG   ulSessionNumber,
					 CHAR    *szSerialNo);


long ASDB_MergeCatalogFileWithSerial(PTCHAR computer, 
				   CHAR    *szTapeName,
				   ULONG   ulTapeNumber,
				   ULONG   ulRandomID,
				   ULONG   ulSessionNumber,
				   CHAR    *szSerialNo);

long ASDB_GetCatalogTapeFolderWithSerial(PTCHAR computer, 
					 CHAR    *szTapeName,
					 ULONG   ulTapeNumber,
					 ULONG   ulRandomID,
					 ULONG   ulSessionNumber,
					 CHAR    *szSerialNo,
					   char *tszTapeFolder, 
					   long limit);

   
short ASDB_SmartDeleteStaging(PASDB_SESS phSESS, PASDB_Staging pStaging);

#ifdef SESSNUM_INCREASE
long ASDB_GetSessionByMediaAndSessNum (PASDB_SESS phSESS, char *TapeName, short randomID, short seqNum, char *pSerialNum, long sessionNum, PASDB_SessRec pSessRec, ULONG uFlag);   
#else
long ASDB_GetSessionByMediaAndSessNum (PASDB_SESS phSESS, char *TapeName, short randomID, short seqNum, char *pSerialNum, short sessionNum, PASDB_SessRec pSessRec, ULONG uFlag);   
#endif

short ASDB_GetSessionStatusByMasterJobID (PASDB_SESS phSESS, long commonJobID, long MasterJobID, short sestype, char* szSesPath, long* outSessStatus, ULONG uFlag);

//// FP1
short ASDB_UpdateJobEX(LPBACKUPJOBEX lpJob);
short ASDB_BeginJOBEX(LPBACKUPJOBEX lpJob);
short ASDB_EndJOBEX(LPBACKUPJOBEX lpJob);
short ASDB_BeginSESSIONEX(LPBACKUPJOBEX lpJob);
short ASDB_EndSESSIONEX(LPBACKUPJOBEX lpJob);
short ASDB_SubmitDETAILEX(LPBACKUPJOBEX lpJob);
short ASDB_GetEstimateForRestoreEX(LPBACKUPJOBEX lpJob);
long  ASDB_LPMergePathEX (PASDB_SESS phSESS, PASDB_LPMergePathParmEX pMergePath);

short ASDB_QueryFileListEX(PASDB_SESS phSESS, 
                         char *  sComputerName, 
                         char *  sDir, 
                         BOOL bCaseSensitive,
                         BOOL bIncludeSubDir,
                         DWORD NameID, 
                         BOOL bFirst,
                         DWORD nRequest, 
                         PASDB_DetailExtRecEX * pExt,
                         DWORD *nFound);
short ASDB_FindFileListEX(PASDB_SESS phSESS, DWORD NameID, BOOL bFirst, DWORD nRequest, PASDB_DetailExtRecEX *pExt, DWORD *nFound);
short ASDB_GetSessionDetailListBySesIDEX(PASDB_SESS phSESS, long sesID, BOOL bFirst, PASDB_DetailExtRecEX * pData, UINT * nCnt);
short ASDB_RegisterTapeDrvEX(PASDB_SESS phSESS,REGISTERTAPEDRVEX *pReg);

//////////////////////////////////
#endif // ASDB_D2D2T

#ifdef ASDB_SQLEXPRESS_2005  

short ASDB_LoginToComputerWriteMode_EX(char *pComputerName, char* pDatabase, PASDB_SESS phSESS);
short ASDB_MergeCatDummyEntry_EX (PASDB_SESS phSESS, PASDB_SESS phSESS_FILE, PASDB_DetailExtRec FTS, long g_nFTS, short * status);                     
long  ASDB_GetDatabaseTotalRecord_EX (PASDB_SESS phSESS, char* DatabaseName, char* TableName, long* nCount, int iFlag);
#endif // ASDB_SQLEXPRESS_2005  

//#ifdef ASDB_BAB115_SP3

short ASDB_SetTapeFlagEx(PASDB_SESS phSESS, char *pTapeName, short RandomID, short SeqNum, char *pSerialNum, long TapeFlagEx);
short ASDB_GetTapeFlagEx(PASDB_SESS phSESS, char *pTapeName, short RandomID, short SeqNum, char *pSerialNum, long *pTapeFlagEx);
short ASDB_GetSessionCountNotMigratedWithTape(PASDB_SESS phSESS, char *pTapeName, short RandomID, short SeqNum, char *pSerialNum, long *pSessionCount);
short ASDB_InsertJobScript(PASDB_SESS phSESS, long jobid, long flag, long parentjobid, long masterjobid, long nCnt, char *pJobScript);
short ASDB_UpdateJobScript(PASDB_SESS phSESS, long jobid, long flag, long parentjobid, long masterjobid, long nCnt, char *pJobScript);
short ASDB_GetJobScript(PASDB_SESS phSESS, long jobid, long *p_flag, long *p_parentjobid, long *p_masterjobid, long *nCnt, char *pJobScript);
short ASDB_GetJobScriptSize(PASDB_SESS phSESS, long jobid, long *nCnt);
short ASDB_SetSessFlagEx(PASDB_SESS phSESS, long sessid, long flagex, long encralgo);
short ASDB_GetSessFlagEx(PASDB_SESS phSESS, long sessid, long *p_flagex, long *p_encralgo);
long  ASDB_GetSessRecordExt(PASDB_SESS phSESS, PASDB_SessRecExtParm pSessRecExtParm, PASDB_SessRecExt  pSessRecExt);
long  ASDB_GetLastSuccessfulSessionInfo( PASDB_SESS phSESS, PASDB_GetSessInfoParm pGetSessInfoParm, PASDB_SessRec *pSessRec, UINT * nCnt);
long  ASDB_GetMigrationStatus (PASDB_SESS phSESS, PASDB_MigrationStatusParm pMigrationStatusParm, long* mStatus);
long  ASDB_GetLastMasterJobId (PASDB_SESS phSESS, PASDB_GetLastJobIdParm pGetLastJobIdParm, long* pMasterJobID);
long  ASDB_GetLastSuccessfulMasterJobId (PASDB_SESS phSESS, PASDB_GetLastJobIdParm pGetLastJobIdParm, long* pMasterJobID); //sonle01 20071102


long ASDB_GetAsLog (PASDB_SESS phSESS, PASDB_AsLogParm pLogParm, BOOL bFirst, PASDB_AsLogRec *pLogRec, UINT * pCnt);
long ASDB_SetAsLog (PASDB_SESS phSESS, PASDB_AsLogRec pLogRec);
long ASDB_SetAsLogList (PASDB_SESS phSESS, PASDB_AsLogRec pLogRec, long nCNT);
long ASDB_DeleteAsLog (PASDB_SESS phSESS, PASDB_AsLogParm pLogParm);
long ASDB_GetJobIDListAsLog (PASDB_SESS phSESS, PASDB_AsLogJobIDParm pLogParm, PASDB_AsLogJobIDRec *pLogRec, UINT * pCnt);
#ifdef BAB_LOGGER_GUISUPPORT
long ASDB_GetTimeRangeAsLog (PASDB_SESS phSESS, PASDB_AsLogTimeRangeParm pTimeRangeParm, long *starttime, long *endtime);
#endif

long ASDB_WriteJobLogFile (PASDB_SESS phSESS, char* fileName, PASDB_AsLogRec pLogRec);

long ASDB_DBMaintenance(PASDB_SESS phSESS,  unsigned long uFlag, int oPtion);
long ASDB_PruneAsLogByTime(PASDB_SESS phSESS, DWORD PruneCutOffDate,unsigned long uFlag);
#define ASDB_PruneAsLogByTimeW(phSESSW, PruneCutOffDate, uFlag) ASDB_PruneAsLogByTime((PASDB_SESS)phSESSW, PruneCutOffDate, uFlag)

char *ASDB_GetNameFromSession(PASDB_SESS phSESS, long SessionID, unsigned long ulID,char *lpString,unsigned short sBufSize);
char *ASDB_GetStringFromSession(PASDB_SESS phSESS, long SessionID, unsigned long ulID,char *lpString,unsigned short sBufSize);
short ASDB_CATLOGDB_QueryFirst(PASDB_SESS phSESS, 
                         char *  sComputerName, 
                         char *  sDir, 
                         BOOL bCaseSensitive,
                         BOOL bIncludeSubDir,
                         char *pattern,
                         unsigned long *handle);
short ASDB_CATLOGDB_QueryNext(PASDB_SESS phSESS, unsigned long handle, DWORD nRequest, PASDB_DetailExtRec *pExt, DWORD *nFound);
short ASDB_CATLOGDB_QueryNextEx(PASDB_SESS phSESS, unsigned long handle, DWORD nRequest, PASDB_DetailExtRecEX *pExt, DWORD *nFound);
short ASDB_CATLOGDB_QueryClose(PASDB_SESS phSESS, unsigned long handle);    

long ASDB_SetDomainMemberInfo (PASDB_SESS phSESS, PASDB_TOPOLOGY_INFO pTopologyRec);
long ASDB_GetServerListInDomain (PASDB_SESS phSESS, char* primaryName, UINT * nCnt, PASDB_TOPOLOGY_INFO *pTopologyRec);
long ASDB_GetDomainMemberInfoByServerName (PASDB_SESS phSESS, char* serverName, PASDB_TOPOLOGY_INFO pTopologyRec);
long ASDB_DeleteDomainMemberInfoByServerName (PASDB_SESS phSESS, char* serverName);

long ASDB_SetNodeInfo(PASDB_SESS phSESS, PASDB_NodeInfo pNodeInfoRec);
long ASDB_GetNodeInfoList (PASDB_SESS phSESS, PASDB_NodeInfoParm pNodeInfoParm, UINT * nCnt, PASDB_NodeInfo *pNodeInfoRec);
long ASDB_GetNodeInfo(PASDB_SESS phSESS, PASDB_NodeInfoParm pNodeInfoParm, PASDB_NodeInfo pNodeInfoRec);
long ASDB_DeleteNodeInfo(PASDB_SESS phSESS, PASDB_NodeInfoParm pNodeInfoParm);
long ASDB_UpdateNodeInfoForCrashedJob(PASDB_SESS phSESS, PASDB_NodeInfoParm pNodeInfoParm);

//#endif //ASDB_BAB115_SP3

// XOsoft ////////
long ASDB_GetScenarioInfo (PASDB_SESS phSESS, PASDB_Scenario_Info_Parm pScenarioInfoParm, UINT * nCnt, PASDB_Scenario_Info *pScenarioInfo);
long ASDB_GetScenarioXML (PASDB_SESS phSESS, PASDB_Scenario_XML_Parm pScenarioXMLParm, PASDB_Scenario_XML pScenarioXML, long* bufferSize, char* pXML);
long ASDB_CreateScenarioInfo (PASDB_SESS phSESS, PASDB_Scenario_Info pScenarioInfo);
long ASDB_CreateScenarioXML (PASDB_SESS phSESS, PASDB_Scenario_XML pScenarioXML, long bufferSize, char* pXML);
long ASDB_UpdateScenarioInfo (PASDB_SESS phSESS, PASDB_Scenario_Info pScenarioInfo, long flag);
long ASDB_UpdateScenarioXML (PASDB_SESS phSESS, PASDB_Scenario_XML pScenarioXML, long bufferSize, char* pXML, long flag);
long ASDB_DeleteScenarioInfo (PASDB_SESS phSESS, PASDB_Scenario_Info_Parm pScenarioInfoParm);
long ASDB_DeleteScenarioXML (PASDB_SESS phSESS, PASDB_Scenario_XML_Parm pScenarioXMLParm);
long ASDB_GetScenarioXMLSize (PASDB_SESS phSESS, PASDB_Scenario_XML_Parm pScenarioXMLParm, long* bufferSize);

//////////////////

#ifdef BAB_CPDB_SUPPORT
short ASDB_RegisterTapeDrvEX_CPDB(PASDB_SESS phSESS,REGISTERTAPEDRVEX *pReg, char *ComputerName);
short ASDB_RegisterTapeDrv_CPDB(PASDB_SESS phSESS,REGISTERTAPEDRV *pReg, char *ComputerName);
short ASDB_HeadCleanTapeDrv_CPDB(PASDB_SESS phSESS,char AdapterID,char AdapterBusID,char scsiid,char lun,char *ComputerName);
short ASDB_UsageTimeTapeDrv_CPDB(PASDB_SESS phSESS,char AdapterID,char AdapterBusID,char scsiid,char lun,long *pUsagetime,char *ComputerName);
short ASDB_RegisterTapeDrvErr_CPDB(PASDB_SESS phSESS,REGISTERTAPEDRVERROR *pReg,char *ComputerName);
short ASDB_RegisterTape_CPDB(PASDB_SESS phSESS,FORMATTAPE *pTape,char *ComputerName);
short ASDB_RegisterTapeEx_CPDB(char *pComputerName, PASDB_SESS phSESS,FORMATTAPE *pTape,char *ComputerName);
short ASDB_FormatTape_CPDB(PASDB_SESS phSESS,FORMATTAPE *pTape,char *ComputerName);
short ASDB_FormatTapeEx_CPDB(char *pComputerName, PASDB_SESS phSESS,FORMATTAPE *pTape,char *ComputerName);
short ASDB_EraseTape_CPDB(PASDB_SESS phSESS,FORMATTAPE *pTape,char *ComputerName);
short ASDB_EraseTapeEx_CPDB(char *pComputerName, PASDB_SESS phSESS,FORMATTAPE *pTape,char *ComputerName);
short ASDB_UpdateTape_CPDB(PASDB_SESS phSESS,UPDATETAPE *pTape,char *ComputerName);
short ASDB_UpdateTapeEx_CPDB(char * pComputerName, PASDB_SESS phSESS,UPDATETAPEEX *pTape,char *ComputerName);

short ASDB_BeginJOB_CPDB(LPBACKUPJOB lpJob,char *ComputerName);
short ASDB_UpdateJobEX_CPDB(LPBACKUPJOBEX lpJob,char *ComputerName);
short ASDB_UpdateJob_CPDB(LPBACKUPJOB lpJob,char *ComputerName);
short ASDB_BeginJOBEX_CPDB(LPBACKUPJOBEX lpJob,char *ComputerName);
short ASDB_EndJOBEX_CPDB(LPBACKUPJOBEX lpJob,char *ComputerName);
short ASDB_EndJOB_CPDB(LPBACKUPJOB lpJob,char *ComputerName);
short ASDB_BeginCOPYJOB_CPDB(LPCOPYJOB lpJob,char *ComputerName);
short ASDB_EndCOPYJOB_CPDB(LPCOPYJOB lpJob,char *ComputerName);

short ASDB_AddRHost(PASDB_SESS phSESS, PASDB_NodeRec pNodeRec);
short ASDB_UpdateRHost(PASDB_SESS phSESS, PASDB_NodeRec pNodeRec);
short ASDB_DeleteRHost(PASDB_SESS phSESS, PASDB_NodeRec pNodeRec);
short ASDB_UpdateRHostNetworkType(PASDB_SESS phSESS, PASDB_NodeRec pNodeRec, long mask);
PASDB_NodeRec ASDB_EnumRHosts(PASDB_SESS phSESS, UINT *Cnt);
DWORD ASDB_CreatePoolEx(PASDB_SESS phSESS,PASDB_PoolRec pASPool,char *fromHost);
PASDB_PoolRecEx ASDB_GetPoolListEx(PASDB_SESS phSESS,short *pCnt);

#ifdef ARC_SAVE_NODEINFO
DWORD ASDB_RHostCount(PASDB_SESS phSESS, PASDB_NodeRec pNodeRec);
DWORD ASDB_GetRHost(PASDB_SESS phSESS, PASDB_NodeRec pNodeRec);
DWORD ASDB_RHostCountByTypeList(PASDB_SESS phSESS, UINT *nCnt, UINT** typeList);
DWORD ASDB_GetRHostListByTypeList(PASDB_SESS phSESS,UINT *nTypeCnt, UINT** typeList,UINT *nCnt,PASDB_NodeRec *pNodeRecList);
#endif

#endif

////// Central Setup (Oriole) ////////////
long ASDB_SetSetupMemberDB(PASDB_SetupMemberDB pSetupMember);
long ASDB_RemoveSetupMemberDB(char * szPrimaryMachineName);
int ASDB_IsSQLConnect(char* SQLuser, char* SQLpass);
//////////////////////////////////////////

//// SQLNODE INFO /////////
long ASDB_GetSQLNodeInfo(PASDB_SQLNODE_INFOParm pSqlNodeInfoParm, PASDB_SQLNODE_INFO pSqlNodeInfo);

///// VMWARE////////
long  ASDB_CreateVM(PASDB_SESS phSESS, PASDB_VMRec pVMRec, long xmlSize, char* pXML); // If same (proxyName/esxName/vmName)exists then overwrite it. (11/13/2008)  
long ASDB_UpdateVM (PASDB_SESS phSESS, PASDB_VMRec pVMRec, long  xmlSize, char* pXML, long flag);
long ASDB_UpdateVMWithoutXML (PASDB_SESS phSESS, PASDB_VMRec pVMRec, long flag);
long ASDB_DeleteVM(PASDB_SESS phSESS, PASDB_VMRecParm pVMRecParm);
long ASDB_GetVMProxyList (PASDB_SESS phSESS, UINT * nCnt, PASDB_VMNameRec* pVMNameRec);
long ASDB_GetVMListByProxy (PASDB_SESS phSESS, PASDB_VMNameRec  pVMNameRec, UINT * nCnt, PASDB_VMRec* pVMRec);
long ASDB_GetVMXmlSize (PASDB_SESS phSESS, PASDB_VMRecParm  pVMRecParm, long* xmlSize);
long ASDB_GetVMXml (PASDB_SESS phSESS, PASDB_VMRecParm  pVMRecParm, long * xmlSize, char * pXML); 
long ASDB_GetVMListByESX (PASDB_SESS phSESS, PASDB_VMEsxNameRec  pVMEsxNameRec, UINT * nCnt, PASDB_VMRec* pVMRec);
long ASDB_GetVMEsxListByProxyName (PASDB_SESS phSESS, PASDB_VMNameRec  pVMProxyNameRec, UINT * nCnt, PASDB_VMNameRec* pVMEsxNameRec);

long ASDB_GetTotalJobsInfoByJobIDs (PASDB_SESS phSESS, PULONG puJobIDs, UINT nCnt, PASDB_TotalJobsInfo  pTotalJobsInfo) ;

DWORD ASDB_AdvReportRemoteExecuteCML ( 
					CHAR *computer,
					unsigned long nComputerNameLength,
					CHAR *CmdArgs,
					unsigned long nCmdArgsLength,
					unsigned long ulReportType,
					unsigned long uFlag,		// future use
					unsigned long *nBufferSize,	// Size of Output File	
					char **OutBuffer);			// Data Buffer of Output File 

#ifdef BAB_DBAPI_JOBHISTORY
//yinzh01 2006/12/07
//piStatisticsTimes is an integer array with 9 elements
//First element is job number
//from second to last element are job status and corresponding number.
//element and status map:
//1 - active, 2 - finish, 3 - cancel, 4 - fail, 5 - incomplete, 6 - idle, 7 - waiting, 8 - not attempt fail
long ASDB_GetStatisticsByCommonJobIdDate( PASDB_SESS phSESS, long lCommonJobId, long lStartTime, long lEndTime, int *piJobNumber, PASDB_JobStatistics pstStatistics );

long ASDB_GetExecutionHistoryByCommonJobIdDate(PASDB_SESS phSESS, long lCommonJobId, long lStartTime, long lEndTime, int *piCount, PASDB_JobExecutionHistory *pJobExecutionHistory);

long ASDB_GetDeviceInfoByJobId(PASDB_SESS phSESS, int iJobId, int *piDeviceCount, PASDB_TapeDrvRec *pDeviceInfo );

long ASDB_GetMediaInfoByJobId(PASDB_SESS phSESS, int iJobId, PASDB_TapeDrvRec pstTapeDrv, int *piMediaCount, PASDB_TapeRec *pMediaInfo );

//The last parameter has the same meanings as last parameter in ASDB_GetStatisticsByCommonJobIdDate
long ASDB_GetAgentsStatisticsByJobId(PASDB_SESS phSESS, int iJobId, int *piAgentNum, PASDB_AgentStatistics pstStatistics );

long ASDB_GetSessionStatisticsByJobId(PASDB_SESS phSESS, int iJobId, int *piSessionNumber, PASDB_SessionStatistics pstStatistics );

//piStatisticsTimes is an integer array with 5 element
//0 - complete, 1 - incomplete, 2 - failed, 3 - do not require migration, 4 - migrate in future
long ASDB_GetMigrationStatisticsByJobId(PASDB_SESS phSESS, int iJobId, int *piSessionNumber, PASDB_MigrationStatistics pstMigrationStatistics );

long ASDB_GetAgentDetailInfoByJobIdHostName(PASDB_SESS phSESS, int iJobId, char *pszHostname, int *piAgentCount, PASDB_AgentDetailInfo *pAgentDetailInfo );

long ASDB_GetDeviceInfoByJobIdHostname(PASDB_SESS phSESS, int iJobId, char *pszHostname, int *piDeviceCount, PASDB_TapeDrvRec *pDeviceInfo );

long ASDB_GetMediaInfoByJobIdHostname(PASDB_SESS phSESS, int iJobId, char *pszHostname, PASDB_TapeDrvRec pstTapeDrv, int *piMediaCount, PASDB_TapeRec *pMediaInfo );

long ASDB_GetSessionStatisticsByJobIdHostname(PASDB_SESS phSESS, int iJobId, char *pszHostname, int *piSessionNumber, PASDB_SessionStatistics pstStatistics );

long ASDB_GetMigrationStatisticsByJobIdHostname(PASDB_SESS phSESS, int iJobId, char *pszHostname, int *piSessionNumber, PASDB_MigrationStatistics pstMigrationStatistics );

long ASDB_GetMigrationStatusByJobSession(PASDB_SESS phSESS, int iJobId, int iSessionId, int *piNeedMigration, int *piMigrationStatus );

long ASDB_GetDeviceInfoByJobIdSessId(PASDB_SESS phSESS, int iJobId, int iSessId, int *piDeviceCount, PASDB_TapeDrvRec *pDeviceInfo );

long ASDB_GetMediaInfoByJobIdSessId(PASDB_SESS phSESS, int iJobId, int iSessId, PASDB_TapeDrvRec pstTapeDrv, int *piMediaCount, PASDB_TapeRec *pMediaInfo );

long ASDB_GetDeviceInfoByHostnameDate(PASDB_SESS phSESS, char *pszHostname, char *pszMember, long lStartTime, long lEndTime, int *piDeviceCount, PASDB_TapeDrvRec *pDeviceInfo );

long ASDB_GetMediaInfoByHostnameDate(PASDB_SESS phSESS, char *pszHostname, char *pszMember, long lStartTime, long lEndTime, PASDB_TapeDrvRec pstTapeDrv, int *piMediaCount, PASDB_TapeRec *pMediaInfo );

long ASDB_GetAgentsByHostnameStatusDate(PASDB_SESS phSESS, int iStatus, char *pszHostname, char *pszMembername, long lStartTime, long lEndTime, int *piAgentCount, PASDB_Agent *pstAgentList );

long ASDB_GetSessionByJobIdHostname(PASDB_SESS phSESS, int iJobId, char *pszAgentName, int *piCount, PASDB_SessRec *pstSessRec );

long ASDB_GetExecutionHistoryByNameDate(PASDB_SESS phSESS, char *pszAgentName, char *pszJobName, long lStartTime, long lEndTime, int *piCount, PASDB_JobExecutionHistory *pJobExecutionHistory);

long ASDB_GetExecutionHistoryByNameDateEx(PASDB_SESS phSESS, char *pszServerName, char *pszAgentName, char *pszJobName, long lStartTime, long lEndTime, int *piCount, PASDB_JobExecutionHistory *pJobExecutionHistory);
#endif


DWORD ASDB_UpdateASDBBackupLog(char *pPrimaryServer, PASDB_BackupLogRec pBackUpLogRec);

// R12.v (10/28/2008)
short ASDB_GetStagingStagingGroup(PASDB_SESS phSESS, PASDB_StagingGroupParm pStagingGroupParm, PASDB_StagingGroup *pStagingGroup, UINT * nCnt);

// (oripine) 4/10/2007 
short ASDB_GetDeviceListEX(PASDB_SESS phSESS, PASDB_TapeDrvExRec* dList, DWORD * nCnt);

int ASDB_IsMember (char* primaryName, char* memberName, short lFlag);

//sonle01 20070618
short ASDB_GetNotMigratedCnt(PASDB_SESS phSESS, char *ServerName, long CommonJobID, long MasterJobID, int *Count);

//Begin sonle01 20070917
short ASDB_SetMaxServerMemory(PASDB_SESS phSESS, long maxServerMemory);

short ASDB_GetMaxServerMemoryInfo(PASDB_SESS phSESS, long *min, long *max, long *current);
//End sonle01 20070917

// R12.5 kimwo01 (10/27/2008)
DWORD ASDB_RecoverStagingInfo(PASDB_SESS phSESS, long lFlag);

#ifdef ARC_ROLE_MANAGEMENT

#ifdef USERPROFILE_UNICODE_IMPL

int ASDB_AddAuthUser(PASDB_SESSW phSESSW, PASDB_AuthUserRec pAuthUserRec, int *roleidLst, int roleCnt);
int ASDB_UpdateAuthUser(PASDB_SESSW phSESSW, wchar_t *username, int usertype, char *password, int *status, char *check, wchar_t *description, int *roleidLst, int roleCnt);
int ASDB_GetAuthUserRec(PASDB_SESSW phSESSW, wchar_t *username, int usertype, int *id, char *password, int passLen, int *status, char *check, int checkLen, wchar_t *description, int descLen);
int ASDB_GetAuthUserAll(PASDB_SESSW phSESSW, PASDB_AuthUserRec *pAuthUserLst, int *cnt);
int ASDB_DelAuthUserRec(PASDB_SESSW phSESSW, wchar_t *username, int usertype);
int ASDB_AuthGetRoleList(PASDB_SESSW phSESSW, PASDB_AuthRoleRec *pAuthRoleLst, int *cnt);
int ASDB_AuthGetUserListByRoleId(PASDB_SESSW phSESSW, int roleid, PASDB_AuthUserRec *pAuthUserLst, int *cnt);
int ASDB_AuthGetRoleListByUserId(PASDB_SESSW phSESSW, int userid, PASDB_AuthRoleRec * pAuthRoleLst, int *cnt);
int ASDB_AuthAddRole(PASDB_SESSW phSESSW, PASDB_AuthRoleRec pAuthRoleRec);
int ASDB_AuthGetRole(PASDB_SESSW phSESSW, int* roleid,wchar_t *roleName, PASDB_AuthRoleRec pAuthRoleRec);
int ASDB_AuthDelRole(PASDB_SESSW phSESSW, int * roleId, wchar_t *roleName);
int ASDB_AuthUpdateRole(PASDB_SESSW phSESSW, int roleid, wchar_t * roleName, wchar_t * roleDesc, char * check, char* roleOperBuf);
int ASDB_AuthAddUserListByRoleId(PASDB_SESSW phSESSW, int roleId, int * userIdLst, int* userCnt);
int ASDB_AuthDelUserListByRoleId(PASDB_SESSW phSESSW, int roleId, int * userIdLst, int* userCnt);

#else //USERPROFILE_UNICODE_IMPL

int ASDB_AddAuthUser(PASDB_SESS phSESS, PASDB_AuthUserRec pAuthUserRec, int *roleidLst, int roleCnt);
int ASDB_UpdateAuthUser(PASDB_SESS phSESS, char *username, int usertype, char *password, int *status, char *check, char *description, int *roleidLst, int roleCnt);
int ASDB_GetAuthUserRec(PASDB_SESS phSESS, char *username, int usertype, int *id, char *password, int passLen, int *status, char *check, int checkLen, char *description, int descLen);
int ASDB_GetAuthUserAll(PASDB_SESS phSESS, PASDB_AuthUserRec *pAuthUserLst, int *cnt);
int ASDB_DelAuthUserRec(PASDB_SESS phSESS, char *username, int usertype);
int ASDB_AuthGetRoleList(PASDB_SESS phSESS, PASDB_AuthRoleRec *pAuthRoleLst, int *cnt);
int ASDB_AuthGetUserListByRoleId(PASDB_SESS phSESS, int roleid, PASDB_AuthUserRec *pAuthUserLst, int *cnt);
int ASDB_AuthGetRoleListByUserId(PASDB_SESS phSESS, int userid, PASDB_AuthRoleRec * pAuthRoleLst, int *cnt);
int ASDB_AuthAddRole(PASDB_SESS phSESS, PASDB_AuthRoleRec pAuthRoleRec);
int ASDB_AuthGetRole(PASDB_SESS phSESS, int* roleid,char *roleName, PASDB_AuthRoleRec pAuthRoleRec);
int ASDB_AuthDelRole(PASDB_SESS phSESS, int * roleId, char *roleName);
int ASDB_AuthUpdateRole(PASDB_SESS phSESS, int roleid, char * roleName, char * roleDesc, char * check, char* roleOperBuf);
int ASDB_AuthAddUserListByRoleId(PASDB_SESS phSESS, int roleId, int * userIdLst, int* userCnt);
int ASDB_AuthDelUserListByRoleId(PASDB_SESS phSESS, int roleId, int * userIdLst, int* userCnt);

#endif //USERPROFILE_UNICODE_IMPL

long ASDB_SetPrimaryTopologyId (PASDB_SESS phSESS, int iFlag);

#define ASDB_SetPrimaryTopologyIdW(phSESSW, iFlag) ASDB_SetPrimaryTopologyId((PASDB_SESS)phSESSW, iFlag)
#define ASDB_IsDedupeGFSJOBW(phSESSW, commonjobID, JobNo, ret) ASDB_IsDedupeGFSJOB((PASDB_SESS)phSESSW, commonjobID, JobNo, ret)

#ifdef USERPROFILE_UNICODE_IMPL
int ASDB_GetAuthUserAllW(int dummy);
#else //USERPROFILE_UNICODE_IMPL
int ASDB_GetAuthUserAllW(PASDB_SESSW phSESSW, PASDB_AuthUserRecW *pAuthUserLst, int *cnt);
#endif

#endif

// Oripin (10/17/2007)
PCOMPUTERNAME2 ASDB_GetServerNameList(PASDB_SESS phSESS, UINT nType, UINT *Cnt);
long ASDB_GetTapeDataExByHostID(PASDB_SESS phSESS, PCOMPUTERNAME2 pComName, PTAPEDATAEX pTapeData);

short ASDB_GetDiskSpaceEX(PASDB_SESS phSESS,char *pDrive, unsigned short sDriveBufSize, double *pdbTotal,double *pdbOthers,double *pdbDB,double *pdbFree);

int ASDB_GetServerListForOracle(PASDB_SESS phSESS, unsigned long srchostid, PASDB_SeverHost *pServerHost, UINT *nCnt);

#if 1 //def SIS_SUPPORT
long ASDB_GetSisLastSessionNumber( PASDB_SESS phSESS,PASDB_GetSisSessInfoParm pGetSisSessInfoParm,PULONG pulSessionNumber);
long ASDB_GetSisLastSessionNumberW( PASDB_SESSW phSESSW,PASDB_GetSisSessInfoParmW pGetSisSessInfoParmW,PULONG pulSessionNumber);
long ASDB_SetMigrationSessionForPurge(PASDB_SESS phSESS, PASDB_Staging pStaging);
#endif //SIS_SUPPORT

#if 1 //def Wanhe05_DeDupe_SCAN
PTAPEDATAEXADSCAN ASDB_GetSessionListEx_AdScan(PASDB_SESS phSESS,char* pszAgentNodeName,PTAPEDATAEXADSCAN lpData,UINT *Cnt);
#endif


#ifdef DB_STAGING_GROUP
short ASDB_GetStagingGroup(PASDB_SESS phSESS, PASDB_StagingGroupParm pStagingGroupParm, PASDB_StagingGroup *pStagingGroup, UINT * nCnt);
short ASDB_UpdateStagingGroup(PASDB_SESS phSESS, PASDB_StagingGroup pStagingGroup, long lFlag);
#endif 


// UNICODE SUPPORT

// UNICODE SUPPORT TYPE I - NO CHAR PARAMETER, DEFINE UNICODE INTERFACE
#define ASDB_GetDBSemaphoreHandleW() ASDB_GetDBSemaphoreHandle()
#define ASDB_CryptW(pBuffer, ulSize) ASDB_Crypt(pBuffer, ulSize)
#define ASDB_InitRPCServerW() ASDB_InitRPCServer()
#define ASDB_DeinitRPCServerW() ASDB_DeinitRPCServer()
#define ASDB_ShutDownServerW() ASDB_ShutDownServer()

#define ASDB_GetDevTotalErrNumByIDW(phSESSW, DeviceID, TotalErrors) ASDB_GetDevTotalErrNumByID((PASDB_SESS)phSESSW, DeviceID, TotalErrors)
#define ASDB_GetTotalFilesInSessionBySesIDW(phSESSW, sesID, TotalFiles) ASDB_GetTotalFilesInSessionBySesID((PASDB_SESS)phSESSW, sesID, TotalFiles)
#define ASDB_DeleteDeviceListW(phSESSW, addrList, nCnt) ASDB_DeleteDeviceList((PASDB_SESS)phSESSW, addrList, nCnt)

#define ASDB_GetLastJobIDW(phSESSW, jobID) ASDB_GetLastJobID((PASDB_SESS)phSESSW, jobID)
#define ASDB_DeleteJobListW(phSESSW, addrList, nCnt) ASDB_DeleteJobList((PASDB_SESS)phSESSW, addrList, nCnt)
#define ASDB_DeleteJobListExW(phSESSW, addrList, nCnt) ASDB_DeleteJobListEx((PASDB_SESS)phSESSW, addrList, nCnt)

#define ASDB_GetTapeFirstAndLastKeyW(phSESSW, firstID, lastID) ASDB_GetTapeFirstAndLastKey((PASDB_SESS)phSESSW, firstID, lastID)
#define ASDB_DBCleanSessionW(phSESSW, JobID, SesID, pdwDeleted) ASDB_DBCleanSession((PASDB_SESS)phSESSW, JobID, SesID, pdwDeleted)

#define ASDB_DBCleanUpW(phSESSW, pdwDeleted) ASDB_DBCleanUp((PASDB_SESS)phSESSW, pdwDeleted)
#define ASDB_CleanUpDetailByTapeIDSesNumW(phSESSW, tapeID, SesNum, psDBRec) ASDB_CleanUpDetailByTapeIDSesNum((PASDB_SESS)phSESSW, tapeID, SesNum, psDBRec)
#define ASDB_GetTapeErrListW(phSESSW, DeviceID, pData,  nCnt) ASDB_GetTapeErrList((PASDB_SESS)phSESSW, DeviceID, pData,  nCnt)

#define ASDB_DeleteTapeByIDW(phSESSW, ID, currentDeleted, status) ASDB_DeleteTapeByID((PASDB_SESS)phSESSW, ID, currentDeleted, status)
#define ASDB_GetTotalFilesInTapeByTapeIDW(phSESSW, TapeID, nCnt) ASDB_GetTotalFilesInTapeByTapeID((PASDB_SESS)phSESSW, TapeID, nCnt)

#define ASDB_CheckLocationInUseW(phSESSW, LocationID) ASDB_CheckLocationInUse((PASDB_SESS)phSESSW, LocationID)
#define ASDB_DeleteAllPoolW(phSESSW) ASDB_DeleteAllPool((PASDB_SESS)phSESSW)

#define ASDB_GetTapeListExW(phSESSW, Cnt) ASDB_GetTapeListEx((PASDB_SESS)phSESSW, Cnt)
//#define ASDB_GetTapeListEx2W(phSESSW, Cnt) ASDB_GetTapeListEx2((PASDB_SESS)phSESSW, Cnt)

#define ASDB_StartDatabaseServiceW(phSESSW) ASDB_StartDatabaseService((PASDB_SESS)phSESSW)

#define ASDB_StartBACKUPDAEMONW() ASDB_StartBACKUPDAEMON()
#define ASDB_EndBACKUPDAEMONW() ASDB_EndBACKUPDAEMON()

#define ASDB_StartCOPYDAEMONW() ASDB_StartCOPYDAEMON()
#define ASDB_EndCOPYDAEMONW() ASDB_EndCOPYDAEMON()

#define ASDB_StartDBBACKUPW() ASDB_StartDBBACKUP()
#define ASDB_EndDBBACKUPW() ASDB_EndDBBACKUP()

#define ASDB_GetJobIDW(pusJobID) ASDB_GetJobID(pusJobID)
#define ASDB_StartDBRESTOREW() ASDB_StartDBRESTORE()
#define ASDB_EndDBRESTOREW() ASDB_EndDBRESTORE()

#define ASDB_GetNumberOfRecordsInDBW(phSESSW, nDatabaseID) ASDB_GetNumberOfRecordsInDB((PASDB_SESS)phSESSW, nDatabaseID)

#define ASDB_GetNodeCountByOSW(phSESSW, nCnt) ASDB_GetNodeCountByOS((PASDB_SESS)phSESSW, nCnt)

#define ASDB_GetProfileRightsIDListW(phSESSW, nInCnt, nOutCnt, pProfileIDList) ASDB_GetProfileRightsIDList((PASDB_SESS)phSESSW, nInCnt, nOutCnt, pProfileIDList)
#define ASDB_DeleteProfileRightsByProfileIDListW(phSESSW, nInCnt, pProfileIDList) ASDB_DeleteProfileRightsByProfileIDList((PASDB_SESS)phSESSW, nInCnt, pProfileIDList)
#define ASDB_OpenW(phSESSW, ulDatabaseID, lOpenMode) ASDB_Open((PASDB_SESS)phSESSW, ulDatabaseID, lOpenMode)
#define ASDB_CloseW(phSESSW, ulDatabaseID) ASDB_Close((PASDB_SESS)phSESSW, ulDatabaseID)

#define ASDB_GetDetailExpireAttrW(phSESSW, SessionID, PathID, FileNameID, ExpireAttr) ASDB_GetDetailExpireAttr((PASDB_SESS)phSESSW, SessionID, PathID, FileNameID, ExpireAttr)
#define ASDB_SetDetailExpireAttrW(phSESSW, SessionID, PathID, FileNameID, ExpireAttr) ASDB_SetDetailExpireAttr((PASDB_SESS)phSESSW, SessionID, PathID, FileNameID, ExpireAttr)

#define ASDB_GetTapeRetiredPolicyW(phSESSW, pPolicy) ASDB_GetTapeRetiredPolicy((PASDB_SESS)phSESSW, pPolicy)
#define ASDB_SetTapeRetiredPolicyW(phSESSW, nCnt, pPolicy) ASDB_SetTapeRetiredPolicy((PASDB_SESS)phSESSW, nCnt, pPolicy)

#define ASDB_GetSessionPropertyW(phSESSW, SessionID, pSessRec) ASDB_GetSessionProperty((PASDB_SESS)phSESSW, SessionID, pSessRec)
#define ASDB_SetSessionPropertyW(phSESSW, SessionID, pSessRec) ASDB_SetSessionProperty((PASDB_SESS)phSESSW, SessionID, pSessRec)

#define ASDB_HeapFreeW(lpMem) ASDB_HeapFree(lpMem)

#define ASDB_MsgGetDataW(phSESSW, sessid, lselfid, hselfid,  nCnt,  pData) ASDB_MsgGetData((PASDB_SESS)phSESSW, sessid, lselfid, hselfid,  nCnt, pData)
#define ASDB_MsgSetDataW(phSESSW, sessid, lselfid, hselfid,  nCnt, pData) ASDB_MsgSetData((PASDB_SESS)phSESSW, sessid, lselfid, hselfid,  nCnt, pData)

#define ASDB_SetDebugW(dwDebug) ASDB_SetDebug(dwDebug)

#define ASDB_SetJobMapInfoW(phSESSW, pJobMapInfo, ulFlag) ASDB_SetJobMapInfo((PASDB_SESS)phSESSW, pJobMapInfo, ulFlag)

#define ASDB_GetJobScriptSizeW(phSESSW, jobid, nCnt) ASDB_GetJobScriptSize((PASDB_SESS)phSESSW, jobid, nCnt)
#define ASDB_SetSessFlagExW(phSESSW, sessid, flagex, encralgo) ASDB_SetSessFlagEx((PASDB_SESS)phSESSW, sessid, flagex, encralgo)
#define ASDB_GetSessFlagExW(phSESSW, sessid, p_flagex, p_encralgo) ASDB_GetSessFlagEx((PASDB_SESS)phSESSW, sessid, p_flagex, p_encralgo)

#define ASDB_GetLastSuccessfulMasterJobIdW(phSESSW, pGetLastJobIdParm, pMasterJobID) ASDB_GetLastSuccessfulMasterJobId((PASDB_SESS)phSESSW, pGetLastJobIdParm, pMasterJobID)

#define ASDB_DBMaintenanceW(phSESSW, uFlag, oPtion) ASDB_DBMaintenance((PASDB_SESS)phSESSW, uFlag, oPtion)
//#define ASDB_PruneAsLogByTimeW(phSESSW, PruneCutOffDate, uFlag) ASDB_PruneAsLogByTime((PASDB_SESS)phSESSW, PruneCutOffDate, uFlag)

#define ASDB_GetTotalJobsInfoByJobIDsW(phSESSW, PUpuJobIDs, UnCnt, pTotalJobsInfo)  ASDB_GetTotalJobsInfoByJobIDs((PASDB_SESS)phSESSW, PUpuJobIDs, UnCnt, pTotalJobsInfo) 

#define ASDB_GetStatisticsByCommonJobIdDateW(phSESSW, lCommonJobId, lStartTime, lEndTime, piJobNumber, pstStatistics) ASDB_GetStatisticsByCommonJobIdDate((PASDB_SESS)phSESSW, lCommonJobId, lStartTime, lEndTime, piJobNumber, pstStatistics)
#define ASDB_GetAgentsStatisticsByJobIdW(phSESSW, iJobId, piAgentNum, pstStatistics) ASDB_GetAgentsStatisticsByJobId((PASDB_SESS)phSESSW, iJobId, piAgentNum, pstStatistics)
#define ASDB_GetSessionStatisticsByJobIdW(phSESSW, iJobId, piSessionNumber, pstStatistics) ASDB_GetSessionStatisticsByJobId((PASDB_SESS)phSESSW, iJobId, piSessionNumber, pstStatistics)
#define ASDB_GetMigrationStatisticsByJobIdW(phSESSW, iJobId, piSessionNumber, pstMigrationStatistics) ASDB_GetMigrationStatisticsByJobId((PASDB_SESS)phSESSW, iJobId, piSessionNumber, pstMigrationStatistics)
#define ASDB_GetMigrationStatusByJobSessionW(phSESSW, iJobId, iSessionId, piNeedMigration, piMigrationStatus ) ASDB_GetMigrationStatusByJobSession((PASDB_SESS)phSESSW, iJobId, iSessionId, piNeedMigration, piMigrationStatus )
#define ASDB_SetMaxServerMemoryW(phSESSW, maxServerMemory) ASDB_SetMaxServerMemory((PASDB_SESS)phSESSW, maxServerMemory)
#define ASDB_GetMaxServerMemoryInfoW(phSESSW, min, max, current) ASDB_GetMaxServerMemoryInfo((PASDB_SESS)phSESSW, min, max, current)

#define ASDB_HeadCleanTapeDrvW(phSESSW, AdapterID, AdapterBusID, scsiid, lun) ASDB_HeadCleanTapeDrv((PASDB_SESS)phSESSW, AdapterID, AdapterBusID, scsiid, lun)
#define ASDB_UsageTimeTapeDrvW(phSESSW, AdapterID, AdapterBusID, scsiid, lun, pUsagetime) ASDB_UsageTimeTapeDrv((PASDB_SESS)phSESSW, AdapterID, AdapterBusID, scsiid, lun, pUsagetime)
#define ASDB_GetSessRecListW(phSESSW, JobTapeID, bType, pdbSRec,  nCnt, bFirst) ASDB_GetSessRecList((PASDB_SESS)phSESSW, JobTapeID, bType, pdbSRec,  nCnt, bFirst)
#define ASDB_GetDetailListByCompoundKeyListW(phSESSW, KeyList, nCnt, pExt) ASDB_GetDetailListByCompoundKeyList((PASDB_SESS)phSESSW, KeyList, nCnt, pExt)
#define	ASDB_GetFileByTapeViewW(phSESSW, SessID,nRequest,bFirst,Cnt, pdstat) ASDB_GetFileByTapeView((PASDB_SESS)phSESSW, SessID,nRequest,bFirst,Cnt, pdstat)
#define ASDB_GetSessionListExW(phSESSW, lpData, Cnt) ASDB_GetSessionListEx((PASDB_SESS)phSESSW, lpData, Cnt)
// R12.v (10/20/2008)
#define ASDB_GetSessionListEx2W(phSESSW, lpData, begin_sesstime, end_sesstime, Cnt, lFlag) ASDB_GetSessionListEx2((PASDB_SESS)phSESSW, lpData, begin_sesstime, end_sesstime, Cnt, lFlag)

#define	ASDB_GetFileByTapeViewExW(phSESSW, SessID,nRequest,bFirst,Cnt,pdstat) ASDB_GetFileByTapeViewEx((PASDB_SESS)phSESSW, SessID,nRequest,bFirst,Cnt,pdstat)

#define ASDB_GetScenarioInfoW(phSESSW, pScenarioInfoParm, nCnt, pScenarioInfo) ASDB_GetScenarioInfo((PASDB_SESS)phSESSW, pScenarioInfoParm, nCnt, pScenarioInfo)
#define ASDB_GetScenarioXMLW(phSESSW, pScenarioXMLParm, pScenarioXML, bufferSize, pXML) ASDB_GetScenarioXML((PASDB_SESS)phSESSW, pScenarioXMLParm, pScenarioXML, bufferSize, pXML)
#define ASDB_CreateScenarioInfoW(phSESSW, pScenarioInfo) ASDB_CreateScenarioInfo((PASDB_SESS)phSESSW, pScenarioInfo)
#define ASDB_CreateScenarioXMLW(phSESSW, pScenarioXML, bufferSize, pXML) ASDB_CreateScenarioXML((PASDB_SESS)phSESSW, pScenarioXML, bufferSize, pXML)
#define ASDB_UpdateScenarioInfoW(phSESSW, pScenarioInfo, flag) ASDB_UpdateScenarioInfo((PASDB_SESS)phSESSW, pScenarioInfo, flag)
#define ASDB_UpdateScenarioXMLW(phSESSW, pScenarioXML, bufferSize, pXML, flag) ASDB_UpdateScenarioXML((PASDB_SESS)phSESSW, pScenarioXML, bufferSize, pXML, flag)
#define ASDB_DeleteScenarioInfoW(phSESSW, pScenarioInfoParm) ASDB_DeleteScenarioInfo((PASDB_SESS)phSESSW, pScenarioInfoParm)
#define ASDB_DeleteScenarioXMLW(phSESSW, pScenarioXMLParm) ASDB_DeleteScenarioXML((PASDB_SESS)phSESSW, pScenarioXMLParm)
#define ASDB_GetScenarioXMLSizeW(phSESSW, pScenarioXMLParm, bufferSize) ASDB_GetScenarioXMLSize((PASDB_SESS)phSESSW, pScenarioXMLParm, bufferSize)

#define ASDB_CreateVMW(phSESSW, pVMRec, xmlSize, pXML) ASDB_CreateVM((PASDB_SESS)phSESSW, pVMRec, xmlSize, pXML) // If same (proxyName/esxName/vmName)exists then overwrite it. (11/13/2008)  
#define ASDB_UpdateVMW(phSESSW, pVMRec, xmlSize, pXML, flag) ASDB_UpdateVM((PASDB_SESS)phSESSW, pVMRec, xmlSize, pXML, flag)
#define ASDB_UpdateVMWithoutXMLW(phSESSW, pVMRec, flag) ASDB_UpdateVMWithoutXML((PASDB_SESS)phSESSW, pVMRec, flag)
#define ASDB_DeleteVMW(phSESSW, pVMRecParm) ASDB_DeleteVM((PASDB_SESS)phSESSW, pVMRecParm)
#define ASDB_GetVMProxyListW(phSESSW, nCnt, pVMNameRec) ASDB_GetVMProxyList((PASDB_SESS)phSESSW, nCnt, pVMNameRec)
#define ASDB_GetVMListByProxyW(phSESSW, pVMNameRec, nCnt, pVMRec) ASDB_GetVMListByProxy((PASDB_SESS)phSESSW, pVMNameRec, nCnt, pVMRec)
#define ASDB_GetVMXmlSizeW(phSESSW, pVMRecParm, xmlSize) ASDB_GetVMXmlSize((PASDB_SESS)phSESSW, pVMRecParm, xmlSize)
#define ASDB_GetVMXmlW(phSESSW, pVMRecParm, xmlSize, pXML) ASDB_GetVMXml((PASDB_SESS)phSESSW, pVMRecParm, xmlSize, pXML)
#define ASDB_GetVMListByESXW(phSESSW, pVMEsxNameRec, nCnt, pVMRec) ASDB_GetVMListByESX((PASDB_SESS)phSESSW, pVMEsxNameRec, nCnt, pVMRec)
#define ASDB_GetVMEsxListByProxyNameW(phSESSW, pVMProxyNameRec, nCnt, pVMEsxNameRec) ASDB_GetVMEsxListByProxyName((PASDB_SESS)phSESSW, pVMProxyNameRec, nCnt, pVMEsxNameRec)

#define ASDB_GetDBATableListExW(phSESSW, lpData, Cnt) ASDB_GetDBATableListEx((PASDB_SESS)phSESSW, lpData, Cnt)
//sonle01 20080218 #define ASDB_GetDBADatabaseListExW(phSESSW, lpData, Cnt) ASDB_GetDBADatabaseListEx((PASDB_SESS)phSESSW, lpData, Cnt)
#define ASDB_GetServerListExW(phSESSW,nType, Cnt) ASDB_GetServerListEx((PASDB_SESS)phSESSW,nType, Cnt)
//sonle01_200802 #define ASDB_GetVolumeListExW(phSESSW, lpData, Cnt) ASDB_GetVolumeListEx((PASDB_SESS)phSESSW, lpData, Cnt)

#define ASDB_GetDataList1ExW(phSESSW, lpData, Cnt) ASDB_GetDataList1Ex((PASDB_SESS)phSESSW, lpData, Cnt)
#define ASDB_GetDataListExW(phSESSW, lpData, Cnt) ASDB_GetDataListEx((PASDB_SESS)phSESSW, lpData, Cnt)
//PTAPEDATAEX ASDB_GetDataList1ExW(PASDB_SESSW phSESSW,PTAPEDATAEX lpData,UINT *Cnt);
//PTAPEDATAEX ASDB_GetDataListExW(PASDB_SESSW phSESSW,PTAPEDATAEX lpData,UINT *Cnt);
// END OF UNICODE SUPPORT TYPE I


BOOL ASDB_IsRunningW(wchar_t *pComputerName);
short ASDB_LoginW(PASDB_SESSW phSESSW);
short ASDB_LoginToComputerW(wchar_t *pComputerName,PASDB_SESSW phSESSW);
short ASDB_LoginToComputerWriteModeW(wchar_t *pComputerName,PASDB_SESSW phSESSW);
short ASDB_LogoutW(PASDB_SESSW phSESSW);
short ASDB_ConnectToDBServerW(wchar_t *pComputerName, PASDB_SESSW phSESSW);
short ASDB_DisconnectFromDBServerW(PASDB_SESSW phSESSW);
int ASDB_IsSQLConnectW(wchar_t* SQLuser, wchar_t* SQLpass);

//wchar_t *  ASDB_GetPrimaryNameW(wchar_t *  sBuf, short length, DWORD *method);
long  ASDB_SetPrimaryDBInfoW(wchar_t * machine, 
                           long machineType,                  // in
                           wchar_t * user,                       // in
                           wchar_t * pwd,                        // in
                           PSERVER_ACCOUNT_INFO_EXW pdbInfo);  // in
long  ASDB_GetPrimaryDBInfoW(wchar_t * machine, 
                           long machineType,                  // in
                           wchar_t * user,                       // in
                           wchar_t * pwd,                        // in
                           PSERVER_ACCOUNT_INFO_EXW pdbInfo);  // in
short ASDB_CleanDBHandleW(PASDB_SESSW phSESSW);
//BOOL ASDB_IsConnectedW(wchar_t * netDrive);

short ASDB_GetDiskSpaceW(PASDB_SESSW phSESSW,wchar_t *pDrive,double *pdbTotal,double *pdbOthers,double *pdbDB,double *pdbFree);
PCOMPUTERNAMEW ASDB_GetComputerListW(PASDB_SESSW phSESSW,UINT *Cnt);

short ASDB_GetMediaProperty3W(PASDB_SESSW phSESSW,wchar_t *pTapeName,short RandomID,short SeqNum,PASDB_TapeRecW pASMediaW);
wchar_t  *ASDB_GetStringW(PASDB_SESSW phSESSW,unsigned long ulID,wchar_t *lpString,unsigned short sBufSize, unsigned int *cp_flag);
wchar_t  *ASDB_GetRestoreStringW(PASDB_SESSW phSESSW,unsigned long ulID,wchar_t *lpString,unsigned short sBufSize, unsigned int *cp_flag, wchar_t * seperator);
wchar_t  *ASDB_GetSessionStringW(PASDB_SESSW phSESSW,unsigned long ulID,wchar_t *lpString,unsigned short sBufSize, unsigned int *cp_flag);
wchar_t  *ASDB_GetHostStringW(PASDB_SESSW phSESSW,unsigned long ulID,wchar_t *lpString,unsigned short sBufSize, unsigned int *cp_flag);
wchar_t  *ASDB_GetVolumeStringW(PASDB_SESSW phSESSW,unsigned long ulID,wchar_t *lpString,unsigned short sBufSize, unsigned int *cp_flag);
wchar_t  *ASDB_GetHostStringBySessionW(PASDB_SESSW phSESSW,unsigned long ulID,wchar_t *lpString,unsigned short sBufSize, unsigned int *cp_flag);
unsigned long ASDB_GetStringIDOnlyW(PASDB_SESSW phSESSW, wchar_t *lpString);
unsigned long ASDB_GetHostStringIDOnlyW(PASDB_SESSW phSESSW, wchar_t *lpString);
unsigned long ASDB_GetStringIDW(PASDB_SESSW phSESSW, wchar_t *lpString);
wchar_t  *ASDB_GetNameW(PASDB_SESSW phSESSW,unsigned long ulID,wchar_t *lpString,unsigned short sBufSize, unsigned int *cp_flag);
unsigned long ASDB_GetNameIDOnlyW(PASDB_SESSW phSESSW,wchar_t *lpName);
unsigned long ASDB_GetNameIDW(PASDB_SESSW phSESSW,wchar_t *lpName);
wchar_t  *ASDB_GetNameFromSessionW(PASDB_SESSW phSESSW, long SessionID, unsigned long ulID,wchar_t *lpString,unsigned short sBufSize, unsigned int *cp_flag);
wchar_t  *ASDB_GetStringFromSessionW(PASDB_SESSW phSESSW, long SessionID, unsigned long ulID,wchar_t *lpString,unsigned short sBufSize, unsigned int *cp_flag);
/*
short ASDB_CATLOGDB_QueryFirstW(PASDB_SESSW phSESSW, wchar_t *  sComputerName, wchar_t *  sDir, BOOL bCaseSensitive,
									  BOOL bIncludeSubDir, wchar_t *pattern, unsigned long *handle);
short ASDB_CATLOGDB_QueryNextW(PASDB_SESSW phSESSW, unsigned long handle, DWORD nRequest, PASDB_DetailExtRec *pExt, DWORD *nFound);
short ASDB_CATLOGDB_QueryNextExW(PASDB_SESSW phSESSW, unsigned long handle, DWORD nRequest, PASDB_DetailExtRecEX *pExt, DWORD *nFound);
short ASDB_CATLOGDB_QueryCloseW(PASDB_SESSW phSESSW, unsigned long handle);
*/
PNAMELISTW ASDB_GetNameListW(PASDB_SESSW phSESSW, PNAMELISTW pNameList,unsigned long *nItems);


PVERSIONDATA ASDB_GetVersionHistoryW(PASDB_SESSW phSESSW, PTAPEDATA lpData,UINT *Cnt);
PVERSIONDATA ASDB_GetTapeSessionInfoW(PASDB_SESSW phSESSW,PTAPEDATA lpData);


short ASDB_GetDeviceRecByAddrW(PASDB_SESSW phSESSW, DWORD dbAddr, PASDB_TapeDrvRecW tDRecW);
short ASDB_GetSessionAndTapeRecBySesIDW(PASDB_SESSW phSESSW, long SessionID, PASDB_SessRec sRec, PASDB_TapeRecW tRecW);
short ASDB_GetJobRecByIDW(PASDB_SESSW phSESSW, DWORD jobID, PASDB_JobRecW jRecW);
short ASDB_GetSessionAndTapeRecByJobIDW(PASDB_SESSW phSESSW, long JobID, PASDB_SessRec sRec, PASDB_TapeRecW tRecW);
short ASDB_GetSesAndSrcpathBySesIDW(PASDB_SESSW phSESSW, long SessionID, PASDB_SessRec sRec, wchar_t *  path, short len, unsigned int *cp_flag);
short ASDB_GetTapeMediaRecByTapeIDW(PASDB_SESSW phSESSW, long TapeID, PASDB_TapeRecW tRecW);
short ASDB_GetTapeRecordW(PASDB_SESSW phSESSW, wchar_t * pTapeName,short randomID,short seqnum, PASDB_TapeRecW ptRecW);
short ASDB_GetTapeRecordListW(PASDB_SESSW			 phSESSW,			// IN
							 TapeRecListW		 tapeRecListW,		// IN
							 PASDB_TapeRecExListW ASDB_tapeRecListW); // OUT
short ASDB_AddDetailListW(PASDB_SESSW phSESSW, PASDB_DetailExtRec FTS, UINT nFTS, short *pdstat);
short ASDB_CleanUpMBOW(PASDB_SESSW phSESSW, DWORD *pdwDeleted, PASDB_MBOTapeName * pMBOTapeList);


short ASDB_GetTapeMediaRecListW(PASDB_SESSW phSESSW, BOOL bFirst, PASDB_TapeRecW * pTapeListW, UINT * nCnt);
short ASDB_GetJobRecListW(PASDB_SESSW phSESSW, BOOL bFirst, PASDB_JobRecW *pjDBRecW, UINT * nCnt);
//short ASDB_GetSessRecListW(PASDB_SESSW phSESSW, long JobTapeID, BOOL bType, PASDB_SessRec *pdbSRec, UINT * nCnt, BOOL bFirst);
short ASDB_GetSessionDetailListBySesIDW(PASDB_SESSW phSESSW, long sesID, BOOL bFirst, PASDB_DetailExtRecW * pData, UINT * nCnt);
//short ASDB_GetDetailListByCompoundKeyListW(PASDB_SESSW phSESSW, PASDB_DetailCompoundKey KeyList, DWORD nCnt, PASDB_DetailRec *pExt);
short ASDB_SQLFindFileAddrListW(PASDB_SESSW phSESSW, wchar_t *  sPattern, wchar_t *  sHost, BOOL bCaseSensitive, wchar_t *  DefStr, PDWORD * m_pFList, DWORD * nFound, BOOL bFirst);
short ASDB_FindFileListW(PASDB_SESSW phSESSW, DWORD NameID, BOOL bFirst, DWORD nRequest, PASDB_DetailExtRec * pExt, DWORD * nFound);
short ASDB_QueryFileListW(PASDB_SESSW phSESSW, 
                         WCHAR *  sComputerName, 
                         WCHAR *  sDir, 
                         BOOL bCaseSensitive,
                         BOOL bIncludeSubDir,
                         DWORD NameID, 
                         BOOL bFirst,
                         DWORD nRequest, 
                         PASDB_DetailExtRecW * pExt,
                         DWORD *nFound);
/*
short ASDB_GetDetailRecUnderDirNameW(PASDB_SESSW phSESSW, 
                                    wchar_t * pTapeName,
                                    short randomID,
                                    short seqnum,
                                    short sesnum,
                                    wchar_t *  path,
                                    BOOL bFirst,
                                    PASDB_QFAData *pData,
                                    DWORD *nCount);
short ASDB_PruneSessionByTimeW(PASDB_SESSW phSESSW, 
                                 LONG TaskID, 
                                 DWORD PruneCutOffDate, 
                                 DWORD * dwDeleted, 
                                 wchar_t * tapeName, 
                                 short * randomID,
                                 short * sesNum,
                                 BOOL bFirst,
                                 short * status);
*/

short ASDB_GetFileQFADataW(PASDB_SESSW phSESSW, 
                                    wchar_t * pTapeName,
                                    short randomID,
                                    short seqnum,
                                    long sesnum,
									wchar_t *  path,
									wchar_t *  file,
                                    ASDB_QFAData *pData);

short ASDB_GetDeviceListW(PASDB_SESSW phSESSW, PASDB_TapeDrvRecW * dListW, DWORD * nCnt);
PASDB_LocationRecW ASDB_GetLocationListW(PASDB_SESSW phSESSW,int *nCnt);
PASDB_PoolRecW ASDB_GetPoolListW(PASDB_SESSW phSESSW,short *pCnt);
PASDB_TapeRecW ASDB_GetSaveSetListW(PASDB_SESSW phSESSW,wchar_t *pMediaPool,short *pCnt);
PASDB_TapeRecW ASDB_GetScratchSetListW(PASDB_SESSW phSESSW,wchar_t *pMediaPool,short *pCnt);

short ASDB_RemoveMediaFromPoolW(PASDB_SESSW phSESSW,PASDB_TapeRecW pASMediaW, short cnt);
short ASDB_GetLocationByIDW(PASDB_SESSW phSESSW,long LocationID,PASDB_LocationRecW pASLocW);
short ASDB_AssignLocationIDW(PASDB_SESSW phSESSW,wchar_t *pTapeName,short randomid,long LocationID);
short ASDB_DeleteLocationW(PASDB_SESSW phSESSW,PASDB_LocationRecW pASLocW);
short ASDB_AssignMediaToPoolW(PASDB_SESSW phSESSW,PASDB_TapeRecW pASMediaW, short cnt);
short ASDB_NewLocationW(PASDB_SESSW phSESSW,PASDB_LocationRecW pASLocW);
short ASDB_ModifyLocationW(PASDB_SESSW phSESSW,PASDB_LocationRecW pASLocW);
int  ASDB_GetSaveOrScratchCountW(PASDB_SESSW phSESSW,wchar_t *pMediaPool,int SaveOrScratchSet);
DWORD ASDB_GetDefaultSerialNoRangeW(PASDB_SESSW phSESSW,wchar_t *pBase,wchar_t *pRange);
BOOL  ASDB_ValidatePoolRangeW(PASDB_SESSW phSESSW,wchar_t *pMediaPool,wchar_t *pBase,wchar_t *pRange);
DWORD ASDB_SetPoolPropertyW(PASDB_SESSW phSESSW,PASDB_PoolRecW pMediaPoolW);
DWORD ASDB_CreatePoolW(PASDB_SESSW phSESSW,PASDB_PoolRecW pASPoolW);
short ASDB_SetMediaPropertyW(PASDB_SESSW phSESSW,PASDB_TapeRecW pASMediaW);
DWORD ASDB_DeletePoolW(PASDB_SESSW phSESSW,wchar_t *pMediaPool);
int  ASDB_SetMediaSetStatusW(PASDB_SESSW phSESSW,wchar_t *pTapeName,short RandomID,short SeqNum,short SetStatus);
int  ASDB_SetMediaStatusW(PASDB_SESSW phSESSW,wchar_t *pTapeName,short RandomID,short SeqNum,short mStatus);

DWORD ASDB_GetPoolPropertyW(PASDB_SESSW phSESSW,wchar_t *pMediaPool, PASDB_PoolRecW pASPoolW);
int  ASDB_ExistTapeInPoolW(PASDB_SESSW phSESSW,wchar_t *pMediaPool,wchar_t *pTapeName,short RandomID,short SeqNum);
short ASDB_GetMediaRecByKeyW(PASDB_SESSW phSESSW, wchar_t *pTapeName,short RandomID, short SeqNum, PASDB_TapeRecW mRecW);
DWORD ASDB_ExistPoolW(PASDB_SESSW phSESSW,wchar_t *pMediaPool);
int  ASDB_SetRotationPoolW(PASDB_SESSW phSESSW,wchar_t *pSetName,int nPools,int nDaily,int nWeekly,int nMonthly);
DWORD ASDB_GetPoolProperty2W(PASDB_SESSW phSESSW,wchar_t *pMediaPool,PASDB_PoolRecW pASPoolW);
BOOL ASDB_ValidateSerialNoW(PASDB_SESSW phSESSW,wchar_t *pComputerName,wchar_t *pMediaPool,wchar_t *pSerialNo);
DWORD ASDB_GetNextSerialNoW(PASDB_SESSW phSESSW,wchar_t *pComputerName,wchar_t *pMediaPool,wchar_t *pNextSerialNo);
int ASDB_GetPoolNameBySerialNoW(PASDB_SESSW phSESSW,wchar_t *pComputerName,wchar_t *pSerialNo,wchar_t *pMediaPool);
DWORD ASDB_ReturnNextSerialNoW(PASDB_SESSW phSESSW,wchar_t *pMediaPool,wchar_t *pReturnedNextSerialNo);
DWORD ASDB_DeActivatePoolW(PASDB_SESSW phSESSW,wchar_t *pMediaPool);
DWORD ASDB_ActivatePoolW(PASDB_SESSW phSESSW,wchar_t *pMediaPool);
int  ASDB_MoveMediaToScratchSetW(PASDB_SESSW phSESSW,wchar_t *pMediaPool,int nRetentionTime,int nMinSaveCopies,int *pnMoved);
int  ASDB_GetRotationStartDateW(PASDB_SESSW phSESSW, wchar_t *pSetName,short usJobType,unsigned short *pDosStartDate);
int  ASDB_SetMediaBKTypeW(PASDB_SESSW phSESSW,wchar_t *pTapeName,short RandomID,short SeqNum,short BKType);
short ASDB_GetMediaProperty2W(PASDB_SESSW phSESSW,wchar_t *pSerialNum,PASDB_TapeRecW pASMediaW);

PTAPEDATA ASDB_GetVolumeListW(PASDB_SESSW phSESSW, PTAPEDATA lpData,UINT *Cnt);
PTAPEDATA ASDB_GetServerListW(PASDB_SESSW phSESSW,UINT nType,UINT *Cnt);
PTAPEDATA ASDB_GetSessionListW(PASDB_SESSW phSESSW,PTAPEDATA lpData,UINT *Cnt);
//PASDB_DetailRec ASDB_GetFileByTapeViewW(PASDB_SESSW phSESSW, short SessID,short nRequest,short bFirst,UINT *Cnt,short *pdstat);
PTAPEDATA ASDB_GetDataListW(PASDB_SESSW phSESSW,PTAPEDATA lpData,UINT *Cnt);
PTAPEDATA ASDB_GetDataList1W(PASDB_SESSW phSESSW,PTAPEDATA lpData,UINT *Cnt);
PTAPEDATA ASDB_GetDBADatabaseListW(PASDB_SESSW phSESSW,PTAPEDATA lpData,UINT *Cnt);
PTAPEDATA ASDB_GetTapeListW(PASDB_SESSW phSESSW, UINT *Cnt);


//PTAPEDATAEX ASDB_GetSessionListExW(PASDB_SESSW phSESSW,PTAPEDATAEX lpData,UINT *Cnt);
PVERSIONDATAEXW ASDB_GetVersionHistoryExW(PASDB_SESSW phSESSW, PTAPEDATAEX lpData,UINT *Cnt);
PVERSIONDATAEX2 ASDB_GetVersionHistoryEx2W(PASDB_SESSW phSESSW, PTAPEDATAEX2 lpData,UINT *Cnt);
PVERSIONDATAEXW ASDB_GetTapeSessionInfoExW(PASDB_SESSW phSESSW,PTAPEDATAEX lpData);
short ASDB_GetJobRecByIDExW(PASDB_SESSW phSESSW, DWORD jobID, PASDB_JobExRecW jRecW);
short ASDB_GetJobRecListExW(PASDB_SESSW phSESSW, BOOL bFirst, PASDB_JobExRecW *pjDBRecW, UINT * nCnt);
short ASDB_GetSessRecListByJobNoW(PASDB_SESSW phSESSW, long JobNo, BOOL bType, PASDB_SessRec *pdbSRec, UINT * nCnt);
//PASDB_DetailRec ASDB_GetFileByTapeViewExW(PASDB_SESSW phSESSW, unsigned long SessID,short nRequest,short bFirst,UINT *Cnt,short *pdstat);
PVERSIONDATAEXW ASDB_GetRestoreItemListW(PASDB_SESSW phSESSW, PVERSIONDATAEXW lpData,UINT *Cnt);

PASDB_TapeRec ASDB_GetTapeRecListW(PASDB_SESSW phSESSW, UINT *Cnt,BOOL bFirstTime,short *pdstat);

short ASDB_MergeCreateSessionW(PASDB_SESSW phSESSW, 
                                 long tapeid,
                                 long starttime,
                                 long endtime,
                                 long sessionType,
                                 long sessnum,
                                 short sessionMethod,
                                 long  sessionFlags,
                                 wchar_t *  rootDirectory, 
                                 wchar_t *  wsAddress,
                                 wchar_t *  description,
                                 wchar_t *  ownerName,
                                 wchar_t *  g_Path,
                                 PASDB_SessRec psDBRec);
short ASDB_MergeCatRegisterTapeW(PASDB_SESSW phSESSW, PASDB_KeyTapeName ptDBKey, short *pTapeID, short * status);
short ASDB_MergeCatRegisterTapeExW(PASDB_SESSW phSESSW, PASDB_KeyTapeName ptDBKey, long *pTapeID, short * status);
short ASDB_MergeCatModifySessInfoBySessIDW(PASDB_SESSW phSESSW, long SessID, long  totalkb, long  totalfiles);
short ASDB_MergeCatDummyEntryW(PASDB_SESSW phSESSW, PASDB_DetailExtRecW FTS, long g_nFTS, short * status);


short ASDB_RegisterTapeW(PASDB_SESSW phSESSW,FORMATTAPE *pTape);
short ASDB_FormatTapeW(PASDB_SESSW phSESSW,FORMATTAPE *pTape);
short ASDB_EraseTapeW(PASDB_SESSW phSESSW,FORMATTAPE *pTape);
short ASDB_UpdateTapeW(PASDB_SESSW phSESSW,UPDATETAPE *pTape);
short ASDB_RegisterTapeDrvW(PASDB_SESSW phSESSW,REGISTERTAPEDRV *pReg);
//short ASDB_HeadCleanTapeDrvW(PASDB_SESSW phSESSW,wchar_t AdapterID,wchar_t AdapterBusID,wchar_t scsiid,wchar_t lun);
//short ASDB_UsageTimeTapeDrvW(PASDB_SESSW phSESSW,wchar_t AdapterID,wchar_t AdapterBusID,wchar_t scsiid,wchar_t lun,long *pUsagetime);
short ASDB_RegisterTapeDrvErrW(PASDB_SESSW phSESSW,REGISTERTAPEDRVERROR *pReg);
short ASDB_GetTapeInfoW(PASDB_SESSW phSESSW,wchar_t * pTapeName,short randomID,short seqnum, PASDB_TapeRec ptDBRec);
short ASDB_GetTapeInfoBySerialNoW(PASDB_SESSW phSESSW,wchar_t *pSerialNo, PASDB_TapeRec ptDBRec);
short ASDB_FormatTapeExW(wchar_t *pComputerName, PASDB_SESSW phSESSW,FORMATTAPE *pTape);
short ASDB_EraseTapeExW(wchar_t *pComputerName, PASDB_SESSW phSESSW,FORMATTAPE *pTape);
short ASDB_UpdateTapeExW(wchar_t *pComputerName, PASDB_SESSW phSESSW,UPDATETAPEEX *pTape);
short ASDB_RegisterTapeExW(wchar_t *pComputerName, PASDB_SESSW phSESSW,FORMATTAPE *pTape);

PASDB_TapeErrRec ASDB_GetTapeErrListByTapeIDW(PASDB_SESSW phSESSW,  long tapeid, short * nCnt);
PASDB_TapeErrRec ASDB_GetAllTapeErrListByTapeIDW(PASDB_SESSW phSESSW,  long tapeid, short * nCnt);
PASDB_TapeRecW ASDB_GetLogicTapeRecW(PASDB_SESSW phSESSW, wchar_t * pTapeName, short RandomID, short * nCnt);
wchar_t * ASDB_GetSessionPathExtW(PASDB_SESSW phSESSW, long hostid, long pathid, wchar_t *  path, short len, unsigned int *cp_flag);

DWORD ASDB_GetDatabaseTypeW(wchar_t *  pComputerName);
wchar_t *  ASDB_GetDatabaseNameW(wchar_t * pServer, wchar_t *  dbname, long size);
DWORD ASDB_IsDatabaseInstalledW(wchar_t *  pComputerName);


short ASDB_UpdateJobW(LPBACKUPJOB lpJob);

short ASDB_BeginJOBW(LPBACKUPJOB lpJob);
short ASDB_EndJOBW(LPBACKUPJOB lpJob);
short ASDB_BeginSESSIONW(LPBACKUPJOB lpJob);
short ASDB_EndSESSIONW(LPBACKUPJOB lpJob);
short ASDB_SubmitDETAILW(LPBACKUPJOB lpJob);

short ASDB_BeginCOPYJOBW(LPCOPYJOB lpJob);
short ASDB_EndCOPYJOBW(LPCOPYJOB lpJob);
short ASDB_BeginCOPYSESSIONW(LPCOPYJOB lpJob);
short ASDB_EndCOPYSESSIONW(LPCOPYJOB lpJob);
short ASDB_SubmitCOPYDETAILW(LPCOPYJOB lpJob);

short ASDB_GetEstimateForRestoreW(LPBACKUPJOB lpJob);
void ASDB_GetDBServerStatusW(wchar_t *pServerName,DB_STATUS* pdbStatus);
void ASDB_GetDBServerConfigW(wchar_t *pServerName,DB_CONFIG* pdbConfig);
void ASDB_SetDBServerConfigW(wchar_t *pServerName,DB_CONFIG* pdbConfig);
short ASDB_InitializeDatabaseW(wchar_t *pServerName,wchar_t *dbname);
short ASDB_GetDatabaseAccessModeW(wchar_t *pServerName,int* pAccessMode);
short ASDB_SetDatabaseAccessModeW(wchar_t *pServerName,int AccessMode);
short ASDB_SetMinDiskFreeSpaceW(wchar_t *pServerName,unsigned long ulMinDiskThreshold);
unsigned long ASDB_GetMinDiskFreeSpaceW(wchar_t *pServerName);

wchar_t * ASDB_GetLastDBErrorTextW(PASDB_SESSW phSESSW, long sz, wchar_t *sBuf, unsigned short sBufSize);


long ASDB_ASAPI_DeleteFileW(PASDB_SESSW phSESSW,PASDB_FILEITEM pFileItemBuffer);
long ASDB_ASAPI_QueryFileItemListW(PASDB_SESSW phSESSW, 
                                  wchar_t *  sComputerName, 
                                  wchar_t *  sDir, 
                                  BOOL bCaseSensitive,
                                  BOOL bIncludeSubDir,
                                  DWORD NameID, 
                                  DWORD tCutoffTime,
                                  BOOL bFirst,
                                  DWORD nRequest, 
                                  PASDB_FILEITEM * pExt,
                                  DWORD *nFound);



short ASDB_UpdateNodeListW(PASDB_SESSW phSESSW, PASDB_NodeRecW pNodeListW, short nCnt);
short ASDB_InsertNodeListW(PASDB_SESSW phSESSW, PASDB_NodeRecW pNodeListW, short nCnt);
short ASDB_DeleteNodeListW(PASDB_SESSW phSESSW, PASDB_NodeRecW pNodeListW, short nCnt);
PASDB_NodeRecW ASDB_GetNodeListW(PASDB_SESSW phSESSW, int nOSType, int *nCnt);
PASDB_NodeRecW ASDB_FindNodeByOSW(PASDB_SESSW phSESSW, int nOSType, wchar_t *pNodeName, wchar_t *pNodeAddress);

long  ASDB_GetProfileRightsListW(PASDB_SESSW phSESSW, long nInCnt, long *nOutCnt, long *pProfileIDList, UNIASPROFILEW *pProfileRightsList);
long  ASDB_SetProfileRightsListW(PASDB_SESSW phSESSW, long nInCnt, UNIASPROFILEW *pProfileRightsList);
long  ASDB_GetProfileUserIDListByProfileIDListW(PASDB_SESSW phSESSW, long nInCnt, long *nOutCnt, long *pProfileIDList, UNIASUSERPROFILEW *pProfileUserList);
long  ASDB_SetProfileUserListW(PASDB_SESSW phSESSW, long nInCnt, UNIASUSERPROFILEW *pProfileUserList);
long  ASDB_DeleteProfileUserListW(PASDB_SESSW phSESSW, long nInCnt, UNIASUSERPROFILEW *pProfileUserList);
long  ASDB_GetProfileGroupListByProfileIDListW(PASDB_SESSW phSESSW, long nInCnt, long *nOutCnt, long *pProfileIDList, UNIASGROUPPROFILE *pProfileGroupList);
long  ASDB_SetProfileGroupListW(PASDB_SESSW phSESSW, long nInCnt, UNIASGROUPPROFILE *pProfileGroupList);
long  ASDB_DeleteProfileGroupListW(PASDB_SESSW phSESSW, long nInCnt, UNIASGROUPPROFILE *pProfileGroupList);

short       ASDB_SAPGetDBJobIDW(PASDB_SESSW phSESSW, wchar_t *pSAPJobID);
long        ASDB_SAPGetDBJobIDExW(PASDB_SESSW phSESSW, wchar_t *pSAPJobID);// For ARCserveIT v7.0
PHSAPFILE   ASDB_SAPGetFileListW(PASDB_SESSW phSESSW, wchar_t *pSAPJobID, short *nCnt);
PHSAPJOBID  ASDB_SAPGetSAPJobIDListByFileNameW(PASDB_SESSW phSESSW, wchar_t *pComputerName,wchar_t *pUserID,wchar_t *pPath,wchar_t *pFileName,short *nCnt);
PHSAPJOBID  ASDB_SAPGetSAPJobIDListByUserIDW(PASDB_SESSW phSESSW, wchar_t *pComputerName,wchar_t *pUserID,short *nCnt);
short       ASDB_SAPGetJobInfoW(PASDB_SESSW phSESSW, wchar_t *pSAPJobID,PHSAPJOB phSAPJob);
short       ASDB_SAPGetSessionInfoW(PASDB_SESSW phSESSW, wchar_t *pSAPJobID,wchar_t *pComputerName,wchar_t *pPath,wchar_t *pFileName,PHSAPSESSION phSAPSession);

short ASDB_GetTapeMediaRecListExW(PASDB_SESSW phSESSW, long isValid, long cutofftime, BOOL bFirst, long * nCnt, PASDB_TapeExRecW * pTapeListW);
short ASDB_GetTapeMediaRecByTapeIDExW(PASDB_SESSW phSESSW, long TapeID, PASDB_TapeExRecW tRecW);
short ASDB_GetTapeRecordExW(PASDB_SESSW phSESSW, wchar_t * pTapeName,short randomID,short seqnum, short raidid, wchar_t *serialno, PASDB_TapeExRecW ptRecW);
short ASDB_SetTapeRecordExW(PASDB_SESSW phSESSW, PASDB_TapeExRec ptRec);
short ASDB_SetTapeLocationStatusW(PASDB_SESSW phSESSW, wchar_t * pTapeName,short randomID,short seqnum, short raidid, wchar_t *serialno, long status);

short ASDB_GetTapeRecordExByRandomIDW(PASDB_SESSW phSESSW, short randomID,short seqnum, short raidno, wchar_t *serialno, PASDB_TapeExRecW ptRecW);
short ASDB_GetTotalCountW(PASDB_SESSW phSESSW, wchar_t* querytable, int nType, long* nCnt1, long* nCnt2);
short ASDB_WriteAsLogErrW(PASDB_SESSW phSESSW, PASDB_LogErrRec pLogErrRec);

long ASDB_MMO_LoginW(wchar_t *pComputerName,PASDB_SESSW phSESSW);
long ASDB_MMO_LogoutW(PASDB_SESSW phSESSW);

long ASDB_SetRemoteDatabaseMachineW(wchar_t* computer,
                                    wchar_t* user,
                                    wchar_t* pwd,
                                    unsigned long machinetype,
                                    PSERVER_ACCOUNT_INFO_EXW remoteInfoW);
long ASDB_GetRemoteDatabaseMachineW(wchar_t* computer,
                                    wchar_t* user,
                                    wchar_t* pwd,
                                    unsigned long machinetype,
                                    PSERVER_ACCOUNT_INFO_EXW remoteInfoW);

short ASDB_RegisterTape_71W(PASDB_SESSW phSESSW,FORMATTAPE71 *pTape);
short ASDB_FormatTape_71W(PASDB_SESSW phSESSW,FORMATTAPE71 *pTape);
short ASDB_EraseTape_71W(PASDB_SESSW phSESSW,FORMATTAPE71 *pTape);
short ASDB_UpdateTape71W(PASDB_SESSW phSESSW,UPDATETAPE71 *pTape);
short ASDB_RegisterTapeDrv_71W(PASDB_SESSW phSESSW,REGISTERTAPEDRV71 *pReg);
BOOL ASDB_CanThisTapeBeOverwriteW(PASDB_SESSW phSESSW, PTapeKey ptapekey, short*status);
BOOL ASDB_CanThisTapeBeAppendW(PASDB_SESSW phSESSW, PTapeKey ptapekey, short*status);

short ASDB_GetTapeRecSetW(PASDB_SESSW phSESSW, wchar_t *pTapeName, short randomID, short *nCnt, PASDB_TapeExRecW *ptRecW);

wchar_t  *ASDB_GetOwnerByIDW(PASDB_SESSW phSESSW,unsigned long ulID,wchar_t *lpString,unsigned short sBufSize);

//PTSTR ASDB_GetLastErrorTextW(PDWORD pdwError);
DWORD ASDB_GetProductBuildNumberW(const wchar_t *pComputerName, SERVER_ACCOUNT_INFO *pAccInfo);

DWORD ASDB_DSGetProductDescriptorExW(wchar_t *serverName, DWORD dwProdId, ASProduct *pASProduct, PSERVER_ACCOUNT_INFO pAccInfo);
DWORD ASDB_IsDatabaseInstalled2W(PASDB_SESSW phSESSW, wchar_t *computer, DWORD *pdwIsInstalled);


PASDB_TapeRecW ASDB_GetGlobalScratchSetListW(PASDB_SESSW phSESSW,short *pCnt);

#ifdef SESSNUM_INCREASE
long ASDB_GetTapeSeqEndW(PASDB_SESSW phSESSW, wchar_t *pTapeName, long RandomID, long SesNum, ULONG *pSeqNum);
long ASDB_SetTapeSeqEndW(PASDB_SESSW phSESSW, wchar_t *pTapeName, long Randomid, long SesNum, ULONG   SeqNum);
#else
long ASDB_GetTapeSeqEndW(PASDB_SESSW phSESSW, wchar_t *pTapeName, long RandomID, short SesNum, ULONG *pSeqNum);
long ASDB_SetTapeSeqEndW(PASDB_SESSW phSESSW, wchar_t *pTapeName, long Randomid, short SesNum, ULONG   SeqNum);
#endif

long ASDB_GetTapeSpanSizeW(PASDB_SESSW phSESSW, wchar_t *pTapeName, long RandomID, short SeqNum, ULONG *phSize, ULONG *plSize );
long ASDB_SetTapeSpanSizeW(PASDB_SESSW phSESSW, wchar_t *pTapeName, long RandomID, short SeqNum, ULONG   hSize, ULONG   lSize );

long ASDB_GetCatalogFileNameW(wchar_t    *serverName, 
							 wchar_t    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 BOOL bCreateFolder, 
							 wchar_t *tszCatalog, 
							 UINT nLength);
long ASDB_GetCatalogFileNameExW(wchar_t    *serverName, 
							 wchar_t    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 BOOL bCreateFolder, 
							 ULONG   ulFlags,
							 wchar_t *tszCatalog, 
							 UINT nLength);
long ASDB_DeleteCatalogFileW( wchar_t *serverName, 
							 wchar_t    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber);
long ASDB_PruneCatalogFileByTimeW(wchar_t *serverName, 
								 DWORD PruneCutOffDate);
long ASDB_PurgeCatalogFilesW(wchar_t    *serverName, 
							wchar_t    *szTapeName,
							ULONG   ulTapeNumber,
							ULONG   ulRandomID,
							ULONG   ulSessionNumber);
long ASDB_IsCatalogFileExistW(wchar_t    *serverName, 
							 wchar_t    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber);
long ASDB_CopyCatalogFileW(wchar_t    *serverName, 
						  wchar_t    *szTapeNameDest,
						  ULONG   ulTapeNumberDest,
						  ULONG   ulRandomIDDest,
						  ULONG   ulSessionNumberDest,
						  wchar_t    *szTapeNameSrc,
						  ULONG   ulTapeNumberSrc,
						  ULONG   ulRandomIDSrc,
						  ULONG   ulSessionNumberSrc);
long ASDB_RenameCatalogFileW(wchar_t    *serverName, 
							wchar_t    *szTapeNameDest,
							ULONG   ulTapeNumberDest,
							ULONG   ulRandomIDDest,
							ULONG   ulSessionNumberDest,
							wchar_t    *szTapeNameSrc,
							ULONG   ulTapeNumberSrc,
							ULONG   ulRandomIDSrc,
							ULONG   ulSessionNumberSrc,
							int nDisplayFormat);
BOOL ASDB_IsCatalogDBEnabledW(wchar_t *serverName);
long ASDB_GetCatalogDBHomeW(wchar_t *serverName, wchar_t *path, long limit);
long ASDB_GetCatalogTapeFolderW(wchar_t    *serverName, 
							   wchar_t    *szTapeName,
							   ULONG   ulTapeNumber,
							   ULONG   ulRandomID,
							   ULONG   ulSessionNumber,
							   wchar_t    *tszTapeFolder, 
							   long    limit);

long ASDB_MergeCatalogFileW(wchar_t *computer, 
				   wchar_t    *szTapeName,
				   ULONG   ulTapeNumber,
				   ULONG   ulRandomID,
				   ULONG   ulSessionNumber);


long ASDB_SetMuxW(wchar_t *computer, 
					 wchar_t    *szTapeName,
					 ULONG   ulRandomID,
					 ULONG   ulSessionNumber,
					 char    *pMuxData, 
					 ULONG   ulLength);

long ASDB_GetMuxW(wchar_t *computer, 
							 wchar_t    *szTapeName,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 char    **pMuxData, 
							 ULONG   *pulLength);

long ASDB_PurgeMuxFilesW(wchar_t    *serverName, 
							wchar_t    *szTapeName,
							ULONG   ulTapeNumber,
							ULONG   ulRandomID,
							ULONG   ulSessionNumber);



short ASDB_MergeCatMessageDummyEntryW(PASDB_SESSW phSESSW, PASDB_MsgRec MsgRecord, long g_nMsgRecord, short * status);
short ASDB_MergeCatMsgDatDummyEntryW(PASDB_SESSW phSESSW, PASDB_MsgDatRec MsgDatRecord, long g_nMsgDatRecord, short * status);
short ASDB_MsgGetObjectW(PASDB_SESSW phSESSW, long sessid, long lselfid, long hselfid, PASDB_MsgRecW pRecW);
short ASDB_MsgGetChildrenW(PASDB_SESSW phSESSW, long sessid, long lselfid, long hselfid, BOOL bFirst, PASDB_MsgRecW * pData, UINT * nCnt);
short ASDB_MsgUpdateW(PASDB_SESSW phSESSW, PASDB_MsgRecW MsgRecordW, long g_nMsgRecord, long ulFlags, short * status);
short ASDB_MsgFindFileW(PASDB_SESSW phSESSW, long ulFlags, wchar_t *sHost, wchar_t *sPath, wchar_t *sPattern, BOOL bFirst, PASDB_MsgRecW * pData, UINT * nCnt);
short ASDB_MsgFindFileExW(PASDB_SESSW phSESSW, long ulFlags, wchar_t *sHost, wchar_t *sPath, wchar_t *sPattern, unsigned long begin_sesstime, unsigned long end_sesstime, BOOL bFirst, PASDB_MsgRecW * pData, UINT * nCnt);

DWORD ASDB_ReportRemoteExecuteCMLW( 
					wchar_t *computer,
					wchar_t *Cmdline,
					wchar_t *CmdArgs,
					wchar_t *sReportName,			// Source File Name
					unsigned long InNumLines,	// Size of Source File 
					char * InputBuffer,			// Data Buffer of Source File 
					unsigned long uFlag,
					unsigned long *NumLines,	// Size of Output File		
					char **ResultOut);			// Data Buffer of Output File 

//ULONG ASDB_DBLogW( IN wchar_t *Str,... );

int  ASDB_SetMediaSetStatus2W(PASDB_SESSW phSESSW,wchar_t *pTapeName,short RandomID,short SeqNum,wchar_t *pSerialNum,short SetStatus);
int  ASDB_SetMediaStatus2W(PASDB_SESSW phSESSW,wchar_t *pTapeName,short RandomID,short SeqNum,wchar_t *pSerialNum,short mStatus);
int  ASDB_SetMediaBKType2W(PASDB_SESSW phSESSW,wchar_t *pTapeName,short RandomID,short SeqNum,wchar_t *pSerialNum,short BKType);
int  ASDB_ExistTapeInPool2W(PASDB_SESSW phSESSW,wchar_t *pMediaPool,wchar_t *pTapeName,short RandomID,short SeqNum,wchar_t *pSerialNum);

#ifdef SESSNUM_INCREASE
long ASDB_GetTapeSeqEnd2W(PASDB_SESSW phSESSW, wchar_t *pTapeName, long RandomID, long SesNum, wchar_t *pSerialNum, ULONG *pSeqNum);
long ASDB_SetTapeSeqEnd2W(PASDB_SESSW phSESSW, wchar_t *pTapeName, long Randomid, long SesNum, wchar_t *pSerialNum, ULONG   SeqNum);
#else
long ASDB_GetTapeSeqEnd2W(PASDB_SESSW phSESSW, wchar_t *pTapeName, long RandomID, short SesNum, wchar_t *pSerialNum, ULONG *pSeqNum);
long ASDB_SetTapeSeqEnd2W(PASDB_SESSW phSESSW, wchar_t *pTapeName, long Randomid, short SesNum, wchar_t *pSerialNum, ULONG   SeqNum);
#endif

long ASDB_GetTapeSpanSize2W(PASDB_SESSW phSESSW, wchar_t *pTapeName, long RandomID, short SeqNum, wchar_t *pSerialNum, ULONG *phSize, ULONG *plSize );
long ASDB_SetTapeSpanSize2W(PASDB_SESSW phSESSW, wchar_t *pTapeName, long RandomID, short SeqNum, wchar_t *pSerialNum, ULONG   hSize, ULONG   lSize );

short ASDB_SetSessionMapW(PASDB_SESSW phSESSW, PASDB_SessionMapW pFromSessionW, PASDB_SessionMapW pToSessionW);
short ASDB_DeleteSessionMapW(PASDB_SESSW phSESSW, PASDB_SessionMapW pSessionW);
short ASDB_GetSessionMapW(PASDB_SESSW phSESSW, PASDB_SessionMapW pSessionW, unsigned long ulFlag, PASDB_SessionMapW pSessionOutW);

short ASDB_SetStagingW(PASDB_SESSW phSESSW, PASDB_Staging pStaging);
short ASDB_GetStagingW(PASDB_SESSW phSESSW, PASDB_StagingParm pStagingParm, BOOL bFirst, PASDB_Staging *pStaging, UINT * nCnt);

#ifdef DB_STAGING_GROUP
short ASDB_GetStagingGroupW(PASDB_SESSW phSESSW, PASDB_StagingGroupParmW pStagingGroupParmW, PASDB_StagingGroupW *pStagingGroupW, UINT * nCnt);
short ASDB_UpdateStagingGroupW(PASDB_SESSW phSESSW, PASDB_StagingGroupW  pStagingGroupW, long lFlag);
#endif 

short ASDB_DeleteStagingW(PASDB_SESSW phSESSW, PASDB_Staging pStaging);
short ASDB_DeleteStagingExW(PASDB_SESSW phSESSW, PASDB_Staging pStaging,ULONG ulFlags);
short ASDB_GetSessionMapListW(PASDB_SESSW phSESSW, PASDB_SessionMapW pSessionW, unsigned long ulFlag, UINT * nCnt, PASDB_SessionMapW *pSessionOutW, UINT uReserved);

short ASDB_GetMasterJobsW(PASDB_SESSW phSESSW, PASDB_MasterJobMapInfoParmW pMasterJobMapParmW, UINT * nCnt, PASDB_MasterJobMapInfoW *pMasterJobMapW);
short ASDB_GetChildJobsW(PASDB_SESSW phSESSW, PASDB_ChildJobMapInfoParm pChildJobMapParm, UINT * nCnt, PASDB_ChildJobMapInfo *pChildJobMap);

long ASDB_CopyCatalogFileExW(wchar_t    *serverName, 
						  wchar_t    *szTapeNameDest,
						  ULONG   ulTapeNumberDest,
						  ULONG   ulRandomIDDest,
						  ULONG   ulSessionNumberDest,
						  wchar_t    *szTapeNameSrc,
						  ULONG   ulTapeNumberSrc,
						  ULONG   ulRandomIDSrc,
						  ULONG   ulSessionNumberSrc,
						  ULONG   ulFlags);

long ASDB_SetSessionGUIDW(PASDB_SESSW phSESSW, PASDB_SessionGUID pSessGUID, ULONG uFlag);
long ASDB_GetSessionGUIDW(PASDB_SESSW phSESSW, PASDB_SessionGUIDParm pSessGUIDParm, PASDB_SessionGUID *pSessGUID, UINT * nCnt);
long ASDB_DeleteSessionGUIDW(PASDB_SESSW phSESSW, PASDB_SessionGUID pSessGUID, ULONG ulFlags);						  
#ifdef SESSNUM_INCREASE
long ASDB_GetSessionSPAPathW(PASDB_SESSW phSESSW, wchar_t *TapeName, short randomID, short seqNum, wchar_t *pSerialNum, long sessionNum, wchar_t* outPath, unsigned short sBufSize, unsigned int *cp_flag, ULONG uFlag);
long ASDB_GetSessionGUIDByMediaAndSessNumW(PASDB_SESSW phSESSW, wchar_t *TapeName, short randomID, short seqNum, wchar_t *pSerialNum, long sessionNum, PASDB_SessionGUIDW pSessGUIDW);
#else
long ASDB_GetSessionSPAPathW(PASDB_SESSW phSESSW, wchar_t *TapeName, short randomID, short seqNum, wchar_t *pSerialNum, short sessionNum, wchar_t* outPath, unsigned short sBufSize, unsigned int *cp_flag, ULONG uFlag);
long ASDB_GetSessionGUIDByMediaAndSessNumW(PASDB_SESSW phSESSW, wchar_t *TapeName, short randomID, short seqNum, wchar_t *pSerialNum, short sessionNum, PASDB_SessionGUIDW pSessGUIDW);
#endif
long ASDB_LPMergePathW(PASDB_SESSW phSESSW, PASDB_LPMergePathParm pMergePath);
short ASDB_SetMigratedSessionsInStagingW(PASDB_SESSW phSESSW, PASDB_TapeInfoParm pStagingTapeInfo, PASDB_TapeInfoParm pMigratedTapeInfo, long lastValidDest);
long ASDB_GetMediaAndSessGUIDInfoByGUIDW(PASDB_SESSW phSESSW, wchar_t*  sessguid, PASDB_SessionGUID *pSessGUID, UINT * nCnt, UINT uFlag);

short ASDB_ValidGetServerHostW(PASDB_SESSW phSESSW, wchar_t* pHostName, ULONG uFlag);
short ASDB_GetServerHostW(PASDB_SESSW phSESSW, PASDB_SeverHostW *pServerHostW, UINT * nCnt, ULONG uFlag);

#ifdef SESSNUM_INCREASE
long ASDB_DeleteSessionByMediaAndSessNumW(PASDB_SESSW phSESSW, wchar_t *TapeName, short randomID, short seqNum, wchar_t *pSerialNum, long sessionNum, ULONG uFlag);
#else
long ASDB_DeleteSessionByMediaAndSessNumW(PASDB_SESSW phSESSW, wchar_t *TapeName, short randomID, short seqNum, wchar_t *pSerialNum, short sessionNum, ULONG uFlag);
#endif

short ASDB_GetTapeRecord2W(PASDB_SESSW phSESSW,wchar_t *pTapeName,short randomID,short seqnum, wchar_t *pSerialNum, PASDB_TapeRecW ptRecW);
short ASDB_MergeCatRegisterTapeEx2W(PASDB_SESSW phSESSW, PASDB_KeyTapeName ptDBKey, wchar_t *pSerialNum, long *pTapeID, short * status, wchar_t* fromHost);


long ASDB_GetCatalogFileNameWithSerialW(wchar_t *computer, 
							 wchar_t    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
 							 wchar_t    *szSerialNo,
							 BOOL bCreateFolder, 
							 wchar_t *tszCatalog, 
							 UINT nLength);

long ASDB_GetCatalogFileNameExWithSerialW(wchar_t *computer, 
							 wchar_t    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 wchar_t    *szSerialNo,
							 BOOL bCreateFolder,
							 ULONG   ulFlags,
							 wchar_t *tszCatalog, 
							 UINT nLength);

long ASDB_IsCatalogFileExistWithSerialW(wchar_t *computer, 
							 wchar_t    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 wchar_t	 *szSerialNo);

long ASDB_CopyCatalogFileExWithSerialW(wchar_t *computer, 
							 wchar_t    *szTapeNameDest,
							 ULONG   ulTapeNumberDest,
							 ULONG   ulRandomIDDest,
							 ULONG   ulSessionNumberDest,
							 wchar_t    *szSerialNoDest,
							 wchar_t    *szTapeNameSrc,
							 ULONG   ulTapeNumberSrc,
							 ULONG   ulRandomIDSrc,
							 ULONG   ulSessionNumberSrc,
							 wchar_t    *szSerialNoSrc,
							 ULONG   ulFlags);

long ASDB_RenameCatalogFileWithSerialW(wchar_t *computer, 
							 wchar_t    *szTapeNameDest,
							 ULONG   ulTapeNumberDest,
							 ULONG   ulRandomIDDest,
							 ULONG   ulSessionNumberDest,
							 wchar_t    *szSerialNoDest,
							 wchar_t    *szTapeNameSrc,
							 ULONG   ulTapeNumberSrc,
							 ULONG   ulRandomIDSrc,
							 ULONG   ulSessionNumberSrc,
							 wchar_t    *szSerialNoSrc,
							int nDisplayFormat);

long ASDB_DeleteCatalogFileWithSerialW(wchar_t *computer, 
					 wchar_t    *szTapeName,
					 ULONG   ulTapeNumber,
					 ULONG   ulRandomID,
					 ULONG   ulSessionNumber,
					 wchar_t    *szSerialNo);

long ASDB_PurgeCatalogFilesWithSerialW(wchar_t *computer, 
					 wchar_t    *szTapeName,
					 ULONG   ulTapeNumber,
					 ULONG   ulRandomID,
					 ULONG   ulSessionNumber,
					 wchar_t    *szSerialNo);


long ASDB_MergeCatalogFileWithSerialW(wchar_t *computer, 
				   wchar_t    *szTapeName,
				   ULONG   ulTapeNumber,
				   ULONG   ulRandomID,
				   ULONG   ulSessionNumber,
				   wchar_t    *szSerialNo);

long ASDB_GetCatalogTapeFolderWithSerialW(wchar_t *computer, 
					 wchar_t    *szTapeName,
					 ULONG   ulTapeNumber,
					 ULONG   ulRandomID,
					 ULONG   ulSessionNumber,
					 wchar_t    *szSerialNo,
					   wchar_t *tszTapeFolder, 
					   long limit);

   
short ASDB_SmartDeleteStagingW(PASDB_SESSW phSESSW, PASDB_Staging pStaging);

#ifdef SESSNUM_INCREASE
long ASDB_GetSessionByMediaAndSessNumW(PASDB_SESSW phSESSW, wchar_t *TapeName, short randomID, short seqNum, wchar_t *pSerialNum, long sessionNum, PASDB_SessRec pSessRec, ULONG uFlag);   
#else
long ASDB_GetSessionByMediaAndSessNumW(PASDB_SESSW phSESSW, wchar_t *TapeName, short randomID, short seqNum, wchar_t *pSerialNum, short sessionNum, PASDB_SessRec pSessRec, ULONG uFlag);   
#endif

short ASDB_GetSessionStatusByMasterJobIDW(PASDB_SESSW phSESSW, long commonJobID, long MasterJobID, short sestype, wchar_t* szSesPath, long* outSessStatus, ULONG uFlag);

short ASDB_UpdateJobEXW(LPBACKUPJOBEX lpJob);
short ASDB_BeginJOBEXW(LPBACKUPJOBEXW lpJob);
short ASDB_EndJOBEXW(LPBACKUPJOBEX lpJob);
short ASDB_BeginSESSIONEXW(LPBACKUPJOBEXW lpJob);
short ASDB_EndSESSIONEXW(LPBACKUPJOBEXW lpJob);
short ASDB_SubmitDETAILEXW(LPBACKUPJOBEXW lpJob);
short ASDB_GetEstimateForRestoreEXW(LPBACKUPJOBEX lpJob);
long  ASDB_LPMergePathEXW(PASDB_SESSW phSESSW, PASDB_LPMergePathParmEXW pMergePath);

short ASDB_QueryFileListEXW(PASDB_SESSW phSESSW, 
                         wchar_t *  sComputerName, 
                         wchar_t *  sDir, 
                         BOOL bCaseSensitive,
                         BOOL bIncludeSubDir,
                         DWORD NameID, 
                         BOOL bFirst,
                         DWORD nRequest, 
                         PASDB_DetailExtRecEXW * pExt,
                         DWORD *nFound);
short ASDB_FindFileListEXW(PASDB_SESSW phSESSW, DWORD NameID, BOOL bFirst, DWORD nRequest, PASDB_DetailExtRecEXW *pExt, DWORD *nFound);
short ASDB_GetSessionDetailListBySesIDEXW(PASDB_SESSW phSESSW, long sesID, BOOL bFirst, PASDB_DetailExtRecEXW * pData, UINT * nCnt);
short ASDB_RegisterTapeDrvEXW(PASDB_SESSW phSESSW,REGISTERTAPEDRVEX *pReg);
short ASDB_GetLotusSessionDetailListBySesIDEX(PASDB_SESS phSESS, long sesID, PASDB_DetailExtRecEX* pData);
short ASDB_LoginToComputerWriteMode_EXW(wchar_t *pComputerName, wchar_t* pDatabase, PASDB_SESSW phSESSW);
short ASDB_MergeCatDummyEntry_EXW(PASDB_SESSW phSESSW, PASDB_SESSW phSESSW_FILE, PASDB_DetailExtRecW FTS, long g_nFTS, short * status);                     
long  ASDB_GetDatabaseTotalRecord_EXW(PASDB_SESSW phSESSW, wchar_t* DatabaseName, wchar_t* TableName, long* nCount, int iFlag);

short ASDB_SetTapeFlagExW(PASDB_SESSW phSESSW, wchar_t *pTapeName, short RandomID, short SeqNum, wchar_t *pSerialNum, long TapeFlagEx);
short ASDB_GetTapeFlagExW(PASDB_SESSW phSESSW, wchar_t *pTapeName, short RandomID, short SeqNum, wchar_t *pSerialNum, long *pTapeFlagEx);
short ASDB_GetSessionCountNotMigratedWithTapeW(PASDB_SESSW phSESSW, wchar_t *pTapeName, short RandomID, short SeqNum, wchar_t *pSerialNum, long *pSessionCount);
short ASDB_InsertJobScriptW(PASDB_SESSW phSESSW, long jobid, long flag, long parentjobid, long masterjobid, long nCnt, wchar_t *pJobScript);
short ASDB_UpdateJobScriptW(PASDB_SESSW phSESSW, long jobid, long flag, long parentjobid, long masterjobid, long nCnt, wchar_t *pJobScript);
short ASDB_GetJobScriptW(PASDB_SESSW phSESSW, long jobid, long *p_flag, long *p_parentjobid, long *p_masterjobid, long *nCnt, wchar_t *pJobScript);
long  ASDB_GetSessRecordExtW(PASDB_SESSW phSESSW, PASDB_SessRecExtParmW pSessRecExtParmW, PASDB_SessRecExtW  pSessRecExtW);
long  ASDB_GetLastSuccessfulSessionInfoW( PASDB_SESSW phSESSW, PASDB_GetSessInfoParmW pGetSessInfoParm, PASDB_SessRec *pSessRec, UINT * nCnt);
long  ASDB_GetMigrationStatusW(PASDB_SESSW phSESSW, PASDB_MigrationStatusParm pMigrationStatusParm, long* mStatus);
long  ASDB_GetLastMasterJobIdW(PASDB_SESSW phSESSW, PASDB_GetLastJobIdParm pGetLastJobIdParm, long* pMasterJobID);


long ASDB_GetAsLogW(PASDB_SESSW phSESSW, PASDB_AsLogParmW pLogParmW, BOOL bFirst, PASDB_AsLogRecW *pLogRecW, UINT * pCnt);
long ASDB_SetAsLogW(PASDB_SESSW phSESSW, PASDB_AsLogRecW pLogRecW);
long ASDB_SetAsLogListW(PASDB_SESSW phSESSW, PASDB_AsLogRecW pLogRecW, long nCNT);
long ASDB_DeleteAsLogW(PASDB_SESSW phSESSW, PASDB_AsLogParmW pLogParmW);
long ASDB_GetJobIDListAsLogW(PASDB_SESSW phSESSW, PASDB_AsLogJobIDParmW pLogParmW, PASDB_AsLogJobIDRecW *pLogRecW, UINT * pCnt);
long ASDB_GetTimeRangeAsLogW(PASDB_SESSW phSESSW, PASDB_AsLogTimeRangeParmW pTimeRangeParmW, long *starttime, long *endtime);

long ASDB_WriteJobLogFileW(PASDB_SESSW phSESSW, wchar_t* fileName, PASDB_AsLogRecW pLogRecW);

wchar_t *ASDB_GetNameFromSessionW(PASDB_SESSW phSESSW, long SessionID, unsigned long ulID,wchar_t *lpString,unsigned short sBufSize, unsigned int *cp_flag);
short ASDB_CATLOGDB_QueryFirstW(PASDB_SESSW phSESSW, 
                         wchar_t *  sComputerName, 
                         wchar_t *  sDir, 
                         BOOL bCaseSensitive,
                         BOOL bIncludeSubDir,
                         wchar_t *pattern,
                         unsigned long *handle);
short ASDB_CATLOGDB_QueryFirstExW(PASDB_SESSW phSESSW, 
                         wchar_t *  sComputerName, 
                         wchar_t *  sDir, 
                         BOOL bCaseSensitive,
                         BOOL bIncludeSubDir,
                         unsigned long begin_sesstime, 
						 unsigned long end_sesstime, 
						 wchar_t *pattern,
                         unsigned long *handle);
short ASDB_CATLOGDB_QueryNextW(PASDB_SESSW phSESSW, unsigned long handle, DWORD nRequest, PASDB_DetailExtRecW *pExt, DWORD *nFound);
short ASDB_CATLOGDB_QueryNextExW(PASDB_SESSW phSESSW, unsigned long handle, DWORD nRequest, PASDB_DetailExtRecEXW *pExt, DWORD *nFound);
short ASDB_CATLOGDB_QueryCloseW(PASDB_SESSW phSESSW, unsigned long handle);    

long ASDB_SetDomainMemberInfoW(PASDB_SESSW phSESSW, PASDB_TOPOLOGY_INFO pTopologyRec);
long ASDB_GetServerListInDomainW(PASDB_SESSW phSESSW, wchar_t* primaryName, UINT * nCnt, PASDB_TOPOLOGY_INFOW *pTopologyRecW);
long ASDB_GetDomainMemberInfoByServerNameW(PASDB_SESSW phSESSW, wchar_t* serverName, PASDB_TOPOLOGY_INFO pTopologyRec);
long ASDB_DeleteDomainMemberInfoByServerNameW(PASDB_SESSW phSESSW, wchar_t* serverName);

long ASDB_SetNodeInfoW(PASDB_SESSW phSESSW, PASDB_NodeInfoW pNodeInfoRec);
long ASDB_GetNodeInfoListW(PASDB_SESSW phSESSW, PASDB_NodeInfoParmW pNodeInfoParmW, UINT * nCnt, PASDB_NodeInfoW *pNodeInfoRecW);
long ASDB_GetNodeInfoW(PASDB_SESSW phSESSW, PASDB_NodeInfoParmW pNodeInfoParmW, PASDB_NodeInfoW pNodeInfoRecW);
long ASDB_DeleteNodeInfoW(PASDB_SESSW phSESSW, PASDB_NodeInfoParmW pNodeInfoParmW);
long ASDB_UpdateNodeInfoForCrashedJobW(PASDB_SESSW phSESSW, PASDB_NodeInfoParmW pNodeInfoParmW);


#ifdef BAB_CPDB_SUPPORT
short ASDB_RegisterTapeDrvEX_CPDBW(PASDB_SESSW phSESSW,REGISTERTAPEDRVEX *pReg, wchar_t *ComputerName);
short ASDB_RegisterTapeDrv_CPDBW(PASDB_SESSW phSESSW,REGISTERTAPEDRV *pReg, wchar_t *ComputerName);
short ASDB_HeadCleanTapeDrv_CPDBW(PASDB_SESSW phSESSW,wchar_t AdapterID,wchar_t AdapterBusID,wchar_t scsiid,wchar_t lun,wchar_t *ComputerName);
short ASDB_UsageTimeTapeDrv_CPDBW(PASDB_SESSW phSESSW,wchar_t AdapterID,wchar_t AdapterBusID,wchar_t scsiid,wchar_t lun,long *pUsagetime,wchar_t *ComputerName);
short ASDB_RegisterTapeDrvErr_CPDBW(PASDB_SESSW phSESSW,REGISTERTAPEDRVERROR *pReg,wchar_t *ComputerName);
short ASDB_RegisterTape_CPDBW(PASDB_SESSW phSESSW,FORMATTAPE *pTape,wchar_t *ComputerName);
short ASDB_RegisterTapeEx_CPDBW(wchar_t *pComputerName, PASDB_SESSW phSESSW,FORMATTAPE *pTape,wchar_t *ComputerName);
short ASDB_FormatTape_CPDBW(PASDB_SESSW phSESSW,FORMATTAPE *pTape,wchar_t *ComputerName);
short ASDB_FormatTapeEx_CPDBW(wchar_t *pComputerName, PASDB_SESSW phSESSW,FORMATTAPE *pTape,wchar_t *ComputerName);
short ASDB_EraseTape_CPDBW(PASDB_SESSW phSESSW,FORMATTAPE *pTape,wchar_t *ComputerName);
short ASDB_EraseTapeEx_CPDBW(wchar_t *pComputerName, PASDB_SESSW phSESSW,FORMATTAPE *pTape,wchar_t *ComputerName);
short ASDB_UpdateTape_CPDBW(PASDB_SESSW phSESSW,UPDATETAPE *pTape,wchar_t *ComputerName);
short ASDB_UpdateTapeEx_CPDBW(wchar_t * pComputerName, PASDB_SESSW phSESSW,UPDATETAPEEX *pTape,wchar_t *ComputerName);

short ASDB_BeginJOB_CPDBW(LPBACKUPJOB lpJob,wchar_t *ComputerName);
short ASDB_UpdateJobEX_CPDBW(LPBACKUPJOBEX lpJob,wchar_t *ComputerName);
short ASDB_UpdateJob_CPDBW(LPBACKUPJOB lpJob,wchar_t *ComputerName);
short ASDB_BeginJOBEX_CPDBW(LPBACKUPJOBEXW lpJob,wchar_t *ComputerName);
short ASDB_EndJOBEX_CPDBW(LPBACKUPJOBEX lpJob,wchar_t *ComputerName);
short ASDB_EndJOB_CPDBW(LPBACKUPJOB lpJob,wchar_t *ComputerName);
short ASDB_BeginCOPYJOB_CPDBW(LPCOPYJOB lpJob,wchar_t *ComputerName);
short ASDB_EndCOPYJOB_CPDBW(LPCOPYJOB lpJob,wchar_t *ComputerName);

#ifdef ARC_SAVE_NODEINFO
short ASDB_AddRHostW(PASDB_SESSW phSESSW, PASDB_NodeRecW pNodeRec);
short ASDB_UpdateRHostW(PASDB_SESSW phSESSW, PASDB_NodeRecW pNodeRec);
short ASDB_DeleteRHostW(PASDB_SESSW phSESSW, PASDB_NodeRecW pNodeRec);
short ASDB_UpdateRHostNetworkTypeW(PASDB_SESSW phSESSW, PASDB_NodeRecW pNodeRec, long mask);
PASDB_NodeRecW ASDB_EnumRHostsW(PASDB_SESSW phSESSW, UINT *Cnt);
PASDB_NodeRecW ASDB_EnumRHostsAllW(PASDB_SESSW phSESSW, UINT *Cnt);
short ASDB_SetRHost(PASDB_SESS phSESS, PASDB_NodeRec pNodeRec);
short ASDB_SetRHostW(PASDB_SESSW phSESSW, PASDB_NodeRecW pNodeRec);
long  ASDB_GetRHostW(PASDB_SESSW phSESSW, PASDB_NodeRecW pNodeRec);
short ASDB_SetRHostVerInfW(PASDB_SESSW phSESSW, PASDB_NodeVerInfW nodeVerInf);
#else
short ASDB_AddRHostW(PASDB_SESSW phSESSW, PASDB_NodeRec pNodeRec);
short ASDB_UpdateRHostW(PASDB_SESSW phSESSW, PASDB_NodeRec pNodeRec);
short ASDB_DeleteRHostW(PASDB_SESSW phSESSW, PASDB_NodeRec pNodeRec);
PASDB_NodeRec ASDB_EnumRHostsW(PASDB_SESSW phSESSW, UINT *Cnt);
#endif
DWORD ASDB_CreatePoolExW(PASDB_SESSW phSESSW,PASDB_PoolRec pASPool,wchar_t *fromHost);
PASDB_PoolRecEx ASDB_GetPoolListExW(PASDB_SESSW phSESSW,short *pCnt);
#endif

long ASDB_SetSetupMemberDBW(PASDB_SetupMemberDB pSetupMember);
long ASDB_RemoveSetupMemberDBW(wchar_t * szPrimaryMachineName);

long ASDB_GetSQLNodeInfoW(PASDB_SQLNODE_INFOParmW pSqlNodeInfoParmW, PASDB_SQLNODE_INFOW pSqlNodeInfoW);


DWORD ASDB_AdvReportRemoteExecuteCMLW( 
					wchar_t *computer,
					unsigned long nComputerNameLength,
					wchar_t *CmdArgs,
					unsigned long nCmdArgsLength,
					unsigned long ulReportType,
					unsigned long uFlag,		// future use
					unsigned long *nBufferSize,	// Size of Output File	
					char **OutBuffer);			// Data Buffer of Output File 


long ASDB_GetExecutionHistoryByCommonJobIdDateW(PASDB_SESSW phSESSW, long lCommonJobId, long lStartTime, long lEndTime, int *piCount, PASDB_JobExecutionHistoryW *pJobExecutionHistoryW);
long ASDB_GetDeviceInfoByJobIdW(PASDB_SESSW phSESSW, int iJobId, int *piDeviceCount, PASDB_TapeDrvRecW *pDeviceInfoW );
long ASDB_GetMediaInfoByJobIdW(PASDB_SESSW phSESSW, int iJobId, PASDB_TapeDrvRecW pstTapeDrvW, int *piMediaCount, PASDB_TapeRecW *pMediaInfoW );
long ASDB_GetAgentDetailInfoByJobIdHostNameW(PASDB_SESSW phSESSW, int iJobId, wchar_t *pszHostname, int *piAgentCount, PASDB_AgentDetailInfoW *pAgentDetailInfoW );
long ASDB_GetDeviceInfoByJobIdHostnameW(PASDB_SESSW phSESSW, int iJobId, wchar_t *pszHostname, int *piDeviceCount, PASDB_TapeDrvRecW *pDeviceInfoW );
long ASDB_GetMediaInfoByJobIdHostnameW(PASDB_SESSW phSESSW, int iJobId, wchar_t *pszHostname, PASDB_TapeDrvRecW pstTapeDrvW, int *piMediaCount, PASDB_TapeRecW *pMediaInfoW );
long ASDB_GetSessionStatisticsByJobIdHostnameW(PASDB_SESSW phSESSW, int iJobId, wchar_t *pszHostname, int *piSessionNumber, PASDB_SessionStatistics pstStatistics );
long ASDB_GetMigrationStatisticsByJobIdHostnameW(PASDB_SESSW phSESSW, int iJobId, wchar_t *pszHostname, int *piSessionNumber, PASDB_MigrationStatistics pstMigrationStatistics );
long ASDB_GetDeviceInfoByJobIdSessIdW(PASDB_SESSW phSESSW, int iJobId, int iSessId, int *piDeviceCount, PASDB_TapeDrvRecW *pDeviceInfoW );
long ASDB_GetMediaInfoByJobIdSessIdW(PASDB_SESSW phSESSW, int iJobId, int iSessId, PASDB_TapeDrvRecW pstTapeDrvW, int *piMediaCount, PASDB_TapeRecW *pMediaInfoW );
long ASDB_GetDeviceInfoByHostnameDateW(PASDB_SESSW phSESSW, wchar_t *pszHostname, wchar_t *pszMember, long lStartTime, long lEndTime, int *piDeviceCount, PASDB_TapeDrvRecW *pDeviceInfoW );
long ASDB_GetMediaInfoByHostnameDateW(PASDB_SESSW phSESSW, wchar_t *pszHostname, wchar_t *pszMember, long lStartTime, long lEndTime, PASDB_TapeDrvRecW pstTapeDrvW, int *piMediaCount, PASDB_TapeRecW *pMediaInfoW );
long ASDB_GetAgentsByHostnameStatusDateW(PASDB_SESSW phSESSW, int iStatus, wchar_t *pszHostname, wchar_t *pszMembername, long lStartTime, long lEndTime, int *piAgentCount, PASDB_AgentW *pstAgentListW );
long ASDB_GetSessionByJobIdHostnameW(PASDB_SESSW phSESSW, int iJobId, wchar_t *pszAgentName, int *piCount, PASDB_SessRec *pstSessRec );
long ASDB_GetExecutionHistoryByNameDateW(PASDB_SESSW phSESSW, wchar_t *pszAgentName, wchar_t *pszJobName, long lStartTime, long lEndTime, int *piCount, PASDB_JobExecutionHistory *pJobExecutionHistory);
long ASDB_GetExecutionHistoryByNameDateExW(PASDB_SESSW phSESSW, wchar_t *pszServerName, wchar_t *pszAgentName, wchar_t *pszJobName, long lStartTime, long lEndTime, int *piCount, PASDB_JobExecutionHistoryW *pJobExecutionHistoryW);

DWORD ASDB_UpdateASDBBackupLogW(wchar_t *pPrimaryServer, PASDB_BackupLogRec pBackUpLogRec);
short ASDB_GetDeviceListEXW(PASDB_SESSW phSESSW, PASDB_TapeDrvExRecW* dListW, DWORD * nCnt);
int ASDB_IsMemberW(wchar_t* primaryName, wchar_t* memberName, short lFlag);
short ASDB_GetNotMigratedCntW(PASDB_SESSW phSESSW, wchar_t *ServerName, long CommonJobID, long MasterJobID, int *Count);

PCOMPUTERNAME2W ASDB_GetServerNameListW(PASDB_SESSW phSESSW, UINT nType, UINT *Cnt);
long ASDB_GetTapeDataExByHostIDW(PASDB_SESSW phSESSW, PCOMPUTERNAME2W pComName, PTAPEDATAEX pTapeData);

short ASDB_GetDiskSpaceEXW(PASDB_SESSW phSESSW,wchar_t *pDrive, unsigned short sDriveBufSize, double *pdbTotal,double *pdbOthers,double *pdbDB,double *pdbFree);

int ASDB_GetServerListForOracleW(PASDB_SESSW phSESSW, unsigned long srchostid, PASDB_SeverHostW *pServerHostW, UINT *nCnt);

PTAPEDATAEX2W ASDB_GetTapeListEx2W(PASDB_SESSW phSESSW, UINT *Cnt);

////////////////////////
//sonle01 20080221 Begin
UINT WINAPI ASDB_CstGetSettingBinaryW(WCHAR *computerW, WCHAR *sectionW, 
                                     WCHAR *subSectionW, WCHAR *keyW, 
                                     PVOID defaultBuffer, PVOID buffer, 
                                     ULONG bufferSize);
UINT WINAPI ASDB_CstGetSettingIntW(PWCHAR computerW, PWCHAR sectionW, 
                                  PWCHAR subSectionW, PWCHAR keyW, 
                                  UINT defaultValue);
ULONG WINAPI ASDB_CstGetSettingLongW(WCHAR *computerW, WCHAR *sectionW, 
                                    WCHAR *subSectionW, WCHAR *keyW, 
                                    ULONG defaultValue);
UINT WINAPI ASDB_CstGetSettingStringW(WCHAR *computerW, WCHAR *sectionW, 
                                     WCHAR *subSectionW, WCHAR *keyW,  
                                     WCHAR *defaultStringW, WCHAR *bufferW, 
                                     ULONG bufferSize);
UINT WINAPI ASDB_CstGetGlobalSettingStringW(WCHAR *computerW, WCHAR *sectionW, 
                                     WCHAR *subSectionW, WCHAR *keyW,  
                                     WCHAR *defaultStringW, WCHAR *bufferW, 
                                     ULONG bufferSize);
UINT WINAPI ASDB_CstGetUserBinaryW(WCHAR *computerW, WCHAR *sectionW, 
                                     WCHAR *subSectionW, WCHAR *keyW, 
                                     PVOID defaultBuffer, PVOID buffer, 
                                     ULONG bufferSize);
USHORT WINAPI ASDB_CstGetLogDirectoryW(WCHAR *computerW, WCHAR *pathW, USHORT limit);
UINT WINAPI ASDB_CstSetSettingStringW(WCHAR *computerW, WCHAR *sectionW, 
                                     WCHAR *subSectionW, WCHAR *keyW,
                                     WCHAR *stringW);
UINT WINAPI ASDB_CstSetSettingBinaryW(WCHAR *computerW, WCHAR *sectionW, 
                                     WCHAR *subSectionW, WCHAR *keyW, 
                                     PVOID buffer, ULONG bufferSize);
UINT WINAPI ASDB_CstSetSettingIntW(WCHAR *computerW, WCHAR *sectionW, 
                                  WCHAR *subSectionW, WCHAR *keyW,  
                                  UINT value);
USHORT WINAPI ASDB_CstGetDiscoveryTblStringW(WCHAR *nameW, USHORT limit);
UINT   WINAPI ASDB_CstDelSettingW(WCHAR *computerW, WCHAR *sectionW, 
                                 WCHAR *subSectionW, WCHAR *keyW);
ULONG  WINAPI ASDB_CstSetSettingLongW(WCHAR *computerW, WCHAR *sectionW, 
                                 WCHAR *subSectionW, WCHAR *keyW, 
                                ULONG value);
UINT   WINAPI ASDB_CstDelUserW(WCHAR *computerW, WCHAR *sectionW, 
                                 WCHAR *subSectionW, WCHAR *keyW);
UINT   WINAPI ASDB_CstGetUserIntW(WCHAR *computerW, WCHAR *sectionW, 
                                 WCHAR *subSectionW, WCHAR *keyW, 
                                UINT defaultValue);
UINT   WINAPI ASDB_CstGetUserStringW(WCHAR *computerW, WCHAR *sectionW, 
                                 WCHAR *subSectionW, WCHAR *keyW, 
                                WCHAR *defaultStringW, WCHAR *bufferW, 
                                ULONG bufferSize);
UINT   WINAPI ASDB_CstSetUserBinaryW(WCHAR *computerW, WCHAR *sectionW, 
                                 WCHAR *subSectionW, WCHAR *keyW, 
                                PVOID buffer, ULONG bufferSize);
UINT   WINAPI ASDB_CstSetUserIntW(WCHAR *computerW, WCHAR *sectionW, 
                                 WCHAR *subSectionW, WCHAR *keyW, 
                                UINT value);
UINT   WINAPI ASDB_CstSetUserStringW(WCHAR *computerW, WCHAR *sectionW, 
                                 WCHAR *subSectionW, WCHAR *keyW, 
                                 WCHAR *stringW);
USHORT WINAPI ASDB_CstGetDatabaseDirectoryW(WCHAR *computerW, WCHAR *pathW, USHORT limit);
USHORT WINAPI ASDB_CstGetHomeDirectoryW(WCHAR *computerW, WCHAR *pathW, USHORT limit);
USHORT WINAPI ASDB_CstGetProgramDirectoryW(WCHAR *computerW, WCHAR *pathW, USHORT limit);
USHORT WINAPI ASDB_CstGetRegistryDirectoryW(WCHAR *computerW, WCHAR *pathW, USHORT limit);
USHORT WINAPI ASDB_CstGetSystemDirectoryW(WCHAR *computerW, WCHAR *pathW, USHORT limit);
USHORT WINAPI ASDB_CstGetTempDirectoryW(WCHAR *computerW, WCHAR *pathW, USHORT limit);
USHORT WINAPI ASDB_CstGetUserDirectoryW(WCHAR *computerW, WCHAR *pathW, USHORT limit);
USHORT WINAPI ASDB_CstGetUserNameW(WCHAR *computerW, WCHAR *nameW, USHORT limit);
BOOL   WINAPI ASDB_CstGetUserAccountW(WCHAR *computerW, PCSI_ACCOUNTW accountW);
BOOL   WINAPI ASDB_CstSetUserAccountW(WCHAR *computerW, PCSI_ACCOUNTW accountW);
BOOL   WINAPI ASDB_CstGetSQLAccountW(WCHAR *computerW, PCSI_ACCOUNTW accountW);
BOOL   WINAPI ASDB_CstSetSQLAccountW(WCHAR *computerW, PCSI_ACCOUNTW accountW);
BOOL   WINAPI ASDB_CstGetRemoteSQLAccountW(WCHAR *computerW, PCSI_ACCOUNTW accountW);
BOOL   WINAPI ASDB_CstSetRemoteSQLAccountW(WCHAR *computerW, PCSI_ACCOUNTW accountW);

//sonle01 20080221 End
////////////////////////

// END OF UNICODE SUPPORT

// _R12.x_(DeDupe)	Code Merge 6/262008.
// _R12.x_(DeDupe)	4/8/2008.
long ASDB_SetSessionCompressedSize (PASDB_SESS phSESS, PASDB_SessCompressedParm pSessCompressParm, UINT compressedSize);
//sonle01 20090206 long ASDB_GetTapeCompressedSize (PASDB_SESS phSESS, PASDB_TapeCompressedParm pTapeCompressedParm, UINT* totalSizeMB, UINT* compressedSizeMB);
long ASDB_GetTapeCompressedSize (PASDB_SESS phSESS, PASDB_TapeCompressedParm pTapeCompressedParm, __int64* totalSizeMB, __int64* compressedSizeMB);
long ASDB_GetTapeflag_ext (PASDB_SESS phSESS, char* tapename, long randomId, long tapeseqnum, char* serialnum, long* tapeflag_ext);
long ASDB_SetTapeflag_ext (PASDB_SESS phSESS, char* tapename, long randomId, long tapeseqnum, char* serialnum, long  tapeflag_ext);
long ASDB_GetCompressSizeBySessionID(PASDB_SESS phSESS, long sessionID, unsigned long *sizeOnTapeKB, unsigned long *compressSizeKB);
long ASDB_GetJobRecExV2ByJobInfo(PASDB_SESS phSESS, PASDB_JobInfoParm pJobInfoParm, PASDB_JobExRecV2 pjDBRec);
long ASDB_GetCatFile(PTCHAR computer, 
					 CHAR    *szTapeName,
					 ULONG   ulRandomID,
					 ULONG   ulTapeSeqNumber,
					 ULONG   ulSessionNumber,
					 ULONG   ulSubSessionNumber,
					 CHAR	 *szSerialNumber,
					 CHAR	 *szTargetPath,
					 int	 flag);

long ASDB_SetSessionPropertyExt2(PASDB_SESS phSESS,long SessionID,PASDB_SessRecExt2 pSessRecExt2);
long ASDB_GetSessionPropertyExt2(PASDB_SESS phSESS,long SessionID,PASDB_SessRecExt2 pSessRecExt2);
long ASDB_SetSessScanByMediaAndSessNum(PASDB_SESS phSESS, unsigned short scanStatus, unsigned short scanMethod, char *TapeName, short randomID, short seqNum, char *pSerialNum, long sessionNum);

// UNICODE ...
long ASDB_SetSessionCompressedSizeW (PASDB_SESSW phSESSW, PASDB_SessCompressedParmW pSessCompressParmW, UINT compressedSize);
//sonle01 20090206 long ASDB_GetTapeCompressedSizeW (PASDB_SESSW phSESSW, PASDB_TapeCompressedParmW pTapeCompressedParmW, UINT* totalSizeMB, UINT* compressedSizeMB);
long ASDB_GetTapeCompressedSizeW (PASDB_SESSW phSESSW, PASDB_TapeCompressedParmW pTapeCompressedParmW, __int64* totalSizeMB, __int64* compressedSizeMB);
long ASDB_GetCompressSizeBySessionIDW(PASDB_SESSW phSESSW, long sessionID, unsigned long *sizeOnTapeKB, unsigned long *compressSizeKB);
long ASDB_SetTapeflag_extW (PASDB_SESSW phSESSW, wchar_t *tapename, long randomId, long tapeseqnum, wchar_t *serialnum, long  tapeflag_ext);
long ASDB_GetTapeflag_extW (PASDB_SESSW phSESSW, wchar_t* tapename, long randomId, long tapeseqnum, wchar_t* serialnum, long* tapeflag_ext);
long ASDB_GetJobRecExV2ByJobInfoW(PASDB_SESSW phSESSW, PASDB_JobInfoParm pJobInfoParm, PASDB_JobExRecV2W pjDBRecW);
long ASDB_GetCatFileW(wchar_t *computer, 
					 wchar_t *szTapeName,
					 ULONG   ulRandomID,
					 ULONG   ulTapeSeqNumber,
					 ULONG   ulSessionNumber,
					 ULONG   ulSubSessionNumber,
					 wchar_t *szSerialNumber,
					 wchar_t *szTargetPath,
					 int	 flag);
long ASDB_GetSessionPropertyExt2W(PASDB_SESSW phSESSW,long SessionID,PASDB_SessRecExt2W pSessRecExt2W);
long ASDB_SetSessionPropertyExt2W(PASDB_SESSW phSESSW,long SessionID,PASDB_SessRecExt2W pSessRecExt2W);
// Add by Gil
// 4/9/2008 11:39:05 AM
// Support for Rich Agent Information beyond backup
#ifdef RICH_AGENT_INFORMATION
short ASDB_RichAgentInformation_BeginTrans(PASDB_SESS);
short ASDB_RichAgentInformation_UpdateFragmentEvent(PASDB_SESS phSESS, PASDB_FragmentEvent FragmentEvent);
short ASDB_RichAgentInformation_UpdateNode(PASDB_SESS, PASDB_NodeInformation, unsigned long *);
short ASDB_RichAgentInformation_UpdateOS(PASDB_SESS, PASDB_OSInformation);
short ASDB_RichAgentInformation_UpdateMemory(PASDB_SESS phSESS, PASDB_MemoryInformation MemoryInformation, long nCount);
short ASDB_RichAgentInformation_UpdateCPU(PASDB_SESS phSESS, PASDB_CPUInformation CPUInformation, long nCount);
short ASDB_RichAgentInformation_UpdateNIC(PASDB_SESS phSESS, PASDB_NICInformation NICInformation, long nCount);

// add [8/6/2008 lijbi02]
short ASDB_RichAgentInformation_UpdateIP(PASDB_SESS, PASDB_IPInformation);

// add [8/7/2008 lijbi02]
short ASDB_RichAgentInformation_UpdateFibercard(PASDB_SESS phSESS, PASDB_FibercardInformation fibercardInformation, long nCount);

short ASDB_RichAgentInformation_UpdateDisk(PASDB_SESS phSESS, PASDB_DiskInformation DiskInformation, long nCount);
short ASDB_RichAgentInformation_UpdateVolume(PASDB_SESS phSESS, PASDB_VolumeInformation VolumeInformation, long nCount);
short ASDB_RichAgentInformation_UpdatePartition(PASDB_SESS phSESS, PASDB_PartitionInformation PartitionInformation, long nCount);
short ASDB_RichAgentInformation_UpdateLogicalDisk(PASDB_SESS phSESS, PASDB_LogicalDiskInformation LogicalDiskInformation, long nCouont);
short ASDB_RichAgentInformation_UpdateMap(PASDB_SESS phSESS, int nodeID, const wchar_t VolumeDeviceID[512], unsigned long * DiskNumberList, long nCount);

// modified [7/25/2008 lijbi02]
short ASDB_RichAgentInformation_UpdateAgentInfo(PASDB_SESS phSESS, PASDB_AgentInfo agentInfo, unsigned long* agentID);

// ----------------------------------------------------------------------------
// add [12/17/2008 lijbi02]
// PURPOSE: For storing/updating the component(product) license information to asdb
// ARGUS: <phSESS> session handle structure
//        <pCLI> component with license info pointer
//        <count> the count of ASDB_ComponentLicenseInfo will be dealed with
// RETURN: if this function is invoked success return 0 or 1.
// REMARK: this function will do the insert or update operation, if there existed
//         a the same component id record update the record, else insert a new one.
//         if count <= 0 or pCLI = NULL, return -1.
// ----------------------------------------------------------------------------
short ASDB_RichAgentInformation_UpdateComponentInfo(PASDB_SESS phSESS, PASDB_ComponentLicenseInfo pCLI, int count);

short ASDB_RichAgentInformation_Commit(PASDB_SESS);

// add [7/29/2008 lijbi02]
short ASDB_RichAgentInformation_Rollback(PASDB_SESS);
#endif

// _R12.v_(Dashboard)	7/2/2008.
long ASDB_SetJobExV(PASDB_SESS phSESS,  PASDB_JobExRecV pJobExRecV);
long ASDB_SetJobThroughput(PASDB_SESS phSESS, long jobid, long throughputMBPerMin);
long ASDB_GetJobRecExV2ByID(PASDB_SESS phSESS, long jobID, PASDB_JobExRecV2 pjDBRec);
long ASDB_GetExecutionHistoryByNameDateEx2(PASDB_SESS phSESS, char *pszServerName, char *pszAgentName, char *pszJobName, long lStartTime, long lEndTime, int *piCount, PASDB_JobExecutionHistory2 *pJobExecutionHistory2);
long ASDB_GetJobRecExVByID(PASDB_SESS phSESS, long jobID, PASDB_JobExRecV pjDBRec);
short ASDB_MergeCreateSessionExt2(PASDB_SESS phSESS, 
                                 long tapeid,
                                 long starttime,
                                 long endtime,
                                 long sessionType,
                                 long sessnum,
                                 short sessionMethod,
                                 long  sessionFlags,
                                 char *  rootDirectory, 
                                 char *  wsAddress,
                                 char *  description,
                                 char *  ownerName,
                                 char *  g_Path,
                                 PASDB_SessRecExt2 psDBRec);

// UNICODE ...
long ASDB_SetJobExVW(PASDB_SESSW phSESSW,  PASDB_JobExRecV pJobExRecV);
long ASDB_SetJobThroughputW(PASDB_SESSW phSESSW, long jobid, long throughputMBPerMin);
long ASDB_GetJobRecExV2ByIDW(PASDB_SESSW phSESSW, long jobID, PASDB_JobExRecV2W pjDBRecW);
long ASDB_GetExecutionHistoryByNameDateEx2W(PASDB_SESSW phSESSW, wchar_t *pszServerName, wchar_t *pszAgentName, wchar_t *pszJobName, long lStartTime, long lEndTime, int *piCount, PASDB_JobExecutionHistory2W *pJobExecutionHistory2W);
long ASDB_GetJobRecExVByIDW(PASDB_SESSW phSESSW, long jobID, PASDB_JobExRecV pjDBRec);
short ASDB_MergeCreateSessionExt2W(PASDB_SESS phSESS,
                                 long tapeid,
                                 long starttime,
                                 long endtime,
                                 long sessionType,
                                 long sessnum,
                                 short sessionMethod,
                                 long  sessionFlags,
                                 wchar_t *  rootDirectory,
                                 wchar_t *  wsAddress,
                                 char *  description,
                                 char *  ownerName,
                                 wchar_t *  g_Path,
                                 PASDB_SessRecExt2 psDBRec);


// _R12.v_(VMWARE)	7/2/2008.
long ASDB_SetVMGuidInfo (PASDB_SESS phSESS, PASDB_VMGuidInfo pVMGuidInfo);
long ASDB_GetVMGuidInfo (PASDB_SESS phSESS, PASDB_VMGuidInfoParm  pVMGuidInfoParm, UINT * nCnt, PASDB_VMGuidInfo* pVMGuidInfo);
long ASDB_DeleteVMGuidInfo(PASDB_SESS phSESS, PASDB_VMGuidInfoParm  pVMGuidInfoParm);
long ASDB_SetVMTopology (PASDB_SESS phSESS, PASDB_VMTopology  pVMTopology, long lFlag);
long ASDB_GetVMTopology (PASDB_SESS phSESS, PASDB_VMTopologyParm  pVMTopologyParm, PASDB_VMTopology  pVMTopology);
long ASDB_GetVMTopologyList (PASDB_SESS phSESS, PASDB_VMTopologyParm  pVMTopologyParm, PASDB_VMTopology  *pVMTopology, UINT * nCnt);
long ASDB_DeleteVMTopology (PASDB_SESS phSESS, PASDB_VMTopologyParm  pVMTopologyParm);
long ASDB_GetVMRecoveryInfoList (PASDB_SESS phSESS, PASDB_VMRecoveryInfoParam  pVMRecoveryInfoParm, PASDB_VMRecoveryInfo  *pVMRecoveryInfo, UINT * nCnt);
short ASDB_SetSessionMap2(PASDB_SESS phSESS, PASDB_SessionMap pFromSession, PASDB_SessionMap pToSession);
long ASDB_GetSessionListByTapeAndSessNum(PASDB_SESS phSESS, char *TapeName, short randomID, short tapeSeq, char *pSerialNum, long lSessionNum, long lFlag, PASDB_SessRecExt2 *pSessRecExt2, UINT * nCnt);
short ASDB_SmartDeleteStaging2(PASDB_SESS phSESS, PASDB_Staging pStaging);
long ASDB_GetSessionByMediaAndSessNum2 (PASDB_SESS phSESS, char *TapeName, short randomID, short seqNum, char *pSerialNum, long sessionNum, long subsessionNum, PASDB_SessRec pSessRec, ULONG uFlag);
long ASDB_GetSessionByJobIdHostname2(PASDB_SESS phSESS, int iJobId, char *pszAgentName, int *piCount, PASDB_SessRecExt2 *pstSessRec2 );
short ASDB_GetSessionAndTapeRecBySesID2(PASDB_SESS phSESS, long SessionID, PASDB_SessRecExt2 sRec, PASDB_TapeRec2 tRec);
long ASDB_GetCatalogFileNameExSubNumWithSerial(PTCHAR computer, 
							 CHAR    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 ULONG	 ulSubSessionNum,
							 CHAR    *szSerialNo,
							 BOOL bCreateFolder,
							 ULONG   ulFlags,
							 char *tszCatalog, 
							 UINT nLength);
long ASDB_DeleteCatalogFileSubNumWithSerial(PTCHAR computer, 
							 CHAR    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 ULONG	 ulSubSessionNum,
							 CHAR    *szSerialNo);
long ASDB_CopyCatalogFileExSubNumWithSerial(PTCHAR computer, 
							 CHAR    *szTapeNameDest,
							 ULONG   ulTapeNumberDest,
							 ULONG   ulRandomIDDest,
							 ULONG   ulSessionNumberDest,
							 ULONG   ulSubSessionNumberDest,
							 CHAR    *szSerialNoDest,
							 CHAR    *szTapeNameSrc,
							 ULONG   ulTapeNumberSrc,
							 ULONG   ulRandomIDSrc,
							 ULONG   ulSessionNumberSrc,
							 ULONG   ulSubSessionNumberSrc,
							 CHAR    *szSerialNoSrc,
							 ULONG   ulFlags);


// UNICODE
long ASDB_GetVMRecoveryInfoListW (PASDB_SESSW phSESSW, PASDB_VMRecoveryInfoParamW  pVMRecoveryInfoParmW, PASDB_VMRecoveryInfoW  *pVMRecoveryInfoW, UINT * nCnt);
long ASDB_GetSessionListByTapeAndSessNumW(PASDB_SESSW phSESSW, wchar_t *TapeName, short randomID, short tapeSeq, wchar_t *pSerialNum, long lSessionNum, long lFlag, PASDB_SessRecExt2W *pSessRecExt2W, UINT * nCnt);
short ASDB_SetSessionMap2W(PASDB_SESSW phSESSW, PASDB_SessionMapW pFromSessionW, PASDB_SessionMapW pToSessionW);

long ASDB_GetCatalogFileNameExSubNumWithSerialW(wchar_t *computer, 
										  wchar_t *szTapeName, 
										  ULONG ulTapeNumber, 
										  ULONG ulRandomID, 
										  ULONG ulSessionNumber, 
										  ULONG	ulSubSessionNum,
										  wchar_t *szSerialNo, 
										  BOOL bCreateFolder, 
										  ULONG ulFlags, 
										  wchar_t *tszCatalog, 
										  UINT nLength);
long ASDB_DeleteCatalogFileSubNumWithSerialW(wchar_t *computer, 
											 wchar_t *szTapeName, 
											 ULONG ulTapeNumber, 
											 ULONG ulRandomID, 
											 ULONG ulSessionNumber, 
											 ULONG ulSubSessionNum, 
											 wchar_t *szSerialNo);
long ASDB_CopyCatalogFileExSubNumWithSerialW(wchar_t *computer, 
							 wchar_t    *szTapeNameDest,
							 ULONG   ulTapeNumberDest,
							 ULONG   ulRandomIDDest,
							 ULONG   ulSessionNumberDest,
							 ULONG   ulSubSessionNumberDest,
							 wchar_t    *szSerialNoDest,
							 wchar_t    *szTapeNameSrc,
							 ULONG   ulTapeNumberSrc,
							 ULONG   ulRandomIDSrc,
							 ULONG   ulSessionNumberSrc,
							 ULONG   ulSubSessionNumberSrc,
							 wchar_t    *szSerialNoSrc,
							 ULONG   ulFlags);

long ASDB_GetSessionByMediaAndSessNum2W (PASDB_SESSW phSESSW, wchar_t *TapeName, short randomID, short seqNum, wchar_t *pSerialNum, long sessionNum, long subsessionNum, PASDB_SessRec pSessRec, ULONG uFlag);
short ASDB_GetSessionAndTapeRecBySesID2W(PASDB_SESSW phSESSW, long SessionID, PASDB_SessRecExt2W psRecW, PASDB_TapeRec2W ptRec2W);
long ASDB_GetSessionByJobIdHostname2W(PASDB_SESSW phSESSW, int iJobId, wchar_t *pszAgentName, int *piCount, PASDB_SessRecExt2W *pstSessRec2W );

#ifdef R12_5_MANAGE_PASSWORD
// ******************
// Password Management
// (PARPR04) 09 July 2007
// ******************

long ASDB_SetSessPassword(PASDB_SESS phSESS, PASDB_SessionGUID pSessGUID, char *Password, int LengthPwd);
long ASDB_GetSessPassword(PASDB_SESS phSESS, PASDB_SessionGUIDParm pSessGUID, char *Password, int *BufferSize);

long ASDB_SetPasswordExpiration(PASDB_SESS phSESS, 
								ULONG ulJobNum, 
								ULONG ulCommonJobID, 
								ULONG ulExpirePeriod, 
								ULONG ulExpireDate);
long ASDB_GetPasswordExpiration(PASDB_SESS phSESS,
								ULONG ulJobNum,
								ULONG ulCommonJobID,
								ULONG *pulExpireDate);

long ASDB_DeletePasswordReminderInfo(PASDB_SESS phSESS,
								ULONG ulJobNum,
								ULONG ulCommonJobID);
long ASDB_GetManagedPasswordSessionsCount(PASDB_SESS phSESS, int* count);
long ASDB_GetSessionPasswordListByTime(PASDB_SESS phSESS,
									   PASDB_DumpPwdParm pDumpPwdParm,
									   BOOL bFirst,
									   UINT* pCnt,
									   PASDB_DumpPwdRec* pDumpPwdrec);
long ASDB_ImportSessionPasswordList(PASDB_SESS phSESS,
									DWORD* nCnt,
									PASDB_DumpPwdRec* pDumpPwdRec, ADMKHANDLE ADMK);		

long ASDB_GetEncryptedADMK(PASDB_SESS phSESS, char *ADMK, UINT *len);
long ASDB_IsEncryptionReady(PASDB_SESS phSESS);
long ASDB_GetADMKEncryptedPassword(PASDB_SESS phSESS, PASDB_SessionGUIDParm  pSessGUID, char *EncryptedPassword,int *BufferSize);
long ASDB_SetPassphrase(PASDB_SESS phSESS, char *Passphrase);
long ASDB_GetADMKProtectionMethod(PASDB_SESS phSESS, int* pProtectionMethod);	// pProtectionMethos is ADMK_PROTECTION_METHOD type

// R12.v (6/23/2008)...
long ASDB_SetUserDBInfo(PASDB_SESS phSESS, PASDB_UserDB pUserDB, int iFlag);
long ASDB_GetUserDBInfo(PASDB_SESS phSESS, PASDB_UserDBParm pUserDBParam, PASDB_UserDB pUserDB);
long ASDB_DeleteUserDBInfo(PASDB_SESS phSESS, PASDB_UserDBParm pUserDBParam);
long ASDB_EnumUserDBInfo(PASDB_SESS phSESS, PASDB_UserDBParm pUserDBParam, UINT * nCnt, PASDB_UserDB *pUserDBList);
long ASDB_SetPrimaryTopologyId (PASDB_SESS phSESS, int iFlag);
long ASDB_IsDedupeGFSJOB(PASDB_SESS phSESS, long commonjobID, long JobNo, int* ret); // 11/06/2008 

// R12.v (8/29/2008)
long ASDB_GetSmartCatalogFileNameExWithSerial(PTCHAR computer, 
							 CHAR    *szTapeName,
							 ULONG   ulTapeNumber,
							 ULONG   ulRandomID,
							 ULONG   ulSessionNumber,
							 CHAR    *szSerialNo,
							 BOOL bCreateFolder,
							 ULONG   ulFlags,
							 char *tszCatalog, 
							 UINT nLength);

#define ASDB_IsEncryptionReadyW(phSESSW)	ASDB_IsEncryptionReady((PASDB_SESS)phSESSW) 
#define ASDB_GetADMKProtectionMethodW(phSESSW, pProtectionMethod)  ASDB_GetADMKProtectionMethod((PASDB_SESS)phSESSW, pProtectionMethod)

long ASDB_SetUserDBInfoW(PASDB_SESSW phSESSW, PASDB_UserDBW pUserDB, int iFlag);
long ASDB_GetUserDBInfoW(PASDB_SESSW phSESSW, PASDB_UserDBParmW pUserDBParam, PASDB_UserDBW pUserDB);
long ASDB_DeleteUserDBInfoW(PASDB_SESSW phSESSW, PASDB_UserDBParmW pUserDBParam);
long ASDB_EnumUserDBInfoW(PASDB_SESSW phSESSW, PASDB_UserDBParmW pUserDBParam, UINT * nCnt, PASDB_UserDBW *pUserDBList);

long ASDB_SetSessPasswordW(PASDB_SESSW phSESSW, PASDB_SessionGUIDW pSessGUID, wchar_t *Password, int LengthPwd);
long ASDB_GetSessPasswordW(PASDB_SESSW phSESSW, PASDB_SessionGUIDParmW pSessGUID, wchar_t *Password, int *BufferSize);
long ASDB_SetPassphraseW(PASDB_SESSW phSESSW, wchar_t *Passphrase);

#endif	//R12_5_MANAGE_PASSWORD

long ASDB_GetHostInfoListW(PASDB_SESS phSESS, BOOL bFirst, PASDB_HostInfo * pHostInfoList, UINT * pCnt); // It will be removed (it has been changed as "ASDB_GetHostInfoListForUpgradeW")
long ASDB_GetHostInfoListForUpgradeW(PASDB_SESS phSESS, BOOL bFirst, PASDB_HostInfo * pHostInfoList, UINT * pCnt);
long ASDB_DeleteSessionByID(PASDB_SESS phSESS, long SessionID);
long ASDB_GetAgentHostnameListW(PASDB_SESSW phSESS, PASDB_AGENTHOST_NAMEW *pNameList, UINT *nCnt);

//20081124_New_Dump_DB
long ASDB_SetADMK(PASDB_SESS phSESS, char *primaryServer, char *admk, long len_admk);
long ASDB_EnumTopologyAll(PASDB_SESS phSESS, PASDB_TOPOLOGY_INFOEX *pTopologyLst, long *nCnt);

//20081219 sonle01
long ASDB_GetVMNodeListForInstallation(PASDB_SESS phSESS, BOOL bFirst, PASDB_VMNodeInfo * pVMNodeList, UINT * pCnt);

//20081219 sonle01
long ASDB_SetHostInfoVM(PASDB_SESS phSESS, PASDB_ASHostInfoVM NodeInfoVM);

//R12.6
//#ifdef ASDB_R12_6_API
short ASDB_GetFileQFADataListFirstW(PASDB_SESSW phSESSW, 
									long tapeID, // It is not randomID but unique tapeid 
                                    long sesnum,
                                    unsigned long *cat_find_handle);

short ASDB_GetFileQFADataListNextW(PASDB_SESSW phSESSW, 
								   unsigned long *cat_find_handle,
								   wchar_t *  path,
								   wchar_t *  file,
								   ASDB_QFAData *pData);

short ASDB_GetFileQFADataListCloseW(PASDB_SESSW phSESSW, 
								   unsigned long *cat_find_handle);

short ASDB_GetFileQFADataByTapeIDSesNumW(PASDB_SESSW phSESSW,	// IN
                        			long tapeID,				// IN	// It is not randomID but unique tapeid 
                                    long sesnum,				// IN
									long lFlag,					// IN (Future use) 
									wchar_t *  path,			// IN
									wchar_t *  file,			// IN
                                    ASDB_QFAData *pData);		// OUT


short ASDB_GetTapeSetInfo(PASDB_SESS phSESS, 
						  CHAR* pMediaPool,
						  ASDB_TsiEnumTapeInfo* pTapeInfoList, 
						  UINT uCnt, 
						  ASDB_TapeSetInfo* pTapeSetinfoList);

short ASDB_GetLatestSessionRootDirW(PASDB_SESSW phSESSW, 
									wchar_t * pStr, 
									long sessType, 
									wchar_t* pRootDir, 
									UINT len);
short ASDB_GetLatestSessionRootDirW2(PASDB_SESSW phSESSW, 
									 wchar_t * pDomain, 
									 wchar_t * pStr, 
									 long sessType, 
									 wchar_t* pRootDir, 
									 UINT len);
short ASDB_SetSessionDomainW(PASDB_SESS phSESS, PASDB_TapeCompressedParmW tapeInfo, long sessNum, const wchar_t* pwszDomain); 

#ifdef R14_E14_DB //R14_E14 (2/13/2009)
long ASDB_SetEXADServerInfo (PASDB_SESS phSESS, PASDB_EXADServerInfo pEXADServerInfo);
long ASDB_GetEXADServerInfo (PASDB_SESS phSESS, PASDB_EXADServerInfoParm  pEXADServerInfoParm, UINT * nCnt, PASDB_EXADServerInfo *pEXADServerInfo);
long ASDB_DeleteEXADServerInfo (PASDB_SESS phSESS, PASDB_EXADServerInfoParm  pEXADServerInfoParm);
long ASDB_SetEXCHANGEInfo (PASDB_SESS phSESS, PASDB_EXCHANGEInfo pEXCHANGEInfo);
long ASDB_GetEXCHANGEInfo (PASDB_SESS phSESS, PASDB_EXCHANGEInfoParm  pEXCHANGEInfoParm, UINT * nCnt, PASDB_EXCHANGEInfo *pEXCHANGEInfo);
long ASDB_DeleteEXCHANGEInfo (PASDB_SESS phSESS, PASDB_EXCHANGEInfoParm  pEXCHANGEInfoParm);
long ASDB_SetMailBoxInfo (PASDB_SESS phSESS, PASDB_MailBoxInfo pMailBoxInfo);
long ASDB_GetMailBoxInfo (PASDB_SESS phSESS, PASDB_MailBoxInfoParm  pMailBoxInfoParm, UINT * nCnt, PASDB_MailBoxInfo *pMailBoxInfo);
long ASDB_GetExchangeInfoListForUpgradeW (PASDB_SESS phSESS, BOOL bFirst, PASDB_HostInfo *pEXCHANGEInfo, UINT * pCnt, long lFlag);
long ASDB_GetEXADServerList(PASDB_SESS phSESS, BOOL bFirst, PASDB_EXADServerInfo *pEXADServerInfo, UINT * pCnt);
#endif // //R14_E14 (2/17/2009)


//#endif

#ifdef __cplusplus
  }
#endif /* __cplusplus */


#endif /* __ASDBAPI_H */
