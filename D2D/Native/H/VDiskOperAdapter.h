// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the VDISKOPERADAPTER_EXPORTS
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// VDISKOPERADAPTER_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.
#ifndef _VDISKOPER_ADAPTER_H_INCLUDED_
#define _VDISKOPER_ADAPTER_H_INCLUDED_

#ifdef VDISKOPERADAPTER_EXPORTS
#define VDISKOPERADAPTER_API __declspec(dllexport)
#else
#define VDISKOPERADAPTER_API __declspec(dllimport)
#endif

#include "Log.h"
#include "VDiskOper.h"
#include "RPSCOMM\api_comm.h"
#include "..\Virtualization\HostVMBackup\HyperVBackupInteractLib\CommonDef.h"
#include "UDPResource.h"

typedef DWORD (*PF_VMJobLogActivity)(WCHAR* nodeUUID,DWORD Flags, DWORD JobNo, DWORD ResouceID, ...);

typedef struct tagVDiskAdapterParam
{
	PF_VMJobLogActivity pfVMJobLogActivity;
	PJOBCTX pJobCtx;
	LPWSTR lpstrServerNameForDisplay;
}VDISK_ADAPTER_PARAM_S;

class IVDiskOperAdapter
{
public:
	virtual DWORD Initialize(CONST VDISK_ADAPTER_PARAM_S * pParam = NULL) = 0;
	virtual VOID  Release() = 0;
	virtual DWORD Open(IN CONST VDISK_CREATE_PARAM_S* pstCreateParam,
		               IN ULONG ulDesiredAccess,
					   IN ULONG ulShareMode,
					   IN ULONG ulCreationDisposition) = 0;
	virtual DWORD SetPointerEx(LONGLONG llDistanceToMove, LONGLONG* lpNewFilePointer, ULONG ulMoveMethod) = 0;
	virtual DWORD Read(ULONG nNumberOfBytesToRead, ULONG* lpNumberOfBytesRead, BYTE* pBuffer) = 0;
	virtual DWORD Write(ULONG nNumberOfBytesToWrite, ULONG* lpNumberOfBytesWritten, CONST BYTE* pBuffer, BOOL bCheckStatus = FALSE) = 0;
	virtual DWORD Close() = 0;
	virtual DWORD Delete(LPCWSTR lpVDiskPath) = 0;
	virtual DWORD Rename(LPCWSTR lpExistingFilePath, LPCWSTR lpNewDiskName) = 0;
	virtual DWORD RemoveDiskProtectionFromCBT(LPCWSTR lpVMUUID) = 0;

protected:
	PF_VMJobLogActivity _pfVMJobLogActivity;
	PJOBCTX m_pJobCtx;
	wstring m_strServerNameForDisplay;
};


VDISKOPERADAPTER_API IVDiskOperAdapter* CreateInstanceVDiskOperAdapter(IN CONST wstring& strServerName, IN CONST wstring& strUserName, IN CONST wstring& strPassword);//create the Virtual Disk on the server

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class IVDiskServerManager
{
public:
	virtual VOID Release() = 0;
	virtual VOID SetQuitEvent(PHANDLE phEvtQuit) = 0;
	virtual DWORD Initialize(comm_global_param* pServerParam) = 0;
	virtual bool CreateServer(transport_n enTranType, USHORT usPort, bool bBlockMode = false) = 0;
	virtual bool StopServer() = 0;
	virtual USHORT GetPort() = 0;
	virtual bool IsPortBound(USHORT usPort) = 0;
};
VDISKOPERADAPTER_API IVDiskServerManager* CreateInstanceVDiskServerMgr();

///////////////////////////////////////////////////////////////////////////////////////////////
#define VDISK_DETAILS_DEFAULT_QUERY_TIMEOUT (6 * 60) //six mins
#define VDISK_DETAILS_DEFAULT_SYNC_TIMEOUT  (5 * 60) //five mins
typedef struct tagVDiskStubDetails
{	
	ULONG ulListenPort;
	ULONG ulProcessId;
}VDISK_STUB_DETAILS_S;

VDISKOPERADAPTER_API DWORD VDiskQueryStubDetails(OUT VDISK_STUB_DETAILS_S* pstStubDetails, IN const LPCWSTR lpStubUuid, IN const LPCWSTR lpServerName, IN ULONG ulTimeOutInSec = VDISK_DETAILS_DEFAULT_QUERY_TIMEOUT);

VDISKOPERADAPTER_API DWORD VDiskSyncStubDetails(IN const VDISK_STUB_DETAILS_S* pstStubDetails, IN const LPCWSTR lpStubUuid, IN const ULONG ulTimeOutInSec = VDISK_DETAILS_DEFAULT_SYNC_TIMEOUT);

//////////////////////////////////////////////////////////////////////////////////////////////////

#endif