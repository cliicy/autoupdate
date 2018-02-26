#pragma once

#include <string>
#include <vector>
#include <map>
#include "AFLic.h"
//typedef struct _ARCFLASH_LIC_INFO ARCFLASH_LIC_INFO, *PARCFLASH_LIC_INFO;
#include "D2dbaseDef.h"
#include <Ntddvol.h>
#include "IVolumeBitmap.h"
typedef std::vector<ST_DISK_EXTENT_INFO>   VDISK_EXT;
typedef std::map<DWORD, VDISK_EXT>         MDISK_EXTS;

typedef struct _DISK_BACKUP_PROPTERY_
{
	BOOL bCreated;
}DISK_BACKUP_PROPERTY, *PDISK_BACKUP_PROPERTY;

typedef std::map<DWORD, DISK_BACKUP_PROPERTY>      MDISK_PRAPTERY;

typedef BOOL(*CANJOBCANCEL)(PVOID pContext);

class CBLIDataHandler;
class CDataTransmitter
{
public:
	enum MESSAGE_TYPE {
		MESSAGE_INFOMATION,
		MESSAGE_WARNNING,
		MESSAGE_ERROR
	};

	enum BLI_PHASE {
		PHASE_TAKING_SNAPSHOT,
		PHASE_CREATING_VIRTUAL_DISKS,
		PHASE_REPLICATIING_VOLUMES,
		PHASE_DELETING_SNAPSHOT
	};

	enum BLI_STATUS {
		STATUS_UNKNOWN,
		STATUS_ACTIVE,
		STATUS_DONE,
		STATUS_INCOMPLETE,
		STATUS_FAILED,
		STATUS_CANCELLED
	};

    enum BLI_BCDWM_TYPE {
        BCDWM_BCD,    
        BCDWM_WM
    };

	typedef struct _VOLUME_PARAMETER {
		WCHAR szRootPath[_MAX_PATH];
		WCHAR szShadowCopy[_MAX_PATH]; //<sonmi01>2009-12-8 pass shadow copy to VMImage
		WCHAR szVolumeGUID[_MAX_PATH]; //<sonmi01>2010-2-3 enhance for volume filtering
		WCHAR szFilesystem[8];
		WCHAR szUUID[64];			// filled up by client agent
		LARGE_INTEGER i64Length;
		DWORD dwClusterSize;
		DWORD nVolumeType;			// VOLUME_TYPE
		DWORD nStatus;				// BLI_STATUS
	} VOLUME_PARAMETER, *PVOLUME_PARAMETER;

public:
	virtual ~CDataTransmitter(VOID) = NULL {};

	virtual BOOL CreateDisk(DWORD dwDiskNumber, PDISK_GEOMETRY pDiskGeometry, LARGE_INTEGER i64DiskSize, LARGE_INTEGER i64UsedDiskSize, const VDISK_EXT& DiskExts) = NULL;
	virtual BOOL CloseDisk(DWORD dwDiskNumber) = NULL;

	virtual BOOL WriteDisk(DWORD dwDiskNumber, LARGE_INTEGER i64Offset, DWORD dwLength, LPCVOID pData) = NULL;

	virtual BOOL WriteMessage(MESSAGE_TYPE nType, LPCWSTR lpszMessage) = NULL;

	virtual BOOL UpdateJobPhase(BLI_PHASE nPhase, DWORD dwJobNo) = NULL;
	
	virtual BOOL SessionEstimation(PLARGE_INTEGER pi64Disk, DWORD dwFiles) = NULL;

	virtual BOOL BeginVolume(PVOLUME_PARAMETER pParameter) = NULL;

	virtual BOOL WriteVolumeBitmap(
							LPCWSTR szRootPath,
							LPCWSTR szSnapshotVolumeName,
							PVOLUME_BITMAP_BUFFER pVolumeBitmap,
							DWORD dwSize) = NULL;
	virtual BOOL WriteVolumeBitmap(
							LPCWSTR szRootPath,
							LPCWSTR szSnapshotVolumeName,
							IVolumeBitmap* pVolumeBitmap ) = NULL;
 //   virtual BOOL ReadVolumeBitmap(
	//				        LPCWSTR lpVolumeGUID,
	//				        DWORD dwClusterSize,
	//				        PVOLUME_BITMAP_BUFFER pVolumeBitmap
	//				        ) = NULL;

	//virtual BOOL ReadVolumeBitmap(
	//						LPCWSTR lpVolumeGUID,
	//						DWORD dwClusterSize,
	//						IVolumeBitmap** ppVolumeBitmap
	//						) = NULL;

	virtual BOOL WriteIncrementalBitmap(
							LPCWSTR szRootPath,
							LPCWSTR szSnapshotVolumeName,
							PVOLUME_BITMAP_BUFFER pIncrementalBitmap,
							DWORD dwSize) = NULL;
	virtual BOOL WriteIncrementalBitmap(
							LPCWSTR szRootPath,
							LPCWSTR szSnapshotVolumeName,
							IVolumeBitmap* pIncrementalBitmap ) = NULL;
	virtual BOOL WriteVolumeDiskExtents(
							LPCWSTR szRootPath,
							PVOLUME_DISK_EXTENTS pDiskExtents,
							DWORD dwSize) = NULL;
	virtual BOOL WriteLogicalPhysicalOffsets(
							LPCWSTR szRootPath,
							PVOLUME_LOGICAL_OFFSET pLogicalOffset,
							PVOLUME_PHYSICAL_OFFSETS pPhysicalOffsets) = NULL;

    ///<ZZ[zhoyu03: 2009/04/01]: Add for VSS backup.
    virtual BOOL WriteBCDandWriterMetadata(
                            LPCWSTR        wszFileName,        //File name as which BCD(or writer metadata) will be saved, not including path.
                            DWORD          dwFileNameLen,      //File name length by character, not including NULL terminator.
                            LPCWSTR        wszContent,         //BCD(or writer metadata) content, which is a wide-character string.
                            DWORD          dwContentLen,       //Content length by character, not including NULL terminator.
                            BLI_BCDWM_TYPE eBCDWMType) = NULL; //Whether BCD or writer metadata should be saved, referred to BLI_BCDWM_TYPE.

	virtual BOOL EndVolume(PVOLUME_PARAMETER pParameter) = NULL;
	virtual DWORD UpdateVolumeTrackRecord() = NULL;
	virtual VOID EndBLIBackup(DWORD* status) = NULL;

	virtual DWORD GetJobId() = NULL;
	//virtual VOID SetJobId(DWORD dwJobNo) = NULL;
	virtual VOID SetVolumeSnapshotMapping(const std::vector<std::wstring>& volNames, const std::vector<std::wstring>& volShadows) = NULL;

	virtual BOOL IsJobCancled() = 0;

    virtual PARCFLASH_LIC_INFO GetLicenseInfo() = NULL;
	virtual BOOL BaseLicenseAvailable() = NULL;

    virtual void SetVssBackupMgr(void* pVssBackupMgr) = NULL;

    virtual INT SetContext(CBLIDataHandler*	pBLIDataHandler) = NULL;

    virtual CBLIDataHandler* GetContext() = NULL;
};