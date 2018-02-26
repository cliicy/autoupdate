/****************************************************************************
Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.

 Program Name:  ARCserve for Windows NT
      Version:  6.0  Revision A
 Version Date:  January 2, 1994
****************************************************************************/

#ifndef _ASJMON_H
#define _ASJMON_H

#include "bebenabled.h"
#include "qsi.h"

#ifdef __cplusplus
extern "C" {
#endif

/*
 *      Job Monitor
 */

#pragma pack(1)

//#ifdef JOBMONITOR_PREVIOUSJOB_EXECINFO
typedef struct _SESSSTATUSCOUNT 
{
	USHORT	usSuccess;
	USHORT	usFailed;
	USHORT	usCanceled;
	USHORT	usImcompleted;
	USHORT	usUnknown;

} SESSSTATUSCOUNT, *PSESSSTATUSCOUNT;

#define JOBEXECUTIONINFO_F_IGNORE			0x00000001  // ignore the contents of this structure
#define JOBEXECUTIONINFO_FLAG_JOBINFO		0x00000002	// update Job Execution info
#define JOBEXECUTIONINFO_FLAG_DEVICEINFO	0x00000004  // update device info

#ifndef DRIVE_SERIAL_NUM_LENGTH
#define DRIVE_SERIAL_NUM_LENGTH	48
#endif

typedef struct _JOBEXECUTIONINFO 
{
	ULONG				ulFlags;
	LONG				lStartTime;
	LONG				lEndTime;
	SHORT				jobStatus;
	LONG				lTotalKB;
	LONG				lTotalFiles;
	USHORT				usTotalSessions;
	SESSSTATUSCOUNT		sessStatusCnt;

	UCHAR				tapedriveSerialNum[DRIVE_SERIAL_NUM_LENGTH]; // DRIVE_SERIAL_NUM_LENGTH is defined in tsi_inc.h as 48
	USHORT				tapeDriveAdapter;
	USHORT				tapeDriveBus;
	USHORT				tapeDriveSCSIID;
	USHORT				tapeDriveLUN;
	CHAR				reserved[38];      
} JOBEXECUTIONINFO, *PJOBEXECUTIONINFO;		//total 128 bytes
//#endif// JOBMONITOR_PREVIOUSJOB_EXECINFO

typedef struct  _JOB_MONITOR {
  ULONG         flags;                  // See _JMF_xxx
  USHORT        monitorCount;           // Number of connections
  USHORT        jobPhase;               // See _JMP_xxx
  USHORT        jobStatus;              // See _JMS_xxx
  USHORT        jobType;                // See QJT_xxx
/*
 *      Job Info
 */
  ULONG         jobStartTime,           // Time job started
                jobEndTime;             // Time job ends
  ULONG         jobEstFiles,            // Total estimated files
                jobEstKbDisk;           // Total estimated kilobytes
  ULONG         jobFiles,               // Total files
                jobKbDisk,              // Total kilobytes to/from disk
                jobKbTape;              // Total kilobytes to/from tape
/*
 *      Node Info
 */
  USHORT        nodeCurrent,            // Current Node
                nodeTotal;              // Total Nodes
/*
 *      Device Info for Current Node
 */
  USHORT        deviceCurrent,          // Current Device of Node
                deviceTotal;            // Total Devices for Node
/*
 *      Source and Target (Destination) for Current Device
 */
  UCHAR         source[96],             // Source Device
                target[96];             // Target Device
/*
 *      Data Transfer Info for Current Device
 */
  ULONG         xferStartTime,          // Time data transfer starts
                xferElapsedTime;        // Elapsed time for data transfer
  ULONG         xferEstFiles,           // Estimated files
                xferEstKbDisk;          // Estimated kilobytes to/from disk
  ULONG         xferFiles,              // Files transfered
                xferKbDisk,             // Kilobytes transfered to/from disk
                xferKbTape;             // Kilobytes transfered to/from tape
/*
 *      File Info for Current Device
 *      NOTE: This information is only updated while monitorCount is
 *            greater than zero.  
 */
  UCHAR         fileName[256];          // Path of file
  //
  // 31 May 1996.
  // The ULONG fileType is split into two USHORTs to accomodate the
  // session Type.
  //
  USHORT        fileType;               // Name space or disk type
  USHORT        sessionType;            // Session Type.
  ULONG         fileSize;               // Size of file (in bytes) -> low-order
  ULONG         fileXfer;               // Amount transfered (in bytes)	-> low-order
  UCHAR         groupName[12];          // Tape Group Name
  UCHAR         serverName[16];		// Server Name
  ULONG         taskID;                 // Task or Job ID.
  ULONG         fileSizeHigh;           // Size of file (in bytes) -> high-order
  ULONG         fileXferHigh;           // Amount transfered (in bytes) -> high-order
  ULONG			xferThrouphput;			// Multiplexing/multistreaming master job run time throughput.
  UCHAR         ucReserved[4];
#if     defined(_WIN95)
  UCHAR         folder[260];            // Path of folder
  UCHAR         drives[8];              // See _JMD_xxx
#endif
  //////////////////////////////////////////////////////
  // Unix used variables
  ULONG			percentage;
#ifdef JOBMONITOR_PREVIOUSJOB_EXECINFO
  ULONG			shmJobExecInfoOffset;	// offset of JobExecInfo in share memory
  UCHAR			ucReservedField[64];
#endif //JOBMONITOR_PREVIOUSJOB_EXECINFO
} JOB_MONITOR, *PJOB_MONITOR, ASJOBMON, *PASJOBMON;

typedef struct  _JOB_MONITORW {
	ULONG         flags;                  // See _JMF_xxx
	USHORT        monitorCount;           // Number of connections
	USHORT        jobPhase;               // See _JMP_xxx
	USHORT        jobStatus;              // See _JMS_xxx
	USHORT        jobType;                // See QJT_xxx
	/*
	*      Job Info
	*/
	ULONG         jobStartTime,           // Time job started
		jobEndTime;             // Time job ends
	ULONG         jobEstFiles,            // Total estimated files
		jobEstKbDisk;           // Total estimated kilobytes
	ULONG         jobFiles,               // Total files
		jobKbDisk,              // Total kilobytes to/from disk
		jobKbTape;              // Total kilobytes to/from tape
	/*
	*      Node Info
	*/
	USHORT        nodeCurrent,            // Current Node
		nodeTotal;              // Total Nodes
	/*
	*      Device Info for Current Node
	*/
	USHORT        deviceCurrent,          // Current Device of Node
		deviceTotal;            // Total Devices for Node
	/*
	*      Source and Target (Destination) for Current Device
	*/
	WCHAR         source[96],             // Source Device
		target[96];             // Target Device
	/*
	*      Data Transfer Info for Current Device
	*/
	ULONG         xferStartTime,          // Time data transfer starts
		xferElapsedTime;        // Elapsed time for data transfer
	ULONG         xferEstFiles,           // Estimated files
		xferEstKbDisk;          // Estimated kilobytes to/from disk
	ULONG         xferFiles,              // Files transfered
		xferKbDisk,             // Kilobytes transfered to/from disk
		xferKbTape;             // Kilobytes transfered to/from tape
	/*
	*      File Info for Current Device
	*      NOTE: This information is only updated while monitorCount is
	*            greater than zero.  
	*/
	WCHAR         fileName[256];          // Path of file
	//
	// 31 May 1996.
	// The ULONG fileType is split into two USHORTs to accomodate the
	// session Type.
	//
	USHORT        fileType;               // Name space or disk type
	USHORT        sessionType;            // Session Type.
	ULONG         fileSize;               // Size of file (in bytes) -> low-order
	ULONG         fileXfer;               // Amount transfered (in bytes)	-> low-order
	WCHAR         groupName[12];          // Tape Group Name
	WCHAR         serverName[16];		// Server Name
	ULONG         taskID;                 // Task or Job ID.
	ULONG         fileSizeHigh;           // Size of file (in bytes) -> high-order
	ULONG         fileXferHigh;           // Amount transfered (in bytes) -> high-order
	ULONG			xferThrouphput;			// Multiplexing/multistreaming master job run time throughput.
	WCHAR         ucReserved[4];
#if     defined(_WIN95)
	WCHAR         folder[260];            // Path of folder
	WCHAR         drives[8];              // See _JMD_xxx
#endif
	//////////////////////////////////////////////////////
	// Unix used variables
	ULONG			percentage;
#ifdef JOBMONITOR_PREVIOUSJOB_EXECINFO
	ULONG			shmJobExecInfoOffset;	// offset of JobExecInfo in share memory
	WCHAR			ucReservedField[64];
#endif //JOBMONITOR_PREVIOUSJOB_EXECINFO
} JOB_MONITORW, *PJOB_MONITORW, ASJOBMONW, *PASJOBMONW;
/*
 *      Job Monitor Phase
 */

#define _JMP_ESTIMATE                   1
#define _JMP_COPY                       2
#define _JMP_BACKUP                     3
#define _JMP_RESTORE                    4
#define _JMP_COMPARE                    5
#define _JMP_VERIFY                     6
#define _JMP_TAPE_CONNECT               7
#define _JMP_TAPE_REWIND                8
#define _JMP_TAPE_FORMAT                9
#define _JMP_TAPE_LOCATE_SESSION       10
#define _JMP_TAPE_CALC_SESSION         11
#define _JMP_TAPE_DISCONNECT           12
#define _JMP_MERGE                     13 
#define _JMP_SKIPPING                  14 
#define _JMP_COUNT                     15 
#define _JMP_PURGE                     16 
#define _JMP_SCAN_TAPE                 17 
#define _JMP_UPDATE_DATABASE           18 
#define _JMP_SLEEPING                  19 
#define _JMP_CANCELLING                20 
#define _JMP_TAPE_REPAIR               21
#define _JMP_VIRUS_SCAN		       22
#define _JMP_START_EXCH_DIR_SERVICE    23
#define _JMP_START_EXCH_IS_SERVICE     24
#define _JMP_STOP_EXCH_DIR_SERVICE     25
#define _JMP_STOP_EXCH_IS_SERVICE      26
#define _JMP_REPLICATE                 27
#define	_JMP_SCAN_VOLUME	       28
#define	_JMP_MIGRATE		       29		
#define _JMP_DEMIGRATE		       30	
#define _JMP_REMIGRATE		       31
#define _JMP_RECOVER_SQL_DATABASE      32
#define _JMP_VERIFY_WITH_CRC           33
#define _JMP_MIGRATE_TO_MBO			   34
#define _JMP_MBO_COMPRESSION           35
#define _JMP_MBO_COLLECT               36

#define _JMP_MBO_BACKUP_TO_LCB         37
#define _JMP_MBO_WAIT_LCB              38
#define _JMP_MBO_WAIT_VT               39
#define _JMP_MBO_WAIT_JOBTICKET        40   // -RM As per James Chueh request

#define _JMP_E12_RECOVER_DB         50         // for E12 agent : [restore]recover DB
#define _JMP_E12_RESTORE_MAILBOXS   51         // for E12 agent : [restore]Restore mailboxs
#define _JMP_E12_CREATING_SNAPSHOT  52         // for E12 agent : [backup]Creating SNAPSHOT
#define _JMP_E12_CHECKING_INTEGRITY 53         // for E12 agent : [backup]Checking Integrity
#define _JMP_E12_PURGING_LOGS       54         // for E12 agent : [backup]Purging Logs
#define _JMP_SPS_EXPORT_FILES		55
#define _JMP_SPS_IMPORT_FILES		56
#define _JMP_VM_RECOVERY			57			// VM recovery phase

/*
 *      Job Monitor Status
 */

#define _JMS_ACTIVE                     0
#define _JMS_FINISHED                   1
#define _JMS_CANCELLED                  2
#define _JMS_FAILED                     3
#define _JMS_INCOMPLETE                 4
#define _JMS_TARGET_ACTIVE              5	//for ASNW.
#define _JMS_TARGET_WAITING             6	//for ASNW
#define _JMS_TARGET_WAITING_COMPARE     7	//for ASNW
#define _JMS_TARGET_WAITING_VERIFY	    8	//for ASNW
#define _JMS_CRASHED                    101
#define _JMS_RUN_FAILED                 102

/*
 *      Job Monitor Flags
 */
#define _JMF_ABORT                      0x00000001
#define _JMF_DELETE                     0x00000002
#define _JMF_RMAN_DUMMY					0x00000004
#define _JMF_ESTIMATED                  0x00000100
#define _JMF_FULLPATH                   0x00010000
#define _JMF_CLOCK_RUNNING              0x10000000
#define _JMF_NETWARE_SESSION			0x20000000
#define _JMF_DONE                       0x80000000

#if     defined(_WIN95)
/*
 *      Job Monitor Drives
 */

#define _JMD_PROGRAM                    0
#define _JMD_SOURCE                     1
#define _JMD_TARGET                     2
#endif

/*
 *      Job Monitor Functions
 */
typedef struct  _ASJMON {
  BOOL          isRemote;
  HANDLE        handle;
  PJOB_MONITOR  jobMonitor;
  UCHAR         reserved[20];
} ASJMON, *PASJMON;

typedef struct  _ASJMONW {
	BOOL          isRemote;
	HANDLE        handle;
	PJOB_MONITORW  jobMonitor;
	UCHAR         reserved[20];
} ASJMONW, *PASJMONW;
typedef struct _ASJMON_EX2

{
	ASJOBMON			jobMon; 
	JOBEXECUTIONINFO	jobExecutionInfo;
	BOOL				bMasterJob; // is master job or child job
	ULONG				nJobNo; // Job Number
}  ASJMON_EX2, *PASJMON_EX2;
typedef struct _ASJMON_EX2W

{
	ASJOBMONW			jobMon; 
	JOBEXECUTIONINFO	jobExecutionInfo;
	BOOL				bMasterJob; // is master job or child job
	ULONG				nJobNo; // Job Number
}  ASJMON_EX2W, *PASJMON_EX2W;

#pragma pack()

#define JMWM_CHECKJOBID (WM_USER+1066)

#ifndef MIDL_INVOKED
BOOL    WINAPI ASJobCheckJobID(HWND hAppWnd, ULONG queueID, ULONG jobID);
UINT    WINAPI ASJobMonClose(PASJMON pASJMon);
VOID    WINAPI ASJobMonGetFile(PTCHAR fileName, ULONG threadID);
BOOL    WINAPI ASJobMonIsFlagSet(PASJMON pASJMon, ULONG flag);
PASJMON WINAPI ASJobMonOpen(PTCHAR serverName, ULONG threadID);
UINT    WINAPI ASJobMonSetFlag(PASJMON pASJMon, BOOL on, ULONG flag);
UINT    WINAPI ASJobMonUpdate(PASJMON pASJMon);
#endif


ASRET _ASGetJobMonInfo(handle_t hMachID, ULONG ulJobMonID, ASJMON *pASJMon,
                       PASJOBMON pJobMonRet);
ASRET _ASGetJobMonInfo2(handle_t hMachID, ULONG ulJobMonID, ASJMON *pASJMon,
                       PASJOBMON pJobMonRet, PJOBEXECUTIONINFO pJobExecInfoRet);
ASRET _ASGetJobMonInfoEx(handle_t hMachID, ULONG ulJobMonID,USHORT ulPrcssID, 
                       ASJMON *pASJMon, PASJOBMON pJobMonRet);
ASRET ASGetJobMonInfo(PSZ pszMachine, ULONG ulJobMonID, PASJMON pASJMon,
                      PASJOBMON pJobMonRet);
ASRET ASGetJobMonInfoW(PWSZ pszMachine, ULONG ulJobMonID, PASJMON pASJMon,
					  PASJOBMONW pJobMonRet); // dayra01 translation lib api
ASRET ASGetJobMonInfo2(PSZ pszMachine, ULONG ulJobMonID, PASJMON pASJMon,
                      PASJOBMON pJobMonRet, PJOBEXECUTIONINFO pJobExecInfoRet);
ASRET ASGetJobMonInfoEx(PSZ			pszMachine, 
						ULONG		ulJobMonID, 
						USHORT		ulPrcssID,	
						PASJMON		pASJMon, 
						PASJOBMON	pJobMonRet);
ASRET ASGetJobMonInfoExW(PWSZ			pszMachine, 
						ULONG		ulJobMonID, 
						USHORT		ulPrcssID,	
						PASJMONW		pASJMon, 
						PASJOBMONW	pJobMonRet); // dayra01 translation lib api
ASRET _ASSetJobMonFlags(handle_t hMachID, ULONG ulJobMonID, 
                        BOOL bOn, ULONG flags);
ASRET ASSetJobMonFlags(PSZ pszMachine, ULONG ulJobMonID, 
                       BOOL bOn, ULONG flags);
ASRET ASSetJobMonFlagsW(PWSZ pszMachine, ULONG ulJobMonID, 
					   BOOL bOn, ULONG flags); // dayra01 translation lib api

ASRET _ASOpenJobMon(handle_t hMachID, ULONG ulJobMonID, 
                    ULONG flags, PASJMON pASJMon);
ASRET ASOpenJobMon(PSZ pszMachine, ULONG ulJobMonID, 
                   ULONG flags, PASJMON pASJMon);
ASRET _ASCloseJobMon(handle_t hMachID, PASJMON pASJMon);
ASRET ASCloseJobMon(PSZ pszMachine, PASJMON pASJMon);

ASRET ASAuthSetJobMonFlags(
	PSZ		pszMachine,
	ULONG	ulJobMonID, 
	BOOL	bOn, 
	ULONG	flags,
	PSZ		pszUserName,
	PSZ		pszPassword,
	PSZ		pszDomainName,
	ULONG	ulSecLevel
);

ASRET ASAuthSetJobMonFlags_Ex(PSZ pszMachine, 
							  ULONG ulJobMonID, 
							  BOOL	bOn, 
							  ULONG flags, 
							  PSZ pszUserName, 
							  PSZ pszPassword,
							  PSZ pszDomainName, 
							  ULONG ulSecLevel, 
							  PASJOBUPDATE  pAsJobUpdate);

ASRET ASAuthGetJobMonInfo(
	PSZ			pszMachine, 
	ULONG		ulJobMonID, 
    PASJMON		pASJMon,
	PASJOBMON	pJobMonRet,
	PSZ			pszUserName,
	PSZ			pszPassword,
	PSZ			pszDomainName,
	ULONG		ulSecLevel
);

ASRET ASAuthGetJobMonInfoEx(
	PSZ			pszMachine, 
	ULONG		ulJobMonID, 
	USHORT		ulPrcssID,
    PASJMON		pASJMon,
	PASJOBMON	pJobMonRet,
	PSZ			pszUserName,
	PSZ			pszPassword,
	PSZ			pszDomainName,
	ULONG		ulSecLevel);

ASRET ASGetJobMonInfo_EX2( 
	PSZ pszMachine,
    ULONG ulJobMonID,
    PASJMON_EX2 *ppASJMon,
    USHORT *pNumOfItems);

ASRET ASGetJobMonInfo_EX2W( 
						   PWSZ pszMachine,
						   ULONG ulJobMonID,
						   PASJMON_EX2W *ppASJMon,
						   USHORT *pNumOfItems); // dayra01
#ifdef __cplusplus
}
#endif

#endif
