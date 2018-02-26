/**************************************************************************
Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.

 Program Name:  Queue Server Interface (QSI) API Header
      Version:  Release 1.0, Rev 1.0
 Version Date:  June 11, 1994
**************************************************************************/

#ifndef _INC_QSI
#define _INC_QSI
#define MAINFUNC
#include "bebenabled.h"

#ifndef IN
#define IN
#endif

#ifndef OUT
#define OUT
#endif

#ifdef __cplusplus
  extern "C" {                     /* avoid name-mangling if used from C++ */
#endif /* __cplusplus */
#if defined ASCORE_SCALABILITY_ENHANCEMENT
#ifndef MAX_PATH
#define MAX_PATH 260
#endif
#endif

#ifndef JS_ACTIVE        
#define JS_ACTIVE       0
#endif
#ifndef	JS_FINISHED
#define JS_FINISHED     1
#endif
#ifndef JS_CANCELLED
#define JS_CANCELLED    2
#endif
#ifndef JS_FAILED
#define JS_FAILED        3
#endif
#ifndef JS_INCOMPLETE
#define JS_INCOMPLETE   4
#endif
#ifndef JS_IDLE
#define JS_IDLE         5
#endif
#ifndef JS_WAITING
#define JS_WAITING      6
#endif

#define MAX_NO_OF_QUEUES    8 
#define MAX_NO_OF_JOBS      4096

#define NO_MORE_QUEUES      600
#define NO_SUCH_JOB         601
#define NO_JOB_FOR_SERVICE  602

#define JOB_READY           1
#define JOB_HOLD            2
#define JOB_ACTIVE          3
#define JOB_FAILED          4

#define QUEUE_READY         1
#define QUEUE_HOLD          2
#define QUEUE_ACTIVE        3
#define QUEUE_FAILED        4

#define QF_AUTO_START       0x00000008
#define QF_SERVICE_RESTART  0x00000010
#define QF_ENTRY_OPEN       0x00000020
#define QF_USER_HOLD        0x00000040
#define QF_OPERATOR_HOLD    0x00000080
#define QF_ENTRY_DONE       0x00000100
#define QF_SYSTEM_HOLD      0x00000200
#define QF_EXEC_NOW         0x00000400
#define QF_JOB_EXECD		0x00000800	//Job has been executed at least once
#define QF_INTERLEAFED		0x00010000

#ifdef _TIMEZONE
#define QF_DAYLIGHTSZONE	0x00001000
#define QF_DAYLIGHTTRUE		0x00002000
#endif //_TIMEZONE

#if defined (MUX_TAPE) || defined  (MULTISTRIPE_SQL) 
#define QF_MULTIPROCESS				0x00020000
#define QF_MULTIPLEX				0x00040000
#define QF_MULTIPROCESS_SLAVE		0x00080000
#endif //MUX_TAPE || MULTISTRIPE_SQL

#ifdef _B2D2T
	  //This flag would be used to indicate that this is a amster job in a B2D2T job.	  
#define QF_DISKSTAGING				0x00100000
#endif //_B2D2T

#ifdef NODE_RPT
#define QF_NODE_RPT_INITIALIZE		0x00200000
#endif NODE_RPT

//_LogMessage Types :goyra03			//DebugLevels
#define BEB_ERROR						0x00000100	//Debug Level 1
#define BEB_CRITICAL					0x00000200	//Debug Level 2
#define BEB_WARNING                     0x00000300	//Debug Level 3
#define BEB_NOTE					    0x00000400	//Debug Level 4
#define BEB_DEBUG					    0x00000500	//Debug Level 5
#define BEB_TRACE						0x00000600	//Debug Level 6

#ifdef MSRPC_BUFFER_OVERFLOW
#define		MAX_QUEUENAME_LENGTH	48
#define		MAX_QUEUEPATH_LENGTH	256
#define		MAX_JOBFILE_LENGTH		16
#define     MAX_QUEUEID				1
#endif

// Control flags for function QSIModifyJobEx()
#define QMJF_JOBCONTROLFLAGS			0x00000001 // ulJobControlFlags
#define QMJF_EXECTIME					0x00000002 // ulExecTime
#define QMJF_LASTEXECTIME				0x00000004 // ulLastExecTime
#define QMJF_LASTRESULT					0x00000008 // ulLastResult
#define QMJF_TASKID						0x00000010 // ulTaskID
#define QMJF_TASKHANDLE					0x00000020 // ulTaskHandle
#define QMJF_LOGID						0x00000040 // ulLogID
#define QMJF_DELTAHOUR					0x00000080 // ulDeltaHour
#define QMJF_INTERLEAFLOGIDS			0x00000100 // ulInterleafLogIDs
#define QMJF_LASTAPEXECTIME				0x00000200 // ulLastAPExecTime
#define QMJF_LASTRESOURCEALLOCATIONTIME	0x00000400 // ulLastResourceAllocationTime
#define QMJF_UNIQUEMUXID				0x00000800 // uniqueMuxID
#define QMJF_AUCUSERAREA				0x00001000 // aucUserArea

#define EVENTASCORERPCREADY				(_T("EventAscoreRPCReady"))

//used in QJOBITEMEXW::dwFlags
#define QJIF_MAKEUP_JOB			(1)

////////////////////////////Build Security

#define FILE_FULLCONTROL	(FILE_READ_DATA | FILE_WRITE_DATA | FILE_APPEND_DATA |\
							FILE_READ_EA | FILE_WRITE_EA | FILE_EXECUTE |\
							FILE_DELETE_CHILD | FILE_READ_ATTRIBUTES |\
							FILE_WRITE_ATTRIBUTES)

