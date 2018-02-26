/**************************************************************************
Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.

 Program Name:  Cheyenne Tape Format
      Version:  6.0  Revision A
 Version Date:  January 2, 1994
**************************************************************************/

#ifndef _CTF_H
#define _CTF_H

#include <tchar.h>
#include <Windows.h>

/**************************************************************************
                                  Definitions
**************************************************************************/

/*
 *      Tape Header Signature
 */
#define CSI_TAPE_SIG            0xcece
#define CSI_TAPE_SIG_ERASED     0xeeee

/*
 *      Tape Product Code
 */
#define TPC_ARCSERVE            0x0000
#define TPC_HSM                 0xdede

/*
 *      Tape Formats
 */
#ifndef TF_PRIME 
#define TF_PRIME                0
#define TF_ARCHIVE              1
#define TF_EXABYTE              2
#define TF_FAST_SEEK            3
#endif
/*
 *      Header Signatures
 */
#define SESS_SIG_286            0xdddddddd
#define SESS_SIG_386            0xddddd386      // session header for 386
#define SESS_SIG_PAR            0x5555aaaa      // session header for striping
#define FILE_SIG_DOS            0xbbbbbbbb
#define FILE_SIG_AFP            0xaaaaaaaa
#define FILE_SIG_OS2            0x22222222
#define FILE_SIG_UNIX           0x33333333
#define FILE_SIG_MAC            0x44444444
#define FILE_SIG_NT             0x55555555
#define FILE_SIG_NTW            0x55555557
#define FILE_SIG_PAR_SH         0x55556666      // Parallel Session Header
#define FILE_SIG_PAR_ST         0x55559999      // Parallel Session Trailer
#define FILE_SIG_WIN95          0x66666666
#define FILE_SIG_SMS            0x99999900
#define FILE_SIG_SMS_DOS        0x99999900
#define FILE_SIG_SMS_MAC        0x99999901
#define FILE_SIG_SMS_NFS        0x99999902
#define FILE_SIG_SMS_FTAM       0x99999903
#define FILE_SIG_SMS_OS2        0x99999904
#define FILE_SIG_SMS_MASK       0xffffff00
#define FILE_SIG_HMS_AFP        0xaaaa2222
#define FILE_SIG_HMS_KEY        0xaaaa3333
#define FILE_SIG_UNIVERSAL      0xabbaabba
#define FT_SIGNATURE            0xcccccccc		// file trailer signature
#define ST_SIGNATURE            0x7e7e7e7e      // "~~~~"
#define VTBL_SIGNATURE          0xeeeeeeee		
#define CONT_SIGNATURE          0xffffffff		// continuation signature
#define BLOCKTABLE_SIG_INTERLV	0xaccaacca		// Block Table SIG for Interleaving
/*
 *      Name Spaces
 */
#define NS_DOS          0               // DOS file
#define NS_MAC          1               // MAC file
#define NS_NFS          2               // NFS file
#define NS_FTAM         3               // FTAM file
#define NS_OS2          4               // OS/2 file
#define NS_NTFS         5               // NTFS file
#define NS_CHICAGO      6               // Chicago
#define NS_NT_IMAGE     7               // Window NT Image

/*
 *      WorkStation Types
 */
#define WST_NORMAL      0               // PC or File Server
#define WST_IPX         1               // IPX = Binary Address
#define WST_MAC         2               // MAC = Zone/Machine
#define WST_UNIX        3               // UNIX = Host Name
#define WST_MSNET       4               // MSNet = No Name
#define WST_TCP         5               // TCP = ?
#define WST_WINNT       6               // WinNT = Machine Name

#include "sessdef.h"

/*
 *      Backup Compression Method
 */
#define BCM_NONE        0               // File data is not compressed
#define BCM_MAX_SPEED   1               // STAC LZS221 maximum speed
#define BCM_MIN_SIZE    2               // STAC LZS221 minimum size
#define BCM_PACKED      3               // Stacker Toolkit 3.0

/*
 *      Backup Compression type
 */
#define BCT_ANY			0               // Any type of compression
#define BCT_PKW			1               // compression type is pkware
#define BCT_GNU		    2               // compression type is GNU


/*
 *      Backup Compression level
 */
#define BCL_ONE		    1               // compression level
#define BCL_TWO		    2               

/*
 *      Backup File Class
 */
#include "fileclass.h"

/*
 *      Limits
 */
