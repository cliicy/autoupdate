#pragma once
#include <comdef.h>
#include <atlstr.h>

#include "V2PNativeFacadeSrv_i.h"//add additional include directories in project
#include "V2PNF_Log.h"
#include "NativeClassIntf.h"

//_COM_SMARTPTR_TYPEDEF(ICNativeCmdCtrl, __uuidof(ICNativeCmdCtrl));

class NativeClassRPC :
	public INativeClass
{
public:
	NativeClassRPC();
	virtual ~NativeClassRPC();

public:
	virtual BOOL LibInit(const char* jarList = NULL, BOOL bOldVer = FALSE);
	virtual void LibExit();

	virtual int connectToESX(WCHAR* esxServer, WCHAR* esxUser, WCHAR* esxPwd, WCHAR* esxPro, bool bIgnoreCert, long lPort);
	static int connectToESX_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int getVMServerType();
	static int getVMServerType_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);


	virtual int checkVMServerLicense(WCHAR* esxName, WCHAR* dcName);
	static int checkVMServerLicense_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL checkVMServerInMaintainenceMode(WCHAR* esxName, WCHAR* dcName);
    static int checkVMServerInMaintainenceMode_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* getESXVersion(WCHAR* esxName, WCHAR* dcName);
    static int getESXVersion_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int getESXNumberOfProcessors(WCHAR* esxName, OUT UINT& numberOfLogicalProcessors, OUT UINT& numberOfProcessors);
    static int getESXNumberOfProcessors_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //!!unfinished for both two
	virtual void disconnectESX();
	static int disconnectESX_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual ESXNode* getEsxNodeList(int *count);
	static int getEsxNodeList_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    // 2015-05-26
	virtual DataStore* getESXHostDataStoreList(ESXNode esxNode, int *count);
    static int getESXHostDataStoreList_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual VM_BasicInfo* getVMList(ESXNode esxNode, int *count);
	static int getVMList_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL checkResPool(ESXNode esxNode, WCHAR* resPool);
    static int checkResPool_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL setInstanceUUID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* newInstanceUUID);
    static int setInstanceUUID_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int checkDSBlockSize(ESXNode esxNode, WCHAR* dataStore);
    static int checkDSBlockSize_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //!!!!
	virtual WCHAR* takeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName, error_Info* info, bool quiesce = true);
    static int takeSnapShot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    virtual WCHAR* checkandtakeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName);
    static int checkandtakeSnapShot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* getVMMoref(WCHAR* vmName, WCHAR* vmUUID);
    static int getVMMoref_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* getVMVersion(WCHAR* vmName, WCHAR* vmUUID);
    static int getVMVersion_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //
	virtual BOOL revertSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
    static int revertSnapShot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL removeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
    static int removeSnapShot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL removeSnapShotAsync(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
    static int removeSnapShotAsync_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL removeSnapShotByName(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName);
    static int removeSnapShotByName_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* getsnapshotCTF(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
    static int getsnapshotCTF_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* getparentSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
    static int getparentSnapshot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //2015-05-27 40 + 9 + 9
	virtual Disk_Info* getVMDiskURLs(WCHAR* vmName, WCHAR* vmUUID, int *count);
    static int getVMDiskURLs_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual Snapshot_Info* getVMSnapshotList(WCHAR* vmName, WCHAR* vmUUID, int *count);
    static int getVMSnapshotList_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual Disk_Info* getSnapShotDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count);
    static int getSnapShotDiskInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual AdrDisk_Info* getSnapShotAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count);
    static int getSnapShotAdrDiskInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual AdrDisk_Info* getVMAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, int *count);
    static int getVMAdrDiskInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual VM_Info* getVMInfo(WCHAR* vmName, WCHAR* vmInstUUID);
    static int getVMInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    virtual int getVMInfoUnderDataCenter(const WCHAR* vmName, const WCHAR* dcName, VM_Info* pVMInfo);
    static int getVMInfoUnderDataCenter_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual void deleteCTKFiles(WCHAR* esxName, WCHAR* dcName, WCHAR* vmName, WCHAR* vmUUID);
    static int deleteCTKFiles_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL hasSufficientPermission(); //<sonmi01>2014-1-9 #87330: With upgrade to VDDK 5.5, we need to Check permissions when importing VM from VC
    static int hasSufficientPermission_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual VMDataStoreInfo* getVMDataStoreDetails(ESXNode nodeDetails, WCHAR* dsName);
    static int getVMDataStoreDetails_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual void rescanallHBA(ESXNode nodeDetails, BOOL rescanVC);
    static int rescanallHBA_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //
	virtual WCHAR* addCloneDataStore(ESXNode nodeDetails, WCHAR* dsGUID);
    static int addCloneDataStore_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL destroyandDeleteClone(ESXNode nodeDetails, WCHAR* dsName);
    static int destroyandDeleteClone_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* createApplianceVM(ESXNode nodeDetails, WCHAR* vmName);
    static int createApplianceVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* createStndAloneApplianceVM(ESXNode nodeDetails);
    static int createStndAloneApplianceVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL deleteApplianceVM(ESXNode nodeDetails, WCHAR* vmName);
    static int deleteApplianceVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* attachDiskToVM(WCHAR* vmName, WCHAR* diskURL, WCHAR* esxName, WCHAR* diskType);
    static int attachDiskToVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* detachDiskFromVM(WCHAR* vmName, WCHAR* diskURL);
    static int detachDiskFromVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL isESXinCluster(ESXNode nodeDetails);
    static int isESXinCluster_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);


	virtual BOOL isESXunderVC(ESXNode nodeDetails);
	static int isESXunderVC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* checkandtakeApplianceSnapShot(WCHAR* vmName, WCHAR* snapshotName);
    static int checkandtakeApplianceSnapShot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL removeSnapshotFromAppliance(WCHAR* vmName, WCHAR*  snapRef);
    static int removeSnapshotFromAppliance_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //2015-05-28
    ////done, need to check
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
		);
    static int createVMwareVirtualMachine_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);


    //proc 
	virtual BOOL getSnapshotConfigInfo(WCHAR* vmName, WCHAR* vmUUID, wchar_t* snapshotId, wchar_t* pathToSave);
    static int getSnapshotConfigInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //proc 
	virtual BOOL getVSSwriterfiles(WCHAR* vmName, WCHAR* vmUUID, wchar_t* snapshotId, wchar_t* pathToSave);
    static int getVSSwriterfiles_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //proc 
	virtual BOOL setVMNVRamFile(WCHAR* vmName, WCHAR* vmUUID, wchar_t* nvRamFile);
    static int setVMNVRamFile_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //proc
	virtual WCHAR* getVMNVRAMFile(WCHAR* vmName, WCHAR* vmUUID, wchar_t* pathToSave);
    static int getVMNVRAMFile_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    ////done , need to check  unfinished large_integer
	virtual int getUsedDiskBlocks(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, WCHAR* diskChangeId, int DiskDeviceKey, PLARGE_INTEGER pbitmapSize, PLARGE_INTEGER pUsedSectorCount, WCHAR* bitmapFilePath, int chunkSize, int sectorSize);
    static int getUsedDiskBlocks_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);


	virtual int enableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID, BOOL bEnable);
    static int enableChangeBlockTracking_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual Disk_Info* generateDiskBitMapForSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count);
    static int generateDiskBitMapForSnapshot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int checkAndEnableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID);
    static int checkAndEnableChangeBlockTracking_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL getFile(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  fileName, WCHAR*  localPath);
    static int getFile_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL getDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  diskURL, int deviceKey);
    static int getDiskBitMap_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual void deleteDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  snapshotURL);
    static int deleteDiskBitMap_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL setFileStream(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  fileName);
    static int setFileStream_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //!!!!unfinished BYTE* and __int64, FengWei did it
	virtual int readFileStream(BYTE* pBuff, __int64 offSet, int length, int* bytesRead);
    //static int readFileStream_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL powerOnVM(WCHAR* vmName, WCHAR* vmUUID);
    static int powerOnVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual void powerOffVM(WCHAR* vmName, WCHAR* vmUUID);
    static int powerOffVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int getVMPowerState(WCHAR* vmName, WCHAR*  vmUUID);
    static int getVMPowerState_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int getVMToolsState(WCHAR* vmName, WCHAR*  vmUUID);
    static int getVMToolsState_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual WCHAR* getVmdkFilePath(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, DWORD dwDiskSignaure);
    static int getVmdkFilePath_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL removeAllSnapshots(WCHAR* vmName, WCHAR*  vmUUID);
    static int removeAllSnapshots_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);





	virtual int deleteVM(WCHAR* vmName, WCHAR* vmUUID);
    static int deleteVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int renameVM(WCHAR* vmName, WCHAR* vmUUID, WCHAR* vmNewname);
    static int renameVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);
    //2015-05-29 16
	virtual BOOL revertSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
    static int revertSnapShotByID_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL removeSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL);
    static int removeSnapShotByID_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual Disk_Info* getSnapShotDiskInfoByID(WCHAR* vmName, WCHAR*  vmUUID, WCHAR* snapshotURL, int *count);
    static int getSnapShotDiskInfoByID_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int enableDiskUUIDForVM(WCHAR* vmName, WCHAR* vmUUID, BOOL bEnable);
    static int enableDiskUUIDForVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual void logUserEvent(WCHAR* vmName, WCHAR* vmUUID, WCHAR* eventMessage);
    static int logUserEvent_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int isVMNameUsed(wchar_t* vmname);
    static int isVMNameUsed_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual BOOL VMHasSnapshot(WCHAR* vmName, WCHAR* vmUUID);
    static int VMHasSnapshot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    ////done need to check
	virtual int SetvDSNetworkInfoEx(CONST VMNetworkAdapter_Info * pVMNetworkAdapter_Info, LONG Count); //<sonmi01>2013-6-5 #vds support
    static int SetvDSNetworkInfoEx_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);


	virtual INT64 GetESXVFlashResource(wchar_t* esxHost, wchar_t* esxDC);
    static int GetESXVFlashResource_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual INT64 GetVMVFlashReadCache(wchar_t* configFilePath);
    static int GetVMVFlashReadCache_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //!!!done, need to check
	virtual int cleanupHotAddedDisksAndConsolidateSnapshot(const wstring& esxHost, const vector<wstring>& proxyVMIPList, const vector<wstring>& proxyHotAddedDiskURLs, const wstring& protectedVMInstanceUUID);
    static int cleanupHotAddedDisksAndConsolidateSnapshot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);


	virtual VM_Info* getVMInfoByMoId(WCHAR* vmMoId);
    static int getVMInfoByMoId_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //done, need to check
	virtual int SetVMDiskInfo(CONST Disk_Info* pVMDiskInfo, LONG Count);
    static int SetVMDiskInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);


    //done, need to check
	virtual int getESXHostListByDatastoreMoRef(const wchar_t* datastoreMoRef, vector<wstring>& esxHostList);
    static int getESXHostListByDatastoreMoRef_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);


	virtual int setVMCpuMemory(int numCPU, int numCoresPerSocket, long memoryMB);
    static int setVMCpuMemory_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual wstring getThumbprint();
    static int getThumbprint_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int consolidateVMDisks(const wchar_t* vmUuid);
	static int consolidateVMDisks_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	/// VIX is replaced by vSphere  -- begin --	
	virtual int ClearVMResult(IN int vmHandle);
	virtual int ClearAllVMResult();

	virtual int Vix_ConnectToHost(IN const char *pszHostName, IN int nHostPort, IN const char *pszUserName, IN const char *pszPassword,
		IN VixMgr_HostOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData);
	virtual void Vix_DisconnectFromHost(IN int iHostHandle);
	virtual int Vix_GetESXVersionVM(IN VixMgr_Handle hHostHandle, IN const char* pszVMName);
	virtual int VixVM_Open(IN int iHostHandle, IN const char *pszVMName);
	virtual int VixVM_LoginInGuest(IN int iHostHandle, IN const char *pszUserName, IN const char *pszPassword, IN int nOptions, IN VixEventProc *fnCallbackProc, void *pvClientData);
	virtual int VixVM_LogoutFromGuest(IN int vmHandle, IN VixEventProc *callbackProc, IN void *clientData);

	virtual int VixVM_RunProgramInGuest(IN int vmHandle, IN const char *pszGuestProgramName, IN const char *pszCommandLineArgs,
		IN VixMgr_RunProgramOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData);

	virtual int VixVM_CopyFileFromHostToGuest(IN int vmHandle, const char *hostPathName, const char *guestPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData);
	virtual int VixVM_CopyFileFromGuestToHost(IN int vmHandle, const char *guestPathName, const char *hostPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData);
	virtual int VixVM_DeleteFileInGuest(IN int vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData);
	virtual int VixVM_FileExistsInGuest(IN int vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData);
	virtual PGUEST_FILE_INFO VixVM_ListFileInGuest(IN int vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData);
	virtual int VixVM_FilterOutFile(IN int vmHandle, IN bool bRemoveFile);
	virtual int VixVM_CreateDirectoryInGuest(IN int vmHandle, const char *pathName, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData);
	virtual int VixVM_DeleteDirectoryInGuest(IN int vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData);
	virtual int VixVM_DirectoryExistsInGuest(IN int vmHandle, const char *pathName, VixEventProc *callbackProc, void *clientData);
	virtual int VixJob_GetNumProperties(IN int vmHandle, int resultPropertyID);
	virtual int VixJob_GetNthProperties(IN int vmHandle, int index, int propertyID, va_list args);
	virtual PGUEST_PROCESS_INFO VixVM_ListProcessesInGuest(IN int vmHandle, int options, VixEventProc *callbackProc, void *clientData);

	/// \fn VixMgr_Error VixJob_Wait( ... );
	/// \brief
	/// \param vmHandle that can be returned by calling function VixHost_Connect(), VixVM_Open(), VixVM_LoginInGuest(), ... any above function that return type is VixMgr_Handle.
	virtual int VixJob_Wait(IN int vmHandle, VixMgr_PropertyID firstPropertyID, ...);
	virtual int VixJob_CheckCompletion(IN int vmHandle, OUT bool *complete);
	virtual BOOL VixVM_GetResultBOOL(IN int vmHandle);
	virtual void VixVM_setResultBOOL(IN int vmHandle, IN BOOL bResult);
	virtual int VixVM_GetLastError(IN int iHandle, OUT wchar_t* pszError, IN size_t ccError);
	/// VIX is replaced by vSphere  -- end --


