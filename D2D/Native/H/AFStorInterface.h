#pragma once

#include "asdefs.h"
#include "D2dbaseDef.h"
#include "Log.h"

enum CHECK_POINT_RESULT_TYPE
{
	RES_UNKNOW = 0x0,
	RES_CHECK_MATCH = 0x01,
	RES_CHECK_MISMATCH = 0x02,
	RES_SIBAT_NOT_FOUND = 0xf1,
	RES_DATA_NOT_FOUND = 0xf2,
	RES_SIBAT_ERROR = 0xf3,
};

enum EVENT_MESSAGE_LEVEL
{
	MSG_LEVEL_INFO = 0,
	MSG_LEVEL_WARNING = 1,
	MSG_LEVEL_ERROR = 2
};

typedef DWORD (*PFUNJOBLOGACTIVITYEX)(WCHAR* vmUUID,DWORD Flags, DWORD JobNo, DWORD ResouceID, va_list Start);

typedef DWORD (*PFUNUPDATEJOBCONTEXT)( const PJOBCTX pCtx, DWORD dwProcessId );

class IEventMessage
{
public:
	//the implementer to instantiate the method and call it to set the job ID
	virtual void SetJobId(DWORD dwJobId) = 0;
	virtual void SetUuid(LPCWSTR lpUuid) = 0;
	virtual DWORD GetJobId() const = 0;
	virtual BOOL GetUuid(LPWSTR lpUuid, DWORD dwSize) = 0;
	virtual void SetLogCallBack(PFUNJOBLOGACTIVITYEX pLog) = 0;
	virtual PFUNJOBLOGACTIVITYEX GetLogCallBack() const = 0;
	virtual void SetJobType(DWORD dwJobType) = 0;
	virtual void SetProductType(DWORD dwProductType) = 0;
	virtual DWORD GetJobType() const = 0;
	virtual DWORD GetProductType() const = 0;
};

DWORD WriteBMRLog(DWORD dwLevel, DWORD dwResourceId, ...);

class CActiveMsg:public IEventMessage
{
public:
	CActiveMsg() {m_dwJobId = 0; ZeroMemory(m_szUuid, sizeof(m_szUuid)); m_pfnLog = NULL; m_dwJobType = 0; m_dwProductType=0;};
	~CActiveMsg() {};
	void SetJobId(DWORD dwJobId) { m_dwJobId = dwJobId;};
	void SetUuid(LPCWSTR lpUuid)
	{
		if((lpUuid == NULL) || (lpUuid[0] == 0))
		{
			ZeroMemory(m_szUuid, sizeof(m_szUuid));
		}
		else
		{
			wcscpy_s(m_szUuid, ARRAYSIZE(m_szUuid), lpUuid);
		}
	}

	void SetLogCallBack(PFUNJOBLOGACTIVITYEX pLog) {m_pfnLog = pLog;}
	DWORD GetJobId()const { return m_dwJobId;}
	BOOL GetUuid(LPWSTR lpUuid, DWORD dwSize)
	{
		size_t iSize = wcslen(m_szUuid);
		if((iSize) && (iSize < dwSize))
		{
			wcscpy_s(lpUuid, dwSize, m_szUuid);
			return TRUE;
		}
		return FALSE;
	}

	PFUNJOBLOGACTIVITYEX GetLogCallBack() const { return m_pfnLog;}

	void SetJobType(DWORD dwJobType) {m_dwJobType = dwJobType;};
	void SetProductType(DWORD dwProductType) {m_dwProductType = dwProductType;};
	DWORD GetJobType() const {return m_dwJobType;};
	DWORD GetProductType() const {return m_dwProductType;};
private:
	DWORD m_dwJobId;
	WCHAR m_szUuid[64];
	PFUNJOBLOGACTIVITYEX m_pfnLog;
	DWORD m_dwJobType;
	DWORD m_dwProductType;
};

typedef struct _DISK_BITMAP_BUFFER_
{
	ULONGLONG ullTotalBlock; //the disk total block count
	DWORD     dwBlockSize;   //the size of 1 bit indicated
	ULONGLONG ullStartBlock; //the start block requested as an input
	ULONGLONG ullBitmapSize; // the number of the blocks on the disk, starting from the ullStartBlock member of the structure 
	BYTE      Buffer[4];     // it means same as Buffer[4] member of VOLUME_BITMAP_BUFFER structure
}DISK_BITMAP_BUFFER, *PDISK_BITMAP_BUFFER;

enum DISK_EXT_TYPE
{
	DISK_D2D = 0,
	DISK_VHD = 1,
};

//replace old vhd by MPII, by danri02, 2013-05

