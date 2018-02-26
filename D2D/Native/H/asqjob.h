/**************************************************************************
This program is an unpublished work fully protected by the United States
Copyright laws and is considered a trade secret belonging to Computer
Associates International, Inc.

Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.

 Program Name:  ARCserveIT Queue Job API Header
      Version:  Release 6.7, Rev 1.0
 Version Date:  October 1, 1998
**************************************************************************/
 
#ifndef _INC_ASQJOB
#define _INC_ASQJOB

#ifdef __cplusplus
extern "C" {
#endif

#include <bebenabled.h>


#define CSI_QJOB_TYPE         0x5050    // Cheyenne Queue Job Type

typedef struct tagSCRIPTINFO
{
  CHAR   szSignature[12];              // Signature should be "CSI_AS50\0"
  CHAR   szScriptName[44];             // Script name
  ULONG  ulTotalDataSize;              // Total size of data in script
  ULONG  ulSrcListOffset;              // File offset of Source Node List
  ULONG  ulDstListOffset;              // File offset of Dest Node List
  USHORT nJobType;                     // ARCserve Job Type
  USHORT nScriptType;                  // Script Type (used by UI)
  ULONG  ulRevisionDate;               // Rev. Date in Hex (e.g. 0x00102095 = 10/20/95)
  UCHAR  aucReserved[180];             // Reserved for future use
} SCRIPTINFO;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef SCRIPTINFO FAR  *PSCRIPTINFO;
typedef SCRIPTINFO NEAR *NPSCRIPTINFO;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef SCRIPTINFO FAR  *PSCRIPTINFO;
typedef SCRIPTINFO NEAR *NPSCRIPTINFO;
#else
typedef SCRIPTINFO *PSCRIPTINFO;
#endif

//
//  Filter Types
//
#define ASFT_VOLUME           1         // Volume (or Drive) filter
#define ASFT_FILEPATTERN      2         // File pattern filter
#define ASFT_DIRPATTERN       3         // Directory pattern filter
#define ASFT_TIMERANGE        4         // Time range filter
#define ASFT_ATTRIBUTE        5         // Attribute filter
#define ASFT_NDS              6         // NDS filter - New in Sniper
#define ASFT_SIZE             7         // Size filter - New in Python

//
//  Include/Exclude
//
#define QJIE_INCLUDE          0         // Include files or dirs in list
#define QJIE_EXCLUDE          1         // Exclude files or dirs in list


typedef struct tagASFILTER_VOLUME
{
  USHORT usFilterType;                  // See Filter Types
  USHORT nInclExcl;                     // See Include/Exclude
  ULONG  nItems;                        // Number of Incl/Excl items in list
  PTSZ   pszNameList;                   // Volumes in list ("C:\0D:\0E:")
  USHORT usDataSize;                    // Buffer size of pszNameList
  UCHAR  aucReserved[18];               // Reserved for padding to 32 bytes
} ASFILTER_VOLUME;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASFILTER_VOLUME FAR  *PASFILTER_VOLUME;
typedef ASFILTER_VOLUME NEAR *NPASFILTER_VOLUME;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASFILTER_VOLUME FAR  *PASFILTER_VOLUME;
typedef ASFILTER_VOLUME NEAR *NPASFILTER_VOLUME;
#else
typedef ASFILTER_VOLUME *PASFILTER_VOLUME;
#endif

typedef struct tagASFILTER_FILE
{
  USHORT usFilterType;                  // See Filter Types
  USHORT nInclExcl;                     // See Include/Exclude
  ULONG  nItems;                        // Number of Incl/Excl items in list
  PTSZ   pszNameList;                   // Names to match (wildcards allowed)
  USHORT usDataSize;                    // Buffer size of pszNameList
  UCHAR  aucReserved[18];               // Reserved for padding to 32 bytes
} ASFILTER_FILE;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASFILTER_FILE FAR  *PASFILTER_FILE;
typedef ASFILTER_FILE NEAR *NPASFILTER_FILE;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASFILTER_FILE FAR  *PASFILTER_FILE;
typedef ASFILTER_FILE NEAR *NPASFILTER_FILE;
#else
typedef ASFILTER_FILE *PASFILTER_FILE;
#endif

typedef struct tagASFILTER_NDS
{
  USHORT usFilterType;                  // See Filter Types
  USHORT nInclExcl;                     // See Include/Exclude
  ULONG  nItems;                        // Number of Incl/Excl items in list
  PSZ    pszNameList;                   // Names to match (wildcards allowed)
  USHORT usDataSize;                    // Buffer size of pszNameList
  UCHAR  aucReserved[18];               // Reserved for padding to 32 bytes
} ASFILTER_NDS;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASFILTER_NDS FAR  *PASFILTER_NDS;
typedef ASFILTER_NDS NEAR *NPASFILTER_NDS;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASFILTER_NDS FAR  *PASFILTER_NDS;
typedef ASFILTER_NDS NEAR *NPASFILTER_NDS;
#else
typedef ASFILTER_NDS *PASFILTER_NDS;
#endif

typedef struct tagASFILTER_DIR
{
  USHORT usFilterType;                  // See Filter Types
  USHORT nInclExcl;                     // See Include/Exclude
  ULONG  nItems;                        // Number of Incl/Excl items in list
  PTSZ   pszNameList;                   // Names to match (wildcards allowed)
  USHORT usDataSize;                    // Buffer size of pszNameList
  UCHAR  aucReserved[18];               // Reserved for padding to 32 bytes
} ASFILTER_DIR;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASFILTER_DIR FAR  *PASFILTER_DIR;
typedef ASFILTER_DIR NEAR *NPASFILTER_DIR;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASFILTER_DIR FAR  *PASFILTER_DIR;
typedef ASFILTER_DIR NEAR *NPASFILTER_DIR;
#else
typedef ASFILTER_DIR *PASFILTER_DIR;
#endif

//
//  ASFILTER_TIMERANGE File System Fields
//
#define ASTMOBJ_CREATETIME    0         // Creation Time
#define ASTMOBJ_MODIFYTIME    1         // Modify Time
#define ASTMOBJ_LASTACCESS    2         // Last Access Time

//
//  ASFILTER_TIMERANGE Time Range Types
//
#define ASTMRT_BEFORE         0         // Check before start time
#define ASTMRT_ONORBEFORE     0         // Check On or before start time
#define ASTMRT_AFTER          1         // Check On or after start time
#define ASTMRT_ONORAFTER      1         // Check before start time
#define ASTMRT_BETWEEN        2         // Check between start and end times
#define ASTMRT_WITHIN         3         // Check within n Days/Months/Years
#define ASTMRT_BEFOREXDAYS    4         // Check before n Days/Months/Years
#define ASTMRT_AFTERXDAYS     5         // Check after n Days/Months/Years

// Document Level Agent
#define ASTMRT_BEFORE_X_DAYS_AGO	4
#define ASTMRT_AFTER_X_DAYS_AGO		5

//
//  ASTMRT_WITHIN Time Units
//
#define ASTMRU_DAYS           0         // Time unit is in Days
#define ASTMRU_MONTHS         1         // Time unit is in Months
#define ASTMRU_YEARS          2         // Time unit is in Years

// define for time zone (nPadding1)
//#define LOCAL_TIME             0x0000   // Local Time // default // Don't care
#define BMT_TIME               0xabcd   // BMT Time


typedef	struct tagIORetry
{
	ULONG			dwRetryInterval_Mins;			//Delay between retires.
	ULONG			dwOriginalSchedule;				//Used to store the original time the job was scheduled to run. This is restored when the number of retries has been done.
	ULONG			dwFlags;						//Various option flags
	unsigned char 	cFreezeFailAction;
	unsigned char	cNumberOfRetries;				//Number of times the job should be retried before giving up.
	unsigned char	cRetryCount;					//Number of times its has been retried.
	unsigned char	packing1;						//Ensure aligned on 4 byte boundry
	unsigned short	cOriginalScheduleType;			//AS the schedular re-schedules after we do, but only
													//if it not a run once, then we set it to a run once, once
													//finished retring we it back
	unsigned char	packing[2];						//Ensure aligned on 4 byte boundry
}IORetry, *PIORetry;	//(20 bytes)

//IOFreezeFailActions
#define 	FAIL_BACKUP					0
#define 	CONTINUE_BACKUP				1
#define		RETRY_BACKUP				2

//Flags for IORetry
#define	USE_LOCAL_SETTINGS				0x00000001
#define	IS_IMAGE_ENABLED				0x00000002
#define	CHECK_FOR_INCONSISTENCIES		0x00000004
#define	STOP_IF_INCONSISTENT			0x00000008
#define	PROMPT_IF_FILES_OPEN			0x00000010
#define	ENABLE_FILE_RESTORE				0x00000020
#define	ADD_FILE_RECS_TO_DB				0x00000040

// Define these for drive types.
#define	FSTYPE_NTFS    1
#define FSTYPE_FAT32   2
#define FSTYPE_FAT     3

typedef struct tagASFILTER_TIMERANGE
{
  USHORT usFilterType;                  // See Filter Types
  USHORT nInclExcl;                     // See Include/Exclude
  USHORT nFSFields;                     // See File System Fields
  USHORT nTRType;                       // See Time Range Types
  ULONG  ulStartTime;                   // Time range start time
  ULONG  ulEndTime;                     // Time range end time
  USHORT nTimeUnit;                     // See ASTMRT_WITHIN Time Units
  USHORT nPadding1;                     // Used as time zone between ARCserve and UNIX agent
                                        // to support rotation differential/incremental job.
                                        // The value should be 0 in regular time filter
  UCHAR  aucReserved[12];               // Reserved for padding to 32 bytes
} ASFILTER_TIMERANGE;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASFILTER_TIMERANGE FAR  *PASFILTER_TIMERANGE;
typedef ASFILTER_TIMERANGE NEAR *NPASFILTER_TIMERANGE;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASFILTER_TIMERANGE FAR  *PASFILTER_TIMERANGE;
typedef ASFILTER_TIMERANGE NEAR *NPASFILTER_TIMERANGE;
#else
typedef ASFILTER_TIMERANGE *PASFILTER_TIMERANGE;
#endif

//
//   Filter Attributes for files
//
#define ASFATT_MIGRATED        0x00400000 // Migrated attr
#define ASFATT_COMPRESSED      0x04000000 // Compressed Attr
/*
** Some other attributes for files are taken from DOS : <dos.h>
**
** These are  _A_NORMAL, _A_RDONLY,_A_HIDDEN,_A_SYSTEM,_A_SUBDIR etc..
*/

typedef struct tagASFILTER_ATTRIBUTE
{
  USHORT usFilterType;                  // See Filter Types
  USHORT nInclExcl;                     // See Include/Exclude
  FLAG32 fAttribute;                    // 32 bit Attribute
  UCHAR  aucReserved[24];               // Reserved for padding to 32 bytes
} ASFILTER_ATTRIBUTE;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASFILTER_ATTRIBUTE FAR  *PASFILTER_ATTRIBUTE;
typedef ASFILTER_ATTRIBUTE NEAR *NPASFILTER_ATTRIBUTE;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASFILTER_ATTRIBUTE FAR  *PASFILTER_ATTRIBUTE;
typedef ASFILTER_ATTRIBUTE NEAR *NPASFILTER_ATTRIBUTE;
#else
typedef ASFILTER_ATTRIBUTE *PASFILTER_ATTRIBUTE;
#endif

//
//  ASFILTER_SIZE Types
//
#define ASFST_EQ            0         // Check if file == size
#define ASFST_LT         	1         // Check if file < size
#define ASFST_GT            2         // Check if file > size
#define ASFST_IN         	3         // Check if size < file < size2

//
// size multiple types:
//
#define ASFSM_B		0	// use multiples of bytes
#define ASFSM_KB	1	// use multiples of kilobytes
#define ASFSM_MB	2	// use multiples of megabytes
#define ASFSM_GB	3	// use multiples of gigabytes

typedef struct tagASFILTER_SIZE
{
  USHORT usFilterType;                  // See Filter Types
  USHORT nInclExcl;                     // See Include/Exclude
  USHORT nSizeType;                     // See Size Types
  UCHAR	 nMultipleType;         		// See multiple types
  UCHAR	 nMultipleType2;         		// See multiple types
  ULONG  ulLowSize;                     // Low size
  ULONG  ulHighSize;                    // High size
  ULONG  ulLowSize2;                    // Low size #2
  ULONG  ulHighSize2;                   // High size #2
  UCHAR  aucReserved[8];                // Reserved for padding to 32 bytes
} ASFILTER_SIZE;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASFILTER_SIZE FAR  *PASFILTER_SIZE;
typedef ASFILTER_SIZE NEAR *NPASFILTER_SIZE;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASFILTER_SIZE FAR  *PASFILTER_SIZE;
typedef ASFILTER_SIZE NEAR *NPASFILTER_SIZE;
#else
typedef ASFILTER_SIZE *PASFILTER_SIZE;
#endif

//
//  Device File System
//
#define QJDFS_UNKNOWN         0         // The file system is unknown
#define QJDFS_UNIX            0         // Generic UNIX file system
#define QJDFS_FAT             1         // The file system is FAT
#define QJDFS_HPFS            2         // The file system is HPFS
#define QJDFS_NTFS            3         // The file system is NTFS
#define QJDFS_MAC             4         // The file system is MAC
#define QJDFS_FTAM            5         // The file system is FTAM
#define QJDFS_NW2X            6         // The file system is NetWare 2.x
#define QJDFS_NW3X            7         // The file system is NetWare 2.x
#define QJDFS_NW4X            8         // The file system is NetWare 4.x
#define QJDFS_DSAGENT         9         // This is DBagent
#define QJDFS_NTIMAGE         10        // This is NT Image
#define QJDFS_REGISTRY        11        // This is NT Registry
#define QJDFS_OPTICALDISK     12        // This is JukeBox
#define QJDFS_MBO             13        // MBO
#define QJDFS_NOTES           14        // Lotus Notes
#define QJDFS_STACKFS         15        // PLC Stack FS
#define QJDFS_NDS             16        // NetWare Directory Service
#define QJDFS_UNIXIMAGE       17        // UNIX File System Image
#define QJDFS_UNIXRAW         18        // UNIX Raw Device
#define QJDFS_OPENINGRES      19        // UNIX CA/OpenIngres DB object
#define QJDFS_SAP             20        // UNIX SAP DB object
#define QJDFS_SYBASENATIVE    21        // UNIX Native Sybase DB object
#define QJDFS_ORACLE8         22        // UNIX Oracle 8
#define QJDFS_NFS             23        // UNIX NFS - added 11/14/1997
#define QJDFS_ASMAGENT        24        // UNIX CA's ASM backup
#define QJDFS_AS400           25        // AS/400 - added 16-NOV-1998
#define QJDFS_VMS             26        // VMS - added 16-NOV-1998
#define QJDFS_SYSTEM_STATE    27        // Windows 2000 System State (JCL 19990604)
#define QJDFS_W2K_IMAGE			28        // Windows 2000  Image

// These defines were moved +20 due to a conflict (JCL 980930)
#define QJDFS_SYBASE          30        // UNIX Sybase DB object
#define QJDFS_ORACLE          31        // UNIX Oracle DB object
#define QJDFS_INFORMIX        32        // UNIX Informix DB object
#define QJDFS_WEBPAGE         33        // WWW pages
#define QJDFS_NTNAS			  34        // NAS / NDMP
#define QJDFS_SYM			  35        // SYM
#define QJDFS_WRITER		  36        // Volume shadow copy writer

//#define QJDFS_EXSIS           37		// Exchange push agent in conflict with unix DB2 = 37
#define QJDFS_DB2				37		// golda12 from Unix 12/2003
#define QJDFS_SAMBA				38		// golda12 from Unix 12/2003
#define QJDFS_BMB_DATA			39
//#define QJDFS_RADR			40
#define QJDFS_MYSQL				40		// golda12 from Unix 12/2003
#define QJDFS_APACHE			41		// golda12 from Unix 12/2003
#define QJDFS_EXSIS				42		// Exchange Document Level push agent
#define QJDFS_RADR				43
//begin BEB_SQL_XPLATFORM
#define QJDFS_MSSQL				44		// MS SQL Server push agent 12/2003
//end BEB_SQL_XPLATFORM
#define QJDFS_EXDB				45		// Exchange Database Level push agent 12/2003

#ifdef NEW_ORACLE
#define QJDFS_ORACLERMAN		46		// Oracle RMAN
#endif

#ifdef BEB_AGENT_SHAREPOINT
#define QJDFS_SHAREPOINT		47		// SharePoint Agent
#define QJDFS_SHAREPOINT_WSS    48
#define QJDFS_SHAREPOINT_SSO    49
#define QJDFS_SHAREPOINT_SI     50
#define QJDFS_SHAREPOINT_DB     51
#define QJNT_ONSTORNODE         52
#endif // BEB_AGENT_SHAREPOINT

#define QJDFS_OES_TSAFS         53		// OES agent

#define QJDFS_EXDBVSS			54		// wu$br01,June 8,2006, E12 agent, Oriole

#ifdef ORA_RMAN_SUPPORT
#define QJDFS_ORA_RMAN		46
#endif

//#ifdef WANSYNC_SUPPORT
#define QJDFS_WANSYNC_ENTITY	55		// Wansync scenario entity
//#endif
#define QJDFS_ASDB_MSSQLEXP     56

//#ifdef VMWARE_SUPPORT
#define QJDFS_VMWARE			57		// VMWare volume
//#endif

#ifdef R12V_VM_TASKS
#define QJDFS_HYPERV			58		// Microsoft Hyper V
#endif// R12V_VM_TASKS

// Add by Gil 2006/12/14
// From 60 to 140 are reserved for SharePoint 2007 usage
//#ifdef SPS_2007_CHANGES
#define QJDFS_SHAREPOINT_2007_BEGIN					60
#define QJDFS_SHAREPOINT_2007_AGENT					60		// SharePoint 2007 Farm
// the first level
#define QJDFS_SHAREPOINT_2007_FARM					61		// SharePoint 2007 Farm

// the second level
#define QJDFS_SHAREPOINT_2007_CONFIG_DB				71		// SharePoint 2007 Contigure DB
#define QJDFS_SHAREPOINT_2007_WEB_APPS				72		// SharePoint 2007 Web Applications
#define QJDFS_SHAREPOINT_2007_WSS_ADMIN				73		// SharePoint 2007 WSS Administration
#define QJDFS_SHAREPOINT_2007_SSP					74		// SharePoint 2007 Shared Service Provider
#define QJDFS_SHAREPOINT_2007_GLOBAL_SETTING		75		// SharePoint 2007 Global Setting
#define QJDFS_SHAREPOINT_2007_SSO					76		// SharePoint 2007 Single Sige On
#define QJDFS_SHAREPOINT_2007_WSS_SEARCH_SERVICES	77		// SharePoint 2007 WSS Search Services

// the third level
#define QJDFS_SHAREPOINT_2007_NORMAL_WEB_APP		90		// SharePoint 2007 Normal Web Application
#define QJDFS_SHAREPOINT_2007_WSS_ADMIN_WEB_APP		91		// SharePoint 2007 WSS Administration Web Application
#define QJDFS_SHAREPOINT_2007_SSP_ADMIN_WEB_APP		92		// SharePoint 2007 SSP Administration Web Application
#define QJDFS_SHAREPOINT_2007_SSP_DB				93		// SharePoint 2007 SSP Database
#define QJDFS_SHAREPOINT_2007_SSP_USER_PROFILE		94		// SharePoint 2007 SSP User Profile
#define QJDFS_SHAREPOINT_2007_SSP_SESSION_STAT		95		// SharePoint 2007 SSP Session Stat
#define QJDFS_SHAREPOINT_2007_SSP_SEARCH_INDEX		96		// SharePoint 2007 Search Index
#define QJDFS_SHAREPOINT_2007_SSO_DB				97		// SharePoint 2007 SSO Database
#define QJDFS_SHAREPOINT_2007_SSO_CONFIGURATION		98		// SharePoint 2007 SSO Configuration
#define QJDFS_SHAREPOINT_2007_WSS_SEARCH_INSTANCE	99		// SharePoint 2007 WSS Search Instance
#define QJDFS_SHAREPOINT_2007_PROJECT_SERVER		100		// SharePoint 2007 Project Server

// the fourth level
#define QJDFS_SHAREPOINT_2007_NORMAL_WEB_APP_CONTENT_DB		110		// SharePoint 2007 Normal Web Application Database
#define QJDFS_SHAREPOINT_2007_WSS_ADMIN_WEB_APP_CONTENT_DB	111		// SharePoint 2007 WSS Administration Web Application Database
#define QJDFS_SHAREPOINT_2007_SSP_ADMIN_WEB_APP_CONTENT_DB	112		// SharePoint 2007 SSP Administration Web Application Database
#define QJDFS_SHAREPOINT_2007_SSP_SEARCH_INDEX_DB			113		// SharePoint 2007 SSP Search Index Database
#define QJDFS_SHAREPOINT_2007_WSS_SEARCH_INSTANCE_DB		114		// SharePoint 2007 WSS Search Instance Database

// wanwe13_SPSO14 Begin
// the second level
#define QJDFS_SHAREPOINT_2010_FORM_SERVICE_BAK			  62	// SharePoint 2010 Forms Service Backup
#define QJDFS_SHAREPOINT_2010_SECURE_STORE_SERVICE		  63	// SharePoint 2010 Secure Store Service
#define QJDFS_SHAREPOINT_2010_MOSS_CONVER_SERVICE		  64	// SharePoint 2010 Word Viewing Service
#define QJDFS_SHAREPOINT_2010_VISIO_GRAPHIC_SERVICE		  65	// SharePoint 2010 Visio Graphics Service
#define QJDFS_SHAREPOINT_2010_METADATA_WEB_SERVICE		  66	// SharePoint 2010 Metadata Web Service
#define QJDFS_SHAREPOINT_2010_WEB_ANALY_WEB_SERVICE       67	// SharePoint 2010 Web Analytic Web Service
#define QJDFS_SHAREPOINT_2010_METADATA_WEB_SERVICE_PROXY  68	// SharePoint 2010 Metadata Web Service Proxy
#define QJDFS_SHAREPOINT_2010_WORD_SERVICE				  69	// SharePoint 2010 Word Service
#define QJDFS_SHAREPOINT_2010_BCD_SERVICE				  70	// SharePoint 2010 Business Data Catalog Service
#define QJDFS_SHAREPOINT_2010_VISIO_GRAPHIC_SERVICE_PROXY 78	// SharePoint 2010 Visio Graphics Service Proxy
#define QJDFS_SHAREPOINT_2010_SP_DIAGNOSTICS_SERVICE	  79	// SharePoint 2010 Diagnostics Service

// the third level
#define QJDFS_SHAREPOINT_2010_FORM_SERVICE					  80	// SharePoint 2010 Forms Service
#define QJDFS_SHAREPOINT_2010_DATA_CONN_FILE_COLLECTION		  81	// SharePoint 2010 Data Connection File Collection
#define QJDFS_SHAREPOINT_2010_FORM_TEMPLATE_COLLECTION		  82	// SharePoint 2010 Form Template Collection
#define QJDFS_SHAREPOINT_2010_EXEMPT_UA_COLLECTION			  83	// SharePoint 2010 Exempt User Agent Collection
#define QJDFS_SHAREPOINT_2010_SECURE_STORE_SERVICE_APP		  84	// SharePoint 2010 Secure Store Service Application
#define QJDFS_SHAREPOINT_2010_SP_USAGE_SETTING				  85	// SharePoint 2010 Usage Settings
#define QJDFS_SHAREPOINT_2010_CONVER_SERVICE_APP			  86	// SharePoint 2010 Conversion Service Application
#define QJDFS_SHAREPOINT_2010_VISIO_GRAPHIC_SERVICE_APP		  87	// SharePoint 2010 Visio Graphics Service Application
#define QJDFS_SHAREPOINT_2010_METADATA_WEB_SERVICE_APP		  88	// SharePoint 2010 Metadata Web Service Application
#define QJDFS_SHAREPOINT_2010_WEB_ANALY_SERVICE_APP  		  89	// SharePoint 2010 Web Analytics Service Application
#define QJDFS_SHAREPOINT_2010_WEB_ANALY_SERVICE_APP_PROXY	  101   // SharePoint 2010 Metadata Web Service Application Proxy
#define QJDFS_SHAREPOINT_2010_SEARCH_SERVICE_APP			  102	// SharePoint 2010 Search Service Application
#define QJDFS_SHAREPOINT_2010_WORD_SERVICE_APP				  103	// SharePoint 2010 Word Service Application
#define QJDFS_SHAREPOINT_2010_BDC_SERVICE_APP			      104	// SharePoint 2010 Business Data Catalog Application 
#define QJDFS_SHAREPOINT_2010_VISIO_GRAPHIC_SERVICE_APP_PROXY 105	// SharePoint 2010 Visio Graphics Service Application Proxy

// the fourth level
#define QJDFS_SHAREPOINT_2010_EXEMPT_USER_AGENT					  106	// SharePoint 2010 Exempt User Agent
#define QJDFS_SHAREPOINT_2010_SECURE_STORE_SERVICE_DB			  107	// SharePoint 2010 Secure Store Service Database
#define QJDFS_SHAREPOINT_2010_SP_WORKFLOW_FAILOVER_JOBDEF		  108	// SharePoint 2010 SP Workflow FailOver Job Definition
#define QJDFS_SHAREPOINT_2010_UNPUBLISH_JOBDEF					  109   // SharePoint 2010 Internal Unpublish Job Definition
#define QJDFS_SHAREPOINT_2010_SP_SOL_DAILY_RES_USAGE_JOBDEF		  115	// SharePoint 2010 SP Solution Daily Resource Usage Job Definition
#define QJDFS_SHAREPOINT_2010_SP_AUDIT_LOG_TRIMMING_JOBDEF		  116	// SharePoint 2010 SP Audit Log Trimming Job Definition
#define QJDFS_SHAREPOINT_2010_PROGOGATE_VARIATION_PAGE_JOBDEF     117	// SharePoint 2010 Progogate Variation Page Job Definition
#define QJDFS_SHAREPOINT_2010_SP_CHANGE_LOG_JOBDEF				  118	// SharePoint 2010 SP Change Log Job Definition
#define QJDFS_SHAREPOINT_2010_SP_DEAD_SITE_DEL_JOBDEF			  119	// SharePoint 2010 SP Dead Site Delete Job Definition
#define QJDFS_SHAREPOINT_2010_SP_IMMEDIATE_ALERT_JOBDEF			  120	// SharePoint 2010 SP Immediate Alerts Job Definition
#define QJDFS_SHAREPOINT_2010_SP_SOL_RES_USAGE_UPDATE_JOBDEF      121	// SharePoint 2010 SP Solution Resource Usage Update Job Definition
#define QJDFS_SHAREPOINT_2010_SP_RECYCLEBIN_CLEANUP_JOBDEF        122	// SharePoint 2010 SP RecycleBin Cleanup Job Definition
#define QJDFS_SHAREPOINT_2010_SP_SITE_INVENTORY_COLLECTION_JOBDEF 123	// SharePoint 2010 SP Site Inventory Collection Job Definition
#define QJDFS_SHAREPOINT_2010_SP_USAGE_ANALY_JOBDEF				  124	// SharePoint 2010 SP Usage Analysis Job Definition
#define QJDFS_SHAREPOINT_2010_SP_DISK_QUOTA_WARNING_JOBDEF		  125	// SharePoint 2010 SP Disk Quota Warning Job Definition
#define QJDFS_SHAREPOINT_2010_SP_SOL_RES_USAGE_LOG_JOBDEF		  126	// SharePoint 2010 SP Solution Resource Usage Log JobDefinition
#define QJDFS_SHAREPOINT_2010_SP_WF_AUTO_CLEAN_JOBDEF			  127	// SharePoint 2010 SP Workflow AutoClean Job Definition
#define QJDFS_SHAREPOINT_2010_METADATA_WEB_SERVICE_DB			  128	// SharePoint 2010 Metadata Web Service Database
#define QJDFS_SHAREPOINT_2010_WEB_ANALY_STAGER_DB				  129	// SharePoint 2010 Web Analytics Stager Database
#define QJDFS_SHAREPOINT_2010_WEB_ANALY_WAREHOURSE_DB			  130	// SharePoint 2010 Web Analytics Warehouse Database
#define QJDFS_SHAREPOINT_2010_SEARCH_ADMIN_DB					  131	// SharePoint 2010 Search Admin Database
#define QJDFS_SHAREPOINT_2010_SEARCH_PROPERTY_STORE_DB			  132	// SharePoint 2010 Search Property Store Database
#define QJDFS_SHAREPOINT_2010_SEARCH_GATHERER_DB				  133	// SharePoint 2010 Search Gatherer Database
#define QJDFS_SHAREPOINT_2010_QUEUE_DB							  134	// SharePoint 2010 Word Server Service Queue Database
#define QJDFS_SHAREPOINT_2010_BDC_SERVICE_DB					  135	// SharePoint 2010 Business Data Catalog Database

// the five level
#define QJDFS_SHAREPOINT_2010_ADMIN_COMPONENT					  136	// SharePoint 2010 Admin Component

// the first level
#define QJDFS_SHAREPOINT_2010_AGENT								  137    // SharePoint 2010 Farm
#define QJDFS_SHAREPOINT_2010_FARM								  138	 // SharePoint 2010 Farm
// wanwe13_SPSO14 End

// the fifth level
#ifdef  SHAREPOINT_2007_DOCLEVEL 
#define QJDFS_SHAREPOINT_2007_NORMAL_SITE					140

// the sixth level
#define	QJDFS_SHAREPOINT_2007_NORMAL_WEB					141

// the seventh level
#define	QJDFS_SHAREPOINT_2007_NORMAL_LIST					142
#define QJDFS_SHAREPOINT_2007_DOCUMENTLIBRARY				143
#define QJDFS_SHAREPOINT_2007_ANNOUNCEMENT					144

// the EIGHTH level 
#define QJDFS_SHAREPOINT_2007_FILE							145
#define QJDFS_SHAREPOINT_2007_FOLDER						146

// THE nineth LEVEL
#define QJDFS_SHAREPOINT_2007_VERSION						147


#endif

#define QJDFS_SHAREPOINT_2007_END							160
//#endif //SPS_2007_CHANGES
// End add by Gil


#define QJDFS_WS_PHYSICALDISK                               161 //for physical disk
#define QJDFS_WS_PHYSICALVOLUME                             162 //for physical volume
#ifdef E14_TASK
#define QJDFS_EDBVSS										163	// <XUVNE01> Exchange E14 DB level agent (VSS). 2009-03-16
#endif	//E14_TASK

// wanwe13_SPSO14 Begin
#define ASDDO_BR_OPTION_SPSO14_CONFIGONLY		  0x00000100 // SharePoint2010 Backup/restore Option Configuration only
#define ASDDO_BR_OPTION_SPSO14_CONFIGANDCONTENT   0x00000200 // SharePoint2010 Backup/restore Option Configuration and Content
// wanwe13_SPSO14 end
// Special flags for RADR, Kevin Lin 6/16/03
#define VOLUME_DEPENDANT             0x80000000 //currently only DR
#define ADV_DR_DEPENDENCY            0x00000001 //DR needs: boot volume, system volume and system



#define QJDFS_DBROOT          255       // UDB_OBJECT_ROOT - 04/14/1998 James
// these are object property stored in options of asdisk for an Oracle db on unix
//object Since there are no disk options using the high bits, these new flags have been
// defined.

#define ASDDO_OPTION_ORACLE_PROP_NEED_SECURITY  0x10000000 // object needs security
#define ASDDO_OPTION_ORACLE_PROP_BROWSEABLE_CONTAINER 0x01000000 // object is a container

//
//  ASDISKITEM Options
//
#define ASDIO_TRAVERSEDIR               0x00000001  // Traverse directories

//
// The following options are used for DSABeginBackup, DSABeginBackupStripe
// trunc_option settings
//
#define ASDIO_OPTION_LOCAL              0x00000002  // backup/restore is local

// CUI
#define ASDIO_BINDERY_OBJECT            0x00000002  // back bindery object in NetWare

#define ASDIO_OPTION_REMOTE             0x00000004  // backup/restore is remote
#define ASDIO_OPTION_DATABASE           0x00000008  // backup/restore database
#define ASDIO_OPTION_WITH_TRUNCATE      0x00000010  // backup/restore log w. truncate
#define ASDIO_OPTION_NO_TRUNCATE        0x00000020  // backup/restore log w.o. truncate
#define ASDIO_OPTION_FULL               0x00000040  // backup/restore database & log w. truncate
#define ASDIO_OPTION_COPY               0x00000080  // backup/restore database and log w.o. truncate
#define ASDIO_OPTION_TABLE              0x00000100  // backup/restore database table
#define ASDIO_OPTION_ERASE              0x00000200  // restore       database and erase existing files
#define ASDIO_OPTION_PUB                0x00000400  // restore public database (in Exchange)
#define ASDIO_OPTION_PRIV               0x00000800  // restore private       database (in Exchange)
#define ASDIO_OPTION_START_SERVICE      0x00001000  // restore and start the service
#define ASDIO_OPTION_VERIFY             0x00002000  // verify after restore
#define ASDIO_OPTION_CHECKSUM           0x00004000  // generate checksum during backup
#define ASDIO_OPTION_INCREMENTAL        0x00008000  // backup note database incrementally

#define ASDIO_VERYFULL                  0x00010000  // Very Full Option

#define DSA_OPTION_LOCAL_NOTES          0x00020000  // for notes
//#define ASDIO_OPTION_TIMEBASED        0x00040000  // backup note database incrementally

#define ASDIO_OPTION_PURGE_MESSAGES     0x00080000  // Exch Doc level backup option, Purge messages after backup.

#define ASDIO_OPTION_NEVER_OVERWRITE    0x00080000  // for
#define ASDIO_OPTION_OVERWRITE_CHANGED  0x00100000  // for
#define ASDIO_OPTION_ALWAYS_OVERWRITE   0x00200000  // for
#define ASDIO_OPTION_NEVER_OVERWRITE_CHANGED    0x00400000  // for
#define ASDIO_OPTION_JOB_DBABATCH       0x00800000  // For Informix, Job coming from DBABATCH
#define ASDIO_OPTION_BACKUP_MESSAGES    0x00800000  // for ???
#define ASDIO_OPTION_LOG                0x01000000  // For Informix, Physical Restore.
#define ASDIO_OPTION_LOG_NUMBER_BASED   0x02000000  // for Informix, Last Logical Log X Restore.
#define ASDIO_OPTION_DIFFERENTIAL       0x04000000  // For Informix, Level-1 Backup.
#define ASDIO_OPTION_TIMEBASED          0x08000000  // backup note database incrementally // value changed

#define ASDIO_OPTION_USE_GLOBAL         0x10000000  // Use global packaged method.

#define ASDIO_DBA_USE_EXTENDED          0x80000000  // flag for front-end UI to tell if extended attr is used

//<XUVNE01> 2008-01-29
//BOOL isASDiskItemUnicode(PASDISKITEM PAsdiskitem)
#define ASDIO_OPTION_UNICODE			(0x01)
#define ASDIO_OPTION_PSEUDO_UNICODE		(0x02)
#define isASDiskItemUnicode(PAsdiskitem)		((PAsdiskitem)?(PAsdiskitem->ucUnicodeFlags & ASDIO_OPTION_UNICODE):FALSE)
#define setASDiskItemUnicode(PAsdiskitem)		(PAsdiskitem->ucUnicodeFlags |= ASDIO_OPTION_UNICODE)
#define isASDiskItemPseudoUnicode(PAsdiskitem)	((PAsdiskitem)?(PAsdiskitem->ucUnicodeFlags & ASDIO_OPTION_PSEUDO_UNICODE):FALSE)
#define setASDiskItemPseudoUnicode(PAsdiskitem)	(PAsdiskitem->ucUnicodeFlags |= ASDIO_OPTION_PSEUDO_UNICODE)

// daist01 3/19/2008
#define clearASDiskItemUnicode(pAsdiskitem)	do{	\
	if ((pAsdiskitem) != NULL)	(pAsdiskitem)->ucUnicodeFlags &= ~ASDIO_OPTION_UNICODE;	\
}while(0)

