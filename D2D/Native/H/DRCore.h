#pragma once

#include <windows.h>
#include <string>
#include "afdefine.h"
#include "drcommonlib.h"
#include "log.h"
#include "winioctl.h"
#include "afjob.h"
#include "RpsRepJobHistory.h"
#include "CatalogJobHistory.h"

using namespace std;
#ifdef DRCORE_EXPORTS
#define DRCORE_API __declspec(dllexport)
#else
#define DRCORE_API __declspec(dllimport)
#endif
#ifndef _WIN32_WINNT		// Allow use of features specific to Windows XP or later.                   
#define _WIN32_WINNT 0x0501	// Change this to the appropriate value to target other versions of Windows.
#endif	


#define BK_DR_MIN_VOLUME_SIZE_FILE							L"MinVolumeSize.tmp"


///ZZ: Added for supporting backup set. Wanna BackupInfo more readable, this enumeration value is defined closed to BackupDetailXml.
typedef enum
{
	EBSF_NOT_BACKUP_SET = 0,
	EBSF_BACKUP_SET = 1,
}E_BKSET_FLAG;

typedef enum
{
	ESS_NORMAL = 0,   ///ZZ: Normal status. This status is ignored.
	ESS_MERGING       ///ZZ: Session is merging. When merge is launched, session to which data is merged will be marked as FULL, and this flag will be set.
}E_SESS_STATUS;

typedef struct tagBackupNameXml
{
	wstring strBackupName;
}BackupNameXml, *PBackupNameXml;

typedef struct tagServerInfoXml
{
	wstring	HostName;
	wstring	CPU;
	wstring	OS;
	wstring OsType; //<huvfe01>2012-11-2 for OS type
	wstring bootFirmware; //'bios' or 'uefi'
}ServerInfoXml, *PServerInfoXml;

typedef struct tagTimeStampXml
{
	wstring	Date;
	wstring	Time;
	wstring copyDate;//[zouyu01] 2011-5-9 time for copy recover point
	wstring copyTime;
	FILETIME BkLocalTime;
}TimeStampXml, *PTimeStampXml;

typedef struct tagBackupStatusXml
{
	tagBackupStatusXml() : dwSessStatus(ESS_NORMAL) {}
	DWORD   dwSessStatus;   ///ZZ: Status for internally operation, such as merge, catalog and so on.
	wstring status;         ///ZZ: Status for backup. E,g. Backup succeed, Backup failed or Backup canceled.
}BackupStatusXml, *PBackupStatusXml;

typedef struct tagRootItemXml
{
	wstring Type;
	wstring DisplayName;
	wstring strMnts; //mount point for the volume. use ';' to delimit.
	wstring GUID;
	wstring strVolDataSizeB; //volume data size in bytes.
	wstring SubSessNo; //sub session No.
	wstring CatalogFile;
	BOOL bIsBootVol; //boot volume. only valid when type is volume.
	BOOL bIsSysVol; //system volume. only valid when type is volume.
	BOOL bIsReFSVol;
	BOOL bIsNTFSVol;
	BOOL bIsDedupVol;
	BOOL bIsUefiRecoveryVol;
}RootItemXml, *PRootItemXml;

typedef struct tagPartItemXml //parition information.
{
	BOOL bIsSysVol; //whether it is a system volume.
	wstring strDosName; //dos device name for this partition.
	wstring strVolGuid; //volume guid path.
	wstring strDiskNo; //disk No for the partition.
	wstring strDiskSig; //disk signature for the partition. For GPT disk, use crc to calculate a sig from GUID.
	wstring strOffset; //partition offset in the disk.
	wstring strSize; //partition length in the disk.
}PartItemXml;