#define  FILE_CHANGE		(FILE_READ_DATA | FILE_WRITE_DATA | FILE_APPEND_DATA |\
							FILE_READ_EA | FILE_WRITE_EA | FILE_EXECUTE |\
							FILE_READ_ATTRIBUTES | FILE_WRITE_ATTRIBUTES )  

#define SHARE_READ			(FILE_GENERIC_READ | FILE_EXECUTE)

#define SHARE_FULLCONTROL	(STANDARD_RIGHTS_ALL | FILE_FULLCONTROL)

#define SHARE_CHANGE		(STANDARD_RIGHTS_READ | STANDARD_RIGHTS_WRITE |\
							DELETE | SYNCHRONIZE | FILE_CHANGE)


#define SHARE_NOACCESS		(SHARE_FULLCONTROL) //ACCESS_DENIED_ACE_TYPE

/////////////////////////////////////////////////////////////////////////////////////
#pragma pack(1)

typedef struct  ASJobUpdate {
	CHAR szARCserveusername[128];
	CHAR szOSUser[128];
	CHAR szMachinename[65];
	CHAR reserved[256];
}ASJOBUPDATE;

typedef ASJOBUPDATE *PASJOBUPDATE;

typedef struct tagJOBEXECPARM
{
  ULONG ulQueueID;
  ULONG ulJobID;
  ULONG bConApp;
  ULONG MboTicket;
  ULONG ulProcessorNum;
  CHAR  szQueHost[32];
  CHAR	reserved[12];
} JOBEXECPARM, *PJOBEXECPARM;

typedef struct tagJOBITEM
{
  ULONG ulJobID;
  ULONG ulLastModTime;
} JOBITEM;
typedef JOBITEM *PJOBITEM;

typedef struct tagQJOBITEM
{
  ULONG ulJobID;
  ULONG ulJobType;
#ifdef _TIMEZONE
  LONG ulEntryTime;
#else
  ULONG ulEntryTime;
#endif
  ULONG ulExecTime;
  ULONG ulLastModTime;
  ULONG ulJobControlFlags;
  ULONG ulTaskID;
  ULONG ulTaskHandle;
  ULONG ulLogID;
  ULONG ulLastResult;
  PVOID pvNext;
  ULONG ulDBProtectFlag;
//12*4 =48 bytes above.
  ULONG ulMasterJobNo;				//Master Job Job NO
#if defined ASCORE_SCALABILITY_ENHANCEMENT_JOBSTRU_1
	USHORT   usJobPosition;
	USHORT   usJobStatus;
	ULONG	 uniqueMuxID;
	USHORT	 nScriptType;
	USHORT	 nPadding;
	ULONG    nSourceItems;               // Number of source items
	ULONG    nDestItems;                 // Number of dest. items
	//48 + (6*4)= 72 bytes above.
	CHAR     pszOwner[16];                   // Job Owner	
	CHAR     pszReportFile[MAX_PATH];        // Report file (fully qualified)
	CHAR     pszComments[40];                // Comments or description
	CHAR     pszSource[128];                  // Archiving: Source Path
	CHAR     pszDest[128];                    // Archiving: Dest Path
	//384 bytes above so far
	CHAR     szMemberServer[16];
	CHAR	 pszSetName[8];
	CHAR	 pszMediaPoolName[16];
	//424 bytes used up so far
	CHAR	 szReserved[88];				// Total size is 512 bytes
#endif	

} QJOBITEM;
typedef QJOBITEM *PQJOBITEM;

