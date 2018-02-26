/**************************************************************************
Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.

 Program Name:  
      Version:  
 Version Date:  
**************************************************************************/
/*
*  This file defines common types that are needed among services
*/

#ifndef __SESSDEF_H
#define __SESSDEF_H

#include "BebEnabled.h"

/* 
*  All session types defined here
*/

#define   BST_D2D_BASE						5000 //<sonmi01>2010-2-9 D2D session type should not conflict with that of arcserve

#define   BST_NW2X                         10
#define   BST_NW3X                         11
#define   BST_NW4X                         12
#define	  BST_IMAGE_SESSION                13 // NetWare image backup for traditional volume

#define	  BST_GROUPWISE_SESSION			   15 // For GroupWise

#define   BST_IMAGE_SESSION_NSS5		   16 // NetWare image backup for NSS volume in NetWare 5.x
#define   BST_IMAGE_SESSION_NSS6		   17 // NetWare image backup for NSS volume in NetWare 6.x
#define	  BST_CLUSTER_SESSION			   18 // For NetWare Cluster 

#define   BST_WS_PC                        20
#define   BST_WS_OS2_FAT                   21
#define   BST_WS_OS2_HPFS                  22
#define   BST_WS_MAC                       23
#define   BST_WS_UNIX                      24
#define   BST_AS_DATABASE                  25
#define   BST_DS_DATABASE                  26     /* Netware Directory Services */
#define   BST_MSNET                        27     /* Network Drive */
#define   BST_ARC_SOLO                     28     /* ARCSolo Database */
#define   BST_SOLO_DATABASE                28       
#define   BST_NT_DATABASE                  29     /* ARCserve/NT database    */
#define   BST_DBAG_ORACLELOG               30
#define   BST_HSM_DATABASE                 31     /* HSM Database */
#define   BST_WS_HSM_DATABASE              BST_HSM_DATABASE  
#define   BST_DBAG_SYBASE                  32     /* DBagent Sybase */
#define   BST_DBAG_SYBASELOG               BST_DBAG_SYBASE /* DBagent Sybase Log files     */
#define   BST_AS6_DATABASE                 33     /* Mercury Database */
#define   BST_NT_DATABASE_VLDB             34     /* ARCserve/NT VLDB database */
#define   BST_DBAG_INFORMIX                35     /* Informix OnBAR */
#define   BST_POSIX_TAR                    36     /* POSIX 1003.1-1990 tar magic"ustar" */
#define   BST_POSIX_CPIO                   37     /* POSIX 1003.1-1990 cpiomagic070707 */
#define   BST_UNIX_IMAGE                   38     /* UNIX file system image backup */
#define   BST_WS_NDS                       39     /* NDS backup */
#define   BST_WS_NT_FAT                    40
#define   BST_WS_NT_HPFS                   41
#define   BST_WS_NT_NTFS                   42
#define   BST_WS_CHICAGO                   43
#define   BST_WS_WIN95_FAT                 BST_WS_CHICAGO
#define   BST_WS_NT_CDFS                   44
#define   BST_WS_NT_CSNW                   45
#define   BST_WS_NT_IMAGE                  46     /* NT image backup        */
#define   BST_WS_FAT32                     47     /* FAT32 for Windows 9.x     */
#define   BST_WS_NT_DRINFO                 48     /* 05/14/99 hhwu added requested by tape group */
#define   BST_WS_W2K_IMAGE                 49     /* Window 2000 image backup*/
#ifdef WIN32
#define   BST_DBA_AGENT                    50     /* DB Agent            */
#else
#define   BST_DBAG_ORACLE                  50     /* DB Agent            */
#endif
#define   BST_DBA_SAP                      51     /* DB Agent SAP            */
#ifndef WIN32
#define   BST_FTP                          52     /* Ftp Site.            */
#endif
#define   BST_SYSTEM_STATE                 53     /* Windows 2000 System State (JCL 19990604)*/
#define   BST_REGISTRY                     54     /* NT Registry            */
#ifdef WIN32
#define   BST_FTP                          55     /* Ftp Site.*/
#endif
#define   BST_OPTICAL_FAT                  56     /* Optical FAT */
#define   BST_OPTICAL_NTFS                 57     /* Optical NTFS */
#define   BST_WS_AS400                     58     /* AS400 */
#define   BST_WS_VMS                       59     /* VMS */
#define   BST_WS_NT_FAT32                  60     /* NT FAT 32*/
#define   BST_UNIX_RAW                     61     /* UNIX raw device backup */
#define   BST_UNIX_OPENINGRES              62     /* UNIX CA/OpenIngres session */
#define   BST_UNIX_SAP                     63     /* UNIX SAP database session */
#define   BST_UNIX_SYBASENATIVE            64     /* UNIX Native Sybase session */
#define   BST_UNIX_ASM                     65     /* UNIX CA/ASM session */
#define   BST_UNIX_ORACLE8                 66     /* UNIX Oracle8 session */
#define   BST_UNIX_ASMDGUX                 67     /* UNIX CA/ASM for Digital */
#define   BST_AS400_ASM                    68     /* AS/400 ASM session*/
#define   BST_ASLITE_FOR_NW                69     /* ARCserve For Netwrae Lite Session*/
#define   BST_NNP_FOR_NW                   70     /* ARCserve For Netware non Netware PArtition*/
#define   BST_WS_NAS                       71     /* UNIX Third party NAS / NDMP backup session*/
#define   BST_NT_SYM                       72     /* NT EMC Symmetrix backup session*/
#define   BST_NT_NAS                       73     /* NT Third party NAS / NDMP backup session*/