typedef struct tagBackupDetailXml
{
	tagBackupDetailXml()
	{
		bIsFullMachine = FALSE;
		TotalRawDataSizeWritten = 0;
		dwBMRFlag = (DWORD)-1;
		dwBKSetFlag = EBSF_NOT_BACKUP_SET;
		bEnableDataSlice = FALSE;
		bIsMspUser = FALSE;

		bEnableDedupe = FALSE;
		llCommonPathSize = 0;
	}

	wstring	ID;                  //session no.
	wstring JobID;            //job id.
	wstring  sessGuid;
	wstring NodeID;				//<sonmi01>2014-3-25 #node id - agent UUID or VM instance UUID agent UUID or VM instance UUID
	wstring	BackupType;
	BOOL	bIsFullMachine;		// chefr03: UPDATE_BACKUP_INFO 
	wstring  CompressType;
	wstring	DataSizeKB;
	wstring  TransferDataSizeKB;
	wstring	ProtectedDataSizeB; //<sonmi01>2013-7-2 #ProtectedDataSizeB for reporting
	wstring  CatalogSizeB;
	wstring  GrtCatalogSizeB;
	wstring	EncryptType; //<sonmi01>2010-5-6 encrypt support
	wstring	EncryptPasswordHash;  //ZZ: Hash of session password
	wstring  DataStoreKeyHash;     //ZZ: Hash of data store hash key.
	wstring	wsSourceDataStore;
	wstring  wsSourceDataStoreName;
	wstring	wsTargetDataStore;
	wstring  wsTargetDataStoreName;
	wstring  wsSourceRPSName;
	wstring  wsRPSName;
	wstring  wsPolicyName;
	//wstring  IsArchiveEnabled; //zouyu01 2010-11-17, archive enable flag for session.
	//wstring  ArchiveStatus;  //zouyu01 2010-11-17, archive status.
	LONGLONG TotalRawDataSizeWritten; //<sonmi01>2010-11-23 total raw data size written to virtual disk
	wstring strBakDest;//backup destination zouyu01 2011-5-9 for edge display. like 'c:\backupdest\zouyu01-win7'
	DWORD dwBMRFlag; //zouyu01 on 2011-6-28
	wstring ReplicationStatus; //<sonmi01>2013-4-9 #replication status
	BOOL bIsMspUser;
	wstring wsUsername; //<huvfe01>2013-12-18 distinguish D2D node with the same hostname
	wstring wsTargetPlanName;
	DWORD dwBKSetFlag;  //ZZ: Indicate if this session is first full session in backup set. 0: No, Others: Yes. 
	DWORD dwBKAdvSchFlag;  //ZZ: Advanced schedule flag. 1: daily backup, 2: weekly backup 4: monthly backup.
	ULONGLONG ullScheduledTime;
	BOOL bEnableDataSlice;  //lijle01: Enable VHD2 data slicing format
	wstring SliceSizeInMB;  //lijle01: Size of each slice in MB
	BOOL bEnableDedupe;			//hefzh01: Is dedupe format session
	LONGLONG llCommonPathSize;	//hefzh01: Data size written into common path. It is same to session data size for nondedupe. 

	//<sonmi01>2015-11-11 ###???
	LONGLONG syntheticRawSizeRead{ 0 };
	LONGLONG syntheticRawSizeWritten{ 0 };
	LONGLONG syntheticRawSizeSkipped{ 0 };
	LONGLONG ntfsVolumeSizeByBitMap{ 0 };
	LONGLONG VirtualDiskProvisionSize{ 0 }; //<sonmi01>2016-1-4 ###???
	BOOL bIsWindows{ FALSE };
	BOOL bIsAllNtfsVolumes{ FALSE };
	BOOL bHasStorageSpace{ FALSE };

	vector<RootItemXml>	RootItems;
	vector<PartItemXml> PartItems; //zouyu01 2011-11-25, backed up parition info. like ESP.
}BackupDetailXml, *PBackupDetailXml;

typedef struct tagBackupInfoXml
{
	wstring			Version;
	BackupNameXml  BackupName;
	wstring			BackupProxy;
	ServerInfoXml	ServerInfo;
	TimeStampXml	TimeStamp;
	BackupStatusXml	BackupStatus;
	BackupDetailXml	BackupDetail;
}BackupInfoXml, *PBackupInfoXml;


typedef struct _DISK_UNIQID_
{
	DWORD dwDiskType; //value is PARTITION_STYLE
	union
	{
		DWORD dwSignature;
		GUID	DiskId;
	};
}DISK_UNIQID, *PDISK_UNIQID;

typedef std::vector<DISK_UNIQID> VDISK_UNIQID;


//PBackupInfoXml backupInfoXml - structure
//LPCTSTR szXmlFileName - XMl file name
INT WriteBackupInfoXml(PBackupInfoXml backupInfoXml, LPCWSTR szXmlFileName);

INT UpdateBackupInfoXml(const PBackupInfoXml backupInfoXml, LPCWSTR szXmlFileName);

//ZZ: Added for update volume size.
INT UpdateBackupInfoXmlEx(const PBackupInfoXml backupInfoXml, LPCWSTR szXmlFileName, bool bRefeshVolSize = true);

DWORD ReadBackupInfoXml(LPCWSTR pszXmlFileName, BackupInfoXml &bakInfo);

/*
*Purpose: Collect DR information.
*@pszDestDir: [input] Destination folder which contains DR information.
*Return:
If function successes, zero will be returned.
If function fails, system error code will be returned.
*/

