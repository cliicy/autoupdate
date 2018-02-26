#pragma once

#include <map>
#include <string>
#include <vector>
#include "afdefine.h"
#include "drcore.h"
#include "afstorinterface.h"
#include "IPasswordManagement.h"
#include "brandname.h"
#include <atlstr.h>
#include "DbgLog.h"
#include "ICryptoWrapperInterface.h"
#include "brandname.h"
#include "AFCommFunc.h"
#include "..\FlashCore\AFCoreFunction\VMConfSettingAPI.h"

using namespace std;

#if defined( UNICODE ) || defined( _UNICODE )
#define AFCryptInit AFCryptInitA
#else // defined( UNICODE ) || defined( _UNICODE )
#define AFCryptInit AFCryptInitW
#endif // defined( UNICODE ) || defined( _UNICODE )

// --------------------------------- helper function set 1  ---------------------------------
// Function AFCryptInitA/AFCryptInitW
// Initialize cryptography.
// Input parameter:
//     pszPassword:[in] password to encrypt/decrypt data.
//                 If it is NULL or empty string, default password will be used
// Return code:
//     NULL : failed to initialize cryptography
//     !NULL: handle to crypty object that will be used by AFEncrypt/AFDecrypt
void* AFCryptInitA(char* pszPassword);
void* AFCryptInitW(wchar_t* pszPassword);

// Function AFEncrypt
// Encrypt data from plaintext to encrypted text.
// Input Parameters:
//     hCrypt: [in] crypty object handle that is created by AFCryptInitA/AFCryptInitW
//     pvData: [in,out] data is crypted in same buffer
// pnDataSize: [in,out] in: plaintext data size, out: encrypted data size
//   nBufSize: [in] buffer size of pvData
//     bFinal: [in] this is the last calling to encrypt data
// Return Code:
//        >=0: successful
//         <0: error code
int AFEncrypt(void* hCrypt, void *pvData, DWORD* pnDataSize, DWORD nBufSize, bool bFinal);

// Function AFDecrypt
// Decrypt data from encrypted text to plaintext.
// Input Parameters:
//     hCrypt: [in] crypty object handle that is created by AFCryptInitA/AFCryptInitW
//     pvData: [in,out] data is decrypted to same buffer
// pnDataSize: [in,out] in: encrypted data size, out: plaintext data size
//     bFinal: [in] this is the last calling to encrypt data
// Return Code:
//        >=0: successful
//         <0: error code
int AFDecrypt(void* hCrypt, void *pvData, DWORD* pnDataSize, bool bFinal);

// Function AFCryptClose
// All data is encrypted/decrypted. Crypty object can be destoried now.
// Input Parameters:
//     hCrypt: [in] crypty object handle that is created by AFCryptInitA/AFCryptInitW
// Return Code:
//        >=0: successful
//         <0: error code
int AFCryptClose(void* hCrypt);

// --------------------------------- helper function set 2  ---------------------------------
// Function AFEncryptData
// Encrypt data with default password. Encrypted data will be started with an encrypt header.
// Input Parameters:
//     pvData: [in,out] data is encrypted to same buffer
//						This parameter is set to NULL to determine the number of bytes required for the returned data.
// pnDataSize: [in,out] in: plaintext data size, out: encryped data size include encrypt header.
//						If pvData is NULL, no error is returned, and the function stores the size of the encrypted data,
//						in bytes, in the DWORD value pointed to by pnDataSize. 
//						This lets an application unambiguously determine the correct buffer size.
//   nBufSize: [in] buffer size of pvData.
// Return Code:
//        >=0: successful
//         <0: error code
int AFEncryptData(void *pvData, DWORD* pnDataSize, DWORD nBufSize);

// Function AFDecryptData
// Decrypt data with default password. Encrypted data must be started with an encrypt header.
// Input Parameters:
//     pvData: [in,out] data is decrypted to same buffer
// pnDataSize: [in,out] in: encrypted data size, out: plaintext data size
// Return Code:
//        >=0: successful
//         <0: error code
int AFDecryptData(void *pvData, DWORD* pnDataSize);

// CAUTION!
// Encrypted data by set 1 must be decrypted with set 1 funciton.
// Meanwhile, encrypted data set 2 function, that data must be decrypted by set 2 function.
// The major difference is in set 2, an interal data block is added begin of encryted data.

/*
*Purpose: Encrypt string.
*@pszStr: [INPUT] input string which will be encrypted.
*@pszBuf: [OUTPUT] output buffer for encrypted string.
*@pBufLen: [INPUT, OUTPUT] For input, it is the length of output buffer in character.
                           For output, it is the length of the encrypted string in character.
*Note:
     If pszBuf is null or pBufLen is less than the length of encrypted string, pBufLen contains the length
     of encrypted string, and false will be returned. And GetLastError will return ERROR_NOT_ENOUGH_MEMORY
*/
BOOL WINAPI AFEncryptToString(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen);

/*
*Purpose: Decrypt string.
*@pszStr: [INPUT] string which is encrpted.
*@pszBuf: [OUTPUT] string which is decrypted.
*@pBufLen: [INPUT, OUTPUT] For input, it is the length of the buffer in character.
                           For output, it is the length of decrypted string in character.
*Note:
     If pszBuf is null or pBufLen is less than the length of decrypted string, pBufLen contains the length
     of decrypted string, and false will be returned. And GetLastError will return ERROR_NOT_ENOUGH_MEMORY
*/
BOOL WINAPI AFDecryptFromString(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen);

//Function to get volume guid path in backup chain.
DWORD WINAPI AFGetLocalDestVolPath(const std::wstring & strCur, std::vector<std::wstring> &vGuid);

//file attribute for signature file
#define SIG_FILE_ATTRIBUTE (FILE_ATTRIBUTE_HIDDEN | FILE_ATTRIBUTE_SYSTEM)

class IARCFlashDev
{
public:

   virtual void Release() = 0;

   /*
   *Purpose: Write signature file into destination.
   *@strCur: [INPUT] Current destination. It cannot be NULL.
   *@strOld: [INPUT] The last destination. It can be NULL.
   *Return: If function succeeds, it will return zero. If function fails, it will return non-zero code.
   *Remarks: If strOld is NULL, only create signature file into destination specified by strCur.
             If strOld is not NULL, one signature file is created into destination specified by strCur,
             and signature file(s) in destination specified by strOld and the next destination pointed 
             by strOld will be updated.
   */
   virtual DWORD InitBackupDev(const NET_CONN_INFO *pCurDest, const NET_CONN_INFO *pOldDest = NULL, BOOL bCopyDest = FALSE) = 0;

   /*
   *Purpose: Write signature file into destination.
   *@strCur: [INPUT] Current destination. It cannot be NULL.
   *@strOld: [INPUT] The last destination. It can be NULL.
   *Return: If function succeeds, it will return zero. If function fails, it will return non-zero code.
   *Remarks: If strOld is NULL, only create signature file into destination specified by strCur.
             If strOld is not NULL, one signature file is created into destination specified by strCur,
             and signature file(s) in destination specified by strOld and the next destination pointed 
             by strOld will be updated.
   */
   virtual DWORD InitBackupDevForRPS(const NET_CONN_INFO *pCurDest, const NET_CONN_INFO *pOldDest = NULL, BOOL bCopyDest=FALSE) = 0;

   virtual BOOL IsBackupDev(const std::wstring &strDir) = 0;

   virtual BOOL IsBackupDest(const NET_CONN_INFO &info) = 0;
   /*
   *Purpose: Retrieve all backup destinations given one destination folder.
   *@strDir:[Input] one destination folder which contains the signature file.
   *@vDest:[output] contains for backup destinations include the one specified by strDir.
   *Note: This function will track all the destination folders by 'double-linked list'.
   */
   virtual DWORD GetAllBackupDestinations(const std::wstring &strDir, std::vector<std::wstring> &vDest) = 0;

   /*
   *Purpose: Retrieve all backup destinations given one destination folder.
   *@strDir:[Input] one destination folder which contains the signature file.
   *@vDest: [Output] the backup destinations.
   *Note: This function doesn't check whether the destination contains full backup.
   */
   virtual DWORD GetAllBackupDestinationsEx(const std::wstring &strDir, std::vector<std::wstring> &vDest) = 0;

   /*
   *Purpose: Retrieve all backup destinations given one destination folder.
   *@strDir:[Input] one destination folder which contains the signature file.
   *@vDest:[Output] the backup destinations.
   *@strErrDest:[Output] the unaccessed backup destination.
   *Note: This function will check whether the available backup chain contains at least one full backup.
   */
   virtual DWORD CheckAvailableBackupDestinations(const std::wstring &strDir, std::vector<std::wstring> &vDest, std::wstring &strErrDest) = 0;

   virtual DWORD UpdateExistBackupDestination(const NET_CONN_INFO &info) = 0;

   virtual DWORD TrackAllBackupDestinations(const NET_CONN_INFO &cur, const std::vector<NET_CONN_INFO> &vDestOri, std::vector<NET_CONN_INFO> &vDest, NET_CONN_INFO &failDest, DWORD &dwType) = 0;

   virtual DWORD TryToAccess(const NET_CONN_INFO &info) = 0;

   virtual DWORD FinishAccess(const NET_CONN_INFO &info) = 0;

   virtual DWORD GetNetConnFromDest(const wstring &strDest, NET_CONN_INFO &info, BOOL bPrev = TRUE) = 0;

   virtual DWORD GetNetConnFromDestWrap(WCHAR* strDest, NET_CONN_INFO &info, BOOL bPrev = TRUE) = 0;

   //if bPrev == true, dest2 is the previous backup dest for dest1.
   //if bPrev == false, dest2 is the next backup dest for dest1.
   virtual DWORD UpdateBackupDest(const NET_CONN_INFO &dest1, const NET_CONN_INFO &dest2, BOOL bPrev = FALSE) = 0;

   virtual BOOL CheckNextPrevDestExist(const wstring &strDir, BOOL bPrev = TRUE) = 0;

   virtual DWORD GetD2DNodeInfo( const wstring &strDir, D2D_NODE_INFO* pD2DNodeInfo ) = 0;

   virtual DWORD GetD2DNodeInfoEx( const wstring &strDir, D2D_NODE_INFO_EX* pD2DNodeInfo ) = 0; //extend for Oolong

   //For network path, just return itself.
   virtual DWORD GetVolumeFullPath4Path(const wstring &strVolPath, wstring &strPath) = 0;
};

DWORD WINAPI CreateIARCFlashDev(IARCFlashDev **ppIARCFlashDev);

//Works as IARCFlashDev::InitBackupDev.
DWORD WINAPI AFUpdateBackupDevSig(const NET_CONN_INFO *pCurDest, const NET_CONN_INFO *pOldDest = NULL, BOOL bCopyDest=FALSE);

DWORD WINAPI AFCheckDestAccess(const NET_CONN_INFO *pDest, BOOL bCheckWritable = FALSE);

DWORD WINAPI AFFinishAccessDest(const NET_CONN_INFO *pDest);

