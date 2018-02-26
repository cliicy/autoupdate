#ifndef _INSTANTVM
#define _INSTANTVM

#include <cmath>
#define IVMJOB_SCRIPT_SIZE 18278
#define ARGUMENT_HEADER_ID	8011
#define ARGUMENT_HEADER_SIZE sizeof(ARGUMENT_HEADER)

#define IVM_AGENT_EVENT_PREFIX						 L"Global\\"
#define DRIVER_LOADED_EVENT_NAME_SUFFIX				 L"-DRIVER-LOAD"
#define PROVIDER_THREAD_EVENT_NAME_SUFFIX			 L"-REGISTER-PROVIDER-THREAD"
#define PROVIDER_THREAD_UNREGISTER_EVENT_NAME_SUFFIX L"-UNREGISTER-PROVIDER-THREAD"
#define STOP_VM_EVENT_SUFFIX						 L"-STOP-VM"
#define SHAREMEMORYCHANGE_EVENT_NAME_SUFFIX			 L"-SHAREMEMORY-CHANGE"

#define INSTANT_VM_AGENT_EXE						 _T("InstantVMAgent.exe")

typedef	struct	_ARGUMENT_HEADER{
	DWORD				dw_header_id;
	DWORD				dw_data_size;
	DWORD				dwReserved;
} ARGUMENT_HEADER, *PARGUMENT_HEADER;

enum IVM_CMD
{
	CMD_NONE = 0,
	CMD_START = 0x1,
	CMD_STOP = 0x2,
	CMD_QUERY = 0x4,
	CMD_RESTART = 0x8,
	CMD_START_HYDRATION = 0x10,
	CMD_STOP_HYDRATION = 0x20
};

enum IVM_STATUS{
	IVM_INIT = 0,
	IVM_START,
	IVM_CREATED,
	IVM_RUNNING,
	IVM_STOPPING,
	IVM_STOPPED,
	IVM_FAILED = 10,
	IVM_CANCELLING = 11,
	IVM_CANCELLED = 12,
	IVM_ERROR_RECOVERABLE = 13 // stand for the error to read data.
};

enum IVM_POWER{
	IVM_UNKNOWN = 0,
	IVM_POWER_ON,
	IVM_POWER_OFF
};

enum IVM_ALIVE_STATUS{
	IVM_STATUS_UNKOWN = 0,
	IVM_STATUS_OK,
	IVM_STATUS_FAILED,
	IVM_STATUS_CHECKING
};
enum IVM_ALIVE_TYPE{
	IVM_NONE_T = 0,
	IVM_HEARTBEAT_T =0x0001,
	IVM_PING_T = 0x0002,
	IVM_CUSTOMER_SCRIPT_ON_PROXY_T = 0x0004,
	IVM_CUSTOMER_SCRIPT_ON_GUEST_SERVER_T = 0x0008
};
#define ALIVE_ITEM_INDEX(S)       ((unsigned)log2((unsigned)(S))) 
#define IVM_ALIVE_TYPE_COUNT  4
typedef struct _IVM_ALIVE_CHECK_ITEM
{
	IVM_ALIVE_STATUS AliveCheckStatus;
	UINT64  AliveCheckTimestamp;
	IVM_ALIVE_TYPE AliveCheckType;
	_IVM_ALIVE_CHECK_ITEM():AliveCheckType(IVM_NONE_T), AliveCheckStatus(IVM_STATUS_UNKOWN), AliveCheckTimestamp(0) {}
}IVM_ALIVE_CHECK_ITEM, *PIVM_ALIVE_CHECK_ITEM;

enum IVM_SCRIPT_TYPE{
	IVM_SCRIPT_NONE_T = 0,
	IVM_SCRIPT_PRE = 0x0001,
	IVM_SCRIPT_POST = 0x0002,
};