typedef struct tagQJOBITEMW
{
	ULONG ulJobID;
	ULONG ulJobType;
#ifdef _TIMEZONE
	LONG ulEntryTime;
#else
	ULONG ulEntryTime;
#endif
	ULONG ulExecTime;
	ULONG ulLastModTime;
	ULONG ulJobControlFlags;
	ULONG ulTaskID;
	ULONG ulTaskHandle;
	ULONG ulLogID;
	ULONG ulLastResult;
	PVOID pvNext;
	ULONG ulDBProtectFlag;
	//12*4 =48 bytes above.
	ULONG ulMasterJobNo;				//Master Job Job NO
#if defined ASCORE_SCALABILITY_ENHANCEMENT_JOBSTRU_1
	USHORT   usJobPosition;
	USHORT   usJobStatus;
	ULONG	 uniqueMuxID;
	USHORT	 nScriptType;
	USHORT	 nPadding;
	ULONG    nSourceItems;               // Number of source items
	ULONG    nDestItems;                 // Number of dest. items
	//48 + (6*4)= 72 bytes above.
	WCHAR     pszOwner[16];                   // Job Owner	
	WCHAR     pszReportFile[MAX_PATH];        // Report file (fully qualified)
	WCHAR     pszComments[40];                // Comments or description
	WCHAR     pszSource[128];                  // Archiving: Source Path
	WCHAR     pszDest[128];                    // Archiving: Dest Path
	//384 bytes above so far
	WCHAR     szMemberServer[16];
	WCHAR	 pszSetName[8];
	WCHAR	 pszMediaPoolName[16];
	//424 bytes used up so far
	WCHAR	 szReserved[88];				// Total size is 512 bytes
#endif	

} QJOBITEMW;
typedef QJOBITEMW *PQJOBITEMW;

typedef struct tagQJOBITEMEX
{
  ULONG ulJobID;
  ULONG ulJobType;
  LONG  ulEntryTime;
  ULONG ulExecTime;
  ULONG ulLastModTime;
  ULONG ulJobControlFlags;
  ULONG ulTaskID;
  ULONG ulTaskHandle;
  ULONG ulLogID;
  ULONG ulLastResult;
  PVOID pvNext;
  ULONG ulDBProtectFlag;
  ULONG ulMasterJobNo;				//Master Job Job NO
	//52 bytes so far above. Above members HAVE to match QJOBITEM structure
  ULONG	 uniqueMuxID;
  USHORT usJobStatus;
  USHORT nScriptType;
  ULONG  ulRunNowExecutionTime;			
	//52 + (3*4)= 64 bytes above.

#ifdef ARC_ROLE_MANAGEMENT
  CHAR   szOwner[24];                // Job Owner	
#else
  CHAR   szOwner[16];                // Job Owner
#endif

  CHAR   szComments[80];             // Comments or description
  CHAR   szSource[64];               // Archiving: Source Path
  CHAR   szMemberServer[16];
#ifdef ARC_ROLE_MANAGEMENT
  CHAR   szReserved[8];
#else
  CHAR	 szReserved[16];			 // Total size is 256 bytes 
#endif
} QJOBITEMEX;
typedef QJOBITEMEX *PQJOBITEMEX;

#ifdef R12_5_JOB_OWNER
typedef struct tagQJOBITEMEXW_OLD
#else
typedef struct tagQJOBITEMEXW
#endif
{
	ULONG ulJobID;
	ULONG ulJobType;
	LONG  ulEntryTime;
	ULONG ulExecTime;
	ULONG ulLastModTime;
	ULONG ulJobControlFlags;
	ULONG ulTaskID;
	ULONG ulTaskHandle;
	ULONG ulLogID;
	ULONG ulLastResult;
	PVOID pvNext;
	ULONG ulDBProtectFlag;
	ULONG ulMasterJobNo;				//Master Job Job NO
	//52 bytes so far above. Above members HAVE to match QJOBITEM structure
	ULONG	 uniqueMuxID;
	USHORT usJobStatus;
	USHORT nScriptType;
	ULONG  ulRunNowExecutionTime;			
	//52 + (3*4)= 64 bytes above.
	WCHAR   szOwner[16];                // Job Owner	
	WCHAR   szComments[80];             // Comments or description
	WCHAR   szSource[64];               // Archiving: Source Path
	WCHAR   szMemberServer[16];
	WCHAR	 szReserved[16];			 // Total size is 256 bytes 
#ifdef R12_5_JOB_OWNER
} QJOBITEMEXW_OLD;
#else
} QJOBITEMEXW;
#endif
#ifdef  R12_5_JOB_OWNER
typedef QJOBITEMEXW_OLD *PQJOBITEMEXW_OLD;
#else
typedef QJOBITEMEXW *PQJOBITEMEXW;
#endif 