DWORD WINAPI AFGetSharedResource(const NET_CONN_INFO *pDest, std::vector<std::wstring> &vShare);

/*
*Purpose: Retrieve all backup destinations given current backup destination.
*@pszCurDest: [INPUT] current backup destination.
*@ppszDestList: [OUTPUT] backup destinations.
*@lpBufLen: [OUTPUT, OPTIONAL]buffer length.
*Return: If function succeeds, it will return zero. If function fails, it will return non-zero error code.
*Note:
     Please remember to free buffer ppszDestList after use. 
     HeapFree(GetProcessHeap(), 0, *ppszDestList).
*/
DWORD WINAPI AFGetAllBackupDestinations(const wchar_t *pszCurDest, wchar_t **ppszDestList, DWORD *lpBufLen);

/*
*Purpose: Initilaize backup destination.
*@pDir: Path for backup destination, local or remote is ok.
*@pDomain: domain name if needed.
*@pUser: user name if needed.
*@pPwd: password if needed.
*Return: 0 for success. Otherwise, system error code will be returned.
*Remarks: 
   For remote shared folder, this function will try to create remote connection.
   For local folder, this funciton will check whether it can be accessed.
*/
DWORD WINAPI AFInitBackupDestForBackend(const wchar_t *pDir, const wchar_t *pDomain=NULL, const wchar_t *pUser=NULL, const wchar_t *pPwd=NULL);

DWORD WINAPI AFInitBackupDestForBackendEx(const NET_CONN_INFO *pInfo, std::wstring &strErrDest, DWORD dwBakType = 0, DWORD dwJobType = 0);

DWORD WINAPI AFInitBackupDestForReplication(const wchar_t *pDir, const wchar_t *pDomain, const wchar_t *pUser, const wchar_t *pPwd);

DWORD WINAPI AFUpdateNodeInfo(const NET_CONN_INFO *pInfo, const D2D_NODE_INFO_EX& stNodeInfo); //<huvfe01>2012-12-18 update node info

DWORD WINAPI AFUpdateLocalNodeInfo(const NET_CONN_INFO *pInfo); //<huvfe01>2012-12-18 update node info

DWORD WINAPI AFDoneInitBackupDestForBackend(const wchar_t *pDir);

BOOL WINAPI IsCompressLevelChanged(const NET_CONN_INFO *pInfo, int iLevel);

BOOL WINAPI IsVhdFormatChanged(const NET_CONN_INFO *pInfo, int iLevel);

#define	ENCINFO_PWD_CHANGE		1
#define	ENCINFO_ALGO_CHANGE		2
BOOL WINAPI IsEncInfoChanged(const NET_CONN_INFO *pInfo, PD2D_ENCRYPTION_INFO pCurEncInfo, bool bChkDataStoreEnc = false);
BOOL WINAPI IsDataSliceInfoChanged(const NET_CONN_INFO *pInfo, BOOL bEnableDataSlice, DWORD dwSliceSizeInMB);

typedef std::map<std::wstring, std::wstring> VOL_SNAPSHOT_MAP;

DWORD WINAPI AFGetFileSize(const wchar_t *pMatch, long long *pSize, VOL_SNAPSHOT_MAP *pMap = NULL);

DWORD WINAPI AFGetFileLogicalSize(const wchar_t *pMatch, long long *pSize, VOL_SNAPSHOT_MAP *pMap = NULL);

DWORD WINAPI AFGetLatestJobId(DWORD *pId, BOOL bNext=TRUE);

DWORD WINAPI AFPreAllocateJobIds(DWORD *pStartId, DWORD dwCount = 1);

DWORD WINAPI AFGetSubSessionNo(const wchar_t *pDest, DWORD dwSessNo, const wchar_t *pFullPath, DWORD *pPos);


//track all backup destinations in DR.
typedef BOOL (WINAPI *_PFNTRACKDEST)( const wstring& strRemoteSharePath, wstring& strDomainName, wstring& userName, wstring& password );

DWORD WINAPI DRGetAllBackupDestinations( const NET_CONN_INFO&   curDestination,
                       vector<NET_CONN_INFO>& allDestinations,
                          NET_CONN_INFO&        unaccessiblePath, 
                          _PFNTRACKDEST     pFuncCallBack );


DWORD WINAPI AFGetVhdPath(const wchar_t *pCurVhd, const wchar_t *pFindVhd, wchar_t *pResult, DWORD dwLen);

DWORD WINAPI AFConnectRemoteSource(const wchar_t *pUsr, const wchar_t *pPwd, const wchar_t *pDir);

DWORD WINAPI AFDisconnectRemoteSource(const wchar_t *pDir, BOOL bForce = FALSE, const wchar_t *pUsr = NULL);

DWORD WINAPI AFGetInstallPath(wchar_t *pInstallPath, DWORD dwLen);

/*
Nov. 4, 2010, wanji10
	AFUpdateHistoryBackup
		dwDays: keep backup history XML of last "dwDays"
		bUseRetentionCount: default behavior is to save same number as retention count in r16
			TRUE - keep same number of backup history XML as backup retention count, dwDays will have this count
			FALSE- keep last "dwDays" backup history XML
*/
/* 
	wansh11: In r5.0, the default backup histories to reserve is 30 days, this is configuration by registry key
	"HKEY_LOCAL_MACHINE\SOFTWARE\CA\ARCserve Unified Data Protection\Engine\BackupHistoryToReserveInDay"
*/
DWORD WINAPI AFUpdateHistoryBackup(const PBackupInfoXml pBakInfoXml, const wchar_t *pUniqueID = NULL); //<sonmi01>2010-9-19 code change after vsphere merge - AFUpdateHistoryBackup

/*
* Function used to remote backup history.
*/
DWORD WINAPI AFReserveHistoryBackup( const wchar_t* pUniqueID = NULL );
DWORD WINAPI AFGetLastHistoryBackupNo(DWORD &dwNo, const WCHAR * pszVMUUID = NULL); //<sonmi01>2011-3-16 edge vminfo.xml not synced #20121594
DWORD WINAPI AFUpdateHistoryVMInfo(const PVMInfoXml pVMInfoXml, ULONG ulFileNo, const wchar_t *pUniqueID = NULL); //<sonmi01>2010-11-18 vminfo xml for edge DB info
DWORD WINAPI AFWriteVMInfoXml(PVMInfoXml pVMfoXml, LPCWSTR szXmlFileName);
DWORD WINAPI AFReadVMInfoXml(LPCWSTR pszXmlFileName, VMInfoXml & vmInfo);

BOOL WINAPI AFCheckNeedToPurgeLog(const FILETIME *pTime, DWORD dwDays, BOOL bSql = TRUE);

//<sonmi01>2010-10-29 purge log schedule per machine
BOOL WINAPI AFCheckNeedToPurgeLogPerMachine(const FILETIME *pTime, DWORD dwDays, BOOL bSql = TRUE, LPCTSTR pszMachineID = NULL);

DWORD WINAPI AFCheckIsSysBootVolume(const wchar_t *pDir);

DWORD WINAPI AFCheckPathForBackup(const std::wstring &strDir, std::wstring &strHostName, const wchar_t *uuid = NULL, const wchar_t* nodeID=NULL, BOOL bCreateFolder=TRUE);

DWORD WINAPI AFCreateIdFileForVM(CONST wchar_t * pDestRootPath, CONST wchar_t * pVMUUID); //<sonmi01>2012-2-8 ###???

DWORD WINAPI AFInitCopyDest(const NET_CONN_INFO *pDir, const wstring &strHost, wstring &strDest);

struct IAFLockDest
{
   virtual BOOL LockFolder(BOOL bWrite = TRUE, DWORD dwWait = 0) = 0;
   virtual void UnLockFolder() = 0;
   virtual void Release() = 0;
};

BOOL WINAPI CreateIAFLockDest(IAFLockDest **ppLockDest, const NET_CONN_INFO *pInfo);

DWORD WINAPI AFSetAdminAccount(const std::wstring &strUser, const std::wstring &strPwd);

DWORD WINAPI AFGetAdminAccount(std::wstring &strUser, wstring &strPwd);

DWORD WINAPI AFCheckAdminAccount(const std::wstring &strUser, const std::wstring &strPwd);

DWORD WINAPI AFGetAdminAccountToken(HANDLE &hTok);

void WINAPI AFReleaseHandle(HANDLE handle);

DWORD WINAPI AFGetSess(const NET_CONN_INFO &info, std::vector<std::wstring> &vSess);

DWORD WINAPI AFGetHashStr(const wchar_t *pstr, wchar_t *pHash, DWORD *pLen);

DWORD WINAPI AFCheckJobCrashed(DWORD *pdwIsCrashed, DWORD *pdwSessionNumber);

DWORD WINAPI AFSetJobCrashed(DWORD dwIsCrashed, DWORD dwSessionNumber);

//this function just check whether the path can be accessed or not. it doesn't create connection for remote destination.
BOOL WINAPI AFCheckDestAvailable(const NET_CONN_INFO *pInfo);

BOOL WINAPI AFCheckDestEmpty(const NET_CONN_INFO *pInfo);

DWORD WINAPI AFGetDestThreshold(unsigned long long * pThreshold);

DWORD WINAPI AFSetDestThreshold(unsigned long long ulThreshold);

BOOL WINAPI AFCheckDestThreshold(const wchar_t *pszDestPath, unsigned long long *pThreshold);

//0 for ok, non-zero for error
INT WINAPI AFCheckDestThresholdForMerge(const wchar_t * pwzRootFolder, 
                                        DWORD dwFullSessNum, 
                                        DWORD dwIncrSessNum, 
                                        BOOL * pbThresholdHit, 
                                        ULONGLONG* pullFreeSpace = NULL,
                                        ULONGLONG* pullSpaceNeed = NULL);

DWORD WINAPI AFSetBdi(const NET_CONN_INFO *pDestInfo, const GUID guid, const wchar_t *pFile = NULL);

DWORD WINAPI AFGetBdi(BDI *pBdi, const wchar_t *pFile = NULL);

DWORD WINAPI AFFindDest(const wchar_t *pCurDest, wchar_t *pResult, DWORD dwLen, const wchar_t *pBdiFile = NULL);

LPTSTR WINAPI JobGetSystemErrorText(LPTSTR pBuf, DWORD Size, DWORD dwError);

LPTSTR WINAPI JobGetAFStorErrorText(LPTSTR pBuf, DWORD Size, DWORD dwError);

LPTSTR WINAPI JobGetAFStorErrorText_CSR(LPTSTR pBuf, DWORD Size, CREATE_SESS_ERR dwError);

HANDLE WINAPI AFIntegrateBackupBegin(const wchar_t *pDest, DWORD dwSessNo);

void WINAPI AFIntegrateBackupEnd(HANDLE *pLckHandle);

HANDLE WINAPI AFIntegrateMergeBegin(const wchar_t *pDest);

void WINAPI AFIntegrateMergeEnd(HANDLE *pLckHandle, const wchar_t *pDest);

HANDLE WINAPI AFIntegrateCatalogBegin(const wchar_t *pDest, DWORD dwSessNo);

