#pragma once
#include "stdafx.h"
#include <Windows.h>
/// VIX is replaced by vSphere  -- begin --
#include <map>
/// VIX is replaced by vSphere  -- end --
#include "V2PNativeFacade.h"

using namespace std;

/// VIX is replaced by vSphere  -- begin --
typedef struct _tagGuestFileInfo
{
	wstring strPathName;
	wstring strType;
	wstring strLinkTarget;
	__int64 nSize;
	__time64_t tModify;
	__time64_t tLastAccess;
	DWORD dwAttributes;
}GUEST_FILE_INFO, *PGUEST_FILE_INFO;

typedef struct _tagGuestProcessInfo
{
	wstring strName;
	wstring owner;
	wstring cmdline;
	DWORD pid;
	int nExitCode;
	__time64_t tStart;
	__time64_t tEnd;
}GUEST_PROCESS_INFO, *PGUEST_PROCESS_INFO;
/// VIX is replaced by vSphere  -- end --

class INativeClass
{
public:
	virtual void Release() {delete this; };

public:
	virtual BOOL LibInit(const char* jarList = NULL, BOOL bOldVer = FALSE) = 0;
	virtual void LibExit() = 0;
	virtual int connectToESX(WCHAR* esxServer, WCHAR* esxUser, WCHAR* esxPwd, WCHAR* esxPro, bool bIgnoreCert, long lPort) = 0;
	virtual int getVMServerType() = 0;
	virtual int checkVMServerLicense(WCHAR* esxName, WCHAR* dcName) = 0;
	virtual BOOL checkVMServerInMaintainenceMode(WCHAR* esxName, WCHAR* dcName) = 0;
	virtual WCHAR* getESXVersion(WCHAR* esxName, WCHAR* dcName) = 0;
	virtual int getESXNumberOfProcessors(WCHAR* esxName, OUT UINT& numberOfLogicalProcessors, OUT UINT& numberOfProcessors) = 0;
	virtual void disconnectESX() = 0;
	virtual ESXNode* getEsxNodeList(int *count) = 0;
	virtual DataStore* getESXHostDataStoreList(ESXNode esxNode, int *count) = 0;
	virtual VM_BasicInfo* getVMList(ESXNode esxNode, int *count) = 0;
	virtual BOOL checkResPool(ESXNode esxNode, WCHAR* resPool) = 0;
	virtual BOOL setInstanceUUID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* newInstanceUUID) = 0;
	virtual int checkDSBlockSize(ESXNode esxNode, WCHAR* dataStore) = 0;
	virtual WCHAR* takeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName, error_Info* info, bool quiesce = true) = 0;
	virtual WCHAR* checkandtakeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName) = 0;
	virtual WCHAR* getVMMoref(WCHAR* vmName, WCHAR* vmUUID) = 0;
	virtual WCHAR* getVMVersion(WCHAR* vmName, WCHAR* vmUUID) = 0;
	virtual BOOL revertSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL) = 0;
	virtual BOOL removeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL) = 0;
	virtual BOOL removeSnapShotAsync(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL) = 0;
	virtual BOOL removeSnapShotByName(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName) = 0;
	virtual WCHAR* getsnapshotCTF(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL) = 0;
	virtual WCHAR* getparentSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL) = 0;
	virtual Disk_Info* getVMDiskURLs(WCHAR* vmName, WCHAR* vmUUID, int *count) = 0;
	virtual Snapshot_Info* getVMSnapshotList(WCHAR* vmName, WCHAR* vmUUID, int *count) = 0;
	virtual Disk_Info* getSnapShotDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count) = 0;
	virtual AdrDisk_Info* getSnapShotAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count) = 0;
	virtual AdrDisk_Info* getVMAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, int *count) = 0;
	virtual VM_Info* getVMInfo(WCHAR* vmName, WCHAR* vmInstUUID) = 0;

    virtual int getVMInfoUnderDataCenter(const WCHAR* vmName, const WCHAR* dcName, VM_Info* pVMInfo) = 0;

	virtual void deleteCTKFiles(WCHAR* esxName, WCHAR* dcName, WCHAR* vmName, WCHAR* vmUUID) = 0;
	virtual BOOL hasSufficientPermission() = 0; //<sonmi01>2014-1-9 #87330: With upgrade to VDDK 5.5, we need to Check permissions when importing VM from VC
	virtual VMDataStoreInfo* getVMDataStoreDetails(ESXNode nodeDetails, WCHAR* dsName) = 0;
	virtual void rescanallHBA(ESXNode nodeDetails, BOOL rescanVC) = 0;
	virtual WCHAR* addCloneDataStore(ESXNode nodeDetails, WCHAR* dsGUID) = 0;
	virtual BOOL destroyandDeleteClone(ESXNode nodeDetails, WCHAR* dsName) = 0;
	virtual WCHAR* createApplianceVM(ESXNode nodeDetails, WCHAR* vmName) = 0;
	virtual WCHAR* createStndAloneApplianceVM(ESXNode nodeDetails) = 0;
	virtual BOOL deleteApplianceVM(ESXNode nodeDetails, WCHAR* vmName) = 0;
	virtual WCHAR* attachDiskToVM(WCHAR* vmName, WCHAR* diskURL, WCHAR* esxName, WCHAR* diskType) = 0;
	virtual WCHAR* detachDiskFromVM(WCHAR* vmName, WCHAR* diskURL) = 0;
	virtual BOOL isESXinCluster(ESXNode nodeDetails) = 0;
	virtual BOOL isESXunderVC(ESXNode nodeDetails) = 0;
	virtual WCHAR* checkandtakeApplianceSnapShot(WCHAR* vmName, WCHAR* snapshotName) = 0;
	virtual BOOL removeSnapshotFromAppliance(WCHAR* vmName, WCHAR*  snapRef) = 0;
	virtual error_Info createVMwareVirtualMachine(wchar_t* configFilePath,
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
		) = 0;
	virtual BOOL getSnapshotConfigInfo(WCHAR* vmName, WCHAR* vmUUID, wchar_t* snapshotId, wchar_t* pathToSave) = 0;
	virtual BOOL getVSSwriterfiles(WCHAR* vmName, WCHAR* vmUUID, wchar_t* snapshotId, wchar_t* pathToSave) = 0;
	virtual BOOL setVMNVRamFile(WCHAR* vmName, WCHAR* vmUUID, wchar_t* nvRamFile) = 0;
	virtual WCHAR* getVMNVRAMFile(WCHAR* vmName, WCHAR* vmUUID, wchar_t* pathToSave) = 0;
	virtual int getUsedDiskBlocks(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, WCHAR* diskChangeId, int DiskDeviceKey, PLARGE_INTEGER pbitmapSize, PLARGE_INTEGER pUsedSectorCount, WCHAR* bitmapFilePath, int chunkSize, int sectorSize) = 0;
	virtual int enableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID, BOOL bEnable) = 0;
	virtual Disk_Info* generateDiskBitMapForSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count) = 0;
	virtual int checkAndEnableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID) = 0;
	virtual BOOL getFile(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  fileName, WCHAR*  localPath) = 0;
	virtual BOOL getDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  diskURL, int deviceKey) = 0;
	virtual void deleteDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  snapshotURL) = 0;
	virtual BOOL setFileStream(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  fileName) = 0;
	virtual int readFileStream(BYTE* pBuff, __int64 offSet, int length, int* bytesRead) = 0;
	virtual BOOL powerOnVM(WCHAR* vmName, WCHAR* vmUUID) = 0;
	virtual void powerOffVM(WCHAR* vmName, WCHAR* vmUUID) = 0;
	virtual int getVMPowerState(WCHAR* vmName, WCHAR*  vmUUID) = 0;
	virtual int getVMToolsState(WCHAR* vmName, WCHAR*  vmUUID) = 0;
	virtual WCHAR* getVmdkFilePath(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, DWORD dwDiskSignaure) = 0;
	virtual BOOL removeAllSnapshots(WCHAR* vmName, WCHAR*  vmUUID) = 0;

	virtual int deleteVM(WCHAR* vmName, WCHAR* vmUUID) = 0;
	virtual int renameVM(WCHAR* vmName, WCHAR* vmUUID, WCHAR* vmNewname) = 0;
	virtual BOOL revertSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL) = 0;
	virtual BOOL removeSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL) = 0;
	virtual Disk_Info* getSnapShotDiskInfoByID(WCHAR* vmName, WCHAR*  vmUUID, WCHAR* snapshotURL, int *count) = 0;
	virtual int enableDiskUUIDForVM(WCHAR* vmName, WCHAR* vmUUID, BOOL bEnable) = 0;
	virtual void logUserEvent(WCHAR* vmName, WCHAR* vmUUID, WCHAR* eventMessage) = 0;
	virtual int isVMNameUsed(wchar_t* vmname) = 0;
	virtual BOOL VMHasSnapshot(WCHAR* vmName, WCHAR* vmUUID) = 0;

	virtual int SetvDSNetworkInfoEx(CONST VMNetworkAdapter_Info * pVMNetworkAdapter_Info, LONG Count) = 0; //<sonmi01>2013-6-5 #vds support

	virtual INT64 GetESXVFlashResource(wchar_t* esxHost, wchar_t* esxDC) = 0;
	virtual INT64 GetVMVFlashReadCache(wchar_t* configFilePath) = 0;

	virtual int cleanupHotAddedDisksAndConsolidateSnapshot(const wstring& esxHost, const vector<wstring>& proxyVMIPList, const vector<wstring>& proxyHotAddedDiskURLs, const wstring& protectedVMInstanceUUID) = 0;
	virtual VM_Info* getVMInfoByMoId(WCHAR* vmMoId) = 0;
	virtual int SetVMDiskInfo(CONST Disk_Info* pVMDiskInfo, LONG Count) = 0;
	virtual int getESXHostListByDatastoreMoRef(const wchar_t* datastoreMoRef, vector<wstring>& esxHostList) = 0;
	virtual int setVMCpuMemory(int numCPU, int numCoresPerSocket, long memoryMB) = 0;
	virtual wstring getThumbprint() = 0;
	virtual int consolidateVMDisks(const wchar_t* vmUuid) = 0;

	/// VIX is replaced by vSphere  -- begin --	
	virtual int ClearVMResult(IN int vmHandle) = 0;
	virtual int ClearAllVMResult() = 0;

	virtual int Vix_ConnectToHost(IN const char *pszHostName, IN int nHostPort, IN const char *pszUserName, IN const char *pszPassword,
		IN VixMgr_HostOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData) = 0;
	virtual void Vix_DisconnectFromHost(IN int iHostHandle) = 0;
	virtual int Vix_GetESXVersionVM(IN VixMgr_Handle hHostHandle, IN const char* pszVMName) = 0;
	virtual int VixVM_Open(IN int iHostHandle, IN const char *pszVMName) = 0;
	virtual int VixVM_LoginInGuest(IN int iHostHandle, IN const char *pszUserName, IN const char *pszPassword, IN int nOptions, IN VixEventProc *fnCallbackProc, void *pvClientData) = 0;
	virtual int VixVM_LogoutFromGuest(IN int vmHandle, IN VixEventProc *callbackProc, IN void *clientData) = 0;

	virtual int VixVM_RunProgramInGuest(IN int vmHandle, IN const char *pszGuestProgramName, IN const char *pszCommandLineArgs,
		IN VixMgr_RunProgramOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData) = 0;

	virtual int VixVM_CopyFileFromHostToGuest(IN int vmHandle, const char *hostPathName, const char *guestPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData) = 0;
	virtual int VixVM_CopyFileFromGuestToHost(IN int vmHandle, const char *guestPathName, const char *hostPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData) = 0;
	virtual int VixVM_DeleteFileInGuest(IN int vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData) = 0;
	virtual int VixVM_FileExistsInGuest(IN int vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData) = 0;
	virtual PGUEST_FILE_INFO VixVM_ListFileInGuest(IN int vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData) = 0;
	virtual int VixVM_FilterOutFile(IN int vmHandle, IN bool bRemoveFile) = 0;
	virtual int VixVM_CreateDirectoryInGuest(IN int vmHandle, const char *pathName, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData) = 0;
	virtual int VixVM_DeleteDirectoryInGuest(IN int vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData) = 0;
	virtual int VixVM_DirectoryExistsInGuest(IN int vmHandle, const char *pathName, VixEventProc *callbackProc, void *clientData) = 0;
	virtual int VixJob_GetNumProperties(IN int vmHandle, int resultPropertyID) = 0;
	virtual int VixJob_GetNthProperties(IN int vmHandle, int index, int propertyID, va_list args) = 0;
	virtual PGUEST_PROCESS_INFO VixVM_ListProcessesInGuest(IN int vmHandle, int options, VixEventProc *callbackProc, void *clientData) = 0;

	/// \fn VixMgr_Error VixJob_Wait( ... ) = 0;
	/// \brief
	/// \param vmHandle that can be returned by calling function VixHost_Connect(), VixVM_Open(), VixVM_LoginInGuest(), ... any above function that return type is VixMgr_Handle.
	virtual int VixJob_Wait(IN int vmHandle, VixMgr_PropertyID firstPropertyID, ...) = 0;
	virtual int VixJob_CheckCompletion(IN int vmHandle, OUT bool *complete) = 0;
	virtual BOOL VixVM_GetResultBOOL(IN int vmHandle) = 0;
	virtual void VixVM_setResultBOOL(IN int vmHandle, IN BOOL bResult) = 0;
	virtual int VixVM_GetLastError(IN int iHandle, OUT wchar_t* pszError, IN size_t ccError) = 0;
	/// VIX is replaced by vSphere  -- end --

	
