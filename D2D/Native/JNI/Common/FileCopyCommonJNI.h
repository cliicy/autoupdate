#pragma once

#include <jni.h>
#include "CommonJNIConv.h"

namespace FileCopyCommonJNI
{
	jlong AFArchive(JNIEnv *env, jclass jclz, jobject in_archiveJobScript);
	jlong DeleteAllPendingFileCopyJobs(JNIEnv* env, jclass this_class, jstring strDest, jstring strDestDomain, jstring strDestUserName, jstring strDestPassword);
	DWORD processJArchiveSourceFilter(JNIEnv *env, PARCHIVE_FILTER_FILEFOLDER& pFilters, jobject nodeList);
	DWORD processJArchiveSizeFilter(JNIEnv *env, PARCHIVE_ASFILTER_SIZE& pFilters, jobject nodeList);
	DWORD processJArchiveTimeRangeFilter(JNIEnv *env, PARCHIVE_ASFILTER_TIME& pFilters, jobject nodeList);
	void processJArchivePolicy(JNIEnv *env, PAFARCHIVEJOBSCRIPT pafJS, jobject nodeList);
	void processJFileCopyPolicy(JNIEnv *env, PAFARCHIVEJOBSCRIPT pafJS, jobject nodeList);
	void processJArchiveNodeList(JNIEnv *env, PAFARCHIVEJOBSCRIPT pafJS, jobject nodeList);
	void processJRestoreArchiveVolumeList(JNIEnv *env, PAFARCHIVENODE afNode, jobject nodeList);
	void processJArchiveRestoreVolumeItems(JNIEnv *env, PAFARCHIVERESTVOLAPP afarchiveVolumes, jobject itemList, int type);
	void processJArchiveSource(JNIEnv *env, PAFARCHIVENODE afArchiveNode, jobject nodeList);
	void ArchiveJobScript2AFJOBSCRIPT(JNIEnv *env, PAFARCHIVEJOBSCRIPT pafJS, jobject* jJobScript);
	void FreeArchiveJobScript(PAFARCHIVEJOBSCRIPT pafJS);

	jlong JNICALL AFCanArchiveJobBeSubmitted(JNIEnv *env, jclass clz, jobject inout_jArchiveJob);
	jlong JNICALL AFCanArchiveSourceDeleteJobBeSubmitted(JNIEnv *env, jclass clz, jobject inout_jArchiveJob);
	jlong AFArchive(JNIEnv *env, jclass jclz, jobject in_archiveJobScript);
	jlong DeleteAllPendingFileCopyJobs(JNIEnv* env, jclass this_class, jstring strDest, jstring strDestDomain, jstring strDestUserName, jstring strDestPassword);
	// initialise filecopy logging
	void InitFileCopyLogging();

	/* START: ARCHVIE */
	extern "C" HMODULE m_hArchiveCatalogHandle;
	typedef HANDLE __declspec(dllimport) (*pfnOpenFileCatalogFile)(TCHAR *in_szVolumeName);
	typedef DWORD __declspec(dllimport) (*pfnGetArchiveDestinationVolumes)(PTCHAR szHostName, std::vector<PTCHAR> &VolumeList);
	typedef  DWORD __declspec(dllimport)(*FREECATALOGITEMS)(vector <pCatalogChildItem> &versions);
	typedef DWORD __declspec(dllimport)(*FREEVOLUMEITEMS) (std::vector<PTCHAR> &VolumeList);
	typedef DWORD __declspec(dllimport) (*pfnGetChildrenCount)(HANDLE hVolumeHandle, TCHAR *in_wscatPath);
	typedef DWORD  __declspec(dllimport) (*pfnGetChildren)(HANDLE VolumeHandle, PTCHAR szPath, vector <pCatalogChildItem> &out_versions);
	typedef DWORD  __declspec(dllimport) (*pfnGetChildrenEx)(HANDLE VolumeHandle, PTCHAR szPath, vector <pCatalogChildItem> &out_versions, DWORD in_dwIndex, DWORD in_dwCount);
	typedef DWORD __declspec(dllimport) (*pfnGetArchiveJobByScheduleStatus)(TCHAR *in_wsBackupDestPath, DWORD in_iScheduleType, vector<ARCHIVE_ITEM_INFO> out_archiveJobs, BOOL in_bFirstJobOnly);
	typedef DWORD  __declspec(dllimport) (*pfnGetChildrenbySearch)(TCHAR * in_szFileName, TCHAR * szHostName, TCHAR * in_szSearchPath, DWORD in_lSearchOptions, DWORD dwIndex, DWORD dwCount, vector <pCatalogChildItem> &vecCatalogList);
	typedef DWORD __declspec(dllimport) (*pfnGetArchiveDestinationVolumes_2)(wstring& catalogDirBasePath, PTCHAR szHostName, std::vector<PTCHAR> &VolumeList);
	typedef __declspec(dllimport) IFileCopyCatalogMgr* (*PFCreateInstanceFileCopyCatalog)();

