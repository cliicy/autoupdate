#ifndef DIRECTORY_H
#define DIRECTORY_H

#define _MAX_SH_PATH            128
#define _MAX_SH_OWNER           48
#define _MAX_SH_PW              24
#define _MAX_SH_DESC            80
#define _MAX_TAPE_NAME          24
#define _MAX_ENCRYPTION_KEY		24
#define _MAX_NT_PATH            512
#define _EXTEND_MAX_NT_PATH 4096

#define   BSF2_UNICODE_SESSION           0x00000100  // Backed Up session is a Unicode Session
/*
#define OT_DIRECTORY         6          // Directory

#pragma pack(1)

typedef struct  _CTF_SESSION_HEADER 
{
   ULONG   signature;					//0   // Session Signature	
   CHAR    rootDirectory[_MAX_SH_PATH]; //4   // Root directory for session
   CHAR    ownerName[_MAX_SH_OWNER];    //132 // Owner of session
   CHAR    password[_MAX_SH_PW];        //180 // Session Password
   CHAR    description[_MAX_SH_DESC];   //204 // one line comment
   USHORT  sessionType;                 //284 // see BST_xxx
   UCHAR   sessionMethod;               //286 // see BSM_xxx
   ULONG   sessionFlags;                //287 // see BSF_xxx
   UCHAR   compressionType;				//291 // see BCT_xxx
   UCHAR   compressionLevel;			//292 // see BCL_xxx
   UCHAR   fsNameLength;				//293 // UNIX file system name length in rootDirectory 

   UCHAR   size1;          // 294 // size of size of Encrypted encryption key (by password-based key) 
   UCHAR   size2;          // 295 // size of Encrypted encryption key (by BAB internal key) 
   UCHAR   data1[12];     // 296 // second part of Encrypted encryption key (by password-based key) 
   UCHAR   data2[12];      // 308 // second part of Encrypted encryption key (by BAB internal key)

   // This definition should be replaced with CTF_SESSION_APPLICATION_VERSION 
   union 
   {
		   ULONG	composite;			//320 // Big-Endian ULONG for storing the composite value in the database
		   struct
		   {
					USHORT	major;		//320 // Version Major Part - Big-Endian Unsigned Short
					UCHAR	minor;		//322 // Version Minor Part - Unsigned Char
					CHAR	edition;	//323 // Version Edition Part - Signed Char - Use negatives for Beta editions.
		   }	partitioned;
   }	   AppVersion;					//320 // Backed-Up Application / OS Version
   UCHAR   reserved0[8];				//324 // Reserved field
   USHORT  tapeNumber;                  //332 // tape sequence number
   ULONG   startTime;                   //334 // session start date time
   UCHAR   reserved1;                   //338 // was/is sessionNum_1_to_255 in 
											  // netware for 3.0x compatibility
   UCHAR   pointShoot;                  //339 // 1 = Point & Shoot			
   UCHAR   wsType;                      //340 // WorkStation type was/is iswsbackup
											  // in netware
   CHAR    wsAddress[64];               //341 // WorkStation address 
   UCHAR   compressionMethod;           //405 // see BCM_xxx,
   USHORT  BackupDate;					//406 // Larry solomon OS2
   USHORT  BackupTime;					//408 // Larry solomon OS2
   UCHAR   IndexFile[9];				//410 // Larry solomon OS2
   UCHAR   lastSession;					//419
   USHORT  parTotal;					//420 // James - Parallel
   USHORT  parSkip;						//422 // James - Parallel
   USHORT  extendedSessionHeader;		//424 // 05/23 was/is isProtocol in NT-AGENT
											  // if set following file header has 
											  // important info
   CHAR    encryptKey[_MAX_ENCRYPTION_KEY];	//426 // WCH new in arcserve-nt for agents
   USHORT  platform;					//450
   UCHAR   locale;						//452
   UCHAR   encryptionType;              //453 // Encryption type, BET_ASCSATG or BET_ETPKI
   UCHAR	sqlTapeSignature;			//454	//The OLD tape will have this as 0x00. The BAB release in 2002 will have this as 0x01
   UCHAR	nVdiStripes;				//455	//max val possible is 64.
												//In the current BAB release in 2002 this will be 01.
   UCHAR	vdiBlockSizeIn512;			//456	//vdiBlockSize * 512 is the actual val which is >=512 && <= 64*1024
   UCHAR	session_GUID[16];			//457	//unique id
   USHORT	usAppType;				    //473   // application type, intial use by Wansync
   UCHAR	version;					//475	// structure version, netware use it
   ULONG	sessionNumber;				//476	// netware uses
   ULONG	sessionFlags2;				//480	// session flag extension
   ULONG	encryptscheme;				//484	// encryption scheme for oriole
   UCHAR	padding[24];				//488
} CTF_SESSION_HEADER, *PCTF_SESSION_HEADER;//512

typedef struct  _CATALOG_TAPE_INFO 
{
	CHAR    szTapeName[_MAX_TAPE_NAME];	//0  // name of the tape
	ULONG   ulTapeNumber;				//24 // tape sequence number
	ULONG   ulRandomID;					//28 // tape random id
	ULONG   ulSessionNumber;			//32 // current session number
} CATALOG_TAPE_INFO, *PCATALOG_TAPE_INFO;//36

typedef struct  _CATALOG_TAPE_INFO_EX 
{
	CHAR    szTapeName[_MAX_TAPE_NAME];	//0  // name of the tape
	ULONG   ulTapeNumber;				//24 // tape sequence number
	ULONG   ulRandomID;					//28 // tape random id
	ULONG   ulSessionNumber;			//32 // current session number
	CHAR    szSerialNumber[32];           //36 // tape serial number
} CATALOG_TAPE_INFO_EX, *PCATALOG_TAPE_INFO_EX;//36

typedef struct _CATALOG_FILE_INFO 
{
	UCHAR	ucSignature;			//0   // Signature 0xFF
	UCHAR	ucRecordLength;			//1   // sizeof (structure)
	UCHAR	ucNameSpace;			//2   // Dos,Unix,Mac,NT name space
	UCHAR	ucFlags;			    //3   // SFC_PARSEPATH, SFC_FULLPATH
	ULONG	ulOwnerID;				//4   // Machine owner id
	ULONG	ulFileAttribute;		//8   // File attributes
	ULONG	ulFileHisize;			//12  // Hisize of the file
	ULONG	ulFileLosize;			//16  // Losize of the file
	ULONG	ulPackedwrDateTime;		//20  // Packed (write date & time)
	ULONG	ulQFAChunkNum;			//24  // File/Dir Chunk number
	ULONG	ulQFAChunkOffset;		//28  // File/Dir Chunk Offset
	USHORT	usFileNameLength;		//32  // Length of Current filename
	USHORT	usPathLength;	        //34  // Length of the path 
	UCHAR	ucDataType;				//36  // File or Directory
	UCHAR	ucStreams;				//37  // Number of the streams
	UCHAR	ucReserved[2];			//38  // Reserved
}CATALOG_FILE_INFO, *PCATALOG_FILE_INFO;//40
*/

