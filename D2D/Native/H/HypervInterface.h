#pragma once

//#ifdef HYPERVMGR_EXPORTS
//#define HyperVMgr_API __declspec(dllexport)
//#else
//#define HyperVMgr_API __declspec(dllimport)
//#endif

#include "HyperVClusterInterface.h"
#include <map>

namespace HyperVManipulation
{
	enum HYPERV_NODE_TYPE_E{
		HYPERV_TYPE_STANDALONE = 0,
		HYPERV_TYPE_CLUSTER_PHYSICAL_NODE,
		HYPERV_TYPE_CLUSTER_VIRTUAL_NODE
	};

	enum MSVM_STATE
	{
		MSVM_STATE_UNKNOWN			= 0, //<sonmi01>2012-11-29 #edit vm in a editable VM state
		MSVM_STATE_ENABLED			= 2,
		MSVM_STATE_DISABLED			= 3,
		MSVM_STATE_RESET			= 10,
		MSVM_STATE_RESET_V2			= 11,
		MSVM_STATE_REBOOT_PAUSED	= 32768,
		MSVM_STATE_REBOOT_SUSPENDED = 32769,
		MSVM_STATE_STARTING			= 32770,
		MSVM_STATE_SNAPSHOTTING		= 32771,
		MSVM_STATE_SAVING			= 32773,
		MSVM_STATE_STOPPING			= 32774,
		MSVM_STATE_PAUSING			= 32776,
		MSVM_STATE_RESUMING			= 32777
	};

    //<huvfe01>2012-12-7 #VM operational status
    #define MSVM_OPERATIONAL_STATUS_MAX_INDEX        2
    enum MSVM_OPERATIONAL_STATUS_INDEX_0  
    {
        MSVM_OPERATIONAL_STATUS_OK                 = 2,
        MSVM_OPERATIONAL_STATUS_DEGRADED           = 3, //Not supported before Windows Server 2008 R2
        MSVM_OPERATIONAL_STATUS_PREDICTIVE_FAILURE = 5, //Not supported before Windows Server 2008 R2
        MSVM_OPERATIONAL_STATUS_STOPPED            = 10,
        MSVM_OPERATIONAL_STATUS_INSERVICE          = 11,
        MSVM_OPERATIONAL_STATUS_DORMANT            = 15
    };

    //<huvfe01>2012-12-7 #VM operational status   
    enum MSVM_OPERATIONAL_STATUS_INDEX_1     //Not supported before Windows Server 2008 R2           
    {
        MSVM_OPERATIONAL_STATUS_CREATING_SNAPSHOT  = 32768,
        MSVM_OPERATIONAL_STATUS_APPLYING_SNAPSHOT  = 32769,
        MSVM_OPERATIONAL_STATUS_DELETING_SNAPSHOT  = 32770,
        MSVM_OPERATIONAL_STATUS_WAITING_TO_START   = 32771,
        MSVM_OPERATIONAL_STATUS_MERGING_DISKS      = 32772,
        MSVM_OPERATIONAL_STATUS_EXPORTING_VM       = 32773,
        MSVM_OPERATIONAL_STATUS_MIGRATING_VM				= 32774,
		MSVM_OPERATIONAL_STATUS_MOVING_STORAGE_2012R2_VM	= 32778,		 // Windows 2012 R2
		MSVM_OPERATIONAL_STATUS_MOVING_STORAGE_2012_VM		= 32779          // Windows 2012
    };

	enum MSVM_HEALTH_STATE	//<sonmi01>2012-11-29 #edit vm in a editable VM state
	{
		MSVM_HEALTH_STATE_OK				= 5,
		MSVM_HEALTH_STATE_MAJOR_FAILURE		= 20,
		MSVM_HEALTH_STATE_CRITICAL_FAILURE	= 25
	};

	//<huvfe01>2014-1-13 #VM Generation
	typedef enum MSVM_VIRTUAL_SYSTEM_SUB_TYPE
	{
		MSVM_GENERATION_1 = 0,
		MSVM_GENERATION_2,
		MSVM_GENERATION_MAX
	}MSVM_GENERATION_E;