#define clearASDiskItemPseudoUnicode(pAsdiskitem) do{\
	if ((pAsdiskitem) != NULL)	(pAsdiskitem)->ucUnicodeFlags &= ~ASDIO_OPTION_PSEUDO_UNICODE;	\
}while(0)
#define ASNO_OPTIONS_PASNODEX_DAG		0x00000001//For modify node type-DAG
#define ASNO_OPTIONS_PASNODEX_STANDLONE	0x00000002//For modify node type-Standlone

#define ASDIO_OPTIONEX_INBOX            0x00000001  // Individual Mail Boxes\Mail Box\Inbox\...
                                                    // Individual Mail Boxes\Mail Box\
                                                    // Individual Mail Boxes
#define ASDIO_OPTIONEX_OUTBOX           0x00000002  // Individual Mail Boxes\Mail Box\Outbox\...
#define ASDIO_OPTIONEX_DELETED          0x00000004  // Individual Mail Boxes\Mail Box\Deleted\...
#define ASDIO_OPTIONEX_SENT             0x00000008  // Individual Mail Boxes\Mail Box\Sent\...
#define ASDIO_OPTIONEX_INDMB_FOLDER     0x00000010  // Individual Mail Boxes\Mail Box\Folder\...
#define ASDIO_OPTIONEX_PUBLIC_FOLDER    0x00000020  // Public Folder\...

#define ASDIO_OPTIONEX_STORAGEGROUP     0x00000040  // Storage Group\...
#define ASDIO_OPTIONEX_MBSDB			0x00000080	// Storage Group\Mailbox Store

#define ASDIO_OPTIONEX_HARDWARE_SNAPSHOT    0x00001000  //this is the diskitem level option in the
#define ASDIO_B_RESYNC						0x00002000 // DBAgents Hardware snapshot Options
#define ASDIO_B_BCV_SPLIT					0x00004000 // DBAgents Hardware snapshot Options
#define ASDIO_B_BCV_COPY					0x00008000 // DBAgents Hardware snapshot Options
//Hardware Snapshot option

#define ASDIO_OPTIONEX_MAILBOX			0x00100000 // Storage Group\Mailbox Store\Mailbox
#define ASDIO_OPTIONEX_CALENDAR			0x00200000 // Storage Group\Mailbox Store\Mailbox\Calendar
#define ASDIO_OPTIONEX_CONTACTS			0x00400000
#define ASDIO_OPTIONEX_DRAFT			0x00800000
#define ASDIO_OPTIONEX_JOURNAL			0x01000000
#define ASDIO_OPTIONEX_NOTES			0x02000000
#define ASDIO_OPTIONEX_TASKS			0x04000000
#define ASDIO_OPTIONEX_COUNT			0x08000000


//SharePoint Option
#define ASDIO_OPTIONEX_WSS				0x00000100
#define ASDIO_OPTIONEX_SSO				0x00000200
#define ASDIO_OPTIONEX_SI				0x00000400
#define ASDIO_OPTIONEX_DBASE			0x00000800
#define ASDIO_OPTIONEX_PORTAL			0x00001000
#define ASDIO_OPTIONEX_INDEXES			0x00002000

#ifdef E12_TASK
#define ASDIO_OPTION_PREFER_REPLICA		0x01000000
#define ASDIO_OPTION_ONLY_REPLICA		0x02000000  //<XUVNE01> [17182703] 2008-10-31
#endif //E12_TASK

#ifdef E14_TASK //<XUVNE01> E14 support 2009-03-26
//ASDISKITEM::fOptions - Disk item level options for Exchange E14  begin
#define ASDIO_OPTION_EXCHDB_BACKUP_FULL								0x00000100		// Full backup
#define ASDIO_OPTION_EXCHDB_BACKUP_COPY								0x00000200		// Copy backup
#define ASDIO_OPTION_EXCHDB_BACKUP_INCR								0x00000400		// Incremental backup
#define ASDIO_OPTION_EXCHDB_BACKUP_DIFF								0x00000800		// differential backup

#define ASDIO_OPTION_EXCHDB_BACKUP_FROM_REPLICA						0x00001000		// Backup from replica
#define ASDIO_OPTION_EXCHDB_BACKUP_FROM_ACTIVE						0x00002000		// Backup from active database
#define ASDIO_OPTION_EXCHDB_BACKUP_USE_ACTIVE_IF_NO_REPLICA			0x00004000		// Backup from active database if there is not healthy replica available 
#define ASDIO_OPTION_EXCHDB_BACKUP_DAG_FIRST						0x01000000		// DAG first preferred
#define ASDIO_OPTION_EXCHDB_BACKUP_DAG_LAST							0x02000000		// DAG last preferred
#define ASDIO_OPTION_EXCHDB_BACKUP_DAG_CUSTOM						0x04000000		// Specified replica preference

#define ASDIO_OPTION_EXCHDB_BACKUP_USE_DEFAULT                      0x00100000      // Use default option specified on disk level. Takes effect in disk item level.

//Doc Level option flags
#define ASDIO_OPTION_EXCHDB_BACKUP_TIMEBASED						0x00010000		// time based backup			( ASDIO_OPTION_TIMEBASED for r12 )
#define ASDIO_OPTION_EXCHDB_BACKUP_PURGE_AFTER_BACKUP				0x00020000		// Purge messages after backup	( ASDIO_OPTION_PURGE_MESSAGES for r12)

//ASDISKITEM::fOptions - Disk item level options for Exchange E14  end
#endif//E14_TASK

#ifdef BAB_EXDB_SUPPORT // liuwe05 2005-11-15 to support Exchange agent DB-Level for r11.5 FP2
// Exchange DB Level Push Agent OptionEx
#define ASDIO_OPTIONEX_DBAEXDB_ISSG				0x00010000
#define ASDIO_OPTIONEX_DBAEXDB_SRSSG			0x00020000
#define ASDIO_OPTIONEX_DBAEXDB_KMSSG			0x00040000
#define ASDIO_OPTIONEX_DBAEXDB_MBSDB			0x00060000
#define ASDIO_OPTIONEX_DBAEXDB_PUBDB			0x00080000
#define ASDIO_OPTIONEX_DBAEXDB_SRSDB			0x00100000
#define ASDIO_OPTIONEX_DBAEXDB_KMSDB			0x00200000
#endif  // end ifdef BAB_EXDB_SUPPORT

typedef struct tagASDISKITEM
{
  PTSZ   pszPath;                       // Fully qualified path
  ULONG  nFiles;                        // # files in list
  PTSZ   pszFileList;                   // List of ASCIIZ Filenames
  FLAG32 fOptions;                      // See ASDISKITEM Options
  FLAG32 fOptionsEx;                    // See ASDISKITEM Options
  //UCHAR  aucReserved[12];               // Reserved for future use
//#ifdef MULTISTRIPE_SQL
  int nMultiStripeNum;
  PTSZ   pszVirtualPath;
#ifdef NODE_RPT_MASTER
  UCHAR  usItemStatus[1];
#else
  UCHAR  aucReservedForItemStatus[1];
#endif //NODE_RPT_MASTER
#ifdef UNICODE_JIS_SUPPORT
  UCHAR  ucUnicodeFlags;
#else // UNICODE_JIS_SUPPORT
  UCHAR  aucReservedForUnicodeFlags;
#endif // UNICODE_JIS_SUPPORT
  UCHAR  aucReserved[2];
//#else //MULTISTRIPE_SQL
//  UCHAR  aucReserved[12];
//#endif //MULTISTRIPE_SQL
} ASDISKITEM;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDISKITEM FAR  *PASDISKITEM;
typedef ASDISKITEM NEAR *NPASDISKITEM;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASDISKITEM FAR  *PASDISKITEM;
typedef ASDISKITEM NEAR *NPASDISKITEM;
#else
typedef ASDISKITEM *PASDISKITEM;
#endif

//
//  ASDDISK Options
//
#define ASDDO_SKIP              0x00000001  // Skip this disk device
#define ASDDO_IMAGE             0x00000004  // Enable image option for disk device
#define ASDDO_DRSAVCFG			0x00008000	// Enable multiprocessing slave job to perform DR operation.
//
//   Backup Specific ASDDISK Options
//
#define ASDDO_B_APGROOM         0x00000002 // Enable grooming
#define ASDDO_VERYFULL          0x00001000 // Very Full Option
#define ASDDO_COMPRESSION       0x00002000 // Enable compression
#define ASDDO_ENCRYPTION        0x00004000 // Enable encryption
//  for sp3 QJDTO_B_VERIFY_CRC Modified by J.K.
#define ASDDO_B_CREATE_CRC      0x00010000 // Create CRC during backup
#define ASDDO_B_COMPARE         0x00020000 // Compare files on tape to disk
#define ASDDO_B_VERIFY          0x00040000 // Scan tape to verify proper layout
#define ASDDO_B_VERIFY_CRC      (ASDDO_B_CREATE_CRC | ASDDO_B_VERIFY) // Verify tape contents with CRC

#define ASDDO_B_CHEYENNE        0x01000000 // For Uagent
#define ASDDO_B_TAR             0x02000000 // For Uagent
#define ASDDO_B_CPIO            0x04000000 // For Uagent
#define ASDDO_B_AGENT_SPLIT		0x00400000 // Agent split
#define ASDDO_B_REMOTE_VI		0x00800000 // Remote Protocol VI
#define ASDDO_B_REMOTE_NP		0x10000000 // Remote Protocol Named Pipes
#define ASDDO_GROUPWISE         0x00800000 // Disk is a GroupWise Resource, for NEtware only.

// Image options
#define ASDDO_INCSTCHECK        0x00000008 // Check for drive inconsistencies
#define ASDDO_INCSTSTOP         0x00000010 // Stop when drive inconsistencies detected
#define ASDDO_PROMPT            0x00000020 // Prompt if opened files on volume
#define ASDDO_SKIPFILE          0x00000040 // Skip file scanning
#define ASDDO_RECORDDB          0x00000080 // Enable database recording
//Keyan01 Added for image with retry
#define ASDDO_IMAGE03	        0x00000100 // Enable database recording

#define ASDDO_B_BINDOPT         0x00080000 // Bindery option for backup/restore
#define ASDDO_B_COMPARE_FIRSTNMB 0x02000000 // Compare first #MB of data
#define ASDDO_B_CRCCHECK        0x04000000 // Do CRC check for current node
#define ASDDO_B_NOVERIFY        0x08000000 // No verification of current node
#define ASDDO_B_CRC_NONE        0x00100000 // No CRC       If neither set do default (Global)

// SYBASE DataTools options
#define ASDDO_B_LOGICAL         0x00000100 // Logical backup
#define ASDDO_R_DRYRUN          0x00000200 // Logical dryrun restore
#define ASDDO_R_COPYOVER        0x00000400 // Logical copyover restore
#define ASDDO_R_MERGE           0x00000800 // Logical merge restore
#define ASDDO_R_DUMP            0x00000100 // Physical restore
#define ASDDO_R_DUMPSYBASE      0x00000200 // Physical Sybase dump restore

// ORACLE EBU options
#define ASDDO_B_ORACLEEBU_OFFLINE       0x00000100 // offline backup: for old and new oraclerman
#define ASDDO_B_ORACLEEBU_PURGELOG      0x00000200 // purge log after backup: for old and new oraclerman
#define ASDDO_R_ORACLEEBU_CONTROLFILE   0x00000100 // restore control file
#define ASDDO_R_ORACLEEBU_OVERWRITELOG  0x00000200 // overwrite existing logs
#define ASDDO_R_ORACLEEBU_RECOVER       0x00002000
#define ASDDO_R_ORACLEEBU_MULTISTREAM	0x00004000 // Multistreaming restore
#define ASDDO_B_ORACLEEBU_TIMEFINDER	0x00000400 // EMC Timefinder
#define ASDDO_B_ORACLEEBU_SERVERLESS    0x00000800 // Serverless: for old and new oraclerman
#define ASDDO_B_ORACLEEBU_FASTRAX       0x00001000 // EMC Fastrax

#ifdef NEW_ORACLE
#define	ASDDO_B_ORACLERMAN_USE_CAT			0x00080000	// New Oracle Agent Backup Option: Use a RMAN Catalog
#define	ASDDO_B_ORACLERMAN_PROXYCOPY 		0x00200000	// New Oracle Agent Backup Type: Use Proxy Copy
#define	ASDDO_B_ORACLERMAN_INCREMENTALBK	0x00100000	// New Oracle Agent Backup Type: Incremental
#define	ASDDO_B_ORACLERMAN_SNAPSHOT			0x00400000  // New Oracle Agent Backup Type: Use Snapshot Backup
#define	ASDDO_B_ORACLERMAN_CUMULATIVE		0x00010000  // New Oracle Agent Backup Type: Cumulative
#define ASDDO_B_ORACLERMAN_RECOVERYAREA		0x00800000	// New Oracle Agent Backup Option: Backup recovery area
#define ASDDO_B_ORACLERMAN_COMPRESSED		0x01000000  // New Oracle Agent Backup Option: Compressed

#ifdef E12_TASK
// E12 agent options
#define ASDDO_B_EXDB_REPLICA			0x00000200 // back up data from replica server. For E12 agent
#define ASDDO_B_EXDB_FULL_INSTANCE		0x00000400 // back up full E12 instance. For E12 agent
#endif // E12_TASK

//define value for Archived Logs Selections
//value used by ASDDISKEXNEXTORACLE.usArchLogSel & ASTAPEXNEXTORACLE.usArchLogSel
#define	ORACLERMAN_AL_ALL			1					// archived Logs Selection: all
#define	ORACLERMAN_AL_PATTERN		2					// archived Logs Selection: Pattern Based
#define	ORACLERMAN_AL_TIME			3					// archived Logs Selection: Time Based
#define	ORACLERMAN_AL_SCN			4					// archived Logs Selection: SCN Based
#define	ORACLERMAN_AL_LOGSEQ		5				    // archived Logs Selection: Log Sequence Based
#define ORACLERMAN_AL_NONE          6                   //archived Logs Selection: NONE of Logs Restored




//define value for New Oracle Agent Restore Types
//values used by ASTAPEXNEXTORACLE.usRestoreype
#define R_ORACLERMAN_RESTORE_LATESTBK		1	// New Oracle Agent Restore Type: From Latest Backup
#define R_ORACLERMAN_RESTORE_BKMADEON		2	// New Oracle Agent Restore Type: From Backup made on
#define R_ORACLERMAN_RESTORE_BKTAG			3	// New Oracle Agent Restore Type: From Backup Tag


//define value for New Oracle Agent Recovery Types
//values used by ASTAPEXNEXTORACLE.usRecoveryType
#define R_ORACLERMAN_RECOVER_NO			1	// New Oracle Agent Recovery Type:
#define R_ORACLERMAN_RECOVER_LOG		2	// New Oracle Agent Recovery Type: Until Log Sequence Number
#define R_ORACLERMAN_RECOVER_SCN		3	// New Oracle Agent Recovery Type: Until SCN Number
#define R_ORACLERMAN_RECOVER_LOGSEQ		4	// New Oracle Agent Recovery Type: Until End of Logs
#define R_ORACLERMAN_RECOVER_TIME		5	// New Oracle Agent Recovery Type: Until Time

//define values for New Oracle Agent Restore Options
//value used by ASTAPEXNEXTORACLE.fOptionsExOracle
#define ASDDO_R_ORACLERMAN_PUT_ONLINE			0x00000400		// put the restored objects online option
#define ASDDO_R_ORACLERMAN_USE_RMAN_CAT			0x00080000		// use an rman catalog
#define ASDDO_R_ORACLERMAN_LIST_BACKUP_SET		0x00000800		// List Backup sets for selected objects