#define _MAX_DIR_SPACE          0x7fffffffL
#define _MAX_PREV_TAPE          100
#define _MAX_SH_PATH            128
#define _MAX_SH_OWNER           48
#define _MAX_SH_PW              24
#define _MAX_SH_DESC            80
#define _MAX_FH_PATH            250
#define _MAX_FH_LNAME           33
#define _MAX_OS2_PATH           512
#define _MAX_NT_PATH            512
#define _MAX_NTW_PATH           (_MAX_NT_PATH * 2)
#define _MAX_WIN95_PATH         512
#define _MAX_SESSION            4000
#define _MAX_SMS_NAME           256
#define _MAX_TAPE_NAME          24
#define _MAX_TAPE_BLOCK         512
#define _MAX_TAPE_BUFFER        0x4000
#define _MAX_TRUSTEE            1024
#define _MAX_UNIX_PATH          4096		//1024
#define _MAX_USER_SPACE         1024
#define _MAX_VT_BUFFER          _MAX_TAPE_BUFFER
#define _MAX_VT_ENTRY           100
#define _MAX_VOLUME_NAME        32
#define _NO_SESSION_TRAILER     0x7e7e7e7e      // "~~~~"
#define _MAX_ENCRYPTION_KEY		24


// Define for platform



// Define for locale
#define LOCALE_SJIS		0
#define LOCALE_EUC      1
#define LOCALE_UTF8     2


// Define for encryption type
#define BET_ASCSATG		0
#define BET_ETPKI		1		// AS10 - etpki encryption

//potvi02 : copied the entry , these are needed by VMDKImage.

#define OT_DIRECTORY         6          // Directory
#define OT_FILE              7          // File

/**************************************************************************
                                     Types
**************************************************************************/

#pragma pack(1)

typedef struct  _CTF_TAPE_HEADER 
{
  ULONG   ulRAIDSignature;  //(4) Cheyenne Software signature
  UCHAR   ucRAIDTapes;      //(5) Number of tapes
  UCHAR   ucRAIDTapeNo;     //(6) Tape No in tape set, starts with 0
  UCHAR   ucRAIDStripeSize; //(7) Tape Strip Size in 16K bytes
  UCHAR   ucRAIDLevel;      //(8) Stripping method, RAID 5, etc...
  UCHAR   ucRAIDLinkPosition;  //(9) for now 0, Link data is immediately after data
  UCHAR   ucRAIDTagSize;    //(10) Portion of Strip reserved for housekeeping in 512 bytes, should be 0 for now
  UCHAR   reserved0[9];
  UCHAR   sequence;                      // 1 = First
  USHORT  productCode;                   // see TPC_xxx
  UCHAR   reserved1[4];
  USHORT  formatCode;                    // see TF_xxx
  USHORT  signature;                     // 0xCECE  ==> Cheyenne Tape
  USHORT  randomID;                      // Random ID (multi-cartridge set)
  USHORT  sessions;                      // # of sessions (Exabyte)
  UCHAR   full;                          // 1 = Full (Exabyte)
  UCHAR   reserved2[11];
  CHAR    tapeName[_MAX_TAPE_NAME];
  USHORT  previousSessions[_MAX_PREV_TAPE];
  UCHAR   reserved3[95];                // SCSI Drive Info
  ULONG   created;                       // time created
  UCHAR   reserved4[2];
  ULONG   expirationDate;
  USHORT  BackupDate;             // ARCsolo for OS/2
  USHORT  BackupTime;             // ARCsolo for OS/2
  ULONG   OrigVol;                // ARCsolo for OS/2
  UCHAR   densityCode;
  CHAR    reserved5[4];           // 0523
  CHAR    SerialNumber[32];
  UCHAR   PoolName[16];    
  USHORT  platform;
  UCHAR   locale;
  UCHAR   ucPadding;
  UCHAR   reserved6[72];
} CTF_TAPE_HEADER, *PCTF_TAPE_HEADER;