#ifdef R12_5_JOB_OWNER
typedef struct tagQJOBITEMEXW
{
	ULONG ulJobID;
	ULONG ulJobType;
	LONG  ulEntryTime;
	ULONG ulExecTime;
	ULONG ulLastModTime;
	ULONG ulJobControlFlags;
	ULONG ulTaskID;
	ULONG ulTaskHandle;
	ULONG ulLogID;
	ULONG ulLastResult;
	PVOID pvNext;
	ULONG ulDBProtectFlag;
	ULONG ulMasterJobNo;				//Master Job Job NO
	//52 bytes so far above. Above members HAVE to match QJOBITEM structure
	ULONG	 uniqueMuxID;
	USHORT usJobStatus;
	USHORT nScriptType;
	ULONG  ulRunNowExecutionTime;			
	//52 + (3*4)= 64 bytes above.	
	WCHAR   szComments[96];             // Comments or description
	WCHAR   szSource[64];               // Archiving: Source Path
	WCHAR   szMemberServer[16];
	WCHAR   szOwner[100];                // Job Owner	
	ULONG	ulFlags;                     // Job Flags
	WCHAR	szReserved[90];              // Total size is 800 bytes 
} QJOBITEMEXW;
typedef QJOBITEMEXW *PQJOBITEMEXW;
#endif

typedef struct tagQJOBINFO_OLD
{
  ULONG ulJobID;
  ULONG ulJobType;
  ULONG ulJobPosition;
  ULONG ulJobStatus;
  ULONG ulJobClass;
  ULONG ulJobControlFlags;
#ifdef _TIMEZONE
  LONG ulEntryTime;
#else
  ULONG ulEntryTime;
#endif
  ULONG ulExecTime;
  ULONG ulLastModTime;
  ULONG ulLastExecTime;
  ULONG ulLastResult;
  ULONG ulTaskID;
  ULONG ulTaskHandle;
  UCHAR szJobFile[16];
  ULONG ulLogID;
  ULONG ulDeltaHour;	// Save adjusting hour here for Daylight saving.
  ULONG	nInterleafThreads;
  ULONG ulInterleafTaskIDs[16];
  ULONG ulInterleafLogIDs[8];  
  UCHAR szExecutionHost[32]; //CJQ, member server name
  ULONG ulLastAPExecTime;
  ULONG ulLastResourceAllocationTime;
  ULONG uniqueMuxID;

#ifdef _B2D2T
  ULONG ulIncrSetRetensionTime;
#ifdef _RUNNOWTIME
  //To display execution time for run now jobs. As chandra for more info. goyra03
  ULONG ulRunNowExecutionTime;
  UCHAR aucReserved[20];
#else //_RUNNOWTIME
  UCHAR aucReserved[24];
#endif //_RUNNOWTIME
#else
  UCHAR aucReserved[28];
#endif
	 ULONG ulDBProtectFlag;
#ifdef BEB_FIX_ISSUE_15446886
	 ULONG ulMasterJobNo;				//Master Job Job NO
#else
     UCHAR aucReservedEx[4];
#endif
  UCHAR aucUserArea[256];
} QJOBINFO_OLD;
typedef QJOBINFO_OLD *PQJOBINFO_OLD;
typedef struct tagQJOBINFO
{
  ULONG ulJobID;
  ULONG ulJobType;
  ULONG ulJobPosition;
  ULONG ulJobStatus;
  ULONG ulJobClass;
  ULONG ulJobControlFlags;
#ifdef _TIMEZONE
  LONG ulEntryTime;
#else
  ULONG ulEntryTime;
#endif
  ULONG ulExecTime;
  ULONG ulLastModTime;
  ULONG ulLastExecTime;
  //10 *4 = 40 bytes 
  ULONG ulLastResult;
  ULONG ulTaskID;
  ULONG ulTaskHandle;
  UCHAR szJobFile[16];
  //40 + 12 +16= 68 bytes 
  ULONG ulLogID;
  ULONG ulDeltaHour;	// Save adjusting hour here for Daylight saving.
  ULONG	nInterleafThreads;
  //68 + 12= 80 bytes
  ULONG ulInterleafTaskIDs[16];
  ULONG ulInterleafLogIDs[8];  
  UCHAR szExecutionHost[32]; //CJQ, member server name
  ULONG ulLastAPExecTime;
  ULONG ulLastResourceAllocationTime;
  ULONG uniqueMuxID;
  //80 + 64+ 32 +32 +12= 220 bytes	
#ifdef _B2D2T
  ULONG ulIncrSetRetensionTime;
#ifdef _RUNNOWTIME
  //To display execution time for run now jobs. As chandra for more info. goyra03
  ULONG ulRunNowExecutionTime;
  USHORT usDBRecoveringFlag;
  UCHAR aucReserved[18];
#else //_RUNNOWTIME
  UCHAR aucReserved[24];
#endif //_RUNNOWTIME
#else
  UCHAR aucReserved[28];
#endif
  //220 +28  + 2 = 250 bytes so far.
  ULONG ulDBProtectFlag;
  ULONG ulMasterJobNo;	//Master Job Job NO 
  
  //248 + 8 = 258 bytes	
  UCHAR aucUserArea[256];
  //Total : 512 bytes so far
#if defined ASCORE_SCALABILITY_ENHANCEMENT_JOBSTRU_1
	USHORT nScriptType;
	USHORT nPadding;
	ULONG  nSourceItems;               // Number of source items
	ULONG  nDestItems;                 // Number of dest. items
	//512 +12=524 bytes
	CHAR   pszOwner[16];                   // Job Owner
	CHAR   pszReportFile[MAX_PATH];        // Report file (fully qualified)
	CHAR   pszComments[40];                // Comments or description
	CHAR   pszSource[128];                  // Archiving: Source Path
	CHAR   pszDest[128];                    // Archiving: Dest Path
	CHAR   pszSetName[8];
	CHAR   pszMediaPoolName[16];
	//524 + 332= 884 bytes so far
	CHAR   szReserved[180];			//Round the entire structure to 1024 bytes
#endif
} QJOBINFO;
typedef QJOBINFO *PQJOBINFO;
#ifdef UNICODE_JIS_SUPPORT1 //kumad02