#endif // NEW_ORACLE


// INFORMIX ONBAR options
#define ASDDO_B_INFMX_CURRENT_LOG       0x00000200 // Current log backup
#define ASDDO_B_INFMX_SALVAGE_LOG       0x00000400 // Salvage log backup
#define ASDDO_B_INFMX_LEVEL             0x00001800 // Backup level 0,1,2
#define ASDDO_B_INFMX_LEVEL_0           0x00000000 // Backup level 0
#define ASDDO_B_INFMX_LEVEL_1           0x00000800 // Backup level 1
#define ASDDO_B_INFMX_LEVEL_2           0x00001000 // Backup level 2
#define ASDDO_R_INFMX_PHY_OR_LOG        0x00000100 // Physical or Logical
#define ASDDO_R_INFMX_PHYSICAL          0x00000200 // Physical restore
#define ASDDO_R_INFMX_POT_OR_LOG        0x00000400 // Point-in-time or Log#
#define ASDDO_R_INFMX_POINTINTIME       0x00000800 // Point-in-time restore

// PLC's Snapshot options
#define ASDDO_SNAP_DEFAULT              0x00000000 // Use Uagent's setting
#define ASDDO_SNAP_MASK                 0x00000300 // Snap on/off mask
#define ASDDO_SNAP_ON                   0x00000100 // Snap on
#define ASDDO_SNAP_OFF                  0x00000200 // Snap off: no snap
#define ASDDO_SNAP_MODE_MASK            0x00000c00 // Snap mode mask
#define ASDDO_SNAP_MODE_FILESYSTEM      0x00000400 // Snap on the filesystem
#define ASDDO_SNAP_MODE_DIR             0x00000800 // Snap on the dirs
#define ASDDO_SNAP_MODE_FILE            0x00000c00 // Snap on the files

// CA OpenIngres options
#define ASDDO_B_OPENINGRES_DELJOURNAL   0x00000100 // delete journal after backup
#define ASDDO_R_OPENINGRES_POINTOFTIME  0x00000100 // restore the check-point to point of time
#define ASDDO_R_OPENINGRES_NOJOURNAL    0x00000200 // don't recover the database from the journal
#define ASDDO_R_OPENINGRES_OVERWRITELOG 0x00000400 // overwrite the logs

// Sybase Native options
#define ASDDO_B_SYBASENATIVE_NODATABASE 0x00000100 // backup transactions logs only

// Misc.
#define ASDDO_EXTENDED			0x20000000 // Include extended structure
#define ASDDO_INTERNAL2         0x40000000 // Reserved Bit for attended mode
#define ASDDO_INTERNAL1         0x80000000 // Reserved Bit for attended mode

//Volume Shadow Copy Service
#define ASDDO_B_VSS_USE					0x00000100
#define ASDDO_B_VSS_EXCLUDE_INCLUDES	0x00000200
#define ASDDO_B_VSS_EXCLUDE_EXCLUDES	0x00000400
#define ASDDO_B_VSS_ON_FAIL				0x00000800
#define ASDDO_B_VSS_USE_JOB_METHOD		0x00080000
#define ASDDO_B_VSS_FULL				0x00100000
#define ASDDO_B_VSS_INCREMENTAL			0x00200000
#define ASDDO_B_VSS_DIFFERENTIAL		0x02000000
#define ASDDO_B_VSS_COPY				0x04000000
#define ASDDO_B_VSS_LOG					0x08000000

#ifdef BEB_HARDWARE_VSS
// for Hardware Volume Shadow Copy Service
#define ASDDO_B_VSS_HARDWARE			0x10000000
#endif	//BEB_HARDWARE_VSS

// For De-Compression of Compressed Files (for OES Services only)
#define ASDDO_DECOMPRESSFILES			0x00000100
#define ASDDO_B_NOARCHIVEBIT			0x00000200

//Flags defined for DISK extension structure
#define ASDEX_B_TIMEFINDER				0x00000001 // DBAgents Hardware snapshot
#define ASDEX_B_FASTRAX					0x00000002 // DBAgents Fastrax
#define ASDEX_B_OFFLINE					0x00000004 // DBAgents Hardware snapshot/Fastrax Offline
#define ASDEX_B_RESYNC					0x00000008 // DBAgents Hardware snapshot/Fastrax Options
#define ASDEX_B_BCV_SPLIT				0x00000010 // DBAgents Hardware snapshot/Fastrax Options
#define ASDEX_B_BCV_COPY				0x00000020 // DBAgents Hardware snapshot/Fastrax Options

#ifdef NEW_ORACLE
#define ASDEX_B_USE_DISKEXNEXTORACLE	0x00000040 // New Oracle Agent, pvNextExtension point to structure ASDDISKEXNEXTORACLE
#endif // NEW_ORACLE

#ifdef BEB_HARDWARE_VSS
#define ASDEX_B_TRANSPORTABLE_VSS		0x00000100 // Transportable snapshot (HDVSS)
#define ASDEX_B_LEAVE_SNAPSHOT			0x00000200 // HDVSS: Leave snapshot volume alone without destroying it after backup.
#define ASDEX_B_LEAVE_SNAPSHOT_MOUNT	0x00000400 // HDVSS: Do not un-mount the snapshot volume.
#endif    //BEB_HARDWARE_VSS

#ifdef BAB_VSS_DPM
#define ASDEX_B_DPM_WRITER				0x00000800
#endif

#ifdef WANSYNC_SUPPORT
#define ASDEX_B_WANSYNC_ENTITY			0x00001000 // pvNextExtension points to ASDDISKEXNEXT_WANSYNC_ENTITY structure
#endif

#ifdef FILTER_COMBINE
#define ASDEX_B_COMBINE_FILTER			0x00002000 // Combine global, node filter to disk filter if they are not conflicting.
#endif //FILTER_COMBINE

#ifdef VMWARE_SUPPORT
#define ASDEX_B_VMWARE_FS				0x00004000 // VMWare file mode backup
#define ASDEX_B_VMWARE_DISKEXNEXT		0x00008000 // pvNextExtension points to ASDDISKEXNEXT_VMWARE structure.
#endif

// maoja01 add begin
#ifdef SPS_2007_CHANGES
#define ASDEX_B_SPS2007_DISKEXNEXT		0x00010000 // pvNextExtension points to ASDDISKEXNEXT_SPS2007 structure.
#endif //SPS_2007_CHANGES
// maoja01 add end
#define ASDEX_B_WHOLENODE_BACKUP		0x00020000 // disk is in whole node backup

#ifdef R12V_VM_TASKS
#define ASDEX_B_VMWARE_IMAGE_FS			0x00040000 // VMWARE_AGENT, Raw mode allow file level restore
#define ASDEX_B_VMWARE_WHOLE_ESX_BACKUP 0x00080000 // VMWARE_AGENT, whole ESX is selected, this flag is saved in each ASDDISK of this ESX
#define ASDEX_B_VMWARE_RAW_FILE_MIX		0x00100000 // VMWARE_AGENT, Mixed mode backup
#define ASDEX_B_VM_IGNORE_LOCAL_OPTION  0x00200000	// VMWARE_AGENT, No VM local option
#define ASDEX_B_VMWARE_FS_VM_DIRECT		0x00400000	// VMWARE_AGENT, File mode backup from VM directly
#endif// R12V_VM_TASKS

//<XUVNE01> 2008-01-29
//BOOL isASDDiskExUnicode(PASDDISKEX pAsDISK)
#define ASDEX_UNICODE					(0x01)
#define ASDEX_PSEUDO_UNICODE			(0x02)
#define isASDDiskExUnicode(pAsDISKEx)			((pAsDISKEx)?(pAsDISKEx->ucUnicodeFlags & ASDEX_UNICODE):FALSE)
#define setASDDiskExUnicode(pAsDISKEx)			(pAsDISKEx->ucUnicodeFlags |= ASDEX_UNICODE)
#define isASDDiskExPseudoUnicode(pAsDISKEx)		((pAsDISKEx)?(pAsDISKEx->ucUnicodeFlags & ASDEX_PSEUDO_UNICODE):FALSE)
#define setASDDiskExPseudoUnicode(pAsDISKEx)	(pAsDISKEx->ucUnicodeFlags |= ASDEX_PSEUDO_UNICODE)
#define isASDDiskPseudoExtension(pAsDISKEx)		((pAsDISKEx)?(pAsDISKEx->ucUnicodeFlags & ASDTO_EX_PSEUDO_Extension):FALSE)
#define setASDDiskPseudoExtension(pAsDISKEx)	(pAsDISKEx->ucUnicodeFlags |= ASDTO_EX_PSEUDO_Extension)

// daist01 3/19/2008
#define clearASDiskExUnicode(pAsDISKEx)	do{	\
	if ((pAsDISKEx) != NULL)	(pAsDISKEx)->ucUnicodeFlags &= ~ASDEX_UNICODE;	\
}while(0)

#define clearASDiskExPseudoUnicode(pAsDISKEx)	do{	\
	if ((pAsDISKEx) != NULL)	(pAsDISKEx)->ucUnicodeFlags &= ~ASDEX_PSEUDO_UNICODE;	\
}while(0)
#define clearASDDiskExPseudoExtension(pAsDISKEx)	do {	\
	if ((pAsDISKEx) != NULL)	(pAsDISKEx)->ucUnicodeFlags &= ~ ASDTO_EX_PSEUDO_Extension;	\
}while(0)


#ifdef NEW_ORACLE
// structure used for new oracle agent RMAN related fields
typedef struct tagASDDISKEXNEXTORACLE
{
	PTSZ pszOracleUserName;
	PTSZ pszOraclePwd;
	PTSZ pszRManCatOwner;							// Catalog Owner Name, max 64 char?
	PTSZ pszRManCatPW;								// Catalog Owner Password, max 64 char?
	PTSZ pszOffloadHost;							// the Offload Host, max 258?
	PTSZ pszBackPiecePrefix; 						// left part of the Backup Piece Format entry, max 25?
	PTSZ pszBackPieceSuffix;						// the right part of the Backup Piece Format entry
	PTSZ pszArchPattern;							// Pattern field defined for the Pattern Based radio button, max length:MAXPATHLEN
	PTSZ pszRManScript;								// the Load RMAN Script, max MAXPATHLEN?
	PTSZ pszRManTag;								// RMAN Backup Tag
	FLAG32 fOptionsExOracle;								// RMAN related options, there is no options go to this flag now.
	ULONG ulArchFromTime;							// shall we use ULONG or pszt? GUI format: 度x/xx/xxxx:xx:xx:xx

	ULONG ulArchUntilTime;							// shall we use ULONG or pszt? GUI format: 度x/xx/xxxx:xx:xx:xx

	ULONG usIncrementalLevel;						// Incremental Level
	ULONG usChannels;								// the Number of Channels (Streams)
	ULONG usBackPieceSize;							// corresponds to the Backup Piece Size
	ULONG usReadRate;								// the Read Rate field
	ULONG usFilesPerBackSet;						// the Number of Files per Backup Set
	ULONG usMaxOpenFiles;							// the Maximum Number of Opened Files
	ULONG usSetSize;								// the Backup Set Size (KB)
	ULONG usBlockSize;								// the Block Size <# bytes>
	ULONG usCopies;									// the Number of Copies
	ULONG usArchLogSel;								// Archived Log Selection		define value for 5 radio button.
	ULONG usThread;									// the Thread Number field
	ULONG usArchFromScn;							// the From SCN field for the SCN Based radio button
	ULONG usArchUntilScn;							// the Until SCN field for the SCN Based radio button
	ULONG usArchFromLogSeq;							// the From Log Sequence Number field for the Log Sequence Based  radio button
	ULONG usArchUntilLogSeq;						// the Until Log Sequence Number field for the Log Sequence Based  radio button

#ifdef BAB_ORACLE_RMAN_ENHANCEMENT
	ULONG usWaitForDriveTime;
	ULONG usMasterJobID;
	ULONG usDummyJobID;
	UCHAR pszOpaque[256];
#endif

	ULONG usPadding1;		// padding
	UCHAR aucReserved[34]; 	// reserved for future use.
} ASDDISKEXNEXTORACLE;

// structure used for old oracle agent RMAN related fields
typedef struct tagASDDISKEXNEXTORACLE_OLD
{
	PTSZ pszOracleUserName;
	PTSZ pszOraclePwd;
	PTSZ pszRManCatOwner;							// Catalog Owner Name, max 64 char?
	PTSZ pszRManCatPW;								// Catalog Owner Password, max 64 char?
	PTSZ pszOffloadHost;							// the Offload Host, max 258?
	PTSZ pszBackPiecePrefix; 						// left part of the Backup Piece Format entry, max 25?
	PTSZ pszBackPieceSuffix;						// the right part of the Backup Piece Format entry
	PTSZ pszArchPattern;							// Pattern field defined for the Pattern Based radio button, max length:MAXPATHLEN
	PTSZ pszRManScript;								// the Load RMAN Script, max MAXPATHLEN?
	PTSZ pszRManTag;								// RMAN Backup Tag
	FLAG32 fOptionsExOracle;								// RMAN related options, there is no options go to this flag now.
	ULONG ulArchFromTime;							// shall we use ULONG or pszt? GUI format: 度x/xx/xxxx:xx:xx:xx

	ULONG ulArchUntilTime;							// shall we use ULONG or pszt? GUI format: 度x/xx/xxxx:xx:xx:xx

	ULONG usIncrementalLevel;						// Incremental Level
	ULONG usChannels;								// the Number of Channels (Streams)
	ULONG usBackPieceSize;							// corresponds to the Backup Piece Size
	ULONG usReadRate;								// the Read Rate field
	ULONG usFilesPerBackSet;						// the Number of Files per Backup Set
	ULONG usMaxOpenFiles;							// the Maximum Number of Opened Files
	ULONG usSetSize;								// the Backup Set Size (KB)
	ULONG usBlockSize;								// the Block Size <# bytes>
	ULONG usCopies;									// the Number of Copies
	ULONG usArchLogSel;								// Archived Log Selection		define value for 5 radio button.
	ULONG usThread;									// the Thread Number field
	ULONG usArchFromScn;							// the From SCN field for the SCN Based radio button
	ULONG usArchUntilScn;							// the Until SCN field for the SCN Based radio button
	ULONG usArchFromLogSeq;							// the From Log Sequence Number field for the Log Sequence Based  radio button
	ULONG usArchUntilLogSeq;						// the Until Log Sequence Number field for the Log Sequence Based  radio button
	ULONG usPadding1;		// padding
	UCHAR aucReserved[34]; 	// reserved for future use.
} ASDDISKEXNEXTORACLE_OLD;

#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDDISKEXNEXTORACLE FAR  *PASDDISKEXNEXTORACLE;
typedef ASDDISKEXNEXTORACLE NEAR *NPASDDISKEXNEXTORACLE;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASDDISKEXNEXTORACLE FAR  *PASDDISKEXNEXTORACLE;
typedef ASDDISKEXNEXTORACLE NEAR *NPASDDISKEXNEXTORACLE;
#else
typedef ASDDISKEXNEXTORACLE *PASDDISKEXNEXTORACLE;
#endif
#endif // NEW_ORACLE

#ifdef WANSYNC_SUPPORT
typedef struct __ASDDISKEXNEXT_WANSYNC_ENTITY {
  PTSZ pszEntity;
  PTSZ pszScenaria;
  PTSZ pszReplicaHost;
  PTSZ pszMasterHost;
  PTSZ pszPhysicalPath;
  PTSZ pszLogicalPath;
  PTSZ pszScenarioID;
  PTSZ pszScenarioVersion;
  PTSZ pszScenarioSignature;
  ULONG scenarioType;
  ULONG entityID;
#ifdef WANSYNC_SUPPORT_MIXED_SCENARIO
  ULONG entityType;
  USHORT usMasterPort;
  USHORT usReplicaPort;
  UCHAR reserved[60];
#else
  USHORT usMasterPort;
  USHORT usReplicaPort;
  UCHAR reserved[64];
#endif
} ASDDISKEXNEXT_WANSYNC_ENTITY;

#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDDISKEXNEXT_WANSYNC_ENTITY FAR  *PASDDISKEXNEXT_WANSYNC_ENTITY;
typedef ASDDISKEXNEXT_WANSYNC_ENTITY NEAR *NPASDDISKEXNEXT_WANSYNC_ENTITY;
#pragma pointer_size(restore)
#else
typedef ASDDISKEXNEXT_WANSYNC_ENTITY *PASDDISKEXNEXT_WANSYNC_ENTITY;
#endif
#endif // WANSYNC_SUPPORT

//R12V_VM_NTAGENT
// flags in ASDDISKEXNEXT_VMWARE::fOptions
#define VM_B_VMAGENT_DIRECT		0x00000001				// issue 17285656

#ifdef VMWARE_SUPPORT

#define VM_OSTYPE_WINDOWS		1	// windows
#define VM_OSTYPE_LINUX			2	// linux/unix
#define VM_OSTYPE_NETWARE		3	// netware

//<XUVNE01> 2008-01-29
#define ASDEXNXT_VM_UNICODE	0x00000001
#define isASDiskExNextVMUnicode(pvDiskExNextVMWare)		((pvDiskExNextVMWare)?(pvDiskExNextVMWare->fOptions & ASDEXNXT_VM_UNICODE):0)
#define setASDiskExNextVMUnicode(pvDiskExNextVMWare) (pvDiskExNextVMWare->fOptions |= ASDEXNXT_VM_UNICODE)

typedef struct __ASDDISKEXNEXT_VMWARE {
  PTSZ pszESXServerName;
  PTSZ pszESXServerIP;
  PTSZ pszVMName;
  PTSZ pszVMIP;
  PTSZ pszVMUUID;
  ULONG fOptions;
  ULONG ulOSType;
  UCHAR reserved[64];
} ASDDISKEXNEXT_VMWARE;

#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDDISKEXNEXT_VMWARE FAR  *PASDDISKEXNEXT_VMWARE;
typedef ASDDISKEXNEXT_VMWARE NEAR *NPASDDISKEXNEXT_VMWARE;
#pragma pointer_size(restore)
#else
typedef ASDDISKEXNEXT_VMWARE *PASDDISKEXNEXT_VMWARE;
#endif
#endif //VMWARE_SUPPORT

// maoja01 add begin
#ifdef SPS_2007_CHANGES
typedef struct __ASDDISKEXNEXT_SPS2007 {
  PTSZ pszDumpLocation;
  PTSZ pszDumpShareName;
  PTSZ pszDumpServerName;
  ULONG fOptions;
  ULONG ulBackupMethod;
  ULONG ulBackupThreads;
  ULONG ulIsVerbose;
  ULONG ulUpdateProgress;
  ULONG ulPurgeDataAfterBackup;
  UCHAR reserved[88];
  PVOID  pvNextExtension;				// Can be further extended
} ASDDISKEXNEXT_SPS2007;				// total 128 bytes

#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDDISKEXNEXT_SPS2007 FAR  *PASDDISKEXNEXT_SPS2007;
typedef ASDDISKEXNEXT_SPS2007 NEAR *NPASDDISKEXNEXT_SPS2007;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASDDISKEXNEXT_SPS2007 FAR  *PASDDISKEXNEXT_SPS2007;
typedef ASDDISKEXNEXT_SPS2007 NEAR *NPASDDISKEXNEXT_SPS2007;
#else
typedef ASDDISKEXNEXT_SPS2007 *PASDDISKEXNEXT_SPS2007;
#endif
#endif //SPS_2007_CHANGES
// maoja01 add end

///Keyan01 25/June/03 aucReserved now 30 from 50
//and ImageOptions struct added
typedef struct tagASDDISKEX
{
	USHORT nCompareFirstMB;				// Compare first n MB to verify
	USHORT usTapeSequence;				// Tape sequence backed up to (internal)
#ifdef SESSNUM_INCREASE
	UCHAR  padding1[2];
#else
	USHORT usTapeSession;					// Tape session backed up to (internal)
#endif
#ifdef BAB_VSSSCHEMA
  USHORT VSSUsedBackupType;
  // Add By Gil
  // PMF=zhagi01
  // 7/6/2006 12:04:51 PM
#ifdef BAB_ASDDISKEX_CONSISTENT
  // this is for DB2 usage
  // before Oriole these two short was formated to a string
  // and send the string in aucReserved[50] (this is a UNIX definition).
  // here we consist these definitions together both UNIX and Windows
  // using two short instead of using aucReserved directly.
  USHORT usPurneHistory;
  USHORT usParallelism;
#ifdef SESSNUM_INCREASE
  ULONG usTapeSession;

#ifdef E14_TASK	//<XUVNE01> E14 support. 2009-05-11
  ULONG	ulExDAGMemberServerCount;			//The number of DAG member servers in the list
  PTSZ  pszExDAGMemberServerList;			//User defined Exchange DAG member server prefer sequence
  UCHAR aucReserved[7];
#else	//E14_TASK
  UCHAR aucReserved[15];
#endif	//E14_TASK

#else
  UCHAR aucReserved[19];
#endif
#ifdef UNICODE_JIS_SUPPORT
  UCHAR ucUnicodeFlags;
#else // UNICODE_JIS_SUPPORT
  UCHAR aucReservedForUnicodeFlags;
#endif // UNICODE_JIS_SUPPORT
#else //BAB_ASDDISKEX_CONSISTENT
  // End Add By Gil
  UCHAR  aucReserved[24];
  // Add By Gil
  // PMF=zhagi01
  // 7/6/2006 12:06:01 PM
#endif //BAB_ASDDISKEX_CONSISTENT
  // End Add By Gil
#else // BAB_VSSSCHEMA
  UCHAR  aucReserved[26];
#endif // BAB_VSSSCHEMA
	FLAG32  fAgentOptions;			// agent options for sql agent
	IORetry ImageOptions;			// Image options
	FLAG32  fOptions;				// Extended options
	PVOID   pvNextExtension;		// Can be further extended
} ASDDISKEX;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDDISKEX FAR  *PASDDISKEX;
typedef ASDDISKEX NEAR *NPASDDISKEX;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASDDISKEX FAR  *PASDDISKEX;
typedef ASDDISKEX NEAR *NPASDDISKEX;
#else
typedef ASDDISKEX *PASDDISKEX;
#endif

typedef struct tagASDDISK
{
  PTSZ   pszDiskName;                   // Volume or Drive Name
  PTSZ   pszDiskPW;                     // Disk password (used for Session PW)
  ULONG  nFilterItems;                  // # of Filter items in list
  PVOID  pvFilterList;                  // List of filters
  ULONG  nDiskItems;                    // # of ASDISKITEM items in list
  PASDISKITEM pASDiskItemList;          // List of ASDISKITEM items
  USHORT usFileSystem;                  // See Device File System
  USHORT usInterleafPriority;         	// Interleaving priority (JCL 980930)
  FLAG32 fOptions;                      // See ASDDISK Options
  UCHAR  aucInternal[4];                // Reserved for internal use
  PTSZ   pszDiskPW2;                    // Disk password (used for Disk PW since other is used for session)
  PTSZ   pszUserName;                   // User name  ( for Microsoft Network )
  PTSZ   pszEncryptKey;                 // Encryption key - New in Sniper
  PASDDISKEX  pASDDiskEx;				// Extended disk structure - new for v6.62
} ASDDISK;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDDISK FAR  *PASDDISK;
typedef ASDDISK NEAR *NPASDDISK;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASDDISK FAR  *PASDDISK;
typedef ASDDISK NEAR *NPASDDISK;
#else
typedef ASDDISK *PASDDISK;
#endif

//
//  ASTAPEITEM Options
//
#define ASTIO_TRAVERSEDIR                       0x00000001  // Traverse directories
#define ASTIO_FILEENTRY                         0x00000002  // Entry is a file, otherwise a dir
#define ASTIO_FILEOWNER       					0x00000004 // Entry is a file-owner
#define ASTIO_VSS_COMPONENT						0x00000008	// Volume Shadow Copy Component
#define ASTIO_VSS_COMPONENT_SELECTABLE			0x00000010	// Volume Shadow Copy Component - selectable for restore
#define ASTIO_VSS_WRITER						0x00000020  // Volume Shadow Copy Writer
#define ASTIO_DBA_NEVER_OVERWRITE               0x00080000  // Microsfot Exchanger Brick Level - Never overwrite original message
#define ASTIO_DBA_OVERWRITE_CHANGED             0x00100000  // Microsfot Exchanger Brick Level - Overwrite original message if changed
#define ASTIO_DBA_ALWAYS_OVERWRITE              0x00200000  // Microsfot Exchanger Brick Level - Always overwrite message
#define ASTIO_DBA_NEVER_OVERWRITE_CHANGED       0x00400000  // Microsfot Exchanger Brick Level - Never overwrite original message if changed
#define ASTIO_ACTIVE_DIR_RESTORE                0x00800000  // active directory object level restore//baide02 2009.05.06

#define ASTIO_OPTION_PARTIAL_DB     0x00000040 //E12 partial mailbox select option

//<XUVNE01> 2008-01-29
//BOOL isASTapeItemUnicode(PASTAPEITEM pAsTapeItem)
#define ASTIO_UNICODE							(0x01)
#define ASTIO_PSEUDO_UNICODE					(0x02)
#define isASTapeItemUnicode(pAsTapeItem)			((pAsTapeItem)?(pAsTapeItem->ucUnicodeFlags & ASTIO_UNICODE):FALSE)
#define setASTAPEITEMUnicode(pAsTapeItem)			(pAsTapeItem->ucUnicodeFlags |= ASTIO_UNICODE)
#define isASTapeItemPseudoUnicode(pAsTapeItem)		((pAsTapeItem)?(pAsTapeItem->ucUnicodeFlags & ASTIO_PSEUDO_UNICODE):FALSE)
#define setASTAPEITEMPseudoUnicode(pAsTapeItem)		(pAsTapeItem->ucUnicodeFlags |= ASTIO_PSEUDO_UNICODE)

