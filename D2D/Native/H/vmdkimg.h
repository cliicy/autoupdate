#ifndef VMDKIMG_H
#define VMDKIMG_H

#pragma once
#include "BackupCommon.h"
#include "brandname.h"

#define VM_TYPE_HYPERV      0x00000001
#define VM_B_ENCRYPTION		0x00000002		//BSF_ENCRYPTED
#define VM_B_COMPRESSION	0x00000004		//BSF_COMPRESSED

#define VM_TYPE_OFFHOST		0x00000008

#define RESTORE_SEQUENTIAL   0x00000010
#define RESTORE_RANDOMORDER  0x00000011

// flags for IMAGE_JOB.job.restoreJob.dwFlags 
#define RESTORE_MULTI_TARGET 0x00000001

#define szARCFlashInstallRegistryKey  CST_REG_ROOT L"\\" _T("Installpath") 
#define szARCFlashRegistryKey  CST_REG_ROOT
typedef enum {
	VOLUME_SIMPLE=0,
	VOLUME_SPANNED,
	VOLUME_STRIPED,
	VOLUME_MIRRORED,
	VOLUME_RAID,
    VOLUME_SNAPSHOT,
	VOLUME_UNKNOWN
}VOLUME_TYPE;

typedef enum {
	DATATYPE_RESTORE_STAT = 0,
	DATATYPE_RESTORE_STAT_EX = 1, //bccma01 add phase for mount and unmount during restore
	DATATYPE_RESTORE_PHASE_MOUNT_VOLUME_START = 2,
	DATATYPE_RESTORE_PHASE_MOUNT_VOLUME_END = 3,
	DATATYPE_RESTORE_PHASE_RESTORE_DATA = 4,
	DATATYPE_RESTORE_PHASE_UNMOUNT_VOLUME_START = 5,
	DATATYPE_RESTORE_PHASE_UNMOUNT_VOLUME_END = 6,
	DATAYPE_UNKNOWN
} CALLBACK_DATATYPE;

typedef INT  (WINAPI *PFN_RestoreVHDSortSectors)(USHORT usVMDKFileIndex, ULONG* Sectors, ULONG ulSecCount);
typedef INT  (WINAPI *PFN_RestoreVMWareIFSLocate)(USHORT usVmdkFileIndex, ULONGLONG llOffset);
typedef BOOL (WINAPI *PFN_VMWareIFSReadFromTapeD)(PCHAR pBuffer, DWORD dwBytesRequested);
typedef void (WINAPI *PFN_FMSG)(USHORT usMsgType, UINT uiMsgID,...);
typedef void (WINAPI *PFN_JOBFMSG)(USHORT usMsgType,DWORD jobNumber,UINT uiMsgID,...);
typedef DWORD(WINAPI *PFN_JOBLOADSTRING)( DWORD ResouceID, TCHAR* szBuf, DWORD dwBufCount);
typedef INT  (WINAPI *PFN_NTAGENTCALLBACK)(PVOID pContext, CALLBACK_DATATYPE usDataType, PVOID pData);

typedef struct __RESTORE_IFS_STATISTIC
{
     ULONG             ulFileNum;
     ULONG             ulDirNum;
	 ULONG             ulFileSkippedNum;
	 ULONG             ulDirSkippedNum;
} RESTORE_IFS_STATISTIC, *PRESTORE_IFS_STATISTIC;

typedef struct __BACKUP_JOB_JOBINFO
{
     WCHAR          szVMName[128];
     WCHAR          szVMIP[64];
     ULONG          ulSessionNumber;
     ULONG          ulInternalSessionNumber;
     ULONG          ulOptions;
} BACKUP_IFS_JOBINFO, *PBACKUP_IFS_JOBINFO;

typedef struct _BACKUP_JOB_STATISTIC
{
	ULONG32	ulTotalDirs;
	ULONG32	ulTotalFiles;
	ULONG32	ulTotalDiskKB;
}BACKUP_JOB_STATISTIC, *PBACKUP_JOB_STATISTIC;

typedef struct _VM_DISK_INFO
{
	WCHAR		wszVMDKName[MAX_PATH];	// vmdk file name for this extent
	HANDLE		hDiskHandle;			//Handle for this file
	LONGLONG	ulExtentStartPos;		// The offset from the beginning of the disk to the extent, in bytes.
	LONGLONG	ulExtentLength;			//The number of bytes in this extent
	LONGLONG	ulVolumeOffset;			// volume offset corresponding to the start of this extent (ulExtentStartPos)
	DWORD		dwOrderOfExtentOnTape;
	D2D_ENCRYPTION_INFO D2DEncryptionInfo;
}VM_DISK_INFO, *PVM_DISK_INFO;


