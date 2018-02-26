#include "stdafx.h"
#include "utils.h"
#include "AFCoreAPIInterface.h"
#include "AFCoreFunction.h"
#include <jni.h>

#define TRY __try{
#define CATCH(procName) }__except(HandleSEH(L"AFCoreInterface.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GET_PROC_ADDRESS(procName) DynGetProcAddress(L"AFCoreInterface.dll", procName)
#define CATCH_CORE_FUNC(procName) }__except(HandleSEH(L"AFCoreFunction.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GET_PROC_ADDRESS_CORE_FUNC(procName) DynGetProcAddress(L"AFCoreFunction.dll", procName)

DWORD WINAPI AFBackup(PAFJOBSCRIPT pAFJOBSCRIPT,
					  pfnUserCallProc UserCallBack,
					  LPVOID lpParameter)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(PAFJOBSCRIPT, pfnUserCallProc, LPVOID);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFBackup");

	return pfun(pAFJOBSCRIPT, UserCallBack, lpParameter);
	GET_PROC_ADDRESS("AFBackup");
	CATCH("AFBackup")
}

DWORD WINAPI AFRestore(PAFJOBSCRIPT pAFJOBSCRIPT,
					   pfnUserCallProc UserCallBack,
					   LPVOID lpParameter)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(PAFJOBSCRIPT, pfnUserCallProc, LPVOID);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFRestore");

	return pfun(pAFJOBSCRIPT, UserCallBack, lpParameter);
	CATCH("AFRestore")
}

DWORD WINAPI AFRunPatchJob(PTCHAR szCommandLine)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(PTCHAR);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFRunPatchJob");

	return pfun(szCommandLine);
	CATCH("AFRunPatchJob")
}
DWORD WINAPI AFCopy(PAFJOBSCRIPT pAFJOBSCRIPT,
					pfnUserCallProc UserCallBack,
					LPVOID lpParameter)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(PAFJOBSCRIPT, pfnUserCallProc, LPVOID);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFCopy");

	return pfun(pAFJOBSCRIPT, UserCallBack, lpParameter);
	CATCH("AFCopy")
}

DWORD WINAPI AFSetJobStatus(IN PJobStatus pJobStatus)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(PJobStatus);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFSetJobStatus");;

	return pfun(pJobStatus);
	CATCH("AFSetJobStatus")
}

DWORD WINAPI AFGetJobStatus(OUT PJobStatus pJobStatus)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(PJobStatus);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetJobStatus");

	return pfun(pJobStatus);
	CATCH("AFGetJobStatus")
}

///<ZZ[zhoyu03: 2009/06/24]: This interface has 2 ways to gather application information..
// 1. Save application information as XML file using caller specifying name. In this case, bSaveAsFile should be 
//    set as true. ppBrowseInfo indicates file name to be used, while pdwBrowseInfoSize indicates character number
//    of file name 
// 2. Return XML file content string to caller. In this case, bSaveAsFile should be set as false. ppBrowseInfo should
//    be a input parameter, and it will be allocated enough memory for XML content. The XML content length will also be
//    returned in pdwBrowseInfoSize. After using, make sure calling ReleaseBrowseInformation to free its memory.
DWORD WINAPI BrowseAppInforamtion(IN OUT LPWSTR* ppBrowseInfo, IN OUT DWORD* pdwBrowseInfoSize, BOOL bSaveAsFile)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(LPWSTR*, DWORD*, BOOL);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("BrowseAppInforamtion");

	return pfun(ppBrowseInfo, pdwBrowseInfoSize, bSaveAsFile);
	CATCH("BrowseAppInforamtion")
}

DWORD WINAPI BrowseVolumeInforamtion(IN OUT LPWSTR* ppBrowseInfo, IN OUT DWORD* pdwBrowseInfoSize, BOOL bSaveAsFile)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(LPWSTR*, DWORD*, BOOL);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("BrowseVolumeInforamtion");

	return pfun(ppBrowseInfo, pdwBrowseInfoSize, bSaveAsFile);
	CATCH("BrowseVolumeInforamtion")
}

DWORD WINAPI ReleaseBrowseInformation(IN LPWSTR* ppBrowseInfo)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(LPWSTR*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("ReleaseBrowseInformation");

	return pfun(ppBrowseInfo);
	CATCH("ReleaseBrowseInformation")
}


DWORD CreateIFileListHandler(IFileListHandler **ppIFileList)
{
	TRY
	typedef DWORD (* LPFUN)(IFileListHandler **);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("CreateIFileListHandler");

	return pfun(ppIFileList);
	CATCH("CreateIFileListHandler")
}

/*
*@strDestPath: [input] Backup destination path.
*@ppIRestorePoint: [output] restore point interface handler.
*Return: If function succeeds, 0 will be resturned, other wise, system error code will be returned.
*/
DWORD WINAPI CreateIRestorePoint(const NET_CONN_INFO &info, IRestorePoint **ppIRestorePoint)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &, IRestorePoint **);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("CreateIRestorePoint");

	return pfun(info, ppIRestorePoint);
	CATCH("CreateIRestorePoint")
}



DWORD WINAPI CreateIBackupSumm(const NET_CONN_INFO &info, IBackupSumm **ppIBackupSumm)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &, IBackupSumm **);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("CreateIBackupSumm");

	return pfun(info, ppIBackupSumm);
	CATCH("CreateIBackupSumm")
}

/*
*Purpose: Initialize and valid backup destination.
*@pNewDest: current backup destination which user specified.
*@pOldDest: the last backup destination.
*Return: If function succeeds, 0 will be returned. otherwise, system error code will be returned.
*/
DWORD WINAPI AFInitBackupDestination(const NET_CONN_INFO *pNewDest, const NET_CONN_INFO *pOldDest, DWORD dwBakType)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO *, const NET_CONN_INFO *, DWORD);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFInitBackupDestination");

	return pfun(pNewDest, pOldDest, dwBakType);
	CATCH("AFInitBackupDestination")
}

