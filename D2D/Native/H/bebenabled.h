/*
|| ------------------------------------------------------------------------------- ||
||                    Proprietary and Confidential Information                     ||
||                                                                                 ||
||       Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.   ||
||All rights reserved.  Any third party trademarks or copyrights are the property  ||
||of their respective owners.                                                      ||
|| --------------------------------------------------------------------------------||
*/


/**************************************************************************************
*
*  File:    bebenabled.h
*  Content: Fixes and features which are enabled for A particular build
*
*
**************************************************************************************/


/**************************************************************************************
**                             Change History                                        **
*                                                                                     *
*       2   <id>     <date>    BEB_<STAR ID>  Add description                         *
*       3   <id>     <date>    BEB_<FEATURE>  Add description                         *
*            ....................                                                     *
*            ....................                                                     *
***************************************************************************************
*                                                                                     *
*  Ver#     User     Date            Description                                      *
*  -------  -------  --------  -------------------------------------------------------*
*       1   venkr01  02/14/02  Created the bebenabled file                            *
**************************************************************************************/


/**************************************************************************************
**                             FIX ENABLE SECTION                                    **
**************************************************************************************/
// kimwo01 (10/27/2008) For Migration Retries.  
//#ifndef MIGRATION_RETRIES
//#define MIGRATION_RETRIES
//#endif
#ifndef _BEBENABLED_H_
#define _BEBENABLED_H_

#ifndef DB_STAGING_GROUP
#define DB_STAGING_GROUP
#endif

#ifndef R12_5_JOB_OWNER
#define R12_5_JOB_OWNER
#endif

#ifndef NEW_AGENT_GLOBAL_OPTION
#define NEW_AGENT_GLOBAL_OPTION
#endif

#ifndef ORACLE_FILE_BASE
#define ORACLE_FILE_BASE
#endif

#ifndef GUI_ORACLE_FILE_BASE
#define GUI_ORACLE_FILE_BASE
#endif

#ifndef maoja01_ORACLE_RMAN
#define maoja01_ORACLE_RMAN
#endif

#ifndef GUI_UNICODE_SUPPORT
#define GUI_UNICODE_SUPPORT
#ifdef MAINFUNC
static char* __GUI_UNICODE_SUPPORT = "@(#)  GUI_UNICODE_SUPPORT enabled  \n";
#endif
#endif

#ifndef ASDB_SIMPLE_RECOVERY
#define ASDB_SIMPLE_RECOVERY
#ifdef MAINFUNC
	static char* __ASDB_SIMPLE_RECOVERY = "@(#)  ASDB_SIMPLE_RECOVERY enabled  \n";
#endif
#endif


#ifndef BEB_DO_JOBSCRIPT_UPGRADE
#define BEB_DO_JOBSCRIPT_UPGRADE
#ifdef MAINFUNC
	static char* __BEB_DO_JOBSCRIPT_UPGRADE = "@(#)  BEB_DO_JOBSCRIPT_UPGRADE enabled  \n";
#endif
#endif


#ifndef WGUI_IMP_BUFFER//Using this macro to buffer some content in GUI memory, improve the performance.
#define WGUI_IMP_BUFFER
#ifdef MAINFUNC
	static char* __WGUI_IMP_BUFFER = "@(#)  WGUI_IMP_BUFFER enabled  \n";
#endif
#endif

#ifndef WGUI_SANITY_CHECK
#define WGUI_SANITY_CHECK
#ifdef MAINFUNC
	static char* __WGUI_SANITY_CHECK = "@(#)  WGUI_SANITY_CHECK enabled  \n";
#endif
#endif

#ifndef FIX_15582883
#define FIX_15582883
#ifdef MAINFUNC
	static char* __FIX_15582883 = "@(#)  FIX_15582883 enabled  \n";
#endif
#endif

#ifndef SKIP_WRITERBACKUP
#define SKIP_WRITERBACKUP
#ifdef MAINFUNC
	static char* __SKIP_WRITERBACKUP = "@(#)  SKIP_WRITERBACKUP enabled  \n";
#endif
#endif

#ifndef SQLWRITER_SUPPORT
#define SQLWRITER_SUPPORT
#ifdef MAINFUNC
	static char* __SQLWRITER_SUPPORT = "@(#)  SQLWRITER_SUPPORT enabled  \n";
#endif
#endif

#ifndef ORACLEWRITER_SUPPORT
#define ORACLEWRITER_SUPPORT
#ifdef MAINFUNC
	static char* __ORACLEWRITER_SUPPORT = "@(#)  ORACLEWRITER_SUPPORT enabled  \n";
#endif
#endif

#ifndef ASCORE_SCALABILITY_ENHANCEMENT
#define ASCORE_SCALABILITY_ENHANCEMENT
#ifdef MAINFUNC
	static char* __ASCORE_SCALABILITY_ENHANCEMENT = "@(#)  ASCORE_SCALABILITY_ENHANCEMENT enabled  \n";
#endif
#endif


#ifndef ASCORE_SCALABILITY_ENHANCEMENT_INDEX
#define ASCORE_SCALABILITY_ENHANCEMENT_INDEX
#ifdef MAINFUNC
	static char* __ASCORE_SCALABILITY_ENHANCEMENT_INDEX = "@(#)  ASCORE_SCALABILITY_ENHANCEMENT_INDEX enabled  \n";
#endif
#endif

#ifndef WANSYNC_SUPPORT
#define WANSYNC_SUPPORT
#endif

#ifndef BAF_EXCLUDE_FILES
#define BAF_EXCLUDE_FILES
#ifdef MAINFUNC
static char* __BAF_EXCLUDE_FILES = "@(#)  BAF_EXCLUDE_FILES enabled  \n";
#endif
#endif

#ifndef NEW_JOB_HISTORY
#define NEW_JOB_HISTORY
#endif

#ifndef WGUI_15425132
#define WGUI_15425132
#endif

#ifndef WGUI_15399915
#define WGUI_15399915
#endif

#ifndef BAB_NEW_JOBMONITOR_V12
#define BAB_NEW_JOBMONITOR_V12
#ifdef MAINFUNC
	static char* __BAB_NEW_JOBMONITOR_V12 = "@(#)  BAB_NEW_JOBMONITOR_V12 enabled  \n";
#endif
#endif

// jaivi02: Fixing DiskEx Bug when Client Agent receives the structure
#ifndef FIX_DISKEX_BUG_1
#define FIX_DISKEX_BUG_1
#endif /* FIX_DISKEX_BUG_1 */

#ifndef BAB_SERVER_MIGRATE
#define BAB_SERVER_MIGRATE
#endif

// Add by gonch02

#ifndef DB_SANITY_CHECKS
#define DB_SANITY_CHECKS
#endif

// For reserving group for ca_scan/ca_merge runnow jobs.

#ifndef BAB_RESERVEGROUP_FOR_RUNJOBNOW
#define BAB_RESERVEGROUP_FOR_RUNJOBNOW
#endif

#ifndef BAB_SP2OR3_MERGE
#define BAB_SP2OR3_MERGE
#endif

// Add by Gil
// PMF=zhagi01
// 8/8/2006 2:06:01 PM
// use by UBrowser to read properties from SQL agent
#ifndef UA_READ_DIR_PROPERTY
#define UA_READ_DIR_PROPERTY
#endif // UA_READ_DIR_PROPERTY
// End Add By Gil

//kimwo01:: User defined catalog DB location.
#ifndef BAB_USER_CATALOGDB
#define BAB_USER_CATALOGDB
#ifdef MAINFUNC
	static char* __BAB_USER_CATALOGDB = "@(#)  BAB_USER_CATALOGDB enabled  \n";
#endif
#endif

//houji03:: Central Catalog
#ifndef BAB_116_CENTRALCATALOG
#define BAB_116_CENTRALCATALOG
#ifdef MAINFUNC
	static char* __BAB_116_CENTRALCATALOG = "@(#)  BAB_116_CENTRALCATALOG enabled  \n";
#endif
#endif

//houji03:: logger DB API for GUI
#ifndef BAB_LOGGER_GUISUPPORT
#define BAB_LOGGER_GUISUPPORT
#ifdef MAINFUNC
	static char* __BAB_LOGGER_GUISUPPORT = "@(#)  BAB_LOGGER_GUISUPPORT enabled  \n";
#endif
#endif

//tanji04: VSS Snap Support
#ifndef BAB_VSS_SNAP_SUPPORT
#define	BAB_VSS_SNAP_SUPPORT
#ifdef MAINFUNC
     static char * __BAB_VSS_SNAP_SUPPORT  = "@(#)  BAB_VSS_SNAP_SUPPORT enabled  \n" ;
#endif /* MAINFUNC */
#endif //BAB_VSS_SNAP_SUPPORT

//houji03:: Cross Platform Central DB Support
#ifndef BAB_CPDB_SUPPORT
#define BAB_CPDB_SUPPORT
#ifdef MAINFUNC
	static char* __BAB_CPDB_SUPPORT = "@(#)  BAB_CPDB_SUPPORT enabled  \n";
#endif
#endif

// Add By Gil
// PMF=zhagi01
// 7/6/2006 12:26:46 PM
// this for consistent of ASDDISKEX between UNIX and Windows
#ifndef BAB_ASDDISKEX_CONSISTENT
#define BAB_ASDDISKEX_CONSISTENT
#ifdef MAINFUNC
static char * ___ASDDISKEX_CONSISTENT = "@(#) _BAB_ASDDISKEX_CONSISTENT Enabled\n";
#endif // MAINFUNC
#endif // BAB_ASDDISKEX_CONSISTENT
// End Add By Gil

// Central logger
#ifndef CENTRAL_LOGGER_INTERFACE
#define CENTRAL_LOGGER_INTERFACE
#ifdef MAINFUNC
	static char* __CENTRAL_LOGGER_INTERFACE = "@(#)  CENTRAL_LOGGER_INTERFACE enabled  \n";
#endif
#endif

/*sonli02:: Enable central alert support*/
#ifndef CENTRAL_ALERT_SUPPORT
#define CENTRAL_ALERT_SUPPORT
#endif /*CENTRAL_ALERT_SUPPORT*/

//jaivi02:	Adding Protection for Denial of Service Attack (merged from 11.5 SP2)
#ifndef AGENT_DOS
#define AGENT_DOS
#ifdef MAINFUNC
	 static char * __AGENT_DOS = "@(#)  AGENT_DOS Enabled\n";
#endif /* MAINFUNC */
#endif
//chach07. Buffer Overflow fixes for 11.5 SP2 . MSRPC services Tapeeng, ASDB, Ascore, DBARPC
#ifndef MSRPC_BUFFER_OVERFLOW
#define MSRPC_BUFFER_OVERFLOW
#ifdef MAINFUNC
     static char * __MSRPC_BUFFER_OVERFLOW = "@(#)  MSRPC_BUFFER_OVERFLOW enabled  \n" ;
#endif /* MAINFUNC */
#endif // MSRPC_BUFFER_OVERFLOW



//jiaca01   Jun 20, 2006 minor Task change for session encryption flag.
#ifndef WDRO_TASK_ENCRYPTION_FLAG
#define WDRO_TASK_ENCRYPTION_FLAG
#ifdef MAINFUNC
	static char* __WDRO_TASK_ENCRYPTION_FLAG = "@(#)  WDRO_TASK_ENCRYPTION_FLAG enabled  \n";
#endif
#endif

// houji03, June 20,2006 TapeCopy Merge for qjobNo.
#ifndef BAB_ICBC_SUPPORT
#define BAB_ICBC_SUPPORT
#ifdef MAINFUNC
	static char* __BAB_ICBC_SUPPORT = "@(#)  BAB_ICBC_SUPPORT enabled  \n";
#endif
#endif

// houji03 for r11.5 SP3 Catalog DB
#ifndef BEB_CATALOG_DB
#define BEB_CATALOG_DB
#endif //BEB_CATALOG_DB

// houji03, June 16,2006 Get Migration Status on Ldbserver.
#ifndef BAB_MIGRATIONSTATUS
#define BAB_MIGRATIONSTATUS
#ifdef MAINFUNC
	static char* __BAB_MIGRATIONSTATUS = "@(#)  BAB_MIGRATIONSTATUS enabled  \n";
#endif
#endif

// wu$br01, June 8,2006 support Exchange server R12.
#ifndef E12_TASK
#define E12_TASK
#ifdef MAINFUNC
	static char* __E12_AGENT = "@(#)  E12_TASK enabled  \n";
#endif
#endif

// wu$br01, August 30,2006 filter enhancement.
	/*
#ifndef FILTER_COMBINE
#define FILTER_COMBINE
#ifdef MAINFUNC
	static char* __FILTER_COMBINE = "@(#)  FILTER_COMBINE enabled  \n";
#endif
#endif
*/

//dinzh01 support Exchange R12 GUI
#ifndef E12_GUI
#define E12_GUI
#endif

// golda12: Apply header changes enabled by this macro to all modules.  Required for consistent application of SAVE_APP_VERSION.
#ifndef NEW_ENC
#define NEW_ENC
#ifdef MAINFUNC
	static char* __NEW_ENC = "@(#)  NEW_ENC enabled  \n";
#endif
#endif

// golda12: Add field to Session Header to store Target Application Version.  Needed for SQL 2005, useful for other agents.
#ifndef SAVE_APP_VERSION
#define SAVE_APP_VERSION
#ifdef MAINFUNC
	static char* __SAVE_APP_VERSION = "@(#)  SAVE_APP_VERSION enabled  \n";
#endif
#endif

// koule01. Support for UNIX DB2 in Windows side.
#ifndef BAB_SUPPORT_UNIX_DB2_AGENT
#define BAB_SUPPORT_UNIX_DB2_AGENT
#ifdef MAINFUNC
	static char* __BAB_SUPPORT_UNIX_DB2_AGENT = "@(#)  BAB_SUPPORT_UNIX_DB2_AGENT enabled  \n";
#endif
#endif

// liupa02: CHANGE_FOR_ASDCEN
#ifndef BEB_CHANGE_FOR_ASDCEN
#define BEB_CHANGE_FOR_ASDCEN
#ifdef MAINFUNC
	static char* __BEB_CHANGE_FOR_ASDCEN = "@(#)  BEB_CHANGE_FOR_ASDCEN enabled  \n";
#endif
#endif

// liupa02: ASCORE_CHANGE_FOR_ENCRYPTION
#ifndef BEB_ASCORE_CHANGE_FOR_ENCRYPTION
#define BEB_ASCORE_CHANGE_FOR_ENCRYPTION
#ifdef MAINFUNC
	static char* __BEB_ASCORE_CHANGE_FOR_ENCRYPTION = "@(#)  BEB_ASCORE_CHANGE_FOR_ENCRYPTION enabled  \n";
#endif
#endif

// liupa02: ascore_change_for_vista
#ifndef BEB_ASCORE_CHANGE_FOR_VISTA
#define BEB_ASCORE_CHANGE_FOR_VISTA
#ifdef MAINFUNC
	static char* __BEB_ASCORE_CHANGE_FOR_VISTA = "@(#)  BEB_ASCORE_CHANGE_FOR_VISTA enabled  \n";
#endif
#endif


#ifndef REMOVE_DR_ENC_BACKDOOR
#define REMOVE_DR_ENC_BACKDOOR
#ifdef MAINFUNC
	 static char * __REMOVE_DR_ENC_BACKDOOR = "@(#)  REMOVE_DR_ENC_BACKDOOR Enabled\n";
#endif /* MAINFUNC */
#endif

//tanji04: System State backup using VSS support
#ifndef BAB_VSS_SYSTEMSTATE_SUPPORT
#define	BAB_VSS_SYSTEMSTATE_SUPPORT
#ifdef MAINFUNC
     static char * __BAB_VSS_SYSTEMSTATE_SUPPORT  = "@(#)  BAB_VSS_SYSTEMSTATE_SUPPORT enabled  \n" ;
#endif /* MAINFUNC */
#endif //BAB_VSS_SYSTEMSTATE_SUPPORT

//tanji04: Vista/Longhorn System State backup using VSS support
#ifdef BAB_VSS_SYSTEMSTATE_SUPPORT
#ifndef BAB_VSS_LONGHORN_SYSTEMSTATE_SUPPORT
#define	BAB_VSS_LONGHORN_SYSTEMSTATE_SUPPORT
#ifdef MAINFUNC
     static char * __BAB_VSS_LONGHORN_SYSTEMSTATE_SUPPORT  = "@(#)  BAB_VSS_LONGHORN_SYSTEMSTATE_SUPPORT enabled  \n" ;
#endif /* MAINFUNC */
#endif //BAB_VSS_LONGHORN_SYSTEMSTATE_SUPPORT
#endif //BAB_VSS_SYSTEMSTATE_SUPPORT

// liupa02: For new feature, WellSpan
#ifndef BEB_TAPECOPY_WELLSPAN
#define BEB_TAPECOPY_WELLSPAN
#ifdef MAINFUNC
     static char * __BEB_TAPECOPY_WELLSPAN = "@(#)  BEB_TAPECOPY_WELLSPAN enabled  \n" ;
#endif /* MAINFUNC */
#endif // BEB_TAPECOPY_WELLSPAN

//chach07. Buffer Overflow fixes for 11.5 SP2 . Discovery Service/asbrdcst.dll
#ifndef DISC_BUFFER_OVERFLOW
#define DISC_BUFFER_OVERFLOW
#ifdef MAINFUNC
     static char * __DISC_BUFFER_OVERFLOW = "@(#)  DISC_BUFFER_OVERFLOW enabled  \n" ;
#endif /* MAINFUNC */
#endif // DISC_BUFFER_OVERFLOW

//chach07. Integer Overflow fixes for 11.5 SP2 . Catirpc.dll and others ..
#ifndef INTEGER_OVERFLOW
#define INTEGER_OVERFLOW
#ifdef MAINFUNC
     static char * __INTEGER_OVERFLOW = "@(#)  INTEGER_OVERFLOW enabled  \n" ;
#endif /* MAINFUNC */
#endif // INTEGER_OVERFLOW

// wanwe08, EMC Celerra 5.5 support DDAR and filters
#ifndef CELERRA55_SUPPORT
#define CELERRA55_SUPPORT
#ifdef MAINFUNC
     static char * __CELERRA55_SUPPORT = "@(#)  CELERRA55_SUPPORT enabled  \n" ;
#endif /* MAINFUNC */
#endif // CELERRA55_SUPPORT

// wu$br01, April 10,2006 support Linux Japanese UTF8
#ifndef SUPPORT_UTF8
#define SUPPORT_UTF8
#ifdef MAINFUNC
     static char * __SUPPORT_UTF8 = "@(#)  SUPPORT_UTF8 enabled  \n" ;
#endif /* MAINFUNC */
#endif // SUPPORT_UTF8

