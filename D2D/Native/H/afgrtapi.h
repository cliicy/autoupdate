#pragma once

#include "windows.h"

#include "iCatalogCallback.h"
#include "vmdkimg.h"

#ifdef AFGRTINTERFACE_EXPORTS
#define AFGRTAPI _declspec(dllexport)
#else
#define AFGRTAPI _declspec(dllimport)
#endif

typedef struct _GrtEdbInfo 
{
	wchar_t*		pszDBFullPath;        // EDb file full path
	wchar_t*	 	pszSTMFullPath;		 // stm file full path
	wchar_t*    pszDBLogDirectory;    // Edb log directory
	wchar_t*	 	pszDBCHKFullPath;		 // Edb chk file Directory
}GrtEdbInfo, * LPGrtEdbInfo;

typedef struct _EE_DataInfo
{
	DWORD       dwVersion;			// this member is not used now. please assign it 1
	PBYTE		pData;				// the returned Data
	DWORD		dwDataSize;			// the Data Size
}EE_DataInfo, *LPEE_DataInfo;

typedef struct _EE_RestorePara
{
	DWORD		dwVersion;			// this member is not used now. please assign it 1
	PWCHAR		pIDList;			// MailboxID, FolderID, MsgID is Contained in the List
	DWORD		dwDataSize;			// the Data Size
}EE_RestorePara,*LPEE_RestorePara;

typedef struct	_GrtSPDBInfo
{
	struct PathPair
	{
		TCHAR	szPath[_MAX_PATH];
		TCHAR	szMountPath[_MAX_PATH];
	};

	DWORD			fileCnt;
	const TCHAR		*szDBName;
	PathPair		pathPairList[1];
}GrtSPDBInfo, * PGrtSPDBInfo;

typedef struct _MountedDiskInformation
{
	WCHAR * pwzSignature;
	WCHAR * pwzMountName;
	WCHAR * pwzSessionFolder;
	WCHAR * pwzVolumeName;
}MountedDiskInformation;

typedef struct _MountedDiskCollection
{
	ULONG ulSize;
	MountedDiskInformation * pMountedDiskInformation;
}MountedDiskCollection;