typedef struct dir_header {
	unsigned int flag;
	unsigned int number_of_ttl_dirs;
	unsigned int pos_volume;
	unsigned int pos_session_path;
} DIR_HEADER;

typedef struct file_header {
	unsigned int flag;
	unsigned int number_of_ttl_files;
	unsigned long sessionid;
	unsigned long jobid;
	unsigned long ownerid;
	unsigned long srchostid;
	unsigned long srcpathid;
	unsigned long starttime;
} FILE_HEADER;

// for exchange and nas
typedef struct parent_entry {
	unsigned long parentid_high;	//	(4) 
	unsigned long parentid_low;		//	(4)

	unsigned int number_of_submsgs;	//	(4)
	unsigned int submsg_loc;		//	(4) the first item of MSG_ENTRY array
} PARENT_ENTRY;

typedef struct msg_entry {
	unsigned long catptr_high;		//	(4)
	unsigned long catptr_low;		//	(4) file point of related catalog file

	unsigned int next_submsg;		//	(4) the next item of MSG_ENTRY static list
} MSG_ENTRY;

// for unix and windows file system
typedef struct dir_entry {
	unsigned long catptr_high;		//	(4) 
	unsigned long catptr_low;		//	(4) file point of related catalog file
#if 1
	unsigned long hashnum; 		 	//	(4) hashnum is id on aspathname table on ver 1, is hash num actually on ver 2.
#else
	unsigned long pathid; 		 	//	(4)
#endif

	unsigned int number_of_dirs;	//	(4)
	unsigned int number_of_files;	//	(4)
	unsigned int file_loc;			//	(4) the first item of FILE_ENTRY array
	unsigned int subdir_loc;		//	(4) the first item of DIR_ENTRY static list
	unsigned int next_subdir;		//	(4) the next item of DIR_ENTRY static list

	unsigned short filename_len;	//  (2) the highest bit is a flag, others is the length of filename(no '\0');
	unsigned short padding;			//  (2)
#if 1
	unsigned int parent_loc;		//	(4) the parent item of DIR_ENTRY static list, ver 2 only
#endif
} DIR_ENTRY;