typedef struct tagQJOBINFOW
{
	ULONG ulJobID;
	ULONG ulJobType;
	ULONG ulJobPosition;
	ULONG ulJobStatus;
	ULONG ulJobClass;
	ULONG ulJobControlFlags;
#ifdef _TIMEZONE
	LONG ulEntryTime;
#else
	ULONG ulEntryTime;
#endif
	ULONG ulExecTime;
	ULONG ulLastModTime;
	ULONG ulLastExecTime;
	ULONG ulLastResult;
	ULONG ulTaskID;
	ULONG ulTaskHandle;
	UCHAR szJobFile[16];
	ULONG ulLogID;
	ULONG ulDeltaHour;	// Save adjusting hour here for Daylight saving.
	ULONG	nInterleafThreads;
	ULONG ulInterleafTaskIDs[16];
	ULONG ulInterleafLogIDs[8];  
	UCHAR szExecutionHost[32]; //CJQ, member server name
	ULONG ulLastAPExecTime;
	ULONG ulLastResourceAllocationTime;
	ULONG uniqueMuxID;
#ifdef _B2D2T
	ULONG ulIncrSetRetensionTime;
#ifdef _RUNNOWTIME
	//To display execution time for run now jobs. As chandra for more info. goyra03
	ULONG ulRunNowExecutionTime;
	UCHAR aucReserved[20];
#else //_RUNNOWTIME
	UCHAR aucReserved[24];
#endif //_RUNNOWTIME
#else
	UCHAR aucReserved[28];
#endif
	ULONG ulDBProtectFlag;

	ULONG ulMasterJobNo;				//Master Job Job NO

	UCHAR aucUserArea[256];
#if defined ASCORE_SCALABILITY_ENHANCEMENT_JOBSTRU_1
	USHORT nScriptType;
	USHORT nPadding;
	ULONG  nSourceItems;               // Number of source items
	ULONG  nDestItems;                 // Number of dest. items
	WCHAR   pszOwner[16];                   // Job Owner
	WCHAR   pszReportFile[MAX_PATH];        // Report file (fully qualified)
	WCHAR   pszComments[40];                // Comments or description
	WCHAR   pszSource[128];                  // Archiving: Source Path
	WCHAR   pszDest[128];                    // Archiving: Dest Path
	WCHAR   pszSetName[8];
	WCHAR   pszMediaPoolName[16];
	WCHAR   szReserved[180];			//Round the entire structure to 1024 bytes
#endif
} QJOBINFOW;
typedef QJOBINFOW *PQJOBINFOW;

#endif //UNICOCDE_JIS_SUPPORT

