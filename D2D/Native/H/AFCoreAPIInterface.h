#pragma once
#include "AFJob.h"
#include "afdefine.h"
#include "AFCoreFunction.h"
// add by wanmi12 for supporting AOE <
#include "./../Exchange.GRT/include/sdefExAdinfo.h"
// add by wanmi12 for supporting AOE >

#include "afgrtapi.h"
#include "AFSessMgr.h"
#include "AFMountMgr.h"
#include "AFMergeMgrInterface.h"
#include "ICryptoWrapperInterface.h"
#include "ICatalogMgrInterface.h"
#include "IXmlParser.h"
#include "PublicAFvAppInfoAPI.h" //<huvfe01>2014-8-20 browse vApp childs

///ZZ: Added for configuring pre-allocate write space on web service side. @2012.2.6
#define D2D_CONFIGURATION_FOLDER_NAME                 L"Configuration"
#define D2D_PREALLOC_SPCAE_CONFIG_FILE_NAME           L"AFStor.ini"
#define D2D_PREALLOC_SPCAE_CONFIG_SECTION_NAME        L"Configuration"
#define D2D_PREALLOC_SPCAE_CONFIG_KEY_NAME            L"PreAllocate%"
#define D2D_PREALLOC_SPCAE_DEFAULT_VAL                10
#define D2D_PREALLOC_SPCAE_DEFAULT_VAL_STR            L"10"

typedef struct tagJobStatus
{
    LARGE_INTEGER	liGrandTotal;
    LARGE_INTEGER	liVolumeTotal;
    LARGE_INTEGER	liVolumeProcessed;
    BOOL			bJobCancled;
} JobStatus, *PJobStatus;

typedef DWORD (WINAPI *pfnUserCallProc)( LPVOID lpParameter);

DWORD WINAPI AFBackup(PAFJOBSCRIPT pAFJOBSCRIPT,
                      pfnUserCallProc UserCallBack,
                      LPVOID lpParameter);

DWORD WINAPI AFRestore(PAFJOBSCRIPT pAFJOBSCRIPT,
                       pfnUserCallProc UserCallBack,
                       LPVOID lpParameter);

DWORD WINAPI AFCopy(PAFJOBSCRIPT pAFJOBSCRIPT,
                    pfnUserCallProc UserCallBack,
                    LPVOID lpParameter);

// Save RPS REP job script // baide02 2011-11-02
DWORD WINAPI AFRPSRepSaveMakeupJobScript(const RPSRepJobScript* pJs);

DWORD WINAPI AFRunPatchJob(PTCHAR szCommandLine);

DWORD WINAPI AFSetJobStatus(IN PJobStatus pJobStatus);

DWORD WINAPI AFGetJobStatus(OUT PJobStatus pJobStatus);

// Check if there is a GRT catalog job for the specified session.
BOOL AFIsCatalogJobRunning (PWCHAR dest, DWORD sessNum, DWORD subSess );

// D2D for Exchange GRT. zhazh06 R16. 
DWORD AFCatalogJob(PAFCATALOGJOB pAFJob );

//<sonmi01>2013-4-28 #
BOOL AFHasCatalogFiles(const wchar_t * dest, DWORD sessNum);


///<ZZ[zhoyu03: 2009/06/24]: This interface has 2 ways to gather application information..
// 1. Save application information as XML file using caller specifying name. In this case, bSaveAsFile should be 
//    set as true. ppBrowseInfo indicates file name to be used, while pdwBrowseInfoSize indicates character number
//    of file name 
// 2. Return XML file content string to caller. In this case, bSaveAsFile should be set as false. ppBrowseInfo should
//    be a input parameter, and it will be allocated enough memory for XML content. The XML content length will also be
//    returned in pdwBrowseInfoSize. After using, make sure calling ReleaseBrowseInformation to free its memory.
DWORD WINAPI BrowseAppInforamtion(IN OUT LPWSTR* ppBrowseInfo, IN OUT DWORD* pdwBrowseInfoSize, BOOL bSaveAsFile);
DWORD WINAPI BrowseVolumeInforamtion(IN OUT LPWSTR* ppBrowseInfo, IN OUT DWORD* pdwBrowseInfoSize, BOOL bSaveAsFile, BOOL bBrowseDetail = FALSE, WCHAR* pwzBackupDest = NULL);
DWORD WINAPI ReleaseBrowseInformation(IN LPWSTR* ppBrowseInfo);


#define MAXIMAL_LENGTH_SQL_INSTANCE_NAME_IN_CHARACTERS   16   // Maximal length of SQL instance name is 16 characters.
#define MAXIMAL_LENGTH_SQL_DATABASE_NAME_IN_CHARACTERS  128   // Maximal length of SQL database name is 128 characters, 
// we only can input 123 characters in client GUI.
#define MAXIMAL_LENGTH_SQL_DATABASE_INDEX_IN_CHARACTERS   4   // Database index number in HEX, e.g. 0001, 00AB           
#define MAXIMAL_LENGTH_ALTERNATE_LOCATION_BASE_PATH     108   // MAX_PATH - 16 - 128 - 4 - 3(backslash) - 1(NULL)

///<ZZ[zhoyu03: 2009/12/25]: Check if folder can be used for alternate location.
// Return zero means the folder can be used for SQL alternate location restore
// Return value more than zero means input folder is too long, the return value indicate maximal length in characters.
// Return value less than zero means internal error. It is most possible no available folder can be used.
long WINAPI AFCheckSQLAlternateLocation(const WCHAR* pwzDstBasePath, 
                                        const WCHAR* pwzInstanceName, 
                                        const WCHAR* pwzDatabaseName,
                                        std::wstring& wsAlterDstPath);

typedef struct _FILE_INFO
{
    DWORD dwFileAttributes; //file attributes. defined in WIN32_FIND_DATAW
    DWORD nFileSizeHigh;    //file size, defined in WIN32_FIND_DATAW
    DWORD nFileSizeLow;     //file size, defined in WIN32_FIND_DATAW
    FILETIME ftCreationTime; //file created time, defined in WIN32_FIND_DATAW
    FILETIME ftLastAccessTime; //file access time, defined in WIN32_FIND_DATAW
    FILETIME ftLastWriteTime;  //file modify time, defined in WIN32_FIND_DATAW
    std::wstring strName;          //file name without path.
    std::wstring strPath;          //file full path.

}FILE_INFO, *PFILE_INFO;


#define	FILELIST_BOTH			0
#define	FILELIST_FOLDER_ONLY	1
#define	FILELIST_FILE_ONLY		2

class IFileListHandler
{
public:

    virtual void Release() = 0;

    /*
    *Purpose: Get file list for specified folder.

    *@vList: [output] container for file or folder information.

    *@iType: [input] indicate what to retrieve. 0 means both files / folder; 1 means folder only; 2 mean file only

    *@iNum: [input output] for input, it defines the number of files or folders which will be got.
    for input, if iNum is negative, all files and folders in specified folder will be returned,
    and iNum contains the number of total files and folders.
    for output, it returns the number of files or folders which are acctually got.

    @strDir: [input] folder to traverse. If strDir is NULL, former folder will be used to continue traverse.

    *Return: Zero for success. If fails, windows standard error code will be returned.

    *Remarks: If return value is Zero, iNum contains the number of files or folders in specified folder.
    If the specified folder doesn't contain files or folders any more after you call this function, 
    and when you call this function again, ERROR_NO_MORE_ITEMS will be returned and iNum will be Zero.
    */
    virtual DWORD GetFileList(std::vector<FILE_INFO> &vList, int &iNum, const std::wstring &strDir) = 0;

    virtual DWORD GetFileListEx(std::vector<FILE_INFO> &vList, int iType, int &iNum, const std::wstring &strDir) = 0;

    virtual DWORD GetFileList(std::vector<FILE_INFO> &vList, int &iNum, const NET_CONN_INFO &info) = 0;

    virtual DWORD GetFileListEx(std::vector<FILE_INFO> &vList, int iType, int &iNum, const NET_CONN_INFO &info) = 0;

    /*
    *@strDir: [input] Destination folder.
    *Return: If folder contains backup data, TRUE will be returned. Otherwise, FALSE will be returned.
    */
    virtual BOOL CheckFolderContainBackups(const std::wstring &strDir) = 0;

    virtual BOOL CheckFolderIsSubFolderOfBackups(const std::wstring &strDir) = 0;
};


DWORD CreateIFileListHandler(IFileListHandler **ppIFileList);

#define OPTION_VM_BROWSEINFO_GET_FOLDER				1
#define OPTION_VM_BROWSEINFO_CREATE_FOLDER			2

typedef struct _st_VM_BrowseInfo
{
	std::wstring		strVMHost_Name;
	std::wstring		strVMHost_User;
	std::wstring		strVMHost_Pwd;
	int					VMHost_Port;

	std::wstring		strVM_Name;
	std::wstring		strVM_User;
	std::wstring		strVM_Pwd;
	std::wstring		strVM_VMXPath;
	std::wstring		strVMInstGUID;

	int					option;
	std::wstring		strParent;
	std::wstring		strTobeCreatedFolder;
}VM_BROWSEINFO,*PVM_BROWSEINFO;


class IBrowseVMHierarchy
{
public:
   virtual void Release() = 0;
public:
	virtual DWORD Browse_VM_Folders(std::vector<FILE_INFO> &vList,VM_BROWSEINFO& vmInfo) = 0;
};
DWORD CreateIBrowseVMHierarchy(IBrowseVMHierarchy** ppIBrowseVM);
/*
* Begin restore point definition
*/
typedef struct _BACKUP_ITEM
{
    DWORD dwMajVer; //Major version.
    DWORD dwMinVer; //minor version.
    std::wstring strType; //volume, application
    std::wstring strDisplayName; //volume mount point or application name
    std::wstring strGuid;
    std::wstring strVolDataSizeB; //volume data size in bytes.
    std::wstring strSubSesId; //sub session id.
    std::wstring strCalalogFile; //full path for catalog file.
    BOOL bIsBootVol; //only valid for volume.
    BOOL bIsSysVol; //only valid for volume.
    BOOL bIsRefsVol; //only valid for volume.
    BOOL bIsNTFSVol; //only valid for volume.
    BOOL bIsDedupVol; //only valid for volume.
	BOOL bContainReplica;
}BACKUP_ITEM, *PBACKUP_ITEM;

typedef std::vector<BACKUP_ITEM> VBACKUP_ITEM;

typedef struct _RESTORE_POINT_ITEM
{
    DWORD dwMajVer;
    DWORD dwMinVer;
    DWORD dwFsCatStatus; //fs catalog status zouyu01 on 2011-5-13
    DWORD dwBKSetFlag;  //ZZ: Backup set flag. 0: not a backup set, 1: backup set.
    DWORD dwBKAdvSchFlag; //ZZ: Advance schedule flag: repeat, daily, weekly and monthly.
	// distinguish agent, guest os, VM hypervisor type. [9/11/2014 zhahu03]
	DWORD dwAgentOSType;		// E_NODE_OS_TYPE: Windows/Linux/Unix/Mac...
	DWORD dwAgentBackupType;	// E_BACKUP_TYPEE: BT_LOCAL_D2D / EBT_HBBU
	DWORD dwVMGuestOsType;		// E_NODE_OS_TYPE, just valid when dwAgentBackupType==EBT_HBBU.
	DWORD dwVMHypervisor;		// HYPERVISOR_TYPE_ESX/HYPERVISOR_TYPE_HYPERV/HYPERVISOR_TYPE_VCLOUD, just valid when dwAgentBackupType==EBT_HBBU.
	//
	ULONGLONG ullScheduledTime;
    std::wstring strBakName;
    std::wstring strId; //job ID.
    std::wstring strSessionGUID; //session GUID.
	std::wstring strNodeUuid;	 //Node ID
    std::wstring strDetailTime;  //06:20:10
    std::wstring strCpyDate; //copy recover point date.
    std::wstring strCpyTime; //copy recover point time.
	std::wstring strBakLocalTime; // the backup local time
    std::wstring strBakStatus; //success, failed or crashed.
    std::wstring strBakType; //full, incremental.
    std::wstring strDataSizeKB; //total backup data size. //2009-11-9, this is bytes now.
	std::wstring strTransferDataSizeKB; //total transfer backup data size. this is bytes now.
    std::wstring strCatalogSizeB; //2009-12-18, catalog size.
	std::wstring strCommonPathSize;
	std::wstring strGrtCatalogSize;
    std::wstring strPath; //Relative path for this recovery point.
	std::wstring strBakDest; // the backup destination
    std::wstring strEncryptType;
    std::wstring strEncryptPasswordHash;
    std::wstring strDataStoreKeyHash;

	//added for vm recovery
	std::wstring strVMName;
	std::wstring strVMvCenter;
	std::wstring strVMEsxHost;

    VBACKUP_ITEM vBakItem;
}RESTORE_POINT_ITEM, *PRESTORE_POINT_ITEM;

