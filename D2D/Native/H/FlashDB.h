#pragma once
#include <Windows.h>
#include <string>

#define FLASHDB_OK						0
#define FLASHDB_ERROR					-1
#define FLASHDB_CANNOT_CONNECT			-2
#define FLASHDB_MEM_NOT_ENOUGH			-3
#define FLASHDB_INVALID_PARAM			-4
#define FLASHDB_CLEAN_DB_ERROR			-5
#define FLASHDB_CORRUPTED				11	// db corrupted, this error is returned from sqlite


#define FLASHDB_NO_DATA					-100

#define LENGTH_OF_NODEID				65
#define LENGTH_OF_NODENAME				256
#define LENGTH_OF_MESSAGE				1024
typedef struct _FLASHDB_JOB_HISTORY
{
	DWORD			dwID;										// the interal id in database
	ULONGLONG		ullJobId;									// the job ID
	DWORD			dwJobType;									// the job type - backup, restor, merge.... 
	DWORD			dwJobMethod;								// the job method - full, incremental
	DWORD			dwStatus;									// the job status - crashed, completed, failed, or canceled
    DWORD           dwAdvSchFlag;                               // the job advance schedule flag to indicate daily, weekly, monthly ro repeat.
	ULONGLONG		ullUTCStartTime;							// the job start time ( UTC )
	ULONGLONG		ullLocalStartTime;							// the job start time ( local )
	ULONGLONG		ullUTCEndTime;								// the job end time ( UTC )
	ULONGLONG		ullLocalEndTime;							// the job end time ( local )
	ULONGLONG		ullDSVersion;								// the version of source data store 
	ULONGLONG		ullDstDSVersion;							// the version of target data store ( for Replication job only )
	ULONGLONG		ullRecoveryPoint;							// the time of recovery point (UTC Time )
	ULONGLONG		ullRecoveryPointLocal;						// the time of recovery point (Local time)
	wchar_t			wszRunningNode[LENGTH_OF_NODENAME];			// the node name of where the job is running       
	wchar_t			wszRunningNodeUUID[LENGTH_OF_NODEID];		// the node GUID of where the job is running
	wchar_t			wszDisposeNode[LENGTH_OF_NODENAME];			// the node name of what the job is running for
	wchar_t			wszDisposeNodeUUID[LENGTH_OF_NODEID];		// the node GUID of what the job is running for
	wchar_t			wszSourceUUID[LENGTH_OF_NODEID];			// the node GUID of the source RPS ( for Replication job only )
	wchar_t			wszTargetUUID[LENGTH_OF_NODEID];			// the node GUID of the destination RPS ( for Replication job and backup job to RPS )
	wchar_t			wszDataStoreUUID[LENGTH_OF_NODEID];			// the data store uuid of the source RPS
	wchar_t			wszTargetDataStoreUUID[LENGTH_OF_NODEID];	// the data store uuid of the target RPS ( for Replication job only )
	wchar_t			wszPlanUUID[LENGTH_OF_NODEID];				// the plan uuid of the source RPS
	wchar_t			wszTargetPlanUUID[LENGTH_OF_NODEID];		// the plan uuid of the target RPS ( for Replication job only )
	wchar_t			wszJobName[LENGTH_OF_NODENAME];				// the job name
}FLASHDB_JOB_HISTORY, *PFLASHDB_JOB_HISTORY;

typedef struct _FLASHDB_ACTIVITY_LOG
{
	DWORD			dwID;										// the internal id in database
	DWORD			dwLogLevel;									// the log level
	DWORD			dwJobType;									// the job type - backup, restor, merge.... 
	DWORD			dwJobMethod;								// the job method - full, incremental
	ULONGLONG		ullJobID;									// the job ID
	ULONGLONG		ullTimeUTC;									// the time of activtity log ( UTC )
	ULONGLONG		ullTimeLocal;								// the time of activtity log ( local )
	DWORD			dwResourceID;								// the resource ID of this activity log
	wchar_t			wszRunningNode[LENGTH_OF_NODENAME];			// the node name of where the job is running       
	wchar_t			wszRunningNodeUUID[LENGTH_OF_NODEID];		// the node GUID of where the job is running
	wchar_t			wszDisposeNode[LENGTH_OF_NODENAME];			// the node name of what the job is running for
	wchar_t			wszDisposeNodeUUID[LENGTH_OF_NODEID];		// the node GUID of what the job is running for
	wchar_t			wszSourceUUID[LENGTH_OF_NODEID];			// the node GUID of the source RPS ( for Replication job only )
	wchar_t			wszTargetUUID[LENGTH_OF_NODEID];			// the node GUID of the destination RPS ( for Replication job and backup job to RPS )
	wchar_t			wszDataStoreUUID[LENGTH_OF_NODEID];			// the data store GUID of RPS
	wchar_t			wszTargetDataStoreUUID[LENGTH_OF_NODEID];	// the data store GUID of destination RPS ( for Replication job only )
	wchar_t			wszPlanUUID[LENGTH_OF_NODEID];				// the plan uuid of the source RPS
	wchar_t			wszTargetPlanUUID[LENGTH_OF_NODEID];		// the plan uuid of the target RPS ( for Replication job only )
	wchar_t			wszLogMessage[LENGTH_OF_MESSAGE];			// the log message
}FLASHDB_ACTIVITY_LOG, *PFLASHDB_ACTIVITY_LOG;