#define   BST_AS_INGRES_DATA               75      /* AS Ingres data( for "iidbdb" and "asdb") */
#define   BST_AS_INGRES_DMP                76      /* AS INgres dmp ( for "iidbdb" and "asdb") */
#define   BST_DBAG_NOTES                   77      /* DBagent Notes files */
#define   BST_AS_DR_TAR_INFO               78       /* AS DR for Unix */

#define   BST_NT_GENERIC_DBAGENT           79      /* NT database agent session type used on Unix side */
#define   BST_NT_ORACLE                    BST_NT_GENERIC_DBAGENT
#define   BST_UNIX_ORACLE9I                80     /* UNIX Oracle9I session */
#define   BST_VIA_NWAGENT_NW3X             81     /* NW 3x backedup via the NW agent, for database browsing of Netware Systems */
#define   BST_VIA_NWAGENT_NW4X            82	  /*  NW 4x backed up via the NW agent */
#define   BST_DBAG_MYSQL                   81     /* UNIX MySQL agent */
#define   BST_DBAG_DB2                     82     /* UNIX DB2 agent */

#define	  BST_XP_WRITER					   83

#define	BST_WS_DBAEXSIS						84 	  /* Database Agent - Exchange Document Level (Single Instance Storage)  */

#define BST_BMB_DATA_PROTECTION				85
#define BST_BMB_DATA_GROWTH					86

#define BST_AS_MMO_BACKUP					87	// Unix MMO database backup session

#define BST_DBAG_SQL						88	// SQL Agent database backup session

#define BST_UNIX_ORACLERMAN					89  //Oracle RMAN database session
#define BST_WINDOWS_ORACLERMAN				90

#define BST_WS_SPDBAGENT_WSS				91	//SharePoint DB Agent - Document Library
#define BST_WS_SPDBAGENT_SI					92	//SharePoint DB Agent - Search Index
#define BST_WS_SPDBAGENT_SSO				93	//SharePoint DB Agent - Single Sign-on
#define BST_WS_SPDBAGENT_DB					94	//SharePoint DB Agent - DataBase
#define BST_WS_SPDBAGENT_SQL				95	//SharePoint DB Agent - SQL DataBase