typedef std::vector<RESTORE_POINT_ITEM> VRESTORE_POINT_ITEM;

typedef struct _RESTORE_POINT
{
    std::wstring strDate;  //2009-07-10
    VRESTORE_POINT_ITEM vRestPointItem;

}RESTORE_POINT, *PRESTORE_POINT;

typedef std::vector<RESTORE_POINT> VRESTORE_POINT;

class IRestorePoint
{
public:
    virtual void Release() = 0;

    /*
    *@vRestPoint: [output] All restore points in destination path.
    *Return: If function succeeds, 0 will be resturned, other wise, system error code will be returned.
    */
    virtual DWORD GetRestorePoints(VRESTORE_POINT &vRestPoint) = 0;

	//get restore pont by specific session guid
	virtual BOOL GetRestorePointByGuid(IN  CONST std::wstring& strSessionGUID, OUT RESTORE_POINT_ITEM& restPoint, OUT wstring &strDate) = 0; //<huvfe01>2014-8-20 browse vApp childs
    /*
    *@strDate: [input] Backup date like '2009-07-10'
    *@restPoint: [output] restore point.
    *Return: If function succeeds, 0 will be resturned, other wise, system error code will be returned.
    */
    virtual DWORD GetRestorePoints(const std::wstring &strDate, RESTORE_POINT &restPoint) = 0;

    /*
    *@strStartDate: [input] Backup start date like '2009-7-10'.
    *@strEndDate: [input] Backup end date like '2009-07-11'
    *@vRestPoint: [output] restore points between start date and end date.
    *Return: If function succeeds, 0 will be resturned, other wise, system error code will be returned.
    */
    virtual DWORD GetRestorePoints(const std::wstring &strStartDate, const std::wstring &strEndDate, VRESTORE_POINT &vRestPoint, BOOL bDetail = TRUE, BOOL bByUTCTime=TRUE) = 0;

    virtual BOOL CheckRestorePointExist() = 0;

    virtual DWORD GetBackupItem(const std::wstring &strDest, const std::wstring &strSubPath, VBACKUP_ITEM &vItem) = 0;

    virtual DWORD GetRestorePointPath(const std::wstring &strSubPath, std::wstring &strSessPath) = 0;

	virtual DWORD GetVMInfo(const std::wstring &strSubPath, OUT VMInfoXml& vmInfo) = 0;
};

/*
*@strDestPath: [input] Backup destination path.
*@ppIRestorePoint: [output] restore point interface handler.
*Return: If function succeeds, 0 will be resturned, other wise, system error code will be returned.
*/
//DWORD WINAPI CreateIRestorePoint(const std::wstring &strDestPath, IRestorePoint **ppIRestorePoint);

DWORD WINAPI CreateIRestorePoint(const NET_CONN_INFO &info, IRestorePoint **ppIRestorePoint);

DWORD WINAPI ASBU_CreateIRestorePoint(LPCWSTR lpszDestination, IRestorePoint **ppIRestorePoint);
/*
* End restore point definition
*/

//
// Enum backup destinations under specified path.
// NOTE: This API is NOT used to enumerate backup destinations under datastore
//
DWORD WINAPI AFIEnumBackupDestinations( const NET_CONN_INFO &info, std::vector<wstring>& vecBackupDestinations );

//
// Detect if the given path is under datastore.
// NOTE: Only check the current and the parent path
//
BOOL WINAPI AFIsPathUnderDatastore( const wstring& strPath );

/*
* Begin Backup statistic definition.
*
*/

//definition for backup destination information.
typedef struct _BACKUP_DEV_INFO
{
    std::wstring strFullDataSize; //full backup data size in KB.
    std::wstring strIncDataSize;  //incremental backup data size in KB.
    std::wstring strResyncDataSize; //re-sync backup data size in KB.
    std::wstring strCatalogSize; //total catalog file size in Bytes.
    std::wstring strFreeSpace; //Free size in bytes.
    std::wstring strTotalSpace; // Total size of the volume in bytes.

}BACKUP_DEV_INFO, *PBACKUP_DEV_INFO;


// definition for Node os type
typedef struct _NODE_OS_INFO
{
	// distinguish agent, guest OS, VM hypervisor type. [9/11/2014 zhahu03]
	DWORD dwAgentOSType;		// E_NODE_OS_TYPE: Windows/Linux/Unix/Mac...
	DWORD dwAgentBackupType;	// E_BACKUP_TYPEE: BT_LOCAL_D2D / EBT_HBBU
	DWORD dwVMGuestOsType;	// E_NODE_OS_TYPE, just valid when dwAgentBackupType==EBT_HBBU.
	DWORD dwVMHypervisor;	// HYPERVISOR_TYPE_ESX/HYPERVISOR_TYPE_HYPERV/HYPERVISOR_TYPE_VCLOUD, just valid when dwAgentBackupType==EBT_HBBU.
}NODE_OS_INFO, *PNODE_OS_INFO;


//definition for 'protection summary'
typedef struct _DATA_PROTECTION
{
    std::wstring strType; //full, incremental, resync.
    std::wstring strCount; //number of this protection.
	std::wstring strTotalLogicalSize; //data size of backup in KB before compression / deduplication
    std::wstring strTotalSize; //total size of backup in KB.
    std::wstring strLastBackupTime; //the latest backup time. like '2009-01-20 12:10:10'
	std::wstring strFirstBackupTime; //the first backup time. like '2009-01-20 12:10:10'

}DATA_PROTECTION, *PDATA_PROTECTION;

typedef std::vector<DATA_PROTECTION> VDATA_PROTECTION;

//definition for 'most recent backups'
typedef struct _BACKUP_INFO
{
    DWORD dwMajVer; //major version.
    DWORD dwMinVer;//minor version.
    DWORD dwCatalogFlag; //1 has catalog, 0 has no catalog.
    DWORD dwBKSetFlag;  //ZZ: Backup set flag. 0: not a backup set, 1: backup set.
    DWORD dwBKAdvSchFlag; //ZZ: Advance schedule flag: repeat, daily, weekly and monthly.
    std::wstring strBackupName; //backup name
    std::wstring strStatus; //success or failed.
    std::wstring strType; //full, incremental, resync.
    std::wstring strDate; //2009-07-10.
    std::wstring strDetailTime; //06:10:56
    std::wstring strSize; // size in KB.
	std::wstring strLogicalDataSize; // size in B
    std::wstring strSessionID; // size in KB.
	std::wstring strBakDest; // the backup destination
}BACKUP_INFO, *PBACKUP_INFO;

typedef std::vector<BACKUP_INFO> VBACKUP_INFO;

//backup status
#define BACKUP_STATUS_SUCCESS 0x01
#define BACKUP_STATUS_FAILED  0x02
#define BACKUP_STATUS_ACTIVE  0x04
#define BACKUP_STATUS_CRASHED 0x08
#define BACKUP_STATUS_CANCELED 0x10
#define BACKUP_STATUS_MISSED   0x20
#define BACKUP_STATUS_ALL     (BACKUP_STATUS_SUCCESS|BACKUP_STATUS_FAILED|BACKUP_STATUS_ACTIVE|BACKUP_STATUS_CRASHED|BACKUP_STATUS_CANCELED|BACKUP_STATUS_MISSED)

#define BACKUP_TYPE_FULL   0x01
#define BACKUP_TYPE_INC    0x02
#define BACKUP_TYPE_RESYNC 0x04
#define BACKUP_TYPE_ALL    (BACKUP_TYPE_FULL|BACKUP_TYPE_INC|BACKUP_TYPE_RESYNC)

/*
*@summary filter
*Notes:
1. If strStartDate and strEndDate are null, date information is ignored.
2. If strStartDate is not null, backup summary whose date >= strStartDate will be returned.
3. If strEndDate is not null, backup summary whose date <= strEndDate will be returned.
4. If strStartDate == strEndDate, backup summary whose date == strStartDate will be returned.
*/
typedef struct _SUMM_FILTER
{
    DWORD dwStatus;  //bit map for backup status, can be 'or'. BACKUP_STATUS_SUCCESS | BACKUP_STATUS_FAILED
    DWORD dwType; //bit map for type. can be 'or'. BACKUP_TYPE_FULL|BACKUP_TYPE_INC
    std::wstring strStartDate; //start date for backup
    std::wstring strEndDate; //end date for backup.

}SUMM_FILTER, *PSUMM_FILTER;

class IBackupSumm
{
public:
    virtual void Release() = 0;

    //Get the total number of restore points for current machine.
    virtual DWORD GetTotalNumberOfRestorePoints() = 0;

    ///ZZ: Total count of backup set.
    virtual DWORD GetTotalNumberOfRestoreSet() = 0;

    //All data protection will be returned.
    virtual DWORD GetDataProtectionSumm(VDATA_PROTECTION &vDataProtect) = 0;

    //Data protection information which meets SUMM_FILTER will be returned.
    virtual DWORD GetDataProtectionSumm(const SUMM_FILTER filter, VDATA_PROTECTION &vDataProtect) = 0;

    //All backup information will be returned.
    virtual DWORD GetBackupInfoSumm(VBACKUP_INFO &vBakInfo,const wchar_t* subFolderName = NULL) = 0;

    //Number of iCount backup information which meets SUMM_FILTER will be returned. 
    //If iCount=-1, backup information which meets SUMM_FILTER will be returned as many as possible.
    //vBakInfo.size() contains actual number of backup information.
    //Note: this function only calculate history recover points.
    virtual DWORD GetRecentBackupInfo(const SUMM_FILTER filter, VBACKUP_INFO &vBakInfo, int iCount = -1, const wchar_t* subFolderName = NULL) = 0;

    //Get backup destination volume information.
    //If input strDestPath is null, the path specified in CreateIBackupSumm will be used.
    virtual DWORD GetBackupDevInfo(const std::wstring &strDestPath, BACKUP_DEV_INFO &bakDevInfo) = 0;

    virtual DWORD GetBackupDevInfo(const NET_CONN_INFO *pInfo, BACKUP_DEV_INFO &bakDevInfo) = 0;

    virtual DWORD GetActiveBackupDataSize(const std::wstring &strDest, DWORD &dwJobMethod, unsigned long long &ulSess, unsigned long long &ulCatalog) = 0;

    virtual DWORD GetLastSuccessfullCpyTime(std::wstring &strDateTime) = 0;

	virtual DWORD GetTotalNumberOfRestorePointsByCategory(DWORD dwCategory) =0;

	virtual DWORD GetNodeOSInfo(NODE_OS_INFO& osInfo) = 0;
};

/*
*@strDestPath: Destination path.
*/

DWORD WINAPI CreateIBackupSumm(const NET_CONN_INFO &info, IBackupSumm **ppIBackupSumm);

//Get history backup information, the history backup information is saved under 'C:\Program Files\CA\ARCserve D2D\BackupHistory'.
DWORD WINAPI GetHistoryBackupInfo(const SUMM_FILTER &filter, VBACKUP_INFO &vBakInfo, int iCount = -1);

//DWORD WINAPI CreateIBackupSumm(const std::wstring &strDestPath, IBackupSumm **ppIBackupSumm);

/*
*
* End Backup statistic definition.
*/

