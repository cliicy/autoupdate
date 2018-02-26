#pragma once
#include <string>
#include <map>
#include <vector>
using std::wstring;
using std::vector;
using std::map;

#define  VSB_HELPER_SVC_NAME					L"vsbhelpersvc"
#define  VM_HELPER_SVC_DISPLAYNAME				L"Arcserve UDP Instant VM Helper Service"
#define  VM_HELPER_SVC_DESCRIPTION              L"Install virtual machine integration tools and setup network resources on virtual machine startup"
#define  IVM_HELPER_SVC_NAME					L"ivmhelpersvc"
#define  IVM_HELPER_SVC_DISPLAYNAME				VM_HELPER_SVC_DISPLAYNAME

struct SDiskExtent
{
	ULONGLONG offset;
	wstring volumeGuid;
};

struct SDiskInfo
{
	unsigned long long totalSize;
	unsigned int diskNo;
	int partitionStyle;

	DWORD   dwSignature;		// for GPT disk, ignore this member
	wstring diskGuid;			// for MBR disk, ignore this member

	vector<SDiskExtent> extents;
};

struct SVolumeInfo
{
	wstring driverLetter;
	wstring guid;
	ULONG ulFlags;
	BOOL isDynamic;
};

DWORD WINAPI GetSystemHiveKeyEx(const wstring& strSystemDir, void** hive);
DWORD WINAPI GetSoftwareHiveKeyEx(const wstring& strSystemDir, void** hive);

// Change System inside VHD
DWORD WINAPI ChangeProductionHostName(const wstring& strSystemDir, const wstring& strNewName);
DWORD WINAPI PrepareD2DForFailover(const wstring& strSystemDir,const wstring& vmType);
DWORD WINAPI SetD2DServiceStart(const wstring& strSystemDir,const wstring& D2DServ);
DWORD WINAPI InjectIDEDriver(const wstring& strSystemDir);
DWORD WINAPI InjectIDEDriverEx(const wstring& strSystemDir, void* sysHive, void* softHive);
DWORD WINAPI InjectSCSIDriver(const wstring& strSystemDir);
DWORD WINAPI DeleteDynamicDiskGroup(const wstring& strSystemDir);
DWORD WINAPI DeleteDynamicDiskGroupEx(const wstring& strSystemDir, void* hive);
DWORD WINAPI InjectBCDFile(const wstring& strEFISystemVolume, const wstring& sessionFolder);
DWORD WINAPI InjectScript(const wstring& strSystemDir,const wstring& key, const wstring& value);
DWORD WINAPI DisableShutDownEventTracker(const wstring& strSystemDir);
DWORD WINAPI DisableShutDownEventTrackerEx(const wstring& strSystemDir, void* hive);
DWORD WINAPI DisableDynamicUpdate(const wstring& strSystemDir, void* hive, bool& valueChanged);
BOOL WINAPI GetGuestID(wstring& strGuestID);
DWORD WINAPI RVCM_InjectServiceToHive(const vector<wstring> & SrcFiles,
									  const wstring& strSystemDir,  //<sonmi01>2012-8-13 ###???
								 LPCTSTR ImagePath, 
								 const vector<wstring> & DependOnService, 
								 LPCTSTR ServiceName = VSB_HELPER_SVC_NAME,
								 DWORD WOW64 = 1, 
								 LPCTSTR DisplayName = VM_HELPER_SVC_DISPLAYNAME, 
								 LPCTSTR Description = VM_HELPER_SVC_DESCRIPTION,
								 DWORD ErrorControl = SERVICE_ERROR_NORMAL, 
								 LPCTSTR ObjectName = L"LocalSystem", 
								 DWORD Start = SERVICE_AUTO_START, 
								 DWORD Type = SERVICE_WIN32_OWN_PROCESS );


DWORD WINAPI Ivm_InjectHelperServiceToHive(
								const wstring& strSystemDir,
								LPCTSTR ImagePath,
								const vector<wstring> & DependOnService,
								LPCTSTR ServiceName = IVM_HELPER_SVC_NAME,
								DWORD WOW64 = 1,
								LPCTSTR DisplayName = IVM_HELPER_SVC_DISPLAYNAME,
								LPCTSTR Description = VM_HELPER_SVC_DESCRIPTION,
								DWORD ErrorControl = SERVICE_ERROR_NORMAL,
								LPCTSTR ObjectName = L"LocalSystem",
								DWORD Start = SERVICE_AUTO_START,
								DWORD Type = SERVICE_WIN32_OWN_PROCESS);

