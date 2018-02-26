#pragma once
#include <string>
#include <vector>
#include "haClientProxy.h"
#include "Virtualization\VMDKIo.h"
#include "Virtualization\VMwareVolumeInfo.h"

using namespace std;

#ifndef MAX_NT_PATH
#define MAX_NT_PATH	1024
#endif

#define MAX_GUID_LENGTH	39
#define THUMBPRINT_LENGTH 60

//#define VMJOB_SCRIPT_SIZE 12114 //  > 12064 actual size including VM_JOB + header size 12
//#define VMJOB_SCRIPT_SIZE 16210 //  > 12064 actual size including VM_JOB + header size 12
//#define VMJOB_SCRIPT_SIZE 16214 // after adding unsigned long blocksize
//#define VMJOB_SCRIPT_SIZE 16222 // after adding unsigned __int64 vddkErrorCode
//#define VMJOB_SCRIPT_SIZE 16230 // after adding unsigned __int64 d2dErrorCode
//#define VMJOB_SCRIPT_SIZE 18278 // after adding TCHAR VMUniqueID field
//#define VMJOB_SCRIPT_SIZE 18788 // after adding size of szVolID
//#define VMJOB_SCRIPT_SIZE 20840 // after adding TCHAR drzFilePath field and isUEFI field
//#define VMJOB_SCRIPT_SIZE 20960   // after adding TCHAR szThumbPrint field
//#define VMJOB_SCRIPT_SIZE 22520		// after adding VMwareVolumeInfo volInfo field
#define VMJOB_SCRIPT_SIZE 23560		// after adding szNetConnUser szNetConnPwd field

#define VM_PROGRESS_SCRIPT_SIZE 600

#define ARGUMENT_LIST_VMJOB_SIZE sizeof(VM_JOB)

//#define szName TEXT("Global\\MyFileMappingObject")	//zxh,this macro define was conflicted with atlstr.h,it seems that this macro was not used,if want to use it please rename it.

#define ARGUMENT_LIST_VM_PROGRESS_SIZE sizeof(VM_REPORT_PROGRESS)
#define D2D2VMDK_SUCCESS	0
#define D2D2VMDK_FAIL	1
#define D2D2VMDK_CANCEL	2
#define D2D2VMDK_HOTADD_SUCCESS	3
#define D2D2VMDK_HOTADD_FAIL	4
#define D2D2VMDK_FAIL_ENCRYPT	5
#define VMDKIO_ERROR_FILE_ACCESS_ERROR	13
#define REF_TOKEN_NOT_EXIST	1008

#define ARGUMENT_HEADER_ID	8001

typedef	struct	_ARGUMENT_HEADER{
	DWORD				dw_header_id;
	DWORD				dw_data_size;
	DWORD				dwReserved;
} ARGUMENT_HEADER, *PARGUMENT_HEADER;

#define ARGUMENT_HEADER_SIZE sizeof(ARGUMENT_HEADER)

#define STATUS_NOT_READY_FOR_READ     1
#define STATUS_READY_FOR_READ         2


#define ARGUMENT_LIST_VMJOB_SIZE sizeof(VM_JOB)

enum diskType{
	COMPRESSED = 0,
	UNCOMPRESSED = 1,
	INVALID_TYPE = 3
};

enum jobType{
	GET_SIGNATURE = 0,
	INJECT_DRIVERS = 1,
	OFFLINE_COPY = 2,
	SET_GEOMETRY = 3,
	CANCEL_JOB = 4,
	CANCELED_JOB = 5,
	INVALID_JOB = 6,
	OFFLINE_COPY_DISKSIGN_WRITE = 7,
	OFFLINE_COPY_SMART = 8,
	INJECT_DRIVERS_SINGLE_VMDK = 9,
	INJECT_SERVICE_AND_TOOLS = 10 //<sonmi01>2012-8-17 #inject service #001
};

#define PIPE_TIMEOUT (1*60*60)

// Any changes in vmJob structure update the VMJOB_SCRIPT_SIZE size accordingly

typedef struct vmJob{
	TCHAR szESX[MAX_PATH];
	TCHAR szUser[MAX_PATH];
	TCHAR szPwd[MAX_PATH];
	TCHAR szMoref[28];
	TCHAR szSystemDsk[MAX_NT_PATH];
	TCHAR szBootDsk[MAX_NT_PATH];
	TCHAR szVolID[MAX_PATH];
	TCHAR szNewHost[MAX_PATH];
	TCHAR szfailoverMode[MAX_PATH];
	TCHAR szVmdkUrl[MAX_NT_PATH];
	TCHAR szD2DPath[MAX_NT_PATH];
	TCHAR D2DFilePathBegin[MAX_NT_PATH];
	TCHAR D2DFilePathEnd[MAX_NT_PATH];
	TCHAR szJobID[MAX_PATH];
	TCHAR szSnapMoref[28];
	TCHAR szKey[MAX_PATH];
	DWORD vmdkError;
	HANDLE pValue;
	long shrdMemLength;
	TCHAR diskSignature[MAX_GUID_LENGTH];
	diskType szD2DType;
	long lPort;
	jobType job;
	BOOL operationStatus;
	unsigned long blocksize;
	int nBackupDescType;
	unsigned __int64 vddkErrorCode;
	unsigned __int64 d2dErrorCode;
	TCHAR VMUniqueID[MAX_NT_PATH];
	bool isUEFI;
	TCHAR szNetConnUser[MAX_PATH];
	TCHAR szNetConnPwd[MAX_PATH];
	TCHAR drzFilePath[MAX_NT_PATH];
	TCHAR szThumbPrint[THUMBPRINT_LENGTH];				//modified by zhepa02 at 2015-04-09 to support vddk 6,add the thumbPrint
	VMwareVolumeInfo volInfo;
}VM_JOB, *PVM_JOB;