DWORD WINAPI AFGetD2DNodeInfo( LPCWSTR lpszBackupDest, PD2D_NODE_INFO_EX pNodeInfo);
/*
*Purpose: Initialize and valid backup destination.
*@pNewDest: current backup destination which user specified.
*@pOldDest: the last backup destination.
*Return: If function succeeds, 0 will be returned. otherwise, system error code will be returned.
*/
DWORD WINAPI AFInitDestination(const NET_CONN_INFO *pNewDest, const NET_CONN_INFO *pOldDest = NULL, DWORD dwBakType = 1, BOOL bCopy = FALSE);

DWORD WINAPI AFCheckFolderAccess(const NET_CONN_INFO *pDest, FILE_INFO &info);

DWORD WINAPI AFRetrieveSharedResource(const NET_CONN_INFO *pDest, std::vector<std::wstring> &vShare);

/*
*Purpos: Get volume type where backup destination exists
*@pszDest: [INPUT] Backup destination path.
*@type: [output] The volume type of backup destination.
VOL_SYSTEM: the backup destination is on a system volume. This is not allowed for DR.
VOL_BOOT: the backup destination is on a boot volume. This is not allowed for DR.
VOL_DYNAMIC: the backup destination is on a dynamic volume. This is an warning for DR.
VOL_BASIC: the backup destination is on a basic volume.
VOL_NETWORK: the backup destination is on a remote shared folder.
*Return: 
If function succeeds, zero will be returned.
If function fails, non-zero code will be returned and type will be set to VOL_UNKNOWN.
*/

typedef enum BAK_DEST_VOL_TYPE
{
    VOL_UNKNOWN = 0,
    VOL_SYSTEM,
    VOL_BOOT,
    VOL_BASIC,
    VOL_DYNAMIC,
    VOL_NETWORK
};

DWORD WINAPI AFGetDestinationVolumeType(const wchar_t *pszDest, BAK_DEST_VOL_TYPE &type);

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

BOOL WINAPI AFEncryptString(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen);

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
BOOL WINAPI AFDecryptString(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen);

BOOL WINAPI AFEncryptStringEx(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen);
BOOL WINAPI AFDecryptStringEx(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen);


BOOL WINAPI AFCheckCompressLevelChanged(const NET_CONN_INFO *pInfo, int iLevel);

BOOL WINAPI AFCheckVhdFormatChanged(const NET_CONN_INFO *pInfo, int iLevel);

BOOL WINAPI AFGetJobId(DWORD *pStartId, DWORD dwCount = 1);

BOOL WINAPI AFGetErrorMsg(DWORD dwErr, std::wstring &strMsg);

BOOL WINAPI AFCheckJobExist();

DWORD WINAPI AFGetHostFQDN(std::wstring &strHostFQDN); //get local host FQDN path

//<sonmi01>2011-1-20 ###???
BOOL WINAPI AFCheckRecoveryVMJobExist(CONST WCHAR * pszHypervisor, CONST WCHAR * pszVMName);


BOOL WINAPI AFCheckArchivePurgeJobExist();
BOOL WINAPI AFCheckArchiveRestoreJobExist();
BOOL WINAPI AFCheckArchivebackupJobExist();
BOOL WINAPI AFCheckArchiveCatalogSyncJobExist();
BOOL WINAPI AFCheckFileCopyBackupJobExist2(wchar_t* pD2DMachineName);
BOOL WINAPI AFCheckArchiveRestoreJobExist2(wchar_t* pD2DMachineName);
BOOL WINAPI AFCheckArchivePurgeJobExist2(wchar_t* pD2DMachineName);
BOOL WINAPI AFCheckArchiveCatalogSyncJobExist2(wchar_t* pD2DMachineName);
BOOL WINAPI AFCheckFileArchiveBackupJobExist2(wchar_t* pD2DMachineName);
//Return: If there is no job running, 0 will be returned.
//        else non-zero value will be returned.
DWORD WINAPI AFGetCurrentJobId();

/************************Retrieve subsession size. Used for Jie**************************/
BOOL WINAPI AFGetSubSessSize(const wchar_t *pDest, DWORD dwSessNo, DWORD dwSubSessNo, unsigned long long *pSubSessSize);

//convert dos time to UTC time(1970, 0:0:0)
BOOL WINAPI AFConvertDosTimeToUTC(long *pTime);

//Cancel job
DWORD WINAPI AFCancelJob(DWORD dwJobId, PWCHAR pwszNodeName = NULL);
DWORD WINAPI AFUpdateThrottling(DWORD dwJobId, PWCHAR pwszNodeName, DWORD dwThrottling);

BOOL WINAPI GetRPSRegRootPathByProduct(wstring &wstr);

BOOL WINAPI RPSDecryptStrWithEarlyVersion(const wchar_t *pszStr, wchar_t* pszBuf, DWORD *pBufLen);

BOOL WINAPI RPSEcryptStrWithEarlyVersion(const wchar_t *pszStr, wchar_t* pszBuf, DWORD *pBufLen) ;


#include "JobMonitor.h" //<sonmi01>2013-3-22 #rps job monitor and eliminate the redefinitions 
typedef  IJobMonitor IAFJobMonitor;

#if 0
class IAFJobMonitor
{
public:
    virtual DWORD Read(PJOB_MONITOR pJM) = 0;
    virtual DWORD Write(const PJOB_MONITOR pJM) = 0;
    virtual DWORD AFJobUpdate(const PJOB_MONITOR pJM) = 0;
    virtual PJOB_MONITOR GetMapView() = 0;
    virtual VOID Destroy() = 0;
    virtual DWORD GetJobID() = 0;
    virtual bool IsOpenExist() = 0;
	//luoca01: add this function to update job type when starting a job, to fix issue 145148
	virtual VOID UpdateJobType(ULONG ulJobType) = 0;
};

// following 3 apis are defined in jobmonitor.h
DWORD WINAPI CreateIJobMonitor(DWORD dwShrMemID, IAFJobMonitor **ppIAFJobMonitor, const WCHAR* pwzPrefix = NULL);
DWORD WINAPI CreateIJobMonitorByName(PWCHAR pwszNodeName, IAFJobMonitor **ppIAFJobMonitor, const WCHAR* pwzPrefix = NULL);
VOID  WINAPI DestroyIJobMonitor(IAFJobMonitor **ppIAFJobMonitor);
#endif  

typedef struct _SQL_INSTANCE_INFO
{
    DWORD dwVersion;
    std::wstring strInstanceName;

} SQL_INSTANCE_INFO, *PSQL_INSTANCE_INFO;

DWORD WINAPI AFGetSQLInstance(std::vector<SQL_INSTANCE_INFO> &vList);

wstring WINAPI AFBackupGetOSNameEx(void);

void WINAPI AFGetPathMaxLength(DWORD *pLen);

void WINAPI AFGetVSpherePathMaxLength(DWORD *pLen);

DWORD WINAPI AFCheckDestValid(const wchar_t *pDir);

DWORD WINAPI AFCheckDestContainRecoverPoint(const NET_CONN_INFO &info);


DWORD WINAPI AFCheckDestNeedHostName(const std::wstring &strPath, std::wstring &strHostName,const wchar_t* uuid=NULL, const wchar_t* nodeID=NULL, BOOL bCreateFolder=TRUE);

DWORD WINAPI AFInitDestForCopySes(const NET_CONN_INFO &info, const std::wstring &strHostName, const std::wstring &strDateTime, NET_CONN_INFO &dest);

DWORD WINAPI AFSaveAdminAccount(const std::wstring &strUser, const std::wstring &strPwd);

DWORD WINAPI AFReadAdminAccount(std::wstring &strUser, std::wstring &strPwd);

DWORD WINAPI AFCheckAdminAccountValid(const std::wstring &strUser, const std::wstring &strPwd);

BOOL WINAPI AFCheckBLILic();

DWORD WINAPI AFGetBakDestDriveType(const NET_CONN_INFO &info, DWORD &dwType);

DWORD WINAPI AFCreateConnection(const NET_CONN_INFO &info);

DWORD WINAPI AFCutConnection(const NET_CONN_INFO &info, BOOL bForce = TRUE);

DWORD WINAPI AFCreateDir(const std::wstring &strParent, const std::wstring &strSubFolder);

DWORD WINAPI AFCreateAllDirs(const std::wstring &strFullFolder);

BOOL WINAPI AFCheckPathAccess(const NET_CONN_INFO &info);

DWORD WINAPI AFGetThreshold(unsigned long long &ulThreshold);

DWORD WINAPI AFSetThreshold(unsigned long long ulThreshold);

DWORD WINAPI AFTryToFindDest(const std::wstring &strOri, std::wstring &strFindDest);

#define LENGTH_USER_NAME 21

typedef struct _PARAM_CHK_REST_DEST
{
    NET_CONN_INFO dest;
    wchar_t srcHostName[MAX_PATH];
    wchar_t srcUserName[LENGTH_USER_NAME];
    DWORD dwDisconnect;
    wchar_t szRemoteRoot[MAX_PATH]; //use this to disconnect the connection later.
    DWORD dwCreateSubFolderFlag;
    DWORD dwRest;  //used for restore or dump.
}PARAM_CHK_REST_DEST, *PPARAM_CHK_REST_DEST;

DWORD WINAPI AFCheckRestDestValid(const PARAM_CHK_REST_DEST &restDest);

BOOL WINAPI AFCheckBaseLic();

///<ZZ[zhoyu03: 2010/01/15]: For gathering excluded files according to selected volumes. 
class CExcludedComponent
{
public:
    std::wstring              m_wsCompDesc;
    std::vector<std::wstring> m_vecExcludedFile;
    std::vector<std::wstring> m_vecAffectMnt;
};
typedef std::vector<CExcludedComponent> ExcludedCompVector;

class CExcludedWriter
{
public:
    std::wstring              m_wsWriterName;
    std::wstring              m_wsWriterID;
    ExcludedCompVector        m_vecExcludedComp;
    std::vector<std::wstring> m_vecAffectMnt;
};
typedef std::vector<CExcludedWriter> ExcludedWriterVector;

long WINAPI GatherExcludedFileListInWriter(
    std::vector<std::wstring>& vecVolumeList, 
    ExcludedWriterVector& vecExcludedItemList);

long WINAPI GatherWriterMetadataForWriter();
////////////////////////////////////////////////////////////////////////////////////////

///<ZZ[zhoyu03: 2010/01/26]: Add volume information detail.
typedef enum eVolumeLayoutType
{
    EVLT_UNKNOWN = 0, // Unknown Type
    EVLT_SIMPLE,        
    EVLT_MIRROR, 
    EVLT_SPANNED,
    EVLT_STRIPPED, 
    EVLT_RAID5
}E_VOLLAYOUT_TYPE;

typedef enum eVolumeType
{
    EVT_UNKNOWN = 0,    // Unknown Type
    EVT_BASIC,
    EVT_DYNAMIC
}E_VOLUME_TYPE;

typedef enum eFileSystemType
{
    EFST_UNKNOWN = 0,   // Unknown Type
    EFST_RAW,           // Raw volume.
    EFST_NTFS,
    EFST_FAT16,
    EFST_FAT32,
    EFST_EXFAT,
    EFST_TEXFAT,
    EFST_HPFS,
    EFST_ReFS
}E_FILESYSTEM_TYPE;

typedef enum eVolumeStatus
{
    EVS_UNKNOWN = 0,             // Unknown Type
    EVS_HEALTHY = 1,
    EVS_FAILED = 2,
    EVS_FAILED_REDUNDANCY = 3,
    EVS_FORMATTING = 4,
    EVS_REGENERATING = 5,
    EVS_RESYNCHING = 6
}E_VOLUME_STATUS;