DWORD WINAPI AFCheckFolderAccess(const NET_CONN_INFO *pDest, FILE_INFO &info)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO *, const FILE_INFO&);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFCheckFolderAccess");

	return pfun(pDest, info);
	CATCH("AFCheckFolderAccess")
}

DWORD WINAPI AFRetrieveSharedResource(const NET_CONN_INFO *pDest, std::vector<std::wstring> &vShare)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO *, std::vector<std::wstring> &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFRetrieveSharedResource");

	return pfun(pDest, vShare);
	CATCH("AFRetrieveSharedResource")
}

DWORD WINAPI AFGetDestinationVolumeType(const wchar_t *pszDest, BAK_DEST_VOL_TYPE &type)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const wchar_t *, BAK_DEST_VOL_TYPE &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetDestinationVolumeType");

	return pfun(pszDest, type);
	CATCH("AFGetDestinationVolumeType")
}

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

BOOL WINAPI AFEncryptString(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(const wchar_t *, wchar_t *, DWORD*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFEncryptString");

	return pfun(pszStr, pszBuf, pBufLen);
	CATCH("AFEncryptString")
}

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
BOOL WINAPI AFDecryptString(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(const wchar_t *, wchar_t *, DWORD*);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFDecryptString");

	return pfun(pszStr, pszBuf, pBufLen);
	CATCH("AFDecryptString")
}

BOOL WINAPI AFCheckCompressLevelChanged(const NET_CONN_INFO *pInfo, int iLevel)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(const NET_CONN_INFO *, int);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFCheckCompressLevelChanged");;

	return pfun(pInfo, iLevel);
	CATCH("AFCheckCompressLevelChanged")
}

BOOL WINAPI AFCheckVhdFormatChanged(const NET_CONN_INFO *pInfo, int iLevel)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(const NET_CONN_INFO *, int);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFCheckVhdFormatChanged") ;

	return pfun(pInfo, iLevel);
	CATCH("AFCheckVhdFormatChanged")
}

BOOL WINAPI AFGetJobId(DWORD *pId, DWORD dwCount)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(DWORD*, DWORD);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetJobId");

	return pfun(pId, dwCount);
	CATCH("AFGetJobId")
}

BOOL WINAPI AFGetErrorMsg(DWORD dwErr, std::wstring &strMsg)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(DWORD, std::wstring &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetErrorMsg");

	return pfun(dwErr, strMsg);
	CATCH("AFGetErrorMsg")
}

BOOL WINAPI AFCheckJobExist()
{
	TRY
	typedef BOOL (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFCheckJobExist");

	return pfun();
	CATCH("AFCheckJobExist")
}

BOOL WINAPI AFCheckBMRPerformed()
{
	TRY
	typedef BOOL (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFCheckBMRPerformed");

	return pfun();
	CATCH("AFCheckBMRPerformed")
}

//Return: If there is no job running, 0 will be returned.
//        else non-zero value will be returned.
DWORD WINAPI AFGetCurrentJobId()
{
	TRY
	typedef DWORD (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFGetCurrentJobId" );

	return pfun();
	CATCH("AFGetCurrentJobId")
}

/************************Retrieve subsession size. Used for Jie**************************/
BOOL WINAPI AFGetSubSessSize(const wchar_t *pDest, DWORD dwSessNo, DWORD dwSubSessNo, unsigned long long *pSubSessSize)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(const wchar_t *, DWORD, DWORD, unsigned long long *);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFGetSubSessSize" );

	return pfun(pDest, dwSessNo, dwSubSessNo, pSubSessSize);
	CATCH("AFGetSubSessSize")
}

//convert dos time to UTC time(1970, 0:0:0)
BOOL WINAPI AFConvertDosTimeToUTC(long *pTime)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(long *);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFConvertDosTimeToUTC" );

	return pfun(pTime);
	CATCH("AFConvertDosTimeToUTC")
}

//Cancel job
DWORD WINAPI AFCancelJob(DWORD dwJobId)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(DWORD);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCancelJob" );

	return pfun(dwJobId);
	CATCH("AFCancelJob")
}

DWORD WINAPI AFUpdateThrottling(DWORD dwJobId, PWCHAR pwszNodeName, DWORD dwThrottling)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(DWORD, PWCHAR, DWORD);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFUpdateThrottling" );

	return pfun(dwJobId, pwszNodeName, dwThrottling);
	CATCH("AFUpdateThrottling")
}

DWORD WINAPI CreateIJobMonitor(DWORD dwShrMemID, IAFJobMonitor **ppIAFJobMonitor)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(DWORD, IAFJobMonitor **);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "CreateIJobMonitor" );

	return pfun(dwShrMemID, ppIAFJobMonitor);
	CATCH("CreateIJobMonitor")
}

VOID WINAPI DestroyIJobMonitor(IAFJobMonitor **ppIAFJobMonitor)
{
	TRY
	typedef VOID (WINAPI* LPFUN)(IAFJobMonitor **);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "DestroyIJobMonitor" );

	return pfun(ppIAFJobMonitor);
	CATCH("DestroyIJobMonitor")
}


DWORD WINAPI AFGetSQLInstance(std::vector<SQL_INSTANCE_INFO> &vList)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(std::vector<SQL_INSTANCE_INFO> &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFGetSQLInstance" );

	return pfun(vList);
	CATCH("AFGetSQLInstance")
}

void WINAPI AFGetVSpherePathMaxLength(DWORD *pLen)
{
	TRY
	typedef void (WINAPI* LPFUN)(DWORD *);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFGetVSpherePathMaxLength" );

	return pfun(pLen);
	CATCH("AFGetVSpherePathMaxLength")
}

