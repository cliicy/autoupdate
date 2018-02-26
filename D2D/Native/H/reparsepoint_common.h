#ifndef __REPARSEPOINT_XXX_2014_COMMON_PUBLIC_H__
#define __REPARSEPOINT_XXX_2014_COMMON_PUBLIC_H__

// {371D207A-7239-4AB9-8C62-75C13C30FCDD}
static const GUID XOIV_REPARSE_GUID = { 0x371d207a, 0x7239, 0x4ab9, { 0x8c, 0x62, 0x75, 0xc1, 0x3c, 0x30, 0xfc, 0xdd } };
#define CA_XOIV_REPARSE_TAG 0x00006361L

typedef GUID XO_FILE_ID;
typedef XO_FILE_ID *PXO_FILE_ID;

typedef struct _DiskCHS
{
	ULONG cylinders;
	ULONG heads;
	ULONG sectors;
}DiskCHS, *PDiskCHS;


#pragma warning(push)
#pragma warning(disable:4200) // disable warnings for structures with zero length arrays.

typedef enum _DiskTypeEnum
{
	SIMPLE_DISK_TYPE = 0,
	SYNTHETIC_DISK_TYPE = 1
}DiskTypeEnum;

typedef enum _PartitionStyleEnum
{
	MBR_PATITION_STYLE,
	GPT_PATITION_STYLE
}PartitionStyleEnum;

typedef struct _SyntheticDiskData
{
	PartitionStyleEnum	PartitionStyle;
	ULONG				VolumeCount;
	GUID				VolumeGUID[];
}SyntheticDiskData;


typedef struct _XOIV_REPARSE_DATA
{
	GUID				DiskGUID;
	wchar_t				rootPath[512];
	ULONG				sessNumber;
	DiskCHS				chs;
	wchar_t				sessPwd[32];
	wchar_t				diskName[512];
	__int64				diskSize;
	ULONG				sectorSize;
	DiskTypeEnum		diskType;
	SyntheticDiskData	extraData;
}XOIV_REPARSE_DATA, SessionDiskContext, *PXOIV_REPARSE_DATA;

#pragma warning(pop)

#endif //__REPARSEPOINT_XXX_2014_COMMON_PUBLIC_H__
