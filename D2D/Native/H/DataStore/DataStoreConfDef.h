//////////////////////////////////////////////////////////////////////////
//Name: DataStoreConfDef.h
//
#ifndef DATA_STORE_CONF_DEF_H
#define DATA_STORE_CONF_DEF_H

#if defined(WIN32)

#include <Windows.h>
#elif defined(LINUX)
#include "DefWrapper.h"

#endif

#pragma pack(1)

const UINT HASH_BUFFER_SIZE	= 32;

typedef struct st_dataStore_GDD_CONF_PrimaryRole
{
	DWORD		dwport;
	wchar_t		*pszHostNameOrIP;
	DWORD		dwPathLen;  //In bytes.
	wchar_t*	pszPath;
	BYTE*		pUser;
	DWORD		dwUserLen;
	BYTE*		pPWD;
	DWORD       dwPWDLen;
}DATASTORE_GDD_CONF_PRIMARYROLE,*PDATASTORE_GDD_CONF_PRIMARYROLE;


typedef struct st_dataStore_GDD_CONF_HashRole
{
	DWORD		dwport;
	wchar_t		*pszHostNameOrIP;
	DWORD64		dwSSDSizeInGB;
	DWORD64		dwMaxMemInMB;
	DWORD		dwHashRoleMode;
	DWORD		dwPathLen; //In bytes.
	wchar_t*	pszPath;
	BYTE*		pUser;
	DWORD		dwUserLen;
	BYTE*		pPWD;
	DWORD       dwPWDLen;
}DATASTORE_GDD_CONF_HASHROLE,*PDATASTORE_GDD_CONF_HASHROLE;


typedef struct st_dataStore_GDD_CONF_DataRole
{
	DWORD		dwport;
	wchar_t		*pszHostNameOrIP;
	DWORD		dwPathLen;  //In bytes.
	wchar_t*	pszPath;
	BYTE*		pUser;
	DWORD		dwUserLen;
	BYTE*		pPWD;
	DWORD       dwPWDLen;
}DATASTORE_GDD_CONF_DATAROLE,*PDATASTORE_GDD_CONF_DATAROLE;


typedef struct st_dataStore_GDD_CONF_debug
{
	DWORD		dwCommLibType;
	DWORD		dwDataSliceMaxSizeInMB;
	DWORD		dwHashKeyCalcType;
	DWORD		dwLogFileSizeInMB;
	DWORD		dwNetworkTimeoutInSec;
	DWORD       dwUseMixedReclaim;
}DATASTORE_GDD_CONF_DEBUG,*PDATASTORE_GDD_CONF_DEBUG;


typedef struct st_dataStore_GDD_CONF
{
	DWORD dwDedupeBlockSize;
	DWORD dwLogLevel;
	DATASTORE_GDD_CONF_DEBUG debugConf;
	DWORD dwPriNum;
	PDATASTORE_GDD_CONF_PRIMARYROLE  pGDDPrimaryserver;
	DWORD dwHashNum;
	PDATASTORE_GDD_CONF_HASHROLE pGDDHashserver;
	DWORD dwDataNUM;
	PDATASTORE_GDD_CONF_DATAROLE pGDDDataServer;
}DATASTORE_GDD_CONF, *PDATASTORE_GDD_CONF;


typedef struct st_dataStore_NGDD_CONF
{
	DWORD dwSliceMergeThresholdInPercentage;		// default: 50
	DWORD dwSliceSizeInMB;							// default: 512
	DWORD dwEnableSparse;							// default: 1
	DWORD dwDisableReservingSpaceForMerge;			// default: 0
	DWORD dwDisablePreRead;							// default: 0
}DATASTORE_NGDD_CONF, *PDATASTORE_NGDD_CONF;


typedef struct st_commStoreConf
{
	wchar_t		*pszCommStorePath;
	BYTE		*pUser;
	DWORD		dwUserLen;
	BYTE		*pPWD;
	DWORD       dwPWDLen;	
}COMM_STORECONF, *PCOMM_STORECONF;