#ifndef MUX_EXABYTE
#define MUX_EXABYTE
#ifdef MAINFUNC
     static char * __MUX_EXABYTE = "@(#)  MUX_EXABYTE enabled  \n" ;
#endif /* MAINFUNC */
#endif // MUX_EXABYTE

// liupa02: For issue 14528179
#ifndef BEB_14528179
#define BEB_14528179
#ifdef MAINFUNC
     static char * __BEB_14528179 = "@(#)  BEB_14528179 enabled  \n" ;
#endif /* MAINFUNC */
#endif // BEB_14528179

// liupa02: Fix issue 15012977   //Merge from SP2-L2: tapecopy_fix_for_15012977  by shebr03 1218
#ifndef BEB_15012977
#define BEB_15012977
#ifdef MAINFUNC
	 static char * __BEB_15012977 = "@(#)  BEB_15012977 enabled  \n" ;
#endif /* MAINFUNC */
#endif // BEB_15012977

// linyu04: export tape for DMJ
#ifndef B2D2T_EXPORTTAPE
#define B2D2T_EXPORTTAPE
#ifdef MAINFUNC
     static char * __B2D2T_EXPORTTAPE = "@(#)  B2D2T_EXPORTTAPE enabled  \n" ;
#endif /* MAINFUNC */
#endif // B2D2T_EXPORTTAPE

// added by gonch02
#ifndef BAB_15921480
#define BAB_15921480
#ifdef MAINFUNC
     static char * __BAB_15921480  = "@(#)  BAB_15921480  enabled  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BAB_15826959
#define BAB_15826959
#ifdef MAINFUNC
     static char * __BAB_15826959  = "@(#)  BAB_15826959  enabled  \n" ;
#endif /* MAINFUNC */
#endif

/* added by chefr03 */
#ifndef BAB_15957700
#define BAB_15957700
#ifdef MAINFUNC
	 static char * __BAB_15957700  = "@(#)  BAB_15957700  enabled  \n" ;
#endif	/* MAINFUNC */
#endif

/* added by chefr03 */
#ifndef BAB_15873078
#define BAB_15873078
#ifdef MAINFUNC
	 static char * __BAB_15873078  = "@(#)  BAB_15873078  enabled  \n" ;
#endif	/* MAINFUNC */
#endif

/* added by chefr03 */
#ifndef BAB_15970460
#define BAB_15970460
#ifdef MAINFUNC
	 static char * __BAB_15970460  = "@(#)  BAB_15970460  enabled  \n" ;
#endif	/* MAINFUNC */
#endif

/* added by chefr03 */
#ifndef BAB_15475825
#define BAB_15475825
#ifdef MAINFUNC
	 static char * __BAB_15475825  = "@(#)  BAB_15475825  enabled  \n" ;
#endif	/* MAINFUNC */
#endif

/* added by chefr03 */
#ifndef BAB_16006967
#define BAB_16006967
#ifdef MAINFUNC
	 static char * __BAB_16006967  = "@(#)  BAB_16006967  enabled  \n" ;
#endif	/* MAINFUNC */
#endif

#ifndef BAB_15851160
#define BAB_15851160
#ifdef MAINFUNC
     static char * __BAB_15851160  = "@(#)  BAB_15851160  enabled  \n" ;
#endif /* MAINFUNC */
#endif

// added by gonch02
#ifndef BAB_15775595 
#define BAB_15775595 
#ifdef MAINFUNC
     static char * __BAB_15775595  = "@(#)  BAB_15775595  enabled  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BAB_15475542
#define BAB_15475542
#ifdef MAINFUNC
     static char * __BAB_15475542 = "@(#)  BAB_15475542 enabled  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BAB_15522007
#define BAB_15522007
#ifdef MAINFUNC
     static char * __BAB_15522007 = "@(#)  BAB_15522007 enabled  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BAB_15550549
#define BAB_15550549
#ifdef MAINFUNC
     static char * __BAB_15550549 = "@(#)  BAB_15550549 enabled  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BAB_15548613
#define BAB_15548613
#ifdef MAINFUNC
     static char * __BAB_15548613 = "@(#)  BAB_15548613 enabled  \n" ;
#endif /* MAINFUNC */
#endif

// liupa02: For issue 14478734
#ifndef BEB_14478734
#define BEB_14478734
#ifdef MAINFUNC
     static char * __BEB_14478734 = "@(#)  BEB_14478734 enabled  \n" ;
#endif /* MAINFUNC */
#endif // BEB_14478734

// liupa02: For issue 14507080
#ifndef BEB_14507080
#define BEB_14507080
#ifdef MAINFUNC
     static char * __BEB_14507080 = "@(#)  BEB_14507080 enabled  \n" ;
#endif /* MAINFUNC */
#endif // BEB_14507080

#ifndef BEB_14507084
#define BEB_14507084
#ifdef MAINFUNC
     static char * __BEB_14507084 = "@(#)  BEB_14507084 enabled  \n" ;
#endif /* MAINFUNC */
#endif // BEB_14507084

// liuwe05 2005-10-20 add new switches to support Java cmdline jobscript conversion
#ifndef JAVA_COMMAND_LINE_SUPPORT
#define JAVA_COMMAND_LINE_SUPPORT
#endif //JAVA_COMMAND_LINE_SUPPORT

#ifndef BEB_14402371
#define BEB_14402371
#ifdef MAINFUNC
    static char * __BEB_14402371  = "@(#)  BEB_14402371 enabled  \n" ;
#endif /* MAINFUNC */
#endif // BEB_14402371


// MingZ:For supporting Authoritative Restore on DFSR Writer
// This would affect the modules in Task and VSS Agents.
#ifndef DFSR_AUTHORITATIVE_RESTORE
#define DFSR_AUTHORITATIVE_RESTORE
#ifdef MAINFUNC
     static char * __DFSR_AUTHORITATIVE_RESTORE  = "@(#)  DFSR_AUTHORITATIVE_RESTORE enabled  \n" ;
#endif /* MAINFUNC */
#endif // DFSR_AUTHORITATIVE_RESTORE

// houji03 fix for r11.5 FP2 ASDB support CPM Exchange Agent Doc-Level
#ifndef BEB_UNIXMSG_SUPPORT
#define BEB_UNIXMSG_SUPPORT
#endif //BEB_UNIXMSG_SUPPORT

// liuwe05 fix for r11.5 FP2 WinGUI support Exchange Agent DB-Level
#ifndef BAB_EXDB_SUPPORT
#define BAB_EXDB_SUPPORT
#endif //BAB_EXDB_SUPPORT

#ifndef BAB_NEW_SQL
#define BAB_NEW_SQL
#ifdef MAINFUNC
     static char * _BAB_NEW_SQL  = "@(#)  BAB_NEW_SQL  \n" ;
#endif // MAINFUNC
#endif //BAB_NEW_SQL

// liuwe05 fix for r11.5 FP2 WinGUI support CPM Exchange Agent Doc-Level
#ifndef BAB_EXSIS_SUPPORT_CPM
#define BAB_EXSIS_SUPPORT_CPM
#endif //BAB_EXSIS_SUPPORT_CPM

// Steven Hwang checked in for the code which made by Alan Maslar for suporting
// longer path length upto 512 characters.
#ifndef _ARCHIVE_DETAIL_
#define _ARCHIVE_DETAIL_
#ifdef MAINFUNC
     static char * __ARCHIVE_DETAIL_  = "@(#)  _ARCHIVE_DETAIL_ enabled  \n" ;
#endif /* MAINFUNC */
#endif //_ARCHIVE_DETAIL_

#if !defined(BAB_VSS_DPM) && !defined(_WIN95AGENT)
#define BAB_VSS_DPM
#ifdef MAINFUNC
     static char * _BAB_VSS_DPM  = "@(#)  BAB_VSS_DPM  \n" ;
#endif // MAINFUNC
#endif //BAB_VSS_DPM

//YL(linyu04):DMJ eject tape for tape library for device configuration
#ifndef BBABW_14176854
#define BBABW_14176854
#ifdef MAINFUNC
     static char * __BBABW_14176854  = "@(#)  BBABW_14176854 enabled  \n" ;
#endif /* MAINFUNC */
#endif //

//YL(linyu04) fix for restoring one million files
#ifndef FP2_RESTORE_MILLION_FILES
#define FP2_RESTORE_MILLION_FILES
#ifdef MAINFUNC
     static char * __FP2_RESTORE_MILLION_FILES  = "@(#)  FP2_RESTORE_MILLION_FILES enabled  \n" ;
#endif /* MAINFUNC */
#endif //

#ifndef NEW_ORACLE_WINDOWS
#define NEW_ORACLE_WINDOWS
#ifdef MAINFUNC
     static char * __NEW_ORACLE_WINDOWS  = "@(#)  NEW_ORACLE_WINDOWS enabled  \n" ;
#endif /* MAINFUNC */
#endif //NEW_ORACLE_WINDOWS

#ifndef WIN_ORACLERMAN_RELEASE_GROUP
#define WIN_ORACLERMAN_RELEASE_GROUP
#ifdef MAINFUNC
     static char * __WIN_ORACLERMAN_RELEASE_GROUP  = "@(#)  WIN_ORACLERMAN_RELEASE_GROUP enabled  \n" ;
#endif /* MAINFUNC */
#endif //WIN_ORACLERMAN_RELEASE_GROUP

#ifndef ORA_RMAN_SUPPORT
#define ORA_RMAN_SUPPORT
#endif //ORA_RMAN_SUPPORT

#ifndef ORACLE_RMAN_SUPPORT
#define ORACLE_RMAN_SUPPORT
#endif  //ORACLE_RMAN_SUPPORT

#ifndef SQL_MST_COUNTDRIVES
#define SQL_MST_COUNTDRIVES
#ifdef MAINFUNC
     static char * __SQL_MST_COUNTDRIVES  = "@(#)  SQL_MST_COUNTDRIVES enabled  \n" ;
#endif /* MAINFUNC */
#endif //SQL_MST_COUNTDRIVES

#ifndef SQL_MST_1DRIVE
#define SQL_MST_1DRIVE
#ifdef MAINFUNC
     static char * __SQL_MST_1DRIVE  = "@(#)  SQL_MST_1DRIVE enabled  \n" ;
#endif /* MAINFUNC */
#endif //SQL_MST_1DRIVE

//CK(kelch07) fix for Tapename being packaged as NULL for ORACLERMAN Jobs.
#ifndef NEW_ORACLE_NULL_TAPE
#define NEW_ORACLE_NULL_TAPE
#endif //NEW_ORACLE_NULL_TAPE


//CK(kelch07) fix for sharepoint EDA sessions with disbale database
#ifndef WSPSA_14149215
#define WSPSA_14149215
#endif //WSPSA_14149215


//CK(kelch07) change strchr()/strrchr() to _mbschr()/_mbsrchr() as Per Jonathan
#ifndef _MULTIBYTE
#define _MULTIBYTE
#endif //_MULTIBYTE

// wu$br01, merge from 10.0 to 11.5
#ifndef BAB_13877635
#define	BAB_13877635
#endif //BAB_13877635

//wu$yu02, Fix issue 14070345, Keep user account/password for each restore session
#ifndef BAB_14070345
#define BAB_14070345
#endif

// Add by Gil
// 12/13/2006 4:27:31 PM
#ifndef SPS_2007_CHANGES
#define SPS_2007_CHANGES
#endif
// End add by Gil 12/13/2006 4:27:32 PM

// maoja01 add begin -- 2007.05.25
#ifndef SPS2007_XML_MOVE
#define SPS2007_XML_MOVE
#endif
// maoja01 add end

//wu$yu02, Drop VI support for all client agents
#ifndef BAB_DROPPING_VI_SUPPORT
#define BAB_DROPPING_VI_SUPPORT
#endif

//wu$yu02
#ifndef BAB_SERVER_ADMIN
#define BAB_SERVER_ADMIN
#ifdef MAINFUNC
     static char * ___BAB_SERVER_ADMIN  = "@(#)  BAB_SERVER_ADMIN  \n" ;
#endif // MAINFUNC
#endif //

//wu$yu02, Refactory GUI code to use multi-TSI instance.
#ifndef BAB_MULTI_TSIHANDEL
#define BAB_MULTI_TSIHANDEL
#endif

//YL(linyu04)
#ifndef _RUNNOWTIME
#define _RUNNOWTIME
#ifdef MAINFUNC
     static char * ___RUNNOWTIME  = "@(#)  _RUNNOWTIME  \n" ;
#endif // MAINFUNC
#endif //_RUNNOWTIME

//di$da01 fix issue 14009844
#ifndef BAB_14009844
#define BAB_14009844
#endif

//YL(linyu04)
#ifndef DR_AGNT_ALIASNAME
#define DR_AGNT_ALIASNAME
#ifdef MAINFUNC
     static char * __DR_AGNT_ALIASNAME  = "@(#)  DR_AGNT_ALIASNAME  \n" ;
#endif // MAINFUNC
#endif //DR_AGNT_ALIASNAME

//Chandra
#ifndef FSD_USE_CHUNK_SIZE
#define FSD_USE_CHUNK_SIZE
#ifdef MAINFUNC
     static char * __FSD_USE_CHUNK_SIZE  = "@(#)  FSD_USE_CHUNK_SIZE  \n" ;
#endif // MAINFUNC
#endif //FSD_USE_CHUNK_SIZE

//donzh01
#ifndef TAPECOPY_PASSTHROUGH
#define TAPECOPY_PASSTHROUGH
#ifdef MAINFUNC
     static char * __TAPECOPY_PASSTHROUGH  = "@(#)  TAPECOPY_PASSTHROUGH  \n" ;
#endif // MAINFUNC
#endif //TAPECOPY_PASSTHROUGH

//wanji14 
//use old mechinasim to support tape copy.
//tape copy need to modify to the new passthrough mechinasim in the future.
//after that, we will remove all the refernece of this macro.
#ifndef REMAINED_FOR_TAPECOPY_USE_ONLY
#define REMAINED_FOR_TAPECOPY_USE_ONLY
#endif //REMAINED_FOR_TAPECOPY_USE_ONLY

//donzh01
#ifndef UNIFORM_TAPE_HEADER
#define UNIFORM_TAPE_HEADER
#ifdef MAINFUNC
     static char * __UNIFORM_TAPE_HEADER  = "@(#)  UNIFORM_TAPE_HEADER  \n" ;
#endif // MAINFUNC
#endif //UNIFORM_TAPE_HEADER

//donzh01
#ifndef MUX_CIM
#define MUX_CIM
#ifdef MAINFUNC
     static char * __MUX_CIM  = "@(#)  MUX_CIM  \n" ;
#endif // MAINFUNC
#endif //MUX_CIM

//Donzh01
#ifndef RM_CHUNKING
#define RM_CHUNKING
#ifdef MAINFUNC
     static char * __RM_CHUNKING  = "@(#)  RM_CHUNKING  \n" ;
#endif // MAINFUNC
#endif //RM_CHUNKING

//Chris Kelly(kelch07): Merge Multiplexing Range of sessions issue.
#ifndef _14098691
#define _14098691
#endif	//_14098691

//Chris Kelly(kelch07), Scan, continue session problem for Multiplexing tapes.
#ifndef _20050401
#define _20050401
#endif //#ifdef _20050401

//Chris Kelly(kelch07), Merge/Scan Frist session on Sequence of Tape that is not Sequence 1.
#ifndef _14033968
#define _14033968
#endif //#ifdef _14033968


//Chris Kelly(kelch07), Change string printed during Staging Makeup job, if media is not found.
#ifndef _14031478
#define _14031478
#endif //_14031478

//Chris Kelly(kelch07), Add Disk Staging Policy to Activity Log for Master Staging Job.
#ifndef _14024420
#define _14024420
#endif //_14024420

//Chris Kelly(kelch07), Scan of NAS session showing as truncated in log.  Added logic to not print this
//message as NAS sessions are a special case.
#ifndef _14027453
#define _14027453
#endif //_14027453
//Chris Kelly(kelch07), Merge/Scan fix to continue to next session if we hit an error on a session
#ifndef _14010907
#define _14010907
#endif //_14010907
//Chris Kelly(kelch07), Merge Fix for Wrong Session Type after Merge of Sharepoint Session
#ifndef _13994248
#define _13994248
#endif	//_13994248

#ifndef BAB_EM_3DES
#define BAB_EM_3DES
#ifdef MAINFUNC
     static char * __BAB_EM_3DES  = "@(#)  BAB_EM_3DES enabled  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BAB_SQL_3DES
#define BAB_SQL_3DES
#ifdef MAINFUNC
     static char * __BAB_SQL_3DES  = "@(#)  BAB_SQL_3DES enabled  \n" ;
#endif /* MAINFUNC */
#endif

// David Goldstein (golda12), Enable the changes for SQL 2005 Compatibility (SQL 2000 Features)
#ifndef SQLYUKON
#define SQLYUKON
#ifdef MAINFUNC
     static char * __SQLYUKON  = "@(#)  SQLYUKON enabled  \n" ;
#endif /* MAINFUNC */
#endif //VSS64LOCALASREMOTE

//Enable for netware groupwise support
#ifndef NW_GW
#define NW_GW
#endif

//Enable for WORM
#ifndef WORM_JOB
#define WORM_JOB
#endif

//Enable for Auto-admin
//#ifndef ENABLE_NONADMIN_13788272
//#define ENABLE_NONADMIN_13788272
//#endif

//Enable huge session enhancement.
#ifndef HUGE_SESSION_ENHANCEMENT
#define HUGE_SESSION_ENHANCEMENT
#endif


//Enable for SharePoint Agent support
#ifndef BEB_AGENT_SHAREPOINT
#define BEB_AGENT_SHAREPOINT
#endif

// Enable for Ingres support ( now backup available only )
#ifndef BEB_INGRES_SUPPORT
#define BEB_INGRES_SUPPORT
#endif //BEB_INGRES_SUPPORT

// Enable for Ingres Multi-instance support
#ifndef INGRES_MULTI_INSTANCE
#define INGRES_MULTI_INSTANCE
#endif //INGRES_MULTI_INSTANCE

//Enable retry_duplicate enhancment - If there is a media error during restore of a session
//check to see if there are duplicate copies of that session and if there are restart restore
//of that session with one of the duplicate copies.
#ifndef retry_duplicate
#define retry_duplicate
#endif //retry_duplicate

//Enable Merge Session Headers Only- Allows for Faster Merge of Large Tape.  Only Merges Session Header
//information  Afterwards you can do a full merge of the sessions you would like to know all the details
//for.
#ifndef SES_HEAD_ONLY
#define SES_HEAD_ONLY
#endif //SES_HEAD_ONLY

