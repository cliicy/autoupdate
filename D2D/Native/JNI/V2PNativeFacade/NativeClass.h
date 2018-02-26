#pragma once
#include "stdafx.h"
#include <Windows.h>
#include "jni.h"
#include "NativeClassIntf.h"

/// VIX is replaced by vSphere  -- begin --
#include <map>
/// VIX is replaced by vSphere  -- end --

using namespace std;

#define CRC32_POLYNOMIAL		0XEDB88320L
#define MAX_GUID_LENGTH  39

GUID OutputStringToGUID(CHAR* lpszGUID);

///// VIX is replaced by vSphere  -- begin --
//typedef struct _tagGuestFileInfo
//{
//	wstring strPathName;
//	wstring strType;
//	wstring strLinkTarget;
//	__int64 nSize;
//	__time64_t tModify;
//	__time64_t tLastAccess;
//	DWORD dwAttributes;
//}GUEST_FILE_INFO, *PGUEST_FILE_INFO;
//
//typedef struct _tagGuestProcessInfo
//{
//	wstring strName;
//	wstring owner;
//	wstring cmdline;
//	DWORD pid;
//	int nExitCode;
//	__time64_t tStart;
//	__time64_t tEnd;
//}GUEST_PROCESS_INFO,*PGUEST_PROCESS_INFO;
///// VIX is replaced by vSphere  -- end --