typedef enum eVolumeSubStatus
{
    EVSS_UNKNOWN = 0,              // Unknown Type
    EVSS_APP_SQL = 0x00000001,
    EVSS_APP_EXCH = 0x00000002,
    EVSS_SYSTEM = 0x00010000,
    EVSS_BOOT = 0x00020000,
    EVSS_PAGEFILE = 0x00040000,
    EVSS_ACTIVE = 0x00080000,
    EVSS_REMOVABLE = 0x00100000,
    EVSS_MOUNTEDFROMVHD = 0x00200000,
    EVSS_VOLUMEON2TDISK = 0x00400000,  // Volume on a disk more than 2TB
	EVSS_RECOVERYVOLUME = 0x00800000  //Recovery Volumn on win8\2012 ,fanda03
}E_VOLUME_SUBSTATUS;

typedef enum eDriveType
{
    EDT_DRIVE_UNKNOWN = 0,              // The drive type cannot be determined.
    EDT_DRIVE_NO_ROOT_DIR,              // The root directory does not exist.
    EDT_DRIVE_REMOVABLE,                // The drive can be removed from the drive.
    EDT_DRIVE_REMOVABLE_FLOPPY,
    EDT_DRIVE_FIXED,                    // The disk cannot be removed from the drive.
    EDT_DRIVE_FIXED_USB_REMOVABLE_DISK, // The drive 
    EDT_DRIVE_REMOTE,                   // The drive is a remote (network) drive.
    EDT_DRIVE_CDROM,                    // The drive is a CD-ROM drive. 
    EDT_DRIVE_RAMDISK                   // The drive is a RAM disk.
}E_DRIVE_TYPE;

typedef enum eVolumeGrayedReason
{
    EVGR_UNKNOWN = 0,
    EVGR_VOL_ON_BACKUP_DEST_CHAIN, // Volume is grayed because it is in backup destination chain.
    EVGR_VOL_NOT_SUPPORTED         // Volume is not supported for backup, because such reason: 1. Not NTFS, 2. RAID-5, 3. Removable device
}E_VOLGRAYED_REASON;
////////////////////////////////////////////////////////////////////////////////////////

DWORD GetDestSize(const std::wstring &strDir, unsigned long long &ulTotal, unsigned long long &ulFree);

DWORD WINAPI AFGetLocalDestVolumes(LPCWSTR szDestPath, std::vector<std::wstring> &vGuid); //<sonmi01>2010-2-11 Get Local Destination Volumes for JAVA


//Retrieve network path for mapped drive.
DWORD WINAPI AFGetNetworkPathForMappedDrive(const std::wstring &strUser, std::vector<MAPPED_DRV_PATH> &vDrvPath);

BOOL WINAPI AFCheckSessExist(const std::wstring &strDest, DWORD dwNum);

//@Purpose: Retrieve license information.
//@vols:[INPUT] volumes selected by user. Must be in drive letters, such as c:, d: or e:.
void WINAPI AFGetLicenseInfo(std::vector<std::wstring> &vols, LICENSE_INFO &licInfo);

//@Purpose: Retrieve license information.
//@vols:[INPUT] volumes selected by user. Must be in drive letters, such as c:, d: or e:.
//@bFilterLicInfo:[INPUT] indicates if the license info is filtered for UI. if it is TRUE, license info is filtered for 
//						  summary pannel licnese summary; if it is FALSE, all license info is returned as is
void WINAPI AFGetLicenseInfoEx(std::vector<std::wstring> &vols, LICENSE_INFO &licInfo, BOOL bFilterLicInfo = TRUE);

void WINAPI AFGetLicenseErrorInfo(LICENSE_INFO &licInfo);

#define LICSTATUS_SUCCESS			0		// license stats is ok
#define LICFAILED_WORKSTATION		1		// need workstation license
#define LICFAILED_STANDARD_SOCKET	2		// need STANDARD_Per_SOCKET license
#define LICFAILED_ADVANCED_SOCKET	3		// need ADVANCED_Per_SOCKET license
#define LIC_USING_TRIAL				4		// is using trial license  
#define LIC_USING_FREE_EDITION		0x10	

DWORD WINAPI AFGetLicenseStatus(BOOL IsUnderCPM);

BOOL WINAPI AFDeleteLicenseError(DWORD dwLicenseId);

DWORD WINAPI AFGetMntFromPath(const std::wstring &strPath, std::vector<std::wstring> &vMnt);
// add by wanmi12 for collecting Architecture of ms exchange <
//@Function: D2DExCheckUserW
//@brief: check the domain user whether belong to both Domain & local Admins group
//@param:
//	pwszDomain		-[in] the domain name 
//	pwszAdmin		-[in] the user name
//	pwszPassword	-[in] the passworld 
//@return S_OK success,otherwize  false
/*
#define  D2DCKUSER_OK	0
#define  D2DCKUSER_FAIL		0xFFF00000
#define  D2DCK_E_VALIDARG	(D2DCKUSER_FAIL + 0x1)
#define  D2DCK_E_LOGON		(D2DCKUSER_FAIL + 0x2)
#define  D2DCK_E_GETTINFO	(D2DCKUSER_FAIL + 0x3)
#define  D2DCK_E_PRIVILEGE	(D2DCKUSER_FAIL + 0x4)
#define  D2DCK_E_OUTOFMEM	(D2DCKUSER_FAIL + 0x5)
*/
//
HRESULT  WINAPI D2DExCheckUserW(const wchar_t * pwszDomain,  const wchar_t * pwszAdmin, const wchar_t * pwszPassword);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOEInit()
Brief:	Initialize the envrionment
Parameter: No
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOEInit();

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOEUninit()
Brief:	un-Initialize the envrionment
Parameter: No
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOEUninit();

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOEGetServers()
Brief:		get all the exchange servers in the current AD
Parameter:	pstServer [out]  Pointer to a EAOBJ_SERVERLIST structure,
the server information is stored in it,the structure must 
be cleaned by invoking AOEFreeServers() when don't use it.
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOEGetServers(PEAOBJ_SERVERLIST OUT pstServer,  int nServerType);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOEFreeServers()
Brief:		clean the result returned by  AOEGetServers()
Parameter:  pstServer -[in] Pointer to a EAOBJ_SERVERLIST structure that
was previously returned by AOEGetServers(). 
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOEFreeServers(PEAOBJ_SERVERLIST IN pstServer);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOEGetSGroup()
Brief:		get the information  of storage group ,the function is valid 
for ms exchange 2003, 2007,but invalid for ms exchange 2010
Parameter:  pwszDN	 -[in] the DN of ms exchange server, the DN is the 
the member pwszDN of structure EAOBJ_SERVER.
pstGroup -[out]  Pointer to a ADOBJ_LIST structure,the  group
information is stored in it,the structure must be cleaned by 
invoking AOEGetSGroup() when don't use it.
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOEGetSGroup(const wchar_t * IN pwszDN, PADOBJ_LIST OUT pstGroup);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOEFreeSGroup() 
Brief:		clean the result returned by  AOEGetSGroup()
Parameter:	pstGroup -[in] Pointer to a ADOBJ_LIST structure that
was previously returned by AOEGetSGroup. 
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOEFreeSGroup(PADOBJ_LIST IN pstGroup);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOEGetEDBs()	 
Brief:		get all the edb files that belong to current Storage group
or current ms exchange server.
Parameter:	pwszDN	 -[in] the DN of ms exchange server, the DN is the 
the member pwszDN of structure EAOBJ_SERVER or structure 
EAOBJ_GROUPITEM.
pstEDBs -[out]  Pointer to a ADOBJ_LIST structure,the  edb
information is stored in it,the structure must be cleaned by 
invoking AOEFreeEDBs() when don't use it.
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOEGetEDBs(const  wchar_t * IN pwszDN, PADOBJ_LIST OUT pstEDBs);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOEFreeEDBs() 
Brief:		
Parameter:  pstEDBs-[in] Pointer to a ADOBJ_LIST structure that  was
previously returned by AOEGetEDBs.
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOEFreeEDBs(PADOBJ_LIST IN pstEDBs);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOEGetMailbox()
Brief:	 
Parameter: No
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOEGetMailbox(const wchar_t * pwszDN, PEAOBJ_MBLIST OUT pstMBs);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOEFreeMailbox()
Brief:	 
Parameter: No
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOEFreeMailbox(PEAOBJ_MBLIST IN pstMBs);

// add by wanmi12 for collecting Architecture of ms exchange >
//////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////
/*
Function:	AOECheckServiceStatus()
Brief:
check the service status including whether the service is installed and 
whether the service is in running statusby service name 

Parameter: 
pszServiceName  -[in]  service name
isInstalled		-[out] whether is  the service installed in current system
isRunning		-[out] whether is  the service installed in running status

Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AOECheckServiceStatus(const wchar_t * pszServiceName, BOOL * isInstalled, BOOL  * isRunning);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AHDesktopFile()
Brief:		Create the desktop.ini file after the gui select the destination path.

Parameter:  pszDesPath -[in] the path selected by GUI
Return Value: S_OK successful, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
HRESULT WINAPI AHDesktopFile(const wchar_t * pszDesPath);

DWORD WINAPI AFCQueryMountedDisk(OUT MountedDiskCollection ** ppMountedDiskCollection);
DWORD WINAPI AFCFreeMountedDisk(IN  MountedDiskCollection * pMountedDiskCollection);
DWORD WINAPI AFCGRTSkipDisk(IN const PWCHAR pwzVolName, OUT BOOL * pbSkipped);


DWORD WINAPI AOEGetOrgBaseinfo(PEAOBJ_ORG_BASEINFO OUT pstORG);
DWORD WINAPI AOEFreeOrgBaseinfo(PEAOBJ_ORG_BASEINFO IN pstORG);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AEGetE15CAS()
Brief:		Get the defalut Client Access Sever Name.

Parameter:  strServerName -[out] the name
Return Value: 0 sucess, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
DWORD WINAPI AEGetE15CAS(wstring & strServerName);

//////////////////////////////////////////////////////////////////////////
/*
Function:	AESetE15CAS()
Brief:		Set the defalut Client Access Sever Name.

Parameter:  strServerName -[in] the CAS name
Return Value:  0 sucess, otherwise failed
*/
/////////////////////////////////////////////////////////////////////////
DWORD WINAPI AESetE15CAS(wstring strServerName );


BOOL WINAPI AFValidateSessPassword(const WCHAR* pwzSessPwd, const WCHAR* pwzBKDest, DWORD dwSessNum);

BOOL WINAPI AFValidateSessPasswordByHash(const WCHAR* pwszPwd, DWORD dwPwdLen, const WCHAR* pwszPwdHash, DWORD dwHashLen);

DWORD WINAPI AFRetriveSessPwdByKeyMgmt(WCHAR* pwzSessPwd, DWORD* pdwSessPwdLen, const WCHAR* pwzSessGUID);

DWORD WINAPI AFIGetBackupDetails(BackupInfoXml& BackupDetail, const WCHAR* pwzBKDest, DWORD dwSessNum);

DWORD WINAPI AFIGetSessPwdFromKeyMgmtBySessNum(WCHAR* pwzSessPwd, DWORD* pdwSessPwdLen, const WCHAR* pwzBKDest, DWORD dwSessNum);

DWORD WINAPI AFIUpdateAdminAccountInKeyMgmtDB(const WCHAR* pwzAdminUser, const WCHAR* pwzAdminPwd);

DWORD WINAPI AFIGetMultiSessPwdFromKeyMgmtDB(std::vector<CSessPwdWrap>& vecMutiSessPwd);

DWORD WINAPI AFCheckAndRunSessScanProcess(NET_CONN_INFO& stBKDestInfo, const WCHAR* pwzTmpPath4Scan = NULL, DWORD* pdwProcessID = NULL);

BOOL WINAPI AFIsEncInfoChanged(NET_CONN_INFO& stBKDestInfo, ST_CRYPTO_INFO& stCryptoInfo);

DWORD WINAPI GetCacheFile4Sync(std::wstring &cacheFileName);

DWORD WINAPI DeleteCacheFile4Sync();

DWORD WINAPI GetReSyncCacheFile(std::wstring &cacheFileName);

std::wstring WINAPI GetD2DSysFolder();
DWORD WINAPI AFGetArchiveJobByScheduleStatus(NET_CONN_INFO ConnectionInfo,
                                             DWORD ScheduleStatus,													 
                                             vector<ARCHIVE_ITEM_INFO>& Jobs,
                                             BOOL bFirstJobOnly	);

