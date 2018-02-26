#ifndef _AF_BACKUP_COMMON_H
#define _AF_BACKUP_COMMON_H
#pragma once 

#include "AFJob.h"

#include <vector>
using namespace std;

#include <tchar.h>

#include "ctf.h"

#include "AFStor.h"
#include "DRCore.h" //baide02 2009-07-14

#include <atlstr.h>
#if 0
#include "..\..\Native\FlashCore\AFBackupDll\FileFolderUtilities.h" //<sonmi01>2009-12-17 threshold check
#else
//<sonmi01>2009-12-17 threshold check
class CDestinationThreshold
{
private:
	CString m_strDestinationRootPath;
	ULONGLONG m_ullThresholdSize;
	DWORD m_dwJobId;

	BOOL m_bThresholdHit;
	BOOL m_bThresholdLogged;

public:
	CDestinationThreshold(LPCTSTR szDestRoot, ULONGLONG ullThresholdSize, DWORD dwJobId);

	virtual ~CDestinationThreshold();

	BOOL CheckThreshold();

};
#endif

//e:\140bli\brightstor r11.5#\BB\Win32Agent\Ntagdll\NTUTIL.H
DWORD	GetDiskSignature(LPCTSTR pszDisk, PDWORD pDiskSign);

//e:\140bli\brightstor r11.5#\bb\h\pushagnt.h
#define		BLI_BEGIN_VOLUME		110		// zhoru02, BACKUP_BLI - begin a volume replication
#define		BLI_CREATE_DISK			111		// command to create a VHD file for a disk
#define		BLI_CLOSE_DISK			112		// command to close the VHD file
#define		BLI_WRITE_DISK			113		// command to write data to the VHD file
#define		BLI_VOLUME_BITMAP		114		// volume bitmap
#define		BLI_VOLUME_EXTENTS		115		// volume disk extents information
#define		BLI_END_VOLUME			116		// end a volume replication
#define		BLI_GET_VOLUME_METHOD	117		// get volume backup method
#define		BLI_RESYNC_DATA_OFFSET	118		// resync data offset sent from agent to task
#define		BLI_RESYNC_HASH			119		// hash sent from Task to agent
#define		BLI_APP_VSSINFO 		142		// zhoru02, BACKUP_BLI
#define		BLI_SESSION_ESTIMATION		143		// linyu04, session estimation, BACKUP_BLI

#include <winioctl.h>

//e:\140bli\brightstor r11.5#\bb\h\pushagnt.h
// BACKUP_BLI
typedef struct __BLI_DISK_CREATE {
	DWORD dwDiskID;					// disk signature
	DISK_GEOMETRY diskGeometry;		// disk geomotry
	LARGE_INTEGER i64DiskSize;
	LARGE_INTEGER i64UsedDiskSize;  //data size on the disk 
	CHAR szReserved[64];
	//PVOID pDiskExtents;
	IAFStorBuf* pDiskExtentsBufInterface;
} BLI_DISK_CREATE, *PBLI_DISK_CREATE;

typedef struct __BLI_DISK_WRITE {
	DWORD dwDiskID;					// disk signature
	LARGE_INTEGER i64Offset;		// disk position of the data
	DWORD dwLength;					// data size
} BLI_DISK_WRITE, *PBLI_DISK_WRITE;

typedef struct __BLI_VOL_BITMAP {
	WCHAR szVolRoot[MAX_PATH];		// volume root path
	DWORD dwSize;					// size of cBitmap
	CHAR  cBitmap[1];				// VOLUME_BITMAP_BUFFER 
} BLI_VOL_BITMAP, *PBLI_VOL_BITMAP;

typedef struct __BLI_VOL_UUID {
	WCHAR wszVolumeName[64];
	WCHAR wszVolumeGUID[64]; //<sonmi01>2010-2-3 enhance for volume filtering
	WCHAR wszUUID[64];
} BLI_VOL_UUID, *PBLI_VOL_UUID;

typedef struct __BLI_APP_METADATA {
	DWORD dwType;
	DWORD dwMetadataSize;			// size in WCHAR 
	WCHAR wszFileName[MAX_PATH];
	WCHAR wszMetadata[1];
} BLI_APP_METADATA, *PBLI_APP_METADATA;

