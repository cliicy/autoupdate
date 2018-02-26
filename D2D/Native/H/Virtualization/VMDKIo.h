// All files within this DLL are compiled with the VMDKIO_EXPORTS
// symbol defined on the command line. this symbol should not be defined on any project
// that uses this DLL. 
#pragma once 
#include <Windows.h>
#include <WinIoCtl.h>
#include <WinDef.h>


#if defined (__cplusplus)
extern "C"
{
#endif // __cplusplus

#ifdef VMDKIO_EXPORTS
#define VMDKIO_API __declspec(dllexport)
#else
#define VMDKIO_API __declspec(dllimport)
#endif

typedef UINT64 VMDKIOLibSectorType;
typedef UINT64 VMDKIOError;
typedef char VMDKIOBool;
typedef void (VMDKIOLibGenericLogFunc)(const char *fmt, va_list args);
#define VMDKIO_OK	0
#define VMDKDISK_HANDLE          void *
#define VMDKIOLIB_HANDLE_INVALID (void *) -1
#define VMDKSNAPSHOT_HANDLE	void *

#ifndef THUMBPRINT_LENGTH
#define THUMBPRINT_LENGTH 60
#endif

#define VMDKIO_SECTOR_SIZE 512
//bccma01 added
typedef struct _VMDKVOLDETAILS
{
	char*            symbolicLink;
	char*			 guestDriveLetter[1];
}VMDKVOLDETAILS, *PVMDKVOLDETAILS;

typedef struct _VMDKDISKDETAILS
{
	char*            filePath;
	bool             isMBR;
	DWORD            diskSignature; // disk signature if MBR disk
}VMDKDISKDETAILS, *PVMDKDISKDETAILS;

//<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll
typedef struct _VMDK_CONNECT_MORE_PARAMS
{
	int				code; //reserved for later use, must be initialized to ZERO
	UINT			flags; //reserved for later use, must be initialized to ZERO
	char*           thumbprint; //VDDK 6 require thumbprint
	_VMDK_CONNECT_MORE_PARAMS() :thumbprint(NULL), code(0), flags(0) {}
}VMDK_CONNECT_MORE_PARAMS, *PVMDK_CONNECT_MORE_PARAMS;

/**
* Initializes VMDKDiskLib. This function is to be called 
* before invokation of any VMDKIo function
* @return VMDKIO_OK on success, suitable VMDKIO error code otherwise.
*/
VMDKIO_API VMDKIOError
VMDKIoLib_Init(VMDKIOLibGenericLogFunc* logFunc);

/**
* Cleans up VMDKDiskLib. This function is to be called
* after the usage of VMDKIo
*/
VMDKIO_API void
VMDKIoLib_Exit(VMDKIOBool isFreeLibrary);


/**
* VMDKIo_Connect is called for each snapshot. 
* The returned snapshot handle is to be used for every disk open function
* @param vmxSpec [in] vmxSpec is of the form- <vmxPathName>?dcPath=<dcpath>&dsName=<dsname>
*					  where vmxPathName is the fullpath for the VMX file,
*					  dcpath is the inventory path of the datacenter and
*					  dsname is the datastore name.
* @param serverName [in] EXS servername or ipaddress
* @param userName [in] username for ESX server
* @param password [in] password for ESX server
* @param port [in]	ESX server port or 0 to use default port
* @param snapshotRef [in] A managed object reference to the specific
*             snapshot of the virtual machine whose disks will be
*             accessed with this connection.  Specifying this
*             property is only meaningful if the vmxSpec property in
*             connectParams is set as well. e.g. "16-snapshot-3"
* @param readOnly [in]
* @param snapshotHandle [out] Snapshot handle is returned in this parameter
* @return VMDKIO_OK if success, suitable VMDKIO error code otherwise.
*/
VMDKIO_API VMDKIOError      
VMDKIo_Connect(char *vmxSpec,
			   char *serverName,
			   char *userName,
			   char *password,
			   UINT32 port,
			   char *snapshotRef,
			   VMDKIOBool readOnly,
			   VMDKSNAPSHOT_HANDLE *snapshotHandle);

VMDKIO_API   VMDKIOError
VMDKIo_ConnectV6(char *vmxSpec,
char *serverName,
char *userName,
char *password,
UINT32 port,
VMDK_CONNECT_MORE_PARAMS * connect_more_params, //<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll
char *snapshotRef,
VMDKIOBool readOnly,
VMDKSNAPSHOT_HANDLE *snapshotHandle
); //<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll

/*
 * Same as VMDKIo_Connect, but here user can send the preferred transport mode
 * in the parameter transportModes. Eg: "file:san:hotadd:nbdssl:nbd"
 * transportModes 
 * An optional list of transport modes that can be used for this connection, separated by colons.
 * If you specify NULL specified (recommended), default setting is used. 
 * The default setting corresponds to the string returned by VMDKIO_ListTransportModes(). 
 * If a disk is opened through this connection, 
 * VDDK starts with the first entry of the list and attempts to use this transport mode to gain access to the virtual disk.
 * If this does not work, the next item in the list is tried until either the disk is successfully opened or the end of the list is reached
 */
VMDKIO_API   VMDKIOError  
VMDKIo_ConnectByTransportMode(char *vmxSpec,
				char *serverName,
				char *userName,
				char *password,
				UINT32 port,
				char *snapshotRef,
				char *transportModes,
				VMDKIOBool readOnly,
				VMDKSNAPSHOT_HANDLE *snapshotHandle);

VMDKIO_API   VMDKIOError
VMDKIo_ConnectByTransportModeV6(char *vmxSpec,
char *serverName,
char *userName,
char *password,
UINT32 port,
VMDK_CONNECT_MORE_PARAMS * connect_more_params, //<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll
char *snapshotRef,
char *transportModes,
VMDKIOBool readOnly,
VMDKSNAPSHOT_HANDLE *snapshotHandle
); //<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll

/**
* VMDKIo_DisConnect is called for the usage of each snapshot. 
*  @param handle [in] The handle returned by VMDKIo_Connect call
*/
VMDKIO_API void
VMDKIo_DisConnect(VMDKSNAPSHOT_HANDLE handle);

/// Open a VMDK File. VMDKDISK_HANDLE should be used for further operations on DISK
/**
* @param VMDKSNAPSHOT_HANDLE [in]
* @param vmdkFilePath [in] VMDK file name given as absolute path
*             e.g. "[storage1] SERVER1/SERVER1Disk.vmdk"
* @param diskHandle [out] disk handle is returned in this parameter
* @return VMDKIO_OK if success, suitable VMDKIO error code otherwise.
*/
VMDKIO_API  VMDKIOError 
OpenVMDKFileA(VMDKSNAPSHOT_HANDLE,
			  const char *vmdkFilePath,
			  VMDKDISK_HANDLE *diskHandle);

/// Open a VMDK File. VMDKDISK_HANDLE should be used for further operations on DISK
/**
* @param VMDKSNAPSHOT_HANDLE [in]
* @param vmdkFilePath [in] VMDK file name given as absolute path
* @param diskHandle [out] Disk handle is returned in this parameter
* @return VMDKIO_OK if success, suitable VMDKIO error code otherwise.
*/
VMDKIO_API  VMDKIOError 
OpenVMDKFileW(VMDKSNAPSHOT_HANDLE,
			  const wchar_t *vmdkFilePath,
			  VMDKDISK_HANDLE *diskHandle);

VMDKIO_API  VMDKIOError 
OpenVMDKFileWToUTF8(VMDKSNAPSHOT_HANDLE,
			  LPCWSTR vmdkFilePath,
			  VMDKDISK_HANDLE *diskHandle);


/// Determine the total number of sectors in this VMDK File 
/**
* @param handle [in] Hanlde to the disk
* @param totalSectors [out] Total number of sectors
* @return VMDKIO_OK if success, suitable VMDKIO error code otherwise.
*/
VMDKIO_API VMDKIOError      
GetVMDKTotalSectors(VMDKDISK_HANDLE handle, 
					VMDKIOLibSectorType *totalSectors);

/// Determine the size of individual sector in this VMDK File 
/**
* @param handle [in] Hanlde to the disk
* @param sectorSize [out] Sector size
* @return VMDKIO_OK if success, suitable VMDKIO error code otherwise.
*/
VMDKIO_API VMDKIOError 
GetVMDKSectorSize(VMDKDISK_HANDLE handle,
				  unsigned int *sectorSize);
	
/**
* Obtain the contain of sectors from an open VMDK file.
* @param diskHandle [in] Handle to an open virtual disk.
* @param startSector [in] Absolute offset.
* @param numSectors [in] Number of sectors to read.
* @param readBuffer [out] Preallocated Buffer to read into.
* @return VMDKIO_OK if success, suitable VMDKIO error code otherwise.
*/
VMDKIO_API VMDKIOError
GetVMDKSectors(VMDKDISK_HANDLE diskHandle,
			   VMDKIOLibSectorType startSector,
			   VMDKIOLibSectorType numSectors, 
			   UINT8 *readBuffer);


/**
* Write to the sectors to an open VMDK file.
* @param diskHandle [in] Handle to an open virtual disk.
* @param startSector [in] Absolute offset.
* @param numSectors [in] Number of sectors to write.
* @param writeBuffer [in] Buffer to write.
* @return VMDKIO_OK if success, suitable VMDKIO_OK error code otherwise.
*/
VMDKIO_API VMDKIOError
WriteVMDKSectors(VMDKDISK_HANDLE diskHandle,
				 VMDKIOLibSectorType startSector,
				 VMDKIOLibSectorType numSectors,
				 const UINT8 *writeBuffer);


/// Close an open VMDK File 
VMDKIO_API void 
CloseVMDKFile(VMDKDISK_HANDLE);


/**
* Returns the textual description of an error.
* @param err [in] A VIX error code.
* @return The error message string. This should only be deallocated
*         by VMDKIo_FreeErrorString.
*         Returns NULL if there is an error in retrieving text.
*/
VMDKIO_API char * 
VMDKIo_GetErrorString(VMDKIOError errcode);



/**
* Free the error message returned by VixDiskLib_GetErrorText.
* @param errMsg [in] Message string returned by VMDKIo_GetErrorString
*    It is OK to call this function with NULL.
* @return None.
*/
VMDKIO_API void
VMDKIo_FreeErrorString(char * errMsg);



/**
* Returns the textual description of an error in unicode.
* @param err [in] A VIX error code.
* @return The error message string. This should only be deallocated
*         by VMDKIo_FreeErrorString.
*         Returns NULL if there is an error in retrieving text.
*/
VMDKIO_API wchar_t * 
VMDKIo_GetErrorStringW(VMDKIOError errCode);



/**
* Free the error message returned by VMDKIo_GetErrorStringW.
* @param errMsg [in] Unicode Message string returned by VMDKIo_GetErrorString
*    It is OK to call this function with NULL.
* @return None.
*/
VMDKIO_API void 
VMDKIo_FreeErrorStringW(wchar_t * errMsg);

/**
* Returns the textual description of an error.
* @param errCode [in] A VIX error code.
* @param outErrMsg [out] The error message string buffer. 
* @param errMsgLen Length of the error message string buffer.
*/
VMDKIO_API int 
VMDKIo_GetErrorStringinBuffer(VMDKIOError errCode, char * outErrMsg, int errMsgLen);


/**
* Returns the transport that will be used for the opened disk.
* @param handle [in] Hande to Disk.
* @param transportUsedforDisk [out] The transport used as string. This string should not be freed.
*/
VMDKIO_API VMDKIOError      
VMDKIo_GetTransportUsedforDisk(VMDKDISK_HANDLE handle, wchar_t **transportUsedforDisk);


/**
* Returns the transport modes supported.
* @param transportUsedforDisk [out] hold the string for transport modes supported. This string should not be freed.
*/
VMDKIO_API VMDKIOError 
VMDKIo_ListTransportModes(wchar_t **transportUsedforDisk);

//bccma01 added
VMDKIO_API VMDKIOError
VMDKIo_MountVM(VMDKSNAPSHOT_HANDLE handle,
		const char **vmdkFilePath,
		const int vmdkFileCount,
		const char *mountPath,
//		VMDKMount **MountedVMDetails);
		void **MountedVMDetails,
		VMDKVOLDETAILS **volumeDetails,
		int *volumeCount,
		VMDKDISKDETAILS **diskDetails);


VMDKIO_API VMDKIOError
VMDKIoMnt_Init(void);

VMDKIO_API VMDKIOError
//VMDKIo_UnMountVM(VMDKMount **MountedVMDetails);
VMDKIo_UnMountVM(void **MountedVMDetails,
				 VMDKVOLDETAILS **volumeDetails);

VMDKIO_API void
VMDKIoMnt_Exit(void);

VMDKIO_API VMDKIOError
GetVMDKSignature(VMDKDISK_HANDLE handle, 
				 DWORD *pDiskSignature)	;

VMDKIO_API VMDKIOError      
GetVMDKDiskGeometry(VMDKDISK_HANDLE handle,
					PDISK_GEOMETRY pDiskGeometry);

//bccma01 : Cleanup API as recommended by VMware
VMDKIO_API VMDKIOError VMDKIo_Cleanup(char *vmxSpec,
										char *serverName,
										char *userName,
										char *password,
										UINT32 port);

VMDKIO_API VMDKIOError VMDKIo_CleanupV6(char *vmxSpec,
	char *serverName,
	char *userName,
	char *password,
	UINT32 port,
	VMDK_CONNECT_MORE_PARAMS * connect_more_params //<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll
	); //<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll

//mutga01: This function is used to notify the host of the virtual machine that the disks of the virtual machine will be opened by our backup application. The host disables operations on the virtual machine that may be adversely affected if they are performed while the disks are open by a third party application.

VMDKIO_API VMDKIOError      
VMDKIo_PrepareforAccess(char *vmxSpec,
			   char *serverName,
			   char *userName,
			   char *password,
			   UINT32 port);


VMDKIO_API   VMDKIOError
VMDKIo_PrepareforAccessV6(char *vmxSpec,
char *serverName,
char *userName,
char *password,
UINT32 port,
VMDK_CONNECT_MORE_PARAMS * connect_more_params //<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll
); //<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll

//mutga01 : This function is used to notify the host of a virtual machine that the virtual machine disks are closed and that the operations which rely on the virtual machine disks to be closed can now be allowed. 

VMDKIO_API VMDKIOError      
VMDKIo_EndAccess(char *vmxSpec,
			   char *serverName,
			   char *userName,
			   char *password,
			   UINT32 port);

VMDKIO_API   VMDKIOError
VMDKIo_EndAccessV6(char *vmxSpec,
char *serverName,
char *userName,
char *password,
UINT32 port,
VMDK_CONNECT_MORE_PARAMS * connect_more_params //<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll
); //<sonmi01>2015-4-2 #vddk 6 support - vmdkio.dll

#if defined (__cplusplus)
}
#endif // __cplusplus