//sonle01: D2D sync activity log
DWORD WINAPI GetD2DActiveLogTransFileXML(wstring &transFileXML);
DWORD WINAPI GetFullD2DActiveLogTransFileXML(wstring &transFileXML);
DWORD WINAPI DelD2DActiveLogTransFileXML();

BOOLEAN WINAPI IsFirstD2DSyncCalled();
DWORD WINAPI MarkFirstD2DSyncCalled();

DWORD WINAPI AFIDeleteAllVmInfoTransFile();
DWORD WINAPI AFIDeleteVmInfoTransFile();
wstring WINAPI AFIGetAllVmInfo4Trans();
wstring WINAPI AFIGetCachedVmInfo4Trans();


/* Luoca01 - read/save VM information for restore GUI
This is an interface
*/
using namespace std;
class IVMList
{
public:
    virtual BOOL CheckDestContainBackup(const std::wstring &strDir) = 0;
    virtual DWORD ReadVMList(const std::wstring &strDir, VMInfoList &vmList) = 0;
    virtual DWORD ReadVMListPerVM(const std::wstring &strDir, VMInfoList &vmList) = 0; //<sonmi01>2011-3-17 ###???
    virtual void Release() = 0;
};

/*
*@info: [input] Backup destination path.
*@IVMList: [output] VM list interface handler.
*Return: If function succeeds, 0 will be resturned, other wise, system error code will be returned.
*Note: please remember to call Releease on IVMList to release the class.
*/
DWORD WINAPI CreateIVMList(const NET_CONN_INFO &info, IVMList **ppList);

//<sonmi01>2011-5-21 ###???
DWORD WINAPI AFGetBackupSourceVMInfo(const NET_CONN_INFO &info, ULONG ulSessionNumber, VMInfoXml& vmInfo);

//<huvfe01>2014-8-20 browse vApp childs
DWORD WINAPI AFGetBackupVAppInfo(const NET_CONN_INFO &info, ULONG ulSessionNumber, vAppInfoXml & vAppInfo);

typedef struct VMVolume_Info
{
    DWORD volType;
    std::wstring driverLetter;

}VMVOLUME_INFO, PVMVOLUME_INFO;

typedef struct VMDisk_Info
{
    DWORD dwDiskNo; //disk No.
    DWORD dwSignature; //disk signature.
    LONGLONG diskTotalSize; // the total size of the disk
    //LONGLONG vmdkSizeInKB; //<sonmi01>2011-2-28 ###???
    std::wstring diskUrl; 
    std::wstring diskDataStor;
    std::vector<VMVolume_Info> vols;
}VMDISK_INFO, PVMDISK_INFO;

typedef std::vector<VMDisk_Info> VMDiskList;

typedef struct tagHyperVVMBackupDiskInfo
{
	DWORD dwDiskNo;
	_PERSIST_DISK_INFO stPersistInfo;
	std::vector<VMVolume_Info> vols;
}HYPERV_VM_BACKUP_DISK_INFO_S;

typedef std::vector<HYPERV_VM_BACKUP_DISK_INFO_S> HyperVVMDiskList;

class IDiskVolumeMap
{
public:
	virtual DWORD GetHyperVVMBackupDiskInfo(const std::wstring &strDir, const wstring &strSubPath,HyperVVMDiskList &diskList) = 0;
    virtual DWORD GetDiskInfo(const std::wstring &strDir, const wstring &strSubPath,VMDiskList &diskList) = 0;
    virtual void Release() = 0;
};
DWORD WINAPI AFSetArchiveJobScheduleStatus(NET_CONN_INFO ConnectionInfo,
                                           DWORD ScheduleStatus,													 
                                           PAFARCHIVEJOBSCRIPT pArchiveJobJS,
                                           LARGE_INTEGER TotalArchiveSize,
                                           LARGE_INTEGER TotalFilCopySize);

/*
*@info: [input] Backup destination path.
*@IVMList: [output] Disk volume map interface handler.
*Return: If function succeeds, 0 will be resturned, other wise, system error code will be returned.
*Note: please remember to call Releease on IVMList to release the class.
*/
DWORD WINAPI CreateIDiskVolumeMap(const NET_CONN_INFO &info, IDiskVolumeMap **ppMap);

BOOL WINAPI AFCheckBMRPerformed();

//////////////////////////////////////////////////////////////////////////
//DWORD CheckSessVerByNo(const wstring &strDest, int iNum, int &iResult) = 0;
//////////////////////////////////////////////////////////////////////////
DWORD WINAPI CheckSessVerByNo(const wstring &strDest, int iNum, int &iResult);

/*
*@Purpose: check whether the give file or folder exist or not.
*@Note: If the folder which will be checked is a remote shared folder, the caller
*       must create the network connection before calling this API.
*@Return: TRUE if file/folder exists. Otherwise, FALSE will be returned and GetLastError()
*         API will return the system error code.
*/
BOOL WINAPI CheckFileFolderExist(const wstring &strFile);

DWORD WINAPI AFFileCopyBackup(PAFARCHIVEJOBSCRIPT pAFJOBSCRIPT,
	pfnUserCallProc UserCallBack,
	LPVOID lpParameter);
DWORD WINAPI AFArchive(PAFARCHIVEJOBSCRIPT pAFJOBSCRIPT,
                       pfnUserCallProc UserCallBack,
                       LPVOID lpParameter);

DWORD WINAPI AFArchiveRestore(PAFARCHIVEJOBSCRIPT pAFJOBSCRIPT,
                              pfnUserCallProc UserCallBack,
                              LPVOID lpParameter);

DWORD WINAPI AFArchiveCatalogSync(PAFARCHIVEJOBSCRIPT pAFArchiveCatalogJOBSCRIPT,
                                  pfnUserCallProc UserCallBack,
                                  LPVOID lpParameter);

DWORD WINAPI AFArchiveSourceDelete(PAFARCHIVEJOBSCRIPT pAFArchiveCatalogJOBSCRIPT,
	pfnUserCallProc UserCallBack,
	LPVOID lpParameter);

DWORD WINAPI AFArchivePurge(PAFARCHIVEJOBSCRIPT pAFJOBSCRIPT,
                            pfnUserCallProc UserCallBack,
                            LPVOID lpParameter);


DWORD WINAPI AFGetArchiveJobByScheduleStatus(NET_CONN_INFO ConnectionInfo, DWORD ScheduleStatus, vector<ARCHIVE_ITEM_INFO>& Jobs, BOOL bFirstJobOnly, DWORD dwJobType);
DWORD WINAPI AFSetArchiveJobScheduleStatus(NET_CONN_INFO ConnectionInfo, DWORD ScheduleStatus, PAFARCHIVEJOBSCRIPT pArchiveJobJS, LARGE_INTEGER TotalArchiveSize, LARGE_INTEGER TotalFilCopySize);
DWORD WINAPI AFGetLastArchiveJobStatus(NET_CONN_INFO ConnectionInfo, vector<ARCHIVE_ITEM_INFO>& Jobs, DWORD dwJobType);
DWORD WINAPI AFGetNextScheduleArchiveJob(NET_CONN_INFO ConnectionInfo, vector<ARCHIVE_ITEM_INFO>& Jobs, DWORD dwJobType);

DWORD WINAPI AFGetArchiveJobByScheduleStatus2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, DWORD ScheduleStatus, vector<ARCHIVE_ITEM_INFO>& Jobs, BOOL bFirstJobOnly, DWORD dwJobType);
DWORD WINAPI AFGetScheduleArchiveJobInfoCount2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, DWORD dwJobType, DWORD& count);
DWORD WINAPI AFGetNextScheduleArchiveJob2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, vector<ARCHIVE_ITEM_INFO>& Jobs, DWORD dwJobType);
DWORD WINAPI AFSetArchiveJobScheduleStatus2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, DWORD ScheduleStatus, PAFARCHIVEJOBSCRIPT pArchiveJobJS, LARGE_INTEGER TotalArchiveSize, LARGE_INTEGER TotalFilCopySize);
DWORD WINAPI AFGetLastArchiveJobStatus2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, vector<ARCHIVE_ITEM_INFO>& Jobs, DWORD dwJobType);
DWORD WINAPI AFGetNextFCSourceDeleteJob2(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, vector<ARCHIVE_ITEM_INFO>& Jobs, DWORD dwJobType);
BOOL WINAPI AFArchiveGetDNSHostName(LPTSTR szHostName);
//<sonmi01>2010-11-22 vddk vix version info
int  AFGetVddkVersionAPI(ULONG * pnVerHigh, ULONG * pnVerLow, ULONG * pnVerSub);
int  AFGetVixVersionAPI(ULONG * pnVerHigh, ULONG * pnVerLow, ULONG * pnVerSub);


// wanmi12 for supporting 19539409 2010 11 23<
// it's a proxy function that  will invoke  AFCheckCatalogExist() exported by AFSessMgr.dll
DWORD WINAPI AFSCheckCatalogExist(const wstring &strBakDest, DWORD dwSessNo, CATALOG_INFO_LIST &vInfo);
// wanmi12 for supporting 19539409 2010 11 23>

DWORD WINAPI AFSCheckCatalogExistEx(const wstring &strBakDest, DWORD dwSessNo, CATALOG_INFO_LIST_EX &vInfoEx);

//ZZ: APIs for catalog generation and save exchange GRT on demand 
//ZZ: This API should be called in local system security.
DWORD WINAPI AFStartCatalogGenerator(
    DWORD        dwQueueType,           // Job in which queue will be run. 1 = regular, 2 = ondemand
    DWORD        dwJobNum,              // Job number for job monitor.
    DWORD*       pdwProcID = NULL,      // Return the new process if needed.
    const WCHAR* pwzUsrName = NULL,     // User name for security requirement when start process.
    const WCHAR* pwzPassword = NULL,    // Password for security requirement when start process.
    const WCHAR* pwzJobQIdentity = NULL,  // Job queue name, Empty for local D2D, VM GUID for vsphere.
	const NET_CONN_INFO* stBKDest = NULL, //// Backup destination information, including account information for remote folder.
    const WCHAR* pwzCatalogModeID = NULL); 

DWORD WINAPI AFSaveJobScriptForExchGRT(
    const NET_CONN_INFO& stBKDest,            // Backup destination information, including account information for remote folder.
    DWORD                dwSessNum,           // Session number for specified exchange writer.
    DWORD                dwSubSessNum,        // Sub session number for specified exchange writer, if equal to zero it will handle all exchange writers
    const WCHAR*         pwzSessPWD = NULL,   // Session password for encrypted session.
    const WCHAR*         pwzJobQIdentity = NULL);  // Job queue name, Empty for local D2D, VM GUID for vsphere.

DWORD WINAPI AFSaveJobScriptForExchGRT2(
    const NET_CONN_INFO& stBKDest,           
    DWORD                dwSessNum,           
    DWORD                dwSubSessNum,         
    const WCHAR*         pwzSessPWD  ,    
    const WCHAR*         pwzJobQIdentity,
    std::vector<wstring> & vecEDBlist); 

DWORD WINAPI AFSaveJS4FSOndemand(
    const NET_CONN_INFO& stBKDest,
    DWORD dwSessNum, 
    const WCHAR* pwzJobQIdentity = NULL,
    DWORD dwSubSessNum = 0,
    const WCHAR* pwzSessPWD = NULL);

DWORD WINAPI AFIMoveJobScript(
    E_QUEUE_TYPE eJobQType,    
    const WCHAR* pwzBKDest,
    DWORD        dwSessNum,
    const WCHAR* pwzJobQIdentity = NULL,
    const WCHAR* pwzCatalogNodeID = NULL);