typedef struct __BLI_SESS_ESTIMATION {
	LARGE_INTEGER	estimateKBytes;		//bli backup, bytes to be processed, sent by agent
	LONG			estimateFiles;
	CHAR			szReserved[64];
}BLI_SESS_ESTIMATION, *PBLI_SESS_ESTIMATION;

//e:\140bli\brightstor r11.5#\bb\h\pushagnt.h
typedef struct _FSAG_TLI_HEADER 
{
	unsigned short	sequenceNo; /* packet sequence num */
	char			command;
	char			device;     /* arcserve/agent version */
	unsigned short	status;     /* platform */
	unsigned short	blkOffset;  /* data length in uData */
}	FSAG_TLI_HEADER, *PFSAG_TLI_HEADER;

//e:\140BLI\BrightStor r11.5#\BB\Win32Agent\Ntagdll\Wa_type.h
// Buffer sizes
#define BUFFER_SIZE_1K		(1024)
#define BUFFER_SIZE_MTU		(1492)	// 1492 - the smallest MTU in a network
#define BUFFER_SIZE_4K		(4096)
#define BUFFER_SIZE_16K     (16384)
#define BUFFER_SIZE_32K     (32768)
#define BUFFER_SIZE_48K     (49152)
#define BUFFER_SIZE_64K		(65536)
#define BUFFER_SIZE_60K		(61440)
#define BUFFER_SIZE_120K    (122880)
#define BUFFER_SIZE_128K    (131072)
#define BUFFER_SIZE_180K    (131072)
#define BUFFER_SIZE_192K    (196608)
#define BUFFER_SIZE_240K	(245760)
#define BUFFER_SIZE_256K    (262144)

//#ifdef	R12_V
typedef struct __VM_VOL_DISK_INFO {
	WCHAR		wszVMDKName[MAX_PATH];	// vmdk file name for this extent
	ULONG		ulDiskNum;				// BACKUP_BLI, disk number
	ULONG		ulSignature; 				// disk signature
	LONGLONG	ulExtentStartPos;			// The offset from the beginning of the disk to the extent, in bytes.
	LONGLONG	ulExtentLength;			//The number of bytes in this extent
	LONGLONG	ulVolumeOffset;			// volume offset corresponding to the start of this extent (ulExtentStartPos)
	ULONG		ulSectorSize;				// size of every sector on the disk. VMware & MSFT virtual solutions have it fixed to 512 bytes, kept for future compatibility.
}VM_VOL_DISK_INFO, *PVM_VOL_DISK_INFO;

typedef vector<VM_VOL_DISK_INFO *> VEC_VM_VOL_DISK_INFO;

#define VM_VOL_UNFORMATED		0x00000001	// the volume is not formated
#define VM_VOL_MOUNTPOINT		0x00000002	// no drive letter but mount point
#define VM_VOL_NOT_MOUNTED		0x00000004	// no drive letter and no mount point
#define VM_VOL_FS_FAT			0x00000008	// If this flag is not specified assume volume is NTFS
#define VM_VOL_FS_FAT32			0x00000010  // the volume is FAT32
#define VM_VOL_FS_REFS          0x00000020  // the volume is REFS
#define VM_VOL_FS_NTFS          0x00000040  // the volume is NTFS
#define VM_VOL_DEDUPE           0x00000080  // the volume is DEDUPE
#define VM_VOL_NOT_FIXED        0x00000100  // the volume is not fixed

//E:\140BLI\BrightStor r11.5#\BB\Win32Agent\h\asagcmd.h
typedef struct __VM_VOL_INFO {
	WCHAR wszVol_GUID[64];		// volume ID
	WCHAR wszVolName[MAX_PATH];	// drive name or mount point
	ULONG ulOptions;   			// formatted, mountpoint, etc
	ULONG ulNumOfDisks;			// number of disks 
	VM_VOL_DISK_INFO vm_disks[1];
}VM_VOL_INFO, *PVM_VOL_INFO;

typedef vector<VM_VOL_INFO *> VEC_VM_VOL_INFO;
typedef vector<VM_VOL_INFO *>::iterator IT_VEC_VM_VOL_INFO;