typedef struct _CTF_LAST_DR_SESSION_INFO {
      //
      // signature to be defined. 
      //
      ULONG    ulExtendedTapeHeaderSignature;                        //4
      
      //
      // 0==> tapename, random id, sequence and dr session number 
      //      fields are invalid.
      // 1==> tapename, random id, sequence and dr session number 
      //      fields are valid.
      //                                                             //8
      ULONG    ulDRSessionInfoValid;            
      
      //
      // Tapename, sequence number and random id of tape having last 
      // DR session.  
      //                                           
      CHAR     cTapeName[24];                                        //32
      USHORT   usSequence;                                           //34
      USHORT   usRandomID;                                           //36
      
      //
      // Session number of the last DR Session on above tape. 
      //
      USHORT   usDRSessionNumber;                                   //38
      
      //
      // Future.
      //
      UCHAR    ucReserved[474];                                     //512         
      
}CTF_LAST_DR_SESSION_INFO, *PCTF_LAST_DR_SESSION_INFO;

#ifdef SAVE_APP_VERSION
typedef union _CTF_SESSION_APPLICATION_VERSION
{
	ULONG       composite;         // Big-Endian ULONG for storing the composite value in the database
	struct
	{
		USHORT  major;             // Version Major Part - Big-Endian Unsigned Short
		UCHAR   minor;             // Version Minor Part - Unsigned Char
		CHAR    edition;           // Version Edition Part - Signed Char - Use negatives for Beta editions.
	} partitioned;
} CTF_SESSION_APPLICATION_VERSION; // Backed-Up Application / OS Version
#endif //SAVE_APP_VERSION

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
#ifdef NEW_ENC
   UCHAR   size1;          // 294 // size of size of Encrypted encryption key (by password-based key) 
   UCHAR   size2;          // 295 // size of Encrypted encryption key (by BAB internal key) 
   UCHAR   data1[12];     // 296 // second part of Encrypted encryption key (by password-based key) 
   UCHAR   data2[12];      // 308 // second part of Encrypted encryption key (by BAB internal key)
#ifdef SAVE_APP_VERSION

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
#else //SAVE_APP_VERSION
   UCHAR   reserved0[12];				//320 // Reserved field
#endif //SAVE_APP_VERSION
#else //NEW_ENC
#ifdef SAVE_APP_VERSION
   UCHAR   reserved0_NEW_ENC[26];		//294 // Placeholder for space occupied by fields for NEW_ENC
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
#else //SAVE_APP_VERSION
   UCHAR   reserved0[38];				//294 // Reserved field
#endif //SAVE_APP_VERSION
#endif //NEW_ENC
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
#ifdef BEB_SQL_BA_RE_NO_VDI_INFO_ON_TAPE_BUG
   UCHAR	sqlTapeSignature;			//454	//The OLD tape will have this as 0x00. The BAB release in 2002 will have this as 0x01
   UCHAR	nVdiStripes;				//455	//max val possible is 64.
												//In the current BAB release in 2002 this will be 01.
   UCHAR	vdiBlockSizeIn512;			//456	//vdiBlockSize * 512 is the actual val which is >=512 && <= 64*1024
   UCHAR	session_GUID[16];			//457	//unique id
#ifndef FIPS_NONDATA_ENC
   UCHAR	padding[39];				//473	
#else
#ifdef WANSYNC_SUPPORT
   USHORT	usAppType;				    //473   // application type, intial use by Wansync
#else
   UCHAR	reserved2[2];				//473	// for alignment purpose
#endif
   UCHAR	version;					//475	// structure version, netware use it
   ULONG	sessionNumber;				//476	// netware uses
   ULONG	sessionFlags2;				//480	// session flag extension
   ULONG	encryptscheme;				//484	// encryption scheme for oriole
   UCHAR	padding[24];				//488
#endif
#else //BEB_SQL_BA_RE_NO_VDI_INFO_ON_TAPE_BUG
   UCHAR   padding[58];	                //454
#endif //BEB_SQL_BA_RE_NO_VDI_INFO_ON_TAPE_BUG
} CTF_SESSION_HEADER, *PCTF_SESSION_HEADER;//512