	//Cloud related API's
	extern "C" HMODULE m_hArchiveFileStoreHandle;
	typedef DWORD __declspec(dllimport) (*VALIDATEBUCKET)(PARCHIVECLOUDDESTINFO in_pARCHCloudDestInfo, BOOL &out_bValid);
	typedef DWORD __declspec(dllimport) (*CREATEBUCKET)(PARCHIVECLOUDDESTINFO in_pARCHCloudDestInfo);
	typedef DWORD __declspec(dllimport) (*GETBUCKETLIST)(PARCHIVECLOUDDESTINFO in_pARCHCloudDestInfo, LPWSTR** out_listOfContainers, long* out_itemCount);
	typedef DWORD __declspec(dllimport) (*FREEBUCKETLIST)(LPWSTR** out_listOfContainers, long * out_itemCount);
	typedef DWORD __declspec(dllimport) (*GETREGIONLIST)(PARCHIVECLOUDDESTINFO in_pARCHCloudDestInfo, LPWSTR** out_listOfRegions, long * out_itemCount);
	typedef DWORD __declspec(dllimport) (*TESTCLOUDCONNECTION)(PARCHIVECLOUDDESTINFO in_pARCHCloudDestInfo, int nProductType);
	typedef DWORD __declspec(dllimport) (*GETREGIONFORBUCKET)(PARCHIVECLOUDDESTINFO in_pARCHCloudDestInfo, LPWSTR * out_szRegion);

	//Archive Catalog Sync API's
	typedef BOOL __declspec(dllimport) (*GETLASTARCHIVECATALOGUPDATETIME)(wstring &szHostName, FILETIME &stLastUpdateTimeStamp, DWORD &dwStatus);

	//Affilestore APIs
	typedef BOOL __declspec(dllimport) (*INITCLOUDSESSIONEX)(PAFARCHIVEJOBSCRIPT pAFJobScript, DWORD &dwCCIErrCode);
	typedef LONG __declspec(dllimport) (*ISFILECOPYDESTINATIONINITEDFORENCRYPTION) (DWORD& dwCCIRetCode, BOOL& bDestInited, BOOL& bDestEncrypted);
	typedef LONG __declspec(dllimport) (*INITFILECOPYDESTINATIONFORENCRYPTION) (DWORD& dwCCIRetCode, BOOL& bDestEncrypted, wstring& szEncrPassword, DWORD enAlgType, DWORD cryptoApiType);
	typedef LONG __declspec(dllimport) (*VALIDATEFILECOPYDESTENCRPASSWORD) (wstring& szEncrPassword, DWORD& dwCCIRetCode, DWORD enAlgType, DWORD cryptoApiType);
	typedef BOOL __declspec(dllimport) (*CLOSECLOUDSESSION)();
	typedef LONG __declspec(dllimport) (*AFCCICLOUDDISABLEASYNCMODE)(bool bASYNCMode);
	typedef __declspec(dllimport) ICloudAPIs* (*PFCreateCldAPIsPtr)();

	// affilestore and afarchiverdll functions
	typedef BOOL __declspec(dllimport) (*INITAFARCHLOG)(wstring szModuleName, wstring& nodename, DWORD dwJobNumber);
	typedef void __declspec(dllimport) (*SETAFARCHLOGPARAMETERS)(DWORD dwLogLevel, DWORD dwMaxLogLinesPerFile);
	typedef BOOL __declspec(dllimport) (*QUERYAFARCHLOGPARAMETERS)(DWORD& dwLogLevel, DWORD& dwMaxLinesPerFile);
	typedef BOOL __declspec(dllimport) (*GETFILECOPYCATALOGPATH)(PTCHAR szMachineName, DWORD dwProductType, PTCHAR szCatalogPath);