public:
	//////////////////////////////////////////////////////////////////////////
	// for vcloudmanager
	//////////////////////////////////////////////////////////////////////////
	virtual int connectVCloud(const wchar_t* vcloudDirectorServerName, const wchar_t* username, const wchar_t* password, const wchar_t* protocol = L"https", const int port = 443, const bool ignoreCert = true);
    static int connectVCloud_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual void disconnectVCloud();
    static int disconnectVCloud_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int saveVAppInfo(const wchar_t* vAppId, const wchar_t* vAppInfoFilePath);
    static int saveVAppInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //!!!!! caution!!, //unfinished, done, need to check
	virtual int getVMListOfVApp(const wchar_t* vAppId, VCloudVM_Info** vmList, int* vmCount);
    static int getVMListOfVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //!!!unfinished, done, need to check
	virtual VCloud_CreateVAppResult* createVApp(const VCloud_CreateVAppParams* createVAppParams);
    static int createVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //!!!unfinished, done, need to check
	virtual VCloud_ImportVMResult* importVM(const VCloud_ImportVMParams* importVMParams);
    static int importVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	// delete vApp and children VMs
	virtual int deleteVApp(const wchar_t* vAppId);
    static int deleteVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);


	// rename vApp and children VMs
	// append: true - append suffix, false - remove suffix; 
	// renameVM: true - rename vApp and VMs, false - rename vApp only;
	virtual int renameVAppEx(const wchar_t* vAppId, const wchar_t* suffix, const bool append, const bool renameVM);
    static int renameVAppEx_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //FengWei checked
	virtual int getVApp(const wchar_t* vAppId, VCloudVApp_Info* vAppInfo);
    static int getVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //done, need to check
	virtual int getVAppListOfVDC(const wchar_t* vdcId, VCloudVApp_Info** vAppList, int* count);
    static int getVAppListOfVDC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //FengWei checked
	virtual int getVAppInOrg(const wchar_t* vdcId, const wchar_t* vAppName, VCloudVApp_Info* vAppInfo);
    static int getVAppInOrg_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int powerOnVApp(const wchar_t* vAppId);
    static int powerOnVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

	virtual int powerOffVApp(const wchar_t* vAppId);
    static int powerOffVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //done, need to check
	virtual int getDatastore(const wchar_t* datastoreId, VCloudDatastore_Info* datastoreInfo);
    static int getDatastore_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //done, need to check
	virtual int getESXHostListOfVDC(const wchar_t* vdcId, VCloudESXHost_Info** esxHostList, int* count);
    static int getESXHostListOfVDC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //done, need to check
	virtual int getStorageProfileListOfVDC(const wchar_t* vdcId, VCloud_StorageProfile** storageProfileList, int* count);
    static int getStorageProfileListOfVDC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);

    //done, need to check
	virtual int verifyVCloudInfo(VCloud_VerifyInfo* vCloudInfo);//VCloud_VerifyInfo* is input
    static int verifyVCloudInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);


private:
	CComPtr<ICNativeCmdCtrl> m_autoNativeRPCCtrl;
	BOOL m_bCoInitialized;

	//static V2PNFlog m_log;

private:
	ESXNode* m_pesxDetails;
	VM_BasicInfo* m_pvmList;
    DataStore* m_pdataStoreList;
    Disk_Info* m_pdiskInfoList;
    Snapshot_Info* m_psnapshotInfoList;
    AdrDisk_Info* m_padrDiskInfoList;
    VM_Info* m_pvmInfoList;
    VMDataStoreInfo* m_pvmDataStoreInfoList;


    VCloud_CreateVAppResult* m_pvcCreateVAppResult;
    VCloud_ImportVMResult* m_pvcImportVMResult;

    wstring m_wstrReturn;

	//for com call
public:
	HRESULT RPC_Invoke(IN unsigned long inputCmd, IN const CString& inputData = L"", OUT unsigned long* outputValue = NULL, OUT CString* outputData = NULL);
	static INT RPC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData);
//private:
//    CRITICAL_SECTION m_cs;
//    TASKPARAMTER_S m_taskParam;
};