// daist01 3/19/2008
#define clearASDTapeItemUnicode(pAsTapeItem)	do {	\
	if ((pAsTapeItem) != NULL)	(pAsTapeItem)->ucUnicodeFlags &= ~ ASTIO_UNICODE;	\
}while(0)

#define clearASDTapeItemPseudoUnicode(pAsTapeItem)	do {	\
	if ((pAsTapeItem) != NULL)	(pAsTapeItem)->ucUnicodeFlags &= ~ ASTIO_PSEUDO_UNICODE;	\
}while(0)


typedef struct tagASTAPEITEM
{
  ULONG  ulQFAChunkNum;                 // QFA chunk number
  USHORT usQFAChunkOffset;              // QFA chunk offset
  USHORT nPadding1;                     // Reserved for padding
  PTSZ   pszFileorDir;                  // Fully qualified file or directory
  FLAG32 fOptions;                      // See ASTAPEITEM Options
  // Add by Gil
  // 3/5/2007 10:41:10 AM
#ifdef SPS_2007_CHANGES
  #ifdef SPS2007_XML_MOVE
    UCHAR  aucReservedSPS2007RestoreInfo[4];               // Reserved for future use
  #else
    PTSZ	 pszSPS2007RestoreInfo;			// SharePoint 2007 restore information, credential info, location info, chunk num/off info
  #endif // SPS2007_XML_MOVE
#else // SPS_2007_CHANGES
  // End add by Gil 3/5/2007 10:41:10 AM
  UCHAR  aucReservedForSPS2007Changes[4];               // Reserved for future use
#endif // SPS_2007_CHANGES
#ifdef UNICODE_JIS_SUPPORT
  UCHAR ucUnicodeFlags;
#else // UNICODE_JIS_SUPPORT
  UCHAR aucReservedForUnicodeFlags;
#endif // UNICODE_JIS_SUPPORT
    UCHAR  aucReserved[11];               // Reserved for future use
} ASTAPEITEM;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASTAPEITEM FAR  *PASTAPEITEM;
typedef ASTAPEITEM NEAR *NPASTAPEITEM;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASTAPEITEM FAR  *PASTAPEITEM;
typedef ASTAPEITEM NEAR *NPASTAPEITEM;
#else
typedef ASTAPEITEM *PASTAPEITEM;
#endif

//
//  ASDTAPE Options
//
#define ASDTO_SKIP            0x00000001 // Skip this tape device
#define ASDTO_COMPAREONLY     0x00000002 // Compare only - New in Sniper
#define ASDTO_COPYCOMPARE     0x00000004 // Copy and Compare - New in Sniper
#define ASDTO_CHANGERPRESENT  0x00000008 // Changer present - New in Sniper
#define ASDTO_EXTENDED        0x08000000 // Include extended structure
#define ASDTO_ROTATION        0x40000000 // Rotation tape for restore
#define ASDTO_GFS_ROTATION    0x80000000 // GFS Rotation tape for restore

//
// The following options are used for DSABeginBackup, DSABeginBackupStripe
// trunc_option settings
//
#define ASDTO_OPTION_LOCAL                     0x00000002  // backup/restore is local(only used by Agent)
#define ASDTO_OPTION_REMOTE                    0x00000004  // backup/restore is remote
#define ASDTO_OPTION_DATABASE                  0x00000008  // backup/restore database
#define ASDTO_OPTION_WITH_TRUNCATE             0x00000010  // backup/restore log w. truncate
#define ASDTO_OPTION_NO_TRUNCATE               0x00000020  // backup/restore log w.o. truncate
#define ASDTO_OPTION_FULL                      0x00000040  // backup/restore database & log w. truncate
#define ASDTO_OPTION_COPY                      0x00000080  // backup/restore database and log w.o. truncate
#define ASDTO_OPTION_TABLE                     0x00000100  // backup/restore database table
#define ASDTO_OPTION_ERASE                     0x00000200  // restore       database and erase existing files
#define ASDTO_OPTION_PUB                       0x00000400  // restore public database (in Exchange)
#define ASDTO_OPTION_PRIV                      0x00000800  // restore private       database (in Exchange)
#define ASDTO_OPTION_START_SERVICE             0x00001000  // restore and start the service
#define ASDTO_OPTION_VERIFY                    0x00002000  // verify after restore
#define ASDTO_OPTION_CHECKSUM                  0x00004000  // generate checksum during backup
//for exchange 2000
#define ASDTO_OPTION_IS2000					   0x00008000	//dbaexch 2000 restore
#define ASDTO_OPTION_LASTSET				   0x00010000	//dbaexch 2000 restore
#define	ASDTO_OPTION_MOUNTDB				   0x00020000	//dbaexch 2000 restore
#define	ASDTO_OPTION_REST_LOGSONLY			   0x00040000	//dbaexch 2000 restore
#define ASDTO_OPTION_NOITEM					   0x00080000	//dbaexch 2000 restore
#define ASDTO_OPTION_APPLYLOGS                 0x00100000   //dbaexch 2000 restore

#define ASDTO_OPTION_WAIT_COMMITMENT			0x00200000 //dbaexch 2000 restore
#define ASDTO_OPTION_DISMOUNT_DB				0x00400000 //dbaexch 2000 restore
#define ASDTO_OPTION_ALLOW_DB_OVERWRITTEN		0x00800000 //dbaexch 2000 restore
#define ASDTO_OPTION_CREATE_RSG					0x01000000 //CP restore
#define ASDTO_OPTION_TO_RSG						0x02000000 //E12 restore
#define ASDTO_OPTION_RESTORE_MBX				0x04000000 //E12 restore
#define ASDTO_OPTION_AUTO_PACKAGING				0x08000000 //E12 restore
#define ASDTO_OPTION_PARTIAL_DB					0x00000040 //E12 restore
#define ASDTO_OPTION_TO_FSD						0x00000080 //E12 restore
#define ASDTO_OPTION_RESTORE_LOGS_ONLY			0x00000100 //E14 restore logs only

//
#define ASDTO_OPTION_NEVER_OVERWRITE           0x00080000  // Microsfot Exchanger Brick Level - Never overwrite original message
#define ASDTO_OPTION_OVERWRITE_CHANGED         0x00100000  // Microsfot Exchanger Brick Level - Overwrite original message if changed
#define ASDTO_OPTION_ALWAYS_OVERWRITE          0x00200000  // Microsfot Exchanger Brick Level - Always overwrite message
#define ASDTO_OPTION_NEVER_OVERWRITE_CHANGED   0x00400000  // Microsfot Exchanger Brick Level - Never overwrite original message if changed

#define ASDTO_OPTION_SCANMERGESTARTATSEQ1      0x10000000  // Scan Merge  Start scan/merge at seq 1
#define ASDTO_OPTION_SCANMERGESTARTATCURRENT   0x20000000  // Scan Merge  Start scan/merge at current tape in drive

//
// Following constant are used for Image Option.
//
#define ASDTO_NW_IMAGE             0x00004000   // Used for image option NetWare only.
#define ASDTO_NT_IMAGE			   0x00008000   // Used for image option NT only.

//
//  New Tape Methods
//
#define QJNTM_APPEND                0  // Append session to tape
#define QJNTM_OVERWRITESAMEBLANK    1  // Overwrite same tape first or blank
#define QJNTM_OVERWRITESAMEBLANKANY 2  // Overwrite same, blank, or any tape
#define QJNTM_OVERWRITESAMEANYBLANK 3  // Overwrite same, any or blank tape - New in Sniper

#define QJNTM_MULTISTREAMAPPEND		7  // Append especially for multistreaming
									   // Used only bet. master and slave jobs


//
//  Span Tape Methods
//
#define QJSTM_OVERWRITESAMEBLANK    0  // Overwrite same tape first or blank
#define QJSTM_OVERWRITESAMEBLANKANY 1  // Overwrite same, blank, or any tape
#define QJSTM_OVERWRITESAMEANYBLANK 2  // Overwrite same, any or blank tape - New in Sniper


////////////////////////////////////////////////////////////////////////////////////////////////
//
// extended tape structure fOption definitions for Restore:
//defined in DBAPI.H

// DSA modules use flag range of
//     0x00000001  -    0x00004000        15 flags
//
//                                        0x00000001
//                                        0x00000002
//                                        0x00000004
//                                        0x00000008
//                                        0x00000010
//                                        0x00000020
//                                        0x00000040
//                                        0x00000080
//                                        0x00000100	= DSA_OPTION_RESTORE_VI
//                                        0x00000200
//                                        0x00000400
//                                        0x00000800
//                                        0x00001000
//                                        0x00002000
//                                        0x00004000
//                                        0x00008000
#define DSA_OPTION_RESTORE_TIMEFINDER			0x00000400	//for DBAgents Timefinder
#define DSA_OPTION_RESTORE_FASTRAX				0x00000800	//for DBAgents Fastrax
#define DSA_OPTION_RESTORE_OFFLINE				0x00001000	//for DBAgents T/F Offline
#define DSA_OPTION_RESTORE_TOBCV				0x00002000	//for Restoring to BCV
#define DSA_OPTION_RESTORE_RESYNC				0x00004000	//for Resync after restore
#ifdef MULTISTRIPE_SQL // Kevin Lin 6/3/03 SQL multistripe
#define DSA_OPTION_MULTISTRIPE_MASTER		0x40000000
#define DSA_OPTION_MULTISTRIPE_CHILD		0x20000000
#endif //MULTISTRIPE_SQL
//
// TASK modules use flag range of
//     0x00008000  -    0x10000000        17 flags

#define ASDTO_EX_WRITER_NOTIFY				0x00008000
#define ASDTO_EX_WRITER_AUTHORITATIVE_RS    0x00002000

#define ASDTO_EX_AV_SCAN_ARCHIVE          0x00004000       // scan archive files


#define ASDTO_EX_SS_AD_AUTHORATIVE        0x00010000
#define ASDTO_EX_SS_CLUS_FORCERESTORE     0x00020000
#define ASDTO_EX_SS_CLUS_QUORUMCHANGED    0x00040000
#define ASDTO_EX_SS_CERT_DONOTSTOPWWW     0x00080000
#define ASDTO_EX_SS_NTFRS_ASPRIMARY       0x00100000

#ifdef BAB_VSS_LONGHORN_SYSTEMSTATE_SUPPORT
#define ASDTO_EX_SS_CLUS_AUTHORITATIVE_LH_ONLY 0x00200000
#endif

#define ASDTO_EX_JUNCTIONPOINTS_ONLY      0x00200000
#define ASDTO_EX_JUNCTIONPOINTS_NORMALDIR 0x00400000
#define ASDTO_EX_JUNCTIONPOINTS_SKIP      0x00800000
#define ASDTO_EX_WMIDBFORCERESTORE        0x01000000
#define ASDTO_EX_RESTOREDISKSIGNATURE     0x02000000
#define ASDTO_EX_R_TLSDATABASE            0x04000000
#define ASDTO_EX_R_DISKQUOTA              0x08000000
#define ASDTO_EX_R_RSMDATABASE            0x10000000
#define ASDTO_EX_R_WMIDATABASE            0x20000000
#define ASDTO_EX_R_SECURITYDATABASE       0x40000000
//#ifdef BEB_MID_SEQ_RESTORE
#define ASDTO_EX_RESTORE_ONE_SEQUENCE     0x80000000
//#endif
//#define ASDTO_EX_xxxxx_16               0x80000000

//<XUVNE01> 2008-01-29
//BOOL isASDTapeXUnicode(PASDTAPEX pAsdTape)
#define ASDTO_EX_UNICODE						(0x01)
#define ASDTO_EX_PSEUDO_UNICODE					(0x02)
#define ASDTO_EX_PSEUDO_Extension				(0x04)

#define isASDTapeXUnicode(pAsdTapeEx)			((pAsdTapeEx)?(pAsdTapeEx->ucUnicodeFlags & ASDTO_EX_UNICODE):FALSE)
#define setASDTAPEXUnicode(pAsdTapeEx)			(pAsdTapeEx->ucUnicodeFlags |= ASDTO_EX_UNICODE)
#define isASDTapeXPseudoUnicode(pAsdTapeEx)		((pAsdTapeEx)?(pAsdTapeEx->ucUnicodeFlags & ASDTO_EX_PSEUDO_UNICODE):FALSE)
#define setASDTAPEXPseudoUnicode(pAsdTapeEx)	(pAsdTapeEx->ucUnicodeFlags |= ASDTO_EX_PSEUDO_UNICODE)
#define isASDTapeXPseudoExtension(pAsdTapeEx)		((pAsdTapeEx)?(pAsdTapeEx->ucUnicodeFlags & ASDTO_EX_PSEUDO_Extension):FALSE)
#define setASDTAPEXPseudoExtension(pAsdTapeEx)	(pAsdTapeEx->ucUnicodeFlags |= ASDTO_EX_PSEUDO_Extension)

// daist01 3/19/2008
#define clearASDTapeXUnicode(pAsTapeEx)	do {	\
	if ((pAsTapeEx) != NULL)	(pAsTapeEx)->ucUnicodeFlags &= ~ ASDTO_EX_UNICODE;	\
}while(0)

#define clearASDTapeXPseudoUnicode(pAsTapeEx)	do {	\
	if ((pAsTapeEx) != NULL)	(pAsTapeEx)->ucUnicodeFlags &= ~ ASDTO_EX_PSEUDO_UNICODE;	\
}while(0)

#define clearASDTapeXPseudoExtension(pAsTapeEx)	do {	\
	if ((pAsTapeEx) != NULL)	(pAsTapeEx)->ucUnicodeFlags &= ~ ASDTO_EX_PSEUDO_Extension;	\
}while(0)


#ifdef NEW_ORACLE
#define ASDTO_R_EX_TAPEXNEXTORACLE		  0x00000080		//NEW ORACLE AGENT
#endif // NEW_ORACLE

#ifdef BAB_VSSSCHEMA

// flag definitions for ASDTAPEX::fOptionsEx
#define ASDTO_EX2_WRITER_NOT_LAST		0x00000001		// Set if there are more sessions to restore
#endif // BAB_VSSSCHEMA

#ifdef WANSYNC_SUPPORT
#define ASDTO_EX2_REPLICA				0x00000002		// the session is to be restored to replica host
#endif

#ifdef R12V_VM_TASKS
#define ASDTO_EX2_VMIFS_FILEMODE		0x00000004		// VMWARE_AGENT, VM IFS session file mode restore
#define ASDTO_EX2_VM_BMR				0x00000008		// VMWARE_AGENT, VM image restore with BMR
#endif// R12V_VM_TASKS

// flag used by OES agent
#define ASDTO_EX_R_OES_NOTRUSTEES						0x00000100
#define ASDTO_EX_R_OES_NO_DISK_SPACE_RESTRICTIONS		0x00000200
#define ASDTO_EX_R_OES_NO_VOL_RESTRICTIONS				0x00000400

// maoja01 add begin
#ifdef SPS_2007_CHANGES
#define ASDTO_R_EX_SPS2007_TAPEXNEXT 0x00000800 // pvNextExtension points to ASDTAPEXNEXT_SPS2007 structure.
#endif // SPS_2007_CHANGES
// maoja01 add end

#ifdef R12V_VM_TASKS
#define ASDTO_EX_R_VMWARE_TAPEXNEXT		0x00001000	// pvNextExtension points to ASDTAPEXNEXTVMWARE structure.
#define ASDTO_EX_R_VM_POWERON_VM		0x00002000	//Power On VM
#define ASDTO_EX_R_VM_OVERWRITE_VM		0x00004000	//Overwrite VM
#define ASDTO_EX_R_VM_VMRECOVERY_VM		0x00008000	//Recovery VM
#endif// R12V_VM_TASKS

#ifdef NEW_ORACLE
//NEW ORACLE AGENT RESTORE TAPE EX STRUCTURE
typedef struct tagASDTAPEXNEXTORACLE
{
PTSZ	pszOracleUserName;
PTSZ	pszOraclePwd;
PTSZ	pszRManCatOwner;			//catalog owner name
PTSZ	pszRManCatPW;				// catalog owner password
PTSZ	pszRManScript;				// load rman script field
PTSZ	pszArchPattern;				// pattern field for pattern based option
PTSZ	pszRManTag;					// restore from backup tag field
ULONG	ulRestoreTime;				// restore from backup made on
ULONG 	ulRecoverUntilTime;			// until time field for recover until time
ULONG	ulArchFromTime;				//from date field for time based option
ULONG	ulArchUntilTime;			//until date field for time based option
ULONG	usRecoverUntilScn;			// scn number field for until scn option
ULONG	usRecoverUntilLogseq;		// until log seq num for log until option
ULONG	usChannels;					// number of channels
ULONG	usRestoreType;				// Restore Type Options Flag
ULONG	usArchFromScn;				// from scn field for scn based option
ULONG	usArchUntilScn;				// until scn field for scn based option
ULONG	usArchFromLogSeq;			//from log seq field for log sec based
ULONG	usArchUntilLogSeq;			//until log seq field for log sec based
ULONG 	usThread;					// thread number
ULONG	usRecoverThreadNum;			// thread number used with until log seq option
ULONG	usBlockSize;				// block size field in advanced options
ULONG	usValidateBackUpSet;		// validate backup set number field
ULONG	usArchLogSel;				//Archived logs options(Advanced)
ULONG	usRecoveryType;				// Recovery Type Options
FLAG32  fOptionsExOracle;			// PutOnline, ControlFile, List Backup Sets, use Rman Cat options

// either pszUnique File Name or the two BackPiece fields will be used to queury the database, once I know how this info is stored in the DB i can get rid of one field.
//PTSZ	pszUniqueFileName;			//query the database to get tape name and session number
//PTSZ	pszBackPiecePrefix; 		// left part of the Backup Piece Format entry, max 25?
//PTSZ	pszBackPieceSuffix;			// the right part of the Backup Piece Format entry
//PTSZ	pszTapeName;				//tape that database is on
//ULONG	ulSessionNumber;			//session on tape.
UCHAR	aucRESERVED[32];			//RESERVERD Space For Future use
} ASDTAPEXNEXTORACLE;

#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDTAPEXNEXTORACLE FAR  *PASDTAPEXNEXTORACLE;
typedef ASDTAPEXNEXTORACLE NEAR *NPASTAPEXNEXTORACLE;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASDTAPEXNEXTORACLE FAR  *PASDTAPEXNEXTORACLE;
typedef ASDTAPEXNEXTORACLE NEAR *NPASDTAPEXNEXTORACLE;
#else
typedef ASDTAPEXNEXTORACLE *PASDTAPEXNEXTORACLE;
#endif
#endif // NEW_ORACLE

// maoja01 add begin
#ifdef SPS_2007_CHANGES
typedef struct __ASDTAPEXNEXT_SPS2007 {
  PTSZ pszDumpLocation;
  PTSZ pszDumpShareName;
  PTSZ pszDumpServerName;
  ULONG fOptions;
  ULONG ulEntireFarmRestore;
  ULONG ulRestoreMethod;
  ULONG ulRestoreThreads;
  ULONG ulIsVerbose;
  ULONG ulUpdateProgress;
  ULONG ulPurgeDataAfterRestore;
  ULONG ulContinueRestoreIfFailure;
#ifdef SPS2007_XML_MOVE
  PTSZ pszRestoreInfo;
  UCHAR reserved[76];
#else
  UCHAR reserved[80];
#endif // SPS2007_XML_MOVE
  PVOID pvNextExtension; // Can be further extended
} ASDTAPEXNEXT_SPS2007; // total 128 bytes

#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDTAPEXNEXT_SPS2007 FAR  *PASDTAPEXNEXT_SPS2007;
typedef ASDTAPEXNEXT_SPS2007 NEAR *NPASDTAPEXNEXT_SPS2007;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASDTAPEXNEXT_SPS2007 FAR  *PASDTAPEXNEXT_SPS2007;
typedef ASDTAPEXNEXT_SPS2007 NEAR *NPASDTAPEXNEXT_SPS2007;
#else
typedef ASDTAPEXNEXT_SPS2007 *PASDTAPEXNEXT_SPS2007;
#endif
#endif // SPS_2007_CHANGES
// maoja01 add end

#ifdef R12V_VM_TASKS
typedef struct __ASDTAPEXNEXT_VMWAREIFS {
  ULONG  nTapeItems;                    // # of ASTAPEITEM items in list
  PASTAPEITEM pASTapeItemList;          // List of ASTAPEITEM items of VMDK files
  UCHAR  reserved[128];
}ASDTAPEXNEXT_VMWAREIFS;

#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDTAPEXNEXT_VMWAREIFS FAR  *PASDTAPEXNEXT_VMWAREIFS;
typedef ASDTAPEXNEXT_VMWAREIFS NEAR *NPASDTAPEXNEXT_VMWAREIFS;
#pragma pointer_size(restore)
#else
typedef ASDTAPEXNEXT_VMWAREIFS *PASDTAPEXNEXT_VMWAREIFS;
#endif
#endif// R12V_VM_TASKS

typedef struct tagASDTAPEX
{
  PTSZ	 pszDBAUser;					// User name for DBA login
  PTSZ	 pszDBAPW;						// Password for DBA login
  ULONG	 ulDBARestoreTime;				// Time to restore to for DBA
  ULONG	 nTItems;						// Indicates the number of items in the List
  PTSZ	 pzTList;						// The List containing the above stated number of strings
// before changes:
//  UCHAR  QuorumDrive;					// The user needs to let us know if the letter is different
//  ULONG  ulQFAChunkNum;     // located QFA chunk number of sis common store folder in current session for ntagent
//  USHORT usQFAChunkOffset;  // located QFA chunk offset of sis common store folder in current session for ntagent
//  UCHAR  aucReserved[29];       // Reserved

// sis point-n-select fix:
  UCHAR  QuorumDrive;         // The user needs to let us know if the letter is different
  UCHAR  Padding3[3];
  ULONG  ulQFAChunkNum;     // located QFA chunk number of sis common store folder in current session for ntagent
  USHORT usQFAChunkOffset;  // located QFA chunk offset of sis common store folder in current session for ntagent


#ifdef BAB_VSSSCHEMA
#ifdef WANSYNC_SUPPORT
  USHORT usAppPort;					// application port #
#else
  UCHAR  aucReserved0[2];			// Padding; available for later use
#endif
  PTSZ	 pszVirtualPath;
  FLAG32 fAgentOptions;
#ifdef UNICODE_JIS_SUPPORT
  UCHAR ucUnicodeFlags;
#else // UNICODE_JIS_SUPPORT
  UCHAR aucReservedForUnicodeFlags;
#endif // UNICODE_JIS_SUPPORT
#ifdef SESSNUM_INCREASE
  UCHAR  aucReserved[3];       // Reserved
  ULONG	 ulSessionNum;
  ULONG  ulSessionNum2;
#else
  UCHAR  aucReserved[11];       // Reserved
#endif
  FLAG32 fOptionsEx;
#else // NOT BAB_VSSSCHEMA
  UCHAR  aucReserved0[2];			// Padding; available for later use
  PTSZ	 pszVirtualPath;
  FLAG32 fAgentOptions;
  UCHAR  aucReserved[16];       // Reserved
#endif // BAB_VSSSCHEMA


  FLAG32 fOptions;						// Extended options
  PVOID  pvNextExtension;				// Can be further extended
} ASDTAPEX;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDTAPEX FAR  *PASDTAPEX;
typedef ASDTAPEX NEAR *NPASDTAPEX;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASDTAPEX FAR  *PASDTAPEX;
typedef ASDTAPEX NEAR *NPASDTAPEX;
#else
typedef ASDTAPEX *PASDTAPEX;
#endif

typedef struct tagASDTAPE
{
  PTSZ   pszDeviceGroup;                // Tape Device Group
  PTSZ   pszTapeName;                   // Tape name
  USHORT ulTapeID;                      // unsigned short Tape ID (Random ID)
  UCHAR  ucStreamNum;                   // unsigned char  Stream number for RAID - New in Sniper
  UCHAR  ucReserved;                    // unsigned char  Reserved
  USHORT usTapeSeq;                     // Tape sequence number
  USHORT usSessionNum;                  // Tape session number
  ULONG  ulQFABlockNum;                 // QFA starting block number
  PTSZ   pszSessionPW;                  // Tape session password
  USHORT nNewTapeMethod;                // See New Tape Method // For UNIX this is the Range to Session Number
  USHORT nSpanTapeMethod;               // See Span Tape Method
  USHORT nNewTapeTimeout;               // New tape prompting timeout (# mins)
  USHORT nSpanTapeTimeout;              // Span tape prompting timeout (# mins)
  ULONG  nFilterItems;                  // # of Filter items in list
  PVOID  pvFilterList;                  // List of filters
  ULONG  nTapeItems;                    // # of ASTAPEITEM items in list
  PASTAPEITEM pASTapeItemList;          // List of ASTAPEITEM items
  USHORT usFileSystem;                  // See Device File System
  USHORT nInternalBuf;                  // Internal use only for packing Job in RS
  FLAG32 fOptions;                      // See ASDTAPE Options
  PTSZ   pszPath;                       // Session Path ie SYS: or SYS:\PUBLIC
  PTSZ   pszLocation;                   // Location of Tape ie VAULT
  PTSZ   pszMediaPoolName;              // Media Pool Name (NetWare) - new for v6.62
  PASDTAPEX pASDTapeX;                  // Extended tape structure - new for v6.62
} ASDTAPE;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASDTAPE FAR  *PASDTAPE;
typedef ASDTAPE NEAR *NPASDTAPE;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASDTAPE FAR  *PASDTAPE;
typedef ASDTAPE NEAR *NPASDTAPE;
#else
typedef ASDTAPE *PASDTAPE;
#endif