	//Windows 8.1 [desktop apps only]
	enum MSVM_BOOT_SOURCE_TYPE
	{
		MSVM_BOOT_SOURCE_TYPE_UNKNOWN = 0,
		MSVM_BOOT_SOURCE_TYPE_DRIVE = 1,
		MSVM_BOOT_SOURCE_TYPE_NETWORK = 2,
		MSVM_BOOT_SOURCE_TYPE_FILE = 3
	};

	enum AUTO_STARTUP_ACTION
	{
		STARTUP_ACTION_NONE = 0,
		STARTUP_ACTION_RESTART_IF_PREVOUS_RUNNING = 1,
		STARTUP_ACTION_ALWAYS_STARTUP = 2
	};

	enum AUTO_SHUTDOWN_ACTION
	{
		SHUTDOWN_ACTION_TURN_OFF = 0,
		SHUTDOWN_ACTION_SAVE = 1,
		SHUTDOWN_ACTION_SHUTDOWN = 2
	};

	enum IDE_CONTROLLER_INDEX
	{ 
		IDE_CONTROLLER_INDEX_IDE0 = 0,
		IDE_CONTROLLER_INDEX_IDE1 = 1,
		IDE_CONTROLLER_INDEX_MAX
	};

	enum MEDIA_HARD_DISK_TYPE
	{ 
		MEDIA_HARD_DISK_TYPE_VIRTUAL_DISK,
		MEDIA_HARD_DISK_TYPE_PHYSICAL_DISK
	};

	enum MEDIA_CD_DVD_TYPE
	{
		MEDIA_CD_DVD_TYPE_NONE,
		MEDIA_CD_DVD_TYPE_IMAGE_FILE,
		MEDIA_CD_DVD_TYPE_PHYSICAL_DRIVE
	};

	enum HARD_DISK_BUS_TYPE
	{
		HARD_DISK_BUS_TYPE_IDE = 0,
		HARD_DISK_BUS_TYPE_SCSI
	};

	enum HARD_DISK_CONTROLLER_ID_BASE
	{
		HARD_DISK_IDE_ID_BASE = 200,
		HARD_DISK_SCSI_ID_BASE = 1000
	};

	enum INTEGRATION_SERVICES_STATUS
	{
		INTEGRATION_SERVICES_STATUS_ERROR = -1,                // Cannot detect the status
		INTEGRATION_SERVICES_STATUS_NOT_INSTALLED = 0,         // One or more services are not installed
		INTEGRATION_SERVICES_STATUS_OUTOFDATE = 1,		       // One or more services are out of date
		INTEGRATION_SERVICES_STATUS_OK = 2,		               // Installed and up to date
		INTEGRATION_SERVICES_STATUS_TIMEOUT = 3,               // Not used
		INTEGRATION_SERVICES_STATUS_NOT_ENABLED = INTEGRATION_SERVICES_STATUS_ERROR,                   // One or more services are disabled
		INTEGRATION_SERVICES_STATUS_NEW_VERSION_IS_AVAILABLE = INTEGRATION_SERVICES_STATUS_OUTOFDATE,  // OK but new version is available

		INTEGRATION_SERVICES_STATUS_DEGRADED = INTEGRATION_SERVICES_STATUS_OUTOFDATE,            // The service is operating normally but the guest service negotiated a compatible communications protocol version
		INTEGRATION_SERVICES_STATUS_NON_RECOVERABLE_ERROR = 7,                                   // The guest does not support a compatible protocol version
		INTEGRATION_SERVICES_STATUS_NO_CONTACT = 12,              // The guest service is not installed or has not yet been contacted.
		INTEGRATION_SERVICES_STATUS_LOST_COMMUNICATION = 13       // The guest service is no longer responding normally.
	};

	typedef struct _HardDiskProperty
	{
		ULONG	ulDiskBusType;
		ULONG	ulDiskControllerId;
		ULONG	ulDiskPosition;
		WCHAR	szDiskPath[MAX_PATH];
	} HardDiskProperty;