void WINAPI AFGetPathMaxLength(DWORD *pLen)
{
	TRY
	typedef void (WINAPI* LPFUN)(DWORD *);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFGetPathMaxLength" );

	return pfun(pLen);
	CATCH("AFGetPathMaxLength")
}

DWORD WINAPI AFCheckDestValid(const wchar_t *pDir)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const wchar_t *);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCheckDestValid" );

	return pfun(pDir);
	CATCH("AFCheckDestValid")
}

DWORD WINAPI AFCheckDestContainRecoverPoint(const NET_CONN_INFO &info)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCheckDestContainRecoverPoint" );

	return pfun(info);
	CATCH("AFCheckDestContainRecoverPoint")
}

DWORD WINAPI AFCheckDestNeedHostName(const std::wstring &strPath, std::wstring &strHostName, const wchar_t* uuid, const wchar_t* nodeID, BOOL bCreateFolder)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const std::wstring &, std::wstring &, const wchar_t* uuid, const wchar_t* nodeID, BOOL bCreateFolder);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCheckDestNeedHostName" );

	return pfun(strPath, strHostName, uuid, nodeID, bCreateFolder);
	CATCH("AFCheckDestNeedHostName")
}

DWORD WINAPI AFInitDestForCopySes(const NET_CONN_INFO &info, const std::wstring &strHostName, const std::wstring &strDateTime, NET_CONN_INFO &dest)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &, const std::wstring &, const std::wstring &, NET_CONN_INFO &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFInitDestForCopySes" );

	return pfun(info, strHostName, strDateTime, dest);
	CATCH("AFInitDestForCopySes")
}

DWORD WINAPI AFSaveAdminAccount(const std::wstring &strUser, const std::wstring &strPwd)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const std::wstring &, const std::wstring &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFSaveAdminAccount" );

	return pfun(strUser, strPwd);
	CATCH("AFSaveAdminAccount")
}

DWORD WINAPI AFReadAdminAccount(std::wstring &strUser, std::wstring &strPwd)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(std::wstring &, std::wstring &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFReadAdminAccount" );

	return pfun(strUser, strPwd);
	CATCH("AFReadAdminAccount")
}

DWORD WINAPI AFCheckAdminAccountValid(const std::wstring &strUser, const std::wstring &strPwd)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const std::wstring &, const std::wstring &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCheckAdminAccountValid" );

	return pfun(strUser, strPwd);
	CATCH("AFCheckAdminAccountValid")
}

DWORD WINAPI AFInitDestination( const NET_CONN_INFO *pNewDest, const NET_CONN_INFO *pOldDest /*= NULL*/, DWORD dwBakType /*= 1*/, BOOL bCopy /*= FALSE*/ )
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO *, const NET_CONN_INFO *, DWORD, BOOL);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFInitDestination" );

	return pfun(pNewDest, pOldDest, dwBakType, bCopy);
	CATCH("AFInitDestination")
}

DWORD WINAPI AFGetBakDestDriveType( const NET_CONN_INFO &info, DWORD &dwType )
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &, DWORD&);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFGetBakDestDriveType" );

	return pfun(info, dwType);
	CATCH("AFGetBakDestDriveType")
}

DWORD WINAPI AFCreateConnection( const NET_CONN_INFO &info )
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCreateConnection" );

	return pfun(info);
	CATCH("AFCreateConnection")
}

DWORD WINAPI AFCreateDir( const std::wstring &strParent, const std::wstring &strSubFolder )
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const std::wstring &, const std::wstring &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCreateDir" );

	return pfun(strParent, strSubFolder);
	CATCH("AFCreateDir")
}

BOOL WINAPI AFCheckPathAccess( const NET_CONN_INFO &info )
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(const NET_CONN_INFO &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCheckPathAccess" );

	return pfun(info);
	CATCH("AFCheckPathAccess")
}

DWORD WINAPI AFGetThreshold( unsigned long long &ulThreshold )
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(unsigned long long &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFGetThreshold" );

	return pfun(ulThreshold);
	CATCH("AFGetThreshold")
}

DWORD WINAPI AFTryToFindDest( const std::wstring &strOri, std::wstring &strFindDest )
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const std::wstring &, std::wstring &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFTryToFindDest" );

	return pfun(strOri, strFindDest);
	CATCH("AFTryToFindDest")
}

long WINAPI AFCheckSQLAlternateLocation( const WCHAR* pwzDstBasePath, const WCHAR* pwzInstanceName, const WCHAR* pwzDatabaseName, std::wstring& wsAlterDstPath )
{
	TRY
	typedef long (WINAPI* LPFUN)(const WCHAR*, const WCHAR*, const WCHAR*, std::wstring&);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCheckSQLAlternateLocation" );

	return pfun(pwzDstBasePath, pwzInstanceName, pwzDatabaseName, wsAlterDstPath);
	CATCH("AFCheckSQLAlternateLocation")
}