// the following type is used to specify session format when do backup.
enum DISK_BACKUP_FORMAT_CATGORY_TYPE
{
	DISK_BACKUP_FORMAT_INVALID		= 0,	
	DISK_BACKUP_FORMAT_OLD			= 0x80,		// including the Old D2D COMPRESS FORMAT and MS VHD(if non-compress and non-encryption)
	DISK_BACKUP_FORMAT_NEW			= 0x100,	// including the data slice format and Dedup format.
	DISK_BACKUP_FORMAT_DEDICATE_MPII = 0X200	// used by lite intergation
};

// the following type is used to described the already backed up session disk format.
enum SESS_DISK_FORMAT_SPECFICATION
{
	SESS_DISK_FORMAT_INVALID		= 0,
	SESS_DISK_FORMAT_SINGLE_FILE	= 1,  // Old D2D COMPRESS FORMAT.
	SESS_DISK_FORMAT_VHD_FILE		= 2,  // Data will be store as MS VHD FORMAT.
	SESS_DISK_FORMAT_SLICE_FILE		= 3,  // Slice format.
	SESS_DISK_FORMAT_DEDUP			= 4	  // Disk data will be store in dedup system.
};

typedef struct st_SESS_FORMAT_DESCRIPTOR
{
	SESS_DISK_FORMAT_SPECFICATION  diskfomat;
	unsigned int				   disksig;
	unsigned int				   diskverion;
	unsigned int				   disk_software_version;
	unsigned int				   diskmanagement;			// 1 = Manage by D2D, 2 = Manage by RPS. 0 = previous R17 backed up session.
	unsigned int				   compresstype;
	unsigned int				   encryptionAltype;
	unsigned int				   encLibType;
	unsigned int				   sliceSizeInMB; // if the format is slice format.
	unsigned int				   fullbackupdisk;
}SESS_FORMAT_DESCRIPTOR,*PSESS_FORMAT_DESCRIPTOR;
//replace old vhd by MPII, by danri02, 2013-05, end

typedef struct _stPreReadInfo
{
    BOOL bEstimation;
    BOOL bIsVolume;
    const WCHAR* szVolumeName;
    DWORD dwVolumeClusterSize;
    VOID*  pVolBitmapMng;
}ST_PREREAD_INFO, *PST_PREREAD_INFO;

struct IAFStorErrorDesc
{
	virtual DWORD GetErrorCode() = NULL;
	virtual LPCWSTR GetErrorString() = NULL;
	virtual VOID Release() = NULL;
};

struct IAFStorBase
{
	virtual IAFStorErrorDesc* GetLastErrorDesc() = NULL;
	virtual BOOL Release() = NULL; //delete object
};

struct IAFVHD : IAFStorBase
{
	//must align to 512 bytes
	virtual BOOL Read(void* pBuffer, DWORD dwNumberOfBytesToRead, DWORD* pdwNumberOfBytesRead) = NULL;
	//must align to 512 bytes
	virtual BOOL Seek(LARGE_INTEGER liSeekTo, PLARGE_INTEGER pliNewPosition, DWORD dwSeekMethod) = NULL;
	virtual BOOL SortSectors(ULONG* pSectorIndexArray, ULONG ulCount) = NULL;
	virtual BOOL GetSectorCount(ULONG* pCount) = NULL;
	virtual BOOL GetSectorList(ULONG* pList, DWORD pCount) = NULL;
	virtual BOOL GetDiskBitMap(ULONGLONG ullStartOffset, PDISK_BITMAP_BUFFER* ppDiskBitmapBuffer) = NULL;
	virtual BOOL FreeDiskBitMapBuffer(PDISK_BITMAP_BUFFER pDiskBitmapBuffer) = NULL;

	virtual void SetAccount4D2D(const wchar_t* pwszD2DUsr, const wchar_t* pwszD2DPwd) = NULL;
    virtual void SetAccount4DST(const wchar_t* pwszDSTUsr, const wchar_t* pwszDSTPwd) = NULL;
	virtual ULONG GetBlockSize() = NULL;

	virtual DWORD GetChangeIDXFile(UINT fromSessionNo,UINT toSessionNo, wchar_t* tempIDXFilePath,wchar_t* IDXFileName,BOOL& bFullBackup) = NULL;

	virtual DWORD CheckDataByIndex(LONGLONG llStartBlockIdx, DWORD dwBlockCount, BYTE* btResult) = NULL;
};

struct IAFSessionBlock : IAFStorBase
{
	virtual BOOL Read(void* pBuffer, DWORD dwNumberOfBytesToRead, DWORD* pdwNumberOfBytesRead) = NULL;
	virtual BOOL Seek(LARGE_INTEGER liSeekTo, PLARGE_INTEGER pliNewPosition, DWORD dwSeekMethod) = NULL;
};

struct IAFDiskEnumerator : IAFStorBase
{
    virtual BOOL GetCount(DWORD* pdwCount) = NULL;
    virtual BOOL Reset() = NULL;
    virtual BOOL Next(DWORD dwRequested, DWORD* pdwBufferOfDiskSignature, DWORD* pdwReturned) = NULL;
};