public:
	//////////////////////////////////////////////////////////////////////////
	// for vcloudmanager
	//////////////////////////////////////////////////////////////////////////
	virtual int connectVCloud(const wchar_t* vcloudDirectorServerName, const wchar_t* username, const wchar_t* password, const wchar_t* protocol = L"https", const int port = 443, const bool ignoreCert = true) = 0;
	virtual void disconnectVCloud() = 0;

	virtual int saveVAppInfo(const wchar_t* vAppId, const wchar_t* vAppInfoFilePath) = 0;
	virtual int getVMListOfVApp(const wchar_t* vAppId, VCloudVM_Info** vmList, int* vmCount) = 0;

	virtual VCloud_CreateVAppResult* createVApp(const VCloud_CreateVAppParams* createVAppParams) = 0;
	virtual VCloud_ImportVMResult* importVM(const VCloud_ImportVMParams* importVMParams) = 0;

	// delete vApp and children VMs
	virtual int deleteVApp(const wchar_t* vAppId) = 0;

	// rename vApp and children VMs
	// append: true - append suffix, false - remove suffix; 
	// renameVM: true - rename vApp and VMs, false - rename vApp only;
	virtual int renameVAppEx(const wchar_t* vAppId, const wchar_t* suffix, const bool append, const bool renameVM) = 0;

	virtual int getVApp(const wchar_t* vAppId, VCloudVApp_Info* vAppInfo) = 0;
	virtual int getVAppListOfVDC(const wchar_t* vdcId, VCloudVApp_Info** vAppList, int* count) = 0;
	virtual int getVAppInOrg(const wchar_t* vdcId, const wchar_t* vAppName, VCloudVApp_Info* vAppInfo) = 0;

	virtual int powerOnVApp(const wchar_t* vAppId) = 0;
	virtual int powerOffVApp(const wchar_t* vAppId) = 0;

	virtual int getDatastore(const wchar_t* datastoreId, VCloudDatastore_Info* datastoreInfo) = 0;

	virtual int getESXHostListOfVDC(const wchar_t* vdcId, VCloudESXHost_Info** esxHostList, int* count) = 0;
	virtual int getStorageProfileListOfVDC(const wchar_t* vdcId, VCloud_StorageProfile** storageProfileList, int* count) = 0;
	virtual int verifyVCloudInfo(VCloud_VerifyInfo* vCloudInfo) = 0;
};

class CNativeClassFactory
{
	//public:
	//	CNativeClassFactory();
	//	virtual ~CNativeClassFactory();
//this is for RPC client class
public:
	static INativeClass* CreateInstanceNativeClassRPC();

//this is for RPC server class
public:
	static INativeClass* CreateInstanceNativeClass();
	static void SetJVM(INativeClass * pClass, void *pJVM);
	static void* GetJVM(INativeClass * pClass);

};

//<sonmi01>2015-9-6 ###???
class V2PNFGlobals
{
private:
	static BOOL m_isSharedJVM; //default is FALSE

public:
	static VOID Set_isSharedJVM(BOOL isSharedJVM)
	{
		m_isSharedJVM = isSharedJVM;
	}

	static BOOL Get_isSharedJVM()
	{
		return m_isSharedJVM;
	}
};