#ifndef _GRTMNTBROWSE_H
#define _GRTMNTBROWSE_H
#include "windows.h"
#include "tchar.h"
#include "AFStorHBAMgmt.h"
#include "catalog.h"
#include "AFjob.h"
#include "vmdkimg.h"
//#include "GRTMounter.h"
//#include "FileHandler.h"
#include <string>
using namespace std;

#ifdef GRTMNTBROWSER_EXPORTS
#define GRT_MNT_API __declspec(dllexport)
#else
#define GRT_MNT_API __declspec(dllimport)
#endif

#if defined (__cplusplus)
extern "C"
{
#endif // __cplusplus

	
typedef struct _RESTORE_JOB_INFO
{
	PAFRESTVOLAPP pJobScript;
	TCHAR		szSourceDrive[MAX_PATH];
	TCHAR      *pszMntPathOrGuid;		    //Will be filled if the backup volume didn’t have drive letter
	PRESTORE_IFS_STATISTIC pJobStatistic;	//restore stats will be filled by vmdkimg dll
	DWORD       dwFlag;						//for future use
	PFN_NTAGENTCALLBACK pfn_callback;		//generic NT agent callback routine
	PSHORT		p_isJobCancelled;			//Job Cancelled status
	int jobNumber;
	LPCTSTR	    pszLogUniqueID;
}RESTORE_JOB_INFO, *PRESTORE_JOB_INFO;


typedef struct st_mountedvol_state
{
	int nVersion;				// size of this structure
	BOOL isMounted;				// whether the volume identified by DiskId is at mounted state
}MNT_VOLUE_STATE, *PMNT_VOLUE_STATE;

GRT_MNT_API HANDLE InitGRTMounter();

GRT_MNT_API DWORD ExitGRTMounter(HANDLE handle);

//Do we need to lock before mounting? no need as per Jerry
//Return ERROR_SUCCESS on success  
GRT_MNT_API DWORD MountVolume(
						 __in		HANDLE handle, //mounter object handle
						 __in		SESSION_INFO SessionInfo,//the information for open a session
						 __in		LPWSTR lpVolumeGUID,    //the backuped volume GUID to be mounted
						 __in		LPWSTR lpEncryptKey,    //the encryption key if the session is encrypted.
						  __in      DWORD  dwJobId,
						 __in       DWORD  dwJobType,
						 __in       DWORD  dwProductType,
						 __in       LPWSTR lpMachineUuid,
						 __out		LPWSTR lpDiskId,         //the mounted disk signature needed for unmount
						 __out		LPWSTR szFinalMountedVolumeGUID		//volume GUID of the newly mounted volume		
  						 );

GRT_MNT_API DWORD UnMountVolume(__in HANDLE handle, //mounter object handle
								__in LPWSTR lpDiskId);

GRT_MNT_API DWORD UnMountRestoreBrowseVolumes(
						__in BOOL bForceAll			// Force unmount all restore browse volumes, ignore whether timeout or not
						);	

GRT_MNT_API DWORD UpdateVolumeAccessTime(
						__in LPWSTR lpDiskId		// Disk signature returned by mount.
						);

GRT_MNT_API PDetailW GetFileChildren(__in LPWSTR szFinalMountedVolumeGUID,
								 __in LPWSTR szFullPath, //path for which we need the get the children 								 
								 __out UINT *Cnt);

GRT_MNT_API PDetailW GetFileChildrenEx(__in LPWSTR szFinalMountedVolumeGUID,
								 __in LPWSTR szFullPath, //path for which we need the get the children 
								 __out UINT *Cnt,  //number of children
								 __in BOOL bGetCount = FALSE,
								 __in UINT nStart = 0, 
								 __in UINT nRequest = -1);

GRT_MNT_API UINT GetFileChildrenCount(__in LPWSTR szFinalMountedVolumeGUID,
								 __in LPWSTR szFullPath);

GRT_MNT_API DWORD UnMountAll(__in HANDLE handle); //mounter object handle


//GRT_MNT_API DWORD MountVolume(
//						__in		SESSION_INFO SessionInfo,//the information for open a session
//						 __in		LPCWSTR lpVolumeName,    //the backuped volume will be mounted
//						 //__in		LPCWSTR lpMountName,	 //the path which set the mounted volume to it
//						 __out		LPWSTR lpDiskId,         //the mounted disk id
//						 //__in		DWORD dwBufferSize,		 //the buffer size of the lpDiskId
//						 //__in		BOOLEAN bReadOnly = FALSE,   //the mounted volume is readonly or not
//						 //__in_opt	PMOUNT_OPT	pMountOpt = NULL //the mount option, if NULL, bForeDismount = FALSE
//					

GRT_MNT_API DWORD RestoreFilesFolders(
						 __in		PSESSION_INFO SessionInfo,//the information for open a session
						 __in		PRESTORE_JOB_INFO RestoreInfo    //the backuped volume GUID to be mounted
						 );

GRT_MNT_API HANDLE SearchMountPoint(__in LPWSTR szBackupDestination,
									__in LPWSTR szMountedVolumeGUID,
									__in LPWSTR szDriveLetter,
								 __in LPWSTR szPattern, 
								 __in LPWSTR szSearchDir,
								 __in BOOL bIncludeSubDir = TRUE,
								 __in BOOL bCaseSensitive = FALSE);

GRT_MNT_API DWORD FindNextSearchItems(__in HANDLE hSearchContext,
								  __in UINT nRequest, 
								  __out PDetailW *pDetail, 
								  __out UINT *nFound);

GRT_MNT_API DWORD FindCloseSearchItems(__in HANDLE hSearchContext);

GRT_MNT_API DWORD UpdateCatalogJobScript(__in SESSION_INFO si, __in LPWSTR jobScript, const WCHAR* pwzJobQIdentity =NULL);

GRT_MNT_API INT GetCatalogStatus(__in LPWSTR sessionPath);

GRT_MNT_API INT SetCatalogStatus(__in LPWSTR backupDest, __in LPWSTR sessionPath);

GRT_MNT_API HRESULT XInit();
GRT_MNT_API HRESULT XUnInit();
GRT_MNT_API HRESULT XQueryMountedVolume(LPWSTR lpDiskId, PMNT_VOLUE_STATE pVolState);

#define MNT_SOURCE_UNKNOWN	0x00
#define MNT_SOURCE_BROWSE	0x01		// mount from browsing catalog-less session.
#define MNT_SOURCE_MRP		0x02		// mount recovry session.
#define MNT_SOURCE_ADGRT	0x03		// AD GRT browse

typedef struct st_mnt_info
{
	DWORD	dwSize;			// size of current strurcture 
	DWORD	dwSource;		// MNT_SOURCE_BROWSE or MNT_SOURCE_MRP
	WCHAR * lpSessPath;
	WCHAR * lpVolGUID;
	WCHAR * lpMntTo;
	WCHAR * lpUserName;
	WCHAR * lpPassword;
	WCHAR * lpSessPSW;
	BOOL bReadOnly;

	DWORD   dwJobId;             
	DWORD   dwJobType;           
	DWORD   dwProductType;       
	WCHAR * lpMachineUUID;
	st_mnt_info()
	{
		dwSize = 0;
		dwSource = 0;
		lpSessPath	= NULL;
		lpVolGUID	= NULL;
		lpMntTo		= NULL;
		lpUserName	= NULL;
		lpPassword	= NULL;
		lpSessPSW	= NULL; 
		bReadOnly = TRUE;
		dwJobId       = 0;
		dwJobType     = 0;
		dwProductType = 0;
		lpMachineUUID = NULL;

	}

}MNT_SESS_INFO, *PMNT_SESS_INFO;

typedef struct st_mnt_result
{
	DWORD dwSize;			// size of current strurcture 
	WCHAR wszDiskSig[MAX_PATH];
	WCHAR wszVolGUID[MAX_PATH];

	st_mnt_result()
	{
		memset(wszDiskSig, 0, sizeof(wszDiskSig) );
		memset(wszVolGUID, 0, sizeof(wszVolGUID) );
	}
}MNT_RESULT, *PMNT_RESULT;


//////////////////////////////////////////////////////////////////////////
//Function Name: XMntD2DSession
//	Using Mounting Drivedr to Mount D2D Backup Session. THIS FUNCTION IS ONLY USED BY TOMCAT, PLEASE DON'T USE IT IN OTHER PLACE
//Parameters: 
//            PMNT_SESS_INFO	[in] the informatin used to mount  D2D Session. For more information, please check structure of st_mnt_info
//            pMntinfo			[out]the information related with mounted volume, for more information  please check structure of st_mnt_result
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
GRT_MNT_API DWORD XMntD2DSession( PMNT_SESS_INFO psessinfo, PMNT_RESULT pMntinfo);


//////////////////////////////////////////////////////////////////////////
//Function Name: XDisMntD2DSession
//				 Dis-Mount volume mounted by D2D.  THIS FUNCTION IS ONLY USED BY TOMCAT, PLEASE DON'T USE IT IN OTHER PLACE
//Parameters: 
//            lpDiskSig	[in] the signature of volume
//            lpMntName [in] the name of volume, optional parameter	
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
GRT_MNT_API DWORD XDisMntD2DSession(const WCHAR * lpDiskSig, const WCHAR * lpMntName =NULL);

typedef struct st_mnt_data
{
	DWORD version;
	DWORD dwbufSize;
	WCHAR *pwszBuf;
}MNT_ITEM_DATA, *PMNT_ITEM_DATA;


//////////////////////////////////////////////////////////////////////////
//Function Name: XGetMntItemData
//				 Get the bacupinfo.xml related with current mounted voluem.  THIS FUNCTION IS ONLY USED BY TOMCAT, PLEASE DON'T USE IT IN OTHER PLACE
//Parameters: 
//            lpDiskSig	[in] the signature of volume
//            pMntData  [out] the content of bacupinfo.xm, this paramater should be release by XReleaseMntItemData
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
GRT_MNT_API DWORD XGetMntItemData(const WCHAR * lpDiskSig, PMNT_ITEM_DATA  pMntData);

//////////////////////////////////////////////////////////////////////////
//Function Name: XReleaseMntItemData
//				release the structure returned by XGetMntItemData.  THIS FUNCTION IS ONLY USED BY TOMCAT, PLEASE DON'T USE IT IN OTHER PLACE
//Parameters: 
//            pMntData	[in] the point of data that returned by XGetMntItemData
//return value: void
//////////////////////////////////////////////////////////////////////////
GRT_MNT_API void XReleaseMntItemData(PMNT_ITEM_DATA  pMntData);

#if defined (__cplusplus)
}
#endif // __cplusplus

#endif //_GRTMNTBROWSE_H