class NativeClass : public INativeClass
{
public:
	NativeClass();
	virtual ~NativeClass();
	BOOL LibInit(const char* jarList = NULL, BOOL bOldVer=FALSE);
	void LibExit();
	int connectToESX(WCHAR* esxServer, WCHAR* esxUser, WCHAR* esxPwd, WCHAR* esxPro, bool bIgnoreCert, long lPort);
	int getVMServerType();
	int checkVMServerLicense(WCHAR* esxName, WCHAR* dcName);
	BOOL checkVMServerInMaintainenceMode(WCHAR* esxName, WCHAR* dcName);
	WCHAR* getESXVersion(WCHAR* esxName, WCHAR* dcName);
	int getESXNumberOfProcessors(WCHAR* esxName, OUT UINT& numberOfLogicalProcessors, OUT UINT& numberOfProcessors);
	void disconnectESX();
	void SetContextClassLoader(JNIEnv* env);
#ifdef __OLD_JNI_ENV__
	BOOL AttachToJVM();
#else
	JNIEnv * AttachToJVMThread(); //<sonmi01>2015-9-9 ###???
#endif
	void DetachJVM();
	ESXNode* getEsxNodeList(int *count);
	DataStore* getESXHostDataStoreList(ESXNode esxNode, int *count);
	VM_BasicInfo* getVMList(ESXNode esxNode, int *count);
	BOOL checkResPool(ESXNode esxNode, WCHAR* resPool);
	BOOL setInstanceUUID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* newInstanceUUID);
	int checkDSBlockSize(ESXNode esxNode, WCHAR* dataStore);
	WCHAR* takeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName, error_Info* info, bool quiesce = true);
	WCHAR* checkandtakeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName);
	WCHAR* getVMMoref(WCHAR* vmName, WCHAR* vmUUID);
	WCHAR* getVMVersion(WCHAR* vmName, WCHAR* vmUUID);
	BOOL revertSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
	BOOL removeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
	BOOL removeSnapShotAsync(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
	BOOL removeSnapShotByName(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName);
	WCHAR* getsnapshotCTF(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
	WCHAR* getparentSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
	Disk_Info* getVMDiskURLs(WCHAR* vmName, WCHAR* vmUUID, int *count);
	Snapshot_Info* getVMSnapshotList(WCHAR* vmName, WCHAR* vmUUID, int *count);
	Disk_Info* getSnapShotDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count);
	AdrDisk_Info* getSnapShotAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count);
	AdrDisk_Info* getVMAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, int *count);
	VM_Info* getVMInfo(WCHAR* vmName, WCHAR* vmInstUUID);
    int getVMInfoUnderDataCenter(const WCHAR* vmName, const WCHAR* dcName, OUT VM_Info* pVmInfo);
	void deleteCTKFiles(WCHAR* esxName, WCHAR* dcName, WCHAR* vmName, WCHAR* vmUUID);
	BOOL hasSufficientPermission(); //<sonmi01>2014-1-9 #87330: With upgrade to VDDK 5.5, we need to Check permissions when importing VM from VC
	VMDataStoreInfo* getVMDataStoreDetails(ESXNode nodeDetails, WCHAR* dsName);
	void rescanallHBA(ESXNode nodeDetails, BOOL rescanVC);
	WCHAR* addCloneDataStore(ESXNode nodeDetails, WCHAR* dsGUID);
	BOOL destroyandDeleteClone(ESXNode nodeDetails, WCHAR* dsName);
	WCHAR* createApplianceVM(ESXNode nodeDetails, WCHAR* vmName);
	WCHAR* createStndAloneApplianceVM(ESXNode nodeDetails);
	BOOL deleteApplianceVM(ESXNode nodeDetails, WCHAR* vmName);
	WCHAR* attachDiskToVM(WCHAR* vmName, WCHAR* diskURL, WCHAR* esxName, WCHAR* diskType);
	WCHAR* detachDiskFromVM(WCHAR* vmName, WCHAR* diskURL);
	BOOL isESXinCluster(ESXNode nodeDetails);
	BOOL isESXunderVC(ESXNode nodeDetails);
	WCHAR* checkandtakeApplianceSnapShot(WCHAR* vmName, WCHAR* snapshotName);
	BOOL removeSnapshotFromAppliance(WCHAR* vmName, WCHAR*  snapRef);
	error_Info createVMwareVirtualMachine (wchar_t* configFilePath,
									wchar_t* vcName,
									wchar_t* esxHost,
									wchar_t* esxDC,
									wchar_t* vmResPool,
									wchar_t* datastoreOfVM,
									wchar_t* vmNewName,
									int numDisks,
									wchar_t** diskUrlList, 
									wchar_t** datastoreList,
									BOOL overwriteVM,
									BOOL recoverToOriginal,
									VM_Info* pVMInfo //out									
									);
	BOOL getSnapshotConfigInfo(WCHAR* vmName, WCHAR* vmUUID, wchar_t* snapshotId, wchar_t* pathToSave);
	BOOL getVSSwriterfiles(WCHAR* vmName, WCHAR* vmUUID, wchar_t* snapshotId, wchar_t* pathToSave);
	BOOL setVMNVRamFile(WCHAR* vmName, WCHAR* vmUUID, wchar_t* nvRamFile);
	WCHAR* getVMNVRAMFile(WCHAR* vmName, WCHAR* vmUUID, wchar_t* pathToSave);
	int getUsedDiskBlocks(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, WCHAR* diskChangeId,int DiskDeviceKey,PLARGE_INTEGER pbitmapSize, PLARGE_INTEGER pUsedSectorCount,WCHAR* bitmapFilePath,int chunkSize, int sectorSize);
	int enableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID,BOOL bEnable);
	Disk_Info* generateDiskBitMapForSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count);
	int checkAndEnableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID);
	BOOL getFile(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  fileName, WCHAR*  localPath);
	BOOL getDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  diskURL, int deviceKey);
	void deleteDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  snapshotURL);
	BOOL setFileStream(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  fileName);
	int readFileStream(BYTE* pBuff, __int64 offSet, int length, int* bytesRead);
	BOOL powerOnVM(WCHAR* vmName, WCHAR* vmUUID);
	void powerOffVM(WCHAR* vmName, WCHAR* vmUUID);
	int getVMPowerState(WCHAR* vmName, WCHAR*  vmUUID);
	int getVMToolsState(WCHAR* vmName, WCHAR*  vmUUID);
	WCHAR* getVmdkFilePath(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, DWORD dwDiskSignaure);
	BOOL removeAllSnapshots(WCHAR* vmName, WCHAR*  vmUUID);
	JavaVM * jvm;
	int deleteVM(WCHAR* vmName, WCHAR* vmUUID);
	int renameVM(WCHAR* vmName, WCHAR* vmUUID,  WCHAR* vmNewname);	
	BOOL revertSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
	BOOL removeSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
	Disk_Info* getSnapShotDiskInfoByID(WCHAR* vmName, WCHAR*  vmUUID, WCHAR* snapshotURL, int *count);
	int enableDiskUUIDForVM(WCHAR* vmName, WCHAR* vmUUID, BOOL bEnable);
	void logUserEvent(WCHAR* vmName, WCHAR* vmUUID,WCHAR* eventMessage);
	int isVMNameUsed(wchar_t* vmname);
	BOOL VMHasSnapshot(WCHAR* vmName, WCHAR* vmUUID);

	int SetvDSNetworkInfoEx(CONST VMNetworkAdapter_Info * pVMNetworkAdapter_Info, LONG Count); //<sonmi01>2013-6-5 #vds support

	INT64 GetESXVFlashResource(wchar_t* esxHost, wchar_t* esxDC);
	INT64 GetVMVFlashReadCache(wchar_t* configFilePath); 	

    int cleanupHotAddedDisksAndConsolidateSnapshot(const wstring& esxHost, const vector<wstring>& proxyVMIPList, const vector<wstring>& proxyHotAddedDiskURLs, const wstring& protectedVMInstanceUUID);
	VM_Info* getVMInfoByMoId(WCHAR* vmMoId);
	int SetVMDiskInfo(CONST Disk_Info* pVMDiskInfo, LONG Count);
	int getESXHostListByDatastoreMoRef(const wchar_t* datastoreMoRef, vector<wstring>& esxHostList);
	int setVMCpuMemory(int numCPU, int numCoresPerSocket, long memoryMB);
	wstring getThumbprint();
	int consolidateVMDisks(const wchar_t* vmUuid);