void WINAPI AFIntegrateCatalogEnd(HANDLE *pLckHandle);

DWORD WINAPI AFGetMntPathForRemotePath(const NET_CONN_INFO *pInfo, wchar_t *pszPath, DWORD dwLen);

DWORD WINAPI AFCutMntPathForRemotePath(const NET_CONN_INFO *pInfo, BOOL bForce=TRUE);

//@curTime: current time.
//@ulTime: time period in seconds.
BOOL WINAPI AFCheckTimeExpireForBackup(const FILETIME &curTime, unsigned long long ulTime, DWORD dwJobMethod = AF_JOBMETHOD_INCR);

DWORD WINAPI AFGetSessPathByNo(const wstring &strCurDest, int iNum, wstring &strDest, wstring &strSessPath, BOOL bToPurge=FALSE);

DWORD WINAPI AFBuildBackupInfoDB(const wstring &strDest, BOOL bUpdateAll=FALSE);

DWORD WINAPI AFBuildBackupInfoDBWrap(WCHAR* strDest, BOOL bUpdateAll=FALSE);

DWORD WINAPI AFGetAllBackupInfoDB(const wstring &strDest, BACKUP_INFO_DB &infoDb);

DWORD WINAPI AFGetSpecBackupInfoDB(const wstring &strDest, BACKUP_INFO_DB &infoDb);

DWORD WINAPI AFUpdateBackupInfoDbForBackup(const wstring &strDest, const wstring &strSessPath);

DWORD WINAPI AFUpdateBackupInfoDbForOnDemandCatalog(const wstring &strDest, const wstring &strSessPath);

DWORD WINAPI AFUpdateBackupInfoDbForMerge(const wstring &strDestDel, const wstring &strSessDel, const wstring &strDestUpdate, const wstring &strSessUpdate);

///ZZ: If pwzSessPath is not NULL, we will get session path based on dwSessNum, otherwise dwSessNum will be ignored.
DWORD WINAPI AFDelItemInBackupInfoDB(const WCHAR* pwzBKDest, DWORD dwSessNum, const WCHAR* pwzSessPath);

///ZZ: If pwzSessPath is not NULL, we will get session path based on dwSessNum, otherwise dwSessNum will be ignored.
///ZZ: if dwJobMethod or ullDataSize is not equal to -1, this value will be update to backupinfodb.xml
///ZZ: dwStartSessNo is used for integration with RHA. 0: there is no such field in backupinfodb.xml, may be backed up by old D2D
///ZZ: -1: Sessions have never been merged. Others: THe least session number on destination, maybe under merging or after merging.
///ZZ: This start session number will be considered as full backup to resynch by RHA.
DWORD WINAPI AFUpdateItemInBackupInfoDB(const WCHAR* pwzBKDest, DWORD dwSessNum, const WCHAR* pwzSessPath, DWORD dwJobMethod, ULONGLONG ullLogicSize, ULONGLONG ullDataSize, DWORD dwStartSessNo = 0);

DWORD WINAPI AFEnumNetworkShare(const NET_CONN_INFO &info, vector<wstring> &vShare);

DWORD WINAPI AFGetVolumeBootSystemType(const wchar_t *pDir);

// API's responsible for managing the ArchiveInfo DB
DWORD WINAPI AFGetArchiveJobByScheduleStatusHelper(NET_CONN_INFO ConnectionInfo, DWORD dwJobType,
													 DWORD ScheduleStatus,													 
													 vector<ARCHIVE_ITEM_INFO>& Jobs,
													 BOOL bFirstJobOnly	);

DWORD WINAPI  AFSetArchiveJobScheduleStatusHelper(NET_CONN_INFO ConnectionInfo,
										   DWORD ScheduleStatus,													 
											     PAFARCHIVEJOBSCRIPT pArchiveJobJS,
											     LARGE_INTEGER TotalArchiveSize,
											     LARGE_INTEGER TotalFilCopySize);


DWORD WINAPI  AFSetArchiveJobScheduleRemoteStatusHelper(NET_CONN_INFO ConnectionInfo,
	DWORD ScheduleStatus,
	PAFARCHIVEJOBSCRIPT pArchiveJobJS);


DWORD WINAPI AFUpdateArchiveInfoDbForBackup(const wstring &strDest, DWORD ulSessionNumber, DWORD dwJoMethod, DWORD dwJobType);

DWORD WINAPI AFGetArchiveSessionsForMerge(NET_CONN_INFO ConnectionInfo,
													const wstring &strSessPath,
													vector<ARCHIVE_ITEM_INFO>& Jobs);

BOOL WINAPI CanISubmitArchiveForThisSession(const wstring &strDest,ULONG ulSessionID);
BOOL WINAPI CanISubmitArchiveForThisSession2(const wstring &strDest, ULONG ulSessionID, DWORD dwJobType);
DWORD WINAPI AFGetLastArchiveJobStatusHelper(NET_CONN_INFO ConnectionInfo, DWORD dwJobType,
											 vector<ARCHIVE_ITEM_INFO>& Jobs
											 );
DWORD WINAPI AFGetNextScheduleArchiveJobInfoHelper(NET_CONN_INFO ConnectionInfo, DWORD dwJobType,
												   vector<ARCHIVE_ITEM_INFO>& Jobs
												   );
DWORD WINAPI AFClearPendingArchiveJobsHelper(NET_CONN_INFO ConnectionInfo, DWORD dwJobType);
DWORD WINAPI GetLastSuccessfullBackupSession(const wstring &strDest, DWORD dwJobType);
DWORD WINAPI AFDisableFilecopyHelper(NET_CONN_INFO ConnectionInfo);
//this API is responsible for creating folder structure for that backup destination.
DWORD WINAPI AFTryToCreateFolder(const NET_CONN_INFO &info);

DWORD WINAPI __AFIsArchiveJobAllowed(const wstring& d2dMachineName, const NET_CONN_INFO& d2dMachineDestInfo, ULONG ulSessionID, PFILECOPYJOB_SCHEDULER_POLICY archiveJobSchedulerInfo, BOOL* bDoArchive);
DWORD WINAPI __AFQueueArchiveJob(const wstring& d2dMachineName, const NET_CONN_INFO& d2dMachineDestInfo, ULONG ulSessionNumber, DWORD dwJobMethod, PFILECOPYJOB_SCHEDULER_POLICY archiveJobSchedulerInfo, DWORD dwJobType);
DWORD WINAPI __AFChk_UptArchiveJobScheduleForNBackups(const wstring& d2dMachineName, const NET_CONN_INFO& d2dMachineDestInfo, ULONG ulSessionID, PFILECOPYJOB_SCHEDULER_POLICY archiveJobSchedulerInfo, BOOL* bArchiveJobScheduled, DWORD dwJobType);
DWORD WINAPI AFGetArchiveJobByScheduleStatusHelper2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, DWORD ScheduleStatus, vector<ARCHIVE_ITEM_INFO>& Jobs, BOOL bFirstJobOnly, DWORD dwJobType);
DWORD WINAPI AFGetScheduleArchiveJobInfoCountHelper2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, DWORD dwJobType, DWORD& count);
DWORD WINAPI AFGetNextScheduleArchiveJobInfoHelper2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, vector<ARCHIVE_ITEM_INFO>& Jobs, DWORD dwJobType);
DWORD WINAPI AFGetLastArchiveJobStatusHelper2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, vector<ARCHIVE_ITEM_INFO>& Jobs, DWORD dwJobType);
DWORD WINAPI AFSetArchiveJobScheduleStatusHelper2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, DWORD ScheduleStatus, PAFARCHIVEJOBSCRIPT pArchiveJobJS, LARGE_INTEGER TotalArchiveSize, LARGE_INTEGER TotalFilCopySize);
DWORD WINAPI AFGetNextFCSourceDeleteJobInfoHelper2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, vector<ARCHIVE_ITEM_INFO>& Jobs, DWORD dwJobType);
/*
*Purpose: Get all mapped drive from terminal session according to input user 'strUser'.
*@strUser: [INPUT] the user which belongs to the terminal session.
*@vDrvPath: [OUTPUT] mapped network drive path.
*@return: 0 for success, otherwise system error code will be returned.
*/
DWORD WINAPI AFGetAllMappedDrvPath(const std::wstring &strUser, std::vector<MAPPED_DRV_PATH> &vDrvPath);

typedef enum _eAppInstalledStatus
{
    EAIS_NOT_CHECK = 0,  // Do not check if this application has been installed.
    EAIS_2B_CHECK,       // Check if this application has been installed.
    EAIS_NOT_INSTALLED,  // Application has not been installed.
    EAIS_INSTALLED       // Application has been installed.
} E_APP_INSTALLED_STATUS;

class CAppInstalledStatus
{
public:
    CAppInstalledStatus() : m_eSQLInstalled(EAIS_2B_CHECK), m_eExchInstalled(EAIS_2B_CHECK){}
    E_APP_INSTALLED_STATUS m_eSQLInstalled;
    E_APP_INSTALLED_STATUS m_eExchInstalled;
};

DWORD WINAPI AFCheckAppInstalledStatus(CAppInstalledStatus& AppinstalledStaus);

BOOL WINAPI AFCheckDestChainContainSess(const NET_CONN_INFO &info);

//<sonmi01>2011-3-4 ###???
DWORD WINAPI AFRecordSessGuid(const std::wstring &strGuid, CONST WCHAR * pszVMUUID = NULL);

BOOL WINAPI AFCheckIncBackupCanBeDone(const std::wstring &strDest, CONST WCHAR * pszVMUUID = NULL);

DWORD WINAPI AFGetLastIncBackupPath(const std::wstring &strDest,std::wstring &strPreviousSessionPath);
#define DEFAULT_CONTROL_VALUE_NAME         L"JobTypeCtrl"
#define CONTROL_CORRUPTED_SESSION_NUMBER   L"CorruptedSessNumber"
#define CONTROL_SESSION_LIST               L"SessionList"
#define DEFAULT_CONTROL_RELATIVE_KEYNAME   CST_REG_ROOT_L L"\\" AF_REG_BACKUP_DLL

DWORD WINAPI AFSetControlDWORDValue(
      DWORD dwCtrlValue, 
      const WCHAR* pwzKeyName = DEFAULT_CONTROL_RELATIVE_KEYNAME, 
      const WCHAR* pwzValueName = DEFAULT_CONTROL_VALUE_NAME,
      bool bCreateKey = true);

DWORD WINAPI AFGetControlDWORDValue(
      DWORD& dwCtrlValue, 
      const WCHAR* pwzKeyName = DEFAULT_CONTROL_RELATIVE_KEYNAME, 
      const WCHAR* pwzValueName = DEFAULT_CONTROL_VALUE_NAME);

DWORD WINAPI AFSetControlStringValue(
    const WCHAR* pwzStrVal, 
    const WCHAR* pwzKeyName = DEFAULT_CONTROL_RELATIVE_KEYNAME, 
    const WCHAR* pwzValueName = DEFAULT_CONTROL_VALUE_NAME,
    bool bCreateKey = true);

