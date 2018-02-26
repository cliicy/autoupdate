#pragma once
#include <atlstr.h>
#include "AFJob.h"
#include "afdefine.h"
#include "DbgLog.h"

/* BEGIN: Added by huvfe01, 2013/3/14   PN:ARCserve Oolong Job Monitor and Cancel Job */
enum {
    AF_OK = 0,
    AF_Error_Memory,
    AF_Error_Size,
    AF_Error_Init,
    AF_Error_Param
};
/* END:   Added by huvfe01, 2013/3/14   PN:ARCserve Oolong Job Monitor and Cancel Job */

typedef DWORD (WINAPI *pfnUserCallProc)( LPVOID lpParameter);


//Cancel job
//DWORD WINAPI AFCancelJob(DWORD dwJobId, PWCHAR pwszNodeName = NULL); //<sonmi01>2013-3-22 #rps job monitor and eliminate the redefinitions 


//JobMonitor Structure

//JOB_MONITOR::ulJobPhase
//for backup, the value corresponding the define "native\h\datatransmitter.h"
#define	BACKUP_PHASE_START_BACKUP				0x01
#define	BACKUP_PHASE_TAKING_SNAPSHOT			0x02
#define	BACKUP_PHASE_CREATING_VIRTUAL_DISKS		0x03
#define	BACKUP_PHASE_REPLICATIING_VOLUMES		0x04
#define	BACKUP_PHASE_DELETING_SNAPSHOT			0x05
#define PHASE_CANCELING							0x06


//<sonmi01>2009-9-13 job monitor
#define BACKUP_PHASE_CREATE_METADATA			0x08
#define BACKUP_PHASE_COLLECT_DR_INFO			0x09
#define BACKUP_PHASE_PURGE_SESSION				0x0A

#define BACKUP_PHASE_JOB_END					0x20

//</sonmi01>

#define BACKUP_PHASE_PRE_JOB					0x0B
#define BACKUP_PHASE_POST_SNAPSHOT				0x0C
#define BACKUP_PHASE_POST_JOB					0x0D

#define BACKUP_PHASE_PROC_EXIT					0x0E
// <wansh11> - for continue merge failed sessioins 
#define BACKUP_PHASE_CONTINUR_FAILED_MERGE      0x0F  

#define BACKUP_PHASE_CONNECT_TO_STUB		    0x10 
#define BACKUP_PHASE_UPGRADE_CBT				0x11
#define BACKUP_PHASE_INITIALIZE_STUB			0x12
#define BACKUP_PHASE_COLLECT_DATA				0x13
#define BACKUP_PHASE_CHECK_RECOVERY_POINT		0x14
#define BACKUP_PHASE_CREATE_HW_SNAPSHOT			0x15
#define BACKUP_PHASE_DELETE_HW_SNAPSHOT			0x16


// <korpa02>9th Jan, 2011 - for adding a phase for catalog resync - common for archive job and resync from alt location
#define ARCHIVE_PHASE_CATALOG_UPDATE			0x21

//restore job phase
#define RESTORE_PHASE_START_RESTORE				0x07
#define RESTORE_PHASE_DUMP_METADATA				0x40
#define RESTORE_PHASE_RESTORE_DATA				0x41
#define RESTORE_PHASE_JOB_END					BACKUP_PHASE_JOB_END
#define RESTORE_PHASE_RESTORE_EXCHGRT_DATA		0x42 // D2D for Exchange GRT. zhazh06 R16.
//bccma01 add phase for mount and unmount during restore of catalogless sessions.
#define RESTORE_PHASE_MOUNT_VOLUME				0x43 
#define RESTORE_PHASE_UNMOUNT_VOLUME			0x44
//for VM direct restore job phase
#define RESTORE_PHASE_VM_DIRECT_RESTORE_DETECT_VM_STATUS 0x45
#define RESTORE_PHASE_VM_DIRECT_RESTORE_CONNECT_VM_HOST	 0x46
#define RESTORE_PHASE_VM_DIRECT_RESTORE_CONNECT_VM		 0X47
#define RESTORE_PHASE_VM_DIRECT_RESTORE_CREATE_MINID2D	 0X48
#define RESTORE_PHASE_VM_DIRECT_RESTORE_DUMY1			 0X49
#define RESTORE_PHASE_VM_DIRECT_RESTORE_DUMY2			 0X50


//copy job phase
#define COPY_PHASE_START_COPY					0x51
#define COPY_PHASE_COPY_DATA					0x52
#define COPY_PHASE_ESTIMATE_DATA				0x53
#define COPY_PHASE_LOCK_SESSION					0x54
#define COPY_PHASE_LOCK_SESSION_SUCCESSFUL		0x55
#define COPY_PHASE_LOCK_SESSION_FAILED			0x56
#define COPY_PHASE_JOB_END						BACKUP_PHASE_JOB_END

//vapp restore phase
#define RESTORE_PHASE_CREATE_VAPP               0x60
#define RESTORE_PHASE_VAPP_RESTORE_VM           0x61
#define RESTORE_PHASE_VAPP_IMPORT_VM            0x62
#define RESTORE_PHASE_VAPP_CLEAN_VM             0x6F