/// VIX is replaced by vSphere  -- begin --	
	typedef struct _tagVMResult
	{
		PGUEST_FILE_INFO m_pGuestFileInfo;
		PGUEST_PROCESS_INFO m_pGuestProcessInfo;
		int m_nListItemCount;
		BOOL m_bResult;
	}VM_RESULT,*PVM_RESULT;
	map <int, VM_RESULT> m_mapVMResult;
	typedef pair <int, VM_RESULT> VMResult_Pair;
	typedef map <int, VM_RESULT>::iterator VMResult_Iterator;
	int ClearVMResult(IN int vmHandle);
	int ClearAllVMResult();

	int Vix_ConnectToHost(IN const char *pszHostName, IN int nHostPort, IN const char *pszUserName, IN const char *pszPassword,
		IN VixMgr_HostOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData);
	void Vix_DisconnectFromHost(IN int iHostHandle);
	int Vix_GetESXVersionVM(IN VixMgr_Handle hHostHandle, IN const char* pszVMName);
	int VixVM_Open(IN int iHostHandle, IN const char *pszVMName);
	int VixVM_LoginInGuest(IN int iHostHandle, IN const char *pszUserName, IN const char *pszPassword, IN int nOptions, IN VixEventProc *fnCallbackProc, void *pvClientData);
	int VixVM_LogoutFromGuest(IN int vmHandle, IN VixEventProc *callbackProc, IN void *clientData);

	int VixVM_RunProgramInGuest(IN int vmHandle, IN const char *pszGuestProgramName, IN const char *pszCommandLineArgs,
		IN VixMgr_RunProgramOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData);

	int VixVM_CopyFileFromHostToGuest(IN int vmHandle, const char *hostPathName, const char *guestPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData);
	int VixVM_CopyFileFromGuestToHost(IN int vmHandle, const char *guestPathName, const char *hostPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData);
	int VixVM_DeleteFileInGuest(IN int vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData);
	int VixVM_FileExistsInGuest(IN int vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData);
	PGUEST_FILE_INFO VixVM_ListFileInGuest(IN int vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData);
	int VixVM_FilterOutFile(IN int vmHandle, IN bool bRemoveFile);
	int VixVM_CreateDirectoryInGuest(IN int vmHandle, const char *pathName, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData);
	int VixVM_DeleteDirectoryInGuest(IN int vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData);
	int VixVM_DirectoryExistsInGuest(IN int vmHandle, const char *pathName, VixEventProc *callbackProc, void *clientData);
	int VixJob_GetNumProperties(IN int vmHandle, int resultPropertyID);
	int VixJob_GetNthProperties(IN int vmHandle, int index, int propertyID, va_list args);
	PGUEST_PROCESS_INFO VixVM_ListProcessesInGuest(IN int vmHandle, int options, VixEventProc *callbackProc, void *clientData);

/// \fn VixMgr_Error VixJob_Wait( ... );
/// \brief
/// \param vmHandle that can be returned by calling function VixHost_Connect(), VixVM_Open(), VixVM_LoginInGuest(), ... any above function that return type is VixMgr_Handle.
	int VixJob_Wait(IN int vmHandle, VixMgr_PropertyID firstPropertyID, ...);
	int VixJob_CheckCompletion(IN int vmHandle, OUT bool *complete);
	BOOL VixVM_GetResultBOOL(IN int vmHandle);
	void VixVM_setResultBOOL(IN int vmHandle, IN BOOL bResult);
	int VixVM_GetLastError(IN int iHandle, OUT wchar_t* pszError, IN size_t ccError );
