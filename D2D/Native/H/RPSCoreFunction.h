#pragma once

#include "AFDefine.h"
#include "afjob.h"
#include "MergeJobHistory.h"
#include "MergeJobHistoryRW.h"
#include "RpsRepJobScript.h"
#include "RpsRepJobScript.h"
#include "AFCoreFunction.h"
#include "MergeJobScript.h"
#include "RpsRepJobHistory.h"
#include "DbgLog.h"

#include "..\RPS\RPSCoreFunction\RPSBackupInfoXmlMgrInterface.h" //<sonmi01>2013-4-9 ###???
#include "..\RPS\RPSCoreFunction\AdrCfgXmlReader.h"//<huvfe01>2013-7-3 get disk extents from adr cfg xml file

#include <string>
using namespace std;

DWORD WINAPI AFWriteMergeJobHistoryXml(MergeJobHistoryXml & HistoryXml/*, LPCWSTR szXmlFileName*/);

DWORD WINAPI AFReadMergeJobHistoryXml(LPCWSTR pszXmlFileName, MergeJobHistoryXml & HistoryXml);

DWORD WINAPI RPSWriteRepJobHistoryXml(RepJobHistoryXml & HistoryXml/*, LPCWSTR szXmlFileName*/);

DWORD WINAPI RPSReadRepJobHistoryXml(LPCWSTR pszXmlFileName, RepJobHistoryXml & HistoryXml);

VOID WINAPI RPSRepInitJobScriptToHistoryXml(const RPSRepJobScript & Script, RepJobHistoryXml & RepHistoryXml);

VOID WINAPI RPSRepSetStartTime(RepJobHistoryXml & RepHistoryXml);

VOID WINAPI RPSRepSetEndTimeAndJobStatus(RepJobHistoryXml & RepHistoryXml, LPCTSTR pszJobStatus);

DWORD WINAPI __AFUpdatePurgeJobRecord4Sync(JobIdentify jobId, const wchar_t *uniqueID, long lDataType);

DWORD WINAPI __AFUpdateNewJobRecord4Sync(void* pJobInfo, const wchar_t *uniqueID, long lDataType);

DWORD WINAPI __AFBeforeSessionSync(BOOL bFullSync, std::vector<BACKUP_ITEM_INFOEX>& vInfoEx);

DWORD WINAPI __AFDeleteRPSSessionCacheFile();

DWORD WINAPI RPSGetInstallPath(wchar_t *pInstallPath, DWORD dwLen);

DWORD WINAPI RPSGetAdminAccount(std::wstring &strUser, wstring &strPwd);

DWORD WINAPI RPSGetLatestJobId(DWORD *pId, BOOL bNext);

INT WINAPI RPSGetSessionCount(IAFStorDev* pAFStorDev, PDWORD pdwSessionCount);

INT WINAPI RPSGetSessionNumbers(IAFStorDev* pAFStorDev, DWORD dwSessionCount, DWORD adwNumberArray[]);

DWORD WINAPI RPSGetSessionBackupInfo(LPCWSTR lpRootPath, DWORD dwSessionNum, BackupInfoXml* pstBackupInfo);

DWORD WINAPI GetRPSRegRootPath(wstring &wstr);

//ZZ: APIs used for manage encryption information using key file
//////////////////////////////////////////////////////////////////////////
//ZZ: Save a data buffer into key file. This data will be encrypted by public key, while private key is encrypted by pwzPassword.
long WINAPI RPSKeyFileSaveData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer to be encrypted and saved into key file path.
    DWORD dwDataBufSize,         //ZZ: Specify data buffer size, in bytes.
    const WCHAR* pwzKeyFilePath, //ZZ: Specify a valid and accessible full path which used to store key and data.
    const WCHAR* pwzPassword     //ZZ: Specify a password to encrypt private key. If this parameter is empty. all data is plain text.
    );