//Enable Fix __12630643 - Send Server Version Information(r##.#) to Unix agent as per new Unix
//agent requirements.
#ifndef _12630643
#define _12630643
#endif //_12630643

//Enable Fix _13621909 - Skip Scan of session if session is Exchange Document Level.  Also if
//CRC values are different during scan, cause job to be incomplete.
#ifndef _13621909
#define _13621909
#endif //_13621909


//Enable Fix _13552203 - Generic Job with wrong parameters final status failed if error in log.
#ifndef _13552203
#define _13552203
#endif //_13552203


//Enable Fix _13079833 - If bad session during scan, skip session and continue.
#ifndef _13079833
#define _13079833
#endif //_13079833

//Enable Fix _12924061 - Merge Range of sessions and Merge From Session to End of Tape
#ifndef _12924061
#define _12924061
#endif //_12924061

//this feature is disabled on 06/15/2005. Code review and testing are required if enable it in the future.
//Enabled fix #ifdef _13080601 Master Log for Multiplex/MultiStream Failed Jobs
//#ifndef _13080601
//#define _13080601
//#endif //#ifdef _13080601

//Enabled for Royal Caribbean
#ifndef _TIMEZONE		// GOYRA03 09/21/2004 (ASCORE.DLL, CA_BACKUP.EXE)
#define _TIMEZONE		// Timezone change support.
#ifdef MAINFUNC
     static char * ___TIMEZONE = "@(#)  _TIMEZONE Enabled\n" ;
#endif /* MAINFUNC */
#endif

// MingZ, Enable the VSS Incremental/Differential snapshot support.
// This would affect the modules in GUI, Task and Agents.
#ifndef BAB_VSSSCHEMA
#define BAB_VSSSCHEMA
#ifdef MAINFUNC
     static char * __BAB_VSSSCHEMA  = "@(#)  BAB_VSSSCHEMA enabled  \n" ;
#endif /* MAINFUNC */
#endif //BAB_VSSSCHEMA

// David Goldstein (golda12), Enable the Task changes for 64-bit machines.
#ifndef VSS64LOCALASREMOTE
#define VSS64LOCALASREMOTE
#ifdef MAINFUNC
     static char * __VSS64LOCALASREMOTE  = "@(#)  VSS64LOCALASREMOTE enabled  \n" ;
#endif /* MAINFUNC */
#endif //VSS64LOCALASREMOTE

//Added for Oracle RMAN support. This would enable this entire feature.
//This also means that all the changes done in Task, GUI etc would be included.

#ifndef BEB_ORACLE_RMAN
#define BEB_ORACLE_RMAN
#ifdef MAINFUNC
     static char * __BEB_ORACLE_RMAN  = "@(#)  BEB_ORACLE_RMAN enabled  \n" ;
#endif /* MAINFUNC */
#endif //BEB_ORACLE_RMAN
#ifndef NEW_ORACLE
#define NEW_ORACLE
#endif //NEW_ORACLE

// liuwe05 2005-3-16 fix Issue: 14034540    Title: BAB.EXE SECURITY FLAW
// add check for equivalence for BAB.EXE
#ifndef BEB_CHECK_EQUIV
#define BEB_CHECK_EQUIV
#endif //BEB_CHECK_EQUIV

//Added for Device Replacement project. This would enable this entire feature.
#ifndef DEVICE_REPLACEMENT
#define DEVICE_REPLACEMENT
#ifdef MAINFUNC
     static char * __DEVICE_REPLACEMENT  = "@(#)  DEVICE_REPLACEMENT  \n" ;
#endif /* MAINFUNC */
#endif //DEVICE_REPLACEMENT

//Added for B2D2T project. This would enable this entire feature.
//This also means that all the changes done in TE, Task, GUI etc would be included.
#ifndef _B2D2T
#define _B2D2T
#ifdef MAINFUNC
     static char * ___B2D2T  = "@(#)  _B2D2T  \n" ;
#endif /* MAINFUNC */
#endif //_B2D2T

//Added for B2T2T project. This would enable this entire feature.
//This also means that all the changes done in TE, Task, GUI , WinCMDL etc would be included.
#ifndef _B2T2T
#define _B2T2T
#ifdef MAINFUNC
     static char * ___B2T2T  = "@(#)  _B2T2T  \n" ;
#endif /* MAINFUNC */
#endif //_B2T2T

#ifndef ASDB_SQLEXPRESS_2005  // kimwo01 1/6/2006 (Database modules)
#define ASDB_SQLEXPRESS_2005  // New Database for ARCserve
#ifdef MAINFUNC
     static char * __ASDB_SQLEXPRESS_2005  = "@(#)  ASDB_SQLEXPRESS  \n" ;
#endif /* MAINFUNC */
#endif //ASDB_SQLEXPRESS_2005

// Enabled for Audit
#ifndef BAB_AUDIT
#define BAB_AUDIT
#endif

// Enabled for Backup 2 Disk 2 Tape
#ifndef ASDB_D2D2T
#define ASDB_D2D2T
#endif

//////bater.makhabel,2005May19,remove the logs of activity log populated in the daemon log files, such as, cauthd.log
#ifndef GLOBAL_LOGGER_OBJECTS
#define GLOBAL_LOGGER_OBJECTS
#endif

//Enabled for CCB
#ifndef BEB_CCB_JOBWINDOW		// KIMJU01 4/04/2002 (aslogres.dll, jobwindow.exe & jobeng.exe)
#define BEB_CCB_JOBWINDOW		// Job window configuration support.
#ifdef MAINFUNC
     static char * __BEB_CCB_JOBWINDOW = "@(#)  BEB_CCB_JOBWINDOW Enabled\n" ;
#endif /* MAINFUNC */
#endif

//Dana; Flash Recovery Area is the default for Oracle 10G
#ifndef ORCL_FRA_10G
#define ORCL_FRA_10G
#endif

//<li$we04 desc="Enable username/password encryption after BAB11.01 beta 1">
#ifndef ENABLE_SECUREDATA_ENCRYPTION
#define ENABLE_SECUREDATA_ENCRYPTION
#endif
//</li$we04>

//Dana; No.1 hot Customer Issue for Windows Oracle Agent
#ifndef ORCL_WINDOWS_AUTH
#define ORCL_WINDOWS_AUTH
#endif

//Dana; check the ASM support for Oracle 10G
#ifndef ORCL_ASM_10G
#define ORCL_ASM_10G
#endif

//Dana; No.2 hot Customer issue for for Windows Oracle Agent
//Error handling is bad for RMAN Windows Oracle Agent
//the next 2 issues are related to this
#ifndef ORCL_RMAN_ERR_LOG
#define ORCL_RMAN_ERR_LOG
#endif

//Dana; No.2 hot Customer issue for for Windows Oracle Agent
#ifndef ORCL_RMAN_ERR_EXCEPT
#define ORCL_RMAN_ERR_EXCEPT
#endif

//Dana; Oracle Customer issue: make the session timeout configurable
#ifndef ORCL_BAB_13126885
#define ORCL_BAB_13126885
#endif

//Dana; Oracle Customer issue: if the TBLSPACE is already in backup mode don't return error
//equivalent to the issue opened by James: 13254530
#ifndef ORCL_BAB_13223312
#define ORCL_BAB_13223312
#endif


//Chandra
#ifndef FIX_BETTER_LOGGING
#define FIX_BETTER_LOGGING
#ifdef MAINFUNC
     static char * __FIX_BETTER_LOGGING  = "@(#)  FIX_BETTER_LOGGING  \n" ;
#endif /* MAINFUNC */
#endif

//Chandra
#ifndef FLUSH_DATA_FM_FIX
#define FLUSH_DATA_FM_FIX
#ifdef MAINFUNC
     static char * __FLUSH_DATA_FM_FIX  = "@(#)  FLUSH_DATA_FM_FIX  \n" ;
#endif /* MAINFUNC */
#endif

//Chandra
#ifndef MUX_COPY
#define MUX_COPY
#ifdef MAINFUNC
     static char * __MUX_COPY  = "@(#)  MUX_COPY  \n" ;
#endif /* MAINFUNC */
#endif

//Chandra
#ifndef FIX_ILLEGAL_BLOCK_SIZE_ERROR
#define FIX_ILLEGAL_BLOCK_SIZE_ERROR
#ifdef MAINFUNC
     static char * __FIX_ILLEGAL_BLOCK_SIZE_ERROR  = "@(#)  FIX_ILLEGAL_BLOCK_SIZE_ERROR  \n" ;
#endif /* MAINFUNC */
#endif

//Chandra
#ifndef FIX_FM_RETURNS_EW_ERROR
#define FIX_FM_RETURNS_EW_ERROR
#ifdef MAINFUNC
     static char * __FIX_FM_RETURNS_EW_ERROR  = "@(#)  FIX_FM_RETURNS_EW_ERROR  \n" ;
#endif /* MAINFUNC */
#endif

//Chandra
#ifndef FIX_END_SESSION_SPAN_FAILURE
#define FIX_END_SESSION_SPAN_FAILURE
#ifdef MAINFUNC
     static char * __FIX_END_SESSION_SPAN_FAILURE  = "@(#)  FIX_END_SESSION_SPAN_FAILURE  \n" ;
#endif /* MAINFUNC */
#endif

//Chandra
#ifndef FIX_CHILD_HANG_PROBLEM
#define FIX_CHILD_HANG_PROBLEM
#ifdef MAINFUNC
     static char * __FIX_CHILD_HANG_PROBLEM  = "@(#)  FIX_CHILD_HANG_PROBLEM  \n" ;
#endif /* MAINFUNC */
#endif

//This fix is actually for issue 13127046 in STAR. Chandra
#ifndef FIX_13124174
#define FIX_13124174
#ifdef MAINFUNC
     static char * __FIX_13124174  = "@(#)  FIX_13124174  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_ENABLED
#define BEB_ENABLED

#ifndef BEB_HARDWARE_VSS	// VSS Hardware Snap-shot Support for BAB 11.1
#define BEB_HARDWARE_VSS
#endif

#ifndef EB_FIREWALL_SUPPORT  // New firewall support
#define EB_FIREWALL_SUPPORT
#endif

#ifndef ASDB_ASLOG  // New DB support for job error
#define ASDB_ASLOG
#endif

#ifndef  PREPOST_SEC_ENH   //Require user/password for Prepost cmds.
#define PREPOST_SEC_ENH
#endif

#ifndef SYB_MULT_SERVER
#define SYB_MULT_SERVER
#endif

#ifndef BAB_SQL_ROTATION
#define BAB_SQL_ROTATION
#endif

#ifndef MULTISTRIPE_SQL
#define MULTISTRIPE_SQL
#endif

#ifndef BAB_13189516
#define BAB_13189516
#endif

//#define SP3
//#define SP4
//#define SP5
//....
//#define SPN


#ifndef MNIC_SUPPORT
#define MNIC_SUPPORT
#ifdef MAINFUNC
     static char * __MNIC_SUPPORT  = "@(#)  MNIC_SUPPORT  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef SEC_ENH
#define SEC_ENH
#ifdef MAINFUNC
     static char * __SEC_ENH  = "@(#)  SEC_ENH  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef SCAN_EX  //Multistreaming master job monitor
#define SCAN_EX
#ifdef MAINFUNC
     static char * __SCAN_EX  = "@(#)  SCAN_EX  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef MP_JM  //Multistreaming master job monitor
#define MP_JM
#ifdef MAINFUNC
     static char * __MP_JM  = "@(#)  MP_JM  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef MUX_TAPE  //Chandra for MULTIPLEXING
#define MUX_TAPE  //Chandra
#ifdef MAINFUNC
     static char * __MUX_TAPE  = "@(#)  MUX_TAPE  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef MUX_DEVICE  //Chandra for MULTIPLEXING
#define MUX_DEVICE  //Chandra
#ifdef MAINFUNC
     static char * __MUX_DEVICE  = "@(#)  MUX_DEVICE  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef TIMESTAMPED_SLOTUPDATE //Added by boest02 9/16/03 - allows gui to get Time stamped slot updates
#define TIMESTAMPED_SLOTUPDATE
#ifdef MAINFUNC
     static char * __TIMESTAMPED_SLOTUPDATE  = "@(#)  TIMESTAMPED_SLOTUPDATE  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CMD_DELIM
#define BEB_CMD_DELIM
#ifdef MAINFUNC
     static char * __BEB_CMD_DELIM  = "@(#)  BEB_CMD_DELIM  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef DISCOVERY_PINGSWEEP
#define DISCOVERY_PINGSWEEP	/* chach07 :Add new discovery using pingsweep*/
#ifdef MAINFUNC
     static char * __DISCOVERY_PINGSWEEP  = "@(#)  DISCOVERY_PINGSWEEP  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11718749  //David Goldstein for Chandra Reddy 10/30/02
#define BEB_11718749  //SQL Agent Remote Cancel Fix
#ifdef MAINFUNC
     static char * __BEB_11718749  = "@(#)  BEB_11718749  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_SQL_REMOTE_CANCEL_FIX  //David Goldstein for Chandra Reddy 10/30/02
#define BEB_SQL_REMOTE_CANCEL_FIX  //SQL Agent Remote Cancel Fix
#ifdef MAINFUNC
     static char * __BEB_SQL_REMOTE_CANCEL_FIX  = "@(#)  BEB_SQL_REMOTE_CANCEL_FIX  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_SQL_CLEAN_EXIT_ON_ERR  //David Goldstein 10/30/02
#define BEB_SQL_CLEAN_EXIT_ON_ERR  //SQL Agent Clean Exit on SQL Server Error
#ifdef MAINFUNC
     static char * __BEB_SQL_CLEAN_EXIT_ON_ERR  = "@(#)  BEB_SQL_CLEAN_EXIT_ON_ERR  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_UNIX_LOGGER_FIXES_MERGE  //palsi01  10/25/2002: Merged Unix Logger fixes int NT Code branch
#define BEB_UNIX_LOGGER_FIXES_MERGE
#ifdef MAINFUNC
     static char * __BEB_UNIX_LOGGER_FIXES_MERGE  = "@(#)  BEB_UNIX_LOGGER_FIXES_MERGE  \n" ;
#endif /* MAINFUNC */
#endif

#if 0
#ifndef BEB_ACTIVITYLOG_NAME  //saidh01  10/31/2002: Make Activity log file name same across unix and NT
#define BEB_ACTIVITYLOG_NAME
#ifdef MAINFUNC
     static char * __BEB_ACTIVITYLOG_NAME  = "@(#)  BEB_ACTIVITYLOG_NAME  \n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_RUNTIME_ACTIVITYLOG  //saidh01  09/25/2003: Detect activitylog name at runtime for BAB and BEB
#define BEB_RUNTIME_ACTIVITYLOG
#ifdef MAINFUNC
     static char * __BEB_RUNTIME_ACTIVITYLOG  = "@(#)  BEB_RUNTIME_ACTIVITYLOG  \n" ;
#endif
#endif

#endif /* Disabled to make a fixed activity log name " CST_PRODUCTLOG_A " */


#ifndef BEB_INDEX_FIX  //saidh01  02/12/2003: Fix for corrupt line index.
#define BEB_INDEX_FIX
#ifdef MAINFUNC
     static char * __BEB_INDEX_FIX  = "@(#)  BEB_INDEX_FIX  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_PURIFY_FIX  //saidh01  02/12/2003: Fix for problems reported in purify.
#define BEB_PURIFY_FIX
#ifdef MAINFUNC
     static char * __BEB_PURIFY_FIX  = "@(#)  BEB_PURIFY_FIX  \n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11799718  //saidh01  11/08/2002: Implementing case free log file names.
#define BEB_11799718
#ifdef MAINFUNC
     static char * __BEB_11799718  = "@(#)  BEB_11799718  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_12336592  //saidh01  11/15/2002: Delayed Registry affect ca_log functionality.
#define BEB_12336592
#ifdef MAINFUNC
     static char * __BEB_12336592  = "@(#)  BEB_12336592  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_SHOW_MSG_SEV  //saidh01  11/18/2002: ca_log will show msg sev.
#define BEB_SHOW_MSG_SEV
#ifdef MAINFUNC
     static char * __BEB_SHOW_MSG_SEV  = "@(#)  BEB_SHOW_MSG_SEV  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_LOGGER_PRUNE_FIX  //saidh01  03/24/2003: Pruning corruptes indexes.
#define BEB_LOGGER_PRUNE_FIX
#ifdef MAINFUNC
     static char * __BEB_LOGGER_PRUNE_FIX  = "@(#)  BEB_LOGGER_PRUNE_FIX  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_DUPLOG_FIX  //saidh01  07/31/2003: Duplicate Log Lines in ca_log.
#define BEB_DUPLOG_FIX
#ifdef MAINFUNC
     static char * __BEB_DUPLOG_FIX  = "@(#)  BEB_DUPLOG_FIX  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef LOGGER_DATE_PURGE  //saidh01  06/19/2003: Date wise purging of log files.
#define LOGGER_DATE_PURGE
#ifdef MAINFUNC
     static char * __LOGGER_DATE_PURGE  = "@(#)  LOGGER_DATE_PURGE  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_GENERIC_MSG  //saidh01  06/27/2003: Added a generic RPC message.
#define BEB_GENERIC_MSG
#ifdef MAINFUNC
     static char * __BEB_GENERIC_MSG  = "@(#)  BEB_GENERIC_MSG  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_LOGGER_ENHANCEMENTS  //saidh01  01/29/2004: Added logger enhancements.
#define BEB_LOGGER_ENHANCEMENTS
#ifdef MAINFUNC
     static char * __BEB_LOGGER_ENHANCEMENTS  = "@(#)  BEB_LOGGER_ENHANCEMENTS  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef LOGGER_RUNTIME_CONFIG  //saidh01  02/09/2004: Manage runtime configuration.
#define LOGGER_RUNTIME_CONFIG
#ifdef MAINFUNC
     static char * __LOGGER_RUNTIME_CONFIG  = "@(#)  LOGGER_RUNTIME_CONFIG \n" ;
#endif /* MAINFUNC */
#endif

#ifndef TASK_LOCAL_JOBLOGS  //saidh01  01/29/2004: task will create local job logs.
#define TASK_LOCAL_JOBLOGS
#ifdef MAINFUNC
     static char * __TASK_LOCAL_JOBLOGS  = "@(#)  TASK_LOCAL_JOBLOGS  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_NO_TAPECOPY_LOG  //saidh01  11/19/2002: No tapecopy logs for NT logger.
#define BEB_NO_TAPECOPY_LOG
#ifdef MAINFUNC
     static char * __BEB_NO_TAPECOPY_LOG  = "@(#)  BEB_NO_TAPECOPY_LOG  \n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_PARALLEL_REMOTE_RESTORE_INVALID_RE_ID_FIX		//Chandra. During SQL multiple remote restores proper Restore ID was not passed. This fixes it.
