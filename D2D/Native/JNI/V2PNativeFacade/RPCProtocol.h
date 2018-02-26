#pragma once
#include <windows.h>
#include <string>
#include <atlbase.h>
#include <atlstr.h>

#include "..\..\Native\Virtualization\ASVCommonLib\PublicHeader\PublicGetCurrentModuleName.h"

#include "..\..\Native\Virtualization\ASVCommonLib\PublicHeader\PublicCodeBlock.h"
#include "..\..\Native\Virtualization\ASVCommonLib\PublicHeader\PublicDTraceLib.h"

#include "..\..\Native\Virtualization\ASVCommonLib\PublicHeader\PublicValueXmlElementConvert.h"
#include "..\..\Native\Virtualization\ASVCommonLib\PublicHeader\PublicSerializeFieldDefine.h"
#include "..\..\Native\Virtualization\ASVCommonLib\PublicHeader\PublicSimpleXml.h"
#include "..\..\Native\Virtualization\ASVCommonLib\PublicHeader\PublicFileDeviceInterface.h"
#include "..\..\Native\Virtualization\ASVCommonLib\PublicHeader\PublicParameterStreamDef.h"

#include "V2PNativeFacade.h"

using namespace std;

#define PTR_2_STR(psz)    (psz ? psz : L"")
#define bool_2_BOOL(bv)   (bv ? TRUE : FALSE)
#define BOOL_2_bool(Bv)   (Bv ? true : false)
#define ROOT_ITEM(x)      (L#x)
#define RPC_CMD(x)        RPC_CMD_##x

enum _RPC_CMD_
{
    RPC_CMD(connectToESX) = 100,
    RPC_CMD(getVMServerType),
    RPC_CMD(checkVMServerLicense),
    RPC_CMD(checkVMServerInMaintainenceMode),
    RPC_CMD(getESXVersion),
    RPC_CMD(getESXNumberOfProcessors),
    RPC_CMD(disconnectESX),
    RPC_CMD(getEsxNodeList),
    RPC_CMD(getESXHostDataStoreList),
    RPC_CMD(getVMList),
    RPC_CMD(checkResPool),
    RPC_CMD(setInstanceUUID),
    RPC_CMD(checkDSBlockSize),
    RPC_CMD(takeSnapShot),
    RPC_CMD(checkandtakeSnapShot),
    RPC_CMD(getVMMoref),
    RPC_CMD(getVMVersion),
    RPC_CMD(revertSnapShot),
    RPC_CMD(removeSnapShot),
    RPC_CMD(removeSnapShotAsync),
    RPC_CMD(removeSnapShotByName),
    RPC_CMD(getsnapshotCTF),
    RPC_CMD(getparentSnapshot),
    //2015-05-27
    RPC_CMD(getVMDiskURLs),
    RPC_CMD(getVMSnapshotList),
    RPC_CMD(getSnapShotDiskInfo),
    RPC_CMD(getSnapShotAdrDiskInfo),
    RPC_CMD(getVMAdrDiskInfo),
    RPC_CMD(getVMInfo),
    RPC_CMD(getVMInfoUnderDataCenter),//2015-06-15
    RPC_CMD(deleteCTKFiles),
    RPC_CMD(hasSufficientPermission),
    RPC_CMD(getVMDataStoreDetails),
    RPC_CMD(rescanallHBA),
    RPC_CMD(addCloneDataStore),
    RPC_CMD(destroyandDeleteClone),
    RPC_CMD(createApplianceVM),
    RPC_CMD(createStndAloneApplianceVM),
    RPC_CMD(deleteApplianceVM),
    RPC_CMD(attachDiskToVM),
    RPC_CMD(detachDiskFromVM),
    RPC_CMD(isESXinCluster),
    RPC_CMD(checkandtakeApplianceSnapShot),
    RPC_CMD(removeSnapshotFromAppliance),
    //2015-05-28
    RPC_CMD(createVMwareVirtualMachine),
    RPC_CMD(getSnapshotConfigInfo),
    RPC_CMD(getVSSwriterfiles),
    RPC_CMD(setVMNVRamFile),
    RPC_CMD(getVMNVRAMFile),
    RPC_CMD(getUsedDiskBlocks),
    RPC_CMD(enableChangeBlockTracking),
    RPC_CMD(generateDiskBitMapForSnapshot),
    RPC_CMD(checkAndEnableChangeBlockTracking),
    RPC_CMD(getFile),
    RPC_CMD(getDiskBitMap),
    RPC_CMD(deleteDiskBitMap),
    RPC_CMD(setFileStream),
    //RPC_CMD(readFileStream),//!!!
    RPC_CMD(powerOnVM),
    RPC_CMD(powerOffVM),
    RPC_CMD(getVMPowerState),
    RPC_CMD(getVMToolsState),
    RPC_CMD(getVmdkFilePath),
    RPC_CMD(removeAllSnapshots),
    RPC_CMD(deleteVM),
    RPC_CMD(renameVM),
    //2015-05-29
    RPC_CMD(revertSnapShotByID),
    RPC_CMD(removeSnapShotByID),
    RPC_CMD(getSnapShotDiskInfoByID),
    RPC_CMD(enableDiskUUIDForVM),
    RPC_CMD(logUserEvent),
    RPC_CMD(isVMNameUsed),
    RPC_CMD(VMHasSnapshot),
    RPC_CMD(SetvDSNetworkInfoEx),
    RPC_CMD(GetESXVFlashResource),
    RPC_CMD(GetVMVFlashReadCache),
    RPC_CMD(cleanupHotAddedDisksAndConsolidateSnapshot),
    RPC_CMD(getVMInfoByMoId),
    RPC_CMD(SetVMDiskInfo),////
    RPC_CMD(getESXHostListByDatastoreMoRef),/////
    RPC_CMD(setVMCpuMemory),
    RPC_CMD(getThumbprint),

    //2015-06-01
    RPC_CMD(connectVCloud),
    RPC_CMD(disconnectVCloud),
    RPC_CMD(saveVAppInfo),
    RPC_CMD(getVMListOfVApp),//
    RPC_CMD(createVApp),//
    RPC_CMD(importVM),//
    RPC_CMD(deleteVApp),
    //2015-06-02
    RPC_CMD(renameVAppEx),
    RPC_CMD(getVApp),
    RPC_CMD(getVAppListOfVDC),//
    RPC_CMD(getVAppInOrg),
    RPC_CMD(powerOnVApp),
    RPC_CMD(powerOffVApp),
    RPC_CMD(getDatastore),
    RPC_CMD(getESXHostListOfVDC),
    RPC_CMD(getStorageProfileListOfVDC),
    RPC_CMD(verifyVCloudInfo),
	RPC_CMD(consolidateVMDisks),
	RPC_CMD(isESXunderVC)
};

#define ASSERT_CMD(x, y) assert((x) == (y))

struct tagVM_BasicInfo_internal
{
	SERIALIZE_BEGIN_STRUCT(tagVM_BasicInfo_internal, tagVM_BasicInfo_internal);

	VOID __tagToInternal(IN const VM_BasicInfo& vmBasic)
	{
		vmName = PTR_2_STR(vmBasic.vmName);
		vmInstUUID = PTR_2_STR(vmBasic.vmInstUUID);
		isVCM = bool_2_BOOL(vmBasic.isVCM);
		isIVM = bool_2_BOOL(vmBasic.isIVM);
	}

	VOID __tagFromInternal(OUT VM_BasicInfo& vmBasic)
	{
		wcscpy_s(vmBasic.vmName, NAME_SIZE, vmName.c_str());
		wcscpy_s(vmBasic.vmInstUUID, NAME_SIZE, vmInstUUID.c_str());
		vmBasic.isIVM = BOOL_2_bool(isVCM);
		vmBasic.isIVM = BOOL_2_bool(isIVM);
	}

	wstring vmName;				SERIALIZE_BASIC(vmName);
	wstring vmInstUUID;			SERIALIZE_BASIC(vmInstUUID);
	BOOL isVCM;					SERIALIZE_BASIC(isVCM);
	BOOL isIVM;					SERIALIZE_BASIC(isIVM);

	SERIALIZE_END_STRUCT(tagVM_BasicInfo_internal, tagVM_BasicInfo_internal);
	STRUCT_XML_SERIALIZER;
};

struct tagESXNode_internal
{
	SERIALIZE_BEGIN_STRUCT(tagESXNode_internal, tagESXNode_internal);

	VOID __tagToInternal(IN const ESXNode& esxNode)
	{
		esxName = PTR_2_STR(esxNode.esxName);
		dcName = PTR_2_STR(esxNode.dcName);
        clusterName = PTR_2_STR(esxNode.clusterName);
	}

	VOID __tagFromInternal(OUT ESXNode& esxNode)
	{
		wcscpy_s(esxNode.esxName, NAME_SIZE, esxName.c_str());
		wcscpy_s(esxNode.dcName, NAME_SIZE, dcName.c_str());
        wcscpy_s(esxNode.clusterName, NAME_SIZE, clusterName.c_str());
	}

	wstring esxName;				SERIALIZE_BASIC(esxName);
	wstring dcName;					SERIALIZE_BASIC(dcName);
    wstring clusterName;            SERIALIZE_BASIC(clusterName);

	SERIALIZE_END_STRUCT(tagESXNode_internal, tagESXNode_internal);
	STRUCT_XML_SERIALIZER;
};

struct tagDataStore_internal
{
    SERIALIZE_BEGIN_STRUCT(tagDataStore_internal, tagDataStore_internal);

    VOID __tagToInternal(IN const DataStore& dataStoreInput)
    {
        esxName = PTR_2_STR(dataStoreInput.esxName);
        dcName = PTR_2_STR(dataStoreInput.dcName);
        dataStore = PTR_2_STR(dataStoreInput.dataStore);
    }

    VOID __tagFromInternal(OUT DataStore& dataStoreInput)
    {
        wcscpy_s(dataStoreInput.esxName, NAME_SIZE, esxName.c_str());
        wcscpy_s(dataStoreInput.dcName, NAME_SIZE, dcName.c_str());
        wcscpy_s(dataStoreInput.dataStore, NAME_SIZE, dataStore.c_str());
    }

    wstring esxName;                SERIALIZE_BASIC(esxName);
    wstring dcName;                 SERIALIZE_BASIC(dcName);
    wstring dataStore;              SERIALIZE_BASIC(dataStore);

    SERIALIZE_END_STRUCT(tagDataStore_internal, tagDataStore_internal);
    STRUCT_XML_SERIALIZER;
};

struct tagError_Info_internal
{
    SERIALIZE_BEGIN_STRUCT(tagError_Info_internal, tagError_Info_internal);

    VOID __tagToInternal(IN const error_Info& errorInfoInput)
    {

        errorString = PTR_2_STR(errorInfoInput.erroString);
        errorCode = errorInfoInput.errorCode;
        messageCode = errorInfoInput.messageCode;
    }

    VOID __tagFromInternal(OUT error_Info& errorInfoInput)
    {
        wcscpy_s(errorInfoInput.erroString, ERROR_STRING_SIZE, errorString.c_str());
        errorInfoInput.errorCode = errorCode;
        errorInfoInput.messageCode = messageCode;
    }

    wstring errorString;                SERIALIZE_BASIC(errorString);
    int errorCode;                      SERIALIZE_BASIC(errorCode);
    long messageCode;                   SERIALIZE_BASIC(messageCode);

    SERIALIZE_END_STRUCT(tagError_Info_internal, tagError_Info_internal);
    STRUCT_XML_SERIALIZER;
};

struct tagDisk_Info_internal
{
    SERIALIZE_BEGIN_STRUCT(tagDisk_Info_internal, tagDisk_Info_internal);

    VOID __tagToInternal(IN const Disk_Info& diskInfoInput)
    {
        vmDisk = PTR_2_STR(diskInfoInput.vmDisk);
        chageID = PTR_2_STR(diskInfoInput.chageID);
        diskMode = PTR_2_STR(diskInfoInput.diskMode);
        diskCompMode = PTR_2_STR(diskInfoInput.diskCompMode);
        deviceKey = diskInfoInput.deviceKey;
        sizeinKB = diskInfoInput.sizeinKB;
        datastoreType = PTR_2_STR(diskInfoInput.datastoreType);
        diskType = diskInfoInput.diskType;
        diskProvisioning = diskInfoInput.diskProvisioning;
        bIsIDEDisk = diskInfoInput.bIsIDEDisk;
        bIsSATADisk = diskInfoInput.bIsSATADisk;
        bUseSWSnapshot = diskInfoInput.bUseSWSnapshot;
    }

    VOID __tagFromInternal(OUT Disk_Info& diskInfoInput)
    {
        wcscpy_s(diskInfoInput.vmDisk, DISKURL_SIZE, vmDisk.c_str());
        wcscpy_s(diskInfoInput.chageID, NAME_SIZE, chageID.c_str());
        wcscpy_s(diskInfoInput.diskMode, NAME_SIZE, diskMode.c_str());
        wcscpy_s(diskInfoInput.diskCompMode, NAME_SIZE, diskCompMode.c_str());
        diskInfoInput.deviceKey = deviceKey;
        diskInfoInput.sizeinKB = sizeinKB;
        wcscpy_s(diskInfoInput.datastoreType, NAME_SIZE, datastoreType.c_str());
        diskInfoInput.diskType = diskType;
        diskInfoInput.diskProvisioning = diskProvisioning;
        diskInfoInput.bIsIDEDisk = bIsIDEDisk;
        diskInfoInput.bIsSATADisk = bIsSATADisk;
        diskInfoInput.bUseSWSnapshot = bUseSWSnapshot;
    }

    wstring vmDisk;                     SERIALIZE_BASIC(vmDisk);
    wstring chageID;                    SERIALIZE_BASIC(chageID);
    wstring diskMode;                   SERIALIZE_BASIC(diskMode);
    wstring diskCompMode;               SERIALIZE_BASIC(diskCompMode);
    long deviceKey;                     SERIALIZE_BASIC(deviceKey);
    long long sizeinKB;                 SERIALIZE_BASIC(sizeinKB);
    wstring datastoreType;              SERIALIZE_BASIC(datastoreType);
    int diskType;                       SERIALIZE_BASIC(diskType);
    int diskProvisioning;               SERIALIZE_BASIC(diskProvisioning);
    BOOL bIsIDEDisk;                    SERIALIZE_BASIC(bIsIDEDisk);
    BOOL bIsSATADisk;                   SERIALIZE_BASIC(bIsSATADisk);
    BOOL bUseSWSnapshot;                SERIALIZE_BASIC(bUseSWSnapshot);

    SERIALIZE_END_STRUCT(tagDisk_Info_internal, tagDisk_Info_internal);
    STRUCT_XML_SERIALIZER;
};

struct tagSnapshot_Info_internal
{
    SERIALIZE_BEGIN_STRUCT(tagSnapshot_Info_internal, tagSnapshot_Info_internal);

    VOID __tagToInternal(IN const Snapshot_Info& snapShotInfoInput)
    {
        snapURL = PTR_2_STR(snapShotInfoInput.snapURL);
        snapshotName = PTR_2_STR(snapShotInfoInput.snapshotName);
    }

    VOID __tagFromInternal(OUT Snapshot_Info& snapShotInfoInput)
    {
        wcscpy_s(snapShotInfoInput.snapURL, DISKURL_SIZE, snapURL.c_str());
        wcscpy_s(snapShotInfoInput.snapshotName, NAME_SIZE, snapshotName.c_str());
    }

    wstring snapURL;                        SERIALIZE_BASIC(snapURL);
    wstring snapshotName;                   SERIALIZE_BASIC(snapshotName);

    SERIALIZE_END_STRUCT(tagSnapshot_Info_internal, tagSnapshot_Info_internal);
    STRUCT_XML_SERIALIZER;
};

struct tagAdrDisk_Info_internal
{
    SERIALIZE_BEGIN_STRUCT(tagAdrDisk_Info_internal, tagAdrDisk_Info_internal);