//CommonPathConf is used to save the flat text for user name and password, 
//but COMM_STORECONF is save encrypted user name and password.
typedef struct st_CommonPathConf
{
	wchar_t		szPath[260];
	wchar_t		szLoginUserName[128];
	wchar_t		szLoginPassword[128];
}CommonPathConf, *pCommonPathConf;


typedef struct st_storeConfHeader
{
	DWORD IsLittleEnd; //1 for Little End, 0 for big end.
	DWORD dwVersion;
	DWORD dwUsageTag;
	DWORD dwStoreConfStreamSize;
	GUID  dsGUID;
	CHAR  szCfgHash[HASH_BUFFER_SIZE];
	SYSTEMTIME	stHashTime;
}STORECONF_HEADER, *PSTORECONF_HEADER;


typedef struct st_storeConf
{
	DWORD		dwDataStoreStartHistory;
	GUID		dsGUIDTag;
	GUID		dsWatermark;
	DWORD		dwEnableGDD;					// 0 for not enable, 1 for enable strictly
	DWORD		dwEnableEncrypt;				// 0 for not enable, 1 for enable strictly
	DWORD		dwEnableCompress;				// 0 for not enable, 1 for enable strictly
	DWORD		dwEncryptMethod;				// it is meaningful if enable Encrypt
	DWORD       dwCompressMethod;				// is is meaningful if enable compress.
	DWORD		dwMaxNodeCount;					// Max node amount for this data store
	DWORD		dwDSVersion;					// data store version
	DWORD		dwEncryPwdLen;
	DWORD		dwDSDisplayNameLen;
	BYTE*		pEncryptPWD;
	WCHAR*		pDSDisplayName;
	PCOMM_STORECONF pcommon_store;
	PDATASTORE_GDD_CONF pgdd_store;
	PDATASTORE_NGDD_CONF pngdd_store;
}STORE_CONF, *PSTORE_CONF;


typedef struct st_storeOwnerInfo
{
	WCHAR		pDSOwnerRPSName[256];
	WCHAR		pDSOwnerRPSSID[256];
}STORECONF_OWNER, *PSTORECONF_OWNER;

typedef struct st_tagDSThreshold
{
	WCHAR		szCommonWarn[8];
	WCHAR		szCommonError[8];
	WCHAR		szIndexWarn[8];
	WCHAR		szIndexError[8];
	WCHAR		szHashPathWarn[8];
	WCHAR		szHashPathError[8];
	WCHAR		szHashMemWarn[8];
	WCHAR		szHashMemError[8];
	WCHAR		szDataWarn[8];
	WCHAR		szDataError[8];
}DSTHRESHOLD, *PDSTHRESHOLD;

//data store configuration that user cannot identify 
typedef struct st_storeInternalConf
{
	WCHAR		szInstGUID[128];
	GUID		waterMark;
	WCHAR		szSharedName[256];
	WCHAR		szDSPassword[256];
	WCHAR		szDSPwdHash[256];
	DSTHRESHOLD dsThreshold;
}STORE_INTERNAL_CONF, *PSTORE_INTERNAL_CONF;


typedef struct DataStoreStoreValidator
{
	GUID		confAndverifyGUID;
	SYSTEMTIME	markTimeStamp;
}DATASTOREVALIDATORINFO,*PDATASTOREVALIDATORINFO;



typedef struct st_dsVersion
{
	WORD		wMajor;
	WORD		wMinor;
	WORD		wBuild;
}DSVERSION, *PDSVERSION;


//DSExtendInfo will be put to the position of after 64 WCHAR of pDSOwnerRPSSID.
typedef struct st_dsExtendInfo
{
	DSVERSION	createVersion;		//
	SYSTEMTIME	createTime;			//for new ds opt + repair cfg file opt
	DSVERSION	importVersion;		//the RPS version when import the ds
	SYSTEMTIME	importTime;			//for import ds opt
	SYSTEMTIME	RemoveTime;			//for remove ds opt
	BYTE		bExtend[512];
}DSExtendInfo, *PDSExtendInfo;



#pragma pack()

#endif