typedef struct _BACKUP_JOB
{
			TCHAR		*szDrive;				//Source drive name or guid or mount path
			ULONG		ulTypeofValueInDrive;   //type of value in the szDrive member
			TCHAR		*szVolumeType;          //volume type "FAT" "FAT32" or "NTFS"
			IN TCHAR	*pszCatalogFileName;	//Catalog file name to be sent to vmdkimg dll
			PBACKUP_IFS_JOBINFO pJobInfo;		//job info to be filled by ntagent dll
			PVOID		pTripletArray;			//Array required for calling SetPathMap function
												//in case of opening vhd files
			BACKUP_JOB_STATISTIC backupJobStatistic; //Backupjob stats returned to ntagent
													 //by vmkdimgdll
			TCHAR* pShadowCopy;
			ULONG* ulClusterNoList;
			ULONG  ulNumberOfClustersInList;
			TCHAR      *pszVolumeGuid; // this value is need for the Mounting the GRT VOlume
			TCHAR*      pwszDestPath;
			BOOL 		bDoArchive;
			TCHAR      *pwszCurrentSessionPath;
			TCHAR      *pwszPreviousSessionPath;
			BOOL        bProcessNTFSdedupFiles;
}BACKUP_JOB,*PBACKUP_JOB;

/*structure to be used for restore*/
typedef struct _RESTOREJOB
{
			PAFRESTVOLAPP pJobScript;
			TCHAR      *pszMntPathOrGuid;		      //Will be filled if the backup volume didn’t have drive letter
			PFN_RestoreVMWareIFSLocate    pfn_RestoreVMWareIFSLocate;
			PFN_VMWareIFSReadFromTapeD    pfn_VMWareIFSReadFromTapeD;
			PFN_RestoreVHDSortSectors	  pfn_RestoreVHDSortSectors;
			PRESTORE_IFS_STATISTIC pJobStatistic; //restore stats will be filled by vmdkimg dll
			DWORD       dwFlag;			          //for future use
}RESTOREJOB,*PRESTOREJOB;

//defined in pushagent.h
#define DEST_PATH_MAX  76
#define FILE_PATH_MAX  33

typedef struct 
{
	LONG   fileSize;       /* 339 File size (data + resource fork) */
	LONG   xferSize;       /* 343 # bytes tranferred (-1=>not available) */
	LONG   kBytesToTarget, /* 403   X              X */
		   filesToTarget;  /* 411   X              X */
	WCHAR  destPath[DEST_PATH_MAX];   /* 302 Full dest path name (vol:\dir drv:\dir vol::fdr /usr) */
	ULONG  attributes;         // BUGBUG for ASNT use 4 bytes in destpath
	WCHAR  fileName[FILE_PATH_MAX];   /*  335  FileName (long name for MAC) */
	char   nameSpace;      /* 344 DOS, MAC, NFS, OS/2, etc */
	char   padding1;
	USHORT dirsToTarget;   // was char padding[2];   //WCH for ASNT
	char   padding2[2];
}	RESTORE_STATISTICSW;

//ZZ: [2013/11/06 16:18] 
typedef enum
{
    ECF_NONE = 0,
    ECF_KEPT_MFT = 0x00000001     //ZZ: MFT file will be kept after generating catalog.
}E_CTRL_FLAG;

/*structure to be passed by ntagent dll to image dll for backup and restore*/
typedef struct _IMAGE_JOB
{
	TCHAR		**pVmdkFileName;			//filled for both backup and restore
    LONGLONG	*pDiskExtentStartingOffset;   //filled for both backup and restore
    LONGLONG	*pDiskExtentLength;           //filled for both backup and restore
	LONGLONG	*pVolumeOffset;				//filled for both backup and restore
	DWORD		dwNumOfDiskExtents; 		//filled for both backup and restore
	TCHAR       *pszPathToImageMetaDataFiles;    //filled for both backup and restore
    PFN_FMSG	pfn_LogMsg;				//Log function	
	PVOID       pContext;				//parameter to log function
	PFN_NTAGENTCALLBACK pfn_callback;	// generic NT agent callback routine
	PSHORT		p_isJobCancelled; //pidma02: fix for issue 17275080 Job Cancelled status
	PBACKUP_JOB    pbackupjob;
	PRESTOREJOB	   pRestoreJob;
	int jobNumber;
	D2D_ENCRYPTION_INFO D2DEncryptionInfo;
	DWORD       dwCtrlOpt;    //ZZ: [2013/11/06 16:17] Give more control right to caller.
}IMAGE_JOB, *PIMAGE_JOB;

typedef struct _CVcnLcnLength
{
        LONGLONG m_Vcn;      //file virtual start cluster number (this field is not a MUST)
        LONGLONG m_Lcn;       //volume start cluster number
        LONGLONG m_Length; //cluster count
}CVcnLcnLengthEx, *PCVcnLcnLengthEx;

USHORT WINAPI CstGetRegRootString(PTCHAR name, USHORT limit);
USHORT WINAPI CstGetProductsString(PTCHAR name, USHORT limit);
DWORD GetParamVal(PTCHAR szValName,
					   PDWORD pdwValue,
					   DWORD dwDefaultValue);