    VOID __tagToInternal(IN const AdrDisk_Info& adrDiskInfoInput)
    {
        vmDisk = PTR_2_STR(adrDiskInfoInput.vmDisk);
        diskUnitNumber = PTR_2_STR(adrDiskInfoInput.diskUnitNumber);
        diskSize = adrDiskInfoInput.diskSize;
        diskType = PTR_2_STR(adrDiskInfoInput.diskType);
        diskBusDesc = PTR_2_STR(adrDiskInfoInput.diskBusDesc);
        diskControllerId = adrDiskInfoInput.diskControllerId;
        diskPosition = adrDiskInfoInput.diskPosition;
        memcpy_s(&diskPage83Id, sizeof(GUID), &adrDiskInfoInput.diskPage83Id, sizeof(GUID));
    }

    VOID __tagFromInternal(OUT AdrDisk_Info& adrDiskInfoInput)
    {
        wcscpy_s(adrDiskInfoInput.vmDisk, DISKURL_SIZE, vmDisk.c_str());
        wcscpy_s(adrDiskInfoInput.diskUnitNumber, NAME_SIZE, diskUnitNumber.c_str());
        adrDiskInfoInput.diskSize = diskSize;
        wcscpy_s(adrDiskInfoInput.diskType, NAME_SIZE, diskType.c_str());
        wcscpy_s(adrDiskInfoInput.diskBusDesc, NAME_SIZE, diskBusDesc.c_str());
        adrDiskInfoInput.diskControllerId = diskControllerId;
        adrDiskInfoInput.diskPosition = diskPosition;
        memcpy_s(&adrDiskInfoInput.diskPage83Id, sizeof(GUID), &diskPage83Id, sizeof(GUID));
    }

    wstring vmDisk;                             SERIALIZE_BASIC(vmDisk);
    wstring diskUnitNumber;                     SERIALIZE_BASIC(diskUnitNumber);
    long long diskSize;                         SERIALIZE_BASIC(diskSize);
    wstring diskType;                           SERIALIZE_BASIC(diskType);
    wstring diskBusDesc;                        SERIALIZE_BASIC(diskBusDesc);
    ULONG diskControllerId;                     SERIALIZE_BASIC(diskControllerId);
    ULONG diskPosition;                         SERIALIZE_BASIC(diskPosition);
    GUID  diskPage83Id;                         SERIALIZE_BASIC(diskPage83Id);

    SERIALIZE_END_STRUCT(tagAdrDisk_Info_internal, tagAdrDisk_Info_internal);
    STRUCT_XML_SERIALIZER;
};

struct tagVM_Info_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVM_Info_internal, tagVM_Info_internal);

    VOID __tagToInternal(IN const VM_Info& vmInfoInput)
    {
        vmName = PTR_2_STR(vmInfoInput.vmName);
        vmUUID = PTR_2_STR(vmInfoInput.vmUUID);
        vmHost = PTR_2_STR(vmInfoInput.vmHost);
        vmVMX = PTR_2_STR(vmInfoInput.vmVMX);
        vmESXHost = PTR_2_STR(vmInfoInput.vmESXHost);
        vmInstUUID = PTR_2_STR(vmInfoInput.vmInstUUID);
        vmGuestOS = PTR_2_STR(vmInfoInput.vmGuestOS);
        powerState = vmInfoInput.powerState;
        vmIP = PTR_2_STR(vmInfoInput.vmIP);
        vmresPool = PTR_2_STR(vmInfoInput.vmresPool);
        dwVMMemInMB = vmInfoInput.dwVMMemInMB;
        vmMoRef = PTR_2_STR(vmInfoInput.vmMoRef);
        vmClusterName = PTR_2_STR(vmInfoInput.vmClusterName);
    }

    VOID __tagFromInternal(OUT VM_Info& vmInfoInput)
    {
        wcscpy_s(vmInfoInput.vmName, NAME_SIZE, vmName.c_str());
        wcscpy_s(vmInfoInput.vmUUID, NAME_SIZE, vmUUID.c_str());
        wcscpy_s(vmInfoInput.vmHost, NAME_SIZE, vmHost.c_str());
        wcscpy_s(vmInfoInput.vmVMX, NAME_SIZE, vmVMX.c_str());
        wcscpy_s(vmInfoInput.vmESXHost, NAME_SIZE, vmESXHost.c_str());
        wcscpy_s(vmInfoInput.vmInstUUID, NAME_SIZE, vmInstUUID.c_str());
        wcscpy_s(vmInfoInput.vmGuestOS, NAME_SIZE, vmGuestOS.c_str());
        vmInfoInput.powerState = powerState;
        wcscpy_s(vmInfoInput.vmIP, NAME_SIZE, vmIP.c_str());
        wcscpy_s(vmInfoInput.vmresPool, NAME_SIZE, vmresPool.c_str());
        vmInfoInput.dwVMMemInMB = dwVMMemInMB;
        wcscpy_s(vmInfoInput.vmMoRef, NAME_SIZE, vmMoRef.c_str());
        wcscpy_s(vmInfoInput.vmClusterName, NAME_SIZE, vmClusterName.c_str());
    }

    wstring vmName;                             SERIALIZE_BASIC(vmName);
    wstring vmUUID;                             SERIALIZE_BASIC(vmUUID);
    wstring vmHost;                             SERIALIZE_BASIC(vmHost);
    wstring vmVMX;                              SERIALIZE_BASIC(vmVMX);
    wstring vmESXHost;                          SERIALIZE_BASIC(vmESXHost);
    wstring vmInstUUID;                         SERIALIZE_BASIC(vmInstUUID);
    wstring vmGuestOS;                          SERIALIZE_BASIC(vmGuestOS);
    BOOL powerState;                            SERIALIZE_BASIC(powerState);
    wstring vmIP;                               SERIALIZE_BASIC(vmIP);
    wstring vmresPool;                          SERIALIZE_BASIC(vmresPool);
    DWORD dwVMMemInMB;                          SERIALIZE_BASIC(dwVMMemInMB);
    wstring vmMoRef;                            SERIALIZE_BASIC(vmMoRef);
    wstring vmClusterName;                      SERIALIZE_BASIC(vmClusterName);


    
    SERIALIZE_END_STRUCT(tagVM_Info_internal, tagVM_Info_internal);
    STRUCT_XML_SERIALIZER;
};

struct tagVMDataStoreInfo_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVMDataStoreInfo_internal, tagVMDataStoreInfo_internal);

    VOID __tagToInternal(IN const VMDataStoreInfo& vmDataStoreInfoInput)
    {
        m_vendorName = PTR_2_STR(vmDataStoreInfoInput.m_vendorName);
        m_wwnID = PTR_2_STR(vmDataStoreInfoInput.m_wwnID);
        m_dataStoreName = PTR_2_STR(vmDataStoreInfoInput.m_dataStoreName);
        m_hbaAdapterID = PTR_2_STR(vmDataStoreInfoInput.m_hbaAdapterID);
        m_diskName = PTR_2_STR(vmDataStoreInfoInput.m_diskName);
        m_srvAddress = PTR_2_STR(vmDataStoreInfoInput.m_srvAddress);
        m_dsType = PTR_2_STR(vmDataStoreInfoInput.m_dsType);
        m_dsGUID = PTR_2_STR(vmDataStoreInfoInput.m_dsGUID);
        lunNumber = vmDataStoreInfoInput.lunNumber;
        isExternalStorage = vmDataStoreInfoInput.isExternalStorage;
    }

    VOID __tagFromInternal(OUT VMDataStoreInfo& vmDataStoreInfoInput)
    {
        wcscpy_s(vmDataStoreInfoInput.m_vendorName, NAME_SIZE, m_vendorName.c_str());
        wcscpy_s(vmDataStoreInfoInput.m_wwnID, NAME_SIZE, m_wwnID.c_str());
        wcscpy_s(vmDataStoreInfoInput.m_dataStoreName, NAME_SIZE, m_dataStoreName.c_str());
        wcscpy_s(vmDataStoreInfoInput.m_hbaAdapterID, NAME_SIZE, m_hbaAdapterID.c_str());
        wcscpy_s(vmDataStoreInfoInput.m_diskName, NAME_SIZE, m_diskName.c_str());
        wcscpy_s(vmDataStoreInfoInput.m_srvAddress, NAME_SIZE, m_srvAddress.c_str());
        wcscpy_s(vmDataStoreInfoInput.m_dsType, NAME_SIZE, m_dsType.c_str());
        wcscpy_s(vmDataStoreInfoInput.m_dsGUID, NAME_SIZE, m_dsGUID.c_str());
        vmDataStoreInfoInput.lunNumber = lunNumber;
        vmDataStoreInfoInput.isExternalStorage = isExternalStorage;
    }

    wstring m_vendorName;                               SERIALIZE_BASIC(m_vendorName);
    wstring m_wwnID;                                    SERIALIZE_BASIC(m_wwnID);
    wstring m_dataStoreName;                            SERIALIZE_BASIC(m_dataStoreName);
    wstring m_hbaAdapterID;                             SERIALIZE_BASIC(m_hbaAdapterID);
    wstring m_diskName;                                 SERIALIZE_BASIC(m_diskName);
    wstring m_srvAddress;                               SERIALIZE_BASIC(m_srvAddress);
    wstring m_dsType;                                   SERIALIZE_BASIC(m_dsType);
    wstring m_dsGUID;                                   SERIALIZE_BASIC(m_dsGUID);
    int lunNumber;                                      SERIALIZE_BASIC(lunNumber);
    BOOL isExternalStorage;                             SERIALIZE_BASIC(isExternalStorage);

    SERIALIZE_END_STRUCT(tagVMDataStoreInfo_internal, tagVMDataStoreInfo_internal);
    STRUCT_XML_SERIALIZER;
};

struct tagVMNetworkAdapter_Info_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVMNetworkAdapter_Info_internal, tagVMNetworkAdapter_Info_internal);

    VOID __tagToInternal(IN const VMNetworkAdapter_Info& vmNetworkAdapterInfo)
    {
        m_label = PTR_2_STR(vmNetworkAdapterInfo.m_label);
        m_deviceName = PTR_2_STR(vmNetworkAdapterInfo.m_deviceName);
        m_switchName = PTR_2_STR(vmNetworkAdapterInfo.m_switchName);
        m_portgroupName = PTR_2_STR(vmNetworkAdapterInfo.m_portgroupName);
        m_switchUuid = PTR_2_STR(vmNetworkAdapterInfo.m_switchUuid);
        m_portgroupKey = PTR_2_STR(vmNetworkAdapterInfo.m_portgroupKey);
    }

    VOID __tagFromInternal(OUT VMNetworkAdapter_Info& vmNetworkAdapterInfo)
    {
        wcscpy_s(vmNetworkAdapterInfo.m_label, NAME_SIZE, m_label.c_str());
        wcscpy_s(vmNetworkAdapterInfo.m_deviceName, NAME_SIZE, m_deviceName.c_str());
        wcscpy_s(vmNetworkAdapterInfo.m_switchName, NAME_SIZE, m_switchName.c_str());
        wcscpy_s(vmNetworkAdapterInfo.m_portgroupName, NAME_SIZE, m_portgroupName.c_str());
        wcscpy_s(vmNetworkAdapterInfo.m_switchUuid, NAME_SIZE, m_switchUuid.c_str());
        wcscpy_s(vmNetworkAdapterInfo.m_portgroupKey, NAME_SIZE, m_portgroupKey.c_str());
    }

    wstring m_label;                                 SERIALIZE_BASIC(m_label);
    wstring m_deviceName;                            SERIALIZE_BASIC(m_deviceName);
    wstring m_switchName;                            SERIALIZE_BASIC(m_switchName);
    wstring m_portgroupName;                         SERIALIZE_BASIC(m_portgroupName);
    wstring m_switchUuid;                            SERIALIZE_BASIC(m_switchUuid);
    wstring m_portgroupKey;                          SERIALIZE_BASIC(m_portgroupKey);

    SERIALIZE_END_STRUCT(tagVMNetworkAdapter_Info_internal, tagVMNetworkAdapter_Info_internal);
    STRUCT_XML_SERIALIZER;
};