struct IAFSession : IAFStorBase
{
	//input the same BLI commands as what we do in BLI feature
	virtual DWORD Write(void* pBufferToWrite, DWORD dwSizeToWrite ) = NULL;
	//this function will return the same stream as TSIReadBlockDaemon in BLI feature
	virtual DWORD Read(void* lpBuffer, DWORD dwNumberOfBytesToRead, DWORD* pdwNumberOfBytesRead) = NULL;
	virtual DWORD Seek(LARGE_INTEGER liSeekTo, PLARGE_INTEGER pliNewPosition, DWORD dwSeekMethod) = NULL;

	//open a VHD file for read
    virtual BOOL OpenVHD(LPCWSTR pszVHDFilename, OUT IAFVHD** ppVHD, wchar_t *lckFilePath = NULL, PST_PREREAD_INFO pPreReadInfo = NULL) = NULL;
    virtual BOOL OpenVHD(DWORD dwDiskSignature, OUT IAFVHD** ppVHD, wchar_t *lckFilePath = NULL, PST_PREREAD_INFO pPreReadInfo = NULL) = NULL;
	//open a session block for read
	virtual BOOL OpenBlock(DWORD dwBlockID, OUT IAFSessionBlock** ppBlock) = NULL;

	virtual BOOL GetSessionNumber(DWORD* pdwSessionNum) = NULL;

	virtual BOOL GetCompressedSessSize(PLARGE_INTEGER pi64Size) = NULL;

	virtual BOOL Flush(void) = NULL;

	//
	virtual BOOL SetMaxWriteThroughput(LARGE_INTEGER maxWriteThroughput) = NULL;

	virtual BOOL GetAllDiskCompressedSize(PLARGE_INTEGER pi64Size) = NULL;
	virtual void SetBackupDataSize(LARGE_INTEGER BackupSize,ULONG ulDiskID) = NULL;

	virtual BOOL SetEventMessageCallBack(IEventMessage* pEventMsg) = NULL;

    virtual BOOL EnumDisks(IAFDiskEnumerator** ppEnum) = NULL;

	virtual BOOL GetDeDupeInfo(LONGLONG& nTotalSize,LONGLONG& nUniqueSize,LONGLONG& nCompressedSize) = NULL;
	virtual BOOL GetRealTimeDeDupeInfo(LONGLONG& nTotalSize,LONGLONG& nUniqueSize,LONGLONG& nCompressedSize) = NULL;
	
	virtual void SetAccount4D2D(const wchar_t* pwszD2DUsr, const wchar_t* pwszD2DPwd) = NULL;
	virtual void SetAccount4DST(const wchar_t* pwszDSTUsr, const wchar_t* pwszDSTPwd) =NULL;

	virtual BOOL SetEncInfo(PD2D_ENCRYPTION_INFO pEncInfo) = NULL;
    
    virtual BOOL IsBlockExist(DWORD dwBlockID) = NULL;
};

struct IAFSessionEnumerator : IAFStorBase
{
	virtual BOOL GetCount(DWORD* pdwCount) = NULL;
	virtual BOOL Reset() = NULL;
	virtual BOOL Next(DWORD dwRequested, DWORD* pdwBufferOfSessNum, DWORD* pdwReturned) = NULL;
};

typedef struct _stDiskMergeInfo
{
	DWORD     dwCurDiskSig2Merge;  ///ZZ: Disk signature of disk being merged/
	ULONGLONG ullDiskBytes2Merge;  ///ZZ: How much data on disk to merge.
	ULONGLONG ullDiskBytesMerged;  ///ZZ: How much data on disk has been merged
}ST_DISK_MERGE_INFO, *PST_DISK_MERGE_INFO;

typedef struct _stSessMergeInfo
{
	DWORD     dwSessCnt2Merge;       ///ZZ: How many sessions will be merged.
	DWORD     dwDiskCnt2Merge;       ///ZZ: How many disks will be merged.
	ULONGLONG ullTotalBytes2Merge;   ///ZZ: Total data size to merge in this job.
	ULONGLONG ullTotalBytesMerged;   ///ZZ: Total data size has been merged.
	BYTE      plistDiskMergeInfo[1]; ///ZZ: Disk merge information list, its size is defined by dwDiskCnt2Merge. (dwDiskCnt2Merge * sizeof(ST_DISK_MERGE_INFO))
}ST_SESS_MERGE_INFO, *PST_SESS_MERGE_INFO;