DWORD WINAPI AFGetControlStringValue(
    wstring& wsStrVal, 
    const WCHAR* pwzKeyName = DEFAULT_CONTROL_RELATIVE_KEYNAME, 
    const WCHAR* pwzValueName = DEFAULT_CONTROL_VALUE_NAME);

//ZZ: [2013/10/08 15:39] Added for storing control information under backup destination instead of storing under registry table.
//ZZ: Be careful to use this 4 APIs designed for Oolong build. This 4 APIs do not consider the case backup destination chain which
//ZZ: which is not supported by Oolong, all inc/resync sessions will be located together with its full session in the same folder.
//ZZ: Please feel free to enhance this 4 APIs in case someone intend to support this terrible case.
DWORD WINAPI AFSetControlDWORDValueEx(
    DWORD dwCtrlValue, 
    const WCHAR* pwzBKDest, 
    const WCHAR* pwzValueName = DEFAULT_CONTROL_VALUE_NAME);

DWORD WINAPI AFGetControlDWORDValueEx(
    DWORD& dwCtrlValue, 
    const WCHAR* pwzBKDest, 
    const WCHAR* pwzValueName = DEFAULT_CONTROL_VALUE_NAME);

DWORD WINAPI AFSetControlStringValueEx(
    const WCHAR* pwzStrVal, 
    const WCHAR* pwzBKDest, 
    const WCHAR* pwzValueName);

DWORD WINAPI AFGetControlStringValueEx(
    wstring& wsStrVal, 
    const WCHAR* pwzBKDest, 
    const WCHAR* pwzValueName);

typedef struct _SESSION_ITEM_
{
	DWORD dwNumber;
	DWORD dwType;
}SESSION_ITEM, *PSESSION_ITEM;

DWORD WINAPI AFSetSessionItem(
							  SESSION_ITEM Item,
							  const WCHAR* pwzKeyName = DEFAULT_CONTROL_RELATIVE_KEYNAME,
							  const WCHAR* pwzValueName = CONTROL_SESSION_LIST,
							  bool bCreateKey = true
							  );

DWORD WINAPI AFGetSessionItems(
							   PSESSION_ITEM* ppItems,
							   DWORD* pdwCount,
							   const WCHAR* pwzKeyName = DEFAULT_CONTROL_RELATIVE_KEYNAME,
							   const WCHAR* pwzValueName = CONTROL_SESSION_LIST
							   );

BOOL WINAPI AFFreeSessionItem(
							  PSESSION_ITEM pItems
							  );

DWORD WINAPI AFUpdateSessionItemById(
									 DWORD dwSessNumber,
									 DWORD dwNewSessNumber,
									 const WCHAR* pwzKeyName = DEFAULT_CONTROL_RELATIVE_KEYNAME,
									 const WCHAR* pwzValueName = CONTROL_SESSION_LIST
									 );
DWORD WINAPI AFDeleteSessionItemsById(
									 DWORD dwStartSessNumber,
									 DWORD dwEndSessNumber,
									 const WCHAR* pwzKeyName = DEFAULT_CONTROL_RELATIVE_KEYNAME,
									 const WCHAR* pwzValueName = CONTROL_SESSION_LIST
									 );

DWORD WINAPI AFGetVolumeGuidForLocalShare(const std::wstring &strLocalShare, std::wstring &strGuid);

//@strGuid: '\\?\Volume{c9867edf-5a37-11dd-809b-806d6172696f\'
DWORD WINAPI AFGetMntForVolumeGUID(const std::wstring &strGuid, std::vector<std::wstring> &vMnt);

//check if the destnation contains backup
BOOL WINAPI AFCheckDestContainBackup(const std::wstring &dest);

//<sonmi01>2010-5-6 ###???
struct CMD5Helper
{
	//********************************************************************
	//[OUT BYTE * bMd5Digest] is 16 byte length, i.e. bMd5Digest[16]
	static VOID CalcBinMD5(IN CONST BYTE* pBuffer, IN INT nLength, OUT BYTE * bMd5Digest);
	static VOID CalcStringMD5(IN LPCTSTR szText, OUT wstring & wstrMd5Digest);

	//********************************************************************
	static VOID BinToString(CONST BYTE * pBuffer, INT nLength, CString & strText);
};
VOID AFCalcStringMD5(IN LPCTSTR szText, OUT wstring & wstrMd5Digest);

BOOL WINAPI AFValidateSessPwdByHash(const PBYTE pbPwdBuf, DWORD dwPwdBufSize, const WCHAR* pwzPwdHash);

BOOL WINAPI AFValidateSessPwdBySessInfo(const WCHAR* pwzSessPwd, const WCHAR* pwzBKDest, DWORD dwSessNum, bool bChkDataStoreEPWD = false);

DWORD WINAPI AFGetHashValueStringForBin(PBYTE pbDataBuf, DWORD dwBufLen, wstring& wsHashVal);

DWORD WINAPI AFGetHashValueStringForStr(WCHAR* pwzDataStr, wstring& wsHashVal);

DWORD WINAPI AFGetFullBackup4Session(const WCHAR* pwzBKDest, DWORD dwSessNum, PBACKUP_ITEM_INFO pstFullBackupItem, PBACKUP_ITEM_INFO pstCurBackupItem);

DWORD WINAPI AFGetBackupDetails(BackupInfoXml& BackupDetail, const WCHAR* pwzBKDest, DWORD dwSessNum);

class CByteBuf;

// Open it when file exists.
// Create it when file not exists.
// 0 - Succeed
// 1 - Password Management file created
// -5 - Unable to create password management file
IPasswordManagement* __stdcall CreatePasswordManagement(LPCTSTR szFileName, LPCTSTR szUserName, LPCTSTR szPassword, CByteBuf *pEncMasterKey = NULL);

IPasswordManagement* __stdcall CreatePasswordManagementEx(LPCTSTR szFileName, LPCTSTR szUserName, LPCTSTR szPassword, int& nErrorCode, CByteBuf *pEncMasterKey);

long WINAPI CreatePasswordManagementSmart(IPasswordManagement** ppKeyMgmtInstance, wstring* pwsKeyMgmtDBPath = NULL, const WCHAR* pwzUserName = NULL, const WCHAR* pwzPassword = NULL);

DWORD WINAPI AFGetKeyMgmtDBPath(WCHAR* pwzKeyDBPath, DWORD* pdwKeyDBPathLen);

DWORD WINAPI AFAddSessPwdIntoKeyMgmtDB(const GUID& guidSession, const WCHAR* pwzSessPwd, DWORD dwSessPwdLen);

DWORD WINAPI AFGetSessPwdFromKeyMgmtDB(WCHAR* pwzSessPwd, DWORD* pdwSessPwdLen, const GUID& guidSession);

DWORD WINAPI AFGetSessPwdFromKeyMgmtDBEx(WCHAR* pwzSessPwd, DWORD* pdwSessPwdLen, const WCHAR* pwzSessGUID);

DWORD WINAPI AFGetSessPwdFromKeyMgmtBySessNum(WCHAR* pwzSessPwd, DWORD* pdwSessPwdLen, const WCHAR* pwzBKDest, DWORD dwSessNum);

DWORD WINAPI AFRemoveSessPwdInKeyMgmtDB(const GUID& guidSession);

DWORD WINAPI AFRemoveSessPwdInKeyMgmtDBEx(const WCHAR* pwzSessGUID);

DWORD WINAPI AFShrinkKeyMgmtDBFile();

DWORD WINAPI AFUpdateAdminAccountInKeyMgmtDB(const WCHAR* pwzAdminUser, const WCHAR* pwzAdminPwd);

DWORD WINAPI AFGetMultiSessPwdFromKeyMgmtDB(std::vector<CSessPwdWrap>& vecMutiSessPwd);

bool WINAPI AFIsFullSession(const WCHAR* pwzSessPath, BackupInfoXml* pBKInfoXML = NULL);

bool WINAPI AFIsSessNeedScan(const WCHAR* pwzSessPath);

long WINAPI AFCreateSessHoleFile(const WCHAR* pwzSessPath);

long WINAPI AFFilterSess2Scan(std::vector<std::wstring>& vecSessPath, bool bInitHoleFile = true, bool bChkNeedScan = true);

bool WINAPI AFIsSessScanNeedRun(NET_CONN_INFO& stBKDestInfo, std::vector<std::wstring>* pvecSessPath2Scan = NULL);

DWORD WINAPI AFGetAllFullSessPath(
    std::vector<std::wstring>& vecSessPath,
    NET_CONN_INFO& stBKDestInfo,
    bool bFindfirst = true, 
    bool bWithInc = true);

DWORD WINAPI AFScanSession4ReclaimHole(
    NET_CONN_INFO& stBKDestInfo, 
    const WCHAR* pwzTmpPath4Scan = NULL,
    DWORD* pdwProcessID = NULL);

DWORD WINAPI __AFGetCacheFile4Sync( wstring &cacheFileName, long lDataType = SYNC_DATA_TYPE_BACKUP);
DWORD WINAPI __AFDeleteCacheFile4Sync(long lDataType = SYNC_DATA_TYPE_BACKUP);
DWORD WINAPI __AFReSyncFullData(wstring &cacheFileName, long lDataType = SYNC_DATA_TYPE_BACKUP);
wstring WINAPI __AFGetD2DSysFolder();
union JobIdentify
{
	int sessID;
	wchar_t jobUUID[MAX_PATH];
	wchar_t sessionUUID[MAX_PATH];
};
DWORD WINAPI __AFUpdatePurgeBackupRecord4Sync(int sessId, const wchar_t *uniqueID);
DWORD WINAPI __AFUpdateNewBackupRecord4Sync(PBackupInfoXml backupInfo, const wchar_t *uniqueID);
DWORD WINAPI __AFUpdatePurgeJobRecord4Sync(JobIdentify jobId, const wchar_t *uniqueID, long lDataType = SYNC_DATA_TYPE_BACKUP);
DWORD WINAPI __AFUpdateNewJobRecord4Sync(void* pJobInfo, const wchar_t *uniqueID, long lDataType = SYNC_DATA_TYPE_BACKUP);
BOOLEAN WINAPI __AFIsFirstSyncCalled(long lDataType = SYNC_DATA_TYPE_BACKUP);
DWORD WINAPI __AFMarkFirstSyncCalled(long lDataType = SYNC_DATA_TYPE_BACKUP);
wstring WINAPI AFGetCachedVmInfo4Trans();
wstring WINAPI AFGetAllVmInfo4Trans();
DWORD WINAPI AFDeleteVmInfoTransFile();
DWORD WINAPI AFDeleteAllVmInfoTransFile();
wstring WINAPI AFGetArchiveCacheFileName4Trans();
DWORD WINAPI AFDeleteArchiveCacheFileTrans();
DWORD WINAPI __AFBeforeSessionSync(BOOL bFullSync, std::vector<BACKUP_ITEM_INFOEX>& vInfoEx);
DWORD WINAPI __AFDeleteRPSSessionCacheFile();