#define BEB_PARALLEL_REMOTE_RESTORE_INVALID_RE_ID_FIX
#ifdef MAINFUNC
     static char * __BEB_PARALLEL_REMOTE_RESTORE_INVALID_RE_ID_FIX = "@(#)  BEB_PARALLEL_REMOTE_RESTORE_INVALID_RE_ID_FIX\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_REMOTE_RESTORE_SYNCH_FIX		//Chandra. During SQL multiple restores, there were synchronization issues. This fixes it.
#define BEB_REMOTE_RESTORE_SYNCH_FIX
#ifdef MAINFUNC
     static char * __BEB_REMOTE_RESTORE_SYNCH_FIX = "@(#)  BEB_REMOTE_RESTORE_SYNCH_FIX\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_NO_CLEANUP_FIX		//Chandra. After a SQL backup/restore there was no proper cleanup. This does that.
#define BEB_NO_CLEANUP_FIX
#ifdef MAINFUNC
     static char * __BEB_NO_CLEANUP_FIX = "@(#)  BEB_NO_CLEANUP_FIX\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_LOG_SYNC_FIX		//Chandra. If trace is enabled, sometimes SQL backups/restores hangs because of synchronization issues. This fixes it.
#define BEB_LOG_SYNC_FIX
#ifdef MAINFUNC
     static char * __BEB_LOG_SYNC_FIX = "@(#)  BEB_LOG_SYNC_FIX\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_CREATETHREAD_REPLACE		//Chandra. SQL Agent. CreateThread is replaced by _begin_threadex. This eliminates a small memory leak inherent in Create Thread.
#define BEB_CREATETHREAD_REPLACE
#ifdef MAINFUNC
     static char * __BEB_CREATETHREAD_REPLACE = "@(#)  BEB_CREATETHREAD_REPLACE\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_THREAD_PARAM_BUG		//Chandra. During SQL Restore parameters passed to thread were destroyed after passing to thread. This fixes it.
#define BEB_THREAD_PARAM_BUG
#ifdef MAINFUNC
     static char * __BEB_THREAD_PARAM_BUG = "@(#)  BEB_THREAD_PARAM_BUG\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_EXCLUSIVE_SEMAPHORE		//Chandra. SQL Agent. A single semaphore was being created again and again without getting destroyed thus introducing handle leaks. This fixes it.
#define BEB_EXCLUSIVE_SEMAPHORE
#ifdef MAINFUNC
     static char * __BEB_EXCLUSIVE_SEMAPHORE = "@(#)  BEB_EXCLUSIVE_SEMAPHORE\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_SQL_BA_RE_NO_VDI_INFO_ON_TAPE_BUG		//Chandra. VDI Information during SQL backup was not put in to tape, causing problems during restore. This fixes it.
#define BEB_SQL_BA_RE_NO_VDI_INFO_ON_TAPE_BUG
#ifdef MAINFUNC
     static char * __BEB_SQL_BA_RE_NO_VDI_INFO_ON_TAPE_BUG = "@(#)  BEB_SQL_BA_RE_NO_VDI_INFO_ON_TAPE_BUG\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_REMOTE_VDI_SYNC_FIX		//Chandra. During SQL Backup, BAB was not waiting for all SQL stripes to finish backup. So subsequent jobs used to hang.
#define BEB_REMOTE_VDI_SYNC_FIX
#ifdef MAINFUNC
     static char * __BEB_REMOTE_VDI_SYNC_FIX = "@(#)  BEB_REMOTE_VDI_SYNC_FIX\n" ;
#endif /* MAINFUNC */
#endif

#ifndef  BEB_12103555 /* chach07 : Take out authentication check in this part */
#define  BEB_12103555
#ifdef MAINFUNC
     static char * __BEB_12103555 = "@(#)  BEB_12103555\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_12139070		//diael01;8/14/2002 admin$ retry logic for restore
#define BEB_12139070
#ifdef MAINFUNC
     static char * __BEB_12139070 = "@(#)  BEB_12139070\n" ;
#endif /* MAINFUNC */
#endif



//-----------------------------------------------------
#if 0 // Remove this #if 0 when building Japanese SP4.
#ifndef BEB_SJIS_FIX
#define BEB_SJIS_FIX   // venkr01
#ifdef MAINFUNC
   static char * __BEB_SJIS_FIX = "@(#)   BEB_SJIS_FIX Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_SJIS_EUC
#define BEB_SJIS_EUC
#ifdef MAINFUNC
     static char * __BEB_SJIS_EUC = "@(#)  BEB_SJIS_EUC\n" ;
#endif /* MAINFUNC */
#endif
#endif

//-------------------------------------------------------

// Add fixes that needs to go into SP3
//#if defined(SP3) || defined(_SP3)

#ifndef BEB_11687777
#define BEB_11687777	/* CHUKU01 3/14/2002 */
#ifdef MAINFUNC
     static char * __BEB_11687777 = "@(#)  BEB_11687777 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11732870
#define BEB_11732870	/* YANFANG 3/14/2002 */
#ifdef MAINFUNC
     static char * __BEB_11732870 = "@(#)  BEB_11732870 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11785407
#define BEB_11785407	/* DANA 3/14/2002 */
#ifdef MAINFUNC
     static char * __BEB_11785407 = "@(#)  BEB_11785407 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11583620
#define BEB_11583620	/* MIHAI 3/14/2002 */
#ifdef MAINFUNC
     static char * __BEB_11583620 = "@(#)  BEB_11583620 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11754737
#define BEB_11754737	/* MIHAI 3/14/2002 */
#ifdef MAINFUNC
     static char * __BEB_11754737 = "@(#)  BEB_11754737 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11650777
#define BEB_11650777	/* hwapi01 steven hwang 3/13/2002 SP3 */
#ifdef MAINFUNC
     static char * __BEB_11650777 = "@(#)  BEB_11650777 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11734181
#define BEB_11734181	/* VENKR01 3/13/2002 SP3 */
#ifdef MAINFUNC
     static char * __BEB_11734181 = "@(#)  BEB_11734181 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// -lee, fix job package problem with remote Protocal, under agent machine
#ifndef	BEB_11682141
#define BEB_11682141 /* 3/12/2002 leech09 */
#  ifdef MAINFUNC
	 static char * __BEB_11682141 = "@(#)	BEB_11682141 Enabled\n" ;
#  endif
#endif

#ifndef BEB_11718635
#define BEB_11718635  /* 3/12/2002 admin$ issue - diael01 */
#  ifdef MAINFUNC
     static char * __BEB_11718635 = "@(#)  BEB_11718635 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11750794
#define BEB_11750794  /* 3/12/2002 Glen GP probelm, -leech09 */
#  ifdef MAINFUNC
     static char * __BEB_11750794 = "@(#)  BEB_11750794 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef SP3_11711784
#  define SP3_11711784  /* li$ya01: 3/5/02 */
#  ifdef MAINFUNC
     static char * __SP3_11711784 = "@(#)  SP3_11711784 Enabled\n" ;
#  endif /* MAINFUNC */
#endif /* SP3_11711784 */

#ifndef SP3_11719458
#  define SP3_11719458  /* chach07: 3/4/02 */
#  ifdef MAINFUNC
     static char * __SP3_11719458 = "@(#)  SP3_11719458 Enabled\n" ;
#  endif /* MAINFUNC */
#endif /* SP3_11719458 */

#ifndef SP3_11752794
#  define SP3_11752794  /* zitja01: 3/4/02 */
#  ifdef MAINFUNC
     static char * __SP3_11752794 = "@(#)  SP3_11752794 Enabled\n" ;
#  endif /* MAINFUNC */
#endif /* SP3_11752794 */

#ifndef SP3_11749339
#  define SP3_11749339  /* diael01: 3/4/02 */
#  ifdef MAINFUNC
     static char * __SP3_11749339 = "@(#)  SP3_11749339 Enabled\n" ;
#  endif /* MAINFUNC */
#endif /* SP3_11749339 */

#ifndef SP3_11701283
#  define SP3_11701283  /* lupmo01: 3/4/02 */
#  ifdef MAINFUNC
     static char * __SP3_11701283 = "@(#)  SP3_11701283 Enabled\n" ;
#  endif /* MAINFUNC */
#endif /* SP3_11701283 */

#ifndef SP3_11657406
#  define SP3_11657406  /* liusi02: Feb 14, 2002 */
#  ifdef MAINFUNC
     static char * __SP3_11657406 = "@(#)  SP3_11657406 Enabled\n" ;
#  endif /* MAINFUNC */
#endif /* SP3_11657406 */

#ifndef BEB_11706947
#define  BEB_11706947  /* chach07 */
#  ifdef MAINFUNC
     static char * __BEB_11706947 = "@(#)  BEB_11706947 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_SPEEDUP_LOCALRPC_CALLS
#define BEB_SPEEDUP_LOCALRPC_CALLS  /* chach07 */
#  ifdef MAINFUNC
     static char * __BEB_SPEEDUP_LOCALRPC_CALLS = "@(#)  BEB_SPEEDUP_LOCALRPC_CALLS Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11546446
#define  BEB_11546446  /* hu$xu02 */
#  ifdef MAINFUNC
     static char * __BEB_11546446 = "@(#)  BEB_11546446 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11654503
#define BEB_11654503 // mocsc01 2/15/2002
#  ifdef MAINFUNC
     static char * __BEB_11654503 = "@(#)  BEB_11654503 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11700715
#define BEB_11700715 // mudja01 2/26/2002
#  ifdef MAINFUNC
     static char * __BEB_11700715 = "@(#)  BEB_11700715 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11676031
#define BEB_11676031 // podpa01 2/28/2002
#  ifdef MAINFUNC
     static char * __BEB_11676031 = "@(#)  BEB_11676031 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11697727 // lo$ja01 3/4/2002 Fix to message engine GP(asdbcli1.dll)
#define BEB_11697727 // lo$ja01 duplicate of 11719458
#  ifdef MAINFUNC
     static char * __BEB_11697727 = "@(#)  BEB_11697727 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef	BEB_11754737
#define BEB_11754737
#ifdef MAINFUNC
	 static char * __BEB_11754737 = "@(#)	BEB_11754737 Enabled\n" ;
#endif
#endif

#ifndef BEB_11758164 // GP in asagent when backing up NDS jobs.
#define BEB_11758164 // venkr01 3/6/2002 asbackup/ascompar/asrestor
#  ifdef MAINFUNC
     static char * __BEB_11758164 = "@(#)  BEB_11758164 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11762312 //
#define BEB_11762312 // RAHAW01 3/7/2002 AGUIEXC.DLL
#  ifdef MAINFUNC
     static char * __BEB_11762312 = "@(#)  BEB_11762312 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11766239 //
#define BEB_11766239 // RAHAW01 3/7/2002 ASPATCH.DLL
#  ifdef MAINFUNC
     static char * __BEB_11766239 = "@(#)  BEB_11766239 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef SP3_11670260
#  define SP3_11670260  /* diael01: 3/7/02 */
#  ifdef MAINFUNC
     static char * __SP3_11670260 = "@(#)  SP3_11670260 Enabled\n" ;
#  endif /* MAINFUNC */
#endif /* SP3_11670260 */

#ifndef SP3_11719209
#  define SP3_11719209  /* diael01: 3/7/02 */
#  ifdef MAINFUNC
     static char * __SP3_11719209 = "@(#)  SP3_11719209 Enabled\n" ;
#  endif /* MAINFUNC */
#endif /* SP3_11719209 */

#ifndef BEB_11765545 //
#define BEB_11765545 // MURRA02 3/7/2002 ASM_TOOL.DLL
#  ifdef MAINFUNC
     static char * __BEB_11765545 = "@(#)  BEB_11765545 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11767869 //
#define BEB_11767869 // ZITJA01 3/7/2002 DBAXCHG2.DLL
#  ifdef MAINFUNC
     static char * __BEB_11767869 = "@(#)  BEB_11767869 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11758815
#define BEB_11758815 // mocsc01 03-08-2002
#  ifdef MAINFUNC
     static char * __BEB_11758815 = "@(#)  BEB_1178815 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

//#endif /* SP3 */

//-----------------------------------------------------

// Add fixes that needs to go into SP4
#if defined(SP4) || defined(_SP4)


#ifndef BEB_11809172_02
#define BEB_11809172_02	/*  Sunder Start 4/1/2002 */
#ifdef MAINFUNC
     static char * __BEB_11809172_02 = "@(#)  BEB_11809172_02 Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_GIS_PROTOCOL_ERROR
#define BEB_GIS_PROTOCOL_ERROR	/* Also fixes BEB_11836494 */
#ifdef MAINFUNC
     static char * __BEB_GIS_PROTOCOL_ERROR = "@(#)  BEB_GIS_PROTOCOL_ERROR Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_TID_ENHANCEMENT
#define BEB_TID_ENHANCEMENT	/*  */
#ifdef MAINFUNC
     static char * __BEB_TID_ENHANCEMENT = "@(#)  BEB_TID_ENHANCEMENT Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11710739
#define BEB_11710739	/*  */
#ifdef MAINFUNC
     static char * __BEB_11710739 = "@(#)  BEB_11710739 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11802180
#define BEB_11802180 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11802180  = "@(#)  BEB_11802180  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11789229
#define BEB_11789229 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11789229  = "@(#)  BEB_11789229  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11799921
#define BEB_11799921 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11799921  = "@(#)  BEB_11799921  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11719291
#define BEB_11719291 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11719291  = "@(#)  BEB_11719291  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11798061
#define BEB_11798061 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11798061  = "@(#)  BEB_11798061  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_NDMP_TRACE
#define BEB_NDMP_TRACE 	/* 4/18/02 - GP in TLO setup */
#ifdef MAINFUNC
     static char * __BEB_NDMP_TRACE  = "@(#)  BEB_NDMP_TRACE  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11718617
#define BEB_11718617  	/*  */
#ifdef MAINFUNC
     static char * __BEB_11718617   = "@(#)  BEB_11718617   Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11632720
#define BEB_11632720 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11632720  = "@(#)  BEB_11632720  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11731012
#define BEB_11731012 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11731012  = "@(#)  BEB_11731012  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11723839
#define BEB_11723839 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11723839  = "@(#)  BEB_11723839  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11727230
#define BEB_11727230 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11727230  = "@(#)  BEB_11727230  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11754533
#define BEB_11754533 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11754533  = "@(#)  BEB_11754533  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11718810
#define BEB_11718810 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11718810  = "@(#)  BEB_11718810  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11791167
#define BEB_11791167 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11791167  = "@(#)  BEB_11791167  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11661270
#define BEB_11661270 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11661270  = "@(#)  BEB_11661270  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11695272
#define BEB_11695272 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11695272  = "@(#)  BEB_11695272  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11726436
#define BEB_11726436 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11726436  = "@(#)  BEB_11726436  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11665784
#define BEB_11665784 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11665784  = "@(#)  BEB_11665784  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11619670
#define BEB_11619670 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11619670  = "@(#)  BEB_11619670  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11640276
#define BEB_11640276 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11640276  = "@(#)  BEB_11640276  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11637676
#define BEB_11637676 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11637676  = "@(#)  BEB_11637676  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11633959
#define BEB_11633959 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11633959  = "@(#)  BEB_11633959  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11643241
#define BEB_11643241 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11643241  = "@(#)  BEB_11643241  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11702751
#define BEB_11702751 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11702751  = "@(#)  BEB_11702751  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11643018
#define BEB_11643018 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11643018  = "@(#)  BEB_11643018  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11814152
#define BEB_11814152 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11814152  = "@(#)  BEB_11814152  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11823767
#define BEB_11823767 	/*  */
#ifdef MAINFUNC
     static char * __BEB_11823767  = "@(#)  BEB_11823767  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_GIS_MAKEUP
#define BEB_GIS_MAKEUP 	/*  Sunder End 4/1/2002 */
#ifdef MAINFUNC
     static char * __BEB_GIS_MAKEUP  = "@(#)  BEB_GIS_MAKEUP  Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11707502
#define BEB_11707502	/* 3/14/2002 */
#ifdef MAINFUNC
     static char * __BEB_11707502 = "@(#)  BEB_11707502 Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_SP4_TLO_Enhancement
#define  BEB_SP4_TLO_Enhancement  /* boest02 */
#  ifdef MAINFUNC
     static char * __BEB_SP4_TLO_Enhancement = "@(#)  BEB_SP4_TLO_Enhancement Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11781133
#define  BEB_11781133  /* liusi02 */
#  ifdef MAINFUNC
     static char * __BEB_11781133 = "@(#)  BEB_11781133 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11748975
#define  BEB_11748975  /* Lempi01 */
#  ifdef MAINFUNC
     static char * __BEB_11748975 = "@(#)  BEB_11748975 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef _DR_SCSI_DESC_10_0_SP4
#define _DR_SCSI_DESC_10_0_SP4  /* choso01 */
#  ifdef MAINFUNC
     static char * __DR_SCSI_DESC_10_0_SP4 = "@(#)  _DR_SCSI_DESC_10_0_SP4 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef _DR_MINOR_FIX_10_0_SP4
#define _DR_MINOR_FIX_10_0_SP4  /* choso01 */
#  ifdef MAINFUNC
     static char * __DR_MINOR_FIX_10_0_SP4 = "@(#)  _DR_MINOR_FIX_10_0_SP4 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11764685
#define BEB_11764685  // added to keep serial number - chuku01
#  ifdef MAINFUNC
     static char * __BEB_11764685 = "@(#)  BEB_11764685 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_NDMP_V2
#define BEB_NDMP_V2   // venkr01
#ifdef MAINFUNC
   static char * __BEB_NDMP_V2 = "@(#)   BEB_NDMP_V2 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_NDMP_TRACE
#define BEB_NDMP_TRACE   // venkr01
#ifdef MAINFUNC
   static char * __BEB_NDMP_TRACE = "@(#)   BEB_NDMP_TRACE Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_SP4_LOAD_LTO
#define BEB_SP4_LOAD_LTO   // Steven Hwang, hwapi01. 2-16-2002
#ifdef MAINFUNC
   static char * __BEB_SP4_LOAD_LTO = "@(#)   BEB_SP4_LOAD_LTO Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_SQL_REMOTE_MUL_JOBS	//Chandra. Added to enable SQL REMOTE MULTIPLE JOBS
#define BEB_SQL_REMOTE_MUL_JOBS
#ifdef MAINFUNC
   static char * __BEB_SQL_REMOTE_MUL_JOBS = "@(#)   BEB_SQL_REMOTE_MUL_JOBS Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11746466
#define  BEB_11746466  /* liusi02 */
#  ifdef MAINFUNC
     static char * __BEB_11746466 = "@(#)  BEB_11746466 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11721427  // ADDED TO FIX DEVICE OPEN PROBLEM ON IMPORT DURING SPAN