DWORD GetRegistryString(PTCHAR szKey, PTCHAR szValName, PTCHAR szValue, 
						BOOL bCreate, PTCHAR szDefaultValue);
DWORD initLogFunction();

//ZZ: [2014/02/18 16:07] Added for control logic of catalog.
typedef enum
{
    EIO_NONE = 0,
    EIO_DISABLE_ALLOCATION_SIZE_AS_VALID_SIZE = 0x00000001,
    EIO_DISABLE_RTC_28180_OFFSET_14BIT_2_16BIT = 0x00000002,
    EIO_DISABLE_RTC_108902_PRECREATE_UNNAMED_STREAM = 0x00000004,
	EIO_DISABLE_RTC_174113_FILE_SIZE_DIFF_IDX_AND_$DATA = 0x00000008,
	EIO_DEAL_WITH_SPECIFIED_FILE_RECORDS_4_DEBUG = 0x00000010,
	EIO_DUMP_ALL_FILE_INFO_WHEN_TRAVERSE = 0x00000020,
	EIO_DISABLE_RTC_192031_$SECURE_ATTR_LIST_MULTI_FILE_REF = 0x00000040,
    EIO_DISABLE_RTC_193805_IGNORE_FILE_RECORD_MARK_AS_NOT_USED = 0x00000080,
    EIO_COLLECT_MORE_PERFORMANCE_STATISTIC_INFO = 0x00000100,
    EIO_DUMP_OFFSET_AND_SIZE_4_READ_FROM_SESS = 0x00000300,
    EIO_DUMP_FILE_INFO_WHEN_MISMATCH_DATA_SIZE = 0x00000400,
    EIO_DUMP_MEMORY_ALLOCATE_AND_FREE = 0x00000800,
	EIO_GEN_FULL_MEMORY_DUMP = 0x00000800
}E_IMG_OPT;

typedef enum
{
    EFT_NONE = 0,
    EFT_POINTER_TRUNCATED = 0x00000001
}E_FAULT_TYPE;

typedef struct _stPerfInfo
{
    ULONGLONG m_ullSeekVHDCost;
    ULONGLONG m_ullReadVHDCost;
    ULONGLONG m_ullReadVHDTimes;
    ULONGLONG m_ullSeekVHDTimes;
    ULONGLONG m_ullReadVHDTotalSize2Read;
    ULONGLONG m_ullReadVHDTotalSizeRead;
    ULONGLONG m_ullLastContinuousBlockOffset;
    ULONGLONG m_ullLastContinuousBlockSize;
    ULONGLONG m_ullMaxContinuousBlockOffset;
    ULONGLONG m_ullMaxContinuousBlockSize;
}ST_PERF_INFO, *PST_PERF_INFO;

typedef struct _stPerfStat
{
    //ZZ: performance statistic for traverse MFT.
    ST_PERF_INFO m_stPerfInfo;
    //ZZ: performance statistic for dump MFT.
    ST_PERF_INFO m_stPerfInfo4MFT;
    //ZZ: Common performance statistic
    ULONGLONG m_ullLastSeekVHDOffset;
    ULONGLONG m_ullLastReadVHDTotalSize;
    ULONGLONG m_ullLastReadVHDSize;
	ULONGLONG m_ullTotalMemoryAllocated;
	ULONGLONG m_ullTotalMemoryFreed;
	ULONGLONG m_ullTotalMemoryTaken;
}ST_PERF_STAT, *PST_PERF_STAT;

extern ST_PERF_STAT g_stPerfStat;
extern DWORD g_dwVMImageOption;
extern DWORD g_dwFaultDetected;
extern DWORD g_dwFileRec2ParseCnt;
extern PULONGLONG g_pullFileRec2Parse;
extern DWORD g_dwMaxDataRunCnt4Attr;
extern DWORD g_dwDirStructIndent;
extern PBYTE g_pbVHDDataCache;
extern DWORD g_dwVHDDataCacheSize;
extern ULONGLONG g_ullVHDDataOffsetInCache;


#define DEFAULT_DIR_STRUCT_INDENT_IN_CHAR           2
#define MAKEI64U(HI32, LO32)     ((ULONGLONG)(((DWORD)(((DWORD_PTR)(LO32)) & 0xFFFFFFFF)) | ((ULONGLONG)((DWORD)(((DWORD_PTR)(HI32)) & 0xFFFFFFFF))) << 32))  
#define MAKEFILEREC(HI32, LO32)  ((ULONGLONG)(((DWORD)(((DWORD_PTR)(LO32)) & 0xFFFFFFFF)) | ((ULONGLONG)((DWORD)(((DWORD_PTR)(HI32)) & 0x0000FFFF))) << 32))  
#define LO32(QW)                 ((DWORD)(QW))
#define HI32(QW)                 ((DWORD)(((QW) >> 32) & 0xffffffff))
#define MAX_DATA_RUN_COUNT_FOR_ATTRIBUTE            512

#endif