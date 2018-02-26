#ifndef	__STRUCT_H
#define __STRUCT_H

#include <time.h>

/* STRUCT.H **********************************************************


**********************************************************************/
/* signature */
#define REQUEST_SIGNATURE       0xcccccccc
#define	OP_MAKEUP		        10
#define	OP_COMPARE				14
#define	OP_AUTO_PILOT			9

#define MAX_SERVER              4
#define MAX_PAGER               15
#define MAX_EMAIL               15

/* method */
#define VIRUS_REPORT            0x00
#define VIRUS_DELETE            0x01
#define VIRUS_RENAME            0x02
#define VIRUS_CURE              0x03
#define VIRUS_MOVE              0x04
#define VIRUS_PURGE             0x05
#define VIRUS_MOVE_RENAME       0x06

/* status */
#define REQ_HOLD                0x00
#define REQ_READY               0x01
#define REQ_ACTIVE              0x02

/* pattern */
#define SCAN_DOS_EXE            0x00    /* 00000000 */
#define SCAN_DOS_ALL            0x01    /* 00000001 */
#define SCAN_MAC_EXE            0x02    /* 00000010 */
#define SCAN_MAC_ALL            0x03    /* 00000011 */
#define SCAN_OS2_EXE            0x04    /* 00000100 */
#define SCAN_OS2_ALL            0x05    /* 00000101 */


/* STATUS */
#define CRITICAL_ERROR			0x01
#define WARNING_ERROR			0x02
#define INFORMATIONAL_ERROR		0x04

#pragma	pack(1)

typedef struct _ROOT 
{
    char        ServerName[48];         /* server name                  */
    char        Directory[128];         /* Vol:directory                */
    char        loginID[48];            /* login name                   */
    char        Password[48];
} ROOT;


typedef struct SERVER_OTHERINFO_STRUCT
{
	long	installedState;
	char	userName[48];
}	SERVER_OTHERINFO;


#define     BUFFER_SIZE    48


typedef struct _PRINT_QUEUE 
{
    char        ServerName[BUFFER_SIZE];         /* server name                  */
    char        QueueName[BUFFER_SIZE];           /* printer queue name           */
    char        loginID[BUFFER_SIZE];            /* login name                   */
    char        Password[BUFFER_SIZE];
} PRINT_QUEUE;

#define         MAX_SCAN_FILE   64

typedef struct _SCAN_FILE 
{
    long        fileCount;              /* 0 = no incl/excl,1 see below */
    char        nameList[MAX_SCAN_FILE][256];
} SCAN_FILE;

typedef struct  _F_REQUEST 
{
    SCAN_FILE   file;
    char        movDir[128];  /* error log file */
    char        RenameExt[4];
    
    char        startYear;      /* Start Executing Time */
    char        startMonth;
    char        startDay;
    char        startHour;

    char        startMinute;
    char        enable;         // 0: Disable 1: Enable
    char        method;         /* When Virus detected */
    char        mode;

    char        repeatMonth;    /* Frequence */
    char        repeatDay;
    char        repeatHour;
    char        repeatMinute;
    
    char        reserved[8];
} F_REQUEST;

/* VIRUS_REQUEST Will be Written to v$req.wrk */

typedef struct  _VIRUS_REQUEST  /* define Virus Job Request Block */
{
    long			Signature;
    DWORD			wJobId;			/*	*/
	time_t			ExecTime;
    ROOT			source;
//    PRINT_QUEUE		printer;
    char            CompressPattern[20][4]; /* Executable File Pattern */
    char            reserved2[192-80];

    SCAN_FILE		file;
    char			movDir[128];  /* error log file */
    char			pattern[20][4]; /* Executable File Pattern */
    char			RenameExt[4];
    
    char			repeatMonth;    /* Frequence */
    char			repeatDay;
    char			repeatHour;
    char			repeatMinute;

    char			exeOnly;        /* Scan Executable File Only */
    char			method;         /* When Virus detected */
    char			mode;
    char			TraverseDirectory;  // 1: traverse  0: no 

    char			MacType;      	// 0: No 1: Application 2: Resource Fork
    char			fastScan;
    char			speedLevel;
    char			status;         /* Scan Hold/Ready/Active <0/1/2> */

    char			PrintSummary;   // 0: No print, 1: print
    char			compressFile;  
    char			migrateFile;
	char			DomainFlag;

    char			dirty;
    char            ScanCompressedFiles;    // 1 = Scan Compressed file, 0 = skip
    char            skipCDROM;              // 1 = Skip CD-ROM
    char            reserved3;       

    char			reserved[128-4];
} V_REQUEST;


typedef struct  _L_V_REQ		/* define Virus Job Request Block */
{
    ROOT    source;
    SCAN_FILE   file;
    char    errorLog[128];  /* error log file */
    char    movDir[128];
    char    pattern[20][4];	/* Executable File Pattern */
    char    RenameExt[4];
    
    char    exeOnly;        /* Scan Executable File Only */
    char    method;         /* When Virus detected */
    char    mode;
	char    TraverseDirectory;
    
    char    MacType;
    char    append;
	char	reserve[6];
} L_V_REQ;