BOOL WINAPI AFCheckBLILic()
{
	TRY
	typedef BOOL (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCheckBLILic" );

	return pfun();
	CATCH("AFCheckBLILic")
}

BOOL WINAPI AFCheckBaseLic()
{
	TRY
	typedef BOOL (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCheckBaseLic" );

	return pfun();
	CATCH("AFCheckBaseLic")
}

//[li$fe01] wrapper due to __try need object not be unwinded.
long WINAPI GatherExcludedFileListInWriterWithOutUnwinded( std::vector<std::wstring>& vecVolumeList, ExcludedWriterVector& vecExcludedItemList )
{
	typedef long (WINAPI* LPFUN)(std::vector<std::wstring>&, ExcludedWriterVector&);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "GatherExcludedFileListInWriter" );
    if (NULL == pfun)
        return E_INVALIDARG;
	return pfun(vecVolumeList, vecExcludedItemList);	
}

long WINAPI GatherExcludedFileListInWriter( std::vector<std::wstring> &vecVolumeList, ExcludedWriterVector& vecExcludedItemList )
{
	TRY
	return GatherExcludedFileListInWriterWithOutUnwinded(vecVolumeList, vecExcludedItemList);
	CATCH("GatherExcludedFileListInWriter")
}


DWORD GetDestSize( const std::wstring &strDir, unsigned long long &ulTotal, unsigned long long &ulFree )
{
	TRY
	typedef DWORD (* LPFUN)(const std::wstring &, unsigned long long &, unsigned long long &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "GetDestSize" );

	return pfun(strDir, ulTotal, ulFree);
	CATCH("GetDestSize")
}

DWORD WINAPI BrowseVolumeInforamtion( IN OUT LPWSTR* ppBrowseInfo, IN OUT DWORD* pdwBrowseInfoSize, BOOL bSaveAsFile, BOOL bBrowseDetail /*= FALSE*/ )
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(IN OUT LPWSTR*, IN OUT DWORD*, BOOL, BOOL);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "BrowseVolumeInforamtion" );

	return pfun(ppBrowseInfo, pdwBrowseInfoSize,bSaveAsFile, bBrowseDetail);
	CATCH("BrowseVolumeInforamtion")
}

DWORD WINAPI AFSetThreshold( unsigned long long ulThreshold )
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(unsigned long long );
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFSetThreshold" );

	return pfun(ulThreshold);
	CATCH("AFSetThreshold")
}

DWORD WINAPI AFCutConnection( const NET_CONN_INFO &info, BOOL bForce /*= TRUE*/ )
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO &, BOOL);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCutConnection" );

	return pfun( info, bForce );
	CATCH("AFCutConnection")
}

long WINAPI GatherWriterMetadataForWriter()
{
	TRY
	typedef long (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "GatherWriterMetadataForWriter" );

	return pfun();
	CATCH("GatherWriterMetadataForWriter")
}

DWORD WINAPI Native_CreateIARCFlashDev(IARCFlashDev **ppIARCFlashDev)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(IARCFlashDev **);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS_CORE_FUNC("CreateIARCFlashDev");

	return pfun(ppIARCFlashDev);
	CATCH_CORE_FUNC ("CreateIARCFlashDev")
}

DWORD WINAPI AFRetrieveActiveJobs(vector<JOB_CONTEXT> &activeJobs)
{
	TRY
		typedef DWORD (WINAPI* LPFUN)(vector<JOB_CONTEXT>&);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFRetrieveActiveJobs");

		return pfun(activeJobs);
	CATCH("AFRetrieveActiveJobs");
}

BOOL WINAPI AFValidateSessPasswordByHash(const WCHAR* pwszPwd, DWORD dwPwdLen, const WCHAR* pwszPwdHash, DWORD dwHashLen)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const WCHAR*, DWORD, const WCHAR*, DWORD);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFValidateSessPasswordByHash" );

	return pfun( pwszPwd, dwPwdLen, pwszPwdHash, dwHashLen);
	CATCH("AFValidateSessPasswordByHash")
}

DWORD WINAPI AFGetMntSess(vector<MNT_SESS> &vMntSess)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(vector<MNT_SESS> &vMntSess);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFGetMntSess" );

	return pfun( vMntSess);
	CATCH("AFGetMntSess")
}
BOOL WINAPI AFCheckDestChainAccess(const NET_CONN_INFO &curDest, NET_CONN_INFO &baseDest, NET_CONN_INFO &errDest, BOOL bPrev)
{
	TRY
	typedef BOOL (WINAPI* LPFUN)(const NET_CONN_INFO&, NET_CONN_INFO&, NET_CONN_INFO&, BOOL);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCheckDestChainAccess" );

	return pfun( curDest, baseDest, errDest, bPrev);
	CATCH("AFCheckDestChainAccess")
}

DWORD WINAPI AFCheckUpdateNetConn(const NET_CONN_INFO &dest1, const NET_CONN_INFO &dest2, BOOL bPrev)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(const NET_CONN_INFO&, const NET_CONN_INFO&, BOOL);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFCheckUpdateNetConn" );

	return pfun( dest1, dest2, bPrev);
	CATCH("AFCheckUpdateNetConn")
}

DWORD WINAPI AFRetrieveConnections(vector<NET_CONN> &vConn)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(vector<NET_CONN>&);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFRetrieveConnections");

	return pfun( vConn);
	CATCH("AFRetrieveConnections");
}

DWORD WINAPI AFMoveLogs()
{
	TRY
	typedef DWORD (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFMoveLogs");
	return pfun();
	CATCH("AFMoveLogs");
}

void WINAPI AFGetLicenseErrorInfo(LICENSE_INFO &licInfo)
{
	TRY
	typedef void (WINAPI* LPFUN)(LICENSE_INFO &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFGetLicenseErrorInfo" );

	return pfun(licInfo);
	CATCH("AFGetLicenseErrorInfo")
}

DWORD WINAPI AFGetLienseStatus()
{
	DWORD dwRet =0;
	TRY
	typedef DWORD (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFGetLienseStatus" );

	if(pfun) 
	{
		dwRet= pfun();
	}

	return dwRet;
	CATCH("AFGetLienseStatus")
}

DWORD WINAPI AFOnlineDisks()
{
	TRY
	typedef DWORD (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFOnlineDisks");
	return pfun();
	CATCH("AFOnlineDisks");
}
DWORD WINAPI AFDeleteLicError(DWORD licCode)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(DWORD);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFDeleteLicenseError" );

	return pfun(licCode);
	CATCH("AFDeleteLicError")
}

DWORD WINAPI GetD2DActiveLogTransFileXML(wstring &transFileXML)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(wstring &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "GetD2DActiveLogTransFileXML" );

	return pfun(transFileXML);
	CATCH("GetD2DActiveLogTransFileXML")
}

DWORD WINAPI DelD2DActiveLogTransFileXML()
{
	TRY
	typedef DWORD (WINAPI* LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "DelD2DActiveLogTransFileXML" );

	return pfun();
	CATCH("DelD2DActiveLogTransFileXML")
}

DWORD WINAPI GetFullD2DActiveLogTransFileXML(wstring &transFileXML)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(wstring &);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "GetFullD2DActiveLogTransFileXML" );

	return pfun(transFileXML);
	CATCH("GetFullD2DActiveLogTransFileXML")
}