typedef struct _IVM_SCRIPT_EXEC_ITEM
{
	int CommandExecStatus;
	UINT64  CommandExecTimestamp;
	IVM_SCRIPT_TYPE CommandExecType;
	_IVM_SCRIPT_EXEC_ITEM() :CommandExecStatus(0), CommandExecType(IVM_SCRIPT_NONE_T), CommandExecTimestamp(0) {}
}IVM_SCRIPT_EXEC_ITEM, *PIVM_SCRIPT_EXEC_ITEM;

typedef struct IVMJob{
	TCHAR szInstantVMName[MAX_PATH];
	TCHAR szInstantVMGUID[MAX_PATH];
	IVM_STATUS ivm_status;
	IVM_POWER ivm_power;
	IVM_CMD ivm_cmd;
	DWORD dwPID;
	DWORD dwJobID;
	DWORD dwJobType;
}IVM_JOB, *PIVM_JOB;




//////////////////////////////////////////////////////////////////////////// 
// -------------------------------------------------------------------
//  |  |	Function Bit Mask												|
//  |31|    30 - 0 bit														|
// -------------------------------------------------------------------
//  If Bit 31 is set 1, all the other bit mask is ignored and considered to be set the value 1 against the specified Hyper-visor type.
//  Under this conditions, the function will be checked regardless its actual bit mask value.
//  If Bit 31 is set 0, the check results depend on the left Bit settings.
//  currently if the first check item fails, the next one will not be checked if there are more than multiple bit masks.
/////////////////////////////////////////////////////////////////////////////

#define  SET_CHECK_ALL_ITEMS(x)       							    ((x) | 0x80000000LL)
#define  CLEAR_CHECK_ALL_ITEMS(x)									((x) & 0x7FFFFFFFLL)
#define  IS_CHECK_ALL_ITEMS(x)										((x) & 0x80000000LL)
#define  IS_ITEM_CHECK_REQUIRED(value, mask)						((value) & (mask))
#define  SET_ITEM_CHECKBIT(value, mask)                             ((value) | (mask))

#define  SET_CLIENT_OS_VERSION_CPU_CHECKBIT						    0x00000001LL              // Client OS Version and CPU Architecture of Windows 10 or Later.
#define  SET_SERVER_OS_VERSION_CPU_CHECKBIT						    0x00000002LL              // OS Version and CPU Architecture of Windows Server 2008 R2 or later.
#define  SET_DRIVER_EXISTS_CHECKBIT							        0x00000004LL			  // Drive has been installed
#define  SET_DIRECTORY_VALID_CHECKBIT							    0x00000008LL			  // Directory is valid.
#define  SET_DIRECTORY_EXISTS_CHECKBIT							    0x00000010LL			  // Directory exists.
#define  SET_DIRECTORY_COMPRESS_EFS_CHECKBIT                        0x00000020LL			  // Directory has a compression attribute
#define  SET_DRIVE_TYPE_CHECKBIT								    0x00000040LL			  // Drive Type
#define  SET_FILE_SYSTEM_CHECKBIT								    0x00000080LL			  // File system checking
#define  SET_NFS_FEATURE_CHECKBIT									0x00000100LL			  // NFS Feature checking 

typedef struct	_PRE_CHECK_STRUCT
{
	TCHAR vmConfigPath[MAX_PATH];
	TCHAR node_uuid[MAX_PATH];
	TCHAR vm_name[MAX_PATH];
	DWORD job_type;
	DWORD jobID;
	DWORD hypervisor_type;	// 0: hyper-v, 1: vmware
	DWORD check_mask;
	DWORD error_mask;				 //indicates the bit checking results.
	BOOL  exits_once_error;		     //indicated whether the check exists until it catches a unsatisfied condition. 
									 //False indicated it exits until it meet the first unsatisfied condition.

	_PRE_CHECK_STRUCT() :check_mask(0), error_mask(0), hypervisor_type(0), job_type(0), jobID(0), exits_once_error(0)
	{
		ZeroMemory(vmConfigPath, MAX_PATH * sizeof(TCHAR));
		ZeroMemory(vmConfigPath, MAX_PATH * sizeof(TCHAR));
		ZeroMemory(vm_name, MAX_PATH * sizeof(TCHAR));
	}

}PRE_CHECK_STRUCT;

#endif