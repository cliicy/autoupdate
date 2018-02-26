// Note: If you change the define number, you need at least change astask_e.rc  JC

#ifndef _fileclass_
#define _fileclass_

#define BFC_STANDARD	0               // Normal file

#define BFC_REGISTRY	1				// Windows NT Registry file
#define BFC_EVENT_LOG	2				// Windows NT Event Log
#define BFC_HDLNK_1ST	3				// Windows NT Hard Link (Primary)
#define BFC_HDLNK_2ND	4				// Windows NT Hard Link (Secondary)
#define BFC_CATALOG		5				// Arcserve Catalog File
#define BFC_EISA		6				// EISA Configuration File
// JCL - 19990604
#define BFC_ROOT		10				// as c:

#define BFC_ADS_DB		11				// Active Directory Service Database
#define BFC_ADS_LOG		12				// Active Directory log
//#define BFC_ADS_PATCH   18              // Active Directory Patch File

#define BFC_COM_DB		13				// COM+ Database
            
#define BFC_RSM_DB		14				// Removable Storage Management Database
#define BFC_RSS_DB		15				// Remote Storage Service Database
#define BFC_SFC_CAT		16				// Protected System File Catalog
#define BFC_SFC_ITEM	17 				// Protected System File Item

#define BFC_ADS_PATCH   18              // Active Directory Patch File

#define BFC_CS_PATCH	19				// Certificate Service patch file
#define BFC_CS_DB		20				// Certificate Server Database
#define BFC_CS_LOG		21				// Certificate Server Database Logs

#define BFC_CLUSTER		22				// Cluster Files
#define BFC_SYSVOL		23				// SYSVOL files

#define	BFC_DLLCACHE	24				// For special handling of \Dllcache files during restore
#define BFC_TLS_DB		25				// for special handling of terminal services licensing database	
#define BFC_CI			26				// Content Indexing Server Files
#define BFC_LINKLOG		27				// Link Tracking Log Files
#define BFC_SIS			28				// SIS Common Store db
			
#define BFC_WMI_DB		29				// WMI DB file
#define BFC_SRVLESS		30				// backed up by symm 
#define BFC_SQL_PERF	31				// SQL mini agent stream
#define BFC_EXT_SH		32			    // Extended session header file
#define BFC_SKIP		99
#define BFC_SKIP_FILE   0xFF			// Intended as a skip directive

#define BFC_FSRM        33              // FSRM Writer's file, only valid under Windows 2003 R2
#define BFC_IISMETA     34              // IIS Metabase Writer's file

#define BFC_META		35				// Complete Database file list as a single file entry
#define BFC_CHKPT		36				// Time Stamp to use for Stop-At for Transaction Log restores
#define BFC_PREREQ		37				// File Type that contains a list of known copies of a pre-requisite session
//#ifdef R12V_VM_WIN32AGENT
#define BFC_VMIFS_META	38				// VMWARE_AGENT, VM volume metadata
#define BFC_PARTIALFILE 39				// R12V_SQL_DIFF, partial file backup
//#endif //R12V_VM_WIN32AGENT
#define BFC_VMIFS_META_APP	40			// file class for application sub-session metadata, BACKUP_BLI

#endif