typedef struct __VM_APP_INFO {
	WCHAR wszApp_GUID[64];		// App writer ID
	WCHAR wszAppName[128];		// App name
	ULONG ulOptions;			// Application related options
	ULONG ulAppMetadatasize;	// metadata length in WCHAR
	ULONG		ulApp;			// Application VM_EXCH/VM_SQLSRVR,VM_SPS
	__VM_APP_INFO	*pNext;
	WCHAR wszAppMetadata[1];	// App writer metadata
} VM_APP_INFO, *PVM_APP_INFO;

typedef vector<PVM_APP_INFO> VEC_VM_APP_INFO;
typedef vector<PVM_APP_INFO>::iterator IT_VEC_VM_APP_INFO;

#define EXCH	1
#define SQLSRVR	2
#define SPS		3

typedef enum __VM_APP_TYPE
{
	VM_EXCH = 1,
	VM_SQLSRVR,
	VM_SPS,
	VM_FIRST_APP = VM_EXCH,
	VM_LAST_APP = VM_SPS
}VM_APP_TYPE;

int /*ASAGCMD_API*/ GetAppInfo(PVM_APP_INFO *pAppInfo, int iApptype);

//e:\140BLI\BrightStor r11.5#\BB\Win32Agent\Ntagdll\Ntutil.cpp
#define PASSWORD_LEN	16
#define MAX_PARTITIONS	32		//for Get disk signature.
#define MAX_ITEMS		10

//e:\140BLI\BrightStor r11.5#\BB\Win32Agent\Ntagdll\vmware.h
#define VM_IFS_CATALOG_NAME		_T("ARC_VM_IFS__.CAT")	// internal session catalog file
#define VM_IFS_METADATA_FILE	_T("$$ImgMetaData$$.000")
#define VM_IFS_CLUSTER_MAP_FILE	_T("ClustM$$.000")			// cluster map
#define VM_IFS_DIR_STRUCT_FILE	_T("DirStr$$.000")			// directory structure file
#define VM_IFS_DACE_FILE		_T("$dace$$$.000")			// NTFS, DACE info
#define VM_IFS_SACE_FILE		_T("$sace$$$.000")			// NTFS, SACE info
#define VM_IFS_SECURITY_FILE	_T("$Secur$$.000")			// NTFS, security info
#define IMG_TEMP_STREAM_DIR		_T("NTFS_DUDP_META")
#define	VM_PREMOUNT_FAILURE		2000
#define	VM_POSTMOUNT_FAILURE	2001

#define VM_MAX_VMDK_FILES	128

typedef struct _IMAGE_METADATA_STATS
{
	ULONG	ulTotalDirs;
	ULONG	ulTotalFiles;
	ULONG	ulTotalDiskKB;
}IMAGE_METADATA_STATS, *PIMAGE_METADATA_STATS;

typedef struct _AS_APP_METADATA		// BACKUP_BLI
{
	GUID uuidApp;
	PWCHAR pwszBCD;
	DWORD dwBCDSize;
	PWCHAR pwszWriterMetadata;
	DWORD dwWriterMetadataSize;
}AS_APP_METADATA, *PAS_APP_METADATA;

typedef vector<WCHAR *> VEC_METADATA_PATH;
typedef vector<WCHAR *>::iterator IT_VEC_METADATA_PATH;

typedef vector<IMAGE_METADATA_STATS *> VEC_METADATA_STATS;
typedef vector<IMAGE_METADATA_STATS *>::iterator IT_VEC_METADATA_STATS;

typedef vector<PAS_APP_METADATA>		VEC_AS_APP_METADATA;		// BACKUP_BLI
typedef VEC_AS_APP_METADATA::iterator	IT_VEC_AS_APP_METADATA;

// VMWARE_AGENT
typedef struct vmwareImageFSParm_
{
	VEC_VM_VOL_INFO vecVolInfo;
	VEC_VM_VOL_DISK_INFO vecSortedDiskInfo;  // For internal session restore.
	VEC_METADATA_PATH vecMetadataPath;
	VEC_METADATA_STATS vecMetaDataStats;
	VEC_AS_APP_METADATA vecAppInfo;				// BACKUP_BLI
	PVOID pTapexNextVMIFS;
	ULONG ulMountTime;
	ULONGLONG ullLastStreamNum;
	ULARGE_INTEGER liDataStreamSize;
	ULARGE_INTEGER liStreamOffset;
} VMWareImageFSParm;