typedef struct tagQUEUEINFO
{
  ULONG  ulQueueID;
  CHAR   szQueueName[48];
  ULONG  ulQueueType;
  ULONG  ulQueuePriority;
  ULONG  ulQueueStatus;
  ULONG  nQUsers;
  PVOID  pvQUserList;
  ULONG  nJobs;
  PVOID  pvJobList;
  CHAR   szBasePath[260];
  ULONG  nActiveJobs;
  ULONG  nReadyJobs;
  ULONG  nHoldJobs;
  ULONG  nDoneJobs;
  ULONG  ulRetainTime;
  ULONG  ulScanInterval;
  ULONG  ulLastModTime;
  ULONG  ulLastDelTime;
  UCHAR  aucReserved[32];
} QUEUEINFO;
typedef QUEUEINFO *PQUEUEINFO;
typedef struct tagQUEUEINFOW
{
	ULONG  ulQueueID;
	wchar_t   szQueueName[48];
	ULONG  ulQueueType;
	ULONG  ulQueuePriority;
	ULONG  ulQueueStatus;
	ULONG  nQUsers;
	PVOID  pvQUserList;
	ULONG  nJobs;
	PVOID  pvJobList;
	wchar_t   szBasePath[260];
	ULONG  nActiveJobs;
	ULONG  nReadyJobs;
	ULONG  nHoldJobs;
	ULONG  nDoneJobs;
	ULONG  ulRetainTime;
	ULONG  ulScanInterval;
	ULONG  ulLastModTime;
	ULONG  ulLastDelTime;
	UCHAR  aucReserved[32];
} QUEUEINFOW;
typedef QUEUEINFOW *PQUEUEINFOW;


#define QSI_DOMAIN_PRIMARY       0x00000001

typedef struct tagQSYSINFO
{
  ULONG ulQSysID;
  ULONG ulStatus;
  ULONG nQueues;
  PVOID pvQueueList;
  ULONG nQUsers;
  PVOID pvQUserListPool;
  ULONG nJobProcs;
  PVOID pvJobProcTable;
  ULONG nJobs;
  PVOID pvJobListPool;
  ULONG	dwDebugLevel;
  ULONG	dwDebugFileSize;
#ifdef _TIMEZONE
  ULONG ulLocalTimeZoneMode;
  ULONG	dwFlags;
  CHAR  szPrimaryServer[16];  
  UCHAR aucReserved[32];	
#else //_TIMEZONE
  UCHAR aucReserved[56];
#endif //_TIMEZONE
} QSYSINFO;
typedef QSYSINFO *PQSYSINFO;

typedef struct _QSIJOBPROCINFO
{
  ULONG  ulJobType;
  CHAR   szDllName[32];
  CHAR   szFunction[32];
  CHAR   szJobType[32];
} QSIJOBPROCINFO;
typedef QSIJOBPROCINFO	*PQSIJOBPROCINFO;

typedef struct _CRASHEDJOB
{
	ULONG ulJobNo;
	ULONG ulJobID;	
	BOOL  bIsNodeTableUpdated; 
}CRASHEDJOB;
typedef CRASHEDJOB *PCRASHEDJOB;
#pragma pack()

//duvdo01, Please be VERY CAREFUL to change this value, or would lead dead loop in job queue 
#define MAX_JOB_PROCS	50  


void   QSIGetQueuePath(ULONG ulQueueID, PSZ pszRetBuf, PSZ pszBasePath, 
                       BOOL bFullPath); 
void   QSIGetQueueUsersFile(ULONG ulQueueID, PSZ pszRetBuf, PSZ pszBasePath,
                            BOOL bFullPath);
void   QSIGetQueueJobsFile(ULONG ulQueueID, PSZ pszRetBuf, PSZ pszBasePath,
                           BOOL bFullPath);
QSIRET QSIGetJobFile(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, PSZ pszRetBuf, 
                     PSZ pszBasePath, BOOL bFullPath);

QSIRET QSICreateQueue(PSZ pszMachine, PSZ pszQueueName, ULONG ulQueueType,
                      PSZ pszPathName, PULONG pulQueueID);
QSIRET QSIGetQueue(PSZ pszMachine, ULONG ulQueueID, PQUEUEINFO pQueueInfo);

QSIRET QSIGetQueueW(wchar_t* pszMachine, ULONG ulQueueID, PQUEUEINFOW pQueueInfo);
QSIRET QSIModifyQueue(PSZ pszMachine, ULONG ulQueueID, PQUEUEINFO pQueueInfo);
// Oripin: UNICODE_JIS Support :redbh03
QSIRET QSIModifyQueueW(wchar_t* pszMachine, ULONG ulQueueID, PQUEUEINFOW pQueueInfo);

QSIRET QSIDeleteQueue(PSZ pszMachine, ULONG ulQueueID);

QSIRET QSICreateQJobItem(ULONG ulQueueID, ULONG ulJobID, ULONG ulJobType,
                         ULONG ulExecTime, ULONG ulLastModTime, 
                         ULONG ulJobControlFlags, ULONG ulTaskID, 
                         ULONG ulTaskHandle);

QSIRET QSIAddJob(PSZ pszMachine, ULONG ulQueueID, PQJOBINFO pJobInfo, PULONG pulJobID);

QSIRET QSIGetJob(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, PQJOBINFO pJobInfo);

QSIRET QSIModifyJob(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, PQJOBINFO pJobInfo);
QSIRET QSIModifyJobEx(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, PQJOBINFO pJobInfo, ULONG ulControlFlags);
QSIRET QSIModifyJobW(PWSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, PQJOBINFO pJobInfo); // dayra01 translation lib api

