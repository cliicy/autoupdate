#pragma once
#include <BaseTsd.h>
#include <rpc.h>
#include <winioctl.h>

#define VHD_BLOCK_SIZE      (2*1024*1024)
#define VHD_BLOCK_SHIFT     21
#define VHD_SECTOR_SIZE     512
#define VHD_SECTOR_SHIFT    9
#define CA_BLK_UNUSED       0xFFFFFFFF
#define CA_VHD_COOKIE       "conectix"
#define CA_VHD_FF_VERSION   0x00010000
#define CA_VHD_RESERVED     0x00000002
#define CA_VHD_CreateVer    0x00010000

#define CA_VHD_HR_VER       0x00010000

class CPlatformCode
{
public:
    static const __int32 None = 0x00000000;
    static const __int32 W2ru = 0x75723257; // UNICODE relative   *(__int32*)"W2ru" //little-endian
    static const __int32 W2ku = 0x756B3257; // UNICODE absolute   *(__int32*)"W2ku"
    static const __int32 Wi2r = 0x72326957; // ANSI relative      *(__int32*)"Wi2r"
    static const __int32 Wi2k = 0x6B326957; // ANSI absolute      *(__int32*)"Wi2k"
};

#pragma pack(push, CVHDImageDef)
#pragma  pack(1)
typedef union _VHDFooter
{
	struct  
	{
		char   Cookie[8];
		UINT32 Features;
		UINT32 FileFormatVersion;
		UINT64 DataOffset;
		UINT32 TimeStamp;
		char   CreatorApplication[4];
		UINT32 CreatorVersion;
		char   CreatorHostOS[4];
		UINT64 OriginalSize;
		UINT64 CurrentSize;
		union
		{
			struct  
			{
				UINT8   SectorsPerTrack_Cylinder;
				UINT8   Heads;
				UINT16 Cylinder;
			};
			UINT32 Value;
		}DiskGeometry;
		UINT32 DiskType;
		UINT32 Checksum;
		GUID   UniqueId;
		UINT8  SavedState;
	};
	unsigned char   argBuffer[512];
}VHDFOOTER, *PVHDFOOTER;

typedef struct _VHDParentLocator
{
   UINT32  PlatformCode;
   UINT32  PlatformDataSpace;
   UINT32  PlatformDataLength;
   UINT32  Reserved;
   UINT64  PlatformDataOffset;
}VHDPARENTLOCATOR, *PVHDPARENTLOCATOR;

typedef union _VHDDYNHeader
{
   struct  
   {
      char Cookie[8];
      UINT64 DataOffset;
      UINT64  TableOffset;
      UINT32  HeaderVersion;
      UINT32  MaxTableEntries;
      UINT32  BlockSize;
      UINT32  Checksum;
      GUID ParentUniqueID;
      UINT32  ParentTimeStamp;
      UINT32  Reserved;
      wchar_t   ParentUnicodeName[256];
      VHDPARENTLOCATOR ParentLocators[8];
   };

   unsigned char argBuffer[1024];
}VHDDYNHEADER, *PVHDDYNHEADER;

#pragma  pack(pop, CVHDImageDef)

//VHD-Writer macro
typedef struct _Hole
{
   LONGLONG llStartOffsetInSector;
   LONGLONG llSizeInSector;
   ULONG ulChecksum;
}HOLE, *PHOLE;

#define VHD_RET_SUCCESSFULL 0
#define VHD_RET_INVALID_PARA 1
#define VHD_RET_FAILED 2
#define VHD_RET_NULL_BLOCK 3
#define VHD_RET_BLOCK_NOT_ON_CURDISK 4

/////////////////////////////////////////////////////////////////////////////////////////////
///                             CA VHD Format                                             ///
/////////////////////////////////////////////////////////////////////////////////////////////
#define VDISK_MAGIC					0x4b534944562e4143
#define VHD_VERSION					2
#define VHD_SOFTWARE_VERSION        16

// In D2D R17, VDISK still has same Magic and uses format code to differentiate among formats.
// Doing so is because the DiskVersion checking does not work correctly in D2D R16 and Prior version.
#define VHD_VERSION_2				VHD_VERSION             // first release, GM=2, beta = 0;
#define VHD_VERSION_3				3                       // Add support for slice and dedupe format
#define VHD_SOFTWARE_VERSION_16     VHD_SOFTWARE_VERSION    // Used in first release
#define VHD_SOFTWARE_VERSION_17     17
#define VDISK_INVALID_CODE          0xFFFFFFFFFFFFFFFF		// Invalid mode code
#define VDISK_MANAGED_BY_D2D        0x4432445F544D474D		// Managed by old D2D logic         "MGMT_D2D"
#define VDISK_MANAGED_BY_RPS        0x5350525F544D474D		// Managed by RPS data store logic  "MGMT_RPS"
#define VDISK_FORMAT_IN_D2D         0x204432445F574152		// Stored in raw D2D format         "RAW_D2D "
#define VDISK_FORMAT_IN_SLICE       0x2020204543494C53		// Stored in data slice format      "SLICE   "
#define VDISK_FORMAT_IN_DEDUPE      0x2020455055444544		// Stored in dedupe format          "DEDUPE  "

