#ifndef	__CA_RPS_GDD_GDDManagersdef_H__
#define	__CA_RPS_GDD_GDDManagersdef_H__ 

#define  DATASTORE_INSNAME_LEN							257
#define  GDD_INSTANCE_MAXNUM							16
#define	 DATASTOER_NODENAME_LEN							256
#define	 DATASTORE_STURCT_COM_LEN						256

#define  DATA_STORE_ENABLE_GDD_STATUS_IS_AVAILABLE		0X00000001 
#define  DATA_STORE_OVERALL_STATUS_IS_AVAILABLE			0X00000002 
#define  DATA_STORE_COMM_STATUS_IS_AVAILABLE			0X00000004 
#define  DATA_STORE_GDD_STATUS_IS_AVAILABLE				0X00000008 

#pragma pack(4)
 
enum GDDHashRoleMode
{
	HASH_ROLE_MODE_MEM = 1,
	HASH_ROLE_MODE_MEM_PLUS_SSD = 2
};


typedef struct st_gdd_primary_role_attr 
{
	DWORD		dwPort;				   // the listen socket port or primary role
	DWORD		dwlengthIndexFilePath; // the length of pszIndexFilePaths
	WCHAR*		pszIndexFilePaths; // the location where the index file exists. there may be more than one path.Format "path 1 \0 path 2	\0 path 3 \0\0"
	wchar_t*	pszUser;         // for remote share folder, this user is used to access the remote folder;
	wchar_t*	pszPWD;
}GDD_PRIMARY_ROLE_ATTr, *PGDD_PRIMARY_ROLE_ATTr;


typedef struct st_gdd_hash_role_attr 
{
	DWORD		dwPort;							// the listen socket port of hash role
	DWORD		dwHashRoleMode;				// ref to enum GDDHashRoleMode
	DWORD64		dw64AvailMemInMB;
	DWORD64		dw64ExpectedUniqueDataInGB;
	DWORD		dwlengthHashFilePath;				//the length of lengthHashFilePath
	WCHAR*		pszHashFilePaths;				// the location where the hash file exists.there may be more than one path.Format "path 1 \0 path 2	\0 path 3 \0\0"
	wchar_t*	pszUser;         // for remote share folder, this user is used to access the remote folder;
	wchar_t*	pszPWD;
}GDD_HASH_ROLE_ATTr, *PGDD_HASH_ROLE_ATTr;


typedef struct st_gdd_data_role_attr 
{
	DWORD		dwPort;				// the listen socket port of data role
	DWORD		dwlengthDataFilePath; // the length of pszDataFilePath
	WCHAR*		pszDataFilePath;	// the location where the data file exists. there may be more than one path.Format "path 1 \0 path 2	\0 path 3 \0\0"
	wchar_t*	pszUser;         // for remote share folder, this user is used to access the remote folder;
	wchar_t*	pszPWD;
}GDD_DATA_ROLE_ATTr, *PGDD_DATA_ROLE_ATTr;
 

typedef struct _st_COMM_INSTANCE_CONFIG
{
	DWORD		dwFlags;
	wchar_t*	pszStorePath;	// the system folder used to store backed up data if GDD is disable. there may be more than one path.Format "path 1 \0 path 2	\0 path 3 \0\0"
	wchar_t*	pszUser;
	wchar_t*	pszPWD;
	wchar_t*    pszStoreSharedName;
}COMM_INSTANCE_CONFIG,*PCOMM_INSTANCE_CONFIG;


typedef struct _st_NGDD_INSTANCE_CONFIG
{
	DWORD dwSliceMergeThresholdInPercentage;		// default: g_non_dedup_instCfg_SliceMergeThresholdInPercentage
	DWORD dwSliceSizeInMB;							// default: g_non_dedup_instCfg_SliceSizeInMB
	DWORD dwEnableSparse;							// default: g_non_dedup_instCfg_EnableSparse
	DWORD dwDisableReservingSpaceForMerge;			// default: g_non_dedup_instCfg_DisableReservingSpaceForMerge
	DWORD dwDisablePreRead;							// default: g_non_dedup_instCfg_DisablePreRead
}NGDD_INSTANCE_CONFIG, *PNGDD_INSTANCE_CONFIG;