DWORD WINAPI Ivm_InjectHelperServiceToHiveEx(
	const wstring& strSystemDir,
	LPCTSTR ImagePath,
	const vector<wstring> & DependOnService,
	LPCTSTR ServiceName = IVM_HELPER_SVC_NAME,
	DWORD WOW64 = 1,
	LPCTSTR DisplayName = IVM_HELPER_SVC_DISPLAYNAME, 
	LPCTSTR Description = VM_HELPER_SVC_DESCRIPTION,
	DWORD ErrorControl = SERVICE_ERROR_NORMAL,
	LPCTSTR ObjectName = L"LocalSystem",
	DWORD Start = SERVICE_AUTO_START,
	DWORD Type = SERVICE_WIN32_OWN_PROCESS,
	void* hive = NULL);

// platform_type 0 hyperv
// platform_type 1 esx/vsphere
DWORD WINAPI RVCM_EnableVirtualPlatformServices(const wstring& strSystemDir, UINT32 platform_type, bool bEnable);

DWORD WINAPI RVCM_EnableVirtualPlatformServicesEx(const wstring& strSystemDir, UINT32 platform_type, bool bEnable, void* sysHive, void* softHive);

DWORD WINAPI DisableD2DAgentService(const wstring& strSystemDir, bool& serviceExists);

DWORD WINAPI DisableD2DAgentServiceEx(const wstring& strSystemDir, bool& serviceExists, void* hive);


// Adjust the drive letter of new added virtual CD-ROM Drive Letter
// So that it will reuse the original CD-ROM drive letter or use a new one, and will not occupy the
// drive letter used by volumes reside on hard disk.
// @nIDEController -  the controller index of IDE
// @nLocation      -  the location index of IDE
// return 0 success, others fail.
DWORD WINAPI AdjustVirtualCDDriveLetter_HyperV(const wstring& strSystemDir, int nIDEController, int nLocation);

// Create ISO Image
DWORD WINAPI CreateIsoImage(const wstring& strFolder, const wstring& strVolumeName, const wstring& strImageFile);

//Get host information
bool WINAPI IsHyperVRole();
DWORD WINAPI GetHostProcessorCount( unsigned long &count);
WORD WINAPI GetHostCPUArchitecture();
DWORD WINAPI GetHostPhysicalMemory( unsigned long long &size );
DWORD WINAPI GetHostIsDHCPEnabed(const wstring& strNetworkAdapterDeviceId, bool &isDHCP);
DWORD WINAPI GetHostDNSServerSetting(const wstring& strNetworkAdapterDeviceId, bool &isAutoDNS, vector<wstring> &vecDnsServer);
DWORD WINAPI GetHostIPSetting(const wstring& strNetworkAdapterDeviceId, vector<wstring> &vecIP, vector<wstring> &vecMask);
DWORD WINAPI GetHostGatewaySetting(const wstring& strNetworkAdapterDeviceId, vector<wstring> &vecGateway);
DWORD WINAPI GetHostMac(const wstring& strNetworkAdapterDeviceId, wstring &strMac);

// If >= designated version, return TRUE, else FALSE.
// for example, to check if >= w2k8sp2, call HA_IsHostOSGreaterEqual(6, 0, 2, 0)
BOOL  WINAPI HA_IsHostOSGreaterEqual(unsigned long dwMajor,
                                     unsigned long dwMinor,
                                     unsigned short usServicePackMajor,
                                     unsigned short usServicePackMinor);

// Set Host Network Settings
std::map<std::wstring, std::wstring> WINAPI GetHostAdapterList();
DWORD WINAPI EnableHostDHCP(const wstring& strAdapterName);
DWORD WINAPI EnableHostStatic(const wstring& strAdapterName, const vector<wstring>& vIPAddress, const vector<wstring>& vMask);