DWORD WINAPI AFIQueryJobQueue(
    E_QUEUE_TYPE eJobQType,                  //ZZ: Specify job queue type. Refer to E_QUEUE_TYPE(Regular, On-Demand, and Makeup)
    const WCHAR* pwzJobQIdentity = NULL,     //ZZ: Job queue identity. VM GUID for HBBU or computer identity for catalog running on RPS.
    wstring*     pwsJobQPath = NULL,         //ZZ: Return job queue path which contain job scripts. Ignore this parameter when it is NULL.
    WSVector*    pvecJobScriptList = NULL,   //ZZ: Return job script list under job queue. Ignore this parameter when it is NULL.
    bool         bCreateJobQFolder = false,  //ZZ: If create job queue folder when it doesn't exist.
    const WCHAR* pwzCatalogModeID = NULL     //ZZ: Server identity where catalog should be launched. If this parameter is empty, it will be ignored.
    );

//zouyu01: functions for schedule export on 2010-12-02.
DWORD WINAPI AFEnableShExp(BOOL bEnable, LPCWSTR pNodeInstanceID = NULL); //<sonmi01>2014-7-29 ###???

BOOL WINAPI AFCheckShExpBackupNum(unsigned long long ullNum, LPCWSTR pNodeInstanceID = NULL);

DWORD WINAPI AFAddSucceedBackupNum(LPCWSTR pNodeInstanceID = NULL);

//sonle01 archive synchronization
wstring WINAPI AFIGetArchiveCacheFileName4Trans();
DWORD WINAPI AFIDeleteArchiveCacheFileTrans();

//sonli02 show job monitor after web service is restarted
typedef struct _JOB_CONTEXT_Internal
{
	DWORD		  dwPrdType;					   // APT_D2D or APT_RPS, to identify if this is a RPS job or D2D job
	DWORD         dwJobId;
	DWORD         dwQueueType;
	DWORD         dwJobType;
	DWORD         dwProcessId;
	DWORD         dwJMShmId;
	ULONG         ulJobAttribute;                  //useful for VM job monitor
	wchar_t       wstrNodeName[512];               //useful for VM job monitor
	wchar_t       wstrLauncherInstanceUUID[512];   //useful for VM job monitor*/
	wchar_t       wstrRPSName[512];					//<sonmi01>2014-5-7 #116756: Server ¨C Tomcat module got crashed on source RPS server 
	wchar_t		  wstrD2DNodeName[512];				//<sonmi01>2014-5-7 #116756: Server ¨C Tomcat module got crashed on source RPS server 
	/* BEGIN: Added by <huvfe01>, 2013/4/19   PN:save rps job context */
	wchar_t       wstrD2DNodeGuid[512];
	/* END:   Added by <huvfe01>, 2013/4/19   PN:save rps job context */
	wchar_t		  wstrPolicyGuid[512];
	wchar_t       wzD2DIdentity[512];  //ZZ: Unique string to identity D2D server.
	/* BEGIN: Added by <shuzh02>, 2014/4/01   PN:save rps job context */
	wchar_t       wstrRPSNodeGuid[512];
	/* END:   Added by <shuzh02>, 2014/4/01   PN:save rps job context */

	_JOB_CONTEXT_Internal()
	{
		dwPrdType = APT_D2D;
		dwJobId = 0;
		dwQueueType = 0;
		dwJobType = 0;
		dwProcessId = 0;
		dwJMShmId = 0;
		ulJobAttribute = 0;       
		ZeroMemory( wstrNodeName, sizeof(wstrNodeName) );
		ZeroMemory( wstrLauncherInstanceUUID, sizeof(wstrNodeName) );
		ZeroMemory( wstrRPSName, sizeof(wstrRPSName) );
		ZeroMemory( wstrD2DNodeName, sizeof(wstrD2DNodeName) );
		ZeroMemory( wstrD2DNodeGuid, sizeof(wstrD2DNodeGuid) );
		ZeroMemory( wstrPolicyGuid, sizeof(wstrPolicyGuid) );
		ZeroMemory( wzD2DIdentity, sizeof(wzD2DIdentity) ); 
		ZeroMemory( wstrRPSNodeGuid, sizeof(wstrRPSNodeGuid) );
	}
} JOB_CONTEXT_Internal, *PJOB_CONTEXT_Internal;
typedef struct _JOB_CONTEXT
{
	DWORD         dwJobId;
	DWORD         dwQueueType;
	DWORD         dwJobType;
	DWORD         dwProcessId;
	DWORD         dwJMShmId;
	ULONG         ulJobAttribute;                  //useful for VM job monitor
	std::wstring  wstrNodeName;                    //useful for VM job monitor
	std::wstring  wstrLauncherInstanceUUID;        //useful for VM job monitor*/
	std::wstring  wstrRPSName; 
	std::wstring  wstrD2DNodeName;
	/* BEGIN: Added by <huvfe01>, 2013/4/19   PN:save rps job context */
	std::wstring  wstrD2DNodeGuid;
    /* END:   Added by <huvfe01>, 2013/4/19   PN:save rps job context */
	std::wstring  wstrPolicyGuid;
    std::wstring  wstrD2DIdentity;     //ZZ: Unique string to identity D2D server.
	/* BEGIN: Added by <shuzh02>, 2014/4/01   PN:save rps job context */
	std::wstring  wstrRPSNodeGuid;
    /* END:   Added by <shuzh02>, 2014/4/01   PN:save rps job context */
	/* BEGIN: Added by <caowe01>, 2014/8/6   PN:Add for vApp backup to put the high priority child VM into the waiting queue */
	DWORD		  dwPriority;
	DWORD		  dwMasterJobId;
	/* END: Added by <caowe01>, 2014/8/6   PN:Add for vApp backup to put the high priority child VM into the waiting queue */
	std::wstring   generatedDestination;	//caowe01 : for vApp child VM generated destination path
} JOB_CONTEXT, *PJOB_CONTEXT;
DWORD WINAPI AFRetrieveActiveJobs(std::vector<JOB_CONTEXT> &vecActiveJobs);

long WINAPI AFIsCatalogAvailable(DWORD dwQueueType, const WCHAR* pwzJobQIdentity = NULL, const WCHAR* pwzCatalogModeID = NULL);

BOOL WINAPI AFIsCatalogJobInQueue(DWORD dwQueueType, WCHAR* pwzBKDest, DWORD dwSessNum, DWORD dwSubSessNum);


//<sonmi01>2011-3-2 vm backup application infomation API
struct CVMWriterMetadataItem
{
    wstring m_wsWriterID;
    wstring m_wsWrtierName;
    wstring m_wsInstanceName;
    wstring m_wsMetadataContent; //not used now, reserve for later use
};
INT WINAPI AFGetVMAppInfo(OUT wstring & strAppInfotimeStamp, OUT vector<CVMWriterMetadataItem> & appInfo, IN CONST WCHAR * pszVMUuid);

typedef struct stGrtSessionInfo
{
    DWORD dwSize	;// the size of current structure
    BOOL isSameOStype;// x86 x64
    BOOL isSameMajor; // OS Major type  
    BOOL isSameMinor; // OS Minor type 
}GRT_SESSIONINFO, *PGRT_SESSIONINFO;

// add by wanmi12 <
//@Function: AFCheckGrtSession
//@brief: compare the os information between current machine and it where the session is backuped from 
//@param:
//	strDest		-[in] destination path
//	dwSessNo	-[in] the session number
//@return S_OK success,otherwize  false

DWORD WINAPI AFCheckGrtSession(const wstring &strDest, DWORD dwSessNo, GRT_SESSIONINFO &info);

//To get a mount point path from volume GUID
DWORD WINAPI AFGetMntFromVolumeGuid(const wstring &strGuid, wstring &strMntPath);

BOOL WINAPI AFCheckDestChainAccess(const NET_CONN_INFO &curDest, NET_CONN_INFO &baseDest, NET_CONN_INFO &errDest, BOOL bPrev = TRUE);

//check & update new credentials into backup chain.
DWORD WINAPI AFCheckUpdateNetConn(const NET_CONN_INFO &dest1, const NET_CONN_INFO &dest2, BOOL bPrev = TRUE);

typedef struct _MNT_SESS
{
    DWORD dwNo;
    wstring strSessFolder;
}MNT_SESS, *PMNT_SESS;

DWORD WINAPI AFGetMntSess(vector<MNT_SESS> &vMntSess);

typedef struct _NET_CONN
{
    wstring strConn;
    wstring strUser;
}NET_CONN, *PNET_CONN;

//this function retrieves all network connections in current logon session.
//this function does impersonation
DWORD WINAPI AFRetrieveConnections(vector<NET_CONN> &vConn);

DWORD WINAPI AFMoveLogs();

DWORD WINAPI AFOnlineDisks();

DWORD WINAPI AFDeleteLicError(DWORD licCode);

int WINAPI AFRPSRepConvertXmlToJobscript(const wchar_t* szXML, RPSRepJobScript ** ppJobScript, void*& jsHandle);

struct WriterInfo
{
    wchar_t wszWriterName[128];
    wchar_t wszWriterID[128];
    wchar_t wszInstanceName[128];
    wchar_t wszInstanceID[128];
};

struct VMInfo
{
    wchar_t wszvmOSVersion[128];
    bool bSQLServerInstalled;
    bool bExchangeInstalled;
    bool bHasDynamicDisk;
	bool bHasStorageSpaces;
};

LONG WINAPI AFGetApplicationDetailsInESXVM(vector<WriterInfo> & vecAppDetails, wchar_t* stuESXCredentials_ServerName, wchar_t* stuESXCredentials_Username, wchar_t* stuESXCredentials_Password, wchar_t* stuVMInfo_vmName, wchar_t* stuVMInfo_vmVMX, wchar_t* VMUsername, wchar_t* VMPassword);
LONG WINAPI AFGetESXVMInfo(VMInfo &vmInfo, wchar_t* stuESXCredentials_ServerName, wchar_t* stuESXCredentials_Username, wchar_t* stuESXCredentials_Password, wchar_t* stuVMInfo_vmName, wchar_t* stuVMInfo_vmVMX, wchar_t* VMUsername, wchar_t* VMPassword);

///ZZ: Added for configuring pre-allocate write space on web service side. @2012.2.6
LONG WINAPI AFISetPreAllocSpacePercent(DWORD dwPercent);
LONG WINAPI AFIGetPreAllocSpacePercent(DWORD& dwPercent);

BOOL WINAPI IsFirmwareuEFI();

/*
#define REG_NONE                    ( 0 )   // No value type
#define REG_SZ                      ( 1 )   // Unicode nul terminated string
#define REG_EXPAND_SZ               ( 2 )   // Unicode nul terminated string
// (with environment variable references)
#define REG_BINARY                  ( 3 )   // Free form binary
#define REG_DWORD                   ( 4 )   // 32-bit number
#define REG_DWORD_LITTLE_ENDIAN     ( 4 )   // 32-bit number (same as REG_DWORD)
#define REG_DWORD_BIG_ENDIAN        ( 5 )   // 32-bit number
#define REG_LINK                    ( 6 )   // Symbolic Link (unicode)
#define REG_MULTI_SZ                ( 7 )   // Multiple Unicode strings
#define REG_RESOURCE_LIST           ( 8 )   // Resource list in the resource map
#define REG_FULL_RESOURCE_DESCRIPTOR ( 9 )  // Resource list in the hardware description
#define REG_RESOURCE_REQUIREMENTS_LIST ( 10 )
#define REG_QWORD                   ( 11 )  // 64-bit number
#define REG_QWORD_LITTLE_ENDIAN     ( 11 )  // 64-bit number (same as REG_QWORD)
*/

#define MAX_REG_VALUE_BUF_SIZE      (MAX_PATH * 2)

typedef struct _stRegValue
{
    union
    {
        WCHAR wzRegStr[1];
        DWORD dwRegDec;
        BYTE  pbRegVal[MAX_REG_VALUE_BUF_SIZE];
    };

    DWORD dwValType;
}ST_REG_VAL;

DWORD WINAPI AFIReadD2DReg(LPBYTE lpBuffer,DWORD& dwBufSize,DWORD& dwRegType, 
                           const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg = CST_REG_ROOT_L);