typedef struct _stMergeStatistics
{
	DWORD     dwMergePhase;        ///ZZ: Which kind stage of merge, such as lock session, merge data and so on. Refer to E_JOB_PHASE
	DWORD     dwSessCnt2Merge;     ///ZZ: How many sessions will be merged.
	DWORD     dwSessCntMerged;     ///ZZ: How many sessions have been merged.
	DWORD     dwCurSess2Merge;     ///ZZ: Current session number which is being merged.
	DWORD     dwDiskCnt2Merge;     ///ZZ: How many disks will be merged in one session
	DWORD     dwDiskCntMerged;     ///ZZ: How many disks have been merged for some session.
	DWORD     dwCurDiskSig2Merge;  ///ZZ: Disk signature of disk being merged/
	ULONGLONG ullBytesProcessed;   ///ZZ: How much data is processed in one report timeline.
	ULONGLONG ullDiskBytes2Merge;  ///ZZ: How much data on disk to merge.
	ULONGLONG ullDiskBytesMerged;  ///ZZ: How much data on disk has been merged
	ULONGLONG ullSessBytes2Merge;  ///ZZ: How much data in session to merge.
	ULONGLONG ullSessBytesMerged;  ///ZZ: How much data in session has been merged.
	ULONGLONG ullTotalBytes2Merge; ///ZZ: Total data size to merge in this job.
	ULONGLONG ullTotalBytesMerged; ///ZZ: Total data size has been merged.
	ULONGLONG ullTotalBytesReleased; //ZZ: Total Release disk space by this merge job.
	double	  fProcessPercent;
}ST_MERGE_STAT, *PST_MERGE_STAT;

typedef struct _stBKDestInfo
{
	ULONGLONG ullFreeSpce;
	ULONGLONG ullTotalSize;
	ULONGLONG ullSpaceNeeded;
	WCHAR     wzBKDest[MAX_PATH];
}ST_BKDEST_INFO, *PST_BKDEST_INFO;

typedef struct _stAFStorConfiguration
{
    BOOL bEnableDataSlice;
    DWORD dwSliceSizeInMB;
}ST_AFSTOR_CONFIGURATION, *PST_AFSTOR_CONFIGURATION;

typedef struct _stDataStorePath
{
    BOOL bDeDupe;//1 for dedupe datastore, 0 for non-dedupe datastore

    WCHAR szComPath[MAX_PATH];//the common path

	//if it is not dedupe datastore, the following path are not used.
	WCHAR szIndexPath[MAX_PATH];//the dedupe index path
	WCHAR szHashPath[MAX_PATH];//the dedupe hash path
	WCHAR szDataPath[MAX_PATH];//the dedupe data path

}ST_DATASTORE_PATH, *PST_DATASTORE_PATH;

typedef struct _stDataStoreSize
{
	LONGLONG llRawSize;//the total session raw data size
	LONGLONG llUniqueSize;//the total session after deduped size(non-dedupe data store is 0) 
	LONGLONG llCompSize;//the total deduped data or raw data after compressed size

    LONGLONG llComPathSize;//the total file size on common path
	LONGLONG llFreeVolumeSizeOfComPath;//the free volume size which common path located in
	LONGLONG llTotalVolumeSizeOfComPath;//the total volume size which common path located in

	LONGLONG llIndexPathSize;//the total file size on index path, this variable only used for dedupe datastore
	LONGLONG llFreeVolumeSizeOfIndexPath;//the free volume size which index path located in, this variable only used for dedupe datastore
	LONGLONG llTotalVolumeSizeOfIndexPath;//the total volume size which index path located in, this variable only used for dedupe datastore

	LONGLONG llHashPathSize;//the total file size on hash path, this variable only used for dedupe datastore
	LONGLONG llFreeVolumeSizeOfHashPath;//the free volume size which hash path located in, this variable only used for dedupe datastore
	LONGLONG llTotalVolumeSizeOfHashPath;//the total volume size which hash path located in, this variable only used for dedupe datastore

	LONGLONG llDataPathSize;//the total file size on data path, this variable only used for dedupe datastore
	LONGLONG llFreeVolumeSizeOfDataPath;//the free volume size which data path located in, this variable only used for dedupe datastore
	LONGLONG llTotalVolumeSizeOfDataPath;//the total volume size which data path located in, this variable only used for dedupe datastore

}ST_DATASTORE_SIZE, *PST_DATASTORE_SIZE;

enum ToBeMergedSessionDiskFormat
{
	FORMAT_UNSPECFIC	=	0x00000000,
	FORMAT_IN_D2D		= 	0x00000001,		// Stored in old D2D format
	FORMAT_IN_SLICE		=	0x00000002,		// Stored in data slice format
	FORMAT_IN_DEDUPE	= 	0x00000004		// Stored in dedupe format

};