DWORD WINAPI EnableHostDNS(const wstring& strAdapterName);
DWORD WINAPI SetHostDNSDomain(const wstring& strAdapterName, const wstring& strDNSDomain);
DWORD WINAPI SetHostDNSServerSearchOrder(const wstring& strAdapterName, const vector<wstring>& vDNSServerSearchOrder);
DWORD WINAPI SetHostDNSSuffixSearchOrder(const wstring& strAdapterName, const vector<wstring>& vDNSDomainSuffixSearchOrder);
DWORD WINAPI SetHostDynamicDNSRegistration(const wstring& strAdapterName, bool bFullDNSRegistrationEnabled, bool bDomainDNSRegistrationEnabled);

DWORD WINAPI SetHostGateways(const wstring& strAdapterName, const vector<wstring>& vGateway, const vector<unsigned short>& vCostMetric);
DWORD WINAPI SetHostWINSServer(const wstring& strAdapterName, const wstring& strWINSPrimaryServer, const wstring& strWINSSecondaryServer);

DWORD WINAPI InstallIntegrationService(bool quiet, bool restart);
DWORD WINAPI InstallVMwareTools();
DWORD WINAPI GetSystemDiskSignature(DWORD& dwSignature);

//get disk and volume infomation
DWORD WINAPI GetOnlineDisks(vector<SDiskInfo> &disks);
DWORD WINAPI GetOnlineVolumes(vector<SVolumeInfo> &volumes);
DWORD WINAPI GetOnlineDisksAndVolumes(vector<SDiskInfo> &disks, vector<SVolumeInfo> &volumes);


/**
* Get the VDDK version
* @nVerHigh - major version
* @nVerLow - minor version
* @nVerSub - sub version
* For example, if VDDK version is 1.2.0, the out par will be:
nVerHigh == 1, nVerLow == 2, nVerSub == 0

return 0 success, others fail.
*/
int WINAPI GetVddkVersion(unsigned long& nVerHigh, unsigned long& nVerLow, unsigned long& nVerSub);

int WINAPI GetOffcopySize_HyperV(const wchar_t* pwszRoot, __int64& llTotalSize);

int WINAPI AdjustVolumeBootCode_HyperV(const wchar_t* pwszVmGuid, const wchar_t* pwszSnapGuid);

/**
* For custom port support !!!
* For snapshot NOW, Get the full local copy path for adrconfigure.xml.
* @pwszVmName       [in] - the vm name
* @pwszVmGuid       [in] - the vm GUID
* @wszPath          [out]- Save the full path of the adrconfigure.xml's local copy.
* @nSizeInWord      [in] - the size of buf pointed by wszPath.
* return value:
0 - success
ERROR_INSUFFICIENT_BUFFER - the input buffer is not enough
otherss - fail
If wszPath == NULL or nSizeInWord is not enough, will return ERROR_INSUFFICIENT_BUFFER, and nSizeInWord is the 
size needed.

*/
int WINAPI GetDrInfoLocalCopyPathForSnapNow_HyperV(const wchar_t* pwszVmName, const wchar_t* pwszVmGuid,
                                            wchar_t* wszPath, int& nSizeInWord);

int WINAPI GetAdrInfoCLocalCopyPathForSnapNow_HyperV(const wchar_t* pwszVmName, const wchar_t* pwszVmGuid,
	wchar_t* wszPath, int& nSizeInWord);

int WINAPI GetIVMAdrConfigLocalCopyPathForSnapNow_HyperV(const wchar_t* pwszVmName, const wchar_t* pwszVmGuid,
	wchar_t* wszPath, int& nSizeInWord);

/**
* represent a folder, 
* if is a share folder, pwszUser and pwszPwd is the access username and password.
* else if is local folder, pwszUser and pwszPwd should be NULL.
*/
typedef struct _t_conn_info
{
    wchar_t*    pwszDir;
    wchar_t*    pwszUser;
    wchar_t*    pwszPwd;
} ST_CONN_INFO;


/**
* To get the size of disk that D2D file represented to send.
* @pstRoot      [in] - the root of end session
* @nStartSessNo [in] - the start session No.
* @nEndSessNo   [in] - the end session No. If not smart copy, nStartSessNo should be equal to nEndSessNo.
* @pwszD2DName  [in] - the D2D file name, such as disk2535102234.D2D.
* @pllSize      [out]- the size to send, in bytes.    
* If the session path is \\baide02\D$\test52_r2\arc\VStore\S0000000001, the pstRoot->pwszDir should be
* \\baide02\D$\test52_r2\arc
* return value - 0 success, others fail.
*/
int WINAPI GetDiskSizeToSend(ST_CONN_INFO* pstRoot, int nStartSessNo, int nEndSessNo, 
                            const wchar_t* pwszD2DName, int nBackupDescType, __int64* pllSize);