//
//  ASAPGROOM Options
//
#define ASAPGO_GROOM          0x00000002 // Enable grooming
#define ASAPGO_MIGRATE        0x00000004 // Enable migrating

// GROOM operation return codes
#define ASAP_COMPLETE           0
#define ASAP_INCOMPLETE         1
#define ASAP_FAILED             2

//
// Unix Job Mask values
//
#define USE_DMW  0  // D = Daily M = Monthly W = Weekly
#define USE_MWD  1  // M = Monthly W = Weekly D = Daily - added 10/27 James
#define USE_GFS  2  // G = GrandFather F = Father S = Son

#ifndef _A_EXECUTE
#define _A_EXECUTE          0x08 /* Execute only file                        */
#endif

#ifndef _A_VOLID
#define _A_VOLID            0x08 /* Volume ID entry (SAME as EXECUTE)        */
#endif

#ifndef _A_SHARE
#define _A_SHARE            0x80 /* Used for compatibility with DOS software */
#endif

//
//  Node Type
//
#define QJNT_SERVER           0  // The node is a server
#define QJNT_WSPC             1  // The node is a PC workstation
#define QJNT_WSMAC            2  // The node is a MAC workstation
#define QJNT_WSUNIX           3  // The node is a UNIX workstation
#define QJNT_MSNET            4  // The node is a MS Net workstation
#define QJNT_WSRPC            5  // The node is a remote drive
#define QJNT_WSNT             6  // The node is a NT workstation
#define QJNT_DOMAIN           7  // The node is a domain
#define QJNT_NWAGENT          8  // The node is a NW server (use NWagent)
#define QJNT_NTAGENT          9  // The node is a NT machine (use NTagent)
#define QJNT_OS2AGENT        10  // The node is a OS/2 machine (use OS2agent)
#define QJNT_WIN95AGENT      11  // The node is a Win95 machine (use W95agent)
#define QJNT_WINAGENT        12  // The node is a Win3x machine (use W3Xagent)
#define QJNT_NDS             13  // The node is an NDS node. ASNW uses number 6
#define QJNT_DOSAGENT        14  // CUI The node is a dos machine (use DOSagent)
#define QJNT_VMSAGENT        15  // The node is a VMS machine (use VMSagent)
#define QJNT_AS400AGENT      16  // The node is a AS400 machine (use AS400agent)
#define QJNT_MACOSXAGENT     17  // The node is a MAC OS X machine (use MACOSXagent)
#define QJNT_FTP             20  // The node is FTP.
#define QJNT_OPTICAL         21  // The node is an optical platter
#define QJNT_SYBASE          21  // Sybase Node --NEW IN 6.0
#define QJNT_ORACLE          22  // Oracle Node -- NEW in 6.0
#define QJNT_INFORMIX        23  // Informix Node -- NEW in 6.0
#define QJNT_TAPE            24  // Tape Node -- NEW in 6.0
#define QJNT_RESTORE         25  // Restore Node -- NEW in 6.0
#define QJNT_SNAPSHOT        26  // PLC SnapShot agent
#define QJNT_DBRESTORE       27  // Database Restore Node
#define QJNT_NASNODE	     28  // NAS node represented by a NAS agent
#define QJNT_SYMHOSTNODE	 29  // Symmetrix node represented by a SYM agent
#define QJNT_NETAPPNODE		 30  // NAS Net-App Server Node

#define QJNT_PROCOMNODE		 31  // NAS Procom Server Node

#define QJNT_EMCNODE		 32  // NAS Procom Server Node
#define QJNT_GROUPWISE 		 33  // NetWare GroupWise
#define QJNT_CELERRANODE     34
#define QJNT_HPNODE          35
#define QJNT_XCHGSIS         36	 // Exchange push agent node type
#define QJNT_XCHGDB			 37	 // Exchange DB level push agent node type

#define QJNT_DRNTAG          40  // The node is a 4.0 NTAGENT which supports remote DR
#define QJNT_BLUEARCNODE	 41

#define QJNT_MSSQL			 44

#ifdef NEW_ORACLE
#define QJNT_ORACLERMAN		 46	 // New oracle rman node(will make sure this value is same as Unix Server later).
#endif //NEW_ORACLE

#ifdef BEB_AGENT_SHAREPOINT
#define QJNT_SHAREPOINT      47 // SharePoint Agent node type
#define QJNT_SHAREPOINT_WSS  48
#define QJNT_SHAREPOINT_SSO  49
#define QJNT_SHAREPOINT_SI   50
#define QJNT_SHAREPOINT_DB   51
#endif // BEB_AGENT_SHAREPOINT

#define QJNT_XCHGDBVSS		 52 // wu$br01,June 8,2006, E12 agent, Oriole

#ifdef WANSYNC_SUPPORT
#define QJNT_WANSYNC		 53	// XOSoft Wansync node
#endif
#ifdef VMWARE_SUPPORT
#define QJNT_VMWARE			 54	// VMWARE VCB proxy node
#endif
#define QJNT_ASDB_MSSQLEXP	 56

// maoja01 add begin
//#ifdef SPS_2007_CHANGES
#define QJNT_SPSAGENT_2007	 57	// MS Share point 2007 Agent 
//#endif //SPS_2007_CHANGES
// maoja01 add end

#ifdef R12V_VM_TASKS
#define QJNT_MSVM			 58 // Microsoft Hyper-V Machine
#endif// R12V_VM_TASKS

#ifdef E14_TASK
#define QJNT_XCHGSERVER		59		// E14 exchange server. Standalone server or DAG.
#define QJNT_XCHGDBLVLVSS	60		// E14 DB level agent node (VSS).
#define QJNT_XCHGDOCLVL		61		// E14 Doc level agent node.
#endif //E14_TASK

//
//  Node Operating System Type
//
#define QJNOST_UNKNOWN       0          // The operating system is Unknown
#define QJNOST_DOS           1          // The operating system is DOS
#define QJNOST_OS2           2          // The operating system is OS/2
#define QJNOST_NETWARE_BIND  3          // The operating system is NetWare Bindery
#define QJNOST_NETWARE_DS    4          // The operating system is NetWare Directory Services
#define QJNOST_WINDOWS       5          // The operating system is Windows
#define QJNOST_UNIX          6          // The operating system is UNIX
#define QJNOST_MAC           7          // The operating system is Macintosh
#define QJNOST_NT            8          // The operating system is NT
#define QJNOST_MYCOMPUTER    9          // This is really My Computer
#define QJNOST_PREFERSHARE  10          // This is under preferred shares
#define QJNOST_PREFERMACH   11          // This is under preferred machine
#define QJNOST_VMS			12			// The operating system is VMS
#define QJNOST_OS400		13			// The operating system is OS400

//
//  Node Operating System Version
//
#define QJNOSV_MSDOS33       "MSDOS 3.3"       // Microsoft DOS 3.3
#define QJNOSV_MSDOS50       "MSDOS 5.0"       // Microsoft DOS 5.0
#define QJNOSV_OS213         "IBM OS/2 1.3"    // IBM OS/2 1.3
#define QJNOSV_OS220         "IBM OS/2 2.0"    // IBM OS/2 2.0
#define QJNOSV_NW215         "NETWARE 2.15"    // Novell NetWare 2.15
#define QJNOSV_NW311         "NETWARE 3.11"    // Novell NetWare 3.11
#define QJNOSV_NW400         "NETWARE 4.00"    // Novell NetWare 4.00
#define QJNOSV_NW500         "NETWARE 5.00"    // Novell NetWare 5.00
#define QJNOSV_WIN31         "WINDOWS 3.10"    // Microsoft Windows 3.10
#define QJNOSV_WINNT100      "WINDOWS NT 3.10" // Microsoft Windows NT 3.10
#define QJNOSV_WINNT351      "WINDOWS NT 3.51" // Microsoft Windows NT 3.51
#define QJNOSV_WINNT400      "WINDOWS NT 4.00" // Microsoft Windows NT 4.00
#define QJNOSV_WIN95         "WINDOWS 95"      // Microsoft Windows 95
#define QJNOSV_WIN98         "WINDOWS 98"      // Microsoft Windows 98

//
//  Device Type
//
#define QJDT_DISK            0          // The device is a server disk
#define QJDT_TAPE            1          // The device is a server tape
#define QJDT_WSDISK          2          // The device is a workstation disk
#define QJDT_WSTAPE          3          // The device is a workstation tape
#define QJDT_DSA             4          // The device is a data storage agent

//
//  ASNODE Options in fOptions
//
#define QJNO_MOVESYSOBJECTS   0x00000001 // Move system objects (ie NW Bindery)
#define QJNO_CLEARALLCONN     0x00000002 // Clear all connections and disable logins
#define QJNO_TRAVSYMLINK      0x00000004 // UNIX - Traverse symbolic link, else record it
#define QJNO_INCSHARABLEVOLS  0x00000008 // MAC  - Include Sharable volumes, else only local HD
#define QJNO_AUTOEXPAND       0x00000010 // NT   - Check the list of volumes in case any have been added
#define QJNO_TRAVSNFS         0x00000010 // UNIX - Traverse NFS mount direct
#define QJNO_RESETACCESSTIME  0x00000020 // UNIX - ResetAccess time on backup
#define QJNO_NOESTIMATE       0x00000040 // UNIX - No estimate on backup
#define QJNO_NOTSETUID        0x00000080 // UNIX - Don't set uid on backup
#define QJNO_NOTACROSSFS      0x00000100 // UNIX - Don't traverse across file
#define QJNO_POINT_TIME_UNIX  0x00000400 // UNIX -
#define QJNO_CHECK_POINT_UNIX 0x00000800 // UNIX -

#define QJNO_SKIP_DELAY       0x00001000 // Skip wait the time specified by user if is seted
#define QJNO_SKIP_JOB         0x00002000 // Skip  jog if is seted
#define QJNO_SKIP_POST        0x00004000 // Skip Post application specified by user if is seted (Don't Start it!)

//image options for backup
#define QJNO_IMAGE            0x00010000 // Enable image option for all drives
#define QJNO_INCSTCHECK       0x00020000 // Check for drive inconsistencies
#define QJNO_INCSTSTOP        0x00040000 // Stop when drive inconsistencies detected
#define QJNO_PROMPT           0x00080000 // Prompt if opened files on volume
#define QJNO_SKIPFILE         0x00100000 // Skip file scanning
#define QJNO_RECORDDB         0x00200000 // Enable database recording

#define QJNO_GLOBAL_VIEW      0x00400000 // Used to mark source nodes that are generated from Exchange Global View

#define QJNO_CURRENTHOST      0x01000000 // GUI Internal staff to know if is Current ArcServe Host
#define QJNO_SKIP_POST_FAIL   0x02000000 // Skip post command if fails
#define QJNO_SKIP_POST_INCMP  0x04000000 // Skip post command if incomplete
#define QJNO_SKIP_POST_CMP    0x08000000 // Skip post command if complete

#define QJNO_MAKEUP           0x02000000 // Turn on makeup - 6.0x addon
#define QJNO_ISLOCAL          0x00000200 // UNIX - it is local backup - added 10/15/97
#define QJNO_DISABLELOGINS    0x00008000 // CUI
#define QJNO_FULL_W_AGENT     0x10000000 // Full node backup with agent info

// Misc.
#define QJNO_EXTENDED         0x20000000 // Include extended structure
#define QJNO_INTERNAL2        0x40000000 // Reserved Bit for attended mode (QJNO_SKIP       in astasks.h)
#define QJNO_SKIP			  0x40000000
#define QJNO_INTERNAL1        0x80000000 // Reserved Bit for attended mode (QJNO_LOCALALLOC in astasks.h)


//
//  ASNODE Options extension in fOptionsEx
//
#define QJNO_ENCRYPTFILES		0x0001		// Encrypt each file onto tape
#define QJNO_COMPRESSFILES		0x0002		// Compress each File onto tape
#define QJNO_EX_TRUSTAUTH		0x0008		// Using GUID authentication	//R12V_VM_UnivAg

//<XUVNE01> 2008-01-29
//BOOL isASNodeUnicode(PASNODE pAsnode)
#define QJNO_UNICODE 0x0004
#define isASNodeUnicode(pAsnode) ((pAsnode)?(pAsnode->fOptionsEx & QJNO_UNICODE):0)
#define setASNodeUnicode(pAsnode)(pAsnode->fOptionsEx |= QJNO_UNICODE)

// daist01 3/19/2008
#define clearASNodeUnicode(pAsnode)	do {	\
	if ((pAsnode) != NULL)	(pAsnode)->fOptionsEx &= ~ QJNO_UNICODE;	\
}while(0)


// ASNODE options Extensions. Used only between Back end to Agent
#define QJNXO_TRAVSYMLINK		0x00000010	// Traverse Sym Links
#define QJNXO_MTPTSINSESSION	0x00000020	// Backup Volume Mount Pts in Sess
#define QJNXO_TRAVHARDLINKS		0x00000040  // Traverse Hard Links

// Used by backend itself
#define QJNXO_PROXYLOCAL		0x00000080  // The node is a proxy of local node

// Used in the VI support
#define QJNXO_FORCEVI			0x00000100  // Attempt VI connection

#define QJNXO_NODEALLOCLOCAL	0x00000200	// The ASNODE structure is allocated locally
#define QJNXO_USE_IMG03			0x00000400	//Use the new image structure

#ifdef FILTER_COMBINE
#define QJNXO_FILTER_COMBINE	0x00000800	// combine global filter to node filter if they are not conflicting.
#endif // FILTER_COMBINE

//updated SJT 04/03/03
#define	QJNXO_VSS_USE					0x00010000
#define	QJNXO_VSS_REVERT				0x00020000
#define	QJNXO_VSS_EXCLUDE_INCLUDES		0x00040000
#define	QJNXO_VSS_EXCLUDE_EXCLUDES		0x00080000
#define	QJNXO_VSS_ON_FAIL				0x00100000
#define QJNXO_ADR_PARTIAL_NODE			0x00200000	// ADR partial node option.

#define QJNXO_AV_SCAN_ARCHIVE           0x00400000
#define QJNXO_CUSTOM_MAKEUP				0x00800000	// Exchange push agent need to know custom makeup job status.

#define QJNXO_BACKUP_CATALOG			0x01000000  // Flag set if Catalog DB needs to be backed up
#define QJNXO_BACKUP_JOBQUEUE			0x02000000  // Flag set if Job Queue directory needs to be backed up
#define QJNXO_BACKUP_ASDB               0x04000000
#define QJNXO_BACKUP_LOCALDR            0x08000000  // Flag set if it's Local DR backup job

//
//  Skip Weekdays - Skip the week days specified for a repeating job
//
#define QJSWD_SUN            0x0001    // Skip Sunday
#define QJSWD_MON            0x0002    // Skip Monday
#define QJSWD_TUE            0x0004    // Skip Tuesday
#define QJSWD_WED            0x0008    // Skip Wednesday
#define QJSWD_THU            0x0010    // Skip Thursday
#define QJSWD_FRI            0x0020    // Skip Friday
#define QJSWD_SAT            0x0040    // Skip Saturday

//
//  Every Units - unit stored in nMonths, number stored in nDays
//
#define QJEVU_MINUTE         0x0000    // Minute(s)
#define QJEVU_HOUR           0x0001    // Hour(s)
#define QJEVU_DAY            0x0002    // Day(s)
#define QJEVU_WEEK           0x0003    // Week(s)
#define QJEVU_MONTH          0x0004    // Month(s)

//
//  Day(s) Of Week - Run on the days specified for a repeating job
//
#define QJDOW_SUN            0x0001    // Sunday
#define QJDOW_MON            0x0002    // Monday
#define QJDOW_TUE            0x0004    // Tuesday
#define QJDOW_WED            0x0008    // Wednesday
#define QJDOW_THU            0x0010    // Thursday
#define QJDOW_FRI            0x0020    // Friday
#define QJDOW_SAT            0x0040    // Saturday

//
//  Week(s) Of Month - Run on the weeks specified for a repeating job
//
#define QJWOM_1ST            0x0001    // First week
#define QJWOM_2ND            0x0002    // Second week
#define QJWOM_3RD            0x0004    // Third week
#define QJWOM_4TH            0x0008    // Fourth week
#define QJWOM_LAST           0x0010    // Last week

//
//  Day for Week(s) of Month
//
#define QJWOMD_SUN           0x0000    // Sunday
#define QJWOMD_MON           0x0001    // Monday
#define QJWOMD_TUE           0x0002    // Tuesday
#define QJWOMD_WED           0x0003    // Wednesday
#define QJWOMD_THU           0x0004    // Thursday
#define QJWOMD_FRI           0x0005    // Friday
#define QJWOMD_SAT           0x0006    // Saturday

//
//  Schedule Mode
//
#define QJSM_CUSTOM          0x0000    // Custom configurable (ARCserve 5.x)
#define QJSM_EVERY           0x0001    // Every x months/days/hours/minutes
#define QJSM_DOW             0x0002    // Day(s) of Week
#define QJSM_WOM             0x0003    // Week(s) of Month
#define QJSM_DOM             0x0004    // Day of Month
#define QJSM_ONCE            0xFFFF    // Only run once
#define QJSM_RUNNOW			 0x0008	   // Run the job temporarily as RunNOW and do not reschedule after execution

typedef struct tagASFILTSCHED           // New in Sniper
{
  ULONG  dtSubmitted;                   // Exception Date
  USHORT nInclExcl;                     // See Include/Exclude
  UCHAR  uBackupMethod;                 // Daily backup method
  UCHAR  aucReserve;                    // Reserved for Future use
  ULONG  dtExecuteTime;                 // Exception Execute Time ( hour & minute )
} ASFILTSCHED;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASFILTSCHED FAR  *PASFILTSCHED;
typedef ASFILTSCHED NEAR *NPASFILTSCHED;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASFILTSCHED FAR  *PASFILTSCHED;
typedef ASFILTSCHED NEAR *NPASFILTSCHED;
#else
typedef ASFILTSCHED *PASFILTSCHED;
#endif

typedef struct tagASSCHED
{
  ULONG dtSubmitted;                   // Job submit date & time
  ULONG dtExecuteOn;                   // Schedule job at this date & time
  USHORT nMonths;                      // Reschedule job every n Months
  USHORT nDays;                        // Reschedule job every n Days
  USHORT nHours;                       // Reschedule job every n Hours
  USHORT nMinutes;                     // Reschedule job every n Minutes
  FLAG16 fSkipWeekdays;                // See Skip Weekdays
  USHORT nExecTimes;                   // # of job execute times
  PULONG pulExecTimes;                 // List of execute times
  ULONG  nExclTimes;                   // # of job exclude times
  PULONG pulExclTimes;                 // List of exclude times
  USHORT usScheduleMode;               // Schedule Mode
  USHORT nFilterSchedItems;            // # of Filter items in list              - New in Sniper
  PASFILTSCHED pFilterSched;           // List of filters in Schedule            - New in Sniper
  ULONG  dtRescheduleTime;             // Reschedule the time in case of failure - New in Sniper
  // linku02: Due to the new variable ulFullJobID the structure size is changed and caused
  // functionality issues. Comment out the following line to retain the structure size
  //UCHAR  aucReserved[4];               // Reserved for future use
  ULONG  ulFullJobID;				   // Full backup jobID used by Custom scheduled incremental and differential jobs for UNIX host. Add by wu$yu02, Aug.26, 2004
} ASSCHED;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASSCHED FAR  *PASSCHED;
typedef ASSCHED NEAR *NPASSCHED;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASSCHED FAR  *PASSCHED;
typedef ASSCHED NEAR *NPASSCHED;
#else
typedef ASSCHED *PASSCHED;
#endif

//
//  AutoPilot Options
//
#define QJAPO_RETRYMISSED    0x00000001 // Retry missed targets
#define QJAPO_RECYCLEWEEKLY  0x00000002 // Recycle weekly tapes
#define QJAPO_GROOM          0x00000004 // Enable grooming
#define QJAPO_APPENDTAPES    0x00000008 // Append to Tapes  - New in Sniper
#define QJAPO_RUNNOW		 0x00000010 // Run the job temporarily as RunNOW and do not reschedule after execution

#ifdef MAKEUP_OF_MAKEUP
#define QJAPO_MAKEUP_RUN_AT					0x00000020 // Makeup Run at specific time
#define QJAPO_MAKEUP_RUN_AFTER_JOB_END		0x00000040 // Makeup Job Run at job end time + # minutes
#endif //MAKEUP_OF_MAKEUP

//
//  AutoPilot Backup Methods
//
#define QJAPM_GRANDFATHER    0         // Grandfather (Daily Full Backup)
#define QJAPM_ARCHBITINCR    1         // Incremental (Archive Bit)
#define QJAPM_ARCHBITDIFF    2         // Differential (Archive Bit)
#define QJAPM_MODDATEDIFF    3         // Differential (Last Mod Date)
#define QJAPM_ROTATION       4         // Simple Tape Rotation - New in Sniper
#define QJAPM_LEVEL          4         // Level (Last Mod Date)

//
//  Daily Backup Methods (specify for each Method Weekday (aucMethodWeek[])
//
#define QJAPD_OFF            0         // No backup for the day
#define QJAPD_FULL           1         // Full backup
#define QJAPD_DIFFMODDATE    2         // Differential (Last Mod Date)
#define QJAPD_DIFFARCHBIT    3         // Differential (Archive Bit)
#define QJAPD_INCRARCHBIT    4         // Incremental (Archive Bit)
#define QJAPD_WEEKLY         5         // Full Weekly Backup  - New in Sniper
#define QJAPD_MONTHLY        6         // Full Monthly Backup - New in Sniper
#define QJAPD_INCRMODDATE    7         // Incremental (Modified ) Unix

// AutoPilot Methods
#define  AP_OFFDAY          0
#define  AP_FULL            1
#define  AP_DIFFERENTIAL    2
#define  AP_INCREMENTAL     4
#define  AP_INCRMODDATE     7

//
//  nPadding1 : used for non-AutoPilot level backup
//
#define QJAPD_LEVEL_0       QJAPD_FULL // Level 0 (Full backup)
#define QJAPD_LEVEL_1       11         // Level 1
#define QJAPD_LEVEL_2       12         // Level 2

//Custom Schedule values for Unix
enum skipWeekDays { Sunday = 01, Monday=02, Tuesday=04, Wednesday=010,
        Thursday=020, Friday=040, Saturday=0100};
//
//  Method Weekdays
//
#define QJMD_SUN             0         // Sunday
#define QJMD_MON             1         // Monday
#define QJMD_TUE             2         // Tuesday
#define QJMD_WED             3         // Wednesday
#define QJMD_THU             4         // Thursday
#define QJMD_FRI             5         // Friday
#define QJMD_SAT             6         // Saturday

//JobUnit types from Unix
#define APO_APPEND           0
#define APO_OVERWRITE        1

#ifdef _B2D2T
#define ROTATION_JOB_BACKUP_TO_TAPE		0 //used in ASAPMETHOD::cBackupDevice to indicate backup to tape
#define ROTATION_JOB_BACKUP_TO_DISK		1 //used in ASAPMETHOD::cBackupDevice to indicate backup to disk
#endif //_B2D2T


typedef struct tagASAPMETHOD
{
  ULONG ulExecuteTime;                 // Execution Time in Calendar Format
  char  cMethod;                       // See Daily Backup Method
  char  cReserved;                     // Reserved
#ifndef _B2D2T
  short sReserved;
#else //_B2D2T
  char	sBackupDevice;					//Shall be used to indicate if the backup has to happen to disk or to tape. This is
										//specifically useful in B2D2T scenarios where on some days backup happens to disk
										//and on other days backup happens to tape.
#endif //_B2D2T
} ASAPMETHOD;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASAPMETHOD FAR  *PASAPMETHOD;
typedef ASAPMETHOD NEAR *NPASAPMETHOD;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASAPMETHOD FAR  *PASAPMETHOD;
typedef ASAPMETHOD NEAR *NPASAPMETHOD;
#else
typedef ASAPMETHOD *PASAPMETHOD;
#endif

// AutoPilot Export
#define	AP_EXPORT_DAILY_ID		0		// Daily Incremental & Differential
#define	AP_EXPORT_DAILY_FULL	1		// Daily Full
#define AP_EXPORT_WEEKLY		2		// Weekly
#define AP_EXPORT_MONTHLY		3		// Monthly

typedef struct tagASAPINFO
{
  PTSZ   pszSetName;                   // Set Name
  PTSZ   pszSetPW;                     // Set Password
  USHORT nAPMethod;                    // AutoPilot Backup Methods
  USHORT nPadding1;                    // Reserved for padding
  USHORT nPadding2;                    // Reserved for padding2
  USHORT nASAPMethod;                  // # of AP Backup Method in pASAPMethod
  PASAPMETHOD  pASAPMethod;            // See ASAPMETHOD structure defines Backup Methods for AP job - New in Sniper
  USHORT nSaveMonthly;                 // # months (12) to preserve monthly tapes
  USHORT nSaveWeekly;                  // # weeks (5) to preserve weekly tapes
  USHORT nSaveDaily;                   // # days (7) to preserve daily tapes
  USHORT nPadding3;                    // Reserved for padding
  ULONG  ulUnixAPtime;                 // used by increment/diferencial Uagent backup job
  UCHAR  DumpLevel;					   // Dump Level as required for NAS dump backups
  UCHAR  aucInternal[3];               // Reserved for internal use
  UCHAR	 aucExport[4];				   // Export flags
  FLAG32 fOptions;                     // See AutoPilot Options
  USHORT nMonths;                      // # AP backup months before grooming
  USHORT nCopies;                      // # safe copies before grooming
  PTSZ   pszMediaPoolName;             // Media Pool Name to be used         - New in Sniper
  PTSZ   pszSchemeName;                // Rotation Scheme Name
  ULONG  dtDefaultExecuteTime;         // Default Execute Time in Calendar Format
} ASAPINFO;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASAPINFO FAR  *PASAPINFO;
typedef ASAPINFO NEAR *NPASAPINFO;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASAPINFO FAR  *PASAPINFO;
typedef ASAPINFO NEAR *NPASAPINFO;
#else
typedef ASAPINFO *PASAPINFO;
#endif