DWORD WINAPI AFIWriteD2DReg(const LPBYTE lpBuffer,DWORD dwWriteBufSize,DWORD dwRegType, 
                            const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg = CST_REG_ROOT_L);
DWORD WINAPI AFGetActualPathName(wstring &wsFolderPath, wstring &wsOutFolderPath);

DWORD WINAPI AFClearPendingArchiveJobs(NET_CONN_INFO ConnectionInfo, DWORD dwJobType);
DWORD WINAPI AFDisableFilecopy(NET_CONN_INFO ConnectionInfo);
/** @brief This API checks if the ArchiveJob is allowed or not
*
*     a) check source destination
*     b) check if archiving is enabled or not
*     d) Checking if the file copy source volume is available in backup or not
*     e) Check volume type as we dont support ReFS and NTFS Dedup Volume
*     f) Check the destination if reachable or not. If reachable create the GUID
*
*
*  @param d2dMachineName      The D2DMachineName for which the ArchiveJob must run
*  @param d2dMachineDestInfo  The D2DMachine destination info which consist of destination path and the credentinals to connect
*  @param ulSessionID         The backup SessionId for which ArchiveJob must run
*  @param archiveConfigPath   The complete path of the ArchiveConfiguration.XML file
*  @param bDoArchive          This is the return value which indicates the archive job can be created
*  @return                    If this API is success the return value is D2DCOMM_S_OK and any other value is failure 
*/
DWORD WINAPI AFIsArchiveJobAllowed(const wstring& d2dMachineName, const NET_CONN_INFO& d2dMachineDestInfo, ULONG ulSessionID, PFILECOPYJOB_SCHEDULER_POLICY archiveJobSchedulerInfo, BOOL* bDoArchive);

/** @brief This API checks queues the ArchiveJob and the lock the particular session
*
*  @param d2dMachineName      The D2DMachineName for which the ArchiveJob must run
*  @param d2dMachineDestInfo  The D2DMachine destination info which consist of destination path and the credentinals to connect
*  @param ulSessionID         The backup SessionId for which ArchiveJob must run
*  @param dwJobMethod         The JobMethod it could be either full or incremental
*  @param archiveConfigPath   The complete path of the ArchiveConfiguration.XML file
*  @return                    If this API is success the return value is D2DCOMM_S_OK and any other value is failure
*/
DWORD WINAPI AFQueueArchiveJob(const wstring& d2dMachineName, const NET_CONN_INFO& d2dMachineDestInfo, ULONG ulSessionID, DWORD dwJobMethod, PFILECOPYJOB_SCHEDULER_POLICY archiveJobSchedulerInfo, DWORD dwJobType);

/** @brief This API checks if the ArchiveJob is scheduled to run or not.  It even updates the backup session count. After 'N' backups the job will be scheduled
*
*     a) check if archiving is enabled or not
*     b) check schedule settings (it checks if the archive job is allowed after 'N' backups); It even updates the SessionCount in ArchiveConfiguration.xml file stored under backup destination.
*
*  @param d2dMachineName		The D2DMachineName for which the ArchiveJob must run
*  @param d2dMachineDestInfo	The D2DMachine destination info which consist of destination path and the credentinals to connect
*  @param ulSessionID			The backup SessionId for which ArchiveJob must run
*  @param archiveConfigPath		The complete path of the ArchiveConfiguration.XML file
*  @param bArchiveJobScheduled  This is the return value which indicates the archive job can be scheduled
*  @return                    If this API is success the return value is D2DCOMM_S_OK and any other value is failure
*/
DWORD WINAPI AFChk_UptArchiveJobScheduleForNBackups(const wstring& d2dMachineName, const NET_CONN_INFO& d2dMachineDestInfo, ULONG ulSessionID, PFILECOPYJOB_SCHEDULER_POLICY archiveJobSchedulerInfo, BOOL* bArchiveJobScheduled, DWORD dwJobType);

/**********************************************************
* Wrapper API used to check if need start file copy job.
*/
DWORD WINAPI AFMarkFileCopyReadyOrNot(PAFARCHIVE_INFO pFileCopySchedule, NET_CONN_INFO DestInfo, DWORD dwSessNo);

int WINAPI AFIUpdateSessionPasswordByGUID(wstring &guid, LPCWSTR password, ULONG length);

bool WINAPI AFIsDedupedNTFSVolume(const wstring & volname);

long WINAPI AFGetFileSystemType(const WCHAR* pwzVolumeNameOrGUID, eFileSystemType& eFSType);

//wanmi12 for supporting win8 2012-08-10<
typedef struct st_system_info
{
	int nSize;				//	the size of current structure 
	BOOL isDedupeInstalled; //  dedupe featuren is installed 
	BOOL isWin8;			//	current system is windows 8
	BOOL isSupportedRefs;
}D2D_SYSINFO, *PD2D_SYSINFO; 

int WINAPI AFCollectSystemInfo(PD2D_SYSINFO pstObj);
//wanmi12 for supporting win8 2012-08-10<

///ZZ: Added for exporting some API to operate merge job.
IJobMonInterface* WINAPI AFICreateMergeJM(DWORD dwJobID, DWORD* pdwErrCode = NULL, DWORD dwJobType = EJT_MERGE);
void WINAPI AFIReleaseMergeJM(IJobMonInterface** ppJobMonMgr);
long WINAPI AFISaveMergeJS(CMergeJS& MergeJS, const WCHAR* pwzJSPath);
long WINAPI AFIStopMergeJob(DWORD dwJobID, DWORD dwJobType = EJT_MERGE);
long WINAPI AFIStartJob(
    const WCHAR* pwzJSPath,
    DWORD* pdwProcID = NULL, 
    const WCHAR* pwzUsrName = NULL, 
    const WCHAR* pwzPassword = NULL,
    DWORD dwJobType = EJT_MERGE
    );
long WINAPI AFIIsMergeJobAvailable(
    DWORD dwRetentionCnt, 
    const WCHAR* pwzBKDest, 
    const WCHAR* pwzVMGUID = NULL, 
    const WCHAR* pwzBKUsr = NULL, 
    const WCHAR* pwzBKPwd = NULL, 
    const WCHAR* pwzDS4Replication = NULL
    );

long WINAPI AFIIsMergeJobAvailableEx(
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

long WINAPI AFIRetrieveMergeJM(ActJobVector& vecActiveJob);

//xuvji01 add for issue 140275
long WINAPI AFIGetSessNumListForNextMerge(DWORD dwRetentionCnt, const WCHAR* pwzBKDest, vector<DWORD>& vecSessNumList, const WCHAR* pwzBKUsr = NULL, const WCHAR* pwzBKPwd= NULL);


///ZZ: Added for supporting backup set.
long WINAPI AFISetBackupSetFlag(const WCHAR* pwzBKDest, DWORD dwSessNum, DWORD dwBKSetFlag, const WCHAR* pwzVMGUID = NULL);

long WINAPI AFVM_BrowseFolderUnderDir(std::vector<FILE_INFO> &vList,wchar_t* stuESXCredentials_ServerName, wchar_t* stuESXCredentials_Username, wchar_t* stuESXCredentials_Password, 
									  wchar_t* stuVMInfo_vmName, wchar_t* stuVMInfo_vmVMX, wchar_t* VMUsername, wchar_t* VMPassword,wchar_t* vmGUID,
									  wchar_t* szParent,DWORD dwOptions,wchar_t* szTobeCreateFolder);

 

long WINAPI AFVM_BrowseFolderUnderDir_V2(std::vector<FILE_INFO> &vList,VM_BROWSEINFO& vmInfo);

///ZZ: Added for retrieving full session of specified incremental session
long WINAPI AFIGetFullSess4Inc(DWORD& dwFullSess, DWORD dwIncSess, const WCHAR* pwzBKDest);

//ZZ: APIs used for manage encryotion information using key file
//////////////////////////////////////////////////////////////////////////
//ZZ: Save a data buffer into key file. This data will be encrypted by public key, while private key is encrypted by pwzPassword.
long WINAPI AFIKeyFileSaveData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer to be encrypted and saved into key file path.
    DWORD dwDataBufSize,         //ZZ: Specify data buffer size, in bytes.
    const WCHAR* pwzKeyFilePath, //ZZ: Specify a valid and accessible full path which used to store key and data.
    const WCHAR* pwzPassword     //ZZ: Specify a password to encryt private key. If this paramter is empty. all data is plain text.
    );

//ZZ: Read decrypted data from key file. When input buffer size is not enough or pbDatabuffer is NULL this API will return D2DCRYPTO_E_MORE_DATA.
//ZZ: and dwDataBufSize will receive required buffer size.
long WINAPI AFIKeyFileReadData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer which receives decrytped data. Set NULL to get required size.
    DWORD& dwDataBufSize,        //ZZ: Specify data buffer size and recieve decryped size. Return required size if size is not enough or pbDataBuffer is NULL, 
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read.
    const WCHAR* pwzPassword     //ZZ: Specify password used to decrypt orivate key.
    );

//ZZ: Replace data saved in current key file. This data will be encrypted using public key stored in key file
long WINAPI AFIKeyFileUpdateData(
    PBYTE pbDataBuffer,          //ZZ: Specify data buffer to replace the data in key file.
    DWORD dwDataBufSize,         //ZZ: Specify data buffer size.
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read. 
    const WCHAR* pwzPassword     //ZZ: Not used now. should be set as NULL.
    );

//ZZ: Decrypt data stored in key file using original password and encrypt them using new password. If current key file is not encrypted data will be encrypted.
//ZZ: If new password is NULL or empty. the data will be decrupted and saved into key file in plain text format.
long WINAPI AFIKeyFileUpdatePassword(
    const WCHAR* pwzKeyFilePath, //ZZ: Specify full path of key file. Caller should make sure this file can be read. 
    const WCHAR* pwzNewPassword, //ZZ: Specify new password to encrypt private key. If this parameter is NULL or empty. private key will be plain text.
    const WCHAR* pwzCurPassword  //ZZ: Specify original password to decrypt private key.
    );

//ZZ: Save data store hash key and encrypted by session password
long WINAPI AFISaveDataStoreHashKey(
    const WCHAR* pwzBKDest,    //ZZ: Backup destination path.
    DWORD        dwSessNum,    //ZZ: Session number.
    const WCHAR* pwzDSHashKey, //ZZ: Data store hash key which used to encrypt session data.
    const WCHAR* pwzPassword   //ZZ: Password to encrypt data store hash key.
    );

//ZZ: Save data store hash key and encrypted by session password
long WINAPI AFIGetDataStoreHashKey(
    wstring&     wsDSHashKey,  //ZZ: Data store hash key.
    const WCHAR* pwzBKDest,    //ZZ: Backup destination path.
    DWORD        dwSessNum,    //ZZ: Session number.
    const WCHAR* pwzSessPwd    //ZZ: Session password to decrypt data store hash key.
    );

//ZZ: Update data store hash key for session
long WINAPI AFIUpdateDataStoreHashKey(
    const WCHAR* pwzBKDest,       //ZZ: Backup destination path.
    DWORD        dwSessNum,       //ZZ: Session number.
    const WCHAR* pwzNewDSHashKey  //ZZ: New data store hash key which used to encrypt session data.
    );

//ZZ: [2013/07/02 12:07] Add function to get SID of specified user or current computer.
DWORD WINAPI AFIGetUserSID(
    wstring& wsUserSID,                 //ZZ: Receive user SID in text format.
    const WCHAR* pwzUserName = NULL,    //ZZ: Specify user name in form machine\user. if this parameter is NULL, SID of current computer will be returned.
    wstring* pwsSIDAccount = NULL,      //ZZ: Receive actual account name used to acquire SID.
    wstring* pwsDomain4SID = NULL,      //ZZ: Receive domain name of specified user.
    DWORD*   pdwSIDAccountType = NULL,  //ZZ: Receive account type of specified user.
    DWORD    dwHashAlg = EHAT_SHA1      //ZZ: To make sure fixed length of this unique value, we will hash it. Set EHAT_UNKNOWN to retrieve actaul SID.
    );