	typedef struct _HYPERV_PFC_ITEMS
	{
		// Data consistency
		UINT nHasNotSupportedFileSystem;          // 1 if the VM has File Systems other than NTFS/ReFS
		UINT nHasNotSupportedDiskType;            // 1 if the VM has dynamic disk
		UINT nIsIntegrationServiceInBadState;     // 1 if the integration service is in a bad state
		UINT nIsScopeSnapshotEnabled;             // 1 if the ScopeSnapshot is enabled in the VM		
		UINT nHasStorageSpace;                    // 1 if the VM has storage space

		// Disk
		UINT nHasPhysicalHardDisk;                // 1 if the VM has physical disk drive
		UINT nHasDiskOnRemoteShare;               // 1 if the VM has disk image on remote share

		// VM Credential
		UINT nIsVMCredentialNotOK;                // 1 if the VM credential is NOT OK. (Cannot connect VM by WMI)

		// Extension for Data consistency
		UINT nIsDataConsistencyNotSupported;      // 1 if data consistency is NOT supported. 
		UINT nHasShadowStorageOnDifferentVolume;  // 1 if a volume's shadow copy storage is not located on itself
	} HYPERV_PFC_ITEMS;

	enum HYPERV_PFC_STATUS_CODE
	{
		HYPREV_PFC_STATUS_NO_PROBLEM                  = 0,       // item is not detected
		HYPREV_PFC_STATUS_PROBLEM_DETECTED            = 1,       // item is detected
		HYPREV_PFC_STATUS_ERROR_CANNOT_GET_VM_BY_GUID = 2,       // cannot check an item due to cannot get VM by GUID
		HYPREV_PFC_STATUS_ERROR_VM_IS_NOT_RUNNING     = 3,       // cannot check an item due to VM is not running
		HYPREV_PFC_STATUS_ERROR_INTEGRATION_SERVICE   = 4,       // cannot check an item due to integration service is not OK
		HYPERV_PFC_STATUS_ERROR_CANNOT_ACCESS_VM      = 5,       // cannot check an item due to VM is not accessible (privilege/network problems)
		HYPERV_PFC_STATUS_ERROR_CANNOT_CHECK_PROBLEM  = 6        // cannot check an item due to unknown reason
	};


	enum HYPERV_VM_AUTO_STARTUP_ACTION_CODE
	{
		HYPERV_VM_AUTO_STARTUP_ACTION_NONE = 2,
		HYPERV_VM_AUTO_STARTUP_ACTION_RESTART_IF_PREVIOUS_ACTIVE = 3,
		HYPERV_VM_AUTO_STARTUP_ACTION_ALWAYS_START = 4
	};

	enum HYPERV_VM_AUTO_SHUTDOWN_ACTION_CODE
	{
		HYPERV_VM_AUTO_SHUTDOWN_ACTION_TURN_OFF = 2,
		HYPERV_VM_AUTO_SHUTDOWN_ACTION_SAVE_STATE = 3,
		HYPERV_VM_AUTO_SHUTDOWN_ACTION_SHUTDOWN = 4
	};

	typedef struct _VOLUME_INFO
	{
		WCHAR strDriveLetter[MAX_PATH];         // drive letter. e.g. "F:", maybe NULL
		WCHAR strDriveName[MAX_PATH];           // drive name. e.g. "F:\"
		UINT64 nFreeSpace;                      // free space of the volume in bytes
		UINT64 nCapacity;                       // capacity of the volume in bytes
	} VOLUME_INFO;


	typedef struct _tagWin32_Service_Info
	{
		WCHAR szServiceName[MAX_PATH];
		WCHAR szStartMode[MAX_PATH]; //"Boot", "System", "Auto", "Manual", "Disabled"
		WCHAR szState[MAX_PATH];//"Stopped", "Start Pending", "Stop Pending", "Running", "Continue Pending", "Pause Pending", "Paused", "Unknown"
		BOOL  bStarted;
		ULONG ulProcessId;
	}WIN32_SERVICE_INFO_S;

class IInternalEthernetPort
{
public:
	virtual ~IInternalEthernetPort() = 0;
	virtual wstring GetName() const = 0;
	virtual wstring GetGuid() const = 0;
	virtual wstring GetPath() const = 0;
};

class IExternalEthernetPort
{
public:
	virtual ~IExternalEthernetPort() = 0;
	virtual wstring GetName() const = 0;
	virtual wstring GetGuid() const = 0;
	virtual wstring GetPath() const = 0;
	virtual bool IsBound() const = 0;

};

class IVirtualSwitch
{
public:
	virtual ~IVirtualSwitch() = 0;
	virtual wstring GetName() const = 0;
	virtual wstring GetGuid() const = 0;
	virtual wstring GetPath() const = 0;
};

class ISnapshot
{
public:
	virtual ~ISnapshot() = 0;