typedef struct tagASFAXINFO
{
  PTSZ pszServerName;                  // Remote FAX host server
  PTSZ pszUserName;                    // Remote user (login) name
  PTSZ pszUserPW;                      // Remote user (login) password
  PTSZ pszFaxNumber;                   // FAX phone number
  PTSZ pszCoverPageFile;               // PCX filename of cover page
} ASFAXINFO;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASFAXINFO FAR  *PASFAXINFO;
typedef ASFAXINFO NEAR *NPASFAXINFO;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASFAXINFO FAR  *PASFAXINFO;
typedef ASFAXINFO NEAR *NPASFAXINFO;
#else
typedef ASFAXINFO *PASFAXINFO;
#endif

#define BUFLEN_FAXSERVERNAME    48
#define BUFLEN_FAXUSERNAME      48
#define BUFLEN_FAXPASSWORD      48
#define BUFLEN_FAXNUMBER        46
#define BUFLEN_COVERPAGEFILE    128

   //Email Types
#define  ASE_INTERNET_EMAIL 0
#define  ASE_MHS_EMAIL      1

typedef struct tagASEMAILINFO
{
  PTSZ pszServerName;                   // Remote Email host server
  PTSZ pszUserName;                     // Remote user (login) name
  PTSZ pszUserPW;                       // Remote user (login) password
  PTSZ pszMHSDir;                       // MHS home directory
  PTSZ pszMHSId;                        // MHS host and workgroup name
  PTSZ pszMHSUserName;                  // MHS user name
} ASEMAILINFO;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASEMAILINFO FAR  *PASEMAILINFO;
typedef ASEMAILINFO NEAR *NPASEMAILINFO;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASEMAILINFO FAR  *PASEMAILINFO;
typedef ASEMAILINFO NEAR *NPASEMAILINFO;
#else
typedef ASEMAILINFO *PASEMAILINFO;
#endif

#define BUFLEN_EMAILSERVERNAME  49
#define BUFLEN_EMAILUSERNAME    49
#define BUFLEN_EMAILPASSWORD    49
#define BUFLEN_MHSDIR           129
#define BUFLEN_MHSID            600
#define BUFLEN_MHSUSERNAME      600

typedef struct tagASPRINTQINFO
{
  PTSZ pszServerName;                   // Remote Print Queue host server
  PTSZ pszUserName;                     // Remote user (login) name
  PTSZ pszUserPW;                       // Remote user (login) password
  PTSZ pszQueueName;                    // Print queue name
} ASPRINTQINFO;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASPRINTQINFO FAR  *PASPRINTQINFO;
typedef ASPRINTQINFO NEAR *NPASPRINTQINFO;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASPRINTQINFO FAR  *PASPRINTQINFO;
typedef ASPRINTQINFO NEAR *NPASPRINTQINFO;
#else
typedef ASPRINTQINFO *PASPRINTQINFO;
#endif

#define BUFLEN_PRINTQSERVERNAME 48
#define BUFLEN_PRINTQUSERNAME   600
#define BUFLEN_PRINTQPASSWORD   48
#define BUFLEN_QUEUENAME        600

// CUI end

//
//   Open File Actions
//
#define ASOFA_SKIP              0x00000000 // Skip open file
#define ASOFA_RETRYNOW          0x00000001 // Retry immediately (use retry counts)
#define ASOFA_RETRYEOS          0x00000002 // Retry at end of session

//
//   Open File Methods
//
#define ASOFM_DENYNONEIFDWF     0x00000000 // Use Deny None if Deny Write Fails
#define ASOFM_DENYNONEONLY      0x00000001 // Only use Deny None
#define ASOFM_DENYWRITEONLY     0x00000002 // Only use Deny Write
#define ASOFM_LOCKMODEIFDWF     0x00000004 // Use Lock Mode if Deny Write Fails

typedef struct tagASOFINFO
{
  FLAG32 fActions;                     // See Open File Actions
  FLAG32 fMethods;                     // See Open File Methods
  USHORT nRetryCount;                  // # of Retries
  USHORT nRetryInterval;               // Retry Interval (in seconds)
  UCHAR  aucReserved[16];              // Reserved for future use
} ASOFINFO;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASOFINFO FAR  *PASOFINFO;
typedef ASOFINFO NEAR *NPASOFINFO;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASOFINFO FAR  *PASOFINFO;
typedef ASOFINFO NEAR *NPASOFINFO;
#else
typedef ASOFINFO *PASOFINFO;
#endif

//
//  Job Type
//
#define QJT_DISKBACKUP       0         // Backup to Disk or Copy
#define QJT_TAPEBACKUP       1         // Backup to Tape
#define QJT_APDISKBACKUP     2         // Backup to Disk w/ AutoPilot
#define QJT_APTAPEBACKUP     3         // Backup to Tape w/ AutoPilot
#define QJT_APDISKMAKEUP     4         // Makeup backup to Disk w/ AutoPilot
#define QJT_APTAPEMAKEUP     5         // Makeup backup to Tape w/ AutoPilot
#define QJT_DISKRESTORE      6         // Restore from Disk
#define QJT_TAPERESTORE      7         // Restore from Tape
#define QJT_MERGETAPE        8         // Merge Tape to AS DB
#define QJT_DBBACKUP         9         // Selective DB Backup
#define QJT_DBRESTORE        10        // Selective DB Restore
#define QJT_COMPARE          11        // Compare Disk to Tape
#define QJT_COUNT            12        // Count - Attended
#define QJT_PURGE            13        // Purge - Attended
#define QJT_SCANTAPE         14        // Scan Tape - Attended
#define QJT_DISKGROOM        15        // Disk Grooming
#define QJT_TAPECOPY         16        // Tape to Tape Copy
#define QJT_DBRECOVER        17        // Recover AS DB - Attended
// following 2 job types are same but 1 is for NT other for Netware
#define QJT_DBPRUNE          18        // Prune AS DB - UnAttended (NT)
#define QJT_TAPECLEANING     18        // Tape Cleaning job type (NetWare)
// Why are these the same?
#define QJT_JOBSTATUS        19        // Job Status
#define QJT_VIRUSSCAN        19        // Virus Scan - Attended
#define QJT_ROTATIONTAPEBACKUP 20      // Backup to Tape w/ Rotation
#define QJT_ROTATIONTAPEMAKEUP 21      // Makeup to Tape w/ Rotation
#define QJT_CONVERSIONJOB    22        // Conversion Job for NetWare
#define QJT_LITEBACKUP       23        // Backup ARCserve home directory and system directory
#define QJT_LITERESTORE      24        // Restore ARCserve home directory and system directory
#define QJT_NNPRESTORE       25        // Restore of None Netware Partition
#define QJT_HSM              26        // ARCserve HSM
#define QJT_MERGEDB          27        // Merge ARCserve DB to ARCserve DB
#define QJT_COPYTOMVS        28        // Copy Media to MBO (MVS)
#define QJT_VERIFY           29        // Verify job for Unix
#define QJT_DBA_BACKUP       30        // DB Agent Backup
#define QJT_DBA_SAP          31        // DB Agent SAP Backup
#define QJT_BACKUPSESSIONCDR 32		   //Backup OBDR session cdr file (Netware)
#define QJT_RESTORESESSIONCDR 33	   //Restore OBDR session cdr file (Netware)
#define QJT_NDMPBACKUP            34     // Backup using NDMP protocol
#define QJT_NDMPAPTAPEBACKUP      35     // Backup to Tape w/ AutoPilot using NDMP protocol
#define QJT_NDMPAPTAPEMAKEUP      36     // Makeup backup to Tape w/ AutoPilot using NDMP protocol
#define QJT_NDMPROTATIONTAPEBACKUP    37 // Backup to Tape w/ Rotation using NDMP protocol
#define QJT_NDMPROTATIONTAPEMAKEUP    38 // Makeup to Tape w/ Rotation using NDMP protocol
#define QJT_NDMPTAPERESTORE       39      // Restore from Tape using NDMP protocol
#define QJT_ASM_BACKUP			40		// ASM backup
#define QJT_ASM_RESTORE			41		// ASM restore
#define QJT_ASM_MERGE			42		// ASM merge
#define QJT_NDMPMERGETAPE		43		// These values are used by the UNIX product for NAS. Not used by NT
#define QJT_NDMPSCANTAPE		44		// These values are used by the UNIX product for NAS. Not used by NT
#define QJT_DEVICE_MANAGEMENT   45		// Device Management
#define QJT_GENERICJOB          46      // Generic job
//#define QJT_DMJOB_FORMAT      46
//#define QJT_DMJOB_ERASE       47
#define QJT_DATA_MIGRATION		48		//Data-Migration Job for a B2D2T job.
#define QJT_DATA_PURGE			49		//Data-Purge Job for a B2D job or B2D2T job.


#define QJT_COMMANDLINE       50
#define QJT_SLAVEJOB		  51      // ForMultiprocessing

#define QJT_DATA_MIGRATION_CPM	54		//Data-Migration Job for a cpm B2D2T job.
#define QJT_DATASTAGING_CPM		55		//Data-Staging Job for a cpm B2D2T job.
//////////////////////////////////////////////////////////////////////////
#define QJT_ASDB_PROTECTION_JOB	56

#define QJT_SCAN_ADVANCED		57		//Wanhe05_DeDupe_SCAN

//
//  Job Status
//
#define QJS_READY            0         // Job is ready to be processed
#define QJS_HOLD             1         // Job is on hold
#define QJS_ACTIVE           2         // Job is being executed
#define QJS_DONE             3         // Job is done
#define QJS_NONE             4         // Job isn't found

//
//  Data Transfer Global Options
//
#define QJDTO_NOASDBREC         0x00000001 // Disable AS DB recording
#define QJDTO_FAXRPTFILE        0x00000002 // Fax Report File
#define QJDTO_EMAILRPTFILE      0x00000004 // Email Report File
#define QJDTO_PRINTRPTFILE      0x00000008 // Print Report File
#define QJDTO_FULLDTLRPTFILE    0x00000010 // Include all msg info in report file
#define QJDTO_CLEARALLCONN      0x00000020 // Clear all connections and disable logins
#define QJDTO_DISABLELOGINS     0x00000040 // Disable logins
#define QJDTO_NORPTFILE         0x00000040 // Disable Report File
#define QJDTO_DISASTERRECOVERY  0x00000100 // Prepare for disaster recovery (Netware)
#define QJDTO_BACKUPCOMPFILES   0x00000200  // Backup compressed files as uncompressed (netware)
#define QJDTO_CRCCHECK          0x00000400 // Do CRC check for entire job
#define QJDTO_SUPER_SUBMIT      0x00000800 // Job Submitter Supervisor Equivalent

#define QJDTO_G_FORCEDDEMIG     0x00001000 // Forced De-migration option
#define QJDTO_G_FORCEDDECOMP    0x00002000 // Forced Decompression option


#define QJDTO_SKIP_POST_FAIL    0x00000100 // Skip post command if fails
#define QJDTO_SKIP_POST_INCMP   0x00000200 // Skip post command if incomplete
#define QJDTO_SKIP_POST_CMP     0x00000400 // Skip post command if complete

#define QJDTO_SKIP_DELAY        0x00001000 // Skip wait the time specified by user if is seted
#define QJDTO_SKIP_JOB          0x00002000 // Skip  jog if is seted
#define QJDTO_SKIP_POST         0x00004000 // Skip Post application specified by user if is seted (Don't Start it!)
#define QJDTO_ON_EXITCODE		0x00008000  // On Exit Code is set

//
//   Data Transfer Backup Specific Options
//
#define QJDTO_B_MIGRATE         0x00010000 // Migrate files (delete source files)
#define QJDTO_B_COMPARE         0x00020000 // Compare files on tape to disk
#define QJDTO_B_VERIFY          0x00040000 // Scan tape to verify proper layout
#define QJDTO_B_CLEARARCHBIT    0x00080000 // Clear archive bit
#define QJDTO_B_NOESTIMATE      0x00100000 // No estimate before backup
#define QJDTO_B_PARTIALASDBREC  0x00200000 // Record Job and Session Only.
#define QJDTO_B_NOARCSERVEDB    0x00400000 // Do not backup ARCserve DB
#define QJDTO_B_ENCRYPTFILES    0x00800000 // Encrypt each file onto tape
#define QJDTO_B_COMPRESSFILES   0x01000000 // Compress each File onto tape
//for sp3 QJDTO_B_VERIFY_CRC Modified by J.K.
#define QJDTO_B_CREATE_CRC      0x02000000 // Create CRC
#define QJDTO_B_VERIFY_CRC      (QJDTO_B_CREATE_CRC | QJDTO_B_VERIFY) // Verify tape with CRC

// Lower two are similar :watch out
#define QJDTO_B_ARCSERVEDB      0x04000000 // Always backup ARCserve DB at the end of job.
//Not to be used anymore.Instead use the ASQJDTEX->nCompareFirstMB.Avoid above conflict. :Chinta
//#define QJDTO_B_COMPARE_FIRSTNMB 0x04000000 // Compare first #MB of data (netware)

#define QJDTO_B_VIRSCANSKIP     0x08000000 // Virus Scan Skip file w/ INOCULAN
#define QJDTO_B_VIRSCANDELETE   0x10000000 // Virus Scan and Delete w/ INOCULAN
#define QJDTO_B_VIRSCANRENAME   0x20000000 // Virus Scan and Rename w/ INOCULAN
#define QJDTO_B_VIRSCANCURE     0x40000000 // Virus Scan and Cure: ASNT.70,chinta
#define QJDTO_B_EJECTTAPE       0x80000000 // Eject tape after job
#define QJDTO_B_COMPRESSFILES   0x01000000 // Compress each File onto tape
#define QJDTO_B_SCAN_TO_MBO     0x00000080 // backup to MBO after backup
#define QJDTO_B_INTERLEAVEDJOB  0x00000800 // Is this is a interleaved job ?
#define QJDTO_B_EJECTTAPEDEFAULT   0x00008000 // Default Eject tape value

//
//   Data Transfer Archiving Specific Options
//
#define QJDTO_A_NEWFILES        0x00010000 // Archive new files
#define QJDTO_A_MIRROR          0x00020000 // Mirror directories
#define QJDTO_A_MIGRATE         0x00040000 // Migrate files (delete source files)
#define QJDTO_A_CLEARARCHBIT    0x00080000 // Clear archive bit
#define QJDTO_A_COPYSECURITY    0x00100000 // Copy Security
#define QJDTO_A_CREATEEMPTYDIR  0x00200000 // Create empty directories
#define QJDTO_A_CREATEWHOLEPATH 0x00400000 // Create whole path for destination
#define QJDTO_A_CREATETOPDIRS   0x00800000 // Create top directories
#define QJDTO_A_NOTRUSTEES      0x01000000 // Don't return NW Trustees to dest.
#define QJDTO_A_USERSPACERES    0x02000000 // Preserve user space restrictions
#define QJDTO_A_DIRSPACERES     0x04000000 // Preserve directory space restrictions
#define QJDTO_A_VIRSCANSKIP     0x08000000 // Virus Scan Skip file w/ INOCULAN
#define QJDTO_A_VIRSCANDELETE   0x10000000 // Virus Scan and Delete w/ INOCULAN
#define QJDTO_A_VIRSCANRENAME   0x20000000 // Virus Scan and Rename w/ INOCULAN
#define QJDTO_A_VIRSCANCURE     0x40000000 // Virus Scan and Cure :ASNT.70 ,Chinta
#define QJDTO_A_AUTONAMING      0x80000000 // Autonaming destination directory for multi-source copy

//
//   Data Transfer Restore Specific Options
//
#define QJDTO_R_VIRSCANSKIP			0x00000100 // Virus Scan Skip file w/ INOCULAN
#define QJDTO_R_VIRSCANDELETE		0x00000200 // Virus Scan and Delete w/ INOCULAN
#define QJDTO_R_VIRSCANRENAME		0x00000400 // Virus Scan and Rename w/ INOCULAN
#define QJDTO_R_VIRSCANCURE			0x00000800 // Virus Scan and Cure :ASNT.70 ,Chinta
#define QJDTO_R_CREATEEMPTYDIRNEW	0x00001000 // Create empty directories
#define QJDTO_R_NOBUFFERING			0x00002000 // Open file handle with no buffering option
#define QJDTO_R_NOFILESECATTR		0x00004000 // Skip file security and attribute restore
#define QJDTO_R_WSCONTINUEALWAYS	0x00008000 // Continue WANSync session restore even connot stop scenario
#define QJDTO_R_CLEARARCHBIT		0x00080000 // Clear archive bit

#define QJDTO_R_SYSTEMFILES			0x00100000 // Restore system files
#define QJDTO_R_CREATEEMPTYDIR		0x00200000 // Create empty directories
#define QJDTO_R_CREATEWHOLEPATH		0x00400000 // Create whole path for destination
#define QJDTO_R_CREATETOPDIRS		0x00800000 // Create top directories
#define QJDTO_R_TRUSTEESONLY		0x00000400 // Trustees Restore only
#define QJDTO_R_NOTRUSTEES			0x01000000 // Don't return NW Trustees to dest.
#define QJDTO_R_USERSPACERES		0x02000000 // Preserve user space restrictions
#define QJDTO_R_DIRSPACERES			0x04000000 // Preserve directory space restrictions
#define QJDTO_R_DBRESTORE			0x08000000 // ARCserve Database restore
#define QJDTO_R_FILEOWNERONLY		0x10000000 // Restore based on file owner
#define QJDTO_R_ORGLOCATION			0x20000000 // Restore back to original location
#define QJDTO_R_EJECTTAPE			0x80000000 // Eject tape after job

//
//   Data Transfer Count Specific Options
//
#define QJDTO_C_VIRSCANSKIP     0x08000000 // Virus Scan Skip file w/ INOCULAN
#define QJDTO_C_VIRSCANDELETE   0x10000000 // Virus Scan and Delete w/ INOCULAN
#define QJDTO_C_VIRSCANRENAME   0x20000000 // Virus Scan and Rename w/ INOCULAN
#define QJDTO_C_VIRSCANCURE     0x40000000 // Virus Scan and Cure: ASNT.70 ,Chinta

//
//   Data Transfer Purge Specific Options
//
#define QJDTO_P_REMOVEDIRS      0x00010000 // Remove directories

//
//   Data Transfer Scan Tape Specific Options
//
#define QJDTO_S_NOTIFYEACHSESSION   0x00010000 // Notify each session
#define QJDTO_S_NOTIFYEACHDBSESSION 0x00020000 // Notify each DB session
#define QJDTO_S_VERIFY_CRC          0x00040000 // Scan tape contents with CRC

#ifdef Wanhe05_DeDupe_SCAN
#define QJDTO_S_SESSION_HEADER_ONLY	0x00080000 // Scan session header only. Conflicts with QJDTO_S_VERIFY_CRC and QJDTO_FULLDTLRPTFILE
#endif


//<XUVNE01> 2008-01-29
//BOOL isASQJDTUnicode(PASQJDT  pAsjqdt)

// All flags set to fOptionsXX should be defined here and begin with "QJDTOXX_" ==> 
#define QJDTOXX_UNICODE 0x01
#define UNICODE_SCRIPT_CONVERTED 0x02//this flag is just for UI and will not be packed it into jobscript
#define QJDTOXX_DDD_BACKUP 0x04   // Global option to backup the DDD files.
#define QJDT1OXX_UNICODE 0x0001     
#define QJDT1SOURCE_UNICODE 0x0010
#define QJDT1DEST_UNICODE 0x0100
// All flags set to fOptionsXX should be defined here and begin with "QJDTOXX_" <== 

//<XUVNE01> 2008-01-29
#define isASQJDT1Unicode(pAsjqdt1)		((pAsjqdt1)?(pAsjqdt1->fOptionsXX & QJDT1OXX_UNICODE):0)
#define setASQJDT1Unicode(pAsjqdt1) (pAsjqdt1->fOptionsXX |= QJDT1OXX_UNICODE)

#define isASQJDT1SourceUnicode(pAsjqdt1)	((pAsjqdt1)?(pAsjqdt1->fOptionsXX & QJDT1SOURCE_UNICODE):0)
#define setASQJDT1SourceUnicode(pAsjqdt1) (pAsjqdt1->fOptionsXX |= QJDT1SOURCE_UNICODE)

#define isASQJDT1DestUnicode(pAsjqdt1)		((pAsjqdt1)?(pAsjqdt1->fOptionsXX & QJDT1DEST_UNICODE):0)
#define setASQJDT1DestUnicode(pAsjqdt1) (pAsjqdt1->fOptionsXX |= QJDT1DEST_UNICODE)
//[End of] <XUVNE01> 2008-01-29

#define isASQJDTUnicode(pAsjqdt)	((pAsjqdt)?(pAsjqdt->fOptionsXX & QJDTOXX_UNICODE):0)
#define setASQJDTUnicode(pAsjqdt) (pAsjqdt->fOptionsXX |= QJDTOXX_UNICODE)

// daist01 3/19/2008
#define clearASQJDTUnicode(pAsjqdt)	do{	\
	if ((pAsjqdt) != NULL)	(pAsjqdt)->fOptionsXX &= ~QJDTOXX_UNICODE;	\
}while(0)

#define isASQJDTConverted(pAsjqdt)	((pAsjqdt)?((pAsjqdt->fOptionsXX & UNICODE_SCRIPT_CONVERTED) != 0):0)
#define setASQJDTConverted(pAsjqdt) (pAsjqdt->fOptionsXX |= UNICODE_SCRIPT_CONVERTED)
#define clearASQJDTConverted(pAsjqdt) (pAsjqdt->fOptionsXX &= ~UNICODE_SCRIPT_CONVERTED)

// Job Methods (for Backup and Copy)
#define METHOD_CUSTOM           0          // Ignore archive bit
#define METHOD_FULL             1          // Clear archive bit
#define METHOD_INCREMENTAL      2          // Check and clear archive bit
#define METHOD_DIFFERENTIAL     3          // Check archive bit

// On Conflict Methods (for Archive and Restore)
#define ONCONFLICT_SKIP         1          // On conflict skip
#define ONCONFLICT_RENAME       2          // On conflict rename
#define ONCONFLICT_REPLACE      3          // On conflict replace (overwrite)
#define ONCONFLICT_REPLNEW      4          // On conflict replace if newer
#define ONCONFLICT_REPLCONFIRM  5          // On conflict confirm replace (overwrite)

// Alert Flags
#define AF_ONSUCCESS            0x0001     // Alert on success
#define AF_ONINCOMPLETE         0x0002     // Alert on incomplete
#define AF_ONFAILURE            0x0004     // Alert on failure
#define AF_ONCANCEL             0x0008     // Alert on cancellation
#define AF_ONVIRUS              0x0010     // Alert on virus detected
#define AF_ATTACHLOG            0x0080     // Attach job log
#define AF_BROADCAST            0x0100     // Broadcast
#define AF_PAGER                0x0200     // Pager
#define AF_EMAIL                0x0400     // E-mail
#define AF_TTICKET              0x0800     // Trouble ticket
#define AF_SNMP                 0x1000     // SNMP


//for exchange 2000
#define ASDTO_OPTION_IS2000					   0x00008000	//dbaexch 2000 restore
#define ASDTO_OPTION_LASTSET				   0x00010000	//dbaexch 2000 restore
#define	ASDTO_OPTION_MOUNTDB				   0x00020000	//dbaexch 2000 restore
#define	ASDTO_OPTION_REST_LOGSONLY			   0x00040000	//dbaexch 2000 restore


//
// Merge Scan Type values from Unix
//

#define ASQM_ALL     0        // Merges all tapes
#define ASQM_CURRENT 1        // Merges only the tape in the drive

/*
 *   Host Server OS Type values
 */
//see definitions in CUI.H
//#define HSOST_UNKNOWN		0				// Undefined OS
//#define HSOST_NT            1             // The operating system is NT
//#define HSOST_NETWARE       2             // The operating system is NetWare
//#define HSOST_UNIX          3             // The operating system is UNIX


/*
 *  Host Server OS Type API implemented as macros
 */

#define  ASIsHostServerOSNT(x)                 (x == HSOST_NT)
#define  ASIsHostServerOSUnix(x)               (x == HSOST_UNIX)
#define  ASIsHostServerOSNetware(x)            (x == HSOST_NETWARE)

#define  ASSetHostServerOSNT(x)                (x = HSOST_NT)
#define  ASSetHostServerOSUnix(x)              (x = HSOST_UNIX)
#define  ASSetHostServerOSNetware(x)           (x = HSOST_NETWARE)

/*
 * Replication Options
 */

#define	QJRO_IGNORE			0x00000000		// Do nothing
#define	QJRO_SKIP			0x00000001		// Skip any replication files
#define	QJRO_BACKUPASREMOTE	0x00000002		// Backup as if read from primary server

/*
 * VMS Options
 */

#define	QJVMSO_R_VERS_CREATE	0x00000000	// On restore, create new versions of files
#define	QJVMSO_R_VERS_REPLACE	0x00000001	// On restore, replace the current version of files
#define	QJVMSO_R_VERS_RESTORE	0x00000002	// On restore, restore the backed up version of files
#define	QJVMSO_R_VERSION_MASK	0x0000000F

//
//   Data Transfer Extended Options
//
#define QJDTXO_EXPORT_ALL                      0x00000001    // Export all tapes after job
#define QJDTXO_EXPORT_DUPLICATE                0x00000002    // Export duplicate tapes after job
#define QJDTXO_EXPORT_VIRTUAL                  0x00000004    // Use virtual export (future)
// AXWBAS_1896_T46B174	
#define QJDTXO_EXPORT_MASK					   (QJDTXO_EXPORT_ALL | QJDTXO_EXPORT_DUPLICATE | QJDTXO_EXPORT_VIRTUAL)
//#define QJDTXO_EXPORT_MASK                     0x0000000F    //
#define QJDTXO_B_TRAVSYMLINK                   0x00000010    // Backup only option
#define QJDTXO_B_MTPTSINSESSION                0x00000020    //
#define QJDTXO_B_TRAVHARDLINKS                 0x00000040    //