//e:\140BLI\BrightStor r11.5#\BB\Win32Agent\Ntagdll\commonstr.h
#define MAX_NT_PATH		512			// 1024 is too large to stack !


// BACKUP_BLI
#define BLI_MACHINE_NAME_LEN						128
#define BLI_VOLUME_NAME_LEN							64

//e:\140bli\brightstor r11.5#\bb\h\asdb.h
#define ASDB_BLI_GET_ALL						  0
#define ASDB_BLI_GET_BY_AGENT_VOLUME_HOST		  100
typedef struct tagASDB_BLIVolumeBackupInfoParm{
	WCHAR wszAgentMachineName[BLI_MACHINE_NAME_LEN];
	WCHAR wszVolumeName[BLI_VOLUME_NAME_LEN];
	WCHAR wszHostName[BLI_MACHINE_NAME_LEN];
	WCHAR tapename[24];			// Interface (UNICODE) but schema is using ANSI (char: 24) because all other tables are using it as char(24)
	short randomid;		
	long  lFlag;				// ASDB_BLI_GET_ALL / ASDB_BLI_GET_BY_AGENT_VOLUME_HOST
	char  reserved[130];
}ASDB_BLIVolumeBackupInfoParm, *PASDB_BLIVolumeBackupInfoParm;


typedef struct tagASDB_BLIVolumeBackupInfo{
	WCHAR wszAgentMachineName[BLI_MACHINE_NAME_LEN];
	WCHAR wszVolumeName[BLI_VOLUME_NAME_LEN];
	WCHAR wszVolumeGUID[BLI_VOLUME_NAME_LEN]; //<sonmi01>2010-2-3 enhance for volume filtering
	WCHAR wszHostName[BLI_MACHINE_NAME_LEN];
	WCHAR wszUUID[64];
	WCHAR tapename[24];			// Interface (UNICODE) but schema is using ANSI (char: 24) because all other tables are using it as char(24)
	short randomid;		
	ULONG ulSessionNum;
	long  sessionTime;
	char  reserved[130];
}ASDB_BLIVolumeBackupInfo, *PASDB_BLIVolumeBackupInfo;

//<MyCode> 2009-7-4 @@@###
typedef struct tagASDB_BLIVolumeBackupInfoRecord{
	//WCHAR wszAgentMachineName[BLI_MACHINE_NAME_LEN];
	WCHAR wszVolumeName[BLI_VOLUME_NAME_LEN];
	WCHAR wszVolumeGUID[BLI_VOLUME_NAME_LEN]; //<sonmi01>2010-2-3 enhance for volume filtering
	//WCHAR wszHostName[BLI_MACHINE_NAME_LEN];
	GUID Guid;
	//WCHAR tapename[24];
	//short randomid;		
	ULONG ulSessionNum;
	LONG  sessionTime;
	//char  reserved[130];
}ASDB_BLIVolumeBackupInfoRecord, *PASDB_BLIVolumeBackupInfoRecord;
//</MyCode>

//E:\140BLI\BrightStor r11.5#\BB\h\pushagnt.h
typedef struct __ASAG_FILE_TRANSFER_HEADER {
	ULONG ulOptions;
	ULONG ulIndex;			// In case of FT_VM_INT_CATALOG, ulIndex will the subsession # of the catalog file
	CHAR reserved[120];
	ULONG ulKbOnDisk;
	ULONG ulFiles;
	ULONG ulFileNameLen;   // in bytes
	TCHAR pszFileName[1];
} ASAG_FILE_TRANSFER_HEADER, *PASAG_FILE_TRANSFER_HEADER;

#define ASAG_FILE_TRANSFER_HEADER_SIZE		136

//e:\140bli\brightstor r11.5#\bb\tasks\backup\asbackup.h
typedef struct _VMWARE_SUBSESS_INFO 
{
	CTF_SESSION_HEADER  SubSessHeader;	
	ULONG				sSubSessNum;
	ULONG				ulTotalKb;				//total kb 
	ULONG				ulTotalFiles;			//total files
}VMWARE_SUBSESS_INFO, *PVMWARE_SUBSESS_INFO;

