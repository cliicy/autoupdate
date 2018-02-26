#pragma once

#include <MergeJobHistory.h>
#include "MergeJobScript.h"
#include "RPSCancelJob.h"
#include "JobMonitor.h"
#include "ICryptoWrapperInterface.h"
#include "RPSCOMM\network_throttling.h"

////////////////////////////////////////////////////////////////////////////////////////////////////
//replication job & RPS web service
#define RPS_NODE_UUID_MAX_LENGTH 64
#define RPS_NODE_NAME_MAX_LENGTH 256
#define RPS_CREDENTIAL_MAX_LENGTH	256
#define RPS_REP_EXTENDS_LENGTH 4096

typedef enum enRPSJobCmd
{
	E_RPS_JOB_CMD_RUN  = 0,
	E_RPS_JOB_CMD_STOP,

	E_RPS_JOB_CMD_MAX,
	E_RPS_JOB_CMD_UNDEFINED
}RPS_JOB_CMD_E; 

//please don't change the existing defined code value
typedef enum enRepJobStopCode
{
	E_RC_AUTHENTICATION_FAILED = 0x51,
	E_RC_JOB_COUNT_EXCEED_LIMIT,
	E_RC_JOB_PLAN_IS_PAUSED,
	E_RC_JOB_PLAN_IS_PURGING,
	E_RC_JOB_IN_DUP,
	E_RC_TIMEOUT_EXPIRED = 0x60
}REP_JOB_STOP_REASON_CODE_E;

#define REP_JOB_CMD_WAIT_DEFAULT_TIMEOUT (10 * 60 * 1000)
#define REP_JOB_CMD_SEND_DEFAULT_TIMEOUT (9 * 60 * 1000)

#pragma pack(push, 1)
typedef struct tagRPSJobInfo
{
	DWORD dwJobId;
	DWORD dwTargetJobType; //support seeding
	DWORD dwProcessId;
	WCHAR wszSrcRPSGuid[RPS_NODE_UUID_MAX_LENGTH];
	WCHAR wszD2DNodeGuid[RPS_NODE_UUID_MAX_LENGTH];
	WCHAR wszD2DNodeName[RPS_NODE_NAME_MAX_LENGTH];
	WCHAR wszSrcPlanUUID[RPS_NODE_UUID_MAX_LENGTH];
	WCHAR wszTargetPlanUUID[RPS_NODE_UUID_MAX_LENGTH];
	BOOL  bMspUser;
	WCHAR wszUserName[RPS_CREDENTIAL_MAX_LENGTH];
	WCHAR wszPassword[RPS_CREDENTIAL_MAX_LENGTH];
	WCHAR wszDomain[RPS_CREDENTIAL_MAX_LENGTH];
	WCHAR wszRepExtends[RPS_REP_EXTENDS_LENGTH];
	AFARCHIVE_INFO stArchiveSchInfo;
}RPS_JOB_INFO_S;

typedef struct tagRepJobCmdParam
{
	DWORD dwJobCmd; //RPS_JOB_CMD_E
	DWORD dwReserved;
	DWORD dwHasError; //1 has error
	DWORD dwErrorCode; //error code
	AFARCHIVE_INFO stArchiveSchInfo;
}REP_JOB_CMD_PARAM_S;

#pragma pack(pop)
///////////////////////////////////////////////////////////////////////////////////////////////////

typedef DWORD (WINAPI *pfnUserCallProc)( LPVOID lpParameter);

DWORD WINAPI AFInterMerge(IN MergeJobScript *pMergeJobScript, IN pfnUserCallProc UserCallBack, IN LPVOID lpParameter);

DWORD WINAPI AFRPSRepJob(IN RPSRepJobScript *pRPSRepJobScript, IN pfnUserCallProc UserCallBack, IN LPVOID lpParameter);

DWORD WINAPI AFRPSSaveRepJobInfo2Context(IN const RPS_JOB_INFO_S& stRPSJobInfo);

DWORD WINAPI AFRPSRemoveRepJobContext(DWORD dwJobId);