#define QJDTXO_R_VMRECOVERY                    0x00000010	// restore only option, indicate the restore job is packaged from VM recovery manager. - VMWARE_AGENT
#define QJDTXO_R_VMRECOVERY_POWERON			   0x00000020	// restore only option, power on the VM after restore.
#define QJDTXO_R_VMRECOVERY_OVERWRITE		   0x00000040	// restore only option, overwrite the VM if it already exists.

#ifdef DEDUPE_SUPPORT // <XUVNE01> Add @2008-05-28
#define QJDTXO_B_DEDUPEFORCE2RESETARCHBIT      0x00000080    // Force 2 reset archive bit after backup for DeDupe stream
#endif

#define QJDTXO_FORCEDDEMIG                     0x00000100    // Forced De-migration option (for ASNW)
#define QJDTXO_FORCEDDECOMP                    0x00000200    // Forced Decompression option (for ASNW)
#define QJDTXO_MULTIPROSESSING                 0x00000400    // enable multiprosessing
#define QJDTXO_MULTIPROC_SLAVE                 0x00000800    // we need to differentiate the SLAVE jobs for multiprocessing
#define QJDTXO_MULTIPROC_SLAVE_APPEND          0x00001000    //
#define QJDTXO_COPY_TAPE_SESS_AFTER_BK         0x00002000    // use for UNIX Backend
#define QJDTXO_MULTIPROC_SLAVE_HDS             0x00004000    // HDS slave job
#define QJDTXO_REDIRECTED_LOCAL                0x00008000    // Job is redirected from local server
#define QJDTXO_B_VSS_USE                       0x00010000
#define QJDTXO_B_VSS_REVERT                    0x00020000
#define QJDTXO_B_VSS_EXCLUDE_INCLUDES          0x00040000
#define QJDTXO_B_VSS_EXCLUDE_EXCLUDES          0x00080000
#define QJDTXO_B_VSS_ON_FAIL                   0x00100000
#define QJDTXO_MULTIPLEXING                    0x00200000	 // MULTIPLEXING Flag.
#define QJDTXO_MULTIPROC_MULTISTRIPE_SQL       0x00400000    // Kevin Lin 6/3/03 SQL multistripe
#define QJDTXO_MULTIPROC_MULTISTRIPE_SQL_CHILD 0x00800000    //
#define QJDTXO_REWINDTAPE_AFTERJOB             0x01000000    // James Lo 6/27/03 Rewind tape after job
#define QJDTXO_B_ALERT_PARENT_ONLY             0x02000000    // Multistreaming/Multiplexing option, alert via parent job only.
#define QJDTXO_B_DR_PARTIAL_NODE               0x04000000    // Force to have DR session even if it is a partial node backup.
#define QJDTXO_B_DR_IGNORE_FILTER			   0x08000000    // DR Job extension Flag
#define QJDTXO_AV_SCAN_ARCHIVE                 0x10000000    // Indicating archive file needs to be scanned
#define QJDTXO_MAKEUP						   0x20000000	 // Indicating makeup job(include custom and rotation)
#define QJDTXO_ORACLERMAN_MASTERJOB			   0x40000000    // OracleRMAN Backup master job
#define	QJDTXO_B_RESETACCESSTIME			   0x80000000	 // Windows options. 1 means keep original access time after backup.

#ifdef FINAL_DEST_SNAPLOCK
#define	QJDTXO_B_FINAL_DEST_ENABLE_SNAPLOCK	   0x00000200    //  Snap Lock.
#endif

//B2D2T Flags in ASQJDTEX::fOptionsEx: //Bits 0 to 15 reserved for B2D2T.
#ifdef _B2D2T
#define QJDTXO_B_DISK_STAGING									0x00000001	 // Indicating Disk Staging is ON.
#define QJDTXO_B_DISK_STAGING_ENABLE_SNAPLOCK					0x00000002	 // Indicates that Snap lock is enabled for FULL backup
#define QJDTXO_B_DISK_STAGING_DISABLE_COPY_INCR_DIFF_FROM_D2T	0x00000004	 // Indicates that data migration should be disabled for incr\diff backup sessions.
//#define QJDTXO_B_DISK_FULL_FAIL								0x00000008	 // Indicating fail the job on disk full condition.
//#define QJDTXO_B_DISK_FULL_PURGE_CONTINUE						0x00000010	 // Indicating on disk full, purge oldest sessions and continue backup
//#define QJDTXO_B_DISK_FULL_TAPE_CONTINUE						0x00000020	 // Indicating on disk full, backup to tape.
#define QJDTXO_B_DISK_NO_STAGING								0x00000040   // Indicating Disk Staging is ON, Backup directly to tape.
#define QJDTXO_B_DISK_STAGING_DISABLE_COPY_FULL_FROM_D2T		0x00000080   // Indicates that data migration should be disabled for full backup sessions.
#define QJDTXO_B_DISK_STAGING_ENABLE_SNAPLOCK_INCR_DIFF			0x00000100   // Indicates that Snap lock is enabled for INCR | DIFF backups
#define QJDTXO_B_DISK_STAGING_DISK_FULL_MAKEUP_TO_TAPE			0x00000400   // Indicates that under disk full conditions, makeup job will go to tape.
#endif //_B2D2T

#define QJDTXO_B_SQL_NOROTATION									0x00001000	 // IndicatingDo not alpply Diff/Inc Backup method to SQL database

#ifdef BAB_PULL_SQL2005
#define QJDTXO_B_SQL_NOT_UPGRADE_PARTIAL						0x00002000	 // Indicating do not automatically upgrade Microsoft SQL Server Partial Backup to Database Full Backup if Database Full not found
#endif //BAB_PULL_SQL2005

#ifdef SECURE_MIGRATION
// Server Encryption & Compression options, applied to ASQJDTEX::fOptionsEx
#define QJDTXOX_B_ENCRYPTFILES_SERVER							0x00004000 // Encrypt each file onto tape on TE side
#define QJDTXOX_B_COMPRESSFILES_SERVER							0x00008000 // Compress each File onto tape on TE side
#endif // TE_ENCRYPTION

#ifdef MASTER_CHILD_JOBLOG_CONSOLIDATION
#define QJDTXOX_B_MASTER_CHILD_JOBLOG_CONSOLIDATION				0x00010000 // Consolidate master job log to child
#endif //MASTER_CHILD_JOBLOG_CONSOLIDATION

#ifdef STAGING_REPEATING_APPEND_ANY_TAPE
#define QJDTXOX_B_DISK_STAGING_REPEATJOB						0x00020000   // Indicates that this is repeating staging job.
#endif //STAGING_REPEATING_APPEND_ANY_TAPE

#define QJDTO_B_BACKUPCATALOG									0x00040000
#define QJDTO_B_BACKUPASDB										0x00080000
#define QJDTO_B_BACKUPJOBQUEUE									0x00100000
#define QJDTXOX_B_ASDB_PROTECT_BACKUPJOB						0x00200000
#define QJDTO_B_BACKUPSQLDR										0x00400000

#define QJDTXOX_RESERVE_GROUP									0x00800000	// Issue#15854392, wanhe05, 5/31/2007, force Job Engine check group availability by setting QJOBINFO.ulJobControlFlags with QF_JOB_EXECD							

// chefr03, add for Password Management
#ifdef R12_5_MANAGE_PASSWORD
#define QJDTXOX_B_MANAGE_PASSWORD								0x01000000  // R12_5_MANAGE_PASSWORD
#define QJDTXOX_B_NOTIFY_PASSWORD_EXPIRATION					0x02000000	// R12_5_MANAGE_PASSWORD
#endif

//end for ASQJDTEX::fOptionsEx, all the bits should be added to this range.

#ifdef _B2D2T
//The following copy policies would be stored in ASQJDTEX::ucCopyPolicyForFull and ASQJDTEX::ucCopyPolicyForIncDiff
#define COPY_POLICY_JOB_START_TIME					0x00000001	//Calculate copy time by adding specified time to the start time of the job.
#define COPY_POLICY_JOB_END_TIME					0x00000002	//Calculate copy time by adding specified time to the end time of the job.
#define COPY_POLICY_SESSION_END_TIME				0x00000004	//Calculate copy time by adding specified time to the end time of each individual session.
#define COPY_POLICY_ABSOLUTE_TIME					0x00000008	//Use whatever is specified by the user.
#define COPY_POLICY_ABSOLUTE_TIME_OR_JOB_FINISH		0x00000010	//Use whatever is specified by the user or when a job finishes whichever finishes later.

//The following purge policies would be stored in ASQJDTEX::ucPurgePolicyForFull and ASQJDTEX::ucPurgePolicyForIncrDiff
//It's implicit in all the following purge policies that even if it's time to purge, data will NOT be purged untill it's copied.
#define PURGE_POLICY_JOB_START_TIME					0x00000001	//Calculate purge time by adding specified time to the start time.
#define PURGE_POLICY_JOB_END_TIME				    0x00000002	//Calculate purge time by adding specified time to the job end time.
#define PURGE_POLICY_ABSOLUTE_TIME					0x00000004	//Use whatever is specified by the user or when a job finishes whichever finishes later.
#endif //_B2D2T

// Bit 16 reserved for TASKS ENHANCEMENT FOR RMAN, applied to ASDDISKEXNEXTORACLE::fOptionsExOracle
#ifdef BAB_ORACLE_RMAN_ENHANCEMENT
#define QJDTXO_MULTIPROC_REUSABLE_SLAVE 0x00010000
#define QJDTXO_B_RMAN_LESS_DRIVE        0x00020000				//
#define QJDTXO_B_RMAN_LESS_COPY	        0x00040000				//If number of copies is more than 1 and so many drives are not available, setting this flag will fail the job. Not setting the flag will let the job continue with no extra copy.
#endif

#ifdef ORACLE_FILE_BASE
#define QJDTXO_B_ORACLE_FILE_BASE        0x08000000				//Backup Oracle with file base agent
#endif

#ifdef _DISKSTAGING_CONSOLIDATION
#define ASDEX2_B_USE_TAPE_COPY				0x00000001			//indicate tape copy is enabled (turned on in tagASQJDTEX.fOptions2)
#define ASDEX2_B_USE_MULTISTREAM			0x00000002			//indicate multisteam is used in backup stage(for B2T2T job, turned on in tagASQJDTEX.fOptions2)
#define ASDEX2_B_USE_CONSOLIDATION			0x00000004			//indicate consolidation is enabled (turned on in tagASQJDTEX.fOptions2)
#endif // _DISKSTAGING_CONSOLIDATION

#define ASDEX2_B_RERUN_CRASHED_JOB			0x00000008			//master job crashes and rerun since retry crashed job is enabled or in cluster environment 
#define ASDEX2_B_SUBMITMAKEUPJOB_ON_HOLD	0x00000010			//makeup job is submitted on hold by backend

//R12V_VM_UnivAg
#define ASJEX2_B_VMWARE_RAW                 0x00000001			// VMWARE_AGENT, whole ESX is selected, this flag is saved in each ASDDISK of this ESX
#define ASJEX2_B_VMWARE_FILE                0x00000002			// VMWARE_AGENT r12.v Full backup Raw mode, inc/dif backup File mode 
#define ASJEX2_B_VMWARE_RAW_FILE_MIX        0x00000004			// VMWARE_AGENT, Mixed mode backup
#define ASJEX2_B_VMWARE_IMAGE_FS            0x00000008			// VMWARE_AGENT, Raw mode allow file level restore
#define ASJEX2_B_VMWARE_FILE_VM_DIRECT		0x00000010			// VMWARE_AGENT, file mode backup from VM directly

//
// Unix Job Time values
//
#define USE_HOUR        1
#define USE_DAY         2
#define USE_WEEK        3
#define USE_MONTH       4

//
// use in INOCULANINFO.ulOptions
//
#define   INOC_SCAN_OFF            0x00000000
#define   INOC_SCAN_NORMAL         0x00000001
#define   INOC_SCAN_EXE_ONLY       0x00000002
#define   INOC_SUBMIT_JOB_ONLY     0x00000004

//
// use in INOCULANINFO.ulScanMode
//
#define   INOC_SCAN_FAST           0
#define   INOC_SCAN_SECURE         1
#define   INOC_SCAN_REVIEWER       2

//
// use in INOCULANINFO.ulVirusFoundAction
//
#define   INOC_METHOD_REPORTONLY   0
#define   INOC_METHOD_DELETE       1
#define   INOC_METHOD_RENAME       2
#define   INOC_METHOD_CURE         3
#define   INOC_METHOD_MOVE         4
#define   INOC_METHOD_PURGE        5
#define   INOC_METHOD_MOVE_RENAME  6

// For NetWare Alert cMsgDestination
#define  GM_BROADCAST         0x01
#define  GM_PAGER             0x02
#define  GM_EMAIL             0x04
#define  GM_SNMP              0x08
#define  GM_TNG               0x10

//
// Interleaf Options
//
#define ASILO_GROUPING          0x00000001  // User-defined grouping
#define ASILO_DYNAMIC           0x00000002  // Dynamic striping
#define ASILO_EXPANDNODES       0x00000010  // Expand nodes not explicitly expanded
#define ASILO_EXCLUSIVE         0x00000100  // Use exclusive connect to device group

//////////////////////////// added by gonch02 on 04/12/2007 for avoid compile error //////////////////////////////
///// pls pay attention to the codes when change them, because there are three copies on different modules ///////
///// h\asqjob.h      asopen.dll\asqjob\commqjob.h      asopen.dll\jobscript\as2asqjob.h /////////////////////////

#ifndef _BAB_ASQJOB_DEFINED
#define _BAB_ASQJOB_DEFINED

typedef struct tagASAPGROOM
{
  USHORT nMonths;                       // # AP backup months before grooming
  USHORT nCopies;                       // # safe copies before grooming
  ULONG  nFilterItems;                  // # of Filter items in list
  PVOID  pvFilterList;                  // List of filters
  FLAG32 fOptions;                      // See ASAPGROOM Options
  UCHAR  aucReserved[8];                // Reserved for future use
} ASAPGROOM;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASAPGROOM FAR  *PASAPGROOM;
typedef ASAPGROOM NEAR *NPASAPGROOM;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASAPGROOM FAR  *PASAPGROOM;
typedef ASAPGROOM NEAR *NPASAPGROOM;
#else
typedef ASAPGROOM *PASAPGROOM;
#endif

typedef struct tagINOCULANINFO
{
  ULONG ulVirusFoundAction;             // Off, Scan Only, Rename, Delete, etc.
  ULONG ulScanMode;                     // Fast, Secure, Reviewer
  ULONG ulOptions;                      // Scan File while backing up, Submit Job to scan before backup, etc.
  ULONG reserved;
} INOCULANINFO;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef INOCULANINFO FAR  *PINOCULANINFO;
typedef INOCULANINFO NEAR *NPINOCULANINFO;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef INOCULANINFO FAR  *PINOCULANINFO;
typedef INOCULANINFO NEAR *NPINOCULANINFO;
#else
typedef INOCULANINFO *PINOCULANINFO;
#endif

// Extension structure for NetWare : For ASNW, ASUX
typedef struct tagASQJNWEX
{
  INOCULANINFO  InocInfo;               // Inoculan info
  ASAPGROOM     APGroom;                // AP Groom info
  USHORT        nCompareFirstMB;        // Compare first these MB of data.
  USHORT		cMsgDestination;		// Message destination for Unix Job
  UCHAR         reserved[14];
  PVOID			pvNextExtension;
} ASQJNWEX;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASQJNWEX FAR  *PASQJNWEX;
typedef ASQJNWEX NEAR *NPASQJNWEX;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASQJNWEX FAR  *PASQJNWEX;
typedef ASQJNWEX NEAR *NPASQJNWEX;
#else
typedef ASQJNWEX *PASQJNWEX;
#endif

typedef struct tagASINTERLEAFINFO
{
  FLAG32 fOptions;                      // see ASILO_xxx
  USHORT usMaxPriority;                 // Maximum priority number
  USHORT usMaxThreads;                  // Number of threads to interleaf
  UCHAR  aucReserved[24];
} ASINTERLEAFINFO;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASINTERLEAFINFO FAR  *PASINTERLEAFINFO;
typedef ASINTERLEAFINFO NEAR *NPASINTERLEAFINFO;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASINTERLEAFINFO FAR  *PASINTERLEAFINFO;
typedef ASINTERLEAFINFO NEAR *NPASINTERLEAFINFO;
#else
typedef ASINTERLEAFINFO *PASINTERLEAFINFO;
#endif

#ifdef SECURE_MIGRATION
typedef struct tagASQJCOPYCOMMON //common info for B2D2T and B2T2T
{
	CHAR	pszSecureMigrationPW[24]; //the max length of password equals to that of agent.
	USHORT	uiCryptoType;
	CHAR	ucAlgorithmType;
	UCHAR	ucUsingCompression;
	UCHAR	reserved[64 - 24 - 2 - 1 - 1];
}ASQJCOPYCOMMON;

#if defined(_64BIT_ARCH_)
	#pragma pointer_size(save)
	#pragma pointer_size(short)

	typedef ASQJCOPYCOMMON FAR  *PASQJCOPYCOMMON;
	typedef ASQJCOPYCOMMON NEAR *NASQJCOPYCOMMON;

	#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
	typedef ASQJCOPYCOMMON FAR  *PASQJCOPYCOMMON;
	typedef ASQJCOPYCOMMON NEAR *NASQJCOPYCOMMON;
#else
	typedef ASQJCOPYCOMMON *PASQJCOPYCOMMON;
#endif

#endif // SECURE_MIGRATION

#ifdef _B2T2T
// constants for field bEnableFullBackupTapeCopy, bEnableIncDiffBackupTapeCopy
#define B2T2TCOPY_DISABLED						(0)
#define B2T2TCOPY_ENABLED						(1)
#define B2T2TCOPY_ENABLED_MONTHLYBACKUPONLY		(2)		// field bEnableFullBackupTapeCopy specified, valid for GFS job only

#define B2D2TCOPY_ENABLED_MONTHLYBACKUPONLY		(1)		//used by field pASQJDTEx->usDiskStagingFlags, vaild for GFS job only.

typedef struct tagASQJDTEXTAPECOPY
{
	ULONG		ulCopyTimeForFullBackUp;
	ULONG		ulCopyTimeForIncDiffBackUp;
	PTSZ		pszFinalGrpName;						// Allocate UCHAR TSI_MAX_GROUP_NAME + 1 (8 + 1)
	PTSZ		pszFinalMediaName;						// Allocate UCHAR TSI_MAX_TAPE_NAME + 1 (24 + 1)
	PTSZ		pszFinalMediaPool;						// Allocate UCHAR MAX_POOL_NAME_SIZE + 1 (16 + 1)
	USHORT		usFinalMediaSequence;
	USHORT		usFinalMediaRandomId;
	UCHAR		bEnableFullBackupTapeCopy;
	UCHAR		bEnableIncDiffBackupTapeCopy;
	UCHAR		ucCreateDMJMakeupJobOnHold;				// TRUE:Create DMJ makeup job and submit it on hold; Default = TRUE.
	UCHAR		ucLeaveCatalogsOnDisk;					// Levae catalog files on disk if enabled. DEFAULT = TRUE.
	ULONG		ulMakeupAfterJobEndTime;				// Makeup Job schedule time is job end time + ulMakeupAfterJobEndTime, default value is 0, -1(0xffff ffff) indicates this option is disabled
	UCHAR		szReserved[32];							// Reserve 32 bytes. Total of this structure is 64 bytes
}ASQJDTEXTAPECOPY;

#if defined(_64BIT_ARCH_)
	#pragma pointer_size(save)
	#pragma pointer_size(short)

	typedef ASQJDTEXTAPECOPY FAR  *PASQJDTEXTAPECOPY;
	typedef ASQJDTEXTAPECOPY NEAR *NPASQJDTEXTAPECOPY;

	#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
	typedef ASQJDTEXTAPECOPY FAR  *PASQJDTEXTAPECOPY;
	typedef ASQJDTEXTAPECOPY NEAR *NPASQJDTEXTAPECOPY;
#else
	typedef ASQJDTEXTAPECOPY *PASQJDTEXTAPECOPY;
#endif
#endif // _B2T2T

#ifdef DEDUPE_SUPPORT
// constants for field tagASQJDTEX.ucDDDPurgeCancelledFailedSessions
#define DDD_PURGE_CANCELLED_SESSIONS	(0x01)
#define DDD_PURGE_FAILED_SESSIONS		(0x02)
#endif 

#ifdef NEW_AGENT_GLOBAL_OPTION
typedef struct tagASAGENTOPTION
{
	USHORT usNodeType;                  // See Node Type
	USHORT usNodeOS;                    // See Node Operating System
	USHORT usDiskType;                  // See Device File System
	USHORT usFileType;                  // new for disk item.
	FLAG32 fOptions;                    // See agent option
	FLAG32 fOptionsEx;                  // See agent option 2.
	ULONG nXMLOptionLength;				// How many NULL terminating string options are saved in pXMLOption.
	WCHAR* pXMLOption;                  // more agent option in XML/string format. Layout is as same as pszFileList in ASDISTITEM.
}ASAGENTOPTION;

#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASAGENTOPTION FAR  *PASAGENTOPTION;
typedef ASAGENTOPTION NEAR *NPASAGENTOPTION;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASAGENTOPTION FAR  *PASAGENTOPTION;
typedef ASAGENTOPTION NEAR *NPASAGENTOPTION;
#else
typedef ASAGENTOPTION *PASAGENTOPTION;
#endif
#endif //NEW_AGENT_GLOBAL_OPTION