//ZZ: Read decrypted data from key file. When input buffer size is not enough or pbDatabuffer is NULL this API will return D2DCRYPTO_E_MORE_DATA.
//ZZ: and dwDataBufSize will receive required buffer size.
long WINAPI RPSKeyFileReadData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer which receives decrypted data. Set NULL to get required size.
    DWORD& dwDataBufSize,        //ZZ: Specify data buffer size and receive decrypted size. Return required size if size is not enough or pbDataBuffer is NULL, 
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read.
    const WCHAR* pwzPassword     //ZZ: Specify password used to decrypt private key.
    );

//ZZ: Replace data saved in current key file. This data will be encrypted using public key stored in key file
long WINAPI RPSKeyFileUpdateData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer to replace the data in key file.
    DWORD dwDataBufSize,         //ZZ: Specify data buffer size.
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read. 
    const WCHAR* pwzPassword     //ZZ: Not used now. should be set as NULL.
    );

//ZZ: Decrypt data stored in key file using original password and encrypt them using new password. If current key file is not encrypted data will be encrypted.
//ZZ: If new password is NULL or empty. the data will be decrypted and saved into key file in plain text format.
long WINAPI RPSKeyFileUpdatePassword(
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read. 
    const WCHAR* pwzNewPassword, //ZZ: Specify new password to encrypt private key. If this parameter is NULL or empty. private key will be plain text.
    const WCHAR* pwzCurPassword  //ZZ: Specify original password to decrypt private key.
    );

//ZZ: Save data store hash key and encrypted by session password
long WINAPI RPSSaveDataStoreHashKey(
    const WCHAR* pwzBKDest,    //ZZ: Backup destination path.
    DWORD        dwSessNum,    //ZZ: Session number.
    const WCHAR* pwzDSHashKey, //ZZ: Data store hash key which used to encrypt session data.
    const WCHAR* pwzPassword   //ZZ: Password to encrypt data store hash key.
    );

//ZZ: Save data store hash key and encrypted by session password
long WINAPI RPSGetDataStoreHashKey(
    wstring&     wsDSHashKey,  //ZZ: Data store hash key.
    const WCHAR* pwzBKDest,    //ZZ: Backup destination path.
    DWORD        dwSessNum,    //ZZ: Session number.
    const WCHAR* pwzSessPwd    //ZZ: Session password to decrypt data store hash key.
    );

//ZZ: Update data store hash key for session
long WINAPI RPSUpdateDataStoreHashKey(
    const WCHAR* pwzBKDest,       //ZZ: Backup destination path.
    DWORD        dwSessNum,       //ZZ: Session number.
    const WCHAR* pwzNewDSHashKey  //ZZ: New data store hash key which used to encrypt session data.
    );

//ZZ: [2013/07/02 12:07] Add function to get SID of specified user or current computer.
DWORD WINAPI RPSGetUserSID(
    wstring& wsUserSID,                 //ZZ: Receive user SID in text format.
    const WCHAR* pwzUserName = NULL,    //ZZ: Specify user name in form machine\user. if this parameter is NULL, SID of current computer will be returned.
    wstring* pwsSIDAccount = NULL,      //ZZ: Receive actual account name used to acquire SID.
    wstring* pwsDomain4SID = NULL,      //ZZ: Receive domain name of specified user.
    DWORD*   pdwSIDAccountType = NULL,  //ZZ: Receive account type of specified user.
    DWORD    dwHashAlg = EHAT_SHA1      //ZZ: To make sure fixed length of this unique value, we will hash it. Set EHAT_UNKNOWN to retrieve actaul SID.
    );

//ZZ: [2013/10/30 14:40]  Add function to get SID of specified user or current computer with more option
DWORD WINAPI RPSGetUserSIDEx(
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

DWORD WINAPI RPSGetNodeID( wstring& strNodeID );

DWORD WINAPI RPSGetAllMappedDrvPath( const std::wstring &strUser, std::vector<MAPPED_DRV_PATH> &vDrvPath );

DWORD WINAPI RPSGetAFLockListByMode(vector<LCK_ERR_INFO>& vLockLst, const WCHAR* pwzBKDest, AF_LCK_MODE mode);
DWORD WINAPI RPSGetAFLockListByOpType(vector<LCK_ERR_INFO>& vLockLst, const WCHAR* pwzBKDest, DWORD opType);