DWORD WINAPI AFRPSRepSaveMakeupJobScript(const RPSRepJobScript* pJs);

DWORD WINAPI AFInterMergeSaveMakeupJobScript(const MergeJobScript* pJs);

int WINAPI AFRPSMergeConvertXmlToJobscript(const wchar_t* szXML, MergeJobScript ** ppJobScript, void*& jsHandle);

int WINAPI AFRPSRepConvertXmlToJobscript(const wchar_t* szXML, RPSRepJobScript ** ppJobScript, void*& jsHandle);

DWORD WINAPI RPSGetSyncLog(wstring strFolderName, wstring& strFileContent);

BOOL WINAPI CreateRPSSynLogFolder(wstring D2DName, DWORD jobID, DWORD d2dJobID);

BOOL WINAPI StopSyncLog(HANDLE hEvent);

DWORD WINAPI SaveLogFromRPS(wstring strBuffer, wstring szVMUUID);

DWORD WINAPI GetRPSFolderNameList(std::vector<std::wstring>& vFolderList);

DWORD WINAPI GetRPSLogFolderStatus(wstring folderName);

DWORD WINAPI UpdateRPSFolder(wstring folderName, DWORD dwStatus);

HANDLE WINAPI StartSyncLog(DWORD jobID);

BOOL WINAPI SetRPSLogFolderStatus(wstring folderName, DWORD dwAttribute);

DWORD WINAPI SaveJobHistoryFromRPS(const wstring &strSessGuid, BYTE* buffer, int len, const wchar_t* pSubFolder);

BOOL WINAPI IsHistoryFileValid(const wstring& strFile, long retention);

BOOL WINAPI CreateRPSLogFile(DWORD jobID, wstring policy);

LONG WINAPI AFRPSPurgeLog();

LONG WINAPI AFRegistryGetIntValue(WCHAR *subkey, WCHAR *valname);

BOOL WINAPI DeleteRPSLogFile(DWORD jobID);

HANDLE GetControlRPSJobHandle(DWORD dwFlag, BOOL bAsync, JobFilter filter, JNIEnv* env, ILogger* pLogger);

DWORD ControlRPSJobs(HANDLE handle);

void FreeControlRPSJobHandle(HANDLE handle);

DWORD GetControlRPSJobStatus(HANDLE handle);

BOOL WINAPI RPSEncryptString(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen);

BOOL WINAPI RPSDecryptString(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen);

BOOL WINAPI RPSGetJobId(DWORD *pId);

DWORD WINAPI CreateRPSJobMonitor(DWORD dwShrMemID, IJobMonitor **ppIJobMonitor);

VOID WINAPI DestroyRPSJobMonitor(IJobMonitor **ppIJobMonitor);

/* BEGIN: Added by huvfe01, 2013/3/15   PN:ARCserve Oolong Job Monitor and Cancel Job */
VOID* WINAPI RPSCreateClientJobSharedQueue();

VOID WINAPI RPSDestroyClientJobSharedQueue(IN VOID* pSharedJobQueue);

BOOL WINAPI RPSNotifyNewClientJobArrival(IN CONST RPS_JOB_INFO_S* pstJobInfo);

BOOL WINAPI RPSWaitForNewClientJobArrival(IN VOID* pSharedJobQueue, OUT RPS_JOB_INFO_S* pstJobInfo);

//web service should send job cmd on job arrival
BOOL WINAPI RPSSendClientJobRunCmdOnJobArrival(const RPS_JOB_INFO_S* pstJobInfo);

BOOL WINAPI RPSSendClientJobStopCmdOnJobArrival(const RPS_JOB_INFO_S* pstJobInfo, DWORD dwStopReasonCode);

//after replication (In) back stage notify job arrival, will wait for job cmd from web service. dwMilliseconds is the timeout
DWORD WINAPI RPSWaitForClientJobCmdOnJobStart(const RPS_JOB_INFO_S* pstJobInfo, DWORD dwMilliseconds, OUT REP_JOB_CMD_PARAM_S* pstCmd);
/* END:   Added by huvfe01, 2013/3/15 */