struct IAFStorCallbacks
{
	virtual ~IAFStorCallbacks(){};
	virtual void PurgeCallback(ULONG ulSessionNumber, ULONGLONG ullTotalSize, ULONGLONG ullProcessedSize){};
	virtual DWORD OnPurgeStart(ULONG ulSessionNumber){return 0;};
	virtual void OnPurgeFinish(ULONG ulSessionNumber, BOOL bSucceeded){};
	virtual BOOL SortingSIBAT_Begin(void){return TRUE;};
	virtual BOOL SortingSIBAT_Progress(double fProg){return TRUE;};
	virtual BOOL ScanD2D_Begin(LONGLONG llTotalSectors){return TRUE;};
	virtual BOOL ScanD2D_Progress(double fProg){return TRUE;};
	virtual BOOL ScanD2D_GetHole(LONGLONG llSectorOffset, LONGLONG llHoleSizeInSector){return TRUE;};
	virtual BOOL ScanD2D_IsNeedStop() {return FALSE;};
	virtual BOOL ScanD2D_End(void){return TRUE;};

	///ZZ: Add some callback function to control merge job and get performance information.
	virtual BOOL IsMergeJobStop() { return FALSE; } 
	virtual void MergeJobStat(PST_MERGE_STAT pstMergeStat) {}
	virtual long OnMergeStart(PDWORD pdwSess2MergeList, DWORD dwSess2MergeCnt) { return 0; }
	virtual long OnMergeEnd(BOOL bSucceeded) { return 0; }
	virtual void UpdateBKDestInfo(const ST_BKDEST_INFO& stBKDestInfo) {}
	virtual long ValidateBKDestForMerge() { return 0; }
	//Begin: danri02 fix issue: 11122
	virtual DWORD CurMergingSessDiskFormat(DWORD DiskFormat,BOOL IsSetFormat=FALSE){return 0;}
	//END: danri02 fix issue:11122
};

//danri02:Begin Variable retention

typedef enum _en_E_MERGE_JOB_STATUS
{
	EMJ_STATUS_NULL			= 0,
	EMJ_STATUS_START		= 1,	
	EMJ_STATUS_PAUSED		= 2,
	EMJ_STATUS_DONE			= 9
} E_MERGE_JOB_STATUS;

#pragma pack(push, 1)

//512 Bytes.
typedef union _st_Merge_Job_Range_Info
{
	struct
	{
		DWORD		dwMergeMethod;			//Refer to E_MERGE_METHOD
		DWORD		dwStartSessNum;
		DWORD		dwEndSessNum;
		DWORD		dwMergeStatus;			// Refer to E_MERGE_JOB_STATUS

		ULONGLONG	ullTotalBytes2Merge;	//Total logic data size to merge
		ULONGLONG	ullTotalBytesMerged;	//Total logic data size has been merged
		ULONGLONG	ullActualBytes2Merge;	//Total actual data size to merge
		ULONGLONG	ullActualBytes2Merged;	//Total actual data size has been merged
		
		ULONGLONG	ullTotalBytes2Purge;	//Total logic data size to purge
		ULONGLONG	ullTotalBytesPurged;	//Total logic data size has been purged
		ULONGLONG	ullActualBytes2Purge;	//Total actual data size to purge
		ULONGLONG	ullActualBytesPurged;	//Total actual data size has been purged

		DWORD		dwSessCount;
		BOOL		bSkipForChangeDestDiskFull;
	};

	char szReserved[512];
} Merge_Job_Range_Info, *PMerge_Job_Range_Info;

//4096 byte align
typedef struct _st_Merge_Job_Status
{
	DWORD		dwCheckSum;
	DWORD		dwMajorVer;
	DWORD		dwMinVer;
	DWORD		dwJobID;
	DWORD		dwProcessID;
	DWORD		dwJobStatus;	//Refer to E_MERGE_JOB_STATUS
	DWORD		dwPrdType;		//The product type of merge job (RPS or D2D), used to write activity log when merge job crashed
	DWORD		dwRangeCount;
	DWORD		dwMergeMethod;	// before r17 or after r17.
	ULONGLONG	ullJobStartTime;
	DWORD		Reserve[117];

	Merge_Job_Range_Info		arMergeRangeStatus[7];
} Merge_Job_Status, *PMerge_Job_Status;

#pragma pack(pop)

//One this class instance can only handle one root folder.
struct IAFStorMergeStatus
{
	virtual	long Initialize(LPCWSTR pszRootFolder) = 0;
	virtual BOOL IsStatusFileExist(void) = 0;
	virtual LPCTSTR GetStoredDestPath(void) = 0;
	virtual long ReadJobStatus(Merge_Job_Status** ppJobStatus) = 0;
	virtual long WriteJobStatus(Merge_Job_Status* pJobStatus) = 0;
	virtual long AllocateJobStatus(DWORD dwRangeCount, Merge_Job_Status** ppJobStatus) = 0;
	virtual	void ReleaseJobStatus(Merge_Job_Status* pJobStatus) = 0;
	virtual	long DeleteJobStatusFile(void) = 0;
	virtual	long DeleteRangeStatusFile(void) = 0;
};
//danri02:End Variable retention

