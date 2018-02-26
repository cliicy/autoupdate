#pragma once

//////////////////////////////////////////////////////////////////////////
// command 
#define  D2D_CALLBACK_CMD_BASE 0xF0000000
#define  CMD_SEND_MERGE_MALI	D2D_CALLBACK_CMD_BASE + 0x1
#define  CMD_BACKUP_VM_FAILED	D2D_CALLBACK_CMD_BASE + 0x2
#define  CMD_CHECK_LICENSE		D2D_CALLBACK_CMD_BASE + 0x3
#define  CMD_RES_CHECK_LICENSE  D2D_CALLBACK_CMD_BASE + 0x4
#define  CMD_CHILD_VM_JOB_CONTEXT		D2D_CALLBACK_CMD_BASE + 0x5 //<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm
#define  CMD_RES_CHILD_VM_JOB_CONTEXT	D2D_CALLBACK_CMD_BASE + 0x6

//////////////////////////////////////////////////////////////////////////
#include <windows.h>
#define  D2D_PXY_CMD_SIGNATURE		0xFF88FF88
#define  D2D_PXY_CMD_RES_SIGNATURE	0xFF99FF99

#pragma pack(4)

#define  IPC_CALLBACK_VER_1   1

typedef struct _D2DIPC_MSG_HEADER
{
	DWORD version;
	DWORD signature;
	DWORD cmdid;	
	DWORD oricmdid; //used for response 
	DWORD retval;
	WCHAR msg[64];
}D2DIPC_MSG_HEADER, *PD2DIPC_MSG_HEADER;

#pragma pack()

class AtomCodec
{ 
protected:
	union  d2
	{
		short n2;
		WORD  w2;
		BYTE data[2];
	};

	union d4
	{
		int n4;
		DWORD32 w4;
		long l4;
		float f4;
		BYTE data[4];
	};

	union d8
	{
		DWORD64 w8;
		DATE	date8;
		double	double8;
		BYTE data[8];

	};

public:
	static BOOL d2stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, WORD wdata);
	static BOOL stream2d2(const BYTE * stream ,const DWORD streamLen, DWORD & dwIndex, WORD & wdata);

	static BOOL d4stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, DWORD dwdata);
	static BOOL stream2d4(const BYTE * stream ,const DWORD streamLen, DWORD & dwIndex, DWORD32 & dwdata);

	static BOOL l4stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, long dwdata);
	static BOOL stream2l4(const BYTE * stream ,const DWORD streamLen, DWORD & dwIndex, long & dwdata);

	static BOOL f4stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, float dwdata);
	static BOOL stream2f4(const BYTE * stream ,const DWORD streamLen, DWORD & dwIndex, float & dwdata);

	static BOOL d8stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, DWORD64 dwdata);
	static BOOL stream2d8(const BYTE * stream ,const DWORD streamLen, DWORD & dwIndex, DWORD64 & dwdata);

	static BOOL wstr2stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, const WCHAR * pszData);
	static BOOL stream2wstr(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, WCHAR ** ppszData, WORD & strsize);

	static BOOL binary2stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, const BYTE * pbzData, WORD dwsizeOfdata);
	static BOOL stream2binary(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, BYTE ** ppbzData, WORD & dwsizeOfdata);

	static BOOL header2stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, PD2DIPC_MSG_HEADER pHeader);
	static BOOL stream2header(const BYTE * stream, const DWORD streamLen, DWORD & dwIndex, PD2DIPC_MSG_HEADER pHeader);

};
//////////////////////////////////////////////////////////////////////////
#pragma pack(4)
#define  MERGE_SOURCE_BACKUP		1 // the mail is sent by d2d backup job
#define  MERGE_SOURCE_CATALOG		2 // the mail is sent by catalog job
#define  MERGE_SOURCE_BACKUP_VM		3 // the mail is sent by backup VM job
#define  MERGE_SOURCE_CATALOG_VM	4 // the mail is sent by catalog VM job

typedef struct st_merge_info
{
	DWORD32 dwRangeStart;
	DWORD32 dwRangeEnd;
	DWORD32 dwFailedStart;
	DWORD32 dwFailedEnd;
	DWORD32 dwJobID;
	DWORD32 dwRetCode;
	DWORD32 dwSource;	// MERGE_SOURCE_BACKUP MERGE_SOURCE_CATALOG
	WCHAR	VMInstance[128];
}D2DIPC_MERGE_INFO, *PD2DIPC_MERGE_INFO;

typedef struct st_backupvm_info
{
	DWORD32 dwJobID;
	DWORD32 dwRetCode;
}D2DIPC_VMBACKUP_INFO, *PD2DIPC_VMBACKUP_INFO;

//////////////////////////////////////////////////////////////////////////

#define COMID_CHK_UNDER_EDGE	0xFFFF

typedef struct st_chklic_item
{
	long CompntID;				// set by background side, the mask of sub-feature 
	BOOL  isRevered;			// set by Java side.
	long  retVal;
	st_chklic_item()
	{
		CompntID =-1;
		isRevered =FALSE;
	}

}D2D_CHKLIC_ITEM, *PD2D_CHKLIC_ITEM;

typedef struct st_chklic_listentry
{
	DWORD32 size;
	DWORD32 version;
	DWORD32 processId;
	DWORD32 Mask;			// in: the mask of sub-feature 
	DWORD32 SocketNum;		// in: the number of CPU socket
	DWORD32 LicID;			// out: the allocated license id
	DWORD64 flags;
	DWORD32 JobID;
	DWORD32 listsize;
	PD2D_CHKLIC_ITEM pvItemList;
}D2D_CHKLIC_LISTENTRY, * PD2D_CHKLIC_LISTENTRY;