///<ZZ[zhoyu03: 2009/12/14]: Application restore process in VSSWrapperDll.dll
#define VSSWRAP_PHASE_GATHER_WRITERS_INFO             0xA1   
#define VSSWRAP_PHASE_INIT_VSS                        0xA2
#define VSSWRAP_PHASE_SELECT_COMPONENTS_TO_RESTORE    0xA3
#define VSSWRAP_PHASE_DISMOUNT_EXCHANGE_DATABASE      0xA4
#define VSSWRAP_PHASE_GATHER_DB_INFO_FROM_AD          0xA5
#define VSSWRAP_PHASE_STOP_SQL_SERVICE_RESTORE_MASTER 0xA6
#define VSSWRAP_PHASE_START_SQL_SERVICE               0xA7
#define VSSWRAP_PHASE_PRERESTORE                      0xA8
#define VSSWRAP_PHASE_RESTORE_FILE                    0xA9
#define VSSWRAP_PHASE_POSTRESTORE                     0xAA
#define VSSWRAP_PHASE_MOUNT_EXCHANGE_DATABASE         0xAB

//ZZ: Catalog process phase. Value from 0xB0, Not all of these phase will be shown in GUI
//    some have been considered as one item.
//ZZ: Validate if catalog script is valid.
#define CATPROC_PHASE_VALIDATE_CATALOG_SCRIPT         0xB0
//ZZ: Parse catalog script
#define CATPROC_PHASE_PARSE_CATALOG_SCRIPT            0xB1  
//ZZ: Lock session for intergration with client agent.
#define CATPROC_PHASE_LOCK_SESS_INTERGRATION          0xB2  
//ZZ: Prepare for catalog generation.
#define CATPROC_PHASE_PREPARE_FOR_CATALOG             0xB3  
//ZZ: Delete last failed session.
#define CATPROC_PHASE_DELETE_FAILED_SESSION           0xB4  
//ZZ: Continue merging failed session when last merge.
#define CATPROC_PHASE_CONTINUR_FAILED_MERGE           0xB5  
//ZZ: Merge session based on recovery point count configured.
#define CATPROC_PHASE_MERGE_SESS_BY_SETTING           0xB6  
//ZZ: Lock session for catalog in read mode.
#define CATPROC_PHASE_LOCK_SESS_FOR_CATALOG_READ      0xB7
//ZZ: Begin to generate catalog file.
#define CATPROC_PHASE_BEGIN_TO_GENERATE_CATALOG       0xB8
//ZZ: Generate catalog file for specified volume.
#define CATPROC_PHASE_GENERATE_CATALOG_FOR_VOLUME     0xB9
//ZZ: Creating index file for catalog file.
#define CATPROC_PHASE_GENERATE_CAT_INDEX_FOR_VOLUME   0xBA
//ZZ: Update catalog information for session.
#define CATPROC_PHASE_UPDATE_SESSION_INFORMATIIN      0xBB
//ZZ: Lock session for data update in write mode.
#define CATPROC_PHASE_LOCK_SESS_FOR_CATALOG_WRITE     0xBC
//ZZ: Update cluster map for session header.
#define CATPROC_PHASE_UPDATE_SESS_BLOCK_2             0xBD
//ZZ: Start next schedule catalog job. This maybe not occur in normal mode.
#define CATPROC_PHASE_START_NEXT_CATLOG_JOB           0xBE
//ZZ: Catalog generation finishes.
#define CATPROC_PHASE_CATALOG_GENERATE_FINISH         0xBF

#define CATPROC_PHASE_CATALOG_PROC_EXIT				  0xC0

//////////////////////////////////////////////////////////////////////////
//Generating exchange GRT phase, Not all of these phase will be shown in GUI
#define EXGRT_PHASE_CATALOB_BASE			0x1000
#define EXGRT_PHASE_MOUNTING_DRIVER			EXGRT_PHASE_CATALOB_BASE+1
#define EXGRT_PHASE_DISMOUNTING_DRIVER		EXGRT_PHASE_CATALOB_BASE+2
#define EXGRT_PHASE_ESTIMATE				EXGRT_PHASE_CATALOB_BASE+3
#define EXGRT_PHASE_GENERATE_CATALOG_BEGIN	EXGRT_PHASE_CATALOB_BASE+4
#define EXGRT_PHASE_GENERATE_CATALOG		EXGRT_PHASE_CATALOB_BASE+5
#define EXGRT_PHASE_GENERATE_CATALOG_END	EXGRT_PHASE_CATALOB_BASE+6
#define EXGRT_PHASE_GENERATE_INDEX_FILE		EXGRT_PHASE_CATALOB_BASE+7
#define EXGRT_PHASE_DEGRAGMENT				EXGRT_PHASE_CATALOB_BASE+8


#define MERGESESSION_PHASE_BASE 0x2000
#define MERGESESSION_PHASE_LOCKSESSION    (MERGESESSION_PHASE_BASE + 1)
#define MERGESESSION_PHASE_MERGINGSESSION (MERGESESSION_PHASE_BASE + 2)