struct IAFStorBuf
{
public:
	virtual ULONG	AddRef()			= 0;
	virtual ULONG	Release()			= 0;
	virtual BOOL	IsBufAvailable()	= 0;
	virtual ULONG	GetBufSize()		= 0;
	virtual LPVOID	GetBuf()			= 0;
};

struct IAFStorDev : IAFStorBase
{
	virtual BOOL NewSession(OUT IAFSession** ppSession, IN PD2D_ENCRYPTION_INFO pEncryptionInfo,int ExtType = DISK_D2D) = NULL;
	virtual BOOL OpenSession(DWORD dwSessionNumber, OUT IAFSession** ppSession, IN PD2D_ENCRYPTION_INFO pEncryptionInfo) = NULL;
	virtual BOOL OpenSession2(DWORD dwSessionNumber, OUT IAFSession** ppSession, IN PD2D_ENCRYPTION_INFO pEncryptionInfo, IEventMessage* pEventMsg) = NULL;
	virtual BOOL FindSessNumOfParentDisk(DWORD dwDiskSig, OUT DWORD* pdwSessNum) = NULL;
	virtual BOOL GetVDiskName(DWORD dwSessionNumber, DWORD dwDiskSig, WCHAR* pVDiskName, int nSizeinWCHAR) = NULL;
	virtual BOOL EnumSessions(IAFSessionEnumerator** ppEnum) = NULL;

	virtual BOOL PurgeSession(ULONG ulSessionNumber, IAFStorCallbacks* pCallback, IEventMessage* pEventMsg = NULL) = NULL;
	virtual BOOL ContinueFailedPurge(IAFStorCallbacks* pCallback) = NULL;
	virtual BOOL DeleteSession(ULONG ulSessionNumber) = NULL;

	virtual BOOL NewSession2(LPCWSTR pszDomain, LPCWSTR pszUserName, LPCWSTR pszPassword, OUT IAFSession** ppSession, IN PD2D_ENCRYPTION_INFO pEncryptionInfo, DWORD dwStoreFormat = DISK_BACKUP_FORMAT_NEW, int ExtType = DISK_D2D) = NULL; //2010-5-10 encrypt support

	///ZZ: Merge session by a session number range. Invalid session data will be merged from session number larger than ulStartSessNum to session ulEndSessNum.
	///ZZ: If both ulStartSessNum and ulEndSessNum are zero, we will merge all sessions failed in last merge.
	virtual BOOL MergeSession(DWORD dwMergeMethod, ULONG ulStartSessNum, ULONG ulEndSessNum, IAFStorCallbacks* pCallback, IEventMessage* pEventMsg = NULL) = NULL;
	virtual long GetDiskHeader(PBYTE pbDiskHdr, DWORD* pdwDiskHdrSize, DWORD dwSessNum, DWORD* pdwDiskSig) = NULL;
	//
	//@dwMergeMethod, please reference E_MERGE_METHOD which is declared in AFMergeMgrInterface.h
	//
	//@ulStartSessNum, 0 means purge the session from the oldest full session,
	//                      non-0 menas purge the session from the specified session
	//@ulEndSessNum, remain ulRetentionCount session from ulBaseSessionNumber after purge finished
	//
	//virtual BOOL PurgeSessionEx(DWORD dwMergeMethod, ULONG ulStartSessNum, ULONG ulEndSessNum, IAFStorCallbacks* pCallback, IEventMessage* pEventMsg = NULL) = NULL;
	
	//For variable retention
	//pRangeInfo: merge range info; pszStatusDestPath: the path which the job status file locate at
	virtual BOOL PurgeSessionEx(Merge_Job_Range_Info * pRangeInfo, LPCWSTR pszStatusDestPath, DWORD dwRangeCount, DWORD dwRangeIndex, BOOL bResume, IAFStorCallbacks* pCallback, IEventMessage* pEventMsg = NULL) = NULL;
	
	// add by danri02 for 144800
	virtual BOOL CheckSessionFailure_Use_BackupInfoXmlFile(ULONG ulSessionNumber){return FALSE;};
	virtual long GetBufInterface(IAFStorBuf** ppBufInterface, ULONG ulBufSize) = 0;
	//End added by danri02

	//danri02:Begin Variable retention
	virtual long CreateMergeStatusInterface(IAFStorMergeStatus ** ppMergeStatus, LPCWSTR pszRootFolder) = 0;
	virtual void ReleaseMergeStatusInterface(IAFStorMergeStatus * pMergeStatus) = 0;
	//danri02:End Variable retention

};