// chefr03, check registry key CST_REG_ROOT_L\\FaileoverNewSettingFlag
// If it's value is "1", do "FULL" backup job this time
// Otherwise, do nothing
#define	REG_D2D_BASE_REGISTRY		CST_REG_ROOT_L	// defined in brandname.h 
#define REG_FORCE_FULL_BACKUP_FLAG	L"ForceFullBackupFlag"

// This API is used by AFBackupEntry.cpp:AdjustBackupJobMethodPreJob()
BOOL WINAPI AFGetForceFullBackupFlag(void);

// This API is used by AFBackupEntry.cpp:UpdateBackupInfoJobEndStatus()
DWORD WINAPI AFClearForceFullBackupFlag(void);


class IAFSess
{
public:

    virtual void Release() = 0;

    virtual DWORD GetSessPath(const wstring &strDest, vector<wstring> &vPath) = 0;

    //Get session path from the specified destination instead of all backup destinations.
    virtual DWORD GetSpecifiedSessPath(const wstring &strDest, vector<wstring> &vSess) = 0;

    //Get the last session path in all backup destinations.
    virtual DWORD GetLastSessPath(const wstring &strDest, wstring &strSess) = 0;

    virtual BOOL IsSessionFull(const wstring &strSess) = 0;

    //'E:\fsd_data\W2K3-FLASH\VStore\S0000000001'
    //'E:\fsd_data\W2K3-FLASH\Index\S0000000001.idx'
    //'E:\fsd_data\W2K3-FLASH\Catalog\S0000000001'
    //Above items must exist at the same time.
    virtual DWORD GetSessInfoByNum(const wstring &strDest, int iNum, vector<wstring> &vSessInfo, BOOL bToPurge = FALSE) = 0;

    //Retrieve master session path according to session number.
    //Note that the session may not exist on current destination given by strCurDest.
    //@strSessPath: master session path.
    //@strDest: backup destination which contains the master session.
    virtual DWORD GetSessPathByNum(const wstring &strCurDest, int iNum, wstring &strSessPath, wstring &strDest, BOOL bToPurge=FALSE) = 0;

	//Similar to GetSessPathByNum, using the AFMgrDestInit API to connect the remote path 
	virtual DWORD ANMGetSessPathByNum(const NET_CONN_INFO &info, int iNum, wstring &strSessPath, wstring &strDest, BOOL bToPurge=FALSE)=0;

    virtual DWORD GetSessPathByNumEx(const wstring &strCurDest, int iNum, wstring &strSess, NET_CONN_INFO &info, BOOL bToPurge = FALSE) = 0;

    virtual DWORD GetSessVerByNo(const wstring &strDest, int iNum, DWORD &dwMajor, DWORD &dwMin) = 0;

    //If version of specified session is bigger than current verion, iResult is >0.
    //If version of specified session is less than current version, iResult is <0.
    //If version of specified session is equal to current version, iResult is 0.
    virtual DWORD CheckSessVerByNo(const wstring &strDest, int iNum, int &iResult) = 0;

    //get sessions only from specific destination. These sessions include merging session.
    //this api will retrieve sessions from VStore folder directly.
    virtual DWORD GetSessFromSpecificDest(const wstring &strDest, vector<wstring> &vSess) = 0;

    virtual BOOL IsSessionMerging(const wstring &strSess) = 0;
};

DWORD WINAPI AFCreateIAFSess(CDbgLog *pLog, IAFSess **ppIAFSess);

void WINAPI AFReleaseIAFSess(IAFSess *pIAFSess);

/*
*Get current app session version.
*/
#define D2D_SESS_VERSION_FOR_R16				0x00020001
#define D2D_SESS_VERSION_FOR_OOLONG             0x00030000	// Code name "Oolong" is used for ARCserve UDP v1
#define D2D_SESS_VERSION_FOR_TUNGSTEN			0x00060000	// For Tungsten, session version change to 6.0.

void WINAPI GetSessVer(DWORD &dwMajor, DWORD &dwMin, DWORD dwSessVer = D2D_SESS_VERSION_FOR_TUNGSTEN);
void WINAPI GetSessVerStr(wstring &strVer, DWORD dwSessVer = D2D_SESS_VERSION_FOR_TUNGSTEN);

DWORD WINAPI AFChangeBakTypeForSess(const wstring &strSessPath, DWORD dwBakType);

DWORD WINAPI AFChangeBakTypeForSessWrap(WCHAR* szSessPath, DWORD dwBakType);

#ifdef __cplusplus
extern "C"{
#endif

typedef struct _CHKMERGE_PARAM
{
    DWORD dwSessNo; //master session number.
    wchar_t szDest[MAX_PATH]; //any backup destination in a backup chain.
}CHKMERGE_PARAM, *PCHKMERGE_PARAM;

#ifdef __cplusplus
}
#endif

BOOL WINAPI AFCheckSessCanMerge(const CHKMERGE_PARAM &param);
//luoca01: functions to check VDDK  and vix version
#define VDDK_NOT_INSTALL -201
#define VDDK_NO_64BIT_BINARY -202
#define VIX_NOT_INSTALL -203
int  AFGetVddkVersion(unsigned long& nVerHigh, unsigned long& nVerLow, unsigned long& nVerSub);
int  AFGetVixVersion(unsigned long& nVerHigh, unsigned long& nVerLow, unsigned long& nVerSub);
//zouyu01: functions for schedule export on 2010-12-02.
class IAFExportMgr
{
public:
    virtual void Release() = 0;
    virtual DWORD AddSuccedBackupNum() = 0;
    virtual DWORD EnableShExp(BOOL bEnable) = 0;
    virtual BOOL CheckBackupNumMeet(unsigned long long ullNum) = 0;
};

//<sonmi01>2014-7-29 ###???
DWORD WINAPI CreateIAFExportMgr(CDbgLog *pLog, IAFExportMgr **ppIExpMgr, LPCWSTR pNodeInstanceID = NULL); 

typedef DWORD(WINAPI *_pfn_CreateIAFExportMgr)(CDbgLog *pLog, IAFExportMgr **ppIExpMgr, LPCWSTR pNodeInstanceID);

//zouyu01: do impersonation.
//This function will use admin account recorded in D2D registry to do impersonation.
//Return: 0 for success, otherwise, system standard error code will be returned.
DWORD WINAPI AFImpersonate();

void AFRevertToSelf();


DWORD WINAPI XMntImpersonate();		// Only used by Mounting volume  
void XMntRevertToSelf();			// Only used by Mounting volume  


DWORD WINAPI GetLogonId(LUID &id);

#ifdef __cplusplus
extern "C"{
#endif
typedef struct _GRT_OS_INFO
{
    DWORD dwMajor;
    DWORD dwMinor;
    DWORD dwProduct;
    DWORD dwCpuArc;
}GRT_OS_INFO, *PGRT_OS_INFO;
#ifdef __cplusplus
}
#endif
DWORD WINAPI AFGetGrtOSInfo(const wstring &strDest, DWORD dwSessNo, GRT_OS_INFO &info);

DWORD WINAPI AFGetPathDriveType(const wchar_t *pDir);
typedef struct _ST_D2D_VER
{
    DWORD dwMajor;
    DWORD dwMinor;
    DWORD dwBuild;
    DWORD dwPatchNum;
}ST_D2D_VER;
DWORD WINAPI AFGetD2DVersion(ST_D2D_VER& stD2DVer);

DWORD WINAPI AFTrytoMountVolumeGuidPath(const wstring &strGuid, wstring &strMntPath);

DWORD WINAPI AFDismountVolume(const wstring &strGuid);

typedef struct _AF_NET_SHARE
{
    wchar_t szPath[MAX_PATH];
    wchar_t szUser[MAX_PATH];
}AF_NET_SHARE, *PAF_NET_SHARE;

//this function is used to get all connections in current logon session.
DWORD WINAPI AFGetCurrentConnections(vector<AF_NET_SHARE> &vShare);

//check whether the given access info can be used for given remote share in
//current logon session.
BOOL WINAPI AFCheckNetPreConnect(const NET_CONN_INFO &info);

//After catalog is generated, update catalog information into backup history.
//refer to issue 20150353.
DWORD WINAPI AFUpdateCatalogInfoHistory(const wstring &strSessGuid, const wstring &strBakInfoXml, const wchar_t *pSubFolder = NULL, bool bSyncEdge = true);

BOOL WINAPI AFCheckJobExistHistory(const FILETIME &curTime, unsigned long long ullPeriod, DWORD dwJobType);

BOOL WINAPI IsSQLInstanceExpress(LPCTSTR szInstance);

//ZZ: Return value indicates the instance count found.
DWORD WINAPI AFGatherSQLExpressInstance(std::vector<std::wstring>& vecInstance, bool bSQLExpressOnly = false);

//ZZ: Get debug configuration value.
DWORD WINAPI AFGetD2DDebugCfgValue(
    const WCHAR* pwzKeyName, 
    const WCHAR* pwzSectionName, 
    const WCHAR* pwzFileName,
    const WCHAR* pwzConfigPath = NULL); //ZZ: NUll indicate config path is [D2D installation path]\Configuration

wstring WINAPI AFGetD2DDebugCfgString(
    const WCHAR* pwzKeyName, 
    const WCHAR* pwzSectionName, 
    const WCHAR* pwzFileName,
    const WCHAR* pwzConfigPath = NULL); //ZZ: NUll indicate config path is [D2D installation path]\Configuration

//[zouyu01] 2011-5-12, update related backup information to BackupInfoDB.xml.
//Note that the bakInfo must be in the same dest with strDest.
DWORD WINAPI AFUpdateBackupInfoDb(const wstring &strDest, const BackupInfoXml &bakInfo);

/**
* Get the session lock wait and retry time from registry.
* @dwJobType    - the job type defined in afjob.h, such as AF_JOBTYPE_BACKUP
* @dwWait       - the wait time for lock, in ms
* @dwRetry      - max retry time
* @dwDefWait    - default wait time
* @dwDefRetry   - default retry time
*/
void WINAPI AFGetSessLockWaitAndRetryTime(/*in */DWORD dwJobType,
    /*out*/DWORD& dwWait, /*out*/DWORD& dwRetry,
    /*in */DWORD dwDefWait, /*in */DWORD dwDefRetry);

/**
* For replication job, for AQA use
* Get the running time and job status from the registry table. Both the two should be set
* in the registry, else this function will fail. return 0 success, others fail.
* @dwRunnngTime             - the running time, in ms
* @wstrJobStatus            - the final job status, such as AF_JOB_STATUS_FAILED_STRING
*/
int WINAPI AFGetRepDbgRunningTimeAndStatus(DWORD& dwRunningTime, wstring& wstrJobStatus);

/**
* Get the Error text for CID2DPlugin.dll
* @pBuf - pointer to the buffer to receive the string.
* The string is truncated and NULL terminated if it is longer than 
* the number of characters specified.
* return 0 success, others fail.
*/
int WINAPI AFGetCID2DPluginErrorText(wchar_t* pBuf, DWORD Size, DWORD dwError);


