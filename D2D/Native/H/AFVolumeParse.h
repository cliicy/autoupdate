#ifndef _AF_VOLUME_PARSE_H_
#define _AF_VOLUME_PARSE_H_

#include <Windows.h>
#include <vector>

#if defined (__cplusplus)
extern "C"
{
#endif // __cplusplus

#if defined (AFVOLUMEPARSE_EXPORTS)
#define AFVOLUMEPARSE_EXTERN __declspec(dllexport)
#else
#define AFVOLUMEPARSE_EXTERN __declspec(dllimport)
#endif

//
// Error codes
//
#define AF_VOLUMEPARSE_ERR_MASK						(0x0A500000)

#define AF_VOLUMEPARSE_ERR_NONE						(0x00000000)
#define AF_VOLUMEPARSE_ERR_INVAIL_PARAMETER			(0x00000001|AF_VOLUMEPARSE_ERR_MASK)
#define AF_VOLUMEPARSE_ERR_OUT_OF_MEM				(0x00000002|AF_VOLUMEPARSE_ERR_MASK)
#define AF_VOLUMEPARSE_ERR_PASRE					(0x00000003|AF_VOLUMEPARSE_ERR_MASK)
#define AF_VOLUMEPARSE_ERR_FILE_NOT_FOUND			(0x00000004|AF_VOLUMEPARSE_ERR_MASK)
#define AF_VOLUMEPARSE_ERR_VOLUME_IO				(0x00000005|AF_VOLUMEPARSE_ERR_MASK)
#define AF_VOLUMEPARSE_ERR_NOT_NTFS_VOLUME			(0x00000006|AF_VOLUMEPARSE_ERR_MASK)
#define AF_VOLUMEPARSE_ERR_FILE_RECORD_MAGIC		(0x00000007|AF_VOLUMEPARSE_ERR_MASK)
#define AF_VOLUMEPARSE_ERR_FILES_PATH				(0x00000008|AF_VOLUMEPARSE_ERR_MASK)
#define AF_VOLUMEPARSE_ERR_UPATE_SEQUENCE			(0x00000009|AF_VOLUMEPARSE_ERR_MASK)
#define AF_VOLUMEPARSE_ERR_UNKNOW					(0x0000000A|AF_VOLUMEPARSE_ERR_MASK)
#define AF_VOLUMEPARSE_ERR_FILE_RECORD_NOT_IN_USE   (0x0000000B|AF_VOLUMEPARSE_ERR_MASK)

namespace af
{

#pragma pack(push, _DATARUN_LAYOUT_, 1)

	typedef struct _LayoutExtents
	{
		LONGLONG m_llVcn;
		LONGLONG m_llLcn;
		LONGLONG m_llLength;
	} LayoutExtents;

	typedef struct _LayoutData
	{
		BYTE* m_pFileData;
		ULONG m_ulSize;
	} LayoutData;

	struct AF_FILE_LAYOUT_BUFFER_T
	{
		BOOL  m_bResident;
		DWORD m_dwExtentCount;

		union
		{
			LayoutExtents Extents[1];
			LayoutData Data;
		};
	};
#pragma pack(pop, _DATARUN_LAYOUT_)

//
// Abstract class for volume IO operations
//
class IAFVolumeIO
{
public:

    //
    // Number of bytes to offset from origin.
    // If successful, the function returns a zero value.Otherwise, it returns nonzero value.
    //
    virtual DWORD Seek(ULONGLONG ullNumberOfBytes) = 0;

    //
    // Reads number of bytes to buffer .
    // If successful, the function returns a zero value.Otherwise, it returns nonzero value.
    //
    virtual DWORD Read(void* pDest, ULONGLONG ullNumberOfBytes) = 0;
};

//
// Abstract class for file layout operation
//
class IAFDataRunCallBack
{
public:

    //
    // pLayoutBuffer - File's layout.
    //
    // wcsFileName - File's full path in volume.
    //
    virtual void OperateDataRuns(const AF_FILE_LAYOUT_BUFFER_T* pLayoutBuffer, const wchar_t* wcsFileName, BOOL bCompressed, ULONGLONG ullFileSize) = 0;

	virtual void SetClusterSize(ULONG ulSize) = 0;
};

struct MountPointInfo
{
	WCHAR wszMountPath[MAX_PATH];
	WCHAR wszMountSource[MAX_PATH];
};

//
// wcsDeviceName - Physic disk volume or snapshot volume name.
//                 Samples: 
//                 "\.\D:" indicate open D volume.
//                 "\\?\GLOBALROOT\Device\HarddiskVolumeShadowCopy26" indicate open snapshot volume
// 
// wcsFilesName - The files will be parsed, support wildcard.
//                Samples:
//                "\hiberfil.sys"                   indicate single file in root folder.
//                "\System Volume Information\*"    indicate all files in folder of System Volume Information.
//                "\System Volume Information\* /s" indicate all files in folder of System Volume Information and files in its sub folders.
//
// If successful, the function returns a zero value. Otherwise, it returns nonzero value.
//
DWORD AFVOLUMEPARSE_EXTERN AFGetFileLayoutOnDevice(const wchar_t* wcsDeviceName,
                                                   const wchar_t* wcsFilesName,
                                                   IAFDataRunCallBack& dataRunsCallBack);

DWORD AFVOLUMEPARSE_EXTERN AFGetFileLayoutOnVolumeIO(IAFVolumeIO& volumeIO,
                                                   const wchar_t* wcsFilesName,
                                                   IAFDataRunCallBack& dataRunsCallBack);

DWORD AFVOLUMEPARSE_EXTERN AFGetFileLayoutOnFAT32VolumeIO(IAFVolumeIO& volumeIO, const wchar_t* wcsFilesName, IAFDataRunCallBack& dataRunsCallBack);

DWORD AFVOLUMEPARSE_EXTERN AFGetVolumeMountPoints(IAFVolumeIO& volumeIO, MountPointInfo** ppMountPoints, ULONG& ulCount);
void AFVOLUMEPARSE_EXTERN AFClearMountPointBuffer(MountPointInfo*& ppMountPoints);

} // namespace af

#if defined (__cplusplus)
}
#endif // __cplusplus

#endif // _AF_VOLUME_PARSE_H_
