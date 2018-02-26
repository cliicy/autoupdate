// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the VDISKOPER_EXPORTS
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// VDISKOPER_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.
#ifndef _VDISKOPER_H_INCLUDED_
#define _VDISKOPER_H_INCLUDED_

#ifdef VDISKOPER_EXPORTS
#define VDISKOPER_API __declspec(dllexport)
#else
#define VDISKOPER_API __declspec(dllimport)
#endif

//#include <virtdisk.h>

typedef enum
{
	VDISK_TYPE_UNKNOWN = 0,
	VDISK_TYPE_VHD,
	VDISK_TYPE_VHDX
}VDISK_TYPE_E;

typedef enum
{
	VDISK_STORAGE_TYPE_DYNAMIC = 0,
	VDISK_STORAGE_TYPE_FIXED,
	VDISK_STORAGE_TYPE_DIFF
}VDISK_STORAGE_TYPE_E;

typedef struct tagVDiskCreateParam
{
	LPCWSTR strVDiskPath;
	LPCWSTR strParentPath;	
	VDISK_TYPE_E  dwVDiskType; //Vhd  or Vhdx
	VDISK_STORAGE_TYPE_E  enDiskStorageType;//dynamic or fixed
	ULONGLONG  ullFileSize;
	DWORD      dwBlockSize;
	DWORD      dwLogicalSectorSize;
	DWORD      dwPhysicalSectorSize;
	GUID	   gUniqueId;

}VDISK_CREATE_PARAM_S;

class IVDiskOper
{
public:
	virtual VOID  Release() = 0;
	virtual DWORD Open(IN CONST VDISK_CREATE_PARAM_S* pstCreateParam,
		               IN ULONG ulDesiredAccess,
		               IN ULONG ulShareMode,
		               IN ULONG ulCreationDisposition,
					   IN BOOL bSequential) = 0; //create, open and attach the disk
	virtual DWORD SetPointerEx(LONGLONG llDistanceToMove, LONGLONG* lpNewFilePointer, ULONG ulMoveMethod) = 0;
	virtual DWORD Read(ULONG nNumberOfBytesToRead, ULONG* lpNumberOfBytesRead, BYTE* pBuffer) = 0;
	virtual DWORD Write(ULONG nNumberOfBytesToWrite, ULONG* lpNumberOfBytesWritten, CONST BYTE* pBuffer) = 0;
	virtual DWORD Close() = 0; //close and detach the disk
	virtual wstring GetDiskPath() = 0;
};

//create oper instance
VDISKOPER_API IVDiskOper* CreateInstanceVDiskOper();

#endif