//////////////////////////////////////////////////////////////////////////
//InterMerge job phase
#define IMJ_PHASE_BASE						0x3000
#define IMJ_PHASE_START						(IMJ_PHASE_BASE + 0)
#define IMJ_PHASE_CONTINUE_FAILED_MERGE		CATPROC_PHASE_CONTINUR_FAILED_MERGE
#define IMJ_PHASE_PREPARE_SESSION			(IMJ_PHASE_BASE + 1)
#define IMJ_PHASE_MERGE_DISK				(IMJ_PHASE_BASE + 2)
#define IMJ_PHASE_MOVE_DISK					(IMJ_PHASE_BASE + 3)
#define IMJ_PHASE_DELETE_SESSION			(IMJ_PHASE_BASE + 4)
#define IMJ_PHASE_END						BACKUP_PHASE_JOB_END


//////////////////////////////////////////////////////////////////////////
//RPSReplication job phase
#define RPSREP_PHASE_BASE                   0x3800
#define RPSREP_PHASE_START                  (RPSREP_PHASE_BASE + 0)
#define RPSREP_PHASE_PREPARE                (RPSREP_PHASE_BASE + 1)
#define RPSREP_PHASE_REPLICATE              (RPSREP_PHASE_BASE + 2)
#define RPSREP_PHASE_CANCELING              PHASE_CANCELING
#define RPSREP_PHASE_END                    BACKUP_PHASE_JOB_END
//////////////////////////////////////////////////////////////////////////
// Purge node data job phase.
#define RPS_PURGENODEDATA_PHASE_BASE		0X4000
#define RPS_PURGENODEDATA_PHASE_START		(RPS_PURGENODEDATA_PHASE_BASE + 0)
#define RPS_PURGENODEDATA_PHASE_END			(RPS_PURGENODEDATA_PHASE_BASE + 1)
#define RPS_PURGENODEDATA_PHASE_INIT		(RPS_PURGENODEDATA_PHASE_BASE + 2)
#define RPS_PURGENODEDATA_PHASE_DEL_DATA	(RPS_PURGENODEDATA_PHASE_BASE + 3)
//////////////////////////////////////////////////////////////////////////

#define TOKEN_VALUE_TO_STR(token) (L#token)

//////////////////////////////////////////////////////////////////////////
//JOB_MONITOR::ulJobStatus
#define JS_ACTIVE       0               // Job Status
#define JS_FINISHED     1
#define JS_CANCELLED    2
#define JS_FAILED       3
#define JS_INCOMPLETE   4
#define JS_IDLE         5
#define JS_WAITING      6
#define JS_CRASHED		7
#define JS_NEEDREBOOT   8
#define JS_FAILED_NO_LICENSE   9		//<sonmi01>2010-1-21 mark no license job as FAILED and update JS_FAILED_NO_LICENSE for Java
#define JS_PROC_EXIT    10				//ZZ: Process of job exits.
#define JS_SKIPPED		11				// Job is skipped due to merge operation in progress
#define JS_STOPPED		12				// stopped by user or other job
#define JS_FAILLOCK		13				// skipped due to fail lock
#define JS_MISSED       10000			// the missed job

//JOB_MONITOR::ulFlags
#define JIF_INIT			0x0001
#define JIF_PHASE			0x0002
#define JIF_JOB_EST			0x0004
#define JIF_VOLUME_EST		0x0008
#define JIF_PROGRESS		0x0010
#define JIF_JOB_STATUS		0x0020
#define JIF_ESTIMATED		0x0040			//the flag is set in share memory by backend if estimation is on.
#define JIF_JOB_BACKUP_METHOD		0x0080			//<sonmi01>2010-2-25 ###???
#define JIF_PERF_DATA		0x0100
#define JIF_ENC_STATUS      0x0200   //ZZ: Update encryption information.
#define JIF_CUR_VOLUME      0x0400   //ZZ: Update current volume being backed up.
#define JIF_COMPRESS_RATE   0x0800   //ZZ: Update compression rate.
#define JIF_CUR_THROTTLING  0x1000   //ZZ: Current throughout throttling, specified in backup setting or job monitor.
#define JIF_CUR_VMDISK      0x2000   //<sonmi01>2010-11-11 ###???
#define JIF_VMDISK_TRANSPORT_MODE	0x4000 //<sonmi01>2010-11-17 vm disk transport mode
 