/////////////////////////////////////
//for vCloud
/////////////////////////////////////
struct tagVCloudVM_Info_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVCloudVM_Info_internal, tagVCloudVM_Info_internal);

    VOID __tagToInternal(IN const VCloudVM_Info& vCloudVMInfo)
    {
        name = PTR_2_STR(vCloudVMInfo.name);
        id = PTR_2_STR(vCloudVMInfo.id);
        moRef = PTR_2_STR(vCloudVMInfo.moRef);
        vCenter = PTR_2_STR(vCloudVMInfo.vCenter);
        vCenterId = PTR_2_STR(vCloudVMInfo.vCenterId);
        storageProfile = PTR_2_STR(vCloudVMInfo.storageProfile);
        storageProfileId = PTR_2_STR(vCloudVMInfo.storageProfileId);
        datastore = PTR_2_STR(vCloudVMInfo.datastore);
        datastoreId = PTR_2_STR(vCloudVMInfo.datastoreId);
        datastoreMoRef = PTR_2_STR(vCloudVMInfo.datastoreMoRef);
        esxHost = PTR_2_STR(vCloudVMInfo.esxHost);
        esxHostId = PTR_2_STR(vCloudVMInfo.esxHostId);
        esxHostMoRef = PTR_2_STR(vCloudVMInfo.esxHostMoRef);
    }

    VOID __tagFromInternal(OUT VCloudVM_Info& vCloudVMInfo)
    {
        wcscpy_s(vCloudVMInfo.name, NAME_SIZE, name.c_str());
        wcscpy_s(vCloudVMInfo.id, NAME_SIZE, id.c_str());
        wcscpy_s(vCloudVMInfo.moRef, NAME_SIZE, moRef.c_str());
        wcscpy_s(vCloudVMInfo.vCenter, NAME_SIZE, vCenter.c_str());
        wcscpy_s(vCloudVMInfo.vCenterId, NAME_SIZE, vCenterId.c_str());
        wcscpy_s(vCloudVMInfo.storageProfile, NAME_SIZE, storageProfile.c_str());
        wcscpy_s(vCloudVMInfo.storageProfileId, NAME_SIZE, storageProfileId.c_str());
        wcscpy_s(vCloudVMInfo.datastore, NAME_SIZE, datastore.c_str());
        wcscpy_s(vCloudVMInfo.datastoreId, NAME_SIZE, datastoreId.c_str());
        wcscpy_s(vCloudVMInfo.datastoreMoRef, NAME_SIZE, datastoreMoRef.c_str());
        wcscpy_s(vCloudVMInfo.esxHost, NAME_SIZE, esxHost.c_str());
        wcscpy_s(vCloudVMInfo.esxHostId, NAME_SIZE, esxHostId.c_str());
        wcscpy_s(vCloudVMInfo.esxHostMoRef, NAME_SIZE, esxHostMoRef.c_str());
    }

    //WCHAR name[NAME_SIZE];
    //WCHAR id[NAME_SIZE];
    //WCHAR moRef[NAME_SIZE];
    //WCHAR vCenter[NAME_SIZE];
    //WCHAR vCenterId[NAME_SIZE];
    //WCHAR storageProfile[NAME_SIZE];
    //WCHAR storageProfileId[NAME_SIZE];
    //WCHAR datastore[NAME_SIZE];
    //WCHAR datastoreId[NAME_SIZE];
    //WCHAR datastoreMoRef[NAME_SIZE];
    //WCHAR esxHost[NAME_SIZE];
    //WCHAR esxHostId[NAME_SIZE];
    //WCHAR esxHostMoRef[NAME_SIZE];
    wstring name;                                   SERIALIZE_BASIC(name);
    wstring id;                                     SERIALIZE_BASIC(id);
    wstring moRef;                                  SERIALIZE_BASIC(moRef);
    wstring vCenter;                                SERIALIZE_BASIC(vCenter);
    wstring vCenterId;                              SERIALIZE_BASIC(vCenterId);
    wstring storageProfile;                         SERIALIZE_BASIC(storageProfile);
    wstring storageProfileId;                       SERIALIZE_BASIC(storageProfileId);
    wstring datastore;                              SERIALIZE_BASIC(datastore);
    wstring datastoreId;                            SERIALIZE_BASIC(datastoreId);
    wstring datastoreMoRef;                         SERIALIZE_BASIC(datastoreMoRef);
    wstring esxHost;                                SERIALIZE_BASIC(esxHost);
    wstring esxHostId;                              SERIALIZE_BASIC(esxHostId);
    wstring esxHostMoRef;                           SERIALIZE_BASIC(esxHostMoRef);

    SERIALIZE_END_STRUCT(tagVCloudVM_Info_internal, tagVCloudVM_Info_internal);
    STRUCT_XML_SERIALIZER;
};
//
struct tagVCloudVApp_Info_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVCloudVApp_Info_internal, tagVCloudVApp_Info_internal);

    VOID __tagToInternal(IN const VCloudVApp_Info& vCloudVAppInfo)
    {
        name = PTR_2_STR(vCloudVAppInfo.name);
        id = PTR_2_STR(vCloudVAppInfo.id);
        status = vCloudVAppInfo.status;
        vCenter = PTR_2_STR(vCloudVAppInfo.vCenter);
        vCenterId = PTR_2_STR(vCloudVAppInfo.vCenterId);
        vmHighestHWVersion = vCloudVAppInfo.vmHighestHWVersion;
        vdcId = PTR_2_STR(vCloudVAppInfo.vdcId);
    }

    VOID __tagFromInternal(OUT VCloudVApp_Info& vCloudVAppInfo)
    {
        wcscpy_s(vCloudVAppInfo.name, NAME_SIZE, name.c_str());
        wcscpy_s(vCloudVAppInfo.id, NAME_SIZE, id.c_str());
        vCloudVAppInfo.status = status;
        wcscpy_s(vCloudVAppInfo.vCenter, NAME_SIZE, vCenter.c_str());
        wcscpy_s(vCloudVAppInfo.vCenterId, NAME_SIZE, vCenterId.c_str());
        vCloudVAppInfo.vmHighestHWVersion = vmHighestHWVersion;
        wcscpy_s(vCloudVAppInfo.vdcId, NAME_SIZE, vdcId.c_str());
    }

    //WCHAR name[NAME_SIZE];
    //WCHAR id[NAME_SIZE];
    //int status;
    //WCHAR vCenter[NAME_SIZE];
    //WCHAR vCenterId[NAME_SIZE];
    //int vmHighestHWVersion;
    //WCHAR vdcId[NAME_SIZE];

    wstring name;                                    SERIALIZE_BASIC(name);
    wstring id;                                      SERIALIZE_BASIC(id);
    INT status;                                      SERIALIZE_BASIC(status);
    wstring vCenter;                                 SERIALIZE_BASIC(vCenter);
    wstring vCenterId;                               SERIALIZE_BASIC(vCenterId);
    INT vmHighestHWVersion;                          SERIALIZE_BASIC(vmHighestHWVersion);
    wstring vdcId;                                   SERIALIZE_BASIC(vdcId);

    SERIALIZE_END_STRUCT(tagVCloudVApp_Info_internal, tagVCloudVApp_Info_internal);
    STRUCT_XML_SERIALIZER;
};
//!!pointer size
struct tagVCloud_CreateVAppParams_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVCloud_CreateVAppParams_internal, tagVCloud_CreateVAppParams_internal);

    VOID __tagToInternal(IN const VCloud_CreateVAppParams& vCloudCreateVAppPara)
    {
        vAppInfoFilePath = PTR_2_STR(vCloudCreateVAppPara.vAppInfoFilePath);
        vAppName = PTR_2_STR(vCloudCreateVAppPara.vAppName);
        vdcId = PTR_2_STR(vCloudCreateVAppPara.vdcId);
        pVAppConfigXML = PTR_2_STR(vCloudCreateVAppPara.pVAppConfigXML);
    }

    VOID __tagFromInternal(OUT VCloud_CreateVAppParams& vCloudCreateVAppPara)
    {
        wcscpy_s(vCloudCreateVAppPara.vAppInfoFilePath, NAME_SIZE, vAppInfoFilePath.c_str());
        wcscpy_s(vCloudCreateVAppPara.vAppName, NAME_SIZE, vAppName.c_str());
        wcscpy_s(vCloudCreateVAppPara.vdcId, NAME_SIZE, vdcId.c_str());
        wcscpy_s(vCloudCreateVAppPara.pVAppConfigXML, NAME_SIZE, pVAppConfigXML.c_str());


    }

    //WCHAR vAppInfoFilePath[PATH_SIZE];           			// full path of the vAppMetadata.xml file
    //WCHAR vAppName[NAME_SIZE];								// vApp name
    //WCHAR vdcId[NAME_SIZE];                      			// target VDC id
    //WCHAR* pVAppConfigXML;                                  // XML string in VAppSessionInfo format, generated by UI
    wstring vAppInfoFilePath;                               SERIALIZE_BASIC(vAppInfoFilePath);
    wstring vAppName;                                       SERIALIZE_BASIC(vAppName);
    wstring vdcId;                                          SERIALIZE_BASIC(vdcId);
    wstring pVAppConfigXML;                                 SERIALIZE_BASIC(pVAppConfigXML);

    SERIALIZE_END_STRUCT(tagVCloud_CreateVAppParams_internal, tagVCloud_CreateVAppParams_internal);
    STRUCT_XML_SERIALIZER;
};
//unfinished
struct tagVCloud_CreateVAppResult_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVCloud_CreateVAppResult_internal, tagVCloud_CreateVAppResult_internal);

    VOID __tagToInternal(IN const VCloud_CreateVAppResult& vCloudCreateVAppResult)
    {
        errorCode = vCloudCreateVAppResult.errorCode;
        erroMessage = PTR_2_STR(vCloudCreateVAppResult.erroMessage);
        //vAppInfo.?? wcscpy_s?
        vAppInfo.__tagToInternal(vCloudCreateVAppResult.vAppInfo);
    }

    VOID __tagFromInternal(OUT VCloud_CreateVAppResult& vCloudCreateVAppResult)
    {
        vCloudCreateVAppResult.errorCode = errorCode;
        wcscpy_s(vCloudCreateVAppResult.erroMessage, ERROR_STRING_SIZE, erroMessage.c_str());
        vAppInfo.__tagFromInternal(vCloudCreateVAppResult.vAppInfo);
    }

    //int errorCode;
    //WCHAR erroMessage[ERROR_STRING_SIZE];
    //VCloudVApp_Info vAppInfo;

    INT errorCode;                                          SERIALIZE_BASIC(errorCode);
    wstring erroMessage;                                    SERIALIZE_BASIC(erroMessage);
    tagVCloudVApp_Info_internal vAppInfo;                   SERIALIZE_COMPLEX(vAppInfo);

    SERIALIZE_END_STRUCT(tagVCloud_CreateVAppResult_internal, tagVCloud_CreateVAppResult_internal);
    STRUCT_XML_SERIALIZER;
};
//!!pointer size
struct tagVCloud_ImportVMParams_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVCloud_ImportVMParams_internal, tagVCloud_ImportVMParams_internal);

    VOID __tagToInternal(IN const VCloud_ImportVMParams& vCloudImportVMPara)
    {
        vCenterId = PTR_2_STR(vCloudImportVMPara.vCenterId);
        vAppId = PTR_2_STR(vCloudImportVMPara.vAppId);
        vdcStorageProfileId = PTR_2_STR(vCloudImportVMPara.vdcStorageProfileId);
        vmName = PTR_2_STR(vCloudImportVMPara.vmName);
        vmMoRef = PTR_2_STR(vCloudImportVMPara.vmMoRef);
        vAppInfoFilePath = PTR_2_STR(vCloudImportVMPara.vAppInfoFilePath);
        pVAppConfigXML = PTR_2_STR(vCloudImportVMPara.pVAppConfigXML);

    }

    VOID __tagFromInternal(OUT VCloud_ImportVMParams& vCloudImportVMPara)
    {
        wcscpy_s(vCloudImportVMPara.vCenterId, NAME_SIZE, vCenterId.c_str());
        wcscpy_s(vCloudImportVMPara.vAppId, NAME_SIZE, vAppId.c_str());
        wcscpy_s(vCloudImportVMPara.vdcStorageProfileId, NAME_SIZE, vdcStorageProfileId.c_str());
        wcscpy_s(vCloudImportVMPara.vmName, NAME_SIZE, vmName.c_str());
        wcscpy_s(vCloudImportVMPara.vmMoRef, NAME_SIZE, vmMoRef.c_str());
        wcscpy_s(vCloudImportVMPara.vAppInfoFilePath, NAME_SIZE, vAppInfoFilePath.c_str());
        wcscpy_s(vCloudImportVMPara.pVAppConfigXML, NAME_SIZE, pVAppConfigXML.c_str());


    }

    //WCHAR vCenterId[NAME_SIZE];
    //WCHAR vAppId[NAME_SIZE];
    //WCHAR vdcStorageProfileId[NAME_SIZE];
    //WCHAR vmName[NAME_SIZE];
    //WCHAR vmMoRef[NAME_SIZE];
    //WCHAR vAppInfoFilePath[PATH_SIZE];           			// full path of the vAppMetadata.xml file
    //WCHAR* pVAppConfigXML;                                  // XML string in VAppSessionInfo format, generated by UI
    wstring vCenterId;                                      SERIALIZE_BASIC(vCenterId);
    wstring vAppId;                                         SERIALIZE_BASIC(vAppId);
    wstring vdcStorageProfileId;                            SERIALIZE_BASIC(vdcStorageProfileId);
    wstring vmName;                                         SERIALIZE_BASIC(vmName);
    wstring vmMoRef;                                        SERIALIZE_BASIC(vmMoRef);
    wstring vAppInfoFilePath;                               SERIALIZE_BASIC(vAppInfoFilePath);
    wstring pVAppConfigXML;                                 SERIALIZE_BASIC(pVAppConfigXML);

    SERIALIZE_END_STRUCT(tagVCloud_ImportVMParams_internal, tagVCloud_ImportVMParams_internal);
    STRUCT_XML_SERIALIZER;
};
//unfinished
struct tagVCloud_ImportVMResult_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVCloud_ImportVMResult_internal, tagVCloud_ImportVMResult_internal);

    VOID __tagToInternal(IN const VCloud_ImportVMResult& vCloudImportVMResult)
    {
        errorCode = vCloudImportVMResult.errorCode;
        erroMessage = PTR_2_STR(vCloudImportVMResult.erroMessage);
        vmInfo.__tagToInternal(vCloudImportVMResult.vmInfo);
    }

    VOID __tagFromInternal(OUT VCloud_ImportVMResult& vCloudImportVMResult)
    {
        vCloudImportVMResult.errorCode = errorCode;
        wcscpy_s(vCloudImportVMResult.erroMessage, ERROR_STRING_SIZE, erroMessage.c_str());
        vmInfo.__tagFromInternal(vCloudImportVMResult.vmInfo);
    }

    //int errorCode;
    //WCHAR erroMessage[ERROR_STRING_SIZE];
    //VCloudVM_Info vmInfo;

    INT errorCode;                                          SERIALIZE_BASIC(errorCode);
    wstring erroMessage;                                    SERIALIZE_BASIC(erroMessage);
    tagVCloudVM_Info_internal vmInfo;                       SERIALIZE_COMPLEX(vmInfo);

    SERIALIZE_END_STRUCT(tagVCloud_ImportVMResult_internal, tagVCloud_ImportVMResult_internal);
    STRUCT_XML_SERIALIZER;
};

struct tagVCloudDatastore_Info_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVCloudDatastore_Info_internal, tagVCloudDatastore_Info_internal);

    VOID __tagToInternal(IN const VCloudDatastore_Info& vCloudDatastore)
    {
        name = PTR_2_STR(vCloudDatastore.name);
        id = PTR_2_STR(vCloudDatastore.id);
        moRef = PTR_2_STR(vCloudDatastore.moRef);
    }

    VOID __tagFromInternal(OUT VCloudDatastore_Info& vCloudDatastore)
    {
        wcscpy_s(vCloudDatastore.name, NAME_SIZE, name.c_str());
        wcscpy_s(vCloudDatastore.id, NAME_SIZE, id.c_str());
        wcscpy_s(vCloudDatastore.moRef, NAME_SIZE, moRef.c_str());
    }

    //WCHAR name[NAME_SIZE];
    //WCHAR id[NAME_SIZE];
    //WCHAR moRef[NAME_SIZE];
    wstring name;                                     SERIALIZE_BASIC(name);
    wstring id;                                       SERIALIZE_BASIC(id);
    wstring moRef;                                    SERIALIZE_BASIC(moRef);

    SERIALIZE_END_STRUCT(tagVCloudDatastore_Info_internal, tagVCloudDatastore_Info_internal);
    STRUCT_XML_SERIALIZER;
};
struct tagVCloudESXHost_Info_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVCloudESXHost_Info_internal, tagVCloudESXHost_Info_internal);

    VOID __tagToInternal(IN const VCloudESXHost_Info& vCloudESXHostInfo)
    {
        name = PTR_2_STR(vCloudESXHostInfo.name);
        id = PTR_2_STR(vCloudESXHostInfo.id);
        moRef = PTR_2_STR(vCloudESXHostInfo.moRef);
        vCenter = PTR_2_STR(vCloudESXHostInfo.vCenter);
        vCenterId = PTR_2_STR(vCloudESXHostInfo.vCenterId);
        cpuType = PTR_2_STR(vCloudESXHostInfo.cpuType);
        numOfCpusPackages = vCloudESXHostInfo.numOfCpusPackages;
        numOfCpusLogical = vCloudESXHostInfo.numOfCpusLogical;
        cpuTotal = vCloudESXHostInfo.cpuTotal;
        memTotal = vCloudESXHostInfo.memTotal;
        hostOsName = PTR_2_STR(vCloudESXHostInfo.hostOsName);
        hostOsVersion = PTR_2_STR(vCloudESXHostInfo.hostOsVersion);
    }

    VOID __tagFromInternal(OUT VCloudESXHost_Info& vCloudESXHostInfo)
    {
        wcscpy_s(vCloudESXHostInfo.name, NAME_SIZE, name.c_str());
        wcscpy_s(vCloudESXHostInfo.id, NAME_SIZE, id.c_str());
        wcscpy_s(vCloudESXHostInfo.moRef, NAME_SIZE, moRef.c_str());
        wcscpy_s(vCloudESXHostInfo.vCenter, NAME_SIZE, vCenter.c_str());
        wcscpy_s(vCloudESXHostInfo.vCenterId, NAME_SIZE, vCenterId.c_str());
        wcscpy_s(vCloudESXHostInfo.cpuType, NAME_SIZE, cpuType.c_str());
        vCloudESXHostInfo.numOfCpusPackages = numOfCpusPackages;
        vCloudESXHostInfo.numOfCpusLogical = numOfCpusLogical;
        vCloudESXHostInfo.cpuTotal = cpuTotal;
        vCloudESXHostInfo.memTotal = memTotal;
        wcscpy_s(vCloudESXHostInfo.hostOsName, NAME_SIZE, hostOsName.c_str());
        wcscpy_s(vCloudESXHostInfo.hostOsVersion, NAME_SIZE, hostOsVersion.c_str());
    }

    //WCHAR name[NAME_SIZE];
    //WCHAR id[NAME_SIZE];
    //WCHAR moRef[NAME_SIZE];
    //WCHAR vCenter[NAME_SIZE];
    //WCHAR vCenterId[NAME_SIZE];
    //WCHAR cpuType[NAME_SIZE];
    //int numOfCpusPackages;
    //int numOfCpusLogical;
    //INT64 cpuTotal;
    //INT64 memTotal;
    //WCHAR hostOsName[NAME_SIZE];
    //WCHAR hostOsVersion[NAME_SIZE];
    wstring name;                                        SERIALIZE_BASIC(name);
    wstring id;                                          SERIALIZE_BASIC(id);
    wstring moRef;                                       SERIALIZE_BASIC(moRef);
    wstring vCenter;                                     SERIALIZE_BASIC(vCenter);
    wstring vCenterId;                                   SERIALIZE_BASIC(vCenterId);
    wstring cpuType;                                     SERIALIZE_BASIC(cpuType);
    INT numOfCpusPackages;                               SERIALIZE_BASIC(numOfCpusPackages);
    INT numOfCpusLogical;                                SERIALIZE_BASIC(numOfCpusLogical);
    INT64 cpuTotal;                                      SERIALIZE_BASIC(cpuTotal);
    INT64 memTotal;                                      SERIALIZE_BASIC(memTotal);
    wstring hostOsName;                                  SERIALIZE_BASIC(hostOsName);
    wstring hostOsVersion;                               SERIALIZE_BASIC(hostOsVersion);

    SERIALIZE_END_STRUCT(tagVCloudESXHost_Info_internal, tagVCloudESXHost_Info_internal);
    STRUCT_XML_SERIALIZER;
};