//[zouyu01] 2011-9-21, update backup information to BackupInfoDB.xml.
// This function is for integration with RHA.
DWORD WINAPI AFUpdateBackupInfoDb2(const wchar_t *pDest, const wchar_t *pSess);

/*
*@purpose: retrieve hidden system volume information.
*/
DWORD WINAPI AFGetSystemVolume(AF_VOL_INFO::VOL_INFO &sysVol);

/*
*@purpose: try to lock the volume.
*Note: after volume is locked, the handle for that volume is returned, user must use this handle after lock.
*/
DWORD WINAPI AFLockVolume(const AF_VOL_INFO::VOL_INFO &vol, HANDLE &hVol);

//this function will unlock the volume and close handle:hVol.
DWORD WINAPI AFUnlockVolume(HANDLE &hVol);

//Note: AFNetMgrInitDest & AFNetMgrDoneInitDest must be called in the same thread.
DWORD WINAPI AFNetMgrInitDest(const wchar_t *pszDest, DWORD *pdwId, const wchar_t *pszUser, const wchar_t *pszPwd);

void WINAPI AFNetMgrDoneInitDest(DWORD dwId);
//check whether the given drive letter is used by network redirection.
//Note, in this api, it will impersonate the terminal session, then revert it. Be attention to call this api.
BOOL WINAPI AFIsDriveLetterUsedForNet(const wchar_t *pszDriveLetter);

DWORD WINAPI AFReadDestinationSig(const wchar_t *pszDest, wchar_t *pszGuid, DWORD dwLen);

INT WINAPI AFCheckCatalogStatus(const wstring &strSessPath);

INT WINAPI AFUpdateCatalogStatus(const wstring &backupDest, const wstring &strSessPath);

DWORD WINAPI AFReplaceD2DPathWithLocalTemp(IN LPCTSTR pwszLocalTemp, IN LPCTSTR pwszD2DPath, OUT wstring & strNewPath);


typedef struct _DISK_INFO_
{
	DWORD    dwSignature;      // disk signature
	DWORD    dwSectorSize;     
	LONGLONG llExtStartPos;    // The offset from the beginning of the disk to the extent, in bytes.
	LONGLONG llExtLenght;      // The number of bytes in this extent
	LONGLONG llVolumeOffset;   // volume offset corresponding to the start of this extent (ulExtentStartPos)
	DWORD    dwBusType;        // the value is same with _VDS_STORAGE_BUS_TYPE which comes from vdslun.h
}DISK_INFO, *PDISK_INFO;


#define FST_RAW      1
#define FST_FAT      2
#define	FST_FAT32    3
#define	FST_NTFS     4
#define	FST_REFS     9

typedef struct _VOLEXT_INFO_
{
	WCHAR szName[MAX_PATH];
	WCHAR szGuidPath[MAX_PATH];
	DWORD dwClusterSize;
	DWORD dwFSType;             // file system type
	BOOL  bIsBackup;
	BOOL  bIsESPPartition;		// if this is esp partition
	DWORD dwNumOfDisks;			// number of disks 	
	DISK_INFO Disks[1];
}VOLEXT_INFO, *PVOLEXT_INFO;

typedef struct _VOLDSK_INFO_
{
	DWORD      dwCount;
	PVOLEXT_INFO  pVolDiskInfo[1];
}VOLDSK_INFO, *PVOLDSK_INFO;

DWORD WINAPI AFGetVolumeDiskExtendFromAdfCfg(IN LPCWSTR lpRootPath, IN DWORD dwSessNumber, OUT PVOLDSK_INFO* ppVolDskInfo);
void  WINAPI AFFreeVolDiskInfo(PVOLDSK_INFO pVolDskInfo);

///ZZ: Get specified backup destination information.
class CBKDevEntry
{
public:
    CBKDevEntry() { Clear(); }
    CBKDevEntry(const CBKDevEntry& CurBKDevInfo)
    {
        memcpy_s(&guidBKDev, sizeof(GUID), &CurBKDevInfo.guidBKDev, sizeof(GUID));
        dwDriveType = CurBKDevInfo.dwDriveType;
        wsBKDest = CurBKDevInfo.wsBKDest;
        wsDomain = CurBKDevInfo.wsDomain;
        wsUserName = CurBKDevInfo.wsUserName;
        wsUserPwd = CurBKDevInfo.wsUserPwd;
    }

    void Clear()
    {
        memset(&guidBKDev, 0, sizeof(GUID));
        dwDriveType = 0;
        wsBKDest.clear();
        wsDomain.clear();
        wsUserName.clear();
        wsUserPwd.clear();

    }

    GUID    guidBKDev;
    DWORD   dwDriveType;
    wstring wsBKDest;
    wstring wsDomain;
    wstring wsUserName;
    wstring wsUserPwd;
};

class CBKDevSig
{
public:
    CBKDevSig() { Clear(); }
    void Clear()
    {
        memset(&guidHeader, 0, sizeof(GUID));
        memset(&guidBackup, 0, sizeof(GUID));
        dwPrevSigSize = 0;
        dwNextSigSize = 0;
        PrevDevInfo.Clear();
        NextDevInfo.Clear();
    }

    GUID        guidHeader;
    GUID        guidBackup;
    DWORD       dwPrevSigSize;
    DWORD       dwNextSigSize;
    CBKDevEntry PrevDevInfo;
    CBKDevEntry NextDevInfo;
};

///ZZ: Before calling these API please make sure all destinations are accessible.
DWORD WINAPI AFGetBKDevSig(CBKDevSig& CurBKDevSig, const WCHAR* pwzCurBKDest);

///ZZ: If there is only one backup destination, we cannot find connection information, this API will return D2DCOMM_E_UNABLE_FIND_SEPARATE_BKDEST_INFO
DWORD WINAPI AFGetBKInfo(NET_CONN_INFO& stCurBKInfo, const WCHAR* pwzCurBKDest);

// add by danri02 to check the exchange restore account privileges
typedef struct _Exch_VSS_Restore_Account_Check_Result
{
	enum ExchVer
	{
		EXCH_2003 = 10,
		EXCH_2007,
		EXCH_2010,
		EXCH_2013
	};
	enum ExchJobType
	{
		EXCH_VSS_BACKUP = 1,
		EXCH_VSS_RESTORE 
	};
	DWORD		dwPassed;
	HANDLE		TokenOfCheck;
	DWORD		dwExchVer;
	DWORD		dwExch_D2D_Job_Type;

}EXCH_VSS_RESTORE_ACCOUNT_CHECK_RESULT,*PEXCH_VSS_RESTORE_ACCOUNT_CHECK_RESULT;


DWORD WINAPI AFCheckExchange_VSS_Restore_Account_Privilege(PEXCH_VSS_RESTORE_ACCOUNT_CHECK_RESULT pResult);

BOOL WINAPI AFIsFileRestoreSkippedWithoutAddingCounter(LPCWCH pwszFilePath);
BOOL WINAPI AFIsDirectoryRestoreSkippedWithoutAddingCounter(LPCWCH pwszDirectoryPath);

//ZZ: [2013/01/15 15:37] Added for handling case volume is shrunk when backup.
#define VOL_MAX_LEN_FILE_SYSTEM_NAME                      8
#define VOL_REFS_BITMAP_ALIGN_BYTES					     (64*1024*1024)

typedef struct _stVolumeInfo
{	
    ULONGLONG ullBitmapSize;			                  //ZZ: Bitmap size on volume(or snapshot). Normally equal to cluster count on volume.
    ULONGLONG ullUsedSize;							      //ZZ: Used bytes on volume.	
    ULONGLONG ullBackupSize;						      //ZZ: Backup size.
    ULONGLONG ullElapsedTime;						      //ZZ: Elapsed time for backup. In microsecond.
    ULONGLONG ullVolumeSize;	   						  //ZZ: Volume size. For ReFS this size will be greater than ullClusterCnt * dwClusterSize because bitmap is aligned by 64M
    BOOL	  bIsIncremental;	 					      //ZZ: If this is volume is backup as incremental.
    BOOL      nIsDynamicVol;							  //ZZ: If this is a dynamic volume
    BOOL	  bIsBackedUp;								  //ZZ: If this volume has been backed up.
    DWORD     dwVolumeType;                               //ZZ: Volume type. refer to AF_VOLUME_TYPE and VDS_VOLUME_TYPE
    DWORD     dwClusterSize;                              //ZZ: Cluster size. In bytes
    DWORD     dwFileSystemType;							  //ZZ: File system type. refer to VDS_FILE_SYSTEM_TYPE.
    WCHAR     wzFileSystem[VOL_MAX_LEN_FILE_SYSTEM_NAME]; //ZZ: File system name.
    WCHAR     wzSnapshotName[MAX_PATH];			          //ZZ: Device name of snapshot for this volume.
    WCHAR     wzVolumeName[MAX_PATH];                     //ZZ: Volume Name
    WCHAR     wzVolumeGUID[MAX_PATH];                     //ZZ: Volume GUID
    WCHAR     wzVolumeLabel[MAX_PATH];                    //ZZ: Volume label
    WCHAR     wzVolumeDeviceName[MAX_PATH];               //ZZ: Device Name
}ST_BKVOL_INFO, *PST_BKVOL_INFO;

typedef std::vector<ST_BKVOL_INFO> BKVolInfoVector;

long WINAPI AFGetVolumeInfoFromDRInfo(BKVolInfoVector& vecVolInfo, const WCHAR* pwzBKDestOrSessPath, DWORD dwSessNum, bool bBackupVolOnly);

long AFEncryptStr(wstring& wsCipherStr, const wstring wsPlainStr, ICryptoWrapperInterface* pCryptoWrap /* = NULL */);
long AFDecryptStr(wstring& wsPlainStr, const wstring wsCipherStr, ICryptoWrapperInterface* pCryptoWrap /* = NULL */);

//ZZ: APIs used for manage encryption information using key file
//////////////////////////////////////////////////////////////////////////
//ZZ: Save a data buffer into key file. This data will be encrypted by public key, while private key is encrypted by pwzPassword.
long WINAPI AFKeyFileSaveData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer to be encrypted and saved into key file path.
    DWORD dwDataBufSize,         //ZZ: Specify data buffer size, in bytes.
    const WCHAR* pwzKeyFilePath, //ZZ: Specify a valid and accessible full path which used to store key and data.
    const WCHAR* pwzPassword     //ZZ: Specify a password to encrypt private key. If this parameter is empty. all data is plain text.
    );

//ZZ: Read decrypted data from key file. When input buffer size is not enough or pbDatabuffer is NULL this API will return D2DCRYPTO_E_MORE_DATA.
//ZZ: and dwDataBufSize will receive required buffer size.
long WINAPI AFKeyFileReadData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer which receives decrypted data. Set NULL to get required size.
    DWORD& dwDataBufSize,        //ZZ: Specify data buffer size and receive decrypted size. Return required size if size is not enough or pbDataBuffer is NULL, 
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read.
    const WCHAR* pwzPassword     //ZZ: Specify password used to decrypt private key.
    );