typedef enum
{
	EFDT_NORMAL = 0,	//FLASH DB Type -- to store normal data, the database will enable sync
	EFDT_LOG			//FLASH DB Type -- to store activity log data, the database will disable sync to get better performance
}E_FLASH_DB_TYPE;

#define ACTLOG_QUERY_BY_AGENTNODE            0X0001	// by agent node UUID
#define ACTLOG_QUERY_BY_SERVERNODE			 0X0002	// by running node UUID
#define ACTLOG_QUERY_BY_AGENTNODE_NAME       0X0004	// by agent node name
#define ACTLOG_QUERY_BY_SERVERNODE_NAME		 0X0008	// by running node name
#define ACTLOG_QUERY_BY_UTCTIME				 0X0010	// by utc time
#define ACTLOG_QUERY_BY_LOCALTIME			 0X0020	// by local time
#define ACTLOG_QUERY_BY_UNIQUEID			 0X0040	// by unique id
#define ACTLOG_QUERY_BY_JOBID				 0X0080	// by job id
typedef struct _FLAHSDB_ACT_LOG_FILTER_COL
{
	ULONGLONG	ullAfterTimeUTC;								// the time of activity log ( UTC ), will add filter utctime >= ullAfterTimeUTC. 0 will not enable this filter.
	ULONGLONG	ullEndTimeUTC;									// the end  time of activity log ( UTC ), will add filter utctime < ullEndTimeUTC. 0 will not enable this filter.
	ULONGLONG	ullAfterTimeLocal;								// the time of activity log ( local ), will add filter localtime >= ullAfterTimeLocal. 0 will not enable this filter.
	ULONGLONG	ullEndTimeLocal;								// the end time of activity log ( local ), will add filter localtime < ullEndTimeLocal. 0 will not enable this filter.
	DWORD		dwStartID;										// the start Unique ID of activity log, will add filter id>=dwStartID. 
	DWORD		dwEndID;										// the end Unique ID of activity log, will add filter id<dwEndID. 0 will not enable this filter.
	ULONGLONG	ullJobID;										// the job ID, will add filter jobid = ullJobID. 0 will not enable this filter.
	DWORD		dwFilterFlags;
	wchar_t		wszDisposeNodeUUID[LENGTH_OF_NODEID];			// the node GUID of what the job is running for. Will add filter dispose_agent_uuid = wszDisposeNodeUUID
	wchar_t     wszRunningNodeUUID[LENGTH_OF_NODEID];			// the node GUID of where the job is running. Will add filter running_uuid = wszRunningNodeUUID	
	wchar_t		wszDisposeNode[LENGTH_OF_NODENAME];				// the node name of what the job is running for. Will add filter dispose_node = wszDisposeNode
	wchar_t		wszRunningNode[LENGTH_OF_NODENAME];				// the node name of where the job is running. Will add filter running_node = wszDisposeNode

	_FLAHSDB_ACT_LOG_FILTER_COL(){
		ullAfterTimeUTC = 0;
		ullEndTimeUTC = 0;
		ullAfterTimeLocal = 0;
		ullEndTimeLocal = 0;
		dwStartID = 0;
		dwEndID = 0;
		ullJobID = 0;
		dwFilterFlags = 0;
		ZeroMemory( wszDisposeNodeUUID, sizeof(wszDisposeNodeUUID) );
		ZeroMemory( wszRunningNodeUUID, sizeof(wszRunningNodeUUID) );
		ZeroMemory( wszRunningNode, sizeof(wszRunningNode) );
		ZeroMemory( wszDisposeNode, sizeof(wszDisposeNode) );
	}
}FLASHDB_ACT_LOG_FILTER_COL, *PFLASHDB_ACT_LOG_FILTER_COL;