#define BEB_11721427  // MASAL03 2-22-2002
#ifdef MAINFUNC
   static char * __BEB_11721427 = "@(#)   BEB_11721427 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11721420  // ADDED TO FIX FOR IMPORT
#define BEB_11721420  // MASAL03 2-27-2002
#ifdef MAINFUNC
   static char * __BEB_11721420 = "@(#)   BEB_11721420 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11627622 // THIS WAS DONE TO TAKE CARE OF A NAMESPACE MISMATHCING WHEN USING TERMINAL SERVICES
#define BEB_11627622 // MASAL03 2-20-2002
#ifdef MAINFUNC
   static char * __BEB_11627622 = "@(#)   BEB_11627622 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11700711  //goyra03 2-19-2002
#define BEB_11700711
#ifdef MAINFUNC
   static char * __BEB_11700711 = "@(#)   BEB_11700711 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11723800  //murra02 2-19-2002
#define BEB_11723800
#ifdef MAINFUNC
   static char * __BEB_11723800 = "@(#)   BEB_11723800 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// #ifndef BEB_SQLPERF  // For SQL mini agent implementation
// #define BEB_SQLPERF  // zhoru02, 2-19-2002
// #ifdef MAINFUNC
//   static char * __BEB_SQLPERF = "@(#)   BEB_SQLPERF Enabled\n" ;
// #endif /* MAINFUNC */
// #endif


#ifndef BEB_11178882
#define  BEB_11178882  /* mocsc01 2/20/2002 origin: sp4 */
#ifdef MAINFUNC
     static char * __BEB_11178882 = "@(#)  BEB_11178882 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11728583
#define BEB_11728583	// Kimwo01 2/20/2002 SP4 dbtosql.exe(sqltable.c)
#ifdef MAINFUNC
     static char * __BEB_11728583 = "@(#)  BEB_11728583 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11727230	// For stopping job through terminal services while spanning tape
#define BEB_11727230	// linyu04 2/21/2002 SP4 asbackup.dll(backutil.c) astask.dll(jobtapef.c)
#ifdef MAINFUNC
     static char * __BEB_11727230 = "@(#)  BEB_11727230 Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11731012	// Fix incorrent available drive under driver offline, driver active, changer driver assign to diffirent group, etc.
#define BEB_11731012	// linyu04 2/25/2002 2/21/2002 SP4 asbackup.dll(MultiProcessBackup.c, MultiProcessBackup.h)
#ifdef MAINFUNC
     static char * __BEB_11731012 = "@(#)  BEB_11731012 Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_11723839	// Fix only one drive avalable still do submit multistream subjob problem
#define BEB_11723839	// linyu04 2/25/2002 SP4 asbackup.dll(MultiProcessBackup.c, MultiProcessBackup.h)
#ifdef MAINFUNC
     static char * __BEB_11723839 = "@(#)  BEB_11723839 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11632720	// Display trace message for cluster failover missing node
#define BEB_11632720	// linyu04 3/04/2002 SP4 asbackup.dll(inteleave.c)
#ifdef MAINFUNC
     static char * __BEB_11632720 = "@(#)  BEB_11632720 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11650292	// in RS mgr, NDS packageing in func CDBTreeWnd::GetFullPathCTree()
#define BEB_11650292	// was not correct.
#ifdef MAINFUNC
     static char * __BEB_11650292 = "@(#)  BEB_11650292 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11723750	// mocsc01 2/25/2002 allow abort during retry
#define BEB_11723750
#ifdef MAINFUNC
	static char * __BEB_11723750 = "@(#)  BEB_11723750 Enabled\n" ;
#endif
#endif

#ifndef BEB_11634586	// gonbo01 2/26/2002 added support for Windows 2000 SP2 integrated CD
#define BEB_11634586
#ifdef MAINFUNC
	static char * __BEB_11634586 = "@(#)  BEB_11634586 Enabled\n" ;
#endif
#endif

// NDS packaging issue -leech09 2/25/02
#ifndef BEB_11739901
#define BEB_11739901
#ifdef MAINFUNC
	static char * __BEB_11739901 = "@(#)  BEB_11739901 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11744528
#define BEB_11744528
#ifdef MAINFUNC
	static char * __BEB_11744528 = "@(#)  BEB_11744528 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11729020
#define BEB_11729020  //zhoru02, fix group rename issue.
#ifdef MAINFUNC
     static char * __BEB_11729020 = "@(#)   BEB_11729020 Enabled\n" ;
#endif
#endif

#ifndef BEB_11381334
#define BEB_11381334  //zhoru92, clear new home slot reserve flag.
#ifdef MAINFUNC
     static char * __BEB_11381334 = "@(#)   BEB_11381334 Enabled\n";
#endif
#endif

#ifndef BEB_11727515
#define BEB_11727515  // mocsc01 3/1/2002 : win2k_sp2_srp1_bwfix (asrestor.dll)
#ifdef MAINFUNC
     static char * __BEB_11727515 = "@(#)   BEB_11727515 Enabled\n";
#endif
#endif

#ifndef BEB_11381334
#define BEB_11381334  // Allow blank tape by default if first tape full
#ifdef MAINFUNC
     static char * __BEB_11381334 = "@(#)   BEB_11381334\n";
#endif
#endif

#ifndef TNGASO_HANDLE_RPC_ERRORS
#define TNGASO_HANDLE_RPC_ERRORS    /* chach07 3/1/02: Fix from Unix */
#ifdef MAINFUNC
     static char * __TNGASO_HANDLE_RPC_ERRORS = "@(#)   TNGASO_HANDLE_RPC_ERRORS Enabled\n";
#endif
#endif /* TNGASO_HANDLE_RPC_ERRORS */

#ifndef TNGASO_11738730
#define TNGASO_11738730 /* chach07 : 3/1/02 : Fix GP for ENOSPC */
#ifdef MAINFUNC
     static char * __TNGASO_11738730 = "@(#)   TNGASO_11738730 Enabled\n";
#endif
#endif /*TNGASO_11738730 */

#ifndef TNGASO_11670766
#define TNGASO_11670766 /* chach07 : 3/1/02 : Merge from Unix */
#ifdef MAINFUNC
     static char * __TNGASO_11670766 = "@(#)   TNGASO_11670766 Enabled\n";
#endif
#endif /*TNGASO_11670766 */

#ifndef TNGASO_11442878
#define TNGASO_11442878 /* chach07 : 3/1/02 : Merge from Unix */
#ifdef MAINFUNC
     static char * __TNGASO_11442878 = "@(#)   TNGASO_11442878 Enabled\n";
#endif
#endif /*TNGASO_11442878  */

#ifndef BEB_11710638
#define BEB_11710638 /* mocsc01 : 3/4/02 : sharing and lock violations during backup mark job as incomplete */
#ifdef MAINFUNC
     static char * __BEB_11710638 = "@(#)   BEB_11710638 Enabled\n";
#endif
#endif /*BEB_11710638  */

#ifndef BEB_11740760
#define BEB_11740760 /* boest02 : 3/4/02 : GP during tape cleaning */
#ifdef MAINFUNC
     static char * __BEB_11740760 = "@(#)   BEB_11740760 Enabled\n";
#endif
#endif /*BEB_11740760  */

#ifndef BEB_11750379	// Set default to OK for tape promping
#define BEB_11750379	// chuku01 3/04/2002 SP4 astask.dll(jobtapef.h)
#ifdef MAINFUNC
     static char * __BEB_11750379 = "@(#)  BEB_11750379 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11754109	// Fix estimate for registry - no estimate
#define BEB_11754109	// chuku01 SP4 asbackup.dll
#ifdef MAINFUNC
     static char * _BEB_11754109 = "@(#)  BEB_11754109 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11758292
#define BEB_11758292	// hwapi01 SP4 tapeeng.dll
#ifdef MAINFUNC
     static char * _BEB_11758292 = "@(#)  BEB_11758292 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11760869
#define BEB_11760869	// chach07 SP4 asopen.dll/loggerd/caloggerd.exe
#ifdef MAINFUNC
     static char * _BEB_11760869 = "@(#)  BEB_11760869 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11735775	// For restricted users NTAG can't get own location due to a bad handle.
#define BEB_11735775	// simmi04 SP4 ntagent.dll
#ifdef MAINFUNC
     static char * _BEB_11735775 = "@(#)  BEB_11735775 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11668784	// TCW FIX W/ LOOPING ON "UPDATING" TO "UNRECOGNIZED MEDIA".
#define BEB_11668784	// masal03 SP4 ndmpdev.dll
#ifdef MAINFUNC
     static char * _BEB_11668784 = "@(#)  BEB_11668784 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11765472	// For restore jobs, cancel command during span operation generates annoying messages in activity log.
#define BEB_11765472	// simmi04 SP4 ntagent.dll & asrestor.dll
#ifdef MAINFUNC
     static char * _BEB_11765472 = "@(#)  BEB_11765472 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11751171
#define BEB_11751171	// chach07 SP4 as6rpc.dll, caserved.exe, catirpc.dll & catirpc.exe
#ifdef MAINFUNC
     static char * _BEB_11751171 = "@(#)  BEB_11751171 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11765781	// Supports span tape backup and restore for informix agent.
#define BEB_11765781	// hu$xu02 SP4 dbabatch
#ifdef MAINFUNC
     static char * _BEB_11765781 = "@(#)  BEB_11765781 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11761399
#define BEB_11761399	// goyra03 3/8/2002 SP4
#ifdef MAINFUNC
     static char * __BEB_11761399 = "@(#)  BEB_11761399 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11657766
#define BEB_11657766	// goyra03 3/8/2002 SP4
#ifdef MAINFUNC
     static char * __BEB_11657766 = "@(#)  BEB_11657766 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11754533	// Fix job property showing wrong source info such as "Source(Node 19/17)"
#define BEB_11754533	// linyu04 3/08/2002 SP4 asbackup.dll(backdb.c)
#ifdef MAINFUNC
     static char * __BEB_11754533 = "@(#)  BEB_11754533 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11707256
#define BEB_11707256	// li$ya01 3/11/2002 SP4
#ifdef MAINFUNC
     static char * __BEB_11707256 = "@(#)  BEB_11707256 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11707896
#define BEB_11707896	// linbr06 3/11/2002 SP4
#ifdef MAINFUNC
     static char * __BEB_11707896 = "@(#)  BEB_11707896 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11718810	// UNIXFSAGENT NOT LOGGING SUMMARY
#define BEB_11718810	// linyu04 3/15/2002 SP4 asbackup.dll(backio.c)
#ifdef MAINFUNC
     static char * __BEB_11718810 = "@(#)  BEB_11718810 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11784984	// Report agenr error as warning
#define BEB_11784984	// chuku01 3/15/2002 SP4 asbackup.dll(backagnt.c)
#ifdef MAINFUNC
     static char * __BEB_11784984 = "@(#)  BEB_11784984 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11716055	// FAIL TO CONNECT TCP
#define BEB_11716055	// LI$YA01 3/18/2002 SP4 asbackup.dll,  asrestore.dll(backdsa2.c, RestExchTcp.c)
#ifdef MAINFUNC
     static char * __BEB_11716055 = "@(#)  BEB_11716055 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11788387	// Enterprise Job Status Report - dialog "Preffered Servers for Report": you can press "Security" button even if there is no selection in "Preffered Servers" list ).
#define BEB_11788387	// MURRA02 3/18/2002 SP4 ASM_RP.DLL
#ifdef MAINFUNC
     static char * __BEB_11788387 = "@(#)  BEB_11788387 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11794414
#define BEB_11794414	// HWAPI01 3/19/2002 SP4 TAPELIST.DLL & TAPEENG.dll
#ifdef MAINFUNC
     static char * __BEB_11794414 = "@(#)  BEB_11794414 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11788864
#define BEB_11788864	// MURRA02 3/19/2002 SP4 ASM_TOOL.DLL
#ifdef MAINFUNC
     static char * __BEB_11788864 = "@(#)  BEB_11788864 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11788763
#define BEB_11788763	// MINZH01 3/20/2002 SP4 SYMAGENT.DLL
#ifdef MAINFUNC
     static char * __BEB_11788763 = "@(#)  BEB_11788763 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11718714
#define BEB_11718714	// MINZH01 3/20/2002 SP4 DBAORA7.DLL
#ifdef MAINFUNC
     static char * __BEB_11718714 = "@(#)  BEB_11718714 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11795119
#define BEB_11795119	// ZITJA01 3/20/2002 SP4 DSA.DLL & DBAXCHG2.DLL
#ifdef MAINFUNC
     static char * __BEB_11795119 = "@(#)  BEB_11795119 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11762252
#define BEB_11762252	// MURRA02 3/20/2002 SP4 ASM_TOOL.DLL
#ifdef MAINFUNC
     static char * __BEB_11762252 = "@(#)  BEB_11762252 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11799921
#define BEB_11799921	// CHUKU01 3/20/2002 SP4
#ifdef MAINFUNC
     static char * __BEB_11799921 = "@(#)  BEB_11799921 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11798061
#define BEB_11798061	// CHUKU01 3/20/2002 SP4
#ifdef MAINFUNC
     static char * __BEB_11798061 = "@(#)  BEB_11798061 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11761193
#define BEB_11761193	// LIUSI02 3/20/2002 SP4 isoimg.dll
#ifdef MAINFUNC
     static char * __BEB_11761193 = "@(#)  BEB_11761193 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11805368	// katsh02 Submit a backup job w/ preferred shares connection as source. Security box does not show up.
#define BEB_11805368	// 4/12/02 - the security box was not showing for Machine share
#ifdef MAINFUNC
     static char * __BEB_11805368 = "@(#)   BEB_11805368 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11788919
#define BEB_11788919	// HWAPI01 tapeeng.dll SP4 3/21/02
#ifdef MAINFUNC
     static char * __BEB_11788919 = "@(#)   BEB_11788919 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11762472	// -leech09 When we modifty job, the SQL option don't get save for SQL DB Object Node in tree view
#define BEB_11762472
#ifdef MAINFUNC
     static char * __BEB_11762472  = "@(#)   BEB_11762472  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11611812	// Re-scheduling 'time' the GFS rotation job fixed. In fix binary, the time is the one specified by user,  default is submitted time.
#define BEB_11611812	// GOYRA03 ca_backup.exe SP4 3/21/02
#ifdef MAINFUNC
     static char * __BEB_11611812 = "@(#) BEB_11611812 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// -leech09 	Fix GP when they click ok on Security Info, while they are trying to submit job.
#ifndef BEB_11806209
#define BEB_11806209
#ifdef MAINFUNC
     static char * __BEB_11806209 = "@(#)   BEB_11806209 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// -leech09 	if user did not select anything in tree in RS dest , they could not submit job
#ifndef BEB_11806256
#define BEB_11806256
#ifdef MAINFUNC
     static char * __BEB_11806256  = "@(#)   BEB_11806256  Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11386544_3	// CHOSO01 3/25/02
#define BEB_11386544_3	// Better logic for installing network adapters without drivers during DR.
#ifdef MAINFUNC
     static char * __BEB_11386544_3 = "@(#)   BEB_11386544_3 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// -leech09 	when user click cancel on security Dialog, tree will not continue to expand
#ifndef BEB_11719467
#define BEB_11719467
#ifdef MAINFUNC
     static char * __BEB_11719467 = "@(#)   BEB_11719467 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11802296	// KIMJU01 3/25/02
#define BEB_11802296	// Fix a problem that BEB is not able to delete job log files older than specified pruning cutoff days.
#ifdef MAINFUNC
     static char * __BEB_11802296= "@(#)   BEB_11802296 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11623301	// zitja01 3/25/02
#define BEB_11623301	// Allows for automated mailboxes on exchange 2000 to be immediately used for user creation.
#ifdef MAINFUNC
     static char * __BEB_11623301 = "@(#)  BEB_11623301 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11810759	// GONBO01 3/25/02
#define BEB_11810759
#ifdef MAINFUNC
     static char * __BEB_11810759 = "@(#) BEB_11810759 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// -leech09 	RS by Query, cell too short to display whole path, and user not able to enlarge it, fix allow user to enlarge cell
#ifndef BEB_11802322
#define BEB_11802322
#ifdef MAINFUNC
     static char * __BEB_11802322 = "@(#) BEB_11802322 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// -leech09 	after user Modifty job for SQL agent, SQL agent has NT agent password if they did not expand SQL agent, if they expand SQL agent, the correct SQL agent security is set
#ifndef BEB_11748300
#define BEB_11748300
#ifdef MAINFUNC
     static char * __BEB_11748300 = "@(#)   BEB_11748300 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// -GOYRA03 	Resource error fixed.
#ifndef BEB_11809519
#define BEB_11809519
#ifdef MAINFUNC
     static char * __BEB_11809519 = "@(#) BEB_11809519 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// -leech09 	if a NAS Session does not have finish Status, we do not show it in RS Mgr
#ifndef BEB_11715886
#define BEB_11715886
#ifdef MAINFUNC
     static char * __BEB_11715886  = "@(#)   BEB_11715886 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// MURRA02  3/26/02	asres.dll    Changed "On-line" with "Online"
#ifndef BEB_11814687
#define BEB_11814687
#ifdef MAINFUNC
     static char * __BEB_11814687  = "@(#)  BEB_11814687 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// GOYRA03  3/26/02	ascore.dll	      Connect to BEB Server via a remote BEB Manger causes Message Engine exception fixed.
#ifndef BEB_11809809
#define BEB_11809809
#ifdef MAINFUNC
     static char * __BEB_11809809  = "@(#) BEB_11809809 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11789229
#define BEB_11789229	/* chuku01 3/28/2002 */
#ifdef MAINFUNC
     static char * __BEB_BEB_11789229 = "@(#)  BEB_11789229 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11802180
#define BEB_11802180	/* chuku01 3/28/2002 */
#ifdef MAINFUNC
     static char * __BEB_11802180 = "@(#)  BEB_11802180 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11800463	/* Fixed tape engine always initializing the changer with bar-code support no matter what. */
#define BEB_11800463	/* LO$JA01 3/28/2002 */
#ifdef MAINFUNC
     static char * __BEB_11800463 = "@(#)  BEB_11800463 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11718661	/* Remote restore Oracle Database to different location. */
#define BEB_11718661	/* HU$XU02 3/28/2002 */
#ifdef MAINFUNC
     static char * __BEB_11718661 = "@(#)  BEB_11718661 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11802374	/* validate servernames. */
#define BEB_11802374	/* CHACH07 3/28/2002 */
#ifdef MAINFUNC
     static char * __BEB_11802374 = "@(#)  BEB_11802374 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_CENTRALDB	// KIMJU01 3/28/02