struct tagVCloud_StorageProfile_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVCloud_StorageProfile_internal, tagVCloud_StorageProfile_internal);

    VOID __tagToInternal(IN const VCloud_StorageProfile& vCloudStorageProfile)
    {
        name = PTR_2_STR(vCloudStorageProfile.name);
        id = PTR_2_STR(vCloudStorageProfile.id);
        moRef = PTR_2_STR(vCloudStorageProfile.moRef);
    }

    VOID __tagFromInternal(OUT VCloud_StorageProfile& vCloudStorageProfile)
    {
        wcscpy_s(vCloudStorageProfile.name, NAME_SIZE, name.c_str());
        wcscpy_s(vCloudStorageProfile.id, NAME_SIZE, id.c_str());
        wcscpy_s(vCloudStorageProfile.moRef, NAME_SIZE, moRef.c_str());
    }

    //WCHAR name[NAME_SIZE];
    //WCHAR id[NAME_SIZE];
    //WCHAR moRef[NAME_SIZE];
    wstring name;                                     SERIALIZE_BASIC(name);
    wstring id;                                       SERIALIZE_BASIC(id);
    wstring moRef;                                    SERIALIZE_BASIC(moRef);

    SERIALIZE_END_STRUCT(tagVCloud_StorageProfile_internal, tagVCloud_StorageProfile_internal);
    STRUCT_XML_SERIALIZER;
};
struct tagVCloud_VerifyInfo_internal
{
    SERIALIZE_BEGIN_STRUCT(tagVCloud_VerifyInfo_internal, tagVCloud_VerifyInfo_internal);

    VOID __tagToInternal(IN const VCloud_VerifyInfo& vCloudVerifyInfo)
    {
        orgId = PTR_2_STR(vCloudVerifyInfo.orgId);
        orgName = PTR_2_STR(vCloudVerifyInfo.orgName);
        orgVdcId = PTR_2_STR(vCloudVerifyInfo.orgVdcId);
        orgVdcName = PTR_2_STR(vCloudVerifyInfo.orgVdcName);
        vAppId = PTR_2_STR(vCloudVerifyInfo.vAppId);
        vAppName = PTR_2_STR(vCloudVerifyInfo.vAppName);

    }

    VOID __tagFromInternal(OUT VCloud_VerifyInfo& vCloudVerifyInfo)
    {
        wcscpy_s(vCloudVerifyInfo.orgId, NAME_SIZE, orgId.c_str());
        wcscpy_s(vCloudVerifyInfo.orgName, NAME_SIZE, orgName.c_str());
        wcscpy_s(vCloudVerifyInfo.orgVdcId, NAME_SIZE, orgVdcId.c_str());
        wcscpy_s(vCloudVerifyInfo.orgVdcName, NAME_SIZE, orgVdcName.c_str());
        wcscpy_s(vCloudVerifyInfo.vAppId, NAME_SIZE, vAppId.c_str());
        wcscpy_s(vCloudVerifyInfo.vAppName, NAME_SIZE, vAppName.c_str());

    }

    //WCHAR orgId[NAME_SIZE];
    //WCHAR orgName[NAME_SIZE];
    //WCHAR orgVdcId[NAME_SIZE];
    //WCHAR orgVdcName[NAME_SIZE];
    //WCHAR vAppId[NAME_SIZE];
    //WCHAR vAppName[NAME_SIZE];
    wstring orgId;                                       SERIALIZE_BASIC(orgId);
    wstring orgName;                                     SERIALIZE_BASIC(orgName);
    wstring orgVdcId;                                    SERIALIZE_BASIC(orgVdcId);
    wstring orgVdcName;                                  SERIALIZE_BASIC(orgVdcName);
    wstring vAppId;                                      SERIALIZE_BASIC(vAppId);
    wstring vAppName;                                    SERIALIZE_BASIC(vAppName);

    SERIALIZE_END_STRUCT(tagVCloud_VerifyInfo_internal, tagVCloud_VerifyInfo_internal);
    STRUCT_XML_SERIALIZER;
};

#if 1
struct tagEsxVMs
{
	SERIALIZE_BEGIN_STRUCT(tagEsxVMs, tagEsxVMs);

	tagESXNode_internal esx;				SERIALIZE_COMPLEX(esx);
	vector<tagVM_BasicInfo_internal> vms;	SERIALIZE_COMPLEX_VECTOR(vms);

	SERIALIZE_END_STRUCT(tagEsxVMs, tagEsxVMs);
	STRUCT_XML_SERIALIZER;
};

struct tagAllEsx
{
	SERIALIZE_BEGIN_STRUCT(tagAllEsx, tagAllEsx);
	vector<tagEsxVMs> allEsx;	SERIALIZE_COMPLEX_VECTOR(allEsx);
	SERIALIZE_END_STRUCT(tagAllEsx, tagAllEsx);
	STRUCT_XML_SERIALIZER;
};

#endif

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
struct tagBasicReturn
{
	SERIALIZE_BEGIN_STRUCT(tagBasicReturn, tagBasicReturn);

	ULONG		ulReturn;			SERIALIZE_BASIC(ulReturn);

	SERIALIZE_END_STRUCT(tagBasicReturn, tagBasicReturn);
	STRUCT_XML_SERIALIZER;
};

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
struct tagConnectToESX_In
{
	SERIALIZE_BEGIN_STRUCT(tagConnectToESX_In, tagConnectToESX_In);	

	wstring		esxServer;			SERIALIZE_BASIC(esxServer);
	wstring		esxUser;			SERIALIZE_BASIC(esxUser);
	wstring     esxPwd;             SERIALIZE_BASIC(esxPwd);
	wstring		esxPro;				SERIALIZE_BASIC(esxPro);
	BOOL		bIgnoreCert;		SERIALIZE_BASIC(bIgnoreCert);
	LONG		lPort;				SERIALIZE_BASIC(lPort);

	SERIALIZE_END_STRUCT(tagConnectToESX_In, tagConnectToESX_In);
	STRUCT_XML_SERIALIZER;
};

struct tagConnectToESX_Out
{
	SERIALIZE_BEGIN_STRUCT(tagConnectToESX_Out, tagConnectToESX_Out);
	tagBasicReturn basicReturn;     SERIALIZE_COMPLEX(basicReturn);
	SERIALIZE_END_STRUCT(tagConnectToESX_Out, tagConnectToESX_Out);
	STRUCT_XML_SERIALIZER;
};

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
struct tagGetVMServerType_Out
{
	SERIALIZE_BEGIN_STRUCT(tagGetVMServerType_Out, tagGetVMServerType_Out);
	tagBasicReturn basicReturn;     SERIALIZE_COMPLEX(basicReturn);
	SERIALIZE_END_STRUCT(tagGetVMServerType_Out, tagGetVMServerType_Out);
	STRUCT_XML_SERIALIZER;
};

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
struct tagcheckVMServerLicense_In
{
	SERIALIZE_BEGIN_STRUCT(tagcheckVMServerLicense_In, tagcheckVMServerLicense_In);

	wstring esxName;				SERIALIZE_BASIC(esxName);
	wstring dcName;					SERIALIZE_BASIC(dcName);

	SERIALIZE_END_STRUCT(tagcheckVMServerLicense_In, tagcheckVMServerLicense_In);
	STRUCT_XML_SERIALIZER;
};

struct tagcheckVMServerLicense_Out
{
	SERIALIZE_BEGIN_STRUCT(tagcheckVMServerLicense_Out, tagcheckVMServerLicense_Out);

	tagBasicReturn basicReturn;     SERIALIZE_COMPLEX(basicReturn);

	SERIALIZE_END_STRUCT(tagcheckVMServerLicense_Out, tagcheckVMServerLicense_Out);
	STRUCT_XML_SERIALIZER;
};



struct taggetEsxNodeList_Out
{
	SERIALIZE_BEGIN_STRUCT(taggetEsxNodeList_Out, taggetEsxNodeList_Out);

	vector<tagESXNode_internal> vecEsxNodes;	SERIALIZE_COMPLEX_VECTOR(vecEsxNodes);

	SERIALIZE_END_STRUCT(taggetEsxNodeList_Out, taggetEsxNodeList_Out);
	STRUCT_XML_SERIALIZER;
};

struct taggetVMList_In
{
	SERIALIZE_BEGIN_STRUCT(taggetVMList_In, taggetVMList_In);
	
	tagESXNode_internal esxNode;    SERIALIZE_COMPLEX(esxNode)

	SERIALIZE_END_STRUCT(taggetVMList_In, taggetVMList_In);
	STRUCT_XML_SERIALIZER;
};

struct taggetVMList_Out
{
	SERIALIZE_BEGIN_STRUCT(taggetVMList_Out, taggetVMList_Out);

	vector<tagVM_BasicInfo_internal> vecVMs;  SERIALIZE_COMPLEX_VECTOR(vecVMs);

	SERIALIZE_END_STRUCT(taggetVMList_Out, taggetVMList_Out);
	STRUCT_XML_SERIALIZER;
};

//checkVMServerInMaintainenceMode
struct tagcheckVMServerInMaintainenceMode_In
{
    SERIALIZE_BEGIN_STRUCT(tagcheckVMServerInMaintainenceMode_In, tagcheckVMServerInMaintainenceMode_In);

    wstring esxName;				SERIALIZE_BASIC(esxName);
    wstring dcName;					SERIALIZE_BASIC(dcName);

    SERIALIZE_END_STRUCT(tagcheckVMServerInMaintainenceMode_In, tagcheckVMServerInMaintainenceMode_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcheckVMServerInMaintainenceMode_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcheckVMServerInMaintainenceMode_Out, tagcheckVMServerInMaintainenceMode_Out);

    BOOL bRet;                      SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagcheckVMServerInMaintainenceMode_Out, tagcheckVMServerInMaintainenceMode_Out);
    STRUCT_XML_SERIALIZER;
};

//getESXVersion
struct taggetESXVersion_In
{
    SERIALIZE_BEGIN_STRUCT(taggetESXVersion_In, taggetESXVersion_In);

    wstring esxName;                SERIALIZE_BASIC(esxName);
    wstring dcName;                 SERIALIZE_BASIC(dcName);

    SERIALIZE_END_STRUCT(taggetESXVersion_In, taggetESXVersion_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetESXVersion_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetESXVersion_Out, taggetESXVersion_Out);

    wstring esxVersion;             SERIALIZE_BASIC(esxVersion);

    SERIALIZE_END_STRUCT(taggetESXVersion_Out, taggetESXVersion_Out);
    STRUCT_XML_SERIALIZER;
};

//getESXNumberOfProcessors
//<sonmi01>2015-7-23 #shared jvm review and fix
struct taggetESXNumberOfProcessors_In
{
    SERIALIZE_BEGIN_STRUCT(taggetESXNumberOfProcessors_In, taggetESXNumberOfProcessors_In);

    wstring esxName;                SERIALIZE_BASIC(esxName);

    SERIALIZE_END_STRUCT(taggetESXNumberOfProcessors_In, taggetESXNumberOfProcessors_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetESXNumberOfProcessors_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetESXNumberOfProcessors_Out, taggetESXNumberOfProcessors_Out);
	UINT numberOfLogicalProcessors; SERIALIZE_BASIC(numberOfLogicalProcessors);
	UINT numberOfProcessors;        SERIALIZE_BASIC(numberOfProcessors);
    INT nRet;                       SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(taggetESXNumberOfProcessors_Out, taggetESXNumberOfProcessors_Out);
    STRUCT_XML_SERIALIZER;
};

//getESXHostDataStoreList
struct taggetESXHostDataStoreList_In
{
    SERIALIZE_BEGIN_STRUCT(taggetESXHostDataStoreList_In, taggetESXHostDataStoreList_In);

    tagESXNode_internal esxNode_internal;	SERIALIZE_COMPLEX(esxNode_internal);

    SERIALIZE_END_STRUCT(taggetESXHostDataStoreList_In, taggetESXHostDataStoreList_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetESXHostDataStoreList_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetESXHostDataStoreList_Out, taggetESXHostDataStoreList_Out);

    vector<tagDataStore_internal> vecDSs;  SERIALIZE_COMPLEX_VECTOR(vecDSs);

    SERIALIZE_END_STRUCT(taggetESXHostDataStoreList_Out, taggetESXHostDataStoreList_Out);
    STRUCT_XML_SERIALIZER;
};

//checkResPool
struct tagcheckResPool_In
{
    SERIALIZE_BEGIN_STRUCT(tagcheckResPool_In, tagcheckResPool_In);

    tagESXNode_internal esxNode;    SERIALIZE_COMPLEX(esxNode);
    wstring resPool;                SERIALIZE_BASIC(resPool);

    SERIALIZE_END_STRUCT(tagcheckResPool_In, tagcheckResPool_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcheckResPool_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcheckResPool_Out, tagcheckResPool_Out);

    BOOL bRet;                      SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagcheckResPool_Out, tagcheckResPool_Out);
    STRUCT_XML_SERIALIZER;
};

//setInstanceUUID
struct tagsetInstanceUUID_In
{
    SERIALIZE_BEGIN_STRUCT(tagsetInstanceUUID_In, tagsetInstanceUUID_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring newInstanceUUID;                SERIALIZE_BASIC(newInstanceUUID);

    SERIALIZE_END_STRUCT(tagsetInstanceUUID_In, tagsetInstanceUUID_In);
    STRUCT_XML_SERIALIZER;
};

struct tagsetInstanceUUID_Out
{
    SERIALIZE_BEGIN_STRUCT(tagsetInstanceUUID_Out, tagsetInstanceUUID_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagsetInstanceUUID_Out, tagsetInstanceUUID_Out);
    STRUCT_XML_SERIALIZER;
};

//checkDSBlockSize
struct tagcheckDSBlockSize_In
{
    SERIALIZE_BEGIN_STRUCT(tagcheckDSBlockSize_In, tagcheckDSBlockSize_In);

    tagESXNode_internal esxNode;            SERIALIZE_COMPLEX(esxNode)
    wstring dataStore;                      SERIALIZE_BASIC(dataStore);

    SERIALIZE_END_STRUCT(tagcheckDSBlockSize_In, tagcheckDSBlockSize_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcheckDSBlockSize_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcheckDSBlockSize_Out, tagcheckDSBlockSize_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagcheckDSBlockSize_Out, tagcheckDSBlockSize_Out);
    STRUCT_XML_SERIALIZER;
};


//takeSnapShot
struct tagtakeSnapShot_In
{
    SERIALIZE_BEGIN_STRUCT(tagtakeSnapShot_In, tagtakeSnapShot_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotName;                   SERIALIZE_BASIC(snapshotName);
    BOOL bQuiesce;                          SERIALIZE_BASIC(bQuiesce);

    SERIALIZE_END_STRUCT(tagtakeSnapShot_In, tagtakeSnapShot_In);
    STRUCT_XML_SERIALIZER;
};

struct tagtakeSnapShot_Out
{
    SERIALIZE_BEGIN_STRUCT(tagtakeSnapShot_Out, tagtakeSnapShot_Out);

