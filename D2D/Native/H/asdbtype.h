#ifndef __ASDBTYPE_H
#define __ASDBTYPE_H

//
//   Object Type
//
#define OT_SERVER            0          // File Server
#define OT_WS_PC             1          // PC Workstation
#define OT_WS_OS2            2          // OS/2 Workstation
#define OT_WS_MAC            3          // Macintosh Workstation
#define OT_WS_UNIX           4          // UNIX Workstation
#define OT_VOLUME            5          // Volume
#define OT_DIRECTORY         6          // Directory
#define OT_FILE              7          // File
#define OT_TAPE              8			// Tape
#define OT_TAPESESSION       9			// Tape Session
#define OT_SESSIONS			 15			// Sessions
#define OT_MSNET_SERVER      10         // MS Net - PeerToPeer Server
#define OT_DBA_DATABASE		 11			// DB Agent Database
#define OT_DBA_DB_INSTANCE   12			// DB Agent Database Instance
#define OT_WS_AS400          13         // AS400 
#define OT_WS_VMS            14         // VMS
#define OT_DBA_TABLE		 15			// DB Agent Table


#define OT_WS_NT             20         // NT workstation
#define OT_WS_CHICAGO        21         // Chicago workstation
#define OT_SERVER_AGENT      22    		// NetWare Server with NW Agent
#define OT_WS_NT_AGENT       23    		// NT workstation with NT Agent
#define OT_WS_FTP            24    		// Ftp site
#define OT_MBO_TAPE          25			// MBO Tape   //for CDBTREE display only
#define OT_TANDEM_INDEX_0    26	        //for Tandem from DBA
#define OT_TANDEM_INDEX_1    27
#define OT_TANDEM_AUDIT      28
#define OT_TANDEM_AGENT      29
#define OT_TANDEM_SQLNODE    30
#define OT_TANDEM_CATALOG    31
#define OT_TANDEM_SCHEMA     32
#define OT_TANDEM_TABLE      33
#define OT_TANDEM_VIEW       34
#define OT_TANDEM_MODULE     35
#define OT_TANDEM_PARTITION  36

#define OT_NDS_TREE			 37			//NDS Tree 
#define OT_NDS_BRANCH		 38			//NDS Container object
#define OT_NDS_LEAF			 39			//NDS leaf object

#define OT_NAS               50         //for NAS
#define OT_NT_SYM            51         //NT EMC Symmetrix
#define OT_GROUPWISE		 52			//for GroupWise
#define OT_GROUPWISE_DOMAIN	 53			//for GroupWise Domain
#define OT_GROUPWISE_PO		 54			//for GroupWise PO

//akusr01/11AUG2005/14160809 - To identify cluster server - virtual cluster node
#define OT_NWCLUSTER		57	//for cluster volumes
#define OT_NWCLUSTER_DIR	58	//for Directory under cluster volumes
#define OT_NWCLUSTER_FILE	59	//for File under cluster volumes

// PL 06/Mar/03 Required to identify the various VSS types in the database
#define OT_VSS_WRITER					71	// Writer
#define OT_VSS_COMPONENT				72	// Component
#define OT_VSS_COMPONENT_SELECTABLE		73	// Component that is selectable for restore
#define OT_VSS_LOGICALPATH				74  // Logical path

#ifdef ARC_SAVE_NODEINFO
#define OT_MSDPM_ROOT                   75    
#define OT_MSSQL_ROOT                   76          
#define OT_MSSHP_ROOT                   77          
#define OT_MSEXG_ROOT                   78       
#define OT_ORACLE_ROOT                  79       
#define OT_SYBASE_ROOT                  80       
#define OT_INFMIX_ROOT                  81       
#define OT_LOTUS_ROOT                   82   
#endif

#define OT_DBAEXSIS			 90			// MS Exchange Document Level
#define OT_DBAEXDBVSS			 91			