#define  BEB_CCB_CENTRALDB 	// Fix a problem that fails on logging to BEB database.
#ifdef MAINFUNC
     static char * __BEB_CCB_CENTRALDB = "@(#)  BEB_CCB_CENTRALDB Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11817244	/* Fix a hanging problem on SQL6.5 DBAgent, introduced by SP3 (also fixes BEB_11836503). */
#define BEB_11817244	/* DIAEL01 3/28/2002 */
#ifdef MAINFUNC
     static char * __BEB_11817244 = "@(#)  BEB_11817244 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11685951	/* setupcls.dll & asetgui.dll */
#define BEB_11685951	/* Awsafur 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_11685951 = "@(#)  BEB_11685951 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11783592	/* ca_devmgr.exe */
#define BEB_11783592	/* Rahul 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_11783592 = "@(#)  BEB_11783592 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_NDMP_BLOCK_FIX	/* tapeeng.dll */
#define BEB_NDMP_BLOCK_FIX	/* Alan Maslar 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_NDMP_BLOCK_FIX = "@(#)  BEB_NDMP_BLOCK_FIX Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11675798	/* ndmpdev.dll & ndmpc.dll */
#define BEB_11675798	/* Alan Maslar 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_11675798 = "@(#)  BEB_11675798 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_SP4_UDP_REMOVE	/* ubrowser.dll & ndmpc.dll */
#define BEB_SP4_UDP_REMOVE	/* Mihai Sima 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_SP4_UDP_REMOVE = "@(#)  BEB_SP4_UDP_REMOVE Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_GIS_SKIP_MERGECAT	/* asbackup.dll */
#define BEB_GIS_SKIP_MERGECAT	/* James Chueh 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_GIS_SKIP_MERGECAT = "@(#)  BEB_GIS_SKIP_MERGECAT Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11719222	/* pagjc */
#define BEB_11719222	/* Xiadong 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_11719222 = "@(#)  BEB_11719222 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11730990	/* orapaadp */
#define BEB_11730990	/* Xiadong 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_11730990 = "@(#)  BEB_11730990 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_NEW_LOG_SCHEMA	/* ntagent.dll */
#define BEB_NEW_LOG_SCHEMA	/* Mihai Sima 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_NEW_LOG_SCHEMA = "@(#)  BEB_NEW_LOG_SCHEMA Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11701339	/* ntagent.dll */
#define BEB_11701339	/* Mihai Sima 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_11701339 = "@(#)  BEB_11701339 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11773687	/* asbackup.dll */
#define BEB_11773687	/* Brian Linton 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_11773687 = "@(#)  BEB_11773687 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11708025	/* asmerge.dll */
#define BEB_11708025	/* Gordon 4/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_11708025 = "@(#)  BEB_11708025 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11788868	/* In backup manager browser under SQL cluster virtual node, only one SQL agent entry should be displayed. */
#define BEB_11788868	/* HU$XU02 4/3/2002 */
#ifdef MAINFUNC
     static char * __BEB_11788868 = "@(#)  BEB_11788868 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11833153	/* Fix crash which happens when job packages a node which the DBagent removed from it later on. */
#define BEB_11833153	/* DIAEL01 4/3/2002 */
#ifdef MAINFUNC
     static char * __BEB_11833153 = "@(#)  BEB_11833153 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11833329	/* queue.dll,mediasvr.exe */
#define BEB_11833329	/* CHACH07 4/4/2002 */
#ifdef MAINFUNC
     static char * __BEB_11833329 = "@(#)  BEB_11833329 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11839493	/* ndmpc.dll - gp connecting to the NAS NDMP 2 server */
#define BEB_11839493	/* VENKR01 4/4/2002 */
#ifdef MAINFUNC
     static char * __BEB_11839493 = "@(#)  BEB_11839493 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11833156	/* asres.dll - about box fix to reflect SP4 */
#define BEB_11833156	/* KATSH02 4/4/2002 */
#ifdef MAINFUNC
     static char * __BEB_11833156 = "@(#)  BEB_11833156 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11836484	// After no device available, continue connecting to media.(GIS issue)
#define BEB_11836484	// linyu04 4/03/2002 SP4 asbackup.dll(tape_com.c)
#ifdef MAINFUNC
     static char * __BEB_11836484 = "@(#)  BEB_11836484 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11712860	/* ALL ASOPEN MODULES */
#define BEB_11712860	/* huach01 4/8/2002. 4/10/02 - Fixed for BEB_11712860 and EUC-SJIS conversion */
#ifdef MAINFUNC
     static char * __BEB_11712860 = "@(#)  BEB_11712860 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11730410	/* univagent.exe - creates exception log under univagent folder. */
#define BEB_11730410	/* SIMMI04 4/8/2002 */
#ifdef MAINFUNC
     static char * __BEB_11730410 = "@(#)  BEB_11730410 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11835590	/* ASDBRES.DLL - Fix a problem that BEB setup sql displays wrong string message. */
#define BEB_11835590	/* LO$JA01 4/8/2002 , asdbres.dll - 4/10/02 Fix a problem that BEB setup sql displays wrong string message */
#ifdef MAINFUNC
     static char * __BEB_11835590 = "@(#)  BEB_11835590 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11708293	/* ASCORE.DLL - DELETING MAKEUP JOB twice for GFS Rotation Job fixed. */
#define BEB_11708293	/* GOYRA03 4/8/2002 */
#ifdef MAINFUNC
     static char * __BEB_11708293 = "@(#)  BEB_11708293 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11827998	/* NASAgent.dll - Job not stopping. */
#define BEB_11827998	/* VENKR01 4/8/2002 */
#ifdef MAINFUNC
     static char * __BEB_11827998 = "@(#)  BEB_11827998 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11843222	/* NASAgent.dll */
#define BEB_11843222	/* VENKR01 4/9/2002 */
#ifdef MAINFUNC
     static char * __BEB_11843222 = "@(#)  BEB_11843222 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11838130	/* Ubrowser.dll - Junk Characters in Tracing calls and creation of zero size files */
#define BEB_11838130	/* VENKR01 4/9/2002 */
#ifdef MAINFUNC
     static char * __BEB_11838130 = "@(#)  BEB_11838130 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11840102	/* tapeeng.dll - Fixed a losing Virtual Library configuration issue */
#define BEB_11840102	/* hwapi01 4/10/2002 */
#ifdef MAINFUNC
     static char * __BEB_11840102 = "@(#)  BEB_11840102 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11783505
#define  BEB_11783505 /* liusi02 - Mediasvr.exe : ca_devmgr.exe - groupinfo error on a changer device */
#  ifdef MAINFUNC
     static char * __BEB_11783505 = "@(#)  BEB_11783505 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11846727
#define  BEB_11846727 /* LO$JA01 - 4/10/02 - asdbcli1.dll The setupsql doesn't work on Auto configuration when prompted for overwrite database, if you select "yes" the setup will fail.*/
#  ifdef MAINFUNC
     static char * __BEB_11846727 = "@(#)  BEB_11846727 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_ENABLE_SAN_DEVICES	/* tapeeng.dll */
#define BEB_ENABLE_SAN_DEVICES	/* hwapi01 4/10/2002 Allow users to use SAN device(s) (fibre-connected) locally without installing SAN Option */
#ifdef MAINFUNC
     static char * __BEB_ENABLE_SAN_DEVICES = "@(#)  BEB_ENABLE_SAN_DEVICES Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11853622
#define  BEB_11853622 /* MURRA02 - 4/10/02 - ADMINRES.DLL Show SP4 and 2002 in About Box for NT Agent*/
#  ifdef MAINFUNC
     static char * __BEB_11853622 = "@(#)  BEB_11853622 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11853085
#define  BEB_11853085 /* MURRA02 - 4/10/02 - ADMINRES.DLL Show SP4 and 2002 in About Box for NT Agent*/
#  ifdef MAINFUNC
     static char * __BEB_11853085 = "@(#)  BEB_11853085 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11840436
#define  BEB_11840436 /* GOYRA03 - 4/12/02 - CMD DOES NOT EXIST IN DATABASE. Ca-restore cmdline couldn't find sessions to restore in DB if the DB is SQL, 4/18/02 - ca_restore command line failed to restore due to the fact that it doesn't get the qfa number.*/
#  ifdef MAINFUNC
     static char * __BEB_11840436 = "@(#)  BEB_11840436 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11853367
#define  BEB_11853367 /* Shreesh Kattepur  - 4/12/02 - When you expand the source tree on the MS SQL server you got an error.*/
#  ifdef MAINFUNC
     static char * __BEB_11853367 = "@(#)  BEB_11853367 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11667747
#define  BEB_11667747 /* Steven Hwang  - 4/12/02 - Unable to access a tape inside a drive, locked by UNIX, to continue a backup job.*/
#  ifdef MAINFUNC
     static char * __BEB_11667747 = "@(#)  BEB_11667747 Enabled\n" ;
#  endif /* MAINFUNC */
#endif

#ifndef BEB_11809172
#define BEB_11809172	/*  Stephen Boettcher - 4/12/2002 - Fixed a problem with a GFS job trying to backup to a drive that needs cleaning.  The drive will be marked offline, but the GFS retries will continue to try and use it... */
#ifdef MAINFUNC
     static char * __BEB_11809172 = "@(#)  BEB_11809172 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11849757
#define BEB_11849757 	// -lee - 4/17/2002, fix security box was not showing security info for machine share
#ifdef MAINFUNC
     static char * __BEB_11849757 = "@(#)   BEB_11849757 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11866489
#define BEB_11866489 	// -Jack Zito - 4/17/2002,fix for user creation.
#ifdef MAINFUNC
     static char * __BEB_11866489 = "@(#)   BEB_11866489 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11860080
#define BEB_11860080 	// -Bo Gong - 4/18/2002,This fixed has been verified on the multi-language platforms by CTL QA.
#ifdef MAINFUNC
     static char * __BEB_11860080 = "@(#)   BEB_11860080 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_DTCC_RAID1_FIX	/* raid.dll - Fixed Tapeengine GP when tapeengine fails to load tape into one of the RAID1 drives. */
#define BEB_DTCC_RAID1_FIX	/* Kunnumbrath Manden, Prakashbabu 4/19/2002 */
#ifdef MAINFUNC
     static char * __BEB_DTCC_RAID1_FIX = "@(#)  BEB_DTCC_RAID1_FIX Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_RAID_ON_DISTRIBUTED_SERVER	/* tapeeng.dll - Fixed "Unable to run job to any RAID Changer from Distributed NT Server" issue. */
#define BEB_RAID_ON_DISTRIBUTED_SERVER	/* Steven Hwang 4/19/2002 */
#ifdef MAINFUNC
     static char * __BEB_RAID_ON_DISTRIBUTED_SERVER = "@(#)  BEB_RAID_ON_DISTRIBUTED_SERVER Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_NO_DB_INSTALLED	/* asbackup.dll - Fixed job crash when Database Agent is installed but database is not installed. */
#define BEB_NO_DB_INSTALLED	/* Chueh, Kuang Ru - 4/19/2002 */
#ifdef MAINFUNC
     static char * __BEB_NO_DB_INSTALLED = "@(#)  BEB_NO_DB_INSTALLED Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11876801
#define BEB_11876801 	// -Bo Gong - 4/19/2002 - Drcreate.exe
#ifdef MAINFUNC
     static char * __BEB_11876801 = "@(#)   BEB_11876801 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_DRIFDLL_GP	/* drifdll.dll - Fixed gis GP. */
#define BEB_DRIFDLL_GP	/* Sid - 4/22/2002 */
#ifdef MAINFUNC
     static char * __BEB_DRIFDLL_GP = "@(#)  BEB_DRIFDLL_GP Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11751411	/* asbackup.dll & dbaxchg2.dll - Folder path not displayed when it can't be opened. */
#define BEB_11751411	/* Yangfang - 4/25/2002 */
#ifdef MAINFUNC
     static char * __BEB_11751411 = "@(#)  BEB_11751411 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11889351	/* asbackup.dll - Connect to IPC$ instead of Admin$ in backdsa. */
#define BEB_11889351	/* Yangfang - 4/25/2002, 5/8/02 - Fix remote Exchange Brick level backup */
#ifdef MAINFUNC
     static char * __BEB_11889351 = "@(#)  BEB_11889351 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_SQL_MSG_OVERFLOW	/* asbackup.dll - Asbackup crashwhen SQL Agent sends a msg of 260+ characters. */
#define BEB_SQL_MSG_OVERFLOW	/* Dana - 4/25/2002 */
#ifdef MAINFUNC
     static char * __BEB_SQL_MSG_OVERFLOW = "@(#)  BEB_SQL_MSG_OVERFLOW Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11880689	/* asbackup.dll - When spanning tape, if user cancels job, BEB should not continue backup of next session. */
#define BEB_11880689	/* James Chueh - 4/25/2002 */
#ifdef MAINFUNC
     static char * __BEB_11880689 = "@(#)  BEB_11880689 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_POSITION_ERROR	/* asbackup.dll - If BEB fails to position media to next session, BEB should not continue backup of next session. */
#define BEB_POSITION_ERROR	/* James Chueh - 4/25/2002 */
#ifdef MAINFUNC
     static char * __BEB_POSITION_ERROR = "@(#)  BEB_POSITION_ERROR Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_LOGON_FAILURE	/* dbasvr.exe - Failed to logon to dbagent machine or all the dbagents. */
#define BEB_LOGON_FAILURE	/* Daniel Lee - 4/30/2002 */
#ifdef MAINFUNC
     static char * __BEB_LOGON_FAILURE = "@(#)  BEB_LOGON_FAILURE Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_GIS_HANG		/* mergecat.exe - Fixed hanging problem in GIS due to locked session table. */
#define BEB_GIS_HANG		/* Jonathan Kim - 5/1/2002 */
#ifdef MAINFUNC
     static char * __BEB_GIS_HANG = "@(#)  BEB_GIS_HANG Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_MASTER_HUNG		/* asbackup.dll - Fix for master job hanging problem, slave job scheduling problem & master job cancellation problem. */
#define BEB_MASTER_HUNG		/* Gordon Zhou - 5/2/2002 */
#ifdef MAINFUNC
     static char * __BEB_MASTER_HUNG = "@(#)  BEB_MASTER_HUNG Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_LOGON_FAILURE1		/* asrestor.dll - The restore crashes if the RPC service returns error 1385 ('Logon failure: the user has not been granted the requested logon type at this computer.'). The work around is to give the user "Log on as a service right". Changed the asrestor.dll to handle all security errors. */
#define BEB_LOGON_FAILURE1		/* Elena Diaconu - 5/3/2002 */
#ifdef MAINFUNC
     static char * __BEB_LOGON_FAILURE1 = "@(#)  BEB_LOGON_FAILURE1 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_MULTISTREAM_DEBUG		/* asbackup .dll - The multistreaming backup crashes if the user set the debug mode. This bug is from SP3 when we changed the all DBAgent traces to be created in the LOG directory. Multistreaming is using the DBAgents traces but does not set the trace path to be in the LOG directory. The current fix fixes the crash, but 		for multistreaming, the traces will still be created in the Win32 directory.For this one we need a new bug for SP5 */
#define BEB_MULTISTREAM_DEBUG		/* Elena Diaconu - 5/3/2002 */
#ifdef MAINFUNC
     static char * __BEB_MULTISTREAM_DEBUG = "@(#)  BEB_MULTISTREAM_DEBUG Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_LOGON_FAILURE2		/* dbasvr.exe,dsa.dll - Fix potential GP of RPC Service for localization for Oracle & Exchange */
#define BEB_LOGON_FAILURE2		/* Elena Diaconu - 5/3/2002 */
#ifdef MAINFUNC
     static char * __BEB_LOGON_FAILURE2 = "@(#)  BEB_LOGON_FAILURE2 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_IFS_CRASH		/* dbasvr.exe, dsa.dll - Fix GP of RPC Service for IFS Exchange */
#define BEB_IFS_CRASH		/* Elena Diaconu - 5/6/2002 */
#ifdef MAINFUNC
     static char * __BEB_IFS_CRASH = "@(#)  BEB_IFS_CRASH Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11917412		/* nasagent.dll - Problems restoring SP3 sessions w/ SP4 builds.  Rolled back to 16K NDMP tape record size as default. */
#define BEB_11917412		/* Sarad Thapa - 5/7/2002 */
#ifdef MAINFUNC
     static char * __BEB_11917412 = "@(#)  BEB_11917412 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_BACKWARD_SP3		/* dbasvr.exe, dsa.dll - Fix backward compatibility issue on SP4. */
#define BEB_BACKWARD_SP3		/* Elena Diaconu - 5/9/2002 */
#ifdef MAINFUNC
     static char * __BEB_BACKWARD_SP3 = "@(#)  BEB_BACKWARD_SP3 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_DBMEDIA_ERROR		/* asbackup.dll - Fix GP when Media error occurs. */
#define BEB_DBMEDIA_ERROR		/* Elena Diaconu - 5/13/2002 */
#ifdef MAINFUNC
     static char * __BEB_DBMEDIA_ERROR = "@(#)  BEB_DBMEDIA_ERROR Enabled\n" ;
#endif /* MAINFUNC */
#endif

#endif /* SP4 */
//-----------------------------------------------------

// Add fixes that needs to go into SP5
#if defined(SP5) || defined(_SP5)



#ifndef	BEB_SP4_SAN_ComData
#define BEB_SP4_SAN_ComData   // Steven Hwang, hwapi01. 2-16-2002
#ifdef MAINFUNC
  static char * __BEB_SP4_SAN_ComData = "@(#)   BEB_SP4_SAN_ComData Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_11829633	/* astask.dll, asbackup.dll */
#define BEB_11829633	/* ZHORU02 4/9/2002 */
#ifdef MAINFUNC
     static char * __BEB_11829633 = "@(#)  BEB_11829633 Enabled\n" ;
#endif /* MAINFUNC */
#endif

// The following issue was moved on 4/12/02 from SP4 as per Steven Hwang's request
#ifndef BEB_11794595	// Added Emulex Port Driver support for Serverless Backup Option with using SNIA HBA API.
#define BEB_11794595	// hwapi01 3/18/2002 SP4 IMGW2K.DLL, IMGW2K.EXE(EXTCOPY.C, FCHBA.C).
#ifdef MAINFUNC
     static char * __BEB_11794595 = "@(#)  BEB_11794595 Enabled\n" ;
#endif /* MAINFUNC */
#endif

#endif /* SP5 */

//-----------------------------------------------------

// Add fixes that needs to go into CCB release only
#if defined(BEB_CCB)

#ifndef BEB_CCB_MAKEUP_JOBID
#define BEB_CCB_MAKEUP_JOBID		// kimju01, chuku01
#ifdef MAINFUNC
     static char * __BEB_CCB_MAKEUP_JOBID = "@(#)  BEB_CCB_MAKEUP_JOBID Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_MMO