//ZZ: Flag for catalog generation.
#define JIF_CAT_PARSE_CATALOG_SCRIPT                  0x8000
//ZZ: Delete last failed session.
#define JIF_CAT_DELETE_FAILED_SESSION                 0x10000   
//ZZ: Merge session.
#define JIF_CAT_MERGE_SESS_BY_SETTING                 0x20000  
//ZZ: Lock session for catalog\.
#define JIF_CAT_LOCK_SESS_FOR_CATALOG_READ            0x40000
//ZZ: Generate catalog file for specified volume.
#define JIF_CAT_GENERATE_CATALOG_FOR_VOLUME           0x80000
//ZZ: Start next schedule catalog job. This maybe not occur in normal mode.
#define JIF_CAT_START_NEXT_CATLOG_JOB                 0x100000
//ZZ: Update current session when session replication starts
#define JIF_CUR_SESSION                               0x200000
//ZZ: Set replication session range
#define JIF_REP_SESSION_RANGE                         0x400000
//ZZ: Set replication throughput
#define JIF_REP_PROGRESS_THROUGHPUT                   0x800000
//ZZ: Set compression type information
#define JIF_COMPRESS_STATUS                           0x1000000
//set vm identifier
#define JIF_VM_IDENTIFIER                             0x2000000 //<huvfe01>2014-8-14 update vm instance uuid after vm recovery job finished.
//update vApp child job progress
#define JIF_VAPP_PROGRESS                             0x4000000

typedef enum _CONTROL_COMMAND_ID
{
    ECC_NOTHING = 0x00000000,
    ECC_STOP_JOB = 0x00000001
}E_CTRL_CMD_ID;

typedef struct _JOB_CONTROL
{
    ULONG ulJobCmdID;
}ST_JOB_CTRL, *PST_JOB_CTRL;

typedef struct _CATALOG_FIELD
{
    DWORD    dwBKJobID;                   //ZZ: Job ID used for backup job when this session is backed up.
    DWORD    dwBKSessNum;                 //ZZ: Session number for which catalog is created.
    WCHAR    wzBKBackupDest[MAX_PATH];    //ZZ: Backup destination for the session for which catalog is created.
    WCHAR    wzBKDestUsrName[MAX_PATH];   //ZZ: User name of backup destination when it is remote folder.
    WCHAR    wzBKDestPassword[MAX_PATH];  //ZZ: Password of backup destination when it is remote folder.  
    WCHAR    wzBKStartTime[MAX_PATH];     //ZZ: Session time.
    WCHAR    wzBKJobName[MAX_PATH];       //ZZ: Backup job name.
    WCHAR    wzCurCatVol[MAX_PATH];       //ZZ: Information for volume or Exchange database is generated catalog.
	WCHAR    wzVMInstUUID[MAX_PATH];      //ZZ: Launch VM instance UUID. Will be used to write crash activity log
}ST_CATALOG_FIELD, *PST_CATALOG_FIELD;


typedef struct _st_PURGE_DATA_FIELD
{
	DWORD			dwCurJobID;
	wchar_t			wszDataStoreName[MAX_PATH];
	BOOL			IsDedupDS;
	DWORD			dwTotalNodes2Purge;
	DWORD			dwCurrentNode2Purge;
	wchar_t			wszCurrentNodeName[MAX_PATH];
	DWORD			dwTotalSessOfCurNode;
	DWORD			dwCurSess2Purge;
}PURGE_DATA_FIELD, *PPURGE_DATA_FIELD;

//<sonmi01>2015-6-9 ###???
enum class enum_ulSubJobStatus : ULONG
{
	SJS_SUCCESS = 0,
	SJS_CHECK_RP_FAILED = 1,
};