typedef struct  _st_GDD_INSTANCE_CONFIG
{											// structure size
	DWORD dwFlags;								// the flags reserved to identify which attribute to get/set
	DWORD dwDedupeBlockSizeInKB;				// de-dupe block size

	DWORD  dwPrimayRoleNumber;
	PGDD_PRIMARY_ROLE_ATTr  pPrimaryAttrArray;		// the attribute of primary role   

	DWORD dwHashRoleNumber;						// the array size of pHashATTrArray
	PGDD_HASH_ROLE_ATTr	  pHashAttrArray;	// the attribute of hash role,there may be more than one
	// hash roles in the GDD system, so there use array to hold all the information.

	DWORD dwDataRoleNumber;						// the array size of pDataATTrArray
	PGDD_DATA_ROLE_ATTr	  pDataAttrArray;	// the attribute of data role,there may be more than one
	// hash roles in the GDD system, so there use array to hold all the information.

}GDD_INSTANCE_CONFIG,*PGDD_INSTANCE_CONFIG;


typedef struct _st_DATA_STORE_INSTANCE_CONFIG
{ 	
	DWORD dwFlags;									// the flags reserved to identify which attribute to get/set
	DWORD dwEnableGDD;								// 1 for enable. 0 for not enable.
	DWORD dwEnableEncry;							// 1 for enable. 0 for not enable.
	DWORD dwEncryMethod;							// has meaning if encryption is enable.
	wchar_t* pszEncryPwd;
	DWORD dwEnableCompress;							// 1 for enable. 0 for not enable.
	DWORD dwCompressMethod;							// has meaning if compress is enable.
	DWORD dwMaxNodeCount;							// Maximum node amount for this data store
	DWORD dwDSVersion;
	PCOMM_INSTANCE_CONFIG pCommInstanceCfg;			// Common folder configure info				
	PGDD_INSTANCE_CONFIG pGDDInstanceCfg;			// GDD instance configure info. if GDD is not enable, it is NULL.
	PNGDD_INSTANCE_CONFIG pNGDDInstanceCfg;			// Non-GDD, instance configure info. if GDD is enable, it is NULL.
}DATASTORE_INSTANCE_INFO, *PDATASTORE_INSTANCE_INFO;


//The structure is used for save data store static information.
typedef struct _st_DATA_STORE_INSTANCE_CONFIG_STATIC
{
	wchar_t	 szOwnerName[DATASTORE_INSNAME_LEN];
	wchar_t  szOwnerSID[DATASTORE_INSNAME_LEN];

	UINT64  dwUsedMemSize;

	GUID	dsSTOREGUIDTag;
	GUID	dsMatchTag;
}DATASTORE_INSTANCE_INFO_STATIC, *PDATASTORE_INSTANCE_INFO_STATIC;


typedef struct _st_DATA_STORE_PATH_THRESHOLD
{
	double	fCommonWarnThreshold;
	double	fCommonErrorThreshold;
	double	fIndexWarnThreshold;
	double	fIndexErrorThreshold;
	double	fHashPathWarnThreshold;
	double	fHashPathErrorThreshold;
	double	fHashMemWarnThreshold;
	double	fHashMemErrorThreshold;
	double	fDataWarnThreshold;
	double	fDataErrorThreshold;
}DATA_STORE_PATH_THRESHOLD, *PDATA_STORE_PATH_THRESHOLD;


#define  DATASTORE_VERSION_1 0x1


typedef struct _st_DATA_STORE_INSTANCE_ITEM
{
	wchar_t InsName[DATASTORE_INSNAME_LEN];				// save guid
	wchar_t DispName[DATASTORE_INSNAME_LEN];			// display name

}DATASTORE_INSTANCE_ITEM,*PDATASTORE_INSTANCE_ITEM;


enum GDDRoleJObState
{
	ROLE_JOB_RUNNING		=1,
	ROLE_NO_JOB				=2,
};