// chefr03, SMART_COPY_BITMAP

// Destroy a directory and all its contents
DWORD WINAPI DestroyDirectory(PCWSTR pwszDirectory);

// Create a Bitmap file according to the specified D2D file, it is invoked by CreateBitmap4Session()
// @pwszSource			[in]	-	The directory which the .D2D file locates
// @pwszTarget			[in]	-	The directory which the .BITMAP file locates, the new .BITMAP file saves here
// @pwszD2DBasename		[in]	-	The base file name of .D2D without leading path
// Example:
//	pwszSource:			D:\BackupDestination\hostname\VStore\S0000000001
//	pwszTarget:			D:\BackupDestination\hostname\Bitmap\S0000000001
//  pwszD2DBasename:	disk1094926659.D2D
DWORD WINAPI CreateBitmap4D2DFile(PCWSTR pwszSource, PCWSTR  pwszTarget, PCWSTR  pwszD2DBasename, int nBackupDescType);

// Create bitmap files for specified session, it is invoked by CreateSessionBitmap(), and it invokes CreateBitmap4D2DFile()
DWORD WINAPI CreateBitmap4Session(PCWSTR pwszSessionPath, PCWSTR pwszBitmapPath, PCWSTR pwszSessionName, int nBackupDescType);

//<huvfe01>2013-1-23 for defect #170195: RVCM - A full conversion is launched due to no bitmap is generated
// Create bitmap files for a backup destination, it is invoked by CreateSessionBitmap() and invokes CreateBitmap4Session()
// Create bitmap files from the latest session to the nearest full session but not including the full session.
//@pwszBackupRootDest    Original backup node root path
//@pwszBitmapRootDest    New backup node root path where bitmap file to be created, if it is input as NULL, will use pwszBackupRootDest as Bitmap root path
DWORD WINAPI CreateBitmap4BackupDest(PCWSTR pwszBackupRootDest, int nBackupDescType, PCWSTR pwszBitmapRootDest = NULL);

// Create bitmap files for all sessions, it invokes CreateBitmap4BackupDest() for each backup destination
int   WINAPI CreateSessionBitmap(PCWSTR pwszCurrentBackupDest, int nBackupDescType, PCWSTR pwszNewBitmapDest = NULL);

// Create bitmap files for designated sessions which ( dwSessBegin <= session << dwSessEnd)
// If dwBegin == dwEnd == 0, only create bitmap for the newest session.
// @bRegen - If force regenerate the bitmaps for session. If FALSE, it will NOT generate bitmap
//           for the sessions that already have the bitmap.
int   WINAPI CreateSessionBitmapEx(PCWSTR pwszCurrentBackupDest, PCWSTR pwszNewBitmapDest, DWORD dwBegin, DWORD dwEnd, BOOL bRegen, int nBackupDescType);


// Get all backup destinations according to currently backup destination
DWORD WINAPI GetBackupDestList(PCWSTR pwszCurrentBackupDest, vector<wstring>& vBackupDestList);

// Check if bitmap is available for a specified session under specified backup destination
DWORD WINAPI CheckSessionBitmap(PCWSTR pwszBackupDest, PCWSTR pwszSessionName, BOOL& ifSession, BOOL& ifBitmap, BOOL& ifMatch);

// Remove session bitmaps from specified backup destination according to the filter
// The filter is the session name or "", the latter one will remove all bitmaps under this backup destination
// This APi is invoked by DeleteSessionBitmap()
DWORD WINAPI DeleteBitmap4BackupDest(PCWSTR pwszBackupDest, PCWSTR pwszFilter);

// Remove specified session bitmap, it will remove all bitmaps for this session and older sessions
// If pwszSessionName is "", all bitmaps will be removed
// It will traverse all backup destinations, and invokes DeleteBitmap4BackupDest() to remove bitmaps accordingly
DWORD WINAPI DeleteSessionBitmap(PCWSTR pwszCurrentBackupDest, PCWSTR pwszSessionName);