//JOB_MONITOR::ulJobType
typedef struct _JOB_MONITOR
{
    ULONG	ulSessionID;		//Current Session Number
    ULONG   ulBeginSessID;      //Begin Session number for rps
    ULONG   ulEndSessID;        //End Session number for rps
    ULONG 	ulFlags;			//Indicate the action made to the share memory
    ULONG	ulJobPhase;			//BLI_PHASE::PHASE_TAKING_SNAPSHOT.. in "native\h\datatransmitter.h"
    ULONG/*SHORT*/  ulJobStatus;		//corespond to value for AF_JOB_STATUS_ACTIVE_STRING... in afdefine.h 
    ULONG	ulJobType;			//afjob.h, AF_JOBTYPE_BACKUP, AF_JOBTYPE_RESTORE
    ULONG	ulJobMethod;		//afjob.h, AF_JOBMETHOD_FULL, AF_JOBMETHOD_INCR, AF_JOBMETHOD_RESYNC
    //job level, is updated at begin of the job
    ULONG   ulCompressLevel;    //compress level which is defined in afdefine.h. zouyu01 on 2010-11-08
    ULONG	ulVolMethod;		//afjob.h, AF_JOBMETHOD_FULL, AF_JOBMETHOD_INCR, AF_JOBMETHOD_RESYNC
    //volume level, is updated at beginning of each volume
    //job/volume progress; job remaining time; job throughput so far can be caculated through following fields
    LONGLONG/*ULONG*/	ulEstBytesJob;			//JobLevel Total Bytes estimation
    LONGLONG/*ULONG*/	ulXferBytesJob;		//JobLevel Bytes processed
    LONGLONG/*ULONG*/	ulEstBytesDisk;		//Volume Level Total Bytes estimation
    LONGLONG/*ULONG*/	ulXferBytesDisk;		//Volume Level processed Bytes

    TCHAR	wszDiskName[256];	//current process disk name
    ULONG	ulVMDiskTransportMode; //<sonmi01>2010-11-17 vm disk transport mode

    LONGLONG/*ULONG*/	ulBackupStartTime;	//start backup time, update at JIF_INIT phase. In case UI need it
    ULONG	ulElapsedTime;		//Time spend on backup/restore, filled by backend.
	ULONG   ulRemainingTime; //Added for replication job
    //Not include situation that does not counted as backup time.
    //Time when restore file process
    //Throughput = ulXferKBJob/ulElaspsedTime
    // D2D for Exchange GRT. zhazh06 R16. 
    TCHAR	wszEDB[512];	// current EDB file
    TCHAR	wszMailFolder[512];	// current Mail folder
    ULONG	ulTotalFolder;
    ULONG	ulProcessedFolder;

    USHORT		nProgramCPU;	// CPU usage of Afbackend.exe 	Numbers, should be 0 ~ 100
    USHORT		nSystemCPU;		// Total system CPU usage		Numbers, should be 0 ~ 100, and should be no less than CPU usage of afbackend.exe

    ULONG	nReadSpeed;		// Read I/O speed of afbackend.exe	MB/min
    ULONG	nWriteSpeed;	// Write I/O speed of afbackend.exe	MB/min
    ULONG	nSystemReadSpeed;// Total system read speed	MB/min
    ULONG	nSystemWriteSpeed;//Total system write speed MB/min
	ULONG	nLogicSpeed;	// Total logic speed MB/min
    ULONG   ulThrottling;       //ZZ: Backup throughout throttling, MB/min.
    ULONG   ulEncInfoStatus;    //ZZ: Encryption algorithm ID, AES-128, 192 and 256
    LONGLONG/*ULONG*/   ulTotalSizeRead;    //ZZ: Total size(in bytes) have been read from snapshot. Means data size before compression,
    LONGLONG/*ULONG*/   ulTotalSizeWritten; //ZZ: Total size(in bytes) have been written to VHD. Means data size after compression. [Compression rate]=ulTotalSizeWritten/ulTotalSizeRead;
    LONGLONG	ulTotalUniqueSize;//the size after deduped, it is used to calculate dedupe percentage, the ulTotalSizeWritten is compressed size
	ULONG   ulSavedBandwidthPercent; //ZZ: the overall saved bandwidth during replication due to compression/deduplication Should be 0 ~ 100
    WCHAR   wzCurVolMntPoint[MAX_PATH];  //ZZ: Current volume being backed up. Maybe mount point, driver letter or GUID.
    ST_JOB_CTRL stJobCtrl;           //ZZ: Receive some control command, such as stop purge hole process.
    ST_CATALOG_FIELD stCatalogField;  //ZZ: Some catalog information.

    ULONG ulTotalMegedSessions; // the number of total to be merged sessions
    ULONG ulMergedSessions;     // the sequence of being merged sessions

	// add below 4 fields for intermerge.exe use.
	ULONG   ulTotalDiskCnt;
	ULONG   ulCurrDiskIdx;
	wchar_t wszNodeName[64];	
	wchar_t wszPolicyGuid[64];
	wchar_t wszRPSName[64];
	wchar_t wszRemoteRPSName[64];
	wchar_t wszSourceDataStoreName[64];
	wchar_t wszTargetDataStoreName[64];

	wchar_t wszNodeGuid[64]; //<huvfe01>2014-8-14 update vm instance uuid after vm recovery job finished.
	ULONG ulTotalVMJobCount;
	ULONG ulFinishedVMJobCount;
	ULONG ulCanceledVMJobCount;
	ULONG ulFailedVMJobCount;

	//<sonmi01>2011-10-31 #add rps job flag for web service more processing // add below fields for rpsreplication.exe use.
	ULONG JobEndFlagForRpsRep;	//bitwise fields to indicate rep occur after which job finished, backup job or catalog job, REP_AFTER_XXX_JOB_END

    WCHAR wzD2DIdentity[MAX_PATH];     //ZZ: Unique string to indicate a D2D server.
	ULONG ulJobStatusShadow; //<sonmi01>2014-8-11 #shadow of ulJobStatus except for JS_PROC_EXIT
	ULONG ulSubJobStatus; //<sonmi01>2015-6-9 ###???

	PURGE_DATA_FIELD		purgejobfield;

	wchar_t wszVMHostName[64]; // hostname of guest OS within VM. ZhangHeng

}JOB_MONITOR, *PJOB_MONITOR;

class IJobMonitor
{
public:
    virtual DWORD Read(PJOB_MONITOR pJM) = 0;
    virtual DWORD Write(const PJOB_MONITOR pJM) = 0;
    virtual DWORD AFJobUpdate(const PJOB_MONITOR pJM) = 0;
    virtual PJOB_MONITOR GetMapView() = 0;
    virtual VOID Destroy() = 0;
    virtual DWORD GetJobID() = 0;
    virtual bool IsOpenExist() = 0;
	//luoca01: add this function to update job type when starting a job, to fix issue 145148
	virtual VOID UpdateJobType(ULONG ulJobType) = 0;
};