typedef struct _VMWARE_CTL {
	ASAG_FILE_TRANSFER_HEADER	ASAGFTHeader;
	HANDLE						hVMWareMetaData;
	INT							nMetaDataFileIndex;
	TCHAR						szMetaDataFileName[1024];
	BOOL						bDeleteMetaDataFile;
	PVMWARE_SUBSESS_INFO        pVMWareSubSessInfo;			//dynamically allocate 32 items for each VMIFS session
} VMWARE_CTL, *PVMWARE_CTL;

#define VCB_MAX_HOST_NAME 256

//E:\140BLI\BrightStor r11.5#\BB\h\tsi_inc.h
typedef struct _TSI_FS_CREDENTIAL
{
	ULONG ulEncDataSize; //If 0, it means no credential.
	union
	{
		struct  
		{
			WCHAR    szUserName[64];
			WCHAR    szPassword[64];
			WCHAR    szDomainName[64];
		}Credential;
		unsigned char argEncBuf[2048];
	};
}TSI_FS_CREDENTIAL, *PTSI_FS_CREDENTIAL;

//e:\140bli\brightstor r11.5#\bb\tasks\backup\asbackup.h
typedef struct _BACKUP_BLI_CTRL {
	HANDLE				hBLI;							// BLI data handler, BACKUP_BLI
	ULONG				ulBLIHeaders;					// number of BLI header written
	ULONG				ulBLIDiskID;					// the current disk ID of the VHD file
	BOOL				bFlushDummyData;				// flush dummy data to TE
	ULONG				ulBytesInDummyFlush;			// the total bytes flushed along with dummy flush command
	ULARGE_INTEGER		uliFlatVHDSize;					// current VHD file size in flat format
	ULARGE_INTEGER		uliBackedUPVHDSize;				// The total flat size of VHD files have been backed up
	ULARGE_INTEGER		uliAllDynamicVHDSize;			// The total dynamic size of all VHD files
	DWORD				ulStatus;						// Volume Status
	BOOL				bVMImageErr;					//VMImage module returns error - <linyu04>Compare
	BOOL				bReleaseSession;				//ReleaseSession - <linyu04>Compare
	BOOL				bBLIPassBeginSession;			// BLI PassBeginSession
	BOOL				bBLIStartedDBSession;			// BLI insert DB record
	TSI_FS_CREDENTIAL	tsiFsCred;						//<sonmi01>2009-5-18 Remote BLI FSD //BACKUP_BLI
	BLI_SESS_ESTIMATION	bliSessEstimation;				// BLI session estimation info from agent

	//<sonmi01>2009-6-24 ###???
	PAFJOBSCRIPT		pAFJobscript;
	ULONG				ulSessionNumber;
	IAFStorDev*			pAFStorDev;
	IAFSession*			pAFSession;
	PVMWARE_CTL			pVMWareCtl;
	VMWareImageFSParm*	pvmwareIFS;
	ULONG				ulSubSessionPad;
	ULONG				ulLastSessNumber;				// last known good session number
	//</sonmi01>

	//linyu04
	CTF_SESSION_HEADER	sessionHeader;
	HANDLE				hCatFile;							//catlog file handle
	TCHAR				pszWriterName[MAX_PATH];			
	TCHAR				pszComponentName[MAX_PATH];
	TCHAR				pszFileName[MAX_NT_PATH]; 
    TCHAR               volumePath[MAX_NT_PATH];

	//<sonmi01>2009-7-10 ###???
	BackupInfoXml		BackupInfo;		
	//</sonmi01>

	//<sonmi01>2009-9-14 ###???
	ULONG				ulOriginalJobMethod;
	BOOL				bFailToPurgeFailedSession; //<sonmi01>2009-9-19 ensure purging failed session
	//</sonmi01>

	//<sonmi01>2009-12-17 threshold check
	CDestinationThreshold * m_DestinationThreshold;
	INT m_FailedReason; //<sonmi01>2010-1-14 BLI license check
	FILETIME m_ftBackupStart;

    //ZZ: Added for key management.
    GUID                guidSession;
} BACKUP_BLI_CTRL, *PBACKUP_BLI_CTRL;

//E:\140BLI\BrightStor r11.5#\BB\h\tsi_inc.h
// Define SERIAL_NUM_LENGTH if not already defined
#ifndef SERIAL_NUM_LENGTH
#define SERIAL_NUM_LENGTH  32
#endif