typedef struct tagASQJDTEX
{
  ASINTERLEAFINFO       InterleafInfo;
  FLAG32				fReplicationOptions;
  FLAG32				fPrePostOptions;
  FLAG32				fVMSOptions;
  PASQJNWEX				pASQJNWEx;

  ULONG					uniqueMuxID;		// unique id for Multiplexing Child jobs to one tape.
  ULONG					muxChunkSize;		// user configurable size of data block on tape for faster restores.
  ULONG					muxClusterSize;		// user defined checkpointing size for faster rollover.
  ULONG					maxDrivesToUse;		// maximum # of drives to be used by the job.
  USHORT				maxNumberOfStreams; // maximum # of steams multiplexed to a drive.
  USHORT				mstripeobjID;
  PTSZ					pszPrePostUser;
  PTSZ					pszPrePostPassword;
  ULONG					musNodeLevel;		// linku02 Multi-stream node level for UNIX
  ULONG					musMaxDrives;		// linku02 Multi-stream node level for UNIX
  PTSZ					pszPrePostCodeUnix;	// the pre/post command exit code for unix can have multiple values separated by commas
  ULONG					prePostCriteria;	// For UNIX exit code criteria, 0: Equal To, 1: Greater Than , 2: Less Than, 3: Not Equal To
  ULONG					ulRetentionTimeForFullBkp; //Retention time for the Full Backup on Disk
  ULONG					ulRetentionTimeForIncOrDiffBkp; //Retention time for the Incr/Diff Backup on Disk
  FLAG32				fOptionsEx;	//Bits 0 to 15 reserved for B2D2T.
  ULONG					maxNumberOfStreamsToDisk; //This specifies the max simul streams to use while writing to disk.
													//This should be used ONLY while writing to disk.In scenarios where job is
													//changesd to write to tape, this var SHOULD be neglected.
  ULONG					ulCopyTimeForFullBkp;		//When to copy the full backup sessions from disk
  ULONG					ulCopyTimeForIncOrDiffBkp;  //When to copy the incr /diff backup sessions from disk
  ULONG					ulMasterJobID;				//Master Job Job ID
  ULONG					ulMasterJobExecTime;		//Master Job Execution Time
  ULONG					ulMasterJobMuxID;			//Master Job Mux ID
  UCHAR					ucCopyPolicyForFull;		//For FULL Backups:Indicates the copy policy to use.
  UCHAR					ucCopyPolicyForIncDiff;		//For Incr\Diff Backups: Indicates the copy policy to use.
  UCHAR					ucPurgePolicyForFull;		//For FULL Backups:Indicates the purge policy to use.
  UCHAR					ucPurgePolicyForIncrDiff;	//For Incr\Diff Backups:Indicates the purge policy to use.
  UCHAR					ucEligibleForPurgeAhead;	//Indicates that sessions backed up by this job qualify for Purge ahead in cases of disk full condition.
  UCHAR					bPurgeFailedSessions;		//Purge Failed Session. Default = 0
  UCHAR					bPurgeCancelledSessions;	//Purge Cancelled Session. Default = 0
  UCHAR					bDSMakeupJobToTape;			//Backup to Disk, Disk is full, makeup job directly to final destination.Default = TRUE;
  UCHAR					bCreateDMJMakeupJobOnHold;	//TRUE:Create DMJ makeup job and submit it on hold; FALSE No Makeup Job Creatd. Default = TRUE.
  UCHAR					bLeaveCatalogsOnDisk;		//Levae catalog files on disk if enabled. DEFAULT = 1.
  UCHAR					bOptimizeRestore;			//If set to 1, optimize the restore by restoring from disk. By default, the value is 1 i.e enabled in GUI in restore options.
  UCHAR					bDistinguishMediaByNameOnly;//Tape selection will ignore Random ID and Sequence number
  USHORT				usDSChunkSize;				//Amount of Data written to disk in one shot during Disk Staging Staging. This should be expressed in KB. Default = 64
  USHORT				nRetriesOnNewTapeForDMJMakeup;	//During Data Migration, under media errors, retry on n number of blank or scratch media before failing.Default = 1.
  ULONG					ulRetentionTimeFinalDest;
  UCHAR					bUseWormMediaForCustomJobs;	//Use worm media for custom jobs. Default = 0.
  UCHAR					bUseWormMedia_Daily;		//Use Worm media for daily jobs (for rotation and GFS). Default = 0
  UCHAR					bUseWormMedia_Weekly;		//Use Worm media for Weekly jobs (for rotation and GFS). Default = 0
  UCHAR					bUseWormMedia_Monthly;		//Use Worm media for Monthly jobs (for rotation and GFS). Default = 0
  UCHAR					agentOpaqueData[32];        //To be used by Oracle RMAN agent for their internal purposes.
  UCHAR					sessionGuid[16];			//Used for Informix agent restore job (B2D2T backup session)
  UCHAR					bEjectTapeOn;				//B2D2T master job set eject tape options
  UCHAR					bDMExportTapeOn;				//B2D2T master job set export tape
  UCHAR					bDMEjectByDevSetOn;			//B2D2T master job set eject tape by Dev options
  UCHAR					ucMaxMakeupRetries;			//Max# of makeup retries.
  ULONG					ulMasterJobNo;				//Master Job Job ID
  ULONG					ulMakeupAfterJobEndTime;	//Makeup Job schedule time is job end time + ulMakeupAfterJobEndTime, default value is 0
  PASQJCOPYCOMMON		pCommon;
  UCHAR *				pPostMigrationOperations;
  FLAG32				fOptions2;
  USHORT				usEnableConMigration;				// Indicate the data consolidation is enabled or not while the data get migrated.
  USHORT				usConsolidationCopyMethod;			// Append = 0(QJNTM_APPEND); Overwrite = 1(QJNTM_OVERWRITESAMEBLANK) Default = Overwrite
  PTSZ					pszConsolidationMediaPrefix;		// allocate UCHAR TSI_MAX_TAPE_NAME + 1 (24 + 1)
  PTSZ					pszConsolidationMediaPoolPrefix;	// allocate UCHAR MAX_POOL_NAME_SIZE + 1 (16 + 1)
  PASQJDTEXTAPECOPY		pASQJDTEXTapeCopy;
  ULONG					ulMasterJobScheduleTime;
#ifdef NODE_RPT_MASTER
  USHORT				usMasterJobDiskListIndex;			// record the index of node in the master job disklist. the info is saved here when child is creatd, 
															// or the index is passed by master job when the idle node is pushed to child, and child job receives this index 
															// and saves it in this field.	
#else
  UCHAR					aucReservedForNodeRptMaster[2];
#endif // NODE_RPT_MASTER
  USHORT				usDiskListType;						//linyu04: master job status 09_21_2008. there are several kind of disklists in master job, this indicates the kind of list this node comes from
//  UCHAR 				aucReservedForPadding[2];			// Used for 8 bytes alignment. Should not use directly. Who wants to reused it must change it to another name.

#ifdef DEDUPE_SUPPORT
  USHORT				usDDDScanDuringLastDays;
  UCHAR					usDDDReservedForPadding[1];
  UCHAR					ucDiskStagingFlags;
  ULONG					ulDDDScanSessionCount;
  ULONG					ulDDDRetentionTimeForFullBkp;       //Retention time for the Full Backup on DDD final destination
  ULONG					ulDDDRetentionTimeForIncOrDiffBkp;  //Retention time for the Incr/Diff Backup on DDD final destination
  UCHAR					ucDDDPurgePolicyForFull;            //For FULL Backups to DDD final destination: Indicates the purge policy to use
  UCHAR					ucDDDPurgePolicyForIncrDiff;        //For Incr\Diff Backups to DDD final destination: Indicates the purge policy to use
  UCHAR					ucDDDScanSessionCountUnit;
  UCHAR					ucDDDPurgeCancelledFailedSessions;	// Purge the Cacelled/Failed sessions on DDD which are generated by data migration jobs or non-staging backup jobs
#else //DEDUPE_SUPPORT
  UCHAR					aucReservedForDDD[20];
#endif //DEDUPE_SUPPORT

#ifdef Wanhe05_DeDupe_SCAN
  PTSZ					pszAgentNodeNames;					//For advanced scan job only, in its global options, allow user to input multiple agent node names separated by commas to qualify the scanning sessions that is backed up via these agents.
#else
  UCHAR					aucReservedForADScan[4];
#endif

#ifdef R12_5_MANAGE_PASSWORD
  ULONG					ulPwdExpirePeriod;			// How long the password will expire, in days
#else	// R12_5_MANAGE_PASSWORD
  UCHAR					aucReservedForManagePassword[4];
#endif	// R12_5_MANAGE_PASSWORD

#ifdef NEW_AGENT_GLOBAL_OPTION
	UCHAR	aucReserved[216]; //220=228-12
	ULONG	nAgentVersion;	  // = (majorVersion<<16)|((minorVersion & 0x0FF) << 8 )|(servicePack &0x0FF)
	ULONG	nAgentOptions;	  //How many agent options are in below arrays
	PASAGENTOPTION pAgentOptions; //point to Agent option array
#else //NEW_AGENT_GLOBAL_OPTION
	UCHAR					aucReserved[228];
#endif //NEW_AGENT_GLOBAL_OPTION 

  FLAG32				fOptions;
  PVOID                 pvNextExtension;
} ASQJDTEX;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASQJDTEX FAR  *PASQJDTEX;
typedef ASQJDTEX NEAR *NPASQJDTEX;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASQJDTEX FAR  *PASQJDTEX;
typedef ASQJDTEX NEAR *NPASQJDTEX;
#else
typedef ASQJDTEX *PASQJDTEX;
#endif

#endif /* ifndef _BAB_ASQJOB_DEFINED */
///////////////////// gonch02  _BAB_ASQJOB_DEFINED //////////////////////////////

///Keyan01 25/June/03 aucReserved now 35 from 55
//and ImageOptions struct added
typedef struct tagASNODEX
{
  IORetry		ImageOptions;			//Image options
#ifdef PREPOST_SEC_ENH
  PTSZ	 pszPrePostUser;
  PTSZ	 pszPrePostPassword;

#ifdef E14_TASK	//<XUVNE01> E14 Support 2009-03-11
  PTSZ  pszExchDomainName;                      //Exchange Domain Name
  PTSZ  pszExchADServerName;                    //Exchange AD Server Name
  PTSZ  pszExchADServerAddr;                    //Exchange AD Server IP Address
  PTSZ  pszExchADServerUserName;                //Exchange AD Server User Name
  PTSZ  pszExchADServerUserPWD;                 //Exchange AD Server Password
  UCHAR  aucReserved[7];
#else
  UCHAR  aucReserved[27];
#endif

#else
  UCHAR  aucReserved[35];
#endif
  UCHAR  nPriority;						// For Netware usage only
  FLAG32 fOptions;						// Extended options
  PVOID  pvNextExtension;				// Can be further extended
} ASNODEX;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASNODEX FAR  *PASNODEX;
typedef ASNODEX NEAR *NPASNODEX;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASNODEX FAR  *PASNODEX;
typedef ASNODEX NEAR *NPASNODEX;
#else
typedef ASNODEX *PASNODEX;
#endif

typedef struct tagASNODE
{
  USHORT usNodeType;                    // See Node Type
  USHORT usNodeOS;                      // See Node Operating System
  PTSZ   pszNodeOSVer;                  // See Node Operating System Version
  PTSZ   pszNodeName;                   // Server or workstation name
  PTSZ   pszNodeAddr;                   // Server or workstation internet addr
  PTSZ   pszUserName;                   // User name
  PTSZ   pszUserPW;                     // User password
  ULONG  nFilterItems;                  // # of Filter items in list
  PVOID  pvFilterList;                  // List of filters
  USHORT usDeviceType;                  // See Device Type
  USHORT usInterleafPriority;           // Interleaving priority (JCL 980930)
  ULONG  nDeviceItems;                  // # of Device items
  PVOID  pvDeviceList;                  // List Device structures
  PTSZ   pszBeforeNode;                 // Process string before node
  PTSZ   pszAfterNode;                  // Process string after node
  USHORT usBeforeNodeWait;              // Before Node wait time (# minutes)
  FLAG16 fOptionsEx;                    // See ASNODE OptionsEx
  FLAG32 fOptions;                      // See ASNODE Options
  ASAPGROOM APGroom;                    // AP Groom info
  PTSZ   pszNodeDomain;                 // Domain name
  USHORT usPreExitCode;                 // Exit code for Application wich run before
  USHORT nPadding2;
  PTSZ   pszEncryptKey;                 // Encryption key
  PASNODEX pASNodeX;                    // Extended node structure
} ASNODE;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASNODE FAR  *PASNODE;
typedef ASNODE NEAR *NPASNODE;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASNODE FAR  *PASNODE;
typedef ASNODE NEAR *NPASNODE;
#else
typedef ASNODE *PASNODE;
#endif

//
//  Queue Job structure for Data Transfer (usLevel == LVL_ASDATATRANSFER)
//
typedef struct tagASQJDATATRANSFER
{
  ULONG        ulJobID;                // Job ID
  USHORT       usJobType;              // See Job Type
  USHORT       usJobPosition;          // Job Position
  USHORT       usJobStatus;            // See Job Status
  USHORT       nScriptType;            // Script Type (used by UI)
  ULONG        nSourceItems;           // Number of source items
  PASNODE      pSourceList;            // Source node list
  ULONG        nDestItems;             // Number of dest. items
  PASNODE      pDestList;              // Dest. node list
  PTSZ         pszOwner;               // Job Owner
  PTSZ         pszReportFile;          // Report file (fully qualified)
  PTSZ         pszComments;            // Comments or description
  PTSZ         pszBeforeJob;           // Process string before job starts
  PTSZ         pszAfterJob;            // Process string after job ends
  USHORT       usBeforeJobWait;        // Before Job wait time (# minutes)
  USHORT       usJobMethod;            // See Job Methods (B, C)
  USHORT       OnConflictMethod;       // See On Conflict Methods (A, R)
  USHORT       usAlertFlags;           // See Alert Flags
  ASSCHED      Schedule;               // Schedule info
  ASOFINFO     OpenFileInfo;           // Open File info
  ASFAXINFO    FaxInfo;                // Send report file via FAXserve
  PASQJDTEX    pASQJDTEx;              // Data Transfer Extension(s)
  ASEMAILINFO  EmailInfo;              // Send report file via Email (MHS)
  ASPRINTQINFO PrintQInfo;             // Send report file via Print Queue
  ASAPINFO     APInfo;                 // AutoPilot info
  FLAG32       fOptions;               // See Global, B, A, R Options
  PTSZ         pszHost;                // Host machine that processes the job
  ULONG        nFilterItems;           // # of Filter items in list
  PVOID        pvFilterList;           // List of filters
  PTSZ         pszUserDomain;          // User's domain name
  PTSZ         pszUserPW;              // User's password (User name is pszOwner)
  PVOID        pvAlertData;
  USHORT       usPreExitCode;          // Exit code for Application wich run before
  UCHAR        cHostType;              // CUI See Host Server type values in Univdefs.h
  UCHAR        fOptionsXX;             // for UNICODE_JIS_SUPPORT kumad02 
  PTSZ         pszEncryptKey;          // Encryption key
} ASQJDATATRANSFER;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASQJDATATRANSFER FAR  *PASQJDATATRANSFER;
typedef ASQJDATATRANSFER NEAR *NPASQJDATATRANSFER;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASQJDATATRANSFER FAR  *PASQJDATATRANSFER;
typedef ASQJDATATRANSFER NEAR *NPASQJDATATRANSFER;
#else
typedef ASQJDATATRANSFER *PASQJDATATRANSFER;
#endif

//
//  Queue Job structure for Data Transfer (usLevel == LVL_ASQJDT1)
//
typedef struct tagASQJDT1
{
  ULONG    ulJobID;                    // Job ID
  USHORT   usJobType;                  // See Job Type
  USHORT   usJobPosition;              // Job Position
  USHORT   usJobStatus;                // See Job Status
  USHORT   nScriptType;                // Script Type (used by UI)
  ULONG    dtSubmitted;                // Job submit date & time
  ULONG    dtExecuteOn;                // Schedule job at this date & time
  ULONG    nSourceItems;               // Number of source items
  ULONG    nDestItems;                 // Number of dest. items
  PSZ      pszOwner;                   // Job Owner
  PSZ      pszReportFile;              // Report file (fully qualified)
  PSZ      pszComments;                // Comments or description
  PSZ      pszSource;                  // Archiving: Source Path
                                       // Backup   : # Nodes (nSourceItems)
                                       // Restore  : DeviceGroup/TapeName
  PSZ      pszDest;                    // Archiving: Dest Path
                                       // Backup   : DeviceGroup/TapeName
                                       // Restore  : Dest Path
  ULONG    ulLastModTime;              // Last time this job was modified
  PVOID    pvJobMon;                   // Job Monitor, if job active
#ifdef MUX_TAPE
	#if defined(UNICODE_JIS_SUPPORT)
	  ULONG    uniqueMuxID;					// MUX ID for GUI, for finding parent child relationship.
	  USHORT    fOptionsXX;
	  UCHAR    aucReserved[18];				// 24-4 =20
	#else
	  ULONG    uniqueMuxID;					// MUX ID for GUI, for finding parent child relationship.
	  UCHAR    aucReserved[20];				// 24-4 =20
	#endif // UNICODE_JIS_SUPPORT
#else  //MUX_TAPE
	#if defined(UNICODE_JIS_SUPPORT)
	  USHORT    fOptionsXX;
	  UCHAR    aucReserved[22];            // Reserved for future use
	#else
	  UCHAR    aucReserved[24];            // Reserved for future use
	#endif // UNICODE_JIS_SUPPORT
#endif //MUX_TAPE
  UCHAR    szMemberServer[32];
  ULONG	   ulDBProtectFlag;
} ASQJDT1;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASQJDT1 FAR  *PASQJDT1;
typedef ASQJDT1 NEAR *NPASQJDT1;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASQJDT1 FAR  *PASQJDT1;
typedef ASQJDT1 NEAR *NPASQJDT1;
#else
typedef ASQJDT1 *PASQJDT1;
#endif

//
//  Queue Job structure for Data Transfer (usLevel == LVL_ASQJDTCRA)
//

//
// General Flags
//
#define CRAF_OVERWRITETAPE 0x00000001  // If set, overwrite tape
#define CRAF_JOBMODIFIED   0x00000002  // If set, job was modified by GUI.
#define CRAF_JOBCREATED	   0x00000004  // If set, job was created by GUI.
#ifdef	_B2D2T
#define CRAF_B2DFSDFULL	   0x00000008  // If set, B2D hit media full.
#endif

typedef struct tagASQJDTCRA
{
  ULONG    ulJobType;                  // See Job Type
  ULONG    ulTaskID;                   // NLM ID of NLM processing the job
  UCHAR    szJobFileName[16];          // Active Job File Name
  UCHAR    szDeviceGroup[9];           // Tape Device Group Name, TSI_MAX_GROUP_NAME+1 == 9
  UCHAR    szReservedGroup[9];         // Job Engine reserved Group Name
  UCHAR	   szReserved[2];              // Keep it for backward compatible
  ULONG    ulExecuteTime;              // Job Execute Time (Calendar time format).  Next Regular job.
  ULONG    ulLogPos;                   // Daily Log file link (lseek position)
  FLAG32   fFlags;                     // See General Flags
  ULONG    ulTotalKBytes;              // Total KBytes processed last job
  ULONG    ulTotalFiles;               // Total Files processed last job
  UCHAR    szTapeName[24];             // Tape Name to use for execution
  USHORT   usTapeSeq;                  // Tape sequence number
  USHORT   usMakeup;                   // Starting Node for cancelled AP Makeup
  ULONG    ulLastModTime;              // Last time this job was modified
  ULONG    ulTapeID;                   // Tape ID (Random ID)
  UCHAR    szSerialNum[32];            // Tape's Serial Number  - New in Sniper
  UCHAR    szSetName[10];              // Rotation Job Set Name - New in Sniper
  USHORT   usGroupType;                // Tape Group Type for MBO
  UCHAR    DumpLevel;				   // Dump Levels are used for NAS backups
#if defined (MUX_TAPE) || defined  (MULTISTRIPE_SQL)
  UCHAR	   aucReserved[3];			   // 7-4 =3
  ULONG	   jobToken;				   // Uniquely identifying each job (Master and child inside tape engine). Job# was found to be insufficent for this purpose (debatable).
									   // Tapeengine corelates resources reserved by job engine with their respective job.
#else //MUX_TAPE || MULTISTRIPE_SQL
  UCHAR    aucReserved[7];             // Reserved Area of Client Rec Area
#endif //MUX_TAPE || MULTISTRIPE_SQL
} ASQJDTCRA;
#if     defined(_64BIT_ARCH_)
#pragma pointer_size(save)
#pragma pointer_size(short)
typedef ASQJDTCRA FAR  *PASQJDTCRA;
typedef ASQJDTCRA NEAR *NPASQJDTCRA;
#pragma pointer_size(restore)
#elif   defined(_16BIT_ARCH_)
typedef ASQJDTCRA FAR  *PASQJDTCRA;
typedef ASQJDTCRA NEAR *NPASQJDTCRA;
#else
typedef ASQJDTCRA *PASQJDTCRA;
#endif

//
//  Structure Level - usLevel
//
#define LVL_ASQJDATATRANSFER 0         // Use ASQJDATATRANSFER structure level
#define LVL_ASQJDT1          1         // Use ASQJDT1 structure level

//
//  Security Level - GUI can require it for manipulations with queue
//
//  Performs no authentication.
#define SECURITY_LVL_NONE      (RPC_C_AUTHN_LEVEL_NONE)

//  Authenticates that all data received is from the expected client
#define SECURITY_LVL_ACCESSCHECK  (RPC_C_AUTHN_LEVEL_PKT)

//  Authenticates and verifies that none of the data
//  transferred between client and server has been modified.
#define SECURITY_LVL_INTEGRITYCHECK  (RPC_C_AUTHN_LEVEL_PKT_INTEGRITY)

//  Authenticates all previous levels and encrypts the
//  argument value of each remote procedure call.
#define SECURITY_LVL_PRIVACYCHECK  (RPC_C_AUTHN_LEVEL_PKT_PRIVACY)

//////////////////////////////////////////////////////////////////////////
//ASDDISKEX::fAgentOptions  begin

//ASDDISKEX::fAgentOptions - Agent for ARCserve Database  begin

// SQLE Backup Method Filter
#define ASDBSQLE_B_FILTER_METHOD		0x00000003

//Use Global or Rotation phase (Full vs. Differential; Incremental as Differential)
#define ASDBSQLE_B_GLOBAL 0x00000000

//Perform a Full Database backup of every System and Production database in the ASDB Express instance.
#define ASDBSQLE_B_FULL   0x00000001

//Perform a Differential backup of every Production database in the ASDB Express instance.
#define ASDBSQLE_B_DIFF   0x00000002

//Instance-wide DBCC before backup for ASDB Express instance.
#define ASDBSQLE_B_DBCC_BEFORE  0x00000100

//Instance-wide DBCC before after for ASDB Express instance.
#define ASDBSQLE_B_DBCC_AFTER   0x00000200

//Instance-wide DBCC before backup failure for ASDB Express instance should not abort backup.
#define ASDBSQLE_B_DBCC_PROCEED 0x00000400

//Instance-wide DBCC for ASDB Express instance perform Physical check only.
#define ASDBSQLE_B_DBCC_PHYSONLY 0x00000800

//Instance-wide DBCC for ASDB Express instance do not check Indexes.
#define ASDBSQLE_B_DBCC_NOINDEX	0x00001000

// Override Inherited Option Flags 
#define ASDBSQLE_B_SPECIAL_NO_INHERIT 0x08000000

//Instance-level Backup flag, indicating that Disaster Recovery Elements should be dynamically added to the source list.
#define MSSQL_IB_DR_INCLUDE			0x00010000

//ASDDISKEX::fAgentOptions - Agent for ARCserve Database  end


#ifdef E14_TASK	//<XUVNE01> E14 support 2009-03-06
//ASDDISKEX::fAgentOptions - Agent options for Exchange E14  begin
#define ASDDXAO_EXCHDB_BACKUP_FULL							ASDIO_OPTION_EXCHDB_BACKUP_FULL	
#define ASDDXAO_EXCHDB_BACKUP_COPY							ASDIO_OPTION_EXCHDB_BACKUP_COPY	
#define ASDDXAO_EXCHDB_BACKUP_INCR							ASDIO_OPTION_EXCHDB_BACKUP_INCR
#define ASDDXAO_EXCHDB_BACKUP_DIFF							ASDIO_OPTION_EXCHDB_BACKUP_DIFF

#define ASDDXAO_EXCHDB_BACKUP_FROM_REPLICA					ASDIO_OPTION_EXCHDB_BACKUP_FROM_REPLICA
#define ASDDXAO_EXCHDB_BACKUP_FROM_ACTIVE				    ASDIO_OPTION_EXCHDB_BACKUP_FROM_ACTIVE
#define ASDDXAO_EXCHDB_BACKUP_USE_ACTIVE_IF_NO_REPLICA      ASDIO_OPTION_EXCHDB_BACKUP_USE_ACTIVE_IF_NO_REPLICA

#define ASDDXAO_EXCHDB_BACKUP_DAG_FIRST                     ASDIO_OPTION_EXCHDB_BACKUP_DAG_FIRST
#define ASDDXAO_EXCHDB_BACKUP_DAG_LAST                      ASDIO_OPTION_EXCHDB_BACKUP_DAG_LAST
#define ASDDXAO_EXCHDB_BACKUP_DAG_CUSTOM                    ASDIO_OPTION_EXCHDB_BACKUP_DAG_CUSTOM

#define ASDDXAO_EXCHDB_BACKUP_TIMEBASED                     ASDIO_OPTION_EXCHDB_BACKUP_TIMEBASED              
#define ASDDXAO_EXCHDB_BACKUP_PURGE_AFTER_BACKUP            ASDIO_OPTION_EXCHDB_BACKUP_PURGE_AFTER_BACKUP

#define ASDDXAO_EXCHDB_BACKUP_USE_GLOBAL_SCHEDULED_BACKUP_METHOD        0x00100000      // Use globally scheduled backup method. Takes effect in global agent option dialog
#define ASDDXAO_EXCHDB_BACKUP_USE_METHOD_OF_GLOBAL_OPTION               0x00200000      // Use backup method specified in global agent option. Takes effect in disk level
#define ASDDXAO_EXCHDB_BACKUP_USE_SOURCE_OF_GLOBAL_OPTION               0x00400000      // Use backup source specified in global agent option. Takes effect in disk level

//ASDDISKEX::fAgentOptions - Agent options for Exchange E14  end
#endif//E14_TASK

//ASDDISKEX::fAgentOptions  end
//////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////
//ASDTAPEX::fAgentOptions  begin

//ASDTAPEX::fAgentOptions - Agent for ARCserve Database  begin

//Use the current BAB Server's ASDB location as the Original Location.
#define ASDBSQLE_R_UPDATEORIGLOC 0x00000001

//When restoring a multi-database ASDB to a non-ASDB-owned SQL Server instance, consolidate the 24 databases into a normal single-database ASDB.
#define ASDBSQLE_R_NOCONSOLIDATE 0x00000002

//Only valid for sessions with BSF2_LISTSPREREQS  flag.  Use information stored in Differential session to find and restore pre-requisite Full session.  (Mastering Agent)
#define ASDBSQLE_R_SEEKPREREQ		 0x00000004

// Recover Type Filter
#define ASDBSQLE_R_FILTER_RECOVER	0x00000010

//Bring ASDB databases online after restore.  No further sessions will be restored.
#define ASDBSQLE_R_RECOVER			 0x00000000

//Session being restored is Full, and a Differential session will be restored afterwards.  Skip Transaction Log restores and leave ASDB databases in Loading state.
#define ASDBSQLE_R_NORECOVER		 0x00000010

//Instance-wide DBCC after for ASDB Express instance.
#define ASDBSQLE_R_DBCC_AFTER		 0x00000100

//Instance-wide DBCC for ASDB Express instance perform Physical check only.
#define ASDBSQLE_R_DBCC_PHYSONLY       0x00000200

//Instance-wide DBCC for ASDB Express instance do not check Indexes.
#define ASDBSQLE_R_DBCC_NOINDEXES      0x00000400

//Apply the "With Replace" option to database restores.
#define ASDBSQLE_R_REPLACE		 0x00020000

//Move Rule for files in ASDB, relocate to specified drive letter.
#define ASDBSQLE_R_MOVEDRIVE 0x00040000

//Move Rule for files in ASDB, relocate to specified directory path.
#define ASDBSQLE_R_MOVEDIR 0x00080000

//Overwriting ASDB from a different domain; Cache ARCserve Topology Data Before Restore
#define ASDBSQLE_R_ASDB_CACHE_TOPOLOGYDATA	0x40000000

//Overwriting ASDB from a different domain; Write ARCserve Topology Data From Cache After Restore
#define ASDBSQLE_R_ASDB_WRITE_TOPOLOGYDATA	0x80000000

//ASDTAPEX::fAgentOptions - Agent for ARCserve Database  end

//ASDTAPEX::fAgentOptions  end
//////////////////////////////////////////////////////////////////////////

#define QJDTXO_R_STOPDBENGINE  0x00010000 //Stop Database Engine before restoring.
#define ASDTO_EX_STOPDBENGINE  QJDTXO_R_STOPDBENGINE

#define QJDTXO_R_STARTDBENGINE 0x00020000 //Restart Database Engine after restoring.
#define ASDTO_EX_STARTDBENGINE QJDTXO_R_STARTDBENGINE

#ifdef __cplusplus
}
#endif

#if	defined(WIN32)
#include <asqjobfn.h>
#include <cui.h>
#endif

#endif  /* _INC_ASQJOB */