#ifdef __cplusplus
extern "C" {
#endif

/***************************************************************************
AFGRTEDBCopyBinary()

   This function generated exchange catalog on the specific destination.
	@Parameter
	DestinationPath:	the path which stores exchange binary
	@return value
   Returns 0 if function succeed
   otherwise means failure
*/

AFGRTAPI DWORD	AFGRTEDBCopyBinary(WCHAR* DestinationPath);


/***************************************************************************
//Copy Exchange binaries from mounted path to backup destination session folder.
*/
AFGRTAPI DWORD	AFGRTCopyBinaryEx(DWORD dwJobNo, 
			WCHAR* pwszSessionRootPath, 
			ULONGLONG ullSessionID,  
			const WCHAR* pwszExchangeBinaryPath, 
			IGRTCallBack * pvCallback = NULL, 
			WCHAR* pwszUserName = NULL, 
			WCHAR* pwszPassword = NULL, 
			PD2D_ENCRYPTION_INFO pEI = NULL);

/***************************************************************************
AFGRTGenerateCatalog()

   This function generated exchange catalog on the specific destination.
	@Parameter
	SessionRootPath:	session root path
	ulSessionID:		session ID
	EDBGeneratedPath: specific destination for generated catalog
	LPGrtEdbInfo:		EDB 4 files path
	ExchangeBinaryPath:	the path which stores exchange binary
	@return value
   Returns 0 if function succeed
   otherwise means failure
*/

AFGRTAPI DWORD AFGRTEDBGenerateCatalog(DWORD dwJobNo, WCHAR* SessionRootPath, ULONGLONG ullSessionID, WCHAR* EDBGeneratedPath, LPGrtEdbInfo pGrtEdbInfo, WCHAR* ExchangeBinaryPath,IGRTCallBack * pvCallback = NULL);
AFGRTAPI DWORD AFGRTEDBGenerateCatalogEx(DWORD dwJobNo, WCHAR* SessionRootPath, ULONGLONG ullSessionID, WCHAR* EDBGeneratedPath, LPGrtEdbInfo pGrtEdbInfo, WCHAR* ExchangeBinaryPath, IGRTCallBack * pvCallback = NULL, WCHAR* pwszUserName = NULL, WCHAR* pwszPassword = NULL);
AFGRTAPI DWORD AFGRTEDBGenerateCatalogEx2(DWORD dwJobNo, WCHAR* SessionRootPath, ULONGLONG ullSessionID, WCHAR* EDBGeneratedPath, LPGrtEdbInfo pGrtEdbInfo, WCHAR* ExchangeBinaryPath, IGRTCallBack * pvCallback = NULL, WCHAR* pwszUserName = NULL, WCHAR* pwszPassword = NULL, PD2D_ENCRYPTION_INFO pEI = NULL);

AFGRTAPI DWORD AFGRTSPDBGenerateCatalog(WCHAR* SessionRootPath, ULONGLONG ullSessionID, WCHAR* SPDBGeneratedPath, GrtSPDBInfo *pDBInfo);
/***************************************************************************
AFGRTEDBRestore()

   This function restore exchange files based on the xml file.
	@Parameter
	SessionRootPath:		session root path (Backup Destination -- should contain machine name)
	ulSessionID:			session ID
	pGrtEdbInfo:			EDB 4 files path(the path contains edb file which on restore tree, as well as log etc)
	ExchangeBinaryPath:	the path which stores exchange binary
	pXMLFile:				a buffer contains to XML content
	pStat:				    record the restore statistic info
	@return value
   Returns 0 if function succeed
   otherwise means failure
*/
AFGRTAPI DWORD AFGRTEDBRestore(WCHAR* SessionRootPath, ULONGLONG ullSessionID, LPGrtEdbInfo pGrtEdbInfo, WCHAR* ExchangeBinaryPath, PBYTE pXMLFile, PRESTORE_IFS_STATISTIC pStat, PLONGLONG pulXferBytesJob);
AFGRTAPI DWORD AFGRTEDBRestoreEx2(WCHAR* SessionRootPath, ULONGLONG ullSessionID, LPGrtEdbInfo pGrtEdbInfo, WCHAR* ExchangeBinaryPath, PBYTE pXMLFile, PRESTORE_IFS_STATISTIC pStat, PLONGLONG pulXferBytesJob, WCHAR* pwszUserName, WCHAR* pwszPassword, PD2D_ENCRYPTION_INFO pEI);
AFGRTAPI DWORD AFGRTEDBRestore_BackEnd(DWORD dwJobNo, WCHAR* SessionRootPath, ULONGLONG ullSessionID, LPGrtEdbInfo pGrtEdbInfo, WCHAR* ExchangeBinaryPath, PBYTE pXMLFile, PRESTORE_IFS_STATISTIC pStat, PLONGLONG pulXferBytesJob, PULONG pulMailboxCount, WCHAR* pwszEncryptPwd, WCHAR* pwszDestUserName, WCHAR* pwszDestPassword);
AFGRTAPI DWORD AFGRTSPDBRestore(WCHAR* SessionRootPath, ULONGLONG ullSessionID, GrtSPDBInfo *pDBInfo, PBYTE pXMLFile);


AFGRTAPI DWORD AFGRTMountEDB(IN const PWCHAR SessionRootPath, IN const ULONGLONG ullSessionID, IN const LPGrtEdbInfo pGrtEdbInfo, OUT HANDLE * pGRTEDBMountedObjectHandle, DWORD dwJobNo = 0);
AFGRTAPI DWORD AFGRTMountEDBEx(IN const PWCHAR SessionRootPath, IN const ULONGLONG ullSessionID, IN const LPGrtEdbInfo pGrtEdbInfo, OUT HANDLE * pGRTEDBMountedObjectHandle, WCHAR* pwszUserName, WCHAR* pwszPassword, DWORD dwJobNo = 0);
AFGRTAPI DWORD AFGRTMountEDBEx2(IN const PWCHAR SessionRootPath, IN const ULONGLONG ullSessionID, IN const LPGrtEdbInfo pGrtEdbInfo, OUT HANDLE * pGRTEDBMountedObjectHandle, WCHAR* pwszUserName, WCHAR* pwszPassword, PD2D_ENCRYPTION_INFO pEI, DWORD dwJobNo = 0);
AFGRTAPI DWORD AFGRTMountInstVol(IN const PWCHAR SessionRootPath, IN const ULONGLONG ullSessionID, IN const PWCHAR pExchInstPath, OUT HANDLE * pGRTEDBMountedObjectHandle, WCHAR* pwszUserName, WCHAR* pwszPassword, PD2D_ENCRYPTION_INFO pEI, DWORD dwJobNo = 0);
AFGRTAPI DWORD AFGRTDisMountEDB(IN HANDLE pGRTEDBMountedObjectHandle);

AFGRTAPI DWORD AFGRTRecoverEDB(IN HANDLE pGRTEDBMountedObjectHandle);
AFGRTAPI DWORD AFGRTInitEDB(IN HANDLE pGRTEDBMountedObjectHandle);
AFGRTAPI DWORD AFGRTRestoreEDBItem(IN HANDLE pGRTEDBMountedObjectHandle, IN const EE_RestorePara * pRestorePara, OUT LPEE_DataInfo * ppDataInfo);
AFGRTAPI DWORD AFGRTUninitAndDisMountEDB(IN HANDLE pGRTEDBMountedObjectHandle);

AFGRTAPI DWORD AFGRTUninitAndDisMountEDB(IN HANDLE pGRTEDBMountedObjectHandle);

AFGRTAPI DWORD AFGRTQueryMountedDisk(OUT MountedDiskCollection ** ppMountedDiskCollection);
AFGRTAPI DWORD AFGRTFreeMountedDisk(IN MountedDiskCollection * pMountedDiskCollection);

AFGRTAPI DWORD AFGRTSkipDisk(IN const PWCHAR pwzVolName, OUT BOOL * pbSkipped);

#ifdef __cplusplus
}
#endif