#define BEB_CCB_MMO		// Kimwo01 2/22/2002 BEB_CCB asdbapi.dll(asdbapi3.c)
#ifdef MAINFUNC
     static char * __BEB_CCB_MMO = "@(#)  BEB_CCB_MMO Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_GLOBALSCRATCHSET
#define BEB_CCB_GLOBALSCRATCHSET		// Kimju01 2/26/2002 BEB_CCB asdbcli2.dll(vldbcli1.c & vldbcli.c)
#ifdef MAINFUNC
     static char * __BEB_CCB_GLOBALSCRATCHSET = "@(#)  BEB_CCB_GLOBALSCRATCHSET Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_GENERIC_JOB
#define BEB_CCB_GENERIC_JOB		// Kimju01 3/6/2002 BEB_CCB Tasks\asgtjob.dll, jobeng.exe, srvres.dll
#ifdef MAINFUNC
     static char * __BEB_CCB_GENERIC_JOB = "@(#)  BEB_CCB_GENERIC_JOB Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_GFS_APPEND
#define BEB_CCB_GFS_APPEND				// chuku01 2/26/2002 BEB_CCB asbackup.dll
#ifdef MAINFUNC
     static char * __BEB_CCB_GFS_APPEND = "@(#)  BEB_CCB_GFS_APPEND Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_MMO_BACKUP
#define BEB_CCB_MMO_BACKUP				// linyu04 2/28/2002 BEB_CCB asbackup.dll, astask.dll(jobtape.c & astask.def & tape_com.c)
#ifdef MAINFUNC
     static char * __BEB_CCB_MMO_BACKUP = "@(#)  BEB_CCB_MMO_BACKUP Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_MMO_RESTORE
#define BEB_CCB_MMO_RESTORE				// linyu04 3/01/2002 BEB_CCB astask.dll, srvres.dll(jobtape.c & astask_e.rc & astaskres.h)
#ifdef MAINFUNC
     static char * __BEB_CCB_MMO_RESTORE = "@(#)  BEB_CCB_MMO_RESTORE Enabled\n" ;
#endif /* MAINFUNC */
#endif

// -leech09 GUI only display warning  for GFS share media Prefix
#ifndef BEB_CCB_ENABLE_GFS_SHARE_MEDIA_PREFIX
#define BEB_CCB_ENABLE_GFS_SHARE_MEDIA_PREFIX
#ifdef MAINFUNC
     static char * __BEB_CCB_ENABLE_GFS_SHARE_MEDIA_PREFIX = "@(#)   BEB_CCB_EN_GFS_SHARE_MED_PRE Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_CENTRALDB
#define BEB_CCB_CENTRALDB		// Kimwo01 3/4/2002 BEB_CCB asdbapi.dll
#ifdef MAINFUNC
     static char * __BEB_CCB_CENTRALDB = "@(#)  BEB_CCB_CENTRALDB Enabled\n" ;
#endif /* MAINFUNC */
#endif

// -leech09 GUI only display warning  For vaulted tape
#ifndef BEB_CCB_ENABLE_RS_MGR_VAULTED_MESG
#define BEB_CCB_ENABLE_RS_MGR_VAULTED_MESG
#ifdef MAINFUNC
     static char * __BEB_CCB_ENABLE_RS_MGR_VAULTED_MESG = "@(#)   BEB_CCB_ENABLE_RS_MGR_VAULTED_MESG Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_GLOBALSCRATCHSET_TASK
#define BEB_CCB_GLOBALSCRATCHSET_TASK	// linyu04 3/06/2002 BEB_CCB asbackup.dll(TAPE_COM.c)
#ifdef MAINFUNC
     static char * __BEB_CCB_GLOBALSCRATCHSET_TASK = "@(#)  BEB_CCB_GLOBALSCRATCHSET_TASK Enabled\n" ;
#endif /* MAINFUNC */
#endif


#ifndef BEB_CCB_JOBSUMMARY		// linyu04 3/15/2002 BEB_CCB	asbackup.dll(backap.c,backmain.c)
#define BEB_CCB_JOBSUMMARY		//astask.dll(astaskres.h & report.c & jobmain.c) srvres.dll(astask_e.rc & astaskres.h)
#ifdef MAINFUNC
     static char * __BEB_CCB_JOBSUMMARY = "@(#)  BEB_CCB_JOBSUMMARY Enabled\n" ;
#endif /* MAINFUNC */
#endif


// -leech09 3/15/2002 Display Tape info in RS mgr for RS by session
#ifndef BEB_CCB_ENABLE_RS_MGR_TAPE_INFO
#define BEB_CCB_ENABLE_RS_MGR_TAPE_INFO
#ifdef MAINFUNC
     static char * __BEB_CCB_ENABLE_RS_MGR_TAPE_INFO = "@(#)   BEB_CCB_ENABLE_RS_MGR_TAPE_INFO Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_GLOBALSCRATCHSET_CUSTJOB
#define BEB_CCB_GLOBALSCRATCHSET_CUSTJOB	// linyu04 3/20/2002 BEB_CCB astask.dll(jobtapef.c)
#ifdef MAINFUNC
     static char * __BEB_CCB_GLOBALSCRATCHSET_CUSTJOB = "@(#)  BEB_CCB_GLOBALSCRATCHSET_CUSTJOB Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_JOBCONTINUATION		// linyu04 4/04/2002 BEB_CCB srvres.dll(astaskres.h,ASTASK_E.RC)
#define BEB_CCB_JOBCONTINUATION		// asbackup.dll(asbackup.h,backredir.c,backap.c,asjob.c,backmain.c, backwin32.c,backagent.c,backdomain.c)
#ifdef MAINFUNC
     static char * __BEB_CCB_JOBCONTINUATION = "@(#)  BEB_CCB_GLOBALSCRATCHSET_CUSTJOB Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_TAPECOPY		// masal03 4/04/2002 (tapecopy.exe)
#define BEB_CCB_TAPECOPY		// CCB enhancements for tapecopy.
#ifdef MAINFUNC
     static char * __BEB_CCB_TAPECOPY = "@(#)  BEB_CCB_TAPECOPY Enabled\n" ;
#endif /* MAINFUNC */
#endif

#ifndef BEB_CCB_JOBWINDOW		// KIMJU01 4/04/2002 (aslogres.dll, jobwindow.exe & jobeng.exe)
#define BEB_CCB_JOBWINDOW		// Job window configuration support.
#ifdef MAINFUNC
     static char * __BEB_CCB_JOBWINDOW = "@(#)  BEB_CCB_JOBWINDOW Enabled\n" ;
#endif /* MAINFUNC */
#endif

#define BEB_11846140
#define BEB_NO_MEMCPY
#define BEB_DTCCFIX
#define BEB_11838583
#define BEB_11932233			// RAWSCSI.DLL fixes Tapeeng GP.
#define BEB_11836540			// Changer.dll fixes MMO tape export problem.
#define BEB_CCB_MERGE_STOPLOOP	// asmerge.dll
#define BEB_CCB_MERGE_FIX
#define BEB_CCB_AGENT_VOLUME_SKIP	// backagent.c in asbackup.dll
#define BEB_CCB_MERGE_SKIP_BAD_SESSION  // asmerge.dll
#define BEB_CCB_SQL_VDI_ERR_BACKUP_CRASH   // asbackup.dll
#define BEB_11868107	//Exchange brick level fix in exchangnew.lib and dbaxchg2.dll

#endif /* BEB_CCB */

//--------------------------------------------------------------------

// Add fixes that need to go into the JAGUAR Agent Update release only

#ifdef MAINFUNC
	 static char * __BEB_AU_VSS = "@(#)  BEB_AU_VSS Enabled\n";
#endif /* MAINFUNC */


#ifdef MAINFUNC
	 static char * __BEB_DBAEXSIS = "@(#)  BEB_DBAEXSIS Enabled\n";
#endif /* MAINFUNC */


#ifndef BEB_AU_GROUPWC
#define BEB_AU_GROUPWC
#ifdef MAINFUNC
	 static char * __BEB_AU_GROUPWC = "@(#)  BEB_AU_GROUPWC Enabled\n";
#endif /* MAINFUNC */
#endif



#ifndef FOR_EXCLUDESIVE_EAR_ONLY
#define FOR_EXCLUDESIVE_EAR_ONLY
#ifdef MAINFUNC
	 static char * __FOR_EXCLUDESIVE_EAR_ONLY = "@(#)  FOR_EXCLUDESIVE_EAR_ONLY Enabled\n";
#endif /* MAINFUNC */
#endif


#endif  /* BEB_ENABLED */

#ifndef BEB_RMTDVPRT
#define BEB_RMTDVPRT
#ifdef MAINFUNC
     static char * __BEB_RMTDVPRT  = "@(#)  BEB_RMTDVPRT  \n" ;
#endif /* MAINFUNC */

#ifndef BAB_TAPE_CIRCULARLOG
#define BAB_TAPE_CIRCULARLOG
#endif

#endif


#ifndef CLUSTERPRO_VIRNAME_SUPPORT
#define CLUSTERPRO_VIRNAME_SUPPORT
#ifdef MAINFUNC
	 static char * __CLUSTERPRO_VIRNAME_SUPPORT = "@(#)  CLUSTERPRO_VIRNAME_SUPPORT Enabled\n";
#endif /* MAINFUNC */
#endif


#ifndef FIPS_NONDATA_ENC
#define FIPS_NONDATA_ENC
#ifdef MAINFUNC
	 static char * __FIPS_NONDATA_ENC = "@(#)  FIPS_NONDATA_ENC Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef JOBMONITOR_PREVIOUSJOB_EXECINFO
#define JOBMONITOR_PREVIOUSJOB_EXECINFO
#ifdef MAINFUNC
	 static char * __JOBMONITOR_PREVIOUSJOB_EXECINFO = "@(#)  JOBMONITOR_PREVIOUSJOB_EXECINFO Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef FIPS_DATA_ENC
#define FIPS_DATA_ENC
#ifdef MAINFUNC
	 static char * __FIPS_DATA_ENC = "@(#)  FIPS_DATA_ENC Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef MAKEUP_OF_MAKEUP
#define MAKEUP_OF_MAKEUP
#ifdef MAINFUNC
	 static char * __MAKEUP_OF_MAKEUP = "@(#)  MAKEUP_OF_MAKEUP Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef TASK_BUG_FIX_ORIOLE
#define TASK_BUG_FIX_ORIOLE
#ifdef MAINFUNC
	 static char * __TASK_BUG_FIX_ORIOLE = "@(#)  TASK_BUG_FIX_ORIOLE Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef GENERIC_CAREPORT_AUTHENTICATION
#define GENERIC_CAREPORT_AUTHENTICATION
#ifdef MAINFUNC
	 static char * __GENERIC_CAREPORT_AUTHENTICATION = "@(#)  GENERIC_CAREPORT_AUTHENTICATION Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef MASTER_CHILD_JOBLOG_CONSOLIDATION
#define MASTER_CHILD_JOBLOG_CONSOLIDATION
#ifdef MAINFUNC
	 static char * __MASTER_CHILD_JOBLOG_CONSOLIDATION = "@(#)  MASTER_CHILD_JOBLOG_CONSOLIDATION Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef TAPE_EXPIRATION_WARNING
#define TAPE_EXPIRATION_WARNING
#ifdef MAINFUNC
	 static char * __TAPE_EXPIRATION_WARNING = "@(#)  TAPE_EXPIRATION_WARNING Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef SESSION_INFO_FROM_DB
#define SESSION_INFO_FROM_DB
#ifdef MAINFUNC
	 static char * __SESSION_INFO_FROM_DB = "@(#)  SESSION_INFO_FROM_DB Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef STAGING_REPEATING_APPEND_ANY_TAPE
#define STAGING_REPEATING_APPEND_ANY_TAPE
#ifdef MAINFUNC
	 static char * __STAGING_REPEATING_APPEND_ANY_TAPE = "@(#)  STAGING_REPEATING_APPEND_ANY_TAPE Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef SECURE_MIGRATION
#define SECURE_MIGRATION
#ifdef MAINFUNC
     static char * __SECURE_MIGRATION  = "@(#)  SECURE_MIGRATION Enabled\n";
#endif /* MAINFUNC */
#endif //SECURE_MIGRATION

#ifndef SUPPORT_NEW_PASSTHROUGH   //add by shebr03 for supporting new PASSTHROUGH mechanism which provided by TE
#define SUPPORT_NEW_PASSTHROUGH
#ifdef MAINFUNC
	 static char * __SUPPORT_NEW_PASSTHROUGH  = "@(#)  SUPPORT_NEW_PASSTHROUGH Enabled\n";
#endif /* MAINFUNC */
#endif //SUPPORT_NEW_PASSTHROUGH

#ifndef SUPPORT_DESTINATION_ENCRYPTION   //add by shebr03 
#define SUPPORT_DESTINATION_ENCRYPTION
#ifdef MAINFUNC
	 static char * __SUPPORT_DESTINATION_ENCRYPTION  = "@(#)  SUPPORT_DESTINATION_ENCRYPTION Enabled\n";
#endif /* MAINFUNC */
#endif //SUPPORT_DESTINATION_ENCRYPTION
//
#ifndef ASDB_BAB116_CRYPTOGRAPHY
#define ASDB_BAB116_CRYPTOGRAPHY
#endif

#ifndef POST_MIGRATION_OPERATIONS
#define POST_MIGRATION_OPERATIONS
#ifdef MAINFUNC
     static char * __POST_MIGRATION_OPERATIONS  = "@(#)  POST_MIGRATION_OPERATIONS Enabled\n";
#endif /* MAINFUNC */
#endif //POST_MIGRATION_OPERATIONS

#ifndef TASK_VISTA_SUPPORT
#define TASK_VISTA_SUPPORT
#endif

#ifndef _DISKSTAGING_CONSOLIDATION
#define _DISKSTAGING_CONSOLIDATION
#ifdef MAINFUNC
     static char * ___DISKSTAGING_CONSOLIDATION  = "@(#)  _DISKSTAGING_CONSOLIDATION Enabled\n";
#endif /* MAINFUNC */
#endif //_DISKSTAGING_CONSOLIDATION

#ifndef CSTOOL_PROMPT_MESSAGE_VISTA
#define CSTOOL_PROMPT_MESSAGE_VISTA
#ifdef MAINFUNC
     static char * ___CSTOOL_PROMPT_MESSAGE_VISTA  = "@(#)  _CSTOOL_PROMPT_MESSAGE_VISTA Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef AGENT_VISTA_SUPPORT
#define AGENT_VISTA_SUPPORT
#ifdef MAINFUNC
     static char * ___AGENT_VISTA_SUPPORT  = "@(#)  _AGENT_VISTA_SUPPORT Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef WREST_14730585
#define WREST_14730585
#ifdef MAINFUNC
	 static char * __WREST_14730585 = "@(#)  WREST_14730585 Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef WREST_15072305
#define WREST_15072305
#ifdef MAINFUNC
     static char * ___WREST_15072305  = "@(#)  _WREST_15072305 Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef WANSYNC_SUPPORT
#define WANSYNC_SUPPORT
#ifdef MAINFUNC
     static char * ___WANSYNC_SUPPORT  = "@(#)  _WANSYNC_SUPPORT Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef MASTER_CHILD_SEPARATION
#define MASTER_CHILD_SEPARATION
#ifdef MAINFUNC
     static char * ___MASTER_CHILD_SEPARATION  = "@(#)  MASTER_CHILD_SEPARATION Enabled\n";
#endif
#endif

#ifndef AGENT_LOGGING
#define AGENT_LOGGING
#ifdef MAINFUNC
     static char * ___AGENT_LOGGING  = "@(#)  AGENT_LOGGING Enabled\n";
#endif
#endif

#ifndef CM_AGENT_LICENSE
#define CM_AGENT_LICENSE
#ifdef MAINFUNC
     static char * ___CM_AGENT_LICENSE  = "@(#)  CM_AGENT_LICENSE Enabled\n";
#endif
#endif

#ifndef _CM_MIGRATION
#define _CM_MIGRATION
#ifdef MAINFUNC
     static char * ___CM_MIGRATION  = "@(#)  _CM_MIGRATION Enabled\n";
#endif /* MAINFUNC */
#endif // _CM_MIGRATION

#ifndef CJQ_DEBUGLOG
#define CJQ_DEBUGLOG
#ifdef MAINFUNC
     static char * __CJQ_DEBUGLOG  = "@(#)  CJQ_DEBUGLOG Enabled\n";
#endif /* MAINFUNC */
#endif // CJQ_DEBUGLOG

#ifndef MASTER_CHILD_SEPERATION_CHKPT
#define MASTER_CHILD_SEPERATION_CHKPT
#ifdef MAINFUNC
     static char * __MASTER_CHILD_SEPERATION_CHKPT  = "@(#)  MASTER_CHILD_SEPERATION_CHKPT Enabled\n";
#endif /* MAINFUNC */
#endif // MASTER_CHILD_SEPERATION_CHKPT

#ifndef TASK_ENHANCEMENT_FOR_SQL_PUSH_AGENT
#define TASK_ENHANCEMENT_FOR_SQL_PUSH_AGENT
#ifdef MAINFUNC
	 static char * __TASK_ENHANCEMENT_FOR_SQL_PUSH_AGENT = "@(#)  SQL_PUSH_AGENT Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef EXCHDB_SQL_PUSH_AGENT
#define EXCHDB_SQL_PUSH_AGENT
#ifdef MAINFUNC
	 static char * __EXCHDB_SQL_PUSH_AGENT = "@(#)  EXCHDB_SQL_PUSH_AGENT Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef SQL_EXCH_PUSH_AGNT_MAKEUP
#define SQL_EXCH_PUSH_AGNT_MAKEUP
#ifdef MAINFUNC
	 static char * __SQL_EXCH_PUSH_AGNT_MAKEUP = "@(#)  SQL_EXCH_PUSH_AGNT_MAKEUP Enabled\n";
#endif /* MAINFUNC */
#endif

//yi$li01
#ifndef BAB_CM_CML
#define BAB_CM_CML
#ifdef MAINFUNC
     static char * ___BAB_CM_CML  = "@(#)  BAB_CM_CML  \n" ;
#endif // MAINFUNC
#endif //BAB_CM_CML

//yi$li01
#ifndef PFC_DISK_STAGING_ENHANCE
#define PFC_DISK_STAGING_ENHANCE
#ifdef MAINFUNC
     static char * ___PFC_DISK_STAGING_ENHANCE   = "@(#)  PFC_DISK_STAGING_ENHANCE  \n" ;
#endif // MAINFUNC
#endif //PFC_DISK_STAGING_ENHANCE