DWORD FlashDRCollectInfo(const wchar_t *pszDestDir);

DWORD FlashCollectDRInfo(const wchar_t *pszDestDir);

DWORD FlashUpdateDRInfo(const wchar_t *pszDestDir);
/*
*Purpose: Collect DR information.
*@pszDestDir: [input] Destination folder which contains DR information.
*Return:
If function succeeds, zero will be returned.
If function fails, system error code will be returned.
*/

DWORD FlashDRCollectInfoForHA(const wchar_t *pszDestDir);

/*
*Purpose: Collect BCD and compress it to AdrInfoC.drz under $D2D_install\\Bin\DR\BCD.
*@strDrzFile: [output] the full file path of the AdrInfoC.drz.
*Return:
If function succeeds, zero will be returned.
If function fails, system error code will be returned.
*/

DWORD FlashDRCollectBCDForHA(wstring &strDrzFile);

/*
 Description:
 The function is to detect whether the OS is restored by BMR
 */
BOOL IsOsRestoreByBMR();

/*
 Description:
 The function is clean the BMR flag which is identify the OS is restored by BMR
 */
DWORD CleanBMRFlag();

BOOL IsOsRestoreByV2p();

DWORD DRCleanV2pFlag();

/*
 Description:
 The function is to get the changed size disk signature
 */
DWORD GetChangedDiskSignautre(VDISK_UNIQID& DiskIds);

//For BMR license.
#define LIC_BMR2ORI 0x00000001//License for BMR to original machine.
#define LIC_BMR2AHW 0x00000002//License for BMR to alternate hardware.

DWORD DRSetLicense(const NET_CONN_INFO &info, DWORD dwLicFlag);

BOOL DRCheckLicense(const wstring &strCfg, DWORD &dwLicFlag);

DWORD DRCheckAndSaveLic(const wchar_t *pDest, DWORD *pdwLicFlag);


DWORD UpdateBackupMethodDRCfg(LPCWSTR lpFilePath, LPCWSTR lpType);

DWORD UpdateSessCryptoInfoDRCfg(LPCWSTR lpFilePath, PST_CRYPTO_INFO pstCryptoInfo);

// Change <strPath> to full session path(..\VStore\S0000000001).  [9/11/2014 zhahu03] 
void WINAPI BackupInfoXml2BackupInfoDB(const BackupInfoXml &infoXml, const wstring &strPath, BACKUP_INFO_DB &info);

DWORD WINAPI UpdateBackupInfoDB(const wchar_t *pDir, const BACKUP_INFO_DB &infoDb);

DWORD WINAPI GetBackupInfoDB(const wchar_t *pDir, BACKUP_INFO_DB &infoDb);

DWORD WINAPI SaveBackupInfoDBToFile(const wchar_t* pszFilepath, const BACKUP_INFO_DB &infoDb);

DWORD WINAPI ReadBackupInfoDBFromFile(const wchar_t* pszFilepath, BACKUP_INFO_DB &infoDb, const wchar_t* pszBakupDestination = NULL);

// pBakInfoDBFileName: BackupInfoDB.xml file full path.
// dwVersion: format window MAKEWORD(MajVer, MinVer)
BOOL WINAPI BkInfoDBVerGreaterOrEqual(const wchar_t *pBakInfoDBFileName, DWORD dwVersion);