#define BST_WS_VMS_ODS2					    BST_WS_VMS	/* Added for VMS ODS-2 */
#define BST_WS_VMS_ODS5					    101			/* Added for VMS ODS-5 */
#define BST_UNIX_OES_TSAFS					97	//UNIX OES session

#ifndef WIN32
#define BST_DBA_AGENT						BST_DBAG_ORACLE
#define BST_UNIX_ORACLE						BST_DBAG_ORACLE
#endif

#define  BST_WS_DBAEXDB						98 //Exchange CP agent
#define	 BST_WS_DBAEXDBVSS					99	//E12 agent
#ifdef WANSYNC_SUPPORT
#define	 BST_WS_WANSYNC						102 // Wansync backup
#endif

#define	BST_ASDB_MSSQL						103 // Single-Database backup of ASDB in non-ASDB-owned Instance
#define	BST_ASDB_CATALOG					104 // ASDB catalog files
#define	BST_ADR_MSSQL						105 // SQL Server DR Element Files
#define BST_ASDB_JOBQUEUE					106 // ASDB Job Queue files

#define BST_ASDB_MSSQLEXP					107 // Whole-Instance backup of ASDB-owned Instance
//#ifdef VMWARE_SUPPORT
#define BST_WS_VMWARE						108 // VMWare backup session
//#endif

//#ifdef SPS_2007_CHANGES
#define BST_WS_SHAREPOINT_2007						109	// MS Share point 2007 Agent 
//#endif //SPS_2007_CHANGES

#define BST_VM_INTERNAL						(BST_D2D_BASE + 110)   // VMWARE_AGENT, VM IFS internal session

//MSVM_SUPPORT
#define BST_WS_HYPERV						(BST_D2D_BASE + 111)   //Microsoft Hyper-V session
#define BST_WS_BLI							(BST_D2D_BASE + 112)	  // BLI backup session - BACKUP_BLI
#define BST_VM_INTERNAL_APP					(BST_D2D_BASE + 113)	  // BLI backup internal APP session
#define BST_VM_INTERNAL_APP_SQL				(BST_D2D_BASE + 114)	  // AF APP SQL Session
#define BST_VM_INTERNAL_APP_EXCH			(BST_D2D_BASE + 115)	  // AF APP Exchange Session

//potvi02--win8
#define   BSF2_NTFS_DEDUP_OPTIMIZED      0x00000200
#define   BSF2_NTFS_IMAGE_OPTIMIZED      0x00000400
///<ZZ[zhoyu03: 2009/09/27]: Add a new session type for exhcange2010. In order to integrate with ARCServe
//                           we should define session type 30000-40000 reserved by ARCServe.
//                           session type 110-115 should be changed in future, then new session type is started
//                           from 30006. session type is USHORT in ARCServe now.
#define BST_VM_INTERNAL_APP_EXCH_2010       (BST_D2D_BASE + 116)
#define BST_VM_INTERNAL_APP_EXCH_2013       (BST_D2D_BASE + 117)
#define BST_VM_INTERNAL_APP_AD				(BST_D2D_BASE + 118)
#define BST_VM_INTERNAL_APP_EXCH_2016       (BST_D2D_BASE + 119)

#define   BSM_AP_FULL                1
#define   BSM_AP_DIFFMODDATE         2
#define   BSM_AP_DIFFARCHBIT         3
#define   BSM_AP_INCRARCHBIT         4
#define   BSM_AP_WEEKLY              5    /*Full Weekly Backup  - New in Sniper*/
#define   BSM_AP_MONTHLY             6    /* Full Monthly Backup - New in Sniper*/
#define   BSM_AP_INCRMODDATE         7    /* - added 03/09/1999 James*/
 
#define   BSM_FULL                   10
#define   BSM_FULL_CLR_ARCHIVE       11
#define   BSM_INCREMENTAL            12
#define   BSM_DIFFERENTIAL           13
#define   BSM_LEVEL0                 BSM_FULL
#define   BSM_LEVEL1                 14
#define   BSM_LEVEL2                 15