//ZZ: APIs used for manage encryption information using key file
//////////////////////////////////////////////////////////////////////////
//ZZ: Save a data buffer into key file. This data will be encrypted by public key, while private key is encrypted by pwzPassword.
long WINAPI RPSIKeyFileSaveData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer to be encrypted and saved into key file path.
    DWORD dwDataBufSize,         //ZZ: Specify data buffer size, in bytes.
    const WCHAR* pwzKeyFilePath, //ZZ: Specify a valid and accessible full path which used to store key and data.
    const WCHAR* pwzPassword     //ZZ: Specify a password to encrypt private key. If this parameter is empty. all data is plain text.
    );

//ZZ: Read decrypted data from key file. When input buffer size is not enough or pbDatabuffer is NULL this API will return D2DCRYPTO_E_MORE_DATA.
//ZZ: and dwDataBufSize will receive required buffer size.
long WINAPI RPSIKeyFileReadData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer which receives decrypted data. Set NULL to get required size.
    DWORD& dwDataBufSize,        //ZZ: Specify data buffer size and receive decrypted size. Return required size if size is not enough or pbDataBuffer is NULL, 
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read.
    const WCHAR* pwzPassword     //ZZ: Specify password used to decrypt private key.
    );

//ZZ: Replace data saved in current key file. This data will be encrypted using public key stored in key file
long WINAPI RPSIKeyFileUpdateData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer to replace the data in key file.
    DWORD dwDataBufSize,         //ZZ: Specify data buffer size.
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read. 
    const WCHAR* pwzPassword     //ZZ: Not used now. should be set as NULL.
    );

//ZZ: Decrypt data stored in key file using original password and encrypt them using new password. If current key file is not encrypted data will be encrypted.
//ZZ: If new password is NULL or empty. the data will be decrypted and saved into key file in plain text format.
long WINAPI RPSIKeyFileUpdatePassword(
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read. 
    const WCHAR* pwzNewPassword, //ZZ: Specify new password to encrypt private key. If this parameter is NULL or empty. private key will be plain text.
    const WCHAR* pwzCurPassword  //ZZ: Specify original password to decrypt private key.
    );

//ZZ: Save data store hash key and encrypted by session password
long WINAPI RPSISaveDataStoreHashKey(
    const WCHAR* pwzBKDest,    //ZZ: Backup destination path.
    DWORD        dwSessNum,    //ZZ: Session number.
    const WCHAR* pwzDSHashKey, //ZZ: Data store hash key which used to encrypt session data.
    const WCHAR* pwzPassword   //ZZ: Password to encrypt data store hash key.
    );

//ZZ: Save data store hash key and encrypted by session password
long WINAPI RPSIGetDataStoreHashKey(
    wstring&     wsDSHashKey,  //ZZ: Data store hash key.
    const WCHAR* pwzBKDest,    //ZZ: Backup destination path.
    DWORD        dwSessNum,    //ZZ: Session number.
    const WCHAR* pwzSessPwd    //ZZ: Session password to decrypt data store hash key.
    );

//ZZ: Update data store hash key for session
long WINAPI RPSIUpdateDataStoreHashKey(
    const WCHAR* pwzBKDest,       //ZZ: Backup destination path.
    DWORD        dwSessNum,       //ZZ: Session number.
    const WCHAR* pwzNewDSHashKey  //ZZ: New data store hash key which used to encrypt session data.
    );

DWORD WINAPI RPSCancelJob(DWORD dwJobId, PWCHAR pwszNodeName=NULL);


// web service use this API to check if there are new sessions to replicate
// Note: it is for checking purpose only before submitting new job, please do not rely on it to perform real replication
BOOL WINAPI CheckNewSessionsToReplicate(const wchar_t * pSessionRootPath, const wchar_t* pUser, const wchar_t* pPass);

DWORD WINAPI RPSRetrieveActiveJobs(std::vector<JOB_CONTEXT> &vecActiveJobs);

///ZZ: Added for exporting some API to operate merge job.
IJobMonInterface* WINAPI RPSICreateMergeJM(
    DWORD dwJobID,
    DWORD* pdwErrCode = NULL
    );