struct IAFReclaim : IAFStorBase
{
	//San the  D2D file,should use the callback object to control the scan progress
	virtual BOOL Scan(IAFStorCallbacks* pCallback) = NULL;
	//virtual BOOL GetBackupTime(SYSTEMTIME* pTime) = 0;
	//virtual BOOL GetFileSize(LARGE_INTEGER* pFileSize) = 0;
	//virtual BOOL GetDiskSize(LARGE_INTEGER* pDiskSize) = 0;
	//virtual BOOL GetUniqueID(GUID* pID) = 0;
	//check if this D2D file need to be scaned.
	virtual BOOL IsNeedScan() = NULL;
	virtual BOOL CreateHoleFile() = NULL;
};

// baide02, the VHD meta (disk*.mta)'s interface
struct IAFVHDMta : IAFStorBase
{
    virtual BOOL Open(LPCWSTR pszRootFolder, DWORD dwSessNum, DWORD dwDisk) = NULL;
    virtual BOOL Open(LPCWSTR pszMTAFile) = NULL;
    virtual BOOL GetParent(DWORD* pdwSessNum, DWORD* pdwDisk) = NULL;
    virtual BOOL GetChild(DWORD* pdwSessNum, DWORD* pdwDisk) = NULL;
    virtual BOOL SetParent(DWORD dwSessNum, DWORD dwDisk) = NULL;
    virtual BOOL SetChild(DWORD dwSessNum, DWORD dwDisk) = NULL;
    virtual BOOL Close(BOOL bUpdate) = NULL;
};

enum CREATE_SESS_ERR
{
	_cse_ok,
	_cse_not_enough_memory,
	_cse_invalid_parameter,
	_cse_invalid_destinations,
};

// danri02: Dedupe Merge optimal, @2013-06, code begin
struct IAFStorDedupSessProcessor : public IAFStorBase
{
	enum PROCESSMODE
	{
		MERGE_SIBAT = 1,
		CHECK_MERGE_ERROR_PURGENODE = 2
	};
	struct st_DedupSess_Info
	{
		wchar_t			szRootPath[MAX_PATH];
		unsigned int	uiSessNum;
		unsigned int	uiDiskSig;
		PROCESSMODE		mode;
	};
	struct _st_Failure_DedupSess_Info
	{
		wchar_t			szRootPath[MAX_PATH];
		unsigned int	uiSessNum;
		void*			pDataStoreConfigBuf;
		unsigned int	uiBufLen;
	};
	virtual long PostProcessReplicatedDedupSess(st_DedupSess_Info* pDedupSessInfo) = 0;
	virtual long PurgeFailureReplicatingDedupSess(_st_Failure_DedupSess_Info* pFailureReplicatedSessInfo) = 0;
	virtual long CheckMergeErrorForPurgeNodeData(st_DedupSess_Info* pDedupSessInfo) = 0;
};
// danri02: Dedupe Merge optimal, @2013-06, code end