typedef enum
{
	FALOT_ORDER_BY_TIME_DESC = 0x0000000000000001,
	FALOT_ORDER_BY_TIME_ASC = 0x0000000000000002
}FLASH_ACT_LOG_ORDER_TYPE;

#define JOBHISTORY_QUERY_BY_AGENTNODE            0X0001
#define JOBHISTORY_QUERY_BY_SERVERNODE			 0X0002
typedef struct _FLAHSDB_JOB_HISTORY_FILTER_COL
{
	ULONGLONG		ullJobId;										// the job ID, will add filter jobid = ullJobId.
	DWORD			dwJobType;										// the job type - backup, restor, merge.... will add filter jobtype = dwJobType. 0xFFFFFFFF will disable this
	DWORD			dwStatus;										// the job status - crashed, completed, failed, or canceled. will add filter status = dwStatus, oxFFFFFFFF will disable this
	ULONGLONG		ullAfterUTCStartTime;							// the job start time ( UTC ), will add filter utcstarttime >= ullAfterUTCStartTime
	ULONGLONG		ullAfterUTCEndTime;								// the job end time ( UTC ), will add filter utcendtime >= ullAfterUTCEndTime
	DWORD			dwFilterFlags;
	wchar_t			wszDisposeNodeUUID[LENGTH_OF_NODEID];			// the node GUID of what the job is running for. Will add filter dispose_agent_uuid = wszDisposeNodeUUID
	wchar_t			wszRunningNodeUUID[LENGTH_OF_NODEID];			// the node GUID of where the job is running. Will add filter running_uuid = wszRunningNodeUUID
}FLASHDB_JOB_HISTORY_FILTER_COL, *PFLASHDB_JOB_HISTORY_FILTER_COL;
typedef enum
{
	FJHOT_ORDER_BY_TIME_DESC = 0x0000000000000001,
	FJHOT_ORDER_BY_TIME_ASC = 0x0000000000000002
}FLASH_JOB_HISTORY_ORDER_TYPE;

class IActLogDB
{
public:
	virtual void	Release( ) = 0;

	virtual long	CreateDB( BOOL isOverwrite ) = 0;

	virtual long	ShrinkDB() = 0;

	virtual long	LockDBForRepair( ) = 0;

	virtual long	BackupDB( LPCWSTR lpszNewDBFile, void(*xProgress)(int, int) ) = 0;

	virtual long	InsertActivityLog(FLASHDB_ACTIVITY_LOG* pLog) = 0;

	virtual long    InsertActivityLogEx(PFLASHDB_ACTIVITY_LOG pLogs, ULONGLONG ullCnt) = 0;
	
	virtual long	GetActivityLogs( ULONGLONG ullStart, ULONGLONG ullRequest, ULONGLONG* pUllCnt, 
									 ULONGLONG* pUlltotal, PFLASHDB_ACTIVITY_LOG* ppLogs,
									 ULONGLONG falotOrderType = FALOT_ORDER_BY_TIME_DESC,
									 const PFLASHDB_ACT_LOG_FILTER_COL pActivityLogFilter = NULL ) = 0;

	virtual long	DeleteActivityLogsByLocalTime( LPCWSTR lpszRunningNodeID, ULONGLONG ullTimeLocal ) = 0;

	virtual long	DeleteActivityLogsByUTCTime( LPCWSTR lpszRunningNodeID, ULONGLONG ullTimeUTC ) = 0;

	virtual long	UpdateNodeNameOfActivityLog( ULONGLONG ullJobID, LPCWSTR lpszNodeName ) = 0;
	
	virtual long	UpdateJobMethodOfActivityLog( ULONGLONG ullJobID, DWORD dwJobMethod ) = 0;
	
	virtual long	CleanAcitivityLogs( ) = 0;	
};


class IJobHistoryDB
{
public:

	virtual void	Release( ) = 0;

	virtual long	CreateDB(BOOL isOverwrite) = 0;

	virtual long	ShrinkDB() = 0;

	virtual long	LockDBForRepair( ) = 0;

	virtual long	BackupDB( LPCWSTR lpszNewDBFile, void(*xProgress)(int, int) ) = 0;

