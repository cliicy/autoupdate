#pragma once
#include "D2dbaseDef.h"
#define ERR_MGMT_BASE				1
#define ERR_MGMT_NO_ENOUGHBUF		ERR_MGMT_BASE+1

#pragma pack(push, 8)

#define AFSTOR_VD_SEARCH_FOR_ALL				0xFFFFFFFF
#define AFSTOR_VD_SEARCH_FOR_FOLDER				0x00000001
#define AFSTOR_VD_SEARCH_FOR_VOLUME				0x00000002
#define AFSTOR_VD_SEARCH_FOR_MOUNTPOINT			0x00000004
#define AFSTOR_VD_SEARCH_FOR_SIGNATURE			0x00000008

typedef struct _VD_SEARCH_SPEC_
{
	ULONG  ulSelectionFlag;//AFSTOR_VD_SEARCH_FOR_ALL,AFSTOR_VD_SEARCH_FOR_FOLDER ...
							// indict which field in _VD_SEARCH_SPEC_ is available
	DWORD dwSignature;
    PWCHAR SessionFolder;
    PWCHAR VolumeName;
    PWCHAR MountName;
}VD_SEARCH_SPEC, *PVD_SEARCH_SPEC;

typedef struct _VD_RESULT_SPEC_
{
    PWCHAR SessionFolder;	// session folder.			/* Eg: D:\BK\zhahu03-WIN8X64\VStore\S0000000001\ */
    PWCHAR VolumeName;		// original volume GUID.	/* Eg: \\?\Volume{1b5bd250-fc09-11e3-8289-5cf9dd736a3e} */
    PWCHAR MountName;		// mount point.				/* Eg: X: */
	ULONG  ulOption;
	ULONG  ulDiskSignature;	// disk signature.			/* Eg: 855093  (GetTickcount()) */
}VD_RESULT_SPEC, *PVD_RESULT_SPEC;

typedef struct _VD_RESULT_LIST_
{
	ULONG	ulTotalNumbers; //ulTotalNumbers is pDiskList size. 
	//if ulTotalNumbers < actual numbers, set ulTotalNumbers = actual numbers and VDQueryMntPoint return ERR_MGMT_NO_ENOUGHBUF
	PVD_RESULT_SPEC pDiskList;
}VD_RESULT_LIST, *PVD_RESULT_LIST;


#define DEFAULT_MNTVOL_TIMEOUT				(10 * 60 * 1000)	// Default idle for 10mins, the volume will be dismounted.
#define AF_VOLEXT_UPDATE_ALL_FIELD			0
#define AF_VOLEXT_UPDATE_LAST_ACCESS_TIME	1

typedef struct _update_value_pair{
	DWORD	dwUpField;			// update which field
	PVOID	dwValue;			// field value, can be NULL
}
AF_VOLEXT_VALUEPAIR, *PAF_VOLEXT_VALUEPAIR;

//
// Save some extension data in driver for current volume,
// Currently, we save last access time in driver.
//
typedef struct _mntvol_extend_data
{
	DWORD	dwMntSponsor;		/* Mount sponsor	*/
	DWORD	dwDiskSignature;	/* Disk signature	*/
	DWORD	dwTimeoutMs;		/* Idle timeout		*/
	DWORD	dwCreateTime;		/* Create time, GetTickCount()		*/
	DWORD	dwLastAccessTime;	/* Last access time. GetTickCount() */
}
AF_MNTVOL_EXT_DATA, *PAF_MNTVOL_EXT_DATA;


#pragma pack(pop)


#define VMMOUNT_ERR_BASE					0xF0000000
#define VMMOUNT_ERR_NOSUPPORT_LENGTH		VMMOUNT_ERR_BASE + 1
#define VMMOUNT_ERR_LOCK_SESSION			VMMOUNT_ERR_BASE+2
#define VMMOUNT_ERR_OPEN_MOUNTING_DRIVER    VMMOUNT_ERR_BASE + 3
#define VMMOUNT_ERR_VALID_ADMIN_FAILED      VMMOUNT_ERR_BASE + 4
/// \brief call this to mount a virtual volume as disk
///
/// detail 
/// \param[in] pszRootFolder: example,\\155.35.79.81\ExchangeGRT\Backups\qijfe01-e3
/// \param[in] dwSessNo: session number
/// \param[in] VolumeName: volume name
/// \param[IN] pszMountName: mount name
/// \param[OUT] pszDiskID: Disk signautre
/// \param[in] BufSize pszVolumeID buffer size
/// \return 0 if success
/// \version 1.0
int __stdcall VDMount(IN const WCHAR* pszRootFolder, 
					  IN DWORD dwSessNo, const WCHAR* VolumeName, 
					  IN WCHAR* pszMountName,OUT WCHAR* pszDiskID,IN DWORD BufSize,
					  IN BOOLEAN bReadOnly = FALSE);