#define   BSM_ROTATE_FULL            21   /* Customer Rotation Full        */
#define   BSM_ROTATE_DIFFMODDATE     22   /* Customer Rotation Diff /Mod Date    */
#define   BSM_ROTATE_DIFFARCHBIT     23   /* Customer Rotation Diff /Arch Bit    */
#define   BSM_ROTATE_INCRARCHBIT     24   /* Customer Rotation Incr /Arch Bit    */

#define   BSM_DBA_BRICK_FULL         28
#define   BSM_DBA_BRICK_INCRE        29
#define   BSM_DBA_BRICK_TIMEBSE      30

#define   BSM_DBA_DATABASE           31   /* Backup DBA Database            */
#define   BSM_DBA_TABLE              32   /* Backup DBA Table            */
#define   BSM_DBA_LOG                33   /* Backup DBA Log            */
#define   BSM_DBA_LOG_TRUNC          34   /* Backup DBA Log with Truncation    */

#define   BSM_DBA_DIR_FULL           35   /* Backup DBA Directory Service Full    */
#define   BSM_DBA_DIR_COPY           36   /* Backup DBA Directory Service Copy    */
#define   BSM_DBA_DIR_INCR           37   /* Backup DBA Directory Service Incr    */
#define   BSM_DBA_DIR_DIFF           38   /* Backup DBA Directory Service Diff    */
#define   BSM_DBA_IS_FULL            39   /* Backup DBA Information Store Full    */
#define   BSM_DBA_IS_COPY            40   /* Backup DBA Information Store Copy    */
#define   BSM_DBA_IS_INCR            41   /* Backup DBA Information Store Incr    */
#define   BSM_DBA_IS_DIFF            42   /* Backup DBA Information Store Diff    */
#define   BSM_DBA_FILEGRP            43   /*  Backup DBA file/filegrp for SQL server                  */
#define   BSM_DBA_SVRLESS            44   /*  Backup DBA use serverless option                      */
#define   BSM_DBA_FILE_DIFF          45   /*  Backup DBA Differential options for SQL server files  */
#define   BSM_DBA_LOG_NOREC          46   /*  Backup DBA Log Norecovery options for SQL server files*/
#define   BSM_DBA_LOG_STANDBY        47   /*  Backup DBA Log Stabdby options for SQL server files      */
#define   BSM_DBA_FASTRAX            48   /*  Backup DBA use EMC Fastrax option*/
#define   BSM_DBA_TIMEFINDER         49   //  Backup DBA use Timefinder option


#define   BSM_NDMP_INC1              50   /* for NAS_SUPPORT */
#define   BSM_NDMP_INC2              51   /* for NAS_SUPPORT */
#define   BSM_NDMP_INC3              52   /* for NAS_SUPPORT */
#define   BSM_NDMP_INC4              53   /* for NAS_SUPPORT */
#define   BSM_NDMP_INC5              54   /* for NAS_SUPPORT */
#define   BSM_NDMP_INC6              55   /* for NAS_SUPPORT */
#define   BSM_NDMP_INC7              56   /* for NAS_SUPPORT */
#define   BSM_NDMP_INC8              57   /* for NAS_SUPPORT */

#define   BSM_DBA_DOC_FULL	         58   /* for Exchange Document Level - Full			*/
#define   BSM_DBA_DOC_INC            59   /* for Exchange Document Level - Incremental	*/
#define   BSM_DBA_DOC_DIFF           60   /* for Exchange Document Level - Differential */
#define   BSM_DBA_DOC_TIME           61   /* for Exchange Document Level - Time-based	*/
#define	  BSM_VSSWRITER_LOG			 62   /* for writer log type sessions */
#define   BSM_VSSWRITER_COPY		 63	  /* for writer copy type sessions */

