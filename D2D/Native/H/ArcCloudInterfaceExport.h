#pragma once

#include "ARCCloudDefs.h"
#ifdef DLL_API_EXPORT
	#define ARC_CLOUD_API __declspec(dllexport)
#else
	#define ARC_CLOUD_API __declspec(dllimport)
#endif 



#ifdef  __cplusplus
extern "C"
{
#endif
	ARC_CLOUD_API	LONG InitCCI(long * remoteHandle, LPWSTR installationPath);
	ARC_CLOUD_API	LONG InitCCIEx(long *remoteHandle, LPWSTR installationPath,
					  struct CCIGlobalParamExternal* cciGlobalParamExternal);
	ARC_CLOUD_API	LONG DeInitCCI(long remoteHandle);

	ARC_CLOUD_API	LONG AddCloudProvider ( long remoteHandle, struct CALLER_CONTEXT_INFO callerCtxInfo, 
						struct CLOUD_VENDOR_INFO * cldVendorInfo, long validateUser, LPWSTR profileName, 
							LPWSTR providerUserName );

	ARC_CLOUD_API	LONG UpdateCloudProvider ( long remoteHandle, LPWSTR providerUserName, 
								struct CLOUD_VENDOR_INFO * cldVendorInfo, long validateUser );

	ARC_CLOUD_API	LONG RemoveCloudProvider ( long remoteHandle, LPWSTR providerUserName );

	ARC_CLOUD_API	LONG BeginCloudSession ( long remoteHandle, LPWSTR providerUserName, GUID * sessionToken );
	ARC_CLOUD_API	LONG EndCloudSession ( long remoteHandle, GUID sessionToken );

	ARC_CLOUD_API	LONG CloudCreateDirectory ( long remoteHandle, GUID sessionToken, LPWSTR dirName );

	ARC_CLOUD_API	LONG CloudSetCurrentDirectory ( long remoteHandle, GUID sessionToken, LPWSTR dirName );

	ARC_CLOUD_API	LONG CloudRemoveDirectory ( long remoteHandle, GUID sessionToken, LPWSTR dirName, long recursive );

	ARC_CLOUD_API	LONG CloudCreateFile ( long remoteHandle, GUID sessionToken, LPWSTR fileName, 
								struct CLOUD_FILE cldFile_Info, long * fileHandle );

	ARC_CLOUD_API	LONG CloudOpenFile ( long remoteHandle, GUID sessionToken, LPWSTR fileName, 
								struct CLOUD_FILE* cldFile_Info, long * fileHandle );

	ARC_CLOUD_API	LONG CloudWriteFile ( long remoteHandle, GUID sessionToken, long fileHandle, 
								BYTE *data_buffer, unsigned __int64 bytesToWrite, LPWSTR* objIDList, 
								unsigned __int64 * bytesWritten );

	ARC_CLOUD_API	LONG CloudReadFile ( long remoteHandle, GUID sessionToken, long fileHandle, BYTE *data_buffer, 
								unsigned __int64 bytesToRead, unsigned __int64 * bytesRead ) ;

	ARC_CLOUD_API	LONG CloudWriteFileEx ( long remoteHandle, GUID sessionToken, long fileHandle, BYTE *data_buffer, 
					 unsigned __int64 bytesToWrite, LPWSTR* objIDListParam, unsigned __int64 * bytesWritten, CWriteFileData* writeFileData );


	ARC_CLOUD_API   LONG CloudReadFileEx ( long remoteHandle, GUID sessionToken, long fileHandle, BYTE *data_buffer, 
					unsigned __int64 bytesToRead, unsigned __int64 * bytesRead, CReadFileData* readFileData );


	ARC_CLOUD_API	LONG CloudCloseFile ( long remoteHandle, GUID sessionToken, long fileHandle );

	ARC_CLOUD_API	LONG CloudSetFilePointer ( long remoteHandle, GUID sessionToken, long fileHandle, 
								struct CLOUD_FILE * cldFile_Info );

	ARC_CLOUD_API	LONG CloudFileInfo ( long remoteHandle, GUID sessionToken, LPWSTR fileName, 
							struct CLOUD_FILE * cldFile_Info );

	ARC_CLOUD_API	LONG CloudFlushFile ( long remoteHandle, GUID sessionToken, long fileHandle );

	ARC_CLOUD_API	LONG CloudDeleteFile ( long remoteHandle, GUID sessionToken, LPWSTR fileName );


	ARC_CLOUD_API	LONG CloudListItems ( long remoteHandle, GUID sessionToken, LPWSTR searchPattern, 
								LPWSTR dirToSearch, long * searchHandle, LPWSTR * listOfItems, long * itemCount );

	ARC_CLOUD_API	LONG CloudEnumProviderFirst ( long remoteHandle, struct CALLER_CONTEXT_INFO callerCtxInfo, 
								struct CLOUD_VENDOR_INFO * cldVendorInfo, LPWSTR * providerName, GUID * searchHandle );

	ARC_CLOUD_API	LONG CloudEnumProviderNext ( long remoteHandle, struct CALLER_CONTEXT_INFO callerCtxInfo, 
							GUID searchHandle, struct CLOUD_VENDOR_INFO * cldVendorInfo, LPWSTR * providerName );

	ARC_CLOUD_API	LONG CloudEnumProviderClose ( long remoteHandle, struct CALLER_CONTEXT_INFO callerCtxInfo,
												 GUID searchHandle );
	ARC_CLOUD_API	LONG CloudFindCloseFile ( long remoteHandle, GUID sessionToken, GUID handle );
	ARC_CLOUD_API	LONG CloudFindNextFile ( long remoteHandle, GUID sessionToken, GUID handle, struct ARCCloudDirectoryMetaEntry * fileInfo );
	ARC_CLOUD_API	LONG CloudFindFirstFile ( long remoteHandle, GUID sessionToken, LPWSTR szFileName, GUID * fileSearchHandle, struct ARCCloudDirectoryMetaEntry * fileInfo );
	ARC_CLOUD_API   LONG CloudDirectoryStatistics ( long remoteHandle, GUID sessionToken, wchar_t *dirName, long bRecursive, struct ARCCLD_DIR_STATISTICS * dirStats );
	ARC_CLOUD_API   LONG CloudGetCurrentDirectory (long remoteHandle,GUID sessionToken, LPWSTR * dirName );
	ARC_CLOUD_API	VOID CloudGetErrorDetails (struct ARCCloudErrorDetails* cldFile_Info);
	ARC_CLOUD_API   LONG CloudGetDefaultChunkSizeByVendorType (long remoteHandle,enum CLOUD_TYPE cldvendorType,__int64 * defaultChunkSize );
	ARC_CLOUD_API   LONG CloudGetDefaultChunkSizeByProviderToken (long remoteHandle, LPWSTR providerUserName, __int64 * defaultChunkSize );
	ARC_CLOUD_API   LONG CloudGetProviderStat (long remoteHandle,LPWSTR providerUserName,enum CLOUD_STATISTICS_STATUS cldStatsStatus,struct CLOUD_PROVIDER_STATS * cldProviderStats );
	ARC_CLOUD_API   LONG CloudGetUniqueAccountName (long remoteHandle,LPWSTR providerUserName, LPWSTR * accountName);
	ARC_CLOUD_API   LONG CloudCheckConnectivity ( long remoteHandle, LPWSTR providerUserName );
	ARC_CLOUD_API   LONG CCIReleaseStringMemory(LPWSTR param);
	ARC_CLOUD_API	LONG CCIReleaseStringList(LPWSTR** listOfContainers,long itemCount);
	ARC_CLOUD_API   LONG CloudGetCACloudArchiveSet(long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO* cldVendorInfo, LPWSTR** listOfArchive, long* itemCount );
	ARC_CLOUD_API   LONG CloudListContainers(long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO* cldVendorInfo, LPWSTR** listOfContainers, long* itemCount );
	ARC_CLOUD_API   LONG CloudListRegions ( long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo, LPWSTR** listOfRegions, long * itemCount );
	ARC_CLOUD_API   LONG CloudIsValidContainer ( long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo,BOOL* bVlidate );
	ARC_CLOUD_API   LONG CloudGetRegionForBucket( long remoteHandle, struct CLOUD_VENDOR_INFO * cldVendorInfo, LPWSTR* regionName );
	ARC_CLOUD_API   LONG CloudCheckAccessRights(long remoteHandle, GUID sessionToken, DWORD* accessRights);
	ARC_CLOUD_API   LONG CloudGetSupportedProviderList(long remoteHandle, struct CALLER_CONTEXT_INFO callerCtxInfo, struct CProvider** providerList, long* count); 
	ARC_CLOUD_API   LONG CloudGetProviderInformation(long remoteHandle, struct CALLER_CONTEXT_INFO callerCtxInfo, enum CLOUD_TYPE cloudType, struct CProviderInformation* providerInformation); 
	ARC_CLOUD_API   LONG CloudReleaseSupportedProviderList(struct CProvider* providerList, long count);
	ARC_CLOUD_API   LONG CloudSetCCIParams(long remoteHandle,
									       GUID sessionToken,
									       struct CCCIParams* pCCIParams);
	ARC_CLOUD_API   LONG CloudGetUserInfoFromCACloud ( long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo, struct CLOUD_CACLOUD_USER_INFO * cldCACloudUserInfo);
	ARC_CLOUD_API   LONG CloudGetCACloudStorageKey(long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo, LPWSTR username, LPWSTR password);
	//ARC_CLOUD_API   LONG CloudConsumeLicense(long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo, LPWSTR pwszServerName,LPWSTR pwszD2dDeviceIP,int nD2dDeviceTypeID, int& nD2dLicenseStatus);
	ARC_CLOUD_API   LONG CloudGetContainerName(long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo, LPWSTR lpwszContainerName);
	ARC_CLOUD_API   LONG CloudDeleteContainerFromCACloud(long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo);
	ARC_CLOUD_API   LONG CloudAddJobInfo(long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo, CACLOUDJOBINFO jobInfo);
	ARC_CLOUD_API   LONG CloudSetThrottleSpeed(long remoteHandle, IN GUID sessionToken, ThrottleParam* throttleParam);
	ARC_CLOUD_API   LONG CloudDeleteFileEx( long remoteHandle, GUID sessionToken, LPWSTR fileName, BOOL bSync );

	ARC_CLOUD_API   LONG CloudListMachineName( long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo, LPWSTR** listOfMachineName, long * itemCount );
	ARC_CLOUD_API   LONG CloudCanBackup( long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo, long* subStatus);
	ARC_CLOUD_API   LONG CloudUpdateStorageKey( long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo, LPWSTR* newStorageKey);

	ARC_CLOUD_API   LONG CheckLicenseAndQuota(long remoteHandle,struct CALLER_CONTEXT_INFO callerCtxInfo, struct CLOUD_VENDOR_INFO * cldVendorInfo, wchar_t* szD2DVersion, wchar_t* szServerIP, PARCFLASH_LIC_INFO pLicInfo,BOOL bIsSQLInstalled, BOOL bIsExchInstalled,DWORD *pLicResult);
	ARC_CLOUD_API	LONG CCISetProxyDetails(long remoteHandle, struct CALLER_CONTEXT_INFO callerCtxInfo, PROXY_DETAILS* proxyDetails);
	ARC_CLOUD_API	LONG CCIGetProxyDetails(long remoteHandle, struct CALLER_CONTEXT_INFO callerCtxInfo, PROXY_DETAILS* proxyDetails);
	ARC_CLOUD_API	LONG UpgradeCCISettings();
	ARC_CLOUD_API	LONG CloudIsFileCanBeUploaded(IN LONG* remoteHandle, IN GUID sessionToken, IN LPWSTR filePath, BOOL* bAllowFileToUpload, PReasonTypes pReason);
#ifdef  __cplusplus
}
#endif