	virtual wstring GetName() const = 0;
	virtual wstring GetInstacneID() const = 0;
	virtual wstring GetGuid() const = 0;
	virtual wstring GetParentName() const = 0;
	virtual wstring GetParentGuid() const = 0;
	virtual time_t	GetTimestamp() const = 0;

	virtual DWORD GetDiskImages(vector<wstring> &vImage) = 0;
	virtual DWORD GetDiskFolder( wstring& wstrFolderPath ) = 0;
};

#define INTERGRATIONSERVICE_INVALID_ENABLED_STATE -1
class IIntergrationService
{
public:
	virtual void Release() = 0;
	virtual wstring GetElementName() = 0;
	virtual wstring GetInstanceType() = 0;
	virtual LONG GetEnabledState() = 0;
	virtual DWORD SetEnabledState(LONG lState) = 0;
};

class IVirtualMachine
{
public:
	virtual ~IVirtualMachine() = 0;

	virtual long	GetState() const = 0;
	virtual long	GetHealthState() const = 0; //<sonmi01>2012-10-18 ###???
	virtual long	GetStatusDescriptions(vector<wstring>& vecStatus) const = 0;

	virtual wstring GetName() const = 0;
	virtual wstring GetGuid() const = 0;
	virtual wstring GetPath() const = 0;
	virtual wstring GetNotes() = 0;

	virtual wstring GetDataFolder() = 0;
	virtual wstring GetSnapshotFolder() = 0;
	virtual DWORD   GetDiskImages(vector<wstring> &vDiskImage) = 0;
    virtual DWORD   GetKvpIntrinsicItems(vector<wstring>& vKvpItem) = 0;

	virtual wstring GetHostName() = 0;
	virtual DWORD   GetIPAddress(vector<wstring> &vIPAddress) = 0;
	virtual wstring GetOSName() = 0;
	virtual wstring GetOSVersion() = 0;
	virtual wstring GetIntegrationServicesVersion() = 0;

	// return: IS_STATUS_ERROR = -1, IS_STATUS_NOT_INSTALL = 0,	IS_STATUS_OUTOFDATE = 1, IS_STATUS_OK = 2;
	virtual long    GetIntegrationServicesStatus(wstring& strHyperVGuestInstallerVersion) = 0;

	virtual DWORD   GetHeartbeatStatus(DWORD& dwHeartbeatStatus) = 0;
	virtual DWORD   GetMemory(int& nSizeInMB) = 0; //<huvfe01> 2014-7-9 added for instance VM
	virtual DWORD   GetCpuNum(int& nCpuNum) = 0;
	// Operations
public:
	virtual DWORD	PowerOn() = 0;
	virtual DWORD	Shutdown() = 0;
	virtual DWORD	Reboot() = 0;
	virtual DWORD	Pause() = 0;
	virtual DWORD	Suspend() = 0;

	virtual DWORD	SetStartupAction(AUTO_STARTUP_ACTION action) = 0;
	virtual DWORD	SetShutdownAction(AUTO_SHUTDOWN_ACTION action) = 0;
	virtual DWORD	SetBootOrder() = 0;

	//<huvfe01>2014-1-13 #get/set boot source oder. Not supported until Windows 8.1 and Windows Server 2012 R2.
	virtual DWORD   GetBootSourceOrder(OUT vector<wstring>& vecBootSourceOder, OUT BOOL & bIsSecureBootEnabled) = 0;
	virtual DWORD   SetBootSourceOrder(IN const vector<wstring>& vecBootSourceOder, IN BOOL bIsSecureBootEnabled) = 0;