#ifdef __cplusplus
extern "C"
{
#endif
	CREATE_SESS_ERR CreateDevObject(LPCWSTR pszRootFolder, IAFStorDev** ppDevObj);

	//create a new session. it's for backup job
	CREATE_SESS_ERR NewSession(LPCWSTR pszRootFolder, OUT IAFSession** ppSession, IN PD2D_ENCRYPTION_INFO pEncryptionInfo,int ExtType = DISK_D2D);
	//open a existing session. it's for restore job.
	CREATE_SESS_ERR OpenSession(LPCWSTR pszRootFolder, DWORD dwSessionNumber, OUT IAFSession** ppSession, IN PD2D_ENCRYPTION_INFO pEncryptionInfo);
	CREATE_SESS_ERR OpenSession2(LPCWSTR pszRootFolder, DWORD dwSessionNumber, OUT IAFSession** ppSession, IN PD2D_ENCRYPTION_INFO pEncryptionInfo, IEventMessage* pEventMsg);

	CREATE_SESS_ERR OpenSessionEx(LPCWSTR pszRootFolder, DWORD dwSessionNumber, OUT IAFSession** ppSession, LPWSTR pwzSessFolder, DWORD* pdwSessFolderLen, IN PD2D_ENCRYPTION_INFO pEncryptionInfo);
	CREATE_SESS_ERR OpenSessionEx2(LPCWSTR pszRootFolder, DWORD dwSessionNumber, OUT IAFSession** ppSession, LPWSTR pwzSessFolder, DWORD* pdwSessFolderLen, IN PD2D_ENCRYPTION_INFO pEncryptionInfo, IEventMessage* pEventMsg);

	DWORD FindSessNumOfParentDisk(LPCWSTR pszRootFolder, DWORD dwDiskSig);
	DWORD GetVDiskName(LPCWSTR pszRootFolder, DWORD dwSessionNumber, DWORD dwDiskID, WCHAR* pVDiskName, int nSizeinWCHAR);
	DWORD CreateReclaimObject(LPCWSTR pszSessionFolder, LPCWSTR pszTmpFolder, IAFReclaim** ppReclaim);
	int CreateVHDDiskEx(const wchar_t* pwszFilePath, unsigned long ulDesiredAccess,
		unsigned long ulShareMode, D2D_ENCRYPTION_INFO* pEncryptionInfo,IVHDFile** ppVHDFile);

	DWORD OpenVHD(LPCWSTR pszFileName, IN D2D_ENCRYPTION_INFO * pEncryptionInfo, IAFVHD ** ppVhd, LPCWSTR lckFilePath);

	///ZZ: Check if a session is full session, at least all disk are full disk.
	DWORD AFStorIsFullSess(bool& bIsFullSess, const WCHAR* pwzSessFolder);

	void SetPreAllocSize(DWORD dwPreAllocSize);
    int CreateD2DFileWriter(const wchar_t* pwszFilePath, D2D_CREATE_PARMS* pstParms, 
                        D2D_ENCRYPTION_INFO* pCryptInfo, IVHDFileW** ppVHDFileW);
    int CreateD2DFileWriter_ds(const wchar_t* pwszFilePath, D2D_CREATE_PARMS* pstParms, 
        D2D_ENCRYPTION_INFO* pCryptInfo, const D2D_USER_CIPHER* pUserCipher, IVHDFileW** ppVHDFileW);
    int CreateIAFVHDMta(IAFVHDMta** ppMta);

    DWORD GetAFStorConfiguration(BOOL bIsManagedByRPS, PWCHAR pwszDestPath, PST_AFSTOR_CONFIGURATION pAFStorConfiguration);
	DWORD AFStorGetSessionDataSize(LPCWSTR pszRootFolder, DWORD dwSessionNumber, OUT ULONGLONG &ullLogicSize, OUT ULONGLONG &ullActualSize);
	// danri02: Dedupe Merge optimal, @2013-06, code begin
	CREATE_SESS_ERR CreateDedupSessProcessor(IAFStorDedupSessProcessor** ppDedupProcessor);
	// danri02: Dedupe Merge optimal, @2013-06, code end

	//Begin: danri02 fix issue:29675
	CREATE_SESS_ERR	CreateBufInterface(IAFStorBuf** ppAfstorBufInterface,ULONG ulBufSize);
	//END: danri02 fix issue:29675
	//replace old vhd by MPII, by danri02, 2013-05
	DWORD GetDiskDescriptor(PWCHAR pszDest, DWORD dwSessNo,PSESS_FORMAT_DESCRIPTOR pFormatDesc);
	//replace old vhd by MPII, by danri02, 2013-05, end

	//the following 2 APIs are used to statistic data store size
	int AddNodeSessionSize(const wchar_t* pszRootPath,DWORD dwSesNo, LONGLONG llRawSize,LONGLONG llCompSize);
	
	//return: 0,SUCCESS, other: error
	int GetDataStoreSize(PST_DATASTORE_PATH pDataStorePath,PST_DATASTORE_SIZE pDataStoreSize);

	DWORD RemoveNodeSessionSize(const wchar_t* pwcszDSPath, const wchar_t* pwcszNodeName);

#ifdef __cplusplus
};
#endif

typedef CREATE_SESS_ERR (*PFNCREATEDEVOBJECT)(LPCWSTR pszRootFolder, IAFStorDev** ppDevObj);
typedef VOID (*PF_SETPREALLOCATIONSIZE)(DWORD dwPreAllocationSize); //<huvfe01>2012-11-22 for pre-allocate space
typedef DWORD (*PFNGETAFSTORCONFIGURATION)(BOOL bIsManagedByRPS, PWCHAR pwszDestPath, PST_AFSTOR_CONFIGURATION pAFStorConfiguration);
typedef DWORD (*PAFSTORGETSESSIONDATASIZE)(LPCWSTR, DWORD, OUT ULONGLONG&, OUT ULONGLONG&);
// danri02: Dedupe Merge optimal, @2013-06, code begin
typedef CREATE_SESS_ERR	(*PF_CREATE_DEDUP_SESS_PROCESSOR)(IAFStorDedupSessProcessor** ppDedupProcessor);
// danri02: Dedupe Merge optimal, @2013-06, code end

//Begin: danri02 fix issue:29675
typedef CREATE_SESS_ERR (*PF_CREATE_AFSTOR_BUF_INTERFACE)(IAFStorBuf** ppAfstorBufInterface,ULONG ulBufSize);
//END: danri02 fix issue:29675

//the following 2 APIs are used to statistic the data store size
typedef int (*PFN_ADDNODESESSIONSIZE)(const wchar_t* pszRootPath,DWORD dwSesNo, LONGLONG llRawSize,LONGLONG llCompSize);
typedef int (*PFN_GetDataStoreSize)(PST_DATASTORE_PATH pDataStorePath,PST_DATASTORE_SIZE pDataStoreSize);