typedef struct  _I_REQUEST		/* define Virus Job Request Block */
{
	ROOT    source;
    char    movDir[128];	/* error log file */
    char    pattern[20][4];	/* Executable File Pattern */
    char    RenameExt[4];
    
    char    exeOnly;        /* Scan Executable File Only */
    char    method;         /* When Virus detected */
    char    mode;
    char    speedLevel;

    char    reserved[8];
} I_REQUEST;

typedef struct  _R_REQUEST		/* define RealTime Virus Job Request Block */
{
    char    movDir[128];	/* error log file */
    char    pattern[20][4];	/* Executable File Pattern */
    char    RenameExt[4];
   
    char    exeOnly;        /* Scan Executable File Only */
    char    method;         /* When Virus detected */
    char    mode;
    char    MacType;

    char    FastScan;
    char    direction;
    char    compressFile;  
    char    migrateFile;

    char    snmp;
    char    reserved[7];
//        SCAN_FILE   file;
} R_REQUEST;


typedef struct _VIRUS_SCAN_REQ 
{
	char    SourceDirectory[128];
	char    ARCHostServer[48];
	char    ServerName[48];
	char    LoginID[48];
	char    Password[48];
	char    OperationOnVirus;
	char    ExeOnly;
	char	reserve[2];
} VIRUS_SCAN_REQ;

typedef struct _SCAN_RESULT 
{
	short     CompleteCode;
	short     NumberOfViruses;
	short     FilesInfected;
	short     FilesDeleted;
	short     FilesRenamed;
	short     FilesCured;
	short     FilesNCured;
	short     wReserve;
} SCAN_RESULT;


typedef struct _INOCULAN_VERSION 
{
	char    major,
			minor,
			virus_major,
			virus_minor;
	char	year,       // 1900 -
			month,		// 0 - 11
			day;		// 1 - 31
	char    reserved[121];
} INOCULAN_VERSION;

typedef struct _DOMAIN_MEMBER 
{
    DWORD   dwNetFlags;         // NT
    DWORD   dwOSVersion;
    DWORD   dwInocuLANVersion;
    DWORD   dwEngineVersion;
    DWORD   dwEngineDate;
    DWORD   dwSigntureVersion;
    DWORD   dwSigntureDate;
    DWORD   dwMachineStatus;

    char    member[48];
    char    SerialNumber[8];

    char    flag;
    char    reserved[128- 32 - 48 - 8 - 1];
} DOMAIN_MEMBER;

typedef struct _DOMAIN_SERVER 
{
	char    domain[16];
//	char    domain[12];
	char    master[48];
	char    creater[48];
	char    c_year;
	char    c_month;
	char    c_day;
	char    c_hour;
	char    c_minute;
	char    c_second;
	char    m_year;
	char    m_month;
	char    m_day;
	char    m_hour;
	char    m_minute;
	char    m_second;
	char    reserved[128-48-16-48-6-6];
} DOMAIN_SERVER;

#define MAX_FILE_RECORD_NUMBER	1000
#define MAX_RECORD_NUMBER		100
#define RECORD_SIZE				256

typedef struct _MASTER_RECORD		// Total 256 bytes
{
	DWORD	Index;
	DWORD   nvirus;
	
	WORD	wJobId;
	WORD	wReserve;				

	char    owner[32];
	char    source[128];
	char    ServerName[48];
	char    executionTime[8];
	char    reportfile[16];
	

	char    method;
	char    status;         // 0: complete, 1:canceled
	char    Used;
	char	reserved[9];
	
} MASTER_RECORD;

#define MAX_FILE_LOG_NUMBER		1000
#define MAX_LOG_NUMBER			200
#define LOG_RECORD_SIZE			16
typedef struct 
{
	DWORD StartPos;
	DWORD Status;
	DWORD Length;
	DWORD dwId;
} LOGINDEX;

typedef struct
{
	DWORD mNumberOfLog;
	DWORD mMaxRecord;
	char Reserve[8];
} LOGINDEX_HEADER;

typedef struct
{
	DWORD mNumberOfRecord;
	DWORD mMaxRecord;
	DWORD mNextRecord;
	char Reserve[244];
} RECORD_HEADER;


typedef struct
{
	DWORD	nVirus;					//   4
	DWORD	nFiles;					//   4
	DWORD	nKBytes;				//   4
	DWORD	nDirs;					//   4
	DWORD	nPercent;				//   4

	DWORD	nCureFiles;				//   4
	DWORD	nEffFiles;				//   4
	DWORD	nPurgeFiles;			//   4
	DWORD	nDelFiles;				//   4
	DWORD	nRenFiles;				//   4
	DWORD	nNCureFiles;			//   4
	DWORD	nMovFiles;				//   4
	DWORD	nMoveRenameFiles;		//   4
	DWORD	TotalKBytes;			//   4

	WORD	wJobId;					//   2 
	WORD	wReserve;				//   2

	unsigned char	Owner[48];	 			//  48
	unsigned char	SourceDir[256];			// 256
	unsigned char	CurrentFileName[256];	// 256
	unsigned char	VirusName[128];			// 128

	char			TraverseDirectory;		//   1			1: traverse  0: no 
									//Total	   749
	unsigned char	Reserve[1024-749];		// 275
} REALTIME_SCANINFO;


typedef struct
{
	WORD wJobId;
	WORD wReserve;

	char Directory[220];
	char loginID[17];						//
	char method;							//   1
	char Reserve[14];
} INOCULAN_JOBINFO;

#pragma	pack()
#endif		//__STRUCT_H