// liuwe05 2005-11-15 to support Exchange agent DB-Level for r11.5 FP2
#define OT_DBAEXDB                       92         // MS Exchange Database Level (Push)


#ifdef SPS_2007_CHANGES
#define OT_SPAGENT2007                       95
#endif


// liuwe05 2009-02-11 for GUI to support E14
#define OT_DBAEDBVSS                   96     // for MS Exchange 14 Database Level


//
//   Name Space Type
//
#define NS_DOS               0          // Dos File Name 8.3 Format
#define NS_MAC               1          // Macintosh File Name up to 32 chars
#define NS_NFS               2          // UNIX (or NFS) file name up to 256
#define NS_FTAM              3          // FTAM file name up to 256
#define NS_OS2               4          // OS/2 file name up to 255
#define NS_NT                5          // Window NT
#define NS_CHICAGO           6          // Chicago
#define NS_NT_IMAGE          7          // Window NT Image

//
//   File System Type
//
#define FST_UNKNOWN          0         // The file system is unknown
#define FST_FAT              1         // The file system is FAT
#define FST_HPFS             2         // The file system is HPFS
#define FST_NTFS             3         // The file system is NTFS
#define FST_MAC              4         // The file system is MAC
#define FST_FTAM             5         // The file system is FTAM
#define FST_NW2X             6         // The file system is NetWare 2.x
#define FST_NW3X             7         // The file system is NetWare 3.x
#define FST_NW4X             8         // The file system is NetWare 4.x

//
//   ARCserve Job Type
//
#define AJT_DISKBACKUP       0         // Backup to Disk
#define AJT_TAPEBACKUP       1         // Backup to Tape
#define AJT_APDISKBACKUP     2         // Backup to Disk w/ AutoPilot
#define AJT_APTAPEBACKUP     3         // Backup to Tape w/ AutoPilot
#define AJT_APDISKMAKEUP     4         // Makeup backup to Disk w/ AutoPilot
#define AJT_APTAPEMAKEUP     5         // Makeup backup to Tape w/ AutoPilot
#define AJT_DISKRESTORE      6         // Restore from Disk
#define AJT_TAPERESTORE      7         // Restore from Tape
#define AJT_MERGETAPE        8         // Merge Tape to AS DB
#define AJT_DBBACKUP         9         // Selective DB Backup
#define AJT_DBRESTORE        10        // Selective DB Restore
#define AJT_COMPARE          11        // Compare Disk to Tape
#define AJT_COUNT            12        // Count - Attended
#define AJT_PURGE            13        // Purge - Attended
#define AJT_SCANTAPE         14        // Scan Tape - Attended
#define AJT_DISKGROOM        15        // Disk Grooming
#define AJT_TAPECOPY         16        // Tape to Tape Copy
#define AJT_DBRECOVER        17        // Recover AS DB - Attended
#define AJT_DBPRUNE          18        // Prune AS DB - UnAttended
#define AJT_VIRUSSCAN		 19
#define AJT_ROTATIONBACKUP   20
#define AJT_ROTATIONMAKEUP   21
#define AJT_CONVERSIONJOB    22            // Conversion Job for NetWare 
#define AJT_LITEBACKUP       23		   // Backup ARCserve home directory and system directory
#define AJT_LITERESTORE      24		   // Restore ARCserve home directory and system directory
#define AJT_NNPRESTORE       25		   // Restore of None Netware Partition
#define AJT_HSM	             26
#define AJT_MERGEDB	     27        // Merge ARCserve DB to ARCserve DB 
#define AJT_COPYTOMVS	     28	       // Copy Media to MBO (MVS)
#define AJT_VERIFY           29        //Verify job for Unix
#define AJT_DBA_BACKUP       30        // DB Agent Backup
#define AJT_DBA_SAP          31        // DB Agent SAP Backup
//#define AJT_TAPECLEANING	 32	       // Unix use this type for// tape cleaning job		
#define AJT_BACKUPSESSIONCDR 32		   //Backup OBDR session cdr file (Netware)
#define AJT_RESTORESESSIONCDR 33	   //Restore OBDR session cdr file (Netware)