//ZZ: [2013/10/30 14:40]  Add function to get SID of specified user or current computer with more option
DWORD WINAPI AFIGetUserSIDEx(
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

DWORD WINAPI AFIGetCatalogStatus(
    const WCHAR*       pwzBKDest,
    PST_CATALOG_STATUS pstCatalogStatusList,
    DWORD              dwCatalogStatus,
    const WCHAR*       pwzJobQIdentity = NULL);

DWORD WINAPI AFIRemoveCatalogJS(
    E_QUEUE_TYPE eJobQType,
    const WCHAR* pwzDataStoreGUID,
    const WCHAR* pwzJobQIdentity = NULL,
    bool         bMoveInsteadDelete = true);
//////////////////////////////////////////////////////////////////////////
//Function Name: AFIGetNodeID
//				Get the unique ID of current machine. This ID is different from the logon ID
//Parameters: 
//		strNodeID [out] the node ID
//return value: 0, success; otherwise, failed.
DWORD WINAPI AFIGetNodeID( wstring& strNodeID );

//Function Name: AFIVerifyDestUser
//				 check current user whether can connect to Destination folder, Both functions of AFMgrDestInit and AFMgrDestUnInit should be invoked in the same thread.
//Parameters:
//		pwszPath [in] the path of d2d destination
//		pwszUser [in] the user name of destination , the remote destination ,the format should be: (domain name\user name) or (machine name\user name)
//		pwszPSw	 [in] the password for user identified by pwszUser
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD WINAPI AFIVerifyDestUser(const wchar_t * pwszPath, const wchar_t * pwszUser, const wchar_t * pwszpPsw);


//Function Name: AFAddMissedBackupHistory
//				 add a missed backup job history under ..\BackupHistory\
//Parameters:
//		ullTime      [in] the UTC time of this job
//		dwJobID      [in] the job ID, got from web service
//		dwJobMethod	 [in] the job method - full, inc or verify
//      pszJobName   [in] the job name
//      pVmUUID      [in] the VM instance UUID
//return value: 0, success; otherwise, failed.
DWORD WINAPI AFAddMissedBackupHistory( ULONGLONG ullTime, DWORD dwJobID, DWORD dwJobMethod, const wchar_t* pszJobName, const wchar_t* pVmUUID, PFLASHDB_JOB_HISTORY pJobHistory = NULL);

//////////////////////////////////////////////////////////////////////////
//Function Name: AFIGenerateRecoveryPointSyncInfo
//				Generate the recovery point sync information ( which recovery point is updated and which one is deleted )
//Parameters: 
//		info [IN]: the current destination and its connect infor
//      lpszVmUUID [IN]: the VM instance UUID. If this parameter is null or empty, it will generate sync infor of local host
//		bFullSync [IN]: Full sync or incremental sync. By default it is incremental sync.
//return value: 0, success; otherwise, failed.
DWORD WINAPI AFIGenerateRecoveryPointSyncInfo( const NET_CONN_INFO &info, const wchar_t* lpszVmUUID, BOOL bFullSync = FALSE );

typedef struct _CARemoteEngineerInputParameter
{
	PWCHAR pwzCaseNumber;
	PWCHAR pwzIssueNumber;
	PWCHAR pwzSiteID;
	BOOL bUploadToFTP;
	PWCHAR pwzFTPAccount;
	PWCHAR pwzFTPPassword;
}CA_REMOTE_ENGINEER_INPUT_PARAMETER;

DWORD WINAPI AFRemoteEngineerCollectInformation(const CA_REMOTE_ENGINEER_INPUT_PARAMETER * pCARemoteEngineerInputParameter);


//the hyperv vm configuration file parser
namespace VMPersistConfiguration{

	class CVMMemory
	{
	public:
		DWORD  m_dwSizeInMB;
	};

	class CVMProcessor
	{
	public:
		DWORD  m_dwCount;
	};

	//hyperv configuration
	class CHyperVVMProcessor : public CVMProcessor
	{

	};

	class CHyperVVMMemory :public CVMMemory
	{
	public:
		BOOL m_bIsDynamicMemEnabled;
		DWORD m_dwReservationSizeInMB;
		DWORD m_dwLimitSizeInMB;
	};

    #define NETWORK_ADAPTER_TYPE_SYNTH_ETHERENT 0
    #define NETWORK_ADAPTER_TYPE_EMULATED_ETHERNET 1
	class CHyperVVMNetworkAdapter
	{
	public:
		DWORD   m_dwID;
		DWORD   m_dwResourceType;
		BOOL    m_bIsMacAddrStatic;
		BOOL    m_bIsConnected;
		wstring m_strChannelInstanceGuid; //not used for legacy network adapter
		wstring m_strFriendlyName;
		wstring m_strMacAddress;
		wstring m_strSwitchName;
		wstring m_strPortName;
	};

	class CHyperVVMControllerDrive
	{
	public:
		DWORD   m_dwDriveId;
		wstring m_strPathName;
		wstring m_strPoolId;
		wstring m_strType;
	};

	class CHyperVVMController
	{
	public:
		DWORD m_dwControllerId;
		vector<CHyperVVMControllerDrive> m_vecDrives;
	};

	class CHyperVVMManifestDevice
	{
	public:
		DWORD   m_dwDevId;
		wstring m_strDevice;
		wstring m_strInstance;
		wstring m_strName;
		INT     m_nFlag;
	};

	class IHyperVVMPersistConfParser
	{
	public:
		virtual VOID  Release() = 0;
		virtual DWORD Initialize(LPCWSTR pConfFile) = 0;
		virtual BOOL GetMemory(CHyperVVMMemory& vmMemory) = 0;
		virtual BOOL GetProcessor(CHyperVVMProcessor& vmProcessor) = 0;
		virtual BOOL GetIdeControllers(vector<CHyperVVMController>& vecIdeControllers) = 0;
		virtual BOOL ListScsiControllerInstances(vector<wstring>& vecScsiInstances) = 0;
		virtual BOOL GetScsiControllers(const wstring& strInstance, vector<CHyperVVMController>& vecScsiControllers) = 0;
		virtual BOOL GetNetworkAdapters(vector<CHyperVVMNetworkAdapter>& vecAdapters) = 0;
		virtual BOOL GetLegacyNetworkAdapters(vector<CHyperVVMNetworkAdapter>& vecAdapters) = 0;
		virtual BOOL GetAllNetworkAdapters(vector<CHyperVVMNetworkAdapter>& vecAdapters) = 0;
	};

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	IHyperVVMPersistConfParser* WINAPI AFCreateInstanceHyperVVMConfParser();

	DWORD WINAPI CreateIHyperVMPersistConfParser(const NET_CONN_INFO &info, LPCWSTR pSubPath, IHyperVVMPersistConfParser **ppParser);
}

//
//
// APIs for ARCUpdate
//
//
//  dwType:				0 is for Engine and 1 is for FULL
//	dwServerType:		see defination of ARCUPDATE_SERVER_TYPE, 0 means from CA server, 1 means from staging server.
//  strServer:			for staging server only. If dwServerType==0, ignore this parameter.
//  nPort:				for staging server only. If dwServerType==0, ignore this parameter.
//  strProxyServer:		for CA server only. If dwServerType==1, ignore this parameter.
//  nProxyServerPort:	for CA server only. If dwServerType==1, ignore this parameter.
//  strProxyUser:		for CA server only. If dwServerType==1, ignore this parameter.
//  strProxyPassword:	for CA server only. If dwServerType==1, ignore this parameter.
//
DWORD WINAPI AFTestUpdateServerConnection( DWORD dwType, DWORD dwServerType, const wstring& strServer, int nPort, 
											 const wstring& strProxyServer, int nProxyServerPort, 
											 const wstring& strProxyUser, const wstring& strProxyPassword );

DWORD WINAPI AFTestBIUpdateServerConnection(DWORD dwType, DWORD dwServerType, const wstring& strServer, int nPort,
	const wstring& strProxyServer, int nProxyServerPort,
	const wstring& strProxyUser, const wstring& strProxyPassword);

//
//  dwType:				0 is for Engine and 1 is for FULL
//
DWORD WINAPI AFInstallBIUpdate(DWORD dwType);

//
//  dwType:				0 is for Engine and 1 is for FULL
//
DWORD WINAPI AFGetBIUpdateStatusFile(DWORD dwType, wstring& strFile);


//
//  dwType:				0 is for Engine and 1 is for FULL
//
DWORD WINAPI AFCheckBIUpdate(DWORD dwType);

//added by cliicy.luo

//
//  dwType:				0 is for Engine and 1 is for FULL
//
BOOL WINAPI AFIsUpdateBusy( DWORD dwType );

BOOL WINAPI AFIsUpdateServiceRunning();

//
//  dwType:				0 is for Engine and 1 is for FULL
//
DWORD WINAPI AFCheckUpdate( DWORD dwType );

//
//  dwType:				0 is for Engine and 1 is for FULL
//
DWORD WINAPI AFGetUpdateStatusFile( DWORD dwType, wstring& strFile);

//
//  dwType:				0 is for Engine and 1 is for FULL
//
DWORD WINAPI AFGetUpdateSettingsFile( DWORD dwType, wstring& strFile);

//
//  dwType:				0 is for Engine and 1 is for FULL
//
DWORD WINAPI AFInstallUpdate( DWORD dwType );

//
//  dwType:				0 is for Engine and 1 is for FULL
//  mailAlertFiles:     File list of mails
//
DWORD WINAPI AFGetAlertMailFiles( DWORD dwType, std::vector<wstring>& mailAlertFiles );

//
//  dwType:				0 is for Engine and 1 is for FULL
//
DWORD WINAPI AFGetUpdateErrorMessage( DWORD dwType, DWORD dwErrCode, wstring& strErrorMessage );

//
//  dwType:				0 is for Engine and 1 is for FULL
//
DWORD WINAPI AFDisalbeAutoUpdate( DWORD dwType=0, BOOL bDisable=TRUE );

// Cancel master job and child jobs by ID
DWORD WINAPI AFCancelJobEx(ULONG jobType, DWORD jobId, vector<DWORD> childJobIDs);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Node backup data size information for Dashboard. 2014-12-18 [zhahu03]
typedef struct _stBkDataSize
{
	ULONGLONG ullDiskOccupied;		// Total disk occupied size of the node's all backup.
	ULONGLONG ullRawDataSize;		// Backup volume original size
	ULONGLONG ullRestorableSize;	// Backup transfered size
	std::wstring strNodeUUID;		// Node UUID
	std::wstring strvmName;			// if HBBU backup, vm name
	std::wstring strNodeName;		// if agent-based backup, agent name
	std::wstring strDest;			// if DataStore, return DataStore’s UUID. If Share folder/local folder, return its folder path

	_stBkDataSize():
		ullDiskOccupied(0),
		ullRawDataSize(0),
		ullRestorableSize(0),
		strNodeUUID(L""),
		strvmName(L""),
		strNodeName(L"")
	{}
}BACKUP_NODE_SIZE, *PBACKUP_NODE_SIZE;

typedef std::vector<BACKUP_NODE_SIZE> VBACKUP_NODES_SIZE;

typedef struct _stStorageMaxCap
{
	ULONGLONG ullCapacityB;		// total size of volume that all backup destination belongs to
	ULONGLONG ullUsedSizeB;		// used size
	ULONGLONG ullFreeSizeB;		// free size
	ULONGLONG ullReserved;		// reserved

	_stStorageMaxCap()
		:ullCapacityB(0),
		ullUsedSizeB(0),
		ullFreeSizeB(0),
		ullReserved(0)
	{}
}BK_STORAGE_MAX_CAP, *PBK_STORAGE_MAX_CAP;

// Get data backup size under specified folder(local/share folder).
DWORD WINAPI AFGetDataSizeFromShareFolder(IN NET_CONN_INFO netInfo, OUT VBACKUP_NODES_SIZE& vBkNodesSize);

// Get summary storage Total and Used size of volumes include in @destList
DWORD WINAPI AFGetMaxStorageCapacity(std::vector<wstring>& destList, BK_STORAGE_MAX_CAP& stMaxCap);