    tagError_Info_internal errorInfo;       SERIALIZE_COMPLEX(errorInfo);
    wstring szRet;                          SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(tagtakeSnapShot_Out, tagtakeSnapShot_Out);
    STRUCT_XML_SERIALIZER;
};


//checkandtakeSnapShot
struct tagcheckandtakeSnapShot_In
{
    SERIALIZE_BEGIN_STRUCT(tagcheckandtakeSnapShot_In, tagcheckandtakeSnapShot_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotName;                   SERIALIZE_BASIC(snapshotName);

    SERIALIZE_END_STRUCT(tagcheckandtakeSnapShot_In, tagcheckandtakeSnapShot_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcheckandtakeSnapShot_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcheckandtakeSnapShot_Out, tagcheckandtakeSnapShot_Out);

    wstring szRet;                          SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(tagcheckandtakeSnapShot_Out, tagcheckandtakeSnapShot_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMMoref
struct taggetVMMoref_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMMoref_In, taggetVMMoref_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);

    SERIALIZE_END_STRUCT(taggetVMMoref_In, taggetVMMoref_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMMoref_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMMoref_Out, taggetVMMoref_Out);

    wstring szRet;                          SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(taggetVMMoref_Out, taggetVMMoref_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMVersion
struct taggetVMVersion_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMVersion_In, taggetVMVersion_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);

    SERIALIZE_END_STRUCT(taggetVMVersion_In, taggetVMVersion_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMVersion_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMVersion_Out, taggetVMVersion_Out);

    wstring szRet;                          SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(taggetVMVersion_Out, taggetVMVersion_Out);
    STRUCT_XML_SERIALIZER;
};

//////////////////
//revertSnapShot
struct tagrevertSnapShot_In
{
    SERIALIZE_BEGIN_STRUCT(tagrevertSnapShot_In, tagrevertSnapShot_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(tagrevertSnapShot_In, tagrevertSnapShot_In);
    STRUCT_XML_SERIALIZER;
};

struct tagrevertSnapShot_Out
{
    SERIALIZE_BEGIN_STRUCT(tagrevertSnapShot_Out, tagrevertSnapShot_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagrevertSnapShot_Out, tagrevertSnapShot_Out);
    STRUCT_XML_SERIALIZER;
};

//removeSnapShot
struct tagremoveSnapShot_In
{
    SERIALIZE_BEGIN_STRUCT(tagremoveSnapShot_In, tagremoveSnapShot_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(tagremoveSnapShot_In, tagremoveSnapShot_In);
    STRUCT_XML_SERIALIZER;
};

struct tagremoveSnapShot_Out
{
    SERIALIZE_BEGIN_STRUCT(tagremoveSnapShot_Out, tagremoveSnapShot_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagremoveSnapShot_Out, tagremoveSnapShot_Out);
    STRUCT_XML_SERIALIZER;
};

//removeSnapShotAsync
struct tagremoveSnapShotAsync_In
{
    SERIALIZE_BEGIN_STRUCT(tagremoveSnapShotAsync_In, tagremoveSnapShotAsync_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(tagremoveSnapShotAsync_In, tagremoveSnapShotAsync_In);
    STRUCT_XML_SERIALIZER;
};

struct tagremoveSnapShotAsync_Out
{
    SERIALIZE_BEGIN_STRUCT(tagremoveSnapShotAsync_Out, tagremoveSnapShotAsync_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagremoveSnapShotAsync_Out, tagremoveSnapShotAsync_Out);
    STRUCT_XML_SERIALIZER;
};

//removeSnapShotByName
struct tagremoveSnapShotByName_In
{
    SERIALIZE_BEGIN_STRUCT(tagremoveSnapShotByName_In, tagremoveSnapShotByName_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotName;                   SERIALIZE_BASIC(snapshotName);

    SERIALIZE_END_STRUCT(tagremoveSnapShotByName_In, tagremoveSnapShotByName_In);
    STRUCT_XML_SERIALIZER;
};

struct tagremoveSnapShotByName_Out
{
    SERIALIZE_BEGIN_STRUCT(tagremoveSnapShotByName_Out, tagremoveSnapShotByName_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagremoveSnapShotByName_Out, tagremoveSnapShotByName_Out);
    STRUCT_XML_SERIALIZER;
};

//getsnapshotCTF
struct taggetsnapshotCTF_In
{
    SERIALIZE_BEGIN_STRUCT(taggetsnapshotCTF_In, taggetsnapshotCTF_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(taggetsnapshotCTF_In, taggetsnapshotCTF_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetsnapshotCTF_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetsnapshotCTF_Out, taggetsnapshotCTF_Out);

    wstring szRet;                          SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(taggetsnapshotCTF_Out, taggetsnapshotCTF_Out);
    STRUCT_XML_SERIALIZER;
};

//getparentSnapshot
struct taggetparentSnapshot_In
{
    SERIALIZE_BEGIN_STRUCT(taggetparentSnapshot_In, taggetparentSnapshot_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(taggetparentSnapshot_In, taggetparentSnapshot_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetparentSnapshot_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetparentSnapshot_Out, taggetparentSnapshot_Out);

    wstring szRet;                          SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(taggetparentSnapshot_Out, taggetparentSnapshot_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMDiskURLs
struct taggetVMDiskURLs_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMDiskURLs_In, taggetVMDiskURLs_In);

    wstring vmName;                          SERIALIZE_BASIC(vmName);
    wstring vmUUID;                          SERIALIZE_BASIC(vmUUID);

    SERIALIZE_END_STRUCT(taggetVMDiskURLs_In, taggetVMDiskURLs_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMDiskURLs_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMDiskURLs_Out, taggetVMDiskURLs_Out);

    vector<tagDisk_Info_internal> vecDIs;  SERIALIZE_COMPLEX_VECTOR(vecDIs);

    SERIALIZE_END_STRUCT(taggetVMDiskURLs_Out, taggetVMDiskURLs_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMSnapshotList
struct taggetVMSnapshotList_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMSnapshotList_In, taggetVMSnapshotList_In);

    wstring vmName;                          SERIALIZE_BASIC(vmName);
    wstring vmUUID;                          SERIALIZE_BASIC(vmUUID);

    SERIALIZE_END_STRUCT(taggetVMSnapshotList_In, taggetVMSnapshotList_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMSnapshotList_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMSnapshotList_Out, taggetVMSnapshotList_Out);

    vector<tagSnapshot_Info_internal> vecSSIs;  SERIALIZE_COMPLEX_VECTOR(vecSSIs);

    SERIALIZE_END_STRUCT(taggetVMSnapshotList_Out, taggetVMSnapshotList_Out);
    STRUCT_XML_SERIALIZER;
};

//getSnapShotDiskInfo
struct taggetSnapShotDiskInfo_In
{
    SERIALIZE_BEGIN_STRUCT(taggetSnapShotDiskInfo_In, taggetSnapShotDiskInfo_In);

    wstring vmName;                          SERIALIZE_BASIC(vmName);
    wstring vmUUID;                          SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                     SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(taggetSnapShotDiskInfo_In, taggetSnapShotDiskInfo_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetSnapShotDiskInfo_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetSnapShotDiskInfo_Out, taggetSnapShotDiskInfo_Out);

    vector<tagDisk_Info_internal> vecDIs;  SERIALIZE_COMPLEX_VECTOR(vecDIs);

    SERIALIZE_END_STRUCT(taggetSnapShotDiskInfo_Out, taggetSnapShotDiskInfo_Out);
    STRUCT_XML_SERIALIZER;
};

////
//getSnapShotAdrDiskInfo
struct taggetSnapShotAdrDiskInfo_In
{
    SERIALIZE_BEGIN_STRUCT(taggetSnapShotAdrDiskInfo_In, taggetSnapShotAdrDiskInfo_In);

    wstring vmName;                          SERIALIZE_BASIC(vmName);
    wstring vmUUID;                          SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                     SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(taggetSnapShotAdrDiskInfo_In, taggetSnapShotAdrDiskInfo_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetSnapShotAdrDiskInfo_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetSnapShotAdrDiskInfo_Out, taggetSnapShotAdrDiskInfo_Out);

    vector<tagAdrDisk_Info_internal> vecADIs;  SERIALIZE_COMPLEX_VECTOR(vecADIs);

    SERIALIZE_END_STRUCT(taggetSnapShotAdrDiskInfo_Out, taggetSnapShotAdrDiskInfo_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMAdrDiskInfo
struct taggetVMAdrDiskInfo_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMAdrDiskInfo_In, taggetVMAdrDiskInfo_In);

    wstring vmName;                          SERIALIZE_BASIC(vmName);
    wstring vmUUID;                          SERIALIZE_BASIC(vmUUID);

    SERIALIZE_END_STRUCT(taggetVMAdrDiskInfo_In, taggetVMAdrDiskInfo_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMAdrDiskInfo_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMAdrDiskInfo_Out, taggetVMAdrDiskInfo_Out);

    vector<tagAdrDisk_Info_internal> vecADIs;  SERIALIZE_COMPLEX_VECTOR(vecADIs);

    SERIALIZE_END_STRUCT(taggetVMAdrDiskInfo_Out, taggetVMAdrDiskInfo_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMInfo
struct taggetVMInfo_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMInfo_In, taggetVMInfo_In);

    wstring vmName;                          SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                      SERIALIZE_BASIC(vmInstUUID);

    SERIALIZE_END_STRUCT(taggetVMInfo_In, taggetVMInfo_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMInfo_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMInfo_Out, taggetVMInfo_Out);

    tagVM_Info_internal vmInfo;             SERIALIZE_COMPLEX(vmInfo);

    SERIALIZE_END_STRUCT(taggetVMInfo_Out, taggetVMInfo_Out);
    STRUCT_XML_SERIALIZER;
};




///////////////////////////////
//deleteCTKFiles
struct tagdeleteCTKFiles_In
{
    SERIALIZE_BEGIN_STRUCT(tagdeleteCTKFiles_In, tagdeleteCTKFiles_In);

    wstring esxName;                         SERIALIZE_BASIC(esxName);
    wstring dcName;                          SERIALIZE_BASIC(dcName);
    wstring vmName;                          SERIALIZE_BASIC(vmName);
    wstring vmUUID;                          SERIALIZE_BASIC(vmUUID);

    SERIALIZE_END_STRUCT(tagdeleteCTKFiles_In, tagdeleteCTKFiles_In);
    STRUCT_XML_SERIALIZER;
};

struct tagdeleteCTKFiles_Out
{
    SERIALIZE_BEGIN_STRUCT(tagdeleteCTKFiles_Out, tagdeleteCTKFiles_Out);

    SERIALIZE_END_STRUCT(tagdeleteCTKFiles_Out, tagdeleteCTKFiles_Out);
    STRUCT_XML_SERIALIZER;
};

//hasSufficientPermission
struct taghasSufficientPermission_In
{
    SERIALIZE_BEGIN_STRUCT(taghasSufficientPermission_In, taghasSufficientPermission_In);

    SERIALIZE_END_STRUCT(taghasSufficientPermission_In, taghasSufficientPermission_In);
    STRUCT_XML_SERIALIZER;
};

struct taghasSufficientPermission_Out
{
    SERIALIZE_BEGIN_STRUCT(taghasSufficientPermission_Out, taghasSufficientPermission_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(taghasSufficientPermission_Out, taghasSufficientPermission_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMDataStoreDetails
struct taggetVMDataStoreDetails_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMDataStoreDetails_In, taggetVMDataStoreDetails_In);

    tagESXNode_internal nodeDetails;             SERIALIZE_COMPLEX(nodeDetails);
    wstring dsName;                              SERIALIZE_BASIC(dsName);

    SERIALIZE_END_STRUCT(taggetVMDataStoreDetails_In, taggetVMDataStoreDetails_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMDataStoreDetails_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMDataStoreDetails_Out, taggetVMDataStoreDetails_Out);

    tagVMDataStoreInfo_internal vmDataStoreInfo;        SERIALIZE_COMPLEX(vmDataStoreInfo);

    SERIALIZE_END_STRUCT(taggetVMDataStoreDetails_Out, taggetVMDataStoreDetails_Out);
    STRUCT_XML_SERIALIZER;
};

//rescanallHBA
struct tagrescanallHBA_In
{
    SERIALIZE_BEGIN_STRUCT(tagrescanallHBA_In, tagrescanallHBA_In);

    tagESXNode_internal nodeDetails;             SERIALIZE_COMPLEX(nodeDetails);
	BOOL rescanVC;								 SERIALIZE_BASIC(rescanVC);

    SERIALIZE_END_STRUCT(tagrescanallHBA_In, tagrescanallHBA_In);
    STRUCT_XML_SERIALIZER;
};

struct tagrescanallHBA_Out
{
    SERIALIZE_BEGIN_STRUCT(tagrescanallHBA_Out, tagrescanallHBA_Out);

    SERIALIZE_END_STRUCT(tagrescanallHBA_Out, tagrescanallHBA_Out);
    STRUCT_XML_SERIALIZER;
};

//addCloneDataStore
struct tagaddCloneDataStore_In
{
    SERIALIZE_BEGIN_STRUCT(tagaddCloneDataStore_In, tagaddCloneDataStore_In);

    tagESXNode_internal nodeDetails;             SERIALIZE_COMPLEX(nodeDetails);
    wstring dsGUID;                              SERIALIZE_BASIC(dsGUID);

    SERIALIZE_END_STRUCT(tagaddCloneDataStore_In, tagaddCloneDataStore_In);
    STRUCT_XML_SERIALIZER;
};

struct tagaddCloneDataStore_Out
{
    SERIALIZE_BEGIN_STRUCT(tagaddCloneDataStore_Out, tagaddCloneDataStore_Out);

    wstring szRet;                              SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(tagaddCloneDataStore_Out, tagaddCloneDataStore_Out);
    STRUCT_XML_SERIALIZER;
};

//destroyandDeleteClone
struct tagdestroyandDeleteClone_In
{
    SERIALIZE_BEGIN_STRUCT(tagdestroyandDeleteClone_In, tagdestroyandDeleteClone_In);

    tagESXNode_internal nodeDetails;             SERIALIZE_COMPLEX(nodeDetails);
    wstring dsName;                              SERIALIZE_BASIC(dsName);

    SERIALIZE_END_STRUCT(tagdestroyandDeleteClone_In, tagdestroyandDeleteClone_In);
    STRUCT_XML_SERIALIZER;
};

struct tagdestroyandDeleteClone_Out
{
    SERIALIZE_BEGIN_STRUCT(tagdestroyandDeleteClone_Out, tagdestroyandDeleteClone_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagdestroyandDeleteClone_Out, tagdestroyandDeleteClone_Out);
    STRUCT_XML_SERIALIZER;
};

//createApplianceVM
struct tagcreateApplianceVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagcreateApplianceVM_In, tagcreateApplianceVM_In);

    tagESXNode_internal nodeDetails;             SERIALIZE_COMPLEX(nodeDetails);
    wstring vmName;                              SERIALIZE_BASIC(vmName);

    SERIALIZE_END_STRUCT(tagcreateApplianceVM_In, tagcreateApplianceVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcreateApplianceVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcreateApplianceVM_Out, tagcreateApplianceVM_Out);

    wstring szRet;                              SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(tagcreateApplianceVM_Out, tagcreateApplianceVM_Out);
    STRUCT_XML_SERIALIZER;
};

//createStndAloneApplianceVM
struct tagcreateStndAloneApplianceVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagcreateStndAloneApplianceVM_In, tagcreateStndAloneApplianceVM_In);

    tagESXNode_internal nodeDetails;             SERIALIZE_COMPLEX(nodeDetails);

    SERIALIZE_END_STRUCT(tagcreateStndAloneApplianceVM_In, tagcreateStndAloneApplianceVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcreateStndAloneApplianceVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcreateStndAloneApplianceVM_Out, tagcreateStndAloneApplianceVM_Out);

    wstring szRet;                              SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(tagcreateStndAloneApplianceVM_Out, tagcreateStndAloneApplianceVM_Out);
    STRUCT_XML_SERIALIZER;
};

//deleteApplianceVM
struct tagdeleteApplianceVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagdeleteApplianceVM_In, tagdeleteApplianceVM_In);

    tagESXNode_internal nodeDetails;             SERIALIZE_COMPLEX(nodeDetails);
    wstring vmName;                              SERIALIZE_BASIC(vmName);

    SERIALIZE_END_STRUCT(tagdeleteApplianceVM_In, tagdeleteApplianceVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagdeleteApplianceVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagdeleteApplianceVM_Out, tagdeleteApplianceVM_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagdeleteApplianceVM_Out, tagdeleteApplianceVM_Out);
    STRUCT_XML_SERIALIZER;
};

//attachDiskToVM
struct tagattachDiskToVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagattachDiskToVM_In, tagattachDiskToVM_In);

    wstring vmName;                              SERIALIZE_BASIC(vmName);
    wstring diskURL;                             SERIALIZE_BASIC(diskURL);
    wstring esxName;                             SERIALIZE_BASIC(esxName);
    wstring diskType;                            SERIALIZE_BASIC(diskType);

    SERIALIZE_END_STRUCT(tagattachDiskToVM_In, tagattachDiskToVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagattachDiskToVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagattachDiskToVM_Out, tagattachDiskToVM_Out);

    wstring szRet;                              SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(tagattachDiskToVM_Out, tagattachDiskToVM_Out);
    STRUCT_XML_SERIALIZER;
};

//detachDiskFromVM
struct tagdetachDiskFromVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagdetachDiskFromVM_In, tagdetachDiskFromVM_In);