void WINAPI RPSIReleaseMergeJM(
    IJobMonInterface** ppJobMonMgr
    );

long WINAPI RPSISaveMergeJS(
    CMergeJS& MergeJS, 
    const WCHAR* pwzJSPath
    );

long WINAPI RPSIStopMergeJob(
    DWORD dwJobID
    );

long WINAPI RPSIStartJob(
    const WCHAR* pwzJSPath, 
    DWORD* pdwProcID = NULL, 
    const WCHAR* pwzUsrName = NULL,
    const WCHAR* pwzPassword = NULL,
    DWORD dwJobType = EJT_MERGE
    );

long WINAPI RPSIIsMergeJobAvailable(
    DWORD dwRetentionCnt, 
    const WCHAR* pwzBKDest, 
    const WCHAR* pwzVMGUID = NULL, 
    const WCHAR* pwzBKUsr = NULL, 
    const WCHAR* pwzBKPwd = NULL,
    const WCHAR* pwzDS4Replication = NULL
    );

long WINAPI RPSIIsMergeJobAvailableEx(
    const WCHAR* pwzBKDest, 
    DWORD dwRetentionCnt, 
    DWORD dwDailyCnt = 0,
    DWORD dwWeeklyCnt = 0,
    DWORD dwMonthlyCnt = 0,
    const WCHAR* pwzVMGUID = NULL, 
    const WCHAR* pwzDS4Replication = NULL,
    const WCHAR* pwzBKUsr = NULL, 
    const WCHAR* pwzBKPwd = NULL,
	DWORD	  dwArchiveDays = 0,		 // [8/28/2014 zhahu03] archive to tape execute day[bit0~bit6:sun~sat]
	DWORD	  dwArchiveTypeFlag = 0,	 // [8/28/2014 zhahu03] archived task enable flag.[Daily/Weekly/Monthly/Custom]
	LONGLONG  llArchiveScheduleTime = 0  // [8/28/2014 zhahu03] archived task schedule applied time)
    );

long WINAPI RPSIRetrieveMergeJM(
    ActJobVector& vecActiveJob
    );

long WINAPI RPSIGetSessNumListForNextMerge(
    DWORD dwRetentionCnt, 
    const WCHAR* pwzBKDest, 
    vector<DWORD>& vecSessNumList, 
    const WCHAR* pwzBKUsr = NULL, 
    const WCHAR* pwzBKPwd= NULL
    );

//ZZ: [2013/07/02 12:07] Add function to get SID of specified user or current computer.
DWORD WINAPI RPSIGetUserSID(
    wstring& wsUserSID,                 //ZZ: Receive user SID in text format.
    const WCHAR* pwzUserName = NULL,    //ZZ: Specify user name in form machine\user. if this parameter is NULL, SID of current computer will be returned.
    wstring* pwsSIDAccount = NULL,      //ZZ: Receive actual account name used to acquire SID.
    wstring* pwsDomain4SID = NULL,      //ZZ: Receive domain name of specified user.
    DWORD*   pdwSIDAccountType = NULL,  //ZZ: Receive account type of specified user.
    DWORD    dwHashAlg = EHAT_SHA1      //ZZ: To make sure fixed length of this unique value, we will hash it. Set EHAT_UNKNOWN to retrieve actaul SID.
    );