//ZZ: Replace data saved in current key file. This data will be encrypted using public key stored in key file
long WINAPI AFKeyFileUpdateData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer to replace the data in key file.
    DWORD dwDataBufSize,         //ZZ: Specify data buffer size.
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read. 
    const WCHAR* pwzPassword     //ZZ: Not used now. should be set as NULL.
    );

//ZZ: Decrypt data stored in key file using original password and encrypt them using new password. If current key file is not encrypted data will be encrypted.
//ZZ: If new password is NULL or empty. the data will be decrypted and saved into key file in plain text format.
long WINAPI AFKeyFileUpdatePassword(
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read. 
    const WCHAR* pwzNewPassword, //ZZ: Specify new password to encrypt private key. If this parameter is NULL or empty. private key will be plain text.
    const WCHAR* pwzCurPassword  //ZZ: Specify original password to decrypt private key.
    );

//ZZ: Save data store hash key and encrypted by session password
long WINAPI AFSaveDataStoreHashKey(
    const WCHAR* pwzBKDest,    //ZZ: Backup destination path.
    DWORD        dwSessNum,    //ZZ: Session number.
    const WCHAR* pwzDSHashKey, //ZZ: Data store hash key which used to encrypt session data.
    const WCHAR* pwzPassword   //ZZ: Password to encrypt data store hash key.
    );

//ZZ: Save data store hash key and encrypted by session password
long WINAPI AFGetDataStoreHashKey(
    wstring&     wsDSHashKey,  //ZZ: Data store hash key.
    const WCHAR* pwzBKDest,    //ZZ: Backup destination path.
    DWORD        dwSessNum,    //ZZ: Session number.
    const WCHAR* pwzSessPwd    //ZZ: Session password to decrypt data store hash key.
    );

long WINAPI AFGetDataStoreHashKeyWrap(
	WCHAR* pwsDSHashKey, 
	const WCHAR* pwzBKDest, 
	DWORD dwSessNum, 
	const WCHAR* pwzSessPwd
	);

//ZZ: Update data store hash key for session
long WINAPI AFUpdateDataStoreHashKey(
    const WCHAR* pwzBKDest,       //ZZ: Backup destination path.
    DWORD        dwSessNum,       //ZZ: Session number.
    const WCHAR* pwzNewDSHashKey  //ZZ: New data store hash key which used to encrypt session data.
    );

//////////////////////////////////////////////////////////////////////////
//ZZ: 2013.04.16 Added for getting data store information.
#define AFCOREFUNCTION_API_VALIDATION_INFORMATION           L"##ADMIN$$!@#$%^$$CA$$5241494E$$VALIDATION$$+_)(*&##"

long WINAPI AFGetDataStoreInfo(
    wstring*     pwsDSHashKey,    //ZZ: Receive data store hash key.
    wstring*     pwsDSPassword,   //ZZ: Receive data store password. 
    const WCHAR* pwzDSIdentity,   //ZZ: Specify data store identity for which information is retrieved. Can be GUID, name or common path.
    const WCHAR* pwzAdminAccount  //ZZ: Specify administrator information to call this API.
    );

long WINAPI AFGetD2DSessionPwd(
	const wchar_t*	pwszSessBackupInfoPath,		//the BackupInfo.xml file path of the session 
	const WCHAR*	pwzSessGUID,				//Session GUID
	DWORD			dwBackupDescType,			//Backup Session destination type: 0 - backup to share folder; 1 - backup to RPS datastore
	std::wstring&	wstrPwd,					//Encryption password
    const WCHAR*	pwzAdminAccount				//ZZ: Specify administrator information to call this API.
    );

//////////////////////////////////////////////////////////////////////////
// wanmi12
typedef struct _NMGR_D2D_USER_INFO
{
	DWORD size;						// Size of current structure
	BOOL isNeedImpnt;				// Whether need to impersonate inside the function. TRUE, will impersonate the session that is created by Net Manager system, FALSE, will not impersonate.
	wchar_t wsD2DUser[MAX_PATH];	// The User name of curent D2D. it's used for creating new session to be impersonated. it's only valid when isUseCurToken is TRUE
	wchar_t wsD2DPSW [MAX_PATH];	// The Password for user identified by wsD2DUser
	DWORD Flags;					// if no more option , must set it to 0
}NMGR_USER_INFO, *PNMGR_USER_INFO, NMGR_EXTEND_OPTION, *PNMGR_EXTEND_OPTION;

//Flags
#define NMR_FLAG_NORMAL		0
#define NMR_FLAG_GETUSER_FROM_LEGACY	0x01

typedef struct __NMGR_DST_USER_INFO
{
	DWORD size;
	wchar_t wsDstUser[MAX_PATH];	
	wchar_t wsDstPSW [MAX_PATH];	
}MGR_DST_USER_INFO, *PMGR_DST_USER_INFO;


//Function Name: AFMgrDestInit
//				 connect to the d2d destination. Both functions of AFMgrDestInit and AFMgrDestUnInit should be invoked in the same thread.
//Parameters:
//		pwszPath [in] the path of d2d destination
//		pwszUser [in] the user name of destination , the remote destination ,the format should be: (domain name\user name) or (machine name\user name)
//		pwszPSw	 [in] the password for user identified by pwszUser
//		dwCooike [out] to identify the connection , and it is used by function AFMgrDestUnInit to disconnect from d2d destination
//		isConnAllChain	[in] whether to connect all the D2D destinations in current chain, the default value is FALSE
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD WINAPI AFMgrDestInit(const wchar_t * pwszPath, const wchar_t * pwszUser, const wchar_t * pwszPsw, DWORD64 & dwCookie, BOOL isConnChain = FALSE,  PNMGR_EXTEND_OPTION pvExtend = 0);

//////////////////////////////////////////////////////////////////////////
//Function Name: AFMgrDestUnInit
//				 dis-connect from the d2d destination.
//Parameters: 
//		dwCooike [in] this parameter is returned by function AFMgrDestInit to identify the connection. this dwCooike must be returnd by 
//		AFMgrDestInit in the same thread,
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD WINAPI AFMgrDestUnInit(DWORD64 dwCookie);

//////////////////////////////////////////////////////////////////////////
//Function Name: AFVerifyDestUser
//				 check current user whether can connect to Destination folder, Both functions of AFMgrDestInit and AFMgrDestUnInit should be invoked in the same thread.
//Parameters:
//		pwszPath [in] the path of d2d destination
//		pwszUser [in] the user name of destination , the remote destination ,the format should be: (domain name\user name) or (machine name\user name)
//		pwszPSw	 [in] the password for user identified by pwszUser
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD WINAPI AFVerifyDestUser(const wchar_t * pwszPath, const wchar_t * pwszUser, const wchar_t * pwszpPsw);


//////////////////////////////////////////////////////////////////////////
//Function Name: AFVerifyDSDestFreeSpace
//				 check current DS free space is enough for data store. 
//Parameters:
//		pwszPath [in] the path of destination
//		pwszUser [in] the user name of destination , the remote destination ,the format should be: (domain name\user name) or (machine name\user name)
//				 If the path is local, the user and password is ignored.
//		pwszPSw	 [in] the password for user identified by pwszUser
//		dwMinFreeSpaceSize [In] the free space threshold for data store.
//return value: 0, space is enough for the input threshold, otherwise, failed or not enough(1: indicate not enough).
//////////////////////////////////////////////////////////////////////////
DWORD WINAPI AFVerifyDSDestFreeSpace(const wchar_t * pwszPath, const wchar_t * pwszUser, const wchar_t * pwszpPsw, DWORD64 dwMinFreeSpaceSize);

 
//Temp Function, will be removed after Jerry modify the UMDF code<
//////////////////////////////////////////////////////////////////////////
//Function Name: AFMgrDestInitForAFStore
//				 connect to d2d destination chain
//Parameters: 
//		pwszPath [in] the path of d2d destination
//		dwCooike [out] to identify the connection , and it is used by function AFMgrDestUnInitForAFStore to disconnect from d2d destination
//		pDstUser  [in] account information of remote destination.
//		pvD2dUser [in] account information of d2d. for more information ,please check NMGR_USER_INFO. if the parameter is NULL, will use the information saved in the cache.
//		AFMgrDestInit in the same thread,
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD WINAPI AFMgrDestInitForAFStore(const wchar_t * pwszPath, DWORD64 & dwCookie, PMGR_DST_USER_INFO pDstUser = 0, PNMGR_EXTEND_OPTION pvExtend = 0);


//////////////////////////////////////////////////////////////////////////
//Function Name: AFMgrDestUnInitForAFStore
//				 dis-connect from the d2d destination.
//Parameters: 
//		dwCooike [in] this parameter is returned by function AFMgrDestInitForAFStore to identify the connection. this dwCooike must be returnd by 
//		AFMgrDestInitForAFStore in the same thread,
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD WINAPI AFMgrDestUnInitForAFStore(DWORD64 dwCookie);
//Temp Function, will be removed after Jerry modify the UMDF code<


//////////////////////////////////////////////////////////////////////////
//Function Name: AFGetAccountForMntMgr
//				Get account information used to connect to D2D destination. the function only be used by MntMgr, please don't it in other function/feature.
//Parameters: 
//		pwszPath [in] the path of d2d destination
//		info     [out] account information including User Name/password
//		AFMgrDestInit in the same thread,
//return value: 0, success; otherwise, failed.
/////////////////////////////////////////////////////////////////////////
DWORD WINAPI AFGetAccountForMntMgr(const wchar_t * pwszPath, NET_CONN_INFO & info);

//Retrieve master session path according to session number.Similar to AFGetSessPathByNo, using the AFMgrDestInit API to connect the remote path 
//Note that the session may not exist on current destination given by strCurDest.
//@strSessPath: master session path.
//@strDest: backup destination which contains the master session.

DWORD WINAPI ANMGetSessPathByNo(const NET_CONN_INFO &info, int iNum, wstring &strDest, wstring &strSessPath, BOOL bToPurge=FALSE);

/*
*Purpose: Retrieve all backup destinations given current backup destination.Similar to AFGetAllBackupDestinations, using the AFMgrDestInit API to connect the remote path 
*@info: [INPUT] connection info.
*@ppszDestList: [OUTPUT] backup destinations.
*@lpBufLen: [OUTPUT, OPTIONAL]buffer length.
*Return: If function succeeds, it will return zero. If function fails, it will return non-zero error code.
*Note:
     Please remember to free buffer ppszDestList after use. 
     HeapFree(GetProcessHeap(), 0, *ppszDestList).
*/
DWORD WINAPI ANMGetAllBackupDestinations(const NET_CONN_INFO &info, wchar_t **ppszDestList, DWORD *lpBufLen);