DWORD WINAPI CreateIJobMonitor(DWORD dwShrMemID, IJobMonitor **ppIJobMonitor, wchar_t* pwszProductName = NULL );
DWORD WINAPI CreateIJobMonitorByName(PWCHAR pwszNodeName, IJobMonitor **ppIJobMonitor, wchar_t* pwszProductName = NULL );
VOID WINAPI  DestroyIJobMonitor(IJobMonitor **ppIJobMonitor);


/*
*      Job Monitor Data Block
*/
typedef struct  _JMDB {
	ULONG ulCommand;
	FILETIME ft;
} JMDB, *PJMDB;

class CSharedMemory
{
public:
	CSharedMemory();
	DWORD Create(DWORD dwSize, LPCTSTR szName, LPCTSTR szMutexName);
	LPVOID GetMapView();
	DWORD Write(LPCVOID pvBuffer, ULONG nSize, ULONG nOffset = 0, BOOL bLock = FALSE);
	DWORD Read(LPVOID pvBuffer, ULONG nSize, ULONG nOffset = 0, BOOL bLock = FALSE);
	bool IsOpenExist() { return m_bIsOpenExist; } 
	VOID Destroy();
	virtual ~CSharedMemory();

	void Lock();
	void Unlock();

private:
	HANDLE	m_hFileMapping;
	LPVOID	m_pMemory;
	ULONG	m_nSize;
	HANDLE	m_hMutex;
	bool    m_bIsOpenExist;     //ZZ: If this share memory exists before create.
	//BOOL m_bInitialized;
protected:
    CDbgLog	m_Log; //<huvfe01>
};

//ZZ: Some initial value for job monitor.
#define JOBMON_FIELD_NOT_SET_FLAGS        0xFFFFFFFF

class CJobMonitor : public IJobMonitor
{
public:
	CJobMonitor(DWORD dwShrMemID, wchar_t* pwszProductName = L"D2D");
	CJobMonitor(PWCHAR pwszNodeName, wchar_t* pwszProductName = L"D2D");
	virtual ~CJobMonitor();
	virtual DWORD Read(PJOB_MONITOR pJM);
	virtual DWORD Write(const PJOB_MONITOR pJM);
	virtual DWORD AFJobUpdate(const PJOB_MONITOR pJM);
	virtual PJOB_MONITOR GetMapView();
	virtual VOID Destroy();
	virtual DWORD GetJobID();
	virtual bool IsOpenExist(){return (m_SharedMemory.IsOpenExist());}
	virtual VOID UpdateJobType(ULONG ulJobType);

private:
	//ZZ: Set some initial value for job monitor so that GUI can show N/A when field is not set.
	DWORD InitJobMonitor(PJOB_MONITOR pJobMonitor = NULL);
	BOOL  IsJobDone(PJOB_MONITOR pJobMonitor);

private:
	DWORD			m_ulShrMemID;
	void Create(PWCHAR pwszNodeName);
	static const int BUFF_SIZE = 1024;
	WCHAR			m_shareMemName[BUFF_SIZE];
	WCHAR			m_mutexName[BUFF_SIZE];
	CSharedMemory	m_SharedMemory;
	BOOL			m_bAvailable;
	DWORD			m_dwPageSize;
	PJOB_MONITOR	m_pJobMonitor;
	CDbgLog			m_Log;
};

/* BEGIN: Added by huvfe01, 2013/3/13   PN:ARCserve Oolong Job Monitor and Cancel Job */
class CGlobalMutex
{
public:
    CGlobalMutex(const CString& strGlobalMutexName, LPSECURITY_ATTRIBUTES lpsaAttribute = NULL, BOOL bInitiallyOwn = FALSE)
    {
        m_hMutex = CreateMutex(lpsaAttribute, bInitiallyOwn, strGlobalMutexName.GetString());
        if (NULL != m_hMutex)
        {
            WaitForSingleObject(m_hMutex, INFINITE);
        }
    }

    virtual ~CGlobalMutex()
    {
        if (NULL != m_hMutex)
        {
            ReleaseMutex(m_hMutex);
            CloseHandle(m_hMutex);
            m_hMutex = NULL;
        }
    }

private:
    HANDLE m_hMutex;
};

template<typename T>
class CSharedQueue : private CSharedMemory
{
	typedef struct tagQueueSentry
	{
		DWORD dwSize;
        DWORD dwUsedSize;
		DWORD dwHdrIndex;
		DWORD dwTailIndex;
	}QUEUE_SENTRY_S;

public:
    CSharedQueue();
    virtual ~CSharedQueue();
	
public:
    DWORD CreateQ(DWORD dwSize, LPCTSTR szName, BOOL bInitial,
                  LPCTSTR szMutexName,
                  BOOL bBlockingMode = FALSE, //specify if pull function will wait INFINITE on the data coming
                  LPCTSTR szSemaphoreName = NULL);
	
	VOID  DestroyQ();
	DWORD Push(const T& Data);
	DWORD Pull(T& Data);