#pragma pack(push, __DISK_ENC_INFO, 1)
typedef struct _DISK_ENC_INFO
{
	unsigned __int16 EncLibType;
	unsigned __int8  AlgoType;
	unsigned __int8  VerifyInfoValid;
	unsigned __int8  reserved;
	union
	{
		struct  
		{
			unsigned __int8 CheckOutData[252];
			unsigned __int32 CheckSum;
		};
		unsigned __int8  ValidationData[256]; //to validate the password
	};
}DISK_ENC_INFO, *PDISK_ENC_INFO;
#pragma pack(pop, __DISK_ENC_INFO)

#pragma pack(push, __DISK_HEADER, 1)
typedef union _DISK_HEADER
{
   struct  
   {
      unsigned __int64 Magic;			//'C','A','.','V','D','I','S','K'
      unsigned __int32 DiskType;		//full : inc
      unsigned __int32 BlockSize;		//64KB
      unsigned __int64 DataOffset;		//sizeof(DISK_HEADER) by default
      unsigned __int64 BATOffset;		//offset in byte
      unsigned __int32 BATSize;
      unsigned __int64 DiskSize;		//size in byte
      //unsigned __int32 MaxTableEntries;
      DISK_GEOMETRY DiskGeometry;
      GUID UniqueID;

      struct  
      {
         unsigned __int32 Algo;
      }Compression;

      // struct 
      //{
      //	unsigned __int16 EncLibType;
      //	unsigned __int8  AlgoType;
      //	unsigned __int8  reserved[2];
      //	unsigned __int8  CheckoutData[256]; //the encryption data of the first 256 bytes data in disk header
      //}Encryption;
      DISK_ENC_INFO Encryption;

      struct  
      {
         wchar_t AbsoluteFilename[512];
         wchar_t RelatedFilename[512];
         UUID UniqueID;
      }Parent;

      unsigned __int32 HdrChecksum;
      // in first release
      // this value is zero in beta build
      // when GM this value is 2
      // thus we can indicate the different between beta and GM
      // and format modification should change this value as well
      unsigned __int32 DiskVersion; 
      unsigned __int64 llBATBucketSize;
      unsigned __int64 llOriginalBATSize;
      unsigned __int32 ReleaseVersion;

      // Enabled when DiskVersion >= VHD_VERSION_3 and ReleaseVersion >= VHD_SOFTWARE_VERSION_17 --- Begin
      unsigned __int64 ManagementCode;
      unsigned __int64 FormatCode;
      union{
         struct
         {
            unsigned __int64 SliceSize;      
         }SliceParam;

         struct
         {
            DWORD				nFileNo;
			unsigned __int64	BlockNumInDisk; //the block num that is written into this disk. for incr backup job,it is changed block.
			unsigned __int64	SliceSize;
			unsigned __int64	DedupBATSize;
			unsigned __int64	processphase;
			DWORD				nFileNoBeforeMerge; // the is only used by merge job. This field is used to keep the fileno of disk of lastest session.

         }DedupeParam;

         struct
         {
            unsigned __int8  ParamPlaceHolder[128];
         }UnknownParam;
      }FormatParam;

	  //For data statistic
      unsigned __int64 ullLogicDataSize;

      // Enabled when DiskVersion >= VHD_VERSION_3 and ReleaseVersion >= VHD_SOFTWARE_VERSION_17 --- End
   };

	unsigned __int8 HdrData[ 16 * 1024 ];
}DISK_HDR, *PDISK_HDR;

#define BITMAP_FILE_MAGIC 0x50414d5449424143

#define BITMAP_VERSION 6

typedef union _BitmapHeader
{
	struct 
	{
		ULONGLONG	ullMagic;		//'C','A','B','I','T','M','A','P'   0x50414d5449424143
		ULONGLONG	ullDiskSize;
		DWORD		dwBlockSize;
		DWORD		dwCheckSum;		//Valiate data
		ULONGLONG	ullUsedBlockCount;
		DWORD		dwVersion;
	};

	BYTE Data[512];
}BitmapHeader, *PBitmapHeader;

#pragma pack(pop, __DISK_HEADER)