//ZZ: [2013/10/30 14:40]  Add function to get SID of specified user or current computer with more option
DWORD WINAPI RPSIGetUserSIDEx(
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

DWORD WINAPI RPSIGetNodeID( wstring& strNodeID );

DWORD WINAPI RPSStartCatalogGenerator(
	DWORD        dwQueueType,           // Job in which queue will be run. 1 = regular, 2 = ondemand
	DWORD        dwJobNum,              // Job number for job monitor.
	DWORD*       pdwProcID = NULL,      // Return the new process if needed.
	const WCHAR* pwzUsrName = NULL,     // User name for security requirement when start process.
	const WCHAR* pwzPassword = NULL,    // Password for security requirement when start process.
	const WCHAR* pwzJobQIdentity = NULL,  // Job queue name, Empty for local D2D, VM GUID for vsphere.
	const NET_CONN_INFO* stBKDest = NULL, //// Backup destination information, including account information for remote folder.
	const WCHAR* pwzCatalogModeID = NULL);

DWORD WINAPI RPSSaveJS4FSOndemand(
	const NET_CONN_INFO& stBKDest, 
	DWORD dwSessNum, 
	const WCHAR* pwzJobQIdentity, 
	DWORD dwSubSessNum, 
	const WCHAR* pwzSessPWD );

DWORD WINAPI RPSMoveJobScript(
	DWORD dwJobQType,    //ZZ: Job queue type, 1:regular queue, 2:ondemand queue, 3:makeup queue. You can always specify 1 so far
	const WCHAR* wsBKDest,		//ZZ: Backup destination. Connection should be created before call this API
	DWORD dwSessNum,			//ZZ: Session number.
	const WCHAR* wsJobQID,		//ZZ: It should be VM GUID for HBBU catalog, while it should be D2D SID or similar identity for D2D on RPS.
	const WCHAR* pwzCatalogNodeID);

DWORD WINAPI RPSIQueryJobQueue(
	DWORD eJobQType,						 //ZZ: Specify job queue type. Refer to E_QUEUE_TYPE(Regular, On-Demand, and Makeup)
	const WCHAR* pwzJobQIdentity = NULL,     //ZZ: Job queue identity. VM GUID for HBBU or computer identity for catalog running on RPS.
	wstring*     pwsJobQPath = NULL,         //ZZ: Return job queue path which contain job scripts. Ignore this parameter when it is NULL.
	WSVector*    pvecJobScriptList = NULL,   //ZZ: Return job script list under job queue. Ignore this parameter when it is NULL.
	bool bCreateJobQFolder = false,			 //ZZ: If create job queue folder when it doesn't exist.
	const WCHAR* pwzCatalogModeID = NULL);   //ZZ: Server identity where catalog should be launched. If this parameter is empty, it will be ignored.

long WINAPI RPSIsCatalogAvailable(
	DWORD dwQueueType, 
	const WCHAR* pwzJobQIdentity = NULL, 
	const WCHAR* pwzRPSSvrIdentity = NULL);

int WINAPI RPSNTP_AddPolicy(PNETWORK_THROTTLING_POLICY pPolicy);

int WINAPI RPSNTP_UpdatePolicy(PNETWORK_THROTTLING_POLICY pPolicy);

int WINAPI RPSNTP_RemovePolicy(GUID* pGuid);

DWORD WINAPI RPSIGetAllMappedDrvPath(const std::wstring &strUser, std::vector<MAPPED_DRV_PATH> &vDrvPath);

int WINAPI RPSGetCatalogStatus(LPWSTR sessionPath);

DWORD WINAPI AFIGetCatalogStatus(
	const WCHAR*       pwzBKDest,
	PST_CATALOG_STATUS pstCatalogStatusList,
	DWORD              dwCatalogStatus,
	const WCHAR*       pwzJobQIdentity);

DWORD WINAPI RPSAFIEnumBackupDestinations( const NET_CONN_INFO &info, std::vector<wstring>& vecBackupDestinations );

DWORD WINAPI RPSAFGetAFLockListByOpType(vector<LCK_ERR_INFO>& vLockLst, const WCHAR* pwzBKDest, DWORD opType);

BOOL WINAPI RPSAFIsPathUnderDatastore( const wstring& strPath);
DWORD WINAPI RPS_AFArchiveJob(IN PAFARCHIVEJOBSCRIPT pAFJOBSCRIPT, IN pfnUserCallProc UserCallBack, IN LPVOID lpParameter);
DWORD WINAPI RPS_CancelArchiveJob(DWORD dwJobType, DWORD dwJobId, IAFJobMonitor * pAFJobMonitor);

DWORD WINAPI RPSAFEnumNodesUnderDatastore( const wchar_t* pDSPath, std::vector<UDP_NODE_INFO>& vecNodes );