// Get file list according to specified pattern
DWORD WINAPI GetFileListByPattern(PCWSTR pwszPattern, vector<wstring>& vFileList);

// Get bitmap disk list by session number and current backup destination
DWORD WINAPI GetBitmapDiskList(PCWSTR pwszCurrentBackupDest, PCWSTR pwszSessionName, vector<wstring>& vBitmapDiskList);

// Check if is full D2D before merged by the bitmap file.
DWORD WINAPI CheckIfIsFullD2DBeforeMerged(const wchar_t* pwszBitmapFile, bool& bFull);

// Get all bitmap list
DWORD WINAPI GetSessionBitmapList(PCWSTR pwszCurrentBackupDest, vector<wstring>&vSessionBitmapList);
// chefr03, SMART_COPY_BITMAP

DWORD WINAPI DetectAndRemoveObsolteBitmap(PCWSTR pwszCurrentBackupDest, PCWSTR pwszBeginSession, PCWSTR wszEndSession);

// Read bitmap contents according to specified bitmap disk
namespace HaVhdUtility
{
struct IDiskBmpReader;
};
DWORD WINAPI ReadDiskBitmap(const wchar_t* pwszBitmapFile, HaVhdUtility::IDiskBmpReader** ppReader);

DWORD WINAPI GetBitmapReader_IncsFromFull(const wchar_t* pwszFullVhd, unsigned long ulSessBegin, 
                                          unsigned long ulSessEnd, HaVhdUtility::IDiskBmpReader** ppBmpReader);


/*added by zouyu01 on 2010/9/10
*retrieve ip address directly from dns not in cache.
*@Return: 0 for success, otherwise, system error code will be returned.
*/
int WINAPI GetIpAddressFromDns(const std::wstring &strDns, std::vector<std::wstring> &vIp);

DWORD WINAPI RebootSystem( bool force );

/**
@pwszRoot [in] - the session root
@nSessNum [in] - the session number
@pnEncType[out]- point to the session encrypt type. If is 0, means non-encrypt.
@wstrPwd  [out]- the session's password if session is encrypted.
return value:
0 success, others fail.
*/
int WINAPI GetD2DSessionPwd(const wchar_t* pwszRoot, int nSessNum, int nBackupDescType, int* pnEncType, std::wstring& wstrPwd);

/**
* Get the destination session root that the input wstrMarkFile exists.
* Call this API at Monitor side.
* return value:
* 0 success, wstrSessRoot is the root found.
* others fail. and ERROR_FILE_NOT_FOUND means cannot find the root which contain wstrMarkFile.
*/
int WINAPI GetDirOfMarkFile_HyperV(const std::wstring& wstrLastRoot, const std::wstring& wstrMarkFile,
                                   std::wstring& wstrSessRoot);
int WINAPI GetDirOfMarkFileByVm_HyperV(const std::wstring& wstrLastRoot, const std::wstring& wstrMarkFile,
                                   std::wstring& wstrSessRoot);


// Add offlinecopy lock for D2D sessions
// if is not smart copy, let nSessBegin == nSessEnd 
#define HA_LOCK_D2D_SESSION_BASE				0xF0000000
#define HA_LOCK_D2D_SESSION_FAIL_MERGE			(HA_LOCK_D2D_SESSION_BASE | 0x1)
DWORD WINAPI HA_LockD2DSessions(/*IN*/const wchar_t* pwszSessRoot, /*IN*/int nSessBegin,
                                /*IN*/int nSessEnd, /*OUT*/HANDLE* phLock, BOOL bRemoteVCM); //<sonmi01>2012-9-11 ###???
DWORD WINAPI HA_UnlockCTF2(HANDLE hLock);
void  WINAPI HA_UnlockD2DSessions(HANDLE hLock);

/**
* Check if VMtools is installed and running.
* return 0 success, others failed.
*/
int WINAPI CheckVMToolStatus(bool& bInstalled, bool& bRunning);

/**
* Check If a file or directory is compressed or encrypted by FileSystem.
*/
int WINAPI CheckFileCryptCmprsAttr(const wchar_t* pwszFilePath, bool& bCmprsed, bool& bCrypted);