typedef int (__stdcall *PFNVDMOUNT)(IN const WCHAR* pszRootFolder, 
					  IN DWORD dwSessNo, const WCHAR* VolumeName, 
					  IN WCHAR* pszMountName,OUT WCHAR* pszDiskID,IN DWORD BufSize,
					  IN BOOLEAN bReadOnly);

/// \brief call this to mount a virtual volume as disk
///
/// detail 
/// \param[in] pszRootFolder: example,\\155.35.79.81\ExchangeGRT\Backups\qijfe01-e3
/// \param[in] pszUserName, it can be NULL if the rootfolder is local folder, the username should be include the domain name
///                         its format is domainname\usernmae or username@domainname
/// \param[in] pszPassword, it can be NULL
/// \param[in] dwSessNo: session number
/// \param[in] VolumeName: volume name
/// \param[IN] pszMountName: mount name
/// \param[OUT] pszDiskID: Disk signature
/// \param[in] BufSize pszVolumeID buffer size
/// \return 0 if success
/// \version 1.0
int __stdcall VDMountEx(IN const WCHAR* pszRootFolder,
					  IN const WCHAR* pszUserName,
					  IN const WCHAR* pszPassword,
					  IN DWORD dwSessNo, const WCHAR* VolumeName, 
					  IN WCHAR* pszMountName,OUT WCHAR* pszDiskID,IN DWORD BufSize,
					  IN BOOLEAN bReadOnly = FALSE);

typedef int (__stdcall *PFNVDMOUNTEX)(IN const WCHAR* pszRootFolder,
					  IN const WCHAR* pszUserName,
					  IN const WCHAR* pszPassword,
					  IN DWORD dwSessNo, const WCHAR* VolumeName, 
					  IN WCHAR* pszMountName,OUT WCHAR* pszDiskID,IN DWORD BufSize,
					  IN BOOLEAN bReadOnly);

typedef struct _SESSION_INFO_
{
	LPCWSTR lpRootFolder; //example,\\155.35.79.81\ExchangeGRT\Backups\qijfe01-e3
	LPCWSTR	lpUserName;	  //it can be NULL if the rootfolder is local folder, the username should be include the domain name
						  //its format is domainname\usernmae or username@domainname
	LPCWSTR	lpPassword;	  //it can be NULL
	DWORD	dwSessNo;     //session number
	PD2D_ENCRYPTION_INFO pEncryptInfo; //the encryption information, it can be null or set the uiCryptoType = 0 to idicate is no encrypted session
}SESSION_INFO, *PSESSION_INFO;

#define  MNT_SPONSOR_UNKNOWN                 0
#define  MNT_SPONSOR_GRT_CAT_JOB             1	// exchange GRT catalog job
#define  MNT_SPONSOR_GRT_REST_JOB			 2	// exchange GRT restore job                 
#define  MNT_SPONSOR_F2C_JOB	             3	// file copy job
#define  MNT_SPONSOR_REST_NO_CAT_JOB		 4	// catalog-less restore job
#define  MNT_SPONSOR_REST_BROWSE             5  // restore browse
#define  MNT_SPONSOR_REST_SEARCH			 6  // restore search
#define  MNT_SPONSOR_MDL                     7	// mount to drive letter
#define  MNT_SPONSOR_EE                      8	// explorer extension
#define  MNT_SPONSOR_UNDEFINE                (MNT_SPONSOR_EE+1) //invalid value


#define  MNT_MACHINE_UUID_LENGHT             512

typedef struct _MOUNTT_OPT_ACTLOG_
{
	DWORD   dwJobId;             
	DWORD   dwJobType;           
	DWORD   dwProductType;       
	WCHAR   szMachineUUID[MNT_MACHINE_UUID_LENGHT];
}MOUNTT_OPT_ACTLOG, *PMOUNTT_OPT_ACTLOG;