typedef struct file_entry {
	unsigned long catptr_high;		//	(4)
	unsigned long catptr_low;		//	(4) file point of related catalog file
} FILE_ENTRY;
/*
typedef struct dir_entry {
	unsigned int number_of_dirs;
	unsigned int number_of_files;
	unsigned int first_child_dir;		// index of FILE_ENTRY
	unsigned int first_child_file;		// index of FILE_ENTRY

	unsigned int related_file_entry;	// index of FILE_ENTRY
//	unsigned int hash;					// if we decide to drop pathid, use it to identify this entry
	unsigned long pathid;				// sort array according pathid for finding performance
} DIR_ENTRY;

typedef struct file_entry {
	unsigned __int64 catptr;			// file point of related catalog file
	unsigned int next_sibling;			// index of FILE_ENTRY

	unsigned int related_dir_entry;		// -1 means just a file, no entry for dir_entry, others are an index of DIR_ENTRY
	unsigned long pathid;

	unsigned short filename_len;		// the highest bit is a flag, others is the length of filename(no '\0');
//	unsigned long fileid;
} FILE_ENTRY;
*/
#if 1
#define PATHID_INCATFILE	0x80000000
#endif
#define NAMEID_INCATFILE	0x80000000
#define NAMEID_INABDFILE	0x40000000

#define NOREC_ONCATFILE		0x8000
#define NAME_INROOTPATH		0x4000
//#define ENTRYISFILE		0x2000		// if we decide to drop pathid, use this flag identify this entry is a file, 
										// related_dir_entry will be parent node of this on dir_entry rather than -1

#define LAREGE_CATFILE		0x00000001
#if 1
#define PATHIN_CATFILE		0x00000002
#endif
#define FSFMT_CATFILE		0x00000000
#define EXCHGFMT_CATFILE	0x80000000
#define ALLFMT_CATFILE		0xFFFFFFFF

#define ASDB_CP_UNICODE		0
#define ASDB_CP_1252		-1

static WCHAR *_ASDBAnsiToUnicode(char *s, WCHAR *ws, UINT limit, unsigned int *cp_flag)
{
	//Need to implement
	BOOL g_MBCSEnabled = TRUE;
	UINT   n = 0;
	WCHAR  *p = ws;

	if (!ws)
		return ws;                          // Return NULL

	if (!s) 
	{
		*ws = 0;                            // Return nul string
		return ws;
	}  

	if (limit == 0) 
	{                     // Use length of string
		if (limit = (UINT)strlen(s))
			limit++;                          // Incrememt for nul
		else {
			*ws = 0;                          // Return nul string
			return ws;
		}
	}

	{//Alwyas convert it with 1252 codepage
		if(cp_flag)
			*cp_flag = ASDB_CP_1252;
		MultiByteToWideChar(1252, MB_PRECOMPOSED, s, -1, ws, limit);
	}

	return ws;
}

#endif