DWORD WINAPI AFArchive(PAFARCHIVEJOBSCRIPT pAFJOBSCRIPT,
					 pfnUserCallProc UserCallBack,
					 LPVOID lpParameter)
{
    TRY
    typedef DWORD (WINAPI *LPFUN)(PAFARCHIVEJOBSCRIPT pAFJOBSCRIPT,
					 pfnUserCallProc UserCallBack,
					 LPVOID lpParameter);
    LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFArchive");
    return pfun(pAFJOBSCRIPT, UserCallBack, lpParameter);
    CATCH("AFArchive")
}

LONG WINAPI AFISetPreAllocSpacePercent(DWORD dwPercent)
{
	TRY
	typedef DWORD (WINAPI* LPFUN)(DWORD);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFISetPreAllocSpacePercent" );

	return pfun(dwPercent);
	CATCH("AFISetPreAllocSpacePercent")
}
LONG WINAPI AFIGetPreAllocSpacePercent(DWORD& dwPercent)
{	
	TRY
		typedef DWORD (WINAPI* LPFUN)(DWORD&);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS( "AFIGetPreAllocSpacePercent" );

	return pfun(dwPercent);
	CATCH("AFIGetPreAllocSpacePercent")	
}

 BOOL WINAPI IsFirmwareuEFI()
{
	TRY
	typedef BOOL (WINAPI *LPFUN)();
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("IsFirmwareuEFI");
	return pfun();
	CATCH("IsFirmwareuEFI")
}

 DWORD WINAPI AFGetMntInfoCount(const NET_CONN_INFO &info, const wchar_t *pszRelative)
 {
	 TRY
	 typedef DWORD (WINAPI *LPFUN)(const NET_CONN_INFO&, const wchar_t*);
	 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetMntInfoCount");
	 return pfun(info, pszRelative);
	 CATCH("AFGetMntInfoCount")
 }

 DWORD WINAPI AFGetMntInfo(const NET_CONN_INFO &info, const wchar_t *pszRelative, AFMOUNTMGR::AFMOUNTINFO *pMntList, DWORD dwNum)
 {
	 TRY
	 typedef DWORD (WINAPI *LPFUN)(const NET_CONN_INFO&, const wchar_t*,AFMOUNTMGR::AFMOUNTINFO*, DWORD);
	 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetMntInfo");
	 return pfun(info, pszRelative,pMntList,dwNum);
	 CATCH("AFGetMntInfo")
 }

 DWORD WINAPI AFGetAllMntInfoCount()
 {
	 TRY
	 typedef DWORD (WINAPI *LPFUN)();
	 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetAllMntInfoCount");
	 return pfun();
	 CATCH("AFGetAllMntInfoCount")
 }

 DWORD WINAPI AFGetAllMntInfo(AFMOUNTMGR::AFMOUNTINFO *pMntList, DWORD dwNum)
 { 
	 TRY
	 typedef DWORD (WINAPI *LPFUN)(AFMOUNTMGR::AFMOUNTINFO *, DWORD);
	 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetAllMntInfo");
	 return pfun(pMntList, dwNum);
	 CATCH("AFGetAllMntInfo")

 }

 DWORD WINAPI AFMountSession(const AFMOUNTMGR::MOUNT_PARAM *pMntParam, AFMOUNTMGR::AFMOUNTINFO *pMntInfo)
 {
	 TRY
	 typedef DWORD (WINAPI *LPFUN)(const AFMOUNTMGR::MOUNT_PARAM *, AFMOUNTMGR::AFMOUNTINFO *);
	 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFMountSession");
	 return pfun(pMntParam, pMntInfo);
	 CATCH("AFMountSession")
 }

 DWORD WINAPI AFDismount(const wchar_t *pszMnt, DWORD dwDiskSig)
 {
	 TRY
	 typedef DWORD (WINAPI *LPFUN)(const wchar_t*, DWORD);
	 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFDismount");
	 return pfun(pszMnt, dwDiskSig);
	 CATCH("AFDismount")
 }

 DWORD WINAPI AFGetMntInfoForVolume(const NET_CONN_INFO &info, const wchar_t *pszRelative, const wchar_t *pszVolGuid, AFMOUNTMGR::AFMOUNTINFO *pMntInfo)
 {
	 TRY
	 typedef DWORD (WINAPI *LPFUN)(const NET_CONN_INFO&, const wchar_t*, const wchar_t*, AFMOUNTMGR::AFMOUNTINFO*);
	 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetMntInfoForVolume");
	 return pfun(info, pszRelative, pszVolGuid,pMntInfo);
	 CATCH("AFGetMntInfoForVolume")
 }

 DWORD WINAPI AFGetMntInfoForVolumeCount(const NET_CONN_INFO &info, const wchar_t *pszRelative, const wchar_t *pszVolGuid)
 {
	 TRY
	 typedef DWORD (WINAPI *LPFUN)(const NET_CONN_INFO&, const wchar_t*, const wchar_t*);
	 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetMntInfoForVolumeCount");
	 return pfun(info, pszRelative, pszVolGuid);
	 CATCH("AFGetMntInfoForVolumeCount")
 }