/// VIX is replaced by vSphere  -- end --

private:
#ifdef __OLD_JNI_ENV__
	JNIEnv *env; //<sonmi01>2015-9-9 ###???
#endif
	//JavaVM * jvm;
	WCHAR szserverName[PATH_SIZE], szuserName[PATH_SIZE], szPassword[PATH_SIZE], szPro[PATH_SIZE];
	long Port;
	bool bCertificate;
	ESXNode* esxDetails;
	DataStore* esxDS;
	VM_BasicInfo* vmList;
	VM_Info* vmInf;
	VMDataStoreInfo* dsInf;
	Disk_Info* diskList;
	AdrDisk_Info* adrDiskList;
	Snapshot_Info* snapList;
	WCHAR snapShot_URL[PATH_SIZE];
	WCHAR vmMoref[PATH_SIZE];
	WCHAR snapshotCTF[PATH_SIZE];
	WCHAR serverVersion[PATH_SIZE];
	WCHAR parentSnapshot[PATH_SIZE];
	WCHAR diskURL[PATH_SIZE];
	WCHAR nvramFile[PATH_SIZE];
	WCHAR cloneDS[PATH_SIZE];
	JNIEnv* create_vm(JavaVM ** jvm, const char* jarList = NULL, BOOL bOldVer=FALSE);
	JNIEnv* create_vm_debug(JavaVM ** jvm, const char* jarList = NULL, BOOL bOldVer=FALSE); //<sonmi01>2013-11-26 ###???
	BOOL b_Attached;
	jobject objectInstance;

	
	//////////////////////////////////////////////////////////////////////////
	// for vcloudmanager
	//////////////////////////////////////////////////////////////////////////
private: 
	jobject jvcloudInstance;

public:
	int connectVCloud(const wchar_t* vcloudDirectorServerName, const wchar_t* username, const wchar_t* password, const wchar_t* protocol = L"https", const int port = 443, const bool ignoreCert = true);
	void disconnectVCloud();

	int saveVAppInfo(const wchar_t* vAppId, const wchar_t* vAppInfoFilePath);
	int getVMListOfVApp(const wchar_t* vAppId, VCloudVM_Info** vmList, int* vmCount);

	VCloud_CreateVAppResult* createVApp(const VCloud_CreateVAppParams* createVAppParams);
	VCloud_ImportVMResult* importVM(const VCloud_ImportVMParams* importVMParams); 

	// delete vApp and children VMs
	int deleteVApp(const wchar_t* vAppId);

	// rename vApp and children VMs
	// append: true - append suffix, false - remove suffix; 
	// renameVM: true - rename vApp and VMs, false - rename vApp only;
	int renameVAppEx(const wchar_t* vAppId, const wchar_t* suffix, const bool append, const bool renameVM);

	int getVApp(const wchar_t* vAppId, VCloudVApp_Info* vAppInfo);
	int getVAppListOfVDC(const wchar_t* vdcId, VCloudVApp_Info** vAppList, int* count);
	int getVAppInOrg(const wchar_t* vdcId, const wchar_t* vAppName, VCloudVApp_Info* vAppInfo);

	int powerOnVApp(const wchar_t* vAppId);
	int powerOffVApp(const wchar_t* vAppId);

	int getDatastore(const wchar_t* datastoreId, VCloudDatastore_Info* datastoreInfo);

	int getESXHostListOfVDC(const wchar_t* vdcId, VCloudESXHost_Info** esxHostList, int* count);
	int getStorageProfileListOfVDC(const wchar_t* vdcId, VCloud_StorageProfile** storageProfileList, int* count);
	int verifyVCloudInfo(VCloud_VerifyInfo* vCloudInfo);
};

/*
comment out this class, it was already defined in drcommonlib.h
typedef struct _ASAG_CRC
{
	BOOL                   CRCTableInitialized;
	BOOL                   VerifyCRC;
	DWORD                  CRCTable[256];
	DWORD                  chkSum;

	VOID                   BuildCRCTable();
	VOID                   CSSCheckSum(PCHAR buffer, DWORD count);
	VOID                   CSSCheckSum(PCHAR buffer, DWORD count, DWORD *pdwCRC);
	_ASAG_CRC()    { CRCTableInitialized = FALSE; }
}    ASAG_CRC, *PASAG_CRC;
*/