#define SFO_SPARSE_FIXED  0x01          // for CTF_FILE_HEADER::cOption
#define SFO_DATASTM_SPARSE_ATTRIBUTE 0x02 // for CTF_FILE_HEADER::cOption, indicate if the datastream is sparse
typedef struct  _CTF_FILE_HEADER 
{
  ULONG  signature;						//0   // File header signature
  CHAR   relativePathName[_MAX_FH_PATH];//4   // relative to rootDirectory
  CHAR   longName[_MAX_FH_LNAME];       //254 // AFP's Long Name
  UCHAR  level;                         //287 // directory level
  ULONG  time;                          //288 // file or dir date time
  ULONG  fSize;                         //292 // file or dir size
  ULONG  rSize;                         //296 // resource fork size
  ULONG  attributes;                    //300 // file or dir attributes
  ULONG  ownerID;                       //304 // file ownerID on rootDir server
  USHORT mask;                          //308 // inherited mask (386)
  UCHAR  fileClass;                     //310 // See BFC_
  ULONG  trusteeLength;                 //311 // Length of trustee portion of data
  ULONG  dirSpace;                      //315 // Directory space restriction
  USHORT lastAccessDate;				//319
  ULONG  creationDateTime;				//321	 
  ULONG  sSize;                         //325 // ManishS?
  ULONG  aSize;                         //329 // ManishS?
  USHORT parSession;                    //333 // Parallel session #
  USHORT stripesTotal;					//335 // added by Yoon, total stripe number for a file   
  //UCHAR  reserved1[10];				//337 // Yoon reduced reserved to 10 from 12 
  UCHAR  cOption;                       //337
  UCHAR  reserved1[9];					//338 // Yoon reduced reserved to 10 from 12 
} CTF_FILE_HEADER, *PCTF_FILE_HEADER;	//347
										

// Values for flags of session file catalog
#define SFC_PARSEPATH	0x0001		// need to parse path
#define SFC_FULLPATH 	0x0002		// full path specified
// defines the type of TSI operation to perform
#define	QFA_CHUNK		0			// look for QFA from chunk number
#define	QFA_BACK		1			// look for QFA from BackChunk number

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


typedef struct _CATALOG_MESSAGE_INFO 
{
	UCHAR	ucSignature;			//0   // Signature 0xFF
	UCHAR	ucRecordLength;			//1   // sizeof (structure)
	UCHAR	ucNameSpace;			//2   // Dos,Unix,Mac,NT name space
	UCHAR	ucFlags;			    //3   // SFC_PARSEPATH, SFC_FULLPATH
	ULONG	ulQFAChunkNum;			//4   // Chunk number
	ULONG	ulQFAChunkOffset;		//8   // Chunk Offset
	ULONG	ulObjType;				//12
	ULONG	ulObjDate;				//16
	ULONG	ulObjFlags;				//20
	ULONG	ullObjSize;				//24
	ULONG	ulhObjSize;				//28
	ULONG	ullObjSelfId;			//32
	ULONG	ulhObjSelfId;			//36
	ULONG	ullObjParentId;			//40
	ULONG	ulhObjParentId;			//44
	ULONG	ullObjBody;				//48 // Can be used for child id.
	ULONG	ulhObjBody;				//52 // Can be used for child id.
	ULONG	ulObjAux;				//56
	USHORT	usObjNameSize;			//60 // Size of Name buffer which follows this structure.
	USHORT	usObjInfoSize;			//62 // Size of Info buffer which follows the Name buffer.
	ULONG	ulObjDataSize;			//64 // Size of Data buffer which follows the Info buffer.
}CATALOG_MESSAGE_INFO, *PCATALOG_MESSAGE_INFO;//68


// catalog file tape info structure
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


typedef struct  _CTF_SESSION_TRAILER
{
	ULONG  	signature;						//0   // 0x7e7e7e7e = "~~~~"
	CHAR   	relativePathName[_MAX_FH_PATH]; //4   // 0x7e7e7e7e = "~~~~"
	CHAR   	longName[_MAX_FH_LNAME];   		//254 // 0x7e7e7e7e = "~~~~"
	UCHAR  	level;							//287 // 0x7e7e7e7e = "~~~~"
	ULONG  	time;							//288 // 0x7e7e7e7e = "~~~~"
	ULONG  	fSize;							//292 // file or dir size
	ULONG	ulCatalogChunkNum;				//296 // NEW
	ULONG	ulCatalogChunkOffset;			//300 // NEW
	ULONG	ulBackQFAChunkNum;				//304 // NEW
	ULONG	ulChunkSize;					//308 // For BEB UNIX to merge BEB NT tape
	ULONG   ulSessionNumber;				//312 // current session number
	UCHAR	ucReserved[31];					//314 // 0x7e7e7e7e = "~~~~"
} CTF_SESSION_TRAILER, *PCTF_SESSION_TRAILER;// 347