	jlong JNICALL AFArchiveRestore(JNIEnv *env, jclass jclz, jobject bkpJS);
	jlong JNICALL CanArchiveJobBeSubmitted(JNIEnv *env, jclass clz, jobject inout_jArchiveJob);
	jlong AFArchive(JNIEnv *env, jclass jclz, jobject in_archiveJobScript);
	jlong DeleteAllPendingFileCopyJobs(JNIEnv* env, jclass this_class, jstring strDest, jstring strDestDomain, jstring strDestUserName, jstring strDestPassword);
	jlong JNICALL AFArchivePurge(JNIEnv *env, jclass jclz, jobject in_archiveJobScript);
	jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFArchiveRestore
		(JNIEnv *env, jclass jclz, jobject bkpJS);
	jlong JNICALL AFArchiveCatalogSync
		(JNIEnv *env, jclass jclz, jobject in_archiveCatalogSyncJobScript);
	jlong JNICALL validateEncryptionSettings(JNIEnv *env, jclass clz, jobject in_archiveJobScript, jobject out_jErrorcode, jobject out_jCCIErrorCode);
	void InitFileCopyLogging();
	jlong JNICALL browseArchiveCatalogChildrenEx(JNIEnv *env, jclass clz, jlong lVolumeHandle, jstring inCatalogFilePath, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems);
	jlong JNICALL searchArchiveCatalogChildren(JNIEnv *env, jclass clz, jstring inFileName, jstring inHostName, jstring inSearchpath, jobject inArchiveDestConfig,
		jlong in_lSearchOptions, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems);
	jlong JNICALL AFGetArchiveDestinationVolumes
		(JNIEnv *env, jclass clz, jstring hostName, jobject jDestInfo, jobject volumeList);
	jlong JNICALL GetArchivedVolumeHandle(JNIEnv *env, jclass clz, jstring strVolume);
	jlong JNICALL GetArchiveChildrenCount(JNIEnv *env, jclass clz, jlong lVolumeHandle, jstring strCatalogPath, jobject childCount);
	jlong JNICALL browseArchiveCatalogChildren(JNIEnv *env, jclass clz, jlong lVolumeHandle, jstring inCatalogFilePath, jobject archiveCatalogItems);
	jlong JNICALL verifyBucketName(JNIEnv *env, jclass clz, jobject jCloudDestInfo);
	jlong JNICALL getCloudBuckets(JNIEnv *env, jclass clz, jobject jCloudDestInfo, jobject out_Buckets);
	jlong JNICALL getCloudRegions(JNIEnv *env, jclass clz, jobject jCloudDestInfo, jobject out_CloudRegions);
	jstring JNICALL getRegionForBucket(JNIEnv *env, jclass clz, jobject jCloudDestInfo, jobject out_jrwRet);
	jlong testConnection(JNIEnv *env, jclass clz, jobject jCloudDestInfo);
	jlong testBIConnection(JNIEnv *env, jclass clz, jobject jCloudDestInfo);//added by cliicy.luo
	BOOL bNFConnectToCloud(JNIEnv *env, jclass clz, jobject jDestInfo, DWORD &dwCCIErrCode, HANDLE& handle, ICloudAPIs** cldAPIsPtr);
	jlong JNICALL GetLastArchiveCatalogUpdateTime(JNIEnv *env, jclass clz, jobject jDestInfo, jobject out_CatalogDetails);
	jstring JNICALL GetFileCopyCatalogPathy(JNIEnv *env, jclass, jstring MachineName, jlong ProductType);
	jlong JNICALL GetLastArchiveCatalogUpdateTime2(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jobject jDestInfo, jobject out_CatalogDetails);
	jlong JNICALL searchArchiveCatalogChildren2(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jstring inFileName, jstring inHostName, jstring inSearchpath, jobject inArchiveDestConfig,
	jlong in_lSearchOptions, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems);
	jlong JNICALL ArchiveOpenMachine(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jstring catalogDirUserName, jstring catalogDirPassword, jstring hostName, jobject jDestInfo, jobject pMachineHandle);
	jlong JNICALL GetArchiveVolumeList(JNIEnv *env, jclass clz, jlong pMachineHandle, jobject volumeList);
	jlong JNICALL ArchiveOpenVolume(JNIEnv *env, jclass clz, jlong pMachineHandle, jstring strVolume);
	jlong JNICALL GetArchiveChildItemCount(JNIEnv *env, jclass clz, jlong pVolumeHandle, jstring strPath, jobject childCount);
	jlong JNICALL GetArchiveChildItems(JNIEnv *env, jclass clz, jlong pVolumeHandle, jstring strPath, jobject archiveCatalogItems);
	jlong JNICALL GetArchiveChildItemsEx(JNIEnv *env, jclass clz, jlong pVolumeHandle, jstring strPath, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems);
	jlong JNICALL ArchiveCloseVolume(JNIEnv *env, jclass clz, jlong pVolumeHandle);
	jlong JNICALL ArchiveCloseMachine(JNIEnv *env, jclass clz, jlong pMachineHandle);

