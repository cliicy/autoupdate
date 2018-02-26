// All files within this DLL are compiled with the V2PNF_EXPORTS
// symbol defined on the command line. this symbol should not be defined on any project
// that uses this DLL. 
#pragma once 
#include <Windows.h>
#include "iostream"
#include "tchar.h"
#include "wchar.h"
#include "io.h"
#include <vector>
#include <string>
#include "Virtualization/VMDKIo.h"
//#include "VM_API_Demo.h"
using namespace std;

#define SKIP_VDS_DEVICE_CONSTANT  0x00000001
#define SKIP_INDEPENDENT_DEVICE_CONSTANT  0x00000002
#define SKIP_PHYSICAL_RDM_DEVICE_CONSTANT  0x00000004
#define RESTORE_VIRTUAL_RDM_DEVICE_DISK_CONSTANT  0x00000008
#define RESTORE_VIRTUAL_NETWORK_IS_NOT_AVAILABLE  0x00000010
#define RESTORE_VFLASH_RESOURCE_NOT_SUFFICIENT    0x00000020
#define RESTORE_VFLASH_CACHE_CLEARED              0x00000040
#define RESTORE_VIRTUAL_DISK_TYPE_THICK_NOT_SUPPORTED  0x00000080
#define DISK_THIN 0x00000001
#define DISK_THICK 0x00000002
	

#if defined (__cplusplus)
extern "C"
{
#endif // __cplusplus

#ifdef V2PNF_EXPORTS
#define V2PNF_API __declspec(dllexport)
#else
#define V2PNF_API __declspec(dllimport)
#endif

#define V2PNF_HANDLE    void*
#define PATH_SIZE		1024
#define NAME_SIZE       256
#define DISKURL_SIZE    1024
#define ERROR_STRING_SIZE 1024
#define MAX_JVM_BUFFER 16383
#define VCB_MAX_HOST_NAME 256
#define MSG_SIZE  8192

struct VC_ESX_CREDENTIALS
{
	wchar_t ServerName[VCB_MAX_HOST_NAME];
	wchar_t Username[VCB_MAX_HOST_NAME];
	wchar_t Password[VCB_MAX_HOST_NAME];
	long  VIport;
	wchar_t Protocol[VCB_MAX_HOST_NAME];
	bool ignoreCertificate;
};
struct ESXNode
{
	WCHAR esxName[NAME_SIZE];	
	WCHAR dcName[NAME_SIZE];
	WCHAR clusterName[NAME_SIZE]; //<huvfe01>2015-6-9 Bug#415647 identify the cluster, empty name means the the esx is not in a cluster
};

struct DataStore
{
	WCHAR esxName[NAME_SIZE];	
	WCHAR dcName[NAME_SIZE];
	WCHAR dataStore[NAME_SIZE];
};

struct VM_Info
{
	WCHAR vmName[NAME_SIZE];	
	WCHAR vmUUID[NAME_SIZE];
	WCHAR vmHost[NAME_SIZE];
	WCHAR vmVMX[NAME_SIZE];
	WCHAR vmESXHost[NAME_SIZE];
	WCHAR vmInstUUID[NAME_SIZE];
	WCHAR vmGuestOS[NAME_SIZE];
	bool powerState;
	WCHAR vmIP[NAME_SIZE];
	WCHAR vmresPool[NAME_SIZE];
	DWORD dwVMMemInMB;//<huvfe01>2-14-7-14
	WCHAR vmMoRef[NAME_SIZE];
	WCHAR vmClusterName[NAME_SIZE]; //<huvfe01>2015-6-9 Bug#415647 identify the cluster, empty name means the the vm is not in a cluster
};

struct VM_BasicInfo
{
	WCHAR vmName[NAME_SIZE];	
	WCHAR vmInstUUID[NAME_SIZE];
	bool isVCM;
	bool isIVM;
};

struct Snapshot_Info
{
	WCHAR snapURL[DISKURL_SIZE];
	WCHAR snapshotName[NAME_SIZE];
};

struct Disk_Info
{
	WCHAR vmDisk[DISKURL_SIZE];
	WCHAR chageID[NAME_SIZE];
	WCHAR diskMode[NAME_SIZE];
	WCHAR diskCompMode[NAME_SIZE];
	long  deviceKey;
	long long sizeinKB; //<sonmi01>2013-12-24 #88769: HBBU backup with >2TB disk convert to ESX failed.
	WCHAR datastoreType[NAME_SIZE];
	int diskType;
	int diskProvisioning; // 0: Thick Provision Lazy Zeroed/ 2: Thick Provision Eager Zeroed/ 1: Thin Provision
	bool bIsIDEDisk;
	bool bIsSATADisk;
	bool bUseSWSnapshot;
	bool bUseNFSClientToBackup;
};

struct AdrDisk_Info
{
	WCHAR vmDisk[DISKURL_SIZE];
	WCHAR diskUnitNumber[NAME_SIZE];
	long long  diskSize; //<sonmi01>2013-12-24 #88769: HBBU backup with >2TB disk convert to ESX failed.
	WCHAR diskType[NAME_SIZE];
	WCHAR diskBusDesc[NAME_SIZE];
	ULONG diskControllerId;
	ULONG diskPosition;
	GUID  diskPage83Id;
};

struct error_Info
{
	WCHAR erroString[ERROR_STRING_SIZE];
	int errorCode;
	long messageCode;
};

//<sonmi01>2013-6-5 #vds support
struct VMNetworkAdapter_Info
{
	WCHAR m_label[NAME_SIZE];				// VMware: VM network adapter name                            //Hyper-V: Legacy or Not
	WCHAR m_deviceName[NAME_SIZE];			// VMware: if not null, it is standard virtual switch         //Hyper-V: ID
	WCHAR m_switchName[NAME_SIZE];			// VMware: if not null, it is distributed virtual switch      //Hyper-V: Not used
	WCHAR m_portgroupName[NAME_SIZE];		// VMware: port group name of distributed virtual switch      //Hyper-V: Not Used
	WCHAR m_switchUuid[NAME_SIZE];			// VMware: uuid of distributed virtual switch                 //Hyper-V: the switch uuid to be connected
	WCHAR m_portgroupKey[NAME_SIZE];		// VMware:port group key of distributed virtual switch        //Hyper-V: Not Used
};

//mutga01
struct VMDataStoreInfo
{
	WCHAR m_vendorName[NAME_SIZE];
	WCHAR m_wwnID[NAME_SIZE];
	WCHAR m_dataStoreName[NAME_SIZE];
	WCHAR m_hbaAdapterID[NAME_SIZE];
	WCHAR m_diskName[NAME_SIZE];
	WCHAR m_srvAddress[NAME_SIZE];
	WCHAR m_dsType[NAME_SIZE];
	WCHAR m_dsGUID[NAME_SIZE];
	int lunNumber = 0;
	bool isExternalStorage;
};

//modified by zhepa02 at 2015-04-28, add the strThumbprint
typedef DWORD (WINAPI *PFN_GetSignature)(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, UINT32 iPort, VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strSnapMoref, const wstring& strVMDKURL, wstring& strDskGUID, const wstring& strJobIDId, const wstring& strAfguid); 
//end

#define PATH_SIZE		1024
#define FILE_PATH 30

//Com server will invoke this function
////////////////////////////////////////////////////////////////////////////////////////////////////////////
V2PNF_API int V2PNativeFacadeRPC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const wstring& inputData, OUT unsigned long* outputValue, OUT wstring& outputData);