//default non-dedup instance config. The value should be saved into instance registry when AddInstance().
//And the value should not be changed except user change it from registry.
const DWORD g_non_dedup_instCfg_SliceMergeThresholdInPercentage = 50;
const DWORD g_non_dedup_instCfg_SliceSizeInMB = 512;
const DWORD g_non_dedup_instCfg_EnableSparse = 1;
const DWORD g_non_dedup_instCfg_DisableReservingSpaceForMerge = 0;
const DWORD g_non_dedup_instCfg_DisablePreRead = 0;


//The min free disk space for data store when create/modify, 5MB
const ULONGLONG g_nInitMinFreeDiskSpaceForDS = 5*1024*1024;


typedef struct tag_GDD_PRIMARY_ROLE_STATUS
{
	DWORD		dwRoleCurrentState;		// ref to enum GDDRoleState
	DWORD		dwJobStatus;			// ref to enum GDDRoleJObState
	DWORD64		dw64IndexVolumeTotalSize;	// the total size of Volume where the index file exists
	DWORD64		dw64IndexVolumeFreeSize;	// the free size of Volume where the index file exists
	DWORD64		dw64IndexVolumeUsedSize;	// the used size of Volume where the index file exists
	DWORD64		dw64IndexDirSize;			// the size of the folder  where the index file exists
	DWORD		dwPort;				    // Primary server listen port;
}GDD_INST_PRIMARY_ROLE_STATUS , *PGDD_INST_PRIMARY_ROLE_STATUS ;


typedef struct tag_GDD_HASH_ROLE_STATUS
{
	DWORD    dwRoleCurrentState;	// ref to enum GDDRoleState
	DWORD64  dw64HashVolumeTotalSize; // the total size of Volume where the hash file exists
	DWORD64  dw64HashVolumeFreeSize;  // the free size of Volume where the hash file exists	
	DWORD64  dw64HashVolumeUsedSize;  // the used size of Volume where the hash file exists	
	DWORD64  dw64HashDirSize;		  // the size of the folder  where the hash file exists	
	DWORD64  dw64HashDirMinRequireSize;		// the min require hash folder size when modifying DS
	DWORD64  dw64MemTotalSize;		  // total size of memory in current machine
	DWORD64  dw64MemAvailSize;		  // free size of memory in current machine
	DWORD64  dw64MemUsedByHashRole;   // the memory size used by hash role
	DWORD64  dw64MemMinRequireSize;		// the min required memory size when modifying DS
	DWORD    dwPort;				  // Hash server listen port;
}GDD_INST_HASH_ROLE_STATUS , *PGDD_INST_HASH_ROLE_STATUS ;


typedef struct tag_GDD_DATA_ROLE_STATUS 
{
	DWORD	 dwRoleCurrentState;		// ref to enum GDDRoleState
	DWORD64  dw64DataVolumeTotalSize;	// the total size of Volume where the data file exists
	DWORD64  dw64DataVolumeFreeSize;	// the free size of Volume where the data file exists	
	DWORD64  dw64DataVolumeUsedSize;	// the Used size of Volume where the data file exists	
	DWORD64  dw64DataDirSize;			// the size of the folder  where the data file exists
	DWORD    dwPort;				    // Storage(Data) server listen port;
}GDD_INST_DATA_ROLE_STATUS , *PGDD_INST_DATA_ROLE_STATUS ;


typedef struct tag_DATA_STORE_GDD_INSTANCE_STATUS
{
	DWORD dwFlags;					// the flags reserved to identify which attribute to get
	DWORD dwPrimaryRoleNumber;		// the size of Primary array pHashRoleStatusArray
	PGDD_INST_PRIMARY_ROLE_STATUS   pPrimaryRoleStatus; //The Status of Primary server of GDD instance
	DWORD dwHashRoleNumber;		// the size of Hash array pHashRoleStatusArray
	PGDD_INST_HASH_ROLE_STATUS      pHashRoleStatusArray; //The Status of Hash server of GDD instance
	DWORD dwDataRoleNumber;			// the size of Data array pDataRoleStatusArray
	PGDD_INST_DATA_ROLE_STATUS		pDataRoleStatusArray; //The Status of Storage server of GDD instance
}DATA_STORE_GDD_INST_STATUS,*PDATA_STORE_GDD_INST_STATUS;
 