DWORD WINAPI AFIReadD2DReg(LPBYTE lpBuffer,DWORD& dwBufSize,DWORD& dwRegType, 
	 const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg)
 {
	TRY 
	{
		typedef DWORD (WINAPI *LPFUN) (LPBYTE,DWORD&, DWORD&, const WCHAR*, const WCHAR*, const WCHAR*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIReadD2DReg");
		return pfun(lpBuffer, dwBufSize, dwRegType, pwzRegKeyName, pwzRegValName, pwzD2DRootReg);
	}
	CATCH ("AFIReadD2DReg");
 }

 DWORD WINAPI AFIWriteD2DReg(const LPBYTE lpBuffer,DWORD dwWriteBufSize,DWORD dwRegType, 
	 const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg)
 {
	 TRY 
	 {
		 typedef DWORD (WINAPI *LPFUN) (const LPBYTE,DWORD,DWORD, const WCHAR*, const WCHAR*, const WCHAR*);
		 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIWriteD2DReg");
		 return pfun(lpBuffer,dwWriteBufSize, dwRegType, pwzRegKeyName, pwzRegValName, pwzD2DRootReg);
	 }
	 CATCH ("AFIWriteD2DReg");
 }

 DWORD WINAPI AFGetActualPathName(wstring &wsFolderPath, wstring &wsOutFolderPath)
{
    TRY
    typedef DWORD (WINAPI *LPFUN)(wstring &wsFolderPath, wstring &wsOutFolderPath);
    LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFGetActualPathName");
    return pfun(wsFolderPath, wsOutFolderPath);
    CATCH("AFGetActualPathName")
}

 IJobMonInterface* WINAPI AFICreateMergeJM(DWORD dwJobID, DWORD* dwRet, DWORD dwJobType)
 {
	TRY 
	{
		typedef IJobMonInterface* (WINAPI *LPFUN) (DWORD, DWORD*, DWORD);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFICreateMergeJM");
		return pfun(dwJobID, dwRet, dwJobType);
	}
	CATCH ("AFICreateMergeJM");
 }

 void WINAPI AFIReleaseMergeJM(IJobMonInterface** ppJobMonMgr)
 {
	TRY 
	{
		typedef void (WINAPI *LPFUN) (IJobMonInterface**);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIReleaseMergeJM");
		pfun(ppJobMonMgr);
	}
	CATCH ("AFIReleaseMergeJM");
 }

 long WINAPI AFIStopMergeJob(DWORD dwJobID, DWORD dwJobType)
 {
	 TRY 
	 {
		typedef long (WINAPI *LPFUN)(DWORD, DWORD);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIStopMergeJob");
		return pfun(dwJobID, dwJobType);
	 }
	 CATCH ("AFIStopMergeJob");
 }

 long WINAPI AFIStartJob(const WCHAR* pwzJSPath, DWORD* pdwProcID /* = NULL*/, 
	 const WCHAR* pwzUsrName /* = NULL*/, 
	 const WCHAR* pwzPassword /* = NULL*/, 
	 DWORD dwJobType /* = EJT_MERGE*/)
 {
	 TRY 
	 {
		 typedef long (WINAPI *LPFUN)(const WCHAR*, DWORD*, 
			 const WCHAR*, 
			 const WCHAR*, 
			 DWORD);
		 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIStartJob");
		 return pfun(pwzJSPath, pdwProcID, pwzUsrName, pwzPassword, dwJobType);
	 }
	 CATCH ("AFIStartJob");
 }

 long WINAPI AFIIsMergeJobAvailable(DWORD dwRetentionCnt, 
					const WCHAR* pwzBKDest, 
					const WCHAR* pwzVMGUID/* = NULL*/, 
					const WCHAR* pwzBKUsr/* = NULL*/, 
					const WCHAR* pwzBKPwd/* = NULL*/,
                    const WCHAR* pwzDS4Replication/* = NULL*/)
 {
	TRY 
	{
		typedef long (WINAPI *LPFUN)(DWORD, 
			const WCHAR*, 
			const WCHAR*, 
			const WCHAR*, 
			const WCHAR*,
            const WCHAR*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIIsMergeJobAvailable");
		return pfun(dwRetentionCnt, pwzBKDest, pwzVMGUID, pwzBKUsr, pwzBKPwd, pwzDS4Replication);
	}
	CATCH ("AFIIsMergeJobAvailable")
 }
	
 long  WINAPI AFIRetrieveMergeJM(ActJobVector& vecActiveJob)
 {
	 TRY 
	 {
		 typedef long (WINAPI *LPFUN)(ActJobVector&);
		 LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIRetrieveMergeJM");
		 return pfun(vecActiveJob);
	 }
	 CATCH ("AFIRetrieveMergeJM")
 }

 long WINAPI AFISetBackupSetFlag(const WCHAR* pwzBKDest, DWORD dwSessNum, DWORD dwBKSetFlag, const WCHAR* pwzVMGUID)
 {
	TRY 
	{
		typedef long (WINAPI *LPFUN)(const WCHAR*, DWORD, DWORD, const WCHAR*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFISetBackupSetFlag");
		return pfun(pwzBKDest, dwSessNum, dwBKSetFlag, pwzVMGUID);
	}
	CATCH ("AFISetBackupSetFlag")
 }
 DWORD WINAPI AFStartCatalogGenerator(
						DWORD        dwQueueType,           // Job in which queue will be run. 1 = regular, 2 = ondemand
						DWORD        dwJobNum,              // Job number for job monitor.
						DWORD*       pdwProcID,      // Return the new process if needed.
				const WCHAR* pwzUsrName,     // User name for security requirement when start process.
				const WCHAR* pwzPassword,    // Password for security requirement when start process.
				const WCHAR* pwzJobQIdentity,  // Job queue name, Empty for local D2D, VM GUID for vsphere.
				const NET_CONN_INFO* stBKDest)
 {
	 TRY 
	 {
		 typedef DWORD (WINAPI *LPFUN)(DWORD, DWORD, DWORD*, const WCHAR*, 
			 const WCHAR*, const WCHAR*, const NET_CONN_INFO*);
		 LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFStartCatalogGenerator");
		 return fpun(dwQueueType, dwJobNum, pdwProcID, pwzUsrName, 
			 pwzPassword, pwzJobQIdentity, stBKDest);
	 }
	 CATCH ("AFStartCatalogGenerator")
 }

int WINAPI AFIUpdateSessionPasswordByGUID(wstring& guid, LPCWSTR password, ULONG length)
{
	TRY
	typedef int (WINAPI* LPFUN)(wstring&, LPCWSTR, ULONG);
	LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIUpdateSessionPasswordByGUID");

	return pfun(guid, password, length);
	CATCH("AFIUpdateSessionPasswordByGUID")
}

long WINAPI AFISaveMergeJS(CMergeJS& MergeJS, const WCHAR* pwzJSPath)
{
	TRY 
	{
		typedef long (WINAPI *LPFUN)(CMergeJS&, const WCHAR*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFISaveMergeJS");
		return pfun(MergeJS, pwzJSPath);
	}
	CATCH ("AFISaveMergeJS")
}

long WINAPI AFIGetSessNumListForNextMerge(DWORD dwRetentionCnt, 
					const WCHAR* pwzBKDest, 
					vector<DWORD>& vecSessNumList, 
					const WCHAR* pwzBKUsr/* = NULL*/, 
					const WCHAR* pwzBKPwd/* = NULL*/)
 {
	TRY 
	{
		typedef long (WINAPI *LPFUN)(DWORD, 
			const WCHAR*, 
			vector<DWORD>&, 
			const WCHAR*, 
			const WCHAR*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIGetSessNumListForNextMerge");
		return pfun(dwRetentionCnt, pwzBKDest, vecSessNumList, pwzBKUsr, pwzBKPwd);
	}
	CATCH ("AFIGetSessNumListForNextMerge")
 }

long WINAPI AFIGetFullSess4Inc(DWORD& dwFullSess, 
							   DWORD dwIncSess, 
							   const WCHAR* pwzBKDest)
{
	TRY 
	{
		typedef long (WINAPI *LPFUN)(DWORD&,
							DWORD, const WCHAR*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIGetFullSess4Inc");
		return pfun(dwFullSess, dwIncSess, pwzBKDest);
	}
	CATCH ("AFIGetFullSess4Inc")
}

long WINAPI AFIGetDataStoreHashKey(wstring& wsDSHashKey, 
									const WCHAR* pwzBKDest, 
									DWORD dwSessNum, 
									const WCHAR* pwzSessPwd)
{
	TRY 
	{
		typedef long (WINAPI *LPFUN)(wstring&,
			const WCHAR*, DWORD, const WCHAR*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIGetDataStoreHashKey");
		return pfun(wsDSHashKey, pwzBKDest, dwSessNum, pwzSessPwd);
	}
	CATCH ("AFIGetDataStoreHashKey")
}

BOOL AFHasCatalogFiles(const wchar_t * dest, DWORD sessNum)
{
	TRY 
	{
		typedef BOOL (*LPFUN)(const wchar_t*, DWORD);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFHasCatalogFiles");
		return pfun(dest, sessNum);
	}
	CATCH ("AFHasCatalogFiles")
}

DWORD WINAPI AFIVerifyDestUser(const wchar_t * pwszPath, const wchar_t * pwszUser, const wchar_t * pwszpPsw)
{
	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(const wchar_t*, const wchar_t*, const wchar_t*);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIVerifyDestUser");
		return pfun(pwszPath, pwszUser, pwszpPsw);
	}
	CATCH ("AFIVerifyDestUser")
}

DWORD WINAPI AFIGetUserSID(
	wstring& wsUserSID, 
	const WCHAR* pwzUserName /* = NULL */, 
	wstring* pwsSIDAccount /* = NULL */, 
	wstring* pwsDomain4SID /* = NULL */, 
	DWORD* pdwSIDAccountType /* = NULL */,
	DWORD  dwHashAlg /* = EHAT_SHA1 */)
{
	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)(wstring&, const WCHAR*, wstring*, wstring*, DWORD*, DWORD);
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIGetUserSID");
		return pfun(wsUserSID, pwzUserName, pwsSIDAccount, pwsDomain4SID, pdwSIDAccountType, dwHashAlg);
	}
	CATCH ("AFIGetUserSID")	
}

DWORD WINAPI AFIGetNodeID( wstring& strNodeID )
{
	TRY 
	{
		typedef DWORD (WINAPI *LPFUN)( wstring& );
		LPFUN pfun = (LPFUN)GET_PROC_ADDRESS("AFIGetNodeID");
		return pfun(strNodeID);
	}
	CATCH ("AFIGetNodeID")	
}

DWORD WINAPI AFIQueryJobQueue(
	E_QUEUE_TYPE eJobQType,						 //ZZ: Specify job queue type. Refer to E_QUEUE_TYPE(Regular, On-Demand, and Makeup)
	const WCHAR* pwzJobQIdentity /* = NULL */,     //ZZ: Job queue identity. VM GUID for HBBU or computer identity for catalog running on RPS.
	wstring*     pwsJobQPath /* = NULL */,         //ZZ: Return job queue path which contain job scripts. Ignore this parameter when it is NULL.
	WSVector*    pvecJobScriptList /* = NULL */,   //ZZ: Return job script list under job queue. Ignore this parameter when it is NULL.
	bool bCreateJobQFolder /* = false */,			 //ZZ: If create job queue folder when it doesn't exist.
	const WCHAR* pwzCatalogModeID /* = NULL */)    //ZZ: Server identity where catalog should be launched. If this parameter is empty, it will be ignored.
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(E_QUEUE_TYPE, const WCHAR*, wstring*, WSVector*, bool, const WCHAR*);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFIQueryJobQueue");
		return fpun((E_QUEUE_TYPE)eJobQType, pwzJobQIdentity, pwsJobQPath, pvecJobScriptList, bCreateJobQFolder,pwzCatalogModeID);
	}
	CATCH ("AFIQueryJobQueue")
}

DWORD WINAPI AFTestUpdateServerConnection( DWORD dwType, DWORD dwServerType, const wstring& strServer, int nPort, 
	const wstring& strProxyServer, int nProxyServerPort, 
	const wstring& strProxyUser, const wstring& strProxyPassword )
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(DWORD, DWORD, const wstring&, int, const wstring&, int, const wstring&, const wstring&);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFTestUpdateServerConnection");
		return fpun( dwType, dwServerType, strServer, nPort, strProxyServer, nProxyServerPort, strProxyUser, strProxyPassword );
	}
	CATCH ("AFTestUpdateServerConnection")
}