//E:\140BLI\BrightStor r11.5#\BB\h\pushagnt.h
typedef struct _FILE_HEADER 
{
	ULONG	signature;
	UCHAR   relativePathName[250];  // Relative to the starting directory
	UCHAR	longName[33];           // Macintosh long name
	UCHAR   level;                  // Directory level
	ULONG	time;                   // file or dir's last-modify date&time
	ULONG	fSize;                  // file size
	ULONG	rSize;                  // resource fork size
	ULONG	attributes;             // file or dir attributes
	ULONG	ownerID;                // owner ID
	USHORT  mask;                   // Max. Rights Mask (286) or Inherited Mask (386)
	UCHAR   cFileClass;
	ULONG	trusteeLength;          // Length of trustee portion of data
	ULONG	dir_space;              // Directory space restriction
	USHORT	lastAccessDate;			// Last access date for file
	ULONG	creationDT;					// Create Date/Time for file/directory
	ULONG	ChunkNum;
	ULONG	ChunkOffset;
	UCHAR   reserved[14];
}	FILE_HEADER, *PFILE_HEADER;
#define	FILE_HEADER_SIZE		sizeof(FILE_HEADER)

//E:\140BLI\BrightStor r11.5#\BB\h\pushagnt.h
#ifndef TSI_MAX_TAPE_NAME
#define TSI_MAX_TAPE_NAME	24
#endif

//e:\140bli\brightstor r11.5#\bb\h\asdbtype.h
// PL 06/Mar/03 Required to identify the various VSS types in the database
#define OT_VSS_WRITER					71	// Writer
#define OT_VSS_COMPONENT				72	// Component
#define OT_VSS_COMPONENT_SELECTABLE		73	// Component that is selectable for restore
#define OT_VSS_LOGICALPATH				74  // Logical path


#define OT_VSS_SQL_WRITER					10		//SQL writer
#define OT_VSS_SQL_COMPONENT				11		//SQL Component		
#define OT_VSS_SQL_COMPONENT_SELECTABLE		12		//SQL Selectable Component
#define OT_VSS_SQL_NODE						13		//SQL Machinename
#define OT_VSS_SQL_LOGICALPATH				14		//SQL Logical Path

#define OT_VSS_EXCH_WRITER					20		//EXCH writer
#define OT_VSS_EXCH_COMPONENT				21		//EXCH Component					
#define OT_VSS_EXCH_COMPONENT_SELECTABLE	22		//EXCH Selectable Component
#define OT_VSS_EXCH_NODE					23		//EXCH Machinename
#define OT_VSS_EXCH_LOGICALPATH				24		//EXCH Logical Path
#define OT_VSS_EXCH_SERVER					25		//EXCH Server name
#define OT_VSS_EXCH_INFOSTORE				26		//EXCH Infomation Store
#define OT_VSS_EXCH_COMPONENT_PUBLIC        27      //EXCH public folder or public database
#define OT_VSS_EXCH_NODE_REPLICA            28      //EXCH replica node(only for replica writer)


//ZZ: Added for Exchange GRT to shown different icon for EDB or public folder.
#define OT_VSS_EXCH_DBAEXSIS_MBSDB               255
#define OT_VSS_EXCH_DBAEXSIS_PUBLIC_FOLDERS      254

#define OT_DIRECTORY         6          // Directory
#define OT_FILE              7          // File

//e:\140bli\brightstor r11.5#\bb\win32agent\h\asdb.h
// R12.v
#define ASDB_MASTER_SESSION_IDENTITY	0x0FFFFFFF
#define ASDB_ONLY_INTERNAL_SESNUM	100
#define ASDB_ONLY_MASTER_SESNUM		101

#define ASDB_VM_VMTYPE_VMWARE	0
#define ASDB_VM_VMTYPE_HYPERV	1
#define ASDB_VM_VMTYPE_BLI		2			// BACKUP_BLI
#define ASDB_VM_VMTYPE_ALL		900

//ZZ: Maximal timeout value for check disk operation after volume restore during BMR. 
#define MAX_BMR_CHECK_DISK_TIMEOUT_VALUE    (2 * 60 * 60 * 1000)

#endif //_AF_BACKUP_COMMON_H