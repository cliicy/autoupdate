#pragma once

#include <winioctl.h>

#ifdef __cplusplus
namespace BLICommand
{
#endif

   enum BLICMD
   {
      CMD_UNKNOWN = 0,
      CMD_INITBACKUP,
      CMD_CREATEDISK,
      CMD_CLOSEDISK,
      CMD_WRITEDISK,
      CMD_CREATEBLOCK,
      CMD_WRITEBLOCK,
      CMD_EOS,
	  CMD_SETBACKUPSIZE,
      CMD_DUMMYDATA,
   };

   enum BLIBLOCKTYPE
   {
		BLI_BLOCK_UNKNOWN = 0,
		BLI_BLOCK_SESSION_HEADER,
		BLI_BLOCK_SUBSESSION_METADATA,
		BLI_BLOCK_VHD_HEADER,
		BLI_BLOCK_VHD,
		BLI_BLOCK_VHD_TRAILER,
		BLI_BLOCK_SESSION_TRAILER,
		BLI_BLOCK_SUBSESSION_METADATA_TEMP,
		BLI_BLOCK_VOLUME_INFO,
   };

#pragma pack(push, BLIDEF)
#pragma pack(1)

   typedef struct _BLIInitBackup 
   {
      WCHAR szMachineName[128];	// 128 should be big enough
   }BLIINITBACKUP, *PBLIINITBACKUP;

   typedef struct _BLICreateBlock
   {
      ULONG ulBlockIndex;		// the block index
      ULONG ulBlockType;
      union
      {
         WCHAR   szDescription[128]; //for BLI_BLOCKTYPE_METADATA
         ULONG   usDiskID; //for BLI_BLOCKTYPE_VHD
      };
   }BLICREATEBLOCK, *PBLICREATEBLOCK;

   typedef struct _BLIWriteBlock
   {
      ULONG ulBlockIndex;
      ULONG ulSeekMethod;
      LARGE_INTEGER liDistanceToMove;
      ULONG ulWriteDataSize;
   }BLIWRITEBLOCK, *PBLIWRITEBLOCK;


   typedef struct _BLICreateDisk 
   {
      ULONG usDiskID;		// the disk id
      struct  
      {
         ULONG usDiskID;	// the parent session disk id
         ULONG ulSessionNum;
      }parent;
      DISK_GEOMETRY stGeometry;
      LARGE_INTEGER liDiskSize;
      DWORD dwCompressionLevel;
      BOOL  bIsManagedByRPS;
    //  PVOID pDiskExtents;   // Actually it's a pointer of PST_DISK_EXTENTS type defined in D2DbaseDef.h
	  PVOID pDiskExtentsInterface; // Actually it's a pointer of PST_DISK_EXTENTS type defined in D2DbaseDef.h

	  //replace old vhd by MPII, by danri02, 2013-05
	  DWORD	dwDiskFormat;
	  //replace old vhd by MPII, by danri02, 2013-05, end
   }BLICREATEDISK, *PBLICREATEDISK;

   typedef struct _BLIWriteDisk 
   {
      ULONG ulDiskID;
      ULONG ulSeekMethod;
      LARGE_INTEGER liDistanceToMove;
      ULONG ulWriteDataSize;
   }BLIWRITEDISK, *PBLIWRITEDISK;

   typedef struct _BLICloseDisk 
   {
      ULONG ulDiskID;
   }BLICLOSEDISK, *PBLICLOSEDISK;
   typedef struct _BLISetBackupSize
   {
	   ULONG ulDiskID;
	   LARGE_INTEGER liSize; 
   }BLISETBACKUPSIZE, *PBLISETBACKUPSIZE;

   typedef struct _BLIDummyData
   {
      ULONG ulDataSize;
   }BLIDUMMYDATA, *PBLIDUMMYDATA;

   typedef union _BLI_Header 
   {
      struct  
      {
         ULONG ulCmd;
         ULONG ulFlags;  // for future use
         union {
            BLIINITBACKUP	Init;
            BLICREATEBLOCK 	CreateBlock;
            BLICREATEDISK	CreateDisk;
            BLIWRITEBLOCK	WriteBlock;
            BLIWRITEDISK	WriteDisk;
            BLICLOSEDISK	CloseDisk;
			BLISETBACKUPSIZE SetBackupSize;
            BLIDUMMYDATA   DummyData;
         }cmd_data;
      };

      unsigned char buffer[512];
   }BLIHEADER, *PBLIHEADER;

#define BLIHEADERSIZE	512

#pragma pack(pop, BLIDEF)

#ifdef __cplusplus
}
#endif