    wstring vmName;                              SERIALIZE_BASIC(vmName);
    wstring diskURL;                             SERIALIZE_BASIC(diskURL);

    SERIALIZE_END_STRUCT(tagdetachDiskFromVM_In, tagdetachDiskFromVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagdetachDiskFromVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagdetachDiskFromVM_Out, tagdetachDiskFromVM_Out);

    wstring szRet;                              SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(tagdetachDiskFromVM_Out, tagdetachDiskFromVM_Out);
    STRUCT_XML_SERIALIZER;
};

//isESXunderVC
struct tagisESXunderVC_In
{
	SERIALIZE_BEGIN_STRUCT(tagisESXunderVC_In, tagisESXunderVC_In);

	tagESXNode_internal nodeDetails;             SERIALIZE_COMPLEX(nodeDetails);

	SERIALIZE_END_STRUCT(tagisESXunderVC_In, tagisESXunderVC_In);
	STRUCT_XML_SERIALIZER;
};

struct tagisESXunderVC_Out
{
	SERIALIZE_BEGIN_STRUCT(tagisESXunderVC_Out, tagisESXunderVC_Out);

	BOOL bRet;                              SERIALIZE_BASIC(bRet);

	SERIALIZE_END_STRUCT(tagisESXunderVC_Out, tagisESXunderVC_Out);
	STRUCT_XML_SERIALIZER;
};

//isESXinCluster
struct tagisESXinCluster_In
{
    SERIALIZE_BEGIN_STRUCT(tagisESXinCluster_In, tagisESXinCluster_In);

    tagESXNode_internal nodeDetails;             SERIALIZE_COMPLEX(nodeDetails);

    SERIALIZE_END_STRUCT(tagisESXinCluster_In, tagisESXinCluster_In);
    STRUCT_XML_SERIALIZER;
};

struct tagisESXinCluster_Out
{
    SERIALIZE_BEGIN_STRUCT(tagisESXinCluster_Out, tagisESXinCluster_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagisESXinCluster_Out, tagisESXinCluster_Out);
    STRUCT_XML_SERIALIZER;
};

//checkandtakeApplianceSnapShot
struct tagcheckandtakeApplianceSnapShot_In
{
    SERIALIZE_BEGIN_STRUCT(tagcheckandtakeApplianceSnapShot_In, tagcheckandtakeApplianceSnapShot_In);

    wstring vmName;                              SERIALIZE_BASIC(vmName);
    wstring snapshotName;                        SERIALIZE_BASIC(snapshotName);

    SERIALIZE_END_STRUCT(tagcheckandtakeApplianceSnapShot_In, tagcheckandtakeApplianceSnapShot_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcheckandtakeApplianceSnapShot_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcheckandtakeApplianceSnapShot_Out, tagcheckandtakeApplianceSnapShot_Out);

    wstring szRet;                              SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(tagcheckandtakeApplianceSnapShot_Out, tagcheckandtakeApplianceSnapShot_Out);
    STRUCT_XML_SERIALIZER;
};

//removeSnapshotFromAppliance
struct tagremoveSnapshotFromAppliance_In
{
    SERIALIZE_BEGIN_STRUCT(tagremoveSnapshotFromAppliance_In, tagremoveSnapshotFromAppliance_In);

    wstring vmName;                             SERIALIZE_BASIC(vmName);
    wstring snapRef;                            SERIALIZE_BASIC(snapRef);

    SERIALIZE_END_STRUCT(tagremoveSnapshotFromAppliance_In, tagremoveSnapshotFromAppliance_In);
    STRUCT_XML_SERIALIZER;
};

struct tagremoveSnapshotFromAppliance_Out
{
    SERIALIZE_BEGIN_STRUCT(tagremoveSnapshotFromAppliance_Out, tagremoveSnapshotFromAppliance_Out);

    BOOL bRet;                                  SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagremoveSnapshotFromAppliance_Out, tagremoveSnapshotFromAppliance_Out);
    STRUCT_XML_SERIALIZER;
};

//////////////////////////////////////////////////////////////////////////////
//createVMwareVirtualMachine
struct tagcreateVMwareVirtualMachine_In
{
    SERIALIZE_BEGIN_STRUCT(tagcreateVMwareVirtualMachine_In, tagcreateVMwareVirtualMachine_In);

    //wchar_t* configFilePath
    //wchar_t* vcName,
    //    wchar_t* esxHost,
    //    wchar_t* esxDC,
    //    wchar_t* vmResPool,
    //    wchar_t* datastoreOfVM,
    //    wchar_t* vmNewName,
    //    int numDisks,
    //    wchar_t** diskUrlList,
    //    wchar_t** datastoreList,
    //    BOOL overwriteVM,
    //    BOOL recoverToOriginal,
    //    VM_Info* pVMInfo //out									
    wstring configFilePath;                                 SERIALIZE_BASIC(configFilePath);
    wstring vcName;                                         SERIALIZE_BASIC(vcName);
    wstring esxHost;                                        SERIALIZE_BASIC(esxHost);
    wstring esxDC;                                          SERIALIZE_BASIC(esxDC);
    wstring vmResPool;                                      SERIALIZE_BASIC(vmResPool);
    wstring datastoreOfVM;                                  SERIALIZE_BASIC(datastoreOfVM);
    wstring vmNewName;                                      SERIALIZE_BASIC(vmNewName);
    INT numDisks;                                           SERIALIZE_BASIC(numDisks);
    vector<wchar_t*> diskUrlList;                           SERIALIZE_VECTOR(diskUrlList);
    vector<wchar_t*> datastoreList;                         SERIALIZE_VECTOR(datastoreList);
    BOOL overwriteVM;                                       SERIALIZE_BASIC(overwriteVM);
    BOOL recoverToOriginal;                                 SERIALIZE_BASIC(recoverToOriginal);




    SERIALIZE_END_STRUCT(tagcreateVMwareVirtualMachine_In, tagcreateVMwareVirtualMachine_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcreateVMwareVirtualMachine_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcreateVMwareVirtualMachine_Out, tagcreateVMwareVirtualMachine_Out);

    tagError_Info_internal errorInfoRet;                    SERIALIZE_COMPLEX(errorInfoRet);
    tagVM_Info_internal vmInfo;                             SERIALIZE_COMPLEX(vmInfo);

    SERIALIZE_END_STRUCT(tagcreateVMwareVirtualMachine_Out, tagcreateVMwareVirtualMachine_Out);
    STRUCT_XML_SERIALIZER;
};



//getSnapshotConfigInfo
struct taggetSnapshotConfigInfo_In
{
    SERIALIZE_BEGIN_STRUCT(taggetSnapshotConfigInfo_In, taggetSnapshotConfigInfo_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);
    wstring snapshotId;                     SERIALIZE_BASIC(snapshotId);
    wstring pathToSave;                     SERIALIZE_BASIC(pathToSave);

    SERIALIZE_END_STRUCT(taggetSnapshotConfigInfo_In, taggetSnapshotConfigInfo_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetSnapshotConfigInfo_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetSnapshotConfigInfo_Out, taggetSnapshotConfigInfo_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(taggetSnapshotConfigInfo_Out, taggetSnapshotConfigInfo_Out);
    STRUCT_XML_SERIALIZER;
};

//getVSSwriterfiles
struct taggetVSSwriterfiles_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVSSwriterfiles_In, taggetVSSwriterfiles_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);
    wstring snapshotId;                     SERIALIZE_BASIC(snapshotId);
    wstring pathToSave;                     SERIALIZE_BASIC(pathToSave);

    SERIALIZE_END_STRUCT(taggetVSSwriterfiles_In, taggetVSSwriterfiles_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVSSwriterfiles_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVSSwriterfiles_Out, taggetVSSwriterfiles_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(taggetVSSwriterfiles_Out, taggetVSSwriterfiles_Out);
    STRUCT_XML_SERIALIZER;
};

//setVMNVRamFile
struct tagsetVMNVRamFile_In
{
    SERIALIZE_BEGIN_STRUCT(tagsetVMNVRamFile_In, tagsetVMNVRamFile_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);
    wstring nvRamFile;                      SERIALIZE_BASIC(nvRamFile);

    SERIALIZE_END_STRUCT(tagsetVMNVRamFile_In, tagsetVMNVRamFile_In);
    STRUCT_XML_SERIALIZER;
};

struct tagsetVMNVRamFile_Out
{
    SERIALIZE_BEGIN_STRUCT(tagsetVMNVRamFile_Out, tagsetVMNVRamFile_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagsetVMNVRamFile_Out, tagsetVMNVRamFile_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMNVRAMFile
struct taggetVMNVRAMFile_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMNVRAMFile_In, taggetVMNVRAMFile_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);
    wstring pathToSave;                     SERIALIZE_BASIC(pathToSave);

    SERIALIZE_END_STRUCT(taggetVMNVRAMFile_In, taggetVMNVRAMFile_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMNVRAMFile_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMNVRAMFile_Out, taggetVMNVRAMFile_Out);

    wstring szRet;                         SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(taggetVMNVRAMFile_Out, taggetVMNVRAMFile_Out);
    STRUCT_XML_SERIALIZER;
};

//getUsedDiskBlocks
struct taggetUsedDiskBlocks_In
{
    SERIALIZE_BEGIN_STRUCT(taggetUsedDiskBlocks_In, taggetUsedDiskBlocks_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);
    wstring diskChangeId;                   SERIALIZE_BASIC(diskChangeId);
    INT DiskDeviceKey;                      SERIALIZE_BASIC(DiskDeviceKey);
    wstring bitmapFilePath;                 SERIALIZE_BASIC(bitmapFilePath);
    INT chunkSize;                          SERIALIZE_BASIC(chunkSize);
    INT sectorSize;                         SERIALIZE_BASIC(sectorSize);

    SERIALIZE_END_STRUCT(taggetUsedDiskBlocks_In, taggetUsedDiskBlocks_In);
    STRUCT_XML_SERIALIZER;
};

//<sonmi01>2015-7-23 #shared jvm review and fix
struct taggetUsedDiskBlocks_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetUsedDiskBlocks_Out, taggetUsedDiskBlocks_Out);

    INT nRet;                       SERIALIZE_BASIC(nRet);
	LONGLONG bitmapSize;             SERIALIZE_BASIC(bitmapSize);
	LONGLONG UsedSectorCount;        SERIALIZE_BASIC(UsedSectorCount);

    SERIALIZE_END_STRUCT(taggetUsedDiskBlocks_Out, taggetUsedDiskBlocks_Out);
    STRUCT_XML_SERIALIZER;
};


//enableChangeBlockTracking
struct tagenableChangeBlockTracking_In
{
    SERIALIZE_BEGIN_STRUCT(tagenableChangeBlockTracking_In, tagenableChangeBlockTracking_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);
    BOOL bEnable;                           SERIALIZE_BASIC(bEnable);

    SERIALIZE_END_STRUCT(tagenableChangeBlockTracking_In, tagenableChangeBlockTracking_In);
    STRUCT_XML_SERIALIZER;
};

struct tagenableChangeBlockTracking_Out
{
    SERIALIZE_BEGIN_STRUCT(tagenableChangeBlockTracking_Out, tagenableChangeBlockTracking_Out);

    INT nRet;                       SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagenableChangeBlockTracking_Out, tagenableChangeBlockTracking_Out);
    STRUCT_XML_SERIALIZER;
};


//generateDiskBitMapForSnapshot
struct taggenerateDiskBitMapForSnapshot_In
{
    SERIALIZE_BEGIN_STRUCT(taggenerateDiskBitMapForSnapshot_In, taggenerateDiskBitMapForSnapshot_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(taggenerateDiskBitMapForSnapshot_In, taggenerateDiskBitMapForSnapshot_In);
    STRUCT_XML_SERIALIZER;
};

struct taggenerateDiskBitMapForSnapshot_Out
{
    SERIALIZE_BEGIN_STRUCT(taggenerateDiskBitMapForSnapshot_Out, taggenerateDiskBitMapForSnapshot_Out);

    vector<tagDisk_Info_internal> vecDIs;  SERIALIZE_COMPLEX_VECTOR(vecDIs);

    SERIALIZE_END_STRUCT(taggenerateDiskBitMapForSnapshot_Out, taggenerateDiskBitMapForSnapshot_Out);
    STRUCT_XML_SERIALIZER;
};

/////////////////////////////////////////////////////////////
//checkAndEnableChangeBlockTracking
struct tagcheckAndEnableChangeBlockTracking_In
{
    SERIALIZE_BEGIN_STRUCT(tagcheckAndEnableChangeBlockTracking_In, tagcheckAndEnableChangeBlockTracking_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);

    SERIALIZE_END_STRUCT(tagcheckAndEnableChangeBlockTracking_In, tagcheckAndEnableChangeBlockTracking_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcheckAndEnableChangeBlockTracking_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcheckAndEnableChangeBlockTracking_Out, tagcheckAndEnableChangeBlockTracking_Out);

    INT nRet;                              SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagcheckAndEnableChangeBlockTracking_Out, tagcheckAndEnableChangeBlockTracking_Out);
    STRUCT_XML_SERIALIZER;
};
//getFile
struct taggetFile_In
{
    SERIALIZE_BEGIN_STRUCT(taggetFile_In, taggetFile_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring fileName;                       SERIALIZE_BASIC(fileName);
    wstring localPath;                      SERIALIZE_BASIC(localPath);
    
    SERIALIZE_END_STRUCT(taggetFile_In, taggetFile_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetFile_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetFile_Out, taggetFile_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(taggetFile_Out, taggetFile_Out);
    STRUCT_XML_SERIALIZER;
};
//getDiskBitMap
struct taggetDiskBitMap_In
{
    SERIALIZE_BEGIN_STRUCT(taggetDiskBitMap_In, taggetDiskBitMap_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring diskURL;                        SERIALIZE_BASIC(diskURL);
    INT deviceKey;                          SERIALIZE_BASIC(deviceKey);

    SERIALIZE_END_STRUCT(taggetDiskBitMap_In, taggetDiskBitMap_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetDiskBitMap_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetDiskBitMap_Out, taggetDiskBitMap_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(taggetDiskBitMap_Out, taggetDiskBitMap_Out);
    STRUCT_XML_SERIALIZER;
};
//deleteDiskBitMap
struct tagdeleteDiskBitMap_In
{
    SERIALIZE_BEGIN_STRUCT(tagdeleteDiskBitMap_In, tagdeleteDiskBitMap_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(tagdeleteDiskBitMap_In, tagdeleteDiskBitMap_In);
    STRUCT_XML_SERIALIZER;
};

struct tagdeleteDiskBitMap_Out
{
    SERIALIZE_BEGIN_STRUCT(tagdeleteDiskBitMap_Out, tagdeleteDiskBitMap_Out);

    SERIALIZE_END_STRUCT(tagdeleteDiskBitMap_Out, tagdeleteDiskBitMap_Out);
    STRUCT_XML_SERIALIZER;
};

//setFileStream
struct tagsetFileStream_In
{
    SERIALIZE_BEGIN_STRUCT(tagsetFileStream_In, tagsetFileStream_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring fileName;                       SERIALIZE_BASIC(fileName);

    SERIALIZE_END_STRUCT(tagsetFileStream_In, tagsetFileStream_In);
    STRUCT_XML_SERIALIZER;
};

struct tagsetFileStream_Out
{
    SERIALIZE_BEGIN_STRUCT(tagsetFileStream_Out, tagsetFileStream_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagsetFileStream_Out, tagsetFileStream_Out);
    STRUCT_XML_SERIALIZER;
};

//readFileStream!!!!!!!!!!!!!!!!!!!!!
struct tagreadFileStream_In
{
    SERIALIZE_BEGIN_STRUCT(tagreadFileStream_In, tagreadFileStream_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring fileName;                       SERIALIZE_BASIC(fileName);

    SERIALIZE_END_STRUCT(tagreadFileStream_In, tagreadFileStream_In);
    STRUCT_XML_SERIALIZER;
};

struct tagreadFileStream_Out
{
    SERIALIZE_BEGIN_STRUCT(tagreadFileStream_Out, tagreadFileStream_Out);

    INT nRet;                              SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagreadFileStream_Out, tagreadFileStream_Out);
    STRUCT_XML_SERIALIZER;
};

/////////////////////////////////////////////////////////////////////////
//powerOnVM
struct tagpowerOnVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagpowerOnVM_In, tagpowerOnVM_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);

    SERIALIZE_END_STRUCT(tagpowerOnVM_In, tagpowerOnVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagpowerOnVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagpowerOnVM_Out, tagpowerOnVM_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagpowerOnVM_Out, tagpowerOnVM_Out);
    STRUCT_XML_SERIALIZER;
};

///////////////////////////
//powerOffVM
struct tagpowerOffVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagpowerOffVM_In, tagpowerOffVM_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);

    SERIALIZE_END_STRUCT(tagpowerOffVM_In, tagpowerOffVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagpowerOffVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagpowerOffVM_Out, tagpowerOffVM_Out);

    SERIALIZE_END_STRUCT(tagpowerOffVM_Out, tagpowerOffVM_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMPowerState
struct taggetVMPowerState_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMPowerState_In, taggetVMPowerState_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);

    SERIALIZE_END_STRUCT(taggetVMPowerState_In, taggetVMPowerState_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMPowerState_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMPowerState_Out, taggetVMPowerState_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(taggetVMPowerState_Out, taggetVMPowerState_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMToolsState
struct taggetVMToolsState_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMToolsState_In, taggetVMToolsState_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);

    SERIALIZE_END_STRUCT(taggetVMToolsState_In, taggetVMToolsState_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMToolsState_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMToolsState_Out, taggetVMToolsState_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(taggetVMToolsState_Out, taggetVMToolsState_Out);
    STRUCT_XML_SERIALIZER;
};

//getVmdkFilePath
struct taggetVmdkFilePath_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVmdkFilePath_In, taggetVmdkFilePath_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);
    DWORD dwDiskSignaure;                   SERIALIZE_BASIC(dwDiskSignaure);

    SERIALIZE_END_STRUCT(taggetVmdkFilePath_In, taggetVmdkFilePath_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVmdkFilePath_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVmdkFilePath_Out, taggetVmdkFilePath_Out);

    wstring szRet;                          SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(taggetVmdkFilePath_Out, taggetVmdkFilePath_Out);
    STRUCT_XML_SERIALIZER;
};

//removeAllSnapshots
struct tagremoveAllSnapshots_In
{
    SERIALIZE_BEGIN_STRUCT(tagremoveAllSnapshots_In, tagremoveAllSnapshots_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);

    SERIALIZE_END_STRUCT(tagremoveAllSnapshots_In, tagremoveAllSnapshots_In);
    STRUCT_XML_SERIALIZER;
};

struct tagremoveAllSnapshots_Out
{
    SERIALIZE_BEGIN_STRUCT(tagremoveAllSnapshots_Out, tagremoveAllSnapshots_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagremoveAllSnapshots_Out, tagremoveAllSnapshots_Out);
    STRUCT_XML_SERIALIZER;
};

//deleteVM
struct tagdeleteVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagdeleteVM_In, tagdeleteVM_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);