typedef struct _MOUNT_OPT_
{
	BOOL	bForceDismount;		//if true, will force dismout the volume when it block the merge session operation
								//Suggest Shell Extension set it to TRUE, other caller sets it to FALSE
	DWORD	dwTimeOut;			//the time-out interval, in seconds, if bForeceDismount = TRUE, it will valide, -1 is infinit
	BOOL	bOtherProcess;		//if true Dismount the virtual volume by other process, not the caller process;
								//if false, the mount and dismount the virtual volume is in one process
	ULONG   ulOption;            //record who sponsores the mount operation 

	//Below 4 parameters are used when print the activity log
	DWORD   dwJobId;             
	DWORD   dwJobType;           
	DWORD   dwProductType;       
	WCHAR   szMachineUUID[MNT_MACHINE_UUID_LENGHT];

}MOUNT_OPT, *PMOUNT_OPT;

int __stdcall VDMountEx2(
						 __in		SESSION_INFO SessionInfo,//the information for open a session
						 __in		LPCWSTR lpVolumeName,    //the backuped volume will be mounted
						 __in		LPCWSTR lpMountName,	 //the path which set the mounted volume to it
						 __out		LPWSTR lpDiskId,         //the mounted disk id
						 __in		DWORD dwBufferSize,		 //the buffer size of the lpDiskId
						 __in		BOOLEAN bReadOnly = FALSE,   //the mounted volume is readonly or not
						 __in_opt	PMOUNT_OPT	pMountOpt = NULL //the mount option, if NULL, bForeDismount = FALSE
						 // if non-null, the mount moniter process will dismount the volume as the option
						 // doesn't implement it now
						 );

typedef int (__stdcall *PFNVDMOUNTEX2)(
									   __in		SESSION_INFO SessionInfo,
									   __in		LPCWSTR lpVolumeName,
									   __in		LPCWSTR lpMountName,
									   __out	LPWSTR lpDiskId,
									   __in		DWORD dwBufferSize,
									   __in		BOOLEAN bReadOnly,
									   __in_opt	PMOUNT_OPT	pMountOpt
									   );

int __stdcall VDMountExReadOnly(
	__in		SESSION_INFO SessionInfo,//the information for open a session
	__in		LPCWSTR lpVolumeName,    //the backuped volume will be mounted
	__in		LPCWSTR lpMountName,	 //the path which set the mounted volume to it
	__out		LPWSTR lpDiskId,         //the mounted disk id
	__in		DWORD dwBufferSize,		 //the buffer size of the lpDiskId
	__in_opt	PMOUNT_OPT	pMountOpt	/* = NULL*/ //the mount option
	);

typedef int(__stdcall *PFNVDMOUNTEXREADONLY)(
	__in		SESSION_INFO SessionInfo,
	__in		LPCWSTR lpVolumeName,
	__in		LPCWSTR lpMountName,
	__out		LPWSTR lpDiskId,
	__in		DWORD dwBufferSize,
	__in_opt	PMOUNT_OPT	pMountOpt
	);


/// \brief VDDismount
///
/// dismount a disk file 
/// \param[in] pszDiskID returned in VDMount
/// \return 0 if success
/// \author qijfe01
/// \version 1.0
int __stdcall VDDismount(IN const WCHAR* pszDiskID);

typedef int (__stdcall *PFNVDDISMOUNT)(IN const WCHAR* pszDiskID);


//first, pass a empty(zero) pSearchSpec to receive the numbers of list
//then, allocate buffer and call this API agin to retrieve device list
//if ulTotalNumbers < actual numbers, set ulTotalNumbers = actual numbers 
//and VDQueryMntPoint return ERR_MGMT_NO_ENOUGHBUF
int __stdcall VDQueryMntPoint(IN PVD_SEARCH_SPEC pSearchSpec, OUT PVD_RESULT_LIST pResult);

typedef int (__stdcall *PFNVDQUERYMNTPOINT)(IN PVD_SEARCH_SPEC pSearchSpec, OUT PVD_RESULT_LIST pResult);
#define VDQUERYMNTPOINT_NAME		"VDQueryMntPoint"


#define AFSTOR_VD_ISVD_NOTEXIST		1
#define AFSTOR_VD_ISVD_VD			2
#define AFSTOR_VD_ISVD_NOTVD		3

/// \brief VDIsVirtualDisk
///
/// whether  pVolName is a virtual disk which mounted by afstorhba
/// \param[in] pVolName volume name
/// \param[in] SizeofVolName pVolName buffer size
/// \param[out] pDiskType disk type. return AFSTOR_VD_ISVD_NOTEXIST, AFSTOR_VD_ISVD_VD or AFSTOR_VD_ISVD_NOTVD
/// \return 0 if success
/// \version 1.0
int __stdcall VDIsVirtualDisk(IN const WCHAR *pVolName, DWORD *pDiskType);
typedef int (__stdcall *PFNVDISVIRTUALDISK)(IN const WCHAR *pVolName, DWORD *pDiskType);
#define VDISVIRTUALDISK	"VDIsVirtualDisk"