	virtual DWORD   GetGeneration(MSVM_GENERATION_E& enVMGeneration) = 0;
	virtual DWORD	DiskStorages(vector<wstring>& storages) = 0;
	// Snapshot Operation
public: 
	virtual DWORD	GetLastSnapshotGuid(wstring &strSnapshotGuid) = 0;
	virtual DWORD	TakeSnapshot(const wstring& strSnapshotName, const wstring& strSnapshotNotes, wstring& strSnapshotGuid) = 0;
	virtual DWORD	DeleteLastSnapshot() = 0;
	virtual DWORD	DeleteSnapshot(const wstring& strSnapshotGuid, bool waitJobDone = true) = 0;
	virtual DWORD	DeleteSnapshotSubtree(const wstring& strSnapshotGuid) = 0;

	virtual DWORD	RevertToLastSnapshot(wstring &strSnapshotGuid) = 0;
	virtual DWORD	RevertToSnapshot(const wstring& strSnapshotGuid) = 0;
	virtual DWORD	RenameSnapshot(const wstring& strSnapshotGuid, const wstring& strSnapshotName, const wstring& strSnapshotNotes = L"") = 0;

	virtual DWORD	GetSnapshotByGuid( const wstring& strSnapshotGuid, ISnapshot* &pSnapshot) = 0;
	virtual DWORD	GetSnapshotList(vector<ISnapshot*> &vSnapshots) = 0;
	virtual DWORD	MergeSnapshots(int method, vector<wstring> &vSnapshots) = 0;

	// Settings
public:
	virtual DWORD	ChangeFriendlyName( const wstring& strNewName ) = 0;
	virtual DWORD	SetNotes(const wstring& strNotes) = 0;

	virtual DWORD	SetMemorySize(int nSize) = 0;
	virtual DWORD   EnableDynamicMemory(int nSize, int nReservation, int nLimit) = 0; //<huvfe01>2013-11-7
	virtual DWORD	SetLogicalProcessorNum(int nNum) = 0;

	virtual DWORD   ListIDEControllers(vector<wstring> &vNames, vector<wstring> &vInstanceIDs) = 0;
	virtual DWORD	AttachIdeDisk(IDE_CONTROLLER_INDEX index, int nPosition, const wstring& strPath) = 0;
	virtual DWORD	DetachIdeDisk(IDE_CONTROLLER_INDEX index, int nPosition) = 0;
	virtual DWORD	ModifyIdeDisk(IDE_CONTROLLER_INDEX index, int nPosition, const wstring& strPath, bool bDeleteOriginalFile = false) = 0;
	virtual DWORD	HasIdeDisk(IDE_CONTROLLER_INDEX index, int nPosition, bool &bHas) = 0;

	virtual DWORD	ListScsiControls(vector<wstring> &vNames) = 0;
	virtual DWORD	ListScsiControlsEx(vector<wstring> &vNames, vector<wstring> &vInstanceIDs) = 0; //<huvfe01>2013-11-7
	virtual DWORD	AddScsiControl(const wstring& strName) = 0;
	virtual DWORD	RemoveScsiControl( const wstring& strName ) = 0;
	virtual DWORD	AttachScsiDisk(const wstring& strName, int nPosition, const wstring& strPath) = 0;
	virtual DWORD   AttachScsiDiskByInstID(const wstring& strInstID, int nPosition, const wstring& strPath) = 0; //<huvfe01>2013-11-7
	virtual DWORD	ModifyScsiDisk(const wstring& strName, int nPosition, const wstring& strPath, bool bDeleteOriginalFile = false) = 0;
	virtual DWORD	HasScsiDisk(const wstring& strName, int nPosition, bool &bHas) = 0;
	virtual DWORD	HasScsiDiskByInstID(const wstring& strInstID, int nPosition, bool &bHas) = 0;
	virtual DWORD	DetachScsiDisk(const wstring& strName, int nPosition) = 0;
	virtual DWORD	DetachScsiDiskByInstID(const wstring& strInstID, int nPosition) = 0;