	virtual long	GetJobHistories( ULONGLONG ullStart, ULONGLONG ullRequest, OUT ULONGLONG* pUllCnt, 
									 OUT ULONGLONG* pUlltotal, OUT PFLASHDB_JOB_HISTORY* ppJobHistories,
								     ULONGLONG fjhotOrderType= FJHOT_ORDER_BY_TIME_DESC,
								     const PFLASHDB_JOB_HISTORY_FILTER_COL pHistoryFilter = NULL) = 0;

	virtual long	GetJobHistory( ULONGLONG ullJobID, FLASHDB_JOB_HISTORY*  pHistory ) = 0;

	virtual long	DeleteJobHistoryByLocalTime( ULONGLONG ullTimeLocal ) = 0;

	virtual long	DeleteJobHistoryByUTCTime( ULONGLONG ullTimeUTC ) = 0;

	virtual long	UpdateNodeNameOfJobHistory( ULONGLONG ullJobID, LPCWSTR lpszNodeName ) = 0;

	virtual long	UpdateJobDetails( ULONGLONG ullJobID, void* pDetail, DWORD dwSizeInBytes ) = 0;

	virtual long	GetJobDetails( ULONGLONG ullJobID, void* pDetail, PDWORD pdwSizeInBytes ) = 0;

	virtual long	MarkJobAsEnd( ULONGLONG ullJobID, DWORD dwJobStatus ) = 0;

	virtual long	UpdateJobHistory( FLASHDB_JOB_HISTORY*  pHistory ) = 0;
};

//
// create activity log db instance by product type. 
// nPrdType==PRODUCT_D2D or nPrdType==PRODUCT_RPS
//
DWORD WINAPI CreateActLogDBInstance( int nPrdType, IActLogDB** ppActLogDB );
typedef DWORD (WINAPI *PFUNC_CreateActLogDBInstance) ( int, IActLogDB** );

//
// create job history db instance by product type. 
// nPrdType==PRODUCT_D2D or nPrdType==PRODUCT_RPS
//
DWORD WINAPI CreateJobHisotryDBInstance( int nPrdType, IJobHistoryDB** ppJobHistoryDB );
typedef DWORD (WINAPI *PFUNC_CreateJobHisotryDBInstance) ( int, IJobHistoryDB** );

//
// create activity log db instance by a file
//
DWORD WINAPI CreateActLogDBInstanceEx( LPCWSTR lpszDBFile , IActLogDB** ppActLogDB );
typedef DWORD (WINAPI *PFUNC_CreateActLogDBInstanceEx) ( LPCWSTR, IActLogDB** );

//
// create job history db instance by a file
//
DWORD WINAPI CreateJobHisotryDBInstanceEx( LPCWSTR lpszDBFile, IJobHistoryDB** ppJobHistoryDB );
typedef DWORD (WINAPI *PFUNC_CreateJobHisotryDBInstanceEx) ( LPCWSTR, IJobHistoryDB** );

//
// get the default activity log db file path
//
DWORD WINAPI GetActLogDBFile( int nPrdType, LPWSTR pszFilePath, DWORD* pdwSize );
typedef DWORD (WINAPI *PFUNC_GetActLogDBFile)( int, LPWSTR, DWORD* );

//
// get the default job history db file path
//
DWORD WINAPI GetJobHistoryDBFile( int nPrdType, LPWSTR pszFilePath, DWORD* pdwSize );
typedef DWORD (WINAPI *PFUNC_GetJobHistoryDBFile)( int, LPWSTR, DWORD* );

/********************************************************************
*
*  a wraper class for easily invoke IFlashDB
*
*********************************************************************/