//yi$li01
#ifndef TAPECOPY_TP_LICENSE_CHECK
#define TAPECOPY_TP_LICENSE_CHECK
#ifdef MAINFUNC
     static char * ___TAPECOPY_TP_LICENSE_CHECK   = "@(#)  TAPECOPY_TP_LICENSE_CHECK  \n" ;
#endif // MAINFUNC
#endif //TAPECOPY_TP_LICENSE_CHECK

#ifndef NODE_RPT
#define NODE_RPT
#ifdef MAINFUNC
	 static char * __NODE_RPT = "@(#)  NODE_RPT Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef TASK_LOCALBACKUPRESTORE_FOR_PRIMARY_MEMBER_SERVER
#define TASK_LOCALBACKUPRESTORE_FOR_PRIMARY_MEMBER_SERVER
#ifdef MAINFUNC
	 static char * __TASK_LOCALBACKUPRESTORE_FOR_PRIMARY_MEMBER_SERVER = "@(#)  TASK_LOCALBACKUPRESTORE_FOR_PRIMARY_MEMBER_SERVER Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef UNIX_INC_DIF_SESS_FILTER
#define UNIX_INC_DIF_SESS_FILTER
#ifdef MAINFUNC
	 static char * __UNIX_INC_DIF_SESS_FILTER = "@(#)  UNIX_INC_DIF_SESS_FILTER Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef TASK_ASDB_LOCAL_HANDLE
#define TASK_ASDB_LOCAL_HANDLE
#ifdef MAINFUNC
	 static char * __TASK_ASDB_LOCAL_HANDLE = "@(#)  TASK_ASDB_LOCAL_HANDLE Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef TASK_SESSIONREC_ENHANCEMENT
#define TASK_SESSIONREC_ENHANCEMENT
#ifdef MAINFUNC
	 static char * __TASK_SESSIONREC_ENHANCEMENT = "@(#)  TASK_SESSIONREC_ENHANCEMENT Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef TASK_AGENT_LOG_ENHANCEMENT
#define TASK_AGENT_LOG_ENHANCEMENT
#ifdef MAINFUNC
	 static char * __TASK_AGENT_LOG_ENHANCEMENT = "@(#)  TASK_AGENT_LOG_ENHANCEMENT Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef TASK_TP_LICENSE_CHECK
#define TASK_TP_LICENSE_CHECK
#ifdef MAINFUNC
	 static char * __TASK_TP_LICENSE_CHECK = "@(#)  TASK_TP_LICENSE_CHECK Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef TASK_64BIT_DR
#define TASK_64BIT_DR
#ifdef MAINFUNC
	 static char * __TASK_64BIT_DR = "@(#)  TASK_64BIT_DR Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef VSS_W_BAOF
#define VSS_W_BAOF
#ifdef MAINFUNC
	 static char * __VSS_W_BAOF = "@(#)  VSS_W_BAOF Enabled\n";
#endif /* MAINFUNC */
#endif //VSS_W_BAOF

#ifndef TASK_ENHANCEMENT_FOR_BAB_DATABASE_CATALOG_PROTECTION
#define TASK_ENHANCEMENT_FOR_BAB_DATABASE_CATALOG_PROTECTION
#ifdef MAINFUNC
	 static char * __TASK_ENHANCEMENT_FOR_BAB_DATABASE_CATALOG_PROTECTION = "@(#)  TASK_ENHANCEMENT_FOR_BAB_DATABASE_CATALOG_PROTECTION Enabled\n";
#endif /* MAINFUNC */
#endif //TASK_ENHANCEMENT_FOR_BAB_DATABASE_CATALOG_PROTECTION

#ifndef MC_ARC_LOCALDR_SUPPORT
#define MC_ARC_LOCALDR_SUPPORT
#ifdef MAINFUNC
	 static char * __MC_ARC_LOCALDR_SUPPORT = "@(#)  MC_ARC_LOCALDR_SUPPORT Enabled\n";
#endif /* MAINFUNC */
#endif //MC_ARC_LOCALDR_SUPPORT

#ifndef DR_FILES_LAYOUT_CHANGE
#define DR_FILES_LAYOUT_CHANGE
#ifdef MAINFUNC
	 static char * __DR_FILES_LAYOUT_CHANGE = "@(#)  DR_FILES_LAYOUT_CHANGE Enabled\n";
#endif /* MAINFUNC */
#endif //DR_FILES_LAYOUT_CHANGE

#ifndef ASDB_CHANGE_TO_MSSQL
#define ASDB_CHANGE_TO_MSSQL
#ifdef MAINFUNC
	 static char * __ASDB_CHANGE_TO_MSSQL = "@(#)  ASDB_CHANGE_TO_MSSQL Enabled\n";
#endif /* MAINFUNC */
#endif //ASDB_CHANGE_TO_MSSQL

#ifndef VMWARE_SUPPORT
#define VMWARE_SUPPORT
#ifdef MAINFUNC
	 static char * __VMWARE_SUPPORT = "@(#)  VMWARE_SUPPORT Enabled\n";
#endif /* MAINFUNC */
#endif //VMWARE_SUPPORT

#ifndef TASK_MUS_SPAN_SUPPORT
#define TASK_MUS_SPAN_SUPPORT
#ifdef MAINFUNC
     static char * __TASK_MUS_SPAN_SUPPORT  = "@(#)  TASK_MUS_SPAN_SUPPORT Enabled\n" ;
#endif /* MAINFUNC */
#endif // TASK_MUS_SPAN_SUPPORT

#ifndef TASK_MUX_SPAN_SUPPORT
#define TASK_MUX_SPAN_SUPPORT
#ifdef MAINFUNC
     static char * __TASK_MUX_SPAN_SUPPORT  = "@(#)  TASK_MUX_SPAN_SUPPORT Enabled\n" ;
#endif /* MAINFUNC */
#endif // TASK_MUX_SPAN_SUPPORT

#ifndef WREST_15072305
#define WREST_15072305
#ifdef MAINFUNC
     static char * __WREST_15072305  = "@(#)  WREST_15072305 Enabled\n" ;
#endif /* MAINFUNC */
#endif // WREST_15072305

#ifndef BAB_DBAPI_JOBHISTORY
#define BAB_DBAPI_JOBHISTORY
#ifdef MAINFUNC
     static char * __BAB_DBAPI_JOBHISTORY  = "@(#)  BAB_DBAPI_JOBHISTORY Enabled\n" ;
#endif /* MAINFUNC */
#endif // BAB_DBAPI_JOBHISTORY

#define BACKEND_CHANGES_FOR_SERVICE_ADMIN

#ifndef OPTIMIZE_SEQUENTIAL_COPY
#define OPTIMIZE_SEQUENTIAL_COPY
#ifdef MAINFUNC
     static char * ___OPTIMIZE_SEQUENTIAL_COPY  = "@(#)  _OPTIMIZE_SEQUENTIAL_COPY Enabled\n";
#endif /* MAINFUNC */
#endif // OPTIMIZE_SEQUENTIAL_COPY

#ifndef BEB_15461126
#define BEB_15461126
#ifdef MAINFUNC
     static char * ___BEB_15461126  = "@(#)  _BEB_15461126 Enabled\n";
#endif /* MAINFUNC */
#endif // BEB_15461126

#ifndef WANSYNC_APP_TYPE_SUPPORT
#define WANSYNC_APP_TYPE_SUPPORT
#ifdef MAINFUNC
     static char * ___WANSYNC_APP_TYPE_SUPPORT  = "@(#)  _WANSYNC_APP_TYPE_SUPPORT Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef DISKSTAGING_MEDIA_MAXIMIZATION
#define DISKSTAGING_MEDIA_MAXIMIZATION
#ifdef MAINFUNC
     static char * __DISKSTAGING_MEDIA_MAXIMIZATION  = "@(#)  DISKSTAGING_MEDIA_MAXIMIZATION Enabled\n" ;
#endif /* MAINFUNC */
#endif // DISKSTAGING_MEDIA_MAXIMIZATION

#ifndef ERGO 
#define ERGO
#ifdef MAINFUNC
     static char * ___ERGO  = "@(#)  _ERGO Enabled\n";
#endif /* MAINFUNC */
#endif

#ifndef BAB_DYNAMIC_KEY_ASC
#define BAB_DYNAMIC_KEY_ASC
#ifdef MAINFUNC
     static char * __BAB_DYNAMIC_KEY_ASC  = "@(#)  BAB_DYNAMIC_KEY_ASC Enabled\n" ;
#endif /* MAINFUNC */
#endif // BAB_DYNAMIC_KEY_ASC

#ifndef BAB_DYNAMIC_KEY_ID
#define BAB_DYNAMIC_KEY_ID
#ifdef MAINFUNC
     static char * __BAB_DYNAMIC_KEY_ID  = "@(#)  BAB_DYNAMIC_KEY_ID Enabled\n" ;
#endif /* MAINFUNC */
#endif // BAB_DYNAMIC_KEY_ID

#ifndef BAB_DYNAMIC_KEY
#define BAB_DYNAMIC_KEY
#ifdef MAINFUNC
     static char * __BAB_DYNAMIC_KEY  = "@(#)  BAB_DYNAMIC_KEY Enabled\n" ;
#endif /* MAINFUNC */
#endif // BAB_DYNAMIC_KEY

#ifndef MUX_TPSEQ
#define MUX_TPSEQ
#ifdef MAINFUNC
     static char * __MUX_TPSEQ  = "@(#)  MUX_TPSEQ Enabled\n" ;
#endif /* MAINFUNC */
#endif // MUX_TPSEQ

/////////////////////////////////////////////////////////////////////////////////////////////////
#ifndef TASKS_ENHANCEMENT_FOR_RMAN
#define TASKS_ENHANCEMENT_FOR_RMAN
#ifdef MAINFUNC
     static char * __TASKS_ENHANCEMENT_FOR_RMAN  = "@(#)  TASKS_ENHANCEMENT_FOR_RMAN Enabled\n" ;
#endif /* MAINFUNC */
#endif // TASKS_ENHANCEMENT_FOR_RMAN

#ifndef BAB_ORACLE_RMAN_ENHANCEMENT
#define BAB_ORACLE_RMAN_ENHANCEMENT
#ifdef MAINFUNC
     static char * __BAB_ORACLE_RMAN_ENHANCEMENT  = "@(#)  BAB_ORACLE_RMAN_ENHANCEMENT Enabled\n" ;
#endif /* MAINFUNC */
#endif // BAB_ORACLE_RMAN_ENHANCEMENT

#ifndef LONGHORN_HDLK_SUPPORT
#define LONGHORN_HDLK_SUPPORT
#ifdef MAINFUNC
     static char * __LONGHORN_HDLK_SUPPORT  = "@(#)  LONGHORN_HDLK_SUPPORT Enabled\n" ;
#endif /* MAINFUNC */
#endif //LONGHORN_HDLK_SUPPORT

#ifndef _TAPE_STAGING_POST_MIGRATION_OPERATION
#define _TAPE_STAGING_POST_MIGRATION_OPERATION
#endif //_TAPE_STAGING_POST_MIGRATION_OPERATION

#ifndef VSS_AGENT_LONGHORN
#define VSS_AGENT_LONGHORN
#endif

#ifndef WANSYNC_EXCH2007
#define WANSYNC_EXCH2007
#endif

#ifndef BEB_16373616
#define BEB_16373616
#ifdef MAINFUNC
      static char * __BEB_16373616  = "@(#)  BEB_16373616 Enabled\n" ;
#endif // MAINFUNC
#endif // BEB_16373616

#ifndef NODE_RPT_MASTER
#define NODE_RPT_MASTER
#endif
#ifndef UNICODE_JIS_SUPPORT 
#define UNICODE_JIS_SUPPORT
#endif

#ifndef DIAG_UNICODE_SUPPORT
#define DIAG_UNICODE_SUPPORT
#endif

#ifndef VI35_SUPPORT
#define VI35_SUPPORT
#ifdef MAINFUNC
	 static char * __VI35_SUPPORT = "@(#)  VI35_SUPPORT Enabled\n";
#endif /* MAINFUNC */
#endif //VI35_SUPPORT

//Added for DEDUPE project. This would enable this entire feature.
#ifndef DEDUPE_SUPPORT
#define DEDUPE_SUPPORT
#endif

#if defined(DEDUPE_SUPPORT) && !defined(GDD_SUPPORT)
#define GDD_SUPPORT
#endif

#if defined(DEDUPE_SUPPORT) && !defined(DEDUPE_DIFF_INCR_BACKUP_OPTIMIZATION)
#define DEDUPE_DIFF_INCR_BACKUP_OPTIMIZATION
#endif

#ifndef R12_5_MANAGE_PASSWORD
#define R12_5_MANAGE_PASSWORD
#endif

#ifndef ARC_ROLE_MANAGEMENT
#define ARC_ROLE_MANAGEMENT
#endif

#ifndef ARC_SAVE_NODEINFO
#define ARC_SAVE_NODEINFO
#endif

#ifndef USERPROFILE_UNICODE_IMPL
#define USERPROFILE_UNICODE_IMPL
#endif

#ifndef Wanhe05_DeDupe_SCAN
#define Wanhe05_DeDupe_SCAN
#ifdef MAINFUNC
     static char * __Wanhe05_DeDupe_SCAN  = "@(#)  Wanhe05_DeDupe_SCAN\n" ;
#endif /* MAINFUNC */
#endif //Wanhe05_DeDupe_SCAN

/////// R14_E14. Project (2/11/2009)
#ifndef R14_BLI_DB
#define R14_BLI_DB
#ifdef MAINFUNC
     static char * __R14_BLI_DB  = "@(#)  R14_BLI_DB\n" ;
#endif /* MAINFUNC */
#endif //R14_BLI_DB

///////// FOR CODE DB MERGE (7/8/2008) (kimwo01)//////////////////
#ifndef R12_V_VM_DB
#define R12_V_VM_DB
#ifdef MAINFUNC
     static char * __R12_V_VM_DB  = "@(#)  R12_V_VM_DB\n" ;
#endif /* MAINFUNC */
#endif //R12_V_VM_DB

#ifndef R12_V_DEDUPE_DB
#define R12_V_DEDUPE_DB
#ifdef MAINFUNC
     static char * __R12_V_DEDUPE_DB  = "@(#)  R12_V_DEDUPE_DB\n" ;
#endif /* MAINFUNC */
#endif //R12_V_DEDUPE_DB

#ifndef R12_V_DASHBOARD_DB
#define R12_V_DASHBOARD_DB
#ifdef MAINFUNC
     static char * __R12_V_DASHBOARD_DB  = "@(#)  R12_V_DASHBOARD_DB\n" ;
#endif /* MAINFUNC */
#endif //R12_V_DASHBOARD_DB

#ifndef R12_V_USERPROFILE_DB
#define R12_V_USERPROFILE_DB
#ifdef MAINFUNC
     static char * __R12_V_USERPROFILE_DB  = "@(#)  R12_V_USERPROFILE_DB\n" ;
#endif /* MAINFUNC */
#endif //R12_V_USERPROFILE_DB
///////// END OF CODE DB MERGE (kimwo01)//////////////////
////Code Merge for VM project-Tasks, JobEng and some agent moduels(linyu04)
#ifndef R12V_VM_TASKS
#define R12V_VM_TASKS
#ifdef MAINFUNC
     static char * __R12V_VM_TASKS  = "@(#)  R12V_VM_TASKS\n" ;
#endif /* MAINFUNC */
#endif //R12V_VM_TASKS

#ifndef R12V_VM_JOBENG
#define R12V_VM_JOBENG
#ifdef MAINFUNC
     static char * __R12V_VM_JOBENG  = "@(#)  R12V_VM_JOBENG\n" ;
#endif /* MAINFUNC */
#endif //R12V_VM_JOBENG

#ifndef R12V_VM_UnivAg
#define R12V_VM_UnivAg
#ifdef MAINFUNC
     static char * __R12V_VM_UnivAg  = "@(#)  R12V_VM_UnivAg\n" ;
#endif /* MAINFUNC */
#endif //R12V_VM_UnivAg

#ifndef R12V_VM_NTAGENT
#define R12V_VM_NTAGENT
#ifdef MAINFUNC
     static char * __R12V_VM_NTAGENT  = "@(#)  R12V_VM_NTAGENT\n" ;
#endif /* MAINFUNC */
#endif //R12V_VM_NTAGENT
////End Code Merge for VM project-Tasks, JobEng and some agent moduels

//Added for DASHBOARD project. This would enable this entire feature on Task side.
#ifndef DASHBOARD_SUPPORT
#define DASHBOARD_SUPPORT
#endif

#ifndef R12V_VM_VMAGENT
#define R12V_VM_VMAGENT
#ifdef MAINFUNC
	 static char * __R12V_VM_VMAGENT = "@(#)  R12V_VM_VMAGENT Enabled\n";
#endif /* MAINFUNC */
#endif //R12V_VM_VMAGENT
	 
#ifndef R12V_VM_AGENTADMIN
#define R12V_VM_AGENTADMIN
#endif

#ifndef R12V_VM_ASMGR		//GUI support for Virtual Machine
#define R12V_VM_ASMGR
#endif

#ifndef ASMGR_MEDIA_POOL_ENHANCEMENT
#define ASMGR_MEDIA_POOL_ENHANCEMENT
#endif

// Enable Dashboard Manager in GUI 
#ifndef AS_DASHBOARD_GUI_SUPPORT
#define AS_DASHBOARD_GUI_SUPPORT
#endif

#ifndef HUGE_SESSION_FILTER
#define HUGE_SESSION_FILTER
#endif


#ifndef RICH_AGENT_INFORMATION
#define RICH_AGENT_INFORMATION
#endif // RICH_AGENT_INFORMATION


#ifndef SESSNUM_INCREASE
#define SESSNUM_INCREASE
#endif //SESSNUM_INCREASE

#ifndef CATALOG_STREAM_CRC
#define CATALOG_STREAM_CRC
#endif

//<sonmi01>2008-12-16 ###???
#ifndef MEDIA_ASSURE_VOLUME_OPTION
#define MEDIA_ASSURE_VOLUME_OPTION
#endif
//</sonmi01>

#ifndef DETAIL_LOG_TO_FILE
#define DETAIL_LOG_TO_FILE
#endif

#ifndef BACKUP_BLI
#define BACKUP_BLI
#endif


#ifndef BLI_SUPPORT
#define BLI_SUPPORT
#endif


#endif /* _BEBENABLED_H_ */ 
/**************************************************************************************
!!!                                                                                 !!!
!!!                                                                                 !!!
!!!    Please don't append after this paragraph to ensure there is a new line at    !!!
!!!    the end of this file                                                         !!!
!!!                                                                                 !!!
!!!                                                                                 !!!
**************************************************************************************/