    SERIALIZE_END_STRUCT(tagdeleteVM_In, tagdeleteVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagdeleteVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagdeleteVM_Out, tagdeleteVM_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagdeleteVM_Out, tagdeleteVM_Out);
    STRUCT_XML_SERIALIZER;
};

//renameVM
struct tagrenameVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagrenameVM_In, tagrenameVM_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);
    wstring vmNewname;                      SERIALIZE_BASIC(vmNewname);

    SERIALIZE_END_STRUCT(tagrenameVM_In, tagrenameVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagrenameVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagrenameVM_Out, tagrenameVM_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagrenameVM_Out, tagrenameVM_Out);
    STRUCT_XML_SERIALIZER;
};

////////////////////////////////////////////////////////////////////////////////////////////
//revertSnapShotByID
struct tagrevertSnapShotByID_In
{
    SERIALIZE_BEGIN_STRUCT(tagrevertSnapShotByID_In, tagrevertSnapShotByID_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(tagrevertSnapShotByID_In, tagrevertSnapShotByID_In);
    STRUCT_XML_SERIALIZER;
};

struct tagrevertSnapShotByID_Out
{
    SERIALIZE_BEGIN_STRUCT(tagrevertSnapShotByID_Out, tagrevertSnapShotByID_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagrevertSnapShotByID_Out, tagrevertSnapShotByID_Out);
    STRUCT_XML_SERIALIZER;
};

//removeSnapShotByID
struct tagremoveSnapShotByID_In
{
    SERIALIZE_BEGIN_STRUCT(tagremoveSnapShotByID_In, tagremoveSnapShotByID_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(tagremoveSnapShotByID_In, tagremoveSnapShotByID_In);
    STRUCT_XML_SERIALIZER;
};

struct tagremoveSnapShotByID_Out
{
    SERIALIZE_BEGIN_STRUCT(tagremoveSnapShotByID_Out, tagremoveSnapShotByID_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagremoveSnapShotByID_Out, tagremoveSnapShotByID_Out);
    STRUCT_XML_SERIALIZER;
};

//getSnapShotDiskInfoByID
struct taggetSnapShotDiskInfoByID_In
{
    SERIALIZE_BEGIN_STRUCT(taggetSnapShotDiskInfoByID_In, taggetSnapShotDiskInfoByID_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring snapshotURL;                    SERIALIZE_BASIC(snapshotURL);

    SERIALIZE_END_STRUCT(taggetSnapShotDiskInfoByID_In, taggetSnapShotDiskInfoByID_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetSnapShotDiskInfoByID_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetSnapShotDiskInfoByID_Out, taggetSnapShotDiskInfoByID_Out);

    vector<tagDisk_Info_internal> vecDIs;  SERIALIZE_COMPLEX_VECTOR(vecDIs);

    SERIALIZE_END_STRUCT(taggetSnapShotDiskInfoByID_Out, taggetSnapShotDiskInfoByID_Out);
    STRUCT_XML_SERIALIZER;
};

//enableDiskUUIDForVM
struct tagenableDiskUUIDForVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagenableDiskUUIDForVM_In, tagenableDiskUUIDForVM_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);
    BOOL bEnable;                           SERIALIZE_BASIC(bEnable);

    SERIALIZE_END_STRUCT(tagenableDiskUUIDForVM_In, tagenableDiskUUIDForVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagenableDiskUUIDForVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagenableDiskUUIDForVM_Out, tagenableDiskUUIDForVM_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagenableDiskUUIDForVM_Out, tagenableDiskUUIDForVM_Out);
    STRUCT_XML_SERIALIZER;
};

//logUserEvent
struct taglogUserEvent_In
{
    SERIALIZE_BEGIN_STRUCT(taglogUserEvent_In, taglogUserEvent_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmUUID;                         SERIALIZE_BASIC(vmUUID);
    wstring eventMessage;                   SERIALIZE_BASIC(eventMessage);

    SERIALIZE_END_STRUCT(taglogUserEvent_In, taglogUserEvent_In);
    STRUCT_XML_SERIALIZER;
};

struct taglogUserEvent_Out
{
    SERIALIZE_BEGIN_STRUCT(taglogUserEvent_Out, taglogUserEvent_Out);

    SERIALIZE_END_STRUCT(taglogUserEvent_Out, taglogUserEvent_Out);
    STRUCT_XML_SERIALIZER;
};

//isVMNameUsed
struct tagisVMNameUsed_In
{
    SERIALIZE_BEGIN_STRUCT(tagisVMNameUsed_In, tagisVMNameUsed_In);

    wstring vmname;                         SERIALIZE_BASIC(vmname);

    SERIALIZE_END_STRUCT(tagisVMNameUsed_In, tagisVMNameUsed_In);
    STRUCT_XML_SERIALIZER;
};

struct tagisVMNameUsed_Out
{
    SERIALIZE_BEGIN_STRUCT(tagisVMNameUsed_Out, tagisVMNameUsed_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagisVMNameUsed_Out, tagisVMNameUsed_Out);
    STRUCT_XML_SERIALIZER;
};

//VMHasSnapshot
struct tagVMHasSnapshot_In
{
    SERIALIZE_BEGIN_STRUCT(tagVMHasSnapshot_In, tagVMHasSnapshot_In);

    wstring vmName;                         SERIALIZE_BASIC(vmName);
    wstring vmInstUUID;                     SERIALIZE_BASIC(vmInstUUID);

    SERIALIZE_END_STRUCT(tagVMHasSnapshot_In, tagVMHasSnapshot_In);
    STRUCT_XML_SERIALIZER;
};

struct tagVMHasSnapshot_Out
{
    SERIALIZE_BEGIN_STRUCT(tagVMHasSnapshot_Out, tagVMHasSnapshot_Out);

    BOOL bRet;                              SERIALIZE_BASIC(bRet);

    SERIALIZE_END_STRUCT(tagVMHasSnapshot_Out, tagVMHasSnapshot_Out);
    STRUCT_XML_SERIALIZER;
};

//SetvDSNetworkInfoEx
struct tagSetvDSNetworkInfoEx_In
{
    SERIALIZE_BEGIN_STRUCT(tagSetvDSNetworkInfoEx_In, tagSetvDSNetworkInfoEx_In);

    vector<tagVMNetworkAdapter_Info_internal> vecNetworkAdapterInfo;  SERIALIZE_COMPLEX_VECTOR(vecNetworkAdapterInfo);
    LONG Count;                                                      SERIALIZE_BASIC(Count);

    SERIALIZE_END_STRUCT(tagSetvDSNetworkInfoEx_In, tagSetvDSNetworkInfoEx_In);
    STRUCT_XML_SERIALIZER;
};

struct tagSetvDSNetworkInfoEx_Out
{
    SERIALIZE_BEGIN_STRUCT(tagSetvDSNetworkInfoEx_Out, tagSetvDSNetworkInfoEx_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagSetvDSNetworkInfoEx_Out, tagSetvDSNetworkInfoEx_Out);
    STRUCT_XML_SERIALIZER;
};


//GetESXVFlashResource
struct tagGetESXVFlashResource_In
{
    SERIALIZE_BEGIN_STRUCT(tagGetESXVFlashResource_In, tagGetESXVFlashResource_In);

    wstring esxHost;                         SERIALIZE_BASIC(esxHost);
    wstring esxDC;                           SERIALIZE_BASIC(esxDC);

    SERIALIZE_END_STRUCT(tagGetESXVFlashResource_In, tagGetESXVFlashResource_In);
    STRUCT_XML_SERIALIZER;
};

struct tagGetESXVFlashResource_Out
{
    SERIALIZE_BEGIN_STRUCT(tagGetESXVFlashResource_Out, tagGetESXVFlashResource_Out);

    INT64 nRet;                              SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagGetESXVFlashResource_Out, tagGetESXVFlashResource_Out);
    STRUCT_XML_SERIALIZER;
};


//GetVMVFlashReadCache
struct tagGetVMVFlashReadCache_In
{
    SERIALIZE_BEGIN_STRUCT(tagGetVMVFlashReadCache_In, tagGetVMVFlashReadCache_In);

    wstring configFilePath;                  SERIALIZE_BASIC(configFilePath);

    SERIALIZE_END_STRUCT(tagGetVMVFlashReadCache_In, tagGetVMVFlashReadCache_In);
    STRUCT_XML_SERIALIZER;
};

struct tagGetVMVFlashReadCache_Out
{
    SERIALIZE_BEGIN_STRUCT(tagGetVMVFlashReadCache_Out, tagGetVMVFlashReadCache_Out);

    INT64 nRet;                              SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagGetVMVFlashReadCache_Out, tagGetVMVFlashReadCache_Out);
    STRUCT_XML_SERIALIZER;
};


/////////////////////////////////////////////////////////////
//cleanupHotAddedDisksAndConsolidateSnapshot
struct tagcleanupHotAddedDisksAndConsolidateSnapshot_In
{
    SERIALIZE_BEGIN_STRUCT(tagcleanupHotAddedDisksAndConsolidateSnapshot_In, tagcleanupHotAddedDisksAndConsolidateSnapshot_In);

    wstring esxHost;                                SERIALIZE_BASIC(esxHost);
    vector<wstring> proxyVMIPList;                  SERIALIZE_VECTOR(proxyVMIPList);
    vector<wstring> proxyHotAddedDiskURLs;          SERIALIZE_VECTOR(proxyHotAddedDiskURLs);
    wstring protectedVMInstanceUUID;                SERIALIZE_BASIC(protectedVMInstanceUUID);

    SERIALIZE_END_STRUCT(tagcleanupHotAddedDisksAndConsolidateSnapshot_In, tagcleanupHotAddedDisksAndConsolidateSnapshot_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcleanupHotAddedDisksAndConsolidateSnapshot_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcleanupHotAddedDisksAndConsolidateSnapshot_Out, tagcleanupHotAddedDisksAndConsolidateSnapshot_Out);

    INT nRet;                              SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagcleanupHotAddedDisksAndConsolidateSnapshot_Out, tagcleanupHotAddedDisksAndConsolidateSnapshot_Out);
    STRUCT_XML_SERIALIZER;
};
////////////////////////////////////////

//getVMInfoByMoId
struct taggetVMInfoByMoId_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMInfoByMoId_In, taggetVMInfoByMoId_In);

    wstring vmMoId;                          SERIALIZE_BASIC(vmMoId);

    SERIALIZE_END_STRUCT(taggetVMInfoByMoId_In, taggetVMInfoByMoId_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMInfoByMoId_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMInfoByMoId_Out, taggetVMInfoByMoId_Out);

    tagVM_Info_internal vmInfo;             SERIALIZE_COMPLEX(vmInfo);

    SERIALIZE_END_STRUCT(taggetVMInfoByMoId_Out, taggetVMInfoByMoId_Out);
    STRUCT_XML_SERIALIZER;
};

//SetVMDiskInfo
struct tagSetVMDiskInfo_In
{
    SERIALIZE_BEGIN_STRUCT(tagSetVMDiskInfo_In, tagSetVMDiskInfo_In);

    vector<tagDisk_Info_internal> vecVMDiskInfo;          SERIALIZE_COMPLEX_VECTOR(vecVMDiskInfo);
    LONG Count;                                 SERIALIZE_BASIC(Count);

    SERIALIZE_END_STRUCT(tagSetVMDiskInfo_In, tagSetVMDiskInfo_In);
    STRUCT_XML_SERIALIZER;
};

struct tagSetVMDiskInfo_Out
{
    SERIALIZE_BEGIN_STRUCT(tagSetVMDiskInfo_Out, tagSetVMDiskInfo_Out);

    INT nRet;                                   SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagSetVMDiskInfo_Out, tagSetVMDiskInfo_Out);
    STRUCT_XML_SERIALIZER;
};

//getESXHostListByDatastoreMoRef
struct taggetESXHostListByDatastoreMoRef_In
{
    SERIALIZE_BEGIN_STRUCT(taggetESXHostListByDatastoreMoRef_In, taggetESXHostListByDatastoreMoRef_In);

    wstring datastoreMoRef;                             SERIALIZE_BASIC(datastoreMoRef);

    SERIALIZE_END_STRUCT(taggetESXHostListByDatastoreMoRef_In, taggetESXHostListByDatastoreMoRef_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetESXHostListByDatastoreMoRef_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetESXHostListByDatastoreMoRef_Out, taggetESXHostListByDatastoreMoRef_Out);

    INT nRet;                                           SERIALIZE_BASIC(nRet);
    vector<wstring> esxHostList;                        SERIALIZE_VECTOR(esxHostList);

    SERIALIZE_END_STRUCT(taggetESXHostListByDatastoreMoRef_Out, taggetESXHostListByDatastoreMoRef_Out);
    STRUCT_XML_SERIALIZER;
};


//setVMCpuMemory
struct tagsetVMCpuMemory_In
{
    SERIALIZE_BEGIN_STRUCT(tagsetVMCpuMemory_In, tagsetVMCpuMemory_In);

    INT numCPU;                                 SERIALIZE_BASIC(numCPU);
    INT numCoresPerSocket;                      SERIALIZE_BASIC(numCoresPerSocket);
    LONG memoryMB;                              SERIALIZE_BASIC(memoryMB);

    SERIALIZE_END_STRUCT(tagsetVMCpuMemory_In, tagsetVMCpuMemory_In);
    STRUCT_XML_SERIALIZER;
};