#define MODULENAME_FLASHDBAPI  L"FlashDBAPI.dll"
class _CFlashDB
{
public:
	_CFlashDB( ) 
		: m_hModule( NULL )
		, m_pFuncCreateActLogDB(NULL)
		, m_pFuncCreateActLogDBEx(NULL)
		, m_pFuncCreateJobHistoryDB(NULL)
		, m_pFuncCreateJobHistoryDBEx(NULL)
		, m_pFuncGetActLogDBFile(NULL)
		, m_pFuncGetJobHistoryDBFile(NULL)
	{
		m_hModule = LoadLibrary( MODULENAME_FLASHDBAPI );
		if( m_hModule==NULL )
		{
			// load from d2d home\bin
			MEMORY_BASIC_INFORMATION mbi;
			static int dummy;
			VirtualQuery( &dummy, &mbi, sizeof(mbi) );
			HMODULE h = (HMODULE)mbi.AllocationBase;

			WCHAR szPath[MAX_PATH] = {0};
			::GetModuleFileName( h, szPath, MAX_PATH );
			if( wcslen(szPath)>0 )
			{
				WCHAR* ptr = wcsrchr( szPath, L'\\' );
				if( ptr ) 
				{ 
					ptr++; *ptr = 0; 
					wcscat_s( szPath, _ARRAYSIZE(szPath), MODULENAME_FLASHDBAPI );
					m_hModule = LoadLibrary( szPath );
				}				
			}
		}

		if( m_hModule!=NULL )
		{
			m_pFuncCreateActLogDB  = (PFUNC_CreateActLogDBInstance)GetProcAddress(m_hModule, "CreateActLogDBInstance" );
			m_pFuncCreateActLogDBEx= (PFUNC_CreateActLogDBInstanceEx)GetProcAddress(m_hModule, "CreateActLogDBInstanceEx" );

			m_pFuncCreateJobHistoryDB = (PFUNC_CreateJobHisotryDBInstance)GetProcAddress(m_hModule, "CreateJobHisotryDBInstance" );
			m_pFuncCreateJobHistoryDBEx = (PFUNC_CreateJobHisotryDBInstanceEx)GetProcAddress(m_hModule, "CreateJobHisotryDBInstanceEx" );

			m_pFuncGetActLogDBFile = (PFUNC_GetActLogDBFile)GetProcAddress(m_hModule, "GetActLogDBFile" );
			m_pFuncGetJobHistoryDBFile = (PFUNC_GetJobHistoryDBFile)GetProcAddress(m_hModule, "GetJobHistoryDBFile" );
		}
	}

	~_CFlashDB( )
	{
		Destory( );
	}

	DWORD Init( )
	{
		if( !m_pFuncCreateActLogDB || !(m_pFuncCreateActLogDBEx) || 
			(!m_pFuncCreateJobHistoryDB) || (!m_pFuncCreateJobHistoryDBEx) || 
			(!m_pFuncGetActLogDBFile) || (!m_pFuncGetJobHistoryDBFile) )
			return (DWORD)-1;
		return 0;
	}

	void Destory( )
	{
		if( m_hModule ) 
			FreeLibrary( m_hModule );
		m_hModule = NULL;
	}

	DWORD GetActLogDB( int dwProductType, IActLogDB** pDBInst  )
	{
		return m_pFuncCreateActLogDB( dwProductType, pDBInst );
	}

	DWORD GetActLogDBEx( LPCWSTR lpszDBFile, IActLogDB** pDBInst  )
	{
		return m_pFuncCreateActLogDBEx( lpszDBFile, pDBInst );
	}

	DWORD GetJobHistoryDB( int dwProductType, IJobHistoryDB** pDBInst  )
	{
		return m_pFuncCreateJobHistoryDB( dwProductType, pDBInst );
	}

	DWORD GetJobHistoryDBEx( LPCWSTR lpszDBFile, IJobHistoryDB** pDBInst  )
	{
		return m_pFuncCreateJobHistoryDBEx( lpszDBFile, pDBInst );
	}

	DWORD GetActLogDBFile( int dwProductType, LPWSTR pszFilePath, DWORD* pdwSize )
	{
		return m_pFuncGetActLogDBFile( dwProductType, pszFilePath, pdwSize );
	}

	DWORD GetJobHistoryDBFile( int dwProductType, LPWSTR pszFilePath, DWORD* pdwSize )
	{
		return m_pFuncGetJobHistoryDBFile( dwProductType, pszFilePath, pdwSize );
	}

protected:
	HMODULE								m_hModule;
	PFUNC_CreateActLogDBInstance		m_pFuncCreateActLogDB;
	PFUNC_CreateActLogDBInstanceEx		m_pFuncCreateActLogDBEx;
	PFUNC_CreateJobHisotryDBInstance	m_pFuncCreateJobHistoryDB;
	PFUNC_CreateJobHisotryDBInstanceEx	m_pFuncCreateJobHistoryDBEx;
	PFUNC_GetActLogDBFile				m_pFuncGetActLogDBFile;
	PFUNC_GetJobHistoryDBFile			m_pFuncGetJobHistoryDBFile;
};