	DWORD GetQueueTotalSize(DWORD &dwTotalSize);
    DWORD GetQueueFreeSize(DWORD &dwFreeSize);

private:
	DWORD InitQueueSentry();    
	DWORD GetQueueSentry(QUEUE_SENTRY_S& stQueueSentry);	
    DWORD UpdateQueueSentry(const QUEUE_SENTRY_S& stQueueSentry);

private:
    DWORD m_dwQueueSize;
    CString m_strQueueName;
    CString m_strGlobalMutexName;
    CString m_strGlobalSemaphoreName;
    HANDLE m_hSemaphore;
    HANDLE m_hMutex;
    BOOL m_bBlockingMode;
	SECURITY_DESCRIPTOR m_sd; 
	SECURITY_ATTRIBUTES m_sa; 

};

template<typename T>
CSharedQueue<T>::CSharedQueue()
{
    m_dwQueueSize = 0;
    m_strQueueName = L"";
    m_strGlobalMutexName = L"";
    m_strGlobalSemaphoreName = L"";
    m_hSemaphore = NULL;
    m_hMutex = NULL;
    m_bBlockingMode = FALSE;
    
	InitializeSecurityDescriptor(&m_sd,SECURITY_DESCRIPTOR_REVISION); 
	SetSecurityDescriptorDacl(&m_sd,TRUE,(PACL)NULL,FALSE); //set all the user can access the object      
	m_sa.nLength = sizeof(SECURITY_ATTRIBUTES);
	m_sa.bInheritHandle = FALSE;
	m_sa.lpSecurityDescriptor = &m_sd;
}

template<typename T>
CSharedQueue<T>::~CSharedQueue()
{
    DestroyQ();
}

template<typename T>
DWORD CSharedQueue<T>::CreateQ(DWORD dwSize, LPCTSTR szName, BOOL bInitial,
                               LPCTSTR szMutexName,
                               BOOL bBlockingMode,
                               LPCTSTR szSemaphoreName)
{
    DWORD dwQueueSentrySize = sizeof(QUEUE_SENTRY_S);
    DWORD dwQueueItemSize   = sizeof(T);
    DWORD dwRet = 0;

    if ((NULL == szName) || (NULL == szMutexName))
    {
        return AF_Error_Param;
    }

    //create global mutex
    m_strGlobalMutexName = szMutexName;
    m_hMutex = CreateMutex(&m_sa, FALSE, szMutexName);
    if (NULL == m_hMutex)
    {
    	dwRet = GetLastError();
		m_Log.LogW(LL_ERR, dwRet, L"%s: Failed to create Mutex for Shared Queue.", __WFUNCTION__);
        return AF_Error_Init;
    }

    //create global semaphore
    m_bBlockingMode = bBlockingMode;
    if (bBlockingMode)
    {
        if (szSemaphoreName)
        {
            m_strGlobalSemaphoreName = szSemaphoreName;
            m_hSemaphore = CreateSemaphore(&m_sa, 0, dwSize, szSemaphoreName);
            if (NULL == m_hSemaphore)
            {
            	dwRet = GetLastError();
				m_Log.LogW(LL_ERR, dwRet, L"%s: Failed to create Semaphore for Shared Queue.", __WFUNCTION__);
                return AF_Error_Init;
            }
        }
        else
        {
			m_Log.LogW(LL_ERR, dwRet, L"%s: Semaphore name is NULL.", __WFUNCTION__);
            return AF_Error_Param;
        }
    }
    
    //create Queue
    m_dwQueueSize = dwSize;
    m_strQueueName = szName;
    dwRet = Create((dwQueueSentrySize + dwQueueItemSize * dwSize), szName, NULL);
    if (AF_OK == dwRet)
    {
    	if (bInitial)
		{
	        dwRet = InitQueueSentry();
		}
    }
	else
	{		
		m_Log.LogW(LL_ERR, dwRet, L"%s: Failed to create share memory for shared queue.", __WFUNCTION__);
	}

    return dwRet;
}

template<typename T>
VOID CSharedQueue<T>::DestroyQ()
{
    if (NULL != m_hSemaphore)
    {
        CloseHandle(m_hSemaphore);
        m_hSemaphore = NULL;
    }

    if (NULL != m_hMutex)
    {
        CloseHandle(m_hMutex);
        m_hMutex = NULL;
    }
}