#define   BSM_NEXT_AVAULABLE_METHOD  64   /*  next new define session method should use this and increase the BSM_NEXT_AVAULABLE_METHOD */

#define   BSM_DBA_PARTIAL			 68	  /* for SQL Server 2005 - Partial Full Backup */
#define   BSM_DBA_PARTIAL_DIFF		 69   /* for SQL Server 2005 - Partial Diff Backup */

//Session Method (Session Header, Restore Browse): Full Database Backup performed using Clone-Snap mechanism.
#define   BSM_DBA_CLONESNAP          70

//add by chech24, session method for SPS2007 new agent
#ifdef SPS_2007_CHANGES
#define	  BSM_SPS2007_DB_FULL		 71
#define   BSM_SPS2007_DB_DIFF        72
#endif 

#define   BSF_HASSYSOBJ                  0x00000001
#define   BSF_VOLLEVEL                   0x00000002
#define   BSF_FILTERED                   0x00000004
#define   BSF_PRUNED                     0x00000008
#define   BSF_FULLSMS                    0x00000010
#define   BSF_PARTIALSMS                 0x00000020
#define   BSF_SIDF                       0x00000040      /* Session backed up in Directory Services mode*/
#define   BSF_AGENT                      0x00000080      /* Session backuped with Agent                 */
#define   BSF_X                          0x00000100      /* Used by NetWare                             */
#define   BSF_XX                         0x00000200      /* Used by NetWare                             */
#define   BSF_COMPRESSED                 0x00000400       
#define   BSF_ENCRYPTED                  0x00000800       
#define   BSF_HASCATALOG                 0x00001000      /* session has a catalog session following     */
#define   BSF_NODETAIL                   0x00002000      /* session does not hava detailed records      */
#define   BSF_HASCRC                     0x00004000       
#define   BSF_CHECKPOINTED               0x00008000        
#define   BSF_CHECKPOINT                 BSF_CHECKPOINTED                
#define   BSF_HASEISA                    0x00010000      /* session has EISA config file.               */
#define   BSF_MBO                        0x00020000      /* NOT USED ANYMORE: Session backed up to MBO                    */
#define   BSF_DONOTREPLICATE             0x00020000      /* Don't Replicate session in tapecopy (CCB)   */
#define   BSF_MSCS                       0x00040000      /* Microsoft Cluster Shared Disk               */
#define   BSF_SIS                        0x00100000      /* session contain SIS common file             */
#define   BSF_SRVLESS                    0x00200000      /* ServerLess Session type    */
#define   BSF_MERGE_INCOMPLETED          0x10000000      /* Merge Database incompleted                  */
#define	  BSF_REMOTE_VI	 				 0x00080000 // Remote Protocol VI
#define   BSF_REMOTE_NP					 0x00400000 // Remote Protocol Named Pipes
#define   BSF_NEWSQLFORMAT				 0x00800000 // NOT USED: For SQL High performance new format for backup/restore
#define   BSF_MULTISTRIPE_MASTER		 BSF_NEWSQLFORMAT // Session multistriping master job
#define	  BSF_OFFLINE					 0x01000000	// For DBAgents	Fastrax/TIMEFINDER
#define   BSF_REPLICATED				 0x02000000 // TapeCopy : Replicated
#define   BSF_VAULTED					 0x04000000 // MMO : Vaulted
#define   BSF_08000000					 0x08000000
#define	  BSF_SESSPWD_ENCRYPTED			 BSF_08000000	// to indicate the session password is encrypted.
#define   BSF_20000000					 0x20000000		// used for Virtual Path (SPS)
#define   BSF_HAS_VIRTUALPATH			 BSF_20000000	// Virtual Path present for Application-based backup of Database
#define   BSF_SPS_DB					 BSF_HAS_VIRTUALPATH // Virtual Path present for SharePoint Agent backup of database
#define   BSF_40000000					 0x40000000     // Reserved, it is used for CDP integration.