struct tagsetVMCpuMemory_Out
{
    SERIALIZE_BEGIN_STRUCT(tagsetVMCpuMemory_Out, tagsetVMCpuMemory_Out);

    INT nRet;                                   SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagsetVMCpuMemory_Out, tagsetVMCpuMemory_Out);
    STRUCT_XML_SERIALIZER;
};

//getVmdkFilePath
struct taggetThumbprint_In
{
    SERIALIZE_BEGIN_STRUCT(taggetThumbprint_In, taggetThumbprint_In);

    SERIALIZE_END_STRUCT(taggetThumbprint_In, taggetThumbprint_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetThumbprint_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetThumbprint_Out, taggetThumbprint_Out);

    wstring szRet;                          SERIALIZE_BASIC(szRet);

    SERIALIZE_END_STRUCT(taggetThumbprint_Out, taggetThumbprint_Out);
    STRUCT_XML_SERIALIZER;
};


//consolidateVMDisks
struct tagconsolidateVMDisks_In
{
	SERIALIZE_BEGIN_STRUCT(tagconsolidateVMDisks_In, tagconsolidateVMDisks_In);

	wstring vmUuid;                  SERIALIZE_BASIC(vmUuid);

	SERIALIZE_END_STRUCT(tagconsolidateVMDisks_In, tagconsolidateVMDisks_In);
	STRUCT_XML_SERIALIZER;
};

struct tagconsolidateVMDisks_Out
{
	SERIALIZE_BEGIN_STRUCT(tagconsolidateVMDisks_Out, tagconsolidateVMDisks_Out);

	int nRet;                              SERIALIZE_BASIC(nRet);

	SERIALIZE_END_STRUCT(tagconsolidateVMDisks_Out, tagconsolidateVMDisks_Out);
	STRUCT_XML_SERIALIZER;
};


////////////////2015-06-01
//connectVCloud
struct tagconnectVCloud_In
{
    SERIALIZE_BEGIN_STRUCT(tagconnectVCloud_In, tagconnectVCloud_In);

    wstring vcloudDirectorServerName;                           SERIALIZE_BASIC(vcloudDirectorServerName);
    wstring username;                                           SERIALIZE_BASIC(username);
    wstring password;                                           SERIALIZE_BASIC(password);
    wstring protocol;                                           SERIALIZE_BASIC(protocol);
    INT port;                                                   SERIALIZE_BASIC(port);
    BOOL ignoreCert;                                            SERIALIZE_BASIC(ignoreCert);


    SERIALIZE_END_STRUCT(tagconnectVCloud_In, tagconnectVCloud_In);
    STRUCT_XML_SERIALIZER;
};

struct tagconnectVCloud_Out
{
    SERIALIZE_BEGIN_STRUCT(tagconnectVCloud_Out, tagconnectVCloud_Out);

    INT nRet;                                   SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagconnectVCloud_Out, tagconnectVCloud_Out);
    STRUCT_XML_SERIALIZER;
};

//saveVAppInfo
struct tagsaveVAppInfo_In
{
    SERIALIZE_BEGIN_STRUCT(tagsaveVAppInfo_In, tagsaveVAppInfo_In);

    wstring vAppId;                            SERIALIZE_BASIC(vAppId);
    wstring vAppInfoFilePath;                  SERIALIZE_BASIC(vAppInfoFilePath);

    SERIALIZE_END_STRUCT(tagsaveVAppInfo_In, tagsaveVAppInfo_In);
    STRUCT_XML_SERIALIZER;
};

struct tagsaveVAppInfo_Out
{
    SERIALIZE_BEGIN_STRUCT(tagsaveVAppInfo_Out, tagsaveVAppInfo_Out);

    INT nRet;                                   SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagsaveVAppInfo_Out, tagsaveVAppInfo_Out);
    STRUCT_XML_SERIALIZER;
};

//getVMListOfVApp
struct taggetVMListOfVApp_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMListOfVApp_In, taggetVMListOfVApp_In);

    wstring vAppId;                                         SERIALIZE_BASIC(vAppId);

    SERIALIZE_END_STRUCT(taggetVMListOfVApp_In, taggetVMListOfVApp_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMListOfVApp_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMListOfVApp_Out, taggetVMListOfVApp_Out);

    INT nRet;                                               SERIALIZE_BASIC(nRet);
    vector<tagVCloudVM_Info_internal> vmList;     SERIALIZE_COMPLEX_VECTOR(vmList);
    INT vmCount;                                            SERIALIZE_BASIC(vmCount);

    SERIALIZE_END_STRUCT(taggetVMListOfVApp_Out, taggetVMListOfVApp_Out);
    STRUCT_XML_SERIALIZER;
};

//createVApp
struct tagcreateVApp_In
{
    SERIALIZE_BEGIN_STRUCT(tagcreateVApp_In, tagcreateVApp_In);

    tagVCloud_CreateVAppParams_internal vCloudCreateVAppPara;    SERIALIZE_COMPLEX(vCloudCreateVAppPara);

    SERIALIZE_END_STRUCT(tagcreateVApp_In, tagcreateVApp_In);
    STRUCT_XML_SERIALIZER;
};

struct tagcreateVApp_Out
{
    SERIALIZE_BEGIN_STRUCT(tagcreateVApp_Out, tagcreateVApp_Out);

    tagVCloud_CreateVAppResult_internal vCloudCreateVAppResult;   SERIALIZE_COMPLEX(vCloudCreateVAppResult);

    SERIALIZE_END_STRUCT(tagcreateVApp_Out, tagcreateVApp_Out);
    STRUCT_XML_SERIALIZER;
};

//importVM
struct tagimportVM_In
{
    SERIALIZE_BEGIN_STRUCT(tagimportVM_In, tagimportVM_In);

    tagVCloud_ImportVMParams_internal vCloudImportVMPara;    SERIALIZE_COMPLEX(vCloudImportVMPara);

    SERIALIZE_END_STRUCT(tagimportVM_In, tagimportVM_In);
    STRUCT_XML_SERIALIZER;
};

struct tagimportVM_Out
{
    SERIALIZE_BEGIN_STRUCT(tagimportVM_Out, tagimportVM_Out);

    tagVCloud_ImportVMResult_internal vCloudImportVMResult;   SERIALIZE_COMPLEX(vCloudImportVMResult);

    SERIALIZE_END_STRUCT(tagimportVM_Out, tagimportVM_Out);
    STRUCT_XML_SERIALIZER;
};

//deleteVApp
struct tagdeleteVApp_In
{
    SERIALIZE_BEGIN_STRUCT(tagdeleteVApp_In, tagdeleteVApp_In);

    wstring vAppId;                                         SERIALIZE_BASIC(vAppId);

    SERIALIZE_END_STRUCT(tagdeleteVApp_In, tagdeleteVApp_In);
    STRUCT_XML_SERIALIZER;
};

struct tagdeleteVApp_Out
{
    SERIALIZE_BEGIN_STRUCT(tagdeleteVApp_Out, tagdeleteVApp_Out);

    INT nRet;                                  SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagdeleteVApp_Out, tagdeleteVApp_Out);
    STRUCT_XML_SERIALIZER;
};



/////////////2015-06-02
//renameVAppEx
struct tagrenameVAppEx_In
{
    SERIALIZE_BEGIN_STRUCT(tagrenameVAppEx_In, tagrenameVAppEx_In);

    wstring vAppId;                         SERIALIZE_BASIC(vAppId);
    wstring suffix;                         SERIALIZE_BASIC(suffix);
    BOOL append;                            SERIALIZE_BASIC(append);
    BOOL renameVM;                          SERIALIZE_BASIC(renameVM);

    SERIALIZE_END_STRUCT(tagrenameVAppEx_In, tagrenameVAppEx_In);
    STRUCT_XML_SERIALIZER;
};

struct tagrenameVAppEx_Out
{
    SERIALIZE_BEGIN_STRUCT(tagrenameVAppEx_Out, tagrenameVAppEx_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagrenameVAppEx_Out, tagrenameVAppEx_Out);
    STRUCT_XML_SERIALIZER;
};

//getVApp
struct taggetVApp_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVApp_In, taggetVApp_In);

    wstring vAppId;                                 SERIALIZE_BASIC(vAppId);

    SERIALIZE_END_STRUCT(taggetVApp_In, taggetVApp_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVApp_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVApp_Out, taggetVApp_Out);

    INT nRet;                                 SERIALIZE_BASIC(nRet);
    tagVCloudVApp_Info_internal vAppInfo;     SERIALIZE_COMPLEX(vAppInfo);

    SERIALIZE_END_STRUCT(taggetVApp_Out, taggetVApp_Out);
    STRUCT_XML_SERIALIZER;
};

//getVAppListOfVDC
struct taggetVAppListOfVDC_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVAppListOfVDC_In, taggetVAppListOfVDC_In);

    wstring vdcId;                                            SERIALIZE_BASIC(vdcId);

    SERIALIZE_END_STRUCT(taggetVAppListOfVDC_In, taggetVAppListOfVDC_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVAppListOfVDC_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVAppListOfVDC_Out, taggetVAppListOfVDC_Out);

    INT nRet;                                                 SERIALIZE_BASIC(nRet);
    vector<tagVCloudVApp_Info_internal> vAppList;     SERIALIZE_COMPLEX_VECTOR(vAppList);
    INT count;                                                SERIALIZE_BASIC(count);

    SERIALIZE_END_STRUCT(taggetVAppListOfVDC_Out, taggetVAppListOfVDC_Out);
    STRUCT_XML_SERIALIZER;
};

//getVAppInOrg
struct taggetVAppInOrg_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVAppInOrg_In, taggetVAppInOrg_In);

    wstring vdcId;                                  SERIALIZE_BASIC(vdcId);
    wstring vAppName;                               SERIALIZE_BASIC(vAppName);

    SERIALIZE_END_STRUCT(taggetVAppInOrg_In, taggetVAppInOrg_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVAppInOrg_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVAppInOrg_Out, taggetVAppInOrg_Out);

    INT nRet;                                       SERIALIZE_BASIC(nRet);
    tagVCloudVApp_Info_internal vAppInfo;           SERIALIZE_COMPLEX(vAppInfo);

    SERIALIZE_END_STRUCT(taggetVAppInOrg_Out, taggetVAppInOrg_Out);
    STRUCT_XML_SERIALIZER;
};

//powerOnVApp
struct tagpowerOnVApp_In
{
    SERIALIZE_BEGIN_STRUCT(tagpowerOnVApp_In, tagpowerOnVApp_In);

    wstring vAppId;                         SERIALIZE_BASIC(vAppId);

    SERIALIZE_END_STRUCT(tagpowerOnVApp_In, tagpowerOnVApp_In);
    STRUCT_XML_SERIALIZER;
};

struct tagpowerOnVApp_Out
{
    SERIALIZE_BEGIN_STRUCT(tagpowerOnVApp_Out, tagpowerOnVApp_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagpowerOnVApp_Out, tagpowerOnVApp_Out);
    STRUCT_XML_SERIALIZER;
};

//powerOffVApp
struct tagpowerOffVApp_In
{
    SERIALIZE_BEGIN_STRUCT(tagpowerOffVApp_In, tagpowerOffVApp_In);

    wstring vAppId;                         SERIALIZE_BASIC(vAppId);

    SERIALIZE_END_STRUCT(tagpowerOffVApp_In, tagpowerOffVApp_In);
    STRUCT_XML_SERIALIZER;
};

struct tagpowerOffVApp_Out
{
    SERIALIZE_BEGIN_STRUCT(tagpowerOffVApp_Out, tagpowerOffVApp_Out);

    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagpowerOffVApp_Out, tagpowerOffVApp_Out);
    STRUCT_XML_SERIALIZER;
};

//getDatastore
struct taggetDatastore_In
{
    SERIALIZE_BEGIN_STRUCT(taggetDatastore_In, taggetDatastore_In);

    wstring datastoreId;                                   SERIALIZE_BASIC(datastoreId);

    SERIALIZE_END_STRUCT(taggetDatastore_In, taggetDatastore_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetDatastore_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetDatastore_Out, taggetDatastore_Out);

    INT nRet;                                              SERIALIZE_BASIC(nRet);
    tagVCloudDatastore_Info_internal datastoreInfo;        SERIALIZE_COMPLEX(datastoreInfo);

    SERIALIZE_END_STRUCT(taggetDatastore_Out, taggetDatastore_Out);
    STRUCT_XML_SERIALIZER;
};

//getESXHostListOfVDC
struct taggetESXHostListOfVDC_In
{
    SERIALIZE_BEGIN_STRUCT(taggetESXHostListOfVDC_In, taggetESXHostListOfVDC_In);

    wstring vdcId;                                   SERIALIZE_BASIC(vdcId);

    SERIALIZE_END_STRUCT(taggetESXHostListOfVDC_In, taggetESXHostListOfVDC_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetESXHostListOfVDC_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetESXHostListOfVDC_Out, taggetESXHostListOfVDC_Out);

    INT nRet;                                              SERIALIZE_BASIC(nRet);
    vector<tagVCloudESXHost_Info_internal> esxHostList;     SERIALIZE_COMPLEX_VECTOR(esxHostList);
    INT count;                                              SERIALIZE_BASIC(count);

    SERIALIZE_END_STRUCT(taggetESXHostListOfVDC_Out, taggetESXHostListOfVDC_Out);
    STRUCT_XML_SERIALIZER;
};

//getStorageProfileListOfVDC
struct taggetStorageProfileListOfVDC_In
{
    SERIALIZE_BEGIN_STRUCT(taggetStorageProfileListOfVDC_In, taggetStorageProfileListOfVDC_In);

    wstring vdcId;                                   SERIALIZE_BASIC(vdcId);

    SERIALIZE_END_STRUCT(taggetStorageProfileListOfVDC_In, taggetStorageProfileListOfVDC_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetStorageProfileListOfVDC_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetStorageProfileListOfVDC_Out, taggetStorageProfileListOfVDC_Out);

    INT nRet;                                              SERIALIZE_BASIC(nRet);
    vector<tagVCloud_StorageProfile_internal> storageProfileList;        SERIALIZE_COMPLEX_VECTOR(storageProfileList);
    INT count;                                              SERIALIZE_BASIC(count);

    SERIALIZE_END_STRUCT(taggetStorageProfileListOfVDC_Out, taggetStorageProfileListOfVDC_Out);
    STRUCT_XML_SERIALIZER;
};

//verifyVCloudInfo
struct tagverifyVCloudInfo_In
{
    SERIALIZE_BEGIN_STRUCT(tagverifyVCloudInfo_In, tagverifyVCloudInfo_In);

    tagVCloud_VerifyInfo_internal vCloudInfo;        SERIALIZE_COMPLEX(vCloudInfo);

    SERIALIZE_END_STRUCT(tagverifyVCloudInfo_In, tagverifyVCloudInfo_In);
    STRUCT_XML_SERIALIZER;
};

struct tagverifyVCloudInfo_Out
{
    SERIALIZE_BEGIN_STRUCT(tagverifyVCloudInfo_Out, tagverifyVCloudInfo_Out);

    INT nRet;                                              SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(tagverifyVCloudInfo_Out, tagverifyVCloudInfo_Out);
    STRUCT_XML_SERIALIZER;
};


//getVMInfoUnderDataCenter
struct taggetVMInfoUnderDataCenter_In
{
    SERIALIZE_BEGIN_STRUCT(taggetVMInfoUnderDataCenter_In, taggetVMInfoUnderDataCenter_In);

    wstring vmName;                          SERIALIZE_BASIC(vmName);
    wstring dcName;                          SERIALIZE_BASIC(dcName);

    SERIALIZE_END_STRUCT(taggetVMInfoUnderDataCenter_In, taggetVMInfoUnderDataCenter_In);
    STRUCT_XML_SERIALIZER;
};

struct taggetVMInfoUnderDataCenter_Out
{
    SERIALIZE_BEGIN_STRUCT(taggetVMInfoUnderDataCenter_Out, taggetVMInfoUnderDataCenter_Out);

    tagVM_Info_internal vmInfo;             SERIALIZE_COMPLEX(vmInfo);
    INT nRet;                               SERIALIZE_BASIC(nRet);

    SERIALIZE_END_STRUCT(taggetVMInfoUnderDataCenter_Out, taggetVMInfoUnderDataCenter_Out);
    STRUCT_XML_SERIALIZER;
};