// \brief VDSetTimeEx
//Save the time value in driver for the specific virtual disk
//
//\param[in] pszDiskID, the virtual disk signature, use it to find the special disk
//\param[in] ulTime, the time value will be saved
//
int __stdcall VDSetTimeEx(const WCHAR* pszDiskID, ULONG ulTime);
typedef int(__stdcall *PFNVDSETTIMEEX)(const WCHAR* pszDiskID, ULONG ulTime);
#define VDSETTIMEEX		"VDSetTimeEx"

// \brief VDSetTime
// the diminish version VDSetTimeEx, it will get the current time as parameter pass to VDSetTimeEx
//
int __stdcall VDSetTime(const WCHAR* pszDiskID);
typedef int(__stdcall *PFNVDSETTIME)(const WCHAR* pszDiskID);
#define VDSETTIME		"VDSetTime"


// \brief VDGetTime
//retrieve the saved time from the device space
//
//\param[in] pszDiskID, the virtual disk signature, use it to find the special disk
//
int __stdcall VDGetTime(const WCHAR* pszDiskID, ULONG* pulTime);
typedef int(__stdcall *PFNVDGETTIME)(const WCHAR* pszDiskID, PULONG pulTime);
#define VDGETTIME		"VDGetTime"


//
//Tell the mounting driver to hold the write IO for the mounted disk
//
int __stdcall VDHoldWriteStart();
typedef int (__stdcall *PFNVDHOLDWRITESTART)();
#define VDHOLDWRITESTART_NAME       "VDHoldWriteStart"

//
//Tell the mounting driver to stop hold th write IO for the mounted disk
//
int __stdcall VDHoldWriteStop();
typedef int (__stdcall *PFNVDHOLDWRITESTOP)();
#define VDHOLDWRITESTOP_NAME       "VDHoldWriteStop"

//
// Check if volume already mounted.
//  @pszVolumeGUID	: Backed up volume GUID
//	@pszSessFolder	: Session folder
//
// Return TRUE if mounted, otherwise FALSE.
//  @pulDiskSig		: Mounted volume signature
//  @pMntedVolGuid	: Mounted volume GUID
//
BOOL __stdcall VDCheckIfMounted(IN const WCHAR* pszVolumeGUID, IN const WCHAR* pszSessFolder, OUT PULONG pulDiskSig, OUT GUID* pMntedVolGuid);
typedef PVD_RESULT_SPEC (__stdcall *PFNVDCHECKIFMOUNTED)(const WCHAR*, const WCHAR*, PULONG, GUID*);
#define VDCHECKIFMOUNTED			"VDCheckIfMounted"



#ifndef AFMUNTING_SYNC_LOCK
#define AFMUNTING_SYNC_LOCK L"Global\\AFSTORHBAMGMTSync992A3328-F4DE-4999-A65C-6E3ACF2B5DB4"
#endif

class AFMntSyncLock
{
public:
	AFMntSyncLock(){;}
	~AFMntSyncLock()
	{
		Unlock();
	}

	DWORD Lock(LPCWSTR lpLockName, DWORD dwWait = 10*1000*60)
	{
		DWORD dwRet = 0;
		SECURITY_DESCRIPTOR sd; 
		SECURITY_ATTRIBUTES sa; 
		InitializeSecurityDescriptor(&sd,SECURITY_DESCRIPTOR_REVISION); 
		SetSecurityDescriptorDacl(&sd,TRUE,(PACL)NULL,FALSE); //set all the user can access the object      
		sa.nLength=sizeof(SECURITY_ATTRIBUTES); 
		sa.bInheritHandle=FALSE; 
		sa.lpSecurityDescriptor=&sd;
		m_hMux = CreateMutex(&sa, FALSE, lpLockName);
		if(m_hMux == NULL)
		{
			dwRet = GetLastError();
			//LOGERR(dwRet,_T("Fail to create mutex"));
			return dwRet;
		}

		if(WaitForSingleObject(m_hMux, dwWait)==WAIT_FAILED)
		{
			dwRet = GetLastError();
			CloseHandle(m_hMux);
			m_hMux =NULL;
			//LOGERR(dwRet,_T("Fail to WaitForSingleObject"));
			return dwRet;
		}

		return dwRet;
	}

	void Unlock()
	{
		if(m_hMux)
		{
			ReleaseMutex(m_hMux);
			CloseHandle(m_hMux);
			m_hMux = NULL;
		}
	}

private:
	HANDLE m_hMux;
};