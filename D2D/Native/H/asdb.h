#ifndef ASDB_H
#define ASDB_H

#ifdef __cplusplus            
  extern "C" {                     /* avoid name-mangling if used from C++ */
#endif /* __cplusplus */

#include "bebenabled.h"

#define AS_SP3_BUILD_NUMBER   580
#define AS_70_BUILD_NUMBER   950

#if defined(UNICODE_JIS_SUPPORT)
	#define _MAX_UNICODE_NETPATH 2048
#endif // UNICODE_JIS_SUPPORT
#ifndef _MAX_NETPATH
#define _MAX_NETPATH 1024
#endif

#ifndef MAX_PATH
#define MAX_PATH 260
#endif

#if defined(UNICODE_JIS_SUPPORT)
	#define ASDB_UNICODE_MAX_PATH 1024
#endif // UNICODE_JIS_SUPPORT

#define ASDB_MAX_PATH 260

#define ASDB_MAX_PATH_EX 512

/* Raima Database Server Version R 1.1.0 BR1 */
#ifndef ASDB_ADDR 
typedef unsigned long ASDB_ADDR;
#endif

#ifndef DWORD
typedef unsigned long DWORD;
#endif

#ifndef BOOL
typedef int BOOL;
#endif

#ifndef PVOID
typedef void *PVOID;
#endif

#ifndef MAX_COMPUTERNAME_LENGTH
#define MAX_COMPUTERNAME_LENGTH 15
#endif

#ifndef _EXTEND_MAX_NT_PATH
#define _EXTEND_MAX_NT_PATH 4096
#endif

#ifndef CALLBACK
#if (_MSC_VER >= 800) || defined(_STDCALL_SUPPORTED)
#define CALLBACK    __stdcall
#define WINAPI      __stdcall
#define WINAPIV     __cdecl
#define APIENTRY    WINAPI
#define APIPRIVATE  __stdcall
#define PASCAL      __stdcall
#else
#define CALLBACK
#define WINAPI
#define WINAPIV
#define APIENTRY    WINAPI
#define APIPRIVATE
#define PASCAL      pascal
#endif
#endif

#pragma pack(1)

enum {ENUM_BY_NONE=0, ENUM_BY_NAME, ENUM_BY_ID};

#define MAX_FTS         100


#ifdef ASDB_D2D2T
#define ASDBID_MAX_ID   17		// Added 2 table for D2D2T (ASDBID_SESMAP, ASDBID_STAGING, ASDBID_JOBMAP,ASDBID_SESSGUI)
#else // ASDB_D2D2T
#define ASDBID_MAX_ID   13
#endif // ASDB_D2D2T

//sonle01 20080303
#define NAMEID_INCATFILE		0x80000000
#define NAMEID_INUNICODERANGE   0x40000000

// kimwo01 20081020 (For ASDB_GetSessionListEx2)
#define GETSESSION_ALL						0
#define GETSESSION_STARTTIME_RANGE			1
#define GETSESSION_GREATER_EQUAL_STARTTIME	2
#define GETSESSION_LESS_EQUAL_STARTTIME		3


#define ASDB_DEBUGLEVEL_INFO			1
#define ASDB_DEBUGLEVEL_WARNING			2
#define ASDB_DEBUGLEVEL_ERROR			3
#define ASDB_DEBUGLEVEL_CRITICAL		4
#define ASDB_DEBUGLEVEL_DEBUG			5
#define ASDB_DEBUGLEVEL_PERFORMANCE		10

#define ASDB_GetDebugLogLevel() ((g_viewDebugLog && !IsBadReadPtr((VOID*)g_viewDebugLog,sizeof(DWORD))) ? (*((DWORD*)g_viewDebugLog)) : 0)

// _R12.x_(DASHBOARD)(4/18/2008)
#define NOT_AVAILABLE		0
#define NOT_ENABLED		1
#define ENABLED			2

// R12.v (Getcatfile file extention) :9/4/2008
#define ASDB_FILE_EXT_TMP	100

// ASMsg Column Schema 
#define ASMSG_SESSIONID 0x00000001
#define ASMSG_QFANUMBER 0x00000002
#define ASMSG_QFAOFFSET 0x00000004
#define ASMSG_OBJTYPE   0x00000008
#define ASMSG_OBJDATE   0x00000010
#define ASMSG_OBJFLAGS  0x00000020
#define ASMSG_LOBJSIZE  0x00000040
#define ASMSG_HOBJSIZE  0x00000080
#define ASMSG_OBJNAME   0x00000100
#define ASMSG_OBJINFO   0x00000200
#define ASMSG_LOBJSID   0x00000400
#define ASMSG_HOBJSID   0x00000800
#define ASMSG_LOBJPID   0x00001000
#define ASMSG_HOBJPID   0x00002000
#define ASMSG_LOBJBOD   0x00004000
#define ASMSG_HOBJBOD   0x00008000
#define ASMSG_OBJAUX    0x00010000

#define ALL_UPDATE_FLAGS 0x0001FFFF


#ifdef DASHBOARD_SUPPORT

//DASHBOARD Value defined for Dashboard Project
#define ASNODE_FULLNODESELECTED_UNKNOWN		0x00000000			//unknow
#define ASNODE_FULLNODESELECTED_TRUE		0x00000001			//full node is selected
#define ASNODE_FULLNODESELECTED_FALSE		0x00000002			//partial node is selected

#define ASNODE_DRBACKUP_UNKNOWN				0x00000000			//unknow
#define ASNODE_DRBACKUP_TRUE				0x00000001			//Will Backup DR 
#define ASNODE_DRBACKUP_FALSE				0x00000002			//Will Not Backup DR Session
#define ASNODE_DRBACKUP_TRUE_VM				0x00000003			//VM Raw session generatd by Raw/Mix or RAW/MIX plus IFS backup
#define ASNODE_DRBACKUP_SUCCESS				0x00000004			//successfully backed up DR

#define ASJOB_MAKEUPJOBCONFIGURE_UNKNOWN	0x00000000			//unknow
#define ASJOB_MAKEUPJOBCONFIGURE_TRUE		0x00000001			//submit make up job
#define ASJOB_MAKEUPJOBCONFIGURE_FALSE		0x00000002			//not submit makeup job

#define ASJOB_COMPRESSION_UNKNOWN			0x00000000			//unknow
#define ASJOB_COMPRESSION_TRUE				0x00000001			//compression is set
#define ASJOB_COMPRESSION_FALSE				0x00000002			//compression is not set

#define ASJOB_FLAG_BEGIN_OF_JOB				0x00000000
#define ASJOB_FLAG_END_OF_JOB				0x00000001

#define ASJOB_ENCRYPTION_UNKNOWN			0x00000000			//unknow
#define ASJOB_ENCRYPTION_TRUE				0x00000001			//encryption is set
#define ASJOB_ENCRYPTION_FALSE				0x00000002			//encryption is not set

#define ASJOB_ALERTCONFIG_UNKNOWN			0x00000000			//unknow
#define ASJOB_ALERTCONFIG_TRUE				0x00000001			//alert is configured
#define ASJOB_ALERTCONFIG_FALSE				0x00000002			//alert is not configured

#define ASJOB_PREPOST_UNKNOWN				0x00000000			//unknow
#define ASJOB_PREPOST_TRUE					0x00000001			//prepost is configured
#define ASJOB_PREPOST_FALSE					0x00000002			//prepost is not configured

#define ASJOB_VIRUSSCAN_UNKNOWN				0x00000000			//unknow
#define ASJOB_VIRUSSCAN_TRUE				0x00000001			//virus scan is configured
#define ASJOB_VIRUSSCAN_FALSE				0x00000002			//virus scan is not configured

#define ASJOB_FILTER_UNKNOWN				0x00000000			//unknow
#define ASJOB_FILTER_TRUE					0x00000001			//filter is configured
#define ASJOB_FILTER_FALSE					0x00000002			//filter is not configured

#define ASJOB_VERIFYAFTERBACKUP_UNKNOWN		0x00000000			//unknow
#define ASJOB_VERIFYAFTERBACKUP_TRUE		0x00000001			//verify after backup is configured
#define ASJOB_VERIFYAFTERBACKUP_FALSE		0x00000002			//verify after backup is not configured

#define ASJOB_MAKEUPJOBSTATUS_UNKNOWN		0x00000000			//unknow
#define ASJOB_MAKEUPJOBSTATUS_CREATE		0x00000001			//makeup job is submitted
#define ASJOB_MAKEUPJOBSTATUS_ACTIVE		0x00000002			//makeup job is running
#define ASJOB_MAKEUPJOBSTATUS_DONE			0x00000003			//makeup job is done, no more makeup is submitted

#define ASJOB_JOBEXECTYPE_UNKNOWN			0x00000000			//unknow
#define ASJOB_JOBEXECTYPE_GFS				0x00000001			//GFS
#define ASJOB_JOBEXECTYPE_ROTATION			0x00000002			//Rotation
#define ASJOB_JOBEXECTYPE_SIMPLE_REPEAT		0x00000003			//Simple Repeat
#define ASJOB_JOBEXECTYPE_SIMPLE_ONCE		0x00000004			//Simple Once

#define ASTPSES_ENCLOCATION_BACKUP_UNKNOWN		0x00000000		//unknow
#define ASTPSES_ENCLOCATION_BACKUP_AGENT		0x00000001		//enc by agent
#define ASTPSES_ENCLOCATION_BACKUP_SERVER		0x00000002		//enc by server during backup
#define ASTPSES_ENCLOCATION_MIGRATION_SERVER	0x00000003		//enc by server during migration
#define ASTPSES_ENCLOCATION_NO_ENC				0x00000004		//no encryption applied to the session

#define  ASTPSES_ENCTYPE_UNKNOW					0x00000000		//encryption type: unknow
#define  ASTPSES_ENCTYPE_PASSWORDPROTECT_TRUE	0x00000001		//encryption type: password protection
#define  ASTPSES_ENCTYPE_HW						0x00000002		//encryption type: hardware
#define  ASTPSES_ENCTYPE_SW						0x00000003		//encryption type: software
#define  ASTPSES_ENCTYPE_NOT_CATEGORIED			0x00000004		//encryption type: not use encryption

#define ASTPSES_USE_STROREDPASSWORD_UNKNOWN	0x00000000			//unknow
#define ASTPSES_USE_STROREDPASSWORD_TRUE	0x00000001			//use stored password
#define ASTPSES_USE_STROREDPASSWORD_FALSE	0x00000002			//not use stored password

#endif //DASHBOARD_SUPPORT

//For R12.5
#define PRODUCT_VERSION_MAJOR   12  
#define PRODUCT_VERSION_MINOR    5


typedef enum  _tag_ASDBNAMEID 
{
   ASDBID_COPY,
   ASDBID_JOB,
   ASDBID_TAPE,
   ASDBID_TAPEDRV,
   ASDBID_TPSDAT,
   ASDBID_MEDIA,
   ASDBID_OBJECT,
   ASDBID_REMOTEHOST,
   ASDBID_MMO,
#ifdef ASDB_D2D2T
   ASDBID_SESMAP,
#endif // ASDB_D2D2T
   ASDBID_MSG,
   ASDBID_MSGDAT,
   ASDBID_LOGERR,
#ifdef ASDB_D2D2T
   ASDBID_STAGING,
   ASDBID_JOBMAP,
   ASDBID_SESSGUI,
#endif // ASDB_D2D2T
   DBID_MAX_ID
} ASDBNAMEID;

//#ifndef ASDBID_MSG
//#define ASDBID_MSG (ASDBID_MMO+1)
//#define ASDBID_MSGDAT (ASDBID_MMO+2)
//#endif 

typedef enum _tag_ASDBOPENMODEID
{
   ASDB_MODE_SHARE,
   ASDB_MODE_SHARE_NO_TRANSACTION,
   ASDB_MODE_SHARE_QUICK,
   ASDB_MODE_EXCLUSIVE,
   ASDB_MODE_EXCLUSIVE_NO_TRANSACTION,
   ASDB_MODE_EXCLUSIVE_QUICK,
   ASDB_MODE_TEMPORARY,
   ASDB_MODE_READ,
   ASDB_MODE_READ_NO_TRANSACTION,
   ASDB_MODE_MAX_ID
}ASDBOPENMODEID;

#define BACKUPDAEMON_BUFSIZE    1024
#define COPYDAEMON_BUFSIZE      1024
#define BACKUP_DB_SEMAPHORE   TEXT("BackupASDB")


//sonle01_200801
#define ASDB_CP_UNICODE		0
#define ASDB_CP_1252		-1

#define SERVERLIST4RMAN		0x1000

typedef struct _tagASDB_JobRec 
{
   long  id;
   short type;
   short status;
   long  starttime;
   long  endtime;
   char  owner[32];
   char  comment[96];
}ASDB_JobRec, * PASDB_JobRec;

// Oripin: UNICODE_JIS Support :redbh03
typedef struct _tagASDB_JobRecW 
{
	long  id;
	short type;
	short status;
	long  starttime;
	long  endtime;
	char  owner[32];
	char  comment[96];
}ASDB_JobRecW, * PASDB_JobRecW;
typedef struct _tagASDB_JobExRec 
{
   long  id;
   short type;
   short status;
   long  starttime;
   long  endtime;
   char  owner[32];
   char  comment[96];
   long  handle;
   char  host[16];
   char  setname[12];            //SetName[12];      // Up to 8 characters   
   long  jobno;
}ASDB_JobExRec, * PASDB_JobExRec;
typedef struct _tagASDB_JobExRecW
{
	long  id;
	short type;
	short status;
	long  starttime;
	long  endtime;
	wchar_t  owner[32];
	wchar_t  comment[96];
	long  handle;
	wchar_t  host[16];
	wchar_t  setname[12];            //SetName[12];      // Up to 8 characters   
	long  jobno;
}ASDB_JobExRecW, * PASDB_JobExRecW;

// DASHBOARD (4/29/2008): 
#define ASDB_MEDIUM_VTL   999  	// This value can be deployed by "MBOsavesetno"field in ASDB_TapeRec structure
				// This Value is to use ASDB_SetMediaProperty for special action.

typedef struct _tagASDB_TapeRec 
{
   long  id;
   char  tapename[24];
   short randomid;
   short seqnum;
   short tapetype;
   short formatcode;
   short mediacode;
   short densitycode;
   long  firstformat;
   long  lastformat;
   long  expiredate;
   long  destroyed;
   short ttlraidtapes;
   short curraidtapes;					//Start from ASNT.71, curraidtapes store the raid tape no.(id)
   long  blocksize;

   long  overwritepass;
   long  udepass;
   long  curmediaerr;
   long  curswerr;
   long  cursrerr;
   long  curusagetime;
   long  curkbwritten;
   long  ttlwritepass;
   long  ttlreadpass;
   long  ttlmediaerr;
   long  ttlswerr;
   long  ttlsrerr;
   long  ttlusagetime;
   long  ttlkbwritten;

   //media info
   char  poolname [16];
   char  serialnum [32];
   short tapestatus;
   short poolsetstatus;
   short backuptype;
   long  lastwrite;                 //lastwritedate
   long  lastread;                  //lastreaddate
   short locatstatus;
   long  locatsentdate;
   long  locatid;

   //MBO info
   long  MBOsavesetno;

   long  tapeflag;

   //field not used by ASMGR.EXE in RDS tape_record
   //long curwritepass;
   //long curreadpass;
   //char treserved2[20];
   //char ttlraidtapes;
   //char curraidtapes;
} ASDB_TapeRec, *PASDB_TapeRec;

// Oripin: UNICODE_JIS Support dayra01
typedef struct _tagASDB_TapeRecW 
{
	long  id;
	wchar_t  tapename[24];
	short randomid;
	short seqnum;
	short tapetype;
	short formatcode;
	short mediacode;
	short densitycode;
	long  firstformat;
	long  lastformat;
	long  expiredate;
	long  destroyed;
	short ttlraidtapes;
	short curraidtapes;					//Start from ASNT.71, curraidtapes store the raid tape no.(id)
	long  blocksize;

	long  overwritepass;
	long  udepass;
	long  curmediaerr;
	long  curswerr;
	long  cursrerr;
	long  curusagetime;
	long  curkbwritten;
	long  ttlwritepass;
	long  ttlreadpass;
	long  ttlmediaerr;
	long  ttlswerr;
	long  ttlsrerr;
	long  ttlusagetime;
	long  ttlkbwritten;

	//media info
	wchar_t  poolname [16];
	wchar_t  serialnum [32];
	short tapestatus;
	short poolsetstatus;
	short backuptype;
	long  lastwrite;                 //lastwritedate
	long  lastread;                  //lastreaddate
	short locatstatus;
	long  locatsentdate;
	long  locatid;

	//MBO info
	long  MBOsavesetno;

	long  tapeflag;

	//field not used by ASMGR.EXE in RDS tape_record
	//long curwritepass;
	//long curreadpass;
	//char treserved2[20];
	//char ttlraidtapes;
	//char curraidtapes;
} ASDB_TapeRecW, *PASDB_TapeRecW;

//R12.v
// R12.v (7/11/2008)
typedef struct _tagASDB_TapeRec2 
{
   long  id;
   char  tapename[24];
   short randomid;
   short seqnum;
   short tapetype;
   short formatcode;
   short mediacode;
   short densitycode;
   long  firstformat;
   long  lastformat;
   long  expiredate;
   long  destroyed;
   short ttlraidtapes;
   short curraidtapes;					//Start from ASNT.71, curraidtapes store the raid tape no.(id)
   long  blocksize;

   long  overwritepass;
   long  udepass;
   long  curmediaerr;
   long  curswerr;
   long  cursrerr;
   long  curusagetime;
   long  curkbwritten;
   long  ttlwritepass;
   long  ttlreadpass;
   long  ttlmediaerr;
   long  ttlswerr;
   long  ttlsrerr;
   long  ttlusagetime;
   long  ttlkbwritten;

   //media info
   char  poolname [16];
   char  serialnum [32];
   short tapestatus;
   short poolsetstatus;
   short backuptype;
   long  lastwrite;                 //lastwritedate
   long  lastread;                  //lastreaddate
   short locatstatus;
   long  locatsentdate;
   long  locatid;

   //MBO info
   long  MBOsavesetno;
   long  tapeflag;
      
   char  reserved[128];		// future use...
} ASDB_TapeRec2, *PASDB_TapeRec2;
typedef struct _tagASDB_TapeRec2W 
{
	long  id;
	wchar_t  tapename[24];
	short randomid;
	short seqnum;
	short tapetype;
	short formatcode;
	short mediacode;
	short densitycode;
	long  firstformat;
	long  lastformat;
	long  expiredate;
	long  destroyed;
	short ttlraidtapes;
	short curraidtapes;					//Start from ASNT.71, curraidtapes store the raid tape no.(id)
	long  blocksize;

	long  overwritepass;
	long  udepass;
	long  curmediaerr;
	long  curswerr;
	long  cursrerr;
	long  curusagetime;
	long  curkbwritten;
	long  ttlwritepass;
	long  ttlreadpass;
	long  ttlmediaerr;
	long  ttlswerr;
	long  ttlsrerr;
	long  ttlusagetime;
	long  ttlkbwritten;

	//media info
	wchar_t  poolname [16];
	wchar_t  serialnum [32];
	short tapestatus;
	short poolsetstatus;
	short backuptype;
	long  lastwrite;                 //lastwritedate
	long  lastread;                  //lastreaddate
	short locatstatus;
	long  locatsentdate;
	long  locatid;

	//MBO info
	long  MBOsavesetno;

	long  tapeflag;
	char  reserved[128];		// future use...
} ASDB_TapeRec2W, *PASDB_TapeRec2W;


typedef struct _TapeRecStruct
{
	char   pTapeName[24];
	ULONG  randomID;
	ULONG  seqnum;

} TapeRecStruct;

// Oripin: UNICODE_JIS Support dayra01
typedef struct _TapeRecStructW
{
	wchar_t   pTapeName[24];
	ULONG  randomID;
	ULONG  seqnum;

} TapeRecStructW;
typedef struct _TapeRecList
{
	int nLength;
	TapeRecStruct* pTapeRecords;
} TapeRecList;

// Oripin: UNICODE_JIS Support dayra01
typedef struct _TapeRecListW
{
	int nLength;
	TapeRecStructW* pTapeRecords;
} TapeRecListW;
// Output
//
typedef struct _ASDB_TapeRecEx
{
	ASDB_TapeRec cTapeRecord;
	BOOL bValid; // TRUE, if record is valid 
} ASDB_TapeRecEx;

// Oripin: UNICODE_JIS Support dayra01
typedef struct _ASDB_TapeRecExW
{
	ASDB_TapeRecW cTapeRecord;
	BOOL bValid; // TRUE, if record is valid 
} ASDB_TapeRecExW;
typedef struct _ASDB_TapeRecExList
{
	int nLength;
	ASDB_TapeRecEx* pTapeRecords;		
} ASDB_TapeRecExList;

typedef ASDB_TapeRecExList *PASDB_TapeRecExList;
// Oripin: UNICODE_JIS Support dayra01
typedef struct _ASDB_TapeRecExListW
{
	int nLength;
	ASDB_TapeRecExW* pTapeRecords;		
} ASDB_TapeRecExListW;

typedef ASDB_TapeRecExListW *PASDB_TapeRecExListW;


typedef struct _tagASDB_TapeExRec 
{
   long  id;
   char  tapename[24];
   short randomid;
   short seqnum;
   short tapetype;
   short formatcode;
   short mediacode;
   short densitycode;
   long  firstformat;
   long  lastformat;
   long  expiredate;
   long  destroyed;
   short ttlraidtapes;
   short curraidtapes;
   long  blocksize;

   long  overwritepass;
   long  udepass;
   long  curmediaerr;
   long  curswerr;
   long  cursrerr;
   long  curusagetime;
   long  curkbwritten;
   long  ttlwritepass;
   long  ttlreadpass;
   long  ttlmediaerr;
   long  ttlswerr;
   long  ttlsrerr;
   long  ttlusagetime;
   long  ttlkbwritten;

   //media info
   char  poolname [16];
   char  serialnum [32];
   short tapestatus;
   short poolsetstatus;
   short backuptype;
   long  lastwrite;                 //lastwritedate
   long  lastread;                  //lastreaddate
   short locatstatus;
   long  locatsentdate;
   long  locatid;

   //MBO info
   long  MBOsavesetno;
   long  tapeflag;

   //field not used by ASMGR.EXE in RDS tape_record
   char thostname [64];
   long curreadpass   ;
   long curwritepass  ;
   long full          ;
   long percentused   ;
   char uuid[20]      ;
   long spanhsize     ;             // BEB 10.1 (was treserved0) // This field is also used by MMO. Don't change the name.
   long spanlsize     ;             // BEB 10.1 (was treserved1)
} ASDB_TapeExRec, *PASDB_TapeExRec;

typedef struct _tagASDB_TapeExRecW 
{
	long  id;
	wchar_t  tapename[24];
	short randomid;
	short seqnum;
	short tapetype;
	short formatcode;
	short mediacode;
	short densitycode;
	long  firstformat;
	long  lastformat;
	long  expiredate;
	long  destroyed;
	short ttlraidtapes;
	short curraidtapes;
	long  blocksize;

	long  overwritepass;
	long  udepass;
	long  curmediaerr;
	long  curswerr;
	long  cursrerr;
	long  curusagetime;
	long  curkbwritten;
	long  ttlwritepass;
	long  ttlreadpass;
	long  ttlmediaerr;
	long  ttlswerr;
	long  ttlsrerr;
	long  ttlusagetime;
	long  ttlkbwritten;

	//media info
	wchar_t  poolname [16];
	wchar_t  serialnum [32];
	short tapestatus;
	short poolsetstatus;
	short backuptype;
	long  lastwrite;                 //lastwritedate
	long  lastread;                  //lastreaddate
	short locatstatus;
	long  locatsentdate;
	long  locatid;

	//MBO info
	long  MBOsavesetno;
	long  tapeflag;

	//field not used by ASMGR.EXE in RDS tape_record
	wchar_t thostname [64];
	long curreadpass   ;
	long curwritepass  ;
	long full          ;
	long percentused   ;
	char uuid[20]      ;
	long spanhsize     ;             // BEB 10.1 (was treserved0) // This field is also used by MMO. Don't change the name.
	long spanlsize     ;             // BEB 10.1 (was treserved1)
} ASDB_TapeExRecW, *PASDB_TapeExRecW; // dayra01

#ifdef SESSNUM_INCREASE
typedef struct _tagASDB_SessRec_Old 
{
   long  id;
   long  jobid;
   long  tapeid;
   short status;
   short sestype;
   unsigned short sesnum;
   short sesmethod;
   long  sesflags;
   long  qfablocknum;
   long  starttime;
   long  endtime;
   long  totalkb;
   long  totalfiles;
   long  totalmissed;
   long  srchostid;
   long  srcpathid;
   long  ownerid;
   short streamnum;
#if defined(MIDL_INVOKED)
   long  reserved;
#else
   short tapeseq_end;
   short fsname_length;
#endif
}ASDB_SessRec_Old, *PASDB_SessRec_Old;

typedef struct _tagASDB_SessRec 
{
   long  id;
   long  jobid;
   long  tapeid;
   short status;
   short sestype;
   unsigned short sesnum_old;
   short sesmethod;
   long  sesflags;
   long  qfablocknum;
   long  starttime;
   long  endtime;
   long  totalkb;
   long  totalfiles;
   long  totalmissed;
   long  srchostid;
   long  srcpathid;
   long  ownerid;
   short streamnum;
#if defined(MIDL_INVOKED)
   long  reserved;
#else
   short tapeseq_end;
   short fsname_length;
#endif
   short  reserved2;
   long sesnum;
}ASDB_SessRec, *PASDB_SessRec;
#else
typedef struct _tagASDB_SessRec 
{
   long  id;
   long  jobid;
   long  tapeid;
   short status;
   short sestype;
   unsigned short sesnum;
   short sesmethod;
   long  sesflags;
   long  qfablocknum;
   long  starttime;
   long  endtime;
   long  totalkb;
   long  totalfiles;
   long  totalmissed;
   long  srchostid;
   long  srcpathid;
   long  ownerid;
   short streamnum;
#if defined(MIDL_INVOKED)
   long  reserved;
#else
   short tapeseq_end;
   short fsname_length;
#endif
}ASDB_SessRec, *PASDB_SessRec;
#endif

#define MAX_MESSAGE_RECORD			1000
#define MAX_OBJ_DATA_SIZE			32*1024 // DO NOT > 64K

typedef struct _tagASDB_MsgRec
{
   long sessid;
   long qfachunknum;
   long qfachunkoffset;
   long objtype;
   long objdate;
   long objflags;
   long lobjsize;
   long hobjsize;
   char objname[256];
   char objinfo[64];
   long lobjselfid;
   long hobjselfid;
   long lobjparentid;
   long hobjparentid;
   long lobjbody;
   long hobjbody;
   long objaux;
   long reserved;
}ASDB_MsgRec, *PASDB_MsgRec;
//Oripin_JIS_Support: kumga04
typedef struct _tagASDB_MsgRecW
{
	long sessid;
	long qfachunknum;
	long qfachunkoffset;
	long objtype;
	long objdate;
	long objflags;
	long lobjsize;
	long hobjsize;
	wchar_t objname[256];
	wchar_t objinfo[64];
	long lobjselfid;
	long hobjselfid;
	long lobjparentid;
	long hobjparentid;
	long lobjbody;
	long hobjbody;
	long objaux;
	long reserved;
	unsigned int cp_flag;
}ASDB_MsgRecW, *PASDB_MsgRecW;

typedef struct _tagASDB_MsgDatRec
{
   long sessid;
   long lselfid;
   long hselfid;
   long objsize;
   char objbin[32];
}ASDB_MsgDatRec, *PASDB_MsgDatRec;


typedef struct _tagASDB_DetailRec
{
   long  sesid;
   long  pathid;
   long  filedate;
   long  lsize;                    //low file size
   long  hsize;                    //high file size
   long  qfachunknum;
   long  qfachunkoffset;
   long  shortnameid;              //No meaning for this field, could be use for sth else
   long  longnameid;
   short datatype;
   short namesp;

   //new for ODBC
   long  fileattr;
   long  streamnum;
   long detailflag;              //Up to 620, this field is dummy.
                                 // In 6.6, this field is only for Unix platform
}ASDB_DetailRec, *PASDB_DetailRec;


typedef struct _tagASDB_TapeDrvRec
{
   unsigned long id;                    // for RDS it is the DB_ADDR
   short adapterid;
   short adapterbusid;
   short scsiid;
   short lun;
   short devicetype;
   char productname[18];
   char productrevision[8];
   char prevrevision[8];
   char vendorname[10];
   char compliance;
   char tdreserved1;
   long hdcleancount;
   long hdcleandate;
   long tdusagetime;
   long tdblocksize;
   long tdttlmbwritten;
   long tdttlmediaerr;
   long tdttlswerr;
   short tddestroyed;
   short tdreserved2;
   char tdreserved3[4];
}ASDB_TapeDrvRec;
typedef ASDB_TapeDrvRec *PASDB_TapeDrvRec;

//Oripin_JIS_Support: kumga04
typedef struct _tagASDB_TapeDrvRecW
{
	unsigned long id;                    // for RDS it is the DB_ADDR
	short adapterid;
	short adapterbusid;
	short scsiid;
	short lun;
	short devicetype;
	wchar_t productname[18];
	wchar_t productrevision[8];
	wchar_t prevrevision[8];
	wchar_t vendorname[10];
	char compliance;
	char tdreserved1;
	long hdcleancount;
	long hdcleandate;
	long tdusagetime;
	long tdblocksize;
	long tdttlmbwritten;
	long tdttlmediaerr;
	long tdttlswerr;
	short tddestroyed;
	short tdreserved2;
	char tdreserved3[4];
}ASDB_TapeDrvRecW;
typedef ASDB_TapeDrvRecW *PASDB_TapeDrvRecW;

typedef struct _tagASDB_TapeDrvExRec
{
   long id             ;             //   1  
   short adapterid     ;             //   2  
   short adapterbusid  ;             //   3  
   short scsiid        ;             //   4  
   short lun           ;             //   5  
   short devicetype    ;             //   6  
   short compliance    ;             //   7  
   char productname[18];             //   8  
   char productrevision [8];         //   9  
   char prevrevision [8];            //  10  
   char vendorname[10] ;             //  11  
   long hdcleancount   ;             //   2  
   long hdcleandate    ;             //   3  
   long tdusagetime    ;             //   4  
   long tdblocksize    ;             //   5  
   long tdttlmbwritten;
   long tdttlmediaerr  ;             //   7  
   long tdttlswerr     ;             //   8  
   short tddestroyed   ;             //   9  
   short tdreserved1   ;             //  20  
   char tdhostname[64] ;             //   1  (48)--> (64) 4/10/2007
   char serialno[32]   ;             //   2  
   long servicedate    ;             //   3  
   long tdttlkbread    ;             //   4  
   long ttlmounts      ;             //   5  
   long ttlseeks       ;             //   6  
   long ttlmovesmedia  ;             //   7  
   long tdreserved2    ;             //  28  
   char tdreserved3[4] ;
   char reserved[128]  ;
}ASDB_TapeDrvExRec;
typedef ASDB_TapeDrvExRec *PASDB_TapeDrvExRec;
//Oripin_JIS_Code : kumga04
typedef struct _tagASDB_TapeDrvExRecW
{
	long id             ;             //   1  
	short adapterid     ;             //   2  
	short adapterbusid  ;             //   3  
	short scsiid        ;             //   4  
	short lun           ;             //   5  
	short devicetype    ;             //   6  
	short compliance    ;             //   7  
	wchar_t productname[18];             //   8  
	wchar_t productrevision [8];         //   9  
	wchar_t prevrevision [8];            //  10  
	wchar_t vendorname[10] ;             //  11  
	long hdcleancount   ;             //   2  
	long hdcleandate    ;             //   3  
	long tdusagetime    ;             //   4  
	long tdblocksize    ;             //   5  
	long tdttlmbwritten;
	long tdttlmediaerr  ;             //   7  
	long tdttlswerr     ;             //   8  
	short tddestroyed   ;             //   9  
	short tdreserved1   ;             //  20  
	wchar_t tdhostname[64] ;             //   1  (48)--> (64) 4/10/2007
	wchar_t serialno[32]   ;             //   2  
	long servicedate    ;             //   3  
	long tdttlkbread    ;             //   4  
	long ttlmounts      ;             //   5  
	long ttlseeks       ;             //   6  
	long ttlmovesmedia  ;             //   7  
	long tdreserved2    ;             //  28  
	char tdreserved3[4] ;
	char reserved[128]  ;
}ASDB_TapeDrvExRecW;
typedef ASDB_TapeDrvExRecW *PASDB_TapeDrvExRecW;

#ifdef SESSNUM_INCREASE
typedef struct _tagASDB_TapeErrRec_Old
{
   short tapeid;
   unsigned short sesnum;
   short errorcode;
   short raidtapeno;
   long errortime;
   char senseinfo[16];
   long tdusagetime;
   long mediaerr;
   long swerr;
   long srerr;
   long tpusagetime;
   long kbwritten;
   long owpass;
   long udepass;
   long ttlwritepass;
   long ttlreadpass;
   long ttlmediaerr;
   long ttlswerr;
   long ttlsrerr;
   long ttlusagetime;
   long ttlkbwritten;
}ASDB_TapeErrRec_Old;
typedef ASDB_TapeErrRec_Old *PASDB_TapeErrRec_Old;

typedef struct _tagASDB_TapeErrRec
{
   short tapeid;
   unsigned short sesnum_old;
   short errorcode;
   short raidtapeno;
   long errortime;
   char senseinfo[16];
   long tdusagetime;
   long mediaerr;
   long swerr;
   long srerr;
   long tpusagetime;
   long kbwritten;
   long owpass;
   long udepass;
   long ttlwritepass;
   long ttlreadpass;
   long ttlmediaerr;
   long ttlswerr;
   long ttlsrerr;
   long ttlusagetime;
   long ttlkbwritten;
   long sesnum;
}ASDB_TapeErrRec;
typedef ASDB_TapeErrRec *PASDB_TapeErrRec;
#else
typedef struct _tagASDB_TapeErrRec
{
   short tapeid;
   unsigned short sesnum;
   short errorcode;
   short raidtapeno;
   long errortime;
   char senseinfo[16];
   long tdusagetime;
   long mediaerr;
   long swerr;
   long srerr;
   long tpusagetime;
   long kbwritten;
   long owpass;
   long udepass;
   long ttlwritepass;
   long ttlreadpass;
   long ttlmediaerr;
   long ttlswerr;
   long ttlsrerr;
   long ttlusagetime;
   long ttlkbwritten;
}ASDB_TapeErrRec;
typedef ASDB_TapeErrRec *PASDB_TapeErrRec;
#endif

typedef struct _tagASDB_LocationRec
{
   long id;
   char llocation[24];
   char lname[32];
   char lacctnum[24];
   char lcontact[32];
   char lphone[24];
   char lstreet[48];
   char lcity[32];
   char lstate[24];
   char lzip[16];
   char lcountry[32];
   char scompany[32];
   char sname[32];
   char sphone[24];
   char sstreet[48];
   char scity[32];
   char sstate[24];
   char szip[16];
   char scountry[32];
   char comment[80];
}ASDB_LocationRec;
typedef ASDB_LocationRec *PASDB_LocationRec;

typedef struct _tagASDB_LocationRecW
{
	long id;
	wchar_t llocation[24];
	wchar_t lname[32];
	wchar_t lacctnum[24];
	wchar_t lcontact[32];
	wchar_t lphone[24];
	wchar_t lstreet[48];
	wchar_t lcity[32];
	wchar_t lstate[24];
	wchar_t lzip[16];
	wchar_t lcountry[32];
	wchar_t scompany[32];
	wchar_t sname[32];
	wchar_t sphone[24];
	wchar_t sstreet[48];
	wchar_t scity[32];
	wchar_t sstate[24];
	wchar_t szip[16];
	wchar_t scountry[32];
	wchar_t comment[80];
}ASDB_LocationRecW;
typedef ASDB_LocationRecW *PASDB_LocationRecW;
typedef struct tagASDB_PoolRec
{
   char   pool[16];
   char   basenum[32];           //base serial number
   char   nextnum[32];           //next serial number
   char   range[32];             //SerialNoRange[32];
   long   minsaveset;            //nMinSaveSetCopies;
   long   retention;             //nSaveSetRetention; // in hours
   long   activated;               //bActivatedDate;   // Pool Activated Date in DOS time format
   char   setname[12];            //SetName[12];      // Up to 8 characters   
   short  increments;             //for UNIX, NT always 1
   short  pruneretensiondays;
   long   reserved2;
   long   reserved3;
} ASDB_PoolRec;
typedef ASDB_PoolRec *PASDB_PoolRec;
// Oripin: UNICODE_JIS Support kalsa03
typedef struct tagASDB_PoolRecW
{
	wchar_t   pool[16];
	wchar_t   basenum[32];           //base serial number
	wchar_t   nextnum[32];           //next serial number
	wchar_t   range[32];             //SerialNoRange[32];
	long   minsaveset;            //nMinSaveSetCopies;
	long   retention;             //nSaveSetRetention; // in hours
	long   activated;               //bActivatedDate;   // Pool Activated Date in DOS time format
	wchar_t   setname[12];            //SetName[12];      // Up to 8 characters   
	short  increments;             //for UNIX, NT always 1
	short  pruneretensiondays;
	long   reserved2;
	long   reserved3;
} ASDB_PoolRecW;
typedef ASDB_PoolRecW *PASDB_PoolRecW;

#ifdef BAB_CPDB_SUPPORT
typedef struct tagASDB_PoolRecEx
{
   char   pool[16];
   char   basenum[32];           //base serial number
   char   nextnum[32];           //next serial number
   char   range[32];             //SerialNoRange[32];
   long   minsaveset;            //nMinSaveSetCopies;
   long   retention;             //nSaveSetRetention; // in hours
   long   activated;               //bActivatedDate;   // Pool Activated Date in DOS time format
   char   setname[12];            //SetName[12];      // Up to 8 characters   
   char   hostname[64];            //HostName[64];
   short  increments;             //for UNIX, NT always 1
   short  pruneretensiondays;
   long   reserved2;
   long   reserved3;
} ASDB_PoolRecEx;
typedef ASDB_PoolRecEx *PASDB_PoolRecEx;
#endif

//<li$we04 desc="new table aslogerr">
typedef struct _tagASDB_LogErrRec 
{
   long  jobid;
   long  sessid;
   long  logtype;
   long  logflag;
   long  logdate;
   char  logmsg[512];
   long  reserved;
}ASDB_LogErrRec, * PASDB_LogErrRec;
//</li$we04>

//the ASDB_DetailExtOldRec is define for compatable with build 620
//in build 620, midl recognize the length of Path in ASDB_DetailExtRec 
//is 256, but ASDBAPI.DLL treats it as 260
#ifdef ASDB_OLD_IDL_DEFINE       //defined in ASDBAPI\dbrpc.idl
#define ASDB_OLD_MAX_PATH 256
#else
#define ASDB_OLD_MAX_PATH 260
#endif

//This is to match 620 IDL definition of ASDB_DetailExtRec
//Because dbrpc.idl in 620 didn't include <stdlib.h>, the _MAX_PATH is not defined, 
//then _MAX_PATH becomes 256
typedef struct _tagASDB_DetailExtOldRec 
{
   ASDB_DetailRec dRec;
   char   ShortName[12];
   char   LongName[256];
   char   Path[256];
}ASDB_DetailExtOldRec, * PASDB_DetailExtOldRec;

//But since in the ASDBAPI.DLL, ASDBCLI.DLL, ASEM.DLL in 620 use _MAX_PATH as 260, 
//so the ASDB_DetailExtRec data from 620 has then same size as ASDB_DetailExtRec in build Num > 620
typedef struct _tagASDB_DetailExtRec 
{
   ASDB_DetailRec dRec;
   char   ShortName[12];
   char   LongName[256];
   char   Path[ASDB_MAX_PATH];
}ASDB_DetailExtRec, * PASDB_DetailExtRec;

//But since in the ASDBAPI.DLL, ASDBCLI.DLL, ASEM.DLL in 620 use _MAX_PATH as 260, 
//so the ASDB_DetailExtRec data from 620 has then same size as ASDB_DetailExtRec in build Num > 620
typedef struct _tagASDB_DetailExtRecW 
{
   ASDB_DetailRec dRec;
   wchar_t   ShortName[12];
   wchar_t   LongName[256];
   wchar_t   Path[ASDB_MAX_PATH];
   unsigned int cp_flag;
}ASDB_DetailExtRecW, * PASDB_DetailExtRecW;

typedef struct _tagASDB_DetailExtRecAW 
{
   ASDB_DetailRec dRec;
   char   ShortName[24];
   char   LongName[512];
   char   Path[ASDB_UNICODE_MAX_PATH];
   BOOL		flag;
}ASDB_DetailExtRecAW, * PASDB_DetailExtRecAW;
// R11.5 FP1 (10/12/2005)
typedef struct _tagASDB_DetailExtRecEX 
{
   ASDB_DetailRec dRec;
   char   ShortName[12];
   char   LongName[256];
   char   Path[512];		// 256 --> 512
   //char	  reserved[8192];	// (Fix: 15808709) Too big.
}ASDB_DetailExtRecEX, * PASDB_DetailExtRecEX;

typedef struct _tagASDB_DetailExtRecEXAW 
{
   ASDB_DetailRec dRec;
   char   ShortName[24];
   char   LongName[512];
   char   Path[1024];		// 256 --> 512
   BOOL	 flag;
   //char	  reserved[8192];	// (Fix: 15808709) Too big.
}ASDB_DetailExtRecEXAW, * PASDB_DetailExtRecEXAW;


// R11.5 FP1 (10/12/2005)
//Oripin_JIS_Support: kumga04
typedef struct _tagASDB_DetailExtRecEXW 
{
	ASDB_DetailRec dRec;
	wchar_t   ShortName[12];
	wchar_t   LongName[256];
	wchar_t  Path[512];		// 256 --> 512
	unsigned cp_flag;
	//char	  reserved[8192];	// (Fix: 15808709) Too big.
}ASDB_DetailExtRecEXW, * PASDB_DetailExtRecEXW;


// CUI start 
//Cross Platform Computer OS TYPE
typedef enum  _tag_NOS_OS_TYPE 
{
   NOS_UNKNOWN          = 0,       // The operating system is Unknown
   NOS_DOS              ,          // The operating system is DOS
   NOS_OS2              ,          // The operating system is OS/2
   NOS_NETWARE          ,          // The operating system is NetWare
   NOS_WINDOWS          ,          // The operating system is Windows (NetWare 3.1 or WIN95)
   NOS_UNIX             ,          // The operating system is UNIX
   NOS_MAC              ,          // The operating system is Macintosh
   NOS_NT               ,          // The operating system is Windows NT
   NOS_WIN95            ,          // The operating system is Windows 95
   NOS_NAS				,          // The operating system is NAS server 
   NOS_AS400			,		   // The operating system is AS400 server 
   NOS_LAST_TYPE                   // Any new os type should add before this type
} NOS_OS_TYPE;

typedef enum  _tag_NOS_NET_TYPE 
{
   NET_TYPE_TCPIP         ,
   NET_TYPE_UNKNOWN
} NOS_NET_TYPE;

typedef enum  _tag_NOS_MACHINE_TYPE 
{
   MACHINE_TYPE_WORKSTATION       ,
   MACHINE_TYPE_SERVER       ,
   MACHINE_TYPE_UNKNOWN
} NOS_MACHINE_TYPE;

//20080826
#define ASDB_NODE_VISIBLE	1
#define ASDB_NODE_INVISIBLE	2
#define ASDB_NODE_INVISIBLE_FOR_VM 3	// To protect VM ashost record (in prunning time) and invisible property.

// For DASHBOARD ........ (09/15/2008)
#define TIER_HIGH		1
#define TIER_MEDIUM		2
#define TIER_LOW		3

// For Agent Type (11/05/2008)
#define ASHOST_AGENT_DPM				0x00000001
#define ASHOST_AGENT_SQL				0x00000002
#define ASHOST_AGENT_SHAREPOINT			0x00000004
#define ASHOST_AGENT_EXCHANGE			0x00000008
#define ASHOST_AGENT_ORACLE				0x00000010
#define ASHOST_AGENT_INFORMIX			0x00000020
#define ASHOST_AGENT_SYBASE				0x00000040
#define ASHOST_AGENT_LOTUS_DOMINO		0x00000080
#define ASHOST_AGENT_SAP				0x00000100
#define ASHOST_AGENT_INGRES				0x00000200
#define ASHOST_AGENT_CLIENT_AGENT		0x00000400
#define ASHOST_AGENT_OPENFILE_AGENT		0x00000800  
#define ASHOST_AGENT_VM_AGENT			0x00001000  

#ifndef ARC_SAVE_NODEINFO
// copied from UNIDB.H
typedef struct _tagASDB_NodeRec
{
   long ulID;
   long ulMachineType;
   long ulOSType;
   long ulNetType;
   char NodeAddress[66];
   char NodeName[66];
   char LoginName[66];
   long ulDiagSocket;
   char OSDescription[16];
   char OSVersion[12];
   char HardwareType[16];
   long ulShellMajor;
   long ulShellMinor;
   long ulIPXMajor;
   long ulIPXMinor;
   long ulSPXMajor;
   long ulSPXMinor;
   char LanCard[80];
   char HWSettings[80];
   long ulMaxDataSize;
   long ulLastModDate;
   long ulAgentLoaded;
   char Reserved[4];
}ASDB_NodeRec;
#else
typedef struct _tagASDB_NodeRec
{
   long ulID;
   long ulMachineType;	//0:ulID is index, 1: UlID is rhostid ,2: delete instance
   long ulOSType;
   long ulNetType;
   char NodeAddress[66];
   char NodeName[66];
   char LoginName[66];
   long ulDiagSocket;
   char OSDescription[16];
   char OSVersion[12];
   char HardwareType[16];
   long ulShellMajor;
   long ulShellMinor;
   long ulIPXMajor;
   long ulIPXMinor;
   long ulSPXMajor;
   long ulSPXMinor;
   char LanCard[80];
   char HWSettings[256];
   long ulMaxDataSize;
   long ulLastModDate;
   long ulAgentLoaded;
   long tier;		// (6/23/2008)
   long topologyid;	// (6/23/2008) Primary indicator...
   long MajorVersion;
   long MinorVersion;
   long ServicePack;
   long BuildNumber;
   long IsVisible;	// 1 Visible     2 InVisible
   char Reserved[490];
}ASDB_NodeRec;

#endif

typedef ASDB_NodeRec *PASDB_NodeRec;

#ifndef ARC_SAVE_NODEINFO
// Oripin: UNICODE_JIS Support dayra01
typedef struct _tagASDB_NodeRecW
{
	long ulID;
	long ulMachineType;
	long ulOSType;
	long ulNetType;
	wchar_t NodeAddress[66];
	wchar_t NodeName[66];
	wchar_t LoginName[66];
	long ulDiagSocket;
	wchar_t OSDescription[16];
	wchar_t OSVersion[12];
	wchar_t HardwareType[16];
	long ulShellMajor;
	long ulShellMinor;
	long ulIPXMajor;
	long ulIPXMinor;
	long ulSPXMajor;
	long ulSPXMinor;
	wchar_t LanCard[80];
	wchar_t HWSettings[80];
	long ulMaxDataSize;
	long ulLastModDate;
	long ulAgentLoaded;
	char Reserved[4];
}ASDB_NodeRecW;
typedef ASDB_NodeRecW *PASDB_NodeRecW;

#else

typedef struct _tagASDB_NodeRecW
{
   long ulID;
   long ulMachineType;	//0:ulID is index, 1: UlID is rhostid ,2: delete instance
   long ulOSType;
   long ulNetType;
   wchar_t NodeAddress[66];
   wchar_t NodeName[66];
   wchar_t LoginName[66];
   long ulDiagSocket;
   wchar_t OSDescription[16];
   wchar_t OSVersion[12];
   wchar_t HardwareType[16];
   long ulShellMajor;
   long ulShellMinor;
   long ulIPXMajor;
   long ulIPXMinor;
   long ulSPXMajor;
   long ulSPXMinor;
   wchar_t LanCard[80];
   wchar_t HWSettings[256];
   long ulMaxDataSize;
   long ulLastModDate;
   long ulAgentLoaded;
   long tier;		// (6/23/2008)
   long topologyid;	// (6/23/2008) Primary indicator...
   long MajorVersion;
   long MinorVersion;
   long ServicePack;
   long BuildNumber;
   long IsVisible;	// 1 Visible     2 InVisible
   char Reserved[490];
}ASDB_NodeRecW, *PASDB_NodeRecW;

typedef struct _tagASDB_NodeVerInfW
{
   wchar_t NodeAddress[66];
   wchar_t NodeName[66];
   long	MajorVersion;
   long MinorVersion;
   long BuildNumber;
   char Reserved[256];
}ASDB_NodeVerInfW, *PASDB_NodeVerInfW;

#endif
//  CUI end

//20081223 sonle01
typedef struct _tagASDB_ASHostInfoVM
{
   wchar_t  VMNodeName[66];
   wchar_t  VMNodeAddress[66];
   long		MajorVersion;
   long		MinorVersion;
   long		BuildNumber;
   long		ServicePack;
   long		OSType;
   char		Reserved[128];
}ASDB_ASHostInfoVM, *PASDB_ASHostInfoVM;

// R12.v (6/19/2008)
#define USERDB_BY_OBJECT_SET    100   // By specifying this value, these 4 values should take effect: authuserid, object, type, subtype
#define USERDB_GET_ALL          101   // By specifying this value, no parameter values needed
#define USERDB_BY_ID            102   // By specifying this value, this values should take effect: id
#define USERDB_BY_HOST			103   // The rhostname will take effect
#define USERDB_BY_AUTHUSERID	104	  // By this set, authuserid is required and all records related to this authuserid will be fetched

#define ONLY_INSERT				10
#define ONLY_UPDATE				11

typedef struct _tagASDB_UserDB_Parm
{
      long    id;
      long    authuserid;
      long    rhostid;
      char    rhostname[64];
      wchar_t object[256];
      long    type;
      long    subtype;
      long    lFlag;          // (USERDB_BY_OBJECT_SET,  USERDB_GET_ALL , USERDB_BY_ID, etc?
      char    reserved[128];	
}ASDB_UserDBParm, *PASDB_UserDBParm;

typedef struct _tagASDB_UserDB
{
      long    id;
      long    authuserid;
      long    rhostid;
      char    rhostname[64];
      wchar_t object[256];
      long    type;
      long    subtype;
      wchar_t domain[256];
      char    userName[256];
      char    password[256];
      char    reserved[128];	
}ASDB_UserDB, *PASDB_UserDB;
////// END of R12.v (6/19/2008)////////////

#ifdef R12_5_MANAGE_PASSWORD

typedef struct _tagASDB_UserDBW
{
      long    id;
      long    authuserid;
      long    rhostid;
      wchar_t    rhostname[64];
      wchar_t object[256];
      long    type;
      long    subtype;
      wchar_t domain[256];
      wchar_t    userName[256];
      wchar_t    password[256];
      char    reserved[128];	
}ASDB_UserDBW, *PASDB_UserDBW;

typedef struct _tagASDB_UserDB_ParmW
{
      long    id;
      long    authuserid;
      long    rhostid;
      wchar_t    rhostname[64];
      wchar_t object[256];
      long    type;
      long    subtype;
      long    lFlag;          // (USERDB_BY_OBJECT_SET,  USERDB_GET_ALL , USERDB_BY_ID, etc?
      char    reserved[128];	
}ASDB_UserDBParmW, *PASDB_UserDBParmW;

#endif //R12_5_MANAGE_PASSWORD

#define S_OKAY        0       /* normal return, okay */

#define MAX_DAEMON_BUFFER_SIZE 1024
#define MAX_SERIAL_NUM_SIZE  32
#define MAX_POOL_NAME_SIZE   16


typedef struct ASComputerName {
   unsigned long NameID;         // points to ASOBJECT.DB
   char          ComputerName[52];   // Maximum computer name is 15 characters in NT and 48 in NetWare plus "\\".
} COMPUTERNAME, *PCOMPUTERNAME;

typedef struct ASComputerNameW {
	unsigned long NameID;         // points to ASOBJECT.DB
	wchar_t          ComputerName[52];   // Maximum computer name is 15 characters in NT and 48 in NetWare plus "\\".
} COMPUTERNAMEW, *PCOMPUTERNAMEW;

typedef struct ASComputerName2 {
   unsigned long NameID;         // points to ASOBJECT.DB
   long		 nType;
   char          ComputerName[64];   // Maximum computer name is 15 characters in NT and 48 in NetWare plus "\\".
   char		 reserved[16];
} COMPUTERNAME2, *PCOMPUTERNAME2;

typedef struct ASComputerName2W {
	unsigned long NameID;         // points to ASOBJECT.DB
	long		 nType;
	wchar_t          ComputerName[64];   // Maximum computer name is 15 characters in NT and 48 in NetWare plus "\\".
	char		 reserved[16];
} COMPUTERNAME2W, *PCOMPUTERNAME2W;


typedef struct {
   //-----------------------------------------------------------
   //                          Register     Format     Erase
   //-----------------------------------------------------------
   char  OldTapeName[24];    //             Yes         Yes
   short OldRandomID;        //             Yes         Yes
   short OldSequenceNum;     //             Yes         Yes

   char  NewTapeName[24];    //  Yes        Yes
   short NewRandomID;        //  Yes        Yes
   short NewSequenceNum;     //  Yes        Yes

   long  NewFirstFormatDate; //  Yes        Yes               in Calendar time ( seconds )
   long  NewLastFormatDate;  //  Yes        Yes               in Calendar time ( seconds )

   char  FormatCode;         //  Yes        Yes             
   char  MediaCode;          //  Yes        Yes             
   char  DensityCode;        //  Yes        Yes             
   char  Reserved;

   long  ExpireDate;         //  Yes        Yes             
} FORMATTAPE2;

typedef struct {
   //----------------------------------------------------------------------
   //                                      Register  Format   Erase
   //----------------------------------------------------------------------
   char  OldTapeName[24];                 //          Yes       Yes
   short OldRandomID;                     //          Yes       Yes
   short OldSequenceNum;                  //          Yes       Yes
   char  OldSerialNum[MAX_SERIAL_NUM_SIZE];//         Yes       Yes
   char  OldPoolName[MAX_POOL_NAME_SIZE]; //          Yes       Yes

   char  NewTapeName[24];                 //  Yes     Yes
   short NewRandomID;                     //  Yes     Yes
   short NewSequenceNum;                  //  Yes     Yes
   char  NewSerialNum[MAX_SERIAL_NUM_SIZE];// Yes     Yes
   char  NewPoolName[MAX_POOL_NAME_SIZE]; //  Yes     Yes

   long  NewFirstFormatDate;              //  Yes     Yes   in Calendar time ( seconds )
   long  NewLastFormatDate;               //  Yes     Yes   in Calendar time ( seconds )

   char  FormatCode;                      //  Yes     Yes             
   char  MediaCode;                       //  Yes     Yes             
   char  DensityCode;                     //  Yes     Yes             
   char  Reserved;

   long  ExpireDate;                      //  Yes     Yes             
   unsigned char  ucTapeType;             //  Yes     Yes (0:AS 1:Retired 2:SIDF 3:HSM 4:RAID 10:MTF)
   unsigned char  ucRAIDTapes;  // Number of tapes in RAID Tape Set
   unsigned char  ucRAIDTapeNo; // Tape no. in RAID Tape Set starting with 0
   unsigned char  ucReserved;
} FORMATTAPE;

typedef struct {              // <Delta>
   //    Tape Information
   char  TapeName[24];         //
   short RandomID;            //
   short SequenceNum;          //
   long  WritePasses;         //
   long  ReadPasses;          //
   long  UDEPasses;           //
   long  TotalMediaErrors;    //
   long  TotalSWErrors;       //
   long  TotalSRErrors;       //
   long  UsageTime;           //
   long  KByteWritten;        //
   // Tape Drive Information
   char  AdapterID;            //
   char  AdapterBusID;        //
   char  SCSIid;              // 
   char  LogicalUN;           //
} UPDATETAPE;

typedef struct {              // <Delta>
   //    Tape Information
   char  TapeName[24];         //
   short RandomID;            //
   short SequenceNum;          //
   long  WritePasses;         //
   long  ReadPasses;          //
   long  UDEPasses;           //
   long  TotalMediaErrors;    //
   long  TotalSWErrors;       //
   long  TotalSRErrors;       //
   long  UsageTime;           //
   long  KByteWritten;        //
   // Tape Drive Information
   char  AdapterID;            //
   char  AdapterBusID;        //
   char  SCSIid;              // 
   char  LogicalUN;           //
   long  TapeFlag;
   //long  reserved1;		  // replace it with serialnum[16]	in R11.5 (1/14/2005)	
   //long  reserved2;
   //long  reserved3;
   //long  reserved4;
   char	 serialnum[16];
} UPDATETAPEEX;


typedef struct {
   char   AdapterID;         //
   char   AdapterBusID;      //
   char   SCSIid;              //
   char   LogicalUN;           //
   short  DeviceType;          //   
   char   VendorName[10];      //   
   char   ProductName[18];     //  
   char   ProductReVision[8];  //  
   long   BlockSize;           //  
   char   Compliance;          //  
   char   Reserved[3];
} REGISTERTAPEDRV;

typedef struct {
   char   AdapterID;         //
   char   AdapterBusID;      //
   char   SCSIid;              //
   char   LogicalUN;           //
   short  DeviceType;          //   
   char   VendorName[10];      //   
   char   ProductName[18];     //  
   char   ProductReVision[8];  //  
   long   BlockSize;           //  
   char   Compliance;          //  
   char   Reserved[3];
   char	  serialno[32];		   // New field for R11.5 + sp1
   char   Reserved2[14];	   // New field.	
#ifdef R14_DB_VISUAL // R14 (4/7/2009)
   char   parentserialno[32];  // New field (R14) // 4/7/2009	
   long	  deviceflag;		   // New field (R14) // 4/7/2009		
   unsigned short totaldrives; // New field (R14) // 5/11/2009	
   unsigned long  totalslots;  // New field (R14) // 5/11/2009		
#endif
} REGISTERTAPEDRVEX;	// fix (14630192)

typedef struct {              // <Delta>
   long  ID;                  // Tape Device ID
   char  AdapterID;           //
   char  AdapterBusID;        //
   char  SCSIid;              // 
   char  LogicalUnitNo;       // 
   short HeadCleanCount;      //
   long  LastHeadCleanDate;   //
   long  UsageTime;           //
} UPDATETAPEDRV;

#ifdef SESSNUM_INCREASE
typedef struct {              // <Delta>
   char  AdapterID;            //
   char  AdapterBusID;        //
   char  SCSIid;              // 
   char  LogicalUN;           //
   unsigned short SesNum;           //
   char  TapeName[24];        //
   short RandomID;           //
   short SeqNum;            //
   long  ErrorCode;           //
   char  SenseInfo[16];        //
   long  TapeDrvUsageTime;    //
   long  MediaErrors;        //
   long  SWErrors;           //
   long  SRErrors;            //
   long  KByteWritten;        //
   long  TapeUsageTime;       //
} REGISTERTAPEDRVERROR_OLD;

typedef struct {              // <Delta>
   char  AdapterID;            //
   char  AdapterBusID;        //
   char  SCSIid;              // 
   char  LogicalUN;           //
   long  SesNum;           //
   char  TapeName[24];        //
   short RandomID;           //
   short SeqNum;            //
   long  ErrorCode;           //
   char  SenseInfo[16];        //
   long  TapeDrvUsageTime;    //
   long  MediaErrors;        //
   long  SWErrors;           //
   long  SRErrors;            //
   long  KByteWritten;        //
   long  TapeUsageTime;       //
} REGISTERTAPEDRVERROR;
#else
typedef struct {              // <Delta>
   char  AdapterID;            //
   char  AdapterBusID;        //
   char  SCSIid;              // 
   char  LogicalUN;           //
   unsigned short SesNum;           //
   char  TapeName[24];        //
   short RandomID;           //
   short SeqNum;            //
   long  ErrorCode;           //
   char  SenseInfo[16];        //
   long  TapeDrvUsageTime;    //
   long  MediaErrors;        //
   long  SWErrors;           //
   long  SRErrors;            //
   long  KByteWritten;        //
   long  TapeUsageTime;       //
} REGISTERTAPEDRVERROR;
#endif

typedef struct _DB_STATUS {
   DWORD buffer_numbers;
   DWORD backup_jobs;
   DWORD total_records;
   DWORD reserved;
} DB_STATUS;

typedef struct _DB_CONFIG {
   DWORD buffer_numbers;
   DWORD pruning_enable;
   DWORD pruning_days;
   DWORD pruning_time;    // in seconds
   DWORD delay_deletion;
   DWORD minDiskThreshold;// in Bytes. The default is 5120000 ( 5 MB ).
   DWORD catalogDB_enable;// enhacement for jaguar. (kimwo01) // 10/21/2002	
   DWORD MediaPoolMaintenance_enable;// enhacement for jaguar. (kimwo01) // 10/21/2002	
   DWORD catalog_pruning_days;	     // enhacement for R11.5 (D2D2T)
   DWORD ActiveLogPruningDays;	// Add by lu$ed01
   // For R11.6 (DB Maintenance)...... (4/18/2006)
   DWORD UpdateStat;
   DWORD RebuildIndex;
   DWORD CheckIntegrity;
   DWORD ReduceDBsize;
} DB_CONFIG;

//_R14_DB_TRACE_2009_ (kimwo01) :4/30/2009
#define  DEFAULT_DEBUG_LOG_SIZE				   300  // default (300 MB) ( 300 means 300*1024*1024) :auto calculate by each module.
#define  MIN_DEBUG_LOG_SIZE			   1*1024*1024  // ( 1 MB)
#define  MAX_DEBUG_LOG_SIZE			1024*1024*1024  // ( 1 GB)



// This struct must be the same as tape_record for USA version
//typedef struct gen_tape_record {
//   short tape_id;
//   short treserved1;
//   char tapename[24];
//   short randomid;
//   short seqnum;
//   char tapetype;
//   char formatcode;
//   char mediacode;
//   char densitycode;
//   long firstformat;
//   long lastformat;
//   long expiredate;
//   long overwritepass;
//   long udepass;
//   long curwritepass;
//   long curreadpass;
//   long curmediaerr;
//   long curswerr;
//   long cursrerr;
//   long curusagetime;
//   long curkbwritten;
//   long ttlwritepass;
//   long ttlreadpass;
//   long ttlmediaerr;
//   long ttlswerr;
//   long ttlsrerr;
//   long ttlusagetime;
//   long ttlkbwritten;
//   char treserved2[20];
//   short destroyed;
//   char ttlraidtapes;
//   char curraidtapes;
//   long treserved3;
//}GEN_TAPE_RECORD;

//#ifdef _UNICODE_DB
//struct key_tapename_old
//{
//   char  tapename[24];
//   short randomid;
//   short seqnum;
//};
//#endif

typedef struct _tagASDB_MBOTapeName 
{
   char  tapename[24];
   short randomid;
   short seqnum;
   long  MBOsavesetno;
}ASDB_MBOTapeName, * PASDB_MBOTapeName;

typedef struct _tagASDB_KeyTapeName
{
   char  tapename[24];
   short randomid;
   short seqnum;
} ASDB_KeyTapeName, *PASDB_KeyTapeName;

typedef struct _tagASDB_KeyTapeNameW
{
	wchar_t  tapename[24];
	short randomid;
	short seqnum;
} ASDB_KeyTapeNameW, *PASDB_KeyTapeNameW;
typedef struct _tagASDB_DetailCompoundKey
{
   long sesid;
   long pathid;
   long filenameid;
   long reserved;
} ASDB_DetailCompoundKey, *PASDB_DetailCompoundKey;

typedef struct _tagQFAData
{
   long  qfachunknum;
   long  qfachunkoffset;
}ASDB_QFAData, *PASDB_QFAData;

#define ASDB_CENTRAL_MEMBER_SETTING_REC_DETAIL 0x00000001
#define ASDB_CENTRAL_MEMBER_SETTING_KEEP_LOCAL 0x00000002

//Cross Platform Computer Type
#define ASDB_MACHINETYPE_UNKNOWN    0
#define ASDB_MACHINETYPE_NT         1
#define ASDB_MACHINETYPE_NETWARE    2
#define ASDB_MACHINETYPE_UNIX       3

//Database Engine Type
#define ASDB_EXPRESS       0

#define ASDB_RAIMA         0
#define ASDB_MSSQL         1
#define ASDB_VLDB          2
#define ASDB_UNIXDB        3
#define ASDB_NETWAREDB     4
#define ASDB_INGRES        5
#define ASDB_ORACLE        6

#define SZSERVICENAME_MSSQL            "MSSQLServer"
#define SZSERVICENAME_OPENINGRES       "iidbms.exe"

typedef enum _tag_ASDB_SQLType
{
   ASDB_SQL_UNKNOWN=0,
   ASDB_SQL_LOCAL,
   ASDB_SQL_REMOTE,
   ASDB_SQL_VIRTUAL
}ASDB_SQLTYPE;


#define _MAX_ACCTOUNT_NAME  64 
typedef struct  _tag_ARC_PRIMARYDB_INFO
{
    char DB_GroupName[16];   
    char DB_PrimaryServerName[64];   
    unsigned short DB_primaryType;          // 0 -- regular central db, 1 -- primary media server
    unsigned short DB_machineType;          // refer to asdb.h
    unsigned short DB_databaseType;         //See Database Engine Type, refer to asdb.h
    unsigned short DB_method;               //refer to ASDBAPI.H
} ARC_PRIMARYDB_INFO, *PARC_PRIMARYDB_INFO;

typedef struct  _tag_ARC_DB_INFO
{
    unsigned long ulStreamId;               // ID under the produce ID, refer to Lic_def.h
    unsigned long ulStreamSize;             // size of this structure
    ARC_PRIMARYDB_INFO primaryDB;
} ARC_DB_INFO, *PARC_DB_INFO;

typedef struct  _tag_SERVER_ACCOUNT_INFO
{
  char ComputerName[MAX_COMPUTERNAME_LENGTH + 1];   
  char domain[_MAX_ACCTOUNT_NAME];
  char user[_MAX_ACCTOUNT_NAME];
  char pw[_MAX_ACCTOUNT_NAME];
  unsigned long DatabaseType;             //See Database Engine Type
  unsigned long method;                   //refer to ASDBAPI.H
} SERVER_ACCOUNT_INFO, *PSERVER_ACCOUNT_INFO;

typedef struct  _tag_SERVER_ACCOUNT_INFO_EX
{
    char ComputerName[_MAX_ACCTOUNT_NAME];   
    //char domain[_MAX_ACCTOUNT_NAME];
    char user[_MAX_ACCTOUNT_NAME];
    char pw[_MAX_ACCTOUNT_NAME];
    char DB_GroupName[16];   
    unsigned long DB_primaryType;          // 0 -- regular central db, 1 -- primary media server
    unsigned long DB_machineType;          // refer to asdb.h
    unsigned long DB_databaseType;         //See Database Engine Type, refer to asdb.h
    unsigned long DB_method;               //refer to ASDBAPI.H
} SERVER_ACCOUNT_INFO_EX, *PSERVER_ACCOUNT_INFO_EX;

//Oripin_JIS_Support: kumga04
typedef struct  _tag_SERVER_ACCOUNT_INFO_EXW
{
	wchar_t ComputerName[_MAX_ACCTOUNT_NAME];   
	//char domain[_MAX_ACCTOUNT_NAME];
	wchar_t user[_MAX_ACCTOUNT_NAME];
	wchar_t pw[_MAX_ACCTOUNT_NAME];
	wchar_t DB_GroupName[16];   
	unsigned long DB_primaryType;          // 0 -- regular central db, 1 -- primary media server
	unsigned long DB_machineType;          // refer to asdb.h
	unsigned long DB_databaseType;         //See Database Engine Type, refer to asdb.h
	unsigned long DB_method;               //refer to ASDBAPI.H
} SERVER_ACCOUNT_INFO_EXW, *PSERVER_ACCOUNT_INFO_EXW;

//sonle01 20080227
#define SERVER_VER_121		1	// server version is r12.1

#define ASDB_IsOldServer(phSESS)	(phSESS->serverVer < SERVER_VER_121?TRUE:FALSE)

typedef unsigned long ASDB_DB;
typedef struct _tagASDB_SESS
{
   //For Both
   BOOL bValidHandle;                              // 4:   0-3
   char ComputerName[MAX_COMPUTERNAME_LENGTH + 1]; // 16:   4-19

   //For RDS
   unsigned long hSESS;                            // 4:   20-23
   unsigned long hReadDB [8];                     // 4*10:24-63
   long			 SQLHandleUID; 					   // hReadDB [10] -> hReadDB [9] ----------------> 4/5/2007 (For Fixing NEC cluster problem: 15830968)
   long		 TopologyID;						   // hReadDB [9] -> hReadDB [8] ----> 6/24/2008 (To get primaryID from astopology) // R12.v (user profile)
   unsigned long hWriteDB[10];                     // 4*10:64-103
   short hEM;                                      // 2:   104-105
   unsigned char ComputerType;                     // 1:   106-106   //See Cross Platform Computer Type
   unsigned char data;                             // 1:   107-107
   //for ODBC
   unsigned long hENV;                             // 4:   108-111
   unsigned long hDBC;                             // 4:   112-115
   unsigned long hSTMT;                            // 4:   116-119
   unsigned short DatabaseType;                    // 2:   120-121   //See Database Engine Type
   // NetworkConnection Type
   // 0 --- do not need to connect to remote serve befor login
   // 1 --- need to connect to server as ARCserve$
   // 2 --- need to connect to server as ADMIN$
   unsigned short NetworkConnectionType;           // 2:   122-123
   // For Raima SQL call
   unsigned long   hCData;                         // 4:   124-127 
   // For RPC calls
   unsigned long   hMachID;                        // 4:   128-131
   unsigned long   pszStringBinding;               // 4: 132-135
   unsigned long   impersonateToken;               // 4: 136-139
   unsigned long   hNetwork;                       // 4: 140-143
   unsigned long   BuildNumber;                    // 4: 144-147
   BOOL         bManualCommit;                     // 4: 148-151
   char username[32];                              // 32:152-183
   char password[16];                              // 16:184-199
   char domain[16];                                // 16:200-215
   short lastError;                                // 2: 216-217
   short serverVer;                                 // 2: 218-219
   long  centralMethod;                            // 2: 220-223
   unsigned long centralDBHandle;                  // 224-227

   // for ExchangeAgentUpdate
   unsigned long hReadMSGDB;					   // 4:228-231
   unsigned long hWriteMSGDB;                      // 4:232-235
   unsigned long hReadMSGDATDB;                    // 4:236-239
   unsigned long hWriteMSGDATDB;                   // 4:240-243

   unsigned long hReadStagingDB;					   // 4:244-247
   unsigned long hWriteStagingDB;                   // 4:248-251
   unsigned long hCatalogDB;                      // 252-255
                                                   // reserved[20]: used for Netware find file flag
} ASDB_SESS, *PASDB_SESS;// sizeof(ASDB_SESS) is 256 bytes
// Oripin: UNICODE_JIS Support dayra01
typedef struct _tagASDB_SESSW
{
   //For Both
   BOOL bValidHandle;                              // 4:   0-3
   char ComputerNameA[MAX_COMPUTERNAME_LENGTH + 1]; // 16:   4-19

   //For RDS
   unsigned long hSESS;                            // 4:   20-23
   unsigned long hReadDB [8];                     // 4*10:24-63
   long			 SQLHandleUID; 					   // hReadDB [10] -> hReadDB [9] ----------------> 4/5/2007 (For Fixing NEC cluster problem: 15830968)
   long		 TopologyID;						   // hReadDB [9] -> hReadDB [8] ----> 6/24/2008 (To get primaryID from astopology) // R12.v (user profile)
   unsigned long hWriteDB[10];                     // 4*10:64-103
   short hEM;                                      // 2:   104-105
   unsigned char ComputerType;                     // 1:   106-106   //See Cross Platform Computer Type
   unsigned char data;                             // 1:   107-107
   //for ODBC
   unsigned long hENV;                             // 4:   108-111
   unsigned long hDBC;                             // 4:   112-115
   unsigned long hSTMT;                            // 4:   116-119
   unsigned short DatabaseType;                    // 2:   120-121   //See Database Engine Type
   // NetworkConnection Type
   // 0 --- do not need to connect to remote serve befor login
   // 1 --- need to connect to server as ARCserve$
   // 2 --- need to connect to server as ADMIN$
   unsigned short NetworkConnectionType;           // 2:   122-123
   // For Raima SQL call
   unsigned long   hCData;                         // 4:   124-127 
   // For RPC calls
   unsigned long   hMachID;                        // 4:   128-131
   unsigned long   pszStringBinding;               // 4: 132-135
   unsigned long   impersonateToken;               // 4: 136-139
   unsigned long   hNetwork;                       // 4: 140-143
   unsigned long   BuildNumber;                    // 4: 144-147
   BOOL         bManualCommit;                     // 4: 148-151
   char usernameA[32];                              // 32:152-183
   char passwordA[16];                              // 16:184-199
   char domainA[16];                                // 16:200-215
   short lastError;                                // 2: 216-217
   short serverVer;                                // 2: 218-219
   long  centralMethod;                            // 2: 220-223
   unsigned long centralDBHandle;                  // 224-227

   // for ExchangeAgentUpdate
   unsigned long hReadMSGDB;					   // 4:228-231
   unsigned long hWriteMSGDB;                      // 4:232-235
   unsigned long hReadMSGDATDB;                    // 4:236-239
   unsigned long hWriteMSGDATDB;                   // 4:240-243

   unsigned long hReadStagingDB;					   // 4:244-247
   unsigned long hWriteStagingDB;                   // 4:248-251
   unsigned long hCatalogDB;                      // 252-255
                                                   // reserved[20]: used for Netware find file flag
   // keep same as ASDB_SESS above
	wchar_t ComputerName[MAX_COMPUTERNAME_LENGTH + 1];
	wchar_t username[32];                              // 32:152-183
	wchar_t password[16];                              // 16:184-199
	wchar_t domain[16];                                // 16:200-215
} ASDB_SESSW, *PASDB_SESSW;
/*
typedef struct _tagASDB_SESSW
{
	//For Both
	BOOL bValidHandle;                              // 4:   0-3
	wchar_t ComputerName[MAX_COMPUTERNAME_LENGTH + 1]; // 16:   4-19

	//For RDS
	unsigned long hSESS;                            // 4:   20-23
	unsigned long hReadDB [9];                     // 4*10:24-63
	long			 SQLHandleUID; 					   // hReadDB [10] -> hReadDB [9] ----------------> 4/5/2007 (For Fixing NEC cluster problem: 15830968)
	unsigned long hWriteDB[10];                     // 4*10:64-103
	short hEM;                                      // 2:   104-105
	unsigned char ComputerType;                     // 1:   106-106   //See Cross Platform Computer Type
	unsigned char data;                             // 1:   107-107
	//for ODBC
	unsigned long hENV;                             // 4:   108-111
	unsigned long hDBC;                             // 4:   112-115
	unsigned long hSTMT;                            // 4:   116-119
	unsigned short DatabaseType;                    // 2:   120-121   //See Database Engine Type
	// NetworkConnection Type
	// 0 --- do not need to connect to remote serve befor login
	// 1 --- need to connect to server as ARCserve$
	// 2 --- need to connect to server as ADMIN$
	unsigned short NetworkConnectionType;           // 2:   122-123
	// For Raima SQL call
	unsigned long   hCData;                         // 4:   124-127 
	// For RPC calls
	unsigned long   hMachID;                        // 4:   128-131
	unsigned long   pszStringBinding;               // 4: 132-135
	unsigned long   impersonateToken;               // 4: 136-139
	unsigned long   hNetwork;                       // 4: 140-143
	unsigned long   BuildNumber;                    // 4: 144-147
	BOOL         bManualCommit;                     // 4: 148-151
	wchar_t username[32];                              // 32:152-183
	wchar_t password[16];                              // 16:184-199
	wchar_t domain[16];                                // 16:200-215
	short lastError;                                // 2: 216-217
	short pending;                                  // 2: 218-219
	long  centralMethod;                            // 2: 220-223
	unsigned long centralDBHandle;                  // 224-227

	// for ExchangeAgentUpdate
	unsigned long hReadMSGDB;					   // 4:228-231
	unsigned long hWriteMSGDB;                      // 4:232-235
	unsigned long hReadMSGDATDB;                    // 4:236-239
	unsigned long hWriteMSGDATDB;                   // 4:240-243

	unsigned long hReadStagingDB;					   // 4:244-247
	unsigned long hWriteStagingDB;                   // 4:248-251
	unsigned long hCatalogDB;                      // 252-255
	// reserved[20]: used for Netware find file flag
} ASDB_SESSW, *PASDB_SESSW;
*/
typedef DWORD (CALLBACK *PROCESS_LIST_FUNCTION_PTR)(PVOID pvData, PVOID pvData2, PVOID pvData3, DWORD Cnt);


#define ASDB_CENTRAL_METHOD_PARTIAL       0
#define ASDB_CENTRAL_METHOD_FULL          1

///////////////////////////////////////////////////////////////////////////
//  MMAPI.H
///////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////
// M+edia Pool Support Structures and APIs
/////////////////////////////////////////////////////////////////////////////

// Media Status
#define ASDB_MS_NORMAL        0x00   // Normal 			   // Normal (Overwriteable) 
#define ASDB_MS_SAVE          0x01   // Saved, read only   // Saved, read only
#define ASDB_MS_BAD           0x02   // Bad				   // Bad, should be used
#define ASDB_MS_MARGINAL      0x04   // Future			   // Future
#define ASDB_MS_APPEND_ONLY   0x08   //					   //	read and append only
#define ASDB_MS_PERMANENT     0x10   //					   //	mark by user, read only
#define ASDB_MS_RETIRED       0x20   // 				   // mark by the time that tape expired, read only
   
// Media Set Status
#define ASDB_SCRATCH_SET         0   // Tape is in Scratch Set
#define ASDB_SAVE_SET            1   // Tape is in Save Set
#define ASDB_NOT_APPLICABLE_SET  2   // Tape is not belong any media pool. (i.e : Dedupe media) : 10/7/2008

// Media Location Status
#define ASDB_MLS_ONLINE          0   //
#define ASDB_MLS_OFFLINE         1   //
#define ASDB_MLS_OFFSITE         2   //
#define ASDB_MLS_CHECKIN          0   //
#define ASDB_MLS_TEMP_CHECKIN     1   //
#define ASDB_MLS_CHECKOUT         2   //


//
//    Backup Job Tape Type  (sets ASMEDIA->MBKTYYPE)
//
#define BJT_CUSTOM               0   //
#define BJT_FULL                 1   //
#define BJT_DIFF                 2   //
#define BJT_INC                  3   //
#define BJT_ROTATE               4   //

typedef struct _tagSessAndTape
{
   PASDB_TapeRec ptDBRec;
   PASDB_SessRec psDBRec;
} SessAndTape, *PSessAndTape;


//--------------------------------------------------------------
// Global Variable for Backup DAEMON
//--------------------------------------------------------------
struct BACKUPDAEMON
{
   int      nInstanceCount;      // Instance counter of asdbnt.dll.
   int      iBegin;              // Begin and End index to the Backup buffer slot. 
   int      iEnd;                //
   int      nSize;               // Buffer size of Backup DAEMON.
   int      nDaemon;             // Instance counter of Backup DAEMON.
   int      nTotalRecords;       // Total records processed
};

#define MAX_OWNERNAME   32
#define MAX_COMMENTS   96

typedef struct {
//-----------------------------------
// To Start a Job
//-----------------------------------
   unsigned long  JobID;
//-----------------------------------
   short          JobType;
   short          SesFlags2;
   char           lpOwnerName[MAX_OWNERNAME];// Up to 32 characters
   char           lpComments[MAX_COMMENTS];  // Up to 48 characters
   char           SetName[8];                // Up to 8  characters
//-----------------------------------
// To End a Job
//-----------------------------------
//  Just set "Status" field
//-----------------------------------
// To Start a Session
//-----------------------------------
   unsigned long  SessionID;        // Used internally
   unsigned long  TapeID;           // Used internally
//-----------------------------------
   char           TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
#ifdef SESSNUM_INCREASE
   unsigned short SesNum_Old;
#else
   unsigned short SesNum;
#endif
   unsigned short SesMethod;
   unsigned short SesFlags;
   unsigned short QFABlockNum;
   char           lpSrcHost[ASDB_MAX_PATH];    // "\\MyMachine"
   char           lpSrcPath[ASDB_MAX_PATH];    // "C:"
   short          SesType;
//-----------------------------------
// To End a Session
//-----------------------------------
   short          Status;
   unsigned long  TotalKb;
   unsigned long  TotalFiles;
   unsigned long  TotalMissed;
//-----------------------------------
// To Submit a FTS Detail
//-----------------------------------
   char           ParseIt;
   char           dirLevel;                // Not Used
   char           DataType;
   char           NameSpace;
   unsigned long  FileDate;                // Dos Format ( )
   unsigned long  FileHighSize;            // High File Size
   unsigned long  FileSize;                // Low File Size
   unsigned long  FileAttrib;              // 
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   char           lpPath[ASDB_MAX_PATH];       // "SERVER/VOLUME:\PATH"
   char           lpShortName[ASDB_MAX_PATH];  // 8.3 format short name
   char           lpLongName[ASDB_MAX_PATH];   // Null terminate string
//-----------------------------------
   unsigned long  PathID;                  // Used internally 
   char           Path[ASDB_MAX_PATH];         // Used internally
//-----------------------------------
// For Local only, hSESS is local.
// For Central only, hSESS is central.
// For Local & Central, hSESS is local, and hCentralDBSess is central.
   ASDB_SESS      hSESS;
//-----------------------------------
   short          StreamNum;
//-----------------------------------
   short          mediaType;     //Used for MBO backup
   short          MBOSaveSetNo;  //Used for MBO backup
   short		  fsname_length;	// Used for NAS system name length.	// 1/10/2003	
//-----------------------------------
   ASDB_SESS      hCentralDBSess;         // Used for Central database hSESS
   unsigned long  CentralDBJobID;
   unsigned long  CentralDBSessionID;     // Used internally
   unsigned long  CentralDBTapeID;        // Used internally
   unsigned long  CentralDBPathID;        // Used internally
//-----------------------------------
   unsigned long  JobNo;		// Job No. in Job Quere is Used in Unix DB
   char           PoolName[16];
   char			  SerialNum[32]; // Used this field in R11.5 // 12/21/2004
   char			  bFlagEx;		// 0,  1-->(Multiplexing job) // 11/21/2005
#ifdef SESSNUM_INCREASE
   char           Reserved2[3];
   long           SesNum;
   char           Reserved[36];  // Used internally (original space 76-->44) : 32 space used by serialnumber
								 //  44 --> 43 : 1 space used by // 11/21/2005
#else
   char           Reserved[43];  // Used internally (original space 76-->44) : 32 space used by serialnumber
								 //  44 --> 43 : 1 space used by // 11/21/2005
#endif
} BACKUPJOB, *LPBACKUPJOB;

//typedef BACKUPJOB *LPBACKUPJOB;


typedef struct {
//-----------------------------------
// To Start a Job
//-----------------------------------
   unsigned long  JobID;
//-----------------------------------
   short          JobType;
   short          SesFlags2;
   wchar_t           lpOwnerName[MAX_OWNERNAME];// Up to 32 characters
   wchar_t           lpComments[MAX_COMMENTS];  // Up to 48 characters
   wchar_t           SetName[8];                // Up to 8  characters
//-----------------------------------
// To End a Job
//-----------------------------------
//  Just set "Status" field
//-----------------------------------
// To Start a Session
//-----------------------------------
   unsigned long  SessionID;        // Used internally
   unsigned long  TapeID;           // Used internally
//-----------------------------------
   wchar_t           TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
#ifdef SESSNUM_INCREASE
   unsigned short SesNum_Old;
#else
   unsigned short SesNum;
#endif
   unsigned short SesMethod;
   unsigned short SesFlags;
   unsigned short QFABlockNum;
   wchar_t           lpSrcHost[ASDB_MAX_PATH];    // "\\MyMachine"
   wchar_t           lpSrcPath[ASDB_MAX_PATH];    // "C:"   // Oripin: UNICODE_JIS Support upasr01
   short          SesType;
//-----------------------------------
// To End a Session
//-----------------------------------
   short          Status;
   unsigned long  TotalKb;
   unsigned long  TotalFiles;
   unsigned long  TotalMissed;
//-----------------------------------
// To Submit a FTS Detail
//-----------------------------------
   char           ParseIt;
   char           dirLevel;                // Not Used
   char           DataType;
   char           NameSpace;
   unsigned long  FileDate;                // Dos Format ( )
   unsigned long  FileHighSize;            // High File Size
   unsigned long  FileSize;                // Low File Size
   unsigned long  FileAttrib;              // 
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   wchar_t           lpPath[ASDB_MAX_PATH];       // "SERVER/VOLUME:\PATH"  // Oripin: UNICODE_JIS Support upasr01
   wchar_t           lpShortName[ASDB_MAX_PATH];  // 8.3 format short name  // Oripin: UNICODE_JIS Support upasr01
   wchar_t           lpLongName[ASDB_MAX_PATH];   // Null terminate string  // Oripin: UNICODE_JIS Support upasr01
//-----------------------------------
   unsigned long  PathID;                  // Used internally 
   wchar_t           Path[ASDB_MAX_PATH];         // Used internally // Oripin: UNICODE_JIS Support upasr01
//-----------------------------------
// For Local only, hSESS is local.
// For Central only, hSESS is central.
// For Local & Central, hSESS is local, and hCentralDBSess is central.
   ASDB_SESSW      hSESS;
//-----------------------------------
   short          StreamNum;
//-----------------------------------
   short          mediaType;     //Used for MBO backup
   short          MBOSaveSetNo;  //Used for MBO backup
   short		  fsname_length;	// Used for NAS system name length.	// 1/10/2003	
//-----------------------------------
   ASDB_SESSW      hCentralDBSess;         // Used for Central database hSESS
   unsigned long  CentralDBJobID;
   unsigned long  CentralDBSessionID;     // Used internally
   unsigned long  CentralDBTapeID;        // Used internally
   unsigned long  CentralDBPathID;        // Used internally
//-----------------------------------
   unsigned long  JobNo;		// Job No. in Job Quere is Used in Unix DB
   wchar_t           PoolName[16];
   wchar_t			  SerialNum[32]; // Used this field in R11.5 // 12/21/2004
   char			  bFlagEx;		// 0,  1-->(Multiplexing job) // 11/21/2005
#ifdef SESSNUM_INCREASE
   char           Reserved2[3];
   long           SesNum;
   char           Reserved[36];  // Used internally (original space 76-->44) : 32 space used by serialnumber
								 //  44 --> 43 : 1 space used by // 11/21/2005
#else
   wchar_t           Reserved[43];  // Used internally (original space 76-->44) : 32 space used by serialnumber
								 //  44 --> 43 : 1 space used by // 11/21/2005
#endif
} BACKUPJOBW, *LPBACKUPJOBW;

typedef struct {
//-----------------------------------
// To Start a Job
//-----------------------------------
   unsigned long  JobID;
//-----------------------------------
   short          JobType;
   short          SesFlags2;
   char           lpOwnerName[MAX_OWNERNAME];// Up to 32 characters
   char           lpComments[MAX_COMMENTS];  // Up to 48 characters
   char           SetName[8];                // Up to 8  characters
//-----------------------------------
// To End a Job
//-----------------------------------
//  Just set "Status" field
//-----------------------------------
// To Start a Session
//-----------------------------------
   unsigned long  SessionID;        // Used internally
   unsigned long  TapeID;           // Used internally
//-----------------------------------
   char           TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
#ifdef SESSNUM_INCREASE
   unsigned short SesNum_Old;
#else
   unsigned short SesNum;
#endif
   unsigned short SesMethod;
   unsigned short SesFlags;
   unsigned short QFABlockNum;
   char           lpSrcHost[ASDB_MAX_PATH];    // "\\MyMachine"
   char           lpSrcPath[4096];    // From ASDB_MAX_PATH to 4K (4096) (New Request from Task) 8/31/2005
   short          SesType;
//-----------------------------------
// To End a Session
//-----------------------------------
   short          Status;
   unsigned long  TotalKb;
   unsigned long  TotalFiles;
   unsigned long  TotalMissed;
//-----------------------------------
// To Submit a FTS Detail
//-----------------------------------
   char           ParseIt;
   char           dirLevel;                // Not Used
   char           DataType;
   char           NameSpace;
   unsigned long  FileDate;                // Dos Format ( )
   unsigned long  FileHighSize;            // High File Size
   unsigned long  FileSize;                // Low File Size
   unsigned long  FileAttrib;              // 
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   char           lpPath[4096];      	   // "SERVER/VOLUME:\PATH"  (From ASDB_MAX_PATH to 4K)
   char           lpShortName[ASDB_MAX_PATH];  // 8.3 format short name
   char           lpLongName[ASDB_MAX_PATH];   // Null terminate string
//-----------------------------------
   unsigned long  PathID;                  // Used internally 
   char           Path[4096];   	   // From ASDB_MAX_PATH to 4K (4096) (New Request from Task) 8/31/2005
//-----------------------------------
// For Local only, hSESS is local.
// For Central only, hSESS is central.
// For Local & Central, hSESS is local, and hCentralDBSess is central.
   ASDB_SESS      hSESS;
//-----------------------------------
   short          StreamNum;
//-----------------------------------
   short          mediaType;     //Used for MBO backup
   short          MBOSaveSetNo;  //Used for MBO backup
   short		  fsname_length;	// Used for NAS system name length.	// 1/10/2003	
//-----------------------------------
   ASDB_SESS      hCentralDBSess;         // Used for Central database hSESS
   unsigned long  CentralDBJobID;
   unsigned long  CentralDBSessionID;     // Used internally
   unsigned long  CentralDBTapeID;        // Used internally
   unsigned long  CentralDBPathID;        // Used internally
//-----------------------------------
   unsigned long  JobNo;		// Job No. in Job Quere is Used in Unix DB
   char           PoolName[16];
   char			  SerialNum[32]; // Used this field in R11.5 // 12/21/2004
   char			  bFlagEx;		// 0,  1-->(Multiplexing job) // 11/21/2005
#ifdef SESSNUM_INCREASE
   char           Reserved2[3];
   long			  SesNum;
#endif
   long			  SessionFlagEx; // Addition sessionflag for R11.5 +sp3 // 2/24/2006
   long			  encr_algo;	 // encryption algorithm
   long			  src_app_ver;	 // Application version information. (Agent)
   long			  SessionStartTime; // (7/20/2007): to save starttime from beginsessionex and use it in EndSessionEX	

   long			  ThroughputMBPerMin;
   char			  TapeDrvSerialNum[32];
   short		  adapterid;
   short		  adapterbusid;
   short		  scsiid;
   short		  lun;
   char			  Hostname[64];
   char			  SessionHostName[64];	// Oripin 4/19/2007

#ifdef SESSNUM_INCREASE
   //// R12.v (Dashboard) 6/2/2008 /////////////////
   char			  encr_loc;
   char			  encr_type;
   char			  store_pw;
   char           Reserved3;
   unsigned long	  sizeOnTapeKB;	// 6/17/2008. (R12.v)
   
   long  	  	  SubSesNum;	// R12.v  3/10/2008 (VMWARE)
   char		     	  Reserved[8000];
#elif defined R12_V_DASHBOARD_DB // & & R12_V_VM_DB
   //// R12.v (Dashboard) 6/2/2008 /////////////////
   char			  encr_loc;
   char			  encr_type;
   char			  store_pw;
   unsigned long	  sizeOnTapeKB;	// 6/17/2008. (R12.v)
   
   long  	  	  SubSesNum;	// R12.v  3/10/2008 (VMWARE)
   char           	  Reserved[27];
   char		     	  Reserved2[8009];		//  8020-->8009 : 11 space used (7/8/2008) (encr_loc/encr_type/store_pw/sizeOnTapeKB/SubSesNum)		   
#else   
   char           Reserved[27];  // Used internally (original space 76-->44) : 32 space used by serialnumber
								 //  44 --> 43 : 1 space used by // 11/21/2005
								 //  43 --> 39 : 4 space used by // 2/24/2006 (SessionFlagEX).
								 //  39 --> 31 : 8 space used by // 5/17/2006 (encr_algo / src_app_ver)
								 //  31 --> 27 : 4 space used by // 7/20/2007 (SessionStartTime)
   char		  Reserved2[8020];	 //  8192 --> 8156 : 32 + 4 space used by //10/12/2006 (TapeDrvSerialNum / ThroughputMBPerMin)
   					 //  8156 --> 8084 : 2+2+2+2+64 space (adapterid/adapterbusid/scsiid/lun + Hostname)
   					 //  8084 --> 8020 : SessionHostName[64]; // Oripin 4/19/2007
#endif // R12_V_DASHBOARD_DB   					 
} BACKUPJOBEX, *LPBACKUPJOBEX;		// sizeof (22128)

#ifdef UNICODE_JIS_SUPPORT
typedef struct {
//-----------------------------------
// To Start a Job
//-----------------------------------
   unsigned long  JobID;
//-----------------------------------
   short          JobType;
   short          SesFlags2;
   wchar_t           lpOwnerName[MAX_OWNERNAME];// Up to 32 characters
   wchar_t           lpComments[MAX_COMMENTS];  // Up to 48 characters
   wchar_t           SetName[8];                // Up to 8  characters
//-----------------------------------
// To End a Job
//-----------------------------------
//  Just set "Status" field
//-----------------------------------
// To Start a Session
//-----------------------------------
   unsigned long  SessionID;        // Used internally
   unsigned long  TapeID;           // Used internally
//-----------------------------------
   wchar_t           TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
#ifdef SESSNUM_INCREASE
   unsigned short SesNum_Old;
#else
   unsigned short SesNum;
#endif
   unsigned short SesMethod;
   unsigned short SesFlags;
   unsigned short QFABlockNum;
   wchar_t           lpSrcHost[ASDB_MAX_PATH];    // "\\MyMachine"
   wchar_t           lpSrcPath[4096];    // From ASDB_MAX_PATH to 4K (4096) (New Request from Task) 8/31/2005 // Oripin: UNICODE_JIS Support upasr01
   short          SesType;
//-----------------------------------
// To End a Session
//-----------------------------------
   short          Status;
   unsigned long  TotalKb;
   unsigned long  TotalFiles;
   unsigned long  TotalMissed;
//-----------------------------------
// To Submit a FTS Detail
//-----------------------------------
   char           ParseIt;
   char           dirLevel;                // Not Used
   char           DataType;
   char           NameSpace;
   unsigned long  FileDate;                // Dos Format ( )
   unsigned long  FileHighSize;            // High File Size
   unsigned long  FileSize;                // Low File Size
   unsigned long  FileAttrib;              // 
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   wchar_t           lpPath[4096];      	   // "SERVER/VOLUME:\PATH"  (From ASDB_MAX_PATH to 4K) // Oripin: UNICODE_JIS Support upasr01
   wchar_t           lpShortName[ASDB_UNICODE_MAX_PATH];  // 8.3 format short name // Oripin: UNICODE_JIS Support upasr01
   wchar_t           lpLongName[ASDB_UNICODE_MAX_PATH];   // Null terminate string // Oripin: UNICODE_JIS Support upasr01
//-----------------------------------
   unsigned long  PathID;                  // Used internally 
   wchar_t           Path[4096];   	   // From ASDB_MAX_PATH to 4K (4096) (New Request from Task) 8/31/2005 // Oripin: UNICODE_JIS Support upasr01
//-----------------------------------
// For Local only, hSESS is local.
// For Central only, hSESS is central.
// For Local & Central, hSESS is local, and hCentralDBSess is central.
   ASDB_SESS      hSESS;
//-----------------------------------
   short          StreamNum;
//-----------------------------------
   short          mediaType;     //Used for MBO backup
   short          MBOSaveSetNo;  //Used for MBO backup
   short		  fsname_length;	// Used for NAS system name length.	// 1/10/2003	
//-----------------------------------
   ASDB_SESS      hCentralDBSess;         // Used for Central database hSESS
   unsigned long  CentralDBJobID;
   unsigned long  CentralDBSessionID;     // Used internally
   unsigned long  CentralDBTapeID;        // Used internally
   unsigned long  CentralDBPathID;        // Used internally
//-----------------------------------
   unsigned long  JobNo;		// Job No. in Job Quere is Used in Unix DB
   wchar_t           PoolName[16];
   wchar_t			  SerialNum[32]; // Used this field in R11.5 // 12/21/2004
   char			  bFlagEx;		// 0,  1-->(Multiplexing job) // 11/21/2005
#ifdef SESSNUM_INCREASE
   char           Reserved2[3];
   long			  SesNum;
#endif
   long			  SessionFlagEx; // Addition sessionflag for R11.5 +sp3 // 2/24/2006
   long			  encr_algo;	 // encryption algorithm
   long			  src_app_ver;	 // Application version information. (Agent)
   long			  SessionStartTime; // (7/20/2007): to save starttime from beginsessionex and use it in EndSessionEX	

   long			  ThroughputMBPerMin;
   wchar_t			  TapeDrvSerialNum[32];
   short		  adapterid;
   short		  adapterbusid;
   short		  scsiid;
   short		  lun;
   wchar_t			  Hostname[64];
   wchar_t			  SessionHostName[64];	// Oripin 4/19/2007

#ifdef SESSNUM_INCREASE
   //// R12.v (Dashboard) 6/2/2008 /////////////////
   char			  encr_loc;
   char			  encr_type;
   char			  store_pw;
   char           Reserved3;
   unsigned long	  sizeOnTapeKB;	// 6/17/2008. (R12.v)
   
   long  	  	  SubSesNum;	// R12.v  3/10/2008 (VMWARE)
   char		     Reserved[8000];				 //  8020-->8009 : 11 space used (7/8/2008) (encr_loc/encr_type/store_pw/sizeOnTapeKB/SubSesNum)
#elif defined R12_V_DASHBOARD_DB // & R12_V_VM_DB
   //// R12.v (Dashboard) 6/2/2008 /////////////////
   char			  encr_loc;
   char			  encr_type;
   char			  store_pw;
   unsigned long	  sizeOnTapeKB;	// 6/17/2008. (R12.v)
   
   long  	  	  SubSesNum;	// R12.v  3/10/2008 (VMWARE)
   wchar_t           Reserved[27];  // Used internally (original space 76-->44) : 32 space used by serialnumber
								 //  44 --> 43 : 1 space used by // 11/21/2005
								 //  43 --> 39 : 4 space used by // 2/24/2006 (SessionFlagEX).
								 //  39 --> 31 : 8 space used by // 5/17/2006 (encr_algo / src_app_ver)
								 //  31 --> 27 : 4 space used by // 7/20/2007 (SessionStartTime)
   char		     Reserved2[8009];				 //  8020-->8009 : 11 space used (7/8/2008) (encr_loc/encr_type/store_pw/sizeOnTapeKB/SubSesNum)
#else   
   wchar_t           Reserved[27];  // Used internally (original space 76-->44) : 32 space used by serialnumber
								 //  44 --> 43 : 1 space used by // 11/21/2005
								 //  43 --> 39 : 4 space used by // 2/24/2006 (SessionFlagEX).
								 //  39 --> 31 : 8 space used by // 5/17/2006 (encr_algo / src_app_ver)
								 //  31 --> 27 : 4 space used by // 7/20/2007 (SessionStartTime)
   									 
   char		  Reserved2[8020];	 //  8192 --> 8156 : 32 + 4 space used by //10/12/2006 (TapeDrvSerialNum / ThroughputMBPerMin)
   					 //  8156 --> 8084 : 2+2+2+2+64 space (adapterid/adapterbusid/scsiid/lun + Hostname)
   					 //  8084 --> 8020 : SessionHostName[64]; // Oripin 4/19/2007
#endif // R12_V_DASHBOARD_DB   					 
} BACKUPJOBEXW, *LPBACKUPJOBEXW;		// sizeof (22128)

#endif // UNICODE_JIS_SUPPORT

typedef struct {
//-----------------------------------
// To Start a Job
//-----------------------------------
   unsigned long  JobID;
//-----------------------------------
   short          JobType;
   short          sPadding1;
   char           lpOwnerName[MAX_OWNERNAME];   // Up to 32 characters
   char           lpComments[MAX_COMMENTS];     // Up to 48 characters
//-----------------------------------
// To End a Job
//-----------------------------------
   short          Status;
   short          sPadding2;
   ASDB_SESS      hSESS;
//-----------------------------------
   ASDB_SESS      hCentralDBSess;               // Used for Central database hSESS
   unsigned long  CentralDBJobID;

   char           Reserved[236]; // Used internally
} COPYJOB, *LPCOPYJOB;

//typedef COPYJOB *LPCOPYJOB;

typedef struct  _DBCB {
  ULONG         sessionTime;
  USHORT        diskType,usPadding;
  BOOL          daemon,
                asDOS,
                setPathEnd,
                firstRecord,
                firstRootFile,
                firstDir;
  UINT          pathLen;
#ifdef _ARCHIVE_DETAIL_ //wu$br01,Sep. 8,2005 issue:13796223
  BACKUPJOBEX   record;
#else // _ARCHIVE_DETAIL_
  BACKUPJOB     record;
#endif // _ARCHIVE_DETAIL_
  PCHAR         pathEnd,
                volPath;
  CHAR          noName[2],
                altName[14],
                computerName[80],
                ownerName[128],
                comments[128],
                fileName[ASDB_MAX_PATH],
                path[_MAX_NETPATH];
  wchar_t       parentPath[_MAX_NETPATH];
  char          srcPath[_MAX_NETPATH];
} DBCB, *PDBCB;

#ifdef UNICODE_JIS_SUPPORT
typedef struct  _DBCBEX {
  ULONG         sessionTime;
  USHORT        diskType,usPadding;
  BOOL          daemon,
                asDOS,
                setPathEnd,
                firstRecord,
                firstRootFile,
                firstDir;
  UINT          pathLen;
#ifdef _ARCHIVE_DETAIL_ //wu$br01,Sep. 8,2005 issue:13796223
  BACKUPJOBEX   record;
#else // _ARCHIVE_DETAIL_
  BACKUPJOB     record;
#endif // _ARCHIVE_DETAIL_
  PCHAR         pathEnd,
                volPath;
  CHAR          noName[2],
                altName[14],
                computerName[80],
                ownerName[128],
                comments[128],
                fileName[ASDB_MAX_PATH],
                path[_MAX_NETPATH];
  wchar_t       parentPath[_MAX_NETPATH];
  char          srcPath[_MAX_NETPATH];
// Added for Unicode support
#ifdef _ARCHIVE_DETAIL_
  BACKUPJOBEXW  recordW;
#else // _ARCHIVE_DETAIL_
  BACKUPJOBW    recordW;
#endif // _ARCHIVE_DETAIL_
  wchar_t       fileNameW[ASDB_MAX_PATH],
                pathW[_MAX_NETPATH],
                srcPathW[_MAX_NETPATH];
} DBCBEX, *PDBCBEX;
#endif // UNICODE_JIS_SUPPORT

//For Tandom
typedef struct {
   // Tape Information.
   char tapename[24];            
   unsigned long randomid;
   unsigned long sequence;
   unsigned long  sessionnumber;    // Session number
   unsigned long  backuptime;       // Session start time in MS-DOS date/time format
   // Tape data information.
   char path[2*MAX_PATH];           // Full Path : NY-ENG/SYS:\PUBLIC or \\NT-ENG\E:\USER
   char name[256];                  // Long Name
   unsigned long  filedate;         // File date & time in MS-DOS date/time format
   unsigned long  filesizehigh;     // High File Size
   unsigned long  filesize;         // Low File Size
   unsigned long  qfachunknum;      // QFA Chunk number
   unsigned long  qfachunkoffset;   // QFA Chunk offset
   unsigned long  datatype;         // File(7) or Directory(6)
   unsigned long  dbAddress;        // Internal record address.(INTERNAL USE ONLY)
} ASDB_FILEITEM; 
typedef ASDB_FILEITEM *PASDB_FILEITEM;

//for ASEM for RAIMA's EM
#define MAX_EM_GETDATALIST                512
#define MAX_EM_GETDETAILLIST              40	// 100-->40  (4/5/2007) : Fix (15808709)
#define MAX_EM_GETVERSIONHIST             100
#define MAX_EM_GETSERVERLIST              512
#define MAX_EM_GETVOLUMELIST              512
#define MAX_EM_GETTAPELIST                512
#define MAX_EM_GETSESSIONLIST             512
#define MAX_EM_GETJOBRECLIST              50
#define MAX_EM_GETTAPERECLIST             30
#define MAX_EM_GETSESSRECLIST             512
#define MAX_EM_GETDBADATABASELIST         512
#define MAX_EM_GETSETMEDIALIST            200
#define MAX_EM_GETQFADATALIST             2048
#define MAX_EM_QUERYFILEITEMLIST          30

#define MAX_EM_MSGGETCHILDREN             MAX_EM_GETDETAILLIST	// 100, must be same as MAX_EM_GETDETAILLIST : Fix (16047160)
#define MAX_EM_MSGGETDATA                 100
#define MAX_EM_MSGFINDFILE                100

#ifdef ASDB_D2D2T
#define MAX_EM_STAGING                    100
#define MAX_EM_SESMAP                     100
#define MAX_EM_GETJOBMAP		  50
#endif

//change for R12.v
#define MAX_EM_GETHOSTINFOLIST			  100
// DBDTL
#define MAX_EM_GETDETAIL_EXPORT_IMPORT    1024


#define MAX_SAPJOBID_LENGTH  	18 	// SAP___YYMMDDHHMM
#define MAX_SAPFILE_LENGTH  	256	

#define MAX_EM_GETEXCHANGINFOLIST          100

//**************************************************
//Begin of SAP support
//**************************************************
typedef struct {
   char ID[MAX_SAPJOBID_LENGTH];
} HSAPJOBID;
typedef HSAPJOBID *PHSAPJOBID;

typedef struct {
   char Name[MAX_SAPFILE_LENGTH];
   int  Pathid;
   int  Filenameid;
} HSAPFILE;
typedef HSAPFILE *PHSAPFILE;

typedef struct {
   short   SessionCnt;
   short   Reserved;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   unsigned long  HSAPJobReserved;
} HSAPJOB;
typedef HSAPJOB *PHSAPJOB;

typedef struct {
   // Tape Info.
   char    TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
   // Session Info.
   unsigned long  QFABlockNum;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   unsigned long  SessionID;
   // File info.
   unsigned long  FileSizeHigh;
   unsigned long  FileSize;
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   unsigned long  HSAPSessionReserved;
} HSAPSESSION;
typedef HSAPSESSION *PHSAPSESSION;

//**************************************************
//End of SAP support
//**************************************************

//ASNT.71 support
typedef struct {
	//----------------------------------------------------------------------
	//                                      Register  Format   Erase
	//----------------------------------------------------------------------
	char	OldTapeName[24];                 //          Yes       Yes
	long	OldRandomID;                     //          Yes       Yes
	long	OldSequenceNum;                  //          Yes       Yes
	long	OldRAIDTapeNo;                   //          Yes       Yes              Tape no. in RAID Tape Set starting with 0
	char	OldSerialNum[32];                //          Yes       Yes     32
	char	OldPoolName[16];                 //          Yes       Yes     16
	char	OldUUID[24];                     //          Yes       Yes

	char	NewTapeName[24];                 //  Yes     Yes
	long	NewRandomID;                     //  Yes     Yes
	long	NewSequenceNum;                  //  Yes     Yes
	long	NewRAIDTapeNo;                   //  Yes     Yes                        Tape no. in RAID Tape Set starting with 0
	char	NewSerialNum[32];                //  Yes     Yes
	char	NewPoolName[16];                 //  Yes     Yes
	char	NewUUID[24];                     //  Yes     Yes       Yes

	long	NewFirstFormatDate;              //  Yes     Yes   in Calendar time ( seconds )
	long	NewLastFormatDate;               //  Yes     Yes   in Calendar time ( seconds )

	long	FormatCode;                      //  Yes     Yes             
	long	MediaCode;                       //  Yes     Yes             
	long	DensityCode;                     //  Yes     Yes             
	long	ucTapeType;                      //  Yes     Yes (0:AS 1:Retired 2:SIDF 3:HSM 4-9:RAID_0 to RAID_5 10:MTF)

	long	ucRAIDTapes;  // Number of tapes in RAID Tape Set

	long	ExpireDate;                      //  Yes     Yes             
	long	padding;

	char	HostName[64];
	char	reserved[72];
} FORMATTAPE71;                    //total 384 bytes
 

typedef struct {              // <Delta>
	//    Tape Information
	char	TapeName[24];      

	long	RandomID;          
	long	SequenceNum;       
	long	WritePasses;       
	long	ReadPasses;        

	long	UDEPasses;         
	long	TotalMediaErrors;  
	long	TotalSWErrors;     
	long	TotalSRErrors;     

	long	UsageTime;         
	long	KByteWritten;      
	long	TapeFlag;
	long	TapeType;

	// Tape Drive Information
	long	AdapterID;         
	long	AdapterBusID;      
	long	SCSIid;            
	long	LogicalUN;         

	char	UUID[24];
	char	SerialNum[32];
	char	HostName[64];
	char	reserved[48];
} UPDATETAPE71;                             //total 256 bytes

 

typedef struct {
	long	AdapterID;         
	long	AdapterBusID;      
	long	SCSIid;            
	long	LogicalUN;         

	long	Compliance;        
	long	DeviceType;         
	long	BlockSize;         
	long	padding;

	char	VendorName[16];     
	char	ProductName[24];   
	char	ProductReVision[8];

	char	SerialNum[32];
	char	HostName[64];
	char	reserved[80];
} REGISTERTAPEDRV71;          //total 256

typedef struct {              // <Delta>
	long	ID;                  // Tape Device ID
	long	AdapterID;           //
	long	AdapterBusID;        //
	long	SCSIid;              // 
	long	LogicalUnitNo;       // 
	long	HeadCleanCount;      //
	long	LastHeadCleanDate;   //
	long	UsageTime;           //
	char	SerialNum[32];
	char	HostName[64];
	char	reserved[128];
} UPDATETAPEDRV71;              //total 256 bytes

typedef struct {
	char	UUID[24];
	char	SerialNum[32];
	char	TapeName[24];
	long	RandomID;
	long	SeqNum;
	long	RaidID;
} TapeKey, *PTapeKey;

typedef struct {
	unsigned long maxOverwrite;
	unsigned long maxUsageTime;
	unsigned long tapetype;			//	for future
	unsigned long formatcode;		//	for future
	unsigned long mediacode;			//	for future
	unsigned long densitycode;		//	for future
	unsigned long reserved[8];		//	for future
}TapeRetiredPolicy, *PTapeRetiredPolicy;

#ifdef ASDB_D2D2T

// disk staging session characteristic flags
#define ASDB_STAGINGFLAG_MIGRATED			0x00000001 // indicate the session has been copied
#define ASDB_STAGINGFLAG_NOT_MIGRATED		0x00000002 // indicate the session not yet migrated
#define ASDB_STAGINGFLAG_DONT_MIGRATE		0x00000004 // the session should be copied
#define ASDB_STAGINGFLAG_MIGRATED_FAILED	0x00000008 // This flag is not being used now.
#define ASDB_STAGINGFLAG_SUCCESSFULL		0x00000010
#define ASDB_STAGINGFLAG_FAILED				0x00000020
#define ASDB_STAGINGFLAG_READY_FOR_DM		0x00000040
#define ASDB_STAGINGFLAG_EXPIRED			0x00000080
#define ASDB_STAGINGFLAG_BY_GROUPNAME		0x00000100
#define ASDB_STAGINGFLAG_SNAPLOCKED			0x00000200
#define ASDB_STAGINGFLAG_SESSION_LOCAL		0x00000400
#define ASDB_STAGINGFLAG_SESSION_ACTIVE		0x00000800
#define ASDB_STAGINGFLAG_BY_SERVERNAME		0x00001000 // return server-specific staging records
#define ASDB_STAGINGFLAG_BY_TAPE			0x00002000 // return tape-specific staging records
#define ASDB_STAGINGFLAG_BY_SESSION			0x00004000 // return one staging record related to the session
#define ASDB_STAGINGFLAG_LAST_VALID_DEST	0x00008000 // indicating last valid destination for a common job
#define ASDB_STAGINGFLAG_BY_COMMONJOBID		0x00010000 // return commonjobid-specific staging records
#define ASDB_STAGINGFLAG_BY_MASTERJOBID		0x00020000 // return commonjobid and masterjobid specific staging records
#define ASDB_STAGINGFLAG_SESSION_DR			0x00040000 // request from DR-bakcup. 
#define ASDB_STAGINGFLAG_DMJ_DEVICE_ERR		0x00080000 // dmj job fails due to device error, its makeup job will exclude the current media
#define ASDB_STAGINGFLAG_BY_SESSIONID		0x00100000 // return sessionID-specific staging records

#define ASDB_STAGINGFLAG_DMJMAKEUP_ONHOLD	0x00200000 // create datamigration job makeup job on hold
#define ASDB_STAGINGFLAG_USE_WORM			0x00400000	// indicating DMJ use worm tape.
#define ASDB_STAGINGFLAG_FAIL_QUALIFY		0x00800000 // indicating that if this session fails, it's OK to purge..
#define ASDB_STAGINGFLAG_CANCEL_QUALIFY		0x01000000 // indicating that if this session is cancelled, it's OK to purge  
#define ASDB_STAGINGFLAG_MODIFIED			0x02000000 // indicating B2D job has been modified.
#define ASDB_STAGINGFLAG_ROTATION			0x04000000 // indicating B2D is simple rotation/simplerotation makeup job
#define ASDB_STAGINGFLAG_GFS				0x08000000 // indicating B2D is GFS/GFS makeupjob
#define ASDB_STAGINGFLAG_INC				0x10000000 // reserved
#define ASDB_STAGINGFLAG_DIFF				0x20000000 // reserved
#define ASDB_STAGINGFLAG_FULL				0x40000000 // reserved
#define ASDB_STAGINGFLAG_EJECTTAPE			0x80000000 // eject tape.


#ifdef _B2T2T
#define ASDB_STAGING_NEED_MIGRATION_SORT_BY_TAPE_NEEDED 100  
#define ASDB_STAGING_D2T2T_CONSOLIDATE		200
#define ASDB_STAGING_D2T2T_NO_CONSOLIDATE	300
#define ASDB_STAGING_COPIED_SESSION		400
#endif //_B2T2T

#ifdef _B2T2T
#define ASDB_STAGINGSNAPLOCK_CONSOLIDATION_NEEDED	0x00000002		//Indication this staging session need to be consolidated.
#endif // _B2T2T

#define ASDB_STAGINGSNAPLOCK_DM_MAKEUPJOBONHOLD		0x00000004		//active sessions packaged in DM hold makeup job						

// query source or destination session from session map
#define ASDB_SESSIONMAPFLAG_GET_SRC        0x00000001 // get source from destination
#define ASDB_SESSIONMAPFLAG_GET_DST        0x00000002 // get destination from source
#define ASDB_SESSIONMAPFLAG_GET_BOTH_SRC   0x00000004 // get both from source
#define ASDB_SESSIONMAPFLAG_GET_BOTH_DST   0x00000008 // get both from destination

//#ifdef ASDB_BAB115_SP3

//New tape flagex
#define ASDB_TAPEFLAGEX_SESSIONNOTMIGRATED				0x00000001

#define	ASDB_NON_MERGECAT		  0xFFFFFFFF  // Non-mergecat & non-readonly	

//#endif //ASDB_BAB115_SP3

// Recover Action .....
#define		ACTION_SET_STAGING		1
#define		ACTION_DEL_STAGING		2
#define		ACTION_DEL_STAGINGEX		3
#define		ACTION_SET_SESMAP		4
#define		ACTION_DEL_SESMAP		5
#define		ACTION_SET_MIGRATEDSTAGING	6
#define		ACTION_SMART_DEL_STAGING	7

#define		ACTION_RECOVERY_BLOCK		99

// LAST_VALID_DEST (Prevent deleting staging record with lastvaliddest as 1)
#define		ASDB_DELETE_STAGING_BLOCK	100

#define		ASDB_MIGRATED_STAGEING		110

//define flag used by lastvaliddest
#define ASDB_STAGING_LASTVALID_APPEND		0x00000001
#define ASDB_STAGING_LASTVALID_OVERWRITE	0x00000002
#ifdef SECURE_MIGRATION  //jiaca01   Mar 28, 2006
#define ASDB_STAGINGLASTVALID_TEENCRYPTION		0x00000004		//DS job set this flag to tell DM job to use TE encryption
#endif //SECURE_MIGRATION
#ifdef _B2T2T
#define ASDB_STAGINGLASTVALID_B2T2T_SESSION		0x00000008		// Indicate this session was created by a B2T2T backup job
#endif // _B2T2T
#if 1 //def SIS_SUPPORT
#define ASDB_STAGINGLASTVALID_DDD_SESSION		0x00000010		//Indicate DDD Staging Session
#define ASDB_STAGINGLASTVALID_DDD_FINAL_SESSION 0x00000020		//Indicate DDD final Session
#define ASDB_STAGINGLASTVALID_DDD_DIRECTLY_SESSION	0x00000040	//Indicate a sessions which is backed up to DeDupe Device directly
#endif //SIS_SUPPORT

#ifdef B2D2T_EXPORTTAPE
#define ASDB_STAGINGLASTVALID_EXPORT_ALL												0x00100000		//export all 
#define ASDB_STAGINGLASTVALID_EXPORT_DUPLICATE									0x00200000		//export raid duplicate
#define ASDB_STAGINGLASTVALID_EXPORT_VIRTUAL										0x00400000		//export virtual
#endif //B2D2T_EXPORTTAPE

#ifdef STAGING_REPEATING_APPEND_ANY_TAPE
#define ASDB_STAGINGLASTVALID_REPEATJOB							0x00800000   // Indicates that this is repeating staging job.
#endif //STAGING_REPEATING_APPEND_ANY_TAPE

#ifdef BBABW_14176854
#define ASDB_STAGINGLASTVALID_EJECTTAPEBYDEVSETON               0x08000000      //If this bit wise flag is set for a row, this would indicate that this was the last session backed up in that staging job.
#endif //BBABW_14176854
#define ASDB_STAGINGLASTVALID_LARGEST_SESSION_ID                0x01000000      //If this bit wise flag is set for a row, this would indicate that this was the last session backed up in that staging job.
#ifdef _B2T2T
#define ASDB_STAGINGLASTVALID_SORT_BY_TAPE_NEEDED               0x02000000      //If this bit wise flag is set for a row, this would indicate that backup might have used multiple tapes (for multistream backup).
#endif // _B2T2T

#define ASDB_STAGINGLASTVALID_LEAVE_CATALOG_ONDISK              0x04000000		//Leave catalog detail on disk for staging policy, Merge L2_14901929_T2d2542_wu$el01 by wanhe05

#define ASDB_STAGINGLASTVALID_MAKEUP_SESSION					0x10000000		//indicate staging session was generated by B2D makeup job. 
#define ASDB_STAGINGLASTVALID_ACTIVE_CONTINUE_WARNING_MSG		0x20000000		//print debug message for active session when active time is large than 72 hours
#define ASDB_STAGINGLASTVALID_ACTIVE_DEBUG_MSG					0x40000000		//print debug message for active staging session when active time is less than 3 hours
#define ASDB_STAGINGLASTVALID_ACTIVE_WARNING_MSG				0x80000000


#ifdef SESSNUM_INCREASE
typedef struct tagASDB_StagingParm_Old {	  
   char  groupname[16];
   char  hostname[64];
   unsigned long  ulFlag;
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum[32];
   unsigned short sesnum;
   long  jobid;
   long  sessid;
   long  commonjobid;
   long	 masterjobid;
   long	 duedate;	// Can be used for any date due to time zone consideration
   
   long  clienttimestamp;	// ASDBAPI internally will fill this value. so client like job engine , task does not have to fill this.
   
#ifdef _B2T2T
   short sFlagEx;			// Used by D2T2T retrive creteria.
   char  reserved[10];
#else
   char  reserved[12];
#endif // _B2T2T
} ASDB_StagingParm_Old, *PASDB_StagingParm_Old; 

typedef struct tagASDB_SessionMap_Old {	  
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
   unsigned short sesnum;
   long  sessid;	// for output value.		
   unsigned short mediaType; // this is for mediacode...
   unsigned short vaulted;
   unsigned short reserved1;
   short	  recoveryaction;			// Recover Action when DB is avaible (Write the file)
#ifdef R12_V_VM_DB
   long	  subsesnum;	// R12.v (VMWARE) : 6/25/2008
   char   reserved[14];
#else
   char   reserved[18];
#endif //R12_V_VM_DB
} ASDB_SessionMap_Old, *PASDB_SessionMap_Old; 

// Oripin: UNICODE_JIS Support :redbh03
typedef struct tagASDB_SessionMapW_Old {	  
	wchar_t  tapename[24];
	short randomid;
	short seqnum;
	wchar_t  serialnum [32];
	unsigned short sesnum;
	long  sessid;	// for output value.		
	unsigned short mediaType; // this is for mediacode...
	unsigned short vaulted;
	unsigned short reserved1;
	short	  recoveryaction;			// Recover Action when DB is avaible (Write the file)
#ifdef R12_V_VM_DB
   long	  subsesnum;	// R12.v (VMWARE) : 6/25/2008
   char   reserved[14];
#else
   char   reserved[18];
#endif //R12_V_VM_DB
} ASDB_SessionMapW_Old, *PASDB_SessionMapW_Old;

typedef struct tagASDB_StagingParm {	  
   char  groupname[16];
   char  hostname[64];
   unsigned long  ulFlag;
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum[32];
   long  sesnum;
   long  jobid;
   long  sessid;
   long  commonjobid;
   long	 masterjobid;
   long	 duedate;	// Can be used for any date due to time zone consideration
   
   long  clienttimestamp;	// ASDBAPI internally will fill this value. so client like job engine , task does not have to fill this.
   
#ifdef _B2T2T
   short sFlagEx;			// Used by D2T2T retrive creteria.
   char  reserved[10];
#else
   char  reserved[12];
#endif // _B2T2T
} ASDB_StagingParm, *PASDB_StagingParm; 

#define		SESMAP_DISTINGUISH		999

typedef struct tagASDB_SessionMap {	  
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
   long  sesnum;
   long  sessid;	// for output value.		
   unsigned short mediaType; // this is for mediacode...
   unsigned short vaulted;
   unsigned short reserved1;
   short	  recoveryaction;			// Recover Action when DB is avaible (Write the file)
#ifdef R12_V_VM_DB
   long	  subsesnum;	// R12.v (VMWARE) : 6/25/2008
   long   tapeflag_ext; // R12.v (DEDUPE)
   char   reserved[10];
#else
   char   reserved[18];
#endif //R12_V_VM_DB
} ASDB_SessionMap, *PASDB_SessionMap; 

// Oripin: UNICODE_JIS Support :redbh03
typedef struct tagASDB_SessionMapW {	  
	wchar_t  tapename[24];
	short randomid;
	short seqnum;
	wchar_t  serialnum [32];
	long  sesnum;
	long  sessid;	// for output value.		
	unsigned short mediaType; // this is for mediacode...
	unsigned short vaulted;
	unsigned short reserved1;
	short	  recoveryaction;			// Recover Action when DB is avaible (Write the file)
#ifdef R12_V_VM_DB
   long	  subsesnum;	// R12.v (VMWARE) : 6/25/2008
   long   tapeflag_ext; // R12.v (DEDUPE)
   char   reserved[10];
#else
   char   reserved[18];
#endif //R12_V_VM_DB
} ASDB_SessionMapW, *PASDB_SessionMapW;
#else
typedef struct tagASDB_StagingParm {	  
   char  groupname[16];
   char  hostname[64];
   unsigned long  ulFlag;
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum[32];
   unsigned short sesnum;
   long  jobid;
   long  sessid;
   long  commonjobid;
   long	 masterjobid;
   long	 duedate;	// Can be used for any date due to time zone consideration
   
   long  clienttimestamp;	// ASDBAPI internally will fill this value. so client like job engine , task does not have to fill this.
   
#ifdef _B2T2T
   short sFlagEx;			// Used by D2T2T retrive creteria.
   char  reserved[10];
#else
   char  reserved[12];
#endif // _B2T2T
} ASDB_StagingParm, *PASDB_StagingParm; 

#define		SESMAP_DISTINGUISH		999

typedef struct tagASDB_SessionMap {	  
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
   unsigned short sesnum;
   long  sessid;	// for output value.		
   unsigned short mediaType; // this is for mediacode...
   unsigned short vaulted;
   unsigned short reserved1;
   short	  recoveryaction;			// Recover Action when DB is avaible (Write the file)
#ifdef R12_V_VM_DB
   long	  subsesnum;	// R12.v (VMWARE) : 6/25/2008
   long   tapeflag_ext; // R12.v (DEDUPE)
   char   reserved[10];
#else
   char   reserved[18];
#endif //R12_V_VM_DB
} ASDB_SessionMap, *PASDB_SessionMap; 

// Oripin: UNICODE_JIS Support :redbh03
typedef struct tagASDB_SessionMapW {	  
	wchar_t  tapename[24];
	short randomid;
	short seqnum;
	wchar_t  serialnum [32];
	unsigned short sesnum;
	long  sessid;	// for output value.		
	unsigned short mediaType; // this is for mediacode...
	unsigned short vaulted;
	unsigned short reserved1;
	short	  recoveryaction;			// Recover Action when DB is avaible (Write the file)
#ifdef R12_V_VM_DB
   long	  subsesnum;	// R12.v (VMWARE) : 6/25/2008
   long   tapeflag_ext; // R12.v (DEDUPE)
   char   reserved[10];
#else
   char   reserved[18];
#endif //R12_V_VM_DB
} ASDB_SessionMapW, *PASDB_SessionMapW;
#endif

// R12.5 (10/27/2008) -- RecoverStaging Info (MIGRATION_RETRIES)
#define		STAGING_INSERT		1
#define		STAGING_NOTINSERT	2

//SPECIAL ERROR VALUE FOR MIGRATION RETRIES in STAGING RECORD.
#define		STAGING_RETRIAL_SIGNAL		200	 //(Error and not insertion time)

typedef struct tagASDB_Staging {	  
   long		jobid;
   long		sesnum;					// backup session #
   long		sessid;					// db internal session id
   long		commonjobid;			// Job's common id
   long		masterjobid;			// Job's mater id
   char		ds_groupname[16];		// disk staging group name
   
									// For future expansion. Not use now.
   char		ds_tapename[24];		// data staging tape name
   long		ds_taperandomid;		// data staging tape ID
   long		ds_tapeseqnum;			// data staging tape seq
   char		ds_serial[32];			// data staging serial number
   char		ds_poolname[16];		// data staging media pool name

   char		dm_tapename[24];		// data migration final tape name
   long		dm_taperandomid;		// data migration final tape ID
   long		dm_tapeseqnum;			// data migration final tape seq
   char		dm_serial[32];			// data migration final serial number
   char		dm_groupname[16];		// data migration final group name
   char		dm_poolname[16];		// data migration media pool name
   long		dm_tapeselflag;			// first tape selection flags
   long		dm_tapeseltimeout;		// first tape time out	
   long		dm_spanselflag;			// span tape selection flags
   long		dm_spanseltimeout;		// span tape time out
   long		dm_duedate;				// 0: migration completed. The time the session should be migrated.
   long		pruneduedate;			// the time the seesion should be purged.
   long		stagingflags;			// bit field. staging session flag defined above 
   long		ismigrated;				//   0 - Not migrated
									//   1 - Migration completed. 
									//   2 - Don't need to be migrated 
   long		snaplock;				// SNAPLOCK or not
   long		lastvaliddest;			// Last valid destination for a common job
   long		executiondate;			// The time the job is executed.
   
   short	recoveryaction;			// Recover Action when DB is avaible (Write the file)
   short	sessionstatus;
   long		session_totalkb;		// R11.5 +sp2 (return copied staging session)
   long		session_starttime;		// R11.5 +sp2 (return copied staging session)

   short    insertingflag;          //   STAGING_INSERT (1) / STAGING_NOTINSERT (2)
                                    
   char     reserved[62];           //   reserved[64] --> 62 // 10/23/2008 (for inserting flag)   
} ASDB_Staging, *PASDB_Staging; 


////// R12.V (10/28/2008) //////////////
#ifdef DB_STAGING_GROUP

#define		ASDB_STAGINGGROUP_ALL								100
#define		ASDB_STAGINGGROUP_BY_COMMON_AND_MASTER_JOBID		101
#define		ASDB_STAGINGGROUP_BY_COMMON_JOBID					102

typedef struct tagASDB_StagingGroupParm {	  
   long  commonjobid;
   long	 masterjobid;
   long  lFlag;
   char  reserved[256];
} ASDB_StagingGroupParm, *PASDB_StagingGroupParm; 

typedef struct tagASDB_StagingGroupParmW {	  
   long  commonjobid;
   long	 masterjobid;
   long  lFlag;
   char  reserved[256];
} ASDB_StagingGroupParmW, *PASDB_StagingGroupParmW; 


typedef struct tagASDB_StagingGroup {	  
   long		sessid;					// db internal session id
   long		commonjobid;			// Job's common id
   long		masterjobid;			// Job's mater id
   
   char		dm_tapename[24];		// data migration final tape name
   char		dm_groupname[16];		// data migration final group name
   long		dm_duedate;				// 0: migration completed. The time the session should be migrated.
   long		pruneduedate;			// the time the seesion should be purged.
   long		ismigrated;				//   0 - Not migrated
									//   1 - Migration completed. 
									//   2 - Don't need to be migrated 
   long		executiondate;			// The time the job is executed.
   short    isPendingSchedule;		//   1 - pending  2 - not pending. 
   
   __int64	total_session_totalkb;
   long     total_numberof_sessions;
   short	jobstatus;
   short	isFSdevice;		//   1 - Yes	
   long		stagingFlag;      
   char     reserved[250];           
} ASDB_StagingGroup, *PASDB_StagingGroup; 

typedef struct tagASDB_StagingGroupW {	  
   long		sessid;					// db internal session id
   long		commonjobid;			// Job's common id
   long		masterjobid;			// Job's mater id
   
   wchar_t	dm_tapename[24];		// data migration final tape name
   wchar_t	dm_groupname[16];		// data migration final group name
   long		dm_duedate;				// 0: migration completed. The time the session should be migrated.
   long		pruneduedate;			// the time the seesion should be purged.
   long		ismigrated;				//   0 - Not migrated
									//   1 - Migration completed. 
									//   2 - Don't need to be migrated 
   long		executiondate;			// The time the job is executed.
   short    isPendingSchedule;		//   1 - pending  2 - not pending. 
   
   __int64	total_session_totalkb;
   long     total_numberof_sessions;
   short	jobstatus;
   short	isFSdevice;		//   1 - Yes	
   long		stagingFlag;      
   char     reserved[250];           
} ASDB_StagingGroupW, *PASDB_StagingGroupW; 

#endif //DB_STAGING_GROUP

typedef struct tagASDB_TapeInfoParm{        
   char   tapename[24];
   short  randomid;
   short  seqnum;
   char   serialnum [32];
#ifdef SESSNUM_INCREASE
   unsigned short sesnum_old;
#else
   unsigned short sesnum;
#endif
   short  recoveryaction;			// Recover Action when DB is avaible (Write the file)
#ifdef SESSNUM_INCREASE
   long   sesnum;
   char   reserved[20];
#else
   char   reserved[24];
#endif
} ASDB_TapeInfoParm, *PASDB_TapeInfoParm;


#define	ASDB_MASTER_JOB	1
#define	ASDB_SLAVE_JOB	2

#define ASDB_GREATER_CRITERIA		1
#define ASDB_GREATER_EQUAL_CRITERIA	2
#define ASDB_LESS_CRITERIA		3
#define ASDB_LESS_EQUAL_CRITERIA	4
#define ASDB_EQUAL_CRITERIA		5
#define	ASDB_RANGE_CRITERIA		6
#define ASDB_ALL_CRITERIA		7

typedef struct tagASDB_InJobMapInfo {	  
   long	  jobid;
   long   commonjobid;
   long   masterjobid;		// use masterslaveflag as masterjobid
   long   reserved;	   		
   char   reserved1[16];
} ASDB_InJobMapInfo, *PASDB_InJobMapInfo; 

#define REPORT_MASTER_JOBS 1

typedef struct tagASDB_MasterJobMapInfoParm {	  
   long	  fromtime;
   long   totime;
   long   criteria;
   long	  commonjobid;
   char   hostname[64];
   long   queryFlag;
   long   reserved1;	   		
} ASDB_MasterJobMapInfoParm, *PASDB_MasterJobMapInfoParm; 

// Oripin: UNICODE_JIS Support upasr01
typedef struct tagASDB_MasterJobMapInfoParmW {	  
   long	  fromtime;
   long   totime;
   long   criteria;
   long	  commonjobid;
   wchar_t   hostname[64];
   long   queryFlag;
   long   reserved1;	   		
} ASDB_MasterJobMapInfoParmW, *PASDB_MasterJobMapInfoParmW; 
typedef struct tagASDB_ChildJobMapInfoParm {	  
   long	  masterjobid;
   char   hostname[64];		
   long   reserved;	   		
} ASDB_ChildJobMapInfoParm, *PASDB_ChildJobMapInfoParm; 


typedef struct tagASDB_MasterJobMapInfo {	  
   long	  jobid;
   long   commonjobid;
   long   masterjobid; 		// use masterslaveflag as masterjobid
   short  type;
   short  status;
   long   starttime;
   long   endtime;
   char   hostname[64];
   char   owner[32];
   char   comment[96];
   char   setname[12];
   long	  jobno;	   
   char   reserved[24];	   		
} ASDB_MasterJobMapInfo, *PASDB_MasterJobMapInfo; 
// Oripin: UNICODE_JIS Support upasr01
typedef struct tagASDB_MasterJobMapInfoW {	  
   long	  jobid;
   long   commonjobid;
   long   masterjobid; 		// use masterslaveflag as masterjobid
   short  type;
   short  status;
   long   starttime;
   long   endtime;
   wchar_t   hostname[64];
   wchar_t   owner[32];
   wchar_t   comment[96];
   wchar_t   setname[12];
   long	  jobno;	   
   char   reserved[24];	   		
} ASDB_MasterJobMapInfoW, *PASDB_MasterJobMapInfoW; 

typedef struct tagASDB_ChildJobMapInfo {	  
   long	  jobid;
   long   commonjobid;
   long   masterjobid;		// use masterslaveflag as masterjobid
   short  type;
   short  status;
   long   starttime;
   long   endtime;
   long	  jobno;	   
   char   reserved[20];	   		
} ASDB_ChildJobMapInfo, *PASDB_ChildJobMapInfo; 

//////////// Session & GUID /////////////////////////

#define ASDB_GET_LOGICCALPATH		1
#define ASDB_GET_PHYSICALPATH		2
#define ASDB_GET_MSESSGUID			3	
#define ASDB_BY_SESSIONID			4

typedef struct tagASDB_SessionGUIDParm {	
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
#ifdef SESSNUM_INCREASE
   long  sesnum;
   char	 reserved[16];
#else
   unsigned short sesnum;
   char	 reserved[18];
#endif
} ASDB_SessionGUIDParm, *PASDB_SessionGUIDParm; 

#ifdef R12_5_MANAGE_PASSWORD
typedef struct tagASDB_SessionGUIDParmW {	
   wchar_t  tapename[24];
   short randomid;
   short seqnum;
   wchar_t  serialnum [32];
#ifdef SESSNUM_INCREASE
   long  sesnum;
   char	 reserved[16];
#else
   unsigned short sesnum;
   char	 reserved[18];
#endif
} ASDB_SessionGUIDParmW, *PASDB_SessionGUIDParmW; 
#endif

#ifdef SESSNUM_INCREASE
typedef struct tagASDB_SessionGUID_Old {	
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
   unsigned short sesnum;
   long  jobid;   		
	  
   long  sessid;	
   char  sessguid[16];
   char  msessguid[16];
   long	 logicalpid;
   long	 physicalpid;
   unsigned short appType;	//R11.5+sp3 use this field. 	//long  reserved1;
   __int64 scenarioID;		//R11.5+sp3 (XOsoft)
   long  flag_ext;
   long  encr_algo;
   long  src_app_ver;
   char	 reserved2[12];	
} ASDB_SessionGUID_Old, *PASDB_SessionGUID_Old; 
// Oripin: UNICODE_JIS Support :redbh03


typedef struct tagASDB_SessionGUIDW_Old {	
	wchar_t  tapename[24];
	short randomid;
	short seqnum;
	wchar_t  serialnum [32];
	unsigned short sesnum;
	long  jobid;   		

	long  sessid;	
	wchar_t  sessguid[16];
	wchar_t  msessguid[16];
	long	 logicalpid;
	long	 physicalpid;
	unsigned short appType;	//R11.5+sp3 use this field. 	//long  reserved1;
	__int64 scenarioID;		//R11.5+sp3 (XOsoft)
	long  flag_ext;
	long  encr_algo;
	long  src_app_ver;
	char	 reserved2[12];	
} ASDB_SessionGUIDW_Old, *PASDB_SessionGUIDW_Old; 

typedef struct tagASDB_LPMergePathParm_Old {	
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
   unsigned short sesnum;
   char  phySrcHost[ASDB_MAX_PATH];   //  ex) \\HostName
   char  phySrcPath[ASDB_MAX_PATH];   
   char  logSrcHost[ASDB_MAX_PATH];   //  ex) \\HostName		 
   char  logSrcPath[ASDB_MAX_PATH];   
   char	 reserved[198];
} ASDB_LPMergePathParm_Old, *PASDB_LPMergePathParm_Old; 

typedef struct tagASDB_SessionGUID {	
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
   long  sesnum;
   long  jobid;   		
	  
   long  sessid;	
   char  sessguid[16];
   char  msessguid[16];
   long	 logicalpid;
   long	 physicalpid;
   unsigned short appType;	//R11.5+sp3 use this field. 	//long  reserved1;
   __int64 scenarioID;		//R11.5+sp3 (XOsoft)
   long  flag_ext;
   long  encr_algo;
   long  src_app_ver;
   char	 reserved2[12];	
} ASDB_SessionGUID, *PASDB_SessionGUID; 
// Oripin: UNICODE_JIS Support :redbh03


typedef struct tagASDB_SessionGUIDW {	
	wchar_t  tapename[24];
	short randomid;
	short seqnum;
	wchar_t  serialnum [32];
	long  sesnum;
	long  jobid;   		

	long  sessid;	
	wchar_t  sessguid[16];
	wchar_t  msessguid[16];
	long	 logicalpid;
	long	 physicalpid;
	unsigned short appType;	//R11.5+sp3 use this field. 	//long  reserved1;
	__int64 scenarioID;		//R11.5+sp3 (XOsoft)
	long  flag_ext;
	long  encr_algo;
	long  src_app_ver;
	char	 reserved2[12];	
} ASDB_SessionGUIDW, *PASDB_SessionGUIDW; 

typedef struct tagASDB_LPMergePathParm {	
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
   long  sesnum;
   char  phySrcHost[ASDB_MAX_PATH];   //  ex) \\HostName
   char  phySrcPath[ASDB_MAX_PATH];   
   char  logSrcHost[ASDB_MAX_PATH];   //  ex) \\HostName		 
   char  logSrcPath[ASDB_MAX_PATH];   
   char	 reserved[198];
} ASDB_LPMergePathParm, *PASDB_LPMergePathParm; 
#else
typedef struct tagASDB_SessionGUID {	
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
   unsigned short sesnum;
   long  jobid;   		
	  
   long  sessid;	
   char  sessguid[16];
   char  msessguid[16];
   long	 logicalpid;
   long	 physicalpid;
   unsigned short appType;	//R11.5+sp3 use this field. 	//long  reserved1;
   __int64 scenarioID;		//R11.5+sp3 (XOsoft)
   long  flag_ext;
   long  encr_algo;
   long  src_app_ver;
   char	 reserved2[12];	
} ASDB_SessionGUID, *PASDB_SessionGUID; 
// Oripin: UNICODE_JIS Support :redbh03


typedef struct tagASDB_SessionGUIDW {	
	wchar_t  tapename[24];
	short randomid;
	short seqnum;
	wchar_t  serialnum [32];
	unsigned short sesnum;
	long  jobid;   		

	long  sessid;	
	wchar_t  sessguid[16];
	wchar_t  msessguid[16];
	long	 logicalpid;
	long	 physicalpid;
	unsigned short appType;	//R11.5+sp3 use this field. 	//long  reserved1;
	__int64 scenarioID;		//R11.5+sp3 (XOsoft)
	long  flag_ext;
	long  encr_algo;
	long  src_app_ver;
	char	 reserved2[12];	
} ASDB_SessionGUIDW, *PASDB_SessionGUIDW; 

typedef struct tagASDB_LPMergePathParm {	
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
   unsigned short sesnum;
   char  phySrcHost[ASDB_MAX_PATH];   //  ex) \\HostName
   char  phySrcPath[ASDB_MAX_PATH];   
   char  logSrcHost[ASDB_MAX_PATH];   //  ex) \\HostName		 
   char  logSrcPath[ASDB_MAX_PATH];   
   char	 reserved[198];
} ASDB_LPMergePathParm, *PASDB_LPMergePathParm; 
#endif

typedef struct tagASDB_LPMergePathParmEX {	
   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
#ifdef SESSNUM_INCREASE
   long  sesnum;
#else
   unsigned short sesnum;
   short reserved1;	// padding 4 byte alignment.
#endif
   char  phySrcHost[ASDB_MAX_PATH];   //  ex) \\HostName
   char  phySrcPath[4096];   
   char  logSrcHost[ASDB_MAX_PATH];   //  ex) \\HostName		 
   char  logSrcPath[4096];   
   char	 reserved[8192];
} ASDB_LPMergePathParmEX, *PASDB_LPMergePathParmEX; 

typedef struct tagASDB_LPMergePathParmEXW {	
   wchar_t  tapename[24];
   short randomid;
   short seqnum;
   wchar_t  serialnum [32];
#ifdef SESSNUM_INCREASE
   long  sesnum;
#else
   unsigned short sesnum;
   short reserved1;	// padding 4 byte alignment.
#endif
   wchar_t  phySrcHost[ASDB_MAX_PATH];   //  ex) \\HostName
   wchar_t  phySrcPath[4096];   
   wchar_t  logSrcHost[ASDB_MAX_PATH];   //  ex) \\HostName		 
   wchar_t  logSrcPath[4096];   
   char	 reserved[8192];
} ASDB_LPMergePathParmEXW, *PASDB_LPMergePathParmEXW; 

typedef struct tagASDB_SeverHost {	
   char   hostname[64];
   char	  reserved[256];
} ASDB_SeverHost, *PASDB_SeverHost; 

typedef struct tagASDB_SeverHostW {	
	wchar_t   hostname[64];
	char	  reserved[256];
} ASDB_SeverHostW, *PASDB_SeverHostW;  // dayra01 translation lib api
typedef struct tag_REG_FILETIME { 
    DWORD dwLowDateTime; 
    DWORD dwHighDateTime; 
} REG_FILETIME, *PREG_FILETIME; 


#endif // ASDB_D2D2T


//////////// ORIOLE //////////////////////
typedef struct tagASDB_SessRecExtParm {	
   long  sessionid;	//If this is 0, the following fields (Tape Info) will be used for query purposes.

   char  tapename[24];
   short randomid;
   short seqnum;
   char  serialnum [32];
   long  sesnum;

   char	 reserved[68];
} ASDB_SessRecExtParm, *PASDB_SessRecExtParm;
// Oripin: UNICODE_JIS Support :redbh03

typedef struct tagASDB_SessRecExtParmW {	
	long  sessionid;	//If this is 0, the following fields (Tape Info) will be used for query purposes.

	wchar_t  tapename[24];
	short randomid;
	short seqnum;
	wchar_t  serialnum [32];
	long  sesnum;

	char	 reserved[68];
} ASDB_SessRecExtParmW, *PASDB_SessRecExtParmW;

typedef struct _tagASDB_SessRecExt 
{
   long  id;
   long  jobid;
   long  tapeid;
   short status;
   short sestype;
   long sesnum;
   short sesmethod;
   long  sesflags;
   long  qfablocknum;
   long  starttime;
   long  endtime;
   long  totalkb;
   long  totalfiles;
   long  totalmissed;
   long  srchostid;
   long  srcpathid;
   long  ownerid;
   short streamnum;
   short tapeseq_end;
   short fsname_length;
   long	 throughputMBPerMin;
   long  tapeDrvID;

   /// Get from SessionGuid.
   char  sessionGuid[16];
   char  parentSessionGUID[16];
   unsigned short appType;	//R11.5+sp3  
   __int64 scenarioID;		//R11.5+sp3 (XOsoft)	
   long  sessionFlagsExt;				//This will contain the information whether the session was TE encrypted or tape drive encrypted.
   long  sessionEncryptionAlgo;			//will be 0 if session is NOT encrypted.
   long	 src_app_ver;
   char	 reserved[48];
}ASDB_SessRecExt, *PASDB_SessRecExt;

// Oripin: UNICODE_JIS Support :redbh03
typedef struct _tagASDB_SessRecExtW 
{
	long  id;
	long  jobid;
	long  tapeid;
	short status;
	short sestype;
	long sesnum;
	short sesmethod;
	long  sesflags;
	long  qfablocknum;
	long  starttime;
	long  endtime;
	long  totalkb;
	long  totalfiles;
	long  totalmissed;
	long  srchostid;
	long  srcpathid;
	long  ownerid;
	short streamnum;
	short tapeseq_end;
	short fsname_length;
	long	 throughputMBPerMin;
	long  tapeDrvID;

	/// Get from SessionGuid.
	wchar_t  sessionGuid[16];
	wchar_t  parentSessionGUID[16];
	unsigned short appType;	//R11.5+sp3  
	__int64 scenarioID;		//R11.5+sp3 (XOsoft)	
	long  sessionFlagsExt;				//This will contain the information whether the session was TE encrypted or tape drive encrypted.
	long  sessionEncryptionAlgo;			//will be 0 if session is NOT encrypted.
	long	 src_app_ver;
	char	 reserved[48];
}ASDB_SessRecExtW, *PASDB_SessRecExtW;
#define ASDB_GET_All_SESSIONS	99
#if defined(UNICODE_JIS_SUPPORT)

typedef struct tagASDB_GetSessInfoParmW {	
	wchar_t  srcHost[64];
	wchar_t  srcPath[512];
	short sessionMethod;
	short sessionStatus;
	short sestype;
	long  lFlag;
	char	 reserved[126];
} ASDB_GetSessInfoParmW, *PASDB_GetSessInfoParmW;

#endif // UNICODE_JIS_SUPPORT
typedef struct tagASDB_GetSessInfoParm {	
   char  srcHost[64];
   char  srcPath[512];
   short sessionMethod;
   short sessionStatus;
   short sestype;
   long  lFlag;
   char	 reserved[126];
} ASDB_GetSessInfoParm, *PASDB_GetSessInfoParm;

#if 1 //def SIS_SUPPORT
typedef struct tagASDB_GetSisSessInfoParm {
   char  srcHost[64];
   char  srcPath[512];
   short sessionMethod;
   short sessionType;
   char  tapeName[24];
   short tapeRandomId;
   short tapeSeqNum;
   char	 reserved[126];
} ASDB_GetSisSessInfoParm, *PASDB_GetSisSessInfoParm;

typedef struct tagASDB_GetSisSessInfoParmW {
   wchar_t  srcHost[64];
   wchar_t  srcPath[512];
   short sessionMethod;
   short sessionType;
   wchar_t  tapeName[24];
   short tapeRandomId;
   short tapeSeqNum;
   char	 reserved[126];
} ASDB_GetSisSessInfoParmW, *PASDB_GetSisSessInfoParmW;
#endif //SIS_SUPPORT

#define MIGRATIONSTATUS_BY_STARTTIME	1
#define MIGRATIONSTATUS_BY_ENDTIME	2
typedef struct tagASDB_MigrationStatusParm {
   long	starttime;
   long endtime;
   long lFlag;
   char comment[96];
   char reserved[148];
} ASDB_MigrationStatusParm, *PASDB_MigrationStatusParm;

typedef struct tagASDB_GetLastJobIdParm {
   long commonjobid;
   long sessionMethod;
   long masterjobid;
   char reserved[56];		
} ASDB_GetLastJobIdParm, *PASDB_GetLastJobIdParm;

#if defined(MIDL_INVOKED)

#define RPC_SIZE(X)		[size_is(X)]

#else // ! MIDL_PASS

#define RPC_SIZE(X)		

#endif // MIDL_PASS

///////////////////////////////////////////
typedef struct _tagASDB_AsLogRec 
{
   unsigned long id;		
   long  jobid;
   long  logtime;
   long  sessid;
   long  moduleid;
   char  serverhost[64];
   char  agenthost[64];
   long  agenttype;
   long  msgtextid;
   char  msgtext[1024];
   char  username[64];
   long  alertbitmap;
   long  resourceclassid;
   long  action;
   char  resourcename[64];
   long  actionresult;
   char  attrtype[24];
   char  attrvalue[24];
   long  msgTypeid;
   long  severity;
   long  destid;
   long  totalRecord;
   char  reserved1[64];
//   char  header[64];
   long  eventid;
   long  eventtype;
   long  masterjobno;
   long  masterjobid;
   unsigned long  alerteventid;
   unsigned long  alertblobsize;
   char header[156];
//   char  reserved1[156]; 
   RPC_SIZE(alertblobsize) char * pAlertblob;	
}ASDB_AsLogRec, * PASDB_AsLogRec;
// Oripin: UNICODE_JIS Support dayra01
typedef struct _tagASDB_AsLogRecW 
{
	unsigned long id;		
	long  jobid;
	long  logtime;
	long  sessid;
	long  moduleid;
	wchar_t  serverhost[64];
	wchar_t  agenthost[64];
	long  agenttype;
	long  msgtextid;
	wchar_t  msgtext[1024];
	wchar_t  username[64];
	long  alertbitmap;
	long  resourceclassid;
	long  action;
	wchar_t  resourcename[64];
	long  actionresult;
	wchar_t  attrtype[24];
	wchar_t  attrvalue[24];
	long  msgTypeid;
	long  severity;
	long  destid;
	long  totalRecord;
    char  reserved1[64];
//   char  header[64];
   long  eventid;
   long  eventtype;
   long  masterjobno;
   long  masterjobid;
   unsigned long  alerteventid;
   unsigned long  alertblobsize;
   wchar_t header[156];
//   char  reserved1[156]; 
   RPC_SIZE(alertblobsize) wchar_t * pAlertblob;	
}ASDB_AsLogRecW, * PASDB_AsLogRecW;


#ifdef BAB_CPDB_SUPPORT
typedef struct _tagASDB_RemoteHostRec 
{
   long rhostid;
   long machinetype;
   long networktype;
   long ostype;
   long lastupdated;
   long rhostsflags;
   char rhostname[64];
   char ipaddress[32];
   char osdesc[32];
   char osver[32];
   char hwtype[32];
   char rh_reserved[32];
}ASDB_RemoteHostRec, * PASDB_RemoteHostRec;
#endif

/////// To show errors/warnings in last 24 hours for activity log;
//Select * from aslog where logtime > start_time and 
//logtime < end_time and destid = given_number and msgtypeid=given_number; 
 
//////// To filter activity log/audit log by time.
//Select * from aslog where logtime > start_time and 
//logtime < end_time  destid = given_number; 
 
//////// To filter activity log/joblog.
//Select * from aslog where jobid=given_number and destid = given_number;
//Select * from aslog where jobid>given_number and jobid < given_number and destid = given_number;
//Select * from aslog where logtime < end_time and destid = given_number and jobid>given_number and jobid < given_number and destid = given_number;
 
//////// To filter daemon log.
//Select * from aslog where destid = given_number and moduleid=given_number; 
//Select * from aslog where logtime > start_time and logtime < end_time  and destid = given_number and moduleid=given_number; 
 
 
/////// Query all
//Select * from aslog where  jobid > start_job_id and jobid< end_job_id 
//and logtime > start_time and logtime < end_time and destid = given_number and  moduleid = given_number and msgtypeid = given_number.
    
#define ASDB_GET_ASLOG_BY_JOBID			 1
#define ASDB_GET_ASLOG_BY_MODULEID		 2
#define ASDB_GET_ASLOG_BY_RESOURCE_CLASSID	 3

#define ASDB_GET_ASLOG_BY_LOGTIME_ERROR_WARNING	 4	// logtime / destid / msgtypeid
#define ASDB_GET_ASLOG_BY_LOGTIME_AUDITLOG	 5	// logtime / destid 
#define ASDB_GET_ASLOG_BY_JOBLOG		 6	// jobid / destid
#define ASDB_GET_ASLOG_BY_JOBID_JOBLOG		 7	// jobid / destid
#define ASDB_GET_ASLOG_BY_LOGTIME_JOBID_JOBLOG	 8	// logtime / jobid / destid
#define ASDB_GET_ASLOG_BY_DAEMONLOG		 9	// moduleid / destid
#define ASDB_GET_ASLOG_BY_LOGTIME_DAEMONLOG	10	// logtime / moduleid / destid
#define ASDB_GET_ASLOG_BY_ALL			11	//
#ifdef BAB_LOGGER_GUISUPPORT
#define ASDB_GET_ASLOG_BY_SVR_LOGTIME_TYPE		 12	// serverhost / logtime / type
#endif

// To improve GUI query job history performance
#define ASDB_GET_ASLOG_BY_JOBID_MSGTYPEID_ID	13	// jobid / msgtypeid / id

#ifdef ARC_ROLE_MANAGEMENT
#define	ASDB_GET_ASLOG_BY_AUDIT_LOG_FILTER		14	// msgTypeId / agentHost / action / username (serverhost) / moduleID / logtime
#endif

//// For querying For Error (Job history)
#define ASDB_GET_ASLOG_BY_JOBID_ERROR			 20
#define ASDB_GET_ASLOG_BY_JOBID_WARNING			 21
#define ASDB_GET_ASLOG_BY_JOBID_ERROR_OR_WARNING	 22


#define MAX_EM_GET_ASLOGIST              100

#ifdef BAB_LOGGER_GUISUPPORT
typedef struct _tagASDB_AsLogTimeRangeParm 
{
   char  serverhost[128];
   char  reserved[128];
}ASDB_AsLogTimeRangeParm, * PASDB_AsLogTimeRangeParm;
#endif

//upasr01
#ifdef BAB_LOGGER_GUISUPPORT
typedef struct _tagASDB_AsLogTimeRangeParmW
{
   wchar_t  serverhost[128];
   char  reserved[128];
}ASDB_AsLogTimeRangeParmW, * PASDB_AsLogTimeRangeParmW;
#endif
typedef struct _tagASDB_AsLogJobIDParm 
{
   long  starttime;
   long  endtime;
   long  start_jobid;
   long  end_jobid;
   long  master_jobid;
   long  sort_method;
   char  jobcomment[96];
   char  serverhost[64];
   long  criteria;
   char  reserved[36];
}ASDB_AsLogJobIDParm, * PASDB_AsLogJobIDParm;

//upasr01
typedef struct _tagASDB_AsLogJobIDParmW 
{
   long  starttime;
   long  endtime;
   long  start_jobid;
   long  end_jobid;
   long  master_jobid;
   long  sort_method;
   wchar_t  jobcomment[96];
   wchar_t  serverhost[64];
   long  criteria;
   char  reserved[36];
}ASDB_AsLogJobIDParmW, * PASDB_AsLogJobIDParmW;
typedef struct _tagPASDB_AsLogJobIDRec 
{
   long  id;
   short type;
   short status;
   long  starttime;
   long  endtime;
   char  owner[32];
   char  comment[96];
   long  handle;
   char  host[16];
   char  setname[12];            //SetName[12];      // Up to 8 characters   
   long  jobno;
   long  masterjobid;
   char reserved[56];
}ASDB_AsLogJobIDRec, * PASDB_AsLogJobIDRec;
typedef struct _tagPASDB_AsLogJobIDRecW 
{
   long  id;
   short type;
   short status;
   long  starttime;
   long  endtime;
   wchar_t  owner[32];
   wchar_t  comment[96];
   long  handle;
   wchar_t  host[16];
   wchar_t  setname[12];            //SetName[12];      // Up to 8 characters   
   long  jobno;
   long  masterjobid;
   char reserved[56];
}ASDB_AsLogJobIDRecW, * PASDB_AsLogJobIDRecW;
typedef struct _tagASDB_AsLogParmW 
{
   long  jobid;
   long  sessid;
   long  moduleid;
   long  resourceclassid;
   long  severity;
   long  criteria;
   long  starttime;
   long  endtime;
   long  start_jobid;
   long  end_jobid;
   long  msgTypeid;
   long  destid;
   long  userDefinedRecordCount;
   long  start_sessid;
   long  end_sessid;
   long  start_id;
   long  end_id;
   long  start_pos;
   long  end_pos;
   long  sort_method;
   long  master_jobid;
   wchar_t  logmessage[96];   //upasr01 Unicode
   wchar_t  serverhost[64];   //upasr01 Unicode
   wchar_t  agenthost[64];    //upasr01 Unicode
   char  reserved[40];     //upasr01 Unicode
}ASDB_AsLogParmW, * PASDB_AsLogParmW;

typedef struct _tagASDB_AsLogParm 
{
   long  jobid;
   long  sessid;
   long  moduleid;
   long  resourceclassid;
   long  severity;
   long  criteria;
   long  starttime;
   long  endtime;
   long  start_jobid;
   long  end_jobid;
   long  msgTypeid;
   long  destid;
   long  userDefinedRecordCount;
   long  start_sessid;
   long  end_sessid;
   long  start_id;
   long  end_id;
   long  start_pos;
   long  end_pos;
   long  sort_method;
   long  master_jobid;
   char  logmessage[96];
   char  serverhost[64];
   char  agenthost[64];
   char  reserved[40];
}ASDB_AsLogParm, * PASDB_AsLogParm;


////////////// TOPOLOGY_INFO //////////////////
//serverClass definitions
#define SERVER_CLASS_MEMBER             1
#define SERVER_CLASS_SECONDARY          2
#define SERVER_CLASS_PRIMARY            4

//serverOsType definitions
#define SERVER_OS_WIN                   1
#define SERVER_OS_UNIX                  2

//sanType definitions
#define SERVER_NOT_ON_SAN    	        0
#define SERVER_SAN_DISTRIBUTED  		1
#define SERVER_SAN_PRIMARY          	2

//serverState definitions
#define SERVER_STATE_DECOMISSIONED      0x00000000
#define SERVER_STATE_OK                 0x00000001
#define SERVER_STATE_CMO_EXPIRED        0x00000002

//lFlag definitions
#define OFF_ACTIVITY_MESSAGE1		0
#define ON_ACTIVITY_MESSAGE1		1

typedef struct _tagASDB_AgentHost_Name
{
	char	name[64];

} ASDB_AGENTHOST_NAME, *PASDB_AGENTHOST_NAME;

typedef struct _tagASDB_AgentHost_NameW
{
	wchar_t	name[64];

} ASDB_AGENTHOST_NAMEW, *PASDB_AGENTHOST_NAMEW;
typedef struct _tagASDB_TOPOLOGY_INFO
{
      char  domainName[64];
      char  primaryName[64];
      char  serverName[64];
      long  serverClass; 
      long  serverOsType; 
      long  sanType; 
      long  serverState;  // It is a bitwise flag...
      short lFlag;	// On/Off (Activity Error message)
      char  reserved[46]; // 112->110, 110->46 for adding primaryName
} ASDB_TOPOLOGY_INFO, *PASDB_TOPOLOGY_INFO;
// Oripin: UNICODE_JIS Support :redbh03
typedef struct _tagASDB_TOPOLOGY_INFOW
{
	wchar_t  domainName[64];
    wchar_t  primaryName[64];
	wchar_t  serverName[64];
	long  serverClass; 
	long  serverOsType; 
	long  sanType; 
	long  serverState; 
	short lFlag;	// On/Off (Activity Error message)
	char  reserved[46]; // 112->110
} ASDB_TOPOLOGY_INFOW, *PASDB_TOPOLOGY_INFOW;
///////////////////////////////////////////////////

//20081124_New_Dump_DB
typedef struct _tagASDB_TOPOLOGY_INFOEX
{
      char  domainName[64+1];
      char  primaryName[64+1];
      char  serverName[64+1];
      long  serverClass; 
      long  serverOsType; 
      long  sanType; 
      long  serverState;  // It is a bitwise flag...
      long  topologyid;
	  char  domainguid[16];
	  long  len_guid;  //length of domainguid
	  char  admk[256];
	  long  len_admk;  //length of admk
      char  reserved[41]; 
} ASDB_TOPOLOGY_INFOEX, *PASDB_TOPOLOGY_INFOEX;

// DBMaintenance ...
#define   DB_MAINT_UPD_STA                 0x00000001		// Update Statistics
#define   DB_MAINT_REB_IND                 0x00000002		// Re-build Indexes
#define   DB_MAINT_DB_CONT		   0x00000004		// Check DB Integrity
#define   DB_MAINT_REDU_SIZ                0x00000008		// Reduce size of BAB DB

#define   DAILY_SHRINK_FILE_ONLY	   999			// Shrink aslog file to reduce file size...

//////////////// XOsoft //////////////////////
///ERROR SET...
#define		XO_SQL_NO_DATA			100		// There is no db record.
#define		XO_INVALID_DBHANDLE		1000		// Invalid DB handle
#define		XO_SQL_SYNTAX_ERROR		1001		// SQL syntax error
#define		XO_SQL_FETCH_ERROR		1002		// SQL fetch (Query) error.
#define		XO_SQL_INSERT_ERROR		1003		// Unable to insert record.
#define		XO_SQL_UPDATE_ERROR		1004		// Unable to update record.
#define		XO_SQL_DELETE_ERROR		1005		// Unable to delete record.
#define		XO_WRONG_BUFFER_ERROR		1006		// Buffer error.
#define		XO_AUTH_ERROR			1007		// Authentication Error.
#define		XO_LOGIN_ERROR			1008		// Unable to logon. 
#define		XO_LOGOUT_ERROR			1009		// Fail to logout.
#define		XO_INVALID_FILE_HANDLE		2001		// Invalid File Handle.
#define		XO_FILE_WRITE_ERROR		2002		// Unable to write the record into a file.
#define		XO_FILE_READ_ERROR		2003		// Unable to read the record from a file.
#define		XO_INVALID_FILE_SIZE		2004		// Invalid file size
#define		XO_FILE_DELETE_ERROR		2005		// Unable to delete the file.
#define		XO_RPC_ERROR			3000		// Unable to connect to RPC server.

#define ASDB_SCENARIOFLAG_WITHOUT_XML	100

typedef struct _tagASDB_ScenarioInfo_Parm
{
      __int64 scID;
      wchar_t scName[512];
      char reserved[256];
}ASDB_Scenario_Info_Parm, *PASDB_Scenario_Info_Parm;

typedef struct _tagASDB_Scenario_Info
{
      __int64 scID;
      wchar_t scName[512];
      wchar_t MasterServer[256];
      wchar_t ReplicaServer[256];
      wchar_t scenarioType[64];
      char reserved[512]; 
}ASDB_Scenario_Info, *PASDB_Scenario_Info;

typedef struct _tagASDB_Scenario_XML_Parm
{
      __int64 scID;
      wchar_t scName[512];
      long    flag;
      char reserved[132]; 		
}ASDB_Scenario_XML_Parm, *PASDB_Scenario_XML_Parm;

typedef struct _tagASDB_Scenario_XML
{
      __int64 scID;
      wchar_t scName[512];
      wchar_t sRepStatus[64];
      long lastBackupStatus;
      long lastBackupTime;
      char reserved[256]; 
}ASDB_Scenario_XML, *PASDB_Scenario_XML;
//////////////// XOsoft //////////////////////

////  (Oriole) SetupMemberDB ////////
typedef struct _tagASDB_SetupMemberDB
{
      char szPrimaryMachineName[64]; 
      char username[64];     
      char password[64];     
	  long noCopyASNTFlag;
      char reserved[316]; // add noCopyASNTFlag
}ASDB_SetupMemberDB, *PASDB_SetupMemberDB;


#define ASDB_NODE_GETSTATUS	1000
/////// ASRPTNODE ////////////
typedef struct _tagASDB_NodeInfoParm 
{
      long	scheduleTime;
      long	execTime;
      long	jobNo;
      long	commonJobID;
      long	masterJobID;
      char	nodeName[64];
      long	lFlag;			// ASDB_NODE_GETSTATUS (latest node): 
      char	reserved[112];		
}ASDB_NodeInfoParm, *PASDB_NodeInfoParm;
typedef struct _tagASDB_NodeInfoParmW 
{
	long	scheduleTime;
	long	execTime;
	long	jobNo;
	long	commonJobID;
	long	masterJobID;
	wchar_t	nodeName[64];
    long	lFlag;			// ASDB_NODE_GETSTATUS (latest node): 
	char	reserved[112];		
}ASDB_NodeInfoParmW, *PASDB_NodeInfoParmW; // dayra01

typedef struct _tagASDB_NodeInfo 
{
      long	scheduleTime;
      long	executionTime;
      long	jobNo;
      long	commonJobID;
      long	masterJobID;
      char	nodeName[64];
      long	status;
      long	backupMethod;		// full / incr / ...
      long	jobType;
      // _R12.v (DASHBOARD)(4/18/2008)
      char	isFullyselected;  	// Full Backup or Partial
      char	DRBackup;		// DR Backup (NA , Enable, NotEnable)
      long	MajorVersion;
      long	MinorVersion;
      long	ServicePack;
      char	reserved[114];		// 128 --> 126 --> 114
}ASDB_NodeInfo, *PASDB_NodeInfo;

typedef struct _tagASDB_NodeInfoW 
{
	long	scheduleTime;
	long	executionTime;
	long	jobNo;
	long	commonJobID;
	long	masterJobID;
	wchar_t	nodeName[64];
	long	status;
	long	backupMethod;
	long	jobType;
    // _R12.v (DASHBOARD)(4/18/2008)
    char	isFullyselected;  	// Full Backup or Partial
    char	DRBackup;		// DR Backup (NA , Enable, NotEnable)
    long	MajorVersion;
    long	MinorVersion;
    long	ServicePack;
    char	reserved[114];		// 128 --> 126 --> 114
}ASDB_NodeInfoW, *PASDB_NodeInfoW; // dayra01
////////// SQLNODE_INFO //////////////
typedef struct _tagASDB_SQLNODE_INFOParm
{
      char primaryMachineName[64]; 
      char username[64];     
      char password[64];     
      char reserved[320]; 		
}ASDB_SQLNODE_INFOParm, *PASDB_SQLNODE_INFOParm;
// Oripin: UNICODE_JIS Support redbh03
typedef struct _tagASDB_SQLNODE_INFOParmW
{
	wchar_t primaryMachineName[64]; 
	wchar_t username[64];     
	wchar_t password[64];     
	char    reserved[320]; 		
}ASDB_SQLNODE_INFOParmW, *PASDB_SQLNODE_INFOParmW;

typedef struct _tagASDB_SQLNODE_INFO
{
      char  sqlserverName[64];
      char  sqlInstanceName[64];
      char  sqlUserName[64];	        // sa user
      char  sqlPassWord[64];		// sa password
      char  sqlOsUserName[64];		
      char  sqlOsPassword[64];
      char  primaryDomain[64];		//Primary system account
      char  primaryUserName[64];	// 
      char  primaryPassword[64];	// ..
      char  localDomain[64];		// Local system account
      char  localUserName[64];		//
      char  localPassword[64];		// ..
      long  securityMode;		// 1: SQL_WINDOWSONLY , 2: SQL_MIXEDMODE
      char  reserved[260];
} ASDB_SQLNODE_INFO, *PASDB_SQLNODE_INFO;
// Oripin: UNICODE_JIS Support redbh03
typedef struct _tagASDB_SQLNODE_INFOW
{
	wchar_t  sqlserverName[64];
	wchar_t  sqlInstanceName[64];
	wchar_t  sqlUserName[64];	        // sa user
	wchar_t  sqlPassWord[64];		// sa password
	wchar_t  sqlOsUserName[64];		
	wchar_t  sqlOsPassword[64];
	wchar_t  primaryDomain[64];		//Primary system account
	wchar_t  primaryUserName[64];	// 
	wchar_t  primaryPassword[64];	// ..
	wchar_t  localDomain[64];		// Local system account
	wchar_t  localUserName[64];		//
	wchar_t  localPassword[64];		// ..
	long  securityMode;		// 1: SQL_WINDOWSONLY , 2: SQL_MIXEDMODE
	char  reserved[260];
} ASDB_SQLNODE_INFOW, *PASDB_SQLNODE_INFOW;


//////// VMWARE ////////////////
///ERROR SET...
#define		VM_SQL_NO_DATA			100		// There is no db record.
#define		VM_INVALID_DBHANDLE		1000		// Invalid DB handle
#define		VM_SQL_SYNTAX_ERROR		1001		// SQL syntax error
#define		VM_SQL_FETCH_ERROR		1002		// SQL fetch (Query) error.
#define		VM_SQL_INSERT_ERROR		1003		// Unable to insert record.
#define		VM_SQL_UPDATE_ERROR		1004		// Unable to update record.
#define		VM_SQL_DELETE_ERROR		1005		// Unable to delete record.
#define		VM_WRONG_BUFFER_ERROR		1006		// Buffer error.
#define		VM_AUTH_ERROR			1007		// Authentication Error.
#define		VM_LOGIN_ERROR			1008		// Unable to logon. 
#define		VM_LOGOUT_ERROR			1009		// Fail to logout.
#define		VM_RPC_ERROR			3000		// Unable to connect to RPC server.

#define VCB_MAX_HOST_NAME 256

typedef struct _tagASDB_VMRec_Parm
{
      wchar_t proxyName[VCB_MAX_HOST_NAME];
      wchar_t esxName[VCB_MAX_HOST_NAME];
      wchar_t vmName[VCB_MAX_HOST_NAME];
      long    type;			// 1'VCBProxy, 2'ESXServer, 3'VM, 4' ALL MATCH (Proxy & esx & vm)
#ifdef R12_V_VM_DB
	  short   vmtype;				// (R12.v)
      char    reserved[258];			// 260 --> 258 
#else
      char    reserved[260];	
#endif
}ASDB_VMRecParm, *PASDB_VMRecParm;

typedef struct _tagASDB_VMRec
{
      wchar_t proxyName[VCB_MAX_HOST_NAME];
      wchar_t esxName[VCB_MAX_HOST_NAME];
      wchar_t vmName[VCB_MAX_HOST_NAME];
      long    ostype; 		// WIN ' 1   UNIX/Linux ' 2  NETWARE ' 3  
      long    xmlSize;
#ifdef R12_V_VM_DB
	  short   vmtype;				// (R12.v)
      char    reserved[258];			// 260 --> 258  	
#else
      char    reserved[260];	
#endif
}ASDB_VMRec, *PASDB_VMRec;

typedef struct _tagASDB_VMNameRec
{
   wchar_t name[VCB_MAX_HOST_NAME];
#ifdef R12_V_VM_DB
   short   vmtype;				// ASDB_VM_VMTYPE_VMWARE(0), ASDB_VM_VMTYPE_HYPERV(1), ASDB_VM_VMTYPE_ALL (900)
   char  reserved[254];				// 256 --> 254	
#else
   char  reserved[256];
#endif
}ASDB_VMNameRec, *PASDB_VMNameRec;

typedef struct _tagASDB_VMEsxNameRec
{
   wchar_t proxyName[VCB_MAX_HOST_NAME];
   wchar_t esxName[VCB_MAX_HOST_NAME];
   char  reserved[256];
}ASDB_VMEsxNameRec, *PASDB_VMEsxNameRec;

// R12.v... (3/20/2008)
typedef struct _tagASDB_VMGuidInfo_Parm
{
      wchar_t vmName[VCB_MAX_HOST_NAME];	// INPUT
      wchar_t ipaddress[64];				// INPUT	
      char    guid[16]; 					// INPUT (?) 16-byte binary GUID
	  char    encry_guid[128];				// INPUT (encrypted guid for RPC) : To prevent security hold through the RPC
      short   vmtype;						// INPUT	
      char    reserved[126];	
}ASDB_VMGuidInfoParm, *PASDB_VMGuidInfoParm;
// R12.v... (3/20/2008)
typedef struct _tagASDB_VMGuidInfo
{
      wchar_t vmName[VCB_MAX_HOST_NAME];
      wchar_t ipaddress[64];
      char    guid[16]; 			// 16-byte binary GUID
	  char    encry_guid[128];      // encrypted GUID for RPC. (To prevent security hold through the RPC)
	  char	  bEncr_guid;			// 0 - NO, 1 - YES.
      short   vmtype;
	  //------- (9/15/2008): To add/update ashost table for VM.
      long    MajorVersion;
	  long    MinorVersion;
	  long    ServicePack;
      long    BuildNumber;
      char    reserved[109];	// 125 --> 109
}ASDB_VMGuidInfo, *PASDB_VMGuidInfo;

// R12.v... (5/14/2008)
typedef struct _tagASDB_VMTopology_Parm
{
     wchar_t vmName[VCB_MAX_HOST_NAME];           // IN
     wchar_t ipaddress[64];                       // IN   
     short   vmtype;                              // IN   
     char    reserved[126];         
}ASDB_VMTopologyParm, *PASDB_VMTopologyParm;
// R12.v... (5/14/2008)
typedef struct _tagASDB_VMTopology
{
      wchar_t 	vmName[VCB_MAX_HOST_NAME];
      wchar_t 	phost[VCB_MAX_HOST_NAME];        // Ptsical host.
      wchar_t 	ipaddress[64];
      wchar_t   vcentername[VCB_MAX_HOST_NAME];  // virtual center name
	  wchar_t	dataStore[128];					// ESX data store of the VM
      short     vmtype;
      char      reserved[126];       
}ASDB_VMTopology, *PASDB_VMTopology;

// R12.v ... (5/20/2008)

#define ASDB_VM_VMTYPE_VMWARE	0
#define ASDB_VM_VMTYPE_HYPERV	1
#define ASDB_VM_VMTYPE_ALL	900

typedef struct _tagASDB_VMRecoveryInfoParam
{
      ////////// Filter Options ////////////////////////////
      char 		HostName[64];
      short     vmtype;
      long		lQueryFlag;
      char      reserved[258];       
}ASDB_VMRecoveryInfoParam, *PASDB_VMRecoveryInfoParam;


typedef struct _tagASDB_VMRecoveryInfoParamW
{
      ////////// Filter Options ////////////////////////////
      wchar_t 	HostName[64];
      short     vmtype;
      long	lQueryFlag;
      char      reserved[258];       
}ASDB_VMRecoveryInfoParamW, *PASDB_VMRecoveryInfoParamW;


#define MAX_AUTH_USER_NAME		256
#define MAX_AUTH_PASSWORD		256
typedef struct _tagASDB_VMRecoveryInfo
{
      ////////// astpses (SESSION) ////////////////////////////
      long  	sesid;
      long  	jobid;
      long  	tapeid;
      short 	status;
      short 	sestype;
      long  	sesnum;
      long  	starttime;
      long  	srchostid;
      long	srcpathid;         
      long	physicpathid;
	  unsigned long	vm_size;
      ////////// astape  (TAPE)    ////////////////////////////	      		
      char  	tapename[24];
      short 	randomid;
      short 	seqnum;
      char  	serialnum [32];
      ////////// asvmware ///////////////////////////////////// 		
      wchar_t   proxyName[VCB_MAX_HOST_NAME];				
      ////////// asvmtopology//////////////////////////////////
      char 		virtualMachine[512];
      wchar_t 	phost[VCB_MAX_HOST_NAME];        // Pysica host.
      wchar_t 	dataStore[128];					 // Newly Added in 10/1/2008
      short     vmtype;
      ///////// profile /////////////////////////
      char		username1[MAX_AUTH_USER_NAME+1]; // future use (User Profile)
      char  	password1[MAX_AUTH_PASSWORD+1];	 // future use (User Profile)
      char		username2[MAX_AUTH_USER_NAME+1];  // future use (User Profile)	
      char  	password2[MAX_AUTH_PASSWORD+1];	  // future use (User Profile)
      char      reserved[118];       // 122 --> 118
}ASDB_VMRecoveryInfo, *PASDB_VMRecoveryInfo;

typedef struct _tagASDB_VMRecoveryInfoW
{
      ////////// astpses (SESSION) ////////////////////////////
      long  	sesid;
      long  	jobid;
      long  	tapeid;
      short 	status;
      short 	sestype;
      long  	sesnum;
      long  	starttime;
      long  	srchostid;
      long	srcpathid;
      long	physicpathid;
	  unsigned long	vm_size;
      ////////// astape  (TAPE)    ////////////////////////////	      		
      wchar_t  	tapename[24];
      short 	randomid;
      short 	seqnum;
      wchar_t  	serialnum [32];
      ////////// asvmware ///////////////////////////////////// 		
      wchar_t   proxyName[VCB_MAX_HOST_NAME];				
      ////////// asvmtopology//////////////////////////////////
      wchar_t	virtualMachine[512];
      wchar_t 	phost[VCB_MAX_HOST_NAME];        // Physical host.
      wchar_t 	dataStore[128];					 // Newly Added in 10/1/2008
      short     vmtype;
      ///////// profile /////////////////////////
      char		username1[MAX_AUTH_USER_NAME+1]; // future use (User Profile)
      char  	password1[MAX_AUTH_PASSWORD+1];	 // future use (User Profile)
      char		username2[MAX_AUTH_USER_NAME+1];  // future use (User Profile)	
      char  	password2[MAX_AUTH_PASSWORD+1];	  // future use (User Profile)
      char      reserved[118];       // 122 --> 118
}ASDB_VMRecoveryInfoW, *PASDB_VMRecoveryInfoW;


// R12.v
#define ASDB_MASTER_SESSION_IDENTITY	0x0FFFFFFF
#define ASDB_ONLY_INTERNAL_SESNUM	100
#define ASDB_ONLY_MASTER_SESNUM		101


//#ifdef DEDUPE_SUPPORT
// _R12.x_(DeDupe) Code merge (6/25/2008)
// _R12.x_(DeDupe)... (4/7/2008)
typedef struct _tagASDB_SessCompressedParm_Parm
{
      char  tapename[24];	// IN
      long  randomID;		// IN
      long  tapeseqnum;		// IN
      char  serialnum[32];	// IN
      long  sessionNum;		// IN
      long  subSessionNum;	// IN default  0. (It is for VMWare in R12.v)
      char  reserved[128];
}ASDB_SessCompressedParm, *PASDB_SessCompressedParm;

typedef struct _tagASDB_SessCompressedParm_ParmW
{
      wchar_t  tapename[24];	// IN
      long	   randomID;		// IN
      long	   tapeseqnum;		// IN
      wchar_t  serialnum[32];	// IN
      long	   sessionNum;		// IN
      long	   subSessionNum;	// IN default  0. (It is for VMWare in R12.v)
      char     reserved[128];
}ASDB_SessCompressedParmW, *PASDB_SessCompressedParmW;

// _R12.x_(DeDupe)... (4/7/2008)
typedef struct _tagASDB_TapeCompressedParm_Parm
{
      char  tapename[24];	// IN
      long  randomID;		// IN
      long  tapeseqnum;		// IN
      char  serialnum[32];	// IN
      char  reserved[128];
}ASDB_TapeCompressedParm, *PASDB_TapeCompressedParm;

typedef struct _tagASDB_TapeCompressedParm_ParmW
{
      wchar_t  tapename[24];	// IN
      long	   randomID;		// IN
      long	   tapeseqnum;		// IN
      wchar_t  serialnum[32];	// IN
      char  reserved[128];
}ASDB_TapeCompressedParmW, *PASDB_TapeCompressedParmW;

// _R12.x_(DASHBOARD)(4/18/2008)
typedef struct _tagASDB_JobExRecV 
{
   long  id;		// Job ID
   long  throughputMBPerMin;		   
   char  makeupJobStatus;   // makeupjob or normaljob 	
   char  isMakeupJobConfig;  // IsMakeupJobConfigured   
   short jobexectype;	// bitwise (multiplexing / multistream/diskstaging?etc)
   char  compression;	// values will indicate if agent or server or software
   char  encryption;    // values will indicate if agent or server during backup 
			      // or server during migration or session password protected
   char  verifyAfterbackup; //values will indicate if verify or scan or compare after 
				    //backup is enabled
   char  alertConfig;
   char  prepost;		// values will indicate if none is enabled or pre is enabled 
				// or post is enabled or both
   char  virusScan;        // values will indicate if its enabled or not, and if its 
				// enabled what to do
   char  filter; 		// values will indicate if not enabled, or include filter or 
				// exclude filter or both enabled.
   char  reserved[257];	// future use //257 = 1 + 256 (4 byte alignment):Total
}ASDB_JobExRecV, * PASDB_JobExRecV;

// _R12.x_(DASHBOARD)(4/21/2008)
typedef struct _tagASDB_JobExRecV2 
{
   long  id;
   short type;
   short status;
   long  starttime;
   long  endtime;
   char  owner[32];
   char  comment[96];
   long  handle;
   char  host[16];
   char  setname[12];            //SetName[12];      // Up to 8 characters   
   long  jobno;
   long  throughputMBPerMin;		   
   char  makeupJobStatus;  // makeupjob or normaljob 	
   char  isMakeupJobConfig;  // IsMakeupJobConfigured   
   short jobexectype;	// bitwise (multiplexing / multistream/diskstaging?etc)
   char  compression;	// values will indicate if agent or server or software
   char  encryption;    // values will indicate if agent or server during backup 
			      // or server during migration or session password protected
   char  verifyAfterbackup; //values will indicate if verify or scan or compare after 
				    //backup is enabled
   char  alertConfig;
   char  prepost;		// values will indicate if none is enabled or pre is enabled 
				// or post is enabled or both
   char  virusScan;        // values will indicate if its enabled or not, and if its 
				// enabled what to do
   char  filter; 		// values will indicate if not enabled, or include filter or 
				// exclude filter or both enabled.
   char  reserved[257];
}ASDB_JobExRecV2, * PASDB_JobExRecV2;

typedef struct _tagASDB_JobExRecV2W 
{
   long  id;
   short type;
   short status;
   long  starttime;
   long  endtime;
   wchar_t  owner[32];
   wchar_t  comment[96];
   long  handle;
   wchar_t  host[16];
   wchar_t  setname[12];            //SetName[12];      // Up to 8 characters   
   long  jobno;
   long  throughputMBPerMin;		   
   char  makeupJobStatus;  // makeupjob or normaljob 	
   char  isMakeupJobConfig;  // IsMakeupJobConfigured   
   short jobexectype;	// bitwise (multiplexing / multistream/diskstaging?etc)
   char  compression;	// values will indicate if agent or server or software
   char  encryption;    // values will indicate if agent or server during backup 
			      // or server during migration or session password protected
   char  verifyAfterbackup; //values will indicate if verify or scan or compare after 
				    //backup is enabled
   char  alertConfig;
   char  prepost;		// values will indicate if none is enabled or pre is enabled 
				// or post is enabled or both
   char  virusScan;        // values will indicate if its enabled or not, and if its 
				// enabled what to do
   char  filter; 		// values will indicate if not enabled, or include filter or 
				// exclude filter or both enabled.
   char  reserved[257];
}ASDB_JobExRecV2W, * PASDB_JobExRecV2W;

// R12.v (6/17/2008)
#define ASDB_JOBINFO_EXCLUSIVE		100
typedef struct _tagASDB_JobInfoParm 
{
   long  commonJID;
   long  JobNo;
   long  masterjobID;
   short sFlag;			// ASDB_JOBINFO_EXCLUSIVE (Exclusive current(Input)mastorJobID)
   char  reserved[122]; // 128->122 // 10/14/2008
}ASDB_JobInfoParm, * PASDB_JobInfoParm;

//#endif //DEDUPE_SUPPORT

// R12.v (DASHBOARD) 6/3/2008

typedef struct _tagASDB_SessRecExt2 
{
   long  id;
   long  jobid;
   long  tapeid;
   short status;
   short sestype;
   long  sesnum;
   short sesmethod;
   long  sesflags;
   long  qfablocknum;
   long  starttime;
   long  endtime;
   long  totalkb;
   long  totalfiles;
   long  totalmissed;
   long  srchostid;
   long  srcpathid;
   long  ownerid;
   short streamnum;
   short tapeseq_end;
   short fsname_length;
   long	 throughputMBPerMin;
   long  tapeDrvID;
   char  HostName[64];
   long  subnum;
   unsigned long  compressedSizeKB;
   unsigned long  sizeOnTapeKB;
   char  encr_loc;
   char  encr_type;
   char  store_pw;
   char	 reserved[117];
}ASDB_SessRecExt2, *PASDB_SessRecExt2;

typedef struct _tagASDB_SessRecExt2W 
{
   long  id;
   long  jobid;
   long  tapeid;
   short status;
   short sestype;
   long  sesnum;
   short sesmethod;
   long  sesflags;
   long  qfablocknum;
   long  starttime;
   long  endtime;
   long  totalkb;
   long  totalfiles;
   long  totalmissed;
   long  srchostid;
   long  srcpathid;
   long  ownerid;
   short streamnum;
   short tapeseq_end;
   short fsname_length;
   long	 throughputMBPerMin;
   long  tapeDrvID;
   wchar_t  HostName[64];
   long  subnum;
   unsigned long  compressedSizeKB;
   unsigned long  sizeOnTapeKB;
   char  encr_loc;
   char  encr_type;
   char  store_pw;
   char	 reserved[117];
}ASDB_SessRecExt2W, *PASDB_SessRecExt2W;


#ifdef BAB_DBAPI_JOBHISTORY
#define ASDB_QUERY_JOBHISTORY_BYCOMMONJOBID 0
#define ASDB_QUERY_JOBHISTORY_BYNAME        1

#define ASDB_JOB_COMMENT_LEN				96
typedef struct _tagASDB_JobExecutionHistory
{
	char szComment[ASDB_JOB_COMMENT_LEN+1];
	long lStartTime;
	long lEndTime;
	int  iStatus;
	__int64 lTotalKb;  //backup/restore size //20090206
	__int64 lTotalFiles;      //backup/restore file number //20090206
	__int64 lTotalMissed; //20090206
	__int64 iThroughputKbPerSec; //20090206
	long lTimeUsed;
	int  iJobNo;
	int  iJobId;
	__int64 sumSizeOnTapeKB;	 //20081226 sonle01
	__int64 sumCompressedSizeKB; //20081226 sonle01
} ASDB_JobExecutionHistory, *PASDB_JobExecutionHistory;

typedef struct _tagASDB_JobExecutionHistoryW // dayra01
{
	wchar_t szComment[ASDB_JOB_COMMENT_LEN+1];
	long lStartTime;
	long lEndTime;
	int  iStatus;
	__int64 lTotalKb;  //backup/restore size //20090206
	__int64 lTotalFiles;      //backup/restore file number //20090206
	__int64 lTotalMissed;//20090206
	__int64 iThroughputKbPerSec;//20090206
	long lTimeUsed;
	int  iJobNo;
	int  iJobId;
	__int64 sumSizeOnTapeKB;	 //20081226 sonle01
	__int64 sumCompressedSizeKB; //20081226 sonle01
} ASDB_JobExecutionHistoryW, *PASDB_JobExecutionHistoryW;

// _R12.x_(DASHBOARD)(5/13/2008)
typedef struct _tagASDB_JobExecutionHistory2
{
	char szComment[ASDB_JOB_COMMENT_LEN+1];
	long lStartTime;
	long lEndTime;
	int  iStatus;
	long lTotalKb;  	//backup/restore size
	long lTotalFiles;       //backup/restore file number
	long lTotalMissed;
	int  iThroughputKbPerSec;
	long lTimeUsed;
	int  iJobNo;
	int  iJobId;
	unsigned long ulCompressSizeMB;
	char reserved[127];	// future use....
} ASDB_JobExecutionHistory2, *PASDB_JobExecutionHistory2;

typedef struct _tagASDB_JobExecutionHistory2W
{
	wchar_t szComment[ASDB_JOB_COMMENT_LEN+1];
	long lStartTime;
	long lEndTime;
	int  iStatus;
	long lTotalKb;  	//backup/restore size
	long lTotalFiles;       //backup/restore file number
	long lTotalMissed;
	int  iThroughputKbPerSec;
	long lTimeUsed;
	int  iJobNo;
	int  iJobId;
	unsigned long ulCompressSizeMB;
	char reserved[127];	// future use....
} ASDB_JobExecutionHistory2W, *PASDB_JobExecutionHistory2W;


#define ASDB_QUERY_MEDIAINFO_BYJOBID    0x01
#define ASDB_QUERY_MEDIAINFO_BYHOSTNAME 0x02
#define ASDB_QUERY_MEDIAINFO_BYSESSID	0x04
#define ASDB_QUERY_MEDIAINFO_BYHOSTNAMEDATE 0x08

#define TAPE_NAME_LEN 24
#define TAPE_SERIALNUM_LEN 32
#define TPDRV_SERIALNO_LEN 32

typedef struct _tagASDB_MediaInfo
{
	wchar_t szName[TAPE_NAME_LEN+1];
	wchar_t szSerial[TAPE_SERIALNUM_LEN+1];
	short   shSeqNum;
} ASDB_MediaInfo, *PASDB_MediaInfo;

#define ASDB_AGENTNAME_LEN	64
typedef struct _tagASDB_AgentDetailInfo
{
	char    szName[ASDB_AGENTNAME_LEN+1];
	long	lStartTime;
	long	lEndTime;
	int     iStatus;
	long    lTotalKB;         //backup/restore size
	long    lTotalFiles;      //backup/restore file number
	long    lTotalMissed;
	__int64	sumSizeOnTapeKB;	//20081226 sonle01
	__int64 sumCompressedSizeKB;//20081226 sonle01
} ASDB_AgentDetailInfo, *PASDB_AgentDetailInfo;

typedef struct _tagASDB_AgentDetailInfoW
{
	wchar_t    szName[ASDB_AGENTNAME_LEN+1];
	long	lStartTime;
	long	lEndTime;
	int     iStatus;
	long    lTotalKB;         //backup/restore size
	long    lTotalFiles;      //backup/restore file number
	long    lTotalMissed;
	__int64	sumSizeOnTapeKB;	//20081226 sonle01
	__int64 sumCompressedSizeKB;//20081226 sonle01
} ASDB_AgentDetailInfoW, *PASDB_AgentDetailInfoW;
#define DBAPI_JOBHISTORY_AGENT_STATUS_FINISH		0x01
#define DBAPI_JOBHISTORY_AGENT_STATUS_INCOMPLETE	0x02
#define DBAPI_JOBHISTORY_AGENT_STATUS_FAILED		0x04
#define DBAPI_JOBHISTORY_AGENT_STATUS_CANCELED		0x08
#define DBAPI_JOBHISTORY_AGENT_STATUS_ACTIVE		0x10
#define DBAPI_JOBHISTORY_AGENT_STATUS_NOTATTEMPTED  0x20
#define DBAPI_JOBHISTORY_AGENT_STATUS_ALL			0x3F

typedef struct _tagASDB_Agent
{
	char	szName[ASDB_AGENTNAME_LEN+1];
	int	iStatus;
	int     iJobNumber;
	int     iSuccessCount; //status value is 1
	int     iFailCount;    //status value is 3
	int     iCancel;       //status value is 2
	int     iActive;        // status value is 0
	int     iIncomplete;   //status value is 4
	int     iNotAttempted; //status value is 8
	char    reserved[23];  // future use and 8 byte alignment.
} ASDB_Agent, *PASDB_Agent;
typedef struct _tagASDB_AgentW
{
	wchar_t	szName[ASDB_AGENTNAME_LEN+1];
	int		iStatus;
	int     iJobNumber;
	int     iSuccessCount; //status value is 1
	int     iFailCount;    //status value is 3
	int     iCancel;       //status value is 2
	int     iActive;        // status value is 0
	int     iIncomplete;   //status value is 4
	int     iNotAttempted; //status value is 8
	char    reserved[23];  // future use and 8 byte alignment.
} ASDB_AgentW, *PASDB_AgentW;

typedef struct _tagASDB_JobStatisticsF
{
	int     iActive;        // status value is 0
	int	iFinish;        // status value is 1
	int	iCancel;        // status value is 2
	int     iFail;          // status value is 3
	int     iIncomplete;    // status value is 4
	int     iIdle;          // status value is 5
	int     iWaiting;       // status value is 6
	int     iNotAttempFail; // status value is 7
	int	iLastJobStatus;
	char	reserved[12];	// future use and 8 byte alignment.
} ASDB_JobStatistics, *PASDB_JobStatistics;

typedef struct _tagASDB_AgentStatistics
{
	int	iActive;	// status value is 0
	int	iFinish;        // status value is 1
	int	iCancel;        // status value is 2
	int     iFail;          // status value is 3
	int	iIncomplete;	// status value is 4
	int     iUnknown;       // status value is above 5
	int     iNotAttempFail; // status value is 7
	char    reserved[4];	// For future use and 8 byte alignment.
} ASDB_AgentStatistics, *PASDB_AgentStatistics;

//??
typedef struct _tagASDB_SessionStatistics
{
	int	iActive;	// status value is 0
	int	iFinish;        // status value is 1
	int	iCancel;        // status value is 2
	int     iFail;          // status value is 3
	int	iIncomplete;	// status value is 4
	int     iUnknown;       // status value is above 5
} ASDB_SessionStatistics, *PASDB_SessionStatistics;

typedef struct _tagASDB_MigrationStatistics
{
	int     iIncomplete;    // value is 0
	int     iComplete;      // value is 1
	int     iNotRequired;   // value is 2
	int     iFail;          // value is 3
	int     iInFuture;      // value is 4
	int     iStaging;		// value is 110, we will filter it in most cases.
} ASDB_MigrationStatistics, *PASDB_MigrationStatistics;
#endif

typedef struct _tagASDB_TotalJobsInfo
{
	long startTime;
	long totalKB;
	long totalFiles;
} ASDB_TotalJobsInfo, *PASDB_TotalJobsInfo;

/////// (2/6/2007) : UpdateASDBBackupsLog  /// 
typedef struct _tagASDB_BackupLogRec
{
	int iCreationMethod; 		// 1 = New, 2 = Copy or Migration
	char szMemberServer[80];	// Member Server Name where backup was written to tape
	char szStartTime[20]; 		// string in yyyy-mm-dd HH:MM:SS format
	char GUID[16]; 			// 16-byte binary GUID
	char szTapeName[24]; 		// Tape Name 
	long dwTapeID;			// Random/Unique Tape ID
	char szTapeSerial[32]; 		// string containing the Tape Serial Number
	int  iSessNo; 			// Session Number on Tape
	int  iSessMethod; 		// Session Method
	char szSessPath[512];  		// string containing the Session Path
	char PreReqGUID[16]; 		// 16-byte binary GUID of Pre-Requisite Session
	char reserved[516];		

} ASDB_BackupLogRec, *PASDB_BackupLogRec;

static char szDefaultPoolName[]="DEFAULT";

#define ASDB_CAT_DEFAULT				1
#define ASDB_CAT_ORIGINAL_PATH			2
#define ASDB_CAT_CATALOG_DB_PATH		3

#ifdef ARC_ROLE_MANAGEMENT

#define MAX_AUTH_USER_NAME		256
#define MAX_AUTH_ROLE_NAME      256
#define MAX_AUTH_PASSWORD		256
#define MAX_AUTH_DESCRIPTION	256
#define MAX_AUTH_CHECK_FIELD_LENGTH	256

#ifdef USERPROFILE_UNICODE_IMPL // 20080818 this MACRO will be removed after development finished

typedef struct {
  int		id; // unique in table, used internally
  int		topologyid; 
  wchar_t	username[MAX_AUTH_USER_NAME+1];
  int		usertype;
  char		password[MAX_AUTH_PASSWORD+1]; //it's byte-data so don't need unicode
  int		len_password;
  int		status;
  char		check[MAX_AUTH_CHECK_FIELD_LENGTH]; // check field
  wchar_t	description[MAX_AUTH_DESCRIPTION+1];
  char		reserved[128];
}ASDB_AuthUserRec, *PASDB_AuthUserRec;

typedef struct {
  int		id; // unique in table, used internally
  int		type;	//isCustom
  wchar_t   rolename[MAX_AUTH_ROLE_NAME+1];
  char		permbits[256]; // permission bits 
  char		check[MAX_AUTH_CHECK_FIELD_LENGTH]; // check field
  wchar_t   description[MAX_AUTH_DESCRIPTION+1];
  char		reserved[128];
}ASDB_AuthRoleRec, *PASDB_AuthRoleRec;

#else //USERPROFILE_UNICODE_IMPL

typedef struct {
  int	id; // unique in table, used internally
  int	topologyid; 
  char	username[MAX_AUTH_USER_NAME+1];
  int	usertype;
  char  password[MAX_AUTH_PASSWORD+1];
  int   len_password;
  int	status;
  char	check[MAX_AUTH_CHECK_FIELD_LENGTH]; // check field
  char	description[MAX_AUTH_DESCRIPTION+1];
  char  reserved[128];
}ASDB_AuthUserRec, *PASDB_AuthUserRec;

typedef struct {
  int	id; // unique in table, used internally
  int	topologyid; 
  wchar_t	username[MAX_AUTH_USER_NAME+1];
  int	usertype;
  char  password[MAX_AUTH_PASSWORD+1];
  int   len_password;
  int	status;
  wchar_t	check[MAX_AUTH_CHECK_FIELD_LENGTH]; // check field
  wchar_t	description[MAX_AUTH_DESCRIPTION+1];
  char  reserved[131];
}ASDB_AuthUserRecW, *PASDB_AuthUserRecW;

typedef struct {
  int id; // unique in table, used internally
  int type;	//isCustom
  char      rolename[MAX_AUTH_ROLE_NAME+1];
  char		permbits[256]; // permission bits 
//  int		permbits; // permission bits 
  char	check[MAX_AUTH_CHECK_FIELD_LENGTH]; // check field
  char      description[MAX_AUTH_DESCRIPTION+1];
  char  reserved[128];
}ASDB_AuthRoleRec, *PASDB_AuthRoleRec;

#endif //USERPROFILE_UNICODE_IMPL


typedef struct {
	int ID;
	wchar_t Name[256];
	wchar_t  Description[1024];
	int ActionType;
	int ScheduleType;
	wchar_t ScheduleParam[1024];
	long ActionTime;
	long RepeatFrom;
	int RepeatType;
	wchar_t RepeatParam[1024];
	long LastActionTime;
	int ActedTimes;
	int UserID;
	long CreatedAt;
	long LastModifiedAt;
	char  reserved[128];
}ASDB_DashboardScheduleRecW, *PASDB_DashboardScheduleRecW;

typedef struct {
	int ID;
	int ScheduleID;
	wchar_t SenderName[256];
	wchar_t FromAddress[256];
	wchar_t ToAddresses[1024];
	wchar_t CCAddresses[1024];
	int Priority;
	wchar_t MailSubject[1024];
	char MailComment[1024];
	char  reserved[128];
}ASDB_DashboardScheduleEmailRecW, *PASDB_DashboardScheduleEmailRecW;

typedef struct {
	int ID;
	int EmailID;
	int ReportID;
	wchar_t ReportParam[1024];
	char  reserved[128];
}ASDB_DashboardEmailReportRecW, *PASDB_DashboardEmailReportRecW;

typedef struct {
	int ID;
	int UserID;
	int Level;
	wchar_t Source[256];
	long Time;
	char Message[1024];
	char  reserved[128];
}ASDB_DashboardLogMessageRecW, *PASDB_DashboardLogMessageRecW;

typedef struct {
	int roleID;
	int userID;
}ASDB_AuthRoleUserRecW, *PASDB_AuthRoleUserRecW;

#endif //ARC_ROLE_MANAGEMENT

//20081124_New_Dump_DB
#ifndef _ADMKHANDLE_DEF
#define _ADMKHANDLE_DEF
typedef struct _ADMK_KEY_CACHE_ 
{
	unsigned char	pEncKey[512];
	unsigned int	pEncKeyLen;
	int				bInitialized;
} ADMK_KEY_CACHE, *ADMKHANDLE;
#endif

#ifdef R12_5_MANAGE_PASSWORD
typedef struct _tagASDB_DumpPwdParm
{
	int		dumpNULL;	/* 0: dump non-null 1: dump all */
	long	startTime;
	long	endTime;
	char		PrimaryServer[64+1];
	ADMKHANDLE	ADMK;
} ASDB_DumpPwdParm, *PASDB_DumpPwdParm;

#define		MAX_SESSION_GUID_LENGTH		16
#define		MAX_SESSION_PASSWORD_LENGTH	256

typedef struct _tagASDB_DumpPwdRec
{
	long	startTime;
	long	endTime;
	char	sessGUID[MAX_SESSION_GUID_LENGTH + 1];
	int		sessPwdLen;
	char	sessPwd[MAX_SESSION_PASSWORD_LENGTH + 1];
} ASDB_DumpPwdRec, *PASDB_DumpPwdRec;
#endif	/* R12_5_MANAGE_PASSWORD */

#ifdef R12_5_MANAGE_PASSWORD
#define	MAX_EM_GETDUMPSESSPWD			100
#endif
// Add by Gil
// 4/9/2008 12:26:03 PM
// Support for Rich Agent Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagNodeInformation
{
	int		ID;
	wchar_t NodeName[255];	// the node name of a agent machine
	int		Virtualization;	// idicate whether the machine is a virtualization
	int   rhostID;   // Reference to ashost.rhostid
	char reserve[5]; // reserve and structure layout
}ASDB_NodeInformation, *PASDB_NodeInformation;
#endif //RICH_AGENT_INFORMATION
// End add by Gil


// Add by Gil
// 4/16/2008 10:18:13 AM
// Support for Rich Agent Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagOSInformation
{
	int		ID;
	int		NodeID;
	int		BuildNumber;
	wchar_t	BuildType[128]; // 512 -> 128 [7/15/2008 lijbi02]
	wchar_t	Caption[512];
	wchar_t	CountryCode[128];// 512 -> 128 [7/15/2008 lijbi02]
	wchar_t CSDVersion[128]; // 512 -> 128 [7/15/2008 lijbi02]
	wchar_t CSName[256]; // 512 -> 256 [7/15/2008 lijbi02]
	wchar_t Manufacturer[512];
	wchar_t OEMInfo[512]; // June/23/2008 18:30 Libin Add.
	int		OSLanguage;
	int		ProductType;
	wchar_t	SerialNumber[128];
	int		ServicePackMajorVersion;
	int		ServicePackMinorVersion;
	wchar_t	SystemDevice[128]; // 512 -> 128 [7/15/2008 lijbi02]
	wchar_t	SystemDirectory[256]; // 512 -> 256 [7/15/2008 lijbi02]
	wchar_t SystemDrive[50];
	wchar_t Version[50];
	wchar_t WindowsDirectory[512];
	wchar_t RawData[2048];
}ASDB_OSInformation, *PASDB_OSInformation;
#endif //RICH_AGENT_INFORMATION
// End add by Gil


// Add by Gil
// 4/17/2008 4:16:22 PM
// Support for Rich Agent Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagMemoryInformation
{
	int		ID;
	int		NodeID;
	__int64 Capacity;
	int		DataWidth;
	wchar_t DeviceLocator[512];
	int		FormFactor;
	wchar_t Manufacturer[512]; // June/23/2008 12:11 Libin add.
	int		MemoryType;
	int     Speed;             // June/23/2008 12:11 Libin Modify : w_char[512] to int.
	wchar_t Tag[512];
	int		TotalWidth;
	int		TypeDetail;
	wchar_t Name[512];
	wchar_t RawData[2048];
}ASDB_MemoryInformation, *PASDB_MemoryInformation;
#endif //RICH_AGENT_INFORMATION
// End add by Gil


// Add by Gil
// 5/5/2008 10:56:23 AM
// Support for Rich Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagCPUInformation
{
	int		ID;
	int		NodeID;
	int		AddressWidth;
	int		Architecture;
	int		Availability;
	int		DataWidth;
	wchar_t DeviceID[512];
	int		Family;
	int     L2CacheSize; // June/23/2008 10:11 Libin add.
	wchar_t Manufacturer[512];
	int		MaxClockSpeed;
	wchar_t Name[512];
	wchar_t ProcessorId[50];
	int ProcessorType	;	
	wchar_t RawData[2048];
}ASDB_CPUInformation, *PASDB_CPUInformation;
#endif // RICH_AGENT_INFORMATION
// End add by Gil



// Add by Gil
// 5/5/2008 4:54:13 PM
// Support for Rich Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagNICInformation
{
	int		ID;
	int		NodeID;
	wchar_t AdapterType[512];
	wchar_t MACAddress[50];
	wchar_t Manufacturer[512];
	wchar_t Name[512];
	__int64	Speed;
	int		MTU;
	wchar_t RawData[2048];
}ASDB_NICInformation, *PASDB_NICInformation;
#endif // RICH_AGENT_INFORMATION
// End add by Gil

// Add [8/5/2008 lijbi02]
// Support for Rich Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagIPInformation 
{
    int     NodeID;
    wchar_t MACAddress[50];
    wchar_t IP[128];
    wchar_t Subnet[128];
    wchar_t SubnetMask[16];
    wchar_t DefGateway[128];
    wchar_t DNSDomain[128];
    wchar_t DNSHostName[128];
    wchar_t DHCPServer[128];
    int     PrefixLen;
    wchar_t RawData[2048];

}ASDB_IPInformation, *PASDB_IPInformation;
#endif

// Add [8/6/2008 lijbi02]
// Support for Rich Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagFibercardInformation
{
    int      NodeID;
    int      Availability;
    wchar_t  Caption[128];
    wchar_t  Description[255];
    wchar_t  DriverName[128];
    wchar_t  Name[128];
    wchar_t  Manufacturer[128];
    int      ProtocolSupported;
    wchar_t  HardwareVersion[64];
    wchar_t  DriverVersion[64];
    int      MaxDataWidth;
    int      MaxNumberControlled;
    __int64  MaxTransferRate;
    wchar_t  RawData[2048];
}ASDB_FibercardInformation, *PASDB_FibercardInformation;
#endif

// Add by Gil
// 5/8/2008 5:31:53 PM
// Support for Rich Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagDiskInformation
{
	int		ID;
	int		NodeID;
	wchar_t Caption[512];
	wchar_t Description[512];
	wchar_t DeviceID[128]; // 512 -> 128 [7/15/2008 lijbi02]
	__int64 DiskUsedSpace; // June/23/2008 17:30 Libin Add.
	int     DiskThroughput; // June/23/2008 17:40 Libin Add.
	wchar_t InterfaceType[64]; // 512 -> 64 [7/15/2008 lijbi02]
	wchar_t Manufacturer[512];
	wchar_t MediaType[128]; // 512 -> 128 [7/15/2008 lijbi02]
	wchar_t Model[512];
	wchar_t Name[256]; // 512 -> 128 [7/15/2008 lijbi02]
	short   Partitions;		// Change it due to RPC compiler failure. // __int16 --> short (2 BYTE)
	__int64 Size;
	__int64 TotalCylinders;
	__int64 TotalHeads;
	__int64 TotalSectors;
	__int64 TotalTracks;
	__int64 TracksPerCylinder;
	int		SCSIBus;
	int		SCSILogicalUnit;
	int		SCSIPort;
	int		SCSITargetId;
	wchar_t Signature[512];
	int		Index;
	int		DiskType;
	wchar_t RawData[2048];
}ASDB_DiskInformation, *PASDB_DiskInformation;
#endif // RICH_AGENT_INFORMATION
// End add by Gil



// Add by Gil
// 5/9/2008 1:50:37 PM
// Support for Rich Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagVolumeInformation
{
	int		ID;
	int		NodeID;
	__int64	BlockSize;
	wchar_t Caption[50];
	wchar_t Name[50];
	__int64 Capacity;
	int		Compressed;
	wchar_t DriveLetter[50];
	int		DriveType;
	wchar_t FileSystem[50];
	__int64 FreeSpace;
	int		FreeSpacePercent;
	wchar_t DeviceID[512];
	int VolumeType;
	wchar_t RawData[2048];
}ASDB_VolumeInformation, *PASDB_VolumeInformation;
#endif // RICH_AGENT_INFORMATION
// End add by Gil


// Add by Gil
// 5/9/2008 5:05:43 PM
// Support for Rich Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagPartitionInformation
{
	int ID;
	int NodeID;
	int DiskID;
	__int64 BlockSize;
	int BootPartition;
	wchar_t Name[512];
	__int64 NumberOfBlocks;
	int PrimaryPartition;
	__int64 Size;
	wchar_t Type[512];
	wchar_t RawData[2048];
	int DiskIndex;	
}ASDB_PartitionInformation, *PASDB_PartitionInformation;
#endif // RICH_AGENT_INFORMATION
// End add by Gil



// Add by Gil
// 5/12/2008 12:15:41 PM
// Support for Rich Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagLogicalDiskInformation
{
	int		ID;
	int		partitionID;
	int		volumeID;
	int		NodeID;
	wchar_t Caption[50];
	int		Compressed;
	wchar_t Description[512];
	wchar_t DeviceID[512];
	int		DriveType;
	wchar_t FileSystem[50];
	__int64 FreeSpace;
	int		MaximumComponentLength;
	int		MediaType;
	wchar_t Name[50];
	int		QuotasDisabled;
	int		QuotasIncomplete;
	int		QuotasRebuilding;
	__int64 Size;
	int		SupportsDiskQuotas;
	int		SupportsFileBasedCompression;
	wchar_t VolumeName[512];
	wchar_t RawData[2048];
	wchar_t PartitionName[512];
	wchar_t VolumeDeviceID[512];
}ASDB_LogicalDiskInformation, *PASDB_LogicalDiskInformation;
#endif // RICH_AGENT_INFORMATION
// End add by Gil

// Add by Gil
// 5/19/2008 4:35:44 PM
// Support for Rich Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagFragmentEvent
{
	int		ID;
	int		NodeID;
	__int64	AverageFileSize;
	float	AverageFragmentsPerFile;
	__int64	ClusterSize;
	__int64	ExcessFolderFragments;
	int		FilePercentFragmentation;
	__int64	FragmentedFolders;
	__int64	FreeSpace;
	int		FreeSpacePercent;
	int		FreeSpacePercentFragmentation;
	int		MFTPercentInUse;
	__int64	MFTRecordCount;
	__int64	PageFileSize;
	__int64 TotalExcessFragments;
	__int64 TotalFiles;
	__int64 TotalFolders;
	__int64 TotalFragmentedFiles;
	__int64 TotalMFTFragments;
	__int64 TotalMFTSize;
	__int64 TotalPageFileFragments;
	int		TotalPercentFragmentation;
	__int64 UsedSpace;
	__int64 VolumeSize;
	wchar_t VolumeDeviceID[512];
}ASDB_FragmentEvent, *PASDB_FragmentEvent;
#endif // RICH_AGENT_INFORMATION
// End add by Gil


// Add by BinLI {
// [7/14/2008 lijbi02]
// Support for Rich Information beyond backup
#ifdef RICH_AGENT_INFORMATION
typedef struct _tagAgentInfo
{
    int		ID;
    int		NodeID;
    wchar_t AgentName[128];
    wchar_t Path[260];
    int     MajorVersion;
    int     MinorVersion;
    int     BuildVersion;
    int     Revision;
    int     PortNumber;
    wchar_t RawData[3072];
}ASDB_AgentInfo, *PASDB_AgentInfo;

typedef struct _tagAgentBinaryInfo
{
    int     ID;
    int     AgentID;
    wchar_t Name[128];
    wchar_t RelativePath[260];
    int     MajorVersion;
    int     MinorVersion;
    int     BuildVersion;
    int     Revision;
    wchar_t Architecture[32];
    wchar_t Company[128];
    wchar_t RawData[1024];
}ASDB_AgentBinaryInfo, *PASDB_AgentBinaryInfo;
#endif 
// }

// Add {
// [12/17/2008 lijbi02]
// Support for Dashboard component license report
#ifdef RICH_AGENT_INFORMATION
//typedef struct _tagComponentMachineInfo
//{
//    int   componentId;
//    wchar_t machineName[64];
//    short  installed;        // The flag of component is applied with license 
//}ASDB_ComponentMachineInfo, *PASDB_ComponentMachineInfo;

typedef struct _tagComponentLicenseInfo
{
    int topologyId;
    int componentId;
    int majorVersion;
    int minorVersion;
    int licensedCount;
    int usageCount;
}ASDB_ComponentLicenseInfo, *PASDB_ComponentLicenseInfo;
#endif
// }

typedef struct _tagHostInfo
{
	int			rhostid;
	wchar_t 	rhostname[64];
	wchar_t		ipaddress[64];		//This will have ip address. This could be 0.0.0.0
	wchar_t		domainName[256];	//Domain name
	wchar_t		username[256];		//User name 
	wchar_t		password[256];		//passowrd
	int 		MajorVersion;		//We don't need an array because all the agents MUST be of the same version.
	int 		MinorVersion;		//We don't need an array because all the agents MUST be of the same version.
	int			ServicePack;
	int			BuildNumber;
	char		reserved[128];		//reserved fields for future expansion.
}ASDB_HostInfo, *PASDB_HostInfo;

//20081219 sonle01
typedef struct _tagVMNodeInfo
{
	wchar_t 	vmname[64];
	int			ostype;				//543 vmware,    547 hyper-v node
	long		MajorVersion;
	long		MinorVersion;
	long		ServicePack;
	long		BuildNumber;
	char		reserved[128];		//reserved fields for future expansion.
}ASDB_VMNodeInfo, *PASDB_VMNodeInfo;

#ifdef R14_BLI_DB //R14_E14 (2/12/2009)
#define		EX_MAX_HOST_NAME	256

// lFlag (Getting option)
#define		ASDB_EX_GET_ALL			100
#define		ASDB_EX_GET_BY_USER_SERVER_DBNAME 104	// for mailbox table
#define		ASDB_EX_GET_BY_SERVER_DBNAME 105		// for mailbox table

#define		ASDB_EX_DEL_ALL		110

typedef struct _tagASDB_EXADServerInfo_Parm
{
	wchar_t	ADServerName[EX_MAX_HOST_NAME];
	wchar_t	ipaddress[64];
	long		lFlag;
	char		reserved[128];	
}ASDB_EXADServerInfoParm, *PASDB_EXADServerInfoParm;

typedef struct _tagASDB_EXADServerInfo
{
	wchar_t	ADServerName[EX_MAX_HOST_NAME];
	wchar_t	ipaddress[64];
	wchar_t   domain[EX_MAX_HOST_NAME];
	wchar_t   userName[256];
	wchar_t   password[256];
	char      reserved[128];	
}ASDB_EXADServerInfo, *PASDB_EXADServerInfo;

typedef struct _tagASDB_EXCHANGEInfo_Parm
{
	wchar_t	ServerName[EX_MAX_HOST_NAME];
	wchar_t	ipaddress[64];
	wchar_t   domain[EX_MAX_HOST_NAME];
	long		lFlag;
	char		reserved[128];	
}ASDB_EXCHANGEInfoParm, *PASDB_EXCHANGEInfoParm;

typedef struct _tagASDB_EXCHANGEInfo
{
	wchar_t	ServerName[EX_MAX_HOST_NAME];
	wchar_t	ipaddress[64];
	wchar_t	domain[EX_MAX_HOST_NAME];
	short		TypeFlag;
	wchar_t	DAGName[EX_MAX_HOST_NAME];
	char      reserved[130];	
}ASDB_EXCHANGEInfo, *PASDB_EXCHANGEInfo;

typedef struct _tagASDB_MailBoxInfo_Parm
{
	long	    DBIndex;
	wchar_t	ExUser[64];
	long		ExDate;
	wchar_t	DBName[128];
	wchar_t	ServerName[EX_MAX_HOST_NAME];
	long      jobid;
	short     jobtype;
	long		lFlag;
	char		reserved[128];	
}ASDB_MailBoxInfoParm, *PASDB_MailBoxInfoParm;

typedef struct _tagASDB_MailBoxInfo
{
	long	    DBIndex;
	wchar_t	ExUser[64];
	long		ExDate;
	wchar_t	DBName[128];
	wchar_t	ServerName[EX_MAX_HOST_NAME];
	long      jobid;
	short     jobtype;
	char		reserved[128];
}ASDB_MailBoxInfo, *PASDB_MailBoxInfo;

#endif //R14_BLI_DB

//zhezh03 20090610
//support DB bulk enumeration
typedef struct _tagASDB_TsiEnumTapeInfo
{
	CHAR	szTapeName[24];
	LONG	uRandomID;
	LONG	uSeq;
	LONG	uTapeFilags;
	CHAR	szSerialNum[32];
	CHAR	UUID[24];
	LONG	RaidID;
}ASDB_TsiEnumTapeInfo, *PASDB_TsiEnumTapeInfo;

typedef struct _tagASDB_TapeSetInfo  
{
	UCHAR	ucSetType;
	BOOL	bOverWrite;
	CHAR	reserve[2];
}ASDB_TapeSetInfo, *PASDB_TapeSetInfo;

//shada07 2008-11-17
//Define the global options for DBAPIs
//Now, we only define time out for each DBAPI
typedef enum _tagGlobalOptions
{
    ASDB_GLOBAL_OPTIONS_TIME_OUT = 0
} ASDB_GlobalOptions, *pASDB_GlobalOptions;
#pragma pack()

#include <asdbbase.h>

#include <asdbmmotbl.h>

#ifdef __cplusplus
  }
#endif /* __cplusplus */

#endif    /* ASDB_H */