	/* ARCHIVE */
	int JArchiveDestConfig2ArchiveDiskDestInfo(JNIEnv *env, PARCHIVEDISKDESTINFO parchiveDestInfo, jobject* jDestInfo);
	int JArchiveDestConfig2ArchiveCloudDestInfo(JNIEnv *env, PARCHIVECLOUDDESTINFO parchiveDestInfo, jobject* jDestInfo);
	int JArchiveCloudConfig2ArchiveCloudDestInfo(JNIEnv *env, PARCHIVECLOUDDESTINFO parchiveDestInfo, jobject* jCloudDestInfo);

	int convertCVersionToJniVersion(JNIEnv *env, jclass clz, jstring strCatPath, vector <pCatalogChildItem> & CatalogList, jobject &archiveCatalogItems);
	int ConvertArchiveJobsInformation2JObject(JNIEnv *env, jclass clz, vector<ARCHIVE_ITEM_INFO>  & archiveJobs, jobject & out_archiveJobsList);
	int FillCatalogLastUpdateTime2JObject(JNIEnv *env, jclass clz, wstring szHostName, FILETIME & stLastUpdateTimeStamp, DWORD dwStatus, jobject & out_CatalogDetails);

	ARCHIVECLOUDDESTINFO ConvertCloudDestinationInfo(JNIEnv *env, jclass clz, jobject jcloudDestInfo);

	void processJJobScriptNodeList(JNIEnv *env, PAFJOBSCRIPT pafJS, jobject nodeList);
	jlong JNICALL GetArchiveJobsInfo(JNIEnv *env, jclass clz, jobject inout_jArchiveJob, jobject out_archiveJobsList);
	jlong JNICALL GetArchiveJobInfoCount(JNIEnv *env, jclass clz, jobject inout_jArchiveJob, jobject jobCount);
	jboolean JNICALL IsArchiveJobRunning(JNIEnv *env, jclass clz);
	jboolean JNICALL IsArchiveRestoreJobRunning(JNIEnv *env, jclass clz);
	jboolean JNICALL IsArchivePurgeJobRunning(JNIEnv *env, jclass clz);
	jboolean JNICALL IsArchiveCatalogSyncJobRunning(JNIEnv *env, jclass clz);
	jstring JNICALL GetArchiveDNSHostName(JNIEnv *env, jclass jclz);
	jstring JNICALL GetArchiveHostName(JNIEnv *env, jclass jclz);

	DWORD PrepareAFArchiveJS(DWORD dwDestType, ARCHIVEDISKDESTINFO* archiveDestDiskInfo, ARCHIVECLOUDDESTINFO* archiveDestCloudInfo, AFARCHIVEJOBSCRIPT** ppAFJobScript);
	DWORD CreateICloudAPIsObject(CDbgLog* log, ICloudAPIs** ptr);
	DWORD CreateIFileCopyCatalogMgrObject(CDbgLog* log, IFileCopyCatalogMgr** ptr);
	void UINT64TOJRWLong(JNIEnv *env, UINT64 val, jobject rwLong);
	void FreeArchiveJobScript(PAFARCHIVEJOBSCRIPT pafJS);
	jboolean JNICALL IsFileCopyJobRunning2(JNIEnv *env, jclass clz, jstring d2dMachineName);
	jboolean JNICALL IsFileArchiveJobRunning2(JNIEnv *env, jclass clz, jstring d2dMachineName);
	jboolean JNICALL IsArchiveRestoreJobRunning2(JNIEnv *env, jclass clz, jstring d2dMachineName);
	jboolean JNICALL IsArchivePurgeJobRunning2(JNIEnv *env, jclass clz, jstring d2dMachineName);
	jboolean JNICALL IsArchiveCatalogSyncJobRunning2(JNIEnv *env, jclass clz, jstring d2dMachineName);
	jlong DisableFileCopy(JNIEnv* env, jclass this_class, jstring strDest, jstring strDestDomain, jstring strDestUserName, jstring strDestPassword);
}