	virtual DWORD	EnumIDEHardDisk(vector<HardDiskProperty>& vecHardDisk) = 0;
	virtual DWORD	EnumSCSIHardDisk(vector<HardDiskProperty>& vecHardDisk) = 0;

	virtual DWORD	EnumIDEHardDiskEx(vector<HardDiskProperty>& vecHardDisk) = 0;
	virtual DWORD	EnumSCSIHardDiskEx(vector<HardDiskProperty>& vecHardDisk) = 0;

	virtual DWORD   ListPhysicalDiskDrive(vector<wstring> &vNames, vector<wstring> &vHostResource, vector<wstring>& vPhysicalDiskDriveNames) = 0;
	virtual DWORD   ListVirtualHardDiskImage(vector<wstring> &vNames, vector<wstring> &vInstanceIDs, vector<wstring>& vPath) = 0;

	virtual DWORD	DetachAllDisks() = 0;

	virtual DWORD	AttachImageDvd(IDE_CONTROLLER_INDEX index, int nPosition, const wstring& strPath, bool attachISO = true) = 0;
	virtual DWORD	AttachImageDvdToScsi(const wstring& strScsiName, int nPosition, const wstring& strPath, bool attachISO = true) = 0;
	virtual DWORD   AttachImageDvdToScsiByInstID(const wstring& strInstID, int nPosition, const wstring& strPath, bool attachISO = true) = 0;
	virtual DWORD	AttachPhysicalDvd(IDE_CONTROLLER_INDEX index, int nPosition, const wstring& strPath) = 0;

	virtual DWORD	AddNetworkAdapter(const wstring& strName, const wstring& strMac = L"", const wstring& strVirtualSwitchGuid = L"", BOOL bIsStaticMac = FALSE, const wstring& strChannelInstanceGuid = L"") = 0;
	virtual DWORD	AddLegacyNetworkAdapter(const wstring& strName, const wstring& strMac = L"", const wstring& strVirtualSwitchGuid = L"", BOOL bIsStaticMac = FALSE) = 0;
	
	//<sonmi01>2012-10-18 ###???
	virtual DWORD	ListNetworkAdapters(vector<wstring> &vNames, vector<wstring> &vInstanceIDs) = 0;
	virtual DWORD	ListLegacyNetworkAdapters(vector<wstring> &vNames, vector<wstring> &vInstanceIDs) = 0;

	virtual DWORD	RemoveNetworkAdapter(const wstring& strInstanceID ) = 0;
	virtual DWORD	RemoveLegacyNetworkAdapter(const wstring& strInstanceID ) = 0;

    virtual DWORD   Get_MsvmOperationalStatus(vector<LONG> &vOperationalStatus) = 0; //<huvfe01>2012-12-7 #VM operational status
	virtual DWORD   Get_MsvmOperationalStatusEx(vector<LONG> &vOperationalStatus) = 0;
    virtual DWORD   WaitForMergingDisksDone(bool checkBeforeCreate) = 0; //<huvfe01>2012-12-26 for issue#151425

	//<huvfe01> DEV - restore settings like Integration Services
	virtual DWORD   GetAutoStartupAction(HYPERV_VM_AUTO_STARTUP_ACTION_CODE& dwActionCode, wstring& strActionDelay) = 0;
	virtual DWORD   SetAutoStartupAction(HYPERV_VM_AUTO_STARTUP_ACTION_CODE dwActionCode, const wstring& strActionDelay) = 0;
	virtual DWORD   GetAutoShutdownAction(HYPERV_VM_AUTO_SHUTDOWN_ACTION_CODE& dwActionCode) = 0;
	virtual DWORD   SetAutoShutdownAction(HYPERV_VM_AUTO_SHUTDOWN_ACTION_CODE dwActionCode) = 0;
	virtual DWORD   ListIntergrationServices(vector<IIntergrationService *>& vecServices) = 0;
};

class IHypervOperation
{
public: 
	virtual  ~IHypervOperation() = 0;

	virtual DWORD _GetVmList(vector<IVirtualMachine*> &vVms) = 0;

	virtual DWORD _CreateVm(const wstring& strVmName, const wstring& strExternalDataRoot, wstring& strVmGuid) = 0;