// Maps over CTF_FILE_HEADER.lastAccessDate
typedef struct _CTF_MAC_FOLDER_INFO {
  USHORT ownerID;
  USHORT groupID;
  USHORT accessID;
  UCHAR  shareAttributes;
} CTF_MAC_FOLDER_INFO, *PCTF_MAC_FOLDER_INFO;

// Maps over CTF_FILE_HEADER.relativePathName[1]
typedef struct _CTF_UNIX_FILE_INFO {
  ULONG  deviceNo;
  ULONG  nodeNo;
  ULONG  mode;
  ULONG  linkNo;
  ULONG  userID;
  ULONG  groupID;
  ULONG  version;
  ULONG  accessTime;
  ULONG  modifyTime;
} CTF_UNIX_FILE_INFO, *PCTF_UNIX_FILE_INFO;

typedef struct  _CTF_FILE_TRAILER {
  ULONG signature;						//0   // 
  CHAR  relativePathName[246];			//4   // relative to rootDirectory
  ULONG fileCRC;						//250 // file CRC value 	
  UCHAR status;							//254 // file ownerID on rootDir server
  UCHAR reserved[257];					//255
} CTF_FILE_TRAILER, *PCTF_FILE_TRAILER;	//512 

typedef struct  _CTF_DIR_TRUSTEE {
  ULONG  trusteeID;
  USHORT rightsMask;
} CTF_DIR_TRUSTEE, *PCTF_DIR_TRUSTEE;

typedef struct  _CTF_USER_SPACE {
  ULONG userID;
  ULONG blocksAvailable;
} CTF_USER_SPACE, *PCTF_USER_SPACE;

typedef struct  _CTF_FILE_MAP {
  ULONG offset;
  ULONG size;
} CTF_FILE_MAP, *PCTF_FILE_MAP;

typedef struct  _CTF_VT_ENTRY {
  CHAR   rootDirectory[_MAX_SH_PATH];   // Root directory for session
  USHORT tapeNumber;                    // tape sequence number
  ULONG  startTime;                     // session start date time
  UCHAR  sessionNumber;                 // session number of this session
  ULONG  blockNumber;                   // block number of start of this session
  CHAR   ownerName[16];                 // Owner Name
  ULONG  sessionLength;                 // session length in bytes
} CTF_VT_ENTRY, *PCTF_VT_ENTRY;

typedef struct  _CTF_VOLUME_TABLE {
	ULONG         signature;		
	USHORT        numEntries;     // Number of entries in Volume Table
	CTF_VT_ENTRY  entries[_MAX_VT_ENTRY];
} CTF_VOLUME_TABLE, *PCTF_VOLUME_TABLE;
#define	SZ_CTF_VOLUME_TABLE		sizeof(CTF_VOLUME_TABLE)

//
//	Old definition 
//
typedef struct  _CTF_CONT_BLOCK {
	ULONG signature;				// 0xffffffff 
	ULONG blockNumber;				// Block Number of last valid VT on tape
} CTF_CONT_BLOCK, *PCTF_CONT_BLOCK;
#define	SZ_CTF_CONT_BLOCK		sizeof(CTF_CONT_BLOCK)

//
//	Same as continuation block, enhancement , valid from version 6.0 sp3
//
typedef struct _CTF_CONT_INFO
{
	ULONG signature;			//0  //continuation block signature 0xFFFFFFFF 
	ULONG blockNumber;			//4  //Absolute Block Number of the last valid VT address on tape	
	ULONG LastSessionNumber;	//8  //Session number of the last session on the tape
	ULONG SecondSignature;  	//12 //Get a blank tape and format it as the 
								     //Next sequence when time and the highest   
								     //sequence tape is full.
	ULONG ulCatalogChunkNum;	//16 //Cluster number 
	ULONG ulCatalogChunkOffset;	//20 //Offset
	ULONG ulBackQFAChunkNum;	//24 //Postive value of catalog file chunk num
} CTF_CONT_INFO, *PCTF_CONT_INFO;//28
#define	SZ_CTF_CONT_INFO		sizeof(CTF_CONT_INFO)


typedef struct _CTF_AFP_INFO {
  DWORD entryID;
  DWORD parentID;
  WORD  attributes;
  DWORD dataForkLength;
  DWORD resourceForkLength;
  WORD  numOffspring;
  WORD  creationDate;
  WORD  accessDate;
  WORD  modifyDate;
  WORD  modifyTime;
  WORD  backupDate;
  WORD  backupTime;
  BYTE  finderInfo[32];
  char  longName[32];
  DWORD ownerID;
  char  shortName[12];
  WORD  accessPrivileges;
} CTF_AFP_INFO, *PCTF_AFP_INFO;
#define	SZ_CTF_AFP_INFO		sizeof(CTF_AFP_INFO)

