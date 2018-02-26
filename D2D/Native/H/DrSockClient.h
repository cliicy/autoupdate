/******************************************************************************
*
*        filename  :      DrSockClient.h
*        created   :      2010/04/01
*        Written by:      baide02
*        comment   :      the header of DRSockClient, it's used to get DR info 
                          from remote socket server.
*
******************************************************************************/
#ifndef _DR_SOCKET_CLIENT_H_
#define _DR_SOCKET_CLIENT_H_

#include "HAVmIoIntf.h"

#ifdef DRSOCKCLIENT_EXPORTS
#define DRSOCKCLIENT_API extern "C" __declspec(dllexport)
#else
#define DRSOCKCLIENT_API extern "C" __declspec(dllimport)
#endif


namespace HaVhdUtility
{
//===================================================================================
//
// Operation for HyperV
//
//===================================================================================

typedef struct _t_ha_server_info
{
    const wchar_t* pwszName; // "hostname@ip" or "hostname" or "@ip"
    const wchar_t* pwszPort;
    const wchar_t* pwszUser; // username for log on
    const wchar_t* pwszPwd;  // password for log on
} ST_HASRV_INFO, *PST_HASRV_INFO;



/************************************************************************/
/*                                                                      */
/************************************************************************/

#define VDISK_FLAG_SNAPSHOT     0x00000001      // IF read FULL image
#define VDISK_FLAG_STANDARDVHD  0x00000002      // NOT ALL VHDs which combine the disk 
                                                // are in D2D session, that is to say, some of those
                                                // VHD are normal standard VHD, not plain D2D

/** 
* Create IVDisk
* @pstSrvInfo     [in] - the server info of the socket server
* @pwszFilePath   [in] - the full path of disk file (*.VHD)
* @flag           [in] - the macro with VDISK_FLAG_ prefix, such as VDISK_FLAG_SNAPSHOT
* @ppDisk         [out]- the IVDisk* returned
* 
* return value:
* 0       success
* others  fail
*/
DRSOCKCLIENT_API 
int CreateVDisk(const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszFilePath, 
                unsigned long flag, IVDisk** ppDisk);
typedef int (*PFN_CreateVDisk) (const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszFilePath, 
                                unsigned long flag, IVDisk** ppDisk);

/**
* Get the DR info of snapshot, save it at local.
* @pstSrvInfo         [in] - the server info of the socket server.
* @pwszVmGuid         [in] - the virtualmachine's GUID to get DR info.
* @pwszSnapshotGuid   [in] - the GUID of snapshot to get DR info.
* @pwszDrInfoPath     [in] - the local file path where we want to save the DR info. such as "D:\test.xml"

* return value:
* 0       success
* others  fail
*/
DRSOCKCLIENT_API
int GetDrInfoFile(const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszVmGuid, 
                  const wchar_t* pwszSnapshotGuid, const wchar_t* pwszDrInfoPath);
typedef int (*PFN_GetDrInfoFile) (const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszVmGuid, 
                                  const wchar_t* pwszSnapshotGuid, const wchar_t* pwszDrInfoPath);

/**
* Get the DR info of snapshot NOW
*/
DRSOCKCLIENT_API
int GetDrInfoFileForSnapshotNow(const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszVmGuid, 
                    const wchar_t* pwszSnapshotGuid, const wchar_t* pwszDrInfoPath);
typedef int (*PFN_GetDrInfoFileForSnapshotNow) (const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszVmGuid, 
                                    const wchar_t* pwszSnapshotGuid, const wchar_t* pwszDrInfoPath);

/**
* Get the BCD info of snapshot, save it at local.
* @pstSrvInfo         [in] - the server info of the socket server.
* @pwszVmGuid         [in] - the virtual machine's GUID to get DR info.
* @pwszSnapshotGuid   [in] - the GUID of snapshot to get DR info.
* @pwszDrInfoPath     [in] - the local file path where we want to save the DR info. such as "D:\test.xml"

* return value:
* 0       success
* others  fail
*/
DRSOCKCLIENT_API
	int GetAdrInfoCFile(const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszVmGuid, 
	const wchar_t* pwszSnapshotGuid, const wchar_t* pwszDrInfoPath);
typedef int (*PFN_GetAdrInfoCFile) (const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszVmGuid, 
	const wchar_t* pwszSnapshotGuid, const wchar_t* pwszDrInfoPath);

#define VVOL_FLAG_SNAPSHOT      0x00000001      // IF read FULL image
#define VVOL_FLAG_NO_CTF        0x00000002      /* For volume which is not from D2D session, thus not
                                                   have CTF file (which contains volume bitmap)*/
/**
* Create IVVol by volume GUID and (VM GUID && snapshot GUID).
* @pstSrvInfo         [in] - the server info of the socket server.
* @pwszVmGuid         [in] - VM GUID
* @pwszSnapshotGUID   [in] - snapshot GUID
* @pwszVolGuid        [in] - the GUID of volume
* @ulFlag             [in] - flag, the macro with VVOL_FLAG_ prefix, such as VVOL_FLAG_SNAPSHOT
* @ppVol              [out]- the IVVol* returned
* 
* return value:
* 0       success
* others  fail
*/
DRSOCKCLIENT_API
int CreateVVolumeEx(const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszVmGuid, 
                    const wchar_t* pwszSnapshotGuid, const wchar_t* pwszVolGuid, 
                    unsigned long ulFlag, IVVol** ppVol); 
typedef int (*PFN_CreateVVolumeEx) (const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszVmGuid, 
                                    const wchar_t* pwszSnapshotGuid, const wchar_t* pwszVolGuid, 
                                    unsigned long ulFlag, IVVol** ppVol); 

/**
* Create the IVVolDiff for the volume between 2 snapshot
* @pstSrvInfo         [in] - the server info of the socket server.
* @pwszVmGuid         [in] - Virtual machine's GUID
* @pwszBaseSnapGuid   [in] - GUID of the base snapshot
* @pwszCurrSnapGuid   [in] - GUID of the current snapshot
* @pwszVolGuid        [in] - the GUID of volume
* @ulFlag             [in] - flag, reserved now
* @ppVolDiff          [out]- the difference for volume(represent by pwszVolGuid) between the 2 snapshot.
* 
* return value:
* 0       success
* others  fail
*/
DRSOCKCLIENT_API 
int CreateVVolumeDiff(const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszVmGuid, 
                      const wchar_t* pwszBaseSnapGuid, const wchar_t* pwszCurrSnapGuid,
                      const wchar_t* pwszVolGuid, unsigned long ulFlag, IVVolDiff** ppVolDiff); 
typedef int (*PFN_CreateVVolumeDiff)(const ST_HASRV_INFO* pstSrvInfo, const wchar_t* pwszVmGuid, 
                                     const wchar_t* pwszBaseSnapGuid, const wchar_t* pwszCurrSnapGuid,
                                     const wchar_t* pwszVolGuid, unsigned long ulFlag, IVVolDiff** ppVolDiff); 



//===================================================================================
//
// Operation for VMware
//
//===================================================================================
typedef struct _t_esx_srv_info
{
    wchar_t* pwszName; 
    wchar_t* pwszUser;
    wchar_t* pwszPwd;
    wchar_t* pwszPro;
    long     lPort;
    bool     bIgnoreCert;
} ST_HAESX_INFO, *PST_HAESX_INFO;

typedef struct _t_esx_node
{
    wchar_t* pwszEsxName;	
    wchar_t* pwszDcName;
} ST_HAESX_NODE, *PST_HAESX_NODE;

/**
* Do initialize work for VMware V2P (snapshot NOW).
* Add support for check if is CBT enabled for SAN env.
* Should be called after connectToEsx().
* @hVmV2P             [in] - the HANDLE returned by V2PNativeFacadeInit()
* @pwszVmName         [in] - the virtual machine name
* @pwszVmGuid         [in] - the virtual machine GUID
* @pwszSnapGuid       [in] - the GUID of the snapshot
* @ppCtx              [out]- the context handle returned
* @pbCbtEnabled       [out]- if the CBT is enabled
*
* return 0 success, others fail.
*/
DRSOCKCLIENT_API
int InitializeV2P_VMware(void* hVmV2P, const wchar_t* pwszVmName, const wchar_t* pwszVmGuid, 
                        const wchar_t* pwszSnapGuid, void** ppCtx, bool* pbCbtEnabled);
typedef int (*PFN_InitializeV2P_VMware)(void* hVmV2P, const wchar_t* pwszVmName, const wchar_t* pwszVmGuid, 
                         const wchar_t* pwszSnapGuid, void** ppCtx, bool* pbCbtEnabled);

/**
* Do initialize work for VMware V2P (snapshot NOW)
* should be called before disconnectToEsx(). 
* @pCtx              [in] - the context handle returned from InitializeV2P_VMware.
* return value:
* 0 success, others fail. 
* IF return 0, user should not call this API again, like the free() in CRT.
*/
DRSOCKCLIENT_API
int UnInitializeV2P_VMware(void* pCtx);
typedef int (*PFN_UnInitializeV2P_VMware)(void* pCtx);

/**
* Create IVVol for VMware by volume GUID and snapshot path.
* @hVmV2P             [in] - the HANDLE returned by V2PNativeFacadeInit()
* @pstSrvInfo         [in] - the esx server info.
* @pwszVmName         [in] - the virtual machine name
* @pwszVmGuid         [in] - the virtual machine GUID
* @pwszSnapshotGuid   [in] - the GUID of the snapshot
* @pwszVolGuid        [in] - the GUID of volume
* @ulFlag             [in] - flag, if is snapshot NOW, use VVOL_FLAG_NO_CTF.
* @ppVol              [out]- the IVVol* returned
* 
* return value:
* 0       success
* others  fail
*/
DRSOCKCLIENT_API
int CreateVVolume_VMware(void* hVmV2P, const ST_HAESX_INFO* pstSrvInfo, 
                         const wchar_t* pwszVmName, const wchar_t* pwszVmGuid, 
                         const wchar_t* pwszSnapshotGuid, const wchar_t* pwszVolGuid, 
                         unsigned long ulFlag, IVVol** ppVol);
typedef int (*PFN_CreateVVolume_VMware)(void* hVmV2P, const ST_HAESX_INFO* pstSrvInfo, 
                         const wchar_t* pwszVmName, const wchar_t* pwszVmGuid, 
                         const wchar_t* pwszSnapshotGuid, const wchar_t* pwszVolGuid, 
                         unsigned long ulFlag, IVVol** ppVol);

/**
* Get the DR info of snapshot NOW for VMware.
* Before call this function, need ensure have called connectToESX() on hVmV2P
* @hVmV2P             [in] - the HANDLE returned by V2PNativeFacadeInit()
*/
DRSOCKCLIENT_API
int GetDrInfoFileForSnapshotNow_VMware(void* hVmV2P, const ST_HAESX_NODE* pstEsxNode,
                                       const wchar_t* pwszVmName, const wchar_t* pwszVmGuid, 
                                       const wchar_t* pwszDrInfoPath);
typedef int (*PFN_GetDrInfoFileForSnapshotNow_VMware) (void* hVmV2P, const ST_HAESX_NODE* pstEsxNode,
                                                       const wchar_t* pwszVmName, const wchar_t* pwszVmGuid, 
                                                       const wchar_t* pwszDrInfoPath);

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
DRSOCKCLIENT_API
int GetDrInfoLocalCopyPathForSnapNow_VMware(const wchar_t* pwszVmName, const wchar_t* pwszVmGuid,
                                            wchar_t* wszPath, int& nSizeInWord);

}; //HaVhdUtility

namespace DRSOC = HaVhdUtility;



#endif //_DR_SOCKET_CLIENT_H_