typedef struct ReportProgress{
	unsigned __int64 totalBytesWritten;
	unsigned __int64 totalBytes;
}REPORT_PROGRESS, *PREPORT_PROGRESS;

// Any changes in ReportThreadArg structure update the VM_PROGRESS_SCRIPT_SIZE accordingly

typedef struct ReportThreadArg{
	TCHAR JobID[MAX_PATH];
	IHADTCallback* pCallback;
    HANDLE volatile hPipe; //the handle of named pipe
}REPORT_TARG, *PREPORT_TARG;

DWORD WINAPI DriverInject(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, const VMwareVolumeInfo& volInfo, const wstring& newHostName, const wstring& szFailoverMode, UINT32 iPort, std::vector<std::wstring> strVMDKURL, const wstring& key, const wstring& value, const wstring& JOBID, const wstring& strAfguid, bool isUEFI, const wstring& wstrDrzFilePath);
//modified by zhepa02 to support vddk 6 at 2015-04-13, add thumbprint
DWORD WINAPI GetVMDKSignature(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId,
	UINT32 iPort, VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strSnapMoref, const wstring& strVMDKURL, wstring& diskSig, const wstring& JOBID, const wstring& strAfguid);

DWORD WINAPI SetVMDKGeometry(const wstring& strEsx, const wstring& strUser, const wstring& strPassword,
	const wstring& strMorefId, UINT32 iPort, VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strVMDKURL, long volOffset, const wstring& JOBID, const wstring& strAfguid);

DWORD WINAPI D2D2VmdkConversion(const wstring& D2DFilePath, const wstring& Moref, const wstring& EsxHostName, const wstring& EsxUserName, const wstring& EsxUserPassword,
	const wstring& VmdkDiskUrl, DWORD VDDKPort, VMDK_CONNECT_MORE_PARAMS& moreParams, DWORD D2DType, const wstring& strSnapMoref,
	const wstring& JobID, const wstring& VMUniqueID, unsigned long blocksize, int nBackupDescType, 
	const wstring& NetConnUserName, const wstring& NetConnPwd, unsigned long &vddkErrorCode, unsigned long &d2dErrorCode, IHADTCallback* pCallback);

DWORD WINAPI UpdateDiskSignNbd(const wstring& D2DFilePath, const wstring& Moref, const wstring& EsxHostName, const wstring& EsxUserName, const wstring& EsxUserPassword,
	const wstring& VmdkDiskUrl, DWORD VDDKPort, VMDK_CONNECT_MORE_PARAMS& moreParams, DWORD D2DType, const wstring& strSnapMoref, const wstring& JobID,
	const wstring& VMUniqueID, int nBackupDescType, const wstring& NetConnUserName, const wstring& NetConnPwd, IHADTCallback* pCallback);

bool WINAPI D2DCancelConversion(const wstring& JobID);

DWORD WINAPI D2D2VmdkReportProgress(PREPORT_TARG pREPORT_ARG);

DWORD WINAPI D2D2VmdkSmartCopy(const wstring& D2DFilePathBegin, const wstring& D2DFilePathEnd, const wstring& Moref, const wstring& EsxHostName, const wstring& EsxUserName,
	const wstring& EsxUserPassword, const wstring& VmdkDiskUrl, DWORD VDDKPort, VMDK_CONNECT_MORE_PARAMS& moreParams, DWORD D2DType, const wstring& strSnapMoref, const wstring& JobID,
	const wstring& VMUniqueID, unsigned long blocksize, int nBackupDescType, const wstring& NetConnUserName, const wstring& NetConnPwd, unsigned long &vddkErrorCode, unsigned long &d2dErrorCode, IHADTCallback* pCallback);

DWORD WINAPI RVCMInjectService(const wstring& strEsx,const wstring& strUser,const wstring& strPassword,const wstring& strMorefId,const VMwareVolumeInfo& volInfo,const wstring& szFailoverMode,
	UINT32 iPort,VMDK_CONNECT_MORE_PARAMS& moreParams,std::vector<std::wstring> strVMDKURL,const wstring& szFileDirInKey,const wstring& JOBID);

DWORD WINAPI DriverInjectSingleVMDK(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId,
	const wstring& newHostName, const wstring& szFailoverMode, UINT32 iPort, VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strVMDKURL, const wstring& key,
	const wstring& value, const wstring& JOBID, const wstring& strAfguid);