	virtual DWORD _CreateVmEx(IN const wstring& strVmName, IN const wstring& strExternalDataRoot, IN MSVM_VIRTUAL_SYSTEM_SUB_TYPE enSubType, OUT wstring& strVmGuid) = 0; //<huvfe01>2014-1-13 #VM Generation

	virtual DWORD _CreateVmEx2(IN const wstring& strVmName, IN const wstring& strNotes, IN const wstring& strExternalDataRoot, IN MSVM_VIRTUAL_SYSTEM_SUB_TYPE enSubType, OUT wstring& strVmGuid) = 0; //<yinji02>2014-10-09 #VM notes

	virtual DWORD _DestroyVm(const wstring& strVmGuid) = 0;

	virtual DWORD _RenameVM(const wstring& strVmGuid, const wstring& strNewName) = 0;

	virtual IVirtualMachine* GetVmByGuid( const wstring& strVmGuid ) = 0;

	virtual DWORD MountVHD(const wstring& strVhdFile,const ULONGLONG bootExtentOffset, wstring& bootVolumePath, bool forceOnlineDisk = false, bool* signatureChanged = NULL) = 0;

	virtual DWORD MountVHD_GetWinSysBootVol(const wstring& strVhdFile, 
											vector<wstring>& vBootVolPath,
											wstring& strSysVolPath) = 0;


	virtual DWORD UnmountVHD(const wstring& strVhdFile) = 0;
	virtual DWORD CreateChildVHD(const wstring& strVhdFile, const wstring& strChild) = 0;

	virtual DWORD AddPrivateNetwork(const wstring& strNetworkName, wstring& strNetworkGuid) = 0;
	virtual DWORD AddInternalNetwork(const wstring& strNetworkName, wstring& strNetworkGuid) = 0;
	virtual DWORD AddExternalNetwork(const wstring& strNetworkName, const wstring& strPhysicalAdapter, wstring& strNetworkGuid) = 0;
	virtual DWORD GetFreePhysicalNetworkAdapter(vector<IExternalEthernetPort*> &vEthernet) = 0;
	virtual DWORD GetVirutalSwitchList(vector<IVirtualSwitch* > &vVs ) = 0;

	virtual DWORD GetMaxCPUSForVm(unsigned int &cpus) = 0;
	virtual DWORD GetMaxRAMForVm(unsigned long long &ram) = 0;
	virtual DWORD GetAvailableRAMForVm(unsigned long long &ram) = 0;
	virtual DWORD GetNumberOfProcessors(UINT& numberOfLogicalProcessors, UINT& numberOfProcessors) = 0;

	virtual DWORD GetIntegrationServicesGuestInstallerVersion(wstring& guestInstallerVersion) = 0;

	// browser the folders of HyperV, set strParentFolder as "\\" or "" to get the volume list; set it as a specified folder to get its child folders
	virtual DWORD BrowseFolder(const wstring& strParentFolder, vector<wstring>& vChildFolders) = 0;

	// create folder on HyperV
	virtual DWORD CreateFolder(const wstring& strPath, const wstring& strFolder) = 0;

	// get Hyper-V settings
	virtual DWORD GetDefaultFolderOfVHD(wstring& strDefaultFolder) = 0;     // the default folder to store virtual hard disk files
	virtual DWORD GetDefaultFolderOfVM(wstring& strDefaultFolder) = 0;      // the default folder to store virtual machine configuration files

	virtual wstring GetHyperVServerOsVersion() = 0;

	virtual DWORD PFCCheckVM(const wstring& strVmGuid, const wstring& strVMUserName, const wstring& strVMPassword, HYPERV_PFC_ITEMS& status) = 0;

	virtual DWORD GetVolumes(vector<VOLUME_INFO>& vVolumeInfos) = 0; // get volume info list

	virtual DWORD GetSystemDrive(wstring& strSystemDrive) = 0;//<huvfe01>2014-10-15