template<typename T>
DWORD CSharedQueue<T>::Push(const T& Data)
{
    DWORD dwRet = 0;
    QUEUE_SENTRY_S stQueueSentry;
    ZeroMemory(&stQueueSentry, sizeof(QUEUE_SENTRY_S));

    CGlobalMutex lock(m_strGlobalMutexName, &m_sa);

    dwRet = GetQueueSentry(stQueueSentry);
    if (0 == dwRet)
    {
        DWORD dwTailIndex = stQueueSentry.dwTailIndex;
        DWORD dwHdrIndex = stQueueSentry.dwHdrIndex;
        DWORD dwQueueSize = stQueueSentry.dwSize;
        DWORD dwUsedSize = stQueueSentry.dwUsedSize;

        dwRet = AF_Error_Size;
        if (dwQueueSize > dwUsedSize)
        {
            dwRet = Write(&Data, sizeof(T), sizeof(QUEUE_SENTRY_S) + dwHdrIndex * sizeof(T), FALSE);
            if (AF_OK == dwRet)
            {
                (stQueueSentry.dwHdrIndex)++;
                (stQueueSentry.dwHdrIndex) %= dwQueueSize;
                (stQueueSentry.dwUsedSize)++;
                dwRet = UpdateQueueSentry(stQueueSentry);

                if (m_hSemaphore)
                {
                    ReleaseSemaphore(m_hSemaphore, 1, NULL);
                }
            }
			else
			{				
				m_Log.LogW(LL_ERR, dwRet, L"%s: Failed to write data to share memory for shared queue.", __WFUNCTION__);
			}
        }
		else
		{
			m_Log.LogW(LL_ERR, dwRet, L"%s: Failed to push data because queue is full.", __WFUNCTION__);
		}
    }

    return dwRet;
}

template<typename T>
DWORD CSharedQueue<T>::Pull(T& Data)
{
    DWORD dwRet = 0;
    QUEUE_SENTRY_S stQueueSentry;
    ZeroMemory(&stQueueSentry, sizeof(QUEUE_SENTRY_S));

    if (m_hSemaphore)
    {
        dwRet = WaitForSingleObject(m_hSemaphore, (m_bBlockingMode ? INFINITE: 0L));
		if (WAIT_FAILED == dwRet)
		{
			m_Log.LogW(LL_ERR, dwRet, L"%s: wait semaphore failed when pulling data.", __WFUNCTION__);
		}
    }

    CGlobalMutex lock(m_strGlobalMutexName, &m_sa);

    dwRet = GetQueueSentry(stQueueSentry);
    if (AF_OK == dwRet)
    {
        DWORD dwTailIndex = stQueueSentry.dwTailIndex;
        DWORD dwHdrIndex = stQueueSentry.dwHdrIndex;
        DWORD dwQueueSize = stQueueSentry.dwSize;
        DWORD dwUsedSize = stQueueSentry.dwUsedSize;

        dwRet = AF_Error_Size;
        if (dwUsedSize > 0)
        {
            dwRet = Read(&Data, sizeof(T), sizeof(QUEUE_SENTRY_S) + dwTailIndex * sizeof(T), FALSE);
            if (AF_OK == dwRet)
            {
                (stQueueSentry.dwTailIndex)++;
                (stQueueSentry.dwTailIndex) %= dwQueueSize;
                (stQueueSentry.dwUsedSize)--;
                dwRet = UpdateQueueSentry(stQueueSentry);
            }
        }
    }

    return dwRet;
}

template<typename T>
DWORD CSharedQueue<T>::GetQueueTotalSize(DWORD &dwTotalSize)
{
    DWORD dwRet = 0;
    QUEUE_SENTRY_S stQueueSentry;
    ZeroMemory(&stQueueSentry, sizeof(QUEUE_SENTRY_S));

    CGlobalMutex lock(m_strGlobalMutexName, &m_sa);

    dwRet = GetQueueSentry(stQueueSentry);
    if (AF_OK == dwRet)
    {
        dwTotalSize = stQueueSentry.dwSize;
    }

    return dwRet;
}

template<typename T>
DWORD CSharedQueue<T>::GetQueueFreeSize(DWORD &dwFreeSize)
{
    DWORD dwRet = 0;
    QUEUE_SENTRY_S stQueueSentry;
    ZeroMemory(&stQueueSentry, sizeof(QUEUE_SENTRY_S));

    CGlobalMutex lock(m_strGlobalMutexName, &m_sa);

    dwRet = GetQueueSentry(stQueueSentry);
    if (AF_OK == dwRet)
    {
        dwFreeSize = stQueueSentry.dwSize - stQueueSentry.dwUsedSize;
    }

    return dwRet;
}

template<typename T>
DWORD CSharedQueue<T>::InitQueueSentry()
{
    QUEUE_SENTRY_S stQueueSentry;
    ZeroMemory(&stQueueSentry, sizeof(QUEUE_SENTRY_S));

    stQueueSentry.dwHdrIndex = 0;
    stQueueSentry.dwTailIndex = 0;
    stQueueSentry.dwUsedSize = 0;
    stQueueSentry.dwSize = m_dwQueueSize;

    return Write(&stQueueSentry, sizeof(QUEUE_SENTRY_S), 0, FALSE);
}

template<typename T>
DWORD CSharedQueue<T>::GetQueueSentry(QUEUE_SENTRY_S& stQueueSentry)
{
    return Read(&stQueueSentry, sizeof(QUEUE_SENTRY_S), 0, FALSE);
}

template<typename T>
DWORD CSharedQueue<T>::UpdateQueueSentry(const QUEUE_SENTRY_S& stQueueSentry)
{
    return Write(&stQueueSentry, sizeof(QUEUE_SENTRY_S), 0, FALSE);
}
/* END:   Added by huvfe01, 2013/3/13   PN:ARCserve Oolong Job Monitor and Cancel Job */