//For D2D-Edge Data Sync 20100705
DWORD WINAPI __InitializeCacheIDGenerator(wstring &cache_file_id);
DWORD WINAPI __GetNextCacheID(wstring &cache_file_id);
DWORD WINAPI __CreateNextCacheFile(wstring &cache_file, DWORD cacheID);
DWORD WINAPI __InsertBackupRecordXML(wstring &cacheFileName, PBackupInfoXml backupInfo, const wchar_t *uniqueID);
__declspec(dllexport) DWORD WINAPI __InsertRPSJobRecordXML(wstring &cacheFileName, RepJobHistoryXml* jobInfo, const wchar_t *uniqueID);
__declspec(dllexport) DWORD WINAPI __InsertRPSJobRecordXML(wstring &cacheFileName, MergeJobHistoryXml* jobInfo, const wchar_t *uniqueID);
__declspec(dllexport) DWORD WINAPI __InsertRPSJobRecordXML(wstring &cacheFileName, CatJobHistoryXml* jobInfo, const wchar_t *uniqueID);
__declspec(dllexport) DWORD WINAPI __InsertRPSJobRecord4PurgeXML(wstring &cacheFileName, wstring JobId, const wchar_t *uniqueID);
__declspec(dllexport) DWORD WINAPI __InsertRPSSessionRecordXML(wstring &cacheFileName, BACKUP_ITEM_INFOEX* sessInfo, const wchar_t *uniqueID);
__declspec(dllexport) DWORD WINAPI __InsertRPSSessionRecord4PurgeXML(wstring &cacheFileName, wstring SessionGUID, const wchar_t *uniqueID);
DWORD WINAPI __CopyBackupHistory2CacheFile(wstring backupHistoryFile, wstring cacheFile, wstring uniqueID);
__declspec(dllexport) DWORD WINAPI __CopyRPSJobHistory2CacheFile(wstring backupHistoryFile, wstring cacheFile, wstring uniqueID);
DWORD WINAPI __MergeCacheRecord2CacheFileTrans(wstring cacheFile, wstring cacheFileTrans);
DWORD WINAPI __InsertBackupRecord4PurgeXML(wstring &cacheFileName, wstring SessId, const wchar_t *uniqueID);
DWORD UpdateVMListInBackup(LPCWSTR dest, VM_BACKUP_INFO &info);
DWORD ReadVMListBeforeRestore(LPCWSTR dest, VMInfoList &vmList);
DWORD ReadVMListBeforeRestorePerVM(LPCWSTR topDest, VMInfoList &vmList); //<sonmi01>2011-3-17 ###???
DWORD ReadVMBackupInfo(LPCWSTR vmDest, VMInfoList &vmList);


//sonle01: D2D sync activity log
DWORD WINAPI InitActivityLogTransXML(wstring &transFileNameXML);
DWORD WINAPI InsertActiveSyncLog2XML(wstring transFileNameXML, PActiveSyncLog logRec);

DRCORE_API DWORD WINAPI UpdateArchiveInfoDB(const wchar_t *pDir, const ARCHIVE_INFO_DB &infoDb, wstring xmlFileName);
DRCORE_API DWORD WINAPI GetArchiveInfoDB(const wchar_t *pDir, ARCHIVE_INFO_DB &infoDb, wstring xmlFileName);
DRCORE_API DWORD WINAPI GetArchiveScheduleInfo(const wchar_t* strFile, ARCHIVE_SCHEDULER_SETTINGS &infoDb);
DRCORE_API DWORD WINAPI GetArchiveDestination(const wstring strFile,
	PARCHIVEDISKDESTINFO &  pDiskDest,
	PARCHIVECLOUDDESTINFO & pCloudDest,
	DWORD				 & dwDestType);
DRCORE_API DWORD WINAPI CopyArchiveInfoDBToHistoryDir(const wchar_t *pSrcDir, const wchar_t *pHistoryDir, wstring xmlFileName);

DRCORE_API DWORD WINAPI GetArchiveDestinationProperties(const wchar_t* strFile, ARCHIVE_DESTINATION_PROPERTIES& infoDestProp);
// by khaga01 
DRCORE_API DWORD WINAPI UpdateArchiveInfoDBforFileCopy(const wchar_t *pDir, const ARCHIVE_INFO_DB &infoDb);
DRCORE_API DWORD WINAPI UpdateArchiveInfoDBForFileArchive(const wchar_t *pDir, const ARCHIVE_INFO_DB &infoDb);

DRCORE_API DWORD WINAPI GetArchiveInfoDBforFileCopy(const wchar_t *pDir, ARCHIVE_INFO_DB &infoDb);
DRCORE_API DWORD WINAPI GetArchiveInfoDBForFileArchive(const wchar_t *pDir, ARCHIVE_INFO_DB &infoDb);

DRCORE_API DWORD WINAPI GetArchiveScheduleInfoforFileCopy(const wchar_t* strFile, ARCHIVE_SCHEDULER_SETTINGS &infoDb);
DRCORE_API DWORD WINAPI GetArchiveScheduleInfoForFileArchive(const wchar_t* strFile, ARCHIVE_SCHEDULER_SETTINGS &infoDb);

DRCORE_API DWORD WINAPI GetArchiveDestinationforFileCopy(const wstring strFile,
	PARCHIVEDISKDESTINFO &  pDiskDest,
	PARCHIVECLOUDDESTINFO & pCloudDest,
	DWORD				 & dwDestType);
DRCORE_API DWORD WINAPI GetArchiveDestinationForFileArchive(const wstring strFile,
	PARCHIVEDISKDESTINFO &  pDiskDest,
	PARCHIVECLOUDDESTINFO & pCloudDest,
	DWORD				 & dwDestType);