typedef struct  _CTF_OS2_INFO {
  WORD  fdateCreation;
  WORD  ftimeCreation;
  WORD  fdateLastAccess;
  WORD  ftimeLastAccess;
  WORD  fdateLastWrite;
  WORD  ftimeLastWrite;
  DWORD cbFile;
  DWORD cbFileAlloc;
  WORD  attrFile;
  BYTE  cchName;
  CHAR  achName[256];
} CTF_OS2_INFO, *PCTF_OS2_INFO;
#define	SZ_CTF_OS2_INFO	sizeof(CTF_OS2_INFO)

typedef struct  _CTF_WIN32_INFO {
  DWORD dwFileAttributes;
  DWORD dwCreationTimeLow;
  DWORD dwCreationTimeHigh;
  DWORD dwLastAccessTimeLow;
  DWORD dwLastAccessTimeHigh;
  DWORD dwLastWriteTimeLow;
  DWORD dwLastWriteTimeHigh;
  DWORD dwFileSizeHigh;
  DWORD dwFileSizeLow;
  DWORD dwReserved0;
  DWORD dwReserved1;
  CHAR  cFileName[260];
  CHAR  cAlternateName[14];
} CTF_WIN32_INFO, *PCTF_WIN32_INFO;
#define	SZ_CTF_WIN32_INFO	sizeof(CTF_WIN32_INFO)

typedef struct  _CTF_WIN32_INFOW {
  DWORD dwFileAttributes;
  DWORD dwCreationTimeLow;
  DWORD dwCreationTimeHigh;
  DWORD dwLastAccessTimeLow;
  DWORD dwLastAccessTimeHigh;
  DWORD dwLastWriteTimeLow;
  DWORD dwLastWriteTimeHigh;
  DWORD dwFileSizeHigh;
  DWORD dwFileSizeLow;
  DWORD dwReserved0;
  DWORD dwReserved1;
  WCHAR cFileName[260];
  WCHAR cAlternateName[14];
} CTF_WIN32_INFOW, *PCTF_WIN32_INFOW;
#define	SZ_CTF_WIN32_INFOW	sizeof(CTF_WIN32_INFOW)

#define BACKUP_EOS      		0
#define BACKUP_ENCRYPTED_DATA   0x8000
#define BACKUP_AD_DATA			0x9000
#define BACKUP_AD_TOKEN         0xa000
#define BACKUP_VSS_PARTIAL_FILE	0xb000		// R12V_VM_WIN32AGENT R12.V_SQL_DIFF

typedef struct  _CTF_WIN32_STREAM_ID {
  DWORD dwStreamId;
  DWORD dwStreamAttributes;
  DWORD dwStreamSizeLow;
  DWORD dwStreamSizeHigh;
  DWORD dwStreamNameSize;
} CTF_WIN32_STREAM_ID, *PCTF_WIN32_STREAM_ID;
#define	SZ_CTF_WIN32_STREAM_ID	sizeof(CTF_WIN32_STREAM_ID)


// Defined to store the partition information
typedef struct  _CTF_DISK_FREE_SPACE {
   UCHAR   ucDriveSign[4];			// "DRV"
   DWORD   dwSectorsPerCluster;			
   DWORD   dwBytesPerSector;				
   DWORD   dwNumberOfFreeClusters;		
   DWORD   dwTotalNumberOfClusters; 		
   UCHAR   ucVolumeName[_MAX_VOLUME_NAME]; //32
   UCHAR   ucFileSystem;
   UCHAR   ucReserved1[3];				
   DWORD   dwDriveSignature;				// Disk Signature for MSCS
   UCHAR   ucReserved2[4];				
} CTF_DISK_FREE_SPACE, *PCTF_DISK_FREE_SPACE;
#define FS_UNKNOWN	0
#define FS_FAT		1
#define FS_NTFS		2
#define	SZ_CTF_DISK_FREE_SPACE	sizeof(CTF_DISK_FREE_SPACE)

/**************************************************************************
                             Universal File Format
**************************************************************************/

#include "univfh.h"

#pragma pack()

#endif