typedef struct st_chklic_list
{
	DWORD size;
	DWORD version;
	DWORD processXid;
	DWORD64 flags;
	WCHAR szComputerName[256]; 
	DWORD listsize;
	PD2D_CHKLIC_ITEM pvItemList;
}D2D_CHKLIC_LIST, *PD2D_CHKLIC_LIST;


//<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm
#if 0 //Java job context
long         dwJobId;//The job id
long         dwQueueType;//Job queue type 1 for regular 2 for on demand both of them for catalog job
//unknow for other jobs
long         dwJobType;//the job type
long         dwProcessId;
long         dwJMShmId;//same as job id
String       launcherInstanceUUID;
long         dwLauncher;
String       executerInstanceUUID;
long         dwPriority;//the job priority, 0 - VM , 1 - Child VM under vApp, 2 - vApp, vApp job has the highest priority
long       dwMasterJobId; // specific for vApp master job, so that child jobs can associate with.
String     generatedDestination; //vApp child VM backup destination path
#endif

typedef struct st_cpp_vm_job_context_item
{
	DWORD32			dwJobId;//The job id
	DWORD32			dwQueueType;//Job queue type 1 for regular 2 for on demand both of them for catalog job //unknow for other jobs
	DWORD32			dwJobType;//the job type
	DWORD32			dwProcessId;
	DWORD32			dwJMShmId;//same as job id
	DWORD32			dwLauncher;
	DWORD32			dwPriority;//the job priority, 0 - VM , 1 - Child VM under vApp, 2 - vApp, vApp job has the highest priority
	DWORD32			dwMasterJobId; // specific for vApp master job, so that child jobs can associate with.
	WCHAR			executerInstanceUUID[64];
	WCHAR			launcherInstanceUUID[64];
	WCHAR			generatedDestination[512]; //vApp child VM backup destination path

	//////////////////////////////////////////////////////////////////////////
	st_cpp_vm_job_context_item()
	{
		ZeroMemory(this, sizeof(*this));
	}

} D2D_CPP_VM_JOB_CONTEXT_ITEM, *PD2D_CPP_VM_JOB_CONTEXT_ITEM;

typedef struct st_cpp_vm_job_context_list
{
	DWORD32 listsize;
	PD2D_CPP_VM_JOB_CONTEXT_ITEM items;

	//////////////////////////////////////////////////////////////////////////
	st_cpp_vm_job_context_list()
	{
		ZeroMemory(this, sizeof(*this));
	}
} D2D_CPP_VM_JOB_CONTEXT_LIST, *PD2D_CPP_VM_JOB_CONTEXT_LIST;


#pragma pack()

class CMDcodec
{
public://pack
	static DWORD PackHeader(DWORD CMDid, BYTE * & pStream, DWORD & dwLenStream);
	static DWORD PackResponse(DWORD dwRetVal, DWORD CMDid,DWORD oriCMDid, BYTE * & pStream, DWORD & dwLenStream);
	static DWORD PackResponse(PD2DIPC_MSG_HEADER pHeader , BYTE * & pStream, DWORD & dwLenStream);
	static DWORD PackMerge(PD2DIPC_MERGE_INFO pstInfo, BYTE * & pStream, DWORD & dwLenStream);
	static DWORD PackVMINFO(PD2DIPC_VMBACKUP_INFO pstInfo, BYTE * & pStream, DWORD & dwLenStream);
	//License
	static DWORD PackLicList(PD2DIPC_MSG_HEADER pHeader, PD2D_CHKLIC_LISTENTRY pListEntry, BYTE * & pStream, DWORD & dwLenStream);
public://unpack
	static DWORD UnpackHeader(BYTE * pStream, DWORD dwLenStream ,PD2DIPC_MSG_HEADER pHeader);
	static DWORD UnpackResponse(BYTE * pStream, DWORD dwLenStream ,PD2DIPC_MSG_HEADER pHeader);
	static DWORD UnpackMerge(BYTE * pStream, DWORD dwLenStream,PD2DIPC_MERGE_INFO pMergeinfo);
	static DWORD UnpackVMINFO(BYTE * pStream, DWORD dwLenStream,PD2DIPC_VMBACKUP_INFO pVMinfo);
	//License
	static DWORD UnpackLicList(BYTE * pStream, DWORD dwLenStream ,PD2DIPC_MSG_HEADER pHeader,  PD2D_CHKLIC_LISTENTRY pListEntry);
	static DWORD ReleaseLicList(PD2D_CHKLIC_LISTENTRY pListEntry);

public: //<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm
	static DWORD Pack_D2D_CPP_VM_JOB_CONTEXT_LIST(/*PD2DIPC_MSG_HEADER pHeader, */PD2D_CPP_VM_JOB_CONTEXT_LIST pListEntry, BYTE * & pStream, DWORD & dwLenStream);
	static DWORD Unpack_D2D_CPP_VM_JOB_CONTEXT_LIST(/*PD2DIPC_MSG_HEADER pHeader, */PD2D_CPP_VM_JOB_CONTEXT_LIST pListEntry, BYTE * & pStream, DWORD & dwLenStream);
	static DWORD Release_D2D_CPP_VM_JOB_CONTEXT_LIST(PD2D_CPP_VM_JOB_CONTEXT_LIST pListEntry);
	static DWORD test_D2D_CPP_VM_JOB_CONTEXT_LIST();
};

//////////////////////////////////////////////////////////////////////////