/**
* Check if a file or directory is resides on a compressed volume
* @pwszFilePath [in] - the input file or dir. such as C:\test\test.xml
*/
int WINAPI CheckIfFileOnCmprsVol(const wchar_t* pwszFilePath, bool& bCmprsVol);


/**
* Call this before delete the bootable and non-bootable snapshot.
* @pwszVmGuid [ in]- VM GUID
* @pwszS1     [ in]- GUID of snapshot SN-1  
* @pwszS1Boot [ in]- GUID of snapshot SN-1_bootable
* @pwszS2     [ in]- GUID of snapshot SN
* @phCtx      [out]- HANDLE retrieved
*/
int WINAPI BeginDeleteSnapshot_HyperV(const wchar_t* pwszVmGuid, const wchar_t* pwszS1, const wchar_t* pwszS1Boot,
                                      const wchar_t* pwszS2, HANDLE* phCtx);

/**
* Call this after finished delete the snapshots
* @hCtx      [ in]- HANDLE retrieved by BeginDeleteSnapshot_HyperV
* @bSucc     [ in]- If delete snapshot operation is succeed.
*/
int WINAPI EndDeleteSnapshot_HyperV(HANDLE hCtx, bool bSucc);









//HADT
struct _D2D_ENCRYPTION_INFO;
namespace HADT
{

    int CheckIfIsFullD2DFile(const std::wstring& wstrVhdPath, struct _D2D_ENCRYPTION_INFO* pEncInfo, bool& bFullVhd);

    // Get the bitmap file path by the disk name and a set of bmppath of one session
    // @vBmpPath [in] - the vector of bitmap file path return by GetBitmapDiskList
    // @wstrDiskName [in] - the name the disk dile name, such as disk23335102234.d2d
    // 
    std::wstring FindBmpPathByVhdName(const std::vector<std::wstring>& vBmpPath, const std::wstring& wstrDiskName);
}


int WINAPI CheckIfExistDiskLargerThan2T(bool& bExist);


/**
* 2 APIs to read/write the VCM configurations.
* Those 2 API is added to the Manual Virtual Conversion.
*/
typedef struct _VCM_CFG
{
    bool    bManual;            // if is manual VCM
    wstring wstrRWFolder;       // The R/W folder to store the idx file if is manual VCM.
} VCM_CFG;

int WINAPI ReadVCMConfigure(VCM_CFG& cfg);
typedef int (WINAPI * PFN_ReadVCMConfigure)(VCM_CFG& cfg);

int WINAPI WriteVCMConfigure(VCM_CFG& cfg);
typedef int (WINAPI * PFN_WriteVCMConfigure)(VCM_CFG& cfg);


// For manual VCM, Get the R/W folder of this session.
int WINAPI MVCM_GetSessionRWFolder(const wstring& wstrSessPath, wstring& wstrRWFolder);
typedef int (WINAPI * PFN_MVCM_GetSessionRWFolder)(const wstring& wstrSessPath, wstring& wstrRWFolder);


/**
* the VSS snapshot management for MVCM
*
*/
struct IVcmSnapshotSet
{
    virtual void Release() = 0;

    // Get the snapshot dev object name of original volume path
    virtual int QuerySnapshotDevName(const wstring& strOrigVolName, wstring& strSnapshotDevName) = 0;
};

struct IVcmVssMgr
{
    virtual void Release() = 0;

    virtual int Init(DWORD dwFlag, bool bPersistent) = 0;
    virtual int CreateSnapshotSet(const vector<wstring>& vVol, wstring& strSnapshotSetID) = 0;
    virtual int DeleteSnapshotSet(const wstring& strSnapshotSetID) = 0;

    virtual int GetSnapshotSetByGuid(const wstring& strSnapshotSetID, IVcmSnapshotSet* &pSnapshotSet) = 0;
};

int WINAPI MVCM_CreateVssMgr(IVcmVssMgr** ppMgr);
typedef int (WINAPI * PFN_MVCM_CreateVssMgr)(IVcmVssMgr** ppMgr);

// Input the session root, output the list of intact sessions
int MVCM_OpenIntactSessionListHandle(const wchar_t* pwszRoot, void** pHandle);
int MVCM_QueryIntactSessionList(void* handle, int* pSessList, int nCntIn, int* nCntOut);
void MVCM_CloseIntactSessionListHandle(void* handle);