QSIRET QSIDeleteJob(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID);
// Oripin: UNICODE_JIS Support :redbh03
QSIRET QSIDeleteJobW(wchar_t* pszMachine, ULONG ulQueueID, ULONG ulJobID);
QSIRET QSIGetJobList(ULONG ulQueueID, PULONG pulJobIDList);

QSIRET QSIGetJobItemList(PSZ pszMachine, ULONG ulQueueID, PJOBITEM pJobItemList,
                         ULONG ulBufSize, PULONG pnJobItems);
QSIRET QSIGetJobItemListW(PWSZ pszMachine, ULONG ulQueueID, PJOBITEM pJobItemList,
						 ULONG ulBufSize, PULONG pnJobItems); // dayra01 translation lib api
QSIRET QSICreateJob(PSZ pszMachine, ULONG ulQueueID, PQJOBINFO pJobInfo, HFILE *phFile);

QSIRET QSIAbortJob(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, HFILE hFile);

QSIRET QSIMarkJobForService(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, HFILE hFile);

ULONG  QSIMapJobStatus(ULONG ulTaskID, ULONG ulJobControlFlags);
QSIRET QSIGetQJobItemList(PSZ pszMachine, ULONG ulQueueID, PQJOBITEM pQJobItemList, 
                          ULONG ulBufSize, PULONG pnJobItems, PQUEUEINFO pQueueInfoRet);
QSIRET QSIGetQJobItemListEx(PSZ pszMachine, ULONG ulQueueID, PQJOBITEMEX pQJobItemList, 
                          ULONG ulBufSize, PULONG pnJobItems, PQUEUEINFO pQueueInfoRet, PULONG pulIsJobDeletedInQueue);
QSIRET QSIGetQJobItemListW(wchar_t* pszMachine, ULONG ulQueueID, PQJOBITEMW pQJobItemList, 
						  ULONG ulBufSize, PULONG pnJobItems, PQUEUEINFOW pQueueInfoRet);
QSIRET QSIGetQJobItemListExW(PWSZ pszMachine, ULONG ulQueueID, PQJOBITEMEXW pQJobItemList, 
							ULONG ulBufSize, PULONG pnJobItems, PQUEUEINFOW pQueueInfoRet, PULONG pulIsJobDeletedInQueue);
QSIRET QSIGetJobItem(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, PQJOBITEM pInQJobItem);

QSIRET QSIEnumQJobItems(PSZ pszMachine, ULONG ulQueueID, PULONG phEnumContext,
                        PQJOBITEM pQJobItemRet);
// Oripin: UNICODE_JIS Support redbh03

QSIRET QSIEnumQJobItemsW(PWSZ pszMachine, ULONG ulQueueID, PULONG phEnumContext,
						PQJOBITEMW pQJobItemRet);

PQJOBITEM QSIJobAlloc(ULONG ulQueueID);
PQJOBITEM QSIFindQJobItemToModify(ULONG ulQueueID,ULONG ulJobID);

QSIRET QSIInitQSysInfo(PSZ pszBasePath, PQSYSINFO pQSysInfoRet, BOOL bRefresh);
void QSIDeInitQSysInfo(); 	
QSIRET QSICreateMux(void);
void QSIDestroyMux();
QSIRET QSIInitFromQueueDir(PSZ pszBasePath);
QSIRET QSIInitFromRegistry(void);
QSIRET QSIInitFromQueueSysFiles(ULONG ulQueueID, PSZ pszQueueName, 
                                PSZ pszQueuePath);
QSIRET QSIGetFileInfo(PSZ pszFileName, PULONG pulQueueID, PULONG pulJobID, 
                      PUINT puiFileType);
QSIRET QSIGetJobForService(PSZ pszMachine, ULONG ulQueueID, PQJOBITEM pQJobItemRet);
QSIRET QSIMaintainQueue(PSZ pszMachine, ULONG ulQueueID, ULONG ulDoneJobsRetainTime);
QSIRET QSIMaintainQueue_Ex(PSZ pszMachine, ULONG ulQueueID, ULONG ulDoneJobsRetainTime, BOOL  bisFirstStartedUp, BOOL bIsPrimary, USHORT* pnCrashedJobs, CRASHEDJOB* pcrashedJobsArray);
QSIRET QSIClearQJobItemList(ULONG ulQueueID);
QSIRET QSIInitJobs(PQUEUEINFO pQueueInfo);
 