typedef struct tag_DATA_STORE_COMMPATH_STATUS
{
	DWORD		dwFlags;					// the flags reserved to identify which attribute to get
	DWORD64		dw64CommPathVolumeTotalSize;	// the total size of Volume where the common path exists
	DWORD64		dw64CommPathVolumeFreeSize;	// the free size of Volume where the common path exists	
	DWORD64		dw64CommPathVolumeUsedSize;	// the used size of Volume where the common path exists	
	DWORD64		dw64CommPathDirSize;			// the size of the folder  where the common path file exists
}DATA_STORE_COMMPATH_STATUS,*PDATA_STORE_COMMPATH_STATUS;



typedef struct st_DATA_STORE_INIT_PROGRESS_TIME
{
	DWORD       dwElapsedTimeInSec;
	DWORD       dwEstRemainTimeInSec;
}DATA_STORE_INIT_PROGRESS_TIME, *PDATA_STORE_INIT_PROGRESS_TIME;


typedef struct st_DATA_STORE_INSTANCE_STATUS
{
	DWORD dwFlags;					// the flags reserved to identify which attribute to get
	DWORD dwEnableGDD;				// the GDD feature is enable if it is 1, 0 for not enable.
	DWORD dwOverallStatus;			// the running state
	DWORD dwStatusErrorCode;		// When the status is abnormal, need error code to web service to show details
	DWORD dwTimeStamp;				// the time stamp of the instance status
	DATA_STORE_INIT_PROGRESS_TIME		stInitProgTime;

	DWORD64	dwTotalSrcDataSize;		// total data size of source data, in byte
	DWORD64	dwUniqueDataSize;		// deduped data size for the source data, in byte
	DWORD64	dwCompressDataSize;		// compressed data size for the source data, in byte

	PDATA_STORE_COMMPATH_STATUS			pCommStoreStatus;
	PDATA_STORE_GDD_INST_STATUS			pGDDStoreStatus; //the GDD feature is not enable, it is null.
}DATASTORE_INSTANCE_STATUS, *PDATASTORE_INSTANCE_STATUS;


//For Machine Level, all Instance Info 
typedef struct st_DATA_STORE_INSTANCE_LIST
{
	DWORD InsCount;						// the count of instances, including GDD store and non-gdd store
	PDATASTORE_INSTANCE_ITEM pInstance;	//the instance list
}DATASTORE_INSTANCE_LIST, *PDATASTORE_INSTANCE_LIST;


typedef struct st_DATASTORE_NODE_ITEM
{
	WCHAR	szNodeName[DATASTOER_NODENAME_LEN];
	WCHAR	szNodeID[DATASTOER_NODENAME_LEN];
	WCHAR	szVMInstanceID[DATASTORE_STURCT_COM_LEN];
	WCHAR	szMSPUser[DATASTORE_STURCT_COM_LEN];
	WCHAR	szSrcRPSName[DATASTORE_STURCT_COM_LEN];
	WCHAR	szDstPlanName[DATASTORE_STURCT_COM_LEN];
	DWORD	dwIsVM;
	DWORD	dwIntegrity;
	DWORD64 dwLastBackupTime;
	DWORD64 dwTransferDataSize;
	DWORD64 dwDataSize;
	DWORD64 dwCommonPathSize;
	DWORD64 dwCatalogSize;
	DWORD64 dwGrtCatalogSize;
}DATASTORE_NODE_ITEM, *pDATASTORE_NODE_ITEM;


typedef struct st_DATASTORE_NODE_LIST
{
	DWORD dwNodeCount;				//the count of node in the data store. 
	pDATASTORE_NODE_ITEM pNodeItem;		//the node list
}DATASTORE_NODE_LIST, *pDATASTORE_NODE_LIST;


typedef struct st_DATA_STORE_HASH_DETECT_INFO
{
	DWORD dwLen;
	const wchar_t* pszHash_MachineConf;
}DATA_STORE_HASH_DETECT_INFO,*PDATA_STORE_HASH_DETECT_INFO;

#pragma pack()

#endif//__CA_RPS_GDD_GDDManagersdef_H__