//ZZ: [2013/07/02 12:07] Add function to get SID of specified user or current computer.
DWORD WINAPI AFGetUserSID(
    wstring& wsUserSID,                 //ZZ: Receive user SID in text format.
    const WCHAR* pwzUserName = NULL,    //ZZ: Specify user name in form machine\user. if this parameter is NULL, SID of current computer will be returned.
    wstring* pwsSIDAccount = NULL,      //ZZ: Receive actual account name used to acquire SID.
    wstring* pwsDomain4SID = NULL,      //ZZ: Receive domain name of specified user.
    DWORD*   pdwSIDAccountType = NULL,  //ZZ: Receive account type of specified user.
    DWORD    dwHashAlg = EHAT_SHA1      //ZZ: To make sure fixed length of this unique value, we will hash it. Set EHAT_UNKNOWN to retrieve actual SID.
    );

//ZZ: [2013/10/30 14:40]  Add function to get SID of specified user or current computer with more option
DWORD WINAPI AFGetUserSIDEx(
    wstring& wsUserSID,              //ZZ: Receive user SID in text format.
    const WCHAR* pwzUserName = NULL, //ZZ: Account name to query. If this value is NULL or equal to computer name, SID of computer will be returned
    const WCHAR* pwzSysName = NULL,  //ZZ: Remote server name. Only use to query SID on remote server. Should be set as NULL for local system.
    bool bSIDAppendAccount = false,  //ZZ: If this value is true, result SID will be append full account information used to query.
    DWORD dwHashAlg = EHAT_SHA1,     //ZZ: If caller specify hash type, the result will be hashed. Refer to E_HASHALG_TYPE.
    wstring* pwsSIDText = NULL,      //ZZ: Return SID result of specified account without being hashed.
    wstring* pwsSIDAccount = NULL,   //ZZ: Return account information used to query SID.
    wstring* pwsDomain4SID = NULL,   //ZZ: Return domain information of account used to query SID.
    DWORD* pdwSIDAccountType = NULL  //ZZ: Return type of account's SID. Refer to SID_NAME_USE.
    );

//////////////////////////////////////////////////////////////////////////
//Function Name: AFGetNodeID
//				Get the unique ID of current machine. This ID is different from the logon ID
//Parameters: 
//		strNodeID [out] the node ID
//return value: 0, success; otherwise, failed.
DWORD WINAPI AFGetNodeID( wstring& strNodeID );

//////////////////////////////////////////////////////////////////////////
//Function Name: AFGenerateRecoveryPointSyncInfo
//				Generate the recovery point sync information ( which recovery point is updated and which one is deleted )
//Parameters: 
//		strCurDestination [IN]: the current destination
//      strVmUUID [IN]: the VM instance UUID. If this parameter is empty, it will generate sync infor of local host
//		bFullSync [IN]: Full sync or incremental sync. By default it is incremental sync.
//return value: 0, success; otherwise, failed.
DWORD WINAPI AFGenerateRecoveryPointSyncInfo( const wstring& strCurDestination, const wstring& strVmUUID, BOOL bFullSync = FALSE);
//
//wanje04 [2013-08-26] Wrap the API of GetDiskDescriptor which is exported from AFStor
//
DWORD WINAPI AFGetSessionFormatInfo(PNET_CONN_INFO pInfo, DWORD dwSessionNumber, PSESS_FORMAT_DESCRIPTOR pFormatDesc);

#define IS_D2D_MPII_FORMAT(x) ((x == SESS_DISK_FORMAT_SLICE_FILE) || (x == SESS_DISK_FORMAT_DEDUP)) //merge phase 2 format

//
// Enumerate protected nodes under datastore.
//
typedef struct _UDP_NODE_INFO_
{
	wstring		nodeName;		// the node name
	wstring		nodeId;			// the node ID
	wstring		vmInstanceID;	// for hbbu backup, it is the vm instance UUID		
	wstring		mspUser;		// the plan user
	wstring     srcRpsName;		// the source rps server name
	wstring		dstPlanName;	// the destination plan name
	BOOL		bIsVM;			// if this node is a VM
	BOOL		bIntegrity;		// check node integrity
	ULONGLONG	lastBackupTime;	// the last backup time of the node
	ULONGLONG	ullTransferDataSize; // the total transfer data size.
	ULONGLONG   ullDataSize;		 // the total data size
	ULONGLONG	ullCommonPathSize;   // the total common path size
	ULONGLONG	ullCatalogSize;		 // the total catalog size
	ULONGLONG	ullGrtCatalogSize;	 // the total GRT catalog size
}UDP_NODE_INFO, *PUDP_NODE_INFO;

DWORD WINAPI AFEnumNodesUnderDatastore( const wchar_t* pDSPath, std::vector<UDP_NODE_INFO>& vecNodes );

//Serialize and unseralize hyperV vm backup persist information
typedef struct _PERSIST_DISK_INFO
{
	DWORD dwPersistSize;
	DWORD dwSignature;
	ULONGLONG ullSize;
	WCHAR wszFullPath[MAX_PATH];
	ULONG ulStorageType;
	ULONG ulLogicalSectorSize;
	ULONG ulPhysicalSectorSize;
	ULONG ulBlockSize;
	WCHAR wszTopDiskPath[MAX_PATH];
} PERSIST_DISK_INFO;

typedef struct _PERSIST_DISK_INFO_EX
{
	DWORD dwSignature;
	GUID guidUniqueId;
} PERSIST_DISK_INFO_EX;

#define  HYPERV_VDISK_TYPE_DYNAMIC  0
#define  HYPERV_VDISK_TYPE_FIXED    1
#define  HYPERV_VDISK_TYPE_DIFF     2

typedef std::vector<PERSIST_DISK_INFO> PERSIST_DISK_INFO_LIST;

class IHypervPersistInfo
{
public:
	virtual VOID  Release() = 0;
	virtual BOOL  IsDiskSizeEqual(DWORD dwDiskSignature, ULONGLONG ullNewDiskSize) = 0;
	virtual ULONG GetSessionNumber() = 0;
	virtual DWORD FindDiskInfoByDiskSignature(DWORD dwDiskSignature, PERSIST_DISK_INFO& info) = 0;
	virtual DWORD FindDiskExtInfoByDiskSignature(DWORD dwDiskSignature, PERSIST_DISK_INFO_EX& info) = 0;
	virtual DWORD Serialize(LPCTSTR pszSessRoot, ULONG ulSessionNumber) = 0;
	virtual DWORD Serialize(LPCTSTR pszFullPath) = 0;
	virtual DWORD UnSerialize(LPCTSTR pszSessRoot, ULONG ulSessionNumber) = 0;
	virtual DWORD UnSerialize(LPCTSTR pszFullPath) = 0;
	virtual DWORD SetSessionNumber(DWORD dwSessNum) = 0;
	virtual DWORD SetVMRootPath(LPCTSTR pszVMRoot) = 0;
	virtual wstring GetVMRootPath() = 0;
	virtual DWORD AddVMDiskInfo(CONST PERSIST_DISK_INFO* pDiskInfo, CONST PERSIST_DISK_INFO_EX* pDiskInfoEx) = 0;
	virtual CONST PERSIST_DISK_INFO_LIST& GetVMDiskInfoList() = 0;
	virtual DWORD GetVMGeneration() = 0;
	virtual DWORD SetVMGeneration(DWORD dwGeneration) = 0;
	virtual wstring GetHyperVVersion() = 0;
	virtual DWORD SetHyperVVersion(const wstring& strVersion) = 0;
	virtual vector<wstring> GetBootOrder() = 0;
	virtual DWORD SetBootOrder(const vector<wstring>& vecBootOrder) = 0;
	virtual BOOL GetSecurityBoot() = 0;
	virtual DWORD SetSecurityBoot(BOOL bSecurityBoot) = 0;
	virtual void SetClustreVM(BOOL bClusterVM) = 0;
	virtual void SetVMOwner(const wstring& strOwner) = 0;
	virtual BOOL IsClustreVM() = 0;
	virtual wstring GetVMOwner() = 0;
};

DWORD WINAPI AFCreateInstanceHyperVPersistInfo(IHypervPersistInfo** ppHyperVPersist);

BOOL WINAPI AFIsExistIncompleteMergeJob(const wchar_t * pNodeRootPath);

//if Execute Command Line on local server, the lpszHostName should be NULL
DWORD WINAPI AFExecuteCommandLine(LPCTSTR lpszHostName, const wstring& strDomain, const wstring& strUsername, const wstring& strPassword, const wstring& strCommandLine, DWORD& dwCommandRet);
 
#define D2D_OSVER_UNKNOWN			0
#define D2D_OSVER_WORKSTATION		1
#define D2D_OSVER_SBS				2
#define D2D_OSVER_FOUNDATIONS		3
#define D2D_OSVER_STORAGE			4
#define D2D_OSVER_SERVER			5

DWORD WINAPI AFGetOSVersion();
wstring WINAPI AFGetOSName();

//Exchange GRT
typedef struct st_exchange_grt_extend
{
	int nSize;
	BOOL isHBBUSession;
}EXGRT_EXTEND_INFO, *PEXGRT_EXTEND_INFO;

DWORD WINAPI AFSetExGrtExtendInfo(DWORD dwFlags, PEXGRT_EXTEND_INFO pstExtend);
DWORD WINAPI AFGetExGrtExtendInfo(PEXGRT_EXTEND_INFO pstExtend);

#define PORCESS_FLAG_RPS_DEST	0x01
typedef struct st_process_info
{
	int nSize;
	DWORD64 dwFlags;
	DWORD dwJobId;
}D2D_PROCESS_INFO, *PD2D_PROCESS_INFO;

DWORD WINAPI AFSetCurProcessInfo(PD2D_PROCESS_INFO pvProcessInfo);
DWORD WINAPI AFGetCurProcessInfo(PD2D_PROCESS_INFO pvProcessInfo);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

DWORD WINAPI AFGetAFLockListByMode(vector<LCK_ERR_INFO>& vLockLst, const WCHAR* pwzBKDest, AF_LCK_MODE mode);
DWORD WINAPI AFGetAFLockListByOpType(vector<LCK_ERR_INFO>& vLockLst, const WCHAR* pwzBKDest, DWORD opType);


//ZZ: [2014/09/05 17:28] Added for generate temp folder for each job.
//ZZ: dwJobType is the job type defined in AFJob.h
//ZZ: API return zero when temp folder is retrieved successfully, pdwTmpPathLen receives temp path length including 
//ZZ: terminating null character. If pwzD2DTempPath is NULL or buffer len specified by pdwTmpPathLen is not large enough, 
//ZZ: API return D2DCOMM_E_MORE_DATA and pdwTmpPathLen receives temp path length including terminating null character.
DWORD WINAPI AFGetD2DTempPath(WCHAR* pwzD2DTempPath, PDWORD pdwTmpPathLen, DWORD dwJobType);


//
// Get the local path of share folder if share folder resided in local machine.
// !! Remember to free returned buffer after using
WCHAR* WINAPI AFGetLocalPathViaUncPath(LPCWSTR uncPath);

// add user full control of the input file 
// if strUserSid is empty, use strUserName instead.
DWORD WINAPI AFAddUserFullControl(IN const std::wstring& strCfgFile, IN const std::wstring& strUserName, IN const std::wstring& strUserSid);

// get process user from process token
DWORD WINAPI AFGetProcessUser(OUT std::wstring& strUserName, OUT std::wstring& strUserSID);