//<sonmi01>2015-9-6 ###???
V2PNF_API VOID V2PNFSet_isSharedJVM(BOOL isSharedJVM);
////////////////////////////////////////////////////////////////////////////////////////////////////////////

V2PNF_API V2PNF_HANDLE V2PNativeFacadeInit(const char* jarList = NULL, BOOL bOldVer=FALSE);
V2PNF_API V2PNF_HANDLE V2PNativeFacadeInit_RPC(const char* jarList = NULL, BOOL bOldVer = FALSE);
V2PNF_API void V2PNativeFacadeExit(V2PNF_HANDLE pHandle);
//V2PNF_API void V2PNativeFacadeExitDestroyJvm(V2PNF_HANDLE pHandle); //<sonmi01>2015-5-28 #retry backup on CBT failure
V2PNF_API int connectToESX(WCHAR* esxServer, WCHAR* esxUser, WCHAR* esxPwd, WCHAR* esxPro, bool bIgnoreCert, long lPort, V2PNF_HANDLE pHANDLE);
V2PNF_API int getVMServerType(V2PNF_HANDLE pHandle);
V2PNF_API int checkVMServerLicense(WCHAR* esxName, WCHAR* dcName, V2PNF_HANDLE pHandle);
V2PNF_API BOOL checkVMServerInMaintainenceMode(WCHAR* esxName, WCHAR* dcName, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* getESXVersion(WCHAR* esxName, WCHAR* dcName, V2PNF_HANDLE pHandle);
V2PNF_API int getESXNumberOfProcessors(IN WCHAR* esxName, OUT UINT& numberOfLogicalProcessors, OUT UINT& numberOfProcessors, IN V2PNF_HANDLE pHandle);
V2PNF_API void deleteCTKFiles(WCHAR* esxName, WCHAR* dcName, WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle);
V2PNF_API void disconnectESX(V2PNF_HANDLE pHANDLE);
V2PNF_API ESXNode* getEsxNodeList(int *count, V2PNF_HANDLE pHANDLE);
V2PNF_API DataStore* getESXHostDataStoreList(ESXNode esxNode, int *count, V2PNF_HANDLE pHANDLE);
V2PNF_API VM_BasicInfo* getVMList(ESXNode esxNode, int *count, V2PNF_HANDLE pHANDLE);
V2PNF_API VM_Info* getVMInfo(WCHAR* vmName, WCHAR* vmInstUUID, V2PNF_HANDLE pHandle);
V2PNF_API int getVMInfoUnderDataCenter(OUT VM_Info* pVMInfo, IN const WCHAR* dcName, IN const WCHAR* vmName, IN V2PNF_HANDLE pHandle);
V2PNF_API BOOL checkResPool(ESXNode esxNode, WCHAR* resPool, V2PNF_HANDLE pHANDLE);
V2PNF_API int checkDSBlockSize(ESXNode esxNode, WCHAR* datastoreName, V2PNF_HANDLE pHANDLE);
V2PNF_API WCHAR* takeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName, error_Info *info, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* takeSnapShotEx(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName, error_Info *info, bool bQuiesce, V2PNF_HANDLE pHandle);  //<huvfe01>2015/4/30 expose quiesce option
V2PNF_API BOOL setInstanceUUID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* newUUID, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* checkandtakeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName,V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* getVMMoref(WCHAR* vmName, WCHAR* vmUUID,V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* getVMVersion(WCHAR* vmName, WCHAR* vmUUID,V2PNF_HANDLE pHandle);
V2PNF_API BOOL revertSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL,V2PNF_HANDLE pHandle);
V2PNF_API BOOL removeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL,V2PNF_HANDLE pHandle);
V2PNF_API BOOL removeSnapShotAsync(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, V2PNF_HANDLE pHandle);
V2PNF_API BOOL removeSnapShotByName(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName,V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* getsnapshotCTF(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* getparentSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, V2PNF_HANDLE pHandle);
V2PNF_API Disk_Info* getVMDiskURLs(WCHAR* vmName, WCHAR* vmUUID, int *count, V2PNF_HANDLE pHandle);
V2PNF_API Snapshot_Info* getVMSnapshotList(WCHAR* vmName, WCHAR* vmUUID, int *count, V2PNF_HANDLE pHandle);
V2PNF_API Disk_Info* getSnapShotDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count, V2PNF_HANDLE pHandle);
V2PNF_API AdrDisk_Info* getSnapShotAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count, V2PNF_HANDLE pHandle);
V2PNF_API AdrDisk_Info* getVMAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, int *count, V2PNF_HANDLE pHandle);
V2PNF_API Disk_Info* generateDiskBitMapForSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count, V2PNF_HANDLE pHandle);
V2PNF_API BOOL getFile(WCHAR* vmName, WCHAR* vmUUID, WCHAR* fileName, WCHAR* localPath, V2PNF_HANDLE pHandle);
V2PNF_API BOOL getDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  diskURL, int deviceKey, V2PNF_HANDLE pHandle);
V2PNF_API void deleteDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR* snapshotURL, V2PNF_HANDLE pHandle);
V2PNF_API BOOL setFileStream(WCHAR* vmName, WCHAR* vmUUID, WCHAR* fileName, V2PNF_HANDLE pHandle);
V2PNF_API int readFileStream(BYTE* pBuff, int length, int* bytesRead , __int64 offSet,  V2PNF_HANDLE pHandle);
V2PNF_API BOOL powerOnVM(WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle);
V2PNF_API void powerOffVM(WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* getVmdkFilePath(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, DWORD dwDiskSignaure,V2PNF_HANDLE pHandle);
V2PNF_API int getVMPowerState(WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle);
V2PNF_API int getVMToolsState(WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle);
V2PNF_API BOOL removeAllSnapshots(WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle);
V2PNF_API V2PNF_HANDLE V2PNativeFacadeInit_Ex(void* pJVM, const char* jarList = NULL, BOOL bOldVer=FALSE);
V2PNF_API void* getJVM(V2PNF_HANDLE pHandle);
V2PNF_API BOOL hasSufficientPermission(V2PNF_HANDLE pHandle); //<sonmi01>2014-1-9 #87330: With upgrade to VDDK 5.5, we need to Check permissions when importing VM from VC
//mutga01: HW Snapshot support
V2PNF_API VMDataStoreInfo* getVMDataStoreDetails(ESXNode nodeDetails, WCHAR* dsName, V2PNF_HANDLE pHandle);
V2PNF_API void rescanallHBA(ESXNode nodeDetails, BOOL rescanVC, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* addCloneDataStore(ESXNode nodeDetails, WCHAR* dsGUID, V2PNF_HANDLE pHandle);
V2PNF_API BOOL destroyandDeleteClone(ESXNode nodeDetails, WCHAR* dsName, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* createApplianceVM(ESXNode nodeDetails, WCHAR* vmName, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* createStndAloneApplianceVM(ESXNode nodeDetails, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* attachDiskToVM(WCHAR* vmName, WCHAR* diskURL, WCHAR* esxName, WCHAR* diskType, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* detachDiskFromVM(WCHAR* vmName, WCHAR* diskURL, V2PNF_HANDLE pHandle);
V2PNF_API BOOL isESXinCluster(ESXNode nodeDetails, V2PNF_HANDLE pHandle);
V2PNF_API BOOL isESXunderVC(ESXNode nodeDetails, V2PNF_HANDLE pHandle);
V2PNF_API BOOL deleteApplianceVM(ESXNode nodeDetails, WCHAR* vmName, V2PNF_HANDLE pHandle);
V2PNF_API WCHAR* checkandtakeApplianceSnapShot(WCHAR* vmName, WCHAR* snapshotName, V2PNF_HANDLE pHandle);
V2PNF_API BOOL removeSnapshotFromAppliance(WCHAR* vmName, WCHAR*  snapRef, V2PNF_HANDLE pHandle);
//APIs for vSphere integration start.
//Return 1 for success and non-one on error.
V2PNF_API int VM_EnableChangeTracking(VM_Info* vm_info,V2PNF_HANDLE pHandle);
//Return 1 for success and non-one on error.
V2PNF_API int VM_DisableChangeTracking(VM_Info* vm_info,V2PNF_HANDLE pHandle);
//Return 1 for success and non-one on error.
V2PNF_API int VM_EnableDiskUUID(VM_Info* vm_info,V2PNF_HANDLE pHandle);
V2PNF_API error_Info VM_TakeSnapshot(VM_Info* vm_info, wchar_t* snapshotName, wchar_t* snapshotId,Disk_Info** ppdiskInfo, int *count,V2PNF_HANDLE pHandle);

//<huvfe01>2015/4/30 expose quiesce option
V2PNF_API error_Info VM_TakeSnapshotEx(VM_Info* vm_info, wchar_t* snapshotName, wchar_t* snapshotId,Disk_Info** ppdiskInfo, int *count, bool bQuiesce, V2PNF_HANDLE pHandle);

V2PNF_API BOOL VM_RemoveSnapshot(VM_Info* vm_info, 
					wchar_t* snapshotId, V2PNF_HANDLE pHandle);
V2PNF_API BOOL VM_RemoveSnapshotByName(VM_Info* vm_info, 
	wchar_t* snapshotName, V2PNF_HANDLE pHandle);
V2PNF_API BOOL VM_RevertSnapshot(VM_Info* vm_info,
					wchar_t* snapshotId, V2PNF_HANDLE pHandle);

//Return 1 for success and non-one on error.
V2PNF_API int VM_GetDiskBitmap(VM_Info* pvm_info,
                     Disk_Info* pDiskDetails,
                     wchar_t* snapshotId,
					 long sectorSize,
                     PLARGE_INTEGER pbitmapSize,
					 PLARGE_INTEGER pUsedSectorCount,
                     wchar_t* filePath,
					 V2PNF_HANDLE pHandle);
//Return 1 for success and non-one on error.
V2PNF_API int VM_GetDiskChangesBitmap(VM_Info* pvm_info,
                     Disk_Info* pDiskDetails,
                     wchar_t* snapshotId,
					 long sectorSize,
                     PLARGE_INTEGER pbitmapSize,
					 PLARGE_INTEGER pUsedSectorCount,
                     wchar_t* filePath,
					 V2PNF_HANDLE pHandle);

//Return 1 for success and non-one on error.
V2PNF_API int VM_GetVMXSpec(VM_Info* vm_info, 
					wchar_t* vmxSpec, V2PNF_HANDLE pHandle);

//Return 1 for success and non-one on error.
V2PNF_API int VM_GetVSSwriterfiles(VM_Info* vm_info,
								   wchar_t* snapshotId,
								   wchar_t* pathToSave,
								   V2PNF_HANDLE pHandle);

//Return filename for success and null on error.
V2PNF_API WCHAR* VM_GetVMNVRAMFile(VM_Info* vm_info,
								   wchar_t* pathToSave,
								   V2PNF_HANDLE pHandle);

//Return 1 for success and 0 on error.
V2PNF_API int VM_SetVMnvramfiles(VM_Info* vm_info,
								   wchar_t* nvRamFile,
								   V2PNF_HANDLE pHandle);

//Return 1 for success and non-one on error.
V2PNF_API int VM_GetSnapshotConfigInfo(VM_Info* vm_info,
									   wchar_t* snapshotId,
									   wchar_t* pathToSave,
									   V2PNF_HANDLE pHandle);
//Return 1 for success and non-one on error.
V2PNF_API error_Info VM_Create(wchar_t* configFilePath,
						wchar_t* vcName,
						wchar_t* esxHost,
						wchar_t* esxDC,
						wchar_t* vmresPool,
						wchar_t* vmNewName,
						wchar_t* datastoreOfVM,						
						int numDisksToCreate,
						wchar_t** diskUrlList, 
						wchar_t** diskDatastoreList,
						BOOL overwriteVM,
						BOOL recoverToOriginal,
						VM_Info* pVMInfo, //out
						V2PNF_HANDLE pHandle);
//Return 1 for success and non-one on error.
V2PNF_API int VM_GetVMCurrentDiskURLs(VM_Info* vm_info, 
									  Disk_Info** ppdiskInfo,
									  int *count,
									  V2PNF_HANDLE pHandle);	

V2PNF_API BOOL VM_PowerOn(VM_Info* vm_info,V2PNF_HANDLE pHandle);
V2PNF_API void VM_PowerOff(VM_Info* vm_info,V2PNF_HANDLE pHandle);
//Return 1 for success and non-one on error.
V2PNF_API int VM_Delete(VM_Info* vm_info,V2PNF_HANDLE pHandle);
V2PNF_API int VM_Rename(VM_Info* vm_info,wchar_t* vmNewname,V2PNF_HANDLE pHandle);
//Return 1 for success and non-one on error. Return 2 if VM has snapshot.
V2PNF_API int VM_CheckAndEnableChangeTracking(VM_Info* vm_info,V2PNF_HANDLE pHandle);
V2PNF_API int VM_GetPowerState(VM_Info* vm_info,V2PNF_HANDLE pHandle);
V2PNF_API int VM_GetToolsState(VM_Info* vm_info,V2PNF_HANDLE pHandle);
V2PNF_API int VM_IsVMNameUsed(wchar_t* vmname,V2PNF_HANDLE pHandle);
V2PNF_API void logUserEvent(WCHAR* vmName, WCHAR* vmUUID, WCHAR* eventMessage, V2PNF_HANDLE pHandle);
V2PNF_API BOOL VM_HasSnapshot(VM_Info* vm_info,V2PNF_HANDLE pHandle);

V2PNF_API int SetvDSNetworkInfoEx(CONST VMNetworkAdapter_Info * pVMNetworkAdapter_Info, LONG Count, V2PNF_HANDLE pHandle); //<sonmi01>2013-6-5 #vds support

V2PNF_API INT64 GetESXVFlashResource(wchar_t* esxHost, wchar_t* esxDC, V2PNF_HANDLE pHandle); 
V2PNF_API INT64 GetVMFlashReadCache(wchar_t* configFilePath, V2PNF_HANDLE pHandle); 
V2PNF_API int cleanupHotAddedDisksAndConsolidateSnapshot(const V2PNF_HANDLE pHandle, const wstring& esxHost, const vector<wstring>& proxyVMIPList, const vector<wstring>& proxyHotAddedDiskURLs, const wstring& protectedVMInstanceUUID);
V2PNF_API VM_Info* getVMInfoByMoId(V2PNF_HANDLE pHandle, WCHAR* vmMoId);
V2PNF_API int SetVMDiskInfo(CONST Disk_Info* pVMDiskInfo, LONG Count, V2PNF_HANDLE pHandle); 
V2PNF_API int getESXHostListByDatastoreMoRef(const wchar_t* datastoreMoRef, vector<wstring>& esxHostList, V2PNF_HANDLE pHandle);
V2PNF_API int setVMCpuMemory(int numCPU, int numCoresPerSocket, long memoryMB, V2PNF_HANDLE pHandle);
V2PNF_API wstring getThumbprint(V2PNF_HANDLE pHandle);

V2PNF_API Disk_Info* getSnapShotDiskInfoByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count, V2PNF_HANDLE pHandle);
V2PNF_API INT64 GetVMFlashReadCache(wchar_t* configFilePath, V2PNF_HANDLE pHandle);
V2PNF_API int consolidateVMDisks(const wchar_t* vmUuid, V2PNF_HANDLE pHandle); 

//////////////////////////////////////////////////////////////////////////
// for vcloudmanager
//////////////////////////////////////////////////////////////////////////

struct VCloudVM_Info
{
	WCHAR name[NAME_SIZE];
	WCHAR id[NAME_SIZE];
	WCHAR moRef[NAME_SIZE];
	WCHAR vCenter[NAME_SIZE];
	WCHAR vCenterId[NAME_SIZE];	
	WCHAR storageProfile[NAME_SIZE];
	WCHAR storageProfileId[NAME_SIZE];
	WCHAR datastore[NAME_SIZE];		    
	WCHAR datastoreId[NAME_SIZE];
	WCHAR datastoreMoRef[NAME_SIZE];
	WCHAR esxHost[NAME_SIZE];			
	WCHAR esxHostId[NAME_SIZE];
	WCHAR esxHostMoRef[NAME_SIZE];
};

struct VCloudVApp_Info
{
	WCHAR name[NAME_SIZE];
	WCHAR id[NAME_SIZE];
	int status;
	WCHAR vCenter[NAME_SIZE];
	WCHAR vCenterId[NAME_SIZE];
	int vmHighestHWVersion;
	WCHAR vdcId[NAME_SIZE];
};

struct VCloud_CreateVAppParams
{
	WCHAR vAppInfoFilePath[PATH_SIZE];           			// full path of the vAppMetadata.xml file
	WCHAR vAppName[NAME_SIZE];								// vApp name
	WCHAR vdcId[NAME_SIZE];                      			// target VDC id
	WCHAR* pVAppConfigXML;                                  // XML string in VAppSessionInfo format, generated by UI
};

struct VCloud_CreateVAppResult
{
	int errorCode;
	WCHAR erroMessage[ERROR_STRING_SIZE];
	VCloudVApp_Info vAppInfo;
};

struct VCloud_ImportVMParams
{
	WCHAR vCenterId[NAME_SIZE];
	WCHAR vAppId[NAME_SIZE];
	WCHAR vdcStorageProfileId[NAME_SIZE];
	WCHAR vmName[NAME_SIZE];
	WCHAR vmMoRef[NAME_SIZE];
	WCHAR vAppInfoFilePath[PATH_SIZE];           			// full path of the vAppMetadata.xml file
	WCHAR* pVAppConfigXML;                                  // XML string in VAppSessionInfo format, generated by UI
};

struct VCloud_ImportVMResult
{
	int errorCode;
	WCHAR erroMessage[ERROR_STRING_SIZE];
	VCloudVM_Info vmInfo;
};

struct VCloudDatastore_Info
{
	WCHAR name[NAME_SIZE];
	WCHAR id[NAME_SIZE];
	WCHAR moRef[NAME_SIZE];
};

struct VCloudESXHost_Info
{
	WCHAR name[NAME_SIZE];
	WCHAR id[NAME_SIZE];
	WCHAR moRef[NAME_SIZE];
	WCHAR vCenter[NAME_SIZE];
	WCHAR vCenterId[NAME_SIZE];
	WCHAR cpuType[NAME_SIZE];
	int numOfCpusPackages;
	int numOfCpusLogical;
	INT64 cpuTotal;
	INT64 memTotal;
	WCHAR hostOsName[NAME_SIZE];
	WCHAR hostOsVersion[NAME_SIZE];
};

struct VCloud_StorageProfile
{
	WCHAR name[NAME_SIZE];
	WCHAR id[NAME_SIZE];
	WCHAR moRef[NAME_SIZE];
};

struct VCloud_VerifyInfo
{
	WCHAR orgId[NAME_SIZE];
	WCHAR orgName[NAME_SIZE];
	WCHAR orgVdcId[NAME_SIZE];
	WCHAR orgVdcName[NAME_SIZE];
	WCHAR vAppId[NAME_SIZE];
	WCHAR vAppName[NAME_SIZE];
};

V2PNF_API int connectVCloud(const V2PNF_HANDLE pHandle, const wchar_t* vcloudDirectorServerName, const wchar_t* username, const wchar_t* password, const wchar_t* protocol = L"https", const int port = 443, const bool ignoreCert = true);
V2PNF_API void disconnectVCloud(const V2PNF_HANDLE pHandle);

V2PNF_API int saveVAppInfo(const V2PNF_HANDLE pHandle, const wchar_t* vAppId, const wchar_t* vAppInfoFilePath);
V2PNF_API int getVMListOfVApp(const V2PNF_HANDLE pHandle, const wchar_t* vAppId, VCloudVM_Info** vmList, int* vmCount);  // call "delete[] vmList;" to release the memory

V2PNF_API VCloud_CreateVAppResult* createVApp(const V2PNF_HANDLE pHandle, const VCloud_CreateVAppParams* createVAppParams);  // need to release memory of the result
V2PNF_API VCloud_ImportVMResult* importVM(const V2PNF_HANDLE pHandle, const VCloud_ImportVMParams* importVMParams);  // need to release memory of the result

V2PNF_API int deleteVApp(const V2PNF_HANDLE pHandle, const wchar_t* vAppId); // delete vApp and children VMs

// rename vApp and children VMs
// append: true - append suffix, false - remove suffix; 
// renameVM: true - rename vApp and VMs, false - rename vApp only;
V2PNF_API int renameVAppEx(const V2PNF_HANDLE pHandle, const wchar_t* vAppId, const wchar_t* suffix, const bool append, const bool renameVM);

V2PNF_API int getVApp(const V2PNF_HANDLE pHandle, const wchar_t* vAppId, VCloudVApp_Info* vAppInfo);
V2PNF_API int getVAppListOfVDC(const V2PNF_HANDLE pHandle, const wchar_t* vdcId, VCloudVApp_Info** vAppList, int* count);

// get the vApp info of the Org (which org contains the VDC)
V2PNF_API int getVAppInOrg(const V2PNF_HANDLE pHandle, const wchar_t* vdcId, wchar_t* vAppName, VCloudVApp_Info* vAppInfo);

V2PNF_API int powerOnVApp(const V2PNF_HANDLE pHandle, const wchar_t* vAppId); // powerOn vApp and children VMs
V2PNF_API int powerOffVApp(const V2PNF_HANDLE pHandle, const wchar_t* vAppId); // powerOn vApp and children VMs

V2PNF_API int getDatastore(const V2PNF_HANDLE pHandle, const wchar_t* datastoreId, VCloudDatastore_Info* datastoreInfo);

V2PNF_API int getESXHostListOfVDC(const V2PNF_HANDLE pHandle, const wchar_t* vdcId, VCloudESXHost_Info** esxHostList, int* count);
V2PNF_API int getStorageProfileListOfVDC(const V2PNF_HANDLE pHandle, const wchar_t* vdcId, VCloud_StorageProfile** storageProfileList, int* count);
V2PNF_API int verifyVCloudInfo(const V2PNF_HANDLE pHandle, VCloud_VerifyInfo* vCloudInfo);
// end of for vcloudmanager


/// VIX is replaced by vSphere  -- begin --
typedef UINT64 VixMgr_Error;
#define VixMgr_Error_CODE(err)   ((err) & 0xFFFF)
#define VixMgr_SUCCEEDED(err)    (VixMgr_Error_OK == (err))
#define VixMgr_FAILED(err)       (VixMgr_Error_OK != (err))
typedef int VixMgr_Handle;
enum {
   VixMgr_INVALID_HANDLE   = 0,
};
typedef int VixMgr_ErrorEventType;
enum {
   VixMgr_ErrorEVENTTYPE_JOB_COMPLETED          = 2,
   VixMgr_ErrorEVENTTYPE_JOB_PROGRESS           = 3,
   VixMgr_ErrorEVENTTYPE_FIND_ITEM              = 8,
   VixMgr_ErrorEVENTTYPE_CALLBACK_SIGNALLED     = 2,  // Deprecated - Use VixMgr_ErrorVENTTYPE_JOB_COMPLETED instead.
};
typedef void VixEventProc(VixMgr_Handle handle,
                          VixMgr_ErrorEventType eventType,
                          VixMgr_Handle moreEventInfo,
                          void *clientData);
typedef int VixMgr_PropertyID;
enum {
   VixMgr_PROPERTY_NONE                                  = 0,

   VixMgr_PROPERTY_META_DATA_CONTAINER                   = 2,

   VixMgr_PROPERTY_HOST_HOSTTYPE                         = 50,
   VixMgr_PROPERTY_HOST_API_VERSION                      = 51,

   VixMgr_PROPERTY_VM_NUM_VCPUS                          = 101,
   VixMgr_PROPERTY_VM_VMX_PATHNAME                       = 103, 
   VixMgr_PROPERTY_VM_VMTEAM_PATHNAME                    = 105, 
   VixMgr_PROPERTY_VM_MEMORY_SIZE                        = 106,
   VixMgr_PROPERTY_VM_READ_ONLY                          = 107,
   VixMgr_PROPERTY_VM_NAME                               = 108,
   VixMgr_PROPERTY_VM_GUESTOS                            = 109,
   VixMgr_PROPERTY_VM_IN_VMTEAM                          = 128,
   VixMgr_PROPERTY_VM_POWER_STATE                        = 129,
   VixMgr_PROPERTY_VM_TOOLS_STATE                        = 152,
   VixMgr_PROPERTY_VM_IS_RUNNING                         = 196,
   VixMgr_PROPERTY_VM_SUPPORTED_FEATURES                 = 197,
   VixMgr_PROPERTY_VM_IS_RECORDING                       = 236,
   VixMgr_PROPERTY_VM_IS_REPLAYING                       = 237,

   VixMgr_PROPERTY_JOB_RESULT_ERROR_CODE                 = 3000,
   VixMgr_PROPERTY_JOB_RESULT_VM_IN_GROUP                = 3001,
   VixMgr_PROPERTY_JOB_RESULT_USER_MESSAGE               = 3002,
   VixMgr_PROPERTY_JOB_RESULT_EXIT_CODE                  = 3004,
   VixMgr_PROPERTY_JOB_RESULT_COMMAND_OUTPUT             = 3005,
   VixMgr_PROPERTY_JOB_RESULT_HANDLE                     = 3010,
   VixMgr_PROPERTY_JOB_RESULT_GUEST_OBJECT_EXISTS        = 3011,
   VixMgr_PROPERTY_JOB_RESULT_GUEST_PROGRAM_ELAPSED_TIME = 3017,
   VixMgr_PROPERTY_JOB_RESULT_GUEST_PROGRAM_EXIT_CODE    = 3018,
   VixMgr_PROPERTY_JOB_RESULT_ITEM_NAME                  = 3035,
   VixMgr_PROPERTY_JOB_RESULT_FOUND_ITEM_DESCRIPTION     = 3036,
   VixMgr_PROPERTY_JOB_RESULT_SHARED_FOLDER_COUNT        = 3046,
   VixMgr_PROPERTY_JOB_RESULT_SHARED_FOLDER_HOST         = 3048,
   VixMgr_PROPERTY_JOB_RESULT_SHARED_FOLDER_FLAGS        = 3049,
   VixMgr_PROPERTY_JOB_RESULT_PROCESS_ID                 = 3051,
   VixMgr_PROPERTY_JOB_RESULT_PROCESS_OWNER              = 3052,
   VixMgr_PROPERTY_JOB_RESULT_PROCESS_COMMAND            = 3053,
   VixMgr_PROPERTY_JOB_RESULT_FILE_FLAGS                 = 3054,
   VixMgr_PROPERTY_JOB_RESULT_PROCESS_START_TIME         = 3055,
   VixMgr_PROPERTY_JOB_RESULT_VM_VARIABLE_STRING         = 3056,
   VixMgr_PROPERTY_JOB_RESULT_PROCESS_BEING_DEBUGGED     = 3057,
   VixMgr_PROPERTY_JOB_RESULT_SCREEN_IMAGE_SIZE          = 3058,
   VixMgr_PROPERTY_JOB_RESULT_SCREEN_IMAGE_DATA          = 3059,
   VixMgr_PROPERTY_JOB_RESULT_FILE_SIZE                  = 3061,
   VixMgr_PROPERTY_JOB_RESULT_FILE_MOD_TIME              = 3062,

   VixMgr_PROPERTY_FOUND_ITEM_LOCATION                   = 4010,

   VixMgr_PROPERTY_SNAPSHOT_DISPLAYNAME                  = 4200,   
   VixMgr_PROPERTY_SNAPSHOT_DESCRIPTION                  = 4201,
   VixMgr_PROPERTY_SNAPSHOT_POWERSTATE                   = 4205,
   VixMgr_PROPERTY_SNAPSHOT_IS_REPLAYABLE                = 4207,

   VixMgr_PROPERTY_GUEST_SHAREDFOLDERS_SHARES_PATH       = 4525,

   VixMgr_PROPERTY_VM_ENCRYPTION_PASSWORD                = 7001,
};
typedef int VixMgr_ServiceProvider;
enum {
   VixMgr_SERVICEPROVIDER_DEFAULT               = 1,
   VixMgr_SERVICEPROVIDER_VMWARE_SERVER         = 2,
   VixMgr_SERVICEPROVIDER_VMWARE_WORKSTATION    = 3,
   VixMgr_SERVICEPROVIDER_VMWARE_PLAYER         = 4,
   VixMgr_SERVICEPROVIDER_VMWARE_VI_SERVER      = 10,
};
typedef int VixMgr_HostOptions;
enum {
   VixMgr_HOSTOPTION_USE_EVENT_PUMP        = 0x0008,
};
typedef int VixMgr_FindItemType;
enum {
   VixMgr_FIND_RUNNING_VMS         = 1,
   VixMgr_FIND_REGISTERED_VMS      = 4,
};
typedef int VixMgr_RunProgramOptions;
enum {
	VixMgr_RUNPROGRAM_RETURN_IMMEDIATELY   = 0x0001,
	VixMgr_RUNPROGRAM_ACTIVATE_WINDOW      = 0x0002,
};
/// \fn const char * Vix_GetErrorText(VixMgr_Error err, const char *locale);
/// \brief not implemented, always returns empty string.
V2PNF_API const char * Vix_GetErrorText(VixMgr_Error err, const char *locale);

/// \fn const char * Vix_GetLastErrorText(IN int iSess)
/// \brief get last error message.
/// \param iSess session number that can be returnned by calling function VixHost_Connect() or VixVM_Open().
/// \param pszError copy error message to this buffer
/// \param ccError buffer size of pszError, in character
/// \return last error message.
V2PNF_API const char * Vix_GetLastErrorText(IN int iSess, OUT char* pszError, IN size_t ccError);


/// \fn int Vix_GetESXVersionVM(...)
/// \brief get ESX version that VM is running on it.
/// \param hHostHandle that handle is returned from calling function VixHost_Connect().
/// \param pszVMName. Encoded in UTF-8. For example: "wanzh02-w2k3"
/// \return: high 16bit is major version, low 16bit is minor version
V2PNF_API int Vix_GetESXVersionVM(IN VixMgr_Handle hHostHandle, IN const char* pszVMName);

/// \fn VixMgr_Handle VixVM_Open(...)
/// \brief prepare to do more operation on a special VM. No related function in vSphere SDK
/// \param hHostHandle that handle is returned from calling function VixHost_Connect().
/// \param pszVMFilePathName. Encoded in UTF-8. For example: "[Storage1] wanzh02-w2k3/wanzh02-w2k3.vmx"
/// \param fnCallbackProc callback function is provided by caller.
/// \param pvClientData client data that is the last parameter of callback function (fnCallbackProc).
/// \return: >0: success: handle; 0: failed, other: not defined.NOTE: it is not a real handle! It is return code only.
V2PNF_API VixMgr_Handle VixVM_Open(IN VixMgr_Handle hHostHandle, IN const char *pszVMFilePathName, IN VixEventProc *fnCallbackProc, IN void *pvClientData);

/// \fn void Vix_ReleaseHandle(VixMgr_Handle handle);
/// \brief release the handle that is returned from calling function VixHost_Connect() or below VixVM_xxx() that return type is VixMgr_Handle.
/// NOTE: it is an empty function. Nothing is done in the function body.
/// \param hHandle the handle that is returned from calling function VixHost_Connect() or below VixVM_xxx() that return type is VixMgr_Handle.
V2PNF_API void Vix_ReleaseHandle(VixMgr_Handle hHandle);

/// \fn VixMgr_Error Vix_GetProperties(VixMgr_Handle handle, VixMgr_PropertyID firstPropertyID, ...);
/// \brief get properties from last calling.
/// NOTE: only VixMgr_PROPERTY_JOB_RESULT_GUEST_OBJECT_EXISTS is real supported.
/// \return: always zero
V2PNF_API VixMgr_Error Vix_GetProperties(VixMgr_Handle handle, VixMgr_PropertyID firstPropertyID, ...);

/// \fn void Vix_FreeBuffer(void *p);
/// \brief release allocated memory from last calling.
V2PNF_API void Vix_FreeBuffer(void *p);

/// \fn VixMgr_Handle VixHost_Connect(...)
/// \brief create a connection to ESX/ESXi server
/// \param nAPIVer API version
/// \param nHostType host type VixMgr_SERVICEPROVIDER_xxx 1:default;2:VMWare server;3:VMWare workstation; 4:VMWare player; 10:VMWare VI server
/// \param pszHostName ESX/ESXi server name. Encoded in UTF-8.
/// \param nHostPort ESX/ESXi port number. If it is set to 0 (zero), default port (902) will be used. According SDK, port 443 (HTTPS) should be used.
/// \param pszUserName user on ESX/ESXi server. Encoded in UTF-8.
/// \param pszPassword password of user (pszUserName) on the ESX/ESXi server. Encoded in UTF-8.
/// \param nOptions
/// \param hPropertyListHandle
/// \param fnCallbackProc callback function is provided by caller.
/// \param pvClientData client data that is the last parameter of callback function (fnCallbackProc).
/// \return: >0: success: handle; 0: failed, other: not defined.
V2PNF_API VixMgr_Handle VixHost_Connect( IN int nAPIVer, IN VixMgr_ServiceProvider nHostType, IN const char *pszHostName, IN int nHostPort,
	IN const char *pszUserName, IN const char *pszPassword, IN VixMgr_HostOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData);

/// \fn void VixHost_Disconnect(VixMgr_Handle hostHandle);
/// \brief destory the connection that is created by function VixHost_Connect
/// \param hHostHandle that handle is returned from calling function VixHost_Connect().
V2PNF_API void VixHost_Disconnect(VixMgr_Handle hHostHandle);

/// \fn VixMgr_Handle VixHost_FindItems(...)
/// \brief get some items. NOTE: isn't implemented.
/// \return: always zero.
V2PNF_API VixMgr_Handle VixHost_FindItems(VixMgr_Handle hostHandle, VixMgr_FindItemType searchType, VixMgr_Handle searchCriteria, UINT32 timeout, VixEventProc *callbackProc, void *clientData);

/// \fn VixMgr_Handle VixVM_LoginInGuest( ... );
/// \brief log into guest. Then, more operation can be done in guest OS.
/// \param vmHandle handle from function VixVM_Open().
/// \param pszUserName user on VM. Encoded in UTF-8.
/// \param pszPassword password of user (pszUserName) on the VM. Encoded in UTF-8.
/// \param nOptions not used
/// \param fnCallbackProc not used
/// \param pvClientData not used
/// \return: >0: success; <=0: failed, error code
V2PNF_API VixMgr_Handle VixVM_LoginInGuest( IN VixMgr_Handle vmHandle, IN const char *pszUserName, IN const char *pszPassword, IN int nOptions, IN VixEventProc *fnCallbackProc, void *pvClientData );

/// \fn VixMgr_Handle VixVM_LogoutFromGuest(...)
/// \brief logoff from VM
/// \param vmHandle handle from function VixVM_Open().
/// \param callbackProc not used
/// \param clientData not used
/// \return always zero.
V2PNF_API VixMgr_Handle VixVM_LogoutFromGuest(IN VixMgr_Handle vmHandle, IN VixEventProc *callbackProc, IN void *clientData);

/// \fn VixMgr_Handle VixVM_RunProgramInGuest(...)
/// \brief launch a process in VM.
/// \param vmHandle handle from function VixVM_Open().
/// \param pszGuestProgramName program file in the VM. It must in local format, for example "C:\Tools\precommand.exe".
/// \param pszCommandLineArgs command line's input parameters
/// \param nOptions not used
/// \param hPropertyListHandle not used
/// \param fnCallbackProc not used
/// \param pvClientData not used
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Handle VixVM_RunProgramInGuest(IN VixMgr_Handle vmHandle, IN const char *pszGuestProgramName, IN const char *pszCommandLineArgs,
	IN VixMgr_RunProgramOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData);

/// \fn VixVM_CopyFileFromHostToGuest(
/// \brief upload a file from local file system to remote virtual machine.
/// \param vmHandle handle from function VixVM_Open().
/// \param hostPathName file path name in local machine. It must in local format without machine name as prefix.
/// \param guestPathName file path name in remote virtual machine. It must in local format without machine name as prefix.
/// \param options not used
/// \param propertyListHandle not used
/// \param callbackProc not used
/// \param clientData not used
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Handle VixVM_CopyFileFromHostToGuest(VixMgr_Handle vmHandle, const char *hostPathName, const char *guestPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData);

/// \fn VixMgr_Handle VixVM_CopyFileFromGuestToHost(...)
/// \brief download file from remote virtual machine to local machine.
/// \param vmHandle handle from function VixVM_Open().
/// \param guestPathName file path name in remote virtual machine. It must in local format without machine name as prefix.
/// \param hostPathName file path name in local machine. It must in local format without machine name as prefix.
/// \param options not used
/// \param propertyListHandle not used
/// \param callbackProc not used
/// \param clientData not used
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Handle VixVM_CopyFileFromGuestToHost(VixMgr_Handle vmHandle, const char *guestPathName, const char *hostPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData);

/// \fn VixMgr_Handle VixVM_DeleteFileInGuest(...)
/// \brief delete a file in remote virtual machine
/// \param vmHandle handle from function VixVM_Open().
/// \param guestPathName file path name in remote virtual machine. It must in local format without machine name as prefix.
/// \param callbackProc not used
/// \param clientData not used
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Handle VixVM_DeleteFileInGuest(VixMgr_Handle vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData);

/// \fn VixMgr_Handle VixVM_FileExistsInGuest(...)
/// \brief check file is in remote virtual machine.
/// \param vmHandle handle from function VixVM_Open().
/// \param guestPathName file path name in remote virtual machine. It must in local format without machine name as prefix.
/// \param callbackProc not used
/// \param clientData not used
/// \return 1: exist; other: file is not in remote virtual machine
V2PNF_API VixMgr_Handle VixVM_FileExistsInGuest(VixMgr_Handle vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData);

/// \fn VixMgr_Handle VixVM_ListDirectoryInGuest(...)
/// \brief get all directories' name from remote virtual machine.
/// \param vmHandle handle from function VixVM_Open().
/// \param pathName root directory path name in remote virtual machine. It must in local format without machine name as prefix.
/// \param options not used
/// \param callbackProc not used
/// \param clientData not used
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Handle VixVM_ListDirectoryInGuest(VixMgr_Handle vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData);

/// \fn VixMgr_Handle VixVM_CreateDirectoryInGuest(...)
/// \brief create a directory in remote virtual machine
/// \param vmHandle handle from function VixVM_Open().
/// \param pathName root directory name in remote virtual machine. It must in local format without machine name as prefix.
/// \param propertyListHandle not used
/// \param callbackProc not used
/// \param clientData not used
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Handle VixVM_CreateDirectoryInGuest(VixMgr_Handle vmHandle, const char *pathName, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData);

/// \fn VixMgr_Handle VixVM_DeleteDirectoryInGuest(...)
/// \brief delete a directory in remote virtual machine
/// \param vmHandle handle from function VixVM_Open().
/// \param pathName the directory path name in remote virtual machine. It must in local format without machine name as prefix.
/// \param options not used
/// \param callbackProc not used
/// \param clientData not used
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Handle VixVM_DeleteDirectoryInGuest(VixMgr_Handle vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData);

/// \fn VixMgr_Handle VixVM_DirectoryExistsInGuest(...)
/// \brief check the directory in the remote virtual machine or not.
/// \param vmHandle handle from function VixVM_Open().
/// \param pathName the directory path name in remote virtual machine. It must in local format without machine name as prefix.
/// \param callbackProc not used
/// \param clientData not used
/// \return 1: exist; other: file is not in remote virtual machine
V2PNF_API VixMgr_Handle VixVM_DirectoryExistsInGuest(VixMgr_Handle vmHandle, const char *pathName, VixEventProc *callbackProc, void *clientData);

/// \fn int VixJob_GetNumProperties(VixMgr_Handle vmHandle,int resultPropertyID);
/// \brief find properties count from last calling
/// \param vmHandle handle from function VixVM_Open().
/// \param resultPropertyID property ID
/// \return >=0: count; <0: error code
V2PNF_API int VixJob_GetNumProperties(VixMgr_Handle vmHandle,int resultPropertyID);

/// \fn VixMgr_Error  VixJob_GetNthProperties(VixMgr_Handle vmHandle,int index,int propertyID,...);
/// \brief get one or more property/properties from last enumrate function (VixVM_ListDirectoryInGuest or VixVM_ListProcessesInGuest).
/// \param vmHandle handle from function VixVM_Open().
/// \param index order number
/// \param propertyID property ID
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Error  VixJob_GetNthProperties(VixMgr_Handle vmHandle,int index,int propertyID,...);

/// \fn VixMgr_Handle VixVM_ListProcessesInGuest(...)
/// \brief get process list in remote virtual machine.
/// \param vmHandle handle from function VixVM_Open().
/// \param options not used
/// \param callbackProc not used
/// \param clientData not used
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Handle VixVM_ListProcessesInGuest(VixMgr_Handle vmHandle,int options,VixEventProc *callbackProc,void *clientData);

/// \fn VixMgr_Error VixJob_Wait( ... );
/// \brief wait for a signal. NOTE: implemented VixMgr_PROPERTY_VM_IS_RUNNING only for for VixVM_RunProgramInGuest().
/// \param vmHandle that can be returned by calling function VixVM_Open().
V2PNF_API VixMgr_Error VixJob_Wait(VixMgr_Handle vmHandle, VixMgr_PropertyID firstPropertyID, ...);

/// \fn VixMgr_Error VixJob_CheckCompletion(VixMgr_Handle vmHandle, bool *complete);
/// \brief check the process exits or not for function VixVM_RunProgramInGuest().
/// \param vmHandle handle from function VixVM_Open().
/// \param complete it is the result. true: exit, false: no.
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Error VixJob_CheckCompletion(VixMgr_Handle vmHandle, bool *complete);

/// \fn VixMgr_Error VixJob_GetError(VixMgr_Handle vmHandle);
/// \brief get last error code. NOTE: not implemented.
/// \param vmHandle handle from function VixVM_Open().
/// \return >=0: success; <0: error code
V2PNF_API VixMgr_Error VixJob_GetError(VixMgr_Handle vmHandle);
/// VIX is replaced by vSphere  -- end --
#if defined (__cplusplus)
}
#endif // __cplusplus