#define AJT_NDMPBACKUP            34     // Backup using NDMP protocol
#define AJT_NDMPAPTAPEBACKUP      35     // Backup to Tape w/ AutoPilot using NDMP protocol
#define AJT_NDMPAPTAPEMAKEUP      36     // Makeup backup to Tape w/ AutoPilot using NDMP protocol
#define AJT_NDMPROTATIONTAPEBACKUP    37 // Backup to Tape w/ Rotation using NDMP protocol
#define AJT_NDMPROTATIONTAPEMAKEUP    38 // Makeup to Tape w/ Rotation using NDMP protocol
#define AJT_NDMPTAPERESTORE       39      // Restore from Tape using NDMP protocol
#define AJT_ASM_BACKUP			40		// ASM backup 
#define AJT_ASM_RESTORE			41		// ASM restore
#define AJT_ASM_MERGE			42		// ASM merge 
#define AJT_NDMPMERGETAPE		43		// These values are used by the UNIX product for NAS. Not used by NT
#define AJT_NDMPSCANTAPE		44		// These values are used by the UNIX product for NAS. Not used by NT
#define AJT_DEVICE_MANAGEMENT   45		// Device Management
//#define AJT_DMJOB_FORMAT      46
//#define AJT_DMJOB_ERASE       47
#define AJT_DATA_MIGRATION		48		//Data-Migration Job for a B2D2T job.
#define AJT_DATA_PURGE			49		//Data-Purge Job for a B2D job or B2D2T job.

  
//
//   Format Code Type
//
#define FMT_PRIME            0          // Dumb streamer - File marks
#define FMT_ARCHIVE          1          // Intelligent - Volume Table
#define FMT_EXABYTE          2          // Exabyte special dumb streamer
#define FMT_FASTSEEK         3          // DAT drives
#define FMT_RAID                 0x0010          //RAID
#define FMT_MTF                  0x0080          //MicroSoft Tape Format
#define FMT_CA_UNICENTER_NT      0x0081          //CA Unicenter for NT
#define FMT_CA_UNICENTER_UNIX    0x0082          //CA Unicenter for UNIX
#define FMT_NA                   0xfffe          // For Optical Tape Format is not applicable.
#define FMT_UNKNOWN              0xffff          //unknown tape format

//
//    Tape Media Type
//
#define TMT_CARTRIDGE        0          //
#define TMT_4MM              1          //
#define TMT_8MM              2          //

//
//    Tape Type
//
#define TT_ARCSERVE        0           //
#define TT_RETIRED         1           //
#define TT_SIDF				2
#define TT_HSM				   3
#define TT_RAID            4			// 4 - 9 are reserved by raid level 0,1,5
#define TT_MTF				   10			// Microsoft Tape Format 
#define TT_MBO				   11			// MBO Type
#define TT_BLANK           12
#define TT_NON_CHEYENNE    13

//
//    Cheyenne defined Board Type
//
#define CBT_ADAPTEC          0          //
#define CBT_BUSTEK           1          //
#define CBT_QIC02            2          //

//
//    Cheyenne defined Device Type
//
#define CDT_REGULAR          0          //
#define CDT_STACKER          1          //
#define CDT_CHANGER          2          //
#define CDT_JUKEBOX          3          //

//
//    Archive Session Type
//
#define AST_WS_ARCHIVE       1
#define AST_FS_ARCHIVE       2

//
//    AutoPilot Target Type
//
#define ATT_NW_FS            0          // File Server
#define ATT_IPX              1          // IPX Workstation
#define ATT_MAC              2          // Mac Workstation
#define ATT_UNIX             3          // Unix Workstation
#define ATT_DATABASE		    10


#endif /* __ASDBTYPE_H */