DWORD WINAPI AFTestBIUpdateServerConnection(DWORD dwType, DWORD dwServerType, const wstring& strServer, int nPort,
	const wstring& strProxyServer, int nProxyServerPort,
	const wstring& strProxyUser, const wstring& strProxyPassword)
{
	TRY
	{
		typedef DWORD(WINAPI *LPFUN)(DWORD, DWORD, const wstring&, int, const wstring&, int, const wstring&, const wstring&);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFTestBIUpdateServerConnection");
		return fpun(dwType, dwServerType, strServer, nPort, strProxyServer, nProxyServerPort, strProxyUser, strProxyPassword);
	}
		CATCH("AFTestBIUpdateServerConnection")
}

DWORD WINAPI AFInstallBIUpdate(DWORD dwType)
{
	TRY
	{
		typedef DWORD(WINAPI *LPFUN)(DWORD);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFInstallBIUpdate");
		return fpun(dwType);
	}
		CATCH("AFInstallBIUpdate")
}

DWORD WINAPI AFCheckBIUpdate(DWORD dwType)
{
	TRY
	{
		typedef DWORD(WINAPI *LPFUN)(DWORD);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFCheckBIUpdate");
		return fpun(dwType);
	}
		CATCH("AFCheckBIUpdate")
}

//added by cliicy.luo