	//cluster related
public:
	virtual HyperVCluster::IClusterOperation * GetClusterOperation() = 0;
	virtual HYPERV_NODE_TYPE_E GetHyperVServerType() = 0;
	virtual BOOL IsServiceInstalled(const wstring& strServiceName) = 0;//<huvfe01>2014-10-15 check service	
};

class INodeOperation
{
public: 
	virtual  ~INodeOperation() = 0;

	virtual DWORD BrowseFolder(const wstring& strParentFolder, vector<wstring>& vChildFolders) = 0;
	virtual DWORD CreateFolder(const wstring& strPath, const wstring& strFolder) = 0;
	virtual BOOL  IsFolderExist(const wstring& strFullPath) = 0;

	virtual DWORD RegGetValueString(const long hKey, const wstring& strSubKey, const wstring& strValueName, wstring& strValue) = 0;
	virtual DWORD RegGetValueDWORD(const long hKey, const wstring& strSubKey, const wstring& strValueName, DWORD& dwValue) = 0;

	virtual DWORD GetDiskTypes(vector<wstring> &vDiskTypes) = 0;               // MBR/GPT   Dynamic/Basic
	virtual DWORD GetFileSystemTypes(vector<wstring> &vFileSystemTypes) = 0;  // NTFS/FAT/FAT16/FAT32/ReFS/...

	virtual DWORD RunCommand( const wstring& commandLine, DWORD & outputPid ) = 0; //<sonmi01>2014-2-11 #hyper-v vm pre-post command using WMI
	virtual DWORD EnumProcess(const wstring & CommandLine, DWORD pid, LONG & processCount) = 0; //<sonmi01>2014-2-17 ###???
	virtual DWORD TerminateProcess(DWORD pid) = 0; //<sonmi01>2014-2-18 ###???
	virtual DWORD GetNumberOfProcessors(UINT& numberOfLogicalProcessors, UINT& numberOfProcessors) = 0;

	virtual DWORD GetWindowsServiceInfo(const wstring& strServiceName, OUT WIN32_SERVICE_INFO_S& ServiceInfo) = 0;

	virtual DWORD GetShadowCopyStorages(std::map<wstring, wstring>& mapShadowStorage, std::map<wstring, wstring>& mapVolume) = 0;
	virtual DWORD GetStorageSpaces(vector<wstring>& vDiskDrives) = 0;
	virtual DWORD IdentifyVM(const wstring& strHyperVHostName, const wstring& strVMName, const wstring& vmUuid) = 0;
};


HyperVMgr_API IHypervOperation* OpenHyperVOperation( const wstring &_wstrServer, const wstring &_wstrUser, const wstring &_wstrPwd );
HyperVMgr_API DWORD DelIHyperVOperation( IHypervOperation* pHypeV );
HyperVMgr_API DWORD DelISnapshot( ISnapshot* pSnapshot );
HyperVMgr_API DWORD DelIVirtualMachine( IVirtualMachine* pMachine);
HyperVMgr_API DWORD DelIInternalEthernetPort( IInternalEthernetPort* pEthernet);
HyperVMgr_API DWORD DelIExternalEthernetPort( IExternalEthernetPort* pEthernet);
HyperVMgr_API DWORD DelIVirtualSwitch(IVirtualSwitch* pVirtualSwitch);
HyperVMgr_API wstring GetHyperVMgrLastError();
HyperVMgr_API DWORD RemoveAllHyperVVMNetworks(IVirtualMachine* pIVirtualMachine); //<sonmi01>2012-10-19 ###???
HyperVMgr_API INodeOperation* OpenNodeOperation(const wstring &_wstrServer, const wstring &_wstrUser, const wstring &_wstrPwd); // connect to the WMI of a node
HyperVMgr_API INodeOperation* OpenNodeRegOperation(const wstring &_wstrServer, const wstring &_wstrUser, const wstring &_wstrPwd); // connect to the WMI of a node
HyperVMgr_API DWORD DelNodeOperation( INodeOperation* pNodeOperation );
HyperVMgr_API BOOL IsLocalMachine(const wstring& strHostName);
HyperVMgr_API DWORD GetVolumeIdByOnlineDisk(DWORD diskNumber, DWORD bootExtentOffset, wstring& bootVolumePath, bool& signatureChanged);
}

