#ifndef ASDBBASE_H
#define ASDBBASE_H

#ifdef __cplusplus            
  extern "C" {                     /* avoid name-mangling if used from C++ */
#endif /* __cplusplus */

#pragma pack(1)

// Do not change or rearrange the order of fields.
typedef struct TapeData {
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;    //for unix, this field is session handle
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFANumOffset;
   ASDB_ADDR recordPos;
} TAPEDATA, *PTAPEDATA ; 

typedef struct TapeDataEx {
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short SesStatus;
   unsigned long  SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;    //for unix, this field is session handle
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   ASDB_ADDR recordPos;
   unsigned long  tapedataFlag;  //Up to 620, this field is dummy.
                                 // In 6.6, this field is only for Unix platform
} TAPEDATAEX, *PTAPEDATAEX ; 

typedef struct TapeDataEx2 {	  //FALCA01  Added SerialNum
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short SesStatus;
   unsigned long  SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;    //for unix, this field is session handle
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   ASDB_ADDR recordPos;
   unsigned long  tapedataFlag;   //Up to 620, this field is dummy.
   char			  SerialNum[32];
} TAPEDATAEX2, *PTAPEDATAEX2 ; 

// Oripin: UNICODE_JIS Support :redbh03

typedef struct TapeDataEx2W {	  //FALCA01  Added SerialNum
	unsigned char  DataType;//kalsa03 reverted to uchar
	unsigned char  SesType;//kalsa03
	unsigned short SesStatus;
	unsigned long  SessionID;
	unsigned long  PathID;
	unsigned long  LongNameID;
	unsigned long  ShortNameID;    //for unix, this field is session handle
	unsigned long  FileDate;
	unsigned long  FileSizeHigh;   // High File Size
	unsigned long  FileSize;       // Low File Size
	unsigned long  QFAChunkNum;
	unsigned long  QFAChunkOffset;
	ASDB_ADDR recordPos;
	unsigned long  tapedataFlag;   //Up to 620, this field is dummy.
	wchar_t			  SerialNum[32];
} TAPEDATAEX2W, *PTAPEDATAEX2W ; 

typedef struct TapeDataEx_AdScan { //Wanhe05_DeDupe_SCAN
	ASDB_KeyTapeName tapeName;
	unsigned char  DataType;
	unsigned char  SesType;
	unsigned short SesStatus;
	unsigned long  SessionID;
	unsigned long  PathID;
	unsigned long  LongNameID;
	unsigned long  ShortNameID;    //for unix, this field is session handle
	unsigned long  FileDate;
	unsigned long  FileSizeHigh;   // High File Size
	unsigned long  FileSize;       // Low File Size
	unsigned long  QFAChunkNum;
	unsigned long  QFAChunkOffset;
	ASDB_ADDR recordPos;
	unsigned long  tapedataFlag;  //Up to 620, this field is dummy.
	// In 6.6, this field is only for Unix platform
	long        	StartTime;
} TAPEDATAEXADSCAN, *PTAPEDATAEXADSCAN; 

#ifdef SESSNUM_INCREASE
typedef struct VersionData_Old {
   // Tape Info.
   char TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
   char Location[20];
   // Session Info.
   unsigned long  StartTime;
   unsigned short SesNum;
   unsigned short SesMethod;
   unsigned long  SesFlags;
   unsigned long  SrcHostID;
   unsigned long  SrcPathID;
   unsigned long  QFABlockNum;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   // Tape data info.
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFANumOffset;
   ASDB_ADDR recordPos;
   unsigned char  StreamNum;
   unsigned char  SesStatus;
   char           Reserved2[6];
   // Job Info.
   //char OwnerName[22]
} VERSIONDATA_Old, *PVERSIONDATA_Old ; 

typedef struct VersionDataEx_Old {
   // Tape Info.
   char TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
   char Location[20];
   // Session Info.
   unsigned long  StartTime;
   unsigned short SesNum;
   unsigned short SesMethod;
   unsigned long  SesFlags;
   unsigned long  SrcHostID;
   unsigned long  SrcPathID;
   unsigned long  QFABlockNum;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   // Tape data info.   get to be the same as TAPEDATAEX
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short versiondata_pad;
   unsigned long  SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   ASDB_ADDR recordPos;
   unsigned long  versiondataFlag;  //Up to 620, this field is dummy.
                                    // In 6.6, this field is only for Unix platform
   unsigned char  StreamNum;
   unsigned char  SesStatus;
   char           Reserved2[6];
   // Job Info.
   //char OwnerName[22]
} VERSIONDATAEX_Old, *PVERSIONDATAEX_Old ; 

// Oripin: UNICODE_JIS Support :redbh03
typedef struct VersionDataExW_Old {
	// Tape Info.
	wchar_t TapeName[24];
	unsigned short RandomID;
	unsigned short SeqNum;
	wchar_t Location[20];
	// Session Info.
	unsigned long  StartTime;
	unsigned short SesNum;
	unsigned short SesMethod;
	unsigned long  SesFlags;
	unsigned long  SrcHostID;
	unsigned long  SrcPathID;
	unsigned long  QFABlockNum;
	unsigned long  TotalKBytes;
	unsigned long  TotalFiles;
	// Tape data info.   get to be the same as TAPEDATAEX
	unsigned char  DataType;
	unsigned char  SesType;
	unsigned short versiondata_pad;
	unsigned long  SessionID;
	unsigned long  PathID;
	unsigned long  LongNameID;
	unsigned long  ShortNameID;
	unsigned long  FileDate;
	unsigned long  FileSizeHigh;   // High File Size
	unsigned long  FileSize;       // Low File Size
	unsigned long  QFAChunkNum;
	unsigned long  QFAChunkOffset;
	ASDB_ADDR recordPos;
	unsigned long  versiondataFlag;  //Up to 620, this field is dummy.
	// In 6.6, this field is only for Unix platform
	unsigned char  StreamNum;
	unsigned char SesStatus;
   	char           Reserved2[6];
	// Job Info.
	//char OwnerName[22]
} VERSIONDATAEXW_Old, *PVERSIONDATAEXW_Old ; 

typedef struct VersionDataEx2_Old {
   // Tape Info.
   char TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
   char Location[20];
   // Session Info.
   unsigned long  StartTime;
   unsigned short SesNum;
   unsigned short SesMethod;
   unsigned long  SesFlags;
   unsigned long  SrcHostID;
   unsigned long  SrcPathID;
   unsigned long  QFABlockNum;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   // Tape data info.   get to be the same as TAPEDATAEX
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short versiondata_pad;
   unsigned long  SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   ASDB_ADDR recordPos;
   unsigned long  versiondataFlag;  //Up to 620, this field is dummy.
                                    // In 6.6, this field is only for Unix platform
   unsigned char  StreamNum;
   unsigned char  SesStatus;
   char           Reserved2[6];
   // Job Info.
   //char OwnerName[22]
   char			  SerialNum[32];
} VERSIONDATAEX2_Old, *PVERSIONDATAEX2_Old ; 

typedef struct VersionData {
   // Tape Info.
   char TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
   char Location[20];
   // Session Info.
   unsigned long  StartTime;
   unsigned short SesNum_Old;
   unsigned short SesMethod;
   unsigned long  SesFlags;
   unsigned long  SrcHostID;
   unsigned long  SrcPathID;
   unsigned long  QFABlockNum;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   // Tape data info.
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFANumOffset;
   ASDB_ADDR recordPos;
   unsigned char  StreamNum;
   unsigned char  SesStatus;
#ifdef R12_V_VM_DB  // R12.v
   char           Reserved2[2];
   long		  SesNum;
   long		  SubSessNum;
   char           Reserved[20];
#else
   char           Reserved2[6];
#endif
   // Job Info.
   //char OwnerName[22]
} VERSIONDATA, *PVERSIONDATA ; 

typedef struct VersionDataEx {
   // Tape Info.
   char TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
   char Location[20];
   // Session Info.
   unsigned long  StartTime;
   unsigned short SesNum_Old;
   unsigned short SesMethod;
   unsigned long  SesFlags;
   unsigned long  SrcHostID;
   unsigned long  SrcPathID;
   unsigned long  QFABlockNum;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   // Tape data info.   get to be the same as TAPEDATAEX
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short versiondata_pad;
   unsigned long  SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   ASDB_ADDR recordPos;
   unsigned long  versiondataFlag;  //Up to 620, this field is dummy.
                                    // In 6.6, this field is only for Unix platform
   unsigned char  StreamNum;
   unsigned char  SesStatus;
#ifdef R12_V_VM_DB  // R12.v
   char           Reserved2[2];
   long		  SesNum;
   long		  SubSessNum;
   char           Reserved[20];
#else
   char           Reserved2[6];
#endif   
   // Job Info.
   //char OwnerName[22]
} VERSIONDATAEX, *PVERSIONDATAEX ; 

// Oripin: UNICODE_JIS Support :redbh03
typedef struct VersionDataExW {
	// Tape Info.
	wchar_t TapeName[24];
	unsigned short RandomID;
	unsigned short SeqNum;
	wchar_t Location[20];
	// Session Info.
	unsigned long  StartTime;
	unsigned short SesNum_Old;
	unsigned short SesMethod;
	unsigned long  SesFlags;
	unsigned long  SrcHostID;
	unsigned long  SrcPathID;
	unsigned long  QFABlockNum;
	unsigned long  TotalKBytes;
	unsigned long  TotalFiles;
	// Tape data info.   get to be the same as TAPEDATAEX
	unsigned char  DataType;
	unsigned char  SesType;
	unsigned short versiondata_pad;
	unsigned long  SessionID;
	unsigned long  PathID;
	unsigned long  LongNameID;
	unsigned long  ShortNameID;
	unsigned long  FileDate;
	unsigned long  FileSizeHigh;   // High File Size
	unsigned long  FileSize;       // Low File Size
	unsigned long  QFAChunkNum;
	unsigned long  QFAChunkOffset;
	ASDB_ADDR recordPos;
	unsigned long  versiondataFlag;  //Up to 620, this field is dummy.
	// In 6.6, this field is only for Unix platform
	unsigned char  StreamNum;
	unsigned char SesStatus;
#ifdef R12_V_VM_DB  // R12.v
   	char           Reserved2[2];
	long		  SesNum;
	long		  SubSessNum;
	char           Reserved[20];
#else
   	char           Reserved2[6];
#endif   	
	// Job Info.
	//char OwnerName[22]
} VERSIONDATAEXW, *PVERSIONDATAEXW ; 

typedef struct VersionDataEx2 {
   // Tape Info.
   char TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
   char Location[20];
   // Session Info.
   unsigned long  StartTime;
   unsigned short SesNum_Old;
   unsigned short SesMethod;
   unsigned long  SesFlags;
   unsigned long  SrcHostID;
   unsigned long  SrcPathID;
   unsigned long  QFABlockNum;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   // Tape data info.   get to be the same as TAPEDATAEX
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short versiondata_pad;
   unsigned long  SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   ASDB_ADDR recordPos;
   unsigned long  versiondataFlag;  //Up to 620, this field is dummy.
                                    // In 6.6, this field is only for Unix platform
   unsigned char  StreamNum;
   unsigned char  SesStatus;
#ifdef R12_V_VM_DB  // R12.v
   	char           Reserved2[2];
	long		  SubSessNum;
#else
   char           Reserved2[6];
#endif
   // Job Info.
   //char OwnerName[22]
   char			  SerialNum[32];
#ifdef SESSNUM_INCREASE
	long		  SesNum;
	char           Reserved[20];
#endif
} VERSIONDATAEX2, *PVERSIONDATAEX2 ; 
#else
typedef struct VersionData {
   // Tape Info.
   char TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
   char Location[20];
   // Session Info.
   unsigned long  StartTime;
   unsigned short SesNum;
   unsigned short SesMethod;
   unsigned long  SesFlags;
   unsigned long  SrcHostID;
   unsigned long  SrcPathID;
   unsigned long  QFABlockNum;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   // Tape data info.
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFANumOffset;
   ASDB_ADDR recordPos;
   unsigned char  StreamNum;
   unsigned char  SesStatus;
#ifdef R12_V_VM_DB  // R12.v
   long		  SubSessNum;
   char           Reserved2[2];
#else
   char           Reserved2[6];
#endif
   // Job Info.
   //char OwnerName[22]
} VERSIONDATA, *PVERSIONDATA ; 

typedef struct VersionDataEx {
   // Tape Info.
   char TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
   char Location[20];
   // Session Info.
   unsigned long  StartTime;
   unsigned short SesNum;
   unsigned short SesMethod;
   unsigned long  SesFlags;
   unsigned long  SrcHostID;
   unsigned long  SrcPathID;
   unsigned long  QFABlockNum;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   // Tape data info.   get to be the same as TAPEDATAEX
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short versiondata_pad;
   unsigned long  SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   ASDB_ADDR recordPos;
   unsigned long  versiondataFlag;  //Up to 620, this field is dummy.
                                    // In 6.6, this field is only for Unix platform
   unsigned char  StreamNum;
   unsigned char  SesStatus;
#ifdef R12_V_VM_DB  // R12.v
   long		  SubSessNum;
   char           Reserved2[2];
#else
   char           Reserved2[6];
#endif   
   // Job Info.
   //char OwnerName[22]
} VERSIONDATAEX, *PVERSIONDATAEX ; 

// Oripin: UNICODE_JIS Support :redbh03
typedef struct VersionDataExW {
	// Tape Info.
	wchar_t TapeName[24];
	unsigned short RandomID;
	unsigned short SeqNum;
	wchar_t Location[20];
	// Session Info.
	unsigned long  StartTime;
	unsigned short SesNum;
	unsigned short SesMethod;
	unsigned long  SesFlags;
	unsigned long  SrcHostID;
	unsigned long  SrcPathID;
	unsigned long  QFABlockNum;
	unsigned long  TotalKBytes;
	unsigned long  TotalFiles;
	// Tape data info.   get to be the same as TAPEDATAEX
	unsigned char  DataType;
	unsigned char  SesType;
	unsigned short versiondata_pad;
	unsigned long  SessionID;
	unsigned long  PathID;
	unsigned long  LongNameID;
	unsigned long  ShortNameID;
	unsigned long  FileDate;
	unsigned long  FileSizeHigh;   // High File Size
	unsigned long  FileSize;       // Low File Size
	unsigned long  QFAChunkNum;
	unsigned long  QFAChunkOffset;
	ASDB_ADDR recordPos;
	unsigned long  versiondataFlag;  //Up to 620, this field is dummy.
	// In 6.6, this field is only for Unix platform
	unsigned char  StreamNum;
	unsigned char SesStatus;
#ifdef R12_V_VM_DB  // R12.v
   	long			  SubSessNum;
   	char           Reserved2[2];
#else
   	char           Reserved2[6];
#endif   	
	// Job Info.
	//char OwnerName[22]
} VERSIONDATAEXW, *PVERSIONDATAEXW ; 

typedef struct VersionDataEx2 {
   // Tape Info.
   char TapeName[24];
   unsigned short RandomID;
   unsigned short SeqNum;
   char Location[20];
   // Session Info.
   unsigned long  StartTime;
   unsigned short SesNum;
   unsigned short SesMethod;
   unsigned long  SesFlags;
   unsigned long  SrcHostID;
   unsigned long  SrcPathID;
   unsigned long  QFABlockNum;
   unsigned long  TotalKBytes;
   unsigned long  TotalFiles;
   // Tape data info.   get to be the same as TAPEDATAEX
   unsigned char  DataType;
   unsigned char  SesType;
   unsigned short versiondata_pad;
   unsigned long  SessionID;
   unsigned long  PathID;
   unsigned long  LongNameID;
   unsigned long  ShortNameID;
   unsigned long  FileDate;
   unsigned long  FileSizeHigh;   // High File Size
   unsigned long  FileSize;       // Low File Size
   unsigned long  QFAChunkNum;
   unsigned long  QFAChunkOffset;
   ASDB_ADDR recordPos;
   unsigned long  versiondataFlag;  //Up to 620, this field is dummy.
                                    // In 6.6, this field is only for Unix platform
   unsigned char  StreamNum;
   unsigned char  SesStatus;
#ifdef R12_V_VM_DB  // R12.v
   long		  SubSessNum;
   char           Reserved2[2];
#else
   char           Reserved2[6];
#endif
   // Job Info.
   //char OwnerName[22]
   char			  SerialNum[32];
} VERSIONDATAEX2, *PVERSIONDATAEX2 ; 
#endif

//Structure to store the List of ID/string pairs for RPC calls (ASNW)
typedef struct _NameList {
   unsigned long  LongNameID;
   char		*szName;
} NAMELIST, *PNAMELIST;


typedef struct _NameListW {
	unsigned long  LongNameID;
	wchar_t		*szName;
} NAMELISTW, *PNAMELISTW;
#pragma pack()



#ifdef __cplusplus
  }
#endif /* __cplusplus */

#endif    /* ASDBBASE_H */