DRCORE_API DWORD WINAPI CopyArchiveInfoDBToHistoryDirforFileCopy(const wchar_t *pSrcDir, const wchar_t *pHistoryDir);
DRCORE_API DWORD WINAPI CopyArchiveInfoDBToHistoryDirForFileArchive(const wchar_t *pSrcDir, const wchar_t *pHistoryDir);

DRCORE_API DWORD WINAPI GetArchiveDestinationPropertiesforFileCopy(const wchar_t* strFile, ARCHIVE_DESTINATION_PROPERTIES& infoDestProp);
DRCORE_API DWORD WINAPI GetArchiveDestinationPropertiesForFileArchive(const wchar_t* strFile, ARCHIVE_DESTINATION_PROPERTIES& infoDestProp);


/* copied form VCM project*/
typedef struct _DISK_EXTENT_INFO
{
	DWORD dwDiskNo; //disk No.
	DWORD dwSignature; //disk signature.
	ULONG ulSectorSize; //sector size.
	LONGLONG llStart; //offset on disk
	LONGLONG llLength; //extent length.
	LONGLONG llVolumeOffset; //offset on volume.
}DISK_EXTENT_INFO, *PDISK_EXTENT_INFO;

typedef struct _HA_VOLUME_INFO
{
	DWORD volType;
	DWORD dwClusterSize; //cluster size of this volume in bytes.
	ULONG ulOption;
	std::wstring strGuid; //volume GUID path like '\\?\Volume{717bac22-e1f1-11dd-8d97-806d6172696f}\'.
	std::wstring strMnt; //one of mount points for the volume. 
	ULONG ulFlags; //refer to VDS_VOLUME_FLAG.
	BOOL bBackup; //whether the volume is backed up or not.
	std::vector<DISK_EXTENT_INFO> vDiskExtent;
}HA_VOLUME_INFO, *PHA_VOLUME_INFO;

DWORD WINAPI GetVolumeDiskExtentInfo(const std::wstring &strCfg, std::vector<HA_VOLUME_INFO> &volInfoList);
/* end copied from VCM project*/


typedef struct _DISK_SIGANGURE_
{
	DWORD dwDiskType; //value is PARTITION_STYLE
	union
	{
		DWORD dwSignature;
		GUID	DiskId;
	};
}DISK_SIGANGURE, *PDISK_SIGANGURE;

typedef struct _DISK_IDENTITY
{
	DISK_SIGANGURE	diskSignature;
	LARGE_INTEGER	diskSize;
	DWORD			dwDiskNo;		// disk no.

	bool operator== (const _DISK_IDENTITY& other) const
	{
		if (PARTITION_STYLE_MBR == this->diskSignature.dwDiskType)
		{
			return (this->diskSignature.dwDiskType == other.diskSignature.dwDiskType &&
				this->diskSignature.dwSignature == other.diskSignature.dwSignature &&
				this->diskSize.QuadPart == other.diskSize.QuadPart);
		}
		else if (PARTITION_STYLE_GPT == this->diskSignature.dwDiskType)
		{
			return (this->diskSignature.dwDiskType == other.diskSignature.dwDiskType &&
				0 == memcmp((void *)&(this->diskSignature.DiskId), (void *)&(other.diskSignature.DiskId), sizeof(GUID)) &&
				this->diskSize.QuadPart == other.diskSize.QuadPart);
		}
		else
		{
			return (this->diskSignature.dwDiskType == other.diskSignature.dwDiskType &&
				this->diskSize.QuadPart == other.diskSize.QuadPart);
		}
	}
} DISK_IDENTITY, *PDISK_IDENTITY;

typedef std::vector<DISK_IDENTITY> DISK_IDENTITY_List;

DWORD WINAPI AFGetDiskIdentityFromAdrconfig(IN LPCWSTR strAdrConfigFileName,
	OUT DISK_IDENTITY_List& diskIndentityList);

typedef std::vector<DISK_EXTENT> DISK_EXTENT_LIST;

DWORD WINAPI AFGetAllVolumeDiskExtend(IN LPCWSTR lpVolName, OUT DISK_EXTENT_LIST& diskExtentList);

DWORD WINAPI AFGetDiskInfo(IN DWORD dwDiskNumber, OUT OPTIONAL PDISK_SIGANGURE pDiskSignature, OUT OPTIONAL PDISK_GEOMETRY_EX pDiskGeometry);

DWORD WINAPI OnlineDisks();

BOOL WINAPI IsHyperVVM();

BOOL WINAPI IsEsxVM();

DWORD WINAPI GetMemorySizeMB();