#define   BSF_PC_CATALOG				 0x80000000 // Catalog Updates to Parent Child Tables

#define	  BSF_NEWSQLFORMAT_RAID0		 0x01000000 // For SQL High performance new format for backup/restore ; for RAID0
#define   BSF_MULTISTRIPE_CHILD          BSF_NEWSQLFORMAT_RAID0	// Session multistriping child job
#define   BSF_VSS_AUTHORITATIVE_RESTORE  BSF_X	// For VSS Authoritative Restore. Since NW doesn't have Writer, we reuse this flag.

#ifdef BAB_VSS_SYSTEMSTATE_SUPPORT
#define   BSF_VSS_SYSTEMSTATE            BSF_XX      //For VSS backup system state since NetWare does not have Writer
#endif

/*
 * BSF2_* is the extended session flags.  It should be used in 
 * SESSION_HEADER::sessionFlags2 field.
 */
#define	  BSF2_NATIVECHECKSUM			 0x00000001  // for SQL Server 2005 - Backed-up data contains checksum generated by source application or database
#define	  BSF2_GWSESS_UTF				 0x00000002	 // Netware GroupWise
#define   BSF2_COPY_ONLY				 0x00000004  // for SQL Server 2005 - copy-only backup
#define   BSF2_COMPOUND					 0x00000008  // for Agent for ARCserve Database (MSSQL) - session contains multiple databases & transaction logs for application point-in-time consistency

//Extended Session Flag (Session Header, Catalog File): Differential Session contains location information for the Pre-Requisite sessions.
#define   BSF2_LISTSPREREQS              0x00000010
#define   BSF2_ONECOPY_HARDLINK			 0x00000020	 // only one copy of the hardlink files backed up.
#define   BSF2_COPY_SESSION				 0x00000040	 // the session is generated through migration or tape copy
#define	  BSF2_BLI_SESSION				 0x00000080	 // BLI backup session
#define	  BSF2_BLI_VHD					 0x00000200  // BLI backup in VHD format - BACKUP_BLI
#define	  BSF2_BLI_READY_FOR_CONSOLIDATE 0x00000400	 // BLI ready for consolidate session - BACKUP_BLI
#define   BSF2_ENCRYPTED_BY_TE			 0x80000000  // Indicate a session that encrypted by tape engine
#define   BSF2_SYSTEM_VOLUME             0x00001000  // Backed Up session is a system volume Session

// Defined During UNICODE_JIS_SUPPORT 
#define   BSF2_UNICODE_SESSION           0x00000100  // Backed Up session is a Unicode Session
#if defined(UNICODE_JIS_SUPPORT)// Oripin: UNICODE_JIS Support kalsa03
#define isSessionUnicode(sessHeader) ((sessHeader).sessionFlags2 & BSF2_UNICODE_SESSION)
#define setSessionUnicode(sessHeader) ((sessHeader).sessionFlags2 |= BSF2_UNICODE_SESSION)
#endif
#ifdef WANSYNC_SUPPORT
/*
 * Application type used in SESSION_HEADER::usAppType field
 */
#define  APP_T_WANSYNC_FS				1
#define  APP_T_WANSYNC_MSSQL			2
#define  APP_T_WANSYNC_EXCHANGE			3
#define  APP_T_WANSYNC_ORACLE			4
#define  APP_T_WANSYNC_DB2				5
#endif

#ifdef VMWARE_SUPPORT
/*
 * these are session sub-types of session BST_WS_VMWARE.  It tells what 
 * type of VMWare backup it is.
 */
#define APP_T_VMWARE_WINDOWS_IMG		1
#define APP_T_VMWARE_WINDOWS_FS			2
#define APP_T_VMWARE_LINUX_IMG			3
#define APP_T_VMWARE_WINDOWS_IMG_FS		4		// R12V_VM_TASKS
#endif
#endif  /* __SESSDEF_H */