QSIRET QSIInitJobProcTable(void);
QSIRET QSIDeinitJobProcTable(void);
QSIRET QSIAddJobProcInfo(PQSIJOBPROCINFO pJobProcInfo);
QSIRET QSIGetJobProcInfo(ULONG ulJobType, PQSIJOBPROCINFO pJobProcInfo);
QSIRET QSISetJobProcInfo(ULONG ulJobType, PQSIJOBPROCINFO pJobProcInfo);
QSIRET QSIDeleteJobProcInfo(ULONG ulJobType);

QSIRET QSIRegSetForQueue(PQUEUEINFO pQueueInfo);
QSIRET QSIRegDeleteForQueue(PQUEUEINFO pQueueInfo);
QSIRET QSIRetryCrashedJobs(PSZ pszServer, BOOL* pbEnableCheckPointing);

LONG   ASCOREInitRPCServer(void);
LONG   ASCOREDeinitRPCServer(void);

ULONG	 ulAdjust_LastTime(ULONG ulLastModTime, ULONG ulCurTime);
void	InitGenericMapping(void);

QSIRET QSIAuthModifyJobExecTime(
			PSZ		pszMachine,
			ULONG	ulQueueID,
			ULONG	ulJobID,
            ULONG	ulNewExecTime,
			PSZ		pszUserName,
			PSZ		pszPassword,
			PSZ		pszDomainName,
			ULONG	ulSecLevel
			);

 QSIRET	QSIAuthModifyJobStatus(
			PSZ		pszMachine, 
			ULONG	ulQueueID,
			ULONG	ulJobID,
			ULONG	ulNewJobStatus,
			PSZ		pszUserName,
			PSZ		pszPassword,
			PSZ		pszDomainName,
			ULONG	ulSecLevel
		  );

 QSIRET	QSIAuthModifyJobStatusW(
	 PWSZ		pszMachine, 
	 ULONG	ulQueueID,
	 ULONG	ulJobID,
	 ULONG	ulNewJobStatus,
	 PWSZ		pszUserName,
	 PWSZ		pszPassword,
	 PWSZ		pszDomainName,
	 ULONG	ulSecLevel
	 ); // dayra01 translation lib api
QSIRET QSIAuthModifyJob(
	PSZ			pszMachine,
	ULONG		ulQueueID,
	ULONG		ulJobID,
	PQJOBINFO	pJobInfo,
	PSZ			pszUserName,
	PSZ			pszPassword,
	PSZ			pszDomainName,
	ULONG		ulSecLevel);

	QSIRET QSIModifyJobExecTime	(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, ULONG ulNewExecTime);
	QSIRET QSIModifyJobStatus	(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, ULONG ulJobStatus);
	QSIRET QSIModifyJobStatusW	(PWSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, ULONG ulJobStatus);// Oripin: UNICODE_JIS Support kalsa03

	////////////////////////////	security stuff	////////////////// 
	BOOL	MatchAccessRequest(handle_t hMachID, ULONG dwMask);
	
	//////////////////////////////////////////////////////////////////

	//monitor rpc inteface and if lost reregister
	QSIRET QSIRpcIfMonitor(PULONG pdwSleepTime);

/*	//Cluster
	BOOL QSIIsClusterServiceRunning(BOOL *pbFirst);
*/
	//_LogMessage -goyra03
	void _LogMessage(ULONG ulDbgLevel, ULONG ulType, BOOL bQFLog, char *fmt,...);

	QSIRET	QSISetDebugLevel(ULONG ulDebugLevel);

#ifdef _TIMEZONE
	QSIRET _QSIModifyJobTimeForTimeZoneOrDSTChange (handle_t hMachID, ULONG ulQueueID );
#endif //_TIMEZONE

	// Add for central management. 2006/09/08
	QSIRET QSIGetJob_EX2(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, PQJOBINFO pJobInfo);
	QSIRET QSIGetJob_EX2W(PWSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, PQJOBINFO pJobInfo);// Oripin: UNICODE_JIS Support kalsa03
	QSIRET QSIDeleteJob_EX2(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID);
	//Add for cluster support	
	QSIRET QSICheckQueueForCluster(PSZ pszMachine, ULONG ulQueueID, PQJOBITEM* ppQjobItem, USHORT* pNumOfJobItems);
	
	QSIRET QSIRefreshQSysInfo();
	QSIRET QSIModifyJob_Ex(PSZ pszMachine, ULONG ulQueueID, ULONG ulJobID, PQJOBINFO pJobInfo, PASJOBUPDATE  pAsJobUpdate);
	QSIRET QSIGetTotalJobsInQueue(PSZ pszMachine, PULONG pulTotalJobs);
	QSIRET QSIGetTotalJobsInQueueW(PWSZ pszMachine, PULONG pulTotalJobs);


#ifdef __cplusplus
  }
#endif /* __cplusplus */

#endif



