BOOL WINAPI AFIsUpdateBusy( DWORD dwType )
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(DWORD);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFIsUpdateBusy");
		return fpun( dwType );
	}
	CATCH ("AFIsUpdateBusy")
}

DWORD WINAPI AFCheckUpdate( DWORD dwType )
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(DWORD);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFCheckUpdate");
		return fpun(dwType);
	}
	CATCH ("AFCheckUpdate")
}

DWORD WINAPI AFGetUpdateStatusFile( DWORD dwType, wstring& strFile )
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(DWORD, wstring&);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFGetUpdateStatusFile");
		return fpun( dwType, strFile );
	}
	CATCH ("AFGetUpdateStatusFile")
}

DWORD WINAPI AFGetUpdateSettingsFile( DWORD dwType, wstring& strFile )
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(DWORD, wstring&);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFGetUpdateSettingsFile");
		return fpun( dwType, strFile );
	}
	CATCH ("AFGetUpdateSettingsFile")
}

DWORD WINAPI AFInstallUpdate( DWORD dwType )
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(DWORD);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFInstallUpdate");
		return fpun( dwType );
	}
	CATCH ("AFInstallUpdate")
}

DWORD WINAPI AFGetAlertMailFiles( DWORD dwType, std::vector<wstring>& mailAlertFiles )
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(DWORD, std::vector<wstring>&);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFGetAlertMailFiles");
		return fpun( dwType, mailAlertFiles );
	}
	CATCH ("AFGetAlertMailFiles")
}

DWORD WINAPI AFGetUpdateErrorMessage( DWORD dwType, DWORD dwErrCode, wstring& strErrorMessage )
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)(DWORD, DWORD, wstring& );
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFGetUpdateErrorMessage");
		return fpun( dwType, dwErrCode, strErrorMessage );
	}
	CATCH ("AFGetUpdateErrorMessage")
}

DWORD WINAPI AFIGenerateRecoveryPointSyncInfo( const NET_CONN_INFO &info, const wchar_t* lpszVmUUID, BOOL bFullSync /*= FALSE*/ )
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)( const NET_CONN_INFO&, const wchar_t*, BOOL);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFIGenerateRecoveryPointSyncInfo");
		return fpun( info, lpszVmUUID, bFullSync );
	}
	CATCH ("AFIGenerateRecoveryPointSyncInfo")
}

DWORD WINAPI AFIEnumBackupDestinations( const NET_CONN_INFO &info, std::vector<wstring>& vecBackupDestinations )
{
	TRY
	{
		typedef DWORD (WINAPI *LPFUN)( const NET_CONN_INFO&, std::vector<wstring>& );
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFIEnumBackupDestinations");
		return fpun( info, vecBackupDestinations );
	}
	CATCH ("AFIEnumBackupDestinations")
}

//added by cliicy.luo
DWORD WINAPI AFGetBIUpdateStatusFile(DWORD dwType, wstring& strFile)
{
	TRY
	{
		typedef DWORD(WINAPI *LPFUN)(DWORD, wstring&);
		LPFUN fpun = (LPFUN)GET_PROC_ADDRESS("AFGetBIUpdateStatusFile");
		return fpun(dwType, strFile);
	}
		CATCH("AFGetUpdateStatusFile")
}
//added by cliicy.luo